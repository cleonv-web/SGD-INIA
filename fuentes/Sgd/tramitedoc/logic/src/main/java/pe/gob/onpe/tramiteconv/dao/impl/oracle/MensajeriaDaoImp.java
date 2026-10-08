package pe.gob.onpe.tramiteconv.dao.impl.oracle;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepMensajeriaBean;
import pe.gob.onpe.tramitedoc.bean.DestiDocumentoEnvMensajeriaBean;
import pe.gob.onpe.tramitedoc.bean.DestinoResBen;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepMensajeriaBean;
import pe.gob.onpe.tramitedoc.bean.TipoElementoMensajeriaBean;
import pe.gob.onpe.tramitedoc.dao.MensajeriaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
/**
 *
 * @author wcondori
 */
@Repository("mensajeriaDao")
public class MensajeriaDaoImp extends SimpleJdbcDaoBase  implements MensajeriaDao{ 
    
    @Override
    public List<DocumentoRecepMensajeriaBean> getDocumentoRecepMensajeria(BuscarDocumentoRecepMensajeriaBean buscarDocumentoRecepMensajeriaBean) {
        boolean bBusqFiltro = false;
        boolean bBusqDep = false;
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();        
        sql.append("SELECT  ROWNUM,NU_ANN,NU_EMI,\n" +
                "     PK_SGD_DESCRIPCION.DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,FE_EMI_CORTA,TOTAL_DESTINO,TOTAL_ENVIADO,TOTAL_PENDIENTE,\n" +
                "        PK_SGD_DESCRIPCION.DE_DEPENDENCIA (X.CO_DEP_EMI) DE_DEPENDENCIA,\n" +                
                "            DE_ASU,\n" +
                "         DECODE (X.TI_EMI,\n" +
                "         		'01', X.NU_DOC_EMI || '-' || X.NU_ANN || '/' || X.DE_DOC_SIG,\n" +
                "         		'05', X.NU_DOC_EMI || '-' || X.NU_ANN || '/' || X.DE_DOC_SIG,\n" +
                "         		X.DE_DOC_SIG\n" +
                "         	   ) NU_DOC,\n" +
                "         DECODE (X.NU_CANDES,\n" +
                "         		1, PK_SGD_DESCRIPCION.TI_DES_EMP (X.NU_ANN, X.NU_EMI),\n" +
                "         		PK_SGD_DESCRIPCION.TI_DES_EMP_V (X.NU_ANN, X.NU_EMI)\n" +
                "         	   ) DE_EMP_DES, \n" +
                "         DE_ES_DOC_EMI_MP, DOC_ESTADO_MSJ, FEC_RECEPMP,      \n" +  //PK_SGD_DESCRIPCION.ESTADOS_MP(X.ES_DOC_EMI,'TDTV_REMITOS')
                "         PK_SGD_DESCRIPCION.DE_LOCAL(X.CO_LOC_EMI) DE_LOC_EMI \n" +
                "        ,FEC_ENVIOMSJ, DE_AMBITO,DE_TIP_ENV,NU_SER_MSJ, FE_PLA_MSJ,DIAS_PLA_DEVO  diasDevoluvion ,DIAS_PLA_ENTR diasEntrega,TO_CHAR(FE_ENV_MES,'DD/MM/YYYY hh24:mi:ss') fechaenvioamensajeria \n"+
                "         FROM ( \n" +
                "         SELECT A.NU_ANN,A.NU_EMI ,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n" +
                "         A.DE_ASU,A.CO_DEP_EMI,A.CO_EMP_EMI,A.CO_OTR_ORI_EMI,A.CO_TIP_DOC_ADM,A.NU_DOC_EMI,A.DE_DOC_SIG,\n" +
                "          DE_ES_DOC_EMI_MP, DOC_ESTADO_MSJ,TO_CHAR(FEC_RECEPMP,'DD/MM/YYYY hh24:mi:ss') FEC_RECEPMP, \n" +
                "         A.TI_EMI,A.NU_RUC_EMI,A.NU_DNI_EMI,A.NU_CANDES,A.ES_DOC_EMI,A.CO_LOC_EMI,\n" +
                "         A.CO_DEP,TO_CHAR(NVL(TOTAL_DESTINO,0)) TOTAL_DESTINO ,TO_CHAR(NVL(TOTAL_ENVIADO,0))TOTAL_ENVIADO ,TO_CHAR(NVL(TOTAL_PENDIENTE,0)) TOTAL_PENDIENTE\n" +
                "        ,FEC_ENVIOMSJ, DE_AMBITO,DE_TIP_ENV,NU_SER_MSJ, FE_PLA_MSJ \n"+
                "        ,  C.DIAS_PLA_DEVO,C.DIAS_PLA_ENTR,A.FE_ENV_MES \n"+
                "          FROM TDTV_REMITOS A INNER JOIN TDTX_REMITOS_RESUMEN B ON A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI\n" +
                "        LEFT JOIN (SELECT NU_ANN,NU_EMI, NVL(COUNT(NU_DES),0) TOTAL_DESTINO FROM TDTV_DESTINOS WHERE TI_DES<>'01' AND ES_DOC_REC IN  ('0','1','2','3','4') GROUP BY NU_ANN,NU_EMI ) E ON A.NU_ANN = E.NU_ANN AND A.NU_EMI = E.NU_EMI\n" +
                "        LEFT JOIN (SELECT NU_ANN,NU_EMI,NVL(COUNT(NU_DES),0) TOTAL_ENVIADO ,M.FEC_ENVIOMSJ,M.DIAS_PLA_DEVO,M.DIAS_PLA_ENTR, M.DE_AMBITO,M.DE_TIP_ENV,M.NU_SER_MSJ||'-'||M.AN_SER_MSJ NU_SER_MSJ, M.FE_PLA_MSJ\n" +
                "                   FROM TDTV_DESTINOS D LEFT JOIN TD_MENSAJERIA M ON M.NU_MSJ=D.NU_MSJ\n" +
                "                   WHERE D.TI_DES <>'01' AND ES_DOC_REC IN ('2','3','4') GROUP BY NU_ANN,NU_EMI\n" +
                "                   ,M.FEC_ENVIOMSJ, M.DIAS_PLA_DEVO,M.DIAS_PLA_ENTR,M.DE_AMBITO,M.DE_TIP_ENV,M.NU_SER_MSJ,M.AN_SER_MSJ, M.FE_PLA_MSJ) C ON A.NU_ANN = C.NU_ANN AND A.NU_EMI = C.NU_EMI \n" +
                "        LEFT JOIN (SELECT NU_ANN,NU_EMI, NVL(COUNT(NU_DES),0) TOTAL_PENDIENTE FROM TDTV_DESTINOS WHERE TI_DES <>'01' AND ES_DOC_REC IN ('0','1') GROUP BY NU_ANN,NU_EMI) D ON A.NU_ANN = D.NU_ANN AND A.NU_EMI = D.NU_EMI\n" +
                "     LEFT JOIN (SELECT CO_EST,DE_EST DE_ES_DOC_EMI_MP FROM TDTR_ESTADOS WHERE DE_TAB='ENVIO_DOCUMENTO_MSJ') EST ON EST.CO_EST= A.DOC_ESTADO_MSJ \n"+
                "  WHERE NOT A.DOC_ESTADO_MSJ IS NULL AND TI_ENV_MSJ='0'   ");        
//        String pNuAnn = buscarDocumentoRecepMensajeriaBean.getCoAnnio();
        String pEsFiltroFecha = buscarDocumentoRecepMensajeriaBean.getEsFiltroFecha();
        String pEsFiltroFechaENVmSJ = buscarDocumentoRecepMensajeriaBean.getEsFiltroFechaEnvMsj();
        
//        if (!(pEsFiltroFecha.equals("1")&&pNuAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN = :pNuAnn");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNuAnn);
//        }
        sql.append(" AND A.CO_GRU = :pCoGru");        
        objectParam.put("pCoGru", buscarDocumentoRecepMensajeriaBean.getCoGrupo());
//        sql.append(" AND A.ES_DOC_EMI<>9");
        //sql.append(" AND A.TI_EMI<>'01'");
        sql.append(" AND A.ES_ELI='0'");
        //sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi");        
        //objectParam.put("pCoDepEmi", buscarDocumentoRecepMensajeriaBean.getCoDepEmi());

        String pTipoBusqueda = buscarDocumentoRecepMensajeriaBean.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoRecepMensajeriaBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        
        //String auxTipoAcceso=buscarDocumentoRecepMensajeriaBean.getTiAcceso();
        //String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
        //if(tiAcceso.equals("1")){//acceso personal
        //    sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
        //    objectParam.put("pcoEmpRes", buscarDocumentoRecepMensajeriaBean.getCoEmpleado());            
        //}else {//acceso total
            //if(buscarDocumentoRecepMensajeriaBean.getInMesaPartes().equals("0") /*&& buscarDocumentoExtRecepBean.getInCambioEst().equals("0")*/){
             //   bBusqDep = true;
                //sql.append(" AND A.CO_DEP = :pCoDep");        
        sql.append(" AND A.COD_DEP_MSJ = :pCoDep");   
        objectParam.put("pCoDep", buscarDocumentoRecepMensajeriaBean.getCoDependencia());                   
           // }
        //} 
        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            
             if (buscarDocumentoRecepMensajeriaBean.getCoDependenciaBusca()!= null && buscarDocumentoRecepMensajeriaBean.getCoDependenciaBusca().trim().length() > 0) {
                sql.append(" AND A.CO_DEP_EMI = :pCoDependenciaBusca ");
                objectParam.put("pCoDependenciaBusca", buscarDocumentoRecepMensajeriaBean.getCoDependenciaBusca());
            }   
            
            if (buscarDocumentoRecepMensajeriaBean.getCoTipoDoc()!= null && buscarDocumentoRecepMensajeriaBean.getCoTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoRecepMensajeriaBean.getCoTipoDoc());
            }            
            if (buscarDocumentoRecepMensajeriaBean.getCoEstadoDoc()!= null && buscarDocumentoRecepMensajeriaBean.getCoEstadoDoc().trim().length() > 0) {
                //sql.append(" AND A.ES_DOC_EMI = :pCoEsDocEmi ");
                sql.append(" AND A.DOC_ESTADO_MSJ = :pCoEsDocEmi ");
                objectParam.put("pCoEsDocEmi", buscarDocumentoRecepMensajeriaBean.getCoEstadoDoc());
            }
            if(buscarDocumentoRecepMensajeriaBean.getCoTipoEmisor()!= null && buscarDocumentoRecepMensajeriaBean.getCoTipoEmisor().trim().length() > 0){
                sql.append(" AND A.TI_EMI = :pCoTipoEmisor ");
                objectParam.put("pCoTipoEmisor", buscarDocumentoRecepMensajeriaBean.getCoTipoEmisor());                
            }
            /*if (buscarDocumentoExtRecepBean.getCoLocEmi()!= null && buscarDocumentoExtRecepBean.getCoLocEmi().trim().length() > 0 && !bBusqLocal) {
                sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi ");
                objectParam.put("pcoLocEmi", buscarDocumentoExtRecepBean.getCoLocEmi());
            } */     
            if (buscarDocumentoRecepMensajeriaBean.getCoDepOriRec()!= null && buscarDocumentoRecepMensajeriaBean.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                sql.append(" AND A.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", buscarDocumentoRecepMensajeriaBean.getCoDepOriRec());                
            }
            if (buscarDocumentoRecepMensajeriaBean.getEsFiltroFecha() != null  && (pEsFiltroFecha.equals("1") || pEsFiltroFecha.equals("3") || pEsFiltroFecha.equals("2"))) {
                String vFeEmiIni = buscarDocumentoRecepMensajeriaBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoRecepMensajeriaBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0  && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'DD/MM/YYYY') AND TO_DATE(:pFeEmiFin,'DD/MM/YYYY') + 0.99999");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
            if (buscarDocumentoRecepMensajeriaBean.getEsFiltroFechaEnvMsj()!= null  && (pEsFiltroFechaENVmSJ.equals("1") || pEsFiltroFechaENVmSJ.equals("3") || pEsFiltroFechaENVmSJ.equals("2"))) {
                String vFeEmiIni = buscarDocumentoRecepMensajeriaBean.getFeEmiIniEnvMSJ();
                String vFeEmiFin = buscarDocumentoRecepMensajeriaBean.getFeEmiFinEnvMSJ();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0  && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_ENV_MES between TO_DATE(:pFeEmiIniEnv,'DD/MM/YYYY') AND TO_DATE(:pFeEmiFinEnv,'DD/MM/YYYY') + 0.99999");
                    objectParam.put("pFeEmiIniEnv", vFeEmiIni);
                    objectParam.put("pFeEmiFinEnv", vFeEmiFin);
                }
            }
        }   
        
        //Busqueda
        //if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoRecepMensajeriaBean.getBusNumDoc() != null && buscarDocumentoRecepMensajeriaBean.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoRecepMensajeriaBean.getBusNumDoc());
            }
            if (buscarDocumentoRecepMensajeriaBean.getBusNumExpediente() != null && buscarDocumentoRecepMensajeriaBean.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoRecepMensajeriaBean.getBusNumExpediente());
            }
            if (buscarDocumentoRecepMensajeriaBean.getBusAsunto() != null && buscarDocumentoRecepMensajeriaBean.getBusAsunto().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||UPPER(:pDeAsunto)||'%' ");
                objectParam.put("pDeAsunto", buscarDocumentoRecepMensajeriaBean.getBusAsunto());
            }            
      //  }        
          if (buscarDocumentoRecepMensajeriaBean.getBusDesti()!= null && buscarDocumentoRecepMensajeriaBean.getBusDesti().trim().length() > 0  ) {
                sql.append("  AND UPPER (DECODE (A.NU_CANDES,\n" +
                        " 1, PK_SGD_DESCRIPCION.TI_DES_EMP (A.NU_ANN, A.NU_EMI),\n" +
                        " PK_SGD_DESCRIPCION.TI_DES_EMP_V (A.NU_ANN, A.NU_EMI)\n" +
                        "))  LIKE '%'||UPPER(:pDesti)||'%' ");
                
                objectParam.put("pDesti", buscarDocumentoRecepMensajeriaBean.getBusDesti());
            }  
          
        sql.append(" ORDER BY A.NU_COR_EMI DESC");
        sql.append(") X ");
        sql.append("WHERE ROWNUM < 51");        
        
        List<DocumentoRecepMensajeriaBean> list = new ArrayList<DocumentoRecepMensajeriaBean>();

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(),objectParam,BeanPropertyRowMapper.newInstance(DocumentoRecepMensajeriaBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;             
        }catch (Exception e) {
            list = null;
            e.printStackTrace();            
        }
        return list;
    }
    @Override
    public List<DestiDocumentoEnvMensajeriaBean> getLstDestinoEnvMensajeria(String pnuAnnpnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestiDocumentoEnvMensajeriaBean> list = null;
        sql.append("select ROWNUM fila, a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,NVL2(a.co_loc_des,substr(PK_SGD_DESCRIPCION.DE_LOCAL(a.co_loc_des), 1, 100),NULL) de_local, \n" +
"                    B.co_dep_emi co_dependencia,\n" +
"                    NVL2(B.co_dep_emi,substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.co_dep_emi), 1, 100),NULL) de_dependencia, \n" +
"                  --  a.co_emp_des co_empleado,\n" +
"                    --NVL2(a.co_emp_des,substr(PK_SGD_DESCRIPCION.DE_NOM_EMP(a.co_emp_des), 1, 100),NULL) de_empleado, \n" +
"                    PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM) documento,\n" +
"                    DECODE(A.TI_DES,\n" +
"                            '01', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.co_dep_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.co_emp_des),\n" +
"                            '02', PK_SGD_DESCRIPCION.DE_PROVEEDOR (A.NU_RUC_DES),\n" +
"                            '03', PK_SGD_DESCRIPCION.ANI_SIMIL (A.NU_DNI_DES),\n" +
"                            '04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.CO_OTR_ORI_DES),\n" +
"                            '05', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.co_dep_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES)\n" +
"                            )  destinatario,\n" +
"                    a.co_mot co_tramite,NVL2(A.co_mot,substr(PK_SGD_DESCRIPCION.MOTIVO(a.co_mot), 1, 100),NULL) de_tramite, \n" +
"                    a.co_pri co_prioridad, \n" +
"                    a.de_pro de_indicaciones, \n" +
"                    a.ti_des co_tipo_destino,\n" +
"                      a.CDIR_REMITE direccion,\n" +
"                            DECODE(A.TI_DES,\n" +
"                            '01', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS),\n" +
"                            '02', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),\n" +
"                            '03', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),\n" +
"                            '04', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),\n" +
"                            '05', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS)\n" +
"                            ) departamento,CCOD_TIPO_UBI AS ambito,\n" +
"                 DECODE (B.TI_EMI, \n" +
"                                 '01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG, \n" +
"                                '05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG, \n" +
"                                 B.DE_DOC_SIG ) NU_DOC,TO_CHAR(B.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,a.CCOD_DPTO,a.CCOD_PROV,a.CCOD_DIST \n" +
"                    FROM tdtv_destinos a INNER JOIN TDTV_REMITOS B ON a.NU_ANN = B.NU_ANN AND a.NU_EMI = B.NU_EMI\n" +
"                    LEFT JOIN ( SELECT D.CO_DEPENDENCIA,D.DE_DEPENDENCIA,D.DE_CORTA_DEPEN,L.DE_NOMBRE_LOCAL,L.DE_DIRECCION_LOCAL,NODEP,NOPRV,NODIS ,L.CCOD_LOCAL\n" +
"                                FROM RHTM_DEPENDENCIA D \n" +
"                                INNER JOIN SITM_LOCAL_DEPENDENCIA DL ON DL.CO_DEP=D.CO_DEPENDENCIA\n" +
"                                INNER JOIN SI_MAE_LOCAL L ON L.CCOD_LOCAL=DL.CO_LOC\n" +
"                                LEFT JOIN IDTUBIAS U ON U.UBDEP||U.UBPRV||U.UBDIS =L.CO_UBIGEO  ) DEP ON DEP.CO_DEPENDENCIA=A.co_dep_DES AND DEP.CCOD_LOCAL=a.co_loc_des " +
                " LEFT JOIN IDTUBIAS UBIDESTINO ON UBIDESTINO.UBDEP||UBIDESTINO.UBPRV||UBIDESTINO.UBDIS =a.CCOD_DPTO||a.CCOD_PROV||a.CCOD_DIST  \n"+
                    "where a.TI_DES <>'01' AND a.nu_ann || a.nu_emi in ("+pnuAnnpnuEmi+") \n" +
                    "AND a.ES_ELI='0' AND  a.ES_DOC_REC='1'  \n" +
                    "order by 1");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestiDocumentoEnvMensajeriaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    
    
    @Override
    public List<TipoElementoMensajeriaBean> getlistTipoElementoMensajeria(String tipo){
                StringBuffer sql = new StringBuffer();        
        sql.append("select CELE_DESELE de_destinatario,CELE_CODELE co_destinatario from SI_ELEMENTO WHERE CTAB_CODTAB=? \n" +
                    "ORDER BY CELE_CODELE ");         
        List<TipoElementoMensajeriaBean> list = new ArrayList<TipoElementoMensajeriaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoElementoMensajeriaBean.class)
                    , new Object[]{tipo});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<TipoElementoMensajeriaBean> getListResponsableMensajeria(String tipo,String Ambito,String tipoEnvio){
        StringBuffer sql = new StringBuffer();         
        //if (tipo.equals("MOTORIZADO")){
        //sql.append("SELECT CEMP_NU_DNI codigo,CEMP_APEPAT||' '||CEMP_APEMAT ||' '|| CEMP_DENOM nombre FROM RHTM_PER_EMPLEADOS WHERE CEMP_CO_CARGO='038'");         
        //}
        if(tipo.equals("COURRIER")){        //NELE_NUMSEC
        //sql.append("SELECT CPRO_RUC codigo ,CPRO_RAZSOC nombre FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC IN ( select CELE_DESELE from SI_ELEMENTO WHERE CTAB_CODTAB='DE_COURRIER' AND CELE_CODELE2 IN (SELECT  CELE_CODELE FROM  si_elemento WHERE CTAB_CODTAB='DE_AMBITO_MENS' AND cele_desele='"+Ambito+"')  ) ");
        sql.append("SELECT CPRO_RUC codigo ,CPRO_RAZSOC nombre FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC IN (  select CU.CELE_DESELE from SI_ELEMENTO CU \n" +
                    " INNER JOIN SI_ELEMENTO AM ON CU.cele_codele2=AM.cele_codele AND AM.CTAB_CODTAB='DE_AMBITO_MENS' \n" +
                    " INNER JOIN SI_ELEMENTO TENV ON CU.cele_codele3=TENV.cele_codele AND TENV.CTAB_CODTAB='RE_ENV_MSJ_MENS' \n" +
                    " WHERE CU.CTAB_CODTAB='DE_COURRIER' AND AM.cele_desele='"+Ambito+"' AND TENV.cele_desele='"+tipoEnvio+"' ) ");
        }
        else {
          sql.append("select NULEM codigo,DEAPP||' '||DEAPM||' '||DENOM nombre from TDTX_ANI_SIMIL where NULEM in (select CELE_DESELE from SI_ELEMENTO WHERE CTAB_CODTAB='"+tipo+"')");      
        }
        List<TipoElementoMensajeriaBean> list = new ArrayList<TipoElementoMensajeriaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoElementoMensajeriaBean.class) );
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public String insMensajeriaDocumento(DocumentoRecepMensajeriaBean documentoMensajeria) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry1 = new StringBuffer();
        StringBuffer sqlQry2 = new StringBuffer();
        sqlQry1.append("select SEC_TD_MENSAJERIA.NextVal from dual"); 
        sqlQry2.append("select to_char(sysdate,'dd/mm/yyyy hh24:mi:ss') from dual");        
        StringBuffer sqlIns = new StringBuffer();
        StringBuffer sqlUpd = new StringBuffer();
        StringBuffer sqlUpdRemito = new StringBuffer();
        StringBuffer sqlUpdRemitoParcial = new StringBuffer();
        sqlIns.append("INSERT INTO  td_mensajeria ( nu_msj,fe_reg_msj,co_use_cre,de_ambito,de_tip_msj,re_env_msj,\n" +
                    "    de_tip_env,nu_ser_msj, an_ser_msj,fec_enviomsj,ho_env_msj,fe_pla_msj,ho_pla_msj,flag_msj,dias_pla_entr,dias_pla_devo ,PE_ENV_MSJ,IN_ENV_SEDE_LOCAL,DE_OBS_MSJ) \n" +
                    "    VALUES(?,(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),?,?,?,?,?,?,?,(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),'0',?,?,?,?,?)");
//                    "    de_tip_env,nu_ser_msj, an_ser_msj,fec_enviomsj,ho_env_msj,fe_pla_msj,ho_pla_msj,flag_msj,dias_pla_entr,dias_pla_devo ) \n" +
//                    "    VALUES(?,(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),?,?,?,?,?,?,?,(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),(select to_date(?,'dd/mm/yyyy hh24:mi:ss') from dual),'0',?,?)");
        
        try{
            String snuNumsj = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class);            
            String snufecha = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class);    
            documentoMensajeria.setNumsj(snuNumsj);
            documentoMensajeria.setFeregmsj(snufecha);
            sqlUpd.append("UPDATE TDTV_DESTINOS SET ES_DOC_REC='2', NU_MSJ='"+snuNumsj+"', FEC_ENVIOMSJ= SYSDATE  WHERE  NU_ANN||NU_EMI||NU_DES in ( "+ documentoMensajeria.getCodigo()+" ) ");
            sqlUpdRemito.append("UPDATE TDTV_REMITOS  SET  FEC_ENVIODEP=SYSDATE,DOC_ESTADO_MSJ='2' \n" +
                    " WHERE  NU_ANN||NU_EMI IN ( SELECT NU_ANN||NU_EMI FROM  TDTV_DESTINOS WHERE NU_ANN||NU_EMI||NU_DES in (  "+documentoMensajeria.getCodigo()+"  ) ) ");
            //sqlUpdRemitoParcial.append("UPDATE TDTV_REMITOS  SET  FEC_ENVIODEP=SYSDATE,DOC_ESTADO_MSJ='3' \n" +
             //       " WHERE  NU_ANN||NU_EMI IN ( SELECT NU_ANN||NU_EMI FROM  TDTV_DESTINOS WHERE ES_DOC_REC<>'2' and NU_ANN||NU_EMI||NU_DES in (  "+documentoMensajeria.getCodigo()+"  ) ) ");
            sqlUpdRemitoParcial.append("UPDATE TDTV_REMITOS  SET  FEC_ENVIODEP=SYSDATE,DOC_ESTADO_MSJ='3' \n" +
"                     WHERE  NU_ANN||NU_EMI IN (SELECT NU_ANN||NU_EMI FROM  TDTV_DESTINOS WHERE ES_DOC_REC<>'2' \n" +
"                     and NU_ANN||NU_EMI in ( SELECT NU_ANN||NU_EMI FROM  TDTV_DESTINOS WHERE   NU_ANN||NU_EMI||NU_DES in (  "+documentoMensajeria.getCodigo()+"  )  ) )");
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{snuNumsj,snufecha,
                documentoMensajeria.getCousecre(),documentoMensajeria.getDeambito(), documentoMensajeria.getDetipmsj(), documentoMensajeria.getReenvmsj(),
                documentoMensajeria.getDetipenv(), documentoMensajeria.getNusermsj(),documentoMensajeria.getAnsermsj(),documentoMensajeria.getFecenviomsj(),
                documentoMensajeria.getHoenvmsj(),documentoMensajeria.getFeplamsj(),documentoMensajeria.getHoplamsj(),documentoMensajeria.getDiasEntrega(),
                documentoMensajeria.getDiasDevoluvion(),documentoMensajeria.getCalculaPenalizacion(),documentoMensajeria.getIn_env_sede_local(),documentoMensajeria.getDe_obs_msj()});
//                documentoMensajeria.getHoenvmsj(),documentoMensajeria.getFeplamsj(),documentoMensajeria.getHoplamsj(),documentoMensajeria.getDiasEntrega(),documentoMensajeria.getDiasDevoluvion()});
            this.jdbcTemplate.update(sqlUpd.toString());            
            this.jdbcTemplate.update(sqlUpdRemito.toString());          
            this.jdbcTemplate.update(sqlUpdRemitoParcial.toString());          
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Mensajeria Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    @Override
    public String updMensajeriaDocumentoRecibir(String codigo) {
        String vReturn = "NO_OK";    
        StringBuffer sqlIns = new StringBuffer(); 
        StringBuffer sqlUpd = new StringBuffer(); 
        sqlIns.append("UPDATE TDTV_REMITOS  SET FEC_RECEPMP=SYSDATE,DOC_ESTADO_MSJ='1'  WHERE  NU_ANN||NU_EMI IN ("+codigo+" ) ");
        sqlUpd.append("UPDATE TDTV_DESTINOS SET ES_DOC_REC='1'  WHERE TI_DES <>'01' AND NU_ANN||NU_EMI in ( "+ codigo +" ) ");
        try{
            this.jdbcTemplate.update(sqlIns.toString() );
            this.jdbcTemplate.update(sqlUpd.toString() );
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Mensajeria Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
      @Override
    public String updMensajeriaDocumentoDevolver(String codigo) {
        String vReturn = "NO_OK";    
        StringBuffer sqlIns = new StringBuffer();
        StringBuffer sqlUpd = new StringBuffer();  
        sqlIns.append("UPDATE TDTV_REMITOS  SET  DOC_ESTADO_MSJ=NULL,COD_DEP_MSJ=NULL ,TI_ENV_MSJ=NULL  WHERE  NU_ANN||NU_EMI IN ("+codigo+" ) ");        
        sqlUpd.append("UPDATE TDTV_DESTINOS SET ES_DOC_REC='0'  WHERE TI_DES <>'01' AND NU_ANN||NU_EMI in ( "+ codigo +" ) ");
        try{
            this.jdbcTemplate.update(sqlIns.toString() );
            this.jdbcTemplate.update(sqlUpd.toString() ); 
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Mensajeria Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    @Override
    public String selectCalcularFechaPlazo(DocumentoRecepMensajeriaBean documentoMensajeria) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry0 = new StringBuffer();
        StringBuffer sqlQry1 = new StringBuffer();
        StringBuffer sqlQry2 = new StringBuffer();
        sqlQry0.append("SELECT TO_CHAR(NELE_NUMSEC) FROM SI_ELEMENTO WHERE CTAB_CODTAB='DE_TIP_MSJ_MENS' AND CELE_DESELE='"+documentoMensajeria.getDetipmsj()+"'");  
        if(documentoMensajeria.getDetipmsj().equals("COURRIER")){
           sqlQry1.append("SELECT  CELE_DESCOR FROM  si_elemento WHERE CTAB_CODTAB='DE_COURRIER' AND cele_desele='"+documentoMensajeria.getDeNuRuc()+"'  \n" +
                    "AND cele_codele2 IN (SELECT  cele_codele FROM  si_elemento WHERE CTAB_CODTAB='DE_AMBITO_MENS' AND cele_desele='"+documentoMensajeria.getDeambito()+"') \n" +
                    "AND cele_codele3 IN (SELECT  cele_codele FROM  si_elemento WHERE CTAB_CODTAB='RE_ENV_MSJ_MENS' AND cele_desele='"+documentoMensajeria.getDetipenv()+"') "  );
        }
        else {
           sqlQry1.append("SELECT  CELE_DESCOR FROM  si_elemento WHERE CTAB_CODTAB='"+documentoMensajeria.getDetipmsj()+"' AND cele_desele='"+documentoMensajeria.getDeNuRuc()+"'  ");
        }
        try{
            String confCalculaPena = this.jdbcTemplate.queryForObject(sqlQry0.toString(), String.class);
            if(confCalculaPena.equals("1")){
                String sdias = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class);
                 String sdiasDev="";
                 if(sdias!=""){
                 String[] dias = sdias.split(Pattern.quote("|"));
                 if(dias.length>0){
                     sdias=dias[0];
                     sdiasDev=dias[1];
                 }else {
                 sdias="-1";
                 sdiasDev="-1";
                 }
                 }
                 else {
                 sdias="-1";
                 sdiasDev="-1";
                 }
                sqlQry2.append("select TO_CHAR(PK_SGD_CONSULTAS.CALCULAR_DIAS_HABILES(TO_DATE('"+documentoMensajeria.getFecenviomsj()+"','DD/MM/YYYY'),"+sdias+"),'DD/MM/YYYY') FROM DUAL");  
                String snufecha = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class); 

                documentoMensajeria.setFeVence(snufecha);  
                documentoMensajeria.setDiasDevoluvion(sdiasDev); 
                documentoMensajeria.setDiasEntrega(sdias); 
                
            }
            else {
            documentoMensajeria.setFeVence(documentoMensajeria.getFecenviomsj());  
            documentoMensajeria.setDiasDevoluvion("0"); 
            documentoMensajeria.setDiasEntrega("0"); 
            }
            documentoMensajeria.setCalculaPenalizacion(confCalculaPena); 
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    
    @Override
    public List<DocumentoRecepMensajeriaBean> getLstDetalleMensajeria(DestinoResBen oDestinoResBen) {
        StringBuffer sql = new StringBuffer();
        List<DocumentoRecepMensajeriaBean> list = null;
        sql.append("SELECT M.NU_MSJ as numsj,M.DE_TIP_MSJ as detipmsj,\n" +
                    "(CASE WHEN M.DE_TIP_MSJ='MOTORIZADO' THEN R.CEMP_APEPAT||' '||R.CEMP_APEMAT ||' '|| R.CEMP_DENOM\n" +
                    "ELSE P.CPRO_RAZSOC\n" +
                    "END) as reenvmsj\n" +
                    ",M.FEC_ENVIOMSJ,M.DE_TIP_ENV,\n" +
                    "PK_SGD_DESCRIPCION.ESTADOS (D.ES_DOC_REC,'TD_MENSAJERIA') docEstadoMsj,FE_PLA_MSJ,\n" +
                    " TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(D.NU_DES)||'-1' as nu_Acta_Vis1,TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(D.NU_DES)||'-2' as nu_Acta_Vis2, "+
                    "  NVL(TO_CHAR(DM1.FEC_NOT_ACTA),' ') as fe_Acta_Vis1,NVL(DM1.ES_NOT_ACTA,2) as es_Acta1_msj, "+
                    "  NVL(TO_CHAR(DM2.FEC_NOT_ACTA),' ') as fe_Acta_Vis2,NVL(DM2.ES_NOT_ACTA,2) as es_Acta2_msj, "+
                    " NVL(DM1.DE_RUT_ORI,' ') as archivo_Acta1, "+
                    " NVL(DM2.DE_RUT_ORI,' ') as archivo_Acta2, "+
                    " NVL(TO_CHAR(CARGO.NU_ANE),' ') as archivo_Cargo  "+
                    " FROM TDTV_DESTINOS D \n" +
                    " INNER JOIN TDTV_REMITOS B ON D.NU_ANN = B.NU_ANN AND D.NU_EMI = B.NU_EMI "+
                    " INNER JOIN TD_MENSAJERIA M ON M.NU_MSJ=D.NU_MSJ\n" +
                    " LEFT JOIN RHTM_PER_EMPLEADOS R ON R.CEMP_NU_DNI=M.RE_ENV_MSJ \n" +
                    " LEFT JOIN LG_PRO_PROVEEDOR P ON P.CPRO_RUC=M.RE_ENV_MSJ "+ 
                    " LEFT JOIN TD_DET_MENSAJERIA DM1 ON DM1.NU_ANN=D.NU_ANN AND DM1.NU_EMI=D.NU_EMI AND DM1.NU_DES=D.NU_DES AND DM1.NU_ACTA=TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(D.NU_DES)||'-1' "+
                    " LEFT JOIN TD_DET_MENSAJERIA DM2 ON DM2.NU_ANN=D.NU_ANN AND DM2.NU_EMI=D.NU_EMI AND DM2.NU_DES=D.NU_DES AND DM2.NU_ACTA=TO_CHAR(B.NU_COR_EMI)||'-'||TO_CHAR(D.NU_DES)||'-2' "+
                    " LEFT JOIN TDTV_ANEXOS CARGO ON CARGO.NU_ANN=D.NU_ANN AND CARGO.NU_EMI=D.NU_EMI AND CARGO.NU_DES=D.NU_DES AND CARGO.DE_DET='CARGO_NOTIFICACION'||TO_CHAR(D.NU_DES)||'.pdf' "+
                    " where D.TI_DES <>'01' AND D.nu_ann="+oDestinoResBen.getNuAnn().toString()+
                    " AND D.NU_EMI="+oDestinoResBen.getNuEmi()+
                    " AND D.NU_DES="+oDestinoResBen.getNuDes()+
                    " order by 1");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoRecepMensajeriaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
   
    @Override
    public List<DestiDocumentoEnvMensajeriaBean> getEditLstDetalleMensajeria(String  nroMensajeria) {
        StringBuffer sql = new StringBuffer();
        List<DestiDocumentoEnvMensajeriaBean> list = null;
       
         sql.append("select ROWNUM fila, a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,NVL2(a.co_loc_des,substr(PK_SGD_DESCRIPCION.DE_LOCAL(a.co_loc_des), 1, 100),NULL) de_local, \n" +
"                    B.co_dep_emi co_dependencia,\n" +
"                    NVL2(B.co_dep_emi,substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.co_dep_emi), 1, 100),NULL) de_dependencia, \n" +
"                    PK_SGD_DESCRIPCION.DE_DOCUMENTO(B.CO_TIP_DOC_ADM) documento,\n" +
"                    DECODE(A.TI_DES,\n" +
"                            '01', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.co_dep_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.co_emp_des),\n" +
"                            '02', PK_SGD_DESCRIPCION.DE_PROVEEDOR (A.NU_RUC_DES) ||'-'|| DECODE(A.REMI_TI_EMI, '03', PK_SGD_DESCRIPCION.ANI_SIMIL (A.REMI_NU_DNI_EMI), '04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.REMI_CO_OTR_ORI_EMI) )  ,\n" +
"                            '03', PK_SGD_DESCRIPCION.ANI_SIMIL (A.NU_DNI_DES),\n" +
"                            '04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.CO_OTR_ORI_DES),\n" +
"                            '05', PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.co_dep_DES) ||'-'||PK_SGD_DESCRIPCION.DE_NOM_EMP (A.CO_EMP_DES)\n" +
"                            ) destinatario,\n" +
"                    a.co_mot co_tramite,NVL2(A.co_mot,substr(PK_SGD_DESCRIPCION.MOTIVO(a.co_mot), 1, 100),NULL) de_tramite, \n" +
"                    a.co_pri co_prioridad, \n" +
"                    a.de_pro de_indicaciones, \n" +
"                    a.ti_des co_tipo_destino,\n" +
"                      a.CDIR_REMITE direccion,\n" +
"                            DECODE(A.TI_DES,\n" +
"                            '01', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS),\n" +
"                            '02', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),\n" +
"                            '03', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),\n" +
"                            '04', TRIM(UBIDESTINO.NODEP)||'/'||TRIM(UBIDESTINO.NOPRV)||'/'||TRIM(UBIDESTINO.NODIS),\n" +
"                            '05', TRIM(DEP.NODEP)||'/'||TRIM(DEP.NOPRV)||'/'||TRIM(DEP.NODIS)\n" +
"                            ) departamento,CCOD_TIPO_UBI AS ambito,\n" +
"                 DECODE (B.TI_EMI, \n" +
"                                 '01', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG, \n" +
"                                '05', B.NU_DOC_EMI || '-' || B.NU_ANN || '/' || B.DE_DOC_SIG, \n" +
"                                 B.DE_DOC_SIG ) NU_DOC  ,TO_CHAR(B.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,M.FEC_ENVIOMSJ, a.DE_CARGO,M.NU_SER_MSJ||'-'||M.AN_SER_MSJ guia, M.DE_AMBITO ambito \n" +
"                    FROM tdtv_destinos a INNER JOIN TDTV_REMITOS B ON a.NU_ANN = B.NU_ANN AND a.NU_EMI = B.NU_EMI\n" +
          "          INNER JOIN TD_MENSAJERIA M ON M.NU_MSJ=a.NU_MSJ \n"+     
"                    LEFT JOIN ( SELECT D.CO_DEPENDENCIA,D.DE_DEPENDENCIA,D.DE_CORTA_DEPEN,L.DE_NOMBRE_LOCAL,L.DE_DIRECCION_LOCAL,NODEP,NOPRV,NODIS ,L.CCOD_LOCAL\n" +
"                                FROM RHTM_DEPENDENCIA D \n" +
"                                INNER JOIN SITM_LOCAL_DEPENDENCIA DL ON DL.CO_DEP=D.CO_DEPENDENCIA\n" +
"                                INNER JOIN SI_MAE_LOCAL L ON L.CCOD_LOCAL=DL.CO_LOC\n" +
"                                LEFT JOIN IDTUBIAS U ON U.UBDEP||U.UBPRV||U.UBDIS =L.CO_UBIGEO  ) DEP ON DEP.CO_DEPENDENCIA=A.co_dep_DES AND DEP.CCOD_LOCAL=a.co_loc_des " +
                " LEFT JOIN IDTUBIAS UBIDESTINO ON UBIDESTINO.UBDEP||UBIDESTINO.UBPRV||UBIDESTINO.UBDIS =a.CCOD_DPTO||a.CCOD_PROV||a.CCOD_DIST  \n"+
                    "where a.TI_DES <>'01' AND M.NU_MSJ="+nroMensajeria+"   \n" +                
                    "order by 1");

        
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestiDocumentoEnvMensajeriaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    
    @Override
    public List<DocumentoRecepMensajeriaBean> getLstDetalleMesaVirtual(DestinoResBen oDestinoResBen) {
        StringBuffer sql = new StringBuffer();
        List<DocumentoRecepMensajeriaBean> list = null;
        sql.append("SELECT A.SIDEMIEXT sidEmiExt,'MESA DE PARTES VIRTUAL' detipmsj,NVL(TO_CHAR(A.DFECENV,'DD/MM/YYYY'),' ') fecEnviomsj,\n" +
                   "PK_SGD_DESCRIPCION.ESTADOS (A.CFLGEST,'IOTDTC_DESPACHO') docEstadoMsj,\n" +
                    "NVL(TO_CHAR(A.DFECREGSTDREC,'DD/MM/YYYY'),' ')  fecRecepmp,\n" +
                    "'cargo' archivo_Cargo  \n" +
                    "FROM IOTDTC_DESPACHO A "+
                    " where A.VANIOREGSTD="+oDestinoResBen.getNuAnn().toString()+
                    " AND A.VNUMREGSTD="+oDestinoResBen.getNuEmi()+                    
                    " order by 1");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoRecepMensajeriaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
   
}
