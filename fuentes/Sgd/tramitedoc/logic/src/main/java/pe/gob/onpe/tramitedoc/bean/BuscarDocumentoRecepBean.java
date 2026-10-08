package pe.gob.onpe.tramitedoc.bean;

import pe.gob.onpe.tramitedoc.util.FiltroPaginate;

public class BuscarDocumentoRecepBean extends FiltroPaginate {


    private String sCoAnnio;
    private String sEstadoDoc;
    private String sCoDependencia;
    private String sCoEmpleado;    
    private String sPrioridadDoc;
    private String sTipoDoc;
    private String sRemitente;
    private String sDestinatario;
    private String sExpediente;
    private String sTiAcceso;
    private String esFiltroFecha;
    private String sFeEmiIni;
    private String sFeEmiFin;    
    private String idEtiqueta;    
    
    /***************************************/
    private String sTipoBusqueda;//0 Filtrar, 1 Buscar
    /***************************************/
    //busqueda desde tabla hacia BD
//    private String sFechaEmisionIni;
//    private String sFechaEmisionFin;
//    private String sUoremitente;
    private String sNroEmision;
    private String sDeTipoDocAdm;
    private String sNroDocumento;
//    private String sUoDestinatario;
    private String sBuscAsunto;
    private String sBusRemitente;
    private String sBuscNroExpediente;
    private String sBuscDestinatario;
    private String sNumDocRef;
    private boolean esIncluyeFiltro;
    private String sCoAnnioBus;
    private String sTipoProyDoc;
    private String coTema;
    private boolean esIncluyeOficina;
    private boolean esIncluyeProfesional;
    
    private String esRecibido;
    private String txtRemitente;
    private String txtDestinatario;
    private String txtDepEmiteBus;
    
    
    public String getCoTema() {
        return coTema;
    }

    public void setCoTema(String coTema) {
        this.coTema = coTema;
    }
//    private String sBuscEstado;
    public String getIdEtiqueta() {
        return idEtiqueta;
    }

    public void setIdEtiqueta(String idEtiqueta) {
        this.idEtiqueta = idEtiqueta;
    }
    

    public BuscarDocumentoRecepBean() {
        this.sTipoBusqueda = "0";
    }

    public String getsCoAnnioBus() {
        return sCoAnnioBus;
    }

    public void setsCoAnnioBus(String sCoAnnioBus) {
        this.sCoAnnioBus = sCoAnnioBus;
    }

    public String getEsFiltroFecha() {
        return esFiltroFecha;
    }

    public void setEsFiltroFecha(String esFiltroFecha) {
        this.esFiltroFecha = esFiltroFecha;
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

    public boolean isEsIncluyeFiltro() {
        return esIncluyeFiltro;
    }

    public void setEsIncluyeFiltro(boolean esIncluyeFiltro) {
        this.esIncluyeFiltro = esIncluyeFiltro;
    }

    public String getsNumDocRef() {
        return sNumDocRef;
    }

    public void setsNumDocRef(String sNumDocRef) {
        this.sNumDocRef = sNumDocRef;
    }

    public String getsBuscDestinatario() {
        return sBuscDestinatario;
    }

    public void setsBuscDestinatario(String sBuscDestinatario) {
        this.sBuscDestinatario = sBuscDestinatario;
    }

//    public String getsFechaEmisionIni() {
//        return sFechaEmisionIni;
//    }
//
//    public void setsFechaEmisionIni(String sFechaEmisionIni) {
//        this.sFechaEmisionIni = sFechaEmisionIni;
//    }

//    public String getsFechaEmisionFin() {
//        return sFechaEmisionFin;
//    }
//
//    public void setsFechaEmisionFin(String sFechaEmisionFin) {
//        this.sFechaEmisionFin = sFechaEmisionFin;
//    }

    public String getsNroEmision() {
        return sNroEmision;
    }

    public void setsNroEmision(String sNroEmision) {
        this.sNroEmision = sNroEmision;
    }

    public String getsDeTipoDocAdm() {
        return sDeTipoDocAdm;
    }

    public void setsDeTipoDocAdm(String sDeTipoDocAdm) {
        this.sDeTipoDocAdm = sDeTipoDocAdm;
    }

    public String getsNroDocumento() {
        return sNroDocumento;
    }

    public void setsNroDocumento(String sNroDocumento) {
        this.sNroDocumento = sNroDocumento;
    }

//    public String getsUoDestinatario() {
//        return sUoDestinatario;
//    }
//
//    public void setsUoDestinatario(String sUoDestinatario) {
//        this.sUoDestinatario = sUoDestinatario;
//    }

    public String getsBuscAsunto() {
        return sBuscAsunto;
    }

    public void setsBuscAsunto(String sBuscAsunto) {
        this.sBuscAsunto = sBuscAsunto;
    }

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

    public String getsCoEmpleado() {
        return sCoEmpleado;
    }

    public String getsTiAcceso() {
        return sTiAcceso;
    }

    public void setsTiAcceso(String sTiAcceso) {
        this.sTiAcceso = sTiAcceso;
    }

    public void setsCoEmpleado(String sCoEmpleado) {
        this.sCoEmpleado = sCoEmpleado;
    }

    public String getsCoDependencia() {
        return sCoDependencia;
    }

    public void setsCoDependencia(String sCoDependencia) {
        this.sCoDependencia = sCoDependencia;
    }

    public String getsTipoBusqueda() {
        return sTipoBusqueda;
    }

    public void setsTipoBusqueda(String sTipoBusqueda) {
        this.sTipoBusqueda = sTipoBusqueda;
    }

//    public String getsUoremitente() {
//        return sUoremitente;
//    }
//
//    public void setsUoremitente(String sUoremitente) {
//        this.sUoremitente = sUoremitente;
//    }

    public String getsTipoDoc() {
        return sTipoDoc;
    }

    public void setsTipoDoc(String sTipoDoc) {
        this.sTipoDoc = sTipoDoc;
    }

    public String getsRemitente() {
        return sRemitente;
    }

    public void setsRemitente(String sRemitente) {
        this.sRemitente = sRemitente;
    }

    public String getsDestinatario() {
        return sDestinatario;
    }

    public void setsDestinatario(String sDestinatario) {
        this.sDestinatario = sDestinatario;
    }

    public String getsExpediente() {
        return sExpediente;
    }

    public void setsExpediente(String sExpediente) {
        this.sExpediente = sExpediente;
    }

    public String getsPrioridadDoc() {
        return sPrioridadDoc;
    }

    public void setsPrioridadDoc(String sPrioridadDoc) {
        this.sPrioridadDoc = sPrioridadDoc;
    }

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

    /**
     * @return the esRecibido
     */
    public String getEsRecibido() {
        return esRecibido;
    }

    /**
     * @param esRecibido the esRecibido to set
     */
    public void setEsRecibido(String esRecibido) {
        this.esRecibido = esRecibido;
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
     * @return the esIncluyeOficina
     */
    public boolean isEsIncluyeOficina() {
        return esIncluyeOficina;
    }

    /**
     * @param esIncluyeOficina the esIncluyeOficina to set
     */
    public void setEsIncluyeOficina(boolean esIncluyeOficina) {
        this.esIncluyeOficina = esIncluyeOficina;
    }

    /**
     * @return the esIncluyeProfesional
     */
    public boolean isEsIncluyeProfesional() {
        return esIncluyeProfesional;
    }

    /**
     * @param esIncluyeProfesional the esIncluyeProfesional to set
     */
    public void setEsIncluyeProfesional(boolean esIncluyeProfesional) {
        this.esIncluyeProfesional = esIncluyeProfesional;
    }

    public String getsBusRemitente() {
        return sBusRemitente;
    }

    public void setsBusRemitente(String sBusRemitente) {
        this.sBusRemitente = sBusRemitente;
    }
    
        /**
     * @return the sTipoProyDoc
     */
    public String getsTipoProyDoc() {
        return sTipoProyDoc;
    }

    /**
     * @param sTipoProyDoc the sTipoProyDoc to set
     */
    public void setsTipoProyDoc(String sTipoProyDoc) {
        this.sTipoProyDoc = sTipoProyDoc;
    }

    
    
}
