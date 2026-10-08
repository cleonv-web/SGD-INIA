/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import pe.gob.onpe.tramitedoc.dao.MaestrosDao;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import pe.gob.onpe.tramitedoc.bean.AnnioBean;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;



import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.*;
import pe.gob.onpe.tramitedoc.bean.MensajesConsulBean;
import pe.gob.onpe.tramitedoc.web.util.BusquedaTextual;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import pe.gob.onpe.tramitedoc.dao.MensajesDao;

/**
 *
 * @author oti3
 */
@Repository("mensajesDao")
public class MensajesDaoImp extends SimpleJdbcDaoBase implements MensajesDao {
    private SimpleJdbcCall spDescargamsj;
        @Override
        public List<EstadoDocumentoBean> listEstadosMsj(String nomTabla) {
        StringBuffer sql = new StringBuffer();
       
                sql.append("SELECT DE_EST_MP DE_EST,CO_EST \n" +
                    "FROM TDTR_ESTADOS \n" +
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
    public List<SiElementoBean> getLsSiElementoBean(String pctabCodtab) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CELE_DESELE,CELE_CODELE,NELE_NUMSEC,CELE_DESCOR\n" +
                   "FROM   SI_ELEMENTO\n" +
                   "WHERE  CTAB_CODTAB = ? UNION SELECT '.: TODOS :.',NULL,NULL,NULL \n" +
                   "FROM DUAL \n" +
                   "ORDER BY CELE_CODELE");

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
    public List<ElementoMensajeroBean> getListMensajero(String tipo,String Ambito){
        
        StringBuffer sql = new StringBuffer();                 
       /* if (tipo.equals("MOTORIZADO")){
        sql.append("SELECT CEMP_NU_DNI codigo,CEMP_APEPAT||' '||CEMP_APEMAT ||' '|| CEMP_DENOM nombre FROM RHTM_PER_EMPLEADOS WHERE CEMP_CO_CARGO='038'");         
        }
        if(tipo.equals("COURRIER")){        
        sql.append("SELECT CPRO_RUC codigo ,CPRO_RAZSOC nombre FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC IN ( select CELE_DESELE from SI_ELEMENTO WHERE CTAB_CODTAB='DE_COURRIER' )");         
        }*/
        if(tipo.equals("COURRIER")){        //NELE_NUMSEC
        //sql.append("SELECT CPRO_RUC codigo ,CPRO_RAZSOC nombre FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC IN ( select CELE_DESELE from SI_ELEMENTO WHERE CTAB_CODTAB='DE_COURRIER' AND CELE_CODELE2 IN (SELECT  CELE_CODELE FROM  si_elemento WHERE CTAB_CODTAB='DE_AMBITO_MENS' AND cele_desele='"+Ambito+"')  ) ");
            if(!Ambito.equals(".: TODOS :."))
                {
                    sql.append("SELECT CPRO_RUC codigo ,CPRO_RAZSOC nombre FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC IN (  select CU.CELE_DESELE from SI_ELEMENTO CU \n" +
                    " INNER JOIN SI_ELEMENTO AM ON CU.cele_codele2=AM.cele_codele AND AM.CTAB_CODTAB='DE_AMBITO_MENS' \n" +
                    " WHERE CU.CTAB_CODTAB='DE_COURRIER' AND AM.cele_desele='"+Ambito+"'  ) ");
                    
                }
            else
                {
                    sql.append("SELECT CPRO_RUC codigo ,CPRO_RAZSOC nombre FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC IN (  select CU.CELE_DESELE from SI_ELEMENTO CU \n" +
                    " INNER JOIN SI_ELEMENTO AM ON CU.cele_codele2=AM.cele_codele AND AM.CTAB_CODTAB='DE_AMBITO_MENS' \n" +
                    " WHERE CU.CTAB_CODTAB='DE_COURRIER') ");
                }

        }
        else {
          sql.append("select NULEM codigo,DEAPP||' '||DEAPM||' '||DENOM nombre from TDTX_ANI_SIMIL where NULEM in (select CELE_DESELE from SI_ELEMENTO WHERE CTAB_CODTAB='"+tipo+"')");      
        }
        List<ElementoMensajeroBean> list = new ArrayList<ElementoMensajeroBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ElementoMensajeroBean.class) );
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        
        return list; 
    }

    @Override
    public List<EstadoDocumentoBean> listEstadosCarga(String nomTabla) {
        
        StringBuffer sql = new StringBuffer();
       
        sql.append("SELECT DE_EST,CO_EST \n" +
                    "FROM TDTR_ESTADOS \n" +
                    "WHERE CO_EST!=2 AND DE_TAB=?\n" +
                    "AND DE_EST IS NOT NULL AND DE_EST_MP IS NOT NULL " +
                    "UNION SELECT 'PENDIENTE' as DE_EST,'2' as CO_EST from dual "+
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
    public List<MensajesConsulBean> getBuscaDocumentosCarga(BuscarDocumentoCargaMsjBean buscarDocumentoCargaMsjBean) {

        StringBuffer sql = new StringBuffer();
        
        boolean bBusqFiltro = false;
        
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<MensajesConsulBean> list = new ArrayList<MensajesConsulBean>();

        
        sql.append("SELECT  ");
        sql.append("A.NU_MSJ,TO_CHAR(M.FEC_ENVIOMSJ,'DD/MM/YYYY') FEC_ENVIOMSJ, A.NU_ANN,A.NU_EMI,A.NU_DES,PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM)|| ' ' ||DECODE (B.TI_EMI,");
        sql.append("'01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
        sql.append("'05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
        sql.append("B.DE_DOC_SIG)");
        sql.append("DE_TIP_DOC,UPPER(B.DE_ASU) DE_ASU,A.CO_LOC_DES CO_LOCAL,NVL2(A.CO_LOC_DES,SUBSTR(PK_SGD_DESCRIPCION.DE_LOCAL(A.CO_LOC_DES), 1, 100),NULL) DE_LOCAL, ");
        sql.append("B.CO_DEP_EMI CO_DEPENDENCIA,");
        sql.append("NVL2(B.CO_DEP_EMI,SUBSTR(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.CO_DEP_EMI), 1, 100),NULL) DE_DEPENDENCIA,");                    
        sql.append("DECODE(A.TI_DES,");
        sql.append("'01', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES),");
        sql.append("'02', PK_SGD_DESCRIPCION.DE_PROVEEDOR (A.NU_RUC_DES),");
        sql.append("'03', PK_SGD_DESCRIPCION.ANI_SIMIL (A.NU_DNI_DES),");
        sql.append("'04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.CO_OTR_ORI_DES),");
        sql.append("'05', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES)");
        sql.append(")  DESTINATARIO,");
        sql.append("A.CO_MOT CO_TRAMITE,NVL2(A.CO_MOT,SUBSTR(PK_SGD_DESCRIPCION.MOTIVO(A.CO_MOT), 1, 100),NULL) DE_TRAMITE, ");
        sql.append("A.CO_PRI CO_PRIORIDAD, ");
        sql.append("A.DE_PRO DE_INDICACIONES, ");
        sql.append("A.TI_DES CO_TIPO_DESTINO,");
        sql.append("A.CDIR_REMITE direccion,");
        sql.append("DECODE(A.TI_DES,");
        sql.append("'01', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS),");
        sql.append("'02', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),");
        sql.append("'03', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),");
        sql.append("'04', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),");
        sql.append("'05', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS)");
        sql.append(") DEPARTAMENTO,M.DE_TIP_MSJ,M.DE_TIP_ENV,");
        sql.append("(CASE WHEN M.DE_TIP_MSJ='COURRIER' THEN (SELECT TRIM(CPRO_RAZSOC) FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC=M.RE_ENV_MSJ) ELSE (SELECT TRIM(CEMP_APEPAT)||' '||TRIM(CEMP_APEMAT) ||' '|| TRIM(CEMP_DENOM) FROM RHTM_PER_EMPLEADOS WHERE CEMP_NU_DNI=M.RE_ENV_MSJ) END) RE_ENV_MSJ, "); 
        sql.append("M.NU_SER_MSJ||'-'||M.AN_SER_MSJ NU_SERVICIO,");
        sql.append("NVL2(A.FE_ENT_MSJ,TO_CHAR(A.FE_ENT_MSJ,'DD/MM/YYYY'),' ') FE_ENT_MSJ, ");
        sql.append("NVL2(A.FE_DEV_MSJ,TO_CHAR(A.FE_DEV_MSJ,'DD/MM/YYYY'),' ') FE_DEV_MSJ,");
        sql.append("(SELECT DE_EST FROM TDTR_ESTADOS WHERE CO_EST=A.ES_DOC_REC AND DE_TAB='TD_MENSAJERIA') EST_MSJ,A.ES_DOC_REC COD_EST_MSJ,M.DE_AMBITO,TO_CHAR(M.FE_PLA_MSJ,'DD/MM/YYYY') FE_PLA_MSJ,A.ES_PEN_MSJ,NVL2(M.DE_OBS_MSJ,M.DE_OBS_MSJ,' ')||'  '||NVL2(A.OB_MSJ,A.OB_MSJ,' ') OB_MSJ,NVL2(A.MO_MSJ_DEV,A.MO_MSJ_DEV,' ') MO_MSJ_DEV, ");   
        sql.append("trunc(M.FE_PLA_MSJ)-trunc(M.FEC_ENVIOMSJ)+1 DIA_PLA,");
        sql.append("(CASE WHEN A.ES_DOC_REC=2 THEN PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,SysDate) ELSE ");
        sql.append(" PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,A.FE_ENT_MSJ)  END) DIA_TRA,");
        sql.append("(CASE WHEN M.PE_ENV_MSJ='1' AND (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ)-PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,SysDate))>0 AND A.ES_DOC_REC='2' ");
        sql.append("THEN (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ))-(PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,SysDate)) ELSE ");
        sql.append("0 END) DIA_PEN, ");
        sql.append("(CASE WHEN M.PE_ENV_MSJ='1' AND (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ)-PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,SysDate))<0 AND A.ES_DOC_REC='2' ");
        sql.append("THEN (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,SysDate)-PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ)) ELSE CASE WHEN M.PE_ENV_MSJ='1' AND (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ)-PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,A.FE_ENT_MSJ))<0 AND A.ES_DOC_REC='3' ");
        sql.append("THEN (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,A.FE_ENT_MSJ)-PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ)) ELSE CASE WHEN M.PE_ENV_MSJ='1' AND  (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ)-PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,A.FE_DEV_MSJ))<0 AND A.ES_DOC_REC='4' THEN (PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,A.FE_DEV_MSJ)-PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FEC_ENVIOMSJ,M.FE_PLA_MSJ)) ELSE 0 END END END) DIA_VEN, ");
        sql.append("(CASE WHEN A.ES_DOC_REC='3' AND M.PE_ENV_MSJ='1' AND PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FE_PLA_MSJ,A.FE_ENT_MSJ)>0 THEN PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(M.FE_PLA_MSJ,A.FE_ENT_MSJ) ELSE 0 END) DIA_ENT,");
        sql.append("(CASE WHEN A.ES_DOC_REC='4' AND M.PE_ENV_MSJ='1' AND PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(A.FE_PLA_DEV,A.FE_DEV_MSJ)>0 THEN PK_SGD_MENSAJERIA.CAL_DIF_FECHAS_HABILES(A.FE_PLA_DEV,A.FE_DEV_MSJ) ELSE 0 END) DIA_DEV,");
        sql.append("B.ES_DOC_EMI,NVL2(A.FE_PLA_DEV,TO_CHAR(A.FE_PLA_DEV,'DD/MM/YYYY'),' ') FE_PLA_DEV, ");
        sql.append("NVL2(B.FE_ENV_MES,TO_CHAR(B.FE_ENV_MES,'DD/MM/YYYY'),' ') FE_ENV_MES,NVL2(B.FE_EMI,TO_CHAR(B.FE_EMI,'DD/MM/YYYY'),' ') FE_EMI, ");
        sql.append("NVL2(B.FEC_RECEPMP,TO_CHAR(B.FEC_RECEPMP,'DD/MM/YYYY'),' ') FEC_RECEPMP, ");
        sql.append("NVL2(A.nro_guia_devolucion,A.nro_guia_devolucion,' ') as nro_guia_devolucion ");
        sql.append("FROM TDTV_DESTINOS A INNER JOIN TDTV_REMITOS B ON A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI ");
        sql.append("LEFT JOIN ( SELECT D.CO_DEPENDENCIA,D.DE_DEPENDENCIA,D.DE_CORTA_DEPEN,L.DE_NOMBRE_LOCAL,L.DE_DIRECCION_LOCAL,NODEP,NOPRV,NODIS ,L.CCOD_LOCAL " );
        sql.append("FROM RHTM_DEPENDENCIA D " );
        sql.append("INNER JOIN SITM_LOCAL_DEPENDENCIA DL ON DL.CO_DEP=D.CO_DEPENDENCIA " );
        sql.append("INNER JOIN SI_MAE_LOCAL L ON L.CCOD_LOCAL=DL.CO_LOC " );
        sql.append("LEFT JOIN IDTUBIAS U ON U.UBDEP||U.UBPRV||U.UBDIS =L.CO_UBIGEO  ) DEP ON DEP.CO_DEPENDENCIA=A.co_dep_DES AND DEP.CCOD_LOCAL=a.co_loc_des " );
        sql.append(" LEFT JOIN IDTUBIAS UBIDESTINO ON UBIDESTINO.UBDEP||UBIDESTINO.UBPRV||UBIDESTINO.UBDIS =a.CCOD_DPTO||a.CCOD_PROV||a.CCOD_DIST  ");
        sql.append(" INNER JOIN TD_MENSAJERIA M ON A.NU_MSJ=M.NU_MSJ "); 
        sql.append(" WHERE B.TI_ENV_MSJ='0' AND B.COD_DEP_MSJ ='"+buscarDocumentoCargaMsjBean.getCoDependencia()+"' "); 
        String pNUAnn = buscarDocumentoCargaMsjBean.getCoAnnio();
//        System.out.println("EMPLEADO"+buscarDocumentoCargaMsjBean.getCoEmpleado());
        String pTipoBusqueda = buscarDocumentoCargaMsjBean.getTipoBusqueda();
        
        StringBuffer sqlQry2 = new StringBuffer();        
        sqlQry2.append("SELECT CELE_DESELE FROM SI_ELEMENTO WHERE CTAB_CODTAB='MOTORIZADO' AND CELE_DESELE IN (SELECT CEMP_NU_DNI FROM RHTM_PER_EMPLEADOS  WHERE CEMP_CODEMP='"+buscarDocumentoCargaMsjBean.getCoEmpleado()+"')");
        
        String siExisteDni = "";
        try
            {
                siExisteDni = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class);
            }
        catch(Exception e)
            {
                siExisteDni="";
            }
        
        
            

        
        
        if (pTipoBusqueda.equals("1") && buscarDocumentoCargaMsjBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        
        
        String pEsFiltroFecha = buscarDocumentoCargaMsjBean.getEsFiltroFecha();
//        if (!(pEsFiltroFecha.equals("1")&&pNUAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN = :pNuAnn");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNUAnn);
//        }
        
        if (buscarDocumentoCargaMsjBean.getCoAmbitoMsj()!= null && buscarDocumentoCargaMsjBean.getCoAmbitoMsj().trim().length() > 0 && !buscarDocumentoCargaMsjBean.getCoAmbitoMsj().equals(".: TODOS :.") ) {
                sql.append(" AND M.DE_AMBITO= :pAmbito ");
                objectParam.put("pAmbito", buscarDocumentoCargaMsjBean.getCoAmbitoMsj());
        }
     
        if (buscarDocumentoCargaMsjBean.getCoTipoEnvMsj()!= null && buscarDocumentoCargaMsjBean.getCoTipoEnvMsj().trim().length() > 0 && !buscarDocumentoCargaMsjBean.getCoTipoEnvMsj().equals(".: TODOS :.") ) {
                sql.append(" AND M.DE_TIP_ENV= :pTipoEnv ");
                objectParam.put("pTipoEnv", buscarDocumentoCargaMsjBean.getCoTipoEnvMsj());
        }
        
        if (buscarDocumentoCargaMsjBean.getCoTipoMsj()!= null && buscarDocumentoCargaMsjBean.getCoTipoMsj().trim().length() > 0 && !buscarDocumentoCargaMsjBean.getCoTipoMsj().equals(".: TODOS :.") ) {
                sql.append(" AND M.DE_TIP_MSJ= :pTipoMsj ");
                objectParam.put("pTipoMsj", buscarDocumentoCargaMsjBean.getCoTipoMsj());
        }

        if (buscarDocumentoCargaMsjBean.getCoEstadoDoc()!= null && buscarDocumentoCargaMsjBean.getCoEstadoDoc().trim().length() > 0 && !buscarDocumentoCargaMsjBean.getCoEstadoDoc().equals(".: TODOS :.") ) {
                sql.append(" AND A.ES_DOC_REC= :pEstadoDoc ");
                objectParam.put("pEstadoDoc", buscarDocumentoCargaMsjBean.getCoEstadoDoc());
        }
        else
        {
            sql.append(" AND A.ES_DOC_REC IN ('2','3','4') ");
                
        }
       
        if (buscarDocumentoCargaMsjBean.getCoResponsable()!= null && buscarDocumentoCargaMsjBean.getCoResponsable().trim().length() > 0 && !buscarDocumentoCargaMsjBean.getCoResponsable().equals("-1") ) {
                sql.append(" AND RE_ENV_MSJ= :pResponsable ");
                objectParam.put("pResponsable", buscarDocumentoCargaMsjBean.getCoResponsable());
        }
        
        if (buscarDocumentoCargaMsjBean.getEsFiltroFecha() != null
                    && (pEsFiltroFecha.equals("1") || pEsFiltroFecha.equals("3") || pEsFiltroFecha.equals("2"))) {
                String vFeEmiIni = buscarDocumentoCargaMsjBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoCargaMsjBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND M.FEC_ENVIOMSJ between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + 0.99999");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
        
        String auxTipoAcceso=buscarDocumentoCargaMsjBean.getTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
        if(tiAcceso.equals("1")){//acceso personal
                if (siExisteDni.length()>=8)
                {
                        sql.append(" AND M.RE_ENV_MSJ= :pReEnvMsj ");
                        objectParam.put("pReEnvMsj", siExisteDni);

                }            
        }
        //else {//acceso total
            
         //   sql.append(" AND B.COD_DEP_MSJ ='"+buscarDocumentoCargaMsjBean.getCoDependencia()+"' ");
            
        //}

        
       // if (pTipoBusqueda.equals("0") || bBusqFiltro)
       // {
//            System.out.println(buscarDocumentoCargaMsjBean.getBusNuMsj());
            if (buscarDocumentoCargaMsjBean.getBusNuMsj()!= null && buscarDocumentoCargaMsjBean.getBusNuMsj().trim().length() > 0  ) {
                sql.append(" AND A.NU_MSJ= :pNuMsj ");
                objectParam.put("pNuMsj", buscarDocumentoCargaMsjBean.getBusNuMsj());
            }
            
            if (buscarDocumentoCargaMsjBean.getBusAnSerMsj()!= null && buscarDocumentoCargaMsjBean.getBusAnSerMsj().trim().length() > 0  ) {
                sql.append(" AND M.AN_SER_MSJ= :pAnSerMsj ");
                objectParam.put("pAnSerMsj", buscarDocumentoCargaMsjBean.getBusAnSerMsj());
            }
            
            if (buscarDocumentoCargaMsjBean.getBusNuSerMsj()!= null && buscarDocumentoCargaMsjBean.getBusNuSerMsj().trim().length() > 0  ) {
                sql.append(" AND M.NU_SER_MSJ LIKE '%'||:pNuSerMsj||'%' ");
                objectParam.put("pNuSerMsj", buscarDocumentoCargaMsjBean.getBusNuSerMsj());
            }  
            
            if (buscarDocumentoCargaMsjBean.getBusDesti()!= null && buscarDocumentoCargaMsjBean.getBusDesti().trim().length() > 0  ) {
                
                sql.append(" AND UPPER(DECODE(A.TI_DES,");
                sql.append("'01', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES),");
                sql.append("'02', PK_SGD_DESCRIPCION.DE_PROVEEDOR (A.NU_RUC_DES),");
                sql.append("'03', PK_SGD_DESCRIPCION.ANI_SIMIL (A.NU_DNI_DES),");
                sql.append("'04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.CO_OTR_ORI_DES),");
                sql.append("'05', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES)");
                sql.append(")) LIKE '%'||UPPER(:pDesti)||'%' ");
                
                objectParam.put("pDesti", buscarDocumentoCargaMsjBean.getBusDesti());
            }  
            
            if (buscarDocumentoCargaMsjBean.getBusNuDoc()!= null && buscarDocumentoCargaMsjBean.getBusNuDoc().trim().length() > 0  ) {
                String palabra="";
                String cadena;
                int cont=0;
                cadena=buscarDocumentoCargaMsjBean.getBusNuDoc();
                sql.append(" AND (");
                
                for(int i=0;i<cadena.length();i++)
                {
                    
                        if(cadena.charAt(i)==' ')
                        {    
                            
                            if (palabra.trim().length()>0)
                            {
                                if (cont==0)
                                {
                                    sql.append("UPPER(PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM)|| ' ' ||DECODE (B.TI_EMI,");
                                    sql.append("'01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                                    sql.append("'05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                                    sql.append("B.DE_DOC_SIG)) LIKE '%'||UPPER(:pNuDoc)||'%' ");
                                    objectParam.put("pNuDoc", palabra.trim());
                                }
                                else
                                {
                                    sql.append(" OR UPPER(PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM)|| ' ' ||DECODE (B.TI_EMI,");
                                    sql.append("'01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                                    sql.append("'05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                                    sql.append("B.DE_DOC_SIG)) LIKE '%'||UPPER(:pNuDoc)||'%' ");
                                    objectParam.put("pNuDoc", palabra.trim());
                                }

                            }
                            
                            cont++;
                            palabra="";
                        } 
                        else
                        {
                            palabra=palabra+cadena.charAt(i);
                        }
                    
                }
                if (palabra.trim().length()>0)
                {
                    
                    if (cont==0)
                    {
                        sql.append("UPPER(PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM)|| ' ' ||DECODE (B.TI_EMI,");
                        sql.append("'01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                        sql.append("'05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                        sql.append("B.DE_DOC_SIG)) LIKE '%'||UPPER(:pNuDoc)||'%' ");
                        objectParam.put("pNuDoc", palabra.trim());
                    }
                    else
                    {
                        sql.append(" OR UPPER(PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM)|| ' ' ||DECODE (B.TI_EMI,");
                        sql.append("'01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                        sql.append("'05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
                        sql.append("B.DE_DOC_SIG)) LIKE '%'||UPPER(:pNuDoc)||'%' ");
                        objectParam.put("pNuDoc", palabra.trim());
                    }

                }
                sql.append(" ) ");
                
                


                
            }

            if (buscarDocumentoCargaMsjBean.getCoOficina()!= null && buscarDocumentoCargaMsjBean.getCoOficina().trim().length() > 0 && !buscarDocumentoCargaMsjBean.getCoOficina().equals(".: TODOS :.") ) {
                    sql.append(" AND B.CO_DEP_EMI= :pOficina ");
                    objectParam.put("pOficina", buscarDocumentoCargaMsjBean.getCoOficina());
            }
        
     //   }
        
        sql.append("ORDER BY 3");

               
       // System.out.println(sql);
 
        
        try {
            //objectParam,
            list = this.namedParameterJdbcTemplate.query(sql.toString(),objectParam,  BeanPropertyRowMapper.newInstance(MensajesConsulBean.class));

        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    
    

    }

    @Override
    public String updMensajeriaDocumento(DescargaMensajeBean descargaMensaje) {
        String vReturn = "NO_OK";    
        /*StringBuffer sql = new StringBuffer(); 
        
        sql.append("UPDATE TDTV_DESTINOS ");
        sql.append("SET ES_DOC_REC='"+descargaMensaje.getCo_EstadoDoc()+"', ");
        sql.append("Ob_msj='"+descargaMensaje.getOb_msj()+"', ");
        if (descargaMensaje.getCo_EstadoDoc().equals("3"))
        {
            sql.append("fe_ent_msj='"+descargaMensaje.getFe_ent_msj()+"' ");
        }
        else
        {
            sql.append("fe_dev_msj='"+descargaMensaje.getFe_ent_msj()+"' ");
        }
    
        sql.append("WHERE NU_ANN||NU_EMI in ( "+ descargaMensaje.getNu_ann()+descargaMensaje.getNu_emi()+" ) ");
        sql.append("AND nu_des in ( "+ descargaMensaje.getNu_des()+" ) AND Nu_msj in ( "+ descargaMensaje.getNu_msj()+" )");
        
        try{
            
            this.jdbcTemplate.update(sql.toString() );
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Mensajeria Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }*/
        
        if (descargaMensaje.getCo_EstadoDoc().equals("3"))
        {
            descargaMensaje.setMo_msj_dev("");
        }

        if (descargaMensaje.getEs_pen_msj().equals("0"))
        {
            descargaMensaje.setFe_pla_dev("");
            descargaMensaje.setEs_pen_msj("");
            descargaMensaje.setEs_pen_dev("");
        }
        
        
                
                this.spDescargamsj = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_MENSAJERIA").withProcedureName("descargamsj")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("pnu_ann", "pnu_emi", "pnu_des", "pnu_msj", "pfechaent","phoraent","pfechadev","phoradev",
                        "pob_msj", "pmo_msj_dev", "pes_pen_msj","pes_pen_dev", "pest","pnro_guia_devolucion","pfepladev",
                        "pnu_Acta_Vis1", "pnu_Acta_Vis2", "pfe_Acta_Vis1","pfe_Acta_Vis2", "pes_Acta1_msj","pes_Acta2_msj","pco_usuario")
                        //descargamsj(pnu_ann tdtv_remitos.nu_ann%TYPE,pnu_emi tdtv_remitos.nu_emi%TYPE,pnu_des tdtv_destinos.nu_des%TYPE,pnu_msj tdtv_destinos.nu_msj%TYPE,pfecha tdtv_destinos.fec_finmsj%TYPE,pob_msj tdtv_destinos.ob_msj%TYPE,pmo_msj_dev tdtv_destinos.mo_msj_dev%TYPE,pes_pen_msj tdtv_destinos.es_pen_msj%TYPE,pest VARCHAR2);
                .declareParameters(
                        new SqlParameter("pnu_ann", Types.VARCHAR),
                        new SqlParameter("pnu_emi", Types.VARCHAR),
                        new SqlParameter("pnu_des", Types.VARCHAR),
                        new SqlParameter("pnu_msj", Types.VARCHAR),
                        new SqlParameter("pfechaent", Types.VARCHAR),
                        new SqlParameter("phoraent", Types.VARCHAR), 
                        new SqlParameter("pfechadev", Types.VARCHAR),
                        new SqlParameter("phoradev", Types.VARCHAR),
                        new SqlParameter("pob_msj", Types.VARCHAR),
                        new SqlParameter("pmo_msj_dev", Types.VARCHAR),
                        new SqlParameter("pes_pen_msj", Types.VARCHAR),
                        new SqlParameter("pes_pen_dev", Types.VARCHAR),
                        new SqlParameter("pest", Types.VARCHAR),
                        new SqlParameter("pnro_guia_devolucion", Types.VARCHAR),
                        new SqlParameter("pfepladev", Types.VARCHAR),
                        new SqlParameter("pnu_Acta_Vis1", Types.VARCHAR),
                        new SqlParameter("pnu_Acta_Vis2", Types.VARCHAR),
                        new SqlParameter("pfe_Acta_Vis1", Types.VARCHAR),
                        new SqlParameter("pfe_Acta_Vis2", Types.VARCHAR),
                        new SqlParameter("pes_Acta1_msj", Types.VARCHAR),
                        new SqlParameter("pes_Acta2_msj", Types.VARCHAR),
                        new SqlParameter("pco_usuario", Types.VARCHAR));
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pnu_ann", descargaMensaje.getNu_ann())
                .addValue("pnu_emi", descargaMensaje.getNu_emi())
                .addValue("pnu_des", descargaMensaje.getNu_des())
                .addValue("pnu_msj", descargaMensaje.getNu_msj())
                .addValue("pfechaent", descargaMensaje.getFe_ent_msj())
                .addValue("phoraent", descargaMensaje.getHo_ent_msj())   
                .addValue("pfechadev", descargaMensaje.getFe_dev_msj())
                .addValue("phoradev", descargaMensaje.getHo_dev_msj())
                .addValue("pob_msj", descargaMensaje.getOb_msj())
                .addValue("pmo_msj_dev", descargaMensaje.getMo_msj_dev())
                .addValue("pes_pen_msj", descargaMensaje.getEs_pen_msj())
                .addValue("pes_pen_dev", descargaMensaje.getEs_pen_dev())
                .addValue("pest", descargaMensaje.getCo_EstadoDoc())
                .addValue("pnro_guia_devolucion", descargaMensaje.getNro_guia_devolucion())                        
                .addValue("pfepladev", descargaMensaje.getFe_pla_dev())
                .addValue("pnu_Acta_Vis1", descargaMensaje.getNu_Acta_Vis1())
                .addValue("pnu_Acta_Vis2", descargaMensaje.getNu_Acta_Vis2())
                .addValue("pfe_Acta_Vis1", descargaMensaje.getFe_Acta_Vis1())
                .addValue("pfe_Acta_Vis2", descargaMensaje.getFe_Acta_Vis2())
                .addValue("pes_Acta1_msj", descargaMensaje.getEs_Acta1_msj())
                .addValue("pes_Acta2_msj", descargaMensaje.getEs_Acta2_msj())
                .addValue("pco_usuario", descargaMensaje.getCo_usuario());
                /*System.out.println(descargaMensaje.getHo_ent_msj());
                System.out.println(descargaMensaje.getNu_ann());
                System.out.println(descargaMensaje.getNu_emi());
                System.out.println(descargaMensaje.getNu_des());
                System.out.println(descargaMensaje.getNu_msj());
                System.out.println(descargaMensaje.getFe_ent_msj());
                System.out.println(descargaMensaje.getOb_msj());
                System.out.println( descargaMensaje.getMo_msj_dev());
                System.out.println(descargaMensaje.getEs_pen_msj());
                System.out.println(descargaMensaje.getCo_EstadoDoc());*/
       
        try {
            this.spDescargamsj.execute(in);
            vReturn = "OK";
            
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }
        
        return vReturn;   
    }

    @Override
    public String insArchivoAnexoDes(final DocumentoAnexoDesBean docAnexo,final  InputStream archivoAnexo,final int size) {
        String vReturn = "NO_OK";
        docAnexo.setDeDet("CARGO_NOTIFICACION"+docAnexo.getNuDes()+".pdf");
        StringBuffer sql = new StringBuffer();
        
        String pcount=getCount(docAnexo.getNuAnn(),docAnexo.getNuEmi(),docAnexo.getNuDes());
       /* System.out.println("eeeeee");
        System.out.println(pcount);*/
        if (pcount.equals("0"))
        {
            sql.append("insert into tdtv_anexos(\n"
            + "nu_ann,\n"
            + "nu_emi,\n"
            + "nu_ane,\n"
            + "de_det,\n"
            + "de_rut_ori,\n"
            + "co_use_cre,\n"
            + "fe_use_cre,\n"
            + "co_use_mod,\n"
            + "fe_use_mod,\n"
            + "feula, \n"
            + "nu_des, \n"
            + "bl_doc )\n"
            + "values(?,?,?,REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ''),REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' '),?,sysdate,?,sysdate,to_char(sysdate,'yyyymmdd'),?,?)");

            final LobHandler lobhandler = new DefaultLobHandler();

            try {
                this.jdbcTemplate.execute(sql.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
                        ps.setString(1, docAnexo.getNuAnn());
                        ps.setString(2, docAnexo.getNuEmi());
                        ps.setString(3, docAnexo.getNuAne());
                        ps.setString(4, docAnexo.getDeDet());
                        ps.setString(5, docAnexo.getDeRutOri());
                        ps.setString(6, docAnexo.getCoUseCre());
                        ps.setString(7, docAnexo.getCoUseMod());
                        ps.setString(8, docAnexo.getNuDes());
                        lobCreator.setBlobAsBinaryStream(ps, 9, archivoAnexo, size);
                    }
                });
                vReturn = "OK";
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        }
        else
        {
            sql.append("update tdtv_anexos set\n"  
                + "de_det=REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' '),\n"
                + "de_rut_ori=REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' '),\n"
                + "co_use_cre=?,\n"
                + "fe_use_cre=sysdate,\n"
                + "co_use_mod=?,\n"
                + "fe_use_mod=sysdate,\n"
                + "feula=to_char(sysdate,'yyyymmdd'),\n"
                + "bl_doc=?\n"
                + "where \n"
                + "nu_ann=? and nu_emi=? and nu_des=?");

            final LobHandler lobhandler = new DefaultLobHandler();

            try {
                this.jdbcTemplate.execute(sql.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
            
                        ps.setString(1, docAnexo.getDeDet());
                        ps.setString(2, docAnexo.getDeRutOri());
                        ps.setString(3, docAnexo.getCoUseCre());
                        ps.setString(4, docAnexo.getCoUseMod());
                        lobCreator.setBlobAsBinaryStream(ps, 5, archivoAnexo, size);
                        ps.setString(6, docAnexo.getNuAnn());
                        ps.setString(7, docAnexo.getNuEmi());
                        ps.setString(8, docAnexo.getNuDes());
                    }
                });
                vReturn = "OK";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }    


        return vReturn;
    
    }

    
    @Override
    public String insArchivoActa(final MensajesConsulBean docAnexo,final  InputStream archivoAnexo,final int size) {
        String vReturn = "NO_OK";

        StringBuffer sql = new StringBuffer();
        
        String pcount=getCountActa(docAnexo.getNu_ann(),docAnexo.getNu_emi(),docAnexo.getNu_des(),docAnexo.getNu_Acta_Vis1());
       /* System.out.println("eeeeee");
        System.out.println(pcount);*/
        if (pcount.equals("0"))
        {
            sql.append("insert into TD_DET_MENSAJERIA(\n"
            + "nu_ann,\n"
            + "nu_emi,\n"
            + "nu_des,\n"
            + "NU_ACTA,\n"
            + "de_rut_ori,\n"
            + "co_use_cre,\n"
            + "fe_use_cre,\n"
            + "bl_doc )\n"
            + "values(?,?,?,?,REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ''),?,sysdate,?)");

            final LobHandler lobhandler = new DefaultLobHandler();

            try {
                this.jdbcTemplate.execute(sql.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
                        ps.setString(1, docAnexo.getNu_ann());
                        ps.setString(2, docAnexo.getNu_emi());
                        ps.setString(3, docAnexo.getNu_des());
                        ps.setString(4, docAnexo.getNu_Acta_Vis1());
                        ps.setString(5, docAnexo.getDeRutOri());
                        ps.setString(6, docAnexo.getCoUseCre());                       
                        lobCreator.setBlobAsBinaryStream(ps, 7, archivoAnexo, size);
                    }
                });
                vReturn = "OK";
            } catch (Exception e) {
                e.printStackTrace();
            }
        
        }
        else
        {
            sql.append("update TD_DET_MENSAJERIA set\n"  
                + "de_rut_ori=REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' '),\n"
                + "co_use_cre=?,\n"
                + "fe_use_cre=sysdate,\n"
                + "co_use_mod=?,\n"
                + "fe_use_mod=sysdate,\n"
                + "bl_doc=?\n"
                + "where \n"
                + "nu_ann=? and nu_emi=? and nu_des=? and nu_acta=?");

            final LobHandler lobhandler = new DefaultLobHandler();

            try {
                this.jdbcTemplate.execute(sql.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
            
                        ps.setString(1, docAnexo.getDeRutOri());
                        ps.setString(2, docAnexo.getCoUseCre());
                        ps.setString(3, docAnexo.getCoUseMod());
                        lobCreator.setBlobAsBinaryStream(ps, 4, archivoAnexo, size);
                        ps.setString(5, docAnexo.getNu_ann());
                        ps.setString(6, docAnexo.getNu_emi());
                        ps.setString(7, docAnexo.getNu_des());
                        ps.setString(8, docAnexo.getNu_Acta_Vis1());
                    }
                });
                vReturn = "OK";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }    


        return vReturn;
    
    }

    
    public String getCount(String pnuAnn, String pnuEmi,String pnuDes) {
        String result = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) \n"
                + "from tdtv_anexos \n"
                + "where nu_ann = ? \n"
                + "and nu_emi = ? \n"
                + "and nu_des = ? ");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pnuEmi,pnuDes});
        } catch (Exception e) {
            result = "0";
            e.printStackTrace();
        }
        return result;

    }
    
    public String getCountActa(String pnuAnn, String pnuEmi,String pnuDes,String pnuActa) {
        String result = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select count(*) \n"
                + "from TD_DET_MENSAJERIA \n"
                + "where nu_ann = ? \n"
                + "and nu_emi = ? \n"
                + "and nu_des = ? "
                + "and nu_acta = ? "
        );

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pnuEmi,pnuDes,pnuActa});
        } catch (Exception e) {
            result = "0";
            e.printStackTrace();
        }
        return result;

    }
    
    
    @Override
    public String getUltimoAnexo(String pnuAnn, String pnuEmi) {
        String result = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select to_char(nvl(max(nu_ane),0) + 1) nu_ane \n"
                + "from tdtv_anexos \n"
                + "where nu_ann = ? \n"
                + "and nu_emi = ? ");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pnuEmi});
        } catch (Exception e) {
            result = "1";
            e.printStackTrace();
        }
        return result;

    }

    @Override
    public String delMensajeriaDocumento(DescargaMensajeBean descargaMensaje) {
        String vReturn = "NO_OK";    
       /* StringBuffer sql = new StringBuffer(); 
        
        sql.append("UPDATE TDTV_DESTINOS ");
        sql.append("SET Nu_msj=null, ");
        sql.append("fec_enviomsj=null, ");
        sql.append("ES_DOC_REC='1' ");
        sql.append("WHERE NU_ANN||NU_EMI in ( "+ descargaMensaje.getNu_ann()+descargaMensaje.getNu_emi()+" ) ");
        sql.append("AND nu_des in ( "+ descargaMensaje.getNu_des()+" ) AND Nu_msj in ( "+ descargaMensaje.getNu_msj()+" )");
        System.out.println(sql.toString());
        try{
            
            this.jdbcTemplate.update(sql.toString() );
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }*/
       
        this.spDescargamsj = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_MENSAJERIA").withProcedureName("delmsj")
        .withoutProcedureColumnMetaDataAccess()
        .useInParameterNames("pnu_ann", "pnu_emi", "pnu_des", "pnu_msj")
                //descargamsj(pnu_ann tdtv_remitos.nu_ann%TYPE,pnu_emi tdtv_remitos.nu_emi%TYPE,pnu_des tdtv_destinos.nu_des%TYPE,pnu_msj tdtv_destinos.nu_msj%TYPE,pfecha tdtv_destinos.fec_finmsj%TYPE,pob_msj tdtv_destinos.ob_msj%TYPE,pmo_msj_dev tdtv_destinos.mo_msj_dev%TYPE,pes_pen_msj tdtv_destinos.es_pen_msj%TYPE,pest VARCHAR2);
        .declareParameters(
                new SqlParameter("pnu_ann", Types.VARCHAR),
                new SqlParameter("pnu_emi", Types.VARCHAR),
                new SqlParameter("pnu_des", Types.VARCHAR),
                new SqlParameter("pnu_msj", Types.VARCHAR)               
                        );
        SqlParameterSource in = new MapSqlParameterSource()
        .addValue("pnu_ann", descargaMensaje.getNu_ann())
        .addValue("pnu_emi", descargaMensaje.getNu_emi())
        .addValue("pnu_des", descargaMensaje.getNu_des())
        .addValue("pnu_msj", descargaMensaje.getNu_msj());                
       
        try {
            this.spDescargamsj.execute(in);
            vReturn = "OK";
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }   
       
       
       
        return vReturn;   
    }

    @Override
    public MensajesConsulBean getBuscaDocumentosMsj(String nu_ann, String nu_emi, String nu_des, String nu_msj) {
    StringBuffer sql = new StringBuffer();
        MensajesConsulBean mensajesConsulBean=new MensajesConsulBean();
        
//        boolean bBusqFiltro = false;
//        
//        Map<String, Object> objectParam = new HashMap<String, Object>();
//
//        List<MensajesConsulBean> list = new ArrayList<MensajesConsulBean>();

        
        sql.append("SELECT  ");
        sql.append("A.NU_MSJ,TO_CHAR(M.FEC_ENVIOMSJ,'DD/MM/YYYY') FEC_ENVIOMSJ, A.NU_ANN,A.NU_EMI,A.NU_DES,PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM)|| ' ' ||DECODE (B.TI_EMI,");
        sql.append("'01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
        sql.append("'05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG,");
        sql.append("B.DE_DOC_SIG)");
        sql.append("DE_TIP_DOC,UPPER(B.DE_ASU) DE_ASU,A.CO_LOC_DES CO_LOCAL,NVL2(A.CO_LOC_DES,SUBSTR(PK_SGD_DESCRIPCION.DE_LOCAL(A.CO_LOC_DES), 1, 100),NULL) DE_LOCAL, ");
        sql.append("B.CO_DEP_EMI CO_DEPENDENCIA,");
        sql.append("NVL2(B.CO_DEP_EMI,SUBSTR(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.CO_DEP_EMI), 1, 100),NULL) DE_DEPENDENCIA,");                    
        sql.append("DECODE(A.TI_DES,");
        sql.append("'01', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES),");
        sql.append("'02', PK_SGD_DESCRIPCION.DE_PROVEEDOR (A.NU_RUC_DES),");
        sql.append("'03', PK_SGD_DESCRIPCION.ANI_SIMIL (A.NU_DNI_DES),");
        sql.append("'04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.CO_OTR_ORI_DES),");
        sql.append("'05', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES)");
        sql.append(")  DESTINATARIO,");
        sql.append("A.CO_MOT CO_TRAMITE,NVL2(A.CO_MOT,SUBSTR(PK_SGD_DESCRIPCION.MOTIVO(A.CO_MOT), 1, 100),NULL) DE_TRAMITE, ");
        sql.append("A.CO_PRI CO_PRIORIDAD, ");
        sql.append("A.DE_PRO DE_INDICACIONES, ");
        sql.append("A.TI_DES CO_TIPO_DESTINO,");
        sql.append("DECODE(A.TI_DES,");
        sql.append("'01', TRIM(DEP.DE_DIRECCION_LOCAL),");
        sql.append("'02', 'DIRECCION PROVEEDOR',");
        sql.append("'03', 'DIRECCION ASIMIL',");
        sql.append("'04', 'DIRECCION OTROS',");
        sql.append("'05', TRIM(DEP.DE_DIRECCION_LOCAL)) DIRECCION,");
        sql.append("DECODE(A.TI_DES,");
        sql.append("'01', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS),");
        sql.append("'02', 'DEPARTAMENTO PROVEEDOR',");
        sql.append("'03', 'DEPARTAMENTO ASIMIL',");
        sql.append("'04', 'DEPARTAMENTO OTROS',");
        sql.append("'05', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS)) DEPARTAMENTO,M.DE_TIP_MSJ,M.DE_TIP_ENV,");
        sql.append("(CASE WHEN M.DE_TIP_MSJ='COURRIER' THEN (SELECT TRIM(CPRO_RAZSOC) FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC=M.RE_ENV_MSJ) ELSE (SELECT TRIM(CEMP_APEPAT)||' '||TRIM(CEMP_APEMAT) ||' '|| TRIM(CEMP_DENOM) FROM RHTM_PER_EMPLEADOS WHERE CEMP_NU_DNI=M.RE_ENV_MSJ) END) RE_ENV_MSJ, ");
        sql.append("M.NU_SER_MSJ||'-'||M.AN_SER_MSJ NU_SERVICIO,");
        sql.append("NVL2(A.FE_ENT_MSJ,TO_CHAR(A.FE_ENT_MSJ,'DD/MM/YYYY'),' ') FE_ENT_MSJ, ");
        sql.append("NVL2(A.FE_DEV_MSJ,TO_CHAR(A.FE_DEV_MSJ,'DD/MM/YYYY'),' ') FE_DEV_MSJ,");
        sql.append("(SELECT DE_EST FROM TDTR_ESTADOS WHERE CO_EST=A.ES_DOC_REC AND DE_TAB='TD_MENSAJERIA') EST_MSJ,A.ES_DOC_REC COD_EST_MSJ,M.DE_AMBITO,M.FE_PLA_MSJ,A.ES_PEN_MSJ,NVL2(A.OB_MSJ,A.OB_MSJ,' ') OB_MSJ,NVL2(A.MO_MSJ_DEV,A.MO_MSJ_DEV,' ') MO_MSJ_DEV, "); 
        sql.append("NVL2(A.HO_ENT_MSJ,TO_CHAR(A.HO_ENT_MSJ,'HH24:MI'),' ') HO_ENT_MSJ,");
        sql.append("NVL2(A.HO_DEV_MSJ,TO_CHAR(A.HO_DEV_MSJ,'HH24:MI'),' ') HO_DEV_MSJ,");
        sql.append(" CASE WHEN TOTAL>0 THEN 'SI' ELSE 'NO' END tieneanexocargo,TO_CHAR(B.FE_EMI,'DD/MM/YYYY') FE_EMI,TO_CHAR(B.FEC_RECEPMP,'DD/MM/YYYY') FEC_RECEPMP,M.DIAS_PLA_DEVO,A.ES_PEN_DEV,M.PE_ENV_MSJ, ");
        sql.append(" A.nro_guia_devolucion,TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(A.NU_DES)||'-1' as nu_Acta_Vis1,TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(A.NU_DES)||'-2' as nu_Acta_Vis2, ");
        sql.append(" DM1.FEC_NOT_ACTA as fe_Acta_Vis1,NVL(DM1.ES_NOT_ACTA,2) as es_Acta1_msj, ");
        sql.append(" DM2.FEC_NOT_ACTA as fe_Acta_Vis2,NVL(DM2.ES_NOT_ACTA,2) as es_Acta2_msj, ");
        sql.append(" NVL(DM1.DE_RUT_ORI,'') as archivo_Acta1, ");
        sql.append(" NVL(DM2.DE_RUT_ORI,'') as archivo_Acta2, ");
        sql.append(" NVL(TO_CHAR(DM1.FEC_NOT_ACTA),' ') as fe_Acta_Vis1,NVL(DM1.ES_NOT_ACTA,2) as es_Acta1_msj, ");
        sql.append("  NVL(TO_CHAR(DM2.FEC_NOT_ACTA),' ') as fe_Acta_Vis2,NVL(DM2.ES_NOT_ACTA,2) as es_Acta2_msj, ");
        sql.append(" NVL(DM1.DE_RUT_ORI,' ') as archivo_Acta1, ");
        sql.append(" NVL(DM2.DE_RUT_ORI,' ') as archivo_Acta2, ");
        sql.append(" NVL(TO_CHAR(CARGO.NU_ANE),' ') as archivo_Cargo ");
        sql.append(" FROM TDTV_DESTINOS A INNER JOIN TDTV_REMITOS B ON A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI ");
        sql.append(" LEFT JOIN ( SELECT D.CO_DEPENDENCIA,D.DE_DEPENDENCIA,D.DE_CORTA_DEPEN,L.DE_NOMBRE_LOCAL,L.DE_DIRECCION_LOCAL,NODEP,NOPRV,NODIS ,L.CCOD_LOCAL ");
        sql.append(" FROM RHTM_DEPENDENCIA D ");
        sql.append(" INNER JOIN SITM_LOCAL_DEPENDENCIA DL ON DL.CO_DEP=D.CO_DEPENDENCIA ");
        sql.append(" INNER JOIN SI_MAE_LOCAL L ON L.CCOD_LOCAL=DL.CO_LOC ");
        sql.append(" LEFT JOIN IDTUBIAS U ON U.UBDEP||U.UBPRV||U.UBDIS =L.CO_UBIGEO) DEP ON DEP.CO_DEPENDENCIA=A.CO_DEP_DES AND DEP.CCOD_LOCAL=A.CO_LOC_DES ");
        sql.append(" INNER JOIN TD_MENSAJERIA M ON A.NU_MSJ=M.NU_MSJ "); 
        sql.append(" LEFT JOIN ( select  nu_ann,nu_emi,nu_des, COUNT(1) TOTAL    from tdtv_anexos  GROUP BY nu_ann,nu_emi,nu_des ) TB ON  TB.nu_ann = A.nu_ann  AND TB.nu_emi=A.nu_emi AND TB.nu_des=A.nu_des "); 
        sql.append(" LEFT JOIN TD_DET_MENSAJERIA DM1 ON DM1.NU_ANN=A.NU_ANN AND DM1.NU_EMI=A.NU_EMI AND DM1.NU_DES=A.NU_DES AND DM1.NU_ACTA=TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(A.NU_DES)||'-1' ");
        sql.append(" LEFT JOIN TD_DET_MENSAJERIA DM2 ON DM2.NU_ANN=A.NU_ANN AND DM2.NU_EMI=A.NU_EMI AND DM2.NU_DES=A.NU_DES AND DM2.NU_ACTA=TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(A.NU_DES)||'-2' ");
        sql.append(" LEFT JOIN TDTV_ANEXOS CARGO ON CARGO.NU_ANN=A.NU_ANN AND CARGO.NU_EMI=A.NU_EMI AND CARGO.NU_DES=A.NU_DES  ");//AND CARGO.DE_DET='CARGO_NOTIFICACION'||TO_CHAR(A.NU_DES)||'.pdf'
        sql.append(" WHERE  A.NU_ANN =? AND A.NU_EMI =? AND A.NU_DES =? AND  A.NU_MSJ =? ");
            // Parametros Basicos       
        
      /*  objectParam.put("pNuAnn", nu_ann);
        objectParam.put("pNuEmi", nu_emi);
        objectParam.put("pNuDes", nu_des);
        objectParam.put("pNuMsj", nu_msj);*/
                        
        sql.append("ORDER BY 3");
                      
 
//        System.out.println(sql.toString());
        try {
            //objectParam,
            mensajesConsulBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(MensajesConsulBean.class),
                    new Object[]{nu_ann, nu_emi, nu_des, nu_msj});

        } catch (EmptyResultDataAccessException e) {
//            list = null;
        } catch (Exception e) {
//            list = null;
            e.printStackTrace();
        }
        return mensajesConsulBean;
    }

    @Override
    public DocumentoObjBean getNombreArchivoMsj(String pnuAnn, String pnuEmi, String pnuAnexo) {
                StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "nu_ann,\n" +
                    "nu_emi,\n" +
                    "nu_ane,\n" +
                    "de_rut_ori nombre_Archivo,\n" +
                    "nvl(dbms_lob.getlength(bl_doc),0) nu_Tamano\n" +
                    "from tdtv_anexos\n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?\n" +
                    "and nu_des = ?");
        System.out.println(""+pnuAnn);
        System.out.println(""+pnuEmi);
        System.out.println(""+pnuAnexo);
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class),new Object[]{ pnuAnn,pnuEmi,pnuAnexo} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(docObjBean);
    }

    @Override
    public String deleteMsj(DescargaMensajeBean descargaMensaje) {
         String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer(); 
       
        sqlUpd.append("DELETE FROM TDTV_ANEXOS \n"
                + "WHERE\n"
                + "NU_ANN=? AND\n"
                + "NU_EMI=? AND\n"
                + "NU_DES=?");
        
        System.out.println(descargaMensaje.getNu_ann());
        System.out.println(descargaMensaje.getNu_emi());
        System.out.println(descargaMensaje.getNu_des());
        
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{descargaMensaje.getNu_ann(), descargaMensaje.getNu_emi(),
                descargaMensaje.getNu_des()});
                      
            vReturn = "OK";//+"NU_ANN:"+documentoEmiBean.getNuAnn().toString()+"\n NU_EMI:"+documentoEmiBean.getNuEmi();
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;   
    }

    @Override
    public String revMensajeriaDocumento(DescargaMensajeBean descargaMensaje) {
        String vReturn = "NO_OK";    
       
        this.spDescargamsj = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_MENSAJERIA").withProcedureName("revmsj")
        .withoutProcedureColumnMetaDataAccess()
        .useInParameterNames("pnu_ann", "pnu_emi", "pnu_des", "pnu_msj")
                //descargamsj(pnu_ann tdtv_remitos.nu_ann%TYPE,pnu_emi tdtv_remitos.nu_emi%TYPE,pnu_des tdtv_destinos.nu_des%TYPE,pnu_msj tdtv_destinos.nu_msj%TYPE,pfecha tdtv_destinos.fec_finmsj%TYPE,pob_msj tdtv_destinos.ob_msj%TYPE,pmo_msj_dev tdtv_destinos.mo_msj_dev%TYPE,pes_pen_msj tdtv_destinos.es_pen_msj%TYPE,pest VARCHAR2);
        .declareParameters(
                new SqlParameter("pnu_ann", Types.VARCHAR),
                new SqlParameter("pnu_emi", Types.VARCHAR),
                new SqlParameter("pnu_des", Types.VARCHAR),
                new SqlParameter("pnu_msj", Types.VARCHAR)               
                        );
        SqlParameterSource in = new MapSqlParameterSource()
        .addValue("pnu_ann", descargaMensaje.getNu_ann())
        .addValue("pnu_emi", descargaMensaje.getNu_emi())
        .addValue("pnu_des", descargaMensaje.getNu_des())
        .addValue("pnu_msj", descargaMensaje.getNu_msj());                
       
        try {
            this.spDescargamsj.execute(in);
            System.out.println("se revirtio");
            vReturn = "OK";
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }   
       
       
       
        return vReturn;   
    }

    @Override
    public List<DependenciaBean> getLsOficina() {
        StringBuffer sql = new StringBuffer();
       
        sql.append("SELECT deDependencia,coDependencia FROM (SELECT NVL2(CO_DEP_EMI,SUBSTR(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(CO_DEP_EMI), 1, 100),NULL) deDependencia,CO_DEP_EMI coDependencia \n" +
                    "FROM TDTV_REMITOS \n" +
                    "WHERE CO_DEP_EMI IS NOT NULL \n" +
                    "UNION SELECT '.: TODOS :.',NULL \n" +
                    "FROM DUAL ) TB \n" +
                    "ORDER BY deDependencia");
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(DependenciaBean.class));
            
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public String selectCalFechaPlazo(DescargaMensajeBean descargaMensaje) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        StringBuffer sqlQry2 = new StringBuffer();
        
        try{
            
            sqlQry2.append("select TO_CHAR(PK_SGD_CONSULTAS.CALCULAR_DIAS_HABILES(TO_DATE('"+descargaMensaje.getFe_ent_msj()+"','DD/MM/YYYY'),1),'DD/MM/YYYY') FROM DUAL");  
            String snufechai = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class);    
             
            //System.out.println("fechai==>"+snufechai);
            int diadev=Integer.parseInt(descargaMensaje.getDi_pla_dev())-1;
            if (diadev<0)
            {
                diadev=0;
            }
            sqlQry.append("select TO_CHAR(PK_SGD_CONSULTAS.CALCULAR_DIAS_HABILES(TO_DATE('"+snufechai+"','DD/MM/YYYY'),"+diadev+"),'DD/MM/YYYY') FROM DUAL");  
            String snufecha = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);    
            descargaMensaje.setFe_pla_dev(snufecha); 
            /*System.out.println("diasdev==>"+descargaMensaje.getDi_pla_dev());
            System.out.println("fechadev==>"+snufecha);*/
            
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }

    @Override
    public List<SiElementoBean> getLsMotivo(String pctabCodtab) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CELE_DESELE,CELE_CODELE,NELE_NUMSEC,CELE_DESELE||'|'||(CASE WHEN LENGTH(CELE_DESCOR)=0 OR CELE_DESCOR IS NULL THEN '0|0|0' ELSE CELE_DESCOR END) CELE_DESCOR\n" +
                   "FROM   SI_ELEMENTO\n" +
                   "WHERE  CTAB_CODTAB = ? \n" +
                   "ORDER BY CELE_CODELE");

        List<SiElementoBean> list = null;
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(SiElementoBean.class),
                    new Object[]{pctabCodtab});
        }catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    
}
