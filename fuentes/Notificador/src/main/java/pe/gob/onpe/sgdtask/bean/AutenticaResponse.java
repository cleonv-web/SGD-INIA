/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.bean;

/**
 *
 * @author FSilva
 */
public class AutenticaResponse {
    private String coRespuesta;
    private String deRespuesta;
    private String deLogin;
    private String deNombres;
    private String coDependencia;
    private String deDependencia;
    private String coEmpleado;
    private String coTipoAcceso;

    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }
    
    public String getCoRespuesta() {
        return coRespuesta;
    }

    public void setCoRespuesta(String coRespuesta) {
        this.coRespuesta = coRespuesta;
    }

    public String getDeRespuesta() {
        return deRespuesta;
    }

    public void setDeRespuesta(String deRespuesta) {
        this.deRespuesta = deRespuesta;
    }

    public String getDeLogin() {
        return deLogin;
    }

    public void setDeLogin(String deLogin) {
        this.deLogin = deLogin;
    }

    public String getDeNombres() {
        return deNombres;
    }

    public void setDeNombres(String deNombres) {
        this.deNombres = deNombres;
    }

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
    }

    public String getCoTipoAcceso() {
        return coTipoAcceso;
    }

    public void setCoTipoAcceso(String coTipoAcceso) {
        this.coTipoAcceso = coTipoAcceso;
    }
    
    
}
