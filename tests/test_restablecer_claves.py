"""Restablecimiento acotado con respaldo; ninguna prueba toca la base activa."""
from pathlib import Path
import contextlib
import io
import json
import sys
import tempfile
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import gestion_data as g
import recargar_usuarios as r


class RestablecerClavesTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='sgd-claves-qa-')
        self.root = Path(self.temp.name)
        (self.root/'configuracion.json').write_text(json.dumps({'base':'qa', 'url_publica':'http://local.invalid'}))
        self.stack = contextlib.ExitStack()
        self.stack.enter_context(patch.object(r, 'ROOT', self.root))
        self.stack.enter_context(patch.object(g, 'DATA', self.root/'datos'))
        self.stack.enter_context(patch.object(g, 'private_folder', side_effect=lambda p:p.mkdir(parents=True)))
        self.stack.enter_context(contextlib.redirect_stdout(io.StringIO()))
        self.users = [{'usuario':'QA_UNO', 'codigo_empleado':'12345'}]
        self.targets = self.stack.enter_context(patch.object(r, 'usuarios_para_claves', return_value=self.users))
        self.backup = self.stack.enter_context(patch.object(g, 'backup', return_value=self.root/'respaldo.dump'))
        self.passwords = self.stack.enter_context(patch.object(g, 'make_passwords', return_value=({'QA_UNO':'Clave QA'}, {'QA_UNO':'abcdef'})))
        self.execute = self.stack.enter_context(patch.object(g, 'execute_sql', return_value={'claves_restablecidas':1}))
        self.addCleanup(self.temp.cleanup)
        self.addCleanup(self.stack.close)

    def test_plan_offline_no_consulta_cuentas_ni_genera_claves(self):
        r.restablecer_claves('00217')
        self.targets.assert_not_called()
        self.backup.assert_not_called()
        self.passwords.assert_not_called()

    def test_confirmacion_incorrecta_no_cambia_nada(self):
        with self.assertRaisesRegex(ValueError, 'Confirmación incorrecta'):
            r.restablecer_claves('00217', True, 'CLAVES:00195')
        self.targets.assert_not_called()

    def test_respaldo_fallido_no_genera_ni_aplica_claves(self):
        self.backup.side_effect = RuntimeError('Respaldo fallido')
        with self.assertRaisesRegex(RuntimeError, 'Respaldo fallido'):
            r.restablecer_claves('00217', True, 'CLAVES:00217')
        self.passwords.assert_not_called()
        self.execute.assert_not_called()

    def test_resultado_confirmado_entrega_accesos_privados(self):
        r.restablecer_claves('00217', True, 'CLAVES:00217')
        folder = next((self.root/'datos').iterdir())
        result = json.loads((folder/'resultado.json').read_text())
        self.assertEqual(result['estado'], 'TRANSACCION CONFIRMADA')
        self.assertIn('http://local.invalid/sisdoc/login.do', (folder/'ACCESOS-NUEVOS.txt').read_text())
        self.assertEqual(json.loads((folder/'CLAVES-GENERADAS-PRIVADAS.json').read_text()), {'QA_UNO':'Clave QA'})
        self.assertNotIn('Clave QA', self.execute.call_args[0][0])

    def test_fallo_sql_conserva_provisional_y_no_publica_accesos(self):
        self.execute.side_effect = RuntimeError('Conflicto')
        with self.assertRaisesRegex(RuntimeError, 'Conflicto'):
            r.restablecer_claves('00217', True, 'CLAVES:00217')
        folder = next((self.root/'datos').iterdir())
        self.assertTrue((folder/'CLAVES-PROVISIONALES-PRIVADAS.json').is_file())
        self.assertFalse((folder/'ACCESOS-NUEVOS.txt').exists())
        self.assertEqual(json.loads((folder/'resultado.json').read_text())['estado'], 'PENDIENTE')

    def test_sql_comprueba_identidad_area_activa_y_procedencia(self):
        sql = r.claves_sql('00217', self.users, {'QA_UNO':'abcdef'}, 'qa')
        self.assertIn("current_database()<>'qa'", sql)
        self.assertIn("d.co_dep='00217'", sql)
        self.assertIn('u.cemp_codemp=item.empleado', sql)
        self.assertIn("p.tabla='seg_usuarios1'", sql)
        self.assertIn("a.es_activo='1'", sql)
        self.assertIn('nu_intento=0', sql)
        self.assertNotIn('DELETE', sql)
        self.assertNotIn('UPDATE idosgd.tdtx_config_emp', sql)

    def test_parser_impide_mezclar_carpeta_y_area(self):
        with self.assertRaisesRegex(ValueError, 'sin --carpeta'):
            r.main(['--restablecer-claves','--area','00217','--carpeta','otra'])
        with self.assertRaisesRegex(ValueError, 'sólo corresponde'):
            r.main(['--area','00217'])


if __name__ == '__main__':
    unittest.main()
