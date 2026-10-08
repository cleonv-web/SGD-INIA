
package pe.gob.segdi.wsiotramite.dao;

import pe.gob.segdi.wsiotramite.bean.RecepcionExterna;
import pe.gob.segdi.wsiotramite.bean.RespuestaConsultaTramite;


public interface IRecepcionExternaDao {
    
	int insRecepcionExterna(RecepcionExterna emisionExterna) throws Exception;
	
	RespuestaConsultaTramite consultarRegistroTramite(String  vcuo) throws Exception;
	
	
	
	 
}
