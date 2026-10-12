"""Compila y despliega solamente la consulta ciudadana; conserva la base y los demás módulos."""
import hashlib
import json
import shutil
import sys
from pathlib import Path

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd

def actualizar():
    if not (sgd.DOMAIN / 'config/domain.xml').is_file():
        raise RuntimeError('Falta la instalación del servidor. Usar Instalar-SGD primero.')
    fuente = sgd.ROOT / 'fuentes/ConsultaTuTramite'
    print('Compilando y verificando Consulta de Expediente INIA...', flush=True)
    sgd.maven(['clean', 'install'], fuente, 'compilar-consulta-inia.log')
    destino = sgd.ROOT / 'binarios/consulta.war'
    respaldo = sgd.LOG / 'consulta-actualizacion-anterior.war'
    if destino.exists():
        shutil.copy2(destino, respaldo)
    shutil.copy2(fuente / 'web/target/consultadoc-web-1.0.war', destino)
    estado = sgd.admin(['list-domains'], check=False).stdout
    if not any(line.strip().startswith('sgd running') for line in estado.splitlines()):
        sgd.admin(['start-domain', 'sgd'], log='payara-iniciar-consulta.log')
    sgd.admin(['deploy', '--force=true', '--precompilejsp=true', '--name', 'consulta', '--contextroot', 'consulta', destino], log='payara-desplegar-consulta-inia.log')
    huella = hashlib.sha256()
    for archivo in sorted(fuente.rglob('*')):
        relativo = archivo.relative_to(fuente)
        if archivo.is_file() and 'target' not in relativo.parts:
            huella.update(relativo.as_posix().encode())
            huella.update(archivo.read_bytes())
    registro = {'modulo': 'consulta', 'origen': 'fuentes/ConsultaTuTramite', 'raiz_hash': 'fuentes/ConsultaTuTramite',
                'fuentes_sha256': huella.hexdigest(), 'war_sha256': hashlib.sha256(destino.read_bytes()).hexdigest()}
    manifiesto = sgd.LOG / 'compilacion.json'
    entradas = json.loads(manifiesto.read_text(encoding='utf-8')) if manifiesto.exists() else []
    sgd.save(manifiesto, [item for item in entradas if item.get('modulo') != 'consulta'] + [registro])
    print('Consulta actualizada: ' + sgd.C['url_publica'] + '/consulta/inicio.do', flush=True)

if __name__ == '__main__':
    try:
        actualizar()
    except Exception as error:
        print('ERROR: ' + sgd.redact(str(error)), file=sys.stderr)
        sys.exit(1)
