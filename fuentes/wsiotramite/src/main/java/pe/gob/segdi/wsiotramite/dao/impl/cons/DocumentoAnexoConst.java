package pe.gob.segdi.wsiotramite.dao.impl.cons;

/**
 * 
 * Objeto : DocumentoAnexoConst.java.
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
public final class DocumentoAnexoConst {
	
	/** */
	
	public static String INS_DOCUMENTO_ANEXO = insDocumentoAnexo();
			
	
	
	static String insDocumentoAnexo(){
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into idosgd.iotdtd_anexo(").
		append(" siddocanx, siddocext, vnomdoc)").
		append(" VALUES(").
		append(" nextval('idosgd.sec_iotdtd_anexo'), ?, ?)");
		return sql.toString();
	}
	
	
	
	
		
}
