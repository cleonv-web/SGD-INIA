"""Actualiza los cargos QR del SGD y el autocompletado de Consulta, sin modificar datos."""
import hashlib
import json
import os
import re
import shutil
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
sys.path.insert(0, str(Path(__file__).resolve().parent))
import sgd
from configuracion_mpd import configurar_logo_sgd
from actualizar_consulta import actualizar as actualizar_consulta

URL_DEFAULT = 'https://calidad.inia.gob.pe/consulta/inicio.do'

def compilar_plantillas(muestras=None):
    proyecto = sgd.ROOT / 'fuentes/Sgd/tramitedoc'
    clases = sgd.LOG / 'cargos-qr/clases'
    clases.mkdir(parents=True, exist_ok=True)
    cp = os.pathsep.join([str(proyecto / 'logic/target/classes'),
                         str(proyecto / 'web/target/tramitedoc-web-1.0/WEB-INF/lib/*'),
                         str(sgd.ROOT / 'herramientas/maven-repo/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar'), str(clases)])
    java = sgd.java_home() / 'bin'
    sgd.run([java / ('javac.exe' if os.name == 'nt' else 'javac'), '-encoding', 'UTF-8',
             '-cp', cp, '-d', clases, sgd.ROOT / 'herramientas/CompilarCargosQr.java'],
            env=sgd.java_env(), log='compilar-utilidad-cargos-qr.log')
    args = [java / ('java.exe' if os.name == 'nt' else 'java'), '-Djava.awt.headless=true',
            '-cp', cp, 'CompilarCargosQr', sgd.ROOT]
    if muestras is not None:
        args.append(muestras)
    sgd.run(args, env=sgd.java_env(), log='compilar-plantillas-cargos-qr.log')

def actualizar():
    proyecto = sgd.ROOT / 'fuentes/Sgd/tramitedoc'
    destino = sgd.ROOT / 'binarios/sgd.war'
    if destino.exists():
        shutil.copy2(destino, sgd.LOG / 'sgd-cargos-qr-anterior.war')
    print('Compilando SGD y verificando los enlaces QR...', flush=True)
    sgd.maven(['clean', 'install'], proyecto, 'compilar-sgd-qr.log')
    compilar_plantillas(sgd.LOG / 'cargos-qr/muestras')
    sgd.maven(['package', '-DskipTests'], proyecto / 'web', 'empaquetar-sgd-qr.log')
    shutil.copy2(proyecto / 'web/target/tramitedoc-web-1.0.war', destino)
    properties = sgd.DOMAIN / 'sgdproperties/application.properties'
    contenido = properties.read_text(encoding='utf-8')
    url = sgd.C.get('url_consulta_qr') or URL_DEFAULT
    if re.search(r'^url_consulta_qr\s*=', contenido, re.M):
        contenido = re.sub(r'^url_consulta_qr\s*=.*$', lambda m: 'url_consulta_qr=' + url, contenido, flags=re.M)
    else:
        contenido += '\nurl_consulta_qr=' + url + '\n'
    shutil.copy2(properties, sgd.LOG / 'sgd-properties-cargos-qr-anterior.properties')
    properties.write_text(contenido, encoding='utf-8')
    configurar_logo_sgd(sgd.DOMAIN)
    estado = sgd.admin(['list-domains'], check=False).stdout
    if not any(line.strip().startswith('sgd running') for line in estado.splitlines()):
        sgd.admin(['start-domain', 'sgd'], log='payara-iniciar-cargos-qr.log')
    sgd.admin(['deploy', '--force=true', '--name', 'sgd', '--contextroot', 'sisdoc', destino],
              log='payara-desplegar-cargos-qr.log')
    huella = hashlib.sha256()
    origen = proyecto.parent
    for archivo in sorted(origen.rglob('*')):
        relativo = archivo.relative_to(origen)
        if archivo.is_file() and 'target' not in relativo.parts:
            huella.update(relativo.as_posix().encode()); huella.update(archivo.read_bytes())
    registro = {'modulo': 'sgd', 'origen': 'fuentes/Sgd/tramitedoc', 'raiz_hash': 'fuentes/Sgd',
                'fuentes_sha256': huella.hexdigest(), 'war_sha256': hashlib.sha256(destino.read_bytes()).hexdigest()}
    manifiesto = sgd.LOG / 'compilacion.json'
    entradas = json.loads(manifiesto.read_text(encoding='utf-8')) if manifiesto.exists() else []
    sgd.save(manifiesto, [item for item in entradas if item.get('modulo') != 'sgd'] + [registro])
    actualizar_consulta()
    print('Cargos QR actualizados. Destino: ' + url, flush=True)

if __name__ == '__main__':
    try:
        actualizar()
    except Exception as error:
        print('ERROR: ' + sgd.redact(str(error)), file=sys.stderr)
        sys.exit(1)
