/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;


import java.util.ArrayList;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ecueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrxDocExternoRecepBean {
    private String nuAnn;
    private String nuEmi;
    private String nuAnnExp;
    private String nuSecExp;
    private String coUserMod;
    private String cempCodEmp;
    private String coDependencia;
    private String accionBD;
    private DocumentoExtRecepBean documentoEmiBean;
    private ExpedienteDocExtRecepBean expedienteEmiBean;
    private RemitenteDocExtRecepBean remitenteEmiBean;
    private ArrayList<ReferenciaDocExtRecepBean> lstReferencia;
    private ArrayList<DestinatarioDocumentoEmiBean> lstDestinatario;    

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getNuAnnExp() {
        return nuAnnExp;
    }

    public void setNuAnnExp(String nuAnnExp) {
        this.nuAnnExp = nuAnnExp;
    }

    public String getNuSecExp() {
        return nuSecExp;
    }

    public void setNuSecExp(String nuSecExp) {
        this.nuSecExp = nuSecExp;
    }

    public String getAccionBD() {
        return accionBD;
    }

    public void setAccionBD(String accionBD) {
        this.accionBD = accionBD;
    }

    public String getCoUserMod() {
        return coUserMod;
    }

    public void setCoUserMod(String coUserMod) {
        this.coUserMod = coUserMod;
    }

    public String getCempCodEmp() {
        return cempCodEmp;
    }

    public void setCempCodEmp(String cempCodEmp) {
        this.cempCodEmp = cempCodEmp;
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

    public DocumentoExtRecepBean getDocumentoEmiBean() {
        return documentoEmiBean;
    }

    public void setDocumentoEmiBean(DocumentoExtRecepBean documentoEmiBean) {
        this.documentoEmiBean = documentoEmiBean;
    }

    public ExpedienteDocExtRecepBean getExpedienteEmiBean() {
        return expedienteEmiBean;
    }

    public void setExpedienteEmiBean(ExpedienteDocExtRecepBean expedienteEmiBean) {
        this.expedienteEmiBean = expedienteEmiBean;
    }

    public RemitenteDocExtRecepBean getRemitenteEmiBean() {
        return remitenteEmiBean;
    }

    public void setRemitenteEmiBean(RemitenteDocExtRecepBean remitenteEmiBean) {
        this.remitenteEmiBean = remitenteEmiBean;
    }

    public ArrayList<ReferenciaDocExtRecepBean> getLstReferencia() {
        return lstReferencia;
    }

    public void setLstReferencia(ArrayList<ReferenciaDocExtRecepBean> lstReferencia) {
        this.lstReferencia = lstReferencia;
    }

    public ArrayList<DestinatarioDocumentoEmiBean> getLstDestinatario() {
        return lstDestinatario;
    }

    public void setLstDestinatario(ArrayList<DestinatarioDocumentoEmiBean> lstDestinatario) {
        this.lstDestinatario = lstDestinatario;
    }
}
