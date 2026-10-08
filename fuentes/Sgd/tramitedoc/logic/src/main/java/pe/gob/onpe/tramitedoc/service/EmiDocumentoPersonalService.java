package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.bean.TrxDocumentoEmiBean;



public interface EmiDocumentoPersonalService {
    List<DocumentoEmiBean> getDocumentosEmiAdm(BuscarDocumentoEmiBean buscarDocumentoEmiBean);
    DocumentoEmiBean getDocumentoEmiAdm(String pnuAnn,String pnuEmi);
    List<DestinatarioDocumentoEmiBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi);
    List<ReferenciaBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi);
    DocumentoEmiBean getDocumentoEmiAdmNew(String codDependencia,String codEmpleado,String codLocal);
    String grabaDocumentoEmiAdm(TrxDocumentoEmiBean trxDocumentoEmiBean, Usuario usuario) throws Exception;
    String verificaNroDocumentoEmiDuplicado(DocumentoEmiBean documentoEmiBean);
    String changeToProyecto(DocumentoEmiBean documentoEmiBean, Usuario usuario);
    String changeToEmitido(DocumentoEmiBean documentoEmiBean, String prutaDoc, Usuario usuario) throws Exception;
    String updEstadoDocumentoReferenciasEmitido(String coUseMod,String nuEmi,String nuAnn,String nuDes);
    String cargaDocEmi(DocumentoObjBean docObjBean);
    String anularDocumento(DocumentoEmiBean documentoEmiBean, Usuario usuario);
    List<ReferenciaBean> getLstDocumReferenciaAtiendeDeriva(DocumentoBean documentoRecepBean);
    String getNumeroDocSiguientePersonal(String pnuAnn, String pcoEmp, String pcoDoc);
    
}