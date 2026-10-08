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
public class DetGuiaMesaPartesBean {
    private String nuAnnGui;
    private String nuGui;
    private String nuCor;
    private String nuAnn;
    private String nuEmi;
    private String nuDes;
    private String esDetGui;
    private String coUseMod;

    public String getNuAnnGui() {
        return nuAnnGui;
    }

    public void setNuAnnGui(String nuAnnGui) {
        this.nuAnnGui = nuAnnGui;
    }

    public String getNuGui() {
        return nuGui;
    }

    public void setNuGui(String nuGui) {
        this.nuGui = nuGui;
    }

    public String getNuCor() {
        return nuCor;
    }

    public void setNuCor(String nuCor) {
        this.nuCor = nuCor;
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

    public String getNuDes() {
        return nuDes;
    }

    public void setNuDes(String nuDes) {
        this.nuDes = nuDes;
    }

    public String getEsDetGui() {
        return esDetGui;
    }

    public void setEsDetGui(String esDetGui) {
        this.esDetGui = esDetGui;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }
}
