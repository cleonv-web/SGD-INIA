/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.CargoFuncionBean;
import pe.gob.onpe.tramitedoc.bean.DatosInterBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.bean.TablaMaestraBean;
import pe.gob.onpe.tramitedoc.dao.CommonQueryDao;
import pe.gob.onpe.tramitedoc.service.CommonQryService;

/**
 *
 * @author ECueva
 */
@Service("commonQryService")
public class CommonQryServiceImp implements CommonQryService{

    @Autowired
    private CommonQueryDao commonQueryDao;
    
    @Override
    public DependenciaBean getDependenciaxCoDependencia(String coDepen) {
        DependenciaBean dependenciaBean = null;
        try {
            dependenciaBean = commonQueryDao.getDependenciaxCoDependencia(coDepen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dependenciaBean;
    }
    
    @Override
    public List<RemitenteBean> getListRemitente(String coDepen){
       List<RemitenteBean> list = null;
        try {
            list = commonQueryDao.getListRemitente(coDepen);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;
    }
    
    @Override
    public String isExpedienteDuplicado(String pnuAnnExp, String pcoDepExp, String pcoGru, String pnuCorrExp){
        String vResult="1";
        try {
            vResult = commonQueryDao.verificarExpedienteDuplicado(pnuAnnExp, pcoDepExp, pcoGru, pnuCorrExp);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vResult;        
    }
    
    @Override
    public List<EmpleadoBean> getLsEmpleado(String coDepen){
       List<EmpleadoBean> list = null;
        try {
            list = commonQueryDao.getLsEmpleado(coDepen);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;
    }    
    
    @Override
    public List<CargoFuncionBean> getLsEmpleo(){
       List<CargoFuncionBean> list = null;
        try {
            list = commonQueryDao.getLsEmpleo();
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;
    }
    
    @Override
    public List<DependenciaBean> getLsDepencia(){
       List<DependenciaBean> list = null;
        try {
            list = commonQueryDao.getLsDepencia();
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;        
    }
    
     @Override
    public List<SiElementoBean> getLsDepenciaMensjeria(){
       List<SiElementoBean> list = null;
        try {
            list = commonQueryDao.getLsDepenciaMensjeria();
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;        
    }
    
    @Override
    public List<EmpleadoBean> getLsEmpleadoIntitu(String deEmp){
       List<EmpleadoBean> list = null;
        try {
            list = commonQueryDao.getLsEmpleadoIntitu(deEmp);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;
    }
    
    @Override
    public EmpleadoBean getEmpJefeDep(String coDep) {
        EmpleadoBean empleadoBean = null;
        try {
            empleadoBean = commonQueryDao.getEmpJefeDep(coDep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleadoBean;
    }    
    
    @Override
    public List<EmpleadoBean> getLsEmpDepDesEmp(String deEmp,String coDep){
       List<EmpleadoBean> list = null;
        try {
            list = commonQueryDao.getLsEmpDepDesEmp(deEmp,coDep);
        } catch (Exception e) {
            e.printStackTrace();
        }
       return list;
    }
    
    @Override
    public List<EmpleadoVoBoBean> getLsPersonalVoBo(String nuAnn,String nuEmi){
        List<EmpleadoVoBoBean> list = null;
        try {
            list = commonQueryDao.getLsPersonalVoBo(nuAnn,nuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }     
    
     
    @Override
    public String obtenerValorParametro(String nombreParametro){
        return commonQueryDao.obtenerValorParametro(nombreParametro);
    }
    
    @Override
    public String getCargoEmpleado(String coDep, String coEmp) {
        String cargoEmpleadoAux = commonQueryDao.getCargoEmpleado(coDep, coEmp);
        String cargoEmpleado = "";
        if(this.hasParam(cargoEmpleadoAux)){
            cargoEmpleado = cargoEmpleadoAux;
        }
        return cargoEmpleado;
    }
    
    private boolean hasParam(String string1){
        return string1!=null&&string1.trim().length()>0;
    }
    
    /*interoperabilidad*/
    @Override
    public DatosInterBean DatosInter(String nuAnn, String nuEmi) {
        DatosInterBean list = null;
        try {
            list = commonQueryDao.DatosInter(nuAnn,nuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }    
    /*interoperabilidad*/


       @Override
    public List<TablaMaestraBean> getLsContenidoTabla(String vTabla){
        List<TablaMaestraBean> list = null;
        try {
            list = commonQueryDao.getLsContenidoTabla(vTabla);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }    
    
    
    
    //YUAL
    
    
    @Override
    public List<CargoFuncionBean> getCargoBusqList(CargoFuncionBean cargo) {
        List<CargoFuncionBean> list = null;
        try {
            list = commonQueryDao.getCargoBusqList(cargo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

   @Override
    public String insCargo(CargoFuncionBean cargo, String codusuario) {
        String vReturn = "NO_OK";
        try {
            vReturn = commonQueryDao.insCargo(cargo, codusuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    //END YUAL
}
