package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.sql.Date;

public class EstadoMovimiento implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String nuann;
    private String nuemi;
    private int nudes;
    private Date fedml;
    private String tiproceso;
    private String esproceso;
    private String deuser;
    private String deippc;
    private String denamepc;
    private String deuserpc;

    public String getNuann() {
        return nuann;
    }

    public void setNuann(String nuann) {
        this.nuann = nuann;
    }

    public String getNuemi() {
        return nuemi;
    }

    public void setNuemi(String nuemi) {
        this.nuemi = nuemi;
    }

    public int getNudes() {
        return nudes;
    }

    public void setNudes(int nudes) {
        this.nudes = nudes;
    }

    public Date getFedml() {
        return fedml;
    }

    public void setFedml(Date fedml) {
        this.fedml = fedml;
    }

    public String getTiproceso() {
        return tiproceso;
    }

    public void setTiproceso(String tiproceso) {
        this.tiproceso = tiproceso;
    }

    public String getEsproceso() {
        return esproceso;
    }

    public void setEsproceso(String esproceso) {
        this.esproceso = esproceso;
    }

    public String getDeuser() {
        return deuser;
    }

    public void setDeuser(String deuser) {
        this.deuser = deuser;
    }

    public String getDeippc() {
        return deippc;
    }

    public void setDeippc(String deippc) {
        this.deippc = deippc;
    }

    public String getDenamepc() {
        return denamepc;
    }

    public void setDenamepc(String denamepc) {
        this.denamepc = denamepc;
    }

    public String getDeuserpc() {
        return deuserpc;
    }

    public void setDeuserpc(String deuserpc) {
        this.deuserpc = deuserpc;
    }

}
