"""Limpieza documental local con respaldo; no reinicia la base ni las cuentas.

Sin argumentos: plan offline. --revisar: conteos de solo lectura.
La aplicación requiere --aplicar --confirmar TRAMITES:idosgd_inia.
"""
from pathlib import Path
import argparse
from datetime import datetime, timezone
import json
import os
import re
import secrets
import subprocess
import sys

ROOT = Path(__file__).resolve().parent
sys.path.insert(0, str(ROOT))
TOKEN = 'TRAMITES:idosgd_inia'
CONTROL = 'limpieza_tramites_control'
# Lista explícita: nunca incorporar tablas por un prefijo ni usar CASCADE.
TABLAS = (
    'tdtc_expediente', 'tdtc_guia_mp', 'tdtd_guia_mp', 'tdtd_guia_mp_his',
    'tdtv_remitos', 'tdtv_remitos_his', 'tdtv_destinos', 'tdtv_destinos_his',
    'tdtv_destino_detalle', 'tdtv_bandeja_rec', 'tdtv_estados_mov',
    'tdtr_arbol_seg', 'tdtr_avance', 'tdtr_referencia', 'tdtr_referencia_his',
    'tdtv_anexos', 'tdtv_anexos_his', 'tdtv_archivo_doc', 'tdtv_personal_vb',
    'tdtv_visualiza_doc', 'tdtv_notificaciones', 'tdtx_remitos_resumen',
    'tdtx_requisito_expediente', 'tdtx_resumen_dep', 'tdtx_resumen_doc',
    'td_det_mensajeria', 'td_mensajeria', 'tdtr_det_fiscalizacion',
    'tdtr_fiscalizacion', 'tdtc_notificacion_job', 'tdtd_notificacion_job_det',
    'tdtc_notificacion_log', 'iotdtc_despacho', 'iotdtc_recepcion',
    'iotdtd_anexo', 'iotdtd_doc_principal', 'iotdtm_doc_externo',
    'dr$ctx_remitos_t1$i', 'dr$ctx_remitos_t1$k',
    'dr$ctx_remitos_t1$n', 'dr$ctx_remitos_t1$r',
    # Solicitantes guardados por MPV: no son cuentas ni empleados del SGD.
    # Deben quedar vacíos para que una prueba nueva no reutilice nombres anteriores.
    'idtanirs', 'tdtx_ani_simil', 'lg_pro_proveedor',
    'dr$ctx_proveedor_t1$i', 'dr$ctx_proveedor_t1$k',
    'dr$ctx_proveedor_t1$n', 'dr$ctx_proveedor_t1$p', 'dr$ctx_proveedor_t1$r',
)
REQUERIDAS = {'seg_usuarios1', 'rhtm_per_empleados', 'rhtm_dependencia',
              'tdtr_permisos', 'tdtr_plantilla_docx', 'instalacion_portable'}


def literal(value):
    return "'" + str(value).replace("'", "''") + "'"


def identificador(value):
    return '"' + value.replace('"', '""') + '"'


def nombre(tabla):
    return 'idosgd.' + identificador(tabla)


def consulta(s, sql):
    return s.pg_tool('psql', [*s.connection(), '-X', '-qAt', '-v',
                            'ON_ERROR_STOP=1', '-c', sql]).stdout.strip()


def inventario(s):
    return json.loads(consulta(s, """
SELECT coalesce(json_agg(t), '[]') FROM (
 SELECT n.nspname AS esquema, c.relname AS tabla, c.relkind AS tipo,
        array_agg(a.attname ORDER BY a.attnum) AS columnas
 FROM pg_class c JOIN pg_namespace n ON n.oid=c.relnamespace
 JOIN pg_attribute a ON a.attrelid=c.oid AND a.attnum>0 AND NOT a.attisdropped
 WHERE n.nspname NOT LIKE 'pg_%' AND n.nspname <> 'information_schema'
   AND c.relkind IN ('r','p','f','m')
 GROUP BY n.nspname, c.relname, c.relkind ORDER BY n.nspname,c.relname
) t;"""))


def validar_inventario(info):
    tablas = {t['tabla']: t for t in info if t['esquema'] == 'idosgd'}
    faltan = (set(TABLAS) | REQUERIDAS) - tablas.keys()
    if faltan:
        raise RuntimeError('Estructura incompatible; faltan tablas: ' + ', '.join(sorted(faltan)))
    for t in info:
        if t['tipo'] != 'r':
            raise RuntimeError('Revisar tabla particionada, externa o materializada: '
                               + t['esquema'] + '.' + t['tabla'])
        if (t['tabla'] not in TABLAS and t['tabla'] != CONTROL
                and {'nu_emi', 'nu_sec_exp'}.intersection(t['columnas'])):
            raise RuntimeError('Tabla documental adicional sin revisar: '
                               + t['esquema'] + '.' + t['tabla'])
    return tablas


def sql_limpieza(operacion):
    """Una transacción: vaciar lista, verificar resto y registrar COMMIT recuperable."""
    targets = ','.join(literal(t) for t in TABLAS)
    required = ','.join(literal(t) for t in sorted(REQUERIDAS))
    truncate = ',\n '.join(nombre(t) for t in TABLAS)
    # Huella por fila y ordenación estable: no exportar contraseñas ni blobs.
    huella = """SELECT count(*), encode(sha256(convert_to(coalesce(string_agg(h,'' ORDER BY h),''),'UTF8')),'hex')
 FROM (SELECT encode(sha256(convert_to(row_to_json(t)::text,'UTF8')),'hex') h FROM %I.%I t) filas"""
    return f"""BEGIN;
SET LOCAL lock_timeout = '10s';
SET LOCAL statement_timeout = '10min';
SELECT pg_advisory_xact_lock(73471013);
CREATE TEMP TABLE limpiar_objetivos(tabla text PRIMARY KEY);
INSERT INTO limpiar_objetivos SELECT unnest(ARRAY[{targets}]::text[]);
CREATE TEMP TABLE limpiar_protegidas(esquema text, tabla text, cantidad bigint, huella text);
DO $validar$
DECLARE r record; n bigint; h text;
BEGIN
 IF EXISTS(SELECT 1 FROM pg_stat_activity WHERE datname=current_database()
           AND pid<>pg_backend_pid() AND backend_type='client backend') THEN
   RAISE EXCEPTION 'Cerrar otras conexiones a la base antes de limpiar';
 END IF;
 IF EXISTS(SELECT 1 FROM limpiar_objetivos o WHERE to_regclass(format('idosgd.%I',o.tabla)) IS NULL)
    OR EXISTS(SELECT 1 FROM unnest(ARRAY[{required}]::text[]) t
              WHERE to_regclass(format('idosgd.%I',t)) IS NULL) THEN
   RAISE EXCEPTION 'Estructura incompatible';
 END IF;
 IF EXISTS(SELECT 1 FROM pg_trigger g JOIN pg_class c ON c.oid=g.tgrelid
           JOIN pg_namespace ns ON ns.oid=c.relnamespace
           WHERE ns.nspname='idosgd' AND c.relname IN (SELECT tabla FROM limpiar_objetivos)
             AND NOT g.tgisinternal AND (g.tgtype::int & 32)<>0) THEN
   RAISE EXCEPTION 'Hay triggers TRUNCATE sin revisar';
 END IF;
 FOR r IN SELECT ns.nspname esquema,c.relname tabla,c.relkind tipo
          FROM pg_class c JOIN pg_namespace ns ON ns.oid=c.relnamespace
          WHERE ns.nspname NOT LIKE 'pg_%' AND ns.nspname<>'information_schema'
            AND c.relkind IN ('r','p','f','m') ORDER BY ns.nspname,c.relname LOOP
   IF r.tipo<>'r' THEN RAISE EXCEPTION 'Tabla no soportada: %.%',r.esquema,r.tabla; END IF;
   EXECUTE format('LOCK TABLE %I.%I IN ACCESS EXCLUSIVE MODE',r.esquema,r.tabla);
   IF NOT (r.esquema='idosgd' AND (r.tabla IN (SELECT tabla FROM limpiar_objetivos)
                                  OR r.tabla={literal(CONTROL)})) THEN
     IF EXISTS(SELECT 1 FROM information_schema.columns WHERE table_schema=r.esquema
               AND table_name=r.tabla AND column_name IN ('nu_emi','nu_sec_exp')) THEN
       RAISE EXCEPTION 'Tabla documental adicional: %.%',r.esquema,r.tabla;
     END IF;
     EXECUTE format({literal(huella)},r.esquema,r.tabla) INTO n,h;
     INSERT INTO limpiar_protegidas VALUES(r.esquema,r.tabla,n,h);
   END IF;
 END LOOP;
END $validar$;
CREATE TABLE IF NOT EXISTS idosgd.{CONTROL}(
 operacion text PRIMARY KEY, fecha timestamptz NOT NULL DEFAULT now(), resultado jsonb NOT NULL);
CREATE TEMP TABLE limpiar_conteos(tabla text, cantidad bigint);
DO $antes$
DECLARE r record; n bigint;
BEGIN
 FOR r IN SELECT tabla FROM limpiar_objetivos LOOP
   EXECUTE format('SELECT count(*) FROM idosgd.%I',r.tabla) INTO n;
   INSERT INTO limpiar_conteos VALUES(r.tabla,n);
 END LOOP;
END $antes$;
TRUNCATE TABLE
 {truncate}
 CONTINUE IDENTITY RESTRICT;
DO $verificar$
DECLARE r record; n bigint; h text;
BEGIN
 FOR r IN SELECT * FROM limpiar_protegidas LOOP
   EXECUTE format({literal(huella)},r.esquema,r.tabla) INTO n,h;
   IF n<>r.cantidad OR h<>r.huella THEN
     RAISE EXCEPTION 'Cambio en tabla protegida: %.%',r.esquema,r.tabla;
   END IF;
 END LOOP;
 FOR r IN SELECT tabla FROM limpiar_objetivos LOOP
   EXECUTE format('SELECT count(*) FROM idosgd.%I',r.tabla) INTO n;
   IF n<>0 THEN RAISE EXCEPTION 'Quedan tramites en %',r.tabla; END IF;
 END LOOP;
END $verificar$;
INSERT INTO idosgd.{CONTROL}(operacion,resultado)
 SELECT {literal(operacion)},jsonb_build_object('tablas_vaciadas',(SELECT jsonb_agg(c) FROM limpiar_conteos c),
   'tablas_conservadas',(SELECT jsonb_agg(p) FROM limpiar_protegidas p),'secuencias','conservadas');
COMMIT;
"""


def resultado_confirmado(s, operacion):
    if consulta(s, f"SELECT to_regclass('idosgd.{CONTROL}') IS NOT NULL;") != 't':
        return None
    value = consulta(s, f'SELECT resultado FROM idosgd.{CONTROL} WHERE operacion={literal(operacion)};')
    return json.loads(value) if value else None


def restaurar_archivos(documentos, anteriores, manifiesto):
    from reiniciar_piloto import huella_archivo
    # Verificar todos antes de mover el primero; nunca sobreescribir nuevos archivos.
    for item in manifiesto:
        rel = Path(item['archivo'])
        source, target = anteriores / rel, documentos / rel
        if (not source.resolve().is_relative_to(anteriores.resolve())
                or not target.resolve().is_relative_to(documentos.resolve())
                or target.exists() or huella_archivo(source) != item['sha256']):
            raise RuntimeError('No se pueden restaurar automáticamente los archivos: ' + str(rel))
    for item in manifiesto:
        rel = Path(item['archivo'])
        (documentos / rel).parent.mkdir(parents=True, exist_ok=True)
        (anteriores / rel).rename(documentos / rel)


def validar_destino(s, *, iniciar_bd=False):
    config = s.C
    if (config.get('modo'), config.get('base'), config.get('host_bd'), config.get('puerto_bd')) != (
            'completo', 'idosgd_inia', '127.0.0.1', 55432):
        raise RuntimeError('Solo se admite el piloto local idosgd_inia, puerto 55432, modo completo.')
    data = s.DATA.resolve()
    docs = data / 'documentos'
    if data != s.ROOT.resolve() / 'datos' or docs.resolve() != docs:
        raise RuntimeError('Rutas documentales fuera de esta instalación.')
    if not (data/'postgresql/PG_VERSION').is_file() or not (data/'credenciales.json').is_file():
        raise RuntimeError('Falta la instalación existente; no se crea ni reinicia una base.')
    if s.pg_tool('pg_ctl', ['-D', data/'postgresql', 'status'], check=False).returncode:
        if not iniciar_bd:
            raise RuntimeError('PostgreSQL está detenido. Ejecutar Iniciar-SGD antes de continuar.')
        print('Iniciando PostgreSQL existente...', flush=True)
        s.pg_tool('pg_ctl', ['-D',data/'postgresql','-l',s.LOG/'postgresql-servidor.log',
                             '-w','-t','30','start'],log='tramites-postgresql-iniciar.log')
    return data, docs


def revisar(s):
    data, docs = validar_destino(s)
    validar_inventario(inventario(s))
    from reiniciar_piloto import archivos_documentales
    conteos = {t: int(consulta(s, f'SELECT count(*) FROM {nombre(t)};')) for t in TABLAS}
    archivos = archivos_documentales(docs) if docs.exists() else []
    print(json.dumps({'tablas_a_vaciar': conteos, 'archivos_a_archivar': len(archivos),
                      'bytes_archivos': sum(p.stat().st_size for p in archivos),
                      'usuarios_conservados': int(consulta(s, 'SELECT count(*) FROM idosgd.seg_usuarios1;'))},
                     ensure_ascii=False, indent=2))


def clientes_windows(*, cerrar=False, exigir_cierre=True):
    """Control local limitado a Word y Tramitedoc; sin efectos en el plan/revisión."""
    if os.name != 'nt':
        return {'cerrados': [], 'activos': []}
    powershell = Path(os.environ['SystemRoot'])/'System32/WindowsPowerShell/v1.0/powershell.exe'
    command = [str(powershell), '-NoProfile', '-NonInteractive', '-ExecutionPolicy', 'Bypass',
               '-File', str(ROOT/'herramientas/cerrar_clientes_tramites.ps1')]
    if cerrar:
        command.append('-Cerrar')
    try:
        result = subprocess.run(command, capture_output=True, text=True, encoding='utf-8',
                                errors='replace', timeout=65 if cerrar else 10,
                                creationflags=subprocess.CREATE_NO_WINDOW)
    except subprocess.TimeoutExpired as exc:
        raise RuntimeError('No se confirmó el cierre de Word/Tramitedoc. No se inicia la limpieza.') from exc
    state = json.loads(result.stdout) if result.stdout.strip() else {'cerrados': [], 'activos': []}
    if result.returncode or (exigir_cierre and state['activos']):
        error = RuntimeError('No se confirmó el cierre de Word/Tramitedoc. '
                             'Resolver los avisos de guardado antes de limpiar. ' + result.stderr.strip())
        error.clientes = state
        raise error
    return state


def reabrir_clientes(state):
    """Reabrir las aplicaciones previamente cerradas, sin volver a abrir trámites antiguos."""
    if not state['cerrados']:
        return
    actuales = clientes_windows(exigir_cierre=False)
    abiertos = {p['nombre'].lower() for p in actuales['activos']}
    for item in state['cerrados']:
        path = Path(item.get('ruta') or '')
        name = item['nombre'].lower()
        if name in abiertos:
            continue
        if name not in {'winword', 'tramitedoc'} or path.name.lower() != name+'.exe' or not path.is_file():
            raise RuntimeError('No se pudo localizar el programa para reabrir: ' + name)
        subprocess.Popen([str(path)], cwd=path.parent)
        abiertos.add(name)


def verificar_sgd(s):
    import urllib.request
    import http.cookiejar
    opener = urllib.request.build_opener(urllib.request.HTTPCookieProcessor(http.cookiejar.CookieJar()))
    with opener.open(s.C['url_publica']+'/sisdoc/login.do', timeout=30) as response:
        if response.status != 200 or b'coUsuario' not in response.read():
            raise RuntimeError('El SGD se inició, pero no devolvió el formulario de acceso esperado.')


def cerrar_conexiones_bd(s):
    """Desconectar sólo clientes de la base configurada; el motor sigue activo."""
    result = json.loads(consulta(s, """SELECT coalesce(json_agg(pg_terminate_backend(pid,5000)),'[]')
 FROM pg_stat_activity WHERE datname=current_database() AND pid<>pg_backend_pid()
 AND backend_type='client backend';"""))
    if any(ok is not True for ok in result):
        raise RuntimeError('No se pudieron cerrar todas las conexiones de la base. No se inicia la limpieza.')
    if int(consulta(s, "SELECT count(*) FROM pg_stat_activity WHERE datname=current_database() "
                       "AND pid<>pg_backend_pid() AND backend_type='client backend';")):
        raise RuntimeError('Una conexión a la base sigue activa o se reconectó. No se inicia la limpieza.')
    return len(result)


def limpiar(*, aplicar=False, confirmar='', reanudar_todo=False, _sgd=None):
    if not aplicar:
        print('PLAN OFFLINE: vaciar trámites de TODAS las áreas y TODOS los años.')
        print('Conservar cuentas SGD, claves, empleados, permisos, oficinas, plantillas, catálogos y auditoría general.')
        print('Vaciar también ciudadanos y proveedores de la MPV para no reutilizar datos de pruebas anteriores.')
        print('Archivar datos/documentos con SHA256 y respaldar la base completa; conservar secuencias.')
        print('Al aplicar: cerrar Payara, Tramitedoc, Word y conexiones a esta base antes de respaldar/borrar.')
        print('Tablas documentales: ' + ', '.join(TABLAS))
        print('Vista previa técnica: python limpiar_tramites.py --revisar')
        print('Para el operador: doble clic en Limpiar-Tramites.exe, sin parámetros.')
        return
    if confirmar != TOKEN:
        raise ValueError('Se requiere --confirmar ' + TOKEN)
    if _sgd is None:
        import sgd as _sgd
    s = _sgd
    data, docs = validar_destino(s, iniciar_bd=reanudar_todo)
    validar_inventario(inventario(s))
    from reiniciar_piloto import archivar_documentos
    import gestion_data as g
    operation = datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%S') + '-' + secrets.token_hex(6)
    work = data / ('limpieza-tramites-' + operation)
    if work.resolve() != work or not work.is_relative_to(data):
        raise RuntimeError('Ruta de respaldo incompatible.')
    # Un fallo/interrupción deja este bloqueo para impedir reintentos a ciegas.
    lock = data / '.limpieza-tramites.lock'
    try:
        with lock.open('x', encoding='utf-8') as f:
            f.write(str(work))
    except FileExistsError:
        raise RuntimeError('Hay una limpieza pendiente. Revisar ' + str(lock)) from None
    safe_to_restart = True
    was_running = False
    phase = 'PREPARANDO'
    manifest = []
    closed = {'cerrados': [], 'activos': []}
    try:
        g.private_folder(work)
        def report(state, **extra):
            g.private_write(work/'resultado.json', json.dumps(
                dict(estado=state, operacion=operation, **extra), ensure_ascii=False, indent=2))
        report(phase)
        was_running = bool(re.search(r'(?mi)^\s*sgd\s+running\b', s.admin(['list-domains']).stdout))
        if was_running:
            print('Pausando Payara; PostgreSQL continúa activo...', flush=True)
            s.admin(['stop-domain', 'sgd'], log='tramites-payara-detener.log')
        if re.search(r'(?mi)^\s*sgd\s+running\b', s.admin(['list-domains']).stdout):
            raise RuntimeError('Payara sigue activo; no se limpió la base.')
        print('Cerrando Tramitedoc y Word. Resolver cualquier aviso de guardado de Word...', flush=True)
        try:
            closed = clientes_windows(cerrar=True)
        except Exception as exc:
            closed = getattr(exc, 'clientes', closed)
            raise
        connections = cerrar_conexiones_bd(s)
        report('CLIENTES_CERRADOS', clientes=closed, conexiones_cerradas=connections)
        # Las rutas configuradas deben estar dentro de la carpeta que se archivará.
        rutas = json.loads(consulta(s, "SELECT coalesce(json_agg(ruta),'[]') FROM ("
                "SELECT DISTINCT unnest(ARRAY[de_dir_emi,de_dir_rec,de_dir_ane]) ruta "
                "FROM idosgd.tdtx_config_emp) r WHERE nullif(trim(ruta),'') IS NOT NULL;"))
        for ruta in rutas:
            p = Path(ruta)
            if not p.is_absolute() or not p.resolve().is_relative_to(docs):
                raise RuntimeError('Ruta documental externa sin revisar: ' + ruta)
        print('Respaldando base completa...', flush=True)
        dump = g.backup(work)
        s.pg_tool('pg_restore', ['--list', dump])
        report('RESPALDADO', respaldo=str(dump))
        sql = sql_limpieza(operation)
        g.private_write(work/'limpieza.sql', sql)
        # Detectar reaperturas antes de tocar archivos y desconectar conexiones tardías.
        clientes_windows()
        cerrar_conexiones_bd(s)
        safe_to_restart = False
        phase = 'ARCHIVANDO'
        report(phase)
        try:
            manifest = archivar_documentos(docs, work/'documentos-anteriores', data)
        except Exception:
            # El archivador revierte movimientos, pero exige revisión si falla la reversión.
            phase = 'ERROR_AL_ARCHIVAR'
            raise
        g.private_write(work/'archivos-documentales.json', json.dumps(manifest, indent=2))
        phase = 'APLICANDO'
        report(phase, archivos=len(manifest))
        sql_error = None
        try:
            clientes_windows()
            cerrar_conexiones_bd(s)
            s.psql(sql, log='tramites-limpieza.log')
        except Exception as exc:
            sql_error = exc
        # El registro se confirma con los TRUNCATE en la misma transacción.
        # Resolver también una desconexión después del COMMIT sin restaurar datos viejos.
        result = resultado_confirmado(s, operation)
        if result is None:
            restaurar_archivos(docs, work/'documentos-anteriores', manifest)
            safe_to_restart = True
            phase = 'REVERTIDO'
            report(phase)
            raise RuntimeError('La limpieza no se confirmó; archivos restaurados. '
                               'Revisar logs/tramites-limpieza.log.') from sql_error
        phase = 'COMPLETADO'
        report(phase, archivos=len(manifest), comprobacion=result)
        safe_to_restart = True
        print('Trámites y solicitantes MPV eliminados; cuentas SGD y configuración conservadas.', flush=True)
        print('Respaldo y archivos anteriores: ' + str(work), flush=True)
    except Exception as exc:
        if work.exists():
            g.private_write(work/'error.txt', s.redact(str(exc)))
        if not safe_to_restart:
            print('Revisión necesaria: Payara queda detenido. Conservar respaldo y bloqueo: ' + str(work),
                  file=sys.stderr, flush=True)
        raise
    finally:
        if safe_to_restart:
            lock.unlink(missing_ok=True)
            if was_running or (reanudar_todo and phase == 'COMPLETADO'):
                try:
                    s.iniciar(solo=True)
                    if reanudar_todo:
                        verificar_sgd(s)
                except Exception as exc:
                    raise RuntimeError('Estado de limpieza: ' + phase + '. No se pudo reanudar Payara; '
                                       'no repetir la limpieza. Revisar ' + str(work)) from exc
            try:
                reabrir_clientes(closed)
            except Exception as exc:
                raise RuntimeError('Estado de limpieza: ' + phase + '. No se pudo reabrir un cliente; '
                                   'no repetir la limpieza. Revisar ' + str(work)) from exc


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    mode = parser.add_mutually_exclusive_group()
    mode.add_argument('--revisar', action='store_true')
    mode.add_argument('--aplicar', action='store_true')
    mode.add_argument('--simple', action='store_true', help=argparse.SUPPRESS)
    parser.add_argument('--confirmar', default='')
    args = parser.parse_args(argv)
    if args.confirmar and not args.aplicar:
        parser.error('--confirmar requiere --aplicar')
    if args.simple:
        limpiar(aplicar=True, confirmar=TOKEN, reanudar_todo=True)
        import sgd
        import webbrowser
        webbrowser.open(sgd.C['url_publica']+'/sisdoc/login.do')
    elif args.revisar:
        import sgd
        revisar(sgd)
    else:
        limpiar(aplicar=args.aplicar, confirmar=args.confirmar)


if __name__ == '__main__':
    try:
        main()
    except Exception as exc:
        print('ERROR: ' + str(exc), file=sys.stderr)
        sys.exit(1)
