/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author oti3
 */
public class BuscarDocumentoCargaMsjBean {
   private String coGrupo;
    private String coDependencia;
    private String coEmpleado;    
    private String tiAcceso;
    private String coResponsable;


    private boolean esIncluyeFiltro;
    //
    private String coAnnio;
    private String coEstadoDoc;
    private String coAmbitoMsj;
    private String coTipoEnvMsj;
    private String coTipoMsj;
    private String coOficina;
    
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
    private String nro_guia_devolucion;
    
    /***************************************/
    private String tipoBusqueda;//0 Filtrar, 1 Buscar

    /***************************************/
    //busqueda desde tabla hacia BD
    private String busNuMsj;
    private String busNuSerMsj;
    private String busAnSerMsj;
    private String busNuDoc;
    private String busDesti;
    
    private String RutaReporteJasper;
    private String FormatoReporte;


    
    
    public BuscarDocumentoCargaMsjBean() {
        this.tipoBusqueda = "0";
        this.esFiltroFecha = "0";
        this.esIncluyeFiltro = false;        
    }

    public String getInMesaPartes() {
        return inMesaPartes;
    }

    public void setInMesaPartes(String inMesaPartes) {
        this.inMesaPartes = inMesaPartes;
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
    
    public String getCoResponsable() {
        return coResponsable;
    }

    public void setCoResponsable(String coResponsable) {
        this.coResponsable = coResponsable;
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
    
    public String getCoAmbitoMsj() {
        return coAmbitoMsj;
    }

    public void setCoAmbitoMsj(String coAmbitoMsj) {
        this.coAmbitoMsj = coAmbitoMsj;
    }

    public String getCoTipoMsj() {
        return coTipoMsj;
    }

    public void setCoTipoMsj(String coTipoMsj) {
        this.coTipoMsj = coTipoMsj;
    }    
   
    public String getCoTipoEnvMsj() {
        return coTipoEnvMsj;
    }

    public void setCoTipoEnvMsj(String coTipoEnvMsj) {
        this.coTipoEnvMsj = coTipoEnvMsj;
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

    public String getBusNuMsj() {
        return busNuMsj;
    }

    public void setBusNuMsj(String busNuMsj) {
        this.busNuMsj = busNuMsj;
    }

    public String getBusNuSerMsj() {
        return busNuSerMsj;
    }

    public void setBusNuSerMsj(String busNuSerMsj) {
        this.busNuSerMsj = busNuSerMsj;
    }

    public String getBusAnSerMsj() {
        return busAnSerMsj;
    }


    public void setBusAnSerMsj(String busAnSerMsj) {
        this.busAnSerMsj = busAnSerMsj;
    }    

    
    public String getRutaReporteJasper() {
        return RutaReporteJasper;
    }

    public void setRutaReporteJasper(String RutaReporteJasper) {
        this.RutaReporteJasper = RutaReporteJasper;
    }

    public String getFormatoReporte() {
        return FormatoReporte;
    }

    public void setFormatoReporte(String FormatoReporte) {
        this.FormatoReporte = FormatoReporte;
    }

    public String getCoOficina() {
        return coOficina;
    }

    public void setCoOficina(String coOficina) {
        this.coOficina = coOficina;
    }

    public String getBusNuDoc() {
        return busNuDoc;
    }

    public void setBusNuDoc(String busNuDoc) {
        this.busNuDoc = busNuDoc;
    }

    public String getBusDesti() {
        return busDesti;
    }

    public void setBusDesti(String busDesti) {
        this.busDesti = busDesti;
    }

    /**
     * @return the nro_guia_devolucion
     */
    public String getNro_guia_devolucion() {
        return nro_guia_devolucion;
    }

    /**
     * @param nro_guia_devolucion the nro_guia_devolucion to set
     */
    public void setNro_guia_devolucion(String nro_guia_devolucion) {
        this.nro_guia_devolucion = nro_guia_devolucion;
    }
    
    
    
}
