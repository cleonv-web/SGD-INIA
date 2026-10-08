/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.dao.ValidaDatosDao;
import pe.gob.onpe.tramitedoc.service.ValidaDatosService;

/**
 *
 * @author WCutipa
 */
@Service("validaDatosService")
public class ValidaDatosServiceImp implements ValidaDatosService{
    @Autowired
    private ValidaDatosDao validaDatosDao;
    
    @Override
    public String getValidaDep(String pcoEmp,String pcoUsu, String pcoDep){
        String vReturn = null;
        try{
           vReturn =  validaDatosDao.getValidaDep(pcoEmp, pcoUsu, pcoDep);
        }catch(Exception e){
            vReturn = null;
        }
        return vReturn;    
    }

    
}
