/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.PlantillaDocx;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.service.DatosPlantillaService;

/**
 *
 * @author wcutipa
 */
@Service("datosPlantillaService")
public class DatosPlantillaServiceImp implements DatosPlantillaService{

    @Autowired
    private DatosPlantillaDao datosPlantillaDao;
    
    @Override
    public List<PlantillaDocx> getLstPlantillasDocx(String pcoDep,String idPlantilla){
        List<PlantillaDocx> list = null ;
        try{
            list = datosPlantillaDao.getLstPlantillasDocx(pcoDep, idPlantilla);
            
        }catch(Exception ex){
            list = null;
            ex.printStackTrace();
        }

        return list;        
    }  
    
    
    
}
