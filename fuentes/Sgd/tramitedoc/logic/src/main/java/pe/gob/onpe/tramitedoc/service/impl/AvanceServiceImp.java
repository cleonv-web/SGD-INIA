/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import pe.gob.onpe.tramitedoc.bean.AvanceBean;
import pe.gob.onpe.tramitedoc.bean.TemaBean;
import pe.gob.onpe.tramitedoc.dao.AvanceDao;
import pe.gob.onpe.tramitedoc.service.AvanceService;

/**
 *
 * @author oti2
 */
@Service("avanceService")
public class AvanceServiceImp implements AvanceService  {
    
     @Autowired
    private AvanceDao avanceDao;
    @Override
    public List<AvanceBean> getListAvance(AvanceBean buscarAvanceBean) {
        List<AvanceBean> list = null;
        try { 
            list = avanceDao.getListAvance(buscarAvanceBean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
     @Override
    public String insertAvance(AvanceBean buscarAvanzadaBean) {
     String vReturn = "ERR";
        try{
           vReturn =   avanceDao.insertAvance(buscarAvanzadaBean);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    }
}
