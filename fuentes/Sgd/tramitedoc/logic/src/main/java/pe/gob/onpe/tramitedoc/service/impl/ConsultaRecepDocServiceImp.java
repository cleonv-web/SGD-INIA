/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaRecepDocDao;
import pe.gob.onpe.tramitedoc.service.ConsultaRecepDocService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

@Service("consultaRecepDocService")
public class ConsultaRecepDocServiceImp implements ConsultaRecepDocService {
    
    @Autowired
    ApplicationProperties applicationProperties;

    @Autowired
    private ConsultaRecepDocDao consultaRecepDocDao;    
    
    @Override
    public BuscarDocumentoRecepConsulBean estadoRecepcionDocumento(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean,String accion){
        estadoRecepcionDocumento(buscarDocumentoRecepConsulBean);
        return buscarDocumentoRecepConsulBean;
    }
        
    private void estadoRecepcionDocumento(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean){
     String sEstado = buscarDocumentoRecepConsulBean.getEstadoDoc();
     if(sEstado.equals("04")){
         buscarDocumentoRecepConsulBean.setEstadoDoc("0");
         buscarDocumentoRecepConsulBean.setPrioridadDoc("2");
     }else if(sEstado.equals("01")){
         buscarDocumentoRecepConsulBean.setEstadoDoc("0");
     }else if(sEstado.equals("07")){
         buscarDocumentoRecepConsulBean.setEstadoDoc("0");
         buscarDocumentoRecepConsulBean.setPrioridadDoc("3");
     }
    }
    
    @Override
    public List<DocumentoRecepConsulBean> getDocumentosBuscaRecepAdm(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean){
        List<DocumentoRecepConsulBean> list = null;
        //estadoRecepcionDocumento(buscarDocumentoRecep);
        try{
            list = consultaRecepDocDao.getDocumentosBuscaRecepAdm(buscarDocumentoRecepConsulBean);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;        
    }
    
    @Override
    public DocumentoRecepConsulBean getDocumentoRecepAdm(String pnuAnn,String pnuEmi,String pnuDes){    
            DocumentoRecepConsulBean documentoRecepConsulBean = null;
        try{
            documentoRecepConsulBean = consultaRecepDocDao.getDocumentoRecepAdm(pnuAnn, pnuEmi, pnuDes);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return documentoRecepConsulBean;
    }    

    @Override
    public List<ReferenciaConsulBean> getDocumentosRefRecepAdm(String pnuAnn, String pnuEmi) {
        List<ReferenciaConsulBean> list = null;
        //estadoRecepcionDocumento(buscarDocumentoRecep);
        try{
            list = consultaRecepDocDao.getDocumentosRefRecepAdm(pnuAnn,pnuEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;   
    }
    
    @Override
    public HashMap getDocumentosEnReferencia(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean){
        List<DocumentoRecepConsulBean> list = null;
        DocumentoRecepConsulBean documentoRecepConsulBean = null;
        HashMap mp = new HashMap();
        try{
            documentoRecepConsulBean = consultaRecepDocDao.existeDocumentoReferenciado(buscarDocumentoRecepConsulBean);
            if(documentoRecepConsulBean!=null && documentoRecepConsulBean.getMsjResult()!=null && documentoRecepConsulBean.getMsjResult().equals("OK")){
                documentoRecepConsulBean.setCoDepDes(buscarDocumentoRecepConsulBean.getCoDependencia());
                documentoRecepConsulBean.setCoEmpDes(buscarDocumentoRecepConsulBean.getCoEmpleado());                
                list = consultaRecepDocDao.getDocumentosReferenciadoBusq(documentoRecepConsulBean,buscarDocumentoRecepConsulBean.getTiAcceso());                
                mp.put("recepDocumAdmList",list);
                mp.put("msjResult", "1");//Existe Documento
            }else{
                mp.put("msjResult", "0");//NO existe Documento
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return mp;           
    }
    
    @Override
    public String getRutaReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean) {
        return consultaRecepDocDao.getRutaReporte(buscarDocumentoRecepConsulBean);
    }   
    @Override
    public List<DocumentoRecepConsulBean> getListaReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean) {
        return consultaRecepDocDao.getListaReporteBusqueda(buscarDocumentoRecepConsulBean);
    } 
    @Override
    public ReporteBean getGenerarReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean,Map parametros)
    {
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
                lista = getListaReporte(buscarDocumentoRecepConsulBean);
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
                coReporte = "TDR09";                
                extensionArch=".pdf";
                tipoArchivo = 2;
            }else{
                coReporte = "TDR09_XLS";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            
            String rutaJasper =  buscarDocumentoRecepConsulBean.getRutaReporteJasper() + "/" + coReporte + ".jasper";
            buscarDocumentoRecepConsulBean.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "RECEPDOCEXT_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|RECEPDOCEXT_"+fecha+extensionArch;
            
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
