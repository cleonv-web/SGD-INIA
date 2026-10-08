/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.TupaBean;
import pe.gob.onpe.tramitedoc.dao.TupaDao;
import pe.gob.onpe.tramitedoc.service.TupaService;

/**
 *
 * @author RBerrocal
 */
@Service("tupaService")
public class TupaServiceImp implements TupaService{

    @Autowired
    private TupaDao tupaDao; 
    
    public List<TupaBean> getTupaList() {
        List<TupaBean> list = null;
        try {
            list = tupaDao.getTupaList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<TupaBean> getTupaBusqList(String busTupaCorto, String busTupaDescripcion) {
        List<TupaBean> list = null;
        try {
            list = tupaDao.getTupaBusqList(busTupaCorto, busTupaDescripcion);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String insTupa(TupaBean tupa, String codusuario) {
        String vReturn = "NO_OK";
        try {
            vReturn = tupaDao.insTupa(tupa, codusuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    public TupaBean getTupa(String codTupa){
        TupaBean tupaBean = null;
                try {
            tupaBean = tupaDao.getTupa(codTupa);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tupaBean;
    }
    
    public String updTupa(TupaBean tupa, String codusuario){
        String vReturn = "NO_OK";
        try {
            tupa.setDeNombre(tupa.getDeNombre().toUpperCase());
            tupa.setDeDescripcion(tupa.getDeDescripcion().toUpperCase());
            
            vReturn = tupaDao.updTupa(tupa, codusuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
}
