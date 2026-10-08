/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.CitizenBean;
import pe.gob.onpe.tramitedoc.dao.CiudadanoDao;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.dao.MesaPartesDao;
import pe.gob.onpe.tramitedoc.service.CiudadanoService;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.segdi.pide.consultas.bean.ConsultaDniBean;
import pe.gob.segdi.pide.consultas.ws.WSConsultaDni;

/**
 *
 * @author RBerrocal
 */
@Service("ciudadanoService")
public class CiudadanoServiceImp implements CiudadanoService{
    
    @Autowired
    private CiudadanoDao ciudadanoDao; 
     @Autowired
    private MesaPartesDao mesaPartesDao;
     @Autowired
    private DatosPlantillaDao datosPlantillaDao;
    public List<CitizenBean> getCiudadanoList(){
        List<CitizenBean> list = null;
        try {
            list = ciudadanoDao.getCiudadanoList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<CitizenBean> getCiudadanoBusqList(String busCiudDocumento, String busCiudApPaterno, String busCiudApMaterno, String busCiudNombres,String pUsuario) {
        List<CitizenBean> list = null;
        try {
            list = ciudadanoDao.getCiudadanoBusqList(busCiudDocumento, busCiudApPaterno, busCiudApMaterno, busCiudNombres);
            if((list == null || list.size()==0) && busCiudDocumento!=null && busCiudDocumento.length()==8){
                try {
                    String cCELE_DECOR=mesaPartesDao.getPassDniPide("CO_DNI_PIDE", pUsuario);
                    
                    WSConsultaDni wSSunat = new WSConsultaDni();
                    ConsultaDniBean beanDdp  = wSSunat.consultaDni(datosPlantillaDao.getParametros("URL_RENIEC_REST"), busCiudDocumento,cCELE_DECOR.substring(0, 8),datosPlantillaDao.getParametros("RUC_INSTITUCION"),Utility.getInstancia().descifrar(cCELE_DECOR.substring(9, cCELE_DECOR.length()),ConstantesSec.SGD_SECRET_KEY_PASSWORD));
                        if(beanDdp.getPrenombres()!=null&&beanDdp.getApPrimer()!=null){                            
                            String vResult=ciudadanoDao.insPideCiudadano(beanDdp,busCiudDocumento);
                    
                            if (vResult=="OK")
                                {
                                    list = ciudadanoDao.getCiudadanoBusqList(busCiudDocumento, busCiudApPaterno, busCiudApMaterno, busCiudNombres); 
                                }                            
                            } 
                    }
                    catch (Exception e) {
                         e.printStackTrace();   
                    }    
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public ProcessResult<List<CitizenBean>> getCiudadanoList(String busCiudDocumento, String busCiudApPaterno, String busCiudApMaterno, String busCiudNombres,String pUsuario, FiltroPaginate paginate) {
        ProcessResult<List<CitizenBean>> list = null;
        try {
            list = ciudadanoDao.getCiudadanoBusqList(busCiudDocumento, busCiudApPaterno, busCiudApMaterno, busCiudNombres,paginate);
            if((list == null || list.getResult() == null || list.getResult().size()==0) && busCiudDocumento!=null && busCiudDocumento.length()==8){
                try {
                    String cCELE_DECOR=mesaPartesDao.getPassDniPide("CO_DNI_PIDE", pUsuario);
                    
                    WSConsultaDni wSSunat = new WSConsultaDni();
                    ConsultaDniBean beanDdp  = wSSunat.consultaDni(datosPlantillaDao.getParametros("URL_RENIEC_REST"), busCiudDocumento,cCELE_DECOR.substring(0, 8),datosPlantillaDao.getParametros("RUC_INSTITUCION"),Utility.getInstancia().descifrar(cCELE_DECOR.substring(9, cCELE_DECOR.length()),ConstantesSec.SGD_SECRET_KEY_PASSWORD));
                        if(beanDdp.getPrenombres()!=null&&beanDdp.getApPrimer()!=null){                            
                            String vResult=ciudadanoDao.insPideCiudadano(beanDdp,busCiudDocumento);
                    
                            if (vResult=="OK")
                                {
                                    list = ciudadanoDao.getCiudadanoBusqList(busCiudDocumento, busCiudApPaterno, busCiudApMaterno, busCiudNombres,paginate);
                                }                            
                            } 
                    }
                    catch (Exception e) {
                         e.printStackTrace();   
                    }    
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public String insCiudadano(CitizenBean ciudadano, String codUsuario) {
        String vReturn = "NO_OK";
        try {
            //Se hace el cambio para que se guarde los datos relevantes en mayuscula
            ciudadano.setDeApp(ciudadano.getDeApp().toUpperCase().trim());
            ciudadano.setDeApm(ciudadano.getDeApm().toUpperCase().trim());
            ciudadano.setDeNom(ciudadano.getDeNom().toUpperCase().trim());
            vReturn = ciudadanoDao.insCiudadano(ciudadano, codUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public CitizenBean getCiudadano(String codCiudadano) {
        CitizenBean ciudadano = null;
        try {
            
            ciudadano = ciudadanoDao.getCiudadano(codCiudadano);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ciudadano;
    }

    public String updCiudadano(CitizenBean ciudadano, String codUsuario) {
        String vReturn = "NO_OK";
        try {
            //Se hace el cambio para que se guarde los datos relevantes en mayuscula
            ciudadano.setDeApp(ciudadano.getDeApp().toUpperCase().trim());
            ciudadano.setDeApm(ciudadano.getDeApm().toUpperCase().trim());
            ciudadano.setDeNom(ciudadano.getDeNom().toUpperCase().trim());
            
            vReturn = ciudadanoDao.updCiudadano(ciudadano, codUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
}
