/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.bean;

/**
 *
 * @author Usuario
 */

public class TipoDocumentoBean {
    private String coDepDes;
    private String coTipDoc;
    private String deTipDoc;
    private String cdocGrupo;

    public String getCdocGrupo() {
        return cdocGrupo;
    }

    public void setCdocGrupo(String cdocGrupo) {
        this.cdocGrupo = cdocGrupo;
    }
 
    public TipoDocumentoBean() {
    }

    public String getCoDepDes() {
        return coDepDes;
    }

    public void setCoDepDes(String coDepDes) {
        this.coDepDes = coDepDes;
    }

    public String getCoTipDoc() {
        return coTipDoc;
    }

    public void setCoTipDoc(String coTipDoc) {
        this.coTipDoc = coTipDoc;
    }

    public String getDeTipDoc() {
        return deTipDoc;
    }

    public void setDeTipDoc(String deTipDoc) {
        this.deTipDoc = deTipDoc;
    }
}
