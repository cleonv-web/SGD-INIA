/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author NGilt
 */
public class DocumentoFileBean {
    private String idDocumento;
    private String NombreArchivo;
    private String TamanoArchivo;
    private String TipoArchivo;
    private byte[] ArchivoBytes;

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
