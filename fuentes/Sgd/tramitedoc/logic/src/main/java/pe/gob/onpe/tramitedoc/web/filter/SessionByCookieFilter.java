/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.filter;

import org.springframework.util.StringUtils;
import pe.gob.onpe.tramitedoc.util.Constantes;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import pe.gob.onpe.tramitedoc.web.servlet.XSSRequestWrapper;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

/**
 *
 * @author crosales
 */
public class SessionByCookieFilter implements Filter {
    private String urlLogin;
    private String urlIndex;

    //@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {

        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpServletResponse httpServletResponse = (HttpServletResponse) response;
        HttpServletResponse hResponse = (HttpServletResponse) response;
        hResponse.setHeader("X-Frame-Options", "DENY");
        hResponse.setHeader("X-XSS-Protection", "1; mode=block");
        hResponse.setHeader("X-Content-Type-Options","nosniff");
        hResponse.setHeader("Pragma","no-cache");
        hResponse.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
        hResponse.setHeader("Vary", "Accept-Encoding");
        hResponse.setHeader("Expires", "0" );
        hResponse.setHeader("Set-Cookie", "key=value; Secure;HttpOnly; SameSite=strict");
        //chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);  

        String uri = httpServletRequest.getRequestURI();
        int x=0;
        if(uri!=null){
            x = uri.lastIndexOf("/");
            if(x>0){
                uri = uri.substring(x);
            }
        }

        String logedIn = Utilidades.getInstancia().giveMeCoockieValue(httpServletRequest, Constantes.SESSION_SATTUS);

        //HttpSession sesion = httpServletRequest.getSession(false);
        if(uri.equals(this.urlLogin)){
                RequestDispatcher dispatcher = request.getRequestDispatcher(urlIndex);
            //if(!StringUtils.hasLength(usuario.getNuDni())){
                if(!StringUtils.hasLength(logedIn)){
                    //login
                    chain.doFilter(request, response);
                }
                else{
                    //if(ServletUtility.getInstancia().loadSessionId(httpServletRequest).equals(usuario.getRemoteAttribs().getSessionId())){
                    if(logedIn.equals("1")){
                        // si es la misma session del usuario, le redirijes al panel proncipal y actualizamos la cookie
                        //Utilidades.getInstancia().saveUserInCookie(httpServletResponse, usuario);
                       dispatcher.forward(request, response);
                    }
                    else{
                        //login
                       chain.doFilter(request, response);
                    }
                }
        }
        else
            {
                RequestDispatcher dispatcher = request.getRequestDispatcher(urlLogin);
                if(!StringUtils.hasLength(logedIn)){
                    //login
                    dispatcher.forward(request, response);
                }
                else{
                    //if(ServletUtility.getInstancia().loadSessionId(httpServletRequest).equals(usuario.getRemoteAttribs().getSessionId())){
                    if(logedIn.equals("1")){
                        // tiene session activa, continua con la carga de la pagina
                       //Utilidades.getInstancia().saveUserInCookie(httpServletResponse, usuario);
                       chain.doFilter(request, response);
                    }
                    else{
                        //login
                        dispatcher.forward(request, response);
                    }
                }
            }
         
    }

    //@Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.urlLogin = filterConfig.getInitParameter("urlLogin");
        this.urlIndex = filterConfig.getInitParameter("urlIndex");

        if (urlLogin == null || urlLogin.trim().length() == 0) {
        //Error al cargar la url de login
        throw new ServletException("No se ha configurado URL de login");
        }
    }



    //@Override
    public void destroy() {
    }
}
