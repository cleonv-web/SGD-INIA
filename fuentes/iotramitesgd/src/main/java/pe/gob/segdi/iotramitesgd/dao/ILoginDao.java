package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.DatosUsuario;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuarioConfiguracion;

import java.util.List;
import java.util.Map;


public interface ILoginDao {

    DatosUsuario autenticarUsuario(DatosUsuario datosUsuario, String usuario) throws Exception;

    String desbloquearUsuario(String usuario) throws Exception;

    int incrementarNumeroIntentos(String usuario) throws Exception;

    void getRespuestaAplicativo(DatosUsuario datosUsuario, String codAplicativo) throws Exception;

    DatosUsuarioConfiguracion getConfiguracionUsuario(DatosUsuario datosUsuario) throws Exception;

    List<Map<String, Object>> getDependenciaUsuario(String coduser) throws Exception;


}
