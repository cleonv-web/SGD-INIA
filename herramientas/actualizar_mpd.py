"""Actualiza únicamente Mesa de Partes; no ejecuta SQL ni recrea usuarios."""
import hashlib
import json
import shutil
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd
from herramientas.configuracion_mpd import configurar_mpd


def actualizar():
    if not (sgd.DOMAIN / 'config/domain.xml').is_file():
        raise RuntimeError('Falta la instalación del servidor. Usar Instalar-SGD primero.')
    fuente = sgd.ROOT / 'fuentes/MPVCiudadano'
    print('Compilando Mesa de Partes INIA desde fuentes, sin modificar la base...', flush=True)
    sgd.maven(['clean', 'install', '-DskipTests'], fuente, 'compilar-mpd-inia.log')
    destino = sgd.ROOT / 'binarios/mpd.war'
    shutil.copy2(fuente / 'web/target/mpvdoc-web-1.0.war', destino)
    configurar_mpd(sgd.DOMAIN, sgd.C['url_publica'])
    estado = sgd.admin(['list-domains'], check=False).stdout
    if not any(line.strip().startswith('sgd running') for line in estado.splitlines()):
        sgd.admin(['start-domain', 'sgd'], log='payara-iniciar-mpd.log')
    sgd.admin(['deploy', '--force=true', '--name', 'mpd', '--contextroot', 'mpd', destino], log='payara-desplegar-mpd-inia.log')
    huella = hashlib.sha256()
    for archivo in sorted(fuente.rglob('*')):
        relativo = archivo.relative_to(fuente)
        if archivo.is_file() and 'target' not in relativo.parts:
            huella.update(relativo.as_posix().encode())
            huella.update(archivo.read_bytes())
    registro = {'modulo': 'mpd', 'origen': 'fuentes/MPVCiudadano', 'raiz_hash': 'fuentes/MPVCiudadano',
                'fuentes_sha256': huella.hexdigest(), 'war_sha256': hashlib.sha256(destino.read_bytes()).hexdigest()}
    manifiesto = sgd.LOG / 'compilacion.json'
    entradas = json.loads(manifiesto.read_text(encoding='utf-8')) if manifiesto.exists() else []
    sgd.save(manifiesto, [item for item in entradas if item.get('modulo') != 'mpd'] + [registro])
    print('Mesa de Partes actualizada: ' + sgd.C['url_publica'] + '/mpd/inicio.do', flush=True)
    print('Recargar el navegador con Ctrl+F5. Base, usuarios y contraseñas conservados.', flush=True)


if __name__ == '__main__':
    try:
        actualizar()
    except Exception as error:
        print('ERROR: ' + sgd.redact(str(error)), file=sys.stderr)
        sys.exit(1)
