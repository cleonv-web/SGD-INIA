/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import pe.gob.onpe.autentica.model.Usuario;

/**
 *
 * @author ecueva
 */
public interface AuditoriaMovimientoDocService {
     String audiEstadoDocumentoRemito(Usuario currentUser,String nuAnn, String nuEmi, String esDoc);
     String audiEstadoDocumentoDestino(Usuario currentUser,String nuAnn, String nuEmi, String nuDes, String esDoc);
}
