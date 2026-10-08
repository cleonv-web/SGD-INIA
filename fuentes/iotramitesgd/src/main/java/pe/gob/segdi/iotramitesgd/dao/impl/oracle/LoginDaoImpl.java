package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.SQLWarningException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuario;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuarioConfiguracion;
import pe.gob.segdi.iotramitesgd.dao.ILoginDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.LoginConst;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Objeto : LoginDaoImpl.java.
 * Descripción : Clase de servicio, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Repository("iLoginDao")
public class LoginDaoImpl extends JdbcTemplate implements ILoginDao, Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger depurador = Logger.getLogger(LoginDaoImpl.class.getName());

    @Autowired
    public LoginDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public DatosUsuario autenticarUsuario(DatosUsuario datosUsuario, String usuario) throws Exception {
        //depurador.info("autenticarUsuario ==>");
        List<DatosUsuario> datos = new ArrayList<DatosUsuario>();
        datos = query(LoginConst.GET_DATOS_USUARIO, new BeanPropertyRowMapper<DatosUsuario>(DatosUsuario.class),
                usuario);
        if (datos.size() == 0) {
            datosUsuario = new DatosUsuario();
            datosUsuario.setCodRespuesta("005");
            datosUsuario.setDesRespuesta("Usuario no existe");
        } else {
            datosUsuario = datos.get(0);
            datosUsuario.setCodRespuesta("00");

        }

        return datosUsuario;
    }

    @Override
    public String desbloquearUsuario(String ususario) throws Exception {
        ////depurador.info("desbloquearUsuario ==>"+ususario);
        String vReturn = "NO_OK";
        try {
            update(LoginConst.DESBLOQUEAR_USUARIO, ususario);
            vReturn = "OK";
        } catch (Exception e) {
            vReturn = "NO_OK";
        }
        return vReturn;

    }

    @Override
    public int incrementarNumeroIntentos(String usuario) throws Exception {
        ////depurador.info("incrementarNumeroIntentos ==>"+usuario);
        int flag = 0;
        try {
            update(LoginConst.INCREMENTAR_NUMERO_INTENTOS, usuario.trim(), usuario.trim());
        } catch (SQLWarningException e) {
            depurador.error(null, e);
        }
        return flag;
    }

    @Override
    public void getRespuestaAplicativo(DatosUsuario datosUsuario, String codAplicativo) throws Exception {
        //depurador.info("getRespuestaAplicativo ==>");

        try {
            String esactivo = queryForObject(LoginConst.GET_RESPUESTA_APLICATIVO, String.class, datosUsuario.getCodUser(), codAplicativo);

            if (datosUsuario != null) {
                if (esactivo.equals("1")) {
                    datosUsuario.setCodRespuesta("00");
                    datosUsuario.setDesRespuesta("Usuario Válido");
                } else {
                    datosUsuario.setCodRespuesta("005");
                    datosUsuario.setDesRespuesta("Usuario no tiene accesos al aplicativo");
                }
            }
        } catch (EmptyResultDataAccessException e) {
            datosUsuario.setCodRespuesta("005");
            datosUsuario.setDesRespuesta("Usuario no tiene accesos al aplicativo");
        } catch (Exception e) {
            datosUsuario.setCodRespuesta("5001");
            datosUsuario.setDesRespuesta("Error interno de base de datos, no es posible iniciar session");
        }
        ;
    }

    @Override
    public DatosUsuarioConfiguracion getConfiguracionUsuario(DatosUsuario datosUsuario) throws Exception {
        DatosUsuarioConfiguracion usuarioConfiguracion = new DatosUsuarioConfiguracion();
        usuarioConfiguracion = queryForObject(LoginConst.GET_CONFIGURACION_USUARIO, BeanPropertyRowMapper.newInstance(DatosUsuarioConfiguracion.class),
                datosUsuario.getCodependencia(),
                datosUsuario.getCempCodemp());
        return usuarioConfiguracion;
    }

    @Override
    public List<Map<String, Object>> getDependenciaUsuario(String coduser) throws Exception {
        //depurador.info("getDependenciaUsuario ==>"+coduser);
        List<Map<String, Object>> list = queryForList(LoginConst.GET_DEPENDENCIA_USUARIO,
                coduser);
        return list;
    }


}
