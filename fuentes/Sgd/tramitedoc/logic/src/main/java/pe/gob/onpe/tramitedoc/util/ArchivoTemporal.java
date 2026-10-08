/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.util;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import org.springframework.beans.factory.annotation.Autowired;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

/**
 *
 * @author WCutipa
 */
public abstract class ArchivoTemporal {

    
    public ArchivoTemporal()
    {
    }

    public static byte[] leerArchivo(String nombreArchivo)
    {
        boolean inLeerDoc = false;
        String vret = "NO";
        String rutaArchivo= nombreArchivo;
        byte[] retorno = null;
        ByteArrayOutputStream ous = null;
        InputStream ios = null;
        
        try{
            File fileDoc = new File(rutaArchivo); 
            if (fileDoc.exists() && fileDoc.length() > 0){
                inLeerDoc=true;  
            }else{
                inLeerDoc=false;  
            }
            if (inLeerDoc){
                byte[] buffer = new byte[4096];
                ous = new ByteArrayOutputStream();
                ios = new FileInputStream(fileDoc);
                int read = 0;
                while ( (read = ios.read(buffer)) != -1 ) {
                    ous.write(buffer, 0, read);
                } 
                ous.flush();
                retorno = ous.toByteArray();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }finally { 
            try {
                 if ( ous != null ) 
                     ous.close();
            } catch ( IOException e) {}
            try {
                 if ( ios != null ) 
                      ios.close();
            } catch ( IOException e) {}
        }
        
        //Borrar Archivo
        
        return(retorno);
    }
    
    
}
