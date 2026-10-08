/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAltaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author WCutipa
 */
@Repository("emiDocumentoAltaDao")
public class EmiDocumentoAltaDaoImp extends SimpleJdbcDaoBase implements EmiDocumentoAltaDao{

    @Override
    public String cargaDocumentoEmi(String coUsu,String pnuAnn, String pnuEmi, DocumentoFileBean pfileAnexo) {
        String vReturn = "NO_OK";
        
        return vReturn;
    }
    
}
