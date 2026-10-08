
package pe.gob.segdi.wsiotramite.service.impl;


import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import pe.gob.segdi.wsiotramite.bean.RecepcionExterna;
import pe.gob.segdi.wsiotramite.bean.RespuestaConsultaTramite;
import pe.gob.segdi.wsiotramite.dao.IRecepcionExternaDao;
import pe.gob.segdi.wsiotramite.service.IDocumentoExternoService;
import pe.gob.segdi.wsiotramite.service.IRecepcionExternaService;
/**
 * 
 * Objeto : RecepcionExternaServiceImp.java.
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
@Service("iRecepcionExternaService")
public class RecepcionExternaServiceImp implements IRecepcionExternaService {
	private static Logger depurador = Logger.getLogger(RecepcionExternaServiceImp.class.getName());
	@Autowired
	IRecepcionExternaDao iRecepcionExternaDao;
	@Autowired
	IDocumentoExternoService iDocumentoExternoService;
	
    @Override
	@Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
	public void insRecepcionExterna(RecepcionExterna recepcionExterna) throws Exception {
    	//depurador.info("insRecepcionExterna ==>");
		
		int sidrecext = 0;
		sidrecext = iRecepcionExternaDao.insRecepcionExterna(recepcionExterna);
		
		if(sidrecext != 0){
			int flagDocExt = 0;
			recepcionExterna.getDocumentoExterno().setSidrecext(sidrecext);
			flagDocExt = iDocumentoExternoService.insDocumentoExterno(recepcionExterna.getDocumentoExterno());
		}
	}

	@Override
	public RespuestaConsultaTramite consultarRegistroTramite(String vcuo)  {
		try {
			return iRecepcionExternaDao.consultarRegistroTramite(vcuo);
		} catch (Exception e) {
			depurador.error(null,e);
			return null;
		}
	}

}
