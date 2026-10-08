/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import pe.gob.onpe.tramitedoc.util.FiltroPaginate;

/**
 *
 * @author ecueva
 */
public class BuscarDocumentoEmiBean extends FiltroPaginate {
    //parametros generales
    private String sCoDependencia;
    private String sCoEmpleado;    
    private String sTiAcceso;
    //
    private String sCoAnnio;
    private String sEstadoDoc;
    private String sPrioridadDoc;
    private String sTipoDoc;
    private String sExpediente;    
    private String sRefOrigen;    
    private String sDestinatario;
    private String sElaboradoPor;
    private String sTrabDestino;//YUAL
    private String coEmpRec;
    private String esFiltroFecha;
    private String sFeEmiIni;
    private String sFeEmiFin;
    /***************************************/
    private String sTipoBusqueda;//0 Filtrar, 1 Buscar,2 Buscar Referencia
    /***************************************/
    //busqueda desde tabla hacia BD
    private String sNumCorEmision;
    private String sDeEmiReferencia;
    //private String sFeEmiIni;
    //private String sFeEmiFin;
    private String sDeTipoDocAdm;
    private String sNumDoc;
    private String sNumDocRef;
    private String sDeAsuM;
    private String sBuscDestinatario;
    //private String sBuscElaboraradoPor;    
    private String sBuscNroExpediente;
    private boolean esIncluyeFiltro;
    private String sCoAnnioBus;
    private String coTema;
    private String coEstMensajeria;
    private String coEst;
    private String deEst;
    
    
    private String txtRemitente;
    private String txtDestinatario;
    private String txtDepEmiteBus;
    private String txtRefOrigen;
    private String deEmpRec;
    private String txtElaboradoPor;
    
    private boolean esIncluyeOficina;
    private boolean esIncluyeProfesional;
    
    private String sFirmadoPor;
    private String sParaFirmar;

    public String getsFirmadoPor() {
        return sFirmadoPor;
    }

    public void setsFirmadoPor(String sFirmadoPor) {
        this.sFirmadoPor = sFirmadoPor;
    }

    public String getsParaFirmar() {
        return sParaFirmar;
    }

    public void setsParaFirmar(String sParaFirmar) {
        this.sParaFirmar = sParaFirmar;
    }
    
    
    public boolean isEsIncluyeOficina() {
        return esIncluyeOficina;
    }

    public void setEsIncluyeOficina(boolean esIncluyeOficina) {
        this.esIncluyeOficina = esIncluyeOficina;
    }

    public boolean isEsIncluyeProfesional() {
        return esIncluyeProfesional;
    }

    //private String sBuscEstado;
    public void setEsIncluyeProfesional(boolean esIncluyeProfesional) {
        this.esIncluyeProfesional = esIncluyeProfesional;
    }

    public String getCoTema() {
        return coTema;
    }

    public void setCoTema(String coTema) {
        this.coTema = coTema;
    }

    public BuscarDocumentoEmiBean() {
        this.sTipoBusqueda = "0";
        this.esFiltroFecha = "0";
        this.esIncluyeFiltro = false;
    }

    public String getsCoAnnioBus() {
        return sCoAnnioBus;
    }

    public void setsCoAnnioBus(String sCoAnnioBus) {
        this.sCoAnnioBus = sCoAnnioBus;
    }
    
    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }

    public String getsFeEmiIni() {
        return sFeEmiIni;
    }

    public void setsFeEmiIni(String sFeEmiIni) {
        this.sFeEmiIni = sFeEmiIni;
    }

    public String getsFeEmiFin() {
        return sFeEmiFin;
    }

    public void setsFeEmiFin(String sFeEmiFin) {
        this.sFeEmiFin = sFeEmiFin;
    }

    public String getEsFiltroFecha() {
        return esFiltroFecha;
    }

    public void setEsFiltroFecha(String esFiltroFecha) {
        this.esFiltroFecha = esFiltroFecha;
    }

    public String getsNumDocRef() {
        return sNumDocRef;
    }

    public void setsNumDocRef(String sNumDocRef) {
        this.sNumDocRef = sNumDocRef;
    }
    
//    public String getsFeEmiIni() {
//        return sFeEmiIni;
//    }

    public void setsCoDependencia(String sCoDependencia) {
        this.sCoDependencia = sCoDependencia;
    }

    public void setsCoEmpleado(String sCoEmpleado) {
        this.sCoEmpleado = sCoEmpleado;
    }

    public void setsTiAcceso(String sTiAcceso) {
        this.sTiAcceso = sTiAcceso;
    }

    public String getsCoDependencia() {
        return sCoDependencia;
    }

    public String getsCoEmpleado() {
        return sCoEmpleado;
    }

    public String getsTiAcceso() {
        return sTiAcceso;
    }

//    public void setsFeEmiIni(String sFeEmiIni) {
//        this.sFeEmiIni = sFeEmiIni;
//    }
//
//    public String getsFeEmiFin() {
//        return sFeEmiFin;
//    }
//
//    public void setsFeEmiFin(String sFeEmiFin) {
//        this.sFeEmiFin = sFeEmiFin;
//    }

    public String getsCoAnnio() {
        return sCoAnnio;
    }

    public void setsCoAnnio(String sCoAnnio) {
        this.sCoAnnio = sCoAnnio;
    }

    public String getsEstadoDoc() {
        return sEstadoDoc;
    }

    public void setsEstadoDoc(String sEstadoDoc) {
        this.sEstadoDoc = sEstadoDoc;
    }

    public String getsPrioridadDoc() {
        return sPrioridadDoc;
    }

    public void setsPrioridadDoc(String sPrioridadDoc) {
        this.sPrioridadDoc = sPrioridadDoc;
    }

    public String getsTipoDoc() {
        return sTipoDoc;
    }

    public void setsTipoDoc(String sTipoDoc) {
        this.sTipoDoc = sTipoDoc;
    }

    public String getsExpediente() {
        return sExpediente;
    }

    public void setsExpediente(String sExpediente) {
        this.sExpediente = sExpediente;
    }

    public String getsRefOrigen() {
        return sRefOrigen;
    }

    public void setsRefOrigen(String sRefOrigen) {
        this.sRefOrigen = sRefOrigen;
    }

    public String getsDestinatario() {
        return sDestinatario;
    }

    public void setsDestinatario(String sDestinatario) {
        this.sDestinatario = sDestinatario;
    }

    public String getsElaboradoPor() {
        return sElaboradoPor;
    }

    public void setsElaboradoPor(String sElaboradoPor) {
        this.sElaboradoPor = sElaboradoPor;
    }

    public String getsTipoBusqueda() {
        return sTipoBusqueda;
    }

    public void setsTipoBusqueda(String sTipoBusqueda) {
        this.sTipoBusqueda = sTipoBusqueda;
    }

    public String getsNumCorEmision() {
        return sNumCorEmision;
    }

    public void setsNumCorEmision(String sNumCorEmision) {
        this.sNumCorEmision = sNumCorEmision;
    }

    public String getsDeEmiReferencia() {
        return sDeEmiReferencia;
    }

    public void setsDeEmiReferencia(String sDeEmiReferencia) {
        this.sDeEmiReferencia = sDeEmiReferencia;
    }

    public String getsDeTipoDocAdm() {
        return sDeTipoDocAdm;
    }

    public void setsDeTipoDocAdm(String sDeTipoDocAdm) {
        this.sDeTipoDocAdm = sDeTipoDocAdm;
    }

    public String getsNumDoc() {
        return sNumDoc;
    }

    public void setsNumDoc(String sNumDoc) {
        this.sNumDoc = sNumDoc;
    }

    public String getsDeAsuM() {
        return sDeAsuM;
    }

    public void setsDeAsuM(String sDeAsuM) {
        this.sDeAsuM = sDeAsuM;
    }

    public String getsBuscDestinatario() {
        return sBuscDestinatario;
    }

    public void setsBuscDestinatario(String sBuscDestinatario) {
        this.sBuscDestinatario = sBuscDestinatario;
    }

//    public String getsBuscElaboraradoPor() {
//        return sBuscElaboraradoPor;
//    }
//
//    public void setsBuscElaboraradoPor(String sBuscElaboraradoPor) {
//        this.sBuscElaboraradoPor = sBuscElaboraradoPor;
//    }

    public String getsBuscNroExpediente() {
        return sBuscNroExpediente;
    }

    public void setsBuscNroExpediente(String sBuscNroExpediente) {
        this.sBuscNroExpediente = sBuscNroExpediente;
    }

//    public String getsBuscEstado() {
//        return sBuscEstado;
//    }
//
//    public void setsBuscEstado(String sBuscEstado) {
//        this.sBuscEstado = sBuscEstado;
//    }

    /**
     * @return the sTrabDestino
     */
    public String getsTrabDestino() {
        return sTrabDestino;
    }

    /**
     * @param sTrabDestino the sTrabDestino to set
     */
    public void setsTrabDestino(String sTrabDestino) {
        this.sTrabDestino = sTrabDestino;
    }

    /**
     * @return the coEmpRec
     */
    public String getCoEmpRec() {
        return coEmpRec;
    }

    /**
     * @param coEmpRec the coEmpRec to set
     */
    public void setCoEmpRec(String coEmpRec) {
        this.coEmpRec = coEmpRec;
    }

    /**
     * @return the coEstMensajeria
     */
    public String getCoEstMensajeria() {
        return coEstMensajeria;
    }

    /**
     * @param coEstMensajeria the coEstMensajeria to set
     */
    public void setCoEstMensajeria(String coEstMensajeria) {
        this.coEstMensajeria = coEstMensajeria;
    }

    /**
     * @return the coEst
     */
    public String getCoEst() {
        return coEst;
    }

    /**
     * @param coEst the coEst to set
     */
    public void setCoEst(String coEst) {
        this.coEst = coEst;
    }

    /**
     * @return the deEst
     */
    public String getDeEst() {
        return deEst;
    }

    /**
     * @param deEst the deEst to set
     */
    public void setDeEst(String deEst) {
        this.deEst = deEst;
    }

    /**
     * @return the txtRemitente
     */
    public String getTxtRemitente() {
        return txtRemitente;
    }

    /**
     * @param txtRemitente the txtRemitente to set
     */
    public void setTxtRemitente(String txtRemitente) {
        this.txtRemitente = txtRemitente;
    }

    /**
     * @return the txtDestinatario
     */
    public String getTxtDestinatario() {
        return txtDestinatario;
    }

    /**
     * @param txtDestinatario the txtDestinatario to set
     */
    public void setTxtDestinatario(String txtDestinatario) {
        this.txtDestinatario = txtDestinatario;
    }

    /**
     * @return the txtDepEmiteBus
     */
    public String getTxtDepEmiteBus() {
        return txtDepEmiteBus;
    }

    /**
     * @param txtDepEmiteBus the txtDepEmiteBus to set
     */
    public void setTxtDepEmiteBus(String txtDepEmiteBus) {
        this.txtDepEmiteBus = txtDepEmiteBus;
    }

    /**
     * @return the txtRefOrigen
     */
    public String getTxtRefOrigen() {
        return txtRefOrigen;
    }

    /**
     * @param txtRefOrigen the txtRefOrigen to set
     */
    public void setTxtRefOrigen(String txtRefOrigen) {
        this.txtRefOrigen = txtRefOrigen;
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
     * @return the txtElaboradoPor
     */
    public String getTxtElaboradoPor() {
        return txtElaboradoPor;
    }

    /**
     * @param txtElaboradoPor the txtElaboradoPor to set
     */
    public void setTxtElaboradoPor(String txtElaboradoPor) {
        this.txtElaboradoPor = txtElaboradoPor;
    }
}
