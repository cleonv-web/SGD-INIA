/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

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
import pe.gob.onpe.tramitedoc.bean.PrioridadDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ProvinciaBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.bean.TipoDestinatarioEmiBean;
import pe.gob.onpe.tramitedoc.bean.TipoDocumentoBean; 
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
        sql.append("SELECT CANO_ANOEJE CANO, CANO_ANOEJE DE_ANIO ");
        sql.append("FROM IDOSGD.SI_MAE_ANO_EJECUCION UNION SELECT '.:TODOS.:', ");
        sql.append("NULL FROM DUAL ORDER BY 1 DESC ");
        
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
        sql.append("SELECT DE_EST,CO_EST ");
        sql.append("FROM IDOSGD.TDTR_ESTADOS ");
        sql.append("WHERE DE_TAB = ? ");
        sql.append("UNION ");
        sql.append("SELECT '[TODOS]',NULL FROM DUAL ORDER BY CO_EST ");
        
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
        sql.append("SELECT '[TODOS]' as DE_EST,NULL as CO_EST FROM DUAL ");
        sql.append("UNION ");
        sql.append("SELECT DE_EST,CO_EST ");
        sql.append("FROM IDOSGD.TDTR_ESTADOS ");
        sql.append("WHERE DE_TAB = ? ORDER BY CO_EST DESC");
        
                
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

        sql.append("SELECT DE_PRI,CO_PRI ");
        sql.append("FROM IDOSGD.TDTR_PRIORIDAD ");
        sql.append("UNION ");
        sql.append("SELECT '.:TODOS.:',NULL FROM DUAL ");
        
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
        
        sql.append("SELECT DE_TIP_DOC,CO_TIP_DOC ");
        sql.append("FROM "
                + "(\n" +
                    "SELECT DISTINCT\n" +
                    "A.CO_DEP CO_DEP_DES ,CO_DOC CO_TIP_DOC , B.CDOC_DESDOC DE_TIP_DOC\n" +
                    "from IDOSGD.tdtx_resumen_doc A , IDOSGD.SI_MAE_TIPO_DOC B\n" +
                    "WHERE ti_tab = 'D'\n" +
                    "AND A.CO_DOC = B.CDOC_TIPDOC)");
        sql.append("WHERE CO_DEP_DES = ? "); 
        sql.append("UNION "); 
        sql.append("SELECT '.:TODOS:.' DESCRI ,NULL CODIGO FROM DUAL ORDER BY 1 "); 
        
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
        public List<TipoDocumentoBean> listTipoDocumentoEmi(String coDependencia) {
        StringBuilder sql = new StringBuilder();        
        sql.append("SELECT CDOC_DESDOC DE_TIP_DOC,CDOC_TIPDOC CO_TIP_DOC ");
        sql.append("FROM "
                + "(SELECT  A.CDOC_TIPDOC, B.CO_DEP, UPPER(A.CDOC_DESDOC) CDOC_DESDOC,A.CDOC_GRUPO\n" +
                    "FROM IDOSGD.SI_MAE_TIPO_DOC A,IDOSGD.SITM_DOC_DEPENDENCIA B\n" +
                    "WHERE A.CDOC_INDBAJ ='0'\n" +
                    "AND A.CDOC_TIPDOC = B.CO_TIP_DOC)");
        sql.append(" WHERE CO_DEP = ? "); 
        sql.append("UNION "); 
        sql.append("SELECT '.:TODOS:.' DESCRI ,NULL CODIGO FROM DUAL ORDER BY 1 "); 
        
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
        sql.append("SELECT DISTINCT A.CO_EMP_EMI codDep,E.CEMP_APEPAT ||' '|| E.CEMP_APEMAT ||' '|| E.CEMP_DENOM descrip\n" +
                    "FROM IDOSGD.TDTV_REMITOS A \n" +
                    "INNER JOIN  RHTM_PER_EMPLEADOS E ON E.CEMP_CODEMP=A.CO_EMP_EMI\n" +
                    "WHERE  A.TI_EMI='01' \n" +
                    "AND A.ES_ELI = '0'\n" +
                    "AND A.CO_GRU = '1' \n" );
        sql.append(" AND  A.CO_DEP_EMI = ? "); 
        sql.append("UNION "); 
        sql.append("SELECT  NULL CODIGO  ,'.:TODOS:.' DESCRI FROM DUAL ORDER BY 1 desc "); 
        
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
        public List<ExpedienteBean> listExpedientes(String coDependencia) {
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT DE_EXP, CO_EXP ");
        sql.append("FROM IDOSGD.TDTR_EXPEDIENTE ");
        sql.append("WHERE CO_DEP = ? ");
        sql.append("UNION SELECT '.: TODOS :.', NULL FROM DUAL ");
        sql.append("UNION SELECT '.:SIN EXPEDIENTE:.', 'SINEX' FROM DUAL ORDER BY 1 ");
        
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
        
        sql.append("select distinct   ");
        sql.append("	DE_DEPENDENCIA descrip,");
        sql.append("	CO_DEPENDENCIA cod_dep,");
        sql.append("	b.de_corta_depen");
        sql.append("  from IDOSGD.tdtx_resumen_dep a , IDOSGD.RHTM_DEPENDENCIA b");
        sql.append(" where /* a.nu_ann = ? and */");
        sql.append("    a.co_dep_ref = ?/*:global.vcod_dep*/");
        sql.append("   and a.ti_tab='D'");
        sql.append("   and a.ti_emi = '01'");
        sql.append("   AND (a.co_emp_res = decode(?/*:global.ti_acceso*/, 0, a.co_emp_res, ?/*:global.USER*/) ");
        sql.append("	   OR ( a.co_emp_res IS NULL )  )");
        sql.append("   and a.CO_DEP = b.CO_DEPENDENCIA");
        sql.append(" union  ");
        sql.append("select distinct   ");
        sql.append("  DECODE(a.ti_emi,");
        sql.append("	'02', ' [PROVEEDORES]',");
        sql.append("	'03', ' [CIUDADANOS]',");
        sql.append("	'04', ' [OTROS]',");
        sql.append("	'05', ' [PERSONALES]') descrip,");
        sql.append("  a.co_dep,");
        sql.append("  NULL");
        sql.append("  from IDOSGD.tdtx_resumen_dep a  ");
        sql.append(" where /*  a.nu_ann = ? and */");
        sql.append("    a.co_dep_ref = ?/*:global.vcod_dep*/");
        sql.append("   and a.ti_tab='D'");
        sql.append("   and a.ti_emi <> '01'");
        sql.append("   AND (a.co_emp_res = decode(?/*:global.ti_acceso*/, 0, a.co_emp_res, ?/*:global.USER*/) ");
        sql.append("	   OR ( a.co_emp_res IS NULL )  )");
        sql.append(" UNION");
        sql.append(" SELECT ' [TODOS]',");
        sql.append("	   NULL,");
        sql.append("       NULL");
        sql.append("  FROM dual");
        sql.append(" ORDER BY 1");

        
        List<RemitenteBean> list = new ArrayList<RemitenteBean>();

        try {
//            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{annio,usuarioConfigBean.getCoDep(),
//            usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp(),annio,usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()});
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{
            usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp(),
            usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }        

    @Override
        public List<DestinatarioBean> listDestinatario(String annio, String coDependencia,String ptiAcceso,String pcoEmpleado) {
        StringBuffer sql = new StringBuffer();
        boolean bquery=false;
        
        if(ptiAcceso!=null&&(ptiAcceso.equals("0")||ptiAcceso.equals("2"))){
            sql.append("SELECT SUBSTR(PK_SGD_DESCRIPCION.DE_NOM_EMP(CO_EMP_RES),1,200) nombres , CO_EMP_RES CO_EMP_DES        ");
            sql.append("FROM ( select DISTINCT CO_EMP_RES ");
            sql.append("		 from IDOSGD.TDTX_RESUMEN_DEP");
            //sql.append("		where NU_ANN = ?/*:B_01_ANN.DE_ANIO*/");
            sql.append("		  where co_dep_REF = ?/*:global.vcod_dep*/");
            sql.append("		  AND TI_TAB = 'D') A");
            sql.append(" UNION");
            sql.append(" SELECT ' [TODOS]',");
            sql.append("	   NULL");
            sql.append("  FROM dual");
            sql.append(" ORDER BY 1"); 
            bquery=true;
        }else{//personal
            sql.append("SELECT SUBSTR(PK_SGD_DESCRIPCION.DE_NOM_EMP(CO_EMP_RES),1,200) nombres , CO_EMP_RES CO_EMP_DES        ");
            sql.append("FROM ( select DISTINCT CO_EMP_RES ");
            sql.append("		 from IDOSGD.TDTX_RESUMEN_DEP");
            //YUAL
            //sql.append("		where NU_ANN = ?/*:B_01_ANN.DE_ANIO*/");
            sql.append("		  where co_dep_REF = ?/*:global.vcod_dep*/");
            sql.append("		  AND TI_TAB = 'D' and CO_EMP_RES=?) A");
            sql.append(" UNION");
            sql.append(" SELECT ' [TODOS]',");
            sql.append("	   NULL");
            sql.append("  FROM dual");
            sql.append(" ORDER BY 1");              
        }

        
        List<DestinatarioBean> list = new ArrayList<DestinatarioBean>();

        try {
            if(bquery){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioBean.class), new Object[]{coDependencia});    
            }else{
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
       
        sql.append("SELECT e.cemp_apepat,e.cemp_apemat,e.CEMP_DENOM, e.CEMP_CODEMP\n" +
        "FROM IDOSGD.RHTM_PER_EMPLEADOS e\n" +
        "where e.CEMP_EST_EMP = '1'\n" +
        "and (CEMP_CO_DEPEND = ?\n" +
        "or CO_DEPENDENCIA = ?\n" +
        ")\n" +
        "ORDER BY 1");
        
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
    
    //Jhon
    @Override
        public List<RemitenteBean> listReferenciaOrigenVB(String annio,String ptiAcceso, UsuarioConfigBean usuarioConfigBean) {
        StringBuffer sql = new StringBuffer();      
        sql.append("SELECT DECODE(c.ti_emi,\n" +
                "              '01', d.de_dependencia,'05', d.de_dependencia,\n" +
                "              '02', ' [PROVEEDORES]',\n" +
                "              '03', ' [CIUDADANOS]',\n" +
                "              '04', ' [OTROS]') descrip,\n" +
                "       DECODE(c.ti_emi,\n" +
                "              '01', c.co_dep_emi,'05', c.co_dep_emi,\n" +
                "              '02', 'P',\n" +
                "              '03', 'C',\n" +
                "              '04', 'O') cod_dep,\n" +
                "              d.de_corta_depen\n" +
                "  FROM IDOSGD.tdtr_referencia a\n" +
                "       INNER JOIN IDOSGD.tdtv_remitos b ON a.nu_ann = b.nu_ann AND a.nu_emi = b.nu_emi\n" +
                "       INNER JOIN IDOSGD.tdtv_remitos c ON a.nu_ann_ref = c.nu_ann AND a.nu_emi_ref = c.nu_emi\n" +
                "       INNER JOIN IDOSGD.rhtm_dependencia d ON c.co_dep_emi = d.co_dependencia\n" +
                "       INNER JOIN IDOSGD.TDTV_PERSONAL_VB e ON e.nu_emi = b.nu_emi\n" +
                 " WHERE b.es_eli = '0'\n" + 
                "   AND e.co_dep = ?\n"+       
                "   AND e.co_emp_vb = ?\n");
                 if(ptiAcceso.equals("C") && ptiAcceso !=null){
                    sql.append(" AND b.co_gru IN ('1') \n ");       
                 }else{
                    sql.append(" AND b.co_gru IN ('1','2') \n ");
                 }
                 if(annio !=null && annio.isEmpty()){
                   sql.append(" ");  
                 } else {
                   sql.append(" AND e.nu_ann = ? \n");
                 }
        sql.append(" GROUP BY c.ti_emi,\n" +
                "          c.co_dep_emi,\n" +
                "          d.de_dependencia,\n" +
                "          d.de_corta_depen\n" +
                " UNION\n" +
                "SELECT ' [TODOS]',\n" +
                "       NULL,\n" +
                "       NULL\n" +
                "  FROM dual\n" +
                " ORDER BY 1");

        
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
        sql.append(" select distinct   ");
        sql.append(" 	b.DE_DEPENDENCIA descrip,");
        sql.append(" 	b.CO_DEPENDENCIA cod_dep,");
        sql.append(" 	b.DE_CORTA_DEPEN");
        sql.append("   from IDOSGD.tdtx_resumen_dep a , IDOSGD.RHTM_DEPENDENCIA b");
        sql.append("  where ");
        if(annio.length() == 1 || annio == "0"){
           sql.append(" ");
        } else {
           sql.append("  a.nu_ann = ? and/*:b_01_ann.de_anio*/"); 
        }
        sql.append("    a.co_dep = ?/*:GLOBAL.vcod_dep*/");
        sql.append("    and a.ti_tab='R'");
        sql.append("    and a.ti_emi = '01'");
        sql.append("    and a.co_emp_res = DECODE(?/*:GLOBAL.TI_ACCESO*/, 0, a.co_emp_res, ?/*:GLOBAL.USER*/) ");
        sql.append("    and a.CO_DEP_REF = b.CO_DEPENDENCIA");
        sql.append(" union  ");
        sql.append(" select distinct   ");
        sql.append(" 	DECODE(a.ti_emi,");
        sql.append(" 				  '02', ' [PROVEEDORES]',");
        sql.append(" 				  '03', ' [CIUDADANOS]',");
        sql.append(" 				  '04', ' [OTROS]',");
        sql.append(" 				  '05', ' [PERSONALES]') descrip,");
        sql.append(" 	a.co_dep_ref cod_dep,");
        sql.append(" 	NULL");
        sql.append("   from IDOSGD.tdtx_resumen_dep a  ");
        sql.append("  where ");
        if(annio.length() == 1 || annio == "0"){
           sql.append(" ");
        } else {
           sql.append("  a.nu_ann = ? and /*:b_01_ann.de_anio*/"); 
        }
        sql.append("    a.co_dep = ?/*:GLOBAL.vcod_dep*/");
        sql.append("    and a.ti_tab='R'");
        sql.append("    and a.ti_emi <> '01'");
        sql.append("    and a.co_emp_res = DECODE(?/*:GLOBAL.TI_ACCESO*/, 0, a.co_emp_res, ?/*:GLOBAL.USER*/) ");
        sql.append("  UNION");
        sql.append(" SELECT ' [TODOS]',");
        sql.append(" 	   NULL,");
        sql.append(" 	   NULL");
        sql.append("   FROM dual");
        sql.append("  ORDER BY 1");

        
        List<RemitenteBean> list = new ArrayList<RemitenteBean>();

        try {
            if(annio.length() == 1 || annio == "0"){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class), new Object[]{usuarioConfigBean.getCoDep(),
                usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp(),usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()});
             } else {
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
        
        sql.append(" select distinct   ");
        sql.append(" 	b.DE_DEPENDENCIA deDependencia,");
        sql.append(" 	a.co_dep_ref coDependencia,");
        sql.append(" 	b.DE_CORTA_DEPEN");
        sql.append("   from IDOSGD.tdtx_resumen_dep a , IDOSGD.RHTM_DEPENDENCIA b");
        sql.append("  where ");
        if(annio.length() == 1 || annio == "0"){
           sql.append(" ");
        } else {
           sql.append("  a.nu_ann = ? and/*:b_01_ann.de_anio*/"); 
        }
        sql.append("    a.co_dep = ?/*:global.vcod_dep*/");
        sql.append("    and a.ti_tab='D'");
        sql.append("    and a.ti_emi = '01'");
        sql.append("    and a.co_emp_res = DECODE(?/*:GLOBAL.TI_ACCESO*/, 0, a.co_emp_res, 2, a.co_emp_res, ?/*:GLOBAL.USER*/) ");
        sql.append("    and a.CO_DEP_ref = b.CO_DEPENDENCIA");
        sql.append(" union  ");
        sql.append(" select distinct   ");
        sql.append("   DECODE(a.ti_emi,");
        sql.append(" '02', ' [PROVEEDORES]',");
        sql.append(" '03', ' [CIUDADANOS]',");
        sql.append(" '04', ' [OTROS]',");
        sql.append(" '05', ' [PERSONALES]') deDependencia,");
        sql.append("   a.co_dep_ref coDependencia,");
        sql.append("   NULL");
        sql.append("   from IDOSGD.tdtx_resumen_dep a  ");
        sql.append("  where ");
        if(annio.length() == 1 || annio == "0"){
           sql.append(" ");
        } else {
           sql.append("  a.nu_ann = ? and/*:b_01_ann.de_anio*/"); 
        }
        sql.append("    a.co_dep = ?/*:global.vcod_dep*/");
        sql.append("    and a.ti_tab='D'");
        sql.append("    and a.ti_emi <> '01'");
        sql.append("    and a.co_emp_res = DECODE(?/*:GLOBAL.TI_ACCESO*/, 0, a.co_emp_res, 2, a.co_emp_res, ?/*:GLOBAL.USER*/) ");
        sql.append("  UNION");
        sql.append(" SELECT ' [TODOS]',");
        sql.append(" 	   NULL,");
        sql.append(" 	   NULL");
        sql.append("   FROM dual");
        sql.append("  ORDER BY 1");

        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            if(annio.length() == 1 || annio == "0"){
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
       
        if(ptiAcceso!=null&&(ptiAcceso.equals("0")||ptiAcceso.equals("2"))){
            sql.append(" SELECT e.cemp_apepat||' '||e.cemp_apemat||' '||e.cemp_denom nombre, e.CEMP_CODEMP");
            sql.append(" FROM IDOSGD.RHTM_PER_EMPLEADOS e");
            sql.append(" where e.CEMP_EST_EMP = '1'");
            sql.append(" AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA from IDOSGD.RHTM_DEPENDENCIA d where d.CO_DEPENDENCIA=?/*:global.VCOD_DEP*/ or d.CO_DEPEN_PADRE=?/*:global.VCOD_DEP*/) ");
            sql.append(" OR CO_DEPENDENCIA =  ?/*:B_02.CO_DEP_EMI */)");
            sql.append(" UNION");
            sql.append(" SELECT e.cemp_apepat||' '||e.cemp_apemat||' '||e.cemp_denom nombre, e.CEMP_CODEMP");
            sql.append(" FROM IDOSGD.RHTM_PER_EMPLEADOS e");
            sql.append(" where e.CEMP_EST_EMP = '1'");
            sql.append(" AND cemp_codemp ");
            sql.append(" in ( select co_emp from IDOSGD.tdtx_dependencia_empleado  where co_dep=?/*:global.VCOD_DEP*/ and es_emp='0')");
            sql.append(" UNION ");
            sql.append(" SELECT ' [TODOS]', NULL FROM DUAL");
            sql.append(" ORDER BY 1");            
            bquery=true;
        }else{//personal
            sql.append(" SELECT e.cemp_apepat||' '||e.cemp_apemat||' '||e.cemp_denom nombre, e.CEMP_CODEMP");
            sql.append(" FROM IDOSGD.RHTM_PER_EMPLEADOS e");
            sql.append(" where e.CEMP_EST_EMP = '1'");
            sql.append(" AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA from IDOSGD.RHTM_DEPENDENCIA d where d.CO_DEPENDENCIA=?/*:global.VCOD_DEP*/ or d.CO_DEPEN_PADRE=?/*:global.VCOD_DEP*/) ");
            sql.append(" OR CO_DEPENDENCIA =  ?/*:B_02.CO_DEP_EMI */) and e.CEMP_CODEMP=?");
            sql.append(" UNION ");
            sql.append(" SELECT ' [TODOS]', NULL FROM DUAL");
            sql.append(" ORDER BY 1");              
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
    public List<EmpleadoBean> listEmpleadoElaboradoPorMP(String pcodDependencia,String ptiAcceso,String pcoEmpleado){
        StringBuffer sql = new StringBuffer();
        boolean bquery=false;
       
            sql.append(" SELECT DISTINCT DE_EMP_RES NOMBRE, CO_EMP_RES CEMP_CODEMP ");
            sql.append(" FROM (SELECT ");
            sql.append(" PK_SGD_DESCRIPCION.DE_NOM_EMP(R.CO_EMP_RES) DE_EMP_RES, R.CO_EMP_RES ");
            sql.append(" FROM IDOSGD.TDTV_REMITOS R  ");
            sql.append(" INNER JOIN IDOSGD.TDTV_DESTINOS D ON D.NU_ANN=R.NU_ANN AND D.NU_EMI=R.NU_EMI ");
            sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR ON RR.NU_ANN=R.NU_ANN AND RR.NU_EMI=R.NU_EMI ");
            sql.append(" INNER JOIN IDOSGD.TDTR_MOTIVO M ON  M.CO_MOT=D.CO_MOT ");
            sql.append(" INNER JOIN IDOSGD.TDTC_EXPEDIENTE C ON C.NU_ANN_EXP=R.NU_ANN_EXP  AND C.NU_SEC_EXP=R.NU_SEC_EXP ");
            sql.append(" INNER JOIN (SELECT Y.NU_ANN, Y.NU_EMI, Y.FE_FINALIZA,PK_SGD_DESCRIPCION.FU_DIA_TRA (TRUNC (SYSDATE),Y.FE_FINALIZA) NU_DIA_FAL FROM (SELECT X.NU_ANN, X.NU_EMI, ");
            sql.append(" TRUNC (X.FE_EMI)+ PK_SGD_DESCRIPCION.FU_DIA_MAS (TRUNC (X.FE_EMI),NU_DIA_ATE) AS FE_FINALIZA  ");
            sql.append(" FROM TDTV_REMITOS X) Y)  E ON  E.NU_ANN=R.NU_ANN  AND E.NU_EMI=R.NU_EMI ");
            sql.append(" WHERE R.CO_GRU='3' ");
            sql.append(" AND R.ES_ELI='0'");
            sql.append(" AND D.ES_ELI='0'"); 
            sql.append(" AND R.ES_DOC_EMI NOT IN ('5', '9', '7')");
            sql.append(" AND R.TI_EMI IN ('02','03','04')");
            sql.append(" AND R.CO_DEP_EMI = ? ");
            if(ptiAcceso!=null&&(ptiAcceso.equals("0")||ptiAcceso.equals("2"))){
               sql.append("  "); 
            } else {
               sql.append(" AND R.CO_EMP_RES = ? ");  
               bquery = true;
            }
            sql.append(" UNION ");
            sql.append(" SELECT ' [TODOS]', NULL FROM DUAL");
            sql.append(" ");              
             sql.append(" )X ORDER BY 1");

        
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        
        try {
             if(bquery){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcoEmpleado
                                                                                                                    });                
             }else{
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia
                                                                                                                });                
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
       // boolean bquery=false;
           
        sql.append(" SELECT DISTINCT  E.CEMP_APEPAT || ' ' || E.CEMP_APEMAT || ' ' || E.CEMP_DENOM  NOMBRE, E.CEMP_CODEMP");
        sql.append(" FROM IDOSGD.TDTV_REMITOS RE");
        sql.append(" INNER JOIN IDOSGD.TDTV_PERSONAL_VB VB ON  RE.NU_ANN=VB.NU_ANN AND RE.NU_EMI=VB.NU_EMI ");
        sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR ON RR.NU_ANN=RE.NU_ANN AND RR.NU_EMI=RE.NU_EMI ");
        sql.append(" INNER JOIN IDOSGD.RHTM_PER_EMPLEADOS E ON E.CEMP_CODEMP = RE.CO_EMP_RES");
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
        sql.append(" SELECT ' [TODOS]', NULL FROM DUAL");
        sql.append(" ORDER BY 1");    
           
         
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        
        try {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcoEmpleado});                
            //else{
//            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class), new Object[]{pcodDependencia,pcodDependencia,
//                                                                                                                pcodDependencia,pcoEmpleado});                
            
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        
        return list;        
    }
    
        //YUAL
        @Override
        public List<EmpleadoBean> listEmpleadoDestino(String pcodDependencia,String ptiAcceso,String pcoEmpleado){
         StringBuffer sql = new StringBuffer();
        boolean bquery=false;
       
        if(ptiAcceso!=null&&(ptiAcceso.equals("0")||ptiAcceso.equals("2"))){
            sql.append(" SELECT e.cemp_apepat||' '||e.cemp_apemat||' '||e.cemp_denom nombre, e.CEMP_CODEMP");
            sql.append(" FROM IDOSGD.RHTM_PER_EMPLEADOS e");
            sql.append(" where e.CEMP_EST_EMP = '1'");
            sql.append(" AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA from IDOSGD.RHTM_DEPENDENCIA d where d.CO_DEPENDENCIA=? or d.CO_DEPEN_PADRE=?) ");
            sql.append(" OR CO_DEPENDENCIA =  ?)");
            sql.append(" UNION");
            sql.append(" SELECT e.cemp_apepat||' '||e.cemp_apemat||' '||e.cemp_denom nombre, e.CEMP_CODEMP");
            sql.append(" FROM IDOSGD.RHTM_PER_EMPLEADOS e");
            sql.append(" where e.CEMP_EST_EMP = '1'");
            sql.append(" AND cemp_codemp ");
            sql.append(" in ( select co_emp from IDOSGD.tdtx_dependencia_empleado  where co_dep=? and es_emp='0')");
            sql.append(" UNION ");
            sql.append(" SELECT ' [TODOS]', NULL FROM DUAL");
            sql.append(" ORDER BY 1");            
            bquery=true;
        }else{//personal
            sql.append(" SELECT e.cemp_apepat||' '||e.cemp_apemat||' '||e.cemp_denom nombre, e.CEMP_CODEMP");
            sql.append(" FROM IDOSGD.RHTM_PER_EMPLEADOS e");
            sql.append(" where e.CEMP_EST_EMP = '1'");
            sql.append(" AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA from IDOSGD.RHTM_DEPENDENCIA d where d.CO_DEPENDENCIA=?/*:global.VCOD_DEP*/ or d.CO_DEPEN_PADRE=?) ");
            sql.append(" OR CO_DEPENDENCIA =  ?) and e.CEMP_CODEMP=?");
            sql.append(" UNION ");
            sql.append(" SELECT ' [TODOS]', NULL FROM DUAL");
            sql.append(" ORDER BY 1");              
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
        
        sql.append("SELECT DE_DEPENDENCIA,\n" +
        "       CO_DEPENDENCIA,\n" +
        "       DE_CORTA_DEPEN\n" +
        "  FROM IDOSGD.RHTM_DEPENDENCIA\n" +
        " WHERE co_nivel <> '6'\n" +
        "   AND IN_BAJA = '0'\n" +
        "   AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%' )\n" +
        "UNION\n" +
        "SELECT DE_DEPENDENCIA,\n" +
        "       CO_DEPENDENCIA,\n" +
        "       DE_CORTA_DEPEN\n" +
        "  FROM IDOSGD.RHTM_DEPENDENCIA\n" +
        " WHERE co_nivel = '6'\n" +
        "   AND IN_BAJA = '0'\n" +
        "   AND (CO_DEPEN_PADRE in ( select CO_DEPEN_PADRE co_dep from RHTM_DEPENDENCIA where CO_DEPENDENCIA = ?)\n" +
        "       OR ? IN (CO_DEPENDENCIA, CO_DEPEN_PADRE))\n" +
        "   AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%')\n" +
        " ORDER BY 1");

        
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
        sql.append("SELECT  UPPER(A.CDOC_DESDOC) DE_TIP_DOC, A.CDOC_TIPDOC CO_TIP_DOC,CDOC_GRUPO \n" +
            "FROM IDOSGD.SI_MAE_TIPO_DOC A,IDOSGD.SITM_DOC_DEPENDENCIA B\n" +
            "WHERE A.CDOC_INDBAJ ='0'\n" +
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

        sql.append("SELECT DE_PRI,CO_PRI ");
        sql.append("FROM TDTR_PRIORIDAD ");
        
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
        
//        sql.append("select a.CO_DOC CO_TIP_DOC , B.CDOC_DESDOC DE_TIP_DOC\n" +
//                    "from (\n" +
//                    "    select DISTINCT co_doc \n" +
//                    "    from IDOSGD.tdtx_resumen_doc\n" +
//                    "    where CO_DEP = ?)a, SI_MAE_TIPO_DOC b\n" +
//                    "WHERE  A.CO_DOC = B.CDOC_TIPDOC\n" +
//                    "order by 2"); 
        
        
        sql.append("select a.CO_DOC CO_TIP_DOC , B.CDOC_DESDOC DE_TIP_DOC\n" +
"                    from (\n" +
"                        select DISTINCT co_doc \n" +
"                        from IDOSGD.tdtx_resumen_doc\n" +
"                        where CO_DEP = ?)a, SI_MAE_TIPO_DOC b\n" +
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
        
        sql.append("select CELE_DESELE de_destinatario,CELE_CODELE co_destinatario from IDOSGD.SI_ELEMENTO WHERE CTAB_CODTAB='TIP_DESTINO' AND CELE_CODELE not in  ('05')"); 
        
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
        
        sql.append("SELECT DE_GRU_DES de_grupo,CO_GRU_DES co_grupo FROM IDOSGD.TDTM_GRU_DES WHERE CO_DEP=? AND ES_GRU_DES='1' ORDER BY 2"); 
        
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
        
        sql.append("SELECT a.de_mot, a.co_mot FROM IDOSGD.tdtr_motivo a, IDOSGD.tdtx_moti_docu_depe b WHERE a.co_mot = b.co_mot AND b.co_dep = ?/*:B_02.CO_DEP_EMI*/ AND b.co_tip_doc = ?/*:b_02.co_tip_doc_adm*/ UNION SELECT de_mot, co_mot FROM tdtr_motivo  where co_mot in ('0','1') ORDER BY 1"); 
        
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
        sql.append("SELECT DE_DEPENDENCIA, CO_DEPENDENCIA \n" +
                    "FROM IDOSGD.RHTM_DEPENDENCIA \n" +
                    "where CO_DEPENDENCIA in (SELECT ? FROM DUAL \n" +
                    "                        UNION SELECT co_dep_ref FROM tdtx_referencia  \n" +
                    "                              WHERE co_dep_emi = ?\n" +
                    "                              AND ti_emi = 'R' \n" +
                    "                              AND es_ref = '1')"); 
        
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
        sql.append("select DE_NOMBRE_LOCAL DE_LOCAL,CCOD_LOCAL CO_LOCAL from IDOSGD.SI_MAE_LOCAL WHERE CCOD_LOCAL IN (SELECT CO_LOC FROM SITM_LOCAL_DEPENDENCIA WHERE CO_DEP = ?)"); 
        
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
        sql.append("select DE_NOMBRE_LOCAL DE_LOCAL,CCOD_LOCAL CO_LOCAL from IDOSGD.SI_MAE_LOCAL ORDER BY 2"); 
        
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

    sql.append("SELECT DECODE(c.ti_emi,\n" +
                "              '01', d.de_dependencia,'05', d.de_dependencia,\n" +
                "              '02', ' [PROVEEDORES]',\n" +
                "              '03', ' [CIUDADANOS]',\n" +
                "              '04', ' [OTROS]') descrip,\n" +
                "       DECODE(c.ti_emi,\n" +
                "              '01', c.co_dep_emi,'05', c.co_dep_emi,\n" +
                "              '02', 'P',\n" +
                "              '03', 'C',\n" +
                "              '04', 'O') cod_dep,\n" +
                "              d.de_corta_depen\n" +
                "  FROM IDOSGD.tdtr_referencia a,\n" +
                "       IDOSGD.tdtv_remitos    b,\n" +
                "       IDOSGD.tdtv_remitos    c,\n" +
                "       IDOSGD.rhtm_dependencia  d\n" +
                " WHERE b.es_eli = '0'\n" +
                "   AND b.co_gru = '2' \n" + //  AND b.co_gru = '1'  /* /* JHON -- La consulta en cuestión son los documentos del profesional por ende el valor correspondiente es '2'--*/ 
                "   AND a.nu_ann = b.nu_ann\n" +
                "   AND a.nu_emi = b.nu_emi\n" +
                "   AND a.nu_ann_ref = c.nu_ann\n" +
                "   AND a.nu_emi_ref = c.nu_emi\n" +
                "   AND c.co_dep_emi = d.co_dependencia\n" +
                "   AND b.co_emp_emi = ?/*:GLOBAL.USER */\n" +
                " GROUP BY c.ti_emi,\n" +
                "          c.co_dep_emi,\n" +
                "          d.de_dependencia,\n" +
                "          d.de_corta_depen\n" +
                " UNION\n" +
                "SELECT ' [TODOS]',\n" +
                "       NULL,\n" +
                "       NULL\n" +
                "  FROM dual\n" +
                " ORDER BY 1");


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
        
        sql.append("SELECT DECODE(c.ti_des,\n" +
                    "              '01', d.de_dependencia,\n" +
                    "              '02', ' [PROVEEDORES]',\n" +
                    "              '03', ' [CIUDADANOS]',\n" +
                    "              '04', ' [OTROS]') deDependencia,\n" +
                    "       DECODE(c.ti_des,\n" +
                    "              '01', c.co_dep_des,\n" +
                    "              '02', 'P',\n" +
                    "              '03', 'C',\n" +
                    "              '04', 'O') coDependencia,\n" +
                    "       d.DE_CORTA_DEPEN\n" +
                    "  FROM IDOSGD.tdtv_remitos    b,\n" +
                    "       IDOSGD.tdtv_destinos   c,\n" +
                    "       IDOSGD.rhtm_dependencia  d\n" +
                    " WHERE b.es_eli = '0'\n" +
                    "   AND b.co_gru = '2'\n" +  //  AND b.co_gru = '1'  /* JHON -- La consulta en cuestión son los documentos del profesional por ende el valor correspondiente es '2'--*/ 
                    "   AND b.nu_ann = c.nu_ann\n" +               
                    "   AND b.nu_emi = c.nu_emi\n" +
                    "   AND c.co_dep_des = d.co_dependencia (+)\n" +
                    "   AND b.co_emp_emi = ? \n" +
                    " GROUP BY c.ti_des,\n" +
                    "          c.co_dep_des,\n" +
                    "          d.de_dependencia,\n" +
                    "          d.de_corta_depen\n" +
                    " UNION\n" +
                    "SELECT ' [TODOS]',\n" +
                    "       NULL,\n" +
                    "       NULL\n" +
                    "  FROM dual\n" +
                    " ORDER BY 1");

        
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
        sql.append("select \n" +
                    "co_dependencia,\n" +
                    "co_empleado,\n" +
                    "de_dependencia tituloDep,\n"+
                    "PK_SGD_DESCRIPCION.DE_NOM_EMP(co_empleado) de_dependencia,\n" +
                    "de_sigla \n" +
                    "from IDOSGD.rhtm_dependencia \n" +
                    "where co_dependencia = ?");
        
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
        sql.append("SELECT DE_EST_MP DE_EST,CO_EST \n" +
                    "FROM IDOSGD.TDTR_ESTADOS \n" +
                    "WHERE DE_TAB=?\n" +
                    "AND DE_EST_MP IS NOT NULL UNION SELECT '.: TODOS :.',NULL \n" +
                    "FROM DUAL \n" +
                    "ORDER BY CO_EST");
        
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
        
        sql.append("select CELE_DESELE de_destinatario,CELE_CODELE co_destinatario from IDOSGD.SI_ELEMENTO \n" +
                    "WHERE CTAB_CODTAB='TIP_DESTINO' \n" +
                    "AND CELE_CODELE NOT IN ('01','05')\n" +
                    "UNION SELECT '.: TODOS :.',NULL\n" +
                    "FROM DUAL\n" +
                    "ORDER BY 2"); 
        
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
        
        sql.append("SELECT DE_NOMBRE DE_TUPA, CO_PROCESO CO_TUPA\n" +
                    "FROM IDOSGD.TDTR_PROCESOS_EXP \n" +
                    "WHERE TI_PROCESO = '1' \n" +
                    "AND ES_ESTADO = '1'"+
                    "UNION  \n" +
                    "SELECT '[SIN TUPA]','0000' \n" +
                    "FROM DUAL  \n" +
                    "ORDER BY 2"); 
        
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
        
        sql.append("SELECT DE_NOMBRE DE_TUPA, CO_PROCESO CO_TUPA\n" +
                    "FROM IDOSGD.TDTR_PROCESOS_EXP \n" +
                    "WHERE TI_PROCESO = '1' \n" +
                    "UNION  \n" +
                    "SELECT '[SIN TUPA]','0000' \n" +
                    "FROM DUAL  \n" +
                    "ORDER BY 2"); 
        
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
        sql.append("select DE_NOMBRE_LOCAL DE_LOCAL,CCOD_LOCAL CO_LOCAL from SI_MAE_LOCAL \n" +
                    "union\n" +
                    "select ' [TODOS]' DE_LOCAL,NULL CO_LOCAL\n" +
                    "from dual\n" +
                    "ORDER BY 1"); 

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

        sql.append("select co_est,de_est \n"
                + "from IDOSGD.tdtr_estados \n"
                + "where de_tab = 'CO_ETIQUETA_REC' \n"
                + "UNION SELECT NULL,'.: TODOS :.' \n"
                + "FROM DUAL\n");

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
        
//        sql.append(" select\n" +
//                    "   b.DE_DEPENDENCIA deDependencia,\n" +
//                    "   a.co_dep_ref coDependencia,\n" +
//                    "   b.DE_CORTA_DEPEN\n" +
//                    "   from tdtx_resumen_dep a , RHTM_DEPENDENCIA b\n" +
//                    "  where a.nu_ann = ?\n" +
//                    "    and a.co_dep = ?\n" +
//                    "    and a.ti_tab='D'\n" +
//                    "    and a.ti_emi in ('02','03','04')\n" +
//                    "    and a.CO_DEP_ref = b.CO_DEPENDENCIA\n" +
//                    "  UNION\n" +
//                    " SELECT ' [TODOS]',\n" +
//                    "      NULL,\n" +
//                    "      NULL\n" +
//                    "   FROM dual\n" +
//                    "  ORDER BY 1");
        sql.append( "SELECT DE_DEPENDENCIA deDependencia,\n" +
                    "       CO_DEPENDENCIA coDependencia,\n" +
                    "       DE_CORTA_DEPEN\n" +
                    "FROM IDOSGD.RHTM_DEPENDENCIA\n" +
                    "WHERE IN_BAJA = '0'\n" +
                    "      order by 1"
        );
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
//            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), 
//                    new Object[]{annio,coDep});
        list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class));
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
        
        sql.append("SELECT DE_DEPENDENCIA , CO_DEPENDENCIA , DE_CORTA_DEPEN\n" +
                "FROM IDOSGD.RHTM_DEPENDENCIA \n" +
                "where CO_DEPENDENCIA in \n" +
                "(select CO_DEP from sitm_local_dependencia WHERE CO_LOC=?) UNION select ' [TODOS]',NULL,NULL  FROM DUAL  order by 1");
        
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

        sql.append("select co_est,de_est \n"
                + "from IDOSGD.tdtr_estados \n"
                + "where de_tab = 'VENCIMIENTO' \n"
                + "UNION SELECT NULL,'.: TODOS :.' \n"
                + "FROM DUAL\n");

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
        sql.append("SELECT DE_EST,CO_EST ");
        sql.append("FROM IDOSGD.TDTR_ESTADOS ");
        sql.append("WHERE DE_TAB = ? ");
        sql.append("AND CO_EST NOT IN('5','9') ");
        sql.append("UNION ");
        sql.append("SELECT '.:TODOS.:',NULL FROM DUAL ORDER BY CO_EST ");
        
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
        sql.append("SELECT CELE_CODELE,CELE_DESELE,NELE_NUMSEC,CELE_DESCOR,BL_DOC as archivo \n" +
            "FROM   IDOSGD.SI_ELEMENTO\n" +
            "WHERE  CTAB_CODTAB = ?");

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
        sql.append("SELECT * FROM\n"+        
        "(SELECT DE_DEPENDENCIA,\n" +
        "       CO_DEPENDENCIA,\n" +
        "       DE_CORTA_DEPEN\n" +
        "  FROM IDOSGD.RHTM_DEPENDENCIA\n" +
        " WHERE co_nivel <> '6'\n" +
        "   AND IN_BAJA = '0'\n" +
        "   AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%' )\n" +
        "UNION\n" +
        "SELECT DE_DEPENDENCIA,\n" +
        "       CO_DEPENDENCIA,\n" +
        "       DE_CORTA_DEPEN\n" +
        "  FROM IDOSGD.RHTM_DEPENDENCIA\n" +
        " WHERE co_nivel = '6'\n" +
        "   AND IN_BAJA = '0'\n" +
        "   AND (CO_DEPEN_PADRE in ( select CO_DEPEN_PADRE co_dep from RHTM_DEPENDENCIA where CO_DEPENDENCIA = ?)\n" +
        "       OR ? IN (CO_DEPENDENCIA, CO_DEPEN_PADRE))\n" +
        "   AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%')\n" +
        "ORDER BY 1)\n" +
        "UNION\n" +
        "SELECT '[Elija Opción]','',''\n" +
        "FROM DUAL");

        
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
    public List<ProvinciaBean> listProvincia(String coDep){
                StringBuffer sql = new StringBuffer();

        sql.append("select ubprv coProvincia, noprv noProvincia from IDOSGD.idtubias where ubdep=? and ubprv not in('00') and trim(noprv) is not null group by ubprv,noprv order by 2");

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

        sql.append("select ubdis coDistrito, nodis noDistrito from IDOSGD.idtubias where ubdep=? and ubprv=? and ubdis not in('00') and trim(nodis) is not null group by ubdis,nodis order by 2");
        List<DistritoBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(),new Object[]{coDep,coDis}, BeanPropertyRowMapper.newInstance(DistritoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }    
        
}
