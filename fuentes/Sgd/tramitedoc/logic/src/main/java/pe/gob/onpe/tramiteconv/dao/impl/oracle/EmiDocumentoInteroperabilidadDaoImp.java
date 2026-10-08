package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.ParameterizedBeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import org.xml.sax.SAXException;
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
import pe.gob.onpe.tramitedoc.bean.EstadoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaEmiDocBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaRemitoBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteEmiBean;
import pe.gob.onpe.tramitedoc.bean.TblRemitosBean;
import pe.gob.onpe.tramitedoc.bean.TipoCategoriaBean;
import pe.gob.onpe.tramitedoc.bean.TipoDocumentoBean;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoInteroperabilidadDao;
import pe.gob.onpe.tramitedoc.dao.ProveedorDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.util.Constantes;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import pe.gob.segdi.pide.consultas.bean.ConsultaSunatBean;
import pe.gob.segdi.pide.consultas.ws.WSConsultaRuc;

@Repository("emiDocumentoInteroperabilidadDao")
public class EmiDocumentoInteroperabilidadDaoImp extends SimpleJdbcDaoBase implements EmiDocumentoInteroperabilidadDao {
private SimpleJdbcCall spEmiDocumento;
private SimpleJdbcCall spProveedor;

    @Autowired
    private ProveedorDao proveedorDao;
    
    @Autowired
    private DatosPlantillaDao datosPlantillaDao;
    
    @Override
    public List<DocumentoEmiBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmi) {
        StringBuffer sql = new StringBuffer();
        boolean bBusqFiltro = false;
        String sOrdenList="";
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocumentoEmiBean> list = new ArrayList<DocumentoEmiBean>();

        sql.append("SELECT X.*,");
        sql.append(" PK_SGD_DESCRIPCION.DE_EMI_REF(X.NU_ANN, X.NU_EMI) DE_EMI_REF,");        
        sql.append(" PK_SGD_DESCRIPCION.DE_DOCUMENTO(X.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");        
        sql.append(" DECODE(X.NU_CANDES, 1, PK_SGD_DESCRIPCION.TI_DES_EMP(X.NU_ANN, X.NU_EMI),PK_SGD_DESCRIPCION.TI_DES_EMP_V(X.NU_ANN, X.NU_EMI)) DE_EMP_PRO,");
        sql.append(" PK_SGD_DESCRIPCION.DE_NOM_EMP(X.CO_EMP_RES) DE_EMP_RES,");        
        sql.append(" PK_SGD_DESCRIPCION.ESTADOS(X.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,");        
        sql.append("ROWNUM");
        sql.append(" FROM ( ");
        sql.append(" SELECT ");
        sql.append(" A.NU_COR_EMI,A.FE_EMI,DECODE(NVL(B.TI_EMI_REF,'0')||NVL(B.IN_EXISTE_ANEXO,'2'),'00',0,'02',0,1) EXISTE_ANEXO, NVL(B.CO_PRIORIDAD,'1') CO_PRIORIDAD,");
        sql.append(" TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,");
        sql.append(" B.NU_DOC,UPPER(A.DE_ASU) DE_ASU_M,A.NU_ANN,B.NU_EXPEDIENTE,A.NU_EMI,A.TI_CAP,B.IN_EXISTE_DOC EXISTE_DOC,");
        sql.append(" A.ES_DOC_EMI,A.CO_TIP_DOC_ADM,A.NU_CANDES,A.CO_EMP_RES,NVL(A.DOC_ESTADO_MSJ,-1) as DOC_ESTADO_MSJ,");
        sql.append(" (SELECT C.TI_DES FROM TDTV_DESTINOS C WHERE C.NU_EMI=A.NU_EMI AND C.NU_ANN=A.NU_ANN and ROWNUM=1) as tiDest,A.TI_ENV_MSJ tiEnvMsj,");
        sql.append(" CASE A.TI_ENV_MSJ WHEN '0' THEN 'MESA DE PARTES' WHEN '1' THEN 'ENVÍO DIRECTO' END    recursoenvio");
        sql.append(" FROM TDTV_REMITOS A,TDTX_REMITOS_RESUMEN B");
        
        sql.append(" WHERE");
        sql.append(" B.NU_ANN=A.NU_ANN");
        sql.append(" AND B.NU_EMI=A.NU_EMI");
        String pNUAnn = buscarDocumentoEmi.getsCoAnnio();
        if (!(buscarDocumentoEmi.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNUAnn);
        }
        sql.append(" AND A.TI_EMI='06'");
        sql.append(" AND B.TI_EMI='06'");
        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND A.CO_GRU = '6'");
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
                sql.append(" AND INSTR(B.TI_EMI_REF, :pTiEmiRef) > 0 ");
                objectParam.put("pTiEmiRef", buscarDocumentoEmi.getsRefOrigen());
            }
            if (buscarDocumentoEmi.getsDestinatario() != null && buscarDocumentoEmi.getsDestinatario().trim().length() > 0) {
                sql.append(" AND INSTR(B.TI_EMI_DES, :pTiEmpPro) > 0 ");
                objectParam.put("pTiEmpPro", buscarDocumentoEmi.getsDestinatario());
            }
            if (buscarDocumentoEmi.getEsFiltroFecha() != null
                    && (buscarDocumentoEmi.getEsFiltroFecha().equals("1") || buscarDocumentoEmi.getEsFiltroFecha().equals("3"))) {
                String vFeEmiIni = buscarDocumentoEmi.getsFeEmiIni();
                String vFeEmiFin = buscarDocumentoEmi.getsFeEmiFin();
                if (vFeEmiIni != null && vFeEmiIni.trim().length() > 0
                        && vFeEmiFin != null && vFeEmiFin.trim().length() > 0) {
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + 0.99999");
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);
                }
            }

        }

        //Busqueda
        if (pTipoBusqueda.equals("1")) {
            if (buscarDocumentoEmi.getsNumCorEmision() != null && buscarDocumentoEmi.getsNumCorEmision().trim().length() > 0) {
                sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
                objectParam.put("pnuCorEmi", buscarDocumentoEmi.getsNumCorEmision());
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
        sql.append(") X ");
        sql.append("WHERE ROWNUM < 101");

      
        //System.out.println("sql:"+sql);
        
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
    public DocumentoEmiBean getDocumentoEmitido(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT \n"
                + "a.nu_ann, \n"
                + "a.nu_emi, \n"
                + "to_char(a.nu_cor_emi) nu_cor_emi, \n"
                + "a.co_loc_emi, PK_SGD_DESCRIPCION.de_local(a.co_loc_emi) de_loc_emi, \n"
                + "a.co_dep_emi, PK_SGD_DESCRIPCION.de_dependencia(a.co_dep_emi) de_dep_emi,\n"
                + "a.ti_emi, PK_SGD_DESCRIPCION.ti_destino(a.ti_emi) de_tip_emi,\n"
                + "a.co_emp_emi, PK_SGD_DESCRIPCION.de_nom_emp(co_emp_emi) de_emp_emi,\n"
                + "a.co_emp_res, PK_SGD_DESCRIPCION.de_nom_emp(a.co_emp_res) de_emp_res,\n"
                + "a.nu_dni_emi, \n"
                + "a.nu_ruc_emi, \n"
                + "a.co_otr_ori_emi,\n"
                + "PK_SGD_TRAMITE.ti_emi_emp (a.nu_ann, a.nu_emi) de_ori_emi,\n"
                + "TO_CHAR(a.fe_emi, 'DD/MM/YYYY') fe_emi, \n"
                + "TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,\n"
                + "a.co_tip_doc_adm, PK_SGD_DESCRIPCION.de_documento(a.co_tip_doc_adm) de_tip_doc_adm,\n"
                + "a.ti_emi,\n"
                + "a.nu_doc_emi,\n"
                + "a.de_doc_sig,\n"
                + "a.es_doc_emi, PK_SGD_DESCRIPCION.estados (a.es_doc_emi,'TDTV_REMITOS') de_es_doc_emi,\n"
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
                + "PK_SGD_DESCRIPCION.DE_NU_EXPEDIENTE(a.nu_ann_exp,a.nu_sec_exp) NU_EXPEDIENTE\n"
                + "FROM tdtv_remitos a \n"
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
                + " PK_SGD_DESCRIPCION.DE_NOM_EMP(A.CO_EMP_EMI) DE_EMP_EMI,"
                + " PK_SGD_DESCRIPCION.DE_NOM_EMP(A.CO_EMP_RES) DE_EMP_RES,\n"
                + " TO_CHAR(A.FE_EMI,'DD/MM/YYYY') FE_EMI_CORTA,"
                + " PK_SGD_DESCRIPCION.ESTADOS(A.ES_DOC_EMI,'TDTV_REMITOS') DE_ES_DOC_EMI,\n"
                + " B.FE_EXP,TO_CHAR(B.FE_EXP,'DD/MM/YYYY') FE_EXP_CORTA,B.NU_EXPEDIENTE,\n"
                + " B.CO_PROCESO,"
                + " PK_SGD_DESCRIPCION.DE_PROCESO_EXP(B.CO_PROCESO) DE_PROCESO,\n"
                + " RR.IN_FIRMA_ANEXO\n"
                + " from TDTV_REMITOS A left join TDTC_EXPEDIENTE B\n"
                + " on A.NU_ANN_EXP = B.NU_ANN_EXP and A.NU_SEC_EXP = B.NU_SEC_EXP,\n"
                + " TDTX_REMITOS_RESUMEN RR\n"
                + " where\n"
                + " A.NU_ANN = ? AND A.NU_EMI = ?\n"
                + " AND RR.NU_ANN = ? AND RR.NU_EMI = ?");

        DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
        try {
            documentoEmiBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoEmiBean.class),
                    new Object[]{pnuAnn, pnuEmi, pnuAnn, pnuEmi});
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
        sql.append("select B.NU_EXPEDIENTE,B.FE_EXP,PK_SGD_DESCRIPCION.DE_PROCESO_EXP(B.CO_PROCESO) DE_PROCESO,TO_CHAR(B.FE_EXP,'DD/MM/YYYY') FE_EXP_CORTA,\n"
                + "B.NU_ANN_EXP,B.NU_SEC_EXP,B.CO_PROCESO\n"
                + "from TDTC_EXPEDIENTE B \n"
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
        StringBuffer sql = new StringBuffer();
        sql.append("select A.CO_LOC CO_LOCAL,PK_SGD_DESCRIPCION.DE_LOCAL(A.CO_LOC) DE_LOCAL,B.CO_EMPLEADO CEMP_CODEMP,PK_SGD_DESCRIPCION.DE_NOM_EMP(B.CO_EMPLEADO)COMP_NAME\n"
                + "from sitm_local_dependencia A,rhtm_dependencia B\n"
                + "where B.CO_DEPENDENCIA = ? and A.CO_DEP=?");

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
                    "FROM RHTM_PER_EMPLEADOS e, \n" +
                    "( \n" +
                    "SELECT CEMP_CODEMP \n" +
                    "FROM RHTM_PER_EMPLEADOS \n" +
                    "where CEMP_EST_EMP = '1' \n" +
                    "  and (CO_DEPENDENCIA=? or CEMP_CO_DEPEND=?) \n" +
                    "union \n" +
                    "select co_emp from tdtx_dependencia_empleado  where co_dep=? and es_emp='0' \n" +
                    "union \n" +
                    "select co_empleado from rhtm_dependencia where co_dependencia = ? \n" +
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
                + "  FROM tdtr_motivo         a,\n"
                + "       tdtx_moti_docu_depe b\n"
                + " WHERE a.co_mot     = b.co_mot\n"
                + "   AND b.co_dep     = ?\n"
                + "   AND b.co_tip_doc = ?\n"
                + "UNION\n"
                + "SELECT de_mot,\n"
                + "       co_mot\n"
                + "FROM tdtr_motivo  \n"
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

        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");        
        
        sql.append("select *\n"
                + "from("
                + "select \n"
                + "RE.fe_emi,\n"
                + "SUBSTR(PK_SGD_DESCRIPCION.DE_DOCUMENTO(RE.co_tip_doc_adm),1,100) DE_TIP_DOC_ADM,\n"
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
                + "from TDTV_REMITOS RE,TDTX_REMITOS_RESUMEN RR\n"
                + "where RE.nu_ann = ?\n"
                + "AND RE.NU_ANN=RR.NU_ANN\n"
                + "AND RE.NU_EMI=RR.NU_EMI\n"
                + "AND RE.es_eli = '0'\n"
                + "and RE.es_doc_emi not in ('9','7','5')\n"
                + "AND RE.CO_GRU = '1'\n"
                + "AND RE.co_dep_emi in \n"
                + "(select ?/*:b_02.co_dep_emi*/ from dual \n"
                + "union \n"
                + "  SELECT co_dep_ref\n"
                + "    FROM tdtx_referencia\n"
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
                + "SUBSTR(PK_SGD_DESCRIPCION.DE_DOCUMENTO(RE.co_tip_doc_adm),1,100) TIPO,\n"
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
                + "from TDTV_REMITOS RE,TDTX_REMITOS_RESUMEN RR\n"
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
        sql.append("WHERE ROWNUM < 201");                            

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
        List<DocumentoBean> list = new ArrayList<DocumentoBean>();

        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");
        
    
        sql.append("select *\n"+
                    "from(\n"+
                    "select\n"+
                    "d.FE_REC_DOC,\n" +
                    "SUBSTR(PK_SGD_DESCRIPCION.de_documento(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(d.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "d.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(PK_SGD_DESCRIPCION.de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "from TDTV_REMITOS R,TDTV_DESTINOS D,TDTX_REMITOS_RESUMEN RR\n" +
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
                    "(select ?/*:b_02.co_dep_emi*/ from dual \n" +
                    "union \n" +
                    "  SELECT co_dep_ref\n" +
                    "    FROM tdtx_referencia\n" +
                    "   WHERE co_dep_emi = ?/*:b_02.co_dep_emi*/\n" +
                    "     AND ti_emi = 'D'\n" +
                    "     AND es_ref = '1')\n" +
                    "AND r.co_tip_doc_adm = ?/*:B_03.LI_TIP_DOC*/\n");
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
                    }
                    sql.append(") E\n"+
                    "order by 1 desc");
                    
	sql.append(") A ");
        sql.append("WHERE ROWNUM < 201");    

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{pannio,pcoDepen, pcoDepen, ptiDoc});
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
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,substr(PK_SGD_DESCRIPCION.DE_LOCAL(a.co_loc_des), 1, 100) de_local,\n"
                + "a.co_dep_des co_dependencia,substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.co_dep_des), 1, 100) de_dependencia,\n"
                + "a.co_emp_des co_empleado,substr(PK_SGD_DESCRIPCION.DE_NOM_EMP(a.co_emp_des), 1, 100) de_empleado,\n"
                + "a.co_mot co_tramite,substr(PK_SGD_DESCRIPCION.MOTIVO(a.co_mot), 1, 100) de_tramite,\n"
                + "a.co_pri co_prioridad,\n"
                + "a.de_pro de_indicaciones\n"
                + "FROM tdtv_destinos a\n"
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
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,a.co_loc_des co_local,NVL2(a.co_loc_des,substr(PK_SGD_DESCRIPCION.DE_LOCAL(a.co_loc_des), 1, 100),NULL) de_local,\n" +
                        "                a.co_dep_des co_dependencia,NVL2(a.co_dep_des,substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.co_dep_des), 1, 100),NULL) de_dependencia,\n" +
                        "                a.co_emp_des co_empleado,NVL2(a.co_emp_des,substr(PK_SGD_DESCRIPCION.DE_NOM_EMP(a.co_emp_des), 1, 100),NULL) de_empleado,\n" +
                        "                a.co_mot co_tramite,NVL2(a.co_mot,substr(PK_SGD_DESCRIPCION.MOTIVO(a.co_mot), 1, 100),NULL) de_tramite,\n" +
                        "                a.co_pri co_prioridad,\n" +
                        "                a.de_pro de_indicaciones,\n" +
                        "                a.NU_RUC_DES nu_ruc,NVL2(a.NU_RUC_DES,substr(PK_SGD_DESCRIPCION.DE_PROVEEDOR(a.NU_RUC_DES), 1, 100),NULL) de_proveedor,\n" +
                        "                a.NU_DNI_DES nu_dni,NVL2(a.NU_DNI_DES,substr(PK_SGD_DESCRIPCION.ANI_SIMIL(a.NU_DNI_DES), 1, 100),NULL) de_ciudadano,\n" +
                        "                a.CO_OTR_ORI_DES co_otro_origen,\n" +
                        "                NVL2(a.CO_OTR_ORI_DES,de_otro_origen_full,NULL) de_otro_origen_full, a.ti_des co_tipo_destino, NVL(a.ES_ENV_POR_TRA,'0') ENV_MESA_PARTES,\n" +
                        "                CDIR_REMITE,CEXP_CORREOE,CTELEFONO,CCOD_DPTO,CCOD_PROV,CCOD_DIST,\n" +
                        "                trim(UB.NODEP)||' '||trim(UB.NOPRV)||' '||trim(UB.NODIS) ubigeo \n" +
                        "                ,A.REMI_TI_EMI   ,DECODE(A.REMI_TI_EMI,                                             \n" +
                        "                '03', PK_SGD_DESCRIPCION.ANI_SIMIL (A.REMI_NU_DNI_EMI),\n" +
                        "                '04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (A.REMI_CO_OTR_ORI_EMI)\n" +
                        "                 )  nombres,\n" +
                        "                 DECODE(A.REMI_TI_EMI,                                             \n" +
                        "                 '03', REMI_NU_DNI_EMI,\n" +
                        "                 '04', REMI_CO_OTR_ORI_EMI\n" +
                        "                  )  REMI_NU_DNI_EMI,DE_CARGO AS cargo,A.CIDCAT,(SELECT VDESCAT FROM IOTDX_CATEGORIA WHERE CIDCAT=A.CIDCAT) DECAT,A.DE_DEP_DES,A.DE_NOM_DES,A.DE_CAR_DES \n"+
                        "                FROM tdtv_destinos a\n" +
                        "                LEFT JOIN ( SELECT C.CO_OTR_ORI,C.DE_APE_PAT_OTR||' '||C.DE_APE_MAT_OTR||', '||C.DE_NOM_OTR || ' - ' ||\n" +
                        "                     C.DE_RAZ_SOC_OTR ||'##'||  NVL(B.CELE_DESELE,'   ') ||'##'||  C.NU_DOC_OTR_ORI  de_otro_origen_full\n" +
                        "                  FROM TDTR_OTRO_ORIGEN C LEFT JOIN  (\n" +
                        "                  SELECT CELE_CODELE, CELE_DESELE  FROM SI_ELEMENTO  WHERE CTAB_CODTAB ='TIP_DOC_IDENT') B\n" +
                        "                  ON C.CO_TIP_OTR_ORI = B.CELE_CODELE ) OTRDES ON OTRDES.CO_OTR_ORI=a.CO_OTR_ORI_DES\n" +
                        "                LEFT JOIN IDTUBIAS UB ON UB.UBDEP=a.CCOD_DPTO AND UB.UBPRV=a.CCOD_PROV AND UB.UBDIS=a.CCOD_DIST\n" +
                         
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
                + "PK_SGD_DESCRIPCION.de_documento (a.co_tip_doc_adm) de_tipo_doc, \n"
                + "DECODE (a.ti_emi,'01', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,\n"
                + "         '05', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,a.de_doc_sig) li_nu_doc,\n"
                + "substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.co_dep_emi), 1, 100) de_dep_emi, \n"
                + "TO_CHAR(a.fe_emi,'DD/MM/YY') fe_emi_corta, \n"
                + "b.nu_ann,\n"
                + "b.nu_emi,\n"
                + "nvl(trim(to_char(b.nu_des)),'N') nu_des ,\n"
                + "b.nu_ann_ref,\n"
                + "b.nu_emi_ref,\n"
                + "nvl(trim(to_char(b.nu_des_ref)),'N') nu_des_ref,\n"
                + "b.co_ref,\n"
                + "DECODE(nvl(trim(to_char(b.nu_des_ref)),'N'),'N','emi','rec') tip_doc_ref,\n"
                + "a.co_tip_doc_adm,'BD' accion_bd\n"
                + "FROM tdtv_remitos a,TDTR_REFERENCIA b \n"
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
//        sql.append("SELECT nvl(MAX(ti_des),'01')\n" +
        sql.append("SELECT MAX(ti_des)\n"
                + "/*INTO :b_02.li_ti_des*/\n"
                + "FROM tdtv_destinos\n"
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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update tdtv_remitos A \n"
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
                    + ",A.NU_COR_DOC=nvl2(?,?,(SELECT nvl(MAX(nu_cor_doc), 0) + 1\n"
                    + "FROM tdtv_remitos\n"
                    + "WHERE nu_ann = ?\n"
                    + "AND co_dep_emi = ?\n"
                    + "AND co_tip_doc_adm = ?\n"
                    + "AND ti_emi = ?)),\n");
        }
        if (expedienteBean != null) {
            sqlUpd.append("A.NU_SEC_EXP='" + expedienteBean.getNuSecExp() + "',\n");
        }
        if (remitenteEmiBean != null) {
            sqlUpd.append("A.CO_LOC_EMI='" + remitenteEmiBean.getCoLocal() + "'\n"
                    + ",A.CO_DEP_EMI='" + remitenteEmiBean.getCoDependencia() + "'\n"
                    + ",A.CO_EMP_EMI='" + remitenteEmiBean.getCoEmpFirma() + "',\n");
        }
        sqlUpd.append("A.FE_USE_MOD=SYSDATE,A.NU_FOL=? "
                + "where\n"
                + "A.NU_ANN=? and\n"
                + "A.NU_EMI=?");

        try {
            if (documentoEmiBean != null) {
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, documentoEmiBean.getDeAsu(), documentoEmiBean.getNuDiaAte(),
                    documentoEmiBean.getCoTipDocAdm(), documentoEmiBean.getEsDocEmi(), documentoEmiBean.getNuDocEmi(), documentoEmiBean.getDeDocSig(),
                    documentoEmiBean.getNuDocEmi(), documentoEmiBean.getNuCorDoc(), nuAnn, documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm(),
                    documentoEmiBean.getTiEmi(),documentoEmiBean.getNuFol(), nuAnn, nuEmi});
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
        sqlQry.append("select lpad(SEC_REMITOS_NU_EMI.NextVal, 10, '0') from dual");
        sqlQry1.append("SELECT nvl(MAX(nu_cor_emi), 0) + 1\n"
                + "FROM tdtv_remitos\n"
                + "WHERE nu_ann = ?\n"
                + "AND co_dep_emi = ?\n"
                + "AND ti_emi = '06'");

        sqlUpd.append("INSERT INTO tdtv_remitos(\n"
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
                + "FE_USE_MOD,NU_FOL)\n"
                + "VALUES(?,?,?,?,?,?,?,'06',SYSDATE,'6',?,?,?,'03',?,\n"
                + "?,?,?,?,1,\n"
                + "'0',?,?,SYSDATE,?,SYSDATE,?)");
        
        try {
            String snuEmi = this.jdbcTemplate.queryForObject(sqlQry.toString(), String.class);
            String snuCorEmi = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class, new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getCoDepEmi()});
            documentoEmiBean.setNuEmi(snuEmi);
            documentoEmiBean.setNuCorEmi(snuCorEmi);
            //documentoEmiBean.setNuCorDoc(vnuCorDoc);

//            System.out.println(documentoEmiBean.getNuAnn());
//            System.out.println(documentoEmiBean.getCoDepEmi());
//            System.out.println(documentoEmiBean.getCoTipDocAdm());
//            System.out.println(snuCorEmi);
//            System.out.println(documentoEmiBean.getNuCorDoc());
            
            
            
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getNuAnn(), snuEmi, snuCorEmi,
                documentoEmiBean.getCoLocEmi(), documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoEmpEmi(), documentoEmiBean.getCoEmpRes(),
                documentoEmiBean.getDeAsu(), documentoEmiBean.getEsDocEmi(), documentoEmiBean.getNuDiaAte(),documentoEmiBean.getCoTipDocAdm(),
                documentoEmiBean.getNuCorDoc(), documentoEmiBean.getDeDocSig(),documentoEmiBean.getNuAnnExp(), documentoEmiBean.getNuSecExp(), 
                documentoEmiBean.getNuDocEmi(),documentoEmiBean.getCoUseMod(), documentoEmiBean.getCoUseMod(),documentoEmiBean.getNuFol()});

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
        StringBuffer sqlQry2 = new StringBuffer();        
        StringBuffer sqlUpdUbigeo = new StringBuffer();
        sqlQry1.append("SELECT CELE_CODELE FROM SI_ELEMENTO WHERE CTAB_CODTAB='UPD_PARAMT_UBIGEO'");
        sqlQry2.append("SELECT COUNT(*) FROM LG_PRO_PROVEEDOR WHERE CPRO_RUC='"+destinatarioDocumentoEmiBean.getNuRuc()+"'");
        
        sqlUpd.append("INSERT INTO TDTV_DESTINOS(\n"
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
                + "ES_ENV_POR_TRA,CDIR_REMITE,CEXP_CORREOE,CTELEFONO,CCOD_DPTO,CCOD_PROV,CCOD_DIST,REMI_TI_EMI,REMI_NU_DNI_EMI,REMI_CO_OTR_ORI_EMI,DE_CARGO,CIDCAT,DE_DEP_DES,DE_NOM_DES,DE_CAR_DES)\n"
                + "VALUES(?,?,(select nvl(MAX(a.nu_des) + 1,1) FROM tdtv_destinos a where \n"
                + "A.NU_ANN=? and\n"
                + "A.NU_EMI=?),?,?,?,?,?,?,?,?,?,?,'0',SYSDATE,SYSDATE,?,?,'0',?,?,?,?,?,?,?,?,?,?,?,?,?,?,?)");
        


        try {
            String []ubigeo = null;
            String dep="";
            String pro="";
            String dis="";
            
            String siExisteRuc = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class);
            
            if (siExisteRuc.equalsIgnoreCase("0"))
            {
                                         
                try {
                        WSConsultaRuc wSSunat = new WSConsultaRuc();
                        ConsultaSunatBean beanDdp;
                
                        beanDdp = wSSunat.consultaSunat(datosPlantillaDao.getParametros("URL_RZ_SUNAT_REST"),destinatarioDocumentoEmiBean.getNuRuc());
                        if(beanDdp.getDdp_nombre()!=null){ 
                            
                        proveedorDao.insPideProveedor(beanDdp);
                            
                        }
                } catch (SAXException ex) {
//                    Logger.getLogger(EmiDocumentoInteroperabilidadDaoImp.class.getName()).log(Level.SEVERE, null, ex);
                } catch (IOException ex) {
//                    Logger.getLogger(EmiDocumentoInteroperabilidadDaoImp.class.getName()).log(Level.SEVERE, null, ex);
                }

            }

            
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
            dep,pro,dis,destinatarioDocumentoEmiBean.getRemiTiEmi(),destinatarioDocumentoEmiBean.getRemiNuDniEmi(),destinatarioDocumentoEmiBean.getRemiCoOtrOriEmi(),
            destinatarioDocumentoEmiBean.getCargo(),destinatarioDocumentoEmiBean.getCidCat(),destinatarioDocumentoEmiBean.getDeDepDes(),destinatarioDocumentoEmiBean.getDeNomDes(),destinatarioDocumentoEmiBean.getDeCarDes()});
            
            
            String isUpdateUbigeo = this.jdbcTemplate.queryForObject(sqlQry1.toString(), String.class);
            if("1".equals(isUpdateUbigeo)){            
                if("02".equals(destinatarioDocumentoEmiBean.getCoTipoDestino())){   
                    sqlUpdUbigeo.append("UPDATE  lg_pro_proveedor SET cpro_domicil=?,cpro_ubigeo=?,cpro_telefo=?,cpro_email=?,cubi_coddep=?,\n" +
                                        "    cubi_codpro=?,cubi_coddis=?  WHERE cpro_ruc=?");
                    this.jdbcTemplate.update(sqlUpdUbigeo.toString(), new Object[]{destinatarioDocumentoEmiBean.getCdirRemite(),dep+pro+dis,
                        destinatarioDocumentoEmiBean.getcTelefono(),destinatarioDocumentoEmiBean.getCexpCorreoe(),dep,pro,dis,destinatarioDocumentoEmiBean.getNuRuc()});
                }
                if("03".equals(destinatarioDocumentoEmiBean.getCoTipoDestino())){   
                    sqlUpdUbigeo.append(" UPDATE  TDTX_ANI_SIMIL  SET  DEDOMICIL=?,DETELEFO=?, DEEMAIL=?,UBDEP=?,UBPRV=?, UBDIS=?  WHERE  NULEM=? ");
                    this.jdbcTemplate.update(sqlUpdUbigeo.toString(), new Object[]{destinatarioDocumentoEmiBean.getCdirRemite(),
                        destinatarioDocumentoEmiBean.getcTelefono(),destinatarioDocumentoEmiBean.getCexpCorreoe(),dep,pro,dis,destinatarioDocumentoEmiBean.getNuDni()});
                }
                if("04".equals(destinatarioDocumentoEmiBean.getCoTipoDestino())){   
                    sqlUpdUbigeo.append("UPDATE TDTR_OTRO_ORIGEN SET  DE_DIR_OTRO_ORI=?,DE_TELEFO=?,DE_EMAIL=?,UB_DEP=?, UB_PRO=?, UB_DIS=?   WHERE  CO_OTR_ORI=?    ");
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
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update TDTV_DESTINOS \n"
                + "set CO_LOC_DES=?\n"
                + ",CO_DEP_DES=?\n"
                + ",CO_EMP_DES=?\n"
                + ",CO_PRI=?\n"
                + ",DE_PRO=?\n"
                + ",CO_MOT=?\n"
                + ",CO_OTR_ORI_DES=? \n"
                + ",NU_DNI_DES=? \n"
                + ",NU_RUC_DES=? \n"
                + ",FE_USE_MOD=SYSDATE\n"
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
                + ",CIDCAT=? \n"
                + ",DE_DEP_DES=? \n"
                + ",DE_NOM_DES=? \n"
                + ",DE_CAR_DES=? \n"
                + "WHERE\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=? and\n"
                + "NU_DES=?"); 
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
               destinatarioDocumentoEmiBean.getCidCat(),destinatarioDocumentoEmiBean.getDeDepDes(),destinatarioDocumentoEmiBean.getDeNomDes(),destinatarioDocumentoEmiBean.getDeCarDes(),
                nuAnn, nuEmi, destinatarioDocumentoEmiBean.getNuDes(),});
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
        sqlDel.append("delete from TDTV_DESTINOS\n"
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
        sqlUpd.append("INSERT INTO tdtr_referencia(\n"
                + "NU_ANN,\n"
                + "NU_EMI,\n"
                + /*"NU_DES,\n" +*/ "CO_REF,\n"
                + "NU_ANN_REF,\n"
                + "NU_EMI_REF,\n"
                + "NU_DES_REF,\n"
                + "CO_USE_CRE,\n"
                + "FE_USE_CRE)\n"
                + //                    "VALUES(?,?,(SELECT lpad(SEC_REFERENCIA_CO_REF.NextVal, 10, '0')\n" +
                //                    "FROM DUAL),?,?,'ADM',SYSDATE)");
                "VALUES(?,?,lpad(SEC_REFERENCIA_CO_REF.NextVal, 10, '0'),?,?,?,?,SYSDATE)");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nuAnn, nuEmi, referenciaEmiDocBean.getNuAnn(), referenciaEmiDocBean.getNuEmi(),
                referenciaEmiDocBean.getNuDes(), referenciaEmiDocBean.getCoUseCre()});
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
        sqlUpd.append("update tdtr_referencia \n"
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
        sqlIns.append("DELETE FROM tdtr_referencia\n"
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
        sql.append("select "
                + " PK_SGD_DESCRIPCION.DE_SIGLA(?) de_doc_sig,\n"
                + " CO_EMPLEADO co_emp_emi,"
                + " PK_SGD_DESCRIPCION.DE_NOM_EMP(CO_EMPLEADO) de_emp_emi,\n"
                + " PK_SGD_DESCRIPCION.ESTADOS (?,'TDTV_REMITOS') de_es_doc_emi,"
                + " '0' existe_doc,"
                + " '0' existe_anexo,\n"
                + " ? co_es_doc_emi \n"
                + " from rhtm_dependencia\n"
                + " where CO_DEPENDENCIA = ?");

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
        sqlQry1.append("select lpad(rtrim(ltrim(to_char(NVL(max(NU_CORR_EXP),0) +1 ))), 7, '0') nuCorrExp, \n"
                + " substr(PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(?),1,6) deSiglaCorta\n"
                + "from tdtc_expediente\n"
                + "where nu_ann_exp = ?\n"
                + "and co_dep_exp = ?\n"
                + "and co_gru     = '1'");

        StringBuffer sqlQry2 = new StringBuffer();
        sqlQry2.append("select lpad(NVL(max(nu_sec_exp),0) +1, 10, '0') \n"
                + "from tdtc_expediente\n"
                + "where nu_ann_exp = ?");

        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO TDTC_EXPEDIENTE(\n"
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
                + "VALUES(?,?,(select to_date(?,'DD/MM/YYYY') from dual),?,?,?,'1',?,?,?,?,?,SYSDATE,?,SYSDATE,'0')");

        try {
            Map mapResult = this.jdbcTemplate.queryForMap(sqlQry1.toString(), new Object[]{expedienteBean.getCoDepExp(), expedienteBean.getNuAnnExp(), expedienteBean.getCoDepExp()});
            String snuSecExp = this.jdbcTemplate.queryForObject(sqlQry2.toString(), String.class, new Object[]{expedienteBean.getNuAnnExp()});
            expedienteBean.setNuSecExp(snuSecExp);
            String snuCorrExp = String.valueOf(mapResult.get("nuCorrExp"));
            String sdeSiglaCorta = String.valueOf(mapResult.get("deSiglaCorta"));

            expedienteBean.setNuExpediente(Utilidades.AjustaCampoIzquierda(sdeSiglaCorta, 6, "0") + expedienteBean.getNuAnnExp() + snuCorrExp);

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
        sqlQry.append("SELECT nvl(MAX(es_doc_rec),'0')\n"
                + "FROM tdtv_destinos\n"
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
        sqlQry.append("SELECT nvl(MAX(nu_cor_doc), 1) + 1\n"
                + "FROM tdtv_remitos\n"
                + "WHERE nu_ann         = ?\n"
                + "AND ti_emi         = ?\n"
                + "AND co_dep_emi     = ?\n"
                + "AND co_tip_doc_adm = ?\n"
                + "AND (  (? IS NOT NULL AND nu_doc_emi = ?)\n"
                + "OR ? IS NULL\n"
                + ")");

        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update tdtv_remitos \n"
                + "set es_doc_emi = ?,\n"
                + "nu_cor_doc = ?,fe_use_mod=SYSDATE,co_use_mod=? \n"
                + "WHERE nu_ann = ?\n"
                + "AND nu_emi = ?\n"
                + "AND es_eli = '0'");

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

        sql.append("SELECT a.co_dep co_dependencia,PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.co_dep) de_dependencia,\n" +
                    "NVL(a.co_emp, b.co_empleado) co_empleado,PK_SGD_DESCRIPCION.DE_NOM_EMP(NVL(a.co_emp, b.co_empleado)) de_empleado,\n" +
                    "c.co_loc co_local,PK_SGD_DESCRIPCION.DE_LOCAL(c.co_loc) de_local,'1' co_prioridad,\n" +
                    "? co_tramite_first, ? de_tramite_first, ? co_tramite_next, ? de_tramite_next \n" +
                    "FROM (   \n" +
                    "  select y.cemp_codemp co_emp,  x.co_dep\n" +
                    "  from tdtd_dep_gru x, rhtm_per_empleados y\n" +
                    "  where x.co_gru_des= ?\n" +
                    "  and x.es_dep_gru = '1'\n" +
                    "  and y.cemp_codemp (+) = x.co_emp \n" +
                    "  and y.cemp_est_emp(+) = '1'\n" +
                    "  order by y.rowid\n" +
                    ") a , rhtm_dependencia b, sitm_local_dependencia c\n" +
                    "WHERE a.co_dep = b.co_dependencia\n" +
                    "AND a.co_dep = c.co_dep \n"+
                    "order by a.rowid ");

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

    public String updDocumentoObj(final DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
        boolean inInsert = false;
        /*Verificamos si es Insert o Update*/
        try {
            int cant = this.jdbcTemplate.queryForInt("select count(nu_ann) from tdtv_archivo_doc where nu_ann = ? and nu_emi = ?", new Object[]{docObjBean.getNuAnn(), docObjBean.getNuEmi()});
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
            sql.append("INSERT INTO tdtv_archivo_doc (NU_ANN,NU_EMI,BL_DOC,DE_RUTA_ORIGEN,ES_FIRMA,FEULA,W_BL_DOC,W_DE_RUTA_ORIGEN)\n"
                    + "VALUES(?,?,?,?,'0',TO_CHAR(SYSDATE,'YYYYMMDD'),?,?)");
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
            
            sql1.append("UPDATE TDTV_ARCHIVO_DOC SET W_BL_DOC = BL_DOC, W_DE_RUTA_ORIGEN = DE_RUTA_ORIGEN \n"
                    + "WHERE NU_ANN = ? \n"
                    + "AND NU_EMI = ? \n"
                    + "AND SUBSTR(DE_RUTA_ORIGEN,-4) = 'docx' ");
            
            
            
            sql2.append("UPDATE TDTV_ARCHIVO_DOC SET BL_DOC = ?, DE_RUTA_ORIGEN = ?, FEULA=TO_CHAR(SYSDATE,'YYYYMMDD')\n"
                    + "WHERE NU_ANN = ? \n"
                    + "AND NU_EMI = ? ");
            try {
                
                
                this.jdbcTemplate.update(sql1.toString(), new Object[]{docObjBean.getNuAnn(), docObjBean.getNuEmi()});            
                
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
                    + "     FROM tdtv_remitos tr\n"
                    + "    WHERE tr.nu_ann = ?\n"
                    + "      AND tr.co_dep_emi = ?\n"
                    + "      AND tr.co_tip_doc_adm = ?\n"
                    + "      AND tr.nu_doc_emi = ?\n"
                    + "      AND tr.ti_emi = '06'\n"
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
                + "FROM RHTM_PER_EMPLEADOS e\n"
                + "WHERE e.CEMP_EST_EMP = '1'\n"
                + "AND (CEMP_CO_DEPEND in (select CO_DEPENDENCIA from rhTM_dependencia d where d.CO_DEPENDENCIA =? or CO_DEPEN_PADRE=?) \n"
                + "OR CO_DEPENDENCIA=? )\n"
                + "UNION\n"
                + "SELECT e.cemp_apepat,e.cemp_apemat,e.cemp_denom, e.CEMP_CODEMP\n"
                + "FROM RHTM_PER_EMPLEADOS e\n"
                + "WHERE e.CEMP_EST_EMP = '1'\n"
                + "AND cemp_codemp \n"
                + "in (\n"
                + "select co_emp from tdtx_dependencia_empleado  where co_dep=? and es_emp='0'\n"
                + "union select CO_EMPLEADO from rhtm_dependencia  where co_dependencia=?\n"
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
    public String updEstadoDocumentoEmiAdm(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update tdtv_remitos \n"
                + "set CO_USE_MOD=?\n"
                + ",ES_DOC_EMI=?\n"
                + ",FE_USE_MOD=SYSDATE\n"
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
        /*StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("update tdtv_remitos \n"
                + "set CO_USE_MOD=? \n"
                + ",FE_USE_MOD=SYSDATE \n"
                + ",FE_EMI=SYSDATE \n"
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
        
        this.spEmiDocumento = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_EMIDOCUMENTO").withProcedureName("UPD_ESTADODOCUMENTOEMITIDO")
        .withoutProcedureColumnMetaDataAccess()
        .useInParameterNames("P_COUSEMOD", "P_NUCORDOC", "P_NUDOCEMI", "P_ESDOCEMI", "P_NUANN", "P_NUEMI")
                //descargamsj(pnu_ann tdtv_remitos.nu_ann%TYPE,pnu_emi tdtv_remitos.nu_emi%TYPE,pnu_des tdtv_destinos.nu_des%TYPE,pnu_msj tdtv_destinos.nu_msj%TYPE,pfecha tdtv_destinos.fec_finmsj%TYPE,pob_msj tdtv_destinos.ob_msj%TYPE,pmo_msj_dev tdtv_destinos.mo_msj_dev%TYPE,pes_pen_msj tdtv_destinos.es_pen_msj%TYPE,pest VARCHAR2);
        .declareParameters(
                new SqlParameter("P_COUSEMOD", Types.VARCHAR),
                new SqlParameter("P_NUCORDOC", Types.VARCHAR),
                new SqlParameter("P_NUDOCEMI", Types.VARCHAR),
                new SqlParameter("P_ESDOCEMI", Types.VARCHAR) ,
                new SqlParameter("P_NUANN", Types.VARCHAR),
                new SqlParameter("P_NUEMI", Types.VARCHAR)
                        );
        SqlParameterSource in = new MapSqlParameterSource()
        .addValue("P_COUSEMOD", documentoEmiBean.getCoUseMod())
        .addValue("P_NUCORDOC", documentoEmiBean.getNuCorDoc())
        .addValue("P_NUDOCEMI", documentoEmiBean.getNuDocEmi())
        .addValue("P_ESDOCEMI", documentoEmiBean.getEsDocEmi())               
       .addValue("P_NUANN", documentoEmiBean.getNuAnn()) 
       .addValue("P_NUEMI", documentoEmiBean.getNuEmi()); 
        try {
            this.spEmiDocumento.execute(in);
            vReturn = "OK";
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }  
        
        
        return vReturn;
    }

    @Override
    public String updDocumentoEmiAdmBean(String nuAnn, String nuEmi, DocumentoEmiBean documentoEmiBean, ExpedienteBean expedienteBean, RemitenteEmiBean remitenteEmiBean, String pcoUserMod) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("update tdtv_remitos A \n"
                + "set A.CO_USE_MOD=?,");
        if (documentoEmiBean != null) {
            documentoEmiBean.setNuAnn(nuAnn);
            sqlUpd.append("A.DE_ASU=?\n"
                    + ",A.NU_DIA_ATE=?\n"
                    + ",A.CO_TIP_DOC_ADM=?\n"
                    + ",A.NU_DOC_EMI=?\n"
                    + ",A.DE_DOC_SIG=?\n"
                    + ",A.NU_COR_DOC=nvl2(?,?,?),\n"
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
                sqlUpd.append("A.NU_COR_EMI='" + documentoEmiBean.getNuCorEmi()+ "',\n");
            }
        }
        if (expedienteBean != null) {
            sqlUpd.append("A.NU_ANN_EXP='" + expedienteBean.getNuAnnExp() + "',\n");
            sqlUpd.append("A.NU_SEC_EXP='" + expedienteBean.getNuSecExp() + "',\n");
        }
        if (remitenteEmiBean != null) {
            sqlUpd.append("A.CO_LOC_EMI='" + remitenteEmiBean.getCoLocal() + "'\n"
                    + ",A.CO_DEP_EMI='" + remitenteEmiBean.getCoDependencia() + "'\n"
                    + ",A.CO_EMP_EMI='" + remitenteEmiBean.getCoEmpFirma() + "',\n");
        }
        sqlUpd.append("A.FE_USE_MOD=SYSDATE "
                + "where\n"
                + "A.NU_ANN=? and\n"
                + "A.NU_EMI=?");

        try {
            if (documentoEmiBean != null) {
                this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{pcoUserMod, documentoEmiBean.getDeAsu(), documentoEmiBean.getNuDiaAte(),
                    documentoEmiBean.getCoTipDocAdm(), documentoEmiBean.getNuDocEmi(), documentoEmiBean.getDeDocSig(),
                    documentoEmiBean.getNuDocEmi(), documentoEmiBean.getNuCorDoc(),documentoEmiBean.getNuCorDoc(),/*, nuAnn, documentoEmiBean.getCoDepEmi(), documentoEmiBean.getCoTipDocAdm(),
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
            sql.append("         SELECT trim(to_char(nvl(MAX(nu_doc_emi), 0) + 1)) \n"
                    + "           FROM tdtv_remitos tr\n"
                    + "          WHERE tr.nu_ann = ?\n"
                    + "            AND tr.co_dep_emi = ?\n"
                    + "            AND tr.co_tip_doc_adm = ?\n"
                    + "            AND tr.ti_emi = '06'\n"
                    + "            AND tr.nu_cor_doc = 1\n"
                    + "            AND tr.es_doc_emi != '9'\n"
                    + "            AND PK_SGD_DESCRIPCION.is_number(nu_doc_emi) = 1");
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
                + "UPPER(SUBSTR(de_ruta_origen,instr(de_ruta_origen, '.', -1, 1)+1)) tipo_doc\n"
                + "from tdtv_archivo_doc \n"
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
        sqlQry.append("SELECT nvl(MAX(nu_cor_doc), 0) + 1\n"
                + "FROM tdtv_remitos\n"
                + "WHERE nu_ann       = ?\n"
                + "AND ti_emi         = ?\n"
                + "AND co_dep_emi     = ?\n"
                + "AND co_tip_doc_adm = ?");
        try {
            
            System.out.println(documentoEmiBean.getNuAnn());
            System.out.println(documentoEmiBean.getTiEmi());
            System.out.println(documentoEmiBean.getCoDepEmi());
            System.out.println(documentoEmiBean.getCoTipDocAdm());
            
            
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
        sqlQry.append("SELECT nvl(MAX(nu_cor_doc), 1) + 1\n"
                + "FROM tdtv_remitos\n"
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
        sqlDel.append("DELETE FROM tdtr_referencia\n"
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
        sqlDel.append("DELETE FROM TDTV_DESTINOS\n"
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

        sqlUpd.append("update tdtv_remitos \n"
                + "set CO_USE_MOD=?\n"
                + ",ES_ELI='1',NU_DOC_EMI=?,NU_COR_DOC=?,NU_ANN_EXP=?,NU_SEC_EXP=?,NU_DET_EXP=?\n"
                + ",FE_USE_MOD=SYSDATE\n"
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
                + "  FROM RHTM_DEPENDENCIA\n"
                + " WHERE co_nivel <> '6'\n"
                + "   AND IN_BAJA = '0'");
        if (vfiltro) {
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%' )");
        }
        sql.append(" union  ");
        sql.append("SELECT DE_DEPENDENCIA,\n"
                + "       CO_DEPENDENCIA,\n"
                + "       DE_CORTA_DEPEN\n"
                + "  FROM RHTM_DEPENDENCIA\n"
                + " WHERE co_nivel = '6'\n"
                + "   AND IN_BAJA = '0'\n"
                + "   AND (CO_DEPEN_PADRE in ( select CO_DEPEN_PADRE co_dep from RHTM_DEPENDENCIA where CO_DEPENDENCIA = ?)\n"
                + "       OR ? IN (CO_DEPENDENCIA, CO_DEPEN_PADRE))");
        if (vfiltro) {
            sql.append(" AND (DE_DEPENDENCIA LIKE '%' || ? || '%' OR DE_CORTA_DEPEN LIKE ? || '%')");
        }
        sql.append("  UNION");
        sql.append(" SELECT ' [TODOS]',");
        sql.append(" 	   NULL,");
        sql.append(" 	   NULL");
        sql.append("   FROM dual");
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
        sql.append("FROM tdtv_destinos ");
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
        sql.append(" SELECT ti_des,\n" +
                    " decode(ti_des,'02',PK_SGD_DESCRIPCION.DE_PROVEEDOR(nu_ruc_des),\n" +
                    "               '03',PK_SGD_DESCRIPCION.ANI_SIMIL(nu_dni_des),\n" +
                    "               '04',PK_SGD_DESCRIPCION.OTRO_ORIGEN(co_otr_ori_des),' ' ) co_dep_des\n" +
                    " FROM tdtv_destinos \n" +
                    " where nu_ann = ?\n" +
                    " and nu_emi = ?\n" +
                    " and es_eli = '0' \n" +
                    " ORDER BY ti_des ");
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
        sql.append("FROM   tdtr_referencia a, tdtv_remitos b ");
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
                    " decode(b.ti_emi,'02',PK_SGD_DESCRIPCION.DE_PROVEEDOR(b.nu_ruc_emi),\n" +
                    "               '03',PK_SGD_DESCRIPCION.ANI_SIMIL(b.nu_dni_emi),\n" +
                    "               '04',PK_SGD_DESCRIPCION.OTRO_ORIGEN(b.co_otr_ori_emi),' ' ) co_dep_emi\n" +
                    " FROM   tdtr_referencia a, tdtv_remitos b \n" +
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
        sql.append("FROM tdtv_destinos ");
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
        sqlQry.append("FROM tdtv_destinos ");
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
        sqlUpd.append("UPDATE TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_DES = ?,CO_PRIORIDAD = ? ,nu_candes=? ");
        sqlUpd.append("WHERE NU_ANN = ? ");
        sqlUpd.append("AND NU_EMI = ? ");

        StringBuffer sqlUpd2 = new StringBuffer();
        sqlUpd2.append("UPDATE TDTV_REMITOS SET DE_ORI_DES = ?\n" +
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
        sqlUpd.append("UPDATE TDTX_REMITOS_RESUMEN SET ");
        sqlUpd.append("TI_EMI_REF = ? ");
        sqlUpd.append("WHERE NU_ANN = ? ");
        sqlUpd.append("AND NU_EMI = ?");

        StringBuffer sqlUpd2 = new StringBuffer();
        sqlUpd2.append("UPDATE TDTV_REMITOS SET DE_ORI_EMI = ?\n" +
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
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.NU_COR_DOC FROM TDTV_REMITOS A\n" +
                    "WHERE A.NU_ANN=?\n" +
                    "AND A.CO_DEP_EMI=?\n" +
                    "AND A.TI_EMI='06'\n" +
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
        
        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");        
        sql.append(" SELECT ");        
        sql.append(" DISTINCT B.NU_COR_EMI,PK_SGD_TRAMITE.DE_EMI_REF(B.NU_ANN, B.NU_EMI) DE_EMI_REF,B.FE_EMI,");
        sql.append(" TO_CHAR(B.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,");
        sql.append(" (SELECT CDOC_DESDOC FROM SI_MAE_TIPO_DOC WHERE CDOC_TIPDOC = B.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" DECODE(B.NU_CANDES, 1, PK_SGD_TRAMITE.TI_DES_EMP(B.NU_ANN, B.NU_EMI),PK_SGD_TRAMITE.TI_DES_EMP_V(B.NU_ANN, B.NU_EMI)) DE_EMP_PRO,");
        sql.append(" (SELECT CEMP_APEPAT || ' ' || CEMP_APEMAT || ' ' || CEMP_DENOM FROM RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP = B.CO_EMP_RES) DE_EMP_RES,");
        sql.append(" (SELECT DE_EST FROM TDTR_ESTADOS WHERE CO_EST || DE_TAB = B.ES_DOC_EMI || 'TDTV_REMITOS') DE_ES_DOC_EMI,");
        sql.append(" C.NU_DOC,UPPER(B.DE_ASU) DE_ASU_M,B.NU_ANN,C.NU_EXPEDIENTE,B.NU_EMI,B.TI_CAP,C.IN_EXISTE_DOC EXISTE_DOC,DECODE(NVL(C.TI_EMI_REF,'0')||NVL(C.IN_EXISTE_ANEXO,'2'),'00',0,'02',0,1) EXISTE_ANEXO, NVL(C.CO_PRIORIDAD,'1') CO_PRIORIDAD,");
        sql.append(" B.ES_DOC_EMI");
        sql.append(" FROM ( ");
        sql.append("  SELECT NU_ANN, NU_EMI ");
        sql.append("  FROM TDTR_ARBOL_SEG ");
        sql.append("	 START WITH PK_REF = :pCoAnio||:pNuEmi||'0' ");
        sql.append("	CONNECT BY PRIOR PK_EMI = PK_REF");
        sql.append(" ) A , TDTV_REMITOS B, TDTX_REMITOS_RESUMEN C");
        sql.append(" WHERE A.NU_ANN = B.NU_ANN");
        sql.append(" AND A.NU_EMI = B.NU_EMI");
        sql.append(" AND C.NU_ANN = B.NU_ANN");
        sql.append(" AND C.NU_EMI = B.NU_EMI");
        sql.append(" AND B.TI_EMI='06'");
        sql.append(" AND C.TI_EMI='06'");
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
        sql.append("WHERE ROWNUM < 101");            
        
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
        sql.append("SELECT A.NU_ANN,A.NU_EMI,A.CO_DEP_EMI,A.FE_EMI FROM TDTV_REMITOS A\n" +
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
                    "FROM TDTV_REMITOS\n" +
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
        "FROM TDTV_REMITOS\n" +
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
        sqlQry.append("SELECT nvl(MAX(nu_cor_emi), 0) + 1\n"
                + "FROM tdtv_remitos\n"
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

        sqlUpd.append("update tdtv_remitos \n"
                + "set CO_USE_MOD=?\n"
                + ",ES_DOC_EMI=?\n"
                + ",FE_USE_MOD=SYSDATE\n"
                + ",FE_EMI=SYSDATE\n"
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
       // System.out.println("llegue:"+documentoEmiBean.getTiDest());
        sqlUpd.append("UPDATE TDTV_REMITOS \n"
                + "set CO_USE_MOD=?\n"
                + ",DOC_ESTADO_MSJ=?\n"
                + ",COD_DEP_MSJ=?\n"
                + ",FE_USE_MOD=SYSDATE\n"
                + ",TI_ENV_MSJ=?\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        
        sqlUpd2.append("UPDATE TDTV_DESTINOS \n"
                + "set CO_USE_MOD=?\n"
                + ",CO_PRI=?\n"
                + ",FE_USE_MOD=SYSDATE\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getEsDocEmi(),
                documentoEmiBean.getCoDepEmi(),documentoEmiBean.getTiEnvMsj(),documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
           this.jdbcTemplate.update(sqlUpd2.toString(), new Object[]{documentoEmiBean.getCoUseMod(), documentoEmiBean.getCoPrioridad(),
                documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            
            vReturn = "OK";//+"NU_ANN:"+documentoEmiBean.getNuAnn().toString()+"\n NU_EMI:"+documentoEmiBean.getNuEmi();
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

        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");
        
    
        sql.append("select *\n"+
                    "from(\n"+
                    "select\n"+
                    "d.FE_REC_DOC,\n" +
                    "SUBSTR(PK_SGD_DESCRIPCION.de_documento(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(d.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "d.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(PK_SGD_DESCRIPCION.de_dependencia(d.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "from TDTV_REMITOS R,TDTV_DESTINOS D,TDTX_REMITOS_RESUMEN RR\n" +
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
                    "(select ?/*:b_02.co_dep_emi*/ from dual \n" +
                    "union \n" +
                    "  SELECT co_dep_ref\n" +
                    "    FROM tdtx_referencia\n" +
                    "   WHERE co_dep_emi = ?/*:b_02.co_dep_emi*/\n" +
                    "     AND ti_emi = 'D'\n" +
                    "     AND es_ref = '1')\n" +
                    "AND r.co_tip_doc_adm = ?/*:B_03.LI_TIP_DOC*/\n");
                    if(pnuDoc!=null&&pnuDoc.trim().length()>0){
                        pnuDoc=Utility.getInstancia().rellenaCerosIzquierda(pnuDoc, 6);
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
                    }
                    sql.append("UNION \n" +
                    "SELECT\n" +
                    "DE.FE_REC_DOC,\n" +
                    "SUBSTR(PK_SGD_DESCRIPCION.de_documento(r.co_tip_doc_adm),1,100) de_tip_doc_adm,\n" +
                    "SUBSTR(rr.nu_doc,1,50) NU_DOC,\n" +
                    "TO_CHAR(r.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,\n" +
                    "TO_CHAR(DE.FE_REC_DOC,'DD/MM/YY') FE_REC_DOC_CORTA,\n" +
                    "r.nu_ann,\n" +
                    "r.nu_emi,\n" +
                    "DE.nu_des,\n" +
                    "REPLACE(LTRIM(RTRIM(r.DE_ASU)),CHR(10),' ') DE_ASU,\n" +
                    "r.co_tip_doc_adm,\n" +
                    "substr(PK_SGD_DESCRIPCION.de_dependencia(DE.co_dep_des),1,200) de_dep_des,\n" +
                    "SUBSTR(rr.nu_expediente,1,20) NU_EXPEDIENTE,\n" +
                    "r.NU_ANN_EXP,\n" +
                    "r.NU_SEC_EXP\n" +
                    "FROM TDTV_DESTINOS DE,TDTV_REMITOS R,TDTX_REMITOS_RESUMEN RR\n" +
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
                        sql.append("AND (R.NU_DOC_EMI = '").append(pnuDoc).append("' OR RR.NU_EXPEDIENTE LIKE '%").append(pnuDoc).append("')\n");
                    } 
                    sql.append(") E\n"+
                    "order by 1 desc");
                    
	sql.append(") A ");
        sql.append("WHERE ROWNUM < 201");    

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoBean.class),
                    new Object[]{pannio,pcoDepen, pcoDepen, ptiDoc, pannio, ptiDoc});
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
        "FROM TDTV_REMITOS\n" +
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
        sqlUpd.append("INSERT INTO TDTV_PERSONAL_VB(NU_ANN,NU_EMI,CO_DEP,\n" +
                    "CO_EMP_VB,IN_VB,CO_USE_CRE,\n" +
                    "FE_USE_CRE,CO_USE_MOD,FE_USE_MOD)\n" +
                    "VALUES(?,?,?,\n" +
                    "?,'0',?,\n" +
                    "SYSDATE,?,SYSDATE)");

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
        sqlDel.append("DELETE FROM TDTV_PERSONAL_VB\n" +
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
        sqlQry.append("SELECT IN_NUMERACION FROM SI_MAE_TIPO_DOC\n" +
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
        sqlQry.append("SELECT IN_TIPO_FIRMA FROM SI_MAE_TIPO_DOC\n" +
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
                    "NVL(a.co_emp, b.co_empleado) co_empleado,PK_SGD_DESCRIPCION.DE_NOM_EMP(NVL(a.co_emp, b.co_empleado)) de_empleado\n" +
                    "FROM (   \n" +
                    "  select y.cemp_codemp co_emp,  x.co_dep\n" +
                    "  from tdtd_dep_gru x, rhtm_per_empleados y\n" +
                    "  where x.co_gru_des= ?\n" +
                    "  and x.es_dep_gru = '1'\n" +
                    "  and y.cemp_codemp (+) = x.co_emp \n" +
                    "  and y.cemp_est_emp(+) = '1'\n" +
                    "  order by y.rowid\n" +
                    ") a , rhtm_dependencia b, sitm_local_dependencia c\n" +
                    "WHERE a.co_dep = b.co_dependencia\n" +
                    "AND a.co_dep = c.co_dep \n"+
                    "order by a.rowid ");

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
        /*StringBuffer sqlUpd = new StringBuffer(); 

        sqlUpd.append("UPDATE TDTV_REMITOS \n"
                + "set ES_DOC_EMI=4\n"
                + ",FE_USE_MOD=SYSDATE\n"
                + "where\n"
                + "NU_ANN=? and\n"
                + "NU_EMI=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi()});
            vReturn = "OK";//+"NU_ANN:"+documentoEmiBean.getNuAnn().toString()+"\n NU_EMI:"+documentoEmiBean.getNuEmi();
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }*/
        
        
                this.spEmiDocumento = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_MENSAJERIA").withProcedureName("actualiza_estado_a")
                .withoutProcedureColumnMetaDataAccess()
                //.useInParameterNames("pnu_ann", "pnu_emi", "pde_ane", "pfec_finmsj")
                .useInParameterNames("pnu_ann", "pnu_emi", "pde_ane")
                .declareParameters(
                        new SqlParameter("pnu_ann", Types.VARCHAR),
                        new SqlParameter("pnu_emi", Types.VARCHAR),
                        new SqlParameter("pde_ane", Types.VARCHAR));
                        //new SqlParameter("pfec_finmsj", Types.VARCHAR));
        SqlParameterSource in = new MapSqlParameterSource()
                .addValue("pnu_ann", documentoEmiBean.getNuAnn())
                .addValue("pnu_emi", documentoEmiBean.getNuEmi())
                .addValue("pde_ane", documentoEmiBean.getDocObser());

        try {
            this.spEmiDocumento.execute(in);
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
        sqlQry.append("select sum(NVL(B.nu_des,'0'))\n" +                   
                    "FROM tdtv_destinos A LEFT JOIN tdtv_anexos B \n" +
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

    @Override
    public List<TipoCategoriaBean> listTipCategoria() {
            StringBuffer sql = new StringBuffer();
        sql.append("SELECT  CIDCAT, VDESCAT \n" +
            "FROM IOTDX_CATEGORIA ORDER BY VDESCAT ASC"); 
        
        List<TipoCategoriaBean> list = new ArrayList<TipoCategoriaBean>();
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoCategoriaBean.class), new Object[]{});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String insProveedorEmi(ProveedorBean proveedor) {
        String vReturn = "NO_OK";    

        this.spProveedor = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_PROVEEDOR").withProcedureName("INS_PROVEEDOR_EMI")
        .withoutProcedureColumnMetaDataAccess()
        .useInParameterNames("PCPRO_RUC", "PCPRO_RAZSOC")

        .declareParameters(
                new SqlParameter("PCPRO_RUC", Types.VARCHAR),
                new SqlParameter("PCPRO_RAZSOC", Types.VARCHAR)                
                        );
        SqlParameterSource in = new MapSqlParameterSource()
        .addValue("PCPRO_RUC", proveedor.getNuRuc())
        .addValue("PCPRO_RAZSOC", proveedor.getDescripcion());
                
               
       
        try {
            this.spProveedor.execute(in);
            vReturn = "OK";
            
        } catch (Exception ex) {
            //mensaje = "NO_OK";
            ex.printStackTrace();
        }
        
        return vReturn; 
    }

    @Override
    public List<TipoDocumentoBean> listTipoDocumentoEmi() {
         StringBuilder sql = new StringBuilder();
        sql.append("SELECT   UPPER(A.CDOC_DESDOC) DE_TIP_DOC,A.CDOC_TIPDOC CO_TIP_DOC\n" +
                    "FROM IDOSGD.SI_MAE_TIPO_DOC A,IDOSGD.IOTDTX_TIPO_DOCUMENTO B\n" +
                    "WHERE A.CDOC_INDBAJ ='0'\n" +
                    "AND A.CDOC_TIPDOC = B.CDOC_TIPDOC\n" +
                    "UNION \n" +
                    "SELECT '.:TODOS:.' DESCRI ,NULL CODIGO FROM DUAL ORDER BY 1  "); 
        
        List<TipoDocumentoBean> list = new ArrayList<TipoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<TipoDocumentoBean> listTipoDocumentoxRef() {
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT   UPPER(A.CDOC_DESDOC) DE_TIP_DOC,A.CDOC_TIPDOC CO_TIP_DOC\n" +
                    "FROM IDOSGD.SI_MAE_TIPO_DOC A,IDOSGD.IOTDTX_TIPO_DOCUMENTO B\n" +
                    "WHERE A.CDOC_INDBAJ ='0'\n" +
                    "AND A.CDOC_TIPDOC = B.CDOC_TIPDOC ORDER BY 1"); 
        
        List<TipoDocumentoBean> list = new ArrayList<TipoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<EstadoDocumentoBean> listEstadosDocumento(String nomTabla) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_EST,CO_EST ");
        sql.append("FROM TDTR_ESTADOS ");
        sql.append("WHERE DE_TAB = ? AND CO_EST IN('0','5','7','9') ");
        sql.append("UNION ");
        sql.append("SELECT '.:TODOS.:',NULL FROM DUAL ORDER BY CO_EST ");
        
        List<EstadoDocumentoBean> list = new ArrayList<EstadoDocumentoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), ParameterizedBeanPropertyRowMapper.newInstance(EstadoDocumentoBean.class),
                    new Object[]{nomTabla});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }



}
