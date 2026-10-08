/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.autentica.model;

import java.util.Date;

/**
 *
 * @author wcutipa
 */
public class DatosUsuario {
  private String nuDni;
  private String coUsuario;
  private String dePrenombres;
  private String deApellidoPaterno;
  private String deApellidoMaterno;
  private String cempCodemp;
  private String dePassword;
  private String coDep;
  private String deDep;
  private String esActivo;
  private String esAdmin;
  private Date feModClave;
  private Date feActual;  
  private Date dFecMod; //fecha de actualizacion del registro
  private Date fullFechaActual;
  private int nroIntento;
private String inAD;

    public int getNroIntento() {
        return nroIntento;
    }

    public void setNroIntento(int nroIntento) {
        this.nroIntento = nroIntento;
    }

    public Date getdFecMod() {
        return dFecMod;
    }

    public void setdFecMod(Date dFecMod) {
        this.dFecMod = dFecMod;
    }

    public Date getFullFechaActual() {
        return fullFechaActual;
    }

    public void setFullFechaActual(Date fullFechaActual) {
        this.fullFechaActual = fullFechaActual;
    }

    public Date getFeModClave() {
        return feModClave;
    }

    public void setFeModClave(Date feModClave) {
        this.feModClave = feModClave;
    }

    public Date getFeActual() {
        return feActual;
    }

    public void setFeActual(Date feActual) {
        this.feActual = feActual;
    }

    public String getNuDni() {
        return nuDni;
    }

    public void setNuDni(String nuDni) {
        this.nuDni = nuDni;
    }

    public String getCoUsuario() {
        return coUsuario;
    }

    public void setCoUsuario(String coUsuario) {
        this.coUsuario = coUsuario;
    }

    public String getDePrenombres() {
        return dePrenombres;
    }

    public void setDePrenombres(String dePrenombres) {
        this.dePrenombres = dePrenombres;
    }

    public String getDeApellidoPaterno() {
        return deApellidoPaterno;
    }

    public void setDeApellidoPaterno(String deApellidoPaterno) {
        this.deApellidoPaterno = deApellidoPaterno;
    }

    public String getDeApellidoMaterno() {
        return deApellidoMaterno;
    }

    public void setDeApellidoMaterno(String deApellidoMaterno) {
        this.deApellidoMaterno = deApellidoMaterno;
    }

    public String getCempCodemp() {
        return cempCodemp;
    }

    public void setCempCodemp(String cempCodemp) {
        this.cempCodemp = cempCodemp;
    }

    public String getDePassword() {
        return dePassword;
    }

    public void setDePassword(String dePassword) {
        this.dePassword = dePassword;
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(String esActivo) {
        this.esActivo = esActivo;
    }

    public String getEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(String esAdmin) {
        this.esAdmin = esAdmin;
    }

    public String getDeDep() {
        return deDep;
    }

    public void setDeDep(String deDep) {
        this.deDep = deDep;
    }

    /**
     * @return the inAD
     */
    public String getInAD() {
        return inAD;
    }

    /**
     * @param inAD the inAD to set
     */
    public void setInAD(String inAD) {
        this.inAD = inAD;
    }

  
    
}
