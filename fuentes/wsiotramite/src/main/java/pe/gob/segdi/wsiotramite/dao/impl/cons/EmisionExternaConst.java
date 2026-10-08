package pe.gob.segdi.wsiotramite.dao.impl.cons;

/**
 * 
 * Objeto : EmisionExternaConst.java.
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
public final class EmisionExternaConst {
	
	
	public static String UPD_CARGO_EMISION_EXTERNA = updCargoEmisionExterna();
	public static String OBT_ESTADO_DESPACHO = obtEstadoDespacho();
	
	
	static String updCargoEmisionExterna(){
		StringBuilder sql = new StringBuilder();
		sql.append(" update idosgd.iotdtc_despacho").
		append(" set vnumregstdrec = ?,").
		append(" vanioregstdrec = ?,").
		append(" dfecregstdrec = ?,").
		append(" vuniorgstdrec = ?,").
		append(" vusuregstdrec = ?,").
		append(" bcarstdrec = ?,").
		append(" cflgest = ?,").
		append(" vobs = ?,").
		append(" vdesanxstdrec = ?").
		append(" WHERE").
		append(" vcuo = ?");
		return sql.toString();
	}
	
	static String obtEstadoDespacho(){
		StringBuilder sql = new StringBuilder();
		sql.append(" SELECT").
		append(" CFLGEST").
		append(" FROM").
		append(" idosgd.iotdtc_despacho").
		append(" where").
		append(" vcuo = ?");
		return sql.toString();
	}
	
		
}
