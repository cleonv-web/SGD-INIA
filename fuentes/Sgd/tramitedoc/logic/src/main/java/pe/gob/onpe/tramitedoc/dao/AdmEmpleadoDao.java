/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoCargoBean;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoCategoriaBean;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoAccesoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;

/**
 *
 * @author GLuque
 */
public interface AdmEmpleadoDao {
    
    /**
     * Obtiene un determinado empleado mediante su codigo
     * @param codEmp
     * @return
     * @throws Exception 
     */    
    AdmEmpleadoBean getAdmEmpleado(String codEmp) throws Exception;
    
    /**
     * Obtiene un determinado empleado mediante su DNI
     * @param dni
     * @return
     * @throws Exception 
     */
    AdmEmpleadoBean getAdmEmpleadoDesdeDni(String dni) throws Exception;
    
    /**
     * Actualiza datos personales de un empleado
     * @param empleado
     * @return
     * @throws Exception 
     */
    int updAdmEmpleado(AdmEmpleadoBean empleado, Usuario usuario) throws Exception;
    
    /**
     * Guardar nuevo empleado
     * @param empleado
     * @param usuario
     * @return
     * @throws Exception 
     */
    String nuevoAdmEmpleado(AdmEmpleadoBean empleado, Usuario usuario) throws Exception;
    
    
    /**
     * Busca empleado de la tabla maestra mediante DNI
     * @param dni
     * @return Object AdmEmpleadoBean
     * @throws Exception 
     */
    AdmEmpleadoBean getPersonaDesdeDni(String dni) throws Exception;
    
    /**
     * Realiza búsqueda segun a los criterios detallados lineas abajo:
     * @param dni
     * @param apPaterno
     * @param apMaterno
     * @param nombres
     * @return
     * @throws Exception 
     */
    List<AdmEmpleadoBean> getBsqAdmEmpleado(String dni, String apPaterno, String apMaterno, String nombres, String usuario) throws Exception;
    
    /**
     * Obtiene una lista de dependencia,
     * realizando una búsqueda de acuerdo al parámetro "CRITERIO"
     * @param criterio
     * @return
     * @throws Exception 
     */
    List<DependenciaBean> getBsqDependencia(String criterio)throws Exception;
    
    /**
     * Obtiene lista de cargos de los empleados
     * @return
     * @throws Exception 
     */
    List<AdmEmpleadoCargoBean> getLsCargo() throws Exception;
    
    
    /**
     * Obtiene Objeto Acceso del Empleado
     * @param coEmpleado
     * @return
     * @throws Exception 
     */
    AdmEmpleadoAccesoBean getAcceso(String coEmpleado) throws Exception;
    
    /**
     * Obtiene lista de Obj Acceso que coincidan con el NombreUsuario el cual se 
     * le asignará al empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    List<AdmEmpleadoAccesoBean> getLsAcceso(AdmEmpleadoAccesoBean acceso) throws Exception;
    
    
    /**
     * Guarda nuevo Usuario con Acceso al sistema
     * @param coUser
     * @param coEmp
     * @param nuDni
     * @return
     * @throws Exception 
     */
    String saveNuevoAcceso(String coUser, String coEmp, String nuDni, String inAD) throws Exception;
    
     String updAcceso(String coUser, String coEmp, String nuDni, String inAD) throws Exception;
    
    
    /**
     * Elimina acceso al sistema para un determinado empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    /*boolean deleteAcceso(AdmEmpleadoAccesoBean acceso) throws Exception;*/
    
    
    /**
     * Restablece el acceso al sistema de un empleado
     * @param acceso
     * @param empleado
     * @return
     * @throws Exception 
     */
    /*
    boolean restablecerAcceso(AdmEmpleadoAccesoBean acceso, AdmEmpleadoBean empleado) throws Exception;*/
    
    String getNextCoEmpleado();
}
