/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.AsignacionFuncionarioBean;

/**
 *
 * @author FSilva
 */
public interface AsignacionFuncionarioService {
     //Registra Asignación
    public String[] insAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
    //Consulta Asignaciónes
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
    //Obtiene el funcionario actual
    public AsignacionFuncionarioBean getFuncionarioPorDependencia(String coDependencia);
    //validacion de asignacion de empleado
    public int getExisteProgramacionEmpleado(String coEmpleado);
    //validacion de asignacion de entidad
    public int getExisteProgramacionDependencia(String coDependencia) ;
    //Actualiza el estado de una asignación 
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
    //Consulta Una Asignación
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionarioBean);
    //Elimina una asignación , cambia de estado
    public String delAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
}
