/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import java.util.ArrayList;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ECueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrxDocumentoEmiBean {
    private String accionBD;
    private String nuAnn;
    private String nuEmi;
    private String nuCorEmi;
    private String coUserMod;
    private String cempCodEmp;
    private String nuAnnExp;
    private String nuSecExp;
    private String feExp;
    private String nuExpediente;
    private String nuDoc;
    private String nuDocEmi;
    
    private DocumentoEmiBean documentoEmiBean;
    private ExpedienteBean expedienteEmiBean;
    private RemitenteEmiBean remitenteEmiBean;
    private ArrayList<ReferenciaEmiDocBean> lstReferencia;
    private ArrayList<DestinatarioDocumentoEmiBean> lstDestinatario;
    private ArrayList<EmpleadoVoBoBean> lstEmpVoBo;

    public String getAccionBD() {
        return accionBD;
    }

    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }

    public String getNuDocEmi() {
        return nuDocEmi;
    }

    public void setNuDocEmi(String nuDocEmi) {
        this.nuDocEmi = nuDocEmi;
    }

    public void setAccionBD(String accionBD) {
        this.accionBD = accionBD;
    }

    public RemitenteEmiBean getRemitenteEmiBean() {
        return remitenteEmiBean;
    }

    public void setRemitenteEmiBean(RemitenteEmiBean remitenteEmiBean) {
        this.remitenteEmiBean = remitenteEmiBean;
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

    public String getFeExp() {
        return feExp;
    }

    public void setFeExp(String feExp) {
        this.feExp = feExp;
    }

    public String getNuExpediente() {
        return nuExpediente;
    }

    public void setNuExpediente(String nuExpediente) {
        this.nuExpediente = nuExpediente;
    }

    public String getNuCorEmi() {
        return nuCorEmi;
    }

    public void setNuCorEmi(String nuCorEmi) {
        this.nuCorEmi = nuCorEmi;
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

    public DocumentoEmiBean getDocumentoEmiBean() {
        return documentoEmiBean;
    }

    public ExpedienteBean getExpedienteEmiBean() {
        return expedienteEmiBean;
    }

    public void setExpedienteEmiBean(ExpedienteBean expedienteEmiBean) {
        this.expedienteEmiBean = expedienteEmiBean;
    }

    public ArrayList<ReferenciaEmiDocBean> getLstReferencia() {
        return lstReferencia;
    }

    public void setLstReferencia(ArrayList<ReferenciaEmiDocBean> lstReferencia) {
        this.lstReferencia = lstReferencia;
    }

    public void setDocumentoEmiBean(DocumentoEmiBean documentoEmiBean) {
        this.documentoEmiBean = documentoEmiBean;
    }

    public ArrayList<DestinatarioDocumentoEmiBean> getLstDestinatario() {
        return lstDestinatario;
    }

    public void setLstDestinatario(ArrayList<DestinatarioDocumentoEmiBean> lstDestinatario) {
        this.lstDestinatario = lstDestinatario;
    }

    public ArrayList<EmpleadoVoBoBean> getLstEmpVoBo() {
        return lstEmpVoBo;
    }

    public void setLstEmpVoBo(ArrayList<EmpleadoVoBoBean> lstEmpVoBo) {
        this.lstEmpVoBo = lstEmpVoBo;
    }
}
