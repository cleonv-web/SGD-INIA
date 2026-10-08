/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author WCutipa
 */
public class DocumentoObjBean {
    private String nuAnn;
    private String nuEmi;
    private String nuAne;
    private String tiCap;
    private String numeroSecuencia;
    private String nombreArchivo;
    private String tipoDoc;
    private String coUseMod;
    private int    nuTamano;
    private byte[] documento;
    private String deRespuesta;
    
   private String wNombreArchivo;
    private int    wNuTamano;

    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getNuEmi() {
        return nuEmi;
    }

    public void setNuEmi(String nuEmi) {
        this.nuEmi = nuEmi;
    }

    public String getTiCap() {
        return tiCap;
    }

    public void setTiCap(String tiCap) {
        this.tiCap = tiCap;
    }

    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public byte[] getDocumento() {
        return documento;
    }

    public void setDocumento(byte[] documento) {
        this.documento = documento;
    }

    public int getNuTamano() {
        return nuTamano;
    }

    public void setNuTamano(int nuTamano) {
        this.nuTamano = nuTamano;
    }

    public String getNuAne() {
        return nuAne;
    }

    public void setNuAne(String nuAne) {
        this.nuAne = nuAne;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public String getNumeroSecuencia() {
        return numeroSecuencia;
    }

    public void setNumeroSecuencia(String numeroSecuencia) {
        this.numeroSecuencia = numeroSecuencia;
    }

    public String getDeRespuesta() {
        return deRespuesta;
    }

    public void setDeRespuesta(String deRespuesta) {
        this.deRespuesta = deRespuesta;
    }

       public String getwNombreArchivo() {
        return wNombreArchivo;
    }
    
    public void setwNombreArchivo(String wNombreArchivo) {
        this.wNombreArchivo = wNombreArchivo;
    }
 
    public int getwNuTamano() {
        return wNuTamano;
    }

    public void setwNuTamano(int wNuTamano) {
    this.wNuTamano = wNuTamano;
    }

        
        
}
