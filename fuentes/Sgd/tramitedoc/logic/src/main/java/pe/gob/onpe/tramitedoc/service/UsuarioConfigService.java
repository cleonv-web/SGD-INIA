package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.libreria.util.Mensaje;
import pe.gob.onpe.tramitedoc.bean.UsuarioBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioDepAcceso;

public interface UsuarioConfigService {

    UsuarioConfigBean getConfig(Mensaje msg,String cempCodemp, String coDep);
    UsuarioConfigBean getConfigTotal(String cempCodemp, String coDep);
    void obtieneConfig(UsuarioConfigBean usuarioConfig, String cempCodemp, String coDep);
    public String updUsuarioConfing(UsuarioConfigBean usuarioConf);
    public String insUsuarioConfing(UsuarioConfigBean usuarioConf);
    public List<UsuarioDepAcceso> getListDepAccesos(String cempCodemp, String coUsuario);
    public List<UsuarioDepAcceso> getListDepTotal(String cempCodemp);
    //YUAL
    public List<UsuarioBean> getUsuariosList();
    public UsuarioBean getUsuarioxCodEmp(String coEmp);
    public UsuarioConfigBean getTipAccesoConsulta(String codEmp, String coDep);
    public String updTipoPermisoUsuario(String codEmp, String codDep, String tipoAcceso, String tipoConsulta );
    public String insUsuarioConfingBasico(UsuarioConfigBean usuarioConf);
    
}
