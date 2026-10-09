"""Procesa DOCX con el motor incluido y datos ficticios, sin BD ni cliente."""
from pathlib import Path
import os
import subprocess
import tempfile
import unittest
import zipfile

ROOT = Path(__file__).resolve().parents[1]
JAVA_TEST = r'''
import java.io.*;
import fr.opensagres.xdocreport.document.*;
import fr.opensagres.xdocreport.document.registry.*;
import fr.opensagres.xdocreport.template.*;

public class PlantillasDocxTest {
 public static void main(String[] args) throws Exception {
  String[] keys={"NOMBRE_ANIO","NOMBRE_ANIO2","NUMERO_DOC","DEPENDENCIA_TITULO","NRO_EXPEDIENTE","ASUNTO","REFERENCIA","FECHA_DOC","EMPLEADO_EMITE","EMPLEADO_DESTINO","CARGO_EMP_EMITE","CARGO_EMP_DESTINO","DEPENDENCIA_EMITE","DEPENDENCIA_DESTINO","NOMBRE_DESTINATARIO","CARGO","ENTIDAD_PRIVADA_DESTINATARIO","DIRECCION_DESTINATARIO","INICIALES_EMP","COPIA"};
  for (String kind:new String[]{"INFORME","OFICIO"}) {
   for(String test:new String[]{"interno","externo","vacio"}) {
    IXDocReport report;
    try(InputStream in=new FileInputStream(new File(args[0],kind+"-INIA.docx"))) {
     report=XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Freemarker);
    }
    IContext ctx=report.createContext();
    for(String k:keys)ctx.put(k,"");
    if(!test.equals("vacio")) {
     ctx.put("NOMBRE_ANIO","DENOMINACION ANUAL FICTICIA PARA VALIDACION");
     ctx.put("NUMERO_DOC","N° FICTICIO-2026-INIA");ctx.put("NRO_EXPEDIENTE","EXPEDIENTE FICTICIO");
     ctx.put("DEPENDENCIA_TITULO","UNIDAD DE TECNOLOGIA DE LA INFORMACION");
     ctx.put("ASUNTO","Prueba ficticia de una plantilla para comprobar la generación y el uso de caracteres como & y <.");
     ctx.put("REFERENCIA","Referencia ficticia");ctx.put("FECHA_DOC","9 de octubre de 2026");
     ctx.put("EMPLEADO_EMITE","REMITENTE FICTICIO");ctx.put("EMPLEADO_DESTINO","DESTINATARIO INTERNO FICTICIO");
     ctx.put("CARGO_EMP_EMITE","Cargo emisor ficticio");ctx.put("CARGO_EMP_DESTINO","Cargo destinatario ficticio");
     ctx.put("DEPENDENCIA_EMITE","Unidad emisora ficticia");ctx.put("DEPENDENCIA_DESTINO","Unidad destinataria ficticia");
     ctx.put("INICIALES_EMP","FIC");
    }
    if(test.equals("externo")) {
     ctx.put("NOMBRE_DESTINATARIO","DESTINATARIO EXTERNO FICTICIO");ctx.put("CARGO","Cargo externo ficticio");
     ctx.put("ENTIDAD_PRIVADA_DESTINATARIO","Entidad externa ficticia");ctx.put("DIRECCION_DESTINATARIO","Dirección ficticia para pruebas");
    }
    try(OutputStream out=new FileOutputStream(new File(args[1],kind+"-"+test+".docx"))) {report.process(ctx,out);}
   }
  }
  System.out.println("Seis DOCX ficticios procesados con XDocReport y FreeMarker incluidos.");
 }
}
'''


def procesar(output):
    suffix='.exe' if os.name=='nt' else ''
    jdk=(next((ROOT/'java/windows').glob('jdk*')) if os.name=='nt' else ROOT/'java/linux')/'bin'
    libs=ROOT/'fuentes/Sgd/tramitedoc/web/target/tramitedoc-web-1.0/WEB-INF/lib'
    assert (libs/'tramitedoc-logic-1.0.jar').is_file(),'Ejecutar SGD compilar primero'
    output=Path(output);output.mkdir(parents=True,exist_ok=True)
    with tempfile.TemporaryDirectory(prefix='sgd-plantillas-java-') as temp:
        src=Path(temp)/'PlantillasDocxTest.java';src.write_text(JAVA_TEST,encoding='utf-8')
        cp=str(libs/'*')
        subprocess.run([str(jdk/('javac'+suffix)),'-encoding','UTF-8','-cp',cp,'-d',temp,str(src)],check=True,capture_output=True,text=True)
        result=subprocess.run([str(jdk/('java'+suffix)),'-cp',temp+os.pathsep+cp,'PlantillasDocxTest',str(ROOT/'plantillas-docx'),str(output)],capture_output=True,text=True)
        if result.returncode:raise AssertionError(result.stderr)
        print(result.stdout.strip())


class PlantillasDocxTest(unittest.TestCase):
    def test_campos_y_destinatarios(self):
        with tempfile.TemporaryDirectory(prefix='sgd-docx-ficticio-') as temp:
            procesar(temp)
            for path in Path(temp).glob('*.docx'):
                with zipfile.ZipFile(path) as z:
                    xml=''.join(z.read(n).decode('utf-8') for n in z.namelist() if n.startswith('word/') and n.endswith('.xml'))
                self.assertNotIn('${',xml,path.name)
                self.assertNotIn('«',xml,path.name)
                for footer in ['Av. La Molina 1981','Central telefónica: 240-2400','www.gob.pe/inia','www.gob.pe/midagri','Instituto Nacional de Innovación Agraria']:
                    self.assertIn(footer,xml,path.name)
                if 'vacio' not in path.name:
                    self.assertIn('REMITENTE FICTICIO',xml)
                    self.assertIn('&amp;',xml);self.assertIn('&lt;',xml)
                if path.name=='OFICIO-interno.docx':
                    self.assertIn('DESTINATARIO INTERNO FICTICIO',xml)
                    self.assertIn('Unidad destinataria ficticia',xml)
                if path.name=='OFICIO-externo.docx':
                    self.assertIn('DESTINATARIO EXTERNO FICTICIO',xml)
                    self.assertNotIn('DESTINATARIO INTERNO FICTICIO',xml)


if __name__=='__main__':unittest.main()
