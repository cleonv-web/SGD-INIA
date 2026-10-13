"""Pruebas offline y PostgreSQL temporal; nunca limpian la base del operador."""
from pathlib import Path
import json
import hashlib
import os
import sys
import tempfile
import subprocess
import time
import shutil
import unittest
from types import SimpleNamespace
from unittest.mock import Mock, patch

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
sys.path.insert(0, str(Path(__file__).resolve().parent))
import limpiar_tramites as l
import gestion_data as g
import reiniciar_piloto as r
import test_carga_postgresql_integracion as pg


class SeguridadTests(unittest.TestCase):
    def test_plan_offline_y_confirmacion_invalida_no_importan_backend(self):
        backend = Mock()
        l.limpiar(_sgd=backend)
        with self.assertRaises(ValueError):
            l.limpiar(aplicar=True, confirmar='REINICIAR:idosgd_inia', _sgd=backend)
        self.assertEqual(backend.mock_calls, [])

    def test_destino_ajeno_se_rechaza_antes_de_conectar(self):
        backend = Mock(C=dict(modo='completo', base='otra', host_bd='127.0.0.1', puerto_bd=55432))
        with self.assertRaisesRegex(RuntimeError, 'piloto local'):
            l.limpiar(aplicar=True, confirmar=l.TOKEN, _sgd=backend)
        backend.pg_tool.assert_not_called()
        backend.admin.assert_not_called()

    def test_modo_simple_inicia_solo_el_cluster_existente(self):
        with tempfile.TemporaryDirectory() as folder:
            root=Path(folder).resolve(); data=root/'datos'; (data/'postgresql').mkdir(parents=True)
            (data/'postgresql/PG_VERSION').write_text('17')
            (data/'credenciales.json').write_text('{}')
            backend=Mock(ROOT=root,DATA=data,LOG=root/'logs',
                C=dict(modo='completo',base='idosgd_inia',host_bd='127.0.0.1',puerto_bd=55432))
            backend.pg_tool.side_effect=[SimpleNamespace(returncode=1),SimpleNamespace(returncode=0)]
            l.validar_destino(backend,iniciar_bd=True)
            self.assertEqual(backend.pg_tool.call_args_list[0].args[1][-1],'status')
            self.assertEqual(backend.pg_tool.call_args_list[1].args[1][-1],'start')
            backend.base.assert_not_called()

    def test_reabre_clientes_cerrados_una_sola_vez(self):
        with tempfile.TemporaryDirectory() as folder:
            word=Path(folder)/'WINWORD.exe'; word.write_bytes(b'FICTICIO')
            state={'cerrados':[{'nombre':'WINWORD','ruta':str(word)},
                              {'nombre':'WINWORD','ruta':str(word)}]}
            with patch.object(l,'clientes_windows',return_value={'activos':[],'cerrados':[]}), \
                    patch.object(l.subprocess,'Popen') as launch:
                l.reabrir_clientes(state)
                launch.assert_called_once_with([str(word)],cwd=word.parent)

    def test_word_abierto_se_rechaza_sin_forzarlo(self):
        result = SimpleNamespace(returncode=0, stdout=json.dumps(
            {'cerrados':[], 'activos':[{'nombre':'WINWORD','pid':123}]}),stderr='')
        with patch.object(l.subprocess,'run',return_value=result):
            with self.assertRaisesRegex(RuntimeError,'guardado'):
                l.clientes_windows(cerrar=True)

    def test_reapertura_solo_programas_con_ruta_verificada(self):
        with patch.object(l,'clientes_windows',return_value={'cerrados':[],'activos':[]}), \
                patch.object(l.subprocess,'Popen') as launch:
            with self.assertRaisesRegex(RuntimeError,'localizar'):
                l.reabrir_clientes({'cerrados':[{'nombre':'WINWORD','ruta':'malicioso.exe'}]})
            launch.assert_not_called()

    @unittest.skipUnless(os.name=='nt','Lanzador nativo Windows')
    def test_exe_invoca_modo_simple_desde_su_carpeta_sin_limpiar_datos(self):
        # Copia del EXE y runtime ficticio: nunca ejecutar el EXE de la instalación.
        compiler=Path(os.environ['WINDIR'])/'Microsoft.NET/Framework64/v4.0.30319/csc.exe'
        with tempfile.TemporaryDirectory(prefix='sgd-launcher-qa-') as folder:
            root=Path(folder); runtime=root/'herramientas/python-windows'; runtime.mkdir(parents=True)
            shutil.copy2(l.ROOT/'Limpiar-Tramites.exe',root/'Limpiar-Tramites.exe')
            (root/'limpiar_tramites.py').write_text('# backend ficticio; no contiene limpieza')
            source=root/'RuntimeFicticio.cs'
            source.write_text('using System; using System.IO; class QA { static int Main(string[] args) {'
                'File.WriteAllText(Path.Combine(AppDomain.CurrentDomain.BaseDirectory,"invocacion.txt"),'
                'string.Join("\\n",args)); return 0; }}')
            subprocess.run([str(compiler),'/nologo','/target:exe',
                            '/out:'+str(runtime/'python.exe'),str(source)],check=True,capture_output=True)
            result=subprocess.run([str(root/'Limpiar-Tramites.exe')],cwd=runtime,
                                   capture_output=True,timeout=10)
            self.assertEqual(result.returncode,0)
            self.assertEqual((runtime/'invocacion.txt').read_text(),str(root/'limpiar_tramites.py')+'\n--simple')

    def test_archivos_se_restauran_sin_sobreescribir(self):
        with tempfile.TemporaryDirectory() as folder:
            data = Path(folder).resolve()/'datos'
            docs = data/'documentos'; docs.mkdir(parents=True)
            work = data/'backup'; work.mkdir()
            (docs/'qa.pdf').write_bytes(b'PDF FICTICIO')
            archive = work/'documentos-anteriores'
            manifest = r.archivar_documentos(docs, archive, data)
            (docs/'qa.pdf').write_bytes(b'NUEVO')
            with self.assertRaisesRegex(RuntimeError, 'restaurar'):
                l.restaurar_archivos(docs, archive, manifest)
            self.assertEqual((docs/'qa.pdf').read_bytes(), b'NUEVO')
            (docs/'qa.pdf').unlink()
            l.restaurar_archivos(docs, archive, manifest)
            self.assertEqual((docs/'qa.pdf').read_bytes(), b'PDF FICTICIO')


@unittest.skipUnless(os.environ.get('SGD_PROBAR_POSTGRESQL_AISLADO') == '1',
                     'Requiere PostgreSQL temporal aislado')
class TramitesPostgreSQLTests(unittest.TestCase):
    # Reutiliza sólo el arranque del cluster temporal, sin heredar sus otros tests.
    run_tool = pg.CargaPostgreSQLTests.__dict__['run_tool']
    @classmethod
    def query(cls, db, sql, check=True):
        result = cls.run_tool('psql','-h','127.0.0.1','-p',cls.port,'-U','postgres','-d',db,
                              '-X','-qAt','-v','ON_ERROR_STOP=1','-f','-',input=sql,check=False)
        if check and result.returncode:
            raise RuntimeError(result.stderr)
        return result
    setUpClass = pg.CargaPostgreSQLTests.__dict__['setUpClass']
    tearDownClass = pg.CargaPostgreSQLTests.__dict__['tearDownClass']

    def setUp(self):
        clientes = patch.object(l,'clientes_windows',return_value={'cerrados':[],'activos':[]})
        self.clientes = clientes.start()
        self.addCleanup(clientes.stop)
        self.db = 'qa_'+hashlib.sha256(self._testMethodName.encode()).hexdigest()[:12]
        self.run_tool('createdb', '-h', '127.0.0.1', '-p', self.port, '-U', 'postgres', '-T', 'qa_base', self.db)
        self.root = self.path/self.db; self.data = self.root/'datos'; self.docs = self.data/'documentos'
        self.docs.mkdir(parents=True)
        (self.data/'postgresql').mkdir()
        (self.data/'postgresql/PG_VERSION').write_text('17')
        (self.data/'credenciales.json').write_text('{}')
        (self.docs/'qa.pdf').write_bytes(b'PDF FICTICIO')
        self.query(self.db, """
INSERT INTO idosgd.seg_usuarios1(cod_user,cdes_user,cclave,cemp_codemp,nu_intento)
 VALUES('qa.real','Cuenta cargada QA','CLAVE_FICTICIA','49001',2);
INSERT INTO idosgd.tdtc_expediente(nu_ann_exp,nu_sec_exp,nu_expediente) VALUES('2026','42','QA2026000042');
INSERT INTO idosgd.tdtv_remitos(nu_ann,nu_emi,co_dep_emi,co_emp_emi,nu_ann_exp,nu_sec_exp,de_asu,
 co_loc_emi,es_doc_emi,es_eli,co_use_cre,fe_use_cre,co_use_mod,fe_use_mod)
 VALUES('2026','42','49001','49001','2026','42','TRAMITE FICTICIO','001','5','0','qa.real',now(),'qa.real',now());
INSERT INTO idosgd.tdtv_destinos(nu_ann,nu_emi,nu_des,co_dep_des,co_emp_des,
 es_eli,co_use_cre,fe_use_cre,co_use_mod,fe_use_mod)
 VALUES('2026','42',1,'49002','49002','0','qa.real',now(),'qa.real',now());
INSERT INTO idosgd.tdtv_anexos(nu_ann,nu_emi,nu_ane,bl_doc,co_use_cre,co_use_mod,fe_use_cre,fe_use_mod)
 VALUES('2026','42',1,decode('0102','hex'),'qa.real','qa.real',now(),now());
INSERT INTO idosgd.tdtv_archivo_doc(nu_ann,nu_emi,bl_doc) VALUES('2026','42',decode('0304','hex'));
INSERT INTO idosgd.tdtr_referencia(nu_ann,nu_emi,nu_des,co_ref,nu_ann_ref,nu_emi_ref,nu_des_ref,co_use_cre,fe_use_cre)
 VALUES('2026','42',1,'42','2026','42',1,'qa.real',now());
INSERT INTO idosgd.tdtr_plantilla_docx(co_tipo_doc,co_dep,bl_doc) VALUES('003','00000',decode('aabbcc','hex'));
SELECT setval('idosgd.sec_remitos_nu_emi',777,true);
""" + 'UPDATE idosgd.tdtx_config_emp SET de_dir_emi='+l.literal(str(self.docs))+
            ',de_dir_rec='+l.literal(str(self.docs))+',de_dir_ane='+l.literal(str(self.docs))+';')
        self.running = True
        self.actions = []
        def admin(args, **kw):
            self.actions.append(args[0])
            if args[0] == 'stop-domain': self.running = False
            return SimpleNamespace(stdout='sgd running' if self.running else 'sgd not running')
        def iniciar(**kw):
            self.running = True
        def tool(name, args, **kw):
            if name == 'pg_ctl': return SimpleNamespace(returncode=0)
            if name == 'psql':
                return self.query(self.db, args[-1])
            return self.run_tool(name, *args)
        self.backend = SimpleNamespace(ROOT=self.root, DATA=self.data,
            C=dict(modo='completo',base='idosgd_inia',host_bd='127.0.0.1',puerto_bd=55432),
            pg_tool=tool, connection=lambda:[], admin=admin, iniciar=iniciar,
            psql=lambda sql,**kw:self.query(self.db,sql), redact=lambda value:value)

    def backup(self, work):
        target = work/'antes-de-aplicar.dump'
        self.run_tool('pg_dump', '-h','127.0.0.1','-p',self.port,'-U','postgres','-d',self.db,'-Fc','-f',target)
        return target

    def apply(self):
        with patch.object(g, 'backup', side_effect=self.backup):
            l.limpiar(aplicar=True, confirmar=l.TOKEN, _sgd=self.backend)

    def user(self):
        return self.query(self.db, "SELECT row_to_json(t) FROM idosgd.seg_usuarios1 t WHERE cod_user='qa.real';").stdout

    def count(self):
        return self.query(self.db, 'SELECT count(*) FROM idosgd.tdtv_remitos;').stdout.strip()

    def test_limpieza_respalda_y_conserva_cuentas_plantillas_y_secuencias(self):
        before = self.user()
        self.apply()
        self.assertEqual(self.count(), '0')
        self.assertEqual(self.user(), before)
        self.assertEqual(self.query(self.db, 'SELECT last_value FROM idosgd.sec_remitos_nu_emi;').stdout.strip(),'777')
        self.assertEqual(self.query(self.db, "SELECT encode(bl_doc,'hex') FROM idosgd.tdtr_plantilla_docx WHERE co_dep='00000';").stdout.strip(),'aabbcc')
        self.assertFalse((self.docs/'qa.pdf').exists())
        work = next(self.data.glob('limpieza-tramites-*'))
        self.assertEqual((work/'documentos-anteriores/qa.pdf').read_bytes(),b'PDF FICTICIO')
        self.assertTrue(self.running)
        report = json.loads((work/'resultado.json').read_text(encoding='utf-8'))
        self.assertEqual(report['estado'],'COMPLETADO')
        self.assertGreater(len(report['comprobacion']['tablas_conservadas']),50)
        # Restaurar el dump en otra base temporal demuestra que es recuperable.
        restored = self.db+'_restore'
        self.run_tool('createdb','-h','127.0.0.1','-p',self.port,'-U','postgres',restored)
        self.run_tool('pg_restore','-h','127.0.0.1','-p',self.port,'-U','postgres','-d',restored,
                      '--exit-on-error',work/'antes-de-aplicar.dump')
        self.assertEqual(self.query(restored,'SELECT count(*) FROM idosgd.tdtv_remitos;').stdout.strip(),'1')
        # Segunda ejecución también conserva datos y no reutiliza secuencias.
        self.apply()
        self.assertEqual(self.user(),before)

    def test_fk_extra_revierte_sql_y_restauracion_archivos(self):
        self.query(self.db, 'CREATE TABLE idosgd.extension_qa(id integer,ann varchar(4),emi varchar(10),'
                   'FOREIGN KEY(ann,emi) REFERENCES idosgd.tdtv_remitos(nu_ann,nu_emi));')
        with self.assertRaisesRegex(RuntimeError,'archivos restaurados'):
            self.apply()
        self.assertEqual(self.count(),'1')
        self.assertTrue((self.docs/'qa.pdf').exists())
        self.assertTrue(self.running)

    def test_error_respaldo_no_mueve_archivos_ni_limpia(self):
        with patch.object(g,'backup',side_effect=RuntimeError('fallo QA')):
            with self.assertRaisesRegex(RuntimeError,'fallo QA'):
                l.limpiar(aplicar=True, confirmar=l.TOKEN, _sgd=self.backend)
        self.assertEqual(self.count(),'1')
        self.assertTrue((self.docs/'qa.pdf').exists())
        self.assertTrue(self.running)

    def test_tabla_documental_nueva_se_rechaza_sin_pausar(self):
        self.query(self.db,'CREATE TABLE idosgd.nuevos_tramites(nu_emi text);')
        with self.assertRaisesRegex(RuntimeError,'adicional'):
            self.apply()
        self.assertNotIn('stop-domain',self.actions)
        self.assertEqual(self.count(),'1')

    def test_commit_confirmado_aunque_cliente_reporte_error(self):
        def committed(sql, **kw):
            self.query(self.db,sql)
            raise RuntimeError('Desconexion despues del COMMIT QA')
        self.backend.psql = committed
        self.apply()
        self.assertEqual(self.count(),'0')
        self.assertFalse((self.docs/'qa.pdf').exists())
        self.assertTrue(self.running)

    def test_error_al_archivar_conserva_base_y_exige_revision(self):
        with patch.object(r,'archivar_documentos',side_effect=PermissionError('Word abierto QA')):
            with self.assertRaises(PermissionError):
                self.apply()
        self.assertEqual(self.count(),'1')
        self.assertTrue((self.docs/'qa.pdf').exists())
        self.assertFalse(self.running)
        self.assertTrue((self.data/'.limpieza-tramites.lock').exists())

    def test_trigger_truncate_sin_revisar_impide_cambios(self):
        self.query(self.db, "CREATE FUNCTION idosgd.trigger_qa() RETURNS trigger LANGUAGE plpgsql AS $$"
                   "BEGIN UPDATE idosgd.seg_usuarios1 SET cclave='MAL'; RETURN NULL; END;$$;"
                   "CREATE TRIGGER qa_truncate AFTER TRUNCATE ON idosgd.tdtv_remitos "
                   "FOR EACH STATEMENT EXECUTE FUNCTION idosgd.trigger_qa();")
        before = self.user()
        with self.assertRaisesRegex(RuntimeError,'archivos restaurados'):
            self.apply()
        self.assertEqual(self.user(),before)
        self.assertEqual(self.count(),'1')
        self.assertTrue(self.running)

    def test_cierre_fallido_no_respalda_archiva_ni_limpia(self):
        self.clientes.side_effect = RuntimeError('Word no cierra QA')
        with patch.object(g,'backup') as dump, patch.object(r,'archivar_documentos') as archive:
            with self.assertRaisesRegex(RuntimeError,'Word no cierra'):
                l.limpiar(aplicar=True,confirmar=l.TOKEN,_sgd=self.backend)
            dump.assert_not_called()
            archive.assert_not_called()
        self.assertEqual(self.count(),'1')
        self.assertTrue((self.docs/'qa.pdf').exists())
        self.assertTrue(self.running)

    def test_modo_simple_habilita_servidor_aunque_estaba_detenido(self):
        self.running = False
        with patch.object(g,'backup',side_effect=self.backup), patch.object(l,'verificar_sgd') as verify:
            l.limpiar(aplicar=True, confirmar=l.TOKEN, reanudar_todo=True, _sgd=self.backend)
            verify.assert_called_once_with(self.backend)
        self.assertTrue(self.running)
        self.assertEqual(self.count(),'0')
        self.assertTrue(self.clientes.call_args_list[0].kwargs['cerrar'])

    def test_cierra_sesion_postgresql_antes_de_limpiar(self):
        command=[str(self.home/'bin'/('psql.exe' if os.name=='nt' else 'psql')),'-h','127.0.0.1','-p',str(self.port),
                 '-U','postgres','-d',self.db,'-X','-c','SELECT pg_sleep(30);']
        with subprocess.Popen(command,stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL) as client:
            try:
                deadline=time.monotonic()+5
                while time.monotonic()<deadline:
                    value=l.consulta(self.backend,"SELECT count(*) FROM pg_stat_activity WHERE "
                        "datname=current_database() AND pid<>pg_backend_pid() AND backend_type='client backend';")
                    if int(value): break
                    time.sleep(.05)
                self.assertEqual(l.cerrar_conexiones_bd(self.backend),1)
                client.wait(timeout=5)
            finally:
                if client.poll() is None: client.terminate()
        self.assertEqual(self.count(),'1')


if __name__ == '__main__':
    unittest.main()
