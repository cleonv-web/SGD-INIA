/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl; 
import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.List; 
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service; 
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoCargaMsjBean;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepMensajeriaBean; 
import pe.gob.onpe.tramitedoc.bean.DestiDocumentoEnvMensajeriaBean;
import pe.gob.onpe.tramitedoc.bean.DestinoResBen;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepMensajeriaBean; 
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.TipoElementoMensajeriaBean;
import pe.gob.onpe.tramitedoc.dao.MensajeriaDao; 
import pe.gob.onpe.tramitedoc.service.DocumentoMensajeriaService; 
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author wcondori
 */
@Service("documentoMensajeriaService")
public class DocumentoMensajeriaServiceImp implements DocumentoMensajeriaService {
    
    @Autowired
    private MensajeriaDao mensajeriaDao;
     @Autowired
    ApplicationProperties applicationProperties;
     @Override
    public List<DocumentoRecepMensajeriaBean> getDocumentoRecepMensajeria(BuscarDocumentoRecepMensajeriaBean buscarDocumentoRecepMensajeriaBean) {
        List<DocumentoRecepMensajeriaBean> list = null;
        try {
            buscarDocumentoRecepMensajeriaBean.setCoGrupo("1"); 
            list = mensajeriaDao.getDocumentoRecepMensajeria(buscarDocumentoRecepMensajeriaBean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    @Override
    public List<DestiDocumentoEnvMensajeriaBean> getLstDestinoEnvMensajeria(String pnuAnnpnuEmi){
     List<DestiDocumentoEnvMensajeriaBean> list = null;
        try {  
            list = mensajeriaDao.getLstDestinoEnvMensajeria(pnuAnnpnuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    @Override
    public List<TipoElementoMensajeriaBean> getlistTipoElementoMensajeria(String tipo){
     List<TipoElementoMensajeriaBean> list = null;
        try {  
            list = mensajeriaDao.getlistTipoElementoMensajeria(tipo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    @Override
    public List<TipoElementoMensajeriaBean> getListResponsableMensajeria(String tipo,String Ambito,String tipoEnvio){
     List<TipoElementoMensajeriaBean> list = null;
        try {  
            list = mensajeriaDao.getListResponsableMensajeria(tipo,Ambito,tipoEnvio);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }     
      @Override
    public String insMensajeriaDocumento(DocumentoRecepMensajeriaBean documentoMensajeria){
        String vReturn = "ERR";
        try{
           vReturn =   mensajeriaDao.insMensajeriaDocumento(documentoMensajeria);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    }
      @Override
    public String updMensajeriaDocumentoRecibir(String codigos){
        String vReturn = "ERR";
        try{
           vReturn =   mensajeriaDao.updMensajeriaDocumentoRecibir(codigos);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    }
     @Override
    public String updMensajeriaDocumentoDevolver(String codigos){
        String vReturn = "ERR";
        try{
           vReturn =   mensajeriaDao.updMensajeriaDocumentoDevolver(codigos);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    } 
      @Override
    public String selectCalcularFechaPlazo(DocumentoRecepMensajeriaBean documentoMensajeria){
        String vReturn = "ERR";
        try{
           vReturn =   mensajeriaDao.selectCalcularFechaPlazo(documentoMensajeria);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    }
    
     public List<DocumentoRecepMensajeriaBean> getLstDetalleMensajeria(DestinoResBen oDestinoResBen){
     List<DocumentoRecepMensajeriaBean> list = null;
        try {  
            list = mensajeriaDao.getLstDetalleMensajeria(oDestinoResBen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
     
     
    public List<DocumentoRecepMensajeriaBean> getLstDetalleMesaVirtual(DestinoResBen oDestinoResBen){
     List<DocumentoRecepMensajeriaBean> list = null;
        try {  
            list = mensajeriaDao.getLstDetalleMesaVirtual(oDestinoResBen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
      
    @Override
    public ReporteBean getImprimirReporte(BuscarDocumentoRecepMensajeriaBean buscarDocumentoRecepConsulBean, Map parametros) {
       List lista = null;
        ReporteBean objReporte = new ReporteBean();
        String coRespuesta="";
        String deRespuesta="";
        String prutaReporte = "";
        String deNoDoc = "";
        String coReporte = "";
        String extensionArch;
        String nombreArchivo = "";
        int tipoArchivo=0;
        byte[] archivo;
        String eserror="1";//si
            
        try {
            String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());   
            try{
                lista = mensajeriaDao.getEditLstDetalleMensajeria(buscarDocumentoRecepConsulBean.getBusNumDoc());
                eserror="0";
            }catch(Exception ex){               
                eserror="1";
                deRespuesta=ex.getMessage();
            }
            if (lista.size()==0) {                 
                 coRespuesta = "1";
                 deRespuesta="No existe información para generar reporte.";
                 throw new Exception(deRespuesta);
            }
            
            if(buscarDocumentoRecepConsulBean.getFormatoReporte().equalsIgnoreCase("PDF")){                               
                extensionArch=".pdf";
                tipoArchivo = 2;
                coReporte = "TDR_IMPRE_MSJ"; 
            }else{ 
                extensionArch=".xls";
                tipoArchivo = 1;
                coReporte = "TDR_IMPRE_MSJ_XLS"; 
            }
            
            String rutaJasper =  buscarDocumentoRecepConsulBean.getRutaReporteJasper() + "/" + coReporte + ".jasper";
            buscarDocumentoRecepConsulBean.setRutaReporteJasper(rutaJasper);
             
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "DOCUSALMJS_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|DOCUSALMJS_"+fecha+extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(buscarDocumentoRecepConsulBean.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
            if (archivo==null) {
                eserror = "0";
                coRespuesta=eserror;
                throw new Exception("No generó correctamente el archivo del reporte.");
            }
            
            String rutaBase = applicationProperties.getRutaTemporal() + "/" + prutaReporte;
            
            File tmpFile = new File(rutaBase);
            FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.write(archivo);
            fos.flush();
            fos.close();
            String rutaUrl = "reporte?coReporte=";
            prutaReporte = rutaUrl + prutaReporte;
            
            if(eserror.equals("0")){
                coRespuesta=eserror;
            }else{
                coRespuesta="1";
            }
        } catch (Exception e) {
            Logger.getLogger(ConsultaEmiDocServiceImp.class.getName()).log(Level.SEVERE, null, e);
            eserror="1";
            deRespuesta=e.getMessage();
        }finally{
            objReporte.setcoRespuesta(coRespuesta);
            objReporte.setdeRespuesta(deRespuesta);
            objReporte.setnoUrl(prutaReporte);
            objReporte.setnoDoc(deNoDoc);
            return objReporte;
        }
    }

    
}
