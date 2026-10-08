/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.CiudadanoBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioOtroOrigenBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ProcesoExpBean;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.RequisitoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocExternoRecepBean;

/**
 *
 * @author ecueva
 */
public interface DocumentoExtRecepService {
    List<DocumentoExtRecepBean> getDocumentosExtRecep(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean);
    DocumentoExtRecepBean getDocumentoExtRecepNew(String pcoDependencia);
    List<MotivoBean> getLstMotivoxTipoDocumento();
    String grabaDocumentoExternoRecep(TrxDocExternoRecepBean trxDocExternoRecepBean, Usuario usuario) throws Exception;
    String isExpedienteDuplicado(String pnuAnnExp, String pcoDepExp, String pnuCorrExp);
    List<ReferenciaDocExtRecepBean> getLstDocExtReferencia(String pnuAnn,String pcoTiDoc,String pcoDepEmi,String pnuExpediente);
    String getJsonRptGrabaDocumentoExternoRecep(TrxDocExternoRecepBean trxDocExternoRecepBean);
    DocumentoExtRecepBean getDocumentoExtRec(String pnuAnn,String pnuEmi);
    List<DestinatarioDocumentoEmiBean> getLstDestinoEmiDoc(String pnuAnn, String pnuEmi);
    List<ReferenciaDocExtRecepBean> getLstReferenciaDoc(String pnuAnn, String pnuEmi);
    List<ProveedorBean> getLstProveedores(String prazonSocial);
    String getCiudadano(String pnuDoc,String Usuario);
    List<DestinatarioOtroOrigenBean> getLstOtrosOrigenes(String pdescripcion);
    String getProveedor(String pnuRuc);
    ProcesoExpBean getProcesoExpedienteObj(String pcoProceso);
    String VerificaToObservado(DocumentoExtRecepBean documentoExtRecepBean, Usuario usuario) throws Exception;
    String changeToRegistrado(DocumentoExtRecepBean documentoExtRecepBean, Usuario usuario) throws Exception;
    String changeToEnRegistro(DocumentoExtRecepBean documentoExtRecepBean, Usuario usuario) throws Exception;
    String getVerificaPdfDoc(String pnuAnn, String pnuEmi, String ptiCap);
    String anularDocumentoExtRecep(DocumentoExtRecepBean documentoExtRecepBean, Usuario usuario);
    Map getNewDocExtRecepAtender(DocumentoBean docRecBean);
    //String getNuDiaAteTupa(String pcoProceso);
    String changeToParaVerificar(DocumentoExtRecepBean documentoExtRecepBean, Usuario usuario) throws Exception;
    String changeToObservado(DocumentoExtRecepBean documentoExtRecepBean, Usuario usuario) throws Exception;
    String getProcesoExpediente(String pcoProceso);
    List<DestinatarioDocumentoEmiBean> getLsDestinoNewDocExtMesaPartes();
    List<RemitenteBean> getAllDependencias();
    List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp, String codProceso);
    String guardarReqExpDocExtRec(List<RequisitoBean> list, String nuAnnExp, String nuSecExp, String coProceso, String coUsuario) throws Exception ;
    String getEsDocEmi(String nuAnn, String nuEmi);
    String getPkExpDocExtOrigen(String pkEmi);
    List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp); 
    List<DocumentoBean> getListaReporteVoucher(DocumentoBean documento);
    ReporteBean getGenerarReporteVoucher(Map parametros, DocumentoBean documento);
    ReporteBean getGenerarReporte(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean, Map parametros);
    List<DocumentoExtRecepBean> getListaReporte(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean);
    List<CiudadanoBean> getLstCiudadanos(String sDescCiudadano);
    DocumentoExtRecepBean getDocumentosExpInteExt(String nuExpediente);
    List<SiElementoBean> getLsBuscardoExpIntExt(String codigoEmpleado);
}
