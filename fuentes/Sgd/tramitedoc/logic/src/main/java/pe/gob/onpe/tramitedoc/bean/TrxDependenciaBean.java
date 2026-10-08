/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

import java.util.ArrayList;

/**
 *
 * @author ECueva
 */
public class TrxDependenciaBean {
    private String coDep;
    private String coUseMod;
    private DependenciaBean dependencia;
    private ArrayList<EmpleadoBean> lstIntegrante;

    public TrxDependenciaBean() {
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public DependenciaBean getDependencia() {
        return dependencia;
    }

    public void setDependencia(DependenciaBean dependencia) {
        this.dependencia = dependencia;
    }

    public ArrayList<EmpleadoBean> getLstIntegrante() {
        return lstIntegrante;
    }

    public void setLstIntegrante(ArrayList<EmpleadoBean> lstIntegrante) {
        this.lstIntegrante = lstIntegrante;
    }
}
