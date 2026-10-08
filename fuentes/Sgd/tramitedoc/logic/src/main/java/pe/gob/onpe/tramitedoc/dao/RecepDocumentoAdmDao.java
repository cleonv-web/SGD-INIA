package pe.gob.onpe.tramitedoc.dao;

import java.util.List;

import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.Paginacion;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

public interface RecepDocumentoAdmDao {

//	List<DocumentoBean> getDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep);
        List<DocumentoBean> getDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep, Paginacion paginacion);
        List<DocumentoBean> getDocumentosBuscaRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep);
        ProcessResult<List<DocumentoBean>> getDocumentosBuscaRecepAdmList(BuscarDocumentoRecepBean buscarDocumentoRecep,FiltroPaginate paginate);
        public List<DocumentoBean> getResumenRecepAdmList(BuscarDocumentoRecepBean buscarDocumentoRecep);
        List<DependenciaBean> getDocumDepenProveido(String codDepen);
        DocumentoBean getDocumentoRecepAdm(String pnuAnn,String pnuEmi,String pnuDes);
        ExpedienteBean getExpDocumentoRecepAdm(String pnuAnnExp,String pnuSecExp);
        List<ReferenciaBean> getDocumentosRefRecepAdm(String pnuAnn,String pnuEmi);
        /*List<DocumentoBean> _getDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep);*/
        String actualizarGuiaMesaPartes(DocumentoBean documentoBean);
        String getNumCorrelativoDestino(String nuAnneterr, String coDepDes);
        String actualizarEstado(DocumentoBean documentoBean);
        String updDocumentoBean(DocumentoBean documentoBean, String paccion);
        int getRowDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep);
        String getDesEstadoDocRecepcion(String sesDocRec);
        String validarAnulacionDocRecepcion(String pnuAnn,String pnuEmi,String pnuDes);
        DocumentoBean existeDocumentoReferenciado(BuscarDocumentoRecepBean buscarDocumentoRecep);
        List<DocumentoBean> getDocumentosReferenciadoBusq(DocumentoBean documentoBean,String sTipoAcceso);
        String getVerificaAtendido(String pnuAnn, String pnuEmi, String pnuDes);
        List<ReferenciaBean> getConsultaAtendido(String pnuAnn, String pnuEmi, String pnuDes);

        String updEtiquetaTipoRecepDocumento(DocumentoBean documentoBean);
        DocumentoBean getEstadoDocumento(String nuAnn, String nuEmi, String nuDes);
        String getEstadoDocAdmBasico(String nuAnn, String nuEmi);
        //servicio rest notificaciones movil
        //Anula la recepcion de un documento tabla TDTV_DESTINOS
        public String updAnulaRecepecionDocumentoBean(String nuAnn,String nuEmi, String nuDes, String coUseMod);
        //Actualiza la recepcion de un documento  tabla TDTV_DESTINOS
        public String updRecepcionDocumentoBean(DocumentoBean documentoBean);
        //Actualiza la atencion o archivamiento de un documento  tabla TDTV_DESTINOS
        public String updAtencionDocumentoBean(DocumentoBean documentoBean);        
        //servicio rest notificaciones movil
}