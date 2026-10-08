/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.consultadoc.bean;

/**
 *
 * @author RATAYAURI
 */
public class TipoDocBean {
    
    private String coTipDoc;
    private String deTipDoc;

    public TipoDocBean() {
    }
    
    public TipoDocBean(String coTipDoc, String deTipDoc) {
        this.coTipDoc = coTipDoc;
        this.deTipDoc = deTipDoc;
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
