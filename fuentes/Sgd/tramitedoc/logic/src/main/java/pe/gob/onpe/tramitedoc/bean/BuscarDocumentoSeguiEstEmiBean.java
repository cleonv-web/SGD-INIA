/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author NGilt
 */
public class BuscarDocumentoSeguiEstEmiBean extends ReporteBean {
    private String coAnnio;
    private String estadoDoc;
    private String coDependencia;
    private String coDepDestino;    
    private String coEmpleado;
    private String tiAcceso;
    private boolean esIncluyeFiltro;
    
    private String prioridadDoc;
    private String coEmpDestino;
    private String coExpediente; 
    private String tipoBusqueda;//0 Filtrar, 1 Buscar,2 Buscar Referencia
    private String idEtiqueta;
    private String coVencimiento;
    
    private String esFiltroFecha;
    private String feEmiIni;
    private String feEmiFin;  
    
    private String coTipoDocAdm;
    private String coDepRemite;
    
    private String busCodTipoDocRef;
    private String busNumDoc;
    private String busNumDocRef;
    private String busAsunto;
    private String busCodDepEmiRef;
    private String busNumExpediente;
    private String busCoAnnio;
    
    //
    private String coRefOrigen;
    private String coDepOrigen;
    private String coEmpElaboro;
    
    

    public BuscarDocumentoSeguiEstEmiBean() {
    }

    public String getCoDepDestino() {
        return coDepDestino;
    }

    public void setCoDepDestino(String coDepDestino) {
        this.coDepDestino = coDepDestino;
    }

    public String getCoExpediente() {
        return coExpediente;
    }

    public void setCoExpediente(String coExpediente) {
        this.coExpediente = coExpediente;
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

    public String getCoDepRemite() {
        return coDepRemite;
    }

    public void setCoDepRemite(String coDepRemite) {
        this.coDepRemite = coDepRemite;
    }

    public String getCoTipoDocAdm() {
        return coTipoDocAdm;
    }

    public void setCoTipoDocAdm(String coTipoDocAdm) {
        this.coTipoDocAdm = coTipoDocAdm;
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

    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }

    public String getTipoBusqueda() {
        return tipoBusqueda;
    }

    public void setTipoBusqueda(String tipoBusqueda) {
        this.tipoBusqueda = tipoBusqueda;
    }

    public String getCoEmpDestino() {
        return coEmpDestino;
    }

    public void setCoEmpDestino(String coEmpDestino) {
        this.coEmpDestino = coEmpDestino;
    }

    public String getPrioridadDoc() {
        return prioridadDoc;
    }

    public void setPrioridadDoc(String prioridadDoc) {
        this.prioridadDoc = prioridadDoc;
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

    public String getCoAnnio() {
        return coAnnio;
    }

    public void setCoAnnio(String coAnnio) {
        this.coAnnio = coAnnio;
    }

    public String getBusCoAnnio() {
        return busCoAnnio;
    }

    public void setBusCoAnnio(String busCoAnnio) {
        this.busCoAnnio = busCoAnnio;
    }

    public String getEstadoDoc() {
        return estadoDoc;
    }

    public void setEstadoDoc(String estadoDoc) {
        this.estadoDoc = estadoDoc;
    }

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(String idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }

    public String getCoVencimiento() {
        return coVencimiento;
    }

    public void setCoVencimiento(String coVencimiento) {
        this.coVencimiento = coVencimiento;
    }

    public String getCoRefOrigen() {
        return coRefOrigen;
    }

    public void setCoRefOrigen(String coRefOrigen) {
        this.coRefOrigen = coRefOrigen;
    }

    public String getCoDepOrigen() {
        return coDepOrigen;
    }

    public void setCoDepOrigen(String coDepOrigen) {
        this.coDepOrigen = coDepOrigen;
    }

    public String getCoEmpElaboro() {
        return coEmpElaboro;
    }

    public void setCoEmpElaboro(String coEmpElaboro) {
        this.coEmpElaboro = coEmpElaboro;
    }
    
    
}
