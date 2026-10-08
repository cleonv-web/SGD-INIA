/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author GLuque
 */
public class AdmEmpleadoGlobalBean {
    private AdmEmpleadoBean empleado;
    private AdmEmpleadoAccesoBean acceso;

    public AdmEmpleadoBean getEmpleado() {
        return empleado;
    }

    public void setEmpleado(AdmEmpleadoBean empleado) {
        this.empleado = empleado;
    }

    public AdmEmpleadoAccesoBean getAcceso() {
        return acceso;
    }

    public void setAcceso(AdmEmpleadoAccesoBean acceso) {
        this.acceso = acceso;
    }
    
}
