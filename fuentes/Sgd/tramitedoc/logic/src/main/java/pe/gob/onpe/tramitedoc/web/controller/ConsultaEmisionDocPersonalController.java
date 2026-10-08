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
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiPersConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.ConsultaEmiDocPerService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Controller
@RequestMapping("/{version}/srConsultaEmiDocPersonal.do")
public class ConsultaEmisionDocPersonalController {
    
    @Autowired
    private ReferencedData referencedData;    
    
    @Autowired
    private ConsultaEmiDocPerService consultaDocPersonalSrv;      
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        //String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());    
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        DocumentoEmiPersConsulBean buscarDocPer = new DocumentoEmiPersConsulBean();
        
        model.addAttribute("buscarConsulDocPersEmiBean",buscarDocPer);
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));        
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));                
        return "/consultaEmiDocPersonal/consultaEmiDocPersonal";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(DocumentoEmiPersConsulBean buscarDocPer, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        buscarDocPer.setCoEmpleado(usuario.getCempCodemp());
        
        List list = null;

        try{
            list = consultaDocPersonalSrv.getDocsPersConsulta(buscarDocPer);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=200) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }         
            model.addAttribute("emiDocumAdmList",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/consultaEmiDocPersonal/tblDocEmitidos";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }        
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetDocumentoPers")
    public String goDetDocumentoPers(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       
       //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       //String codDependencia = usuario.getCoDep();
       DocumentoEmiPersConsulBean docPersConsulBean;
       List lsReferenciaDocPersEmi;
       List lsDestinoDocPersEmi;
       
       try{
           docPersConsulBean=consultaDocPersonalSrv.getDocumentoPersonalEmi(snuAnn, snuEmi);
           lsReferenciaDocPersEmi=consultaDocPersonalSrv.getLstDocumReferenciatblEmi(snuAnn, snuEmi);
           lsDestinoDocPersEmi=consultaDocPersonalSrv.getLstDestintariotlbEmi(snuAnn, snuEmi);
           
           model.addAttribute("lstReferenciaDocAdmEmi",lsReferenciaDocPersEmi);
           model.addAttribute("lstDestintarioDocAdmEmi",lsDestinoDocPersEmi);
           model.addAttribute("documentoEmiPersConsulBean",docPersConsulBean);
           mensaje = "OK";
       }catch(Exception ex){
          ex.printStackTrace(); 
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/consultaEmiDocPersonal/detDocPersonalEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm","5");
        model.addAttribute("listaDestinatario",referencedData.getListDestinatarioEmiPersonal(usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaDestinatarioEmiConsul";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaReferenciaOrigen")
    public String goBuscaReferenciaOrigen(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm","2");
        model.addAttribute("listaReferenOrig",referencedData.getListReferenciaOrigenPersonal(usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaReferenciaOrigenConsul";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaReporte")
    public @ResponseBody String goRutaReporte(DocumentoEmiPersConsulBean buscarDocPer, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta="";
        String deNoDoc;
        String extensionArch;

        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocPer.setCoEmpleado(usuarioConfigBean.getCempCodemp());
        //setter
        String pcoReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        buscarDocPer.setTipoBusqueda("0");
        buscarDocPer.setEsIncluyeFiltro(false);
        String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());
        if(pcoReporte.equalsIgnoreCase("PDF")){
            pcoReporte = "TDR31";
            extensionArch=".pdf";
        }else{
            pcoReporte = "TDR31_XLS";
            extensionArch=".xls";
        }
        deNoDoc = "temp|EMISIONPERS_"+fecha+extensionArch;
        
        String prutaReporte="reporte?coReporte="+pcoReporte+"&coParametros=P_USER="+usuarioConfigBean.getCoUsuario()+
                            "|P_DE_DEPENDENCIA="+usuarioConfigBean.getDeDep()+"|P_WHERE=";
        String prutaReporteAux="";
        String eserror="1";//si
        try{
            prutaReporteAux = consultaDocPersonalSrv.getRutaReporte(buscarDocPer);
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

    @RequestMapping(method = RequestMethod.POST, params = "accion=goExportarArchivoLista")
    public @ResponseBody String goExportarArchivoLista(DocumentoEmiPersConsulBean buscarDocPer,HttpServletRequest request,
            BindingResult result, Model model) throws IOException
    {
        ServletContext sc = request.getSession().getServletContext();
        String coReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiConsulta();
        String anio = ServletUtility.getInstancia().loadRequestParameter(request, "coAnnio");
        
        String rutaReporte=sc.getRealPath("/reports/");
//        String rutaImagen = sc.getRealPath("/reports/logo_onpe.jpg");
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        Map parametros = new HashMap();
        parametros.put("P_DE_DEPENDENCIA", usuarioConfigBean.getDeDep());
        parametros.put("P_USER", usuario.getCoUsuario());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        
        buscarDocPer.setCoEmpleado(usuarioConfigBean.getCempCodemp());
        buscarDocPer.setRutaReporteJasper(rutaReporte);
        buscarDocPer.setFormatoReporte(coReporte);
        
        ReporteBean objReporteBean = consultaDocPersonalSrv.getGenerarReporte(buscarDocPer, parametros);
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
