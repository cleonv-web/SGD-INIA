/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiPersConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;

/**
 *
 * @author ecueva
 */
public interface ConsultaEmiDocPerService {
    List<DocumentoEmiPersConsulBean> getDocsPersConsulta(DocumentoEmiPersConsulBean buscarDocPer);
    DocumentoEmiPersConsulBean getDocumentoPersonalEmi(String pnuAnn, String pnuEmi);
    List<DestinatarioDocumentoEmiConsulBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi);
    List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi);
    String getRutaReporte(DocumentoEmiPersConsulBean buscarDocPer);
    ReporteBean getGenerarReporte(DocumentoEmiPersConsulBean buscarDocPer,Map parametros);
    List<DocumentoEmiPersConsulBean> getListaReporte(DocumentoEmiPersConsulBean buscarDocPer);
}
