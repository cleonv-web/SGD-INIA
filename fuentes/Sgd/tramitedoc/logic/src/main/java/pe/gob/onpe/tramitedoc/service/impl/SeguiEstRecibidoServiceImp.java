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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstRecBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstRecibidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.dao.SeguiEstRecibidoDao;
import pe.gob.onpe.tramitedoc.service.SeguiEstRecibidoService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author NGilt
 */
@Service("consultaEstRecibidoService")
public class SeguiEstRecibidoServiceImp implements SeguiEstRecibidoService {

    @Autowired
    ApplicationProperties applicationProperties;
    
    @Autowired
    private SeguiEstRecibidoDao seguiEstRecibidoDao;

    @Override
    public List<DocumentoSeguiEstRecibidoBean> getListDocSeguiEstRec(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean) {
        List<DocumentoSeguiEstRecibidoBean> list = null;
        try {
            list = seguiEstRecibidoDao.getListDocSeguiEstRec(buscarDocumentoSeguiEstRecBean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public DocumentoRecepSeguiBean getDocumentoRecepAdmSegui(String snuAnn, String snuEmi, String snuDes) {
        DocumentoRecepSeguiBean documentoRecepConsulBean = null;
        try {
            documentoRecepConsulBean = seguiEstRecibidoDao.getDocumentoRecepAdmSegui(snuAnn, snuEmi, snuDes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return documentoRecepConsulBean;
    }

    @Override
    public List<ReferenciaConsulBean> getDocumentosRefRecepAdmSegui(String snuAnn, String snuEmi) {
        List<ReferenciaConsulBean> list = null;
        //estadoRecepcionDocumento(buscarDocumentoRecep);
        try {
            list = seguiEstRecibidoDao.getDocumentosRefRecepAdmSegui(snuAnn, snuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    
    @Override
    public HashMap getDocumentosEnReferencia(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean) {
        List<DocumentoSeguiEstRecibidoBean> list = null;
        DocumentoSeguiEstRecibidoBean documentoSeguiEstRecibidoBean = null;
        HashMap mp = new HashMap();
        try {
            documentoSeguiEstRecibidoBean = seguiEstRecibidoDao.existeDocumentoReferenciado(buscarDocumentoSeguiEstRecBean);
            if (documentoSeguiEstRecibidoBean != null && documentoSeguiEstRecibidoBean.getMsjResult() != null && documentoSeguiEstRecibidoBean.getMsjResult().equals("OK")) {
                documentoSeguiEstRecibidoBean.setCoDepDes(buscarDocumentoSeguiEstRecBean.getCoDependencia());
                documentoSeguiEstRecibidoBean.setCoEmpDes(buscarDocumentoSeguiEstRecBean.getCoEmpleado());
                
                list = seguiEstRecibidoDao.getDocumentosReferenciadoBusq(documentoSeguiEstRecibidoBean, buscarDocumentoSeguiEstRecBean.getTiAcceso());
                mp.put("recepDocumAdmList", list);
                mp.put("msjResult", "1");//Existe Documento
            } else {
                mp.put("msjResult", "0");//NO existe Documento
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return mp;
    }

    @Override
    public String getRutaReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean) {
        return seguiEstRecibidoDao.getRutaReporte(buscarDocumentoSeguiEstRecBean);
    }

    @Override
    public HashMap getBuscaDependenciaEmite(String coDep, String pdeDepEmite) {
        String vReturn = "1";//muestra lista
        HashMap map = new HashMap();
        List list;
        try {
            list = seguiEstRecibidoDao.getListDestinatarioEmi(coDep, pdeDepEmite);
            if (list != null) {
                if (list.isEmpty()) {
                    list = seguiEstRecibidoDao.getListDestinatarioEmi(coDep, null);
                } else if (list.size() == 1) {
                    vReturn = "0";//solo objeto dependencia
                    map.put("objDestinatario", list.get(0));
                }
            }
            map.put("listaDestinatario", list);
        } catch (Exception e) {
            vReturn = e.getMessage();
        }
        map.put("vReturn", vReturn);
        return map;
    }
    
    @Override
    public ReporteBean getGenerarReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean,Map parametros)
    {
        ReporteBean objReporte = new ReporteBean();
        String eserror="1";//si
        String coRespuesta="";
        String deRespuesta="";
        String prutaReporte = "";
        String deNoDoc = "";
        String nombreArchivo = "";
        String coReporte = "";
        
        try {
            String extensionArch;
            int tipoArchivo=0;
            byte[] archivo;
            
            String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());     
            
            List lista = null;
            
            try{
                lista = getListaReporte(buscarDocumentoSeguiEstRecBean);
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
            if(buscarDocumentoSeguiEstRecBean.getFormatoReporte().equalsIgnoreCase("PDF")){
                coReporte = "TDR391";
                extensionArch=".pdf";
                tipoArchivo = 2;
            }else{
                coReporte = "TDR391_XLS";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            String rutaJasper =  buscarDocumentoSeguiEstRecBean.getRutaReporteJasper() + "/" + coReporte + ".jasper";            
            buscarDocumentoSeguiEstRecBean.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "SEGUIMIENTO_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|SEGUIMIENTO_"+fecha+extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(buscarDocumentoSeguiEstRecBean.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
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
            Logger.getLogger(SeguiEstRecibidoServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(SeguiEstRecibidoServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (IOException ex) {
            Logger.getLogger(SeguiEstRecibidoServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror="1";
            deRespuesta=ex.getMessage();
        } finally{
            objReporte.setcoRespuesta(coRespuesta);
            objReporte.setdeRespuesta(deRespuesta);
            objReporte.setnoUrl(prutaReporte);
            objReporte.setnoDoc(deNoDoc);
            return objReporte;
        }        
    }
    @Override
    public List<DocumentoSeguiEstRecibidoBean> getListaReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean) {
        return seguiEstRecibidoDao.getListaReporteBusqueda(buscarDocumentoSeguiEstRecBean);
    }
}
