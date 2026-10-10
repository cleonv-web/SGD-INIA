"""Servicios Java reales y DAOs simulados: jamás conecta a PostgreSQL."""
from pathlib import Path
import os
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA = r'''
import java.lang.reflect.*;
import java.util.*;
import pe.gob.onpe.tramitedoc.bean.*;
import pe.gob.onpe.tramitedoc.dao.*;
import pe.gob.onpe.tramitedoc.service.impl.*;
import pe.gob.onpe.tramitedoc.util.TextoDocumento;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

public class LocalidadDocumentoTest {
 static int checks;
 static void check(boolean b,String m){checks++;if(!b)throw new AssertionError(m);}
 static void inject(Object o,String n,Object v)throws Exception{
  Field f=o.getClass().getDeclaredField(n);f.setAccessible(true);f.set(o,v);
 }
 public static void main(String[] args)throws Exception{
  Properties p=new Properties();p.setProperty("localidad_documental_predeterminada","La Molina");
  p.setProperty("sigla_institucion","INIA");p.setProperty("formato_siglas","{TIPODOC} {NUDOC}-{ANIO}-{INS}/{UUOO}");
  ApplicationProperties config=new ApplicationProperties();config.setProperties(p);
  for(String lugar:new String[]{null,"","  ","null"," NULL ","Cusco","  Arequipa  "}) {
   String expected=(lugar==null||lugar.trim().isEmpty()||"null".equalsIgnoreCase(lugar.trim()))?"La Molina":lugar.trim();
   for(Object service:new Object[]{new DocumentoObjServiceImp(),new DocumentoObjInterServiceImp()}){
    DocumentoDatoBean dto=new DocumentoDatoBean();dto.setDeLugar(lugar);
    DocumentoObjDao dao=(DocumentoObjDao)Proxy.newProxyInstance(LocalidadDocumentoTest.class.getClassLoader(),new Class[]{DocumentoObjDao.class},(o,m,a)->{
     if(m.getName().equals("getDatosDoc"))return dto;
     throw new AssertionError("Acceso inesperado: "+m.getName());
    });
    inject(service,"documentoObjDao",dao);inject(service,"applicationProperties",config);
    DocumentoDatoBean result=(DocumentoDatoBean)service.getClass().getMethod("getDatosDoc",String.class,String.class).invoke(service,"2026","QA");
    check(result!=null,"Servicio descartó el documento");
    check(expected.equals(result.getDeLugar()),"Localidad incorrecta: "+result.getDeLugar());
    check(result.getNuSecFirma()!=null,"No se asignó secuencia de firma");
   }
   check((expected+", 10 de octubre de 2026").equals(TextoDocumento.fecha(lugar,"10 de octubre de 2026","La Molina")),"Fecha con null o distrito válido reemplazado");
   DatosPlantillaDoc datos=new DatosPlantillaDoc();datos.setCoEmpEmi("QA1");datos.setCoEmpFun("QA2");
   datos.setDeTipoDoc("INFORME");datos.setNuDocEmi("000001");datos.setNuAnn("2026");datos.setDeDocSig("QA");
   datos.setDeCargoFunEmiMae("DIRECTOR");datos.setDeTiFun("");datos.setInMultiple("1");datos.setFeEmiLargo("10 de octubre de 2026");
   DatosPlantillaDao plantillaDao=(DatosPlantillaDao)Proxy.newProxyInstance(LocalidadDocumentoTest.class.getClassLoader(),new Class[]{DatosPlantillaDao.class},(o,m,a)->{
    if(m.getName().equals("getDocumentoEmitido"))return datos;
    if(m.getName().equals("getLstDestintarios"))return new ArrayList<DestinatarioDocumentoEmiBean>();
    if(m.getName().equals("getLstReferencia"))return new ArrayList<ReferenciaBean>();
    if(m.getName().equals("getDistritoLocal"))return lugar;
    if(m.getName().equals("getParametros")||m.getName().equals("getPiePagina"))return "";
    throw new AssertionError("Consulta inesperada: "+m.getName());
   });
   DocumentoXmlServiceImp xml=new DocumentoXmlServiceImp();inject(xml,"datosPlantillaDao",plantillaDao);inject(xml,"applicationProperties",config);
   DatosPlantillaDoc date=xml.datosParaPlantilla("2026","QA");
   check(date!=null,"Servicio de plantilla descartó los datos");
   check((expected+", 10 de octubre de 2026").equals(date.getFechaDoc()),"Fecha incorrecta en el servicio real de plantilla");
  }
  check("".equals(TextoDocumento.fecha(null,null,null)),"Ausencias imprimen null");
  check("La Molina".equals(TextoDocumento.fecha(null,null,"La Molina")),"Fecha opcional imprime null");
  check("10 de octubre".equals(TextoDocumento.fecha(null,"10 de octubre",null)),"Fecha sin localidad falla");
  Properties empty=new Properties();config.setProperties(empty);
  check("".equals(config.getLocalidadDocumentalPredeterminada()),"Se inventó una localidad sin configurar");
  System.out.println(checks+" comprobaciones de localidad y firma aprobadas, DAOs simulados.");
 }
}
'''


class LocalidadDocumentalTest(unittest.TestCase):
    def test_servicios_reales_y_fecha_sin_bd(self):
        suffix = '.exe' if os.name == 'nt' else ''
        jdk = (next((ROOT/'java/windows').glob('jdk*')) if os.name == 'nt' else ROOT/'java/linux')/'bin'
        libs = ROOT/'fuentes/Sgd/tramitedoc/web/target/tramitedoc-web-1.0/WEB-INF/lib'
        base = ROOT/'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc'
        servlet = ROOT/'herramientas/maven-repo/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar'
        cp = os.pathsep.join([str(libs/'*'), str(servlet)])
        with tempfile.TemporaryDirectory(prefix='sgd-localidad-') as temp:
            src = Path(temp)/'LocalidadDocumentoTest.java';src.write_text(JAVA,encoding='utf-8')
            sources = [src, base/'util/TextoDocumento.java',base/'web/util/ApplicationProperties.java',
                       base/'service/impl/DocumentoObjServiceImp.java',base/'service/impl/DocumentoObjInterServiceImp.java',
                       base/'service/impl/DocumentoXmlServiceImp.java']
            result = subprocess.run([str(jdk/('javac'+suffix)),'-encoding','UTF-8','-cp',cp,'-d',temp,*map(str,sources)],capture_output=True,text=True)
            self.assertEqual(result.returncode,0,result.stderr)
            result = subprocess.run([str(jdk/('java'+suffix)),'-cp',temp+os.pathsep+cp,'LocalidadDocumentoTest'],capture_output=True,text=True)
            self.assertEqual(result.returncode,0,result.stdout+result.stderr)
            print(result.stdout.strip())

if __name__ == '__main__':unittest.main()
