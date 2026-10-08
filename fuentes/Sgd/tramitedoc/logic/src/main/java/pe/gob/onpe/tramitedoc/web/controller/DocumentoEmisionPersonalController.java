/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;

import java.util.Date;
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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoPersonalService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import pe.gob.onpe.tramitedoc.service.NotificacionService;

/**
 *
 * @author ecueva
 */

@Controller
@RequestMapping("/{version}/srDocumentoEmisionPersonal.do")
public class DocumentoEmisionPersonalController {
    
    @Autowired
    private EmiDocumentoPersonalService emiDocumentoPersonalService;
    
    @Autowired
    private ReferencedData referencedData;
    
    @Autowired
    private ApplicationProperties applicationProperties;    

    @Autowired
    private CommonQryService commonQryService; 
   
    @Autowired
    private NotificacionService notiService;
        
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        //BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String pEstado = ServletUtility.getInstancia().loadRequestParameter(request, "estadoDoc");
        
        BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        
        buscarDocumentoEmiBean.setsCoAnnio(Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        buscarDocumentoEmiBean.setsEstadoDoc(pEstado);
        buscarDocumentoEmiBean.setsCoDependencia(codDependencia);
        
        model.addAttribute("buscarDocumentoPersonalEmiBean",buscarDocumentoEmiBean);
        
        model.addAttribute("deAnnioList",referencedData.getAnnioList());
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));        
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        return "/DocumentoPersonalEmi/documentoAdmEmi";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoEmiBean buscarDocumentoEmiBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";

        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiAcceso();
        buscarDocumentoEmiBean.setsCoDependencia(codDependencia);
        buscarDocumentoEmiBean.setsCoEmpleado(codEmpleado);
        buscarDocumentoEmiBean.setsTiAcceso(tipAcceso);
        
        List list = null;

        try{
            list = emiDocumentoPersonalService.getDocumentosEmiAdm(buscarDocumentoEmiBean);                
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=50) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }         
            model.addAttribute("emiDocumAdmList",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/DocumentoPersonalEmi/tblDocAdmEmision";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaReferenciaOrigen")
    public String goBuscaReferenciaOrigen(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("listaReferenOrig",referencedData.getListReferenciaOrigenPersonal(usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaReferenciaOrigen";
    } 
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("listaDestinatario",referencedData.getListDestinatarioEmiPersonal(usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaDestinatarioEmi";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEditDocumentoEmi")
    public String goEditDocumentoEmi(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       String sexisteDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pexisteDoc");
       String sexisteAnexo = ServletUtility.getInstancia().loadRequestParameter(request, "pexisteAnexo");
       
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       String codDependencia = usuario.getCoDep();
       DocumentoEmiBean documentoEmiBean;
       //ExpedienteBean expedienteBean;
       List listReferenciaDocAdmEmi;
       
       try{
           documentoEmiBean = emiDocumentoPersonalService.getDocumentoEmiAdm(snuAnn,snuEmi);
           //expedienteBean = emiDocumentoAdmService.getExpDocumentoEmitido(documentoEmiBean.getNuAnnExp(), documentoEmiBean.getNuSecExp());
//           if(stipoDestinatario != null){
               model.addAttribute("lstDestintarioDocAdmEmi",emiDocumentoPersonalService.getLstDestintariotlbEmi(snuAnn,snuEmi));
//           }else{
//              stipoDestinatario = "01"; 
//           }
           model.addAttribute("sTipoDestEmi","01");
           model.addAttribute("sEsNuevoDocAdm","0");
           
           listReferenciaDocAdmEmi = emiDocumentoPersonalService.getLstDocumReferenciatblEmi(snuAnn,snuEmi);
           model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependenciaPersonal(codDependencia));
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
           model.addAttribute("pcodEmp",usuario.getCempCodemp());
           model.addAttribute("pdesEmp",usuario.getDeFullName());
           documentoEmiBean.setExisteDoc(sexisteDoc);
           documentoEmiBean.setExisteAnexo(sexisteAnexo);
           model.addAttribute("documentoPersonalEmiBean",documentoEmiBean);
           //model.addAttribute("expedienteBean",expedienteBean);
           model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
           
           model.addAttribute("lstReferenciaDocAdmEmi",listReferenciaDocAdmEmi);
           model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
           model.addAttribute("lstMotivoDestinatario",referencedData.getLstMotivoDestinatario(codDependencia,documentoEmiBean.getCoTipDocAdm()));
//           model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));
//           model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi());
           //model.addAttribute("refRecepDocumAdmList",list);
           List<EmpleadoVoBoBean> lsEmpVobo=commonQryService.getLsPersonalVoBo(snuAnn,snuEmi);
           model.addAttribute("lstEmpVoBoDocAdm",lsEmpVobo);
           mensaje = "OK";
       }catch(Exception ex){
          ex.printStackTrace(); 
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoPersonalEmi/nuevoDocumAdmEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goNuevoDocumentoEmi")
    public String goNuevoDocumentoEmi(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
//       String nuAnn = Utility.getInstancia().dateToFormatStringYYYY(new Date());
//       String fechaActual = Utility.getInstancia().dateToFormatString(new Date());
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       String codDependencia = usuario.getCoDep();
       String codEmpleado = usuario.getCempCodemp();
       String codLocal = usuario.getCoLocal();
       DocumentoEmiBean documentoEmiBean;
       try{
           //String esDocEmi = "5";
           documentoEmiBean = emiDocumentoPersonalService.getDocumentoEmiAdmNew(codDependencia,codEmpleado,codLocal);//estadoDocEmi, codigo de dependencia
           //documentoEmiBean.setEsDocEmi(esDocEmi);
           //documentoEmiBean.setCoDepEmi(codDependencia);
//           documentoEmiBean.setCoLocEmi(usuario.getCoLocal());
//           documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
//           documentoEmiBean.setDeEmpRes(usuario.getDeFullName());
//           documentoEmiBean.setFeEmiCorta(fechaActual);
//           documentoEmiBean.setTiEmi("05");
//           documentoEmiBean.setNuDiaAte("0");
//           documentoEmiBean.setNuAnn(nuAnn);
           model.addAttribute("sEsNuevoDocAdm","1");
           model.addAttribute("sTipoDestEmi","01");
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));           
           model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependenciaPersonal(codDependencia));
//           model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
//           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
//            model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi());
//            model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));            
            model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());           
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
//           model.addAttribute("pcodEmp",usuario.getCempCodemp());
//           model.addAttribute("pdesEmp",usuario.getDeFullName());
           model.addAttribute("documentoPersonalEmiBean",documentoEmiBean);
           mensaje = "OK";
       }catch(Exception ex){
          ex.printStackTrace(); 
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoPersonalEmi/nuevoDocumAdmEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goGrabaDocumentoEmi",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goGrabaDocumentoEmi(@RequestBody TrxDocumentoEmiBean trxDocumentoEmiBean,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta;
        String pnuAnnExp = ServletUtility.getInstancia().loadRequestParameter(request,"pnuAnnExp");//0 no crea, 1 crea
        String pnuSecExp = ServletUtility.getInstancia().loadRequestParameter(request,"pnuSecExp");
        String pnuExpediente = ServletUtility.getInstancia().loadRequestParameter(request,"pnuExpediente");
//        String sCrearExpediente = ServletUtility.getInstancia().loadRequestParameter(request,"pcrearExpediente");//0 no crea, 1 crea
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        trxDocumentoEmiBean.setCoUserMod(usuario.getCoUsuario());
        trxDocumentoEmiBean.setCempCodEmp(usuario.getCempCodemp());
        trxDocumentoEmiBean.setNuAnnExp(pnuAnnExp);
        trxDocumentoEmiBean.setNuSecExp(pnuSecExp);
        trxDocumentoEmiBean.setNuExpediente(pnuExpediente);
        try{
            mensaje = emiDocumentoPersonalService.grabaDocumentoEmiAdm(trxDocumentoEmiBean,usuario);
                    
            
        }catch(validarDatoException e){
           mensaje=(e.valorMsg);
        }catch (Exception e) { 
           e.printStackTrace(); 
        } 
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else if(mensaje.equals("NO_OK")){
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
        retval.append("\",\"nuEmi\":\"");
        retval.append(trxDocumentoEmiBean.getNuEmi());          
        retval.append("\",\"nuDoc\":\"");
        retval.append(trxDocumentoEmiBean.getNuDoc());          
        retval.append("\",\"nuDocEmi\":\"");
        retval.append(trxDocumentoEmiBean.getNuDocEmi());                  
        retval.append("\",\"nuCorEmi\":\"");
        retval.append(trxDocumentoEmiBean.getNuCorEmi());
        retval.append("\"}");

        return retval.toString();        
    }    
    
    //YUAL
    @RequestMapping(method = RequestMethod.POST, params = "accion=goNotificarVistoBueno")
    public @ResponseBody String goNotificarVistoBueno(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String coRespuesta;
        String deRespuesta;
        String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        String vReturn = notiService.notificarVistoBueno(snuEmi,snuAnn);
                    
                  
        if (vReturn.equals("OK")) {
            coRespuesta = "1";
        }else if(vReturn.equals("NO_OK")){
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }  
        deRespuesta = mensaje;
        
  

        return vReturn.toString();        
    }    
   
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerificarNumDocEmi",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goVerificarNumDocEmi(DocumentoEmiBean documentoEmiBean,HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;

       try{
           mensaje = emiDocumentoPersonalService.verificaNroDocumentoEmiDuplicado(documentoEmiBean);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToProyecto")
    public @ResponseBody String goChangeToProyecto(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       try{
          mensaje = emiDocumentoPersonalService.changeToProyecto(documentoEmiBean,usuario);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToEmitido")
    public @ResponseBody String goChangeToEmitido(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);
       
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       documentoEmiBean.setNuCorDoc(usuario.getCempCodemp());
       documentoEmiBean.setNuDocEmi(docSession.getNumeroDoc());
       documentoEmiBean.setNuSecuenciaFirma(applicationProperties.getRutaTemporal()+"//"+documentoEmiBean.getNuSecuenciaFirma());
       try{
          mensaje = emiDocumentoPersonalService.changeToEmitido(documentoEmiBean,docSession.getNoDocumento(),usuario);
        //YUAL  -NOTIFICACION
        notiService.getLsEmpleadoNotificar(documentoEmiBean.getNuEmi(),documentoEmiBean.getNuAnn(),documentoEmiBean.getDeTipDocAdm()+' '+documentoEmiBean.getNuDocEmi()+'-'+documentoEmiBean.getNuAnn()+'-'+documentoEmiBean.getDeDocSig());
        
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAnularDocumento")
    public @ResponseBody String goAnularDocumento(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       try{
          mensaje = emiDocumentoPersonalService.anularDocumento(documentoEmiBean,usuario);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goNuevoDocumentoEmiAtender")
    public String goNuevoDocumentoEmiAtender(DocumentoBean documentoRecepBean,HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
//       String nuAnn = Utility.getInstancia().dateToFormatStringYYYY(new Date());
//       String fechaActual = Utility.getInstancia().dateToFormatString(new Date());
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       String codDependencia = usuario.getCoDep();
       String codEmpleado = usuario.getCempCodemp();
       String codLocal = usuario.getCoLocal();
       DocumentoEmiBean documentoEmiBean;
       try{
           //String esDocEmi = "5";
           documentoEmiBean = emiDocumentoPersonalService.getDocumentoEmiAdmNew(codDependencia,codEmpleado,codLocal);//estadoDocEmi, codigo de dependencia
           //documentoEmiBean.setEsDocEmi(esDocEmi);
           //documentoEmiBean.setCoDepEmi(codDependencia);
//           documentoEmiBean.setCoLocEmi(usuario.getCoLocal());
//           documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
//           documentoEmiBean.setDeEmpRes(usuario.getDeFullName());
//           documentoEmiBean.setFeEmiCorta(fechaActual);
//           documentoEmiBean.setTiEmi("05");
//           documentoEmiBean.setNuDiaAte("0");
//           documentoEmiBean.setNuAnn(nuAnn);
                //YUAL
                documentoEmiBean.setNuAnnExp(documentoRecepBean.getNuAnnExp());
                documentoEmiBean.setNuSecExp(documentoRecepBean.getNuSecExp());
                documentoEmiBean.setNuExpediente(documentoRecepBean.getNuExpediente());
                //END YUAL
           documentoEmiBean.setDeAsu(documentoRecepBean.getDeAsu());
           model.addAttribute("sEsNuevoDocAdm","1");
           model.addAttribute("sTipoDestEmi","01");
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));           
           model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependenciaPersonal(codDependencia));
//           model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
//           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
//            model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi());
//            model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));            
           model.addAttribute("lstReferenciaDocAdmEmi",emiDocumentoPersonalService.getLstDocumReferenciaAtiendeDeriva(documentoRecepBean)); 
            model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());           
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
//           model.addAttribute("pcodEmp",usuario.getCempCodemp());
//           model.addAttribute("pdesEmp",usuario.getDeFullName());
            model.addAttribute("coPrioridadRef",documentoRecepBean.getCoPri());
           model.addAttribute("documentoPersonalEmiBean",documentoEmiBean);
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoPersonalEmi/nuevoDocumAdmEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }    
}
