/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.bean;

import java.util.Date;

/**
 *
 * @author FSilva
 */
public class RecepcionExternaBean {
    private long coRecepcion;
    private Date feEnvio;
    private String coEstado;
    private String noEstado;
    private long coEntidadExterna;
    private String noEntidadExterna;  
    private String nuRuc;
    private Date feRecibidoSgd;
    private Date feRegistra;
    private String inRecepcion;
    private int nuAnexos;
    private String inTupa;
    //
    private String feRecepcionIni;
    private String feRecepcionFin;    
    private String coAnnio;
    private String coEstadoDoc;
    private String esFiltroFecha;
    private String tipoBusqueda;
    private boolean esIncluyeFiltro;
    private DocumentoExternoBean documentoExternoBean;

    public String getNuRuc() {
        return nuRuc;
    }

    public void setNuRuc(String nuRuc) {
        this.nuRuc = nuRuc;
    }
    
    public int getNuAnexos() {
        return nuAnexos;
    }

    public void setNuAnexos(int nuAnexos) {
        this.nuAnexos = nuAnexos;
    }

    public String getInTupa() {
        return inTupa;
    }

    public void setInTupa(String inTupa) {
        this.inTupa = inTupa;
    }
    
    public Date getFeRecibidoSgd() {
        return feRecibidoSgd;
    }

    public void setFeRecibidoSgd(Date feRecibidoSgd) {
        this.feRecibidoSgd = feRecibidoSgd;
    }

    public Date getFeRegistra() {
        return feRegistra;
    }

    public void setFeRegistra(Date feRegistra) {
        this.feRegistra = feRegistra;
    }
    
    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }
    
    public String getInRecepcion() {
        return inRecepcion;
    }

    public void setInRecepcion(String inRecepcion) {
        this.inRecepcion = inRecepcion;
    }
    
    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }
    
    public String getEsFiltroFecha() {
        return esFiltroFecha;
    }

    public void setEsFiltroFecha(String esFiltroFecha) {
        this.esFiltroFecha = esFiltroFecha;
    }
    
    public String getCoEstadoDoc() {
        return coEstadoDoc;
    }

    public void setCoEstadoDoc(String coEstadoDoc) {
        this.coEstadoDoc = coEstadoDoc;
    }
    
    public String getCoAnnio() {
        return coAnnio;
    }

    public void setCoAnnio(String coAnnio) {
        this.coAnnio = coAnnio;
    }
    
    public String getNoEntidadExterna() {
        return noEntidadExterna;
    }

    public void setNoEntidadExterna(String noEntidadExterna) {
        this.noEntidadExterna = noEntidadExterna;
    }
    
    public String getFeRecepcionIni() {
        return feRecepcionIni;
    }

    public void setFeRecepcionIni(String feRecepcionIni) {
        this.feRecepcionIni = feRecepcionIni;
    }

    public String getFeRecepcionFin() {
        return feRecepcionFin;
    }

    public void setFeRecepcionFin(String feRecepcionFin) {
        this.feRecepcionFin = feRecepcionFin;
    }

    public DocumentoExternoBean getDocumentoExternoBean() {
        return documentoExternoBean;
    }

    public void setDocumentoExternoBean(DocumentoExternoBean documentoExternoBean) {
        this.documentoExternoBean = documentoExternoBean;
    }
    
    public long getCoRecepcion() {
        return coRecepcion;
    }

    public void setCoRecepcion(long coRecepcion) {
        this.coRecepcion = coRecepcion;
    }

    public Date getFeEnvio() {
        return feEnvio;
    }

    public void setFeEnvio(Date feEnvio) {
        this.feEnvio = feEnvio;
    }
    
    public String getCoEstado() {
        return coEstado;
    }

    public void setCoEstado(String coEstado) {
        this.coEstado = coEstado;
    }

    public String getNoEstado() {
        return noEstado;
    }

    public void setNoEstado(String noEstado) {
        this.noEstado = noEstado;
    }

    public long getCoEntidadExterna() {
        return coEntidadExterna;
    }

    public void setCoEntidadExterna(long coEntidadExterna) {
        this.coEntidadExterna = coEntidadExterna;
    }
    
}
