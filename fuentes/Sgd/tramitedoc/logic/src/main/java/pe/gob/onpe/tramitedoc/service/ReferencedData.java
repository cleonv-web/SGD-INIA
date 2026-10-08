/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
import pe.gob.onpe.tramitedoc.dao.MaestrosDao;

/**
 *
 * @author ECueva
 * consultar
 * 
 */

@Service(value="referencedData")
public class ReferencedData {
  
    @Autowired
    private MaestrosDao maestrosDao;
    
    
    private List<AnnioBean> grpAnnioList;
    private List<EstadoDocumentoBean> grpEstadoDocumentoEmiList;
    private List<EstadoDocumentoBean> grpEstadoDocumentoEmiSeguiList;
    private List<EstadoDocumentoBean> grpEstadoDocumentoRecepList;
    private List<EstadoDocumentoBean> grpEstadoDocumentoMpList;
    private List<PrioridadDocumentoBean> grpPrioridadDocumentoList;
    private List<PrioridadDocumentoBean> grpPrioridadDestEmiList;
    private List<EstadoDocumentoBean> grpEstadoDocumentoExtRecepList;        
    private List<TipoDocumentoBean> grpTipDocumentoList;
    private List<TipoDestinatarioEmiBean> grpTipDestEmiList;
    private List<TipoDestinatarioEmiBean> grpTipEmisorDocExtRec;
    private List<TupaExpedienteBean> grpTupaExpediente;
    private List<TupaExpedienteBean> grpTupaExpedienteNew;
    private List<TipoDocumentoBean> grpTipoDocRef;
    private List<LocalBean> grpLocal;
    private List<EstadoDocumentoBean> grpEstadoCargoEntregaList;        
    private List<EtiquetaBean> grpEtiquetasList;
    private List<VencimientoBean> grpVencimientoList;
    private List<SiElementoBean> grpElementoList;
    private List<EstadoDocumentoBean> grpEstadoDocVoboList;
    //Nuevas Funciones
    private List<DepartamentoBean> grpDepartamentoList; 
    private List<TipoDocumentoBean> grpTipDocXDependenciaList;
    private List<TipoDocumentoBean> grpTipDocumentoEmiList;
    private List<ExpedienteBean> grupoExpedienteList;
    public ReferencedData(){
        
    }
    public List<LocalBean> getLsLocales(){
        if(this.grpLocal==null){
            this.grpLocal=maestrosDao.lsLocal();
        }
        return grpLocal;
    }
    
    public List<TipoDocumentoBean> getTipoDocRefList(String CodDependencia) {
       // if(this.grpTipoDocRef == null){
            this.grpTipoDocRef = maestrosDao.listTipDocReferencia(CodDependencia);
        //}
        return grpTipoDocRef;
    }      
    
    public List<TupaExpedienteBean> getTupaExpList() {
        if(this.grpTupaExpediente == null){
            this.grpTupaExpediente = maestrosDao.listTupaExpediente();
        }
        return grpTupaExpediente;
    } 
    
    public List<TupaExpedienteBean> getTupaExpListNew() {
        if(this.grpTupaExpedienteNew == null){
            this.grpTupaExpedienteNew = maestrosDao.listTupaExpedienteNew();
        }
        return grpTupaExpedienteNew;
    }     
    
    
    public List<AnnioBean> getAnnioList() {
        if(this.grpAnnioList == null){
            this.grpAnnioList = maestrosDao.listAnnioEjec();
        }
        return grpAnnioList;
    }
    
    
    public List<TipoDestinatarioEmiBean> getLstEmisorDocExtRecep() {
        if(this.grpTipEmisorDocExtRec == null){
            this.grpTipEmisorDocExtRec = maestrosDao.listTipEmisorDocExtRecep();
        }
        return grpTipEmisorDocExtRec;
    }
    
   
     public List<EstadoDocumentoBean> getLstEstadoDocumentoExtRecep(String nomTabla) {
      
       
        return maestrosDao.listEstadosDocumentoMP(nomTabla);
    }     
    
    public List<EstadoDocumentoBean> getLstEstadoCargoEntrega(String nomTabla) {//ecueva
        
       return maestrosDao.listEstadosDocumento(nomTabla);
    }    
    
   public List<EstadoDocumentoBean> getLstEstMensajeria(String nomTabla) {//ecueva
       /* if(this.grpEstadoCargoEntregaList == null){
            this.grpEstadoCargoEntregaList =*/return maestrosDao.listEstMensajeria(nomTabla);
        /*}
        return grpEstadoCargoEntregaList;*/
    }  
   
    public List<EstadoDocumentoBean> getLstEstadosDocumentoEmi(String nomTabla) {
        if(this.grpEstadoDocumentoEmiList == null){
            this.grpEstadoDocumentoEmiList = maestrosDao.listEstadosDocumento(nomTabla);
        }
        return grpEstadoDocumentoEmiList;
    }  
    
    public List<EstadoDocumentoBean> getLstEstadosDocumentoRec(String nomTabla) {
        if(this.grpEstadoDocumentoRecepList == null){
            this.grpEstadoDocumentoRecepList = maestrosDao.listEstadosDocumento(nomTabla);
        }
        return grpEstadoDocumentoRecepList;
    }    
    
    public List<EstadoDocumentoBean> getLstEstadosDocumentoMp(String nomTabla) {
        if(this.grpEstadoDocumentoMpList == null){
            this.grpEstadoDocumentoMpList = maestrosDao.listEstadosDocumento(nomTabla);
        }
        return grpEstadoDocumentoMpList;
    }    
    
    public List<PrioridadDocumentoBean> getPrioridadesDocumentoList() {
        if(this.grpPrioridadDocumentoList == null){
            this.grpPrioridadDocumentoList = maestrosDao.listPrioridadDocumento();
        }
        return grpPrioridadDocumentoList;
    }
    
    public List<TipoDocumentoBean> getTipoDocumentoList(String CodDependencia) {
        //if(this.grpTipDocumentoList == null){
            this.grpTipDocumentoList = maestrosDao.listTipoDocumento(CodDependencia);
      //  }
        return this.grpTipDocumentoList;
    } 
    
    public List<TipoDocumentoBean> getTipoDocumentoEmiList(String CodDependencia) {
       // if(this.grpTipDocumentoEmiList == null){
            this.grpTipDocumentoEmiList = maestrosDao.listTipoDocumentoEmi(CodDependencia);
        //}
        return this.grpTipDocumentoEmiList;
    }    
    public List<RemitenteBean> getFirmadoPorList(String CodDependencia) {         
        return this.maestrosDao.getFirmadoPorList(CodDependencia);
    }    
    
    public List<ExpedienteBean> getExpedienteList(String CodDependencia) {
        if(this.grupoExpedienteList == null){
            this.grupoExpedienteList = maestrosDao.listExpedientes(CodDependencia);
        }
        return this.grupoExpedienteList;
    }
    
    public List<RemitenteBean> getListRemitente(String annio, UsuarioConfigBean usuarioConfigBean){
        List<RemitenteBean> list = null;
        try{
            list = maestrosDao.listRemitente(annio, usuarioConfigBean);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }    
    
    public List<DestinatarioBean> getListDestinatario(String annio, String coDependencia, String ptiAcceso,String pcoEmpleado){
        List<DestinatarioBean> list = null;
        try{
            list = maestrosDao.listDestinatario(annio, coDependencia,ptiAcceso, pcoEmpleado);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }  
    
    public List<EmpleadoBean> getListEmpleadoDependencia(String coDependencia){
        List<EmpleadoBean> list = null;
        try{
            list = maestrosDao.listEmpleadoDependencia(coDependencia);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<RemitenteBean> getListReferenciaOrigen(String annio, UsuarioConfigBean usuarioConfigBean){
        List<RemitenteBean> list = null;
        try{
            list = maestrosDao.listReferenciaOrigen(annio, usuarioConfigBean);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<RemitenteBean> getListReferenciaOrigenVB(String annio, String ptiAcceso, UsuarioConfigBean usuarioConfigBean){
        List<RemitenteBean> list = null;
        try{
            list = maestrosDao.listReferenciaOrigenVB(annio, ptiAcceso, usuarioConfigBean);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<DependenciaBean> getListDestinatarioEmi(String annio, UsuarioConfigBean usuarioConfigBean){
        List<DependenciaBean> list = null;
        try{
            list = maestrosDao.listDestinatarioEmi(annio, usuarioConfigBean);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    } 
    
    //YUAL
    public List<EmpleadoBean> getListEmpleadoDestino(String coDependencia,String ptiAcceso,String pcoEmpleado){
        List<EmpleadoBean> list = null;
        try{
            list = maestrosDao.listEmpleadoDestino(coDependencia,ptiAcceso,pcoEmpleado);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    } 
    
    
    public List<EmpleadoBean> getListEmpleadoElaboradoPor(String coDependencia,String ptiAcceso,String pcoEmpleado){
        List<EmpleadoBean> list = null;
        try{
            list = maestrosDao.listEmpleadoElaboradoPor(coDependencia,ptiAcceso,pcoEmpleado);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }    
    
    public List<EmpleadoBean> getListEmpleadoElaboradoPorVB(String coDependencia,String ptiAcceso,String pcoEmpleado){
        List<EmpleadoBean> list = null;
        try{
            list = maestrosDao.listEmpleadoElaboradoPorVB(coDependencia,ptiAcceso,pcoEmpleado);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }  
    
    public List<EmpleadoBean> getListEmpleadoElaboradoPorMP(String coDependencia,String ptiAcceso,String pcoEmpleado){
        List<EmpleadoBean> list = null;
        try{
            list = maestrosDao.listEmpleadoElaboradoPorMP(coDependencia,ptiAcceso,pcoEmpleado);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<DependenciaBean> getListDependenciaDestinatarioEmi(String coDepen, String deDepen){
        List<DependenciaBean> list = null;
        try{
            list = maestrosDao.listDependenciaDestinatarioEmi(coDepen, deDepen);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<TipoDocumentoBean> listTipDocXDependencia(String CodDependencia) {
      //  if(this.grpTipDocXDependenciaList == null){
            this.grpTipDocXDependenciaList = maestrosDao.listTipDocXDependencia(CodDependencia);
     //   }
        return this.grpTipDocXDependenciaList;
    }
    public List<TipoDocumentoBean> listTipDocXDependenciaPersonal(String CodDependencia) {         
        return maestrosDao.listTipDocXDependenciaPersonal(CodDependencia);
    }
    public List<PrioridadDocumentoBean> getLstPrioridadDestEmi() {
        if(this.grpPrioridadDestEmiList == null){
            this.grpPrioridadDestEmiList = maestrosDao.listPrioridadDocTblEmi();
        }
        return grpPrioridadDestEmiList;
    } 
    
    public List<TipoDocumentoBean> getLstTipDocReferencia(String CodDependencia) {
        return this.listTipDocXDependencia(CodDependencia);
    }   
    
    public List<TipoDestinatarioEmiBean> getTipoDestinatarioEmiList() {
        if(this.grpTipDestEmiList == null){
            this.grpTipDestEmiList = maestrosDao.listTipDestinatarioEmi();
        }
        return grpTipDestEmiList;
    }
    
    public List<GrupoDestinatarioBean> getGrupoDestinatario(String codDependencia){
        return maestrosDao.listGrupoDestinatario(codDependencia);
    }
    
    public List<MotivoBean> getLstMotivoDestinatario(String codDependencia, String coTipoDoc){
        return maestrosDao.listMotivoDestinatario(codDependencia,coTipoDoc);
    }
    
    public List<DependenciaBean> getLstDependenciaRemitenteEmi(String coDependencia){
       return maestrosDao.listDependenciaRemitenteEmi(coDependencia); 
    }

    public List<LocalBean> getLstLocalRemitenteEmi( String coDependencia ){
       return maestrosDao.listLocalRemitenteEmi(coDependencia); 
    }

    public List<LocalBean> getLstLocal(){
       return maestrosDao.listLocal(); 
    }
    
    public List<RemitenteBean> getListReferenciaOrigenPersonal(String sCoEmpEmi){
        List<RemitenteBean> list = null;
        try{
            list = maestrosDao.listReferenciaOrigenPersonal(sCoEmpEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<DependenciaBean> getListDestinatarioEmiPersonal(String sCoEmpEmi){
        List<DependenciaBean> list = null;
        try{
            list = maestrosDao.listDestinatarioEmiPersonal(sCoEmpEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }   
    
    public DependenciaBean getDatosDependencia(String codDependencia){
        String deSigla = null;
        DependenciaBean depEmi = null;
        try{
            depEmi = maestrosDao.getDatosDependencia(codDependencia);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return depEmi;        
    }

    public List<EtiquetaBean> getEtiquetasList() {
        if (this.grpEtiquetasList == null) {
            this.grpEtiquetasList = maestrosDao.getEtiquetasList();
        }
        return grpEtiquetasList;
    }
    
    public List<DependenciaBean> getListDestinatarioEmiDocExt(String annio, String coDep){
        List<DependenciaBean> list = null;
        try{
            list = maestrosDao.getlsDestinatarioEmiDocExt(annio, coDep);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<DependenciaBean> getListDestinatarioDocPendEntrega(String coDep){
        List<DependenciaBean> list = null;
        try{
            list = maestrosDao.getlsDestinatarioDocPendEntrega(coDep);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;        
    }

    public List<VencimientoBean> getVencimientoList() {
        if (this.grpVencimientoList == null) {
            this.grpVencimientoList = maestrosDao.getVencimientoList();
        }
        return grpVencimientoList;
    }

    public List<EstadoDocumentoBean> getLstEstadosDocumentoEmiSegui(String tdtv_destinos) {
        if(this.grpEstadoDocumentoEmiSeguiList == null){
            this.grpEstadoDocumentoEmiSeguiList = maestrosDao.listEstadosDocumentoEmiSegui(tdtv_destinos);
        }
        return grpEstadoDocumentoEmiSeguiList;
    }
    
    public List<SiElementoBean> grpElementoList(String pctabCodtab){
        this.grpElementoList = maestrosDao.getLsSiElementoBean(pctabCodtab);
        return grpElementoList;        
    }
    
   public List<SiElementoBean> grpTipoProyList(String pctabCodtab){
        this.grpElementoList = maestrosDao.getLsSiElementoBeanOrder(pctabCodtab);
        return grpElementoList;        
    }
    
    public List<EstadoDocumentoBean> getLstEstadosDocVoBo(String nomTabla) {
        if(this.grpEstadoDocVoboList == null){
            this.grpEstadoDocVoboList = maestrosDao.listEstadosDocumento(nomTabla);
        }
        return grpEstadoDocVoboList;
    } 
    
    public List<DepartamentoBean> getlistaDepartamento() {
        if(this.grpDepartamentoList == null){
            this.grpDepartamentoList = maestrosDao.listDepartamentos();
        }
        return grpDepartamentoList;
    }
    
    public List<DependenciaBean> getNewUpdListDependenciaDestinatarioEmi(String coDepen, String deDepen){
        List<DependenciaBean> list = null;
        try{
            list = maestrosDao.listNewUpdDependenciaDestinatarioEmi(coDepen, deDepen);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<ProvinciaBean> listProvincia(String coDep) {
        List<ProvinciaBean> list = null;
        try {
             list = maestrosDao.listProvincia(coDep);            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public List<DistritoBean> listDistrito(String coDep,String coDis) {
        List<DistritoBean> list = null;
        try {
             list = maestrosDao.listDistrito(coDep,coDis);            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }
    
    public void emptyListMaestra(){
        if(this.grpTupaExpediente!=null){
            this.grpTupaExpediente = null;
            this.grpTupaExpediente = maestrosDao.listTupaExpediente();
        }
        if(this.grpTupaExpedienteNew!=null){
            this.grpTupaExpedienteNew = null;
            this.grpTupaExpedienteNew = maestrosDao.listTupaExpedienteNew();
        }        
        if(this.grpLocal!=null){
                this.grpLocal = null;
                this.grpLocal = maestrosDao.lsLocal();
        }
        if(this.grpAnnioList!=null){
                this.grpAnnioList = null;
                this.grpAnnioList = maestrosDao.listAnnioEjec();
        }
        if(this.grpTipEmisorDocExtRec!=null){
                this.grpTipEmisorDocExtRec = null;
                this.grpTipEmisorDocExtRec = maestrosDao.listTipEmisorDocExtRecep();
        }
        if(this.grpEstadoDocumentoExtRecepList!=null){
                this.grpEstadoDocumentoExtRecepList = null;
        }
        if(this.grpEstadoCargoEntregaList!=null){
                this.grpEstadoCargoEntregaList = null;
        }
        if(this.grpEstadoDocumentoEmiList!=null){
                this.grpEstadoDocumentoEmiList = null;
        }
        if(this.grpEstadoDocumentoRecepList!=null){
                this.grpEstadoDocumentoRecepList = null;
        }
        if(this.grpEstadoDocumentoMpList!=null){
                this.grpEstadoDocumentoMpList = null;
        }
        if(this.grpPrioridadDocumentoList!=null){
                this.grpPrioridadDocumentoList = null;
                this.grpPrioridadDocumentoList = maestrosDao.listPrioridadDocumento();
        }
        if(this.grpPrioridadDestEmiList!=null){
                this.grpPrioridadDestEmiList = null;
                this.grpPrioridadDestEmiList = maestrosDao.listPrioridadDocTblEmi();
        }
        if(this.grpTipDestEmiList!=null){
                this.grpTipDestEmiList = null;
                this.grpTipDestEmiList = maestrosDao.listTipDestinatarioEmi();
        }
        if(this.grpEtiquetasList!=null){
                this.grpEtiquetasList = null;
                this.grpEtiquetasList = maestrosDao.getEtiquetasList();
        }
        if(this.grpVencimientoList!=null){
                this.grpVencimientoList = null;
                this.grpVencimientoList = maestrosDao.getVencimientoList();
        }
        if(this.grpEstadoDocumentoEmiSeguiList!=null){
                this.grpEstadoDocumentoEmiSeguiList = null;
        }
        if(this.grpElementoList!=null){
                this.grpElementoList = null;
        }  
        this.grpEstadoDocumentoMpList= null;  
        this.grpTipDocumentoEmiList= null;
        this.grupoExpedienteList= null;
        this.grpTipDocumentoList= null;  
        this.grpTipDocXDependenciaList= null; 
    }
    
    public void emptyListTupaExpediente() {
        if (this.grpTupaExpedienteNew != null) {
            this.grpTupaExpedienteNew = null;
        }
    }    
}
