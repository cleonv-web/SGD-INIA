/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaDocVoBoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoQuery;
import pe.gob.onpe.tramitedoc.web.util.BusquedaTextual;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Repository("consultaDocVoBoDao")
public class ConsultaDocVoBoDaoImp extends SimpleJdbcDaoQuery implements ConsultaDocVoBoDao{

    @Override
    public List<DocumentoVoBoBean> getLsDocsVoBo(BuscarDocumentoVoBoBean bDocVoBo) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoVoBoBean> list;

        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");
        sql.append("	SELECT RE.NU_ANN,RE.NU_EMI,RE.NU_COR_EMI,TO_CHAR(RE.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,PK_SGD_DESCRIPCION.DE_EMI_REF(RE.NU_ANN, RE.NU_EMI) DE_EMI_REF,");
        sql.append("	(SELECT CDOC_DESDOC FROM SI_MAE_TIPO_DOC WHERE CDOC_TIPDOC = RE.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,RR.NU_DOC,UPPER(SUBSTR(RE.DE_ASU,0,399)) DE_ASU,");
        sql.append("	(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = VB.CO_EMP_VB)  DE_EMP_VB,");
        sql.append("	(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = RE.CO_EMP_RES) DE_EMP_ELABORA,");
        sql.append("	RR.NU_EXPEDIENTE NRO_EXPEDIENTE,");
        sql.append("	(SELECT DE_EST FROM TDTR_ESTADOS WHERE CO_EST || DE_TAB = DECODE(VB.IN_VB,'B','0',VB.IN_VB) || 'TDTV_PERSONAL_VB') DE_ES_DOC_EMI,");
        sql.append("	RR.IN_EXISTE_DOC EXISTE_DOC,DECODE(NVL(RR.TI_EMI_REF,'0')||NVL(RR.IN_EXISTE_ANEXO,'2'),'00',0,'02',0,1) EXISTE_ANEXO");
        sql.append("	FROM TDTV_REMITOS RE,TDTV_PERSONAL_VB VB,TDTX_REMITOS_RESUMEN RR");
        sql.append("	WHERE RE.NU_ANN=VB.NU_ANN");
        sql.append("	AND RE.NU_EMI=VB.NU_EMI");
        sql.append("	AND RR.NU_ANN=RE.NU_ANN");
        sql.append("	AND RR.NU_EMI=RE.NU_EMI");
        //yual
      //  sql.append("	AND RR.TI_EMI='01'");
       // sql.append("	AND RE.TI_EMI='01'");
        sql.append("	AND RE.ES_DOC_EMI NOT IN('5','9')");
        sql.append("	AND RE.ES_ELI='0'");
//yual
//        sql.append("	AND RE.CO_GRU='1'");
        

//sql.append("	AND VB.IN_VB='0'");
        String pnuAnn = bDocVoBo.getNuAnn();
        if (pnuAnn!=null&&pnuAnn.trim().length()==4) {
            sql.append(" AND RE.NU_ANN = :pnuAnn");
            objectParam.put("pnuAnn", pnuAnn);
        }
        sql.append(" AND VB.CO_DEP = :pCoDep");

        // Parametros Basicos
        objectParam.put("pCoDep", bDocVoBo.getCoDepUsu());

        String pTipoBusqueda = bDocVoBo.getTipBusqueda();
        if (pTipoBusqueda.equals("1")&&bDocVoBo.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }

        sql.append(" AND VB.CO_EMP_VB = :pcoEmpVobo ");
        objectParam.put("pcoEmpVobo", bDocVoBo.getCoEmpUsu());
        
        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (bDocVoBo.getTipDoc()!= null && bDocVoBo.getTipDoc().trim().length() > 0) {
                sql.append(" AND RE.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", bDocVoBo.getTipDoc());
            }
            if (bDocVoBo.getEsDoc()!= null && bDocVoBo.getEsDoc().trim().length() > 0) {
                sql.append(" AND (VB.IN_VB = :pEsDocEmi ");
                String esDoc=bDocVoBo.getEsDoc();
                if(esDoc.equals("0")){
                   sql.append(" OR VB.IN_VB = 'B') "); 
                }else{
                   sql.append(") ");  
                }
                objectParam.put("pEsDocEmi", bDocVoBo.getEsDoc());
            }else{
                sOrdenList=" DESC";
            }
            if (bDocVoBo.getCoPrioridadDoc()!= null && bDocVoBo.getCoPrioridadDoc().trim().length() > 0) {
                sql.append(" AND RR.CO_PRIORIDAD = :pCoPrioridad ");
                objectParam.put("pCoPrioridad", bDocVoBo.getCoPrioridadDoc());
            }
            if (bDocVoBo.getCoRefOri()!= null && bDocVoBo.getCoRefOri().trim().length() > 0) {
                sql.append(" AND INSTR(RR.TI_EMI_REF, :pTiEmiRef) > 0 ");
                objectParam.put("pTiEmiRef", bDocVoBo.getCoRefOri());
            }
            if (bDocVoBo.getCoEmpElabora()!= null && bDocVoBo.getCoEmpElabora().trim().length() > 0) {
                sql.append(" AND RE.CO_EMP_RES = :pcoEmpEla ");
                objectParam.put("pcoEmpEla", bDocVoBo.getCoEmpElabora());                            
            }
            String vFeEmiIni = bDocVoBo.getFeIni();
            String vFeEmiFin = bDocVoBo.getFeFin();
            if (vFeEmiIni != null && vFeEmiIni.trim().length() == 10
                    && vFeEmiFin != null && vFeEmiFin.trim().length() == 10) {
                sql.append(" AND RE.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + 0.99999");
                objectParam.put("pFeEmiIni", vFeEmiIni);
                objectParam.put("pFeEmiFin", vFeEmiFin);
            }
        }

        //Busqueda
        if (pTipoBusqueda.equals("1")) {
            if (bDocVoBo.getNuCorEmi()!= null && bDocVoBo.getNuCorEmi().trim().length() > 0) {
                sql.append(" AND RE.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", bDocVoBo.getNuCorEmi());
            }

            if (bDocVoBo.getNuDoc() != null && bDocVoBo.getNuDoc().trim().length() > 1) {
                sql.append(" AND RR.NU_DOC LIKE ''||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", bDocVoBo.getNuDoc());
            }

            if (bDocVoBo.getNroExp() != null && bDocVoBo.getNroExp().trim().length() > 1) {
                sql.append(" AND RR.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", bDocVoBo.getNroExp());
            }
             
            // Busqueda del Asunto
            //bDocVoBo.setAsunto(Utilidades.fn_getCleanTextLenGreathree(Utilidades.fn_getCleanTextExpReg(bDocVoBo.getAsunto())));            
            if (bDocVoBo.getAsunto()!= null && bDocVoBo.getAsunto().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '").append(BusquedaTextual.getContextValue(bDocVoBo.getAsunto())).append("', 1 ) > 1 ");
                 sql.append(" AND UPPER(RE.DE_ASU) LIKE '%'||:pDeAsunto ||'%' ");
                objectParam.put("pDeAsunto", bDocVoBo.getAsunto());
            }
        }
        sql.append(" ORDER BY RE.FE_EMI").append(sOrdenList);
        sql.append(") A ");
        sql.append("WHERE ROWNUM < 101");


        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoVoBoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public DocumentoVoBoBean getDocumentoVoBo(String pnuAnn, String pnuEmi, String coDep, String coEmpVb) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT \n" +
                    "RE.NU_ANN, \n" +
                    "RE.NU_EMI,\n" +
                    "RE.NU_COR_EMI,\n" +
                    "PK_SGD_DESCRIPCION.DE_DEPENDENCIA(RE.CO_DEP_EMI) DE_DEP_EMI,\n" +
                    "(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = RE.CO_EMP_EMI) DE_EMP_EMI,\n" +
                    "(SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = RE.CO_EMP_RES) DE_EMP_ELABORA,\n" +
                    "TO_CHAR(RE.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n" +
                    "PK_SGD_DESCRIPCION.DE_DOCUMENTO(RE.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
                    "DECODE (RE.TI_EMI,'01', NVL(RE.NU_DOC_EMI,' ') || '-' || RE.NU_ANN || '-' || RE.DE_DOC_SIG,\n" +
                    "'05', NVL(RE.NU_DOC_EMI,' ') || '-' || RE.NU_ANN || '-' || RE.DE_DOC_SIG,NVL(RE.DE_DOC_SIG,'S/N')) NU_DOC,\n" +
                    "DECODE(VB.IN_VB,'B','0',VB.IN_VB) ES_DOC_EMI,(SELECT DE_EST FROM TDTR_ESTADOS WHERE CO_EST || DE_TAB = DECODE(VB.IN_VB,'B','0',VB.IN_VB) || 'TDTV_PERSONAL_VB') DE_ES_DOC_EMI,\n"+
                    "RE.NU_DIA_ATE, \n" +
                    "RE.DE_ASU, \n" +
                    "EX.NU_EXPEDIENTE NRO_EXPEDIENTE,\n" +
                    "TO_CHAR(EX.FE_EXP,'DD/MM/YYYY') FE_EXP_CORTA,\n" +
                    "PK_SGD_DESCRIPCION.DE_PROCESO_EXP(EX.CO_PROCESO) DE_PROCESO_EXP,\n" +
                    "RE.TI_CAP,\n" +
                    "VB.OBS_VB DE_OBS\n" +
                    "FROM TDTV_REMITOS RE LEFT JOIN TDTC_EXPEDIENTE EX\n" +
                    "ON RE.NU_ANN_EXP=EX.NU_ANN_EXP AND RE.NU_SEC_EXP=EX.NU_SEC_EXP,TDTV_PERSONAL_VB VB\n" +
                    "WHERE RE.NU_ANN=? AND RE.NU_EMI=?\n" +
                    "AND VB.NU_ANN=? AND VB.NU_EMI=?\n" +
                    "AND VB.CO_DEP=? AND VB.CO_EMP_VB=?\n" +                
                    "AND RE.ES_DOC_EMI NOT IN('5','9')\n" +
                    "AND RE.ES_ELI='0'\n" +
                    "AND RE.CO_GRU='1'");

        DocumentoVoBoBean docVobo=null;
        try {
            docVobo = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoVoBoBean.class),
                    new Object[]{pnuAnn, pnuEmi, pnuAnn, pnuEmi, coDep, coEmpVb});
        } catch (EmptyResultDataAccessException e) {
            docVobo = null;
        } catch (Exception e) {
            System.out.println("nu_ann="+pnuAnn+","+"nu_emi="+pnuEmi);
            e.printStackTrace();
        }
        return docVobo;
    }
}
