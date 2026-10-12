package pe.gob.onpe.consultadoc.service;

import java.util.*;
import org.junit.Test;
import static org.junit.Assert.*;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.dao.impl.postgresql.ConsultaDocDaoImpl;

public class ConsultaResumenDaoTest {
    static class Dao extends ConsultaDocDaoImpl {
        Dao(final List<Map<String,Object>> resumen) {
            final Map<String,Object> original = new HashMap<String,Object>();
            original.put("anio", "2026"); original.put("numero_emision", "0000000581");
            original.put("rroexpediente", "2026-0000002"); original.put("remitente", " Remitente de prueba ");
            original.put("institucion", "  "); original.put("sumilla", "Prueba");
            jdbcTemplate = new JdbcTemplate() {
                @Override public List<Map<String,Object>> queryForList(String sql, Object... args) {
                    assertEquals("2026-0000002", args[0]);
                    return Collections.singletonList(original);
                }
            };
            namedParameterJdbcTemplate = new NamedParameterJdbcTemplate(jdbcTemplate) {
                @Override public List<Map<String,Object>> queryForList(String sql, SqlParameterSource params) {
                    assertEquals("2026", params.getValue("anio"));
                    assertEquals("0000000581", params.getValue("emision"));
                    return resumen;
                }
            };
        }
    }
    Map<String,Object> registro() {
        Map<String,Object> r=new HashMap<String,Object>();
        r.put("fecha_expediente", "11/10/2026 19:10:54");r.put("oficina_registro", "MESA DE PARTES");
        r.put("estado_registro", "EN PROYECTO");r.put("tiene_derivacion", Boolean.FALSE);return r;
    }
    @Test public void sinDestinoConservaEstadoRealYOficinaDeRegistro() {
        ConsultaDocBean b=new Dao(Collections.singletonList(registro())).getBuscarExpediente("2026-0000002").get(0);
        assertEquals("EN PROYECTO",b.getEstado());assertEquals("MESA DE PARTES",b.getOficinaRegistro());
        assertEquals("",b.getOficinaAtencion());assertFalse(b.isTieneDerivacion());
        assertEquals("11/10/2026 19:10:54",b.getFechaExpediente());
        assertEquals("Remitente de prueba",b.getRemitente());assertEquals("",b.getInstitucion());
    }
    @Test public void derivacionUsaOficinaEstadoYObservacionDelDestino() {
        Map<String,Object> r=registro();r.put("oficina_atencion","ESTACIÓN EXPERIMENTAL");
        r.put("estado_destino","ARCHIVADO");r.put("obsfinalizar","Conclusión registrada");r.put("tiene_derivacion",Boolean.TRUE);
        ConsultaDocBean b=new Dao(Collections.singletonList(r)).getBuscarExpediente("2026-0000002").get(0);
        assertEquals("ARCHIVADO",b.getEstado());assertEquals("ESTACIÓN EXPERIMENTAL",b.getOficinaAtencion());
        assertEquals("Conclusión registrada",b.getObsFinalizar());assertTrue(b.isTieneDerivacion());
    }
    @Test public void registroEliminadoNoDevuelveDatosDeLaFuncionAntigua() {
        assertTrue(new Dao(Collections.<Map<String,Object>>emptyList()).getBuscarExpediente("2026-0000002").isEmpty());
    }
}
