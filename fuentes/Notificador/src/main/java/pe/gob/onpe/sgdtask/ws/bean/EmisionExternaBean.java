/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.bean;

import java.util.Date;

/**
 *
 * @author ecueva
 */
public class EmisionExternaBean {
    private String noEntidadExterna;
    private long coRecepcionExterna;
    private String coEntidadExterna;
    private long coEmision;
    private Date feEnvio;
    private String inEnvio;
    private byte[] blSignatureEmision;
    private byte[] blSignatureRecepcion;
    private Date feEmiSgd;
    private int nuAnexos;
    private String inTupa;

    public String getInTupa() {
        return inTupa;
    }

    public void setInTupa(String inTupa) {
        this.inTupa = inTupa;
    }
    
    public int getNuAnexos() {
        return nuAnexos;
    }

    public void setNuAnexos(int nuAnexos) {
        this.nuAnexos = nuAnexos;
    }
    
    public Date getFeEmiSgd() {
        return feEmiSgd;
    }

    public void setFeEmiSgd(Date feEmiSgd) {
        this.feEmiSgd = feEmiSgd;
    }
    
    public byte[] getBlSignatureEmision() {
        return blSignatureEmision;
    }

    public void setBlSignatureEmision(byte[] blSignatureEmision) {
        this.blSignatureEmision = blSignatureEmision;
    }

    public byte[] getBlSignatureRecepcion() {
        return blSignatureRecepcion;
    }

    public void setBlSignatureRecepcion(byte[] blSignatureRecepcion) {
        this.blSignatureRecepcion = blSignatureRecepcion;
    }
    
    public String getInEnvio() {
        return inEnvio;
    }

    public void setInEnvio(String inEnvio) {
        this.inEnvio = inEnvio;
    }
    
    public String getCoEntidadExterna() {
        return coEntidadExterna;
    }

    public void setCoEntidadExterna(String coEntidadExterna) {
        this.coEntidadExterna = coEntidadExterna;
    }
    
    public long getCoRecepcionExterna() {
        return coRecepcionExterna;
    }

    public void setCoRecepcionExterna(long coRecepcionExterna) {
        this.coRecepcionExterna = coRecepcionExterna;
    }
    
    public String getNoEntidadExterna() {
        return noEntidadExterna;
    }

    public void setNoEntidadExterna(String noEntidadExterna) {
        this.noEntidadExterna = noEntidadExterna;
    }
    
    public long getCoEmision() {
        return coEmision;
    }

    public void setCoEmision(long coEmision) {
        this.coEmision = coEmision;
    }

    public Date getFeEnvio() {
        return feEnvio;
    }

    public void setFeEnvio(Date feEnvio) {
        this.feEnvio = feEnvio;
    }
    
}
