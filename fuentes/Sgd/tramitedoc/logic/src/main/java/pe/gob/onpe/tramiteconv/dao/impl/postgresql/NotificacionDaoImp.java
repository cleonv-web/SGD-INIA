/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.postgresql;


//import java.util.List;
import java.sql.Types;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
//import pe.gob.onpe.tramitedoc.bean.NotificacionBean;
import pe.gob.onpe.tramitedoc.dao.NotificacionDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author ecueva
 */
@Repository("notificacionDao")
public class NotificacionDaoImp extends SimpleJdbcDaoBase implements NotificacionDao{

    /*@Override
    public String insNotificacion(NotificacionBean noti) {
        String vReturn = "NO_OK";
        StringBuilder sqlIns = new StringBuilder();
        sqlIns.append("INSERT INTO IDOSGD.TDTV_NOTIFICACIONES(\n" +
                        "NU_ANN,\n" +
                        "NU_EMI,\n" +
                        "CO_EMP,\n" +
                        "CO_DEP,\n" +
                        "TIPO,\n" +
                        "CO_USER_CRE,\n" +
                        "CO_USER_MOD)\n" +
                        "VALUES(?,?,?,?,?,?,?)");

        try {
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{noti.getNuAnn(), noti.getNuEmi(), noti.getCoEmp(), noti.getCoDep(), noti.getTipo(),
                noti.getCoUser(), noti.getCoUser()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String delNotificacion(NotificacionBean noti) {
        String vReturn = "NO_OK";
        StringBuilder sqlDel = new StringBuilder();
        sqlDel.append("DELETE FROM IDOSGD.TDTV_NOTIFICACIONES\n" +
                "WHERE \n" +
                "NU_ANN=?\n" +
                "AND NU_EMI=?\n" +
                "AND CO_EMP=?\n" +
                "AND CO_DEP=?");
        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{noti.getNuAnn(), noti.getNuEmi(), noti.getCoEmp(), noti.getCoDep()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String updNotificacion(NotificacionBean noti) {
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("UPDATE IDOSGD.TDTV_NOTIFICACIONES \n"
                + "SET EST_ENV = ?, \n"
                + "FE_ENV = SYSDATE,\n"
                + "EMAIL_ENV = ?,\n"
                + "CO_USE_MOD = ?,\n"
                + "FE_USE_MOD = now() \n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND CO_EMP = ? AND CO_DEP = ? AND ESTADO='1'");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{noti.getEstadoEnvio(), noti.getToEmail(), noti.getCoUser(), noti.getNuAnn(), noti.getNuEmi(),
                noti.getCoEmp(), noti.getCoDep()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;    
    }
    
    @Override
    public List<NotificacionBean> getLsNotiPendienteEnvio(String pnuAnn, String pnuEmi){
        StringBuilder sql = new StringBuilder();
        List<NotificacionBean> list = null;
        sql.append("SELECT A.NU_ANN, A.NU_EMI, A.CO_EMP, A.CO_DEP, B.CEMP_EMAIL TO_EMAIL, B.CEMP_APEPAT ||' '|| B.CEMP_APEMAT ||' '|| B.CEMP_DENOM DE_NOM\n" +
                    "FROM IDOSGD.TDTV_NOTIFICACIONES A, IDOSGD.RHTM_PER_EMPLEADOS B\n" +
                    "WHERE \n" +
                    "A.NU_ANN=? AND A.NU_EMI=?\n" +
                    "AND B.CEMP_CODEMP=A.CO_EMP\n" +
                    "AND A.EST_ENV='0' AND A.ESTADO='1'");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(NotificacionBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }*/
    
    @Override
    public DocumentoDatoBean getDatosDoc(String nuAnn, String nuEmi){
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.NU_EMI, A.NU_ANN,TO_CHAR(A.FE_EMI,'DD/MM/YYYY HH24:MI') FECHA_DOC, B.NU_DOC SIGLAS_DOC, A.DE_ASU, A.ES_DOC_EMI, "
                  + "IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(A.CO_TIP_DOC_ADM) TIPO_DOC,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_EMI) DE_DEP_EMI\n" +
                    "FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTX_REMITOS_RESUMEN B\n" +
                    "WHERE A.NU_ANN=? AND A.NU_EMI=?\n" +
                    "AND B.NU_ANN=? AND B.NU_EMI=?");
   
        DocumentoDatoBean docDatoBean = new DocumentoDatoBean();

        try {
            docDatoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDatoBean.class),new Object[]{nuAnn, nuEmi, nuAnn, nuEmi});
        } catch (EmptyResultDataAccessException e) {
            docDatoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docDatoBean;        
    }    
    
    public String notificarVistoBueno(String nuEmi,String nuAnn){
       StringBuffer sql = new StringBuffer();
        List<EmpleadoBean> list= null;
        sql.append("select R.CEMP_APEPAT||' '||R.CEMP_APEMAT||' '||R.CEMP_DENOM as cempDenom ,R.CEMP_EMAIL as compName,\n" +
                    "'<b>'||tip.CDOC_DESDOC||' '||rt.NU_DOC_EMI||'-'||(SELECT DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR='DE_INSTITUCION' LIMIT 1 )||'-'||rt.DE_DOC_SIG||'<br/>'||'FECHA:'||TO_CHAR(rt.FE_EMI, 'DD/MM/YYYY HH24:MI')||'</b><br/><p ><b>ASUNTO:</b> '||rt.DE_ASU||'</p>' as accionBD "+
                    "from IDOSGD.TDTV_PERSONAL_VB T\n" +
                    "inner join IDOSGD.tdtv_remitos rt ON rt.NU_ANN=T.NU_ANN AND rt.NU_EMI=T.NU_EMI\n" +
                    "inner join IDOSGD.si_mae_tipo_doc tip ON tip.CDOC_TIPDOC=rt.CO_TIP_DOC_ADM\n" +
                    "inner join IDOSGD.rhtm_per_empleados R ON T.CO_EMP_VB=R.CEMP_CODEMP\n" +
                    "where T.nu_ann='"+nuAnn+"' and T.nu_emi='"+nuEmi+"'");

        try {  
           
             list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class));

        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
       
        EmpleadoBean oEmpleadoBean=new EmpleadoBean();
        for(int i=0;i<list.size();i++)
        {
          oEmpleadoBean=list.get(i);
          //notificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"ENVIA_NOT_CORREO_VISTOBUENO");
          RegisternotificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"Notificacion del Documento para Visto Bueno","El Sistema de Gestion Documental le recuerda que tiene los siguientes documentos por Visar");
        }
        
        return "OK";        
        
    }
     public String getLsEmpleadoNotificar(String nuEmi,String nuAnn, String detalleDoc){
       StringBuffer sql = new StringBuffer();
        List<EmpleadoBean> list= null;
        sql.append("select DISTINCT R.CEMP_APEPAT||' '||R.CEMP_APEMAT||' '||R.CEMP_DENOM as cempDenom ,R.CEMP_EMAIL as compName,\n" +
                    "'<b>EXP. '||(SELECT NU_EXPEDIENTE FROM IDOSGD.TDTC_EXPEDIENTE WHERE NU_SEC_EXP=rt.NU_SEC_EXP and NU_ANN_EXP=rt.NU_ANN_EXP LIMIT 1)||'<br/>'||tip.CDOC_DESDOC||' '||rt.NU_DOC_EMI||'-'||(SELECT DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR='DE_INSTITUCION' limit 1)||'-'||rt.DE_DOC_SIG||'<br/>'||'FECHA:'||TO_CHAR(rt.FE_EMI, 'DD/MM/YYYY HH24:MI')||'</b><br/><p ><b>ASUNTO:</b> '||rt.DE_ASU||'</p>' as accionBD "+
                    "from IDOSGD.tdtv_destinos T\n" +
                    "inner join IDOSGD.tdtv_remitos rt ON rt.NU_ANN=T.NU_ANN AND rt.NU_EMI=T.NU_EMI\n" +
                    "inner join IDOSGD.si_mae_tipo_doc tip ON tip.CDOC_TIPDOC=rt.CO_TIP_DOC_ADM\n" +
                    "inner join IDOSGD.rhtm_per_empleados R ON T.CO_EMP_DES=R.CEMP_CODEMP\n" +
                    "inner join IDOSGD.TDTX_CONFIG_EMP C ON R.CEMP_CODEMP=C.CO_EMP  \n" +
                    "where T.nu_ann='"+nuAnn+"' and T.nu_emi='"+nuEmi+"' and  (C.IN_MAIL_DERIV='3' OR (C.IN_MAIL_DERIV='1' and T.CO_PRI IN ('2','3')) OR (C.IN_MAIL_DERIV='2' and rt.CO_TIP_DOC_ADM='011'))");
 
        try {  
           
             list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class));

        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
       
        EmpleadoBean oEmpleadoBean=new EmpleadoBean();
        for(int i=0;i<list.size();i++)
        {
          oEmpleadoBean=list.get(i);
          //notificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"ENVIA_NOT_CORREO_DERIV");
          RegisternotificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"Notificacion del Documento Derivado","El Sistema de Gestion Documental le notifica la derivacion del siguiente documento ");
        }
        
        return "OK";      
     }
      public void RegisternotificarCorreo(String v_nom, String p_Para, String detalleDoc, String asunto, String cuerpo) {
      String mensaje = "NO_OK"; 
      SimpleJdbcCall spNotificarCorreo = new SimpleJdbcCall(this.dataSource)
               .withSchemaName("idosgd")
              .withFunctionName("pk_sgd_tramite_crea_notificacion_log_inserta")
            .withoutProcedureColumnMetaDataAccess()
            .useInParameterNames("p_nombre", "p_para","p_detalle","p_asunto","p_cuerpo")
            .declareParameters(
                new SqlParameter("p_nombre", Types.VARCHAR),
                new SqlParameter("p_para", Types.VARCHAR),
                new SqlParameter("p_detalle", Types.VARCHAR),
                new SqlParameter("p_asunto", Types.VARCHAR),
                new SqlParameter("p_cuerpo", Types.VARCHAR));
                
        SqlParameterSource in = new MapSqlParameterSource()        
                .addValue("p_nombre", v_nom)
                .addValue("p_para", p_Para)
                .addValue("p_detalle", detalleDoc)
                .addValue("p_asunto", asunto)
                .addValue("p_cuerpo", cuerpo);
       
       try{
           spNotificarCorreo.execute(in);
           mensaje = "OK";
       }catch(Exception ex){
           mensaje = "NO_OK";
           ex.printStackTrace();
       }
      
    }

      
       public String getLsMesaVirtualNotificar(String nuEmi,String nuAnn, String detalleDoc){
       StringBuffer sql = new StringBuffer();
        List<EmpleadoBean> list= null;
        sql.append("select DISTINCT 'Administrador' as cempDenom ,si.CELE_DESCOR as compName,\n" +
                    "'<b>EXP. '||(SELECT NU_EXPEDIENTE FROM IDOSGD.TDTC_EXPEDIENTE WHERE NU_SEC_EXP=rt.NU_SEC_EXP LIMIT 1)||'<br/>'||tip.CDOC_DESDOC||' '||rt.NU_DOC_EMI||'-'||(SELECT DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR='DE_INSTITUCION' LIMIT 1)||'-'||rt.DE_DOC_SIG||'<br/>'||'FECHA:'||TO_CHAR(rt.FE_EMI, 'DD/MM/YYYY HH24:MI')||'</b><br/><p ><b>ASUNTO:</b> '||rt.DE_ASU||'</p>' as accionBD "+
                    "from IDOSGD.tdtv_destinos T\n" +
                    "inner join IDOSGD.tdtv_remitos rt ON rt.NU_ANN=T.NU_ANN AND rt.NU_EMI=T.NU_EMI\n" +
                    "inner join IDOSGD.si_mae_tipo_doc tip ON tip.CDOC_TIPDOC=rt.CO_TIP_DOC_ADM\n" +
                    "inner join IDOSGD.IOTDTC_DESPACHO io ON io.VNUMREGSTD=T.NU_EMI and io.VANIOREGSTD=T.NU_ANN \n" +
                    "left join IDOSGD.si_elemento si ON si.CTAB_CODTAB='NOTI_DESP_MV' \n" +
                    "where T.nu_ann='"+nuAnn+"' and T.nu_emi='"+nuEmi+"' and T.TI_DES not in ('01','05')");                    
 
        try {  
           
             list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class));

        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
       
        EmpleadoBean oEmpleadoBean=new EmpleadoBean();
        for(int i=0;i<list.size();i++)
        {
          oEmpleadoBean=list.get(i);
          //notificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"ENVIA_NOT_CORREO_MESAVIRTUAL");
          RegisternotificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"Notificacion de la Mesa de Partes Virtual - PIDE","El Sistema de Gestion Documental le notifica la derivacion del siguiente documento de PIDE.");
        }
        
        return "OK";     
       }
       
       public String getLsTupaNotificar(String nuEmi,String nuAnn, String detalleDoc){
          StringBuffer sql = new StringBuffer();
        List<EmpleadoBean> list= null;
        sql.append("select DISTINCT 'Administrador' as cempDenom ,si.CELE_DESCOR as compName,\n" +
                    "(select proc.de_nombre from IDOSGD.tdtr_procesos_exp proc where proc.co_proceso=si.CELE_DESELE LIMIT 1)||'<br/>'||'<b>EXP. '||ex.NU_EXPEDIENTE||'<br/>'||tip.CDOC_DESDOC||' '||rt.NU_DOC_EMI||'-'||(SELECT DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR='DE_INSTITUCION' LIMIT 1)||'-'||rt.DE_DOC_SIG||'<br/>'||'FECHA:'||TO_CHAR(rt.FE_EMI, 'DD/MM/YYYY HH24:MI')||'</b><br/><p ><b>ASUNTO:</b> '||rt.DE_ASU||'</p>' as accionBD "+
                    "from IDOSGD.tdtv_destinos T\n" +
                    "inner join IDOSGD.tdtv_remitos rt ON rt.NU_ANN=T.NU_ANN AND rt.NU_EMI=T.NU_EMI\n" +
                    "inner join IDOSGD.tdtc_expediente ex ON ex.NU_SEC_EXP=rt.NU_SEC_EXP \n "+
                    "inner join IDOSGD.si_mae_tipo_doc tip ON tip.CDOC_TIPDOC=rt.CO_TIP_DOC_ADM\n" +
                    "left join IDOSGD.si_elemento si ON si.CTAB_CODTAB='NOTI_CORREO_TUPA' \n" +
                    "where T.nu_ann='"+nuAnn+"' and T.nu_emi='"+nuEmi+"' and ((si.CELE_DESELE='0011' and rt.NU_RUC_EMI='20161749126') OR\n" +
                    "(si.CELE_DESELE!='0011' and ex.co_proceso=si.CELE_DESELE)) ");                    
 
        try {  
           
             list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class));

        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
       
        EmpleadoBean oEmpleadoBean=new EmpleadoBean();
        for(int i=0;i<list.size();i++)
        {
          oEmpleadoBean=list.get(i);
          //notificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"ENVIA_NOT_CORREO_TUPA");
          RegisternotificarCorreo(oEmpleadoBean.getCempDenom(), oEmpleadoBean.getCompName(),oEmpleadoBean.getAccionBD(),"Notificacion de Documento TUPA","El Sistema de Gestion Documental le notifica la derivacion del siguiente documento TUPA.");
        }
        
        return "OK"; 
       }
       
        
}
