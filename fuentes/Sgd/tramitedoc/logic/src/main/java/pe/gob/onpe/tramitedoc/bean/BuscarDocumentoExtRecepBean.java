/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class BuscarDocumentoExtRecepBean extends ReporteBean{
    //parametros generales
    private String coGrupo;
    private String coDependencia;
    private String coEmpleado;    
    private String tiAcceso; 
    private boolean esIncluyeFiltro;
    //
    private String coAnnio;
    private String coEstadoDoc;
    private String coTipoDoc;
    private String coTipoEmisor;
    private String coRemitente;
    private String coDepDestino;
    private String esFiltroFecha;
    private String feEmiIni;
    private String feEmiFin;
    private String coLocal;
    private String inCambioEst;
    private String coLocEmi;
    private String coDepEmi;
    private String coDepOriRec;//codigo de dependencia Origen de recepcion de documentos externos.
    private String inMesaPartes;
    private String coProceso;

    /***************************************/
    private String tipoBusqueda;//0 Filtrar, 1 Buscar
    /***************************************/    
    
    //busqueda desde tabla hacia BD
    private String busNumEmision;
    private String busNumDoc;
    private String busAsunto;
    private String busNumExpediente;
    
    private String busResultado = ""; 
    private String coTipoPersona;
    private String busNumDni;
    private String busDescDni;
    private String busNumRuc;
    private String busDescRuc;    
    private String busCoOtros;
    private String busNomOtros;
    private String coTipoExp;
    private String coOriDoc;
    
    public BuscarDocumentoExtRecepBean() {
        this.tipoBusqueda = "0";
        this.esFiltroFecha = "0";
        this.esIncluyeFiltro = false;        
    }

    public String getInMesaPartes() {
        return inMesaPartes;
    }

    public void setInMesaPartes(String inMesaPartes) {
        this.inMesaPartes = inMesaPartes;
    }public String getCoProceso() {
        return coProceso;
    }

    public void setCoProceso(String coProceso) {
        this.coProceso = coProceso;
    }
    
    

    public String getCoDepOriRec() {
        return coDepOriRec;
    }

    public void setCoDepOriRec(String coDepOriRec) {
        this.coDepOriRec = coDepOriRec;
    }

    public String getCoDepEmi() {
        return coDepEmi;
    }

    public void setCoDepEmi(String coDepEmi) {
        this.coDepEmi = coDepEmi;
    }

    public String getCoLocEmi() {
        return coLocEmi;
    }

    public void setCoLocEmi(String coLocEmi) {
        this.coLocEmi = coLocEmi;
    }

    public String getInCambioEst() {
        return inCambioEst;
    }

    public void setInCambioEst(String inCambioEst) {
        this.inCambioEst = inCambioEst;
    }

    public String getCoLocal() {
        return coLocal;
    }

    public void setCoLocal(String coLocal) {
        this.coLocal = coLocal;
    }

    public String getCoGrupo() {
        return coGrupo;
    }

    public void setCoGrupo(String coGrupo) {
        this.coGrupo = coGrupo;
    }

    public String getCoTipoEmisor() {
        return coTipoEmisor;
    }

    public void setCoTipoEmisor(String coTipoEmisor) {
        this.coTipoEmisor = coTipoEmisor;
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

    public String getCoEstadoDoc() {
        return coEstadoDoc;
    }

    public void setCoEstadoDoc(String coEstadoDoc) {
        this.coEstadoDoc = coEstadoDoc;
    }

    public String getCoTipoDoc() {
        return coTipoDoc;
    }

    public void setCoTipoDoc(String coTipoDoc) {
        this.coTipoDoc = coTipoDoc;
    }

    public String getCoRemitente() {
        return coRemitente;
    }

    public void setCoRemitente(String coRemitente) {
        this.coRemitente = coRemitente;
    }

    public String getCoDepDestino() {
        return coDepDestino;
    }

    public void setCoDepDestino(String coDepDestino) {
        this.coDepDestino = coDepDestino;
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

    public String getBusNumDoc() {
        return busNumDoc;
    }

    public void setBusNumDoc(String busNumDoc) {
        this.busNumDoc = busNumDoc;
    }

    public String getBusAsunto() {
        return busAsunto;
    }

    public void setBusAsunto(String busAsunto) {
        this.busAsunto = busAsunto;
    }

    public String getBusNumExpediente() {
        return busNumExpediente;
    }

    public void setBusNumExpediente(String busNumExpediente) {
        this.busNumExpediente = busNumExpediente;
    }

    public String getBusNumEmision() {
        return busNumEmision;
    }

    public void setBusNumEmision(String busNumEmision) {
        this.busNumEmision = busNumEmision;
    }

    public String getCoTipoPersona() {
        return coTipoPersona;
    }

    public void setCoTipoPersona(String coTipoPersona) {
        this.coTipoPersona = coTipoPersona;
    }

    public String getBusNumDni() {
        return busNumDni;
    }

    public void setBusNumDni(String busNumDni) {
        this.busNumDni = busNumDni;
    }

    public String getBusDescDni() {
        return busDescDni;
    }

    public void setBusDescDni(String busDescDni) {
        this.busDescDni = busDescDni;
    }

    public String getBusNumRuc() {
        return busNumRuc;
    }

    public void setBusNumRuc(String busNumRuc) {
        this.busNumRuc = busNumRuc;
    }

    public String getBusDescRuc() {
        return busDescRuc;
    }

    public void setBusDescRuc(String busDescRuc) {
        this.busDescRuc = busDescRuc;
    }

    public String getBusCoOtros() {
        return busCoOtros;
    }

    public void setBusCoOtros(String busCoOtros) {
        this.busCoOtros = busCoOtros;
    }

    public String getBusNomOtros() {
        return busNomOtros;
    }

    public void setBusNomOtros(String busNomOtros) {
        this.busNomOtros = busNomOtros;
    }

    public String getBusResultado() {
        return busResultado;
    }

    public void setBusResultado(String busResultado) {
        this.busResultado = busResultado;
    }

    public String getCoTipoExp() {
        return coTipoExp;
    }

    public void setCoTipoExp(String coTipoExp) {
        this.coTipoExp = coTipoExp;
    }

    public String getCoOriDoc() {
        return coOriDoc;
    }

    public void setCoOriDoc(String coOriDoc) {
        this.coOriDoc = coOriDoc;
    }
    
}
