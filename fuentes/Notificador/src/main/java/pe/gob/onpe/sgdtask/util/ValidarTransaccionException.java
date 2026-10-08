/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.util;

/**
 *
 * @author FSilva
 */
public class ValidarTransaccionException extends Exception{
    public String valorMsg;
    public ValidarTransaccionException(String valMsg){
        this.valorMsg = valMsg;
    }
}
