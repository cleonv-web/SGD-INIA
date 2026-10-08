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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaRecepDocDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

@Repository("consultaRecepDocDao")
public class ConsultaRecepDocDaoImp extends SimpleJdbcDaoBase implements ConsultaRecepDocDao {

    @Override
    public List<DocumentoRecepConsulBean> getDocumentosBuscaRecepAdm(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean){
        StringBuffer sql = new StringBuffer(); 
        boolean bBusqFiltro=false;
        boolean bBusqFeEmi=false;
        boolean bBusqFePendiente=false;
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoRecepConsulBean> list = new ArrayList<DocumentoRecepConsulBean>();

        sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (X.NU_ANN, X.NU_EMI) DE_ORI_EMI,"); 
        sql.append("CASE\n"
                + "WHEN X.TI_DES = '01' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.CO_EMP_DES)\n"
                + "WHEN X.TI_DES = '02' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.NU_RUC_DES)\n"
                + "WHEN X.TI_DES = '03' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (X.NU_DNI_DES)\n"
                + "WHEN X.TI_DES = '04' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (X.CO_OTR_ORI_DES)\n"
                + "END DE_EMP_DES,");
        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (X.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(X.CO_MOT) DE_MOTIVO,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_REC) DE_EMP_REC "); 
        sql.append(" FROM ( ");        
        sql.append(" SELECT A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,A.FE_EMI,A.CO_TIP_DOC_ADM,");
        sql.append(" UPPER (A.DE_ASU) DE_ASU_M,C.NU_EXPEDIENTE,B.TI_DES,B.CO_EMP_DES,B.NU_RUC_DES,B.NU_DNI_DES,B.CO_OTR_ORI_DES,C.NU_DOC,");
        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append("CASE\n"
                + "WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='00' THEN\n"
                + "0\n"
                + "WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='02' THEN\n"
                + "0\n"
                + "ELSE\n"
                + "1 \n"
                + "END AS EXISTE_ANEXO,");
        sql.append("COALESCE(B.CO_PRI,'1') CO_PRI,B.ES_DOC_REC,");
        sql.append(" B.FE_REC_DOC,TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY') FE_REC_CORTA,B.CO_MOT,");
        sql.append(" B.CO_ETIQUETA_REC,A.NU_DIA_ATE,A.CO_EMP_RES,B.CO_EMP_REC");
        sql.append(" FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B,IDOSGD.TDTX_REMITOS_RESUMEN C");
        sql.append(" WHERE"); 
        sql.append(" B.NU_ANN = A.NU_ANN");
        sql.append(" AND B.NU_EMI = A.NU_EMI");
        sql.append(" AND C.NU_ANN = B.NU_ANN");
        sql.append(" AND C.NU_EMI = B.NU_EMI");
//        String pNUAnn = buscarDocumentoRecepConsulBean.getCoAnnio();
//        if(!(buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))){
//            sql.append(" AND A.NU_ANN = :pNuAnn");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNUAnn);                
//        }        

        //sql.append(" AND B.CO_DEP_DES = :pCoDepDes");
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND B.ES_ELI = '0'");  
        sql.append(" AND A.CO_GRU IN ('1','2','3')");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7','8')");
        //sql.append(" AND B.ES_DOC_REC <> '0'");
        sql.append(" AND A.IN_OFICIO = '0'");        
        
        // Parametros Basicos
        //objectParam.put("pCoDepDes", buscarDocumentoRecepConsulBean.getCoDependencia());

        
        String pTipoBusqueda = buscarDocumentoRecepConsulBean.getTipoBusqueda();
        if(pTipoBusqueda.equals("1") && buscarDocumentoRecepConsulBean.isEsIncluyeFiltro()){
            bBusqFiltro=true;
        }         
        String auxTipoAcceso=buscarDocumentoRecepConsulBean.getTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1")||auxTipoAcceso.equals("2"))?auxTipoAcceso:"1";        
        if (buscarDocumentoRecepConsulBean.getCoEmpDestino()!=null && buscarDocumentoRecepConsulBean.getCoEmpDestino().trim().length()>0 &&(pTipoBusqueda.equals("0") || bBusqFiltro)&&tiAcceso.equals("0")){
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
            objectParam.put("pcoEmpDes", buscarDocumentoRecepConsulBean.getCoEmpDestino());
        }else {    
            if(tiAcceso.equals("1")){
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
                objectParam.put("pcoEmpDes", buscarDocumentoRecepConsulBean.getCoEmpleado());
            }else if(tiAcceso.equals("2")){
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes  ");
                objectParam.put("pcoEmpDes", buscarDocumentoRecepConsulBean.getCoEmpleado());
            }
        }
        
       
        //Filtro
        if(pTipoBusqueda.equals("0") || bBusqFiltro){
            if (buscarDocumentoRecepConsulBean.getCoTipoDocAdm()!= null && buscarDocumentoRecepConsulBean.getCoTipoDocAdm().trim().length()>0){
               sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
               objectParam.put("pCoDocEmi", buscarDocumentoRecepConsulBean.getCoTipoDocAdm());
            }
            if (buscarDocumentoRecepConsulBean.getEstadoDoc()!= null && buscarDocumentoRecepConsulBean.getEstadoDoc().trim().length()>0){
                String estadoDoc=buscarDocumentoRecepConsulBean.getEstadoDoc();
                if(estadoDoc.equals("P")){
                   
                   bBusqFePendiente=true;
                    sql.append(" AND B.ES_DOC_REC in ('0','1') ");
               }
               else{
                sql.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                objectParam.put("pEsDocRec", estadoDoc);
               }
                if(estadoDoc.equals("0")||estadoDoc.equals("P")){
                    bBusqFeEmi=true;
                }
            }
            else {
                bBusqFeEmi=true;
            }
            if (buscarDocumentoRecepConsulBean.getPrioridadDoc()!= null && buscarDocumentoRecepConsulBean.getPrioridadDoc().trim().length()>0){
               sql.append(" AND B.CO_PRI = :pCoPrioridad ");
               objectParam.put("pCoPrioridad", buscarDocumentoRecepConsulBean.getPrioridadDoc());
            }
            if (buscarDocumentoRecepConsulBean.getCoDepRemite()!= null && buscarDocumentoRecepConsulBean.getCoDepRemite().trim().length()>0){
               sql.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = :pTiEmiRef ");
               objectParam.put("pTiEmiRef", buscarDocumentoRecepConsulBean.getCoDepRemite());
            }
            if (buscarDocumentoRecepConsulBean.getCoDepDestino()!= null && buscarDocumentoRecepConsulBean.getCoDepDestino().trim().length()>0){
               sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
               objectParam.put("pcoDepDes", buscarDocumentoRecepConsulBean.getCoDepDestino());
            }
            if (buscarDocumentoRecepConsulBean.getIdEtiqueta() != null && buscarDocumentoRecepConsulBean.getIdEtiqueta().trim().length() > 0) {
                sql.append(" AND B.CO_ETIQUETA_REC = :pcoEtiquetaRec ");
                objectParam.put("pcoEtiquetaRec", buscarDocumentoRecepConsulBean.getIdEtiqueta());
            }
            if(buscarDocumentoRecepConsulBean.getEsFiltroFecha()!= null && 
               (buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("2") || buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("3"))){
                String vFeEmiIni = buscarDocumentoRecepConsulBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoRecepConsulBean.getFeEmiFin();       
                if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                   vFeEmiFin!= null && vFeEmiFin.trim().length()>0){
                    if(bBusqFeEmi){ 
                        sql.append(" AND A.FE_EMI between to_timestamp(:pFeEmiIni,'dd/mm/yyyy') AND to_timestamp(:pFeEmiFin,'dd/mm/yyyy hh24:mi:ss')");
                    }else{ 
                        sql.append(" AND B.FE_REC_DOC between to_timestamp(:pFeEmiIni,'dd/mm/yyyy') AND to_timestamp(:pFeEmiFin,'dd/mm/yyyy hh24:mi:ss')");
                    }
                    if(bBusqFePendiente){ 
                        sql.append(" AND ( (B.ES_DOC_REC='0' AND A.FE_EMI between TO_DATE(:pFeEmiIni, 'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin, 'dd/mm/yyyy') + 0.99999) ");                         
                        sql.append(" OR (B.ES_DOC_REC='1' AND B.FE_REC_DOC between TO_DATE(:pFeEmiIni, 'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin, 'dd/mm/yyyy') + 0.99999)) ");                         
                    }
                    
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin + " 23:59:59");                    
                }
            }            
        }    

        if (pTipoBusqueda.equals("1"))
        {
            if(!bBusqFiltro){
                if (buscarDocumentoRecepConsulBean.getCoDependencia()!= null && buscarDocumentoRecepConsulBean.getCoDependencia().trim().length()>0){
                   sql.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                   objectParam.put("pcoDepDes", buscarDocumentoRecepConsulBean.getCoDependencia());
                }                 
            }
            if (buscarDocumentoRecepConsulBean.getBusNumDoc()!= null && buscarDocumentoRecepConsulBean.getBusNumDoc().trim().length()>1){
               sql.append(" AND A.NU_DOC_EMI LIKE '%'||:pnuDocEmi||'%' ");
               objectParam.put("pnuDocEmi", buscarDocumentoRecepConsulBean.getBusNumDoc());
            }

            if (buscarDocumentoRecepConsulBean.getBusNumExpediente()!= null && buscarDocumentoRecepConsulBean.getBusNumExpediente().trim().length()>1){
               sql.append(" AND C.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
               objectParam.put("pnuExpediente", buscarDocumentoRecepConsulBean.getBusNumExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoRecepConsulBean.getBusAsunto()!= null && buscarDocumentoRecepConsulBean.getBusAsunto().trim().length()>1){                
               sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
               objectParam.put("pDeAsunto", buscarDocumentoRecepConsulBean.getBusAsunto());
            }
            
            // Busqueda del remitente
            buscarDocumentoRecepConsulBean.setBusRemitente(Utilidades.fn_getCleanTextLenGreathree(Utilidades.fn_getCleanTextExpReg(buscarDocumentoRecepConsulBean.getBusRemitente())));                                    
            if (buscarDocumentoRecepConsulBean.getBusRemitente()!= null && buscarDocumentoRecepConsulBean.getBusRemitente().trim().length()>1){

                sql.append(" AND UPPER(DECODE(A.TI_EMI,");
                sql.append("'01', IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_EMI) ||'-'||IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_EMI),");
                sql.append("'02', IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR (A.NU_RUC_EMI),");
                sql.append("'03', IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (A.NU_DNI_EMI),");
                sql.append("'04', IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (A.CO_OTR_ORI_EMI),");
                sql.append("'05', IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_EMI) ||'-'||IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_EMI)");
                sql.append(")) LIKE '%'||UPPER(:pRemitente)||'%' ");
                                
                objectParam.put("pRemitente", buscarDocumentoRecepConsulBean.getBusRemitente());
            }
            if (buscarDocumentoRecepConsulBean.getBusNumDocRef() != null && buscarDocumentoRecepConsulBean.getBusNumDocRef().trim().length() > 1) {
                //busqeda referencia
                 sql.append("  AND A.NU_EMI IN( ");
                sql.append("   SELECT D.NU_EMI ");
                sql.append("   FROM IDOSGD.TDTR_ARBOL_SEG D ");
                sql.append("   INNER JOIN IDOSGD.TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                sql.append("   WHERE R.NU_ANN='"+buscarDocumentoRecepConsulBean.getBusCoAnnio()+"'   ");
                if (buscarDocumentoRecepConsulBean.getBusCodTipoDocRef() != null && buscarDocumentoRecepConsulBean.getBusCodTipoDocRef().trim().length() > 1) {
                sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoRecepConsulBean.getBusCodTipoDocRef()+"' ");
                }
                if (buscarDocumentoRecepConsulBean.getBusCodDepEmiRef() != null && buscarDocumentoRecepConsulBean.getBusCodDepEmiRef().trim().length() > 1) {
                sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoRecepConsulBean.getBusCodDepEmiRef()+"' ");
                }
                sql.append("   AND ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoRecepConsulBean.getBusNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoRecepConsulBean.getBusNumDocRef()+"'||'%'))");
                sql.append(" )");
            }
            
        }        
        
        sql.append(" ORDER BY A.FE_EMI DESC");
        sql.append(") X ");
//        sql.append("WHERE ROWNUM < 301");
        sql.append(" LIMIT 300");
        
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoRecepConsulBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;           
    }
    
    @Override
    public DocumentoRecepConsulBean getDocumentoRecepAdm(String pnuAnn,String pnuEmi,String pnuDes){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,C.NU_EXPEDIENTE,TO_CHAR((SELECT FE_EXP FROM IDOSGD.TDTC_EXPEDIENTE WHERE NU_ANN_EXP=A.NU_ANN_EXP AND NU_SEC_EXP=A.NU_SEC_EXP),'DD/MM/YYYY') FE_EXP_CORTA,\n" +
        "IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(C.CO_PROCESO_EXP) DE_PROCESO_EXP, IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI, IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_REC) DE_EMP_REC,\n" +
        "CASE \n" +
                "WHEN B.TI_DES = '01' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES)\n" +
                "WHEN B.TI_DES = '02' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES)\n" +
                "WHEN B.TI_DES = '03' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES)\n" +
                "WHEN B.TI_DES = '04' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES)\n" +
                "END AS DE_EMP_DES," +
        "CASE \n" +
                "WHEN A.TI_EMI = '01' THEN\n" +
                "A.NU_DOC_EMI || '-' || A.NU_ANN || '-' || A.DE_DOC_SIG \n" +
                "WHEN B.TI_DES = '05' THEN\n" +
                "A.NU_DOC_EMI || '-' || A.NU_ANN || '-' || A.DE_DOC_SIG \n" +
                "ELSE \n" +
                "A.DE_DOC_SIG \n" +
                "END AS NU_DOC," +                
        "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA (B.CO_DEP_DES) DE_DEP_DES,IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n" +
        "A.DE_ASU,A.NU_DIA_ATE,IDOSGD.PK_SGD_DESCRIPCION_MOTIVO (B.CO_MOT) DE_MOTIVO,B.DE_PRO DE_INDICACIONES,IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD (B.CO_PRI) DE_PRIORIDAD,\n" +
        "TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY HH24:MI:SS') FE_REC_CORTA,TO_CHAR(B.FE_ATE_DOC,'DD/MM/YYYY') FE_ATENCION_CORTA,\n" +
        "TO_CHAR(B.FE_ARC_DOC,'DD/MM/YYYY') FE_ARCHIVAMIENTO_CORTA,IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_REC,\n" +
        "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_RES) DE_EMP_RES, B.DE_ANE\n" +
        "FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B,IDOSGD.TDTX_REMITOS_RESUMEN C\n" +
        "WHERE A.ES_ELI='0' AND B.ES_ELI='0'\n" +
        "AND A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI\n" +
        "AND C.NU_ANN = A.NU_ANN AND C.NU_EMI = A.NU_EMI\n" +
        "AND A.ES_DOC_EMI NOT IN ('5', '9', '7')\n" +
        "AND A.IN_OFICIO = '0'\n" +
        "AND B.NU_ANN=? AND B.NU_EMI=? AND B.NU_DES= CAST(? as bigint)");        
        
        DocumentoRecepConsulBean documentoRecepConsulBean = new DocumentoRecepConsulBean();
        try {
            documentoRecepConsulBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoRecepConsulBean.class),
                    new Object[]{pnuAnn,pnuEmi,pnuDes});
        } catch (EmptyResultDataAccessException e) {
            documentoRecepConsulBean = null;
        } catch (Exception e) {
            System.out.println("nu_ann="+pnuAnn+","+"nu_emi="+pnuEmi+"nu_des="+pnuDes);
            e.printStackTrace();
        }
        return documentoRecepConsulBean;
    }
    
	@Override
	public List<ReferenciaConsulBean> getDocumentosRefRecepAdm(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tipo_doc, " + 
                    "CASE\n" +
                            "WHEN a.ti_emi = '01' THEN\n" +
                            "a.nu_doc_emi || '-' || a.nu_ann || '/' || a.de_doc_sig \n" +
                            "WHEN a.ti_emi = '05' THEN\n" +
                            "a.nu_doc_emi || '-' || a.nu_ann || '/' || a.de_doc_sig \n" +
                            "ELSE \n" +
                            "a.de_doc_sig \n" +
                            "END AS li_nu_doc," + 
                    " TO_CHAR(a.fe_emi,'DD/MM/YYYY') fe_emi_corta,b.co_ref,b.nu_ann,b.nu_emi,b.nu_des,b.nu_ann_ref,\n" +
                    "                 b.nu_emi_ref,b.nu_des_ref\n" +
                    "FROM IDOSGD.tdtv_remitos a,IDOSGD.TDTR_REFERENCIA b\n" +
                    "WHERE a.nu_ann = b.nu_ann_ref\n" +
                    "AND a.nu_emi = b.nu_emi_ref\n" +
                    "AND b.NU_EMI=? \n" +
                    "AND b.NU_ANN=?");
        
        List<ReferenciaConsulBean> list = new ArrayList<ReferenciaConsulBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaConsulBean.class),
                    new Object[]{pnuEmi,pnuAnn});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
        
    @Override
    public DocumentoRecepConsulBean existeDocumentoReferenciado(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean){
        StringBuffer sql = new StringBuffer();
        DocumentoRecepConsulBean documentoRecepConsulBean = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_DOC FROM IDOSGD.TDTV_REMITOS A\n" +
                    "WHERE A.NU_ANN=?\n" +
                    "AND A.CO_DEP_EMI=?\n" +
                    "AND A.TI_EMI='01'\n" +
                    "AND A.CO_TIP_DOC_ADM=?\n" +
                    "AND A.NU_DOC_EMI=?\n" +
                    "AND A.ES_ELI='0'\n" +
                    "AND A.ES_DOC_EMI NOT IN ('5','7','9')");        
        
        try {
             documentoRecepConsulBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoRecepConsulBean.class),
                    new Object[]{buscarDocumentoRecepConsulBean.getBusCoAnnio(),buscarDocumentoRecepConsulBean.getBusCodDepEmiRef(),buscarDocumentoRecepConsulBean.getBusCodTipoDocRef(),
                    buscarDocumentoRecepConsulBean.getBusNumDocRef()});
             documentoRecepConsulBean.setMsjResult("OK");
        } catch (EmptyResultDataAccessException e) {
            documentoRecepConsulBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoRecepConsulBean;          
    }
    
    @Override
    public List<DocumentoRecepConsulBean> getDocumentosReferenciadoBusq(DocumentoRecepConsulBean documentoRecepConsulBean,String ptipoAcceso){       
        StringBuffer sql = new StringBuffer(); 
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List<DocumentoRecepConsulBean> list = new ArrayList<DocumentoRecepConsulBean>();        
        
        sql.append("SELECT A.*");
        sql.append(" FROM ( ");        
        sql.append("SELECT DISTINCT A.NU_ANN,A.NU_EMI,B.NU_DES,B.NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,A.FE_EMI,");
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
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(B.CO_MOT) DE_MOTIVO,(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM IDOSGD.RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = B.CO_EMP_REC) DE_EMP_REC");
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
         sql.append("D, IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B,IDOSGD.TDTX_REMITOS_RESUMEN C ");          
        sql.append(" WHERE");
        sql.append(" D.NU_ANN = A.NU_ANN");
        sql.append(" AND D.NU_EMI = A.NU_EMI");
        sql.append(" AND B.NU_ANN = A.NU_ANN");
        sql.append(" AND B.NU_EMI = A.NU_EMI");
        sql.append(" AND C.NU_ANN = B.NU_ANN");
        sql.append(" AND C.NU_EMI = B.NU_EMI");
        sql.append(" AND B.CO_DEP_DES = :pCoDepDes");
        if(ptipoAcceso.equals("1")){
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes");      
            objectParam.put("pcoEmpDes", documentoRecepConsulBean.getCoEmpDes());           
        }        
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND B.ES_ELI = '0'");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7')");
        sql.append(" AND A.IN_OFICIO = '0'");    

        objectParam.put("pCoAnio", documentoRecepConsulBean.getNuAnn());           
        objectParam.put("pNuEmi", documentoRecepConsulBean.getNuEmi());   
        objectParam.put("pCoDepDes", documentoRecepConsulBean.getCoDepDes());   
        
        sql.append(" ORDER BY A.FE_EMI DESC");
        sql.append(") A ");
        sql.append("LIMIT 300");            
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoRecepConsulBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;          
    }    
    
    
    @Override
    public String getRutaReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean) {
        String vResult;
        boolean bBusqFeEmi=false;
        boolean bBusqFePendiente=false;
        StringBuffer prutaReporte =  new StringBuffer();
        prutaReporte.append(" B.NU_ANN = A.NU_ANN");
        prutaReporte.append(" AND B.NU_EMI = A.NU_EMI");
        prutaReporte.append(" AND C.NU_ANN = B.NU_ANN");
        prutaReporte.append(" AND C.NU_EMI = B.NU_EMI");        

        try {
            String pNUAnn = buscarDocumentoRecepConsulBean.getCoAnnio();
            if (!(buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
                // Parametros Basicos
                prutaReporte.append(" AND A.NU_ANN = '").append(pNUAnn).append("'");
            }
            // Parametros Basicos 
            prutaReporte.append(" AND A.ES_ELI = '0'");
            prutaReporte.append(" AND B.ES_ELI = '0'");          
            prutaReporte.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7')");
            prutaReporte.append(" AND A.IN_OFICIO = '0'");  

            String pTipoBusqueda = buscarDocumentoRecepConsulBean.getTipoBusqueda();   
            
            if (buscarDocumentoRecepConsulBean.getCoEmpDestino()!=null && buscarDocumentoRecepConsulBean.getCoEmpDestino().trim().length()>0 && pTipoBusqueda.equals("0")){
                prutaReporte.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoRecepConsulBean.getCoEmpDestino()).append("'");
            }else {    
                if(buscarDocumentoRecepConsulBean.getTiAcceso().equals("1")){
                    prutaReporte.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoRecepConsulBean.getCoEmpleado()).append("'");
                }else if(buscarDocumentoRecepConsulBean.getTiAcceso().equals("2")){
                    prutaReporte.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoRecepConsulBean.getCoEmpleado()).append("'");
                }
            }            
            
           
            //Filtro
            if(pTipoBusqueda.equals("0")){
                if (buscarDocumentoRecepConsulBean.getCoTipoDocAdm()!= null && buscarDocumentoRecepConsulBean.getCoTipoDocAdm().trim().length()>0){
                   prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoRecepConsulBean.getCoTipoDocAdm()).append("'");
                }
                if (buscarDocumentoRecepConsulBean.getEstadoDoc()!= null && buscarDocumentoRecepConsulBean.getEstadoDoc().trim().length()>0){
                    String estadoDoc=buscarDocumentoRecepConsulBean.getEstadoDoc();
                    if(estadoDoc.equals("P")){

                        bBusqFePendiente=true;
                         prutaReporte.append(" AND B.ES_DOC_REC in ('0','1') ");
                    }
                    else{
                     prutaReporte.append(" AND B.ES_DOC_REC = '"+estadoDoc+"' ");
                      
                    }

                     if(estadoDoc.equals("0")||estadoDoc.equals("P")){
                         bBusqFeEmi=true;
                     }
                     
                }
                else {
                bBusqFeEmi=true;
                }
                if (buscarDocumentoRecepConsulBean.getPrioridadDoc()!= null && buscarDocumentoRecepConsulBean.getPrioridadDoc().trim().length()>0){
                   prutaReporte.append(" AND B.CO_PRI = '").append(buscarDocumentoRecepConsulBean.getPrioridadDoc()).append("'");
                }
                if (buscarDocumentoRecepConsulBean.getCoDepRemite()!= null && buscarDocumentoRecepConsulBean.getCoDepRemite().trim().length()>0){
                   prutaReporte.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = '").append(buscarDocumentoRecepConsulBean.getCoDepRemite()).append("'");
                }
                if (buscarDocumentoRecepConsulBean.getCoDepDestino()!= null && buscarDocumentoRecepConsulBean.getCoDepDestino().trim().length()>0){
                   prutaReporte.append(" AND B.CO_DEP_DES = '").append(buscarDocumentoRecepConsulBean.getCoDepDestino()).append("'");
                }
                if (buscarDocumentoRecepConsulBean.getIdEtiqueta() != null && buscarDocumentoRecepConsulBean.getIdEtiqueta().trim().length() > 0) {
                    prutaReporte.append(" AND B.CO_ETIQUETA_REC = '").append(buscarDocumentoRecepConsulBean.getIdEtiqueta()).append("'");
                }                
                if(buscarDocumentoRecepConsulBean.getEsFiltroFecha()!= null && 
                   (buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("3"))){
                    String vFeEmiIni = buscarDocumentoRecepConsulBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoRecepConsulBean.getFeEmiFin();       
                    if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                       vFeEmiFin!= null && vFeEmiFin.trim().length()>0){
                        if(bBusqFeEmi){
                              prutaReporte.append(" AND A.FE_EMI between to_timestamp('").append(vFeEmiIni).append("','dd/mm/yyyy')").append(" AND to_timestamp('").append(vFeEmiFin)
                                        .append("','dd/mm/yyyy hh24:mi:ss')");
                        }else{
                              prutaReporte.append(" AND B.FE_REC_DOC between to_timestamp('").append(vFeEmiIni).append("','dd/mm/yyyy')").append(" AND to_timestamp('").append(vFeEmiFin)
                                        .append("','dd/mm/yyyy hh24:mi:ss')");
                        }  
                        if(bBusqFePendiente){
                            prutaReporte.append(" ");
                            prutaReporte.append("AND ( (B.ES_DOC_REC='0' AND A.FE_EMI between to_timestamp('"+vFeEmiIni+"' ,'dd/mm/yyyy') AND to_timestamp('"+vFeEmiFin+"' ,'dd/mm/yyyy hh24:mi:ss')  ) ");                         
                            prutaReporte.append(" OR (B.ES_DOC_REC='1' AND B.FE_REC_DOC between to_timestamp('"+vFeEmiIni+"' ,'dd/mm/yyyy') AND to_timestamp('"+vFeEmiFin+"' ,'dd/mm/yyyy hh24:mi:ss') )) ");                         
                        }   
                        
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

    @Override
    public List<DocumentoRecepConsulBean> getListaReporteBusqueda(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean) {
        List lista=null ;
        boolean bBusqFeEmi=false;
        boolean bBusqFiltro=false;
        boolean bBusqFePendiente=false;
        Map<String, Object> objectParam = new HashMap<String, Object>();
        
        StringBuffer prutaReporte =  new StringBuffer();
        
        prutaReporte.append("SELECT    B.NU_COR_DES,");
	prutaReporte.append("TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA, ");
	prutaReporte.append("IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI, ");
	prutaReporte.append("IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
	prutaReporte.append("C.NU_DOC, ");
	prutaReporte.append("CASE B.TI_DES ");
        prutaReporte.append("WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES) ");
        prutaReporte.append("WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES) ");
        prutaReporte.append("WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES) ");
        prutaReporte.append("WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES) ");
	prutaReporte.append("END AS DE_EMP_DES, ");
	prutaReporte.append("A.DE_ASU, ");
	prutaReporte.append("C.NU_EXPEDIENTE, ");
	prutaReporte.append("IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES, ");
	prutaReporte.append("TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY') FE_REC_CORTA, ");
	prutaReporte.append("(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM ");
	prutaReporte.append("FROM IDOSGD.RHTM_PER_EMPLEADOS ");
	prutaReporte.append("WHERE CEMP_CODEMP = A.CO_EMP_RES) DE_EMP_RES, ");
	prutaReporte.append("IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(B.CO_MOT) DE_MOTIVO, ");
        prutaReporte.append("IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD (B.CO_PRI) DE_PRIORIDAD, ");
	prutaReporte.append("(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM ");        
	prutaReporte.append("FROM IDOSGD.RHTM_PER_EMPLEADOS ");
	prutaReporte.append("WHERE CEMP_CODEMP = B.CO_EMP_REC) DE_EMP_REC, ");
        prutaReporte.append("A.NU_DIA_ATE ");
        prutaReporte.append("FROM IDOSGD.TDTV_REMITOS A, ");
        prutaReporte.append("IDOSGD.TDTV_DESTINOS B, ");
        prutaReporte.append("IDOSGD.TDTX_REMITOS_RESUMEN C ");
        prutaReporte.append("WHERE ");
        
        prutaReporte.append(" B.NU_ANN = A.NU_ANN ");
        prutaReporte.append(" AND B.NU_EMI = A.NU_EMI ");
        prutaReporte.append(" AND C.NU_ANN = B.NU_ANN ");
        prutaReporte.append(" AND C.NU_EMI = B.NU_EMI ");        

        try {
            String pNUAnn = buscarDocumentoRecepConsulBean.getCoAnnio();
            if (!(buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
                // Parametros Basicos
                //prutaReporte.append(" AND A.NU_ANN = '").append(pNUAnn).append("'");
                prutaReporte.append(" AND A.NU_ANN = :pNuAnn ");
                objectParam.put("pNuAnn", pNUAnn); 
            }
            // Parametros Basicos
            //prutaReporte.append(" AND B.CO_DEP_DES = '").append(buscarDocumentoRecepConsulBean.getCoDependencia()).append("'");
            prutaReporte.append(" AND A.ES_ELI = '0' ");
            prutaReporte.append(" AND B.ES_ELI = '0' ");          
            prutaReporte.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7','8') ");
            prutaReporte.append(" AND A.IN_OFICIO = '0' ");  

            String pTipoBusqueda = buscarDocumentoRecepConsulBean.getTipoBusqueda();   
            
            if (buscarDocumentoRecepConsulBean.getCoEmpDestino()!=null && buscarDocumentoRecepConsulBean.getCoEmpDestino().trim().length()>0 && pTipoBusqueda.equals("0")){
                //prutaReporte.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoRecepConsulBean.getCoEmpDestino()).append("'");
                prutaReporte.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
                objectParam.put("pcoEmpDes", buscarDocumentoRecepConsulBean.getCoEmpDestino());
            }else {    
                if(buscarDocumentoRecepConsulBean.getTiAcceso().equals("1")){
                    //prutaReporte.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoRecepConsulBean.getCoEmpleado()).append("'");
                    prutaReporte.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
                    objectParam.put("pcoEmpDes", buscarDocumentoRecepConsulBean.getCoEmpleado());
                }else if(buscarDocumentoRecepConsulBean.getTiAcceso().equals("2")){
                    //prutaReporte.append(" AND B.CO_EMP_DES = '").append(buscarDocumentoRecepConsulBean.getCoEmpleado()).append("'");
                    prutaReporte.append(" AND B.CO_EMP_DES = :pcoEmpDes  ");
                    objectParam.put("pcoEmpDes", buscarDocumentoRecepConsulBean.getCoEmpleado());
                }
            }            
            
           
            //Filtro
            if(pTipoBusqueda.equals("0")|| bBusqFiltro){
                if (buscarDocumentoRecepConsulBean.getCoTipoDocAdm()!= null && buscarDocumentoRecepConsulBean.getCoTipoDocAdm().trim().length()>0){
                   //prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoRecepConsulBean.getCoTipoDocAdm()).append("'");
                    prutaReporte.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                    objectParam.put("pCoDocEmi", buscarDocumentoRecepConsulBean.getCoTipoDocAdm());
                }
                if (buscarDocumentoRecepConsulBean.getEstadoDoc()!= null && buscarDocumentoRecepConsulBean.getEstadoDoc().trim().length()>0){
                    String estadoDoc=buscarDocumentoRecepConsulBean.getEstadoDoc();
                     if(estadoDoc.equals("P")){

                        bBusqFePendiente=true;
                         prutaReporte.append(" AND B.ES_DOC_REC in ('0','1') ");
                    }
                    else{
                        prutaReporte.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                        objectParam.put("pEsDocRec", estadoDoc);
                    }
                     
                   
                    if(estadoDoc.equals("0")||estadoDoc.equals("P")){
                        bBusqFeEmi=true;
                    }
                } 
                else {
                    bBusqFeEmi=true;
                }
                if (buscarDocumentoRecepConsulBean.getPrioridadDoc()!= null && buscarDocumentoRecepConsulBean.getPrioridadDoc().trim().length()>0){ 
                    prutaReporte.append(" AND B.CO_PRI = :pCoPrioridad ");
                    objectParam.put("pCoPrioridad", buscarDocumentoRecepConsulBean.getPrioridadDoc());
                }
                if (buscarDocumentoRecepConsulBean.getCoDepRemite()!= null && buscarDocumentoRecepConsulBean.getCoDepRemite().trim().length()>0){ 
                    prutaReporte.append(" AND ISNULL(C.CO_DEP_EMI_REF, '0') = :pTiEmiRef ");
                    objectParam.put("pTiEmiRef", buscarDocumentoRecepConsulBean.getCoDepRemite());
                }
                if (buscarDocumentoRecepConsulBean.getCoDepDestino()!= null && buscarDocumentoRecepConsulBean.getCoDepDestino().trim().length()>0){ 
                    prutaReporte.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                    objectParam.put("pcoDepDes", buscarDocumentoRecepConsulBean.getCoDepDestino());
                }
                if (buscarDocumentoRecepConsulBean.getIdEtiqueta() != null && buscarDocumentoRecepConsulBean.getIdEtiqueta().trim().length() > 0) { 
                    prutaReporte.append(" AND B.CO_ETIQUETA_REC = :pcoEtiquetaRec ");
                    objectParam.put("pcoEtiquetaRec", buscarDocumentoRecepConsulBean.getIdEtiqueta());
                }                
                if(buscarDocumentoRecepConsulBean.getEsFiltroFecha()!= null && 
                   (buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("1") || buscarDocumentoRecepConsulBean.getEsFiltroFecha().equals("3"))){
                    String vFeEmiIni = buscarDocumentoRecepConsulBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoRecepConsulBean.getFeEmiFin();       
                    if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                       vFeEmiFin!= null && vFeEmiFin.trim().length()>0){
                        if(bBusqFeEmi){                        
                           prutaReporte.append(" AND A.FE_EMI between to_timestamp(:pFeEmiIni,'dd/mm/yyyy') AND to_timestamp(:pFeEmiFin,'dd/mm/yyyy hh24:mi:ss')");
                        }else{                           
                            prutaReporte.append(" AND B.FE_REC_DOC between to_timestamp(:pFeEmiIni,'dd/mm/yyyy') AND to_timestamp(:pFeEmiFin,'dd/mm/yyyy hh24:mi:ss')");
                        }   
                        if(bBusqFePendiente){
                            prutaReporte.append(" ");
                          prutaReporte.append("AND ( (B.ES_DOC_REC='0' AND A.FE_EMI between to_timestamp(:pFeEmiIni, 'dd/mm/yyyy') AND to_timestamp(:pFeEmiFin, 'dd/mm/yyyy hh24:mi:ss') ) ");                         
                          prutaReporte.append(" OR (B.ES_DOC_REC='1' AND B.FE_REC_DOC between to_timestamp(:pFeEmiIni, 'dd/mm/yyyy') AND to_timestamp(:pFeEmiFin, 'dd/mm/yyyy hh24:mi:ss'))) ");                         
                        }
                        
                        objectParam.put("pFeEmiIni", vFeEmiIni);
                        objectParam.put("pFeEmiFin", vFeEmiFin); 
                    }
                }            
            }  
            
            if (pTipoBusqueda.equals("1")) {
                if(!bBusqFiltro){
                    if (buscarDocumentoRecepConsulBean.getCoDependencia()!= null && buscarDocumentoRecepConsulBean.getCoDependencia().trim().length()>0){
                       prutaReporte.append(" AND B.CO_DEP_DES = :pcoDepDes ");
                       objectParam.put("pcoDepDes", buscarDocumentoRecepConsulBean.getCoDependencia());
                    }                 
                }
                if (buscarDocumentoRecepConsulBean.getBusNumDoc()!= null && buscarDocumentoRecepConsulBean.getBusNumDoc().trim().length()>1){
                   prutaReporte.append(" AND A.NU_DOC_EMI LIKE '%'||:pnuDocEmi||'%' ");
                   objectParam.put("pnuDocEmi", buscarDocumentoRecepConsulBean.getBusNumDoc());
                }

                if (buscarDocumentoRecepConsulBean.getBusNumExpediente()!= null && buscarDocumentoRecepConsulBean.getBusNumExpediente().trim().length()>1){
                   prutaReporte.append(" AND C.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                   objectParam.put("pnuExpediente", buscarDocumentoRecepConsulBean.getBusNumExpediente());
                }
                // Busqueda del Asunto
                if (buscarDocumentoRecepConsulBean.getBusAsunto()!= null && buscarDocumentoRecepConsulBean.getBusAsunto().trim().length()>1){ 
                   prutaReporte.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
                   objectParam.put("pDeAsunto", buscarDocumentoRecepConsulBean.getBusAsunto());                                               
                }
                if (buscarDocumentoRecepConsulBean.getBusNumDocRef() != null && buscarDocumentoRecepConsulBean.getBusNumDocRef().trim().length() > 1) {
                    //busqeda referencia
                     prutaReporte.append("  AND A.NU_EMI IN( ");
                    prutaReporte.append("   SELECT D.NU_EMI ");
                    prutaReporte.append("   FROM IDOSGD.TDTR_ARBOL_SEG D ");
                    prutaReporte.append("   INNER JOIN IDOSGD.TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                    prutaReporte.append("   WHERE R.NU_ANN='"+buscarDocumentoRecepConsulBean.getBusCoAnnio()+"'   ");
                    if (buscarDocumentoRecepConsulBean.getBusCodTipoDocRef() != null && buscarDocumentoRecepConsulBean.getBusCodTipoDocRef().trim().length() > 1) {
                    prutaReporte.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoRecepConsulBean.getBusCodTipoDocRef()+"' ");
                    }
                    if (buscarDocumentoRecepConsulBean.getBusCodDepEmiRef() != null && buscarDocumentoRecepConsulBean.getBusCodDepEmiRef().trim().length() > 1) {
                    prutaReporte.append(" AND R.CO_DEP_EMI='"+buscarDocumentoRecepConsulBean.getBusCodDepEmiRef()+"' ");
                    }
                    prutaReporte.append("   AND ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoRecepConsulBean.getBusNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoRecepConsulBean.getBusNumDocRef()+"'||'%'))");
                    prutaReporte.append(" )");
                }                
            }
            prutaReporte.append(" ORDER BY A.FE_EMI DESC ");

            try {
                lista = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoRecepConsulBean.class));
            } catch (EmptyResultDataAccessException e) {
                lista = null;
            }
            
        } catch (Exception ex) {            
            ex.printStackTrace();
        }
                
        return lista;
    }
}
