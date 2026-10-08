/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.service.AnexoDocumentoService;
import pe.gob.onpe.tramitedoc.service.DocumentoAnexoService;
import pe.gob.onpe.tramitedoc.service.DocumentoObjService;
import pe.gob.onpe.tramitedoc.service.ValidarFirmaService;
import pe.gob.onpe.tramitedoc.util.ArchivoTemporal;

/**
 *
 * @author ecueva
 */
@Service("documentoAnexoService")
public class DocumentoAnexoServiceImp implements DocumentoAnexoService{
    
    private static Logger logger=Logger.getLogger("SGDDocAnexoService");        
    
    @Autowired
    private DocumentoObjService documentoObjService;
    
    @Autowired
    private AnexoDocumentoService anexoDocumentoService;   
    
    @Autowired
    private ValidarFirmaService validarFirmaService;    
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String cargaDocAnexoFirmado(String pnuAnn,String pnuEmi,String pnuAne,String pnuSecFirma,String coUsuario, String prutaDoc, String dniUsu, String nroRucInstitucion) throws Exception {
        String vReturn = "NO_OK";
            try {
                DocumentoObjBean docObjBean = documentoObjService.getNombreArchivoAnexo(pnuAnn, pnuEmi, pnuAne);
                if (docObjBean!=null && docObjBean.getNuTamano()>0){
                    docObjBean.setCoUseMod(coUsuario);
                    docObjBean.setNumeroSecuencia(pnuSecFirma);
                    vReturn = validarFirmaService.getValidaCertificadoEmp(docObjBean.getNumeroSecuencia(),dniUsu,nroRucInstitucion);
                    if (vReturn.equals("NO_OK")) {
                        vReturn="Firma no pertenece a Usuario";
                    }else{
                        vReturn=this.cargaDocAnexoFirmado(docObjBean);
                        if(vReturn.equals("OK")){
                            vReturn=anexoDocumentoService.getCanAnexosReqFirma(pnuAnn, pnuEmi);
                            if(vReturn.equals("0")){
                                vReturn=anexoDocumentoService.updRemitosResumenInFirmaAnexos("0", pnuAnn, pnuEmi);
                                if(!vReturn.equals("OK")){
                                    throw new validarDatoException("Error actualizando remitos resumen firma anexos.");
                                }                                        
                            }else{
                                vReturn="OK";
                            }
                        }                        
                    }
                }else{
                   throw new validarDatoException("Error cargando documento Firmado."); 
                }
            } catch (validarDatoException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw new validarDatoException("ERROR EN TRANSACCION.");
            }
        return vReturn;
    }
    
    private String cargaDocAnexoFirmado(DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
        
        if(docObjBean!=null){
            try {
                
                byte[] archivoByte = ArchivoTemporal.leerArchivo(docObjBean.getNumeroSecuencia());
                docObjBean.setDocumento(archivoByte);
                docObjBean.setNuTamano((int) Math.round(((double) archivoByte.length) / 1024));
                    
                
                vReturn=anexoDocumentoService.updArchivoAnexoFirmado(docObjBean);
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
}
