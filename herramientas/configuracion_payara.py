"""Copia y verifica la configuracion Payara sin arrancar procesos ni consultar BD.

La copia incluye las credenciales y certificados del dominio piloto, por
autorizacion del operador. No incluye la carpeta datos ni archivos de actividad.
"""
from pathlib import Path, PurePosixPath
import argparse
import datetime
import hashlib
import json
import shutil
import sys
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
DOMAIN = ROOT / 'payara/payara5/glassfish/domains/sgd'
SNAPSHOT = ROOT / 'payara/configuracion'
CONFIG_FILES = (
    'domain.xml', 'admin-keyfile', 'keyfile', 'domain-passwords',
    'local-password', 'keystore.jks', 'cacerts.jks', 'logging.properties',
    'default-logging.properties', 'default-web.xml', 'glassfish-acc.xml',
    'javaee.server.policy', 'login.conf', 'restrict.server.policy',
    'server.policy', 'wss-server-config-1.0.xml', 'wss-server-config-2.0.xml',
)
PROPERTY_DIRS = (
    'sgdproperties', 'consultaproperties', 'mpvproperties',
    'verificaproperties', 'notificadorproperties',
)


def digest(path):
    with path.open('rb') as stream:
        return hashlib.file_digest(stream, 'sha256').hexdigest()


def safe_file(base, relative):
    parts = PurePosixPath(relative)
    if parts.is_absolute() or not parts.parts or any(
            part in ('.', '..') or ':' in part or '\\' in part
            for part in parts.parts):
        raise ValueError('Ruta invalida en manifiesto.')
    base = base.resolve()
    path = base.joinpath(*parts.parts).resolve()
    if not path.is_relative_to(base):
        raise ValueError('La ruta sale de la carpeta permitida.')
    return path


def export_config(domain=DOMAIN, output=SNAPSHOT):
    domain, output = Path(domain), Path(output)
    if (output / 'MANIFIESTO.json').exists():
        raise ValueError('La copia ya existe. Exporte a otra carpeta con --destino y revise las diferencias.')
    tree = ET.parse(domain / 'config/domain.xml')
    paths = [Path('config') / name for name in CONFIG_FILES
             if (domain / 'config' / name).is_file()]
    for folder in PROPERTY_DIRS:
        if (domain / folder).is_dir():
            paths.extend(path.relative_to(domain)
                         for path in sorted((domain / folder).glob('*.properties')))
    paths.extend(path.relative_to(domain)
                 for path in sorted((domain / 'lib').glob('postgresql-*.jar')))
    if (domain / 'docroot/favicon.ico').is_file():
        paths.append(Path('docroot/favicon.ico'))
    expected = {path.as_posix() for path in paths}
    existing = output / 'dominio'
    if existing.exists() and any(existing.rglob('*')):
        raise ValueError('La carpeta destino contiene archivos. Use otra carpeta.')
    manifest = {
        'formato': 1,
        'fecha_utc': datetime.datetime.now(datetime.timezone.utc).isoformat(),
        'payara': '5.2022.5',
        'java': 'Temurin 8u504',
        'dominio': 'sgd',
        'contiene_secretos_del_dominio': True,
        'sql_ejecutado': False,
        'opciones_jvm_server': [node.text for node in tree.findall(
            './/config[@name="server-config"]/java-config/jvm-options')],
        'archivos': {},
        'runtime_externo': {},
    }
    for relative in sorted(expected):
        source = safe_file(domain, relative)
        target = safe_file(output / 'dominio', relative)
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(source, target)
        manifest['archivos'][relative] = digest(target)
    for relative in ('payara/payara-5.2022.5-backup.zip',
                     'payara/driver/postgresql-42.7.8.jar'):
        source = ROOT / relative
        if source.is_file():
            manifest['runtime_externo'][relative] = digest(source)
    output.mkdir(parents=True, exist_ok=True)
    (output / 'MANIFIESTO.json').write_text(
        json.dumps(manifest, indent=2, ensure_ascii=False) + '\n', encoding='utf-8')
    verify_config(output)
    return manifest


def verify_config(snapshot=SNAPSHOT):
    snapshot = Path(snapshot)
    manifest = json.loads((snapshot / 'MANIFIESTO.json').read_text(encoding='utf-8'))
    if manifest.get('formato') != 1 or not manifest.get('archivos'):
        raise ValueError('Manifiesto no reconocido o vacio.')
    if 'config/domain.xml' not in manifest['archivos']:
        raise ValueError('Falta domain.xml en manifiesto.')
    actual = {path.relative_to(snapshot / 'dominio').as_posix()
              for path in (snapshot / 'dominio').rglob('*') if path.is_file()}
    if actual != set(manifest['archivos']):
        raise ValueError('Los archivos de la copia no coinciden con el manifiesto.')
    for relative, expected in manifest['archivos'].items():
        if digest(safe_file(snapshot / 'dominio', relative)) != expected:
            raise ValueError('Hash distinto: ' + relative)
    ET.parse(snapshot / 'dominio/config/domain.xml')
    return manifest


def restore_config(snapshot, domain, backup, confirmation):
    if confirmation != 'PAYARA:DOMINIO':
        raise ValueError('La restauracion requiere --confirmar PAYARA:DOMINIO.')
    snapshot, domain, backup = Path(snapshot), Path(domain), Path(backup)
    manifest = verify_config(snapshot)
    # Se rechaza incluso un PID antiguo: el operador debe comprobar la parada.
    if (domain / 'config/pid').exists():
        raise ValueError('Existe config/pid. Detenga Payara y compruebe que no este en ejecucion antes de restaurar.')
    if not (domain.parent.parent / 'lib/client/appserver-cli.jar').is_file():
        raise ValueError('El destino no pertenece a una instalacion Payara preparada.')
    if backup.resolve().is_relative_to(domain.resolve()) or domain.resolve().is_relative_to(backup.resolve()):
        raise ValueError('El respaldo debe estar fuera del dominio destino.')
    if backup.resolve().is_relative_to(Path(snapshot).resolve()):
        raise ValueError('El respaldo privado debe estar fuera de la copia versionada.')
    backup.mkdir(parents=True, exist_ok=False)
    # Todas las comprobaciones se completan antes de modificar el dominio.
    for relative in manifest['archivos']:
        target = safe_file(domain, relative)
        if target.exists():
            saved = safe_file(backup, relative)
            saved.parent.mkdir(parents=True, exist_ok=True)
            shutil.copy2(target, saved)
    for relative in manifest['archivos']:
        target = safe_file(domain, relative)
        target.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(safe_file(snapshot / 'dominio', relative), target)
    # No se arrancan Payara/PostgreSQL ni se aplican SQL.
    return manifest


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('accion', choices=['exportar', 'verificar', 'restaurar'])
    parser.add_argument('--dominio', type=Path, default=DOMAIN)
    parser.add_argument('--destino', type=Path, default=SNAPSHOT)
    parser.add_argument('--respaldo', type=Path)
    parser.add_argument('--confirmar')
    args = parser.parse_args()
    try:
        if args.accion == 'exportar':
            result = export_config(args.dominio, args.destino)
        elif args.accion == 'verificar':
            result = verify_config(args.destino)
        else:
            backup = args.respaldo or ROOT / 'datos' / (
                'respaldo-payara-config-' + datetime.datetime.now().strftime('%Y%m%dT%H%M%S%f'))
            result = restore_config(args.destino, args.dominio, backup, args.confirmar)
        print('Configuracion ' + args.accion + ': ' + str(len(result['archivos'])) +
              ' archivos. No se ejecutaron SQL ni se arrancaron servidores.')
    except (OSError, ValueError, ET.ParseError) as error:
        print('ERROR: ' + str(error), file=sys.stderr)
        return 1
    return 0


if __name__ == '__main__':
    sys.exit(main())
