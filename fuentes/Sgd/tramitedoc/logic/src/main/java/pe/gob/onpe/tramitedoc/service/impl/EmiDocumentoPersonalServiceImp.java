/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DestinoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaEmiDocBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaRemitoBean;
import pe.gob.onpe.tramitedoc.bean.TblRemitosBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.dao.CommonQueryDao;
import pe.gob.onpe.tramitedoc.dao.DocumentoVoBoDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoPersonalDao;
import pe.gob.onpe.tramitedoc.service.ActualizaResumenService;
import pe.gob.onpe.tramitedoc.service.AuditoriaMovimientoDocService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoPersonalService;
import pe.gob.onpe.tramitedoc.service.ValidarFirmaService;
import pe.gob.onpe.tramitedoc.util.ArchivoTemporal;

/**
 *
 * @author ecueva
 */
@Service("emiDocumentoPersonalService")
public class EmiDocumentoPersonalServiceImp implements EmiDocumentoPersonalService{

    @Autowired
    private EmiDocumentoPersonalDao emiDocumentoPersonalDao;  
    
    @Autowired
    private ActualizaResumenService actualizaResumenService;   
    
    @Autowired
    AuditoriaMovimientoDocService audiMovDoc; 
    
    @Autowired
    private ValidarFirmaService validarFirmaService;
    
    @Autowired
    private CommonQueryDao commonQueryDao;    
    /*segdi mvaldera para vobo*/
    @Autowired
    private EmiDocumentoAdmDao emiDocumentoAdmDao;

    @Autowired
    private DocumentoVoBoDao docVoboDao;  
    /*segdi mvaldera para vobo*/
    
    @Override
    public List<DocumentoEmiBean> getDocumentosEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmiBean) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        List<DocumentoEmiBean> list = null;
        try{
            list = emiDocumentoPersonalDao.getDocumentosEmiAdm(buscarDocumentoEmiBean);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;        
    }
    
    @Override
    public DocumentoEmiBean getDocumentoEmiAdm(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        DocumentoEmiBean documentoEmiBean = null;
        try{
            documentoEmiBean = emiDocumentoPersonalDao.getDocumentoEmiAdm(pnuAnn, pnuEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return documentoEmiBean;        
    }
    
    @Override
    public List<DestinatarioDocumentoEmiBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi) {
        List<DestinatarioDocumentoEmiBean> list = null;
        try{
            list = emiDocumentoPersonalDao.getLstDestintariotlbEmi(pnuAnn,pnuEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;   
    }
    
    @Override
    public List<ReferenciaBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi) {
        List<ReferenciaBean> list = null;
        try{
            list = emiDocumentoPersonalDao.getLstDocumReferenciatblEmi(pnuAnn,pnuEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;   
    }
    
    @Override
    public DocumentoEmiBean getDocumentoEmiAdmNew(String codDependencia,String codEmpleado,String codLocal) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        DocumentoEmiBean documentoEmiBean = null;
        try{
            documentoEmiBean = emiDocumentoPersonalDao.getDocumentoEmiAdmNew(codDependencia,codEmpleado,codLocal);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return documentoEmiBean;        
    }
    
    @Override
    @Transactional (propagation = Propagation.REQUIRED,rollbackFor=Exception.class) 
    public String grabaDocumentoEmiAdm(TrxDocumentoEmiBean trxDocumentoEmiBean,Usuario usuario) throws Exception{
       String vReturn = "NO_OK";
        try{
            String pnuAnn = trxDocumentoEmiBean.getNuAnn();
            String pnuEmi = trxDocumentoEmiBean.getNuEmi();
            String pcoUserMod = trxDocumentoEmiBean.getCoUserMod();
            DocumentoEmiBean documentoEmiBean = trxDocumentoEmiBean.getDocumentoEmiBean();
            //RemitenteEmiBean remitenteEmiBean = trxDocumentoEmiBean.getRemitenteEmiBean();
            ArrayList<ReferenciaEmiDocBean> lstReferencia = trxDocumentoEmiBean.getLstReferencia();
            ArrayList<DestinatarioDocumentoEmiBean> lstDestinatario = trxDocumentoEmiBean.getLstDestinatario();     
            ArrayList<EmpleadoVoBoBean> lstEmpVoBo = trxDocumentoEmiBean.getLstEmpVoBo();
            DocumentoEmiBean documentoEmiBeanBD=null;
            if(pnuAnn != null && pnuEmi != null && pnuAnn.trim().length()>0 && pnuEmi.trim().length()>0){//UPD
                documentoEmiBeanBD = emiDocumentoAdmDao.getEstadoDocumento(pnuAnn, pnuEmi);
                if(documentoEmiBean != null){
                    documentoEmiBean.setCoUseMod(pcoUserMod);
                    documentoEmiBean.setNuAnn(pnuAnn);
                    documentoEmiBean.setNuEmi(pnuEmi);
                    if(!(documentoEmiBean.getNuDocEmi()!=null&&documentoEmiBean.getNuDocEmi().trim().length()>0)){
                        throw new validarDatoException("Especifique Número de Documento.");
                    }
                    vReturn = emiDocumentoPersonalDao.updDocumentoEmiAdmBean(documentoEmiBean);
                    if (!"OK".equals(vReturn)) {
                        throw new validarDatoException("Error al Actualizar Documento.");
                    }  
                }
                for (ReferenciaEmiDocBean ref : lstReferencia) {
                    String sAccionBD = ref.getAccionBD();
                    if("INS".equals(sAccionBD)){
                        ref.setCoUseCre(pcoUserMod);
                        vReturn = emiDocumentoPersonalDao.insReferenciaDocumentoEmi(pnuAnn, pnuEmi, ref);
                        if (!"OK".equals(vReturn)) {
                            throw new validarDatoException("Error Agregando Referencia.");
                        } 
                    }else if("UPD".equals(sAccionBD)){
                        vReturn = emiDocumentoPersonalDao.updReferenciaDocumentoEmi(pnuAnn, pnuEmi, ref);
                        if (!"OK".equals(vReturn)) {
                            throw new validarDatoException("Error Actualizando Referencia.");
                        }                         
                    }else if("DEL".equals(sAccionBD)){
                        vReturn = emiDocumentoPersonalDao.delReferenciaDocumentoEmi(pnuAnn, pnuEmi, ref);
                        if (!"OK".equals(vReturn)) {
                            throw new validarDatoException("Error Borrando Referencia.");
                        }                         
                    }
                }
                for (DestinatarioDocumentoEmiBean dest : lstDestinatario) {
                    String sAccionBD = dest.getAccionBD();
                    dest.setCoUseCre(pcoUserMod);
                    if("INS".equals(sAccionBD)){
                        vReturn = emiDocumentoPersonalDao.insDestinatarioDocumentoEmi(pnuAnn, pnuEmi, dest);
                        if (!"OK".equals(vReturn)) {
                            throw new validarDatoException("Error Agregando Destinatario.");
                        }  
                    }else if("UPD".equals(sAccionBD)){
                        vReturn = emiDocumentoPersonalDao.updDestinatarioDocumentoEmi(pnuAnn, pnuEmi, dest);
                        if (!"OK".equals(vReturn)) {
                            throw new validarDatoException("Error Actualizando Destinatario.");
                        }                         
                    }else if("DEL".equals(sAccionBD)){
                        vReturn = emiDocumentoPersonalDao.delDestinatarioDocumentoEmi(pnuAnn, pnuEmi, dest);
                        if (!"OK".equals(vReturn)) {
                            throw new validarDatoException("Error Borrando Destinatario.");
                        }                        
                    }
                }
                for(EmpleadoVoBoBean emp : lstEmpVoBo){
                    String sAccionBD = emp.getAccionBD();
                    if("DEL".equals(sAccionBD)){
                        vReturn = emiDocumentoPersonalDao.delPersonalVoBo(pnuAnn, pnuEmi, emp.getCoDependencia(), emp.getCoEmpleado());
                        if (!"OK".equals(vReturn)) {
                            throw new validarDatoException("Error Borrando Personal VB.");
                        }                        
                    }else if("INS".equals(sAccionBD)){
                        vReturn = emiDocumentoPersonalDao.insPersonalVoBo(pnuAnn, pnuEmi, emp.getCoDependencia(), emp.getCoEmpleado(), pcoUserMod);
                        if(!"OK".equals(vReturn)){
                            throw new validarDatoException("Error Agregando Personal VB.");
                        }                        
                    }
                }
            }else{//INSERTAR
                documentoEmiBean.setCoUseMod(pcoUserMod);
                
                documentoEmiBean.setNuAnnExp(trxDocumentoEmiBean.getNuAnnExp());
                documentoEmiBean.setNuSecExp(trxDocumentoEmiBean.getNuSecExp());
                
                vReturn = emiDocumentoPersonalDao.insDocumentoEmiBean(documentoEmiBean);
                if (!"OK".equals(vReturn)) {
                    throw new validarDatoException("Error al Grabar Documento - Número De Documento Existente.");
                }
                pnuEmi = documentoEmiBean.getNuEmi();
                vReturn=audiMovDoc.audiEstadoDocumentoRemito(usuario, documentoEmiBean.getNuAnn(), pnuEmi, "NC");
                if(!vReturn.equals("OK")){
                    vReturn = "Error insertando Auditoria.";
                    throw new validarDatoException(vReturn);
                }                 
                trxDocumentoEmiBean.setNuEmi(pnuEmi);
                trxDocumentoEmiBean.setNuCorEmi(documentoEmiBean.getNuCorEmi());
                trxDocumentoEmiBean.setNuDoc(documentoEmiBean.getNuDoc());
                trxDocumentoEmiBean.setNuDocEmi(documentoEmiBean.getNuDocEmi());
                for (ReferenciaEmiDocBean ref : lstReferencia) {
                    ref.setCoUseCre(pcoUserMod);
                    vReturn = emiDocumentoPersonalDao.insReferenciaDocumentoEmi(pnuAnn, pnuEmi, ref);
                    if (!"OK".equals(vReturn)) {
                        throw new validarDatoException("Error Agregando Referencia.");
                    } 
                }
                for (DestinatarioDocumentoEmiBean dest : lstDestinatario) {
                    dest.setCoUseCre(pcoUserMod);
                    vReturn = emiDocumentoPersonalDao.insDestinatarioDocumentoEmi(pnuAnn, pnuEmi, dest);
                    if (!"OK".equals(vReturn)) {
                        throw new validarDatoException("Error Agregando Destinatario.");
                    }  
                }
                for(EmpleadoVoBoBean emp : lstEmpVoBo){
                    vReturn = emiDocumentoPersonalDao.insPersonalVoBo(pnuAnn, pnuEmi, emp.getCoDependencia(), emp.getCoEmpleado(), pcoUserMod);
                    if(!"OK".equals(vReturn)){
                        throw new validarDatoException("Error Agregando Personal VB.");
                    }
                }                
            }
            //verificar si el documento tiene vistos buenos o esta observado
            if(!(documentoEmiBeanBD!=null&&(documentoEmiBeanBD.getCoEmpEmi().equals(usuario.getCempCodemp())||documentoEmiBeanBD.getCoEmpRes().equals(usuario.getCempCodemp())))){
                vReturn=docVoboDao.existeVistoBuenoDocAdm(pnuAnn, pnuEmi);
                if(vReturn.equals("0")){
                    vReturn="OK";
                }else{
                    throw new validarDatoException("Doc. con Visto Bueno u Observado.");                
                }                  
            }
            /*actualizar resumenes*/
            if (lstDestinatario.size() >= 1) {
                vReturn=actualizaResumenService.updRemitosResumenDes(pnuAnn, pnuEmi);
                if (!"OK".equals(vReturn)) {
                    throw new validarDatoException("Error Actualizando Remito resumen destinatario");
                }
            }
            if (lstReferencia.size() >= 1) {
                vReturn=actualizaResumenService.updRemitosResumenRef(pnuAnn, pnuEmi);
                if (!"OK".equals(vReturn)) {
                    throw new validarDatoException("Error Actualizando Remito resumen Referencia");
                }                
            }            
        }catch (validarDatoException e) { 
            throw e; 
         } 
         catch (Exception e) { 
            e.printStackTrace(); 
            throw new validarDatoException("ERROR EN TRANSACCION"); 
         }
       return vReturn;
    }
    
    public List<DestinoBean> getDestinosListCodDepTipoDes(String nu_ann, String nu_emi) {
        List<DestinoBean> list = null;
        try {
            list = emiDocumentoPersonalDao.getListaDestinosCodDepTipoDes(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<ReferenciaRemitoBean> getReferenciaRemitoList(String nu_ann, String nu_emi) {
        List<ReferenciaRemitoBean> list = null;
        try {
            list = emiDocumentoPersonalDao.getListaReferenciaRemitos(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<DestinoBean> getDestinosListCodPri(String nu_ann, String nu_emi) {
        List<DestinoBean> list = null;
        try {
            list = emiDocumentoPersonalDao.getListaDestinosCodPri(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public String verificaNroDocumentoEmiDuplicado(DocumentoEmiBean documentoEmiBean){
        String vReturn = "NO_OK";
        DocumentoEmiBean documentoEmiBeanBD;
        try{
            documentoEmiBeanBD = emiDocumentoPersonalDao.getEstadoDocumento(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi());
            String pEsDocEmi=documentoEmiBeanBD.getEsDocEmi();
            if(pEsDocEmi!=null&&(pEsDocEmi.equals("5")||pEsDocEmi.equals("7"))){
                vReturn = emiDocumentoPersonalDao.getCanDocumentoEmiDuplicados(documentoEmiBean);
                if(vReturn.equals("0")){
                   vReturn = "OK";
                }else{
                    vReturn ="Número de documento duplicado.";
                }                
            }else{
                vReturn = "Operación no permitida.";
            }
        }catch(Exception e){
            vReturn = e.getMessage();
        }
        return vReturn;
    }    
    
    @Override
    public String changeToProyecto(DocumentoEmiBean documentoEmiBean,Usuario usuario) {
        String vReturn = "NO_OK";
        String pcoUseMod = documentoEmiBean.getCoUseMod();
        try {
            documentoEmiBean = emiDocumentoPersonalDao.getEstadoDocumento(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi());
            if (documentoEmiBean != null && (documentoEmiBean.getEsDocEmi().equals("7") || documentoEmiBean.getEsDocEmi().equals("0"))) {
                String pesDocEmi = documentoEmiBean.getEsDocEmi();
                vReturn = emiDocumentoPersonalDao.verificarDocumentoLeido(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi());
                if (vReturn != null && vReturn.equals("0")) {
                    documentoEmiBean.setEsDocEmi("5");
                    documentoEmiBean.setCoUseMod(pcoUseMod);
                    vReturn = emiDocumentoPersonalDao.updEstadoDocumentoEmiAdm(documentoEmiBean);
                    if (vReturn.equals("OK")) {
                        vReturn=audiMovDoc.audiEstadoDocumentoRemito(usuario, documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi(), pesDocEmi);
                        if(!vReturn.equals("OK")){
                            vReturn = "Error insertando Auditoria.";
                        }                        
                    }else{
                        vReturn = "Error al Cambiar a Proyecto el Documento.";                        
                    }
                } else {
                    vReturn = "Documento ya fue leido por los Destinatarios";
                }
            } else {
                vReturn = "Documento no puede cambiar de Estado.";
            }
        } catch (Exception e) {
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    @Transactional (propagation = Propagation.REQUIRED,rollbackFor=Exception.class) 
    public String changeToEmitido(DocumentoEmiBean documentoEmiBean, String prutaDoc, Usuario usuario) throws Exception{
        String vReturn = "NO_OK";
        String pesDocEmi = documentoEmiBean.getEsDocEmi();
        if(pesDocEmi!=null && !pesDocEmi.equals("")){
            try{
                
                DocumentoObjBean docObjBean = new DocumentoObjBean();

                docObjBean.setNuAnn(documentoEmiBean.getNuAnn());
                docObjBean.setNuEmi(documentoEmiBean.getNuEmi());
                docObjBean.setTiCap(documentoEmiBean.getTiCap());
                docObjBean.setNombreArchivo(prutaDoc);
                docObjBean.setNumeroSecuencia(documentoEmiBean.getNuSecuenciaFirma());
                docObjBean.setCoUseMod(documentoEmiBean.getCoUseMod());
                
                String esOblFirma = commonQueryDao.getInFirmaDoc(documentoEmiBean.getCoDepEmi(),documentoEmiBean.getCoTipDocAdm());                        
                vReturn = validarFirmaService.getValidaCertificadoEmp(docObjBean.getNumeroSecuencia(),usuario.getNuDni());
                if (vReturn.equals("NO_OK")) {
                    throw new validarDatoException("Firma Personal no pertenece a Usuario");
                }else if(vReturn.equals("")&&esOblFirma.equals("F")){
                    throw new validarDatoException("Necesita Firmar el Documento.");
                }                   
                
                vReturn = cargaDocEmi(docObjBean);
                if(!vReturn.equals("OK")){
                    throw new validarDatoException("Error al Cargar Documento");
                }
                documentoEmiBean.setEsDocEmi("0");
                vReturn = emiDocumentoPersonalDao.updEstadoDocumentoEmitido(documentoEmiBean);
                if(!vReturn.equals("OK")){
                    throw new validarDatoException(vReturn);
                }
                
                //YUAL
                List<ReferenciaRemitoBean> lsReferencia;
                lsReferencia=emiDocumentoPersonalDao.getListaReferenciaRemitos(documentoEmiBean.getNuAnn(),documentoEmiBean.getNuEmi());
                ReferenciaRemitoBean oReferenciaRemitoBean= new ReferenciaRemitoBean();
                String g="";
                for(int i=0; i<lsReferencia.size(); i++)
                {
                 //    if(oReferenciaRemitoBean.getNu_des_ref()!=null){
                    oReferenciaRemitoBean=lsReferencia.get(i);
                    g=emiDocumentoPersonalDao.updEstadoDocumentoReferenciasEmitido(docObjBean.getCoUseMod(), oReferenciaRemitoBean.getNu_emi(), oReferenciaRemitoBean.getNu_ann(),oReferenciaRemitoBean.getNu_des_ref());
               // }
                }
                
                

                TblRemitosBean tblRemitosBean = emiDocumentoAdmDao.getDatosDocumento(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi());
                if (!tblRemitosBean.getMsgResult().equals("OK")) {
                    throw new validarDatoException(vReturn);
                }                
                
                
                vReturn=audiMovDoc.audiEstadoDocumentoRemito(usuario, documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi(), pesDocEmi);
                if(!vReturn.equals("OK")){
                    vReturn = "Error insertando Auditoria.";
                    throw new validarDatoException(vReturn);
                }
                
                //verificar si el documento tiene vistos buenos pendeintes o esta observado
                vReturn=docVoboDao.existeVistoBuenoPendienteDocAdm(tblRemitosBean.getNuAnn(), tblRemitosBean.getNuEmi());
                if(vReturn.equals("0")){
                    vReturn="OK";
                }else{
                    throw new validarDatoException("Doc. con Visto Bueno Pendiente u Observado.");
                }
            }catch (validarDatoException e){ 
                throw e; 
             } 
             catch (Exception e) { 
                e.printStackTrace(); 
                throw new validarDatoException("ERROR EN TRANSACCION DE EMISION"); 
             }
        }else{
            vReturn = "Documento no esta Listo.";
        }
        return vReturn;        
    } 
    
    
     @Override
    public String updEstadoDocumentoReferenciasEmitido(String coUseMod,String nuEmi,String nuAnn,String nuDes)
    {
        return emiDocumentoPersonalDao.updEstadoDocumentoReferenciasEmitido(coUseMod,nuEmi,nuAnn,nuDes);
    }
    
    @Override
    public String cargaDocEmi(DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
    
            try{
              docObjBean.setTipoDoc(docObjBean.getNombreArchivo().substring(docObjBean.getNombreArchivo().lastIndexOf('.')+1));
              docObjBean.setNombreArchivo(docObjBean.getNombreArchivo().substring(docObjBean.getNombreArchivo().lastIndexOf('|')+1));
            }catch(Exception e){
            }
            
        
            try{
                byte[] archivoByte = ArchivoTemporal.leerArchivo(docObjBean.getNumeroSecuencia());
                docObjBean.setDocumento(archivoByte);
                docObjBean.setNuTamano((int) Math.round(((double) archivoByte.length)/1024));
                vReturn = emiDocumentoPersonalDao.updDocumentoObj(docObjBean);
            }catch(Exception e){
                vReturn = e.getMessage().substring(0,20);
            }
        return vReturn;
    }
    
    @Override
    public String anularDocumento(DocumentoEmiBean documentoEmiBean, Usuario usuario) {
        String vReturn = "NO_OK";
        String pcoUsedMod = documentoEmiBean.getCoUseMod();
        try {
            documentoEmiBean = emiDocumentoPersonalDao.getEstadoDocumento(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi());
            String pesDocEmi = documentoEmiBean.getEsDocEmi();
            if (pesDocEmi != null && (pesDocEmi.equals("5") || pesDocEmi.equals("7"))) {
                documentoEmiBean.setCoUseMod(pcoUsedMod);
                vReturn = emiDocumentoPersonalDao.verificarDocumentoLeido(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi());
                if (vReturn != null && vReturn.equals("0")) {
                    documentoEmiBean.setEsDocEmi("9");//anulado
                    vReturn = emiDocumentoPersonalDao.anularDocumento(documentoEmiBean);
                    if(vReturn.equals("OK")){
                        vReturn=audiMovDoc.audiEstadoDocumentoRemito(usuario, documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi(), pesDocEmi);
                        if(!vReturn.equals("OK")){
                            vReturn = "Error insertando Auditoria.";
                        }                        
                    }else{
                        vReturn = "Error anulando documento.";
                    }
                } else {
                    vReturn = "Documento ya fue leido por los Destinatarios";
                }
            } else {
                vReturn = "Documento ya fue leido por los Destinatarios";
            }
        } catch (Exception e) {
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public List<ReferenciaBean> getLstDocumReferenciaAtiendeDeriva(DocumentoBean documentoRecepBean) {
        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>(); 
        ReferenciaBean referenciaBean = new ReferenciaBean();
        try{
            referenciaBean.setNuAnnRef(documentoRecepBean.getNuAnn());
            referenciaBean.setCoTipDocAdm(documentoRecepBean.getCoTipDocAdm());
            referenciaBean.setTipDocRef("rec");
            referenciaBean.setLiNuDoc(documentoRecepBean.getNuDoc());
            referenciaBean.setFeEmiCorta(documentoRecepBean.getFeEmiCorta2());
            referenciaBean.setNuEmiRef(documentoRecepBean.getNuEmi());
            referenciaBean.setNuDesRef(documentoRecepBean.getNuDes());
            referenciaBean.setAccionBd("INS");
            referenciaBean.setCoRef("");
            list.add(referenciaBean);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;   
    }    

    @Override
    public String getNumeroDocSiguientePersonal(String pnuAnn, String pcoEmp, String pcoDoc) {
        String vReturn = "NO_OK";
        try {
            vReturn = emiDocumentoPersonalDao.getNumeroDocSiguientePersonal(pnuAnn, pcoEmp, pcoDoc);
        } catch (Exception e) {
            vReturn = "NO_OK";
        }

        return vReturn;
    }    
}
