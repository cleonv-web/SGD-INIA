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
public class TipoExpedienteEmiBean {
    private String codTipExp;
    private String detTipExp;

    
    public TipoExpedienteEmiBean() {
    }

    public String getCodTipExp() {
        return codTipExp;
    }

    /**
     * @param codTipExp the codTipExp to set
     */
    public void setCodTipExp(String codTipExp) {
        this.codTipExp = codTipExp;
    }

    /**
     * @return the detTipExp
     */
    public String getDetTipExp() {
        return detTipExp;
    }

    /**
     * @param detTipExp the detTipExp to set
     */
    public void setDetTipExp(String detTipExp) {
        this.detTipExp = detTipExp;
    }

}
