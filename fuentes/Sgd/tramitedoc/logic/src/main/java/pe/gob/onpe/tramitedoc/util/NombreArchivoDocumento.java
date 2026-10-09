package pe.gob.onpe.tramitedoc.util;

/** Normaliza solo las siglas usadas en archivos; no cambia las siglas del documento. */
public final class NombreArchivoDocumento {
    private NombreArchivoDocumento() { }

    public static String siglas(String valor) {
        return valor == null ? null : valor.replace('/', '-').replace('\\', '-');
    }
}
