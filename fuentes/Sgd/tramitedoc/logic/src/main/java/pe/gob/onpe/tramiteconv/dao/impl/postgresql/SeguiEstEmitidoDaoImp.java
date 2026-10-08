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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstEmiBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstEmitidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.dao.SeguiEstEmitidoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author NGilt
 */
@Repository("consultaEstEmitidoDao")
public class SeguiEstEmitidoDaoImp extends SimpleJdbcDaoBase implements SeguiEstEmitidoDao {

    @Override
    public List<DocumentoSeguiEstEmitidoBean> getListDocSeguiEstRec(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoSeguiEstEmitidoBean> list = new ArrayList<DocumentoSeguiEstEmitidoBean>();

        sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(X.NU_ANN, X.NU_EMI) DE_EMI_REF,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (X.NU_ANN, X.NU_EMI) DE_ORI_EMI,"); 
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
         
        sql.append("CASE\n" +
                "WHEN X.TI_DES = '01' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA (X.CO_DEP_DES)|| ' - ' || IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.CO_EMP_DES)\n" +
                "WHEN X.TI_DES = '02' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.NU_RUC_DES) \n" +
                "WHEN X.TI_DES = '03' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (X.NU_DNI_DES) \n" +
                "WHEN X.TI_DES = '04' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (X.CO_OTR_ORI_DES) \n" +
                "END AS DE_EMP_DES,"
        );
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (X.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,");    
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(X.CO_MOT) DE_MOTIVO, IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_REC) DE_EMP_REC,");        
        sql.append(" CAST(X.FE_EMI as date) + IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas(CAST(X.FE_EMI as date),X.NU_DIA_ATE) F_LIMITE,");
        sql.append(" TO_CHAR((CAST(X.FE_EMI as date) + IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas(CAST(X.FE_EMI as date),X.NU_DIA_ATE)),'DD/MM/YYYY') F_LIMITE_CORTA ");  
        sql.append(" FROM ( ");
        sql.append(" SELECT A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" C.NU_DOC,A.CO_TIP_DOC_ADM,B.TI_DES,B.CO_DEP_DES,B.CO_EMP_DES,B.NU_RUC_DES,B.NU_DNI_DES,B.CO_OTR_ORI_DES,");
        sql.append(" UPPER (A.DE_ASU) DE_ASU_M,C.NU_EXPEDIENTE,A.CO_EMP_RES,");

        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append("CASE\n"
                + "WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='00' THEN\n"
                + "0\n"
                + "WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='02' THEN\n"
                + "0\n"
                + "ELSE\n"
                + "1 \n"
                + "END AS EXISTE_ANEXO,"); 
        sql.append(" COALESCE(B.CO_PRI,'1') CO_PRI,B.ES_DOC_REC,");
        sql.append(" B.FE_REC_DOC,TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY') FE_REC_CORTA,B.CO_MOT,B.CO_EMP_REC,");
        sql.append(" B.CO_ETIQUETA_REC,");
        sql.append(" A.NU_DIA_ATE,");
        sql.append(" CASE");
        sql.append(" WHEN a.nu_dia_ate > 0");
        sql.append(" AND (CAST(E.fe_finaliza as date) - CAST(now() as date)) <= 2");
        sql.append(" AND (CAST (E.fe_finaliza as date) - CAST (now() as date)) > 0");
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
        sql.append(" WHEN (b.fe_ate_doc IS   NULL OR b.fe_arc_doc IS   NULL) AND (a.nu_dia_ate = 0 OR D.in_dia = '0')");
        sql.append(" THEN '0'");
        sql.append(" END co_est_ven,");
        sql.append(" CASE");
        sql.append(" WHEN a.nu_dia_ate > 0");
        sql.append(" AND CAST(now() as date) > CAST(E.fe_finaliza as date )");
        sql.append(" AND b.fe_ate_doc IS NULL");
        sql.append(" AND b.fe_arc_doc IS NULL");
        sql.append(" AND d.in_dia = '0'");
        sql.append(" THEN IDOSGD.PK_SGD_DESCRIPCION_fu_dia_tra (fe_finaliza, CAST(now() as date))");
        sql.append(" ELSE 0");
        sql.append(" END nu_dia_exc,b.fe_arc_doc,b.fe_ate_doc,");
        sql.append(" TO_CHAR(b.fe_arc_doc,'dd/mm/yy') fe_arc_doc_CORTA,");
        sql.append(" TO_CHAR(b.fe_ate_doc,'dd/mm/yy') fe_ate_doc_CORTA");

        sql.append(" FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B, IDOSGD.TDTX_REMITOS_RESUMEN C, IDOSGD.TDTR_MOTIVO D,");
        sql.append(" (SELECT y.nu_ann, y.nu_emi, y.fe_finaliza, IDOSGD.PK_SGD_DESCRIPCION_fu_dia_tra (CAST(now() as date),y.fe_finaliza) nu_dia_fal FROM (SELECT x.nu_ann, x.nu_emi,");
        sql.append(" CAST (x.fe_emi as date)+ IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas (CAST (x.fe_emi as date),nu_dia_ate) AS fe_finaliza");
        sql.append(" FROM IDOSGD.tdtv_remitos x) y) E");
        sql.append(" WHERE");
        sql.append(" A.NU_ANN=B.NU_ANN ");
        sql.append(" AND A.NU_EMI=B.NU_EMI ");
        sql.append(" AND C.NU_EMI = B.NU_EMI ");
        
//        String pNUAnn = buscarDocumentoSeguiEstEmiBean.getCoAnnio();
//        if (!(buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN = :pNuAnn ");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNUAnn);
//        }

        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7','8') ");
        sql.append(" AND A.ES_ELI = '0' ");
        sql.append(" AND B.ES_ELI = '0' ");
        sql.append(" AND b.co_mot = d.co_mot ");
        sql.append(" AND a.nu_ann = E.nu_ann ");
        sql.append(" AND a.nu_emi = E.nu_emi ");
        sql.append(" AND A.TI_EMI='01' ");
        sql.append(" AND A.CO_GRU = '1'");

        // Parametros Basicos
        String pTipoBusqueda = buscarDocumentoSeguiEstEmiBean.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoSeguiEstEmiBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }

        //Tipo de acceso personal o total
        String auxTipoAcceso = buscarDocumentoSeguiEstEmiBean.getTiAcceso();
        String tiAcceso = auxTipoAcceso != null && (auxTipoAcceso.equals("0") || auxTipoAcceso.equals("1") || auxTipoAcceso.equals("2")) ? auxTipoAcceso : "1";
        if (buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro() != null && buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro().trim().length() > 0 && (pTipoBusqueda.equals("0") || bBusqFiltro) && tiAcceso.equals("0")) {
            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro());
        } else {
            if (tiAcceso.equals("1")) {//PERSONAL ,'0' TOTAL
                sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoSeguiEstEmiBean.getCoEmpleado());
            } else if (tiAcceso.equals("2")) {//TOTAL
                sql.append(" AND (A.CO_EMP_RES = :pcoEmpRes OR A.CO_EMP_EMI = :pcoEmpRes) ");
                objectParam.put("pcoEmpRes", buscarDocumentoSeguiEstEmiBean.getCoEmpleado());
            }
        }
        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (buscarDocumentoSeguiEstEmiBean.getEstadoDoc() != null && buscarDocumentoSeguiEstEmiBean.getEstadoDoc().trim().length() > 0) {
                sql.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                objectParam.put("pEsDocRec", buscarDocumentoSeguiEstEmiBean.getEstadoDoc());
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm() != null && buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm());
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoRefOrigen() != null && buscarDocumentoSeguiEstEmiBean.getCoRefOrigen().trim().length() > 0) {
                sql.append(" AND STRPOS(C.TI_EMI_REF, :pTiEmiRef) > 0 ");
                objectParam.put("pTiEmiRef", buscarDocumentoSeguiEstEmiBean.getCoRefOrigen());
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoDepOrigen() != null && buscarDocumentoSeguiEstEmiBean.getCoDepOrigen().trim().length() > 0) {
                sql.append(" AND A.CO_DEP_EMI = :pCoDepOrigen ");
                objectParam.put("pCoDepOrigen", buscarDocumentoSeguiEstEmiBean.getCoDepOrigen());
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoDepDestino() != null && buscarDocumentoSeguiEstEmiBean.getCoDepDestino().trim().length() > 0) {
                sql.append(" AND STRPOS(C.TI_EMI_DES, :pTiEmpPro) > 0 ");
                objectParam.put("pTiEmpPro", buscarDocumentoSeguiEstEmiBean.getCoDepDestino());
            }

            if (buscarDocumentoSeguiEstEmiBean.getCoVencimiento() != null && buscarDocumentoSeguiEstEmiBean.getCoVencimiento().trim().length() > 0) {

                if ("1".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) <= 2");
                    sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) > 0");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                }
                if ("2".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND CAST(now() as date) > CAST(E.fe_finaliza as date )");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                    sql.append(" AND A.NU_DIA_ATE <> 0");
                }
                if ("3".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    //vence hoy
                    sql.append(" AND CAST(now() as date) = CAST(E.fe_finaliza as date )");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                    sql.append(" AND A.NU_DIA_ATE <> 0");
                }
                if ("4".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) > 2");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                }
                 
                if ("5".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND ( b.fe_ate_doc is not null");
                    sql.append(" OR b.fe_arc_doc is not null )");
                }
                if ("0".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" and (a.nu_dia_ate = 0");
                    sql.append(" OR D.in_dia = '0' ) ");
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
            if (buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha() != null
                    && (buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("1") || buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("2") || buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoSeguiEstEmiBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoSeguiEstEmiBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) { 
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59' ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
        }

        if (pTipoBusqueda.equals("1")) {
            if (!bBusqFiltro) {
                if (buscarDocumentoSeguiEstEmiBean.getCoDepDestino() != null && buscarDocumentoSeguiEstEmiBean.getCoDepDestino().trim().length() > 0) {
                    sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                    objectParam.put("pcoDepDes", buscarDocumentoSeguiEstEmiBean.getCoDepDestino());
                }
            }
            if (buscarDocumentoSeguiEstEmiBean.getBusNumDoc() != null && buscarDocumentoSeguiEstEmiBean.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND ((A.NU_DOC_EMI LIKE '%'||:pnuDocEmi||'%') OR (A.NU_DOC_EMI ||'-'|| A.NU_ANN ||'-'|| A.DE_DOC_SIG  LIKE '%'||:pnuDocEmiAlt||'%')) ");                
                objectParam.put("pnuDocEmi", buscarDocumentoSeguiEstEmiBean.getBusNumDoc());
                objectParam.put("pnuDocEmiAlt", buscarDocumentoSeguiEstEmiBean.getBusNumDoc());
            }

            if (buscarDocumentoSeguiEstEmiBean.getBusNumExpediente() != null && buscarDocumentoSeguiEstEmiBean.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND C.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoSeguiEstEmiBean.getBusNumExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoSeguiEstEmiBean.getBusAsunto() != null && buscarDocumentoSeguiEstEmiBean.getBusAsunto().trim().length() > 1) {                 
               sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
               objectParam.put("pDeAsunto", buscarDocumentoSeguiEstEmiBean.getBusAsunto());                
            }
            if (buscarDocumentoSeguiEstEmiBean.getBusNumDocRef() != null && buscarDocumentoSeguiEstEmiBean.getBusNumDocRef().trim().length() > 1) {
                //busqeda referencia
                 sql.append("  AND A.NU_EMI IN( ");
                sql.append("   SELECT D.NU_EMI ");
                sql.append("   FROM IDOSGD.TDTR_ARBOL_SEG D ");
                sql.append("   INNER JOIN IDOSGD.TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                sql.append("   WHERE R.NU_ANN='"+buscarDocumentoSeguiEstEmiBean.getBusCoAnnio()+"'   ");
                if (buscarDocumentoSeguiEstEmiBean.getBusCodTipoDocRef() != null && buscarDocumentoSeguiEstEmiBean.getBusCodTipoDocRef().trim().length() > 1) {
                sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoSeguiEstEmiBean.getBusCodTipoDocRef()+"' ");
                }
                if (buscarDocumentoSeguiEstEmiBean.getBusCodDepEmiRef() != null && buscarDocumentoSeguiEstEmiBean.getBusCodDepEmiRef().trim().length() > 1) {
                sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoSeguiEstEmiBean.getBusCodDepEmiRef()+"' ");
                }
                sql.append("   AND ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoSeguiEstEmiBean.getBusNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoSeguiEstEmiBean.getBusNumDocRef()+"'||'%'))");
                sql.append(" )");
            }
        }

        sql.append(" ORDER BY A.FE_EMI DESC");
        sql.append(") X ");
//        sql.append("WHERE ROWNUM < 301");
        sql.append(" LIMIT 300");

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoSeguiEstEmitidoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<DependenciaBean> getListDestinatarioEmi(String coDep, String pdeDepEmite) {
        StringBuffer sql = new StringBuffer();

        boolean vfiltro = pdeDepEmite != null && !pdeDepEmite.trim().equals("") ? true : false;

        sql.append("SELECT DE_DEPENDENCIA,\n"
                + "       CO_DEPENDENCIA,\n"
                + "       DE_CORTA_DEPEN\n"
                + "  FROM IDOSGD.RHTM_DEPENDENCIA\n"
                + " WHERE co_nivel <> '6'\n"
                + "   AND IN_BAJA = '0'");
        if (vfiltro) {
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%' )");
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
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%')");
        }
        sql.append("  UNION");
        sql.append(" SELECT ' [TODOS]',");
        sql.append(" 	   NULL,");
        sql.append(" 	   NULL "); 
        if (vfiltro) {
            sql.append(" where ' [TODOS]' like '%'||?||'%'");
        }
        sql.append("  ORDER BY 1");

        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        try {
            if (vfiltro) {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{pdeDepEmite, pdeDepEmite, coDep, coDep,
                    pdeDepEmite, pdeDepEmite, pdeDepEmite});
            } else {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class), new Object[]{coDep, coDep});
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
    public DocumentoSeguiEstEmitidoBean existeDocumentoReferenciado(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        StringBuffer sql = new StringBuffer();
        DocumentoSeguiEstEmitidoBean documentoSeguiEstEmitidoBean = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_DOC FROM IDOSGD.TDTV_REMITOS A\n"
                + "WHERE A.NU_ANN=?\n"
                + "AND A.CO_DEP_EMI=?\n"
                + "AND A.TI_EMI='01'\n"
                + "AND A.CO_TIP_DOC_ADM=?\n"
                + "AND A.NU_DOC_EMI=?\n"
                + "AND A.ES_ELI='0'\n"
                + "AND A.ES_DOC_EMI NOT IN ('5','7','9')");

        try {
            documentoSeguiEstEmitidoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoSeguiEstEmitidoBean.class),
                    new Object[]{buscarDocumentoSeguiEstEmiBean.getBusCoAnnio(), buscarDocumentoSeguiEstEmiBean.getBusCodDepEmiRef(), buscarDocumentoSeguiEstEmiBean.getBusCodTipoDocRef(),
                        buscarDocumentoSeguiEstEmiBean.getBusNumDocRef()});
            documentoSeguiEstEmitidoBean.setMsjResult("OK");
        } catch (EmptyResultDataAccessException e) {
            documentoSeguiEstEmitidoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoSeguiEstEmitidoBean;
    }

    @Override
    public List<DocumentoSeguiEstEmitidoBean> getDocumentosReferenciadoBusq(DocumentoSeguiEstEmitidoBean documentoSeguiEstEmitidoBean, String tiAcceso) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List<DocumentoSeguiEstEmitidoBean> list = new ArrayList<DocumentoSeguiEstEmitidoBean>();

//        sql.append("SELECT A.*, ROWNUM");
        sql.append("SELECT A.*");
        sql.append(" FROM ( ");
        sql.append(" SELECT DISTINCT A.NU_COR_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(A.NU_ANN, A.NU_EMI) DE_EMI_REF,A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,C.NU_DOC,"); 
        
        sql.append(" CASE ");
        sql.append(" 			WHEN  B.TI_DES='01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES) ");
        sql.append(" 				 WHEN  B.TI_DES='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES) ");
        sql.append(" 				 WHEN  B.TI_DES='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES) ");
        sql.append(" 				 WHEN  B.TI_DES='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES)");
        sql.append(" 				END AS DE_EMP_DES,");
        
        
        sql.append(" UPPER (A.DE_ASU) DE_ASU_M,C.NU_EXPEDIENTE,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,"); 
        
        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC, "
                + " CASE  WHEN (COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2'))='00' THEN 0 "
                + " WHEN (COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2'))='02' THEN 0 ELSE 1 END AS EXISTE_ANEXO,COALESCE(B.CO_PRI,'1') CO_PRI,B.ES_DOC_REC,");
        
        
        sql.append(" B.FE_REC_DOC,TO_CHAR(B.FE_REC_DOC,'DD/MM/YY') FE_REC_CORTA,(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM IDOSGD.RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = A.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(B.CO_MOT) DE_MOTIVO,(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM IDOSGD.RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = B.CO_EMP_REC) DE_EMP_REC,");
        sql.append(" B.CO_ETIQUETA_REC,");

        sql.append(" A.NU_DIA_ATE,CAST(A.FE_EMI as date) + IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas(CAST(A.FE_EMI as date),A.NU_DIA_ATE) F_LIMITE,");
        sql.append(" TO_CHAR((CAST(A.FE_EMI as date) + IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas(CAST(A.FE_EMI as date),A.NU_DIA_ATE)),'DD/MM/YY') F_LIMITE_CORTA,");
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
        sql.append(" THEN IDOSGD.PK_SGD_DESCRIPCION_fu_dia_tra (fe_finaliza, CAST(now() as date))");
        sql.append(" ELSE 0");
        sql.append(" END nu_dia_exc,b.fe_arc_doc,b.fe_ate_doc,");
        sql.append(" TO_CHAR(b.fe_arc_doc,'dd/mm/yy') fe_arc_doc_CORTA,");
        sql.append(" TO_CHAR(b.fe_ate_doc,'dd/mm/yy') fe_ate_doc_CORTA");
        sql.append(" FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B,IDOSGD.TDTX_REMITOS_RESUMEN C,IDOSGD.TDTR_MOTIVO D,");
        sql.append(" (SELECT y.nu_ann, y.nu_emi, y.fe_finaliza, IDOSGD.PK_SGD_DESCRIPCION_fu_dia_tra (CAST(now() as date),y.fe_finaliza) nu_dia_fal FROM (SELECT x.nu_ann, x.nu_emi,");
        sql.append(" CAST (x.fe_emi as date)+ IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas (CAST (x.fe_emi as date),nu_dia_ate) AS fe_finaliza");
        sql.append(" FROM IDOSGD.tdtv_remitos x) y) E,"); 
        sql.append(" ("
                + "WITH RECURSIVE q AS ( \n" +
            "    SELECT NU_ANN , NU_EMI \n" +
            "      FROM IDOSGD.TDTR_ARBOL_SEG  po \n" +
            "     WHERE po.PK_REF = :pCoAnio||:pNuEmi||'0' \n" +
            "    UNION ALL\n" +
            "    SELECT po.NU_ANN , po.NU_EMI \n" +
            "      FROM IDOSGD.TDTR_ARBOL_SEG  po \n" +
            "      JOIN IDOSGD.TDTR_ARBOL_SEG q ON q.PK_EMI=po.PK_REF \n" +
            ") \n" +
            "SELECT * FROM q \n" +
            ") F ");
        
        objectParam.put("pCoAnio", documentoSeguiEstEmitidoBean.getNuAnn());
        objectParam.put("pNuEmi", documentoSeguiEstEmitidoBean.getNuEmi());
        sql.append(" WHERE ");
        String pNUAnn = documentoSeguiEstEmitidoBean.getNuAnn();
        sql.append(" A.NU_ANN = :pNuAnn ");
        objectParam.put("pNuAnn", pNUAnn);
 
        sql.append(" AND F.NU_ANN=A.NU_ANN ");
        sql.append(" AND F.NU_EMI=A.NU_EMI ");
        sql.append(" AND A.NU_ANN=B.NU_ANN ");
        sql.append(" AND A.NU_EMI=B.NU_EMI ");
        sql.append(" AND C.NU_EMI = B.NU_EMI ");

        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7') ");
        sql.append(" AND A.ES_ELI = '0' ");
        sql.append(" AND B.ES_ELI = '0' ");
        sql.append(" AND b.co_mot = d.co_mot ");
        sql.append(" AND a.nu_ann = E.nu_ann ");
        sql.append(" AND a.nu_emi = E.nu_emi ");
        if (tiAcceso.equals("1")) {
            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes");
            objectParam.put("pcoEmpRes", documentoSeguiEstEmitidoBean.getCoEmpRes());
        }
        sql.append(" ORDER BY A.FE_EMI DESC");
        sql.append(") A ");
//        sql.append("WHERE ROWNUM < 301");
        sql.append("LIMIT  300");

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoSeguiEstEmitidoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public DocumentoEmiSeguiBean getDocumentoEmiSeguiBean(String pnuAnn, String pnuEmi, String pnuDes) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT B.DE_ANE,A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,C.NU_EXPEDIENTE,TO_CHAR((SELECT FE_EXP FROM IDOSGD.TDTC_EXPEDIENTE WHERE NU_ANN_EXP=A.NU_ANN_EXP AND NU_SEC_EXP=A.NU_SEC_EXP),'DD/MM/YYYY') FE_EXP_CORTA,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(C.CO_PROCESO_EXP) DE_PROCESO_EXP, IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI, IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_REC) DE_EMP_REC,\n"
                
                +"CASE\n"
                + "WHEN B.TI_DES = '01' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES)\n"
                + "WHEN B.TI_DES = '02' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES)\n"
                + "WHEN B.TI_DES = '03' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES)\n"
                + "WHEN B.TI_DES = '04' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES)\n"
                + "END AS DE_EMP_DES,"
                
                
                +"CASE\n"
                + "WHEN A.TI_EMI = '01' THEN\n"
                + "A.NU_DOC_EMI || '-' || A.NU_ANN || '/' || A.DE_DOC_SIG \n"
                + "WHEN A.TI_EMI = '05' THEN\n"
                + "A.NU_DOC_EMI || '-' || A.NU_ANN || '/' || A.DE_DOC_SIG \n"
                + "ELSE \n"
                + "A.DE_DOC_SIG \n"
                + "END AS NU_DOC,"
                
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA (B.CO_DEP_DES) DE_DEP_DES, IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n"
                + "A.DE_ASU,A.NU_DIA_ATE, IDOSGD.PK_SGD_DESCRIPCION_MOTIVO (B.CO_MOT) DE_MOTIVO,B.DE_PRO DE_INDICACIONES, IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD (B.CO_PRI) DE_PRIORIDAD,\n"
                + "TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY HH24:MI:SS') FE_REC_CORTA,TO_CHAR(B.FE_ATE_DOC,'DD/MM/YYYY') FE_ATENCION_CORTA,\n"
                + "TO_CHAR(B.FE_ARC_DOC,'DD/MM/YYYY') FE_ARCHIVAMIENTO_CORTA, IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_REC,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_RES) DE_EMP_RES\n"
                + "FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B, IDOSGD.TDTX_REMITOS_RESUMEN C\n"
                + "WHERE A.ES_ELI='0' AND B.ES_ELI='0'\n"
                + "AND A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI\n"
                + "AND C.NU_ANN = A.NU_ANN AND C.NU_EMI = A.NU_EMI\n"
                + "AND A.ES_DOC_EMI NOT IN ('5', '9', '7')\n"
                + "AND A.IN_OFICIO = '0'\n"
                + "AND B.NU_ANN=? AND B.NU_EMI=? AND B.NU_DES=CAST(? AS BIGINT)");
        DocumentoEmiSeguiBean documentoEmiSeguiBean = new DocumentoEmiSeguiBean();
        try {
            documentoEmiSeguiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiSeguiBean.class),
                    new Object[]{pnuAnn, pnuEmi, pnuDes});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiSeguiBean = null;
        } catch (Exception e) {
            System.out.println("nu_ann=" + pnuAnn + "," + "nu_emi=" + pnuEmi + "nu_des=" + pnuDes);
            e.printStackTrace();
        }
        return documentoEmiSeguiBean;
    }

    @Override
    public List<ReferenciaConsulBean> getDocumentosRefRecepAdm(String snuAnn, String snuEmi) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tipo_doc, " 
                +"CASE\n"
                + "WHEN a.ti_emi = '01' THEN\n"
                + "a.nu_doc_emi || '-' || a.nu_ann || '/' || a.de_doc_sig \n"
                + "WHEN a.ti_emi = '05' THEN\n"
                + "a.nu_doc_emi || '-' || a.nu_ann || '/' || a.de_doc_sig\n"
                + "ELSE \n"
                + "a.de_doc_sig \n"
                + "END AS li_nu_doc, \n"
                
                + "TO_CHAR(a.fe_emi,'DD/MM/YYYY') fe_emi_corta,b.co_ref,b.nu_ann,b.nu_emi,b.nu_des,b.nu_ann_ref,\n"
                + "                 b.nu_emi_ref,b.nu_des_ref\n"
                + "FROM IDOSGD.tdtv_remitos a, IDOSGD.TDTR_REFERENCIA b\n"
                + "WHERE a.nu_ann = b.nu_ann_ref\n"
                + "AND a.nu_emi = b.nu_emi_ref\n"
                + "AND b.NU_EMI=? \n"
                + "AND b.NU_ANN=?");

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
    public String getRutaReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        String vResult;
        StringBuffer sql = new StringBuffer();
        try {

            String pNUAnn = buscarDocumentoSeguiEstEmiBean.getCoAnnio();
            if (!(buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
                sql.append(" A.NU_ANN = '").append(pNUAnn).append("'");
            }
            sql.append(" AND A.NU_ANN=B.NU_ANN ");
            sql.append(" AND A.NU_EMI=B.NU_EMI ");
            sql.append(" AND C.NU_EMI = B.NU_EMI ");
            sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7') ");
            sql.append(" AND A.ES_ELI = '0' ");
            sql.append(" AND B.ES_ELI = '0' ");
            sql.append(" AND b.co_mot = d.co_mot ");
            sql.append(" AND a.nu_ann = E.nu_ann ");
            sql.append(" AND a.nu_emi = E.nu_emi ");
            sql.append(" AND A.TI_EMI='01' ");

            //Tipo de acceso personal o total
            String auxTipoAcceso = buscarDocumentoSeguiEstEmiBean.getTiAcceso();
            String tiAcceso = auxTipoAcceso != null && (auxTipoAcceso.equals("0") || auxTipoAcceso.equals("1") || auxTipoAcceso.equals("2")) ? auxTipoAcceso : "1";
            if (buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro() != null && buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro().trim().length() > 0 && tiAcceso.equals("0")) {
                sql.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro()).append("'");
            } else {
                if (tiAcceso.equals("1")) {//PERSONAL ,'0' TOTAL
                    sql.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpleado()).append("'");
                } else if (tiAcceso.equals("2")) {//TOTAL
                    sql.append(" AND (A.CO_EMP_RES = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpleado()).append("' OR A.CO_EMP_EMI = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpleado()).append("') ");
                }
            }
            //Filtro

            if (buscarDocumentoSeguiEstEmiBean.getEstadoDoc() != null && buscarDocumentoSeguiEstEmiBean.getEstadoDoc().trim().length() > 0) {
                sql.append(" AND B.ES_DOC_REC = '").append(buscarDocumentoSeguiEstEmiBean.getEstadoDoc()).append("'");
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm() != null && buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm()).append("'");
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoRefOrigen() != null && buscarDocumentoSeguiEstEmiBean.getCoRefOrigen().trim().length() > 0) {
                sql.append(" AND STRPOS(C.TI_EMI_REF, '").append(buscarDocumentoSeguiEstEmiBean.getCoRefOrigen()).append("') > 0 ");
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoDepOrigen() != null && buscarDocumentoSeguiEstEmiBean.getCoDepOrigen().trim().length() > 0) {
                sql.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoSeguiEstEmiBean.getCoDepOrigen()).append("'");
            }
            if (buscarDocumentoSeguiEstEmiBean.getCoDepDestino() != null && buscarDocumentoSeguiEstEmiBean.getCoDepDestino().trim().length() > 0) {
                sql.append(" AND STRPOS(C.TI_EMI_DES, '").append(buscarDocumentoSeguiEstEmiBean.getCoDepDestino()).append("') > 0 ");
            }

            if (buscarDocumentoSeguiEstEmiBean.getCoVencimiento() != null && buscarDocumentoSeguiEstEmiBean.getCoVencimiento().trim().length() > 0) {

                if ("1".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) <= 2");
                    sql.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) > 0");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                }
                if ("2".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND CAST(NOW() AS DATE) > CAST(E.fe_finaliza AS DATE)");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                    sql.append(" AND A.NU_DIA_ATE <> 0");
                }
                if ("3".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    //vence hoy
                    sql.append(" AND CAST(NOW() AS DATE) = CAST(E.fe_finaliza AS DATE)");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                    sql.append(" AND A.NU_DIA_ATE <> 0");
                }
                if ("4".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) > 2");
                    sql.append(" AND b.fe_ate_doc IS NULL");
                    sql.append(" AND b.fe_arc_doc IS NULL");
                    sql.append(" AND d.in_dia = '0'");
                }
                if ("5".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" AND b.fe_ate_doc is not null");
                    sql.append(" AND b.fe_rec_doc is not null");
                }
                if ("0".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                    sql.append(" and a.nu_dia_ate = 0");
                    sql.append(" and D.in_dia = '0'");
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
            if (buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha() != null
                    && (buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("1") || buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoSeguiEstEmiBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoSeguiEstEmiBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                sql.append(" AND A.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("','dd/mm/yyyy') + TIME '23:59:59' ");
                }
            }
            sql.append(" ");
//            vResult = "0" + sql.toString().replace("+", "%2B").replace("-", "%2D").replace("<", "%3C").replace(">", "%3E");
            vResult = "0" + sql.toString();
        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }
        return vResult;
    }

    @Override
    public List<DocumentoSeguiEstEmitidoBean> getListaReporteBusqueda(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        Map<String, Object> objectParam = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        List<DocumentoSeguiEstEmitidoBean> list = new ArrayList<DocumentoSeguiEstEmitidoBean>();
        try {

            sql.append("SELECT	(IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(A.NU_ANN, A.NU_EMI)) DE_EMI_REF,\n" +
    "		A.NU_COR_EMI,\n" +
    "		TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n" +
    "		IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
    "		C.NU_DOC,\n" +
    "		CASE B.TI_DES\n" +
    "			WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(B.CO_DEP_DES) || ' - ' || IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(B.CO_EMP_DES)\n" +
    "			WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(B.NU_RUC_DES)\n" +
    "			WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(B.NU_DNI_DES)\n" +
    "			WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(B.CO_OTR_ORI_DES)\n" +
    "		END DE_EMP_DES,\n" +
    "        A.DE_ASU,\n" +
    "		C.NU_EXPEDIENTE,\n" +
    "		IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(B.ES_DOC_REC, 'TDTV_DESTINOS') DE_ES_DOC_DES,\n" +
    "        TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY') FE_REC_DOC_CORTA,\n" +
    "		(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM\n" +
    "		FROM IDOSGD.RHTM_PER_EMPLEADOS\n" +
    "		WHERE CEMP_CODEMP = A.CO_EMP_RES) DE_EMP_RES,\n" +
    "		IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(B.CO_MOT) DE_MOTIVO,\n" +
    "		(IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD(B.CO_PRI)) DE_PRIORIDAD,\n" +
    "		(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM\n" +
    "		FROM IDOSGD.RHTM_PER_EMPLEADOS\n" +
    "		WHERE CEMP_CODEMP = B.CO_EMP_REC) DE_EMP_REC,\n" +
    "        A.NU_DIA_ATE,\n" +
    "		TO_CHAR((CAST(A.FE_EMI as date) + IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas(CAST(A.FE_EMI as date),A.NU_DIA_ATE)),'DD/MM/YYYY') F_LIMITE_CORTA ,\n"+
    "		CASE\n" +
    "			WHEN a.nu_dia_ate > 0\n" +
    "				AND (CAST(E.fe_finaliza as date) - CAST(now() as date)) <= 2\n" +
    "				AND (CAST (E.fe_finaliza as date) - CAST (now() as date)) > 0\n" +
    "				AND b.fe_ate_doc IS NULL\n" +
    "				AND b.fe_arc_doc IS NULL\n" +
    "				AND d.in_dia = '0'\n" +
    "			THEN '1'\n" +
    "			WHEN a.nu_dia_ate > 0\n" +
    "				AND CAST(now() as date) > CAST(E.fe_finaliza as date )\n" +
    "				AND b.fe_ate_doc IS NULL\n" +
    "				AND b.fe_arc_doc IS NULL\n" +
    "				AND d.in_dia = '0'\n" +
    "			THEN '2'\n" +
    "			WHEN a.nu_dia_ate > 0\n" +
    "				AND CAST(now() as date) = CAST(E.fe_finaliza as date )\n" +
    "				AND b.fe_ate_doc IS NULL\n" +
    "				AND b.fe_arc_doc IS NULL\n" +
    "				AND d.in_dia = '0'\n" +
    "			THEN '3'\n" +
    "			WHEN a.nu_dia_ate > 0\n" +
    "				AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) > 2\n" +
    "				AND b.fe_ate_doc IS NULL\n" +
    "				AND b.fe_arc_doc IS NULL\n" +
    "				AND d.in_dia = '0'\n" +
    "			THEN '4'\n" +
    "			WHEN b.fe_ate_doc IS NOT NULL OR b.fe_arc_doc IS NOT NULL\n" +
    "			THEN '5'\n" +
    "			WHEN a.nu_dia_ate = '0' OR D.in_dia = '0'\n" +
    "			THEN '0'\n" +
    "		END co_est_ven,\n" +
    "		CASE\n" +
    "			WHEN a.nu_dia_ate > 0\n" +
    "				AND CAST(now() as date) > CAST(E.fe_finaliza as date )\n" +
    "				AND b.fe_ate_doc IS NULL\n" +
    "				AND b.fe_arc_doc IS NULL\n" +
    "				AND d.in_dia = '0'\n" +
    "			THEN (IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_TRA(fe_finaliza, CAST(now() as date)))\n" +
    "			ELSE '0'\n" +
    "		END nu_dia_exc,\n" +
    "        TO_CHAR(b.fe_arc_doc,'dd/mm/yy') fe_arc_doc_CORTA,\n" +
    "        TO_CHAR(b.fe_ate_doc,'dd/mm/yy') fe_ate_doc_CORTA \n" +
    "FROM IDOSGD.TDTV_REMITOS A,\n" +
    "	 IDOSGD.TDTV_DESTINOS B,\n" +
    "	 IDOSGD.TDTX_REMITOS_RESUMEN C,\n" +
    "	 IDOSGD.TDTR_MOTIVO D,\n" +
    "	 (SELECT y.nu_ann,\n" +
    "			 y.nu_emi,\n" +
    "			 y.fe_finaliza,\n" +
    "			 IDOSGD.PK_SGD_DESCRIPCION_fu_dia_tra (CAST(now() as date),y.fe_finaliza) nu_dia_fal\n" +
    "	  FROM (SELECT x.nu_ann,\n" +
    "				   x.nu_emi,\n" +
    "				   CAST (x.fe_emi as date)+ IDOSGD.PK_SGD_DESCRIPCION_fu_dia_mas (CAST (x.fe_emi as date),nu_dia_ate)  AS fe_finaliza\n" +
    "			FROM IDOSGD.tdtv_remitos x) y) E\n" +
    " WHERE 1=1 ");
            
//            String pNUAnn = buscarDocumentoSeguiEstEmiBean.getCoAnnio();
//            if (!(buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
//                //sql.append(" A.NU_ANN = '").append(pNUAnn).append("'");
//                sql.append(" AND A.NU_ANN = :pNuAnn ");
//                // Parametros Basicos
//                objectParam.put("pNuAnn", pNUAnn);
//            }
            sql.append(" A.NU_ANN=B.NU_ANN ");
            sql.append(" AND A.NU_EMI=B.NU_EMI ");
            sql.append(" AND C.NU_EMI = B.NU_EMI ");
            sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7') ");
            sql.append(" AND A.ES_ELI = '0' ");
            sql.append(" AND B.ES_ELI = '0' ");
            sql.append(" AND b.co_mot = d.co_mot ");
            sql.append(" AND a.nu_ann = E.nu_ann ");
            sql.append(" AND a.nu_emi = E.nu_emi ");
            sql.append(" AND A.TI_EMI='01' ");

            // Parametros Basicos
            String pTipoBusqueda = buscarDocumentoSeguiEstEmiBean.getTipoBusqueda();
            if (pTipoBusqueda.equals("1") && buscarDocumentoSeguiEstEmiBean.isEsIncluyeFiltro()) {
                bBusqFiltro = true;
            }
            //Tipo de acceso personal o total
            String auxTipoAcceso = buscarDocumentoSeguiEstEmiBean.getTiAcceso();
            
            String tiAcceso = auxTipoAcceso != null && (auxTipoAcceso.equals("0") || auxTipoAcceso.equals("1") || auxTipoAcceso.equals("2")) ? auxTipoAcceso : "1";
            if (buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro() != null && buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro().trim().length() > 0 && tiAcceso.equals("0")) {
                //sql.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro()).append("'");
                sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoSeguiEstEmiBean.getCoEmpElaboro());
            } else {
                if (tiAcceso.equals("1")) {//PERSONAL ,'0' TOTAL
                    //sql.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpleado()).append("'");
                    sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                    objectParam.put("pcoEmpRes", buscarDocumentoSeguiEstEmiBean.getCoEmpleado());
                } else if (tiAcceso.equals("2")) {//TOTAL
                    //sql.append(" AND (A.CO_EMP_RES = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpleado()).append("' OR A.CO_EMP_EMI = '").append(buscarDocumentoSeguiEstEmiBean.getCoEmpleado()).append("') ");
                    sql.append(" AND (A.CO_EMP_RES = :pcoEmpRes OR A.CO_EMP_EMI = :pcoEmpRes) ");
                    objectParam.put("pcoEmpRes", buscarDocumentoSeguiEstEmiBean.getCoEmpleado());
                }
            }
            //Filtro
            if (pTipoBusqueda.equals("0") || bBusqFiltro) {
                if (buscarDocumentoSeguiEstEmiBean.getEstadoDoc() != null && buscarDocumentoSeguiEstEmiBean.getEstadoDoc().trim().length() > 0) {
                    //sql.append(" AND B.ES_DOC_REC = '").append(buscarDocumentoSeguiEstEmiBean.getEstadoDoc()).append("'");
                    sql.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                    objectParam.put("pEsDocRec", buscarDocumentoSeguiEstEmiBean.getEstadoDoc());
                }
                if (buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm() != null && buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm().trim().length() > 0) {
                    //sql.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm()).append("'");
                    sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                    objectParam.put("pCoDocEmi", buscarDocumentoSeguiEstEmiBean.getCoTipoDocAdm());
                }
                if (buscarDocumentoSeguiEstEmiBean.getCoRefOrigen() != null && buscarDocumentoSeguiEstEmiBean.getCoRefOrigen().trim().length() > 0) {
                    //sql.append(" AND CHARINDEX('").append(buscarDocumentoSeguiEstEmiBean.getCoRefOrigen()).append("', C.TI_EMI_REF) > 0 ");
                    sql.append(" AND STRPOS(C.TI_EMI_REF,:pTiEmiRef) > 0 ");
                    objectParam.put("pTiEmiRef", buscarDocumentoSeguiEstEmiBean.getCoRefOrigen());
                }
                if (buscarDocumentoSeguiEstEmiBean.getCoDepOrigen() != null && buscarDocumentoSeguiEstEmiBean.getCoDepOrigen().trim().length() > 0) {
                    //sql.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoSeguiEstEmiBean.getCoDepOrigen()).append("'");
                    sql.append(" AND A.CO_DEP_EMI = :pCoDepOrigen ");
                    objectParam.put("pCoDepOrigen", buscarDocumentoSeguiEstEmiBean.getCoDepOrigen());
                }
                if (buscarDocumentoSeguiEstEmiBean.getCoDepDestino() != null && buscarDocumentoSeguiEstEmiBean.getCoDepDestino().trim().length() > 0) {
                    //sql.append(" AND CHARINDEX('").append(buscarDocumentoSeguiEstEmiBean.getCoDepDestino()).append("', C.TI_EMI_DES) > 0 ");
                    sql.append(" AND STRPOS(C.TI_EMI_DES,:pTiEmpPro) > 0 ");
                    objectParam.put("pTiEmpPro", buscarDocumentoSeguiEstEmiBean.getCoDepDestino());
                }

                if (buscarDocumentoSeguiEstEmiBean.getCoVencimiento() != null && buscarDocumentoSeguiEstEmiBean.getCoVencimiento().trim().length() > 0) {

                    if ("1".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                        sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) <= 2");
                        sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) > 0");
                        sql.append(" AND b.fe_ate_doc IS NULL");
                        sql.append(" AND b.fe_arc_doc IS NULL");
                        sql.append(" AND d.in_dia = '0'");
                    }
                    if ("2".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                        sql.append(" AND CAST(now() as date) > CAST(E.fe_finaliza as date )");
                        sql.append(" AND b.fe_ate_doc IS NULL");
                        sql.append(" AND b.fe_arc_doc IS NULL");
                        sql.append(" AND d.in_dia = '0'");
                        sql.append(" AND A.NU_DIA_ATE <> 0");
                    }
                    if ("3".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                        //vence hoy
                        sql.append(" AND CAST(now() as date) = CAST(E.fe_finaliza as date )");
                        sql.append(" AND b.fe_ate_doc IS NULL");
                        sql.append(" AND b.fe_arc_doc IS NULL");
                        sql.append(" AND d.in_dia = '0'");
                        sql.append(" AND A.NU_DIA_ATE <> 0");
                    }
                    if ("4".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                        sql.append(" AND (CAST(E.fe_finaliza as date ) - CAST(now() as date)) > 2");
                        sql.append(" AND b.fe_ate_doc IS NULL");
                        sql.append(" AND b.fe_arc_doc IS NULL");
                        sql.append(" AND d.in_dia = '0'");
                    }
                    if ("5".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                        sql.append(" AND b.fe_ate_doc is not null");
                        sql.append(" AND b.fe_rec_doc is not null");
                    }
                    if ("0".equals(buscarDocumentoSeguiEstEmiBean.getCoVencimiento())) {
                        sql.append(" and a.nu_dia_ate = 0");
                        sql.append(" and D.in_dia = '0'");
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
                if (buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha() != null
                        && (buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("1") || buscarDocumentoSeguiEstEmiBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = buscarDocumentoSeguiEstEmiBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoSeguiEstEmiBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                            && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                        //sql.append(" AND CAST(A.FE_EMI AS DATE) between CAST('").append(vFeEmiIni).append("' AS DATE) AND CAST('").append(vFeEmiFin).append("' AS DATE) ");                    
                        sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59' ");
                        objectParam.put("pFeEmiIni", vFeEmiIni);
                        objectParam.put("pFeEmiFin", vFeEmiFin);
                    }
                }
            }
            
            
            if (pTipoBusqueda.equals("1")) {
                if (!bBusqFiltro) {
                    if (buscarDocumentoSeguiEstEmiBean.getCoDependencia() != null && buscarDocumentoSeguiEstEmiBean.getCoDependencia().trim().length() > 0) {
                        sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                        objectParam.put("pcoDepDes", buscarDocumentoSeguiEstEmiBean.getCoDependencia());
                    }
                }
                if (buscarDocumentoSeguiEstEmiBean.getBusNumDoc() != null && buscarDocumentoSeguiEstEmiBean.getBusNumDoc().trim().length() > 1) {
                    sql.append(" AND A.NU_DOC_EMI LIKE '%'||:pnuDocEmi||'%' ");
                    objectParam.put("pnuDocEmi", buscarDocumentoSeguiEstEmiBean.getBusNumDoc());
                }

                if (buscarDocumentoSeguiEstEmiBean.getBusNumExpediente() != null && buscarDocumentoSeguiEstEmiBean.getBusNumExpediente().trim().length() > 1) {
                    sql.append(" AND C.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                    objectParam.put("pnuExpediente", buscarDocumentoSeguiEstEmiBean.getBusNumExpediente());
                }

                // Busqueda del Asunto
                if (buscarDocumentoSeguiEstEmiBean.getBusAsunto() != null && buscarDocumentoSeguiEstEmiBean.getBusAsunto().trim().length() > 1) {
                    //sql.append(" AND CONTAINS(in_busca_texto, '" + BusquedaTextual.getContextValue(buscarDocumentoSeguiEstEmiBean.getBusAsunto()) + "', 1 ) > 1 ");
    //               sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoSeguiEstEmiBean.getBusAsunto())+"', 1 ) > 1 ");                
                   sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
                   objectParam.put("pDeAsunto", buscarDocumentoSeguiEstEmiBean.getBusAsunto());                
                }
                if (buscarDocumentoSeguiEstEmiBean.getBusNumDocRef() != null && buscarDocumentoSeguiEstEmiBean.getBusNumDocRef().trim().length() > 1) {
                    //busqeda referencia
                     sql.append("  AND A.NU_EMI IN( ");
                    sql.append("   SELECT D.NU_EMI ");
                    sql.append("   FROM IDOSGD.TDTR_ARBOL_SEG D ");
                    sql.append("   INNER JOIN IDOSGD.TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                    sql.append("   WHERE R.NU_ANN='"+buscarDocumentoSeguiEstEmiBean.getBusCoAnnio()+"'   ");
                    if (buscarDocumentoSeguiEstEmiBean.getBusCodTipoDocRef() != null && buscarDocumentoSeguiEstEmiBean.getBusCodTipoDocRef().trim().length() > 1) {
                    sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoSeguiEstEmiBean.getBusCodTipoDocRef()+"' ");
                    }
                    if (buscarDocumentoSeguiEstEmiBean.getBusCodDepEmiRef() != null && buscarDocumentoSeguiEstEmiBean.getBusCodDepEmiRef().trim().length() > 1) {
                    sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoSeguiEstEmiBean.getBusCodDepEmiRef()+"' ");
                    }
                    sql.append("   AND ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoSeguiEstEmiBean.getBusNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoSeguiEstEmiBean.getBusNumDocRef()+"'||'%'))");
                    sql.append(" )");
                }
            }
            
            sql.append(" ORDER BY A.FE_EMI DESC");
            sql.append(" ");
            //vResult = "0" + sql.toString().replace("+", "%2B").replace("-", "%2D").replace("<", "%3C").replace(">", "%3E");
            /////vResult = "0" + sql.toString();
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoSeguiEstEmitidoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception ex) {
            //vResult = "1" + ex.getMessage();
            list = null;
            ex.printStackTrace();
        }
        
        return list;
    }
}
