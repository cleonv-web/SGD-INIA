/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author 
 */
public class OrigenDocumentoEmiBean {
    private String codOrigenDoc;
    private String detOrigenDoc;

    
    public OrigenDocumentoEmiBean() {
    }
    /**
     * @return the codOrigenDoc
     */
    public String getCodOrigenDoc() {
        return codOrigenDoc;
    }

    /**
     * @param codOrigenDoc the codOrigenDoc to set
     */
    public void setCodOrigenDoc(String codOrigenDoc) {
        this.codOrigenDoc = codOrigenDoc;
    }

    /**
     * @return the detOrigenDoc
     */
    public String getDetOrigenDoc() {
        return detOrigenDoc;
    }

    /**
     * @param detOrigenDoc the detOrigenDoc to set
     */
    public void setDetOrigenDoc(String detOrigenDoc) {
        this.detOrigenDoc = detOrigenDoc;
    }

}
