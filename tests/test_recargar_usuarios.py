"""Pruebas offline de recarga documental; nunca usan la base activa."""
from pathlib import Path
import csv
import io
import json
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import gestion_data as g
import recargar_usuarios as r


class RecargaTests(unittest.TestCase):
    def setUp(self):
        self.tmp = tempfile.TemporaryDirectory(prefix='sgd-recarga-qa-')
        self.root = Path(self.tmp.name)
        self.folder = self.make('oficina-a', '51011', '52011', 'qa.uno')
        (self.root / 'configuracion.json').write_text('{"base":"qa","modo":"completo"}', encoding='utf-8')

    def tearDown(self):
        self.tmp.cleanup()

    def make(self, name, area_code, employee, login):
        folder = self.root / name
        folder.mkdir()
        area = json.loads((g.ROOT / 'bd/piloto-area.json.ejemplo').read_text(encoding='utf-8'))
        area.update(codigo=area_code, nombre='OFICINA QA', aprobado='SI', verificado_por='QA',
                    titular_empleado=employee)
        area['sede'].update(nombre='SEDE QA', direccion='DIRECCION QA')
        user = dict(zip(g.FIELDS, ['1', employee, login, 'NOMBRE QA', 'PATERNO QA', 'MATERNO QA',
                                 '12345678', '', '1001', 'ANALISTA QA', 'USUARIO_AREA', 'SI', '1']))
        (folder / 'area.json').write_text(json.dumps(area), encoding='utf-8')
        with (folder / 'usuarios-verificados.csv').open('w', encoding='utf-8', newline='') as f:
            writer = csv.DictWriter(f, fieldnames=g.FIELDS)
            writer.writeheader()
            writer.writerow(user)
        return folder

    def test_lote_varias_oficinas_y_permiso_explicito(self):
        folder = self.make('oficina-b', '51012', '52012', 'qa.dos')
        path = folder / 'area.json'
        area = json.loads(path.read_text())
        area['perfiles']['USUARIO_AREA']['acceso_documentos'] = 'dependencia'
        path.write_text(json.dumps(area))
        items = r.bundles(parent=self.root)
        self.assertEqual(len(items), 2)
        sql = r.reload_sql(items, 'qa-ejecucion', 'qa')
        self.assertIn("'51011','52011','qa.uno','12345678','USUARIO_AREA','1'", sql)
        self.assertIn("'51012','52012','qa.dos','12345678','USUARIO_AREA','0'", sql)
        self.assertEqual(sql.count('BEGIN;'), 1)
        self.assertEqual(sql.count('COMMIT;'), 1)
        self.assertLess(sql.index('COMMIT;'), sql.index("SELECT '__PILOTO_RESULTADO__'"))
        for forbidden in ['DELETE FROM', 'TRUNCATE', 'UPDATE idosgd.seg_usuarios1',
                          'UPDATE idosgd.tdtv_remitos', 'cclave=', 'CREATE ROLE']:
            self.assertNotIn(forbidden, sql)

    def test_oficinas_duplicadas_y_entrada_no_aprobada_se_rechazan(self):
        self.make('duplicada', '51011', '52012', 'qa.dos')
        with self.assertRaisesRegex(ValueError, 'repetida'):
            r.bundles(parent=self.root)
        path = self.folder / 'area.json'
        area = json.loads(path.read_text())
        area['aprobado'] = 'NO'
        path.write_text(json.dumps(area))
        with self.assertRaisesRegex(ValueError, 'aprobado'):
            r.bundles(self.folder)

    def invoke(self, *args, **patches):
        with patch.object(r, 'ROOT', self.root), patch.object(g, 'DATA', self.root / 'resultados'), \
             patch.object(g, 'private_folder', lambda p: p.mkdir(parents=True, exist_ok=True)), \
             patch.object(g, 'backup', **patches.get('backup', {})) as backup, \
             patch.object(g, 'execute_sql', **patches.get('execute', {})) as execute, \
             patch('sys.stdout', new=io.StringIO()):
            result = r.main(['--carpeta', str(self.folder), *args])
            return result, backup, execute

    def test_plan_offline_no_respalda_ni_conecta(self):
        result, backup, execute = self.invoke()
        self.assertEqual(result, 0)
        backup.assert_not_called()
        execute.assert_not_called()

    def test_confirmacion_y_respaldo_antes_de_cualquier_sql(self):
        with self.assertRaisesRegex(ValueError, 'Confirmacion'):
            self.invoke('--aplicar')
        with self.assertRaisesRegex(RuntimeError, 'respaldo'):
            self.invoke('--aplicar', '--confirmar', 'RECARGAR:USUARIOS',
                        backup={'side_effect': RuntimeError('respaldo')},
                        execute={'side_effect': AssertionError('No ejecutar SQL sin respaldo')})

    def test_resultado_verificado_antes_de_afirmar_confirmacion(self):
        result, backup, execute = self.invoke('--aplicar', '--confirmar', 'RECARGAR:USUARIOS',
            backup={'return_value': self.root / 'backup.dump'},
            execute={'return_value': {'usuarios_comprobados': 1, 'oficinas': 1,
                                     'permisos_actualizados': 1, 'rastreos_actualizados': 1}})
        self.assertEqual(result, 0)
        backup.assert_called_once()
        execute.assert_called_once()
        with self.assertRaisesRegex(ValueError, 'falta el resultado'):
            self.invoke('--aplicar', '--confirmar', 'RECARGAR:USUARIOS',
                        execute={'return_value': {}})


if __name__ == '__main__':
    unittest.main()
