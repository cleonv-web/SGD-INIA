/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import java.util.Date;

/**
 *
 * @author FSilva
 */
public class AsignacionFuncionarioBean {
    private long coAsignacionFuncionario;
    private String coDependencia;
    private String deDependencia;
    private String deDocAsigna;
    private String coEmpleado;
    private String deEmpleado;
    private Date feInicio;
    private Date feFin;
    private String coTipoEncargo;
    private String deTipoEncargo;
    private String coUseCre;
    private Date feUseCre;
    private String coUseMod;
    private Date feUseMod;
    private String coEstado;
    private String inAsignacion;
    private String deAsignacion;
    //Busqueda
    private String feRegistraInicio;
    private String feRegistraFin;
    private String fec_inicio;
    private String fec_fin;

    public String getFeRegistraInicio() {
        return feRegistraInicio;
    }

    public void setFeRegistraInicio(String feRegistraInicio) {
        this.feRegistraInicio = feRegistraInicio;
    }

    public String getFeRegistraFin() {
        return feRegistraFin;
    }

    public void setFeRegistraFin(String feRegistraFin) {
        this.feRegistraFin = feRegistraFin;
    }
    
    public String getDeTipoEncargo() {
        return deTipoEncargo;
    }

    public void setDeTipoEncargo(String deTipoEncargo) {
        this.deTipoEncargo = deTipoEncargo;
    }
    
    public String getDeEmpleado() {
        return deEmpleado;
    }

    public void setDeEmpleado(String deEmpleado) {
        this.deEmpleado = deEmpleado;
    }
    
    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }
    
    public String getDeAsignacion() {
        return deAsignacion;
    }

    public void setDeAsignacion(String deAsignacion) {
        this.deAsignacion = deAsignacion;
    }
    
    public long getCoAsignacionFuncionario() {
        return coAsignacionFuncionario;
    }

    public void setCoAsignacionFuncionario(long coAsignacionFuncionario) {
        this.coAsignacionFuncionario = coAsignacionFuncionario;
    }

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getDeDocAsigna() {
        return deDocAsigna;
    }

    public void setDeDocAsigna(String deDocAsigna) {
        this.deDocAsigna = deDocAsigna;
    }

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
    }

    public Date getFeInicio() {
        return feInicio;
    }

    public void setFeInicio(Date feInicio) {
        this.feInicio = feInicio;
    }

    public Date getFeFin() {
        return feFin;
    }

    public void setFeFin(Date feFin) {
        this.feFin = feFin;
    }

    public String getCoTipoEncargo() {
        return coTipoEncargo;
    }

    public void setCoTipoEncargo(String coTipoEncargo) {
        this.coTipoEncargo = coTipoEncargo;
    }

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }

    public Date getFeUseCre() {
        return feUseCre;
    }

    public void setFeUseCre(Date feUseCre) {
        this.feUseCre = feUseCre;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public Date getFeUseMod() {
        return feUseMod;
    }

    public void setFeUseMod(Date feUseMod) {
        this.feUseMod = feUseMod;
    }

    public String getCoEstado() {
        return coEstado;
    }

    public void setCoEstado(String coEstado) {
        this.coEstado = coEstado;
    }

    public String getInAsignacion() {
        return inAsignacion;
    }

    public void setInAsignacion(String inAsignacion) {
        this.inAsignacion = inAsignacion;
    }   

    public String getFec_inicio() {
        return fec_inicio;
    }

    public void setFec_inicio(String fec_inicio) {
        this.fec_inicio = fec_inicio;
    }

    public String getFec_fin() {
        return fec_fin;
    }

    public void setFec_fin(String fec_fin) {
        this.fec_fin = fec_fin;
    }
       
}
