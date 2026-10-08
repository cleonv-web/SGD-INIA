/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.EstadoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.TemaBean;
import pe.gob.onpe.tramitedoc.dao.MensajesDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.dao.TemaDao;

/**
 *
 * @author oti2
 */
@Repository("temaDao")
public class TemaDaoImp extends SimpleJdbcDaoBase implements TemaDao{
         
    @Override
    public List<TemaBean> getListTema(TemaBean buscarTemaBean) {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT   co_tema,  de_tema FROM tdtr_tema WHERE ES_ELI='0' AND CO_DEPENDENCIA=? ");        
        List<TemaBean> list = new ArrayList<TemaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(TemaBean.class),
                    new Object[]{buscarTemaBean.getCoDependencia()});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
    
      @Override
    public String updTemaDestinos(String codigoEmision,String codTema) {
        String vReturn = "NO_OK";    
        StringBuffer sqlIns = new StringBuffer();   
        sqlIns.append("UPDATE TDTV_DESTINOS SET CO_TEMA ="+codTema+" WHERE NU_ANN||NU_EMI||NU_DES = '"+codigoEmision+"' "); 
        try{
            this.jdbcTemplate.update(sqlIns.toString() ); 
            vReturn = "OK";
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    @Override
    public String updTemaRemitos(String codigoEmision,String codTema) {
        String vReturn = "NO_OK";    
        StringBuffer sqlIns = new StringBuffer();   
        sqlIns.append("UPDATE TDTV_REMITOS SET CO_TEMA ="+codTema+" WHERE NU_ANN||NU_EMI = '"+codigoEmision+"'  "); 
        try{
            this.jdbcTemplate.update(sqlIns.toString() ); 
            vReturn = "OK";
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    @Override
    public String getEditTemaRemitos(String codigoEmision) {
        String vReturn = "NO_OK";    
        StringBuffer sqlIns = new StringBuffer();   
        sqlIns.append("SELECT CO_TEMA FROM  TDTV_REMITOS     WHERE NU_ANN||NU_EMI = '"+codigoEmision+"'  "); 
        try{ 
            String codTema = this.jdbcTemplate.queryForObject(sqlIns.toString(), String.class);    
            vReturn = codTema;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
     @Override
    public String getEditTemaDestinos(String codigoEmision) {
        String vReturn = "NO_OK";    
        StringBuffer sqlIns = new StringBuffer();   
        sqlIns.append("SELECT CO_TEMA FROM  TDTV_DESTINOS     WHERE NU_ANN||NU_EMI||NU_DES = '"+codigoEmision+"'  "); 
        try{ 
            String codTema = this.jdbcTemplate.queryForObject(sqlIns.toString(), String.class);    
            vReturn = codTema;
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
}
