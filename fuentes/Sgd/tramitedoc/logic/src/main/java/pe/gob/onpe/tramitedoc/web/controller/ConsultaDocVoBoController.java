/*
 * To change this template, choose Tools | Templates
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
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.ConsultaDocVoBoService;
import pe.gob.onpe.tramitedoc.service.ConsultaEmiDocService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
@Controller
@RequestMapping("/{version}/srConsultaDocVobo.do")
public class ConsultaDocVoBoController {
     
    @Autowired
    private ReferencedData referencedData;     
    
    @Autowired
    private ConsultaDocVoBoService consultaDocVoBoService;         
    
    @Autowired
    private ConsultaEmiDocService consultaEmiDocService;   
    
    @Autowired
    private CommonQryService commonQryService;      
    
    @RequestMapping(method = RequestMethod.GET, params = "accion=goInicio")
    public String goInicio(HttpServletRequest request, Model model){
        String coDep = ServletUtility.getInstancia().loadRequestParameter(request, "coDep");
        //String coEsDoc = ServletUtility.getInstancia().loadRequestParameter(request, "coEsDoc");
        BuscarDocumentoVoBoBean bDocVoBo = new BuscarDocumentoVoBoBean();
        String annio = Utility.getInstancia().dateToFormatStringYYYY(new Date());
        bDocVoBo.setNuAnn(annio);
        bDocVoBo.setCoDepUsu(coDep);
        //bDocVoBo.setEsDoc(coEsDoc);
        model.addAttribute(bDocVoBo);
        
        model.addAttribute("deEstadosList",referencedData.getLstEstadosDocVoBo("TDTV_PERSONAL_VB"));
        model.addAttribute("dePrioridadesList",referencedData.getPrioridadesDocumentoList());
        model.addAttribute("deTipoDocumentoList",referencedData.getTipoDocumentoEmiList(coDep));        
        //model.addAttribute("deExpedienteList",referencedData.getExpedienteList(coDep));
        return "/consultaDocVoBo/buscaDocsVobo";
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
                list = consultaDocVoBoService.getLsDocsVoBo(bDocVoBo);
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
            return "/consultaDocVoBo/tblDocsVobo";
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
           DocumentoVoBoBean docVobo = consultaDocVoBoService.getDocumentoVoBo(snuAnn,snuEmi,usuario.getCoDep(),usuario.getCempCodemp());
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
           return "/consultaDocVoBo/docVobo";
       } else {
           model.addAttribute("pMensaje", mensaje);
           return "respuesta";
       }       
    }    
}
