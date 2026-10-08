/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author oti2
 */
public class TemaBean {
    private String deTema;
    private String coTema;
    private String coDependencia;
    public TemaBean() {
    }
    public String getDeTema() {
        return deTema;
    }

    public void setDeTema(String deTema) {
        this.deTema = deTema;
    }

    public String getCoTema() {
        return coTema;
    }

    public void setCoTema(String coTema) {
        this.coTema = coTema;
    }

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }
}
