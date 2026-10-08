/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;

/**
 *
 * @author ecueva
 */
public interface ConsultaDocVoBoService {
    List<DocumentoVoBoBean> getLsDocsVoBo(BuscarDocumentoVoBoBean bDocVoBo);
    DocumentoVoBoBean getDocumentoVoBo(String pnuAnn, String pnuEmi, String coDep, String coEmpVb);
}
