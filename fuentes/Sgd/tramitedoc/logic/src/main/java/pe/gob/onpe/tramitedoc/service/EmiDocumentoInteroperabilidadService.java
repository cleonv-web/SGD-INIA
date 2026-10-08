package pe.gob.onpe.tramitedoc.service;

import java.util.HashMap;
import java.util.List;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.CiudadanoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioOtroOrigenBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoVoBoBean;
import pe.gob.onpe.tramitedoc.bean.EstadoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.TipoCategoriaBean;
import pe.gob.onpe.tramitedoc.bean.TipoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocumentoEmiBean;



public interface EmiDocumentoInteroperabilidadService {
//    List<DocumentoEmiBean> getDocumentosEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmiBean);
    List<DocumentoEmiBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmiBean);
    DocumentoEmiBean getDocumentoEmitido(String pnuAnn, String pnuEmi);
    DocumentoEmiBean getDocumentoEmiAdm(String pnuAnn,String pnuEmi);
    ExpedienteBean getExpDocumentoEmitido(String pnuAnnExp, String pnuSecExp);
    EmpleadoBean getEmpleadoLocaltblDestinatario(String pcoDependencia);
    List<EmpleadoBean> getPersonalDestinatario(String pcoDepen);
    List<MotivoBean> getLstMotivoxTipoDocumento(String pcoDepen, String pcoTipDoc);
    List<DocumentoBean> getLstDocEmitidoRef(String pcoEmpEmi,String pcoDepen,String pannio,String ptiDoc,String pnuDoc);
    List<DocumentoBean> getLstDocRecepcionadoRef(String pcoDepen,String pannio,String ptiDoc,String pnuDoc,String inMesaPartes);
    //List<DestinatarioDocumentoEmiBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi);
    HashMap getLstDestintariotlbEmi(String pnuAnn, String pnuEmi);
    List<ReferenciaBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi);
    String getTipoDestinatarioEmi(String pnuAnn, String pnuEmi);
    List<DestinatarioOtroOrigenBean> getLstOtrosOrigenesAgrega(String pdescripcion);
    List<CiudadanoBean> getLstCiudadano(String nombres);
//    String grabaDocumentoEmi(TrxDocumentoEmiBean trxDocumentoEmiBean,String sCrearExpediente)throws Exception;
    CiudadanoBean getCiudadano(String pnuDoc);
    List<ProveedorBean> getLstProveedoresAgrega(String pRazonSocial);
    String getJsonAfterGrabaDocumentoEmi(String snuAnn,String snuEmi);
    String _getJsonAfterGrabaDocumentoEmi(String snuAnn, String snuEmi);
    DocumentoEmiBean getDocumentoEmiAdmNew(String sEstadoDocEmi, String codDependencia);
    String anularDocumentoEmiAdm(DocumentoEmiBean documentoEmiBean,Usuario usuario);
    String getLstDestintarioAgregaGrupo(String pco,String pcoTipDoc);
//    ExpedienteBean grabarExpedienteBean(String pcodUserMod,String feEmi,String coDepEmi,String deDocSig);
    String cargaDocEmi(DocumentoObjBean docObjBean);
    String verificaNroDocumentoEmiDuplicado(DocumentoEmiBean documentoEmiBean);
    List<ReferenciaBean> getLstDocumReferenciaAtiendeDeriva(DocumentoBean documentoRecepBean);
    List<EmpleadoBean> getPersonalEditDocAdmEmision(String pcoDepEmi);
    String changeToProyecto(DocumentoEmiBean documentoEmiBean,Usuario usuario);
    String changeToDespacho(DocumentoEmiBean documentoEmiBean,Usuario usuario)throws Exception;
    String changeToEmitido(DocumentoEmiBean documentoEmiBean ,String prutaDoc,Usuario usuario,String nroRucInstitucion) throws Exception;
    String changeToEmitidoAlta(DocumentoEmiBean documentoEmiBean) throws Exception;
    String changeToEnvioNotificacion(DocumentoEmiBean documentoEmiBean,Usuario usuario);
    String updArchivarDocumento(DocumentoEmiBean documentoEmiBean,Usuario usuario);
    String grabaDocumentoEmiAdm(TrxDocumentoEmiBean trxDocumentoEmiBean,String pcrearExpediente,Usuario usuario) throws Exception;
    String delDocumentoEmiAdm(DocumentoEmiBean documentoEmiBean,Usuario usuario)throws Exception;
    String getNumeroDocSiguienteAdm(String pnuAnn, String pcoDepEmi, String pcoDoc);
    String getVerificaPdfDoc(String pnuAnn, String pnuEmi, String ptiCap);
    String getFormatoDoc(String pnuAnn, String pnuEmi, String ptiCap);
    HashMap getBuscaDependenciaEmite(String pcoDepen, String pdeDepEmite);
    HashMap getDocumentosEnReferencia(BuscarDocumentoEmiBean buscarDocumentoEmiBean);
    DependenciaBean cambiaDepEmi(String pcoDep);
    String[] getNotificaciones(List<EmpleadoVoBoBean> lsEmpleadoVb,String reqFirmaAnexos);
    String getInFirmaDocAdm(String tipoDoc);
    String getLstPersVoBoGrupo(String pcoGrupo);

    List<TipoCategoriaBean> listTipCategoria();
    String insProveedorEmi(ProveedorBean proveedor);
    List<TipoDocumentoBean> getTipoDocumentoEmiList();
    List<TipoDocumentoBean> getTipoDocumentoxRef();
    List<EstadoDocumentoBean> getLstEstadosDocumentoEmi(String nomTabla);
}