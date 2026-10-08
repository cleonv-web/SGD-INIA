/**
 *
 */
package pe.gob.onpe.tramitedoc.web.controller;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import javax.servlet.http.HttpServletRequest;
import org.springframework.beans.propertyeditors.CustomDateEditor;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.bean.AsignacionFuncionarioBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.service.AsignacionFuncionarioService;
import pe.gob.onpe.tramitedoc.service.CommonQryService;
import pe.gob.onpe.tramitedoc.service.ReferencedData;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 * @author ecueva
 *
 */
@Controller
@RequestMapping("/{version}/srAsignacionFuncionario.do")
public class AsignacionFuncionarioController {

    @Autowired
    private ReferencedData referencedData;
    @Autowired
    private AsignacionFuncionarioService asignacionFuncionarioService;
    @Autowired
    private CommonQryService commonQryService;
    @InitBinder
    protected void initBinder(WebDataBinder binder) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        binder.registerCustomEditor(Date.class, new CustomDateEditor(
                dateFormat, false));
    }

    @RequestMapping(method = RequestMethod.GET, params = "accion=goAsignacionFuncionarios")
    public String goAsignacionFuncionarios(HttpServletRequest request, AsignacionFuncionarioBean asignacionFuncionarioBean, Model model) {
        String url = "/asignacionFuncionarios/adminAsignacion";
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        //AsignacionFuncionarioBean asignacionFuncionarioBean = new AsignacionFuncionarioBean();
        if ("1".equals(usuarioConfigBean.getInEsAdmin())) {
            //model.addAttribute("lista", asignacionFuncionarioService.getListAsignacionFuncionario(asignacionFuncionarioBean));
        } else {
            url = "";
        }
        return url;
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goAsignacionFuncionarios")
    public String goAsignacionFuncionarios(String param, HttpServletRequest request, AsignacionFuncionarioBean asignacionFuncionarioBean, Model model) {
        String mensaje = "NO_OK";       
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        //AsignacionFuncionarioBean asignacionFuncionarioBean = new AsignacionFuncionarioBean();
        List list=null;
        if ("1".equals(usuarioConfigBean.getInEsAdmin())) {
            list=asignacionFuncionarioService.getListAsignacionFuncionario(asignacionFuncionarioBean);
        }
        if (list!=null) {
            if(list.isEmpty()){
                mensaje = "No se encuentran registros.";
            }else{
                if (list.size() >= 100) {
                    model.addAttribute("msjEmision", "OPTIMICE SU CONSULTA");
                }
                model.addAttribute("lista", list);
                mensaje = "OK";
            }            
        }else {
            mensaje = "No se encuentran registros.";
        }
        if (mensaje.equals("OK")) {
            return "/asignacionFuncionarios/adminAsignacionLista";
        } else {
            model.addAttribute("pMensaje", mensaje);
            return "respuesta";
        }
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goDialogAsignacion", produces = "text/plain; charset=utf-8")
    public String goDialogAsignacion(HttpServletRequest request, Model model) {
        AsignacionFuncionarioBean asignacionFuncionarioBean = new AsignacionFuncionarioBean();
        model.addAttribute("asignacionFuncionarioBean", asignacionFuncionarioBean);
        model.addAttribute("listaTipoEncargatura", referencedData.grpElementoList("CO_TIPO_ENC"));
        return "/asignacionFuncionarios/dlgNuevaAsignacion";
    }

    //Buscar dependencia
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDlgBuscarDependencia")
    public String goDlgBuscarDependencia(HttpServletRequest request, Model model) {
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm", "10");
        model.addAttribute("listaDestinatario", commonQryService.getLsDepencia());
        return "/modalGeneral/consultaDestinatarioEmiConsul";
    }

    //Retornar la información del empleado asignado
    @RequestMapping(method = RequestMethod.POST, params = "accion=goFuncionarioAsignado")
    public @ResponseBody
    String goFuncionarioAsignado(HttpServletRequest request, Model model) {
        String coDependencia = ServletUtility.getInstancia().loadRequestParameter(request, "coDependencia");
        StringBuilder retval = new StringBuilder();
        String coRespuesta = "0";
        String vRespuesta = "No existen coincidencias";
        String vAsignacion = "";
        AsignacionFuncionarioBean asignacionFuncionarioBean = asignacionFuncionarioService.getFuncionarioPorDependencia(coDependencia);
        if (asignacionFuncionarioBean != null) {
            coRespuesta = "1";
            vRespuesta = "Datos encontrados";
            String empleado = "";
            if (asignacionFuncionarioBean.getDeEmpleado() != null) {
                empleado = asignacionFuncionarioBean.getDeEmpleado();
            }
            vAsignacion = "\",\"deEmpleado\":\"" + empleado
                    + "\",\"coTipoEncargo\":\"" + asignacionFuncionarioBean.getDeTipoEncargo();
        }
        int existe = asignacionFuncionarioService.getExisteProgramacionDependencia(coDependencia);
        if (existe > 0) {
            coRespuesta = "2";
            vRespuesta = "La dependencia tiene programaciones por asignar, <br/> Sin embargo se permite otra programación bajo citerio del usuario.";
        }
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append(vAsignacion);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(vRespuesta);
        retval.append("\"}");
        return retval.toString();
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goDlgListaEmpleado")
    public String goDlgListaEmpleado(HttpServletRequest request, Model model) {
        UsuarioConfigBean usuarioConfigBean = Utilidades.getInstancia().loadUserConfigFromSession(request);
        model.addAttribute("iniFuncionParm", "7");
        model.addAttribute("listaEmpleado", commonQryService.getLsEmpleado(""));
        return "/modalGeneral/consultaElaboradoPorConsul";
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goRegistrarAsignacionFuncionario", produces = "text/plain; charset=utf-8")
    public @ResponseBody
    String goRegistrarAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean, HttpServletRequest request, Model model) {
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String msg[] = new String[2];
        asignacionFuncionarioBean.setCoUseCre(usuario.getCoUsuario());
        msg = asignacionFuncionarioService.insAsignacionFuncionario(asignacionFuncionarioBean);
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(msg[0]);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(msg[1]);
        retval.append("\"}");
        return retval.toString();
    }

    @RequestMapping(method = RequestMethod.POST, params = "accion=goValidaEmpleadoAsignacion", produces = "text/plain; charset=utf-8")
    public @ResponseBody
    String goValidaEmpleadoAsignacion(HttpServletRequest request, Model model) {
        String coEmpleado = ServletUtility.getInstancia().loadRequestParameter(request, "coEmpleado");
        String coRespuesta = "0";
        String deRespuesta = "";
        try {
            int existe = asignacionFuncionarioService.getExisteProgramacionEmpleado(coEmpleado);
            if (existe > 0) {
                coRespuesta = "1";                
                deRespuesta = "El empleado tiene programaciones por asignar, <br/> Sin embargo se permite otra programación bajo citerio del usuario.";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta);
        retval.append("\"}");
        return retval.toString();
    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goEliminaAsignacionEmpleado", produces = "text/plain; charset=utf-8")
    public @ResponseBody
    String goEliminaAsignacionEmpleado(HttpServletRequest request, Model model) {
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        long coAsignacion = Long.parseLong(ServletUtility.getInstancia().loadRequestParameter(request, "coAsignacion"));
        String coRespuesta = "0";
        String deRespuesta = "";
        try {
            AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
            asignacionFuncionarioBean.setCoAsignacionFuncionario(coAsignacion);
            asignacionFuncionarioBean.setCoUseMod(usuario.getCoUsuario());
            coRespuesta=asignacionFuncionarioService.delAsignacionFuncionario(asignacionFuncionarioBean);
            if(coRespuesta.equals("1")){
                deRespuesta="Se eliminó correctamente el registro";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            coRespuesta="0";
            deRespuesta="Error al eliminar el registro";
        }
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(coRespuesta);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(deRespuesta);
        retval.append("\"}");
        return retval.toString();
    }
    @RequestMapping(method = RequestMethod.POST, params = "accion=goDialogEditarAsignacion", produces = "text/plain; charset=utf-8")
    public String goDialogEditarAsignacion(HttpServletRequest request, Model model) {
        long coAsignacion = Long.parseLong(ServletUtility.getInstancia().loadRequestParameter(request, "coAsignacion"));
        AsignacionFuncionarioBean asignacionFuncionarioBean = asignacionFuncionarioService.getAsignacionFuncionario(coAsignacion);
        model.addAttribute("asignacionFuncionarioBean", asignacionFuncionarioBean);
        model.addAttribute("listaTipoEncargatura", referencedData.grpElementoList("CO_TIPO_ENC"));
        return "/asignacionFuncionarios/dlgEditarAsignacion";
    }
    
    @RequestMapping(method = RequestMethod.POST, params = "accion=goActualizarAsignacionFuncionario", produces = "text/plain; charset=utf-8")
    public @ResponseBody
    String goActualizarAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean, HttpServletRequest request, Model model) {
        Usuario usuario = Utilidades.getInstancia().loadUserFromSession(request);
        String msg[] = new String[2];
        try {
            asignacionFuncionarioBean.setCoUseMod(usuario.getCoUsuario());
            String vReturn = asignacionFuncionarioService.updAsignacionFuncionario(asignacionFuncionarioBean);
            if(vReturn.equals("1")){
                msg[0]="1";
                msg[1]="Actualizado correctamente";
            }else if(vReturn.equals("2")){
                msg[0]="1";
                msg[1]="Se actualizó la información y se asignó al funcionario.";
            }else if(vReturn.equals("3")){
                msg[0]="0";
                msg[1]="Fecha de inicio no puede ser menor que la fecha del sistema.";
            }else{
                msg[0]="0";
                msg[1]="Error al actualizar el registro";
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            msg[0] = "0";
            msg[1] = "Error en el metodo";
        }
        StringBuilder retval = new StringBuilder();
        retval.append("{\"coRespuesta\":\"");
        retval.append(msg[0]);
        retval.append("\",\"deRespuesta\":\"");
        retval.append(msg[1]);
        retval.append("\"}");
        return retval.toString();
    }
    
}
