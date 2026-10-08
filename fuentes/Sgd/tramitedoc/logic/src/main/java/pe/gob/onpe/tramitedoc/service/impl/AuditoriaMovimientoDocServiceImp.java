/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.sql.SQLClientInfoException;
import java.sql.SQLException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.AudiEstadosMovDocBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.dao.AuditoriaMovimientoDocDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.dao.RecepDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.service.AuditoriaMovimientoDocService;

/**
 *
 * @author ecueva
 */
@Service("auditoriaMovDocService")
public class AuditoriaMovimientoDocServiceImp implements AuditoriaMovimientoDocService{
    
    @Autowired
    private AuditoriaMovimientoDocDao audiDao;
    
    @Autowired
    private EmiDocumentoAdmDao emiDoc;

    @Autowired
    private RecepDocumentoAdmDao receDoc;    
    
    @Override
    public String audiEstadoDocumentoRemito(Usuario currentUser,String nuAnn, String nuEmi, String esDoc){
        String vReturn = "NO_OK";
        AudiEstadosMovDocBean audi=new AudiEstadosMovDocBean();
        try {
            audi.setNuAnn(nuAnn);
            audi.setNuEmi(nuEmi);
            audi.setTiProceso("R");
            audi.setEsProceso(esDoc);
            audi.setDeUser(currentUser.getCoUsuario());
            audi.setDeIpPc(currentUser.getIpPC());
            audi.setDeNamePc(currentUser.getNombrePC());
            audi.setDeUserPc(currentUser.getUsuPc());
            if(isChangeEstadoDoc(audi)){
                vReturn=audiDao.audiEstadoDocumento(audi);
            }else{
                vReturn="OK";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String audiEstadoDocumentoDestino(Usuario currentUser,String nuAnn, String nuEmi, String nuDes, String esDoc){
        String vReturn = "NO_OK";
        AudiEstadosMovDocBean audi=new AudiEstadosMovDocBean();
        try {
            audi.setNuAnn(nuAnn);
            audi.setNuEmi(nuEmi);
            audi.setNuDes(nuDes);
            audi.setTiProceso("D");
            audi.setEsProceso(esDoc);
            audi.setDeUser(currentUser.getCoUsuario());
            audi.setDeIpPc(currentUser.getIpPC());
            audi.setDeNamePc(currentUser.getNombrePC());
            audi.setDeUserPc(currentUser.getUsuPc());
            if(isChangeEstadoDoc(audi)){
                vReturn=audiDao.audiEstadoDocumento(audi);
            }else{
                vReturn="OK";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    private boolean isChangeEstadoDoc(AudiEstadosMovDocBean audi){
        boolean vResult=false;
        String esDocBD=null;
        try {
            if(audi.getNuAnn()!=null&&audi.getNuAnn().trim().length()>0&&audi.getNuEmi()!=null&&audi.getNuEmi().trim().length()>0){
                if(audi.getNuDes()!=null&&audi.getNuDes().trim().length()>0){
                    DocumentoBean doc=receDoc.getEstadoDocumento(audi.getNuAnn(), audi.getNuEmi(), audi.getNuDes());                    
                    esDocBD=doc.getEsDocRec();
                }else{
                    DocumentoEmiBean doc=emiDoc.getEstadoDocumentoAudi(audi.getNuAnn(), audi.getNuEmi());                    
                    esDocBD=doc.getEsDocEmi();
                    if(esDocBD!=null&&esDocBD.equals("9")&&audi.getEsProceso()!=null&&audi.getEsProceso().equals("9")){
                       esDocBD="X";
                    }
                }
                if(audi.getEsProceso()!=null&&esDocBD!=null&&esDocBD.trim().length()>0){
                    if(!audi.getEsProceso().equals(esDocBD)){
                        audi.setEsProceso(esDocBD);
                        vResult=true;
                    }
                }
            }
        }         
        catch (Exception e) {
            e.printStackTrace();
        }       
        return vResult;
    }
}
