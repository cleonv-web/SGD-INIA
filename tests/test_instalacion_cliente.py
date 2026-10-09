"""Pruebas offline: no ejecutan MSI, Word, servicios ni consultas de BD."""
import hashlib
import json
import os
from pathlib import Path
import sys
import tempfile
from types import SimpleNamespace
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd


class InstalacionClienteTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory()
        self.root = Path(self.temp.name)
        folder = self.root / 'cliente-windows'
        folder.mkdir()
        self.msi = folder / 'InstallerTramiteDoc.msi'
        self.msi.write_bytes(b'INSTALADOR FICTICIO PARA PRUEBAS')
        (folder / 'PROCEDENCIA.json').write_text(json.dumps({
            'sha256': hashlib.sha256(self.msi.read_bytes()).hexdigest()
        }), encoding='utf-8')
        self.root_patch = patch.object(sgd, 'ROOT', self.root)
        self.root_patch.start()
        self.platform_patch = patch.object(sgd, 'os', SimpleNamespace(name='nt', path=os.path))
        self.platform_patch.start()
        self.output_patch = patch('builtins.print')
        self.output_patch.start()

    def tearDown(self):
        self.output_patch.stop()
        self.platform_patch.stop()
        self.root_patch.stop()
        self.temp.cleanup()

    def test_paquete_valido(self):
        self.assertEqual(sgd.comprobar_paquete_cliente(), self.msi)

    def test_falta_msi_detiene_antes_del_servidor(self):
        self.msi.unlink()
        with patch.object(sgd, 'todo') as server, self.assertRaisesRegex(RuntimeError, 'Falta el cliente'):
            sgd.instalar('servidor')
        server.assert_not_called()

    def test_hash_alterado_no_ejecuta_msi(self):
        self.msi.write_bytes(b'ALTERADO')
        with patch.object(sgd.subprocess, 'run') as execute, self.assertRaisesRegex(RuntimeError, 'SHA256'):
            sgd.instalar_cliente()
        execute.assert_not_called()

    def test_cliente_existente_no_reinstala(self):
        with patch.object(sgd, 'estado_cliente_windows', return_value={'protocolo': True, 'word': True}), \
                patch.object(sgd.subprocess, 'run') as execute:
            sgd.instalar_cliente()
        execute.assert_not_called()

    def test_msi_se_abre_y_se_verifica_al_final(self):
        states = [{'protocolo': False, 'word': True}, {'protocolo': True, 'word': True}]
        with patch.object(sgd, 'estado_cliente_windows', side_effect=states), \
                patch.object(sgd.subprocess, 'run', return_value=SimpleNamespace(returncode=0)) as execute:
            sgd.instalar_cliente()
        execute.assert_called_once_with(['msiexec.exe', '/i', str(self.msi), '/norestart'], cwd=self.root)

    def test_cancelacion_no_prepara_servidor(self):
        with patch.object(sgd, 'estado_cliente_windows', return_value={'protocolo': False, 'word': True}), \
                patch.object(sgd.subprocess, 'run', return_value=SimpleNamespace(returncode=1602)), \
                patch.object(sgd, 'todo') as server, self.assertRaisesRegex(RuntimeError, 'canceló'):
            sgd.instalar('completo')
        server.assert_not_called()

    def test_reinicio_no_declara_puesto_listo(self):
        with patch.object(sgd, 'estado_cliente_windows', return_value={'protocolo': False, 'word': True}), \
                patch.object(sgd.subprocess, 'run', return_value=SimpleNamespace(returncode=3010)), \
                self.assertRaisesRegex(RuntimeError, 'reiniciar'):
            sgd.instalar_cliente()

    def test_error_msi_no_declara_puesto_listo(self):
        with patch.object(sgd, 'estado_cliente_windows', return_value={'protocolo': False, 'word': True}), \
                patch.object(sgd.subprocess, 'run', return_value=SimpleNamespace(returncode=1618)), \
                self.assertRaisesRegex(RuntimeError, '1618'):
            sgd.instalar_cliente()

    def test_exito_msi_sin_registro_se_rechaza(self):
        with patch.object(sgd, 'estado_cliente_windows', return_value={'protocolo': False, 'word': True}), \
                patch.object(sgd.subprocess, 'run', return_value=SimpleNamespace(returncode=0)), \
                self.assertRaisesRegex(RuntimeError, 'correctamente registrado'):
            sgd.instalar_cliente()

    def test_word_ausente_no_declara_puesto_listo(self):
        with patch.object(sgd, 'estado_cliente_windows', return_value={'protocolo': True, 'word': False}), \
                self.assertRaisesRegex(RuntimeError, 'WINWORD'):
            sgd.verificar_cliente()

    def test_solo_servidor_no_instala_cliente(self):
        with patch.object(sgd, 'instalar_cliente') as client, patch.object(sgd, 'todo') as server:
            sgd.instalar('servidor')
        client.assert_not_called()
        server.assert_called_once_with()

    def test_solo_puesto_no_toca_servidor(self):
        with patch.object(sgd, 'instalar_cliente') as client, patch.object(sgd, 'todo') as server:
            sgd.instalar('puesto')
        client.assert_called_once_with()
        server.assert_not_called()

    def test_completo_prepara_cliente_antes_del_servidor(self):
        steps = []
        with patch.object(sgd, 'instalar_cliente', side_effect=lambda: steps.append('cliente')), \
                patch.object(sgd, 'todo', side_effect=lambda: steps.append('servidor')):
            sgd.instalar('completo')
        self.assertEqual(steps, ['cliente', 'servidor'])

    def test_linux_no_ejecuta_instalador_windows(self):
        sgd.os = SimpleNamespace(name='posix', path=os.path)
        with patch.object(sgd.subprocess, 'run') as execute, self.assertRaisesRegex(RuntimeError, 'requiere Windows'):
            sgd.instalar_cliente()
        execute.assert_not_called()

    def test_linux_instala_solo_servidor(self):
        sgd.os = SimpleNamespace(name='posix', path=os.path)
        with patch.object(sgd, 'todo') as server:
            sgd.instalar('servidor')
        server.assert_called_once_with()
        with self.assertRaisesRegex(RuntimeError, 'Linux elegir servidor'):
            sgd.instalar('completo')

    def test_seleccion_interactiva_puesto(self):
        with patch('builtins.input', return_value='3'), patch.object(sgd, 'instalar_cliente') as client, \
                patch.object(sgd, 'todo') as server:
            sgd.instalar()
        client.assert_called_once_with()
        server.assert_not_called()

    def test_entrada_no_interactiva_no_asume_rol(self):
        with patch('builtins.input', side_effect=EOFError), patch.object(sgd, 'todo') as server, \
                self.assertRaisesRegex(RuntimeError, 'Falta seleccionar'):
            sgd.instalar()
        server.assert_not_called()

    def test_opcion_invalida_no_instala(self):
        with patch('builtins.input', return_value='otro'), patch.object(sgd, 'todo') as server, \
                self.assertRaisesRegex(RuntimeError, 'inválida'):
            sgd.instalar()
        server.assert_not_called()


if __name__ == '__main__':
    unittest.main()
