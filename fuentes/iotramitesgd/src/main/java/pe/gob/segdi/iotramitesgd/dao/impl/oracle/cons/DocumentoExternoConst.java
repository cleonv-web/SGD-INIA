package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 * Objeto : DocumentoExternoConst.java.
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
public final class DocumentoExternoConst {

    /**
     *
     */
    //public static String REPORTE_DNI = "{ call SP_REPORTECONSULTADNI ('1', '2016', '', '', '') }";

    public static String INS_DOCUMENTO_EXTERNO = insDocumentoExterno();


    static String insDocumentoExterno() {
        StringBuilder sql = new StringBuilder();
        sql.append(" INSERT INTO IDOSGD.IOTDTM_DOC_EXTERNO(").
                append(" siddocext, sidemiext, vnomentemi, ccodtipdoc, vnumdoc,").
                append(" dfecdoc, vuniorgdst, vnomdst, vnomcardst,").
                append(" vasu, snumanx, snumfol, vurldocanx)").
                append(" VALUES(").
                append(" IDOSGD.NU_INT_DOC_EXT.NextVal, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");
        return sql.toString();
    }


}
