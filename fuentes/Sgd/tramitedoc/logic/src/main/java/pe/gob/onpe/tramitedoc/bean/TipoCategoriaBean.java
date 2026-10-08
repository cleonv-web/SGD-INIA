/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import java.io.Serializable;

/**
 *
 * @author oti3
 */
public class TipoCategoriaBean implements Serializable{
    private String cidcat;
    private String vdescat;

    public String getCidcat() {
        return cidcat;
    }

    public void setCidcat(String cidcat) {
        this.cidcat = cidcat;
    }

    public String getVdescat() {
        return vdescat;
    }

    public void setVdescat(String vdescat) {
        this.vdescat = vdescat;
    }

       
}
