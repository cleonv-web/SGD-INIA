"""Primera instalación en una carpeta nueva; no ejecuta ni consulta servidores."""
import json
from pathlib import Path
import sys
import tempfile
import unittest

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from herramientas.configuracion_instalacion import cargar_configuracion


class ConfiguracionInstalacionTests(unittest.TestCase):
    def test_crea_ejemplo_sin_necesitar_datos_del_piloto(self):
        with tempfile.TemporaryDirectory() as carpeta:
            raiz = Path(carpeta)
            ejemplo = b'{"puerto_bd":55432,"heap_payara_mb":4096}'
            (raiz/'configuracion.ejemplo.json').write_bytes(ejemplo)
            resultado = cargar_configuracion(raiz, crear=True)
            self.assertEqual(resultado['heap_payara_mb'], 4096)
            self.assertEqual((raiz/'configuracion.json').read_bytes(), ejemplo)

    def test_conserva_configuracion_existente(self):
        with tempfile.TemporaryDirectory() as carpeta:
            raiz = Path(carpeta)
            existente = b'{"base":"ambiente_existente","puerto_bd":55555}'
            (raiz/'configuracion.json').write_bytes(existente)
            (raiz/'configuracion.ejemplo.json').write_text('{}')
            self.assertEqual(cargar_configuracion(raiz, crear=True), json.loads(existente))
            self.assertEqual((raiz/'configuracion.json').read_bytes(), existente)

    def test_no_crea_configuracion_al_actualizar(self):
        with tempfile.TemporaryDirectory() as carpeta:
            raiz = Path(carpeta)
            (raiz/'configuracion.ejemplo.json').write_text('{}')
            with self.assertRaisesRegex(RuntimeError, 'Instalar-SGD'):
                cargar_configuracion(raiz)
            self.assertFalse((raiz/'configuracion.json').exists())


if __name__ == '__main__':
    unittest.main()
