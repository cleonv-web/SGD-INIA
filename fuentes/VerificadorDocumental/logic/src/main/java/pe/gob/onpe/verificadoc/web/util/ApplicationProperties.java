package pe.gob.onpe.verificadoc.web.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

@Component("applicationProperties")
public class ApplicationProperties {

    @Autowired @Qualifier(value = "applicationProps")
    private Properties properties;
       
    public Properties getProperties() {
        return properties;
    }
    
    public void inicializarConstantes(){
    }
       
    public void setProperties(Properties properties) {
        this.properties = properties;
    }
    
    public String getSiglaInstitucion(){
        String retval="";
        try {
            //retval=Utility.getInstancia().descifrar(properties.getProperty("sigla_institucion"),ConstantesSec.SGD_SECRET_KEY_PROPERTIES);
            retval=properties.getProperty("sigla_institucion");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public String getRutaTemporal() {
        String retval="";
        try {
            retval=properties.getProperty("ruta_temporal");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval;
    }

    public String getApplicationVersion() {
        return properties.getProperty("application.version");
    }

    public String getLocalCacheObjectsVersion() {
        return properties.getProperty("localCacheObjects.version");
    }

    public String getResourcesVersion() {
       return properties.getProperty("resources.version");
    }
    
    public int getNumConsultasMaxQr() {
       return Integer.parseInt(properties.getProperty("num_consultas_max_qr"));
    }
    
}
