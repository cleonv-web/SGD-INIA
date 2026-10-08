/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.ConConfigBusquedaEmiDocBean;
import pe.gob.onpe.tramitedoc.bean.ConRecepcionDet;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;

/**
 *
 * @author NGilt
 */
public interface ConsultaRecepDocService {
    BuscarDocumentoRecepConsulBean estadoRecepcionDocumento(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean,String accion);
    List<DocumentoRecepConsulBean> getDocumentosBuscaRecepAdm(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean);
    DocumentoRecepConsulBean getDocumentoRecepAdm(String pnuAnn,String pnuEmi,String pnuDes);
    List<ReferenciaConsulBean> getDocumentosRefRecepAdm(String pnuAnn,String pnuEmi);
    HashMap getDocumentosEnReferencia(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean);
    String getRutaReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean);
    ReporteBean getGenerarReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean,Map parametros);
    List<DocumentoRecepConsulBean> getListaReporte(BuscarDocumentoRecepConsulBean buscarDocumentoRecepConsulBean);
}
