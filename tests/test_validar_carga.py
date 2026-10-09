"""Validación offline, informe sin datos personales y ausencia de carga/conexión."""
from pathlib import Path
import csv
import io
import json
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import gestion_data as carga
import validar_carga as validar


class ValidarCargaTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(prefix='sgd-validar-')
        self.root = Path(self.tmp.name)
        self.folder = self.root / 'entrada con espacios'
        self.folder.mkdir()
        self.area = json.loads((carga.ROOT / 'bd/piloto-area.json.ejemplo').read_text(encoding='utf-8'))
        self.area.update(aprobado='SI', verificado_por='OPERADOR QA', nombre='UTI QA')
        self.area['sede'].update(nombre='SEDE QA', direccion='DIRECCION QA')
        self.user = dict(zip(carga.FIELDS, ['1', '52001', 'qa.uno', 'NOMBRE QA', 'PATERNO QA', 'MATERNO QA',
                                          '12345678', '', '1001', 'ANALISTA QA', 'USUARIO_AREA', 'SI', '1']))
        self.write()
        self.environment = {'destino': {'base': 'qa'}, 'conexion_comprobada': False}

    def tearDown(self):
        self.tmp.cleanup()

    def write(self, users=None):
        (self.folder / 'area.json').write_text(json.dumps(self.area), encoding='utf-8')
        with (self.folder / 'usuarios-verificados.csv').open('w', encoding='utf-8', newline='') as f:
            writer = csv.DictWriter(f, fieldnames=carga.FIELDS)
            writer.writeheader()
            writer.writerows(users or [self.user])

    def report(self):
        with patch.object(validar, 'comprobar_entorno', return_value=self.environment):
            return validar.validar(self.folder)

    def test_exclusion_y_reporte_sin_personas_claves_o_sql(self):
        excluded = {k: '' for k in carga.FIELDS}
        excluded.update(incluir_sgd='0', verificado='NO')
        self.write([self.user, excluded])
        before = {p.name: p.read_bytes() for p in self.folder.iterdir()}
        report = self.report()
        self.assertEqual(report['resultado'], 'VALIDACION CORRECTA')
        self.assertFalse(report['base_consultada'])
        self.assertEqual(report['comprobaciones'][0]['detalle'], {'filas': 2, 'incluidos': 1, 'excluidos': 1})
        self.assertTrue(report['area']['titular_incluido'])
        serialized = json.dumps(report)
        for private in ['qa.uno', '12345678', 'NOMBRE QA', '__CIFRADO_PENDIENTE__', 'INSERT INTO']:
            self.assertNotIn(private, serialized)
        self.assertEqual(before, {p.name: p.read_bytes() for p in self.folder.iterdir()})

    def test_usuario_no_verificado_y_titular_excluido_rechazados(self):
        self.user['verificado'] = 'NO'
        self.write()
        self.assertIn('no verificado', ' '.join(self.report()['errores']))
        self.user['verificado'] = 'SI'
        self.area['titular_empleado'] = '52002'
        self.write()
        self.assertIn('titular_empleado', ' '.join(self.report()['errores']))

    def test_duplicados_y_selector_invalido_rechazados(self):
        other = dict(self.user, origen_id='2', codigo_empleado='52002', dni='87654321', usuario='QA.UNO')
        self.write([self.user, other])
        self.assertIn('duplicado de usuario', ' '.join(self.report()['errores']))
        self.user['incluir_sgd'] = '2'
        self.write()
        self.assertEqual(self.report()['resultado'], 'ERROR')

    def test_incompatibilidad_de_columnas_del_modelo_rechazada(self):
        area, users, fingerprint = carga.read_bundle(self.folder)
        sql = carga.load_sql(area, users, fingerprint)
        (self.root / 'bd').mkdir()
        schema = (carga.ROOT / 'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
        schema = schema.replace('cemp_nu_dni', 'columna_qa_eliminada')
        (self.root / 'bd/01-estructura-catalogos.sql').write_text(schema, encoding='utf-8')
        with self.assertRaisesRegex(ValueError, 'Columnas incompatibles'):
            validar.comprobar_modelo(sql, self.root)

    def fake_environment(self, system='windows'):
        (self.root / 'datos').mkdir(exist_ok=True)
        (self.root / 'configuracion.json').write_text(json.dumps(
            {'modo': 'completo', 'host_bd': '127.0.0.1', 'puerto_bd': 55432, 'base': 'qa'}), encoding='utf-8')
        (self.root / 'datos/credenciales.json').write_text('{"postgres":"SECRETO QA"}', encoding='utf-8')
        paths = ['fuentes/Sgd/libreria/target/libreria-onpe-1.0.jar', 'herramientas/CifrarClave.java']
        if system == 'windows':
            paths += ['java/windows/jdk-qa/bin/java.exe', 'java/windows/jdk-qa/bin/javac.exe',
                      'bd/postgresql/windows/bin/psql.exe', 'bd/postgresql/windows/bin/pg_dump.exe']
        else:
            paths += ['java/linux-x64.tar.gz', 'bd/postgresql-linux-x64.tar.gz']
        for relative in paths:
            target = self.root / relative
            target.parent.mkdir(parents=True, exist_ok=True)
            target.write_bytes(b'QA fixture')

    def test_requisitos_faltantes_y_modo_solo_app_bloquean(self):
        self.fake_environment()
        self.assertNotIn('SECRETO QA', json.dumps(validar.comprobar_entorno(self.root, 'windows')))
        (self.root / 'fuentes/Sgd/libreria/target/libreria-onpe-1.0.jar').unlink()
        with self.assertRaisesRegex(ValueError, 'libreria-onpe'):
            validar.comprobar_entorno(self.root, 'windows')
        cfg = self.root / 'configuracion.json'
        data = json.loads(cfg.read_text())
        data['modo'] = 'solo_payara'
        cfg.write_text(json.dumps(data))
        with self.assertRaisesRegex(ValueError, 'solo_payara'):
            validar.comprobar_entorno(self.root, 'windows')

    def test_linux_archivos_empaquetados_y_arquitectura(self):
        self.fake_environment('linux')
        with patch('platform.machine', return_value='x86_64'):
            self.assertEqual(validar.comprobar_entorno(self.root, 'linux')['sistema'], 'linux')
        with patch('platform.machine', return_value='aarch64'):
            with self.assertRaisesRegex(ValueError, 'Linux x64'):
                validar.comprobar_entorno(self.root, 'linux')

    def test_main_no_carga_no_cifra_no_consulta_y_exit_correcto(self):
        with patch.object(carga, 'DATA', self.root / 'salida'), \
             patch.object(carga, 'private_folder', lambda p: p.mkdir(parents=True, exist_ok=True)), \
             patch.object(validar, 'comprobar_entorno', return_value=self.environment), \
             patch.object(carga, 'backup') as backup, patch.object(carga, 'execute_sql') as execute, \
             patch.object(carga, 'make_passwords') as passwords, patch.object(carga, 'pg_settings') as pg, \
             patch('socket.create_connection', side_effect=AssertionError('No conectar')), \
             patch('subprocess.run', side_effect=AssertionError('No ejecutar motores')), \
             patch('sys.stdout', new=io.StringIO()):
            self.assertEqual(validar.main(['--carpeta', str(self.folder)]), 0)
            self.user['verificado'] = 'NO'
            self.write()
            self.assertEqual(validar.main(['--carpeta', str(self.folder)]), 1)
            report = json.loads((self.root / 'salida/validacion-carga/resultado.json').read_text(encoding='utf-8'))
            self.assertEqual(report['resultado'], 'ERROR')
            backup.assert_not_called()
            execute.assert_not_called()
            passwords.assert_not_called()
            pg.assert_not_called()

    def test_no_admite_aplicar(self):
        with patch('sys.stderr', new=io.StringIO()), self.assertRaises(SystemExit) as exc:
            validar.main(['--aplicar'])
        self.assertEqual(exc.exception.code, 2)


if __name__ == '__main__':
    unittest.main()
