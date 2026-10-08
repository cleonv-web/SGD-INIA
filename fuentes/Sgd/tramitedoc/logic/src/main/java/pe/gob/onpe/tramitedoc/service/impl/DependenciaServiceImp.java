/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.LocalDepBean;
import pe.gob.onpe.tramitedoc.bean.TrxDependenciaBean;
import pe.gob.onpe.tramitedoc.dao.CommonQueryDao;
import pe.gob.onpe.tramitedoc.dao.DependenciaDao;
import pe.gob.onpe.tramitedoc.service.DependenciaService;

/**
 *
 * @author ECueva
 */
@Service("dependenciaService")
public class DependenciaServiceImp implements DependenciaService{

    @Autowired
    private DependenciaDao dependenciaDao;
    
    @Autowired
    private CommonQueryDao commonQueryDao;    
    
    @Override
    public List<DependenciaBean> getAllDependencia(boolean esAdmin, String codDependencia) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        List<DependenciaBean> list = null;
        try{
            list = dependenciaDao.getAllDependencia(esAdmin,codDependencia);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;
    }

    @Override
    public List<DependenciaBean> getDependenciaHijo(String coDepPadre) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        List<DependenciaBean> list = null;
        try{
           list = dependenciaDao.getDependenciaHijo(coDepPadre); 
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;
    }
    
    @Override
    public List<DependenciaBean> getBuscaDependencia(String busDep, String busTipo) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        List<DependenciaBean> list = null;
        try{
           list = dependenciaDao.getBuscaDependencia(busDep,busTipo); 
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;
    }
    
    @Override
    public DependenciaBean getDependencia(String coDep){
        DependenciaBean dependenciaBean=null;
        try {
            dependenciaBean=dependenciaDao.getDependencia(coDep); 
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dependenciaBean;
    }
    
//    @Override
//    public String grabaDependenciaBean(DependenciaBean dep,String accionBd){
//        String vReturn = "NO_OK";
//        try {
//            //verificar si se trata de insert o update;
//            if(accionBd.equals("INS")){
//                vReturn=dependenciaDao.insDependencia(dep);
//            }else if(accionBd.equals("UPD")){
//                vReturn=dependenciaDao.updDependencia(dep);
//            }else{
//                vReturn="Operación no permitida.";
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return vReturn;
//    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String insDependenciaBean(TrxDependenciaBean trxDep,LocalDepBean localDep) throws Exception {
        String vReturn = "NO_OK";
        String[] vReturnAux=null;
        try {
            DependenciaBean dep = trxDep.getDependencia();
            dep.setIdAct(trxDep.getCoUseMod());
            vReturn=dependenciaDao.insDependencia(dep);
            if(vReturn.equals("ERROR")){
                vReturn="ERROR AL CREAR DEPENDENCIA.";
                throw new validarDatoException(vReturn);
            }else if(vReturn.equals("OK")){
                if(dep.getTiDependencia().equals("1")){
                    trxDep.setCoDep(dep.getCoDependencia());
                }
                vReturnAux=commonQueryDao.getNroCorrLocalDependencia();
                if(vReturnAux!=null&&vReturnAux[0].equals("OK")){
                    localDep.setNuCorr(vReturnAux[1]);
                    localDep.setCoDep(dep.getCoDependencia());
                    localDep.setCoUseMod(dep.getIdAct());
                    vReturn=dependenciaDao.insLocalDependencia(localDep);   
                    if(vReturn.equals("OK")){
                        String coDep=trxDep.getCoDep();
                        ArrayList<EmpleadoBean> lstIntegrante = trxDep.getLstIntegrante();
                        for (EmpleadoBean emp : lstIntegrante) {
                            vReturn=dependenciaDao.insEmpDependencia(emp.getCempCodemp(), coDep);
                            if(!vReturn.equals("OK")){
                                vReturn="ERROR AL AGREGAR EMPLEADO DEPENDENCIA.";
                                throw new validarDatoException(vReturn);                               
                            }
                        }    
                    }else{
                        vReturn="ERROR AL INSERTAR LOCAL DEPENDENCIA.";
                        throw new validarDatoException(vReturn);                        
                    }
                }else{
                    vReturn="ERROR AL OBTENER NUMERO CORRELATIVO LOCAL DEPENDENCIA.";
                    throw new validarDatoException(vReturn);
                }
            }else{
                throw new validarDatoException(vReturn);
            }
        } catch (validarDatoException e) {
           throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR EN TRANSACCION NUEVA DEPENDENCIA.");
        }
        return vReturn;        
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updDependenciaBean(TrxDependenciaBean trxDep,String coDepAn,LocalDepBean localDep, String pTipoAnt) throws Exception {
        String vReturn = "NO_OK";
        String[] vReturnAux=null;
        try {
            DependenciaBean dep = trxDep.getDependencia();
            if(dep!=null){
                dep.setIdAct(trxDep.getCoUseMod());
                if(coDepAn.equals(dep.getCoDependencia())){ //CODIGO IGUAL NO HAY CAMBIO DE "coDependencia"
                    vReturn=dependenciaDao.updDependencia(dep,coDepAn,pTipoAnt);// Retorna ID de dependencia
                    if(vReturn.equals("ERROR")){
                        vReturn="ERROR AL ACTUALIZAR DEPENDENCIA.";
                        throw new validarDatoException(vReturn);
                    }else if(vReturn.equals("DUPLICADO")){
                        throw new validarDatoException("Código Dependencia Duplicado.");
                    //}else if(vReturn.equals("OK")){
                    }else{ // OK Y RETORNA coDependencia
                        //Actualizamos "coDependencia" ya que puede que haya cambiado con la actualización
                        dep.setCoDependencia(vReturn);
                        trxDep.setCoDep(vReturn);
                        vReturn=dependenciaDao.delLocalDependencia(coDepAn); //Elimina local de dependencia "coDependencia" Anterior
                        if(vReturn.equals("OK")){
                            vReturn=dependenciaDao.delLocalDependencia(dep.getCoDependencia()); //Elimina local de dependencia "coDependencia" Actual
                            if(vReturn.equals("OK")){
                                vReturnAux=commonQueryDao.getNroCorrLocalDependencia();
                                if(vReturnAux!=null&&vReturnAux[0].equals("OK")){                        
                                    localDep.setNuCorr(vReturnAux[1]);
                                    localDep.setCoDep(dep.getCoDependencia());
                                    vReturn=dependenciaDao.insLocalDependencia(localDep);                    
                                    if(!vReturn.equals("OK")){
                                        vReturn="ERROR AL INSERTAR LOCAL DEPENDENCIA.";
                                        throw new validarDatoException(vReturn);
                                    }                            
                                }else{
                                    vReturn="ERROR AL OBTENER NUMERO CORRELATIVO LOCAL DEPENDENCIA.";
                                    throw new validarDatoException(vReturn);
                                }
                            }else{
                                vReturn="ERROR AL BORRAR LOCAL DEPENDENCIA.";
                                throw new validarDatoException(vReturn);                      
                            }                    
                        }else{
                            vReturn="ERROR AL BORRAR LOCAL DEPENDENCIA.";
                            throw new validarDatoException(vReturn);
                        }
                    }
                }else{ //SE MODIFICÓ EL CODIGO "coDependencia"
                    
                    vReturn=dependenciaDao.delLocalDependencia(coDepAn);
                    if(vReturn.equals("OK")){
                        vReturn=dependenciaDao.updDependencia(dep,coDepAn,pTipoAnt);// Retorna ID de dependencia
                        if(vReturn.equals("ERROR")){
                            vReturn="ERROR AL ACTUALIZAR DEPENDENCIA.ZZ";
                            throw new validarDatoException(vReturn);
                        }else if(vReturn.equals("DUPLICADO")){
                            throw new validarDatoException("Código Dependencia Duplicado.");
                        }else{ // OK Y RETORNA coDependencia
                            dep.setCoDependencia(vReturn);// Asignamos el nuevo "coDependencia"
                            trxDep.setCoDep(vReturn);
                            vReturnAux=commonQueryDao.getNroCorrLocalDependencia();
                            if(vReturnAux!=null&&vReturnAux[0].equals("OK")){                        
                                localDep.setNuCorr(vReturnAux[1]);
                                localDep.setCoDep(dep.getCoDependencia());
                                vReturn=dependenciaDao.insLocalDependencia(localDep);                    
                                if(!vReturn.equals("OK")){
                                    vReturn="ERROR AL INSERTAR LOCAL DEPENDENCIA.";
                                    throw new validarDatoException(vReturn);
                                }                            
                            }else{
                                vReturn="ERROR AL OBTENER NUMERO CORRELATIVO LOCAL DEPENDENCIA.";
                                throw new validarDatoException(vReturn);
                            }
                        }
                    }else{
                        vReturn="ERROR AL BORRAR LOCAL DEPENDENCIA.";
                        throw new validarDatoException(vReturn);
                    }
                }
            }
            String coDep=trxDep.getCoDep();
            ArrayList<EmpleadoBean> lstIntegrante = trxDep.getLstIntegrante();
            if(lstIntegrante!=null&&lstIntegrante.size()>0){
                vReturn=dependenciaDao.delEmpDependencia(coDep);
                if(vReturn.equals("OK")){
                    for (EmpleadoBean emp : lstIntegrante) {
                        vReturn=dependenciaDao.insEmpDependencia(emp.getCempCodemp(), coDep);
                        if(!vReturn.equals("OK")){
                            vReturn="ERROR AL AGREGAR EMPLEADO DEPENDENCIA.";
                            throw new validarDatoException(vReturn);                               
                        }
                    }    
                }else{
                    vReturn="ERROR AL BORRAR EMPLEADO DEPENDENCIA.";
                    throw new validarDatoException(vReturn);                      
                }
            }
        } catch (validarDatoException e) {
           throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR EN TRANSACCION ACTUALIZAR DEPENDENCIA.");
        }
        return vReturn;   
    }
    
    @Override
    public String getCoLocal(String coDep){
        String pCoLoc=null;
        try {
            LocalDepBean localDep=dependenciaDao.getLocalDepBean(coDep);
            if(localDep!=null){
                pCoLoc=localDep.getCoLoc();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pCoLoc;
    }
    
    @Override
    public List<EmpleadoBean> getLsEmpDepen(String coDep) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        List<EmpleadoBean> list = null;
        try{
           list = dependenciaDao.getLsEmpDepen(coDep); 
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;
    }    
}
