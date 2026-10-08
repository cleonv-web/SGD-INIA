package pe.gob.onpe.tramitedoc.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpServletRequest;
import org.apache.commons.collections.CollectionUtils;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
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
import pe.gob.onpe.tramitedoc.bean.AnnioBean;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.CiudadanoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.bean.TemaBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.AnexoDocumentoService;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.DocumentoBasicoService;
import pe.gob.onpe.tramitedoc.service.DocumentoExtRecepService;
import pe.gob.onpe.tramitedoc.service.DocumentoObjService;
import pe.gob.onpe.tramitedoc.service.DocumentoVoBoService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoAdmService;
import pe.gob.onpe.tramitedoc.service.NotificacionService;
import pe.gob.onpe.tramitedoc.service.RecepDocumentoAdmService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.service.TemaService;
import pe.gob.onpe.tramitedoc.util.Constantes;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

@Controller
@RequestMapping("/{version}/srDocumentoAdmEmision.do")
public class DocumentoAdmEmisionController {
    
    @Autowired
    private EmiDocumentoAdmService emiDocumentoAdmService;
    
    @Autowired
    private RecepDocumentoAdmService recepDocumentoAdmService;
    
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
    private DocumentoBasicoService documentoBasicoService;
     
    @Autowired
    private AnexoDocumentoService anexoDocumentoService;
    @Autowired
    private DocumentoObjService documentoObjService;
       
    
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        //BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String pEstado = ServletUtility.getInstancia().loadRequestParameter(request, "estadoDoc");    
        String pcoEstMensajeria = ServletUtility.getInstancia().loadRequestParameter(request, "coEstMensajeria");    
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String Codemp=usuario.getCempCodemp();
        
        BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        String sCoAnnio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        
        TemaBean temabean =  new TemaBean();
        temabean.setCoDependencia(usuario.getCoDep());
        List<TemaBean> tema = temaService.getListTema(temabean);
        
        if(pEstado.equals("0")){
        buscarDocumentoEmiBean.setsCoAnnio("Hoy");
        }
        else{
            buscarDocumentoEmiBean.setsCoAnnio(sCoAnnio);
        }
        if(pcoEstMensajeria.equals("3")){
          buscarDocumentoEmiBean.setsCoAnnio(sCoAnnio);
        }
        
        
        //buscarDocumentoEmiBean.setsCoAnnio(sCoAnnio);
        buscarDocumentoEmiBean.setsCoAnnioBus(sCoAnnio);
        buscarDocumentoEmiBean.setsEstadoDoc(pEstado);
        buscarDocumentoEmiBean.setsCoDependencia(codDependencia);
        buscarDocumentoEmiBean.setCoEstMensajeria(pcoEstMensajeria);
        
        model.addAttribute(buscarDocumentoEmiBean);
        buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        model.addAttribute("pEstado", pEstado);
        model.addAttribute("deListTema", tema);
        tema= null;
        model.addAttribute("deAnnioList",referencedData.getAnnioList());
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));        
        model.addAttribute("deListMensajeria",referencedData.getLstEstMensajeria("EST_ENV_DOC_MSJ"));
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(codDependencia));
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        model.addAttribute("deListFirmadoPor",referencedData.getFirmadoPorList(codDependencia));
        
        //YUAL
           //List<SiElementoBean> grpDepSalEspList=referencedData.grpElementoList("COD_DEP_SAL_ESP");
           SiElementoBean obj;
         
           int rlt=0;
           String a,b,c;  
           String vEsFirma="N";
           List<SiElementoBean> grpUserFirma=referencedData.grpElementoList("VIEW_FIR_DEP_USU");
           
           for(int i=0; i<grpUserFirma.size();i++)
           {
               obj= new SiElementoBean();
               obj=(SiElementoBean)grpUserFirma.get(i);
               a=obj.getCeleDesele().toString().trim();
               b=obj.getCeleDescor().toString().trim();
               try {
                   c=obj.getNeleNumsec().toString().trim();// "Firma Directa"
               }
               catch(Exception ex ){
                  c="";
               }               
               if(a.equals(codDependencia)&&b.equals(Codemp)&&c.equals("1"))
               {
                   vEsFirma="S";
               }
           }
           
          
           model.addAttribute("EsBtnFirmar",vEsFirma);
           if(pEstado==null)
            pEstado="";
           if(pcoEstMensajeria==null)
            pcoEstMensajeria="";
           
        if(!pEstado.toLowerCase().contains("script") && !pcoEstMensajeria.toLowerCase().contains("script")   ){
            return "/DocumentoAdmEmi/documentoAdmEmi"; 
        }
        else {
            return ""; 
        }
        
        
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
        
         //YUAL
        if(buscarDocumentoEmiBean.getCoEmpRec()!=null)
        {
        buscarDocumentoEmiBean.setsTrabDestino(buscarDocumentoEmiBean.getCoEmpRec());
        }
        
        List list = null;

        try{
      //      list = emiDocumentoAdmService.getDocumentosBuscaEmiAdm(buscarDocumentoEmiBean);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        if (list!=null) {
            if (list.size()>=100) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            model.addAttribute("emiDocumAdmList",list);
            mensaje = "OK";
            list = null;
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/DocumentoAdmEmi/tblDocAdmEmision";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }
    
      @RequestMapping(method = RequestMethod.POST, params = "accion=goListaBusqDocumEmiAdmJson",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProcessResult<List<DocumentoBean>> goListaBusqDocumEmiAdmJson(HttpServletRequest request, Model model) {
       
        String mensaje = "NO_OK";
        ProcessResult<List<DocumentoBean>> Result= new ProcessResult<List<DocumentoBean>>();
        StringBuilder retval = new StringBuilder();
        try {
            BuscarDocumentoEmiBean buscarDocumentoEmiBean= new BuscarDocumentoEmiBean();
            /*Map<String, Object> map = (Map<String,Object>)model;
            if(map!=null){
            buscarDocumentoRecepBean=(BuscarDocumentoRecepBean)map.get("buscarDocumentoRecepBean");
            }*/
            Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
            UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
  
           /* String busNroRuc = ServletUtility.getInstancia().loadRequestParameter(request, "busNroRuc");
            String busRazonSocial = ServletUtility.getInstancia().loadRequestParameter(request, "busRazonSocial");*/
             String objectg = ServletUtility.getInstancia().loadRequestParameter(request, "buscarDocumentoEmiBean");
             ObjectMapper mapper = new ObjectMapper();
             buscarDocumentoEmiBean=mapper.readValue(objectg, BuscarDocumentoEmiBean.class);
             
             buscarDocumentoEmiBean.setEsIncluyeOficina(Boolean.valueOf(ServletUtility.getInstancia().loadRequestParameter(request, "EsIncluyeOficina")));
             buscarDocumentoEmiBean.setEsIncluyeProfesional(Boolean.valueOf(ServletUtility.getInstancia().loadRequestParameter(request, "EsIncluyeProfesional")));
            //buscarDocumentoRecepBean =(BuscarDocumentoRecepBean) ServletUtility.getInstancia().loadRequestAttribute(request, "buscarDocumentoRecepBean");
             buscarDocumentoEmiBean.setsCoDependencia(usuario.getCoDep());
             buscarDocumentoEmiBean.setsCoEmpleado(usuario.getCempCodemp());
             buscarDocumentoEmiBean.setsTiAcceso(usuarioConfigBean.getTiAcceso());
            
            
            
            String NumeroPagina = ServletUtility.getInstancia().loadRequestParameter(request, "NumeroPagina");
            String RegistrosPagina = ServletUtility.getInstancia().loadRequestParameter(request, "RegistrosPagina");
            String Orden = ServletUtility.getInstancia().loadRequestParameter(request, "Orden");
            buscarDocumentoEmiBean.setOrden(Orden);
            FiltroPaginate paginate=new FiltroPaginate();
            paginate.setNumeroPagina(Integer.parseInt(NumeroPagina));
            paginate.setRegistrosPagina(Integer.parseInt(RegistrosPagina));
            Result = emiDocumentoAdmService.getDocumentosBuscaEmiAdmList(buscarDocumentoEmiBean,paginate);     
             
        } catch (Exception ex) { 
            Result.setIsSuccess(false);
            Result.setMessage(ex.getMessage());
        }
        
        return Result;
    }
    
    
    
    
      
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaSIGLADependencia")
    public @ResponseBody String goBuscaSIGLADependencia(HttpServletRequest request, Model model){
        String vRespuesta;
        String sigla;
        StringBuilder retval = new StringBuilder();
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pCodDep");
        
       
        DependenciaBean depden= new DependenciaBean();
        depden = referencedData.getDatosDependencia(pcoDepen);
        
        sigla = depden.getDeSigla();
        retval.append("{\"sigla\":\"");
        retval.append(sigla);
        retval.append("\"}");
               
        return retval.toString(); 
    }
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goNuevoDocumentoEmi")
    public String goNuevoDocumentoEmi(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String nuAnn = Utility.getInstancia().dateToFormatStringYYYY(new Date());
       String fechaActual = Utility.getInstancia().dateToFormatString(new Date());
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       String codDependencia = usuario.getCoDep();
       String Codemp=usuario.getCempCodemp();
       
       DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
       try{
           String esDocEmi = "5";
           documentoEmiBean = emiDocumentoAdmService.getDocumentoEmiAdmNew(esDocEmi,codDependencia);//estadoDocEmi, codigo de dependencia
           documentoEmiBean.setEsDocEmi(esDocEmi);
           documentoEmiBean.setCoDepEmi(codDependencia);
           documentoEmiBean.setCoLocEmi(usuario.getCoLocal());
           documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
           documentoEmiBean.setDeEmpRes(usuario.getDeFullName());
           documentoEmiBean.setFeEmiCorta(fechaActual);
           documentoEmiBean.setTiEmi("01");
           documentoEmiBean.setNuDiaAte("0");
           documentoEmiBean.setNuAnn(nuAnn);
           /*interoperabilidad*/
           documentoEmiBean.setTiEnvMsj("-1");
           model.addAttribute("pDatosOblDesJur",commonQryService.obtenerValorParametro("DATOS_OBL_DES_JUR"));
           /*interoperabilidad*/
           /*model.addAttribute("snuAnn","");
           model.addAttribute("snuEmi","");
           model.addAttribute("docEstadoMsj",documentoEmiBean.getDocEstadoMsj());
           model.addAttribute("esDocEmi",documentoEmiBean.getEsDocEmi());
           model.addAttribute("tiDest",documentoEmiBean.getTiDest());
           model.addAttribute("tiEnvMsj",documentoEmiBean.getTiEnvMsj());*/
            List<AnnioBean> annioList =referencedData.getAnnioList();
           if(annioList!=null && annioList.size()>0)
           {
               System.out.println("deAnnioList==> CON DATOS-"+String.valueOf(annioList.size()));
           }
           else {
               System.out.println("deAnnioList==>SIN DATA");
           }
           model.addAttribute("sEsNuevoDocAdm","1");
           model.addAttribute("sTipoDestEmi","01");
           model.addAttribute("deAnnioList",annioList);
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));           
           model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependencia(codDependencia));
           model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
           model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(codDependencia));
           model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));    
          
           model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());           
           model.addAttribute("pfechaHoraActual",fechaHoraActual);
           model.addAttribute("pcodEmp",usuario.getCempCodemp());
           model.addAttribute("pdesEmp",usuario.getDeFullName());
           usuario= new Usuario();
           model.addAttribute(documentoEmiBean);
           documentoEmiBean = new DocumentoEmiBean();
           model.addAttribute("inCreaExpediente",commonQryService.obtenerValorParametro("IN_CREA_EXPEDIENTE"));
           

            
           //YUAL
           DependenciaBean depden= new DependenciaBean();
           depden = referencedData.getDatosDependencia(codDependencia);
           model.addAttribute("siglaDependencia",depden.getDeSigla());
           depden= new DependenciaBean();
           
           List<SiElementoBean> grpDepOriEspList=referencedData.grpElementoList("COD_DEP_ORI_ESP");
          // List<DependenciaBean> lstDepOriEspList=referencedData.getListDependenciaDestinatarioEmi(usuario.getCoDep(),"");
           List<DependenciaBean> lstDepOri=new ArrayList<DependenciaBean>();
            DependenciaBean depdenGrp= new DependenciaBean();
           depdenGrp.setDeSigla("0");
           depdenGrp.setTituloDep("SELECCIONE");
           lstDepOri.add(0,depdenGrp);
           
           
                   
           String docDepGrp="";
           if(grpDepOriEspList!=null){
            for(int i=0; i<grpDepOriEspList.size();i++)
            {
               docDepGrp=grpDepOriEspList.get(i).getCeleDescor();
               depdenGrp=referencedData.getDatosDependencia(docDepGrp);
               lstDepOri.add(i+1,depdenGrp);
            }
           }
           
           //usuario.setInAccesoLocal("1");
                        
           model.addAttribute("listaDepOrigenSolicitante",lstDepOri);
           lstDepOri=new ArrayList<DependenciaBean>();
           
           List<SiElementoBean> grpDepSalEspList=referencedData.grpElementoList("COD_DEP_SAL_ESP");
           SiElementoBean obj;
         
           int rlt=0;
           String a,b;  
           if(grpDepSalEspList !=null){
            for(int i=0; i<grpDepSalEspList.size();i++)
            {
                obj= new SiElementoBean();
                obj=(SiElementoBean)grpDepSalEspList.get(i);
                a=obj.getCeleDescor().toString().trim();
                b=codDependencia.toString().trim();
                if(a.equals(b))
                {
                    rlt=1;
                }
            }
          }     
           if(rlt==1)
           {
               model.addAttribute("inDepSalEsp","1" );     
           }
           else
           { 
               model.addAttribute("inDepSalEsp","0" );     
           }
           grpDepSalEspList=null;
           
           String vEsFirma="N";
           List<SiElementoBean> grpUserFirma=referencedData.grpElementoList("VIEW_FIR_DEP_USU");
           if(grpUserFirma!=null)
           {
            for(int i=0; i<grpUserFirma.size();i++)
            {
                obj= new SiElementoBean();
                obj=(SiElementoBean)grpUserFirma.get(i);
                a=obj.getCeleDesele().toString().trim();
                b=obj.getCeleDescor().toString().trim();
                if(a.equalsIgnoreCase(codDependencia)&&b.equalsIgnoreCase(Codemp))
                {
                    vEsFirma="S";
                }
            }
           }
          
           model.addAttribute("coFirma",vEsFirma);
           System.out.println("deAnnioList==>OK");
           mensaje = "OK";
       }catch(Exception ex){
            ex.printStackTrace();
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoAdmEmi/nuevoDocumAdmEmi";
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
       String Codemp=usuario.getCempCodemp();
       DocumentoEmiBean documentoEmiBean;
       //ExpedienteBean expedienteBean;
       List listReferenciaDocAdmEmi;
       
       String vEsDoc="";
       String vEsFirma="";
       try{
           documentoEmiBean = emiDocumentoAdmService.getDocumentoEmiAdm(snuAnn,snuEmi);
           vEsDoc=documentoEmiBean.getEsDocEmi();
           //expedienteBean = emiDocumentoAdmService.getExpDocumentoEmitido(documentoEmiBean.getNuAnnExp(), documentoEmiBean.getNuSecExp());
           String stipoDestinatario = emiDocumentoAdmService.getTipoDestinatarioEmi(snuAnn, snuEmi);//obtener tipo de Destinatario
           if(stipoDestinatario != null){
               HashMap map = emiDocumentoAdmService.getLstDestintariotlbEmi(snuAnn,snuEmi);
               model.addAttribute("lstDestintarioDocAdmEmi",map.get("lst1"));
               model.addAttribute("lstDestintarioProveedorDocAdmEmi",map.get("lst2"));
               model.addAttribute("lstDestintarioCiudadanoDocAdmEmi",map.get("lst3"));
               model.addAttribute("lstDestintarioOtroOrigenDocAdmEmi",map.get("lst4"));
           }else{
              stipoDestinatario = "01"; 
           }
          /* model.addAttribute("snuAnn",snuAnn);
           model.addAttribute("snuEmi",snuEmi);
           model.addAttribute("docEstadoMsj",documentoEmiBean.getDocEstadoMsj());
           model.addAttribute("esDocEmi",documentoEmiBean.getEsDocEmi());
           model.addAttribute("tiDest",documentoEmiBean.getTiDest());
           model.addAttribute("tiEnvMsj",documentoEmiBean.getTiEnvMsj());*/
           model.addAttribute("sexisteDoc",sexisteDoc);
           model.addAttribute("sexisteAnexo",sexisteAnexo);
           
           model.addAttribute("sTipoDestEmi",stipoDestinatario);
           model.addAttribute("sEsNuevoDocAdm","0");
           
           listReferenciaDocAdmEmi = emiDocumentoAdmService.getLstDocumReferenciatblEmi(snuAnn,snuEmi);
           model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependencia(codDependencia));
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
           
           
           //model.addAttribute("pdNoFirmaProveido",emiDocumentoAdmService.getUsuarioNofirmaProveido(usuario.getCempCodemp()));
           
           model.addAttribute("lstReferenciaDocAdmEmi",listReferenciaDocAdmEmi);
           listReferenciaDocAdmEmi= null;
           model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
           model.addAttribute("lstMotivoDestinatario",referencedData.getLstMotivoDestinatario(documentoEmiBean.getCoDepEmi(),documentoEmiBean.getCoTipDocAdm()));
           model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(documentoEmiBean.getCoDepEmi()));
           model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(documentoEmiBean.getCoDepEmi()));
           List<EmpleadoVoBoBean> lsEmpVobo=commonQryService.getLsPersonalVoBo(snuAnn,snuEmi);
           model.addAttribute("lstEmpVoBoDocAdm",lsEmpVobo);
           model.addAttribute("lstNotiDocAdmEmi",emiDocumentoAdmService.getNotificaciones(lsEmpVobo,documentoEmiBean.getInFirmaAnexo()));
           lsEmpVobo= null;
           documentoEmiBean= new DocumentoEmiBean();
                   
//model.addAttribute("refRecepDocumAdmList",list);
           model.addAttribute("inCreaExpediente",commonQryService.obtenerValorParametro("IN_CREA_EXPEDIENTE"));
           
           
           //YUAL
           DependenciaBean depden= new DependenciaBean();
           depden = referencedData.getDatosDependencia(codDependencia);
           model.addAttribute("siglaDependencia",depden.getDeSigla());
           
           List<SiElementoBean> grpDepOriEspList=referencedData.grpElementoList("COD_DEP_ORI_ESP");
          // List<DependenciaBean> lstDepOriEspList=referencedData.getListDependenciaDestinatarioEmi(usuario.getCoDep(),"");
           List<DependenciaBean> lstDepOri=new ArrayList<DependenciaBean>();
           DependenciaBean depdenGrp= new DependenciaBean();
           depdenGrp.setDeSigla("0");
           depdenGrp.setTituloDep("SELECCIONE");
           lstDepOri.add(0,depdenGrp);
              
           String docDepGrp="";
           for(int i=0; i<grpDepOriEspList.size();i++)
           {
              docDepGrp=grpDepOriEspList.get(i).getCeleDescor();
              depdenGrp=referencedData.getDatosDependencia(docDepGrp);
              lstDepOri.add(i+1,depdenGrp);
           }
                        
           model.addAttribute("listaDepOrigenSolicitante",lstDepOri);
           
           
           List<SiElementoBean> grpDepSalEspList=referencedData.grpElementoList("COD_DEP_SAL_ESP");
           SiElementoBean obj;
         
           int rlt=0;
           String a,b;  
          
           for(int i=0; i<grpDepSalEspList.size();i++)
           {
               obj= new SiElementoBean();
               obj=(SiElementoBean)grpDepSalEspList.get(i);
               a=obj.getCeleDescor().toString().trim();
               b=codDependencia.toString().trim();
               if(a.equals(b))
               {
                   rlt=1;
           }
           }
                   
           if(rlt==1)
           {
           model.addAttribute("inDepSalEsp","1" );     
           }
           else
           { model.addAttribute("inDepSalEsp","0" );     
           }          
           
           

           /*interoperabilidad*/           
           model.addAttribute("pDatosOblDesJur",commonQryService.obtenerValorParametro("DATOS_OBL_DES_JUR"));
           /*interoperabilidad*/

     
  

           
           /*inicio mvaldera*/
           String rutaBase = "";
           ServletContext sc = request.getSession().getServletContext();
           rutaBase = sc.getRealPath("/reports/");
           DocumentoVerBean docVerBean = new DocumentoVerBean();
          
           
           
           List<SiElementoBean> grpUserFirma=referencedData.grpElementoList("VIEW_FIR_DEP_USU");
           
           for(int i=0; i<grpUserFirma.size();i++)
           {
               obj= new SiElementoBean();
               obj=(SiElementoBean)grpUserFirma.get(i);
               a=obj.getCeleDesele().toString().trim();
               b=obj.getCeleDescor().toString().trim();
               if(a.equalsIgnoreCase(codDependencia)&&b.equalsIgnoreCase(Codemp))
               {
                   vEsFirma="S";
               }
           }
           DocumentoAnexoBean oAnexoR;
          List<DocumentoAnexoBean> docAnexoList = documentoBasicoService.getAnexosList(snuAnn, snuEmi);
           if (vEsDoc.equals("7")&&vEsFirma.equalsIgnoreCase("S"))
           {
               docVerBean = documentoObjService.getNombreDocEmiReporte(snuAnn, snuEmi, "5",usuario,rutaBase);           
               model.addAttribute("urlDoc",docVerBean.getUrlDocumento());
        
                 //yUAL ANEXOS      
           
       
            String retval = " ";
          List<ReferenciaBean> listRefRoot;
                 listRefRoot = anexoDocumentoService.getReferenciaRootList(snuAnn, snuEmi,"1",50);
              String g="";   
           
           for(int i=1;i<listRefRoot.size();i++){
               g=listRefRoot.get(i).getCoTipDocAdm();
               if(!listRefRoot.get(i).getCoTipDocAdm().equals("232")){
               oAnexoR=  new DocumentoAnexoBean();
               oAnexoR.setDeDet(listRefRoot.get(i).getLiTipDoc()+" "+listRefRoot.get(i).getLiNuDoc());
               oAnexoR.setNuAnn(listRefRoot.get(i).getNuAnnRef());
               oAnexoR.setNuEmi(listRefRoot.get(i).getNuEmiRef());
               oAnexoR.setDeRutOri(listRefRoot.get(i).getDeAsu());
               oAnexoR.setNuAne("doc");
               docAnexoList.add(oAnexoR);
            }
           }
                 
      
           
           
           }
                      
           model.addAttribute("docAnexoList", docAnexoList);
           model.addAttribute("coFirma",vEsFirma);
           /*fin mvaldera*/
           

           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           if (vEsDoc.equals("7")&&vEsFirma.equalsIgnoreCase("S"))
            {
            return "/DocumentoAdmEmi/nuevoDocumAdmEmiFir";
            }
           else
           {
            return "/DocumentoAdmEmi/nuevoDocumAdmEmi";
           }
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
     @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscarEnlaceFirmado",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goBuscarEnlaceFirmado(HttpServletRequest request, Model model){
       String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String rutaBase = "";
       ServletContext sc = request.getSession().getServletContext();
       rutaBase = sc.getRealPath("/reports/");
           
       DocumentoVerBean docVerBean = new DocumentoVerBean();
          
       docVerBean = documentoObjService.getNombreDocEmiReporte(snuAnn, snuEmi, "5",usuario,rutaBase);           
       model.addAttribute("urlDoc",docVerBean.getUrlDocumento());
       return "respuesta";     
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
            mensaje = emiDocumentoAdmService.grabaDocumentoEmiAdm(trxDocumentoEmiBean,sCrearExpediente,usuario);
            //ProbarFrankSilva
           /* if (mensaje.equals("OK")) {
                String vReturn = notiService.procesarNotificacion(trxDocumentoEmiBean.getNuAnn(), trxDocumentoEmiBean.getNuEmi(), usuario.getCoUsuario());
                if (!vReturn.equals("OK")) {
                    throw new validarDatoException(vReturn);
                }
            }*/
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
        
        trxDocumentoEmiBean= new TrxDocumentoEmiBean();
        usuario= new Usuario();
        
        return retval.toString();        
    } 
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdTlbsDestinatario")
    public String goUpdTlbsDestinatario(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");        
       
       try{
            HashMap map = emiDocumentoAdmService.getLstDestintariotlbEmi(snuAnn,snuEmi);
            model.addAttribute("lstDestintarioDocAdmEmi",map.get("lst1"));
            model.addAttribute("lstDestintarioProveedorDocAdmEmi",map.get("lst2"));
            model.addAttribute("lstDestintarioCiudadanoDocAdmEmi",map.get("lst3"));
            model.addAttribute("lstDestintarioOtroOrigenDocAdmEmi",map.get("lst4"));
            model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocal());
            model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());
            
            map= null;
            
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoAdmEmi/actualizaTablasDestinoEmiDoc";
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
           model.addAttribute("lstReferenciaDocAdmEmi",emiDocumentoAdmService.getLstDocumReferenciatblEmi(snuAnn,snuEmi)); 
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
           model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(scoDependencia));
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoAdmEmi/tablaRefEmiDocAdm";
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaTrabDestino")
    public String goBuscaTrabDestino(HttpServletRequest request, Model model){
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);        
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("listaEmpleado",referencedData.getListEmpleadoDependencia(pcoDep));
    //    model.addAttribute("listaEmpleado",referencedData.getListEmpleadoDestino(pcoDep,usuarioConfigBean.getTiAcceso(),usuarioConfigBean.getCempCodemp()));
        
        return "/modalGeneral/consultaEmpleado";
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
            ciudadanoBean = emiDocumentoAdmService.getCiudadano(pnuDoc);
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
        
        ciudadanoBean= new CiudadanoBean();
        return retval.toString();
    }    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestOtroOrigenAgrega")
    public String goBuscaDestOtroOrigenAgrega(HttpServletRequest request, Model model){
        String pnomOtroOrigen = ServletUtility.getInstancia().loadRequestParameter(request, "pnoOtroOrigen");
        model.addAttribute("lsDestOtroOrigen",emiDocumentoAdmService.getLstOtrosOrigenesAgrega(pnomOtroOrigen));
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
        model.addAttribute("lsDestCiudadano",emiDocumentoAdmService.getLstCiudadano(pnoCiudadano));
        model.addAttribute("iniFuncionParm","1");
        return "/modalGeneral/consultaDestCiudadano";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDestProveedorAgrega")
    public String goBuscaDestProveedorAgrega(HttpServletRequest request, Model model){
        String prazonSocial = ServletUtility.getInstancia().loadRequestParameter(request, "prazonSocial");
        model.addAttribute("lsDestProveedor",emiDocumentoAdmService.getLstProveedoresAgrega(prazonSocial));
        model.addAttribute("iniFuncionParm","1");
        return "/modalGeneral/consultaDestProveedor";
    }        
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaPersonalDestinatario")
    public String goBuscaPersonalDestinatario(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String pcoDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepen");
        List list = null;
        try{
            list = emiDocumentoAdmService.getPersonalDestinatario(pcoDepen);
        }catch(Exception ex){
             mensaje = ex.getMessage();
        }
        if (list!=null) {
            model.addAttribute("listaEmpleadoDestEmi",list);
            mensaje = "OK";
            list=null;
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
            list = emiDocumentoAdmService.getLstMotivoxTipoDocumento(pcoDepen,pcoTipDoc);
        }catch(Exception ex){
             mensaje = ex.getMessage();
        }
        if (list!=null) {
            model.addAttribute("listaAccionDestEmi",list);
            mensaje = "OK";
            list=null;
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
           empleadoBean = emiDocumentoAdmService.getEmpleadoLocaltblDestinatario(pcoDepen);
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
                empleadoBean= new EmpleadoBean();
                
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
        String pcoEmpEmi="";
        if(usuarioConfigBean.getTiAcceso().equals("1")){//acceso personal
            pcoEmpEmi = usuario.getCempCodemp();
        }
        String pcoDepen = usuario.getCoDep();      
        List list = null;
        try{
            if(ptiBusqueda.equals("emi")){ // doc emitido
                list = emiDocumentoAdmService.getLstDocEmitidoRef(pcoEmpEmi,pcoDepen,pannio,ptiDoc,pnuDoc);                
            }else if(ptiBusqueda.equals("rec")){ // doc recepcionado
                list = emiDocumentoAdmService.getLstDocRecepcionadoRef(pcoDepen,pannio,ptiDoc,pnuDoc,usuarioConfigBean.getInMesaPartes());
            }
        }catch(Exception ex){
             mensaje = ex.getMessage();
        }
        
        usuarioConfigBean= new UsuarioConfigBean();
        if (list!=null) {
            if (list.size()>=200) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }            
            model.addAttribute("lstDocReferenciaEmi",list);
            list=null;
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
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaDocumentotblReferenciaPerso")
    public String goBuscaDocumentotblReferenciaPerso(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";
        String pannio = ServletUtility.getInstancia().loadRequestParameter(request, "pannio");
        String ptiDoc = ServletUtility.getInstancia().loadRequestParameter(request, "ptiDoc");
        String pnuDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDoc");
        String ptiBusqueda = ServletUtility.getInstancia().loadRequestParameter(request, "ptiBusqueda");// rec doc recepcionado, emi doc emitido
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);        
        String pcoEmpEmi="";
        if(usuarioConfigBean.getTiAcceso().equals("1")){//acceso personal
            pcoEmpEmi = usuario.getCempCodemp();
        }
        String pcoDepen = usuario.getCoDep();      
        List list = null;
        try{
            if(ptiBusqueda.equals("emi")){ // doc emitido
                list = emiDocumentoAdmService.getLstDocEmitidoRef(pcoEmpEmi,pcoDepen,pannio,ptiDoc,pnuDoc);                
            }else if(ptiBusqueda.equals("rec")){ // doc recepcionado
                list = emiDocumentoAdmService.getLstDocRecepcionadoRef(pcoDepen,pannio,ptiDoc,pnuDoc,usuarioConfigBean.getInMesaPartes());
            }
        }catch(Exception ex){
             mensaje = ex.getMessage();
        }
        if (list!=null) {
            if (list.size()>=200) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }            
            model.addAttribute("lstDocReferenciaEmi",list);
            list=null;
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        
        if (mensaje.equals("OK")) {
            return "/modalGeneral/consultaDocumentoRefEmiPersonal";
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
        usuario= new Usuario();
        List<MotivoBean> list = null;
        boolean bandera = false;
        try{
            list = emiDocumentoAdmService.getLstMotivoxTipoDocumento(pcoDepen,pcoTipDoc);
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
           list=null;
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
          mensaje = emiDocumentoAdmService.anularDocumentoEmiAdm(documentoEmiBean,usuario);
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

       retval = emiDocumentoAdmService.getLstDestintarioAgregaGrupo(scogrupo,pcoTipDoc);
           
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
                
                 mensaje = emiDocumentoAdmService.cargaDocEmi(docObjBean);
                 docObjBean = new DocumentoObjBean();
            }catch(Exception e){
                mensaje = "Error en Cargar Docuemntos";
                e.printStackTrace();
            }
       }else{
          mensaje = "Documento no encontrado";
       }
               
        usuario=new Usuario();
        
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
           mensaje = emiDocumentoAdmService.verificaNroDocumentoEmiDuplicado(documentoEmiBean);
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
                documentoEmiBean = emiDocumentoAdmService.getDocumentoEmiAdmNew(esDocEmi,codDependencia);//estadoDocEmi, codigo de dependencia
                documentoEmiBean.setEsDocEmi(esDocEmi);
                documentoEmiBean.setCoDepEmi(codDependencia);
                documentoEmiBean.setCoLocEmi(usuario.getCoLocal());
                documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
                documentoEmiBean.setDeEmpRes(usuario.getDeFullName());
                documentoEmiBean.setFeEmiCorta(fechaActual);
                documentoEmiBean.setTiEmi("01");
                documentoEmiBean.setNuDiaAte("0");
                documentoEmiBean.setNuAnn(nuAnn);
                documentoEmiBean.setDeAsu(documentoRecepBean.getDeAsu());
                documentoEmiBean.setNuExpediente(documentoRecepBean.getNuExpediente());
                documentoEmiBean.setNuAnnExp(documentoRecepBean.getNuAnnExp());
                documentoEmiBean.setNuSecExp(documentoRecepBean.getNuSecExp());
                documentoEmiBean.setFeExpCorta(documentoRecepBean.getFeExpCorta());
                documentoEmiBean.setCoProceso(documentoRecepBean.getCoProceso());
                documentoEmiBean.setDeProceso(documentoRecepBean.getDeProceso());
                //documentoEmiBean.setConfidencial(documentoRecepBean.getConfidencial());
                model.addAttribute("sEsNuevoDocAdm","1");
                model.addAttribute("sTipoDestEmi","01");
                model.addAttribute("deAnnioList",referencedData.getAnnioList());
                model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));           
                model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependencia(codDependencia));
                model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
                model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
                model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(documentoEmiBean.getCoDepEmi()));
                model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));            
                model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());           
                model.addAttribute("lstReferenciaDocAdmEmi",emiDocumentoAdmService.getLstDocumReferenciaAtiendeDeriva(documentoRecepBean)); 
                model.addAttribute("pfechaHoraActual",fechaHoraActual);
                model.addAttribute("pcodEmp",usuario.getCempCodemp());
                model.addAttribute("pdesEmp",usuario.getDeFullName());
                //YUAL
                model.addAttribute("coPrioridadRef",documentoRecepBean.getCoPri());
                model.addAttribute("pdNoFirmaProveido",emiDocumentoAdmService.getUsuarioNofirmaProveido(usuario.getCempCodemp()));
                model.addAttribute(documentoEmiBean);
                model.addAttribute("inCreaExpediente",commonQryService.obtenerValorParametro("IN_CREA_EXPEDIENTE"));
                
                
                model.addAttribute("listaDepOrigenSolicitante",referencedData.getListDependenciaDestinatarioEmi(usuario.getCoDep(),""));
                
                usuario= new Usuario();
                documentoEmiBean= new DocumentoEmiBean();
                documentoRecepBean= new DocumentoBean();
                mensaje = "OK";               
           }
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoAdmEmi/nuevoDocumAdmEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaElaboradoPorEdit")
    public String goBuscaElaboradoPorEdit(HttpServletRequest request, Model model){
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("listaEmpleado",emiDocumentoAdmService.getPersonalEditDocAdmEmision(pcoDep));
        return "/modalGeneral/consultaElaboradoPorEdit";
    }  
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goBuscaFirmadoPorEdit")
    public String goBuscaFirmadoPorEdit(HttpServletRequest request, Model model){
        String pcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDep");
        model.addAttribute("listaEmpleado",emiDocumentoAdmService.getPersonalEditDocAdmEmision(pcoDep));
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
          mensaje = emiDocumentoAdmService.changeToProyecto(documentoEmiBean,usuario);
          documentoEmiBean= new DocumentoEmiBean();
          
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
    
    //YUAL
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEditarDocumentoProveido")
    public String goEditarDocumentoProveido(HttpServletRequest request, Model model){
       String mensaje = "NO_OK";
       String snuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
       String snuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
       String snuDes = ServletUtility.getInstancia().loadRequestParameter(request, "pnuDes");        
       String pDepen = ServletUtility.getInstancia().loadRequestParameter(request, "pDepen");        
       
       Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
       String fechaActual = Utility.getInstancia().dateToFormatString(new Date());
       String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date());
       String codDependencia = usuario.getCoDep();
       
       DocumentoBean documentoRecepBean;
       ExpedienteBean expedienteBean;
       List list;
        documentoRecepBean = recepDocumentoAdmService.getDocumentoRecepAdm(snuAnn, snuEmi, snuDes);
        expedienteBean = recepDocumentoAdmService.getExpDocumentoRecepAdm(documentoRecepBean.getNuAnnExp(),documentoRecepBean.getNuSecExp());
           
           if(expedienteBean!=null){
            documentoRecepBean.setNuExpediente(expedienteBean.getNuExpediente());
            documentoRecepBean.setNuAnnExp(expedienteBean.getNuAnnExp());
            documentoRecepBean.setNuSecExp(expedienteBean.getNuSecExp());
            documentoRecepBean.setFeExpCorta(expedienteBean.getFeExpCorta());
            documentoRecepBean.setCoProceso(expedienteBean.getCoProceso());
            documentoRecepBean.setDeProceso(expedienteBean.getDeProceso());
           }
         
        //YUAL
        //  list = recepDocumentoAdmService.getDocumentosRefRecepAdm(snuAnn, snuEmi);
       
             
       
       
       DocumentoEmiBean documentoEmiBean = new DocumentoEmiBean();
       try{
        
                String esDocEmi = "5";
                documentoEmiBean = emiDocumentoAdmService.getDocumentoEmiAdmNew(esDocEmi,codDependencia);//estadoDocEmi, codigo de dependencia
                documentoEmiBean.setEsDocEmi(esDocEmi);
                documentoEmiBean.setCoDepEmi(codDependencia);
                documentoEmiBean.setCoLocEmi(usuario.getCoLocal());
                documentoEmiBean.setCoEmpRes(usuario.getCempCodemp());
                documentoEmiBean.setDeEmpRes(usuario.getDeFullName());
                documentoEmiBean.setFeEmiCorta(fechaActual);
                documentoEmiBean.setTiEmi("01");
                documentoEmiBean.setNuDiaAte("0");
                documentoEmiBean.setNuAnn(snuAnn);
                documentoEmiBean.setDeAsu(documentoRecepBean.getDeAsu());
              
                documentoEmiBean.setNuExpediente(documentoRecepBean.getNuExpediente());
                documentoEmiBean.setNuAnnExp(documentoRecepBean.getNuAnnExp());
                documentoEmiBean.setNuSecExp(documentoRecepBean.getNuSecExp());
                documentoEmiBean.setFeExpCorta(documentoRecepBean.getFeExpCorta());
                documentoEmiBean.setCoProceso(documentoRecepBean.getCoProceso());
                documentoEmiBean.setDeProceso(documentoRecepBean.getDeProceso());
                
                
                
                model.addAttribute("sEsNuevoDocAdm","1");
                model.addAttribute("sTipoDestEmi","01");
                model.addAttribute("deAnnioList",referencedData.getAnnioList());
                model.addAttribute("lstTipDocReferenciaEmi",referencedData.getLstTipDocReferencia(codDependencia));           
                model.addAttribute("lstTipDocDependencia",referencedData.listTipDocXDependencia(codDependencia));
                model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
                model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
                model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(documentoEmiBean.getCoDepEmi()));
                model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(codDependencia));            
                model.addAttribute("lstPrioridadDestEmi",referencedData.getLstPrioridadDestEmi());           
                model.addAttribute("lstReferenciaDocAdmEmi",emiDocumentoAdmService.getLstDocumReferenciaAtiendeDeriva(documentoRecepBean)); 
                model.addAttribute("pfechaHoraActual",fechaHoraActual);
                model.addAttribute("pcodEmp",usuario.getCempCodemp());
                model.addAttribute("pdesEmp",usuario.getDeFullName());
                model.addAttribute("pOficinaDestino",pDepen);
                DependenciaBean oDependenciaBean= referencedData.getDatosDependencia(pDepen);
                model.addAttribute("pOficinaDesDestino",oDependenciaBean.getTituloDep());
                model.addAttribute("pdNoFirmaProveido",emiDocumentoAdmService.getUsuarioNofirmaProveido(usuario.getCempCodemp()));
                model.addAttribute(documentoEmiBean);
                documentoEmiBean = new DocumentoEmiBean();
                model.addAttribute("inCreaExpediente",commonQryService.obtenerValorParametro("IN_CREA_EXPEDIENTE"));
                
                
                
                model.addAttribute("listaDepOrigenSolicitante",referencedData.getListDependenciaDestinatarioEmi(usuario.getCoDep(),""));
                EmpleadoBean empleadoBean  = null;
                empleadoBean = emiDocumentoAdmService.getEmpleadoLocaltblDestinatario(pDepen);
                
                DestinatarioDocumentoEmiBean oDestinatarioDocumentoEmiBean=new DestinatarioDocumentoEmiBean();
                oDestinatarioDocumentoEmiBean.setCoDependencia(oDependenciaBean.getCoDependencia());
                oDestinatarioDocumentoEmiBean.setDeDependencia(oDependenciaBean.getTituloDep());               
                oDestinatarioDocumentoEmiBean.setCoLocal(empleadoBean.getCoLocal());
                oDestinatarioDocumentoEmiBean.setDeLocal(empleadoBean.getDeLocal());
                oDestinatarioDocumentoEmiBean.setCoEmpleado(empleadoBean.getCempCodemp());
                oDestinatarioDocumentoEmiBean.setDeEmpleado(empleadoBean.getCompName());
                oDestinatarioDocumentoEmiBean.setCoTramite("3");
                oDestinatarioDocumentoEmiBean.setDeTramite("ACCIÓN NECESARIA");
                oDestinatarioDocumentoEmiBean.setCoPrioridad("1");
                oDestinatarioDocumentoEmiBean.setCoTipoDestino("01");
                oDestinatarioDocumentoEmiBean.setAccionBD("INS");
                
                
                HashMap map = new HashMap();
                List lst1 = new ArrayList();
                lst1.add(oDestinatarioDocumentoEmiBean);              
                 map.put("lst1", lst1);
                          
                model.addAttribute("lstDestintarioDocAdmEmi",map.get("lst1"));
        
                empleadoBean  = null;
                oDestinatarioDocumentoEmiBean=new DestinatarioDocumentoEmiBean();
                 
                mensaje = "OK";               
          
       }catch(Exception ex){ 
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoAdmEmi/nuevoDocumAdmEmiProveido";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }
       
       
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
          mensaje = emiDocumentoAdmService.changeToDespacho(documentoEmiBean,usuario);
          //ProbarFrankSilva
          if(mensaje.equals("OK")){
              //YUAL
              //String vReturn = notiService.procesarNotificacion(documentoEmiBean.getNuAnn(), documentoEmiBean.getNuEmi(), usuario.getCoUsuario());
              String vReturn = notiService.notificarVistoBueno(documentoEmiBean.getNuEmi(),documentoEmiBean.getNuAnn());
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
          mensaje = emiDocumentoAdmService.changeToEmitido(documentoEmiBean,docSession.getNoDocumento(),usuario,applicationProperties.getNroRucInstitu());
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
          mensaje = emiDocumentoAdmService.changeToEmitidoAlta(documentoEmiBean);
          
          
       
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
          mensaje = emiDocumentoAdmService.delDocumentoEmiAdm(documentoEmiBean,usuario);
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
           expedienteBean = emiDocumentoAdmService.getExpDocumentoEmitido(pnuAnnExp, pnuSecExp);
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
            map = emiDocumentoAdmService.getBuscaDependenciaEmite(usuarioConfigBean.getCoDep(),pdeDepEmite);
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
            mp = emiDocumentoAdmService.getDocumentosEnReferencia(buscarDocumentoEmiBean);
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
            return "/DocumentoAdmEmi/tblDocAdmEmision";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }      
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goCambiaDepEmi")
    private @ResponseBody String goCambiaDepEmi(HttpServletRequest request, Model model) throws Exception {
        StringBuffer retval = new StringBuffer();
        String pcoDepEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pcoDepEmi");
        DependenciaBean depEmi = emiDocumentoAdmService.cambiaDepEmi(pcoDepEmi);
        
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
           return "/DocumentoAdmEmi/tablaPersVoBoDocAdm";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }         
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAgregarPersVoboGrupo",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goAgregarPersVoboGrupo(HttpServletRequest request, Model model) throws Exception {
       String scogrupo = ServletUtility.getInstancia().loadRequestParameter(request, "pcogrupo");
       String retval;

       retval = emiDocumentoAdmService.getLstPersVoBoGrupo(scogrupo);
           
       return retval.toString();
    }    
    
    
    /*segdi mvaldera*/
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goAgregarDestinatarioExterno",produces = "text/plain; charset=utf-8")
    public @ResponseBody String goAgregarDestinatarioExterno(HttpServletRequest request, Model model) throws Exception {
//       String vRespuesta = "";
       String scogrupo = ServletUtility.getInstancia().loadRequestParameter(request, "pcogrupo");
       String pcoTipDoc = ServletUtility.getInstancia().loadRequestParameter(request, "pcoTipDoc");
       String ptiDes = ServletUtility.getInstancia().loadRequestParameter(request, "ptiDes");
       
//       String coRespuesta = "";
       String retval;

       retval = emiDocumentoAdmService.getLstDestExternoAgregaGrupo(scogrupo,ptiDes);
           
       return retval.toString();
    }
    
    /*segdi mvaldera*/
}
