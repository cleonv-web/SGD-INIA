/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import pe.gob.onpe.tramitedoc.util.BasePaginate;

/**
 * @author RBerrocal
 * bean asociado a la tabla TDTX_ANI_SIMIL para almacenar un CIUDADANO
 */
public class CitizenBean extends BasePaginate{
    private String nuLem; //NUMERO DE DNI
    private String ubDep; //UBIGEO DE DOMICILIO DEPARTAMENTO
    private String ubPrv; //UBIGEO DE DOMICILIO PROVINCIA
    private String ubDis; //UBIGEO DE DOMICILIO DISTRITO
    private String deApp; //APELLIDO PATERNO
    private String deApm; //APELLIDO MATERNO
    private String deNom; //NOMBRES
    private String noDep; //UBIGEO DE DOMICILIO DEPARTAMENTO
    private String noPrv; //UBIGEO DE DOMICILIO PROVINCIA
    private String noDis; //UBIGEO DE DOMICILIO DISTRITO
    private String deDomicil; //Dirección
    private String deTelefo; //Telefono
    private String deEmail; //Email
    
    public String getNuLem() {
        return nuLem;
    }

    public void setNuLem(String nuLem) {
        this.nuLem = nuLem;
    }

    public String getUbDep() {
        return ubDep;
    }

    public void setUbDep(String ubDep) {
        this.ubDep = ubDep;
    }

    public String getUbPrv() {
        return ubPrv;
    }

    public void setUbPrv(String ubPrv) {
        this.ubPrv = ubPrv;
    }

    public String getUbDis() {
        return ubDis;
    }

    public void setUbDis(String ubDis) {
        this.ubDis = ubDis;
    }

    public String getDeApp() {
        return deApp;
    }

    public void setDeApp(String deApp) {
        this.deApp = deApp;
    }

    public String getDeApm() {
        return deApm;
    }

    public void setDeApm(String deApm) {
        this.deApm = deApm;
    }

    public String getDeNom() {
        return deNom;
    }

    public void setDeNom(String deNom) {
        this.deNom = deNom;
    }

    public String getNoDep() {
        return noDep;
    }

    public void setNoDep(String noDep) {
        this.noDep = noDep;
    }

    public String getNoPrv() {
        return noPrv;
    }

    public void setNoPrv(String noPrv) {
        this.noPrv = noPrv;
    }

    public String getNoDis() {
        return noDis;
    }

    public void setNoDis(String noDis) {
        this.noDis = noDis;
    }
    
    
      public String getDeDomicil() {
        return deDomicil;
    }

    public void setDeDomicil(String deDomicil) {
        this.deDomicil = deDomicil;
    }
    
      public String getDeTelefo() {
        return deTelefo;
    }

    public void setDeTelefo(String deTelefo) {
        this.deTelefo = deTelefo;
    }
    
      public String getDeEmail() {
        return deEmail;
    }

    public void setDeEmail(String deEmail) {
        this.deEmail = deEmail;
    }
    
}
