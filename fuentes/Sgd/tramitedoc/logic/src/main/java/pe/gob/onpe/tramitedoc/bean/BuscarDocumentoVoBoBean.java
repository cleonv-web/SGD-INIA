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
public class BuscarDocumentoVoBoBean {
    private String nuAnn;
    private String feIni;
    private String feFin;
    private String nroExp;
    private String tipDoc;
    private String esDoc;
    private String coDepUsu;
    private String nuCorEmi;
    private String nuDoc;
    private String asunto;
    private String tipBusqueda;
    private String tiAccesoUsu;
    private boolean esIncluyeFiltro;
    private String coEmpUsu;
    private String coPrioridadDoc;
    private String coRefOri;
    private String tiExp;
    private String coEmpElabora;

    public BuscarDocumentoVoBoBean() {
        this.tipBusqueda="0";
    }

    public String getCoEmpElabora() {
        return coEmpElabora;
    }

    public void setCoEmpElabora(String coEmpElabora) {
        this.coEmpElabora = coEmpElabora;
    }

    public String getTiAccesoUsu() {
        return tiAccesoUsu;
    }

    public void setTiAccesoUsu(String tiAccesoUsu) {
        this.tiAccesoUsu = tiAccesoUsu;
    }

    public String getCoRefOri() {
        return coRefOri;
    }

    public void setCoRefOri(String coRefOri) {
        this.coRefOri = coRefOri;
    }

    public String getTiExp() {
        return tiExp;
    }

    public void setTiExp(String tiExp) {
        this.tiExp = tiExp;
    }

    public String getCoPrioridadDoc() {
        return coPrioridadDoc;
    }

    public void setCoPrioridadDoc(String coPrioridadDoc) {
        this.coPrioridadDoc = coPrioridadDoc;
    }

    public String getCoEmpUsu() {
        return coEmpUsu;
    }

    public void setCoEmpUsu(String coEmpUsu) {
        this.coEmpUsu = coEmpUsu;
    }

    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }

    public String getTipBusqueda() {
        return tipBusqueda;
    }

    public void setTipBusqueda(String tipBusqueda) {
        this.tipBusqueda = tipBusqueda;
    }

    public String getNuCorEmi() {
        return nuCorEmi;
    }

    public void setNuCorEmi(String nuCorEmi) {
        this.nuCorEmi = nuCorEmi;
    }

    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }

    public String getAsunto() {
        return asunto;
    }

    public void setAsunto(String asunto) {
        this.asunto = asunto;
    }

    public String getCoDepUsu() {
        return coDepUsu;
    }

    public void setCoDepUsu(String coDepUsu) {
        this.coDepUsu = coDepUsu;
    }

    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getFeIni() {
        return feIni;
    }

    public void setFeIni(String feIni) {
        this.feIni = feIni;
    }

    public String getFeFin() {
        return feFin;
    }

    public void setFeFin(String feFin) {
        this.feFin = feFin;
    }

    public String getNroExp() {
        return nroExp;
    }

    public void setNroExp(String nroExp) {
        this.nroExp = nroExp;
    }

    public String getTipDoc() {
        return tipDoc;
    }

    public void setTipDoc(String tipDoc) {
        this.tipDoc = tipDoc;
    }

    public String getEsDoc() {
        return esDoc;
    }

    public void setEsDoc(String esDoc) {
        this.esDoc = esDoc;
    }
}
