"""Regresion offline de rutas: no abre programas ni conecta a PostgreSQL."""
import contextlib
import io
import json
from pathlib import Path, PurePosixPath, PureWindowsPath
import re
import sys
import tempfile
from types import SimpleNamespace
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import gestion_data as g
import sgd


class RutasDocumentosTests(unittest.TestCase):
    def setUp(self):
        self.area = json.loads((g.ROOT / 'bd/piloto-area.json.ejemplo').read_text(encoding='utf-8'))
        self.area['titular_empleado'] = '52001'
        self.users = [dict(zip(g.FIELDS, [str(i), emp, login, nombre, 'Perez', 'Garcia',
                     '12345678', 'qa@ejemplo.invalid', '1001', 'ANALISTA',
                     'USUARIO_AREA', 'SI', '1']))
                     for i, emp, login, nombre in [(1, '52001', 'qa.uno', 'Uno'),
                                                   (2, '52002', 'qa.dos', 'Dos')]]

    def configs(self, data):
        with patch.object(g, 'DATA', data):
            sql = g.load_sql(self.area, self.users, 'huella-offline')
        calls = re.findall(
            r"PERFORM pg_temp.registrar\('[^']*','tdtx_config_emp','((?:[^']|'')*)'::jsonb,'((?:[^']|'')*)'::jsonb\);",
            sql)
        return [json.loads(values.replace("''", "'")) for _, values in calls]

    def test_carga_windows_todos_los_usuarios_y_tres_directorios(self):
        for data in [PureWindowsPath('C:/SGD/datos'),
                     PureWindowsPath("D:/Carpeta con espacios/O'Prueba/datos"),
                     PureWindowsPath('//servidor/compartido/datos')]:
            with self.subTest(data=str(data)):
                rows = self.configs(data)
                self.assertEqual({row['co_emp'] for row in rows}, {'52001', '52002'})
                for row in rows:
                    for field in ['de_dir_emi', 'de_dir_rec', 'de_dir_ane']:
                        self.assertEqual(row[field], str(data / 'documentos'))
                        self.assertNotIn('/', row[field])

    def test_carga_linux_conserva_ruta_linux(self):
        data = PurePosixPath('/opt/sgd/datos')
        for row in self.configs(data):
            for field in ['de_dir_emi', 'de_dir_rec', 'de_dir_ane']:
                self.assertEqual(row[field], '/opt/sgd/datos/documentos')

    def test_instalacion_sql_preserva_barras_espacios_y_apostrofe(self):
        with tempfile.TemporaryDirectory(prefix="sgd O'Prueba ") as folder:
            root = Path(folder)
            (root / 'bd').mkdir()
            (root / 'datos').mkdir()
            (root / 'bd/01-estructura-catalogos.sql').write_text(
                'SET standard_conforming_strings=on;\n', encoding='utf-8')
            (root / 'bd/02-inia-usuarios-ficticios.sql').write_text(
                "SELECT :'ruta', :'ruta', :'ruta', '2026';", encoding='utf-8')
            with patch.object(sgd, 'ROOT', root), patch.object(sgd, 'DATA', root / 'datos'), \
                 patch.object(sgd, 'credentials', return_value={'usuarios': {}, 'aplicacion': 'QA'}), \
                 patch.object(sgd, 'sql_plantillas', return_value=''), \
                 patch.object(sgd, 'psql') as execute, contextlib.redirect_stdout(io.StringIO()):
                sql = sgd.generar_sql()
            expected = sgd.quote(str(root / 'datos/documentos'))
            self.assertIn('SELECT ' + ', '.join([expected] * 3), sql)
            self.assertNotIn(":'ruta'", sql)
            self.assertNotIn('\\set ruta', sql)
            execute.assert_not_called()

    def test_configuracion_intercepta_sql_con_directorio_nativo(self):
        with tempfile.TemporaryDirectory() as folder:
            root = Path(folder)
            domain = root / 'dominio'
            (domain / 'config').mkdir(parents=True)
            (domain / 'config/domain.xml').write_text(
                '<domain><config name="server-config"><java-config/></config></domain>',
                encoding='utf-8')
            data = root / 'datos'
            data.mkdir()
            with patch.object(sgd, 'DOMAIN', domain), patch.object(sgd, 'DATA', data), \
                 patch.object(sgd, 'domain_properties'), patch.object(sgd, 'iniciar'), \
                 patch.object(sgd, 'admin', return_value=SimpleNamespace(stdout='SGDPostgreSQL')), \
                 patch.object(sgd, 'JNDI', []), patch.object(sgd, 'jdbc_sql') as execute, \
                 contextlib.redirect_stdout(io.StringIO()):
                sgd.configurar()
            sql = execute.call_args.args[0]
            self.assertEqual(sql.count(sgd.quote(str(data / 'documentos'))), 3)

    def test_restauracion_intercepta_sql_con_directorio_nativo(self):
        with tempfile.TemporaryDirectory() as folder:
            data = Path(folder)
            (data / 'respaldo-portable.dump').write_bytes(b'QA')
            with patch.object(sgd, 'DATA', data), \
                 patch.object(sgd, 'scalar', side_effect=['f', 't']), \
                 patch.object(sgd, 'credentials', return_value={'aplicacion': 'QA'}), \
                 patch.object(sgd, 'pg_tool'), patch.object(sgd, 'connection', return_value=[]), \
                 patch.object(sgd, 'psql') as execute, contextlib.redirect_stdout(io.StringIO()):
                sgd.restaurar()
            self.assertEqual(execute.call_args.args[0].count(
                sgd.quote(str(data / 'documentos'))), 3)
            self.assertTrue(execute.call_args.kwargs['transaction'])


if __name__ == '__main__':
    unittest.main()
