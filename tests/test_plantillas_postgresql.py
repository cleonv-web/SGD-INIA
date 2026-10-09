"""Prueba optativa: PostgreSQL Docker aislado, sin puertos ni datos reales.

Activar exclusivamente en un equipo de prueba con SGD_TEST_DOCKER=1.
No conecta con PostgreSQL instalado ni con contenedores existentes.
"""
from pathlib import Path
import os
import re
import subprocess
import sys
import time
import unittest
import uuid

sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
import sgd


@unittest.skipUnless(os.environ.get('SGD_TEST_DOCKER')=='1','Requiere SGD_TEST_DOCKER=1 en equipo de prueba')
class PlantillasPostgresqlTest(unittest.TestCase):
    def test_transaccion_idempotencia_y_conflicto(self):
        name='sgd-plantillas-test-'+uuid.uuid4().hex[:12]
        def docker(*args,check=True,input=None):
            return subprocess.run(['docker',*args],input=input,capture_output=True,text=True,encoding='utf-8',check=check)
        def sql(text,check=True):
            return docker('exec','-i',name,'psql','-U','postgres','-X','-v','ON_ERROR_STOP=1','-At',input=text,check=check)
        try:
            docker('run','-d','--name',name,'--network','none','-e','POSTGRES_HOST_AUTH_METHOD=trust','postgres:17-bookworm')
            ready=False
            for _ in range(100):
                if docker('exec',name,'pg_isready','-U','postgres',check=False).returncode==0:ready=True;break
                time.sleep(.2)
            self.assertTrue(ready,'PostgreSQL aislado no inició')
            schema=(sgd.ROOT/'bd/01-estructura-catalogos.sql').read_text(encoding='utf-8')
            table=re.search(r'CREATE TABLE idosgd\.tdtr_plantilla_docx \(.*?\n\);',schema,re.S).group()
            sql('CREATE SCHEMA idosgd;'+table+'ALTER TABLE idosgd.tdtr_plantilla_docx ADD PRIMARY KEY(co_dep,co_tipo_doc);')
            plan='BEGIN;'+sgd.sql_plantillas()+'COMMIT;'
            sql(plan);sql(plan)
            self.assertEqual(sql('SELECT count(*) FROM idosgd.tdtr_plantilla_docx;').stdout.strip(),'2')
            for code,filename,content in sgd.comprobar_plantillas():
                self.assertEqual(sql("SELECT encode(bl_doc,'hex') FROM idosgd.tdtr_plantilla_docx WHERE co_tipo_doc='"+code+"';").stdout.strip(),content.hex())
            # Un conflicto en el segundo formato revierte la inserción del primero.
            codes=[v[0] for v in sgd.comprobar_plantillas()]
            sql("TRUNCATE idosgd.tdtr_plantilla_docx; INSERT INTO idosgd.tdtr_plantilla_docx(co_dep,co_tipo_doc,bl_doc) VALUES('00000','"+codes[1]+"',decode('00','hex')); INSERT INTO idosgd.tdtr_plantilla_docx(co_dep,co_tipo_doc,bl_doc) VALUES('FICTI','003',decode('01','hex'));")
            result=sql(plan,check=False)
            self.assertNotEqual(result.returncode,0)
            self.assertIn('distinta o inactiva',result.stderr)
            self.assertEqual(sql("SELECT co_dep||':'||co_tipo_doc||':'||encode(bl_doc,'hex') FROM idosgd.tdtr_plantilla_docx ORDER BY co_dep;").stdout.strip(),'00000:'+codes[1]+':00\nFICTI:003:01')
            print('PostgreSQL aislado: blobs completos, segunda aplicación sin duplicar, conflicto revierte toda la transacción y conserva formatos por área.')
        finally:
            docker('rm','-f',name,check=False)


if __name__=='__main__':unittest.main()
