/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author NGilt
 */
public class ConConfigBusquedaEmiDocBean {

    private String conAnioList;
    private String conMesList;
    private String conDiaList;
    private String conDestinatariosList;
    private String conTipoRemitenteList;
    private String conExpedienteList;
    private String conRemitenteList;
    private String conEstadoList;
    private String conTipoDocumentoList;
    private String conFInicial;
    private String conFFinal;
    private String rbOpcionFecha;

    public ConConfigBusquedaEmiDocBean() {
    }

    public ConConfigBusquedaEmiDocBean(String conAnioList, String conMesList, String conDiaList, String conDestinatariosList, String conTipoRemitenteList, String conExpedienteList, String conRemitenteList, String conEstadoList, String conTipoDocumentoList, String conFInicial, String conFFinal) {
        this.conAnioList = conAnioList;
        this.conMesList = conMesList;
        this.conDiaList = conDiaList;
        this.conDestinatariosList = conDestinatariosList;
        this.conTipoRemitenteList = conTipoRemitenteList;
        this.conExpedienteList = conExpedienteList;
        this.conRemitenteList = conRemitenteList;
        this.conEstadoList = conEstadoList;
        this.conTipoDocumentoList = conTipoDocumentoList;
        this.conFInicial = conFInicial;
        this.conFFinal = conFFinal;
    }

    public String getConAnioList() {
        return conAnioList;
    }

    public void setConAnioList(String conAnioList) {
        this.conAnioList = conAnioList;
    }

    public String getConMesList() {
        return conMesList;
    }

    public void setConMesList(String conMesList) {
        this.conMesList = conMesList;
    }

    public String getConDiaList() {
        return conDiaList;
    }

    public void setConDiaList(String conDiaList) {
        this.conDiaList = conDiaList;
    }

    public String getConDestinatariosList() {
        return conDestinatariosList;
    }

    public void setConDestinatariosList(String conDestinatariosList) {
        this.conDestinatariosList = conDestinatariosList;
    }

    public String getConTipoRemitenteList() {
        return conTipoRemitenteList;
    }

    public void setConTipoRemitenteList(String conTipoRemitenteList) {
        this.conTipoRemitenteList = conTipoRemitenteList;
    }

    public String getConExpedienteList() {
        return conExpedienteList;
    }

    public void setConExpedienteList(String conExpedienteList) {
        this.conExpedienteList = conExpedienteList;
    }

    public String getConRemitenteList() {
        return conRemitenteList;
    }

    public void setConRemitenteList(String conRemitenteList) {
        this.conRemitenteList = conRemitenteList;
    }

    public String getConEstadoList() {
        return conEstadoList;
    }

    public void setConEstadoList(String conEstadoList) {
        this.conEstadoList = conEstadoList;
    }

    public String getConTipoDocumentoList() {
        return conTipoDocumentoList;
    }

    public void setConTipoDocumentoList(String conTipoDocumentoList) {
        this.conTipoDocumentoList = conTipoDocumentoList;
    }

    public String getConFInicial() {
        return conFInicial;
    }

    public void setConFInicial(String conFInicial) {
        this.conFInicial = conFInicial;
    }

    public String getConFFinal() {
        return conFFinal;
    }

    public void setConFFinal(String conFFinal) {
        this.conFFinal = conFFinal;
    }

    public String getRbOpcionFecha() {
        return rbOpcionFecha;
    }

    public void setRbOpcionFecha(String rbOpcionFecha) {
        this.rbOpcionFecha = rbOpcionFecha;
    }
}
