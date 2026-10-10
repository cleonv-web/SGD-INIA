"""Carga y retiro de un área del piloto. La revisión por defecto es offline.

No conecta a SQL Server. Sólo --aplicar conecta al PostgreSQL configurado.
Usa biblioteca estándar, runtimes incluidos, respaldo y transacciones.
"""
from pathlib import Path
import argparse, csv, hashlib, json, os, re, secrets, subprocess, sys
from datetime import datetime, timezone

ROOT = Path(__file__).resolve().parent
# Python embebido de Windows usa un archivo ._pth y no añade la carpeta del script.
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))
DATA = ROOT / 'datos'
TABLES = ['si_mae_local', 'rhtm_cargos', 'rhtm_dependencia', 'rhtm_per_empleados',
          'seg_usuarios1', 'seg_user_aplica', 'seg_user_aplica_opc', 'tdtr_permisos',
          'tdtx_dependencia_empleado', 'tdtx_config_emp', 'sitm_local_dependencia',
          'sitm_doc_dependencia', 'tdtr_dependencia_mp', 'tdtr_permiso_mp', 'tdtr_mot_dependencia']
KEEP = ['si_mae_local', 'rhtm_cargos']
FIELDS = ['origen_id', 'codigo_empleado', 'usuario', 'nombres', 'apellido_paterno',
          'apellido_materno', 'dni', 'email', 'cargo_codigo', 'cargo_nombre', 'perfil', 'verificado', 'incluir_sgd']
RESERVED = {'00000', '49001', '49002', '49003'}


def require(condition, message):
    if not condition:
        raise ValueError(message)


def text(value, label, limit, empty=False):
    require(isinstance(value, str), label + ': debe ser texto')
    value = value.strip()
    require((empty or bool(value)) and len(value) <= limit, label + ': vacío o supera ' + str(limit))
    require(not any(ord(c) < 32 for c in value), label + ': contiene caracteres de control')
    return value


def code(value, label, size):
    require(isinstance(value, str) and re.fullmatch(r'[0-9]{%d}' % size, value),
            label + ': debe tener exactamente ' + str(size) + ' dígitos')
    return value


def q(value):
    return "'" + str(value).replace("'", "''") + "'"


def j(value):
    return q(json.dumps(value, ensure_ascii=False)) + '::jsonb'


def private_write(path, value):
    path.parent.mkdir(parents=True, exist_ok=True)
    path.write_text(value, encoding='utf-8')
    if os.name != 'nt':
        path.chmod(0o600)


def private_folder(path):
    path.mkdir(parents=True, exist_ok=True)
    if os.name == 'nt':
        account = subprocess.run(['whoami'], capture_output=True, text=True, check=True).stdout.strip()
        # Quitar permisos heredados antes de escribir claves y respaldos.
        subprocess.run(['icacls', str(path), '/inheritance:r', '/grant:r',
                        account + ':(OI)(CI)F', '*S-1-5-18:(OI)(CI)F'],
                       capture_output=True, check=True)
    else:
        path.chmod(0o700)


def catalogs():
    schema = (ROOT / 'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
    result = {}
    for table in ['seg_opciones', 'si_mae_tipo_doc']:
        block = re.search(r'COPY idosgd\.' + table + r' \([^\n]+\) FROM stdin;\n(.*?)\n\\\.',
                          schema, re.S)
        require(block is not None, 'No se encuentra el catálogo local: ' + table)
        result[table] = [line.split('\t') for line in block[1].splitlines()]
    return result


def read_bundle(folder):
    manifest = json.loads((folder / 'area.json').read_text(encoding='utf-8-sig'))
    require(isinstance(manifest, dict), 'area.json debe contener un objeto')
    code(manifest.get('codigo'), 'codigo de área', 5)
    require(manifest['codigo'] not in RESERVED, 'No usar códigos de laboratorio para el área')
    for field, limit in [('nombre', 200), ('sigla', 50), ('nombre_corto', 20)]:
        manifest[field] = text(manifest.get(field), field, limit)
    parent = manifest.get('padre')
    if parent is not None:
        code(parent, 'padre', 5)
        require(parent != manifest['codigo'], 'El área no puede ser su propio padre')
    require(isinstance(manifest.get('nivel'), int) and not isinstance(manifest['nivel'], bool)
            and 1 <= manifest['nivel'] <= 9, 'nivel debe estar entre 1 y 9')
    require(type(manifest.get('mesa_partes')) is bool, 'mesa_partes debe ser true o false')
    require(manifest.get('aprobado') == 'SI', 'Revisar area.json y marcar aprobado=SI')
    require('REEMPLAZAR' not in json.dumps(manifest,ensure_ascii=False).upper(),
            'Sustituir los textos REEMPLAZAR de la plantilla antes de aprobar')
    require(bool(text(manifest.get('verificado_por'), 'verificado_por', 100)), 'Falta responsable de revisión')
    sede = manifest.get('sede', {})
    require(isinstance(sede, dict), 'sede debe ser un objeto')
    code(sede.get('codigo'), 'sede.codigo', 3)
    for field in ['nombre', 'direccion']:
        sede[field] = text(sede.get(field), 'sede.' + field, 100)
    profiles = manifest.get('perfiles')
    require(isinstance(profiles, dict) and profiles, 'Definir perfiles explícitos en area.json')
    known_options = {r[1] for r in catalogs()['seg_opciones'] if r[0] == '9'}
    known_docs = {r[0] for r in catalogs()['si_mae_tipo_doc']}
    # Los tipos añadidos por la instalación local también son entradas del catálogo.
    seed = (ROOT / 'bd/02-inia-usuarios-ficticios.sql').read_text(encoding='utf-8')
    known_docs.update(re.findall(r"SI_MAE_TIPO_DOC\([^\n]+?VALUES\('([^']+)'", seed))
    for name, profile in profiles.items():
        text(name, 'nombre de perfil', 40)
        require(isinstance(profile, dict), 'Perfil inválido: ' + name)
        require(type(profile.get('administrador')) is bool, 'administrador debe ser booleano en ' + name)
        require(type(profile.get('firma')) is bool, 'firma debe ser booleano en ' + name)
        options = profile.get('opciones')
        require(isinstance(options, list) and options and all(isinstance(x, str) for x in options)
                and len(options) == len(set(options)), 'Definir opciones sin duplicados en ' + name)
        require(set(options) <= known_options, 'Hay opciones desconocidas en ' + name)
        # Incluir los menús padres para que las opciones sean visibles.
        for option in options:
            require(all(parent_option in options for parent_option in known_options
                        if option != parent_option and option.startswith(parent_option)),
                    'Faltan menús padres para ' + option + ' en ' + name)
    docs = manifest.get('tipos_documento')
    require(isinstance(docs, list) and docs and all(isinstance(x, str) for x in docs)
            and len(docs) == len(set(docs)) and set(docs) <= known_docs,
            'Definir tipos_documento válidos y sin duplicados')
    with (folder / 'usuarios-verificados.csv').open(encoding='utf-8-sig', newline='') as f:
        reader = csv.DictReader(f)
        require(reader.fieldnames in [FIELDS,FIELDS[:-1]], 'Encabezados CSV inválidos. Usar la plantilla, separador coma')
        rows = list(reader)
    users = []; row_numbers = []
    for number,user in enumerate(rows,2):
        label = 'Fila ' + str(number)
        require(None not in user and all(v is not None for v in user.values()), label + ': número de columnas incorrecto')
        flag = user.get('incluir_sgd','1').strip()
        require(flag in ['0','1'],label + ': incluir_sgd debe ser 0 o 1')
        if flag == '0':
            continue
        user['incluir_sgd'] = '1'
        users.append(user);row_numbers.append(number)
    require(users, 'El CSV no contiene usuarios seleccionados con incluir_sgd=1')
    require(len(users) <= 500, 'Máximo 500 usuarios por carga de área del piloto')
    identifiers = {k: set() for k in ['origen_id', 'codigo_empleado', 'usuario', 'dni']}
    cargos = {}
    for number, user in zip(row_numbers,users):
        label = 'Fila ' + str(number)
        require(None not in user and all(v is not None for v in user.values()), label + ': número de columnas incorrecto')
        require(user['verificado'].strip().upper() == 'SI', label + ': usuario no verificado; revisar o marcar incluir_sgd=0')
        require(re.fullmatch(r'[1-9][0-9]*', user['origen_id']), label + ': origen_id inválido')
        code(user['codigo_empleado'], label + ': codigo_empleado', 5)
        require(user['codigo_empleado'] not in RESERVED, label + ': código reservado de laboratorio')
        user['usuario'] = text(user['usuario'], label + ': usuario', 20)
        require(re.fullmatch(r'[A-Za-z0-9_.@-]+', user['usuario']), label + ': caracteres de usuario no admitidos')
        require(user['usuario'].casefold() not in ['admin', 'mesa_demo', 'area_demo', 'jefe_demo'],
                label + ': no reutilizar cuentas de laboratorio')
        for field, limit in [('nombres', 80), ('apellido_paterno', 40), ('apellido_materno', 40),
                             ('email', 50), ('cargo_nombre', 200), ('perfil', 40)]:
            user[field] = text(user[field], label + ': ' + field, limit,
                               empty=field in ['apellido_materno', 'email'])
        require(user['nombres']!='NOMBRE_VERIFICADO' and user['cargo_nombre']!='CARGO_VERIFICADO',
                label + ': sustituir nombres y cargos de ejemplo')
        code(user['dni'], label + ': dni', 8)
        require(user['dni'] != '00000000', label + ': DNI marcador no admitido')
        code(user['cargo_codigo'], label + ': cargo_codigo', 4)
        require(user['cargo_codigo'] != '0000', label + ': definir cargo real, no el cargo temporal')
        require(user['perfil'] in profiles, label + ': perfil no definido')
        for field in identifiers:
            value = user[field].casefold() if field == 'usuario' else user[field]
            require(value not in identifiers[field], label + ': duplicado de ' + field)
            identifiers[field].add(value)
        cargo = user['cargo_codigo']
        require(cargo not in cargos or cargos[cargo] == user['cargo_nombre'], label + ': cargo con nombres distintos')
        cargos[cargo] = user['cargo_nombre']
    require(manifest.get('titular_empleado') in identifiers['codigo_empleado'],
            'titular_empleado debe corresponder a un usuario verificado de esta carga')
    # Tramitedoc entrega esta ruta al Explorador: conservar el formato nativo.
    document_path = str(DATA / 'documentos')
    require(len(document_path) <= 200, 'Ruta datos/documentos supera 200 caracteres')
    # El selector no es un dato del empleado; mantener la huella de CSV anteriores.
    canonical = json.dumps({'area': manifest, 'usuarios':
                           [{k:v for k,v in u.items() if k!='incluir_sgd'} for u in users]},
                           ensure_ascii=False, sort_keys=True)
    return manifest, users, hashlib.sha256(canonical.encode()).hexdigest()


LEDGER = '''
CREATE TABLE IF NOT EXISTS idosgd.piloto_areas (
 codigo varchar(5) PRIMARY KEY, huella text NOT NULL, nombre text NOT NULL,
 verificado_por text NOT NULL, creada timestamptz NOT NULL DEFAULT now());
CREATE TABLE IF NOT EXISTS idosgd.piloto_objetos (
 tabla text NOT NULL, clave jsonb NOT NULL, creado_por_utilitario boolean NOT NULL,
 datos jsonb NOT NULL, PRIMARY KEY(tabla,clave));
CREATE TABLE IF NOT EXISTS idosgd.piloto_usos (
 area varchar(5) REFERENCES idosgd.piloto_areas(codigo), tabla text NOT NULL, clave jsonb NOT NULL,
 PRIMARY KEY(area,tabla,clave), FOREIGN KEY(tabla,clave) REFERENCES idosgd.piloto_objetos(tabla,clave));
'''


def helper_sql():
    return '''
CREATE FUNCTION pg_temp.registrar(area text, tabla text, clave jsonb, datos jsonb)
RETURNS void LANGUAGE plpgsql AS $fn$
DECLARE actual jsonb; filas jsonb; comparable jsonb; nombres text; valores text; k text; nuevo boolean;
        pk text[]; campos text[];
BEGIN
 IF NOT (tabla=ANY(ARRAY[%s]::text[])) THEN RAISE EXCEPTION 'Tabla no autorizada'; END IF;
 SELECT array_agg(a.attname::text ORDER BY a.attname) INTO pk
 FROM pg_index i JOIN pg_attribute a ON a.attrelid=i.indrelid AND a.attnum=ANY(i.indkey)
 WHERE i.indisprimary AND i.indrelid=to_regclass('idosgd.'||tabla);
 IF pk IS NULL AND tabla='tdtx_dependencia_empleado' THEN pk:=ARRAY['co_dep','co_emp']; END IF;
 SELECT array_agg(key ORDER BY key) INTO campos FROM jsonb_object_keys(clave) key;
 IF pk IS NULL OR campos IS DISTINCT FROM pk THEN RAISE EXCEPTION 'Clave primaria incompleta en %%',tabla; END IF;
 comparable := datos;
 FOR k IN SELECT jsonb_object_keys(datos) LOOP
   IF datos->>k='__AHORA__' THEN
     comparable := comparable-k;
     datos := jsonb_set(datos,ARRAY[k],to_jsonb(current_timestamp));
   END IF;
 END LOOP;
 -- Conservar claves, contadores de acceso y oficina principal en usuarios compartidos.
 comparable := comparable-ARRAY['cclave','nu_intento','cemp_co_depend','co_dependencia'];
 EXECUTE format('SELECT jsonb_agg(to_jsonb(t)) FROM idosgd.%%I t WHERE to_jsonb(t) @> $1',tabla)
 INTO filas USING clave;
 IF coalesce(jsonb_array_length(filas),0)>1 THEN RAISE EXCEPTION 'Clave ambigua en %%',tabla; END IF;
 actual := filas->0;
 nuevo := actual IS NULL;
 IF NOT nuevo AND NOT (actual @> comparable) THEN
   RAISE EXCEPTION 'Conflicto de datos existentes en %% (clave %%); no se sobrescribe',tabla,clave;
 END IF;
 IF nuevo THEN
   SELECT string_agg(format('%%I',key),',' ORDER BY key),
          string_agg(format('x.%%I',key),',' ORDER BY key)
   INTO nombres,valores FROM jsonb_each(datos);
   EXECUTE format('INSERT INTO idosgd.%%I (%%s) SELECT %%s FROM jsonb_populate_record(NULL::idosgd.%%I,$1) x',
                  tabla,nombres,valores,tabla) USING datos;
   IF tabla='seg_usuarios1' THEN INSERT INTO pg_temp.nuevas_cuentas VALUES(datos->>'cod_user'); END IF;
 END IF;
 INSERT INTO idosgd.piloto_objetos(tabla,clave,creado_por_utilitario,datos)
 VALUES(tabla,clave,nuevo,comparable) ON CONFLICT ON CONSTRAINT piloto_objetos_pkey DO NOTHING;
 INSERT INTO idosgd.piloto_usos(area,tabla,clave) VALUES(area,tabla,clave)
 ON CONFLICT ON CONSTRAINT piloto_usos_pkey DO NOTHING;
END $fn$;
''' % ','.join(q(t) for t in TABLES)


def load_sql(area, users, fingerprint, encrypted=None):
    dep = area['codigo']; loc = area['sede']['codigo']; statements = []

    def add(table, key, values):
        statements.append('PERFORM pg_temp.registrar(%s,%s,%s,%s);' % (q(dep), q(table), j(key), j(values)))

    sede = area['sede']
    add('si_mae_local', {'ccod_local': loc}, dict(ccod_local=loc, de_nombre_local=sede['nombre'],
        de_direccion_local=sede['direccion'], es_local='1', user_creator='admin', date_creator='__AHORA__'))
    cargos = {u['cargo_codigo']: u['cargo_nombre'] for u in users}
    for cargo, name in cargos.items():
        add('rhtm_cargos', {'ccar_co_cargo': cargo}, dict(ccar_co_cargo=cargo, ccar_descar=name, ccar_est_car='1'))
    titular = next(u for u in users if u['codigo_empleado'] == area['titular_empleado'])
    add('rhtm_dependencia', {'co_dependencia': dep}, dict(co_dependencia=dep, de_dependencia=area['nombre'],
        in_baja='0', co_nivel=str(area['nivel']), de_corta_depen=area['nombre_corto'],
        co_depen_padre=area['padre'], ti_dependencia='1', id_crea='admin', id_act='admin',
        de_sigla=area['sigla'], co_cargo=titular['cargo_codigo'], co_empleado=titular['codigo_empleado'],
        co_emp_titular=titular['codigo_empleado'], co_tipo_encargatura='1', in_numero_mp='0',
        in_mesa_partes='1' if area['mesa_partes'] else '0', de_descrip='Alta por utilitario de piloto INIA'))
    route = str(DATA / 'documentos')
    for user in users:
        emp = user['codigo_empleado']; login = user['usuario']; profile = area['perfiles'][user['perfil']]
        add('rhtm_per_empleados', {'cemp_codemp': emp}, dict(cemp_codemp=emp, cemp_indbaj='1',
            femp_fecalt='__AHORA__', cemp_apepat=user['apellido_paterno'], cemp_apemat=user['apellido_materno'],
            cemp_denom=user['nombres'], cemp_email=user['email'], cemp_est_emp='1', cemp_co_depend=dep,
            co_dependencia=dep, cemp_co_cargo=user['cargo_codigo'], cemp_nu_dni=user['dni'],
            cemp_co_local=loc, cemp_fe_crea='__AHORA__', cemp_id_crea='admin'))
        add('seg_usuarios1', {'cod_user': login}, dict(cod_user=login, cdes_user=login,
            dfec_cre='__AHORA__', cuser_cre='admin', cclave=(encrypted or {}).get(login,'__CIFRADO_PENDIENTE__'),
            cemp_codemp=emp, es_usuario='A', in_acceso='1', dfe_mod_clave='__AHORA__', nu_intento=0, in_ad='0'))
        add('seg_user_aplica', {'cod_user': login, 'cod_aplica': '9'}, dict(cod_user=login, cod_aplica='9',
            es_admin='1' if profile['administrador'] else '0', es_activo='1', fe_activo='__AHORA__'))
        for option in profile['opciones']:
            add('seg_user_aplica_opc', {'cod_user': login, 'cod_aplica': '9', 'cod_opc': option},
                dict(cod_user=login, cod_aplica='9', cod_opc=option, es_habilitado='1'))
        add('tdtr_permisos', {'co_use': login, 'co_dep': dep}, dict(co_use=login, co_dep=dep, es_act='1', es_act_wf='1'))
        add('tdtx_dependencia_empleado', {'co_dep': dep, 'co_emp': emp}, dict(co_dep=dep, co_emp=emp, es_emp='1'))
        add('tdtx_config_emp', {'co_emp': emp, 'co_dep': dep}, dict(co_emp=emp, co_dep=dep,
            co_loc=loc, fe_use_cre='__AHORA__', fe_use_mod='__AHORA__', de_dir_emi=route, de_dir_rec=route,
            de_dir_ane=route, ti_emi='0', ti_acceso='1' if profile['administrador'] else '0', es_reg='1',
            in_boton_generar='1', in_boton_cargar='1', in_mail='0', in_carga_doc='1', in_carga_emi='1',
            in_firma='1' if profile['firma'] else '0', in_tipo_doc='1', in_obs_docu='1', in_revi_docu='1',
            in_gene_num_emi='1', in_consulta='1', in_consulta_mp='1' if area['mesa_partes'] else '0',
            ti_acceso_mp='0', in_mail_deriv='0', de_pie_pagina='INIA - piloto'))
        if area['mesa_partes']:
            add('tdtr_permiso_mp', {'co_emp': emp, 'co_dep': dep}, dict(co_emp=emp, co_dep=dep,
                in_cambio_est='0', es_eli='0', fe_use_cre='__AHORA__', fe_use_mod='__AHORA__',
                co_use_cre='admin', co_use_mod='admin'))
    add('sitm_local_dependencia', {'co_dep': dep, 'co_loc': loc, 'nu_correlativo': 0}, dict(co_dep=dep, co_loc=loc,
        nu_correlativo=0, es_eli='0', co_use_cre='admin', co_use_mod='admin',
        fe_use_cre='__AHORA__', fe_use_mod='__AHORA__'))
    for doc in area['tipos_documento']:
        add('sitm_doc_dependencia', {'co_dep': dep, 'co_tip_doc': doc}, dict(co_dep=dep, co_tip_doc=doc,
            es_eli='0', co_use_cre='admin', co_use_mod='admin', fe_use_cre='__AHORA__',
            fe_use_mod='__AHORA__', es_obl_firma='0'))
    if area['mesa_partes']:
        add('tdtr_dependencia_mp', {'co_dep': dep}, dict(co_dep=dep, in_mp='1', es_eli='0',
            fe_use_cre='__AHORA__', fe_use_mod='__AHORA__', co_use_cre='admin', co_use_mod='admin'))
    # Motivos por dependencia se crean sólo con catálogo existente y se rastrean.
    statements.append('''FOR motivo IN SELECT co_mot FROM idosgd.tdtr_motivo LOOP
 PERFORM pg_temp.registrar(%s,'tdtr_mot_dependencia',
 jsonb_build_object('co_dep',%s,'co_mot',motivo),
 jsonb_build_object('co_dep',%s,'co_mot',motivo,'es_eli','0',
 'co_use_cre','admin','co_use_mod','admin','fe_use_cre','__AHORA__','fe_use_mod','__AHORA__'));
END LOOP;''' % (q(dep),q(dep),q(dep)))
    checks = []
    for user in users:
        checks.append('''IF EXISTS(SELECT 1 FROM idosgd.seg_usuarios1 WHERE lower(trim(cod_user))=lower(%s)
 AND (cod_user<>%s OR cemp_codemp IS DISTINCT FROM %s)) THEN
 RAISE EXCEPTION 'Colision de usuario; resolver antes de cargar'; END IF;
IF EXISTS(SELECT 1 FROM idosgd.rhtm_per_empleados WHERE cemp_nu_dni=%s AND cemp_codemp<>%s) THEN
 RAISE EXCEPTION 'Documento ya asociado a otro empleado'; END IF;''' %
            (q(user['usuario']),q(user['usuario']),q(user['codigo_empleado']),q(user['dni']),q(user['codigo_empleado'])))
    parent_check = '' if area['padre'] is None else '''IF NOT EXISTS(SELECT 1 FROM idosgd.rhtm_dependencia
 WHERE co_dependencia=%s AND in_baja='0') THEN RAISE EXCEPTION 'Padre no existe o esta inactivo'; END IF;''' % q(area['padre'])
    # Instalaciones anteriores: resolver sólo la sede temporal reconocida.
    # Usa los datos aprobados del área y comparte la transacción de su carga.
    # Las demás sedes y los valores institucionales existentes no se sobrescriben.
    site_upgrade = ''
    if loc == '001':
        site_upgrade = '''UPDATE idosgd.si_mae_local
 SET de_nombre_local=%s, de_direccion_local=%s
 WHERE ccod_local='001' AND de_nombre_local='INSTALACION LOCAL INIA'
 AND de_direccion_local='PENDIENTE' AND es_local='1' AND user_creator='admin';
 IF FOUND THEN RAISE NOTICE 'Sede provisional 001 actualizada con los datos aprobados del area'; END IF;
''' % (q(sede['nombre']), q(sede['direccion']))
    # No adoptar una dependencia ya existente: no hay limpieza fiable sin procedencia.
    return 'BEGIN;\nSET LOCAL standard_conforming_strings=on;\nSET LOCAL lock_timeout=\'15s\';\n' + LEDGER + \
        'LOCK TABLE ' + ','.join('idosgd.'+t for t in TABLES + ['piloto_areas','piloto_objetos','piloto_usos']) + \
        ' IN SHARE ROW EXCLUSIVE MODE;\nCREATE TEMP TABLE nuevas_cuentas(usuario text) ON COMMIT DROP;\n' + helper_sql() + '''
DO $load$
DECLARE motivo varchar; existente text;
BEGIN
 IF to_regclass('idosgd.instalacion_portable') IS NULL THEN RAISE EXCEPTION 'Base sin marca portable'; END IF;
 SELECT huella INTO existente FROM idosgd.piloto_areas WHERE codigo=%s;
 IF existente IS NOT NULL THEN
   IF existente=%s THEN RAISE NOTICE 'Carga identica ya registrada; no se repite'; RETURN; END IF;
   RAISE EXCEPTION 'El area ya tiene una carga diferente; no se sobrescribe';
 END IF;
 IF EXISTS(SELECT 1 FROM idosgd.rhtm_dependencia WHERE co_dependencia=%s) THEN
   RAISE EXCEPTION 'Codigo de dependencia ya existente fuera de esta carga'; END IF;
 %s
 %s
 INSERT INTO idosgd.piloto_areas(codigo,huella,nombre,verificado_por) VALUES(%s,%s,%s,%s);
 %s
END $load$;
SELECT '__PILOTO_RESULTADO__' || json_build_object('cuentas_nuevas',coalesce(json_agg(usuario),'[]'::json))::text
FROM pg_temp.nuevas_cuentas;
COMMIT;
''' % (q(dep),q(fingerprint),q(dep),parent_check,'\n'.join(checks),q(dep),q(fingerprint),
       q(area['nombre']),q(area['verificado_por']),site_upgrade+'\n'.join(statements))


def clean_sql(dep):
    # Candidatos sólo creados por este utilitario y sin uso por otra área.
    return '''BEGIN;
SET LOCAL standard_conforming_strings=on;
SET LOCAL lock_timeout='15s';
DO $clean$
DECLARE item record; col record; relacion record; actual jsonb; filas jsonb; valor text; usado boolean;
        pk text[]; campos text[];
BEGIN
 IF to_regclass('idosgd.piloto_areas') IS NULL THEN RAISE EXCEPTION 'No hay cargas rastreadas'; END IF;
 IF NOT EXISTS(SELECT 1 FROM idosgd.piloto_areas WHERE codigo=%s) THEN
   RAISE EXCEPTION 'Area no cargada por este utilitario; no se elimina'; END IF;
 -- Bloquear escritores durante validación y borrado; no existe ventana entre ambos.
 FOR relacion IN SELECT tablename FROM pg_tables WHERE schemaname='idosgd' ORDER BY tablename LOOP
   EXECUTE format('LOCK TABLE idosgd.%%I IN EXCLUSIVE MODE',relacion.tablename);
 END LOOP;
 CREATE TEMP TABLE retirar ON COMMIT DROP AS
 SELECT o.* FROM idosgd.piloto_objetos o JOIN idosgd.piloto_usos u USING(tabla,clave)
 WHERE u.area=%s AND o.creado_por_utilitario AND o.tabla NOT IN ('si_mae_local','rhtm_cargos')
 AND NOT EXISTS(SELECT 1 FROM idosgd.piloto_usos otro
                WHERE otro.tabla=o.tabla AND otro.clave=o.clave AND otro.area<>%s);
 FOR item IN SELECT * FROM retirar LOOP
   IF NOT (item.tabla=ANY(ARRAY[%s]::text[])) THEN RAISE EXCEPTION 'Tabla fuera de alcance'; END IF;
   SELECT array_agg(a.attname::text ORDER BY a.attname) INTO pk
   FROM pg_index i JOIN pg_attribute a ON a.attrelid=i.indrelid AND a.attnum=ANY(i.indkey)
   WHERE i.indisprimary AND i.indrelid=to_regclass('idosgd.'||item.tabla);
   IF pk IS NULL AND item.tabla='tdtx_dependencia_empleado' THEN pk:=ARRAY['co_dep','co_emp']; END IF;
   SELECT array_agg(key ORDER BY key) INTO campos FROM jsonb_object_keys(item.clave) key;
   IF pk IS NULL OR campos IS DISTINCT FROM pk THEN RAISE EXCEPTION 'Registro de procedencia con clave invalida'; END IF;
   EXECUTE format('SELECT jsonb_agg(to_jsonb(t)) FROM idosgd.%%I t WHERE to_jsonb(t) @> $1',item.tabla)
   INTO filas USING item.clave;
   IF coalesce(jsonb_array_length(filas),0)>1 THEN RAISE EXCEPTION 'Procedencia ambigua; resolver duplicados antes de retirar'; END IF;
   actual:=filas->0;
   IF actual IS NULL THEN RAISE EXCEPTION 'Registro rastreado ausente; revisar procedencia antes de retirar'; END IF;
   IF actual IS NOT NULL AND NOT (actual @> item.datos) THEN
     RAISE EXCEPTION 'Registro modificado desde la carga en %%: revisar manualmente',item.tabla;
   END IF;
   IF item.tabla IN ('rhtm_dependencia','rhtm_per_empleados','seg_usuarios1') THEN
     valor := CASE item.tabla WHEN 'rhtm_dependencia' THEN item.clave->>'co_dependencia'
              WHEN 'rhtm_per_empleados' THEN item.clave->>'cemp_codemp' ELSE item.clave->>'cod_user' END;
     -- Las vistas proyectan las mismas filas, no son referencias independientes.
     FOR col IN SELECT table_name,column_name FROM information_schema.columns
       JOIN information_schema.tables USING(table_schema,table_name)
       WHERE table_type='BASE TABLE' AND table_schema='idosgd' AND table_name NOT LIKE 'piloto_%%'
         AND table_name NOT IN ('seg_usuarios_acceso','seg_usuarios_log')
         AND (CASE item.tabla
          WHEN 'rhtm_dependencia' THEN column_name ~ '^(co_dep|cod_dep|cemp_co_depend)' AND character_maximum_length=5
          WHEN 'rhtm_per_empleados' THEN (column_name ~ '^(co_emp|cemp_codemp)' OR column_name IN ('co_responsable','cemp_co_emp')) AND character_maximum_length=5
          ELSE column_name IN ('cod_user','co_use','co_usuario') END)
     LOOP
       EXECUTE format('SELECT EXISTS(SELECT 1 FROM idosgd.%%I t WHERE t.%%I=$1 AND NOT EXISTS
         (SELECT 1 FROM pg_temp.retirar r WHERE r.tabla=$2 AND to_jsonb(t) @> r.clave))',
         col.table_name,col.column_name) INTO usado USING valor,col.table_name;
       IF usado THEN RAISE EXCEPTION 'Retiro bloqueado: existe uso en %%.%%; conservar historia o resolver relacion',
          col.table_name,col.column_name; END IF;
     END LOOP;
   END IF;
 END LOOP;
 -- Orden inverso al alta, respetando claves foráneas. Nunca CASCADE ni TRUNCATE.
 FOR item IN SELECT * FROM retirar
   ORDER BY array_position(ARRAY[%s]::text[],tabla) DESC LOOP
   EXECUTE format('DELETE FROM idosgd.%%I t WHERE to_jsonb(t) @> $1',item.tabla) USING item.clave;
 END LOOP;
 DELETE FROM idosgd.piloto_usos WHERE area=%s;
 DELETE FROM idosgd.piloto_objetos o USING retirar r WHERE o.tabla=r.tabla AND o.clave=r.clave;
 DELETE FROM idosgd.piloto_areas WHERE codigo=%s;
END $clean$;
COMMIT;
''' % (q(dep),q(dep),q(dep),','.join(q(t) for t in TABLES),','.join(q(t) for t in TABLES),q(dep),q(dep))


def pg_settings():
    from sgd import pg_home
    config = json.loads((ROOT / 'configuracion.json').read_text(encoding='utf-8'))
    require(config.get('modo') != 'solo_payara', 'Ejecutar desde el paquete del servidor de base de datos')
    credentials = json.loads((DATA / 'credenciales.json').read_text(encoding='utf-8'))
    env = os.environ.copy(); env['PGPASSWORD'] = credentials['postgres']; env['PGCLIENTENCODING'] = 'UTF8'
    env['PATH'] = str(pg_home() / 'bin') + os.pathsep + env.get('PATH','')
    if os.name != 'nt': env['LD_LIBRARY_PATH'] = str(pg_home() / 'lib') + os.pathsep + env.get('LD_LIBRARY_PATH','')
    args = ['-h',config['host_bd'],'-p',str(config['puerto_bd']),'-U','postgres','-d',config['base']]
    return pg_home(),args,env


def backup(work):
    home,args,env = pg_settings(); target = work / 'antes-de-aplicar.dump'
    temporary = target.with_suffix('.tmp')
    with temporary.open('wb') as f:
        p = subprocess.run([str(home/'bin'/('pg_dump.exe' if os.name=='nt' else 'pg_dump')), *args,'-Fc'],
                           env=env, stdout=f, stderr=subprocess.PIPE)
    if p.returncode:
        temporary.unlink(missing_ok=True)
        raise RuntimeError('Falló pg_dump; no se aplicó ningún cambio. Revisar conexión y permisos.')
    require(temporary.stat().st_size>0, 'Respaldo vacío; no continuar')
    temporary.replace(target)
    h = hashlib.sha256()
    with target.open('rb') as f:
        for block in iter(lambda: f.read(1024*1024), b''): h.update(block)
    private_write(work/'respaldo.sha256', h.hexdigest()+'\n')
    return target


def execute_sql(sql, work):
    home,args,env = pg_settings()
    p = subprocess.run([str(home/'bin'/('psql.exe' if os.name=='nt' else 'psql')),*args,
                        '-X','-At','-v','ON_ERROR_STOP=1','-f','-'], input=sql.encode(), env=env,
                       stdout=subprocess.PIPE,stderr=subprocess.PIPE)
    output = (p.stdout+p.stderr).decode('utf-8',errors='replace')
    # Un error de PostgreSQL puede incluir parte de una fila con clave cifrada.
    output = re.sub(r'[A-Fa-f0-9]{32,}', '[valor omitido]', output)
    private_write(work/'ejecucion.log',output)
    require(p.returncode==0, 'La transacción no se confirmó. Revisar '+str(work/'ejecucion.log'))
    for line in p.stdout.decode('utf-8',errors='replace').splitlines():
        if line.startswith('__PILOTO_RESULTADO__'):
            return json.loads(line.removeprefix('__PILOTO_RESULTADO__'))
    return {}


def make_passwords(users):
    from sgd import java_home
    jar = ROOT/'fuentes/Sgd/libreria/target/libreria-onpe-1.0.jar'
    require(jar.exists(), 'Falta librería de cifrado; ejecutar SGD compilar primero')
    java = java_home()/'bin'; ext = '.exe' if os.name=='nt' else ''
    cp = str(jar)+os.pathsep+str(ROOT/'herramientas')
    p = subprocess.run([str(java/('javac'+ext)),'-encoding','UTF-8','-cp',str(jar),
                        str(ROOT/'herramientas/CifrarClave.java')],capture_output=True)
    require(p.returncode==0, 'No se pudo preparar la utilidad Java de cifrado')
    plain = {}; encrypted = {}
    for u in users:
        pwd = 'Sgd!'+secrets.token_urlsafe(18)
        p = subprocess.run([str(java/('java'+ext)),'-cp',cp,'CifrarClave'],input=(pwd+'\n').encode(),capture_output=True)
        value = p.stdout.decode().strip()
        require(p.returncode==0 and re.fullmatch('[A-Fa-f0-9]+',value), 'No se pudo cifrar una clave')
        plain[u['usuario']]=pwd; encrypted[u['usuario']]=value
    return plain,encrypted


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('accion',choices=['plantilla','cargar','limpiar','catalogos'])
    parser.add_argument('--carpeta',type=Path)
    parser.add_argument('--area')
    parser.add_argument('--aplicar',action='store_true')
    parser.add_argument('--confirmar',default='')
    args = parser.parse_args(argv)
    if args.accion=='catalogos':
        for row in catalogs()['seg_opciones']:
            if row[0]=='9': print(row[1]+' | '+row[2])
        return
    if args.accion in ['plantilla','cargar']:
        if args.carpeta is None:
            args.carpeta = Path(input('Carpeta con area.json y usuarios-verificados.csv: ').strip().strip('"'))
        folder = args.carpeta.resolve()
        if args.accion=='plantilla':
            folder.mkdir(parents=True,exist_ok=True)
            for name in ['area.json','usuarios-verificados.csv']:
                target=folder/name
                require(not target.exists(),'Ya existe '+str(target)+'; no se sobrescribe')
            for name in ['area.json','usuarios-verificados.csv']:
                private_write(folder/name,(ROOT/'bd'/('piloto-'+name+'.ejemplo')).read_text(encoding='utf-8'))
            print('Plantillas creadas. Completar y verificar antes de cargar: '+str(folder)); return
        area,users,fingerprint = read_bundle(folder); dep=area['codigo']
        sql = load_sql(area,users,fingerprint)
        summary = {'area':dep,'nombre':area['nombre'],'usuarios_verificados':len(users),
                   'perfiles':sorted({u['perfil'] for u in users}),'huella':fingerprint,
                   'estado':'REVISION OFFLINE; NO SE CONSULTO LA BASE'}
    else:
        dep=code(args.area or input('Código de área a retirar (5 dígitos): ').strip(),'area',5)
        require(dep not in RESERVED,'No retirar áreas de laboratorio con este utilitario')
        sql=clean_sql(dep)
        summary={'area':dep,'estado':'PLAN OFFLINE; NO SE CONSULTO LA BASE',
                 'alcance':'Sólo objetos rastreados sin uso por otras áreas; conservar catálogos y documentos'}
    print(json.dumps(summary,ensure_ascii=False,indent=2))
    stamp=datetime.now(timezone.utc).strftime('%Y%m%dT%H%M%S')+'-'+secrets.token_hex(3)
    work=DATA/('piloto-'+dep+'-'+args.accion+'-'+stamp)
    private_folder(work)
    private_write(work/'revision.json',json.dumps(summary,ensure_ascii=False,indent=2))
    if args.accion=='cargar':
        private_write(work/'area-verificada.json',json.dumps(area,ensure_ascii=False,indent=2))
        import io
        snapshot=io.StringIO(newline='');writer=csv.DictWriter(snapshot,fieldnames=FIELDS)
        writer.writeheader();writer.writerows(users)
        private_write(work/'usuarios-verificados.csv',snapshot.getvalue())
    if not args.aplicar:
        prefix='-- PLAN PARA REVISION. No ejecutar este SQL manualmente. Usar el utilitario con --aplicar.\n'
        private_write(work/'plan.sql',prefix+sql)
        print('Sin cambios ni conexión a la base. Plan: '+str(work/'plan.sql'))
        print('Para aplicar, añadir --aplicar --confirmar '+('CARGAR:' if args.accion=='cargar' else 'RETIRAR:')+dep)
        return
    token=('CARGAR:' if args.accion=='cargar' else 'RETIRAR:')+dep
    require(args.confirmar==token,'Confirmación incorrecta; se requiere '+token)
    print('Creando respaldo completo antes de modificar PostgreSQL...')
    target=backup(work)
    if args.accion=='cargar':
        plain,encrypted=make_passwords(users)
        sql=load_sql(area,users,fingerprint,encrypted)
        # Sólo cuentas nuevas usarán estas claves. Las existentes conservan la suya.
        private_write(work/'CLAVES-GENERADAS-PRIVADAS.json',json.dumps(plain,ensure_ascii=False,indent=2))
    result = execute_sql(sql,work)
    if args.accion=='cargar':
        require('cuentas_nuevas' in result,'Transacción aplicada, pero falta reporte de altas. Conservar claves provisionales y revisar el log')
        plain = {u:plain[u] for u in result['cuentas_nuevas']}
        private_write(work/'CLAVES-GENERADAS-PRIVADAS.json',json.dumps(plain,ensure_ascii=False,indent=2))
        summary['cuentas_nuevas']=len(plain)
    summary['estado']='TRANSACCION CONFIRMADA'
    private_write(work/'resultado.json',json.dumps(summary,ensure_ascii=False,indent=2))
    print('Operación terminada. Respaldo: '+str(target))
    print('Resultado: '+str(work/'resultado.json'))
    if args.accion=='cargar':
        print('Claves propuestas para altas NUEVAS en '+str(work/'CLAVES-GENERADAS-PRIVADAS.json'))
        print('Las cuentas existentes conservan su clave. Repetir una carga idéntica no cambia claves.')


if __name__=='__main__':
    try:
        main()
    except (ValueError,RuntimeError,OSError,json.JSONDecodeError) as exc:
        print('ERROR: '+str(exc),file=sys.stderr)
        sys.exit(1)
