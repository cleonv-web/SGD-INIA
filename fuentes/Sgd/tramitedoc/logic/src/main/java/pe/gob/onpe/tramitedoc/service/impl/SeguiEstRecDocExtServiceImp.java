/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.io.File;
import java.io.FileOutputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DocExtRecSeguiEstBean;
import pe.gob.onpe.tramitedoc.dao.SeguiEstRecDocExtDao;
import pe.gob.onpe.tramitedoc.service.SeguiEstRecDocExtService;
import org.codehaus.jackson.map.ObjectMapper;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ECueva
 */
@Service("seguiEstRecDocExtService")
public class SeguiEstRecDocExtServiceImp implements SeguiEstRecDocExtService{
    
    @Autowired
    ApplicationProperties applicationProperties;
    
    @Autowired
    private SeguiEstRecDocExtDao seguiEstRecDocExtDao;   
    
    /*@Autowired
    private MesaPartesDao mesaPartesDao;      */
    
    @Override
    public List<DocExtRecSeguiEstBean> getLsDocExtRecSegui(DocExtRecSeguiEstBean docBuscar) {
        List<DocExtRecSeguiEstBean> list = null;
        try {
            /*String vResult=mesaPartesDao.getPermisoChangeEstadoMP(docBuscar.getCoEmpleado(),docBuscar.getCoDependencia());
            docBuscar.setInCambioEst(vResult!=null&&vResult.equals("1")?"1":"0");            */
            list = seguiEstRecDocExtDao.getLsDocExtRecSegui(docBuscar);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }    

    @Override
    public String getObjJson(String[] params) {
        String retval=null;
        try {
            if(params!=null){
                String coDepEmi=params[0].toString();
                String deDepEmi=params[1].toString();
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, deDepEmi );
                deDepEmi = strWriter.toString();
                String fechaActual=params[2].toString();
                String annioActual=params[3].toString();
                retval="{\"coDepEmi\":\""+coDepEmi+"\",\"deDepEmi\":"+deDepEmi+",\"fechaActual\":\""+fechaActual+"\",\"annioActual\":\""+annioActual+"\"}";
            }
        } catch (Exception e) {
            retval=null;
        }
        return retval;
    }
    
    @Override
    public DocExtRecSeguiEstBean getDocumentoExtSeguiBean(String nuAnn, String nuEmi, String nuDes){
        DocExtRecSeguiEstBean docExt=null;
        try {
            docExt = seguiEstRecDocExtDao.getDocumentoExtSeguiBean(nuAnn,nuEmi,nuDes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return docExt;        
    }
    
    @Override
    public List<DestinatarioDocumentoEmiConsulBean> getLsDestinoEmi(String pnuAnn, String pnuEmi){
        List<DestinatarioDocumentoEmiConsulBean> list = null;
        try {
            list = seguiEstRecDocExtDao.getLsDestinoEmi(pnuAnn, pnuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }   
    
    @Override
    public List<ReferenciaDocExtRecepBean> getLsRefDocExterno(String pnuAnn, String pnuEmi){
        List<ReferenciaDocExtRecepBean> list = null;
        try {
            if(pnuAnn!=null&&pnuAnn.trim().length()>0&&pnuEmi!=null&&pnuEmi.trim().length()>0){
                list = seguiEstRecDocExtDao.getLsRefDocExterno(pnuAnn, pnuEmi);                    
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public String getRutaReporte(DocExtRecSeguiEstBean docBuscar) { 
        return seguiEstRecDocExtDao.getRutaReporte(docBuscar);        
    }    
    
    @Override
    public List<DocExtRecSeguiEstBean> getListaReporte(DocExtRecSeguiEstBean docBuscar) {
        return seguiEstRecDocExtDao.getListaReporteBusqueda(docBuscar);
    }  
    
    @Override
    public List<DocExtRecSeguiEstBean> getListaReporteResumen(DocExtRecSeguiEstBean docBuscar) {
        return seguiEstRecDocExtDao.getListaReporteResumen(docBuscar);
    }  
    
    @Override
    public ReporteBean getGenerarReporte(DocExtRecSeguiEstBean docBuscar, Map parametros) {
        ReporteBean objReporte = new ReporteBean();
        String eserror = "1"; // si
        String coRespuesta = "";
        String deRespuesta = "";
        String prutaReporte = "";
        String deNoDoc = "";
        String nombreArchivo = "";
        String coReporte = "";
        try{  
            String extensionArch;
            int tipoArchivo = 0;
            byte[] archivo;
            String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());  
            
            List lista = null;
            try{
                lista = getListaReporte(docBuscar);
                eserror = "0";
            } catch(Exception ex) {               
                eserror = "1";
                deRespuesta=ex.getMessage();
            }
            if (lista.size() == 0) {                 
                 coRespuesta = "1";
                 deRespuesta="No existe información para generar reporte.";
                 throw new Exception(deRespuesta);
            }
            if (docBuscar.getFormatoReporte().equalsIgnoreCase("PDF")) {
                coReporte = "TDR392";                
                extensionArch=".pdf";
                tipoArchivo = 2;
            } else {
                coReporte = "TDR392_XLS";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            String rutaJasper =  docBuscar.getRutaReporteJasper() + "/" + coReporte + ".jasper";
            docBuscar.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "SEGUI_DOC_EXT_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|SEGUI_DOC_EXT_"+fecha+extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(docBuscar.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
            String rutaBase = applicationProperties.getRutaTemporal() + "/" + prutaReporte;
            
            File tmpFile = new File(rutaBase);
            FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.write(archivo);
            fos.flush();
            fos.close();
            String rutaUrl = "reporte?coReporte=";
            prutaReporte = rutaUrl + prutaReporte;
            
            if (eserror.equals("0")) {
                coRespuesta = eserror;
            } else {
                coRespuesta = "1";
            }
        } catch(Exception ex) {
            eserror = "1";
            deRespuesta = ex.getMessage();
        } finally {
            objReporte.setcoRespuesta(coRespuesta);
            objReporte.setdeRespuesta(deRespuesta);
            objReporte.setnoUrl(prutaReporte);
            objReporte.setnoDoc(deNoDoc);
            return objReporte;
        }        
    }
    
    @Override
    public ReporteBean getGenerarReporteReumen(DocExtRecSeguiEstBean docBuscar, Map parametros) {
        ReporteBean objReporte = new ReporteBean();
        String eserror = "1"; // si
        String coRespuesta = "";
        String deRespuesta = "";
        String prutaReporte = "";
        String deNoDoc = "";
        String nombreArchivo = "";
        String coReporte = "";
        try{  
            String extensionArch;
            int tipoArchivo = 0;
            byte[] archivo;
            String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());  
            
            List lista = null;
            try{
                lista = getListaReporteResumen(docBuscar);
                eserror = "0";
            } catch(Exception ex) {               
                eserror = "1";
                deRespuesta=ex.getMessage();
            }
            if (lista.size() == 0) {                 
                 coRespuesta = "1";
                 deRespuesta="No existe información para generar reporte.";
                 throw new Exception(deRespuesta);
            }
            if (docBuscar.getFormatoReporte().equalsIgnoreCase("PDF")) {
                coReporte = "TDR394";                
                extensionArch=".pdf";
                tipoArchivo = 2;
            } else {
                coReporte = "TDR394";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            String rutaJasper =  docBuscar.getRutaReporteJasper() + "/" + coReporte + ".jasper";
            docBuscar.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "RESU_DOC_EXT_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|RESU_DOC_EXT_"+fecha+extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(docBuscar.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
            String rutaBase = applicationProperties.getRutaTemporal() + "/" + prutaReporte;
            
            File tmpFile = new File(rutaBase);
            FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.write(archivo);
            fos.flush();
            fos.close();
            String rutaUrl = "reporte?coReporte=";
            prutaReporte = rutaUrl + prutaReporte;
            
            if (eserror.equals("0")) {
                coRespuesta = eserror;
            } else {
                coRespuesta = "1";
            }
        } catch(Exception ex) {
            eserror = "1";
            deRespuesta = ex.getMessage();
        } finally {
            objReporte.setcoRespuesta(coRespuesta);
            objReporte.setdeRespuesta(deRespuesta);
            objReporte.setnoUrl(prutaReporte);
            objReporte.setnoDoc(deNoDoc);
            return objReporte;
        }        
    }
}
