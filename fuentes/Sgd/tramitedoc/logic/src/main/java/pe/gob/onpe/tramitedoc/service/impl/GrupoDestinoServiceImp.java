/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoDetalleBean;
import pe.gob.onpe.tramitedoc.dao.GrupoDestinoDao;
import pe.gob.onpe.tramitedoc.service.GrupoDestinoService;

/**
 *
 * @author ngilt
 */
@Service("grupoDestinoService")
public class GrupoDestinoServiceImp implements GrupoDestinoService {

    @Autowired
    private GrupoDestinoDao grupoDestinoDao;

    public List<GrupoDestinoBean> getGruposDestinosList(String codDependencia) {
        List<GrupoDestinoBean> list = null;
        try {
            list = grupoDestinoDao.getGruposDestinosList(codDependencia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<GrupoDestinoDetalleBean> getGrupoDestinoDetalleList(String codGrupoDestino) {
        List<GrupoDestinoDetalleBean> list = null;
        try {
            list = grupoDestinoDao.getGrupoDestinoDetalleList(codGrupoDestino);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<DependenciaBean> getDependenciasList(String codDepen) {
        List<DependenciaBean> list = null;
        try {
            list = grupoDestinoDao.getDependenciasList(codDepen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String insDependenciaDestino(GrupoDestinoDetalleBean destDet, String coUsuario) {
        String vReturn = "NO_OK";
        try {
            destDet.setEs_dep_gru("1");
            destDet.setCo_use_cre(coUsuario);
            destDet.setCo_use_mod(coUsuario);
            vReturn = grupoDestinoDao.insDependenciaDestino(destDet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public List<EmpleadoBean> getEmpleadosDestList(String codDepen, String codGrupoDest) {
        List<EmpleadoBean> list = null;
        try {
            list = grupoDestinoDao.getEmpleadosDestList(codDepen, codGrupoDest);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String updDependenciaDestino(GrupoDestinoDetalleBean destDet, String codEmpActual, String coUsuario) {
        String vReturn = "NO_OK";
        try {
            destDet.setCo_use_mod(coUsuario);
            vReturn = grupoDestinoDao.updDependenciaDestino(destDet, codEmpActual);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public String eliDetalleGrupoDest(GrupoDestinoDetalleBean destDet) {

        
        String vReturn = "NO_OK";
        try {
            vReturn = grupoDestinoDao.eliDetalleGrupoDest(destDet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;

    }

    public String insGrupoDest(GrupoDestinoBean gruDest, String coUsuario) {
        String vReturn = "NO_OK";
        try {
            gruDest.setEsGruDes("1");
            gruDest.setCoUseCre(coUsuario);
            gruDest.setCoUseMod(coUsuario);
            gruDest.setDeGruDes(gruDest.getDeGruDes().toUpperCase());

            vReturn = grupoDestinoDao.insGrupoDest(gruDest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public String eliGrupoDestino(GrupoDestinoBean gruDest, String coUsuario) {
        String vReturn = "NO_OK";
        try {
            gruDest.setCoUseMod(coUsuario);
            vReturn = grupoDestinoDao.eliGrupoDestino(gruDest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public List<EmpleadoBean> getEmpleadosDependenciaList(String codDepen) {
        List<EmpleadoBean> list = null;
        try {
            list = grupoDestinoDao.getEmpleadosDependenciaList(codDepen);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String insNuevoGrupoDest(GrupoDestinoBean gruDest, String codCurrentUser) throws Exception {
        String vReturn = "NO_OK";
        ArrayList<GrupoDestinoDetalleBean> grupoDestinoDetalles = gruDest.getGrupoDestinoDetalle();
        try {
            gruDest.setEsGruDes("1");
            gruDest.setCoUseCre(codCurrentUser);
            gruDest.setCoUseMod(codCurrentUser);
            gruDest.setDeGruDes(gruDest.getDeGruDes().toUpperCase());
            vReturn=grupoDestinoDao.getCantDuplicadoGrupoDestino(gruDest.getDeGruDes(), gruDest.getCoDep());
            if(vReturn.equals("0")){
                vReturn = grupoDestinoDao.insGrupoDest(gruDest);
                String auxCoGrupoDest = vReturn;
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al crear el grupo destino.");
                }
                for (GrupoDestinoDetalleBean grupoDestinoDetalle : grupoDestinoDetalles) {
                    grupoDestinoDetalle.setCo_gru_des(auxCoGrupoDest);
                    grupoDestinoDetalle.setEs_dep_gru("1");
                    grupoDestinoDetalle.setCo_use_cre(codCurrentUser);
                    grupoDestinoDetalle.setCo_use_mod(codCurrentUser);
                    vReturn = grupoDestinoDao.insDependenciaDestino(grupoDestinoDetalle);
                    if ("NO_OK".equals(vReturn)) {
                        throw new validarDatoException("Error al ingresar detalle grupo destino.");
                    }
                }
            }else{
                vReturn = "NO_OK";
                throw new validarDatoException("Error nombre de grupo duplicado.");
            }
        } catch (validarDatoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR GRABAR GRUPO DESTINO.");
        }
        return vReturn;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updGrupoDest(GrupoDestinoBean gruDest, String codCurrentUser) throws Exception {
        String vReturn = "NO_OK";
        ArrayList<GrupoDestinoDetalleBean> grupoDestinoDetalles = gruDest.getGrupoDestinoDetalle();
        try {
            gruDest.setCoUseMod(codCurrentUser);
            gruDest.setDeGruDes(gruDest.getDeGruDes().toUpperCase());
            vReturn = grupoDestinoDao.updGrupoDestinoCabecera(gruDest);
            if ("NO_OK".equals(vReturn)) {
                throw new validarDatoException("Error al crear el grupo destino");
            }
            List<GrupoDestinoDetalleBean> listaEliminarGrupoDestinoDetallesActual = getGrupoDestinoDetalleList(gruDest.getCoGruDes());
            for (GrupoDestinoDetalleBean grupoDestinoDetalleActual : listaEliminarGrupoDestinoDetallesActual) {
                vReturn = eliDetalleGrupoDest(grupoDestinoDetalleActual);
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al eliminar detalle del grupo");
                }
            }
            for (GrupoDestinoDetalleBean grupoDestinoDetalle : grupoDestinoDetalles) {
                grupoDestinoDetalle.setCo_gru_des(gruDest.getCoGruDes());
                grupoDestinoDetalle.setEs_dep_gru("1");
                grupoDestinoDetalle.setCo_use_cre(codCurrentUser);
                grupoDestinoDetalle.setCo_use_mod(codCurrentUser);
                vReturn = grupoDestinoDao.insDependenciaDestino(grupoDestinoDetalle);
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al ingresar detalle grupo destino");
                }
            }
        } catch (validarDatoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR GRABAR GRUPO DESTINO");
        }
        return vReturn;
    }

    @Override
    public List<DependenciaBean> getDestinatariosList(String coTipo) {
        List<DependenciaBean> list = null;
        try {
            list = grupoDestinoDao.getDestinatariosList(coTipo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public String insNuevoGrupoDestVar(GrupoDestinoBean gruDest, String codCurrentUser) throws Exception {
        String vReturn = "NO_OK";
        ArrayList<GrupoDestinoDetalleBean> grupoDestinoDetalles = gruDest.getGrupoDestinoDetalle();
        try {
            gruDest.setEsGruDes("1");
            gruDest.setCoUseCre(codCurrentUser);
            gruDest.setCoUseMod(codCurrentUser);
            gruDest.setDeGruDes(gruDest.getDeGruDes().toUpperCase());
            vReturn=grupoDestinoDao.getCantDuplicadoGrupoDestinoVar(gruDest.getDeGruDes(), gruDest.getCoDep());
            if(vReturn.equals("0")){
                vReturn = grupoDestinoDao.insGrupoDestVar(gruDest);
                String auxCoGrupoDest = vReturn;
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al crear el grupo destino.");
                }
                for (GrupoDestinoDetalleBean grupoDestinoDetalle : grupoDestinoDetalles) {
                    
                    grupoDestinoDetalle.setCo_gru_des(auxCoGrupoDest);
                    grupoDestinoDetalle.setEs_dep_gru("1");
                    grupoDestinoDetalle.setCo_use_cre(codCurrentUser);
                    grupoDestinoDetalle.setCo_use_mod(codCurrentUser);
                    grupoDestinoDetalle.setCo_dep(gruDest.getCoDep());
                    vReturn = grupoDestinoDao.insDependenciaDestinoVar(grupoDestinoDetalle);
                    if ("NO_OK".equals(vReturn)) {
                        throw new validarDatoException("Error al ingresar detalle grupo destino.");
                    }
                }
            }else{
                vReturn = "NO_OK";
                throw new validarDatoException("Error nombre de grupo duplicado.");
            }
        } catch (validarDatoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR GRABAR GRUPO DESTINO.");
        }
        return vReturn;
    }

    @Override
    public List<GrupoDestinoDetalleBean> getGrupoDestinoVarDetalleList(String codGrupoDestino,String codTipoDestino) {
        List<GrupoDestinoDetalleBean> list = null;
        try {
            list = grupoDestinoDao.getGrupoDestinoVarDetalleList(codGrupoDestino,codTipoDestino);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Override
    public List<GrupoDestinoBean> getGruposDestinosVarList(String codTipo) {
        List<GrupoDestinoBean> list = null;
        try {
            list = grupoDestinoDao.getGruposDestinosVarList(codTipo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updGrupoDestVar(GrupoDestinoBean gruDest, String codCurrentUser) throws Exception {
         String vReturn = "NO_OK";
        ArrayList<GrupoDestinoDetalleBean> grupoDestinoDetalles = gruDest.getGrupoDestinoDetalle();
        try {
            gruDest.setCoUseMod(codCurrentUser);
            gruDest.setDeGruDes(gruDest.getDeGruDes().toUpperCase());
            vReturn = grupoDestinoDao.updGrupoDestinoVarCabecera(gruDest);
            if ("NO_OK".equals(vReturn)) {
                throw new validarDatoException("Error al crear el grupo destino");
            }
            List<GrupoDestinoDetalleBean> listaEliminarGrupoDestinoDetallesActual = getGrupoDestinoVarDetalleList(gruDest.getCoGruDes());
            for (GrupoDestinoDetalleBean grupoDestinoDetalleActual : listaEliminarGrupoDestinoDetallesActual) {
//                    grupoDestinoDetalleActual.setCo_emp(grupoDestinoDetalleActual.getCo_dep());    
                    grupoDestinoDetalleActual.setCo_use_mod(codCurrentUser);
//                    grupoDestinoDetalleActual.setCo_dep(gruDest.getCoDep());                    
                    
                vReturn = eliDetalleGrupoDestVar(grupoDestinoDetalleActual);
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al eliminar detalle del grupo");
                }
            }
            for (GrupoDestinoDetalleBean grupoDestinoDetalle : grupoDestinoDetalles) {
                grupoDestinoDetalle.setCo_gru_des(gruDest.getCoGruDes());
                grupoDestinoDetalle.setEs_dep_gru("1");
                grupoDestinoDetalle.setCo_use_cre(codCurrentUser);
                grupoDestinoDetalle.setCo_use_mod(codCurrentUser);

                grupoDestinoDetalle.setCo_dep(gruDest.getCoDep());
                vReturn = grupoDestinoDao.insDependenciaDestinoVar(grupoDestinoDetalle);
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al ingresar detalle grupo destino");
                }
            }
        } catch (validarDatoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR GRABAR GRUPO DESTINO");
        }
        return vReturn;
    }
    
    public String eliDetalleGrupoDestVar(GrupoDestinoDetalleBean destDet) {

        
        String vReturn = "NO_OK";
        try {
            vReturn = grupoDestinoDao.eliDetalleGrupoDestVar(destDet);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;

    }

    public List<GrupoDestinoDetalleBean> getGrupoDestinoVarDetalleList(String codGrupoDestino) {
        List<GrupoDestinoDetalleBean> list = null;
        try {
            list = grupoDestinoDao.getGrupoDestinoVarDetalleList(codGrupoDestino);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }    

    @Override
    public String eliGrupoDestinoVar(GrupoDestinoBean gruDest, String coUsuario) {
        String vReturn = "NO_OK";
        try {
            gruDest.setCoUseMod(coUsuario);
            vReturn = grupoDestinoDao.eliGrupoDestinoVar(gruDest);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
}
