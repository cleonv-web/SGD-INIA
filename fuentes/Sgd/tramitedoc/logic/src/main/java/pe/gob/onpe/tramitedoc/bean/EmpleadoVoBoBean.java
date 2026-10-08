/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ecueva
 */
public class EmpleadoVoBoBean {
    private String coEmpleado;
    private String nombre;
    private String deApPaterno;
    private String deApMaterno;
    private String accionBD;
    private String coDependencia;
    private String deDependencia;
    private String inVobo;//indicador de VoBo

    public EmpleadoVoBoBean() {
    }

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getDeApPaterno() {
        return deApPaterno;
    }

    public void setDeApPaterno(String deApPaterno) {
        this.deApPaterno = deApPaterno;
    }

    public String getDeApMaterno() {
        return deApMaterno;
    }

    public void setDeApMaterno(String deApMaterno) {
        this.deApMaterno = deApMaterno;
    }

    public String getAccionBD() {
        return accionBD;
    }

    public void setAccionBD(String accionBD) {
        this.accionBD = accionBD;
    }

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }

    public String getInVobo() {
        return inVobo;
    }

    public void setInVobo(String inVobo) {
        this.inVobo = inVobo;
    }
}
