package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 * Objeto : DocumentoPrincipalConst.java.
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
public final class DocumentoPrincipalConst {

    /**
     *
     */
    //public static String REPORTE_DNI = "{ call SP_REPORTECONSULTADNI ('1', '2016', '', '', '') }";

    public static String INS_DOCUMENTO_PRINCIPAL = insDocumentoPrincipal();
    public static String GET_DOCUMENTO_PRINCIPAL = getDocumentoPrincipal();


    static String insDocumentoPrincipal() {
        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.IOTDTD_DOC_PRINCIPAL(").
                append(" SIDDOCPRI, siddocext, vnomdoc, bpdfdoc)").
                append(" VALUES(").
                append(" IDOSGD.NU_INT_DOC_PRI.NextVal, ?, ?, ?)");
        return sql.toString();
    }

    static String getDocumentoPrincipal() {
        StringBuilder sql = new StringBuilder();
        sql.append(" Select vnomdoc as vnomdocpri, bpdfdoc as bpdfdocpri").
                append(" from").
                append(" IDOSGD.IOTDTC_RECEPCION ree inner join IDOSGD.IOTDTM_DOC_EXTERNO doe").
                append(" on ree.sidrecext = doe.sidrecext").
                append(" inner join IDOSGD.IOTDTD_DOC_PRINCIPAL dop").
                append(" on doe.siddocext = dop.siddocext").
                append(" WHERE").
                append(" ree.vcuo = ?");
        return sql.toString();
    }


}
