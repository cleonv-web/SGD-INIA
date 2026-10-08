/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;

/**
 *
 * @author NGilt
 */
public interface ConsultaRecepDocDao {
    List<DocumentoRecepConsulBean> getDocumentosBuscaRecepAdm(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean);
    DocumentoRecepConsulBean getDocumentoRecepAdm(String pnuAnn,String pnuEmi,String pnuDes);
    List<ReferenciaConsulBean> getDocumentosRefRecepAdm(String pnuAnn,String pnuEmi);
    DocumentoRecepConsulBean existeDocumentoReferenciado(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean);
    List<DocumentoRecepConsulBean> getDocumentosReferenciadoBusq(DocumentoRecepConsulBean documentoRecepConsulBean,String ptipoAcceso);
    String getRutaReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean);
    List<DocumentoRecepConsulBean> getListaReporteBusqueda(BuscarDocumentoRecepConsulBean buscarDocumentoExtConsulBean);
}
