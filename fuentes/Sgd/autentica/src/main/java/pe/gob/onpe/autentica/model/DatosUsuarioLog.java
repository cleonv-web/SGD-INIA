/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.autentica.model;

import java.util.Date;

/**
 *
 * @author ecueva
 */
public class DatosUsuarioLog {
    private String codUser;
    private String nuCorr;
    private Date dfeIni;
    private String vClave;
    private Date fechActual;
    private String coEmp;
    private String deIppc;
    private String deNamepc;
    private String deUserpc;
    private String success;//'1' intento exitoso, '0' intento fallido

    public String getSuccess() {
        return success;
    }

    public void setSuccess(String success) {
        this.success = success;
    }

    public String getCoEmp() {
        return coEmp;
    }

    public void setCoEmp(String coEmp) {
        this.coEmp = coEmp;
    }

    public String getDeIppc() {
        return deIppc;
    }

    public void setDeIppc(String deIppc) {
        this.deIppc = deIppc;
    }

    public String getDeNamepc() {
        return deNamepc;
    }

    public void setDeNamepc(String deNamepc) {
        this.deNamepc = deNamepc;
    }

    public String getDeUserpc() {
        return deUserpc;
    }

    public void setDeUserpc(String deUserpc) {
        this.deUserpc = deUserpc;
    }

    public Date getDfeIni() {
        return dfeIni;
    }

    public void setDfeIni(Date dfeIni) {
        this.dfeIni = dfeIni;
    }

    public Date getFechActual() {
        return fechActual;
    }

    public void setFechActual(Date fechActual) {
        this.fechActual = fechActual;
    }

    public String getCodUser() {
        return codUser;
    }

    public void setCodUser(String codUser) {
        this.codUser = codUser;
    }

    public String getNuCorr() {
        return nuCorr;
    }

    public void setNuCorr(String nuCorr) {
        this.nuCorr = nuCorr;
    }

    public String getvClave() {
        return vClave;
    }

    public void setvClave(String vClave) {
        this.vClave = vClave;
    }
}
