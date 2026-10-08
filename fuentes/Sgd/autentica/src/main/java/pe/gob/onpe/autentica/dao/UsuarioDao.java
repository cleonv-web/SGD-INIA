package pe.gob.onpe.autentica.dao;

import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.model.UsuarioAcceso;
import pe.gob.onpe.libreria.util.Mensaje;

import java.util.List;
import pe.gob.onpe.autentica.model.DatosUsuario;
import pe.gob.onpe.autentica.model.DatosUsuarioLog;

public interface UsuarioDao {
    void getRptaIdentificacion(Mensaje msg, Usuario usuario, boolean isEncripted) throws Exception;
    String getRptaAplicativo(Mensaje msg, Usuario usuario, String coAplicativo);

    void getModificaClave(Mensaje msg, Usuario usuario) throws Exception;

    void cerrarSesionAplicativo(Usuario usuario, String coAplicativo) throws Exception;

    void getParametrosGlobales(Mensaje msg, Usuario usuario) throws Exception;

    List<UsuarioAcceso> getPermisosUsuario(Usuario usuario, String coAplicativo) throws Exception;

    void desencripta(Mensaje msg, Usuario usuario);

    public String encriptaDato(Mensaje msg, String pdato );
    public DatosUsuario getDepUsuario(String coUsuario);
    List<DatosUsuarioLog> getHistorialClave(String coUsuario);
    String getNroSgtCorrHistorialClave(String coUsuario);
    String insHistorialClave(DatosUsuarioLog data);
    String insAccesoLog(DatosUsuarioLog data);    
    String desbloquearUsuario(String coUsuario);
    String cambiarEstadoUsuario(String coEmpleado,String estado,String coUsuario);
    String resetPassword(String coUse, String clave, String coUseMod);
    String delUserAplicaOpc(String coUse);
    String delUserAplica(String coUse);
    String delUser(String coUse);
    String delClaveUserHis(String coUse);
}
