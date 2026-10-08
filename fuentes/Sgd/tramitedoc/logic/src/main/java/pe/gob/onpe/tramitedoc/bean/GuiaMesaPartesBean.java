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
public class GuiaMesaPartesBean {
    
    private String nuAnn;
    private String nuGuia;
    private String coLocOri;
    private String deLocOri;
    private String coDepOri;
    private String deDepOri;
    private String coLocDes;
    private String deLocDes;
    private String coDepDes;
    private String deDepDes;
    private String feGuiMp;
    private String deObs;
    private String nuCorGui;
    private String coUseMod;
    private String estadoGuia;
    private String deEstadoGuia;

    public String getEstadoGuia() {
        return estadoGuia;
    }

    public void setEstadoGuia(String estadoGuia) {
        this.estadoGuia = estadoGuia;
    }

    public String getDeEstadoGuia() {
        return deEstadoGuia;
    }

    public void setDeEstadoGuia(String deEstadoGuia) {
        this.deEstadoGuia = deEstadoGuia;
    }

    public String getDeLocOri() {
        return deLocOri;
    }

    public void setDeLocOri(String deLocOri) {
        this.deLocOri = deLocOri;
    }

    public String getDeDepOri() {
        return deDepOri;
    }

    public void setDeDepOri(String deDepOri) {
        this.deDepOri = deDepOri;
    }

    public String getDeLocDes() {
        return deLocDes;
    }

    public void setDeLocDes(String deLocDes) {
        this.deLocDes = deLocDes;
    }

    public String getDeDepDes() {
        return deDepDes;
    }

    public void setDeDepDes(String deDepDes) {
        this.deDepDes = deDepDes;
    }

    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getNuGuia() {
        return nuGuia;
    }

    public void setNuGuia(String nuGuia) {
        this.nuGuia = nuGuia;
    }

    public String getCoLocOri() {
        return coLocOri;
    }

    public void setCoLocOri(String coLocOri) {
        this.coLocOri = coLocOri;
    }

    public String getCoDepOri() {
        return coDepOri;
    }

    public void setCoDepOri(String coDepOri) {
        this.coDepOri = coDepOri;
    }

    public String getCoLocDes() {
        return coLocDes;
    }

    public void setCoLocDes(String coLocDes) {
        this.coLocDes = coLocDes;
    }

    public String getCoDepDes() {
        return coDepDes;
    }

    public void setCoDepDes(String coDepDes) {
        this.coDepDes = coDepDes;
    }

    public String getFeGuiMp() {
        return feGuiMp;
    }

    public void setFeGuiMp(String feGuiMp) {
        this.feGuiMp = feGuiMp;
    }

    public String getDeObs() {
        return deObs;
    }

    public void setDeObs(String deObs) {
        this.deObs = deObs;
    }

    public String getNuCorGui() {
        return nuCorGui;
    }

    public void setNuCorGui(String nuCorGui) {
        this.nuCorGui = nuCorGui;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }
}
