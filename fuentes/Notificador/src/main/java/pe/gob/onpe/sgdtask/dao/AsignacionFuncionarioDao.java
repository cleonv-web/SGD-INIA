/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.dao;

import java.util.List;
import pe.gob.onpe.sgdtask.bean.AsignacionFuncionarioBean;

/**
 *
 * @author FSilva
 */
public interface AsignacionFuncionarioDao {    
    //Consulta Asignaciónes
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario();
    //Consulta Asignaciónes que finalizan
    public List<AsignacionFuncionarioBean> getListAsignacionFinaliza();
    //Actualiza Asignaciónes
    public String updAsignacionFuncionarioDependencia(AsignacionFuncionarioBean asignacionFuncionarioBean);
    //Actualiza Empleado Titular
    public String updEmpleadoTitular(AsignacionFuncionarioBean asignacionFuncionarioBean);
    
      public String updEmpleadoDelegado(AsignacionFuncionarioBean asignacionFuncionarioBean);
    
    //Consulta Una Asignación
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionarioBean);    
    //Actualiza Asignaciónes
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);    
    //Restablece a  Empleado Titular
    public String updRestableceEmpleadoTitular(AsignacionFuncionarioBean asignacionFuncionarioBean);
}
