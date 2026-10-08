package pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons;

/**
 *
 * Objeto : PersonaConst.java.
 * Descripción : Clase para los script SQL.
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 *
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX       XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 *
 */
public final class ReporteConst {

    /** */

    public static String GET_BANDEJA_DESPACHO = getBandejaDespacho();
    public static String GET_BANDEJA_DESPACHO_TOTAL_ENVIADOS = getBandejaDespachoTotalEnviados();
    public static String GET_BANDEJA_RECEPCION = getBandejaRecepcion();
    public static String GET_BANDEJA_RECEPCION_TOTAL_RECEPCIONADOS = getBandejaRecepcionTotalRecepcionados();
    public static String GET_BANDEJA_DESPACHO_POR_FECHA = getBandejaDespachoXFecha();
    public static String GET_BANDEJA_RECEPCION_POR_FECHA = getBandejaRecepcionXFecha();
    public static String GET_PEDIENTES_POR_MES_BANDEJA_DESPACHO = getDocumentosPendientesPorMesBandejaDespacho();
    public static String GET_PEDIENTES_POR_MES_BANDEJA_RECEPCION = getDocumentosPendientesPorMesBandejaRecepcion();




    static String getBandejaDespacho(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" SUM(coalesce(( select count(1)  from IDOSGD.IOTDTC_DESPACHO  where   CFLGEST= 'P' AND TO_CHAR(DFECREG,'yyyy') = ? AND CFLGANU <> 'S' GROUP BY CFLGEST),0) ) AS CANTIDAD,").
                append(" '01' AS CFLGEST").
                append(" FROM IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" CFLGEST= 'P' AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ? AND").
                append(" CFLGANU <> 'S'").
                append(" GROUP BY CFLGEST").
                append(" UNION").
                append(" SELECT").
                append(" SUM(coalesce(( select count(1)  from IDOSGD.IOTDTC_DESPACHO  where   CFLGEST= 'P' AND TO_CHAR(DFECREG,'yyyy') = ? AND CFLGANU <> 'S' GROUP BY CFLGEST),0) ) AS CANTIDAD,").
                append(" '02' AS CFLGEST").
                append(" FROM IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" CFLGEST= 'E' AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ? AND").
                append(" CFLGANU <> 'S'").
                append(" GROUP BY CFLGEST").
                append(" UNION").
                append(" SELECT").
                append(" SUM(coalesce(( select count(1)  from IDOSGD.IOTDTC_DESPACHO  where   CFLGEST= 'P' AND TO_CHAR(DFECREG,'yyyy') = ? AND CFLGANU <> 'S' GROUP BY CFLGEST),0) ) AS CANTIDAD,").
                append(" '03' AS CFLGEST").
                append(" FROM IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" CFLGEST IN ('R','O','S') AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ? AND").
                append(" CFLGANU <> 'S'").
                append(" GROUP BY CFLGEST").
                append(" ORDER BY CFLGEST ASC");

        return sql.toString();
    }

    static String getBandejaRecepcion(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" SUM(coalesce(( select count(1)  from IDOSGD.IOTDTC_RECEPCION  where   CFLGEST= 'P' AND TO_CHAR(DFECREG,'yyyy') = ? AND CFLGANU <> 'S' GROUP BY CFLGEST),0) ) AS CANTIDAD,").
                append(" '01' AS CFLGEST").
                append(" FROM IDOSGD.IOTDTC_RECEPCION").
                append(" WHERE").
                append(" CFLGEST= 'P' AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ? AND").
                append(" CFLGANU <> 'S'").
                append(" GROUP BY CFLGEST").
                append(" UNION").
                append(" SELECT").
                append(" SUM(coalesce(( select count(1)  from IDOSGD.IOTDTC_RECEPCION  where   CFLGEST= 'P' AND TO_CHAR(DFECREG,'yyyy') = ? AND CFLGANU <> 'S' GROUP BY CFLGEST),0) ) AS CANTIDAD,").
                append(" '02' AS CFLGEST").
                append(" FROM IDOSGD.IOTDTC_RECEPCION").
                append(" WHERE").
                append(" CFLGEST IN ('R','O') AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ? AND").
                append(" CFLGANU <> 'S'").
                append(" GROUP BY CFLGEST").
                append(" ORDER BY CFLGEST ASC");

        return sql.toString();
    }

    static String getBandejaDespachoTotalEnviados(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" COUNT(CFLGEST) AS CANTIDAD, TO_CHAR(DFECREG,'MM') VMES").
                append(" FROM IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" CFLGEST IN ('E','R') AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ?").
                append(" GROUP BY TO_CHAR(DFECREG,'MM') ").
                append(" ORDER BY TO_CHAR(DFECREG,'MM') ASC");

        return sql.toString();
    }

    static String getBandejaRecepcionTotalRecepcionados(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" COUNT(CFLGEST) AS CANTIDAD, TO_CHAR(DFECREG,'MM') VMES").
                append(" FROM IDOSGD.IOTDTC_RECEPCION").
                append(" WHERE").
                append(" CFLGEST IN ('R','O') AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ?").
                append(" GROUP BY TO_CHAR(DFECREG,'MM') ").
                append(" ORDER BY TO_CHAR(DFECREG,'MM') ASC");

        return sql.toString();
    }

    static String getBandejaDespachoXFecha(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" COUNT(CFLGEST) AS CANTIDAD, CFLGEST FROM IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" DFECREG BETWEEN TO_DATE(?,'dd/mm/YY') AND TO_DATE(?,'dd/mm/YY') +1").
                append(" GROUP BY CFLGEST");
        return sql.toString();
    }

    static String getBandejaRecepcionXFecha(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" COUNT(CFLGEST) AS CANTIDAD, CFLGEST FROM IDOSGD.IOTDTC_RECEPCION").
                append(" WHERE").
                append(" DFECREG BETWEEN TO_DATE(?,'dd/mm/YY') AND TO_DATE(?,'dd/mm/YY') +1").
                append(" GROUP BY CFLGEST");
        return sql.toString();
    }

    static String getDocumentosPendientesPorMesBandejaDespacho(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" COUNT(CFLGEST) AS CANTIDAD, CFLGEST, TO_CHAR(DFECREG,'yyyy') ANIO, TO_CHAR(DFECREG,'MM') MES").
                append(" FROM IDOSGD.IOTDTC_DESPACHO").
                append(" WHERE").
                append(" CFLGEST = 'P' AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ?").
                append(" GROUP BY CFLGEST, TO_CHAR(DFECREG,'yyyy-MM'),  TO_CHAR(DFECREG,'yyyy') , TO_CHAR(DFECREG,'MM')").
                append(" ORDER BY TO_CHAR(DFECREG,'MM') ASC");
        return sql.toString();
    }

    static String getDocumentosPendientesPorMesBandejaRecepcion(){
        StringBuilder sql = new StringBuilder();
        sql.append(" SELECT").
                append(" COUNT(CFLGEST) AS CANTIDAD, CFLGEST, TO_CHAR(DFECREG,'yyyy') ANIO, TO_CHAR(DFECREG,'MM') MES").
                append(" FROM IDOSGD.IOTDTC_RECEPCION").
                append(" WHERE").
                append(" CFLGEST = 'P' AND").
                append(" TO_CHAR(DFECREG,'yyyy') = ?").
                append(" GROUP BY CFLGEST, TO_CHAR(DFECREG,'yyyy-MM'),  TO_CHAR(DFECREG,'yyyy') , TO_CHAR(DFECREG,'MM')").
                append(" ORDER BY TO_CHAR(DFECREG,'MM') ASC");
        return sql.toString();
    }



}
