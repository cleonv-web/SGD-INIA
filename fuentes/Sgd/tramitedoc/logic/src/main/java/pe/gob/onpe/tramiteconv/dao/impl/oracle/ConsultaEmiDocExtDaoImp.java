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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaEmiDocExtDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoQuery;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.BusquedaTextual;

/**
 *
 * @author ecueva
 */
@Repository("consultaEmiDocExtDao")
public class ConsultaEmiDocExtDaoImp extends SimpleJdbcDaoQuery implements ConsultaEmiDocExtDao{
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Override
    public List<DocumentoExtConsulBean> getDocumentosExternos(BuscarDocumentoExtConsulBean buscarDocExt) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        boolean bBusqDep = false;
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoExtConsulBean> list = new ArrayList<DocumentoExtConsulBean>();

        sql.append("SELECT X.*,");
        sql.append(" PK_SGD_DESCRIPCION.TI_EMI_EMP (X.NU_ANN, X.NU_EMI) DE_ORI_EMI,");
        sql.append(" PK_SGD_DESCRIPCION.DE_DOCUMENTO (X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" DECODE (X.NU_CANDES,1, PK_SGD_DESCRIPCION.TI_DES_EMP (X.NU_ANN, X.NU_EMI),PK_SGD_DESCRIPCION.TI_DES_EMP_V (X.NU_ANN, X.NU_EMI)) DE_EMP_DES,");
        sql.append(" PK_SGD_DESCRIPCION.ESTADOS (X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,");
        sql.append(" PK_SGD_DESCRIPCION.DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_REC,");
        sql.append(" PK_SGD_DESCRIPCION.DE_LOCAL(X.CO_LOC_EMI) DE_LOC_EMI,");
        sql.append(" PK_SGD_DESCRIPCION.DE_DEPENDENCIA(X.CO_DEP) DE_DEPENDENCIA,");
        sql.append(" ROWNUM");
        sql.append(" FROM ( ");
        sql.append(" SELECT ");
        sql.append(" A.NU_ANN,A.NU_EMI,A.NU_COR_EMI,A.CO_TIP_DOC_ADM,");
        sql.append(" B.NU_DOC,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,");
        sql.append(" A.DE_ASU,A.NU_CANDES,A.ES_DOC_EMI,A.CO_EMP_RES,");
        sql.append(" A.NU_DIA_ATE,B.NU_EXPEDIENTE,B.IN_EXISTE_DOC EXISTE_DOC,B.IN_EXISTE_ANEXO EXISTE_ANEXO,");
        sql.append(" TO_CHAR(C.FE_VENCE,'DD/MM/YYYY') FE_EXP_VENCE_CORTA, A.CO_LOC_EMI,");
        sql.append(" A.CO_DEP");
        sql.append(" FROM TDTV_REMITOS A,TDTX_REMITOS_RESUMEN B,TDTC_EXPEDIENTE C");
        
        //YUAL
        if (buscarDocExt.getBusCoGrupoExt()!= null && buscarDocExt.getBusCoGrupoExt().trim().length() > 0) {
             sql.append(",TDTD_DEP_GRU_VAR G ");
         }
        
        
        sql.append(" WHERE");
        sql.append(" A.NU_ANN=B.NU_ANN");
        sql.append(" AND A.NU_EMI=B.NU_EMI");
        sql.append(" AND C.NU_ANN_EXP=A.NU_ANN_EXP");
        sql.append(" AND C.NU_SEC_EXP=A.NU_SEC_EXP");
        
           //YUAL
        if (buscarDocExt.getBusCoGrupoExt()!= null && buscarDocExt.getBusCoGrupoExt().trim().length() > 0) {
             sql.append(" AND G.CO_DES=A.NU_RUC_EMI and G.CO_GRU_DES=:pcoGruDes ");
             objectParam.put("pcoGruDes", buscarDocExt.getBusCoGrupoExt());
         }
        
        
//        String pnuAnn = buscarDocExt.getCoAnnio();
//        if (!(buscarDocExt.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN = :pnuAnn");
//            // Parametros Basicos
//            objectParam.put("pnuAnn", pnuAnn);
//        }
        sql.append(" AND A.CO_GRU='3'");
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi");

        // Parametros Basicos
        objectParam.put("pCoDepEmi", buscarDocExt.getCoDepEmi());

        String pTipoBusqueda = buscarDocExt.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocExt.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        
        String auxTipoAcceso=buscarDocExt.getTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
        if(tiAcceso.equals("1")){//acceso personal
            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", buscarDocExt.getCoEmpleado());            
        }else {
            if(buscarDocExt.getInMesaPartes().equals("0")/* && buscarDocExt.getInCambioEst().equals("0")*/){
                bBusqDep = true;
                sql.append(" AND A.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", buscarDocExt.getCoDependencia());                   
            }
        }
        /*else if(tiAcceso.equals("0")){//acceso total
            if(!buscarDocExt.getInCambioEst().equals("1")){
                bBusqLocal = true;
                sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi");
                objectParam.put("pcoLocEmi", buscarDocExt.getCoLocal());
            }
        }*/        

        //Filtro
//        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (buscarDocExt.getTipoDoc() != null && buscarDocExt.getTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocExt.getTipoDoc());
            }
            if (buscarDocExt.getEstadoDoc() != null && buscarDocExt.getEstadoDoc().trim().length() > 0) {
                sql.append(" AND A.ES_DOC_EMI = :pEsDocEmi ");
                objectParam.put("pEsDocEmi", buscarDocExt.getEstadoDoc());
            }
            if (buscarDocExt.getCoTipoRemite()!= null && buscarDocExt.getCoTipoRemite().trim().length() > 0) {
                sql.append(" AND A.TI_EMI = :pcoTipoRemi ");
                objectParam.put("pcoTipoRemi", buscarDocExt.getCoTipoRemite());
            }
            /*if (buscarDocExt.getCoLocEmi()!= null && buscarDocExt.getCoLocEmi().trim().length() > 0 && !bBusqLocal) {
                sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi ");
                objectParam.put("pcoLocEmi", buscarDocExt.getCoLocEmi());
            }*/ 
            if (buscarDocExt.getCoDepOriRec()!= null && buscarDocExt.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                sql.append(" AND A.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", buscarDocExt.getCoDepOriRec());                
            }            
//            if (buscarDocumentoEmiConsulBean.getCoRefOrigen() != null && buscarDocumentoEmiConsulBean.getCoRefOrigen().trim().length() > 0) {
//                sql.append(" AND INSTR(B.TI_EMI_REF, :pTiEmiRef) > 0 ");
//                objectParam.put("pTiEmiRef", buscarDocumentoEmiConsulBean.getCoRefOrigen());
//            }            
//            if (buscarDocumentoEmiConsulBean.getCoDepOrigen() != null && buscarDocumentoEmiConsulBean.getCoDepOrigen().trim().length() > 0) {
//                sql.append(" AND A.CO_DEP_EMI = :pCoDepOrigen ");
//                objectParam.put("pCoDepOrigen", buscarDocumentoEmiConsulBean.getCoDepOrigen());
//            }
            if(buscarDocExt.getCoProceso()!=null&&buscarDocExt.getCoProceso().trim().length()>0){
                if(buscarDocExt.getCoProceso().equals("CON_TUPA")){
                    sql.append(" AND B.CO_PROCESO_EXP not in ('0000') "); 
                }else{
                    sql.append(" AND B.CO_PROCESO_EXP = :pcoProceso ");
                    objectParam.put("pcoProceso", buscarDocExt.getCoProceso()); 
                }                               
            }
            if (buscarDocExt.getCoDepDestino() != null && buscarDocExt.getCoDepDestino().trim().length() > 0) {
                sql.append(" AND INSTR(B.TI_EMI_DES, :pTiEmiDes) > 0 ");
                objectParam.put("pTiEmiDes", buscarDocExt.getCoDepDestino());
            }
            if (buscarDocExt.getEsFiltroFecha() != null
                    && (buscarDocExt.getEsFiltroFecha().equals("1") || buscarDocExt.getEsFiltroFecha().equals("3") || buscarDocExt.getEsFiltroFecha().equals("2"))) {
                String vFeEmiIni = buscarDocExt.getFeEmiIni();
                String vFeEmiFin = buscarDocExt.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + 0.99999");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }

//        }

        //Busqueda
//        if (pTipoBusqueda.equals("1")) {
//            if(!bBusqFiltro){
//                if (buscarDocumentoEmiConsulBean.getCoDependencia() != null && buscarDocumentoEmiConsulBean.getCoDependencia().trim().length() > 0) {
//                    sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi ");
//                    objectParam.put("pCoDepEmi", buscarDocumentoEmiConsulBean.getCoDependencia());
//                }                
//            }            
            if (buscarDocExt.getBusNumEmision() != null && buscarDocExt.getBusNumEmision().trim().length() > 0) {
                sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", buscarDocExt.getBusNumEmision());
            }

            if (buscarDocExt.getBusNumDoc()!= null && buscarDocExt.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocExt.getBusNumDoc());
            }

            if (buscarDocExt.getBusNumExpediente() != null && buscarDocExt.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocExt.getBusNumExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocExt.getBusAsunto()!= null && buscarDocExt.getBusAsunto().trim().length() > 1) {
                //sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
                //objectParam.put("pDeAsunto", buscarDocExt.getBusAsunto());
                sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocExt.getBusAsunto())+"', 1 ) > 1 ");
            }
            
            if (buscarDocExt.getCoTipoExp() != null && !buscarDocExt.getCoTipoExp().equals("")) {
                sql.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                objectParam.put("codTipoExp", buscarDocExt.getCoTipoExp()); 
            }
            
            if (buscarDocExt.getCoOriDoc()!= null && !buscarDocExt.getCoOriDoc().equals("")) {
                sql.append(" AND A.CCOD_ORIGING = :coOriDoc");        
                objectParam.put("coOriDoc", buscarDocExt.getCoOriDoc()); 
            }
            
            if(buscarDocExt.getBusResultado().equals("1"))
            {
                if(buscarDocExt.getCoTipoPersona().equals("03")){
                sql.append(" AND A.NU_DNI_EMI = :pNumDni");
                objectParam.put("pNumDni", buscarDocExt.getBusNumDni());
                }
                else if(buscarDocExt.getCoTipoPersona().equals("02")){
                    sql.append(" AND A.NU_RUC_EMI = :pNumRuc");
                    objectParam.put("pNumRuc", buscarDocExt.getBusNumRuc());
                }
                else if(buscarDocExt.getCoTipoPersona().equals("04")){
                    sql.append(" AND A.CO_OTR_ORI_EMI = :pCoOtr");
                    objectParam.put("pCoOtr", buscarDocExt.getBusCoOtros());
                }
            }
//        }
        sql.append(" ORDER BY A.NU_COR_EMI DESC");
        sql.append(") X ");
        //sql.append("WHERE ROWNUM < 201");
        sql.append("WHERE ROWNUM <=  ").append(applicationProperties.getTopRegistrosConsultas());


        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoExtConsulBean.class));

        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public DocumentoExtConsulBean getDocumentoExtConsulBean(String nuAnn, String nuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_EMI,B.NU_EXPEDIENTE,\n" +
                "TO_CHAR(B.FE_EXP,'DD/MM/YYYY') FE_EXP_CORTA,PK_SGD_DESCRIPCION.DE_PROCESO_EXP(B.CO_PROCESO) DE_PROCESO_EXP,PK_SGD_DESCRIPCION.TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI,\n" +
                "PK_SGD_DESCRIPCION.DE_LOCAL(A.CO_LOC_EMI) DE_LOC_EMI,PK_SGD_DESCRIPCION.DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
                "A.DE_DOC_SIG NU_DOC,A.DE_ASU,A.NU_DIA_ATE,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n" +        
                "PK_SGD_DESCRIPCION.ESTADOS (A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,\n" +
                "PK_SGD_DESCRIPCION.DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_REC\n" +
                "FROM TDTV_REMITOS A LEFT JOIN TDTC_EXPEDIENTE B\n" +
                "ON A.NU_ANN_EXP = B.NU_ANN_EXP AND A.NU_SEC_EXP = B.NU_SEC_EXP\n" +
                "WHERE\n" +
                "A.NU_ANN=?\n" +
                "AND A.NU_EMI=?\n" +
                "AND A.ES_ELI='0'\n" +
                "AND A.CO_GRU='3'");

        DocumentoExtConsulBean docExt = new DocumentoExtConsulBean();
        try {
            docExt = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtConsulBean.class),
                    new Object[]{nuAnn, nuEmi});
        } catch (EmptyResultDataAccessException e) {
            docExt = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docExt;        
    }
    
    @Override
    public List<ReferenciaDocExtRecepBean> getLsRefDocExterno(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        List<ReferenciaDocExtRecepBean> list;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,PK_SGD_DESCRIPCION.DE_DOCUMENTO(A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
                    "C.NU_DOC de_doc_sig,\n" +
                    "C.NU_EXPEDIENTE,\n" +
                    "TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA\n" +
                    "FROM TDTV_REMITOS A,TDTR_REFERENCIA B,TDTX_REMITOS_RESUMEN C\n" +
                    "WHERE \n" +
                    "A.NU_ANN=B.NU_ANN_REF AND\n" +
                    "A.NU_EMI=B.NU_EMI_REF AND\n" +
                    "C.NU_ANN=A.NU_ANN AND\n"+
                    "C.NU_EMI=A.NU_EMI AND\n"+
                    "B.NU_ANN=? AND\n" +
                    "B.NU_EMI=?");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaDocExtRecepBean.class),
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
    public List<DestinatarioDocumentoEmiConsulBean> getLsDestinoEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiConsulBean> list = null;
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,NVL2(a.co_loc_des,substr(PK_SGD_DESCRIPCION.DE_LOCAL(a.co_loc_des), 1, 100),NULL) de_local,\n"
                + "a.co_dep_des co_dependencia,NVL2(a.co_dep_des,substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.co_dep_des), 1, 100),NULL) de_dependencia,\n"
                + "a.co_emp_des co_empleado,NVL2(a.co_emp_des,substr(PK_SGD_DESCRIPCION.DE_NOM_EMP(a.co_emp_des), 1, 100),NULL) de_empleado,\n"
                + "a.co_mot co_tramite,NVL2(a.co_mot,substr(PK_SGD_DESCRIPCION.MOTIVO(a.co_mot), 1, 100),NULL) de_tramite,\n"
                + "a.co_pri co_prioridad,PK_SGD_DESCRIPCION.DE_PRIORIDAD(a.co_pri) de_prioridad,\n"
                + "a.de_pro de_indicaciones,\n"
                + "a.NU_RUC_DES nu_ruc,NVL2(a.NU_RUC_DES,substr(PK_SGD_DESCRIPCION.DE_PROVEEDOR(a.NU_RUC_DES), 1, 100),NULL) de_proveedor,\n"
                + "a.NU_DNI_DES nu_dni,NVL2(a.NU_DNI_DES,substr(PK_SGD_DESCRIPCION.ANI_SIMIL(a.NU_DNI_DES), 1, 100),NULL) de_ciudadano,\n"
                + "a.CO_OTR_ORI_DES co_otro_origen,\n"
                + "NVL2(\n"
                + "a.CO_OTR_ORI_DES,\n"
                + "(SELECT C.DE_APE_PAT_OTR||' '||C.DE_APE_MAT_OTR||', '||C.DE_NOM_OTR || ' - ' ||\n"
                + "     C.DE_RAZ_SOC_OTR ||'##'||\n"
                + "     NVL(B.CELE_DESELE,'   ') ||'##'||\n"
                + "     C.NU_DOC_OTR_ORI  \n"
                + "  FROM TDTR_OTRO_ORIGEN C, (\n"
                + "  SELECT CELE_CODELE, CELE_DESELE\n"
                + "    FROM SI_ELEMENTO\n"
                + "   WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B\n"
                + "WHERE C.CO_TIP_OTR_ORI = B.CELE_CODELE(+)\n"
                + "AND C.CO_OTR_ORI = a.CO_OTR_ORI_DES\n"
                + "),\n"
                + "NULL\n"
                + ") de_otro_origen_full,\n"
                + "a.ti_des co_tipo_destino\n"
                + "FROM tdtv_destinos a\n"
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
    public String getRutaReporte(BuscarDocumentoExtConsulBean DocExt){
        String vResult="0";
        boolean bBusqDep = false;
        StringBuffer sql = new StringBuffer();

        try {
            sql.append(" A.NU_ANN=B.NU_ANN");
            sql.append(" AND A.NU_EMI=B.NU_EMI");
            sql.append(" AND C.NU_ANN_EXP=A.NU_ANN_EXP");
            sql.append(" AND C.NU_SEC_EXP=A.NU_SEC_EXP");
            String pnuAnn = DocExt.getCoAnnio();
            if (!(DocExt.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))) {
                // Parametros Basicos
                sql.append(" AND A.NU_ANN = '").append(pnuAnn).append("'");
            }
            sql.append(" AND A.CO_GRU='3'");
            sql.append(" AND A.ES_ELI = '0'");
            // Parametros Basicos
            sql.append(" AND A.CO_DEP_EMI = '").append(DocExt.getCoDependencia()).append("'");

            String auxTipoAcceso=DocExt.getTiAcceso();
            String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
            if(tiAcceso.equals("1")){//acceso personal
                sql.append(" AND A.CO_EMP_RES = '").append(DocExt.getCoEmpleado()).append("'");
            }else {
                if(DocExt.getInMesaPartes().equals("0")/* && buscarDocExt.getInCambioEst().equals("0")*/){
                    bBusqDep = true;
                    sql.append(" AND A.CO_DEP = '").append(DocExt.getCoDependencia()).append("'");        
                }
            }            
            
            if (DocExt.getTipoDoc() != null && DocExt.getTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = '").append(DocExt.getTipoDoc()).append("'");
            }
            if (DocExt.getEstadoDoc() != null && DocExt.getEstadoDoc().trim().length() > 0) {
                sql.append(" AND A.ES_DOC_EMI = '").append(DocExt.getEstadoDoc()).append("'");
            }
            if (DocExt.getCoTipoRemite()!= null && DocExt.getCoTipoRemite().trim().length() > 0) {
                sql.append(" AND A.TI_EMI = '").append(DocExt.getCoTipoRemite()).append("'");
            }
            if (DocExt.getCoDepOriRec()!= null && DocExt.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                sql.append(" AND A.CO_DEP = '").append(DocExt.getCoDepOriRec()).append("'");    
            }               
//            if (buscarDocumentoEmiConsulBean.getCoRefOrigen() != null && buscarDocumentoEmiConsulBean.getCoRefOrigen().trim().length() > 0) {
//                sql.append(" AND INSTR(B.TI_EMI_REF, :pTiEmiRef) > 0 ");
//                objectParam.put("pTiEmiRef", buscarDocumentoEmiConsulBean.getCoRefOrigen());
//            }            
//            if (buscarDocumentoEmiConsulBean.getCoDepOrigen() != null && buscarDocumentoEmiConsulBean.getCoDepOrigen().trim().length() > 0) {
//                sql.append(" AND A.CO_DEP_EMI = :pCoDepOrigen ");
//                objectParam.put("pCoDepOrigen", buscarDocumentoEmiConsulBean.getCoDepOrigen());
//            }
            if(DocExt.getCoProceso()!=null&&DocExt.getCoProceso().trim().length()>0){
                if(DocExt.getCoProceso().equals("CON_TUPA")){
                    sql.append(" AND B.CO_PROCESO_EXP not in ('0000') "); 
                }else{
                    sql.append(" AND B.CO_PROCESO_EXP = '").append(DocExt.getCoProceso()).append("'");
                }
                
            }            
            if (DocExt.getCoDepDestino() != null && DocExt.getCoDepDestino().trim().length() > 0) {
                sql.append(" AND INSTR(B.TI_EMI_DES, '").append(DocExt.getCoDepDestino()).append("') > 0 ");
            }
            if (DocExt.getEsFiltroFecha() != null
                    && (DocExt.getEsFiltroFecha().equals("1") || DocExt.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = DocExt.getFeEmiIni();
                String vFeEmiFin = DocExt.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin)
                        .append("','dd/mm/yyyy') %2B 0.99999");
                }
            }
            sql.append(" ");
            vResult = "0"+sql.toString();
        } catch (Exception ex) {
            vResult="1"+ex.getMessage();
            ex.printStackTrace();
        }
        return vResult;
    }

    @Override
    public List<DocumentoExtConsulBean> getListaReporteBusqueda(BuscarDocumentoExtConsulBean buscarDocumentoExtConsulBean) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        boolean bBusqFiltro = false;
        boolean bBusqDep = false;
        
        prutaReporte.append("SELECT  A.NU_COR_EMI, ");
        prutaReporte.append("	     TO_CHAR(A.FE_EMI, 'DD/MM/YYYY') FE_EMI_CORTA, ");
        prutaReporte.append("	     (SELECT CDOC_DESDOC ");
        prutaReporte.append("	     FROM SI_MAE_TIPO_DOC ");
        prutaReporte.append("	     WHERE CDOC_TIPDOC = A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,B.NU_DOC, ");
        prutaReporte.append("	     (select CELE_DESELE ");
        prutaReporte.append("	     from SI_ELEMENTO ");
        prutaReporte.append("	     WHERE CTAB_CODTAB='TIP_DESTINO' ");
        prutaReporte.append("	     AND CELE_CODELE=A.TI_EMI) tiRemitente, ");
        prutaReporte.append("	     PK_SGD_DESCRIPCION.TI_EMI_EMP(A.NU_ANN, A.NU_EMI) deRemitente, ");
        prutaReporte.append("	     CASE A.NU_CANDES ");
        prutaReporte.append("	     	WHEN 1 THEN PK_SGD_DESCRIPCION.TI_DES_EMP(A.NU_ANN, A.NU_EMI) ");
        prutaReporte.append("	     	ELSE PK_SGD_DESCRIPCION.TI_DES_EMP_V(A.NU_ANN, A.NU_EMI) ");
        prutaReporte.append("	     END deEmpPro, ");
        prutaReporte.append("	     A.DE_ASU, ");
        prutaReporte.append("	     (SELECT DE_EST ");
        prutaReporte.append("	     FROM TDTR_ESTADOS ");
        prutaReporte.append("	     WHERE CO_EST || DE_TAB = A.ES_DOC_EMI || 'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        prutaReporte.append("	     A.NU_DIA_ATE, ");
        prutaReporte.append("	     B.NU_EXPEDIENTE, ");
        prutaReporte.append("	     TO_CHAR(C.FE_VENCE, 'DD/MM/YYYY') FE_EXP_VENCE_CORTA, ");
        prutaReporte.append("	     (SELECT DE_NOMBRE ");
        prutaReporte.append("	     FROM TDTR_PROCESOS_EXP ");
        prutaReporte.append("	     WHERE CO_PROCESO = B.CO_PROCESO_EXP ");
        prutaReporte.append("	     AND ES_ESTADO='1') DE_PROCESO_EXP, ");
        prutaReporte.append("	     B.CO_PROCESO_EXP AS coProcesoExp, ");
        prutaReporte.append("	     A.NU_FOLIOS AS nuFolios, ");
        prutaReporte.append("	     PK_SGD_DESCRIPCION.DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_REC ");
        prutaReporte.append("FROM TDTV_REMITOS A, ");
        prutaReporte.append("     TDTX_REMITOS_RESUMEN B, ");
        prutaReporte.append("     TDTC_EXPEDIENTE C ");
        
            //YUAL
        if (buscarDocumentoExtConsulBean.getBusCoGrupoExt()!= null && buscarDocumentoExtConsulBean.getBusCoGrupoExt().trim().length() > 0) {
             prutaReporte.append(",TDTD_DEP_GRU_VAR G ");
         }
        
        prutaReporte.append("WHERE A.NU_ANN=B.NU_ANN ");
        prutaReporte.append("AND A.NU_EMI=B.NU_EMI ");
        prutaReporte.append("AND C.NU_ANN_EXP=A.NU_ANN_EXP ");
        prutaReporte.append("AND C.NU_SEC_EXP=A.NU_SEC_EXP ");
             //YUAL
        if (buscarDocumentoExtConsulBean.getBusCoGrupoExt()!= null && buscarDocumentoExtConsulBean.getBusCoGrupoExt().trim().length() > 0) {
             prutaReporte.append(" AND G.CO_DES=A.NU_RUC_EMI and G.CO_GRU_DES=:pcoGruDes ");
             objectParam.put("pcoGruDes", buscarDocumentoExtConsulBean.getBusCoGrupoExt());
         }
        
        try {
            String pnuAnn = buscarDocumentoExtConsulBean.getCoAnnio();
            if (!(buscarDocumentoExtConsulBean.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))) {
                // Parametros Basicos
                prutaReporte.append(" AND A.NU_ANN = '").append(pnuAnn).append("'");
            }
            prutaReporte.append(" AND A.CO_GRU = '3'");
            prutaReporte.append(" AND A.ES_ELI = '0'");
            // Parametros Basicos
            prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoExtConsulBean.getCoDepEmi()).append("'");

            String auxTipoAcceso=buscarDocumentoExtConsulBean.getTiAcceso();
            String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
            if (tiAcceso.equals("1")) { // acceso personal
                prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoExtConsulBean.getCoEmpleado()).append("'");
            } else {
                if(buscarDocumentoExtConsulBean.getInMesaPartes().equals("0")/* && buscarDocExt.getInCambioEst().equals("0")*/){
                    bBusqDep = true;
                    prutaReporte.append(" AND A.CO_DEP = '").append(buscarDocumentoExtConsulBean.getCoDependencia()).append("'");        
                }
            }            
            String pTipoBusqueda = buscarDocumentoExtConsulBean.getTipoBusqueda();
            
            if (pTipoBusqueda.equals("1") && buscarDocumentoExtConsulBean.isEsIncluyeFiltro()) {
                bBusqFiltro = true;
            }
            
            //Filtro
//            if (pTipoBusqueda.equals("0") || bBusqFiltro) {
                if (buscarDocumentoExtConsulBean.getTipoDoc() != null && buscarDocumentoExtConsulBean.getTipoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoExtConsulBean.getTipoDoc()).append("'");
                }
                if (buscarDocumentoExtConsulBean.getEstadoDoc() != null && buscarDocumentoExtConsulBean.getEstadoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.ES_DOC_EMI = '").append(buscarDocumentoExtConsulBean.getEstadoDoc()).append("'");
                }
                if (buscarDocumentoExtConsulBean.getCoTipoRemite()!= null && buscarDocumentoExtConsulBean.getCoTipoRemite().trim().length() > 0) {
                    prutaReporte.append(" AND A.TI_EMI = '").append(buscarDocumentoExtConsulBean.getCoTipoRemite()).append("'");
                }
                if (buscarDocumentoExtConsulBean.getCoDepOriRec()!= null && buscarDocumentoExtConsulBean.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                    prutaReporte.append(" AND A.CO_DEP = '").append(buscarDocumentoExtConsulBean.getCoDepOriRec()).append("'");    
                }               

                if(buscarDocumentoExtConsulBean.getCoProceso()!=null&&buscarDocumentoExtConsulBean.getCoProceso().trim().length()>0){
                    if(buscarDocumentoExtConsulBean.getCoProceso().equals("CON_TUPA")){
                        prutaReporte.append(" AND B.CO_PROCESO_EXP not in ('0000') "); 
                    }else{
                        prutaReporte.append(" AND B.CO_PROCESO_EXP = '").append(buscarDocumentoExtConsulBean.getCoProceso()).append("'");
                    }
                }            
                if (buscarDocumentoExtConsulBean.getCoDepDestino() != null && buscarDocumentoExtConsulBean.getCoDepDestino().trim().length() > 0) {
                    prutaReporte.append(" AND INSTR(B.TI_EMI_DES, '").append(buscarDocumentoExtConsulBean.getCoDepDestino()).append("') > 0 ");
                }
                if (buscarDocumentoExtConsulBean.getEsFiltroFecha() != null
                        && (buscarDocumentoExtConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoExtConsulBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = buscarDocumentoExtConsulBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoExtConsulBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0 && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                        prutaReporte.append(" AND A.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin)
                            .append("','dd/mm/yyyy') + 0.99999 ");
                    }
                }
//            }
            //Busqueda
//            if (pTipoBusqueda.equals("1")) {         
                if (buscarDocumentoExtConsulBean.getBusNumEmision() != null && buscarDocumentoExtConsulBean.getBusNumEmision().trim().length() > 0) {
                    prutaReporte.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                    objectParam.put("pnuCorEmi", Integer.getInteger(buscarDocumentoExtConsulBean.getBusNumEmision()));
                }

                if (buscarDocumentoExtConsulBean.getBusNumDoc()!= null && buscarDocumentoExtConsulBean.getBusNumDoc().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                    objectParam.put("pnuDocEmi", buscarDocumentoExtConsulBean.getBusNumDoc());
                }

                if (buscarDocumentoExtConsulBean.getBusNumExpediente() != null && buscarDocumentoExtConsulBean.getBusNumExpediente().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                    objectParam.put("pnuExpediente", buscarDocumentoExtConsulBean.getBusNumExpediente());
                }
                // Busqueda del Asunto
                if (buscarDocumentoExtConsulBean.getBusAsunto()!= null && buscarDocumentoExtConsulBean.getBusAsunto().trim().length() > 1) {                    
                    //prutaReporte.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoExtConsulBean.getBusAsunto())+"', 1 ) > 1 ");
                     prutaReporte.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
                     objectParam.put("pDeAsunto", buscarDocumentoExtConsulBean.getBusAsunto().trim());
                }
                
                if (buscarDocumentoExtConsulBean.getCoTipoExp() != null && !buscarDocumentoExtConsulBean.getCoTipoExp().equals("")) {
                    prutaReporte.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                    objectParam.put("codTipoExp", buscarDocumentoExtConsulBean.getCoTipoExp()); 
                }

                if (buscarDocumentoExtConsulBean.getCoOriDoc()!= null && !buscarDocumentoExtConsulBean.getCoOriDoc().equals("")) {
                    prutaReporte.append(" AND A.CCOD_ORIGING = :coOriDoc");        
                    objectParam.put("coOriDoc", buscarDocumentoExtConsulBean.getCoOriDoc()); 
                }

                if(buscarDocumentoExtConsulBean.getBusResultado().equals("1"))
                {
                    if(buscarDocumentoExtConsulBean.getCoTipoPersona().equals("03")){
                    prutaReporte.append(" AND A.NU_DNI_EMI = :pNumDni");
                    objectParam.put("pNumDni", buscarDocumentoExtConsulBean.getBusNumDni());
                    }
                    else if(buscarDocumentoExtConsulBean.getCoTipoPersona().equals("02")){
                        prutaReporte.append(" AND A.NU_RUC_EMI = :pNumRuc");
                        objectParam.put("pNumRuc", buscarDocumentoExtConsulBean.getBusNumRuc());
                    }
                    else if(buscarDocumentoExtConsulBean.getCoTipoPersona().equals("04")){
                        prutaReporte.append(" AND A.CO_OTR_ORI_EMI = :pCoOtr");
                        objectParam.put("pCoOtr", buscarDocumentoExtConsulBean.getBusCoOtros());
                    }
                }
//            }

            prutaReporte.append(" ORDER BY A.NU_COR_EMI DESC ");
            
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoExtConsulBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }

}