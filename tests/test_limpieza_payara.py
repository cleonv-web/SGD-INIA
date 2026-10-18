"""Pruebas nativas Windows en carpetas temporales, sin tocar el dominio real."""
from pathlib import Path
import json, os, subprocess, tempfile, unittest

ROOT=Path(__file__).resolve().parents[1]
SCRIPT=ROOT/'herramientas/Limpiar-Payara.ps1'


@unittest.skipUnless(os.name=='nt', 'Utilitario nativo PowerShell Windows')
class LimpiezaPayaraTests(unittest.TestCase):
    def setUp(self):
        self.temp=tempfile.TemporaryDirectory(prefix='sgd-payara-limpieza-')
        self.root=Path(self.temp.name)/'Proyecto [QA] con espacios'
        self.domain=self.root/'payara/payara5/glassfish/domains/sgd'
        self.protected={}
        for relative in ['config/domain.xml','config/domain.xml.bak','config/keystore.jks',
                         'lib/driver.jar','applications/__internal/sgd/sgd.war',
                         'reportes/reporte.pdf','sgdproperties/application.properties']:
            path=self.domain/relative; path.parent.mkdir(parents=True,exist_ok=True)
            path.write_bytes(b'CONSERVAR QA'); self.protected[path]=path.read_bytes()
        for relative in ['datos/documentos/documento.docx','datos/carga-util-preparada/area.json',
                         'payara/payara5/glassfish/modules/modulo.jar']:
            path=self.root/relative;path.parent.mkdir(parents=True,exist_ok=True)
            path.write_bytes(b'CONSERVAR QA');self.protected[path]=path.read_bytes()
        for name in ['generated','osgi-cache','temp','logs']:
            folder=self.domain/name/'sub [QA]';folder.mkdir(parents=True)
            (folder/'archivo temporal.txt').write_text('RETIRAR QA')

    def tearDown(self):
        self.assertTrue(Path(self.temp.name).resolve().is_relative_to(Path(tempfile.gettempdir()).resolve()))
        self.temp.cleanup()

    def invoke(self,apply=False):
        return subprocess.run(['powershell.exe','-NoProfile','-ExecutionPolicy','Bypass','-File',str(SCRIPT),
            '-RaizProyecto',str(self.root),*(['-Aplicar'] if apply else [])],capture_output=True,
            text=True,errors='replace',timeout=45,creationflags=subprocess.CREATE_NO_WINDOW)

    def check_preserved(self):
        for path,content in self.protected.items():
            self.assertEqual(path.read_bytes(),content,str(path))

    def test_revision_no_modifica(self):
        before=sorted(str(p) for p in self.root.rglob('*') if p.is_file())
        result=self.invoke()
        self.assertEqual(result.returncode,0,result.stdout+result.stderr)
        self.assertEqual(before,sorted(str(p) for p in self.root.rglob('*') if p.is_file()))
        self.check_preserved()

    def test_limpieza_conserva_aplicaciones_config_y_documentos(self):
        result=self.invoke(True)
        self.assertEqual(result.returncode,0,result.stdout+result.stderr)
        for name in ['generated','osgi-cache','temp','logs']:
            self.assertEqual(list((self.domain/name).iterdir()),[])
        self.check_preserved()
        report=json.loads((self.root/'logs/limpieza-payara.json').read_text(encoding='utf-8-sig'))
        self.assertFalse(report['postgresql_modificado'])
        self.assertFalse(report['payara_estaba_activo'])

    def test_pid_bloquea_borrado(self):
        (self.domain/'config/pid').write_text('12345')
        result=self.invoke(True)
        self.assertNotEqual(result.returncode,0)
        self.assertIn('config/pid',result.stdout+result.stderr)
        self.assertTrue((self.domain/'temp/sub [QA]/archivo temporal.txt').exists())
        self.check_preserved()

    def test_junction_externo_bloquea_toda_limpieza(self):
        external=Path(self.temp.name)/'externo';external.mkdir()
        original=external/'conservar.txt';original.write_text('CONSERVAR EXTERNO')
        link=self.domain/'temp/enlace'
        creation=subprocess.run(['powershell.exe','-NoProfile','-Command',
            'New-Item -ItemType Junction -Path $env:QA_LINK -Target $env:QA_TARGET | Out-Null'],
            env={**os.environ,'QA_LINK':str(link),'QA_TARGET':str(external)},capture_output=True,timeout=20)
        self.assertEqual(creation.returncode,0)
        try:
            result=self.invoke(True)
            self.assertNotEqual(result.returncode,0)
            self.assertIn('junction',result.stdout+result.stderr)
            self.assertEqual(original.read_text(),'CONSERVAR EXTERNO')
            self.assertTrue((self.domain/'osgi-cache/sub [QA]/archivo temporal.txt').exists())
        finally:
            self.assertTrue(link.absolute().is_relative_to(self.root.absolute()))
            subprocess.run(['powershell.exe','-NoProfile','-Command',
                '[IO.Directory]::Delete($env:QA_LINK)'],env={**os.environ,'QA_LINK':str(link)},
                check=True,capture_output=True,timeout=20)


class ReglasGitTests(unittest.TestCase):
    def test_cargas_runtime_y_documentos(self):
        with tempfile.TemporaryDirectory(prefix='sgd-git-ignore-') as folder:
            root=Path(folder)
            (root/'.gitignore').write_bytes((ROOT/'.gitignore').read_bytes())
            subprocess.run(['git','init','-q',str(root)],check=True,capture_output=True)
            allowed=['datos/carga-util-preparada/area.json','datos/carga-util-preparada/usuarios-verificados.csv',
                'datos/carga-oficina-2/pendientes.csv','datos/cargas/oficina-3/area.json',
                'datos/exportacion-uti/trabajadores.csv','INCIDENCIAS/tema/correccion.sql',
                'java/windows/jdk8/bin/java.exe','java/linux-x64.tar.gz',
                'bd/postgresql/windows/bin/postgres.exe','bd/postgresql-linux-x64.tar.gz',
                'herramientas/python-windows/python.exe','herramientas/python-linux.tar.gz',
                'herramientas/maven/bin/mvn.cmd','herramientas/maven-repo/org/qa/1/qa-1.jar',
                'INCIDENCIAS/tema/correccion.zip','INCIDENCIAS/tema/evidencia.log',
                'plantillas-docx/INFORME-INIA.docx','manual/Manual-Instalacion-SGD-INIA-PG-Actualizado.docx',
                'binarios/proveedor/wstradoc.war','payara/configuracion/dominio/config/domain.xml',
                'payara/payara5/glassfish/modules/modulo.jar',
                'payara/payara5/glassfish/lib/install/applications/ejb-timer-service-app.war',
                'payara/payara5/glassfish/lib/install/applications/metro/wstx-services.war',
                'payara/payara5/mq/lib/imqums.war','payara/payara5/mq/lib/imqhttp.war',
                'payara/payara5/mq/lib/imqhttps.war','cliente-windows/InstallerFirmaONPE_v1.2.7-24-05-2022.msi']
            ignored=['datos/documentos/doc.pdf','datos/documentos/doc.docx','datos/credenciales.json',
                'datos/postgresql/base/1/data','datos/reinicio-piloto-QA/documentos-anteriores/doc.docx',
                'datos/piloto-QA/CLAVES-GENERADAS-PRIVADAS.json','datos/carga-util-preparada/CLAVES-GENERADAS-PRIVADAS.json',
                'datos/carga-util-preparada/respaldo.dump','logs/ULTIMO-ERROR.txt','output/prueba.pdf',
                'binarios/sgd.war','binarios/sgd-viejo.war','binarios/proveedor/sgd.war',
                'fuentes/Sgd/target/sgd.war','payara/payara5/glassfish/domains/sgd/config/domain.xml',
                'payara/payara5/glassfish/domains/sgd/temp/doc.docx','payara/payara5/glassfish/domains/sgd/osgi-cache/bundle.jar',
                'payara/payara5/mq/examples/demo.java','payara/payara5/h2db/src/Test.java',
                'payara/payara-5.2022.5-backup.zip','output/node_modules/a.js','__pycache__/a.pyc']
            ignored += ['bd/postgresql-windows-backup.zip','herramientas/python-windows.zip',
                'java/linux/bin/java','bd/postgresql/linux/bin/postgres',
                'herramientas/python-linux/bin/python3','herramientas/maven-repo/.locks/qa.lock']
            for path in allowed+ignored:
                result=subprocess.run(['git','-C',str(root),'check-ignore','--no-index','-q',path],capture_output=True)
                self.assertIn(result.returncode,[0,1],result.stderr)
                self.assertEqual(result.returncode,0 if path in ignored else 1,path)


if __name__=='__main__':
    unittest.main()
