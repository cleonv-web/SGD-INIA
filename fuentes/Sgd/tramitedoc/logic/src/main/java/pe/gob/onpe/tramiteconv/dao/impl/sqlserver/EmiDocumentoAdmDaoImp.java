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
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DestinoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaEmiDocBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaRemitoBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteEmiBean;
import pe.gob.onpe.tramitedoc.bean.TblRemitosBean;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.util.Constantes;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;


/*interoperabilidad*/
import pe.gob.onpe.tramitedoc.bean.DatosInterBean;
import pe.gob.onpe.tramitedoc.util.AleatorioCodVerificacion;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

/*interoperabilidad*/

@Repository("emiDocumentoAdmDao")
public class EmiDocumentoAdmDaoImp extends SimpleJdbcDaoBase implements EmiDocumentoAdmDao {
    private SimpleJdbcCall spActualizaEstado;
    private SimpleJdbcCall spdataSource;
/*interoperabilidad*/
private SimpleJdbcCall spInsMesaVirtual;
/*interoperabilidad*/
    @Override
    public List<DocumentoEmiBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmi) {
        StringBuilder sql = new StringBuilder();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoEmiBean> list = new ArrayList<DocumentoEmiBean>();

        sql.append("SELECT TOP 100 X.*, ");
        sql.append(" (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_DE_EMI_REF](X.NU_ANN, X.NU_EMI)) DE_EMI_REF, ");        
        sql.append(" [IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        sql.append(" CASE X.NU_CANDES ");
        sql.append("	WHEN 1 THEN (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP](X.NU_ANN, X.NU_EMI)) ");
        sql.append("	ELSE (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP_V](X.NU_ANN, X.NU_EMI)) ");
        sql.append(" END DE_EMP_PRO, ");
        sql.append(" [IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](X.CO_EMP_RES) DE_EMP_RES, ");        
        sql.append(" [IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS](X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        sql.append(" ROW_NUMBER() OVER (ORDER BY X.NU_ANN) AS ROWNUM ");    
        sql.append(" FROM ( "); 
        sql.append(" SELECT "); 
        sql.append(" A.NU_COR_EMI, A.FE_EMI, "); 
        sql.append(" CASE ISNULL(B.TI_EMI_REF, '0') + ISNULL(B.IN_EXISTE_ANEXO, '2') "); 
        sql.append("	WHEN '00' THEN 0 ");
        sql.append("	WHEN '02' THEN 0 ");
        sql.append("    ELSE 1 ");
        sql.append(" END EXISTE_ANEXO, ");
        sql.append(" ISNULL(B.CO_PRIORIDAD, '1') CO_PRIORIDAD, "); 
        sql.append(" CONVERT(VARCHAR(10), A.FE_EMI, 103) FE_EMI_CORTA, ");
        sql.append(" B.NU_DOC,UPPER(A.DE_ASU) DE_ASU_M,A.NU_ANN,B.NU_EXPEDIENTE,A.NU_EMI,A.TI_CAP,B.IN_EXISTE_DOC EXISTE_DOC, ");
        sql.append(" A.ES_DOC_EMI,A.CO_TIP_DOC_ADM,A.NU_CANDES,A.CO_EMP_RES ,CAST(ISNULL(A.DOC_ESTADO_MSJ,'-1') AS NVARCHAR(20)) as DOC_ESTADO_MSJ,");
        sql.append(" (SELECT MAX(C.TI_DES) FROM IDOSGD.TDTV_DESTINOS C  WITH (NOLOCK) WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN   ) as tiDest,CAST(ISNULL(A.TI_ENV_MSJ,'-1')AS NVARCHAR(20)) tiEnvMsj,");//ORDER BY C.TI_DES DESC INTEROPERABILIDAD
        sql.append(" CASE A.TI_ENV_MSJ WHEN '0' THEN 'MESA DE PARTES' WHEN '1' THEN 'ENVÍO DIRECTO' WHEN '2' THEN 'MESA VIRTUAL' END    recursoenvio");//interoperabilidad
        sql.append(" FROM IDOSGD.TDTV_REMITOS A  WITH (NOLOCK) "); 
        sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN B  WITH (NOLOCK) ON B.NU_ANN=A.NU_ANN AND B.NU_EMI=A.NU_EMI ");
        sql.append(" WHERE 1=1 ");        
        String pNUAnn = buscarDocumentoEmi.getsCoAnnio();
        if (!(buscarDocumentoEmi.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNUAnn);
        }
        sql.append(" AND A.TI_EMI='01'");
        sql.append(" AND B.TI_EMI='01'");
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND A.CO_GRU = '1'");
        sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi");

        // Parametros Basicos
        objectParam.put("pCoDepEmi", buscarDocumentoEmi.getsCoDependencia());

        String pTipoBusqueda = buscarDocumentoEmi.getsTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoEmi.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        String auxTipoAcceso=buscarDocumentoEmi.getsTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1")||auxTipoAcceso.equals("2"))?auxTipoAcceso:"1";                
        if (buscarDocumentoEmi.getsElaboradoPor()!=null&&buscarDocumentoEmi.getsElaboradoPor().trim().length()>0&&(pTipoBusqueda.equals("0")||bBusqFiltro)&&tiAcceso.equals("0")) {
            sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
            objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsElaboradoPor());
        } else {
            if (tiAcceso.equals("1")) {
                sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsCoEmpleado());
            } else if (tiAcceso.equals("2")) {
                sql.append(" AND (A.CO_EMP_RES = :pcoEmpRes OR A.CO_EMP_EMI = :pcoEmpRes) ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsCoEmpleado());
            }
        }

        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            if (buscarDocumentoEmi.getCoTema()!= null && buscarDocumentoEmi.getCoTema().trim().length() > 0) {
                sql.append(" AND A.CO_TEMA = :pCoTema ");
                objectParam.put("pCoTema", buscarDocumentoEmi.getCoTema());
            }
            if (buscarDocumentoEmi.getsTipoDoc() != null && buscarDocumentoEmi.getsTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoEmi.getsTipoDoc());
            }
            if (buscarDocumentoEmi.getsEstadoDoc() != null && buscarDocumentoEmi.getsEstadoDoc().trim().length() > 0) {
                sql.append(" AND A.ES_DOC_EMI = :pEsDocEmi ");
                objectParam.put("pEsDocEmi", buscarDocumentoEmi.getsEstadoDoc());
            }else{
                sOrdenList=" DESC";
            }
            if (buscarDocumentoEmi.getsPrioridadDoc() != null && buscarDocumentoEmi.getsPrioridadDoc().trim().length() > 0) {
                sql.append(" AND B.CO_PRIORIDAD = :pCoPrioridad ");
                objectParam.put("pCoPrioridad", buscarDocumentoEmi.getsPrioridadDoc());
            }
            if (buscarDocumentoEmi.getsRefOrigen() != null && buscarDocumentoEmi.getsRefOrigen().trim().length() > 0) {
                sql.append(" AND CHARINDEX(:pTiEmiRef,B.TI_EMI_REF) > 0 ");
                objectParam.put("pTiEmiRef", buscarDocumentoEmi.getsRefOrigen());
            }
            if (buscarDocumentoEmi.getsDestinatario() != null && buscarDocumentoEmi.getsDestinatario().trim().length() > 0) {
                sql.append(" AND CHARINDEX(:pTiEmpPro, B.TI_EMI_DES) > 0 ");
                objectParam.put("pTiEmpPro", buscarDocumentoEmi.getsDestinatario());
            }
            if (buscarDocumentoEmi.getEsFiltroFecha() != null
                    && (buscarDocumentoEmi.getEsFiltroFecha().equals("1") || buscarDocumentoEmi.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoEmi.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoEmi.getsFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) { 
                    sql.append(" AND FE_EMI BETWEEN CONVERT(DATETIME, :pFeEmiIni, 103) AND CONVERT(DATETIME, :pFeEmiFin, 103) + 0.99999 ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
            //YUAL
            if (buscarDocumentoEmi.getCoEstMensajeria()!= null && buscarDocumentoEmi.getCoEstMensajeria().trim().length()>0) {
                sql.append(" AND CO_TIP_DOC_ADM in (SELECT CDOC_TIPDOC FROM IDOSGD.SI_MAE_TIPO_DOC WHERE IN_DOC_SALIDA='1') AND A.ES_DOC_EMI=0 AND  ISNULL((A.TI_ENV_MSJ),3)= :pcoEstMensajeria");
                objectParam.put("pcoEstMensajeria", buscarDocumentoEmi.getCoEstMensajeria());
            }
        }

        //Busqueda
        if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoEmi.getsNumCorEmision() != null && buscarDocumentoEmi.getsNumCorEmision().trim().length() > 0) {
                sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", buscarDocumentoEmi.getsNumCorEmision());
            }

            if (buscarDocumentoEmi.getsNumDoc() != null && buscarDocumentoEmi.getsNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE ''+:pnuDocEmi+'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoEmi.getsNumDoc());
            }

            if (buscarDocumentoEmi.getsBuscNroExpediente() != null && buscarDocumentoEmi.getsBuscNroExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'+:pnuExpediente+'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoEmi.getsBuscNroExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoEmi.getsDeAsuM() != null && buscarDocumentoEmi.getsDeAsuM().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(buscarDocumentoEmi.getsDeAsuM())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE '%'+:pDeAsunto+'%' ");
                objectParam.put("pDeAsunto", buscarDocumentoEmi.getsDeAsuM());
            }
        }        
        sql.append(") AS X ");
        sql.append(" ORDER BY X.FE_EMI").append(sOrdenList);

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class));

        } catch (EmptyResultDataAccessException e) {
            list = null;
            e.printStackTrace();
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    
      @Override
      public ProcessResult<List<DocumentoBean>> getDocumentosBuscaEmiAdmList(BuscarDocumentoEmiBean buscarDocumentoEmi,FiltroPaginate paginate) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();
        ProcessResult<List<DocumentoBean>> Result = new ProcessResult<List<DocumentoBean>>();
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();

        sql.append("SELECT X.*,");
        sql.append(" (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_DE_EMI_REF](X.NU_ANN, X.NU_EMI)) DE_EMI_REF,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");        
        sql.append(" (CASE X.NU_CANDES WHEN 1 THEN (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(X.NU_ANN, X.NU_EMI)) ELSE (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(X.NU_ANN, X.NU_EMI)) END) DE_EMP_PRO,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI ");        
        //sql.append("ROWNUM");
        sql.append(" FROM ( SELECT X.*,COUNT(X.NU_ANN) OVER()  AS filasTotal, ROW_NUMBER() OVER(");
        
        if(buscarDocumentoEmi.getOrden().equals("feEmiCorta-ASC")) sql.append(" ORDER BY FE_EMI ASC  ");
        else if(buscarDocumentoEmi.getOrden().equals("feEmiCorta-DESC"))   sql.append(" ORDER BY FE_EMI DESC ");
        else if(buscarDocumentoEmi.getOrden().equals("deEmiRef-ASC")) sql.append(" ORDER BY NU_RUC_EMI,NU_DNI_EMI,CO_OTR_ORI_EMI ASC");
        else if(buscarDocumentoEmi.getOrden().equals("deEmiRef-DESC")) sql.append(" ORDER BY NU_RUC_EMI,NU_DNI_EMI,CO_OTR_ORI_EMI DESC ");
        else if(buscarDocumentoEmi.getOrden().equals("deTipDocAdm-ASC")) sql.append(" ORDER BY CO_TIP_DOC_ADM ASC ");
        else if(buscarDocumentoEmi.getOrden().equals("deTipDocAdm-DESC")) sql.append(" ORDER BY CO_TIP_DOC_ADM DESC ");
        else if(buscarDocumentoEmi.getOrden().equals("nuDoc-ASC")) sql.append(" ORDER BY NU_DOC ASC ");
        else if(buscarDocumentoEmi.getOrden().equals("nuDoc-DESC")) sql.append(" ORDER BY NU_DOC DESC ");
        else if(buscarDocumentoEmi.getOrden().equals("deEmpPro-ASC")) sql.append(" ORDER BY ti_emi ASC ");
        else if(buscarDocumentoEmi.getOrden().equals("deEmpPro-DESC")) sql.append(" ORDER BY ti_emi DESC ");
        else if(buscarDocumentoEmi.getOrden().equals("deAsuM-ASC")) sql.append(" ORDER BY DE_ASU_M ASC ");
        else if(buscarDocumentoEmi.getOrden().equals("deAsuM-DESC")) sql.append(" ORDER BY DE_ASU_M DESC ");
        else if(buscarDocumentoEmi.getOrden().equals("nuExpediente-ASC")) sql.append(" ORDER BY NU_EXPEDIENTE ASC ");
        else if(buscarDocumentoEmi.getOrden().equals("nuExpediente-DESC")) sql.append(" ORDER BY NU_EXPEDIENTE DESC ");         
        else if(buscarDocumentoEmi.getOrden().equals("deEmpRes-ASC")) sql.append(" ORDER BY CO_EMP_RES ASC ");
        else if(buscarDocumentoEmi.getOrden().equals("deEmpRes-DESC")) sql.append(" ORDER BY CO_EMP_RES DESC ");     
        else if(buscarDocumentoEmi.getOrden().equals("deEsDocEmi-ASC")) sql.append(" ORDER BY ES_DOC_EMI ASC ");
        else if(buscarDocumentoEmi.getOrden().equals("deEsDocEmi-DESC")) sql.append(" ORDER BY ES_DOC_EMI DESC ");        
        else sql.append(" ORDER BY FE_EMI ASC ");
        
        sql.append(" )  AS fila   ");
        sql.append(" FROM ( ");
        sql.append(" SELECT NU_RUC_EMI,NU_DNI_EMI,CO_OTR_ORI_EMI ,A.ti_emi,");
        sql.append(" A.NU_COR_EMI,A.FE_EMI,(CASE ISNULL(B.TI_EMI_REF,'0')+ISNULL(B.IN_EXISTE_ANEXO,'2') WHEN '00' THEN 0 WHEN '02' THEN 0 ELSE 1 END) EXISTE_ANEXO, ISNULL(B.CO_PRIORIDAD,'1') CO_PRI,");
        sql.append(" CONVERT(NVARCHAR,A.FE_EMI,103) FE_EMI_CORTA,");
        sql.append(" A.OBS_DOC DE_OBS_DOC,");
        sql.append(" B.NU_DOC,UPPER(A.DE_ASU) DE_ASU_M,A.NU_ANN,B.NU_EXPEDIENTE,A.NU_EMI,A.TI_CAP,B.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append(" A.ES_DOC_EMI,A.CO_TIP_DOC_ADM,A.NU_CANDES,A.CO_EMP_RES,ISNULL(A.DOC_ESTADO_MSJ,-1) as DOC_ESTADO_MSJ,");
        sql.append(" (SELECT MAX(C.TI_DES) FROM IDOSGD.TDTV_DESTINOS C WITH (NOLOCK) WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN   ) as tiDest,ISNULL(A.TI_ENV_MSJ,-1) tiEnvMsj,");//ORDER BY C.TI_DES DESC INTEROPERABILIDAD
        sql.append(" CASE A.TI_ENV_MSJ WHEN '0' THEN 'MESA DE PARTES' WHEN '1' THEN 'ENVÍO DIRECTO' WHEN '2' THEN 'MESA VIRTUAL' END    recursoenvio");//interoperabilidad
        sql.append(" ,VB.IN_VB sFirmaVB  ");
        sql.append(" FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK) INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN B WITH (NOLOCK) ON  B.NU_ANN=A.NU_ANN AND B.NU_EMI=A.NU_EMI ");          
        sql.append(" LEFT JOIN ( SELECT VB.NU_ANN,VB.NU_EMI\n" +
                        ",MAX(CASE WHEN VB.IN_VB='B' OR VB.IN_VB='0' THEN 1\n" +
                        "WHEN VB.IN_VB='2' THEN 2 \n" +
                        "WHEN VB.IN_VB='1' THEN 0 END ) IN_VB\n" +
                        "FROM IDOSGD.TDTV_PERSONAL_VB VB WITH (NOLOCK) \n" +
                        "GROUP BY VB.NU_ANN,VB.NU_EMI  ) VB ON VB.NU_ANN=A.NU_ANN  AND VB.NU_EMI=A.NU_EMI ");  
        sql.append(" WHERE"); 
      //YUAL: problemas al visualizar documetno de dos años
        /*
        String pNUAnn = buscarDocumentoEmi.getsCoAnnio();
        if (!(buscarDocumentoEmi.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNUAnn);
        }*/
        sql.append(" A.TI_EMI='01'");
        sql.append(" AND B.TI_EMI='01'");
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND A.CO_GRU = '1'");
        sql.append(" AND A.CO_DEP_EMI = :pCoDepEmi");

        // Parametros Basicos
        objectParam.put("pCoDepEmi", buscarDocumentoEmi.getsCoDependencia());

        String pTipoBusqueda = buscarDocumentoEmi.getsTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoEmi.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        String auxTipoAcceso=buscarDocumentoEmi.getsTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1")||auxTipoAcceso.equals("2")||auxTipoAcceso.equals("3"))?auxTipoAcceso:"1";               
       if (buscarDocumentoEmi.getsElaboradoPor()!=null
                &&buscarDocumentoEmi.getsElaboradoPor().trim().length()>0
                &&(pTipoBusqueda.equals("0")||bBusqFiltro)
                &&(tiAcceso.equals("0") || tiAcceso.equals("3"))) {
            if(tiAcceso.equals("3")){
                sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsElaboradoPor());
            }
            else {                
             sql.append("  AND A.CO_EMP_RES = :pcoEmpRes ");//AND A.CONFIDENCIAL='0'
                objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsElaboradoPor());
            }
        } else {
            if (tiAcceso.equals("1")) {
                sql.append(" AND A.CO_EMP_RES = :pcoEmpRes ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsCoEmpleado());
            } else if (tiAcceso.equals("2")) {
                sql.append(" AND (A.CO_EMP_RES = :pcoEmpRes OR A.CO_EMP_EMI = :pcoEmpRes) ");
                objectParam.put("pcoEmpRes", buscarDocumentoEmi.getsCoEmpleado());
            }
            //else if(tiAcceso.equals("0")){
            //    sql.append(" AND A.CONFIDENCIAL='0'  "); 
            //}
        }
        String bExcepSeach="0";
        //Filtro
        //if (pTipoBusqueda.equals("0") || bBusqFiltro) {
        if(bExcepSeach=="0") {
            if (buscarDocumentoEmi.getCoTema()!= null && buscarDocumentoEmi.getCoTema().trim().length() > 0) {
                sql.append(" AND A.CO_TEMA = :pCoTema ");
                objectParam.put("pCoTema", buscarDocumentoEmi.getCoTema());
            }
            if (buscarDocumentoEmi.getsTipoDoc() != null && buscarDocumentoEmi.getsTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoEmi.getsTipoDoc());
            }
            if (buscarDocumentoEmi.getsEstadoDoc() != null && buscarDocumentoEmi.getsEstadoDoc().trim().length() > 0) {
                sql.append(" AND A.ES_DOC_EMI = :pEsDocEmi ");
                objectParam.put("pEsDocEmi", buscarDocumentoEmi.getsEstadoDoc());
            }else{
                sOrdenList=" DESC";
            }
            if (buscarDocumentoEmi.getsPrioridadDoc() != null && buscarDocumentoEmi.getsPrioridadDoc().trim().length() > 0) {
                sql.append(" AND B.CO_PRIORIDAD = :pCoPrioridad ");
                objectParam.put("pCoPrioridad", buscarDocumentoEmi.getsPrioridadDoc());
            }
            if (buscarDocumentoEmi.getsRefOrigen() != null && buscarDocumentoEmi.getsRefOrigen().trim().length() > 0) {
                sql.append(" AND CHARINDEX(B.TI_EMI_REF, :pTiEmiRef) > 0 ");
                objectParam.put("pTiEmiRef", buscarDocumentoEmi.getsRefOrigen());
            }
            if (buscarDocumentoEmi.getsDestinatario() != null && buscarDocumentoEmi.getsDestinatario().trim().length() > 0) {
                sql.append(" AND A.NU_EMI IN (SELECT NU_EMI FROM [IDOSGD].[TDTV_DESTINOS] WHERE CO_DEP_DES=:psDestinatario ) ");
                objectParam.put("psDestinatario", buscarDocumentoEmi.getsDestinatario());
            }
             if (buscarDocumentoEmi.getCoEmpRec()!= null && buscarDocumentoEmi.getCoEmpRec().trim().length() > 0) { 
                sql.append(" AND A.NU_EMI IN (SELECT NU_EMI FROM [IDOSGD].[TDTV_DESTINOS] WHERE CO_EMP_DES=:pCoEmpRec ) ");
                objectParam.put("pCoEmpRec", buscarDocumentoEmi.getCoEmpRec());
            }
            if (buscarDocumentoEmi.getsParaFirmar() != null && buscarDocumentoEmi.getsParaFirmar().equals("P")) {
                sql.append(" AND VB.IN_VB IN ('1','2') "); 
            }
            if (buscarDocumentoEmi.getsParaFirmar() != null && buscarDocumentoEmi.getsParaFirmar().equals("F")) {
                sql.append(" AND (VB.IN_VB ='0' or  VB.IN_VB IS NULL)"); 
            }
            if (buscarDocumentoEmi.getsFirmadoPor() != null && buscarDocumentoEmi.getsFirmadoPor().trim().length() > 0) {
                sql.append(" AND A.CO_EMP_EMI = :pFirmadoPor ");
                objectParam.put("pFirmadoPor", buscarDocumentoEmi.getsFirmadoPor());
            }
             
           //YUAL
            //if (buscarDocumentoEmi.getsTrabDestino()!= null && buscarDocumentoEmi.getsTrabDestino().trim().length() > 0) {
            //    sql.append(" AND isnull((SELECT TOP 1 1 FROM TDTV_DESTINOS C  WITH (NOLOCK) WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN AND CO_EMP_DES=:pTiEmpDest),0) > 0 ");
            //    objectParam.put("pTiEmpDest", buscarDocumentoEmi.getsTrabDestino());
            //}
            if (buscarDocumentoEmi.getEsFiltroFecha() != null
                    && (buscarDocumentoEmi.getEsFiltroFecha().equals("1") || buscarDocumentoEmi.getEsFiltroFecha().equals("3") || buscarDocumentoEmi.getEsFiltroFecha().equals("2"))) {
                String vFeEmiIni = buscarDocumentoEmi.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoEmi.getsFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND FE_EMI BETWEEN CONVERT(DATETIME, :pFeEmiIni, 103) AND CONVERT(DATETIME, :pFeEmiFin, 103) + 0.99999 ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }            
            //YUAL
            if (buscarDocumentoEmi.getCoEstMensajeria()!= null && buscarDocumentoEmi.getCoEstMensajeria().trim().length()>0) {
                sql.append(" AND CO_TIP_DOC_ADM in ( SELECT CDOC_TIPDOC FROM IDOSGD.SI_MAE_TIPO_DOC WITH (NOLOCK)  WHERE IN_DOC_SALIDA=1 ) AND A.ES_DOC_EMI=0 AND  ISNULL((A.TI_ENV_MSJ),3)= :pcoEstMensajeria");
                objectParam.put("pcoEstMensajeria", buscarDocumentoEmi.getCoEstMensajeria());
            }
            
        }

        //Busqueda
        ///if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoEmi.getsNumCorEmision() != null && buscarDocumentoEmi.getsNumCorEmision().trim().length() > 0) {
                sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", buscarDocumentoEmi.getsNumCorEmision());
            }

            if (buscarDocumentoEmi.getsNumDoc() != null && buscarDocumentoEmi.getsNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE ''+:pnuDocEmi+'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoEmi.getsNumDoc());
            }

            if (buscarDocumentoEmi.getsBuscNroExpediente() != null && buscarDocumentoEmi.getsBuscNroExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'+:pnuExpediente+'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoEmi.getsBuscNroExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoEmi.getsDeAsuM() != null && buscarDocumentoEmi.getsDeAsuM().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(buscarDocumentoEmi.getsDeAsuM())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE '%'+:pDeAsunto+'%' ");
                objectParam.put("pDeAsunto", buscarDocumentoEmi.getsDeAsuM());
            }
            
             if (buscarDocumentoEmi.getsNumDocRef() != null && buscarDocumentoEmi.getsNumDocRef().trim().length() > 1) {
             
                //busqeda referencia
                 sql.append("  AND A.NU_EMI IN( ");
                sql.append("   SELECT D.NU_EMI ");
                sql.append("   FROM TDTR_ARBOL_SEG D ");
                sql.append("   INNER JOIN TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
                sql.append("   WHERE R.NU_ANN='"+buscarDocumentoEmi.getsCoAnnioBus()+"'   ");
                if (buscarDocumentoEmi.getsDeTipoDocAdm() != null && buscarDocumentoEmi.getsDeTipoDocAdm().trim().length() > 1) {
                    sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoEmi.getsDeTipoDocAdm()+"' ");
                }
                if (buscarDocumentoEmi.getsBuscDestinatario() != null && buscarDocumentoEmi.getsBuscDestinatario().trim().length() > 1) {
                    sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoEmi.getsBuscDestinatario()+"' ");
                }
                sql.append("   AND ((R.NU_DOC_EMI LIKE '%'+'"+buscarDocumentoEmi.getsNumDocRef()+"'+'%') OR (R.DE_DOC_SIG LIKE '%'+'"+buscarDocumentoEmi.getsNumDocRef()+"'+'%'))");
                sql.append(" )");
            }
      
         
        sql.append(") X  ) X ");
        sql.append(" WHERE fila BETWEEN "+paginate.getPaginaDesde()+" AND "+paginate.getPaginaHasta()+"  ");
        
      
       // System.out.println("sql:"+sql);
        
        try {

            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));
            Result.setResult(list);           
            Result.setIsSuccess(true);
            //objectParam = new HashMap<String, Object>();
            //buscarDocumentoEmi= new BuscarDocumentoEmiBean();
            //list = new ArrayList<DocumentoBean>();
            //sql = new StringBuffer();
      } catch (EmptyResultDataAccessException e) {
            Result.setIsSuccess(false);
            Result.setMessage(e.getMessage());
       } catch (Exception e) {
            Result.setIsSuccess(false);
            Result.setMessage(e.getMessage());
           e.printStackTrace();
       }
        return Result;
    }

    
    
    @Override
    public DocumentoEmiBean getDocumentoEmitido(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT "); 
        sql.append("a.nu_ann, "); 
        sql.append("a.nu_emi, "); 
        sql.append("CAST(a.nu_cor_emi AS VARCHAR(10)) nu_cor_emi, "); 
        sql.append("a.co_loc_emi, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](a.co_loc_emi) de_loc_emi, "); 
        sql.append("a.co_dep_emi, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_emi) de_dep_emi, ");
        sql.append("a.ti_emi, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_TI_DESTINO](a.ti_emi) de_tip_emi, ");
        sql.append("a.co_emp_emi, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](co_emp_emi) de_emp_emi, ");
        sql.append("a.co_emp_res, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](a.co_emp_res) de_emp_res, ");
        sql.append("a.nu_dni_emi, "); 
        sql.append("a.nu_ruc_emi, "); 
        sql.append("a.co_otr_ori_emi, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_TI_EMI_EMP](a.nu_ann, a.nu_emi) de_ori_emi, ");
        sql.append("CONVERT(VARCHAR(10), a.fe_emi, 103) fe_emi, "); 
        sql.append("CONVERT(VARCHAR(10), A.FE_EMI, 103) FE_EMI_CORTA, ");
        sql.append("a.co_tip_doc_adm, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](a.co_tip_doc_adm) de_tip_doc_adm, ");
        sql.append("a.ti_emi, ");
        sql.append("a.nu_doc_emi, ");
        sql.append("a.de_doc_sig, ");
        sql.append("a.es_doc_emi, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS](a.es_doc_emi, 'TDTV_REMITOS') de_es_doc_emi, ");
        sql.append("a.nu_dia_ate, "); 
        sql.append("a.de_asu, "); 
        sql.append("-- a.co_pro, "); 
        sql.append("-- a.co_sub, "); 
        sql.append("a.ti_cap, "); 
        sql.append("a.co_exp, ");
        sql.append("a.co_use_cre, ");
        sql.append("a.fe_use_cre, "); 
        sql.append("a.co_use_mod, "); 
        sql.append("a.fe_use_mod, "); 
        sql.append("a.nu_ann_exp, ");
        sql.append("a.nu_sec_exp, "); 
        sql.append("a.nu_det_exp, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE](a.nu_ann_exp,a.nu_sec_exp) NU_EXPEDIENTE ");
        sql.append("FROM IDOSGD.TDTV_REMITOS a WITH (NOLOCK) "); 
        sql.append("WHERE A.NU_ANN = ? ");
        sql.append("AND A.NU_EMI = ? ");

        DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiBean;
    }

    @Override
    public DocumentoEmiBean getDocumentoEmiAdm(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("select A.*, "
           + " OBS_DOC DE_OBS_DOC,");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](A.CO_EMP_EMI) DE_EMP_EMI, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](A.CO_EMP_RES) DE_EMP_RES, ");
        sql.append("CONVERT(VARCHAR(10), A.FE_EMI, 103) FE_EMI_CORTA, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS](A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI, ");
        sql.append("B.FE_EXP, ");
        sql.append("CONVERT(VARCHAR(10), B.FE_EXP, 103) FE_EXP_CORTA,B.NU_EXPEDIENTE, ");
        sql.append("B.CO_PROCESO, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_PROCESO_EXP](B.CO_PROCESO) DE_PROCESO, ");
        sql.append("RR.IN_FIRMA_ANEXO, ");
        sql.append(" CAST(ISNULL(A.DOC_ESTADO_MSJ,'-1') AS NVARCHAR(20))  DOC_ESTADO_MSJ,(SELECT MAX(C.TI_DES) FROM IDOSGD.TDTV_DESTINOS C  WITH (NOLOCK)  WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN  ) as tiDest,CAST(ISNULL(A.TI_ENV_MSJ,'-1') AS NVARCHAR(20)) tiEnvMsj \n"); /*interoperabilidad*/
        sql.append(" from IDOSGD.TDTV_REMITOS A  WITH (NOLOCK) "); 
        sql.append(" left join IDOSGD.TDTC_EXPEDIENTE B  WITH (NOLOCK) on A.NU_ANN_EXP = B.NU_ANN_EXP and A.NU_SEC_EXP = B.NU_SEC_EXP  ");
        sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR  WITH (NOLOCK) ON A.NU_ANN = RR.NU_ANN and A.NU_EMI = RR.NU_EMI ");
        sql.append(" where A.NU_ANN = ? "); 
        sql.append(" AND A.NU_EMI = ? "); 

        DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            System.out.println("nu_ann="+pnuAnn+","+"nu_emi="+pnuEmi);
            e.printStackTrace();
        }
        return documentoEmiBean;
    }

    @Override
    public ExpedienteBean getExpDocumentoEmitido(String pnuAnnExp, String pnuSecExp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("select B.NU_EXPEDIENTE, ");
        sql.append("B.FE_EXP, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_PROCESO_EXP](B.CO_PROCESO) DE_PROCESO, ");
        sql.append("CONVERT(VARCHAR(10), B.FE_EXP, 103) FE_EXP_CORTA, ");
        sql.append("B.NU_ANN_EXP, ");
        sql.append("B.NU_SEC_EXP, ");
        sql.append("B.CO_PROCESO ");
        sql.append("from IDOSGD.TDTC_EXPEDIENTE B  WITH (NOLOCK) "); 
        sql.append("where B.NU_ANN_EXP = ? "); 
        sql.append("AND B.NU_SEC_EXP = ? ");

        ExpedienteBean expedienteBean = new ExpedienteBean();
        try {
            expedienteBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(ExpedienteBean.class),
                    new Object[]{pnuAnnExp, pnuSecExp});
        } catch (EmptyResultDataAccessException e) {
            expedienteBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return expedienteBean;
    }

    @Override
    public EmpleadoBean getEmpleadoLocaltblDestinatario(String pcoDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.CO_LOC CO_LOCAL, ");
        sql.append("	   [IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](A.CO_LOC) DE_LOCAL, ");
        sql.append("	   B.CO_EMPLEADO CEMP_CODEMP, ");
        sql.append("	   [IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](B.CO_EMPLEADO) COMP_NAME ");
        sql.append("FROM IDOSGD.sitm_local_dependencia A WITH (NOLOCK) , ");
        sql.append("	 IDOSGD.rhtm_dependencia B  WITH (NOLOCK) ");
        sql.append("WHERE B.CO_DEPENDENCIA = ? ");
        sql.append("AND A.CO_DEP = ? ");

        EmpleadoBean empleadoBean = new EmpleadoBean();
        try {
            empleadoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class),
                    new Object[]{pcoDependencia, pcoDependencia});
        } catch (EmptyResultDataAccessException e) {
            empleadoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return empleadoBean;
    }

    @Override
    public List<EmpleadoBean> getPersonalDestinatario(String pcoDepen) {
        StringBuffer sql = new StringBuffer();
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();

        sql.append("SELECT e.cemp_apepat, e.cemp_apemat, e.cemp_denom, e.CEMP_CODEMP "); 
        sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e, "); 
        sql.append("( ");
        sql.append("SELECT CEMP_CODEMP "); 
        sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS  WITH (NOLOCK) "); 
        sql.append("where CEMP_EST_EMP = '1' "); 
        sql.append("  and (CO_DEPENDENCIA=? or CEMP_CO_DEPEND=?) "); 
        sql.append("union "); 
        sql.append("select co_emp from IDOSGD.tdtx_dependencia_empleado WITH (NOLOCK)   where co_dep=? and es_emp='0' "); 
        sql.append("union "); 
        sql.append("select co_empleado from IDOSGD.rhtm_dependencia  WITH (NOLOCK) where co_dependencia = ? "); 
        sql.append(") a "); 
        sql.append("where e.cemp_codemp = a.cemp_codemp ");
        sql.append("order by 1 ");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class),
                    new Object[]{pcoDepen, pcoDepen, pcoDepen,pcoDepen});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<MotivoBean> getLstMotivoxTipoDocumento(String pcoDepen, String pcoTipDoc) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        List<MotivoBean> list = new ArrayList<MotivoBean>();

        sql.append("SELECT a.de_mot, ");
        sql.append("       a.co_mot ");
        sql.append("  FROM IDOSGD.tdtr_motivo         a, ");
        sql.append("       IDOSGD.tdtx_moti_docu_depe b ");
        sql.append(" WHERE a.co_mot     = b.co_mot ");
        sql.append("   AND b.co_dep     = ? ");
        sql.append("   AND b.co_tip_doc = ? ");
        sql.append("UNION ");
        sql.append("SELECT de_mot, ");
        sql.append("       co_mot ");
        sql.append("FROM IDOSGD.tdtr_motivo ");  
        sql.append("where co_mot in ('0','1') ");
        sql.append(" ORDER BY 1 ");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(MotivoBean.class),
                    new Object[]{pcoDepen, pcoTipDoc});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<DocumentoBean> getLstDocEmitidoRef(String pcoEmpEmi, String pcoDepen, String pannio, String ptiDoc, String pnuDoc) {
        StringBuffer sql = new StringBuffer();
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();

        sql.append("SELECT A.* ");
        sql.append("FROM ( ");        

        sql.append("select E.*, ROW_NUMBER() OVER (ORDER BY E.fe_emi) AS ROWNUM ");
        sql.append("from( ");
        sql.append("select "); 
        sql.append("RE.fe_emi, ");
        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](RE.co_tip_doc_adm), 1, 100) DE_TIP_DOC_ADM, ");
        sql.append("RR.NU_DOC, ");                
        sql.append("CONVERT(VARCHAR(10), RE.fe_emi, 3) FE_EMI_CORTA, ");
        sql.append("null FE_REC_DOC_CORTA, ");
        sql.append("RE.NU_ANN, ");
        sql.append("RE.NU_EMI, ");
        sql.append("NULL NU_DES, ");
        sql.append("replace(ltrim(rtrim(RE.de_asu)), char(10), ' ') DE_ASU, "); 
        sql.append("RE.CO_TIP_DOC_ADM, ");  
        sql.append("SUBSTRING(RR.NU_EXPEDIENTE, 1, 20) NU_EXPEDIENTE, ");                
        sql.append("RE.NU_ANN_EXP, ");
        sql.append("RE.NU_SEC_EXP ");
        sql.append("from IDOSGD.TDTV_REMITOS RE WITH (NOLOCK) , IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK)  ");
        sql.append("where RE.nu_ann = ? ");
        sql.append("AND RE.NU_ANN=RR.NU_ANN ");
        sql.append("AND RE.NU_EMI=RR.NU_EMI ");
        sql.append("AND RE.es_eli = '0' ");
        sql.append("and RE.es_doc_emi not in ('9','7','5') ");
        sql.append("AND RE.CO_GRU = '1' ");
        sql.append("AND RE.co_dep_emi in "); 
        sql.append("(select ? ");
        sql.append("union "); 
        sql.append("  SELECT co_dep_ref ");
        sql.append("    FROM IDOSGD.tdtx_referencia  WITH (NOLOCK) ");
        sql.append("   WHERE co_dep_emi = ? ");
        sql.append("     AND ti_emi = 'D' ");
        sql.append("     AND es_ref = '1') ");
        sql.append("AND RE.co_tip_doc_adm = ? ");
        if(pnuDoc != null && pnuDoc.trim().length() > 0) {
            pnuDoc = Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
            sql.append("AND (RE.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("') ");
        }
        
        sql.append("UNION ");

        sql.append("select "); 
        sql.append("RE.fe_emi, ");                        
        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](RE.co_tip_doc_adm), 1, 100) TIPO, ");
        sql.append("RR.NU_DOC numero, ");
        sql.append("CONVERT(VARCHAR(10), RE.fe_emi, 3) fecha_emision, ");
        sql.append("null fecha_recepcion, ");
        sql.append("RE.NU_ANN, ");
        sql.append("RE.NU_EMI, ");
        sql.append("NULL NU_DES, ");
        sql.append("replace(ltrim(rtrim(RE.de_asu)), char(10), ' ') ASUNTO, "); 
        sql.append("RE.CO_TIP_DOC_ADM, ");  
        sql.append("SUBSTRING(RR.NU_EXPEDIENTE, 1, 20) NU_EXPEDIENTE, ");
        sql.append("RE.NU_ANN_EXP, ");
        sql.append("RE.NU_SEC_EXP ");
        sql.append("from IDOSGD.TDTV_REMITOS RE WITH (NOLOCK) , IDOSGD.TDTX_REMITOS_RESUMEN RR  WITH (NOLOCK) ");
        sql.append("where RE.nu_ann = ? ");
        sql.append("AND RE.NU_ANN=RR.NU_ANN ");
        sql.append("AND RE.NU_EMI=RR.NU_EMI ");
        sql.append("AND RE.es_eli = '0' ");
        sql.append("AND RE.es_doc_emi not in ('9','7','5') ");
        sql.append("AND RE.CO_GRU = '2' ");
        sql.append("AND RE.co_tip_doc_adm = ? ");
        if (pnuDoc != null && pnuDoc.trim().length() > 0) {
            pnuDoc = Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
            sql.append("AND (RE.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("') ");
        }
        sql.append("AND RE.co_emp_emi = ? ");

        sql.append(") E ");

        sql.append(")AS A "); 

        sql.append(" WHERE ROWNUM < 201 ");
        sql.append(" ORDER BY A.fe_emi DESC "); // Validar

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{pannio, pcoDepen, pcoDepen, ptiDoc, pannio, ptiDoc, pcoEmpEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<DocumentoBean> getLstDocRecepcionadoRef(String pcoDepen, String pannio, String ptiDoc, String pnuDoc) {
//        StringBuffer sql = new StringBuffer();
        List<DocumentoBean> list = null;

//        sql.append("SELECT A.* ");
//        sql.append("FROM ( ");        
//
//        sql.append("select E.*, ROW_NUMBER() OVER (ORDER BY E.FE_REC_DOC) AS ROWNUM ");
//        sql.append("from( ");
//        sql.append("select ");
//        sql.append("d.FE_REC_DOC, ");
//        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](r.co_tip_doc_adm), 1, 100) de_tip_doc_adm, ");
//        sql.append("SUBSTRING(rr.nu_doc,1,50) NU_DOC, ");
//        sql.append("CONVERT(VARCHAR(10), r.FE_EMI, 3) FE_EMI_CORTA, ");
//        sql.append("CONVERT(VARCHAR(10), d.FE_REC_DOC, 3) FE_REC_DOC_CORTA, ");
//        sql.append("r.nu_ann, ");
//        sql.append("r.nu_emi, ");
//        sql.append("d.nu_des, ");
//        sql.append("REPLACE(LTRIM(RTRIM(r.DE_ASU)), CHAR(10),' ') DE_ASU, ");
//        sql.append("r.co_tip_doc_adm, ");
//        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](d.co_dep_des), 1, 200) de_dep_des, ");
//        sql.append("SUBSTRING(rr.nu_expediente,1,20) NU_EXPEDIENTE, ");
//        sql.append("r.NU_ANN_EXP, ");
//        sql.append("r.NU_SEC_EXP ");
//        sql.append("from IDOSGD.TDTV_REMITOS R   WITH (NOLOCK)  ");
//        sql.append("INNER JOIN	 IDOSGD.TDTV_DESTINOS D  WITH (NOLOCK) ON d.nu_ann = r.nu_ann AND d.nu_emi = r.nu_emi ");
//        sql.append("INNER JOIN	 IDOSGD.TDTX_REMITOS_RESUMEN RR  WITH (NOLOCK) ON rr.nu_ann=r.nu_ann AND rr.nu_emi = r.nu_emi ");
//        sql.append("where ");
//        sql.append("r.es_eli = '0' ");
//        sql.append("AND d.es_eli = '0' ");
//        sql.append("AND r.nu_ann = ? ");      
//        sql.append("AND r.es_doc_emi NOT IN ('5', '9', '7') ");
//        sql.append("and d.es_doc_rec <> '0' ");  
//        sql.append("and d.co_dep_des in "); 
//        sql.append("(select ? ");  
//        sql.append("union "); 
//        sql.append("  SELECT co_dep_ref ");
//        sql.append("    FROM IDOSGD.tdtx_referencia ");
//        sql.append("   WHERE co_dep_emi = ? ");
//        sql.append("     AND ti_emi = 'D' ");
//        sql.append("     AND es_ref = '1') ");
//        sql.append("AND r.co_tip_doc_adm = ? ");
//        if (pnuDoc != null && pnuDoc.trim().length() > 0){
//            pnuDoc = Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
//            sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("') ");
//        }
//        
//        sql.append("UNION \n" );
//        sql.append("select ");
//        sql.append("d.FE_REC_DOC, ");
//        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](r.co_tip_doc_adm), 1, 100) de_tip_doc_adm, ");
//        sql.append("SUBSTRING(rr.nu_doc,1,50) NU_DOC, ");
//        sql.append("CONVERT(VARCHAR(10), r.FE_EMI, 3) FE_EMI_CORTA, ");
//        sql.append("CONVERT(VARCHAR(10), d.FE_REC_DOC, 3) FE_REC_DOC_CORTA, ");
//        sql.append("r.nu_ann, ");
//        sql.append("r.nu_emi, ");
//        sql.append("d.nu_des, ");
//        sql.append("REPLACE(LTRIM(RTRIM(r.DE_ASU)), CHAR(10),' ') DE_ASU, ");
//        sql.append("r.co_tip_doc_adm, ");
//        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](d.co_dep_des), 1, 200) de_dep_des, ");
//        sql.append("SUBSTRING(rr.nu_expediente,1,20) NU_EXPEDIENTE, ");
//        sql.append("r.NU_ANN_EXP, ");
//        sql.append("r.NU_SEC_EXP ");
//        sql.append("from IDOSGD.TDTV_REMITOS R   ");
//        sql.append("INNER JOIN	 IDOSGD.TDTV_DESTINOS D ON d.nu_ann = r.nu_ann AND d.nu_emi = r.nu_emi ");
//        sql.append("INNER JOIN	 IDOSGD.TDTX_REMITOS_RESUMEN RR ON rr.nu_ann=r.nu_ann AND rr.nu_emi = r.nu_emi ");
//        sql.append("where ");
//        sql.append("r.es_eli = '0' ");
//        sql.append("AND d.es_eli = '0' ");
//        sql.append("AND r.nu_ann = ? ");      
//        sql.append("AND r.es_doc_emi NOT IN ('5', '9', '7') ");
//        sql.append("and d.es_doc_rec <> '0' ");  
//        sql.append("and (R.nu_ann+R.nu_emi) in (  ");
//        
//         sql.append("select  (REF.nu_ann_ref+REF.nu_emi_ref) as nurem from IDOSGD.TDTR_REFERENCIA REF where (REF.nu_ann+REF.nu_emi) in \n" +
//                    "(select (R.nu_ann+R.nu_emi) as nurem \n" +
//                    "from IDOSGD.TDTV_REMITOS R WITH (NOLOCK)   \n" +
//                    "INNER JOIN IDOSGD.TDTV_DESTINOS D  WITH (NOLOCK) ON d.nu_ann = r.nu_ann AND d.nu_emi = r.nu_emi \n" +
//                    "INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK)  ON rr.nu_ann=r.nu_ann AND rr.nu_emi = r.nu_emi \n" +
//                    "where\n" +
//                    "r.es_eli = '0'\n" +
//                    "AND d.es_eli = '0'\n" +
//                    "AND r.nu_ann = ?\n" +                    
//                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
//                    "and d.es_doc_rec <> '0'  \n" +
//                    "and d.co_dep_des = ? \n");
//         
                    String pexpediente="";
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        //sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("%')\n");
                        pexpediente=pnuDoc;
                    }
//        
//        sql.append(")) ) E ");
//
//        sql.append(") A "); 
//        sql.append("WHERE ROWNUM < 202 ");
//        sql.append("order by FE_REC_DOC desc ");

//        try {
//            System.out.println("SQLREFERENCIA==>" + sql.toString());
//            System.out.println("pannio==>" + pannio);
//            System.out.println("pcoDepen==>" + pcoDepen);
//            System.out.println("ptiDoc==>" + ptiDoc); 
//            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
//                    new Object[]{pannio, pcoDepen, pcoDepen, ptiDoc, pannio, pannio,pcoDepen});
//        } catch (EmptyResultDataAccessException e) {
//            list = null;
//        } catch (Exception e) {
//            list = null;
//            e.printStackTrace();
//        }
              this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("IDOSGD.UPS_SGD_EMIDOCUMENTODAO_DOCRECEPCIONREF_LIST")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("anio","pcodepen","ptiDoc","pexpediente")                        
                .declareParameters(
                        new SqlParameter("anio", Types.VARCHAR), 
                        new SqlParameter("pcodepen", Types.VARCHAR),
                        new SqlParameter("ptiDoc", Types.VARCHAR),
                        new SqlParameter("pexpediente", Types.VARCHAR))
                       .returningResultSet("recordSets", BeanPropertyRowMapper.newInstance(DocumentoBean.class));;
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("anio", pannio) 
                .addValue("pcodepen", pcoDepen)
                .addValue("ptiDoc", ptiDoc)
                .addValue("pexpediente", pexpediente);  
        try {     
            System.out.println("SQLREFERENCIA==>INICIANDO");
            Map<String, Object> dbResults = this.spdataSource.execute(in);
            list = (List<DocumentoBean>)dbResults.get("recordSets");
            System.out.println("SQLREFERENCIA==>TERMINADO");
            this.spdataSource =null;            
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        
        return list;
    }

    //@Override
    public List<DestinatarioDocumentoEmiBean> _getLstDestintariotlbEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();
        sql.append("SSELECT a.nu_ann, ");
        sql.append("Sa.nu_emi, ");
        sql.append("Sa.nu_des, ");
        sql.append("Sa.co_loc_des co_local, ");
        sql.append("SSUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](a.co_loc_des), 1, 100) de_local, ");
        sql.append("Sa.co_dep_des co_dependencia, ");
        sql.append("SSUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_des), 1, 100) de_dependencia, ");
        sql.append("Sa.co_emp_des co_empleado, ");
        sql.append("SSUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](a.co_emp_des), 1, 100) de_empleado, ");
        sql.append("Sa.co_mot co_tramite, ");
        sql.append("SSUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_MOTIVO](a.co_mot), 1, 100) de_tramite, ");
        sql.append("Sa.co_pri co_prioridad, ");
        sql.append("Sa.de_pro de_indicaciones ");
        sql.append("SFROM IDOSGD.tdtv_destinos a  WITH (NOLOCK) ");
        sql.append("SWHERE a.nu_ann = ? "); 
        sql.append("SAND a.nu_emi = ? ");
        sql.append("SAND a.TI_DES = '01' "); 
        sql.append("SAND a.ES_ELI='0' "); 
        sql.append("SAND a.NU_EMI_REF is null ");
        sql.append("SORDER BY 3 ");

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
    public List<DestinatarioDocumentoEmiBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();
         sql.append("select a.nu_ann, ");
        sql.append("a.nu_emi, ");
        sql.append("a.nu_des, ");
        sql.append("a.co_loc_des co_local, ");
        sql.append("CASE "); 
        sql.append("WHEN a.co_loc_des IS NULL THEN NULL ");
        sql.append("ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](a.co_loc_des), 1, 100) ");
        sql.append("END de_local, ");
        sql.append("a.co_dep_des co_dependencia, ");
        sql.append("CASE "); 
        sql.append("WHEN a.co_dep_des IS NULL THEN NULL ");
        sql.append("ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_des), 1, 100) ");
        sql.append("END de_dependencia, ");
        sql.append("a.co_emp_des co_empleado, ");
        sql.append("CASE "); 
        sql.append("WHEN a.co_emp_des IS NULL THEN NULL ");
        sql.append("ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](a.co_emp_des), 1, 100) ");
        sql.append("END de_empleado, ");
        sql.append("a.co_mot co_tramite, ");
        sql.append("CASE "); 
        sql.append("WHEN a.co_mot IS NULL THEN NULL ");
        sql.append("ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_MOTIVO](a.co_mot), 1, 100) ");
        sql.append("END de_tramite, ");
        sql.append("a.co_pri co_prioridad, ");
        sql.append("a.de_pro de_indicaciones, ");
        sql.append("a.NU_RUC_DES nu_ruc, ");
        sql.append("CASE "); 
        sql.append("WHEN a.NU_RUC_DES IS NULL THEN NULL ");
        sql.append("ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_PROVEEDOR](a.NU_RUC_DES), 1, 100) ");
        sql.append("END de_proveedor, ");
        sql.append("a.NU_DNI_DES nu_dni, ");
        sql.append("CASE "); 
        sql.append("WHEN a.NU_DNI_DES IS NULL THEN NULL ");
        sql.append("ELSE SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](a.NU_DNI_DES), 1, 100) ");
        sql.append("END de_ciudadano, ");
        sql.append("a.CO_OTR_ORI_DES co_otro_origen, ");
        sql.append("CASE "); 
        sql.append("WHEN a.CO_OTR_ORI_DES IS NULL THEN NULL ");
        sql.append("ELSE (SELECT ISNULL(C.DE_APE_PAT_OTR,'') + ' ' + ISNULL(C.DE_APE_MAT_OTR,'') + ', ' + ISNULL(C.DE_NOM_OTR,'') + ' - ' + ");
        sql.append("			 ISNULL(C.DE_RAZ_SOC_OTR,'') + '##' + ISNULL(B.CELE_DESELE,' ') + '##' + ");
        sql.append("			 ISNULL(C.NU_DOC_OTR_ORI,'') ");  
        sql.append("	  FROM IDOSGD.TDTR_OTRO_ORIGEN C ");
        sql.append("		   LEFT OUTER JOIN ( ");
        sql.append("			   SELECT CELE_CODELE, CELE_DESELE ");
        sql.append("			   FROM IDOSGD.SI_ELEMENTO ");
        sql.append("			   WHERE CTAB_CODTAB = 'TIP_DOC_IDENT') B "); 
        sql.append("		   ON C.CO_TIP_OTR_ORI = B.CELE_CODELE ");
        sql.append("	  WHERE C.CO_OTR_ORI = a.CO_OTR_ORI_DES ");
        sql.append("	 ) ");
        sql.append("END de_otro_origen_full, ");
        sql.append("a.ti_des co_tipo_destino, ");
        sql.append("ISNULL(a.ES_ENV_POR_TRA, '0') ENV_MESA_PARTES, ");
        sql.append("CDIR_REMITE,CEXP_CORREOE,CTELEFONO,CCOD_DPTO,CCOD_PROV,CCOD_DIST,\n" +
                        "                rtrim(ltrim(UB.NODEP))+' '+rtrim(ltrim(UB.NOPRV))+' '+rtrim(ltrim(UB.NODIS)) ubigeo \n" +
                        "                ,A.REMI_TI_EMI , CASE A.REMI_TI_EMI WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (A.REMI_NU_DNI_EMI)  \n" +                      
                        "                 WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (A.REMI_CO_OTR_ORI_EMI) END   nombres,\n" +
                        "               CASE A.REMI_TI_EMI WHEN '03' THEN REMI_NU_DNI_EMI WHEN '04' THEN REMI_CO_OTR_ORI_EMI END   REMI_NU_DNI_EMI  \n" +                        
                        "                   ,DE_CARGO AS cargo	  ");
        sql.append("FROM IDOSGD.tdtv_destinos a WITH (NOLOCK)  ");
        sql.append("LEFT JOIN ( SELECT C.CO_OTR_ORI,ISNULL(C.DE_APE_PAT_OTR,'')+' '+ISNULL(C.DE_APE_MAT_OTR,'')+', '+ISNULL(C.DE_NOM_OTR,'') + ' - ' +\n" +
                        "                     ISNULL(C.DE_RAZ_SOC_OTR,'') +'##'+  ISNULL(B.CELE_DESELE,'   ') +'##'+  ISNULL(C.NU_DOC_OTR_ORI,'')  de_otro_origen_full\n" +
                        "                  FROM IDOSGD.TDTR_OTRO_ORIGEN C  WITH (NOLOCK) LEFT JOIN  (\n" +
                        "                  SELECT CELE_CODELE, CELE_DESELE  FROM IDOSGD.SI_ELEMENTO  WITH (NOLOCK)  WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B\n" +
                        "                  ON C.CO_TIP_OTR_ORI = B.CELE_CODELE ) OTRDES ON OTRDES.CO_OTR_ORI=a.CO_OTR_ORI_DES \n  ");
        
        sql.append("LEFT JOIN IDOSGD.IDTUBIAS UB WITH (NOLOCK)  ON UB.UBDEP=a.CCOD_DPTO AND UB.UBPRV=a.CCOD_PROV AND UB.UBDIS=a.CCOD_DIST  ");
        sql.append("where nu_ann = ? "); 
        sql.append("and nu_emi = ? ");
        sql.append("AND ES_ELI='0' "); 
        sql.append("AND NU_EMI_REF is null ");
        sql.append("order by 3 ");

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
    public List<ReferenciaBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();
        sql.append("SELECT "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](a.co_tip_doc_adm) de_tipo_doc, "); 
        sql.append("CASE a.ti_emi "); 
        sql.append("	 WHEN '01' THEN a.nu_doc_emi + '-' + a.nu_ann + '-' + a.de_doc_sig ");
        sql.append("	 WHEN '05' THEN a.nu_doc_emi + '-' + a.nu_ann + '-' + a.de_doc_sig ");
        sql.append("	 ELSE a.de_doc_sig ");
        sql.append("END li_nu_doc, ");
        sql.append("SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_emi), 1, 100) de_dep_emi, "); 
        sql.append("CONVERT(VARCHAR(10), a.fe_emi, 3) fe_emi_corta, "); 
        sql.append("b.nu_ann, ");
        sql.append("b.nu_emi, ");
        sql.append("ISNULL(LTRIM(RTRIM(CAST(b.nu_des AS varchar))),'N') nu_des, ");
        sql.append("b.nu_ann_ref, ");
        sql.append("b.nu_emi_ref, ");
        sql.append("ISNULL(LTRIM(RTRIM(CAST(b.nu_des_ref AS varchar))),'N') nu_des_ref, ");
        sql.append("b.co_ref, ");
        sql.append("CASE ISNULL(LTRIM(RTRIM(CAST(b.nu_des_ref AS varchar))),'N') ");
        sql.append("	 WHEN 'N' THEN 'emi' ");
        sql.append("	 ELSE 'rec' ");
        sql.append("END tip_doc_ref, ");
        sql.append("a.co_tip_doc_adm, ");
        sql.append("'BD' accion_bd ");
        sql.append("FROM IDOSGD.tdtv_remitos a WITH (NOLOCK) , ");
        sql.append("	 IDOSGD.TDTR_REFERENCIA b WITH (NOLOCK)  "); 
        sql.append("WHERE a.nu_ann = b.nu_ann_ref ");
        sql.append("AND a.nu_emi = b.nu_emi_ref ");
        sql.append("AND b.nu_ann = ? ");
        sql.append("AND b.nu_emi = ? ");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class),
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
    public String getTipoDestinatarioEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        String result = null;
        sql.append("SELECT MAX(ti_des) ");
        sql.append("FROM IDOSGD.tdtv_destinos  WITH (NOLOCK) ");
        sql.append("WHERE nu_emi = ? ");
        sql.append("AND nu_ann = ? ");
        sql.append("AND ES_ELI = '0' ");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuEmi, pnuAnn});
        } catch (Exception e) {
            result = "1";
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String updDocumentoEmiBean(String nuAnn, String nuEmi, DocumentoEmiBean documentoEmiBean, ExpedienteBean expedienteBean, RemitenteEmiBean remitenteEmiBean, String pcoUserMod) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos A \n"
                + "set A.CO_USE_MOD=?,");
        if (documentoEmiBean != null) {
//                    sqlUpd.append("A.DE_ASU='"+ documentoEmiBean.getDeAsu() + "'\n" +
//                    ",A.NU_DIA_ATE='"+ documentoEmiBean.getNuDiaAte() + "'\n" +
//                    ",A.CO_TIP_DOC_ADM='"+ documentoEmiBean.getCoTipDocAdm() + "'\n" +        
//                    ",A.ES_DOC_EMI='"+ documentoEmiBean.getEsDocEmi() + "',\n" +
//                    ",A.NU_DOC_EMI='"+ documentoEmiBean.getNuDocEmi() + "',\n" +
//                    ",A.NU_COR_DOC='"+ documentoEmiBean.getNuCorDoc()+ "',\n");
             sqlUpd.append("A.DE_ASU=?\n"
                    + ",A.NU_DIA_ATE=?\n"
                    + ",A.CO_TIP_DOC_ADM=?\n"
                    + ",A.ES_DOC_EMI=?\n"
                    + ",A.NU_DOC_EMI=?\n"
                    + ",A.DE_DOC_SIG=?\n"
                    + ",A.NU_COR_DOC= CASE \n"
                    + "WHEN ? IS NULL THEN ? "
                    + "ELSE (SELECT ISNULL(MAX(nu_cor_doc), 0) + 1\n"
                    + "FROM IDOSGD.tdtv_remitos\n"
                    + "WHERE nu_ann = ?\n"
                    + "AND co_dep_emi = ?\n"
                    + "AND co_tip_doc_adm = ?\n"
                    + "AND ti_emi = ?) END,\n");
        }
        if (expedienteBean != null) {
            sqlUpd.append("A.NU_SEC_EXP='" + expedienteBean.getNuSecExp() + "',\n");
        }
        if (remitenteEmiBean != null) {
            sqlUpd.append("A.CO_LOC_EMI='" + remitenteEmiBean.getCoLocal() + "'\n"
                    + ",A.CO_DEP_EMI='" + remitenteEmiBean.getCoDependencia() + "'\n"
                    + ",A.CO_EMP_EMI='" + remitenteEmiBean.getCoEmpFirma() + "',\n");
        }
        sqlUpd.append("A.FE_USE_MOD=CURRENT_TIMESTAMP "
                + "where\n"
                + "A.NU_ANN=? and\n"
                + "A.NU_EMI=?");

        try {
            if (documentoEmiBean != null) {
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, documentoEmiBean.getDeAsu(), documentoEmiBean.getNuDiaAte(),
                    documentoEmiBean.getCoTipDocAdm(), documentoEmiBean.getEsDocEmi(), documentoEmiBean.getNuDocEmi(), documentoEmiBean.getDeDocSig(),
                    documentoEmiBean.getNuDocEmi(), documentoEmiBean.getNuCorDoc(), nuAnn, documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm(),
                    documentoEmiBean.getTiEmi(), nuAnn, nuEmi});
            } else {
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, nuAnn, nuEmi});
            }
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public String insDocumentoEmiBean(DocumentoEmiBean documentoEmiBean, ExpedienteBean expedienteBean, RemitenteEmiBean remitenteEmiBean) {
         String vReturn = "NO_OK";
        documentoEmiBean.setTiEmi("01");
        StringBuffer sqlUpd = new StringBuffer();
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT ISNULL(MAX(nu_cor_emi), 0) + 1 ");
        sqlQry.append("FROM IDOSGD.tdtv_remitos ");
        sqlQry.append("WHERE nu_ann = ? ");
        sqlQry.append("AND co_dep_emi = ? ");
        sqlQry.append("AND ti_emi = '01' ");

        sqlUpd.append("INSERT INTO IDOSGD.tdtv_remitos(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + "NU_COR_EMI,\n"
                + "CO_LOC_EMI,\n"
                + "CO_DEP_EMI,\n"
                + "CO_EMP_EMI,\n"
                + "CO_EMP_RES, \n"
                + "TI_EMI,\n"
                + "FE_EMI,\n"
                + "CO_GRU,\n"
                + "DE_ASU, \n"
                + "ES_DOC_EMI, \n"
                + "NU_DIA_ATE, \n"
                + "TI_CAP, \n"
                + "CO_TIP_DOC_ADM, \n"
                + "NU_COR_DOC, \n"
                + "DE_DOC_SIG, \n"
                + "NU_ANN_EXP, \n"
                + "NU_SEC_EXP, \n"
                + "NU_DET_EXP, \n"
                + "ES_ELI,\n"
                + "NU_DOC_EMI,\n"
                + "CO_USE_CRE,\n"
                + "FE_USE_CRE,\n"
                + "CO_USE_MOD,\n"
                + "FE_USE_MOD,OBS_DOC,COD_VER_EXT)\n"
                + "VALUES(?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,'1',?,?,?,'03',?,\n"
                + "?,?,?,?,1,\n"
                + "'0',?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?)");
        
        try {
            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_UTILS_SEC_REMITOS_NU_EMI]")
                                            .withoutProcedureColumnMetaDataAccess()
                                            .declareParameters(new SqlOutParameter("COD_NU_EMI", Types.INTEGER));
                    
            Map out = simpleJdbcCall.execute();
            int codigo = ((Integer)out.get("COD_NU_EMI"));
            String snuEmi = String.format("%010d", codigo);
    
            // String snuEmi = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);
            
            String snuCorEmi = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getCoDepEmi()});
            documentoEmiBean.setNuEmi(snuEmi);
            documentoEmiBean.setNuCorEmi(snuCorEmi);
            //documentoEmiBean.setNuCorDoc(vnuCorDoc);
            documentoEmiBean.setCoVerExt(AleatorioCodVerificacion.generaCodVerificacion());
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getNuAnn(), snuEmi,snuCorEmi==null?null:Integer.parseInt(snuCorEmi),
                documentoEmiBean.getCoLocEmi(), documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoEmpEmi(), documentoEmiBean.getCoEmpRes(),
                documentoEmiBean.getTiEmi(), documentoEmiBean.getDeAsu(), documentoEmiBean.getEsDocEmi(),documentoEmiBean.getNuDiaAte()==null?null:Integer.parseInt(documentoEmiBean.getNuDiaAte()),documentoEmiBean.getCoTipDocAdm(),
                documentoEmiBean.getNuCorDoc()==null?null:Integer.parseInt(documentoEmiBean.getNuCorDoc()), documentoEmiBean.getDeDocSig(),documentoEmiBean.getNuAnnExp(), documentoEmiBean.getNuSecExp(), 
                documentoEmiBean.getNuDocEmi()==null?null:Integer.parseInt(documentoEmiBean.getNuDocEmi()),documentoEmiBean.getCoUseMod(), documentoEmiBean.getCoUseMod(),documentoEmiBean.getDeObsDoc(), documentoEmiBean.getCoVerExt()});

            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Numero de Documento Duplicado.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public String insDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        StringBuffer sqlQry1 = new StringBuffer(); 
        StringBuffer sqlUpdUbigeo = new StringBuffer();
        sqlQry1.append("SELECT CELE_CODELE FROM IDOSGD.SI_ELEMENTO WHERE CTAB_CODTAB='UPD_PARAMT_UBIGEO'");
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
                + "CO_OTR_ORI_DES, \n"
                + "NU_DNI_DES, \n"
                + "NU_RUC_DES, \n"
                + "ES_ELI,\n"
                + "FE_USE_CRE,\n"
                + "FE_USE_MOD,\n"
                + "CO_USE_MOD,\n"
                + "CO_USE_CRE,\n"
                + "ES_DOC_REC,\n"
                + "ES_ENV_POR_TRA,CDIR_REMITE,CEXP_CORREOE,CTELEFONO,CCOD_DPTO,CCOD_PROV,CCOD_DIST,REMI_TI_EMI,REMI_NU_DNI_EMI,REMI_CO_OTR_ORI_EMI,DE_CARGO)\n"
                + "VALUES(?,?,(select ISNULL(MAX(a.nu_des) + 1, 1) FROM IDOSGD.tdtv_destinos a  WITH (NOLOCK) where \n"
                + "A.NU_ANN=? and\n"
                + "A.NU_EMI=?),?,?,?,?,?,?,?,?,?,?,'0',CURRENT_TIMESTAMP,CURRENT_TIMESTAMP,?,?,'0',?,?,?,?,?,?,?,?,?,?,?)");

        try {
            String []ubigeo = null;
            String dep="";
            String pro="";
            String dis="";
            if(destinatarioDocumentoEmiBean.getCcodDpto()!= null && !"".equals(destinatarioDocumentoEmiBean.getCcodDpto())){
            ubigeo=destinatarioDocumentoEmiBean.getCcodDpto().split(",");
            }
            if(ubigeo != null && ubigeo.length>0){
            dep=ubigeo[0];pro=ubigeo[1];dis=ubigeo[2];
            }
            if("03".equals(destinatarioDocumentoEmiBean.getRemiTiEmi())){            
                destinatarioDocumentoEmiBean.setRemiCoOtrOriEmi("");
            }
            if("04".equals(destinatarioDocumentoEmiBean.getRemiTiEmi())){
                destinatarioDocumentoEmiBean.setRemiCoOtrOriEmi(destinatarioDocumentoEmiBean.getRemiNuDniEmi());
                destinatarioDocumentoEmiBean.setRemiNuDniEmi("");
            }
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn, nuEmi, nuAnn, nuEmi, destinatarioDocumentoEmiBean.getCoLocal(), destinatarioDocumentoEmiBean.getCoDependencia(), destinatarioDocumentoEmiBean.getCoTipoDestino(),
                destinatarioDocumentoEmiBean.getCoEmpleado(), destinatarioDocumentoEmiBean.getCoPrioridad(), destinatarioDocumentoEmiBean.getDeIndicaciones(), destinatarioDocumentoEmiBean.getCoTramite(),
                destinatarioDocumentoEmiBean.getCoOtroOrigen(), destinatarioDocumentoEmiBean.getNuDni(), destinatarioDocumentoEmiBean.getNuRuc(),
                destinatarioDocumentoEmiBean.getCoUseCre(), destinatarioDocumentoEmiBean.getCoUseCre(),destinatarioDocumentoEmiBean.getEnvMesaPartes(),
            destinatarioDocumentoEmiBean.getCdirRemite(),destinatarioDocumentoEmiBean.getCexpCorreoe(),destinatarioDocumentoEmiBean.getcTelefono(),
               dep,pro,dis,destinatarioDocumentoEmiBean.getRemiTiEmi(),destinatarioDocumentoEmiBean.getRemiNuDniEmi(),destinatarioDocumentoEmiBean.getRemiCoOtrOriEmi(),destinatarioDocumentoEmiBean.getCargo()});
            
             String isUpdateUbigeo = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class);
            if("1".equals(isUpdateUbigeo)){            
                if("02".equals(destinatarioDocumentoEmiBean.getCoTipoDestino())){   
                    sqlUpdUbigeo.append("UPDATE  IDOSGD.lg_pro_proveedor SET cpro_domicil=?,cpro_ubigeo=?,cpro_telefo=?,cpro_email=?,cubi_coddep=?,\n" +
                                        "    cubi_codpro=?,cubi_coddis=?  WHERE cpro_ruc=?");
                    this.jdbcTemplate.update(sqlUpdUbigeo.toString(), new Object[]{destinatarioDocumentoEmiBean.getCdirRemite(),dep+pro+dis,
                        destinatarioDocumentoEmiBean.getcTelefono(),destinatarioDocumentoEmiBean.getCexpCorreoe(),dep,pro,dis,destinatarioDocumentoEmiBean.getNuRuc()});
                }
                if("03".equals(destinatarioDocumentoEmiBean.getCoTipoDestino())){   
                    sqlUpdUbigeo.append(" UPDATE  IDOSGD.TDTX_ANI_SIMIL  SET  DEDOMICIL=?,DETELEFO=?, DEEMAIL=?,UBDEP=?,UBPRV=?, UBDIS=?  WHERE  NULEM=? ");
                    this.jdbcTemplate.update(sqlUpdUbigeo.toString(), new Object[]{destinatarioDocumentoEmiBean.getCdirRemite(),
                        destinatarioDocumentoEmiBean.getcTelefono(),destinatarioDocumentoEmiBean.getCexpCorreoe(),dep,pro,dis,destinatarioDocumentoEmiBean.getNuDni()});
                }
                if("04".equals(destinatarioDocumentoEmiBean.getCoTipoDestino())){   
                    sqlUpdUbigeo.append("UPDATE IDOSGD.TDTR_OTRO_ORIGEN SET  DE_DIR_OTRO_ORI=?,DE_TELEFO=?,DE_EMAIL=?,UB_DEP=?, UB_PRO=?, UB_DIS=?   WHERE  CO_OTR_ORI=?    ");
                     this.jdbcTemplate.update(sqlUpdUbigeo.toString(), new Object[]{destinatarioDocumentoEmiBean.getCdirRemite(),
                        destinatarioDocumentoEmiBean.getcTelefono(),destinatarioDocumentoEmiBean.getCexpCorreoe(),dep,pro,dis,destinatarioDocumentoEmiBean.getCoOtroOrigen()});
                }
            }
            
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado Destino.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public String updDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
         String vReturn = "NO_OK";
//        StringBuffer sqlUpd = new StringBuffer();
//
//        sqlUpd.append("update IDOSGD.TDTV_DESTINOS \n"
//                + "set CO_LOC_DES=?\n"
//                + ",CO_DEP_DES=?\n"
//                + ",CO_EMP_DES=?\n"
//                + ",CO_PRI=?\n"
//                + ",DE_PRO=?\n"
//                + ",CO_MOT=?\n"
//                + ",CO_OTR_ORI_DES=? \n"
//                + ",NU_DNI_DES=? \n"
//                + ",NU_RUC_DES=? \n"
//                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
//                + ",CO_USE_MOD=?\n"
//                + ",ES_ENV_POR_TRA=?\n"
//                + ",CDIR_REMITE=?\n"
//                + ",CEXP_CORREOE=?\n"
//                + ",CTELEFONO=?\n"
//                + ",CCOD_DPTO=?\n"
//                + ",CCOD_PROV=?\n"
//                + ",CCOD_DIST=?\n" 
//                + ",REMI_TI_EMI=?\n"
//                + ",REMI_NU_DNI_EMI=?\n"
//                + ",REMI_CO_OTR_ORI_EMI=? \n"
//                + ",DE_CARGO=? \n"
//                + "where\n"
//                + "NU_ANN=? and\n"
//                + "NU_EMI=? and\n"
//                + "NU_DES=?");
        try {
            String []ubigeo = null;
            String dep="";
            String pro="";
            String dis="";
            if(destinatarioDocumentoEmiBean.getCcodDpto()!= null && !"".equals(destinatarioDocumentoEmiBean.getCcodDpto())){
            ubigeo=destinatarioDocumentoEmiBean.getCcodDpto().split(",");
            }
            if(ubigeo != null && ubigeo.length>0){
            dep=ubigeo[0];pro=ubigeo[1];dis=ubigeo[2];
            }
            if("03".equals(destinatarioDocumentoEmiBean.getRemiTiEmi())){            
                destinatarioDocumentoEmiBean.setRemiCoOtrOriEmi("");
            }
            if("04".equals(destinatarioDocumentoEmiBean.getRemiTiEmi())){
                destinatarioDocumentoEmiBean.setRemiCoOtrOriEmi(destinatarioDocumentoEmiBean.getRemiNuDniEmi());
                destinatarioDocumentoEmiBean.setRemiNuDniEmi("");
            }
//            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{destinatarioDocumentoEmiBean.getCoLocal(), destinatarioDocumentoEmiBean.getCoDependencia(),
//                destinatarioDocumentoEmiBean.getCoEmpleado(), destinatarioDocumentoEmiBean.getCoPrioridad(), destinatarioDocumentoEmiBean.getDeIndicaciones(), destinatarioDocumentoEmiBean.getCoTramite(),
//                destinatarioDocumentoEmiBean.getCoOtroOrigen(), destinatarioDocumentoEmiBean.getNuDni(), destinatarioDocumentoEmiBean.getNuRuc(),
//                destinatarioDocumentoEmiBean.getCoUseCre(), destinatarioDocumentoEmiBean.getEnvMesaPartes(), 
//                destinatarioDocumentoEmiBean.getCdirRemite(),destinatarioDocumentoEmiBean.getCexpCorreoe(),destinatarioDocumentoEmiBean.getcTelefono(),
//               dep,pro,dis,destinatarioDocumentoEmiBean.getRemiTiEmi(),destinatarioDocumentoEmiBean.getRemiNuDniEmi(),destinatarioDocumentoEmiBean.getRemiCoOtrOriEmi(),destinatarioDocumentoEmiBean.getCargo(),
//                nuAnn, nuEmi, destinatarioDocumentoEmiBean.getNuDes()});
            this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_DESTINATARIO_DOCEMI_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi", "p_nudesc","P_CO_LOC_DES","P_CO_DEP_DES","P_CO_EMP_DES","P_CO_PRI","P_DE_PRO","P_CO_MOT",
                        "P_CO_OTR_ORI_DES","P_NU_DNI_DES","P_NU_RUC_DES","P_CO_USE_MOD"
                        ,"P_ES_ENV_POR_TRA","P_CDIR_REMITE","P_CEXP_CORREOE","P_CTELEFONO","P_CCOD_DPTO","P_CCOD_PROV","P_CCOD_DIST","P_REMI_TI_EMI"
                        ,"P_REMI_NU_DNI_EMI","P_REMI_CO_OTR_ORI_EMI","P_DE_CARGO")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR),
                        new SqlParameter("p_nudesc", Types.VARCHAR),
                        new SqlParameter("P_CO_LOC_DES", Types.VARCHAR), 
                        new SqlParameter("P_CO_DEP_DES", Types.VARCHAR),
                        new SqlParameter("P_CO_EMP_DES", Types.VARCHAR),
                        new SqlParameter("P_CO_PRI", Types.VARCHAR),
                        new SqlParameter("P_DE_PRO", Types.VARCHAR),
                        new SqlParameter("P_CO_MOT", Types.VARCHAR),
                        new SqlParameter("P_CO_OTR_ORI_DES", Types.VARCHAR),
                        new SqlParameter("P_NU_DNI_DES", Types.VARCHAR),
                        new SqlParameter("P_NU_RUC_DES", Types.VARCHAR),
                        new SqlParameter("P_CO_USE_MOD", Types.VARCHAR),
                        new SqlParameter("P_ES_ENV_POR_TRA", Types.VARCHAR),
                        new SqlParameter("P_CDIR_REMITE", Types.VARCHAR),
                        new SqlParameter("P_CEXP_CORREOE", Types.VARCHAR),
                        new SqlParameter("P_CTELEFONO", Types.VARCHAR),
                        new SqlParameter("P_CCOD_DPTO", Types.VARCHAR),
                        new SqlParameter("P_CCOD_PROV", Types.VARCHAR),
                        new SqlParameter("P_CCOD_DIST", Types.VARCHAR),
                        new SqlParameter("P_REMI_TI_EMI", Types.VARCHAR),
                        new SqlParameter("P_REMI_NU_DNI_EMI", Types.VARCHAR),
                        new SqlParameter("P_REMI_CO_OTR_ORI_EMI", Types.VARCHAR),
                        new SqlParameter("P_DE_CARGO", Types.VARCHAR)
                );
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", nuAnn)
                .addValue("p_nuEmi", nuEmi)
                .addValue("p_nudesc", destinatarioDocumentoEmiBean.getNuDes())
                .addValue("P_CO_LOC_DES",  destinatarioDocumentoEmiBean.getCoLocal())
                .addValue("P_CO_DEP_DES",  destinatarioDocumentoEmiBean.getCoDependencia())
                .addValue("P_CO_EMP_DES",  destinatarioDocumentoEmiBean.getCoEmpleado())
                .addValue("P_CO_PRI",  destinatarioDocumentoEmiBean.getCoPrioridad())
                .addValue("P_DE_PRO",  destinatarioDocumentoEmiBean.getDeIndicaciones())
                .addValue("P_CO_MOT",  destinatarioDocumentoEmiBean.getCoTramite())
                .addValue("P_CO_OTR_ORI_DES",  destinatarioDocumentoEmiBean.getCoOtroOrigen())
                .addValue("P_NU_DNI_DES",  destinatarioDocumentoEmiBean.getNuDni())
                .addValue("P_NU_RUC_DES",  destinatarioDocumentoEmiBean.getNuRuc())
                .addValue("P_CO_USE_MOD",  destinatarioDocumentoEmiBean.getCoUseCre())
                .addValue("P_ES_ENV_POR_TRA",  destinatarioDocumentoEmiBean.getEnvMesaPartes())
                .addValue("P_CDIR_REMITE",  destinatarioDocumentoEmiBean.getCdirRemite())
                .addValue("P_CEXP_CORREOE",  destinatarioDocumentoEmiBean.getCexpCorreoe())
                .addValue("P_CTELEFONO",  destinatarioDocumentoEmiBean.getcTelefono())
                .addValue("P_CCOD_DPTO",  dep)
                .addValue("P_CCOD_PROV",  pro)
                .addValue("P_CCOD_DIST",  dis)
                .addValue("P_REMI_TI_EMI",  destinatarioDocumentoEmiBean.getRemiTiEmi())
                .addValue("P_REMI_NU_DNI_EMI",  destinatarioDocumentoEmiBean.getRemiNuDniEmi())
                .addValue("P_REMI_CO_OTR_ORI_EMI",  destinatarioDocumentoEmiBean.getRemiCoOtrOriEmi())
                .addValue("P_DE_CARGO",  destinatarioDocumentoEmiBean.getCargo());         
            System.out.println("PK_SGD_TRAMITE_PROCE_DESTINATARIO_DOCEMI_UDP==>" +  nuEmi);         
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
            
                
            vReturn = "OK"; 
        } catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado Destino.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public String delDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("delete from IDOSGD.TDTV_DESTINOS\n"
                + "WHERE NU_ANN = ?\n"
                + "AND NU_EMI = ?\n"
                + "and NU_DES=?");
//        sqlUpd.append("update TDTV_DESTINOS \n"
//                + "set ES_ELI='1'\n"
//                + ",FE_USE_CRE=CURRENT_TIMESTAMP\n"
//                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
//                + ",CO_USE_MOD='ADM'\n"
//                + ",CO_USE_CRE='ADM'\n"
//                + "where\n"
//                + "NU_ANN=? and\n"
//                + "NU_EMI=? and\n"
//                + "NU_DES=?");
        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{nuAnn, nuEmi, destinatarioDocumentoEmiBean.getNuDes()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

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
        sqlUpd.append("VALUES(?, ?, ?, ?, ?, cast(? as numeric(10,0)), ?, CURRENT_TIMESTAMP) ");

        try {
            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_UTILS_SEC_REFERENCIA_CO_REF]")
                                            .withoutProcedureColumnMetaDataAccess()
                                            .declareParameters(new SqlOutParameter("COD_NU_ANN_REF", Types.INTEGER));
                    
            Map out = simpleJdbcCall.execute();
            int codigo = ((Integer)out.get("COD_NU_ANN_REF"));
            String cod_num_ann_ref = String.format("%010d", codigo);            
            
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn, nuEmi, cod_num_ann_ref, referenciaEmiDocBean.getNuAnn(), referenciaEmiDocBean.getNuEmi(),
                referenciaEmiDocBean.getNuDes().equals("")?null:referenciaEmiDocBean.getNuDes(), referenciaEmiDocBean.getCoUseCre()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public String updReferenciaDocumentoEmi(String nuAnn, String nuEmi, ReferenciaEmiDocBean referenciaEmiDocBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtr_referencia \n"
                + "set NU_ANN_REF = ?,\n"
                + "NU_EMI_REF = ?,\n"
                + "NU_DES_REF = ?\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND CO_REF = ?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{referenciaEmiDocBean.getNuAnn(), referenciaEmiDocBean.getNuEmi(),
                referenciaEmiDocBean.getNuDes(), nuAnn, nuEmi, referenciaEmiDocBean.getCoRef()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public String delReferenciaDocumentoEmi(String nuAnn, String nuEmi, ReferenciaEmiDocBean referenciaEmiDocBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlIns = new StringBuffer();
        sqlIns.append("DELETE FROM IDOSGD.tdtr_referencia\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND NU_ANN_REF = ? AND NU_EMI_REF = ?");

        try {
            this.jdbcTemplate.update(sqlIns.toString(), new Object[]{nuAnn, nuEmi, referenciaEmiDocBean.getNuAnn(), referenciaEmiDocBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public DocumentoEmiBean getDocumentoEmiAdmNew(String sEstadoDocEmi, String codDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("select [IDOSGD].[PK_SGD_DESCRIPCION_DE_SIGLA](?) de_doc_sig, ");
        sql.append("CO_EMPLEADO co_emp_emi, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](CO_EMPLEADO) de_emp_emi, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_ESTADOS](?, 'TDTV_REMITOS') de_es_doc_emi, ");
        sql.append("'0' existe_doc, ");
        sql.append("'0' existe_anexo, ");
        sql.append("? co_es_doc_emi "); 
        sql.append("from IDOSGD.rhtm_dependencia ");
        sql.append("where CO_DEPENDENCIA = ? ");

        DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{codDependencia, sEstadoDocEmi, sEstadoDocEmi, codDependencia});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiBean;
    }

    @Override
    public String insExpedienteBean(ExpedienteBean expedienteBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlQry1 = new StringBuffer();
        sqlQry1.append("select RIGHT(REPLICATE('0', 7) + rtrim(ltrim(CAST((ISNULL(max(NU_CORR_EXP), 0) + 1) AS varchar))), 7) nuCorrExp, "); 
        //sqlQry1.append("SUBSTRING((SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(?)), 1, 6) deSiglaCorta ");
        sqlQry1.append("(SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(?)) deSiglaCorta ");
        sqlQry1.append("from IDOSGD.tdtc_expediente ");
        sqlQry1.append("where nu_ann_exp = ? ");
        sqlQry1.append("and co_dep_exp = ? ");
        sqlQry1.append("and co_gru = '1' ");

        StringBuffer sqlQry2 = new StringBuffer();
        sqlQry2.append("select RIGHT(REPLICATE('0', 10) + CAST((ISNULL(max(nu_sec_exp), 0) + 1) AS VARCHAR), 10) ");
        sqlQry2.append("from IDOSGD.tdtc_expediente ");
        sqlQry2.append("where nu_ann_exp = ? ");

        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.TDTC_EXPEDIENTE(\n"
                + "nu_ann_exp,\n"
                + "nu_sec_exp,\n"
                + "fe_exp,\n"
                + "co_proceso,\n"
                + "de_detalle,\n"
                + "co_dep_exp,\n"
                + "co_gru,\n"
                + "nu_corr_exp,\n"
                + "nu_expediente,\n"
                + "nu_folios,\n"
                + "nu_plazo,\n"
                + "us_crea_audi,\n"
                + "fe_crea_audi,\n"
                + "us_modi_audi,\n"
                + "fe_modi_audi,\n"
                + "es_estado)\n"
                + "VALUES(?, ?, (SELECT CONVERT(DATETIME, ?, 103)),?,?,?,'1',?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,'0')");

        try {
            Map mapResult = this.jdbcTemplate.queryForMap(sqlQry1.toString(), new Object[]{expedienteBean.getCoDepExp(), expedienteBean.getNuAnnExp(), expedienteBean.getCoDepExp()});
            String snuSecExp = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class, new Object[]{expedienteBean.getNuAnnExp()});
            expedienteBean.setNuSecExp(snuSecExp);
            String snuCorrExp = String.valueOf(mapResult.get("nuCorrExp"));
            String sdeSiglaCorta = String.valueOf(mapResult.get("deSiglaCorta"));

            //expedienteBean.setNuExpediente(Utilidades.AjustaCampoIzquierda(sdeSiglaCorta, 6, "0") + expedienteBean.getNuAnnExp() + snuCorrExp);
            sdeSiglaCorta=sdeSiglaCorta.replace(".","");
            sdeSiglaCorta=sdeSiglaCorta.replace("-","");
            if(sdeSiglaCorta.length()<6){
                sdeSiglaCorta=Utilidades.AjustaCampoIzquierda(sdeSiglaCorta, 6, "0");
            }
            expedienteBean.setNuExpediente(sdeSiglaCorta + expedienteBean.getNuAnnExp() + snuCorrExp);

            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{expedienteBean.getNuAnnExp(), snuSecExp, expedienteBean.getFeExp(),
                expedienteBean.getCoProceso(), expedienteBean.getDeProceso(), expedienteBean.getCoDepExp(), snuCorrExp, expedienteBean.getNuExpediente(),
                expedienteBean.getNuFolios(), expedienteBean.getNuPlazo(), expedienteBean.getUsCreaAudi(), expedienteBean.getUsCreaAudi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public String verificarDocumentoLeido(String pnuAnn, String pnuEmi) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT ISNULL(MAX(es_doc_rec),'0') ");
        sqlQry.append("FROM IDOSGD.tdtv_destinos  WITH (NOLOCK) ");
        sqlQry.append("WHERE nu_ann = ? ");
        sqlQry.append("AND nu_emi = ? ");
        sqlQry.append("AND es_eli = '0' ");

        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{pnuAnn, pnuEmi});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public String anularDocumentoEmiAdm(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT ISNULL(MAX(nu_cor_doc), 1) + 1 ");
        sqlQry.append("FROM IDOSGD.tdtv_remitos  WITH (NOLOCK)  ");
        sqlQry.append("WHERE nu_ann         = ? ");
        sqlQry.append("AND ti_emi         = ? ");
        sqlQry.append("AND co_dep_emi     = ? ");
        sqlQry.append("AND co_tip_doc_adm = ? ");
        sqlQry.append("AND ((? IS NOT NULL AND nu_doc_emi = ?) ");
        sqlQry.append("OR ? IS NULL) ");

        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.tdtv_remitos "); 
        sqlUpd.append("set es_doc_emi = ?, ");
        sqlUpd.append("nu_cor_doc = ?, ");
        sqlUpd.append("fe_use_mod=CURRENT_TIMESTAMP, ");
        sqlUpd.append("co_use_mod=? "); 
        sqlUpd.append("WHERE nu_ann = ? ");
        sqlUpd.append("AND nu_emi = ? ");
        sqlUpd.append("AND es_eli = '0' ");

        try {
            String snuCorDoc = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getTiEmi(),
                documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm(), documentoEmiBean.getNuDocEmi(), documentoEmiBean.getNuDocEmi(), documentoEmiBean.getNuDocEmi()});

            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getEsDocEmi(), snuCorDoc, documentoEmiBean.getCoUseMod(),
                documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public List<DestinatarioDocumentoEmiBean> getLstDestinatarioGrupo(String pcoGrupo,String pcoTipDoc) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();

        sql.append("SELECT a.co_dep co_dependencia, "); 
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep) de_dependencia, ");
        sql.append("ISNULL(a.co_emp, b.co_empleado) co_empleado, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](ISNULL(a.co_emp, b.co_empleado)) de_empleado, ");
        sql.append("c.co_loc co_local, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_LOCAL](c.co_loc) de_local,'1' co_prioridad, ");
        sql.append("? co_tramite_first, "); 
        sql.append("? de_tramite_first, "); 
        sql.append("? co_tramite_next, "); 
        sql.append("? de_tramite_next "); 
        sql.append("FROM ( ");   
        sql.append("  select y.cemp_codemp co_emp, "); 
        sql.append("		 x.co_dep ");
        sql.append("  from IDOSGD.tdtd_dep_gru x ");
        sql.append("	   RIGHT OUTER JOIN IDOSGD.rhtm_per_empleados y "); 
        sql.append("	   ON y.cemp_codemp = x.co_emp AND y.cemp_est_emp = '1' ");
        sql.append("  where x.co_gru_des= ? ");
        sql.append("  and x.es_dep_gru = '1' ");
        // sql.append(" order by y.cemp_codemp ");
        sql.append(") a, "); 
        sql.append("IDOSGD.rhtm_dependencia b, "); 
        sql.append("IDOSGD.sitm_local_dependencia c ");
        sql.append("WHERE a.co_dep = b.co_dependencia ");
        sql.append("AND a.co_dep = c.co_dep "); 
        sql.append("order by a.co_dep "); 

        try {
            if(pcoTipDoc.equals("232")){
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
                        new Object[]{Constantes.CO_TRAMITE_ATENDER, Constantes.DE_TRAMITE_ATENDER,Constantes.CO_TRAMITE_FINES, Constantes.DE_TRAMITE_FINES, pcoGrupo});                                
            }else{
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
                        new Object[]{Constantes.CO_TRAMITE_ORIGINAL, Constantes.DE_TRAMITE_ORIGINAL,Constantes.CO_TRAMITE_COPIA, Constantes.DE_TRAMITE_COPIA, pcoGrupo});                
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
    public String updDocumentoLimpiarObj(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
 
        sqlUpd.append("UPDATE IDOSGD.TDTV_ARCHIVO_DOC SET BL_DOC = NULL, DE_RUTA_ORIGEN = NULL, FEULA=(SELECT CONVERT(VARCHAR(10), CURRENT_TIMESTAMP, 112)) \n"
                    + "WHERE NU_ANN = ? \n"
                    + "AND NU_EMI = ? ");
        
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }
    @Override
    public String updDocumentoObj(final DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
//        boolean inInsert = false;
//        /*Verificamos si es Insert o Update*/
//        try {
//            int cant = this.jdbcTemplate.queryForInt("select count(nu_ann) from IDOSGD.tdtv_archivo_doc where nu_ann = ? and nu_emi = ?", new Object[]{docObjBean.getNuAnn(), docObjBean.getNuEmi()});
//            if (cant > 0) {
//                inInsert = false;
//            } else {
//                inInsert = true;
//            }
//
//        } catch (Exception e) {
//            e.printStackTrace();
//            vReturn = e.getMessage();
//        }
//
//        /**/
//
//        final LobHandler lobhandler = new DefaultLobHandler();
//        if (inInsert) {
//            StringBuffer sql = new StringBuffer();
//            sql.append("INSERT INTO IDOSGD.tdtv_archivo_doc (NU_ANN,NU_EMI,BL_DOC,DE_RUTA_ORIGEN,ES_FIRMA,FEULA,W_BL_DOC,W_DE_RUTA_ORIGEN)\n"
//                    + "VALUES(?,?,?,?,'0', (SELECT CONVERT(VARCHAR(10), CURRENT_TIMESTAMP, 112)),?,?)");
//            try {
//                this.jdbcTemplate.execute(sql.toString(),
//                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
//                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
//                            throws SQLException {
//                        ps.setString(1, docObjBean.getNuAnn());
//                        ps.setString(2, docObjBean.getNuEmi());
//                        lobCreator.setBlobAsBytes(ps, 3, docObjBean.getDocumento());
//                        ps.setString(4, docObjBean.getNombreArchivo());
//                         lobCreator.setBlobAsBytes(ps, 5, docObjBean.getDocumento());
//                        ps.setString(6, docObjBean.getNombreArchivo()       );
//                    }
//                });
//                vReturn = "OK";
//            } catch (Exception e) {
//                e.printStackTrace();
//            }
//        } else {
//            
//            StringBuffer sql1 = new StringBuffer();
//            StringBuffer sql2 = new StringBuffer();
//            if(docObjBean.getNombreArchivo().toUpperCase().indexOf(".DOCX")>=0)
//                    {
//                    sql1.append("UPDATE IDOSGD.TDTV_ARCHIVO_DOC SET W_BL_DOC = ?, W_DE_RUTA_ORIGEN = ? \n"
//                    + "WHERE NU_ANN = ? \n"
//                    + "AND NU_EMI = ? ");
//                    
//                    this.jdbcTemplate.execute(sql1.toString(),
//                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
//                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
//                        throws SQLException {
//                        lobCreator.setBlobAsBytes(ps, 1, docObjBean.getDocumento());
//                        ps.setString(2, docObjBean.getNombreArchivo());                       
//                        ps.setString(3, docObjBean.getNuAnn());
//                        ps.setString(4, docObjBean.getNuEmi());
//                        }
//                    });
//                                    
//                    
//                    }
//             
//            sql2.append("UPDATE IDOSGD.TDTV_ARCHIVO_DOC SET BL_DOC = ?, DE_RUTA_ORIGEN = ?, FEULA=(SELECT CONVERT(VARCHAR(10), CURRENT_TIMESTAMP, 112)) \n"
//                    + "WHERE NU_ANN = ? \n"
//                    + "AND NU_EMI = ? ");
//            try {
//                this.jdbcTemplate.execute(sql2.toString(),
//                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
//                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
//                            throws SQLException {
//                        lobCreator.setBlobAsBytes(ps, 1, docObjBean.getDocumento());
//                        ps.setString(2, docObjBean.getNombreArchivo());
//                        ps.setString(3, docObjBean.getNuAnn());
//                        ps.setString(4, docObjBean.getNuEmi());
//                    }
//                });
//                vReturn = "OK";
//            } catch (Exception e) {
//                e.printStackTrace();
//                vReturn = e.getMessage().substring(0, 20);
//            }
//        }
        
        this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_TDTV_ARCHIVO_INS_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi", "PBL_DOC","p_DE_RUTA_ORIGEN" )                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR),
                        new SqlParameter("PBL_DOC", Types.VARBINARY),
                        new SqlParameter("p_DE_RUTA_ORIGEN", Types.VARCHAR) );
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", docObjBean.getNuAnn())
                .addValue("p_nuEmi", docObjBean.getNuEmi())
                .addValue("PBL_DOC", docObjBean.getDocumento())
                .addValue("p_DE_RUTA_ORIGEN",  docObjBean.getNombreArchivo())  ;         
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
            System.out.println("PK_SGD_TRAMITE_PROCE_TDTV_ARCHIVO_INS_UDP==>" +  docObjBean.getNuEmi());
        } catch (Exception ex) {             
            ex.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String getCanDocumentoEmiDuplicados(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        try {
            vReturn = this.jdbcTemplate.queryForObject("   SELECT COUNT(1)\n"
                    + "     FROM IDOSGD.tdtv_remitos tr WITH (NOLOCK) \n"
                    + "    WHERE tr.nu_ann = ?\n"
                    + "      AND tr.co_dep_emi = ?\n"
                    + "      AND tr.co_tip_doc_adm = ?\n"
                    + "      AND tr.nu_doc_emi = ?\n"
                    + "      AND tr.ti_emi = '01'\n"
                    + "      AND nu_cor_doc = 1\n"
                    + "      AND tr.es_doc_emi NOT IN ('9')", String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getCoDepEmi(),
                documentoEmiBean.getCoTipDocAdm(), documentoEmiBean.getNuDocEmi()});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public List<EmpleadoBean> getPersonalEditDocAdmEmision(String pcoDepEmi) {
        StringBuffer sql = new StringBuffer();
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        sql.append("SELECT e.cemp_apepat, ");
        sql.append("	   e.cemp_apemat, ");
        sql.append("	   e.cemp_denom, "); 
        sql.append("	   e.CEMP_CODEMP ");
        sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e ");
        sql.append("WHERE e.CEMP_EST_EMP = '1' ");
        sql.append("AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA "); 
        sql.append("		            from IDOSGD.rhTM_dependencia d "); 
        sql.append("		 	    where d.CO_DEPENDENCIA =? "); 
        sql.append("			    or CO_DEPEN_PADRE=?) "); 
        sql.append("			    OR CO_DEPENDENCIA=? ) ");
        sql.append("UNION ");
        sql.append("SELECT  e.cemp_apepat, ");
        sql.append("	    e.cemp_apemat, ");
        sql.append("	    e.cemp_denom, "); 
        sql.append("	    e.CEMP_CODEMP ");
        sql.append("FROM IDOSGD.RHTM_PER_EMPLEADOS e ");
        sql.append("WHERE e.CEMP_EST_EMP = '1' ");
        sql.append("AND cemp_codemp "); 
        sql.append("in ( ");
        sql.append("select co_emp "); 
        sql.append("from IDOSGD.tdtx_dependencia_empleado "); 
        sql.append("where co_dep=? "); 
        sql.append("and es_emp='0' ");
        sql.append("UNION ");
        sql.append("select CO_EMPLEADO "); 
        sql.append("from IDOSGD.rhtm_dependencia "); 
        sql.append("where co_dependencia=? ");
        sql.append(") ");
        sql.append("ORDER BY 1 ");

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class),
                    new Object[]{pcoDepEmi, pcoDepEmi, pcoDepEmi, pcoDepEmi, pcoDepEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
 @Override
    public String getUsuarioNofirmaProveido(String pcoEmpleado) {
        StringBuffer sql = new StringBuffer();
        String listTotal = "0";

        sql.append(" SELECT COUNT(*) TOTAL FROM IDOSGD.SI_ELEMENTO WHERE CTAB_CODTAB='NO_FIRMA_PROVEIDO' AND CELE_DESELE='"+pcoEmpleado+"' ");
        try {
             listTotal = this.jdbcTemplate.queryForObject(sql.toString(), String.class);  
            
        } catch (EmptyResultDataAccessException e) {
           
        } catch (Exception e) {
             
            e.printStackTrace();
        }
        return listTotal;
    }
    @Override
    public String updEstadoDocumentoEmiAdm(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=?\n"
                + ",ES_DOC_EMI=?\n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getEsDocEmi(),
                documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public String updEstadoDocumentoEmitido(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
      /*  StringBuffer sqlUpd = new StringBuffer();

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
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getNuCorDoc(),
                documentoEmiBean.getNuDocEmi(), documentoEmiBean.getEsDocEmi(), documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado.";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }*/
       this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_ESTADODOCUMENTO_EMITIDO_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi","PNU_COR_DOC","PNU_DOC_EMI","PES_DOC_EMI","PUSUARIO" )                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR), 
                        new SqlParameter("PNU_COR_DOC", Types.VARCHAR),
                        new SqlParameter("PNU_DOC_EMI", Types.VARCHAR),
                        new SqlParameter("PES_DOC_EMI", Types.VARCHAR),
                        new SqlParameter("PUSUARIO", Types.VARCHAR) 
                );
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", documentoEmiBean.getNuAnn())
                .addValue("p_nuEmi", documentoEmiBean.getNuEmi())
                .addValue("PNU_COR_DOC", documentoEmiBean.getNuCorDoc())
                .addValue("PNU_DOC_EMI", documentoEmiBean.getNuDocEmi())
                .addValue("PES_DOC_EMI", documentoEmiBean.getEsDocEmi())
                .addValue("PUSUARIO", documentoEmiBean.getCoUseMod()) ;         
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String updDocumentoEmiAdmBean(String nuAnn, String nuEmi, DocumentoEmiBean documentoEmiBean, ExpedienteBean expedienteBean, RemitenteEmiBean remitenteEmiBean, String pcoUserMod) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=?,");
        if (documentoEmiBean != null) {
            documentoEmiBean.setNuAnn(nuAnn);
            sqlUpd.append("DE_ASU=?\n"
                    + ",NU_DIA_ATE=?\n"
                    + ",CO_TIP_DOC_ADM=?\n"
                    + ",NU_DOC_EMI=?\n"
                    + ",DE_DOC_SIG=?\n"
                    + ",NU_COR_DOC= (CASE WHEN ? IS NOT NULL THEN ? ELSE ? END),\n"
                     + "OBS_DOC= ?,\n"
                    /*(SELECT nvl(MAX(nu_cor_doc), 0) + 1\n"
                    + "FROM tdtv_remitos\n"
                    + "WHERE nu_ann = ?\n"
                    + "AND co_dep_emi = ?\n"
                    + "AND co_tip_doc_adm = ?\n"
                    + "AND ti_emi = ?)),\n"*/);
            
            //para verificar numero correlativo de documento
            if(documentoEmiBean.getNuDocEmi()==null || documentoEmiBean.getNuDocEmi().trim().equals("") ){
                String vnuCorDoc = getNroCorrelativoDocumento(documentoEmiBean);
                documentoEmiBean.setNuCorDoc(vnuCorDoc);
            }
            //sqlUpd.append(",A.NU_COR_DOC=").append(documentoEmiBean.getNuCorDoc()).append(",\n");
            
            //Para verificar si ya tiene un numero correlativo de emision
            if (documentoEmiBean.getNuCorEmi()==null || documentoEmiBean.getNuCorEmi().trim().equals("") ){
                String vnuCorEmi = getNroCorrelativoEmision(documentoEmiBean);
                documentoEmiBean.setNuCorEmi(vnuCorEmi);
                sqlUpd.append("NU_COR_EMI='" + documentoEmiBean.getNuCorEmi()+ "',\n");
            }
        }
        if (expedienteBean != null) {
            sqlUpd.append("NU_ANN_EXP='" + expedienteBean.getNuAnnExp() + "',\n");
            sqlUpd.append("NU_SEC_EXP='" + expedienteBean.getNuSecExp() + "',\n");
        }
        if (remitenteEmiBean != null) {
            sqlUpd.append("CO_LOC_EMI='" + remitenteEmiBean.getCoLocal() + "'\n"
                    + ",CO_DEP_EMI='" + remitenteEmiBean.getCoDependencia() + "'\n"
                    + ",CO_EMP_EMI='" + remitenteEmiBean.getCoEmpFirma() + "',\n");
        }
        sqlUpd.append("FE_USE_MOD=CURRENT_TIMESTAMP "
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");

        try {
            if (documentoEmiBean != null) {
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, documentoEmiBean.getDeAsu(), documentoEmiBean.getNuDiaAte(),
                    documentoEmiBean.getCoTipDocAdm(), documentoEmiBean.getNuDocEmi(), documentoEmiBean.getDeDocSig(),
                    documentoEmiBean.getNuDocEmi(), documentoEmiBean.getNuCorDoc(),documentoEmiBean.getNuCorDoc(),documentoEmiBean.getDeObsDoc(),/*, nuAnn, documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm(),
                    documentoEmiBean.getTiEmi(),*/ nuAnn, nuEmi});
            } else {
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, nuAnn, nuEmi});
            }
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            //con.printStackTrace();
            vReturn = "Numero de Documento Duplicado";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public String getNumeroDocSiguienteAdm(String pnuAnn, String pcoDepEmi, String pcoDoc) {
        String vReturn = "1";
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT LTRIM(RTRIM(CAST((ISNULL(MAX(nu_doc_emi), 0) + 1) AS VARCHAR))) "); 
            sql.append("FROM IDOSGD.tdtv_remitos tr WITH (NOLOCK)  ");
            sql.append("WHERE tr.nu_ann = ? ");
            sql.append("AND tr.co_dep_emi = ? ");
            sql.append("AND tr.co_tip_doc_adm = ? ");
            sql.append("AND tr.ti_emi = '01' ");
            sql.append("AND tr.nu_cor_doc = 1 ");
            sql.append("AND tr.es_doc_emi != '9' ");
            sql.append("AND ISNUMERIC(nu_doc_emi) = 1 ");
            vReturn = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pcoDepEmi, pcoDoc});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "1";
            //vReturn=e.getMessage();
        }
        vReturn = Utility.getInstancia().rellenaCerosIzquierda(vReturn, 6);
        return vReturn;
    }

    @Override
    public DocumentoObjBean getPropiedadesArchivo(String pnuAnn, String pnuEmi, String ptiCap) {
        StringBuffer sql = new StringBuffer();
        sql.append("select "); 
        sql.append("nu_ann, "); 
        sql.append("nu_emi, "); 
        sql.append("de_ruta_origen nombre_Archivo, ");
        sql.append("UPPER(SUBSTRING(de_ruta_origen, ((LEN(de_ruta_origen) - CHARINDEX('.', REVERSE(de_ruta_origen)) + 1) + 1), LEN(de_ruta_origen))) tipo_doc ");
        sql.append("from IDOSGD.tdtv_archivo_doc WITH (NOLOCK)  "); 
        sql.append("where nu_ann = ? ");
        sql.append("and nu_emi = ? ");

        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class), new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (docObjBean);
    }

    @Override
    public String getNroCorrelativoDocumento(DocumentoEmiBean documentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT ISNULL(MAX(nu_cor_doc), 0) + 1 ");
        sqlQry.append("FROM IDOSGD.tdtv_remitos WITH (NOLOCK)  ");
        sqlQry.append("WHERE nu_ann       = ? ");
        sqlQry.append("AND ti_emi         = ? ");
        sqlQry.append("AND co_dep_emi     = ? ");
        sqlQry.append("AND co_tip_doc_adm = ? ");

        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getTiEmi(),
                documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm()});
//            documentoEmiBean.setNuCorDoc(vReturn);
//            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }
    
    @Override
    public String getNroCorrelativoDocumentoDel(DocumentoEmiBean documentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT ISNULL(MAX(nu_cor_doc), 1) + 1 ");
        sqlQry.append("FROM IDOSGD.tdtv_remitos WITH (NOLOCK)  ");
        sqlQry.append("WHERE nu_ann       = ? ");
        sqlQry.append("AND ti_emi         = ? ");
        sqlQry.append("AND co_dep_emi     = ? ");
        sqlQry.append("AND co_tip_doc_adm = ? ");

        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getTiEmi(),
                documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm()});
//            documentoEmiBean.setNuCorDoc(vReturn);
//            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }    

    @Override
    public String delAllReferenciaDocumentoEmi(String nuAnn, String nuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("DELETE FROM IDOSGD.tdtr_referencia  ");
        sqlDel.append("WHERE NU_ANN = ? "); 
        sqlDel.append("AND NU_EMI = ? ");

        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{nuAnn, nuEmi});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public String delAllDestinatarioDocumentoEmi(String nuAnn, String nuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("DELETE FROM IDOSGD.TDTV_DESTINOS ");
        sqlDel.append("WHERE NU_ANN = ? "); 
        sqlDel.append("AND NU_EMI = ? ");

        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{nuAnn, nuEmi});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }

    @Override
    public String delDocumentoEmiAdm(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update IDOSGD.tdtv_remitos "); 
        sqlUpd.append("set CO_USE_MOD=?, ");
        sqlUpd.append("ES_ELI='1', ");
        sqlUpd.append("NU_DOC_EMI=?, ");
        sqlUpd.append("NU_COR_DOC=?, ");
        sqlUpd.append("NU_ANN_EXP=?, ");
        sqlUpd.append("NU_SEC_EXP=?, ");
        sqlUpd.append("NU_DET_EXP=?, ");
        sqlUpd.append("FE_USE_MOD=CURRENT_TIMESTAMP ");
        sqlUpd.append("where NU_ANN=? "); 
        sqlUpd.append("and NU_EMI=? ");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getNuDocEmi(),
                documentoEmiBean.getNuCorDoc(), documentoEmiBean.getNuAnnExp(), documentoEmiBean.getNuSecExp(), documentoEmiBean.getNuDetExp(),
                documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }

    @Override
    public List<DependenciaBean> getListDestinatarioEmi(String pcoDepen, String pdeDepEmite) {
        StringBuffer sql = new StringBuffer();

        boolean vfiltro = pdeDepEmite != null && !pdeDepEmite.trim().equals("") ? true : false;

        sql.append("SELECT DE_DEPENDENCIA, ");
        sql.append("       CO_DEPENDENCIA, ");
        sql.append("       DE_CORTA_DEPEN ");
        sql.append("  FROM IDOSGD.RHTM_DEPENDENCIA ");
        sql.append(" WHERE co_nivel <> '6' ");
        sql.append("   AND IN_BAJA = '0' ");
        if (vfiltro) {
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%' ) ");
        }
        sql.append("UNION ");  
        sql.append("SELECT DE_DEPENDENCIA, ");
        sql.append("       CO_DEPENDENCIA, ");
        sql.append("       DE_CORTA_DEPEN ");
        sql.append("  FROM IDOSGD.RHTM_DEPENDENCIA ");
        sql.append(" WHERE co_nivel = '6' ");
        sql.append("   AND IN_BAJA = '0' ");
        sql.append("   AND (CO_DEPEN_PADRE in ( select CO_DEPEN_PADRE co_dep "); 
        sql.append("							from IDOSGD.RHTM_DEPENDENCIA where CO_DEPENDENCIA = ?) ");
        sql.append("							OR ? IN (CO_DEPENDENCIA, CO_DEPEN_PADRE)) ");
        if (vfiltro) {
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' + ? + '%' OR DE_CORTA_DEPEN LIKE ? + '%') ");
        }
        sql.append("UNION ");
        sql.append("SELECT ' [TODOS]', ");
        sql.append(" 	   NULL, ");
        sql.append(" 	   NULL ");
        if (vfiltro) {
            sql.append(" where ' [TODOS]' like '%'+?+'%' ");
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
    public List<DestinoBean> getListaDestinosCodDepTipoDes(String nu_ann, String nu_emi) {
        StringBuffer sql = new StringBuffer();
        List<DestinoBean> list = new ArrayList<DestinoBean>();
        sql.append("SELECT DISTINCT co_dep_des, ");
        sql.append("ti_des ");
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

    @Override
    public List<DestinoBean> getListaDestinosOriTipoDes(String nu_ann, String nu_emi) {
        StringBuffer sql = new StringBuffer();
        List<DestinoBean> list = new ArrayList<DestinoBean>();
        sql.append("SELECT ti_des, ");
        sql.append("CASE ti_des ");
        sql.append("	WHEN '02' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_PROVEEDOR](nu_ruc_des) ");
        sql.append("	WHEN '03' THEN [IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](nu_dni_des) ");
        sql.append("	WHEN '04' THEN [IDOSGD].[PK_SGD_DESCRIPCION_OTRO_ORIGEN](co_otr_ori_des) ");
        sql.append("	ELSE ' ' ");
        sql.append("END co_dep_des ");
        sql.append("FROM IDOSGD.tdtv_destinos  WITH (NOLOCK) "); 
        sql.append("where nu_ann = ? ");
        sql.append("and nu_emi = ? ");
        sql.append("and es_eli = '0' "); 
        sql.append("ORDER BY ti_des ");

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
    public List<ReferenciaRemitoBean> getListaReferenciaRemitos(String nu_ann, String nu_emi) {
        StringBuffer sql = new StringBuffer();
        List<ReferenciaRemitoBean> list = new ArrayList<ReferenciaRemitoBean>();
        sql.append("SELECT DISTINCT b.ti_emi, b.co_dep_emi ");
        sql.append("FROM   IDOSGD.tdtr_referencia a WITH (NOLOCK) , IDOSGD.tdtv_remitos b  WITH (NOLOCK) ");
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
    
    @Override
    public List<ReferenciaRemitoBean> getOriReferenciaLista(String nu_ann, String nu_emi) {
        StringBuffer sql = new StringBuffer();
        List<ReferenciaRemitoBean> list = new ArrayList<ReferenciaRemitoBean>();
        sql.append("SELECT b.ti_emi, ");
        sql.append("CASE b.ti_emi "); 
        sql.append("	WHEN '02' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_PROVEEDOR](b.nu_ruc_emi) "); 
        sql.append("	WHEN '03' THEN [IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](b.nu_dni_emi) "); 
        sql.append("	WHEN '04' THEN [IDOSGD].[PK_SGD_DESCRIPCION_OTRO_ORIGEN](b.co_otr_ori_emi) "); 
        sql.append("	ELSE ' ' "); 
        sql.append("END co_dep_emi ");
        sql.append("FROM   IDOSGD.tdtr_referencia a WITH (NOLOCK) , "); 
        sql.append("	   IDOSGD.tdtv_remitos b WITH (NOLOCK)  "); 
        sql.append("WHERE  a.nu_ann_ref = b.nu_ann "); 
        sql.append("AND    a.nu_emi_ref = b.nu_emi "); 
        sql.append("AND    a.nu_ann = ? "); 
        sql.append("AND    a.nu_emi = ? "); 
        sql.append("ORDER  BY b.ti_emi ");

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

    @Override
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

    @Override
    public String updRemitoResumenDestinatario(String pnuAnn, String pnuEmi, String vti_des, String vco_pri, String vnu_cant_des, String vresOriDes) {
        String vReturn = "NO_OK";
        /*StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_DES = ?,CO_PRIORIDAD = ? ,nu_candes=? ");
        sqlUpd.append("WHERE NU_ANN = ? ");
        sqlUpd.append("AND NU_EMI = ? ");

        StringBuffer sqlUpd2 = new StringBuffer();
        sqlUpd2.append("UPDATE IDOSGD.TDTV_REMITOS SET DE_ORI_DES = ?\n" +
                        "WHERE NU_ANN = ? \n" +
                        "AND NU_EMI = ? ");        
        
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{
                vti_des, vco_pri, vnu_cant_des, pnuAnn, pnuEmi
            });
            
            this.jdbcTemplate.update(sqlUpd2.toString(), new Object[]{
                vresOriDes, pnuAnn, pnuEmi
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }*/
              this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_REMITOS_RESUMEN_DESTINATARIO_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi", "p_vti_des","p_vco_pri","p_vnu_cant_des","p_vresOriDes")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR),
                        new SqlParameter("p_vti_des", Types.VARCHAR),
                        new SqlParameter("p_vco_pri", Types.VARCHAR),
                        new SqlParameter("p_vnu_cant_des", Types.VARCHAR),
                        new SqlParameter("p_vresOriDes", Types.VARCHAR));
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", pnuAnn)
                .addValue("p_nuEmi", pnuEmi)
                .addValue("p_vti_des", vti_des)
                .addValue("p_vco_pri", vco_pri)
                .addValue("p_vnu_cant_des", vnu_cant_des)
                .addValue("p_vresOriDes", vresOriDes);         
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        
        return vReturn;

    }

    public String updRemitoResumenReferencia(String pnuAnn, String pnuEmi, String vti_ori, String vdeOriEmi) {
        String vReturn = "NO_OK";
        /*StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_REF = ? ");
        sqlUpd.append("WHERE NU_ANN = ? ");
        sqlUpd.append("AND NU_EMI = ? ");

        StringBuffer sqlUpd2 = new StringBuffer();
        sqlUpd2.append("UPDATE IDOSGD.TDTV_REMITOS SET DE_ORI_EMI = ? ");
        sqlUpd2.append("WHERE NU_ANN = ? ");
        sqlUpd2.append("AND NU_EMI = ? ");
        
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{
                vti_ori, pnuAnn, pnuEmi
            });
            
            // Actualizacion de de_ori_emi
            this.jdbcTemplate.update(sqlUpd2.toString(), new Object[]{
                vdeOriEmi, pnuAnn, pnuEmi
            });
            
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }*/
        this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_REMITOS_RESUMEN_REFERENCIA_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi", "p_vti_ori","p_vdeOriEmi")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR),
                        new SqlParameter("p_vti_ori", Types.VARCHAR),
                        new SqlParameter("p_vdeOriEmi", Types.VARCHAR) );
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", pnuAnn)
                .addValue("p_nuEmi", pnuEmi)
                .addValue("p_vti_ori", vti_ori)
                .addValue("p_vdeOriEmi", vdeOriEmi) ;         
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        return vReturn;
    }
    @Override
    public DocumentoEmiBean existeDocumentoReferenciado(BuscarDocumentoEmiBean buscarDocumentoEmiBean){
        StringBuffer sql = new StringBuffer();
        DocumentoEmiBean documentoEmiBean = null;
        sql.append("SELECT A.NU_ANN, "); 
        sql.append("A.NU_EMI, ");
        sql.append("A.NU_COR_DOC "); 
        sql.append("FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK)  ");
        sql.append("WHERE A.NU_ANN=? ");
        sql.append("AND A.CO_DEP_EMI=? ");
        sql.append("AND A.TI_EMI='01' ");
        sql.append("AND A.CO_TIP_DOC_ADM=? ");
        sql.append("AND A.NU_DOC_EMI=? ");
        sql.append("AND A.ES_ELI='0' ");
        sql.append("AND A.ES_DOC_EMI NOT IN ('5','7','9') ");        
        
        try {
             documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{buscarDocumentoEmiBean.getsCoAnnioBus(),buscarDocumentoEmiBean.getsBuscDestinatario(),buscarDocumentoEmiBean.getsDeTipoDocAdm(),
                    buscarDocumentoEmiBean.getsNumDocRef()});
             documentoEmiBean.setMsjResult("OK");
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiBean;          
    }
    
    @Override
    public List<DocumentoEmiBean> getDocumentosReferenciadoBusq(DocumentoEmiBean documentoEmiBean,String sTipoAcceso){       
        StringBuffer sql = new StringBuffer(); 
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List<DocumentoEmiBean> list = new ArrayList<DocumentoEmiBean>();        
        
        sql.append("SELECT A.* ");
        sql.append("FROM ( ");         
        sql.append("	SELECT  DISTINCT B.NU_COR_EMI, ");        
        sql.append("            (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_DE_EMI_REF](B.NU_ANN, B.NU_EMI)) DE_EMI_REF, "); 
        sql.append("		B.FE_EMI, ");
        sql.append("		CONVERT(VARCHAR(10), B.FE_EMI, 3) FE_EMI_CORTA, ");
        sql.append("		(SELECT CDOC_DESDOC "); 
        sql.append("		FROM IDOSGD.SI_MAE_TIPO_DOC "); 
        sql.append("		WHERE CDOC_TIPDOC = B.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        sql.append("		CASE B.NU_CANDES ");
        sql.append("                WHEN 1 THEN (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP](B.NU_ANN, B.NU_EMI)) ");
        sql.append("                ELSE (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_TI_DES_EMP_V](B.NU_ANN, B.NU_EMI)) ");
        sql.append("		END DE_EMP_PRO, ");			
        sql.append("		(SELECT CEMP_APEPAT + ' ' + CEMP_APEMAT + ' ' + CEMP_DENOM "); 
        sql.append("		FROM IDOSGD.RHTM_PER_EMPLEADOS "); 
        sql.append("		WHERE CEMP_CODEMP = B.CO_EMP_RES) DE_EMP_RES, ");
        sql.append("            (SELECT DE_EST "); 
        sql.append("		FROM IDOSGD.TDTR_ESTADOS "); 
        sql.append("		WHERE (CO_EST + DE_TAB) = (B.ES_DOC_EMI + 'TDTV_REMITOS')) DE_ES_DOC_EMI, ");
        sql.append("		C.NU_DOC, ");
        sql.append("		UPPER(B.DE_ASU) DE_ASU_M, ");
        sql.append("		B.NU_ANN, ");
        sql.append("		C.NU_EXPEDIENTE, ");
        sql.append("		B.NU_EMI, ");
        sql.append("		B.TI_CAP, ");
        sql.append("		C.IN_EXISTE_DOC EXISTE_DOC, ");
        sql.append("		CASE ISNULL(C.TI_EMI_REF,'0') + ISNULL(C.IN_EXISTE_ANEXO,'2') ");
        sql.append("                WHEN '00' THEN 0 ");
        sql.append("                WHEN '02' THEN 0 ");
        sql.append("                ELSE 1 ");
        sql.append("		END EXISTE_ANEXO, "); 
        sql.append("		ISNULL(C.CO_PRIORIDAD,'1') CO_PRIORIDAD, ");
        sql.append("		B.ES_DOC_EMI, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY B.NU_COR_EMI) AS ROWNUM ");
        sql.append("	FROM ( "); 
        sql.append("		 SELECT NU_ANN, NU_EMI FROM [IDOSGD].[PK_SGD_DESCRIPCION_ARBOL_SEG](:pCoAnio, :pNuEmi) ");
        sql.append("	) A, "); 
        sql.append("	IDOSGD.TDTV_REMITOS B WITH (NOLOCK) , "); 
        sql.append("	IDOSGD.TDTX_REMITOS_RESUMEN C WITH (NOLOCK)  ");
        sql.append("	WHERE A.NU_ANN = B.NU_ANN ");
        sql.append("	AND A.NU_EMI = B.NU_EMI ");
        sql.append("	AND C.NU_ANN = B.NU_ANN ");
        sql.append("	AND C.NU_EMI = B.NU_EMI ");
        sql.append("	AND B.TI_EMI='01' ");
        sql.append("	AND C.TI_EMI='01' ");
        sql.append("	AND B.ES_ELI = '0' ");
        sql.append("	AND B.CO_DEP_EMI = :pCoDepEmi ");        
        if (sTipoAcceso.equals("1")) {
            sql.append(" AND B.CO_EMP_RES = :pcoEmpRes ");      
            objectParam.put("pcoEmpRes", documentoEmiBean.getCoEmpRes());           
        }
        objectParam.put("pCoAnio", documentoEmiBean.getNuAnn());           
        objectParam.put("pNuEmi", documentoEmiBean.getNuEmi());   
        objectParam.put("pCoDepEmi", documentoEmiBean.getCoDepEmi());        
        sql.append(") A "); 
        sql.append("WHERE ROWNUM < 101 ");
        sql.append("ORDER BY A.NU_COR_EMI DESC ");
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;          
    }
    
    @Override
    public TblRemitosBean getDatosDocumento(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        TblRemitosBean tblRemitosBean = null;
        sql.append("SELECT A.NU_ANN, ");
        sql.append("A.NU_EMI, ");
        sql.append("A.CO_DEP_EMI, ");
        sql.append("A.FE_EMI "); 
        sql.append("FROM IDOSGD.TDTV_REMITOS A WITH (NOLOCK) ");
        sql.append("WHERE A.NU_ANN=? "); 
        sql.append("AND A.NU_EMI=? "); 
        sql.append("AND A.ES_ELI='0' ");      
        
        try {
             tblRemitosBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(TblRemitosBean.class),
                    new Object[]{pnuAnn, pnuEmi});
             tblRemitosBean.setMsgResult("OK");
        } catch (EmptyResultDataAccessException e) {
            tblRemitosBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return tblRemitosBean;              
    }
    
    @Override
    public String getCoEmplFirmoDocumento(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        String result = "NO_OK";
        sql.append("SELECT CO_EMP_EMI ");
        sql.append("FROM IDOSGD.TDTV_REMITOS  WITH (NOLOCK) ");
        sql.append("WHERE nu_emi = ? ");
        sql.append("AND nu_ann = ? ");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuEmi, pnuAnn});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;        
    }
    
    @Override
    public DocumentoEmiBean getEstadoDocumento(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        DocumentoEmiBean documentoEmiBean = null;
        sql.append("SELECT CO_EMP_EMI, ");
        sql.append("	   ES_DOC_EMI, ");
        sql.append("	   NU_ANN,NU_EMI, ");
        sql.append("	   TI_EMI, ");
        sql.append("	   CO_DEP_EMI, ");
        sql.append("	   CO_TIP_DOC_ADM, ");
        sql.append("	   NU_DOC_EMI, ");
        sql.append("	   CO_EMP_RES ");
        sql.append("FROM IDOSGD.TDTV_REMITOS  WITH (NOLOCK) ");
        sql.append("WHERE nu_ann = ? ");
        sql.append("AND nu_emi = ? "); 
        sql.append("AND ES_ELI='0' ");        
        
        try {
             documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            documentoEmiBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoEmiBean;             
    }
    
    
    @Override
    public String getNroCorrelativoEmision(DocumentoEmiBean documentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "0";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT ISNULL(MAX(nu_cor_emi), 0) + 1 ");
        sqlQry.append("FROM IDOSGD.tdtv_remitos WITH (NOLOCK)  ");
        sqlQry.append("WHERE nu_ann = ? ");
        sqlQry.append("AND co_dep_emi = ? ");
        sqlQry.append("AND ti_emi = ? ");

        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getCoDepEmi(), documentoEmiBean.getTiEmi()});
//            documentoEmiBean.setNuCorDoc(vReturn);
//            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;
    }
    
    @Override
    public String updChangeToDespacho(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        /*StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=?\n"
                + ",ES_DOC_EMI=?\n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + ",FE_EMI=CURRENT_TIMESTAMP\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getEsDocEmi(),
                documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }*/
         this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_REMITOS_TO_DESPACHO_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi","p_CodUseMod","p_estado")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR), 
                        new SqlParameter("p_CodUseMod", Types.VARCHAR),
                        new SqlParameter("p_estado", Types.VARCHAR) 
                );
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", documentoEmiBean.getNuAnn())
                .addValue("p_nuEmi", documentoEmiBean.getNuEmi())
                .addValue("p_CodUseMod", documentoEmiBean.getCoUseMod())
                .addValue("p_estado", documentoEmiBean.getEsDocEmi()) ;         
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        return vReturn;
    }
    
        @Override
    public String updEstadoDocumentoEnvioNotificacion(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd2 = new StringBuffer();
        StringBuffer sqlUpd = new StringBuffer(); 
         sqlUpd.append("UPDATE IDOSGD.TDTV_REMITOS \n"
                + "set CO_USE_MOD=?\n"
                + ",DOC_ESTADO_MSJ=?\n"
                + ",COD_DEP_MSJ=?\n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + ",FE_ENV_MES=CURRENT_TIMESTAMP\n"
                + ",TI_ENV_MSJ=?\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        sqlUpd2.append("update IDOSGD.tdtv_destinos \n"
                + "set CO_USE_MOD=?\n"
                + ",CO_PRI=?\n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n" 
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        try {
             this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getEsDocEmi(),
                documentoEmiBean.getCoDepEmi(),documentoEmiBean.getTiEnvMsj(),documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            this.jdbcTemplate.update(sqlUpd2.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getCoPrioridad(),
                documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;
    }
    
    @Override
    public List<DocumentoBean> getLstDocRecepcionadoRefMp(String pcoDepen, String pannio, String ptiDoc, String pnuDoc) {
        StringBuffer sql = new StringBuffer();
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();
        
        sql.append("SELECT A.* ");
        sql.append("FROM ( "); 

        sql.append("	select E.*, ROW_NUMBER() OVER (ORDER BY FE_REC_DOC) AS ROWNUM ");
        sql.append("	from ( ");
        sql.append("		select ");
        sql.append("		d.FE_REC_DOC, ");
        sql.append("		SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](r.co_tip_doc_adm),1,100) de_tip_doc_adm, ");
        sql.append("		SUBSTRING(rr.nu_doc,1,50) NU_DOC, ");
        sql.append("		CONVERT(VARCHAR(10), r.FE_EMI, 3) FE_EMI_CORTA, ");
        sql.append("		CONVERT(VARCHAR(10), d.FE_REC_DOC, 3) FE_REC_DOC_CORTA, ");
        sql.append("		r.nu_ann, ");
        sql.append("		r.nu_emi, ");
        sql.append("		d.nu_des, ");
        sql.append("		REPLACE(LTRIM(RTRIM(r.DE_ASU)), CHAR(10), ' ') DE_ASU, ");
        sql.append("		r.co_tip_doc_adm, ");
        sql.append("		SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](d.co_dep_des),1,200) de_dep_des, ");
        sql.append("		SUBSTRING(rr.nu_expediente,1,20) NU_EXPEDIENTE, ");
        sql.append("		r.NU_ANN_EXP, ");
        sql.append("		r.NU_SEC_EXP ");
        sql.append("		from IDOSGD.TDTV_REMITOS R  WITH (NOLOCK) ");
        sql.append("		INNER JOIN IDOSGD.TDTV_DESTINOS D WITH (NOLOCK)  ON d.nu_ann = r.nu_ann AND d.nu_emi = r.nu_emi ");
        sql.append("		INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK)  ON rr.nu_ann=r.nu_ann AND rr.nu_emi = r.nu_emi ");
        sql.append("		where ");
        sql.append("		r.es_eli = '0' ");
        sql.append("		AND d.es_eli = '0' ");
        sql.append("		AND r.nu_ann = ? ");
 
        sql.append("		AND r.es_doc_emi NOT IN ('5', '9', '7') ");
        sql.append("		and d.es_doc_rec <> '0' ");  
        sql.append("		and d.co_dep_des in "); 
        sql.append("		(select ? ");  
        sql.append("		UNION "); 
        sql.append("		  SELECT co_dep_ref ");
        sql.append("			FROM IDOSGD.tdtx_referencia ");
        sql.append("		   WHERE co_dep_emi = ? ");
        sql.append("			 AND ti_emi = 'D' ");
        sql.append("			 AND es_ref = '1') ");
        sql.append("		AND r.co_tip_doc_adm = ? ");
        if (pnuDoc != null && pnuDoc.trim().length() > 0){
            pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
            sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
        }
        
        sql.append("UNION \n" +                            
                    "select\n"+
                    "d.FE_REC_DOC,\n" +
                    "SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTRING(rr.nu_doc,1,50) NU_DOC,\n" +
                    "CONVERT(VARCHAR(10), r.FE_EMI, 3) FE_EMI_CORTA,\n" +
                    "CONVERT(VARCHAR(10), d.FE_REC_DOC, 3) FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "d.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)), CHAR(10), ' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](d.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTRING(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n " );
        sql.append("		from IDOSGD.TDTV_REMITOS R ");
        sql.append("		INNER JOIN IDOSGD.TDTV_DESTINOS D ON d.nu_ann = r.nu_ann AND d.nu_emi = r.nu_emi ");
        sql.append("		INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR ON rr.nu_ann=r.nu_ann AND rr.nu_emi = r.nu_emi ");
        sql.append(" where\n" +
                    "r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +                   
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and (R.nu_ann+R.nu_emi) in ( \n" + 
                    "select  (REF.nu_ann_ref+REF.nu_emi_ref) as nurem from IDOSGD.TDTR_REFERENCIA REF where (REF.nu_ann+REF.nu_emi) in \n" +
                    "(select (R.nu_ann+R.nu_emi) as nurem \n");
        sql.append("		from IDOSGD.TDTV_REMITOS R WITH (NOLOCK)  ");
        sql.append("		INNER JOIN IDOSGD.TDTV_DESTINOS D  WITH (NOLOCK) ON d.nu_ann = r.nu_ann AND d.nu_emi = r.nu_emi ");
        sql.append("		INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK)  ON rr.nu_ann=r.nu_ann AND rr.nu_emi = r.nu_emi ");
        sql.append("where\n" +
                    "r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +
                    "AND r.nu_ann = ? \n" +                   
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and d.co_dep_des = ? \n" );
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("%')\n");
                    }
        
        sql.append("))		UNION "); 
        sql.append("		SELECT ");
        sql.append("		DE.FE_REC_DOC, ");
        sql.append("		SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](r.co_tip_doc_adm),1,100) de_tip_doc_adm, ");
        sql.append("		SUBSTRING(rr.nu_doc,1,50) NU_DOC, ");
        sql.append("		CONVERT(VARCHAR(10), r.FE_EMI, 3) FE_EMI_CORTA, ");
        sql.append("		CONVERT(VARCHAR(10), DE.FE_REC_DOC, 3) FE_REC_DOC_CORTA, ");
        sql.append("		r.nu_ann, ");
        sql.append("		r.nu_emi, ");
        sql.append("		DE.nu_des, ");
        sql.append("		REPLACE(LTRIM(RTRIM(r.DE_ASU)), CHAR(10), ' ') DE_ASU, ");
        sql.append("		r.co_tip_doc_adm, ");
        sql.append("		SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](DE.co_dep_des),1,200) de_dep_des, ");
        sql.append("		SUBSTRING(rr.nu_expediente, 1, 20) NU_EXPEDIENTE, ");
        sql.append("		r.NU_ANN_EXP, ");
        sql.append("		r.NU_SEC_EXP ");
        sql.append("		FROM IDOSGD.TDTV_DESTINOS DE WITH (NOLOCK)  ");
        sql.append("		INNER JOIN IDOSGD.TDTV_REMITOS R  WITH (NOLOCK) ON DE.NU_ANN=R.NU_ANN AND DE.NU_EMI=R.NU_EMI  ");
        sql.append("		INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN RR WITH (NOLOCK)  ON  RR.NU_ANN=R.NU_ANN AND R.NU_EMI=RR.NU_EMI ");
        sql.append("		WHERE R.NU_ANN = ? "); 
        sql.append("		AND R.es_eli = '0' ");
        sql.append("		AND DE.ES_ELI='0' ");
        sql.append("		and R.es_doc_emi not in ('9','7','5') ");
        sql.append("		AND R.CO_GRU = '1' ");
        sql.append("		AND DE.ES_ENV_POR_TRA='1' ");
        sql.append("		AND r.co_tip_doc_adm = ? ");
        if (pnuDoc != null && pnuDoc.trim().length() > 0) {
            pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
            sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
        }
        
        
        
        
        
        sql.append("	) E ");

        sql.append(") A ");
        sql.append("WHERE ROWNUM < 201 ");
        sql.append("order by 1 desc "); 

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{pannio,pcoDepen, pcoDepen, ptiDoc, pannio, ptiDoc,pannio, ptiDoc});
        } catch (EmptyResultDataAccessException e) {
            list = null;
             e.printStackTrace();
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }    
    
    @Override
    public DocumentoEmiBean getEstadoDocumentoAudi(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        DocumentoEmiBean documentoEmiBean = null;
        sql.append("SELECT CO_EMP_EMI, ");
        sql.append("ES_DOC_EMI,NU_ANN, ");
        sql.append("NU_EMI, ");
        sql.append("TI_EMI, ");
        sql.append("CO_DEP_EMI, ");
        sql.append("CO_TIP_DOC_ADM, ");
        sql.append("NU_DOC_EMI ");
        sql.append("FROM IDOSGD.TDTV_REMITOS  WITH (NOLOCK) ");
        sql.append("WHERE nu_ann = ? ");
        sql.append("AND nu_emi = ? ");      
        
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
    public String insPersonalVoBo(String nuAnn,String nuEmi,String coDep,String coEmp,String coUser){
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
            vReturn = "Datos Duplicado INSERT IDOSGD.TDTV_PERSONAL_VB.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    @Override
    public String delPersonalVoBo(String nuAnn,String nuEmi,String coDep,String coEmp){
        String vReturn = "NO_OK";
        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("DELETE FROM IDOSGD.TDTV_PERSONAL_VB\n" +
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

    @Override
    public String getInNumeraDocAdm(String tipoDoc) {
        String vReturn = "0";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT IN_NUMERACION FROM IDOSGD.SI_MAE_TIPO_DOC\n" +
                    "WHERE CDOC_INDBAJ='0'\n" +
                    "AND CDOC_TIPDOC=?");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{tipoDoc});
            if(vReturn==null){
                vReturn="0";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String getInFirmaDocAdm(String tipoDoc) {
        String vReturn = "2";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT IN_TIPO_FIRMA FROM IDOSGD.SI_MAE_TIPO_DOC\n" +
                    "WHERE CDOC_INDBAJ='0'\n" +
                    "AND CDOC_TIPDOC=?");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{tipoDoc});
            if(vReturn==null){
                vReturn="2";
            }            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public List<DestinatarioDocumentoEmiBean> getLstPersVoBoGrupo(String pcoGrupo) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();

        sql.append("SELECT a.co_dep co_dependencia, ");
        sql.append("b.DE_CORTA_DEPEN de_dependencia, ");
        sql.append("ISNULL(a.co_emp, b.co_empleado) co_empleado, ");
        sql.append("[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](ISNULL(a.co_emp, b.co_empleado)) de_empleado ");
        sql.append("FROM ( ");   
        sql.append("  select y.cemp_codemp co_emp, "); 
        sql.append("		 x.co_dep ");
        sql.append("  from IDOSGD.tdtd_dep_gru x ");
        sql.append("	   RIGHT OUTER JOIN IDOSGD.rhtm_per_empleados y ");
        sql.append("	   ON y.cemp_codemp = x.co_emp AND y.cemp_est_emp = '1' ");
        sql.append("  where x.co_gru_des= ? ");
        sql.append("  and x.es_dep_gru = '1' ");
        sql.append(") a, "); 
        sql.append("IDOSGD.rhtm_dependencia b, "); 
        sql.append("IDOSGD.sitm_local_dependencia c ");
        sql.append("WHERE a.co_dep = b.co_dependencia ");
        sql.append("AND a.co_dep = c.co_dep "); 
        sql.append("order by 1 ");

        try {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
                        new Object[]{pcoGrupo});                
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }    

    @Override
    public String updArchivarDocumento(DocumentoEmiBean documentoEmiBean, Usuario usuario) {
        String vReturn = "NO_OK";
         
        this.spActualizaEstado = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_CREA_ACTUALIZA_ESTADO_A]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("pnu_ann", "pnu_emi", "pde_ane")
                .declareParameters(
                        new SqlParameter("pnu_ann", Types.VARCHAR),
                        new SqlParameter("pnu_emi", Types.VARCHAR),
                        new SqlParameter("pde_ane", Types.VARCHAR));
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pnu_ann", documentoEmiBean.getNuAnn())
                .addValue("pnu_emi", documentoEmiBean.getNuEmi())
                .addValue("pde_ane", documentoEmiBean.getDocObser());

        try {
            this.spActualizaEstado.execute(in);
            vReturn = "OK";
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }
       // return mensaje;
        
        
        return vReturn; 
    }

    @Override
    public int getCantidadAnexo(DocumentoEmiBean documentoEmiBean) {
       String vReturn = "0";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("select sum(ISNULL(B.nu_des,0))\n" +                   
                    "FROM IDOSGD.tdtv_destinos A  WITH (NOLOCK) LEFT JOIN IDOSGD.tdtv_anexos B WITH (NOLOCK)  \n" +
                    "ON A.nu_ann=B.nu_ann AND A.nu_emi=B.nu_emi AND A.nu_des=B.nu_des \n" +
                    "where A.nu_ann = ? \n" +             
                    "and A.nu_emi = ? \n");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
//            documentoEmiBean.setNuCorDoc(vReturn);
//            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
}
        return Integer.parseInt(vReturn);
    }

    /*interoperabilidad*/
    @Override
    public String insMesaVitual(DatosInterBean datosInter) {
        String vReturn = "NO_OK";

        
        this.spInsMesaVirtual = new SimpleJdbcCall(this.dataSource).withProcedureName("IDOSGD.PK_SGD_MESA_VIRTUAL_INS_MESA_VIRTUAL")
        .withoutProcedureColumnMetaDataAccess()
        .useInParameterNames("P_COUSEMOD", "P_DEPDES", "P_NOMDES", "P_CARDES", "P_NUANN", "P_NUEMI")
                //descargamsj(pnu_ann tdtv_remitos.nu_ann%TYPE,pnu_emi tdtv_remitos.nu_emi%TYPE,pnu_des tdtv_destinos.nu_des%TYPE,pnu_msj tdtv_destinos.nu_msj%TYPE,pfecha tdtv_destinos.fec_finmsj%TYPE,pob_msj tdtv_destinos.ob_msj%TYPE,pmo_msj_dev tdtv_destinos.mo_msj_dev%TYPE,pes_pen_msj tdtv_destinos.es_pen_msj%TYPE,pest VARCHAR2);
        .declareParameters(
                new SqlParameter("P_COUSEMOD", Types.VARCHAR),
                new SqlParameter("P_DEPDES", Types.VARCHAR),
                new SqlParameter("P_NOMDES", Types.VARCHAR),
                new SqlParameter("P_CARDES", Types.VARCHAR) ,
                new SqlParameter("P_NUANN", Types.VARCHAR),
                new SqlParameter("P_NUEMI", Types.VARCHAR)
                        );
        SqlParameterSource in = new MapSqlParameterSource()
        .addValue("P_COUSEMOD", datosInter.getCoUseMod())
        .addValue("P_DEPDES", datosInter.getDeDepDes())
        .addValue("P_NOMDES", datosInter.getDeNomDes())
        .addValue("P_CARDES", datosInter.getDeCarDes())               
       .addValue("P_NUANN", datosInter.getNuAnn()) 
       .addValue("P_NUEMI", datosInter.getNuEmi()); 
        try {
            this.spInsMesaVirtual.execute(in);
            vReturn = "OK";
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }  
        
        
        return vReturn;
    }
      /*interoperabilidad*/

    @Override
    public List<DestinatarioDocumentoEmiBean> getLstDestExternoGrupo(String pcoGrupo, String pTiDes) {
       StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();

        if(pTiDes.equals("02"))
        {
            
            sql.append("SELECT CPRO_RUC NURUC, CPRO_RAZSOC deProveedor,ISNULL(CPRO_DOMICIL,' ') cdirRemite,ISNULL(CPRO_TELEFO,' ') cTelefono,ISNULL(CPRO_EMAIL,' ') cexpCorreoe, \n" +
                        "ISNULL(TRIM(NODEP)+' '+TRIM(NOPRV)+' '+TRIM(NODIS),' ') UBIGEO, ISNULL(TRIM(CUBI_CODDEP),' ') CCOD_DPTO,ISNULL(LTRIM(CUBI_CODPRO),' ') CCOD_PROV,ISNULL(LTRIM(CUBI_CODDIS),' ') CCOD_DIST\n" +
                        "FROM IDOSGD.LG_PRO_PROVEEDOR J LEFT JOIN IDOSGD.IDTUBIAS U ON J.CUBI_CODDEP+J.CUBI_CODPRO+J.CUBI_CODDIS=U.UBDEP+U.UBPRV+U.UBDIS \n" +
                        "WHERE J.CPRO_RUC IN (SELECT CO_DES FROM IDOSGD.TDTD_DEP_GRU_VAR WHERE CO_GRU_DES = ? AND Es_dep_gru = '1' AND TI_DES = ?)\n" +
                        "order by J.rowid");
        }

        if(pTiDes.equals("03"))
        {
            sql.append("SELECT C.NULEM nu_dni,ISNULL(substring(IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(C.NULEM), 1, 100),' ') de_ciudadano,\n"+
            "ISNULL(C.DEDOMICIL,' ') CDIR_REMITE,ISNULL(C.DEEMAIL,' ') CEXP_CORREOE,ISNULL(C.DETELEFO,' ') CTELEFONO,ISNULL(C.UBDEP,' ') CCOD_DPTO,ISNULL(C.UBPRV,' ') CCOD_PROV,ISNULL(C.UBDIS,' ') CCOD_DIST,\n" +
            "ISNULL(LTRIM(NODEP)+' '+TRIM(NOPRV)+' '+TRIM(NODIS),' ') UBIGEO\n" +          
            "FROM IDOSGD.TDTX_ANI_SIMIL C  WITH (NOLOCK) LEFT JOIN IDOSGD.IDTUBIAS U ON C.UBDEP+C.UBPRV+C.UBDIS=U.UBDEP+U.UBPRV+U.UBDIS \n" +
            "WHERE C.NULEM IN (SELECT CO_DES FROM IDOSGD.TDTD_DEP_GRU_VAR WHERE CO_GRU_DES = ? AND Es_dep_gru = '1' AND TI_DES = ?)\n" +
            "order by C.rowid ");       
        }

        if(pTiDes.equals("04"))
        {
            sql.append( "SELECT O.CO_OTR_ORI co_otro_origen,O.DE_APE_PAT_OTR+' '+O.DE_APE_MAT_OTR+', '+O.DE_NOM_OTR + ' - ' +\n" +
                        "O.DE_RAZ_SOC_OTR +'##'+  ISNULL(S.CELE_DESELE,'   ') +'##'+  O.NU_DOC_OTR_ORI  de_otro_origen_full\n" +
                        "ISNULL(O.DE_DIR_OTRO_ORI,' ') CDIR_REMITE,ISNULL(O.DE_EMAIL,' ') CEXP_CORREOE,ISNULL(O.DE_TELEFO,' ') CTELEFONO,ISNULL(O.UB_DEP,' ') CCOD_DPTO,ISNULL(O.UB_PRO,' ') CCOD_PROV,ISNULL(O.UB_DIS,' ') CCOD_DIST,\n" +
                        "ISNULL(LTRIM(NODEP)+' '+LTRIM(NOPRV)+' '+LTRIM(NODIS),' ') UBIGEO\n" +          
                        "FROM IDOSGD.TDTR_OTRO_ORIGEN O LEFT JOIN IDOSGD.IDTUBIAS U ON O.UB_DEP+O.UB_PRO+O.UB_DIS=U.UBDEP+U.UBPRV+U.UBDIS \n" +
                        "LEFT JOIN  (\n" +
                        "SELECT CELE_CODELE, CELE_DESELE  FROM IDOSGD.SI_ELEMENTO  WHERE CTAB_CODTAB ='TIP_DOC_IDENT') S\n" +
                        "ON O.CO_TIP_OTR_ORI = S.CELE_CODELE \n" +                    
                        "WHERE O.CO_OTR_ORI IN (SELECT CO_DES FROM IDOSGD.TDTD_DEP_GRU_VAR WHERE CO_GRU_DES = ? AND Es_dep_gru = '1' AND TI_DES = ?)\n" +
                        "order by O.rowid ");    
        }
        
        

        try {

                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
                        new Object[]{ pcoGrupo,pTiDes});                
            
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }


}