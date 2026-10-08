/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.bean;

/**
 *
 * @author RATAYAURI
 */
public class VerificaDocBean {
    
    private String nuAnn;
    private String nuEmi;
    private String tipoDoc;
    private String numDoc;
    private String claveAccesoDoc;
    
    private String textoCaptcha;
    private String cookieSessionId;
    private String siglaDoc;

    public String getSiglaDoc() {
        return siglaDoc;
    }

    public void setSiglaDoc(String siglaDoc) {
        this.siglaDoc = siglaDoc;
    }
    
    
    public VerificaDocBean() {
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getNumDoc() {
        return numDoc;
    }

    public void setNumDoc(String numdoc) {
        this.numDoc = numdoc;
    }

    public String getClaveAccesoDoc() {
        return claveAccesoDoc;
    }

    public void setClaveAccesoDoc(String claveAccesoDoc) {
        this.claveAccesoDoc = claveAccesoDoc;
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

    public String getTextoCaptcha() {
        return textoCaptcha;
    }

    public void setTextoCaptcha(String textoCaptcha) {
        this.textoCaptcha = textoCaptcha;
    }

    public String getCookieSessionId() {
        return cookieSessionId;
    }

    public void setCookieSessionId(String cookieSessionId) {
        this.cookieSessionId = cookieSessionId;
    }

}
