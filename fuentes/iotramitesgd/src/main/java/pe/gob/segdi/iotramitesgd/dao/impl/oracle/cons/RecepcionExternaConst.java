package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;


/**
 * Objeto : RecepcionExternaConst.java.
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
public final class RecepcionExternaConst {

    public static String BSQ_RECEPCION_EXTERNA = bsqRecepcionExterna();
    public static String UPD_RECEPCION_EXTERNA = updRecepcionExterna();
    public static String UPD_ESTADO_RECEPCION_EXTERNA = updRecepcionExternaEnvioCargo();
    public static String GET_DOCUMENTO_PRINCIPAL = getDocumetoPrincipal();
    public static String GET_RECEPCION_EXTERNA = getRecepcionExterna();
    public static String UPD_DOCUMENTO_PRINCIPAL = actualizaDocuemntoPrincipal();

    static String bsqRecepcionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select ").
                append(" re.sidrecext, re.vnumregstd, re.vanioregstd, re.dfecregstd as dfecregstd0, ").
                append(" re.cflgest, re.vobs, re.vuniorgstd, re.vcuo, re.vuniorgrem, re.ctipdociderem, ").
                append(" re.vnumdociderem, re.cflgestobs, re.vusuregstd,  re.vrucentrem, ").
                append(" de.siddocext, de.vnomentemi, de.vuniorgdst, ccoduniorgstd, ").
                append(" TRIM(de.ccodtipdoc) ccodtipdoc, de.vnumdoc, de.dfecdoc, ").
                append(" case when de.vurldocanx is null then 'N' else vurldocanx end vurldocanx,").
                append(" de.vnomdst, de.vnomcardst, de.vasu, de.cindtup, de.snumanx, re.dfecreg, cflgenvcar ").
                append(" from ").
                append(" IDOSGD.IOTDTC_RECEPCION re, IDOSGD.IOTDTM_DOC_EXTERNO de ").
                append(" where ").
                append(" re.sidrecext = de.sidrecext and ").
                append(" re.CFLGANU <> ?");


        return sql.toString();
    }

    static String updRecepcionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_RECEPCION").
                append(" set vnumregstd = ?,").
                append(" vanioregstd = (select to_char(now(),'yyyy')),").
                append(" vuniorgstd = ?,").
                append(" ccoduniorgstd = ?,").
                append(" dfecregstd = now(),").
                append(" vusuregstd = ?,").
                append(" bcarstd = ?,").
                append(" vobs = ?,").
                append(" cflgest = ?").
                append(" WHERE").
                append(" sidrecext = ?");
        return sql.toString();
    }

    static String updRecepcionExternaEnvioCargo() {
        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTC_RECEPCION").
                append(" set").
                append(" cflgenvcar = 'S'").
                append(" WHERE").
                append(" sidrecext = ?");
        return sql.toString();
    }

    static String getDocumetoPrincipal() {
        StringBuilder sql = new StringBuilder();
        sql.append(" Select dp.bpdfdoc").
                append(" from").
                append(" IDOSGD.IOTDTC_RECEPCION re, IDOSGD.IOTDTM_DOC_EXTERNO de, IDOSGD.IOTDTD_DOC_PRINCIPAL dp").
                append(" WHERE").
                append(" re.sidrecext = de.sidrecext and").
                append(" de.siddocext = dp.siddocext and").
                append(" re.sidrecext = ?");
        return sql.toString();
    }

    static String getRecepcionExterna() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" re.sidrecext, re.vnumregstd, re.vanioregstd, re.dfecregstd as dfecregstd0, re.cflgest, re.vobs, re.vuniorgstd, re.bcarstd,").
                append(" re.vcuo, re.vuniorgrem, re.ctipdociderem, re.vnumdociderem, re.cflgestobs, re.vusuregstd,  re.vrucentrem,").
                append(" de.siddocext, de.vnomentemi, de.vuniorgdst, ccoduniorgstd, TRIM(de.ccodtipdoc) ccodtipdoc, de.vnumdoc, de.dfecdoc,").
                append(" case when de.vurldocanx is null then 'N' else vurldocanx end vurldocanx,").
                append(" de.vnomdst, de.vnomcardst, de.vasu, de.cindtup, de.snumanx, re.dfecreg,").
                append(" dp.vnomdoc, dp.bpdfdoc").
                append(" from").
                append(" IDOSGD.IOTDTC_RECEPCION re, IDOSGD.IOTDTM_DOC_EXTERNO de, IDOSGD.IOTDTD_DOC_PRINCIPAL dp").
                append(" where").
                append(" re.sidrecext = de.sidrecext and").
                append(" de.siddocext = dp.siddocext and").
                append(" re.sidrecext = ?");

        return sql.toString();
    }

    static String actualizaDocuemntoPrincipal() {

        StringBuilder sql = new StringBuilder();
        sql.append(" UPDATE IDOSGD.IOTDTD_DOC_PRINCIPAL").
                append(" set bpdfdoc = ?").
                append(" WHERE").
                append(" siddocext = ?");
        return sql.toString();
    }


}
