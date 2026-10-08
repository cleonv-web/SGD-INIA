package pe.gob.segdi.wsiotramite.util;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ResourceBundle;
/**
* Resumen.
* Objeto : Utilitarios.
* Descripción : Contiene los métodos de uso común para todas las clases que forman parte del proyecto.
* Fecha de Creación : 25/08/2012.
* Autor : Arturo Delgado Ostos.
* ------------------------------------------------------------------------
* Modificaciones
* Fecha            Nombre                     Descripción
* ------------------------------------------------------------------------
* 
*/

public class Utilitarios {

    
    /**
     * Obtiene el valor de un archivo properties.
     * @param archivo Nombre del archivo properties, tipo String.
     * @param dato Identificador de la linea del properties, tipo String.
     * @return variable Cadena obtenida del propwerties, tipo String.
     * @throws Exception Retorna mensaje de errror.
     */
     public static String ObtenerDatosProperties(String archivo, String dato) {
        String variable = "";
        try {
            ResourceBundle bundle = ResourceBundle.getBundle(archivo);
            variable = bundle.getString(dato);
        } catch (Exception e) {
        	e.printStackTrace();
        }
        return variable;
     }
     
     

     public static boolean isNumeric(String cadena){
		try {
			Integer.parseInt(cadena);
			return true;
		} catch (NumberFormatException nfe){
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
}
