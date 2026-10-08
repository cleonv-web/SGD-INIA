package pe.gob.segdi.wsiotramite.dao.impl;

import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;

import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;

import pe.gob.segdi.wsiotramite.bean.RecepcionExterna;
import pe.gob.segdi.wsiotramite.bean.RespuestaConsultaTramite;
import pe.gob.segdi.wsiotramite.dao.IRecepcionExternaDao;
import pe.gob.segdi.wsiotramite.dao.impl.cons.RecepcionExternaConst;


/**
 * 
 * Objeto : RecepcionExternaDaoImpl.java.
 * Descripción : Clase de servicio, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 28/03/2018.
 * Autor : Arturo Delgadoo.
 * 
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 * 
 * 
 */
@Repository("iRecepcionExternaDao")
public class RecepcionExternaDaoImpl extends JdbcTemplate implements IRecepcionExternaDao,Serializable{
    
	private static final long serialVersionUID = 1L;
	private static Logger depurador = Logger.getLogger(RecepcionExternaDaoImpl.class.getName());

	@Autowired
    public RecepcionExternaDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

	@Override
	public int insRecepcionExterna(RecepcionExterna recepcionExterna) throws Exception {
		//depurador.info("insRecepcionExterna ==>");
		GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
		String id_column = "sidrecext";
		update(con ->{
			PreparedStatement ps = con.prepareStatement(RecepcionExternaConst.INS_RECPCION_EXTERNA,new String[]{id_column});
			ps.setString(1, recepcionExterna.getVrucentrem());
			ps.setString(2, recepcionExterna.getVuniorgrem());
			ps.setString(3, recepcionExterna.getCtipdociderem());
			ps.setString(4, recepcionExterna.getVnumdociderem());
			ps.setString(5, recepcionExterna.getVcuo());
			ps.setString(6, recepcionExterna.getVcuoref());
			return ps;
			},keyHolder);
		
		BigDecimal id = (BigDecimal) keyHolder.getKeys().get(id_column);
		
		return Integer.valueOf(id.intValue());
	}

	@Override
	public RespuestaConsultaTramite consultarRegistroTramite(String vcuo) throws Exception {
		//depurador.info("consultarRegistroTramite ==>"+vcuo);
		RespuestaConsultaTramite respuestaConsultaTramite = new RespuestaConsultaTramite();
		try {
			respuestaConsultaTramite = queryForObject(RecepcionExternaConst.CONSULTAR_REGISTRO_TRAMITE,new BeanPropertyRowMapper<RespuestaConsultaTramite>(RespuestaConsultaTramite.class),
					vcuo);
			respuestaConsultaTramite.setVcodres("0000");
		} catch (EmptyResultDataAccessException e){
			respuestaConsultaTramite = new RespuestaConsultaTramite();
			respuestaConsultaTramite.setVcodres("0001");
		} catch (Exception e) {
			depurador.error(null, e);
		}
		return respuestaConsultaTramite;
	}

	
	
	
	
	
   
}
