/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ecueva
 */
public class RequisitoBean {
    
    private String descripcion;
    private boolean docPresente;
    private String codRequisito;
    private String nuCorrelativo;    
    private String esEstado;      //Estado (1=ACTIVO,0=INACTIVO)
    private String usCreaAudi;    //Usuario que creacion
    private String feCreaAudi;    //Fecha de creacion 
    private String usModiAudi;    //Usuario de modificacion    
    private String feModiAudi;    //Fecha de modificacion
    private String indicador;
    private String codProceso;
    private String codRem;
    private String aniExpediente;
    private String numExpediente;
    private String inObligatorio;

    public String getCodRequisito() {
        return codRequisito;
    }

    public void setCodRequisito(String codRequisito) {
        this.codRequisito = codRequisito;
    }

    public String getNuCorrelativo() {
        return nuCorrelativo;
    }

    public void setNuCorrelativo(String nuCorrelativo) {
        this.nuCorrelativo = nuCorrelativo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public boolean isDocPresente() {
        return docPresente;
    }

    public void setDocPresente(boolean docPresente) {
        this.docPresente = docPresente;
    }

    public String getEsEstado() {
        return esEstado;
    }

    public void setEsEstado(String esEstado) {
        this.esEstado = esEstado;
    }

    public String getUsCreaAudi() {
        return usCreaAudi;
    }

    public void setUsCreaAudi(String usCreaAudi) {
        this.usCreaAudi = usCreaAudi;
    }

    public String getFeCreaAudi() {
        return feCreaAudi;
    }

    public void setFeCreaAudi(String feCreaAudi) {
        this.feCreaAudi = feCreaAudi;
    }

    public String getUsModiAudi() {
        return usModiAudi;
    }

    public void setUsModiAudi(String usModiAudi) {
        this.usModiAudi = usModiAudi;
    }

    public String getFeModiAudi() {
        return feModiAudi;
    }

    public void setFeModiAudi(String feModiAudi) {
        this.feModiAudi = feModiAudi;
    }

    public String getIndicador() {
        return indicador;
    }

    public void setIndicador(String indicador) {
        this.indicador = indicador;
    }

    public String getCodProceso() {
        return codProceso;
    }

    public void setCodProceso(String codProceso) {
        this.codProceso = codProceso;
    }

    public String getCodRem() {
        return codRem;
    }

    public void setCodRem(String codRem) {
        this.codRem = codRem;
    }

    public String getAniExpediente() {
        return aniExpediente;
    }

    public void setAniExpediente(String aniExpediente) {
        this.aniExpediente = aniExpediente;
    }

    public String getNumExpediente() {
        return numExpediente;
    }

    public void setNumExpediente(String numExpediente) {
        this.numExpediente = numExpediente;
    }

    /**
     * @return the inObligatorio
     */
    public String getInObligatorio() {
        return inObligatorio;
    }

    /**
     * @param inObligatorio the inObligatorio to set
     */
    public void setInObligatorio(String inObligatorio) {
        this.inObligatorio = inObligatorio;
    }
    
}
