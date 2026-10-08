package pe.gob.onpe.tramitedoc.web.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.SessionAttributes;
import org.springframework.web.bind.support.SessionStatus;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.libreria.util.Mensaje;
import pe.gob.onpe.tramitedoc.util.Constantes;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.service.UsuarioService;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioDepAcceso;
import pe.gob.onpe.tramitedoc.service.UsuarioConfigService;
import pe.gob.onpe.tramitedoc.service.ValidaDatosService;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;


@Controller(value = "inicioController")
//@RequestMapping("/inicio.do")
@SessionAttributes(value = {"usuario", "usuarioConfig"})

public class InicioController extends GeneralController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @Autowired
    private UsuarioService usuarioService;
    
    @Autowired
    private UsuarioConfigService usuarioConfigService;

    @Autowired
    private ValidaDatosService validaDatosService;
    
    public InicioController() {
    }

    @RequestMapping(value="/inicio.do", method = RequestMethod.POST)
    public String doInicioLogin(@ModelAttribute("usuario") Usuario usuario, Model model, BindingResult result, SessionStatus status, HttpServletRequest request, HttpServletResponse response){
        UsuarioConfigBean usuarioConfig = new UsuarioConfigBean();
    	String destino = "login";
        boolean isAccessOk=false;
        Mensaje msg = new Mensaje();
        usuario.setTiIdentificacion(Constantes.TI_IDENTIFICACION);

        if(!result.hasErrors()){
            loadUsuarioRemoteAttribs(usuario, request);
            usuarioService.autenticarUsuario(msg, usuario, applicationProperties.getCoAplicativo(), false,applicationProperties.getADSecureLDAP(),applicationProperties.getADDomain(),applicationProperties.getADHost(),applicationProperties.getADPort(),applicationProperties.getADAmbitoBusquedaUsuario(),applicationProperties.getADMinutosRestableceIntento());
            if(isOkAndLoadAccess(msg, usuario, result, request)){
                usuarioConfig=usuarioConfigService.getConfig(null, usuario.getCempCodemp() , usuario.getCoDep());
                usuarioConfig.setInEsAdmin(usuario.getInAdmin());
                usuario.setCoDep(usuarioConfig.getCoDep());
                model.addAttribute("usuarioConfig",usuarioConfig);
                isAccessOk = true;
            }
            if(isAccessOk){
                usuario.setSessionId(ServletUtility.getInstancia().loadSessionId(request));
                destino = "forward:/{version}/mainpanel.do";
                //destino = "forward:mainpanel";
            }
        }
        return destino;
    }

    @RequestMapping(value="/principal.do")
    public String doLoginDirecto(HttpServletRequest request, HttpServletResponse response){
        String destino = "forward:/{version}/mainpanel.do";
        return destino;
    }
    
    
    @RequestMapping(value="/{version}/cambiaDep.do", method = RequestMethod.POST)
    public String cambiaDep(Model model,HttpServletRequest request, HttpServletResponse response){
        String destino = "cambiaDepAcceso";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        
         List<UsuarioDepAcceso> listaDependenciaAcceso = usuarioConfigService.getListDepAccesos(usuario.getCempCodemp(), usuario.getCoUsuario());
        model.addAttribute("listaDependenciaAcceso",listaDependenciaAcceso);
        
        return destino;
    }

    @RequestMapping(value="/{version}/cambiaDepUsu.do", method = RequestMethod.GET)
    public String cambiaDepUsu(Model model,HttpServletRequest request, HttpServletResponse response){
        String destino = "/configGeneral/cambiaDepUsuario";
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        List<UsuarioDepAcceso> listaDependenciaAcceso = null;
        if (usuario.getInAdmin().equals("1")){
            listaDependenciaAcceso = usuarioConfigService.getListDepTotal(usuario.getCempCodemp());
        }else{
            listaDependenciaAcceso = usuarioConfigService.getListDepAccesos(usuario.getCempCodemp(), usuario.getCoUsuario());
        }
        model.addAttribute("listaDependenciaAcceso",listaDependenciaAcceso);
        
        return destino;
    }
    
    
    @RequestMapping(value="/acceso.do", method = RequestMethod.POST)
    public String accesoDep(Model model,HttpServletRequest request, HttpServletResponse response){
        String destino = "forward:/{version}/mainpanel.do";
        String vcoDepValida = null;
        String vcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "txtCoDep");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        
        if (vcoDep!=null && usuario != null && usuario.getCempCodemp()!=null && usuario.getCoDep()!=null ){
            vcoDepValida =  validaDatosService.getValidaDep(usuario.getCempCodemp(), usuario.getCoUsuario(), vcoDep);
            if(vcoDepValida !=null && vcoDepValida.equals(vcoDep)) {
                usuario.setCoDep(vcoDep);
            }
            UsuarioConfigBean usuarioConfig = new UsuarioConfigBean();
            usuarioConfig=usuarioConfigService.getConfig(null, usuario.getCempCodemp() , usuario.getCoDep());
            usuarioConfig.setInEsAdmin(usuario.getInAdmin());
            usuarioConfig.setCoUsuario(usuario.getCoUsuario());
            model.addAttribute("usuario",usuario);
            model.addAttribute("usuarioConfig",usuarioConfig);
        }
        return destino;
    }
    
    @RequestMapping(value="/accesoUsuario.do", method = RequestMethod.POST)
    public String accesoUsuario(Model model,HttpServletRequest request, HttpServletResponse response){
        String destino = "forward:/{version}/mainpanel.do";
        String vcoDepValida = null;
        String vcoDep = ServletUtility.getInstancia().loadRequestParameter(request, "txtCoDep");
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        UsuarioConfigBean usuarioConfig = new UsuarioConfigBean();
        
        if (vcoDep!=null && usuario != null && usuario.getCempCodemp()!=null && usuario.getCoDep()!=null ){
            if (usuario.getInAdmin().equals("1")){
                usuario.setCoDep(vcoDep);
                usuarioConfig=usuarioConfigService.getConfigTotal(usuario.getCempCodemp() , usuario.getCoDep());
                usuarioConfig.setInEsAdmin(usuario.getInAdmin());
            }else{
                vcoDepValida =  validaDatosService.getValidaDep(usuario.getCempCodemp(), usuario.getCoUsuario(), vcoDep);
                if(vcoDepValida !=null && vcoDepValida.equals(vcoDep)) {
                    usuario.setCoDep(vcoDep);
                }
                usuarioConfig=usuarioConfigService.getConfig(null, usuario.getCempCodemp() , usuario.getCoDep());
                usuarioConfig.setInEsAdmin(usuario.getInAdmin());
            }
            
            model.addAttribute("usuario",usuario);
            model.addAttribute("usuarioConfig",usuarioConfig);
        }
        return destino;
    }

    @RequestMapping(value="inicioErr.do", method = RequestMethod.POST)
    public String doInicioErr(Usuario usuario, BindingResult result, SessionStatus status, HttpServletRequest request, HttpServletResponse response){
        String destino = "redirect:http://"+request.getServerName()+":"+request.getServerPort()+"/login.do";

        // esto es para limpiar la session
        //status.setComplete();
        return destino;
    }

    @RequestMapping(value="/sesionExpira.do")
    public String doSesionExpira(Usuario usuario, BindingResult result, SessionStatus status, HttpServletRequest request, HttpServletResponse response){
        response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
        String destino = "sesionExpira";
        return destino;
    }

    private boolean isOkAndLoadAccess(Mensaje msg, Usuario usuario, BindingResult result,  HttpServletRequest request){
        if (msg.getCoRespuesta().equals("00")) {
            // acceso valido a ok
            //usuarioService.obtienePermisosSIO(msg, usuario,applicationProperties.getCoAplicativo());
            return true;
        } else {
            // no accede y imprime lel mensaje
            printErrorMessaje(result, msg);
            return false;
        }
    }

    @RequestMapping(value="/{version}/config.do", method = RequestMethod.POST, params = "accion=goDatosPC")
    public @ResponseBody String doDatosPC(@ModelAttribute("usuario") Usuario usuario, Model model, BindingResult result, SessionStatus status, HttpServletRequest request, HttpServletResponse response){

    	/*String mensaje = "OK";
        boolean isAccessOk=false;
        String pIpPC = ServletUtility.getInstancia().loadRequestParameter(request, "ipPC");
        String pNombrePC = ServletUtility.getInstancia().loadRequestParameter(request, "nombrePC");
        String pUsuPC = ServletUtility.getInstancia().loadRequestParameter(request, "usuPC");
        String pSwFirma = ServletUtility.getInstancia().loadRequestParameter(request, "swFirma");
 
        UsuarioConfigBean usuarioConfig = Utilidades.getInstancia().loadUserConfigFromSession(request);
        
        usuario.setIpPC(pIpPC);
        usuario.setNombrePC(pNombrePC);
        usuario.setUsuPc(pUsuPC);
        usuarioConfig.setSwFirma(pSwFirma);
        
        model.addAttribute("usuario",usuario);
        model.addAttribute("usuarioConfig",usuarioConfig);
        return mensaje;*/
        
    	StringBuilder retval = new StringBuilder();
        boolean bretval;
        String pIpPC = ServletUtility.getInstancia().loadRequestParameter(request, "ipPC");
        String pNombrePC = ServletUtility.getInstancia().loadRequestParameter(request, "nombrePC");
        String pUsuPC = ServletUtility.getInstancia().loadRequestParameter(request, "usuPC");
        String pSwFirma = ServletUtility.getInstancia().loadRequestParameter(request, "swFirma");
 
        try{
            UsuarioConfigBean usuarioConfig = Utilidades.getInstancia().loadUserConfigFromSession(request);

            usuario.setIpPC(pIpPC);
            usuario.setNombrePC(pNombrePC);
            usuario.setUsuPc(pUsuPC);
            usuarioConfig.setSwFirma(pSwFirma);
            usuarioConfig.setNroPestanna(usuarioConfig.getNroPestanna() + 1);

            model.addAttribute("usuario",usuario);
            model.addAttribute("usuarioConfig",usuarioConfig);
            bretval = true;
        }catch(Exception ex){
            bretval = false;
            ex.printStackTrace();
        }
        retval.append("{\"retval\":");
        retval.append(bretval);
        retval.append("}");                        
        return retval.toString();        

    }    


}