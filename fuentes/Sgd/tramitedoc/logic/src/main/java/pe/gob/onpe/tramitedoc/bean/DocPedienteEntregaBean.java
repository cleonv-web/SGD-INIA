/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ecueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocPedienteEntregaBean {
    private String nuAnn;
    private String nuEmi;
    private String nuDes;
    private String nuCorEmi;
    private String deOriEmi;
    private String feEmi;
    private String feEmiCorta;
    private String deTipDocAdm;
    private String coTipDocAdm;
    private String nuDoc;
    private String coDepDes;
    private String deDepDes;
    private String coEmpDes;
    private String deEmpDes;
    private String coDepEmi;
    private String coLocEmi;
    private String coLocDes;
    private String feEmiIni;
    private String feEmiFin;    
    private String tipoBusqueda;//0 Filtrar , 1 Buscar
    private boolean esIncluyeFiltro;
    private String filtroFecha;
    private String deAsu;
    private String nuExpediente;
    private String feRecDocCorta;
    private boolean esDetGuia;
    private String deLocDes;
    private String tiAcceso;

    public String getTiAcceso() {
        return tiAcceso;
    }

    public void setTiAcceso(String tiAcceso) {
        this.tiAcceso = tiAcceso;
    }

    public String getDeLocDes() {
        return deLocDes;
    }

    public void setDeLocDes(String deLocDes) {
        this.deLocDes = deLocDes;
    }

    public String getNuExpediente() {
        return nuExpediente;
    }

    public void setNuExpediente(String nuExpediente) {
        this.nuExpediente = nuExpediente;
    }

    public String getFeRecDocCorta() {
        return feRecDocCorta;
    }

    public void setFeRecDocCorta(String feRecDocCorta) {
        this.feRecDocCorta = feRecDocCorta;
    }

    public boolean isEsDetGuia() {
        return esDetGuia;
    }

    public void setEsDetGuia(boolean esDetGuia) {
        this.esDetGuia = esDetGuia;
    }

    public String getNuDes() {
        return nuDes;
    }

    public void setNuDes(String nuDes) {
        this.nuDes = nuDes;
    }

    public String getDeAsu() {
        return deAsu;
    }

    public void setDeAsu(String deAsu) {
        this.deAsu = deAsu;
    }

    public String getFiltroFecha() {
        return filtroFecha;
    }

    public void setFiltroFecha(String filtroFecha) {
        this.filtroFecha = filtroFecha;
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

    public String getNuCorEmi() {
        return nuCorEmi;
    }

    public void setNuCorEmi(String nuCorEmi) {
        this.nuCorEmi = nuCorEmi;
    }

    public String getDeOriEmi() {
        return deOriEmi;
    }

    public void setDeOriEmi(String deOriEmi) {
        this.deOriEmi = deOriEmi;
    }

    public String getFeEmi() {
        return feEmi;
    }

    public void setFeEmi(String feEmi) {
        this.feEmi = feEmi;
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

    public String getCoTipDocAdm() {
        return coTipDocAdm;
    }

    public void setCoTipDocAdm(String coTipDocAdm) {
        this.coTipDocAdm = coTipDocAdm;
    }

    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }

    public String getCoDepDes() {
        return coDepDes;
    }

    public void setCoDepDes(String coDepDes) {
        this.coDepDes = coDepDes;
    }

    public String getDeDepDes() {
        return deDepDes;
    }

    public void setDeDepDes(String deDepDes) {
        this.deDepDes = deDepDes;
    }

    public String getCoEmpDes() {
        return coEmpDes;
    }

    public void setCoEmpDes(String coEmpDes) {
        this.coEmpDes = coEmpDes;
    }

    public String getDeEmpDes() {
        return deEmpDes;
    }

    public void setDeEmpDes(String deEmpDes) {
        this.deEmpDes = deEmpDes;
    }

    public String getCoDepEmi() {
        return coDepEmi;
    }

    public void setCoDepEmi(String coDepEmi) {
        this.coDepEmi = coDepEmi;
    }

    public String getCoLocEmi() {
        return coLocEmi;
    }

    public void setCoLocEmi(String coLocEmi) {
        this.coLocEmi = coLocEmi;
    }

    public String getCoLocDes() {
        return coLocDes;
    }

    public void setCoLocDes(String coLocDes) {
        this.coLocDes = coLocDes;
    }

    public String getFeEmiIni() {
        return feEmiIni;
    }

    public void setFeEmiIni(String feEmiIni) {
        this.feEmiIni = feEmiIni;
    }

    public String getFeEmiFin() {
        return feEmiFin;
    }

    public void setFeEmiFin(String feEmiFin) {
        this.feEmiFin = feEmiFin;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }
}
