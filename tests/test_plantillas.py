"""Plantillas y utilitario con archivos ficticios; nunca consulta la BD real."""
from pathlib import Path
import hashlib
import json
import shutil
import sys
import tempfile
import unittest
from unittest.mock import patch
import zipfile

sys.path.insert(0, str(Path(__file__).resolve().parents[1]))
import sgd


class PlantillasTests(unittest.TestCase):
    def setUp(self):
        self.temp = tempfile.TemporaryDirectory(prefix='sgd-plantillas-')
        self.root = Path(self.temp.name)
        shutil.copytree(sgd.ROOT / 'plantillas-docx', self.root / 'plantillas-docx')
        self.data = self.root / 'datos'; self.data.mkdir()
        self.manifest = self.root / 'plantillas-docx/manifest.json'
        self.patches = [patch.object(sgd, 'ROOT', self.root), patch.object(sgd, 'DATA', self.data),
                        patch.object(sgd, 'C', {'modo':'completo'}), patch('builtins.print')]
        for p in self.patches:p.start()

    def tearDown(self):
        for p in reversed(self.patches):p.stop()
        self.temp.cleanup()

    def change(self, edit):
        m = json.loads(self.manifest.read_text(encoding='utf-8')); edit(m)
        self.manifest.write_text(json.dumps(m), encoding='utf-8')

    def test_paquete_valido(self):
        self.assertEqual({v[0] for v in sgd.comprobar_plantillas()}, {'001','003'})

    def test_archivo_alterado(self):
        (self.root / 'plantillas-docx/INFORME-INIA.docx').write_bytes(b'ALTERADO')
        with self.assertRaisesRegex(RuntimeError, 'Hash'):sgd.comprobar_plantillas()

    def test_tipos_duplicados(self):
        self.change(lambda m:m['plantillas'][0].update(tipo='001'))
        with self.assertRaisesRegex(RuntimeError, 'requiere'):sgd.comprobar_plantillas()

    def test_archivo_de_otro_tipo(self):
        self.change(lambda m:m['plantillas'][0].update(archivo='OFICIO-INIA.docx'))
        with self.assertRaisesRegex(RuntimeError, 'corresponde'):sgd.comprobar_plantillas()

    def test_nombre_fuera_del_paquete(self):
        self.change(lambda m:m['plantillas'][0].update(archivo='../externo.docx'))
        with self.assertRaisesRegex(RuntimeError, 'corresponde'):sgd.comprobar_plantillas()

    def test_macros(self):
        entry = json.loads(self.manifest.read_text())['plantillas'][0]
        path = self.root / 'plantillas-docx' / entry['archivo']
        with zipfile.ZipFile(path,'a') as z:z.writestr('word/vbaProject.bin',b'MACRO FICTICIA')
        self.change(lambda m:m['plantillas'][0].update(sha256=hashlib.sha256(path.read_bytes()).hexdigest()))
        with self.assertRaisesRegex(RuntimeError, 'macros'):sgd.comprobar_plantillas()

    def test_por_defecto_no_consulta_bd(self):
        with patch.object(sgd,'pg_tool') as pg,patch.object(sgd,'psql') as sql,patch.object(sgd,'connection') as connect:
            sgd.plantillas()
        pg.assert_not_called();sql.assert_not_called();connect.assert_not_called()
        plan = (self.data / 'plantillas-INIA.sql').read_text()
        self.assertIn('DO NOTHING',plan);self.assertIn('RAISE EXCEPTION',plan)
        self.assertNotIn('UPDATE ',plan);self.assertNotIn('DELETE ',plan)

    def test_aplicar_necesita_token(self):
        with patch.object(sgd,'sql_plantillas') as plan,self.assertRaisesRegex(RuntimeError,'PLANTILLAS:INIA'):
            sgd.plantillas(True)
        plan.assert_not_called()

    def test_token_sin_aplicar(self):
        with self.assertRaisesRegex(RuntimeError,'requiere'):sgd.plantillas(False,'PLANTILLAS:INIA')

    def test_aplicar_sin_credenciales_no_crea_bd(self):
        with patch.object(sgd,'pg_tool') as pg,patch.object(sgd,'psql') as sql,self.assertRaisesRegex(RuntimeError,'credenciales'):
            sgd.plantillas(True,'PLANTILLAS:INIA')
        pg.assert_not_called();sql.assert_not_called()
        self.assertFalse((self.data / 'credenciales.json').exists())

    def test_solo_payara(self):
        with patch.object(sgd,'C',{'modo':'solo_payara'}),patch.object(sgd,'pg_tool') as pg,self.assertRaisesRegex(RuntimeError,'PostgreSQL'):
            sgd.plantillas(True,'PLANTILLAS:INIA')
        pg.assert_not_called()

    def test_respaldo_fallido_no_aplica(self):
        (self.data / 'credenciales.json').write_text('{}')
        with patch.object(sgd,'connection',return_value=[]),patch.object(sgd,'pg_tool',side_effect=RuntimeError('FALLO FICTICIO')),patch.object(sgd,'psql') as sql,self.assertRaisesRegex(RuntimeError,'FALLO'):
            sgd.plantillas(True,'PLANTILLAS:INIA')
        sql.assert_not_called()

    def test_respaldo_antes_de_transaccion_y_no_sobrescribe(self):
        (self.data / 'credenciales.json').write_text('{}')
        standard = self.data / 'respaldo-portable.dump';standard.write_bytes(b'ANTERIOR')
        order = []
        def dump(tool,args,**kw):
            self.assertEqual(tool,'pg_dump');order.append('dump')
            Path(args[-1]).write_bytes(b'RESPALDO FICTICIO')
        def apply(sql,**kw):
            order.append('apply');self.assertTrue(kw['transaction'])
        with patch.object(sgd,'connection',return_value=[]),patch.object(sgd,'pg_tool',side_effect=dump),patch.object(sgd,'psql',side_effect=apply):
            sgd.plantillas(True,'PLANTILLAS:INIA')
        self.assertEqual(order,['dump','apply']);self.assertEqual(standard.read_bytes(),b'ANTERIOR')
        backup = next(self.data.glob('respaldo-antes-plantillas-*.dump'))
        self.assertEqual(backup.with_suffix('.dump.sha256').read_text().split()[0],hashlib.sha256(backup.read_bytes()).hexdigest())

    def test_paquete_incompleto_detiene_antes_de_servicios(self):
        self.manifest.unlink()
        for action in [sgd.todo,sgd.empaquetar]:
            with self.subTest(action=action.__name__),patch.object(sgd,'comprobar_paquete_cliente'),patch.object(sgd,'diagnosticar') as start,patch.object(sgd.zipfile,'ZipFile') as archive,self.assertRaises(FileNotFoundError):action()
            start.assert_not_called();archive.assert_not_called()


if __name__ == '__main__':unittest.main()
