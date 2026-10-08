/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.OtroOrigenBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaOtroOrigenDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoQuery;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

/**
 *
 * @author ECueva
 */
@Repository("ConsultaOtroOrigenDao")
public class ConsultaOtroOrigenDaoImp extends SimpleJdbcDaoQuery implements ConsultaOtroOrigenDao{

    @Autowired
    private ApplicationProperties applicationProperties;      
    
    @Override
    public List<OtroOrigenBean> getOtroOrigenBusqList(OtroOrigenBean otroOrigen) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        String sqlContains = "";
        sql.append("SELECT A.* FROM ( ");
        sql.append("	SELECT	co_otr_ori, ");
        sql.append("		nu_doc_otr_ori, ");
        sql.append("		co_tip_otr_ori, ");
        sql.append("		de_ape_pat_otr, ");
        sql.append("		de_ape_mat_otr, ");
        sql.append("		de_nom_otr, ");
        sql.append("		de_raz_soc_otr, ");
        sql.append("		de_dir_otro_ori, ");
        sql.append("		ti_origen, ");
        sql.append("		ref_co_otr_ori, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO](ub_dep)) no_dep, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_PROVINCIA](ub_dep, ub_pro)) no_pro, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DISTRITO](ub_dep, ub_pro, ub_dis)) no_dis, ");
        sql.append("		es_activo,  de_email,de_telefo, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY de_ape_pat_otr) AS ROWNUM ");
        sql.append("	FROM IDOSGD.tdtr_otro_origen  WITH (NOLOCK)  ");
        sql.append("	WHERE 1=1 ");
        if( otroOrigen.getDeRazSocOtr()!=null&&otroOrigen.getDeRazSocOtr().trim().length()>0) {
            sql.append(" AND CONTAINS(*, :pBusquedaTextual) ");
            sqlContains = "SELECT RETORNO FROM [IDOSGD].PK_SGD_DESCRIPCION_QUERYFILTER('"+otroOrigen.getDeRazSocOtr()+"')";
        }
        sql.append(") A ");
        sql.append("WHERE ROWNUM <= ").append(applicationProperties.getTopRegistrosConsultas());
        sql.append(" ORDER BY de_ape_pat_otr ");
        
        List<OtroOrigenBean> list = new ArrayList<OtroOrigenBean>();
        try {
            //Obteniendo el parametro textual si es requerido
            if (sqlContains.length() > 0) {
                String cadena = this.jdbcTemplate.queryForObject(sqlContains, String.class);            
                objectParam.put("pBusquedaTextual", cadena);
            }
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(OtroOrigenBean.class));            
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public ProcessResult<List<OtroOrigenBean>> getOtroOrigenBusqList(String busRazonSocial,FiltroPaginate paginate){
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        String sqlContains = "";
        sql.append("SELECT A.* FROM ( ");
        sql.append("	SELECT	co_otr_ori, ");
        sql.append("		nu_doc_otr_ori, ");
        sql.append("		co_tip_otr_ori, ");
        sql.append("		de_ape_pat_otr, ");
        sql.append("		de_ape_mat_otr, ");
        sql.append("		de_nom_otr, ");
        sql.append("		de_raz_soc_otr, ");
        sql.append("		de_dir_otro_ori, ");
        sql.append("		ti_origen, ");
        sql.append("		ref_co_otr_ori, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO](ub_dep)) no_dep, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_PROVINCIA](ub_dep, ub_pro)) no_pro, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DISTRITO](ub_dep, ub_pro, ub_dis)) no_dis, ");
        sql.append("		es_activo,  de_email,de_telefo, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY de_ape_pat_otr) AS fila, COUNT(1) OVER() AS filasTotal ");
        sql.append("	FROM IDOSGD.tdtr_otro_origen  WITH (NOLOCK)  ");
        sql.append("	WHERE 1=1 ");
        if( busRazonSocial!=null&&busRazonSocial.trim().length()>0) {
//            sql.append(" AND CONTAINS(*, :pBusquedaTextual) ");
//            sqlContains = "SELECT RETORNO FROM [IDOSGD].PK_SGD_DESCRIPCION_QUERYFILTER('"+otroOrigen.getDeRazSocOtr()+"')";
            sql.append(" AND UPPER(de_raz_soc_otr) LIKE '%' + UPPER(:pbusNroRuc) + '%' ");
            objectParam.put("pbusNroRuc", busRazonSocial);
            
        }
        sql.append(") A ");
        //sql.append("WHERE ROWNUM <= ").append(applicationProperties.getTopRegistrosConsultas());
        //sql.append(" ORDER BY de_ape_pat_otr ");
        sql.append(" WHERE fila BETWEEN " +paginate.getPaginaDesde()+" AND " +paginate.getPaginaHasta()+ " ");
        ProcessResult<List<OtroOrigenBean>> Result = new ProcessResult<List<OtroOrigenBean>>();
        List<OtroOrigenBean> list = new ArrayList<OtroOrigenBean>();
        try {
            //Obteniendo el parametro textual si es requerido
            if (sqlContains.length() > 0) {
                String cadena = this.jdbcTemplate.queryForObject(sqlContains, String.class);            
                objectParam.put("pBusquedaTextual", cadena);
            }
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(OtroOrigenBean.class));
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
