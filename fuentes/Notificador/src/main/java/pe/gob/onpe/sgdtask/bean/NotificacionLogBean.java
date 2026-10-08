/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.bean;

/**
 *
 * @author oti2
 */
public class NotificacionLogBean {
    private String noEntidadNotifica;
    private String deSiglaEntidadNotifica;
    private String paraEmail;
    private String contenido;
    private String asunto;
    private String nombreEmpleado;
    private String cuerpo;
    private String detalle;
    private String coEstadoEnvio;
    private String codigo;
    private String error;

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
    
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getCoEstadoEnvio() {
        return coEstadoEnvio;
    }

    public void setCoEstadoEnvio(String coEstadoEnvio) {
        this.coEstadoEnvio = coEstadoEnvio;
    }
    
    public String getDetalle() {
        return detalle;
    }

    public void setDetalle(String detalle) {
        this.detalle = detalle;
    }
    
    public String getParaEmail() {
        return paraEmail;
    }

    public void setParaEmail(String paraEmail) {
        this.paraEmail = paraEmail;
    }

    public String getContenido() {
        return contenido;
    }

    public void setContenido(String contenido) {
        this.contenido = contenido;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getNombreEmpleado() {
        return nombreEmpleado;
    }

    public void setNombreEmpleado(String nombreEmpleado) {
        this.nombreEmpleado = nombreEmpleado;
    }

    public String getCuerpo() {
        return cuerpo;
    }

    public void setCuerpo(String cuerpo) {
        this.cuerpo = cuerpo;
    }
    
    public String getNoEntidadNotifica() {
        return noEntidadNotifica;
    }

    public void setNoEntidadNotifica(String noEntidadNotifica) {
        this.noEntidadNotifica = noEntidadNotifica;
    }

    public String getDeSiglaEntidadNotifica() {
        return deSiglaEntidadNotifica;
    }

    public void setDeSiglaEntidadNotifica(String deSiglaEntidadNotifica) {
        this.deSiglaEntidadNotifica = deSiglaEntidadNotifica;
    }
    
}
