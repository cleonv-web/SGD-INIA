/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.service.DocumentoAnexoService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Controller
@RequestMapping("/{version}/srDocAnexo.do")
public class DocumentoAnexoController {
    
    @Autowired
    private ApplicationProperties applicationProperties; 
    
    @Autowired
    private DocumentoAnexoService documentoAnexoService;    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCargaDocAnexoFirmado",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goCargaDocAnexoFirmado(HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);
       
        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuAne = ServletUtility.getInstancia().loadRequestParameter(request, "nuAne");
        String pNuSecFirma = ServletUtility.getInstancia().loadRequestParameter(request, "nuSecFirma");
        
       pNuSecFirma=applicationProperties.getRutaTemporal()+"//"+pNuSecFirma;
       try{
          mensaje = documentoAnexoService.cargaDocAnexoFirmado(pNuAnn,pNuEmi,pNuAne,pNuSecFirma,usuario.getCoUsuario(),docSession.getNoDocumento(),usuario.getNuDni(),applicationProperties.getNroRucInstitu());

       }catch(validarDatoException e){
           mensaje=(e.valorMsg);
       }catch(Exception e){
           mensaje = e.getMessage();
       }
       
       vRespuesta = mensaje;
       if (mensaje.equals("OK")) {
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\"}");
       } else {
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(vRespuesta);                            
            retval.append("\"}");
       }
       return retval.toString();
    }    
}
