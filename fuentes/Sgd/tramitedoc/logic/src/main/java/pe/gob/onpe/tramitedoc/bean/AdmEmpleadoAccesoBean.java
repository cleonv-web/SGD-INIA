/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author GLuque
 */
public class AdmEmpleadoAccesoBean {
    private String coEmpleado;
    private String coUsuario;
    private String deUsuario;
    private String esUsuario;
    private String inAD;
    
    public String getEsUsuario() {
        return esUsuario;
    }

    public void setEsUsuario(String esUsuario) {
        this.esUsuario = esUsuario;
    }
    
    public String getCoUsuario() {
        return coUsuario;
    }

    public void setCoUsuario(String coUsuario) {
        this.coUsuario = coUsuario;
    }

    public String getDeUsuario() {
        return deUsuario;
    }

    public void setDeUsuario(String deUsuario) {
        this.deUsuario = deUsuario;
    }

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
    }

    /**
     * @return the inAD
     */
    public String getInAD() {
        return inAD;
    }

    /**
     * @param inAD the inAD to set
     */
    public void setInAD(String inAD) {
        this.inAD = inAD;
    }
}
