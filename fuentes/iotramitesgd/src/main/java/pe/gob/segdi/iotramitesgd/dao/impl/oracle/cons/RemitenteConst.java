package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 * Objeto : RemitenteConst.java.
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
public final class RemitenteConst {

    /**
     *
     */

    public static String INS_REMITENTE = insRemitente();
    public static String GET_CODIGO_REMITENTE = getCodigoRemitente();


    static String insRemitente() {
        StringBuilder sql = new StringBuilder();
        sql.append(" Insert into IDOSGD.LG_PRO_PROVEEDOR(").
                append(" CPRO_RUC, CPRO_RAZSOC, CPRO_DOMICIL, DPRO_FECINS)").
                append(" VALUES(").
                append(" ?, ?, ?, now())");
        return sql.toString();
    }

    static String getCodigoRemitente() {
        StringBuilder sql = new StringBuilder();
        sql.append(" select").
                append(" CPRO_RUC").
                append(" from").
                append(" IDOSGD.LG_PRO_PROVEEDOR").
                append(" WHERE").
                append(" CPRO_RUC = ?");
        return sql.toString();
    }


}
