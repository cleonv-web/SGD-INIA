"""Validación previa de una carga por área, sin conexión a bases de datos.

Reutiliza las reglas del cargador. Sólo lee el paquete y los archivos revisados;
escribe un informe privado, sin usuarios, DNI, claves ni SQL ejecutable.
"""
from pathlib import Path
import argparse
import csv
import hashlib
import json
import os
import platform
import re
import sys
from datetime import datetime, timezone

ROOT = Path(__file__).resolve().parent
if str(ROOT) not in sys.path:
    sys.path.insert(0, str(ROOT))
import gestion_data as carga


def comprobar_modelo(sql, root=ROOT):
    """Comparar columnas y claves de la carga con el DDL incluido, no con una BD."""
    schema = (root / 'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
    model = {t: set(re.findall(r'^\s+(\w+)\s', body, re.M)) for t, body in
             re.findall(r'CREATE TABLE idosgd\.(\w+) \((.*?)\n\);', schema, re.S)}
    carga.require(set(carga.TABLES) <= set(model), 'Faltan tablas de la carga en el modelo local')
    keys = {t: {x.strip() for x in columns.split(',')} for t, columns in
            re.findall(r'ALTER TABLE ONLY idosgd\.(\w+)\s+ADD CONSTRAINT \w+ PRIMARY KEY \(([^)]+)\)', schema)}
    # El proveedor usa una clave funcional en esta relación, sin PK declarada.
    keys['tdtx_dependencia_empleado'] = {'co_dep', 'co_emp'}
    calls = re.findall(
        r"PERFORM pg_temp.registrar\('[^']*','([^']*)','((?:[^']|'')*)'::jsonb,'((?:[^']|'')*)'::jsonb\);", sql)
    carga.require(bool(calls), 'No se identifican registros para comprobar el modelo')
    for table, key, values in calls:
        fields = json.loads(values.replace("''", "'"))
        identity = json.loads(key.replace("''", "'"))
        carga.require(set(fields) <= model[table], 'Columnas incompatibles con el modelo local: ' + table)
        carga.require(set(identity) == keys.get(table), 'Clave incompatible con el modelo local: ' + table)
    dynamic = {'co_dep', 'co_mot', 'es_eli', 'co_use_cre', 'co_use_mod', 'fe_use_cre', 'fe_use_mod'}
    carga.require(dynamic <= model['tdtr_mot_dependencia'], 'Columnas de motivos incompatibles')
    carga.require({'co_mot'} <= model.get('tdtr_motivo', set()), 'Falta el catálogo local de motivos')
    carga.require(keys.get('tdtr_mot_dependencia') == {'co_dep', 'co_mot'}, 'Clave de motivos incompatible')
    return {'tablas_comprobadas': len(carga.TABLES), 'registros_comprobados': len(calls)}


def comprobar_entorno(root=ROOT, sistema=None):
    """Sólo comprobar archivos; no importar sgd ni invocar motores o Java."""
    sistema = sistema or ('windows' if os.name == 'nt' else 'linux')
    config = json.loads((root / 'configuracion.json').read_text(encoding='utf-8'))
    carga.require(isinstance(config, dict), 'configuracion.json debe contener un objeto')
    carga.require(config.get('modo', 'completo') in ['completo', 'solo_bd'],
                  'Ejecutar la carga desde el paquete de base de datos, no desde solo_payara')
    carga.require(isinstance(config.get('host_bd'), str) and bool(config['host_bd'].strip()), 'Falta host_bd')
    port = config.get('puerto_bd')
    carga.require(type(port) is int and 1 <= port <= 65535, 'puerto_bd debe ser un entero entre 1 y 65535')
    carga.require(isinstance(config.get('base'), str) and re.fullmatch('[a-z][a-z0-9_]*', config['base']),
                  'Nombre de base inválido')
    creds = json.loads((root / 'datos/credenciales.json').read_text(encoding='utf-8'))
    carga.require(isinstance(creds, dict) and isinstance(creds.get('postgres'), str)
                  and bool(creds['postgres']), 'Falta la credencial PostgreSQL de esta instalación')
    required = [root / 'fuentes/Sgd/libreria/target/libreria-onpe-1.0.jar',
                root / 'herramientas/CifrarClave.java']
    if sistema == 'windows':
        homes = sorted((root / 'java/windows').glob('jdk*'))
        carga.require(bool(homes), 'Falta Java 8 en java/windows')
        required.extend(homes[0] / 'bin' / name for name in ['java.exe', 'javac.exe'])
        required.extend(root / 'bd/postgresql/windows/bin' / name for name in ['psql.exe', 'pg_dump.exe'])
    else:
        carga.require(platform.machine() in ['x86_64', 'AMD64'], 'El paquete requiere Linux x64')
        for home, archive, names in [
            ('java/linux', 'java/linux-x64.tar.gz', ['java', 'javac']),
            ('bd/postgresql/linux', 'bd/postgresql-linux-x64.tar.gz', ['psql', 'pg_dump'])]:
            if (root / home).is_dir():
                required.extend(root / home / 'bin' / name for name in names)
            else:
                required.append(root / archive)
    missing = [p.relative_to(root).as_posix() for p in required if not p.is_file() or p.stat().st_size == 0]
    carga.require(not missing, 'Faltan archivos necesarios: ' + ', '.join(missing))
    return {'modo': config.get('modo', 'completo'), 'sistema': sistema,
            'destino': {'host': config['host_bd'], 'puerto': port, 'base': config['base']},
            'conexion_comprobada': False}


def validar(folder, root=ROOT):
    report = {'fecha_utc': datetime.now(timezone.utc).isoformat(), 'carpeta': str(folder),
              'resultado': 'ERROR', 'base_consultada': False, 'comprobaciones': [], 'errores': []}
    def check(name, operation):
        try:
            details = operation()
            report['comprobaciones'].append({'nombre': name, 'estado': 'OK', 'detalle': details})
            return details
        except (ValueError, OSError, KeyError, TypeError) as exc:
            message = str(exc)
            report['comprobaciones'].append({'nombre': name, 'estado': 'ERROR', 'detalle': message})
            report['errores'].append(name + ': ' + message)
            return None

    def selection():
        with (folder / 'usuarios-verificados.csv').open(encoding='utf-8-sig', newline='') as f:
            reader = csv.DictReader(f)
            carga.require(reader.fieldnames in [carga.FIELDS, carga.FIELDS[:-1]],
                          'Encabezados inválidos; usar el CSV preparado separado por coma')
            rows = list(reader)
        for number, row in enumerate(rows, 2):
            carga.require(None not in row and all(v is not None for v in row.values()),
                          'Fila %s: cantidad de columnas incorrecta' % number)
            carga.require(row.get('incluir_sgd', '1').strip() in ['0', '1'],
                          'Fila %s: incluir_sgd debe ser 0 o 1' % number)
        excluded = sum(r.get('incluir_sgd', '1').strip() == '0' for r in rows)
        return {'filas': len(rows), 'incluidos': len(rows) - excluded, 'excluidos': excluded}

    check('CSV y selección 0/1', selection)
    bundle = None
    try:
        bundle = carga.read_bundle(folder)
    except (ValueError, OSError, KeyError, TypeError) as exc:
        report['comprobaciones'].append({'nombre': 'Área, usuarios y relaciones', 'estado': 'ERROR', 'detalle': str(exc)})
        report['errores'].append('Área, usuarios y relaciones: ' + str(exc))
    if bundle is not None:
        area, users, fingerprint = bundle
        summary = {'codigo': area['codigo'], 'nombre': area['nombre'], 'titular_incluido': True,
                   'sede': area['sede']['codigo'], 'padre': area['padre'],
                   'tipos_documento': area['tipos_documento'],
                   'cargos': sorted({u['cargo_codigo'] for u in users}),
                   'perfiles_utilizados': sorted({u['perfil'] for u in users}), 'huella': fingerprint}
        report['area'] = summary
        report['comprobaciones'].append({'nombre': 'Área, usuarios y relaciones', 'estado': 'OK', 'detalle': summary})
        check('Tablas, columnas y claves del modelo local',
              lambda: comprobar_modelo(carga.load_sql(area, users, fingerprint), root))
        report['archivos_sha256'] = {name: hashlib.sha256((folder / name).read_bytes()).hexdigest()
                                    for name in ['area.json', 'usuarios-verificados.csv']}
    check('Configuración y archivos necesarios para aplicar', lambda: comprobar_entorno(root))
    report['pendiente_al_aplicar'] = ('Conexión y permisos de PostgreSQL, respaldo, padre activo y colisiones '
                                     'con registros actuales. Se verifican al aplicar; este informe no los certifica.')
    if not report['errores']:
        report['resultado'] = 'VALIDACION CORRECTA'
    return report


def main(argv=None):
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('--carpeta', type=Path, default=ROOT / 'datos/carga-uti-preparada',
                        help='Carpeta revisada con area.json y usuarios-verificados.csv')
    args = parser.parse_args(argv)
    report = validar(args.carpeta.resolve())
    target = carga.DATA / 'validacion-carga'
    carga.private_folder(target)
    carga.private_write(target / 'resultado.json', json.dumps(report, ensure_ascii=False, indent=2) + '\n')
    print('VALIDACION DE CARGA: sin conexión ni cambios en bases de datos')
    for item in report['comprobaciones']:
        print('[' + item['estado'] + '] ' + item['nombre'])
        if item['estado'] == 'ERROR':
            print('  ' + item['detalle'])
        elif item['nombre'] == 'CSV y selección 0/1':
            print('  %(incluidos)s incluidos; %(excluidos)s excluidos; %(filas)s filas.' % item['detalle'])
    print(report['resultado'])
    print('Informe: ' + str(target / 'resultado.json'))
    print(report['pendiente_al_aplicar'])
    if not report['errores']:
        print('Área ' + report['area']['codigo'] + ': archivos preparados para aplicar la carga.')
    return 0 if not report['errores'] else 1


if __name__ == '__main__':
    try:
        sys.exit(main())
    except (ValueError, OSError) as exc:
        print('ERROR: ' + str(exc), file=sys.stderr)
        sys.exit(1)
