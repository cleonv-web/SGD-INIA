/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author MHuamani
 */
public class ReporteBean {
    private String coRespuesta;
    private String deRespuesta;
    public String noUrl;
    private String noDoc;
    private String rutaReporteJasper;
    private String formatoReporte;
    
    public String getRutaReporteJasper(){
        return rutaReporteJasper;
    }
    public void setRutaReporteJasper(String rutaReporteJasper){
        this.rutaReporteJasper = rutaReporteJasper;
    }
    
    public String getFormatoReporte(){
        return formatoReporte;
    }
    public void setFormatoReporte(String formatoReporte){
        this.formatoReporte = formatoReporte;
    }
    public String getcoRespuesta() {
        return coRespuesta;
    }

    public void setcoRespuesta(String coRespuesta) {
        this.coRespuesta = coRespuesta;
    }
    
    public String getdeRespuesta() {
        return deRespuesta;
    }
    public void setdeRespuesta(String deRespuesta) {
        this.deRespuesta = deRespuesta;
    }
    
    public String getnoUrl() {
        return noUrl;
    }
    public void setnoUrl(String noUrl) {
        this.noUrl = noUrl;
    }
    
    public String getnoDoc() {
        return noDoc;
    }

    public void setnoDoc(String noDoc) {
        this.noDoc = noDoc;
    }
}
