/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoDetalleBean;
import pe.gob.onpe.tramitedoc.bean.PermisoBean;

/**
 *
 * @author ngilt
 */
public interface AdmPermisoService {
    public List<PermisoBean> getPermisoList(String codDependencia);
    
    public String updPermisoUsuario(PermisoBean permisoUsuario) throws Exception ;
    
}
