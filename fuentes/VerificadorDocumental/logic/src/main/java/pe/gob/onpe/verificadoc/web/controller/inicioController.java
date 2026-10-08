package pe.gob.onpe.verificadoc.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import pe.gob.onpe.verificadoc.web.util.ApplicationProperties;
import com.octo.captcha.service.CaptchaServiceException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.gob.onpe.verificadoc.bean.AnexoBean;
import pe.gob.onpe.verificadoc.bean.DocumentoObjBean;
import pe.gob.onpe.verificadoc.bean.VerificaDocBean;
import pe.gob.onpe.verificadoc.service.CaptchaSingletonService;
import pe.gob.onpe.verificadoc.service.DocumentoObjService;
import pe.gob.onpe.verificadoc.service.VerificaDocService;
import pe.gob.onpe.verificadoc.web.util.Constantes;
import pe.gob.onpe.verificadoc.web.util.Utility;

@Controller(value = "inicioController")
@RequestMapping("/inicio.do")
public class inicioController{

    private transient final Log LOGGER = LogFactory.getLog(getClass());

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private VerificaDocService verificaDocService;
    
    @Autowired
    private DocumentoObjService documentoObjService;
    
    public inicioController() {
        
    }
    
    
    @RequestMapping(method = RequestMethod.GET)
    public String doShowForm(HttpServletRequest request, Model model) {
        ServletContext sc = request.getSession().getServletContext();
        Utility.getInstancia().pathPdfs = sc.getRealPath("/"+ applicationProperties.getRutaTemporal() +"/");
        System.out.println("OK CAPTCHA INICIO==>");      
        String cookieSessionId = request.getSession().getId();
        VerificaDocBean verificaDocBean = new VerificaDocBean();
        verificaDocBean.setCookieSessionId(cookieSessionId);
        model.addAttribute("listaTipoDoc",verificaDocService.getTiposDocs());
        model.addAttribute("verificaDocBean",  verificaDocBean);                
        return "inicio";
    }
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goQr")
    public String goQr(VerificaDocBean verificaDocBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        String nomFile = "";
        int maxConsultas = applicationProperties.getNumConsultasMaxQr();
        List<AnexoBean> listaAnexos = new ArrayList<AnexoBean>();
        
        try {
            verificaDocBean.setNuAnn(Utility.getInstancia().descifrar(verificaDocBean.getNuAnn(),Constantes.SGD_SECRET_KEY_PARAMETROS));
        
            verificaDocBean.setNuEmi(Utility.getInstancia().descifrar(verificaDocBean.getNuEmi(),Constantes.SGD_SECRET_KEY_PARAMETROS));
            
            String llaveDocMap = verificaDocBean.getNuAnn()+verificaDocBean.getNuEmi();
            Integer numConsultasActual = Utility.getInstancia().docsAccesoMap.get(llaveDocMap);
            
            if(numConsultasActual == null) numConsultasActual = 0;
            
            //Validamos que el doc no haya superado el máximo numero de consultas
            if(numConsultasActual < maxConsultas){
                
                verificaDocBean.setClaveAccesoDoc(Utility.getInstancia().descifrar(verificaDocBean.getClaveAccesoDoc(),Constantes.SGD_SECRET_KEY_PARAMETROS));

                verificaDocBean.setClaveAccesoDoc(verificaDocBean.getClaveAccesoDoc().toUpperCase());

                String codV = verificaDocService.getCodVerifica(verificaDocBean.getNuAnn(), verificaDocBean.getNuEmi());

                if(StringUtils.isEmpty(codV) || !codV.equals(verificaDocBean.getClaveAccesoDoc()) ){
                    mensaje = "¡ Documento No Encontrado !";
                }else{
                    DocumentoObjBean docObjBean = documentoObjService.leerDocumento(verificaDocBean.getNuAnn(), verificaDocBean.getNuEmi());

                    nomFile = writePdf(docObjBean, verificaDocBean, request);

                    if(!StringUtils.isEmpty(nomFile)){
                        numConsultasActual++;
                        Utility.getInstancia().docsAccesoMap.put(llaveDocMap, numConsultasActual);
                        
                        listaAnexos = verificaDocService.getAnexosList(verificaDocBean.getNuAnn(), verificaDocBean.getNuEmi());
                        if(listaAnexos.isEmpty()){
                            model.addAttribute("hayAnexos", "");
                        }else{
                            model.addAttribute("hayAnexos", "1");
                        }
                        
                        model.addAttribute("nomFile", nomFile);
                        mensaje = "OK";
                    }else{
                        mensaje = "¡ Documento No Encontrado !";
                    }
                }
            }else{
                mensaje = "¡ ha superado el límite máximo de consultas por día !";
            }
        
        } catch (Exception ex) {
            mensaje = "¡ Documento No Encontrado !";
            LOGGER.error(ex.getMessage(), ex);
        }
        
        if (mensaje.equals("OK")) {
            model.addAttribute("listaAnexos", listaAnexos);
            return "/visorPDF";
        } else {
            model.addAttribute("msjEmision", mensaje);
            return "/messages";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(VerificaDocBean verificaDocBean, HttpServletRequest request, BindingResult result, Model model) throws IOException{
        String mensaje = "NO_OK";
        String nomFile = null;    
        Boolean captchaValido =Boolean.FALSE;
        List<AnexoBean> listaAnexos = null;
        String captchaId = verificaDocBean.getCookieSessionId();
        
        verificaDocBean.setTextoCaptcha(verificaDocBean.getTextoCaptcha().toUpperCase());
        verificaDocBean.setClaveAccesoDoc(verificaDocBean.getClaveAccesoDoc().toUpperCase());
        verificaDocBean.setNumDoc(verificaDocBean.getNumDoc().toUpperCase());
        
        String textoCaptcha = verificaDocBean.getTextoCaptcha();
        String validaObli = validaCamposObligatorio(verificaDocBean);
        
        if(StringUtils.isEmpty(validaObli)){
            try{
                captchaValido = CaptchaSingletonService.getInstance().validateResponseForID(captchaId, textoCaptcha.toUpperCase());
            } catch (CaptchaServiceException e) {
                LOGGER.error(e.getMessage(), e);
                captchaValido =Boolean.FALSE;
                result.rejectValue("textoCaptcha", null, "Error al validar texto de la imagen.");
            }

            if(captchaValido){
                VerificaDocBean doc = verificaDocService.getDocRemitos(verificaDocBean);
                if(doc != null ){
                    DocumentoObjBean docObjBean = documentoObjService.leerDocumento(doc.getNuAnn(), doc.getNuEmi());
                    
                    nomFile = writePdf(docObjBean, verificaDocBean, request);
                    
                    if(!StringUtils.isEmpty(nomFile)){
                        mensaje = "OK";
                        listaAnexos = verificaDocService.getAnexosList(doc.getNuAnn(), doc.getNuEmi());
                    }else{
                        mensaje = "¡ Documento No Encontrado !";
                    }
                     
                    
                }else{
                    mensaje = "¡ Documento No Encontrado !";
                }
            }else{
                mensaje = "Código de Imagen Incorrecto.";
            }
        }else{
            mensaje = "Campos Obligatorios: " + validaObli + ".";
        }

        if (mensaje.equals("OK")) {
            model.addAttribute("nomFile", nomFile);
            model.addAttribute("hayAnexos", "");
            model.addAttribute("listaAnexos", listaAnexos);
            return "/visorPDF";
        } else {
            model.addAttribute("msjEmision", mensaje);
            return "/messages";
        }        
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCargarAnexos")
    public String goCargarAnexos(VerificaDocBean verificaDocBean, HttpServletRequest request, BindingResult result, Model model){
        String mensaje = "NO_OK";
        List<AnexoBean> listaAnexos = null;
        String captchaId = verificaDocBean.getCookieSessionId();
        
        verificaDocBean.setTextoCaptcha(verificaDocBean.getTextoCaptcha().toUpperCase());
        verificaDocBean.setClaveAccesoDoc(verificaDocBean.getClaveAccesoDoc().toUpperCase());
        verificaDocBean.setNumDoc(verificaDocBean.getNumDoc().toUpperCase());
        
        String textoCaptcha = verificaDocBean.getTextoCaptcha();

        VerificaDocBean doc = verificaDocService.getDocRemitos(verificaDocBean);
        if(doc != null ){
            listaAnexos = verificaDocService.getAnexosList(doc.getNuAnn(), doc.getNuEmi());
            mensaje = "OK";
        }else{
            mensaje = "";
        }
        if (mensaje.equals("OK")) {
            model.addAttribute("listaAnexos", listaAnexos);
            return "/anexos";
        } else {
            return "";
        }        
    }
    
    
    private String writePdf(DocumentoObjBean docObjBean, VerificaDocBean verificaDocBean, HttpServletRequest request){
        FileOutputStream fileOuputStream = null;
        
        
        if(StringUtils.isEmpty(verificaDocBean.getTipoDoc()) && StringUtils.isEmpty(verificaDocBean.getNumDoc())){
            verificaDocBean = verificaDocService.getDocRemitosBusq(verificaDocBean.getNuEmi(), verificaDocBean.getNuAnn());
        }
//        String tipoDoc = verificaDocService.getDescTipoDoc(verificaDocBean.getTipoDoc()); // ocultamos el tipo de doc
//        String nomFile = tipoDoc + "-" + verificaDocBean.getNumDoc().replace("/", "-") + verificaDocBean.getClaveAccesoDoc() + ".pdf";
        String nomFile = verificaDocBean.getNumDoc().replace("/", "") + verificaDocBean.getClaveAccesoDoc() + ".pdf";
        nomFile = nomFile.replace("-", "");
        
        try {
//            ServletContext sc = request.getSession().getServletContext();
//            String rutaPdf = sc.getRealPath("/"+ applicationProperties.getRutaTemporal() +"/");
            fileOuputStream = new FileOutputStream(Utility.getInstancia().pathPdfs + "//" + nomFile);
            fileOuputStream.write(docObjBean.getDocumento());
            System.out.println("URL = " + this.getClass().getProtectionDomain().getCodeSource().getLocation());
        } catch (IOException e) {
            LOGGER.error(e.getMessage(), e);
            nomFile = null;
        } finally {
            if (fileOuputStream != null) {
                try {
                    fileOuputStream.close();
                } catch (IOException e) {
                    LOGGER.error(e.getMessage(), e);
                }
            }
        }
        
        return nomFile;
    }
    
    private String validaCamposObligatorio(VerificaDocBean verificaDocBean){
        String resultado = "";
        String[] msj = new String[4];
        int i = 0;
        if(StringUtils.isEmpty(verificaDocBean.getTipoDoc()) 
            || StringUtils.isEmpty(verificaDocBean.getNumDoc()) 
            || StringUtils.isEmpty(verificaDocBean.getClaveAccesoDoc())
            || StringUtils.isEmpty(verificaDocBean.getTextoCaptcha()) ){
            
            if(StringUtils.isEmpty(verificaDocBean.getTipoDoc())){
                msj[i] = "Tipo de Documento";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getNumDoc())){
                msj[i]  = "Número de Documento";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getClaveAccesoDoc())){
                msj[i] = "Código de Verificación";
                i++;
            }
            if(StringUtils.isEmpty(verificaDocBean.getTextoCaptcha())){
                msj[i] = "Código Imágen Captcha";
                i++;
            }
            
            resultado = msj[0];
            
            if(i > 1){
                for(int j = 1; j < i; j++){
                    resultado = resultado + ", " + msj[j];
                }
            }
            
        }
        
        return resultado;
    }
    
}
