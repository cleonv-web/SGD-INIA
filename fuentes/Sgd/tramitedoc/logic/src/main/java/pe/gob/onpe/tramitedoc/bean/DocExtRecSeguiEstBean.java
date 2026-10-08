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
public class DocExtRecSeguiEstBean extends ReporteBean{

    /**
     * @return the deDepenPadre
     */
    public String getDeDepenPadre() {
        return deDepenPadre;
    }

    /**
     * @param deDepenPadre the deDepenPadre to set
     */
    public void setDeDepenPadre(String deDepenPadre) {
        this.deDepenPadre = deDepenPadre;
    }

    /**
     * @return the deDependencia
     */
    public String getDeDependencia() {
        return deDependencia;
    }

    /**
     * @param deDependencia the deDependencia to set
     */
    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }

    /**
     * @return the deDepSigla
     */
    public String getDeDepSigla() {
        return deDepSigla;
    }

    /**
     * @param deDepSigla the deDepSigla to set
     */
    public void setDeDepSigla(String deDepSigla) {
        this.deDepSigla = deDepSigla;
    }

    /**
     * @return the inTotalPendiente
     */
    public String getInTotalPendiente() {
        return inTotalPendiente;
    }

    /**
     * @param inTotalPendiente the inTotalPendiente to set
     */
    public void setInTotalPendiente(String inTotalPendiente) {
        this.inTotalPendiente = inTotalPendiente;
    }

    /**
     * @return the inNoLeido
     */
    public String getInNoLeido() {
        return inNoLeido;
    }

    /**
     * @param inNoLeido the inNoLeido to set
     */
    public void setInNoLeido(String inNoLeido) {
        this.inNoLeido = inNoLeido;
    }

    /**
     * @return the inRecibidos
     */
    public String getInRecibidos() {
        return inRecibidos;
    }

    /**
     * @param inRecibidos the inRecibidos to set
     */
    public void setInRecibidos(String inRecibidos) {
        this.inRecibidos = inRecibidos;
    }

    /**
     * @return the inAtenPendiente
     */
    public String getInAtenPendiente() {
        return inAtenPendiente;
    }

    /**
     * @param inAtenPendiente the inAtenPendiente to set
     */
    public void setInAtenPendiente(String inAtenPendiente) {
        this.inAtenPendiente = inAtenPendiente;
    }

    /**
     * @return the inTotalRecibido
     */
    public String getInTotalRecibido() {
        return inTotalRecibido;
    }

    /**
     * @param inTotalRecibido the inTotalRecibido to set
     */
    public void setInTotalRecibido(String inTotalRecibido) {
        this.inTotalRecibido = inTotalRecibido;
    }
    //filtro
    private String esFiltroFecha;
    private String feEmiIni;
    private String feEmiFin;    
    /***************************************/
    private String tipoBusqueda;//0 Filtrar, 1 Buscar
    /***************************************/
    //busqueda
    private boolean esIncluyeFiltro;
    //campos
    private String nuAnn;
    private String nuEmi;
    private String nuDes;
    private String nuCorEmi;
    private String feEmi;
    private String feEmiCorta;
    private String deOriEmi;
    private String nuExpediente;
    private String coTipDocAdm;
    private String deTipDocAdm;
    private String nuDoc;
    private String coEmpDes;
    private String deEmpDes;
    private String deAsu;
    private String nuDiaAte;
    private String feLimiteCorta;
    private String coEstVen;
    private String deEstVen;
    private String nuDiaExc;
    private String feArcDocCorta;
    private String feAteDocCorta;
    private String feRecDocCorta;
    private String coEsDocEmi;
    private String deEsDocEmi;
    private String deMotivo;
    private String deEmpRec;
    private String existeDoc;
    private String existeAnexo;
    private String tiEmi;
    private String coProceso;
    private String coDepEmi;
    private String deDepEmi;
    private String coDepDes;
    private String coEmpRes;
    private String deEmpRes;
    private String feExpCorta;
    private String deProcesoExp;
    private String deDepDes;
    private String deIndicaciones;
    private String dePrioridad;
    private String nuFolios;
    private String tiAcceso;
    private String coEmpleado;
    private String inCambioEst;
    private String coLocal;
    private String coDependencia;
    private String deDepenPadre;
    private String deDependencia;
    private String deDepSigla;
    private String inTotalPendiente;
    private String inNoLeido;
    private String inRecibidos;
    private String inAtenPendiente;
    private String inTotalRecibido;
    
    private String inMesaPartes;
    private String coProcesoExp;
    
    private String busResultado;   
    private String coTipoPersona;
    private String busNumDni;
    private String busDescDni;
    private String busNumRuc;
    private String busDescRuc;
    private String busCoOtros;
    private String busNomOtros;
    private String coTipoExp;
    private String coOriDoc;
        
    public String getInMesaPartes() {
        return inMesaPartes;
    }

    public void setInMesaPartes(String inMesaPartes) {
        this.inMesaPartes = inMesaPartes;
    }

    public String getNuFolios() {
        return nuFolios;
    }

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getTiAcceso() {
        return tiAcceso;
    }

    public void setTiAcceso(String tiAcceso) {
        this.tiAcceso = tiAcceso;
    }

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
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

    public void setNuFolios(String nuFolios) {
        this.nuFolios = nuFolios;
    }

    public String getDeEmpRes() {
        return deEmpRes;
    }

    public void setDeEmpRes(String deEmpRes) {
        this.deEmpRes = deEmpRes;
    }

    public String getDeDepDes() {
        return deDepDes;
    }

    public void setDeDepDes(String deDepDes) {
        this.deDepDes = deDepDes;
    }

    public String getDeIndicaciones() {
        return deIndicaciones;
    }

    public void setDeIndicaciones(String deIndicaciones) {
        this.deIndicaciones = deIndicaciones;
    }

    public String getDePrioridad() {
        return dePrioridad;
    }

    public void setDePrioridad(String dePrioridad) {
        this.dePrioridad = dePrioridad;
    }

    public String getNuDes() {
        return nuDes;
    }

    public void setNuDes(String nuDes) {
        this.nuDes = nuDes;
    }

    public String getDeProcesoExp() {
        return deProcesoExp;
    }

    public void setDeProcesoExp(String deProcesoExp) {
        this.deProcesoExp = deProcesoExp;
    }

    public String getFeExpCorta() {
        return feExpCorta;
    }

    public void setFeExpCorta(String feExpCorta) {
        this.feExpCorta = feExpCorta;
    }

    public String getCoEmpRes() {
        return coEmpRes;
    }

    public void setCoEmpRes(String coEmpRes) {
        this.coEmpRes = coEmpRes;
    }

    public String getCoDepDes() {
        return coDepDes;
    }

    public void setCoDepDes(String coDepDes) {
        this.coDepDes = coDepDes;
    }

    public String getCoDepEmi() {
        return coDepEmi;
    }

    public void setCoDepEmi(String coDepEmi) {
        this.coDepEmi = coDepEmi;
    }

    public String getDeDepEmi() {
        return deDepEmi;
    }

    public void setDeDepEmi(String deDepEmi) {
        this.deDepEmi = deDepEmi;
    }

    public String getCoProceso() {
        return coProceso;
    }

    public void setCoProceso(String coProceso) {
        this.coProceso = coProceso;
    }

    public String getTiEmi() {
        return tiEmi;
    }

    public void setTiEmi(String tiEmi) {
        this.tiEmi = tiEmi;
    }

    public String getCoEsDocEmi() {
        return coEsDocEmi;
    }

    public void setCoEsDocEmi(String coEsDocEmi) {
        this.coEsDocEmi = coEsDocEmi;
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

    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }

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

    public String getNuCorEmi() {
        return nuCorEmi;
    }

    public void setNuCorEmi(String nuCorEmi) {
        this.nuCorEmi = nuCorEmi;
    }

    public String getFeEmi() {
        return feEmi;
    }

    public void setFeEmi(String feEmi) {
        this.feEmi = feEmi;
    }

    public String getFeEmiCorta() {
        return feEmiCorta;
    }

    public void setFeEmiCorta(String feEmiCorta) {
        this.feEmiCorta = feEmiCorta;
    }

    public String getDeOriEmi() {
        return deOriEmi;
    }

    public void setDeOriEmi(String deOriEmi) {
        this.deOriEmi = deOriEmi;
    }

    public String getNuExpediente() {
        return nuExpediente;
    }

    public void setNuExpediente(String nuExpediente) {
        this.nuExpediente = nuExpediente;
    }

    public String getCoTipDocAdm() {
        return coTipDocAdm;
    }

    public void setCoTipDocAdm(String coTipDocAdm) {
        this.coTipDocAdm = coTipDocAdm;
    }

    public String getDeTipDocAdm() {
        return deTipDocAdm;
    }

    public void setDeTipDocAdm(String deTipDocAdm) {
        this.deTipDocAdm = deTipDocAdm;
    }

    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }

    public String getCoEmpDes() {
        return coEmpDes;
    }

    public void setCoEmpDes(String coEmpDes) {
        this.coEmpDes = coEmpDes;
    }

    public String getDeEmpDes() {
        return deEmpDes;
    }

    public void setDeEmpDes(String deEmpDes) {
        this.deEmpDes = deEmpDes;
    }

    public String getDeAsu() {
        return deAsu;
    }

    public void setDeAsu(String deAsu) {
        this.deAsu = deAsu;
    }

    public String getNuDiaAte() {
        return nuDiaAte;
    }

    public void setNuDiaAte(String nuDiaAte) {
        this.nuDiaAte = nuDiaAte;
    }

    public String getFeLimiteCorta() {
        return feLimiteCorta;
    }

    public void setFeLimiteCorta(String feLimiteCorta) {
        this.feLimiteCorta = feLimiteCorta;
    }

    public String getCoEstVen() {
        return coEstVen;
    }

    public void setCoEstVen(String coEstVen) {
        this.coEstVen = coEstVen;
    }

    public String getDeEstVen() {
        return deEstVen;
    }

    public void setDeEstVen(String deEstVen) {
        this.deEstVen = deEstVen;
    }

    public String getNuDiaExc() {
        return nuDiaExc;
    }

    public void setNuDiaExc(String nuDiaExc) {
        this.nuDiaExc = nuDiaExc;
    }

    public String getFeArcDocCorta() {
        return feArcDocCorta;
    }

    public void setFeArcDocCorta(String feArcDocCorta) {
        this.feArcDocCorta = feArcDocCorta;
    }

    public String getFeAteDocCorta() {
        return feAteDocCorta;
    }

    public void setFeAteDocCorta(String feAteDocCorta) {
        this.feAteDocCorta = feAteDocCorta;
    }

    public String getFeRecDocCorta() {
        return feRecDocCorta;
    }

    public void setFeRecDocCorta(String feRecDocCorta) {
        this.feRecDocCorta = feRecDocCorta;
    }

    public String getDeEsDocEmi() {
        return deEsDocEmi;
    }

    public void setDeEsDocEmi(String deEsDocEmi) {
        this.deEsDocEmi = deEsDocEmi;
    }

    public String getDeMotivo() {
        return deMotivo;
    }

    public void setDeMotivo(String deMotivo) {
        this.deMotivo = deMotivo;
    }

    public String getDeEmpRec() {
        return deEmpRec;
    }

    public void setDeEmpRec(String deEmpRec) {
        this.deEmpRec = deEmpRec;
    }

    public String getExisteDoc() {
        return existeDoc;
    }

    public void setExisteDoc(String existeDoc) {
        this.existeDoc = existeDoc;
    }

    public String getExisteAnexo() {
        return existeAnexo;
    }

    public void setExisteAnexo(String existeAnexo) {
        this.existeAnexo = existeAnexo;
    }

    public String getCoProcesoExp() {
        return coProcesoExp;
    }

    public void setCoProcesoExp(String coProcesoExp) {
        this.coProcesoExp = coProcesoExp;
    }

    public String getBusResultado() {
        return busResultado;
    }

    public void setBusResultado(String busResultado) {
        this.busResultado = busResultado;
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
