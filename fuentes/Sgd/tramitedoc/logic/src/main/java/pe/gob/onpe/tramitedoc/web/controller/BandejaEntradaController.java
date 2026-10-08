/**
 * 
 */
package pe.gob.onpe.tramitedoc.web.controller;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestBody; 

import javax.servlet.http.HttpServletRequest;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBandejaEnBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;

import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.AvisoBandejaEntradaService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.onpe.tramitedoc.bean.AvisoBandejaEntradaBean;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBandejaEnBean;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.service.DocumentoExtRecepService;
/**
 * @author ecueva
 *
 */
@Controller
@RequestMapping("/{version}/srBandejaEntrada.do")
public class BandejaEntradaController {
    @Autowired
    private DocumentoExtRecepService documentoExtRecepService;
    @Autowired
    private AvisoBandejaEntradaService avisoBandejaEntradaService;
    
//    @Autowired
//    private ReferencedData referencedData;

        @RequestMapping(method = RequestMethod.POST, params = "accion=goInicioBuscarExpediente")
        public String goInicioBuscarExpediente(HttpServletRequest request, Model model){
            UsuarioConfigBean usuario = Utilidades.getInstancia().loadUserConfigFromSession(request);
            if(usuario.getTiAcceso().equals("0"))
            {
                List<SiElementoBean>  dato = documentoExtRecepService.getLsBuscardoExpIntExt(usuario.getCempCodemp());
                if(dato!=null && dato.size()>0){
                    return "bandejaEntradaBusqExp";
                }
                else {
                    return "bandejaEntradaBusqExpEmpty";
                }
                
            }
            else {
                return "bandejaEntradaBusqExpEmpty";
            }
            
        }
//    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicio")
//    public String goInicio(HttpServletRequest request, Model model){
//        
//        //Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
//        UsuarioConfigBean usuario = Utilidades.getInstancia().loadUserConfigFromSession(request);
//        String coDep=usuario.getCoDep();
//        String coEmp=usuario.getCempCodemp();
//          
//        List<EtiquetaBandejaEnBean> etiquetasList= avisoBandejaEntradaService.getListEtiquetaBandejaEntrada(coDep,coEmp);
//        model.addAttribute("siglasDep",usuario.getDeSiglasDep());
//        String bandeja = ServletUtility.getInstancia().loadRequestParameter(request, "cobandeja");
//       //YUAL
//       /* String vEsAltaDireccion="N";
//           List<SiElementoBean> grpUserFirma=referencedData.grpElementoList("VIEW_BANDEJA_ESP");
//           SiElementoBean obj;
//           int rlt=0;
//           String a,b;  
//          
//           for(int i=0; i<grpUserFirma.size();i++)
//           {
//               obj= new SiElementoBean();
//               obj=(SiElementoBean)grpUserFirma.get(i);
//               a=obj.getCeleDesele().toString().trim();
//               b=obj.getCeleDescor().toString().trim();
//               if(a.equalsIgnoreCase(coDep)&&b.equalsIgnoreCase(coEmp))
//               {
//                   vEsAltaDireccion="S";
//               }
//           }
//        
//        if(vEsAltaDireccion=="S")
//        {
//            model.addAttribute("bandejaEntradaList",avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),"9"));        
//        }
//        else{
//            model.addAttribute("bandejaEntradaList",avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),usuario.getTiAcceso()));        
//        }*/
//        model.addAttribute("bandejaEntradaList",avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),usuario.getTiAcceso(),bandeja)); 
//        model.addAttribute("etiquetasList",etiquetasList);
//        return "bandejaEntradaDet";
//    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicioJson",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProcessResult<List<AvisoBandejaEntradaBean>> goInicioJson(HttpServletRequest request, Model model) {
       
        String mensaje = "NO_OK";
        ProcessResult<List<AvisoBandejaEntradaBean>> Result= new ProcessResult<List<AvisoBandejaEntradaBean>>(); 
        try {             
        UsuarioConfigBean usuario = Utilidades.getInstancia().loadUserConfigFromSession(request);
        //String coDep=usuario.getCoDep();
        //String coEmp=usuario.getCempCodemp();
          
        //List<EtiquetaBandejaEnBean> etiquetasList= avisoBandejaEntradaService.getListEtiquetaBandejaEntrada(coDep,coEmp);
        //model.addAttribute("siglasDep",usuario.getDeSiglasDep());
        String bandeja = ServletUtility.getInstancia().loadRequestParameter(request, "cobandeja");
        Result.setResult(avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),usuario.getTiAcceso(),bandeja));
        Result.setIsSuccess(true);
        Result.setCodigo(bandeja);
        Result.setTiAcceso(usuario.getTiAcceso());
        //model.addAttribute("bandejaEntradaList",avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),usuario.getTiAcceso(),bandeja)); 
        //model.addAttribute("etiquetasList",etiquetasList);
             
        } catch (Exception ex) { 
            Result.setIsSuccess(false);
            Result.setException(ex.getMessage()); 
            Result.setMessage("Hay problemas en los servicios de la bandeja principal");
        }
        
        return Result;
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goInicioEtiquetaJson",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProcessResult<List<EtiquetaBandejaEnBean>> goInicioEtiquetaJson(HttpServletRequest request, Model model) {
       
        ProcessResult<List<EtiquetaBandejaEnBean>> Result= new ProcessResult<List<EtiquetaBandejaEnBean>>(); 
        try {          
        UsuarioConfigBean usuario = Utilidades.getInstancia().loadUserConfigFromSession(request);
        String coDep=usuario.getCoDep();
        String coEmp=usuario.getCempCodemp();
          
        List<EtiquetaBandejaEnBean> etiquetasList= avisoBandejaEntradaService.getListEtiquetaBandejaEntrada(coDep,coEmp);
        //model.addAttribute("siglasDep",usuario.getDeSiglasDep());         
        Result.setResult(etiquetasList);
        Result.setIsSuccess(true);
        Result.setCodigo(usuario.getDeSiglasDep()); 
             
        } catch (Exception ex) { 
            Result.setIsSuccess(false);
            Result.setException(ex.getMessage()); 
            Result.setMessage("Hay problemas en los servicios de la bandeja principal");
        }
        
        return Result;
    }
     @RequestMapping(method = RequestMethod.POST, params = "accion=goInicioBuscarExpedienteJson",produces = MediaType.APPLICATION_JSON_VALUE)
    @ResponseBody
    public ProcessResult<String> goInicioBuscarExpedienteJson(HttpServletRequest request, Model model) {
       String nuExpe = ServletUtility.getInstancia().loadRequestParameter(request, "nuExpe");
        ProcessResult<String> Result= new ProcessResult<String>(); 
        try {         
            DocumentoExtRecepBean documentoExtRecepBean= documentoExtRecepService.getDocumentosExpInteExt(nuExpe);    
            if(documentoExtRecepBean == null){
                Result.setResult("No se encontró el expediente.");
                Result.setIsSuccess(false);
            }
            else {
                Result.setIsSuccess(true);
                Result.setCodigo(documentoExtRecepBean.getNuAnn());
                Result.setResult(documentoExtRecepBean.getNuEmi());
                
            }          
        } catch (Exception ex) { 
            Result.setIsSuccess(false);
            Result.setException(ex.getMessage()); 
            Result.setMessage("Hay problemas en los servicios de la bandeja principal");
        }
        
        return Result;
    }
}
