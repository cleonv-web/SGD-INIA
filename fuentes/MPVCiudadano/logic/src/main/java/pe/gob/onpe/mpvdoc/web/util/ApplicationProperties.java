package pe.gob.onpe.mpvdoc.web.util;

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
 public String getLogoReporteB64() {
         String retval="";
        try {
            retval=(properties.getProperty("B64_LOGO_REPORTE")==null?"":properties.getProperty("B64_LOGO_REPORTE"));
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
    public String getFileSizeMaxDocumento() {
        return properties.getProperty("FILE_SIZE_MAX");
    }
    public String getFileSizeMaxAnexo() {
        return properties.getProperty("FILE_SIZE_MAX_ANEXO");
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
    public String getLinkConsultaTuTramite() {
        return properties.getProperty("CONSULTA_TU_TRAMITE");
    }
    public String getTerminosTitulo() {
        return properties.getProperty("TERMINO_TITULO");
    }
    
    public String getTerminosI() {
        return properties.getProperty("TERMINO_I");
    }
    public String getTerminosII() {
        return properties.getProperty("TERMINO_II");
    }
    public String getTerminosIII() {
        return properties.getProperty("TERMINO_III");
    }
    public String getTerminosIV() {
        return properties.getProperty("TERMINO_IV");
    }
    public String getTerminosV() {
        return properties.getProperty("TERMINO_V");
    }
    public String getTerminosVI() {
        return properties.getProperty("TERMINO_VI");
    }
    public String getTerminosVII() {
        return properties.getProperty("TERMINO_VII");
    }
    public String getTerminosVIII() {
        return properties.getProperty("TERMINO_VIII");
    }
    public String getTerminosIX() {
        return properties.getProperty("TERMINO_IX");
    }
    public String getTerminosX() {
        return properties.getProperty("TERMINO_X");
    }
    public String getTerminosXI() {
        return properties.getProperty("TERMINO_XI");
    }
    public String getTerminosXII() {
        return properties.getProperty("TERMINO_XII");
    }
    public String getTerminosXIII() {
        return properties.getProperty("TERMINO_XIII");
    }
    public String getTerminosXIV() {
        return properties.getProperty("TERMINO_XIV");
    }
    public String getTerminosXV() {
        return properties.getProperty("TERMINO_XV");
    }
      public String getLinkMesaPartesCiudadano() {
        return properties.getProperty("MESA_PARTES_CIUDADANO");
    }
    public String getEmailAsunto() {
        return properties.getProperty("EMAIL_ASUNTO");
    }
    public String getEmailContenidoI() {
        return properties.getProperty("EMAIL_CONTENIDO_I");
    }
    public String getEmailContenidoII() {
        return properties.getProperty("EMAIL_CONTENIDO_II");
    }
    public String getEmailContenidoIII() {
        return properties.getProperty("EMAIL_CONTENIDO_III");
    }
    public String getEmailContenidoIV() {
        return properties.getProperty("EMAIL_CONTENIDO_IV");
    }
    public String getEmailContenidoV() {
        return properties.getProperty("EMAIL_CONTENIDO_V");
    }
    public String getEmailContenidoVI() {
        return properties.getProperty("EMAIL_CONTENIDO_VI");
    }
                
}
