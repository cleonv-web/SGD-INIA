package pe.gob.onpe.tramitedoc.web.controller;

import java.util.ArrayList;
import java.util.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.service.AvisoBandejaEntradaService;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBandejaEnBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.libreria.util.ServletUtility;
@Controller(value = "mainPanelController")
public class MainPanelController {

    @Autowired
    private AvisoBandejaEntradaService avisoBandejaEntradaService;
    
    @Autowired
    private ReferencedData referencedData;
/*
    @Autowired @Qualifier(value = "applicationProps")
    private Properties properties;
*/
     @Autowired
    private ApplicationProperties applicationProperties;

    @RequestMapping("/{version}/mainpanel.do")
    public String showMainPanel(Model model,HttpServletRequest request){
//    	Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuario = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("siglasDep",usuario.getDeSiglasDep());
        String coDep=usuario.getCoDep();
        String coEmp=usuario.getCempCodemp();         
        //YUAL
        /*String vEsAltaDireccion="N";
           List<SiElementoBean> grpUserFirma=referencedData.grpElementoList("VIEW_BANDEJA_ESP");
           SiElementoBean obj;
           int rlt=0;
           String a,b;  
          
           for(int i=0; i<grpUserFirma.size();i++)
           {
               obj= new SiElementoBean();
               obj=(SiElementoBean)grpUserFirma.get(i);
               a=obj.getCeleDesele().toString().trim();
               b=obj.getCeleDescor().toString().trim();
               if(a.equalsIgnoreCase(coDep)&&b.equalsIgnoreCase(coEmp))
               {
                   vEsAltaDireccion="S";
               }
           }
        
        if(vEsAltaDireccion=="S")
        {
        model.addAttribute("bandejaEntradaList",avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),"9"));        
        }
        else{
            model.addAttribute("bandejaEntradaList",avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),usuario.getTiAcceso()));        
        }  
        */ 
        //model.addAttribute("bandejaEntradaList",avisoBandejaEntradaService.getBandejaEntrada(usuario.getCoDep(),usuario.getCempCodemp(),usuario.getTiAcceso(),bandeja));       
        //List<EtiquetaBandejaEnBean> etiquetasList= avisoBandejaEntradaService.getListEtiquetaBandejaEntrada(coDep,coEmp);
        //model.addAttribute("etiquetasList",etiquetasList);
        //etiquetasList=null;
        model.addAttribute("fechaActual",Utility.getInstancia().dateToFormatString(new Date()));
        String nameImgPortada=usuario.getNameImgPortadaSgd();
        if(!(nameImgPortada!=null&&nameImgPortada.trim().length()>0)){
            nameImgPortada="default.jpg";
        }
        //YUAL
         List<SiElementoBean> lstImPortadaOrigen=referencedData.grpElementoList("BANNER_INICIO");
         List<SiElementoBean> lstImPortada=new ArrayList<SiElementoBean>();
         SiElementoBean oSiElementoBean;
         for(int i=0;i<lstImPortadaOrigen.size();i++)
         {
             oSiElementoBean= new SiElementoBean();
             oSiElementoBean=lstImPortadaOrigen.get(i);
             oSiElementoBean.setCeleDesele(Base64.getEncoder().encodeToString(oSiElementoBean.getArchivo()));
             lstImPortada.add(oSiElementoBean); 
        }
         model.addAttribute("lstImPortada",lstImPortada);
        oSiElementoBean=null;
        lstImPortadaOrigen=null;
        lstImPortada=null;
        model.addAttribute("imgId",nameImgPortada);
        
        
        model.addAttribute("urlMesaVirtual",applicationProperties.urlMesaVirtual());
        model.addAttribute("urlConsultaAvanzada",applicationProperties.urlConsultaAvanzada());
        return "mainpanel";
        
    }

}
