/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.service;

import java.util.List;
import pe.gob.onpe.sgdtask.bean.AsignacionFuncionarioBean;

/**
 *
 * @author FSilva
 */
public interface AsignacionFuncionarioService {
    
    //Consulta Asignaciónes
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario();
    //Consulta Asignacion
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionario);    
    //Actualiza el estado de una asignación 
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) throws Exception ;
    //Inicializar el ejecutador de tareas para llamados desde el controller    
    public void getIniciarTareaAsignacionFuncionario();    
}
