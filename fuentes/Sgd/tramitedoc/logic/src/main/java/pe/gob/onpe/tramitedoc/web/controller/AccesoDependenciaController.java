/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.controller;

import java.util.List;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.model.DatosUsuario;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.service.UsuarioService;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.bean.UsuarioDepAcceso;
import pe.gob.onpe.tramitedoc.service.UsuarioConfigService;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author wcutipa
 */
@Controller
@RequestMapping("/{version}/srDepAcceso.do")
public class AccesoDependenciaController {

    @Autowired
    private UsuarioService usuarioService;
    @Autowired
    private UsuarioConfigService usuarioConfigService;

    @RequestMapping(method = RequestMethod.POST, params = "accion=goSeleccionaDep")
    public @ResponseBody
    String goSeleccionaDep(Model model, HttpServletRequest request, HttpServletResponse response) {
        StringBuffer retval = new StringBuffer();
        String pcoUsuario = ServletUtility.getInstancia().loadRequestParameter(request, "pcoUsuario");

        if (pcoUsuario != null) {
            pcoUsuario = Utilidades.DescriptaCadena(pcoUsuario);
            try {
                DatosUsuario depUsuario = usuarioService.getDepUsuario(pcoUsuario.toUpperCase());
                if (depUsuario != null) {
                    retval.append("{'retval':'");
                    retval.append("OK");
                    retval.append("','coEmp':'");
                    retval.append(depUsuario.getCempCodemp());
                    retval.append("','coDep':'");
                    retval.append(depUsuario.getCoDep());
                    retval.append("','deDep':'");
                    retval.append(depUsuario.getDeDep());
                    retval.append("','inAD':'");
                    retval.append(depUsuario.getInAD());
                    retval.append("'}");
                    
                    

                } else {
                    retval.append("{'retval':");
                    retval.append("false");
                    retval.append("}");
                }
            } catch (Exception e) {
                e.printStackTrace();
                retval.append("{'retval':");
                retval.append("false");
                retval.append("}");
            }
        }

        return retval.toString();
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goListaDependencias")
    public String goListaDependencias(HttpServletRequest request, Model model) {

        String pcoUsuario = ServletUtility.getInstancia().loadRequestParameter(request, "pcoUsuario");
        String pcoEmp = ServletUtility.getInstancia().loadRequestParameter(request, "pcoEmp");

        List<UsuarioDepAcceso> listDep = usuarioConfigService.getListDepAccesos(pcoEmp, pcoUsuario);

        model.addAttribute("listaDependenciaAcceso", listDep);

        return "seleccionaDepAcceso";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goCambioPwd")
    public String goCambioPwd(HttpServletRequest request, Model model) {
        String pcoUsuario = ServletUtility.getInstancia().loadRequestParameter(request, "pcoUsuario");
        model.addAttribute("pcoUsuario", pcoUsuario);
        
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        usuario.setInClave(null);     
        model.addAttribute("usuario",usuario);
        return "loginChangePwd";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerRequisitos")
    public String goVerRequisitos(HttpServletRequest request, Model model) {
        return "verificaReqSO";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=msgCambioPwd")
    public String msgCambioPwd(HttpServletRequest request, Model model) {
        model.addAttribute("tipoMsg", ServletUtility.getInstancia().loadRequestParameter(request, "tipoMsg"));        
        return "/modalGeneral/msgChangePwd";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goVerRequisitos2")
    public String goVerRequisitos2(HttpServletRequest request, Model model) {
        return "verificaReqSO2";
    }
}
