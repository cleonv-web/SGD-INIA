/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.AnnioBean;
import pe.gob.onpe.tramitedoc.bean.DepartamentoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioBean;
import pe.gob.onpe.tramitedoc.bean.DistritoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.EstadoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.GrupoDestinatarioBean;
import pe.gob.onpe.tramitedoc.bean.LocalBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.OrigenDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.PrioridadDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ProvinciaBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.bean.TipoDestinatarioEmiBean;
import pe.gob.onpe.tramitedoc.bean.TipoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.TipoExpedienteEmiBean;
import pe.gob.onpe.tramitedoc.bean.TupaExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.VencimientoBean;

/**
 *
 * @author ECueva
 */
public interface MaestrosDao {

    List<AnnioBean> listAnnioEjec();
    List<EstadoDocumentoBean> listEstadosDocumento(String nomTabla);
    List<EstadoDocumentoBean> listEstMensajeria(String nomTabla);    
    List<PrioridadDocumentoBean> listPrioridadDocumento();
    List<TipoDocumentoBean> listTipoDocumento(String coDependencia);
    List<TipoDocumentoBean> listTipoDocumentoEmi(String coDependencia);
    List<RemitenteBean> getFirmadoPorList(String CodDependencia);
    List<ExpedienteBean> listExpedientes(String coDependencia);
    List<RemitenteBean> listRemitente(String annio, UsuarioConfigBean usuarioConfigBean);
    List<DestinatarioBean> listDestinatario(String annio, String coDependencia,String ptiAcceso,String pcoEmpleado);
    List<EmpleadoBean> listEmpleadoDependencia(String pcodDependencia);
    List<RemitenteBean> listReferenciaOrigen(String annio, UsuarioConfigBean usuarioConfigBean);
    List<RemitenteBean> listReferenciaOrigenVB(String annio, String ptiAcceso, UsuarioConfigBean usuarioConfigBean);
    List<DependenciaBean> listDestinatarioEmi(String annio, UsuarioConfigBean usuarioConfigBean);
    List<EmpleadoBean> listEmpleadoDestino(String pcodDependencia,String ptiAcceso,String pcoEmpleado);
    List<EmpleadoBean> listEmpleadoElaboradoPor(String pcodDependencia,String ptiAcceso,String pcoEmpleado);
    List<EmpleadoBean> listEmpleadoElaboradoPorVB(String pcodDependencia,String ptiAcceso,String pcoEmpleado);
    List<EmpleadoBean> listEmpleadoElaboradoPorMP(String pcodDependencia,String ptiAcceso,String pcoEmpleado);
    List<DependenciaBean> listDependenciaDestinatarioEmi(String coDepen, String deDepen);
    List<TipoDocumentoBean> listTipDocXDependencia(String coDependencia);
    List<TipoDocumentoBean> listTipDocXDependenciaPersonal(String coDependencia);
    List<PrioridadDocumentoBean> listPrioridadDocTblEmi();
    List<TipoDestinatarioEmiBean> listTipDestinatarioEmi();
    List<GrupoDestinatarioBean> listGrupoDestinatario(String codDependencia);
    List<MotivoBean> listMotivoDestinatario(String codDependencia, String coTipoDoc);
    List<DependenciaBean> listDependenciaRemitenteEmi(String coDependencia);
    List<LocalBean> listLocalRemitenteEmi(String coDependencia);
    List<LocalBean> listLocal();
    List<RemitenteBean> listReferenciaOrigenPersonal(String sCoEmpEmi);
    List<DependenciaBean> listDestinatarioEmiPersonal(String sCoEmpEmi);
    DependenciaBean getDatosDependencia(String codDependencia);
    List<EstadoDocumentoBean> listEstadosDocumentoMP(String nomTabla);
    List<TipoDestinatarioEmiBean> listTipEmisorDocExtRecep();
    List<TupaExpedienteBean> listTupaExpediente();
    List<TipoDocumentoBean> listTipDocReferencia(String coDependencia);
    List<LocalBean> lsLocal();
    public List<EtiquetaBean> getEtiquetasList();
    List<DependenciaBean> getlsDestinatarioEmiDocExt(String annio,String coDep);
    List<DependenciaBean> getlsDestinatarioDocPendEntrega(String coDep);
    public List<VencimientoBean> getVencimientoList();
    public List<EstadoDocumentoBean> listEstadosDocumentoEmiSegui(String tdtv_destinos);
    List<SiElementoBean> getLsSiElementoBean(String pctabCodtab);
    List<SiElementoBean> getLsSiElementoBeanOrder(String pctabCodtab);    
    List<TupaExpedienteBean> listTupaExpedienteNew();
    
    public List<DepartamentoBean> listDepartamentos();
    public List<DependenciaBean> listNewUpdDependenciaDestinatarioEmi(String coDepen, String deDepen);
    public List<ProvinciaBean> listProvincia(String coDep);
    public List<DistritoBean> listDistrito(String coDep,String coDis);
    
}
