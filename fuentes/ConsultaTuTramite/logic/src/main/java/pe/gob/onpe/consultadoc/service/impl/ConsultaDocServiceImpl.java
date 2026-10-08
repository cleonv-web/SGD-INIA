/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.consultadoc.service.impl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils; 
import pe.gob.onpe.consultadoc.bean.TipoDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocSeguimientoBean;
import pe.gob.onpe.consultadoc.web.util.Utility;
import pe.gob.onpe.consultadoc.dao.ConsultaDocDao;
import pe.gob.onpe.consultadoc.service.ConsultaDocService;

/**
 *
 * @author RATAYAURI
 */
@Service("consultaDocService")
public class ConsultaDocServiceImpl implements ConsultaDocService{
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
    private String EstadoAtencion="";
    private String ObservacionAtencion="";
    private StringBuilder AtencionDestinatario = new StringBuilder();
    
    @Autowired
    private ConsultaDocDao consultaDocDao;
    
    @Override
    public List<ConsultaDocBean> getBuscarExpediente(String numExpediente){
        
        List<ConsultaDocBean> list = null;
        
        try {
            list = consultaDocDao.getBuscarExpediente(numExpediente);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return list;
    }
     
    @Override
    public List<String> SeguimientoOficinaAtencion(String filtro){        
        List<String> resultadoProceso = new ArrayList<String>();
        EstadoAtencion = "";
        ObservacionAtencion = "";
        AtencionDestinatario = new StringBuilder();    
        try {
            StringBuilder Result = new StringBuilder();
            String result = SeguimientoChildrenOfiAtencion(filtro);
            if (result != null && result != "")
            {
                 Result.append(result);
                 resultadoProceso.add(AtencionDestinatario.toString());
                 resultadoProceso.add(EstadoAtencion);
                 resultadoProceso.add(ObservacionAtencion);
            }
            else 
            {
                 resultadoProceso.add("");
                 resultadoProceso.add("");
                 resultadoProceso.add("");
            }
            
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return resultadoProceso;
    }  
 private String SeguimientoChildrenOfiAtencion(String filtro){
    StringBuilder Result = new StringBuilder();
    List<ConsultaDocSeguimientoBean> Lista = consultaDocDao.getBuscarExpedienteSeguimiento(filtro);
    if (Lista != null && Lista.size() > 0){
        for(ConsultaDocSeguimientoBean dato : Lista){
            AtencionDestinatario.append(" ► " + dato.getFechaEmision() + " => " + dato.getDestinatario() + " => " + dato.getEstadoDes()  + "<br/>");
            if(EstadoAtencion =="" || EstadoAtencion.equals("EN PROCESO"))
              {
                EstadoAtencion = dato.getEstadoDes();
              }
            ObservacionAtencion = dato.getObsDestino();
        }
        for(ConsultaDocSeguimientoBean dato : Lista){
            String result = SeguimientoChildrenOfiAtencion(dato.getAnio() + dato.getNroEmision() + dato.getNroDestino());
            if (result != null && result != "")
            {
                Result.append(result);
            }                    
        }
    }
    return Result.toString();
 }
     
     
}
