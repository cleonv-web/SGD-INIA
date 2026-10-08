package pe.gob.onpe.tramitedoc.web.controller;


import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.BindingResult;
import pe.gob.onpe.autentica.model.RemoteAttribs;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.Mensaje;

import javax.servlet.http.HttpServletRequest;

public class GeneralController {
    protected final Log logger = LogFactory.getLog(getClass());

    public void printErrorMessaje( BindingResult result, Mensaje mensaje){
     //result.reject(null, "Error ["+mensaje.getCoRespuesta() +"]: " +  mensaje.getDeRespuesta());
     result.reject(null,  mensaje.getDeRespuesta());
    }

    protected void loadUsuarioRemoteAttribs(Usuario usuario, HttpServletRequest request){
        String ip = (String) request.getAttribute("REMOTE_ADDR");
        if (ip == null || ip.length() == 0) {
            ip = request.getRemoteAddr();
        }

        RemoteAttribs remoteAttribs= new RemoteAttribs();
        remoteAttribs.setIpCliente(ip);
        remoteAttribs.setRemoteHost(request.getRemoteHost());
        remoteAttribs.setRemoteUser(request.getRemoteUser());

        usuario.setIpPC(remoteAttribs.getIpCliente());
    }

}
