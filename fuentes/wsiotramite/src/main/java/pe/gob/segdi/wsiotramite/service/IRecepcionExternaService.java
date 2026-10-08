
package pe.gob.segdi.wsiotramite.service;

import pe.gob.segdi.wsiotramite.bean.RecepcionExterna;
import pe.gob.segdi.wsiotramite.bean.RespuestaConsultaTramite;


public interface IRecepcionExternaService {
    
	void insRecepcionExterna(RecepcionExterna recepcionExterna) throws Exception;
	
	RespuestaConsultaTramite consultarRegistroTramite(String  vcuo) throws Exception;
	
	
	
}
