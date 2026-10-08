package pe.gob.onpe.tramitedoc.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.libreria.util.Mensaje;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioBean;

import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioDepAcceso;
import pe.gob.onpe.tramitedoc.dao.CommonQueryDao;
import pe.gob.onpe.tramitedoc.dao.UsuarioConfigDao;
import pe.gob.onpe.tramitedoc.service.UsuarioConfigService;

@Service("usuarioConfigService")
public class UsuarioConfigServiceImpl implements UsuarioConfigService {

    @Autowired
    private UsuarioConfigDao usuarioConfigDao;
    
    @Autowired
    private CommonQueryDao commonQueryDao;

    @Override
    public UsuarioConfigBean getConfig(Mensaje msg, String cempCodemp, String coDep) {
        UsuarioConfigBean usuConfig = null;
        String vret = "NO";
        String vtiEnc;
        if(msg==null){
            msg = new Mensaje();
        }                
        try {
            if (coDep==null || coDep.isEmpty()){
               coDep = usuarioConfigDao.getCoDepUsuario(cempCodemp);
            }
            
            usuConfig = usuarioConfigDao.getConfig(msg, cempCodemp, coDep);
            if(msg.getCoRespuesta().equals("00")){
                if (usuConfig == null) {
                    usuConfig = new UsuarioConfigBean();
                    usuConfig.setCempCodemp(cempCodemp);
                    usuConfig.setCoDep(coDep);
                    usuConfig.setTiAcceso("1");
                    usuConfig.setTiConsulta("1");
                    usuConfig.setInFirma("0");
                    usuConfig.setInCargaDocMesaPartes("0");
                    usuConfig.setInTipoDoc("0");
                    usuConfig.setInObsDocumento("0");
                    usuConfig.setInReviDocumento("0");
                    usuConfig.setInEmailDeriv("0");
                    vret = usuarioConfigDao.insUsuarioConfingBasico(usuConfig);
                    if (vret.equals("OK")) {
                        usuConfig = usuarioConfigDao.getConfig(msg, cempCodemp, coDep);
                    }
                }

                // Verificar en caso en encargado de area
                vtiEnc = usuarioConfigDao.getTiEncargadoDep(cempCodemp, coDep);
                if (vtiEnc != null){
                    if (usuConfig.getTiAcceso().equals("1")){
                        usuConfig.setTiAcceso("0");
                        usuConfig.setTiConsulta("0");
                        usuConfig.setTiAccesoMp("0");
                        usuConfig.setTiConsultaMp("0");
                    }
                }
            }

        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return usuConfig;

    }

    @Override
    public UsuarioConfigBean getConfigTotal(String cempCodemp, String coDep) {
        UsuarioConfigBean usuConfig = new UsuarioConfigBean();
        try {
            if (coDep==null || coDep.isEmpty()){
               coDep = usuarioConfigDao.getCoDepUsuario(cempCodemp);
            }
            
            DependenciaBean depUsu = commonQueryDao.getDependenciaBasico(coDep);
            usuConfig.setCempCodemp(cempCodemp);
            usuConfig.setCoDep(coDep);
            usuConfig.setDeDep(depUsu.getDeDependencia());
            usuConfig.setDeSiglasDep(depUsu.getDeSigla());
            usuConfig.setCoDepMp(depUsu.getCoDepMp());
            usuConfig.setInMesaPartes(depUsu.getInMesaPartes());
            usuConfig.setTiAcceso("0");
            usuConfig.setTiConsulta("0");
            usuConfig.setTiAccesoMp("0");
            usuConfig.setTiConsultaMp("0");
            usuConfig.setInFirma("0");
            usuConfig.setInCargaDocMesaPartes("0");
            usuConfig.setInTipoDoc("0");
            usuConfig.setInObsDocumento("0");
            usuConfig.setInReviDocumento("0");
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return usuConfig;

    }
    
    @Override
    public void obtieneConfig(UsuarioConfigBean usuarioConfig, String cempCodemp, String coDep) {
        UsuarioConfigBean usuConfig = null;

        try {

            usuConfig = usuarioConfigDao.getConfig(null, cempCodemp, coDep);
            if (usuConfig == null) {
                usuConfig = new UsuarioConfigBean();
                usuConfig.setCempCodemp(cempCodemp);
                usuConfig.setCoDep(coDep);
                usuConfig.setTiAcceso("1");
                usuConfig.setTiConsulta("1");
                usuConfig.setInFirma("0");
                usuConfig.setInCargaDocMesaPartes("0");
                usuConfig.setInTipoDoc("0");
                usuConfig.setInObsDocumento("0");
                usuConfig.setInReviDocumento("0");

                usuarioConfigDao.insUsuarioConfingBasico(usuConfig);
                usuConfig = usuarioConfigDao.getConfig(null, cempCodemp, coDep);
            }

            usuarioConfig.setCoUsuario(usuConfig.getCoUsuario());
            usuarioConfig.setCempCodemp(usuConfig.getCempCodemp());
            usuarioConfig.setInEsAdmin(usuConfig.getInEsAdmin());
            usuarioConfig.setDeRutaAnexo(usuConfig.getDeRutaAnexo());
            usuarioConfig.setDePiePagina(usuConfig.getDePiePagina());
            usuarioConfig.setCoDep(usuConfig.getCoDep());
            usuarioConfig.setDeDep(usuConfig.getDeDep());
            usuarioConfig.setCoLocal(usuConfig.getCoLocal());
            usuarioConfig.setDeLocal(usuConfig.getDeLocal());
            usuarioConfig.setTiAcceso(usuConfig.getTiAcceso());
            usuarioConfig.setTiConsulta(usuConfig.getTiConsulta());
            usuarioConfig.setInCorreo(usuConfig.getInCorreo());
            usuarioConfig.setDeCorreo(usuConfig.getDeCorreo());
            usuarioConfig.setTipoCorreo(usuConfig.getTipoCorreo());
            usuarioConfig.setInFirma(usuConfig.getInFirma());
            usuarioConfig.setInCargaDocMesaPartes(usuConfig.getInCargaDocMesaPartes());
            usuarioConfig.setInObsDocumento(usuConfig.getInObsDocumento());
            usuarioConfig.setInReviDocumento(usuConfig.getInReviDocumento());

        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    @Override
    public String updUsuarioConfing(UsuarioConfigBean usuarioConf) {
        String vReturn = "NO_OK";
        try {
            vReturn = usuarioConfigDao.updUsuarioConfing(usuarioConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public List<UsuarioDepAcceso> getListDepAccesos(String cempCodemp, String coUsuario) {

        List<UsuarioDepAcceso> list = null;

        try {
            list = usuarioConfigDao.getListDepAccesos(cempCodemp, coUsuario);
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }

    @Override
    public List<UsuarioDepAcceso> getListDepTotal(String cempCodemp) {

        List<UsuarioDepAcceso> list = null;

        try {
            list = usuarioConfigDao.getListDepTotal(cempCodemp);
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
    
    @Override
    public String insUsuarioConfing(UsuarioConfigBean usuarioConf) {
        String vReturn = "NO_OK";
        try {
            vReturn = usuarioConfigDao.insUsuarioConfingBasico(usuarioConf);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    //YUAL
    @Override
    public List<UsuarioBean> getUsuariosList() {
        List<UsuarioBean> list = null;
        try {
            list = usuarioConfigDao.getUsuariosList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    @Override
    public UsuarioBean getUsuarioxCodEmp(String coEmp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        UsuarioBean usuario = null;
        try{
           usuario = usuarioConfigDao.getUsuarioxCodEmp(coEmp);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return usuario;
    } 
    
    @Override
    public UsuarioConfigBean getTipAccesoConsulta(String codEmp, String coDep) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        UsuarioConfigBean usuario = null;
        try{
           usuario = usuarioConfigDao.getTipAccesoConsulta(codEmp, coDep);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return usuario;
    }
    
    @Override
    public String updTipoPermisoUsuario(String codEmp, String codDep, String tipoAcceso, String tipoConsulta ){
        String vReturn = "NO_OK";
        try {
            vReturn = usuarioConfigDao.updTipoPermisoUsuario(codEmp, codDep, tipoAcceso, tipoConsulta );
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String insUsuarioConfingBasico(UsuarioConfigBean usuarioConf){
        String vret = "NO";
        vret = usuarioConfigDao.insUsuarioConfingBasico(usuarioConf);
        
        return vret;
    }


}
