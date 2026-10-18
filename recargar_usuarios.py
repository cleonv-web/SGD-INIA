"""Recarga el alcance documental desde archivos aprobados, sin recrear cuentas.

Sin --aplicar solo prepara un plan offline. --raiz admite varias oficinas en
una transaccion y con un solo respaldo de la base destino.
"""
from pathlib import Path
import argparse
import json
import secrets
import subprocess
import sys
from datetime import datetime, timezone

ROOT = Path(__file__).resolve().parent
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))
import gestion_data as carga


def bundles(folder=None, parent=None):
    if parent is not None:
        parent = Path(parent).resolve()
        carga.require(parent.is_dir(), 'No existe la raiz de cargas aprobadas.')
        folders = sorted(p.parent for p in parent.glob('*/area.json')
                         if (p.parent / 'usuarios-verificados.csv').is_file())
        carga.require(folders, 'No hay oficinas con area.json y usuarios-verificados.csv en la raiz.')
    else:
        folders = [Path(folder or ROOT / 'datos/carga-util-preparada').resolve()]
    result = []
    areas = set()
    for path in folders:
        area, users, fingerprint = carga.read_bundle(path)
        carga.require(area['codigo'] not in areas,
                      'Oficina repetida en el lote: ' + area['codigo'])
        areas.add(area['codigo'])
        result.append((path, area, users, fingerprint))
    return result


def reload_sql(items, execution, database):
    rows = []
    for _, area, users, fingerprint in items:
        for user in users:
            profile = area['perfiles'][user['perfil']]
            values = [area['codigo'], user['codigo_empleado'], user['usuario'], user['dni'],
                      user['perfil'], carga.document_access(profile), fingerprint, area['verificado_por']]
            rows.append('(' + ','.join(carga.q(v) for v in values) + ')')
    carga.require(rows, 'La recarga no tiene usuarios incluidos.')
    return '''BEGIN;
SET LOCAL standard_conforming_strings=on;
SET LOCAL lock_timeout='15s';
DO $preflight$
BEGIN
 IF current_database() <> %s THEN RAISE EXCEPTION 'Base destino distinta de la prevista'; END IF;
 IF to_regclass('idosgd.piloto_areas') IS NULL OR
    to_regclass('idosgd.piloto_objetos') IS NULL OR
    to_regclass('idosgd.piloto_usos') IS NULL THEN
   RAISE EXCEPTION 'La base no tiene cargas rastreadas; no se adopta una instalacion ajena';
 END IF;
END $preflight$;
LOCK TABLE idosgd.tdtx_config_emp,idosgd.seg_usuarios1,idosgd.rhtm_per_empleados,
 idosgd.piloto_areas,idosgd.piloto_objetos,idosgd.piloto_usos IN SHARE ROW EXCLUSIVE MODE;
CREATE TEMP TABLE recarga_destino (
 co_dep text,co_emp text,usuario text,dni text,perfil text,ti_acceso text,huella text,operador text,
 PRIMARY KEY(co_dep,co_emp)) ON COMMIT DROP;
INSERT INTO recarga_destino VALUES
%s;
CREATE TABLE IF NOT EXISTS idosgd.piloto_recargas_acceso (
 ejecucion text NOT NULL,co_dep text NOT NULL,co_emp text NOT NULL,
 usuario text NOT NULL,perfil text NOT NULL,acceso_anterior text NOT NULL,acceso_nuevo text NOT NULL,
 config_anterior jsonb NOT NULL,objeto_anterior jsonb NOT NULL,
 huella_revisada text NOT NULL,operador text NOT NULL,fecha timestamptz NOT NULL DEFAULT now(),
 PRIMARY KEY(ejecucion,co_dep,co_emp));
CREATE TEMP TABLE recarga_resultado (datos jsonb);
DO $reload$
DECLARE item record; actual jsonb; anterior jsonb; filas jsonb;
        comprobados integer:=0; cambiados integer:=0; rastreos integer:=0;
BEGIN
 FOR item IN SELECT * FROM recarga_destino ORDER BY co_dep,co_emp LOOP
   IF NOT EXISTS(SELECT 1 FROM idosgd.piloto_areas WHERE codigo=item.co_dep) THEN
     RAISE EXCEPTION 'Oficina no cargada por el utilitario: %%',item.co_dep;
   END IF;
   IF NOT EXISTS(SELECT 1 FROM idosgd.seg_usuarios1 u JOIN idosgd.rhtm_per_empleados e
     ON e.cemp_codemp=u.cemp_codemp WHERE u.cod_user=item.usuario
     AND e.cemp_codemp=item.co_emp AND e.cemp_nu_dni=item.dni) THEN
     RAISE EXCEPTION 'La identidad de usuario no coincide con el CSV revisado';
   END IF;
   SELECT jsonb_agg(to_jsonb(c)) INTO filas FROM idosgd.tdtx_config_emp c
   WHERE c.co_dep=item.co_dep AND c.co_emp=item.co_emp;
   IF coalesce(jsonb_array_length(filas),0)<>1 THEN
     RAISE EXCEPTION 'Configuracion ausente o ambigua; no se recrea el usuario';
   END IF;
   actual:=filas->0;
   SELECT o.datos INTO anterior FROM idosgd.piloto_objetos o
   JOIN idosgd.piloto_usos p ON p.tabla=o.tabla AND p.clave=o.clave
   WHERE p.area=item.co_dep AND o.tabla='tdtx_config_emp'
     AND o.clave=jsonb_build_object('co_dep',item.co_dep,'co_emp',item.co_emp)
     AND o.creado_por_utilitario;
   IF anterior IS NULL OR NOT (actual @> (anterior-'ti_acceso')) THEN
     RAISE EXCEPTION 'Configuracion sin procedencia o modificada; revisar antes de recargar';
   END IF;
   IF actual->>'ti_acceso' NOT IN ('0','1','2') OR actual->>'ti_acceso' IS NULL
      OR anterior->>'ti_acceso' NOT IN ('0','1','2') OR anterior->>'ti_acceso' IS NULL THEN
     RAISE EXCEPTION 'Alcance documental previo desconocido';
   END IF;
   IF actual->>'ti_acceso' IS DISTINCT FROM anterior->>'ti_acceso'
      AND actual->>'ti_acceso' IS DISTINCT FROM item.ti_acceso THEN
     RAISE EXCEPTION 'Permiso cambiado manualmente; conservar y revisar su excepcion';
   END IF;
   comprobados:=comprobados+1;
   IF actual->>'ti_acceso' IS DISTINCT FROM item.ti_acceso
      OR anterior->>'ti_acceso' IS DISTINCT FROM item.ti_acceso THEN
     INSERT INTO idosgd.piloto_recargas_acceso
       (ejecucion,co_dep,co_emp,usuario,perfil,acceso_anterior,acceso_nuevo,
        config_anterior,objeto_anterior,huella_revisada,operador)
     VALUES(%s,item.co_dep,item.co_emp,item.usuario,item.perfil,actual->>'ti_acceso',
       item.ti_acceso,actual,anterior,item.huella,item.operador);
     IF actual->>'ti_acceso' IS DISTINCT FROM item.ti_acceso THEN
       UPDATE idosgd.tdtx_config_emp SET ti_acceso=item.ti_acceso
       WHERE co_dep=item.co_dep AND co_emp=item.co_emp;
       cambiados:=cambiados+1;
     END IF;
     UPDATE idosgd.piloto_objetos SET datos=jsonb_set(datos,'{ti_acceso}',to_jsonb(item.ti_acceso))
     WHERE tabla='tdtx_config_emp' AND clave=jsonb_build_object('co_dep',item.co_dep,'co_emp',item.co_emp);
     rastreos:=rastreos+1;
   END IF;
 END LOOP;
 IF EXISTS(SELECT 1 FROM recarga_destino d LEFT JOIN idosgd.tdtx_config_emp c
   ON c.co_dep=d.co_dep AND c.co_emp=d.co_emp WHERE c.ti_acceso IS DISTINCT FROM d.ti_acceso) THEN
   RAISE EXCEPTION 'La validacion final no coincide; se cancela toda la recarga';
 END IF;
 INSERT INTO recarga_resultado VALUES(jsonb_build_object('usuarios_comprobados',comprobados,
   'permisos_actualizados',cambiados,'rastreos_actualizados',rastreos,
   'oficinas',(SELECT count(DISTINCT co_dep) FROM recarga_destino)));
END $reload$;
COMMIT;
SELECT '__PILOTO_RESULTADO__'||datos::text FROM recarga_resultado;
''' % (carga.q(database), ',\n'.join(rows), carga.q(execution))


def usuarios_para_claves(area):
    """Sólo cuentas activas y rastreadas de un área ya cargada."""
    home, args, env = carga.pg_settings()
    query = '''BEGIN READ ONLY;
SELECT coalesce(json_agg(t),'[]'::json) FROM (
 SELECT DISTINCT u.cod_user AS usuario,u.cemp_codemp AS codigo_empleado
 FROM idosgd.seg_usuarios1 u
 JOIN idosgd.tdtx_dependencia_empleado d ON d.co_emp=u.cemp_codemp
 JOIN idosgd.seg_user_aplica a ON a.cod_user=u.cod_user AND a.cod_aplica='9'
 WHERE d.co_dep=%s AND d.es_emp='1' AND u.es_usuario='A' AND a.es_activo='1'
 AND EXISTS(SELECT 1 FROM idosgd.piloto_usos p WHERE p.area=d.co_dep
   AND p.tabla='seg_usuarios1' AND p.clave=jsonb_build_object('cod_user',u.cod_user))
 ORDER BY u.cod_user
) t;
COMMIT;''' % carga.q(area)
    p = subprocess.run([str(home/'bin'/('psql.exe' if sys.platform=='win32' else 'psql')),
                        *args,'-X','-q','-At','-v','ON_ERROR_STOP=1','-c',query],
                       env=env, capture_output=True, text=True, encoding='utf-8')
    carga.require(p.returncode == 0, 'No se pudo consultar el área cargada; no se restablecieron claves.')
    users = json.loads(p.stdout)
    carga.require(users, 'El área no tiene cuentas activas y rastreadas para restablecer.')
    for user in users:
        carga.text(user['usuario'], 'usuario', 20)
        carga.code(user['codigo_empleado'], 'empleado', 5)
    return users


def claves_sql(area, users, encrypted, database):
    rows = ',\n'.join('(%s,%s,%s)' % (carga.q(u['usuario']), carga.q(u['codigo_empleado']),
                                    carga.q(encrypted[u['usuario']])) for u in users)
    return '''BEGIN;
SET LOCAL lock_timeout='15s';
LOCK TABLE idosgd.seg_usuarios1,idosgd.tdtx_dependencia_empleado,
 idosgd.seg_user_aplica,idosgd.piloto_usos IN SHARE ROW EXCLUSIVE MODE;
CREATE TEMP TABLE claves_destino(usuario text PRIMARY KEY,empleado text,cifrado text) ON COMMIT DROP;
INSERT INTO claves_destino VALUES %s;
DO $reset$
DECLARE item record; total integer;
BEGIN
 IF current_database()<>%s THEN RAISE EXCEPTION 'Base destino distinta de la prevista'; END IF;
 FOR item IN SELECT * FROM claves_destino LOOP
   IF NOT EXISTS(SELECT 1 FROM idosgd.seg_usuarios1 u
     JOIN idosgd.tdtx_dependencia_empleado d ON d.co_emp=u.cemp_codemp
     JOIN idosgd.seg_user_aplica a ON a.cod_user=u.cod_user AND a.cod_aplica='9'
     WHERE u.cod_user=item.usuario AND u.cemp_codemp=item.empleado AND u.es_usuario='A'
     AND d.co_dep=%s AND d.es_emp='1' AND a.es_activo='1'
     AND EXISTS(SELECT 1 FROM idosgd.piloto_usos p WHERE p.area=d.co_dep
       AND p.tabla='seg_usuarios1' AND p.clave=jsonb_build_object('cod_user',u.cod_user))) THEN
     RAISE EXCEPTION 'Cuenta sin identidad, acceso activo o pertenencia rastreada al area';
   END IF;
 END LOOP;
 UPDATE idosgd.seg_usuarios1 u SET cclave=c.cifrado,nu_intento=0,in_acceso='1',
   dfe_mod_clave=current_timestamp,cuser_mod='admin',dfec_mod=current_timestamp
 FROM claves_destino c WHERE u.cod_user=c.usuario AND u.cemp_codemp=c.empleado;
 GET DIAGNOSTICS total=ROW_COUNT;
 IF total<>(SELECT count(*) FROM claves_destino) THEN RAISE EXCEPTION 'Cantidad de cuentas distinta'; END IF;
END $reset$;
SELECT '__PILOTO_RESULTADO__'||jsonb_build_object('claves_restablecidas',count(*))::text FROM claves_destino;
COMMIT;
''' % (rows, carga.q(database), carga.q(area))


def restablecer_claves(area, aplicar=False, confirmar=''):
    area = carga.code(area or '', 'area', 5)
    carga.require(area not in carga.RESERVED, 'No restablecer cuentas de laboratorio con esta opción.')
    if not aplicar:
        print('PLAN OFFLINE: restablecer claves e intentos de cuentas activas del área ' + area)
        print('Para aplicar: --restablecer-claves --area ' + area + ' --aplicar --confirmar CLAVES:' + area)
        return 0
    carga.require(confirmar == 'CLAVES:' + area, 'Confirmación incorrecta; se requiere CLAVES:' + area)
    config = json.loads((ROOT/'configuracion.json').read_text(encoding='utf-8'))
    users = usuarios_para_claves(area)
    execution = datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%S')+'-'+secrets.token_hex(3)
    work = carga.DATA/('restablecer-claves-'+area+'-'+execution)
    carga.private_folder(work)
    summary = {'estado': 'PENDIENTE', 'area': area, 'usuarios': len(users), 'base': config['base'],
               'alcance': 'Sólo claves e intentos; conservar cuentas, permisos y documentos'}
    carga.private_write(work/'resultado.json', json.dumps(summary, ensure_ascii=False, indent=2))
    print('Creando respaldo antes de restablecer las ' + str(len(users)) + ' cuentas...')
    target = carga.backup(work)
    plain, encrypted = carga.make_passwords(users)
    carga.private_write(work/'CLAVES-PROVISIONALES-PRIVADAS.json', json.dumps(plain, ensure_ascii=False, indent=2))
    result = carga.execute_sql(claves_sql(area, users, encrypted, config['base']), work)
    carga.require(result.get('claves_restablecidas') == len(users),
                  'Falta el resultado confirmado; conservar las claves provisionales y comprobar el estado.')
    summary.update(estado='TRANSACCION CONFIRMADA', resultado=result, respaldo=str(target))
    carga.private_write(work/'resultado.json', json.dumps(summary, ensure_ascii=False, indent=2))
    (work/'CLAVES-PROVISIONALES-PRIVADAS.json').rename(work/'CLAVES-GENERADAS-PRIVADAS.json')
    url = config['url_publica'].rstrip('/')+'/sisdoc/login.do'
    text = 'ACCESOS NUEVOS - SGD LOCAL - AREA '+area+'\nURL: '+url+'\n\n'
    text += ''.join('Usuario: '+u+'\nClave: '+p+'\n\n' for u,p in plain.items())
    text += 'Usar estas claves en el SGD local indicado arriba. Las claves anteriores fueron sustituidas.\n'
    carga.private_write(work/'ACCESOS-NUEVOS.txt', text)
    print('Claves restablecidas: ' + str(len(users)))
    print('Accesos privados: ' + str(work/'ACCESOS-NUEVOS.txt'))
    print('Respaldo: ' + str(target))
    return 0


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    source = parser.add_mutually_exclusive_group()
    source.add_argument('--carpeta', type=Path)
    source.add_argument('--raiz', type=Path, help='Subcarpetas inmediatas con oficinas revisadas')
    parser.add_argument('--aplicar', action='store_true')
    parser.add_argument('--confirmar', default='')
    parser.add_argument('--restablecer-claves', action='store_true', help='Cambiar claves de cuentas activas de un área cargada')
    parser.add_argument('--area', help='Código de cinco dígitos para restablecer claves')
    args = parser.parse_args(argv)
    if args.restablecer_claves:
        carga.require(not args.carpeta and not args.raiz, 'Restablecer claves usa --area, sin --carpeta ni --raiz.')
        return restablecer_claves(args.area, args.aplicar, args.confirmar)
    carga.require(not args.area, '--area sólo corresponde a --restablecer-claves.')
    items = bundles(args.carpeta, args.raiz)
    config = json.loads((ROOT / 'configuracion.json').read_text(encoding='utf-8'))
    carga.require(config.get('modo') != 'solo_payara', 'Ejecutar desde el paquete de la base de datos.')
    database = carga.text(config.get('base'), 'base destino', 63)
    execution = datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%S') + '-' + secrets.token_hex(3)
    sql = reload_sql(items, execution, database)
    summary = {'estado': 'PLAN OFFLINE', 'base': database, 'oficinas': len(items),
               'destino': {'host': config.get('host_bd'), 'puerto': config.get('puerto_bd'), 'base': database},
               'usuarios': sum(len(u) for _, _, u, _ in items),
               'alcance': 'Recarga solo TI_ACCESO de usuarios ya cargados y su rastreo; conserva cuentas y documentos',
               'perfiles': {a['codigo']: {name: p.get('acceso_documentos', 'personal')
                                        for name, p in a['perfiles'].items()}
                            for _, a, _, _ in items}}
    if args.aplicar:
        carga.require(args.confirmar == 'RECARGAR:USUARIOS',
                      'Confirmacion incorrecta; se requiere RECARGAR:USUARIOS.')
    work = carga.DATA / ('recarga-usuarios-' + execution)
    carga.private_folder(work)
    carga.private_write(work / 'revision.json', json.dumps(summary, ensure_ascii=False, indent=2) + '\n')
    carga.private_write(work / 'plan.sql', '-- PLAN DE RECARGA; aplicar mediante el utilitario.\n' + sql)
    print('Oficinas:', summary['oficinas'], 'Usuarios:', summary['usuarios'])
    print('Destino:', json.dumps(summary['destino'], ensure_ascii=False))
    if not args.aplicar:
        print('Sin conexion ni cambios en la base. Revision:', work / 'revision.json')
        print('Para aplicar usar --aplicar --confirmar RECARGAR:USUARIOS.')
        return 0
    print('Creando respaldo completo antes de recargar permisos...')
    target = carga.backup(work)
    result = carga.execute_sql(sql, work)
    carga.require(result.get('usuarios_comprobados') == summary['usuarios']
                  and result.get('oficinas') == summary['oficinas'],
                  'Comprobar el estado: falta el resultado completo de la recarga.')
    summary.update(estado='TRANSACCION CONFIRMADA', resultado=result, respaldo=str(target))
    carga.private_write(work / 'resultado.json', json.dumps(summary, ensure_ascii=False, indent=2) + '\n')
    print('Recarga confirmada:', json.dumps(result, ensure_ascii=False))
    print('Respaldo:', target)
    print('Cerrar sesion y volver a ingresar para cargar los permisos nuevos.')
    return 0


if __name__ == '__main__':
    try:
        sys.exit(main())
    except (ValueError, RuntimeError, OSError) as exc:
        print('ERROR:', exc, file=sys.stderr)
        sys.exit(1)
