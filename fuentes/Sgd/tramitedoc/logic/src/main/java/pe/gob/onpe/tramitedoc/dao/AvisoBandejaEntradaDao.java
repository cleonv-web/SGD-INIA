package pe.gob.onpe.tramitedoc.dao;

import java.util.List;

import pe.gob.onpe.tramitedoc.bean.AvisoBandejaEntradaBean;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBandejaEnBean;

public interface AvisoBandejaEntradaDao {

	List<AvisoBandejaEntradaBean> getBandejaEntradaAccesoTotal(String coDependencia,String coEmpleado,String bandeja);
        List<AvisoBandejaEntradaBean> getBandejaEntradaAltaDireccionTotal(String coDependencia,String coEmpleado);
        List<AvisoBandejaEntradaBean> getBandejaEntradaAccesoPersonal(String coDependencia,String coEmpleado,String bandeja);
        List<AvisoBandejaEntradaBean> getBandejaEntradaAccesoFuncionario(String coDependencia,String coEmpleado);
        List<EtiquetaBandejaEnBean> getListEtiquetaBandejaEntrada(String coDep, String coEmp);
}