/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinoDetalleBean;

/**
 *
 * @author ngilt
 */
public interface GrupoDestinoService {
    public List<GrupoDestinoBean> getGruposDestinosList(String codDependencia);
    public List<GrupoDestinoDetalleBean> getGrupoDestinoDetalleList(String codGrupoDestino);
    public List<DependenciaBean> getDependenciasList(String codDepen);
    public String insDependenciaDestino(GrupoDestinoDetalleBean destDet,String coUsuario);
    public List<EmpleadoBean> getEmpleadosDestList(String codDepen,String codGrupoDest);
    public String updDependenciaDestino(GrupoDestinoDetalleBean destDet,String codEmpActual,String coUsuario);
    public String eliDetalleGrupoDest(GrupoDestinoDetalleBean destDet);
    public String insGrupoDest(GrupoDestinoBean gruDest,String coUsuario);
    public String eliGrupoDestino(GrupoDestinoBean gruDest,String coUsuario);
    public List<EmpleadoBean> getEmpleadosDependenciaList(String codDepen);
    public String insNuevoGrupoDest(GrupoDestinoBean gruDest, String codCurrentUser)throws Exception;
    public String updGrupoDest(GrupoDestinoBean gruDest, String codCurrentUser)throws Exception;
    /*SEGDI MVALDERA*/
    public List<DependenciaBean> getDestinatariosList(String coTipo);
    public String insNuevoGrupoDestVar(GrupoDestinoBean gruDest, String codCurrentUser)throws Exception;
    public List<GrupoDestinoDetalleBean> getGrupoDestinoVarDetalleList(String codGrupoDestino,String codTipoDestino);
    public List<GrupoDestinoBean> getGruposDestinosVarList(String codTipo);
    public String updGrupoDestVar(GrupoDestinoBean gruDest, String codCurrentUser)throws Exception;
    public String eliGrupoDestinoVar(GrupoDestinoBean gruDest,String coUsuario);
    public String eliDetalleGrupoDestVar(GrupoDestinoDetalleBean destDet);    
    /*SEGDI MVALDERA*/
}
