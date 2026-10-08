/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import java.util.ArrayList;

/**
 *
 * @author ngilt
 */
public class GrupoDestinoBean {

    private String coGruDes;
    private String coDep;
    private String deGruDes;
    private String esGruDes;
    private String coUseCre;
    private String coUseMod;
    private String feUseCre;
    private String feUseMod;
    private ArrayList<GrupoDestinoDetalleBean> grupoDestinoDetalle;

    public ArrayList<GrupoDestinoDetalleBean> getGrupoDestinoDetalle() {
        return grupoDestinoDetalle;
    }

    public void setGrupoDestinoDetalle(ArrayList<GrupoDestinoDetalleBean> grupoDestinoDetalle) {
        this.grupoDestinoDetalle = grupoDestinoDetalle;
    }

    public String getCoGruDes() {
        return coGruDes;
    }

    public void setCoGruDes(String coGruDes) {
        this.coGruDes = coGruDes;
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getDeGruDes() {
        return deGruDes;
    }

    public void setDeGruDes(String deGruDes) {
        this.deGruDes = deGruDes;
    }

    public String getEsGruDes() {
        return esGruDes;
    }

    public void setEsGruDes(String esGruDes) {
        this.esGruDes = esGruDes;
    }

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public String getFeUseCre() {
        return feUseCre;
    }

    public void setFeUseCre(String feUseCre) {
        this.feUseCre = feUseCre;
    }

    public String getFeUseMod() {
        return feUseMod;
    }

    public void setFeUseMod(String feUseMod) {
        this.feUseMod = feUseMod;
    }
}
