/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.AsignacionFuncionarioBean;

/**
 *
 * @author FSilva
 */
public interface AsignacionFuncionarioDao {
    //Registra Asignación
    public String[] insAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
    //Consulta Asignaciónes
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
    //Actualiza Asignaciónes
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
    //Consulta Una Asignación
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionarioBean);
    //Obtiene el funcionario actual
    public AsignacionFuncionarioBean getFuncionarioPorDependencia(String coDependencia);
    //Verifica si un empleado tiene programaciones pendientes
    public int getExisteProgramacionEmpleado(String coEmpleado);
    //Verifica si una entidad tiene programaciones pendientes
    public int getExisteProgramacionDependencia(String coDependencia);
    //Elimina una asignación , cambia de estado
    public String delAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean);
}
