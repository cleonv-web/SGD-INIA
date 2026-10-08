/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ECueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class ReferenciaEmiDocBean {
    private String nuAnn;
    private String coTipDoc;
    private String esDocEmi; // si el documento fue emitido emi o rec si fue recepcionado
    private String nuDoc;
    private String fechaEmi;
    private String nuDes;
    private String nuEmi;
    private String coRef; //correlativo de referencia
    private String accionBD;
    private String coUseCre;

    public ReferenciaEmiDocBean() {
    }

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }

    public String getAccionBD() {
        return accionBD;
    }

    public void setAccionBD(String accionBD) {
        this.accionBD = accionBD;
    }

    public String getCoRef() {
        return coRef;
    }

    public void setCoRef(String coRef) {
        this.coRef = coRef;
    }

    public String getNuDes() {
        return nuDes;
    }

    public void setNuDes(String nuDes) {
        this.nuDes = nuDes;
    }

    public String getNuEmi() {
        return nuEmi;
    }

    public void setNuEmi(String nuEmi) {
        this.nuEmi = nuEmi;
    }

    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getCoTipDoc() {
        return coTipDoc;
    }

    public void setCoTipDoc(String coTipDoc) {
        this.coTipDoc = coTipDoc;
    }

    public String getEsDocEmi() {
        return esDocEmi;
    }

    public void setEsDocEmi(String esDocEmi) {
        this.esDocEmi = esDocEmi;
    }

    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }

    public String getFechaEmi() {
        return fechaEmi;
    }

    public void setFechaEmi(String fechaEmi) {
        this.fechaEmi = fechaEmi;
    }
}
