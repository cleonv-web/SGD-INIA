"""Diagnóstico de puertos sin iniciar servicios ni escribir credenciales."""
import contextlib
import io
import json
from pathlib import Path
import sys
from types import SimpleNamespace
import unittest
from unittest.mock import patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd


class PuertoHttpsInstalacionTests(unittest.TestCase):
    def setUp(self):
        self.config = json.loads((sgd.ROOT/'configuracion.ejemplo.json').read_text(encoding='utf8'))
        self.stack = contextlib.ExitStack()
        self.addCleanup(self.stack.close)
        self.stack.enter_context(patch.object(sgd, 'C', self.config))
        self.stack.enter_context(patch.object(sgd, 'java_home', return_value=Path('java-simulado')))
        self.stack.enter_context(patch.object(sgd, 'run', return_value=SimpleNamespace(stdout='', stderr='1.8.0')))
        self.stack.enter_context(patch.object(sgd, 'pg_tool', return_value=SimpleNamespace(stdout='17.11')))
        self.credentials = self.stack.enter_context(patch.object(sgd, 'credentials'))
        self.stack.enter_context(patch.object(sgd.os, 'geteuid', return_value=1000, create=True))
        self.stack.enter_context(contextlib.redirect_stdout(io.StringIO()))

    def test_https_443_pasa_diagnostico(self):
        self.config['puerto_https'] = 443
        sgd.diagnosticar()
        self.credentials.assert_called_once()

    def test_configuracion_original_sigue_valida(self):
        sgd.diagnosticar()
        self.credentials.assert_called_once()

    def test_no_habilita_443_en_otros_servicios(self):
        for campo in ['puerto_http', 'puerto_bd', 'puerto_admin']:
            with self.subTest(campo=campo):
                original = self.config[campo]
                self.config[campo] = 443
                with self.assertRaisesRegex(RuntimeError, 'Puerto inválido: '+campo):
                    sgd.diagnosticar()
                self.config[campo] = original
        self.credentials.assert_not_called()

    def test_rechaza_https_invalido_y_puertos_duplicados(self):
        for valor in [0, -1, 80, 1024, 60000, 65536, '443', True]:
            with self.subTest(valor=valor):
                self.config['puerto_https'] = valor
                with self.assertRaisesRegex(RuntimeError, 'Puerto inválido: puerto_https'):
                    sgd.diagnosticar()
        self.config['puerto_https'] = self.config['puerto_http']
        with self.assertRaisesRegex(RuntimeError, 'Los puertos deben ser distintos'):
            sgd.diagnosticar()
        self.credentials.assert_not_called()


if __name__ == '__main__':
    unittest.main()
