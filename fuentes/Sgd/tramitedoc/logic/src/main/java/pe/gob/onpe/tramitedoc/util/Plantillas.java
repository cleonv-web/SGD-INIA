/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.util;

import java.util.ArrayList;
import java.util.List;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import pe.gob.onpe.tramitedoc.bean.PlantillaDocx;
import pe.gob.onpe.tramitedoc.service.DatosPlantillaService;

/**
 *
 * @author WCutipa
 */
public class Plantillas {
    
    
    private DatosPlantillaService datosPlantillaService;    
    
    private static Plantillas instancia=new Plantillas();
    private static List<PlantillaDocx> listDocx;
    

    private Plantillas(){
        
    }

    public static Plantillas getInstancia(){
        return Plantillas.instancia;
    }
    
 public PlantillaDocx getPlantilla(String idPlantilla, String coDep){
        PlantillaDocx  objDocx = null ;
 
//YUAL
        
       // if( Plantillas.listDocx == null ){
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.datosPlantillaService = (DatosPlantillaService) applicationContext.getBean("datosPlantillaService");
            
            List<PlantillaDocx> listPlantilla = datosPlantillaService.getLstPlantillasDocx(coDep,idPlantilla);   

            if(listPlantilla != null && listPlantilla.size()>0)
            {
                return listPlantilla.get(0);
            }
            
            /*
            Plantillas.listDocx = new ArrayList<PlantillaDocx>();
            Plantillas.listDocx = listPlantilla;   
        //}
        
//        for(int i=0;i<listDocx.size();i++){
//          objDocx = listDocx.get(i);
//          if( objDocx.getCoTipoDoc().equals(idPlantilla)){
//            return objDocx;
//          }
//        }
        int index=-1;
        for(int i=0;i<listDocx.size();i++){
          objDocx = listDocx.get(i);
          if(objDocx.getCoTipoDoc().equals(idPlantilla)){
            if(objDocx.getCoDep().equals(coDep)){
                return objDocx;
            }
            index=i;
          }
        }
        if(index>-1){
            return listDocx.get(index);
        } */      

        
        return null;
    }

    
}
