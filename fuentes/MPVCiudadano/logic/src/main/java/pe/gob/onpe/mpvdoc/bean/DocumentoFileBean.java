/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.bean;

/**
 *
 * @author Usuario
 */
public class DocumentoFileBean {
    private String idDocumento;
    private String NombreArchivo;
    private String TamanoArchivo;
    private String TipoArchivo;
    private byte[] ArchivoBytes;
    private String ArchivoBase64;

    public String getArchivoBase64() {
        return ArchivoBase64;
    }

    public void setArchivoBase64(String ArchivoBase64) {
        this.ArchivoBase64 = ArchivoBase64;
    }
    
    
    public String getIdDocumento() {
        return idDocumento;
    }

    public void setIdDocumento(String idDocumento) {
        this.idDocumento = idDocumento;
    }

    public String getNombreArchivo() {
        return NombreArchivo;
    }

    public void setNombreArchivo(String NombreArchivo) {
        this.NombreArchivo = NombreArchivo;
    }

    public String getTamanoArchivo() {
        return TamanoArchivo;
    }

    public void setTamanoArchivo(String TamanoArchivo) {
        this.TamanoArchivo = TamanoArchivo;
    }

    public String getTipoArchivo() {
        return TipoArchivo;
    }

    public void setTipoArchivo(String TipoArchivo) {
        this.TipoArchivo = TipoArchivo;
    }

    public byte[] getArchivoBytes() {
        return ArchivoBytes;
    }

    public void setArchivoBytes(byte[] ArchivoBytes) {
        this.ArchivoBytes = ArchivoBytes;
    }
}
