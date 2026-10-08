/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.ProcesoExpBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RequisitoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;

/**
 *
 * @author ECueva
 */
public interface MesaPartesDao {
    List<DocumentoExtRecepBean> getDocumentosExtRecep(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean);
    DocumentoExtRecepBean getDocumentoExtRecepNew(String coDependencia);
    String getNroCorrelativoDocumento(String pnuAnn, String pcoDepEmi, String ptiEmi);
    String insExpedienteBean(ExpedienteDocExtRecepBean expedienteBean);
    List<MotivoBean> getLstMotivoxTipoDocumento();
    String insDocumentoExtBean(DocumentoExtRecepBean documentoExtRecepBean, ExpedienteDocExtRecepBean expedienteBean, RemitenteDocExtRecepBean remitenteDocExtRecepBean);
    String insDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean);
    List<ReferenciaDocExtRecepBean> getLstDocExtReferencia(String pnuAnn,String pcoTiDoc,String pcoDepEmi,String pnuExpediente);
    String insReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, ReferenciaDocExtRecepBean ref);
    String updDocumentoExtBean(String nuAnn, String nuEmi, DocumentoExtRecepBean documentoExtRecepBean, ExpedienteDocExtRecepBean expedienteBean, RemitenteDocExtRecepBean remitenteDocExtRecepBean, String pcoUserMod);
    String getNroCorrelativoEmision(String pnuAnn, String pcoDepEmi);
    String updExpedienteBean(ExpedienteDocExtRecepBean expedienteBean);
    String updDestinatarioDocumentoEmi(String nuAnn, String nuEmi, DestinatarioDocumentoEmiBean destinatarioDocumentoEmiBean);
    String delDestinatarioDocumentoEmi(String nuAnn, String nuEmi, String pnuDes);
    String updReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, ReferenciaDocExtRecepBean ref);
    String delReferenciaDocumentoEmi(String pnuAnn, String pnuEmi, String pnuAnnRef,String pnuEmiRef);
    DocumentoExtRecepBean getDocumentoExtRec(String pnuAnn,String pnuEmi);
    Map getDesFieldOtro(String pcoOtros);
    List<DestinatarioDocumentoEmiBean> getLstDestinoEmiDoc(String pnuAnn, String pnuEmi);
    List<ReferenciaDocExtRecepBean> getLstReferenciaDoc(String pnuAnn, String pnuEmi);
    DocumentoExtRecepBean getDocumentoExtRecBasic(String pnuAnn, String pnuEmi);
    String updEstadoDocumentoExt(DocumentoExtRecepBean documentoExtRecepBean);
    String anularDocumentoExtRecep(DocumentoExtRecepBean documentoExtRecepBean);
    ReferenciaDocExtRecepBean getRefAtenderDocExtRec(String pnuAnn,String pnuEmi);
    String getNumeroExpediente(ExpedienteDocExtRecepBean expedienteBean);
    //String[] getNuDiaAteTupa(String pcoProceso);
    String updFechaExpedienteMP(String coUser, String nuAnnExp, String nuSecExp);
    String getPermisoChangeEstadoMP(String coEmp, String coDep);
    ProcesoExpBean getProcesoExpediente(String pcoProceso);
    DestinatarioDocumentoEmiBean getEmpleadoDestinoDocExtMp(String pcoDependencia);
    List<RemitenteBean> getAllDependencias();
    List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp, String codProceso);
    String updReqExpedienteDocExtRec(RequisitoBean req, String nuAnnExp, String nuSecExp, String codProceso, String coUsuario);
    String getRequisitoPendiente(String nuAnnExp, String nuSecExp);
    String getPkExpDocExtOrigen(String pnuAnn, String pnuEmi);
    List<RequisitoBean> getAllRequisitoExpediente(String nuAnnExp, String nuSecExp);
    List<DocumentoBean> getListaReporteBusquedaVoucher(DocumentoBean documento);
    List<DocumentoExtRecepBean> getListaReporteBusqueda(BuscarDocumentoExtRecepBean buscarDocumentoExtRecepBean);
    String getPassDniPide(String pctabCodTab,String Dni);
    DocumentoExtRecepBean getDocumentosExpInteExt(String nuExpediente);
}
