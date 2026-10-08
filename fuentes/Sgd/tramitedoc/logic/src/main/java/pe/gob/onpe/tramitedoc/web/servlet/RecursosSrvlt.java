/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.servlet;

import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.model.UsuarioAcceso;
import pe.gob.onpe.tramitedoc.service.RecursosService;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

/**
 *
 * @author crosales
 */
public class RecursosSrvlt extends HttpServlet {

    private RecursosService recursosService;
    private static final String CONTENT_TYPE = "text/html; charset=UTF-8";
    private static final String PIXEL_B64  = "R0lGODlhAQABAIAAAP///wAAACH5BAEUAAAALAAAAAABAAEAAAICRAEAOw==";
    //private static final byte[] PIXEL_BYTES = Base64.decode(PIXEL_B64);

    @Override
    public void init() throws ServletException {
        super.init();    //To change body of overridden methods use File | Settings | File Templates.
        // este es para acceder el servicio del spring

        //this.recursosService = (RecursosService)WebApplicationContextUtils(getServletContext()).getBean("recursosService");
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        HttpSession session = request.getSession(false);
        if (session!=null) {
            if (accion.equals("opcionesMenu")) {
                uploadMenu(request, response);
            } else if (accion.equals("loadObjs")) {
                loadObjs(request, response);
            }
        }
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void uploadMenu(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
//         for(UsuarioAcceso usuarioAcceso: usuario.getUsuarioAccesos()){
//            listMenu.add(new MenuBean("M"+usuarioAcceso.getCoModulo()+usuarioAcceso.getCoOpcion()+usuarioAcceso.getCoSubopcion(),"OP","OP","1"));
//         }
        boolean sucessfull = false;
        String valMenu = new String();
        String vtiUsuario;
        valMenu = "{'rows': [";
        List<UsuarioAcceso> olist = usuario.getUsuarioAccesos();
        UsuarioAcceso obean = new UsuarioAcceso();
        StringBuffer retval = new StringBuffer();
        StringBuffer prepare = new StringBuffer();
        vtiUsuario = "0";
        valMenu += "{'cell':[" +
                   "'"+"TIUSUARIO'," +
                   "'" +vtiUsuario+ "'" +
                   "]}";
        int i = 0;
        try {
            /*for (i=0; i<olist.size(); i++) {
                obean = olist.get(i);
                valMenu += ",{'cell':[" +
                           "'"+"MM"+obean.getCoModulo()+obean.getCoOpcion()+obean.getCoSubopcion()+ "'," +
                           "'1'" +
                           "]}";
            }
            // Verificar si existe opciones adicionales
            if (usuario.getParametrosGlobales().getInValAccesoCvAg()==null || !(usuario.getParametrosGlobales().getInValAccesoCvAg().equals("1"))) {
                valMenu += ",{'cell':[" +
                           "'"+"MM010103000000'," +
                           "'2'" +
                           "]}";
                valMenu += ",{'cell':[" +
                           "'"+"MM020700000000'," +
                           "'2'" +
                           "]}";
            }
            //
            // OPCIÓN CIERRE: Verificar Tipo de local: Consulado o Agencia
            if (usuario.getParametrosGlobales().getTiLocal()!=null && usuario.getParametrosGlobales().getTiLocal().equals("6")) {
                valMenu += ",{'cell':[" +
                           "'"+"MM010201000000'," +
                           "'2'" +
                           "]}";
                valMenu += ",{'cell':[" +
                           "'"+"MM010202000000'," +
                           "'2'" +
                           "]}";
                valMenu += ",{'cell':[" +
                           "'"+"MM010203000000'," +
                           "'2'" +
                           "]}";
            } else {
                valMenu += ",{'cell':[" +
                           "'"+"MM010204000000'," +
                           "'2'" +
                           "]}";
            }
            //*/
            valMenu+="]}";
            prepare.append("'menuItem':" + valMenu);
            sucessfull = true;
        } catch(Exception e) {
            sucessfull = false;
            e.printStackTrace();
        }
        retval.append("{'retmenu':");
        retval.append(sucessfull);
        /*retval.append(", ");
        retval.append(prepare);*/
        retval.append("}");
//        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
//        this.recursosService = (RecursosService) applicationContext.getBean("recursosService");
        response.setContentType(CONTENT_TYPE);
        PrintWriter out = response.getWriter();
        out.println(retval);
        out.close();
        out.flush();
    }

    private void loadObjs(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        this.recursosService = (RecursosService) applicationContext.getBean("recursosService");
        response.setContentType(CONTENT_TYPE);
        response.setHeader("Cache-Control", "max-age=31556926");
        PrintWriter out = response.getWriter();
        out.println(recursosService.getAllObjects());
        out.close();
        out.flush();
    }


}
