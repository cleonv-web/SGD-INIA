package pe.gob.onpe.libreria.exception;

/**
 * Created by IntelliJ IDEA.
 * User: wcutipa
 * Date: 25/07/12
 * Time: 12:33 PM
 * To change this template use File | Settings | File Templates.
 */
public class validarDatoException extends Exception {
    public String valorMsg;
    public validarDatoException(String valMsg){
        this.valorMsg = valMsg;
    }

}
