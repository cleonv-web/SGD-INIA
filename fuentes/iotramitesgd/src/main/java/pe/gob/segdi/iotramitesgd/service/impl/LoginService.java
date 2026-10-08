package pe.gob.segdi.iotramitesgd.service.impl;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuario;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuarioConfiguracion;
import pe.gob.segdi.iotramitesgd.dao.ILoginDao;
import pe.gob.segdi.iotramitesgd.service.ILoginService;
import pe.gob.segdi.iotramitesgd.util.ConstantesSec;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;
import pe.gob.segdi.iotramitesgd.util.Utility;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Named;
import java.util.List;
import java.util.Map;

/**
 * Objeto : LoginService.java.
 * Descripción : Clase que implementa los métodos de la interface login , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Named
@ApplicationScoped
public class LoginService implements ILoginService {
    private static Logger depurador = Logger.getLogger(LoginService.class.getName());

    @Autowired
    ILoginDao iLoginDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public DatosUsuario autenticarUsuario(String usuario, String password) throws Exception {
        depurador.info("autenticarUsuario ==>");
        DatosUsuario datosUsuario = new DatosUsuario();
        try {
            datosUsuario = iLoginDao.autenticarUsuario(datosUsuario, usuario.toUpperCase());
            if (datosUsuario.getCodRespuesta().equals("00")) {
                String esUsuario = datosUsuario.getEsActivo();

                boolean userBloqMaxIntentos = false;
                if (esUsuario != null && (esUsuario.equals("A") || esUsuario.equals("N") || esUsuario.equals("M"))) {
                    boolean passOk = false;
                    //para el caso de migracion de contraseña fuerte.
                    if (esUsuario.equals("M")) {
                        datosUsuario.setEsActivo("A");
                    }
                    String dePass = Utility.getInstancia().cifrar(password, ConstantesSec.SGD_SECRET_KEY_PASSWORD);
                    if (dePass.equals(datosUsuario.getDePassword())) {
                        passOk = true;
                        datosUsuario.setCodRespuesta("00");
                        datosUsuario.setDesRespuesta("Usuario Válido");

                    } else {
                        datosUsuario.setCodRespuesta("005");
                        datosUsuario.setDesRespuesta("El Usuario o la Contraseña es incorrecta");
                    }

                    if (esUsuario.equals("A")) {
                        if (passOk) {
                            iLoginDao.desbloquearUsuario(usuario.toUpperCase());
                            iLoginDao.getRespuestaAplicativo(datosUsuario, Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_APLICATIVO"));
                            depurador.info("CODIGO APLICATIVO 3 ==> " +datosUsuario.getEsActivo());
                            if (datosUsuario.getCodRespuesta().equals("00")) {
                                List<Map<String, Object>> dependencia = (List<Map<String, Object>>) iLoginDao.getDependenciaUsuario(usuario);
                                for (Map<String, Object> row : dependencia) {
                                    datosUsuario.setCodependencia((String) row.get("codependencia"));
                                }
                            }
                            depurador.info("CODIGO APLICATIVO 4 ==> " +datosUsuario.getEsActivo());
                        } else {
                            if (datosUsuario.getNroIntento() == 5) {
                                userBloqMaxIntentos = true;
                            }
                            iLoginDao.incrementarNumeroIntentos(usuario.toUpperCase().trim());
                        }
                    }
                } else if (esUsuario != null && esUsuario.equals("I")) {
                    userBloqMaxIntentos = true;
                } else {
                    datosUsuario.setCodRespuesta("005");
                    datosUsuario.setDesRespuesta("Usuario Bloqueado");
                }
                depurador.info("CODIGO APLICATIVO 5 ==> " +datosUsuario.getEsActivo());
                if (userBloqMaxIntentos) {
                    datosUsuario.setCodRespuesta("009");
                    datosUsuario.setDesRespuesta("Usuario bloqueado por máximo de intentos");
                }


            }
        } catch (Exception e) {
            depurador.error(null, e);
        }
        return datosUsuario;
    }

    @Override
    public DatosUsuarioConfiguracion getConfiguracionUsuario(DatosUsuario datosUsuario) throws Exception {
        // TODO Auto-generated method stub
        return iLoginDao.getConfiguracionUsuario(datosUsuario);
    }


}
