/*
 * To change this template, choose Tools | Templates
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
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.dao.OtroOrigenDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.BusquedaTextual;

/**
 *
 * @author NGilt
 */
@Repository("otroOrigenDao")
public class OtroOrigenDaoImp extends SimpleJdbcDaoBase implements OtroOrigenDao {

    @Autowired
    private ApplicationProperties applicationProperties;  
        
    public List<OtroOrigenBean> getOtrosOrigenesList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select A.* from ( ");
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
        sql.append("		es_activo,de_email, de_telefo, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY de_raz_soc_otr) AS ROWNUM ");
        sql.append("	FROM IDOSGD.tdtr_otro_origen WITH (NOLOCK)  ");
        sql.append(") A ");
        sql.append("WHERE ROWNUM <= ").append(applicationProperties.getTopRegistrosConsultas());
        sql.append(" order by 7 ");
        
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

    public List<SiElementoBean> getTipoDocumentoList() {
        StringBuffer sql = new StringBuffer();
        sql.append("select  CELE_DESELE, ");
        sql.append("        CELE_CODELE "); 
        sql.append("from IDOSGD.SI_ELEMENTO "); 
        sql.append("WHERE CTAB_CODTAB='TIP_DOC_IDENT' ");
        sql.append("AND CELE_CODELE IN ('01','02','03','04','05','06','09') ");

        List<SiElementoBean> list = new ArrayList<SiElementoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(SiElementoBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
 public String insOtroOrigen(OtroOrigenBean otroOrigen) {
       String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("insert into IDOSGD.tdtr_otro_origen( ");
        sql.append("co_otr_ori, nu_doc_otr_ori, co_tip_otr_ori, de_ape_pat_otr , ");
        sql.append("de_ape_mat_otr, de_nom_otr, de_raz_soc_otr, de_dir_otro_ori, ");
        sql.append("ti_origen ,ref_co_otr_ori, ub_dep, ub_pro, ub_dis, es_activo, de_email, de_telefo ");
        sql.append(")values(?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?) ");
        try {
            String seq = "SELECT RIGHT(REPLICATE('0', 10) + CAST((ISNULL(MAX(CAST(co_otr_ori AS INT)), 0) + 1) AS VARCHAR), 10) FROM IDOSGD.tdtr_otro_origen  WITH (NOLOCK)  ";
            String id = (String) this.jdbcTemplate.queryForObject(seq, String.class);
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                id, otroOrigen.getNuDocOtrOri(), otroOrigen.getCoTipOtrOri(), otroOrigen.getDeApePatOtr(),
                otroOrigen.getDeApeMatOtr(), otroOrigen.getDeNomOtr(), otroOrigen.getDeRazSocOtr(), otroOrigen.getDeDirOtroOri(),
                otroOrigen.getTiOrigen(), otroOrigen.getRefCoOtrOri(), otroOrigen.getUbDep(), otroOrigen.getUbPro(), otroOrigen.getUbDis(), otroOrigen.getEsActivo(),
                otroOrigen.getDeEmail(), otroOrigen.getDeTelefo()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
 
    public OtroOrigenBean getOtroOrigen(String codOrigen) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  co_otr_ori, ");
        sql.append("        nu_doc_otr_ori, "); 
        sql.append("        co_tip_otr_ori, "); 
        sql.append("        de_ape_pat_otr, "); 
        sql.append("        de_ape_mat_otr, "); 
        sql.append("        de_nom_otr, ");
        sql.append("        de_raz_soc_otr, "); 
        sql.append("        de_dir_otro_ori, "); 
        sql.append("        ti_origen, ");
        sql.append("        ref_co_otr_ori, ");
        sql.append("        ub_dep,ub_pro, ");
        sql.append("        ub_dis,es_activo, ");
        sql.append("        (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO](ub_dep)) no_dep, ");
        sql.append("        (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_PROVINCIA](ub_dep, ub_pro)) no_pro, ");
        sql.append("        (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DISTRITO](ub_dep, ub_pro, ub_dis)) no_dis, ");
        sql.append("        de_email,de_telefo ");
        sql.append("FROM IDOSGD.tdtr_otro_origen WITH (NOLOCK)  ");
        sql.append("where co_otr_ori = ? ");

        OtroOrigenBean otroOrigen = new OtroOrigenBean();
        try {
            otroOrigen = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(OtroOrigenBean.class),
                    new Object[]{codOrigen});
        } catch (EmptyResultDataAccessException e) {
            otroOrigen = null;
        } catch (Exception e) {
            otroOrigen = null;
            e.printStackTrace();
        }
        return otroOrigen;
    }
      
    public String updOtroOrigen(OtroOrigenBean otroOrigen) {
         String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.tdtr_otro_origen set ");
        sql.append("nu_doc_otr_ori=?,co_tip_otr_ori=?,de_ape_pat_otr=?, ");
        sql.append("de_ape_mat_otr=?,de_nom_otr=?,de_raz_soc_otr=?,de_dir_otro_ori=?, ");
        sql.append("ub_dep=?,ub_pro=?,ub_dis=? ,de_email=?,de_telefo=?  where co_otr_ori=? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                otroOrigen.getNuDocOtrOri(), otroOrigen.getCoTipOtrOri(), otroOrigen.getDeApePatOtr(),
                otroOrigen.getDeApeMatOtr(), otroOrigen.getDeNomOtr(), otroOrigen.getDeRazSocOtr(), otroOrigen.getDeDirOtroOri(),
                otroOrigen.getUbDep(), otroOrigen.getUbPro(), otroOrigen.getUbDis(),otroOrigen.getDeEmail(), otroOrigen.getDeTelefo(),otroOrigen.getCoOtrOri()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    public List<OtroOrigenBean> getOtrosOrigenesPorTipo(String codTipoOtroOrigen) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT co_otr_ori, nu_doc_otr_ori, co_tip_otr_ori, de_ape_pat_otr , ");
        sql.append("de_ape_mat_otr, de_nom_otr, de_raz_soc_otr, de_dir_otro_ori, ");
        sql.append("ti_origen ,ref_co_otr_ori,");
        sql.append("es_activo FROM IDOSGD.tdtr_otro_origen ");
        sql.append("where co_tip_otr_ori = ? ");
        sql.append( "order by 7 ");
        List<OtroOrigenBean> list = new ArrayList<OtroOrigenBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(OtroOrigenBean.class),
                    new Object[]{codTipoOtroOrigen});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
}
