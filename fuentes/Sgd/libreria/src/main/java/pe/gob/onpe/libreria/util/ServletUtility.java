package pe.gob.onpe.libreria.util;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Enumeration;

/**
 * Created by IntelliJ IDEA.
 * User: crosales
 * Date: 26/01/12
 * Time: 03:52 PM
 * To change this template use File | Settings | File Templates.
 */
public class ServletUtility {
    private static boolean instanciated = false;
    private static ServletUtility servletUtility;

    public static ServletUtility getInstancia() {
        if (!ServletUtility.instanciated) {
            ServletUtility.servletUtility = new ServletUtility();
            ServletUtility.instanciated = true;
        }
        return ServletUtility.servletUtility;
    }

    public String recuperarParametro(HttpServletRequest request, String parametro) {
        return request.getParameter(parametro);
    }

    /**
     * Recupera el id de tipo long asociado a un parametro de la página JSF
     * @param parametro
     * @return
     */

    public Integer recuperarIdParametro(HttpServletRequest request, String parametro) {
        Integer i;
        try {
            i = Integer.parseInt(recuperarParametro(request, parametro));
        } catch (NumberFormatException e) {
            i = -1;
        }
        return i;
    }

    public Enumeration requestParameterNames(HttpServletRequest request) {
        return request.getParameterNames();
    }

    public Enumeration requestAttributesNames(HttpServletRequest request) {
        return request.getAttributeNames();
    }

    public Enumeration sessionAttributesNames(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if(session!=null){
            return session.getAttributeNames();
        }
        return null;
    }

    public String loadRequestParameter(HttpServletRequest request, String name) {
        if (request.getParameter(name) == null) {
            return "";
        } else {
            return request.getParameter(name);
        }
    }

    public String[] loadRequestParameterValues(HttpServletRequest request, String name) {
        return request.getParameterValues(name);
    }

    public void saveRequestAttribute(HttpServletRequest request, String name, Object value) {
        request.setAttribute(name, value);

    }

    public Object loadRequestAttribute(HttpServletRequest request, String name) {
        return request.getAttribute(name);
    }

    public boolean hasSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        return (session != null && !session.isNew());
    }

    public HttpSession createSession(HttpServletRequest request) {
        return request.getSession(true);
    }

    public void invalidateSession(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    public Object loadSessionAttribute(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session == null) {
            return null;
        }
        return session.getAttribute(name);
    }

    public void saveSessionAttribute(HttpServletRequest request, String name, Object value) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session = createSession(request);
        }
        session.setAttribute(name, value);
    }

    public void removeAttribute(HttpServletRequest request, String name) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            request.getSession().removeAttribute(name);
        }
    }

    public void saveResponseContentType(HttpServletResponse response, String type) {
        response.setContentType(type);
    }

    public void saveResponseContentLength(HttpServletResponse response, int length) {
        response.setContentLength(length);
    }

    public OutputStream loadResponseOutputStream(HttpServletResponse response) throws IOException {
        return response.getOutputStream();
    }

    public PrintWriter loadResponseWriter(HttpServletResponse response) throws IOException {
        return response.getWriter();
    }

    public void saveHeader(HttpServletResponse response, String cacheControl, String NoCache) throws IOException {
        response.setHeader(cacheControl, NoCache);
    }

    public String loadRemoteAddr(HttpServletRequest request) {
        return request.getRemoteAddr();
    }

    public String loadSessionId(HttpServletRequest request) {
        String retval = "";
        HttpSession session = request.getSession(false);
        if (session != null) {
            retval = session.getId();
        }
        return retval;
    }

    public void imprimeMensaje(HttpServletResponse response, String pMensaje) throws Exception {
        this.saveResponseContentType(response, "text/xml; charset=ISO-8859-1");
        this.saveHeader(response, "Cache-Control", "no-cache");

        PrintWriter out = this.loadResponseWriter(response);
        out.write(pMensaje);
        out.close();
    }

}
