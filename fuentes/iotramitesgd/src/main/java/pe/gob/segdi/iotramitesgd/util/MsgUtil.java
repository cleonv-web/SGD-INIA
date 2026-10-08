package pe.gob.segdi.iotramitesgd.util;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

public class MsgUtil {

    public static void msgInfo(String resumen, String detalle) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_INFO, resumen, detalle);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public static void msgWarning(String resumen, String detalle) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_WARN, resumen, detalle);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public static void msgError(String resumen, String detalle) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_ERROR, resumen, detalle);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

    public static void msgFatal(String resumen, String detalle) {
        FacesMessage message = new FacesMessage(FacesMessage.SEVERITY_FATAL, resumen, detalle);
        FacesContext.getCurrentInstance().addMessage(null, message);
    }

}
