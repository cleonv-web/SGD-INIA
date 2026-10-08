/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaDocVoBoDao;
import pe.gob.onpe.tramitedoc.service.ConsultaDocVoBoService;

/**
 *
 * @author ecueva
 */
@Service("consultaDocVoBoService")
public class ConsultaDocVoBoServiceImp implements ConsultaDocVoBoService{

    private static Logger logger=Logger.getLogger("SGDocumentoVoBoService");

    @Autowired
    private ConsultaDocVoBoDao consultaDocVoBoDao;
    
    
    @Override
    public List<DocumentoVoBoBean> getLsDocsVoBo(BuscarDocumentoVoBoBean bDocVoBo){
        List<DocumentoVoBoBean> list = null;
        bDocVoBo.setAsunto(bDocVoBo.getAsunto().toUpperCase().trim());
        try {
            list = consultaDocVoBoDao.getLsDocsVoBo(bDocVoBo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public DocumentoVoBoBean getDocumentoVoBo(String pnuAnn, String pnuEmi, String coDep, String coEmpVb){
        DocumentoVoBoBean docVobo = null;
        try {
            docVobo = consultaDocVoBoDao.getDocumentoVoBo(pnuAnn, pnuEmi,coDep,coEmpVb);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return docVobo;
    }    
}
