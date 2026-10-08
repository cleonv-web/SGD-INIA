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
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.CargoEntregaBean;
import pe.gob.onpe.tramitedoc.bean.DetGuiaMesaPartesBean;
import pe.gob.onpe.tramitedoc.bean.DocPedienteEntregaBean;
import pe.gob.onpe.tramitedoc.bean.GuiaMesaPartesBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.TrxGeneraGuiaMpBean;
import pe.gob.onpe.tramitedoc.dao.CargoEntregaDao;
import pe.gob.onpe.tramitedoc.service.CargoEntregaService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Service("cargoEntregaService")
public class CargoEntregaServiceImp implements CargoEntregaService{

    @Autowired
    private CargoEntregaDao cargoEntregaDao;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Override
    public List<CargoEntregaBean> getCargosEntrega(CargoEntregaBean cargo) {
        List<CargoEntregaBean> list = null;
        try {
            list = cargoEntregaDao.getCargosEntrega(cargo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    
    @Override
    public List<DocPedienteEntregaBean> getDocsPendienteEntrega(DocPedienteEntregaBean busqDoc) {
        List<DocPedienteEntregaBean> list = null;
        try {
            list = cargoEntregaDao.getDocsPendienteEntrega(busqDoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
    
    @Override
    public List<DocPedienteEntregaBean> getDocsPendienteEntrega(List<DocPedienteEntregaBean> docs){
        List<DocPedienteEntregaBean> list = null;
        try {
            list=getListDepuradoDocPendEntrega(docs);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    private List<DocPedienteEntregaBean> getListDepuradoDocPendEntrega(List<DocPedienteEntregaBean> list){
        String vResult;
        List<DocPedienteEntregaBean> ls = null;
        try {
            ls=new ArrayList<DocPedienteEntregaBean>();
            for (DocPedienteEntregaBean doc : list) {
                if(doc!=null){
                    String nuAnn=doc.getNuAnn();
                    String nuEmi=doc.getNuEmi();
                    String nuDes=doc.getNuDes();
                    if(nuAnn!=null&&nuAnn.trim().length()>0&&
                       nuEmi!=null&&nuEmi.trim().length()>0&&
                       nuDes!=null&&nuDes.trim().length()>0){
                       vResult=cargoEntregaDao.isDocPendienteEnGuiaMp(nuAnn, nuEmi, nuDes);
                       if(vResult.equals("0")){
                           //doc=cargoEntregaDao.getDocPendienteEntrega(nuAnn,nuEmi,nuDes);
                           ls.add(doc);
                       }
                    }        
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ls;
    }
    
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String grabarCargoEntrega(TrxGeneraGuiaMpBean trxGuia)throws Exception {
       String vReturn = "NO_OK";
       
        try {
            String pnuAnnGuia=trxGuia.getNuAnnGuia();
            String pnuGuia=trxGuia.getNuGuia();
            String pcoUsuario=trxGuia.getCoUsuario();
            GuiaMesaPartesBean guia=trxGuia.getGuia();
            List<DetGuiaMesaPartesBean> lsDetGuia=trxGuia.getLsDetGuia();
            
            if(pnuAnnGuia!=null&&pnuGuia!=null&&pnuAnnGuia.trim().length()>1&&pnuGuia.trim().length()>1){//UPD
               //en desarrollo
                trxGuia.setAccionBd("UPD");
                if(guia!=null){
                    //verificar si la fecha tiene el formato adecuado.
                    vReturn = Utility.getInstancia().getStringFechaFormat(guia.getFeGuiMp(),"dd/MM/yyyy HH:mm:ss","dd/MM/yyyy HH:mm");
                    if ("NO_OK".equals(vReturn)) {
                        throw new validarDatoException("Error fecha Guía.");
                    }else{
                        guia.setFeGuiMp(vReturn);                                         
                        guia.setCoUseMod(pcoUsuario);
                        vReturn=cargoEntregaDao.updGuiaMp(guia);
                        if(!vReturn.equals("OK")){
                            throw new validarDatoException("Error Actualizando Cabecera de Guía");
                        }
                    }                  
                }
            }else{//INS
                trxGuia.setAccionBd("INS");
                guia.setCoUseMod(pcoUsuario);
                vReturn=cargoEntregaDao.getNroCorrelativoGuiaCabecera(pnuAnnGuia,guia.getCoDepOri());
                if(vReturn.equals("-1")){
                    throw new validarDatoException("Error al obtener número correlativo Guía Cabecera.");
                }else{
                    guia.setNuCorGui(vReturn);
                    vReturn=cargoEntregaDao.getNroGuiaCabecera(pnuAnnGuia);
                    if(vReturn.equals("-1")){
                        throw new validarDatoException("Error al obtener número Guía Cabecera.");
                    }else{
                        guia.setNuGuia(Utility.getInstancia().rellenaCerosIzquierda(vReturn, 10));
                        vReturn=cargoEntregaDao.insGuiaMp(guia);
                        if("OK".equals(vReturn)){
                            pnuGuia=guia.getNuGuia();
                            for (DetGuiaMesaPartesBean detGuia : lsDetGuia) {
                                detGuia.setCoUseMod(pcoUsuario);
                                vReturn=cargoEntregaDao.getNroCorrelativoDetGuiaCabecera(pnuAnnGuia,pnuGuia);
                                if(vReturn.equals("-1")){
                                    throw new validarDatoException("Error al obtener número Correlativo Detalle Guía.");
                                }else{
                                    detGuia.setNuCor(vReturn);
                                    detGuia.setNuAnnGui(pnuAnnGuia);
                                    detGuia.setNuGui(pnuGuia);
                                    vReturn=cargoEntregaDao.insDetGuiaMp(detGuia);
                                    if(!"OK".equals(vReturn)){
                                        throw new validarDatoException("Error Insertando Detalle de Guía");
                                    }
                                }                                
                            }                            
                        }else{
                            throw new validarDatoException("Error Insertando Cabecera de Guía");
                        }                         
                    }
                }
            }
        } catch (validarDatoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR EN TRANSACCION");
        }
       return vReturn;
    }
    
    @Override
    public String getJsonRptGrabarCargoEntrega(TrxGeneraGuiaMpBean trxGuia){
        boolean bandera = false;
        StringBuilder retval = new StringBuilder(); 
        String paccionBD = trxGuia.getAccionBd();
        try {
            GuiaMesaPartesBean guia = trxGuia.getGuia();
            retval.append("\"accionBd\":\"").append(paccionBD).append("\",");
            if(paccionBD.equals("UPD")){
                //en desarrollo.
            }else if(paccionBD.equals("INS")){
                retval.append("\"guiaBean\":{");
                retval.append("\"nuAnn\":\"");
                retval.append(guia.getNuAnn());
                retval.append("\",\"nuCorGuia\":\"");
                retval.append(guia.getNuCorGui());                     
                retval.append("\",\"nuGuia\":\"");
                retval.append(guia.getNuGuia());
                retval.append("\"},");
            }    
            retval.append("\"lstDetGuia\":[");
            for (DetGuiaMesaPartesBean detGuia : trxGuia.getLsDetGuia()) {
                    retval.append("{\"nuAnnGuia\":\"");
                    retval.append(detGuia.getNuAnnGui());
                    retval.append("\",\"nuGuia\":\"");
                    retval.append(detGuia.getNuGui());                
                    retval.append("\",\"nuCorr\":\"");
                    retval.append(detGuia.getNuCor());
                    retval.append("\",\"nuAnn\":\"");
                    retval.append(detGuia.getNuAnn());                        
                    retval.append("\",\"nuEmi\":\"");
                    retval.append(detGuia.getNuEmi());                        
                    retval.append("\",\"nuDes\":\"");
                    retval.append(detGuia.getNuDes());                                            
                    retval.append("\"},"); 
                    bandera = true;
            }
            if (bandera) {
                retval.deleteCharAt(retval.length() - 1);
                bandera = false;
            }                
            retval.append("]");
        } catch (Exception e) {
            e.printStackTrace();
        }
        return retval.toString();
    }

    @Override
    public GuiaMesaPartesBean getGuiaMp(String pnuAnnGuia, String pnuGuia) {
        GuiaMesaPartesBean guia = null;
        try {
            guia = cargoEntregaDao.getGuiaMp(pnuAnnGuia,pnuGuia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return guia;
    }
    
    @Override
    public List<DocPedienteEntregaBean> getDetalleGuiaMp(String pnuAnnGuia,String pnuGuia){
        List<DocPedienteEntregaBean> list = null;
        try {
            list=cargoEntregaDao.getDetalleGuiaMp(pnuAnnGuia, pnuGuia);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public String anularGuiaMp(GuiaMesaPartesBean guia){
        String vReturn = "NO_OK";
        String coUseMod=guia.getCoUseMod();
        try {
            guia=cargoEntregaDao.getGuiaMp(guia.getNuAnn(), guia.getNuGuia());
            if(guia!=null){
                String esGuiaMp=guia.getEstadoGuia();
                if(esGuiaMp!=null&&esGuiaMp.equals("0")){
                    esGuiaMp="9";//guia anulada
                    vReturn=cargoEntregaDao.updEstadoGuiaMp(esGuiaMp, coUseMod, guia.getNuAnn(), guia.getNuGuia());                        
                    if(!vReturn.equals("OK")){
                        vReturn="Error anulando Cargo.";
                    }
                }else{
                    vReturn="Sólo se puede anular los Generados.";
                }
            }else{
                vReturn="Guía no Existe.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String getRutaGuia(String pnuAnnGuia, String pnuGuia, String pcoUser){
        String vResult="NO_OK";
        String nomReporte="TDR161";
        String extArch=".pdf";
        try {
            String urlGuia=",\"noUrl\":\"reporte?coReporte="+nomReporte+"&coParametros=P_NU_ANN_GUIA="+pnuAnnGuia+"|P_NU_GUIA="+pnuGuia+"|P_USER="+pcoUser+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg";            
            urlGuia=",\"noDoc\":\"TEMP|"+pnuAnnGuia+pnuGuia+extArch+"\""+urlGuia;
            vResult=urlGuia;
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return vResult;
    }
    
    @Override
    public GuiaMesaPartesBean getGuiaMp(List<DocPedienteEntregaBean> docs){
        DocPedienteEntregaBean docPedEntrega=null;
        List<DocPedienteEntregaBean> docsAux=null;
        GuiaMesaPartesBean guia=null;
        try {
            docsAux=this.getLsDependenciaDestinoDocExt(docs);
            docPedEntrega = this.esMismaDependenciaDocExt(docsAux);
            if(docPedEntrega!=null){
                guia = new GuiaMesaPartesBean();
                guia.setCoDepDes(docPedEntrega.getCoDepDes());
                guia.setDeDepDes(docPedEntrega.getDeDepDes());
                guia.setCoLocDes(docPedEntrega.getCoLocDes());
                guia.setDeLocDes(docPedEntrega.getDeLocDes());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guia;
    }    
    
    private List<DocPedienteEntregaBean> getLsDependenciaDestinoDocExt(List<DocPedienteEntregaBean> docs){
        List<DocPedienteEntregaBean> docsAux = null;
        try{
            docsAux=new ArrayList<DocPedienteEntregaBean>();
            for (DocPedienteEntregaBean doc : docs) {
                if(doc!=null){
                    DocPedienteEntregaBean docPedEntrega=cargoEntregaDao.getDependenciaDestinoDocExtRec(doc.getNuAnn(), doc.getNuEmi(), doc.getNuDes());                    
                    docsAux.add(docPedEntrega);
                }
            }            
        }catch (Exception e) {
            e.printStackTrace();
        }
        return docsAux;
    }
    
    private DocPedienteEntregaBean esMismaDependenciaDocExt(List<DocPedienteEntregaBean> docs){
        DocPedienteEntregaBean docPedEntrega = null;
        try {
            String coDepDesAnterior=null;
            for (DocPedienteEntregaBean doc : docs) {
                docPedEntrega=doc;
                String coDepDes = doc.getCoDepDes();
                if(coDepDesAnterior!=null){
                    if(!coDepDesAnterior.equals(coDepDes)){
                        docPedEntrega=null;
                        break;
                    }
                }else{
                    coDepDesAnterior=coDepDes;
                }
            }
        } catch (Exception e) {
            docPedEntrega = null;
            e.printStackTrace();
        }
        return docPedEntrega;
    }
    
    @Override
    public List<CargoEntregaBean> getListaReporte(CargoEntregaBean cargo) {
        return cargoEntregaDao.getListaReporteBusqueda(cargo);
    }

    @Override
    public ReporteBean getGenerarReporte(Map parametros, CargoEntregaBean cargo) {
        ReporteBean objReporte = new ReporteBean();
        String eserror = "1"; // si
        String coRespuesta = "";
        String deRespuesta = "";
        String prutaReporte  =  "";
        String deNoDoc = "";
        String nombreArchivo = "";
        String coReporte = "";
        try {          
            
            String extensionArch;
            int tipoArchivo = 0;
            byte[] archivo;
            
            String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());  
            
            List lista = null;
            try {
                lista = getListaReporte(cargo);
                eserror = "0";
            } catch(Exception ex) {               
                eserror = "1";
                deRespuesta = ex.getMessage();
            }
            if (lista.size() == 0) {                 
                 coRespuesta = "1";
                 deRespuesta = "No existe información para generar reporte.";
                 throw new Exception(deRespuesta);
            }
            
            coReporte = "TDR161";
            extensionArch = ".pdf";
            tipoArchivo = 2; // PDF

            String rutaJasper = cargo.getRutaReporteJasper() + "/" + coReporte + ".jasper";
            
            cargo.setRutaReporteJasper(rutaJasper);
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            prutaReporte = "CARENTREGA_" + nombreArchivo + extensionArch;
            deNoDoc = "temp|CARENTREGA_" + fecha + extensionArch;
            
            Utilidades util = new Utilidades();
            archivo = util.GenerarReporteLista(cargo.getRutaReporteJasper(), lista, coReporte, parametros, tipoArchivo);
            
            if (archivo == null) {
                eserror = "1";
                coRespuesta = "1";
                deRespuesta  =  "No se generó correctamente el archivo.";
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
            
            if (eserror.equals("0")) {
                coRespuesta = eserror;
            } else {
                coRespuesta = "1";
            }
            
        } catch (JRException ex) {
            Logger.getLogger(CargoEntregaServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror = "1";
            deRespuesta = ex.getMessage();
        } catch (FileNotFoundException ex) {
            Logger.getLogger(CargoEntregaServiceImp.class.getName()).log(Level.SEVERE, null, ex);
            eserror = "1";
            deRespuesta = ex.getMessage();
        } catch (IOException ex) {
            Logger.getLogger(CargoEntregaServiceImp.class.getName()).log(Level.SEVERE, null, ex);
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
