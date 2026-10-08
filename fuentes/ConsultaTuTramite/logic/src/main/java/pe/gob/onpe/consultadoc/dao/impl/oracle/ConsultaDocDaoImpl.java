/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.consultadoc.dao.impl.oracle;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository; 
import pe.gob.onpe.consultadoc.bean.TipoDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.consultadoc.dao.ConsultaDocDao;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import pe.gob.onpe.consultadoc.bean.ConsultaDocSeguimientoBean;
/**
 *
 * @author  WCONDORI
 */
@Repository("consultaDocDao")
@Profile("oracle")
public class ConsultaDocDaoImpl extends SimpleJdbcDaoBase implements ConsultaDocDao{
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
    private SimpleJdbcCall spdataSource;

    @Override
    public List<ConsultaDocBean> getBuscarExpediente(String numExpediente) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public List<ConsultaDocSeguimientoBean> getBuscarExpedienteSeguimiento(String filtro) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
     
    
    
    
    
}
