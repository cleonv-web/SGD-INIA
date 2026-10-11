"""Controles de seguridad offline: nunca consultan una base real."""
from pathlib import Path
import os, sys, tempfile, unittest
from types import SimpleNamespace
from unittest.mock import Mock, patch
sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import reiniciar_piloto as r
import gestion_data as g


class ReinicioTests(unittest.TestCase):
    def test_archivo_conserva_carpetas_y_verifica_contenido(self):
        with tempfile.TemporaryDirectory() as folder:
            data=Path(folder).resolve()/'datos'; docs=data/'documentos'
            nested=docs/'2026'/'informe.pdf'; nested.parent.mkdir(parents=True)
            nested.write_bytes(b'PDF QA')
            archive=data/'respaldo'/'documentos-anteriores'; archive.parent.mkdir()
            report=r.archivar_documentos(docs,archive,data)
            self.assertTrue(nested.parent.is_dir())
            self.assertEqual(list(docs.rglob('*.pdf')),[])
            self.assertEqual((archive/'2026'/'informe.pdf').read_bytes(),b'PDF QA')
            self.assertEqual(report,[{'archivo':'2026/informe.pdf',
                'sha256':r.huella_archivo(archive/'2026'/'informe.pdf')}])

    def test_archivo_bloqueado_restaura_movidos_y_no_borra_base(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder).resolve(); data=root/'datos'; docs=data/'documentos'
            docs.mkdir(parents=True)
            (docs/'a.txt').write_text('A'); (docs/'b.txt').write_text('B')
            backend=Mock(ROOT=root, DATA=data,
                C=dict(modo='completo',base='idosgd_inia',host_bd='127.0.0.1',puerto_bd=55432))
            backend.admin.return_value=SimpleNamespace(stdout='sgd not running')
            backend.scalar.return_value='t'
            backend.generar_sql.return_value="CREATE ROLE sgd_app LOGIN PASSWORD 'QA';\n"
            rename=Path.rename
            def bloquear_b(source,target):
                if source == docs/'b.txt':
                    raise PermissionError('QA archivo abierto')
                return rename(source,target)
            with patch.object(g,'backup',return_value=data/'QA.dump'), \
                    patch.object(Path,'rename',bloquear_b):
                with self.assertRaisesRegex(RuntimeError,'No se borro la base'):
                    r.reiniciar(aplicar=True,confirmar=r.TOKEN,_sgd=backend)
            backend.psql.assert_not_called()
            self.assertEqual((docs/'a.txt').read_text(),'A')
            self.assertEqual((docs/'b.txt').read_text(),'B')
            reports=list(data.glob('reinicio-piloto-*/resultado.json'))
            import json
            result=json.loads(reports[0].read_text(encoding='utf-8'))
            self.assertEqual(result['estado'],'ERROR_AL_ARCHIVAR_DOCUMENTOS')
            self.assertIn('QA archivo abierto',result['error'])

    def test_reinicio_solo_borra_despues_de_archivar_y_verificar(self):
        import json
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder).resolve(); data=root/'datos'; docs=data/'documentos'
            docs.mkdir(parents=True); (docs/'documento.txt').write_text('QA')
            backend=Mock(ROOT=root, DATA=data,
                C=dict(modo='completo',base='idosgd_inia',host_bd='127.0.0.1',puerto_bd=55432))
            backend.admin.return_value=SimpleNamespace(stdout='sgd not running')
            backend.scalar.side_effect=['t','t','0']
            backend.generar_sql.return_value="CREATE ROLE sgd_app LOGIN PASSWORD 'QA';\n"
            def comprobar_antes_de_borrar(sql,**kwargs):
                self.assertEqual(r.archivos_documentales(docs),[])
                archives=list(data.glob('reinicio-piloto-*/documentos-anteriores/documento.txt'))
                self.assertEqual(len(archives),1)
                self.assertEqual(archives[0].read_text(),'QA')
            backend.psql.side_effect=comprobar_antes_de_borrar
            with patch.object(g,'backup',return_value=data/'QA.dump'):
                r.reiniciar(aplicar=True,confirmar=r.TOKEN,_sgd=backend)
            self.assertEqual(backend.psql.call_count,2)
            self.assertIn('DROP DATABASE',backend.psql.call_args_list[0].args[0])
            result=json.loads(next(data.glob('reinicio-piloto-*/resultado.json')).read_text(encoding='utf-8'))
            self.assertEqual(result['estado'],'REINICIADO')
            self.assertEqual(result['documentos'],0)
            self.assertEqual(result['archivos'],1)

    def test_destino_documental_fuera_de_datos_rechazado(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder).resolve(); data=root/'datos'; docs=data/'documentos'
            docs.mkdir(parents=True); original=docs/'documento.txt'; original.write_text('QA')
            with self.assertRaises(RuntimeError):
                r.archivar_documentos(docs,root/'archivo-ajeno',data)
            self.assertEqual(original.read_text(),'QA')

    def test_error_de_lectura_no_se_omite(self):
        with tempfile.TemporaryDirectory() as folder:
            docs=Path(folder).resolve()
            walk=os.walk
            def denegar(top,*,followlinks,onerror):
                onerror(PermissionError('QA carpeta ilegible'))
                return walk(top)
            with patch.object(r.os,'walk',side_effect=denegar):
                with self.assertRaises(PermissionError):
                    r.archivos_documentales(docs)

    @unittest.skipUnless(os.name=='nt','Bloqueo de directorio especifico de Windows')
    def test_directorio_abierto_windows_no_impide_archivar(self):
        import ctypes
        from ctypes import wintypes
        kernel=ctypes.WinDLL('kernel32',use_last_error=True)
        kernel.CreateFileW.argtypes=[wintypes.LPCWSTR,wintypes.DWORD,wintypes.DWORD,
            wintypes.LPVOID,wintypes.DWORD,wintypes.DWORD,wintypes.HANDLE]
        kernel.CreateFileW.restype=wintypes.HANDLE
        kernel.CloseHandle.argtypes=[wintypes.HANDLE]
        kernel.CloseHandle.restype=wintypes.BOOL
        with tempfile.TemporaryDirectory() as folder:
            data=Path(folder).resolve()/'datos'; docs=data/'documentos'
            docs.mkdir(parents=True); (docs/'documento.txt').write_text('QA')
            archive=data/'respaldo'/'documentos-anteriores'; archive.parent.mkdir()
            handle=kernel.CreateFileW(str(docs),1,3,None,3,0x02000000,None)
            self.assertNotEqual(handle,ctypes.c_void_p(-1).value)
            try:
                with self.assertRaises(PermissionError):
                    docs.rename(archive)
                report=r.archivar_documentos(docs,archive,data)
                self.assertEqual(len(report),1)
                self.assertTrue(docs.is_dir())
                self.assertEqual((archive/'documento.txt').read_text(),'QA')
            finally:
                kernel.CloseHandle(handle)

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
