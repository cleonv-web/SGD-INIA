/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class BuscarDocumentoEmiConsulBean extends ReporteBean {
    //parametros generales
    private String coDependencia;
    private String coEmpleado;    
    private String tiAcceso; 
    private boolean esIncluyeFiltro;
    //
    private String coAnnio;
    private String coEmi;
    private String estadoDoc;
    private String prioridadDoc;
    private String tipoDoc;
    private String coExpediente;    
    private String coRefOrigen; 
    private String coDepOrigen;
    private String coDepDestino;
    private String coEmpDestino;
    private String coEmpElaboro;
    private String esFiltroFecha;
    private String feEmiIni;
    private String feEmiFin;
    /***************************************/
    private String tipoBusqueda;//0 Filtrar, 1 Buscar,2 Buscar Referencia
    /***************************************/
    //busqueda desde tabla hacia BD
    private String busNumEmision;
    private String busCodTipoDocRef;
    private String busNumDoc;
    private String busNumDocRef;
    private String busAsunto;
    private String busCodDepEmiRef;
    private String busNumExpediente;
    private String busCoAnnio;

    public String getCoEmi() {
        return coEmi;
    }

    public void setCoEmi(String coEmi) {
        this.coEmi = coEmi;
    }

    public String getNoUrl() {
        return noUrl;
    }

    public void setNoUrl(String noUrl) {
        this.noUrl = noUrl;
    }

    /*private String rutaReporteJasper;
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
    }*/
    
    public BuscarDocumentoEmiConsulBean() {
        this.tipoBusqueda = "0";
        this.esFiltroFecha = "0";
        this.esIncluyeFiltro = false;
    }

    public String getCoDepOrigen() {
        return coDepOrigen;
    }

    public void setCoDepOrigen(String coDepOrigen) {
        this.coDepOrigen = coDepOrigen;
    }

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
    }

    public String getTiAcceso() {
        return tiAcceso;
    }

    public void setTiAcceso(String tiAcceso) {
        this.tiAcceso = tiAcceso;
    }

    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }

    public String getCoAnnio() {
        return coAnnio;
    }

    public void setCoAnnio(String coAnnio) {
        this.coAnnio = coAnnio;
    }

    public String getEstadoDoc() {
        return estadoDoc;
    }

    public void setEstadoDoc(String estadoDoc) {
        this.estadoDoc = estadoDoc;
    }

    public String getPrioridadDoc() {
        return prioridadDoc;
    }

    public void setPrioridadDoc(String prioridadDoc) {
        this.prioridadDoc = prioridadDoc;
    }

    public String getTipoDoc() {
        return tipoDoc;
    }

    public void setTipoDoc(String tipoDoc) {
        this.tipoDoc = tipoDoc;
    }

    public String getCoExpediente() {
        return coExpediente;
    }

    public void setCoExpediente(String coExpediente) {
        this.coExpediente = coExpediente;
    }

    public String getCoRefOrigen() {
        return coRefOrigen;
    }

    public void setCoRefOrigen(String coRefOrigen) {
        this.coRefOrigen = coRefOrigen;
    }

    public String getCoDepDestino() {
        return coDepDestino;
    }

    public void setCoDepDestino(String coDepDestino) {
        this.coDepDestino = coDepDestino;
    }

    public String getCoEmpDestino() {
        return coEmpDestino;
    }

    public void setCoEmpDestino(String coEmpDestino) {
        this.coEmpDestino = coEmpDestino;
    }
    
    public String getCoEmpElaboro() {
        return coEmpElaboro;
    }

    public void setCoEmpElaboro(String coEmpElaboro) {
        this.coEmpElaboro = coEmpElaboro;
    }

    public String getEsFiltroFecha() {
        return esFiltroFecha;
    }

    public void setEsFiltroFecha(String esFiltroFecha) {
        this.esFiltroFecha = esFiltroFecha;
    }

    public String getFeEmiIni() {
        return feEmiIni;
    }

    public void setFeEmiIni(String feEmiIni) {
        this.feEmiIni = feEmiIni;
    }

    public String getFeEmiFin() {
        return feEmiFin;
    }

    public void setFeEmiFin(String feEmiFin) {
        this.feEmiFin = feEmiFin;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    public String getBusNumEmision() {
        return busNumEmision;
    }

    public void setBusNumEmision(String busNumEmision) {
        this.busNumEmision = busNumEmision;
    }

    public String getBusCodTipoDocRef() {
        return busCodTipoDocRef;
    }

    public void setBusCodTipoDocRef(String busCodTipoDocRef) {
        this.busCodTipoDocRef = busCodTipoDocRef;
    }

    public String getBusNumDoc() {
        return busNumDoc;
    }

    public void setBusNumDoc(String busNumDoc) {
        this.busNumDoc = busNumDoc;
    }

    public String getBusNumDocRef() {
        return busNumDocRef;
    }

    public void setBusNumDocRef(String busNumDocRef) {
        this.busNumDocRef = busNumDocRef;
    }

    public String getBusAsunto() {
        return busAsunto;
    }

    public void setBusAsunto(String busAsunto) {
        this.busAsunto = busAsunto;
    }

    public String getBusCodDepEmiRef() {
        return busCodDepEmiRef;
    }

    public void setBusCodDepEmiRef(String busCodDepEmiRef) {
        this.busCodDepEmiRef = busCodDepEmiRef;
    }

    public String getBusNumExpediente() {
        return busNumExpediente;
    }

    public void setBusNumExpediente(String busNumExpediente) {
        this.busNumExpediente = busNumExpediente;
    }

    public String getBusCoAnnio() {
        return busCoAnnio;
    }

    public void setBusCoAnnio(String busCoAnnio) {
        this.busCoAnnio = busCoAnnio;
    }
}