/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.TemaBean;
import pe.gob.onpe.tramitedoc.dao.TemaDao;
import pe.gob.onpe.tramitedoc.service.TemaService;

/**
 *
 * @author oti2
 */
@Service("temaService")
public class TemaServiceImp implements TemaService {
    
    @Autowired
    private TemaDao temaDao;
    @Override
    public List<TemaBean> getListTema(TemaBean buscarTemaBean) {
        List<TemaBean> list = null;
        try { 
            list = temaDao.getListTema(buscarTemaBean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
     @Override
    public String updTemaDestinos(String codigoEmision,String codTema) {
        String vReturn = "ERR";
        try{
           vReturn =   temaDao.updTemaDestinos(codigoEmision,codTema);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    
    }
     @Override
    public String updTemaRemitos(String codigoEmision,String codTema) {
         String vReturn = "ERR";
        try{
           vReturn =   temaDao.updTemaRemitos(codigoEmision,codTema);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    }
     @Override
    public String getEditTemaRemitos(String codigoEmision) {
        String vReturn = "ERR";
        try{
           vReturn =   temaDao.getEditTemaRemitos(codigoEmision);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;    
    }
     @Override
    public String getEditTemaDestinos(String codigoEmision) {
       String vReturn = "ERR";
        try{
           vReturn =   temaDao.getEditTemaDestinos(codigoEmision);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    }
}

