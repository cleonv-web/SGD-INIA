/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.autentica.model;

import java.io.Serializable;

/**
 *
 * @author crosales
 */
public class UsuarioAcceso implements Serializable {
    private String coModulo;
    private String coOpcion;
    private String coSubopcion;

    public UsuarioAcceso(String coModulo, String coOpcion) {
        this.coModulo = coModulo;
        this.coOpcion = coOpcion;
    }

    public UsuarioAcceso(String coModulo, String coOpcion, String coSubopcion) {
        this.coModulo = coModulo;
        this.coOpcion = coOpcion;
        this.coSubopcion = coSubopcion;
    }

    public UsuarioAcceso() {
    }

    public String getCoModulo() {
        return coModulo;
    }

    public void setCoModulo(String coModulo) {
        this.coModulo = coModulo;
    }

    public String getCoOpcion() {
        return coOpcion;
    }

    public void setCoOpcion(String coOpcion) {
        this.coOpcion = coOpcion;
    }

    public String getCoSubopcion() {
        return coSubopcion;
    }

    public void setCoSubopcion(String coSubopcion) {
        this.coSubopcion = coSubopcion;
    }

}

