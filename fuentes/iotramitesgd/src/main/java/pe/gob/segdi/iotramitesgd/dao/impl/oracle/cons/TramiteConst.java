package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 * Objeto : TramiteConst.java.
 * Descripción : Clase para los script SQL.
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX       XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
public final class TramiteConst {

    /**
     *
     */
    public static String GET_NUMERO_DE_DOCUMENTOS = getNumeroDeDocumentos();
    public static String GET_NUMERO_SECUENCIA_EXPEDIENTE = getNumeroSecuenciaExpediente();
    public static String GET_NUMERO_EXPEDIENTE = getNumeroExpediente();
    public static String GET_NUMERO_CORRELATIVO_EXPEDIENTE = getNumeroCorrelativoExpediente();
    public static String INS_EXPEDIENTE = insExpediente();
    public static String GET_CORRELATIVO_DOCUMENTO = getCorrelativoDocumento();
    public static String GET_CODIGO_EMPLEADO_DEPENDENCIA = getCodigoEmpleadoDependencia();
    public static String INS_REMITO = insRemito();
    public static String INS_ARCHIVO = insArchivo();
    public static String INS_ANEXO = insAnexo();
    public static String INS_DESTINO = insDestino();
    public static String INS_ESTADO_MOVIMIENTO = insEstadoMovimiento();
    public static String GET_TRAMITE_OFICINAS = getTramiteOficinas();
    public static String GET_CODIGO_TIPO_DOCUMENTO = getCodigoTipoDocumento();
    public static String GET_RESPONSABLE_OFICINA = getResponsableOficina();
    public static String GET_CODIGO_DEPENDENCIA = getCodigoDependencia();
    public static String GET_CODIGO_DEPENDENCIA_TRAMITE = getCodigoDependenciaTramite();
    public static String GET_CODIGO_LOCAL = getCodigoLocal();
    public static String GET_ESTADO_DOCUMENTO = getEstadoDocumento();
    public static String UPD_ESTADO_DOCUMENTO = updEstadoDocumento();
    public static String UPD_ESTADO_REMITO = updEstadoRemito();
    public static String GET_ESTADO_MESA_VIRTUAL = getEstadoMesaVirtual();
    public static String UPD_TIPO_ENVIO = updTipoEnvio();
    public static String HABILITAR_DOCUMENTO = habilitarDocumento();
    public static String OBSERVAR_DOCUMENTO = observarDocumento();

    static String getNumeroDeDocumentos() {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" (SELECT COUNT(1) FROM IDOSGD.IOTDTC_DESPACHO WHERE CFLGEST = 'P') PENDIENTES,").
                append(" (SELECT COUNT(1) FROM IDOSGD.IOTDTC_DESPACHO WHERE CFLGEST = 'E') ENVIADOS,").
                append(" (SELECT COUNT(1) FROM IDOSGD.IOTDTC_DESPACHO WHERE CFLGEST = 'R') RECEPCIONADOS,").
                append(" (SELECT COUNT(1) FROM IDOSGD.IOTDTC_DESPACHO WHERE CFLGEST = 'O') OBSERVADOS").
                //append(" FROM DUAL").
                append(" UNION").
                append(" SELECT").
                append(" (SELECT COUNT(1) FROM IDOSGD.IOTDTC_RECEPCION WHERE CFLGEST = 'P') PENDIENTES,").
                append(" (SELECT COUNT(1) FROM IDOSGD.IOTDTC_RECEPCION WHERE CFLGEST = 'E') ENVIADOS,").
                append(" (SELECT COUNT(1) FROMIDOSGD. IOTDTC_RECEPCION WHERE CFLGEST = 'E') RECEPCIONADOS,").
                append(" (SELECT COUNT(1) FROM IDOSGD.IOTDTC_RECEPCION WHERE CFLGEST = 'O') OBSERVADOS");
                //append(" FROM DUAL");
        return sql.toString();
    }

    static String getTramiteOficinas() {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" CO_DEPENDENCIA as cCodOficina, DE_DEPENDENCIA as cNomOficina").
                append(" FROM").
                append(" IDOSGD.rhtm_dependencia").
                append(" WHERE").
                append(" in_baja = '0'").
                //append(" in_mesa_partes <> 1").
                        append(" ORDER BY de_dependencia ASC");
        return sql.toString();
    }

//	static String getResponsableOficina(){
//		
//		StringBuilder sql = new StringBuilder();
//		sql.append(" SELECT").
//		append(" emp.CEMP_CODEMP as iCodTrabajador, emp.CEMP_APEPAT||' '||emp.CEMP_APEMAT||' '||emp.CEMP_DENOM as cNombresTrabajador").
//		append(" FROM").
//		append(" IDOSGD.RHTM_PER_EMPLEADOS emp inner join IDOSGD.rhtm_dependencia dep").
//		append(" on").
//		append(" emp.CEMP_CO_DEPEND = dep.CO_DEPENDENCIA").
//		append(" WHERE").
//		append(" dep.CO_DEPENDENCIA = ? AND").
//		append(" emp.CEMP_EST_EMP = '1'");
//		return sql.toString();
//	}

    static String getResponsableOficina() {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" emp.CEMP_CODEMP||'-'||emp.CEMP_APEPAT||' '||emp.CEMP_APEMAT||' '||emp.CEMP_DENOM as cNombresTrabajador").
                append(" FROM").
                append(" IDOSGD.RHTM_PER_EMPLEADOS emp").
                append(" WHERE").
                append(" emp.CEMP_CODEMP = (select co_empleado  from IDOSGD.rhtm_dependencia where CO_DEPENDENCIA = ?)");
        return sql.toString();
    }

    static String getNumeroExpediente() {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                //append(" (rpad(substr(PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(?),1,6),6,'0')||to_char(sysdate,'yyyy'))||lpad(rtrim(ltrim(to_char(NVL(max(NU_CORR_EXP),0) +1 ))), 7, '0') nu_expediente").
                        append(" (to_char(now(),'yyyy'))||'-'||lpad(rtrim(ltrim(to_char(NVL(max(NU_CORR_EXP),0) +1 ))), 7, '0') nu_expediente").
                append(" FROM").
                append(" IDOSGD.tdtc_expediente").
                append(" WHERE").
                append(" nu_ann_exp = (select to_char(now(),'yyyy'))").
                append(" and co_dep_exp = ?").
                append(" and co_gru     = '3'");
        return sql.toString();
    }

    static String getNumeroCorrelativoExpediente() {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" (to_char(now(),'yyyy'))||'-'||lpad(rtrim(ltrim(to_char(NVL(max(NU_CORR_EXP),0) +1 ))), 7, '0') nu_expediente").
                append(" FROM").
                append(" IDOSGD.tdtc_expediente").
                append(" WHERE").
                append(" nu_ann_exp = (select to_char(now(),'yyyy'))").
                append(" and co_dep_exp = ?").
                append(" and co_gru     = '3'");
        return sql.toString();
    }

    static String getNumeroSecuenciaExpediente() {

        StringBuilder sql = new StringBuilder();
        sql.append(" select lpad(NVL(max(nu_sec_exp),0) +1, 10, '0')").
                append(" FROM").
                append(" IDOSGD.tdtc_expediente").
                append(" WHERE").
                append(" nu_ann_exp = (select to_char(now(),'yyyy'))");
        return sql.toString();
    }

    static String insExpediente() {

        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.TDTC_EXPEDIENTE(").
                append(" nu_ann_exp, nu_sec_exp, fe_exp,").
                append(" co_proceso, co_dep_exp, co_gru,").
                append(" nu_corr_exp, nu_expediente, us_crea_audi, fe_crea_audi,").
                append(" us_modi_audi, fe_modi_audi, es_estado)").
                append(" VALUES(").
                append(" (select to_char(now(),'yyyy') ), ?, (select to_date(now(),'dd/mm/yyyy')),'0000', ?, '3', ?, ?, ?, now(), ?,now(),'0')");

        return sql.toString();
    }

    static String getCorrelativoDocumento() {

        StringBuilder sql = new StringBuilder();
        sql.append(" select NVL(max(nu_cor_doc),0)+1").
                append(" FROM").
                append(" IDOSGD.tdtv_remitos").
                append(" WHERE").
                append(" nu_ann = (select to_char(now(),'yyyy') )").
                append(" and co_dep_emi = ?").
                append(" and TI_EMI = ?");
        return sql.toString();
    }

    static String getCodigoEmpleadoDependencia() {

        StringBuilder sql = new StringBuilder();
        sql.append(" select CO_EMPLEADO co_emp_emi").
                append(" FROM").
                append(" IDOSGD.rhtm_dependencia").
                append(" WHERE").
                append(" CO_DEPENDENCIA = ?");

        return sql.toString();
    }

    static String insRemito() {

        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.TDTV_REMITOS (").
                append(" NU_ANN, NU_EMI, NU_COR_EMI, CO_LOC_EMI,").
                append(" CO_DEP_EMI, TI_EMI, CO_EMP_EMI, NU_RUC_EMI,").
                append(" FE_EMI, CO_GRU, DE_ASU, ES_DOC_EMI,").
                append(" CO_USE_CRE, FE_USE_CRE, CO_USE_MOD, FE_USE_MOD, CO_TIP_DOC_ADM,").
                append(" CO_EMP_RES, NU_CANDES, NU_COR_DOC, DE_DOC_SIG,").
                append(" NU_ANN_EXP, NU_SEC_EXP, NU_DET_EXP, NU_FOLIOS,").
                append(" DE_ORI_EMI, IN_BUSCA_TEXTO, CO_DEP, NU_DIA_ATE,").
                append(" ES_ELI, CCOD_ORIGING)").
                append(" VALUES(").
                append(" (select to_char(now(),'yyyy') ), LPAD(IDOSGD.SEC_REMITOS_NU_EMI.NextVal,10,'0'), ?, ?,").
                append(" ?, ?, ?, ?,").
                append(" now(), ?, ?, ?,").
                append(" ?, now(), ?, now(), ?,").
                append(" ?, ?, ?, ?,").
                append(" (select to_char(now(),'yyyy') ), ?, ?, ?,").
                append(" ?, ?, ?, 0,").
                append(" 0, ?)");
        return sql.toString();
    }

    static String insArchivo() {

        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.TDTV_ARCHIVO_DOC(").
                append(" NU_ANN, NU_EMI, BL_DOC, DE_RUTA_ORIGEN,").
                append(" ES_FIRMA, FEULA)").
                append(" VALUES(").
                append(" (select to_char(now(),'yyyy') ), ?, ?, ?,").
                append(" 0, (select to_char(now(),'yyyymmdd') ))");

        return sql.toString();
    }

    static String insAnexo() {

        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.TDTV_ANEXOS(").
                append(" NU_ANN, NU_EMI, NU_ANE, DE_DET, ").
                append(" BL_DOC, DE_RUT_ORI, CO_USE_CRE, FE_USE_CRE,").
                append(" CO_USE_MOD, FE_USE_MOD, FEULA)").
                append(" VALUES(").
                append(" (select to_char(now(),'yyyy') ), ?, ?, ?,").
                append(" ?, ?, ?, now(),").
                append(" ?, now(), (select to_char(now(),'yyyymmdd') ))");

        return sql.toString();
    }

    static String insDestino() {

        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.TDTV_DESTINOS(").
                append(" NU_ANN, NU_EMI, NU_DES, TI_DES, ").
                append(" CO_DEP_DES, CO_EMP_DES, ES_ELI, FE_USE_CRE, FE_USE_MOD,").
                append(" CO_USE_MOD, CO_USE_CRE, ES_DOC_REC, ES_ENV_POR_TRA, ").
                append(" CO_LOC_DES, CO_MOT)").
                append(" VALUES(").
                append(" (select to_char(now(),'yyyy') ), ?, ?, ?,").
                append(" ?, ?, ?, now(), now(),").
                append(" ?, ?, ?, ?,").
                append(" (Select co_loc from IDOSGD.SITM_LOCAL_DEPENDENCIA where co_dep = ?), ?)");

        return sql.toString();
    }

    static String insEstadoMovimiento() {

        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.TDTV_ESTADOS_MOV(").
                append(" NU_ANN, NU_EMI, FE_DML, TI_PROCESO,").
                append(" ES_PROCESO, DE_USER, DE_NAMEPC)").
                append(" VALUES(").
                append(" (select to_char(now(),'yyyy') ), ?, now(), ?,").
                append(" ?, ?, ?)");

        return sql.toString();
    }

    static String getCodigoDependencia() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select C.CO_DEPENDENCIA").
                append(" FROM").
                append(" IDOSGD.SEG_USUARIOS1 A, IDOSGD.RHTM_PER_EMPLEADOS B, IDOSGD.RHTM_DEPENDENCIA C").
                append(" WHERE").
                append(" A.CEMP_CODEMP = B.CEMP_CODEMP").
                append(" AND B.CO_DEPENDENCIA = C.CO_DEPENDENCIA").
                append(" AND A.COD_USER = ?").
                append(" AND B.CEMP_EST_EMP ='1'");

        return sql.toString();
    }

    static String getCodigoDependenciaTramite() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select CO_DEPENDENCIA").
                append(" FROM").
                append(" IDOSGD.RHTM_DEPENDENCIA").
                append(" WHERE").
                append(" IN_MESA_PARTES = 1");

        return sql.toString();
    }

    static String getCodigoLocal() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" cCodTipDoc").
                append(" FROM").
                append(" IDOSGD.iotx_tipoDocumento").
                append(" WHERE").
                append(" cIOCodtipdoc = ?");
        return sql.toString();
    }

    static String getCodigoTipoDocumento() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT CDOC_TIPDOC").
                append(" FROM").
                append(" IDOSGD.IOTDTX_TIPO_DOCUMENTO").
                append(" WHERE").
                append(" CIDTIPDOC = ?");
        return sql.toString();
    }

    static String getEstadoDocumento() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" CASE").
                append(" WHEN DOC_ESTADO_MSJ IS NULL THEN").
                append(" '0'").
                append(" ELSE").
                append(" DOC_ESTADO_MSJ").
                append(" END").
                append(" FROM").
                append(" IDOSGD.TDTV_REMITOS").
                append(" WHERE").
                append(" NU_EMI = ? AND").
                append(" NU_ANN = ?");
        return sql.toString();
    }

    

    static String updEstadoRemito() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.TDTV_REMITOS").
                append(" SET ES_DOC_EMI = ?, TI_ENV_MSJ = NULL, DOC_ESTADO_MSJ = NULL").
                append(" WHERE").
                append(" NU_EMI = ? AND").
                append(" NU_ANN = ?");
        return sql.toString();
    }
    
    static String updEstadoDocumento() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.TDTV_REMITOS").
                append(" SET DOC_ESTADO_MSJ = ?").
                append(" WHERE").
                append(" NU_EMI = ? AND").
                append(" NU_ANN = ?");
        return sql.toString();
    }
    
    static String updTipoEnvio() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.TDTV_REMITOS").
                append(" SET TI_ENV_MSJ = NULL").
                append(" WHERE").
                append(" NU_EMI = ? AND").
                append(" NU_ANN = ?");
        return sql.toString();
    }

    static String getEstadoMesaVirtual() {

        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" CO_PAR as copar, DE_PAR as depar").
                append(" FROM").
                append(" IDOSGD.TDTR_PARAMETROS").
                append(" WHERE").
                append(" CO_PAR IN ('WSSERVICELOCATION','URLAPLICATION','ESTADO_MESA_VIRTUAL')");
        return sql.toString();
    }

    

    static String habilitarDocumento() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.TDTV_REMITOS").
                append(" SET ES_DOC_EMI = ?,").
                append(" TI_ENV_MSJ = NULL,").
                append(" DOC_ESTADO_MSJ = ?").
                append(" WHERE").
                append(" NU_EMI = ? AND").
                append(" NU_ANN = ? ");
        return sql.toString();
    }

    static String observarDocumento() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.TDTV_DESTINOS").
                append(" SET OB_MSJ = ?").
                append(" WHERE").
                append(" NU_EMI = ? AND").
                append(" NU_ANN = ? AND").
                append(" NU_RUC_DES = ?");
        return sql.toString();
    }

}
