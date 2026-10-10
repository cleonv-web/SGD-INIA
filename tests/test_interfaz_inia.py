"""Contrato de regresión: el rediseño no altera controles ni scripts del SGD.

Compara con la revisión Git anterior al cambio visual; no consulta la BD.
Ejecutar: herramientas/python-windows/python.exe tests/test_interfaz_inia.py -v
"""
import subprocess
import unittest
import xml.etree.ElementTree as ET
from pathlib import Path

ROOT = Path(__file__).resolve().parents[1]
BASE_REF = '802580ab081f9c9d1acc9a9f7b6003cb08aa0550'
WEB = 'fuentes/Sgd/tramitedoc/web/src/main/webapp/WEB-INF/'
FILES = ['templates/default.jspx', 'templates/main.jspx', 'views/login.jspx']
ATTRIBUTES = {
    'id', 'name', 'type', 'value', 'action', 'method', 'href', 'target', 'src',
    'required', 'readonly', 'disabled', 'maxlength', 'tabindex', 'autocomplete',
    'path', 'test', 'expression', 'scope', 'var', 'template', 'page', 'file',
    'modelAttribute', 'data-target', 'data-slide-to', 'data-slide', 'dojotype',
}

def previous(name):
    return subprocess.check_output(['git', 'show', BASE_REF + ':' + WEB + name], cwd=ROOT)

def contract(content):
    tree = ET.fromstring(content)
    result = []
    for node in tree.iter():
        local = node.tag.split('}')[-1]
        # Sólo se añade un recurso visual y un metadato de viewport.
        if local == 'meta' and node.get('name') == 'viewport':
            continue
        if local == 'link' and 'inia-institucional.css' in node.get('href', ''):
            continue
        attrs = {key: value for key, value in node.attrib.items()
                 if key in ATTRIBUTES or key.startswith('on')}
        if attrs:
            result.append((node.tag, attrs))
    return result

class InterfazInia(unittest.TestCase):
    def test_controles_enlaces_y_bindings_conservados(self):
        for name in FILES:
            with self.subTest(plantilla=name):
                self.assertEqual(contract(previous(name)), contract((ROOT / (WEB + name)).read_bytes()))

    def test_scripts_identicos(self):
        for name in FILES:
            def scripts(data):
                return [''.join(node.itertext()) for node in ET.fromstring(data).iter()
                        if node.tag.split('}')[-1] == 'script']
            with self.subTest(plantilla=name):
                self.assertEqual(scripts(previous(name)), scripts((ROOT / (WEB + name)).read_bytes()))

    def test_tema_y_viewport_sin_reemplazar_dependencias(self):
        for name in FILES[:2]:
            root = ET.parse(ROOT / (WEB + name)).getroot()
            metas = [node for node in root.iter() if node.tag.split('}')[-1] == 'meta'
                     and node.get('name') == 'viewport']
            self.assertEqual(len(metas), 1)
            self.assertEqual(metas[0].get('content'), 'width=device-width, initial-scale=1')
            links = [node for node in root.iter() if node.tag.split('}')[-1] == 'link'
                     and 'inia-institucional.css' in node.get('href', '')]
            self.assertEqual(len(links), 1)

if __name__ == '__main__':
    unittest.main()
