/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.AudiEstadosMovDocBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;

/**
 *
 * @author WCutipa
 */
public interface AuditoriaMovimientoDocDao {
    
String audiVisualizaDocumento(DocumentoObjBean docVisualiza,Usuario usu);
String audiEstadoDocumento(AudiEstadosMovDocBean audi);
}
