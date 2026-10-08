/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.bean;

import java.sql.Date;

/**
 *
 * @author FSilva
 */
public class NotificacionJobBean {
    private long coNotificacionJob;
    private String coEmp;
    private Date feEnvio;
    private String emailEmp;
    private String coEstado;
    private String coUseCre;

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }
        
    public String getEmailEmp() {
        return emailEmp;
    }

    public void setEmailEmp(String emailEmp) {
        this.emailEmp = emailEmp;
    }

    
    public long getCoNotificacionJob() {
        return coNotificacionJob;
    }

    public void setCoNotificacionJob(long coNotificacionJob) {
        this.coNotificacionJob = coNotificacionJob;
    }

    public String getCoEmp() {
        return coEmp;
    }

    public void setCoEmp(String coEmp) {
        this.coEmp = coEmp;
    }

    public Date getFeEnvio() {
        return feEnvio;
    }

    public void setFeEnvio(Date feEnvio) {
        this.feEnvio = feEnvio;
    }

    public String getCoEstado() {
        return coEstado;
    }

    public void setCoEstado(String coEstado) {
        this.coEstado = coEstado;
    }
    
}
