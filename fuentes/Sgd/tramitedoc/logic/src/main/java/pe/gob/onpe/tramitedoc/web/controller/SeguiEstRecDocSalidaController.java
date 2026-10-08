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
import pe.gob.onpe.tramitedoc.bean.DocExtRecSeguiEstBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.service.SeguiEstRecDocExtService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
/**
 *
 * @author oti2
 */
@Controller
@RequestMapping("/{version}/srSeguiEstRecDocSalida.do")
public class SeguiEstRecDocSalidaController {
    @Autowired
    private ReferencedData referencedData;
    
    @Autowired
    private SeguiEstRecDocExtService seguiEstRecDocExtService;    
    
    @Autowired
    private CommonQryService commonQryService;      
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
      @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model) {
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        if(codDependencia!=null&&codDependencia.trim().length()>0){
            String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
            DocExtRecSeguiEstBean buscarDocExtRecSeguiEstBean = new DocExtRecSeguiEstBean();
            buscarDocExtRecSeguiEstBean.setNuAnn(annio);
            buscarDocExtRecSeguiEstBean.setCoDepEmi(codDependencia);
            buscarDocExtRecSeguiEstBean.setDeDepEmi(commonQryService.getDependenciaxCoDependencia(codDependencia).getDeDependencia());
            buscarDocExtRecSeguiEstBean.setCoEstVen("3");

            model.addAttribute("buscarDocExtRecSeguiEstado",buscarDocExtRecSeguiEstBean);
            model.addAttribute("deVencimientoList", referencedData.getVencimientoList());
            model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
            model.addAttribute("deTipoRemiList",referencedData.getLstEmisorDocExtRecep());
            model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));        
            model.addAttribute("lstTupaExp",referencedData.getTupaExpList());
            model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
            model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));   
            return "/segEstRecDocSalida/consultaRegDocSalida";   
        }else{
           model.addAttribute("pMensaje", "SIN ACCESO.");
           return "respuesta";
        }
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(DocExtRecSeguiEstBean docBuscar, HttpServletRequest request, BindingResult result, Model model) {
        String mensaje = "NO_OK";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiConsultaMp();
        docBuscar.setTiAcceso(tipAcceso);
        docBuscar.setCoEmpleado(codEmpleado);
        docBuscar.setCoLocal(usuarioConfigBean.getCoLocal());
        docBuscar.setCoDependencia(codDependencia);
        docBuscar.setInMesaPartes(usuarioConfigBean.getInMesaPartes());           
        //docBuscar.setCoDepEmi(usuarioConfigBean.getCoDepMp());
//        buscarDocumentoSeguiEstRecBean.setCoDependencia(codDependencia);
//        buscarDocumentoSeguiEstRecBean.setCoEmpleado(codEmpleado);
//        buscarDocumentoSeguiEstRecBean.setTiAcceso(tipAcceso);
        List list = null;
        String objJson=null;
        try {
            list = seguiEstRecDocExtService.getLsDocExtRecSegui(docBuscar);
            String[] params={codDependencia,commonQryService.getDependenciaxCoDependencia(codDependencia).getDeDependencia(),
            Utility.getInstancia().dateToFormatString(new Date()),Utility.getInstancia().dateToFormatStringYYYY(new Date())};
            objJson=seguiEstRecDocExtService.getObjJson(params);
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }

        if (list != null) {
            if (list.size() >= 200) {
                model.addAttribute("msjEmision", "OPTIMICE SU CONSULTA");
            }
            model.addAttribute("objJson",objJson);
            model.addAttribute("emiDocumExtList", list);
            mensaje = "OK";
        } else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/segEstRecDocSalida/tblDocsSalida";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDependenciaRemite")
    public String goBuscaDependenciaRemite(HttpServletRequest request, Model model){
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        model.addAttribute("iniFuncionParm","2");
        model.addAttribute("lstRemitente",commonQryService.getListRemitente(pcoDepen));
        return "/modalGeneral/consultaDependenciaOrigenConsul";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaElaboradoPor")
    public String goBuscaElaboradoPor(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("iniFuncionParm","2");
        model.addAttribute("listaEmpleado",referencedData.getListEmpleadoElaboradoPorMP(pcoDep,usuarioConfigBean.getTiConsulta(),usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaElaboradoPorConsul";
    }  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        model.addAttribute("iniFuncionParm","6");
        model.addAttribute("listaDestinatario",referencedData.getListDestinatarioEmiDocExt(pnuAnn,usuario.getCoDep()));
        return "/modalGeneral/consultaDestinatarioEmiConsul";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetDocumentoExt")
    public String goDetDocumentoExt(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");
       
       DocExtRecSeguiEstBean DocExt;
       List lsRefDocExt;
       //List lsDestinoEmi;
       
       try{
           DocExt = seguiEstRecDocExtService.getDocumentoExtSeguiBean(pnuAnn, pnuEmi,pnuDes);
           pnuAnn=DocExt.getNuAnn();
           pnuEmi=DocExt.getNuEmi();
           lsRefDocExt = seguiEstRecDocExtService.getLsRefDocExterno(pnuAnn,pnuEmi);
           //lsDestinoEmi = seguiEstRecDocExtService.getLsDestinoEmi(pnuAnn,pnuEmi);               
           model.addAttribute(DocExt);
           //model.addAttribute("lstDestintarioDocAdmEmi",lsDestinoEmi);
           model.addAttribute("ltRefDocExt",lsRefDocExt);
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/segEstRecDocSalida/detalleDocExterno";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaReporte")
    public @ResponseBody String goRutaReporte(DocExtRecSeguiEstBean docBuscar, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta="";
        String deNoDoc;
        String extensionArch;

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);

        String pcoReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        docBuscar.setTipoBusqueda("0");
        docBuscar.setEsIncluyeFiltro(false);
        docBuscar.setInMesaPartes(usuarioConfigBean.getInMesaPartes());     
        docBuscar.setTiAcceso(usuarioConfigBean.getTiConsultaMp());
        docBuscar.setCoEmpleado(usuario.getCempCodemp());
        docBuscar.setCoDependencia(usuario.getCoDep());        
        String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());
        if(pcoReporte.equalsIgnoreCase("PDF")){
            pcoReporte = "TDR392";
            extensionArch=".pdf";
        }else{
            pcoReporte = "TDR392_XLS";
            extensionArch=".xls";
        }
        deNoDoc = "temp|SEGUI_DOC_EXT"+fecha+extensionArch;
        
        String prutaReporte="reporte?coReporte="+pcoReporte+"&coParametros=P_USER="+usuarioConfigBean.getCoUsuario()+
                            "|P_DE_DEPENDENCIA="+usuarioConfigBean.getDeDep()+"|P_WHERE=";
        String prutaReporteAux="";
        String eserror="1";//si
        try{
            prutaReporteAux = seguiEstRecDocExtService.getRutaReporte(docBuscar);
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
    public @ResponseBody String goExportarArchivoLista(DocExtRecSeguiEstBean docBuscar,HttpServletRequest request,
            BindingResult result, Model model) throws IOException
    {
        ServletContext sc = request.getSession().getServletContext();
        String coReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        
        String rutaReporte=sc.getRealPath("/reports/");
//        String rutaImagen = sc.getRealPath("/reports/logo_onpe.jpg");
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        docBuscar.setInMesaPartes(usuarioConfigBean.getInMesaPartes());     
        docBuscar.setTiAcceso(usuarioConfigBean.getTiConsultaMp());
        docBuscar.setCoEmpleado(usuario.getCempCodemp());
        docBuscar.setCoDependencia(usuario.getCoDep());        
        docBuscar.setRutaReporteJasper(rutaReporte);
        docBuscar.setFormatoReporte(coReporte);
        
        Map parametros = new HashMap();
        parametros.put("P_DE_DEPENDENCIA", usuarioConfigBean.getDeDep());
        parametros.put("P_USER", usuario.getCoUsuario());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        
        ReporteBean objReporteBean = seguiEstRecDocExtService.getGenerarReporte(docBuscar, parametros);
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
