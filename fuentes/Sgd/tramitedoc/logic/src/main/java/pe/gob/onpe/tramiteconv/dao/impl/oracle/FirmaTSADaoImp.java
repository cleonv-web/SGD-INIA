/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DestinoTSABean;
import pe.gob.onpe.tramitedoc.dao.FirmaTSADao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author ecueva
 */
@Repository("firmaTSADao")
public class FirmaTSADaoImp extends SimpleJdbcDaoBase implements FirmaTSADao{

    @Override
    public List<DestinoTSABean> getLsDestinoDoc(String nuAnn, String nuEmi) {
        StringBuilder sql = new StringBuilder();
        List<DestinoTSABean> list = new ArrayList<DestinoTSABean>();
        sql.append("SELECT NU_ANN, NU_EMI, NU_DES, TI_DES, NU_RUC_DES\n" +
                    "FROM TDTV_DESTINOS \n" +
                    "WHERE NU_ANN=? AND NU_EMI=?\n" +
                    "AND TI_DES='02' AND ES_ELI='0'");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinoTSABean.class),
                    new Object[]{nuAnn, nuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }
    
    @Override
    public String getCanEntidadTSA(String nuRuc, String tiDoc) {
        String vReturn="0";
        try {
            vReturn = this.jdbcTemplate.queryForObject("SELECT  COUNT(1)\n" +
                "FROM TDTX_TIMESTAMP_ENTIDAD\n" +
                "WHERE CENT_RUC=? AND CO_TIP_DOC=?\n" +
                "AND IN_ESTADO='1'", 
                    String.class, new Object[]{nuRuc, tiDoc});
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return vReturn;
    }    
    
}
