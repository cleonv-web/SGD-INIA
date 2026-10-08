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
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.CargoEntregaBean;
import pe.gob.onpe.tramitedoc.bean.DocPedienteEntregaBean;
import pe.gob.onpe.tramitedoc.bean.GuiaMesaPartesBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.TrxGeneraGuiaMpBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CargoEntregaService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;


/**
 *
 * @author ecueva
 */
@Controller
@RequestMapping("/{version}/srCargoEntrega.do")
public class CargoEntregaController {
    
    @Autowired
    private ReferencedData referencedData;
    
    @Autowired
    private CargoEntregaService cargoEntregaService;  
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicioCargo")
    public String goInicioCargo(HttpServletRequest request, Model model){
        String coLocal = ServletUtility.getInstancia().loadRequestParameter(request, "coLoc");
        String coDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        CargoEntregaBean cargoEntregaBean = new CargoEntregaBean();
        cargoEntregaBean.setNuAnnGuia(annio);
        cargoEntregaBean.setEstadoGuiaMp("0");//generado
        cargoEntregaBean.setCoLocDes(coLocal);
        cargoEntregaBean.setCoDepOri(coDependencia);
        model.addAttribute("buscarCargoEntregaBean",cargoEntregaBean);        
        
        model.addAttribute("deLocalesList",referencedData.getLsLocales());        
        model.addAttribute("deEstadosList",referencedData.getLstEstadoCargoEntrega("TDTC_GUIA_MP"));
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));        
        return "/mpCargoEntrega/buscaCargoEntrega";
    } 
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicioCargo")
    public String goInicioCargo(CargoEntregaBean cargo, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";

        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        cargo.setCoDepOri(usuarioConfigBean.getCoDep());
        //cargo.setCoLocOri(usuarioConfigBean.getCoLocal());

        List list=null;

        try{
                list = cargoEntregaService.getCargosEntrega(cargo);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=50) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            model.addAttribute("cargoEntregaLs",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/mpCargoEntrega/tblCargoEntrega";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    } 
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicioNewDocPendienteEntrega")
    public String goInicioNewDocPendienteEntrega(HttpServletRequest request, Model model){
        String coLocal = ServletUtility.getInstancia().loadRequestParameter(request, "coLoc");
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        DocPedienteEntregaBean DocPendienteEntrega = new DocPedienteEntregaBean();
        DocPendienteEntrega.setNuAnn(annio);
        DocPendienteEntrega.setCoLocDes(coLocal);
        model.addAttribute("buscarDocPendienteEntregaBean",DocPendienteEntrega);        
        
        model.addAttribute("deLocalesList",referencedData.getLsLocales());        
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));        
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));        
        return "/mpCargoEntrega/buscarDocPendEntrega";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicioNewDocPendienteEntrega")
    public String goInicioNewDocPendienteEntrega(DocPedienteEntregaBean doc, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";

        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        //doc.setCoDepEmi(usuario.getCoDep());
        doc.setCoDepEmi(usuarioConfigBean.getCoDepMp());
        doc.setCoLocEmi(usuarioConfigBean.getCoLocal());
        String tipAcceso = usuarioConfigBean.getTiAcceso();
        doc.setTiAcceso(tipAcceso);

        List list=null;

        try{
            list = cargoEntregaService.getDocsPendienteEntrega(doc);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=50) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            model.addAttribute("docPendEntregaLs",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/mpCargoEntrega/tblDocPendEntrega";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goGeneraCargoEntrega")
    public String goGeneraCargoEntrega(@RequestBody List<DocPedienteEntregaBean> list,HttpServletRequest request, Model model){
        String vResult="";
        String coRespuesta;
        StringBuilder retval = new StringBuilder();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        try {
            GuiaMesaPartesBean guia=cargoEntregaService.getGuiaMp(list);
            if(guia!=null){
                String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
                String fechaHoraActual = Utility.getInstancia().dateToFormatString3(new Date());
                guia.setNuAnn(annio);
                guia.setCoDepOri(usuario.getCoDep());
                guia.setCoLocOri(usuarioConfigBean.getCoLocal());
                guia.setDeDepOri(usuarioConfigBean.getDeDep());
                guia.setDeLocOri(usuarioConfigBean.getDeLocal());
                guia.setDeEstadoGuia("GENERADO");
                guia.setFeGuiMp(fechaHoraActual);

                list=cargoEntregaService.getDocsPendienteEntrega(list);
                model.addAttribute("lDetalleGuia",list);
                model.addAttribute("tblDetCarga","0");//cargar tabla detalle
                model.addAttribute(guia); 
                vResult="/mpCargoEntrega/cargoEntrega";
            }else{
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"deRespuesta\":\"");
                retval.append("Seleccionar registros de una misma dependencia.");                
                retval.append("\"}"); 
                model.addAttribute("pMensaje", retval.toString());
                vResult = "respuesta";
            }              
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vResult;
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goGrabarGuiaMp",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goGrabarGuiaMp(@RequestBody TrxGeneraGuiaMpBean trxGuia,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String coRespuesta="0";
        String deRespuesta;        
        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        trxGuia.setCoUsuario(usuarioConfigBean.getCoUsuario());
        String resultJsonStr="";
        try {
            mensaje = cargoEntregaService.grabarCargoEntrega(trxGuia);
            if(mensaje.equals("OK")){
              coRespuesta = "1"; 
              resultJsonStr=","+cargoEntregaService.getJsonRptGrabarCargoEntrega(trxGuia);
            }
        }catch(validarDatoException e){
           mensaje=(e.valorMsg);
        } catch (Exception e) {
            e.printStackTrace();
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEditCargoEntrega")
    public String goEditCargoEntrega(HttpServletRequest request, Model model){
       String pnuAnnGuia = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnnGuia");
       String pnuGuia = ServletUtility.getInstancia().loadRequestParameter(request, "pnuGuia");       
       List<DocPedienteEntregaBean> list;
       try {
            GuiaMesaPartesBean guia=cargoEntregaService.getGuiaMp(pnuAnnGuia, pnuGuia);
            list=cargoEntregaService.getDetalleGuiaMp(pnuAnnGuia, pnuGuia);
            model.addAttribute("lDetalleGuia",list);
            model.addAttribute("tblDetCarga","1");//cargar tabla detalle edit
            model.addAttribute(guia);
        }catch (Exception ex){
            ex.printStackTrace();
        }
        return "/mpCargoEntrega/cargoEntrega";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAnularGuiaMp")
    public @ResponseBody String goAnularGuiaMp(GuiaMesaPartesBean guia, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
       guia.setCoUseMod(usuarioConfigBean.getCoUsuario());
       try{
          mensaje = cargoEntregaService.anularGuiaMp(guia);
       }catch(Exception e){
           mensaje = e.getMessage();
       }
       
       vRespuesta = mensaje;
       if (mensaje.equals("OK")) {
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\"}");
       } else {
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(vRespuesta);                            
            retval.append("\"}");
       }
       return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaGuia")
    private @ResponseBody String goRutaGuia(HttpServletRequest request, Model model) throws Exception {
        StringBuilder retval=new StringBuilder();;
        String sResult="NO_OK";
        String coRespuesta;
        String vRespuesta;        
        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pnuAnnGuia = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnnGuia");
        String pnuGuia = ServletUtility.getInstancia().loadRequestParameter(request, "pnuGuia");               
        try {
            sResult=cargoEntregaService.getRutaGuia(pnuAnnGuia,pnuGuia,usuarioConfigBean.getCoUsuario());
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if(sResult.equals("NO_OK")){
            coRespuesta = "0";
            vRespuesta = "ERROR AL OBTENER URL DE GUIA.";            
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(vRespuesta);                            
            retval.append("\"}");             
        }else{
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\"");            
            retval.append(sResult);
            retval.append("\"}");  
        }
        return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDependencia")
    public String goBuscaDependencia(HttpServletRequest request, Model model){
        String pcoLocDes = ServletUtility.getInstancia().loadRequestParameter(request, "pcoLocDes");
        model.addAttribute("iniFuncionParm","3");
        model.addAttribute("listaDestinatario",referencedData.getListDestinatarioDocPendEntrega(pcoLocDes));
        return "/modalGeneral/consultaDestinatarioEmiConsul";
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDependenciaCargoEntrega")
    public String goBuscaDependenciaCargoEntrega(HttpServletRequest request, Model model){
        String pcoLocDes = ServletUtility.getInstancia().loadRequestParameter(request, "pcoLocDes");
        model.addAttribute("iniFuncionParm","4");
        model.addAttribute("listaDestinatario",referencedData.getListDestinatarioDocPendEntrega(pcoLocDes));
        return "/modalGeneral/consultaDestinatarioEmiConsul";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goExportarArchivoLista")
    public @ResponseBody String goExportarArchivoLista(HttpServletRequest request, Model model) throws IOException {           
        ServletContext sc = request.getSession().getServletContext();
        
        String pnuAnnGuia = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnnGuia");
        String pnuGuia = ServletUtility.getInstancia().loadRequestParameter(request, "pnuGuia"); 

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
                
        String rutaReporte = sc.getRealPath("/reports/");
//        String rutaImagen = sc.getRealPath("/reports/logo_onpe.jpg");
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        Map parametros = new HashMap();
        parametros.put("P_DE_DEPENDENCIA", usuarioConfigBean.getDeDep());
        parametros.put("P_USER", usuario.getCoUsuario());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);
        
        CargoEntregaBean cargo = new CargoEntregaBean();
        cargo.setNuAnnGuia(pnuAnnGuia);
        cargo.setNuGuia(pnuGuia);        
        cargo.setRutaReporteJasper(rutaReporte);
        
        ReporteBean objReporteBean = cargoEntregaService.getGenerarReporte(parametros, cargo);
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
