"""Pruebas offline. No requieren ni consultan ninguna base de datos."""
from pathlib import Path
import copy, csv, hashlib, io, json, re, sys, tempfile, unittest
from unittest.mock import patch
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
import gestion_data as g


class CargaTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory(); self.folder=Path(self.tmp.name)
        self.area=json.loads((g.ROOT/'bd/piloto-area.json.ejemplo').read_text(encoding='utf-8'))
        self.area['aprobado']='SI'; self.area['verificado_por']='OPERADOR QA'
        self.area['nombre']='UTI QA';self.area['sede'].update(nombre='SEDE QA',direccion='DIRECCION QA')
        self.user=dict(zip(g.FIELDS,['1','52001','qa.uno',"José O'Prueba",'Pérez','García',
                                   '12345678','qa@ejemplo.invalid','1001','ANALISTA','USUARIO_AREA','SI']))
        self.write()

    def tearDown(self): self.tmp.cleanup()

    def write(self,users=None):
        (self.folder/'area.json').write_text(json.dumps(self.area,ensure_ascii=False),encoding='utf-8-sig')
        with (self.folder/'usuarios-verificados.csv').open('w',encoding='utf-8-sig',newline='') as f:
            w=csv.DictWriter(f,fieldnames=g.FIELDS);w.writeheader();w.writerows(users or [self.user])

    def test_verificados_y_no_verificados(self):
        a,u,h=g.read_bundle(self.folder); self.assertEqual(len(u),1)
        self.user['verificado']='NO'; self.write()
        with self.assertRaisesRegex(ValueError,'no verificado'): g.read_bundle(self.folder)

    def test_aprobacion_del_area_obligatoria(self):
        self.area['aprobado']='NO';self.write()
        with self.assertRaisesRegex(ValueError,'aprobado'):g.read_bundle(self.folder)

    def test_identidad_limites_y_menus(self):
        for field,value in [('dni','AB-000001'),('dni','00000000'),('usuario','x'*21),
                            ('codigo_empleado','00000'),('cargo_codigo','0000')]:
            with self.subTest(field=field,value=value):
                old=self.user[field];self.user[field]=value;self.write()
                with self.assertRaises(ValueError):g.read_bundle(self.folder)
                self.user[field]=old
        self.area['perfiles']['USUARIO_AREA']['opciones']=['M020101'];self.write()
        with self.assertRaisesRegex(ValueError,'menús padres'):g.read_bundle(self.folder)

    def test_duplicado_usuario_mayusculas_y_origen(self):
        second=copy.deepcopy(self.user);second.update(origen_id='2',codigo_empleado='52002',dni='87654321',usuario='QA.UNO')
        self.write([self.user,second])
        with self.assertRaisesRegex(ValueError,'duplicado de usuario'):g.read_bundle(self.folder)

    def test_huella_detecta_cambios(self):
        before=g.read_bundle(self.folder)[2]; self.user['email']='otro@ejemplo.invalid';self.write()
        self.assertNotEqual(before,g.read_bundle(self.folder)[2])

    def test_revision_nunca_conecta_y_confirmacion_precede_respaldo(self):
        with patch.object(g,'DATA',self.folder/'salidas'),patch.object(g,'private_folder',lambda p:p.mkdir(parents=True)),\
             patch.object(g,'backup') as backup,patch.object(g,'execute_sql') as execute,patch('sys.stdout',new=io.StringIO()):
            g.main(['cargar','--carpeta',str(self.folder)])
            backup.assert_not_called();execute.assert_not_called()
            with self.assertRaisesRegex(ValueError,'Confirmación'):
                g.main(['cargar','--carpeta',str(self.folder),'--aplicar'])
            backup.assert_not_called();execute.assert_not_called()
            g.main(['limpiar','--area','51001'])
            backup.assert_not_called();execute.assert_not_called()

    def test_fallo_respaldo_impide_carga_y_retiro(self):
        with patch.object(g,'DATA',self.folder/'salidas'),patch.object(g,'private_folder',lambda p:p.mkdir(parents=True)),\
             patch.object(g,'backup',side_effect=RuntimeError('respaldo')) as backup,\
             patch.object(g,'execute_sql') as execute,patch.object(g,'make_passwords') as passwords,\
             patch('sys.stdout',new=io.StringIO()):
            for args in [['cargar','--carpeta',str(self.folder),'--confirmar','CARGAR:51001'],
                         ['limpiar','--area','51001','--confirmar','RETIRAR:51001']]:
                with self.assertRaisesRegex(RuntimeError,'respaldo'):g.main(args+['--aplicar'])
            self.assertEqual(backup.call_count,2);execute.assert_not_called();passwords.assert_not_called()

    def test_claves_solo_para_cuentas_realmente_nuevas(self):
        with patch.object(g,'DATA',self.folder/'salidas'),patch.object(g,'private_folder',lambda p:p.mkdir(parents=True)),\
             patch.object(g,'backup',return_value=self.folder/'respaldo.dump'),\
             patch.object(g,'make_passwords',return_value=({'qa.uno':'clave-qa'},{'qa.uno':'ABCD'})),\
             patch.object(g,'execute_sql',return_value={'cuentas_nuevas':[]}),patch('sys.stdout',new=io.StringIO()):
            g.main(['cargar','--carpeta',str(self.folder),'--aplicar','--confirmar','CARGAR:51001'])
            files=list((self.folder/'salidas').glob('*/CLAVES-GENERADAS-PRIVADAS.json'))
            self.assertEqual(json.loads(files[0].read_text()),{})

    def test_tablas_y_columnas_contra_modelo_local(self):
        schema=(g.ROOT/'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
        model={t:set(re.findall(r'^\s+(\w+)\s',b,re.M)) for t,b in
               re.findall(r'CREATE TABLE idosgd\.(\w+) \((.*?)\n\);',schema,re.S)}
        a,u,h=g.read_bundle(self.folder);sql=g.load_sql(a,u,h)
        self.assertTrue(set(g.TABLES)<=set(model))
        calls=re.findall(r"PERFORM pg_temp.registrar\('[^']*','([^']*)','((?:[^']|'')*)'::jsonb,'((?:[^']|'')*)'::jsonb\);",sql)
        self.assertGreater(len(calls),15)
        for table,key,values in calls:
            values=json.loads(values.replace("''", "'"));key=json.loads(key.replace("''", "'"))
            self.assertTrue(set(values)<=model[table],table)
            self.assertTrue(set(key)<=model[table],table)
        self.assertIn("José O''Prueba",sql)
        keys={t:set(x.strip() for x in cols.split(',')) for t,cols in
              re.findall(r'ALTER TABLE ONLY idosgd\.(\w+)\s+ADD CONSTRAINT \w+ PRIMARY KEY \(([^)]+)\)',schema)}
        # Esta relación del proveedor no declara PK; usar su clave funcional y rechazar duplicados.
        keys['tdtx_dependencia_empleado']={'co_dep','co_emp'}
        for table,key,values in calls:
            self.assertEqual(set(json.loads(key.replace("''", "'"))),keys[table],table)

    def test_sql_retiro_no_borra_historia_y_conserva_compartidos(self):
        sql=g.clean_sql('51001')
        self.assertIn('otro.area<>',sql);self.assertIn('IN EXCLUSIVE MODE',sql)
        self.assertIn("NOT IN ('si_mae_local','rhtm_cargos')",sql)
        self.assertNotRegex(sql,r'(?i)\b(TRUNCATE|CASCADE)\s*[;\n]')
        with self.assertRaises(ValueError):g.code("51001'; DELETE",'area',5)


if __name__=='__main__':unittest.main()
