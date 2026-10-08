/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.MotivoDocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.util.Paginacion;

/**
 *
 * @author ECueva
 */
public interface DocDependenciaDao {
//        List<DocumentoDependenciaBean> getAllDocXDependencia(String codDependencia, Paginacion paginacion);
        //List<DocumentoDependenciaBean> getAllDocXDependencia(String codDependencia, Paginacion paginacion);
        DocumentoDependenciaBean getDocDependencia(String codDependencia,String codTipDoc);
        int getRowCountDocXDependencia(String codDependencia);
//        String updDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean);
//        String insDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean);
        String insDocDependencia(DocumentoDependenciaBean documentoDependenciaBean);
        String insMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep);
        String updMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep,String codMotReempl);
        String eliDocDependencia(DocumentoDependenciaBean documentoDependenciaBean);
        String eliMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep);
        String updDocDependencia(DocumentoDependenciaBean documentoDependenciaBean,String codDocReempl);
        List<DocumentoDependenciaBean> getDocDependenciaList(String codDependencia);
        List<MotivoBean> getMotDependenciaList(String codDependencia, String codDoc);
        List<DocumentoDependenciaBean> getDocDependenciaFaltantesList(String codDependencia);
        List<MotivoBean> getMotFaltantesList(String codDependencia,String codDoc);
}
