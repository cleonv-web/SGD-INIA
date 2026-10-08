/*
 * To change this template, choose Tools | Templates
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
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.ConsultaRecepDocService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoAdmService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author NGilt
 */
@Controller
@RequestMapping("/{version}/srConsultaRecepcionDoc.do")
public class ConsultaRecepcionDocController {
    
    @Autowired
    private CommonQryService commonQryService;     
    
    @Autowired
    private ReferencedData referencedData;    
    
    @Autowired
    private ConsultaRecepDocService consultaRecepDocService;    

    @Autowired
    private EmiDocumentoAdmService emiDocumentoAdmService;    
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
//        String pEstado = ServletUtility.getInstancia().loadRequestParameter(request, "estadoDoc");//'0' documento no leido
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean = new BuscarDocumentoRecepConsulBean();
        
        buscarDocumentoRecepConsulBean.setCoAnnio(annio);
        buscarDocumentoRecepConsulBean.setBusCoAnnio(annio);
//        buscarDocumentoRecepConsulBean.setEstadoDoc(pEstado);
        buscarDocumentoRecepConsulBean.setCoDependencia(codDependencia);
        buscarDocumentoRecepConsulBean.setCoDepDestino(codDependencia);
        //buscarDocumentoRecepConsulBean.setEsIncluyeFiltro(true);     
//        consultaRecepDocService.estadoRecepcionDocumento(buscarDocumentoRecepConsulBean,"");
        model.addAttribute(buscarDocumentoRecepConsulBean);
        model.addAttribute("deAnnioList",referencedData.getAnnioList());
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoRec("TDTV_DESTINOS"));
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoList(codDependencia));
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(codDependencia));
        model.addAttribute("deEtiquetasList",referencedData.getEtiquetasList());
        model.addAttribute("deDepenciaActual",commonQryService.getDependenciaxCoDependencia(codDependencia).getDeDependencia());
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        return "/consultaRecepDoc/consultaRecepDoc";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoRecepConsulBean.setCoDependencia(usuario.getCoDep());
        buscarDocumentoRecepConsulBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoRecepConsulBean.setTiAcceso(usuarioConfigBean.getTiConsulta());
        
        List list = null;

        try{
                list = consultaRecepDocService.getDocumentosBuscaRecepAdm(buscarDocumentoRecepConsulBean);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=300) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            
            model.addAttribute("recepDocumAdmList",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/consultaRecepDoc/tblDocRecepcionados";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetalleDocumento")
    public String goDetalleDocumento(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       String snuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");        
       //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       DocumentoRecepConsulBean documentoRecepConsulBean;
       List list;
       try{
           documentoRecepConsulBean = consultaRecepDocService.getDocumentoRecepAdm(snuAnn, snuEmi, snuDes);
           list = consultaRecepDocService.getDocumentosRefRecepAdm(snuAnn, snuEmi);
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
//           model.addAttribute("pcodEmp",usuario.getCempCodemp());
//           model.addAttribute("pdesEmp",usuario.getDeFullName());
           model.addAttribute("documentoRecepConsulBean",documentoRecepConsulBean);
           model.addAttribute("refRecepDocumAdmList",list);
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/consultaRecepDoc/detRecepDocumAdm";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDocumentoEnReferencia")
    public String goBuscaDocumentoEnReferencia(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoRecepConsulBean.setCoDependencia(usuario.getCoDep());
        buscarDocumentoRecepConsulBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoRecepConsulBean.setTiAcceso(usuarioConfigBean.getTiAcceso());

        HashMap mp = null;
        List list = null;
        
        try{
            mp = consultaRecepDocService.getDocumentosEnReferencia(buscarDocumentoRecepConsulBean);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        } 
        mensaje = String.valueOf(mp.get("msjResult"));
        if(mensaje.equals("1")){
            list = (List) mp.get("recepDocumAdmList");
            if(list!=null){
                if (list.size()>=300){
                    model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
                }
                model.addAttribute("recepDocumAdmList",list);
                mensaje = "OK";                
            }
        }
        if (mensaje.equals("OK")) {
            return "/consultaRecepDoc/tblDocRecepcionados";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }          
     }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDependenciaEmite")
    public String goBuscaDependenciaEmite(HttpServletRequest request, Model model){
        String pdeDepEmite = ServletUtility.getInstancia().loadRequestParameter(request, "pdeDepEmite");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String vResult="";
        String coRespuesta;
        StringBuilder retval = new StringBuilder();
        HashMap map;
        
        try{
            map = emiDocumentoAdmService.getBuscaDependenciaEmite(usuarioConfigBean.getCoDep(),pdeDepEmite);
            String vReturn = (String)map.get("vReturn");
            if(vReturn.equals("1")){
                model.addAttribute("listaDependenciaDestEmi",map.get("listaDestinatario"));
                vResult = "/modalGeneral/consultaDependenciaRecepConsul";
            }else if(vReturn.equals("0")){
                DependenciaBean dependenciaBean = (DependenciaBean)map.get("objDestinatario");
                coRespuesta = "1";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"coDependencia\":\"");
                retval.append(dependenciaBean.getCoDependencia());                
                retval.append("\",\"deDependencia\":\"");
                retval.append(dependenciaBean.getDeDependencia());                
                retval.append("\"}"); 
                model.addAttribute("pMensaje", retval.toString());
                vResult = "respuesta";
            }else{
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"deRespuesta\":\"");
                retval.append("Error: No se puede obtener Dependencia.");                
                retval.append("\"}"); 
                model.addAttribute("pMensaje", retval.toString());
                vResult = "respuesta";
            }            
        }catch(Exception ex){
            ex.printStackTrace();
        }        
        return vResult;        
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaRemitente")
    public String goBuscaRemitente(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("listaRemitente",referencedData.getListRemitente(pnuAnn,usuarioConfigBean));
        return "/modalGeneral/consultaRemitenteRecepConsul";
    } 

        @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model){
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        model.addAttribute("listaDestinatario",commonQryService.getListRemitente(pcoDepen));    
        return "/modalGeneral/consultaDestinatarioRecepConsul";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaReporte")
    public @ResponseBody String goRutaReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta="";
        String deNoDoc;
        String extensionArch;

        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoRecepConsulBean.setCoDependencia(usuario.getCoDep());
        buscarDocumentoRecepConsulBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoRecepConsulBean.setTiAcceso(usuarioConfigBean.getTiConsulta());        
        //setter
        String pcoReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        buscarDocumentoRecepConsulBean.setTipoBusqueda("0");
        buscarDocumentoRecepConsulBean.setEsIncluyeFiltro(false);
        String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());
        if(pcoReporte.equalsIgnoreCase("PDF")){
            pcoReporte = "TDR09";
            extensionArch=".pdf";
        }else{
            pcoReporte = "TDR09_XLS";
            extensionArch=".xls";
        }
        deNoDoc = "temp|RECEPCION_"+fecha+extensionArch;
        
        String prutaReporte="reporte?coReporte="+pcoReporte+"&coParametros=P_USER="+usuario.getCoUsuario()+
                            "|P_DE_DEPENDENCIA="+usuarioConfigBean.getDeDep()+"|P_WHERE=";
        String prutaReporteAux="";
        String eserror="1";//si
        try{
            prutaReporteAux = consultaRecepDocService.getRutaReporte(buscarDocumentoRecepConsulBean);
            eserror=prutaReporteAux.substring(0,1);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if(eserror.equals("0")){
            coRespuesta=eserror;
            prutaReporte+=prutaReporteAux.substring(1)+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg";
        }else{
            coRespuesta="1";
            deRespuesta=prutaReporteAux.substring(1);
        }
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta);
        retval.append("\",\"noUrl\":\"");
        retval.append(prutaReporte);  
        retval.append("\",\"noDoc\":\"");
        retval.append(deNoDoc);
        retval.append("\"}");
        
        return retval.toString();
    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaEmpDestino")
    public String goBuscaEmpDestino(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        model.addAttribute("listaEmpleado",referencedData.getListEmpleadoElaboradoPor(pcoDepen,usuarioConfigBean.getTiConsulta(),usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaEmpDestinoRecep";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goExportarArchivoLista")
    public @ResponseBody String goExportarArchivoLista(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean,HttpServletRequest request,
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
//        String rutaImagen = sc.getRealPath("/reports/logo_onpe.jpg");
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        String coReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        
        Map parametros = new HashMap();
        parametros.put("P_DE_DEPENDENCIA", usuarioConfigBean.getDeDep());
        parametros.put("P_USER", usuario.getCoUsuario());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        
        buscarDocumentoRecepConsulBean.setCoDependencia(codDependencia);
        buscarDocumentoRecepConsulBean.setCoEmpleado(codEmpleado);
        buscarDocumentoRecepConsulBean.setTiAcceso(tipAcceso);
        buscarDocumentoRecepConsulBean.setCoAnnio(anio);    
        buscarDocumentoRecepConsulBean.setRutaReporteJasper(rutaReporte);
        buscarDocumentoRecepConsulBean.setFormatoReporte(coReporte);
        
        ReporteBean objReporteBean = consultaRecepDocService.getGenerarReporte(buscarDocumentoRecepConsulBean, parametros);
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
