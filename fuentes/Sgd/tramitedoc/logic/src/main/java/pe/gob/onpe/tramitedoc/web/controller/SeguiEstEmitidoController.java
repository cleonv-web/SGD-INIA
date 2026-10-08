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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstEmiBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiSeguiBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.service.SeguiEstEmitidoService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author NGilt
 */
@Controller
@RequestMapping("/{version}/srSeguiEstEmi.do")
public class SeguiEstEmitidoController {

    @Autowired
    private ReferencedData referencedData;

    @Autowired
    private CommonQryService commonQryService;

    @Autowired
    private SeguiEstEmitidoService seguiEstEmitidoService;
    
    @Autowired
    private ApplicationProperties applicationProperties;

    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model) {
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        String pEstado = ServletUtility.getInstancia().loadRequestParameter(request, "estadoDoc");
        BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean = new BuscarDocumentoSeguiEstEmiBean();
        buscarDocumentoSeguiEstEmiBean.setCoAnnio(annio);
        buscarDocumentoSeguiEstEmiBean.setBusCoAnnio(annio);
        buscarDocumentoSeguiEstEmiBean.setCoDependencia(codDependencia);
        //buscarDocumentoSeguiEstEmiBean.setCoDepDestino(codDependencia);
        //buscarDocumentoSeguiEstEmiBean.setEstadoDoc(pEstado);
        buscarDocumentoSeguiEstEmiBean.setCoDepOrigen(codDependencia);
        buscarDocumentoSeguiEstEmiBean.setCoVencimiento("3");
        //buscarDocumentoSeguiEstEmiBean.setEsIncluyeFiltro(true);
        model.addAttribute(buscarDocumentoSeguiEstEmiBean);
        model.addAttribute("deAnnioList", referencedData.getAnnioList());
        //model.addAttribute("deEstadosList", referencedData.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
        model.addAttribute("deEstadosList", referencedData.getLstEstadosDocumentoEmiSegui("TDTV_DESTINOS"));
        model.addAttribute("dePrioridadesList", referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList", referencedData.getTipoDocumentoEmiList(codDependencia));
        //model.addAttribute("deExpedienteList", referencedData.getExpedienteList(codDependencia));
        model.addAttribute("deEtiquetasList", referencedData.getEtiquetasList());
        model.addAttribute("deVencimientoList", referencedData.getVencimientoList());
        model.addAttribute("deDepenciaActual", commonQryService.getDependenciaxCoDependencia(codDependencia).getDeDependencia());

        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        return "/segEstadoEmi/segEstadoEmiDoc";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean, HttpServletRequest request, BindingResult result, Model model) {
        String mensaje = "NO_OK";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiConsulta();
        buscarDocumentoSeguiEstEmiBean.setCoDependencia(codDependencia);
        buscarDocumentoSeguiEstEmiBean.setCoEmpleado(codEmpleado);
        buscarDocumentoSeguiEstEmiBean.setTiAcceso(tipAcceso);

        List list = null;

        try {

            list = seguiEstEmitidoService.getListDocSeguiEstEmi(buscarDocumentoSeguiEstEmiBean);
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
            return "/segEstadoEmi/tblSeguimientoEstEmi";
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
        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
        DocumentoEmiSeguiBean documentoEmiSeguiBean;
        List list;
        try {
            documentoEmiSeguiBean = seguiEstEmitidoService.getDocumentoEmiSeguiBean(snuAnn, snuEmi, snuDes);
            list = seguiEstEmitidoService.getDocumentosRefEmiAdmSegui(snuAnn, snuEmi);
            model.addAttribute("pfechaHoraActual", fechaHoraActual);
//           model.addAttribute("pcodEmp",usuario.getCempCodemp());
//           model.addAttribute("pdesEmp",usuario.getDeFullName());
            model.addAttribute("documentoEmiSeguiBean", documentoEmiSeguiBean);
            model.addAttribute("refRecepDocumAdmList", list);
            mensaje = "OK";
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        if (mensaje.equals("OK")) {
            return "/segEstadoEmi/detEmiDocumAdm";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model) {
//        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
//        model.addAttribute("listaDestinatario", commonQryService.getListRemitente(pcoDepen));
//        return "/modalGeneral/consultaDestinatarioEmiSegui";
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm", "1");
        model.addAttribute("listaDestinatario", referencedData.getListDestinatarioEmi(pnuAnn, usuarioConfigBean));
        return "/modalGeneral/consultaDestinatarioEmiConsulSeguiEst";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaEmpDestino")
    public String goBuscaEmpDestino(HttpServletRequest request, Model model) {
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        return "/modalGeneral/consultaEmpDestinoRecepSegui";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaReporte")
    public @ResponseBody
    String goRutaReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean, HttpServletRequest request, BindingResult result, Model model) {
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta = "";
        String deNoDoc;
        String extensionArch;

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoSeguiEstEmiBean.setCoDependencia(usuario.getCoDep());
        buscarDocumentoSeguiEstEmiBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoSeguiEstEmiBean.setTiAcceso(usuarioConfigBean.getTiConsulta());
        //setter
        String pcoReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        buscarDocumentoSeguiEstEmiBean.setTipoBusqueda("0");
        buscarDocumentoSeguiEstEmiBean.setEsIncluyeFiltro(false);
        String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());
        if (pcoReporte.equalsIgnoreCase("PDF")) {
            pcoReporte = "TDR393";
            extensionArch = ".pdf";
        } else {
            pcoReporte = "TDR393_XLS";
            extensionArch = ".xls";
        }
        deNoDoc = "temp|SEGUIMIENTOEMITIDO" + fecha + extensionArch;

        String prutaReporte = "reporte?coReporte=" + pcoReporte + "&coParametros=P_USER=" + usuario.getCoUsuario()
                + "|P_DE_DEPENDENCIA=" + usuarioConfigBean.getDeDep() + "|P_WHERE=";
        String prutaReporteAux = "";
        String eserror = "1";//si
        try {
            prutaReporteAux = seguiEstEmitidoService.getRutaReporte(buscarDocumentoSeguiEstEmiBean);
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

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaReferenciaOrigen")
    public String goBuscaReferenciaOrigen(HttpServletRequest request, Model model) {
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm", "1");
        model.addAttribute("listaReferenOrig", referencedData.getListReferenciaOrigen(pnuAnn, usuarioConfigBean));
        return "/modalGeneral/consultaReferenciaOrigenConsulSeguiEstEmi";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaElaboradoPor")
    public String goBuscaElaboradoPor(HttpServletRequest request, Model model) {
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("listaEmpleado", referencedData.getListEmpleadoElaboradoPor(pcoDep, usuarioConfigBean.getTiConsulta(), usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaElaboradoPorConsulSeguiEstEmi";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDependenciaOrigen")
    public String goBuscaDependenciaOrigen(HttpServletRequest request, Model model) {
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        model.addAttribute("lstRemitente", commonQryService.getListRemitente(pcoDepen));
        return "/modalGeneral/consultaDependenciaOrigenConsulSeguiEstEmi";
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
            map = seguiEstEmitidoService.getBuscaDependenciaEmite(usuarioConfigBean.getCoDep(),pdeDepEmite);
            String vReturn = (String)map.get("vReturn");
            if(vReturn.equals("1")){
                model.addAttribute("listaDependenciaDestEmi",map.get("listaDestinatario"));
                vResult = "/modalGeneral/consultaDependenciaEmiSeguiEst";
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
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDocumentoEnReferencia")
    public String goBuscaDocumentoEnReferencia(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean,HttpServletRequest request, Model model){
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoSeguiEstEmiBean.setCoDependencia(usuario.getCoDep());
        buscarDocumentoSeguiEstEmiBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoSeguiEstEmiBean.setTiAcceso(usuarioConfigBean.getTiAcceso());
        
        //buscarDocumentoEmiBean.setsTipoBusqueda("2");
        //ServletUtility.getInstancia().saveSessionAttribute(request, "buscarDocumentoEmiBeanAux", buscarDocumentoEmiBean);
//        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
//        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        String mensaje ="NO_OK";
        HashMap mp = null;
        List list = null;
        
        try{
            mp = seguiEstEmitidoService.getDocumentosEnReferencia(buscarDocumentoSeguiEstEmiBean);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        } 
        mensaje = String.valueOf(mp.get("msjResult"));
        if(mensaje.equals("1")){
            list = (List) mp.get("emiDocumAdmList");
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
            return "/segEstadoEmi/tblSeguimientoEstEmi";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }      
    }
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goExportarArchivoLista")
    public @ResponseBody String goExportarArchivoLista(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean,HttpServletRequest request,
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
        
        buscarDocumentoSeguiEstEmiBean.setCoDependencia(codDependencia);
        buscarDocumentoSeguiEstEmiBean.setCoEmpleado(codEmpleado);
        buscarDocumentoSeguiEstEmiBean.setTiAcceso(tipAcceso);        
        buscarDocumentoSeguiEstEmiBean.setRutaReporteJasper(rutaReporte);
        buscarDocumentoSeguiEstEmiBean.setFormatoReporte(coReporte);
        
        ReporteBean objReporteBean = seguiEstEmitidoService.getGenerarReporte(buscarDocumentoSeguiEstEmiBean, parametros);
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
