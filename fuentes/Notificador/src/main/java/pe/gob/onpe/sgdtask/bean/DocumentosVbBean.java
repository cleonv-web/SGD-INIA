/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.bean;

import java.util.Date;

/**
 *
 * @author FSilva
 */
public class DocumentosVbBean {
    private String nuEmi;
    private String nuAnn;
    private String estadoDocumento;
    private String coEmpVb;
    private String indicadorVb;
    private Date fechaDoc;
    private String fechaDocString;
    private String siglasDocumento;
    private String tipoDocumento;
    private String deAsunto;
    private String deDependencia;

    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }
    
    public String getDeAsunto() {
        return deAsunto;
    }

    public void setDeAsunto(String deAsunto) {
        this.deAsunto = deAsunto;
    }
    
    public String getSiglasDocumento() {
        return siglasDocumento;
    }

    public void setSiglasDocumento(String siglasDocumento) {
        this.siglasDocumento = siglasDocumento;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }
    
    public String getFechaDocString() {
        return fechaDocString;
    }

    public void setFechaDocString(String fechaDocString) {
        this.fechaDocString = fechaDocString;
    }
    
    public String getNuEmi() {
        return nuEmi;
    }

    public void setNuEmi(String nuEmi) {
        this.nuEmi = nuEmi;
    }

    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getEstadoDocumento() {
        return estadoDocumento;
    }

    public void setEstadoDocumento(String estadoDocumento) {
        this.estadoDocumento = estadoDocumento;
    }

    public String getCoEmpVb() {
        return coEmpVb;
    }

    public void setCoEmpVb(String coEmpVb) {
        this.coEmpVb = coEmpVb;
    }

    public String getIndicadorVb() {
        return indicadorVb;
    }

    public void setIndicadorVb(String indicadorVb) {
        this.indicadorVb = indicadorVb;
    }

    public Date getFechaDoc() {
        return fechaDoc;
    }

    public void setFechaDoc(Date fechaDoc) {
        this.fechaDoc = fechaDoc;
    }
    
    
}
