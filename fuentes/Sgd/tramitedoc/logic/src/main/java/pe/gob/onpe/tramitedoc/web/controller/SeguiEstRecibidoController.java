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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstRecBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepSeguiBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.service.SeguiEstRecibidoService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author NGilt
 */
@Controller
@RequestMapping("/{version}/srSeguiEstRecep.do")
public class SeguiEstRecibidoController {

    @Autowired
    private ReferencedData referencedData;

    @Autowired
    private CommonQryService commonQryService;

    @Autowired
    private SeguiEstRecibidoService seguiEstRecibidoService;
    
    @Autowired
    private ApplicationProperties applicationProperties;

    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model) {
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean = new BuscarDocumentoSeguiEstRecBean();
        buscarDocumentoSeguiEstRecBean.setCoAnnio(annio);
        buscarDocumentoSeguiEstRecBean.setBusCoAnnio(annio);
        buscarDocumentoSeguiEstRecBean.setCoDependencia(codDependencia);
        buscarDocumentoSeguiEstRecBean.setCoDepDestino(codDependencia);
        buscarDocumentoSeguiEstRecBean.setCoVencimiento("3");
        //buscarDocumentoSeguiEstRecBean.setEsIncluyeFiltro(true);
        model.addAttribute(buscarDocumentoSeguiEstRecBean);
        model.addAttribute("deAnnioList", referencedData.getAnnioList());
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoRec("TDTV_DESTINOS"));
        model.addAttribute("dePrioridadesList", referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList", referencedData.getTipoDocumentoEmiList(codDependencia));
        //model.addAttribute("deExpedienteList", referencedData.getExpedienteList(codDependencia));
        model.addAttribute("deEtiquetasList", referencedData.getEtiquetasList());
        model.addAttribute("deVencimientoList", referencedData.getVencimientoList());
        model.addAttribute("deDepenciaActual", commonQryService.getDependenciaxCoDependencia(codDependencia).getDeDependencia());
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        return "/segEstadoRecep/segEstadoRecepDoc";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean, HttpServletRequest request, BindingResult result, Model model) {
        String mensaje = "NO_OK";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiConsulta();
        buscarDocumentoSeguiEstRecBean.setCoDependencia(codDependencia);
        buscarDocumentoSeguiEstRecBean.setCoEmpleado(codEmpleado);
        buscarDocumentoSeguiEstRecBean.setTiAcceso(tipAcceso);

        List list = null;

        try {
            //DocumentoSeguiEstRecibidoBean
            list = seguiEstRecibidoService.getListDocSeguiEstRec(buscarDocumentoSeguiEstRecBean);
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }

        if (list != null) {
            if (list.size() >= 300) {
                model.addAttribute("msjEmision", "OPTIMICE SU CONSULTA");
            }
            model.addAttribute("recepDocumAdmList", list);
            mensaje = "OK";
        } else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/segEstadoRecep/tblSeguimientoEstRecep";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDocumentoEnReferencia")
    public String goBuscaDocumentoEnReferencia(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean,HttpServletRequest request, Model model){
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoSeguiEstRecBean.setCoDependencia(usuario.getCoDep());
        buscarDocumentoSeguiEstRecBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoSeguiEstRecBean.setTiAcceso(usuarioConfigBean.getTiAcceso());
        
        //buscarDocumentoEmiBean.setsTipoBusqueda("2");
        //ServletUtility.getInstancia().saveSessionAttribute(request, "buscarDocumentoEmiBeanAux", buscarDocumentoEmiBean);
//        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
//        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        String mensaje ="NO_OK";
        HashMap mp = null;
        List list = null;
        
        try{
            mp = seguiEstRecibidoService.getDocumentosEnReferencia(buscarDocumentoSeguiEstRecBean);
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
//            return "/consultaEmiDoc/tblDocEmitidos";
            return "/segEstadoRecep/tblSeguimientoEstRecep";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }      
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetalleDocumento")
    public String goDetalleDocumento(HttpServletRequest request, Model model) {
        String mensaje = "NO_OK";
        String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        String snuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");
        String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
        DocumentoRecepSeguiBean documentoRecepSeguiBean;
        List list;
        try {
            documentoRecepSeguiBean = seguiEstRecibidoService.getDocumentoRecepAdmSegui(snuAnn, snuEmi, snuDes);
            list = seguiEstRecibidoService.getDocumentosRefRecepAdmSegui(snuAnn, snuEmi);
            model.addAttribute("pfechaHoraActual", fechaHoraActual);
            model.addAttribute("documentoRecepConsulBean", documentoRecepSeguiBean);
            model.addAttribute("refRecepDocumAdmList", list);
            mensaje = "OK";
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        if (mensaje.equals("OK")) {
            return "/segEstadoRecep/detRecepDocumAdm";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model) {
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        model.addAttribute("listaDestinatario", commonQryService.getListRemitente(pcoDepen));
        return "/modalGeneral/consultaDestinatarioRecepSegui";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaEmpDestino")
    public String goBuscaEmpDestino(HttpServletRequest request, Model model) {
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        model.addAttribute("listaEmpleado", referencedData.getListEmpleadoElaboradoPor(pcoDepen, usuarioConfigBean.getTiConsulta(), usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaEmpDestinoRecepSegui";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaReporte")
    public @ResponseBody
    String goRutaReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean, HttpServletRequest request, BindingResult result, Model model) {
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta = "";
        String deNoDoc;
        String extensionArch;

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoSeguiEstRecBean.setCoDependencia(usuario.getCoDep());
        buscarDocumentoSeguiEstRecBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoSeguiEstRecBean.setTiAcceso(usuarioConfigBean.getTiConsulta());
        //setter
        String pcoReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        buscarDocumentoSeguiEstRecBean.setTipoBusqueda("0");
        buscarDocumentoSeguiEstRecBean.setEsIncluyeFiltro(false);
        String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());
        if (pcoReporte.equalsIgnoreCase("PDF")) {
            pcoReporte = "TDR391";
            extensionArch = ".pdf";
        } else {
            pcoReporte = "TDR391_XLS";
            extensionArch = ".xls";
        }
        deNoDoc = "temp|SEGUIMIENTO" + fecha + extensionArch;

        String prutaReporte = "reporte?coReporte=" + pcoReporte + "&coParametros=P_USER=" + usuario.getCoUsuario()
                + "|P_DE_DEPENDENCIA=" + usuarioConfigBean.getDeDep() + "|P_WHERE=";
        String prutaReporteAux = "";
        String eserror = "1";//si
        try {
            prutaReporteAux = seguiEstRecibidoService.getRutaReporte(buscarDocumentoSeguiEstRecBean);
            eserror = prutaReporteAux.substring(0, 1);
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }

        if (eserror.equals("0")) {
            coRespuesta = eserror;
            prutaReporte += prutaReporteAux.substring(1) + "&coImagenes=P_LOGO_DIR=logo_onpe.jpg";
        } else {
            coRespuesta = "1";
            deRespuesta = prutaReporteAux.substring(1);
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

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDependenciaEmite")
    public String goBuscaDependenciaEmite(HttpServletRequest request, Model model) {
        String pdeDepEmite = ServletUtility.getInstancia().loadRequestParameter(request, "pdeDepEmite");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String vResult = "";
        String coRespuesta;
        StringBuilder retval = new StringBuilder();
        HashMap map;

        try {
            map = seguiEstRecibidoService.getBuscaDependenciaEmite(usuarioConfigBean.getCoDep(), pdeDepEmite);
            String vReturn = (String) map.get("vReturn");
            if (vReturn.equals("1")) {
                model.addAttribute("listaDependenciaDestEmi", map.get("listaDestinatario"));
                vResult = "/modalGeneral/consultaDependenciaRecepSeguiEst";
            } else if (vReturn.equals("0")) {
                DependenciaBean dependenciaBean = (DependenciaBean) map.get("objDestinatario");
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
            } else {
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"deRespuesta\":\"");
                retval.append("Error: No se puede obtener Dependencia.");
                retval.append("\"}");
                model.addAttribute("pMensaje", retval.toString());
                vResult = "respuesta";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return vResult;
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goExportarArchivoLista")
    public @ResponseBody String goExportarArchivoLista(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean,HttpServletRequest request,
            BindingResult result, Model model) throws IOException
    {
        ServletContext sc = request.getSession().getServletContext();
        
        String coReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiConsulta();
        
        String rutaReporte=sc.getRealPath("/reports/");
//        String rutaImagen = sc.getRealPath("/reports/logo_onpe.jpg");
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        Map parametros = new HashMap();
        parametros.put("P_DE_DEPENDENCIA", usuarioConfigBean.getDeDep());
        parametros.put("P_USER", usuario.getCoUsuario());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        
        buscarDocumentoSeguiEstRecBean.setCoDependencia(codDependencia);
        buscarDocumentoSeguiEstRecBean.setCoEmpleado(codEmpleado);
        buscarDocumentoSeguiEstRecBean.setTiAcceso(tipAcceso);        
        buscarDocumentoSeguiEstRecBean.setRutaReporteJasper(rutaReporte);
        buscarDocumentoSeguiEstRecBean.setFormatoReporte(coReporte);
        
        ReporteBean objReporteBean = seguiEstRecibidoService.getGenerarReporte(buscarDocumentoSeguiEstRecBean, parametros);
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
