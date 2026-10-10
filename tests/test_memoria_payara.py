"""Pruebas offline sobre XML y dominios temporales. No consultar PostgreSQL."""
from pathlib import Path
import ast
import subprocess
import sys
import tempfile
import unittest
from unittest.mock import patch
import xml.etree.ElementTree as ET

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'herramientas'))
import memoria_payara as memory
import configuracion_payara as config

XML = (b'<?xml version="1.0"?><domain><configs>\r\n'
       b'<config name="server-config"><java-config>'
       b'<jvm-options>-Xms256m</jvm-options><jvm-options> -Xmx1024m </jvm-options>'
       b'<jvm-options>-Duser.timezone=America/Lima</jvm-options>'
       b'</java-config><jdbc value="ficticio"/></config>\r\n'
       b'<config name="default-config"><java-config>'
       b'<jvm-options>-Xmx512m</jvm-options></java-config></config>'
       b'</configs></domain>')


class MemoriaPayaraTests(unittest.TestCase):
    def test_cli_con_python_portable_aislado(self):
        tool = Path(memory.__file__).resolve()
        result = subprocess.run([sys.executable, '-I', str(tool), '--help'],
                                capture_output=True, text=True, timeout=15)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('preparar', result.stdout)

    def test_importacion_instalador_con_python_portable_sin_acciones(self):
        installer = Path(__file__).resolve().parents[1] / 'sgd.py'
        result = subprocess.run([sys.executable, '-I', str(installer), '--help'],
                                capture_output=True, text=True, timeout=15)
        self.assertEqual(result.returncode, 0, result.stderr)
        self.assertIn('--equipo', result.stdout)

    def test_solo_cambia_heap_server_byte_a_byte(self):
        changed = memory.cambiar_xml(XML, 2048)
        self.assertEqual(changed, XML.replace(b'-Xmx1024m', b'-Xmx2048m'))
        self.assertEqual(memory.cambiar_xml(changed, 1024), XML)

    def test_4096_preserva_xml_y_permite_revertir_a_ambos_valores(self):
        changed = memory.cambiar_xml(XML, 4096)
        self.assertEqual(changed, XML.replace(b'-Xmx1024m', b'-Xmx4096m'))
        self.assertEqual(memory.cambiar_xml(changed, 2048),
                         XML.replace(b'-Xmx1024m', b'-Xmx2048m'))
        self.assertEqual(memory.cambiar_xml(changed, 1024), XML)

    def test_4096_preparar_y_auxiliar_instalador(self):
        memory.preparar(4096, self.snapshot)
        manifest = config.verify_config(self.snapshot)
        self.assertEqual(manifest['ajuste_heap_preparado']['heap_mb'], 4096)
        self.assertIn('-Xmx4096m', [option.strip() for option in manifest['opciones_jvm_server']])
        jvm = ET.fromstring('<java-config><jvm-options>-Xmx2048m</jvm-options>'
                            '<jvm-options>-Xms256m</jvm-options></java-config>')
        memory.configurar_heap(jvm, 4096)
        before = ET.tostring(jvm)
        memory.configurar_heap(jvm)
        self.assertEqual(ET.tostring(jvm), before)
        self.assertEqual([node.text for node in jvm], ['-Xmx4096m', '-Xms256m'])

    def test_unidades_existentes_y_orden_atributos(self):
        source = XML.replace(b'-Xmx1024m', b'-Xmx1g').replace(
            b'<config name="server-config">', b"<config other='si' name='server-config'>")
        self.assertEqual(memory.cambiar_xml(source, 2048),
                         source.replace(b'-Xmx1g', b'-Xmx2048m'))

    def test_rechaza_xml_ambiguo_o_sin_heap(self):
        for source in (XML.replace(b'-Xmx1024m', b'-Xms1024m'),
                       XML.replace(b'-Xms256m', b'-Xmx256m'),
                       XML.replace(b'server-config', b'otro-config'),
                       XML.replace(b'-Xmx1024m', b'-XmxINVALIDO'),
                       b'<domain>'):
            with self.subTest(source=source), self.assertRaises((ValueError, ET.ParseError)):
                memory.cambiar_xml(source, 2048)

    def test_rechaza_valores_ajenos_al_alcance(self):
        for mb in (512, 8192, True, '2048', None):
            with self.subTest(mb=mb), self.assertRaises(ValueError):
                memory.cambiar_xml(XML, mb)

    def test_instalador_preserva_eleccion_existente(self):
        for heap in ('-Xmx1024m', '-Xmx2048m', '-Xmx4096m', '-Xmx2g', '-Xmx4g'):
            jvm = ET.fromstring('<java-config><jvm-options>' + heap +
                                '</jvm-options><jvm-options>-Xms256m</jvm-options></java-config>')
            before = ET.tostring(jvm)
            memory.configurar_heap(jvm)
            self.assertEqual(ET.tostring(jvm), before)

    def test_instalador_permite_eleccion_y_reversion(self):
        jvm = ET.fromstring('<java-config><jvm-options>-Xmx2048m</jvm-options>'
                            '<jvm-options>-Xms256m</jvm-options></java-config>')
        memory.configurar_heap(jvm, 1024)
        self.assertEqual([node.text for node in jvm], ['-Xmx1024m', '-Xms256m'])
        memory.configurar_heap(jvm, 2048)
        self.assertEqual([node.text for node in jvm], ['-Xmx2048m', '-Xms256m'])

    def test_instalador_sin_heap_utiliza_1024_o_eleccion(self):
        for choice, expected in ((None, '-Xmx1024m'), (2048, '-Xmx2048m')):
            jvm = ET.fromstring('<java-config/>')
            memory.configurar_heap(jvm, choice)
            self.assertEqual(jvm.find('jvm-options').text, expected)

    def test_instalador_rechaza_duplicados_y_valores_invalidos(self):
        jvm = ET.fromstring('<java-config><jvm-options>-Xmx1g</jvm-options>'
                            '<jvm-options>-Xmx2g</jvm-options></java-config>')
        with self.assertRaises(ValueError):
            memory.configurar_heap(jvm)
        jvm = ET.fromstring('<java-config/>')
        with self.assertRaises(ValueError):
            memory.configurar_heap(jvm, '2048')
        self.assertEqual(len(jvm), 0)

    def test_configurar_del_instalador_usa_auxiliar_sin_ejecutarlo(self):
        source = (Path(__file__).resolve().parents[1] / 'sgd.py').read_text(encoding='utf-8')
        function = next(node for node in ast.parse(source).body
                        if isinstance(node, ast.FunctionDef) and node.name == 'configurar')
        calls = [node for node in ast.walk(function) if isinstance(node, ast.Call)
                 and isinstance(node.func, ast.Name) and node.func.id == 'configurar_heap']
        self.assertEqual(len(calls), 1)
        self.assertEqual(ast.unparse(calls[0]), "configurar_heap(jvm, C.get('heap_payara_mb'))")

    def setUp(self):
        temporary = tempfile.TemporaryDirectory()
        self.addCleanup(temporary.cleanup)
        self.root = Path(temporary.name)
        self.domain = self.root / 'glassfish/domains/sgd'
        (self.domain / 'config').mkdir(parents=True)
        (self.domain / 'config/domain.xml').write_bytes(XML)
        (self.domain / 'config/local-password').write_bytes(b'ficticio\r\n')
        (self.domain.parent.parent / 'lib/client').mkdir(parents=True)
        (self.domain.parent.parent / 'lib/client/appserver-cli.jar').write_bytes(b'QA')
        self.snapshot = self.root / 'snapshot'
        with patch.object(config, 'ROOT', self.root):
            config.export_config(self.domain, self.snapshot)
        patcher = patch.object(memory, 'ROOT', self.root)
        patcher.start()
        self.addCleanup(patcher.stop)

    def test_preparar_verifica_hash_y_respalda_sin_tocar_dominio(self):
        backup = memory.preparar(2048, self.snapshot)
        manifest = config.verify_config(self.snapshot)
        self.assertEqual((backup / 'domain.xml').read_bytes(), XML)
        self.assertEqual((self.domain / 'config/domain.xml').read_bytes(), XML)
        self.assertEqual((self.snapshot / 'dominio/config/local-password').read_bytes(), b'ficticio\r\n')
        self.assertIn('-Xmx2048m', [option.strip() for option in manifest['opciones_jvm_server']])
        self.assertFalse(manifest['ajuste_heap_preparado']['activacion_verificada'])

    def test_preparar_rechaza_copia_alterada_sin_respaldo(self):
        (self.snapshot / 'dominio/config/local-password').write_bytes(b'alterado')
        with self.assertRaises(ValueError):
            memory.preparar(2048, self.snapshot)
        self.assertFalse((self.root / 'datos').exists())

    def test_preparar_restaura_si_falla_la_escritura(self):
        original_manifest = (self.snapshot / 'MANIFIESTO.json').read_bytes()
        original_write = memory.escribir_atomico
        def fail_manifest(path, data):
            if path.name == 'MANIFIESTO.json':
                raise OSError('Fallo simulado')
            original_write(path, data)
        with patch.object(memory, 'escribir_atomico', side_effect=fail_manifest):
            with self.assertRaises(OSError):
                memory.preparar(2048, self.snapshot)
        self.assertEqual((self.snapshot / 'dominio/config/domain.xml').read_bytes(), XML)
        self.assertEqual((self.snapshot / 'MANIFIESTO.json').read_bytes(), original_manifest)
        config.verify_config(self.snapshot)

    def test_aplicar_rechaza_dominio_activo_sin_escribir(self):
        with patch.object(memory, 'comprobar_detenido', side_effect=ValueError('Activo')):
            with self.assertRaises(ValueError):
                memory.aplicar(2048, self.domain)
        self.assertEqual((self.domain / 'config/domain.xml').read_bytes(), XML)
        self.assertFalse((self.root / 'datos').exists())

    def test_aplicar_detenido_solo_heap_con_respaldo_y_reversion(self):
        with patch.object(memory, 'comprobar_detenido') as stopped:
            backup = memory.aplicar(2048, self.domain)
            self.assertEqual(stopped.call_count, 2)
            self.assertEqual((backup / 'domain.xml').read_bytes(), XML)
            self.assertEqual((self.domain / 'config/domain.xml').read_bytes(),
                             XML.replace(b'-Xmx1024m', b'-Xmx2048m'))
            memory.aplicar(1024, self.domain)
        self.assertEqual((self.domain / 'config/domain.xml').read_bytes(), XML)

    def test_pid_o_proceso_rechaza_aplicacion(self):
        (self.domain / 'config/pid').write_text('999')
        result = type('Result', (), {'stdout': ''})()
        with patch.object(memory.os, 'name', 'nt'), patch.object(memory.subprocess, 'run', return_value=result):
            with self.assertRaisesRegex(ValueError, 'config/pid'):
                memory.comprobar_detenido(self.domain)
            result.stdout = '999\n'
            with self.assertRaisesRegex(ValueError, 'ejecucion'):
                memory.comprobar_detenido(self.domain)


if __name__ == '__main__':
    unittest.main()
