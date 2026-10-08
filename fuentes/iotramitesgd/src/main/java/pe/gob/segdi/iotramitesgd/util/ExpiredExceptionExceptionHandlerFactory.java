/**
 * Resumen.
 * Objecto : ExpiredExceptionExceptionHandlerFactory
 * Descripción : Clase de captura de excepsiones.
 * Fecha de creación: 02/03/2018 16:41:40
 *
 * @author Arturo Delgado
 * ----------------------------------------------------------------
 * Modificaciones.
 * Fecha		Nombre				Descripción
 * ----------------------------------------------------------------
 */
package pe.gob.segdi.iotramitesgd.util;

import javax.faces.context.ExceptionHandler;
import javax.faces.context.ExceptionHandlerFactory;


public class ExpiredExceptionExceptionHandlerFactory extends ExceptionHandlerFactory {
    private ExceptionHandlerFactory parent;

    public ExpiredExceptionExceptionHandlerFactory(ExceptionHandlerFactory parent) {
        this.parent = parent;
    }

    @Override
    public ExceptionHandler getExceptionHandler() {
        return new ExpiredExceptionExceptionHandler(parent.getExceptionHandler());
    }
}
