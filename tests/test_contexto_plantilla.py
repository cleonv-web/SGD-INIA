"""Reproduce el fallo de cabecera y procesa el servicio real con DTO ficticio.

No consulta BD, no abre Word ni genera expedientes reales.
"""
from pathlib import Path
import os
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA = r'''
import java.io.*;
import java.lang.reflect.*;
import java.nio.file.*;
import javax.servlet.*;
import javax.servlet.http.*;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.support.StaticWebApplicationContext;
import fr.opensagres.xdocreport.document.*;
import fr.opensagres.xdocreport.document.registry.*;
import fr.opensagres.xdocreport.template.*;
import pe.gob.onpe.tramitedoc.bean.*;
import pe.gob.onpe.tramitedoc.service.*;
import pe.gob.onpe.tramitedoc.service.impl.*;
import pe.gob.onpe.tramitedoc.web.servlet.DocumentoSrvlt;

public class ContextoPlantillaTest {
 static int checks=0;
 static void check(boolean value,String msg) {checks++;if(!value)throw new AssertionError(msg);}
 static IXDocReport load(String path) throws Exception {
  try(InputStream in=new FileInputStream(path)) {return XDocReportRegistry.getRegistry().loadReport(in,TemplateEngineKind.Freemarker);}
 }
 static class Servicio extends DocumentoXmlServiceImp {
  DatosPlantillaDoc datos; PlantillaDocx plantilla;
  Servicio(DatosPlantillaDoc d,IXDocReport r) {datos=d;plantilla=new PlantillaDocx();plantilla.setNomArchivo("FICTICIO.docx");plantilla.setTemplate(r);}
  public DatosPlantillaDoc datosParaPlantilla(String a,String e){return datos;}
  protected PlantillaDocx obtenerPlantilla(DatosPlantillaDoc d){return plantilla;}
  IContext context(IXDocReport r)throws Exception{return crearContextoPlantilla(r,datos);}
 }
 static class Servlet extends DocumentoSrvlt {
  void process(HttpServletRequest req,HttpServletResponse res)throws Exception{processRequest(req,res);}
 }
 static class Respuesta {
  String tipo;StringWriter text=new StringWriter();ByteArrayOutputStream bytes=new ByteArrayOutputStream();boolean closed;
  HttpServletResponse proxy() {
   ServletOutputStream out=new ServletOutputStream(){
    public void write(int b){bytes.write(b);}
    public void flush(){check(!closed,"Flush posterior al cierre");}
    public void close(){closed=true;}
   };
   return (HttpServletResponse)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpServletResponse.class},(p,m,a)->{
    if(m.getName().equals("setContentType"))tipo=(String)a[0];
    if(m.getName().equals("getOutputStream"))return out;
    if(m.getName().equals("getWriter"))return new PrintWriter(text);
    return null;
   });
  }
 }
 static void servlet(DocumentoObjBean bean,boolean success)throws Exception {
  StaticWebApplicationContext ctx=new StaticWebApplicationContext();
  DocumentoXmlService service=(DocumentoXmlService)Proxy.newProxyInstance(ContextoPlantillaTest.class.getClassLoader(),new Class[]{DocumentoXmlService.class},(p,m,a)->bean);
  ctx.getBeanFactory().registerSingleton("documentoXmlService",service);ctx.refresh();
  Field field=ContextLoader.class.getDeclaredField("currentContext");field.setAccessible(true);Object previous=field.get(null);field.set(null,ctx);
  try {
   HttpServletRequest req=(HttpServletRequest)Proxy.newProxyInstance(ContextoPlantillaTest.class.getClassLoader(),new Class[]{HttpServletRequest.class},(p,m,a)->{
    if(m.getName().equals("getParameter"))return a[0].equals("accion")?"creaDocx":"FICTICIO";return null;
   });
   Respuesta response=new Respuesta();new Servlet().process(req,response.proxy());
   if(success){check("application/octet-stream".equals(response.tipo),"DOCX válido no descargado");check(response.bytes.size()>0,"Respuesta vacía");}
   else{check("application/std".equals(response.tipo),"Archivo incompleto fue anunciado como DOCX");check(response.text.toString().contains("No se pudo generar"),"Error sin explicación");check(response.bytes.size()==0,"Se descargó un archivo incompleto");}
  }finally{field.set(null,previous);ctx.close();}
 }
 public static void main(String[] args)throws Exception {
  PrintStream err=System.err;System.setErr(new PrintStream(new ByteArrayOutputStream()));
  IXDocReport baseline=load(args[0]+"/INFORME-INIA.docx");IContext old=baseline.createContext();
  String[] keys={"NOMBRE_ANIO","NOMBRE_ANIO2","NUMERO_DOC","DEPENDENCIA_TITULO","NRO_EXPEDIENTE","ASUNTO","REFERENCIA","FECHA_DOC","EMPLEADO_EMITE","EMPLEADO_DESTINO","CARGO_EMP_EMITE","CARGO_EMP_DESTINO","DEPENDENCIA_EMITE","DEPENDENCIA_DESTINO","NOMBRE_DESTINATARIO","CARGO","ENTIDAD_PRIVADA_DESTINATARIO","DIRECCION_DESTINATARIO","INICIALES_EMP","COPIA"};
  for(String key:keys)old.put(key,"");old.put("DEPENDENCIA_TITULO",null);
  boolean reproduced=false;try{baseline.process(old,new ByteArrayOutputStream());}catch(Exception e){reproduced=e.getMessage().contains("DEPENDENCIA_TITULO");}
  check(reproduced,"No se reprodujo el error de la ejecución real");
  DocumentoObjBean valid=null;
  for(String kind:new String[]{"INFORME","OFICIO"})for(int test=0;test<4;test++){
   DatosPlantillaDoc datos=new DatosPlantillaDoc();
   if(test==0){datos.setTituloDependencia("TITULO FICTICIO");datos.setDeDepEmiMae("OTRA UNIDAD FICTICIA");}
   if(test==1){datos.setTituloDependencia("  ");datos.setDeDepEmiMae("UNIDAD EMISORA FICTICIA");}
   if(test==2){datos.setDeDepEmi("DEPENDENCIA FICTICIA");}
   IXDocReport report=load(args[0]+"/"+kind+"-INIA.docx");Servicio service=new Servicio(datos,report);
   IContext context=service.context(report);
   String[] expected={"TITULO FICTICIO","UNIDAD EMISORA FICTICIA","DEPENDENCIA FICTICIA",""};
   check(expected[test].equals(context.get("DEPENDENCIA_TITULO")),"Cabecera sin alternativa o título válido reemplazado");
   for(Object value:context.getContextMap().values())check(value!=null && !"null".equals(value),"Campo nulo enviado a FreeMarker");
   valid=service.crearDocx("FICTICIO","FICTICIO");check(valid!=null && valid.getDocumento().length>0,"Servicio real no generó el DOCX con datos opcionales nulos");
   Files.write(Paths.get(args[1],kind+"-nulos-"+test+".docx"),valid.getDocumento());
  }
  IXDocReport report=load(args[0]+"/INFORME-INIA.docx");Servicio missing=new Servicio(null,report);
  check(missing.crearDocx("FICTICIO","FICTICIO")==null,"Documento inexistente causa excepción");
  IXDocReport broken=(IXDocReport)Proxy.newProxyInstance(ContextoPlantillaTest.class.getClassLoader(),new Class[]{IXDocReport.class},(p,m,a)->{
   if(m.getName().equals("createContext"))return report.createContext();
   if(m.getName().equals("process"))throw new IOException("FALLO FICTICIO");return null;
  });
  check(new Servicio(new DatosPlantillaDoc(),broken).crearDocx("FICTICIO","FICTICIO")==null,"Fallo de motor devuelve archivo parcial");
  servlet(null,false);servlet(new DocumentoObjBean(),false);
  DocumentoObjBean empty=new DocumentoObjBean();empty.setDocumento(new byte[0]);servlet(empty,false);servlet(valid,true);
  System.setErr(err);System.out.println(checks+" comprobaciones aprobadas: error original reproducido, ocho DOCX con nulos procesados por el servicio real y respuesta servlet protegida.");
 }
}
'''


def probar(output,compilar_fuente=True):
    output=Path(output);output.mkdir(parents=True,exist_ok=True)
    suffix='.exe' if os.name=='nt' else ''
    jdk=(next((ROOT/'java/windows').glob('jdk*')) if os.name=='nt' else ROOT/'java/linux')/'bin'
    libs=ROOT/'fuentes/Sgd/tramitedoc/web/target/tramitedoc-web-1.0/WEB-INF/lib'
    base=ROOT/'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramitedoc'
    with tempfile.TemporaryDirectory(prefix='sgd-contexto-') as temp:
        src=Path(temp)/'ContextoPlantillaTest.java';src.write_text(JAVA,encoding='utf-8')
        servlet=ROOT/'herramientas/maven-repo/javax/servlet/servlet-api/2.5/servlet-api-2.5.jar'
        cp=os.pathsep.join([str(libs/'*'),str(servlet)])
        sources=[src]
        if compilar_fuente:sources += [base/'service/impl/DocumentoXmlServiceImp.java',base/'web/servlet/DocumentoSrvlt.java',
                                      base/'util/TextoDocumento.java',base/'web/util/ApplicationProperties.java']
        result=subprocess.run([str(jdk/('javac'+suffix)),'-encoding','UTF-8','-cp',cp,'-d',temp,*map(str,sources)],capture_output=True,text=True)
        if result.returncode:raise AssertionError(result.stderr)
        result=subprocess.run([str(jdk/('java'+suffix)),'-cp',temp+os.pathsep+cp,'ContextoPlantillaTest',str(ROOT/'plantillas-docx'),str(output)],capture_output=True,text=True)
        if result.returncode:raise AssertionError(result.stdout+result.stderr)
        print(result.stdout.strip())


class ContextoPlantillaTest(unittest.TestCase):
    def test_servicio_real_y_descarga_sin_bd(self):
        with tempfile.TemporaryDirectory(prefix='sgd-contexto-docx-') as out:probar(out)


if __name__=='__main__':unittest.main()
