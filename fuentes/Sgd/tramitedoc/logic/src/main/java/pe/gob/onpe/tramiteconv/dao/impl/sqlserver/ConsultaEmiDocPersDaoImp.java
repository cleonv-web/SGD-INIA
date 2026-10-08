/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiPersConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaEmiDocPersDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author ecueva
 */
@Repository("consultaEmiDocPersDao")
public class ConsultaEmiDocPersDaoImp extends SimpleJdbcDaoBase implements ConsultaEmiDocPersDao{

    @Override
    public List<DocumentoEmiPersConsulBean> getDocsPersConsulta(DocumentoEmiPersConsulBean buscarDocPer) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sqlContains = "";
        Map<String, Object> objectParam = new HashMap<String, Object>();
        
        sql.append("SELECT  Z.* FROM ( ");
        sql.append("	SELECT	X.*, ");
        sql.append("		IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) TIPO_DOC, ");
        sql.append("		(CASE WHEN X.NU_CANDES=1 THEN (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(X.NU_ANN, X.NU_EMI)) ");
        sql.append("		ELSE (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(X.NU_ANN, X.NU_EMI)) END)DE_DEP_DESTINO, ");
        sql.append("		IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (X.ES_DOC_EMI,'TDTV_REMITOS') ESTADO_DOC, ");
        sql.append("		IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_ELABORO, ");
        sql.append("		(SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(X.NU_ANN, X.NU_EMI)) DE_DEP_REF, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY X.NU_COR_EMI DESC) AS ROWNUM ");
        sql.append("	FROM ( ");
        sql.append("		SELECT R.NU_ANN,R.NU_EMI,R.NU_COR_EMI,CONVERT(VARCHAR(10), R.FE_EMI, 103) FE_EMI_CORTA, ");
        sql.append("		RR.NU_DOC,R.CO_TIP_DOC_ADM,R.NU_CANDES,R.ES_DOC_EMI,R.CO_EMP_RES, ");
        sql.append("		R.DE_ASU,RR.NU_EXPEDIENTE,RR.IN_EXISTE_DOC EXISTE_DOC,RR.IN_EXISTE_ANEXO EXISTE_ANEXO, ");
        sql.append("		R.NU_DIA_ATE ");
        sql.append("		FROM IDOSGD.TDTV_REMITOS R  WITH (NOLOCK) ,IDOSGD.TDTX_REMITOS_RESUMEN RR  WITH (NOLOCK)  ");
        sql.append("		WHERE R.NU_ANN=RR.NU_ANN ");
        sql.append("		AND R.NU_EMI=RR.NU_EMI ");  
        
//        String pNUAnn = buscarDocPer.getNuAnn();
//        if(!(buscarDocPer.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))){
//            sql.append(" AND R.NU_ANN = :pNuAnn");
//            // Parametros Basicos
//            objectParam.put("pNuAnn", pNUAnn);                
//        }        

        sql.append(" AND R.TI_EMI='05'");
        sql.append(" AND R.CO_GRU = '2'");        
        sql.append(" AND RR.TI_EMI='05'");
        sql.append(" AND R.ES_ELI = '0'");
      
        String pTipoBusqueda = buscarDocPer.getTipoBusqueda();        

        sql.append(" AND R.CO_EMP_RES = :pcoEmpRes ");
        objectParam.put("pcoEmpRes", buscarDocPer.getCoEmpleado());

        if(pTipoBusqueda.equals("1") && buscarDocPer.isEsIncluyeFiltro()){
            bBusqFiltro=true;
        }            
            
        //Filtro
        if(pTipoBusqueda.equals("0") || bBusqFiltro){
            if (buscarDocPer.getTipoDoc()!= null && buscarDocPer.getTipoDoc().trim().length()>0){
               sql.append(" AND R.CO_TIP_DOC_ADM = :pCoDocEmi ");
               objectParam.put("pCoDocEmi", buscarDocPer.getTipoDoc());
            }
            if (buscarDocPer.getEstadoDoc()!= null && buscarDocPer.getEstadoDoc().trim().length()>0){
               sql.append(" AND R.ES_DOC_EMI = :pEsDocEmi ");
               objectParam.put("pEsDocEmi", buscarDocPer.getEstadoDoc());
            }
            if (buscarDocPer.getCoDepDestino()!= null && buscarDocPer.getCoDepDestino().trim().length()>0){
               sql.append(" AND CHARINDEX(:pTiEmiDes, RR.TI_EMI_DES) > 0 ");
               objectParam.put("pTiEmiDes", buscarDocPer.getCoDepDestino());
            }
            if (buscarDocPer.getCoDepRef()!= null && buscarDocPer.getCoDepRef().trim().length()>0){
               sql.append(" AND CHARINDEX(:pTiEmiRef, RR.TI_EMI_REF) > 0 ");
               objectParam.put("pTiEmiRef", buscarDocPer.getCoDepRef());
            }            
            if(buscarDocPer.getEsFiltroFecha()!= null && 
               (buscarDocPer.getEsFiltroFecha().equals("1") || buscarDocPer.getEsFiltroFecha().equals("2") || buscarDocPer.getEsFiltroFecha().equals("3"))){
                String vFeEmiIni = buscarDocPer.getFeEmiIni();
                String vFeEmiFin = buscarDocPer.getFeEmiFin();       
                if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                   vFeEmiFin!= null && vFeEmiFin.trim().length()>0){
                    
                    sql.append(" AND R.FE_EMI BETWEEN CONVERT(DATETIME, :pFeEmiIni, 103) AND CONVERT(DATETIME, :pFeEmiFin, 103) + 0.99999 ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);                    
                }
            }

        }    

        //Busqueda
        if (pTipoBusqueda.equals("1"))
        {
            if (buscarDocPer.getNuCorEmi()!= null && buscarDocPer.getNuCorEmi().trim().length()>0){
               sql.append(" AND R.NU_COR_EMI = :pnuCorEmi ");
               objectParam.put("pnuCorEmi", buscarDocPer.getNuCorEmi());
            }

            if (buscarDocPer.getNuDoc()!= null && buscarDocPer.getNuDoc().trim().length()>1){
               sql.append(" AND RR.NU_DOC LIKE '%'+:pnuDocEmi+'%' ");
               objectParam.put("pnuDocEmi", buscarDocPer.getNuDoc());
            }

            // Busqueda del Asunto
            if (buscarDocPer.getDeAsu()!= null && buscarDocPer.getDeAsu().trim().length()>1){
//                sql.append(" AND CONTAINS(R.*, :pBusquedaTextual) ");
//                sqlContains = "SELECT RETORNO FROM [IDOSGD].PK_SGD_DESCRIPCION_QUERYFILTER('"+buscarDocPer.getDeAsu()+"')";
                  sql.append(" AND UPPER(R.DE_ASU) LIKE UPPER('%'+:pDeAsunto+'%') ");
                     objectParam.put("pDeAsunto", buscarDocPer.getDeAsu());
            }
        }
        
        sql.append("	) X ");
        sql.append(") Z ");
        sql.append("WHERE Z.ROWNUM < 201 ");
        sql.append("ORDER BY Z.NU_COR_EMI DESC ");

        List<DocumentoEmiPersConsulBean> list;
        try {
            //Obteniendo el parametro textual si es requerido
//            if (sqlContains.length() > 0) {
//                String cadena = this.jdbcTemplate.queryForObject(sqlContains, String.class);            
//                objectParam.put("pBusquedaTextual", cadena);
//            }
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiPersConsulBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public DocumentoEmiPersConsulBean getDocumentoPersonalEmi(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT R.NU_ANN,R.NU_EMI,R.NU_COR_EMI,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(R.CO_DEP_EMI) DE_DEP_EMI,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(R.CO_LOC_EMI) DE_LOC_EMI,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(R.CO_EMP_EMI) DE_EMP_FIRMO,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(R.CO_EMP_RES) DE_EMP_ELABORO,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(R.CO_TIP_DOC_ADM) TIPO_DOC,\n" +
                    "RR.NU_DOC,R.DE_ASU,R.NU_DIA_ATE,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(R.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,\n"+
                    "CONVERT(VARCHAR(10),R.FE_EMI,103)FE_EMI_CORTA\n" +
                    "FROM IDOSGD.TDTV_REMITOS R WITH (NOLOCK) ,IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK) \n" +
                    "WHERE R.NU_ANN=?\n" +
                    "AND R.NU_EMI=?\n" +
                    "AND RR.NU_ANN=R.NU_ANN\n" +
                    "AND RR.NU_EMI=R.NU_EMI\n" +
                    "AND R.TI_EMI='05'\n" +
                    "AND RR.TI_EMI='05'\n" +
                    "AND R.ES_ELI='0'");        
        
        DocumentoEmiPersConsulBean documentoEmiBean=null;
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiPersConsulBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiBean;             
    }
    
    
    @Override
    public List<DestinatarioDocumentoEmiConsulBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer(); 
        List<DestinatarioDocumentoEmiConsulBean> list;                
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,SUBSTRING(IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(a.co_loc_des), 1, 100) de_local,\n" +
                    " a.co_dep_des co_dependencia,SUBSTRING(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_des), 1, 100) de_dependencia,\n" +
                    " a.co_emp_des co_empleado,SUBSTRING(IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des), 1, 100) de_empleado,\n" +
                    " a.co_mot co_tramite,SUBSTRING(IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(a.co_mot), 1, 100) de_tramite,\n" +
                    " a.co_pri co_prioridad,(SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD(a.co_pri)) de_prioridad,\n" +
                    " a.de_pro de_indicaciones,\n" +
                    " a.ti_des co_tipo_destino\n" +
                    " FROM IDOSGD.tdtv_destinos a WITH (NOLOCK) \n" +
                    " where  A.TI_DES = '01' and a.nu_ann = ? and a.nu_emi = ?\n" +
                    " AND a.ES_ELI='0' AND a.NU_EMI_REF is null\n" +
                    " order by A.NU_COR_DES");        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiConsulBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }
    
    @Override
    public List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer(); 
        List<ReferenciaConsulBean> list;                
        sql.append("SELECT \n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tipo_doc, \n" +
                    " (CASE WHEN a.ti_emi='01' OR a.ti_emi='05' THEN a.nu_doc_emi + '-' + a.nu_ann + '-' + a.de_doc_sig ELSE a.de_doc_sig END)li_nu_doc,\n" +
                    " SUBSTRING(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_emi), 1, 100) de_dep_emi, \n" +
                    " CONVERT(VARCHAR(10),a.fe_emi,103) fe_emi_corta, \n" +
                    " b.nu_ann,\n" +
                    " b.nu_emi,\n" +
                    " COALESCE(RTRIM(Ltrim(CAST(b.nu_des AS VARCHAR))),'N') nu_des ,\n" +
                    " b.nu_ann_ref,\n" +
                    " b.nu_emi_ref,\n" +
                    " COALESCE(RTRIM(Ltrim(CAST(b.nu_des_ref AS VARCHAR))),'N') nu_des_ref,\n" +
                    " b.co_ref,\n" +
                    " (CASE WHEN COALESCE(RTRIM(LTRIM(CAST(b.nu_des_ref AS VARCHAR))),'N')='N' THEN 'emi' ELSE 'rec' END)tip_doc_ref,\n" +
                    " a.co_tip_doc_adm,'BD' accion_bd\n" +
                    " FROM IDOSGD.tdtv_remitos a WITH (NOLOCK) ,IDOSGD.TDTR_REFERENCIA b WITH (NOLOCK) \n" +
                    " WHERE a.nu_ann = b.nu_ann_ref\n" +
                    " AND a.nu_emi = b.nu_emi_ref\n" +
                    " and b.nu_ann = ?\n" +
                    " and b.nu_emi = ?");
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaConsulBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }

    @Override
    public String getRutaReporte(DocumentoEmiPersConsulBean buscarDocPer) {
        String vResult="0";
        StringBuffer sql = new StringBuffer();
        try {
            sql.append(" R.NU_ANN=RR.NU_ANN");
            sql.append(" AND R.NU_EMI=RR.NU_EMI");               
            String pNUAnn = buscarDocPer.getNuAnn();
            if(!(buscarDocPer.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))){
                sql.append(" AND R.NU_ANN = '").append(pNUAnn).append("'");
            }             
            sql.append(" AND R.TI_EMI='05'");
            sql.append(" AND RR.TI_EMI='05'");
            sql.append(" AND R.ES_ELI = '0'");  
            
            sql.append(" AND R.CO_EMP_RES = '").append( buscarDocPer.getCoEmpleado()).append("'");
            
            if (buscarDocPer.getTipoDoc()!= null && buscarDocPer.getTipoDoc().trim().length()>0){
               sql.append(" AND R.CO_TIP_DOC_ADM = '").append(buscarDocPer.getTipoDoc()).append("'");
            }
            if (buscarDocPer.getEstadoDoc()!= null && buscarDocPer.getEstadoDoc().trim().length()>0){
               sql.append(" AND R.ES_DOC_EMI = '").append(buscarDocPer.getEstadoDoc()).append("'");
            }
            if (buscarDocPer.getCoDepDestino()!= null && buscarDocPer.getCoDepDestino().trim().length()>0){
               sql.append(" AND CHARINDEX('").append(buscarDocPer.getCoDepDestino()).append("',RR.TI_EMI_DES ) > 0 ");
            }
            if (buscarDocPer.getCoDepRef()!= null && buscarDocPer.getCoDepRef().trim().length()>0){
               sql.append(" AND CHARINDEX('").append(buscarDocPer.getCoDepRef()).append("',RR.TI_EMI_REF ) > 0 ");
            }            
            if(buscarDocPer.getEsFiltroFecha()!= null && 
               (buscarDocPer.getEsFiltroFecha().equals("1") || buscarDocPer.getEsFiltroFecha().equals("3"))){
                String vFeEmiIni = buscarDocPer.getFeEmiIni();
                String vFeEmiFin = buscarDocPer.getFeEmiFin();       
                if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                   vFeEmiFin!= null && vFeEmiFin.trim().length()>0){                                         
                    sql.append(" AND R.FE_EMI BETWEEN CONVERT(DATETIME, '").append(vFeEmiIni).append("', 103) AND CONVERT(DATETIME, '").append(vFeEmiFin).append("', 103) + 0.99999 ");
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
    public List<DocumentoEmiPersConsulBean> getListaReporteBusqueda(DocumentoEmiPersConsulBean buscarDocPer)
    {
        String vResult="0";
        boolean bBusqFiltro = false;
        String sqlContains = "";
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List<DocumentoEmiPersConsulBean> list;
        
        sql.append("SELECT	R.NU_COR_EMI,\n" +
                    "	CONVERT(VARCHAR(10), R.FE_EMI, 103) FE_EMI_CORTA,\n" +
                    "	(SELECT CDOC_DESDOC\n" +
                    "	FROM IDOSGD.SI_MAE_TIPO_DOC\n" +
                    "	WHERE CDOC_TIPDOC = R.CO_TIP_DOC_ADM) TIPO_DOC,RR.NU_DOC,\n" +
                    "	CASE R.NU_CANDES\n" +
                    "		WHEN 1 THEN (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP](R.NU_ANN, R.NU_EMI))\n" +
                    "		ELSE (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP_V](R.NU_ANN, R.NU_EMI))\n" +
                    "	END DE_DEP_DESTINO,\n" +
                    "	(SELECT DE_EST\n" +
                    "	FROM IDOSGD.TDTR_ESTADOS WITH (NOLOCK) \n" +
                    "	WHERE CO_EST + DE_TAB = R.ES_DOC_EMI + 'TDTV_REMITOS') ESTADO_DOC,\n" +
                    "	(SELECT CEMP_APEPAT + ' ' + CEMP_APEMAT + ' ' + CEMP_DENOM\n" +
                    "	FROM IDOSGD.RHTM_PER_EMPLEADOS WITH (NOLOCK) \n" +
                    "	WHERE CEMP_CODEMP = R.CO_EMP_RES) DE_EMP_ELABORO,\n" +
                    "	R.DE_ASU,\n" +
                    "	RR.NU_EXPEDIENTE,\n" +
                    "	(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_DE_EMI_REF](R.NU_ANN, R.NU_EMI)) DE_DEP_REF,\n" +
                    "	R.NU_DIA_ATE\n" +
                    "FROM IDOSGD.TDTV_REMITOS R WITH (NOLOCK) ,\n" +
                    "     IDOSGD.TDTX_REMITOS_RESUMEN RR  WITH (NOLOCK) WHERE ");       
        
        try {
            sql.append(" R.NU_ANN=RR.NU_ANN");
            sql.append(" AND R.NU_EMI=RR.NU_EMI");               
            String pNUAnn = buscarDocPer.getNuAnn();
            if(!(buscarDocPer.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))){
                //sql.append(" AND R.NU_ANN = '").append(pNUAnn).append("'");
                sql.append(" AND R.NU_ANN = :pNuAnn");
                // Parametros Basicos
                objectParam.put("pNuAnn", pNUAnn);                
            }             
            sql.append(" AND R.TI_EMI='05'");
            sql.append(" AND RR.TI_EMI='05'");
            sql.append(" AND R.ES_ELI = '0'");  
            
            //sql.append(" AND R.CO_EMP_RES = '").append( buscarDocPer.getCoEmpleado()).append("'");
            sql.append(" AND R.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", buscarDocPer.getCoEmpleado());
        
            String pTipoBusqueda = buscarDocPer.getTipoBusqueda();
            if(pTipoBusqueda.equals("1") && buscarDocPer.isEsIncluyeFiltro()){
                bBusqFiltro=true;
            }
            
            
            //Filtro
            if (pTipoBusqueda.equals("0") || bBusqFiltro) {
                if (buscarDocPer.getTipoDoc()!= null && buscarDocPer.getTipoDoc().trim().length()>0){
                   //sql.append(" AND R.CO_TIP_DOC_ADM = '").append(buscarDocPer.getTipoDoc()).append("'");
                   sql.append(" AND R.CO_TIP_DOC_ADM = :pCoDocEmi ");
                   objectParam.put("pCoDocEmi", buscarDocPer.getTipoDoc());
                }
                if (buscarDocPer.getEstadoDoc()!= null && buscarDocPer.getEstadoDoc().trim().length()>0){
                   //sql.append(" AND R.ES_DOC_EMI = '").append(buscarDocPer.getEstadoDoc()).append("'");
                   sql.append(" AND R.ES_DOC_EMI = :pEsDocEmi ");
                   objectParam.put("pEsDocEmi", buscarDocPer.getEstadoDoc());
                }
                if (buscarDocPer.getCoDepDestino()!= null && buscarDocPer.getCoDepDestino().trim().length()>0){
                   //sql.append(" AND CHARINDEX('").append(buscarDocPer.getCoDepDestino()).append("',RR.TI_EMI_DES ) > 0 ");
                   sql.append(" AND CHARINDEX(:pTiEmiDes, RR.TI_EMI_DES) > 0 ");
                   objectParam.put("pTiEmiDes", buscarDocPer.getCoDepDestino());
                }
                if (buscarDocPer.getCoDepRef()!= null && buscarDocPer.getCoDepRef().trim().length()>0){
                   //sql.append(" AND CHARINDEX('").append(buscarDocPer.getCoDepRef()).append("',RR.TI_EMI_REF ) > 0 ");
                   sql.append(" AND CHARINDEX(:pTiEmiRef, RR.TI_EMI_REF) > 0 ");
                   objectParam.put("pTiEmiRef", buscarDocPer.getCoDepRef());
                }            
                if(buscarDocPer.getEsFiltroFecha()!= null && 
                   (buscarDocPer.getEsFiltroFecha().equals("1") || buscarDocPer.getEsFiltroFecha().equals("3"))){
                    String vFeEmiIni = buscarDocPer.getFeEmiIni();
                    String vFeEmiFin = buscarDocPer.getFeEmiFin();       
                    if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                       vFeEmiFin!= null && vFeEmiFin.trim().length()>0){                     
                        sql.append(" AND R.FE_EMI BETWEEN CONVERT(DATETIME, :pFeEmiIni, 103) AND CONVERT(DATETIME, :pFeEmiFin, 103) + 0.99999 ");
                        objectParam.put("pFeEmiIni", vFeEmiIni);
                        objectParam.put("pFeEmiFin", vFeEmiFin);     
                    }
                }   
            }
            
            //Busqueda
            if (pTipoBusqueda.equals("1")) {
                if (buscarDocPer.getNuCorEmi()!= null && buscarDocPer.getNuCorEmi().trim().length()>0){
                   sql.append(" AND R.NU_COR_EMI = :pnuCorEmi ");
                   objectParam.put("pnuCorEmi", buscarDocPer.getNuCorEmi());
                }

                if (buscarDocPer.getNuDoc()!= null && buscarDocPer.getNuDoc().trim().length()>1){
                   sql.append(" AND RR.NU_DOC LIKE '%'+:pnuDocEmi+'%' ");
                   objectParam.put("pnuDocEmi", buscarDocPer.getNuDoc());
                }

                // Busqueda del Asunto
                if (buscarDocPer.getDeAsu()!= null && buscarDocPer.getDeAsu().trim().length()>1){
//                    sql.append(" AND CONTAINS(R.*, :pBusquedaTextual) ");
//                    sqlContains = "SELECT RETORNO FROM [IDOSGD].PK_SGD_DESCRIPCION_QUERYFILTER('"+buscarDocPer.getDeAsu()+"')";
                  sql.append(" AND UPPER(R.DE_ASU) LIKE '%'+:pDeAsunto+'%' ");
                     objectParam.put("pDeAsunto", buscarDocPer.getDeAsu());
                }
            }
            
            sql.append("ORDER BY R.NU_COR_EMI DESC ");

            //Obteniendo el parametro textual si es requerido
//            if (sqlContains.length() > 0) {
//                String cadena = this.jdbcTemplate.queryForObject(sqlContains, String.class);            
//                objectParam.put("pBusquedaTextual", cadena);
//            }
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiPersConsulBean.class));
        } catch (Exception ex) {
            //vResult="1"+ex.getMessage();
            list = null;
            ex.printStackTrace();            
        }
        
        return list;
    }
}

