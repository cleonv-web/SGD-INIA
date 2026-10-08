/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocExtRecSeguiEstBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.dao.SeguiEstRecDocExtDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

/**
 *
 * @author ECueva
 */
@Repository("seguiEstRecDocExtDao")
public class SeguiEstRecDocExtDaoImp extends SimpleJdbcDaoBase implements SeguiEstRecDocExtDao{
    @Autowired
    private ApplicationProperties applicationProperties;
        
    @Override
    public List<DocExtRecSeguiEstBean> getLsDocExtRecSegui(DocExtRecSeguiEstBean docBuscar) {
        StringBuffer sql = new StringBuffer(); 
        boolean bBusqFiltro=false;
        Map<String, Object> objectParam = new HashMap<String, Object>();
        
        List<DocExtRecSeguiEstBean> list = new ArrayList<DocExtRecSeguiEstBean>();

        sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP(X.NU_ANN, X.NU_EMI) DE_ORI_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
         
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
        
        sql.append(" TO_CHAR((CAST(X.FE_EMI as date) + IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_MAS(CAST(X.FE_EMI as date),X.NU_DIA_ATE)),'DD/MM/YYYY') F_LIMITE_CORTA,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(X.CO_MOT) DE_MOTIVO,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_REC");         
        sql.append(" FROM ( ");        
        sql.append(" SELECT R.NU_ANN,R.NU_EMI,D.NU_DES,R.NU_COR_EMI,TO_CHAR(R.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,");
        sql.append(" RR.NU_EXPEDIENTE,R.CO_TIP_DOC_ADM,R.FE_EMI,R.ES_DOC_EMI,D.CO_MOT,R.CO_EMP_RES,");
        sql.append(" COALESCE(RR.NU_DOC,'S/N') NU_DOC,D.TI_DES,D.CO_DEP_DES,D.CO_EMP_DES,D.NU_RUC_DES,D.NU_DNI_DES,D.CO_OTR_ORI_DES,");
        sql.append(" R.DE_ASU,R.NU_DIA_ATE,");
        sql.append(" CASE");
        sql.append(" WHEN R.NU_DIA_ATE > 0");
        sql.append(" AND (CAST(E.FE_FINALIZA as date) - CAST(now() as date)) <= 2");
        sql.append(" AND (CAST(E.FE_FINALIZA as date) - CAST(now() as date)) > 0");
        sql.append(" AND D.FE_ATE_DOC IS NULL");
        sql.append(" AND D.FE_ARC_DOC IS NULL");
        sql.append(" AND M.IN_DIA = '0'");
        sql.append(" THEN '1'");
        sql.append(" WHEN R.NU_DIA_ATE > 0");
        sql.append(" AND CAST(now() as date) > CAST(E.FE_FINALIZA as date)");
        sql.append(" AND D.FE_ATE_DOC IS NULL");
        sql.append(" AND D.FE_ARC_DOC IS NULL");
        sql.append(" AND M.IN_DIA = '0'");
        sql.append(" THEN '2'");
        sql.append(" WHEN R.NU_DIA_ATE > 0");
        sql.append(" AND CAST(now() as date) = CAST(E.FE_FINALIZA as date)");
        sql.append(" AND D.FE_ATE_DOC IS NULL");
        sql.append(" AND D.FE_ARC_DOC IS NULL");
        sql.append(" AND M.IN_DIA = '0'");
        sql.append(" THEN '3'");
        sql.append(" WHEN R.NU_DIA_ATE > 0");
        sql.append(" AND (CAST(E.FE_FINALIZA as date) - CAST(now() as date)) > 2");
        sql.append(" AND D.FE_ATE_DOC IS NULL");
        sql.append(" AND D.FE_ARC_DOC IS NULL");
        sql.append(" AND M.IN_DIA = '0'");
        sql.append(" THEN '4'");
        sql.append(" WHEN D.FE_ATE_DOC IS NOT NULL OR D.FE_ARC_DOC IS NOT NULL");
        sql.append(" THEN '5'");
        sql.append(" WHEN R.NU_DIA_ATE = 0 OR M.IN_DIA = '0'");
        sql.append(" THEN '0'");
        sql.append(" END CO_EST_VEN,");
        sql.append(" CASE");
        sql.append(" WHEN R.NU_DIA_ATE > 0");
        sql.append(" AND CAST(now() as date) > CAST(E.FE_FINALIZA as date)");
        sql.append(" AND D.FE_ATE_DOC IS NULL");
        sql.append(" AND D.FE_ARC_DOC IS NULL");
        sql.append(" AND M.IN_DIA = '0'");
        sql.append(" THEN IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_TRA(E.FE_FINALIZA, CAST(now() as date))");
        sql.append(" ELSE 0");
        sql.append(" END NU_DIA_EXC,");
        sql.append(" TO_CHAR(D.FE_ARC_DOC,'DD/MM/YYYY') FE_ARC_DOC_CORTA,");
        sql.append(" TO_CHAR(D.FE_ATE_DOC,'DD/MM/YYYY') FE_ATE_DOC_CORTA,");
        sql.append(" TO_CHAR(D.FE_REC_DOC,'DD/MM/YYYY') FE_REC_DOC_CORTA,"); 
        sql.append(" RR.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append("CASE\n" +
                "WHEN COALESCE(RR.TI_EMI_REF,'0')||COALESCE(RR.IN_EXISTE_ANEXO,'2') = '00' THEN\n" +
                "0 \n" +
                "WHEN COALESCE(RR.TI_EMI_REF,'0')||COALESCE(RR.IN_EXISTE_ANEXO,'2') = '02' THEN\n" +
                "0 \n" +
                "ELSE \n" +
                "1 \n" +
                "END AS EXISTE_ANEXO "
        );
        sql.append(" FROM IDOSGD.TDTV_REMITOS R, IDOSGD.TDTV_DESTINOS D, IDOSGD.TDTX_REMITOS_RESUMEN RR, IDOSGD.TDTR_MOTIVO M, IDOSGD.TDTC_EXPEDIENTE C,");
        sql.append(" (SELECT Y.NU_ANN, Y.NU_EMI, Y.FE_FINALIZA,IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_TRA (CAST(now() as date),Y.FE_FINALIZA) NU_DIA_FAL FROM (SELECT X.NU_ANN, X.NU_EMI,");
        sql.append(" CAST (X.FE_EMI as date)+ IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_MAS (CAST (X.FE_EMI as date),NU_DIA_ATE) AS FE_FINALIZA");
        sql.append(" FROM IDOSGD.TDTV_REMITOS X) Y) E");
        sql.append(" WHERE");
        sql.append(" D.NU_ANN=R.NU_ANN");
        sql.append(" AND D.NU_EMI=R.NU_EMI");
        sql.append(" AND RR.NU_ANN=R.NU_ANN");
        sql.append(" AND RR.NU_EMI=R.NU_EMI");
        sql.append(" AND E.NU_ANN=R.NU_ANN");
        sql.append(" AND E.NU_EMI=R.NU_EMI");
        sql.append(" AND M.CO_MOT=D.CO_MOT"); 
        sql.append(" AND C.NU_ANN_EXP=R.NU_ANN_EXP ");
        sql.append(" AND C.NU_SEC_EXP=R.NU_SEC_EXP ");
        
        String pnuAnn = docBuscar.getNuAnn();
        if(!(docBuscar.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))){
            sql.append(" AND R.NU_ANN = :pnuAnn");
            // Parametros Basicos
            objectParam.put("pnuAnn", pnuAnn);                
        }        

        sql.append(" AND R.CO_GRU='3'");
        sql.append(" AND R.ES_ELI='0'");
        sql.append(" AND D.ES_ELI='0'");
        sql.append(" AND R.ES_DOC_EMI NOT IN ('5', '9', '7')");
        sql.append(" AND R.TI_EMI IN ('02','03','04')");

        // Parametros Basicos
        //objectParam.put("pCoDepDes", buscarDocumentoSeguiEstRecBean.getCoDependencia());

        
        String pTipoBusqueda = docBuscar.getTipoBusqueda();
        if(pTipoBusqueda.equals("1") && docBuscar.isEsIncluyeFiltro()){
            bBusqFiltro=true;
        }         
        
        
        String auxTipoAcceso=docBuscar.getTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
        if(tiAcceso.equals("1")){//acceso personal
            sql.append(" AND R.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", docBuscar.getCoEmpleado());            
        }else {
            if(docBuscar.getInMesaPartes().equals("0") /*&& docBuscar.getInCambioEst().equals("0")*/){
                //bBusqLocal = true;
                sql.append(" AND R.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", docBuscar.getCoDependencia());                   
            }
        }
        /*else if(tiAcceso.equals("0")){//acceso total
            if(!docBuscar.getInCambioEst().equals("1")){
                sql.append(" AND R.CO_LOC_EMI = :pcoLocEmi");
                objectParam.put("pcoLocEmi", docBuscar.getCoLocal());
            }
        }*/        
//        if (buscarDocumentoSeguiEstRecBean.getCoEmpDestino()!=null && buscarDocumentoSeguiEstRecBean.getCoEmpDestino().trim().length()>0 && (pTipoBusqueda.equals("0") || bBusqFiltro)){
//            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
//            objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpDestino());
//        }else {    
//            if(buscarDocumentoSeguiEstRecBean.getTiAcceso().equals("1")){
//                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
//                objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpleado());
//            }else if(buscarDocumentoSeguiEstRecBean.getTiAcceso().equals("2")){
//                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes  ");
//                objectParam.put("pcoEmpDes", buscarDocumentoSeguiEstRecBean.getCoEmpleado());
//            }
//        }
        
       
        //Filtro
        if(pTipoBusqueda.equals("0") || bBusqFiltro){
            if (docBuscar.getCoTipDocAdm()!= null && docBuscar.getCoTipDocAdm().trim().length()>0){
               sql.append(" AND R.CO_TIP_DOC_ADM = :pcotipDocAdm ");
               objectParam.put("pcotipDocAdm", docBuscar.getCoTipDocAdm());
            }
            if (docBuscar.getCoEsDocEmi()!= null && docBuscar.getCoEsDocEmi().trim().length()>0){
               sql.append(" AND R.ES_DOC_EMI = :pesDocEmi ");
               objectParam.put("pesDocEmi", docBuscar.getCoEsDocEmi());
            }
//            if (buscarDocumentoSeguiEstRecBean.getPrioridadDoc()!= null && buscarDocumentoSeguiEstRecBean.getPrioridadDoc().trim().length()>0){
//               sql.append(" AND B.CO_PRI = :pCoPrioridad ");
//               objectParam.put("pCoPrioridad", buscarDocumentoSeguiEstRecBean.getPrioridadDoc());
//            }
            if (docBuscar.getTiEmi()!= null && docBuscar.getTiEmi().trim().length()>0){
               sql.append(" AND R.TI_EMI = :ptiEmi ");
               objectParam.put("ptiEmi", docBuscar.getTiEmi());
            }
            if (docBuscar.getCoDepEmi()!= null && docBuscar.getCoDepEmi().trim().length()>0){
               sql.append(" AND R.CO_DEP_EMI = :pcoDepEmi ");
               objectParam.put("pcoDepEmi", docBuscar.getCoDepEmi());
            }
            if (docBuscar.getCoDepDes() != null && docBuscar.getCoDepDes().trim().length() > 0) {
                sql.append(" AND STRPOS(RR.TI_EMI_DES, :pTiEmpPro) > 0 ");
                objectParam.put("pTiEmpPro", docBuscar.getCoDepDes());
            }
            if(docBuscar.getCoEmpRes()!=null&&docBuscar.getCoEmpRes().trim().length()>0){
                sql.append(" AND R.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", docBuscar.getCoEmpRes());
            }
            if(docBuscar.getCoProceso()!=null&&docBuscar.getCoProceso().trim().length()>0){
               if(docBuscar.getCoProceso().equals("CON_TUPA")){
                    sql.append(" AND RR.CO_PROCESO_EXP not in ('0000') "); 
                }else{
                    sql.append(" AND RR.CO_PROCESO_EXP = :pcoProcesoExp ");
                    objectParam.put("pcoProcesoExp", docBuscar.getCoProceso());   
                }            
            }
            String pcoEstVencimiento=docBuscar.getCoEstVen();
            if (pcoEstVencimiento!= null&&pcoEstVencimiento.trim().length()>0) {
                
                if ("1".equals(pcoEstVencimiento)) {
                    sql.append(" AND (CAST (E.fe_finaliza as date) - CAST(now() as date)) <= 2");
                    sql.append(" AND (CAST (E.fe_finaliza as date) - CAST(now() as date)) > 0");
                    sql.append(" AND d.fe_ate_doc IS NULL");
                    sql.append(" AND d.fe_arc_doc IS NULL");
                    sql.append(" AND m.in_dia = '0'");
                }
                if ("2".equals(pcoEstVencimiento)) {
                    sql.append(" AND CAST(now() as date) > CAST (E.fe_finaliza as date)");
                    sql.append(" AND d.fe_ate_doc IS NULL");
                    sql.append(" AND d.fe_arc_doc IS NULL");
                    sql.append(" AND m.in_dia = '0'");
                    sql.append(" AND r.NU_DIA_ATE <> 0");
                }
                if ("3".equals(pcoEstVencimiento)) {
                    //vence hoy
                    sql.append(" AND CAST(now() as date) = CAST (E.fe_finaliza as date)");
                    sql.append(" AND d.fe_ate_doc IS NULL");
                    sql.append(" AND d.fe_arc_doc IS NULL");
                    sql.append(" AND m.in_dia = '0'");
                    sql.append(" AND r.NU_DIA_ATE <> 0");
                }
                if ("4".equals(pcoEstVencimiento)) {
                    sql.append(" AND (CAST (E.fe_finaliza as date) - CAST(now() as date)) > 2");
                    sql.append(" AND d.fe_ate_doc IS NULL");
                    sql.append(" AND d.fe_arc_doc IS NULL");
                    sql.append(" AND m.in_dia = '0'");
                }
                if ("5".equals(pcoEstVencimiento)) {
                    sql.append(" AND d.fe_ate_doc is not null");
                    sql.append(" AND d.fe_rec_doc is not null");
                }
                if ("0".equals(pcoEstVencimiento)) {
                    sql.append(" and r.nu_dia_ate = 0");
                    sql.append(" and m.in_dia = '0'");
                    sql.append(" AND d.fe_ate_doc IS NULL");
                    sql.append(" AND d.fe_arc_doc IS NULL");
                }
                
                
                // '1'-- x vencerse
                // '2'--vencidos
                // '3'--vence hoy
                // '4'--Vence a futuro
                // '5'--Atendido
                // '0'--Normal - Sin vencimiento                
                
                
                
            }
            if(docBuscar.getEsFiltroFecha()!= null && 
               (docBuscar.getEsFiltroFecha().equals("1") || docBuscar.getEsFiltroFecha().equals("3"))){
                String vFeEmiIni = docBuscar.getFeEmiIni();
                String vFeEmiFin = docBuscar.getFeEmiFin();       
                if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                   vFeEmiFin!= null && vFeEmiFin.trim().length()>0){
//                    sql.append(" AND R.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + 0.99999");
                    sql.append(" AND R.FE_EMI between TO_DATE(:pFeEmiIni, 'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin, 'dd/mm/yyyy') + TIME '23:59:59' ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);                    
                }
            }            
        }    

        //if (pTipoBusqueda.equals("1"))
        //{
            /*if(!bBusqFiltro){
                if (docBuscar.getCoDepEmi()!= null && docBuscar.getCoDepEmi().trim().length()>0){
                    sql.append(" AND R.CO_DEP_EMI = :pcoDepEmi ");
                    objectParam.put("pcoDepEmi", docBuscar.getCoDepEmi());
                }                 
            }*/
            if (docBuscar.getNuCorEmi()!= null && docBuscar.getNuCorEmi().trim().length() > 0) {
                sql.append(" AND R.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", Integer.parseInt(docBuscar.getNuCorEmi()));
            }            
            if (docBuscar.getNuDoc()!= null && docBuscar.getNuDoc().trim().length() > 1) {
                sql.append(" AND RR.NU_DOC LIKE ''||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", docBuscar.getNuDoc());
            }

            if (docBuscar.getNuExpediente()!= null && docBuscar.getNuExpediente().trim().length()>1){
               sql.append(" AND RR.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
               objectParam.put("pnuExpediente", docBuscar.getNuExpediente());
            }

            // Busqueda del Asunto
            if (docBuscar.getDeAsu()!= null && docBuscar.getDeAsu().trim().length()>1){
                //sql.append(" AND CONTAINS(in_busca_texto, '").append(BusquedaTextual.getContextValue(docBuscar.getDeAsu())).append("', 1 ) > 1 ");
//               sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(docBuscar.getDeAsu())+"', 1 ) > 1 ");                                
               sql.append(" AND UPPER(R.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
               objectParam.put("pDeAsunto", docBuscar.getDeAsu());                                
            }
             if (docBuscar.getCoTipoExp() != null && !docBuscar.getCoTipoExp().equals("")) {
                sql.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                objectParam.put("codTipoExp", docBuscar.getCoTipoExp()); 
            }
            
            if (docBuscar.getCoOriDoc()!= null && !docBuscar.getCoOriDoc().equals("")) {
                sql.append(" AND R.CCOD_ORIGING = :coOriDoc");        
                objectParam.put("coOriDoc", docBuscar.getCoOriDoc()); 
            }
            
            if(docBuscar.getBusResultado().equals("1"))
            {
                if(docBuscar.getCoTipoPersona().equals("03")){
                sql.append(" AND R.NU_DNI_EMI = :pNumDni");
                objectParam.put("pNumDni", docBuscar.getBusNumDni());
                }
                else if(docBuscar.getCoTipoPersona().equals("02")){
                    sql.append(" AND R.NU_RUC_EMI = :pNumRuc");
                    objectParam.put("pNumRuc", docBuscar.getBusNumRuc());
                }
                else if(docBuscar.getCoTipoPersona().equals("04")){
                    sql.append(" AND R.CO_OTR_ORI_EMI = :pCoOtr");
                    objectParam.put("pCoOtr", docBuscar.getBusCoOtros());
                }
            }
            
        //}        
        
        sql.append(" ORDER BY R.FE_EMI DESC");
        sql.append(") X ");
//        sql.append("WHERE ROWNUM < 201");
        sql.append("LIMIT ").append(applicationProperties.getTopRegistrosConsultas());
        
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, new RowMapperDocExtSeguiEstado()/*BeanPropertyRowMapper.newInstance(DocExtRecSeguiEstBean.class)*/);
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;           
    }
    
    private class RowMapperDocExtSeguiEstado implements ParameterizedRowMapper<DocExtRecSeguiEstBean> {

        public DocExtRecSeguiEstBean mapRow(ResultSet rs, int i) throws SQLException {
            DocExtRecSeguiEstBean docExt = new DocExtRecSeguiEstBean();
            docExt.setNuAnn(rs.getString("NU_ANN"));
            docExt.setNuEmi(rs.getString("NU_EMI"));
            docExt.setNuDes(rs.getString("NU_DES"));
            docExt.setNuCorEmi(rs.getString("NU_COR_EMI"));
            docExt.setFeEmiCorta(rs.getString("FE_EMI_CORTA"));
            docExt.setDeOriEmi(rs.getString("DE_ORI_EMI"));
            docExt.setNuExpediente(rs.getString("NU_EXPEDIENTE"));
            docExt.setDeTipDocAdm(rs.getString("DE_TIP_DOC_ADM"));
            docExt.setNuDoc(rs.getString("NU_DOC"));
            docExt.setDeEmpDes(rs.getString("DE_EMP_DES"));
            docExt.setDeAsu(rs.getString("DE_ASU"));
            docExt.setNuDiaAte(rs.getString("NU_DIA_ATE"));
            docExt.setFeLimiteCorta(rs.getString("F_LIMITE_CORTA"));
            docExt.setNuDiaExc(rs.getString("NU_DIA_EXC"));
            docExt.setFeArcDocCorta(rs.getString("FE_ARC_DOC_CORTA"));
            docExt.setFeAteDocCorta(rs.getString("FE_ATE_DOC_CORTA"));
            docExt.setFeRecDocCorta(rs.getString("FE_REC_DOC_CORTA"));
            docExt.setDeEsDocEmi(rs.getString("DE_ES_DOC_EMI"));
            docExt.setDeMotivo(rs.getString("DE_MOTIVO"));
            docExt.setDeEmpRec(rs.getString("DE_EMP_REC"));
            docExt.setExisteDoc(rs.getString("EXISTE_DOC"));
            docExt.setExisteAnexo(rs.getString("EXISTE_ANEXO"));
            String pcoEstVen=rs.getString("CO_EST_VEN");
            docExt.setCoEstVen(pcoEstVen);
            if(pcoEstVen!=null&&pcoEstVen.trim().length()>0){
                if(pcoEstVen.equals("0")){
                    docExt.setDeEstVen("NORMAL");
                }else if(pcoEstVen.equals("1")){
                    docExt.setDeEstVen("PROXIMO A VENCER");
                }else if(pcoEstVen.equals("2")){
                    docExt.setDeEstVen("VENCIDO");
                }else if(pcoEstVen.equals("3")){
                    docExt.setDeEstVen("VENCE HOY");
                }else if(pcoEstVen.equals("4")){
                    docExt.setDeEstVen("POR VENCER");
                }else if(pcoEstVen.equals("5")){
                    docExt.setDeEstVen("ATENDIDO");
                }
            }            
            return docExt;
        }

    }
    
    @Override
    public DocExtRecSeguiEstBean getDocumentoExtSeguiBean(String nuAnn, String nuEmi, String nuDes){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,B.NU_DES,A.NU_COR_EMI,C.NU_EXPEDIENTE,TO_CHAR((SELECT FE_EXP FROM IDOSGD.TDTC_EXPEDIENTE WHERE NU_ANN_EXP=A.NU_ANN_EXP AND NU_SEC_EXP=A.NU_SEC_EXP),'DD/MM/YYYY') FE_EXP_CORTA,\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(C.CO_PROCESO_EXP) DE_PROCESO_EXP, IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI, IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_REC) DE_EMP_REC,\n" +
                "CASE\n" +
                "WHEN B.TI_DES = '01' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES)\n" +
                "WHEN B.TI_DES = '02' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES)\n" +
                "WHEN B.TI_DES = '03' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES)\n" +
                "WHEN B.TI_DES = '04' THEN\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES)\n" +
                "END AS DE_EMP_DES," +
                
                "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA (B.CO_DEP_DES) DE_DEP_DES, IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n" +
                "A.DE_ASU,A.NU_DIA_ATE, IDOSGD.PK_SGD_DESCRIPCION_MOTIVO (B.CO_MOT) DE_MOTIVO,B.DE_PRO DE_INDICACIONES, IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD (B.CO_PRI) DE_PRIORIDAD,\n" +
                "TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY HH24:MI:SS') FE_REC_DOC_CORTA,TO_CHAR(B.FE_ATE_DOC,'DD/MM/YYYY') FE_ATE_DOC_CORTA,\n" +
                "TO_CHAR(B.FE_ARC_DOC,'DD/MM/YYYY') FE_ARC_DOC_CORTA,\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_RES) DE_EMP_RES,A.NU_FOLIOS\n" +
                "FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B, IDOSGD.TDTX_REMITOS_RESUMEN C\n" +
                "WHERE A.ES_ELI='0' AND B.ES_ELI='0'\n" +
                "AND A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI\n" +
                "AND C.NU_ANN = A.NU_ANN AND C.NU_EMI = A.NU_EMI\n" +
                "AND A.ES_DOC_EMI NOT IN ('5', '9', '7')\n" +
                "AND A.CO_GRU='3'\n" +
                "and A.TI_EMI IN ('02','03','04')\n" +
                "AND B.NU_ANN=? AND B.NU_EMI=? AND B.NU_DES=CAST(? AS BIGINT)");

        DocExtRecSeguiEstBean docExt = new DocExtRecSeguiEstBean();
        try {
            docExt = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocExtRecSeguiEstBean.class),
                    new Object[]{nuAnn, nuEmi, nuDes});
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
        sql.append("SELECT A.NU_ANN,A.NU_EMI, IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
                    "C.NU_DOC de_doc_sig,\n" +
                    "C.NU_EXPEDIENTE,\n" +
                    "TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA\n" +
                    "FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTR_REFERENCIA B, IDOSGD.TDTX_REMITOS_RESUMEN C\n" +
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
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local," 
                + " CASE WHEN a.co_loc_des IS NOT NULL\n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(a.co_loc_des), 1, 100) \n"
                + " ELSE NULL END AS de_local,\n"
                + "a.co_dep_des co_dependencia," 
                + " CASE WHEN a.co_dep_des IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_des), 1, 100) \n"
                + " ELSE NULL END AS de_dependencia,\n"
                + "a.co_emp_des co_empleado," 
                + " CASE WHEN a.co_emp_des IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des), 1, 100) \n"
                + " ELSE NULL END AS de_empleado,\n"
                + "a.co_mot co_tramite," 
                + " CASE WHEN a.co_mot IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(a.co_mot), 1, 100) \n"
                + " ELSE NULL END AS de_tramite,\n"
                + "a.co_pri co_prioridad,TD_PK_DESCRIPCION.DE_PRIORIDAD(a.co_pri) de_prioridad,\n"
                + "a.de_pro de_indicaciones,\n"
                + "a.NU_RUC_DES nu_ruc," 
                + " CASE WHEN a.NU_RUC_DES IS NOT NULL \n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.NU_RUC_DES), 1, 100) \n"
                + " ELSE NULL END AS de_proveedor,\n"
                + "a.NU_DNI_DES nu_dni," 
                + " CASE WHEN a.NU_DNI_DES IS NOT NULL\n"
                + " THEN substr(IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.NU_DNI_DES), 1, 100) \n"
                + " ELSE NULL END AS de_ciudadano,\n"
                + "a.CO_OTR_ORI_DES co_otro_origen,\n" 
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
    public String getRutaReporte(DocExtRecSeguiEstBean docBuscar) {
        String vResult;
        StringBuffer sql = new StringBuffer();
        try {

            sql.append(" D.NU_ANN=R.NU_ANN");
            sql.append(" AND D.NU_EMI=R.NU_EMI");
            sql.append(" AND RR.NU_ANN=R.NU_ANN");
            sql.append(" AND RR.NU_EMI=R.NU_EMI");
            sql.append(" AND E.NU_ANN=R.NU_ANN");
            sql.append(" AND E.NU_EMI=R.NU_EMI");
            sql.append(" AND M.CO_MOT=D.CO_MOT");

            String pnuAnn = docBuscar.getNuAnn();
            if(!(docBuscar.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))){
                sql.append(" AND R.NU_ANN = '").append(pnuAnn).append("'");
            }        

            sql.append(" AND R.CO_GRU='3'");
            sql.append(" AND R.ES_ELI='0'");
            sql.append(" AND D.ES_ELI='0'");
            sql.append(" AND R.ES_DOC_EMI NOT IN ('5', '9', '7')");
            sql.append(" AND R.TI_EMI IN ('02','03','04')");

            String auxTipoAcceso=docBuscar.getTiAcceso();
            String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
            if(tiAcceso.equals("1")){//acceso personal
                sql.append(" AND R.CO_EMP_RES = '").append(docBuscar.getCoEmpleado()).append("'");
            }else {
                if(docBuscar.getInMesaPartes().equals("0") /*&& docBuscar.getInCambioEst().equals("0")*/){
                    //bBusqLocal = true;
                    sql.append(" AND R.CO_DEP = '").append(docBuscar.getCoDependencia()).append("'");
                }
            }            
            
        //Filtro
        if (docBuscar.getCoTipDocAdm() != null && docBuscar.getCoTipDocAdm().trim().length() > 0) {
            sql.append(" AND R.CO_TIP_DOC_ADM = '").append(docBuscar.getCoTipDocAdm()).append("'");
        }
        if (docBuscar.getCoEsDocEmi() != null && docBuscar.getCoEsDocEmi().trim().length() > 0) {
            sql.append(" AND R.ES_DOC_EMI = '").append(docBuscar.getCoEsDocEmi()).append("'");
        }
        if (docBuscar.getTiEmi() != null && docBuscar.getTiEmi().trim().length() > 0) {
            sql.append(" AND R.TI_EMI = '").append(docBuscar.getTiEmi()).append("'");
        }
        if (docBuscar.getCoDepDes() != null && docBuscar.getCoDepDes().trim().length() > 0) {
            sql.append(" AND STRPOS(RR.TI_EMI_DES,'").append(docBuscar.getCoDepDes()).append("') > 0");
        }
        if (docBuscar.getCoDepEmi() != null && docBuscar.getCoDepEmi().trim().length() > 0) {
            sql.append(" AND R.CO_DEP_EMI = '").append(docBuscar.getCoDepEmi()).append("'");
        }
        if (docBuscar.getCoEmpRes() != null && docBuscar.getCoEmpRes().trim().length() > 0) {
            sql.append(" AND R.CO_EMP_RES = '").append(docBuscar.getCoEmpRes()).append("'");
        }
        if (docBuscar.getCoProceso() != null && docBuscar.getCoProceso().trim().length() > 0) { 
             if(docBuscar.getCoProceso().equals("CON_TUPA")){
                sql.append(" AND RR.CO_PROCESO_EXP not in ('0000') "); 
            }else{
                sql.append(" AND RR.CO_PROCESO_EXP = '").append(docBuscar.getCoProceso()).append("'"); 
            }
             
        }        
        String pcoEstVencimiento=docBuscar.getCoEstVen();
        if (pcoEstVencimiento!= null && pcoEstVencimiento.trim().length() > 0) {
            if ("1".equals(pcoEstVencimiento)) {
                sql.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) <= 2");
                sql.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) > 0");
                sql.append(" AND d.fe_ate_doc IS NULL");
                sql.append(" AND d.fe_arc_doc IS NULL");
                sql.append(" AND m.in_dia = '0'");
            }
            if ("2".equals(pcoEstVencimiento)) {
                sql.append(" AND CAST(NOW() AS DATE) > CAST(E.fe_finaliza AS DATE)");
                sql.append(" AND d.fe_ate_doc IS NULL");
                sql.append(" AND d.fe_arc_doc IS NULL");
                sql.append(" AND m.in_dia = '0'");
                sql.append(" AND r.NU_DIA_ATE <> 0");
            }
            if ("3".equals(pcoEstVencimiento)) {
                //vence hoy
                sql.append(" AND CAST(NOW() AS DATE) = CAST(E.fe_finaliza AS DATE)");
                sql.append(" AND d.fe_ate_doc IS NULL");
                sql.append(" AND d.fe_arc_doc IS NULL");
                sql.append(" AND m.in_dia = '0'");
                sql.append(" AND r.NU_DIA_ATE >= 0");
            }
            if ("4".equals(pcoEstVencimiento)) {
                sql.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) > 2");
                sql.append(" AND d.fe_ate_doc IS NULL");
                sql.append(" AND d.fe_arc_doc IS NULL");
                sql.append(" AND m.in_dia = '0'");
            }
            if ("5".equals(pcoEstVencimiento)) {
                sql.append(" AND d.fe_ate_doc is not null");
                sql.append(" AND d.fe_rec_doc is not null");
            }
            if ("0".equals(pcoEstVencimiento)) {
                sql.append(" and r.nu_dia_ate = 0");
                sql.append(" and m.in_dia = '0'");
                sql.append(" AND d.fe_ate_doc IS NULL");
                sql.append(" AND d.fe_arc_doc IS NULL");
            }

            // '1'-- x vencerse
            // '2'--vencidos
            // '3'--vence hoy
            // '4'--Vence a futuro
            // '5'--Atendido
            // '0'--Normal - Sin vencimiento                
        }
        if (docBuscar.getEsFiltroFecha() != null
                && (docBuscar.getEsFiltroFecha().equals("1") || docBuscar.getEsFiltroFecha().equals("3"))) {
            String vFeEmiIni = docBuscar.getFeEmiIni();
            String vFeEmiFin = docBuscar.getFeEmiFin();
            if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                    && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
            sql.append(" AND R.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("','dd/mm/yyyy') + TIME '23:59:59'");
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
    public List<DocExtRecSeguiEstBean> getListaReporteBusqueda(DocExtRecSeguiEstBean docExtRecSeguiEstBean) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        boolean bBusqFiltro=false;
        
        prutaReporte.append("SELECT	R.NU_COR_EMI, ");
        prutaReporte.append("		TO_CHAR(R.FE_EMI, 'DD/MM/YYYY') FE_EMI_CORTA, ");
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP(R.NU_ANN, R.NU_EMI) DE_ORI_EMI, ");
        prutaReporte.append("		RR.NU_EXPEDIENTE, ");
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(R.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        prutaReporte.append("		COALESCE(RR.NU_DOC, 'S/N') NU_DOC, ");
        prutaReporte.append("		CASE D.TI_DES ");
        prutaReporte.append("			WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(D.CO_DEP_DES) || ' - ' || IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(D.CO_EMP_DES) ");
        prutaReporte.append("			WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(D.NU_RUC_DES) ");
        prutaReporte.append("			WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(D.NU_DNI_DES) ");
        prutaReporte.append("			WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(D.CO_OTR_ORI_DES) ");
        prutaReporte.append("		END DE_EMP_DES, ");
        prutaReporte.append("		R.DE_ASU, ");
        prutaReporte.append("		R.NU_DIA_ATE, ");
        prutaReporte.append("		TO_CHAR((CAST(R.FE_EMI as date) + IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_MAS(CAST(R.FE_EMI as date), R.NU_DIA_ATE)),'DD/MM/YYYY') AS feLimiteCorta, ");
        prutaReporte.append("		CASE ");
        prutaReporte.append("			WHEN R.NU_DIA_ATE > 0 ");
        prutaReporte.append("				AND (CAST (E.FE_FINALIZA as date) - CAST (now() as date)) <= 2 ");
        prutaReporte.append("				AND (CAST (E.FE_FINALIZA as date) - CAST (now() as date)) > 0 ");
        prutaReporte.append("				AND D.FE_ATE_DOC IS NULL ");
        prutaReporte.append("				AND D.FE_ARC_DOC IS NULL ");
        prutaReporte.append("				AND M.IN_DIA = '0' ");
        prutaReporte.append("			THEN '1' ");
        prutaReporte.append("			WHEN R.NU_DIA_ATE > 0 ");
        prutaReporte.append("				AND CAST (now() as date) > CAST (E.FE_FINALIZA as date) ");
        prutaReporte.append("				AND D.FE_ATE_DOC IS NULL ");
        prutaReporte.append("				AND D.FE_ARC_DOC IS NULL ");
        prutaReporte.append("				AND M.IN_DIA = '0' ");
        prutaReporte.append("			THEN '2' ");
        prutaReporte.append("			WHEN R.NU_DIA_ATE > 0 ");
        prutaReporte.append("				AND CAST (now() as date) = CAST (E.FE_FINALIZA as date) ");
        prutaReporte.append("				AND D.FE_ATE_DOC IS NULL ");
        prutaReporte.append("				AND D.FE_ARC_DOC IS NULL ");
        prutaReporte.append("				AND M.IN_DIA = '0' ");
        prutaReporte.append("			THEN '3' ");
        prutaReporte.append("			WHEN R.NU_DIA_ATE > 0 ");
        prutaReporte.append("				AND (CAST (E.FE_FINALIZA as date) - CAST (now() as date)) > 2 ");
        prutaReporte.append("				AND D.FE_ATE_DOC IS NULL ");
        prutaReporte.append("				AND D.FE_ARC_DOC IS NULL ");
        prutaReporte.append("				AND M.IN_DIA = '0' ");
        prutaReporte.append("			THEN '4' ");
        prutaReporte.append("			WHEN D.FE_ATE_DOC IS NOT NULL OR D.FE_ARC_DOC IS NOT NULL ");
        prutaReporte.append("			THEN '5' ");
        prutaReporte.append("			WHEN R.NU_DIA_ATE = 0 OR M.IN_DIA = '0' ");
        prutaReporte.append("			THEN '0' ");
        prutaReporte.append("		END CO_EST_VEN, ");
        prutaReporte.append("		CASE ");
        prutaReporte.append("			WHEN R.NU_DIA_ATE > 0 ");
        prutaReporte.append("				AND CAST (now() as date) > CAST (E.FE_FINALIZA as date) ");
        prutaReporte.append("				AND D.FE_ATE_DOC IS NULL ");
        prutaReporte.append("				AND D.FE_ARC_DOC IS NULL ");
        prutaReporte.append("				AND M.IN_DIA = '0' ");
        prutaReporte.append("			THEN IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_TRA(E.FE_FINALIZA, CAST (now() as date)) ");
        prutaReporte.append("			ELSE 0 ");
        prutaReporte.append("		END NU_DIA_EXC, ");
        prutaReporte.append("		TO_CHAR(D.FE_ARC_DOC, 'DD/MM/YYYY') FE_ARC_DOC_CORTA, ");
        prutaReporte.append("		TO_CHAR(D.FE_ATE_DOC, 'DD/MM/YYYY') FE_ATE_DOC_CORTA, ");
        prutaReporte.append("		TO_CHAR(D.FE_REC_DOC, 'DD/MM/YYYY') FE_REC_DOC_CORTA, ");
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(R.ES_DOC_EMI, 'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(D.CO_MOT) DE_MOTIVO, ");
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(D.CO_EMP_REC) DE_EMP_REC, ");
        prutaReporte.append("		R.NU_FOLIOS, ");
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(R.CO_EMP_RES) DE_EMP_RES, ");
        prutaReporte.append("		(SELECT DE_NOMBRE ");
        prutaReporte.append("		FROM IDOSGD.TDTR_PROCESOS_EXP ");
        prutaReporte.append("		WHERE CO_PROCESO = RR.CO_PROCESO_EXP ");
        prutaReporte.append("		AND ES_ESTADO='1') DE_PROCESO_EXP, ");
        prutaReporte.append("		RR.CO_PROCESO_EXP ");
        prutaReporte.append("FROM IDOSGD.TDTV_REMITOS R, ");
        prutaReporte.append("     IDOSGD.TDTV_DESTINOS D, ");
        prutaReporte.append("     IDOSGD.TDTX_REMITOS_RESUMEN RR, ");
        prutaReporte.append("     IDOSGD.TDTR_MOTIVO M, ");
        prutaReporte.append("     (SELECT Y.NU_ANN, Y.NU_EMI, Y.FE_FINALIZA, IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_TRA (CAST (now() as date),Y.FE_FINALIZA) NU_DIA_FAL FROM (SELECT X.NU_ANN, X.NU_EMI, ");
        prutaReporte.append("	  CAST (X.FE_EMI as date)+ IDOSGD.PK_SGD_DESCRIPCION_FU_DIA_MAS (CAST (X.FE_EMI as date),NU_DIA_ATE) AS FE_FINALIZA ");
        prutaReporte.append("	  FROM IDOSGD.TDTV_REMITOS X) Y) E ");
        prutaReporte.append("WHERE ");
        prutaReporte.append(" D.NU_ANN=R.NU_ANN ");
        prutaReporte.append(" AND D.NU_EMI=R.NU_EMI ");
        prutaReporte.append(" AND RR.NU_ANN=R.NU_ANN ");
        prutaReporte.append(" AND RR.NU_EMI=R.NU_EMI ");
        prutaReporte.append(" AND E.NU_ANN=R.NU_ANN ");
        prutaReporte.append(" AND E.NU_EMI=R.NU_EMI ");
        prutaReporte.append(" AND M.CO_MOT=D.CO_MOT ");

        try {
            String pnuAnn = docExtRecSeguiEstBean.getNuAnn();
            if(!(docExtRecSeguiEstBean.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))){
                prutaReporte.append(" AND R.NU_ANN = '").append(pnuAnn).append("'");
            }        

            prutaReporte.append(" AND R.CO_GRU='3'");
            prutaReporte.append(" AND R.ES_ELI='0'");
            prutaReporte.append(" AND D.ES_ELI='0'");
            prutaReporte.append(" AND R.ES_DOC_EMI NOT IN ('5', '9', '7')");
            prutaReporte.append(" AND R.TI_EMI IN ('02','03','04')");

            String auxTipoAcceso = docExtRecSeguiEstBean.getTiAcceso();
            String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0") || auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
            if (tiAcceso.equals("1")) { //acceso personal
                prutaReporte.append(" AND R.CO_EMP_RES = '").append(docExtRecSeguiEstBean.getCoEmpleado()).append("'");
            } else {
                if (docExtRecSeguiEstBean.getInMesaPartes().equals("0")) {
                    prutaReporte.append(" AND R.CO_DEP = '").append(docExtRecSeguiEstBean.getCoDependencia()).append("'");
                }
            }
            
            String pTipoBusqueda = docExtRecSeguiEstBean.getTipoBusqueda();
            if(pTipoBusqueda.equals("1") && docExtRecSeguiEstBean.isEsIncluyeFiltro()){
                bBusqFiltro=true;
            }
            
            //Filtro
            if(pTipoBusqueda.equals("0") || bBusqFiltro){
                if (docExtRecSeguiEstBean.getCoTipDocAdm() != null && docExtRecSeguiEstBean.getCoTipDocAdm().trim().length() > 0) {
                    prutaReporte.append(" AND R.CO_TIP_DOC_ADM = '").append(docExtRecSeguiEstBean.getCoTipDocAdm()).append("'");
                }
                if (docExtRecSeguiEstBean.getCoEsDocEmi() != null && docExtRecSeguiEstBean.getCoEsDocEmi().trim().length() > 0) {
                    prutaReporte.append(" AND R.ES_DOC_EMI = '").append(docExtRecSeguiEstBean.getCoEsDocEmi()).append("'");
                }
                if (docExtRecSeguiEstBean.getTiEmi() != null && docExtRecSeguiEstBean.getTiEmi().trim().length() > 0) {
                    prutaReporte.append(" AND R.TI_EMI = '").append(docExtRecSeguiEstBean.getTiEmi()).append("'");
                }
                if (docExtRecSeguiEstBean.getCoDepDes() != null && docExtRecSeguiEstBean.getCoDepDes().trim().length() > 0) {
                    prutaReporte.append(" AND STRPOS(RR.TI_EMI_DES,'").append(docExtRecSeguiEstBean.getCoDepDes()).append("') > 0");
                }
                if (docExtRecSeguiEstBean.getCoDepEmi() != null && docExtRecSeguiEstBean.getCoDepEmi().trim().length() > 0) {
                    prutaReporte.append(" AND R.CO_DEP_EMI = '").append(docExtRecSeguiEstBean.getCoDepEmi()).append("'");
                }
                if (docExtRecSeguiEstBean.getCoEmpRes() != null && docExtRecSeguiEstBean.getCoEmpRes().trim().length() > 0) {
                    prutaReporte.append(" AND R.CO_EMP_RES = '").append(docExtRecSeguiEstBean.getCoEmpRes()).append("'");
                }
                if (docExtRecSeguiEstBean.getCoProceso() != null && docExtRecSeguiEstBean.getCoProceso().trim().length() > 0) {
                    if(docExtRecSeguiEstBean.getCoProceso().equals("CON_TUPA")){
                        prutaReporte.append(" AND RR.CO_PROCESO_EXP not in ('0000') "); 
                    }else{
                        prutaReporte.append(" AND RR.CO_PROCESO_EXP = '").append(docExtRecSeguiEstBean.getCoProceso()).append("'");
                    }
                }        
                String pcoEstVencimiento=docExtRecSeguiEstBean.getCoEstVen();
                if (pcoEstVencimiento!= null && pcoEstVencimiento.trim().length() > 0) {
                    if ("1".equals(pcoEstVencimiento)) {
                        prutaReporte.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) <= 2");
                        prutaReporte.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) > 0");
                        prutaReporte.append(" AND d.fe_ate_doc IS NULL");
                        prutaReporte.append(" AND d.fe_arc_doc IS NULL");
                        prutaReporte.append(" AND m.in_dia = '0'");
                    }
                    if ("2".equals(pcoEstVencimiento)) {
                        prutaReporte.append(" AND CAST(NOW() AS DATE) > CAST(E.fe_finaliza AS DATE)");
                        prutaReporte.append(" AND d.fe_ate_doc IS NULL");
                        prutaReporte.append(" AND d.fe_arc_doc IS NULL");
                        prutaReporte.append(" AND m.in_dia = '0'");
                        prutaReporte.append(" AND r.NU_DIA_ATE <> 0");
                    }
                    if ("3".equals(pcoEstVencimiento)) {
                        //vence hoy
                        prutaReporte.append(" AND CAST(NOW() AS DATE) = CAST(E.fe_finaliza AS DATE)");
                        prutaReporte.append(" AND d.fe_ate_doc IS NULL");
                        prutaReporte.append(" AND d.fe_arc_doc IS NULL");
                        prutaReporte.append(" AND m.in_dia = '0'");
                        prutaReporte.append(" AND r.NU_DIA_ATE >= 0");
                    }
                    if ("4".equals(pcoEstVencimiento)) {
                        prutaReporte.append(" AND (CAST(E.fe_finaliza AS DATE) - CAST(NOW() AS DATE)) > 2");
                        prutaReporte.append(" AND d.fe_ate_doc IS NULL");
                        prutaReporte.append(" AND d.fe_arc_doc IS NULL");
                        prutaReporte.append(" AND m.in_dia = '0'");
                    }
                    if ("5".equals(pcoEstVencimiento)) {
                        prutaReporte.append(" AND d.fe_ate_doc is not null");
                        prutaReporte.append(" AND d.fe_rec_doc is not null");
                    }
                    if ("0".equals(pcoEstVencimiento)) {
                        prutaReporte.append(" and r.nu_dia_ate = 0");
                        prutaReporte.append(" and m.in_dia = '0'");
                        prutaReporte.append(" AND d.fe_ate_doc IS NULL");
                        prutaReporte.append(" AND d.fe_arc_doc IS NULL");
                    }

                    // '1'-- x vencerse
                    // '2'--vencidos
                    // '3'--vence hoy
                    // '4'--Vence a futuro
                    // '5'--Atendido
                    // '0'--Normal - Sin vencimiento                
                }
                if (docExtRecSeguiEstBean.getEsFiltroFecha() != null
                        && (docExtRecSeguiEstBean.getEsFiltroFecha().equals("1") || docExtRecSeguiEstBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = docExtRecSeguiEstBean.getFeEmiIni();
                    String vFeEmiFin = docExtRecSeguiEstBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                            && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                        prutaReporte.append(" AND R.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("','dd/mm/yyyy') + TIME '23:59:59'  ");
                    }
                } 
            }
            if (pTipoBusqueda.equals("1"))  {
                if(!bBusqFiltro){
                    if (docExtRecSeguiEstBean.getCoDepEmi()!= null && docExtRecSeguiEstBean.getCoDepEmi().trim().length()>0){
                        prutaReporte.append(" AND R.CO_DEP_EMI = :pcoDepEmi ");
                        objectParam.put("pcoDepEmi", docExtRecSeguiEstBean.getCoDepEmi());
                    }                 
                }
                if (docExtRecSeguiEstBean.getNuCorEmi()!= null && docExtRecSeguiEstBean.getNuCorEmi().trim().length() > 0) {
                    prutaReporte.append(" AND R.NU_COR_EMI = :pnuCorEmi ");
                    objectParam.put("pnuCorEmi", Integer.parseInt(docExtRecSeguiEstBean.getNuCorEmi()));
                }            
                if (docExtRecSeguiEstBean.getNuDoc()!= null && docExtRecSeguiEstBean.getNuDoc().trim().length() > 1) {
                    prutaReporte.append(" AND RR.NU_DOC LIKE ''||:pnuDocEmi||'%' ");
                    objectParam.put("pnuDocEmi", docExtRecSeguiEstBean.getNuDoc());
                }

                if (docExtRecSeguiEstBean.getNuExpediente()!= null && docExtRecSeguiEstBean.getNuExpediente().trim().length()>1){
                   prutaReporte.append(" AND RR.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                   objectParam.put("pnuExpediente", docExtRecSeguiEstBean.getNuExpediente());
                }
                
                // Busqueda del Asunto
                if (docExtRecSeguiEstBean.getDeAsu()!= null && docExtRecSeguiEstBean.getDeAsu().trim().length()>1){
                    //sql.append(" AND CONTAINS(in_busca_texto, '").append(BusquedaTextual.getContextValue(docBuscar.getDeAsu())).append("', 1 ) > 1 ");
                    //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(docBuscar.getDeAsu())+"', 1 ) > 1 ");                                
                   prutaReporte.append(" AND UPPER(R.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
                   objectParam.put("pDeAsunto", docExtRecSeguiEstBean.getDeAsu());                                
                }

                if (docExtRecSeguiEstBean.getCoTipoExp() != null && !docExtRecSeguiEstBean.getCoTipoExp().equals("")) {
                    prutaReporte.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                    objectParam.put("codTipoExp", docExtRecSeguiEstBean.getCoTipoExp()); 
                }

                if (docExtRecSeguiEstBean.getCoOriDoc()!= null && !docExtRecSeguiEstBean.getCoOriDoc().equals("")) {
                    prutaReporte.append(" AND R.CCOD_ORIGING = :coOriDoc");        
                    objectParam.put("coOriDoc", docExtRecSeguiEstBean.getCoOriDoc()); 
                }

                if(docExtRecSeguiEstBean.getBusResultado().equals("1"))
                {
                    if(docExtRecSeguiEstBean.getCoTipoPersona().equals("03")){
                    prutaReporte.append(" AND R.NU_DNI_EMI = :pNumDni");
                    objectParam.put("pNumDni", docExtRecSeguiEstBean.getBusNumDni());
                    }
                    else if(docExtRecSeguiEstBean.getCoTipoPersona().equals("02")){
                        prutaReporte.append(" AND R.NU_RUC_EMI = :pNumRuc");
                        objectParam.put("pNumRuc", docExtRecSeguiEstBean.getBusNumRuc());
                    }
                    else if(docExtRecSeguiEstBean.getCoTipoPersona().equals("04")){
                        prutaReporte.append(" AND R.CO_OTR_ORI_EMI = :pCoOtr");
                        objectParam.put("pCoOtr", docExtRecSeguiEstBean.getBusCoOtros());
                    }
                }
                
            }
            
            prutaReporte.append(" ORDER BY R.FE_EMI DESC ");
            
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocExtRecSeguiEstBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }
    
    @Override
    public List<DocExtRecSeguiEstBean> getListaReporteResumen(DocExtRecSeguiEstBean docExtRecSeguiEstBean) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        boolean bBusqFiltro = false;
         String vFeEmiIni = docExtRecSeguiEstBean.getFeEmiIni();
          String vFeEmiFin = docExtRecSeguiEstBean.getFeEmiFin();
            
        prutaReporte.append(" select D.CO_DEPENDENCIA as coDependencia,IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(D.CO_DEPEN_PADRE) as deDepenPadre,D.DE_DEPENDENCIA as deDependencia,D.DE_SIGLA as deDepSigla, ");
        prutaReporte.append(" (select count(1) from IDOSGD.TDTV_REMITOS PR,IDOSGD.TDTV_DESTINOS PD where PR.ES_ELI='0' AND PD.ES_ELI='0'  AND PR.ES_DOC_EMI NOT IN ('5', '9', '7') AND PR.NU_EMI=PD.NU_EMI AND PR.NU_ANN=PD.NU_ANN AND PD.ES_DOC_REC IN('0','1') AND PD.CO_DEP_DES=D.CO_DEPENDENCIA AND PR.FE_EMI between TO_DATE('").append(vFeEmiIni).append("', 'dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("', 'dd/mm/yyyy') + TIME '23:59:59') as inTotalPendiente, ");
        prutaReporte.append(" (select count(1) from IDOSGD.TDTV_REMITOS PR,IDOSGD.TDTV_DESTINOS PD where PR.ES_ELI='0' AND PD.ES_ELI='0'  AND PR.ES_DOC_EMI NOT IN ('5', '9', '7') AND PR.NU_EMI=PD.NU_EMI AND PR.NU_ANN=PD.NU_ANN AND PD.ES_DOC_REC IN('0') AND PD.CO_DEP_DES=D.CO_DEPENDENCIA AND PR.FE_EMI between TO_DATE('").append(vFeEmiIni).append("', 'dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("', 'dd/mm/yyyy') + TIME '23:59:59') as inNoLeido, ");
        prutaReporte.append(" (select count(1) from IDOSGD.TDTV_REMITOS PR,IDOSGD.TDTV_DESTINOS PD where PR.ES_ELI='0' AND PD.ES_ELI='0'  AND PR.ES_DOC_EMI NOT IN ('5', '9', '7') AND PR.NU_EMI=PD.NU_EMI AND PR.NU_ANN=PD.NU_ANN AND PD.ES_DOC_REC IN('1') AND PD.CO_DEP_DES=D.CO_DEPENDENCIA AND PR.FE_EMI between TO_DATE('").append(vFeEmiIni).append("', 'dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("', 'dd/mm/yyyy') + TIME '23:59:59') as inRecibidos, ");
        prutaReporte.append(" (select count(1) from IDOSGD.TDTV_REMITOS PR,IDOSGD.TDTV_DESTINOS PD where PR.ES_ELI='0' AND PD.ES_ELI='0'  AND PR.ES_DOC_EMI NOT IN ('5', '9', '7') AND PR.NU_EMI=PD.NU_EMI AND PR.NU_ANN=PD.NU_ANN AND PD.ES_DOC_REC IN('2','3','4','5') AND PD.CO_DEP_DES=D.CO_DEPENDENCIA AND PR.FE_EMI between TO_DATE('").append(vFeEmiIni).append("', 'dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("', 'dd/mm/yyyy') + TIME '23:59:59') as inAtenPendiente, ");
        prutaReporte.append(" (select count(1) from IDOSGD.TDTV_REMITOS PR,IDOSGD.TDTV_DESTINOS PD where PR.ES_ELI='0' AND PD.ES_ELI='0'  AND PR.ES_DOC_EMI NOT IN ('5', '9', '7') AND PR.NU_EMI=PD.NU_EMI AND PR.NU_ANN=PD.NU_ANN AND PD.CO_DEP_DES=D.CO_DEPENDENCIA AND PR.FE_EMI between TO_DATE('").append(vFeEmiIni).append("', 'dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin).append("', 'dd/mm/yyyy') + TIME '23:59:59') as inTotalRecibido ");
        prutaReporte.append(" from IDOSGD.RHTM_DEPENDENCIA D ");
        prutaReporte.append(" where D.IN_BAJA='0'  ");//and D.TI_DEPENDENCIA='0'
        prutaReporte.append(" order by D.CO_DEPENDENCIA,IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(D.CO_DEPEN_PADRE),D.DE_DEPENDENCIA");
       
        try {
           
                 
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocExtRecSeguiEstBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }
}
