"""Preparación de CSV con datos ficticios, sin conexión a bases de datos."""
from pathlib import Path
import csv, io, json, sys, tempfile, unittest
from unittest.mock import patch
sys.path.insert(0,str(Path(__file__).resolve().parents[1]))
import preparar_data as p
import gestion_data as g


class PreparacionTests(unittest.TestCase):
    def setUp(self):
        self.tmp=tempfile.TemporaryDirectory();self.root=Path(self.tmp.name)
        self.source=self.root/'exportacion';self.source.mkdir();self.out=self.root/'preparada'
        self.rows={
            'oficinas':[dict(iCodOficina='501',cNomOficina='UTI QA',cSiglaOficina='UTI',iCodUbicacion='7',es_area_piloto='1')],
            'ubicaciones':[dict(iCodUbicacion='7',cNomUbicacion='SEDE QA')],
            'trabajadores':[dict(iCodTrabajador='301',cUsuario='qa.uno',cNombresTrabajador='José QA',
                cApellidosTrabajador='Pérez García',cNumDocIdentidad='12345678',cMailTrabajador='qa@ejemplo.invalid',
                cCargo='ANALISTA, QA',oficina_piloto_seleccionada='501')],
            'perfiles':[dict(iCodPerfil='1',cDescPerfil='Perfil origen QA')],
            'asignaciones':[dict(iCodTrabajador='301',iCodOficina='501',iCodPerfil='1')]
        }
        self.write()

    def tearDown(self):self.tmp.cleanup()

    def write(self,delimiter=',',encoding='utf-8-sig'):
        for name,rows in self.rows.items():
            with (self.source/(name+'.csv')).open('w',encoding=encoding,newline='') as f:
                writer=csv.DictWriter(f,fieldnames=list(rows[0]),delimiter=delimiter)
                writer.writeheader();writer.writerows(rows)

    def prepare(self):
        with patch.object(p,'private_folder',lambda path:path.mkdir(parents=True)),\
             patch('sys.stdout',new=io.StringIO()),patch.object(g,'execute_sql') as execute,\
             patch.object(g,'backup') as backup:
            p.prepare(self.source,self.out)
            execute.assert_not_called();backup.assert_not_called()
        area=json.loads((self.out/'area.json').read_text(encoding='utf-8-sig'))
        with (self.out/'usuarios-verificados.csv').open(encoding='utf-8-sig',newline='') as f:
            reader=csv.DictReader(f);self.assertEqual(reader.fieldnames,g.FIELDS);users=list(reader)
        return area,users

    def test_preparacion_preserva_datos_y_nunca_aprueba_usuarios(self):
        area,users=self.prepare()
        self.assertEqual(area['codigo'],'00501');self.assertEqual(area['aprobado'],'NO')
        self.assertEqual(users[0]['verificado'],'NO');self.assertEqual(users[0]['perfil'],'')
        self.assertEqual(users[0]['apellido_paterno'],'Pérez García')
        self.assertEqual(users[0]['apellido_materno'],'');self.assertEqual(users[0]['cargo_nombre'],'ANALISTA, QA')
        self.assertEqual(users[0]['dni'],'12345678')
        with self.assertRaises(ValueError):g.read_bundle(self.out)

    def test_codificaciones_y_separadores_ssms(self):
        for index,(delimiter,encoding) in enumerate([(';','utf-8-sig'),('\t','utf-16'),(',','cp1252')]):
            with self.subTest(delimiter=delimiter,encoding=encoding):
                self.write(delimiter,encoding);self.out=self.root/('salida'+str(index))
                area,users=self.prepare();self.assertEqual(users[0]['nombres'],'José QA')

    def test_no_sobrescribe_revision_previa(self):
        self.prepare();marker=self.out/'usuarios-verificados.csv'
        marker.write_text('REVISION HUMANA',encoding='utf-8')
        with self.assertRaisesRegex(ValueError,'No se sobrescribe'):self.prepare()
        self.assertEqual(marker.read_text(),'REVISION HUMANA')

    def test_rechaza_areas_mezcladas_antes_de_escribir(self):
        self.rows['trabajadores'][0]['oficina_piloto_seleccionada']='502';self.write()
        with self.assertRaisesRegex(ValueError,'exportación distinta'):self.prepare()
        self.assertFalse(self.out.exists())

    def test_asignaciones_fuera_de_personal_aprobado(self):
        self.rows['asignaciones'][0]['iCodTrabajador']='999';self.write()
        with self.assertRaisesRegex(ValueError,'personas no exportadas'):self.prepare()
        self.assertFalse(self.out.exists())

    def test_falta_encabezado_o_incluye_claves(self):
        target=self.source/'trabajadores.csv'
        target.write_text('301,qa.uno,José QA\n',encoding='utf-8')
        with self.assertRaisesRegex(ValueError,'Encabezados'):self.prepare()
        self.rows['trabajadores'][0]['password']='PRUEBA NO REAL';self.write()
        with self.assertRaisesRegex(ValueError,'campos privados'):self.prepare()

    def test_documento_incompatible_y_cuenta_generica_quedan_pendientes(self):
        self.rows['trabajadores'][0].update(cNumDocIdentidad='AA-123456',cUsuario='cuenta.generica');self.write()
        area,users=self.prepare();self.assertEqual(users[0]['verificado'],'NO')
        self.assertEqual(users[0]['dni'],'AA-123456')
        pending=(self.out/'pendientes.csv').read_text(encoding='utf-8-sig')
        self.assertIn('Documento origen no es DNI',pending);self.assertIn('cuentas genéricas',pending)

    def test_revision_personal_completa_es_aceptada_offline(self):
        area,users=self.prepare()
        area.update(aprobado='SI',verificado_por='OPERADOR QA',nombre_corto='UTI',titular_empleado='00301')
        area['sede']['direccion']='DIRECCION QA'
        users[0].update(apellido_paterno='Pérez',apellido_materno='García',cargo_codigo='1001',
                        perfil='USUARIO_AREA',verificado='SI')
        (self.out/'area.json').write_text(json.dumps(area,ensure_ascii=False),encoding='utf-8')
        (self.out/'usuarios-verificados.csv').write_text(p.csv_text(users,g.FIELDS),encoding='utf-8')
        self.assertEqual(len(g.read_bundle(self.out)[1]),1)


if __name__=='__main__':unittest.main()
