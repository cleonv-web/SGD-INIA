package pe.gob.onpe.tramitedoc.util;

/** Datos opcionales de la cabecera, sin convertir ausencias a la palabra null. */
public final class TextoDocumento {
    private TextoDocumento() { }

    public static String primero(String... valores) {
        for (String valor : valores) {
            if (valor != null && !valor.trim().isEmpty()
                    && !"null".equalsIgnoreCase(valor.trim())) return valor.trim();
        }
        return "";
    }

    public static String fecha(String lugar, String fecha, String lugarPredeterminado) {
        String localidad = primero(lugar, lugarPredeterminado);
        String dia = primero(fecha);
        if (localidad.isEmpty()) return dia;
        if (dia.isEmpty()) return localidad;
        return localidad + ", " + dia;
    }
}
