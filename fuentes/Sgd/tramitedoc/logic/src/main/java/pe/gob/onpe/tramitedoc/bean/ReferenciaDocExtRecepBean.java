/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ECueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenciaDocExtRecepBean {
    private String accionBd;
    private String coUseCre;
    private String nuAnn;
    private String nuEmi;
    private String nuDes;
    private String coRef;
    private String feExp;
    private String coTipDoc;
    private String nuCorrExp;
    private String nuExpediente;
    private String deAsu;
    private String deDocSig;
    private String deTipDocAdm;
    private String feEmiCorta;    
    private String liNuDoc;
    private String deDepDes;    
    private String deProcedencia;
    
    public ReferenciaDocExtRecepBean() {
    }

    public String getDeProcedencia() {
        return deProcedencia;
    }

    public void setDeProcedencia(String deProcedencia) {
        this.deProcedencia = deProcedencia;
    }
    
    public String getLiNuDoc() {
        return liNuDoc;
    }

    public void setLiNuDoc(String liNuDoc) {
        this.liNuDoc = liNuDoc;
    }

    public String getDeDepDes() {
        return deDepDes;
    }

    public void setDeDepDes(String deDepDes) {
        this.deDepDes = deDepDes;
    }

    public String getFeEmiCorta() {
        return feEmiCorta;
    }

    public void setFeEmiCorta(String feEmiCorta) {
        this.feEmiCorta = feEmiCorta;
    }

    public String getDeTipDocAdm() {
        return deTipDocAdm;
    }

    public void setDeTipDocAdm(String deTipDocAdm) {
        this.deTipDocAdm = deTipDocAdm;
    }

    public String getDeAsu() {
        return deAsu;
    }

    public void setDeAsu(String deAsu) {
        this.deAsu = deAsu;
    }

    public String getDeDocSig() {
        return deDocSig;
    }

    public void setDeDocSig(String deDocSig) {
        this.deDocSig = deDocSig;
    }

    public String getAccionBd() {
        return accionBd;
    }

    public void setAccionBd(String accionBd) {
        this.accionBd = accionBd;
    }

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
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

    public String getCoRef() {
        return coRef;
    }

    public void setCoRef(String coRef) {
        this.coRef = coRef;
    }

    public String getFeExp() {
        return feExp;
    }

    public void setFeExp(String feExp) {
        this.feExp = feExp;
    }

    public String getCoTipDoc() {
        return coTipDoc;
    }

    public void setCoTipDoc(String coTipDoc) {
        this.coTipDoc = coTipDoc;
    }

    public String getNuCorrExp() {
        return nuCorrExp;
    }

    public void setNuCorrExp(String nuCorrExp) {
        this.nuCorrExp = nuCorrExp;
    }

    public String getNuExpediente() {
        return nuExpediente;
    }

    public void setNuExpediente(String nuExpediente) {
        this.nuExpediente = nuExpediente;
    }

    public String getNuDes() {
        return nuDes;
    }

    public void setNuDes(String nuDes) {
        this.nuDes = nuDes;
    }
    
    
}
