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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstEmitidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.dao.SeguiEstEmitidoDao;
import pe.gob.onpe.tramitedoc.service.SeguiEstEmitidoService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author NGilt
 */
@Service("consultaEstEmitidoService")
public class SeguiEstEmitidoServiceImp implements SeguiEstEmitidoService {
    @Autowired
    ApplicationProperties applicationProperties;
    
    @Autowired
    private SeguiEstEmitidoDao seguiEstEmitidoDao;

    @Override
    public DocumentoEmiSeguiBean getDocumentoEmiSeguiBean(String snuAnn, String snuEmi, String snuDes) {
        DocumentoEmiSeguiBean documentoEmiSeguiBean = null;
        try {
            documentoEmiSeguiBean = seguiEstEmitidoDao.getDocumentoEmiSeguiBean(snuAnn, snuEmi, snuDes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return documentoEmiSeguiBean;
    }

    @Override
    public List getDocumentosRefEmiAdmSegui(String snuAnn, String snuEmi) {
        List<ReferenciaConsulBean> list = null;
        try {
            list = seguiEstEmitidoDao.getDocumentosRefRecepAdm(snuAnn, snuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    @Override
    public String getRutaReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        return seguiEstEmitidoDao.getRutaReporte(buscarDocumentoSeguiEstEmiBean);
    }

    @Override
    public List<DocumentoSeguiEstEmitidoBean> getListDocSeguiEstEmi(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        List<DocumentoSeguiEstEmitidoBean> list = null;
        try {
            list = seguiEstEmitidoDao.getListDocSeguiEstRec(buscarDocumentoSeguiEstEmiBean);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public HashMap getBuscaDependenciaEmite(String coDep, String pdeDepEmite) {
        String vReturn = "1";//muestra lista
        HashMap map = new HashMap();
        List list;
        try {
            list = seguiEstEmitidoDao.getListDestinatarioEmi(coDep, pdeDepEmite);
            if (list != null) {
                if (list.isEmpty()) {
                    list = seguiEstEmitidoDao.getListDestinatarioEmi(coDep, null);
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
    public HashMap getDocumentosEnReferencia(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        List<DocumentoSeguiEstEmitidoBean> list = null;
        DocumentoSeguiEstEmitidoBean documentoSeguiEstEmitidoBean = null;
        HashMap mp = new HashMap();
        try {
            documentoSeguiEstEmitidoBean = seguiEstEmitidoDao.existeDocumentoReferenciado(buscarDocumentoSeguiEstEmiBean);
            if (documentoSeguiEstEmitidoBean != null && documentoSeguiEstEmitidoBean.getMsjResult() != null && documentoSeguiEstEmitidoBean.getMsjResult().equals("OK")) {
                documentoSeguiEstEmitidoBean.setCoDepEmi(buscarDocumentoSeguiEstEmiBean.getCoDependencia());
                documentoSeguiEstEmitidoBean.setCoEmpRes(buscarDocumentoSeguiEstEmiBean.getCoEmpleado());
                list = seguiEstEmitidoDao.getDocumentosReferenciadoBusq(documentoSeguiEstEmitidoBean, buscarDocumentoSeguiEstEmiBean.getTiAcceso());
                mp.put("emiDocumAdmList", list);
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
    public ReporteBean getGenerarReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean,Map parametros)
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
                lista = getListaReporte(buscarDocumentoSeguiEstEmiBean);
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
            if(buscarDocumentoSeguiEstEmiBean.getFormatoReporte().equalsIgnoreCase("PDF")){
                coReporte = "TDR393";
                extensionArch=".pdf";
                tipoArchivo = 2;
            }else{
                coReporte = "TDR393_XLS";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            String rutaJasper =  buscarDocumentoSeguiEstEmiBean.getRutaReporteJasper() + "/" + coReporte + ".jasper";            
            buscarDocumentoSeguiEstEmiBean.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "SEGUIMIENTOEMITIDO_"+nombreArchivo + extensionArch;
            deNoDoc = "temp|SEGUIMIENTOEMITIDO_"+fecha+extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(buscarDocumentoSeguiEstEmiBean.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
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
        } finally{
            objReporte.setcoRespuesta(coRespuesta);
            objReporte.setdeRespuesta(deRespuesta);
            objReporte.setnoUrl(prutaReporte);
            objReporte.setnoDoc(deNoDoc);
            return objReporte;
        }        
    }
    @Override
    public List<DocumentoSeguiEstEmitidoBean> getListaReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean) {
        return seguiEstEmitidoDao.getListaReporteBusqueda(buscarDocumentoSeguiEstEmiBean);
    }
}
