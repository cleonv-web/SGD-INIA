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
public class BuscarDocumentoSeguiEstRecBean extends ReporteBean{
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
    private String nuCorDes;
    private String feEmiCorta;
    private String deOriEmi;
    private String deTipDocAdm;
    private String nuDoc;
    private String deEmpDes;
    private String deAsu;
    private String nuExpediente;
    private String deEsDocDes;
    private String feRecCorta;
    private String deEmpRes;
    private String deMotivo;
    private String deEmpRec;
    private String nuDiaAte;
    private String fLimiteCorta;
    private String coEstVen;
    private String nuDiaExc;
    private String feArcDocCorta;
    private String feAteDocCorta;
    private String dePrioridad;

    public String getDeAsu() {
        return deAsu;
    }
    public void setDeAsu(String deAsu) {
        this.deAsu = deAsu;
    }
    
    public String getDeEmpDes() {
        return deEmpDes;
    }
    public void setDeEmpDes(String deEmpDes) {
        this.deEmpDes = deEmpDes;
    }
    
    public String getNuDoc() {
        return nuDoc;
    }
    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }
    
    public String getDeTipDocAdm() {
        return deTipDocAdm;
    }
    public void setDeTipDocAdm(String deTipDocAdm) {
        this.deTipDocAdm = deTipDocAdm;
    }
    
    public String getDeOriEmi() {
        return deOriEmi;
    }
    public void setDeOriEmi(String deOriEmi) {
        this.deOriEmi = deOriEmi;
    }
    
    public String getNuCorDes() {
        return nuCorDes;
    }
    public void setNuCorDes(String nuCorDes) {
        this.nuCorDes = nuCorDes;
    }
    
    public String getFeEmiCorta() {
        return feEmiCorta;
    }
    public void setFeEmiCorta(String feEmiCorta) {
        this.feEmiCorta = feEmiCorta;
    }
    
    public BuscarDocumentoSeguiEstRecBean() {
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

    /**
     * @return the nuExpediente
     */
    public String getNuExpediente() {
        return nuExpediente;
    }

    /**
     * @param nuExpediente the nuExpediente to set
     */
    public void setNuExpediente(String nuExpediente) {
        this.nuExpediente = nuExpediente;
    }

    /**
     * @return the deEsDocDes
     */
    public String getDeEsDocDes() {
        return deEsDocDes;
    }

    /**
     * @param deEsDocDes the deEsDocDes to set
     */
    public void setDeEsDocDes(String deEsDocDes) {
        this.deEsDocDes = deEsDocDes;
    }

    /**
     * @return the feRecCorta
     */
    public String getFeRecCorta() {
        return feRecCorta;
    }

    /**
     * @param feRecCorta the feRecCorta to set
     */
    public void setFeRecCorta(String feRecCorta) {
        this.feRecCorta = feRecCorta;
    }

    /**
     * @return the deEmpRes
     */
    public String getDeEmpRes() {
        return deEmpRes;
    }

    /**
     * @param deEmpRes the deEmpRes to set
     */
    public void setDeEmpRes(String deEmpRes) {
        this.deEmpRes = deEmpRes;
    }

    /**
     * @return the deMotivo
     */
    public String getDeMotivo() {
        return deMotivo;
    }

    /**
     * @param deMotivo the deMotivo to set
     */
    public void setDeMotivo(String deMotivo) {
        this.deMotivo = deMotivo;
    }

    /**
     * @return the deEmpRec
     */
    public String getDeEmpRec() {
        return deEmpRec;
    }

    /**
     * @param deEmpRec the deEmpRec to set
     */
    public void setDeEmpRec(String deEmpRec) {
        this.deEmpRec = deEmpRec;
    }

    /**
     * @return the nuDiaAte
     */
    public String getNuDiaAte() {
        return nuDiaAte;
    }

    /**
     * @param nuDiaAte the nuDiaAte to set
     */
    public void setNuDiaAte(String nuDiaAte) {
        this.nuDiaAte = nuDiaAte;
    }

    /**
     * @return the fLimiteCorta
     */
    public String getfLimiteCorta() {
        return fLimiteCorta;
    }

    /**
     * @param fLimiteCorta the fLimiteCorta to set
     */
    public void setfLimiteCorta(String fLimiteCorta) {
        this.fLimiteCorta = fLimiteCorta;
    }

    /**
     * @return the coEstVen
     */
    public String getCoEstVen() {
        return coEstVen;
    }

    /**
     * @param coEstVen the coEstVen to set
     */
    public void setCoEstVen(String coEstVen) {
        this.coEstVen = coEstVen;
    }

    /**
     * @return the nuDiaExc
     */
    public String getNuDiaExc() {
        return nuDiaExc;
    }

    /**
     * @param nuDiaExc the nuDiaExc to set
     */
    public void setNuDiaExc(String nuDiaExc) {
        this.nuDiaExc = nuDiaExc;
    }

    /**
     * @return the feArcDocCorta
     */
    public String getFeArcDocCorta() {
        return feArcDocCorta;
    }

    /**
     * @param feArcDocCorta the feArcDocCorta to set
     */
    public void setFeArcDocCorta(String feArcDocCorta) {
        this.feArcDocCorta = feArcDocCorta;
    }

    /**
     * @return the feAteDocCorta
     */
    public String getFeAteDocCorta() {
        return feAteDocCorta;
    }

    /**
     * @param feAteDocCorta the feAteDocCorta to set
     */
    public void setFeAteDocCorta(String feAteDocCorta) {
        this.feAteDocCorta = feAteDocCorta;
    }

    /**
     * @return the dePrioridad
     */
    public String getDePrioridad() {
        return dePrioridad;
    }

    /**
     * @param dePrioridad the dePrioridad to set
     */
    public void setDePrioridad(String dePrioridad) {
        this.dePrioridad = dePrioridad;
    }
    
    
}
