/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;

/**
 *
 * @author ECueva
 */
public interface ConsultaEmiDocDao {
    public List<DocumentoEmiConsulBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);    
    DocumentoEmiConsulBean getDocumentoEmiAdm(String pnuAnn, String pnuEmi);
    String getTipoDestinatarioEmi(String pnuAnn, String pnuEmi);
    List<DestinatarioDocumentoEmiConsulBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi);
    List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi);
    DocumentoEmiConsulBean existeDocumentoReferenciado(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);
    List<DocumentoEmiConsulBean> getDocumentosReferenciadoBusq(DocumentoEmiConsulBean documentoEmiConsulBean,String sTipoAcceso);
    String getRutaReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);
    List<DocumentoEmiConsulBean> getListaReporteBusqueda(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);    
}
