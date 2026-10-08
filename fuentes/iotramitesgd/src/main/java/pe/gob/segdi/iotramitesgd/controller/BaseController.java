package pe.gob.segdi.iotramitesgd.controller;

import org.primefaces.PrimeFaces;

import javax.el.ValueExpression;
import javax.faces.application.Application;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileInputStream;
import java.io.OutputStream;
import java.io.Serializable;

/**
 * Objeto : BaseController.java.
 * Descripción : Clase base para todos los controladores.
 * Fecha de Creación : 02/03/2018
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX       XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
public abstract class BaseController implements Serializable {
    private static final long serialVersionUID = 1L;

    public abstract String inicializarControladora();

    public abstract String inicializarControladora(String x);

    public abstract void inicializarVariable() throws Exception;

    public abstract void limpiarObjectos();

    private static final String KEEP_DIALOG_OPENED = "KEEP_DIALOG_OPENED";

    public ExternalContext getContext() {
        return FacesContext.getCurrentInstance().getExternalContext();
    }


    /**
     * Agrega un mensaje.
     *
     * @param severity - tipo de mensaje, tipo FacesMessage.Severity
     * @param --msg      - título del mensaje, tipo String.
     * @param detail   - Detalle del mensaje, tipo String.
     */
    private void addMessage(FacesMessage.Severity severity, String message, String detail) {
        FacesMessage facesMessage = new FacesMessage(severity, message, detail);
        FacesContext.getCurrentInstance().addMessage(null, facesMessage);
    }

    /**
     * Agrega un mensaje de información.
     *
     * @param msg    - título del mensaje, tipo String.
     * @param detail - Detalle del mensaje, tipo String.
     */
    public void addInfoMessage(String msg, String detail) {
        addMessage(FacesMessage.SEVERITY_INFO, msg, detail);
    }

    /**
     * Agrega un mensaje de precaución.
     *
     * @param msg    - título del mensaje, tipo String.
     * @param detail - Detalle del mensaje, tipo String.
     */
    public void addWarnMessage(String msg, String detail) {
        addMessage(FacesMessage.SEVERITY_WARN, msg, detail);
    }

    /**
     * Agrega un mensaje de error.
     *
     * @param msg    - título del mensaje, tipo String.
     * @param detail - Detalle del mensaje, tipo String.
     */
    public void addErrorMessage(String msg, String detail) {
        addMessage(FacesMessage.SEVERITY_ERROR, msg, detail);
    }

    /**
     * Agrega un mensaje de error fatal.
     *
     * @param msg    - título del mensaje, tipo String.
     * @param detail - Detalle del mensaje, tipo String.
     */
    public void addFatalMessage(String msg, String detail) {
        addMessage(FacesMessage.SEVERITY_FATAL, msg, detail);
    }

    /**
     * Agrega un parámetro para ser invocado desde la página al retorno del método invocado.
     *
     * @param name  - Nombre del parámetro, tipo String.
     * @param value - Valor del parámetro, tipo Object.
     */
    public void addCallbackParam(String name, Object value) {
//		 RequestContext context = RequestContext.getCurrentInstance();
//		 context.addCallbackParam(name, value);

        PrimeFaces.current().ajax().addCallbackParam(name, value);
    }


    /**
     * Verifica si la cadena esta vacía
     *
     * @param campo - valor del parámetro a evaluar, tipo String
     * @return true si es vacío o nulo y false lo contrario
     */
    public boolean isNull(String campo) {
        if (campo == null || campo.trim().length() < 1) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * Llama a la activación de un componente para ser invocado desde la página al retorno del método invocado.
     *
     * @param name - Nombre del parámetro, tipo String.
     */
    @SuppressWarnings("static-access")
    public void addCallbackComponent(String name) {
//		RequestContext rctx = RequestContext.getCurrentInstance().getCurrentInstance();
//		rctx.execute(name);

        PrimeFaces.current().executeScript(name);

    }

    /**
     * Reset o limpia los campos de un determinado formulario.
     *
     * @param --name - Nombre del parámetro, tipo String.
     */
    public void reset(String idPanel) {
        //RequestContext.getCurrentInstance().reset(idPanel);

        PrimeFaces.current().resetInputs(idPanel);
    }

    /**
     * Obtiene la dirección IP del cliente.
     *
     * @return Dirección IP del cliente, tipo String.
     */
    public String getRemoteAddress() {
        ExternalContext context = getContext();
        HttpServletRequest httpServletRequest = (HttpServletRequest) context.getRequest();
        return httpServletRequest.getRemoteAddr();
    }

    /**
     * Coloca una variable en sesión.
     *
     * @param valorObjeto Identificador del atributo, tipo String.
     * @param tipoObjeto  Objeto a colocar en sesión, tipo Object.
     */
    public static void setAttributeSession(String valorObjeto, Object tipoObjeto) {
        FacesContext.getCurrentInstance().getExternalContext().getSessionMap().put(valorObjeto, tipoObjeto);
    }

    /**
     * Obtiene un atributo de sesión.
     *
     * @param valorObjeto Identificador del atributo, tipo String.
     * @return Object Retorna objeto obtenido de sesión, tipo Object.
     */
    public static Object getAttributeSession(String valorObjeto) {
        return FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(valorObjeto);
    }

    /**
     * Obtiene el controlador que guarda los datos de sesión.
     *
     * @return Controlador de sesión, tipo CVariableSession.
     */
    public CVariableSession getCVariableSesion() {
        return (CVariableSession) getController("cVariableSession");
    }

    /**
     * Obtiene la instancia activa de un controlador.
     *
     * @param controllerName Nombre del controlador, tipo String.
     * @return Controlador, tipo Object.
     */
    public Object getController(String controllerName) {
        FacesContext fc = FacesContext.getCurrentInstance();
        Application app = fc.getApplication();
        ValueExpression ve = app.getExpressionFactory().createValueExpression(
                fc.getELContext(), String.format("#{%s}", controllerName),
                Object.class);
        return ve.getValue(fc.getELContext());
    }

    /**
     * Obtiene clase controladora.
     *
     * @param <T>
     * @param beanName Nombre de la clase , String.
     * @return <T> Nombre de la clase, String.
     */
    @SuppressWarnings({"unchecked"})
    public <T> T findBean(String beanName) {
        FacesContext context = FacesContext.getCurrentInstance();
        return (T) context.getApplication().evaluateExpressionGet(context, new StringBuilder().append("#{").append(beanName).append("}").toString(), Object.class);
    }

    protected void keepDialogOpen() {
//		getRequestContext().addCallbackParam(KEEP_DIALOG_OPENED, true);
        PrimeFaces.current().ajax().addCallbackParam(KEEP_DIALOG_OPENED, true);
    }

    protected void closeDialog() {
        //getRequestContext().addCallbackParam(KEEP_DIALOG_OPENED, false);
        PrimeFaces.current().ajax().addCallbackParam(KEEP_DIALOG_OPENED, false);
    }

    protected PrimeFaces getRequestContext() {
        //return RequestContext.getCurrentInstance();
        return PrimeFaces.current();
    }

    /**
     * Obtención del contexto.
     *
     * @param ruta ubicación del archivo jrxml , tipo String.
     * @return path Retorna la ruta completa del archivo, dentro del contexto de la aplicación, tipo String.
     */
    public static String getPath(String ruta) {
        ServletContext servletContext = (ServletContext) (FacesContext.getCurrentInstance().getExternalContext().getContext());
        String path = servletContext.getRealPath(ruta);
        return path;
    }

    /**
     * Obtiene un componente del contexto
     *
     * @param id Identificador del componente a obtener, tipo String.
     * @return Componente, tipo Object.
     */
    public Object getComponentById(String id) {
        FacesContext fc = FacesContext.getCurrentInstance();
        UIComponent component = fc.getViewRoot().findComponent(id);
        return component;
    }

    public static String getRequestParameter(String key) {
        return (String) FacesContext.getCurrentInstance().getExternalContext().getRequestParameterMap().get(key);
    }

    public void verArchivo() {
        File file = new File(getPath("/util/archivos/" + getRequestParameter("p_manual")));
        try {
            FileInputStream is = null;
            try {
                is = new FileInputStream(file);
                HttpServletResponse response = (HttpServletResponse) FacesContext.getCurrentInstance().getExternalContext().getResponse();
                response.setContentType("application/pdf");
                response.addHeader("Content-disposition", "attachment; filename=\"" + getRequestParameter("p_manual") + "\"");
                OutputStream os = response.getOutputStream();
                byte[] buffer = new byte[5024];
                int read = is.read(buffer);
                while (read >= 0) {
                    if (read > 0) {
                        os.write(buffer, 0, read);
                    }
                    read = is.read(buffer);
                }
                is.close();
                os.close();
                FacesContext.getCurrentInstance().responseComplete();
            } catch (Exception e) {
                try {
                    e.printStackTrace();
                    is.close();
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
            return;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
