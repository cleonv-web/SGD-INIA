/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;


import java.util.List;
import java.util.HashMap;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;

/**
 *
 * @author ECueva
 */
public interface ConsultaEmiDocService {
  public List<DocumentoEmiConsulBean> getDocumentosBuscaEmiAdm(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);  
  DocumentoEmiConsulBean getDocumentoEmiAdm(String pnuAnn, String pnuEmi);
  String getTipoDestinatarioEmi(String pnuAnn, String pnuEmi);
  HashMap getLstDestintariotlbEmi(String pnuAnn, String pnuEmi);
  List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi);
  HashMap getDocumentosEnReferencia(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);
  String getRutaReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);
  public List<DocumentoEmiConsulBean> getListaReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean);
  public ReporteBean getGenerarReporte(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean,Map parametros);
}
