package pe.gob.segdi.iotramitesgd.util;

import org.jboss.logging.Logger;

import javax.faces.context.FacesContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;

/**
 * Resumen.
 * Objeto : Utilitarios.
 * Descripción : Contiene los métodos de uso común para todas las clases que forman parte del proyecto.
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 */

public class Utilitarios {
    private static Logger depurador = Logger.getLogger(Utilitarios.class.getName());

    /**
     * Obtiene el valor de un archivo properties.
     *
     * @param archivo Nombre del archivo properties, tipo String.
     * @param dato    Identificador de la linea del properties, tipo String.
     * @return variable Cadena obtenida del propwerties, tipo String.
     * @throws Exception Retorna mensaje de errror.
     */
    public static String ObtenerDatosProperties(String archivo, String dato) {
        if ("Parametros".equals(archivo) && "P_TEMP_DIRECTORY".equals(dato)) {
            return System.getProperty("java.io.tmpdir");
        }
        String variable = "";
        try {
            ResourceBundle bundle = ResourceBundle.getBundle("pe.gob.segdi.iotramitesgd.messageresource." + archivo);
            variable = bundle.getString(dato);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return variable;
    }

    public static boolean isNumeric(String cadena) {
        try {
            Integer.parseInt(cadena);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static String getDateString(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //dateFormat.parse()
        return dateFormat.format(date);
    }

    public static String getDateString2(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("ddMMyyyy");
        //dateFormat.parse()
        return dateFormat.format(date);
    }

    public static void executePdf(byte[] bytes, String name) {
        try {

            HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
            response.reset();
            response.setContentType("application/pdf");
            response.setContentLength(bytes.length);
            response.setHeader("Content-disposition", "attachment; filename=\"" + name + "\"");
            response.setHeader("Pragma", "no-cache");
            response.setDateHeader("Expires", 0);

            ServletOutputStream ouputStream = response.getOutputStream();
            ouputStream.write(bytes, 0, bytes.length);
            ouputStream.flush();
            ouputStream.close();
            FacesContext.getCurrentInstance().responseComplete();
        } catch (Exception e) {
            //depurador.info("ERROR ==>"+e);
        }
    }
}
