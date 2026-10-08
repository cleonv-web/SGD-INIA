package pe.gob.onpe.mpvdoc.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import pe.gob.onpe.mpvdoc.web.util.ApplicationProperties;
import com.octo.captcha.service.CaptchaServiceException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod; 
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pe.gob.onpe.mpvdoc.bean.DocumentoFileBean;
import pe.gob.onpe.mpvdoc.bean.DocumentoFilesStructBean;
import pe.gob.onpe.mpvdoc.bean.MpvdocBean;
import pe.gob.onpe.mpvdoc.bean.ResultMpvdocBean;
import pe.gob.onpe.mpvdoc.bean.UbigeoBean;
import pe.gob.onpe.mpvdoc.service.CaptchaSingletonService; 
import pe.gob.onpe.mpvdoc.web.util.Constantes;
import pe.gob.onpe.mpvdoc.web.util.Utility;
import pe.gob.onpe.mpvdoc.service.MpvdocService;

@Controller(value = "inicioController")
@RequestMapping("/inicio.do")
public class inicioController{

    private transient final Log LOGGER = LogFactory.getLog(getClass());

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private MpvdocService mpvDocService;
      
    
    
    @RequestMapping(method = RequestMethod.GET)
    public String doShowForm(HttpServletRequest request, Model model) throws UnsupportedEncodingException {
        //ServletContext sc = request.getSession().getServletContext();
        //Utility.getInstancia().pathPdfs = sc.getRealPath("/"+ applicationProperties.getRutaTemporal() +"/");
        
        String cookieSessionId = request.getSession().getId();
        MpvdocBean mpvdocBean = new MpvdocBean();
        mpvdocBean.setCookieSessionId(cookieSessionId);
        model.addAttribute("listaTipoDoc",mpvDocService.getTiposDocs());
        model.addAttribute("listaDepartamento",mpvDocService.getListDepartamento());
        model.addAttribute("mpvdocBean",  mpvdocBean); 
        model.addAttribute("SizeMaxAnexo",  applicationProperties.getFileSizeMaxAnexo()); 
        model.addAttribute("SizeMaxDocumento",  applicationProperties.getFileSizeMaxDocumento()); 
        model.addAttribute("LinkConsultaTuTramite",  applicationProperties.getLinkConsultaTuTramite()); 
        String TerminosTitulo=new String(applicationProperties.getTerminosTitulo().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosTitulo",  TerminosTitulo);
        String TerminosI=new String(applicationProperties.getTerminosI().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosI",  TerminosI);
        String TerminosII=new String(applicationProperties.getTerminosII().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosII",  TerminosII);
        String TerminosIII=new String(applicationProperties.getTerminosIII().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosIII", TerminosIII);
        String TerminosIV=new String(applicationProperties.getTerminosIV().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosIV",  TerminosIV);
        String TerminosV=new String(applicationProperties.getTerminosV().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosV",  TerminosV);
        String getTerminosVI=new String(applicationProperties.getTerminosVI().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosVI",  getTerminosVI);
        String getTerminosVII=new String(applicationProperties.getTerminosVII().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosVII",  getTerminosVII);
        String getTerminosVIII=new String(applicationProperties.getTerminosVIII().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosVIII",  getTerminosVIII);
        String getTerminosIX=new String(applicationProperties.getTerminosIX().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosIX",  getTerminosIX);
        String getTerminosX=new String(applicationProperties.getTerminosX().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosX", getTerminosX);
        String getTerminosXI=new String(applicationProperties.getTerminosXI().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosXI", getTerminosXI);
        String getTerminosXII=new String(applicationProperties.getTerminosXII().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosXII",getTerminosXII);
        String getTerminosXIII=new String(applicationProperties.getTerminosXIII().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosXIII", getTerminosXIII);
        String getTerminosXIV=new String(applicationProperties.getTerminosXIV().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosXIV", getTerminosXIV);
        String getTerminosXV=new String(applicationProperties.getTerminosXV().getBytes("ISO-8859-1"),"UTF-8");
        model.addAttribute("TerminosXV",getTerminosXV);
        model.addAttribute("linkReporteCargo",applicationProperties.getLinkMesaPartesCiudadano()+"/cargo?");
        request.getSession().setAttribute("SessionDoc", null);
        request.getSession().setAttribute("SessionAnexos", null);
        return "inicio";
    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaProvinciaJson")
    public @ResponseBody List<UbigeoBean> goBuscaProvinciaJson(HttpServletRequest request, Model model){
        List<UbigeoBean> result=null;  
        String pcodigo = request.getParameter("pcodigo");
        try{
            result = mpvDocService.getListProvincia(pcodigo);
        }catch(Exception e){
            e.printStackTrace();
        }       
        return result;
    }    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDistritoJson")
    public @ResponseBody List<UbigeoBean> goBuscaDistritoJson(HttpServletRequest request, Model model){
        List<UbigeoBean> result=null;  
        String pcodigo = request.getParameter("pcodigo");
        try{
            result = mpvDocService.getListDistrito(pcodigo);
        }catch(Exception e){
            e.printStackTrace();
        }       
        return result;
    }    
    

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaCiudadano")
    public @ResponseBody String goBuscaCiudadano(HttpServletRequest request, Model model){
        String result=""; 
        //String pnuDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDoc");
        String pnuDoc = request.getParameter("pnuDoc");
        try{
            result = mpvDocService.getCiudadano(pnuDoc);
        }catch(Exception e){
            e.getMessage();
        }       
        return result;
    }
     @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaProveedorJson")
    public @ResponseBody String goBuscaProveedorJson(HttpServletRequest request, Model model){
        String result="";  
        String pnuRuc = request.getParameter("pnuRuc");
        try{
            result = mpvDocService.getProveedor(pnuRuc);
        }catch(Exception e){
            e.printStackTrace();
        }       
        return result;
    }   
       
    
    @RequestMapping(method = RequestMethod.POST,  params = "accion=goInicio") 
    public @ResponseBody String goInicio(@RequestBody  MpvdocBean mpvdocBean, HttpServletRequest request, Model model){
        String mensaje = "NO_OK";  
        String expediente = "";  
        String anio = "";  
        String emision = "";  
        Boolean captchaValido =Boolean.FALSE; 
        String captchaId = mpvdocBean.getCookieSessionId();
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession sesion = httpServletRequest.getSession(false);        
        mpvdocBean.setTextoCaptcha(mpvdocBean.getTextoCaptcha().toUpperCase());
        try{
            String textoCaptcha = mpvdocBean.getTextoCaptcha();
            String validaObli = validaCamposObligatorio(mpvdocBean, sesion);
            if(StringUtils.isEmpty(validaObli)){
                try{
                    captchaValido = CaptchaSingletonService.getInstance().validateResponseForID(captchaId, textoCaptcha.toUpperCase());
                } catch (CaptchaServiceException e) {
                    LOGGER.error(e.getMessage(), e);
                    captchaValido =Boolean.FALSE;
                   // result.rejectValue("textoCaptcha", null, "Error al validar texto de la imagen.");
                }           
                if(captchaValido){
                    mpvdocBean.setArchivoBytes((byte[])sesion.getAttribute("SessionDoc"));
                    LinkedList<DocumentoFileBean> files = (LinkedList<DocumentoFileBean>)sesion.getAttribute("SessionAnexos");
                    LinkedList<DocumentoFilesStructBean> filesProcess= new LinkedList<DocumentoFilesStructBean>();
                    int totalAnexos=0;
                    if(files!=null && mpvdocBean.getListAnexos()!=null &&  mpvdocBean.getListAnexos().size()>0){
                        for (String idAnexo : mpvdocBean.getListAnexos()) {
                            for (DocumentoFileBean file : files) {
                                if(file.getIdDocumento().equals(idAnexo)){
                                    DocumentoFilesStructBean data = new DocumentoFilesStructBean();
                                    totalAnexos++;
                                    data.setInid(totalAnexos);
                                    data.setVcarcda(file.getArchivoBytes());
                                    data.setVctit(file.getNombreArchivo());
                                    filesProcess.add(data);
                                }
                            }                             
                        }
                        mpvdocBean.setFilesProcess(filesProcess);
                        if(mpvdocBean.getFilesProcess()!=null && mpvdocBean.getFilesProcess().size()!=mpvdocBean.getListAnexos().size()){
                            mensaje = "Se generó erro al momento se subir el/los anexo/s. Vuelva subir de nuevo!";  
                        }
                    }
                    
                    if (mensaje.equals("NO_OK")) {                         
                        ResultMpvdocBean result =  mpvDocService.getGrabarExpediente(mpvdocBean);
                        if (result.getResultado().equals("OK") && mpvdocBean.getCorreo()!=null && mpvdocBean.getCorreo().length()>1) { 
                            mpvDocService.RegisternotificarCorreo(mpvdocBean.getNom()+" "+mpvdocBean.getPat()+" "+mpvdocBean.getMat(),mpvdocBean.getCorreo(),"",applicationProperties.getEmailAsunto(),HtmlCorreo(result));                            
                        }
                         mensaje = result.getResultado();
                         expediente=result.getExpediente();
                         anio=result.getAnio();
                         emision=result.getEmision();
                    }                       
                }else{
                    mensaje = "Código de Imagen Incorrecto.";
                }
            }else{
                mensaje = "Campos Obligatorios: " + validaObli + ".";
            }
            
        }catch(Exception e){
            e.printStackTrace();
        }   
        String coRespuesta="0";
         if (mensaje.equals("OK")) {
            coRespuesta = "1";
        } else{
            coRespuesta = "0";
        }  
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(mensaje);
        retval.append("\",\"expediente\":\"");
        retval.append(expediente);  
        retval.append("\",\"iod\":\"");
        retval.append(emision);  
         retval.append("\",\"anio\":\"");
        retval.append(anio);  
        retval.append("\"}");
        
        return retval.toString();             
    }
    private String HtmlCorreo(ResultMpvdocBean result){
        StringBuilder mensaje= new StringBuilder();
        String url = "";
        url = applicationProperties.getLinkMesaPartesCiudadano()+"/cargo?cargNuAnn=" + result.getAnio() + "&cargNuEmi=" + result.getEmision();
        mensaje.append("<head><style type='text/css'>");
        mensaje.append("<!--");
        mensaje.append(".style1 {font-size: 12px; FONT-FAMILY: Arial, Helvetica, sans-serif;}");
        mensaje.append(".style2 {font-size: 12px; font-weight: bold; }");
        mensaje.append("-->");
        mensaje.append("</style>");
        mensaje.append("</head>");
        mensaje.append("<body>");
        mensaje.append(applicationProperties.getEmailContenidoI()+"<br><br>");
        mensaje.append("<p class='style1'>"+applicationProperties.getEmailContenidoII()+" <strong>" + result.getExpediente() + "</strong> "+applicationProperties.getEmailContenidoIII()+"<br><br>");
        mensaje.append("<p class='style1'>"+applicationProperties.getEmailContenidoIV()+"</p><br>");
        mensaje.append("<p class='style1'><a href='"+applicationProperties.getLinkConsultaTuTramite()+"' target='_blank'>"+applicationProperties.getLinkConsultaTuTramite()+"</a></p><br>");
        mensaje.append("<p class='style1'>"+applicationProperties.getEmailContenidoV()+"<br><br>");
        mensaje.append("<p class='style1'><strong>"+applicationProperties.getEmailContenidoVI()+"</strong></p>");
        mensaje.append("<br><br>");
        mensaje.append("<a href='" + url + "'><img src='"+applicationProperties.getLinkMesaPartesCiudadano()+"/resources-"+applicationProperties.getResourcesVersion()+"/images/cargo.png'/> Cargo virtual</a>");
        mensaje.append("</body>");        
        return mensaje.toString();
    }
    private String validaCamposObligatorio(MpvdocBean verificaDocBean,HttpSession sesion){
        String resultado = "";
        String[] msj = new String[17];
        int i = 0; 
            if(verificaDocBean.getIdTipoPersona().contains("02") && StringUtils.isEmpty(verificaDocBean.getRuc())){
                msj[i] = "Nro de RUC";
                i++;
            }
             if(verificaDocBean.getIdTipoPersona().contains("02") && StringUtils.isEmpty(verificaDocBean.getRaz())){
                msj[i] = "Razón Social";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getDni())){
                msj[i] = "Nro DNI";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getPat())){
                msj[i] = "Apellido paterno";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getMat())){
                msj[i] = "Apellido materno";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getNom())){
                msj[i] = "Nombres";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getTel())){
                msj[i] = "Teléfono";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getCorreo())){
                msj[i] = "Correo Electronico";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getDir())){
                msj[i] = "Dirección";
                i++;
            }
            
            if(StringUtils.isEmpty(verificaDocBean.getDep())){
                msj[i] = "Departamento";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getPro())){
                msj[i] = "Provincia";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getDis())){
                msj[i] = "Distrito";
                i++;
            }
            
            if(StringUtils.isEmpty(verificaDocBean.getTipoDocumento())){
                msj[i] = "ipo de documento";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getNrodoc())){
                msj[i] = "Nro de documento";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getFolios())){
                msj[i] = "Nro de folios";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getAsu())){
                msj[i] = "Asunto";
                i++;
            }
            
            if(StringUtils.isEmpty(verificaDocBean.getTextoCaptcha())){
                msj[i] = "Código Imágen Captcha";
                i++;
            }
            byte[] decoded = (byte[])sesion.getAttribute("SessionDoc");
            if(decoded == null){
                msj[i] = "Documento principal";
                i++;
            }
            
            resultado = msj[0];
            
            if(i > 1){
                for(int j = 1; j < i; j++){
                    resultado = resultado + ", " + msj[j];
                }
            } 
        return resultado;
    }
    
}
