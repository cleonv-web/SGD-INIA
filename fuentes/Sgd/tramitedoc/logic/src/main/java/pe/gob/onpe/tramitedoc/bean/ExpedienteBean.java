/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ECueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ExpedienteBean {
    private String coDep;
    private String coExp;
    private String deExp;
    private String esEli;
    private String coUseCre;
    private String feUseCre;
    private String coUseMod;
    private String feUseMod;
    
    private String nuAnnExp;
    private String nuSecExp;
    private String feExp;
    private String feExpCorta;
    private String feVence;
    private String coProceso;
    private String deProceso;
    private String deDetalle;
    private String coDepExp;
    private String coGru;
    private String nuCorrExp;
    private String nuExpediente;
    private String nuFolios;
    private String nuPlazo;
    private String usCreaAudi;
    private String feCreaAudi;
    private String usModiAudi;
    private String feModiAudi;
    private String esEstado;    

    public ExpedienteBean() {
    }

    public String getFeExpCorta() {
        return feExpCorta;
    }

    public void setFeExpCorta(String feExpCorta) {
        this.feExpCorta = feExpCorta;
    }

    public String getNuAnnExp() {
        return nuAnnExp;
    }

    public void setNuAnnExp(String nuAnnExp) {
        this.nuAnnExp = nuAnnExp;
    }

    public String getNuSecExp() {
        return nuSecExp;
    }

    public void setNuSecExp(String nuSecExp) {
        this.nuSecExp = nuSecExp;
    }

    public String getFeExp() {
        return feExp;
    }

    public void setFeExp(String feExp) {
        this.feExp = feExp;
    }

    public String getFeVence() {
        return feVence;
    }

    public void setFeVence(String feVence) {
        this.feVence = feVence;
    }

    public String getCoProceso() {
        return coProceso;
    }

    public void setCoProceso(String coProceso) {
        this.coProceso = coProceso;
    }

    public String getDeProceso() {
        return deProceso;
    }

    public void setDeProceso(String deProceso) {
        this.deProceso = deProceso;
    }

    public String getDeDetalle() {
        return deDetalle;
    }

    public void setDeDetalle(String deDetalle) {
        this.deDetalle = deDetalle;
    }

    public String getCoDepExp() {
        return coDepExp;
    }

    public void setCoDepExp(String coDepExp) {
        this.coDepExp = coDepExp;
    }

    public String getCoGru() {
        return coGru;
    }

    public void setCoGru(String coGru) {
        this.coGru = coGru;
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

    public String getNuFolios() {
        return nuFolios;
    }

    public void setNuFolios(String nuFolios) {
        this.nuFolios = nuFolios;
    }

    public String getNuPlazo() {
        return nuPlazo;
    }

    public void setNuPlazo(String nuPlazo) {
        this.nuPlazo = nuPlazo;
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

    public String getEsEstado() {
        return esEstado;
    }

    public void setEsEstado(String esEstado) {
        this.esEstado = esEstado;
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getCoExp() {
        return coExp;
    }

    public void setCoExp(String coExp) {
        this.coExp = coExp;
    }

    public String getDeExp() {
        return deExp;
    }

    public void setDeExp(String deExp) {
        this.deExp = deExp;
    }

    public String getEsEli() {
        return esEli;
    }

    public void setEsEli(String esEli) {
        this.esEli = esEli;
    }

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }

    public String getFeUseCre() {
        return feUseCre;
    }

    public void setFeUseCre(String feUseCre) {
        this.feUseCre = feUseCre;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public String getFeUseMod() {
        return feUseMod;
    }

    public void setFeUseMod(String feUseMod) {
        this.feUseMod = feUseMod;
    }
}
