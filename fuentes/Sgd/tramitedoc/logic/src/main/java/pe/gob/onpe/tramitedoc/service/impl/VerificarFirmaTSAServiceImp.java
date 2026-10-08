/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DestinoTSABean;
import pe.gob.onpe.tramitedoc.dao.FirmaTSADao;
import pe.gob.onpe.tramitedoc.service.VerificarFirmaTSAService;

/**
 *
 * @author ecueva
 */
@Service("VerificarFirmaTSAService")
public class VerificarFirmaTSAServiceImp implements VerificarFirmaTSAService{

    @Autowired
    private FirmaTSADao firmaTSADao;
    
    @Override
    public String VerificarDestinoCNM(String nuAnn, String nuEmi, String tiDoc) {
        String vReturn="NO_OK";
        List<DestinoTSABean> lsDestinos;
        try {
            lsDestinos=firmaTSADao.getLsDestinoDoc(nuAnn, nuEmi);
            for (DestinoTSABean des : lsDestinos) {
                String can=firmaTSADao.getCanEntidadTSA(des.getNuRucDes(), tiDoc);
                if(can.equals("1")){
                    vReturn="OK";
                    break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
}
