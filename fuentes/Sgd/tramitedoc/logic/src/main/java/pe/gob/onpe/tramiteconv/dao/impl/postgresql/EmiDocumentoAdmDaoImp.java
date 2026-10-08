package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

//import freemarker.template.utility.NumberUtil;
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
import pe.gob.onpe.tramitedoc.util.AleatorioCodVerificacion;

/*interoperabilidad*/
import pe.gob.onpe.tramitedoc.bean.DatosInterBean;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

/*interoperabilidad*/

@Repository("emiDocumentoAdmDao")
public class EmiDocumentoAdmDaoImp extends SimpleJdbcDaoBase implements EmiDocumentoAdmDao {
private SimpleJdbcCall spActualizaEstado;
/*interoperabilidad*/
private SimpleJdbcCall spInsMesaVirtual;
/*interoperabilidad*/
    @Override
    public List<DocumentoEmiBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmi) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoEmiBean> list = new ArrayList<DocumentoEmiBean>();

        sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(X.NU_ANN, X.NU_EMI) DE_EMI_REF,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");         
        sql.append("CASE\n"
                + "WHEN X.NU_CANDES=1 THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(X.NU_ANN, X.NU_EMI)\n"
                + "ELSE\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(X.NU_ANN, X.NU_EMI)\n"
                + "END AS DE_EMP_PRO,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI");    
        sql.append(" FROM ( ");
        sql.append(" SELECT ");
        sql.append(" A.NU_COR_EMI,A.FE_EMI,"); 
        sql.append("CASE\n"
                + "WHEN COALESCE(B.TI_EMI_REF,'0')||COALESCE(B.IN_EXISTE_ANEXO,'2')='00' THEN 0 \n"
                + "WHEN COALESCE(B.TI_EMI_REF,'0')||COALESCE(B.IN_EXISTE_ANEXO,'2')='02' THEN 0 \n"
                + "ELSE 1  END AS EXISTE_ANEXO,");
        sql.append("COALESCE(B.CO_PRIORIDAD,'1') CO_PRIORIDAD,");
        sql.append(" TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,");
        sql.append(" A.OBS_DOC DE_OBS_DOC,");
        sql.append(" B.NU_DOC,UPPER(A.DE_ASU) DE_ASU_M,A.NU_ANN,B.NU_EXPEDIENTE,A.NU_EMI,A.TI_CAP,B.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append(" A.ES_DOC_EMI,A.CO_TIP_DOC_ADM,A.NU_CANDES,A.CO_EMP_RES,COALESCE(A.DOC_ESTADO_MSJ,'-1') as DOC_ESTADO_MSJ,");
         sql.append(" (SELECT MAX(C.TI_DES) FROM IDOSGD.TDTV_DESTINOS C WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN   ) as tiDest,COALESCE(A.TI_ENV_MSJ,'-1') tiEnvMsj,"); 
        sql.append(" CASE A.TI_ENV_MSJ WHEN '0' THEN 'MESA DE PARTES' WHEN '1' THEN 'ENVÍO DIRECTO' WHEN '2' THEN 'MESA VIRTUAL' END    recursoenvio");//interoperabilidad        
        sql.append(" FROM IDOSGD.TDTV_REMITOS A"
                + " INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN B ON B.NU_ANN=A.NU_ANN AND B.NU_EMI=A.NU_EMI ");
        sql.append(" WHERE A.TI_EMI='01' "); 
        String pNUAnn = buscarDocumentoEmi.getsCoAnnio();
        if (!(buscarDocumentoEmi.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNUAnn);
        } 
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
                sql.append(" AND STRPOS(B.TI_EMI_REF, :pTiEmiRef) > 0 ");
                objectParam.put("pTiEmiRef", buscarDocumentoEmi.getsRefOrigen());
            }
            if (buscarDocumentoEmi.getsDestinatario() != null && buscarDocumentoEmi.getsDestinatario().trim().length() > 0) {
                sql.append(" AND STRPOS(B.TI_EMI_DES, :pTiEmpPro) > 0 ");
                objectParam.put("pTiEmpPro", buscarDocumentoEmi.getsDestinatario());
            }
            if (buscarDocumentoEmi.getEsFiltroFecha() != null
                    && (buscarDocumentoEmi.getEsFiltroFecha().equals("1") || buscarDocumentoEmi.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoEmi.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoEmi.getsFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59' ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }

             if (buscarDocumentoEmi.getCoEstMensajeria()!= null && buscarDocumentoEmi.getCoEstMensajeria().trim().length()>0) {
                sql.append(" AND CO_TIP_DOC_ADM in (SELECT CDOC_TIPDOC FROM IDOSGD.SI_MAE_TIPO_DOC WHERE IN_DOC_SALIDA='1') AND A.ES_DOC_EMI='0' AND  COALESCE(A.TI_ENV_MSJ,'3')= :pcoEstMensajeria");
                objectParam.put("pcoEstMensajeria", buscarDocumentoEmi.getCoEstMensajeria());
            }
             
        }

        //Busqueda
        if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoEmi.getsNumCorEmision() != null && buscarDocumentoEmi.getsNumCorEmision().trim().length() > 0) {
                sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", Integer.parseInt(buscarDocumentoEmi.getsNumCorEmision()));
            }

            if (buscarDocumentoEmi.getsNumDoc() != null && buscarDocumentoEmi.getsNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE ''||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoEmi.getsNumDoc());
            }

            if (buscarDocumentoEmi.getsBuscNroExpediente() != null && buscarDocumentoEmi.getsBuscNroExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoEmi.getsBuscNroExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoEmi.getsDeAsuM() != null && buscarDocumentoEmi.getsDeAsuM().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(buscarDocumentoEmi.getsDeAsuM())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
                objectParam.put("pDeAsunto", buscarDocumentoEmi.getsDeAsuM());
            }
        }
        sql.append(" ORDER BY A.FE_EMI").append(sOrdenList);
        sql.append(") AS X ");
        sql.append(" LIMIT 100");

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
      public ProcessResult<List<DocumentoBean>> getDocumentosBuscaEmiAdmList(BuscarDocumentoEmiBean buscarDocumentoEmi,FiltroPaginate paginate) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();
        ProcessResult<List<DocumentoBean>> Result = new ProcessResult<List<DocumentoBean>>();
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();
       
        sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(X.NU_ANN, X.NU_EMI) DE_EMI_REF,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");         
        sql.append(" (CASE WHEN X.NU_CANDES =1 THEN IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(X.NU_ANN, X.NU_EMI) "
                + "        ELSE IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(X.NU_ANN, X.NU_EMI) END) DE_EMP_PRO,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,");        
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI "); 
        sql.append(" FROM ( SELECT X.*,row_number() OVER (ORDER BY 1) AS   fila, COUNT(1) OVER()  AS filasTotal ");
        sql.append(" FROM ( ");
        sql.append(" SELECT ");
        sql.append(" A.NU_COR_EMI,A.FE_EMI," 
                + "(CASE WHEN COALESCE(B.TI_EMI_REF,'0')||COALESCE(B.IN_EXISTE_ANEXO,'2')='00' THEN '0' "
                + "      WHEN COALESCE(B.TI_EMI_REF,'0')||COALESCE(B.IN_EXISTE_ANEXO,'2')='02' THEN '0' ELSE '1' END )EXISTE_ANEXO, "
                + "COALESCE(B.CO_PRIORIDAD,'1') CO_PRI ,");
        sql.append(" TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,");
        sql.append(" A.OBS_DOC DE_OBS_DOC,");
        sql.append(" B.NU_DOC,UPPER(A.DE_ASU) DE_ASU_M,A.NU_ANN,B.NU_EXPEDIENTE,A.NU_EMI,A.TI_CAP,B.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append(" A.ES_DOC_EMI,A.CO_TIP_DOC_ADM,A.NU_CANDES,A.CO_EMP_RES,COALESCE(A.DOC_ESTADO_MSJ,'-1') as DOC_ESTADO_MSJ,");
        sql.append(" (SELECT MAX(C.TI_DES) FROM IDOSGD.TDTV_DESTINOS C WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN) as tiDest,COALESCE(A.TI_ENV_MSJ,'-1') tiEnvMsj, ");//ORDER BY C.TI_DES DESC INTEROPERABILIDAD
        sql.append(" CASE A.TI_ENV_MSJ WHEN '0' THEN 'MESA DE PARTES' WHEN '1' THEN 'ENVÍO DIRECTO' WHEN '2' THEN 'MESA VIRTUAL' END    recursoenvio");//interoperabilidad
        
        sql.append(" ,VB.IN_VB sFirmaVB  ");
        sql.append(" FROM IDOSGD.TDTV_REMITOS A INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN B ON B.NU_ANN=A.NU_ANN  AND B.NU_EMI=A.NU_EMI  ");
        sql.append(" LEFT JOIN ( SELECT VB.NU_ANN,VB.NU_EMI\n" +
                        ",MAX(CASE WHEN VB.IN_VB='B' OR VB.IN_VB='0' THEN 1\n" +
                        "WHEN VB.IN_VB='2' THEN 2 \n" +
                        "WHEN VB.IN_VB='1' THEN 0 END ) IN_VB\n" +
                        "FROM IDOSGD.TDTV_PERSONAL_VB VB\n" +
                        "GROUP BY VB.NU_ANN,VB.NU_EMI  ) VB ON VB.NU_ANN=A.NU_ANN  AND VB.NU_EMI=A.NU_EMI ");             
        sql.append(" WHERE  A.TI_EMI='01'  "); 
        /*String pNUAnn = buscarDocumentoEmi.getsCoAnnio();
        if (!(buscarDocumentoEmi.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNUAnn);
        } */
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
        }
        String bExcepSeach="0";
        //Filtro
        //if (pTipoBusqueda.equals("0") || bBusqFiltro) {
        if(bExcepSeach.equals("0")) {
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
                sql.append(" AND POSITION(B.TI_EMI_REF in :pTiEmiRef) > 0 ");
                objectParam.put("pTiEmiRef", buscarDocumentoEmi.getsRefOrigen());
            }
            if (buscarDocumentoEmi.getsDestinatario() != null && buscarDocumentoEmi.getsDestinatario().trim().length() > 0) {
                sql.append(" AND POSITION(B.TI_EMI_DES in :pTiEmpPro) > 0 ");
                objectParam.put("pTiEmpPro", buscarDocumentoEmi.getsDestinatario());
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
            /*if (buscarDocumentoEmi.getsTrabDestino()!= null && buscarDocumentoEmi.getsTrabDestino().trim().length() > 0) {
                sql.append(" AND NVL((SELECT 1 FROM TDTV_DESTINOS C WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN AND CO_EMP_DES=:pTiEmpDest and rownum = 1),0) > 0 ");
                objectParam.put("pTiEmpDest", buscarDocumentoEmi.getsTrabDestino());
            }*/
            if (buscarDocumentoEmi.getEsFiltroFecha() != null
                    && (buscarDocumentoEmi.getEsFiltroFecha().equals("1") || buscarDocumentoEmi.getEsFiltroFecha().equals("3") || buscarDocumentoEmi.getEsFiltroFecha().equals("2"))) {
                String vFeEmiIni = buscarDocumentoEmi.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoEmi.getsFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59' ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
            
            //YUAL
            if (buscarDocumentoEmi.getCoEstMensajeria()!= null && buscarDocumentoEmi.getCoEstMensajeria().trim().length()>0) {
                sql.append(" AND CO_TIP_DOC_ADM in ( SELECT CDOC_TIPDOC FROM IDOSGD.SI_MAE_TIPO_DOC WHERE IN_DOC_SALIDA='1' )  AND A.ES_DOC_EMI='0' AND  COALESCE(A.TI_ENV_MSJ,'3')= :pcoEstMensajeria");
                objectParam.put("pcoEstMensajeria", buscarDocumentoEmi.getCoEstMensajeria());
            }
            
        }

        //Busqueda
       // if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoEmi.getsNumCorEmision() != null && buscarDocumentoEmi.getsNumCorEmision().trim().length() > 0) {
                sql.append(" AND A.NU_COR_EMI = cast(:pnuCorEmi as bigint)  ");
                objectParam.put("pnuCorEmi", buscarDocumentoEmi.getsNumCorEmision());
            }

            if (buscarDocumentoEmi.getsNumDoc() != null && buscarDocumentoEmi.getsNumDoc().trim().length() > 1) {
                sql.append(" AND B.NU_DOC LIKE '%'||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoEmi.getsNumDoc());
            }

            if (buscarDocumentoEmi.getsBuscNroExpediente() != null && buscarDocumentoEmi.getsBuscNroExpediente().trim().length() > 1) {
                sql.append(" AND B.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoEmi.getsBuscNroExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoEmi.getsDeAsuM() != null && buscarDocumentoEmi.getsDeAsuM().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue(buscarDocumentoEmi.getsDeAsuM())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
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
                sql.append("   AND ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoEmi.getsNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoEmi.getsNumDocRef()+"'||'%'))");
                sql.append(" )");
            }
       

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
        sql.append(" ) X  ) X ");
        sql.append(" WHERE fila BETWEEN "+paginate.getPaginaDesde()+" AND "+paginate.getPaginaHasta()+"  ");
        
       
        
        try {

            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));
             Result.setResult(list);           
            Result.setIsSuccess(true);
            objectParam = new HashMap<String, Object>();
            buscarDocumentoEmi= new BuscarDocumentoEmiBean();
            list = new ArrayList<DocumentoBean>();
            sql = new StringBuffer();
            
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
        sql.append("SELECT \n"
                + "a.nu_ann, \n"
                + "a.nu_emi, \n"
                + "CAST(a.nu_cor_emi AS TEXT) nu_cor_emi, \n"
                + "a.co_loc_emi, IDOSGD.PK_SGD_DESCRIPCION_de_local(a.co_loc_emi) de_loc_emi, \n"
                + "a.co_dep_emi, IDOSGD.PK_SGD_DESCRIPCION_de_dependencia(a.co_dep_emi) de_dep_emi,\n"
                + "a.ti_emi, IDOSGD.PK_SGD_DESCRIPCION_ti_destino(a.ti_emi) de_tip_emi,\n"
                + "a.co_emp_emi, IDOSGD.PK_SGD_DESCRIPCION_de_nom_emp(co_emp_emi) de_emp_emi,\n"
                + "a.co_emp_res, IDOSGD.PK_SGD_DESCRIPCION_de_nom_emp(a.co_emp_res) de_emp_res,\n"
                + "a.nu_dni_emi, \n"
                + "a.nu_ruc_emi, \n"
                + "a.co_otr_ori_emi,\n"
                + "td_pk_tramite.ti_emi_emp (a.nu_ann, a.nu_emi) de_ori_emi,\n"
                + "TO_CHAR(a.fe_emi, 'DD/MM/YYYY') fe_emi, \n"
                + "TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n"
                + "a.co_tip_doc_adm, IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(a.co_tip_doc_adm) de_tip_doc_adm,\n"
                + "a.ti_emi,\n"
                + "a.nu_doc_emi,\n"
                + "a.de_doc_sig,\n"
                + "a.es_doc_emi, IDOSGD.PK_SGD_DESCRIPCION_estados (a.es_doc_emi,'TDTV_REMITOS') de_es_doc_emi,\n"
                + "a.nu_dia_ate, \n"
                + "a.de_asu, \n"
                + "a.co_pro, \n"
                + "a.co_sub, \n"
                + "a.ti_cap, \n"
                + "a.co_exp,\n"
                + "a.co_use_cre,\n"
                + "a.fe_use_cre, \n"
                + "a.co_use_mod, \n"
                + "a.fe_use_mod, \n"
                + "a.nu_ann_exp,\n"
                + "a.nu_sec_exp, \n"
                + "a.nu_det_exp,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE(a.nu_ann_exp,a.nu_sec_exp) NU_EXPEDIENTE\n"
                + "FROM IDOSGD.tdtv_remitos a \n"
                + "WHERE A.NU_ANN = ? AND A.NU_EMI=?");

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
        sql.append("select A.*,\n"
                + " OBS_DOC DE_OBS_DOC,"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(A.CO_EMP_EMI) DE_EMP_EMI,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_RES,\n"
                + "TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,\n"
                + "B.FE_EXP,TO_CHAR(B.FE_EXP,'DD/MM/YYYY') FE_EXP_CORTA,\n"
                + "B.NU_EXPEDIENTE,\n"
                + "B.CO_PROCESO,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(B.CO_PROCESO) DE_PROCESO,\n"
                + "RR.IN_FIRMA_ANEXO, \n"
                + " COALESCE(A.DOC_ESTADO_MSJ,'-1') as DOC_ESTADO_MSJ,(SELECT MAX(C.TI_DES) FROM IDOSGD.TDTV_DESTINOS C WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN  ) as tiDest,COALESCE(A.TI_ENV_MSJ,'-1') tiEnvMsj \n" 
                + "from IDOSGD.TDTV_REMITOS A "
                + "left join IDOSGD.TDTC_EXPEDIENTE B on A.NU_ANN_EXP = B.NU_ANN_EXP and A.NU_SEC_EXP = B.NU_SEC_EXP \n"
                + "left join IDOSGD.TDTX_REMITOS_RESUMEN RR ON RR.NU_ANN = A.NU_ANN AND RR.NU_EMI = A.NU_EMI \n"
                + "where A.NU_ANN = ? AND A.NU_EMI = ? ");

        DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{pnuAnn, pnuEmi });
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
        sql.append("select B.NU_EXPEDIENTE,B.FE_EXP,IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(B.CO_PROCESO) DE_PROCESO,TO_CHAR(B.FE_EXP,'DD/MM/YYYY') FE_EXP_CORTA,\n"
                + "B.NU_ANN_EXP,B.NU_SEC_EXP,B.CO_PROCESO\n"
                + "from IDOSGD.TDTC_EXPEDIENTE B \n"
                + "where \n"
                + "B.NU_ANN_EXP=? AND B.NU_SEC_EXP=?");

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
        String sql="select "
                + "A.CO_LOC CO_LOCAL,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(A.CO_LOC) DE_LOCAL,\n"
                + "B.CO_EMPLEADO CEMP_CODEMP,\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(B.CO_EMPLEADO) COMP_NAME\n"
                + "from IDOSGD.sitm_local_dependencia A,IDOSGD.rhtm_dependencia B\n"
                + "where B.CO_DEPENDENCIA = ? and A.CO_DEP=?";

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

        sql.append("SELECT e.cemp_apepat,e.cemp_apemat,e.cemp_denom, e.CEMP_CODEMP \n" +
                    "FROM IDOSGD.RHTM_PER_EMPLEADOS e, \n" +
                    "( \n" +
                    "SELECT CEMP_CODEMP \n" +
                    "FROM IDOSGD.RHTM_PER_EMPLEADOS \n" +
                    "where CEMP_EST_EMP = '1' \n" +
                    "  and (CO_DEPENDENCIA=? or CEMP_CO_DEPEND=?) \n" +
                    "union \n" +
                    "select co_emp from IDOSGD.tdtx_dependencia_empleado  where co_dep=? and es_emp='0' \n" +
                    "union \n" +
                    "select co_empleado from IDOSGD.rhtm_dependencia where co_dependencia = ? \n" +
                    ") a \n" +
                    "where e.cemp_codemp = a.cemp_codemp\n" +
                    "order by 1");


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

        sql.append("SELECT a.de_mot,\n"
                + "       a.co_mot\n"
                + "  FROM IDOSGD.tdtr_motivo         a,\n"
                + "       IDOSGD.tdtx_moti_docu_depe b\n"
                + " WHERE a.co_mot     = b.co_mot\n"
                + "   AND b.co_dep     = ?\n"
                + "   AND b.co_tip_doc = ?\n"
                + "UNION\n"
                + "SELECT de_mot,\n"
                + "       co_mot\n"
                + "FROM IDOSGD.tdtr_motivo  \n"
                + "where co_mot in ('0','1')\n"
                + " ORDER BY 1");


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
        sql.append(" FROM ( ");        
        
        sql.append("select *\n"
                + "from("
                + "select \n"
                + "RE.fe_emi,\n"
                + "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(RE.co_tip_doc_adm),1,100) DE_TIP_DOC_ADM,\n"
                + "RR.NU_DOC,\n"                
                + "to_char(RE.fe_emi,'DD/MM/YY') FE_EMI_CORTA,\n"
                + "null FE_REC_DOC_CORTA,\n"
                + "RE.NU_ANN,\n"
                + "RE.NU_EMI,\n"
                + "NULL NU_DES,\n"
                + "replace(ltrim(rtrim(RE.de_asu)),chr(10),' ') DE_ASU, \n"
                + "RE.CO_TIP_DOC_ADM,  \n"
                + "SUBSTR(RR.NU_EXPEDIENTE,1,20) NU_EXPEDIENTE,\n"                
                + "RE.NU_ANN_EXP,\n"
                + "RE.NU_SEC_EXP\n"
                + "from IDOSGD.TDTV_REMITOS RE,IDOSGD.TDTX_REMITOS_RESUMEN RR\n"
                + "where RE.nu_ann = ?\n"
                + "AND RE.NU_ANN=RR.NU_ANN\n"
                + "AND RE.NU_EMI=RR.NU_EMI\n"
                + "AND RE.es_eli = '0'\n"
                + "and RE.es_doc_emi not in ('9','7','5')\n"
                + "AND RE.CO_GRU = '1'\n"
                + "AND RE.co_dep_emi in \n"
                + "(select ?/*:b_02.co_dep_emi*/ \n"//from dual 
                + "union \n"
                + "  SELECT co_dep_ref\n"
                + "    FROM IDOSGD.tdtx_referencia\n"
                + "   WHERE co_dep_emi = ?/*:b_02.co_dep_emi*/\n"
                + "     AND ti_emi = 'D'\n"
                + "     AND es_ref = '1')\n"
                + "AND RE.co_tip_doc_adm = ?/*:B_03.LI_TIP_DOC*/\n");
                if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                    pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                    sql.append("AND (RE.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
                }
                sql.append("UNION\n"
                + "select \n"
                + "RE.fe_emi,\n"                        
                + "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(RE.co_tip_doc_adm),1,100) TIPO,\n"
                + "RR.NU_DOC numero,\n"
                + "to_char(RE.fe_emi,'DD/MM/YY') fecha_emision,\n"
                + "null fecha_recepcion,\n"
                + "RE.NU_ANN,\n"
                + "RE.NU_EMI,\n"
                + "NULL NU_DES,\n"
                + "replace(ltrim(rtrim(RE.de_asu)),chr(10),' ') ASUNTO, \n"
                + "RE.CO_TIP_DOC_ADM,  \n"
                + "SUBSTR(RR.NU_EXPEDIENTE,1,20) NU_EXPEDIENTE,\n"
                + "RE.NU_ANN_EXP,\n"
                + "RE.NU_SEC_EXP\n"
                + "from IDOSGD.TDTV_REMITOS RE,IDOSGD.TDTX_REMITOS_RESUMEN RR\n"
                + "where RE.nu_ann = ?\n"
                + "AND RE.NU_ANN=RR.NU_ANN\n"
                + "AND RE.NU_EMI=RR.NU_EMI\n"
                + "AND RE.es_eli = '0'\n"
                + "and RE.es_doc_emi not in ('9','7','5')\n"
                + "AND RE.CO_GRU = '2'\n"
                + "AND RE.co_tip_doc_adm = ?/*:B_03.LI_TIP_DOC*/\n");
                if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                    pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                    sql.append("AND (RE.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
                }
                sql.append("and RE.co_emp_emi = ?/*:GLOBAL.USER*/\n"
                + ") E\n"
                + "order by 1 desc");

	sql.append(") A ");
        sql.append(" LIMIT 200");                            

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
        StringBuffer sql = new StringBuffer();
         StringBuffer sql2 = new StringBuffer();
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();
        List<DocumentoBean> list2 = new ArrayList<DocumentoBean>();
        sql.append("select \n"+
                    "d.FE_REC_DOC,\n" +
                    "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(d.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "d.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(IDOSGD.PK_SGD_DESCRIPCION_de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "from IDOSGD.TDTV_REMITOS R,IDOSGD.TDTV_DESTINOS D,IDOSGD.TDTX_REMITOS_RESUMEN RR\n" +
                    "where\n" +
                    "r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +
                    "AND r.nu_ann = ?/*:b_03.NU_ANN_REF*/\n" +
                    "and rr.nu_ann=r.nu_ann\n" +
                    "AND rr.nu_emi = r.nu_emi\n" +
                    "AND d.nu_ann = r.nu_ann\n" +
                    "AND d.nu_emi = r.nu_emi\n" +
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and d.co_dep_des in \n" +
                    "(select ?/*:b_02.co_dep_emi*/ \n" +//from dual 
                    "union \n" +
                    "  SELECT co_dep_ref\n" +
                    "    FROM IDOSGD.tdtx_referencia\n" +
                    "   WHERE co_dep_emi = ?/*:b_02.co_dep_emi*/\n" +
                    "     AND ti_emi = 'D'\n" +
                    "     AND es_ref = '1')\n" +
                    "AND r.co_tip_doc_adm = ?/*:B_03.LI_TIP_DOC*/\n");
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
                    }                     
        sql.append(" LIMIT 200 ");    

         sql2.append( "select\n"+
                    "d.FE_REC_DOC,\n" +
                    "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_de_documento(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(d.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "d.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(IDOSGD.PK_SGD_DESCRIPCION_de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "from IDOSGD.TDTV_REMITOS R,IDOSGD.TDTV_DESTINOS D,IDOSGD.TDTX_REMITOS_RESUMEN RR\n" +
                    "where\n" +
                    "  r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +
                    "and rr.nu_ann=r.nu_ann\n" +
                    "AND rr.nu_emi = r.nu_emi\n" +
                    "AND d.nu_ann = r.nu_ann\n" +
                    "AND d.nu_emi = r.nu_emi\n" +
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and r.co_tip_doc_adm ='");
              sql2.append(ptiDoc);  
              sql2.append("' \n"+
                    "and (R.nu_ann||R.nu_emi) in ( \n" + 
                    "select  (REF.nu_ann_ref||REF.nu_emi_ref) as nurem from IDOSGD.TDTR_REFERENCIA REF where (REF.nu_ann||REF.nu_emi) in \n" +
                    "(select (R.nu_ann||R.nu_emi) as nurem \n" +
                    "from IDOSGD.TDTV_REMITOS R,IDOSGD.TDTV_DESTINOS D,IDOSGD.TDTX_REMITOS_RESUMEN RR \n" +
                    "where r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +
                    "AND r.nu_ann ='"+pannio+"'"); 
              sql2.append(" and rr.nu_ann=r.nu_ann\n" +
                    "AND rr.nu_emi = r.nu_emi\n" +
                    "AND d.nu_ann = r.nu_ann\n" +
                    "AND d.nu_emi = r.nu_emi\n" +
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and d.co_dep_des ='"+pcoDepen+"' "); 
   
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql2.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("%')\n");
                    }
                    
                    sql2.append(")) \n");
                    sql2.append(" LIMIT 200"); 
                    
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{pannio,pcoDepen, pcoDepen, ptiDoc});
             list2 = this.jdbcTemplate.query(sql2.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class));
        
            list.addAll(list2);
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    //@Override
    public List<DestinatarioDocumentoEmiBean> _getLstDestintariotlbEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,substr(IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(a.co_loc_des), 1, 100) de_local,\n"
                + "a.co_dep_des co_dependencia,substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_des), 1, 100) de_dependencia,\n"
                + "a.co_emp_des co_empleado,substr(IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des), 1, 100) de_empleado,\n"
                + "a.co_mot co_tramite,substr(IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(a.co_mot), 1, 100) de_tramite,\n"
                + "a.co_pri co_prioridad,\n"
                + "a.de_pro de_indicaciones\n"
                + "FROM IDOSGD.TDTV_DESTINOS a\n"
                + "where a.nu_ann = ? and a.nu_emi = ?\n"
                + "AND a.TI_DES = '01' AND a.ES_ELI='0' AND a.NU_EMI_REF is null\n"
                + "order by 3");

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
        
sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,COALESCE(substr(IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(a.co_loc_des), 1, 100),NULL) de_local,\n" +
                        "                a.co_dep_des co_dependencia,COALESCE(substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_des), 1, 100),NULL) de_dependencia,\n" +
                        "                a.co_emp_des co_empleado,COALESCE(substr(IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des), 1, 100),NULL) de_empleado,\n" +
                        "                a.co_mot co_tramite,COALESCE(substr(IDOSGD.PK_SGD_DESCRIPCION_MOTIVO(a.co_mot), 1, 100),NULL) de_tramite,\n" +
                        "                a.co_pri co_prioridad,\n" +
                        "                a.de_pro de_indicaciones,\n" +
                        "                a.NU_RUC_DES nu_ruc,COALESCE(substr(IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.NU_RUC_DES), 1, 100),NULL) de_proveedor,\n" +
                        "                a.NU_DNI_DES nu_dni,COALESCE(substr(IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.NU_DNI_DES), 1, 100),NULL) de_ciudadano,\n" +
                        "                a.CO_OTR_ORI_DES co_otro_origen,\n" +
                        "                COALESCE(de_otro_origen_full,NULL) de_otro_origen_full, a.ti_des co_tipo_destino, COALESCE(a.ES_ENV_POR_TRA,'0') ENV_MESA_PARTES,\n" +
                        "                CDIR_REMITE,CEXP_CORREOE,CTELEFONO,CCOD_DPTO,CCOD_PROV,CCOD_DIST,\n" +
                        "                trim(UB.NODEP)||' '||trim(UB.NOPRV)||' '||trim(UB.NODIS) ubigeo \n" +
                        "                ,A.REMI_TI_EMI, (CASE WHEN A.REMI_TI_EMI='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (A.REMI_NU_DNI_EMI) "
                      + "                         WHEN A.REMI_TI_EMI='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (A.REMI_CO_OTR_ORI_EMI) END)  nombres,  \n" +
                        "                  (CASE WHEN A.REMI_TI_EMI='03' THEN REMI_NU_DNI_EMI WHEN A.REMI_TI_EMI='04' THEN REMI_CO_OTR_ORI_EMI END) REMI_NU_DNI_EMI,DE_CARGO AS cargo \n"+
                        "                FROM IDOSGD.tdtv_destinos a\n" +
                        "                LEFT JOIN ( SELECT C.CO_OTR_ORI,C.DE_APE_PAT_OTR||' '||C.DE_APE_MAT_OTR||', '||C.DE_NOM_OTR || ' - ' ||\n" +
                        "                     C.DE_RAZ_SOC_OTR ||'##'||  COALESCE(B.CELE_DESELE,'   ') ||'##'||  C.NU_DOC_OTR_ORI  de_otro_origen_full\n" +
                        "                  FROM IDOSGD.TDTR_OTRO_ORIGEN C LEFT JOIN  (\n" +
                        "                  SELECT CELE_CODELE, CELE_DESELE  FROM IDOSGD.SI_ELEMENTO  WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B\n" +
                        "                  ON C.CO_TIP_OTR_ORI = B.CELE_CODELE ) OTRDES ON OTRDES.CO_OTR_ORI=a.CO_OTR_ORI_DES\n" +
                        "                LEFT JOIN IDOSGD.IDTUBIAS UB ON UB.UBDEP=a.CCOD_DPTO AND UB.UBPRV=a.CCOD_PROV AND UB.UBDIS=a.CCOD_DIST\n" +                         
                        "                where nu_ann = ? and nu_emi =? \n" +
                        "                AND ES_ELI='0' AND NU_EMI_REF is null\n" +
                        "                order by 3");

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
        sql.append("SELECT \n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (a.co_tip_doc_adm) de_tipo_doc, \n"
                + "CASE  WHEN a.ti_emi='01' THEN\n"
                + "a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig\n"
                + "WHEN a.ti_emi='05' THEN\n"
                + "a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig\n"
                +" ELSE a.de_doc_sig\n"
                + " END AS li_nu_doc," 
                + "substr(IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_emi), 1, 100) de_dep_emi, \n"
                + "TO_CHAR(a.fe_emi,'DD/MM/YY') fe_emi_corta, \n"
                + "b.nu_ann,\n"
                + "b.nu_emi,\n"
                + "COALESCE(trim(CAST(b.nu_des AS text)),'N') nu_des ,\n"
                + "b.nu_ann_ref,\n"
                + "b.nu_emi_ref,\n"
                + "COALESCE(trim(CAST(b.nu_des_ref AS text)),'N') nu_des_ref,\n"
                + "b.co_ref,\n" 
                + "CASE  WHEN COALESCE(trim(cast(b.nu_des_ref AS text)),'N')='N' THEN\n"
                + "'emi'\n"
                + "WHEN a.ti_emi='05' THEN\n"
                + "'rec'\n"
                + " END AS tip_doc_ref,"
                + "a.co_tip_doc_adm,'BD' accion_bd\n"
                + "FROM IDOSGD.tdtv_remitos a,IDOSGD.TDTR_REFERENCIA b \n"
                + "WHERE a.nu_ann = b.nu_ann_ref\n"
                + "AND a.nu_emi = b.nu_emi_ref\n"
                + "and b.nu_ann = ?\n"
                + "and b.nu_emi = ?\n");

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
//        sql.append("SELECT COALESCE(MAX(ti_des),'01')\n" +
        sql.append("SELECT MAX(ti_des)\n"
                + "/*INTO :b_02.li_ti_des*/\n"
                + "FROM IDOSGD.TDTV_DESTINOS\n"
                + "WHERE nu_emi = ?\n"
                + " AND nu_ann = ?"
                + " AND ES_ELI = '0'");

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
         
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=?,");
        if (documentoEmiBean != null) { 
            sqlUpd.append("DE_ASU=?\n"
                    + ",NU_DIA_ATE=?\n"
                    + ",CO_TIP_DOC_ADM=?\n"
                    + ",ES_DOC_EMI=?\n"
                    + ",NU_DOC_EMI=?\n"
                    + ",DE_DOC_SIG=?\n" 
                    + ",NU_COR_DOC=case when ? is not null then ? else (SELECT COALESCE(MAX(nu_cor_doc), 0) + 1\n"
                    + "FROM IDOSGD.tdtv_remitos\n"
                    + "WHERE nu_ann = ?\n"
                    + "AND co_dep_emi = ?\n"
                    + "AND co_tip_doc_adm = ?\n"
                    + "AND ti_emi = ?) end,\n");
        }
        if (expedienteBean != null) {
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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        documentoEmiBean.setTiEmi("01");
        StringBuffer sqlUpd = new StringBuffer();
        StringBuffer sqlQry = new StringBuffer();
        StringBuffer sqlQry1 = new StringBuffer();
        sqlQry.append("select lpad(cast(nextval('IDOSGD.SEC_REMITOS_NU_EMI') as text),10,'0');");
        sqlQry1.append("SELECT COALESCE(MAX(nu_cor_emi), 0) + 1\n"
                + "FROM IDOSGD.tdtv_remitos\n"
                + "WHERE nu_ann = ?\n"
                + "AND co_dep_emi = ?\n"
                + "AND ti_emi = '01'");

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
                + "FE_USE_MOD,\n"
                 + "OBS_DOC,\n"
                + "COD_VER_EXT)\n" 
                + "VALUES(?,?,?,?,?,?,?,?,CURRENT_TIMESTAMP,'1',?,?,?,'03',?,\n"
                + "?,?,?,?,1,\n"
                + "'0',?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?,?)");
        
        try {
            String snuEmi = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);
            long snuCorEmi = this.jdbcTemplate.queryForObject(sqlQry1.toString(), Long.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getCoDepEmi()});
            documentoEmiBean.setNuEmi(snuEmi);
            documentoEmiBean.setNuCorEmi(String.valueOf(snuCorEmi));
            //documentoEmiBean.setNuCorDoc(vnuCorDoc);

            documentoEmiBean.setCoVerExt(AleatorioCodVerificacion.generaCodVerificacion());
            
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getNuAnn(), snuEmi, snuCorEmi,
                documentoEmiBean.getCoLocEmi(), documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoEmpEmi(), documentoEmiBean.getCoEmpRes(),
                documentoEmiBean.getTiEmi(), documentoEmiBean.getDeAsu(), documentoEmiBean.getEsDocEmi(), Integer.parseInt(documentoEmiBean.getNuDiaAte()),documentoEmiBean.getCoTipDocAdm(),
                Integer.parseInt(documentoEmiBean.getNuCorDoc()), documentoEmiBean.getDeDocSig(),documentoEmiBean.getNuAnnExp(), documentoEmiBean.getNuSecExp(), 
                documentoEmiBean.getNuDocEmi(),documentoEmiBean.getCoUseMod(), documentoEmiBean.getCoUseMod(),documentoEmiBean.getDeObsDoc(), documentoEmiBean.getCoVerExt()});

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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
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
                + "VALUES(?,?,(select COALESCE(MAX(a.nu_des) + 1,1) FROM IDOSGD.TDTV_DESTINOS a where \n"
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
            //con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();

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
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + ",CO_USE_MOD=?\n"
                + ",ES_ENV_POR_TRA=?\n"
                 + ",CDIR_REMITE=?\n"
                + ",CEXP_CORREOE=?\n"
                + ",CTELEFONO=?\n"
                + ",CCOD_DPTO=?\n"
                + ",CCOD_PROV=?\n"
                + ",CCOD_DIST=?\n" 
                + ",REMI_TI_EMI=?\n"
                + ",REMI_NU_DNI_EMI=?\n"
                + ",REMI_CO_OTR_ORI_EMI=? \n"
                + ",DE_CARGO=? \n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=? and\n"
                + "NU_DES= cast(? as bigint)");
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
            
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{destinatarioDocumentoEmiBean.getCoLocal(), destinatarioDocumentoEmiBean.getCoDependencia(),
                destinatarioDocumentoEmiBean.getCoEmpleado(), destinatarioDocumentoEmiBean.getCoPrioridad(), destinatarioDocumentoEmiBean.getDeIndicaciones(), destinatarioDocumentoEmiBean.getCoTramite(),
                destinatarioDocumentoEmiBean.getCoOtroOrigen(), destinatarioDocumentoEmiBean.getNuDni(), destinatarioDocumentoEmiBean.getNuRuc(),
                destinatarioDocumentoEmiBean.getCoUseCre(), destinatarioDocumentoEmiBean.getEnvMesaPartes(),
                destinatarioDocumentoEmiBean.getCdirRemite(),destinatarioDocumentoEmiBean.getCexpCorreoe(),destinatarioDocumentoEmiBean.getcTelefono(),
               dep,pro,dis,destinatarioDocumentoEmiBean.getRemiTiEmi(),destinatarioDocumentoEmiBean.getRemiNuDniEmi(),destinatarioDocumentoEmiBean.getRemiCoOtrOriEmi(),destinatarioDocumentoEmiBean.getCargo(),
                nuAnn, nuEmi, destinatarioDocumentoEmiBean.getNuDes()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado Destino.";
            //con.printStackTrace();
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
                + "and NU_DES= cast(? as bigint)");
//        sqlUpd.append("update IDOSGD.TDTV_DESTINOS \n"
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
        try {
            
        String nu_des_ref=(referenciaEmiDocBean.getNuDes()!= null && !referenciaEmiDocBean.getNuDes().isEmpty())?referenciaEmiDocBean.getNuDes():"NULL";
        sqlUpd.append("INSERT INTO IDOSGD.TDTR_REFERENCIA(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + /*"NU_DES,\n" +*/ "CO_REF,\n"
                + "NU_ANN_REF,\n"
                + "NU_EMI_REF,\n"
                + "NU_DES_REF,\n"
                + "CO_USE_CRE,\n"
                + "FE_USE_CRE)\n"
                + //                    "VALUES(?,?,(SELECT lpad(SEC_REFERENCIA_CO_REF.NextVal, 10, '0')\n" +
                //                    "FROM DUAL),?,?,'ADM',CURRENT_TIMESTAMP)");
                " VALUES(?,?,lpad(cast(nextval('idosgd.sec_referencia_co_ref') as text), 10, '0'),?,?"+(nu_des_ref.equals("NULL")?",NULL":",?")+",?,CURRENT_TIMESTAMP)");
        
        
            Object[] object;
            if(nu_des_ref.equals("NULL")){
                object= new Object[]{nuAnn, nuEmi, referenciaEmiDocBean.getNuAnn(), referenciaEmiDocBean.getNuEmi(),referenciaEmiDocBean.getCoUseCre()};
            }else{
                object= new Object[]{nuAnn, nuEmi, referenciaEmiDocBean.getNuAnn(), referenciaEmiDocBean.getNuEmi(),
                Integer.parseInt(referenciaEmiDocBean.getNuDes()), referenciaEmiDocBean.getCoUseCre()};
            }
            
            this.jdbcTemplate.update(sqlUpd.toString(),object );
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
        
        try {
        String nu_des_ref=(referenciaEmiDocBean.getNuDes()!= null && !referenciaEmiDocBean.getNuDes().isEmpty())?referenciaEmiDocBean.getNuDes():"NULL";
        sqlUpd.append("update IDOSGD.TDTR_REFERENCIA \n"
                + "set NU_ANN_REF = ?,\n"
                + "NU_EMI_REF = ?,\n"
                + "NU_DES_REF = "+(nu_des_ref.equals("NULL")?"NULL":referenciaEmiDocBean.getNuDes())+" \n"
                + " WHERE NU_ANN = ? AND NU_EMI = ? \n"
                + "AND CO_REF = ?");
        
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{referenciaEmiDocBean.getNuAnn(), referenciaEmiDocBean.getNuEmi(), nuAnn, nuEmi, referenciaEmiDocBean.getCoRef()});
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
        sqlIns.append("DELETE FROM IDOSGD.TDTR_REFERENCIA\n"
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
        sql.append("select IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA(?) de_doc_sig,\n"
                + "CO_EMPLEADO co_emp_emi,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(CO_EMPLEADO) de_emp_emi,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (?,'TDTV_REMITOS') de_es_doc_emi,'0' existe_doc,'0' existe_anexo,\n"
                + "? co_es_doc_emi \n"
                + "from IDOSGD.rhtm_dependencia\n"
                + "where CO_DEPENDENCIA = ?");

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
        sqlQry1.append("select LPAD(TRIM(CAST((CAST(COALESCE(max(NU_CORR_EXP),'0')AS int)+1) AS text)),7,'0') nuCorrExp, \n"
                //+ " substr(IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(?),1,6) deSiglaCorta\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(?) deSiglaCorta\n"
                + "from IDOSGD.tdtc_expediente\n"
                + "where nu_ann_exp = ?\n"
                + "and co_dep_exp = ?\n"
                + "and co_gru     = '1'");

        StringBuffer sqlQry2 = new StringBuffer();
        sqlQry2.append("select LPAD(TRIM(cast((cast(COALESCE(max(nu_sec_exp),'0')as int) +1) as text)),10,'0') \n"
                + "from IDOSGD.tdtc_expediente\n"
                + "where nu_ann_exp = ?");

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
                + "VALUES(?,?,(select to_date(?,'DD/MM/YYYY')),?,?,?,'1',?,?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,'0')");

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
        sqlQry.append("SELECT COALESCE(MAX(es_doc_rec),'0')\n"
                + "FROM IDOSGD.TDTV_DESTINOS\n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?\n"
                + "AND es_eli = '0'");

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
        sqlQry.append("SELECT COALESCE(MAX(nu_cor_doc), 1) + 1\n"
                + "FROM IDOSGD.tdtv_remitos\n"
                + "WHERE nu_ann         = ?\n"
                + "AND ti_emi         = ?\n"
                + "AND co_dep_emi     = ?\n"
                + "AND co_tip_doc_adm = ?\n"
                + "AND (  (? <>'null' AND nu_doc_emi = ?)\n"
                + "OR ? ='null'\n"
                + ")");

        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set es_doc_emi = ?,\n"
                + "nu_cor_doc = ?,fe_use_mod=CURRENT_TIMESTAMP,co_use_mod=? \n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?\n"
                + "AND es_eli = '0'");

        String nuDocEmi= (documentoEmiBean.getNuDocEmi()==null)?"null":documentoEmiBean.getNuDocEmi();
        try {
            String snuCorDoc = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getTiEmi(),
                documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm(), nuDocEmi, nuDocEmi, nuDocEmi});

            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getEsDocEmi(), Integer.parseInt(snuCorDoc), documentoEmiBean.getCoUseMod(),
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
 
        sql.append("SELECT a.co_dep co_dependencia,IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep) de_dependencia,\n" +
                    "    COALESCE(a.co_emp, b.co_empleado) co_empleado,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(COALESCE(a.co_emp, b.co_empleado)) de_empleado,\n" +
                    "    c.co_loc co_local,IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL(c.co_loc) de_local,'1' co_prioridad,\n" +
                    "    ? co_tramite_first, ? de_tramite_first, ? co_tramite_next, ? de_tramite_next \n" +
                    "    FROM (   \n" +
                    "      select y.cemp_codemp co_emp,  x.co_dep\n" +//y.oid,
                    "      from IDOSGD.rhtm_per_empleados y right join IDOSGD.tdtd_dep_gru x  on (y.cemp_codemp  = x.co_emp and y.cemp_est_emp='1') \n" +
                    "      where \n" +
                    "      x.co_gru_des= ?\n" +
                    "      and x.es_dep_gru = '1'\n" + 
                    "      \n" +//order by y.oid
                    "    ) a , IDOSGD.rhtm_dependencia b, IDOSGD.sitm_local_dependencia c\n" +
                    "    WHERE a.co_dep = b.co_dependencia\n" +
                    "    AND a.co_dep = c.co_dep\n" +
                    "    ");//order by a.oid

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
 
        sqlUpd.append("UPDATE IDOSGD.TDTV_ARCHIVO_DOC SET BL_DOC = NULL, DE_RUTA_ORIGEN = NULL, FEULA=TO_CHAR(CURRENT_TIMESTAMP,'YYYYMMDD') \n"
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
    public String updDocumentoObj(final DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
        boolean inInsert = false;
        /*Verificamos si es Insert o Update*/
        try {
            int cant = this.jdbcTemplate.queryForInt("select count(nu_ann) from IDOSGD.tdtv_archivo_doc where nu_ann = ? and nu_emi = ?", new Object[]{docObjBean.getNuAnn(), docObjBean.getNuEmi()});
            if (cant > 0) {
                inInsert = false;
            } else {
                inInsert = true;
            }

        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }

        /**/

        final LobHandler lobhandler = new DefaultLobHandler();
        if (inInsert) {
            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO IDOSGD.tdtv_archivo_doc (NU_ANN,NU_EMI,BL_DOC,DE_RUTA_ORIGEN,ES_FIRMA,FEULA,W_BL_DOC,W_DE_RUTA_ORIGEN)\n"
                    + "VALUES(?,?,?,?,'0',TO_CHAR(CURRENT_TIMESTAMP,'YYYYMMDD'),?,?)");
            try {
                this.jdbcTemplate.execute(sql.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                            throws SQLException {
                        ps.setString(1, docObjBean.getNuAnn());
                        ps.setString(2, docObjBean.getNuEmi());
                        lobCreator.setBlobAsBytes(ps, 3, docObjBean.getDocumento());
                        ps.setString(4, docObjBean.getNombreArchivo());
                        lobCreator.setBlobAsBytes(ps, 5, docObjBean.getDocumento());
                        ps.setString(6, docObjBean.getNombreArchivo());
                    }
                });
                vReturn = "OK";
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
             StringBuffer sql1 = new StringBuffer();
            StringBuffer sql2 = new StringBuffer();
            
            try {
                 if(docObjBean.getNombreArchivo().toUpperCase().indexOf(".DOCX")>=0)
                    {
                    sql1.append("UPDATE IDOSGD.TDTV_ARCHIVO_DOC SET W_BL_DOC = ?, W_DE_RUTA_ORIGEN = ? \n"
                    + "WHERE NU_ANN = ? \n"
                    + "AND NU_EMI = ? ");
                    
                    this.jdbcTemplate.execute(sql1.toString(),
                        new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                    protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                        throws SQLException {
                        lobCreator.setBlobAsBytes(ps, 1, docObjBean.getDocumento());
                        ps.setString(2, docObjBean.getNombreArchivo());                       
                        ps.setString(3, docObjBean.getNuAnn());
                        ps.setString(4, docObjBean.getNuEmi());
                        }
                    });                                                        
                    }
                 
                 sql2.append("UPDATE IDOSGD.TDTV_ARCHIVO_DOC SET BL_DOC = ?, DE_RUTA_ORIGEN = ?, FEULA=TO_CHAR(CURRENT_TIMESTAMP,'YYYYMMDD')\n"
                    + "WHERE NU_ANN = ? \n"
                    + "AND NU_EMI = ? ");
                     
                this.jdbcTemplate.execute(sql2.toString(),
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
                vReturn = e.getMessage().substring(0, 20);
            }
        }

        return vReturn;
    }

    @Override
    public String getCanDocumentoEmiDuplicados(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        try {
            vReturn = this.jdbcTemplate.queryForObject("   SELECT COUNT(1)\n"
                    + "     FROM IDOSGD.tdtv_remitos tr\n"
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

        sql.append("SELECT e.cemp_apepat,e.cemp_apemat,e.cemp_denom, e.CEMP_CODEMP\n"
                + "FROM IDOSGD.RHTM_PER_EMPLEADOS e\n"
                + "WHERE e.CEMP_EST_EMP = '1'\n"
                + "AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA from IDOSGD.rhtm_dependencia d where d.CO_DEPENDENCIA =? or CO_DEPEN_PADRE=?) \n"
                + "OR CO_DEPENDENCIA=? )\n"
                + "UNION\n"
                + "SELECT e.cemp_apepat,e.cemp_apemat,e.cemp_denom, e.CEMP_CODEMP\n"
                + "FROM IDOSGD.RHTM_PER_EMPLEADOS e\n"
                + "WHERE e.CEMP_EST_EMP = '1'\n"
                + "AND cemp_codemp \n"
                + "in (\n"
                + "select co_emp from IDOSGD.tdtx_dependencia_empleado  where co_dep=? and es_emp='0'\n"
                + "union select CO_EMPLEADO from IDOSGD.rhtm_dependencia  where co_dependencia=?\n"
                + " )\n"
                + "ORDER BY 1");
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
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=? \n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP \n"
                + ",FE_EMI=CURRENT_TIMESTAMP \n"
                + ",NU_COR_DOC=CAST(? AS int) \n"
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
                    + ",NU_DIA_ATE=CAST(? AS INT)\n"
                    + ",CO_TIP_DOC_ADM=?\n"
                    + ",NU_DOC_EMI=?\n"
                    + ",DE_DOC_SIG=?\n"
                    + ",NU_COR_DOC=CAST(? AS INT)\n"
                     + ",OBS_DOC= ?,\n"
                    /*(SELECT COALESCE(MAX(nu_cor_doc), 0) + 1\n"
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
                sqlUpd.append("NU_COR_EMI=" + documentoEmiBean.getNuCorEmi()+ ",\n");
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
                    /*documentoEmiBean.getNuDocEmi(), documentoEmiBean.getNuCorDoc(),*/documentoEmiBean.getNuCorDoc(), documentoEmiBean.getDeObsDoc(),/*, nuAnn, documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm(),
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
            sql.append("         SELECT TRIM(CAST(COALESCE(MAX(cast (TR.nu_doc_emi as int)), 0) + 1 AS TEXT)) \n"
                    + "           FROM IDOSGD.tdtv_remitos tr\n"
                    + "          WHERE tr.nu_ann = ?\n"
                    + "            AND tr.co_dep_emi = ?\n"
                    + "            AND tr.co_tip_doc_adm = ?\n"
                    + "            AND tr.ti_emi = '01'\n"
                    + "            AND tr.nu_cor_doc = 1\n"
                    //+ "            AND tr.es_doc_emi != '9'\n"
                    + "            AND (nu_doc_emi ~ '^([0-9]+[.]?[0-9]*|[.][0-9]+)$')");
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
        sql.append("select \n"
                + "nu_ann, \n"
                + "nu_emi, \n"
                + "de_ruta_origen nombre_Archivo,\n" 
                + "UPPER(right(de_ruta_origen,3 )) tipo_doc \n "
                + "from IDOSGD.tdtv_archivo_doc \n"
                + "where nu_ann = ?\n"
                + "and nu_emi = ?");


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
        sqlQry.append("SELECT COALESCE(MAX(nu_cor_doc), 0) + 1\n"
                + "FROM IDOSGD.tdtv_remitos\n"
                + "WHERE nu_ann       = ?\n"
                + "AND ti_emi         = ?\n"
                + "AND co_dep_emi     = ?\n"
                + "AND co_tip_doc_adm = ?");
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
        sqlQry.append("SELECT COALESCE(MAX(nu_cor_doc), 1) + 1\n"
                + "FROM IDOSGD.tdtv_remitos\n"
                + "WHERE nu_ann       = ?\n"
                + "AND ti_emi         = ?\n"
                + "AND co_dep_emi     = ?\n"
                + "AND co_tip_doc_adm = ?");
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
        sqlDel.append("DELETE FROM IDOSGD.TDTR_REFERENCIA\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ?");

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
        sqlDel.append("DELETE FROM IDOSGD.TDTV_DESTINOS\n"
                + "WHERE NU_ANN = ? AND NU_EMI = ?");

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

        sqlUpd.append("update IDOSGD.tdtv_remitos \n"
                + "set CO_USE_MOD=?\n"
                + ",ES_ELI='1',NU_DOC_EMI=?,NU_COR_DOC= cast(? as bigint) ,NU_ANN_EXP=?,NU_SEC_EXP=?,NU_DET_EXP=?\n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
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
//        sql.append("   FROM dual");
        if (vfiltro) {
            sql.append(" where ' [TODOS]' like '%'||?||'%'");
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
        sql.append("SELECT DISTINCT co_dep_des, ti_des ");
        sql.append("FROM IDOSGD.TDTV_DESTINOS ");
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
        sql.append(" SELECT ti_des,\n"
                + "CASE \n"
                + "	WHEN ti_des='02' THEN  IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(nu_ruc_des)\n"
                + "	WHEN ti_des='03' THEN  IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(nu_dni_des)\n"
                + "	WHEN ti_des='04' THEN  IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(co_otr_ori_des)\n"
                + "	ELSE  ' ' END AS  co_dep_des "
                + " FROM IDOSGD.TDTV_DESTINOS \n"
                + " where nu_ann = ?\n"
                + " and nu_emi = ?\n"
                + " and es_eli = '0' \n"
                + " ORDER BY ti_des ");
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
        sql.append("FROM   IDOSGD.TDTR_REFERENCIA a, IDOSGD.tdtv_remitos b ");
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
        sql.append(" SELECT b.ti_emi, \n" + 
                    " CASE WHEN b.ti_emi='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(b.nu_ruc_emi) \n" +
                    " WHEN b.ti_emi='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(b.nu_dni_emi) \n" +
                    " WHEN b.ti_emi='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(b.co_otr_ori_emi) ELSE ' ' END AS co_dep_emi\n" +
                    " FROM   IDOSGD.TDTR_REFERENCIA a, IDOSGD.tdtv_remitos b \n" +
                    " WHERE  a.nu_ann_ref = b.nu_ann \n" +
                    " AND    a.nu_emi_ref = b.nu_emi \n" +
                    " AND    a.nu_ann = ? \n" +
                    " AND    a.nu_emi = ? \n" +
                    " ORDER  BY b.ti_emi");
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
        sql.append("FROM IDOSGD.TDTV_DESTINOS ");
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
        sqlQry.append("FROM IDOSGD.TDTV_DESTINOS ");
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
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_DES = ?,CO_PRIORIDAD = ?,nu_candes=CAST(? AS int) ");
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
        }
        return vReturn;

    }

    public String updRemitoResumenReferencia(String pnuAnn, String pnuEmi, String vti_ori, String vdeOriEmi) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_REF = ? ");
        sqlUpd.append("WHERE NU_ANN = ? ");
        sqlUpd.append("AND NU_EMI = ?");

        StringBuffer sqlUpd2 = new StringBuffer();
        sqlUpd2.append("UPDATE IDOSGD.TDTV_REMITOS SET DE_ORI_EMI = ?\n" +
                        "WHERE NU_ANN = ? \n" +
                        "AND NU_EMI = ? ");
        
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
        }
        return vReturn;
    }
    @Override
    public DocumentoEmiBean existeDocumentoReferenciado(BuscarDocumentoEmiBean buscarDocumentoEmiBean){
        StringBuffer sql = new StringBuffer();
        DocumentoEmiBean documentoEmiBean = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_DOC FROM IDOSGD.TDTV_REMITOS A\n" +
                    "WHERE A.NU_ANN=?\n" +
                    "AND A.CO_DEP_EMI=?\n" +
                    "AND A.TI_EMI='01'\n" +
                    "AND A.CO_TIP_DOC_ADM=?\n" +
                    "AND A.NU_DOC_EMI=?\n" +
                    "AND A.ES_ELI='0'\n" +
                    "AND A.ES_DOC_EMI NOT IN ('5','7','9')");        
        
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
        sql.append(" FROM ( ");        
        sql.append(" SELECT ");        
        sql.append(" DISTINCT B.NU_COR_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_EMI_REF(B.NU_ANN, B.NU_EMI) DE_EMI_REF,B.FE_EMI,");
        sql.append(" TO_CHAR(B.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,");
        sql.append(" (SELECT CDOC_DESDOC FROM IDOSGD.SI_MAE_TIPO_DOC WHERE CDOC_TIPDOC = B.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" CASE WHEN B.NU_CANDES=1 THEN IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP(B.NU_ANN, B.NU_EMI) ELSE IDOSGD.PK_SGD_DESCRIPCION_TI_DES_EMP_V(B.NU_ANN, B.NU_EMI) END AS DE_EMP_PRO,");
        sql.append(" (SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM IDOSGD.RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = B.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" (SELECT DE_EST FROM IDOSGD.TDTR_ESTADOS WHERE CO_EST || DE_TAB = B.ES_DOC_EMI || 'TDTV_REMITOS') DE_ES_DOC_EMI,");
        sql.append(" C.NU_DOC,UPPER(B.DE_ASU) DE_ASU_M,B.NU_ANN,C.NU_EXPEDIENTE,B.NU_EMI,B.TI_CAP,C.IN_EXISTE_DOC EXISTE_DOC,"
                + " CASE WHEN (COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2'))='00' THEN 0 "
                + " WHEN (COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2'))='02' THEN 0 "
                + " ELSE 1 END AS EXISTE_ANEXO, "
                + " COALESCE(C.CO_PRIORIDAD,'1') CO_PRIORIDAD,");
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
            objectParam.put("pcoEmpRes", documentoEmiBean.getCoEmpRes());           
        }
        objectParam.put("pCoAnio", documentoEmiBean.getNuAnn());           
        objectParam.put("pNuEmi", documentoEmiBean.getNuEmi());   
        objectParam.put("pCoDepEmi", documentoEmiBean.getCoDepEmi());   
        
        sql.append(" ORDER BY B.NU_COR_EMI DESC");
        sql.append(") A ");
        sql.append("LIMIT 100");            
        
        try {
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
    public TblRemitosBean getDatosDocumento(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        TblRemitosBean tblRemitosBean = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.CO_DEP_EMI,A.FE_EMI FROM IDOSGD.TDTV_REMITOS A\n" +
                   "WHERE A.NU_ANN=? AND A.NU_EMI=? AND A.ES_ELI='0'");        
        
        try {
             tblRemitosBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(TblRemitosBean.class),
                    new Object[]{pnuAnn,pnuEmi});
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
        sql.append("SELECT CO_EMP_EMI\n" +
                    "FROM IDOSGD.TDTV_REMITOS\n" +
                    "WHERE nu_emi = ?\n" +
                    "AND nu_ann = ?");

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
        sql.append("SELECT CO_EMP_EMI,ES_DOC_EMI,NU_ANN,NU_EMI,TI_EMI,CO_DEP_EMI,CO_TIP_DOC_ADM,NU_DOC_EMI,CO_EMP_RES\n" +
        "FROM IDOSGD.TDTV_REMITOS\n" +
        "WHERE nu_ann = ?\n" +
        "AND nu_emi = ? AND ES_ELI='0'");        
        
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
    public String getNroCorrelativoEmision(DocumentoEmiBean documentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "0";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("SELECT COALESCE(MAX(nu_cor_emi), 0) + 1\n"
                + "FROM IDOSGD.tdtv_remitos\n"
                + "WHERE nu_ann = ?\n"
                + "AND co_dep_emi = ?\n"
                + "AND ti_emi = ?");
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
        StringBuffer sqlUpd = new StringBuffer();

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

        }
        return vReturn;
    }
      @Override
    public String updEstadoDocumentoEnvioNotificacion(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK"; 
        StringBuffer sqlUpd = new StringBuffer(); 
        StringBuffer sqlUpd2 = new StringBuffer();  
        sqlUpd.append("UPDATE IDOSGD.TDTV_REMITOS \n"
                + "set CO_USE_MOD=?\n"
                + ",DOC_ESTADO_MSJ=?\n"
                + ",COD_DEP_MSJ=?\n"
                + ",FE_USE_MOD=CURRENT_TIMESTAMP\n"
                +",FE_ENV_MES=CURRENT_TIMESTAMP\n"
                + ",TI_ENV_MSJ=?\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        
        sqlUpd2.append("UPDATE IDOSGD.TDTV_DESTINOS \n"
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
 
        sql.append("SELECT A.*");
        sql.append(" FROM ( ");         
        sql.append("select * from(  select\n"+
                    "d.FE_REC_DOC,\n" +
                    "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_de_documento(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(d.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "d.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(IDOSGD.PK_SGD_DESCRIPCION_de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "from IDOSGD.TDTV_REMITOS R,IDOSGD.TDTV_DESTINOS D,IDOSGD.TDTX_REMITOS_RESUMEN RR\n" +
                    "where\n" +
                    "r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +
                    "AND r.nu_ann = ?/*:b_03.NU_ANN_REF*/\n" +
                    "and rr.nu_ann=r.nu_ann\n" +
                    "AND rr.nu_emi = r.nu_emi\n" +
                    "AND d.nu_ann = r.nu_ann\n" +
                    "AND d.nu_emi = r.nu_emi\n" +
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and d.co_dep_des in \n" +
                    "(select ? /*:b_02.co_dep_emi*/   \n" +
                    "union \n" +
                    "  SELECT co_dep_ref\n" +
                    "    FROM IDOSGD.tdtx_referencia\n" +
                    "   WHERE co_dep_emi = ?/*:b_02.co_dep_emi*/\n" +
                    "     AND ti_emi = 'D'\n" +
                    "     AND es_ref = '1')\n" +
                    "AND r.co_tip_doc_adm = ?/*:B_03.LI_TIP_DOC*/\n");
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("%')\n");
                    }
                    
                    sql.append("UNION \n" +
                            
                    "select\n"+
                    "d.FE_REC_DOC,\n" +
                    "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_de_documento(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(d.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "d.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(IDOSGD.PK_SGD_DESCRIPCION_de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "from IDOSGD.TDTV_REMITOS R,IDOSGD.TDTV_DESTINOS D,IDOSGD.TDTX_REMITOS_RESUMEN RR\n" +
                    "where\n" +
                    "r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +
                    "and rr.nu_ann=r.nu_ann\n" +
                    "AND rr.nu_emi = r.nu_emi\n" +
                    "AND d.nu_ann = r.nu_ann\n" +
                    "AND d.nu_emi = r.nu_emi\n" +
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and (R.nu_ann||R.nu_emi) in ( \n" + 
                    "select  (REF.nu_ann_ref||REF.nu_emi_ref) as nurem from IDOSGD.TDTR_REFERENCIA REF where (REF.nu_ann||REF.nu_emi) in \n" +
                    "(select (R.nu_ann||R.nu_emi) as nurem \n" +
                    "from IDOSGD.TDTV_REMITOS R,IDOSGD.TDTV_DESTINOS D,IDOSGD.TDTX_REMITOS_RESUMEN RR \n" +
                    "where\n" +
                    "r.es_eli = '0'\n" +
                    "AND d.es_eli = '0'\n" +
                    "AND r.nu_ann = ?/*:b_03.NU_ANN_REF*/\n" +
                    "and rr.nu_ann=r.nu_ann\n" +
                    "AND rr.nu_emi = r.nu_emi\n" +
                    "AND d.nu_ann = r.nu_ann\n" +
                    "AND d.nu_emi = r.nu_emi\n" +
                    "AND r.es_doc_emi NOT IN ('5', '9', '7')\n" +
                    "and d.es_doc_rec <> '0'  \n" +
                    "and d.co_dep_des = ? \n" );
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("%')\n");
                    }
                    
                    sql.append(")) UNION \n" +
                    "SELECT\n" +
                    "DE.FE_REC_DOC,\n" +
                    "SUBSTR(IDOSGD.PK_SGD_DESCRIPCION_de_documento(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(DE.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "DE.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(IDOSGD.PK_SGD_DESCRIPCION_de_dependencia(DE.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "FROM IDOSGD.TDTV_DESTINOS DE,IDOSGD.TDTV_REMITOS R,IDOSGD.TDTX_REMITOS_RESUMEN RR\n" +
                    "WHERE R.NU_ANN = ?\n" +
                    "AND DE.NU_ANN=R.NU_ANN\n" +
                    "AND RR.NU_ANN=R.NU_ANN\n" +
                    "AND DE.NU_EMI=RR.NU_EMI\n" +
                    "AND R.NU_EMI=RR.NU_EMI\n" +
                    "AND R.es_eli = '0'\n" +
                    "AND DE.ES_ELI='0'\n" +
                    "and R.es_doc_emi not in ('9','7','5')\n" +
                    "AND R.CO_GRU = '1'\n" +
                    "AND DE.ES_ENV_POR_TRA='1'\n" +
                    "AND r.co_tip_doc_adm = ?\n");
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("%')\n");
                    } 
                    sql.append(") E\n"+
                    "order by 1 desc");
                    
	sql.append(") A ");
        sql.append(" LIMIT 200 ");  
        
        
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{pannio,pcoDepen, pcoDepen, ptiDoc, pannio,pcoDepen,pannio, ptiDoc});
        } catch (EmptyResultDataAccessException e) {
            list = null;
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
        sql.append("SELECT CO_EMP_EMI,ES_DOC_EMI,NU_ANN,NU_EMI,TI_EMI,CO_DEP_EMI,CO_TIP_DOC_ADM,NU_DOC_EMI\n" +
        "FROM IDOSGD.TDTV_REMITOS\n" +
        "WHERE nu_ann = ?\n" +
        "AND nu_emi = ?");        
        
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
            vReturn = "Datos Duplicado INSERT TDTV_PERSONAL_VB.";
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
 
        
            sql.append("SELECT a.co_dep co_dependencia,b.DE_CORTA_DEPEN de_dependencia,\n" +
                    "    COALESCE(a.co_emp, b.co_empleado) co_empleado,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(COALESCE(a.co_emp, b.co_empleado)) de_empleado\n" +
                    "    FROM (   \n" +
                    "      select y.cemp_codemp co_emp,  x.co_dep\n" +
                    "      from IDOSGD.rhtm_per_empleados y RIGHT OUTER JOIN IDOSGD.tdtd_dep_gru x  on (y.cemp_codemp  = x.co_emp and y.cemp_est_emp='1') \n" +
                    "      where \n" +
                    "      x.co_gru_des= ?\n" +
                    "      and x.es_dep_gru = '1'\n" +
                    "    ) a , IDOSGD.rhtm_dependencia b, IDOSGD.sitm_local_dependencia c\n" +
                    "    WHERE a.co_dep = b.co_dependencia\n" +
                    "    AND a.co_dep = c.co_dep \n" +
                    "    order by 1 ");

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
         
        this.spActualizaEstado = new SimpleJdbcCall(this.dataSource)
                .withSchemaName("idosgd")
                .withFunctionName("PK_SGD_MENSAJERIA_actualiza_estado_a")  
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
        return vReturn;   
    }

    @Override
    public int getCantidadAnexo(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "0";
        StringBuffer sqlQry = new StringBuffer();
        sqlQry.append("select sum(COALESCE(B.nu_des,'0'))\n" +                   
                    "FROM IDOSGD.tdtv_destinos A LEFT JOIN IDOSGD.tdtv_anexos B \n" +
                    "ON A.nu_ann=B.nu_ann AND A.nu_emi=B.nu_emi AND A.nu_des=B.nu_des \n" +
                    "where A.nu_ann = ? \n" +             
                    "and A.nu_emi = ? \n");
        try {
            vReturn = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()}); 
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

        
        this.spInsMesaVirtual = new SimpleJdbcCall(this.dataSource)
                .withSchemaName("idosgd")
                .withFunctionName("pk_sgd_mesa_virtual")  
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
    public List<DestinatarioDocumentoEmiBean> getLstDestExternoGrupo(String pcoGrupo,String pTiDes) {
         StringBuffer sql = new StringBuffer();
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();

        if(pTiDes.equals("02"))
        {
            
            sql.append("SELECT CPRO_RUC NURUC, CPRO_RAZSOC deProveedor,COALESCE(CPRO_DOMICIL,' ') cdirRemite,COALESCE(CPRO_TELEFO,' ') cTelefono,COALESCE(CPRO_EMAIL,' ') cexpCorreoe, \n" +
                        "COALESCE(TRIM(NODEP)||' '||TRIM(NOPRV)||' '||TRIM(NODIS),' ') UBIGEO, COALESCE(TRIM(CUBI_CODDEP),' ') CCOD_DPTO,COALESCE(TRIM(CUBI_CODPRO),' ') CCOD_PROV,COALESCE(TRIM(CUBI_CODDIS),' ') CCOD_DIST\n" +
                        "FROM IDOSGD.LG_PRO_PROVEEDOR J LEFT JOIN IDOSGD.IDTUBIAS U ON J.CUBI_CODDEP||J.CUBI_CODPRO||J.CUBI_CODDIS=U.UBDEP||U.UBPRV||U.UBDIS \n" +
                        "WHERE J.CPRO_RUC IN (SELECT CO_DES FROM IDOSGD.TDTD_DEP_GRU_VAR WHERE CO_GRU_DES = ? AND Es_dep_gru = '1' AND TI_DES = ?)\n" +
                        " ");
        }

        if(pTiDes.equals("03"))
        {
            sql.append("SELECT C.NULEM nu_dni,COALESCE(substr(PK_SGD_DESCRIPCION.ANI_SIMIL(C.NULEM), 1, 100),' ') de_ciudadano,\n"+
            "COALESCE(C.DEDOMICIL,' ') CDIR_REMITE,COALESCE(C.DEEMAIL,' ') CEXP_CORREOE,COALESCE(C.DETELEFO,' ') CTELEFONO,COALESCE(C.UBDEP,' ') CCOD_DPTO,COALESCE(C.UBPRV,' ') CCOD_PROV,COALESCE(C.UBDIS,' ') CCOD_DIST,\n" +
            "COALESCE(TRIM(NODEP)||' '||TRIM(NOPRV)||' '||TRIM(NODIS),' ') UBIGEO\n" +          
            "FROM IDOSGD.TDTX_ANI_SIMIL C LEFT JOIN IDOSGD.IDTUBIAS U ON C.UBDEP||C.UBPRV||C.UBDIS=U.UBDEP||U.UBPRV||U.UBDIS \n" +
            "WHERE C.NULEM IN (SELECT CO_DES FROM IDOSGD.TDTD_DEP_GRU_VAR WHERE CO_GRU_DES = ? AND Es_dep_gru = '1' AND TI_DES = ?)\n" +
            "  ");       
        }

        if(pTiDes.equals("04"))
        {
            sql.append( "SELECT O.CO_OTR_ORI co_otro_origen,O.DE_APE_PAT_OTR||' '||O.DE_APE_MAT_OTR||', '||O.DE_NOM_OTR || ' - ' ||\n" +
                        "O.DE_RAZ_SOC_OTR ||'##'||  COALESCE(S.CELE_DESELE,'   ') ||'##'||  O.NU_DOC_OTR_ORI  de_otro_origen_full , \n" +
                        "COALESCE(O.DE_DIR_OTRO_ORI,' ') CDIR_REMITE,COALESCE(O.DE_EMAIL,' ') CEXP_CORREOE,COALESCE(O.DE_TELEFO,' ') CTELEFONO,COALESCE(O.UB_DEP,' ') CCOD_DPTO,COALESCE(O.UB_PRO,' ') CCOD_PROV,COALESCE(O.UB_DIS,' ') CCOD_DIST,\n" +
                        "COALESCE(TRIM(NODEP)||' '||TRIM(NOPRV)||' '||TRIM(NODIS),' ') UBIGEO\n" +          
                        "FROM IDOSGD.TDTR_OTRO_ORIGEN O LEFT JOIN IDOSGD.IDTUBIAS U ON O.UB_DEP||O.UB_PRO||O.UB_DIS=U.UBDEP||U.UBPRV||U.UBDIS \n" +
                        "LEFT JOIN  (\n" +
                        "SELECT CELE_CODELE, CELE_DESELE  FROM IDOSGD.SI_ELEMENTO  WHERE CTAB_CODTAB ='TIP_DOC_IDENT') S\n" +
                        "ON O.CO_TIP_OTR_ORI = S.CELE_CODELE \n" +                    
                        "WHERE O.CO_OTR_ORI IN (SELECT CO_DES FROM IDOSGD.TDTD_DEP_GRU_VAR WHERE CO_GRU_DES = ? AND Es_dep_gru = '1' AND TI_DES = ?)\n" +
                        "  ");    
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
