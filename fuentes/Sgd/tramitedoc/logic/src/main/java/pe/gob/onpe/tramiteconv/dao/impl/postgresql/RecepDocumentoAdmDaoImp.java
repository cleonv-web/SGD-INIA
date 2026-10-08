/**
 *
 */
package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.util.List;
import java.util.ArrayList;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;

import pe.gob.onpe.tramitedoc.dao.RecepDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;

import java.sql.Types;
import java.util.HashMap;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.Paginacion;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

/**
 * @author ecueva
 *
 */
@Repository("recepDocumentoAdmDao")
public class RecepDocumentoAdmDaoImp extends SimpleJdbcDaoBase implements RecepDocumentoAdmDao {

    private SimpleJdbcCall spPuActualizaGuiaMp, spActualizaEstado;

    /* (non-Javadoc)
     * @see pe.gob.onpe.tramitedoc.dao.impl.RecepDocumentoAdmDao#getDocumentosRecepAdm(pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepBean)
     */
//	@Override
//	public List<DocumentoBean> getDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep){
//        StringBuffer sql = new StringBuffer();
//        sql.append("SELECT NU_ANN,NU_EMI,TI_CAP,NU_COR_DES,TO_CHAR(FE_EMI,'DD/MM/YY') FE_EMI_CORTA,FE_EMI,DE_ORI_EMI,DE_TIP_DOC_ADM,NU_DOC,DE_EMP_DES,DE_ASU_M,NU_EXPEDIENTE,DE_ES_DOC_DES,");
//        sql.append("'1' EXISTE_DOC,TD_PK_TRAMITE.FU_DOC_ANE(NU_ANN,NU_EMI) EXISTE_ANEXO,NU_DES ");
//        sql.append("FROM TDVV_DESTINOS_ADM ");
//        sql.append("WHERE ");        
//        sql.append(" NU_ANN = NVL(?/*:B_01_ANN.DE_ANIO*/, NU_ANN)");
//        sql.append(" AND CO_DEP_DES = ?/*:GLOBAL.VCOD_DEP*/ ");
//        sql.append(" AND CO_TIP_DOC_ADM = NVL(?/*:B_01_ANN.DE_DOC_DEP*/,CO_TIP_DOC_ADM)");
//        sql.append(" AND ES_DOC_REC = DECODE(?/*:B_01_ANN.DE_ESTADOS*/, NULL, ES_DOC_REC, ?/*:B_01_ANN.DE_ESTADOS*/)");
//        sql.append(" AND CO_PRI     = DECODE(?/*:B_01_ANN.DE_PRI*/, NULL, CO_PRI, ?/*:B_01_ANN.DE_PRI*/) ");
//        sql.append(" AND (  CO_EMP_DES = DECODE(?/*:GLOBAL.TI_ACCESO*/, 0, CO_EMP_DES, ?/*:GLOBAL.USER*/)");
//        sql.append("	OR (   CO_EMP_DES IS NULL ");
//        sql.append("	   AND TI_DES ='01'");
//        sql.append("	   )");
//        sql.append("	)");
//        sql.append(" AND nvl(TI_EMI_REF,'0') = NVL(?/*:B_01_ANN.TI_EMI_REF*/, nvl(TI_EMI_REF,'0'))");
//        sql.append(" AND NVL(CO_EMP_DES,'NULO') = NVL(?/*:B_01_ANN.TI_EMP_DES*/, NVL(CO_EMP_DES,'NULO'))");
//        sql.append(" AND (  (   NVL(?/*:B_01_ANN.LI_EXPE*/, 'NULO') = 'SINEX'");
//        sql.append("	   AND CO_EXP IS NULL");
//        sql.append("	   )");
//        sql.append("	OR (   NVL(?/*:B_01_ANN.LI_EXPE*/, 'NULO') != 'NULO'");
//        sql.append("	   AND CO_EXP = ?/*:B_01_ANN.LI_EXPE*/");
//        sql.append("	   )");
//        sql.append("	OR ?/*:B_01_ANN.LI_EXPE*/ IS NULL");
//        sql.append("	)");
//        sql.append(" ORDER BY FE_EMI DESC ");
//        List<DocumentoBean> list = new ArrayList<DocumentoBean>();
//        try {
//            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
//                    new Object[]{buscarDocumentoRecep.getsCoAnnio(),buscarDocumentoRecep.getsCoDependencia(),buscarDocumentoRecep.getsTipoDoc(),
//                    buscarDocumentoRecep.getsEstadoDoc(),buscarDocumentoRecep.getsEstadoDoc(),buscarDocumentoRecep.getsPrioridadDoc(),buscarDocumentoRecep.getsPrioridadDoc(),
//                    buscarDocumentoRecep.getsTiAcceso(),buscarDocumentoRecep.getsCoEmpleado(),buscarDocumentoRecep.getsRemitente(),buscarDocumentoRecep.getsDestinatario(),
//                    buscarDocumentoRecep.getsExpediente(),buscarDocumentoRecep.getsExpediente(),buscarDocumentoRecep.getsExpediente(),buscarDocumentoRecep.getsExpediente()});
//        }catch (EmptyResultDataAccessException e) {
//            list = null;
//        }catch (Exception e) {
//            list = null;
//            e.printStackTrace();
//        }
//        return list;
//    }
    @Override
    public List<DocumentoBean> getDocumentosBuscaRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoBean> list = new ArrayList<DocumentoBean>();
  
         sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP(X.NU_ANN, X.NU_EMI) DE_ORI_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(X.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,");
         sql.append("CASE \n"
                + "WHEN X.TI_DES='01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_DES)\n"
                + "WHEN X.TI_DES='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.NU_RUC_DES)\n"
                + "WHEN X.TI_DES='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(X.NU_DNI_DES)\n"
                + "WHEN X.TI_DES='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(X.CO_OTR_ORI_DES)\n"
                + "END AS DE_EMP_DES "); 
        sql.append(" FROM ( ");
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.TI_CAP,B.NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" A.CO_TIP_DOC_ADM , COALESCE(C.NU_DOC,'') as NU_DOC ,");
        sql.append(" B.TI_DES,B.CO_EMP_DES,B.NU_RUC_DES,B.NU_DNI_DES, B.CO_OTR_ORI_DES,");
        sql.append(" UPPER (A.DE_ASU) DE_ASU_M,C.NU_EXPEDIENTE,");
        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append("CASE"
                + " WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='00' THEN 0 "
                + " WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='02' THEN 0 "
                + " ELSE 1 END AS EXISTE_ANEXO,"          
                + "NU_DES,COALESCE(B.CO_PRI,'1') CO_PRI, B.ES_DOC_REC,");
        sql.append(" B.CO_ETIQUETA_REC"); 
        sql.append(" , CASE WHEN B.ES_DOC_REC='1' THEN  CASE WHEN COALESCE(totalproceso_ref,0)>1 THEN 'En Proceso de Atención <br/> Hay '||COALESCE(totalproceso_ref,0)||' documentos' WHEN COALESCE(totalproceso_ref,0)=1 THEN 'En Proceso de Atención <br/> '||tipodocumento_ref||'/'||nrodocumento_ref||'<br/> '||estado_ref ELSE '' END  ELSE '' END enprocesoatencion ");
        sql.append(" FROM IDOSGD.TDTV_REMITOS A INNER JOIN IDOSGD.TDTV_DESTINOS B ON B.NU_ANN = A.NU_ANN  AND B.NU_EMI = A.NU_EMI ");
        sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN C ON   C.NU_ANN = B.NU_ANN AND C.NU_EMI = B.NU_EMI ");
        
        sql.append(" LEFT JOIN (  SELECT  a.nu_ann_ref,a.nu_emi_ref,a.nu_des_ref,MAX(IDOSGD.PK_SGD_DESCRIPCION_de_documento(b.co_tip_doc_adm)) tipodocumento_ref , \n" +
                "                MAX("
                + "(CASE b.ti_emi "
                + " WHEN '01' THEN b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig  "
                + " WHEN '05' THEN b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig  "
                + " ELSE COALESCE(b.de_doc_sig,'S/N')"
                + " END )"
                + ") nrodocumento_ref,                 \n" +
                "                MAX(IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(B.ES_DOC_EMI,'TDTV_REMITOS'))  estado_ref, COUNT(1) totalproceso_ref\n" +
                "                FROM IDOSGD.tdtr_referencia a \n" +
                "                INNER JOIN IDOSGD.tdtv_remitos b ON a.nu_ann = b.nu_ann and a.nu_emi = b.nu_emi\n" +
                "                INNER JOIN IDOSGD.tdtv_destinos c ON b.nu_ann = c.nu_ann  and b.nu_emi = c.nu_emi\n" +
                "                WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                "                GROUP BY a.nu_ann_ref,a.nu_emi_ref,a.nu_des_ref ) TB  ON   TB.nu_ann_ref=c.nu_ann AND TB.nu_emi_ref=c.nu_emi AND TB.nu_des_ref =B.nu_des   "); 
        
        sql.append(" WHERE 1=1 ");    
        
        /*String pNUAnn = buscarDocumentoRecep.getsCoAnnio();
        if (!(buscarDocumentoRecep.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNUAnn);
        }*/

        sql.append(" AND B.CO_DEP_DES = :pCoDepDes");
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND A.CO_GRU IN ('1','2','3')");
        sql.append(" AND B.ES_ELI = '0'");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7')");
        sql.append(" AND A.IN_OFICIO = '0'");

        // Parametros Basicos
        objectParam.put("pCoDepDes", buscarDocumentoRecep.getsCoDependencia());

        String pTipoBusqueda = buscarDocumentoRecep.getsTipoBusqueda();
        if (pTipoBusqueda.equals("1") && buscarDocumentoRecep.isEsIncluyeFiltro()) {
            bBusqFiltro = true;
        }
        String auxTipoAcceso=buscarDocumentoRecep.getsTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1")||auxTipoAcceso.equals("2"))?auxTipoAcceso:"1";         
        if (buscarDocumentoRecep.getsDestinatario()!=null&&buscarDocumentoRecep.getsDestinatario().trim().length()>0&&(pTipoBusqueda.equals("0")||bBusqFiltro)&&tiAcceso.equals("0")) {
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
            objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsDestinatario());
        } else {
            if (tiAcceso.equals("1")) {
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
                objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsCoEmpleado());
            } else if (tiAcceso.equals("2")) {
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes  ");
                objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsCoEmpleado());
            }
        }

        //Filtro
        if (pTipoBusqueda.equals("0") || bBusqFiltro) {
            
            if ((buscarDocumentoRecep.isEsIncluyeOficina() && !buscarDocumentoRecep.isEsIncluyeProfesional()) || (!buscarDocumentoRecep.isEsIncluyeOficina() && buscarDocumentoRecep.isEsIncluyeProfesional())) {
                if(buscarDocumentoRecep.isEsIncluyeOficina()){
                    sql.append(" AND B.CO_EMP_DES  in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pcoEmpDesOficina  )");
                objectParam.put("pcoEmpDesOficina", buscarDocumentoRecep.getsCoDependencia());
                }
                if(buscarDocumentoRecep.isEsIncluyeProfesional()){
                    sql.append(" AND NOT B.CO_EMP_DES  in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pcoEmpDesOficina  )");
                objectParam.put("pcoEmpDesOficina", buscarDocumentoRecep.getsCoDependencia());
                }
            }
            
            if (buscarDocumentoRecep.getCoTema()!= null && buscarDocumentoRecep.getCoTema().trim().length() > 0) {
                sql.append(" AND B.CO_TEMA = :pCoTema ");
                objectParam.put("pCoTema", buscarDocumentoRecep.getCoTema());
            }
            
            if (buscarDocumentoRecep.getsTipoDoc() != null && buscarDocumentoRecep.getsTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoRecep.getsTipoDoc());
            }
            if (buscarDocumentoRecep.getsEstadoDoc() != null && buscarDocumentoRecep.getsEstadoDoc().trim().length() > 0) {
                String estadoDoc=buscarDocumentoRecep.getsEstadoDoc();
                sql.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                objectParam.put("pEsDocRec", estadoDoc);
                if(!estadoDoc.equals("0")){
                    sOrdenList=" DESC";
                }
            }else{
                sOrdenList=" DESC";
            }
            if (buscarDocumentoRecep.getsPrioridadDoc() != null && buscarDocumentoRecep.getsPrioridadDoc().trim().length() > 0) {
                sql.append(" AND B.CO_PRI = :pCoPrioridad ");
                objectParam.put("pCoPrioridad", buscarDocumentoRecep.getsPrioridadDoc());
            }
            if (buscarDocumentoRecep.getsRemitente() != null && buscarDocumentoRecep.getsRemitente().trim().length() > 0) {
                sql.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = :pTiEmiRef ");
                objectParam.put("pTiEmiRef", buscarDocumentoRecep.getsRemitente());
            }
            if (buscarDocumentoRecep.getIdEtiqueta() != null && buscarDocumentoRecep.getIdEtiqueta().trim().length() > 0) {
                sql.append(" AND B.CO_ETIQUETA_REC = :pcoEtiquetaRec ");
                objectParam.put("pcoEtiquetaRec", buscarDocumentoRecep.getIdEtiqueta());
            }
            if (buscarDocumentoRecep.getEsFiltroFecha() != null
                    && (buscarDocumentoRecep.getEsFiltroFecha().equals("1") || buscarDocumentoRecep.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoRecep.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoRecep.getsFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59' ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
        }

        if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoRecep.getsNroDocumento() != null && buscarDocumentoRecep.getsNroDocumento().trim().length() > 1) {
                sql.append(" AND A.NU_DOC_EMI LIKE ''||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoRecep.getsNroDocumento());
            }

            if (buscarDocumentoRecep.getsBuscNroExpediente() != null && buscarDocumentoRecep.getsBuscNroExpediente().trim().length() > 1) {
                sql.append(" AND C.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoRecep.getsBuscNroExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoRecep.getsBuscAsunto() != null && buscarDocumentoRecep.getsBuscAsunto().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoRecep.getsBuscAsunto())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
                objectParam.put("pDeAsunto", buscarDocumentoRecep.getsBuscAsunto());
            }

        }

        sql.append(" ORDER BY A.FE_EMI").append(sOrdenList);
        sql.append(" ) AS X LIMIT 100");

        try {

            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));

        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    @Override
    public ProcessResult<List<DocumentoBean>> getDocumentosBuscaRecepAdmList(BuscarDocumentoRecepBean buscarDocumentoRecep,FiltroPaginate paginate) {
     StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();
        ProcessResult<List<DocumentoBean>> Result = new ProcessResult<List<DocumentoBean>>();
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();

        sql.append("SELECT X.*,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP(X.NU_ANN, X.NU_EMI) DE_ORI_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(X.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,");
        sql.append(" ( CASE X.TI_DES ");
        sql.append(" WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.CO_EMP_DES) ");
        sql.append(" WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(X.NU_RUC_DES) ");
        sql.append(" WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(X.NU_DNI_DES) ");
        sql.append(" WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(X.CO_OTR_ORI_DES)");
        sql.append(" END ) DE_EMP_DES");
        sql.append(" FROM ( SELECT X.*,row_number() OVER (ORDER BY 1) AS   fila, COUNT(1) OVER()  AS filasTotal    ");
        sql.append(" FROM ( ");
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.TI_CAP,A.TI_EMI,COALESCE(B.NU_COR_DES::text,'') as NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YYYY  HH:MI') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" A.CO_TIP_DOC_ADM , C.NU_DOC,");
        sql.append(" B.TI_DES,B.CO_EMP_DES,B.NU_RUC_DES,B.NU_DNI_DES, B.CO_OTR_ORI_DES,");
        sql.append(" '<p style=''text-align:justify;''>'||UPPER(A.DE_ASU)||'</p>' DE_ASU_M,COALESCE(C.NU_EXPEDIENTE,' ') as NU_EXPEDIENTE,");
        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append("CASE"
                + " WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='00' THEN 0 "
                + " WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='02' THEN 0 "
                + " ELSE 1 END AS EXISTE_ANEXO,"                          
                + "NU_DES,COALESCE(B.CO_PRI,'1') CO_PRI, B.ES_DOC_REC,");
        sql.append(" B.CO_ETIQUETA_REC"); 
        sql.append(" ,(SELECT count(1) FROM IDOSGD.TDTR_AVANCE AVA WHERE AVA.NU_EMI=A.NU_EMI AND AVA.NU_ANN=A.NU_ANN) as iCanAvance"); 
        
        sql.append(" ,(CASE WHEN B.ES_DOC_REC='1' THEN  (CASE WHEN COALESCE(totalproceso_ref,0)>1 THEN 'En Proceso de Atención <br/> Hay '||COALESCE(totalproceso_ref,0)||' documentos' WHEN COALESCE(totalproceso_ref,0)=1 THEN 'En Proceso de Atención <br/> '||tipodocumento_ref||'/'||nrodocumento_ref||'<br/> '||estado_ref ELSE '' END)  ");
        sql.append(" WHEN B.ES_DOC_REC in ('2','4') THEN  (CASE WHEN COALESCE(totalproceso_ref,0)>1 THEN 'Atendido con <br/> '||COALESCE(totalproceso_ref,0)||' documentos' WHEN COALESCE(totalproceso_ref,0)=1 THEN 'Atendido con  <br/> '||tipodocumento_ref||'/'||nrodocumento_ref||'<br/> '||estado_ref ELSE '' END)  ELSE '' END) enprocesoatencion ");
        
        
        sql.append(" FROM IDOSGD.TDTV_REMITOS A INNER JOIN IDOSGD.TDTV_DESTINOS B ON B.NU_ANN = A.NU_ANN  AND B.NU_EMI = A.NU_EMI ");
        sql.append(" INNER JOIN IDOSGD.TDTX_REMITOS_RESUMEN C ON   C.NU_ANN = B.NU_ANN AND C.NU_EMI = B.NU_EMI ");
        
      
        sql.append(" LEFT JOIN (  SELECT  r.nu_ann_ref,r.nu_emi_ref,r.nu_des_ref,MAX(s.CDOC_DESDOC) tipodocumento_ref , \n" +
                "                MAX("
               + "(CASE b.ti_emi "
                + " WHEN '01' THEN b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig  "
                + " WHEN '05' THEN b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig  "
                + " ELSE COALESCE(b.de_doc_sig,'S/N')"
                + " END )"
                + ") nrodocumento_ref, \n" +
                "                 MAX(e.de_est)  estado_ref, COUNT(1) totalproceso_ref\n" +
                "                FROM IDOSGD.tdtr_referencia r \n" +
                "                INNER JOIN IDOSGD.tdtv_remitos b ON r.nu_ann = b.nu_ann and r.nu_emi = b.nu_emi\n" +
                "                INNER JOIN IDOSGD.tdtv_destinos c ON b.nu_ann = c.nu_ann  and b.nu_emi = c.nu_emi\n" +
                "                inner join IDOSGD.si_mae_tipo_doc s ON s.CDOC_TIPDOC=b.co_tip_doc_adm\n" +
                "                inner join IDOSGD.tdtr_estados e ON e.co_Est=B.ES_DOC_EMI and e.DE_TAB='TDTV_REMITOS'\n"+
                "                WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                "                GROUP BY r.nu_ann_ref,r.nu_emi_ref,r.nu_des_ref ) TB  ON   TB.nu_ann_ref=c.nu_ann AND TB.nu_emi_ref=c.nu_emi AND TB.nu_des_ref =B.nu_des   "); 
        
        sql.append(" WHERE 1=1 ");         
       
        //YUAL: problemas al visualizar documetno de dos años
        /*
        String pNUAnn = buscarDocumentoRecep.getsCoAnnio();
        if (!(buscarDocumentoRecep.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
         
            objectParam.put("pNuAnn", pNUAnn);
        }*/

        //YUAL sql.append(" AND B.CO_DEP_DES = :pCoDepDes");
        sql.append(" AND B.CO_DEP_DES='"+buscarDocumentoRecep.getsCoDependencia()+"'");
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND A.CO_GRU IN ('1','2','3')");
        sql.append(" AND B.ES_ELI = '0'");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7', '8')");
        sql.append(" AND A.IN_OFICIO = '0'");

        // Parametros Basicos
        objectParam.put("pCoDepDes", buscarDocumentoRecep.getsCoDependencia());

        String pTipoBusqueda = buscarDocumentoRecep.getsTipoBusqueda();
       /* if (pTipoBusqueda.equals("1") && buscarDocumentoRecep.isEsIncluyeFiltro()) {*/
            bBusqFiltro = true;
        /*}*/
        String auxTipoAcceso=buscarDocumentoRecep.getsTiAcceso();
        String tiAcceso=auxTipoAcceso!=null&&(auxTipoAcceso.equals("0")||auxTipoAcceso.equals("1")||auxTipoAcceso.equals("2"))?auxTipoAcceso:"1";         
        if (buscarDocumentoRecep.getsDestinatario()!=null&&buscarDocumentoRecep.getsDestinatario().trim().length()>0&&(pTipoBusqueda.equals("0")||bBusqFiltro)&&tiAcceso.equals("0")) {
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
            objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsDestinatario());
        } else {
            if (tiAcceso.equals("1")) {
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
                objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsCoEmpleado());
            } else if (tiAcceso.equals("2")) {
                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes  ");
                objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsCoEmpleado());
            }
        }

          
        String bExcepSeach="0";
        /*///WCM 22/03/2022
        if((buscarDocumentoRecep.getsNroDocumento() != null && buscarDocumentoRecep.getsNroDocumento().trim().length() > 1)
         ||(buscarDocumentoRecep.getsBuscNroExpediente() != null && buscarDocumentoRecep.getsBuscNroExpediente().trim().length() > 1)
         ||(buscarDocumentoRecep.getsBuscAsunto() != null && buscarDocumentoRecep.getsBuscAsunto().trim().length() > 1)
         ||(buscarDocumentoRecep.getsNumDocRef() != null && buscarDocumentoRecep.getsNumDocRef().trim().length() > 1)
                )
        {  bExcepSeach="1";
        }
        */
        
        //Filtro
       /* if (pTipoBusqueda.equals("0") || bBusqFiltro) {*/
       if(bExcepSeach=="0") {
           
            
            if ((buscarDocumentoRecep.isEsIncluyeOficina() && !buscarDocumentoRecep.isEsIncluyeProfesional()) || (!buscarDocumentoRecep.isEsIncluyeOficina() && buscarDocumentoRecep.isEsIncluyeProfesional())) {
                if(buscarDocumentoRecep.isEsIncluyeOficina()){
                    sql.append(" AND B.CO_EMP_DES  in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pcoEmpDesOficina  )");
                objectParam.put("pcoEmpDesOficina", buscarDocumentoRecep.getsCoDependencia());
                }
                if(buscarDocumentoRecep.isEsIncluyeProfesional()){
                    sql.append(" AND NOT B.CO_EMP_DES  in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pcoEmpDesOficina  )");
                objectParam.put("pcoEmpDesOficina", buscarDocumentoRecep.getsCoDependencia());
                }
            }
            
            if (buscarDocumentoRecep.getCoTema()!= null && buscarDocumentoRecep.getCoTema().trim().length() > 0) {
                sql.append(" AND B.CO_TEMA = :pCoTema ");
                objectParam.put("pCoTema", buscarDocumentoRecep.getCoTema());
            }
            if (buscarDocumentoRecep.getsTipoDoc() != null && buscarDocumentoRecep.getsTipoDoc().trim().length() > 0) {
                sql.append(" AND A.CO_TIP_DOC_ADM = :pCoDocEmi ");
                objectParam.put("pCoDocEmi", buscarDocumentoRecep.getsTipoDoc());
            }
            
            if (buscarDocumentoRecep.getsEstadoDoc() != null && buscarDocumentoRecep.getsEstadoDoc().trim().length() > 0) {
                String estadoDoc=buscarDocumentoRecep.getsEstadoDoc();
               if(estadoDoc.equals("P")){
                    sql.append(" AND B.ES_DOC_REC IN ('0','1') ");                   
               }
               else
               {
                    sql.append(" AND B.ES_DOC_REC = :pEsDocRec ");
                    objectParam.put("pEsDocRec", estadoDoc);
               }
                
              
                if(!estadoDoc.equals("0")){
                    sOrdenList=" DESC";
                }
            }else{
                sOrdenList=" DESC";
            }
            if (buscarDocumentoRecep.getsPrioridadDoc() != null && buscarDocumentoRecep.getsPrioridadDoc().trim().length() > 0) {
               if(buscarDocumentoRecep.getsPrioridadDoc().equals("4")){
                sql.append(" AND B.CO_PRI IN ('2','3') ");
               }
               else{
                    sql.append(" AND B.CO_PRI = :pCoPrioridad ");
                    objectParam.put("pCoPrioridad", buscarDocumentoRecep.getsPrioridadDoc());
               }
                
            }
            //YUAL
             if (buscarDocumentoRecep.getsTipoProyDoc()!= null && buscarDocumentoRecep.getsTipoProyDoc().trim().length() > 0) {
                    if(buscarDocumentoRecep.getsTipoProyDoc().equals("1")){
                    sql.append(" AND COALESCE(totalproceso_ref,0)=0 ");
                    }
                    if(buscarDocumentoRecep.getsTipoProyDoc().equals("2")){
                    sql.append(" AND COALESCE(totalproceso_ref,0)>=1 ");
                    }
                
            }
            
            if (buscarDocumentoRecep.getsRemitente() != null && buscarDocumentoRecep.getsRemitente().trim().length() > 0) {
                sql.append(" AND COALESCE(C.CO_DEP_EMI_REF,'0') = :pTiEmiRef ");
                objectParam.put("pTiEmiRef", buscarDocumentoRecep.getsRemitente());
            }
            if (buscarDocumentoRecep.getIdEtiqueta() != null && buscarDocumentoRecep.getIdEtiqueta().trim().length() > 0) {
                sql.append(" AND B.CO_ETIQUETA_REC = :pcoEtiquetaRec ");
                objectParam.put("pcoEtiquetaRec", buscarDocumentoRecep.getIdEtiqueta());
            }
            if (buscarDocumentoRecep.getEsFiltroFecha() != null/*
                    && (buscarDocumentoRecep.getEsFiltroFecha().equals("1") || buscarDocumentoRecep.getEsFiltroFecha().equals("3"))*/) {
                String vFeEmiIni = buscarDocumentoRecep.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoRecep.getsFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + TIME '23:59:59' ");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }
        }

       /* if (pTipoBusqueda.equals("1")) {*/
            if (buscarDocumentoRecep.getsNroDocumento() != null && buscarDocumentoRecep.getsNroDocumento().trim().length() > 1) {
                sql.append(" AND A.NU_DOC_EMI LIKE ''||:pnuDocEmi||'%' ");
                objectParam.put("pnuDocEmi", buscarDocumentoRecep.getsNroDocumento());
            }

            if (buscarDocumentoRecep.getsBuscNroExpediente() != null && buscarDocumentoRecep.getsBuscNroExpediente().trim().length() > 1) {
                sql.append(" AND C.NU_EXPEDIENTE LIKE '%'||:pnuExpediente||'%' ");
                objectParam.put("pnuExpediente", buscarDocumentoRecep.getsBuscNroExpediente());
            }

            // Busqueda del Asunto
            if (buscarDocumentoRecep.getsBuscAsunto() != null && buscarDocumentoRecep.getsBuscAsunto().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoRecep.getsBuscAsunto())+"', 1 ) > 1 ");
                sql.append(" AND UPPER(A.DE_ASU) LIKE UPPER('%'||:pDeAsunto||'%') ");
                objectParam.put("pDeAsunto", buscarDocumentoRecep.getsBuscAsunto());
            }
            
            if (buscarDocumentoRecep.getsBusRemitente()!= null && buscarDocumentoRecep.getsBusRemitente().trim().length() > 1) {
                //sql.append(" AND CONTAINS(in_busca_texto, '"+BusquedaTextual.getContextValue( buscarDocumentoRecep.getsBuscAsunto())+"', 1 ) > 1 ");
                //sql.append(" AND UPPER(A.DE_ASU) LIKE '%'+:pDeAsunto+'%' ");
                //objectParam.put("pDeAsunto", buscarDocumentoRecep.getsBuscAsunto());
                sql.append(" AND UPPER((CASE A.TI_EMI ");
                sql.append(" WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_EMI) ||'-'||IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_EMI) ");
                sql.append(" WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR (A.NU_RUC_EMI) ");
                sql.append(" WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (A.NU_DNI_EMI) ");
                sql.append(" WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (A.CO_OTR_ORI_EMI) ");
                sql.append(" WHEN '05' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_EMI) ||'-'||IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_EMI)");
                sql.append(" END )) LIKE '%'||UPPER(:pRemitente)||'%' ");
                                
                objectParam.put("pRemitente", buscarDocumentoRecep.getsBusRemitente());
            }
                        
            if (buscarDocumentoRecep.getsNumDocRef() != null && buscarDocumentoRecep.getsNumDocRef().trim().length() > 1) {
            //YUAL
            //busqeda referencia
             sql.append("  AND A.NU_EMI IN( ");
            sql.append("   SELECT D.NU_EMI ");
            sql.append("   FROM IDOSGD.TDTR_ARBOL_SEG D ");
            sql.append("   INNER JOIN IDOSGD.TDTV_REMITOS R ON R.NU_ANN=D.NU_ANN_REF AND D.NU_EMI_REF=R.NU_EMI ");
            sql.append("   WHERE R.NU_ANN='"+buscarDocumentoRecep.getsCoAnnioBus()+"'   ");
            if (buscarDocumentoRecep.getsDeTipoDocAdm() != null && buscarDocumentoRecep.getsDeTipoDocAdm().trim().length() > 1) {
            sql.append(" AND R.CO_TIP_DOC_ADM='"+buscarDocumentoRecep.getsDeTipoDocAdm()+"' ");
            }
            if (buscarDocumentoRecep.getsBuscDestinatario() != null && buscarDocumentoRecep.getsBuscDestinatario().trim().length() > 1) {
            sql.append(" AND R.CO_DEP_EMI='"+buscarDocumentoRecep.getsBuscDestinatario()+"' ");
            }
             
            sql.append("   AND    ((R.NU_DOC_EMI LIKE '%'||'"+buscarDocumentoRecep.getsNumDocRef()+"'||'%') OR (R.DE_DOC_SIG LIKE '%'||'"+buscarDocumentoRecep.getsNumDocRef()+"'||'%'))");
            sql.append(" )");
            }

        /*}*/

        //sql.append(" ORDER BY A.FE_EMI").append(sOrdenList);
        if(buscarDocumentoRecep.getOrden().equals("nuCorDes-ASC"))    sql.append(" ORDER BY NU_COR_DES ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("nuCorDes-DESC"))   sql.append(" ORDER BY NU_COR_DES DESC ");
        else if(buscarDocumentoRecep.getOrden().equals("feEmiCorta-ASC"))  sql.append(" ORDER BY FE_EMI ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("feEmiCorta-DESC")) sql.append(" ORDER BY FE_EMI DESC ");
        else if(buscarDocumentoRecep.getOrden().equals("deOriEmi-ASC")) sql.append(" ORDER BY TI_EMI,NU_RUC_DES,NU_DNI_DES,CO_OTR_ORI_DES ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("deOriEmi-DESC")) sql.append(" ORDER BY TI_EMI,NU_RUC_DES,NU_DNI_DES,CO_OTR_ORI_DES DESC ");
        else if(buscarDocumentoRecep.getOrden().equals("deTipDocAdm-ASC")) sql.append(" ORDER BY CO_TIP_DOC_ADM ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("deTipDocAdm-DESC")) sql.append(" ORDER BY CO_TIP_DOC_ADM DESC ");
        else if(buscarDocumentoRecep.getOrden().equals("nuDoc-ASC")) sql.append(" ORDER BY NU_DOC ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("nuDoc-DESC")) sql.append(" ORDER BY NU_DOC DESC ");
        else if(buscarDocumentoRecep.getOrden().equals("deEmpDes-ASC")) sql.append(" ORDER BY CO_EMP_DES,NU_RUC_DES,NU_DNI_DES,CO_OTR_ORI_DES ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("deEmpDes-DESC")) sql.append(" ORDER BY CO_EMP_DES,NU_RUC_DES,NU_DNI_DES,CO_OTR_ORI_DES DESC ");
        else if(buscarDocumentoRecep.getOrden().equals("deAsuM-ASC")) sql.append(" ORDER BY DE_ASU_M ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("deAsuM-DESC")) sql.append(" ORDER BY DE_ASU_M DESC ");
        else if(buscarDocumentoRecep.getOrden().equals("nuExpediente-ASC")) sql.append(" ORDER BY NU_EXPEDIENTE ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("nuExpediente-DESC")) sql.append(" ORDER BY NU_EXPEDIENTE DESC ");         
        else if(buscarDocumentoRecep.getOrden().equals("deEsDocDes-ASC")) sql.append(" ORDER BY ES_DOC_REC ASC ");
        else if(buscarDocumentoRecep.getOrden().equals("deEsDocDes-DESC")) sql.append(" ORDER BY ES_DOC_REC DESC ");          
        else sql.append(" ORDER BY FE_EMI ");
        sql.append(") X  ) X ");
        sql.append(" WHERE fila BETWEEN "+paginate.getPaginaDesde()+" AND "+paginate.getPaginaHasta()+"  ");
        //sql.append("WHERE ROWNUM < 101");

        try {

            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));
             Result.setResult(list);           
            Result.setIsSuccess(true);
            objectParam = new HashMap<String, Object>();
            buscarDocumentoRecep= new BuscarDocumentoRecepBean();
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
    public List<DocumentoBean> getResumenRecepAdmList(BuscarDocumentoRecepBean buscarDocumentoRecep){
       String Filtro="";
        
         if ((buscarDocumentoRecep.isEsIncluyeOficina() && !buscarDocumentoRecep.isEsIncluyeProfesional()) || (!buscarDocumentoRecep.isEsIncluyeOficina() && buscarDocumentoRecep.isEsIncluyeProfesional())) {
                if(buscarDocumentoRecep.isEsIncluyeOficina()){
                  Filtro=" AND T.CO_EMP_DES  in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep  )";
                
                }
                if(buscarDocumentoRecep.isEsIncluyeProfesional()){
                  Filtro=" AND NOT T.CO_EMP_DES  in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep  )";
                
                }
            }
         
        StringBuffer sql = new StringBuffer();
        sql.append(
                    "SELECT   SUM(nu_can) as iPendientes, 0 as iUrgentes, 0 iNormal \n" +
                    "FROM (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM IDOSGD.Tdtv_Remitos  r, \n" +
                    "           IDOSGD.Tdtv_Destinos t \n" +
                    "     WHERE r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1') \n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    Filtro+                            
                    "     GROUP BY t.Co_Dep_Des) tb \n" +                    
                     "UNION\n" +
                    "SELECT  0 as iPendientes,  SUM(nu_can) as iUrgentes, 0 iNormal \n" +
                    "FROM (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM IDOSGD.Tdtv_Remitos  r, \n" +
                    "           IDOSGD.Tdtv_Destinos t \n" +
                    "     WHERE r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1') \n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "       AND t.co_pri in ('2','3')\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    Filtro+                            
                    "     GROUP BY t.Co_Dep_Des) tb \n" +
                     "UNION\n" +
                    "SELECT  0 as iPendientes,  0 as iUrgentes, SUM(nu_can) iNormal \n" +
                    "FROM (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM IDOSGD.Tdtv_Remitos  r, \n" +
                    "           IDOSGD.Tdtv_Destinos t \n" +
                    "     WHERE r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1') \n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "       AND t.co_pri='1'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    Filtro+                            
                    "     GROUP BY t.Co_Dep_Des) tb\n"         
                    );

        List<DocumentoBean> list = new ArrayList<DocumentoBean>();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoDep", buscarDocumentoRecep.getsCoDependencia());
        objectParam.put("pCoEmp", buscarDocumentoRecep.getsCoEmpleado());
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
@Override
    public List<DependenciaBean> getDocumDepenProveido(String codDepen) {
        StringBuffer sql = new StringBuffer();
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();

        sql.append(" SELECT CO_DEPENDENCIA coDependencia,DE_DEPENDENCIA deDependencia,DE_CORTA_DEPEN deCortaDepen  FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA IN \n" +
                   "( SELECT CELE_DESELE FROM IDOSGD.SI_ELEMENTO WHERE CTAB_CODTAB='DE_DOCUM_PROVEIDO' AND CELE_DESCOR=? ) ");
         
        try {

            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class),new Object[]{codDepen});
             
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public DocumentoBean getDocumentoRecepAdm(String pnuAnn, String pnuEmi, String pnuDes) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.TI_EMI,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_RES) DE_EMP_RES,A.FE_EMI,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI,A.CO_TIP_DOC_ADM,IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n"
                + "CASE  WHEN A.TI_EMI='01' THEN\n"
                + "A.NU_DOC_EMI || '-' || A.NU_ANN || '-' || A.DE_DOC_SIG\n"
                + "WHEN A.TI_EMI='05' THEN\n"
                + "A.NU_DOC_EMI || '-' || A.NU_ANN || '-' || A.DE_DOC_SIG\n"
                + "ELSE A.DE_DOC_SIG\n"
                + "END NU_DOC,\n"
                + "A.NU_DIA_ATE,A.DE_ASU,B.DE_ANE,B.NU_DES,B.NU_COR_DES,B.CO_DEP_DES,IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA (B.CO_DEP_DES) DE_DEP_DES,IDOSGD.PK_SGD_DESCRIPCION_DE_PRIORIDAD (B.CO_PRI) DE_PRI,\n"
                + "CASE  WHEN B.TI_DES='01' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_DES)\n"
                + "WHEN B.TI_DES='02' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.NU_RUC_DES)\n"
                + "WHEN B.TI_DES='03' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (B.NU_DNI_DES)\n"
                + "WHEN B.TI_DES='04' THEN\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (B.CO_OTR_ORI_DES)\n"
                + "ELSE NULL\n"
                + "END DE_EMP_DES,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_MOTIVO (B.CO_MOT) DE_MOT,B.DE_PRO,B.CO_EMP_REC,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (B.CO_EMP_REC) DE_EMP_REC, B.ES_DOC_REC, IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES, B.FE_REC_DOC,\n"
                + "B.FE_ARC_DOC, B.FE_ATE_DOC,A.NU_ANN_EXP,A.NU_SEC_EXP,TO_CHAR(A.FE_EMI,'DD/MM/YYYY HH24:MI') FE_EMI_CORTA,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA2,\n"
                + "TO_CHAR(B.FE_REC_DOC,'DD/MM/YYYY HH24:MI') FE_REC_DOC_CORTA, TO_CHAR(B.FE_ARC_DOC,'DD/MM/YYYY HH24:MI') FE_ARC_DOC_CORTA, \n"
                + "TO_CHAR(B.FE_ATE_DOC,'DD/MM/YYYY HH24:MI') FE_ATE_DOC_CORTA,'1' EXISTE_DOC,IDOSGD.PK_SGD_DESCRIPCION_FU_DOC_ANE(A.NU_ANN,A.NU_EMI) EXISTE_ANEXO,\n"
                + "B.Ti_Fisico_Rec,B.Co_Etiqueta_Rec\n"
                + "FROM IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B\n"
                + "WHERE A.ES_ELI='0' AND B.ES_ELI='0'\n"
                + "AND A.NU_ANN = B.NU_ANN AND A.NU_EMI = B.NU_EMI\n"
                + "AND A.ES_DOC_EMI NOT IN ('5', '9', '7')\n"
                + "AND A.IN_OFICIO = '0'\n"
                + "AND B.NU_ANN=? AND B.NU_EMI=? AND B.NU_DES=?");

        DocumentoBean documentoBean = new DocumentoBean();
        try {
            documentoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{pnuAnn, pnuEmi, Integer.parseInt(pnuDes)});
        } catch (EmptyResultDataAccessException e) {
            documentoBean = null;
        } catch (Exception e) {
            System.out.println("nu_ann=" + pnuAnn + "," + "nu_emi=" + pnuEmi + "nu_des=" + pnuDes);
            e.printStackTrace();
        }
        return documentoBean;
    }

    @Override
    public ExpedienteBean getExpDocumentoRecepAdm(String pnuAnnExp, String pnuSecExp) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NU_ANN_EXP,NU_SEC_EXP,FE_EXP,TO_CHAR(FE_EXP,'DD/MM/YYYY') FE_EXP_CORTA,FE_VENCE,CO_PROCESO,IDOSGD.PK_SGD_DESCRIPCION_DE_PROCESO_EXP(CO_PROCESO) DE_PROCESO,DE_DETALLE,CO_DEP_EXP,CO_GRU,NU_CORR_EXP,NU_EXPEDIENTE,NU_FOLIOS,NU_PLAZO,US_CREA_AUDI,FE_CREA_AUDI,US_MODI_AUDI,FE_MODI_AUDI,ES_ESTADO\n"
                + "FROM IDOSGD.TDTC_EXPEDIENTE\n"
                + "where\n"
                + "NU_ANN_EXP=? and NU_SEC_EXP=?");

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
    public List<ReferenciaBean> getDocumentosRefRecepAdm(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) li_tip_doc,"
                + "CASE  WHEN A.TI_EMI='01' THEN\n"
                + "A.NU_DOC_EMI || '-' || A.NU_ANN || '/' || A.DE_DOC_SIG\n"
                + "WHEN A.TI_EMI='05' THEN\n"
                + "A.NU_DOC_EMI || '-' || A.NU_ANN || '/' || A.DE_DOC_SIG\n"
                + "ELSE A.DE_DOC_SIG\n"
                + "END li_nu_doc,"
                + "a.fe_emi,TO_CHAR(a.fe_emi,'DD/MM/YYYY') fe_emi_corta,b.co_ref,b.nu_ann,b.nu_emi,b.nu_des,b.nu_ann_ref,\n"
                + "b.nu_emi_ref,b.nu_des_ref\n"
                + "FROM IDOSGD.tdtv_remitos a, IDOSGD.TDTR_REFERENCIA b\n"
                + "WHERE a.nu_ann = b.nu_ann_ref\n"
                + "AND a.nu_emi = b.nu_emi_ref\n"
                + "AND b.NU_EMI=? \n"
                + "AND b.NU_ANN=?");

        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class),
                    new Object[]{pnuEmi, pnuAnn});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String actualizarGuiaMesaPartes(DocumentoBean documentoBean) {
        String mensaje = "NO_OK";
//        this.spPuActualizaGuiaMp = new SimpleJdbcCall(this.dataSource).withCatalogName("td_pk_tramite").withProcedureName("pu_actualiza_guia_mp")
          this.spPuActualizaGuiaMp = new SimpleJdbcCall(this.dataSource)
                  .withSchemaName("idosgd")
                  .withFunctionName("PK_SGD_TRAMITE_pu_actualiza_guia_mp")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("pnu_ann", "pnu_emi", "pnu_ann", "pnu_emi")
                .declareParameters(
                        new SqlParameter("pnu_ann", Types.VARCHAR),
                        new SqlParameter("pnu_emi", Types.VARCHAR),
                        new SqlParameter("pnu_des", Types.VARCHAR),
                        new SqlParameter("pes_doc_rec", Types.VARCHAR));
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pnu_ann", documentoBean.getNuAnn())
                .addValue("pnu_emi", documentoBean.getNuEmi())
                .addValue("pnu_des", documentoBean.getNuDes())
                .addValue("pes_doc_rec", documentoBean.getEsDocRec());

        try {
            this.spPuActualizaGuiaMp.execute(in);
            mensaje = "OK";
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }
        return mensaje;
    }

    @Override
    public String getNumCorrelativoDestino(String nuAnn, String coDepDes) {
        String result = "1";
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COALESCE(MAX(nu_cor_des), 0) + 1\n"
                + "FROM IDOSGD.tdtv_destinos\n"
                + "WHERE nu_ann = ?\n"
                + "AND co_dep_des = ?");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{nuAnn, coDepDes});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public String actualizarEstado(DocumentoBean documentoBean) {
        String mensaje = "NO_OK";
        //this.spActualizaEstado = new SimpleJdbcCall(this.dataSource).withCatalogName("td_pk_tramite").withProcedureName("actualiza_estado")
        this.spActualizaEstado = new SimpleJdbcCall(this.dataSource)
                .withSchemaName("idosgd")
                .withFunctionName("PK_SGD_TRAMITE_actualiza_estado")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("pnu_ann", "pnu_emi", "pnu_des", "pest")
                .declareParameters(
                        new SqlParameter("pnu_ann", Types.VARCHAR),
                        new SqlParameter("pnu_emi", Types.VARCHAR),
                        new SqlParameter("pnu_des", Types.VARCHAR),
                        new SqlParameter("pest", Types.VARCHAR));
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pnu_ann", documentoBean.getNuAnn())
                .addValue("pnu_emi", documentoBean.getNuEmi())
                .addValue("pnu_des", documentoBean.getNuDes())
                .addValue("pest", documentoBean.getEsDocRec());

        try {
            this.spActualizaEstado.execute(in);
            mensaje = "OK";
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }
        return mensaje;
    }

    @Override
    public String updDocumentoBean(DocumentoBean documentoBean, String paccion) {
        StringBuilder sql = new StringBuilder();
        String vReturn = "NO_OK";
        try {
            sql.append("UPDATE IDOSGD.TDTV_DESTINOS \n");
            sql.append("SET ES_DOC_REC = ?, CO_USE_MOD = ?, FE_USE_MOD = CURRENT_TIMESTAMP\n");
            //documento recepcionados atendido y derivado esos son los estados.
            if (paccion.equals("1") || paccion.equals("0")/*&& !documentoBean.getFeRecDoc().equals("")*/) {//no leido
                sql.append(",FE_REC_DOC = TO_TIMESTAMP('");
                sql.append(documentoBean.getFeRecDoc());
                sql.append("','DD/MM/YYYY HH24:MI:SS'),");
                sql.append("CO_EMP_REC ='");
                sql.append(documentoBean.getCoEmpRec());
                sql.append("'");
                
                if(documentoBean.getFeAteDoc()!= null && !documentoBean.getFeAteDoc().equals("")){
                    sql.append(",FE_ATE_DOC=TO_TIMESTAMP('");
                    sql.append(documentoBean.getFeAteDoc());
                    sql.append("','DD/MM/YYYY HH24:MI')");
                }
                if(documentoBean.getFeArcDoc()!= null && !documentoBean.getFeArcDoc().equals("")){
                    sql.append(",FE_ARC_DOC=TO_TIMESTAMP('");
                    sql.append(documentoBean.getFeArcDoc());
                    sql.append("','DD/MM/YYYY HH24:MI')");
                }
                 
            }/*else if(paccion.equals("1") && documentoBean.getFeRecDoc().equals("")){//no leido
             sql.append(",FE_REC_DOC=NULL");
             sql.append(",CO_EMP_REC ='");
             sql.append(documentoBean.getCoEmpRec());
             sql.append("'");
             }*/ else if (paccion.equals("2")) {
                sql.append(",CO_EMP_REC=NULL,FE_REC_DOC=NULL,FE_ATE_DOC=NULL,FE_ARC_DOC=NULL");
            }
            if(documentoBean.getNuCorDes()!=null && documentoBean.getNuCorDes()!=""){
              sql.append(",NU_COR_DES=CAST('"+documentoBean.getNuCorDes()+"' AS BIGINT)");
            }
            else {
              sql.append(",NU_COR_DES=NULL");
            }
            sql.append(",DE_ANE=?  WHERE NU_ANN = ? ");
            sql.append("AND NU_EMI = ? ");
            sql.append("AND NU_DES = CAST(? AS BIGINT)");
         System.out.println("getNuCorDes====>" + documentoBean.getNuCorDes());
          System.out.println("getNuAnn====>" + documentoBean.getNuAnn());
           System.out.println("getNuEmi====>" + documentoBean.getNuEmi());
            System.out.println("getNuDes====>" + documentoBean.getNuDes());
            
             this.jdbcTemplate.update(sql.toString(), new Object[]{
                documentoBean.getEsDocRec(), documentoBean.getCodUseMod(), 
                documentoBean.getDeAne(),
                documentoBean.getNuAnn(), documentoBean.getNuEmi(), documentoBean.getNuDes()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public List<DocumentoBean> getDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep, Paginacion paginacion) {
        
        StringBuffer sql = new StringBuffer();
        //para paginacion
        sql.append("SELECT * ");
        sql.append("FROM ( SELECT A.*  ");
        sql.append("FROM ( ");
        //para paginacion
        sql.append("SELECT NU_ANN,NU_EMI,TI_CAP,NU_COR_DES,TO_CHAR(FE_EMI,'DD/MM/YY') FE_EMI_CORTA,FE_EMI,DE_ORI_EMI,DE_TIP_DOC_ADM,NU_DOC,DE_EMP_DES,DE_ASU_M,NU_EXPEDIENTE,DE_ES_DOC_DES,");
        sql.append("'1' EXISTE_DOC,IDOSGD.PK_SGD_TRAMITE_FU_DOC_ANE(NU_ANN,NU_EMI) EXISTE_ANEXO,NU_DES ");
        sql.append("FROM IDOSGD.TDVV_DESTINOS_ADM ");
        sql.append("WHERE ");
        sql.append(" NU_ANN = COALESCE(? , NU_ANN)");
        sql.append(" AND CO_DEP_DES = ?  ");
        sql.append(" AND CO_TIP_DOC_ADM = COALESCE(?/*:B_01_ANN.DE_DOC_DEP*/,CO_TIP_DOC_ADM)");
        sql.append(" AND ES_DOC_REC = (CASE WHEN ? IS NULL THEN ES_DOC_REC ELSE ?  END)");
        sql.append(" AND CO_PRI     = (CASE WHEN ? IS NULL THEN CO_PRI ELSE ? END) ");
        sql.append(" AND (  CO_EMP_DES = DECODE(CASE WHEN ? = 0 THEN CO_EMP_DES ELSE ? END )");
        sql.append("	OR (   CO_EMP_DES IS NULL ");
        sql.append("	   AND TI_DES ='01'");
        sql.append("	   )");
        sql.append("	)");
        sql.append(" AND COALESCE(TI_EMI_REF,'0') = COALESCE(? , COALESCE(TI_EMI_REF,'0'))");
        sql.append(" AND COALESCE(CO_EMP_DES,'NULO') = COALESCE(? , COALESCE(CO_EMP_DES,'NULO'))");
        sql.append(" AND (  (   COALESCE(? , 'NULO') = 'SINEX'");
        sql.append("	   AND CO_EXP IS NULL");
        sql.append("	   )");
        sql.append("	OR (   COALESCE(? , 'NULO') != 'NULO'");
        sql.append("	   AND CO_EXP = ? ");
        sql.append("	   )");
        sql.append("	OR ?  IS NULL");
        sql.append("	)");
        sql.append(" ORDER BY FE_EMI DESC ");
        //para paginacion
        sql.append(") A ");
        sql.append(" LIMIT  (? * ?) ");
        sql.append(") ");
        sql.append(" LIMIT ((?-1) * ?) ");
        //para paginacion
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{buscarDocumentoRecep.getsCoAnnio(), buscarDocumentoRecep.getsCoDependencia(), buscarDocumentoRecep.getsTipoDoc(),
                        buscarDocumentoRecep.getsEstadoDoc(), buscarDocumentoRecep.getsEstadoDoc(), buscarDocumentoRecep.getsPrioridadDoc(), buscarDocumentoRecep.getsPrioridadDoc(),
                        buscarDocumentoRecep.getsTiAcceso(), buscarDocumentoRecep.getsCoEmpleado(), buscarDocumentoRecep.getsRemitente(), buscarDocumentoRecep.getsDestinatario(),
                        buscarDocumentoRecep.getsExpediente(), buscarDocumentoRecep.getsExpediente(), buscarDocumentoRecep.getsExpediente(), buscarDocumentoRecep.getsExpediente(),
                        paginacion.getNumeroDePagina(), paginacion.getRegistrosPorPagina(), paginacion.getNumeroDePagina(), paginacion.getRegistrosPorPagina()});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list; 
    }

    @Override
    public int getRowDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep) {
         int result = -1;
         StringBuffer sql = new StringBuffer();
         sql.append("SELECT COUNT(1) ");
         sql.append("FROM IDOSGD.TDVV_DESTINOS_ADM ");
         sql.append("WHERE "); 
        sql.append(" NU_ANN = COALESCE(? , NU_ANN)");
        sql.append(" AND CO_DEP_DES = ?  ");
        sql.append(" AND CO_TIP_DOC_ADM = COALESCE(?/*:B_01_ANN.DE_DOC_DEP*/,CO_TIP_DOC_ADM)");
        sql.append(" AND ES_DOC_REC = (CASE WHEN ? IS NULL THEN ES_DOC_REC ELSE ?  END)");
        sql.append(" AND CO_PRI     = (CASE WHEN ? IS NULL THEN CO_PRI ELSE ? END) ");
        sql.append(" AND (  CO_EMP_DES = DECODE(CASE WHEN ? = 0 THEN CO_EMP_DES ELSE ? END )");
        sql.append("	OR (   CO_EMP_DES IS NULL ");
        sql.append("	   AND TI_DES ='01'");
        sql.append("	   )");
        sql.append("	)");
        sql.append(" AND COALESCE(TI_EMI_REF,'0') = COALESCE(? , COALESCE(TI_EMI_REF,'0'))");
        sql.append(" AND COALESCE(CO_EMP_DES,'NULO') = COALESCE(? , COALESCE(CO_EMP_DES,'NULO'))");
        sql.append(" AND (  (   COALESCE(? , 'NULO') = 'SINEX'");
        sql.append("	   AND CO_EXP IS NULL");
        sql.append("	   )");
        sql.append("	OR (   COALESCE(? , 'NULO') != 'NULO'");
        sql.append("	   AND CO_EXP = ? ");
        sql.append("	   )");
        sql.append("	OR ?  IS NULL");
        sql.append("	)");
 
         try {
             result = this.jdbcTemplate.queryForInt(sql.toString(), new Object[]{buscarDocumentoRecep.getsCoAnnio(), buscarDocumentoRecep.getsCoDependencia(), buscarDocumentoRecep.getsTipoDoc(),
                buscarDocumentoRecep.getsEstadoDoc(), buscarDocumentoRecep.getsEstadoDoc(), buscarDocumentoRecep.getsPrioridadDoc(), buscarDocumentoRecep.getsPrioridadDoc(),
                buscarDocumentoRecep.getsTiAcceso(), buscarDocumentoRecep.getsCoEmpleado(), buscarDocumentoRecep.getsRemitente(), buscarDocumentoRecep.getsDestinatario(),
                buscarDocumentoRecep.getsExpediente(), buscarDocumentoRecep.getsExpediente(), buscarDocumentoRecep.getsExpediente(), buscarDocumentoRecep.getsExpediente()});
         } catch (Exception e) {
             e.printStackTrace();
         }
         return result; 
    }

     
    public String getDesEstadoDocRecepcion(String sesDocRec) {
        String result = null;
        StringBuffer sql = new StringBuffer();
        sql.append("select IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(?,'TDTV_DESTINOS')");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{sesDocRec});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

    public String validarAnulacionDocRecepcion(String pnuAnn, String pnuEmi, String pnuDes) {
        String vReturn = "NO_OK";
        StringBuffer sqlQry1 = new StringBuffer();
        sqlQry1.append("SELECT COUNT(re.nu_emi)\n"
                + "FROM IDOSGD.tdtr_referencia rf,\n"
                + "IDOSGD.tdtv_remitos   re\n"
                + "WHERE re.nu_ann = rf.nu_ann\n"
                + "AND re.nu_emi = rf.nu_emi\n"
                + "AND rf.nu_ann_ref = ?\n"
                + "AND rf.nu_emi_ref = ?\n"
                + "AND rf.nu_des_ref = ?\n"
                + "AND re.ES_DOC_EMI <> '9'");
        StringBuffer sqlQry2 = new StringBuffer();
        sqlQry2.append("SELECT COUNT(nu_emi)\n"
                + "FROM IDOSGD.tdtv_destinos\n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?\n"
                + "AND nu_des <> ?\n"
                + "AND es_doc_rec <> '0'\n"
                + "AND es_eli = '0'");
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.tdtv_remitos\n"
                + "SET es_doc_emi = '0' -- EMITIDO\n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?");
        try {
            String vResult = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class, new Object[]{pnuAnn, pnuEmi, Integer.parseInt(pnuDes)});
            if (vResult != null && vResult.equals("0")) {
                vResult = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class, new Object[]{pnuAnn, pnuEmi, Integer.parseInt(pnuDes)});
                if (vResult != null) {
                    if (vResult.equals("0")) {
                        this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pnuAnn, pnuEmi});
                    }
                    vReturn = "OK";
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn=e.getMessage();
        }
        return vReturn;
    }

    @Override
    public DocumentoBean existeDocumentoReferenciado(BuscarDocumentoRecepBean buscarDocumentoRecep) {
        StringBuffer sql = new StringBuffer();
        DocumentoBean documentoBean = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_DOC FROM IDOSGD.TDTV_REMITOS A\n"
                + "WHERE A.NU_ANN=?\n"
                + "AND A.CO_DEP_EMI=?\n"
                + "AND A.TI_EMI='01'\n"
                + "AND A.CO_TIP_DOC_ADM=?\n"
                + "AND A.NU_DOC_EMI=?\n"
                + "AND A.ES_ELI='0'\n"
                + "AND A.ES_DOC_EMI NOT IN ('5','7','9')");

        try {
            documentoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{buscarDocumentoRecep.getsCoAnnioBus(), buscarDocumentoRecep.getsBuscDestinatario(), buscarDocumentoRecep.getsDeTipoDocAdm(),
                        buscarDocumentoRecep.getsNumDocRef()});
            documentoBean.setMsjResult("OK");
        } catch (EmptyResultDataAccessException e) {
            documentoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoBean;
    }

    @Override
    public List<DocumentoBean> getDocumentosReferenciadoBusq(DocumentoBean documentoBean, String sTipoAcceso) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();

        sql.append("SELECT A.* ");
        sql.append(" FROM ( ");
        sql.append("SELECT DISTINCT A.NU_ANN,A.NU_EMI,A.TI_CAP,B.NU_COR_DES,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,A.FE_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP (A.NU_ANN, A.NU_EMI) DE_ORI_EMI,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,C.NU_DOC,"); 
        sql.append("CASE \n"
                + "WHEN B.TI_DES='01' THEN\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(B.CO_EMP_DES)\n"
                + "WHEN B.TI_DES='02' THEN\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(B.NU_RUC_DES)\n"
                + "WHEN B.TI_DES='03' THEN\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(B.NU_DNI_DES)\n"
                + "WHEN B.TI_DES='04' THEN\n"
                + " IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(B.CO_OTR_ORI_DES)\n"
                + "END AS DE_EMP_DES, ");
        sql.append(" UPPER (A.DE_ASU) DE_ASU_M,C.NU_EXPEDIENTE,");
        sql.append(" IDOSGD.PK_SGD_DESCRIPCION_ESTADOS (B.ES_DOC_REC,'TDTV_DESTINOS') DE_ES_DOC_DES,");
        sql.append(" C.IN_EXISTE_DOC EXISTE_DOC," 
                +" CASE"
                + " WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='00' THEN 0 "
                + " WHEN COALESCE(C.TI_EMI_REF,'0')||COALESCE(C.IN_EXISTE_ANEXO,'2')='02' THEN 0 "
                + " ELSE 1 END AS EXISTE_ANEXO,"
                + " B.NU_DES,COALESCE(B.CO_PRI,'1') CO_PRI,B.ES_DOC_REC");
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
        sql.append(" D, IDOSGD.TDTV_REMITOS A, IDOSGD.TDTV_DESTINOS B,IDOSGD.TDTX_REMITOS_RESUMEN C");
        sql.append(" WHERE");
        sql.append(" D.NU_ANN = A.NU_ANN");
        sql.append(" AND D.NU_EMI = A.NU_EMI");
        sql.append(" AND B.NU_ANN = A.NU_ANN");
        sql.append(" AND B.NU_EMI = A.NU_EMI");
        sql.append(" AND C.NU_ANN = B.NU_ANN");
        sql.append(" AND C.NU_EMI = B.NU_EMI");
        sql.append(" AND B.CO_DEP_DES = :pCoDepDes");
        if (sTipoAcceso.equals("1")) {
            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes");
            objectParam.put("pcoEmpDes", documentoBean.getCoEmpDes());
        }
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND B.ES_ELI = '0'");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7')");
        sql.append(" AND A.IN_OFICIO = '0'");

        objectParam.put("pCoAnio", documentoBean.getNuAnn());
        objectParam.put("pNuEmi", documentoBean.getNuEmi());
        objectParam.put("pCoDepDes", documentoBean.getCoDepDes());

        sql.append(" ORDER BY A.FE_EMI DESC");
        sql.append(") A ");
        sql.append(" LIMIT 100");

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String getVerificaAtendido(String pnuAnn, String pnuEmi, String pnuDes) {
        String vResult = "0";
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT count(a.nu_ann) nu_cantidad \n"
                + "FROM IDOSGD.tdtr_referencia a, IDOSGD.tdtv_remitos b \n"
                + "WHERE a.nu_ann = b.nu_ann \n"
                + "and a.nu_emi = b.nu_emi \n"
                + "and b.es_eli = '0' \n"
                + "and b.es_doc_emi <> '9' \n"
                + "and a.nu_ann_ref = ?\n"
                + "and a.nu_emi_ref = ? \n"
                + "and a.nu_des_ref = ?");

        try {
            vResult = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pnuEmi, Integer.parseInt(pnuDes)});
        } catch (Exception e) {
            vResult = "0";
            e.printStackTrace();
        }
        return vResult;
    }

    @Override
    public List<ReferenciaBean> getConsultaAtendido(String pnuAnn, String pnuEmi, String pnuDes) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT \n"
                + "IDOSGD.PK_SGD_DESCRIPCION_de_documento (b.co_tip_doc_adm) li_tip_doc, \n" 
                + "CASE WHEN b.ti_emi='01' THEN COALESCE(b.nu_doc_emi,' ') || '-' || b.nu_ann || '-' || COALESCE(b.de_doc_sig,' ') \n"
                + " WHEN b.ti_emi='05' THEN COALESCE(b.nu_doc_emi,' ') || '-' || b.nu_ann || '-' || COALESCE(b.de_doc_sig,' ') ELSE COALESCE(b.de_doc_sig,' ') END AS li_nu_doc, \n"
                + "TO_CHAR(b.fe_emi,'DD/MM/YYYY') fe_emi_corta, \n"
                + "b.nu_ann,\n"
                + "b.nu_emi,\n"
                + "cast(b.nu_cor_emi as text) nu_cor_emi,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_ESTADOS(b.es_doc_emi,'TDTV_REMITOS') DE_ES_DOC_EMI,\n"
                + "a.nu_ann_ref,\n"
                + "a.nu_emi_ref \n"
                + "FROM IDOSGD.tdtr_referencia a, IDOSGD.tdtv_remitos b \n"
                + "WHERE a.nu_ann = b.nu_ann \n"
                + "and a.nu_emi = b.nu_emi \n"
                + "and b.es_eli = '0' \n"
                + "and b.es_doc_emi <> '9'\n"
                + "and a.nu_ann_ref = ? \n"
                + "and a.nu_emi_ref = ? \n"
                + "and a.nu_des_ref = ? \n"
                + "order by b.nu_cor_emi desc");

        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class), new Object[]{pnuAnn, pnuEmi, Integer.parseInt(pnuDes)});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;

    }

    @Override
    public String updEtiquetaTipoRecepDocumento(DocumentoBean documentoBean) {
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE IDOSGD.TDTV_DESTINOS ");
        sql.append("SET ti_fisico_rec =?,co_etiqueta_rec =?");
        sql.append(" WHERE NU_ANN = ? ");
        sql.append("AND NU_EMI = ? ");
        sql.append("AND NU_DES = ? ");
        String vReturn = "NO_OK";
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                documentoBean.getTiFisicoRec(),documentoBean.getCoEtiquetaRec(),
                documentoBean.getNuAnn(), documentoBean.getNuEmi(), Integer.parseInt(documentoBean.getNuDes())});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public DocumentoBean getEstadoDocumento(String nuAnn, String nuEmi, String nuDes) {
        StringBuffer sql = new StringBuffer();
        DocumentoBean documentoBean = null;
        sql.append("SELECT CO_EMP_DES,ES_DOC_REC,NU_ANN,NU_EMI,NU_DES,TI_DES,CO_DEP_DES\n" +
        "FROM IDOSGD.TDTV_DESTINOS\n" +
        "WHERE NU_ANN = ?\n" +
        "AND NU_EMI = ? AND NU_DES = ? AND ES_ELI='0'");        
        
        try {
             documentoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{nuAnn,nuEmi,Integer.parseInt(nuDes)});
        } catch (EmptyResultDataAccessException e) {
            documentoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return documentoBean;          
    }

    @Override
    public String getEstadoDocAdmBasico(String nuAnn, String nuEmi){
        String vResult="NO_OK";
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT ES_DOC_EMI\n" +
                    "FROM IDOSGD.TDTV_REMITOS\n" +
                    "WHERE nu_ann = ? \n" +
                    "AND nu_emi = ? \n" +
                    //"AND ES_DOC_EMI NOT IN ('5', '9', '7') AND ES_ELI='0'");        
                    "AND ES_ELI='0'");        
        
        try {
             vResult = this.jdbcTemplate.queryForObject(sql.toString(), String.class,
                    new Object[]{nuAnn,nuEmi});
        } catch (EmptyResultDataAccessException e) {
            vResult = "NO_OK";
        } catch (Exception e) {
            vResult = "NO_OK";
            e.printStackTrace();
        }
        return vResult;
    }
    
    //servicio rest notificaciones movil
    //Anula la recepcion de un documento
    @Override
    public String updAnulaRecepecionDocumentoBean(String nuAnn, String nuEmi, String nuDes, String coUseMod) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE IDOSGD.TDTV_DESTINOS ");
        sql.append("SET ES_DOC_REC = '0', CO_USE_MOD = ?, FE_USE_MOD = now()");
        sql.append(",CO_EMP_REC=NULL,FE_REC_DOC=NULL,FE_ATE_DOC=NULL,FE_ARC_DOC=NULL");
        sql.append(",NU_COR_DES=NULL");
        sql.append(" WHERE NU_ANN = ? ");
        sql.append("AND NU_EMI = ? ");
        sql.append("AND NU_DES = ? ");
        String vReturn = "NO_OK";
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                coUseMod, nuAnn, nuEmi, nuDes});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String updRecepcionDocumentoBean(DocumentoBean documentoBean) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE IDOSGD.TDTV_DESTINOS SET ");
        sql.append("ES_DOC_REC = ?,");
        sql.append("CO_USE_MOD = ?,");
        sql.append("CO_EMP_REC ='");
        sql.append(documentoBean.getCoEmpRec());
        sql.append("',FE_USE_MOD = now(),");
        sql.append(" FE_REC_DOC = TO_TIMESTAMP('");
        sql.append(documentoBean.getFeRecDoc());
        sql.append("','DD/MM/YYYY HH24:MI:SS'),");
        
        if(documentoBean.getFeAteDoc()!= null && !documentoBean.getFeAteDoc().equals("")){
        sql.append(" FE_ATE_DOC=TO_TIMESTAMP('");
        sql.append(documentoBean.getFeAteDoc());
        sql.append("','DD/MM/YYYY HH24:MI'),");
        }
        if(documentoBean.getFeArcDoc()!= null && !documentoBean.getFeArcDoc().equals("")){
        sql.append(" FE_ARC_DOC=TO_TIMESTAMP('");
        sql.append(documentoBean.getFeArcDoc());
        sql.append("','DD/MM/YYYY HH24:MI'),");
        }
        sql.append(" NU_COR_DES=?,");
        sql.append(" DE_ANE=?");
        sql.append(" WHERE NU_ANN = ? ");
        sql.append(" AND NU_EMI = ? ");
        sql.append(" AND NU_DES = ? ");
        String vReturn = "NO_OK";
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                documentoBean.getEsDocRec(), documentoBean.getCodUseMod(), documentoBean.getNuCorDes(),
                documentoBean.getDeAne(), documentoBean.getNuAnn(), documentoBean.getNuEmi(), documentoBean.getNuDes()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String updAtencionDocumentoBean(DocumentoBean documentoBean) {
        StringBuilder sql = new StringBuilder();
        sql.append("UPDATE IDOSGD.TDTV_DESTINOS SET ");
        sql.append("ES_DOC_REC = ?,");
        sql.append("CO_USE_MOD = ?,");                          
        if(documentoBean.getFeAteDoc()!=null && documentoBean.getFeAteDoc().trim().length()>0){
            sql.append(" FE_ATE_DOC=TO_TIMESTAMP('");
            sql.append(documentoBean.getFeAteDoc());
            sql.append("','DD/MM/YYYY HH24:MI'),");
        }
        if(documentoBean.getFeArcDoc()!=null && documentoBean.getFeArcDoc().trim().length()>0){
            sql.append(" FE_ARC_DOC=TO_TIMESTAMP('");
            sql.append(documentoBean.getFeArcDoc());
            sql.append("','DD/MM/YYYY HH24:MI'), ");
        }        
        sql.append(" FE_USE_MOD = NOW() ");   
        sql.append(" WHERE NU_ANN = ? ");
        sql.append(" AND NU_EMI = ? ");
        sql.append(" AND NU_DES = ? ");
        String vReturn = "NO_OK";
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                documentoBean.getEsDocRec(), documentoBean.getCodUseMod(), documentoBean.getNuAnn(), documentoBean.getNuEmi(), documentoBean.getNuDes()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

}
