import tempfile
import base64
import unittest
import sys
from pathlib import Path
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
from herramientas.configuracion_mpd import configurar_mpd, configurar_logo_sgd


class ConfiguracionMpdTest(unittest.TestCase):
    def test_logo_sgd_conserva_configuracion_y_es_idempotente(self):
        with tempfile.TemporaryDirectory() as temp:
            domain = Path(temp)
            (domain / 'sgdproperties').mkdir()
            archivo = domain / 'sgdproperties/application.properties'
            archivo.write_text('B64_LOGO_REPORTE=logo_anterior\nurl_consulta_qr=https://calidad.inia.gob.pe/consulta/inicio.do\nAD_HOST=conservar\n', encoding='utf-8')
            configurar_logo_sgd(domain)
            first = archivo.read_text(encoding='utf-8')
            configurar_logo_sgd(domain)
            self.assertEqual(first, archivo.read_text(encoding='utf-8'))
            self.assertIn('AD_HOST=conservar\n', first)
            self.assertIn('url_consulta_qr=https://calidad.inia.gob.pe/consulta/inicio.do\n', first)
            encoded = next(line.split('=', 1)[1] for line in first.splitlines() if line.startswith('B64_LOGO_REPORTE='))
            logo = Path(__file__).resolve().parents[1] / 'fuentes/MPVCiudadano/web/src/main/resources/META-INF/web-resources/images/inia-logo.png'
            self.assertEqual(logo.read_bytes(), base64.b64decode(encoded))

    def test_enlaces_publicos_portables_sin_cambiar_otras_propiedades(self):
        with tempfile.TemporaryDirectory() as temp:
            domain = Path(temp)
            (domain / 'mpvproperties').mkdir()
            archivo = domain / 'mpvproperties/application.properties'
            archivo.write_text('MESA_PARTES_CIUDADANO=https://localhost:8181/mpd/\nCONSULTA_TU_TRAMITE=http://localhost:8181/consulta/inicio.do\nFILE_SIZE_MAX=52428800\nclave_prueba=conservar\n', encoding='utf-8')
            configurar_mpd(domain, 'https://sgd.inia.test:8443/')
            first = archivo.read_text(encoding='utf-8')
            configurar_mpd(domain, 'https://sgd.inia.test:8443/')
            self.assertEqual(first, archivo.read_text(encoding='utf-8'))
            self.assertIn('MESA_PARTES_CIUDADANO=https://sgd.inia.test:8443/mpd\n', first)
            self.assertIn('CONSULTA_TU_TRAMITE=https://sgd.inia.test:8443/consulta/inicio.do\n', first)
            self.assertIn('clave_prueba=conservar', first)
            self.assertIn('FILE_SIZE_MAX=52428800', first)
            self.assertIn(r'Innovaci\u00f3n Agraria', first)
            self.assertNotIn('localhost', first)
            encoded = next(line.split('=', 1)[1] for line in first.splitlines() if line.startswith('B64_LOGO_REPORTE='))
            logo = Path(__file__).resolve().parents[1] / 'fuentes/MPVCiudadano/web/src/main/resources/META-INF/web-resources/images/inia-logo.png'
            self.assertEqual(logo.read_bytes(), base64.b64decode(encoded))

if __name__ == '__main__':
    unittest.main()
