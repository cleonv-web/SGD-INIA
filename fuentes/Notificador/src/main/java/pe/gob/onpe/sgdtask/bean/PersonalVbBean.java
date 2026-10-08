/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.bean;

import java.util.List;

/**
 *
 * @author FSilva
 */
public class PersonalVbBean {
    private String coEmpVb;
    private String emailEmp;
    private String coEstadoEnvio;
    private String deNombreCompleto;    
    private String coUseCre;
    private String noEntidadNotifica;
    private String deSiglaEntidadNotifica;

    public String getDeSiglaEntidadNotifica() {
        return deSiglaEntidadNotifica;
    }

    public void setDeSiglaEntidadNotifica(String deSiglaEntidadNotifica) {
        this.deSiglaEntidadNotifica = deSiglaEntidadNotifica;
    }
    
    public String getNoEntidadNotifica() {
        return noEntidadNotifica;
    }

    public void setNoEntidadNotifica(String noEntidadNotifica) {
        this.noEntidadNotifica = noEntidadNotifica;
    }
        
    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }
    
    public String getDeNombreCompleto() {
        return deNombreCompleto;
    }

    public void setDeNombreCompleto(String deNombreCompleto) {
        this.deNombreCompleto = deNombreCompleto;
    }
    
    private List<DocumentosVbBean> listaDocumentos;

    public List<DocumentosVbBean> getListaDocumentos() {
        return listaDocumentos;
    }

    public void setListaDocumentos(List<DocumentosVbBean> listaDocumentos) {
        this.listaDocumentos = listaDocumentos;
    }

    public String getCoEstadoEnvio() {
        return coEstadoEnvio;
    }

    public void setCoEstadoEnvio(String coEstadoEnvio) {
        this.coEstadoEnvio = coEstadoEnvio;
    }
    
    public String getCoEmpVb() {
        return coEmpVb;
    }

    public void setCoEmpVb(String coEmpVb) {
        this.coEmpVb = coEmpVb;
    }

    public String getEmailEmp() {
        return emailEmp;
    }

    public void setEmailEmp(String emailEmp) {
        this.emailEmp = emailEmp;
    }
    
}
