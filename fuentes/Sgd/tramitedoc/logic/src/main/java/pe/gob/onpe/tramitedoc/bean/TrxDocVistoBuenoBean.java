/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ecueva
 */
public class TrxDocVistoBuenoBean {
    private String nuAnn;
    private String nuEmi;
    private String coUserMod;
    private DocumentoVoBoBean docVoBoBean;

    public String getNuAnn() {
        return nuAnn;
    }

    public String getCoUserMod() {
        return coUserMod;
    }

    public void setCoUserMod(String coUserMod) {
        this.coUserMod = coUserMod;
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

    public DocumentoVoBoBean getDocVoBoBean() {
        return docVoBoBean;
    }

    public void setDocVoBoBean(DocumentoVoBoBean docVoBoBean) {
        this.docVoBoBean = docVoBoBean;
    }
}
