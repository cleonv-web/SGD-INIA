package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.DatosUsuario;
import pe.gob.segdi.iotramitesgd.bean.DatosUsuarioConfiguracion;


public interface ILoginService {

    DatosUsuario autenticarUsuario(String usuario, String password) throws Exception;


    DatosUsuarioConfiguracion getConfiguracionUsuario(DatosUsuario datosUsuario) throws Exception;

}
