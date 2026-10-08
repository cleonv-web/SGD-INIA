package pe.gob.onpe.libreria.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

import java.util.Properties;

public class Propiedades  {
    
    private static Propiedades p=null;
    private Properties prop;
    
    private Propiedades(String archivo) {        
        String properties = (String) System.getProperties().get("java.home") +
                                     System.getProperties().get("file.separator") + archivo + ".properties";
        this.prop = new Properties();
        InputStream is = null;
        File file = new File(properties);
        try {
            is = (InputStream) new FileInputStream(file);
            if (is == null) { 
                System.err.println("No se encontro el archivo " + properties); 
            }
            else {
                this.prop.load(is); 
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public static Propiedades getInstancia(String archivo) {
        if (p == null) {
            p = new Propiedades(archivo);
        }
        return p;
    }
    
    public String getPropiedad(String propiedad) {
        if (this.prop == null) {
            return null;
        }
        
        return prop.getProperty(propiedad);
    }
}