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
public class PermisoBean {

    private String coUse;
    private String cdesUser;
    private String coDep;
    private String esAct;
    private String esActWf;

    private ArrayList<PermisoBean> permisoDetalle;

    public ArrayList<PermisoBean> getPermisoDetalle() {
        return permisoDetalle;
    }

    public String getCdesUser() {
        return cdesUser;
    }

    public void setCdesUser(String cdesUser) {
        this.cdesUser = cdesUser;
    }
    
    public void setPermisoDetalle(ArrayList<PermisoBean> permisoDetalle) {
        this.permisoDetalle = permisoDetalle;
    }

    public String getCoUse() {
        return coUse;
    }

    public void setCoUse(String coUse) {
        this.coUse = coUse;
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getEsAct() {
        return esAct;
    }

    public void setEsAct(String esAct) {
        this.esAct = esAct;
    }

    public String getEsActWf() {
        return esActWf;
    }

    public void setEsActWf(String esActWf) {
        this.esActWf = esActWf;
    }
}
