/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.bean;

import java.util.Date;
import java.util.List;

/**
 *
 * @author FSilva
 */
public class DocumentoExternoBean {   
    private long coDocumentoExterno;
    private String noDestino;
    private String noUnidadOrgDestino;
    private String noCargoDestino;
    private String coTipoDocumento;
    private String noTipoDocumento;
    private String nuDocumento;
    private Date feDocumento;
    private String deAsunto;
    private String coEstado;
    private String feRegistra;
    private String coRecepcion;
    private String coEmision;
    private DocumentoPrincipalBean documentoPrincipalBean;
    private List<AnexoBean> listaAnexos;

    public long getCoDocumentoExterno() {
        return coDocumentoExterno;
    }

    public void setCoDocumentoExterno(long coDocumentoExterno) {
        this.coDocumentoExterno = coDocumentoExterno;
    }

    public String getNoDestino() {
        return noDestino;
    }

    public void setNoDestino(String noDestino) {
        this.noDestino = noDestino;
    }

    public String getNoUnidadOrgDestino() {
        return noUnidadOrgDestino;
    }

    public void setNoUnidadOrgDestino(String noUnidadOrgDestino) {
        this.noUnidadOrgDestino = noUnidadOrgDestino;
    }

    public String getNoCargoDestino() {
        return noCargoDestino;
    }

    public void setNoCargoDestino(String noCargoDestino) {
        this.noCargoDestino = noCargoDestino;
    }

    public String getCoTipoDocumento() {
        return coTipoDocumento;
    }

    public void setCoTipoDocumento(String coTipoDocumento) {
        this.coTipoDocumento = coTipoDocumento;
    }

    public String getNoTipoDocumento() {
        return noTipoDocumento;
    }

    public void setNoTipoDocumento(String noTipoDocumento) {
        this.noTipoDocumento = noTipoDocumento;
    }

    public String getNuDocumento() {
        return nuDocumento;
    }

    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }

    public String getDeAsunto() {
        return deAsunto;
    }

    public void setDeAsunto(String deAsunto) {
        this.deAsunto = deAsunto;
    }

    public String getCoEstado() {
        return coEstado;
    }

    public void setCoEstado(String coEstado) {
        this.coEstado = coEstado;
    }

    public String getFeRegistra() {
        return feRegistra;
    }

    public void setFeRegistra(String feRegistra) {
        this.feRegistra = feRegistra;
    }

    public String getCoRecepcion() {
        return coRecepcion;
    }

    public void setCoRecepcion(String coRecepcion) {
        this.coRecepcion = coRecepcion;
    }

    public String getCoEmision() {
        return coEmision;
    }

    public void setCoEmision(String coEmision) {
        this.coEmision = coEmision;
    }

    public DocumentoPrincipalBean getDocumentoPrincipalBean() {
        return documentoPrincipalBean;
    }

    public void setDocumentoPrincipalBean(DocumentoPrincipalBean documentoPrincipalBean) {
        this.documentoPrincipalBean = documentoPrincipalBean;
    }

    public List<AnexoBean> getListaAnexos() {
        return listaAnexos;
    }

    public void setListaAnexos(List<AnexoBean> listaAnexos) {
        this.listaAnexos = listaAnexos;
    }

    public Date getFeDocumento() {
        return feDocumento;
    }

    public void setFeDocumento(Date feDocumento) {
        this.feDocumento = feDocumento;
    }
    
}
