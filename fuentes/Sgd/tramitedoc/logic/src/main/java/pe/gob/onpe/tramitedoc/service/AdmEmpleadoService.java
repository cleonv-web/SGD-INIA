/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

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
public interface AdmEmpleadoService {
    
    /**
     * Obtiene un empleado
     * @param codEmp
     * @return
     * @throws Exception 
     */
    AdmEmpleadoBean getAdmEmpleado(String codEmp) throws Exception;
    
    /**
     * Actualiza datos personales de un empleado
     * @param empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    int updAdmEmpleado(AdmEmpleadoBean empleado, AdmEmpleadoAccesoBean acceso, Usuario usuario) throws Exception;
    
    /**
     * Guardar nuevo empleado
     * @param empleado
     * @param acceso
     * @param usuario
     * @return
     * @throws Exception 
     */
    String nuevoAdmEmpleado(AdmEmpleadoBean empleado, AdmEmpleadoAccesoBean acceso, Usuario usuario) throws Exception;
    
    /**
     * Obtiene una persona de la tabla maestra "idtanirs" mediante su DNI
     * @param dni
     * @return
     * @throws Exception 
     */
    AdmEmpleadoBean getPersonaDesdeDni(String dni) throws Exception;
    
    /**
     * Obtiene un determinado empleado mediante su DNI
     * @param dni
     * @return
     * @throws Exception 
     */
    public AdmEmpleadoBean getAdmEmpleadoDesdeDni(String dni) throws Exception;
    
    
    /**
     * Realiza búsqueda de empleados con los siguientes criterios
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
     * Elimina acceso al sistema para un determinado empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    boolean deleteAcceso(AdmEmpleadoAccesoBean acceso) throws Exception;
    
    
    /**
     * Guarda nuevo Usuario con Acceso al sistema
     * @param acceso
     * @return
     * @throws Exception 
     */
    String saveNuevoAcceso(AdmEmpleadoAccesoBean acceso) throws Exception;
    
    
    /**
     * Restablece el acceso al sistema de un empleado
     * @param coUse     
     * @param acceso
     * @param empleado
     * @return
     * @throws Exception 
     */
    boolean restablecerAcceso(String coUse, AdmEmpleadoAccesoBean acceso, AdmEmpleadoBean empleado) throws Exception;
    
    String cambiarEstadoUsuario(String coEmpleado,String estado,String coUsuario);
}
