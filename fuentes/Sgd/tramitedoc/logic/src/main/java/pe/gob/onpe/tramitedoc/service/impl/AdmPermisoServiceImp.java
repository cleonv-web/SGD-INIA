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
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoDetalleBean;
import pe.gob.onpe.tramitedoc.bean.PermisoBean;
import pe.gob.onpe.tramitedoc.dao.AdmPermisoDao;
import pe.gob.onpe.tramitedoc.dao.GrupoDestinoDao;
import pe.gob.onpe.tramitedoc.service.AdmPermisoService;

/**
 *
 * @author kfrancia
 */
@Service("admPermisoService")
public class AdmPermisoServiceImp implements AdmPermisoService {

    @Autowired
    private AdmPermisoDao admPermisoDao;

    public List<PermisoBean> getPermisoList(String codDependencia) {
        List<PermisoBean> list = null;
        try {
            list = admPermisoDao.getPermisoList(codDependencia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updPermisoUsuario(PermisoBean permisoUsuario) throws Exception {
        String vReturn = "NO_OK";
        try {
            ArrayList<PermisoBean> permisoDetalles = permisoUsuario.getPermisoDetalle();

            vReturn = eliPermiso(permisoUsuario.getCoDep());
            if ("NO_OK".equals(vReturn)) {
                throw new validarDatoException("Error al eliminar permiso a los Usuarios");
            }
            
            for (PermisoBean permisoDetalle : permisoDetalles) {
                vReturn = admPermisoDao.insertarPermiso(permisoDetalle);
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al ingresar permiso al Usuario");
                }
            }
        } catch (validarDatoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR GRABAR PERMISO A USUARIO");
        }
        return vReturn;
    }
    
    public String eliPermiso(String codDep) {
        String vReturn = "NO_OK";
        try {
            vReturn = admPermisoDao.eliPermiso(codDep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
}
