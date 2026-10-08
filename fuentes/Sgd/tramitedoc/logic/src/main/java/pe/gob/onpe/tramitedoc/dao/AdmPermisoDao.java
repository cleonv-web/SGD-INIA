/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoDetalleBean;
import pe.gob.onpe.tramitedoc.bean.PermisoBean;

/**
 *
 * @author ngilt
 */
public interface AdmPermisoDao {
    public List<PermisoBean> getPermisoList(String codDependencia);
    public String eliPermiso(String codDep);
    public String insertarPermiso(PermisoBean permisoDetalle);
    
}
