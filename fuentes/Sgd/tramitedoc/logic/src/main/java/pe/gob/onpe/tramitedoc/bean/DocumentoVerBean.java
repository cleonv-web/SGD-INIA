/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import java.io.Serializable;

/**
 *
 * @author WCutipa
 */
public class DocumentoVerBean implements Serializable{
    private String nuAnn;
    private String nuEmi;
    private String nuAne;
    private String tiCap;
    private String tiEmi;
    private String coDocEmi;
    private String numeroDoc;
    private String feFirma;
    private boolean inDoc;
    private String inTipoFirma;
    private String nuSecFirma;
    private String noDocumento;
    private String noFirma;
    private String urlDocumento;
    private String deMensaje;
    private String noPrefijo;
    private String nombreDocumentoWord;
    private String cargoEmpleado;

    public String getCargoEmpleado() {
        return cargoEmpleado;
    }

    public void setCargoEmpleado(String cargoEmpleado) {
        this.cargoEmpleado = cargoEmpleado;
    }
    
    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getNuEmi() {
        return nuEmi;
    }

    public void setNuEmi(String nuEmi) {
        this.nuEmi = nuEmi;
    }

    public String getTiEmi() {
        return tiEmi;
    }

    public void setTiEmi(String tiEmi) {
        this.tiEmi = tiEmi;
    }

    public boolean isInDoc() {
        return inDoc;
    }

    public void setInDoc(boolean inDoc) {
        this.inDoc = inDoc;
    }

    public String getNoDocumento() {
        return noDocumento;
    }

    public void setNoDocumento(String noDocumento) {
        this.noDocumento = noDocumento;
    }

    public String getNoFirma() {
        return noFirma;
    }

    public void setNoFirma(String noFirma) {
        this.noFirma = noFirma;
    }

    public String getUrlDocumento() {
        return urlDocumento;
    }

    public void setUrlDocumento(String urlDocumento) {
        this.urlDocumento = urlDocumento;
    }

    public String getNuAne() {
        return nuAne;
    }

    public void setNuAne(String nuAne) {
        this.nuAne = nuAne;
    }

    public String getNuSecFirma() {
        return nuSecFirma;
    }

    public void setNuSecFirma(String nuSecFirma) {
        this.nuSecFirma = nuSecFirma;
    }

    public String getDeMensaje() {
        return deMensaje;
    }

    public void setDeMensaje(String deMensaje) {
        this.deMensaje = deMensaje;
    }

    public String getNumeroDoc() {
        return numeroDoc;
    }

    public void setNumeroDoc(String numeroDoc) {
        this.numeroDoc = numeroDoc;
    }

    public String getFeFirma() {
        return feFirma;
    }

    public void setFeFirma(String feFirma) {
        this.feFirma = feFirma;
    }

    public String getCoDocEmi() {
        return coDocEmi;
    }

    public void setCoDocEmi(String coDocEmi) {
        this.coDocEmi = coDocEmi;
    }

    public String getTiCap() {
        return tiCap;
    }

    public void setTiCap(String tiCap) {
        this.tiCap = tiCap;
    }

    public String getInTipoFirma() {
        return inTipoFirma;
    }

    public void setInTipoFirma(String inTipoFirma) {
        this.inTipoFirma = inTipoFirma;
    }

    public String getNoPrefijo() {
        return noPrefijo;
    }

    public void setNoPrefijo(String noPrefijo) {
        this.noPrefijo = noPrefijo;
    }

    /**
     * @return the nombreDocumentoWord
     */
    public String getNombreDocumentoWord() {
        return nombreDocumentoWord;
    }

    /**
     * @param nombreDocumentoWord the nombreDocumentoWord to set
     */
    public void setNombreDocumentoWord(String nombreDocumentoWord) {
        this.nombreDocumentoWord = nombreDocumentoWord;
    }

    
    
}
