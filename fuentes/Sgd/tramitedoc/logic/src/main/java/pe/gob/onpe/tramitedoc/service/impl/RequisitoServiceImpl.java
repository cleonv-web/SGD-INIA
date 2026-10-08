/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.RequisitoBean;
import pe.gob.onpe.tramitedoc.dao.RequisitoDao;
import pe.gob.onpe.tramitedoc.service.RequisitoService;

/**
 *
 * @author JHuamanF
 */
@Service("requisitoService")
public class RequisitoServiceImpl implements RequisitoService {
    
    @Autowired
    private RequisitoDao requisitoDao; 

    @Override
    public List<RequisitoBean> getRequisitoList() {
        List<RequisitoBean> list = null;
        try {
            list = requisitoDao.getRequisitoList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public List<RequisitoBean> getRequisitoBusqList(RequisitoBean requisito) {
        List<RequisitoBean> list = null;
        try {
            list = requisitoDao.getRequisitoBusqList(requisito);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public String insRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        try {
            vReturn = requisitoDao.insRequisito(requisito, codusuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public RequisitoBean getRequisito(String codRequisito) {
        RequisitoBean tupaBean = null;
        try {
            tupaBean = requisitoDao.getRequisito(codRequisito);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return tupaBean;
    }

    @Override
    public String updRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        try {
            requisito.setDescripcion(requisito.getDescripcion().toUpperCase());
            
            vReturn = requisitoDao.updRequisito(requisito, codusuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String insTupaRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        try {
            vReturn = requisitoDao.insTupaRequisito(requisito, codusuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String updTupaRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        try {
            vReturn = requisitoDao.updTupaRequisito(requisito, codusuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String eliTupaRequisito(RequisitoBean requisito) {
        String vReturn = "NO_OK";
        try {
            vReturn = requisitoDao.eliTupaRequisito(requisito);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public List<RequisitoBean> getTupaRequisitoBusqList(RequisitoBean requisito) {
        List<RequisitoBean> list = null;
        try {
            list = requisitoDao.getTupaRequisitoBusqList(requisito);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
}
