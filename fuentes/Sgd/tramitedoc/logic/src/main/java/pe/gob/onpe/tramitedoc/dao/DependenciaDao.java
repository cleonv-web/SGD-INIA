/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.LocalDepBean;


/**
 *
 * @author ECueva
 */
public interface DependenciaDao {
    List<DependenciaBean> getAllDependencia(boolean esAdmin,String codDependencia);
    DependenciaBean getDependencia(String coDep);
    List<DependenciaBean> getDependenciaHijo(String coDepPadre);
    List<DependenciaBean> getBuscaDependencia(String busDep,String busTipo);
    String updDependencia(DependenciaBean dep,String coDepAn, String pTipoAnt);
    String insDependencia(DependenciaBean dep);
    String insLocalDependencia(LocalDepBean localDep);
    LocalDepBean getLocalDepBean(String coDep);
    String delLocalDependencia(String coDep);
    String insEmpDependencia(String coEmp,String coDep);
    String delEmpDependencia(String coDep);
    List<EmpleadoBean> getLsEmpDepen(String coDep);
}
