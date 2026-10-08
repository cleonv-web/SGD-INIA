/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.PlantillaDocx;

/**
 *
 * @author wcutipa
 */
public interface DatosPlantillaService {

    List<PlantillaDocx> getLstPlantillasDocx(String pcoDep,String idPlantilla);
    
}
