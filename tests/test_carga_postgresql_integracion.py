"""Pruebas reales en un cluster temporal propio; nunca usa configuracion.json.

Activar explícitamente SGD_PROBAR_POSTGRESQL_AISLADO=1 para ejecutarlas.
Utiliza el motor incluido, un puerto libre y únicamente datos ficticios.
"""
from pathlib import Path
import copy,csv,json,os,re,socket,subprocess,sys,tempfile,unittest

sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
import gestion_data as g
import recargar_usuarios as recarga
import reiniciar_piloto as reinicio
from types import SimpleNamespace
from unittest.mock import patch


@unittest.skipUnless(os.environ.get('SGD_PROBAR_POSTGRESQL_AISLADO')=='1',
                     'Pruebas PostgreSQL aisladas no solicitadas')
class CargaPostgreSQLTests(unittest.TestCase):
    @classmethod
    def run_tool(cls,name,*args,input=None,check=True):
        env=os.environ.copy()
        for key in ['PGPASSWORD','PGHOST','PGPORT','PGUSER','PGDATABASE','PGSERVICE','PGSERVICEFILE']:
            env.pop(key,None)
        env.update(PGCLIENTENCODING='UTF8',PGCONNECT_TIMEOUT='5')
        if os.name!='nt':env['LD_LIBRARY_PATH']=str(cls.home/'lib')+os.pathsep+env.get('LD_LIBRARY_PATH','')
        command=[str(cls.home/'bin'/(name+('.exe' if os.name=='nt' else ''))),*map(str,args)]
        # En Windows el servidor puede heredar PIPE de pg_ctl y bloquear communicate.
        # Su salida de arranque ya se escribe en postgres.log de este cluster propio.
        output=dict(stdout=subprocess.DEVNULL,stderr=subprocess.DEVNULL) if name=='pg_ctl' else dict(capture_output=True)
        return subprocess.run(command,input=input,env=env,text=True,encoding='utf-8',errors='replace',
             check=check,timeout=60,creationflags=subprocess.CREATE_NO_WINDOW if os.name=='nt' else 0,**output)

    @classmethod
    def query(cls,db,sql,check=True):
        return cls.run_tool('psql','-h','127.0.0.1','-p',cls.port,'-U','postgres','-d',db,
                            '-X','-qAt','-v','ON_ERROR_STOP=1','-f','-',input=sql,check=check)

    @classmethod
    def setUpClass(cls):
        cls.home=g.ROOT/'bd/postgresql'/('windows' if os.name=='nt' else 'linux')
        if not (cls.home/'bin'/('initdb.exe' if os.name=='nt' else 'initdb')).exists():
            raise unittest.SkipTest('Extraer primero el motor incluido para este sistema')
        cls.temp=tempfile.TemporaryDirectory(prefix='sgd-pg-qa-')
        cls.path=Path(cls.temp.name)
        cls.cluster=cls.path/'cluster'
        with socket.socket() as sock:
            sock.bind(('127.0.0.1',0));cls.port=sock.getsockname()[1]
        cls.started=False
        try:
            cls.run_tool('initdb','-D',cls.cluster,'-U','postgres','--auth=trust','--encoding=UTF8','--locale=C')
            cls.run_tool('pg_ctl','-D',cls.cluster,'-l',cls.path/'postgres.log',
                         '-o',f'-h 127.0.0.1 -p {cls.port}','-w','-t','15','start')
            cls.started=True
            cls.run_tool('createdb','-h','127.0.0.1','-p',cls.port,'-U','postgres','qa_base')
            header=''.join('\\set '+name+" 'ABCD'\n" for name in ['clave','clave_mesa_demo','clave_area_demo','clave_jefe_demo'])
            header+="\\set ruta '"+(cls.path/'documentos').as_posix()+"'\n"
            sql=(g.ROOT/'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
            seed=(g.ROOT/'bd/02-inia-usuarios-ficticios.sql').read_text(encoding='utf-8')
            cls.query('qa_base','BEGIN;\n'+header+sql+'\n'+seed+
                "\nCREATE TABLE idosgd.instalacion_portable(version text PRIMARY KEY);"
                "INSERT INTO idosgd.instalacion_portable VALUES('1');\nCOMMIT;")
        except BaseException:
            if cls.started:cls.run_tool('pg_ctl','-D',cls.cluster,'-m','fast','-w','-t','15','stop',check=False)
            cls.temp.cleanup();raise

    @classmethod
    def tearDownClass(cls):
        try:
            if cls.started:cls.run_tool('pg_ctl','-D',cls.cluster,'-m','fast','-w','-t','15','stop')
        finally:cls.temp.cleanup()

    def setUp(self):
        self.db='qa_'+self._testMethodName.removeprefix('test_')
        self.run_tool('createdb','-h','127.0.0.1','-p',self.port,'-U','postgres','-T','qa_base',self.db)
        self.area=json.loads((g.ROOT/'bd/piloto-area.json.ejemplo').read_text(encoding='utf-8'))
        self.area.update(codigo='51011',nombre='AREA FICTICIA QA',sigla='QA',nombre_corto='QA',
                         aprobado='SI',verificado_por='OPERADOR QA',titular_empleado='52011')
        self.area['sede']=dict(codigo='001',nombre='INSTITUTO NACIONAL DE INNOVACIÓN AGRARIA',
                              direccion='Av. La Molina Nº 1981, La Molina, Lima')
        user=dict(zip(g.FIELDS,['11','52011','qa.operador','Nombre QA','Apellido QA','Segundo QA',
               '12345678','qa@ejemplo.invalid','0003','ESPECIALISTA','USUARIO_AREA','SI','1']))
        excluded={k:'' for k in g.FIELDS};excluded.update(incluir_sgd='0',verificado='NO')
        folder=self.path/self.db;folder.mkdir()
        (folder/'area.json').write_text(json.dumps(self.area,ensure_ascii=False),encoding='utf-8')
        with (folder/'usuarios-verificados.csv').open('w',encoding='utf-8',newline='') as handle:
            writer=csv.DictWriter(handle,fieldnames=g.FIELDS);writer.writeheader();writer.writerows([user,excluded])
        self.area,self.users,self.fingerprint=g.read_bundle(folder)
        self.sql=g.load_sql(self.area,self.users,self.fingerprint,{'qa.operador':'ABCD'})

    def legacy(self):
        self.query(self.db,"UPDATE idosgd.si_mae_local SET de_nombre_local='INSTALACION LOCAL INIA',"
                   "de_direccion_local='PENDIENTE' WHERE ccod_local='001';")

    def test_reinicio_completo_con_respaldo(self):
        # Cada comando usa exclusivamente el puerto del cluster temporal.
        self.query(self.db, self.sql)
        self.query('postgres', "CREATE ROLE sgd_app LOGIN PASSWORD 'QA';")
        self.run_tool('createdb','-h','127.0.0.1','-p',self.port,'-U','postgres','-T',self.db,'idosgd_inia')
        root=self.path/'instalacion-reinicio'; data=root/'datos'; docs=data/'documentos'
        docs.mkdir(parents=True); (docs/'anterior.txt').write_text('DOCUMENTO QA',encoding='utf-8')
        header=''.join('\\set '+name+" 'ABCD'\n" for name in ['clave','clave_mesa_demo','clave_area_demo','clave_jefe_demo'])
        initial=header+(g.ROOT/'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
        initial+='\n'+(g.ROOT/'bd/02-inia-usuarios-ficticios.sql').read_text(encoding='utf-8').replace(":'ruta'", "'QA'")
        initial+="\nCREATE TABLE idosgd.instalacion_portable(version text PRIMARY KEY);"
        initial+="\nCREATE ROLE sgd_app LOGIN PASSWORD 'QA';\nGRANT USAGE ON SCHEMA idosgd TO sgd_app;\n"
        def psql(sql,db=None,**kwargs):
            return self.query(db or 'idosgd_inia',sql)
        def base():
            if self.query('postgres',"SELECT EXISTS(SELECT 1 FROM pg_database WHERE datname='idosgd_inia')").stdout.strip()=='f':
                self.query('postgres','CREATE DATABASE idosgd_inia;')
        def backup(work):
            target=work/'antes-de-aplicar.dump'
            self.run_tool('pg_dump','-h','127.0.0.1','-p',self.port,'-U','postgres','-d','idosgd_inia','-Fc','-f',target)
            return target
        backend=SimpleNamespace(ROOT=root,DATA=data,
            C=dict(modo='completo',base='idosgd_inia',host_bd='127.0.0.1',puerto_bd=55432),
            detener=lambda:None, admin=lambda args:SimpleNamespace(stdout='sgd not running'),
            base=base,scalar=lambda sql:psql(sql).stdout.strip(),generar_sql=lambda:initial,
            psql=psql,pg_tool=lambda name,args:self.run_tool(name,*args))
        with patch.object(g,'backup',side_effect=backup):
            reinicio.reiniciar(aplicar=True,confirmar=reinicio.TOKEN,_sgd=backend)
        self.assertEqual(psql('SELECT count(*) FROM idosgd.seg_usuarios1;').stdout.strip(),'4')
        self.assertEqual(psql('SELECT count(*) FROM idosgd.tdtv_remitos;').stdout.strip(),'0')
        self.assertEqual(list(docs.iterdir()),[])
        work=next(data.glob('reinicio-piloto-*'))
        self.assertEqual((work/'documentos-anteriores/anterior.txt').read_text(encoding='utf-8'),'DOCUMENTO QA')
        self.run_tool('createdb','-h','127.0.0.1','-p',self.port,'-U','postgres','qa_respaldo_reinicio')
        self.run_tool('pg_restore','-h','127.0.0.1','-p',self.port,'-U','postgres','-d','qa_respaldo_reinicio',work/'antes-de-aplicar.dump')
        self.assertEqual(self.query('qa_respaldo_reinicio','SELECT count(*) FROM idosgd.seg_usuarios1;').stdout.strip(),'5')

    def site(self):
        return json.loads(self.query(self.db,"SELECT json_build_object('nombre',de_nombre_local,"
            "'direccion',de_direccion_local,'estado',es_local,'creador',user_creator,'fecha',date_creator) "
            "FROM idosgd.si_mae_local WHERE ccod_local='001';").stdout)

    def test_instalacion_nueva(self):
        self.assertEqual(self.site()['nombre'],self.area['sede']['nombre'])
        self.assertEqual(self.site()['direccion'],self.area['sede']['direccion'])
        self.query(self.db,self.sql)
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.seg_usuarios1;").stdout.strip(),'5')
        self.assertEqual(self.query(self.db,"SELECT ccar_descar FROM idosgd.rhtm_cargos WHERE ccar_co_cargo='0003';").stdout.strip(),'ESPECIALISTA')

    def test_sede_provisional(self):
        self.legacy();before=self.site();self.query(self.db,self.sql);after=self.site()
        self.assertEqual(after['nombre'],self.area['sede']['nombre'])
        self.assertEqual(after['direccion'],self.area['sede']['direccion'])
        for key in ['estado','creador','fecha']:self.assertEqual(after[key],before[key])

    def test_repeticion(self):
        self.legacy();self.query(self.db,self.sql);before=self.site()
        again=self.query(self.db,self.sql)
        self.assertIn('Carga identica',again.stderr)
        self.assertEqual(self.site(),before)
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.seg_usuarios1;").stdout.strip(),'5')

    def test_conflicto_sede(self):
        self.query(self.db,"UPDATE idosgd.si_mae_local SET de_nombre_local='OTRA SEDE',de_direccion_local='OTRA DIRECCION' WHERE ccod_local='001';")
        before=self.site();result=self.query(self.db,self.sql,check=False)
        self.assertNotEqual(result.returncode,0);self.assertIn('Conflicto de datos existentes',result.stderr)
        self.assertEqual(self.site(),before)
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.rhtm_dependencia WHERE co_dependencia='51011';").stdout.strip(),'0')

    def test_error_posterior_revierte_sede(self):
        self.legacy();before=self.site()
        self.query(self.db,"INSERT INTO idosgd.rhtm_cargos VALUES('OTRO CARGO','1','0003');")
        result=self.query(self.db,self.sql,check=False)
        self.assertNotEqual(result.returncode,0)
        self.assertEqual(self.site(),before)
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.seg_usuarios1 WHERE cod_user='qa.operador';").stdout.strip(),'0')
        self.assertEqual(self.query(self.db,"SELECT to_regclass('idosgd.piloto_areas') IS NULL;").stdout.strip(),'t')

    def test_retiro_conserva_sede_y_cargo(self):
        self.legacy();self.query(self.db,self.sql);before=self.site()
        result=self.query(self.db,g.clean_sql(self.area['codigo']),check=False)
        self.assertEqual(result.returncode,0,result.stderr)
        self.assertEqual(self.site(),before)
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.rhtm_cargos WHERE ccar_co_cargo='0003';").stdout.strip(),'1')
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.seg_usuarios1;").stdout.strip(),'4')

    def test_retiro_bloquea_referencia_real(self):
        self.query(self.db,self.sql)
        self.query(self.db,"CREATE TABLE idosgd.historial_prueba(co_dep varchar(5) REFERENCES idosgd.rhtm_dependencia(co_dependencia));"
                   "INSERT INTO idosgd.historial_prueba VALUES('51011');")
        result=self.query(self.db,g.clean_sql(self.area['codigo']),check=False)
        self.assertNotEqual(result.returncode,0)
        self.assertIn('historial_prueba.co_dep',result.stderr)
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.rhtm_dependencia WHERE co_dependencia='51011';").stdout.strip(),'1')
        self.assertEqual(self.query(self.db,"SELECT count(*) FROM idosgd.seg_usuarios1 WHERE cod_user='qa.operador';").stdout.strip(),'1')

    def legacy_access(self):
        legacy = copy.deepcopy(self.area)
        legacy['perfiles']['USUARIO_AREA']['acceso_documentos'] = 'dependencia'
        self.query(self.db, g.load_sql(legacy, self.users, self.fingerprint, {'qa.operador': 'ABCD'}))

    def reload(self, execution='qa-recarga', items=None, check=True):
        items = items or [(self.path, self.area, self.users, self.fingerprint)]
        return self.query(self.db, recarga.reload_sql(items, execution, self.db), check=check)

    def test_recarga_conserva_cuenta_clave_y_documentos(self):
        self.legacy_access()
        before = self.query(self.db, "SELECT to_jsonb(u)::text FROM idosgd.seg_usuarios1 u WHERE cod_user='qa.operador';").stdout
        self.query(self.db, "CREATE TABLE idosgd.documento_qa(id integer PRIMARY KEY,contenido text);"
                   "INSERT INTO idosgd.documento_qa VALUES(1,'DOCUMENTO CONSERVADO');")
        result = self.reload()
        self.assertIn('"permisos_actualizados": 1', result.stdout)
        self.assertEqual(self.query(self.db, "SELECT ti_acceso FROM idosgd.tdtx_config_emp WHERE co_emp='52011' AND co_dep='51011';").stdout.strip(), '1')
        self.assertEqual(self.query(self.db, "SELECT to_jsonb(u)::text FROM idosgd.seg_usuarios1 u WHERE cod_user='qa.operador';").stdout, before)
        self.assertEqual(self.query(self.db, "SELECT contenido FROM idosgd.documento_qa;").stdout.strip(), 'DOCUMENTO CONSERVADO')
        self.assertEqual(self.query(self.db, "SELECT datos->>'ti_acceso' FROM idosgd.piloto_objetos WHERE tabla='tdtx_config_emp';").stdout.strip(), '1')
        self.assertEqual(self.query(self.db, "SELECT acceso_anterior||','||acceso_nuevo FROM idosgd.piloto_recargas_acceso;").stdout.strip(), '0,1')

    def test_recarga_repetida_no_duplica_ni_cambia_usuarios(self):
        self.legacy_access()
        self.reload()
        again = self.reload('qa-recarga-2')
        self.assertIn('"permisos_actualizados": 0', again.stdout)
        self.assertEqual(self.query(self.db, "SELECT count(*) FROM idosgd.piloto_recargas_acceso;").stdout.strip(), '1')
        self.assertEqual(self.query(self.db, "SELECT count(*) FROM idosgd.seg_usuarios1;").stdout.strip(), '5')
        self.assertEqual(self.query(self.db, g.clean_sql(self.area['codigo']), check=False).returncode, 0)

    def test_recarga_lote_conflicto_revierte_todas_las_oficinas(self):
        self.legacy_access()
        other = copy.deepcopy(self.area)
        other['codigo'] = '51012'
        other['perfiles']['USUARIO_AREA']['acceso_documentos'] = 'dependencia'
        self.query(self.db, g.load_sql(other, self.users, 'huella-qa-otra-oficina', {'qa.operador': 'ABCD'}))
        self.query(self.db, "UPDATE idosgd.tdtx_config_emp SET ti_acceso='2' WHERE co_dep='51012' AND co_emp='52011';")
        items = [(self.path, self.area, self.users, self.fingerprint),
                 (self.path, other, self.users, 'huella-qa-otra-oficina')]
        other['perfiles']['USUARIO_AREA']['acceso_documentos'] = 'personal'
        failed = self.reload(items=items, check=False)
        self.assertNotEqual(failed.returncode, 0)
        self.assertIn('Permiso cambiado manualmente', failed.stderr)
        self.assertEqual(self.query(self.db, "SELECT co_dep||','||ti_acceso FROM idosgd.tdtx_config_emp WHERE co_emp='52011' ORDER BY co_dep;").stdout.strip(), '51011,0\n51012,2')
        self.assertEqual(self.query(self.db, "SELECT to_regclass('idosgd.piloto_recargas_acceso') IS NULL;").stdout.strip(), 't')

    def test_recarga_excepcion_explicita_conserva_acceso_de_area(self):
        self.legacy_access()
        self.area['perfiles']['USUARIO_AREA']['acceso_documentos'] = 'dependencia'
        result = self.reload()
        self.assertIn('"permisos_actualizados": 0', result.stdout)
        self.assertEqual(self.query(self.db, "SELECT ti_acceso FROM idosgd.tdtx_config_emp WHERE co_emp='52011' AND co_dep='51011';").stdout.strip(), '0')

    def test_recarga_identidad_incorrecta_y_base_distinta_se_bloquean(self):
        self.legacy_access()
        bad = copy.deepcopy(self.users)
        bad[0]['dni'] = '87654321'
        result = self.reload(items=[(self.path,self.area,bad,self.fingerprint)], check=False)
        self.assertNotEqual(result.returncode, 0)
        sql = recarga.reload_sql([(self.path,self.area,self.users,self.fingerprint)], 'qa-base', 'otra_base')
        self.assertNotEqual(self.query(self.db, sql, check=False).returncode, 0)
        self.assertEqual(self.query(self.db, "SELECT ti_acceso FROM idosgd.tdtx_config_emp WHERE co_emp='52011' AND co_dep='51011';").stdout.strip(), '0')

    def test_recarga_cincuenta_oficinas_en_una_transaccion(self):
        items = []
        for index in range(50):
            area = copy.deepcopy(self.area)
            area['codigo'] = str(51011 + index)
            legacy = copy.deepcopy(area)
            legacy['perfiles']['USUARIO_AREA']['acceso_documentos'] = 'dependencia'
            fingerprint = 'huella-qa-oficina-' + str(index)
            self.query(self.db, g.load_sql(legacy, self.users, fingerprint, {'qa.operador': 'ABCD'}))
            items.append((self.path, area, self.users, fingerprint))
        result = self.reload(items=items)
        self.assertIn('"oficinas": 50', result.stdout)
        self.assertIn('"usuarios_comprobados": 50', result.stdout)
        self.assertIn('"permisos_actualizados": 50', result.stdout)
        self.assertEqual(self.query(self.db, "SELECT count(*) FROM idosgd.tdtx_config_emp WHERE co_emp='52011' AND ti_acceso='1';").stdout.strip(), '50')
        self.assertEqual(self.query(self.db, "SELECT count(*) FROM idosgd.seg_usuarios1;").stdout.strip(), '5')


if __name__=='__main__':unittest.main()
