/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.model.Usuario; 
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoCargaMsjBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepMensajeriaBean; 
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepMensajeriaBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.TipoElementoMensajeriaBean;
import pe.gob.onpe.tramitedoc.service.DocumentoMensajeriaService;
import pe.gob.onpe.tramitedoc.service.MensajesData;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
/**
 *
 * @author wcondori
 */
@Controller
@RequestMapping("/{version}/srMensajeriaGestion.do")
public class MensajeriaController {
    @Autowired
    private ReferencedData referencedData;
    @Autowired
    private DocumentoMensajeriaService documentoMensajeriaService;
      @Autowired
  private ApplicationProperties applicationProperties;
      @Autowired
  private MensajesData mensajesData;
     @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        
        String sCoAnnio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        BuscarDocumentoRecepMensajeriaBean buscarDocumentoRecepMensajeriaBean = new BuscarDocumentoRecepMensajeriaBean();
        buscarDocumentoRecepMensajeriaBean.setCoAnnio(sCoAnnio);
        buscarDocumentoRecepMensajeriaBean.setCoEstadoDoc("0");//en registro
        buscarDocumentoRecepMensajeriaBean.setCoDependencia(codDependencia);
        model.addAttribute(buscarDocumentoRecepMensajeriaBean);
        
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));           
        model.addAttribute("deEstadosList",referencedData.getLstEstadoCargoEntrega("ENVIO_DOCUMENTO_MSJ"));
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date())); 
        model.addAttribute("deDependenciaList",mensajesData.getListOficina());   
         return "/mpRecepMensajeria/buscarMensajeria";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoRecepMensajeriaBean buscarDocumentoRecepMensajeriaBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
         List list = null;
        try{
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuarioConfigBean.getCoDep();
        String codEmpleado = usuarioConfigBean.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiAccesoMp();
        buscarDocumentoRecepMensajeriaBean.setCoDependencia(codDependencia);
        buscarDocumentoRecepMensajeriaBean.setCoEmpleado(codEmpleado);
        buscarDocumentoRecepMensajeriaBean.setTiAcceso(tipAcceso);
        buscarDocumentoRecepMensajeriaBean.setCoDepEmi(usuarioConfigBean.getCoDepMp());
        buscarDocumentoRecepMensajeriaBean.setInMesaPartes(usuarioConfigBean.getInMesaPartes());
         
            list = documentoMensajeriaService.getDocumentoRecepMensajeria(buscarDocumentoRecepMensajeriaBean);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=50) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            model.addAttribute("docMensajRecepLst",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/mpRecepMensajeria/tblDocMensajeriaRecep";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEnviarDocMensajeria")
    public String goEnviarDocMensajeria(HttpServletRequest request, Model model){
        String mensaje = "NO_OK"; 
       String codigos = ServletUtility.getInstancia().loadRequestParameter(request, "codigos");
       
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date()); 
       DocumentoRecepMensajeriaBean documentoRecepMensajeriaBean = new DocumentoRecepMensajeriaBean();
       List listAmbito,listTipoEnvio,listTipoMensajero,listDestintarioEnvMensaj; 
       
       try{ 
           codigos ="'"+codigos.replace(",","','")+"'";                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                                   
           listDestintarioEnvMensaj = documentoMensajeriaService.getLstDestinoEnvMensajeria(codigos);
           listAmbito = documentoMensajeriaService.getlistTipoElementoMensajeria("DE_AMBITO_MENS");
           listTipoMensajero = documentoMensajeriaService.getlistTipoElementoMensajeria("DE_TIP_MSJ_MENS");
           listTipoEnvio = documentoMensajeriaService.getlistTipoElementoMensajeria("RE_ENV_MSJ_MENS");
           model.addAttribute("lstDestintarioDocMensajeria",listDestintarioEnvMensaj);
           model.addAttribute("listAmbito",listAmbito);
           model.addAttribute("listTipoMensajero",listTipoMensajero);
           model.addAttribute("listTipoEnvio",listTipoEnvio);
           model.addAttribute(documentoRecepMensajeriaBean);

           model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
           model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));              
           model.addAttribute("horaActual", Utility.getInstancia().dateToFormatStringHHmm(new Date()));     
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
           model.addAttribute("fileSizeMaxCargo",applicationProperties.getFileSizeMaxCargo());
            
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/mpRecepMensajeria/enviarDocMensajeria";           
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
     @RequestMapping(method = RequestMethod.POST, params = "accion=goListaDocResposable")
    public String goListaDocResposable(HttpServletRequest request, Model model) {
        String mensaje = "NO_OK";
        String vcoTipMensajero = ServletUtility.getInstancia().loadRequestParameter(request, "vcoTipMensajero");
        String vcoTipoAmbito = ServletUtility.getInstancia().loadRequestParameter(request, "vcoTipoAmbito");
        String vcoTipoEnvio = ServletUtility.getInstancia().loadRequestParameter(request, "vcoTipoEnvio");
        
        try {
            List<TipoElementoMensajeriaBean> docList = documentoMensajeriaService.getListResponsableMensajeria(vcoTipMensajero,vcoTipoAmbito,vcoTipoEnvio);
            model.addAttribute("listResponsable", docList);
            mensaje = "OK";
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        return "/mpRecepMensajeria/motResponsableEnvioMensajeria";
    }
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goGrabarDocumentoMensajeria",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goGrabarDocumentoMensajeria(@RequestBody DocumentoRecepMensajeriaBean documentoRecepMensajeriaBean,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta;
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        documentoRecepMensajeriaBean.setCousecre(usuarioConfigBean.getCoUsuario());         
        try{
            documentoRecepMensajeriaBean.setCodigo("'"+documentoRecepMensajeriaBean.getCodigo().replace(",","','")+"'");       
            mensaje = documentoMensajeriaService.insMensajeriaDocumento(documentoRecepMensajeriaBean);             
        }catch (Exception e) { 
           e.printStackTrace(); 
        } 
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }  
        deRespuesta = mensaje;
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta);
        retval.append("\",\"deNro\":\"");
        retval.append(documentoRecepMensajeriaBean.getNumsj());
        retval.append("\",\"usuario\":\"");
        retval.append(usuarioConfigBean.getCoUsuario());
        retval.append("\",\"fecha\":\"");
        retval.append(documentoRecepMensajeriaBean.getFeregmsj());  
        retval.append("\""); 
        retval.append("}");

        return retval.toString();        
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRecibirDocumentoMensajeria",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goRecibirDocumentoMensajeria(@RequestBody String codigos,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta;
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request); 
        String resultJsonStr="";
        try{
            codigos="'"+codigos.replace(",","','")+"'";       
            mensaje = documentoMensajeriaService.updMensajeriaDocumentoRecibir(codigos);             
        }catch (Exception e) { 
           e.printStackTrace(); 
        } 
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }  
        deRespuesta = mensaje;
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta);       
        retval.append("\"");
        retval.append(resultJsonStr); 
        retval.append("}");

        return retval.toString();        
    }
    
     @RequestMapping(method = RequestMethod.POST, params = "accion=goDevolverDocumentoMensajeria",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goDevolverDocumentoMensajeria(@RequestBody String codigos,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta; 
        String resultJsonStr="";
        try{
            codigos="'"+codigos.replace(",","','")+"'";       
            mensaje = documentoMensajeriaService.updMensajeriaDocumentoDevolver(codigos);             
        }catch (Exception e) { 
           e.printStackTrace(); 
        } 
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }  
        deRespuesta = mensaje;
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta);       
        retval.append("\"");
        retval.append(resultJsonStr); 
        retval.append("}");

        return retval.toString();        
    }
   
    
     @RequestMapping(method = RequestMethod.POST, params = "accion=goCalcularFechaPlazo",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goCalcularFechaPlazo(@RequestBody DocumentoRecepMensajeriaBean documentoRecepMensajeriaBean,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";    
       String coRespuesta; 
        
       try{ 
           mensaje = documentoMensajeriaService.selectCalcularFechaPlazo(documentoRecepMensajeriaBean);            
           
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }    
        if(documentoRecepMensajeriaBean.getCalculaPenalizacion()=="1" && documentoRecepMensajeriaBean.getDiasEntrega()=="-1"){
            coRespuesta = "0";
            documentoRecepMensajeriaBean.setFeVence("No tiene configurado los parametros de valores(Dias Entrega|Días Devolución).");
        }
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(documentoRecepMensajeriaBean.getFeVence());       
        retval.append("\",\"diasEntrega\":\"");
        retval.append(documentoRecepMensajeriaBean.getDiasEntrega());       
        retval.append("\",\"diasDevolucion\":\"");
        retval.append(documentoRecepMensajeriaBean.getDiasDevoluvion());
        retval.append("\",\"getCalculaPenalizacion\":\"");
        retval.append(documentoRecepMensajeriaBean.getCalculaPenalizacion());  
        retval.append("\"");
        retval.append(""); 
        retval.append("}");
        String datos="";
        return retval.toString(); 
        
    }
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goImprimirDocumento")
    public @ResponseBody String goImprimirDocumento(BuscarDocumentoRecepMensajeriaBean buscarDocumentoCargaMsjBean,HttpServletRequest request,
            BindingResult result, Model model) throws IOException
    {
        ServletContext sc = request.getSession().getServletContext();
        
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiConsulta();
        String anio = ServletUtility.getInstancia().loadRequestParameter(request, "coAnnio");
        String rutaReporte=sc.getRealPath("/reports/"); 
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        String coReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        String nrodocumento = ServletUtility.getInstancia().loadRequestParameter(request, "nrodocumento");
        String responsable = ServletUtility.getInstancia().loadRequestParameter(request, "responsable");
        Map parametros = new HashMap();
        parametros.put("P_COURRIER", responsable);
        parametros.put("P_USER", usuario.getDeFullName());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        if(buscarDocumentoCargaMsjBean== null) buscarDocumentoCargaMsjBean= new BuscarDocumentoRecepMensajeriaBean();
        
        buscarDocumentoCargaMsjBean.setBusNumDoc(nrodocumento);
        buscarDocumentoCargaMsjBean.setCoDependencia(codDependencia);
        buscarDocumentoCargaMsjBean.setCoEmpleado(codEmpleado);
        buscarDocumentoCargaMsjBean.setTiAcceso(tipAcceso);
        buscarDocumentoCargaMsjBean.setCoAnnio(anio);    
        buscarDocumentoCargaMsjBean.setRutaReporteJasper(rutaReporte);
        buscarDocumentoCargaMsjBean.setFormatoReporte(coReporte);
        
        ReporteBean objReporteBean = documentoMensajeriaService.getImprimirReporte(buscarDocumentoCargaMsjBean, parametros);
        if(bis!=null)bis.close();
        
        StringBuilder retval = new StringBuilder();
        
        retval.append("{\"coRespuesta\":\"");        
        retval.append(objReporteBean.getcoRespuesta());
        retval.append("\",\"deRespuesta\":\"");        
        retval.append(objReporteBean.getdeRespuesta());
        retval.append("\",\"noUrl\":\"");
        retval.append(objReporteBean.getnoUrl());
        retval.append("\",\"noDoc\":\"");
        retval.append(objReporteBean.getnoDoc());
        retval.append("\"}");
        
        return retval.toString();
    }
}
