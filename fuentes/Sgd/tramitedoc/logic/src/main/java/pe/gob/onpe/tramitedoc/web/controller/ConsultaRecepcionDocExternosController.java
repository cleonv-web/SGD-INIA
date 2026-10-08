/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.web.controller;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import net.sf.jasperreports.engine.JRException;
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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.ConsultaEmiDocExtService;
import pe.gob.onpe.tramitedoc.service.GrupoDestinoService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Controller
@RequestMapping("/{version}/srConsultaRecDocExterno.do")
public class ConsultaRecepcionDocExternosController {
    
    @Autowired
    private ReferencedData referencedData;     
    
    @Autowired
    private ConsultaEmiDocExtService docExtEmiService;    
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    private GrupoDestinoService grupoDestinoService;
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
       
        BuscarDocumentoExtConsulBean DocExt = new BuscarDocumentoExtConsulBean();
        String sCoAnnio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        DocExt.setCoAnnio(sCoAnnio);
        
        model.addAttribute(DocExt);
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
        model.addAttribute("deTipoRemiList",referencedData.getLstEmisorDocExtRecep());
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));        
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(codDependencia));
        model.addAttribute("deEmisorList",referencedData.getLstEmisorDocExtRecep());
        model.addAttribute("lstTipoExp", referencedData.grpElementoList("TIP_EXPEDIENTE"));
        model.addAttribute("lstOrigen", referencedData.grpElementoList("ORI_DOCUMENTO"));
        model.addAttribute("lstTupaExp",referencedData.getTupaExpList());
        
        List<GrupoDestinoBean> grupoDestinoList = grupoDestinoService.getGruposDestinosVarList("02");
            model.addAttribute("lTipoDestinatarioEmi", grupoDestinoList);
        
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));        
        return "/mpConsultaRegistroDocExt/consultaRegDocExt";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoExtConsulBean buscarDocExt, HttpServletRequest request, Model model){
        String mensaje = "NO_OK";

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocExt.setCoDependencia(usuario.getCoDep());
        buscarDocExt.setCoLocal(usuarioConfigBean.getCoLocal());
        buscarDocExt.setTiAcceso(usuarioConfigBean.getTiConsultaMp());
        buscarDocExt.setCoEmpleado(usuario.getCempCodemp());
        buscarDocExt.setCoDepEmi(usuarioConfigBean.getCoDepMp());
        buscarDocExt.setInMesaPartes(usuarioConfigBean.getInMesaPartes());        
        
        List list = null;

        try{
                list = docExtEmiService.getDocumentosExtConsulBean(buscarDocExt);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            //if (list.size()>=200) {
            if (list.size()>= applicationProperties.getTopRegistrosConsultas()) {    
                //model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
                model.addAttribute("msjEmision","Solo se muestra "+applicationProperties.getTopRegistrosConsultas()+" filas en pantalla, descargue el excel o pdf para visualizar todos los registros");
            }
            model.addAttribute("docExtRecepLst",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/mpConsultaRegistroDocExt/tblDocsExterno";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm","2");
        //model.addAttribute("listaDestinatario",referencedData.getListDestinatarioEmiDocExt(pnuAnn,usuario.getCoDep()));
        model.addAttribute("listaDestinatario",referencedData.getListDestinatarioEmiDocExt(pnuAnn,usuarioConfigBean.getCoDepMp()));
        return "/modalGeneral/consultaDestinatarioEmiConsul";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDetDocumentoExt")
    public String goDetDocumentoExt(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       
       DocumentoExtConsulBean DocExt;
       List lsRefDocExt;
       List lsDestinoEmi;
       
       try{
           DocExt = docExtEmiService.getDocumentoExtConsulBean(pnuAnn, pnuEmi);
           pnuAnn=DocExt.getNuAnn();
           pnuEmi=DocExt.getNuEmi();
           lsRefDocExt = docExtEmiService.getLsRefDocExterno(pnuAnn,pnuEmi);
           lsDestinoEmi = docExtEmiService.getLsDestinoEmi(pnuAnn,pnuEmi);               
           model.addAttribute(DocExt);
           model.addAttribute("lstDestintarioDocAdmEmi",lsDestinoEmi);
           model.addAttribute("ltRefDocExt",lsRefDocExt);
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/mpConsultaRegistroDocExt/detalleDocExterno";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goRutaReporte")
    public @ResponseBody String goRutaReporte(BuscarDocumentoExtConsulBean bDocExt, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta="";
        String deNoDoc;
        String extensionArch;

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuarioConfigBean.getCoDep();
        bDocExt.setCoDependencia(codDependencia);
        //setter
        String pcoReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        bDocExt.setTipoBusqueda("0");
        bDocExt.setEsIncluyeFiltro(false);
        bDocExt.setInMesaPartes(usuarioConfigBean.getInMesaPartes());     
        bDocExt.setTiAcceso(usuarioConfigBean.getTiConsultaMp());
        bDocExt.setCoEmpleado(usuario.getCempCodemp());
        bDocExt.setCoDependencia(usuario.getCoDep());
        String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());
        if(pcoReporte.equalsIgnoreCase("PDF")){
            pcoReporte = "TDR33";
            extensionArch=".pdf";
        }else{
            pcoReporte = "TDR33_XLS";
            extensionArch=".xls";
        }
        deNoDoc = "temp|RECEPDOCEXT_"+fecha+extensionArch;
        
        String prutaReporte="reporte?coReporte="+pcoReporte+"&coParametros=P_USER="+usuarioConfigBean.getCoUsuario()+
                            "|P_DE_DEPENDENCIA="+usuarioConfigBean.getDeDep()+"|P_WHERE=";
        String prutaReporteAux="";
        String eserror="1";//si
        try{
            prutaReporteAux = docExtEmiService.getRutaReporte(bDocExt);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaLocal")
    public String goBuscaLocal(HttpServletRequest request, Model model){
        //String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        model.addAttribute("iniFuncionParm","1");
        model.addAttribute("deLocalesList",referencedData.getLsLocales());
        return "/modalGeneral/consultaLocal";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaAllDependencia")
    public String goBuscaAllDependencia(HttpServletRequest request, Model model){
        model.addAttribute("iniFuncionParm","5");
        model.addAttribute("listaReferenOrig",docExtEmiService.getAllDependencias());
        return "/modalGeneral/consultaReferenciaOrigenConsul";
    }        
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goExportarArchivoLista")
    public @ResponseBody String goExportarArchivoLista(BuscarDocumentoExtConsulBean buscarDocumentoExtConsulBean, HttpServletRequest request,
            BindingResult result, Model model) throws JRException, FileNotFoundException, IOException 
    {           
        ServletContext sc = request.getSession().getServletContext();
        
        String coReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuarioConfigBean.getCoDep();
                
        String rutaReporte=sc.getRealPath("/reports/");
//        String rutaImagen = sc.getRealPath("/reports/logo_onpe.jpg");
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        Map parametros = new HashMap();
        parametros.put("P_DE_DEPENDENCIA", usuarioConfigBean.getDeDep());
        parametros.put("P_USER", usuario.getCoUsuario());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);
        
        //YUAL
        List<GrupoDestinoBean> grupoDestinoList = grupoDestinoService.getGruposDestinosVarList("02");
        if(buscarDocumentoExtConsulBean.getBusCoGrupoExt()!="0")
        { 
            for(int i=0; i<grupoDestinoList.size();i++){
                if(grupoDestinoList.get(i).getCoGruDes().equals(buscarDocumentoExtConsulBean.getBusCoGrupoExt()))
                { 
                    parametros.put("P_GRUPO", "Grupo: "+grupoDestinoList.get(i).getDeGruDes());
                    i=grupoDestinoList.size();
                }
            }
           
        }
        else
        {parametros.put("P_GRUPO", "");
        }
                
        //setter
        buscarDocumentoExtConsulBean.setCoDependencia(codDependencia);        
        buscarDocumentoExtConsulBean.setInMesaPartes(usuarioConfigBean.getInMesaPartes());     
        buscarDocumentoExtConsulBean.setTiAcceso(usuarioConfigBean.getTiConsultaMp());
        buscarDocumentoExtConsulBean.setCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoExtConsulBean.setCoDepEmi(usuarioConfigBean.getCoDepMp());
  
        buscarDocumentoExtConsulBean.setRutaReporteJasper(rutaReporte);
        buscarDocumentoExtConsulBean.setFormatoReporte(coReporte);
        
        ReporteBean objReporteBean = docExtEmiService.getGenerarReporte(buscarDocumentoExtConsulBean, parametros);
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
