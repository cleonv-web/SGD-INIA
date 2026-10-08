package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 * Objeto : PersonaConst.java.
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
public final class DocumentoAnexoConst {

    /**
     *
     */

    public static String INS_DOCUMENTO_ANEXO = insDocumentoAnexo();
    public static String GET_LISTA_DOCUMENTO_ANEXO = getListaDocumentoAnexo();
    public static String GET_LISTA_DOCUMENTO_ANEXO2 = getListaDocumentoAnexo2();

    public static String GET_DOCUMENTO_ANEXO = getDocumentoAnexo();

    static String insDocumentoAnexo() {
        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.IOTDTD_ANEXO(").
                append(" siddocanx, siddocext, vnomdoc)").
                append(" VALUES(").
                append(" NU_INT_ANX.NextVal, ?, ?)");
        return sql.toString();
    }

    static String getListaDocumentoAnexo() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" anx.siddocanx, anx.vnomdoc, anx.siddocext").
                append(" from").
                append(" IDOSGD.IOTDTD_ANEXO anx").
                append(" where").
                append(" anx.siddocext = ?");
        return sql.toString();
    }


    static String getListaDocumentoAnexo2() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" ANX.NU_ANN, ANX.NU_EMI, ANX.DE_DET AS VNOMDOC, ANX.NU_ANE").
                append(" from").
                append(" IDOSGD.TDTV_REMITOS REM").
                append(" inner join IDOSGD.TDTV_ANEXOS ANX").
                append(" on REM.NU_EMI = ANX.NU_EMI AND").
                append(" REM.NU_ANN = ANX.NU_ANN").
                append(" where").
                append(" REM.NU_EMI = ? AND").
                append(" REM.NU_ANN = ? AND").
                append(" ANX.TI_PUBLIC = '1'");
        return sql.toString();
    }

    static String getDocumentoAnexo() {
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" ANX.DE_DET ,ANX.BL_DOC").
                append(" FROM").
                append(" IDOSGD.TDTV_ANEXOS ANX").
                append(" WHERE").
                append(" ANX.NU_EMI = ? AND").
                append(" ANX.NU_ANN = ? AND").
                append(" ANX.NU_ANE = ? ");
        return sql.toString();
    }


}
