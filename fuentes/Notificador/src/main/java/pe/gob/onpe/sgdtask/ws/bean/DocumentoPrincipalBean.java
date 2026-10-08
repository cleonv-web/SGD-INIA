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
public class DocumentoPrincipalBean {
    private long coDocumentoPrincipal;
    private byte[] blDocumento;
    private long coDocumentoExterno;
    private String coEstado;
    private Date feRegistra;

    public long getCoDocumentoPrincipal() {
        return coDocumentoPrincipal;
    }

    public void setCoDocumentoPrincipal(long coDocumentoPrincipal) {
        this.coDocumentoPrincipal = coDocumentoPrincipal;
    }

    public byte[] getBlDocumento() {
        return blDocumento;
    }

    public void setBlDocumento(byte[] blDocumento) {
        this.blDocumento = blDocumento;
    }

    public long getCoDocumentoExterno() {
        return coDocumentoExterno;
    }

    public void setCoDocumentoExterno(long coDocumentoExterno) {
        this.coDocumentoExterno = coDocumentoExterno;
    }

    public String getCoEstado() {
        return coEstado;
    }

    public void setCoEstado(String coEstado) {
        this.coEstado = coEstado;
    }

    public Date getFeRegistra() {
        return feRegistra;
    }

    public void setFeRegistra(Date feRegistra) {
        this.feRegistra = feRegistra;
    }
    
}
