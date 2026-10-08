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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ProcesoExpBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RequisitoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.dao.MesaPartesDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.BusquedaTextual;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ECueva
 */
@Repository("mesaPartesDao")
public class MesaPartesDaoImp extends SimpleJdbcDaoBase  implements MesaPartesDao{
    @Autowired
    private ApplicationProperties applicationProperties;
    @Override
    public List<DocumentoExtRecepBean> getDocumentosExtRecep(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean) {
        boolean bBusqFiltro = false;
        boolean bBusqDep = false;
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();        
        sql.append("SELECT X.*,");
        sql.append("IDOSGD.PK_SGD_DESCRIPCION_TI_DESTINO (X.TI_EMI) DE_TI_EMI,"); 
        sql.append("CASE\n"
                + "WHEN X.TI_EMI='01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA (X.CO_DEP_EMI)|| ' - '|| IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.CO_EMP_EMI)\n"
                + "WHEN X.TI_EMI='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR (X.NU_RUC_EMI)\n"
                + "WHEN X.TI_EMI='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (X.NU_DNI_EMI)\n"                
                + "WHEN X.TI_EMI='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (X.CO_OTR_ORI_EMI)\n"                
                + "WHEN X.TI_EMI='05' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (X.CO_EMP_EMI)\n"                                
                + "ELSE NULL END DE_ORI_EMI_MP,");        
        sql.append("IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,"); 
        sql.append("CASE\n"
                + "WHEN X.TI_EMI='01' THEN X.NU_DOC_EMI || '-' || X.NU_ANN || '/' || X.DE_DOC_SIG\n"
                + "WHEN X.TI_EMI='05' THEN X.NU_DOC_EMI || '-' || X.NU_ANN || '/' || X.DE_DOC_SIG\n"                                
                + "ELSE X.DE_DOC_SIG END NU_DOC,");           
        sql.append("CASE\n"
                + "WHEN X.NU_CANDES=1 THEN IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(X.NU_ANN, X.NU_EMI)\n"
                + "ELSE IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(X.NU_ANN, X.NU_EMI)\n"
                + "END DE_EMP_DES,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS_MP(X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI_MP,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(X.CO_LOC_EMI) DE_LOC_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(X.CO_DEP) DE_DEPENDENCIA");
        sql.append(" FROM ( ");
        sql.append(" SELECT A.NU_ANN,A.NU_EMI,B.NU_EXPEDIENTE,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA, TO_CHAR(C.FE_EXP,'DD/MM/YYYY') FE_EXP, IDOSGD.PK_SGD_DESCRIPCION_DE_DOMINIOS('TIP_EXPEDIENTE',C.CCOD_TIPO_EXP) CO_TIPO_EXP,");
        sql.append(" A.DE_ASU,A.CO_DEP_EMI,A.CO_EMP_EMI,A.CO_OTR_ORI_EMI,A.CO_TIP_DOC_ADM,A.NU_DOC_EMI,A.DE_DOC_SIG,");
        sql.append(" B.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append(" A.TI_EMI,A.NU_RUC_EMI,A.NU_DNI_EMI,A.NU_CANDES,A.ES_DOC_EMI,A.CO_LOC_EMI,");
        sql.append(" A.CO_DEP ,A.CCOD_ORIGING ");
        sql.append(" FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTX_REMITOS_RESUMEN B , IDOSGD.TDTC_EXPEDIENTE C ");
        sql.append(" WHERE");
        sql.append(" B.NU_ANN=A.NU_ANN");
        sql.append(" AND B.NU_EMI=A.NU_EMI"); 
        sql.append(" AND C.NU_ANN_EXP=A.NU_ANN_EXP AND C.NU_SEC_EXP=A.NU_SEC_EXP");   
        String pNuAnn = buscarDocumentoExtRecepBean.getCoAnnio();
        String pEsFiltroFecha = buscarDocumentoExtRecepBean.getEsFiltroFecha();
        if (!(pEsFiltroFecha.equals("1")&&pNuAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNuAnn);
        }
        sql.append(" AND A.CO_GRU = :pCoGru");        
        objectParam.put("pCoGru", buscarDocumentoExtRecepBean.getCoGrupo());
//        sql.append(" AND A.ES_DOC_EMI<>9");
        sql.append(" AND A.TI_EMI<>'01'");
        sql.append(" AND A.ES_ELI='0'");
        sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi");        
        objectParam.put("pCoDepEmi", buscarDocumentoExtRecepBean.getCoDepEmi());

        String pTipoBusqueda = buscarDocumentoExtRecepBean.getTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoExtRecepBean.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        
        String auxTipoAcceso=buscarDocumentoExtRecepBean.getTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
        if(tiAcceso.equals("1")){//acceso personal
            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", buscarDocumentoExtRecepBean.getCoEmpleado());            
        }else {//acceso total
            if(buscarDocumentoExtRecepBean.getInMesaPartes().equals("0") /*&& buscarDocumentoExtRecepBean.getInCambioEst().equals("0")*/){
                bBusqDep = true;
                sql.append(" AND A.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", buscarDocumentoExtRecepBean.getCoDependencia());                   
            }
        }/*else if(tiAcceso.equals("0")){//acceso total
            if(!buscarDocumentoExtRecepBean.getInCambioEst().equals("1")){
                bBusqLocal = true;
                sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi");
                objectParam.put("pcoLocEmi", buscarDocumentoExtRecepBean.getCoLocal());
            }
        }*/
        
        //Filtro
        //if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (buscarDocumentoExtRecepBean.getCoTipoDoc()!= null && buscarDocumentoExtRecepBean.getCoTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoExtRecepBean.getCoTipoDoc());
            }            
            if (buscarDocumentoExtRecepBean.getCoEstadoDoc()!= null && buscarDocumentoExtRecepBean.getCoEstadoDoc().trim().length() > 0) {
                sql.append(" AND A.ES_DOC_EMI = :pCoEsDocEmi ");
                objectParam.put("pCoEsDocEmi", buscarDocumentoExtRecepBean.getCoEstadoDoc());
            }
            if(buscarDocumentoExtRecepBean.getCoTipoEmisor()!= null && buscarDocumentoExtRecepBean.getCoTipoEmisor().trim().length() > 0){
                sql.append(" AND A.TI_EMI = :pCoTipoEmisor ");
                objectParam.put("pCoTipoEmisor", buscarDocumentoExtRecepBean.getCoTipoEmisor());                
            }
            /*if (buscarDocumentoExtRecepBean.getCoLocEmi()!= null && buscarDocumentoExtRecepBean.getCoLocEmi().trim().length() > 0 && !bBusqLocal) {
                sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi ");
                objectParam.put("pcoLocEmi", buscarDocumentoExtRecepBean.getCoLocEmi());
            } */     
            if (buscarDocumentoExtRecepBean.getCoDepOriRec()!= null && buscarDocumentoExtRecepBean.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                sql.append(" AND A.CO_DEP = :pCoDep");        
                objectParam.put("pCoDep", buscarDocumentoExtRecepBean.getCoDepOriRec());                
            }
            if(buscarDocumentoExtRecepBean.getCoProceso()!=null&&buscarDocumentoExtRecepBean.getCoProceso().trim().length()>0){
                if(buscarDocumentoExtRecepBean.getCoProceso().equals("CON_TUPA")){
                    sql.append(" AND B.CO_PROCESO_EXP not in ('0000') "); 
                }else{
                    sql.append(" AND B.CO_PROCESO_EXP = :pcoProceso ");
                    objectParam.put("pcoProceso", buscarDocumentoExtRecepBean.getCoProceso()); 
                }
            }
            
            if (buscarDocumentoExtRecepBean.getEsFiltroFecha() != null
                    && (pEsFiltroFecha.equals("1") || pEsFiltroFecha.equals("3"))) {
                String vFeEmiIni = buscarDocumentoExtRecepBean.getFeEmiIni();
                String vFeEmiFin = buscarDocumentoExtRecepBean.getFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59'");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }            
       // }   
        
        //Busqueda
       // if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoExtRecepBean.getBusNumEmision() != null && buscarDocumentoExtRecepBean.getBusNumEmision().trim().length() > 0) {
                    sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                    objectParam.put("pnuCorEmi", Integer.getInteger(buscarDocumentoExtRecepBean.getBusNumEmision()));
            }
            if (buscarDocumentoExtRecepBean.getBusNumDoc() != null && buscarDocumentoExtRecepBean.getBusNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoExtRecepBean.getBusNumDoc());
            }
            if (buscarDocumentoExtRecepBean.getBusNumExpediente() != null && buscarDocumentoExtRecepBean.getBusNumExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoExtRecepBean.getBusNumExpediente());
            }
            if (buscarDocumentoExtRecepBean.getBusAsunto() != null && buscarDocumentoExtRecepBean.getBusAsunto().trim().length() > 1) {
//                sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
//                sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
                objectParam.put("pDeAsunto", buscarDocumentoExtRecepBean.getBusAsunto());
            }     
            if (buscarDocumentoExtRecepBean.getCoTipoExp() != null && !buscarDocumentoExtRecepBean.getCoTipoExp().equals("")) {
                sql.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                objectParam.put("codTipoExp", buscarDocumentoExtRecepBean.getCoTipoExp()); 
            }
            
            if (buscarDocumentoExtRecepBean.getCoOriDoc()!= null && !buscarDocumentoExtRecepBean.getCoOriDoc().equals("")) {
                sql.append(" AND A.CCOD_ORIGING = :coOriDoc");        
                objectParam.put("coOriDoc", buscarDocumentoExtRecepBean.getCoOriDoc()); 
            }


            if(buscarDocumentoExtRecepBean.getBusResultado().equals("1"))
            {
                if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("03")){
                sql.append(" AND A.NU_DNI_EMI = :pNumDni");
                objectParam.put("pNumDni", buscarDocumentoExtRecepBean.getBusNumDni());
                }
                else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("02")){
                    sql.append(" AND A.NU_RUC_EMI = :pNumRuc");
                    objectParam.put("pNumRuc", buscarDocumentoExtRecepBean.getBusNumRuc());
                }
                else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("04")){
                    sql.append(" AND A.CO_OTR_ORI_EMI = :pCoOtr");
                    objectParam.put("pCoOtr", buscarDocumentoExtRecepBean.getBusCoOtros());
                }
            }
            
      //  }        
        
        sql.append(" ORDER BY A.NU_COR_EMI DESC");
        sql.append(") X ");
        sql.append(" LIMIT ").append(applicationProperties.getTopRegistrosConsultas());;
        
        List<DocumentoExtRecepBean> list = new ArrayList<DocumentoExtRecepBean>();

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(),objectParam,BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public String insExpedienteBean(ExpedienteDocExtRecepBean expedienteBean) {
        String vReturn = "NO_OK";
        String clave = Utilidades.generateRandomLetter(4);
        StringBuffer sqlQry1 = new StringBuffer();
        sqlQry1.append("select LPAD(TRIM(cast((cast(COALESCE(max(nu_sec_exp),'0')as int) +1) as text)),10,'0') \n"
                + "from IDOSGD.tdtc_expediente\n"
                + "where nu_ann_exp = ?");        
        
        StringBuffer sqlIns = new StringBuffer();
        sqlIns.append("INSERT INTO IDOSGD.TDTC_EXPEDIENTE(\n" +
                        "nu_ann_exp,\n" +
                        "nu_sec_exp,\n" +
                        "fe_exp,\n" +
                        "fe_vence,\n" +
                        "co_proceso,\n" +
                        "de_detalle,\n" +
                        "co_dep_exp,\n" +
                        "co_gru,\n" +
                        "nu_corr_exp,\n" +
                        "nu_expediente,\n" +
                        "us_crea_audi,\n" +
                        "fe_crea_audi,\n" +
                        "us_modi_audi,\n" +
                        "fe_modi_audi,\n" +
                        "es_estado,\n" +
                        "ccod_tipo_exp,CCLAVE)\n" +
                        "VALUES(?,?,(select to_timestamp(?,'dd/mm/yyyy hh24:mi:ss'))," +
                        "(select to_timestamp(NULLIF(?,''),'dd/mm/yyyy hh24:mi:ss')),?,?,?,'3',?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,'0',?, ?)");
        try{
            String snuSecExp = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class, new Object[]{expedienteBean.getNuAnnExp()});            
            expedienteBean.setNuSecExp(snuSecExp);
            
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{expedienteBean.getNuAnnExp(), snuSecExp, expedienteBean.getFeExp(),
                expedienteBean.getFeVence(),expedienteBean.getCoProceso(), expedienteBean.getDeDetalle(), expedienteBean.getCoDepEmi(),
                expedienteBean.getNuCorrExp(), expedienteBean.getNuExpediente(),expedienteBean.getUsCreaAudi(), 
                expedienteBean.getUsCreaAudi(), expedienteBean.getCoTipoExp(), clave});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Expediente Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    @Override
    public String updExpedienteBean(ExpedienteDocExtRecepBean expedienteBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update IDOSGD.TDTC_EXPEDIENTE \n"
                + "set US_MODI_AUDI=?\n"
                + ",CCOD_TIPO_EXP=?\n"
                + ",FE_VENCE=to_timestamp(NULLIF(?,''),'dd/mm/yyyy hh24:mi:ss')\n"
                + ",CO_PROCESO=?\n"
                + ",FE_MODI_AUDI=CURRENT_TIMESTAMP\n"
                + "where\n"
                + "NU_ANN_EXP=? and\n"
                + "NU_SEC_EXP=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{expedienteBean.getUsCreaAudi(), expedienteBean.getCoTipoExp(),expedienteBean.getFeVence(),
            expedienteBean.getCoProceso(),expedienteBean.getNuAnnExp(),expedienteBean.getNuSecExp()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }    
    
    @Override
    public String updDocumentoExtBean(String nuAnn, String nuEmi, DocumentoExtRecepBean documentoExtRecepBean, ExpedienteDocExtRecepBean expedienteBean, RemitenteDocExtRecepBean remitenteDocExtRecepBean, String pcoUserMod){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos  \n"
                + "set CO_USE_MOD=?,");
        if (documentoExtRecepBean != null) {
            documentoExtRecepBean.setNuAnn(nuAnn);             
            sqlUpd.append("DE_ASU=?\n"
                    + ",COBS_DOCUMENTO=?\n"
                    + ",DE_DOC_SIG=?\n"
                    + ",CO_TIP_DOC_ADM=?\n"
                    + ",NU_FOLIOS=cast(? as double precision)\n"
                    + ",NU_DIA_ATE=cast(? as smallint),\n");
            sqlUpd.append("CCOD_ORIGING=?,\n");            
            sqlUpd.append("CNUM_DNIMSG=?,\n");
            sqlUpd.append("NRO_SOBREAUT=?,\n");
            sqlUpd.append("AN_SOBREAUT=?,\n");            
            sqlUpd.append("CDOC_DESTRAM=?,\n");
            
            //para verificar numero correlativo de documento
            if(documentoExtRecepBean.getNuCorDoc()==null || documentoExtRecepBean.getNuCorDoc().trim().equals("") ){
                String vnuCorDoc = getNroCorrelativoDocumento(nuAnn,documentoExtRecepBean.getCoDepEmi(),documentoExtRecepBean.getTiEmi());
                documentoExtRecepBean.setNuCorDoc(vnuCorDoc);
                sqlUpd.append("NU_COR_DOC=").append(vnuCorDoc).append(",\n");
            }
            
            sqlUpd.append("CIND_SENSIBLE='" ).append(documentoExtRecepBean.getSensible()==null||documentoExtRecepBean.getSensible().equals("null")?"0":documentoExtRecepBean.getSensible()).append("',\n");
            sqlUpd.append("NU_COPIA='" ).append(documentoExtRecepBean.getNuCopia()==null||documentoExtRecepBean.getNuCopia().equals("null")?"1":documentoExtRecepBean.getNuCopia()).append("',\n");
            sqlUpd.append("NU_ANEXO='" ).append(documentoExtRecepBean.getNuAnexo()==null||documentoExtRecepBean.getNuAnexo().equals("null")?" ":documentoExtRecepBean.getNuAnexo()).append("',\n");
            
//            //Para verificar si ya tiene un numero correlativo de emision
//            if (documentoExtRecepBean.getNuCorEmi()==null || documentoExtRecepBean.getNuCorEmi().trim().equals("") ){
//                String vnuCorEmi = getNroCorrelativoEmision(documentoExtRecepBean.getNuAnn(),documentoExtRecepBean.getCoDepEmi());
//                documentoExtRecepBean.setNuCorEmi(vnuCorEmi);
//                sqlUpd.append("A.NU_COR_EMI=").append(vnuCorEmi).append(",\n");
//            }
        }
//        if (expedienteBean != null) {
//            sqlUpd.append("A.NU_ANN_EXP='").append(expedienteBean.getNuAnnExp()).append("',\n");
//            sqlUpd.append("A.NU_SEC_EXP='").append(expedienteBean.getNuSecExp()).append("',\n");
//        }
        if (remitenteDocExtRecepBean != null) {
            /*sqlUpd.append("TI_EMI='").append(remitenteDocExtRecepBean.getTiEmi()).append("',\n");
            if(remitenteDocExtRecepBean.getNuRuc()!=null&&remitenteDocExtRecepBean.getNuRuc().trim().length() > 0){
              sqlUpd.append("NU_RUC_EMI='" ).append(remitenteDocExtRecepBean.getNuRuc()).append("',\n");
            }else if(remitenteDocExtRecepBean.getNuDni()!=null&&remitenteDocExtRecepBean.getNuDni().trim().length() > 0){
              sqlUpd.append("NU_DNI_EMI='" ).append(remitenteDocExtRecepBean.getNuDni()).append("',\n");  
            }else if(remitenteDocExtRecepBean.getCoOtros()!=null&&remitenteDocExtRecepBean.getCoOtros().trim().length() > 0){
              sqlUpd.append("CO_OTR_ORI_EMI='" ).append(remitenteDocExtRecepBean.getCoOtros()).append("',\n");    
            }*/
            sqlUpd.append("TI_EMI=?,\n");            
            sqlUpd.append("NU_RUC_EMI=?,\n");
            sqlUpd.append("NU_DNI_EMI=?,\n");  
            sqlUpd.append("CO_OTR_ORI_EMI=?,\n");            
            sqlUpd.append("CDIR_REMITE=?,\n");    
            sqlUpd.append("CEXP_CORREOE=?,\n");   
            sqlUpd.append("CTELEFONO=?,\n");    
            sqlUpd.append("REMI_TI_EMI=?,\n");
            sqlUpd.append("REMI_NU_DNI_EMI=?,\n");
            sqlUpd.append("REMI_CARGO=?,\n");    
            sqlUpd.append("CONG_CO_OTR_ORI=?,\n");    
            sqlUpd.append("IND_TIPOCONG=?,\n");    
            sqlUpd.append("IND_TIPOCONGINV=?,\n");    
            sqlUpd.append("REMI_CO_OTR_ORI_EMI=?,\n");    
            sqlUpd.append("CCOD_DPTO=?,\n");    
            sqlUpd.append("CCOD_PROV=?,\n");    
            sqlUpd.append("CCOD_DIST=?,\n");       
            
            sqlUpd.append("AUT_CORREOE='" ).append(remitenteDocExtRecepBean.getNotificado()==null||remitenteDocExtRecepBean.getNotificado().equals("null")?"0":remitenteDocExtRecepBean.getNotificado()).append("',\n");
            sqlUpd.append("IND_REITCONGINV='" ).append(remitenteDocExtRecepBean.getReiterativo()==null||remitenteDocExtRecepBean.getReiterativo().equals("null")?"0":remitenteDocExtRecepBean.getReiterativo()).append("',\n");
            
        }
        sqlUpd.append("FE_USE_MOD=CURRENT_TIMESTAMP "
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=? ");

        try {
            if (documentoExtRecepBean != null && remitenteDocExtRecepBean == null) {
                this.jdbcTemplate.update(sqlUpd.toString(), 
                        new Object[]{pcoUserMod, 
                            documentoExtRecepBean.getDeAsu(), 
                            documentoExtRecepBean.getDeObservacion(), 
                            documentoExtRecepBean.getDeDocSig(),
                            documentoExtRecepBean.getCoTipDocAdm(), 
                            documentoExtRecepBean.getNuFolios(), 
                            documentoExtRecepBean.getNuDiaAte(),
                            documentoExtRecepBean.getCoOriDoc(),            
                            documentoExtRecepBean.getNroDniTramitante(),
                            documentoExtRecepBean.getnSobre(),
                            documentoExtRecepBean.getAnioSobre(),            
                            documentoExtRecepBean.getCoTraDest(),
                            nuAnn, 
                            nuEmi});
            } else if (documentoExtRecepBean == null && remitenteDocExtRecepBean != null) {
                this.jdbcTemplate.update(sqlUpd.toString(), 
                        new Object[]{pcoUserMod,                             
                            remitenteDocExtRecepBean.getTiEmi(),           
                            remitenteDocExtRecepBean.getNuRuc(),
                            remitenteDocExtRecepBean.getNuDni(),  
                            remitenteDocExtRecepBean.getCoOtros(),            
                            remitenteDocExtRecepBean.getDeDireccion(),    
                            remitenteDocExtRecepBean.getDeCorreo(),   
                            remitenteDocExtRecepBean.getTelefono(),    
                            remitenteDocExtRecepBean.getEmiResp(),
                            remitenteDocExtRecepBean.getNuDniRes(),
                            remitenteDocExtRecepBean.getDeCargo(),    
                            remitenteDocExtRecepBean.getCoComision(),    
                            remitenteDocExtRecepBean.getCoTipoCongresista(),    
                            remitenteDocExtRecepBean.getCoTipoInv(),    
                            remitenteDocExtRecepBean.getCoOtrosRes(),    
                            remitenteDocExtRecepBean.getIdDepartamento(),    
                            remitenteDocExtRecepBean.getIdProvincia(),    
                            remitenteDocExtRecepBean.getIdDistrito(),
                            nuAnn, 
                            nuEmi});
            }else if (documentoExtRecepBean != null && remitenteDocExtRecepBean != null){
                    this.jdbcTemplate.update(sqlUpd.toString(), 
                        new Object[]{pcoUserMod, 
                            documentoExtRecepBean.getDeAsu(), 
                            documentoExtRecepBean.getDeObservacion(), 
                            documentoExtRecepBean.getDeDocSig(),
                            documentoExtRecepBean.getCoTipDocAdm(), 
                            documentoExtRecepBean.getNuFolios(), 
                            documentoExtRecepBean.getNuDiaAte(),
                            documentoExtRecepBean.getCoOriDoc(),            
                            documentoExtRecepBean.getNroDniTramitante(),
                            documentoExtRecepBean.getnSobre(),
                            documentoExtRecepBean.getAnioSobre(),            
                            documentoExtRecepBean.getCoTraDest(),
                            remitenteDocExtRecepBean.getTiEmi(),           
                            remitenteDocExtRecepBean.getNuRuc(),
                            remitenteDocExtRecepBean.getNuDni(),  
                            remitenteDocExtRecepBean.getCoOtros(),            
                            remitenteDocExtRecepBean.getDeDireccion(),    
                            remitenteDocExtRecepBean.getDeCorreo(),   
                            remitenteDocExtRecepBean.getTelefono(),    
                            remitenteDocExtRecepBean.getEmiResp(),
                            remitenteDocExtRecepBean.getNuDniRes(),
                            remitenteDocExtRecepBean.getDeCargo(),    
                            remitenteDocExtRecepBean.getCoComision(),    
                            remitenteDocExtRecepBean.getCoTipoCongresista(),    
                            remitenteDocExtRecepBean.getCoTipoInv(),    
                            remitenteDocExtRecepBean.getCoOtrosRes(),    
                            remitenteDocExtRecepBean.getIdDepartamento(),    
                            remitenteDocExtRecepBean.getIdProvincia(),    
                            remitenteDocExtRecepBean.getIdDistrito(),
                            nuAnn, 
                            nuEmi});
            }else{
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, nuAnn, nuEmi});
            }
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Documento Duplicado";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }    

    @Override
    public String insDocumentoExtBean(DocumentoExtRecepBean documentoExtRecepBean, ExpedienteDocExtRecepBean expedienteBean, RemitenteDocExtRecepBean remitenteDocExtRecepBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("select lpad(cast(nextval('IDOSGD.SEC_REMITOS_NU_EMI') as text),10,'0')");

        sqlUpd.append("INSERT INTO IDOSGD.tdtv_remitos(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + "NU_COR_EMI,\n"
                + "CO_LOC_EMI,\n"
                + "CO_DEP_EMI,\n"
                + "CO_EMP_EMI,\n"
                + "CO_EMP_RES, \n"
                + "TI_EMI,\n"
                + "NU_DNI_EMI,\n"
                + "NU_RUC_EMI,\n"
                + "CO_OTR_ORI_EMI,\n"
                + "FE_EMI,\n"
                + "CO_GRU,\n"
                + "DE_ASU, \n"
                + "ES_DOC_EMI, \n"
                + "NU_DIA_ATE, \n"
                + "CO_TIP_DOC_ADM, \n"
                + "NU_COR_DOC, \n"
                + "DE_DOC_SIG, \n"
                + "NU_ANN_EXP, \n"
                + "NU_SEC_EXP, \n"
                + "NU_DET_EXP, \n"
                + "NU_FOLIOS, \n"
                + "ES_ELI,\n"
                + "CO_USE_CRE,\n"
                + "FE_USE_CRE,\n"
                + "CO_USE_MOD,\n"
                + "CO_DEP,\n"
                + "FE_USE_MOD,\n"
                + "CDIR_REMITE,\n"
                + "AUT_CORREOE,\n"
                + "CEXP_CORREOE,\n"
                + "CTELEFONO,\n"
                + "CCOD_DPTO,\n"
                + "CCOD_PROV,\n"
                + "CCOD_DIST,\n"
                + "REMI_TI_EMI,\n"
                + "REMI_NU_DNI_EMI,\n"
                + "REMI_CO_OTR_ORI_EMI,\n"
                + "REMI_CARGO,\n"
                + "CCOD_ORIGING,\n"
                + "CNUM_DNIMSG,\n"
                + "COBS_DOCUMENTO,\n"
                + "CDOC_DESTRAM,\n"
                + "CONG_CO_OTR_ORI,\n"
                + "IND_TIPOCONG,\n"
                + "IND_TIPOCONGINV,\n"
                + "IND_REITCONGINV,\n"
                + "NRO_SOBREAUT,\n"
                + "AN_SOBREAUT,\n"
                + "CIND_SENSIBLE,\n"
                + "NU_COPIA,\n"
                + "NU_ANEXO)\n" 
                + "VALUES(?,?,cast(? as bigint) ,?,?,?,?,?,?,?,?,(select to_timestamp(?,'dd/mm/yyyy hh24:mi:ss')),'3',?,'5',cast(? as smallint),?,cast(? as int),?,?,?,'1',cast(? as int),'0',?,CURRENT_TIMESTAMP,?,?,CURRENT_TIMESTAMP,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");

        try {
            String snuEmi = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);
            documentoExtRecepBean.setNuEmi(snuEmi);

           this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoExtRecepBean.getNuAnn(), snuEmi, expedienteBean.getNuCorrExp(),
                remitenteDocExtRecepBean.getCoLocEmi(), remitenteDocExtRecepBean.getCoDepEmi(), remitenteDocExtRecepBean.getCoEmpEmi(), remitenteDocExtRecepBean.getCoEmpRes(),
                remitenteDocExtRecepBean.getTiEmi(),remitenteDocExtRecepBean.getNuDni(),remitenteDocExtRecepBean.getNuRuc(),remitenteDocExtRecepBean.getCoOtros(),expedienteBean.getFeExp(),
                documentoExtRecepBean.getDeAsu(),documentoExtRecepBean.getNuDiaAte(), documentoExtRecepBean.getCoTipDocAdm(),documentoExtRecepBean.getNuCorDoc(), documentoExtRecepBean.getDeDocSig(),
                expedienteBean.getNuAnnExp(), expedienteBean.getNuSecExp(),documentoExtRecepBean.getNuFolios(), documentoExtRecepBean.getCoUseMod(), documentoExtRecepBean.getCoUseMod(), remitenteDocExtRecepBean.getCoDep(),
                remitenteDocExtRecepBean.getDeDireccion(), remitenteDocExtRecepBean.getNotificado(), remitenteDocExtRecepBean.getDeCorreo(), remitenteDocExtRecepBean.getTelefono(),
                remitenteDocExtRecepBean.getIdDepartamento(), remitenteDocExtRecepBean.getIdProvincia(), remitenteDocExtRecepBean.getIdDistrito(), remitenteDocExtRecepBean.getEmiResp(), remitenteDocExtRecepBean.getNuDniRes(),
                remitenteDocExtRecepBean.getCoOtrosRes(), remitenteDocExtRecepBean.getDeCargo(), documentoExtRecepBean.getCoOriDoc(), documentoExtRecepBean.getNroDniTramitante(),
                documentoExtRecepBean.getDeObservacion(), documentoExtRecepBean.getCoTraDest(), remitenteDocExtRecepBean.getCoComision(), remitenteDocExtRecepBean.getCoTipoCongresista(), remitenteDocExtRecepBean.getCoTipoInv(),
                remitenteDocExtRecepBean.getReiterativo(), documentoExtRecepBean.getnSobre(), documentoExtRecepBean.getAnioSobre(), documentoExtRecepBean.getSensible(), documentoExtRecepBean.getNuCopia(),documentoExtRecepBean.getNuAnexo()
                });
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String insDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) { 
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.TDTV_DESTINOS(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + "NU_DES,\n"
                + "CO_LOC_DES,\n"
                + "CO_DEP_DES,\n"
                + "TI_DES,\n"
                + "CO_EMP_DES,\n"
                + "CO_PRI,\n"
                + "DE_PRO,\n"
                + "CO_MOT, \n" 
                + "ES_ELI,\n"
                + "FE_USE_CRE,\n"
                + "FE_USE_MOD,\n"
                + "CO_USE_MOD,\n"
                + "CO_USE_CRE,\n"
                + "ES_DOC_REC,\n"
                + "ES_ENV_POR_TRA)\n"
                + "VALUES(?,?,?,?,?,?,?,?,?,?,'0',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?,?,'0','0')");

        try {
            
            int vnuDes = this.jdbcTemplate.queryForObject("select COALESCE(MAX(a.nu_des) + 1,1) FROM IDOSGD.tdtv_destinos a where \n" +
                                                            "A.NU_ANN=? and\n" +
                                                            "A.NU_EMI=?", Integer.class, new Object[]{nuAnn,nuEmi});
            destinatarioDocumentoEmiBean.setNuDes(String.valueOf(vnuDes));
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn, nuEmi, vnuDes, destinatarioDocumentoEmiBean.getCoLocal(), destinatarioDocumentoEmiBean.getCoDependencia(), destinatarioDocumentoEmiBean.getCoTipoDestino(),
                destinatarioDocumentoEmiBean.getCoEmpleado(), destinatarioDocumentoEmiBean.getCoPrioridad(), destinatarioDocumentoEmiBean.getDeIndicaciones(), destinatarioDocumentoEmiBean.getCoTramite(),
                /*destinatarioDocumentoEmiBean.getCoOtroOrigen(), destinatarioDocumentoEmiBean.getNuDni(), destinatarioDocumentoEmiBean.getNuRuc(),*/
                destinatarioDocumentoEmiBean.getCoUseCre(), destinatarioDocumentoEmiBean.getCoUseCre()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

        }
        return vReturn;
    }
    
    @Override
    public String insReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, ReferenciaDocExtRecepBean ref){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.tdtr_referencia(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + "CO_REF,\n"
                + "NU_ANN_REF,\n"
                + "NU_EMI_REF,\n"
                + "NU_DES_REF,\n"
                + "CO_USE_CRE,\n"
                + "FE_USE_CRE)\n"
                + "VALUES(?,?,?,?,?, cast(? as bigint),?,now ())");
        
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("select lpad(cast(nextval('IDOSGD.SEC_REFERENCIA_CO_REF') as text), 10, '0')");        

        try {
            String scoRef = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);
            ref.setCoRef(scoRef);            
            
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pnuAnn, pnuEmi, scoRef, ref.getNuAnn(), ref.getNuEmi(), (ref.getNuDes().isEmpty())? null : ref.getNuDes(),
                ref.getCoUseCre()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }    
    
    @Override
    public String getNroCorrelativoDocumento(String pnuAnn, String pcoDepEmi, String ptiEmi) { 
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("	  select COALESCE(max(nu_cor_doc),0)+1 \n" +
                    "	  from IDOSGD.tdtv_remitos\n" +
                    "	  where nu_ann=?\n" +
                    "	  and co_dep_emi=?\n" +
                    "	  AND TI_EMI=?");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{pnuAnn,pcoDepEmi,ptiEmi});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "NO_OK";
        }
        return vReturn;
    }
    
    @Override
    public DocumentoExtRecepBean getDocumentoExtRecepNew(String coDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("select substr(IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(CO_DEPENDENCIA),1,6) DE_DOC_SIG_G,"
                + " IDOSGD.PK_SGD_DESCRIPCION_ESTADOS_MP('5','TDTV_REMITOS') DE_ES_DOC_EMI_MP,"
                + " CO_EMPLEADO co_emp_emi"
                + " from IDOSGD.rhtm_dependencia"
                + " where CO_DEPENDENCIA = ?");

        DocumentoExtRecepBean documentoExtRecepBean = null;
        try {
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{coDependencia});
        } catch (EmptyResultDataAccessException e) {
            documentoExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }    
    
    @Override
    public List<MotivoBean> getLstMotivoxTipoDocumento(){
        StringBuffer sql = new StringBuffer();
        List<MotivoBean> list = new ArrayList<MotivoBean>();

        sql.append("SELECT DE_MOT,CO_MOT FROM IDOSGD.TDTR_MOTIVO order by 1");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(MotivoBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public List<ReferenciaDocExtRecepBean> getLstDocExtReferencia(String pnuAnn,String pcoTiDoc,String pcoDepEmi,String pnuExpediente){
        StringBuffer sql = new StringBuffer();
        List<ReferenciaDocExtRecepBean> list = new ArrayList<ReferenciaDocExtRecepBean>();
 
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.CO_TIP_DOC_ADM CO_TIP_DOC,B.NU_EXPEDIENTE,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EXP,\n" +
                    "SUBSTR(a.de_asu,1,59) de_asu,\n"+
                    " a.de_doc_sig,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tip_doc_adm, \n"+
                    "(CASE a.ti_emi "
                   + " WHEN '01' THEN a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig \n"+
                     " WHEN '05' THEN a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig ELSE a.de_doc_sig "
                   + " END ) li_nu_doc,\n"+                    
                    "substr(IDOSGD.PK_SGD_DESCRIPCION_de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +                   
                    " (CASE WHEN a.CO_GRU='1' THEN 'INTERNO' WHEN a.CO_GRU='3' THEN 'EXTERNO' END ) de_procedencia\n" +
                    "FROM \n" +
                    "IDOSGD.TDTV_REMITOS A LEFT JOIN\n" +
                    "IDOSGD.TDTC_EXPEDIENTE B ON ( A.NU_ANN_EXP=B.NU_ANN_EXP AND\n" +
                    "                             A.NU_SEC_EXP=B.NU_SEC_EXP  )\n" +  //AND A.NU_COR_EMI::text=B.NU_CORR_EXP                  
                    "INNER  JOIN IDOSGD.TDTV_DESTINOS D  ON (A.NU_ANN=D.NU_ANN AND A.NU_EMI=D.NU_EMI)\n" +
                    "WHERE A.NU_ANN=? AND \n" +
                    "(A.CO_TIP_DOC_ADM=? or 1=1 ) AND\n"// 
                  //  "A.CO_DEP_EMI=? AND\n"
                );
        if(pnuExpediente.trim().length()>0){
            sql.append("(B.NU_EXPEDIENTE = '"+pnuExpediente+"' OR A.NU_DOC_EMI = '%"+pnuExpediente+"%'" );
            sql.append(" OR A.NU_DOC_EMI||'-'||A.NU_ANN||'-'||(SELECT de_par FROM IDOSGD.TDTR_PARAMETROS where CO_PAR='DE_INSTITUCION' )||'-'||A.DE_DOC_SIG like UPPER('%"+pnuExpediente+"%')) AND\n" );
        }
        sql.append("A.ES_DOC_EMI NOT IN ('9','7','5') AND \n" +
                    "A.TI_EMI NOT IN('05') AND\n" +
                    "A.CO_GRU in ('1','3') AND\n" +
                    "A.NU_DET_EXP =1 AND\n" +
                    "A.ES_ELI='0' AND\n" + 
                    "D.NU_DES='1'\n" +
                    "order by 1 desc");
         
        sql.append(" LIMIT 200");  
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaDocExtRecepBean.class),
                   new Object[]{pnuAnn,pcoTiDoc/*,pcoDepEmi*/});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public String getNroCorrelativoEmision(String pnuAnn, String pcoDepEmi) {
        String vReturn = "1";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT coalesce( MAX( a.NU_COR_EMI) , 0)+1\n" +
                        "FROM IDOSGD.TDTV_REMITOS a\n" +
                        "WHERE NU_ANN = ?\n" +//:B_02.NU_ANN
                        "AND CO_DEP_EMI=?\n" +//:B_02.CO_DEP_EMI
                        "AND co_gru ='3'");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{pnuAnn,pcoDepEmi});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "1";
        }
        return vReturn;
    }
    
    @Override
    public String updDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

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
                + ",FE_USE_MOD=now()\n"
                + ",CO_USE_MOD=?\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=? and\n"
                + "NU_DES= cast(? as bigint)");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{destinatarioDocumentoEmiBean.getCoLocal(), destinatarioDocumentoEmiBean.getCoDependencia(),
                destinatarioDocumentoEmiBean.getCoEmpleado(), destinatarioDocumentoEmiBean.getCoPrioridad(), destinatarioDocumentoEmiBean.getDeIndicaciones(), destinatarioDocumentoEmiBean.getCoTramite(),
                destinatarioDocumentoEmiBean.getCoOtroOrigen(), destinatarioDocumentoEmiBean.getNuDni(), destinatarioDocumentoEmiBean.getNuRuc(),
                destinatarioDocumentoEmiBean.getCoUseCre(), nuAnn, nuEmi, destinatarioDocumentoEmiBean.getNuDes()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String delDestinatarioDocumentoEmi(String nuAnn, String nuEmi, String pnuDes){
        String vReturn = "NO_OK";
        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("delete from IDOSGD.TDTV_DESTINOS\n"
                + "WHERE NU_ANN = ?\n"
                + "AND NU_EMI = ?\n"
                + "and NU_DES=?");
        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{nuAnn, nuEmi, Integer.parseInt(pnuDes)});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    } 
    
    @Override
    public String updReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, ReferenciaDocExtRecepBean ref) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtr_referencia \n"
                + "set NU_ANN_REF = ?, \n"
                + "NU_EMI_REF = ?, \n"
                + "NU_DES_REF = ?\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND CO_REF = ?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{ref.getNuAnn(), ref.getNuEmi(), ref.getNuDes(),
                pnuAnn, pnuEmi, ref.getCoRef()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;        
    }
    
    @Override
    public String delReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, String pnuAnnRef,String pnuEmiRef){
        String vReturn = "NO_OK";
        StringBuffer sqlIns = new StringBuffer();
        sqlIns.append("DELETE FROM IDOSGD.tdtr_referencia\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND NU_ANN_REF = ? AND NU_EMI_REF = ?");

        try {
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{pnuAnn, pnuEmi, pnuAnnRef, pnuEmiRef});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;        
    }    

    @Override
    public DocumentoExtRecepBean getDocumentoExtRec(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
         
        sql.append("SELECT A.NU_ANN,A.NU_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(A.CO_DEP_EMI) DE_DOC_SIG_G,\n" +
                    "A.CO_LOC_EMI,A.CO_EMP_EMI,A.CO_DEP_EMI,A.NU_COR_DOC,B.NU_ANN_EXP,B.NU_SEC_EXP,TO_CHAR(B.FE_EXP,'DD/MM/YYYY HH24:MI') FE_EXP,\n" +
                    "TO_CHAR(B.FE_VENCE,'DD/MM/YYYY') FE_VENCE,B.NU_CORR_EXP,B.NU_EXPEDIENTE,B.CO_PROCESO,\n" +
                    "A.ES_DOC_EMI CO_ES_DOC_EMI_MP,IDOSGD.PK_SGD_DESCRIPCION_ESTADOS_MP(A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI_MP,\n" +
                    "A.TI_EMI,A.NU_DNI_EMI NU_DNI,IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(A.NU_DNI_EMI) DE_NU_DNI,\n" +
                    "A.NU_RUC_EMI NU_RUC,IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(A.NU_RUC_EMI) DE_NU_RUC,A.CO_OTR_ORI_EMI CO_OTROS,\n" +
                    "A.CO_EMP_RES,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_RES,A.CO_TIP_DOC_ADM,A.DE_DOC_SIG,\n" +
                    "A.NU_FOLIOS,A.DE_ASU,A.NU_DIA_ATE, A.CDIR_REMITE DE_DIRECCION, COALESCE(A.AUT_CORREOE,'0') NOTIFICADO, A.CEXP_CORREOE DE_CORREO, A.CTELEFONO TELEFONO, A.CCOD_PROV ID_PROVINCIA, A.CCOD_DIST ID_DISTRITO,  A.CCOD_DPTO ID_DEPARTAMENTO ,\n" +
                    "A.REMI_TI_EMI EMI_RESP, A.REMI_NU_DNI_EMI NU_DNI_RES, IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(A.REMI_NU_DNI_EMI) DE_NU_DNI_RES,A.REMI_CO_OTR_ORI_EMI CO_OTROS_RES,IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (A.REMI_CO_OTR_ORI_EMI) DE_NOM_OTROS_RES, A.REMI_CARGO DE_CARGO, A.CCOD_ORIGING CO_ORI_DOC, A.CNUM_DNIMSG NRO_DNI_TRAMITANTE, A.COBS_DOCUMENTO DE_OBSERVACION, A.CDOC_DESTRAM CO_TRA_DEST,\n" +
                    "A.CONG_CO_OTR_ORI CO_COMISION, A.IND_TIPOCONG CO_TIPO_CONGRESISTA, A.IND_TIPOCONGINV CO_TIPO_INV, COALESCE(A.IND_REITCONGINV,'0') REITERATIVO, A.NRO_SOBREAUT NSOBRE, A.AN_SOBREAUT ANIO_SOBRE, B.CCOD_TIPO_EXP CO_TIPO_EXP, COALESCE(A.CIND_SENSIBLE,'0') SENSIBLE, COALESCE(A.NU_COPIA,'1') nuCopia, COALESCE(A.NU_ANEXO,' ') nuAnexo,\n" +
                    " IDOSGD.PK_SGD_MENSAJERIA_CAL_DIF_FECHAS_HABILES(B.FE_EXP::date,current_date) as nuDiasHabiles\n"+
                    "FROM IDOSGD.TDTV_REMITOS A left join IDOSGD.TDTC_EXPEDIENTE B\n" +
                    "ON A.NU_ANN_EXP = B.NU_ANN_EXP AND A.NU_SEC_EXP = B.NU_SEC_EXP\n" +
                    "WHERE\n" +
                    "A.NU_ANN=?\n" +
                    "AND A.NU_EMI=?");
        
        DocumentoExtRecepBean documentoExtRecepBean = null;
        try {
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }
    
    @Override
    public Map getDesFieldOtro(String pcoOtros){
        Map mapResult=null;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  C.DE_APE_PAT_OTR || ' ' || C.DE_APE_MAT_OTR || ', ' || C.DE_NOM_OTR || ' - ' || C.DE_RAZ_SOC_OTR DE_NOM_OTROS, ");
        sql.append("        COALESCE(B.CELE_DESELE, '   ') DE_DOC_OTROS, ");
        sql.append("        C.NU_DOC_OTR_ORI NU_DOC_OTROS ");
        sql.append("FROM IDOSGD.TDTR_OTRO_ORIGEN C ");
        sql.append("	LEFT OUTER JOIN ");
        sql.append("   (SELECT CELE_CODELE, CELE_DESELE ");
        sql.append("    FROM IDOSGD.SI_ELEMENTO ");
        sql.append("   WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B ");
        sql.append("   ON C.CO_TIP_OTR_ORI = B.CELE_CODELE ");
        sql.append("WHERE C.CO_OTR_ORI = ? ");
        
        try {
               mapResult = this.jdbcTemplate.queryForMap(sql.toString(), new Object[]{pcoOtros});    
        } catch (Exception e) {
            e.printStackTrace();
        }
        return mapResult;
    }
    
    @Override
    public List<DestinatarioDocumentoEmiBean> getLstDestinoEmiDoc(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = null;
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,"+
                    "substr(IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(a.co_loc_des), 1, 100) de_local,\n" +
                    "a.co_dep_des co_dependencia,substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_des), 1, 100) de_dependencia,\n" +
                    "a.co_emp_des co_empleado,substr(IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des), 1, 100) de_empleado,\n" +
                    "a.co_mot co_tramite,substr(IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(a.co_mot), 1, 100) de_tramite,\n" +
                    "a.co_pri co_prioridad,\n" +
                    "a.de_pro de_indicaciones,\n" +
                    "a.ti_des co_tipo_destino,\n" +
                    "'BD' accionBD\n" +
                    "FROM IDOSGD.tdtv_destinos a\n" +
                    "where nu_ann = ? and nu_emi = ?\n" +
                    "AND ES_ELI='0' AND NU_EMI_REF is null\n" +
                    "order by 3");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
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
    public List<ReferenciaDocExtRecepBean> getLstReferenciaDoc(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        List<ReferenciaDocExtRecepBean> list;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.CO_TIP_DOC_ADM CO_TIP_DOC,\n" +
                    "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE(A.NU_ANN_EXP,A.NU_SEC_EXP),1,20) NU_EXPEDIENTE,\n" +
                    "TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EXP,'BD' ACCION_BD,B.CO_REF \n" +
                    "FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTR_REFERENCIA B\n" +
                    "WHERE \n" +
                    "A.NU_ANN = B.NU_ANN_REF AND\n" +
                    "A.NU_EMI = B.NU_EMI_REF AND\n" +
                    "B.NU_ANN = ? AND\n" +
                    "B.NU_EMI = ? AND\n" +
                    "A.ES_DOC_EMI NOT IN ('9','7','5')AND \n" +
                    "A.NU_DET_EXP =1 AND\n" +
                    "A.ES_ELI='0'");

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
    public DocumentoExtRecepBean getDocumentoExtRecBasic(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,\n" +
                    "A.CO_LOC_EMI,A.CO_EMP_EMI,A.CO_DEP_EMI,A.NU_COR_DOC,\n" +
                    "A.ES_DOC_EMI CO_ES_DOC_EMI_MP,\n" +
                    "A.TI_EMI,\n" +
                    "A.CO_EMP_RES,A.CO_TIP_DOC_ADM,A.DE_DOC_SIG,\n" +
                    "A.NU_FOLIOS,A.NU_ANN_EXP,A.NU_SEC_EXP\n" +
                    "FROM IDOSGD.TDTV_REMITOS A\n" +
                    "WHERE\n" +
                    "A.NU_ANN=?\n" +
                    "AND A.NU_EMI=?\n" +
                    "AND CO_GRU='3' \n" +
                    "AND ES_ELI='0'\n" +
                    "AND NU_DET_EXP='1'\n" +
                    "AND ES_DOC_EMI <> '9'");

        DocumentoExtRecepBean documentoExtRecepBean = null;
        try {
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }
    
    @Override
    public String updEstadoDocumentoExt(DocumentoExtRecepBean documentoExtRecepBean){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=? \n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP \n"
                + ",FE_EMI=CURRENT_TIMESTAMP \n"
                + ",ES_DOC_EMI=? \n"
                + "WHERE NU_ANN=? \n"
                + "AND NU_EMI=? ");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoExtRecepBean.getCoUseMod(),
                documentoExtRecepBean.getCoEsDocEmiMp(), documentoExtRecepBean.getNuAnn(), documentoExtRecepBean.getNuEmi()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public String anularDocumentoExtRecep(DocumentoExtRecepBean documentoExtRecepBean){
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT COALESCE(MAX(nu_cor_doc), 1) + 1\n"
                + "FROM IDOSGD.tdtv_remitos\n"
                + "WHERE nu_ann         = ?\n"
                + "AND ti_emi         = ?\n"
                + "AND co_dep_emi     = ?\n"
                + "AND co_tip_doc_adm = ?");

        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set es_doc_emi = ?,\n"
                + "nu_cor_doc = CAST(? as integer),fe_use_mod= now(),co_use_mod=? \n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?\n"
                + "AND es_eli = '0'");

        try {
            String snuCorDoc = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoExtRecepBean.getNuAnn(), documentoExtRecepBean.getTiEmi(),
                documentoExtRecepBean.getCoDepEmi(), documentoExtRecepBean.getCoTipDocAdm()});

            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoExtRecepBean.getCoEsDocEmiMp(), snuCorDoc, documentoExtRecepBean.getCoUseMod(),
                documentoExtRecepBean.getNuAnn(), documentoExtRecepBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public ReferenciaDocExtRecepBean getRefAtenderDocExtRec(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,SUBSTR(B.NU_EXPEDIENTE,1,20) NU_EXPEDIENTE,\n" +
                    "TO_CHAR(B.FE_EXP,'DD/MM/YY') FE_EXP,A.CO_TIP_DOC_ADM CO_TIP_DOC,\n" +
                    "'INS' ACCION_BD\n" +
                    "FROM IDOSGD.TDTV_REMITOS A,IDOSGD.TDTC_EXPEDIENTE B\n" +
                    "WHERE A.NU_ANN = ? AND A.NU_EMI = ? AND\n" +
                    "B.NU_ANN_EXP=A.NU_ANN_EXP AND B.NU_SEC_EXP=A.NU_SEC_EXP AND\n" +
                    "A.ES_DOC_EMI NOT IN ('9','7','5')AND \n" + 
                    "A.ES_ELI='0' AND B.ES_ESTADO='0'");
        ReferenciaDocExtRecepBean referenciaDocExtRecepBean = null;
        try {
            referenciaDocExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaDocExtRecepBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            referenciaDocExtRecepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return referenciaDocExtRecepBean;        
    }

    @Override
    public String getNumeroExpediente(ExpedienteDocExtRecepBean expedienteBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry1 = new StringBuffer();
        sqlQry1.append("select LPAD(TRIM(CAST((CAST(COALESCE(max(NU_CORR_EXP),'0')AS int)+1) AS text)),7,'0') nuCorrExp, \n"
                + " substr(IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(?),1,6) deSiglaCorta\n"
                + "from IDOSGD.tdtc_expediente\n"
                + "where nu_ann_exp = ?\n"
                + "and co_dep_exp = ?\n"
                + "and co_gru     = '3'");
        
        
        
        try{
            Map mapResult = this.jdbcTemplate.queryForMap(sqlQry1.toString(), new Object[]{expedienteBean.getCoDepEmi(), expedienteBean.getNuAnnExp(), expedienteBean.getCoDepEmi()});
            String snuCorrExp = String.valueOf(mapResult.get("nuCorrExp"));
            String sdeSiglaCorta = String.valueOf(mapResult.get("deSiglaCorta"));
            expedienteBean.setNuCorrExp(snuCorrExp);
            //expedienteBean.setNuExpediente(Utilidades.AjustaCampoIzquierda(sdeSiglaCorta, 6, "0") + expedienteBean.getNuAnnExp() + snuCorrExp);
            expedienteBean.setNuExpediente(expedienteBean.getNuAnnExp()+"-"+snuCorrExp);
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
     
    @Override
    public String updFechaExpedienteMP(String coUser, String nuAnnExp, String nuSecExp){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("UPDATE IDOSGD.TDTC_EXPEDIENTE \n"
                + "SET US_MODI_AUDI=? \n"
                + ",FE_MODI_AUDI=NOW() \n"
                + ",FE_EXP=NOW() \n"
                + "WHERE NU_ANN_EXP=? \n"
                + "AND NU_SEC_EXP=? ");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUser,
                nuAnnExp, nuSecExp});
            vReturn = "OK";
        }catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public String getPermisoChangeEstadoMP(String coEmp, String coDep){
        String vReturn="NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT IN_CAMBIO_EST FROM IDOSGD.TDTR_PERMISO_MP\n" +
                        "WHERE CO_EMP=? AND CO_DEP=?\n" +
                        "AND ES_ELI='0'");       
        try{
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{coEmp, coDep});
        } catch (EmptyResultDataAccessException e) {
            vReturn = "0";
        }  catch (Exception e) {
            e.printStackTrace();
        }           
        
        return vReturn;
    }
    
    @Override
    public ProcesoExpBean getProcesoExpediente(String pcoProceso){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NU_PLAZO NU_DIAS_PLAZO, DE_ASUNTO FROM IDOSGD.TDTR_PROCESOS_EXP\n" +
                    "WHERE CO_PROCESO=? AND ES_ESTADO = '1'");
        ProcesoExpBean proceso = null;
        try {
            proceso = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(ProcesoExpBean.class),
                    new Object[]{pcoProceso});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proceso;        
    }
    
    @Override
    public DestinatarioDocumentoEmiBean getEmpleadoDestinoDocExtMp(String pcoDependencia) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT A.CO_LOC CO_LOCAL,IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(A.CO_LOC) DE_LOCAL,B.CO_EMPLEADO CO_EMPLEADO,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(B.CO_EMPLEADO) DE_EMPLEADO,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(B.CO_DEPENDENCIA) DE_DEPENDENCIA,B.CO_DEPENDENCIA\n" +
                    "FROM IDOSGD.SITM_LOCAL_DEPENDENCIA A, IDOSGD.RHTM_DEPENDENCIA B\n" + 
                    "WHERE B.CO_DEPENDENCIA = A.CO_DEP AND B.IN_MESA_PARTES='1'");

        DestinatarioDocumentoEmiBean destino = null;
        try {
//            destino = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
//                    new Object[]{pcoDependencia, pcoDependencia});
            destino = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class));            
        } catch (EmptyResultDataAccessException e) {
            destino = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return destino;
    }
    
    @Override
    public List<RemitenteBean> getAllDependencias(){
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT DE_DEPENDENCIA descrip,\n" +
                    "       CO_DEPENDENCIA codDep,\n" +
                    "       DE_CORTA_DEPEN\n" +
                    "  FROM IDOSGD.RHTM_DEPENDENCIA\n" +
                    " WHERE co_nivel <> '6'\n" +
                    "   AND IN_BAJA = '0'\n" +
                    "   order by 1");

        
        List<RemitenteBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RemitenteBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }

    @Override
    public List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp, String codProceso) {
         StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT \n" +
                    "(SELECT DE_DESCRIPCION FROM IDOSGD.TDTR_REQUISITOS RQ WHERE RQ.COD_REQ=RQEXP.COD_REQUISITO AND RQ.ES_ESTADO='1') DESCRIPCION,\n" +
                    "RQEXP.IN_REQUISITO_PRESENTE DOC_PRESENTE,\n" +
                    "RQEXP.COD_REQUISITO COD_REQUISITO,\n" +
                    "RQEXP.NU_CORRELATIVO NU_CORRELATIVO,\n" +
                    "RQEXP.IN_OBLIGATORIO as inObligatorio \n" +
                    "FROM IDOSGD.TDTX_REQUISITO_EXPEDIENTE RQEXP\n" +
                    "WHERE RQEXP.NU_ANN_EXP=?\n" +
                    "AND RQEXP.NU_SEC_EXP=?\n" +
                   "AND RQEXP.COD_PROCESO=?\n" +
                    "AND RQEXP.ES_ESTADO='1'");

        
        List<RequisitoBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RequisitoBean.class),
                    new Object[]{nuAnnExp, nuSecExp, codProceso});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;  
    }

    @Override
    public String updReqExpedienteDocExtRec(RequisitoBean req, String nuAnnExp, String nuSecExp, String codProceso, String coUsuario) {
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();

        sqlUpd.append("UPDATE IDOSGD.TDTX_REQUISITO_EXPEDIENTE ");
        sqlUpd.append("SET IN_REQUISITO_OK=(CASE IN_OBLIGATORIO WHEN '1' THEN (CASE WHEN ? THEN '1' ELSE '0' END) ELSE '1' END), ");
        sqlUpd.append("IN_REQUISITO_PRESENTE=(CASE WHEN ? THEN '1' ELSE '0' END), ");
        sqlUpd.append("US_MODI_AUDI=?, ");
        sqlUpd.append("FE_MODI_AUDI=CURRENT_TIMESTAMP ");
        sqlUpd.append("WHERE NU_ANN_EXP=? ");
        sqlUpd.append("AND NU_SEC_EXP=? ");
        sqlUpd.append("AND COD_REQUISITO=? ");
        sqlUpd.append("AND COD_PROCESO=? ");
        sqlUpd.append("AND NU_CORRELATIVO=CAST(? AS NUMERIC) ");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{req.isDocPresente(), req.isDocPresente(), coUsuario,
                                        nuAnnExp, nuSecExp, req.getCodRequisito(), codProceso, req.getNuCorrelativo()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public String getRequisitoPendiente(String nuAnnExp, String nuSecExp) {
           String vReturn = "NO_OK";
        StringBuilder sqlQry = new StringBuilder();
        sqlQry.append("SELECT COUNT(1)\n" +
                    "FROM IDOSGD.TDTX_REQUISITO_EXPEDIENTE\n" +
                    "WHERE NU_ANN_EXP=? AND NU_SEC_EXP=?\n" +
                    "AND IN_REQUISITO_OK='0'\n" +
                    "AND ES_ESTADO='1'");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{nuAnnExp, nuSecExp});
            if(vReturn!=null&&vReturn.equals("0")){
                vReturn="OK";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String getPkExpDocExtOrigen(String pnuAnn, String pnuEmi) {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT DISTINCT REX.NU_ANN_EXP || REX.NU_SEC_EXP ");
        sql.append("FROM (SELECT NU_ANN, NU_EMI, NU_DES FROM IDOSGD.PK_SGD_DESCRIPCION_ARBOL_SEG(?, ?)) DO, ");
        sql.append("	 IDOSGD.TDTV_REMITOS RE, ");
        sql.append("	 IDOSGD.TDTX_REQUISITO_EXPEDIENTE REX ");
        sql.append("WHERE RE.NU_ANN=DO.NU_ANN ");
        sql.append("AND RE.NU_EMI=DO.NU_EMI ");
        sql.append("AND RE.ES_ELI='0' ");
        sql.append("AND RE.CO_GRU='3' ");
        sql.append("AND RE.TI_EMI<>'01' ");
        sql.append("AND REX.NU_ANN_EXP=RE.NU_ANN_EXP ");
        sql.append("AND REX.NU_SEC_EXP=RE.NU_SEC_EXP ");

        String pkExp = null;
        try {
            pkExp = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pnuEmi});            
        } catch (EmptyResultDataAccessException e) {
            pkExp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkExp;
    }

    @Override
    public List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp) {
        StringBuilder sql = new StringBuilder();
        
        sql.append("SELECT      (SELECT DE_DESCRIPCION ");
        sql.append("		FROM IDOSGD.TDTR_REQUISITOS RQ ");
        sql.append("		WHERE RQ.COD_REQ=RQEXP.COD_REQUISITO AND RQ.ES_ESTADO='1') DESCRIPCION, ");
        sql.append("		RQEXP.IN_REQUISITO_PRESENTE DOC_PRESENTE, ");
        sql.append("		RQEXP.COD_REQUISITO COD_REQUISITO, ");
        sql.append("		RQEXP.NU_CORRELATIVO NU_CORRELATIVO ");
        sql.append("FROM IDOSGD.TDTX_REQUISITO_EXPEDIENTE RQEXP ");
        sql.append("WHERE RQEXP.NU_ANN_EXP=? ");
        sql.append("AND RQEXP.NU_SEC_EXP=? ");
        sql.append("AND RQEXP.ES_ESTADO='1' ");
        
        List<RequisitoBean> list;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RequisitoBean.class),
                    new Object[]{nuAnnExp, nuSecExp});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;  
    }

    @Override
    public List<DocumentoBean> getListaReporteBusquedaVoucher(DocumentoBean documento) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
             
        prutaReporte.append("SELECT  (EX.NU_ANN_EXP || '-' || EX.NU_CORR_EXP) AS nuExpediente, ");
        prutaReporte.append("		(EX.NU_ANN_EXP || '-' || EX.NU_CORR_EXP) as NU_CORR_EXP, ");
        prutaReporte.append("		(SELECT DEP.TITULO_DEP FROM IDOSGD.RHTM_DEPENDENCIA DEP WHERE DEP.CO_DEPENDENCIA=RE.CO_DEP_EMI) DE_DEPENDENCIA, ");
        prutaReporte.append("		(SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_INSTITUCION') DE_INSTITUCION, ");
        prutaReporte.append("		COALESCE((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='ANEXO_MESA_PARTES'), ' ') ANEXO_MESA_PARTES, ");
        prutaReporte.append("		COALESCE((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='FONO_INSTITUCION'), ' ') FONO_INSTITUCION, ");
        prutaReporte.append("		(SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='PAG_WEB') PAG_WEB, ");
        prutaReporte.append("		COALESCE((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_MESA_PARTES'), ' ') DE_MESA_PARTES, ");
        prutaReporte.append("		COALESCE((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_SLOGAN'), ' ') DE_SLOGAN, ");
        prutaReporte.append("		COALESCE((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='TIPO_VOUCHER_MP'), ' ') tiVoucherMP, ");
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP(RE.NU_ANN, RE.NU_EMI) DE_ORI_EMI,RE.NU_FOLIOS as nuFolios,");
        prutaReporte.append("		(SELECT STRING_AGG(IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(CO_DEP_DES), ',') "
                //+ "WITHIN GROUP (ORDER BY  CO_DEP_DES) "
                + "FROM IDOSGD.TDTV_DESTINOS DES WHERE DES.NU_ANN=RE.NU_ANN AND DES.NU_EMI=RE.NU_EMI AND DES.CO_MOT<>'1') deDepDes, ");
        prutaReporte.append("		EX.FE_EXP, (SELECT COD_USER FROM IDOSGD.SEG_USUARIOS1 WHERE CEMP_CODEMP = RE.CO_EMP_RES) COD_USER, ");
        prutaReporte.append("		(SELECT CDES_USER FROM IDOSGD.SEG_USUARIOS1 WHERE CEMP_CODEMP = RE.CO_EMP_RES) deUsuario,"
                + "(SELECT STRING_AGG(IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(CO_DEP_DES), ',') "
                //+ "WITHIN GROUP (ORDER BY  CO_DEP_DES) "
                + "FROM IDOSGD.TDTV_DESTINOS DES WHERE DES.NU_ANN=RE.NU_ANN AND DES.NU_EMI=RE.NU_EMI AND DES.CO_MOT='1') as nuCopias,COALESCE(RE.NU_ANEXO,' ') as deAne, ");
        
        prutaReporte.append("		(SELECT STRING_AGG(exp.NU_EXPEDIENTE, '/ ')  FROM IDOSGD.tdtr_referencia ref inner join IDOSGD.tdtv_remitos rt on ref.NU_ANN_REF=rt.NU_ANN and ref.NU_EMI_REF=rt.nu_EMI"
                + " inner join IDOSGD.tdtc_expediente exp on exp.nu_sec_exp=rt.nu_sec_exp where ref.nu_emi=RE.nu_EMI and ref.nu_ann=RE.nu_ANN) nuEmiRef, ");
        prutaReporte.append("		EX.CCLAVE,(CASE WHEN RE.COBS_DOCUMENTO IS NULL THEN ' ' ELSE 'Observación:'||RE.COBS_DOCUMENTO END) as deObsDoc, ");
        prutaReporte.append("		(CASE WHEN RE.ES_DOC_EMI='8' THEN COALESCE((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='OBS_VOUCHER_MP'), ' ') ELSE ' ' END) as obsVoucherMP ");
        prutaReporte.append(" FROM IDOSGD.TDTV_REMITOS RE, ");
        prutaReporte.append("	 IDOSGD.TDTC_EXPEDIENTE EX ");   
        
        
        try {
            prutaReporte.append("WHERE RE.NU_ANN = :nu_ann ");
            objectParam.put("nu_ann", documento.getNuAnn());
            prutaReporte.append("AND RE.NU_EMI = :nu_emi ");
            objectParam.put("nu_emi", documento.getNuEmi());
            prutaReporte.append("AND RE.NU_ANN_EXP=EX.NU_ANN_EXP ");
            prutaReporte.append("AND RE.NU_SEC_EXP=EX.NU_SEC_EXP ");

            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }
    
    @Override
    public List<DocumentoExtRecepBean> getListaReporteBusqueda(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        boolean bBusqFiltro = false;
        boolean bBusqDep = false;
        
        prutaReporte.append("SELECT  A.NU_COR_EMI, ");
        prutaReporte.append("	     TO_CHAR(A.FE_EMI, 'DD/MM/YYYY') FE_EMI_CORTA, ");
        prutaReporte.append("	     (SELECT CDOC_DESDOC ");
        prutaReporte.append("	     FROM IDOSGD.SI_MAE_TIPO_DOC ");
        prutaReporte.append("	     WHERE CDOC_TIPDOC = A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,B.NU_DOC, ");
        prutaReporte.append("	     (select CELE_DESELE ");
        prutaReporte.append("	     from IDOSGD.SI_ELEMENTO ");
        prutaReporte.append("	     WHERE CTAB_CODTAB='TIP_DESTINO' ");
        prutaReporte.append("	     AND CELE_CODELE=A.TI_EMI) tiRemitente, ");
        prutaReporte.append("	     IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP(A.NU_ANN, A.NU_EMI) deRemitente, ");
        prutaReporte.append("	     CASE A.NU_CANDES ");
        prutaReporte.append("	     	WHEN 1 THEN IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(A.NU_ANN, A.NU_EMI) ");
        prutaReporte.append("	     	ELSE IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(A.NU_ANN, A.NU_EMI) ");
        prutaReporte.append("	     END deEmpPro, ");
        prutaReporte.append("	     A.DE_ASU, ");
        prutaReporte.append("	     (SELECT DE_EST ");
        prutaReporte.append("	     FROM IDOSGD.TDTR_ESTADOS ");
        prutaReporte.append("	     WHERE CO_EST || DE_TAB = A.ES_DOC_EMI || 'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        prutaReporte.append("	     A.NU_DIA_ATE, ");
        prutaReporte.append("	     B.NU_EXPEDIENTE, ");
        prutaReporte.append("	     TO_CHAR(C.FE_VENCE, 'DD/MM/YYYY') FE_EXP_VENCE_CORTA, ");
        prutaReporte.append("	     (SELECT DE_NOMBRE ");
        prutaReporte.append("	     FROM IDOSGD.TDTR_PROCESOS_EXP ");
        prutaReporte.append("	     WHERE CO_PROCESO = B.CO_PROCESO_EXP ");
        prutaReporte.append("	     AND ES_ESTADO='1') DE_PROCESO_EXP, ");
        prutaReporte.append("	     B.CO_PROCESO_EXP AS coProcesoExp, ");
        prutaReporte.append("	     A.NU_FOLIOS AS nuFolios, ");
        prutaReporte.append("	     IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_REC, ");
         prutaReporte.append("       IDOSGD.PK_SGD_DESCRIPCION_DE_DOMINIOS('ORI_DOCUMENTO',A.CCOD_ORIGING) deOriEmiMp, ");
        prutaReporte.append("        IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP) deDependencia ");
        
        prutaReporte.append("FROM IDOSGD.TDTV_REMITOS A, ");
        prutaReporte.append("     IDOSGD.TDTX_REMITOS_RESUMEN B, ");
        prutaReporte.append("     IDOSGD.TDTC_EXPEDIENTE C ");
        prutaReporte.append("WHERE A.NU_ANN=B.NU_ANN ");
        prutaReporte.append("AND A.NU_EMI=B.NU_EMI ");
        prutaReporte.append("AND C.NU_ANN_EXP=A.NU_ANN_EXP ");
        prutaReporte.append("AND C.NU_SEC_EXP=A.NU_SEC_EXP ");
        
        try {
            String pnuAnn = buscarDocumentoExtRecepBean.getCoAnnio();
            if (!(buscarDocumentoExtRecepBean.getEsFiltroFecha().equals("1") && pnuAnn.equals("0"))) {
                // Parametros Basicos
                prutaReporte.append(" AND A.NU_ANN = '").append(pnuAnn).append("'");
            }
            prutaReporte.append(" AND A.CO_GRU = '3'"); 
            prutaReporte.append(" AND A.TI_EMI<>'01'");
            prutaReporte.append(" AND A.ES_ELI = '0'");
            // Parametros Basicos
            prutaReporte.append(" AND A.CO_DEP_EMI = '").append(buscarDocumentoExtRecepBean.getCoDependencia()).append("'");

            String auxTipoAcceso=buscarDocumentoExtRecepBean.getTiAcceso();
            String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1"))?auxTipoAcceso:"1";           
            if (tiAcceso.equals("1")) { // acceso personal
                prutaReporte.append(" AND A.CO_EMP_RES = '").append(buscarDocumentoExtRecepBean.getCoEmpleado()).append("'");
            } else {
                if(buscarDocumentoExtRecepBean.getInMesaPartes().equals("0")/* && buscarDocExt.getInCambioEst().equals("0")*/){
                    bBusqDep = true;
                    prutaReporte.append(" AND A.CO_DEP = '").append(buscarDocumentoExtRecepBean.getCoDependencia()).append("'");        
                }
            }            
            //String pTipoBusqueda = buscarDocumentoExtRecepBean.getTipoBusqueda();
            
            //if (pTipoBusqueda.equals("1") && buscarDocumentoExtRecepBean.isEsIncluyeFiltro()) {
            //    bBusqFiltro = true;
            //}
            
            //Filtro
            //if (pTipoBusqueda.equals("0") || bBusqFiltro) {
                if (buscarDocumentoExtRecepBean.getCoTipoDoc() != null && buscarDocumentoExtRecepBean.getCoTipoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.CO_TIP_DOC_ADM = '").append(buscarDocumentoExtRecepBean.getCoTipoDoc()).append("'");
                }
                if (buscarDocumentoExtRecepBean.getCoEstadoDoc() != null && buscarDocumentoExtRecepBean.getCoEstadoDoc().trim().length() > 0) {
                    prutaReporte.append(" AND A.ES_DOC_EMI = '").append(buscarDocumentoExtRecepBean.getCoEstadoDoc()).append("'");
                }
                if (buscarDocumentoExtRecepBean.getCoTipoEmisor()!= null && buscarDocumentoExtRecepBean.getCoTipoEmisor().trim().length() > 0) {
                    prutaReporte.append(" AND A.TI_EMI = '").append(buscarDocumentoExtRecepBean.getCoTipoEmisor()).append("'");
                }
                if (buscarDocumentoExtRecepBean.getCoDepOriRec()!= null && buscarDocumentoExtRecepBean.getCoDepOriRec().trim().length() > 0 && !bBusqDep) {
                    prutaReporte.append(" AND A.CO_DEP = '").append(buscarDocumentoExtRecepBean.getCoDepOriRec()).append("'");    
                }               

                if(buscarDocumentoExtRecepBean.getCoProceso()!=null&&buscarDocumentoExtRecepBean.getCoProceso().trim().length()>0){
                     if(buscarDocumentoExtRecepBean.getCoProceso().equals("CON_TUPA")){
                        prutaReporte.append(" AND B.CO_PROCESO_EXP not in ('0000') "); 
                    }else{
                        prutaReporte.append(" AND B.CO_PROCESO_EXP = '").append(buscarDocumentoExtRecepBean.getCoProceso()).append("'");
                    }
                }            
                //if (buscarDocumentoExtRecepBean.getCoDepDestino() != null && buscarDocumentoExtRecepBean.getCoDepDestino().trim().length() > 0) {
                //    prutaReporte.append(" AND POSITION(B.TI_EMI_DES, '").append(buscarDocumentoExtRecepBean.getCoDepDestino()).append("') > 0 ");
                //}
                if (buscarDocumentoExtRecepBean.getEsFiltroFecha() != null
                        && (buscarDocumentoExtRecepBean.getEsFiltroFecha().equals("1") || buscarDocumentoExtRecepBean.getEsFiltroFecha().equals("3"))) {
                    String vFeEmiIni = buscarDocumentoExtRecepBean.getFeEmiIni();
                    String vFeEmiFin = buscarDocumentoExtRecepBean.getFeEmiFin();
                    if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0 && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                        prutaReporte.append(" AND A.FE_EMI between TO_DATE('").append(vFeEmiIni).append("','dd/mm/yyyy') AND TO_DATE('").append(vFeEmiFin)
                            .append("','dd/mm/yyyy') + TIME '23:59:59' ");
                    }
                }
            //}
            //Busqueda
            //if (pTipoBusqueda.equals("1")) {         
                if (buscarDocumentoExtRecepBean.getBusNumEmision() != null && buscarDocumentoExtRecepBean.getBusNumEmision().trim().length() > 0) {
                    prutaReporte.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                    objectParam.put("pnuCorEmi", Integer.getInteger(buscarDocumentoExtRecepBean.getBusNumEmision()));
                }

                if (buscarDocumentoExtRecepBean.getBusNumDoc()!= null && buscarDocumentoExtRecepBean.getBusNumDoc().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                    objectParam.put("pnuDocEmi", buscarDocumentoExtRecepBean.getBusNumDoc());
                }

                if (buscarDocumentoExtRecepBean.getBusNumExpediente() != null && buscarDocumentoExtRecepBean.getBusNumExpediente().trim().length() > 1) {
                    prutaReporte.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                    objectParam.put("pnuExpediente", buscarDocumentoExtRecepBean.getBusNumExpediente());
                }
                // Busqueda del Asunto
                 if (buscarDocumentoExtRecepBean.getBusAsunto()!= null && buscarDocumentoExtRecepBean.getBusAsunto().trim().length() > 1) {                    
                   // prutaReporte.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextMixto(buscarDocumentoExtRecepBean.getBusAsunto())+"', 1 ) > 1 ");
                 prutaReporte.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
                 objectParam.put("pDeAsunto", buscarDocumentoExtRecepBean.getBusAsunto().trim());
                 
                }
                
                if (buscarDocumentoExtRecepBean.getCoTipoExp() != null && !buscarDocumentoExtRecepBean.getCoTipoExp().equals("")) {
                    prutaReporte.append(" AND C.CCOD_TIPO_EXP = :codTipoExp");        
                    objectParam.put("codTipoExp", buscarDocumentoExtRecepBean.getCoTipoExp()); 
                }

                if (buscarDocumentoExtRecepBean.getCoOriDoc()!= null && !buscarDocumentoExtRecepBean.getCoOriDoc().equals("")) {
                    prutaReporte.append(" AND A.CCOD_ORIGING = :coOriDoc");        
                    objectParam.put("coOriDoc", buscarDocumentoExtRecepBean.getCoOriDoc()); 
                }

                if(buscarDocumentoExtRecepBean.getBusResultado().equals("1"))
                {
                    if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("03")){
                    prutaReporte.append(" AND A.NU_DNI_EMI = :pNumDni");
                    objectParam.put("pNumDni", buscarDocumentoExtRecepBean.getBusNumDni());
                    }
                    else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("02")){
                        prutaReporte.append(" AND A.NU_RUC_EMI = :pNumRuc");
                        objectParam.put("pNumRuc", buscarDocumentoExtRecepBean.getBusNumRuc());
                    }
                    else if(buscarDocumentoExtRecepBean.getCoTipoPersona().equals("04")){
                        prutaReporte.append(" AND A.CO_OTR_ORI_EMI = :pCoOtr");
                        objectParam.put("pCoOtr", buscarDocumentoExtRecepBean.getBusCoOtros());
                    }
                }
                
            //}

            prutaReporte.append(" ORDER BY A.NU_COR_EMI DESC ");
            
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }

    @Override
    public String getPassDniPide(String pctabCodTab, String Dni) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CELE_DESCOR\n" +
                   "FROM   IDOSGD.SI_ELEMENTO\n" +
                   "WHERE  CTAB_CODTAB = ? AND CELE_DESELE= ?");

        String pkExp = null;
        try {
            pkExp = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pctabCodTab, Dni});            
        } catch (EmptyResultDataAccessException e) {
            pkExp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkExp;
    }
    @Override
    public DocumentoExtRecepBean getDocumentosExpInteExt(String nuExpediente) { 
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();  
        sql.append(" SELECT X.* FROM ( SELECT  A.NU_ANN,A.NU_EMI,B.NU_EXPEDIENTE \n" +
                "         FROM IDOSGD.TDTV_REMITOS A\n" +
                "         inner join IDOSGD.TDTX_REMITOS_RESUMEN B on B.NU_ANN=A.NU_ANN  AND B.NU_EMI=A.NU_EMI\n" +
                "         inner join IDOSGD.TDTC_EXPEDIENTE C on C.NU_ANN_EXP=A.NU_ANN_EXP AND C.NU_SEC_EXP=A.NU_SEC_EXP\n" +
                "         WHERE  A.ES_ELI='0' ");     
       
        if(nuExpediente.length()==12)
            sql.append(" AND A.TI_EMI='03'"); 
              
          
        sql.append(" AND B.NU_EXPEDIENTE = ? ");
        objectParam.put("pnuExpediente", nuExpediente);
                         
        sql.append(" ORDER BY A.NU_EMI  ) X  LIMIT 1  ");
        
        DocumentoExtRecepBean documentoExtRecepBean = null;

        try { 
            documentoExtRecepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoExtRecepBean.class),
                    new Object[]{nuExpediente});
        }catch (EmptyResultDataAccessException e) {
            
        }catch (Exception e) {
             
            e.printStackTrace();
        }
        return documentoExtRecepBean;
    }
}
