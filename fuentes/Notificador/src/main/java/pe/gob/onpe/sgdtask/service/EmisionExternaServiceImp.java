/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.service;

import java.net.URL;
import java.util.Date;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.sgdtask.util.ApplicationProperties;
import pe.gob.onpe.sgdtask.util.Utility;
import pe.gob.onpe.sgdtask.ws.bean.AnexoBean;
import pe.gob.onpe.sgdtask.ws.bean.DocumentoExternoBean;
import pe.gob.onpe.sgdtask.ws.bean.DocumentoPrincipalBean;
import pe.gob.onpe.sgdtask.ws.bean.EmisionExternaBean;
import pe.gob.onpe.sgdtask.ws.dao.AnexoDao;
import pe.gob.onpe.sgdtask.ws.dao.DocumentoPrincipalDao;
import pe.gob.onpe.sgdtask.ws.dao.EmisionExternaDao;
/*import pe.gob.onpe.wsoapiop.schema.Anexo;
import pe.gob.onpe.wsoapiop.schema.ObjectFactory;
import pe.gob.onpe.wsoapiop.schema.RecepcionRequest;
import pe.gob.onpe.wsoapiop.schema.RecepcionResponse;
*/
/**
 *
 * @author FSilva
 */
@Service("emisionExternaService")
public class EmisionExternaServiceImp {

    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    DocumentoPrincipalDao documentoPrincipalDao;
    @Autowired
    AnexoDao anexoDao;
    @Autowired
    private EmisionExternaDao emisionExternaDao;
    private static Logger logger = Logger.getLogger(EmisionExternaServiceImp.class);

    //@Scheduled(cron = "${timer.ws}")
    public void enviarDocumentosExternos() {
        logger.warn("Se inició la tarea de verificación de documentos pendientes de envío.");
        List<EmisionExternaBean> lista = emisionExternaDao.getListEmisionExternaPendientes();
        if (!lista.isEmpty()) {
            for (EmisionExternaBean e : lista) {
                
            //yual
            //comentado por que no funciona
            //    procesarEnvioDocExterno(e.getCoEmision());
            }
        }
        logger.warn("Se finalizó la tarea de verificación de documentos pendientes de envío.");
    }
/*
    public String procesarEnvioDocExterno(long coEmision) {
        String vReturn = "NO_OK";
        try {
            ObjectFactory objectFactory = new ObjectFactory();
            RecepcionRequest request = objectFactory.createRecepcionRequest();
            //Obtener EmisionExterna
            EmisionExternaBean emisionExternaBean = emisionExternaDao.getEmisionExterna(coEmision);
            //obtener documento externo
            DocumentoExternoBean documentoExternoBean = emisionExternaDao.getDocumentoExterno(emisionExternaBean.getCoEmision());
            //leer documento principal
            DocumentoPrincipalBean documentoPrincipalBean = documentoPrincipalDao.getDocumentoPrincipal(documentoExternoBean.getCoDocumentoExterno());
            //archivo principal
            request.setPdfDocumento(documentoPrincipalBean.getBlDocumento());//byte[]
            //request.setDeNombreArchivo(docObj.getDeNomArc());
            //lista de anexos
            List<AnexoBean> lsAnexos = anexoDao.getLisAnexos(documentoExternoBean.getCoDocumentoExterno());
            for (AnexoBean a : lsAnexos) {
                Anexo anexo = objectFactory.createAnexo();
                anexo.setPdfAnexo(a.getBlDocAnexo());
                anexo.setDetalleAnexo(a.getDeDescripcion());
                request.getListaAnexos().add(anexo);
            }
            request.setCoEntidadDestino(emisionExternaBean.getCoEntidadExterna());
            request.setCoEntidadOrigen(applicationProperties.getCoEntidad());
            request.setNoEntidadOrigen(applicationProperties.getNoEntidad());
            request.setCoTipoDocumento(documentoExternoBean.getCoTipoDocumento());
            request.setAsunto(documentoExternoBean.getDeAsunto());
            request.setNoTipoDocumento(documentoExternoBean.getNoTipoDocumento());
            request.setNuDocumento(documentoExternoBean.getNuDocumento());
            request.setNoCargoDestino(documentoExternoBean.getNoCargoDestino());
            request.setNoDestino(documentoExternoBean.getNoDestino());
            request.setNoUnidadOrganicaDestino(documentoExternoBean.getNoUnidadOrgDestino());
            request.setInTupa(emisionExternaBean.getInTupa());
            request.setFeDocumento(Utility.getInstancia().dateToFormatString(emisionExternaBean.getFeEmiSgd()));//fORMATO dd/MM/yyyy
            request.setNuAnexos("" + emisionExternaBean.getNuAnexos());
            request.setFeEnvio(Utility.getInstancia().dateToFormatString2(new Date()));//fORMATO dd/MM/yyyy HH:mm:ss
            //Generar el XML 
            String xml = Utility.getInstancia().jaxbObjectToXML(request);
            request.setSignature(Utility.getInstancia().getBytesArchivoXML(xml));
            String wsdlLocation = applicationProperties.getUrlSoapExt();
            pe.gob.onpe.wsoapiop.schema.RecepcionService service = new pe.gob.onpe.wsoapiop.schema.RecepcionService(new URL(wsdlLocation));
            pe.gob.onpe.wsoapiop.schema.Recepcion port = service.getRecepcionSoap11();

            RecepcionResponse response = port.recepcion(request);
            String coRespuesta = response.getIdRespuesta();
            if (coRespuesta.equals("01")) {
                EmisionExternaBean emi = new EmisionExternaBean();
                long coRecepcion = Long.parseLong(response.getCoRecepcion());
                emi.setCoRecepcionExterna(coRecepcion);
                emi.setCoEmision(Long.parseLong(documentoExternoBean.getCoEmision()));
                emi.setFeEnvio(Utility.getInstancia().getDateFromString(request.getFeEnvio()));
                //Archivos XML a actualizar
                emi.setBlSignatureEmision(request.getSignature());
                emi.setBlSignatureRecepcion(response.getSignature());
                //actualizar codigo de recepcion.
                vReturn = emisionExternaDao.updRecepcionexterna(emi);
                if (!vReturn.equals("OK")) {
                    vReturn = "Error actualizando codigo de recepcion externa.";
                }
                logger.warn("Documento enviado:" + coEmision + ", coRecepcion:" + coRecepcion);
            }
            vReturn = "OK";
        } catch (Exception e) {
            logger.error("Error en el envío de documento:" + coEmision);
            e.printStackTrace();
        }
        return vReturn;
    }*/
}
