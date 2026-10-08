package pe.gob.onpe.consultadoc.web.controller;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import pe.gob.onpe.consultadoc.web.util.ApplicationProperties;
import com.octo.captcha.service.CaptchaServiceException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.servlet.ServletContext;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod; 
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.service.CaptchaSingletonService; 
import pe.gob.onpe.consultadoc.web.util.Constantes;
import pe.gob.onpe.consultadoc.web.util.Utility;
import pe.gob.onpe.consultadoc.service.ConsultaDocService;

@Controller(value = "inicioController")
@RequestMapping("/inicio.do")
public class inicioController{

    private transient final Log LOGGER = LogFactory.getLog(getClass());

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private ConsultaDocService verificaDocService;
     
    
    public inicioController() {
        
    }
    
    
    @RequestMapping(method = RequestMethod.GET)
    public String doShowForm(HttpServletRequest request, Model model) {
        //ServletContext sc = request.getSession().getServletContext();
        //Utility.getInstancia().pathPdfs = sc.getRealPath("/"+ applicationProperties.getRutaTemporal() +"/");
        
        String cookieSessionId = request.getSession().getId();
        ConsultaDocBean consultaDocBean = new ConsultaDocBean();
        consultaDocBean.setCookieSessionId(cookieSessionId);
        //model.addAttribute("listaTipoDoc",verificaDocService.getTiposDocs());
        model.addAttribute("consultaDocBean",  consultaDocBean);                
        return "inicio";
    }
     
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(ConsultaDocBean consultaDocBean, HttpServletRequest request, BindingResult result, Model model) throws IOException{
        String mensaje = "NO_OK";
        ConsultaDocBean docExpediente = null;    
        Boolean captchaValido =Boolean.FALSE; 
        String captchaId = consultaDocBean.getCookieSessionId();
        
        consultaDocBean.setTextoCaptcha(consultaDocBean.getTextoCaptcha().toUpperCase());
        //verificaDocBean.setClaveAccesoDoc(verificaDocBean.getClaveAccesoDoc().toUpperCase());
        //verificaDocBean.setNumDoc(verificaDocBean.getNumDoc().toUpperCase());
        
        String textoCaptcha = consultaDocBean.getTextoCaptcha();
        String validaObli = validaCamposObligatorio(consultaDocBean);
        
        if(StringUtils.isEmpty(validaObli)){
            try{
                captchaValido = CaptchaSingletonService.getInstance().validateResponseForID(captchaId, textoCaptcha.toUpperCase());
            } catch (CaptchaServiceException e) {
                LOGGER.error(e.getMessage(), e);
                captchaValido =Boolean.FALSE;
                result.rejectValue("textoCaptcha", null, "Error al validar texto de la imagen.");
            }

            if(captchaValido){
                List<ConsultaDocBean> doc = verificaDocService.getBuscarExpediente(consultaDocBean.getNroExpediente());
                if(doc != null && doc.size()>0 ){
                    docExpediente=doc.get(0);
                    List<String> seguimiento = verificaDocService.SeguimientoOficinaAtencion(docExpediente.getAnio() + docExpediente.getNumeroEmision() + "1");
                    try {
                        if (seguimiento !=null && !seguimiento.get(0).isEmpty()) {
                           docExpediente.setOficinaAtencion(seguimiento.get(0));
                           docExpediente.setEstado(seguimiento.get(1));
                           docExpediente.setObsFinalizar(seguimiento.get(2)); 
                        }
                    }
                    catch(Exception ex){
                        ex.printStackTrace();
                    }
                    mensaje = "OK";
                    
                }else{
                    mensaje = "¡Documento No Encontrado!";
                }
            }else{
                mensaje = "Código de Imagen Incorrecto.";
            }
        }else{
            mensaje = "Campos Obligatorios: " + validaObli + ".";
        }

        if (mensaje.equals("OK")) {
            model.addAttribute("docExpediente", docExpediente); 
            return "/viewExpediente";
        } else {
            model.addAttribute("msjEmision", mensaje);
            return "/messages";
        }        
    }
    
    
    
    
    private String validaCamposObligatorio(ConsultaDocBean verificaDocBean){
        String resultado = "";
        String[] msj = new String[4];
        int i = 0;
        if( StringUtils.isEmpty(verificaDocBean.getNroExpediente())
            || StringUtils.isEmpty(verificaDocBean.getTextoCaptcha()) ){                         
            if(StringUtils.isEmpty(verificaDocBean.getNroExpediente())){
                msj[i] = "Código de Expediente";
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
