package pe.gob.onpe.tramitedoc.web.controller;

import javax.servlet.http.HttpServletRequest;
import org.apache.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

@ControllerAdvice
public class ExceptionControllerAdvice {
        
        private static Logger logger=Logger.getLogger("ExceptionSGD");
        
        @ResponseStatus(value = HttpStatus.INTERNAL_SERVER_ERROR)
        @ExceptionHandler(Exception.class)
	public ModelAndView exception(HttpServletRequest req, Exception ex) {
                StringBuffer mensaje = new StringBuffer();
                Usuario usuario = Utilidades.getInstancia().loadUserFromSession(req);
                String vUsu = "";
                String vUrl = "";
                
                if (usuario!=null){
                    vUsu = usuario.getCoUsuario();
                }
                if (req!=null){
                    vUrl = req.getRequestURL().toString();
                }

                mensaje.append(vUsu+":");
                mensaje.append(vUrl);
		
		ModelAndView mav = new ModelAndView("exception");
		mav.addObject("name", ex.getClass().getSimpleName());
                mav.addObject("url", vUsu+":"+vUrl);
		mav.addObject("exception", ex);
                
                logger.error(mensaje,ex);

		return mav;
	}
        
    
}
