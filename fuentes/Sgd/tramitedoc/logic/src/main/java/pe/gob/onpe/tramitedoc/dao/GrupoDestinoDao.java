/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoDetalleBean;

/**
 *
 * @author ngilt
 */
public interface GrupoDestinoDao {
    public List<GrupoDestinoBean> getGruposDestinosList(String codDependencia);
    public List<GrupoDestinoDetalleBean> getGrupoDestinoDetalleList(String codGrupoDestino);
    public List<DependenciaBean> getDependenciasList(String codDepen);
    public String insDependenciaDestino(GrupoDestinoDetalleBean destDet);
    public List<EmpleadoBean> getEmpleadosDestList(String codDepen,String codGrupoDest);
    public String updDependenciaDestino(GrupoDestinoDetalleBean destDet,String codEmpActual);
    public String eliDetalleGrupoDest(GrupoDestinoDetalleBean destDet);
    public String insGrupoDest(GrupoDestinoBean gruDest);
    public String eliGrupoDestino(GrupoDestinoBean gruDest);

    public List<EmpleadoBean> getEmpleadosDependenciaList(String codDepen);

    public String updGrupoDestinoCabecera(GrupoDestinoBean gruDest);
    String getCantDuplicadoGrupoDestino(String deGrupo, String coDep);
    
    public List<DependenciaBean> getDestinatariosList(String coTipo);
    String getCantDuplicadoGrupoDestinoVar(String deGrupo, String coDep);
    public String insGrupoDestVar(GrupoDestinoBean gruDest);
    public String insDependenciaDestinoVar(GrupoDestinoDetalleBean destDet);
    public List<GrupoDestinoDetalleBean> getGrupoDestinoVarDetalleList(String codGrupoDestino,String codTipoDestino);   
    public List<GrupoDestinoBean> getGruposDestinosVarList(String codTipo);
    public String updGrupoDestinoVarCabecera(GrupoDestinoBean gruDest);
    public String eliDetalleGrupoDestVar(GrupoDestinoDetalleBean destDet);
    public List<GrupoDestinoDetalleBean> getGrupoDestinoVarDetalleList(String codGrupoDestino);
    public String eliGrupoDestinoVar(GrupoDestinoBean gruDest);
    
}
