/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.MotivoDocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.util.Paginacion;

/**
 *
 * @author ECueva
 */

public interface DocDependenciaService {
//    List<DocumentoDependenciaBean> getAllDocXDependencia(String codDependencia, Paginacion paginacion); 
    List<DocumentoDependenciaBean> getDocDependenciaList(String codDependencia); 
    List<MotivoBean> getMotDependenciaList(String codDependencia, String codDoc); 
    String insDocDependencia(DocumentoDependenciaBean documentoDependenciaBean,String coUsuario);
    String insMotDocDependencia(MotivoDocumentoDependenciaBean MotDocDep);
    String updMotDocDependencia(MotivoDocumentoDependenciaBean MotDocDep,String codMotReempl);
    String eliDocDependencia(DocumentoDependenciaBean documentoDependenciaBean);
    String eliMotDocDependencia(MotivoDocumentoDependenciaBean MotDocDep);
    String updDocDependencia(DocumentoDependenciaBean documentoDependenciaBean,String codDocReempl,String coUsuario);
    List<DocumentoDependenciaBean> getDocDependenciaFaltantesList(String codDependencia); 
    List<MotivoBean> getMotFaltantesList(String codDependencia,String codDoc); 
    DocumentoDependenciaBean getDocDependencia(String codDependencia,String codTipDoc);
//    String updDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean);
//    String insDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean);
    List<DocumentoDependenciaBean> getAllDocXDependencia(String codDependencia);

    
}
