/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.OtroOrigenBean;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaOtroOrigenDao;
import pe.gob.onpe.tramitedoc.dao.OtroOrigenDao;
import pe.gob.onpe.tramitedoc.service.OtroOrigenService;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

/**
 *
 * @author NGilt
 */
@Service("otroOrigenService")
public class OtroOrigenServiceImp implements OtroOrigenService {

    @Autowired
    private OtroOrigenDao otroOrigenDao;
    
    @Autowired
    private ConsultaOtroOrigenDao consultaOtroOrigenDao;    
    
    public List<OtroOrigenBean> getOtrosOrigenesList() {
        List<OtroOrigenBean> list = null;
        try {
            list = otroOrigenDao.getOtrosOrigenesList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<SiElementoBean> getTipoDocumentoList() {
        List<SiElementoBean> list = null;
        try {
            list = otroOrigenDao.getTipoDocumentoList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<OtroOrigenBean> getOtroOrigenBusqList(OtroOrigenBean otroOrigen) {
        List<OtroOrigenBean> list = null;
        try {
            list = consultaOtroOrigenDao.getOtroOrigenBusqList(otroOrigen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public ProcessResult<List<OtroOrigenBean>> getOtroOrigenBusqList(String busRazonSocial,FiltroPaginate paginate){
        ProcessResult<List<OtroOrigenBean>> list = null;
        try {
            list = consultaOtroOrigenDao.getOtroOrigenBusqList(busRazonSocial,paginate);
            
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String insOtroOrigen(OtroOrigenBean otroOrigen) {
        String vReturn = "NO_OK";
        try {
            otroOrigen.setDeApeMatOtr(otroOrigen.getDeApeMatOtr().toUpperCase());
            otroOrigen.setDeApePatOtr(otroOrigen.getDeApePatOtr().toUpperCase());
            otroOrigen.setDeNomOtr(otroOrigen.getDeNomOtr().toUpperCase());
            otroOrigen.setDeRazSocOtr(otroOrigen.getDeRazSocOtr().toUpperCase());
            
            vReturn = otroOrigenDao.insOtroOrigen(otroOrigen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public OtroOrigenBean getOtroOrigen(String codOrigen) {
        OtroOrigenBean otroOrigen = null;
        try {
            otroOrigen = otroOrigenDao.getOtroOrigen(codOrigen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return otroOrigen;
    }

    public String updOtroOrigen(OtroOrigenBean otroOrigen) {
        String vReturn = "NO_OK";
        try {
            otroOrigen.setDeApeMatOtr(otroOrigen.getDeApeMatOtr().toUpperCase());
            otroOrigen.setDeApePatOtr(otroOrigen.getDeApePatOtr().toUpperCase());
            otroOrigen.setDeNomOtr(otroOrigen.getDeNomOtr().toUpperCase());
            otroOrigen.setDeRazSocOtr(otroOrigen.getDeRazSocOtr().toUpperCase());
            
            vReturn = otroOrigenDao.updOtroOrigen(otroOrigen);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
      public List<OtroOrigenBean> getOtrosOrigenesPorTipo(String codTipoOtroOrigen) {
        List<OtroOrigenBean> list = null;
        try {
            list = otroOrigenDao.getOtrosOrigenesPorTipo(codTipoOtroOrigen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
}
