package pe.gob.onpe.mpvdoc.web.util;

import com.itextpdf.barcodes.BarcodeQRCode;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.net.URLEncoder;

/** Enlace de consulta y QR imprimible del cargo; no incluye la clave del registro. */
public final class ConsultaExpedienteQr {
    public static final String URL_DEFAULT = "https://calidad.inia.gob.pe/consulta/inicio.do";

    private ConsultaExpedienteQr() { }

    public static String enlace(String base, String expediente) {
        String url = base == null || base.trim().length() == 0 ? URL_DEFAULT : base.trim();
        if (expediente == null || !expediente.trim().matches("[0-9]{4}-[0-9]{1,20}")) {
            throw new IllegalArgumentException("Número de expediente inválido para el QR");
        }
        try {
            URI uri = new URI(url);
            if (!("https".equalsIgnoreCase(uri.getScheme()) || "http".equalsIgnoreCase(uri.getScheme()))
                    || uri.getHost() == null || uri.getUserInfo() != null || uri.getFragment() != null
                    || uri.getRawQuery() != null) {
                throw new IllegalArgumentException("Configurar una URL HTTP/HTTPS de consulta sin parámetros");
            }
            return url + "?expediente=" + URLEncoder.encode(expediente.trim(), "UTF-8");
        } catch (java.net.URISyntaxException e) {
            throw new IllegalArgumentException("URL de consulta inválida", e);
        } catch (java.io.UnsupportedEncodingException e) {
            throw new IllegalStateException(e);
        }
    }

    public static String dominio(String base) {
        return URI.create(enlace(base, "2026-1")).getHost();
    }

    public static BufferedImage imagen(String base, String expediente) {
        Image codigo = new BarcodeQRCode(enlace(base, expediente)).createAwtImage(Color.BLACK, Color.WHITE);
        int ancho = codigo.getWidth(null), alto = codigo.getHeight(null);
        // Borde blanco de cuatro módulos y escala entera, sin suavizar las celdas.
        int margen = 4, escala = 8;
        BufferedImage salida = new BufferedImage((ancho + 2 * margen) * escala,
                (alto + 2 * margen) * escala, BufferedImage.TYPE_INT_RGB);
        Graphics2D g = salida.createGraphics();
        try {
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, salida.getWidth(), salida.getHeight());
            g.drawImage(codigo, margen * escala, margen * escala, ancho * escala, alto * escala, null);
        } finally {
            g.dispose();
        }
        return salida;
    }
}
