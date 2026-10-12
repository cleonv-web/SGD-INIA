package pe.gob.onpe.consultadoc.service;

import java.lang.reflect.*;
import java.util.*;
import javax.servlet.http.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.ui.ExtendedModelMap;
import org.springframework.validation.BeanPropertyBindingResult;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.web.controller.inicioController;

public class ConsultaControllerTest {
    final Map<String,Object> attributes = new HashMap<String,Object>();
    final HttpSession session = (HttpSession) Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpSession.class},new InvocationHandler(){
        public Object invoke(Object p,Method m,Object[] args){
            if(m.getName().equals("getAttribute"))return attributes.get(args[0]);
            if(m.getName().equals("setAttribute")){attributes.put((String)args[0],args[1]);return null;}
            return null;
        }
    });
    String token; String expediente; int searches;
    HttpServletRequest request(){
        return (HttpServletRequest) Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{HttpServletRequest.class},new InvocationHandler(){
            public Object invoke(Object p,Method m,Object[] args){
                if(m.getName().equals("getSession"))return session;
                if(m.getName().equals("getParameter"))return "captchaToken".equals(args[0])?token:("expediente".equals(args[0])?expediente:null);
                return null;
            }
        });
    }
    inicioController controller(final boolean found) throws Exception {
        inicioController c=new inicioController();
        Field service=inicioController.class.getDeclaredField("verificaDocService");service.setAccessible(true);
        service.set(c,new ConsultaDocService(){
            public List<ConsultaDocBean> getBuscarExpediente(String number){
                searches++; assertEquals("2026-000001",number);
                ConsultaDocBean b=new ConsultaDocBean();b.setNroExpediente(number);b.setAnio("2026");b.setNumeroEmision("1");
                return found?Arrays.asList(b):Collections.<ConsultaDocBean>emptyList();
            }
            public List<String> SeguimientoOficinaAtencion(String filtro){return Collections.emptyList();}
        });return c;
    }
    @SuppressWarnings("unchecked") String answer(){
        token=CaptchaSessionService.issue(session);
        return ((Map<String,CaptchaSessionService.Challenge>)attributes.get(CaptchaSessionService.class.getName())).get(token).answer;
    }
    String submit(inicioController controller,ConsultaDocBean b,ExtendedModelMap model)throws Exception{
        return controller.goInicio(b,request(),new BeanPropertyBindingResult(b,"consultaDocBean"),model);
    }
    @Test public void missingFieldsDoNotThrowOrQuery()throws Exception{
        ExtendedModelMap model=new ExtendedModelMap();
        assertEquals("/messages",submit(controller(true),new ConsultaDocBean(),model));assertEquals(0,searches);
        assertTrue(model.get("msjEmision").toString().contains("Campos Obligatorios"));
    }
    @Test public void qrPrefillsFullNumberWithoutQuerying()throws Exception{
        expediente=" 2026-0000002 ";ExtendedModelMap model=new ExtendedModelMap();
        assertEquals("inicio",controller(true).doShowForm(request(),model));
        assertEquals("2026-0000002",((ConsultaDocBean)model.get("consultaDocBean")).getNroExpediente());
        assertEquals(0,searches);
    }
    @Test public void malformedQrParameterDoesNotPrefill()throws Exception{
        expediente="2026-2<script>";ExtendedModelMap model=new ExtendedModelMap();
        controller(true).doShowForm(request(),model);
        assertNull(((ConsultaDocBean)model.get("consultaDocBean")).getNroExpediente());
        assertEquals(0,searches);
    }
    @Test public void invalidCodeNeverQueries()throws Exception{
        answer();ConsultaDocBean b=new ConsultaDocBean();b.setNroExpediente("2026-000001");b.setTextoCaptcha("WRONG-ANSWER");
        assertEquals("/messages",submit(controller(true),b,new ExtendedModelMap()));assertEquals(0,searches);
    }
    @Test public void validCodeReturnsDocumentAndRejectsReplay()throws Exception{
        ConsultaDocBean b=new ConsultaDocBean();b.setNroExpediente(" 2026-000001 ");b.setTextoCaptcha(answer().toLowerCase(Locale.ROOT));
        b.setCookieSessionId("untrusted-client-id");ExtendedModelMap model=new ExtendedModelMap();inicioController c=controller(true);
        assertEquals("/viewExpediente",submit(c,b,model));assertEquals(1,searches);
        assertNotNull(model.get("docExpediente"));
        assertEquals("/messages",submit(c,b,new ExtendedModelMap()));assertEquals(1,searches);
    }
    @Test public void validCodeReportsMissingDocument()throws Exception{
        ConsultaDocBean b=new ConsultaDocBean();b.setNroExpediente("2026-000001");b.setTextoCaptcha(answer());ExtendedModelMap model=new ExtendedModelMap();
        assertEquals("/messages",submit(controller(false),b,model));assertEquals(1,searches);
        assertTrue(model.get("msjEmision").toString().contains("No encontramos"));
    }
}
