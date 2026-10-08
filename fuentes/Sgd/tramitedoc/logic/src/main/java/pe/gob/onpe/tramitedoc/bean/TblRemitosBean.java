/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import java.util.Date;

/**
 *
 * @author ECueva
 */
public class TblRemitosBean {
    private String nuAnn;
    private String nuEmi;
    private String coDepEmi;
    private Date feEmi;
    private String msgResult;

    public String getNuAnn() {
        return nuAnn;
    }

    public String getMsgResult() {
        return msgResult;
    }

    public void setMsgResult(String msgResult) {
        this.msgResult = msgResult;
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

    public String getCoDepEmi() {
        return coDepEmi;
    }

    public void setCoDepEmi(String coDepEmi) {
        this.coDepEmi = coDepEmi;
    }

    public Date getFeEmi() {
        return feEmi;
    }

    public void setFeEmi(Date feEmi) {
        this.feEmi = feEmi;
    }
}
