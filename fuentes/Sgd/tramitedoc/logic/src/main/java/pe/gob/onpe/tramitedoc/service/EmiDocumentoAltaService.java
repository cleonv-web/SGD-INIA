/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;

/**
 *
 * @author WCutipa
 */
public interface EmiDocumentoAltaService {

    String cargaDocumentoEmi(String coUsu,String pnuAnn,String pnuEmi,DocumentoFileBean pfileAnexo);
    public String changeToProyecto(DocumentoEmiBean documentoEmiBean);
}
