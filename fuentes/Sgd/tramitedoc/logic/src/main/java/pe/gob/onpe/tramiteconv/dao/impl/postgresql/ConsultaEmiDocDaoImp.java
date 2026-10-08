/*
 * To change this template, choose Tools | Templates
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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaEmiDocDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author ECueva
 */
@Repository("consultaEmiDocDao")
public class ConsultaEmiDocDaoImp extends SimpleJdbcDaoBase implements ConsultaEmiDocDao{

    @Override
    public List<DocumentoEmiConsulBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoEmiConsulBean> list = new ArrayList<DocumentoEmiConsulBean>();

        sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(X.NU_ANN, X.NU_EMI) DE_EMI_REF,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" CASE WHEN X.NU_CANDES=1 THEN IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(X.NU_ANN, X.NU_EMI)\n"
                + "ELSE IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(X.NU_ANN, X.NU_EMI)\n"
                + "END AS DE_EMP_PRO,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI ");
        sql.append(" FROM ( ");
        sql.append(" SELECT ");
        sql.append(" A.NU_COR_EMI,");
        sql.append(" TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" B.NU_DOC,UPPER(A.DE_ASU) DE_ASU_M,A.NU_ANN,B.NU_EXPEDIENTE,A.NU_EMI,A.TI_CAP,B.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append("CASE\n"
                + "WHEN COALESCE(B.TI_EMI_REF,'0')||COALESCE(B.IN_EXISTE_ANEXO,'2')='00' THEN 0\n"
                + "WHEN COALESCE(B.TI_EMI_REF,'0')||COALESCE(B.IN_EXISTE_ANEXO,'2')='02' THEN 0\n"
                + "ELSE 1  END AS EXISTE_ANEXO,");
        sql.append("COALESCE(B.CO_PRIORIDAD,'1') CO_PRIORIDAD,");
        sql.append(" A.ES_DOC_EMI,A.NU_DIA_ATE,A.CO_TIP_DOC_ADM,A.NU_CANDES,A.CO_EMP_RES, ");
        sql.append(" CASE A.TI_ENV_MSJ WHEN '0' THEN 'MESA DE PARTES' WHEN '1' THEN 'ENVÍO DIRECTO' WHEN '2' THEN 'MESA VIRTUAL' END    recursoenvio");//interoperabilidad
        sql.append(" FROM IDOSGD.TDTV_REMITOS A,IDOSGD.TDTX_REMITOS_RESUMEN B");
        sql.append(" WHERE");
        sql.append(" B.NU_ANN=A.NU_ANN");
        sql.append(" AND B.NU_EMI=A.NU_EMI");
//        String pNUAnn = buscarDocumentoEmiConsulBean.getCoAnnio();
//        if (!(buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN = :pNuAnn");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNUAnn);
//        }
        sql.append(" AND A.TI_EMI='01'");
        sql.append(" AND B.TI_EMI='01'");
        sql.append(" AND A.CO_GRU = '1'");
        sql.append(" AND A.ES_ELI = '0'");
        //sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi");

        // Parametros Basicos
        //objectParam.put("pCoDepEmi", buscarDocumentoEmiConsulBean.getCoDependencia());

        String pTipoBusqueda = buscarDocumentoEmiConsulBean.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoEmiConsulBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        String auxTipoAcceso=buscarDocumentoEmiConsulBean.getTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1")||auxTipoAcceso.equals("2"))?auxTipoAcceso:"1";
        if (buscarDocumentoEmiConsulBean.getCoEmpElaboro()!=null&&buscarDocumentoEmiConsulBean.getCoEmpElaboro().trim().length()>0&&(pTipoBusqueda.equals("0")||bBusqFiltro)&&tiAcceso.equals("0")){
            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpElaboro());
        } else {
            if (tiAcceso.equals("1")) {//PERSONAL ,'0' TOTAL
                sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpleado());
            } else if (tiAcceso.equals("2")) {//TOTAL
                sql.append(" AND (A.CO_EMP_RES = :pcoEmpRes OR A.CO_EMP_EMI = :pcoEmpRes) ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpleado());
            }
        }

        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (buscarDocumentoEmiConsulBean.getTipoDoc() != null && buscarDocumentoEmiConsulBean.getTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoEmiConsulBean.getTipoDoc());
            }
            if (buscarDocumentoEmiConsulBean.getEstadoDoc() != null && buscarDocumentoEmiConsulBean.getEstadoDoc().trim().length() > 0) {
                sql.append(" AND A.ES_DOC_EMI = :pEsDocEmi ");
                objectParam.put("pEsDocEmi", buscarDocumentoEmiConsulBean.getEstadoDoc());
            }
            if (buscarDocumentoEmiConsulBean.getPrioridadDoc() != null && buscarDocumentoEmiConsulBean.getPrioridadDoc().trim().length() > 0) {
                sql.append(" AND B.CO_PRIORIDAD = :pCoPrioridad ");
                objectParam.put("pCoPrioridad", buscarDocumentoEmiConsulBean.getPrioridadDoc());
            }
            if (buscarDocumentoEmiConsulBean.getCoRefOrigen() != null && buscarDocumentoEmiConsulBean.getCoRefOrigen().trim().length() > 0) {
                sql.append(" AND STRPOS(B.TI_EMI_REF, :pTiEmiRef) > 0 ");
                objectParam.put("pTiEmiRef", buscarDocumentoEmiConsulBean.getCoRefOrigen());
            }            
            if (buscarDocumentoEmiConsulBean.getCoDepOrigen() != null && buscarDocumentoEmiConsulBean.getCoDepOrigen().trim().length() > 0) {
                sql.append(" AND A.CO_DEP_EMI = :pCoDepOrigen ");
                objectParam.put("pCoDepOrigen", buscarDocumentoEmiConsulBean.getCoDepOrigen());
            }
            if (buscarDocumentoEmiConsulBean.getCoDepDestino() != null && buscarDocumentoEmiConsulBean.getCoDepDestino().trim().length() > 0) {
                sql.append(" AND STRPOS(B.TI_EMI_DES, :pTiEmpPro) > 0 ");
                objectParam.put("pTiEmpPro", buscarDocumentoEmiConsulBean.getCoDepDestino());
            }
            if (buscarDocumentoEmiConsulBean.getEsFiltroFecha() != null
                    && (buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("2") || buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoEmiConsulBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoEmiConsulBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between to_timestamp(:pFeEmiIni,'dd/mm/yyyy') AND to_timestamp(:pFeEmiFin,'dd/mm/yyyy hh24:mi:ss')");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin+ " 23:59:59");
                }
            }

        }

        //Busqueda
        if (pTipoBusqueda.equals("1")) {
            if(!bBusqFiltro){
                if (buscarDocumentoEmiConsulBean.getCoDependencia() != null && buscarDocumentoEmiConsulBean.getCoDependencia().trim().length() > 0) {
                    sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi ");
                    objectParam.put("pCoDepEmi", buscarDocumentoEmiConsulBean.getCoDependencia());
                }                
            }            
            if (buscarDocumentoEmiConsulBean.getBusNumEmision() != null && buscarDocumentoEmiConsulBean.getBusNumEmision().trim().length() > 0) {
                sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", Integer.parseInt(buscarDocumentoEmiConsulBean.getBusNumEmision()));
            }

            if (buscarDocumentoEmiConsulBean.getBusNumDoc()!= null && buscarDocumentoEmiConsulBean.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE ''||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoEmiConsulBean.getBusNumDoc());
            }

            if (buscarDocumentoEmiConsulBean.getBusNumExpediente() != null && buscarDocumentoEmiConsulBean.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoEmiConsulBean.getBusNumExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoEmiConsulBean.getBusAsunto()!= null && buscarDocumentoEmiConsulBean.getBusAsunto().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoEmiConsulBean.getBusAsunto())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
                objectParam.put("pDeAsunto", buscarDocumentoEmiConsulBean.getBusAsunto());
//                sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoEmiConsulBean.getBusAsunto())+"', 1 ) > 1 ");                
            }
            if (buscarDocumentoEmiConsulBean.getBusNumDocRef() != null && buscarDocumentoEmiConsulBean.getBusNumDocRef().trim().length() > 1) {             
                //busqeda referencia
                 sql.append("  AND A.NU_EMI IN( ");
                sql.append("   SELECT D.NU_EMI ");
                sql.append("   FROM TDTR_ARBOL_SEG D ");
                sql.append("   INNER JOIN TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                sql.append("   WHERE R.NU_ANN='"+buscarDocumentoEmiConsulBean.getBusCoAnnio()+"'   ");
                if (buscarDocumentoEmiConsulBean.getBusCodTipoDocRef() != null && buscarDocumentoEmiConsulBean.getBusCodTipoDocRef().trim().length() > 1) {
                    sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoEmiConsulBean.getBusCodTipoDocRef()+"' ");
                }
                if (buscarDocumentoEmiConsulBean.getBusCodDepEmiRef() != null && buscarDocumentoEmiConsulBean.getBusCodDepEmiRef().trim().length() > 1) {
                    sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoEmiConsulBean.getBusCodDepEmiRef()+"' ");
                }
                sql.append("   AND ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoEmiConsulBean.getBusNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoEmiConsulBean.getBusNumDocRef()+"'||'%'))");
                sql.append(" )");
            }
        }
        sql.append(" ORDER BY A.NU_COR_EMI DESC");
        sql.append(") X ");
//        sql.append("WHERE ROWNUM < 301");
        sql.append(" LIMIT 300");


        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiConsulBean.class));

        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public DocumentoEmiConsulBean getDocumentoEmiAdm(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT TO_CHAR((SELECT FE_EXP FROM IDOSGD.TDTC_EXPEDIENTE WHERE NU_ANN_EXP=A.NU_ANN_EXP AND NU_SEC_EXP=A.NU_SEC_EXP),'DD/MM/YYYY') FE_EXP_CORTA,\n" +
                    "A.NU_ANN,A.NU_EMI,A.NU_COR_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_EMI) DE_DEP_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(A.CO_LOC_EMI) DE_LOC_EMI,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(A.CO_EMP_EMI) DE_EMP_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_RES,IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
                    "B.NU_DOC,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,A.NU_DIA_ATE,A.DE_ASU,B.IN_EXISTE_DOC EXISTE_DOC,B.IN_EXISTE_ANEXO EXISTE_ANEXO,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(B.CO_PROCESO_EXP) DE_PROCESO_EXP,IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE(A.NU_ANN_EXP,A.NU_SEC_EXP) NU_EXPEDIENTE\n" +
                    "from IDOSGD.TDTV_REMITOS A,IDOSGD.TDTX_REMITOS_RESUMEN B\n" +
                    "where A.NU_ANN=?\n" +
                    "AND A.NU_EMI=?\n" +
                    "AND B.NU_ANN=A.NU_ANN\n" +
                    "AND B.NU_EMI=A.NU_EMI");

        DocumentoEmiConsulBean documentoEmiConsulBean = new DocumentoEmiConsulBean();
        try {
            documentoEmiConsulBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiConsulBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiConsulBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiConsulBean;
    }
    
    @Override
    public String getTipoDestinatarioEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        String result = "NO_OK";

        sql.append("SELECT MAX(ti_des)\n"
                + "FROM IDOSGD.tdtv_destinos\n"
                + "WHERE nu_emi = ?\n"
                + " AND nu_ann = ?"
                + " AND ES_ELI = '0'");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuEmi, pnuAnn});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }    
    
    @Override
    public List<DestinatarioDocumentoEmiConsulBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiConsulBean> list = new ArrayList<DestinatarioDocumentoEmiConsulBean>();
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,\n"
//                + "COALESCE2(a.co_loc_des,substr(PK_SGD_DESCRIPCION_DE_LOCAL(a.co_loc_des), 1, 100),NULL) de_local,\n"
                + " CASE WHEN a.co_loc_des IS NOT NULL\n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(a.co_loc_des), 1, 100) \n"
                + " ELSE NULL END AS de_local,\n"
                + "a.co_dep_des co_dependencia,\n"
//                + "COALESCE2(a.co_dep_des,substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_des), 1, 100),NULL) de_dependencia,\n"
                + " CASE WHEN a.co_dep_des IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_des), 1, 100) \n"
                + " ELSE NULL END AS de_dependencia,\n"
                + "a.co_emp_des co_empleado,\n"
//                + " COALESCE2(a.co_emp_des,substr(PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des), 1, 100),NULL) de_empleado,\n"
                + " CASE WHEN a.co_emp_des IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des), 1, 100) \n"
                + " ELSE NULL END AS de_empleado,\n"
                + "a.co_mot co_tramite,\n"
//                + "COALESCE2(a.co_mot,substr(PK_SGD_DESCRIPCION_MOTIVO(a.co_mot), 1, 100),NULL) de_tramite,\n"
                + " CASE WHEN a.co_mot IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(a.co_mot), 1, 100) \n"
                + " ELSE NULL END AS de_tramite,\n"
                + "a.co_pri co_prioridad,IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD(a.co_pri) de_prioridad,\n"
                + "a.de_pro de_indicaciones,\n"
                + "a.NU_RUC_DES nu_ruc,\n"
//                + "COALESCE2(a.NU_RUC_DES,substr(TD_PK_DESCRIPCION.DE_PROVEEDOR(a.NU_RUC_DES), 1, 100),NULL) de_proveedor,\n"
                + " CASE WHEN a.NU_RUC_DES IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.NU_RUC_DES), 1, 100) \n"
                + " ELSE NULL END AS de_proveedor,\n"
                + " a.NU_DNI_DES nu_dni,\n"
//                + " COALESCE2(a.NU_DNI_DES,substr(TD_PK_DESCRIPCION.ANI_SIMIL(a.NU_DNI_DES), 1, 100),NULL) de_ciudadano,\n"
                + " CASE WHEN a.NU_DNI_DES IS NOT NULL\n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.NU_DNI_DES), 1, 100) \n"
                + " ELSE NULL END AS de_ciudadano,\n"
                + "a.CO_OTR_ORI_DES co_otro_origen,\n"
//                + "COALESCE2(\n"
//                + "a.CO_OTR_ORI_DES,\n"
//                + "(SELECT C.DE_APE_PAT_OTR||' '||C.DE_APE_MAT_OTR||', '||C.DE_NOM_OTR || ' - ' ||\n"
//                + "     C.DE_RAZ_SOC_OTR ||'##'||\n"
//                + "     COALESCE(B.CELE_DESELE,'   ') ||'##'||\n"
//                + "     C.NU_DOC_OTR_ORI  \n"
//                + "  FROM IDOSGD.TDTR_OTRO_ORIGEN C, (\n"
//                + "  SELECT CELE_CODELE, CELE_DESELE\n"
//                + "    FROM IDOSGD.SI_ELEMENTO\n"
//                + "   WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B\n"
//                + "WHERE C.CO_TIP_OTR_ORI = B.CELE_CODELE(+)\n"
//                + "AND C.CO_OTR_ORI = a.CO_OTR_ORI_DES\n"
//                + "),\n"
//                + "NULL\n"
//                + ") de_otro_origen_full,\n"
                + " CASE WHEN a.CO_OTR_ORI_DES IS NOT NULL \n"
                + " THEN \n"
                + "(SELECT C.DE_APE_PAT_OTR||' '||C.DE_APE_MAT_OTR||', '||C.DE_NOM_OTR || ' - ' ||\n"
                + "     C.DE_RAZ_SOC_OTR ||'##'||\n"
                + "     COALESCE(B.CELE_DESELE,'   ') ||'##'||\n"
                + "     C.NU_DOC_OTR_ORI  \n"
                + "  FROM IDOSGD.TDTR_OTRO_ORIGEN C LEFT OUTER JOIN (\n"
                + "  SELECT CELE_CODELE, CELE_DESELE\n"
                + "    FROM IDOSGD.SI_ELEMENTO\n"
                + "   WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B\n"
//                + " WHERE C.CO_TIP_OTR_ORI = B.CELE_CODELE(+)\n"
                + " ON C.CO_TIP_OTR_ORI = B.CELE_CODELE\n"
                + " WHERE C.CO_OTR_ORI = a.CO_OTR_ORI_DES\n"
                + " )\n"
                + " ELSE NULL END AS de_otro_origen_full, \n"
                + "a.ti_des co_tipo_destino\n"
                + "FROM IDOSGD.tdtv_destinos a\n"
                + "where nu_ann = ? and nu_emi = ?\n"
                + "AND ES_ELI='0' AND NU_EMI_REF is null\n"
                + "order by 3");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiConsulBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<ReferenciaConsulBean> list = new ArrayList<ReferenciaConsulBean>();
        sql.append("SELECT \n"
                + "IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tipo_doc, \n"                
                + "CASE  WHEN a.ti_emi='01' THEN\n"
                + "COALESCE(a.nu_doc_emi,' ') || '-' || a.nu_ann || '-' || COALESCE(a.de_doc_sig,' ')\n"
                + "WHEN a.ti_emi='05' THEN\n"
                + "COALESCE(a.nu_doc_emi,' ') || '-' || a.nu_ann || '-' || COALESCE(a.de_doc_sig,' ')\n"
                + " ELSE a.de_doc_sig\n"
                + " END AS li_nu_doc,"
//                + "DECODE (a.ti_emi,'01', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,\n"
//                + "         '05', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,a.de_doc_sig) li_nu_doc,\n"
                + "substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_emi), 1, 100) de_dep_emi, \n"
                + "TO_CHAR(a.fe_emi,'DD/MM/YY') fe_emi_corta, \n"
                + "b.nu_ann,\n"
                + "b.nu_emi,\n"
//                + "COALESCE(trim(to_char(b.nu_des)),'N') nu_des ,\n"
                 + "COALESCE(trim(CAST(b.nu_des AS text)),'N') nu_des ,\n"
                + "b.nu_ann_ref,\n"
                + "b.nu_emi_ref,\n"
//                + "COALESCE(trim(to_char(b.nu_des_ref)),'N') nu_des_ref,\n"
                + "COALESCE(trim(CAST(b.nu_des_ref AS text)),'N') nu_des_ref,\n"
                + "b.co_ref,\n"
//                + "DECODE(COALESCE(trim(to_char(b.nu_des_ref)),'N'),'N','emi','rec') tip_doc_ref,\n"
                + "CASE  WHEN COALESCE(trim(cast(b.nu_des_ref AS text)),'N')='N' THEN 'emi' \n"
                + "ELSE 'rec' END AS tip_doc_ref,\n"
                + "a.co_tip_doc_adm,'BD' accion_bd\n"
                + "FROM IDOSGD.tdtv_remitos a,IDOSGD.TDTR_REFERENCIA b \n"
                + "WHERE a.nu_ann = b.nu_ann_ref\n"
                + "AND a.nu_emi = b.nu_emi_ref\n"
                + "and b.nu_ann = ?\n"
                + "and b.nu_emi = ?");
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaConsulBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }    
    
    @Override
    public DocumentoEmiConsulBean existeDocumentoReferenciado(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean){
        StringBuffer sql = new StringBuffer();
        DocumentoEmiConsulBean documentoEmiConsulBean = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_DOC FROM IDOSGD.TDTV_REMITOS A\n" +
                    "WHERE A.NU_ANN=?\n" +
                    "AND A.CO_DEP_EMI=?\n" +
                    "AND A.TI_EMI='01'\n" +
                    "AND A.CO_TIP_DOC_ADM=?\n" +
                    "AND A.NU_DOC_EMI=?\n" +
                    "AND A.ES_ELI='0'\n" +
                    "AND A.ES_DOC_EMI NOT IN ('5','7','9')");        
        
        try {
             documentoEmiConsulBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiConsulBean.class),
                    new Object[]{buscarDocumentoEmiConsulBean.getBusCoAnnio(),buscarDocumentoEmiConsulBean.getBusCodDepEmiRef(),buscarDocumentoEmiConsulBean.getBusCodTipoDocRef(),
                    buscarDocumentoEmiConsulBean.getBusNumDocRef()});
             documentoEmiConsulBean.setMsjResult("OK");
        } catch (EmptyResultDataAccessException e) {
            documentoEmiConsulBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiConsulBean;          
    }
    
    @Override
    public List<DocumentoEmiConsulBean> getDocumentosReferenciadoBusq(DocumentoEmiConsulBean documentoEmiConsulBean,String sTipoAcceso){       
        StringBuffer sql = new StringBuffer(); 
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List<DocumentoEmiConsulBean> list = new ArrayList<DocumentoEmiConsulBean>();        
        
        sql.append("SELECT A.* ");
        sql.append(" FROM ( ");        
        sql.append(" SELECT ");        
        sql.append(" DISTINCT B.NU_COR_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(B.NU_ANN, B.NU_EMI) DE_EMI_REF,B.FE_EMI,");
        sql.append(" TO_CHAR(B.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,");
        sql.append(" (SELECT CDOC_DESDOC FROM IDOSGD.SI_MAE_TIPO_DOC WHERE CDOC_TIPDOC = B.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" CASE WHEN B.NU_CANDES=1 THEN IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(B.NU_ANN, B.NU_EMI) ELSE IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(B.NU_ANN, B.NU_EMI) END AS DE_EMP_PRO,");
        sql.append(" (SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM IDOSGD.RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = B.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" (SELECT DE_EST FROM IDOSGD.TDTR_ESTADOS WHERE CO_EST || DE_TAB = B.ES_DOC_EMI || 'TDTV_REMITOS') DE_ES_DOC_EMI,");
        sql.append(" C.NU_DOC,UPPER(B.DE_ASU) DE_ASU_M,B.NU_ANN,C.NU_EXPEDIENTE,B.NU_EMI,B.TI_CAP,C.IN_EXISTE_DOC EXISTE_DOC, "
                + " CASE WHEN (COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2'))='00' THEN 0 "
                + " WHEN (COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2'))='02' THEN 0 ELSE 1 END AS EXISTE_ANEXO, COALESCE(C.CO_PRIORIDAD,'1') CO_PRIORIDAD,");
        sql.append(" B.ES_DOC_EMI");
        sql.append(" FROM ( ");
        sql.append("WITH RECURSIVE q AS ( \n" +
            "    SELECT NU_ANN , NU_EMI \n" +
            "      FROM IDOSGD.TDTR_ARBOL_SEG  po \n" +
            "     WHERE po.PK_REF = :pCoAnio||:pNuEmi||'0' \n" +
            "    UNION ALL\n" +
            "    SELECT po.NU_ANN , po.NU_EMI \n" +
            "      FROM IDOSGD.TDTR_ARBOL_SEG  po \n" +
            "      JOIN IDOSGD.TDTR_ARBOL_SEG q ON q.PK_EMI=po.PK_REF \n" +
            ") \n" +
            "SELECT * FROM q \n" +
            ") ");
         sql.append("A , IDOSGD.TDTV_REMITOS B, IDOSGD.TDTX_REMITOS_RESUMEN C ");
//        sql.append("  SELECT NU_ANN, NU_EMI ");
//        sql.append("  FROM IDOSGD.TDTR_ARBOL_SEG ");
//        sql.append("	 START WITH PK_REF = :pCoAnio||:pNuEmi||'0' ");
//        sql.append("	CONNECT BY PRIOR PK_EMI = PK_REF");
//        sql.append(" ) A , IDOSGD.TDTV_REMITOS B, IDOSGD.TDTX_REMITOS_RESUMEN C");
        sql.append(" WHERE A.NU_ANN = B.NU_ANN");
        sql.append(" AND A.NU_EMI = B.NU_EMI");
        sql.append(" AND C.NU_ANN = B.NU_ANN");
        sql.append(" AND C.NU_EMI = B.NU_EMI");
        sql.append(" AND B.TI_EMI='01'");
        sql.append(" AND C.TI_EMI='01'");
        sql.append(" AND B.ES_ELI = '0'");
        sql.append(" AND B.CO_DEP_EMI = :pCoDepEmi");        
        if(sTipoAcceso.equals("1")){
            sql.append(" AND B.CO_EMP_RES = :pcoEmpRes");      
            objectParam.put("pcoEmpRes", documentoEmiConsulBean.getCoEmpRes());           
        }
        objectParam.put("pCoAnio", documentoEmiConsulBean.getNuAnn());           
        objectParam.put("pNuEmi", documentoEmiConsulBean.getNuEmi());   
        objectParam.put("pCoDepEmi", documentoEmiConsulBean.getCoDepEmi());   
        
        sql.append(" ORDER BY B.NU_COR_EMI DESC");
        sql.append(") A ");
        sql.append("LIMIT 300");            
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiConsulBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;          
    }
        
    @Override
    public String getRutaReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean) {
        String vResult;
        StringBuffer prutaReporte =  new StringBuffer();
        prutaReporte.append(" B.NU_ANN=A.NU_ANN");
        prutaReporte.append(" AND B.NU_EMI=A.NU_EMI");
        try {
            String pNUAnn = buscarDocumentoEmiConsulBean.getCoAnnio();
            if (!(buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
                // Parametros Basicos
                prutaReporte.append(" AND A.NU_ANN = '").append(pNUAnn).append("'");
            }
            prutaReporte.append(" AND A.TI_EMI='01'");
            prutaReporte.append(" AND B.TI_EMI='01'");
            prutaReporte.append(" AND A.ES_ELI = '0'");
            // Parametros Basicos
            //prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoEmiConsulBean.getCoDependencia()).append("'");

            String pTipoBusqueda = buscarDocumentoEmiConsulBean.getTipoBusqueda();                
            
            if (buscarDocumentoEmiConsulBean.getCoEmpElaboro() != null && buscarDocumentoEmiConsulBean.getCoEmpElaboro().trim().length() > 0 && pTipoBusqueda.equals("0")) {
                prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpElaboro()).append("'");
            } else {
                if (buscarDocumentoEmiConsulBean.getTiAcceso().equals("1")) {
                    prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("'");
                } else if (buscarDocumentoEmiConsulBean.getTiAcceso().equals("2")) {
                    prutaReporte.append(" AND (A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("' OR A.CO_EMP_EMI = '")
                    .append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("')");
                }
            }          
            
            if (pTipoBusqueda.equals("0")) {
                if (buscarDocumentoEmiConsulBean.getTipoDoc() != null && buscarDocumentoEmiConsulBean.getTipoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoEmiConsulBean.getTipoDoc()).append("'");
                }
                if (buscarDocumentoEmiConsulBean.getEstadoDoc() != null && buscarDocumentoEmiConsulBean.getEstadoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.ES_DOC_EMI = '").append(buscarDocumentoEmiConsulBean.getEstadoDoc()).append("'");
                }
                if (buscarDocumentoEmiConsulBean.getPrioridadDoc() != null && buscarDocumentoEmiConsulBean.getPrioridadDoc().trim().length() > 0) {
                    prutaReporte.append(" AND B.CO_PRIORIDAD = '").append(buscarDocumentoEmiConsulBean.getPrioridadDoc()).append("'");
                }
                if (buscarDocumentoEmiConsulBean.getCoRefOrigen() != null && buscarDocumentoEmiConsulBean.getCoRefOrigen().trim().length() > 0) {
                    prutaReporte.append(" AND STRPOS(B.TI_EMI_REF, '").append(buscarDocumentoEmiConsulBean.getCoRefOrigen()).append("') > 0 ");
                }                 
                if (buscarDocumentoEmiConsulBean.getCoDepOrigen() != null && buscarDocumentoEmiConsulBean.getCoDepOrigen().trim().length() > 0) {
                    prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoEmiConsulBean.getCoDepOrigen()).append("' ");
                }
                if (buscarDocumentoEmiConsulBean.getCoDepDestino() != null && buscarDocumentoEmiConsulBean.getCoDepDestino().trim().length() > 0) {
                    prutaReporte.append(" AND STRPOS(B.TI_EMI_DES, '").append(buscarDocumentoEmiConsulBean.getCoDepDestino()).append("') > 0 ");
                }
                if (buscarDocumentoEmiConsulBean.getEsFiltroFecha() != null
                        && (buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = buscarDocumentoEmiConsulBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoEmiConsulBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                            && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                          prutaReporte.append(" AND A.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy')").append(" AND TO_DATE('").append(vFeEmiFin)
                        .append("','dd/mm/yyyy') + TIME '23:59:59'");
                    }
                }

            }
            prutaReporte.append(" ");
            vResult = "0"+prutaReporte.toString();
        } catch (Exception ex) {
            vResult="1"+ex.getMessage();
            ex.printStackTrace();
        }
        return vResult;
    }
/*
    @Override
    public List<DocumentoEmiConsulBean> getRutaReporteLista(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean) {
        String vResult;
        StringBuffer prutaReporte =  new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        
        prutaReporte.append("SELECT	A.NU_COR_EMI, ");
        prutaReporte.append("CONVERT(VARCHAR(10), A.FE_EMI, 103) FE_EMI_CORTA, ");
        prutaReporte.append("(SELECT CDOC_DESDOC ");
        prutaReporte.append("FROM IDOSGD.SI_MAE_TIPO_DOC ");
        prutaReporte.append("WHERE CDOC_TIPDOC = A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        prutaReporte.append("B.NU_DOC, ");
        prutaReporte.append("CASE A.NU_CANDES ");
                prutaReporte.append("WHEN 1 THEN (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP](A.NU_ANN, A.NU_EMI)) ");
                prutaReporte.append("ELSE (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP_V](A.NU_ANN, A.NU_EMI)) ");
        prutaReporte.append("END AS DE_EMP_PRO, ");
        prutaReporte.append("A.DE_ASU, ");
        prutaReporte.append("(SELECT DE_EST ");
        prutaReporte.append("FROM IDOSGD.TDTR_ESTADOS ");
        prutaReporte.append("WHERE CO_EST + DE_TAB = A.ES_DOC_EMI + 'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        prutaReporte.append("A.NU_DIA_ATE, ");
        prutaReporte.append("(SELECT CEMP_APEPAT + ' ' + CEMP_APEMAT + ' ' + CEMP_DENOM ");
        prutaReporte.append("FROM IDOSGD.RHTM_PER_EMPLEADOS ");
        prutaReporte.append("WHERE CEMP_CODEMP = A.CO_EMP_RES) DE_EMP_RES, ");
        prutaReporte.append("B.NU_EXPEDIENTE ");
        prutaReporte.append("FROM IDOSGD.TDTV_REMITOS A, ");
        prutaReporte.append("IDOSGD.TDTX_REMITOS_RESUMEN B ");
        prutaReporte.append("WHERE");
        
        prutaReporte.append(" B.NU_ANN=A.NU_ANN");
        prutaReporte.append(" AND B.NU_EMI=A.NU_EMI");
        try {
            String pNUAnn = buscarDocumentoEmiConsulBean.getCoAnnio();
            if (!(buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
                // Parametros Basicos
                prutaReporte.append(" AND A.NU_ANN = '").append(pNUAnn).append("'");
            }
            prutaReporte.append(" AND A.TI_EMI='01'");
            prutaReporte.append(" AND B.TI_EMI='01'");
            prutaReporte.append(" AND A.ES_ELI = '0'");
            // Parametros Basicos
            //prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoEmiConsulBean.getCoDependencia()).append("'");

            String pTipoBusqueda = buscarDocumentoEmiConsulBean.getTipoBusqueda();                
            
            if (buscarDocumentoEmiConsulBean.getCoEmpElaboro() != null && buscarDocumentoEmiConsulBean.getCoEmpElaboro().trim().length() > 0 && pTipoBusqueda.equals("0")) {
                //prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpElaboro()).append("'");
                prutaReporte.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpElaboro());
            } else {
                if (buscarDocumentoEmiConsulBean.getTiAcceso().equals("1")) {
                    //prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("'");
                    prutaReporte.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                    objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpleado());
                } else if (buscarDocumentoEmiConsulBean.getTiAcceso().equals("2")) {
                    //prutaReporte.append(" AND (A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("' OR A.CO_EMP_EMI = '")
                    //.append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("')");
                    prutaReporte.append(" AND (A.CO_EMP_RES = :pcoEmpRes OR A.CO_EMP_EMI = :pcoEmpRes ");
                    objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpleado());
                }
            }          
            
            if (pTipoBusqueda.equals("0")) {
                if (buscarDocumentoEmiConsulBean.getTipoDoc() != null && buscarDocumentoEmiConsulBean.getTipoDoc().trim().length() > 0) {
                    //prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoEmiConsulBean.getTipoDoc()).append("'");
                    prutaReporte.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                    objectParam.put("pCoDocEmi", buscarDocumentoEmiConsulBean.getTipoDoc());
                }
                if (buscarDocumentoEmiConsulBean.getEstadoDoc() != null && buscarDocumentoEmiConsulBean.getEstadoDoc().trim().length() > 0) {
                    //prutaReporte.append(" AND A.ES_DOC_EMI = '").append(buscarDocumentoEmiConsulBean.getEstadoDoc()).append("'");
                    prutaReporte.append(" AND A.ES_DOC_EMI = :pEsDocEmi ");
                    objectParam.put("pEsDocEmi", buscarDocumentoEmiConsulBean.getEstadoDoc());
                }
                if (buscarDocumentoEmiConsulBean.getPrioridadDoc() != null && buscarDocumentoEmiConsulBean.getPrioridadDoc().trim().length() > 0) {
                    //prutaReporte.append(" AND B.CO_PRIORIDAD = '").append(buscarDocumentoEmiConsulBean.getPrioridadDoc()).append("'");
                    prutaReporte.append(" AND B.CO_PRIORIDAD = :pCoPrioridad ");
                    objectParam.put("pCoPrioridad", buscarDocumentoEmiConsulBean.getPrioridadDoc());
                }
                if (buscarDocumentoEmiConsulBean.getCoRefOrigen() != null && buscarDocumentoEmiConsulBean.getCoRefOrigen().trim().length() > 0) {
                    //prutaReporte.append(" AND CHARINDEX('").append(buscarDocumentoEmiConsulBean.getCoRefOrigen()).append("', B.TI_EMI_REF) > 0 ");
                    prutaReporte.append(" AND CHARINDEX(:pTiEmiRef, B.TI_EMI_REF) > 0 ");
                    objectParam.put("pTiEmiRef", buscarDocumentoEmiConsulBean.getCoRefOrigen());
                }                 
                if (buscarDocumentoEmiConsulBean.getCoDepOrigen() != null && buscarDocumentoEmiConsulBean.getCoDepOrigen().trim().length() > 0) {
                    //prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoEmiConsulBean.getCoDepOrigen()).append("' ");
                    prutaReporte.append(" AND A.CO_DEP_EMI = :pCoDepOrigen ");
                    objectParam.put("pCoDepOrigen", buscarDocumentoEmiConsulBean.getCoDepOrigen());
                }
                if (buscarDocumentoEmiConsulBean.getCoDepDestino() != null && buscarDocumentoEmiConsulBean.getCoDepDestino().trim().length() > 0) {
                    //prutaReporte.append(" AND CHARINDEX('").append(buscarDocumentoEmiConsulBean.getCoDepDestino()).append("', B.TI_EMI_DES) > 0 ");
                     prutaReporte.append(" AND CHARINDEX(B.TI_EMI_DES, :pTiEmpPro) > 0 ");
                     objectParam.put("pTiEmpPro", buscarDocumentoEmiConsulBean.getCoDepDestino());
                }
                if (buscarDocumentoEmiConsulBean.getEsFiltroFecha() != null
                        && (buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = buscarDocumentoEmiConsulBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoEmiConsulBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                            && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                        //prutaReporte.append(" AND CAST(A.FE_EMI AS DATE) between CAST('").append(vFeEmiIni).append("' AS DATE)").append(" AND CAST('").append(vFeEmiFin)
                        //.append("' AS DATE) ");
                        prutaReporte.append(" AND CAST(A.FE_EMI AS DATE) BETWEEN CAST(:pFeEmiIni AS DATE) AND CAST(:pFeEmiFin AS DATE) ");
                        objectParam.put("pFeEmiIni", vFeEmiIni);
                        objectParam.put("pFeEmiFin", vFeEmiFin);
                    }
                }

            }
            //prutaReporte.append(" ");
            
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiConsulBean.class));
            
            //vResult = "0"+prutaReporte.toString();
        } catch (Exception ex) {
            //vResult="1"+ex.getMessage();
            ex.printStackTrace();
        }
        //return vResult;
        return list;
    }
*/
    @Override
    public List<DocumentoEmiConsulBean> getListaReporteBusqueda(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean) {
        boolean bBusqFiltro = false;
        StringBuffer prutaReporte =  new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        
        prutaReporte.append("SELECT	A.NU_COR_EMI, ");
        prutaReporte.append("TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA, IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(B.NU_ANN, B.NU_EMI) DE_EMI_REF,");
        prutaReporte.append("(SELECT CDOC_DESDOC ");
        prutaReporte.append("FROM IDOSGD.SI_MAE_TIPO_DOC ");
        prutaReporte.append("WHERE CDOC_TIPDOC = A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        prutaReporte.append("B.NU_DOC, ");
        prutaReporte.append("CASE WHEN B.NU_CANDES=1 THEN IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(B.NU_ANN, B.NU_EMI) ELSE IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(B.NU_ANN, B.NU_EMI) END AS DE_EMP_PRO , ");
        prutaReporte.append("A.DE_ASU, ");
        prutaReporte.append("(SELECT DE_EST ");
        prutaReporte.append("FROM IDOSGD.TDTR_ESTADOS ");
        prutaReporte.append("WHERE CO_EST || DE_TAB = A.ES_DOC_EMI || 'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        prutaReporte.append("A.NU_DIA_ATE, ");
        prutaReporte.append("(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM ");
        prutaReporte.append("FROM IDOSGD.RHTM_PER_EMPLEADOS ");
        prutaReporte.append("WHERE CEMP_CODEMP = A.CO_EMP_RES) DE_EMP_RES, ");
        prutaReporte.append("B.NU_EXPEDIENTE ");
        prutaReporte.append("FROM IDOSGD.TDTV_REMITOS A, ");
        prutaReporte.append("IDOSGD.TDTX_REMITOS_RESUMEN B ");
        prutaReporte.append("WHERE");
        
        prutaReporte.append(" B.NU_ANN=A.NU_ANN");
        prutaReporte.append(" AND B.NU_EMI=A.NU_EMI");
        try {
//            String pNUAnn = buscarDocumentoEmiConsulBean.getCoAnnio();
//            if (!(buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
//                // Parametros Basicos
//                //prutaReporte.append(" AND A.NU_ANN = '").append(pNUAnn).append("'");
//                prutaReporte.append(" AND A.NU_ANN = :pnuAnn ");
//                objectParam.put("pnuAnn", pNUAnn);
//            }
            prutaReporte.append(" AND A.TI_EMI='01'");
            prutaReporte.append(" AND B.TI_EMI='01'");
            prutaReporte.append(" AND A.CO_GRU = '1'");
            prutaReporte.append(" AND A.ES_ELI = '0'");
            // Parametros Basicos
            //prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoEmiConsulBean.getCoDependencia()).append("'");

            String pTipoBusqueda = buscarDocumentoEmiConsulBean.getTipoBusqueda();                
            if (pTipoBusqueda.equals("1") && buscarDocumentoEmiConsulBean.isEsIncluyeFiltro()) {
                bBusqFiltro = true;
            }
            if (buscarDocumentoEmiConsulBean.getCoEmpElaboro() != null && buscarDocumentoEmiConsulBean.getCoEmpElaboro().trim().length() > 0 && pTipoBusqueda.equals("0")) {
                //prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpElaboro()).append("'");
                prutaReporte.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpElaboro());
            } else {
                if (buscarDocumentoEmiConsulBean.getTiAcceso().equals("1")) {
                    //prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("'");
                    prutaReporte.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                    objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpleado());
                } else if (buscarDocumentoEmiConsulBean.getTiAcceso().equals("2")) {
                    //prutaReporte.append(" AND (A.CO_EMP_RES = '").append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("' OR A.CO_EMP_EMI = '")
                    //.append(buscarDocumentoEmiConsulBean.getCoEmpleado()).append("')");
                    prutaReporte.append(" AND (A.CO_EMP_RES = :pcoEmpRes OR A.CO_EMP_EMI = :pcoEmpRes ");
                    objectParam.put("pcoEmpRes", buscarDocumentoEmiConsulBean.getCoEmpleado());
                }
            }          
            
            if (pTipoBusqueda.equals("0")|| bBusqFiltro) {
                if (buscarDocumentoEmiConsulBean.getTipoDoc() != null && buscarDocumentoEmiConsulBean.getTipoDoc().trim().length() > 0) {
                    //prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoEmiConsulBean.getTipoDoc()).append("'");
                    prutaReporte.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                    objectParam.put("pCoDocEmi", buscarDocumentoEmiConsulBean.getTipoDoc());
                }
                if (buscarDocumentoEmiConsulBean.getEstadoDoc() != null && buscarDocumentoEmiConsulBean.getEstadoDoc().trim().length() > 0) {
                    //prutaReporte.append(" AND A.ES_DOC_EMI = '").append(buscarDocumentoEmiConsulBean.getEstadoDoc()).append("'");
                    prutaReporte.append(" AND A.ES_DOC_EMI = :pEsDocEmi ");
                    objectParam.put("pEsDocEmi", buscarDocumentoEmiConsulBean.getEstadoDoc());
                }
                if (buscarDocumentoEmiConsulBean.getPrioridadDoc() != null && buscarDocumentoEmiConsulBean.getPrioridadDoc().trim().length() > 0) {
                    //prutaReporte.append(" AND B.CO_PRIORIDAD = '").append(buscarDocumentoEmiConsulBean.getPrioridadDoc()).append("'");
                    prutaReporte.append(" AND B.CO_PRIORIDAD = :pCoPrioridad ");
                    objectParam.put("pCoPrioridad", buscarDocumentoEmiConsulBean.getPrioridadDoc());
                }
                if (buscarDocumentoEmiConsulBean.getCoRefOrigen() != null && buscarDocumentoEmiConsulBean.getCoRefOrigen().trim().length() > 0) {
                    //prutaReporte.append(" AND CHARINDEX('").append(buscarDocumentoEmiConsulBean.getCoRefOrigen()).append("', B.TI_EMI_REF) > 0 ");
                    prutaReporte.append(" AND STRPOS(B.TI_EMI_REF,:pTiEmiRef) > 0 ");
                    objectParam.put("pTiEmiRef", buscarDocumentoEmiConsulBean.getCoRefOrigen());
                }                 
                if (buscarDocumentoEmiConsulBean.getCoDepOrigen() != null && buscarDocumentoEmiConsulBean.getCoDepOrigen().trim().length() > 0) {
                    //prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoEmiConsulBean.getCoDepOrigen()).append("' ");
                    prutaReporte.append(" AND A.CO_DEP_EMI = :pCoDepOrigen ");
                    objectParam.put("pCoDepOrigen", buscarDocumentoEmiConsulBean.getCoDepOrigen());
                }
                if (buscarDocumentoEmiConsulBean.getCoDepDestino() != null && buscarDocumentoEmiConsulBean.getCoDepDestino().trim().length() > 0) {
                    //prutaReporte.append(" AND CHARINDEX('").append(buscarDocumentoEmiConsulBean.getCoDepDestino()).append("', B.TI_EMI_DES) > 0 ");
                     prutaReporte.append(" AND STRPOS(B.TI_EMI_DES,:pTiEmpPro) > 0 ");
                     objectParam.put("pTiEmpPro", buscarDocumentoEmiConsulBean.getCoDepDestino());
                }
                if (buscarDocumentoEmiConsulBean.getEsFiltroFecha() != null
                        && (buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoEmiConsulBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = buscarDocumentoEmiConsulBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoEmiConsulBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0 && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                        prutaReporte.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59' ");
                        objectParam.put("pFeEmiIni", vFeEmiIni);
                        objectParam.put("pFeEmiFin", vFeEmiFin);
                    }
                }
            }
            
            //Busqueda
            if (pTipoBusqueda.equals("1")) {
                if(!bBusqFiltro){
                    if (buscarDocumentoEmiConsulBean.getCoDependencia() != null && buscarDocumentoEmiConsulBean.getCoDependencia().trim().length() > 0) {
                        prutaReporte.append(" AND A.CO_DEP_EMI = :pCoDepEmi ");
                        objectParam.put("pCoDepEmi", buscarDocumentoEmiConsulBean.getCoDependencia());
                    }                
                }            
                if (buscarDocumentoEmiConsulBean.getBusNumEmision() != null && buscarDocumentoEmiConsulBean.getBusNumEmision().trim().length() > 0) {
                    prutaReporte.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                    objectParam.put("pnuCorEmi", Integer.parseInt(buscarDocumentoEmiConsulBean.getBusNumEmision()));
                }

                if (buscarDocumentoEmiConsulBean.getBusNumDoc()!= null && buscarDocumentoEmiConsulBean.getBusNumDoc().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_DOC LIKE :pnuDocEmi||'%' ");
                    objectParam.put("pnuDocEmi", buscarDocumentoEmiConsulBean.getBusNumDoc());
                }

                if (buscarDocumentoEmiConsulBean.getBusNumExpediente() != null && buscarDocumentoEmiConsulBean.getBusNumExpediente().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_EXPEDIENTE LIKE  '%'||:pnuExpediente||'%' ");
                    objectParam.put("pnuExpediente", buscarDocumentoEmiConsulBean.getBusNumExpediente());
                }
                
                // Busqueda del Asunto
                if (buscarDocumentoEmiConsulBean.getBusAsunto()!= null && buscarDocumentoEmiConsulBean.getBusAsunto().trim().length() > 1) {    
                    prutaReporte.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
                    objectParam.put("pDeAsunto", buscarDocumentoEmiConsulBean.getBusAsunto());
    
                }
                 if (buscarDocumentoEmiConsulBean.getBusNumDocRef() != null && buscarDocumentoEmiConsulBean.getBusNumDocRef().trim().length() > 1) {             
                    //busqeda referencia
                     prutaReporte.append("  AND A.NU_EMI IN( ");
                    prutaReporte.append("   SELECT D.NU_EMI ");
                    prutaReporte.append("   FROM TDTR_ARBOL_SEG D ");
                    prutaReporte.append("   INNER JOIN TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                    prutaReporte.append("   WHERE R.NU_ANN='"+buscarDocumentoEmiConsulBean.getBusCoAnnio()+"'   ");
                    if (buscarDocumentoEmiConsulBean.getBusCodTipoDocRef() != null && buscarDocumentoEmiConsulBean.getBusCodTipoDocRef().trim().length() > 1) {
                        prutaReporte.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoEmiConsulBean.getBusCodTipoDocRef()+"' ");
                    }
                    if (buscarDocumentoEmiConsulBean.getBusCodDepEmiRef() != null && buscarDocumentoEmiConsulBean.getBusCodDepEmiRef().trim().length() > 1) {
                        prutaReporte.append(" AND R.CO_DEP_EMI='"+buscarDocumentoEmiConsulBean.getBusCodDepEmiRef()+"' ");
                    }
                    prutaReporte.append("   AND ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoEmiConsulBean.getBusNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoEmiConsulBean.getBusNumDocRef()+"'||'%'))");
                    prutaReporte.append(" )");
                }
            }
            
            //prutaReporte.append(" ");
            prutaReporte.append(" ORDER BY A.NU_COR_EMI DESC ");
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiConsulBean.class));
            
            //vResult = "0"+prutaReporte.toString();
        } catch (Exception ex) {
            //vResult="1"+ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }
}
