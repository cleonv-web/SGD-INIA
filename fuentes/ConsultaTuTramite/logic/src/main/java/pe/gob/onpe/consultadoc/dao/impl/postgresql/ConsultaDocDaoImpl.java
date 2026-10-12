/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.consultadoc.dao.impl.postgresql;

import java.sql.Types;
import java.io.InputStreamReader;
import java.io.IOException;
import org.springframework.core.io.ClassPathResource;
import org.springframework.util.FileCopyUtils;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository; 
import pe.gob.onpe.consultadoc.bean.TipoDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocSeguimientoBean;
import pe.gob.onpe.consultadoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.consultadoc.dao.ConsultaDocDao;

/**
 *
 * @author  
 */
@Repository("consultaDocDao")
@Profile("postgresql")
public class ConsultaDocDaoImpl extends SimpleJdbcDaoBase implements ConsultaDocDao{

    private static final String RESUMEN_SQL = cargarResumenSql();

    private static String cargarResumenSql() {
        try {
            return FileCopyUtils.copyToString(new InputStreamReader(
                    new ClassPathResource("sql/consulta-resumen.sql").getInputStream(), "UTF-8"));
        } catch (IOException ex) {
            throw new IllegalStateException("No se pudo cargar la consulta de seguimiento", ex);
        }
    }

    private static String texto(Map<String,Object> fila, String campo) {
        Object valor = fila.get(campo);
        return valor == null ? "" : valor.toString().trim();
    }

    private boolean completarResumen(ConsultaDocBean consulta) {
        MapSqlParameterSource parametros = new MapSqlParameterSource()
                .addValue("anio", consulta.getAnio()).addValue("emision", consulta.getNumeroEmision());
        List<Map<String,Object>> detalles = namedParameterJdbcTemplate.queryForList(RESUMEN_SQL, parametros);
        if (detalles.isEmpty()) return false;
        Map<String,Object> detalle = detalles.get(0);
        consulta.setFechaExpediente(texto(detalle, "fecha_expediente"));
        consulta.setOficinaRegistro(texto(detalle, "oficina_registro"));
        consulta.setOficinaAtencion(texto(detalle, "oficina_atencion"));
        String estadoDestino = texto(detalle, "estado_destino");
        consulta.setEstado(estadoDestino.isEmpty() ? texto(detalle, "estado_registro") : estadoDestino);
        consulta.setObsFinalizar(texto(detalle, "obsfinalizar"));
        consulta.setTieneDerivacion(Boolean.TRUE.equals(detalle.get("tiene_derivacion")));
        return true;
    }
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
      private SimpleJdbcCall spdataSource;
         
    @Override
    public List<ConsultaDocBean> getBuscarExpediente(String numExpediente){

        List<ConsultaDocBean> dataList = new ArrayList<ConsultaDocBean>(); 
        try {                
            List<Map<String, Object>> data = this.jdbcTemplate.queryForList("SELECT * FROM idosgd.pk_sgd_tramite_consulta_tu_expediente(?)", numExpediente);
            if(data != null){
                for(Map<String,Object> u : data) {
                     ConsultaDocBean consulta = new ConsultaDocBean();
                            consulta.setNumeroEmision((String)u.get("numero_emision"));
                            consulta.setAnio((String) u.get("anio"));
                            consulta.setFechaExpediente((String) u.get("fecha_expediente"));
                            consulta.setNroExpediente((String) u.get("rroexpediente")); 
                            consulta.setTipoDocumento((String) u.get("tipo_documento"));
                            consulta.setNroDoc((String) u.get("nrodoc"));
                            consulta.setInstitucion(texto(u, "institucion"));
                            consulta.setRemitente(texto(u, "remitente"));
                            consulta.setFolios(texto(u, "folios"));
                            consulta.setSumilla(texto(u, "sumilla"));
                            consulta.setObservacion(texto(u, "observacion"));
                            consulta.setOficinaAtencion((String) u.get("oficina_atencion"));
                            consulta.setEstado((String) u.get("estado"));
                            consulta.setObsFinalizar((String) u.get("obsfinalizar"));
                            if (!completarResumen(consulta)) continue;
                            dataList.add(consulta);
                }
            }
             
            this.spdataSource =null;              
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
        return dataList;
    } 
       
    @Override
    public List<ConsultaDocSeguimientoBean> getBuscarExpedienteSeguimiento(String filtro){

        List<ConsultaDocSeguimientoBean> dataList = new ArrayList<ConsultaDocSeguimientoBean>(); 
        try {                
            List<Map<String, Object>> data = this.jdbcTemplate.queryForList("SELECT * FROM idosgd.pk_sgd_tramite_consulta_tu_expediente_seguimiento(?)", filtro);
            if(data != null){
                for(Map<String,Object> u : data) {
                     ConsultaDocSeguimientoBean consulta = new ConsultaDocSeguimientoBean();
                            consulta.setAnio((String)u.get("anio"));
                            consulta.setNroEmision((String) u.get("nroemision"));
                            consulta.setTipoDocumento((String) u.get("tipodocumento"));
                            consulta.setNroDocumento((String) u.get("nrodocumento")); 
                            consulta.setEmisor((String) u.get("emisor"));
                            consulta.setDestinatario((String) u.get("destinatario"));
                            consulta.setFechaEmision((String) u.get("fechaemision"));
                            consulta.setEstadoDes((String) u.get("estadodes"));
                            consulta.setInstitucion((String) u.get("institucion"));
                            consulta.setRemitente((String) u.get("remitente"));                            
                            dataList.add(consulta);
                }
            }
             
            this.spdataSource =null;              
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
            ex.printStackTrace();
        }
        return dataList;
    } 
    
}
