package pe.gob.segdi.iotramitesgd.controller;

import org.jboss.logging.Logger;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuario;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuarioConfiguracion;
import pe.gob.segdi.iotramitesgd.bean.DocumentoPorEstado;
import pe.gob.segdi.iotramitesgd.bean.ParametrosBean;
import pe.gob.segdi.iotramitesgd.service.ILoginService;
import pe.gob.segdi.iotramitesgd.service.IMaestroService;
import pe.gob.segdi.iotramitesgd.service.ITramiteService;


import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;

import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.ExternalContext;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

/**
 * Objeto : LoginMB.java. Descripción : Clase controladora de logueo de
 * documentos, contiene los métodos de acceso a la capa DAO
 * (inserciones,actualizaciones y consultas). Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones Fecha Nombre Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX XXXXXXXXX XXXXXXXXXXXXXXXXXX.
 */

@ManagedBean("login")
@SessionScoped
public class LoginMB extends BaseController {
    private static Logger depurador = Logger.getLogger(LoginMB.class.getName());
    private static final long serialVersionUID = 1L;
    private String usuario;
    private String password;

    private int failedAuthCount;
    private boolean isCaptchaSolved;
    private boolean isUserLoggedIn;
    private String errorMessage;

    @Inject
    private ILoginService iLoginService;
    @Inject
    private ITramiteService iTramiteService;
    @Inject
    private IMaestroService iMaestroService;

    @Inject
    private CVariableSession cVariableSession;
    List<DocumentoPorEstado> documentoPorEstado;

    private boolean loggedIn;

    @PostConstruct
    public void init() {
        inicializarVariable();
    }

    @Override
    public String inicializarControladora() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void inicializarVariable() {
        try {

        }catch (Exception e){
            depurador.error("ERORR ", e);
        }

    }

    @Override
    public void limpiarObjectos() {
        // TODO Auto-generated method stub

    }

    public String doLogin() {
         depurador.info("doLogin ==>");
        try {
            List<ParametrosBean> lista;
            lista = iTramiteService.getEstadoMesaVirtual();
            depurador.info("LISTA PARAMETROS ========" + lista.size());
            //CVariableSession obj2 = (CVariableSession) findBean("cVariableSession");
            for (int i = 0; i < lista.size(); i++) {
                if (lista.get(i).getCopar().equals("WSSERVICELOCATION")) {
                    cVariableSession.setWsServiceLocation(lista.get(i).getDepar());
                } else if (lista.get(i).getCopar().equals("URLAPLICATION")) {
                    cVariableSession.setUrlAplication(lista.get(i).getDepar());
                } else if (lista.get(i).getCopar().equals("ESTADO_MESA_VIRTUAL")) {
                    cVariableSession.setEstMesaVirtual(lista.get(i).getDepar());
                }
            }

            List<ParametrosBean> listaParametro;
            listaParametro = iMaestroService.getParametros("MPV_ANEXO");

            for (int i = 0; i < listaParametro.size(); i++) {
                if (listaParametro.get(i).getCopar().equals("CANTIDAD")) {
                    cVariableSession.setAnx_cantidad(Integer.parseInt(listaParametro.get(i).getDepar()));
                } else if (listaParametro.get(i).getCopar().equals("TAMANIO")) {
                    cVariableSession.setAnx_tamanio(Integer.parseInt(listaParametro.get(i).getDepar()));
                }
            }

            cVariableSession.setUsuario(usuario.toUpperCase());

            if (validateLogin()) {
                DatosUsuario datosUsuario = iLoginService.autenticarUsuario(usuario.toUpperCase(), password);
                if (datosUsuario.getCodRespuesta().equals("00")) {
                    DatosUsuarioConfiguracion datosUsuarioConfiguracion = iLoginService
                            .getConfiguracionUsuario(datosUsuario);
                    datosUsuario.setCoDepEmi(datosUsuarioConfiguracion.getCoDepMp());
                    datosUsuario.setCoLocEmi(datosUsuarioConfiguracion.getCoLocal());
                    setAttributeSession("datosUsuario", datosUsuario);
                    loggedIn = true;

                    CReporteController obj = (CReporteController) findBean("cReporte");
                    obj.reporteXBandejasPie();
                    obj.reporteXBandejasBar();

                    return "bienvenida";
                } else {
                    loggedIn = false;
                    addErrorMessage("Error", datosUsuario.getDesRespuesta());
                    return "login";
                }
            }
        } catch (Exception e) {
            depurador.error(null, e);
        }
        return "login";
    }

    public String giveMeCoockieValue(HttpServletRequest request, String cookieName) {
        String retval = "";
        Cookie[] cookies = request.getCookies();

        if (cookies != null) {
            for (int i = 0; i < cookies.length; i++) {
                if (cookies[i].getName().equals(cookieName)) {
                    retval = cookies[i].getValue();
                    // depurador.info("retval ==>"+retval);
                    break;
                }
            }
        }
        return retval;
    }

    public String logout() throws ServletException, IOException {
        // depurador.info("logout ==>");
        loggedIn = false;

        FacesContext context = FacesContext.getCurrentInstance();
        ExternalContext externalContext = context.getExternalContext();
        HttpSession session = (HttpSession) context.getExternalContext().getSession(false);
        session.invalidate();
        String ctxPath = ((ServletContext) externalContext.getContext()).getContextPath();
        externalContext.redirect(new StringBuilder().append(ctxPath).append("/index.html").toString());
        return null;
    }

    private boolean validateLogin() {
        // depurador.info("validateLogin ==>");
        boolean validLogin = false;
        String pattern = "^[a-zA-Z0-9_]+$"; // alphanumeric chars and underscores only

        if (usuario.equals("")) {
            addInfoMessage("Campos Obligatorios", "Debe ingresar el usuario");
        } else if (password.equals("")) {
            addInfoMessage("Campos Obligatorios", "Debe ingresar el password");
        } else {
            validLogin = usuario.matches(pattern) && password.matches(pattern);
            if (!validLogin) {
                errorMessage = "Autenticación Invalida";
                addWarnMessage("Atención", errorMessage);
            }
        }
        return validLogin;
    }

    /*
     * **************************************************
     **************************************************/

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public int getFailedAuthCount() {
        return failedAuthCount;
    }

    public void setFailedAuthCount(int failedAuthCount) {
        this.failedAuthCount = failedAuthCount;
    }

    public boolean isCaptchaSolved() {
        return isCaptchaSolved;
    }

    public void setCaptchaSolved(boolean isCaptchaSolved) {
        this.isCaptchaSolved = isCaptchaSolved;
    }

    public boolean isUserLoggedIn() {
        return isUserLoggedIn;
    }

    public void setUserLoggedIn(boolean isUserLoggedIn) {
        this.isUserLoggedIn = isUserLoggedIn;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public void setErrorMessage(String errorMessage) {
        this.errorMessage = errorMessage;
    }

//    public ILoginService getiLoginService() {
//        return iLoginService;
//    }
//
//    public void setiLoginService(ILoginService iLoginService) {
//        this.iLoginService = iLoginService;
//    }

    public boolean isLoggedIn() {
        return loggedIn;
    }

    public void setLoggedIn(boolean loggedIn) {
        this.loggedIn = loggedIn;
    }

//    public ITramiteService getiTramiteService() {
//        return iTramiteService;
//    }
//
//    public void setiTramiteService(ITramiteService iTramiteService) {
//        this.iTramiteService = iTramiteService;
//    }

    public List<DocumentoPorEstado> getDocumentoPorEstado() {
        return documentoPorEstado;
    }

    public void setDocumentoPorEstado(List<DocumentoPorEstado> documentoPorEstado) {
        this.documentoPorEstado = documentoPorEstado;
    }

    @Override
    public String inicializarControladora(String x) {
        // TODO Auto-generated method stub
        return null;
    }

//    public IMaestroService getiMaestroService() {
//        return iMaestroService;
//    }
//
//    public void setiMaestroService(IMaestroService iMaestroService) {
//        this.iMaestroService = iMaestroService;
//    }

}