package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 *
 * Objeto : LoginConst.java.
 * Descripción : Clase para los script SQL.
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 *
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX       XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 *
 */
public final class LoginConst {

    /** */

    public static String GET_DATOS_USUARIO = getDatosUsuario();
    public static String DESBLOQUEAR_USUARIO = desbloquearUsuario();
    public static String INCREMENTAR_NUMERO_INTENTOS = incrementarNumeroIntentos();
    public static String GET_DEPENDENCIA_USUARIO = getDependenciaUsuario();
    public static String GET_RESPUESTA_APLICATIVO = getRespuestaAplicativo();
    public static String GET_CONFIGURACION_USUARIO = getConfiguracionUsuario();


    static String getDatosUsuario(){
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" EMP.CEMP_NU_DNI, EMP.CEMP_APEPAT||' '||EMP.CEMP_APEMAT||' '||EMP.CEMP_DENOM NOMBRECOMPLETO,").
                append(" USU.cod_user, USU.cemp_codemp, USU.cclave de_Password, TO_DATE(TO_CHAR(USU.DFE_MOD_CLAVE,'YYYY-MM-DD'),'YYYY-MM-DD') FE_MOD_CLAVE,").
                append(" coalesce(USU.ES_USUARIO,'N') ES_ACTIVO, CURRENT_DATE FE_ACTUAL,").
                append(" USU.DFEC_MOD D_FEC_MOD, NOW() FULL_FECHA_ACTUAL, coalesce(USU.NU_INTENTO,0) + 1 NRO_INTENTO").
                append(" from").
                append(" IDOSGD.RHTM_PER_EMPLEADOS EMP INNER JOIN IDOSGD.SEG_USUARIOS1 USU").
                append(" ON EMP.CEMP_CODEMP = USU.CEMP_CODEMP").
                append(" WHERE").
                append(" cod_user = ?");
        return sql.toString();
    }

    static String desbloquearUsuario(){
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.SEG_USUARIOS1").
                append(" SET NU_INTENTO = 0,").
                append(" ES_USUARIO = 'A',").
                append(" CUSER_MOD = ?,").
                append(" DFEC_MOD = NOW()").
                append(" WHERE").
                append(" COD_USER = ?");
        return sql.toString();
    }


    static String incrementarNumeroIntentos(){
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.SEG_USUARIOS1").
                append(" SET NU_INTENTO = coalesce(NU_INTENTO,0) + 1,").
                append(" ES_USUARIO = CASE WHEN NU_INTENTO = 4 THEN 'I' ELSE ES_USUARIO END,").
                append(" CUSER_MOD = ?,").
                append(" DFEC_MOD = NOW()").
                append(" WHERE").
                append(" COD_USER = ?");
        return sql.toString();
    }

    static String getRespuestaAplicativo(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" coalesce(ES_ACTIVO,'0') es_activo").
                append(" FROM IDOSGD.SEG_USER_APLICA").
                append(" WHERE").
                append(" COD_USER = ?").
                append(" AND COD_APLICA = ?");
        return sql.toString();
    }

    static String getDependenciaUsuario(){
        StringBuilder sql = new StringBuilder();
        sql.append(" select C.CO_DEPENDENCIA codependencia, C.DE_DEPENDENCIA dedependencia").
                append(" FROM").
                append(" IDOSGD.SEG_USUARIOS1 A, IDOSGD.RHTM_PER_EMPLEADOS B, IDOSGD.RHTM_DEPENDENCIA C").
                append(" WHERE").
                append(" A.CEMP_CODEMP = B.CEMP_CODEMP").
                append(" AND B.CO_DEPENDENCIA = C.CO_DEPENDENCIA").
                append(" AND A.COD_USER = ?").
                append(" AND B.CEMP_EST_EMP ='1'");

        return sql.toString();
    }

    static String getConfiguracionUsuario(){
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" replace(a.de_dir_emi,'\\\\','|') de_dir_emi ,").
                append(" a.IN_TIPO_DOC in_tipo_doc ,").
                append(" a.CO_EMP cemp_codemp ,").
                append(" a.CO_DEP co_dep ,").
                //append(" IDOSGD.PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.CO_DEP)  de_dep,").
                //append(" IDOSGD.PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(a.CO_DEP)  de_Siglas_Dep,").
                        append(" a.CO_LOC co_local,").
                //append(" IDOSGD.PK_SGD_DESCRIPCION.DE_LOCAL(a.CO_LOC) de_local,").
                        append(" a.DE_DIR_ANE de_Ruta_Anexo,").
                append(" a.DE_PIE_PAGINA ,").
                append(" a.TI_ACCESO ,").
                append(" a.IN_CONSULTA TI_CONSULTA,").
                append(" a.IN_MAIL in_Correo,").
                append(" a.IN_CARGA_DOC in_Carga_Doc_Mesa_Partes,").
                append(" a.IN_FIRMA,").
                append(" a.IN_OBS_DOCU in_Obs_Documento,").
                append(" a.IN_REVI_DOCU in_Revi_Documento,").
                append(" b.in_numero_mp in_numero_mp,").
                append(" b.in_mesa_partes in_mesa_partes,").
                append(" (SELECT DE_PAR FROM IDOSGD.tdtr_parametros WHERE CO_PAR='ID_IMG_PORTADA_SGD') NAME_IMG_PORTADA_SGD,").
                append(" (SELECT DE_PAR FROM IDOSGD.tdtr_parametros WHERE CO_PAR='DIAS_EXPIRACION_CLAVE') DIAS_EXPIRACION_CLAVE,").
                append(" (SELECT DEP.CO_DEPENDENCIA FROM IDOSGD.RHTM_DEPENDENCIA DEP WHERE DEP.IN_MESA_PARTES='1' AND DEP.IN_BAJA='0'").
                append(" AND 0 < (SELECT COUNT(1) FROM IDOSGD.TDTR_DEPENDENCIA_MP DMP WHERE DMP.CO_DEP=B.CO_DEPENDENCIA AND DMP.ES_ELI='0' AND DMP.IN_MP='1')) CO_DEP_MP,").
                append(" a.ti_acceso_mp,").
                append(" a.in_consulta_mp ti_consulta_mp").
                append(" FROM IDOSGD.TDTX_CONFIG_EMP a , IDOSGD.rhtm_dependencia b").
                append(" where a.co_dep= ? ").
                append(" and a.co_emp=?").
                append(" and a.co_dep = b.co_dependencia");

        return sql.toString();
    }

}
