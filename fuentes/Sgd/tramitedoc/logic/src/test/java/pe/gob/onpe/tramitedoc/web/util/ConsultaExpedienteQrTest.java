package pe.gob.onpe.tramitedoc.web.util;

import java.awt.Color;
import java.awt.image.BufferedImage;
import org.junit.Test;
import static org.junit.Assert.*;

public class ConsultaExpedienteQrTest {
    @Test public void incluyeNumeroCompletoConCeros() {
        assertEquals("https://calidad.inia.gob.pe/consulta/inicio.do?expediente=2026-0000002",
                ConsultaExpedienteQr.enlace(null, "2026-0000002"));
    }
    @Test public void permiteCambiarDominioSinCambiarCodigo() {
        assertEquals("http://localhost:8085/consulta/inicio.do?expediente=2026-0000002",
                ConsultaExpedienteQr.enlace(" http://localhost:8085/consulta/inicio.do ", " 2026-0000002 "));
    }
    @Test(expected = IllegalArgumentException.class) public void rechazaUrlNoWeb() {
        ConsultaExpedienteQr.enlace("javascript:alert(1)", "2026-1");
    }
    @Test(expected = IllegalArgumentException.class) public void rechazaExpedienteConParametros() {
        ConsultaExpedienteQr.enlace(null, "2026-2&clave=9477");
    }
    @Test(expected = IllegalArgumentException.class) public void rechazaParametrosEnConfiguracion() {
        ConsultaExpedienteQr.enlace("https://calidad.inia.gob.pe/consulta/inicio.do?clave=9477", "2026-2");
    }
    @Test public void imagenTieneBordeBlancoYCeldasNegras() {
        BufferedImage qr = ConsultaExpedienteQr.imagen(null, "2026-0000002");
        assertEquals(qr.getWidth(), qr.getHeight());
        assertTrue(qr.getWidth() >= 256);
        int negros = 0;
        for (int y = 0; y < qr.getHeight(); y++) for (int x = 0; x < qr.getWidth(); x++) {
            int color = qr.getRGB(x, y);
            assertTrue(color == Color.WHITE.getRGB() || color == Color.BLACK.getRGB());
            if (x < 32 || y < 32 || x >= qr.getWidth() - 32 || y >= qr.getHeight() - 32)
                assertEquals(Color.WHITE.getRGB(), color);
            if (color == Color.BLACK.getRGB()) negros++;
        }
        assertTrue(negros > 0);
    }
}
