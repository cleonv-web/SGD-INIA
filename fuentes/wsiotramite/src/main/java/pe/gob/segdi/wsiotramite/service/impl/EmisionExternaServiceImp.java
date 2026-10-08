
package pe.gob.segdi.wsiotramite.service.impl;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.segdi.wsiotramite.bean.EmisionExterna;
import pe.gob.segdi.wsiotramite.dao.IEmisionExternaDao;
import pe.gob.segdi.wsiotramite.service.IEmisionExternaService;
/**
 * 
 * Objeto : EmisionExternaServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface documentos externos , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 28/03/2018.
 * Autor : Arturo Delgado.
 * 
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 * 
 * 
 */
@Service("iEmisionExternaService")
public class EmisionExternaServiceImp implements IEmisionExternaService {
	private static Logger depurador = Logger.getLogger(EmisionExternaServiceImp.class.getName());
	@Autowired
	IEmisionExternaDao iEmisionExternaDao;
	
	
    @Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public int updCargoEmisionExterna(EmisionExterna emisionExterna) throws Exception{
    	//depurador.info("updCargoEmisionExterna ==>");
		int flgRecEmiExt = 0;
		
		flgRecEmiExt = iEmisionExternaDao.updCargoEmisionExterna(emisionExterna);
				
		return flgRecEmiExt;
	}


	


	
}
