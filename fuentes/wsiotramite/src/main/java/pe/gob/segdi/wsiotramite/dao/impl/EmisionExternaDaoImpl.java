package pe.gob.segdi.wsiotramite.dao.impl;

import java.io.Serializable;

import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pe.gob.segdi.wsiotramite.bean.EmisionExterna;
import pe.gob.segdi.wsiotramite.dao.IEmisionExternaDao;
import pe.gob.segdi.wsiotramite.dao.impl.cons.EmisionExternaConst;


/**
 * 
 * Objeto : EmisionExternaDaoImpl.java.
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
@Repository("iEmisionExternaDao")
public class EmisionExternaDaoImpl extends JdbcTemplate implements IEmisionExternaDao,Serializable{
    
	private static final long serialVersionUID = 1L;
	private static Logger depurador = Logger.getLogger(EmisionExternaDaoImpl.class.getName());

	@Autowired
    public EmisionExternaDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

	@Override
	public int updCargoEmisionExterna(EmisionExterna emisionExterna)  throws Exception {
		depurador.info("SQL ==>"+ EmisionExternaConst.UPD_CARGO_EMISION_EXTERNA);
		
		Integer flag = 0;
		String estado = obtEstadoDespacho(emisionExterna.getVcuo());
		
		if(estado.equals("E")) {
			flag = update(EmisionExternaConst.UPD_CARGO_EMISION_EXTERNA,
						emisionExterna.getVnumregstdrec(),
						emisionExterna.getVanioregstdrec(),
						emisionExterna.getDfecregstdrec(),
						emisionExterna.getVuniorgstdrec(),
						emisionExterna.getVusuregstdrec(),
						emisionExterna.getBcarstdrec(),
						emisionExterna.getCflgest(),
						emisionExterna.getVobs()	,
						emisionExterna.getVdesanxstdrec(),
						emisionExterna.getVcuo()
						);
		

		}else if(estado.equals("R") || estado.equals("O")) {
			flag = 2;
		}

		return flag;
	}
	
	public String obtEstadoDespacho(String vcuo) throws Exception {
		//depurador.info("obtEstadoDespacho ==>"+vcuo);
		return queryForObject(EmisionExternaConst.OBT_ESTADO_DESPACHO, String.class,vcuo);
	}

	
	
   
}
