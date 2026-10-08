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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstRecBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstRecibidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.dao.SeguiEstRecibidoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author NGilt
 */
@Repository("consultaEstRecibidoDao")
public class SeguiEstRecibidoDaoImp extends SimpleJdbcDaoBase implements SeguiEstRecibidoDao {

    @Override
    public List<DocumentoSeguiEstRecibidoBean> getListDocSeguiEstRec(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        Map<String, Object> objectParam = new HashMap<String, Object>();
        String sqlContains="";
        List<DocumentoSeguiEstRecibidoBean> list = new ArrayList<DocumentoSeguiEstRecibidoBean>();

        sql.append("SELECT TOP 300 X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (X.NU_ANN, X.NU_EMI) DE_ORI_EMI,\n");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n");
        sql.append(" (CASE WHEN X.TI_DES='01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.CO_EMP_DES) \n");
        sql.append(" WHEN X.TI_DES='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.NU_RUC_DES) \n");
        sql.append(" WHEN X.TI_DES='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (X.NU_DNI_DES) \n");
        sql.append(" WHEN X.TI_DES='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (X.CO_OTR_ORI_DES)\n");
        sql.append(" END)AS DE_EMP_DES, \n");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (X.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,\n");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,\n");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(X.CO_MOT) DE_MOTIVO,\n");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_REC) DE_EMP_REC,\n");        
        //sql.append(" trunc(X.FE_EMI) + IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas(TRUNC(X.FE_EMI),X.NU_DIA_ATE) F_LIMITE,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_FORMAT(DATEADD(DAY, (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_MAS(CONVERT(DATETIME, X.FE_EMI), X.NU_DIA_ATE)), CONVERT(DATE, X.FE_EMI)),'DD/MM/YYYY') F_LIMITE,");
        //sql.append(" TO_CHAR((trunc(X.FE_EMI) + IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas(TRUNC(X.FE_EMI),X.NU_DIA_ATE)),'DD/MM/YY') F_LIMITE_CORTA ");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_FORMAT(DATEADD(DAY, (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_MAS(CONVERT(DATETIME, X.FE_EMI), X.NU_DIA_ATE)), CONVERT(DATE, X.FE_EMI)),'DD/MM/YYYY') F_LIMITE_CORTA \n");
        sql.append(" FROM ( \n");
        sql.append(" SELECT A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_FORMAT(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" A.CO_TIP_DOC_ADM,C.NU_DOC,B.TI_DES,B.CO_EMP_DES,B.NU_RUC_DES,B.NU_DNI_DES,B.CO_OTR_ORI_DES,\n");
        sql.append(" UPPER (A.DE_ASU) DE_ASU_M,C.NU_EXPEDIENTE,B.CO_MOT,");
        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC,\n");
        sql.append(" (CASE WHEN COALESCE(C.TI_EMI_REF,'0')+COALESCE(C.IN_EXISTE_ANEXO,'2')='00' THEN 0 "
                    + "WHEN COALESCE(C.TI_EMI_REF,'0')+COALESCE(C.IN_EXISTE_ANEXO,'2')='02' THEN 0 "
                    + "ELSE 1 END) EXISTE_ANEXO,");
        sql.append(" COALESCE(B.CO_PRI,'1') CO_PRI,B.ES_DOC_REC,\n");
        sql.append(" B.FE_REC_DOC,IDOSGD.PK_SGD_DESCRIPCION_FORMAT(B.FE_REC_DOC,'DD/MM/YYYY') FE_REC_CORTA,A.CO_EMP_RES,\n");
        sql.append(" B.CO_ETIQUETA_REC,B.CO_EMP_REC,A.NU_DIA_ATE,\n");
        sql.append(" CASE");
        sql.append(" WHEN a.nu_dia_ate > 0\n");
        //sql.append(" AND (TRUNC (E.fe_finaliza) - TRUNC (GETDATE())) > 0");
        
        sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) <= 2 \n");
        sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) > 0 \n");
        
        sql.append(" AND b.fe_ate_doc IS NULL\n");
        sql.append(" AND b.fe_arc_doc IS NULL\n");
        sql.append(" AND d.in_dia = 0");
        sql.append("    THEN '1'");
        sql.append(" WHEN a.nu_dia_ate > 0");
        //sql.append(" AND TRUNC (GETDATE()) > TRUNC (E.fe_finaliza)");
        sql.append(" AND CONVERT(DATE, GETDATE()) > CONVERT(DATE, E.FE_FINALIZA)");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL");
        sql.append(" AND d.in_dia = 0");
        sql.append(" THEN '2'");
        sql.append(" WHEN a.nu_dia_ate > 0\n");
        //sql.append(" AND TRUNC (GETDATE()) = TRUNC (E.fe_finaliza)");
        sql.append(" AND CONVERT(DATE, GETDATE()) = CONVERT(DATE, E.FE_FINALIZA)\n");
        sql.append(" AND b.fe_ate_doc IS NULL\n");
        sql.append(" AND b.fe_arc_doc IS NULL\n");
        sql.append(" AND d.in_dia = 0");
        sql.append(" THEN '3'");
        sql.append(" WHEN a.nu_dia_ate > 0\n");
        //sql.append(" AND (TRUNC (E.fe_finaliza) - TRUNC (GETDATE())) > 2");
        sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.FE_FINALIZA), CONVERT(DATE, GETDATE()))) > 2\n");
        sql.append(" AND b.fe_ate_doc IS NULL\n");
        sql.append(" AND b.fe_arc_doc IS NULL\n");
        sql.append(" AND d.in_dia = 0\n");
        sql.append(" THEN '4'");
        sql.append(" WHEN b.fe_ate_doc IS NOT NULL OR b.fe_arc_doc IS NOT NULL");
        sql.append(" THEN '5'");
        sql.append(" WHEN (b.fe_ate_doc IS   NULL OR b.fe_arc_doc IS   NULL) AND (a.nu_dia_ate = 0 OR D.in_dia = 0)");
        sql.append(" THEN '0'");
        sql.append(" END co_est_ven,");
        sql.append(" CASE");
        sql.append(" WHEN a.nu_dia_ate > 0\n");
        //sql.append(" AND TRUNC (GETDATE()) > TRUNC (E.fe_finaliza)");
        sql.append(" AND CONVERT(DATE, GETDATE()) > CONVERT(DATE, E.FE_FINALIZA)\n");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL\n");
        sql.append(" AND d.in_dia = 0");
        //sql.append(" THEN IDOSGD.PK_SGD_DESCRIPCION_fu_dia_tra (fe_finaliza, TRUNC (GETDATE()))");
        sql.append(" THEN (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_DIA_TRA](E.FE_FINALIZA, CONVERT(DATETIME, GETDATE())))\n");
        sql.append(" ELSE 0");
        sql.append(" END nu_dia_exc,b.fe_arc_doc,b.fe_ate_doc,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_arc_doc,'dd/mm/yyyy') fe_arc_doc_CORTA,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_ate_doc,'dd/mm/yyyy') fe_ate_doc_CORTA\n");

        sql.append(" FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK) , IDOSGD.TDTV_DESTINOS B WITH (NOLOCK) ,IDOSGD.TDTX_REMITOS_RESUMEN C WITH (NOLOCK) ,IDOSGD.TDTR_MOTIVO D WITH (NOLOCK) ,");
        sql.append(" (SELECT y.nu_ann, y.nu_emi, y.fe_finaliza,");
        sql.append(" (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_dia_tra (CONVERT(DATETIME, GETDATE()),y.fe_finaliza)) nu_dia_fal\n");
        sql.append(" FROM (SELECT x.nu_ann, x.nu_emi,\n");
        //sql.append(" TRUNC (x.fe_emi)+ IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas (TRUNC (x.fe_emi),nu_dia_ate) AS fe_finaliza");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_FORMAT(DATEADD(DAY, (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_MAS(CONVERT(DATETIME, X.FE_EMI), X.NU_DIA_ATE)), CONVERT(DATE, X.FE_EMI)),'DD/MM/YYYY') fe_finaliza \n");        
        sql.append(" FROM IDOSGD.tdtv_remitos AS x WITH (NOLOCK) ) AS y) AS E\n");
        sql.append(" WHERE");
        sql.append(" B.NU_ANN = A.NU_ANN");
        sql.append(" AND B.NU_EMI = A.NU_EMI");
        sql.append(" AND C.NU_ANN = B.NU_ANN");
        sql.append(" AND C.NU_EMI = B.NU_EMI");
        sql.append(" AND b.co_mot = d.co_mot");
        sql.append(" AND a.nu_ann = E.nu_ann");
        sql.append(" AND a.nu_emi = E.nu_emi\n");

//        String pNUAnn = buscarDocumentoSeguiEstRecBean.getCoAnnio();
//        if (!(buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN = :pNuAnn");
//            objectParam.put("pNuAnn", pNUAnn);
//        }
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND B.ES_ELI = '0'");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7','8')\n");
        //sql.append(" AND B.ES_DOC_REC <> '0'");
        sql.append(" AND A.IN_OFICIO = '0'");
        sql.append(" AND A.CO_GRU IN ('1','2','3')\n");

        // Parametros Basicos
        String pTipoBusqueda = buscarDocumentoSeguiEstRecBean.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoSeguiEstRecBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        String auxTipoAcceso = buscarDocumentoSeguiEstRecBean.getTiAcceso();
        String tiAcceso = auxTipoAcceso != null && (auxTipoAcceso.equals("0") || auxTipoAcceso.equals("1") || auxTipoAcceso.equals("2")) ? auxTipoAcceso : "1";
        if (buscarDocumentoSeguiEstRecBean.getCoEmpDestino() != null && buscarDocumentoSeguiEstRecBean.getCoEmpDestino().trim().length() > 0 && (pTipoBusqueda.equals("0") || bBusqFiltro) && tiAcceso.equals("0")) {
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
            objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpDestino());
        } else {
            if (tiAcceso.equals("1")) {
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
                objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpleado());
            } else if (tiAcceso.equals("2")) {
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes  \n");
                objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpleado());
            }
        }

        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm() != null && buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm());
            }
            if (buscarDocumentoSeguiEstRecBean.getEstadoDoc() != null && buscarDocumentoSeguiEstRecBean.getEstadoDoc().trim().length() > 0) {
                sql.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                objectParam.put("pEsDocRec", buscarDocumentoSeguiEstRecBean.getEstadoDoc());
            }
            if (buscarDocumentoSeguiEstRecBean.getPrioridadDoc() != null && buscarDocumentoSeguiEstRecBean.getPrioridadDoc().trim().length() > 0) {
                sql.append(" AND B.CO_PRI = :pCoPrioridad \n");
                objectParam.put("pCoPrioridad", buscarDocumentoSeguiEstRecBean.getPrioridadDoc());
            }
            if (buscarDocumentoSeguiEstRecBean.getCoDepRemite() != null && buscarDocumentoSeguiEstRecBean.getCoDepRemite().trim().length() > 0) {
                sql.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = :pTiEmiRef \n");
                objectParam.put("pTiEmiRef", buscarDocumentoSeguiEstRecBean.getCoDepRemite());
            }
            if (buscarDocumentoSeguiEstRecBean.getCoDepDestino() != null && buscarDocumentoSeguiEstRecBean.getCoDepDestino().trim().length() > 0) {
                sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                objectParam.put("pcoDepDes", buscarDocumentoSeguiEstRecBean.getCoDepDestino());
            }
            if (buscarDocumentoSeguiEstRecBean.getIdEtiqueta() != null && buscarDocumentoSeguiEstRecBean.getIdEtiqueta().trim().length() > 0) {
                sql.append(" AND B.CO_ETIQUETA_REC = :pcoEtiquetaRec ");
                objectParam.put("pcoEtiquetaRec", buscarDocumentoSeguiEstRecBean.getIdEtiqueta());
            }
            if (buscarDocumentoSeguiEstRecBean.getCoVencimiento() != null && buscarDocumentoSeguiEstRecBean.getCoVencimiento().trim().length() > 0) {
                //Normal
                if ("1".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    //sql.append(" AND (TRUNC (E.fe_finaliza) - TRUNC (GETDATE())) <= 2");
                    sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.FE_FINALIZA), CONVERT(DATE, GETDATE()))) <=2");
                    //sql.append(" AND (TRUNC (E.fe_finaliza) - TRUNC (GETDATE())) > 0");
                    sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.FE_FINALIZA), CONVERT(DATE, GETDATE()))) >0");                    
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = 0\n");
                }
                else if ("2".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" AND (GETDATE()) > cast(E.fe_finaliza as datetime)");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = 0");
                    sql.append(" AND A.NU_DIA_ATE > 0\n");
                }
                else if ("3".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    //vence hoy
                    //sql.append(" AND TRUNC (GETDATE()) = TRUNC (E.fe_finaliza)");
                    sql.append(" AND CONVERT (DATETIME,GETDATE(),103) = CONVERT(DATETIME,E.fe_finaliza,103)  ");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia > 0");
                    sql.append(" AND A.NU_DIA_ATE > 0\n");
                }
                else if ("4".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    //sql.append(" AND (TRUNC (E.fe_finaliza) - TRUNC (GETDATE())) > 2");
                    sql.append(" AND DATEDIFF(DAY,CONVERT(DATE,(E.fe_finaliza)),CONVERT(DATE,GETDATE())) > 2");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia > 0\n");
                } 
                else if ("5".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" AND ( b.fe_ate_doc is not null");
                    sql.append(" OR b.fe_arc_doc is not null )");
                }
                else if ("0".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" and a.nu_dia_ate = 0"); // CAMBIAR LA 
                    sql.append(" and D.in_dia = 0  ");  // CONDICIONAL 'OR' POR AND SIENDO LA VERSION ANTERIOR ESTE "and (a.nu_dia_ate = 0 OR D.in_dia = 0)
                    sql.append(" AND (b.fe_ate_doc IS NULL");
                    sql.append(" OR b.fe_arc_doc IS NULL) ");
                }
                
                // '1'-- x vencerse
                // '2'--vencidos
                // '3'--vence hoy
                // '4'--Vence a futuro
                // '5'--Atendido
                // '0'--Normal - Sin vencimiento                
            }
            if (buscarDocumentoSeguiEstRecBean.getEsFiltroFecha() != null
                    && (buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("1") || buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("2") || buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoSeguiEstRecBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoSeguiEstRecBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) { 
                    sql.append(" AND A.FE_EMI BETWEEN CONVERT(DATETIME, :pFeEmiIni, 103) AND CONVERT(DATETIME, :pFeEmiFin, 103) + 0.99999 ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
        }

        if (pTipoBusqueda.equals("1")) {
            if (!bBusqFiltro) {
                if (buscarDocumentoSeguiEstRecBean.getCoDepDestino() != null && buscarDocumentoSeguiEstRecBean.getCoDepDestino().trim().length() > 0) {
                    sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                    objectParam.put("pcoDepDes", buscarDocumentoSeguiEstRecBean.getCoDepDestino());
                }
            }
            if (buscarDocumentoSeguiEstRecBean.getBusNumDoc() != null && buscarDocumentoSeguiEstRecBean.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND ((A.NU_DOC_EMI LIKE '%'+:pnuDocEmi+'%') OR (A.NU_DOC_EMI + '-' + A.NU_ANN + '-' + A.DE_DOC_SIG LIKE '%'+:pnuDocEmiAlt+'%' )) ");
                objectParam.put("pnuDocEmi", buscarDocumentoSeguiEstRecBean.getBusNumDoc());
                objectParam.put("pnuDocEmiAlt", buscarDocumentoSeguiEstRecBean.getBusNumDoc());
            }

            if (buscarDocumentoSeguiEstRecBean.getBusNumExpediente() != null && buscarDocumentoSeguiEstRecBean.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND C.NU_EXPEDIENTE LIKE '%'+:pnuExpediente+'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoSeguiEstRecBean.getBusNumExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoSeguiEstRecBean.getBusAsunto() != null && buscarDocumentoSeguiEstRecBean.getBusAsunto().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '" + BusquedaTextual.getContextValue(buscarDocumentoSeguiEstRecBean.getBusAsunto()) + "', 1 ) > 1 ");
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoSeguiEstRecBean.getBusAsunto())+"', 1 ) > 1 ");                                                
//                sql.append(" AND CONTAINS(A.DE_ASU, :pBusquedaTextual) ");
//                sqlContains="SELECT RETORNO FROM [IDOSGD].PK_SGD_DESCRIPCION_QUERYFILTER('"+buscarDocumentoSeguiEstRecBean.getBusAsunto()+"')";
               sql.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'+:pDeAsunto+'%') ");
               objectParam.put("pDeAsunto", buscarDocumentoSeguiEstRecBean.getBusAsunto());                                
            }
            if (buscarDocumentoSeguiEstRecBean.getBusNumDocRef() != null && buscarDocumentoSeguiEstRecBean.getBusNumDocRef().trim().length() > 1) {
                    //busqeda referencia
                     sql.append("  AND A.NU_EMI IN( ");
                    sql.append("   SELECT D.NU_EMI ");
                    sql.append("   FROM IDOSGD.TDTR_ARBOL_SEG D ");
                    sql.append("   INNER JOIN IDOSGD.TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                    sql.append("   WHERE R.NU_ANN='"+buscarDocumentoSeguiEstRecBean.getBusCoAnnio()+"'   ");
                    if (buscarDocumentoSeguiEstRecBean.getBusCodTipoDocRef() != null && buscarDocumentoSeguiEstRecBean.getBusCodTipoDocRef().trim().length() > 1) {
                    sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoSeguiEstRecBean.getBusCodTipoDocRef()+"' ");
                    }
                    if (buscarDocumentoSeguiEstRecBean.getBusCodDepEmiRef() != null && buscarDocumentoSeguiEstRecBean.getBusCodDepEmiRef().trim().length() > 1) {
                    sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoSeguiEstRecBean.getBusCodDepEmiRef()+"' ");
                    }
                    sql.append("   AND ((R.NU_DOC_EMI LIKE '%'+'"+buscarDocumentoSeguiEstRecBean.getBusNumDocRef()+"'+'%') OR (R.DE_DOC_SIG LIKE '%'+'"+buscarDocumentoSeguiEstRecBean.getBusNumDocRef()+"'+'%'))");
                    sql.append(" )");
                }

        }
        sql.append(") X ORDER BY FE_EMI DESC");
        //sql.append("WHERE ROWNUM < 301");

        try {
//            if(sqlContains.length()>0){
//                String cadena = this.jdbcTemplate.queryForObject(sqlContains, String.class);            
//                objectParam.put("pBusquedaTextual", cadena);
//            }
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoSeguiEstRecibidoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public DocumentoRecepSeguiBean getDocumentoRecepAdmSegui(String snuAnn, String snuEmi, String snuDes) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT B.DE_ANE,A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,"
                + "C.NU_EXPEDIENTE,"
                + "IDOSGD.PK_SGD_DESCRIPCION_FORMAT((SELECT FE_EXP FROM IDOSGD.TDTC_EXPEDIENTE WHERE NU_ANN_EXP=A.NU_ANN_EXP AND NU_SEC_EXP=A.NU_SEC_EXP),'DD/MM/YYYY') FE_EXP_CORTA,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(C.CO_PROCESO_EXP) DE_PROCESO_EXP,"
                + "IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_REC) DE_EMP_REC,\n"
                + "(CASE "
                + " WHEN B.TI_DES='01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES)\n"
                + " WHEN B.TI_DES='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES)\n"
                + " WHEN B.TI_DES='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES)\n"
                + " WHEN B.TI_DES='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES) END) DE_EMP_DES,\n"
                + "(CASE"
                + " WHEN A.TI_EMI='01' THEN A.NU_DOC_EMI + '-' + A.NU_ANN + '/' + A.DE_DOC_SIG"
                + " WHEN A.TI_EMI='05' THEN A.NU_DOC_EMI + '-' + A.NU_ANN + '/' + A.DE_DOC_SIG"
                + " ELSE A.DE_DOC_SIG END) NU_DOC,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA (B.CO_DEP_DES) DE_DEP_DES,IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,IDOSGD.PK_SGD_DESCRIPCION_FORMAT(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n"
                + "A.DE_ASU,A.NU_DIA_ATE,"
                + "IDOSGD.PK_SGD_DESCRIPCION_MOTIVO (B.CO_MOT) DE_MOTIVO,B.DE_PRO DE_INDICACIONES,"
                + "(SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD (B.CO_PRI)) DE_PRIORIDAD,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_FORMAT(B.FE_REC_DOC,'DD/MM/YYYY HH24:MI:SS') FE_REC_CORTA,"
                + "IDOSGD.PK_SGD_DESCRIPCION_FORMAT(B.FE_ATE_DOC,'DD/MM/YYYY') FE_ATENCION_CORTA,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_FORMAT(B.FE_ARC_DOC,'DD/MM/YYYY') FE_ARCHIVAMIENTO_CORTA,"
                + "IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_REC,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_RES) DE_EMP_RES\n"
                + "FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK) ,IDOSGD.TDTV_DESTINOS B WITH (NOLOCK) ,IDOSGD.TDTX_REMITOS_RESUMEN C WITH (NOLOCK) \n"
                + "WHERE A.ES_ELI='0' AND B.ES_ELI='0'\n"
                + "AND A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI\n"
                + "AND C.NU_ANN = A.NU_ANN AND C.NU_EMI = A.NU_EMI\n"
                + "AND A.ES_DOC_EMI NOT IN ('5', '9', '7')\n"
                + "AND A.IN_OFICIO = '0'\n"
                + "AND B.NU_ANN=? AND B.NU_EMI=? AND B.NU_DES=?");

        DocumentoRecepSeguiBean documentoRecepSeguiBean = new DocumentoRecepSeguiBean();
        try {
            documentoRecepSeguiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoRecepSeguiBean.class),
                    new Object[]{snuAnn, snuEmi, snuDes});
        } catch (EmptyResultDataAccessException e) {
            documentoRecepSeguiBean = null;
        } catch (Exception e) {
            System.out.println("nu_ann=" + snuAnn + "," + "nu_emi=" + snuEmi + "nu_des=" + snuDes);
            e.printStackTrace();
        }
        return documentoRecepSeguiBean;
    }

    @Override
    public List<ReferenciaConsulBean> getDocumentosRefRecepAdmSegui(String snuAnn, String snuEmi) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tipo_doc,\n"
                + "(CASE \n"
                + " WHEN a.ti_emi='01' THEN a.nu_doc_emi + '-' + a.nu_ann + '/' + a.de_doc_sig \n"
                + " WHEN a.ti_emi='05' THEN a.nu_doc_emi + '-' + a.nu_ann + '/' + a.de_doc_sig \n"
                + " ELSE a.de_doc_sig END) li_nu_doc,\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(a.fe_emi,'DD/MM/YYYY') fe_emi_corta,\n"
                + " b.co_ref,b.nu_ann,b.nu_emi,\n"
                + " b.nu_des,b.nu_ann_ref,\n"
                + " b.nu_emi_ref,b.nu_des_ref\n"
                + " FROM IDOSGD.tdtv_remitos a WITH (NOLOCK) ,IDOSGD.TDTR_REFERENCIA b WITH (NOLOCK) \n"
                + " WHERE a.nu_ann = b.nu_ann_ref\n"
                + " AND a.nu_emi = b.nu_emi_ref\n"
                + " AND b.NU_EMI=? \n"
                + " AND b.NU_ANN=?");

        List<ReferenciaConsulBean> list = new ArrayList<ReferenciaConsulBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaConsulBean.class),
                    new Object[]{snuEmi, snuAnn});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<DependenciaBean> getListDestinatarioEmi(String pcoDepen, String pdeDepEmite) {
        StringBuffer sql = new StringBuffer();

        boolean vfiltro = pdeDepEmite != null && !pdeDepEmite.trim().equals("") ? true : false;

        sql.append("SELECT DE_DEPENDENCIA,\n"
                + "       CO_DEPENDENCIA,\n"
                + "       DE_CORTA_DEPEN\n"
                + "  FROM IDOSGD.RHTM_DEPENDENCIA\n"
                + " WHERE co_nivel <> '6'\n"
                + "   AND IN_BAJA = '0'");
        if (vfiltro) {
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%' )");
        }
        sql.append(" union  ");
        sql.append("SELECT DE_DEPENDENCIA,\n"
                + "       CO_DEPENDENCIA,\n"
                + "       DE_CORTA_DEPEN\n"
                + "  FROM IDOSGD.RHTM_DEPENDENCIA\n"
                + " WHERE co_nivel = '6'\n"
                + "   AND IN_BAJA = '0'\n"
                + "   AND (CO_DEPEN_PADRE in ( select CO_DEPEN_PADRE co_dep from IDOSGD.RHTM_DEPENDENCIA where CO_DEPENDENCIA = ?)\n"
                + "       OR ? IN (CO_DEPENDENCIA, CO_DEPEN_PADRE))");
        if (vfiltro) {
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%')");
        }
        sql.append("  UNION");
        sql.append(" SELECT ' [TODOS]',");
        sql.append(" 	   NULL,");
        sql.append(" 	   NULL");
        if (vfiltro) {
            sql.append(" where ' [TODOS]' like '%'+?+'%'");
        }
        sql.append("  ORDER BY 1");

        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            if (vfiltro) {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{pdeDepEmite, pdeDepEmite, pcoDepen, pcoDepen,
                    pdeDepEmite, pdeDepEmite, pdeDepEmite});
            } else {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{pcoDepen, pcoDepen});
            }
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public String getRutaReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean) {
        String vResult;
        StringBuffer sql = new StringBuffer();
        try {

            sql.append(" B.NU_ANN = A.NU_ANN");
            sql.append(" AND B.NU_EMI = A.NU_EMI");
            sql.append(" AND C.NU_ANN = B.NU_ANN");
            sql.append(" AND C.NU_EMI = B.NU_EMI");
            sql.append(" AND b.co_mot = d.co_mot");
            sql.append(" AND a.nu_ann = E.nu_ann");
            sql.append(" AND a.nu_emi = E.nu_emi");

            String pNUAnn = buscarDocumentoSeguiEstRecBean.getCoAnnio();
            if (!(buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
                // Parametros Basicos
                sql.append(" AND A.NU_ANN = '").append(pNUAnn).append("'");
            }
            sql.append(" AND A.ES_ELI = '0'");
            sql.append(" AND B.ES_ELI = '0'");
            sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7')");
            sql.append(" AND A.IN_OFICIO = '0'");

        if (buscarDocumentoSeguiEstRecBean.getCoEmpDestino()!=null && buscarDocumentoSeguiEstRecBean.getCoEmpDestino().trim().length()>0){
            sql.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoEmpDestino()).append("'");
        }else {    
            if(buscarDocumentoSeguiEstRecBean.getTiAcceso().equals("1")){
                sql.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoEmpleado()).append("'");
            }else if(buscarDocumentoSeguiEstRecBean.getTiAcceso().equals("2")){
                sql.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoEmpleado()).append("'");
            }
        }            
            
        //Filtro
        if (buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm() != null && buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm().trim().length() > 0) {
            sql.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm()).append("'");
            //objectParam.put("pCoDocEmi", buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm());
        }
        if (buscarDocumentoSeguiEstRecBean.getEstadoDoc() != null && buscarDocumentoSeguiEstRecBean.getEstadoDoc().trim().length() > 0) {
            sql.append(" AND B.ES_DOC_REC = '").append(buscarDocumentoSeguiEstRecBean.getEstadoDoc()).append("'");
        }
        if (buscarDocumentoSeguiEstRecBean.getPrioridadDoc() != null && buscarDocumentoSeguiEstRecBean.getPrioridadDoc().trim().length() > 0) {
            sql.append(" AND B.CO_PRI = '").append(buscarDocumentoSeguiEstRecBean.getPrioridadDoc()).append("'");
        }
        if (buscarDocumentoSeguiEstRecBean.getCoDepRemite() != null && buscarDocumentoSeguiEstRecBean.getCoDepRemite().trim().length() > 0) {
            sql.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = '").append(buscarDocumentoSeguiEstRecBean.getCoDepRemite()).append("'");
        }
        if (buscarDocumentoSeguiEstRecBean.getCoDepDestino() != null && buscarDocumentoSeguiEstRecBean.getCoDepDestino().trim().length() > 0) {
            sql.append(" AND B.CO_DEP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoDepDestino()).append("'");
        }
        if (buscarDocumentoSeguiEstRecBean.getIdEtiqueta() != null && buscarDocumentoSeguiEstRecBean.getIdEtiqueta().trim().length() > 0) {
            sql.append(" AND B.CO_ETIQUETA_REC = '").append(buscarDocumentoSeguiEstRecBean.getIdEtiqueta()).append("'");
        }
        if (buscarDocumentoSeguiEstRecBean.getCoVencimiento() != null && buscarDocumentoSeguiEstRecBean.getCoVencimiento().trim().length() > 0) {
            if ("1".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) <= 2");
                sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) > 0");
                sql.append(" AND b.fe_ate_doc IS NULL");
                sql.append(" AND b.fe_arc_doc IS NULL");
                sql.append(" AND d.in_dia = 0");
            }
            else if ("2".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                sql.append(" AND CONVERT(DATE, GETDATE()) > CONVERT(DATE, E.fe_finaliza)");
                sql.append(" AND b.fe_ate_doc IS NULL");
                sql.append(" AND b.fe_arc_doc IS NULL");
                sql.append(" AND d.in_dia = 0");
                sql.append(" AND A.NU_DIA_ATE > 0");
            }
            else if ("3".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                //vence hoy
                sql.append(" AND CONVERT(DATE, GETDATE()) = CONVERT(DATE, E.fe_finaliza)");
                sql.append(" AND b.fe_ate_doc IS NULL");
                sql.append(" AND b.fe_arc_doc IS NULL");
                sql.append(" AND d.in_dia = 0");
                sql.append(" AND A.NU_DIA_ATE > 0");
            }
            else if ("4".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) > 2");
                sql.append(" AND b.fe_ate_doc IS NULL");
                sql.append(" AND b.fe_arc_doc IS NULL");
                sql.append(" AND d.in_dia = 0");
            }
            else if ("5".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                sql.append(" AND b.fe_ate_doc is not null");
                sql.append(" AND b.fe_rec_doc is not null");
            }
            else if ("0".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                sql.append(" and a.nu_dia_ate = 0");
                sql.append(" and D.in_dia = 0");
                sql.append(" AND b.fe_ate_doc IS NULL");
                sql.append(" AND b.fe_arc_doc IS NULL");
            }

            // '1'-- x vencerse
            // '2'--vencidos
            // '3'--vence hoy
            // '4'--Vence a futuro
            // '5'--Atendido
            // '0'--Normal - Sin vencimiento                
        }
        if (buscarDocumentoSeguiEstRecBean.getEsFiltroFecha() != null
                && (buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("1") || buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("3"))) {
            String vFeEmiIni = buscarDocumentoSeguiEstRecBean.getFeEmiIni();
            String vFeEmiFin = buscarDocumentoSeguiEstRecBean.getFeEmiFin();
            if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                    && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) { 
                sql.append(" AND A.FE_EMI BETWEEN CONVERT(DATETIME, '").append(vFeEmiIni).append("', 103) AND CONVERT(DATETIME, '").append(vFeEmiFin).append("', 103) + 0.99999 ");
            }
        }
        sql.append(" ");
        vResult = "0" + sql.toString();
        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }
        return vResult;
    }
    
    @Override
    public DocumentoSeguiEstRecibidoBean existeDocumentoReferenciado(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean) {
        StringBuffer sql = new StringBuffer();
        DocumentoSeguiEstRecibidoBean documentoSeguiEstRecibidoBean = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_DOC FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK) \n"
                + "WHERE A.NU_ANN=?\n"
                + "AND A.CO_DEP_EMI=?\n"
                + "AND A.TI_EMI='01'\n"
                + "AND A.CO_TIP_DOC_ADM=?\n"
                + "AND A.NU_DOC_EMI=?\n"
                + "AND A.ES_ELI='0'\n"
                + "AND A.ES_DOC_EMI NOT IN ('5','7','9')");

        try {
            documentoSeguiEstRecibidoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoSeguiEstRecibidoBean.class),
                    new Object[]{buscarDocumentoSeguiEstRecBean.getBusCoAnnio(), buscarDocumentoSeguiEstRecBean.getBusCodDepEmiRef(), buscarDocumentoSeguiEstRecBean.getBusCodTipoDocRef(),
                        buscarDocumentoSeguiEstRecBean.getBusNumDocRef()});
            documentoSeguiEstRecibidoBean.setMsjResult("OK");
        } catch (EmptyResultDataAccessException e) {
            documentoSeguiEstRecibidoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoSeguiEstRecibidoBean;
    }
    
    @Override
    public List<DocumentoSeguiEstRecibidoBean> getDocumentosReferenciadoBusq(DocumentoSeguiEstRecibidoBean documentoRecepConsulBean, String tiAcceso) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List<DocumentoSeguiEstRecibidoBean> list = new ArrayList<DocumentoSeguiEstRecibidoBean>();

//        sql.append("SELECT A.*, ROWNUM");
        sql.append("SELECT A.*");
        sql.append(" FROM ( ");
        sql.append(" SELECT TOP 300 DISTINCT A.NU_COR_EMI,IDOSGD.TD_PK_TRAMITE_DE_EMI_REF(A.NU_ANN, A.NU_EMI) DE_EMI_REF,A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" IDOSGD.PK_TD_CONSULTA_EMISION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI,");
        sql.append(" IDOSGD.TD_PK_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,C.NU_DOC,");
//        sql.append(" DECODE (B.TI_DES,");
//        sql.append(" 				 '01', IDOSGD.TD_PK_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES),");
//        sql.append(" 				 '02', IDOSGD.TD_PK_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES),");
//        sql.append(" 				 '03', IDOSGD.TD_PK_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES),");
//        sql.append(" 				 '04', IDOSGD.TD_PK_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES)");
//        sql.append(" 				) DE_EMP_DES,");
        
        sql.append(" CASE ");
        sql.append(" 			WHEN  B.TI_DES='01' THEN IDOSGD.TD_PK_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES) ");
        sql.append(" 				 WHEN  B.TI_DES='02' THEN IDOSGD.TD_PK_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES) ");
        sql.append(" 				 WHEN  B.TI_DES='03' THEN IDOSGD.TD_PK_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES) ");
        sql.append(" 				 WHEN  B.TI_DES='04' THEN IDOSGD.TD_PK_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES)");
        sql.append(" 				END AS DE_EMP_DES,");
        
        
        sql.append(" UPPER (A.DE_ASU) DE_ASU_M,C.NU_EXPEDIENTE,");
        sql.append(" IDOSGD.TD_PK_DESCRIPCION_ESTADOS (B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,");
//        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC, "
//                + "DECODE(NVL(C.TI_EMI_REF,'0')||NVL(C.IN_EXISTE_ANEXO,'2'),'00',0,'02',0,1) EXISTE_ANEXO,NVL(B.CO_PRI,'1') CO_PRI,B.ES_DOC_REC,");
        
        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC, "
                + " CASE  WHEN (COALESCE(C.TI_EMI_REF,'0')+COALESCE(C.IN_EXISTE_ANEXO,'2'))='00' THEN 0 "
                + " WHEN (COALESCE(C.TI_EMI_REF,'0')+COALESCE(C.IN_EXISTE_ANEXO,'2'))='02' THEN 0 ELSE 1 END AS EXISTE_ANEXO,COALESCE(B.CO_PRI,'1') CO_PRI,B.ES_DOC_REC,");
        
        
        sql.append(" B.FE_REC_DOC,TO_CHAR(B.FE_REC_DOC,'DD/MM/YY') FE_REC_CORTA,(SELECT CEMP_APEPAT + ' ' + CEMP_APEMAT + ' ' + CEMP_DENOM FROM IDOSGD.RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = A.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" IDOSGD.TD_PK_DESCRIPCION_MOTIVO(B.CO_MOT) DE_MOTIVO,(SELECT CEMP_APEPAT + ' ' + CEMP_APEMAT + ' ' + CEMP_DENOM FROM IDOSGD.RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = B.CO_EMP_REC) DE_EMP_REC,");
        sql.append(" B.CO_ETIQUETA_REC,");

        sql.append(" A.NU_DIA_ATE,CAST(A.FE_EMI as date) + IDOSGD.TD_PK_TRAMITE_fu_dia_mas(CAST(A.FE_EMI as date),A.NU_DIA_ATE) F_LIMITE,");
        sql.append(" TO_CHAR((CAST(A.FE_EMI as date) + IDOSGD.TD_PK_TRAMITE_fu_dia_mas(CAST(A.FE_EMI as date),A.NU_DIA_ATE)),'DD/MM/YY') F_LIMITE_CORTA,");
        sql.append(" CASE");
        sql.append(" WHEN a.nu_dia_ate > 0");
        sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) <= 2");
        sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) > 0");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL");
        sql.append(" AND d.in_dia = '0'");
        sql.append("    THEN '1'");
        sql.append(" WHEN a.nu_dia_ate > 0");
        sql.append(" AND CAST(now() as date) > CAST(E.fe_finaliza as date )");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL");
        sql.append(" AND d.in_dia = '0'");
        sql.append(" THEN '2'");
        sql.append(" WHEN a.nu_dia_ate > 0");
        sql.append(" AND CAST(now() as date) = CAST(E.fe_finaliza as date )");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL");
        sql.append(" AND d.in_dia = '0'");
        sql.append(" THEN '3'");
        sql.append(" WHEN a.nu_dia_ate > 0");
        sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) > 2");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL");
        sql.append(" AND d.in_dia = '0'");
        sql.append(" THEN '4'");
        sql.append(" WHEN b.fe_ate_doc IS NOT NULL OR b.fe_arc_doc IS NOT NULL");
        sql.append(" THEN '5'");
        sql.append(" WHEN a.nu_dia_ate = 0 OR D.in_dia = '0'");
        sql.append(" THEN '0'");
        sql.append(" END co_est_ven,");
        sql.append(" CASE");
        sql.append(" WHEN a.nu_dia_ate > 0");
        sql.append(" AND CAST(now() as date) > CAST(E.fe_finaliza as date )");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL");
        sql.append(" AND d.in_dia = '0'");
        sql.append(" THEN IDOSGD.td_pk_tramite_fu_dia_tra (fe_finaliza, CAST(now() as date))");
        sql.append(" ELSE 0");
        sql.append(" END nu_dia_exc,b.fe_arc_doc,b.fe_ate_doc,");
        sql.append(" TO_CHAR(b.fe_arc_doc,'dd/mm/yy') fe_arc_doc_CORTA,");
        sql.append(" TO_CHAR(b.fe_ate_doc,'dd/mm/yy') fe_ate_doc_CORTA");

        sql.append(" FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK) , IDOSGD.TDTV_DESTINOS B WITH (NOLOCK) ,IDOSGD.TDTX_REMITOS_RESUMEN C WITH (NOLOCK) ,IDOSGD.TDTR_MOTIVO D WITH (NOLOCK) ,");
        sql.append(" (SELECT y.nu_ann, y.nu_emi, y.fe_finaliza, IDOSGD.td_pk_tramite_fu_dia_tra (CAST(now() as date),y.fe_finaliza) nu_dia_fal FROM (SELECT x.nu_ann, x.nu_emi,");
        sql.append(" CAST (x.fe_emi as date)+ IDOSGD.TD_PK_TRAMITE_fu_dia_mas (CAST (x.fe_emi as date),nu_dia_ate) AS fe_finaliza");
        sql.append(" FROM IDOSGD.tdtv_remitos x WITH (NOLOCK) ) y) E,");
//        sql.append(" (SELECT NU_ANN, NU_EMI   FROM IDOSGD.TDTR_ARBOL_SEG    START WITH PK_REF = :pCoAnio||:pNuEmi||'0'   CONNECT BY PRIOR PK_EMI = PK_REF ) F ");
        sql.append(" ("
                + "WITH RECURSIVE q AS ( \n" +
            "    SELECT NU_ANN , NU_EMI \n" +
            "      FROM IDOSGD.TDTR_ARBOL_SEG  po \n" +
            "     WHERE po.PK_REF = :pCoAnio+:pNuEmi+'0' \n" +
            "    UNION ALL\n" +
            "    SELECT po.NU_ANN , po.NU_EMI \n" +
            "      FROM IDOSGD.TDTR_ARBOL_SEG  po \n" +
            "      JOIN IDOSGD.TDTR_ARBOL_SEG q ON q.PK_EMI=po.PK_REF \n" +
            ") \n" +
            "SELECT * FROM q \n" +
            ") F ");
        
        objectParam.put("pCoAnio", documentoRecepConsulBean.getNuAnn());
        objectParam.put("pNuEmi", documentoRecepConsulBean.getNuEmi());
        sql.append(" WHERE ");
        String pNUAnn = documentoRecepConsulBean.getNuAnn();
        sql.append(" A.NU_ANN = :pNuAnn ");
        objectParam.put("pNuAnn", pNUAnn);

//        sql.append(" (SELECT NU_ANN, NU_EMI   FROM IDOSGD.TDTR_ARBOL_SEG    START WITH PK_REF = :pCoAnio||:pNuEmi||'0'   CONNECT BY PRIOR PK_EMI = PK_REF ) F ");
        
        sql.append(" AND F.NU_ANN=A.NU_ANN ");
        sql.append(" AND F.NU_EMI=A.NU_EMI ");
        sql.append(" AND A.NU_ANN=B.NU_ANN ");
        sql.append(" AND A.NU_EMI=B.NU_EMI ");
        sql.append(" AND C.NU_EMI = B.NU_EMI ");
        sql.append(" AND C.NU_ANN = B.NU_ANN ");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7') ");
        sql.append(" AND A.ES_ELI = '0' ");
        sql.append(" AND B.ES_ELI = '0' ");
        sql.append(" AND b.co_mot = d.co_mot ");
        sql.append(" AND a.nu_ann = E.nu_ann ");
        sql.append(" AND a.nu_emi = E.nu_emi ");
//        if (tiAcceso.equals("1")) {
//            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes");
//            objectParam.put("pcoEmpRes", documentoSeguiEstEmitidoBean.getCoEmpRes());
//        }
        if(tiAcceso.equals("1")){
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes");      
            objectParam.put("pcoEmpDes", documentoRecepConsulBean.getCoEmpDes());           
        } 
        sql.append(" ORDER BY A.FE_EMI DESC");
        sql.append(") A ");
//        sql.append("WHERE ROWNUM < 301");
        //sql.append("LIMIT  300");

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoSeguiEstRecibidoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<DocumentoSeguiEstRecibidoBean> getListaReporteBusqueda(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean)
    {        
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        Map<String, Object> objectParam = new HashMap<String, Object>();
        String sqlContains="";
        List<DocumentoSeguiEstRecibidoBean> list = new ArrayList<DocumentoSeguiEstRecibidoBean>();
        
        sql.append("SELECT B.NU_COR_DES,\n" +
                "		 CONVERT(VARCHAR(10), A.FE_EMI, 103) FE_EMI_CORTA,\n" +
                "		 [IDOSGD].[PK_SGD_DESCRIPCION_TI_EMI_EMP](A.NU_ANN, A.NU_EMI) DE_ORI_EMI,\n" +
                "		 [IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
                "		 C.NU_DOC,\n" +
                "		 CASE B.TI_DES\n" +
                "			WHEN '01' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](B.CO_EMP_DES)\n" +
                "			WHEN '02' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](B.NU_RUC_DES)\n" +
                "			WHEN '03' THEN [IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](B.NU_DNI_DES)\n" +
                "			WHEN '04' THEN [IDOSGD].[PK_SGD_DESCRIPCION_OTRO_ORIGEN](B.CO_OTR_ORI_DES)\n" +
                "		 END DE_EMP_DES,\n" +
                "		 A.DE_ASU,C.NU_EXPEDIENTE,\n" +
                "		 [IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS](B.ES_DOC_REC, 'TDTV_DESTINOS') DE_ES_DOC_DES,\n" +
                "		 CONVERT(VARCHAR(10), B.FE_REC_DOC, 103) FE_REC_CORTA,\n" +
                "		 (SELECT CEMP_APEPAT + ' ' + CEMP_APEMAT + ' ' + CEMP_DENOM\n" +
                "		 FROM IDOSGD.RHTM_PER_EMPLEADOS\n" +
                "		 WHERE CEMP_CODEMP = A.CO_EMP_RES) DE_EMP_RES,\n" +
                "		 [IDOSGD].[PK_SGD_DESCRIPCION_MOTIVO](B.CO_MOT) DE_MOTIVO,\n" +
                "		 (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_DE_PRIORIDAD](B.CO_PRI)) DE_PRIORIDAD,\n" +
                "		 (SELECT CEMP_APEPAT + ' ' + CEMP_APEMAT + ' ' + CEMP_DENOM\n" +
                "		 FROM IDOSGD.RHTM_PER_EMPLEADOS\n" +
                "		 WHERE CEMP_CODEMP = B.CO_EMP_REC) DE_EMP_REC,\n" +
                "		 A.NU_DIA_ATE,\n" +
                "		 CONVERT(VARCHAR(10), (DATEADD(DAY, (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_DIA_MAS](CONVERT(DATETIME, A.FE_EMI), A.NU_DIA_ATE)), CONVERT(DATE, A.FE_EMI))), 103) F_LIMITE_CORTA,\n" +
                "		 CASE\n" +
                "			WHEN a.nu_dia_ate > 0\n" +
                "				AND	(DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) <= 2\n" +
                "				AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) > 0\n" +
                "				AND b.fe_ate_doc IS NULL\n" +
                "				AND b.fe_arc_doc IS NULL\n" +
                "				AND d.in_dia = 0\n" +
                "			THEN '1'\n" +
                "			WHEN a.nu_dia_ate > 0\n" +
                "				AND	CONVERT(DATE, GETDATE()) > CONVERT(DATE, E.fe_finaliza)\n" +
                "				AND b.fe_ate_doc IS NULL\n" +
                "				AND b.fe_arc_doc IS NULL\n" +
                "				AND d.in_dia = 0\n" +
                "			THEN '2'\n" +
                "			WHEN a.nu_dia_ate > 0\n" +
                "				AND	CONVERT(DATE, GETDATE()) = CONVERT(DATE, E.fe_finaliza)\n" +
                "				AND b.fe_ate_doc IS NULL\n" +
                "				AND b.fe_arc_doc IS NULL\n" +
                "				AND d.in_dia = 0\n" +
                "			THEN '3'\n" +
                "			WHEN a.nu_dia_ate > 0\n" +
                "				AND	(DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) > 2\n" +
                "				AND b.fe_ate_doc IS NULL\n" +
                "				AND b.fe_arc_doc IS NULL\n" +
                "				AND d.in_dia = 0\n" +
                "			THEN '4'\n" +
                "			WHEN b.fe_ate_doc IS NOT NULL OR b.fe_arc_doc IS NOT NULL\n" +
                "			THEN '5'\n" +
                "			WHEN a.nu_dia_ate = 0 OR D.in_dia = 0\n" +
                "			THEN '0'\n" +
                "		 END co_est_ven,\n" +
                "		 CASE\n" +
                "			WHEN a.nu_dia_ate > 0\n" +
                "				AND	CONVERT(DATE, GETDATE()) > CONVERT(DATE, E.fe_finaliza)\n" +
                "				AND b.fe_ate_doc IS NULL\n" +
                "				AND b.fe_arc_doc IS NULL\n" +
                "				AND d.in_dia = 0\n" +
                "			THEN (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_DIA_TRA](fe_finaliza, CONVERT(DATETIME, GETDATE())))\n" +
                "			ELSE 0\n" +
                "		 END nu_dia_exc,\n" +
                "		 CONVERT(VARCHAR(10), b.fe_arc_doc, 103) fe_arc_doc_CORTA,\n" +
                "		 CONVERT(VARCHAR(10), b.fe_ate_doc, 103) fe_ate_doc_CORTA\n" +
                "FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK) ,\n" +
                "	 IDOSGD.TDTV_DESTINOS B WITH (NOLOCK) ,\n" +
                "	 IDOSGD.TDTX_REMITOS_RESUMEN C WITH (NOLOCK) ,\n" +
                "	 IDOSGD.TDTR_MOTIVO D WITH (NOLOCK) ,\n" +
                "	 (SELECT y.nu_ann,\n" +
                "			 y.nu_emi,\n" +
                "			 y.fe_finaliza,\n" +
                "			 (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_DIA_TRA](CONVERT(DATETIME, GETDATE()), y.fe_finaliza)) nu_dia_fal\n" +
                "	  FROM (SELECT x.nu_ann,\n" +
                "				   x.nu_emi,\n" +
                "				   (DATEADD(DAY, (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_DIA_MAS](CONVERT(DATETIME, x.fe_emi), nu_dia_ate)), CONVERT(DATE, x.fe_emi))) AS fe_finaliza\n" +
                "			FROM IDOSGD.tdtv_remitos x WITH (NOLOCK) ) y) E\n" +
                "WHERE ");
        
        try {

            sql.append(" B.NU_ANN = A.NU_ANN");
            sql.append(" AND B.NU_EMI = A.NU_EMI");
            sql.append(" AND C.NU_ANN = B.NU_ANN");
            sql.append(" AND C.NU_EMI = B.NU_EMI");
            sql.append(" AND b.co_mot = d.co_mot");
            sql.append(" AND a.nu_ann = E.nu_ann");
            sql.append(" AND a.nu_emi = E.nu_emi");

//            String pNUAnn = buscarDocumentoSeguiEstRecBean.getCoAnnio();
//            if (!(buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
//                // Parametros Basicos
//                sql.append(" AND A.NU_ANN = '").append(pNUAnn).append("'");
//            }
            sql.append(" AND A.ES_ELI = '0'");
            sql.append(" AND B.ES_ELI = '0'");
            sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7','8')");
            sql.append(" AND A.IN_OFICIO = '0'");

        if (buscarDocumentoSeguiEstRecBean.getCoEmpDestino()!=null && buscarDocumentoSeguiEstRecBean.getCoEmpDestino().trim().length()>0){
            //sql.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoEmpDestino()).append("'");
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
            objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpDestino());
        }else {    
            if(buscarDocumentoSeguiEstRecBean.getTiAcceso().equals("1")){
                //sql.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoEmpleado()).append("'");
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
                objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpleado());
            }else if(buscarDocumentoSeguiEstRecBean.getTiAcceso().equals("2")){
                //sql.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoEmpleado()).append("'");
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes  \n");
                objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpleado());
            }
        }    
        
        // Parametros Basicos
        String pTipoBusqueda = buscarDocumentoSeguiEstRecBean.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoSeguiEstRecBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }

        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {

            if (buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm() != null && buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm().trim().length() > 0) {
                //sql.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm()).append("'");
                //objectParam.put("pCoDocEmi", buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm());
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoSeguiEstRecBean.getCoTipoDocAdm());
            }
            if (buscarDocumentoSeguiEstRecBean.getEstadoDoc() != null && buscarDocumentoSeguiEstRecBean.getEstadoDoc().trim().length() > 0) {
                //sql.append(" AND B.ES_DOC_REC = '").append(buscarDocumentoSeguiEstRecBean.getEstadoDoc()).append("'");
                sql.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                objectParam.put("pEsDocRec", buscarDocumentoSeguiEstRecBean.getEstadoDoc());
            }
            if (buscarDocumentoSeguiEstRecBean.getPrioridadDoc() != null && buscarDocumentoSeguiEstRecBean.getPrioridadDoc().trim().length() > 0) {
                //sql.append(" AND B.CO_PRI = '").append(buscarDocumentoSeguiEstRecBean.getPrioridadDoc()).append("'");
                sql.append(" AND B.CO_PRI = :pCoPrioridad \n");
                objectParam.put("pCoPrioridad", buscarDocumentoSeguiEstRecBean.getPrioridadDoc());
            }
            if (buscarDocumentoSeguiEstRecBean.getCoDepRemite() != null && buscarDocumentoSeguiEstRecBean.getCoDepRemite().trim().length() > 0) {
                //sql.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = '").append(buscarDocumentoSeguiEstRecBean.getCoDepRemite()).append("'");
                sql.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = :pTiEmiRef \n");
                objectParam.put("pTiEmiRef", buscarDocumentoSeguiEstRecBean.getCoDepRemite());
            }
            if (buscarDocumentoSeguiEstRecBean.getCoDepDestino() != null && buscarDocumentoSeguiEstRecBean.getCoDepDestino().trim().length() > 0) {
                //sql.append(" AND B.CO_DEP_DES = '").append(buscarDocumentoSeguiEstRecBean.getCoDepDestino()).append("'");
                sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                objectParam.put("pcoDepDes", buscarDocumentoSeguiEstRecBean.getCoDepDestino());
            }
            if (buscarDocumentoSeguiEstRecBean.getIdEtiqueta() != null && buscarDocumentoSeguiEstRecBean.getIdEtiqueta().trim().length() > 0) {
                //sql.append(" AND B.CO_ETIQUETA_REC = '").append(buscarDocumentoSeguiEstRecBean.getIdEtiqueta()).append("'");
                sql.append(" AND B.CO_ETIQUETA_REC = :pcoEtiquetaRec ");
                objectParam.put("pcoEtiquetaRec", buscarDocumentoSeguiEstRecBean.getIdEtiqueta());
            }
            if (buscarDocumentoSeguiEstRecBean.getCoVencimiento() != null && buscarDocumentoSeguiEstRecBean.getCoVencimiento().trim().length() > 0) {
                if ("1".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) <= 2");
                    sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) > 0");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = 0");
                }
                else if ("2".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" AND CONVERT(DATE, GETDATE()) > CONVERT(DATE, E.fe_finaliza)");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = 0");
                    sql.append(" AND A.NU_DIA_ATE > 0");
                }
                else if ("3".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    //vence hoy
                    sql.append(" AND CONVERT(DATE, GETDATE()) = CONVERT(DATE, E.fe_finaliza)");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = 0");
                    sql.append(" AND A.NU_DIA_ATE > 0");
                }
                else if ("4".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" AND (DATEDIFF(DAY, CONVERT(DATE, E.fe_finaliza), CONVERT(DATE, GETDATE()))) > 2");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = 0");
                }
                else if ("5".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" AND b.fe_ate_doc is not null");
                    sql.append(" AND b.fe_rec_doc is not null");
                }
                else if ("0".equals(buscarDocumentoSeguiEstRecBean.getCoVencimiento())) {
                    sql.append(" and a.nu_dia_ate = 0");
                    sql.append(" and D.in_dia = 0");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                }

                // '1'-- x vencerse
                // '2'--vencidos
                // '3'--vence hoy
                // '4'--Vence a futuro
                // '5'--Atendido
                // '0'--Normal - Sin vencimiento                
            }
            if (buscarDocumentoSeguiEstRecBean.getEsFiltroFecha() != null
                    && (buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("1") || buscarDocumentoSeguiEstRecBean.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoSeguiEstRecBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoSeguiEstRecBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {  
                    sql.append(" AND A.FE_EMI BETWEEN CONVERT(DATETIME, :pFeEmiIni, 103) AND CONVERT(DATETIME, :pFeEmiFin, 103) + 0.99999 ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
            
        }

        if (pTipoBusqueda.equals("1")) {
            if (!bBusqFiltro) {
                if (buscarDocumentoSeguiEstRecBean.getCoDependencia() != null && buscarDocumentoSeguiEstRecBean.getCoDependencia().trim().length() > 0) {
                    sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                    objectParam.put("pcoDepDes", buscarDocumentoSeguiEstRecBean.getCoDependencia());
                }
            }
            if (buscarDocumentoSeguiEstRecBean.getBusNumDoc() != null && buscarDocumentoSeguiEstRecBean.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND A.NU_DOC_EMI LIKE '%'+:pnuDocEmi+'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoSeguiEstRecBean.getBusNumDoc());
            }

            if (buscarDocumentoSeguiEstRecBean.getBusNumExpediente() != null && buscarDocumentoSeguiEstRecBean.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND C.NU_EXPEDIENTE LIKE '%'+:pnuExpediente+'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoSeguiEstRecBean.getBusNumExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoSeguiEstRecBean.getBusAsunto() != null && buscarDocumentoSeguiEstRecBean.getBusAsunto().trim().length() > 1) {
                sql.append(" AND CONTAINS(A.DE_ASU, :pBusquedaTextual) ");
                sqlContains="SELECT RETORNO FROM [IDOSGD].PK_SGD_DESCRIPCION_QUERYFILTER('"+buscarDocumentoSeguiEstRecBean.getBusAsunto()+"')";                             
            }
             if (buscarDocumentoSeguiEstRecBean.getBusNumDocRef() != null && buscarDocumentoSeguiEstRecBean.getBusNumDocRef().trim().length() > 1) {
                    //busqeda referencia
                     sql.append("  AND A.NU_EMI IN( ");
                    sql.append("   SELECT D.NU_EMI ");
                    sql.append("   FROM IDOSGD.TDTR_ARBOL_SEG D ");
                    sql.append("   INNER JOIN IDOSGD.TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                    sql.append("   WHERE R.NU_ANN='"+buscarDocumentoSeguiEstRecBean.getBusCoAnnio()+"'   ");
                    if (buscarDocumentoSeguiEstRecBean.getBusCodTipoDocRef() != null && buscarDocumentoSeguiEstRecBean.getBusCodTipoDocRef().trim().length() > 1) {
                    sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoSeguiEstRecBean.getBusCodTipoDocRef()+"' ");
                    }
                    if (buscarDocumentoSeguiEstRecBean.getBusCodDepEmiRef() != null && buscarDocumentoSeguiEstRecBean.getBusCodDepEmiRef().trim().length() > 1) {
                    sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoSeguiEstRecBean.getBusCodDepEmiRef()+"' ");
                    }
                    sql.append("   AND ((R.NU_DOC_EMI LIKE '%'+'"+buscarDocumentoSeguiEstRecBean.getBusNumDocRef()+"'+'%') OR (R.DE_DOC_SIG LIKE '%'+'"+buscarDocumentoSeguiEstRecBean.getBusNumDocRef()+"'+'%'))");
                    sql.append(" )");
                }
        }            
            
        sql.append(" ORDER BY A.FE_EMI DESC ");
        
        
        if(sqlContains.length()>0){
            String cadena = this.jdbcTemplate.queryForObject(sqlContains, String.class);            
            objectParam.put("pBusquedaTextual", cadena);
        }
        list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoSeguiEstRecibidoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

}
