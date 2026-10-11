"""Instala en una copia aislada de los archivos seleccionados por Git.

Usa puertos propios y deja las evidencias en output. Nunca usa la configuración,
base, documentos o dominio de la instalación original. No elimina archivos.
"""
from pathlib import Path
import json
import os
import random
import shutil
import socket
import subprocess
import sys
import time

ROOT = Path(__file__).resolve().parents[1]


def main():
    if os.name != 'nt':
        raise RuntimeError('Esta comprobación de instalación corresponde a Windows x64.')
    listado = subprocess.run(['git', '-c', 'core.quotepath=false', 'ls-files', '-z',
        '--cached', '--others', '--exclude-standard'], cwd=ROOT, capture_output=True, check=True)
    archivos = sorted(set(listado.stdout.decode('utf-8').rstrip('\0').split('\0')))
    qa = ROOT/'output'/('clon-'+time.strftime('%H%M%S'))
    qa.mkdir(parents=True, exist_ok=False)
    total = 0
    for nombre in archivos:
        fuente = ROOT/nombre
        if not fuente.is_file():
            raise RuntimeError('Archivo seleccionado no disponible: '+nombre)
        destino = qa/nombre
        if not destino.resolve().is_relative_to(qa.resolve()):
            raise RuntimeError('Ruta fuera del clon aislado.')
        if fuente.stat().st_size >= 100*1024*1024:
            raise RuntimeError('Archivo individual demasiado grande para Git: '+nombre)
        destino.parent.mkdir(parents=True, exist_ok=True)
        shutil.copy2(fuente, destino)
        total += fuente.stat().st_size
    for nombre in ['configuracion.json', 'datos/credenciales.json', 'datos/postgresql',
                   'datos/documentos', 'payara/payara5/glassfish/domains/sgd', 'binarios/sgd.war']:
        if (qa/nombre).exists():
            raise RuntimeError('El clon incluyó actividad privada: '+nombre)
    python = qa/'herramientas/python-windows/python.exe'
    print(f'Copia Git aislada: {len(archivos)} archivos; {total/1024**2:.2f} MiB.', flush=True)
    subprocess.run([python, qa/'sgd.py', 'instalar', '--help'], cwd=qa,
                   stdout=subprocess.DEVNULL, check=True)
    cfg = json.loads((qa/'configuracion.json').read_text(encoding='utf-8'))
    for _ in range(100):
        inicio = random.randrange(42000, 47500, 10)
        puertos = [inicio, inicio+1, inicio+2, inicio+3, *range(inicio+1001, inicio+1006)]
        abiertos = []
        try:
            for puerto in puertos:
                conexion = socket.socket()
                abiertos.append(conexion)
                conexion.bind(('127.0.0.1', puerto))
            break
        except OSError:
            continue
        finally:
            for conexion in abiertos:
                conexion.close()
    else:
        raise RuntimeError('No se encontraron puertos libres para la prueba.')
    cfg.update(base='idosgd_clon_qa', puerto_bd=inicio, puerto_http=inicio+1,
               puerto_admin=inicio+2, puerto_https=inicio+3, direccion_http='127.0.0.1',
               url_publica=f'http://127.0.0.1:{inicio+1}')
    (qa/'configuracion.json').write_text(json.dumps(cfg, indent=2), encoding='utf-8')
    resultado = {'copia': str(qa), 'archivos': len(archivos), 'mib': round(total/1024**2, 2),
                 'puertos_qa': puertos, 'base_original_modificada': False}
    codigo = 1
    try:
        proceso = subprocess.run([python, qa/'sgd.py', 'instalar', '--equipo', 'servidor'], cwd=qa)
        codigo = proceso.returncode
        resultado['instalacion_completa_aprobada'] = codigo == 0
        if codigo == 0:
            consulta = ('import sys;sys.path.insert(0,".");import sgd,json;'
                        'print(json.dumps({"documentos":sgd.scalar("SELECT count(*) FROM idosgd.tdtv_remitos"),'
                        '"usuarios":sgd.scalar("SELECT count(*) FROM idosgd.seg_usuarios1")}))')
            cuentas = subprocess.run([python, '-c', consulta], cwd=qa, capture_output=True, text=True, check=True)
            resultado['base_nueva'] = json.loads(cuentas.stdout)
            if resultado['base_nueva'] != {'documentos': '0', 'usuarios': '4'}:
                raise RuntimeError('La base nueva no coincide con el catálogo inicial esperado.')
    finally:
        # Solo detener servidores de ESTA copia, nunca los del piloto original.
        parada = subprocess.run([python, qa/'sgd.py', 'detener'], cwd=qa, capture_output=True, text=True)
        resultado['parada_qa_correcta'] = parada.returncode == 0
        (ROOT/'output/resultado-clon-instalacion.json').write_text(
            json.dumps(resultado, indent=2), encoding='utf-8')
        if parada.returncode:
            print('Revisar parada del ambiente QA: '+str(qa/'logs'), file=sys.stderr)
    if codigo:
        raise RuntimeError('Falló la instalación del clon: revisar '+str(qa/'logs/ULTIMO-ERROR.txt'))
    print('Clon verificado: instalación completa, base nueva sin documentos y cuatro cuentas técnicas.', flush=True)


if __name__ == '__main__':
    main()
