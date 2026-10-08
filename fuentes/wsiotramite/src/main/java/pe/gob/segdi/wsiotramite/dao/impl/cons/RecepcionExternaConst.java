package pe.gob.segdi.wsiotramite.dao.impl.cons;

/**
 * 
 * Objeto : PersonaConst.java.
 * Descripción : Clase de para los script SQL.
 * Fecha de Creación : 24/03/2015.
 * Autor : Arturo Delgado.
 * 
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX       XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 * 
 */
public final class RecepcionExternaConst {
	
	
	public static String INS_RECPCION_EXTERNA = insRecepcionExterna();
	public static String CONSULTAR_REGISTRO_TRAMITE = consultarRegistroTramite();
	
	
	static String insRecepcionExterna(){
		StringBuilder sql = new StringBuilder();
		sql.append(" insert into idosgd.iotdtc_recepcion(").
		append(" sidrecext, vrucentrem, vuniorgrem, ctipdociderem, vnumdociderem, vcuo, vcuoref)").
		append(" VALUES(").
		append(" nextval('idosgd.sec_iotdtc_recepcion'), ?, ?, ?, ?, ?, ? )").
		append(" returning sidrecext");
		return sql.toString();
	}
	
	static String consultarRegistroTramite(){
		StringBuilder sql = new StringBuilder();
		sql.append(" select").
		append(" vnumregstd, vanioregstd, dfecregstd, vuniorgstd, vusuregstd, bcarstd, vobs, cflgest").
		append(" from idosgd.iotdtc_recepcion").
		append(" where").
		append(" vcuo = ? ");
		return sql.toString();
	}
	
	
	
	
	
	
		
}
