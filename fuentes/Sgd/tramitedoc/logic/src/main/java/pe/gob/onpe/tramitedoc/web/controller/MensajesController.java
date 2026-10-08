/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.apache.commons.codec.binary.Base64;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoCargaMsjBean;
import pe.gob.onpe.tramitedoc.bean.DescargaMensajeBean;
import pe.gob.onpe.tramitedoc.bean.DocRespuestaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.ElementoMensajeroBean;
import pe.gob.onpe.tramitedoc.bean.MensajesConsulBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.DocumentoMensajesService;
import pe.gob.onpe.tramitedoc.service.MensajesData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author mvaldera
 */
@Controller
@RequestMapping("/{version}/srGestionMensajes.do")
@SessionAttributes(value = {"docSession"})
public class MensajesController {
  
  @Autowired
  private MensajesData mensajesData;
  
  @Autowired
  private ApplicationProperties applicationProperties;

  @Autowired
  private DocumentoMensajesService documentoMensajesService;
    
  @Autowired
  private CommonQryService commonQryService;   
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        
        String sCoAnnio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        BuscarDocumentoCargaMsjBean buscarDocumentoCargaMsjBean = new BuscarDocumentoCargaMsjBean();
        buscarDocumentoCargaMsjBean.setCoAnnio(sCoAnnio);
        buscarDocumentoCargaMsjBean.setCoEstadoDoc("2");
        buscarDocumentoCargaMsjBean.setCoAmbitoMsj(".: TODOS :.");//en registro
        buscarDocumentoCargaMsjBean.setCoTipoEnvMsj(".: TODOS :.");//en registro    
        buscarDocumentoCargaMsjBean.setCoTipoMsj(".: TODOS :.");//en registro    
        
        buscarDocumentoCargaMsjBean.setCoDependencia(codDependencia);
     
        model.addAttribute(buscarDocumentoCargaMsjBean);
        
        
        /*model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia)); */               
        /*model.addAttribute("deEmisorList",referencedData.getLstEmisorDocExtRecep());   */     
        model.addAttribute("deEstadosList",mensajesData.getLstEstadoMsj("TD_MENSAJERIA"));
        model.addAttribute("deAmbitosList",mensajesData.getLstAmbitoMsj("DE_AMBITO_MENS"));
        model.addAttribute("deTipoEnvList",mensajesData.getListTipoEnvMsj("RE_ENV_MSJ_MENS"));
        model.addAttribute("deOficinaList",mensajesData.getListOficina());        
        List listTipoMensajero = documentoMensajesService.getListTipoElementoMensajeria("DE_TIP_MSJ_MENS");
        model.addAttribute("deTipoMsjList",listTipoMensajero);
        //model.addAttribute("deTipoMsjList",mensajesData.getLstTipoMsj("DE_TIP_MSJ_MENS"));
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));  
                   
        return "/mpGestionMensajes/BuscarMensajes";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
    public String goInicio(BuscarDocumentoCargaMsjBean buscarDocumentoCargaMsjBean, HttpServletRequest request,  BindingResult result, Model model){
        String mensaje = "NO_OK";
        

        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String codDependencia = usuarioConfigBean.getCoDep();
        String codEmpleado = usuarioConfigBean.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiAccesoMp();
        buscarDocumentoCargaMsjBean.setCoDependencia(codDependencia);
        buscarDocumentoCargaMsjBean.setCoEmpleado(codEmpleado);
        buscarDocumentoCargaMsjBean.setTiAcceso(tipAcceso);
        buscarDocumentoCargaMsjBean.setCoDepEmi(usuarioConfigBean.getCoDepMp());
        buscarDocumentoCargaMsjBean.setInMesaPartes(usuarioConfigBean.getInMesaPartes());
        
        
        
        
        List list = null;

        try{
            list = documentoMensajesService.getBuscaDocumentosCarga(buscarDocumentoCargaMsjBean);
        }catch(Exception ex){
            mensaje = ex.getMessage();
        }

        
        
        
        if (list!=null) {
            if (list.size()>=50) {
                model.addAttribute("msjEmision","OPTIMICE SU CONSULTA");
            }
            model.addAttribute("docMensajeLst",list);
            mensaje = "OK";
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/mpGestionMensajes/tblMensajes";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
        
        
    }    
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDescargarMensaje")
    public String goDescargarMensaje(HttpServletRequest request, Model model) {
        String pnu_ann = ServletUtility.getInstancia().loadRequestParameter(request, "nu_ann");
        String pnu_emi = ServletUtility.getInstancia().loadRequestParameter(request, "nu_emi");
        String pnu_des = ServletUtility.getInstancia().loadRequestParameter(request, "nu_des");
        String pnu_msj = ServletUtility.getInstancia().loadRequestParameter(request, "nu_msj");
        String pfec_enviomsj = ServletUtility.getInstancia().loadRequestParameter(request, "fec_enviomsj");
        String pfec_pla = ServletUtility.getInstancia().loadRequestParameter(request, "fec_plazo");
        
      
        String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date()); 
        String estadoAneCargo =commonQryService.obtenerValorParametro("ANEXAR_CARGO")+"";
        DescargaMensajeBean descargaMensajeBean = new DescargaMensajeBean();
        MensajesConsulBean mensajesConsulBean = new MensajesConsulBean();
        
        descargaMensajeBean.setCo_EstadoDoc("0");//en registro  
        
        mensajesConsulBean = documentoMensajesService.getBuscaDocumentosMsj(pnu_ann,pnu_emi,pnu_des,pnu_msj);
        
        model.addAttribute(descargaMensajeBean);   
        
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("deEstadosList",mensajesData.getLstEstadoCarga("TD_MENSAJERIA"));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));              
        model.addAttribute("horaActual", Utility.getInstancia().dateToFormatStringHHmm(new Date()));     
        model.addAttribute("pfechaHoraActual",fechaHoraActual);
        model.addAttribute("pnu_ann",pnu_ann);
        model.addAttribute("pnu_emi",pnu_emi);
        model.addAttribute("pnu_des",pnu_des);
        model.addAttribute("pnu_msj",pnu_msj);
        model.addAttribute("pfec_enviomsj",pfec_enviomsj);
        model.addAttribute("pfe_pla",pfec_pla);
        model.addAttribute("pdias_pla_dev",mensajesConsulBean.getDias_pla_devo());
        model.addAttribute("anexar_cargo",estadoAneCargo.toUpperCase());
        model.addAttribute("deMotivoList",mensajesData.getLstMotivoMsj("MOTIVO_DEV_MSJ"));
        
        model.addAttribute("pde_TipoDoc",mensajesConsulBean.getDe_tip_doc());
        model.addAttribute("pde_TipoMsj",mensajesConsulBean.getDe_tip_msj());
        
        model.addAttribute("pPeEnvMsj",mensajesConsulBean.getPe_env_msj());
        
        model.addAttribute("pde_Asu",mensajesConsulBean.getDe_asu());
        model.addAttribute("pfe_emi",mensajesConsulBean.getFe_emi());
        model.addAttribute("pfec_recepmp",mensajesConsulBean.getFec_recepmp());
        model.addAttribute("pde_dependencia",mensajesConsulBean.getDe_dependencia());
        model.addAttribute("pde_destinatario",mensajesConsulBean.getDestinatario());
        model.addAttribute("pde_ambito",mensajesConsulBean.getDe_ambito());
        model.addAttribute("pde_tipomsj",mensajesConsulBean.getDe_tip_msj());
        model.addAttribute("pde_tipoenv",mensajesConsulBean.getDe_tip_env());
        model.addAttribute("pde_resenv",mensajesConsulBean.getRe_env_msj());
        
        
        model.addAttribute("pnu_Acta_Vis1",mensajesConsulBean.getNu_Acta_Vis1());
        model.addAttribute("pnu_Acta_Vis2",mensajesConsulBean.getNu_Acta_Vis2());
        model.addAttribute("pfe_Acta_Vis1",mensajesConsulBean.getFe_Acta_Vis1());
        model.addAttribute("pfe_Acta_Vis2",mensajesConsulBean.getFe_Acta_Vis2());
        model.addAttribute("es_Acta1_msj",mensajesConsulBean.getEs_Acta1_msj());
        model.addAttribute("es_Acta2_msj",mensajesConsulBean.getEs_Acta2_msj());
        
         model.addAttribute("parchivo_acta1",mensajesConsulBean.getArchivo_Acta1());
        model.addAttribute("parchivo_acta2",mensajesConsulBean.getArchivo_Acta2());
        
        model.addAttribute("fileSizeMaxCargo",applicationProperties.getFileSizeMaxCargo());
        
        /*
        model.addAttribute("pes_pen_msj",mensajesConsulBean.getEs_pen_msj());
        model.addAttribute("pobservacion",mensajesConsulBean.getOb_msj());
        model.addAttribute("pfe_en_de",mensajesConsulBean.getFe_ent_dev_msj());
        model.addAttribute("pho_en_de",mensajesConsulBean.getHo_ent_dev_msj());
        model.addAttribute("pfe_pla",mensajesConsulBean.getFe_pla_msj());*/
        
        return "/mpGestionMensajes/DescargarMensaje";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goConsDescargarMensaje")
    public String goConsDescargarMensaje(HttpServletRequest request, Model model) {
        String pnu_ann = ServletUtility.getInstancia().loadRequestParameter(request, "nu_ann");
        String pnu_emi = ServletUtility.getInstancia().loadRequestParameter(request, "nu_emi");
        String pnu_des = ServletUtility.getInstancia().loadRequestParameter(request, "nu_des");
        String pnu_msj = ServletUtility.getInstancia().loadRequestParameter(request, "nu_msj");
       // String pfec_enviomsj = ServletUtility.getInstancia().loadRequestParameter(request, "fec_enviomsj");

        String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date()); 
        
        MensajesConsulBean mensajesConsulBean = new MensajesConsulBean();
               
        mensajesConsulBean = documentoMensajesService.getBuscaDocumentosMsj(pnu_ann,pnu_emi,pnu_des,pnu_msj);
        /*System.out.println("demo17");
        System.out.println(mensajesConsulBean.getEst_msj());
        
        model.addAttribute(mensajesConsulBean);   */     
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("deEstadosList",mensajesData.getLstEstadoCarga("TD_MENSAJERIA"));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));              
        model.addAttribute("horaActual", Utility.getInstancia().dateToFormatStringHHmm(new Date()));     
        model.addAttribute("pfechaHoraActual",fechaHoraActual);
        model.addAttribute("pnu_ann",pnu_ann);
        model.addAttribute("pnu_emi",pnu_emi);
        model.addAttribute("pnu_des",pnu_des);
        model.addAttribute("pnu_msj",pnu_msj);
        model.addAttribute("pfec_enviomsj",mensajesConsulBean.getFec_enviomsj());
        model.addAttribute("pest_msj",mensajesConsulBean.getEst_msj());
        model.addAttribute("nro_guia_devolucion",mensajesConsulBean.getNro_guia_devolucion());
        model.addAttribute("pes_pen_msj",mensajesConsulBean.getEs_pen_msj());
        model.addAttribute("pes_pen_dev",mensajesConsulBean.getEs_pen_dev());
        model.addAttribute("pobservacion",mensajesConsulBean.getOb_msj());
        model.addAttribute("pfe_ent_msj",mensajesConsulBean.getFe_ent_msj());
        model.addAttribute("pho_ent_msj",mensajesConsulBean.getHo_ent_msj());
        model.addAttribute("pfe_dev_msj",mensajesConsulBean.getFe_dev_msj());
        model.addAttribute("pho_dev_msj",mensajesConsulBean.getHo_dev_msj());
        model.addAttribute("pfe_pla",mensajesConsulBean.getFe_pla_msj());
        System.out.println("Motivo==>>"+mensajesConsulBean.getMo_msj_dev());
        model.addAttribute("pmo_msj_dev",mensajesConsulBean.getMo_msj_dev());
        
        
        model.addAttribute("pnu_Acta_Vis1",mensajesConsulBean.getNu_Acta_Vis1());
        model.addAttribute("pnu_Acta_Vis2",mensajesConsulBean.getNu_Acta_Vis2());
        model.addAttribute("pfe_Acta_Vis1",mensajesConsulBean.getFe_Acta_Vis1());
        model.addAttribute("pfe_Acta_Vis2",mensajesConsulBean.getFe_Acta_Vis2());
        model.addAttribute("es_Acta1_msj",mensajesConsulBean.getEs_Acta1_msj());
        model.addAttribute("es_Acta2_msj",mensajesConsulBean.getEs_Acta2_msj());
        
         model.addAttribute("parchivo_acta1",mensajesConsulBean.getArchivo_Acta1());
        model.addAttribute("parchivo_acta2",mensajesConsulBean.getArchivo_Acta2());
        model.addAttribute("parchivo_Cargo",mensajesConsulBean.getArchivo_Cargo());
        
        
        model.addAttribute("tiene_cargo",mensajesConsulBean.getTieneanexocargo());
        
        model.addAttribute("pde_TipoDoc",mensajesConsulBean.getDe_tip_doc());
        model.addAttribute("pde_Asu",mensajesConsulBean.getDe_asu());
        model.addAttribute("pfe_emi",mensajesConsulBean.getFe_emi());
        model.addAttribute("pfec_recepmp",mensajesConsulBean.getFec_recepmp());
        
       // model.addAttribute("pho_",pfec_enviomsj);
        
        
        return "/mpGestionMensajes/ConsDescargarMensaje";
    }
    
    
    
    
    
    
    
    
    LinkedList<DocumentoFileBean> files = new LinkedList<DocumentoFileBean>();
    DocumentoFileBean fileMeta = null;
    
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUpdDescargaMsj")
     public String goUpdDescargarMensaje(@RequestBody DescargaMensajeBean descargaMensajeBean, HttpServletRequest request,Model model) throws ServletException, IOException{
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();
        descargaMensajeBean.setCo_usuario(coUsu);
      
       
        //System.out.println(""+descargaMensajeBean.getArchivo().toString());
        
       // File file=new File(descargaMensajeBean.getArchivo());
        
             
        /*MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("archivo");*/
        
        try {
            String respuesta = documentoMensajesService.updMensajeriaDocumento(descargaMensajeBean);
            
           /* fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(file.getName());
            fileMeta.setTamanoArchivo(file.length() / 1024 + " Kb");
            fileMeta.setTipoArchivo(Files.probeContentType(Paths.get(descargaMensajeBean.getArchivo())));
            
            
            try {
                 System.out.println("Archivos");
                System.out.println(descargaMensajeBean.getArchivo());
                //fileMeta.setArchivoBytes(Files.readAllBytes(Paths.get(descargaMensajeBean.getArchivo())));
                fileMeta.setArchivoBytes(getByte(descargaMensajeBean.getArchivo()));
                System.out.println("Archivos");
                respuesta=documentoMensajesService.insArchivoAnexoDes(coUsu,descargaMensajeBean.getNu_ann(), descargaMensajeBean.getNu_emi(), descargaMensajeBean.getNu_des(), fileMeta);
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //2.4 add to files
            files.add(fileMeta);*/
            
            mensaje = respuesta;
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        if (mensaje.equals("OK")) {
            model.addAttribute("pMensaje", "Datos guardados.");
            return "respuesta";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
        
    }
    

     
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUploadMsj")    
    public @ResponseBody String goUploadMsj(MultipartHttpServletRequest request, HttpServletResponse response) {
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;
        
        pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "NuAnn");
        pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "NuEmi");
        pNuDes = ServletUtility.getInstancia().loadRequestParameter(request, "NuDes");
        System.out.println("Hola mundoooo");
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
      
       @RequestMapping(method = RequestMethod.POST, params = "accion=goUploadActa")    
    public @ResponseBody String goUploadActa(MultipartHttpServletRequest request, HttpServletResponse response)  {
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        
        String pNuAnn = null;
        String pNuEmi = null;
        String pNuDes = null;
        String pnu_Acta_Vis = null;
        
        pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "NuAnn");
        pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "NuEmi");
        pNuDes = ServletUtility.getInstancia().loadRequestParameter(request, "NuDes");
        pnu_Acta_Vis = ServletUtility.getInstancia().loadRequestParameter(request, "nu_Acta_Vis");
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
                vreturn=documentoMensajesService.insArchivoActa(coUsu,pNuAnn, pNuEmi, pNuDes,pnu_Acta_Vis, fileMeta);
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
        
     
    private byte[] getByte(String path) {
    byte[] getBytes = {};
    try {
        File file = new File(path);
        getBytes = new byte[(int) file.length()];
        InputStream is = new FileInputStream(file);
        is.read(getBytes);
        is.close();
    } catch (FileNotFoundException e) {
        e.printStackTrace();
    } catch (IOException e) {
        e.printStackTrace();
    }
    return getBytes;
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDelete")
     public String goDelete(HttpServletRequest request,Model model) {
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();
        
        String pnu_ann = ServletUtility.getInstancia().loadRequestParameter(request, "nu_ann");
        String pnu_emi = ServletUtility.getInstancia().loadRequestParameter(request, "nu_emi");
        String pnu_des = ServletUtility.getInstancia().loadRequestParameter(request, "nu_des");
        //String pnu_msj = ServletUtility.getInstancia().loadRequestParameter(request, "nu_msj");

        DescargaMensajeBean descargaMensajeBean=new DescargaMensajeBean();
        
        descargaMensajeBean.setNu_ann(pnu_ann);
        descargaMensajeBean.setNu_emi(pnu_emi);
        descargaMensajeBean.setNu_des(pnu_des);
        /*System.out.println(descargaMensajeBean.getNu_ann());
        System.out.println(descargaMensajeBean.getNu_emi());
        System.out.println(descargaMensajeBean.getNu_des());*/
//        descargaMensajeBean.setNu_msj(pnu_msj);
        
        /*MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("archivo");*/
        
       // System.out.println(descargaMensajeBean.getArchivo());
        try {
            String respuesta = documentoMensajesService.deleteMsj(descargaMensajeBean);
           
            mensaje = respuesta;
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        if (mensaje.equals("OK")) {
            model.addAttribute("pMensaje", "Datos guardados.");
            return "respuesta";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
        
    }
     
    
    
     @RequestMapping(method = RequestMethod.POST, params = "accion=goEliminaMensaje")
     public String goEliminaMensaje(HttpServletRequest request,Model model) {
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();
        
        String pnu_ann = ServletUtility.getInstancia().loadRequestParameter(request, "nu_ann");
        String pnu_emi = ServletUtility.getInstancia().loadRequestParameter(request, "nu_emi");
        String pnu_des = ServletUtility.getInstancia().loadRequestParameter(request, "nu_des");
        String pnu_msj = ServletUtility.getInstancia().loadRequestParameter(request, "nu_msj");

        DescargaMensajeBean descargaMensajeBean=new DescargaMensajeBean();
        
        descargaMensajeBean.setNu_ann(pnu_ann);
        descargaMensajeBean.setNu_emi(pnu_emi);
        descargaMensajeBean.setNu_des(pnu_des);
        descargaMensajeBean.setNu_msj(pnu_msj);
        
        /*MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("archivo");*/
        
       // System.out.println(descargaMensajeBean.getArchivo());
        try {
            String respuesta = documentoMensajesService.delMensajeriaDocumento(descargaMensajeBean);
           
            mensaje = respuesta;
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        if (mensaje.equals("OK")) {
            model.addAttribute("pMensaje", "Datos guardados.");
            return "respuesta";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
        
    }
     
    @RequestMapping(method = RequestMethod.POST, params = "accion=goListaResponsable")
       public String goListaResponsable(HttpServletRequest request, Model model) {
        String mensaje = "NO_OK";
        String  sTipo= ServletUtility.getInstancia().loadRequestParameter(request, "vCodResposable");
        String vAmbito = ServletUtility.getInstancia().loadRequestParameter(request, "vCodAmbito");
        
        try { 
            
            List<ElementoMensajeroBean> tipoList = documentoMensajesService.getListResponsableMensajeria(sTipo,vAmbito);
           
            model.addAttribute("listResponsable", tipoList);
        } catch (Exception ex) {
            mensaje = ex.getMessage();
            
        }
        return "/mpGestionMensajes/ResponsableListado";
    }
    

    @RequestMapping(method = RequestMethod.POST, params = "accion=goExportarArchivoLista")
    public @ResponseBody String goExportarArchivoLista(BuscarDocumentoCargaMsjBean buscarDocumentoCargaMsjBean,HttpServletRequest request,
            BindingResult result, Model model) throws IOException
    {
        ServletContext sc = request.getSession().getServletContext();
        
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        
        String codDependencia = usuario.getCoDep();
        String codEmpleado = usuario.getCempCodemp();
        String tipAcceso = usuarioConfigBean.getTiConsulta();
        String anio = ServletUtility.getInstancia().loadRequestParameter(request, "coAnnio");
        String rutaReporte=sc.getRealPath("/reports/");
//        String rutaImagen = sc.getRealPath("/reports/logo_onpe.jpg");
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        
        String coReporte = ServletUtility.getInstancia().loadRequestParameter(request, "pformatRepor");
        
        Map parametros = new HashMap();
        parametros.put("P_DE_DEPENDENCIA", usuarioConfigBean.getDeDep());
        parametros.put("P_USER", usuario.getCoUsuario());
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        
        buscarDocumentoCargaMsjBean.setCoDependencia(codDependencia);
        buscarDocumentoCargaMsjBean.setCoEmpleado(codEmpleado);
        buscarDocumentoCargaMsjBean.setTiAcceso(tipAcceso);
        buscarDocumentoCargaMsjBean.setCoAnnio(anio);    
        buscarDocumentoCargaMsjBean.setRutaReporteJasper(rutaReporte);
        buscarDocumentoCargaMsjBean.setFormatoReporte(coReporte);
        
        ReporteBean objReporteBean = documentoMensajesService.getGenerarReporte(buscarDocumentoCargaMsjBean, parametros);
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
       
    @RequestMapping(method = RequestMethod.POST, params = "accion=goObtieneActaMsj")
    private @ResponseBody String goObtieneActaMsj(HttpServletRequest request, Model model) throws Exception {
        String retval = "";
        DocRespuestaBean retRespuesta = new DocRespuestaBean();
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);

        String pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "nuAnn");
        String pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "nuEmi");
        String pNuDes = ServletUtility.getInstancia().loadRequestParameter(request, "nuDes");
        String pnuActa = ServletUtility.getInstancia().loadRequestParameter(request, "nuActa");
        String pnombreArchivo = ServletUtility.getInstancia().loadRequestParameter(request, "nombreArchivo");

        
        
        if (pNuAnn != null && pNuEmi != null && pNuDes != null) {
            try {

                DocumentoVerBean docVerBean = new DocumentoVerBean();
                docVerBean = documentoMensajesService.getNombreActaMsj(pNuAnn, pNuEmi, pNuDes,pnuActa,pnombreArchivo);

                retRespuesta.setRetval(docVerBean.getDeMensaje());
                retRespuesta.setNuAnn(docVerBean.getNuAnn());
                retRespuesta.setNuEmi(docVerBean.getNuEmi());
              //  retRespuesta.setNuAne(docVerBean.getNuAne());
                retRespuesta.setNoDoc(docVerBean.getNoDocumento());
                retRespuesta.setNoUrl(docVerBean.getUrlDocumento());
               
            } catch (Exception e) {
                retRespuesta.setRetval("Error en Obtener nombre del Anexo");
                e.printStackTrace();
            }
            try {
                Writer strWriter = new StringWriter();
                ObjectMapper mapper = new ObjectMapper();
                mapper.writeValue(strWriter, retRespuesta );
                retval = strWriter.toString();   
            } catch (Exception e) {
                retval = "";
            }
            

        }
        return retval;
    }
        
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDescargarMensajeDirecto")
    public String goDescargarMensajeDirecto(HttpServletRequest request, Model model) {
        String pnu_ann = ServletUtility.getInstancia().loadRequestParameter(request, "nu_ann");
        String pnu_emi = ServletUtility.getInstancia().loadRequestParameter(request, "nu_emi");
        String pnu_des = "0";
        String pnu_msj = "0";
        String pfec_enviomsj = Utility.getInstancia().dateToFormatString(new Date());
        String pfec_pla = Utility.getInstancia().dateToFormatString(new Date());
        
        
        String fechaHoraActual = Utility.getInstancia().dateToFormatString2(new Date()); 
        
        DescargaMensajeBean descargaMensajeDirectoBean = new DescargaMensajeBean();
        descargaMensajeDirectoBean.setCo_EstadoDoc("0");//en registro           
        model.addAttribute(descargaMensajeDirectoBean);        
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("deEstadosList",mensajesData.getLstEstadoCarga("TD_MENSAJERIA"));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));              
        model.addAttribute("horaActual", Utility.getInstancia().dateToFormatStringHHmm(new Date()));     
        model.addAttribute("pfechaHoraActual",fechaHoraActual);
        model.addAttribute("pnu_ann",pnu_ann);
        model.addAttribute("pnu_emi",pnu_emi);
        model.addAttribute("pnu_des",pnu_des);
        model.addAttribute("pnu_msj",pnu_msj);
        model.addAttribute("pfec_enviomsj",pfec_enviomsj);
        model.addAttribute("pfe_pla",pfec_pla);
        System.out.println(pnu_ann);
        return "/mpGestionMensajes/DescargarMensajeDirecto";
    }
    
         @RequestMapping(method = RequestMethod.POST, params = "accion=goRevertirMensaje")
     public String goRevertirMensaje(HttpServletRequest request,Model model) {
        String mensaje = "NO_OK";
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();
        
        String pnu_ann = ServletUtility.getInstancia().loadRequestParameter(request, "nu_ann");
        String pnu_emi = ServletUtility.getInstancia().loadRequestParameter(request, "nu_emi");
        String pnu_des = ServletUtility.getInstancia().loadRequestParameter(request, "nu_des");
        String pnu_msj = ServletUtility.getInstancia().loadRequestParameter(request, "nu_msj");

        DescargaMensajeBean descargaMensajeBean=new DescargaMensajeBean();
        
        descargaMensajeBean.setNu_ann(pnu_ann);
        descargaMensajeBean.setNu_emi(pnu_emi);
        descargaMensajeBean.setNu_des(pnu_des);
        descargaMensajeBean.setNu_msj(pnu_msj);
        
        /*MultipartHttpServletRequest multipartRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartRequest.getFile("archivo");*/
        
       // System.out.println(descargaMensajeBean.getArchivo());
        try {
            String respuesta = documentoMensajesService.revMensajeriaDocumento(descargaMensajeBean);
           
            mensaje = respuesta;
        } catch (Exception ex) {
            mensaje = ex.getMessage();
        }
        if (mensaje.equals("OK")) {
            model.addAttribute("pMensaje", "Datos guardados.");
            return "respuesta";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
        
    }
     
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goCalFechaPlazo")
    public @ResponseBody String goCalFechaPlazo(HttpServletRequest request, Model model){
        String mensaje = "NO_OK";  
        
        String coRespuesta; 
       
        String pfechaent = ServletUtility.getInstancia().loadRequestParameter(request, "pfechaent");
        String pdipladev = ServletUtility.getInstancia().loadRequestParameter(request, "pdipladev");

        
        DescargaMensajeBean descargaMensajeBean=new DescargaMensajeBean();
        
        descargaMensajeBean.setFe_ent_msj(pfechaent);
        descargaMensajeBean.setDi_pla_dev(pdipladev);

                
       try{ 
           mensaje = documentoMensajesService.selectCalFechaPlazo(descargaMensajeBean);            
           
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
        
        if (mensaje.equals("OK")) {
            coRespuesta = "1";
        }else{
            coRespuesta = "0";
        }     
        
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deFechaDev\":\"");
        retval.append(descargaMensajeBean.getFe_pla_dev());  
        retval.append("\"");
        retval.append(""); 
        retval.append("}");
        
        return retval.toString(); 
        
    }
       
}
