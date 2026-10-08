/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAltaDao;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoAltaService;
import pe.gob.onpe.tramitedoc.util.ArchivoTemporal;

/**
 *
 * @author WCutipa
 */
@Service("emiDocumentoAltaService")
public class EmiDocumentoAltaServiceImp implements EmiDocumentoAltaService{

    private static Logger logger=Logger.getLogger("SGDEmiDocumentoAltaService");    
    
    @Autowired
    private EmiDocumentoAdmDao emiDocumentoAdmDao;
    
    public String cargaDocumentoEmi(String coUsu,String pnuAnn, String pnuEmi, DocumentoFileBean pfileAnexo) {
        String vReturn = "NO_OK";
        DocumentoObjBean docObjBean = new DocumentoObjBean();
        
        try {

            docObjBean.setNuAnn(pnuAnn);
            docObjBean.setNuEmi(pnuEmi);
            docObjBean.setTiCap("01");
            docObjBean.setNombreArchivo(pfileAnexo.getNombreArchivo());
            docObjBean.setCoUseMod(coUsu);
            docObjBean.setTipoDoc(docObjBean.getNombreArchivo().substring(docObjBean.getNombreArchivo().lastIndexOf('.') + 1));

            //Verificar en caso de Linux
            docObjBean.setNombreArchivo(docObjBean.getNombreArchivo().substring(docObjBean.getNombreArchivo().lastIndexOf('\\') + 1));

            byte[] archivoByte = pfileAnexo.getArchivoBytes();
            docObjBean.setDocumento(archivoByte);
            docObjBean.setNuTamano((int) Math.round(((double) archivoByte.length) / 1024));

            vReturn = emiDocumentoAdmDao.updDocumentoObj(docObjBean);
        } catch (Exception ex) {
            StringBuffer mensaje = new StringBuffer();
            vReturn = "Error en Leer Documento de Repositorio";
            mensaje.append(docObjBean.getNuAnn()+"."+docObjBean.getNuEmi());
            logger.error(mensaje,ex);            
        }

        return vReturn;

    }
    
    @Override
    public String changeToProyecto(DocumentoEmiBean documentoEmiBean) {
        String vReturn = "NO_OK";
        String pesDocEmi;
        String pcoUseMod = documentoEmiBean.getCoUseMod();
        String pcoEmpRes = documentoEmiBean.getCoEmpRes();
        try {
            documentoEmiBean = emiDocumentoAdmDao.getEstadoDocumento(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi());
            if(documentoEmiBean!=null && (documentoEmiBean.getEsDocEmi().equals("7")||documentoEmiBean.getEsDocEmi().equals("0"))){
                pesDocEmi = documentoEmiBean.getEsDocEmi();
                vReturn = emiDocumentoAdmDao.verificarDocumentoLeido(documentoEmiBean.getNuAnn(),documentoEmiBean.getNuEmi());
                if (vReturn != null && vReturn.equals("0")) {
                        documentoEmiBean.setEsDocEmi("5");
                        documentoEmiBean.setCoUseMod(pcoUseMod);
                        vReturn = emiDocumentoAdmDao.updEstadoDocumentoEmiAdm(documentoEmiBean);  
                        if(!vReturn.equals("OK")){
                            vReturn = "Error al Cambiar a Proyecto el Documento.";
                        }                        
                } else {
                    vReturn = "Documento ya fue leido por los Destinatarios.";
                }
            }else{
                vReturn = "Documento no puede cambiar de Estado.";
            }
        } catch (Exception e) {
            vReturn = e.getMessage();
        }
        return vReturn;
    }
    
}
