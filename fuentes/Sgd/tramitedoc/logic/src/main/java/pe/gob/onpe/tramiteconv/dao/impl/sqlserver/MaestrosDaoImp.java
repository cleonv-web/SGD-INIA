/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import pe.gob.onpe.tramitedoc.dao.MaestrosDao;
import java.util.ArrayList;
import java.util.List;
import pe.gob.onpe.tramitedoc.bean.AnnioBean;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DepartamentoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioBean;
import pe.gob.onpe.tramitedoc.bean.DistritoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.EstadoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinatarioBean;
import pe.gob.onpe.tramitedoc.bean.LocalBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.OrigenDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.PrioridadDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ProvinciaBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.bean.TipoDestinatarioEmiBean;
import pe.gob.onpe.tramitedoc.bean.TipoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.TipoExpedienteEmiBean;
import pe.gob.onpe.tramitedoc.bean.TupaExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.VencimientoBean;

/**
 *
 * @author ECueva
 */
@Repository("maestrosDao")
public class MaestrosDaoImp extends SimpleJdbcDaoBase implements MaestrosDao{

    @Override
        public List<AnnioBean> listAnnioEjec() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  CANO_ANOEJE CANO, "); 
        sql.append("        CANO_ANOEJE DE_ANIO ");
        sql.append("FROM IDOSGD.SI_MAE_ANO_EJECUCION "); 
        sql.append("UNION "); 
        sql.append("SELECT  '.:TODOS.:', "); 
        sql.append("        NULL ");  
        sql.append("ORDER BY 1 DESC ");
        
        List<AnnioBean> list = new ArrayList<AnnioBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(AnnioBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
    
    @Override
        public List<EstadoDocumentoBean> listEstadosDocumento(String nomTabla) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_EST, CO_EST "); 
        sql.append("FROM IDOSGD.TDTR_ESTADOS "); 
        sql.append("WHERE DE_TAB = ? "); 
        sql.append("UNION "); 
        sql.append("SELECT '.:TODOS.:', NULL "); 
        sql.append("ORDER BY CO_EST ");
        
        List<EstadoDocumentoBean> list = new ArrayList<EstadoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(EstadoDocumentoBean.class),
                    new Object[]{nomTabla});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
    
     @Override
    public List<EstadoDocumentoBean> listEstMensajeria(String nomTabla) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT '[TODOS]' DE_EST,NULL CO_EST  ");
        sql.append("UNION ");
        sql.append("SELECT DE_EST,CO_EST ");
        sql.append("FROM IDOSGD.TDTR_ESTADOS ");
        sql.append("WHERE DE_TAB = ? ");
                        
        List<EstadoDocumentoBean> list = new ArrayList<EstadoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(EstadoDocumentoBean.class),
                    new Object[]{nomTabla});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    } 
        
    @Override
        public List<PrioridadDocumentoBean> listPrioridadDocumento() {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT DE_PRI, CO_PRI ");
        sql.append("FROM IDOSGD.TDTR_PRIORIDAD ");
        sql.append("UNION ");
        sql.append("SELECT '.:TODOS.:',NULL ");
        
        List<PrioridadDocumentoBean> list = new ArrayList<PrioridadDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(PrioridadDocumentoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }    
    
    @Override
        public List<TipoDocumentoBean> listTipoDocumento(String coDependencia) {
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT DE_TIP_DOC, CO_TIP_DOC ");
        sql.append("FROM "
                + "(\n" +
                    "SELECT DISTINCT\n" +
                    "A.CO_DEP CO_DEP_DES ,CO_DOC CO_TIP_DOC , B.CDOC_DESDOC DE_TIP_DOC\n" +
                    "from IDOSGD.tdtx_resumen_doc A WITH (NOLOCK)  , IDOSGD.SI_MAE_TIPO_DOC B WITH (NOLOCK) \n" +
                    "WHERE ti_tab = 'D'\n" +
                    "AND A.CO_DOC = B.CDOC_TIPDOC) Q1");        
        sql.append(" WHERE CO_DEP_DES = ? "); 
        sql.append("UNION "); 
        sql.append("SELECT '.:TODOS:.' DESCRI, NULL CODIGO ORDER BY 1 "); 
        
        List<TipoDocumentoBean> list = new ArrayList<TipoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class), new Object[]{coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
     @Override
        public List<RemitenteBean> getFirmadoPorList(String coDependencia) {
        StringBuilder sql = new StringBuilder();        
        sql.append("SELECT DISTINCT A.CO_EMP_EMI codDep,E.CEMP_APEPAT +' '+ E.CEMP_APEMAT +' '+ E.CEMP_DENOM descrip\n" +
                    "FROM IDOSGD.TDTV_REMITOS A \n" +
                    "INNER JOIN  IDOSGD.RHTM_PER_EMPLEADOS E ON E.CEMP_CODEMP=A.CO_EMP_EMI\n" +
                    "WHERE  A.TI_EMI='01' \n" +
                    "AND A.ES_ELI = '0'\n" +
                    "AND A.CO_GRU = '1' \n" );
        sql.append(" AND A.CO_DEP_EMI = ? "); 
        sql.append("UNION "); 
        sql.append("SELECT  NULL CODIGO  ,'.:TODOS:.' DESCRI ORDER BY 1 desc "); 
        
        List<RemitenteBean> list = null;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }  
        
    @Override
        public List<TipoDocumentoBean> listTipoDocumentoEmi(String coDependencia) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT CDOC_DESDOC DE_TIP_DOC,CDOC_TIPDOC CO_TIP_DOC ");
        sql.append(" FROM "
                + " (SELECT  A.CDOC_TIPDOC, B.CO_DEP, UPPER(A.CDOC_DESDOC) CDOC_DESDOC,A.CDOC_GRUPO\n" +
                    "FROM IDOSGD.SI_MAE_TIPO_DOC A,IDOSGD.SITM_DOC_DEPENDENCIA B\n" +
                    "WHERE A.CDOC_INDBAJ ='0'\n" +
                    "AND A.CDOC_TIPDOC = B.CO_TIP_DOC) AS Q1");
        sql.append(" WHERE CO_DEP = ? "); 
        sql.append(" UNION "); 
        sql.append(" SELECT '.:TODOS:.' DESCRI ,NULL CODIGO ORDER BY 1 "); 
        
        List<TipoDocumentoBean> list = new ArrayList<TipoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class), new Object[]{coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }    
    
    @Override
        public List<ExpedienteBean> listExpedientes(String coDependencia) {
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT DE_EXP, CO_EXP ");
        sql.append("FROM IDOSGD.TDTR_EXPEDIENTE WITH (NOLOCK) ");
        sql.append("WHERE CO_DEP = ? ");
        sql.append("UNION SELECT '.: TODOS :.', NULL ");
        sql.append("UNION SELECT '.:SIN EXPEDIENTE:.', 'SINEX' ORDER BY 1 ");
        
        List<ExpedienteBean> list = new ArrayList<ExpedienteBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ExpedienteBean.class), new Object[]{coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }    

    @Override
        public List<RemitenteBean> listRemitente(String annio, UsuarioConfigBean usuarioConfigBean) {
        StringBuffer sql = new StringBuffer();
        sql.append("select  distinct DE_DEPENDENCIA descrip, ");
        sql.append("		CO_DEPENDENCIA cod_dep, ");
        sql.append("		b.de_corta_depen ");
        sql.append("from IDOSGD.tdtx_resumen_dep a WITH (NOLOCK) , "); 
        sql.append("	 IDOSGD.RHTM_DEPENDENCIA b  WITH (NOLOCK) ");
        sql.append("where /*a.nu_ann = ? and*/");
        sql.append("    a.co_dep_ref = ? ");
        sql.append("   and a.ti_tab='D' ");
        sql.append("   and a.ti_emi = '01' ");
        sql.append("   AND (a.co_emp_res = (CASE ? WHEN 0 THEN a.co_emp_res ELSE ? END) "); 
        sql.append("	   OR ( a.co_emp_res IS NULL )  ) ");
        sql.append("   and a.CO_DEP = b.CO_DEPENDENCIA ");
        sql.append("UNION ");  
        sql.append("select distinct ");
        sql.append("	   CASE a.ti_emi ");
        sql.append("			WHEN '02' THEN ' [PROVEEDORES]' ");
        sql.append("			WHEN '03' THEN ' [CIUDADANOS]' ");
        sql.append("			WHEN '04' THEN ' [OTROS]' ");
        sql.append("			WHEN '05' THEN ' [PERSONALES]' ");
        sql.append("	   END descrip, ");
        sql.append("	   a.co_dep, ");
        sql.append("	   NULL ");
        sql.append("from IDOSGD.tdtx_resumen_dep a  WITH (NOLOCK) ");  
        sql.append("where /*a.nu_ann = ? and*/ ");
        sql.append("    a.co_dep_ref = ? ");
        sql.append("   and a.ti_tab='D' ");
        sql.append("   and a.ti_emi <> '01' ");
        sql.append("   AND (a.co_emp_res = (CASE ? WHEN 0 THEN a.co_emp_res ELSE ? END) ");
        sql.append("	   OR ( a.co_emp_res IS NULL )  ) ");
        sql.append("UNION ");
        sql.append("SELECT ' [TODOS]', ");
        sql.append("	   NULL, ");
        sql.append("       NULL ");
        sql.append("ORDER BY 1 ");
        
        List<RemitenteBean> list = new ArrayList<RemitenteBean>();

        try {
//            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{annio,usuarioConfigBean.getCoDep(),
//            usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp(),annio,usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()});
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{usuarioConfigBean.getCoDep(),
            usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp(),usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }        

    @Override
        public List<DestinatarioBean> listDestinatario(String annio, String coDependencia,String ptiAcceso,String pcoEmpleado) {
        StringBuffer sql = new StringBuffer();
        boolean bquery=false;
        
        if (ptiAcceso != null && (ptiAcceso.equals("0") || ptiAcceso.equals("2"))) {
            sql.append("SELECT SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](CO_EMP_RES), 1, 200) nombres, "); 
            sql.append("	   CO_EMP_RES CO_EMP_DES ");
            sql.append("FROM ( select DISTINCT CO_EMP_RES "); 
            sql.append("	   from IDOSGD.TDTX_RESUMEN_DEP  WITH (NOLOCK) ");
            sql.append("	   where /*NU_ANN = ? AND*/ ");
            sql.append("	    co_dep_REF = ? ");
            sql.append("	   AND TI_TAB = 'D') A ");
            sql.append("UNION ");
            sql.append("SELECT ' [TODOS]', ");
            sql.append("	   NULL ");
            sql.append("ORDER BY 1 ");
            bquery=true;
        } else {//personal
            sql.append("SELECT SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](CO_EMP_RES), 1, 200) nombres, "); 
            sql.append("	   CO_EMP_RES CO_EMP_DES ");        
            sql.append("FROM ( select DISTINCT CO_EMP_RES "); 
            sql.append("	   from IDOSGD.TDTX_RESUMEN_DEP  WITH (NOLOCK) ");
            sql.append("	   where /*NU_ANN = ? AND*/ ");
            sql.append("	    co_dep_REF = ? ");
            sql.append("	   AND TI_TAB = 'D' "); 
            sql.append("	   AND CO_EMP_RES=?) A ");
            sql.append("UNION ");
            sql.append("SELECT ' [TODOS]', ");
            sql.append("	   NULL ");
            sql.append("ORDER BY 1 ");            
        }
        
        List<DestinatarioBean> list = new ArrayList<DestinatarioBean>();

        try {
            if(bquery){
               // list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioBean.class), new Object[]{annio,coDependencia});    
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioBean.class), new Object[]{coDependencia});    
            }else{
                //list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioBean.class), new Object[]{annio,coDependencia,pcoEmpleado});
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioBean.class), new Object[]{coDependencia,pcoEmpleado});
            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<EmpleadoBean> listEmpleadoDependencia(String pcodDependencia){
        StringBuffer sql = new StringBuffer();       
        sql.append("SELECT  e.cemp_apepat, ");
        sql.append("        e.cemp_apemat, ");
        sql.append("        e.CEMP_DENOM, "); 
        sql.append("        e.CEMP_CODEMP ");
        sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e ");
        sql.append("where e.CEMP_EST_EMP = '1' ");
        sql.append("and (CEMP_CO_DEPEND = ? or CO_DEPENDENCIA = ?) ");
        sql.append("ORDER BY 1 ");
        
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcodDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
        public List<RemitenteBean> listReferenciaOrigenVB(String annio, String ptiAcceso, UsuarioConfigBean usuarioConfigBean) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT "); 
        sql.append("        CASE c.ti_emi "); 
        sql.append("		WHEN '01' THEN d.de_dependencia  WHEN '05' THEN d.de_dependencia ");
        sql.append("		WHEN '02' THEN ' [PROVEEDORES]' ");		
        sql.append("		WHEN '03' THEN ' [CIUDADANOS]' ");
        sql.append("		WHEN '04' THEN ' [OTROS]' "); 					
        sql.append("        END descrip, ");
        sql.append("        CASE c.ti_emi "); 
        sql.append("		WHEN '01' THEN c.co_dep_emi WHEN '05' THEN c.co_dep_emi ");
        sql.append("		WHEN '02' THEN 'P' ");
        sql.append("		WHEN '03' THEN 'C' ");
        sql.append("		WHEN '04' THEN 'O' ");
        sql.append("        END cod_dep, ");
        sql.append("        d.de_corta_depen ");
        sql.append("FROM   IDOSGD.tdtr_referencia	a WITH (NOLOCK)  ");
        sql.append("       INNER JOIN IDOSGD.tdtv_remitos		b WITH (NOLOCK) ON  a.nu_ann = b.nu_ann AND a.nu_emi = b.nu_emi ");
        sql.append("       INNER JOIN IDOSGD.tdtv_remitos		c WITH (NOLOCK) ON  a.nu_ann_ref = c.nu_ann AND a.nu_emi_ref = c.nu_emi ");
        sql.append("       INNER JOIN IDOSGD.rhtm_dependencia   d WITH (NOLOCK) ON  c.co_dep_emi = d.co_dependencia ");
        sql.append("       INNER JOIN IDOSGD.TDTV_PERSONAL_VB  e WITH (NOLOCK) ON   e.nu_emi = b.nu_emi");
        sql.append("WHERE b.es_eli = '0' ");
        sql.append("   AND e.co_dep = ?\n");     //sql.append("   AND b.co_emp_emi = ? ");
        sql.append("   AND e.co_emp_vb = ?\n");
        if(ptiAcceso.equals("C") && ptiAcceso !=null){
            sql.append(" AND b.co_gru IN ('1') \n ");       
        }else{
            sql.append(" AND b.co_gru IN ('1','2') \n ");
        }
        if(annio !=null && annio.isEmpty()){
            sql.append(" ");
        }else {
            sql.append(" e.nu_ann = ? ");
        }
        sql.append("GROUP BY c.ti_emi, ");
        sql.append("         c.co_dep_emi, ");
        sql.append("         d.de_dependencia, ");
        sql.append("         d.de_corta_depen ");
        sql.append("UNION ");
        sql.append("SELECT ' [TODOS]', ");
        sql.append("       NULL, ");
        sql.append("       NULL ");
        sql.append("ORDER BY 1 ");
              
        List<RemitenteBean> list = new ArrayList<RemitenteBean>();

        try {
            if(annio !=null && annio.isEmpty()){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{usuarioConfigBean.getCoDep(),
                usuarioConfigBean.getCempCodemp()});
             } else {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{usuarioConfigBean.getCoDep(),
                usuarioConfigBean.getCempCodemp(),annio});
            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
        public List<RemitenteBean> listReferenciaOrigen(String annio, UsuarioConfigBean usuarioConfigBean) {
        StringBuffer sql = new StringBuffer();
        sql.append("select  distinct ");   
        sql.append("        b.DE_DEPENDENCIA descrip, ");
        sql.append("        b.CO_DEPENDENCIA cod_dep, ");
        sql.append("        b.DE_CORTA_DEPEN ");
        sql.append("from IDOSGD.tdtx_resumen_dep a WITH (NOLOCK) , "); 
        sql.append("	 IDOSGD.RHTM_DEPENDENCIA b WITH (NOLOCK)  ");
        sql.append("where ");
        if(annio.length() == 1 || annio == "0"){
            sql.append(" ");
        }else {
            sql.append(" a.nu_ann = ? ");
        }
        sql.append("and a.co_dep = ? ");
        sql.append("and a.ti_tab='R' ");
        sql.append("and a.ti_emi = '01' ");
        sql.append("and a.co_emp_res = (CASE ? WHEN 0 THEN a.co_emp_res ELSE ? END) ");
        sql.append("and a.CO_DEP_REF = b.CO_DEPENDENCIA ");
        sql.append("UNION ");
        sql.append("select  distinct ");
        sql.append("        CASE a.ti_emi "); 
        sql.append("            WHEN '02' THEN ' [PROVEEDORES]' ");
        sql.append("            WHEN '03' THEN ' [CIUDADANOS]' ");
        sql.append("		WHEN '04' THEN ' [OTROS]' ");
        sql.append("            WHEN '05' THEN ' [PERSONALES]' ");
        sql.append("        END descrip, ");
        sql.append("        a.co_dep_ref cod_dep, ");
        sql.append("        NULL ");
        sql.append("from IDOSGD.tdtx_resumen_dep a  WITH (NOLOCK) ");  
        sql.append("where " );
        if(annio.length() == 1 || annio == "0"){
            sql.append(" ");
        }else {
            sql.append(" a.nu_ann = ? ");
        }
        sql.append("and a.co_dep = ? ");
        sql.append("and a.ti_tab='R' ");
        sql.append("and a.ti_emi <> '01' ");
        sql.append("and a.co_emp_res = (CASE ? WHEN 0 THEN a.co_emp_res ELSE ? END) ");
        sql.append("UNION ");
        sql.append("SELECT ' [TODOS]', ");
        sql.append("	   NULL, ");
        sql.append("	   NULL ");
        sql.append("ORDER BY 1 ");
        
        List<RemitenteBean> list = new ArrayList<RemitenteBean>();

        try {
            if(annio.length() == 1 || annio == "0"){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{usuarioConfigBean.getCoDep(),
            usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp(),usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()});
            }else {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{annio,usuarioConfigBean.getCoDep(),
            usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp(),annio,usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()});
            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<DependenciaBean> listDestinatarioEmi(String annio, UsuarioConfigBean usuarioConfigBean) {
        StringBuffer sql = new StringBuffer();
        sql.append("select  distinct ");  
        sql.append("        b.DE_DEPENDENCIA deDependencia, ");
        sql.append("        a.co_dep_ref coDependencia, ");
        sql.append("        b.DE_CORTA_DEPEN ");
        sql.append("from IDOSGD.tdtx_resumen_dep a WITH (NOLOCK) , "); 
        sql.append("	 IDOSGD.RHTM_DEPENDENCIA b  WITH (NOLOCK) ");
        sql.append("where " );
        if(annio.length()== 1 || annio == "0"){
            sql.append(" ");
        } else {
            sql.append(" a.nu_ann = ? ");
        }
        sql.append("and a.co_dep = ? ");
        sql.append("and a.ti_tab='D' ");
        sql.append("and a.ti_emi = '01' ");
        sql.append("and a.co_emp_res = (CASE ? "); 
        sql.append("                        WHEN 0 THEN a.co_emp_res ");	  
        sql.append("                        WHEN 2 THEN a.co_emp_res ");
        sql.append("                        ELSE ? ");
        sql.append("			END) "); 
        sql.append("and a.CO_DEP_ref = b.CO_DEPENDENCIA ");
        sql.append("UNION ");  
        sql.append("select  distinct "); 
        sql.append("        CASE a.ti_emi ");
        sql.append("		WHEN '02' THEN ' [PROVEEDORES]' ");
        sql.append("		WHEN '03' THEN ' [CIUDADANOS]' ");
        sql.append("		WHEN '04' THEN ' [OTROS]' ");
        sql.append("		WHEN '05' THEN ' [PERSONALES]' ");
        sql.append("        END deDependencia, ");
        sql.append("        a.co_dep_ref coDependencia, ");
        sql.append("        NULL ");
        sql.append("from IDOSGD.tdtx_resumen_dep a  WITH (NOLOCK) ");  
        sql.append("where " );
        if(annio.length()== 1 || annio == "0"){
            sql.append(" ");
        } else {
            sql.append(" a.nu_ann = ? ");
        }
        sql.append("and a.co_dep = ? ");
        sql.append("and a.ti_tab='D' ");
        sql.append("and a.ti_emi <> '01' ");
        sql.append("and a.co_emp_res = (CASE ? "); 
        sql.append("                        WHEN 0 THEN a.co_emp_res ");
        sql.append("                        WHEN 2 THEN a.co_emp_res ");
        sql.append("                        ELSE ? ");
        sql.append("			END) "); 
        sql.append("UNION ");
        sql.append("SELECT ' [TODOS]', ");
        sql.append("	   NULL, ");
        sql.append("	   NULL ");
        sql.append("ORDER BY 1 ");
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            if(annio.length()== 1 || annio == "0"){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{usuarioConfigBean.getCoDep(),
                usuarioConfigBean.getTiConsulta(),usuarioConfigBean.getCempCodemp(),usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiConsulta(),usuarioConfigBean.getCempCodemp()});
            } else {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{annio,usuarioConfigBean.getCoDep(),
                usuarioConfigBean.getTiConsulta(),usuarioConfigBean.getCempCodemp(),annio,usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiConsulta(),usuarioConfigBean.getCempCodemp()});
            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
        
    @Override
    public List<EmpleadoBean> listEmpleadoElaboradoPor(String pcodDependencia,String ptiAcceso,String pcoEmpleado){
        StringBuffer sql = new StringBuffer();
        boolean bquery=false;
       
        if (ptiAcceso!=null&&(ptiAcceso.equals("0")||ptiAcceso.equals("2"))) {
            sql.append("SELECT  e.cemp_apepat + ' ' + e.cemp_apemat + ' ' + e.cemp_denom nombre, "); 
            sql.append("	e.CEMP_CODEMP ");
            sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e  WITH (NOLOCK) ");
            sql.append("where e.CEMP_EST_EMP = '1' ");
            sql.append("AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA ");
            sql.append("                        from IDOSGD.RHTM_DEPENDENCIA d  WITH (NOLOCK) "); 
            sql.append("			where d.CO_DEPENDENCIA=? ");
            sql.append("			or d.CO_DEPEN_PADRE=?) ");
            sql.append("OR CO_DEPENDENCIA =  ?) ");
            sql.append("UNION ");
            sql.append("SELECT  e.cemp_apepat + ' ' + e.cemp_apemat + ' ' + e.cemp_denom nombre, "); 
            sql.append("	e.CEMP_CODEMP ");
            sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e  WITH (NOLOCK) ");
            sql.append("where e.CEMP_EST_EMP = '1' ");
            sql.append("AND cemp_codemp in (select co_emp ");
            sql.append("                    from IDOSGD.tdtx_dependencia_empleado  WITH (NOLOCK) "); 
            sql.append("                    where co_dep=? ");
            sql.append("                    and es_emp='0') ");
            sql.append("UNION ");
            sql.append("SELECT ' [TODOS]', ");
            sql.append("        NULL ");
            sql.append("ORDER BY 1 ");           
            bquery=true;
        } else { //personal
            sql.append("SELECT	e.cemp_apepat + ' ' + e.cemp_apemat + ' ' + e.cemp_denom nombre, "); 
            sql.append("        e.CEMP_CODEMP ");
            sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e ");
            sql.append("where e.CEMP_EST_EMP = '1' ");
            sql.append("AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA ");
            sql.append("			from IDOSGD.RHTM_DEPENDENCIA d  WITH (NOLOCK) "); 
            sql.append("			where d.CO_DEPENDENCIA=? ");
            sql.append("			or d.CO_DEPEN_PADRE=?) ");
            sql.append("OR CO_DEPENDENCIA =  ?) ");
            sql.append("AND e.CEMP_CODEMP=? ");
            sql.append("UNION ");
            sql.append("SELECT  ' [TODOS]', "); 
            sql.append("	NULL ");
            sql.append("ORDER BY 1 ");
        }

        
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        
        try {
            if(bquery){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcodDependencia,
                                                                                                                    pcodDependencia,pcodDependencia});                
            }else{
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcodDependencia,
                                                                                                                pcodDependencia,pcoEmpleado});                
            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    
    //JHON
    @Override
    public List<EmpleadoBean> listEmpleadoElaboradoPorVB(String pcodDependencia,String ptiAcceso,String pcoEmpleado){
        StringBuffer sql = new StringBuffer();
        //boolean bquery=false;
       
            sql.append(" SELECT DISTINCT  E.CEMP_APEPAT + ' ' + E.CEMP_APEMAT + ' ' + E.CEMP_DENOM  NOMBRE, E.CEMP_CODEMP "); 
            sql.append(" FROM IDOSGD.tdtv_remitos RE WITH (NOLOCK)");
            sql.append(" INNER JOIN IDOSGD.TDTV_PERSONAL_VB VB WITH (NOLOCK) ON  RE.NU_ANN=VB.NU_ANN AND RE.NU_EMI=VB.NU_EMI ");
            sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK) ON RR.NU_ANN=RE.NU_ANN AND RR.NU_EMI=RE.NU_EMI ");
            sql.append(" INNER JOIN IDOSGD.RHTM_PER_EMPLEADOS E  WITH (NOLOCK) ON E.CEMP_CODEMP = RE.CO_EMP_RES ");
            sql.append(" WHERE "); 
            sql.append(" RE.ES_ELI='0' ");
            if(ptiAcceso.equals("C") && ptiAcceso !=null){
                sql.append(" AND RE.ES_DOC_EMI NOT IN('5','9') ");
            }else{
                sql.append(" AND RE.ES_DOC_EMI NOT IN('9') ");
            }
            sql.append(" AND VB.CO_DEP = ? ");
            sql.append(" AND VB.CO_EMP_VB = ? ");
            sql.append(" UNION ");
            sql.append(" SELECT  ' [TODOS]', "); 
            sql.append("	NULL ");
            sql.append(" ORDER BY 1 ");
        
        
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        
        try {
            
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcoEmpleado});                
            //else{
//            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcodDependencia,
//                                                                                                                pcodDependencia,pcoEmpleado});                
//            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
     //JHON
    @Override
    public List<EmpleadoBean> listEmpleadoElaboradoPorMP(String pcodDependencia,String ptiAcceso,String pcoEmpleado){
        StringBuffer sql = new StringBuffer();
        boolean bquery=false;
       
            sql.append(" SELECT DISTINCT  DE_EMP_REC AS NOMBRE, CO_EMP_RES AS CEMP_CODEMP "); 
            sql.append(" FROM ( SELECT  IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(R.CO_EMP_RES) DE_EMP_REC, R.CO_EMP_RES  ");
            sql.append(" FROM IDOSGD.TDTV_REMITOS R WITH (NOLOCK)  ");
            sql.append(" INNER JOIN IDOSGD.TDTV_DESTINOS D WITH (NOLOCK) ON D.NU_ANN=R.NU_ANN AND D.NU_EMI=R.NU_EMI ");
            sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK) ON RR.NU_ANN=R.NU_ANN AND RR.NU_EMI=R.NU_EMI ");
            sql.append(" INNER JOIN IDOSGD.TDTR_MOTIVO M WITH (NOLOCK) ON M.CO_MOT=D.CO_MOT  "); 
            sql.append(" INNER JOIN IDOSGD.TDTC_EXPEDIENTE C WITH (NOLOCK) ON C.NU_ANN_EXP=R.NU_ANN_EXP  AND C.NU_SEC_EXP=R.NU_SEC_EXP  ");
            sql.append(" INNER JOIN (SELECT Y.NU_ANN, Y.NU_EMI, Y.FE_FINALIZA, ");
            sql.append(" (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_DIA_TRA](CONVERT(DATETIME, GETDATE()), Y.FE_FINALIZA)) NU_DIA_FAL ");
            sql.append(" FROM (SELECT X.NU_ANN, X.NU_EMI, ");
            sql.append(" (DATEADD(DAY, (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_DIA_MAS](CONVERT(DATETIME, X.FE_EMI), NU_DIA_ATE)), CONVERT(DATE, X.FE_EMI))) AS FE_FINALIZA "); 
            sql.append("	FROM IDOSGD.TDTV_REMITOS X WITH (NOLOCK) ) Y) E ON E.NU_ANN=R.NU_ANN AND E.NU_EMI=R.NU_EMI ");
            sql.append(" WHERE R.CO_GRU='3' ");
            sql.append(" AND R.ES_ELI='0' ");
            sql.append(" AND D.ES_ELI='0' ");
            sql.append(" AND R.ES_DOC_EMI NOT IN ('5', '9', '7') ");
            sql.append(" AND R.TI_EMI IN ('02','03','04') "); 
            sql.append(" AND R.CO_DEP_EMI = ? ");
            if (ptiAcceso!=null&&(ptiAcceso.equals("0")||ptiAcceso.equals("2"))) {
                sql.append(" ");
            } else{
                sql.append(" AND R.CO_EMP_RES = ? ");
                bquery=true;
            }
            sql.append(" UNION ");
            sql.append(" SELECT  ' [TODOS]', "); 
            sql.append("	NULL ");
            sql.append(" ) X ");
            sql.append(" ORDER BY 1 ");
        
        
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        
        try {
            if(bquery){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcoEmpleado});                
            }else{
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia});                
            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<EmpleadoBean> listEmpleadoDestino(String pcodDependencia,String ptiAcceso,String pcoEmpleado){
        StringBuffer sql = new StringBuffer();
        boolean bquery=false;
       
        if (ptiAcceso!=null&&(ptiAcceso.equals("0")||ptiAcceso.equals("2"))) {
            sql.append("SELECT  e.cemp_apepat + ' ' + e.cemp_apemat + ' ' + e.cemp_denom nombre, "); 
            sql.append("	e.CEMP_CODEMP ");
            sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e  WITH (NOLOCK) ");
            sql.append("where e.CEMP_EST_EMP = '1' ");
            sql.append("AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA ");
            sql.append("                        from IDOSGD.RHTM_DEPENDENCIA d "); 
            sql.append("			where d.CO_DEPENDENCIA=? ");
            sql.append("			or d.CO_DEPEN_PADRE=?) ");
            sql.append("OR CO_DEPENDENCIA =  ?) ");
            sql.append("UNION ");
            sql.append("SELECT  e.cemp_apepat + ' ' + e.cemp_apemat + ' ' + e.cemp_denom nombre, "); 
            sql.append("	e.CEMP_CODEMP ");
            sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e  WITH (NOLOCK) ");
            sql.append("where e.CEMP_EST_EMP = '1' ");
            sql.append("AND cemp_codemp in (select co_emp ");
            sql.append("                    from IDOSGD.tdtx_dependencia_empleado "); 
            sql.append("                    where co_dep=? ");
            sql.append("                    and es_emp='0') ");
            sql.append("UNION ");
            sql.append("SELECT ' [TODOS]', ");
            sql.append("        NULL ");
            sql.append("ORDER BY 1 ");           
            bquery=true;
        } else { //personal
            sql.append("SELECT	e.cemp_apepat + ' ' + e.cemp_apemat + ' ' + e.cemp_denom nombre, "); 
            sql.append("        e.CEMP_CODEMP ");
            sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e WITH (NOLOCK)  ");
            sql.append("where e.CEMP_EST_EMP = '1' ");
            sql.append("AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA ");
            sql.append("			from IDOSGD.RHTM_DEPENDENCIA d "); 
            sql.append("			where d.CO_DEPENDENCIA=? ");
            sql.append("			or d.CO_DEPEN_PADRE=?) ");
            sql.append("OR CO_DEPENDENCIA =  ?) ");
            sql.append("AND e.CEMP_CODEMP=? ");
            sql.append("UNION ");
            sql.append("SELECT  ' [TODOS]', "); 
            sql.append("	NULL ");
            sql.append("ORDER BY 1 ");
        }

        
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        
        try {
            if(bquery){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcodDependencia,
                                                                                                                    pcodDependencia,pcodDependencia});                
            }else{
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcodDependencia,
                                                                                                                pcodDependencia,pcoEmpleado});                
            }
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    } 
    
    @Override
        public List<DependenciaBean> listDependenciaDestinatarioEmi(String coDepen, String deDepen) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_DEPENDENCIA, ");
        sql.append("       CO_DEPENDENCIA, ");
        sql.append("       DE_CORTA_DEPEN ");
        sql.append("FROM IDOSGD.RHTM_DEPENDENCIA  WITH (NOLOCK) ");
        sql.append("WHERE co_nivel <> '6' ");
        sql.append("AND IN_BAJA = '0' ");
        sql.append("AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%' ) ");
        sql.append("UNION ");
        sql.append("SELECT DE_DEPENDENCIA, ");
        sql.append("       CO_DEPENDENCIA, ");
        sql.append("       DE_CORTA_DEPEN ");
        sql.append("FROM IDOSGD.RHTM_DEPENDENCIA  WITH (NOLOCK) ");
        sql.append("WHERE co_nivel = '6' ");
        sql.append("   AND IN_BAJA = '0' ");
        sql.append("   AND (CO_DEPEN_PADRE in (select CO_DEPEN_PADRE co_dep ");
        sql.append("			       from IDOSGD.RHTM_DEPENDENCIA "); 
        sql.append("			       where CO_DEPENDENCIA = ?) ");
        sql.append("       OR ? IN (CO_DEPENDENCIA, CO_DEPEN_PADRE)) ");
        sql.append("   AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%') ");
        sql.append("ORDER BY 1 ");    
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{deDepen,deDepen,
            coDepen,coDepen,deDepen,deDepen});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
        
    @Override
    public List<TipoDocumentoBean> listTipDocXDependencia(String coDependencia){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  UPPER(A.CDOC_DESDOC) DE_TIP_DOC, ");
        sql.append("		A.CDOC_TIPDOC CO_TIP_DOC,CDOC_GRUPO ");
        sql.append("FROM IDOSGD.SI_MAE_TIPO_DOC A, ");
        sql.append("	 IDOSGD.SITM_DOC_DEPENDENCIA B ");
        sql.append("WHERE A.CDOC_INDBAJ ='0' ");
        sql.append("AND A.CDOC_TIPDOC = B.CO_TIP_DOC ");
        sql.append("AND B.CO_DEP=? ");
        sql.append("ORDER BY 1 "); 
        
        List<TipoDocumentoBean> list = new ArrayList<TipoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class), new Object[]{coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public List<TipoDocumentoBean> listTipDocXDependenciaPersonal(String coDependencia){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  UPPER(A.CDOC_DESDOC) DE_TIP_DOC, A.CDOC_TIPDOC CO_TIP_DOC,CDOC_GRUPO \n" +
            "FROM IDOSGD.SI_MAE_TIPO_DOC A,IDOSGD.SITM_DOC_DEPENDENCIA B\n" +
            "WHERE A.CDOC_INDBAJ ='0'AND CDOC_GRUPO='02' \n" +
            "AND A.CDOC_TIPDOC = B.CO_TIP_DOC\n" +
            "AND B.CO_DEP=?\n" +
            "ORDER BY 1"); 
        
        List<TipoDocumentoBean> list = new ArrayList<TipoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class), new Object[]{coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public List<PrioridadDocumentoBean> listPrioridadDocTblEmi() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_PRI, CO_PRI ");
        sql.append("FROM IDOSGD.TDTR_PRIORIDAD ");
        
        List<PrioridadDocumentoBean> list = new ArrayList<PrioridadDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(PrioridadDocumentoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;        
    }    
    
    @Override
    public List<TipoDocumentoBean> listTipDocReferencia(String coDependencia){
        StringBuffer sql = new StringBuffer();
        
//        sql.append("select  a.CO_DOC CO_TIP_DOC, "); 
//        sql.append("        b.CDOC_DESDOC DE_TIP_DOC ");
//        sql.append("from (select DISTINCT co_doc ");
//        sql.append("      from IDOSGD.tdtx_resumen_doc  WITH (NOLOCK) ");
//        sql.append("      where CO_DEP = ?) a, ");
//        sql.append("	 IDOSGD.SI_MAE_TIPO_DOC b ");
//        sql.append("WHERE  A.CO_DOC = B.CDOC_TIPDOC ");
//        sql.append("order by 2 ");
          sql.append("select a.CO_DOC CO_TIP_DOC , B.CDOC_DESDOC DE_TIP_DOC\n" +
"                    from (\n" +
"                        select DISTINCT co_doc \n" +
"                        from IDOSGD.tdtx_resumen_doc\n" +
"                        where CO_DEP = ?)a, IDOSGD.SI_MAE_TIPO_DOC b\n" +
"                    WHERE  A.CO_DOC = B.CDOC_TIPDOC                  \n" +
"        UNION        \n" +
"         SELECT   A.CDOC_TIPDOC CO_TIP_DOC ,UPPER(A.CDOC_DESDOC) DE_TIP_DOC\n" +
"            FROM IDOSGD.SI_MAE_TIPO_DOC A,IDOSGD.SITM_DOC_DEPENDENCIA B\n" +
"            WHERE A.CDOC_INDBAJ ='0'\n" +
"            AND A.CDOC_TIPDOC = B.CO_TIP_DOC\n" +
"            AND B.CO_DEP= ? \n" +
"            ORDER BY 2"); 
        List<TipoDocumentoBean> list = new ArrayList<TipoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class), new Object[]{coDependencia,coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<TipoDestinatarioEmiBean> listTipDestinatarioEmi(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("select  CELE_DESELE de_destinatario, ");
        sql.append("        CELE_CODELE co_destinatario ");
        sql.append("from IDOSGD.SI_ELEMENTO  WITH (NOLOCK) ");
        sql.append("WHERE CTAB_CODTAB='TIP_DESTINO' "); 
        sql.append("AND CELE_CODELE not in  ('05') "); 
        
        List<TipoDestinatarioEmiBean> list = new ArrayList<TipoDestinatarioEmiBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDestinatarioEmiBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<GrupoDestinatarioBean> listGrupoDestinatario(String codDependencia){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT  DE_GRU_DES de_grupo, ");
        sql.append("        CO_GRU_DES co_grupo "); 
        sql.append("FROM IDOSGD.TDTM_GRU_DES ");
        sql.append("WHERE CO_DEP=? ");
        sql.append("AND ES_GRU_DES='1' "); 
        sql.append("ORDER BY 2 ");
        
        List<GrupoDestinatarioBean> list = new ArrayList<GrupoDestinatarioBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(GrupoDestinatarioBean.class), new Object[]{codDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;           
    }
    
    @Override
    public List<MotivoBean> listMotivoDestinatario(String codDependencia, String coTipoDoc){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT  a.de_mot, "); 
        sql.append("        a.co_mot ");
        sql.append("FROM IDOSGD.tdtr_motivo a, "); 
        sql.append("	 IDOSGD.tdtx_moti_docu_depe b "); 
        sql.append("WHERE a.co_mot = b.co_mot ");
        sql.append("AND b.co_dep = ? "); 
        sql.append("AND b.co_tip_doc = ? ");
        sql.append("UNION ");
        sql.append("SELECT  de_mot, ");
        sql.append("        co_mot ");
        sql.append("FROM IDOSGD.tdtr_motivo ");  
        sql.append("where co_mot in ('0','1') "); 
        sql.append("ORDER BY 1 ");

        List<MotivoBean> list = new ArrayList<MotivoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(MotivoBean.class), new Object[]{codDependencia,coTipoDoc});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;           
    }
    
    @Override
    public List<DependenciaBean> listDependenciaRemitenteEmi(String coDependencia){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  DE_DEPENDENCIA, "); 
        sql.append("        CO_DEPENDENCIA ");
        sql.append("FROM IDOSGD.RHTM_DEPENDENCIA  WITH (NOLOCK) "); 
        sql.append("WHERE CO_DEPENDENCIA in (SELECT ? "); 
        sql.append("                         UNION ");
        sql.append("                         SELECT co_dep_ref ");
        sql.append("			     FROM IDOSGD.tdtx_referencia  WITH (NOLOCK) ");
        sql.append("                         WHERE co_dep_emi = ? ");
        sql.append("                         AND ti_emi = 'R' "); 
        sql.append("                         AND es_ref = '1') ");
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{coDependencia,coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;         
    }
    
    @Override    
    public List<LocalBean> listLocalRemitenteEmi(String coDependencia){
        StringBuffer sql = new StringBuffer();
        sql.append("select DE_NOMBRE_LOCAL DE_LOCAL, ");
        sql.append("	   CCOD_LOCAL CO_LOCAL ");
        sql.append("from IDOSGD.SI_MAE_LOCAL ");
        sql.append("WHERE CCOD_LOCAL IN (SELECT CO_LOC ");
        sql.append("			 FROM IDOSGD.SITM_LOCAL_DEPENDENCIA "); 
        sql.append("			 WHERE CO_DEP = ?) ");
        
        List<LocalBean> list = new ArrayList<LocalBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(LocalBean.class), new Object[]{coDependencia});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;          
    }

    @Override    
    public List<LocalBean> listLocal(){
        StringBuffer sql = new StringBuffer();
        sql.append("select DE_NOMBRE_LOCAL DE_LOCAL, ");
        sql.append("	   CCOD_LOCAL CO_LOCAL ");
        sql.append("from IDOSGD.SI_MAE_LOCAL ");
        sql.append("ORDER BY 2 ");
        
        List<LocalBean> list = new ArrayList<LocalBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(LocalBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;          
    }
    
    @Override
    public List<RemitenteBean> listReferenciaOrigenPersonal(String sCoEmpEmi) {
        StringBuffer sql = new StringBuffer();

        sql.append("SELECT "); 
        sql.append("        CASE c.ti_emi "); 
        sql.append("		WHEN '01' THEN d.de_dependencia  WHEN '05' THEN d.de_dependencia ");
        sql.append("		WHEN '02' THEN ' [PROVEEDORES]' ");		
        sql.append("		WHEN '03' THEN ' [CIUDADANOS]' ");
        sql.append("		WHEN '04' THEN ' [OTROS]' "); 					
        sql.append("        END descrip, ");
        sql.append("        CASE c.ti_emi "); 
        sql.append("		WHEN '01' THEN c.co_dep_emi WHEN '05' THEN c.co_dep_emi ");
        sql.append("		WHEN '02' THEN 'P' ");
        sql.append("		WHEN '03' THEN 'C' ");
        sql.append("		WHEN '04' THEN 'O' ");
        sql.append("        END cod_dep, ");
        sql.append("        d.de_corta_depen ");
        sql.append("FROM   IDOSGD.tdtr_referencia	a WITH (NOLOCK) , ");
        sql.append("       IDOSGD.tdtv_remitos		b WITH (NOLOCK) , ");
        sql.append("       IDOSGD.tdtv_remitos		c WITH (NOLOCK) , ");
        sql.append("       IDOSGD.rhtm_dependencia  d WITH (NOLOCK)  ");
        sql.append("WHERE b.es_eli = '0' ");
        sql.append("   AND b.co_gru = '2' "); ////  AND b.co_gru = '1' 
        sql.append("   AND a.nu_ann = b.nu_ann ");
        sql.append("   AND a.nu_emi = b.nu_emi ");
        sql.append("   AND a.nu_ann_ref = c.nu_ann ");
        sql.append("   AND a.nu_emi_ref = c.nu_emi ");
        sql.append("   AND c.co_dep_emi = d.co_dependencia ");
        sql.append("   AND b.co_emp_emi = ? ");
        sql.append("GROUP BY c.ti_emi, ");
        sql.append("         c.co_dep_emi, ");
        sql.append("         d.de_dependencia, ");
        sql.append("         d.de_corta_depen ");
        sql.append("UNION ");
        sql.append("SELECT ' [TODOS]', ");
        sql.append("       NULL, ");
        sql.append("       NULL ");
        sql.append("ORDER BY 1 ");

        List<RemitenteBean> list = new ArrayList<RemitenteBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{sCoEmpEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<DependenciaBean> listDestinatarioEmiPersonal(String sCoEmpEmi) {
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT  CASE c.ti_des ");
        sql.append("		WHEN '01' THEN d.de_dependencia ");
        sql.append("		WHEN '02' THEN ' [PROVEEDORES]' "); 			
        sql.append("		WHEN '03' THEN ' [CIUDADANOS]' "); 			
        sql.append("		WHEN '04' THEN ' [OTROS]' "); 					
        sql.append("        END deDependencia, ");
        sql.append("        CASE c.ti_des ");
        sql.append("		WHEN '01' THEN c.co_dep_des ");
        sql.append("		WHEN '02' THEN 'P' "); 			
        sql.append("		WHEN '03' THEN 'C' ");
        sql.append("        	WHEN '04' THEN 'O' ");
        sql.append("        END coDependencia, ");
        sql.append("        d.DE_CORTA_DEPEN ");
        sql.append("FROM IDOSGD.tdtv_remitos    b WITH (NOLOCK) , ");
        sql.append("     IDOSGD.tdtv_destinos   c WITH (NOLOCK) ");
        sql.append("	 LEFT OUTER JOIN IDOSGD.rhtm_dependencia  d  WITH (NOLOCK) ");
        sql.append("	 ON c.co_dep_des = d.co_dependencia ");
        sql.append("WHERE b.es_eli = '0' ");
        sql.append("   AND b.co_gru = '2' "); // AND b.co_gru = '1' /*JHON*/
        sql.append("   AND b.nu_ann = c.nu_ann ");
        sql.append("   AND b.nu_emi = c.nu_emi ");
        sql.append("   AND b.co_emp_emi = ? "); 
        sql.append("GROUP BY c.ti_des, ");
        sql.append("         c.co_dep_des, ");
        sql.append("         d.de_dependencia, ");
        sql.append("         d.de_corta_depen ");
        sql.append("UNION ");
        sql.append("SELECT ' [TODOS]', ");
        sql.append("       NULL, ");
        sql.append("       NULL ");
        sql.append("ORDER BY 1 ");            
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{sCoEmpEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }    

    @Override
    public DependenciaBean getDatosDependencia(String codDependencia){
        StringBuffer sql = new StringBuffer();
        sql.append("select  co_dependencia, ");
        sql.append("        co_empleado, ");
        sql.append(" de_dependencia tituloDep,\n");
        sql.append("        [IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](co_empleado) de_dependencia, ");
        sql.append("        de_sigla ");
        sql.append("from IDOSGD.rhtm_dependencia "); 
        sql.append("where co_dependencia = ? ");
        
        DependenciaBean depEmi = new DependenciaBean();
        
        try {
            depEmi = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{codDependencia});
        } catch (EmptyResultDataAccessException e) {
            depEmi = null;
        } catch (Exception e) {
            depEmi = null;
            e.printStackTrace();
        }
        
        return depEmi;

    }
    
    @Override
        public List<EstadoDocumentoBean> listEstadosDocumentoMP(String nomTabla) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  DE_EST_MP DE_EST, ");
        sql.append("        CO_EST "); 
        sql.append("FROM IDOSGD.TDTR_ESTADOS "); 
        sql.append("WHERE DE_TAB=? ");
        sql.append("AND DE_EST_MP IS NOT NULL ");
        sql.append("UNION ");
        sql.append("SELECT  '.: TODOS :.', ");
        sql.append("        NULL ");
        sql.append("ORDER BY CO_EST ");
        
        List<EstadoDocumentoBean> list = new ArrayList<EstadoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(EstadoDocumentoBean.class),
                    new Object[]{nomTabla});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }    

    @Override
    public List<TipoDestinatarioEmiBean> listTipEmisorDocExtRecep(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("select  CELE_DESELE de_destinatario, ");
        sql.append("        CELE_CODELE co_destinatario "); 
        sql.append("from IDOSGD.SI_ELEMENTO "); 
        sql.append("WHERE CTAB_CODTAB='TIP_DESTINO' "); 
        sql.append("AND CELE_CODELE NOT IN ('01','05') ");
        sql.append("UNION "); 
        sql.append("SELECT  '.: TODOS :.', ");
        sql.append("        NULL ");
        //sql.append("ORDER BY 2 "); 
        
        List<TipoDestinatarioEmiBean> list = new ArrayList<TipoDestinatarioEmiBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDestinatarioEmiBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<TupaExpedienteBean> listTupaExpedienteNew(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT  DE_NOMBRE DE_TUPA, "); 
        sql.append("        CO_PROCESO CO_TUPA ");
        sql.append("FROM IDOSGD.TDTR_PROCESOS_EXP "); 
        sql.append("WHERE TI_PROCESO = '1' "); 
        sql.append("AND ES_ESTADO = '1' ");
        sql.append("UNION ");  
        sql.append("SELECT  '[SIN TUPA]', ");
        sql.append("        '0000' "); 
        sql.append("ORDER BY 2 ");
        
        List<TupaExpedienteBean> list = new ArrayList<TupaExpedienteBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TupaExpedienteBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }    
    
    @Override
    public List<TupaExpedienteBean> listTupaExpediente(){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT DE_NOMBRE DE_TUPA, "); 
        sql.append("       CO_PROCESO CO_TUPA ");
        sql.append("FROM IDOSGD.TDTR_PROCESOS_EXP "); 
        sql.append("WHERE TI_PROCESO = '1' "); 
        sql.append("UNION ");
        sql.append("SELECT  '[SIN TUPA]', ");
        sql.append("        '0000' ");
        sql.append("ORDER BY 2 ");
        
        List<TupaExpedienteBean> list = new ArrayList<TupaExpedienteBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TupaExpedienteBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<LocalBean> lsLocal(){
        StringBuffer sql = new StringBuffer();
        sql.append("select  DE_NOMBRE_LOCAL DE_LOCAL, ");
        sql.append("        CCOD_LOCAL CO_LOCAL ");
        sql.append("from IDOSGD.SI_MAE_LOCAL ");
        sql.append("union ");
        sql.append("select  ' [TODOS]' DE_LOCAL, ");
        sql.append("        NULL CO_LOCAL ");
        sql.append("ORDER BY 1 "); 

        List<LocalBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(LocalBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;            
    }
    
    @Override
    public List<EtiquetaBean> getEtiquetasList() {
        StringBuffer sql = new StringBuffer();

        sql.append("select  co_est,de_est ");
        sql.append("from IDOSGD.tdtr_estados "); 
        sql.append("where de_tab = 'CO_ETIQUETA_REC' "); 
        sql.append("UNION "); 
        sql.append("SELECT  NULL, ");
        sql.append("        '.: TODOS :.' ");

        List<EtiquetaBean> list = new ArrayList<EtiquetaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EtiquetaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }    
    
    @Override
    public List<DependenciaBean> getlsDestinatarioEmiDocExt(String annio,String coDep){
        StringBuffer sql = new StringBuffer();
        
        sql.append("select  b.DE_DEPENDENCIA deDependencia, ");
        sql.append("        a.co_dep_ref coDependencia, ");
        sql.append("        b.DE_CORTA_DEPEN ");
        sql.append("from IDOSGD.tdtx_resumen_dep a WITH (NOLOCK) , ");
        sql.append("	 IDOSGD.RHTM_DEPENDENCIA b WITH (NOLOCK)  ");
        sql.append("where a.nu_ann = ? ");
        sql.append("   and a.co_dep = ? ");
        sql.append("   and a.ti_tab='D' ");
        sql.append("   and a.ti_emi in ('02', '03', '04') ");
        sql.append("   and a.CO_DEP_ref = b.CO_DEPENDENCIA ");
        sql.append("UNION ");
        sql.append("SELECT  ' [TODOS]', ");
        sql.append("        NULL, ");
        sql.append("        NULL ");
        sql.append("ORDER BY 1 "); 
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), 
                    new Object[]{annio,coDep});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<DependenciaBean> getlsDestinatarioDocPendEntrega(String coDep){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT  DE_DEPENDENCIA, "); 
        sql.append("        CO_DEPENDENCIA, "); 
        sql.append("        DE_CORTA_DEPEN ");
        sql.append("FROM IDOSGD.RHTM_DEPENDENCIA ");
        sql.append("where CO_DEPENDENCIA in (select CO_DEP ");
        sql.append("                         from IDOSGD.sitm_local_dependencia "); 
        sql.append("                         WHERE CO_LOC=?) ");
        sql.append("UNION "); 
        sql.append("select  ' [TODOS]', ");
        sql.append("        NULL, ");
        sql.append("        NULL ");
        sql.append("order by 1 ");
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), 
                    new Object[]{coDep});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }    

    @Override
    public List<VencimientoBean> getVencimientoList() {
         StringBuffer sql = new StringBuffer();

        sql.append("select  co_est, ");
        sql.append("        de_est ");
        sql.append("from IDOSGD.tdtr_estados ");
        sql.append("where de_tab = 'VENCIMIENTO' "); 
        sql.append("UNION "); 
        sql.append("SELECT  NULL, ");
        sql.append("        '.: TODOS :.' ");

        List<VencimientoBean> list = new ArrayList<VencimientoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(VencimientoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<EstadoDocumentoBean> listEstadosDocumentoEmiSegui(String tdtv_destinos) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  DE_EST, ");
        sql.append("        CAST(CO_EST AS NVARCHAR(10)) CO_EST "); 
        sql.append("FROM IDOSGD.TDTR_ESTADOS ");
        sql.append("WHERE DE_TAB = ? ");
        sql.append("AND CO_EST NOT IN('5', '9') ");
        sql.append("UNION ");
        sql.append("SELECT  '.:TODOS.:', ");
        sql.append("        NULL CO_EST ");
        sql.append("ORDER BY CO_EST ");
        
        List<EstadoDocumentoBean> list = new ArrayList<EstadoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(EstadoDocumentoBean.class),
                    new Object[]{tdtv_destinos});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<SiElementoBean> getLsSiElementoBean(String pctabCodtab){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT	CELE_CODELE, ");
        sql.append("		CELE_DESELE, ");
        sql.append("		NELE_NUMSEC, ");
        sql.append("		CELE_DESCOR,BL_DOC as archivo ");
        sql.append("FROM IDOSGD.SI_ELEMENTO ");
        sql.append("WHERE CTAB_CODTAB = ? ");

        List<SiElementoBean> list = null;
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(SiElementoBean.class),
                    new Object[]{pctabCodtab});
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<SiElementoBean> getLsSiElementoBeanOrder(String pctabCodtab){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CELE_CODELE,CELE_DESELE,NELE_NUMSEC,CELE_DESCOR,BL_DOC as archivo \n" +
            "FROM   IDOSGD.SI_ELEMENTO\n" +
            "WHERE  CTAB_CODTAB = ?"+
            " ORDER BY CELE_CODELE ");

        List<SiElementoBean> list = null;
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(SiElementoBean.class),
                    new Object[]{pctabCodtab});
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
    @Override
    public List<DepartamentoBean> listDepartamentos(){
                StringBuffer sql = new StringBuffer();

        sql.append("select ubdep coDepartamento,nodep noDepartamento "
                + "from IDOSGD.idtubias "
                + "where nodep != '               ' "
                + "and nodep !='***************' "
                + "and ub_inei is not null "
                + "group by ubdep,nodep order by 1");

        List<DepartamentoBean> list = new ArrayList<DepartamentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DepartamentoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
        public List<DependenciaBean> listNewUpdDependenciaDestinatarioEmi(String coDepen, String deDepen) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT TB.* FROM\n"+        
        "(SELECT DE_DEPENDENCIA,\n" +
        "       CO_DEPENDENCIA,\n" +
        "       DE_CORTA_DEPEN\n" +
        "  FROM IDOSGD.RHTM_DEPENDENCIA\n" +
        " WHERE co_nivel <> '6'\n" +
        "   AND IN_BAJA = '0'\n" +
        "   AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%' )\n" +
        "UNION\n" +
        "SELECT DE_DEPENDENCIA,\n" +
        "       CO_DEPENDENCIA,\n" +
        "       DE_CORTA_DEPEN\n" +
        "  FROM IDOSGD.RHTM_DEPENDENCIA\n" +
        " WHERE co_nivel = '6'\n" +
        "   AND IN_BAJA = '0'\n" +
        "   AND (CO_DEPEN_PADRE in ( select CO_DEPEN_PADRE co_dep from IDOSGD.RHTM_DEPENDENCIA where CO_DEPENDENCIA = ?)\n" +
        "       OR ? IN (CO_DEPENDENCIA, CO_DEPEN_PADRE))\n" +
        "   AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%')\n" +
        " ) TB \n" +
        "UNION\n" +
        "SELECT '[Elija Opción]','','' ORDER BY 1 \n" );

        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), 
                    new Object[]{deDepen+"",deDepen+"",
            coDepen+"",coDepen+"",deDepen+"",deDepen+""});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
        
    @Override
    public List<ProvinciaBean> listProvincia(String coDep){
        StringBuffer sql = new StringBuffer();

        sql.append("select ubprv coProvincia, noprv noProvincia from IDOSGD.idtubias where ubdep=? and ubprv not in('00') and rtrim(noprv) is not null group by ubprv,noprv order by 2");

        List<ProvinciaBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(),new Object[]{coDep}, BeanPropertyRowMapper.newInstance(ProvinciaBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;      
    }    
    
    @Override
    public List<DistritoBean> listDistrito(String coDep,String coDis){
        StringBuffer sql = new StringBuffer();
        sql.append("select ubdis coDistrito, NODIS noDistrito from IDOSGD.idtubias where ubdep=? and ubprv=? and ubdis not in('00') and rtrim(NODIS) is not null group by ubdis,NODIS order by 2");
        List<DistritoBean> list;
        try {
            list = this.jdbcTemplate.query(sql.toString(),new Object[]{coDep,coDis}, BeanPropertyRowMapper.newInstance(DistritoBean.class));
        }catch (EmptyResultDataAccessException e) {
            e.printStackTrace();
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;       
    }    
}
