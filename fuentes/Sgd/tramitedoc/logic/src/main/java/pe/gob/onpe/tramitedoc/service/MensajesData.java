/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.ElementoMensajeroBean;
import pe.gob.onpe.tramitedoc.bean.EstadoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.dao.MensajesDao;
/**
 *
 * @author oti3
 */
@Service(value="mensajeriaData")
public class MensajesData {
@Autowired   
private MensajesDao mensajeriaDao;

private List<EstadoDocumentoBean> grpEstadoMsjList;    
private List<SiElementoBean> grpAmbitoMsjList;
private List<SiElementoBean> grpTipoEnvMsjList;
private List<SiElementoBean> grpTipoMsjList;
private List<EstadoDocumentoBean> grpEstadoCargaList; 
private List<ElementoMensajeroBean> grpMensajeroList; 
private List<DependenciaBean> grpDependecia; 
private List<SiElementoBean> grpMotivoMsjList;
    public MensajesData(){
        
    }

    public List<EstadoDocumentoBean> getLstEstadoMsj(String nomTabla) {
        if(this.grpEstadoMsjList == null)
        {
            this.grpEstadoMsjList = mensajeriaDao.listEstadosMsj(nomTabla);
        }
        return grpEstadoMsjList;
    }
    
    public List<EstadoDocumentoBean> getLstEstadoCarga(String nomTabla) {
//        if(this.grpEstadoCargaList == null)
      //  {
            this.grpEstadoCargaList = mensajeriaDao.listEstadosCarga(nomTabla);
     //   }
        return grpEstadoCargaList;
    }   
    
    public List<SiElementoBean> getLstAmbitoMsj(String pctabCodtab){
        if(this.grpAmbitoMsjList == null){
            this.grpAmbitoMsjList = mensajeriaDao.getLsSiElementoBean(pctabCodtab);
        }
        return grpAmbitoMsjList;        
    }
    
    public List<SiElementoBean> getListTipoEnvMsj(String pctabCodtab){
        if(this.grpTipoEnvMsjList == null){
            this.grpTipoEnvMsjList = mensajeriaDao.getLsSiElementoBean(pctabCodtab);
        }
        return grpTipoEnvMsjList;        
    }

    public List<DependenciaBean> getListOficina(){
        if(this.grpDependecia == null){
            this.grpDependecia = mensajeriaDao.getLsOficina();
        }
        return grpDependecia;        
    }

    public List<SiElementoBean> getLstTipoMsj(String pctabCodtab){
        if(this.grpTipoMsjList == null){
            this.grpTipoMsjList = mensajeriaDao.getLsSiElementoBean(pctabCodtab);
        }
        return grpTipoMsjList;        
    }    
    
    
    public List<ElementoMensajeroBean> getLstMensajero(String tipo,String Ambito){
        if(this.grpMensajeroList == null){
            this.grpMensajeroList = mensajeriaDao.getListMensajero(tipo,Ambito);
            System.out.println(tipo);
        }
        return grpMensajeroList;        
    }   
    
    public List<SiElementoBean> getLstMotivoMsj(String pctabCodtab){
        if(this.grpMotivoMsjList == null){
            this.grpMotivoMsjList = mensajeriaDao.getLsMotivo(pctabCodtab);
        }
        return grpMotivoMsjList;        
    }
}
