/**
 * Resumen.
 * Objecto : ExpiredExceptionExceptionHandler
 * Descripción : Clase manejadora de excepciones.
 * Fecha de creación: 02/03/2018 16:41:40
 *
 * @author Arturo Delgado
 * ----------------------------------------------------------------
 * Modificaciones.
 * Fecha		Nombre				Descripción
 * ----------------------------------------------------------------
 */
package pe.gob.segdi.iotramitesgd.util;

import org.jboss.logging.Logger;

import javax.faces.FacesException;
import javax.faces.application.ViewExpiredException;
import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerWrapper;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.faces.event.ExceptionQueuedEvent;
import javax.faces.event.ExceptionQueuedEventContext;
import java.io.IOException;
import java.util.Iterator;

;


public class ExpiredExceptionExceptionHandler extends ExceptionHandlerWrapper {
    private static Logger depurador = Logger.getLogger(ExpiredExceptionExceptionHandler.class.getName());
    private ExceptionHandler wrapped;

    public ExpiredExceptionExceptionHandler(ExceptionHandler wrapped) {
        this.wrapped = wrapped;
    }

    @Override
    public ExceptionHandler getWrapped() {
        return this.wrapped;
    }

    /**
     * Captura de mensajes de error (excepciones y sesiones expiradas).
     */
    @Override
    public void handle() throws FacesException {
        FacesContext fc = FacesContext.getCurrentInstance();
        for (Iterator<ExceptionQueuedEvent> i = getUnhandledExceptionQueuedEvents().iterator(); i.hasNext(); ) {
            ExceptionQueuedEvent event = i.next();
            ExceptionQueuedEventContext context = (ExceptionQueuedEventContext) event.getSource();

            String redirectPage = null;
            Throwable t = context.getException();

            try {
                if (t instanceof ViewExpiredException) {
                    //depurador.info("CESSION TEMINADA ==>");
                    redirectPage = "/pages/error/sesionExpirada.xhtml";
                } else {
                    redirectPage = "/pages/error/error.xhtml";
                }
            } finally {
                i.remove();
            }

            ExternalContext extContext = fc.getExternalContext();
            String url = extContext.encodeActionURL(fc.getApplication().getViewHandler().getActionURL(fc, redirectPage));
            try {
                depurador.error(t.getMessage());
                extContext.redirect(url);
            } catch (IOException ioe) {
                depurador.error(ioe.getMessage());
                throw new FacesException(ioe);
            } catch (IllegalStateException ioe) {
                depurador.error(ioe.getMessage());
                throw new FacesException(ioe);
            }

            break;
        }

        getWrapped().handle();
    }
}
