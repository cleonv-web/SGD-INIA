/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;

/**
 *
 * @author ecueva
 */
public interface DocumentoVoBoDao {
    List<DocumentoVoBoBean> getLsDocsVoBo(BuscarDocumentoVoBoBean bDocVoBo);
    DocumentoVoBoBean getDocumentoVoBo(String pnuAnn, String pnuEmi, String coDep, String coEmpVb);
    String updObsVistoBueno(String nuAnn,String nuEmi,String coDep,String coEmp,String obs,String coUserMod, String esDocVb);
    String updToEnviadoVistoBueno(String nuAnn,String nuEmi,String coDep,String coEmp,String esDocVobo,String coUserMod);
    String updToSinVistoBueno(String nuAnn,String nuEmi,String coDep,String coEmp,String esDocVobo,String coUserMod);
    String getDeObsEmpVoBo(String nuAnn, String nuEmi, String coDep, String coEmp);
    String existeVistoBuenoDocAdm(String nuAnn, String nuEmi);
    String existeVistoBuenoPendienteDocAdm(String nuAnn, String nuEmi);
    String existePersonalVb(String nuAnn, String nuEmi, String coDep, String coEmpVb);
    String updVistoBuenoPersonal(String nuAnn,String nuEmi,String coDep,String coEmp,String esDocVobo,String coUserMod);
//    String updVistoBuenoPersonal(String nuAnn,String nuEmi,String esDocVobo,String coEmp);
    String updVistoBuenoPersonal(String nuAnn,String nuEmi);
    String getEsVobo(String nuAnn, String nuEmi, String coDep, String coEmpVb);
}
