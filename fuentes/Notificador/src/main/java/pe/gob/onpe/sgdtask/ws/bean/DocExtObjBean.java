/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.bean;

/**
 *
 * @author ecueva
 */
public class DocExtObjBean {
    private String coDocumentoExterno;
    private int    nuTamano;
    private byte[] documento;
    private String deDescripcion;
    private String tipoDoc;
    private String urlDoc;
    private String noArchivo;

    public String getNoArchivo() {
        return noArchivo;
    }

    public void setNoArchivo(String noArchivo) {
        this.noArchivo = noArchivo;
    }
    
    public String getDeDescripcion() {
        return deDescripcion;
    }

    public void setDeDescripcion(String deDescripcion) {
        this.deDescripcion = deDescripcion;
    }
    
    public String getUrlDoc() {
        return urlDoc;
    }

    public void setUrlDoc(String urlDoc) {
        this.urlDoc = urlDoc;
    }
    
    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }
    
    public String getCoDocumentoExterno() {
        return coDocumentoExterno;
    }

    public void setCoDocumentoExterno(String coDocumentoExterno) {
        this.coDocumentoExterno = coDocumentoExterno;
    }

    public int getNuTamano() {
        return nuTamano;
    }

    public void setNuTamano(int nuTamano) {
        this.nuTamano = nuTamano;
    }

    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }
}
