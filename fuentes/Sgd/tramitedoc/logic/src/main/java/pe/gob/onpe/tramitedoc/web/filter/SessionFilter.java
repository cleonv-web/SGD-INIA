/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.filter;

import org.springframework.util.StringUtils;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.web.servlet.XSSRequestWrapper;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import javax.servlet.http.Cookie;

public class SessionFilter implements Filter {
    private String urlLogin;
    private String urlIndex;
    private String urlInicio;
    private String urlExpira;
    private String mode="DENY";//SAMEORIGIN

    //@Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain) throws IOException, ServletException {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
	HttpServletResponse hResponse = (HttpServletResponse) response;
        //hResponse.setContentType("text/plain"); 
        hResponse.setHeader("X-Frame-Options", this.mode); 
        hResponse.setHeader("X-XSS-Protection", "1; mode=block");
        hResponse.setHeader("X-Content-Type-Options","nosniff");
        hResponse.setHeader("Pragma","no-cache");
        hResponse.setHeader("Cache-Control","no-store, no-cache, must-revalidate, max-age=0, post-check=0, pre-check=0");
        hResponse.setHeader("Vary", "Accept-Encoding");
        hResponse.setHeader("Expires", "0" ); 
        hResponse.setHeader("Set-Cookie", "key=value; Secure;HttpOnly; SameSite=strict");//COMENTADO POR NO LEVANTAR WEBSOKECT POSTGRESQL 
        //chain.doFilter(new XSSRequestWrapper((HttpServletRequest) request), response);
        /*final HttpServletRequest httpRequest = (HttpServletRequest) request;
        XSSRequestWrapper wrapper = new XSSRequestWrapper(httpRequest) {
            @Override
            public String getHeader(String name) {
              final String value = httpRequest.getParameter(name);
              if (value != null) {
                return value;
              }
              return super.getHeader(name);
            }
          };
          chain.doFilter(wrapper, response);*/

        String uri = httpServletRequest.getRequestURI();        
        int x=0;
        if(uri!=null){
            x = uri.lastIndexOf("/");
            if(x>0){
                uri = uri.substring(x);
            }
        }

        HttpSession sesion = httpServletRequest.getSession(false);

        if(uri.equals(this.urlLogin)){
                RequestDispatcher dispatcher = request.getRequestDispatcher(urlLogin);
                if(sesion == null){
                    dispatcher = request.getRequestDispatcher("/index.jsp");
                    dispatcher.forward(request, response);
                }
                else{
                    Usuario usuario = (Usuario) ServletUtility.getInstancia().loadSessionAttribute(httpServletRequest, "usuario");
                    if(usuario!=null  && StringUtils.hasLength(usuario.getNuDni()) && StringUtils.hasLength(usuario.getSessionId()) ){
                       dispatcher = request.getRequestDispatcher("/principal.do");
                       setCookieidChannelws(hResponse, sesion, httpServletRequest.getContextPath());
                       dispatcher.forward(request, response);
                    }
                    else{
//                       chain.doFilter(request, response);
                        //ServletUtility.getInstancia().invalidateSession(httpServletRequest);
                        //sesion=ServletUtility.getInstancia().createSession(httpServletRequest);
                        ServletUtility.getInstancia().saveSessionAttribute(httpServletRequest, "usuario", usuario);
                        setCookieidChannelws(hResponse, sesion, httpServletRequest.getContextPath());
                        dispatcher.forward(request, response);
                    }
                }
        }
        else if(uri.equals("/srDepAcceso.do")){
                 chain.doFilter(request, response);
            }
        else if(uri.equals("/logout.do")){
                 chain.doFilter(request, response);
            }
        else if(uri.equals("/inicioErr.do")){
                 chain.doFilter(request, response);
            }
        else if(uri.equals("/sesionExpira.do")){
                 chain.doFilter(request, response);
            }
        else if(uri.equals(this.urlInicio) ){
            RequestDispatcher dispatcher = request.getRequestDispatcher(urlInicio);
            Usuario usuario = (Usuario) ServletUtility.getInstancia().loadSessionAttribute(httpServletRequest, "usuario");
            if(usuario!=null  && StringUtils.hasLength(usuario.getNuDni()) && StringUtils.hasLength(usuario.getSessionId())){

               dispatcher.forward(request, response);
            }
            else{
               chain.doFilter(request, response);
            }
        }
        else{
            RequestDispatcher dispatcher = request.getRequestDispatcher(urlExpira);
            if(sesion == null){
                dispatcher.forward(request, response);
            }
            else{
                Usuario usuario = (Usuario) ServletUtility.getInstancia().loadSessionAttribute(httpServletRequest, "usuario");
                if(usuario!=null  && StringUtils.hasLength(usuario.getNuDni()) && StringUtils.hasLength(usuario.getSessionId())){
                    setCookieidChannelws(hResponse, sesion, httpServletRequest.getContextPath());
                   chain.doFilter(request, response);
                }
                else{
                    dispatcher.forward(request, response);
                }
            }
        }
    }


    //@Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.urlLogin = filterConfig.getInitParameter("urlLogin");
        this.urlIndex = filterConfig.getInitParameter("urlIndex");
        this.urlInicio = filterConfig.getInitParameter("urlInicio");
        this.urlExpira = filterConfig.getInitParameter("urlExpira");

        if (urlLogin == null || urlLogin.trim().length() == 0) {
        //Error al cargar la url de login
        throw new ServletException("No se ha configurado URL de login");
        }
    }

    //@Override
    public void destroy() {
    }
    
    private void setCookieidChannelws(HttpServletResponse hRes, HttpSession sesion, String contextPath){
         
        if(sesion!=null){
            Cookie crunchifyCookie = new Cookie("idChannel", sesion.getId());
            crunchifyCookie.setMaxAge(sesion.getMaxInactiveInterval());              
            crunchifyCookie.setPath(contextPath);
            crunchifyCookie.setSecure(true);//COMENTADO POR NO LEVANTAR WEBSOKECT POSTGRESQL          
            //crunchifyCookie.setHttpOnly(true);
            //crunchifyCookie.setComment(SAME_SITE_STRICT_COMMENT);

            hRes.addCookie(crunchifyCookie);              
        }
    }
}
