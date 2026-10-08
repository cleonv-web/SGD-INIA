package pe.gob.onpe.sgdtask.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;

import java.util.Properties;

/**
 * Created by IntelliJ IDEA. User: crosales Date: 27/04/12 Time: 02:47 PM To
 * change this template use File | Settings | File Templates.
 */
@Component("applicationProperties")
public class ApplicationProperties {

    @Autowired
    @Qualifier(value = "applicationProps")
    private Properties properties;

    private String noEntidad;
    private String coEntidad;
    private String urlSoapExt;
    private String deSiglaEntidad;

    public String getDeSiglaEntidad() {
        return properties.getProperty("sigla_entidad");
    }
    
    public String getUrlSoapExt() {
        return properties.getProperty("url_soap_ext");
    }
    public String getNoEntidad() {
        return properties.getProperty("no_entidad");
    }

    public String getCoEntidad() {
        return properties.getProperty("co_entidad");
    }
}
