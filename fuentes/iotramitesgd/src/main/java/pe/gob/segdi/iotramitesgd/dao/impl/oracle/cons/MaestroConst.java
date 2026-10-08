package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;


/**
 * Objeto : MaestroConst.java.
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
public final class MaestroConst {

    /**
     *
     */

    public static String GET_DEPENDENCIA = getDependencia();
    public static String GET_TIPO_DOCUMENTO_MV = getTipoDocumentoMV();
    public static String GET_PARAMETROS = getParametros();

    static String getDependencia() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" CO_DEPENDENCIA codependencia, DE_DEPENDENCIA dedependencia").
                append(" from").
                append(" IDOSGD.RHTM_DEPENDENCIA");
        return sql.toString();
    }

    static String getTipoDocumentoMV() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" A.CIDTIPDOC, B.CDOC_DESDOC, A.CDOC_TIPDOC").
                append(" from").
                append(" IDOSGD.IOTDTX_TIPO_DOCUMENTO A INNER JOIN IDOSGD.SI_MAE_TIPO_DOC B").
                append(" ON").
                append(" A.CDOC_TIPDOC = B.CDOC_TIPDOC");
        return sql.toString();
    }

    static String getParametros() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" CELE_DESELE AS COPAR, CELE_DESCOR AS DEPAR").
                append(" from").
                append(" IDOSGD.SI_ELEMENTO").
                append(" WHERE").
                append(" CTAB_CODTAB = ?");
        return sql.toString();

    }


}
