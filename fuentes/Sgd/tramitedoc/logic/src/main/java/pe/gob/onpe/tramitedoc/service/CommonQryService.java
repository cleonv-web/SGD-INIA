/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.CargoFuncionBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;

/*interoperabilidad*/
import pe.gob.onpe.tramitedoc.bean.DatosInterBean;
import pe.gob.onpe.tramitedoc.bean.TablaMaestraBean;
/*interoperabilidad*/
/**
 *
 * @author ECueva
 */
public interface CommonQryService {
    DependenciaBean getDependenciaxCoDependencia(String coDepen); 
    List<RemitenteBean> getListRemitente(String coDepen);   
    String isExpedienteDuplicado(String pnuAnnExp, String pcoDepExp, String pcoGru, String pnuCorrExp);
    List<EmpleadoBean> getLsEmpleado(String coDepen);
    List<CargoFuncionBean> getLsEmpleo();
    List<DependenciaBean> getLsDepencia();
    List<SiElementoBean> getLsDepenciaMensjeria();
    List<EmpleadoBean> getLsEmpleadoIntitu(String deEmp);
    EmpleadoBean getEmpJefeDep(String coDep);
    List<EmpleadoBean> getLsEmpDepDesEmp(String deEmp,String coDep);
    List<EmpleadoVoBoBean> getLsPersonalVoBo(String nuAnn,String nuEmi);
    String obtenerValorParametro(String nombreParametro);
    String getCargoEmpleado(String coDep, String coEmp);
    
    List<TablaMaestraBean> getLsContenidoTabla(String vTabla);
    /*interoperabilidad*/
    DatosInterBean DatosInter(String nuAnn,String nuEmi);
    /*interoperabilidad*/
    
    //YUAL
      public List<CargoFuncionBean> getCargoBusqList(CargoFuncionBean cargo);
    
      public String insCargo(CargoFuncionBean requisito, String codusuario);
    //END YUAL
}
