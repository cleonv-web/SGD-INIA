package pe.gob.onpe.tramitedoc.web.controller;

import com.sun.org.apache.bcel.internal.generic.AALOAD;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.log4j.Logger;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.http.MediaType;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.PrioridadDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.TemaBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.DocumentoMensajesService;
import pe.gob.onpe.tramitedoc.service.RecepDocumentoAdmService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.service.TemaService;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
@Controller
@RequestMapping("/{version}/srDocumentoAdmRecepcion.do")
public class DocumentoAdmRecepcionController {

    @Autowired
    private RecepDocumentoAdmService recepDocumentoAdmService;
    
    @Autowired
    private ReferencedData referencedData;

    @Autowired
    private TemaService temaService; 
    
    @Autowired
    private DocumentoMensajesService documentoMensajesService; 
    
    @Autowired
    private ApplicationProperties applicationProperties;   
        
        
    private static Logger logger=Logger.getLogger("SGDDocumentoAdmRecepcionController");
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        String pEstado = ServletUtility.getInstancia().loadRequestParameter(request, "estadoDoc");//'0' documento no leido
        String pEtiqueta = ServletUtility.getInstancia().loadRequestParameter(request, "idEtiqueta");//'0' documento no leido
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String coBandeja = ServletUtility.getInstancia().loadRequestParameter(request, "coBandeja");
        String sPrioridadDoc= ServletUtility.getInstancia().loadRequestParameter(request, "sPrioridadDoc");
        BuscarDocumentoRecepBean buscarDocumentoRecepBean = new BuscarDocumentoRecepBean();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request); 
        TemaBean temabean =  new TemaBean();
        temabean.setCoDependencia(usuario.getCoDep());
        List<TemaBean> tema = temaService.getListTema(temabean);
        List listDepa = null;
        buscarDocumentoRecepBean.setsCoAnnio(annio);
        buscarDocumentoRecepBean.setsCoAnnioBus(annio);
        buscarDocumentoRecepBean.setsEstadoDoc(pEstado);
        buscarDocumentoRecepBean.setsCoDependencia(codDependencia);
        buscarDocumentoRecepBean.setIdEtiqueta(pEtiqueta);
        if(coBandeja.equals("P")){
            buscarDocumentoRecepBean.setEsIncluyeProfesional(true);
        }
        else{
            buscarDocumentoRecepBean.setEsIncluyeProfesional(false);
        }
        if(coBandeja.equals("O")){
            buscarDocumentoRecepBean.setEsIncluyeOficina(true);
        }
        else{
            buscarDocumentoRecepBean.setEsIncluyeOficina(false);
        }
        listDepa = recepDocumentoAdmService.getDocumDepenProveido(usuario.getCoDep());
        recepDocumentoAdmService.estadoRecepcionDocumento(buscarDocumentoRecepBean,"");
        
         if(!sPrioridadDoc.equals("")){
            buscarDocumentoRecepBean.setsPrioridadDoc(sPrioridadDoc);
        }
        if(pEtiqueta.length()>0){
             buscarDocumentoRecepBean.setsTipoProyDoc("0");
        }
        else {
             buscarDocumentoRecepBean.setsTipoProyDoc("1");
        }
        
         //ANTES ERA 1
         //buscarDocumentoRecepBean.setsTipoProyDoc("0");
         
         
        //model.addAttribute("recepDocumAdmList",recepDocumentoAdmService.getDocumentosRecepAdm(buscarDocumentoRecepBean));
        model.addAttribute(buscarDocumentoRecepBean);
       
        model.addAttribute("deListTema", tema);
        model.addAttribute("deAnnioList",referencedData.getAnnioList());
     //   if(pEstado=="02")
     //   {
        model.addAttribute("esRecibido", pEstado);        
      //  }
     //   else
      //  {model.addAttribute("esRecibido", "0");        
      //  }
        model.addAttribute("recepDocProveido",listDepa);
        listDepa = null;
        
        List<DocumentoBean> oListResumen=recepDocumentoAdmService.getResumenRecepAdmList(buscarDocumentoRecepBean); 
        String iCantPendientes="0";
        String iCantUrgentes="0";
        String iCantNormal="0";
        if(oListResumen.get(2).getiPendientes()!=null){iCantPendientes=oListResumen.get(2).getiPendientes();}
        if(oListResumen.get(1).getiUrgentes()!=null){iCantUrgentes=oListResumen.get(1).getiUrgentes();}
        if(oListResumen.get(0).getiNormal()!=null){iCantNormal=oListResumen.get(0).getiNormal();}
        
        model.addAttribute("iCantPendientes",iCantPendientes);
        model.addAttribute("iCantUrgentes",iCantUrgentes);
        model.addAttribute("iCantNormal",iCantNormal);
        
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoRec("TDTV_DESTINOS"));
     /*   if(pEstado.equals("P")){
            List<PrioridadDocumentoBean> lstPrioridad= new ArrayList<PrioridadDocumentoBean>();
            PrioridadDocumentoBean oPrioridad= new PrioridadDocumentoBean();
            oPrioridad.setCoPri("");
            oPrioridad.setDePri(".:TODOS.:");
            lstPrioridad.add(oPrioridad);
            oPrioridad= new PrioridadDocumentoBean();
            oPrioridad.setCoPri("1");
            oPrioridad.setDePri("NORMAL");
            lstPrioridad.add(oPrioridad);
            oPrioridad= new PrioridadDocumentoBean();
            oPrioridad.setCoPri("4");
            oPrioridad.setDePri("URGENTE");
            lstPrioridad.add(oPrioridad);
            model.addAttribute("dePrioridadesList",lstPrioridad);
        }
        else{*/
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        /*}*/
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoList(codDependencia));
        model.addAttribute("deTipoProyDocList", referencedData.grpTipoProyList("CO_TIPO_PROY"));
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(codDependencia));
        model.addAttribute("deEtiquetasList",referencedData.getEtiquetasList());
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
         buscarDocumentoRecepBean= new BuscarDocumentoRecepBean();
         
           if(pEstado==null)
            pEstado="";
           if(codDependencia==null)
            codDependencia="";
           
         if(!pEstado.toLowerCase().contains("script") && !codDependencia.toLowerCase().contains("script")){
            return "/recepDocumentosAdm/recepDocumentoAdm"; 
        }
        else {
            return ""; 
        }
        
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoRecepBean buscarDocumentoRecepBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoRecepBean.setsCoDependencia(usuario.getCoDep());
        buscarDocumentoRecepBean.setsCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoRecepBean.setsTiAcceso(usuarioConfigBean.getTiAcceso());
        
        List list = null;
        
        try{
//
//
            //list = recepDocumentoAdmService.getDocumentosBuscaRecepAdm(buscarDocumentoRecepBean);
            buscarDocumentoRecepBean= new BuscarDocumentoRecepBean();
            usuarioConfigBean= new UsuarioConfigBean();
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }
        
        if (list!=null) {
            if (list.size()>=100) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            
            model.addAttribute("recepDocumAdmList",list);
            list= null;
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/recepDocumentosAdm/tablaDocumentoAdm";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
   
    @RequestMapping(method = RequestMethod.POST, params = "accion=goListaBusqDocumRecepAdmJson",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProcessResult<List<DocumentoBean>> goListaBusqDocumRecepAdmJson(HttpServletRequest request, Model model) {
       
        String mensaje = "NO_OK";
        ProcessResult<List<DocumentoBean>> Result= new ProcessResult<List<DocumentoBean>>();
        StringBuilder retval = new StringBuilder();
        try {
            BuscarDocumentoRecepBean buscarDocumentoRecepBean= new BuscarDocumentoRecepBean();
            /*Map<String, Object> map = (Map<String,Object>)model;
            if(map!=null){
            buscarDocumentoRecepBean=(BuscarDocumentoRecepBean)map.get("buscarDocumentoRecepBean");
            }*/
            Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
            UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
  
           /* String busNroRuc = ServletUtility.getInstancia().loadRequestParameter(request, "busNroRuc");
            String busRazonSocial = ServletUtility.getInstancia().loadRequestParameter(request, "busRazonSocial");*/
             String objectg = ServletUtility.getInstancia().loadRequestParameter(request, "buscarDocumentoRecepBean");
             ObjectMapper mapper = new ObjectMapper();
             buscarDocumentoRecepBean=mapper.readValue(objectg, BuscarDocumentoRecepBean.class);
             
             buscarDocumentoRecepBean.setEsIncluyeOficina(Boolean.valueOf(ServletUtility.getInstancia().loadRequestParameter(request, "EsIncluyeOficina")));
             buscarDocumentoRecepBean.setEsIncluyeProfesional(Boolean.valueOf(ServletUtility.getInstancia().loadRequestParameter(request, "EsIncluyeProfesional")));
            //buscarDocumentoRecepBean =(BuscarDocumentoRecepBean) ServletUtility.getInstancia().loadRequestAttribute(request, "buscarDocumentoRecepBean");
             buscarDocumentoRecepBean.setsCoDependencia(usuario.getCoDep());
             buscarDocumentoRecepBean.setsCoEmpleado(usuario.getCempCodemp());
             buscarDocumentoRecepBean.setsTiAcceso(usuarioConfigBean.getTiAcceso());
            
            
            
            String NumeroPagina = ServletUtility.getInstancia().loadRequestParameter(request, "NumeroPagina");
            String RegistrosPagina = ServletUtility.getInstancia().loadRequestParameter(request, "RegistrosPagina");
            String Orden = ServletUtility.getInstancia().loadRequestParameter(request, "Orden");
            buscarDocumentoRecepBean.setOrden(Orden);
            FiltroPaginate paginate=new FiltroPaginate();
            paginate.setNumeroPagina(Integer.parseInt(NumeroPagina));
            paginate.setRegistrosPagina(Integer.parseInt(RegistrosPagina));
            Result = recepDocumentoAdmService.getDocumentosBuscaRecepAdmLst(buscarDocumentoRecepBean,paginate);     
            
            buscarDocumentoRecepBean= new BuscarDocumentoRecepBean();
            paginate=new FiltroPaginate();
            usuario= new Usuario();
            usuarioConfigBean= new UsuarioConfigBean();
            objectg="";
             mapper = new ObjectMapper();
            
            //   Result = recepDocumentoAdmService.getDocumentosBuscaRecepAdm(buscarDocumentoRecepBean);
        } catch (Exception ex) { 
            Result.setIsSuccess(false);
            Result.setMessage(ex.getMessage());
        }
        
        return Result;
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEditarDocumento")
    public String goEditarDocumento(HttpServletRequest request, Model model){
       String mensaje = "NO_OK";
       String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       String snuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");        
       String scoPri = ServletUtility.getInstancia().loadRequestParameter(request, "pcoPri");
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String fechaHoraActual = Utility.getInstancia().dateToFormatString3(new Date());
       String fechaActual = Utility.getInstancia().dateToFormatString(new Date());
       DocumentoBean documentoBean;
       ExpedienteBean expedienteBean;
       List list;
       try{
           documentoBean = recepDocumentoAdmService.getDocumentoRecepAdm(snuAnn, snuEmi, snuDes);
           expedienteBean = recepDocumentoAdmService.getExpDocumentoRecepAdm(documentoBean.getNuAnnExp(),documentoBean.getNuSecExp());
           if(expedienteBean!=null){
            documentoBean.setNuExpediente(expedienteBean.getNuExpediente());
            documentoBean.setNuAnnExp(expedienteBean.getNuAnnExp());
            documentoBean.setNuSecExp(expedienteBean.getNuSecExp());
            documentoBean.setFeExpCorta(expedienteBean.getFeExpCorta());
            documentoBean.setCoProceso(expedienteBean.getCoProceso());
            documentoBean.setDeProceso(expedienteBean.getDeProceso());
           }
           documentoBean.setCoPri(scoPri);
           list = recepDocumentoAdmService.getDocumentosRefRecepAdm(snuAnn, snuEmi);
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
           model.addAttribute("pfechaActual",fechaActual);
           model.addAttribute("pcodEmp",usuario.getCempCodemp());
           model.addAttribute("pdesEmp",usuario.getDeFullName());
           model.addAttribute("documentoRecepBean",documentoBean);
           model.addAttribute("expedienteBean",expedienteBean);
           model.addAttribute("refRecepDocumAdmList",list);
           model.addAttribute("fileSizeMaxCargo",applicationProperties.getFileSizeMaxCargo());
           
           list=null;
           documentoBean= new DocumentoBean();
           expedienteBean= new ExpedienteBean();
           usuario=new Usuario();
           
           mensaje = "OK";
       }catch(Exception ex){
          logger.error(snuAnn+":"+ snuEmi+":"+snuDes,ex);            
          mensaje = "Error en Editar Documento"; 
       }
       if (mensaje.equals("OK")) {
           return "/recepDocumentosAdm/editRecepDocumAdm";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
   
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdDocumento")
    public @ResponseBody String goUpdDocumento(DocumentoBean documentoBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        documentoBean.setCodUseMod(usuario.getCoUsuario());
        documentoBean.setCoEmpRec(usuario.getCempCodemp());
        String sAccion = ServletUtility.getInstancia().loadRequestParameter(request, "vAccion");
        String sEsDocAnu = ServletUtility.getInstancia().loadRequestParameter(request, "vEsDocAnu");
        sEsDocAnu = sEsDocAnu.equals("") ? "A" : sEsDocAnu;
        String coRespuesta;
        String deRespuesta;
        
        try{
            mensaje = recepDocumentoAdmService.updDocumentoRecepAdm(documentoBean,sAccion,sEsDocAnu,usuario);
            //mensaje = recepDocumentoAdmService.updDocumentoRecepAdm(documentoBean,sAccion,usuario); servicio rest notificaciones movil
        }catch(validarDatoException e){
           mensaje=(e.valorMsg);
        }catch (Exception e) { 
           e.printStackTrace(); 
        }         
        
        if (mensaje.equals("OK")) {
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
        retval.append("\",\"nuCorDes\":\"");
        retval.append(documentoBean.getNuCorDes());  
        retval.append("\",\"esDocRec\":\"");
        retval.append(documentoBean.getEsDocRec());
        retval.append("\",\"deEsDocRec\":\"");
        retval.append(documentoBean.getDeEsDocDes());
        retval.append("\"}");
        
        
        documentoBean= new DocumentoBean();
        usuario=new Usuario();
        
        return retval.toString();         
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaEmpleado")
    public String goBuscaEmpleado(HttpServletRequest request, Model model){
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        model.addAttribute("listaEmpleado",referencedData.getListEmpleadoDependencia(usuario.getCoDep()));
        usuario= new Usuario();
        return "/modalGeneral/consultaEmpleado";
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaRemitente")
    public String goBuscaRemitente(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("listaRemitente",referencedData.getListRemitente(pnuAnn,usuarioConfigBean));
        usuarioConfigBean= new UsuarioConfigBean();
        return "/modalGeneral/consultaRemitente";
    } 

        @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestinatario")
    public String goBuscaDestinatario(HttpServletRequest request, Model model){
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);        
        model.addAttribute("listaDestinatario",referencedData.getListDestinatario(pnuAnn,usuarioConfigBean.getCoDep(),usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()));    
        usuarioConfigBean= new UsuarioConfigBean();
        return "/modalGeneral/consultaDestinatario";
    }
        
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDocumentoEnReferencia")
    public String goBuscaDocumentoEnReferencia(BuscarDocumentoRecepBean buscarDocumentoRecepBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        buscarDocumentoRecepBean.setsCoDependencia(usuario.getCoDep());
        buscarDocumentoRecepBean.setsCoEmpleado(usuario.getCempCodemp());
        buscarDocumentoRecepBean.setsTiAcceso(usuarioConfigBean.getTiAcceso());

        HashMap mp = null;
        List list = null;
        
        try{
            mp = recepDocumentoAdmService.getDocumentosEnReferencia(buscarDocumentoRecepBean);
            buscarDocumentoRecepBean=new BuscarDocumentoRecepBean();
            usuarioConfigBean= new UsuarioConfigBean();
            
        }catch(Exception ex){
            mensaje = ex.getMessage();
        } 
        mensaje = String.valueOf(mp.get("msjResult"));
        if(mensaje.equals("1")){
            list = (List) mp.get("recepDocumAdmList");
            if(list!=null){
                if (list.size()>=100){
                    model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
                }
                model.addAttribute("recepDocumAdmList",list);
                list = null;
                mensaje = "OK";                
            }
        }
        if (mensaje.equals("OK")) {
            return "/recepDocumentosAdm/tablaDocumentoAdm";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
        
      
     }        

    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerAtendido")
    public @ResponseBody String goVerAtendido(DocumentoBean documentoBean, HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");
                
        try{
            String vret  = recepDocumentoAdmService.getVerificaAtendido(pnuAnn, pnuEmi, pnuDes);
            if (vret==null || vret.equals("0")){
                mensaje = "OK";
            }else{
                mensaje = "NO_OK";
            }
        }catch (Exception e) { 
           e.printStackTrace(); 
        }         
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"retval\":\"");
        retval.append(mensaje);
        retval.append("\"}");

        return retval.toString();         
    }        
    @RequestMapping(method = RequestMethod.POST, params = "accion=goConsultaAtendido")
    public String goConsultaAtendido(BuscarDocumentoRecepBean buscarDocumentoRecepBean, HttpServletRequest request,  Model model){
        String mensaje = "NO_OK";
        
        String pnuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String pnuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        String pnuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");
        List<ReferenciaBean> listSeg = null;
                
        try{
            listSeg = recepDocumentoAdmService.getConsultaAtendido(pnuAnn, pnuEmi, pnuDes);
        }catch (Exception e) { 
           e.printStackTrace(); 
        }         
        
        model.addAttribute("listAtendido", listSeg);
        listSeg = null;
        return "/recepDocumentosAdm/consultaAtendido";
     }        
    
    
    
    LinkedList<DocumentoFileBean> files = new LinkedList<DocumentoFileBean>();
    DocumentoFileBean fileMeta = null;
    
    
      @RequestMapping(method = RequestMethod.POST, params = "accion=goUpload")    
    public @ResponseBody String goUpload(MultipartHttpServletRequest request, HttpServletResponse response)  {
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;
        
        pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "NuAnn");
        pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "NuEmi");
        pNuDes = ServletUtility.getInstancia().loadRequestParameter(request, "NuDes");
        /* System.out.println("holisss");
        System.out.println(pNuAnn);*/
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();
        
        String vreturn = "NO_OK";
        
       /* if (pkEmi != null) {
            pNuAnn = pkEmi.substring(0, 4);
            pNuEmi = pkEmi.substring(4, 14);
        }*/
        
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next());
            //System.out.println(mpf.getOriginalFilename() + " uploaded! " + files.size());
            //2.3 create new fileMeta
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());
            System.out.println(fileMeta.getNombreArchivo());
            try {
                fileMeta.setArchivoBytes(mpf.getBytes());
                //vreturn=anexoDocumentoService.insArchivoAnexo(coUsu,pNuAnn, pNuEmi, fileMeta);
               vreturn=documentoMensajesService.insArchivoAnexoDes(coUsu,pNuAnn, pNuEmi, pNuDes, fileMeta);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //2.4 add to files
            files.add(fileMeta);
        }

        // result will be like this
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
        
        String id=fileMeta.getIdDocumento();
        String nombreArchivo=fileMeta.getNombreArchivo();
        String tamanoArchivo=fileMeta.getTamanoArchivo();
        String tipoArchivo=fileMeta.getTipoArchivo();
        
        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\"}]";
        res=String.format(res, id,nombreArchivo,tamanoArchivo,tipoArchivo);
        return res;
        //return files;

    } 
      
}
