package pe.gob.segdi.wsiotramite.dao.impl.cons;

/**
 * 
 * Objeto : DocumentoExternoConst.java.
 * Descripción : Clase de para los script SQL.
 * Fecha de Creación : 28/03/2018.
 * Autor : Arturo Delgado.
 * 
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX       XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 * 
 */
public final class DocumentoExternoConst {
	
	/** */
	//public static String REPORTE_DNI = "{ call SP_REPORTECONSULTADNI ('1', '2016', '', '', '') }";
	
	public static String INS_DOCUMENTO_EXTERNO = insDocumentoExterno();
	
	static String insDocumentoExterno(){
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into idosgd.iotdtm_doc_externo(").
		append(" siddocext, sidrecext, ").
		append(" vnomentemi, ccodtipdoc, vnumdoc,").
		append(" dfecdoc, vuniorgdst, vnomdst, vnomcardst,").
		append(" vasu, cindtup, snumanx, snumfol, vurldocanx)").
		append(" VALUES(").
		append(" nextval('idosgd.sec_iotdtm_doc_externo'), ?, ?, ?, ?, ?, ?, ?,?, ?, ?, ?, ?, ?)").
		append(" returning siddocext");
			
		
		return sql.toString();
	}
			
		
		
	
	
	
		
}
