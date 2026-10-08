/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;

import java.io.IOException; 
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestMapping;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author oti2
 */
@Controller(value = "LoginValidateControler")
public class LoginValidateControler extends HttpServlet{
    
    /* @RequestMapping("/{version}/LoginValidate.do")
    public String showMainPanel(Model model,HttpServletRequest request){
        return "indexLogin";
    }*/
 @RequestMapping("/{version}/LoginValidate.do")
 protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
 
    	response.setContentType("text/html"); 
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
         usuario.setNuDni(null);
          usuario.setCoUsuario(null);        
        ServletUtility.getInstancia().invalidateSession(request);         
        response.sendRedirect("login.do");
    	//get parameters from request object.    	 
	}
}
