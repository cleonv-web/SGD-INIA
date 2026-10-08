/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

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
import pe.gob.onpe.tramitedoc.web.util.BusquedaTextual;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

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
        sql.append("SELECT co_otr_ori, nu_doc_otr_ori, co_tip_otr_ori, de_ape_pat_otr , ");
        sql.append("de_ape_mat_otr, de_nom_otr, de_raz_soc_otr, de_dir_otro_ori, ");
        sql.append("ti_origen ,ref_co_otr_ori, ");
        sql.append("PK_SGD_DESCRIPCION.fu_get_departamento (ub_dep) no_dep, ");
        sql.append("PK_SGD_DESCRIPCION.fu_get_provincia (ub_dep, ub_pro) no_pro, ");
        sql.append("PK_SGD_DESCRIPCION.fu_get_distrito (ub_dep,ub_pro,ub_dis) no_dis, es_activo,de_email,de_telefo FROM tdtr_otro_origen WHERE rownum <= ").append(applicationProperties.getTopRegistrosConsultas());
        
        if( otroOrigen.getDeRazSocOtr()!=null&&otroOrigen.getDeRazSocOtr().trim().length()>0) {
            //Se comenta la version orginal
            //sql.append(" and CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(otroOrigen.getDeRazSocOtr()) +"'\n" +
            //            ", 1 ) > 1\n" +
            //            "order by score(1) desc");
            //sql.append(" and CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(Utilidades.fn_getCleanTextLenGreathree(Utilidades.fn_getCleanTextExpReg(otroOrigen.getDeRazSocOtr()))) +"'\n" +
            //            ", 1 ) > 1\n");
            sql.append(" AND  UPPER(de_raz_soc_otr) like UPPER('%"+otroOrigen.getDeRazSocOtr()+"%') \n"); 
        }
        sql.append(" ORDER BY de_ape_pat_otr");
        List<OtroOrigenBean> list = new ArrayList<OtroOrigenBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(OtroOrigenBean.class),
                    new Object[]{});
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
        sql.append("SELECT TB.*, "
                + " PK_SGD_DESCRIPCION.fu_get_departamento (ub_dep) no_dep,\n"
                + " PK_SGD_DESCRIPCION.fu_get_provincia (ub_dep, ub_pro) no_pro,\n"
                + " PK_SGD_DESCRIPCION.fu_get_distrito (ub_dep,ub_pro,ub_dis) no_dis\n"
                + " FROM (" );
        sql.append("SELECT co_otr_ori, \n"
                + " nu_doc_otr_ori, co_tip_otr_ori, de_ape_pat_otr ,\n"
                + " de_ape_mat_otr, de_nom_otr, de_raz_soc_otr, de_dir_otro_ori,\n"
                + " ti_origen ,ref_co_otr_ori, ub_dep, ub_pro, ub_dis,\n"
                + " es_activo, de_email, de_telefo, ROWNUM AS fila, COUNT(ROWNUM) OVER()  AS filasTotal \n"
                + " FROM IDOSGD.tdtr_otro_origen WHERE 1=1");  //WHERE rownum <= ").append(applicationProperties.getTopRegistrosConsultas());
        
        if( busRazonSocial!=null&&busRazonSocial.trim().length()>0) {
            //Se comenta la version orginal
            //sql.append(" and CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(otroOrigen.getDeRazSocOtr()) +"'\n" +
            //            ", 1 ) > 1\n" +
            //            "order by score(1) desc");
            //sql.append(" and CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(Utilidades.fn_getCleanTextLenGreathree(Utilidades.fn_getCleanTextExpReg(otroOrigen.getDeRazSocOtr()))) +"'\n" +
            //            ", 1 ) > 1\n");
            sql.append(" AND  UPPER(de_raz_soc_otr) like UPPER('%'||:pbusNroRuc||'%') \n"); 
            objectParam.put("pbusNroRuc", busRazonSocial);
        }
        
        sql.append(" ORDER BY de_ape_pat_otr");
        sql.append(" ) TB ");
        sql.append("WHERE fila BETWEEN "+paginate.getPaginaDesde()+" AND "+paginate.getPaginaHasta()+ " ");
        ProcessResult<List<OtroOrigenBean>> Result = new ProcessResult<List<OtroOrigenBean>>();
        List<OtroOrigenBean> list = new ArrayList<OtroOrigenBean>();
        try {
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
