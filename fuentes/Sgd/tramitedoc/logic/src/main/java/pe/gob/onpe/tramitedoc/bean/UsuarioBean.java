/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author kfrancia
 */
public class UsuarioBean {
    private String codUser;
    private String esActivo;
    private String cdesUser;

    public String getCodUser() {
        return codUser;
    }

    public void setCodUser(String codUser) {
        this.codUser = codUser;
    }

    public String getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(String esActivo) {
        this.esActivo = esActivo;
    }

    public String getCdesUser() {
        return cdesUser;
    }

    public void setCdesUser(String cdesUser) {
        this.cdesUser = cdesUser;
    }

}
