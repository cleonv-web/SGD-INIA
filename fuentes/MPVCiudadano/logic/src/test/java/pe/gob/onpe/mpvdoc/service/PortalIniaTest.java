package pe.gob.onpe.mpvdoc.service;

import java.lang.reflect.*;
import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.mock.web.*;
import org.codehaus.jackson.map.ObjectMapper;
import pe.gob.onpe.mpvdoc.bean.*;
import pe.gob.onpe.mpvdoc.web.controller.*;
import pe.gob.onpe.mpvdoc.web.util.ApplicationProperties;
import pe.gob.onpe.mpvdoc.web.servlet.CargoSrvlt;

public class PortalIniaTest {
    @Test public void invalidCargoReturns400() throws Exception {
        CargoSrvlt servlet=new CargoSrvlt(); MockHttpServletRequest request=new MockHttpServletRequest();
        MockHttpServletResponse response=new MockHttpServletResponse(); request.setParameter("cargNuAnn","2026");request.setParameter("cargNuEmi","../otro");
        Method method=CargoSrvlt.class.getDeclaredMethod("descargarCargo",javax.servlet.http.HttpServletRequest.class,javax.servlet.http.HttpServletResponse.class);
        method.setAccessible(true);method.invoke(servlet,request,response);assertEquals(400,response.getStatus());assertEquals("no-store",response.getHeader("Cache-Control"));
    }
    @Test public void missingCargoReturns404InsteadOfEmptyPdf() throws Exception {
        CargoSrvlt servlet=new CargoSrvlt();inject(servlet,"applicationProperties",properties());
        MpvdocService service=(MpvdocService)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{MpvdocService.class},new InvocationHandler(){
            public Object invoke(Object proxy,Method method,Object[] args){return Collections.emptyList();}
        });
        inject(servlet,"mpvdocService",service);MockHttpServletRequest request=new MockHttpServletRequest();MockHttpServletResponse response=new MockHttpServletResponse();
        request.setParameter("cargNuAnn","2026");request.setParameter("cargNuEmi","0000000000");
        Method method=CargoSrvlt.class.getDeclaredMethod("descargarCargo",javax.servlet.http.HttpServletRequest.class,javax.servlet.http.HttpServletResponse.class);
        method.setAccessible(true);method.invoke(servlet,request,response);assertEquals(404,response.getStatus());assertTrue(response.getContentAsString().contains("No existe"));assertFalse(response.getContentAsString().startsWith("%PDF"));
    }
    @SuppressWarnings("unchecked")
    private Map<String,CaptchaSessionService.Challenge> values(MockHttpSession session) {
        return (Map<String,CaptchaSessionService.Challenge>)session.getAttribute(CaptchaSessionService.class.getName());
    }
    private String answer(MockHttpSession session, String token) { return values(session).get(token).answer; }
    private void inject(Object target, String name, Object value) throws Exception {
        Field field = target.getClass().getDeclaredField(name); field.setAccessible(true); field.set(target,value);
    }
    private ApplicationProperties properties() {
        ApplicationProperties p = new ApplicationProperties(); Properties v = new Properties();
        v.setProperty("FILE_SIZE_MAX","32"); v.setProperty("FILE_SIZE_MAX_ANEXO","20");
        v.setProperty("EMAIL_ASUNTO","INIA"); v.setProperty("MESA_PARTES_CIUDADANO","http://localhost/mpd");
        v.setProperty("CONSULTA_TU_TRAMITE","http://localhost/consulta/inicio.do"); p.setProperties(v); return p;
    }
    @Test public void independentImagesAndSingleUse() {
        MockHttpSession session = new MockHttpSession(); String first=CaptchaSessionService.issue(session), second=CaptchaSessionService.issue(session);
        assertFalse(first.equals(second)); assertEquals(220,CaptchaSessionService.render(session,first).getWidth());
        String a=answer(session,first); assertTrue(CaptchaSessionService.validate(session,first," " + a.toLowerCase(Locale.ROOT) + " "));
        assertFalse(CaptchaSessionService.validate(session,first,a)); assertTrue(CaptchaSessionService.validate(session,second,answer(session,second)));
    }
    @Test public void sessionIsolationAndWrongAnswer() {
        MockHttpSession owner=new MockHttpSession(), other=new MockHttpSession(); String token=CaptchaSessionService.issue(owner), a=answer(owner,token);
        assertFalse(CaptchaSessionService.validate(other,token,a)); assertFalse(CaptchaSessionService.validate(null,token,a));
        assertFalse(CaptchaSessionService.validate(owner,token,"INVALIDO")); assertFalse(CaptchaSessionService.validate(owner,token,a));
    }
    @Test public void expiryAndBoundedStorage() {
        MockHttpSession session=new MockHttpSession(); String token=CaptchaSessionService.issue(session), a=answer(session,token);
        values(session).put(token,new CaptchaSessionService.Challenge(a,System.currentTimeMillis()-CaptchaSessionService.TTL-1000));
        assertFalse(CaptchaSessionService.validate(session,token,a));
        for(int i=0;i<30;i++) CaptchaSessionService.issue(session);
        assertEquals(CaptchaSessionService.MAX_CHALLENGES,values(session).size());
    }
    @Test public void emptyPayloadReturnsRequiredFieldsWithoutCrash() throws Exception {
        inicioController controller=new inicioController(); MockHttpServletRequest request=new MockHttpServletRequest();
        request.setSession(new MockHttpSession()); MpvdocBean bean=new MpvdocBean(); bean.setIdTipoPersona("02");
        Map result=new ObjectMapper().readValue(controller.goInicio(bean,request,null),Map.class);
        assertEquals("0",result.get("coRespuesta")); assertTrue(result.get("deRespuesta").toString().contains("Documento principal"));
        assertTrue(result.get("deRespuesta").toString().contains("términos"));
    }
    private MpvdocBean valid(MockHttpSession session) {
        MpvdocBean b=new MpvdocBean(); b.setIdTipoPersona("03"); b.setDni("00000001"); b.setPat("PRUEBA"); b.setMat("LOCAL"); b.setNom("FICTICIO");
        b.setTel("900000000"); b.setCorreo("prueba@example.invalid"); b.setDir("Dirección de prueba"); b.setDep("15"); b.setPro("01"); b.setDis("01");
        b.setTipoDocumento("020"); b.setNrodoc("S/N"); b.setFolios("1"); b.setAsu("Prueba con comillas: \"INIA\", & signos."); b.setAceptaTerminos(true);
        b.setNombreArchivo("prueba.pdf"); session.setAttribute("SessionDoc",new byte[]{1}); session.setAttribute("SessionDocName","prueba.pdf");
        b.setCaptchaToken(CaptchaSessionService.issue(session)); b.setTextoCaptcha(answer(session,b.getCaptchaToken())); return b;
    }
    @Test public void validCaptchaReachesServiceOnceAndMailFailureKeepsSuccess() throws Exception {
        inicioController controller=new inicioController(); final int[] calls={0};
        MpvdocService service=(MpvdocService)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{MpvdocService.class},new InvocationHandler(){
            public Object invoke(Object proxy,Method method,Object[] args){
                if(method.getName().equals("getGrabarExpediente")){ calls[0]++; ResultMpvdocBean result=new ResultMpvdocBean();result.setResultado("OK");result.setExpediente("PRUEBA-\"1\"");result.setAnio("2026");result.setEmision("1");return result; }
                if(method.getName().equals("RegisternotificarCorreo"))throw new IllegalStateException("Correo de prueba sin servicio"); return null;
            }});
        inject(controller,"mpvDocService",service); inject(controller,"applicationProperties",properties());
        MockHttpSession session=new MockHttpSession(); MockHttpServletRequest request=new MockHttpServletRequest();request.setSession(session); MpvdocBean bean=valid(session);
        Map result=new ObjectMapper().readValue(controller.goInicio(bean,request,null),Map.class);
        assertEquals("1",result.get("coRespuesta")); assertEquals("PRUEBA-\"1\"",result.get("expediente"));
        result=new ObjectMapper().readValue(controller.goInicio(bean,request,null),Map.class); assertEquals("0",result.get("coRespuesta")); assertEquals(1,calls[0]);
    }
    @Test public void staleFileAndInvalidCaptchaDoNotRegister() throws Exception {
        inicioController controller=new inicioController(); MockHttpSession session=new MockHttpSession(); MockHttpServletRequest request=new MockHttpServletRequest();request.setSession(session);
        MpvdocBean bean=valid(session); session.setAttribute("SessionDocName","otra-pestana.pdf");
        Map result=new ObjectMapper().readValue(controller.goInicio(bean,request,null),Map.class); assertEquals("0",result.get("coRespuesta")); assertTrue(result.get("deRespuesta").toString().contains("sesión de archivos"));
        session.setAttribute("SessionDocName","prueba.pdf");bean.setTextoCaptcha("ZZZZZ");
        result=new ObjectMapper().readValue(controller.goInicio(bean,request,null),Map.class); assertTrue(result.get("deRespuesta").toString().contains("incorrecto o venció"));
    }
    @Test public void missingAnnexDoesNotReachRegistration() throws Exception {
        inicioController controller=new inicioController(); MockHttpSession session=new MockHttpSession();
        MockHttpServletRequest request=new MockHttpServletRequest();request.setSession(session);MpvdocBean bean=valid(session);
        bean.setListAnexos(new ArrayList<String>(Arrays.asList("anexo-de-otra-sesion")));
        Map result=new ObjectMapper().readValue(controller.goInicio(bean,request,null),Map.class);
        assertEquals("0",result.get("coRespuesta"));assertTrue(result.get("deRespuesta").toString().contains("todos los anexos"));
    }
    @Test public void removingAnnexReleasesSessionBytes() throws Exception {
        DocumentoController controller=new DocumentoController();inject(controller,"applicationProperties",properties());
        MockMultipartHttpServletRequest request=new MockMultipartHttpServletRequest();MockHttpServletResponse response=new MockHttpServletResponse();
        request.addFile(new MockMultipartFile("files[]","anexo.docx","application/octet-stream",new byte[]{1,2}));
        List result=new ObjectMapper().readValue(controller.goUploadDocAnexos(request,response),List.class);
        String id=((Map)result.get(0)).get("id").toString();
        MockHttpServletRequest other=new MockHttpServletRequest();other.setSession(new MockHttpSession());
        controller.removeAnexo(id,other);assertEquals(1,((List)request.getSession().getAttribute("SessionAnexos")).size());
        controller.removeAnexo(id,request);assertEquals(0,((List)request.getSession().getAttribute("SessionAnexos")).size());
    }
    @Test public void uploadEscapesNamesAndRejectsFakePdf() throws Exception {
        DocumentoController controller=new DocumentoController(); inject(controller,"applicationProperties",properties());
        MockMultipartHttpServletRequest request=new MockMultipartHttpServletRequest(); MockHttpServletResponse response=new MockHttpServletResponse();
        request.addFile(new MockMultipartFile("files[]","prueba-\"ñ\".pdf","application/pdf","%PDF-prueba".getBytes("UTF-8")));
        List result=new ObjectMapper().readValue(controller.goUploadDoc(request,response),List.class);
        assertEquals("prueba-\"ñ\".pdf",((Map)result.get(0)).get("name")); assertEquals(200,response.getStatus());
        request=new MockMultipartHttpServletRequest();response=new MockHttpServletResponse();request.addFile(new MockMultipartFile("files[]","falso.pdf","application/pdf","no es PDF".getBytes("UTF-8")));
        controller.goUploadDoc(request,response);assertEquals(400,response.getStatus());assertNull(request.getSession().getAttribute("SessionDoc"));
    }
}
