"""Controles de seguridad offline: nunca consultan una base real."""
from pathlib import Path
import sys, tempfile, unittest
from types import SimpleNamespace
from unittest.mock import Mock, patch
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import reiniciar_piloto as r
import gestion_data as g


class ReinicioTests(unittest.TestCase):
    def test_plan_no_toca_servidor(self):
        backend = Mock()
        r.reiniciar(_sgd=backend)
        self.assertEqual(backend.mock_calls, [])

    def test_confirmacion_incorrecta_no_toca_servidor(self):
        backend = Mock()
        with self.assertRaises(ValueError):
            r.reiniciar(aplicar=True, confirmar='RETIRAR:00217', _sgd=backend)
        self.assertEqual(backend.mock_calls, [])

    def test_destino_remoto_rechazado(self):
        backend = Mock(C={'modo':'completo','base':'idosgd_inia', 'host_bd':'192.0.2.1','puerto_bd':55432})
        with self.assertRaises(RuntimeError):
            r.reiniciar(aplicar=True, confirmar=r.TOKEN, _sgd=backend)
        backend.detener.assert_not_called()
        backend.psql.assert_not_called()

    def test_rol_existente_conserva_grants(self):
        sql = "CREATE ROLE sgd_app LOGIN PASSWORD 'QA';\nGRANT USAGE ON SCHEMA idosgd TO sgd_app;\n"
        result = r.preparar_sql(sql, True)
        self.assertNotIn('CREATE ROLE', result)
        self.assertIn('GRANT USAGE', result)
        self.assertEqual(r.preparar_sql(sql, False), sql)

    def test_sql_incompatible_no_continua(self):
        with self.assertRaises(RuntimeError):
            r.preparar_sql('SELECT 1;', True)

    def test_todo_no_se_mezcla_con_area(self):
        with patch('reiniciar_piloto.reiniciar') as reset:
            with self.assertRaises(ValueError):
                g.main(['limpiar','--todo','--area','00217'])
            reset.assert_not_called()

    def test_respaldo_fallido_no_borra_ni_mueve_documentos(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder).resolve(); data=root/'datos'; docs=data/'documentos'
            docs.mkdir(parents=True); original=docs/'documento.txt'; original.write_text('QA')
            backend=Mock(ROOT=root, DATA=data,
                C=dict(modo='completo',base='idosgd_inia',host_bd='127.0.0.1',puerto_bd=55432))
            backend.admin.return_value=SimpleNamespace(stdout='sgd not running')
            backend.scalar.return_value='t'
            backend.generar_sql.return_value="CREATE ROLE sgd_app LOGIN PASSWORD 'QA';\n"
            with patch.object(g,'backup',side_effect=RuntimeError('Respaldo fallido')):
                with self.assertRaisesRegex(RuntimeError,'Respaldo fallido'):
                    r.reiniciar(aplicar=True,confirmar=r.TOKEN,_sgd=backend)
            self.assertEqual(original.read_text(),'QA')
            backend.psql.assert_not_called()


if __name__ == '__main__':
    unittest.main()
