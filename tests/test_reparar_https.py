"""Reparacion SSL sobre archivos temporales; nunca detiene el SGD real."""
from pathlib import Path
import os
import subprocess
import sys
import tempfile
from types import ModuleType, SimpleNamespace
import unittest
import xml.etree.ElementTree as ET

ROOT = Path(__file__).resolve().parents[1]
sys.path.insert(0, str(ROOT))
import sgd

repair = ModuleType('repair_https_test')
repair.__file__ = str(ROOT/'Reparar-HTTPS.bat')
source = (ROOT/'Reparar-HTTPS.bat').read_text(encoding='utf8').split('# INICIO_PYTHON\n', 1)[1]
exec(compile(source, repair.__file__, 'exec'), repair.__dict__)

XML = b'''<domain><system-property name="HTTP_SSL_LISTENER_PORT" value="443"/>
<configs><config name="server-config"><java-config>
<jvm-options>-Djavax.net.ssl.keyStore=${com.sun.aas.instanceRoot}/config/keystore.jks</jvm-options>
</java-config><network-config><protocols>
<protocol name="http-listener-1"><http default-virtual-server="server"/></protocol>
<protocol name="http-listener-2" security-enabled="true"><ssl cert-nickname="s1as" classname="SSL"/></protocol>
</protocols><network-listeners>
<network-listener name="http-listener-2" port="${HTTP_SSL_LISTENER_PORT}" protocol="http-listener-2"/>
</network-listeners></network-config></config></configs><untouched value="conservar"/></domain>'''


class RepararHttpsTests(unittest.TestCase):
    def certificate(self, **changes):
        value = dict(alias='sgd-inia', dns=['*.inia.gob.pe'], private_key=True, start=10, end=100)
        value.update(changes)
        return value

    def test_hostname_wildcard_solo_un_nivel(self):
        self.assertTrue(repair.hostname_matches('sgd.inia.gob.pe', '*.inia.gob.pe'))
        self.assertFalse(repair.hostname_matches('a.sgd.inia.gob.pe', '*.inia.gob.pe'))
        self.assertFalse(repair.hostname_matches('inia.gob.pe', '*.inia.gob.pe'))
        self.assertFalse(repair.hostname_matches('evil-inia.gob.pe', '*.inia.gob.pe'))

    def test_certificado_invalido_no_se_selecciona(self):
        for changes in [dict(start=60), dict(end=40), dict(private_key=False), dict(dns=['DEMO-SGD'])]:
            with self.subTest(changes=changes), self.assertRaises(RuntimeError):
                repair.select_certificate([self.certificate(**changes)], repair.HOST, now=50)

    def test_seleccion_no_depende_del_alias_original(self):
        cert = self.certificate(alias='*.inia.gob.pe_2025')
        self.assertEqual(repair.select_certificate([cert], repair.HOST, now=50), cert)
        with self.assertRaises(RuntimeError):
            repair.select_certificate([cert, self.certificate(alias='otro')], repair.HOST, now=50)

    def test_xml_solo_cambia_cert_nickname_y_se_puede_repetir(self):
        changed, old = repair.patch_domain(XML, 'sgd-inia')
        self.assertEqual(old, 's1as')
        tree = ET.fromstring(changed)
        node = tree.find('.//ssl')
        self.assertEqual(node.get('cert-nickname'), 'sgd-inia')
        node.set('cert-nickname', 's1as')
        self.assertEqual(ET.tostring(tree), ET.tostring(ET.fromstring(XML)))
        self.assertEqual(repair.patch_domain(changed, 'sgd-inia')[0], changed)

    def test_otro_puerto_o_keystore_no_se_modifica(self):
        for xml in [XML.replace(b'value="443"', b'value="8185"'),
                    XML.replace(b'<ssl cert-', b'<ssl key-store="otro.jks" cert-'),
                    XML.replace(b'security-enabled="true"', b'security-enabled="false"')]:
            with self.subTest(xml=xml), self.assertRaises(RuntimeError):
                repair.patch_domain(xml, 'sgd-inia')

    def test_arranque_fallido_recupera_xml_original(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp)/'domains/sgd/config/domain.xml'
            path.parent.mkdir(parents=True)
            path.write_bytes(XML)
            backup = Path(temp)/'backup.xml'; backup.write_bytes(XML)
            calls = []
            def admin(args, **kwargs):
                calls.append(args[0])
                if calls == ['stop-domain', 'start-domain']:
                    raise RuntimeError('arranque simulado fallido')
                return SimpleNamespace(returncode=0, stdout='sgd not running')
            runtime = SimpleNamespace(admin=admin)
            with self.assertRaisesRegex(RuntimeError, 'arranque simulado'):
                repair.apply_domain(runtime, path, repair.patch_domain(XML, 'sgd-inia')[0], backup, 'abc', repair.HOST)
            self.assertEqual(path.read_bytes(), XML)
            self.assertEqual(calls, ['stop-domain', 'start-domain', 'stop-domain', 'list-domains', 'start-domain'])

    def test_huella_distinta_recupera_configuracion(self):
        with tempfile.TemporaryDirectory() as temp:
            path = Path(temp)/'domains/sgd/config/domain.xml'
            path.parent.mkdir(parents=True); path.write_bytes(XML)
            backup = Path(temp)/'backup.xml'; backup.write_bytes(XML)
            runtime = SimpleNamespace(admin=lambda *a, **k: SimpleNamespace(returncode=0, stdout='sgd not running'))
            with self.assertRaisesRegex(RuntimeError, 'otro certificado'):
                repair.apply_domain(runtime, path, repair.patch_domain(XML, 'sgd-inia')[0], backup, 'abc', repair.HOST,
                                    tls_probe=lambda *a: 'otra-huella')
            self.assertEqual(path.read_bytes(), XML)

    def test_inspector_java_real_con_almacen_temporal(self):
        with tempfile.TemporaryDirectory() as temp:
            root = Path(temp); store = root/'prueba.jks'
            java = sgd.java_home()/'bin'; keytool = java/('keytool.exe' if os.name=='nt' else 'keytool')
            pwd = 'SoloPruebaTemporal!17'
            r = subprocess.run([str(keytool), '-genkeypair', '-alias', 'sgd-inia', '-keystore', str(store),
                                '-storetype', 'JKS', '-storepass', pwd, '-keypass', pwd, '-keyalg', 'RSA',
                                '-keysize', '2048', '-validity', '2', '-dname', 'CN=*.inia.gob.pe',
                                '-ext', 'SAN=DNS:*.inia.gob.pe,DNS:inia.gob.pe'], capture_output=True)
            self.assertEqual(r.returncode, 0, r.stderr.decode(errors='replace'))
            entries = repair.inspect_keystore(sgd, store, root, pwd)
            self.assertEqual(len(entries), 1)
            cert = repair.select_certificate(entries, repair.HOST)
            self.assertTrue(cert['private_key'])
            self.assertEqual(len(cert['sha256']), 64)
            with self.assertRaisesRegex(RuntimeError, 'clave maestra'):
                repair.inspect_keystore(sgd, store, root, 'incorrecta')

    @unittest.skipUnless(os.name == 'nt', 'Lanzador exclusivo de Windows')
    def test_bat_real_bloquea_instalacion_ajena_sin_tocar_servicios(self):
        with tempfile.TemporaryDirectory(prefix='sgd-ssl-lanzador-') as temp:
            root = Path(temp)
            original = (ROOT/'Reparar-HTTPS.bat').read_text(encoding='utf8')
            python = str(ROOT/'herramientas/python-windows/python.exe')
            original = original.replace('%~dp0herramientas\\python-windows\\python.exe', python)
            (root/'Reparar-HTTPS.bat').write_text(original, encoding='utf8')
            (root/'sgd.py').write_text("C={'url_publica':'http://127.0.0.1:8085','puerto_https':8185}\n", encoding='utf8')
            (root/'gestion_data.py').write_text('def private_folder(path): raise AssertionError("No tocar archivos")\n', encoding='utf8')
            r = subprocess.run(['cmd', '/d', '/c', 'Reparar-HTTPS.bat'], cwd=root,
                               stdin=subprocess.DEVNULL, capture_output=True, timeout=20)
            self.assertEqual(r.returncode, 1)
            self.assertIn('url_publica=https://sgd.inia.gob.pe', r.stderr.decode('utf8', errors='replace'))
            self.assertFalse((root/'logs').exists())


if __name__ == '__main__':
    unittest.main()
