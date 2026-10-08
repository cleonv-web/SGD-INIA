package pe.gob.segdi.wsiotramite.dao.impl.cons;

/**
 * 
 * Objeto : DocumentoPrincipalConst.java.
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
public final class DocumentoPrincipalConst {
	
	/** */
	//public static String REPORTE_DNI = "{ call SP_REPORTECONSULTADNI ('1', '2016', '', '', '') }";
	
	public static String INS_DOCUMENTO_PRINCIPAL = insDocumentoPrincipal();
			
	
	
	static String insDocumentoPrincipal(){
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into idosgd.iotdtd_doc_principal(").
		append(" siddocpri, siddocext, vnomdoc, bpdfdoc)").
		append(" VALUES(").
		append(" nextval('idosgd.sec_iotdtd_doc_principal'), ?, ?, ?)");
		return sql.toString();
	}
			
		
		
	
	
	
		
}
