/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; 
import pe.gob.onpe.mpvdoc.bean.CiudadanoBean;
import pe.gob.onpe.mpvdoc.bean.ConsultaDniBean;
import pe.gob.onpe.mpvdoc.bean.ConsultaSunatBean;
import pe.gob.onpe.mpvdoc.bean.DocumentoBean;
import pe.gob.onpe.mpvdoc.bean.TipoDocBean;
import pe.gob.onpe.mpvdoc.bean.MpvdocBean;
import pe.gob.onpe.mpvdoc.bean.MpvdocSeguimientoBean;
import pe.gob.onpe.mpvdoc.bean.ProveedorBean;
import pe.gob.onpe.mpvdoc.bean.ResultMpvdocBean;
import pe.gob.onpe.mpvdoc.bean.TipoDocumentoBean;
import pe.gob.onpe.mpvdoc.bean.UbigeoBean;
import pe.gob.onpe.mpvdoc.web.util.Utility;
import pe.gob.onpe.mpvdoc.dao.MpvdocDao;
import pe.gob.onpe.mpvdoc.service.MpvdocService;
import pe.gob.onpe.mpvdoc.web.util.Constantes;
import pe.gob.segdi.pide.consultas.ws.WSConsultaDni;
import pe.gob.segdi.pide.consultas.ws.WSConsultaRuc;

/**
 *
 * @author WCONDORI
 */
@Service("mpvdocService")
public class MpvdocServiceImpl implements MpvdocService{
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
    private String EstadoAtencion="";
    private String ObservacionAtencion="";
    private StringBuilder AtencionDestinatario = new StringBuilder();
    
    @Autowired
    private MpvdocDao mpvdocDao;
       
    @Override
     public List<TipoDocumentoBean> getTiposDocs() { 
        return mpvdocDao.listTipoDocumento();
    }  
     @Override
     public List<UbigeoBean> getListDepartamento() { 
        return mpvdocDao.listDepartamento();
    }  
    @Override
     public List<UbigeoBean> getListProvincia(String codDep) { 
        return mpvdocDao.listProvincia(codDep);
    }  
     @Override
     public List<UbigeoBean> getListDistrito(String codDepProv) { 
        return mpvdocDao.listDistrito(codDepProv);
    }   
     @Override
    public String getCiudadano(String pnuDoc){
        CiudadanoBean ciudadanoBean = null;
        String coRespuesta="0";
        StringBuilder retval = new StringBuilder();   
        try {
            ciudadanoBean =  mpvdocDao.getCiudadano(pnuDoc);
            retval.append("{\"coRespuesta\":\"");
            if(ciudadanoBean!=null){
                coRespuesta="1";
                retval.append(coRespuesta);
                retval.append("\",");                
                retval.append("\"ciudadanoBean\":{");
                retval.append("\"nuDoc\":\"");
                retval.append(ciudadanoBean.getNuDocumento());                     
                retval.append("\",\"nombre\":\"");
                retval.append(ciudadanoBean.getNombre());   
                retval.append("\",\"paterno\":\"");
                retval.append(ciudadanoBean.getPaterno()); 
                retval.append("\",\"materno\":\"");
                retval.append(ciudadanoBean.getMaterno());                
                retval.append("\"}");                  
            }else{
                try {
                    String cCELE_DECOR=mpvdocDao.getPassDniPide("CO_DNI_PIDE");
                    
                    WSConsultaDni wSSunat = new WSConsultaDni();
                    ConsultaDniBean beanDdp  = wSSunat.consultaDni(mpvdocDao.getParametros("URL_RENIEC_REST"), pnuDoc,cCELE_DECOR.substring(0, 8),mpvdocDao.getParametros("RUC_INSTITUCION"),Utility.getInstancia().descifrar(cCELE_DECOR.substring(9, cCELE_DECOR.length()),Constantes.SGD_SECRET_KEY_PASSWORD));
                        if(beanDdp.getPrenombres()!=null&&beanDdp.getApPrimer()!=null){                            
                            String vResult=mpvdocDao.insPideCiudadano(beanDdp,pnuDoc);                    
                            if (vResult=="OK")
                                {
                                    ciudadanoBean = mpvdocDao.getCiudadano(pnuDoc);
                                    if(ciudadanoBean!=null){
                                        coRespuesta="1";
                                        retval.append(coRespuesta);
                                        retval.append("\",");                
                                        retval.append("\"ciudadanoBean\":{");
                                        retval.append("\"nuDoc\":\"");
                                        retval.append(ciudadanoBean.getNuDocumento());                     
                                        retval.append("\",\"nombre\":\"");
                                        retval.append(ciudadanoBean.getNombre());   
                                        retval.append("\",\"paterno\":\"");
                                        retval.append(ciudadanoBean.getPaterno()); 
                                        retval.append("\",\"materno\":\"");
                                        retval.append(ciudadanoBean.getMaterno());                                            
                                        retval.append("\"}");                  
                                    }  
                                    else
                                        {
                                        retval.append(coRespuesta);
                                        retval.append("\"");  
                                        }
                                }
                            else
                                {
                                    retval.append(coRespuesta);
                                    retval.append("\"");  
                                }

                            }
                        else
                            {
                                retval.append(coRespuesta);
                                retval.append("\"");  
                            }
                    }
                    catch (Exception e) {
                         e.printStackTrace();
                         retval.append(coRespuesta);
                         retval.append("\"");  
                    }               
            }
            retval.append("}");
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retval.toString();
    }
 
      @Override
    public String getProveedor(String pnuRuc){
        ProveedorBean proveedorBean;
        String coRespuesta="0";
        StringBuilder retval = new StringBuilder();       
        try {
            proveedorBean = mpvdocDao.getProveedor(pnuRuc);
            retval.append("{\"coRespuesta\":\"");
            if(proveedorBean!=null){
                coRespuesta="1";
                retval.append(coRespuesta);
                retval.append("\",");                
                retval.append("\"proveedorBean\":{");
                retval.append("\"nuRuc\":\"");
                retval.append(proveedorBean.getNuRuc());                     
                retval.append("\",\"deRuc\":\"");
                retval.append(proveedorBean.getDescripcion());               
                retval.append("\"}");                  
            }else{
                    try {
                        
                    WSConsultaRuc wSSunat = new WSConsultaRuc();
                    ConsultaSunatBean beanDdp  = wSSunat.consultaSunat(mpvdocDao.getParametros("URL_SUNAT_REST"),pnuRuc);
                        if(beanDdp.getDdp_nombre()!=null){
                            
                            String vResult=mpvdocDao.insPideProveedor(beanDdp);
                    
                            if (vResult=="OK")
                                {
                                    proveedorBean = mpvdocDao.getProveedor(pnuRuc);
                                    if(proveedorBean!=null)
                                        {
                                        coRespuesta="1";
                                        retval.append(coRespuesta);
                                        retval.append("\",");                
                                        retval.append("\"proveedorBean\":{");
                                        retval.append("\"nuRuc\":\"");
                                        retval.append(proveedorBean.getNuRuc());                     
                                        retval.append("\",\"deRuc\":\"");
                                        retval.append(proveedorBean.getDescripcion());                                       
                                        retval.append("\"}");                  
                                        }   
                                    else
                                        {
                                        retval.append(coRespuesta);
                                        retval.append("\"");  
                                        }
                                }
                            else
                                {
                                    retval.append(coRespuesta);
                                    retval.append("\"");  
                                }

                            }
                        else
                            {
                                retval.append(coRespuesta);
                                retval.append("\"");  
                            }
                    }
                    catch (Exception e) {
                         e.printStackTrace();
                         retval.append(coRespuesta);
                         retval.append("\"");  
                    }

            }
            retval.append("}");
        } catch (Exception e) {
            e.printStackTrace();
            retval.append("}");
        }
        return retval.toString();
    }
     @Override
     public ResultMpvdocBean getGrabarExpediente(MpvdocBean mpvdocBean) { 
        return mpvdocDao.getGrabarExpediente(mpvdocBean);
    }  
     @Override
     public String RegisternotificarCorreo(String v_nom, String p_Para, String detalleDoc, String asunto, String cuerpo) { 
        return mpvdocDao.RegisternotificarCorreo(v_nom, p_Para, detalleDoc, asunto, cuerpo);
    }  
     @Override
     public List<DocumentoBean> getListaReporteBusquedaVoucher(DocumentoBean documento) { 
        return mpvdocDao.getListaReporteBusquedaVoucher(documento);
    }  
}
