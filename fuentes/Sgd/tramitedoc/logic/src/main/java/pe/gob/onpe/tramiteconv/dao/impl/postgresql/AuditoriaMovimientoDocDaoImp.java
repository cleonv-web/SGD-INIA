/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import org.springframework.stereotype.Repository;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.AudiEstadosMovDocBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.dao.AuditoriaMovimientoDocDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author WCutipa
 */
@Repository("auditoriaMovimientoDocDao")
public class AuditoriaMovimientoDocDaoImp extends SimpleJdbcDaoBase implements AuditoriaMovimientoDocDao{
    
    @Override
    public String audiVisualizaDocumento(DocumentoObjBean docVisualiza,Usuario usu){
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO IDOSGD.TDTV_VISUALIZA_DOC(\n" +
                    "NU_ANN,\n" +
                    "NU_EMI,\n" +
                    "FE_DML,\n" +
                    "DE_USER,\n" +
                    "DE_IPPC,\n" +
                    "DE_NAMEPC,\n" +
                    "DE_USERPC\n" +
                    ") VALUES(?,?,CURRENT_TIMESTAMP,?,?,?,?)");
        try {
            int retorno = this.jdbcTemplate.update(sql.toString(), new Object[]{docVisualiza.getNuAnn(),docVisualiza.getNuEmi(),usu.getCoUsuario(),usu.getIpPC(),usu.getNombrePC(),usu.getUsuPc()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String audiEstadoDocumento(AudiEstadosMovDocBean audi){
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO IDOSGD.tdtv_estados_mov(\n" +
                    "NU_ANN,\n" +
                    "NU_EMI,\n" +
                    "NU_DES,\n" +
                    "FE_DML,\n" +
                    "TI_PROCESO,\n" +
                    "ES_PROCESO,\n" +
                    "DE_USER,\n" +
                    "DE_IPPC,\n" +
                    "DE_NAMEPC,\n" +                
                    "DE_USERPC\n" +
                    ") VALUES(?,?,CAST(? AS INT),CURRENT_TIMESTAMP,?,?,?,?,?,?)");
        try {
            int retorno = this.jdbcTemplate.update(sql.toString(), new Object[]{audi.getNuAnn(),audi.getNuEmi(),audi.getNuDes(),audi.getTiProceso(),audi.getEsProceso(),
                                            audi.getDeUser(),audi.getDeIpPc(),audi.getDeNamePc(),audi.getDeUserPc()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
}
