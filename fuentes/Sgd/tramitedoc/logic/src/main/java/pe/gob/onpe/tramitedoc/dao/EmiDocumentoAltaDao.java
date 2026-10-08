/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;

/**
 *
 * @author WCutipa
 */
public interface EmiDocumentoAltaDao {
    String cargaDocumentoEmi(String coUsu,String pnuAnn,String pnuEmi,DocumentoFileBean pfileAnexo);
    
}
