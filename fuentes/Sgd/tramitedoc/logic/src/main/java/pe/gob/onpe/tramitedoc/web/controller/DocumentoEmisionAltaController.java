/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.web.controller;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoAdmService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoAltaService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoPersonalService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author wcutipa
 */
@Controller
@RequestMapping("/{version}/srDocumentoEmisionAlta.do")
public class DocumentoEmisionAltaController {

    @Autowired
    private EmiDocumentoAdmService emiDocumentoAdmService;

    @Autowired
    private ReferencedData referencedData;

    @Autowired
    private EmiDocumentoAltaService emiDocumentoAltaService;
    
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        //BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        String codDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        String pEstado = ServletUtility.getInstancia().loadRequestParameter(request, "estadoDoc");
        
        BuscarDocumentoEmiBean buscarDocumentoEmiBean = new BuscarDocumentoEmiBean();
        String sCoAnnio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        buscarDocumentoEmiBean.setsCoAnnio(sCoAnnio);
        buscarDocumentoEmiBean.setsCoAnnioBus(sCoAnnio);
        buscarDocumentoEmiBean.setsEstadoDoc(pEstado);
        buscarDocumentoEmiBean.setsCoDependencia(codDependencia);
        
        model.addAttribute(buscarDocumentoEmiBean);
        
        model.addAttribute("deAnnioList",referencedData.getAnnioList());
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocumentoEmi("TDTV_REMITOS"));
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(codDependencia));        
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(codDependencia));
        model.addAttribute("fechaActual", Utility.getInstancia().dateToFormatString(new Date()));
        model.addAttribute("annioActual", Utility.getInstancia().dateToFormatStringYYYY(new Date()));
        return "/DocumentoAdmEmi/documentoAltaEmi";
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
                list = emiDocumentoAdmService.getDocumentosBuscaEmiAdm(buscarDocumentoEmiBean);
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
            return "/DocumentoAdmEmi/tblDocAltaEmi";
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
           model.addAttribute("sEsNuevoDocAdm","1");
           model.addAttribute("sTipoDestEmi","01");
           model.addAttribute("deAnnioList",referencedData.getAnnioList());
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
           model.addAttribute(documentoEmiBean);
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoAdmEmi/nuevoDocumAltaEmi";
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
           documentoEmiBean = emiDocumentoAdmService.getDocumentoEmiAdm(snuAnn,snuEmi);
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
           
           model.addAttribute("lstReferenciaDocAdmEmi",listReferenciaDocAdmEmi);
           model.addAttribute("lstTipoDestinatarioEmi",referencedData.getTipoDestinatarioEmiList());
           model.addAttribute("lstGrupoDestinatario",referencedData.getGrupoDestinatario(codDependencia));
           model.addAttribute("lstMotivoDestinatario",referencedData.getLstMotivoDestinatario(documentoEmiBean.getCoDepEmi(),documentoEmiBean.getCoTipDocAdm()));
           model.addAttribute("lstDependenciaRemitenteEmi",referencedData.getLstDependenciaRemitenteEmi(documentoEmiBean.getCoDepEmi()));
           model.addAttribute("lstLocalRemitenteEmi",referencedData.getLstLocalRemitenteEmi(documentoEmiBean.getCoDepEmi()));
           //model.addAttribute("refRecepDocumAdmList",list);
           mensaje = "OK";
       }catch(Exception ex){
          mensaje = ex.getMessage(); 
       }
       if (mensaje.equals("OK")) {
           return "/DocumentoAdmEmi/nuevoDocumAltaEmi";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goUploadEmi")
    public @ResponseBody String goUploadEmi(MultipartHttpServletRequest request, HttpServletResponse response) {

        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;
        DocumentoFileBean fileMeta = null;
        String pNuAnn = null;
        String pNuEmi = null;

        pNuAnn = ServletUtility.getInstancia().loadRequestParameter(request, "pnuAnn");
        pNuEmi = ServletUtility.getInstancia().loadRequestParameter(request, "pnuEmi");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String coUsu=usuario.getCoUsuario();
        
        String vreturn = "NO_OK";
        
        while (itr.hasNext()) {

            mpf = request.getFile(itr.next());
            
            //2.3 create new fileMeta
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());

            try {
                fileMeta.setArchivoBytes(mpf.getBytes());
                vreturn=emiDocumentoAltaService.cargaDocumentoEmi(coUsu, pNuAnn, pNuEmi, fileMeta);
                
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
     
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
          mensaje = emiDocumentoAltaService.changeToProyecto(documentoEmiBean);
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
    
}
