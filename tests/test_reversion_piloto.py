"""Ensayo del retiro en motor temporal: requiere un dump elegido explicitamente.

Nunca usa la conexion del operador ni restaura sobre su base.
"""
from pathlib import Path
import hashlib,json,os,subprocess,sys,unittest,uuid

sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
import test_carga_postgresql_integracion as motor
import limpiar_tramites

ROOT=Path(__file__).resolve().parents[1]
SQL=ROOT/'bd/mantenimiento/retirar-derivacion-secretaria-piloto.sql'


@unittest.skipUnless(os.environ.get('SGD_PROBAR_POSTGRESQL_AISLADO')=='1' and os.environ.get('SGD_RESPALDO_REVERSION'),
                     'Requiere motor aislado y respaldo de ensayo explicito')
class ReversionPilotoTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.backup=Path(os.environ['SGD_RESPALDO_REVERSION']).resolve()
        if not cls.backup.is_file():raise RuntimeError('No existe el dump explicito de ensayo')
        cls.qa=motor.CargaPostgreSQLTests;cls.qa.setUpClass()

    @classmethod
    def tearDownClass(cls):cls.qa.tearDownClass()

    def setUp(self):
        self.db='qa_retiro_'+uuid.uuid4().hex[:10]
        self.qa.run_tool('createdb','-h','127.0.0.1','-p',self.qa.port,'-U','postgres',self.db)
        self.qa.run_tool('pg_restore','-h','127.0.0.1','-p',self.qa.port,'-U','postgres',
                         '--no-owner','--no-acl','--exit-on-error','-d',self.db,self.backup)

    def sql(self,sql,check=True):
        r=self.qa.query(self.db,sql,False)
        if check:self.assertEqual(r.returncode,0,r.stderr)
        return r

    def scalar(self,sql):return self.sql(sql).stdout.strip()

    def retirar(self):self.sql(SQL.read_text(encoding='utf8'))

    def normales(self):
        return self.scalar("SELECT md5(coalesce(json_agg(r ORDER BY nu_ann,nu_emi)::text,'')) FROM idosgd.tdtv_remitos r WHERE co_tip_doc_adm<>'990'")

    def test_retiro_piloto_completo_y_datos_normales_conservados(self):
        before=self.normales()
        usuarios=self.scalar('SELECT md5(json_agg(u ORDER BY cod_user)::text) FROM idosgd.seg_usuarios1 u')
        self.retirar()
        self.assertEqual(self.normales(),before)
        self.assertEqual(self.scalar('SELECT md5(json_agg(u ORDER BY cod_user)::text) FROM idosgd.seg_usuarios1 u'),usuarios)
        self.assertEqual(self.scalar("SELECT count(*) FROM pg_tables WHERE schemaname='idosgd' AND tablename IN ('tdtr_permiso_derivar','tdtv_derivacion_secretaria')"),'0')
        self.assertEqual(self.scalar("SELECT count(*) FROM pg_proc p JOIN pg_namespace n ON n.oid=p.pronamespace WHERE n.nspname='idosgd' AND p.proname LIKE 'sgd_%deriva%'"),'0')
        self.assertEqual(self.scalar("SELECT count(*) FROM pg_trigger WHERE tgname IN ('sgd_referencia_guard_derivacion','sgd_proteger_derivacion')"),'0')
        self.assertEqual(self.scalar("SELECT count(*) FROM idosgd.si_mae_tipo_doc WHERE cdoc_tipdoc='990'"),'0')
        self.assertEqual(self.scalar("SELECT count(*) FROM idosgd.si_mae_tipo_doc WHERE cdoc_tipdoc='232' AND cdoc_desdoc='PROVEIDO'"),'1')
        self.assertEqual(self.scalar("SELECT count(*) FROM idosgd.tdtv_remitos WHERE co_tip_doc_adm='990'"),'0')
        self.assertEqual(self.scalar("SELECT count(*) FROM idosgd.tdtr_referencia f WHERE NOT EXISTS(SELECT 1 FROM idosgd.tdtv_remitos r WHERE r.nu_ann=f.nu_ann_ref AND r.nu_emi=f.nu_emi_ref)"),'0')

    def test_retiro_repetible(self):
        self.retirar();before=self.normales();self.retirar();self.assertEqual(self.normales(),before)

    def test_dependencia_no_prevista_revierte_transaccion(self):
        self.sql('CREATE VIEW public.qa_dependencia_retiro AS SELECT * FROM idosgd.tdtv_derivacion_secretaria;')
        before=self.normales();r=self.sql(SQL.read_text(encoding='utf8'),False)
        self.assertNotEqual(r.returncode,0)
        self.assertEqual(self.normales(),before)
        self.assertEqual(self.scalar("SELECT to_regclass('idosgd.tdtv_derivacion_secretaria') IS NOT NULL"),'t')
        self.assertEqual(self.scalar("SELECT count(*) FROM pg_trigger WHERE tgname IN ('sgd_referencia_guard_derivacion','sgd_proteger_derivacion')"),'4')

    def test_limpiador_anterior_funciona_tras_retiro(self):
        self.retirar()
        usuarios=self.scalar('SELECT md5(json_agg(u ORDER BY cod_user)::text) FROM idosgd.seg_usuarios1 u')
        self.sql(limpiar_tramites.sql_limpieza('QA-RETIRO'))
        self.assertEqual(self.scalar('SELECT count(*) FROM idosgd.tdtv_remitos'),'0')
        self.assertEqual(self.scalar('SELECT md5(json_agg(u ORDER BY cod_user)::text) FROM idosgd.seg_usuarios1 u'),usuarios)


if __name__=='__main__':unittest.main()
