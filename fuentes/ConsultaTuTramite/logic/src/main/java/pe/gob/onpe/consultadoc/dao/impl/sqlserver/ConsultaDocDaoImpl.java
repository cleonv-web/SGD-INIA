/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.consultadoc.dao.impl.sqlserver;

import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository; 
import pe.gob.onpe.consultadoc.bean.TipoDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocSeguimientoBean;
import pe.gob.onpe.consultadoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.consultadoc.dao.ConsultaDocDao;

/**
 *
 * @author RATAYAURI
 */
@Repository("consultaDocDao")
@Profile("sqlserver")
public class ConsultaDocDaoImpl extends SimpleJdbcDaoBase implements ConsultaDocDao{
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
    private SimpleJdbcCall spdataSourcesiga;
    
    @Override
    public List<ConsultaDocBean> getBuscarExpediente(String numExpediente) {                
               this.spdataSourcesiga = new SimpleJdbcCall(this.dataSource).withProcedureName("idosgd.pk_sgd_tramite_consulta_tu_expediente")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("pexpediente")                        
                .declareParameters(
                        new SqlParameter("pexpediente", Types.VARCHAR) )
                       .returningResultSet("recordSets", BeanPropertyRowMapper.newInstance(ConsultaDocBean.class));;
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pexpediente", numExpediente) ;        
        List<ConsultaDocBean> data = new ArrayList<ConsultaDocBean>(); 
        try {               
            Map<String, Object> dbResults = this.spdataSourcesiga.execute(in);
            data = (List<ConsultaDocBean>)dbResults.get("recordSets");
            this.spdataSourcesiga =null;            
        } catch (Exception ex) {
            data = null;
            ex.printStackTrace();
        }        
        return data;       
    }

    @Override
    public List<ConsultaDocSeguimientoBean> getBuscarExpedienteSeguimiento(String filtro) {
         this.spdataSourcesiga = new SimpleJdbcCall(this.dataSource).withProcedureName("idosgd.pk_sgd_tramite_consulta_tu_expediente_seguimiento")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("pfiltro")                        
                .declareParameters(
                        new SqlParameter("pfiltro", Types.VARCHAR) )
                       .returningResultSet("recordSets", BeanPropertyRowMapper.newInstance(ConsultaDocSeguimientoBean.class));;
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pfiltro", filtro) ;        
        List<ConsultaDocSeguimientoBean> data = new ArrayList<ConsultaDocSeguimientoBean>(); 
        try {               
            Map<String, Object> dbResults = this.spdataSourcesiga.execute(in);
            data = (List<ConsultaDocSeguimientoBean>)dbResults.get("recordSets");
            this.spdataSourcesiga =null;            
        } catch (Exception ex) {
            data = null;
            ex.printStackTrace();
        }        
        return data;       
    }
     
    
 
}
