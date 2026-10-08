/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaProveedorDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoQuery;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

/**
 *
 * @author ECueva
 */
@Repository("consultaProveedorDao")
public class ConsultaProveedorDaoImp extends SimpleJdbcDaoQuery implements ConsultaProveedorDao{
    
    @Autowired
    private ApplicationProperties applicationProperties;        
    
    @Override
   public ProcessResult<List<ProveedorBean>> getProveedoresBusqList(String busNroRuc, String busRazonSocial,FiltroPaginate paginate) {
        StringBuilder sql = new StringBuilder();
        Map<String, Object> objectParam = new HashMap<String, Object>();
          
         sql.append("SELECT TB.*,"
                + " IDOSGD.PK_SGD_DESCRIPCION_fu_get_departamento (cubi_coddep) no_dep,\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_fu_get_provincia (cubi_coddep, cubi_codpro) no_prv,\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_fu_get_distrito (cubi_coddep,cubi_codpro,cubi_coddis) no_dis \n"
                + " FROM (");
        sql.append("SELECT cpro_ruc nuRuc, \n"
                + " to_char(dpro_fecins,'dd/mm/yyyy'), \n"
                + " cpro_razsoc descripcion, \n"
                + " cpro_domicil, \n"
                + " cubi_coddep,\n"
                + " cubi_codpro, \n"
                + " cubi_coddis,\n"
               
                + " cpro_telefo,\n"
                + " cpro_email,row_number() OVER (ORDER BY 1) AS   fila, COUNT(1) OVER()  AS filasTotal  \n"
                + " FROM IDOSGD.lg_pro_proveedor WHERE 1=1 ");
        
        
        if (busNroRuc!=null&&busNroRuc.trim().length()>0){
            sql.append(" and cpro_ruc like '%'||:pnroRuc||'%' ");
            objectParam.put("pnroRuc", busNroRuc);
            
        }       
        if( busRazonSocial!=null && !busRazonSocial.equals("")) { 
            sql.append(" and cpro_razsoc like '%'||:prazSoc||'%' ");
            objectParam.put("prazSoc", busRazonSocial); 
        }
         sql.append(" ) TB ");
        sql.append("WHERE fila BETWEEN "+paginate.getPaginaDesde()+" AND "+paginate.getPaginaHasta()+"  ");
        ProcessResult<List<ProveedorBean>> Result = new ProcessResult<List<ProveedorBean>>();
        List<ProveedorBean> list = new ArrayList<ProveedorBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(ProveedorBean.class));
        Result.setResult(list);
            Result.setIsSuccess(true);
        } catch (EmptyResultDataAccessException e) {
            Result.setIsSuccess(false);
            Result.setMessage(e.getMessage());
       } catch (Exception e) {
            Result.setIsSuccess(false);
            Result.setMessage(e.getMessage());
           e.printStackTrace();
       }
        return Result;
    }
}
