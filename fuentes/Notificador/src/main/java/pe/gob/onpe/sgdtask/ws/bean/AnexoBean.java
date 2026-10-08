/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.bean;

import java.util.Date;

/**
 *
 * @author FSilva
 */
public class AnexoBean {
    private long coAnexo;
    private byte[] blDocAnexo;
    private long coDocumentoExterno;
    private String coEstado;
    private Date feRegistra;
    private String deDescripcion;

    public String getDeDescripcion() {
        return deDescripcion;
    }

    public void setDeDescripcion(String deDescripcion) {
        this.deDescripcion = deDescripcion;
    }    
    
    public long getCoAnexo() {
        return coAnexo;
    }

    public void setCoAnexo(long coAnexo) {
        this.coAnexo = coAnexo;
    }

    public byte[] getBlDocAnexo() {
        return blDocAnexo;
    }

    public void setBlDocAnexo(byte[] blDocAnexo) {
        this.blDocAnexo = blDocAnexo;
    }

    public long getCoDocumentoExterno() {
        return coDocumentoExterno;
    }

    public void setCoDocumentoExterno(long coDocumentoExterno) {
        this.coDocumentoExterno = coDocumentoExterno;
    }

    public String getCoEstado() {
        return coEstado;
    }

    public void setCoEstado(String coEstado) {
        this.coEstado = coEstado;
    }

    public Date getFeRegistra() {
        return feRegistra;
    }

    public void setFeRegistra(Date feRegistra) {
        this.feRegistra = feRegistra;
    }
    
    
}
