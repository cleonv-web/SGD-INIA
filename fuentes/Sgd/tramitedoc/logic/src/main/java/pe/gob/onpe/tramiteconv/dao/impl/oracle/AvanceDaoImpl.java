/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.AvanceBean;
import pe.gob.onpe.tramitedoc.dao.AvanceDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author oti2
 */
@Repository("avanceDaoImpl")
public class AvanceDaoImpl extends SimpleJdbcDaoBase implements AvanceDao {
     @Override
    public List<AvanceBean> getListAvance(AvanceBean buscarAvanzadaBean) {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT     nu_ann,    nu_emi, nu_des ,  es_ultimo,    de_avance,    fe_avance,    ti_avance,    es_eli,    co_user\n" +
                   " FROM   tdtr_avance WHERE nu_ann=? and nu_emi=? and nu_des=? and ti_avance=? ORDER BY FE_AVANCE DESC ");        
        List<AvanceBean> list = new ArrayList<AvanceBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(AvanceBean.class),
                    new Object[]{buscarAvanzadaBean.getNuAnn(),buscarAvanzadaBean.getNuEmi()
                            ,buscarAvanzadaBean.getNuDes(),buscarAvanzadaBean.getTiAvance()});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (DataAccessException e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
    
     @Override
    public String insertAvance(AvanceBean buscarAvanzadaBean) {
        String vReturn = "NO_OK";    
        StringBuffer sqlIns = new StringBuffer();   
        StringBuffer sqlupda = new StringBuffer();  
        sqlupda.append("update TDTV_DESTINOS set DE_AVANCE = ?  where nu_ann||nu_emi||nu_des=? "); 
        sqlIns.append("INSERT INTO tdtr_avance (  nu_ann,  nu_emi,nu_des,   ti_avance,  es_ultimo,  de_avance,   fe_avance, es_eli,  co_user) \n" +
                       " VALUES ( ?,?,?,?,'1',?,sysdate,'0',?) "); 
        try{
            this.jdbcTemplate.update(sqlupda.toString(),new Object[]{buscarAvanzadaBean.getDeAvance(),
                buscarAvanzadaBean.getNuAnn()+buscarAvanzadaBean.getNuEmi()+ buscarAvanzadaBean.getNuDes() } ); 
            this.jdbcTemplate.update(sqlIns.toString(),new Object[]{
                buscarAvanzadaBean.getNuAnn(),buscarAvanzadaBean.getNuEmi(), buscarAvanzadaBean.getNuDes()
            , buscarAvanzadaBean.getTiAvance(),buscarAvanzadaBean.getDeAvance(),buscarAvanzadaBean.getCoUser()} ); 
            vReturn = "OK";
        }  catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
}
