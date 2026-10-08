/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBean;
import pe.gob.onpe.tramitedoc.dao.EtiquetaDocumentoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author NGilt
 */
@Repository("etiquetaDocumentoDao")
public class EtiquetaDocumentoDaoImp extends SimpleJdbcDaoBase implements EtiquetaDocumentoDao  {

    @Override
    public List<EtiquetaBean> getListEtiquetas() {
       StringBuffer sql = new StringBuffer();
        sql.append("select co_est,de_est from IDOSGD.tdtr_estados where de_tab = 'CO_ETIQUETA_REC'");
        List<EtiquetaBean> list = new ArrayList<EtiquetaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EtiquetaBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
}
