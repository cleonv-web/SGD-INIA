/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.LocalDepBean;
import pe.gob.onpe.tramitedoc.bean.TrxDependenciaBean;

/**
 *
 * @author ECueva
 */
public interface DependenciaService {
    List<DependenciaBean> getAllDependencia(boolean esAdmin,String codDependencia);
    List<DependenciaBean> getDependenciaHijo(String coDepPadre);
    List<DependenciaBean> getBuscaDependencia(String busDep, String busTipo);
    DependenciaBean getDependencia(String coDep);
//    String grabaDependenciaBean(DependenciaBean dep,String accionBd) throws Exception;
    String insDependenciaBean(TrxDependenciaBean trxDep,LocalDepBean localDep) throws Exception;
    String updDependenciaBean(TrxDependenciaBean trxDep,String coDepAn,LocalDepBean localDep,String pTipoAnt) throws Exception;
    String getCoLocal(String coDep);
    List<EmpleadoBean> getLsEmpDepen(String coDep);
}
