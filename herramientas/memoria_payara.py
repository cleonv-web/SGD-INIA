"""Prepara o aplica solo -Xmx de server-config. No inicia servicios ni usa SQL."""
from pathlib import Path
import argparse
import datetime
import hashlib
import json
import os
import re
import shutil
import subprocess
import sys
import xml.etree.ElementTree as ET

try:
    from . import configuracion_payara as config
except ImportError:
    # Compatible con el Python portable aislado del instalador Windows.
    sys.path.insert(0, str(Path(__file__).resolve().parent))
    import configuracion_payara as config

ROOT = Path(__file__).resolve().parents[1]
DOMAIN = ROOT / 'payara/payara5/glassfish/domains/sgd'
SNAPSHOT = ROOT / 'payara/configuracion'


def validar_mb(mb):
    if type(mb) is not int or mb not in (1024, 2048, 4096):
        raise ValueError('heap_payara_mb debe ser el entero 1024, 2048 o 4096.')
    return mb


def configurar_heap(jvm, mb=None):
    """Auxiliar puro del instalador: preservar salvo eleccion explicita."""
    options = [node for node in jvm.findall('jvm-options')
               if (node.text or '').strip().startswith('-Xmx')]
    if len(options) > 1:
        raise ValueError('Hay varias opciones -Xmx en server-config.')
    if mb is not None:
        validar_mb(mb)
        node = options[0] if options else ET.SubElement(jvm, 'jvm-options')
        node.text = '-Xmx' + str(mb) + 'm'
    elif not options:
        ET.SubElement(jvm, 'jvm-options').text = '-Xmx1024m'


def cambiar_xml(data, mb):
    """Sustituir exclusivamente el valor Xmx, conservando el resto byte a byte."""
    validar_mb(mb)
    tree = ET.fromstring(data)
    nodes = tree.findall('.//config[@name="server-config"]/java-config')
    if len(nodes) != 1:
        raise ValueError('Se requiere un unico java-config de server-config.')
    options = [node.text.strip() for node in nodes[0].findall('jvm-options')
               if (node.text or '').strip().startswith('-Xmx')]
    if len(options) != 1 or not re.fullmatch(r'-Xmx\d+[kKmMgG]', options[0]):
        raise ValueError('Se requiere una unica opcion -Xmx valida.')
    sections = list(re.finditer(
        rb'<config\b[^>]*\bname\s*=\s*([\'"])server-config\1[^>]*>.*?</config\s*>',
        data, re.S))
    if len(sections) != 1:
        raise ValueError('No se puede delimitar server-config sin alterar el XML.')
    section = sections[0]
    java = list(re.finditer(rb'<java-config\b[^>]*>.*?</java-config\s*>',
                            section.group(), re.S))
    if len(java) != 1:
        raise ValueError('No se puede delimitar java-config.')
    matches = list(re.finditer(
        rb'<jvm-options\b[^>]*>\s*(-Xmx\d+[kKmMgG])\s*</jvm-options\s*>',
        java[0].group()))
    if len(matches) != 1:
        raise ValueError('No se puede sustituir exclusivamente -Xmx.')
    match = matches[0]
    start = section.start() + java[0].start() + match.start(1)
    end = section.start() + java[0].start() + match.end(1)
    changed = data[:start] + ('-Xmx' + str(mb) + 'm').encode('ascii') + data[end:]
    ET.fromstring(changed)
    return changed


def respaldo(files):
    folder = ROOT / 'datos' / ('respaldo-heap-payara-' +
        datetime.datetime.now().strftime('%Y%m%dT%H%M%S%f'))
    folder.mkdir(parents=True, exist_ok=False)
    for name, source in files.items():
        shutil.copy2(source, folder / name)
    return folder


def escribir_atomico(path, data):
    # No sobrescribir temporales que pudieran pertenecer al operador.
    temporary = path.with_name(path.name + '.heap-' +
        datetime.datetime.now().strftime('%Y%m%dT%H%M%S%f') + '.tmp')
    try:
        with temporary.open('xb') as stream:
            stream.write(data)
        temporary.replace(path)
    finally:
        temporary.unlink(missing_ok=True)


def preparar(mb, snapshot=SNAPSHOT):
    snapshot = Path(snapshot)
    manifest = config.verify_config(snapshot)
    xml = snapshot / 'dominio/config/domain.xml'
    data = cambiar_xml(xml.read_bytes(), mb)
    backup = respaldo({'domain.xml': xml, 'MANIFIESTO.json': snapshot / 'MANIFIESTO.json'})
    manifest['archivos']['config/domain.xml'] = hashlib.sha256(data).hexdigest()
    manifest['opciones_jvm_server'] = [node.text for node in ET.fromstring(data).findall(
        './/config[@name="server-config"]/java-config/jvm-options')]
    manifest['ajuste_heap_preparado'] = {
        'fecha_utc': datetime.datetime.now(datetime.timezone.utc).isoformat(),
        'heap_mb': mb,
        'activacion_verificada': False,
    }
    try:
        escribir_atomico(xml, data)
        escribir_atomico(snapshot / 'MANIFIESTO.json',
            (json.dumps(manifest, ensure_ascii=False, indent=2) + '\n').encode('utf-8'))
        config.verify_config(snapshot)
    except Exception:
        shutil.copy2(backup / 'domain.xml', xml)
        shutil.copy2(backup / 'MANIFIESTO.json', snapshot / 'MANIFIESTO.json')
        raise
    return backup


def comprobar_detenido(domain):
    if os.name != 'nt':
        raise ValueError('Aplicar al dominio requiere Windows y comprobacion de procesos.')
    env = os.environ.copy()
    env['SGD_HEAP_DOMAIN'] = str(domain.resolve())
    result = subprocess.run(['powershell.exe', '-NoProfile', '-NonInteractive', '-Command',
        "$ErrorActionPreference='Stop'; Get-CimInstance Win32_Process | "
        "Where-Object { $_.Name -match '^java(w)?\\.exe$' -and $_.CommandLine -and "
        "$_.CommandLine.IndexOf($env:SGD_HEAP_DOMAIN, "
        "[StringComparison]::OrdinalIgnoreCase) -ge 0 } | "
        'ForEach-Object { $_.ProcessId }'], env=env, capture_output=True, text=True,
        timeout=30, check=True)
    if result.stdout.strip():
        raise ValueError('Payara sigue en ejecucion. No se modifica domain.xml.')
    if (domain / 'config/pid').exists():
        raise ValueError('Existe config/pid. Verificar la parada; no se borra automaticamente.')


def aplicar(mb, domain=DOMAIN):
    domain = Path(domain).resolve()
    if not (domain.parent.parent / 'lib/client/appserver-cli.jar').is_file():
        raise ValueError('El destino no es una instalacion Payara preparada.')
    comprobar_detenido(domain)
    xml = domain / 'config/domain.xml'
    original = xml.read_bytes()
    data = cambiar_xml(original, mb)
    backup = respaldo({'domain.xml': xml})
    # Repetir la comprobacion antes de escribir; no arrancar a la vez otro lanzador.
    comprobar_detenido(domain)
    if xml.read_bytes() != original:
        raise ValueError('domain.xml cambio durante la preparacion. No se modifica.')
    escribir_atomico(xml, data)
    return backup


def main():
    parser = argparse.ArgumentParser(description=__doc__)
    parser.add_argument('accion', choices=['preparar', 'aplicar'])
    parser.add_argument('--mb', type=int, choices=[1024, 2048, 4096], required=True)
    parser.add_argument('--dominio', type=Path, default=DOMAIN)
    parser.add_argument('--copia', type=Path, default=SNAPSHOT)
    args = parser.parse_args()
    try:
        backup = (preparar(args.mb, args.copia) if args.accion == 'preparar'
                  else aplicar(args.mb, args.dominio))
        print('Heap preparado en archivo: ' + str(args.mb) + ' MB. Respaldo: ' + str(backup))
        print('Activacion pendiente de inicio y verificacion del proceso. No se ejecuto SQL.')
    except (OSError, ValueError, ET.ParseError, subprocess.SubprocessError) as error:
        # No volcar XML, credenciales ni la salida de comandos externos.
        print('ERROR: ' + (str(error) if not isinstance(error, subprocess.SubprocessError)
                           else 'No se pudo comprobar la parada de Payara.'), file=sys.stderr)
        return 1
    return 0


if __name__ == '__main__':
    sys.exit(main())
