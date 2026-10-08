/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import org.springframework.web.context.ContextLoader;
//import org.springframework.web.context.WebApplicationContext;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaEmiDocDao;
import pe.gob.onpe.tramitedoc.service.ConsultaEmiDocService;
//import pe.gob.onpe.tramitedoc.service.DocumentoXmlService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ECueva
 */
@Service("consultaEmiDocService")
public class ConsultaEmiDocServiceImp implements ConsultaEmiDocService{

   @Autowired
   ApplicationProperties applicationProperties;

    //@Autowired
    //private DocumentoXmlService documentoXmlService;
    
    @Autowired
    private ConsultaEmiDocDao consultaEmiDocDao;    
    
    @Override
    public List<DocumentoEmiConsulBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean) {
        List<DocumentoEmiConsulBean> list = null;
        try {
            list = consultaEmiDocDao.getDocumentosBuscaEmiAdm(buscarDocumentoEmiConsulBean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    
    @Override
    public DocumentoEmiConsulBean getDocumentoEmiAdm(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        DocumentoEmiConsulBean documentoEmiConsulBean = null;
        try {
            documentoEmiConsulBean = consultaEmiDocDao.getDocumentoEmiAdm(pnuAnn, pnuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return documentoEmiConsulBean;
    }
    
    @Override
    public String getTipoDestinatarioEmi(String pnuAnn, String pnuEmi) {
        String retval = null;
        try {
            retval = consultaEmiDocDao.getTipoDestinatarioEmi(pnuAnn, pnuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return retval;
    }
    
    @Override
    public HashMap getLstDestintariotlbEmi(String pnuAnn, String pnuEmi) {
        List<DestinatarioDocumentoEmiConsulBean> list = null;
        HashMap map = null;
        try {
            list = consultaEmiDocDao.getLstDestintariotlbEmi(pnuAnn, pnuEmi);
            map = getLstDestinatario(list);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return map;
    }
    
    private HashMap getLstDestinatario(List<DestinatarioDocumentoEmiConsulBean> list) {
        HashMap map = new HashMap();
        List lst1 = new ArrayList();
        List lst2 = new ArrayList();
        List lst3 = new ArrayList();
        List lst4 = new ArrayList();
        for (DestinatarioDocumentoEmiConsulBean dest : list) {
            if (dest != null) {
                String coTipoDest = dest.getCoTipoDestino();
                if (coTipoDest.equals("01")) {
                    lst1.add(dest);
                } else if (coTipoDest.equals("02")) {
                    lst2.add(dest);
                } else if (coTipoDest.equals("03")) {
                    lst3.add(dest);
                } else if (coTipoDest.equals("04")) {
                    lst4.add(dest);
                }
            }
        }
        map.put("lst1", lst1);
        map.put("lst2", lst2);
        map.put("lst3", lst3);
        map.put("lst4", lst4);
        return map;
    }
    
    @Override
    public List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi) {
        List<ReferenciaConsulBean> list = null;
        try {
            list = consultaEmiDocDao.getLstDocumReferenciatblEmi(pnuAnn, pnuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public HashMap getDocumentosEnReferencia(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean){
        List<DocumentoEmiConsulBean> list = null;
        DocumentoEmiConsulBean documentoEmiConsulBean = null;
        HashMap mp = new HashMap();
        try{
            documentoEmiConsulBean = consultaEmiDocDao.existeDocumentoReferenciado(buscarDocumentoEmiConsulBean);
            if(documentoEmiConsulBean!=null && documentoEmiConsulBean.getMsjResult()!=null && documentoEmiConsulBean.getMsjResult().equals("OK")){
                documentoEmiConsulBean.setCoDepEmi(buscarDocumentoEmiConsulBean.getCoDependencia());
                documentoEmiConsulBean.setCoEmpRes(buscarDocumentoEmiConsulBean.getCoEmpleado());                
                list = consultaEmiDocDao.getDocumentosReferenciadoBusq(documentoEmiConsulBean,buscarDocumentoEmiConsulBean.getTiAcceso());                
                mp.put("emiDocumAdmList",list);
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
    public String getRutaReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean) {
        return consultaEmiDocDao.getRutaReporte(buscarDocumentoEmiConsulBean);
    }   
    @Override
    public List<DocumentoEmiConsulBean> getListaReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean) {
        return consultaEmiDocDao.getListaReporteBusqueda(buscarDocumentoEmiConsulBean);
    }   
    
    @Override
    public ReporteBean getGenerarReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean,Map parametros)
    {
        ReporteBean objReporte = new ReporteBean();
        String eserror="1";//si
        String coRespuesta="";
        String deRespuesta="";
        String prutaReporte = "";
        String deNoDoc = "";
        String nombreArchivo = "";
        String coReporte = "";
        try{          
            
            String extensionArch;
            int tipoArchivo=0;
            byte[] archivo;
            
            String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());  
            
            List lista = null;
            try{
                lista = getListaReporte(buscarDocumentoEmiConsulBean);
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
            if(buscarDocumentoEmiConsulBean.getFormatoReporte().equalsIgnoreCase("PDF")){
                coReporte = "TDR10_XLS"; //"TDR10";
                //pcoReporte = "TDR10_XLS";
                extensionArch=".pdf";
                tipoArchivo = 2;
            }else{
                coReporte = "TDR10_XLS";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            String rutaJasper =  buscarDocumentoEmiConsulBean.getRutaReporteJasper() + "/" + coReporte + ".jasper";
            
            buscarDocumentoEmiConsulBean.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "EMISION_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|EMISION_"+fecha+extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(buscarDocumentoEmiConsulBean.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
            if (archivo==null) {
                eserror="1";
                coRespuesta="1";
                deRespuesta = "No se generó correctamente el archivo.";
                throw new Exception("No se generó correctamente el archivo.");                
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
            
        }catch(JRException ex){
            Logger.getLogger(ConsultaEmiDocServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(ConsultaEmiDocServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (IOException ex) {
            Logger.getLogger(ConsultaEmiDocServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        }  
        finally{
            objReporte.setcoRespuesta(coRespuesta);
            objReporte.setdeRespuesta(deRespuesta);
            objReporte.setnoUrl(prutaReporte);
            objReporte.setnoDoc(deNoDoc);
            return objReporte;
        }
            
    }

}
