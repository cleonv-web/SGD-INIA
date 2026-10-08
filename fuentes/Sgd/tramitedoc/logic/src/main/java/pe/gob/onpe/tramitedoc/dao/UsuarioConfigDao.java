package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.libreria.util.Mensaje;
import pe.gob.onpe.tramitedoc.bean.UsuarioBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioDepAcceso;

public interface UsuarioConfigDao {
    public UsuarioConfigBean getConfig(Mensaje msg, String cempCodemp, String coDep);
    public String updUsuarioConfing(UsuarioConfigBean usuarioConf);
    public String insUsuarioConfingBasico(UsuarioConfigBean usuarioConf);
    public List<UsuarioDepAcceso> getListDepAccesos(String cempCodemp, String coUsuario);
    public String getCoDepUsuario(String pcoEmp);
    public String getTiEncargadoDep(String pcoEmp, String pcoDep);
    public List<UsuarioDepAcceso> getListDepTotal(String cempCodemp);
    public List<UsuarioBean> getUsuariosList();
    public UsuarioBean getUsuarioxCodEmp(String coEmp);
    public UsuarioConfigBean getTipAccesoConsulta(String codEmp, String coDep);
    public String updTipoPermisoUsuario(String codEmp, String codDep, String tipoAcceso, String tipoConsulta );
}
