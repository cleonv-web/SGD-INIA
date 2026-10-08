/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.sql.Types;
import java.util.Date;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.dao.DocumentoTriggerDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author ecueva
 */
@Repository("documentoTriggerDao")
public class DocumentoTriggerDaoImp extends SimpleJdbcDaoBase implements DocumentoTriggerDao{

    public String actualizarEstadoAfterEmitirDocumento(String pnuAnn, String pnuEmi, String pcoDepEmi, Date pfeEmi) {
      String mensaje = "NO_OK"; 
      SimpleJdbcCall spActualizaEstado = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_ACTUALIZA_ESTA_U]")
            .withoutProcedureColumnMetaDataAccess()
            .useInParameterNames("p_nu_ann", "p_nu_emi","p_dep_ori", "p_fecha")
            .declareParameters(
                new SqlParameter("p_nu_ann", Types.VARCHAR),
                new SqlParameter("p_nu_emi", Types.VARCHAR),
                new SqlParameter("p_dep_ori", Types.VARCHAR),
                new SqlParameter("p_fecha", Types.DATE));
        SqlParameterSource in = new MapSqlParameterSource()       
                .addValue("p_nu_ann", pnuAnn)
                .addValue("p_nu_emi", pnuEmi)
                .addValue("p_dep_ori", pcoDepEmi)
                .addValue("p_fecha", pfeEmi);                        
       
       try{
           spActualizaEstado.execute(in);
           mensaje = "OK";
           spActualizaEstado=null;
       }catch(Exception ex){
           mensaje = "NO_OK";
           ex.printStackTrace();
       }
      return mensaje;  
    }
    
}
