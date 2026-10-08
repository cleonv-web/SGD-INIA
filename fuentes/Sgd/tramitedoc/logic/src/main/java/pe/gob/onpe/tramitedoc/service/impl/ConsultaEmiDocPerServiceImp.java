/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiPersConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaEmiDocPersDao;
import pe.gob.onpe.tramitedoc.service.ConsultaEmiDocPerService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Service("consultaEmiDocPerService")
public class ConsultaEmiDocPerServiceImp implements ConsultaEmiDocPerService{

    @Autowired
    ApplicationProperties applicationProperties;
    
    @Autowired
    private ConsultaEmiDocPersDao consultaEmiDocPersDao;        
    
    @Override
    public List<DocumentoEmiPersConsulBean> getDocsPersConsulta(DocumentoEmiPersConsulBean buscarDocPer) {
        List<DocumentoEmiPersConsulBean> list = null;
        try {
            list = consultaEmiDocPersDao.getDocsPersConsulta(buscarDocPer);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;        
    }
    
    @Override
    public DocumentoEmiPersConsulBean getDocumentoPersonalEmi(String pnuAnn, String pnuEmi){
        DocumentoEmiPersConsulBean docPers=null;
        try {
            docPers = consultaEmiDocPersDao.getDocumentoPersonalEmi(pnuAnn,pnuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return docPers;        
    }

    @Override
    public List<DestinatarioDocumentoEmiConsulBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi) {
        List<DestinatarioDocumentoEmiConsulBean> list = null;
        try{
            list = consultaEmiDocPersDao.getLstDestintariotlbEmi(pnuAnn,pnuEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;   
    }
    
    @Override
    public List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi) {
        List<ReferenciaConsulBean> list = null;
        try{
            list = consultaEmiDocPersDao.getLstDocumReferenciatblEmi(pnuAnn,pnuEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;   
    }    

    @Override
    public String getRutaReporte(DocumentoEmiPersConsulBean buscarDocPer) {
        return consultaEmiDocPersDao.getRutaReporte(buscarDocPer);
    }
    @Override
    public List<DocumentoEmiPersConsulBean> getListaReporte(DocumentoEmiPersConsulBean buscarDocPer) {
        return consultaEmiDocPersDao.getListaReporteBusqueda(buscarDocPer);
    }
    @Override
    public ReporteBean getGenerarReporte(DocumentoEmiPersConsulBean buscarDocPer,Map parametros)
    {
        ReporteBean objReporte = new ReporteBean();
        String eserror="1";//si
        String coRespuesta="";
        String deRespuesta="";
        String prutaReporte = "";
        String deNoDoc = "";
        String nombreArchivo = "";
        String coReporte = "";
        List lista = null;
        
        try {
            String extensionArch;
            int tipoArchivo = 0;
            byte[] archivo;
            
            String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());
            try {
                lista = getListaReporte(buscarDocPer);
                eserror = "0";
            } catch (Exception e) {
                eserror = "1";
                deRespuesta = e.getMessage();
            }
            if (lista.size()==0) {                 
                 coRespuesta = "1";
                 deRespuesta="No existe información para generar reporte.";
                 throw new Exception(deRespuesta);
            }
            
            if(buscarDocPer.getFormatoReporte().equalsIgnoreCase("PDF")){
                coReporte = "TDR31";                 
                extensionArch=".pdf";
                tipoArchivo = 2;
            }else{
                coReporte = "TDR31_XLS";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            String rutaJasper =  buscarDocPer.getRutaReporteJasper() + "/" + coReporte + ".jasper";
            
            buscarDocPer.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "EMISIONPERS_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|EMISIONPERS_"+fecha+extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(buscarDocPer.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
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
            
        }catch(JRException ex){
            Logger.getLogger(ConsultaEmiDocPerServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConsultaEmiDocPerServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (IOException ex) {
            Logger.getLogger(ConsultaEmiDocPerServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (Exception e) {
            Logger.getLogger(ConsultaEmiDocPerServiceImp.class.getName()).log(Level.SEVERE, null, e);
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
