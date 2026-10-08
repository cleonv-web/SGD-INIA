/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocVistoBuenoBean;

/**
 *
 * @author ecueva
 */
public interface DocumentoVoBoService {
    List<DocumentoVoBoBean> getLsDocsVoBo(BuscarDocumentoVoBoBean bDocVoBo);
    DocumentoVoBoBean getDocumentoVoBo(String pnuAnn, String pnuEmi, String coDep, String coEmpVb);
    String goGrabarDocVoBo(TrxDocVistoBuenoBean trxDocVobo);
    String changeToEnviado(DocumentoVoBoBean docVobo, String prutaDoc, String nuDni, String nroRucInstitucion) throws Exception;
    String goChangeToSinVobo(DocumentoVoBoBean docVobo);
    String cargaDocEmi(DocumentoObjBean docObjBean);
    String getDeObsEmpVoBo(String nuAnn, String nuEmi, String coDep, String coEmp);
    String existeVistoBuenoPendienteDocAdm(String nuAnn, String nuEmi);
    String bloquearVoBoPersonal(String nuAnn,String nuEmi,String coDep,String coEmp,String coUserMod);
    String desBloquearVoBoPersonal(String nuAnn,String nuEmi);
}
