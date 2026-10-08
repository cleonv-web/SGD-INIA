package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 * Objeto : EmisionExternaConst.java.
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
public final class EmisionExternaConst {

    public static String EST_ENVIO = "E";
    public static String INS_EMISION_EXTERNA = insEmisionExterna();
    public static String BSQ_EMISION_EXTERNA = bsqEmisionExterna();
    public static String GET_EMISION_EXTERNA = getEmisionExterna();
    public static String INS_CUO_EMISION_EXTERNA = insCuoEmisionExterna();
    public static String UPD_ESTADO_EMISION_EXTERNA = updEstadoEmisionExterna();
    public static String UPD_CARGO_EMISION_EXTERNA = updCargoEmisionExterna();
    public static String UPD_ESTADO_ENVIO = updEstadoEnvio();
    public static String OBT_ESTADO_ENVIO = obtEstadoEnvio();
    public static String OBT_CUO = obtCuo();
    public static String ANULAR_RECEPCION = anularRecepcion();
    public static String GET_EMISION_EXTERNA_PENDIENTES = getEmisionExternaPendientes();
    public static String GET_DOCUMENTOS_ENVIADOS = getDocumentosEnviados();
    public static String ACTUALIZAR_CARGO_DESPACHO = actualizarCargoDespacho();
    public static String ACTUALIZAR_ESTADOO_DESPACHO = actualizarEstadoDespacho();
    public static String GET_NUMERO_DOCUMENTO = getNumeroDocumento();
    public static String GET_ENTIDAD_ENVIADOS = getEntidadEnviados();
    public static String UPD_CARGO = actualizarCargo();


    static String insEmisionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.IOTDTC_DESPACHO(").
                append("sidemiext, vnumregstd, vanioregstd, vrucentrec, vnomentrec, ctipdociderem, vnumdociderem, vcoduniorgrem, vuniorgrem, vusureg, vcuo)").
                append(" VALUES(").
                append(" IDOSGD.NU_INT_DES_EXT.NextVal, ?, (select to_char(now(),'yyyy') ), ?, ?, ?, ?, ?, ?, ?, ?)");
        return sql.toString();
    }

    static String bsqEmisionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" ee.vnumregstd, ee.vanioregstd,").
                append(" ee.sidemiext, de.siddocext, ee.vrucentrec, ee.vnomentrec, ee.vuniorgrem, ee.ctipdociderem, ee.vnumdociderem, ee.dfecreg,").
                append(" ee.vcuo, ee.vcuoref, ee.vnumregstdrec, ee.vanioregstdrec, ee.dfecregstdrec, ee.vuniorgstdrec,ee.vusuregstdrec , ee.vobs, ee.cflgest, ee.cflgenv,").
                append(" TRIM(de.ccodtipdoc) ccodtipdoc, de.vnumdoc, de.vuniorgdst, de.vnomdst, de.vnomcardst, de.vasu, de.snumanx, de.snumfol, de.dfecdoc dfecdoc0, de.vurldocanx").
                append(" from").
                append(" IDOSGD.IOTDTC_DESPACHO ee, IDOSGD.IOTDTM_DOC_EXTERNO de").
                append(" where").
                append(" ee.sidemiext = de.sidemiext and").
                append(" ee.cflganu = 'N' ");

        return sql.toString();
    }

    static String getEmisionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" ee.vnumregstd, ee.vanioregstd,").
                append(" ee.sidemiext, de.siddocext, ee.vrucentrec, ee.vnomentrec, ee.vuniorgrem, ee.ctipdociderem, ee.vnumdociderem, ee.dfecreg,").
                append(" ee.vcuo, ee.vcuoref, ee.vnumregstdrec, ee.vanioregstdrec, ee.dfecregstdrec, ee.vuniorgstdrec,ee.vusuregstdrec , ee.bcarstdrec, ee.vobs, ee.cflgest, ee.cflgenv,").
                append(" TRIM(de.ccodtipdoc) ccodtipdoc, de.vnumdoc, de.vuniorgdst, de.vnomdst, de.vnomcardst, de.vasu, de.snumanx, de.snumfol, de.dfecdoc dfecdoc0, de.vurldocanx,").
                append(" dp.vnomdoc, dp.bpdfdoc").
                append(" from").
                append(" IDOSGD.IOTDTC_DESPACHO ee, IDOSGD.IOTDTM_DOC_EXTERNO de, IDOSGD.IOTDTD_DOC_PRINCIPAL dp").
                append(" where").
                append(" ee.sidemiext = de.sidemiext and").
                append(" de.siddocext = dp.siddocext and").
                append(" ee.sidemiext = ?");

        return sql.toString();
    }

    static String insCuoEmisionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" set vcuo = ?").
                append(" WHERE").
                append(" sidemiext = ?");
        return sql.toString();
    }

    static String updEstadoEmisionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" set cflgest = ?,").
                append(" dfecenv = now()").
                append(" WHERE").
                append(" sidemiext = ?");
        return sql.toString();
    }

    static String updCargoEmisionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" set vnumregstdrec = ?,").
                append(" vanioregstdrec = ?,").
                append(" dfecregstdrec = ?,").
                append(" vuniorgstdrec = ?,").
                append(" vusuregstdrec = ?,").
                append(" bcarstdrec = ?,").
                append(" cflgest = ?,").
                append(" vobs = ?").
                append(" WHERE").
                append(" vcuo = ?");
        return sql.toString();
    }

    static String updEstadoEnvio() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" SET CFLGENV = ?,").
                append(" dfecenv = now()").
                append(" WHERE").
                append(" sidemiext = ?");
        return sql.toString();
    }

    static String obtEstadoEnvio() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" CFLGENV").
                append(" FROM").
                append(" IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" sidemiext = ?");
        return sql.toString();
    }

    static String obtCuo() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" VCUO").
                append(" FROM").
                append(" IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" sidemiext = ?");
        return sql.toString();
    }

    static String anularRecepcion() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" set cflganu = ?,").
                append(" vdesanu = ?").
                append(" WHERE").
                append(" sidemiext = ?");
        return sql.toString();
    }

    static String getEmisionExternaPendientes() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" ee.vnumregstd, ee.vanioregstd,").
                append(" ee.sidemiext, de.siddocext, ee.vrucentrec, ee.vnomentrec, ee.vuniorgrem, ee.ctipdociderem, ee.vnumdociderem, ee.dfecreg,").
                append(" ee.vcuo, ee.vcuoref, ee.vnumregstdrec, ee.vanioregstdrec, ee.dfecregstdrec, ee.vuniorgstdrec,ee.vusuregstdrec , ee.bcarstdrec, ee.vobs, ee.cflgest, ee.cflgenv,").
                append(" de.ccodtipdoc, de.vnumdoc, de.vuniorgdst, de.vnomdst, de.vnomcardst, de.vasu, de.snumanx, de.snumfol, de.dfecdoc dfecdoc0, de.vurldocanx,").
                append(" dp.vnomdoc, dp.bpdfdoc").
                append(" from").
                append(" IDOSGD.IOTDTC_DESPACHO ee, IDOSGD.IOTDTM_DOC_EXTERNO de, IDOSGD.IOTDTD_DOC_PRINCIPAL dp").
                append(" where").
                append(" ee.sidemiext = de.sidemiext and").
                append(" de.siddocext = dp.siddocext and").
                append(" ee.cflgestu = 'P' and").
                append(" ee.cflganu = 'N'");


        return sql.toString();
    }

    /////////////////////////////////////////////////////////////


    static String actualizarCargoDespacho() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" set vnumregstdrec = ?,").
                append(" vanioregstdrec = ?,").
                append(" dfecregstdrec = ?,").
                append(" vuniorgstdrec = ?,").
                append(" vusuregstdrec = ?,").
                append(" bcarstdrec = ?,").
                append(" cflgest = ?,").
                append(" vobs = ?").
                append(" WHERE").
                append(" vcuo = ?");
        return sql.toString();
    }

    static String actualizarEstadoDespacho() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" set cflgest = ?").
                append(" WHERE").
                append(" vcuo = ?");
        return sql.toString();
    }

    static String getDocumentosEnviados() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" vrucentrec, vcuo,  vnomentrec, vnumregstd, vanioregstd").
                append(" from").
                append(" IDOSGD.IOTDTC_DESPACHO").
                append(" where").
                append(" vrucentrec = ? AND").
                append(" cflgest = 'E' AND").
                append(" CFLGANU = 'N'");
        return sql.toString();
    }

    static String getNumeroDocumento() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" 'D'||REM.NU_DOC_EMI||'-'||REM.NU_ANN||'-'||(SELECT DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR = 'DE_INSTITUCION')||'-'||REM.DE_DOC_SIG AS VNUMCOREMI").
                append(" FROM").
                append(" IDOSGD.IOTDTC_DESPACHO DES INNER JOIN IDOSGD.TDTV_REMITOS REM").
                append(" ON DES.VNUMREGSTD = REM.NU_EMI AND DES.VANIOREGSTD = REM.NU_ANN").
                append(" INNER JOIN IDOSGD.RHTM_PER_EMPLEADOS EMP").
                append(" ON REM.CO_EMP_EMI = EMP.CEMP_CODEMP").
                append(" INNER JOIN IDOSGD.RHTM_DEPENDENCIA DEP").
                append(" ON DEP.CO_DEPENDENCIA=REM.CO_DEP_EMI").
                append(" WHERE").
                append(" DES.VCUO = ?");
        return sql.toString();
    }

    static String getEntidadEnviados() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" VRUCENTREC, VRUCENTREC|| ' - ' ||VNOMENTREC||' ('||COUNT(1)||')'  AS VNOMENTREC").
                append(" FROM").
                append(" IDOSGD.iotdtc_despacho").
                append(" WHERE").
                append(" CFLGEST = 'E' AND").
                append(" CFLGANU = 'N'").
                append(" GROUP BY VRUCENTREC,VNOMENTREC");
        return sql.toString();
    }

    static String actualizarCargo() {

        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_DESPACHO").
                append(" set bcarstdrec = ?, dfecmod=now()").
                append(" WHERE").
                append(" sidemiext = ?");
        return sql.toString();
    }
}
