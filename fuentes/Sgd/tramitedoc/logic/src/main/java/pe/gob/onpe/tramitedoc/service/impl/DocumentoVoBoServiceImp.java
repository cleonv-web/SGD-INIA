/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import com.sun.org.apache.bcel.internal.generic.DCONST;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocVistoBuenoBean;
import pe.gob.onpe.tramitedoc.dao.DocumentoVoBoDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.service.DocumentoVoBoService;
import pe.gob.onpe.tramitedoc.service.ValidarFirmaService;
import pe.gob.onpe.tramitedoc.util.ArchivoTemporal;

/**
 *
 * @author ecueva
 */
@Service("documentoVoboService")
public class DocumentoVoBoServiceImp implements DocumentoVoBoService{

    private static Logger logger=Logger.getLogger("SGDocumentoVoBoService");

    @Autowired
    private DocumentoVoBoDao documentoVoBoDao;
    
    @Autowired
    private EmiDocumentoAdmDao emiDocumentoAdmDao;   
    
    @Autowired
    private ValidarFirmaService validarFirmaService;    
    
    @Override
    public List<DocumentoVoBoBean> getLsDocsVoBo(BuscarDocumentoVoBoBean bDocVoBo){
        List<DocumentoVoBoBean> list = null;
        try {
            list = documentoVoBoDao.getLsDocsVoBo(bDocVoBo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public DocumentoVoBoBean getDocumentoVoBo(String pnuAnn, String pnuEmi, String coDep, String coEmpVb){
        DocumentoVoBoBean docVobo = null;
        try {
            docVobo = documentoVoBoDao.getDocumentoVoBo(pnuAnn, pnuEmi,coDep,coEmpVb);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return docVobo;
    }    
    
    @Override
    public String goGrabarDocVoBo(TrxDocVistoBuenoBean trxDocVobo){
        String vReturn = "NO_OK";
        try {
            DocumentoEmiBean doc = emiDocumentoAdmDao.getEstadoDocumento(trxDocVobo.getNuAnn(), trxDocVobo.getNuEmi());
            String esDocEmi = doc.getEsDocEmi();
            if (esDocEmi!=null&&(esDocEmi.equals("7")||(esDocEmi.equals("5")&&doc.getTiEmi().equals("05")))&&trxDocVobo.getDocVoBoBean()!=null) {
                String deObs=trxDocVobo.getDocVoBoBean().getDeObs();
                if(deObs!=null&&deObs.trim().length()>0){
                    esDocEmi="2";
                }else{
                    esDocEmi="0";
                }
                vReturn = documentoVoBoDao.updObsVistoBueno(trxDocVobo.getNuAnn(), trxDocVobo.getNuEmi(), trxDocVobo.getDocVoBoBean().getCoDepUsu(), 
                        trxDocVobo.getDocVoBoBean().getCoEmpUsu(), trxDocVobo.getDocVoBoBean().getDeObs(),trxDocVobo.getCoUserMod(),esDocEmi);                
                if(!vReturn.equals("OK")){
                    vReturn="Error al grabar Observación del Visto Bueno.";
                }
            } else {
                vReturn = "Operación no Permitida.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String cargaDocEmi(DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
        
        if(docObjBean!=null){
            try {
                docObjBean.setTipoDoc(docObjBean.getNombreArchivo().substring(docObjBean.getNombreArchivo().lastIndexOf('.') + 1));
                docObjBean.setNombreArchivo(docObjBean.getNombreArchivo().substring(docObjBean.getNombreArchivo().lastIndexOf('|') + 1));
                
                //Validamos el PDF
                //validarFirmaService.getValidaCertificadoEmp(docObjBean.getNumeroSecuencia(), "01341466");
                
                byte[] archivoByte = ArchivoTemporal.leerArchivo(docObjBean.getNumeroSecuencia());
                docObjBean.setDocumento(archivoByte);
                docObjBean.setNuTamano((int) Math.round(((double) archivoByte.length) / 1024));
                    
                
                vReturn = emiDocumentoAdmDao.updDocumentoObj(docObjBean);
            } catch (Exception ex) {
                StringBuffer mensaje = new StringBuffer();
                vReturn = "Error en Leer Documento de Repositorio";
                mensaje.append(docObjBean.getNumeroSecuencia()+":");
                mensaje.append(docObjBean.getNuAnn()+"."+docObjBean.getNuEmi());
                logger.error(mensaje,ex);            
            }
        }else{
            vReturn = "Error en Carga de Documento";
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("cargaDocEmi:Objeto docObjBean NULO");
            logger.error(mensaje);            
        }
        return vReturn;
    }    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String changeToEnviado(DocumentoVoBoBean docVobo, String prutaDoc,String nuDni,String nroRucInstitucion) throws Exception {
        String vReturn = "NO_OK";
        String pesDocEmi = docVobo.getEsDocEmi();
        if (pesDocEmi!=null&&(pesDocEmi.equals("7")||pesDocEmi.equals("2")||pesDocEmi.equals("0"))) {
            try {
                String existe_vb = documentoVoBoDao.existePersonalVb(docVobo.getNuAnn(), docVobo.getNuEmi(), docVobo.getCoDepUsu(), docVobo.getCoEmpVb());
                if(existe_vb.equals("1")){
                    pesDocEmi=documentoVoBoDao.getEsVobo(docVobo.getNuAnn(), docVobo.getNuEmi(), docVobo.getCoDepUsu(), docVobo.getCoEmpVb());
                    if(pesDocEmi.equals("B")){
                        DocumentoEmiBean doc = emiDocumentoAdmDao.getEstadoDocumento(docVobo.getNuAnn(), docVobo.getNuEmi());
                        pesDocEmi=doc.getEsDocEmi();
                        if (pesDocEmi!=null&&(pesDocEmi.equals("7")||(pesDocEmi.equals("5")))) {
                            DocumentoObjBean docObjBean = new DocumentoObjBean();

                            docObjBean.setNuAnn(docVobo.getNuAnn());
                            docObjBean.setNuEmi(docVobo.getNuEmi());
                            docObjBean.setTiCap(docVobo.getTiCap());
                            docObjBean.setNombreArchivo(prutaDoc);
                            docObjBean.setNumeroSecuencia(docVobo.getNuSecuenciaFirma());
                            docObjBean.setCoUseMod(docVobo.getCoEmpUsu());

                            vReturn = validarFirmaService.getValidaCertificadoEmp(docObjBean.getNumeroSecuencia(),nuDni,nroRucInstitucion);
                            if (!vReturn.equals("OK")) {
                                throw new validarDatoException("Visto Bueno no pertenece a Usuario");
                            }                    
                            vReturn = cargaDocEmi(docObjBean);
                            if (!vReturn.equals("OK")) {
                                throw new validarDatoException("Error al Cargar Documento");
                            }
                            pesDocEmi="1";
                            vReturn=documentoVoBoDao.updToEnviadoVistoBueno(docVobo.getNuAnn(), docVobo.getNuEmi(), docVobo.getCoDepUsu(), docVobo.getCoEmpVb(), pesDocEmi, docVobo.getCoEmpUsu());
                            if(!vReturn.equals("OK")){
                                throw new validarDatoException("Error actualizando estado Visto Bueno.");
                            }
                            vReturn=this.desBloquearVoBoPersonal(docVobo.getNuAnn(), docVobo.getNuEmi());
                            if(!vReturn.equals("OK")){
                                throw new validarDatoException("Error Desbloqueando Visto Bueno.");
                            }                        
                        }else{
                            vReturn = "Operación no Permitida.";                    
                        }
                    }else{
                        vReturn = "El documento ha cambiado, Por Favor Visar nuevamente.";                    
                    }                    
                }else{
                    vReturn = "Operación no Permitida.";                    
                }
            } catch (validarDatoException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw new validarDatoException("ERROR EN TRANSACCION DE ENVIO.");
            }
        } else {
            vReturn = "Documento no esta Listo.";
        }
        return vReturn;
    }
    
    @Override
    public String goChangeToSinVobo(DocumentoVoBoBean docVobo){
        String vReturn = "NO_OK";
        String pesDocEmi = docVobo.getEsDocEmi();
        if (pesDocEmi!=null&&(pesDocEmi.equals("1"))) {
            try {
                DocumentoEmiBean doc = emiDocumentoAdmDao.getEstadoDocumento(docVobo.getNuAnn(), docVobo.getNuEmi());
                pesDocEmi=doc.getEsDocEmi();
                if (pesDocEmi!=null&&pesDocEmi.equals("7")) {
                    pesDocEmi="0";
                    vReturn=documentoVoBoDao.updToSinVistoBueno(docVobo.getNuAnn(), docVobo.getNuEmi(), docVobo.getCoDepUsu(), docVobo.getCoEmpVb(), pesDocEmi, docVobo.getCoEmpUsu());
                    if(!vReturn.equals("OK")){
                        vReturn="Error actualizando estado Sin Visto Bueno.";
                    }                    
                }else{
                    vReturn = "Operación no Permitida.";                    
                }
            } catch (Exception e) {
                vReturn = e.getMessage();
            }
        } else {
            vReturn = "Documento no esta Listo.";
        }
        return vReturn;
    }    
    
    @Override
    public String getDeObsEmpVoBo(String nuAnn, String nuEmi, String coDep, String coEmp){
        String deObs=null;
        try {
            deObs=documentoVoBoDao.getDeObsEmpVoBo(nuAnn, nuEmi, coDep, coEmp);            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return deObs;
    }

    @Override
    public String existeVistoBuenoPendienteDocAdm(String nuAnn, String nuEmi) {
        String vResult="NO_OK";
        try {
            vResult=documentoVoBoDao.existeVistoBuenoPendienteDocAdm(nuAnn, nuEmi);
            if(vResult.equals("0")){
                vResult="OK";
            }else{
                vResult="Doc. con Visto Bueno Pendiente u Observado.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vResult;
    }
    
    @Override
    public String bloquearVoBoPersonal(String nuAnn,String nuEmi,String coDep,String coEmp,String coUserMod){
        String vResult="NO_OK";
        try {
            String esDocVobo="B";            
            vResult=documentoVoBoDao.updVistoBuenoPersonal(nuAnn, nuEmi, coDep, coEmp, esDocVobo, coUserMod);
//            if(vResult.equals("OK")){
//                esDocVobo="B";
//                vResult = documentoVoBoDao.updVistoBuenoPersonal(nuAnn, nuEmi, esDocVobo, coEmp);
//            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vResult;        
    }
    
    @Override
    public String desBloquearVoBoPersonal(String nuAnn,String nuEmi){
        String vResult="NO_OK";
        try {
            String esDocVobo="0";
            vResult = documentoVoBoDao.updVistoBuenoPersonal(nuAnn, nuEmi);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vResult;        
    }    
}
