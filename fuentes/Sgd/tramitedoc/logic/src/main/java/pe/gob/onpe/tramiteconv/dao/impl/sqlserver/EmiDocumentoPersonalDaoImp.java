/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DestinoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaEmiDocBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaRemitoBean;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoPersonalDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import org.springframework.jdbc.core.BeanPropertyRowMapper; 
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import java.sql.Types;
import pe.gob.onpe.tramitedoc.util.AleatorioCodVerificacion;
/**
 *
 * @author ecueva
 */
@Repository("emiDocumentoPersonalDao")
public class EmiDocumentoPersonalDaoImp   extends SimpleJdbcDaoBase implements EmiDocumentoPersonalDao{
	private SimpleJdbcCall spdataSource;
        @Override
	public List<DocumentoEmiBean> getDocumentosEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmi){
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro=false;
        Map<String, Object> objectParam = new HashMap<String, Object>();
        
        sql.append("SELECT X.*, ");
        sql.append("(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_DE_EMI_REF](X.NU_ANN, X.NU_EMI)) DE_EMI_REF, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        sql.append("CASE X.NU_CANDES ");
        sql.append("	WHEN 1 THEN	(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP](X.NU_ANN, X.NU_EMI)) ");
        sql.append("	ELSE (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP_V](X.NU_ANN, X.NU_EMI)) "); 
        sql.append("END DE_EMP_PRO, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS](X.ES_DOC_EMI, 'TDTV_REMITOS') DE_ES_DOC_EMI ");
        sql.append("FROM ( ");         
        sql.append("	SELECT "); 
        sql.append("	A.TI_CAP, ");
        sql.append("	A.NU_ANN, ");
        sql.append("	A.NU_EMI, ");
        sql.append("	A.NU_COR_EMI, ");
        sql.append("	A.FE_EMI, ");
        sql.append("	A.CO_TIP_DOC_ADM, ");
        sql.append("	CONVERT(VARCHAR(10), A.FE_EMI, 103) FE_EMI_CORTA,B.NU_DOC, ");
        sql.append("	UPPER(A.DE_ASU) DE_ASU_M,A.NU_CANDES, ");
        sql.append("	A.ES_DOC_EMI, ");
        sql.append("	B.IN_EXISTE_DOC EXISTE_DOC, ");
        sql.append("	CASE ISNULL(B.TI_EMI_REF, '0') + ISNULL(B.IN_EXISTE_ANEXO, '2') ");
        sql.append("		WHEN '00' THEN 0 ");
        sql.append("		WHEN '02' THEN 0 ");
        sql.append("		ELSE 1 ");
        sql.append("	END EXISTE_ANEXO, ");
        sql.append("	ROW_NUMBER() OVER (ORDER BY A.NU_COR_EMI) AS ROWNUM ");
        sql.append("	FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK), ");
        sql.append("	IDOSGD.TDTX_REMITOS_RESUMEN B WITH (NOLOCK) ");
        sql.append("	WHERE B.NU_ANN=A.NU_ANN ");
        sql.append("	AND B.NU_EMI=A.NU_EMI ");      
        
//        String pNUAnn = buscarDocumentoEmi.getsCoAnnio();
//        if(!(buscarDocumentoEmi.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))){
//            sql.append(" AND A.NU_ANN = :pNuAnn");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNUAnn);                
//        }        

        sql.append(" AND A.TI_EMI='05'");
        sql.append(" AND B.TI_EMI='05'");
        sql.append(" AND A.CO_GRU = '2'");        
        sql.append(" AND A.ES_ELI = '0'");
      
        String pTipoBusqueda = buscarDocumentoEmi.getsTipoBusqueda();        

        sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
        objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsCoEmpleado());

        if(pTipoBusqueda.equals("1") && buscarDocumentoEmi.isEsIncluyeFiltro()){
            bBusqFiltro=true;
        }            
            
        //Filtro
        if(pTipoBusqueda.equals("0") || bBusqFiltro){
            if (buscarDocumentoEmi.getsTipoDoc()!= null && buscarDocumentoEmi.getsTipoDoc().trim().length()>0){
               sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
               objectParam.put("pCoDocEmi", buscarDocumentoEmi.getsTipoDoc());
            }
            if (buscarDocumentoEmi.getsEstadoDoc()!= null && buscarDocumentoEmi.getsEstadoDoc().trim().length()>0){
               sql.append(" AND A.ES_DOC_EMI = :pEsDocEmi ");
               objectParam.put("pEsDocEmi", buscarDocumentoEmi.getsEstadoDoc());
            }
            if (buscarDocumentoEmi.getsPrioridadDoc()!= null && buscarDocumentoEmi.getsPrioridadDoc().trim().length()>0){
               sql.append(" AND B.CO_PRIORIDAD = :pCoPrioridad ");
               objectParam.put("pCoPrioridad", buscarDocumentoEmi.getsPrioridadDoc());
            }
            if (buscarDocumentoEmi.getsRefOrigen()!= null && buscarDocumentoEmi.getsRefOrigen().trim().length()>0){
               sql.append(" AND CHARINDEX(:pTiEmiRef,B.TI_EMI_REF) > 0 ");
               objectParam.put("pTiEmiRef", buscarDocumentoEmi.getsRefOrigen());
            }
            if (buscarDocumentoEmi.getsDestinatario()!= null && buscarDocumentoEmi.getsDestinatario().trim().length()>0){
               sql.append(" AND CHARINDEX(:pTiEmpPro,B.TI_EMI_DES) > 0 ");
               objectParam.put("pTiEmpPro", buscarDocumentoEmi.getsDestinatario());
            }
            if(buscarDocumentoEmi.getEsFiltroFecha()!= null && 
               (buscarDocumentoEmi.getEsFiltroFecha().equals("1") || buscarDocumentoEmi.getEsFiltroFecha().equals("3")|| buscarDocumentoEmi.getEsFiltroFecha().equals("2"))){
                String vFeEmiIni = buscarDocumentoEmi.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoEmi.getsFeEmiFin();       
                if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                   vFeEmiFin!= null && vFeEmiFin.trim().length()>0){ 
                    sql.append(" AND FE_EMI BETWEEN CONVERT(DATETIME, :pFeEmiIni, 103) AND CONVERT(DATETIME, :pFeEmiFin, 103) + 0.99999 ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);                    
                }
            }

        }    

        //Busqueda
        if (pTipoBusqueda.equals("1"))
        {
            if (buscarDocumentoEmi.getsNumCorEmision()!= null && buscarDocumentoEmi.getsNumCorEmision().trim().length()>0){
               sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
               objectParam.put("pnuCorEmi", buscarDocumentoEmi.getsNumCorEmision());
            }

            if (buscarDocumentoEmi.getsNumDoc()!= null && buscarDocumentoEmi.getsNumDoc().trim().length()>1){
               sql.append(" AND B.NU_DOC LIKE '%'+:pnuDocEmi+'%' ");
               objectParam.put("pnuDocEmi", buscarDocumentoEmi.getsNumDoc());
            }

            /*if (buscarDocumentoEmi.getsBuscNroExpediente()!= null && buscarDocumentoEmi.getsBuscNroExpediente().trim().length()>1){
               sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
               objectParam.put("pnuExpediente", buscarDocumentoEmi.getsBuscNroExpediente());
            }*/

            // Busqueda del Asunto
            if (buscarDocumentoEmi.getsDeAsuM()!= null && buscarDocumentoEmi.getsDeAsuM().trim().length()>1){
               //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoEmi.getsDeAsuM())+"', 1 ) > 1 ");
               sql.append(" AND UPPER(A.DE_ASU) LIKE '%'+:pDeAsunto+'%' ");
               objectParam.put("pDeAsunto", buscarDocumentoEmi.getsDeAsuM());
            }
        }

        sql.append(") X "); 
        sql.append("WHERE ROWNUM < 51 ");
        sql.append("ORDER BY X.NU_COR_EMI DESC ");

        List<DocumentoEmiBean> list = new ArrayList<DocumentoEmiBean>();
        try {
            /*list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{buscarDocumentoEmiBean.getsCoAnnio(),buscarDocumentoEmiBean.getsCoEmpleado(),buscarDocumentoEmiBean.getsTipoDoc(),
                    buscarDocumentoEmiBean.getsEstadoDoc(),buscarDocumentoEmiBean.getsPrioridadDoc(),buscarDocumentoEmiBean.getsRefOrigen(),buscarDocumentoEmiBean.getsRefOrigen(),buscarDocumentoEmiBean.getsRefOrigen(),
                    buscarDocumentoEmiBean.getsDestinatario(),buscarDocumentoEmiBean.getsDestinatario(),buscarDocumentoEmiBean.getsDestinatario(),
                    buscarDocumentoEmiBean.getsExpediente(),buscarDocumentoEmiBean.getsExpediente(),buscarDocumentoEmiBean.getsExpediente(),buscarDocumentoEmiBean.getsExpediente()});*/
            
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override    
    public DocumentoEmiBean getDocumentoEmiAdm(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("select A.*, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](A.CO_EMP_EMI) DE_EMP_EMI, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](A.CO_EMP_RES) DE_EMP_RES, ");
        sql.append("CONVERT(VARCHAR(10), A.FE_EMI, 103) FE_EMI_CORTA, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS](A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](A.CO_DEP_EMI) DE_DEP_EMI, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](A.CO_LOC_EMI) DE_LOC_EMI ");
        sql.append("from IDOSGD.TDTV_REMITOS A  WITH (NOLOCK) ");
        sql.append("where A.ES_ELI='0' "); 
        sql.append("AND A.TI_EMI='05' ");
        sql.append("AND A.NU_ANN = ? "); 
        sql.append("AND A.NU_EMI = ? ");       
        
        DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            System.out.println("nu_ann="+pnuAnn+","+"nu_emi="+pnuEmi);
            e.printStackTrace();
        }
        return documentoEmiBean;        
    }
    
    @Override
    public List<DestinatarioDocumentoEmiBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer(); 
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();                
        sql.append("select a.nu_ann, ");
        sql.append("a.nu_emi, ");
        sql.append("a.nu_des, ");
        sql.append("a.co_loc_des co_local, ");
        sql.append("CASE WHEN a.co_loc_des IS NULL THEN NULL ");
        sql.append("	 ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](a.co_loc_des), 1, 100) ");
        sql.append("END de_local, ");
        sql.append("a.co_dep_des co_dependencia, ");
        sql.append("CASE WHEN a.co_dep_des IS NULL THEN NULL ");
        sql.append("	 ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_des), 1, 100) ");
        sql.append("END de_dependencia, ");
        sql.append("a.co_emp_des co_empleado, ");
        sql.append("CASE WHEN a.co_emp_des IS NULL THEN NULL ");
        sql.append("	 ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](a.co_emp_des), 1, 100) ");
        sql.append("END de_empleado, ");
        sql.append("a.co_mot co_tramite, ");
        sql.append("CASE WHEN a.co_mot IS NULL THEN NULL ");
        sql.append("	 ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_MOTIVO](a.co_mot), 1, 100) ");
        sql.append("END de_tramite, ");
        sql.append("a.co_pri co_prioridad, ");
        sql.append("a.de_pro de_indicaciones, ");
        sql.append("a.ti_des co_tipo_destino ");
        sql.append("FROM IDOSGD.tdtv_destinos a WITH (NOLOCK) ");
        sql.append("where A.TI_DES = '01' "); 
        sql.append("AND A.nu_ann = ? "); 
        sql.append("AND A.nu_emi = ? ");
        sql.append("AND A.ES_ELI='0' "); 
        sql.append("AND A.NU_EMI_REF is null ");
        sql.append("order by A.NU_COR_DES ");
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }
    
    @Override
    public List<ReferenciaBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer(); 
        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();                
        sql.append("SELECT "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](a.co_tip_doc_adm) de_tipo_doc, ");
        sql.append("CASE a.ti_emi ");
        sql.append("	WHEN '01' THEN a.nu_doc_emi + '-' + a.nu_ann + '-' + a.de_doc_sig ");
        sql.append("	WHEN '05' THEN a.nu_doc_emi + '-' + a.nu_ann + '-' + a.de_doc_sig ");
        sql.append("	ELSE a.de_doc_sig ");
        sql.append("END li_nu_doc, ");
        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_emi), 1, 100) de_dep_emi, "); 
        sql.append("CONVERT(VARCHAR(10), a.fe_emi, 3) fe_emi_corta, "); 
        sql.append("b.nu_ann, ");
        sql.append("b.nu_emi, ");
        sql.append("ISNULL(LTRIM(RTRIM(CAST(b.nu_des AS VARCHAR))), 'N') nu_des, ");
        sql.append("b.nu_ann_ref, ");
        sql.append("b.nu_emi_ref, ");
        sql.append("ISNULL(LTRIM(RTRIM(CAST(b.nu_des_ref AS VARCHAR))), 'N') nu_des_ref, ");
        sql.append("b.co_ref, ");
        sql.append("CASE ISNULL(LTRIM(RTRIM(CAST(b.nu_des_ref AS VARCHAR))), 'N') ");
        sql.append("	WHEN 'N' THEN 'emi' ");
        sql.append("	ELSE 'rec' ");
        sql.append("END tip_doc_ref, ");
        sql.append("a.co_tip_doc_adm, ");
        sql.append("'BD' accion_bd, ");
        sql.append("A.TI_EMI, ");
        sql.append("A.ES_ELI ");
        sql.append("FROM IDOSGD.tdtv_remitos a WITH (NOLOCK) , ");
        sql.append("	 IDOSGD.TDTR_REFERENCIA b WITH (NOLOCK)  "); 
        sql.append("WHERE a.nu_ann = b.nu_ann_ref ");
        sql.append("AND a.nu_emi = b.nu_emi_ref ");
        sql.append("and b.nu_ann = ? ");
        sql.append("and b.nu_emi = ? ");
        sql.append("AND B.NU_DES IS NULL ");
        sql.append("AND A.ES_ELI='0' ");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }
    
    public String updEstadoDocumentoAdmReferenciasEmitido(String coUseMod,String nuEmi,String nuAnn, String nuDes) {
     String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        String g="";
        if(nuDes!=null){
          g=" AND NU_DES='"+nuDes+"' ";
        }
        else
        {
         g=" AND NU_DES IS NULL ";
        }        
                sqlUpd.append("update IDOSGD.tdtv_destinos \n"
                + "set CO_USE_MOD='"+coUseMod+"' \n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP \n"
                + ",ES_DOC_REC='4' \n"              
                + " WHERE NU_ANN='"+nuAnn+"' AND NU_EMI='"+nuEmi+"' "+g );                                 
        try {
            this.jdbcTemplate.update(sqlUpd.toString());
            vReturn = "OK";
        }catch(DuplicateKeyException con){
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
        }
        return vReturn;
     }
    
       
      public String updEstadoDocumentoAdmReferenciasRecibido(String coUseMod,String nuEmi,String nuAnn, String nuDes)
      {
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        String g="";
        if(nuDes!=null){
          g=" AND NU_DES='"+nuDes+"' ";
        }
        else
        {
         g=" AND NU_DES IS NULL ";
        }        
                sqlUpd.append("update IDOSGD.tdtv_destinos \n"
                + "set CO_USE_MOD='"+coUseMod+"' \n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP \n"
                + ",ES_DOC_REC='1' \n"              
                + " WHERE NU_ANN='"+nuAnn+"' AND NU_EMI='"+nuEmi+"' "+g );                                 
        try {
            this.jdbcTemplate.update(sqlUpd.toString());
            vReturn = "OK";
        }catch(DuplicateKeyException con){
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
        }
        return vReturn;
      }
     
         
    @Override
    public DocumentoEmiBean getDocumentoEmiAdmNew(String codDependencia,String codEmpleado,String codLocal){
       StringBuffer sql = new StringBuffer();
        sql.append("select  [IDOSGD].[PK_SGD_DESCRIPCION_DE_SIGLA](?) + '-' +(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_INICIALES_EMP](?))  de_doc_sig, ");
        sql.append("? co_dep_emi, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](?) de_dep_emi, ");
        sql.append("? co_loc_emi, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](?) de_loc_emi, ");
        sql.append("? co_emp_emi, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](?) de_emp_emi, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS]('5', 'TDTV_REMITOS') de_es_doc_emi, ");
        sql.append("'0' existe_doc, ");
        sql.append("'0' existe_anexo, ");
        sql.append("'5' es_doc_emi, ");
        sql.append("'05' ti_emi, ");
        sql.append("'0' nu_dia_ate, ");
        sql.append("CONVERT(VARCHAR(10), CURRENT_TIMESTAMP, 103) fe_emi_corta, ");
        sql.append("YEAR(CURRENT_TIMESTAMP) nu_ann ");     
        
        DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{codDependencia,codEmpleado , codDependencia, codDependencia, codLocal, codLocal, codEmpleado, codEmpleado});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiBean;           
    }    
    
    @Override
    public String updDocumentoEmiAdmBean(DocumentoEmiBean documentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos ");
        sqlUpd.append("set CO_USE_MOD=?, ");
        sqlUpd.append("DE_ASU=?, ");
        sqlUpd.append("NU_DIA_ATE=?, ");
        sqlUpd.append("CO_TIP_DOC_ADM=?, ");       
        sqlUpd.append("NU_DOC_EMI=?, ");
        sqlUpd.append("DE_DOC_SIG=?, ");      
        sqlUpd.append("FE_USE_MOD=CURRENT_TIMESTAMP "); 
        sqlUpd.append("where NU_ANN=? ");
        sqlUpd.append("AND NU_EMI=? ");
                
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(),documentoEmiBean.getDeAsu(),documentoEmiBean.getNuDiaAte(),
                    documentoEmiBean.getCoTipDocAdm(),documentoEmiBean.getNuDocEmi(),documentoEmiBean.getDeDocSig(),documentoEmiBean.getNuAnn(),documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        }catch(DuplicateKeyException con){
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
            
        }
        return vReturn;        
    }
    
    @Override
    public String insReferenciaDocumentoEmi(String nuAnn, String nuEmi, ReferenciaEmiDocBean referenciaEmiDocBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.tdtr_referencia( ");
        sqlUpd.append("NU_ANN, ");
        sqlUpd.append("NU_EMI, ");
        sqlUpd.append("CO_REF, ");
        sqlUpd.append("NU_ANN_REF, ");
        sqlUpd.append("NU_EMI_REF, ");
        sqlUpd.append("NU_DES_REF, ");
        sqlUpd.append("CO_USE_CRE, ");
        sqlUpd.append("FE_USE_CRE) ");
        sqlUpd.append("VALUES(?,?,?,?,?,cast(? as numeric(10,0)),?,CURRENT_TIMESTAMP) ");            
        
        try {
            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_UTILS_SEC_REFERENCIA_CO_REF]")
                                            .withoutProcedureColumnMetaDataAccess()
                                            .declareParameters(new SqlOutParameter("COD_NU_ANN_REF", Types.INTEGER));
                    
            Map out = simpleJdbcCall.execute();
            int codigo = (Integer)out.get("COD_NU_ANN_REF");
            String cod_num_ann_ref = String.format("%010d", codigo); 
            if(referenciaEmiDocBean.getNuDes()!=null && referenciaEmiDocBean.getNuDes().length()>0){
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn,nuEmi, cod_num_ann_ref, referenciaEmiDocBean.getNuAnn(),referenciaEmiDocBean.getNuEmi(),
                                                                    referenciaEmiDocBean.getNuDes(),referenciaEmiDocBean.getCoUseCre()});
            }
            else {
                sqlUpd = new StringBuffer();
                sqlUpd.append("INSERT INTO IDOSGD.tdtr_referencia( ");
                sqlUpd.append("NU_ANN, ");
                sqlUpd.append("NU_EMI, ");
                sqlUpd.append("CO_REF, ");
                sqlUpd.append("NU_ANN_REF, ");
                sqlUpd.append("NU_EMI_REF, ");
                
                sqlUpd.append("CO_USE_CRE, ");
                sqlUpd.append("FE_USE_CRE) ");
                sqlUpd.append("VALUES(?,?,?,?,?, ?,CURRENT_TIMESTAMP) "); 
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn,nuEmi, cod_num_ann_ref, referenciaEmiDocBean.getNuAnn(),referenciaEmiDocBean.getNuEmi(),
                                                                    referenciaEmiDocBean.getCoUseCre()});
            }
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
            
        }
        return vReturn;
    }
    
    @Override
    public String updReferenciaDocumentoEmi(String nuAnn, String nuEmi, ReferenciaEmiDocBean referenciaEmiDocBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtr_referencia \n"
                + "set NU_ANN_REF = ?,\n"
                + "NU_EMI_REF = ?,\n"
                + "NU_DES_REF = ?\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND CO_REF = ?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{referenciaEmiDocBean.getNuAnn(),referenciaEmiDocBean.getNuEmi(),
            referenciaEmiDocBean.getNuDes(),nuAnn,nuEmi,referenciaEmiDocBean.getCoRef()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
        }
        return vReturn;
    }

    @Override
    public String delReferenciaDocumentoEmi(String nuAnn, String nuEmi, ReferenciaEmiDocBean referenciaEmiDocBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlIns = new StringBuffer();
        sqlIns.append("DELETE FROM IDOSGD.tdtr_referencia\n" +
        "WHERE NU_ANN = ? AND NU_EMI = ? \n" +
        "AND NU_ANN_REF = ? AND NU_EMI_REF = ?");
        
        try {
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{nuAnn,nuEmi,referenciaEmiDocBean.getNuAnn(),referenciaEmiDocBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
        }        
        return vReturn;
    }
    
    @Override
    public String insDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.TDTV_DESTINOS(\n" +
                    "NU_ANN,\n" +
                    "NU_EMI,\n" +
                    "NU_DES,\n" +
                    "CO_LOC_DES,\n" +
                    "CO_DEP_DES,\n" +
                    "TI_DES,\n" +
                    "CO_EMP_DES,\n" +
                    "CO_PRI,\n" +
                    "DE_PRO,\n" +
                    "CO_MOT, \n" +
                    "CO_OTR_ORI_DES, \n" +
                    "NU_DNI_DES, \n" +
                    "NU_RUC_DES, \n" +
                    "ES_ELI,\n" +
                    "FE_USE_CRE,\n" +
                    "FE_USE_MOD,\n" +
                    "CO_USE_MOD,\n" +
                    "CO_USE_CRE,\n" +
                    "ES_DOC_REC,\n" +
                    "ES_ENV_POR_TRA)\n" +
                    "VALUES(?,?,(select ISNULL(MAX(a.nu_des) + 1, 1) FROM IDOSGD.tdtv_destinos a  WITH (NOLOCK)  where \n" +
                    "A.NU_ANN=? and\n" +
                    "A.NU_EMI=?),?,?,?,?,?,?,?,?,?,?,'0',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?,?,'0','0')");
        
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn,nuEmi,nuAnn,nuEmi,destinatarioDocumentoEmiBean.getCoLocal(),destinatarioDocumentoEmiBean.getCoDependencia(),destinatarioDocumentoEmiBean.getCoTipoDestino(),
            destinatarioDocumentoEmiBean.getCoEmpleado(),destinatarioDocumentoEmiBean.getCoPrioridad(),destinatarioDocumentoEmiBean.getDeIndicaciones(),destinatarioDocumentoEmiBean.getCoTramite(),
            destinatarioDocumentoEmiBean.getCoOtroOrigen(),destinatarioDocumentoEmiBean.getNuDni(),destinatarioDocumentoEmiBean.getNuRuc(),
            destinatarioDocumentoEmiBean.getCoUseCre(),destinatarioDocumentoEmiBean.getCoUseCre()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado Destino.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
            
        }
        return vReturn;   
    }

    @Override
    public String updDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();

                sqlUpd.append("update IDOSGD.TDTV_DESTINOS \n"
                + "set CO_LOC_DES=?\n"
                + ",CO_DEP_DES=?\n"
                + ",CO_EMP_DES=?\n"
                + ",CO_PRI=?\n"
                + ",DE_PRO=?\n"
                + ",CO_MOT=?\n"
                + ",CO_OTR_ORI_DES=? \n"
                + ",NU_DNI_DES=? \n"
                + ",NU_RUC_DES=? \n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + ",CO_USE_MOD=?\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=? and\n"
                + "NU_DES=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{destinatarioDocumentoEmiBean.getCoLocal(),destinatarioDocumentoEmiBean.getCoDependencia(),
            destinatarioDocumentoEmiBean.getCoEmpleado(),destinatarioDocumentoEmiBean.getCoPrioridad(),destinatarioDocumentoEmiBean.getDeIndicaciones(),destinatarioDocumentoEmiBean.getCoTramite(),
            destinatarioDocumentoEmiBean.getCoOtroOrigen(),destinatarioDocumentoEmiBean.getNuDni(),destinatarioDocumentoEmiBean.getNuRuc(),
            destinatarioDocumentoEmiBean.getCoUseCre(),nuAnn,nuEmi,destinatarioDocumentoEmiBean.getNuDes()});
            vReturn = "OK";
        }catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado Destino.";
            //con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
            
        }
        return vReturn;
    }

    @Override
    public String delDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer  sqlDel = new StringBuffer();
        sqlDel.append("delete from IDOSGD.TDTV_DESTINOS\n"
                + "WHERE NU_ANN = ?\n"
                + "AND NU_EMI = ?\n"
                + "and NU_DES=?");        
//        sqlUpd.append("update TDTV_DESTINOS \n"
//                + "set ES_ELI='1'\n"
//                + ",FE_USE_CRE=SYSDATE\n"
//                + ",FE_USE_MOD=SYSDATE\n"
//                + ",CO_USE_MOD='ADM'\n"
//                + ",CO_USE_CRE='ADM'\n"
//                + "where\n"
//                + "NU_ANN=? and\n"
//                + "NU_EMI=? and\n"
//                + "NU_DES=?");
        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{nuAnn,nuEmi,destinatarioDocumentoEmiBean.getNuDes()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
            
        }
        return vReturn;
    }    
    
    @Override
    public String insDocumentoEmiBean(DocumentoEmiBean documentoEmiBean){
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String snuDocEmi = null;
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        StringBuffer  sqlQry1 = new StringBuffer();
        StringBuffer  sqlQry2 = new StringBuffer();
        StringBuffer  sqlQry3 = new StringBuffer();

        sqlQry1.append("SELECT ISNULL(MAX(nu_cor_emi), 0) + 1 ");
        sqlQry1.append("FROM IDOSGD.tdtv_remitos WITH (NOLOCK) ");
        sqlQry1.append("WHERE nu_ann = ? ");
        sqlQry1.append("AND co_dep_emi = ? ");
        sqlQry1.append("AND CO_GRU = '2' ");
        
        sqlQry2.append("SELECT ISNULL(MAX(nu_doc), 0) + 1 ");
        sqlQry2.append("FROM IDOSGD.tdtv_remitos WITH (NOLOCK) ");
        sqlQry2.append("WHERE NU_ANN = ? ");
        sqlQry2.append("AND CO_EMP_EMI = ? ");
        sqlQry2.append("AND CO_GRU = '2' ");
        
        sqlQry3.append("SELECT RIGHT(REPLICATE('0', 6) + CAST((ISNULL(MAX(NU_DOC_EMI), 0) + 1) AS VARCHAR), 6) ");
        sqlQry3.append("FROM IDOSGD.TDTV_REMITOS TR WITH (NOLOCK) ");
        sqlQry3.append("WHERE TR.NU_ANN=? ");
        sqlQry3.append("AND TR.CO_TIP_DOC_ADM=? ");
        sqlQry3.append("AND TR.TI_EMI='05' ");
        sqlQry3.append("AND TR.NU_COR_DOC=? ");
        sqlQry3.append("AND ISNUMERIC(NU_DOC_EMI)=1 ");        
        
        sqlUpd.append("INSERT INTO IDOSGD.tdtv_remitos(\n" +
                                "NU_ANN,\n" +
                                "NU_EMI,\n" +
                                "NU_COR_EMI,\n" +
                                
                                "CO_LOC_EMI,\n" +
                                "CO_DEP_EMI,\n" +
                                "CO_EMP_EMI,\n" +
                                "CO_EMP_RES, \n" +
                                
                                "TI_EMI,\n" +
                                "FE_EMI,\n" +
                                "CO_GRU,\n" +
                                "DE_ASU, \n" +
                                "ES_DOC_EMI, \n" +
                                "NU_DIA_ATE, \n" +
                                "TI_CAP, \n" +
                                "CO_TIP_DOC_ADM, \n" +
                                "NU_COR_DOC, \n" +
                                "DE_DOC_SIG, \n" +
                                
                                "NU_ANN_EXP, \n"+
                                "NU_SEC_EXP, \n"+
                
                                "ES_ELI,\n" +
                                "CO_USE_CRE,\n" +
                                "FE_USE_CRE,\n" +
                                "CO_USE_MOD,\n" +
                                "FE_USE_MOD,NU_DOC,NU_DOC_EMI,COD_VER_EXT)\n" +
                                "VALUES(?,?,?,?,?,?,?,'05',CURRENT_TIMESTAMP,'2',?,'5',?,'03',?,\n" +
                                "?,?,?,?,\n" +
                                "'0',?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?,?)");
                
        try {
            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_UTILS_SEC_REMITOS_NU_EMI]")
                                            .withoutProcedureColumnMetaDataAccess()
                                            .declareParameters(new SqlOutParameter("COD_NU_EMI", Types.INTEGER));
                    
            Map out = simpleJdbcCall.execute();
            int codigo = (Integer)out.get("COD_NU_EMI");
            String snuEmi = String.format("%010d", codigo);
            
            String snuCorEmi = this.jdbcTemplate.queryForObject(sqlQry1.toString(),String.class, new Object[]{documentoEmiBean.getNuAnn(),documentoEmiBean.getCoDepEmi()});
            if (documentoEmiBean.getNuDocEmi() == null) {
                snuDocEmi = this.jdbcTemplate.queryForObject(sqlQry3.toString(),String.class, new Object[]{documentoEmiBean.getNuAnn(),documentoEmiBean.getCoTipDocAdm(),documentoEmiBean.getCoEmpEmi()});
            }else{
                snuDocEmi = documentoEmiBean.getNuDocEmi();
            }
            String snuDoc = this.jdbcTemplate.queryForObject(sqlQry2.toString(),String.class, new Object[]{documentoEmiBean.getNuAnn(),documentoEmiBean.getCoEmpEmi()});
                    
            documentoEmiBean.setNuEmi(snuEmi);
            documentoEmiBean.setNuCorEmi(snuCorEmi);
            documentoEmiBean.setNuDocEmi(snuDocEmi);
            documentoEmiBean.setNuDoc(snuDoc);
            String NuAnnExp,NuSecExp;
            if(documentoEmiBean.getNuAnnExp()==null)
            {
                NuAnnExp="NULL";
            }
            else
            {
                NuAnnExp=documentoEmiBean.getNuAnnExp().replace("'","");
            }
            
            if(documentoEmiBean.getNuSecExp()==null)
            {
                NuSecExp="NULL";
            }
            else{
                NuSecExp=documentoEmiBean.getNuSecExp().replace("'","");
            }   
            documentoEmiBean.setCoVerExt(AleatorioCodVerificacion.generaCodVerificacion());
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getNuAnn(),snuEmi,snuCorEmi,
                documentoEmiBean.getCoLocEmi(),documentoEmiBean.getCoDepEmi(),documentoEmiBean.getCoEmpEmi(),documentoEmiBean.getCoEmpEmi(),
                documentoEmiBean.getDeAsu(),documentoEmiBean.getNuDiaAte(),documentoEmiBean.getCoTipDocAdm(),documentoEmiBean.getCoEmpEmi(),
                documentoEmiBean.getDeDocSig(),NuAnnExp,NuSecExp,documentoEmiBean.getCoUseMod(),documentoEmiBean.getCoUseMod(),snuDoc,snuDocEmi, documentoEmiBean.getCoVerExt()});
            
            vReturn = "OK";
        }catch(DuplicateKeyException con){
            con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        }  catch (Exception e) {
            e.printStackTrace();
            //vReturn =e.getMessage();
            
        }
        return vReturn;
    }
    
    @Override
    public String getCanDocumentoEmiDuplicados(DocumentoEmiBean documentoEmiBean){
        String vReturn = "NO_OK";
        try {
             vReturn = this.jdbcTemplate.queryForObject("SELECT COUNT(1)\n" +
                            "FROM IDOSGD.tdtv_remitos tr WITH (NOLOCK) \n" +
                            "WHERE tr.nu_ann = ?\n" +
                            "AND tr.co_tip_doc_adm = ?\n" +
                            "AND tr.co_emp_emi = ?\n" +
                            "AND tr.nu_doc_emi = ?\n" +
                            "AND tr.ti_emi = '05'"/*\n" +
                            "AND tr.es_doc_emi NOT IN ('9')"*/,String.class,new Object[]{documentoEmiBean.getNuAnn(),documentoEmiBean.getCoTipDocAdm(),
                                documentoEmiBean.getCoEmpEmi(),documentoEmiBean.getNuDocEmi()});
        } catch (Exception e) {
             e.printStackTrace();
             vReturn=e.getMessage();
        }          
        return vReturn;
    }    
    
    @Override
    public String verificarDocumentoLeido(String pnuAnn, String pnuEmi){
       String vReturn = "NO_OK";
       StringBuffer sqlQry = new StringBuffer();
       sqlQry.append("SELECT ISNULL(MAX(es_doc_rec),'0')\n" +
        "FROM IDOSGD.tdtv_destinos WITH (NOLOCK) \n" +
        "WHERE nu_ann = ?\n" +
        "AND nu_emi = ?\n" +
        "AND es_eli = '0'");
       
       try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(),String.class,new Object[]{pnuAnn, pnuEmi});
       } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
       }       
       return vReturn;
    }
    
    @Override
    public String updEstadoDocumentoEmiAdm(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();

                sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=?\n"
                + ",ES_DOC_EMI=?\n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(),documentoEmiBean.getEsDocEmi(),
            documentoEmiBean.getNuAnn(),documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
            
        }
        return vReturn;
    }
    
    @Override
    public String updDocumentoObj(final DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
        boolean inInsert = false;
        /*Verificamos si es Insert o Update*/
        try {
             int cant = this.jdbcTemplate.queryForInt("select count(nu_ann) from IDOSGD.tdtv_archivo_doc WITH (NOLOCK)  where nu_ann = ? and nu_emi = ?",new Object[]{docObjBean.getNuAnn(),docObjBean.getNuEmi()});
             if (cant>0){
                inInsert = false;
             }else{
                inInsert = true;
             }
             
        } catch (Exception e) {
             e.printStackTrace();
             vReturn=e.getMessage();
        }       

        /**/

        final LobHandler lobhandler = new DefaultLobHandler();
        if (inInsert){
            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO IDOSGD.tdtv_archivo_doc (NU_ANN,NU_EMI,BL_DOC,DE_RUTA_ORIGEN,ES_FIRMA,FEULA)\n" +
                        "VALUES(?,?,?,?,'0',CONVERT(VARCHAR(10), CURRENT_TIMESTAMP, 112))");
            try {
                this.jdbcTemplate.execute(sql.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
                        ps.setString(1, docObjBean.getNuAnn());
                        ps.setString(2, docObjBean.getNuEmi());
                        lobCreator.setBlobAsBytes(ps, 3, docObjBean.getDocumento());
                        ps.setString(4, docObjBean.getNombreArchivo());
                    }
                });
                vReturn = "OK";
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else{
            StringBuffer sql = new StringBuffer();
            sql.append("UPDATE IDOSGD.TDTV_ARCHIVO_DOC SET BL_DOC = ?, DE_RUTA_ORIGEN = ?, FEULA=CONVERT(VARCHAR(10), CURRENT_TIMESTAMP, 112)\n" +
                        "WHERE NU_ANN = ? \n" +
                        "AND NU_EMI = ? ");
            try {
                this.jdbcTemplate.execute(sql.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
                        lobCreator.setBlobAsBytes(ps, 1, docObjBean.getDocumento());
                        ps.setString(2, docObjBean.getNombreArchivo());
                        ps.setString(3, docObjBean.getNuAnn());
                        ps.setString(4, docObjBean.getNuEmi());
                    }
                });
                vReturn = "OK";
            } catch (Exception e) {
                e.printStackTrace();
                vReturn = e.getMessage().substring(0,20);
            }
        }

        return vReturn;
    } 
    
    @Override
    public String updEstadoDocumentoEmitido(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();

                sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=? \n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP \n"
                + ",FE_EMI=CURRENT_TIMESTAMP \n"
                + ",NU_COR_DOC=? \n"
                + ",NU_DOC_EMI=? \n"
                + ",ES_DOC_EMI=? \n"
                + "WHERE NU_ANN=? \n"
                + "AND NU_EMI=? ");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(),documentoEmiBean.getNuCorDoc(),
            documentoEmiBean.getNuDocEmi(),documentoEmiBean.getEsDocEmi(),documentoEmiBean.getNuAnn(),documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        }catch(DuplicateKeyException con){
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
        }
        return vReturn;
    }    
    
     
    @Override
    public String updEstadoDocumentoReferenciasEmitido(String coUseMod,String nuEmi,String nuAnn,String nuDes){ 
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        if (nuDes==null || nuDes.equals("null")  || nuDes.equals("NULL") ){
            nuDes="0";
        }
                sqlUpd.append("update IDOSGD.tdtv_destinos \n"
                + "set CO_USE_MOD='"+coUseMod+"' \n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP \n"
                + ",ES_DOC_REC='2' \n"              
                + " WHERE NU_ANN='"+nuAnn+"' AND NU_EMI='"+nuEmi+"' AND NU_DES='"+nuDes+"' ");
                                 
        try {
            this.jdbcTemplate.update(sqlUpd.toString());
            vReturn = "OK";
                      
                  
        }catch(DuplicateKeyException con){
            con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
        }
        return vReturn;
    }
    
    
    @Override
    public String anularDocumento(DocumentoEmiBean documentoEmiBean){
        String vReturn = "NO_OK";
//        StringBuffer sqlQry = new StringBuffer();
//        sqlQry.append("SELECT nvl(MAX(nu_cor_doc), 1) + 1\n" +
//                    "FROM tdtv_remitos\n" +
//                    "WHERE nu_ann         = ?\n" +
//                    "AND ti_emi         = ?\n" +
//                    "AND co_dep_emi     = ?\n" +
//                    "AND co_tip_doc_adm = ?\n" +
//                    "AND nu_doc_emi = ?");
        
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set es_doc_emi = ?,\n"
                + "fe_use_mod=CURRENT_TIMESTAMP,co_use_mod=? \n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?\n"
                + "AND es_eli = '0'");
        
        try {
//            String snuCorDoc = this.jdbcTemplate.queryForObject(sqlQry.toString(),String.class,new Object[]{documentoEmiBean.getNuAnn(),documentoEmiBean.getTiEmi(),
//            documentoEmiBean.getCoDepEmi(),documentoEmiBean.getCoTipDocAdm(),documentoEmiBean.getNuDocEmi()});

            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getEsDocEmi(),documentoEmiBean.getCoUseMod(),
                                    documentoEmiBean.getNuAnn(),documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
        }
        return vReturn;
    }    

    public String updRemitoResumenDestinatario(String vti_des, String vco_pri, String vnu_cant_des, String pnuAnn, String pnuEmi) {
        String vReturn = "NO_OK";
        /*StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_DES = ?,CO_PRIORIDAD = ? ,nu_candes=? ");
        sqlUpd.append("WHERE NU_ANN = ? ");
        sqlUpd.append("AND NU_EMI = ? ");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{
                vti_des, vco_pri, vnu_cant_des, pnuAnn, pnuEmi
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }*/
         this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_REMITOS_RESUMEN_DESTINATARIO_ONLY_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi","p_vti_des","p_vco_pri","p_vnu_cant_des")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR),
                        new SqlParameter("p_vti_des", Types.VARCHAR),
                        new SqlParameter("p_vco_pri", Types.VARCHAR),
                        new SqlParameter("p_vnu_cant_des", Types.VARCHAR));
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", pnuAnn)
                .addValue("p_nuEmi", pnuEmi)
                .addValue("p_vti_des", vti_des)
                .addValue("p_vco_pri", vco_pri)
                .addValue("p_vnu_cant_des", vnu_cant_des);      
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        return vReturn;
    }

    public String updRemitoResumenReferencia(String vti_ori, String pnuAnn, String pnuEmi) {
        String vReturn = "NO_OK";
        /*StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_REF = ? ");
        sqlUpd.append("WHERE NU_ANN = ? ");
        sqlUpd.append("AND NU_EMI = ?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{
                vti_ori, pnuAnn, pnuEmi
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }*/
         this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_REMITOS_RESUMEN_REFERENCIA_ONLY_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi","p_vti_ori")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR),
                        new SqlParameter("p_vti_ori", Types.VARCHAR));
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", pnuAnn)
                .addValue("p_nuEmi", pnuEmi)
                .addValue("p_vti_ori", vti_ori);         
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        return vReturn;
    }

    public String getNumDestinos(String nu_ann, String nu_emi) {
        String vReturn = "0";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT count(nu_des) ");
        sqlQry.append("FROM IDOSGD.tdtv_destinos  WITH (NOLOCK) ");
        sqlQry.append("WHERE nu_ann = ? ");
        sqlQry.append("AND nu_emi = ? ");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{nu_ann, nu_emi});
        } catch (Exception e) {
            vReturn = "0";
            e.printStackTrace();
        }
        return vReturn;
    }

    public List<DestinoBean> getListaDestinosCodDepTipoDes(String nu_ann, String nu_emi) {
        StringBuffer sql = new StringBuffer();
        List<DestinoBean> list = new ArrayList<DestinoBean>();
        sql.append("SELECT DISTINCT co_dep_des, ti_des ");
        sql.append("FROM IDOSGD.tdtv_destinos  WITH (NOLOCK) ");
        sql.append("WHERE es_eli = '0' ");
        sql.append("AND nu_ann = ? ");
        sql.append("AND nu_emi = ? ");
        sql.append("ORDER BY ti_des");
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinoBean.class),
                    new Object[]{nu_ann, nu_emi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ReferenciaRemitoBean> getListaReferenciaRemitos(String nu_ann, String nu_emi) {
        StringBuffer sql = new StringBuffer();
        List<ReferenciaRemitoBean> list = new ArrayList<ReferenciaRemitoBean>();
        sql.append("SELECT DISTINCT b.ti_emi, b.co_dep_emi,b.nu_ann,b.nu_emi,a.nu_des_ref ");
        sql.append("FROM   IDOSGD.tdtr_referencia a WITH (NOLOCK) , IDOSGD.tdtv_remitos b WITH (NOLOCK)  ");
        sql.append("WHERE  a.nu_ann_ref = b.nu_ann ");
        sql.append("AND    a.nu_emi_ref = b.nu_emi ");
        sql.append("AND    a.nu_ann = ? ");
        sql.append("AND    a.nu_emi = ? ");
        sql.append("ORDER  BY b.ti_emi");
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaRemitoBean.class),
                    new Object[]{nu_ann, nu_emi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<DestinoBean> getListaDestinosCodPri(String nu_ann, String nu_emi) {
        StringBuffer sql = new StringBuffer();
        List<DestinoBean> list = new ArrayList<DestinoBean>();
        sql.append("SELECT co_pri ");
        sql.append("FROM IDOSGD.tdtv_destinos  WITH (NOLOCK) ");
        sql.append("WHERE es_eli = '0' ");
        sql.append("AND nu_ann = ? ");
        sql.append("AND nu_emi = ? ");
        sql.append("ORDER BY co_pri desc");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinoBean.class),
                    new Object[]{nu_ann, nu_emi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String getNumeroDocSiguientePersonal(String pnuAnn, String pcoEmp, String pcoDoc) {
        String vReturn = "1";
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT RIGHT(REPLICATE('0', 6) + CAST((ISNULL(MAX(NU_DOC_EMI), 0) + 1) AS VARCHAR), 6) ");
            sql.append("FROM IDOSGD.TDTV_REMITOS TR  WITH (NOLOCK) ");
            sql.append("WHERE TR.NU_ANN=? "); 
            sql.append("AND TR.CO_TIP_DOC_ADM=? ");
            sql.append("AND TR.TI_EMI='05' ");
            sql.append("AND TR.NU_COR_DOC=? "); 
            sql.append("AND ISNUMERIC(NU_DOC_EMI) = 1 ");    
        
            vReturn = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pcoDoc, pcoEmp});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "1";
            //vReturn=e.getMessage();
        }
        vReturn = Utility.getInstancia().rellenaCerosIzquierda(vReturn, 6);
        return vReturn;
    }
    
    @Override
    public DocumentoEmiBean getEstadoDocumento(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        DocumentoEmiBean documentoEmiBean = null;
        sql.append("SELECT CO_EMP_EMI, ");
        sql.append("ES_DOC_EMI, ");
        sql.append("NU_ANN, ");
        sql.append("NU_EMI, ");
        sql.append("TI_EMI, ");
        sql.append("CO_DEP_EMI, ");
        sql.append("CO_TIP_DOC_ADM, ");
        sql.append("NU_DOC_EMI ");
        sql.append("FROM IDOSGD.TDTV_REMITOS  WITH (NOLOCK) ");
        sql.append("WHERE nu_ann = ? ");
        sql.append("AND nu_emi = ? "); 
        sql.append("AND ES_ELI='0' ");       
        
        try {
             documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiBean;             
    }    

    @Override
    public String insPersonalVoBo(String nuAnn, String nuEmi, String coDep, String coEmp, String coUser) {
          String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.TDTV_PERSONAL_VB(NU_ANN,NU_EMI,CO_DEP,\n" +
                    "CO_EMP_VB,IN_VB,CO_USE_CRE,\n" +
                    "FE_USE_CRE,CO_USE_MOD,FE_USE_MOD)\n" +
                    "VALUES(?,?,?,\n" +
                    "?,'0',?,\n" +
                    "CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP)");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn,nuEmi,coDep,coEmp,coUser,coUser});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado INSERT TDTV_PERSONAL_VB.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn; 
    }

    @Override
    public String delPersonalVoBo(String nuAnn, String nuEmi, String coDep, String coEmp) {
        String vReturn = "NO_OK";
        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("DELETE FROM IDOSGD.TDTV_PERSONAL_VB \n" +
                        "WHERE\n" +
                        "NU_ANN=?\n" +
                        "AND NU_EMI=?\n" +
                        "AND CO_DEP=?\n" +
                        "AND CO_EMP_VB=?\n" +
                        "AND IN_VB <> '1'");

        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{nuAnn,nuEmi,coDep,coEmp});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
}
