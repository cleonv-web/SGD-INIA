/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.service.UsuarioService;
import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoCargoBean;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoAccesoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.dao.AdmEmpleadoDao;
import pe.gob.onpe.tramitedoc.service.AdmEmpleadoService;

/**
 *
 * @author GLuque
 */
@Service("admEmpleadoService")
public class AdmEmpleadoServiceImp implements AdmEmpleadoService{
    
    @Autowired
    public AdmEmpleadoDao admEmpleadoDao;
    
    @Autowired
    public UsuarioService usuarioService;    
    

    /**
     * Obtiene un determinado empleado para modificar sus datos
     * @return
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoBean getAdmEmpleado(String codEmp) throws Exception {
        AdmEmpleadoBean empleado = new AdmEmpleadoBean();
        try {
            empleado = admEmpleadoDao.getAdmEmpleado(codEmp);
        } catch (Exception e) {
            throw e;
        }
        return empleado;
    }
    
    /**
     * Actualiza datos personales de un empleado
     * @param empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    @Override
    public int updAdmEmpleado(AdmEmpleadoBean empleado, AdmEmpleadoAccesoBean acceso, Usuario usuario) throws Exception{
        int vReturn=0;
        try {
            vReturn=admEmpleadoDao.updAdmEmpleado(empleado,usuario);
             String vnuDni=Utility.getInstancia().cifrar(empleado.getDni(),ConstantesSec.SGD_SECRET_KEY_PASSWORD);
            if(acceso.getCoUsuario().length()>0){//Crear acceso si solo viene con USERNAME 
               
                admEmpleadoDao.saveNuevoAcceso(acceso.getCoUsuario(), acceso.getCoEmpleado(), vnuDni,acceso.getInAD());
                
            }
            admEmpleadoDao.updAcceso(acceso.getCoUsuario(), acceso.getCoEmpleado(), vnuDni,acceso.getInAD());
        } catch (Exception e) {
            throw e;
        }        
        return vReturn;
    }
    
    /**
     * Guardar nuevo empleado
     * @param empleado
     * @param acceso
     * @param usuario
     * @return
     * @throws Exception 
     */
    @Override
    public String nuevoAdmEmpleado(AdmEmpleadoBean empleado, AdmEmpleadoAccesoBean acceso, Usuario usuario) throws Exception{
        String vResult="NO_OK";
        try {
            empleado.setCoEmpleado(admEmpleadoDao.getNextCoEmpleado());
            if(empleado.getCoEmpleado()!=null&&empleado.getCoEmpleado().trim().length()>0){
                vResult=admEmpleadoDao.nuevoAdmEmpleado(empleado, usuario);
                if(vResult.equals("OK")){
                    // guardamos accesos
                    acceso.setCoEmpleado(empleado.getCoEmpleado());
                    String vnuDni=Utility.getInstancia().cifrar(empleado.getDni(),ConstantesSec.SGD_SECRET_KEY_PASSWORD);
                    admEmpleadoDao.saveNuevoAcceso(acceso.getCoUsuario(), acceso.getCoEmpleado(), vnuDni,acceso.getInAD());
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return vResult;
    }
    
    /**
     * Obtiene un empleado de la tabla maestra "idtanirs" mediante su DNI
     * @param dni
     * @return
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoBean getPersonaDesdeDni(String dni) throws Exception{
        try {
            return admEmpleadoDao.getPersonaDesdeDni(dni);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Obtiene un determinado empleado mediante su DNI
     * @param dni
     * @return 
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoBean getAdmEmpleadoDesdeDni(String dni) throws Exception{
        try {
            return admEmpleadoDao.getAdmEmpleadoDesdeDni(dni);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Realiza búsqueda de empleados con los criterios lineas abajo.
     * Nota: no todo los campos son requeridos.
     * @param dni
     * @param apPaterno
     * @param apMaterno
     * @param nombres
     * @return
     * @throws Exception 
     */
    @Override
    public List<AdmEmpleadoBean> getBsqAdmEmpleado(String dni, String apPaterno, String apMaterno, String nombres, String usuario) throws Exception{
        try {
            return admEmpleadoDao.getBsqAdmEmpleado(dni, apPaterno, apMaterno, nombres,usuario);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Obtiene una lista de dependencia,
     * realizando una búsqueda de acuerdo al parámetro "CRITERIO"
     * @return
     * @throws Exception 
     */
    @Override
    public List<DependenciaBean> getBsqDependencia(String criterio)throws Exception{
        try {
            return admEmpleadoDao.getBsqDependencia(criterio);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Obtiene lista de cargos de los empleados
     * @return
     * @throws Exception 
     */
    @Override
    public List<AdmEmpleadoCargoBean> getLsCargo() throws Exception{
        try {
            return admEmpleadoDao.getLsCargo();
        } catch (Exception e) {
            throw e;
        }
    }
    
    
    
    /**
     * Obtiene Objeto Acceso del Empleado
     * @param coEmpleado
     * @return
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoAccesoBean getAcceso(String coEmpleado) throws Exception{
        try {
            return admEmpleadoDao.getAcceso(coEmpleado);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Obtiene lista de Obj Acceso que coincidan con el NombreUsuario el cual se 
     * le asignará al empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    @Override
    public List<AdmEmpleadoAccesoBean> getLsAcceso(AdmEmpleadoAccesoBean acceso) throws Exception{
        try {
            return admEmpleadoDao.getLsAcceso(acceso);
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Elimina acceso al sistema para un determinado empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    @Override
    public boolean deleteAcceso(AdmEmpleadoAccesoBean acceso) throws Exception{
        try {
            acceso = admEmpleadoDao.getAcceso(acceso.getCoEmpleado());
            if(acceso!=null){
                String vResult = usuarioService.delUser(acceso.getCoUsuario());                
                if(vResult.equals("OK")){
                    return true;
                }
                else 
                {
                    return false;
                }
            }
            else {
               return false;
            }
        } catch (Exception e) {
            throw e;
        }        
    }
    
    /**
     * Guarda nuevo Usuario con Acceso al sistema
     * @param acceso
     * @return
     * @throws Exception 
     */
    @Override
    public String saveNuevoAcceso(AdmEmpleadoAccesoBean acceso) throws Exception{
        try {
            return admEmpleadoDao.saveNuevoAcceso(acceso.getCoUsuario(), acceso.getCoEmpleado(), null,acceso.getInAD());
        } catch (Exception e) {
            throw e;
        }
    }
    
    /**
     * Restablece el acceso al sistema de un empleado
     * @param coUse     
     * @param acceso
     * @param empleado
     * @return
     * @throws Exception 
     */
    @Override
    public boolean restablecerAcceso(String coUse, AdmEmpleadoAccesoBean acceso, AdmEmpleadoBean empleado) throws Exception{
        try {
            acceso = admEmpleadoDao.getAcceso(acceso.getCoEmpleado());
            if(acceso!=null){
                usuarioService.resetPassword(acceso.getCoUsuario(), empleado.getDni(), coUse);                
            }
            return true;
        } catch (Exception e) {
            throw e;
        }
    }
    
     public String cambiarEstadoUsuario(String coEmpleado,String estado,String coUsuario){
        String vResult = "NO_OK";
        try {             
                vResult = usuarioService.cambiarEstadoUsuario(coEmpleado,estado,coUsuario);                      
        } catch (Exception e) {
           e.printStackTrace();
        }
        return vResult;
    }
    
}

