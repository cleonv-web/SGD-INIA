/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.web.controller;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocVistoBuenoBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.ConsultaEmiDocService;
import pe.gob.onpe.tramitedoc.service.DocumentoVoBoService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Controller
@RequestMapping("/{version}/srDocumentoVoBo.do")
public class DocumentoVoBoController {
    
    @Autowired
    private DocumentoVoBoService documentoVoBoService;    
    
    @Autowired
    private ConsultaEmiDocService consultaEmiDocService;  
    
    @Autowired
    private ReferencedData referencedData; 
    
    @Autowired
    private CommonQryService commonQryService;  
    
    @Autowired
    private ApplicationProperties applicationProperties;    
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        String coDep = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String coEsDoc = ServletUtility.getInstancia().loadRequestParameter(request, "coEsDoc");
        BuscarDocumentoVoBoBean bDocVoBo = new BuscarDocumentoVoBoBean();
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        bDocVoBo.setNuAnn(annio);
        bDocVoBo.setCoDepUsu(coDep);
        bDocVoBo.setEsDoc(coEsDoc);
        model.addAttribute(bDocVoBo);
        
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocVoBo("TDTV_PERSONAL_VB"));
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(coDep));        
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(coDep));
        return "/documentoVoBo/buscaDocsVobo";
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoVoBoBean bDocVoBo, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String coDep = usuario.getCoDep();
        String coEmp = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiAcceso();
        bDocVoBo.setCoDepUsu(coDep);
        bDocVoBo.setCoEmpUsu(coEmp);
        bDocVoBo.setTiAccesoUsu(tipAcceso);

        List<DocumentoVoBoBean> list = null;

        try{
                list = documentoVoBoService.getLsDocsVoBo(bDocVoBo);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=100) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            model.addAttribute("lsDocsVobo",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));        
        if (mensaje.equals("OK")) {
            return "/documentoVoBo/tblDocsVobo";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEditDocumentoVoBo")
    public String goEditDocumentoVoBo(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       
       try{
           DocumentoVoBoBean docVobo = documentoVoBoService.getDocumentoVoBo(snuAnn,snuEmi,usuario.getCoDep(),usuario.getCempCodemp());
           String stipoDestinatario = consultaEmiDocService.getTipoDestinatarioEmi(snuAnn, snuEmi);//obtener tipo de Destinatario
           if(stipoDestinatario != null){
               HashMap map = consultaEmiDocService.getLstDestintariotlbEmi(snuAnn,snuEmi);
               model.addAttribute("lstDestintarioDocAdmEmi",map.get("lst1"));
               model.addAttribute("lstDestintarioProveedorDocAdmEmi",map.get("lst2"));
               model.addAttribute("lstDestintarioCiudadanoDocAdmEmi",map.get("lst3"));
               model.addAttribute("lstDestintarioOtroOrigenDocAdmEmi",map.get("lst4"));
           }
           model.addAttribute("lstEmpVoBoDocAdm",commonQryService.getLsPersonalVoBo(snuAnn,snuEmi));
           model.addAttribute("lstReferenciaDocAdmEmi",consultaEmiDocService.getLstDocumReferenciatblEmi(snuAnn,snuEmi));
           model.addAttribute(docVobo);
           
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/documentoVoBo/docVobo";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goGrabarDocVoBo",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goGrabarDocVoBo(@RequestBody TrxDocVistoBuenoBean trxDocVobo,HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       trxDocVobo.setCoUserMod(usuario.getCoUsuario());
       trxDocVobo.getDocVoBoBean().setCoDepUsu(usuario.getCoDep());
       trxDocVobo.getDocVoBoBean().setCoEmpUsu(usuario.getCempCodemp());
       try{
          mensaje = documentoVoBoService.goGrabarDocVoBo(trxDocVobo);
       }catch(Exception e){
           mensaje = e.getMessage();
       }
       
       vRespuesta = mensaje;
       if (mensaje.equals("OK")) {
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            String deObs=trxDocVobo.getDocVoBoBean().getDeObs();
            retval.append("\",\"emptyObs\":\"");
            if(deObs!=null&&deObs.trim().length()>0){
                retval.append("0");                             
            }else{
                retval.append("1");                             
            }
            retval.append("\",\"coDep\":\"");
            retval.append(usuario.getCoDep());                             
            retval.append("\",\"coEmp\":\"");
            retval.append(usuario.getCempCodemp());                             
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToEnviado",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goChangeToEnviado(DocumentoVoBoBean docVobo, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);
       
       docVobo.setCoEmpUsu(usuario.getCoUsuario());
       docVobo.setCoDepUsu(usuario.getCoDep());
       docVobo.setCoEmpVb(usuario.getCempCodemp());
//       documentoEmiBean.setNuCorDoc("1");
//       documentoEmiBean.setNuDocEmi(docSession.getNumeroDoc());
       docVobo.setNuSecuenciaFirma(applicationProperties.getRutaTemporal()+"//"+docVobo.getNuSecuenciaFirma());
       try{
          mensaje = documentoVoBoService.changeToEnviado(docVobo,docSession.getNoDocumento(),usuario.getNuDni(),applicationProperties.getNroRucInstitu());

       }catch(validarDatoException e){
           mensaje=(e.valorMsg);
       }catch(Exception e){
           mensaje = e.getMessage();
       }
       
       vRespuesta = mensaje;
       if (mensaje.equals("OK")) {
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"coDep\":\"");
            retval.append(usuario.getCoDep());                             
            retval.append("\",\"coEmp\":\"");
            retval.append(usuario.getCempCodemp());                
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToSinVobo",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goChangeToSinVobo(DocumentoVoBoBean docVobo, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       docVobo.setCoEmpUsu(usuario.getCoUsuario());
       docVobo.setCoDepUsu(usuario.getCoDep());
       docVobo.setCoEmpVb(usuario.getCempCodemp());       
       try{
          mensaje = documentoVoBoService.goChangeToSinVobo(docVobo);
       }catch(Exception e){
           mensaje = e.getMessage();
       }
       
       vRespuesta = mensaje;
       if (mensaje.equals("OK")) {
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"coDep\":\"");
            retval.append(usuario.getCoDep());                             
            retval.append("\",\"coEmp\":\"");
            retval.append(usuario.getCempCodemp());              
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCargarDocVoboEnvia")
    public @ResponseBody String goCargarDocVoboEnvia(HttpServletRequest request, Model model){
       String mensaje="NO_OK";
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
       String vnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
       String vnuSec = ServletUtility.getInstancia().loadRequestParameter(request, "nuSec");

       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       
       DocumentoObjBean docObjBean = new DocumentoObjBean();
       if (vnuAnn!=null && vnuEmi!=null && vnuSec!=null){
            try{
                 docObjBean.setNuAnn(vnuAnn);
                 docObjBean.setNuEmi(vnuEmi);
                 docObjBean.setTiCap(ServletUtility.getInstancia().loadRequestParameter(request, "tiCap"));
                 docObjBean.setNombreArchivo(ServletUtility.getInstancia().loadRequestParameter(request, "noDoc"));
                 docObjBean.setNumeroSecuencia(applicationProperties.getRutaTemporal()+"//"+vnuSec);
                 docObjBean.setCoUseMod(usuario.getCoUsuario());
                 mensaje = documentoVoBoService.cargaDocEmi(docObjBean);
            }catch(Exception e){
                mensaje = "Error en Cargar Docuemntos";
                e.printStackTrace();
            }
       }else{
          mensaje = "Documento no encontrado";
       }
               
       
       if (mensaje!=null && mensaje.equals("OK")) {
            coRespuesta = "1";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append("Documento Cargado Correctamente");                            
            retval.append("\"}");
       } else {
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(mensaje);                            
            retval.append("\"}");
       }
       return retval.toString();
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaObsEmpVobo")
    public String goBuscaObsEmpVobo(HttpServletRequest request, Model model){
        String nuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String nuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        String coDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        String coEmp = ServletUtility.getInstancia().loadRequestParameter(request, "pcoEmp");
        String view="/modalGeneral/consultaObsPersonalVobo";
        try{
            model.addAttribute("deObs", documentoVoBoService.getDeObsEmpVoBo(nuAnn, nuEmi, coDep, coEmp));
        }catch(Exception e){
            e.printStackTrace();
        }
        return view; 
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaReferenciaOrigen")
    public String goBuscaReferenciaOrigen(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String pconsulta = ServletUtility.getInstancia().loadRequestParameter(request, "consultaO");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm","3");
        model.addAttribute("listaReferenOrig",referencedData.getListReferenciaOrigenVB(pnuAnn,pconsulta,usuarioConfigBean));
        return "/modalGeneral/consultaReferenciaOrigenConsul";
    }  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaElaboradoPor")
    public String goBuscaElaboradoPor(HttpServletRequest request, Model model){
        String pconsulta = ServletUtility.getInstancia().loadRequestParameter(request, "pconsulta");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pcoDep = usuarioConfigBean.getCoDep();
        model.addAttribute("iniFuncionParm","6");
        model.addAttribute("listaEmpleado",referencedData.getListEmpleadoElaboradoPorVB(pcoDep,pconsulta,usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaElaboradoPorConsul";
    }      
}