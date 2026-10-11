"""Configuración local: el instalador crea el ejemplo en un clon nuevo."""
import json
from pathlib import Path


def cargar_configuracion(raiz, *, crear=False):
    raiz = Path(raiz)
    destino = raiz / 'configuracion.json'
    if not destino.exists() and crear:
        ejemplo = (raiz / 'configuracion.ejemplo.json').read_bytes()
        json.loads(ejemplo.decode('utf-8'))
        try:
            with destino.open('xb') as archivo:
                archivo.write(ejemplo)
            print('Configuración inicial creada desde configuracion.ejemplo.json.', flush=True)
        except FileExistsError:
            pass  # Otra ejecución ya creó la configuración; conservarla.
    if not destino.exists():
        raise RuntimeError('Falta configuracion.json. Ejecutar Instalar-SGD para una instalación nueva '
                           'o copiar configuracion.ejemplo.json y adaptarlo antes de continuar.')
    return json.loads(destino.read_text(encoding='utf-8'))
