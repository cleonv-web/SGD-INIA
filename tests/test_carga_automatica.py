"""Carga con un comando; servicios y aplicación simulados, sin tocar bases."""
from pathlib import Path
import contextlib
import io
import sys
import tempfile
from types import SimpleNamespace
import unittest
from unittest.mock import Mock, patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import gestion_data as g
import validar_carga as v


class CargaAutomaticaTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='sgd-carga-auto-')
        self.root = Path(self.temp.name)
        self.data = self.root / 'datos'
        (self.data / 'postgresql').mkdir(parents=True)
        (self.data / 'postgresql/PG_VERSION').write_text('17')
        self.events = []
        self.active = True
        self.pg_active = True
        self.stop_works = True
        self.sgd = SimpleNamespace(
            C={'modo': 'completo', 'host_bd': '127.0.0.1', 'url_publica': 'http://local.invalid'},
            LOG=self.root / 'logs', pg_tool=self.pg_tool, scalar=Mock(return_value='t'),
            admin=self.admin, iniciar=self.iniciar)
        self.area = {'codigo': '00195', 'nombre': 'GERENCIA GENERAL'}
        self.bundle = (self.area, [{'usuario': 'qa'}], 'huella-qa')
        self.stack = contextlib.ExitStack()
        self.stack.enter_context(patch.object(g, 'DATA', self.data))
        self.validation = self.stack.enter_context(patch.object(v, 'validar', return_value={'errores': []}))
        self.read = self.stack.enter_context(patch.object(g, 'read_bundle', return_value=self.bundle))
        self.load = self.stack.enter_context(patch.object(g, 'main', side_effect=self.apply))
        self.stack.enter_context(patch.dict(sys.modules, {'sgd': self.sgd}))
        self.stack.enter_context(contextlib.redirect_stdout(io.StringIO()))
        self.addCleanup(self.stack.close)
        self.addCleanup(self.temp.cleanup)

    def pg_tool(self, name, args, **kwargs):
        self.events.append('postgres-' + args[-1])
        return SimpleNamespace(returncode=0 if self.pg_active or args[-1] != 'status' else 3)

    def admin(self, args, **kwargs):
        self.events.append(args[0])
        if args[0] == 'stop-domain' and self.stop_works:
            self.active = False
        return SimpleNamespace(stdout='sgd running' if self.active else 'sgd not running')

    def iniciar(self, *, solo):
        self.assertTrue(solo)
        self.events.append('reanudar')
        self.active = True

    def apply(self, argv):
        self.assertFalse(self.active)
        self.events.append('aplicar')

    def test_carpeta_y_area_automaticas_con_pausa_y_arranque(self):
        g.cargar_preparacion()
        self.validation.assert_called_once_with(self.data / 'carga-util-preparada')
        self.load.assert_called_once_with(['cargar', '--carpeta', str(self.data / 'carga-util-preparada'),
                                          '--aplicar', '--confirmar', 'CARGAR:00195'])
        self.assertEqual(self.events, ['postgres-status', 'list-domains', 'stop-domain',
                                      'list-domains', 'aplicar', 'reanudar'])

    def test_validacion_fallida_no_consulta_base_ni_servicios(self):
        self.validation.return_value = {'errores': ['Área no aprobada']}
        with self.assertRaisesRegex(ValueError, 'no aprobada'):
            g.cargar_preparacion()
        self.assertEqual(self.events, [])
        self.sgd.scalar.assert_not_called()
        self.load.assert_not_called()

    def test_no_inicializa_cluster_ausente(self):
        (self.data / 'postgresql/PG_VERSION').unlink()
        with self.assertRaisesRegex(ValueError, 'No existe una base'):
            g.cargar_preparacion()
        self.assertEqual(self.events, [])

    def test_inicia_base_existente_sin_parar_postgresql(self):
        self.pg_active = False
        g.cargar_preparacion()
        self.assertEqual(self.events[:2], ['postgres-status', 'postgres-start'])
        self.assertNotIn('postgres-stop', self.events)

    def test_servidor_separado_no_cambia_servicios(self):
        self.sgd.C['host_bd'] = '192.0.2.10'
        with self.assertRaisesRegex(ValueError, 'PostgreSQL local'):
            g.cargar_preparacion()
        self.assertEqual(self.events, [])

    def test_base_no_reconocida_no_aplica(self):
        self.sgd.scalar.return_value = 'f'
        with self.assertRaisesRegex(ValueError, 'instalación SGD'):
            g.cargar_preparacion()
        self.load.assert_not_called()
        self.assertNotIn('stop-domain', self.events)

    def test_pausa_fallida_no_carga_y_reanuda(self):
        self.stop_works = False
        with self.assertRaisesRegex(ValueError, 'sigue ejecutándose'):
            g.cargar_preparacion()
        self.load.assert_not_called()
        self.assertEqual(self.events[-1], 'reanudar')

    def test_error_de_respaldo_o_carga_reanuda_aplicacion(self):
        self.load.side_effect = RuntimeError('Falló respaldo')
        with self.assertRaisesRegex(RuntimeError, 'Falló respaldo'):
            g.cargar_preparacion()
        self.assertTrue(self.active)
        self.assertEqual(self.events[-1], 'reanudar')

    def test_carga_fallida_no_arranca_aplicacion_antes_detenida(self):
        self.active = False
        self.load.side_effect = RuntimeError('Conflicto')
        with self.assertRaisesRegex(RuntimeError, 'Conflicto'):
            g.cargar_preparacion()
        self.assertNotIn('reanudar', self.events)

    def test_cambio_de_archivos_durante_revision_bloquea_carga(self):
        self.read.side_effect = [self.bundle, (self.area, [], 'huella-modificada')]
        with self.assertRaisesRegex(ValueError, 'cambiaron'):
            g.cargar_preparacion()
        self.load.assert_not_called()
        self.assertTrue(self.active)

    def test_carga_aplicada_con_error_arranque_se_informa_distinto(self):
        self.sgd.iniciar = Mock(side_effect=RuntimeError('Arranque fallido'))
        with self.assertRaisesRegex(RuntimeError, 'La carga fue aplicada'):
            g.cargar_preparacion()
        self.load.assert_called_once()

    def test_parser_automatico_no_acepta_limpieza_ni_opciones_manuales(self):
        # Recuperar la función real para comprobar sus argumentos, sin ejecutar cargas.
        with patch.object(g, 'cargar_preparacion') as automatic:
            for argv in [['limpiar', '--automatico'], ['cargar', '--automatico', '--carpeta', 'otra'],
                         ['cargar', '--automatico', '--aplicar'], ['cargar', '--automatico', '--todo']]:
                with self.assertRaisesRegex(ValueError, 'solo se admite'):
                    REAL_MAIN(argv)
            automatic.assert_not_called()
            REAL_MAIN(['cargar', '--automatico'])
            automatic.assert_called_once_with()


REAL_MAIN = g.main
if __name__ == '__main__':
    unittest.main()
