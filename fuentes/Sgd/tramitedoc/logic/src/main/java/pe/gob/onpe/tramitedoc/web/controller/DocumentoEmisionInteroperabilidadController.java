package pe.gob.onpe.tramitedoc.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.CiudadanoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.TemaBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.DocumentoExtRecepService;
import pe.gob.onpe.tramitedoc.service.DocumentoVoBoService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoInteroperabilidadService;
import pe.gob.onpe.tramitedoc.service.NotificacionService;
import pe.gob.onpe.tramitedoc.service.ProveedorService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.service.TemaService;
import pe.gob.onpe.tramitedoc.util.Constantes;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import pe.gob.segdi.wsentidad.ws.EntidadBean;
import pe.gob.segdi.wsentidad.ws.WSEntidad;

@Controller
@RequestMapping("/{version}/srDocumentoEmisionInteroperabilidad.do")
public class DocumentoEmisionInteroperabilidadController {
    
    @Autowired
    private EmiDocumentoInteroperabilidadService emiDocumentoInterService;
    
    @Autowired
    private ReferencedData referencedData;

    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    private CommonQryService commonQryService;    
    
    @Autowired
    private DocumentoVoBoService documentoVoBoService;
    @Autowired
    private NotificacionService notiService;
    @Autowired
    private DocumentoExtRecepService documentoExtRecepService;
    @Autowired
    private TemaService temaService;
    
    @Autowired
    private ProveedorService proveedorService;
    
    @Autowired
    private DatosPlantillaDao datosPlantillaDao;
        
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        //BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String pEstado = ServletUtility.getInstancia().loadRequestParameter(request, "estadoDoc");        
        BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        String sCoAnnio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request); 
        TemaBean temabean =  new TemaBean();
        temabean.setCoDependencia(usuario.getCoDep());
        List<TemaBean> tema = temaService.getListTema(temabean);
        
        buscarDocumentoEmiBean.setsCoAnnio(sCoAnnio);
        buscarDocumentoEmiBean.setsCoAnnioBus(sCoAnnio);
        buscarDocumentoEmiBean.setsEstadoDoc(pEstado);
        buscarDocumentoEmiBean.setsCoDependencia(codDependencia);
        
        model.addAttribute(buscarDocumentoEmiBean);
        model.addAttribute("deListTema", tema);
        model.addAttribute("deAnnioList",referencedData.getAnnioList());
        model.addAttribute("deEstadosList",emiDocumentoInterService.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList",emiDocumentoInterService.getTipoDocumentoEmiList());
       //model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));// emiDocumentoInterService.getTipoDocumentoEmiList());        
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(codDependencia));
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        return "/DocumentoInteroperabilidadEmi/documentoAdmEmi";
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
            list = emiDocumentoInterService.getDocumentosBuscaEmiAdm(buscarDocumentoEmiBean);
            
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=100) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            
            model.addAttribute("emiDocumAdmList",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
           // System.out.println("ENVIANDO LISTA");
            return "DocumentoInteroperabilidadEmi/tblDocAdmEmision";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goNuevoDocumentoEmi")
    public String goNuevoDocumentoEmi(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String nuAnn = Utility.getInstancia().dateToFormatStringYYYY(new Date());
       String fechaActual = Utility.getInstancia().dateToFormatString(new Date());
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       String codDependencia = usuario.getCoDep();
       DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
       try{
           String esDocEmi = "5";
           documentoEmiBean = emiDocumentoInterService.getDocumentoEmiAdmNew(esDocEmi,codDependencia);//estadoDocEmi, codigo de dependencia
           documentoEmiBean.setEsDocEmi(esDocEmi);
           documentoEmiBean.setCoDepEmi(codDependencia);
           documentoEmiBean.setCoLocEmi(usuario.getCoLocal());
           documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
           documentoEmiBean.setDeEmpRes(usuario.getDeFullName());
           documentoEmiBean.setFeEmiCorta(fechaActual);
           documentoEmiBean.setTiEmi("06");
           documentoEmiBean.setNuDiaAte("0");
           documentoEmiBean.setNuAnn(nuAnn);
           model.addAttribute("sEsNuevoDocAdm","1");
           model.addAttribute("sTipoDestEmi","06");
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));   
           model.addAttribute("lstTipDocDependencia",emiDocumentoInterService.getTipoDocumentoxRef());
//           model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependencia(codDependencia)); //emiDocumentoInterService.getTipoDocumentoEmiList());
           //model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
           model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(codDependencia));
           model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));            
           model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());           
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
           model.addAttribute("pcodEmp",usuario.getCempCodemp());
           model.addAttribute("pdesEmp",usuario.getDeFullName());
           model.addAttribute(documentoEmiBean);
           model.addAttribute("inCreaExpediente",commonQryService.obtenerValorParametro("IN_CREA_EXPEDIENTE"));
           
           //model.addAttribute("lstTipCategoria",emiDocumentoInterService.listTipCategoria());
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoInteroperabilidadEmi/nuevoDocumAdmEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
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
           documentoEmiBean = emiDocumentoInterService.getDocumentoEmiAdm(snuAnn,snuEmi);
           
           //expedienteBean = emiDocumentoInterService.getExpDocumentoEmitido(documentoEmiBean.getNuAnnExp(), documentoEmiBean.getNuSecExp());
           String stipoDestinatario = emiDocumentoInterService.getTipoDestinatarioEmi(snuAnn, snuEmi);//obtener tipo de Destinatario
           if(stipoDestinatario != null){
               HashMap map = emiDocumentoInterService.getLstDestintariotlbEmi(snuAnn,snuEmi);
               model.addAttribute("lstDestintarioInterDocAdmEmi",map.get("lst1"));

               
           }else{
              stipoDestinatario = "01"; 
           }
           model.addAttribute("sTipoDestEmi",stipoDestinatario);
           model.addAttribute("sEsNuevoDocAdm","0");
           
           listReferenciaDocAdmEmi = emiDocumentoInterService.getLstDocumReferenciatblEmi(snuAnn,snuEmi);
          // model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependencia(codDependencia));
           model.addAttribute("lstTipDocDependencia",emiDocumentoInterService.getTipoDocumentoxRef());
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
           model.addAttribute("pcodEmp",usuario.getCempCodemp());
           model.addAttribute("pdesEmp",usuario.getDeFullName());
           documentoEmiBean.setExisteDoc(sexisteDoc);
           documentoEmiBean.setExisteAnexo(sexisteAnexo);
           model.addAttribute(documentoEmiBean);
           //model.addAttribute("expedienteBean",expedienteBean);
           model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
           
           model.addAttribute("lstReferenciaDocAdmEmi",listReferenciaDocAdmEmi);
           model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
           model.addAttribute("lstMotivoDestinatario",referencedData.getLstMotivoDestinatario(documentoEmiBean.getCoDepEmi(),documentoEmiBean.getCoTipDocAdm()));
           model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(documentoEmiBean.getCoDepEmi()));
           model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(documentoEmiBean.getCoDepEmi()));
           List<EmpleadoVoBoBean> lsEmpVobo=commonQryService.getLsPersonalVoBo(snuAnn,snuEmi);
           model.addAttribute("lstEmpVoBoDocAdm",lsEmpVobo);
           model.addAttribute("lstNotiDocAdmEmi",emiDocumentoInterService.getNotificaciones(lsEmpVobo,documentoEmiBean.getInFirmaAnexo()));
           //model.addAttribute("refRecepDocumAdmList",list);
           model.addAttribute("inCreaExpediente",commonQryService.obtenerValorParametro("IN_CREA_EXPEDIENTE"));
           //model.addAttribute("lstTipCategoria",emiDocumentoInterService.listTipCategoria());
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoInteroperabilidadEmi/nuevoDocumAdmEmi";
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
        String sCrearExpediente = ServletUtility.getInstancia().loadRequestParameter(request,"pcrearExpediente");//0 no crea, 1 crea
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        trxDocumentoEmiBean.setCoUserMod(usuario.getCoUsuario());
        trxDocumentoEmiBean.setCempCodEmp(usuario.getCempCodemp());
        try{
            mensaje = emiDocumentoInterService.grabaDocumentoEmiAdm(trxDocumentoEmiBean,sCrearExpediente,usuario);
            //ProbarFrankSilva
            if (mensaje.equals("OK")) {
                String vReturn = notiService.procesarNotificacion(trxDocumentoEmiBean.getNuAnn(), trxDocumentoEmiBean.getNuEmi(), usuario.getCoUsuario());
                if (!vReturn.equals("OK")) {
                    throw new validarDatoException(vReturn);
                }
            }
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
        retval.append("\",\"nuCorEmi\":\"");
        retval.append(trxDocumentoEmiBean.getNuCorEmi());
        retval.append("\",\"nuDocEmi\":\"");
        retval.append(trxDocumentoEmiBean.getNuDocEmi());        
        retval.append("\",\"nuAnnExp\":\"");
        retval.append(trxDocumentoEmiBean.getNuAnnExp());
        retval.append("\",\"nuSecExp\":\"");
        retval.append(trxDocumentoEmiBean.getNuSecExp());
        retval.append("\",\"feExp\":\"");
        retval.append(trxDocumentoEmiBean.getFeExp());                 
        retval.append("\",\"nuExpediente\":\"");
        retval.append(trxDocumentoEmiBean.getNuExpediente());                                  
        retval.append("\",\"nuDetExp\":\"");
        retval.append("1");        
        retval.append("\"}");

        return retval.toString();        
    } 
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdTlbsDestinatario")
    public String goUpdTlbsDestinatario(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");        
       
       try{
            HashMap map = emiDocumentoInterService.getLstDestintariotlbEmi(snuAnn,snuEmi);
            
            model.addAttribute("lstDestintarioInterDocAdmEmi",map.get("lst1"));
                      
            
            model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocal());
            model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());
            
            
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           System.out.println("Entree");
           return "/DocumentoInteroperabilidadEmi/actualizaTablasDestinoEmiDoc";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdTlbReferencia")
    public String goUpdTlbReferencia(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");        
        String scoDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDependencia");

        try{
           model.addAttribute("lstReferenciaDocAdmEmi",emiDocumentoInterService.getLstDocumReferenciatblEmi(snuAnn,snuEmi)); 
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(scoDependencia));
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoInteroperabilidadEmi/tablaRefEmiDocAdm";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }    
     
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaReferenciaOrigen")
    public String goBuscaReferenciaOrigen(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("listaReferenOrig",referencedData.getListReferenciaOrigen(pnuAnn,usuarioConfigBean));
        return "/modalGeneral/consultaReferenciaOrigen";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("listaDestinatario",referencedData.getListDestinatarioEmi(pnuAnn,usuarioConfigBean));
        return "/modalGeneral/consultaDestinatarioEmi";
    }   
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaElaboradoPor")
    public String goBuscaElaboradoPor(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);        
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("listaEmpleado",referencedData.getListEmpleadoElaboradoPor(pcoDep,usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()));
        return "/modalGeneral/consultaElaboradoPor";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDependenciaDestinatario")
    public String goBuscaDependenciaDestinatario(HttpServletRequest request, Model model){
        String pdeDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pdeDepen");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        model.addAttribute("listaDependenciaDestEmi",referencedData.getListDependenciaDestinatarioEmi(usuario.getCoDep(),pdeDepen));
        return "/modalGeneral/consultaDependenciaDestEmi";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaCiudadano")
    public @ResponseBody String goBuscaCiudadano(HttpServletRequest request, Model model){
        String vRespuesta;
        String coRespuesta;
        StringBuilder retval = new StringBuilder();        
        String pnuDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDoc");
        CiudadanoBean ciudadanoBean;
        try{
            ciudadanoBean = emiDocumentoInterService.getCiudadano(pnuDoc);
           if (ciudadanoBean != null) {
                coRespuesta = "1";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"ciudadano\":");
                retval.append("{\"nuDoc\":\"");
                retval.append(ciudadanoBean.getNuDocumento());
                retval.append("\",\"nombre\":\"");
                retval.append(ciudadanoBean.getNombre());
                //retval.append("\"");                
                retval.append("\",\"ubigeo\":\"");
                retval.append(ciudadanoBean.getUbigeo());                
                retval.append("\",\"ubdep\":\""); 
                retval.append(ciudadanoBean.getIdDepartamento());                
                retval.append("\",\"ubprv\":\""); 
                retval.append(ciudadanoBean.getIdProvincia());                
                retval.append("\",\"ubdis\":\""); 
                retval.append(ciudadanoBean.getIdDistrito());                 
                retval.append("\",\"dedomicil\":\""); 
                retval.append(ciudadanoBean.getDeDireccion());
                retval.append("\",\"deemail\":\""); 
                retval.append(ciudadanoBean.getDeCorreo());
                retval.append("\",\"detelefo\":\""); 
                retval.append(ciudadanoBean.getTelefono());
                retval.append("\"");
                
                retval.append("}");            
                retval.append("}");            
           }else{
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\"}");
           }            
        }catch(Exception e){
            vRespuesta = e.getMessage();
            model.addAttribute("pMensaje", vRespuesta);
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\"}");
        }       
        return retval.toString();
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestOtroOrigenAgrega")
    public String goBuscaDestOtroOrigenAgrega(HttpServletRequest request, Model model){
        String pnomOtroOrigen = ServletUtility.getInstancia().loadRequestParameter(request, "pnoOtroOrigen");
        model.addAttribute("lsDestOtroOrigen",emiDocumentoInterService.getLstOtrosOrigenesAgrega(pnomOtroOrigen));
        model.addAttribute("iniFuncionParm","1");
        return "/modalGeneral/consultaDestOtroOrigen";
    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestOtroOrigen")
    public String goBuscaDestOtroOrigen(HttpServletRequest request, Model model){
        String pnomOtroOrigen = ServletUtility.getInstancia().loadRequestParameter(request, "pnoOtroOrigen");
        model.addAttribute("lsDestOtroOrigen",documentoExtRecepService.getLstOtrosOrigenes(pnomOtroOrigen));
        model.addAttribute("iniFuncionParm","3");
        return "/modalGeneral/consultaDestOtroOrigen";
    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestCiudadano")
    public String goBuscaDestCiudadano(HttpServletRequest request, Model model){ 
        String pnoCiudadano = ServletUtility.getInstancia().loadRequestParameter(request, "pnoCiudadano");
        model.addAttribute("lsDestCiudadano",emiDocumentoInterService.getLstCiudadano(pnoCiudadano));
        model.addAttribute("iniFuncionParm","1");
        return "/modalGeneral/consultaDestCiudadano";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestProveedorAgrega")
    public String goBuscaDestProveedorAgrega(HttpServletRequest request, Model model){
        String prazonSocial = ServletUtility.getInstancia().loadRequestParameter(request, "prazonSocial");
        model.addAttribute("lsDestProveedor",emiDocumentoInterService.getLstProveedoresAgrega(prazonSocial));
        model.addAttribute("iniFuncionParm","1");
        return "/modalGeneral/consultaDestProveedor";
    }        
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaPersonalDestinatario")
    public String goBuscaPersonalDestinatario(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        List list = null;
        try{
            list = emiDocumentoInterService.getPersonalDestinatario(pcoDepen);
        }catch(Exception ex){
             mensaje = ex.getMessage();
        }
        if (list!=null) {
            model.addAttribute("listaEmpleadoDestEmi",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaEmpleadoDestEmi";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }        
    }  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaAccionDestinatario")
    public String goBuscaAccionDestinatario(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String pcoTipDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pcoTipDoc");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String pcoDepen = usuario.getCoDep();
        List list = null;
        try{
            list = emiDocumentoInterService.getLstMotivoxTipoDocumento(pcoDepen,pcoTipDoc);
        }catch(Exception ex){
             mensaje = ex.getMessage();
        }
        if (list!=null) {
            model.addAttribute("listaAccionDestEmi",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaMotivoDestEmi";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }        
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaEmpleadoLocaltblDestinatario",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goBuscaEmpleadoLocaltblDestinatario(HttpServletRequest request, Model model) throws Exception {
       String vRespuesta = "";
       String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
       String pcoTipoDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pcoTipoDoc");
       String coRespuesta = "";
       StringBuffer retval = new StringBuffer();
       EmpleadoBean empleadoBean  = null;
       try{
           empleadoBean = emiDocumentoInterService.getEmpleadoLocaltblDestinatario(pcoDepen);
           if (empleadoBean != null) {
                coRespuesta = "1";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"coLocal\":\"");
                retval.append(empleadoBean.getCoLocal());
                retval.append("\",\"deLocal\":\"");
                retval.append(empleadoBean.getDeLocal());
                retval.append("\",\"coEmpleado\":\"");
                retval.append(empleadoBean.getCempCodemp());
                retval.append("\",\"deEmpleado\":\"");
                retval.append(empleadoBean.getCompName());
                retval.append("\",\"coTramiteFirst\":\"");
                if(pcoTipoDoc.equals("232")){
                        retval.append(Constantes.CO_TRAMITE_ATENDER);
                        retval.append("\",\"deTramiteFirst\":\"");
                        retval.append(Constantes.DE_TRAMITE_ATENDER);                	
                }else{
                        retval.append(Constantes.CO_TRAMITE_ORIGINAL);
                        retval.append("\",\"deTramiteFirst\":\"");
                        retval.append(Constantes.DE_TRAMITE_ORIGINAL);                
                }
                retval.append("\",\"coTramiteNext\":\"");
                if(pcoTipoDoc.equals("232")){
                        retval.append(Constantes.CO_TRAMITE_FINES);
                        retval.append("\",\"deTramiteNext\":\"");
                        retval.append(Constantes.DE_TRAMITE_FINES);                	
                }else{
                        retval.append(Constantes.CO_TRAMITE_COPIA);
                        retval.append("\",\"deTramiteNext\":\"");
                        retval.append(Constantes.DE_TRAMITE_COPIA);                
                }               
                retval.append("\"}");            
           }else{
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"deRespuesta\":\"");
                retval.append("NO SE PUEDE REGISTRAR DESTINATARIO");                
                retval.append("\"}");
           }
       }catch(Exception e){
            vRespuesta = e.getMessage();
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(vRespuesta);                            
            retval.append("\"}");
       }
       return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDocumentotblReferencia")
    public String goBuscaDocumentotblReferencia(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String pannio = ServletUtility.getInstancia().loadRequestParameter(request, "pannio");
        String ptiDoc = ServletUtility.getInstancia().loadRequestParameter(request, "ptiDoc");
        String pnuDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDoc");
        String ptiBusqueda = ServletUtility.getInstancia().loadRequestParameter(request, "ptiBusqueda");// rec doc recepcionado, emi doc emitido
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pcoDepen = usuario.getCoDep();        
        String pcoEmpEmi = usuario.getCempCodemp();
        List list = null;
        try{
            if(ptiBusqueda.equals("emi")){ // doc emitido
                list = emiDocumentoInterService.getLstDocEmitidoRef(pcoEmpEmi,pcoDepen,pannio,ptiDoc,pnuDoc);                
            }else if(ptiBusqueda.equals("rec")){ // doc recepcionado
                list = emiDocumentoInterService.getLstDocRecepcionadoRef(pcoDepen,pannio,ptiDoc,pnuDoc,usuarioConfigBean.getInMesaPartes());
            }
        }catch(Exception ex){
             mensaje = ex.getMessage();
        }
        if (list!=null) {
            if (list.size()>=200) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }            
            model.addAttribute("lstDocReferenciaEmi",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaDocumentoRefEmi";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaLstMotivoDocEmi")
    public @ResponseBody String goBuscaLstMotivoDocEmi(HttpServletRequest request, Model model){
        String vRespuesta;
        String coRespuesta;
        StringBuilder retval = new StringBuilder();
        String pcoTipDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pcoTipDoc");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String pcoDepen = usuario.getCoDep();
        List<MotivoBean> list = null;
        boolean bandera = false;
        try{
            list = emiDocumentoInterService.getLstMotivoxTipoDocumento(pcoDepen,pcoTipDoc);
           if (list != null) {
                coRespuesta = "1";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"lsMotivo\":");
                retval.append("[");
                for (MotivoBean mot : list){
                    retval.append("{\"value\":\"");
                    retval.append(mot.getCoMot());
                    retval.append("\",\"label\":\"");
                    retval.append(mot.getDeMot());
                    retval.append("\"},");
                    bandera = true;
                }
                if(bandera){
                    retval.deleteCharAt(retval.length()-1);
                }
                retval.append("]");
                retval.append("}");            
           }else{
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\"}");
           }            
        }catch(Exception e){
            vRespuesta = e.getMessage();
            model.addAttribute("pMensaje", vRespuesta);
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\"}");
        }
        
        return retval.toString(); 
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAnularDocEmiAdm")
    public @ResponseBody String goAnularDocEmiAdm(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       try{
          mensaje = emiDocumentoInterService.anularDocumentoEmiAdm(documentoEmiBean,usuario);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAgregarDestinatarioIntitucion",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goAgregarDestinatarioIntitucion(HttpServletRequest request, Model model) throws Exception {
//       String vRespuesta = "";
       String scogrupo = ServletUtility.getInstancia().loadRequestParameter(request, "pcogrupo");
       String pcoTipDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pcoTipDoc");
//       String coRespuesta = "";
       String retval;

       retval = emiDocumentoInterService.getLstDestintarioAgregaGrupo(scogrupo,pcoTipDoc);
           
       return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCargarDocEmi")
    public @ResponseBody String goCargaDocEmi(HttpServletRequest request, Model model){
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
                 mensaje = emiDocumentoInterService.cargaDocEmi(docObjBean);
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
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerificarNumDocEmi",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goVerificarNumDocEmi(DocumentoEmiBean documentoEmiBean,HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;

       try{
           mensaje = emiDocumentoInterService.verificaNroDocumentoEmiDuplicado(documentoEmiBean);
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
       String nuAnn = Utility.getInstancia().dateToFormatStringYYYY(new Date());
       String fechaActual = Utility.getInstancia().dateToFormatString(new Date());
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       String codDependencia = usuario.getCoDep();
       DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
       try{
           String sEsDocRec = documentoRecepBean.getEsDocRec();
           if(sEsDocRec.equals("0") || sEsDocRec.equals("9")){
               if(sEsDocRec.equals("0")){
                   mensaje = "Documento no Recepcionado..";
               }else{
                   mensaje = "Documento esta Anulado..";
               }
           }else{
                String esDocEmi = "5";
                documentoEmiBean = emiDocumentoInterService.getDocumentoEmiAdmNew(esDocEmi,codDependencia);//estadoDocEmi, codigo de dependencia
                documentoEmiBean.setEsDocEmi(esDocEmi);
                documentoEmiBean.setCoDepEmi(codDependencia);
                documentoEmiBean.setCoLocEmi(usuario.getCoLocal());
                documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
                documentoEmiBean.setDeEmpRes(usuario.getDeFullName());
                documentoEmiBean.setFeEmiCorta(fechaActual);
                documentoEmiBean.setTiEmi("06");
                documentoEmiBean.setNuDiaAte("0");
                documentoEmiBean.setNuAnn(nuAnn);
                documentoEmiBean.setDeAsu(documentoRecepBean.getDeAsu());
                documentoEmiBean.setNuExpediente(documentoRecepBean.getNuExpediente());
                documentoEmiBean.setNuAnnExp(documentoRecepBean.getNuAnnExp());
                documentoEmiBean.setNuSecExp(documentoRecepBean.getNuSecExp());
                documentoEmiBean.setFeExpCorta(documentoRecepBean.getFeExpCorta());
                documentoEmiBean.setCoProceso(documentoRecepBean.getCoProceso());
                documentoEmiBean.setDeProceso(documentoRecepBean.getDeProceso());
                model.addAttribute("sEsNuevoDocAdm","1");
                model.addAttribute("sTipoDestEmi","06");
                model.addAttribute("deAnnioList",referencedData.getAnnioList());
                model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));           
                model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependencia(codDependencia));
                model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
                model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
                model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(documentoEmiBean.getCoDepEmi()));
                model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));            
                model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());           
                model.addAttribute("lstReferenciaDocAdmEmi",emiDocumentoInterService.getLstDocumReferenciaAtiendeDeriva(documentoRecepBean)); 
                model.addAttribute("pfechaHoraActual",fechaHoraActual);
                model.addAttribute("pcodEmp",usuario.getCempCodemp());
                model.addAttribute("pdesEmp",usuario.getDeFullName());
                model.addAttribute(documentoEmiBean);
                model.addAttribute("inCreaExpediente",commonQryService.obtenerValorParametro("IN_CREA_EXPEDIENTE"));
                mensaje = "OK";               
           }
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoInteroperabilidadEmi/nuevoDocumAdmEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaElaboradoPorEdit")
    public String goBuscaElaboradoPorEdit(HttpServletRequest request, Model model){
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("listaEmpleado",emiDocumentoInterService.getPersonalEditDocAdmEmision(pcoDep));
        return "/modalGeneral/consultaElaboradoPorEdit";
    }  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaFirmadoPorEdit")
    public String goBuscaFirmadoPorEdit(HttpServletRequest request, Model model){
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("listaEmpleado",emiDocumentoInterService.getPersonalEditDocAdmEmision(pcoDep));
        return "/modalGeneral/consultaFirmadoPorEdit";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToProyecto",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goChangeToProyecto(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
       try{
          mensaje = emiDocumentoInterService.changeToProyecto(documentoEmiBean,usuario);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToDespacho",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goChangeToDespacho(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       if(documentoEmiBean.getNuSecuenciaFirma()!=null && !documentoEmiBean.getNuSecuenciaFirma().equals("")){
            documentoEmiBean.setNuSecuenciaFirma(applicationProperties.getRutaTemporal()+"//"+documentoEmiBean.getNuSecuenciaFirma());
       }else{
           documentoEmiBean.setNuSecuenciaFirma(null);
       }
       try{
          mensaje = emiDocumentoInterService.changeToDespacho(documentoEmiBean,usuario);
          //ProbarFrankSilva
          if(mensaje.equals("OK")){
              String vReturn = notiService.procesarNotificacion(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi(), usuario.getCoUsuario());
              if (!vReturn.equals("OK")) {
                  throw new validarDatoException(vReturn);
              }
          }
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToEmitido",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goChangeToEmitido(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       DocumentoVerBean docSession =Utilidades.getInstancia().loadDocFromSession(request);
       
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       documentoEmiBean.setNuCorDoc("1");
       documentoEmiBean.setNuDocEmi(docSession.getNumeroDoc());
       documentoEmiBean.setNuSecuenciaFirma(applicationProperties.getRutaTemporal()+"//"+documentoEmiBean.getNuSecuenciaFirma());
       try{
          mensaje = emiDocumentoInterService.changeToEmitido(documentoEmiBean,docSession.getNoDocumento(),usuario,applicationProperties.getNroRucInstitu());

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
            
            //add YUAL
            // model.addAttribute("pnuAnn", documentoEmiBean.getNuAnn());
          //   model.addAttribute("pnuEmi", documentoEmiBean.getNuDocEmi());
          //   model.addAttribute("MensajeriaList",commonQryService.getLsDepenciaMensjeria());
         
          //   return "/modalGeneral/consultaEnvioNotificacion";
            
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

    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goChangeToEmitidoAlta",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goChangeToEmitidoAlta(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       documentoEmiBean.setNuCorDoc("1");
       try{
          mensaje = emiDocumentoInterService.changeToEmitidoAlta(documentoEmiBean);

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
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEliminarDocEmiAdm")
    public @ResponseBody String goEliminarDocEmiAdm(DocumentoEmiBean documentoEmiBean, HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       documentoEmiBean.setCoUseMod(usuario.getCoUsuario());
       try{
          mensaje = emiDocumentoInterService.delDocumentoEmiAdm(documentoEmiBean,usuario);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goObtenerExpedienteBean",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goObtenerExpedienteBean(HttpServletRequest request, Model model){
        String vRespuesta;
        String coRespuesta;
        StringBuilder retval = new StringBuilder();
        String pnuAnnExp = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnnExp");
        String pnuSecExp = ServletUtility.getInstancia().loadRequestParameter(request, "pnuSecExp");
        ExpedienteBean expedienteBean;
        try{
           expedienteBean = emiDocumentoInterService.getExpDocumentoEmitido(pnuAnnExp, pnuSecExp);
           if (expedienteBean != null) {
                coRespuesta = "1";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"nuSecExp\":\"");
                retval.append(expedienteBean.getNuSecExp());
                retval.append("\",\"nuExpediente\":\"");
                retval.append(expedienteBean.getNuExpediente());
                retval.append("\",\"nuAnnExp\":\"");
                retval.append(expedienteBean.getNuAnnExp());
                retval.append("\",\"feExpCorta\":\"");
                retval.append(expedienteBean.getFeExpCorta());
                retval.append("\",\"feExp\":\"");
                retval.append(expedienteBean.getFeExp());
                retval.append("\",\"coProceso\":\"");
                retval.append(expedienteBean.getCoProceso());          
                retval.append("\",\"deProceso\":\"");
                retval.append(expedienteBean.getDeProceso());                
                retval.append("\"}");            
           }else{
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"deRespuesta\":\"");
                retval.append("No se puede obtener el expediente");                
                retval.append("\"}");
           }
       }catch(Exception e){
            vRespuesta = e.getMessage();
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(vRespuesta);                            
            retval.append("\"}");
       }
       return retval.toString();
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
            map = emiDocumentoInterService.getBuscaDependenciaEmite(usuarioConfigBean.getCoDep(),pdeDepEmite);
            String vReturn = (String)map.get("vReturn");
            if(vReturn.equals("1")){
                model.addAttribute("listaDependenciaDestEmi",map.get("listaDestinatario"));
                vResult = "/modalGeneral/consultaDependenciaEmi";
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
    public String goBuscaDocumentoEnReferencia(BuscarDocumentoEmiBean buscarDocumentoEmiBean,HttpServletRequest request, Model model){
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoEmiBean.setsCoDependencia(usuario.getCoDep());
        buscarDocumentoEmiBean.setsCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoEmiBean.setsTiAcceso(usuarioConfigBean.getTiAcceso());
        
        String mensaje ="NO_OK";
        HashMap mp = null;
        List list = null;
        
        try{
            mp = emiDocumentoInterService.getDocumentosEnReferencia(buscarDocumentoEmiBean);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        } 
        mensaje = String.valueOf(mp.get("msjResult"));
        if(mensaje.equals("1")){
            list = (List) mp.get("emiDocumAdmList");
            if(list!=null){
                if (list.size()>=100){
                    model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
                }
                model.addAttribute("emiDocumAdmList",list);
                mensaje = "OK";                
            }
        }
        if (mensaje.equals("OK")) {
            return "/DocumentoInteroperabilidadEmi/tblDocAdmEmision";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }      
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goCambiaDepEmi")
    private @ResponseBody String goCambiaDepEmi(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();
        String pcoDepEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepEmi");
        DependenciaBean depEmi = emiDocumentoInterService.cambiaDepEmi(pcoDepEmi);
        
        if (depEmi!=null){
                retval.append("{\"retval\":\"");
                retval.append("OK");
                retval.append("\",\"coEmpEmi\":\"");
                retval.append(depEmi.getCoEmpleado());
                retval.append("\",\"deEmpEmi\":\"");
                retval.append(depEmi.getDeDependencia());
                retval.append("\",\"deSigla\":\"");
                retval.append(depEmi.getDeSigla());
                retval.append("\"}");                        
        }else{
                retval.append("{\"retval\":");
                retval.append("false");
                retval.append("}");
        }
        
        return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaEmpVoBoDocAdm")
    public String goBuscaEmpVoBoDocAdm(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String pnomEmp = ServletUtility.getInstancia().loadRequestParameter(request, "pnomEmp");
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("iniFuncionParm","5");
        model.addAttribute("listaEmpleado",commonQryService.getLsEmpDepDesEmp(pnomEmp,pcoDep));
        return "/modalGeneral/consultaElaboradoPorConsul";
    } 
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDepVoBoDocAdm")
    public String goBuscaDepVoBoDocAdm(HttpServletRequest request, Model model){
        String pdeDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pdeDepen");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        model.addAttribute("iniFuncionParm","9");
        model.addAttribute("listaDestinatario",referencedData.getListDependenciaDestinatarioEmi(usuario.getCoDep(),pdeDepen));        
        return "/modalGeneral/consultaDestinatarioEmiConsul";
    }     
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaEmptblPersonalVoBo",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goBuscaEmptblPersonalVoBo(HttpServletRequest request, Model model) throws Exception {
       String vRespuesta;
       String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
       String coRespuesta;
       StringBuffer retval = new StringBuffer();
       EmpleadoBean empleadoBean;
       try{
           empleadoBean = commonQryService.getEmpJefeDep(pcoDepen);
           if (empleadoBean != null && empleadoBean.getCempCodemp() != null) {
                coRespuesta = "1";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"coEmpleado\":\"");
                retval.append(empleadoBean.getCempCodemp());
                retval.append("\",\"deEmpleado\":\"");
                retval.append(empleadoBean.getNombre());
                retval.append("\"}");            
           }else{
                coRespuesta = "0";
                retval.append("{\"coRespuesta\":\"");
                retval.append(coRespuesta);
                retval.append("\",\"deRespuesta\":\"");
                retval.append("NO SE ENCUENTRA ENCARGADO DEPENDENCIA");                
                retval.append("\"}");
           }
       }catch(Exception e){
            vRespuesta = e.getMessage();
            coRespuesta = "0";
            retval.append("{\"coRespuesta\":\"");
            retval.append(coRespuesta);
            retval.append("\",\"deRespuesta\":\"");
            retval.append(vRespuesta);                            
            retval.append("\"}");
       }
       return retval.toString();
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerificarVistoBuenoPendiente")
    public @ResponseBody String goVerificarVistoBuenoPendiente(HttpServletRequest request, Model model){
       String mensaje;
       StringBuilder retval = new StringBuilder();
       String coRespuesta;
       String vRespuesta;
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       
       try{
          mensaje = documentoVoBoService.existeVistoBuenoPendienteDocAdm(pnuAnn, pnuEmi);
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdTlbVoBo")
    public String goUpdTlbVoBo(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");        
        //String scoDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDependencia");
        List<EmpleadoVoBoBean> lsEmpVobo;
                
        try{
           lsEmpVobo=commonQryService.getLsPersonalVoBo(snuAnn,snuEmi);
           model.addAttribute("lstEmpVoBoDocAdm",lsEmpVobo);
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoInteroperabilidadEmi/tablaPersVoBoDocAdm";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }         
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAgregarPersVoboGrupo",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goAgregarPersVoboGrupo(HttpServletRequest request, Model model) throws Exception {
       String scogrupo = ServletUtility.getInstancia().loadRequestParameter(request, "pcogrupo");
       String retval;

       retval = emiDocumentoInterService.getLstPersVoBoGrupo(scogrupo);
           
       return retval.toString();
    }

  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBusquedaCategoriaDestinatario")
    public String goBusquedaCategoriaDestinatario(HttpServletRequest request, Model model){
        //String pdeDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pdeDepen");
        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
System.out.println("buscarcateg");
        model.addAttribute("lstTipCategoria",emiDocumentoInterService.listTipCategoria());
        
        return "/modalGeneral/consultaDestCategoria";
    }
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBusquedaEntidadDestinatario")
    public String goBusquedaEntidadDestinatario(HttpServletRequest request, Model model) throws Exception {
        String idCategoria = ServletUtility.getInstancia().loadRequestParameter(request, "idCategoria");
        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        WSEntidad wSEntidad = new WSEntidad();
       // System.out.println("categoria==>"+idCategoria);
        
        List<EntidadBean> listta = wSEntidad.getEntidad(Integer.valueOf(idCategoria),datosPlantillaDao.getParametros("URL_ENTIDADES_INTER"));
        List<EntidadBean> listEntidad = new ArrayList<EntidadBean>();
        for (EntidadBean entidadBean : listta) {
            if(!entidadBean.getVrucent().equals("0")){
                listEntidad.add(entidadBean);
            }
        }
        //model.addAttribute("lstEntidad",wSEntidad.getEntidad(Integer.valueOf(pCategoria)));
        model.addAttribute("lstEntidad",listEntidad);
        return "/modalGeneral/consultaDestEntidad";
    }
   
        
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAgregarProveedorEmi")
    public @ResponseBody String goAgregarProveedorEmi(HttpServletRequest request, Model model) {
        
        String mensaje = "NO_OK";
        StringBuilder retval = new StringBuilder();
        String coRespuesta;
        String vRespuesta;
       
        try {
            String ruc = ServletUtility.getInstancia().loadRequestParameter(request, "ruc");
            String descripcion = ServletUtility.getInstancia().loadRequestParameter(request, "descripcion");
            
            ProveedorBean proveedor =new ProveedorBean();
            
            proveedor.setNuRuc(ruc);
            proveedor.setDescripcion(descripcion);
                      
            //String respuesta = 
            emiDocumentoInterService.insProveedorEmi(proveedor);
            mensaje="OK";
       }catch(Exception e){
           mensaje = e.getMessage();
       }
       
       vRespuesta = mensaje;
//        if (mensaje.equals("OK")) {
//            model.addAttribute("pMensaje", "Datos guardados.");
//            return "respuesta";
//        } else {
//            model.addAttribute("pMensaje", mensaje);
//            return "respuesta";
//        }

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

    
    
    
}
