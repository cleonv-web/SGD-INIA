"""Configuracion Payara: copias temporales, sin procesos ni conexiones."""
from pathlib import Path
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1] / 'herramientas'))
import configuracion_payara as config


class ConfiguracionPayaraTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.addCleanup(self.temp.cleanup)
        self.root = Path(self.temp.name)
        self.domain = self.root / 'glassfish/domains/sgd'
        (self.domain / 'config').mkdir(parents=True)
        (self.domain / 'config/domain.xml').write_text(
            '<domain><configs><config name="server-config"><java-config>'
            '<jvm-options>-Xmx1024m</jvm-options>'
            '</java-config></config></configs></domain>', encoding='utf-8')
        (self.domain / 'config/local-password').write_bytes(b'clave-ficticia\r\n')
        (self.domain / 'config/pid').write_text('123')
        (self.domain / 'config/domain.xml.bak').write_text('viejo')
        (self.domain / 'logs').mkdir()
        (self.domain / 'logs/server.log').write_text('log')
        (self.domain / 'sgdproperties').mkdir()
        (self.domain / 'sgdproperties/application.properties').write_bytes(b'clave=ficticia\r\n')
        self.snapshot = self.root / 'copia'

    def export(self):
        with patch.object(config, 'ROOT', self.root):
            return config.export_config(self.domain, self.snapshot)

    def test_conserva_bytes_secretos_y_excluye_actividad(self):
        manifest = self.export()
        self.assertTrue(manifest['contiene_secretos_del_dominio'])
        self.assertEqual(manifest['opciones_jvm_server'], ['-Xmx1024m'])
        self.assertEqual((self.snapshot / 'dominio/config/local-password').read_bytes(),
                         b'clave-ficticia\r\n')
        self.assertEqual(set(manifest['archivos']), {
            'config/domain.xml', 'config/local-password',
            'sgdproperties/application.properties'})
        self.assertEqual(config.verify_config(self.snapshot), manifest)

    def test_rechaza_hash_modificado(self):
        self.export()
        (self.snapshot / 'dominio/config/local-password').write_bytes(b'otro')
        with self.assertRaisesRegex(ValueError, 'Hash distinto'):
            config.verify_config(self.snapshot)

    def test_rechaza_archivos_extra(self):
        self.export()
        (self.snapshot / 'dominio/config/pid').write_text('123')
        with self.assertRaisesRegex(ValueError, 'no coinciden'):
            config.verify_config(self.snapshot)

    def test_rutas_no_pueden_salir_del_destino(self):
        for path in ('../clave', '/tmp/clave', 'C:/clave', 'config/../../clave',
                     'config\\clave'):
            with self.subTest(path=path), self.assertRaises(ValueError):
                config.safe_file(self.root, path)

    def test_exportar_no_sobrescribe_copia_revisada(self):
        self.export()
        with self.assertRaisesRegex(ValueError, 'copia ya existe'):
            self.export()

    def test_restauracion_requiere_confirmacion_y_dominio_detenido(self):
        self.export()
        backup = self.root / 'respaldo'
        with self.assertRaisesRegex(ValueError, 'requiere'):
            config.restore_config(self.snapshot, self.domain, backup, None)
        with self.assertRaisesRegex(ValueError, 'config/pid'):
            config.restore_config(self.snapshot, self.domain, backup, 'PAYARA:DOMINIO')
        self.assertFalse(backup.exists())

    def test_restauracion_respalda_y_conserva_archivos_ajenos(self):
        self.export()
        target = self.root / 'otro/glassfish/domains/sgd'
        (target / 'config').mkdir(parents=True)
        (target.parent.parent / 'lib/client').mkdir(parents=True)
        (target.parent.parent / 'lib/client/appserver-cli.jar').write_bytes(b'QA')
        (target / 'config/domain.xml').write_text('<domain/>')
        (target / 'config/custom.conf').write_text('conservar')
        backup = self.root / 'respaldo'
        config.restore_config(self.snapshot, target, backup, 'PAYARA:DOMINIO')
        self.assertEqual((backup / 'config/domain.xml').read_text(), '<domain/>')
        self.assertEqual((target / 'config/custom.conf').read_text(), 'conservar')
        self.assertEqual((target / 'config/domain.xml').read_bytes(),
                         (self.snapshot / 'dominio/config/domain.xml').read_bytes())

    def test_copia_alterada_no_modifica_destino(self):
        self.export()
        (self.snapshot / 'dominio/config/domain.xml').write_text('<otro/>')
        with self.assertRaisesRegex(ValueError, 'Hash distinto'):
            config.restore_config(self.snapshot, self.domain, self.root / 'backup', 'PAYARA:DOMINIO')
        self.assertFalse((self.root / 'backup').exists())


if __name__ == '__main__':
    unittest.main()
