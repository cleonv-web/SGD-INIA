/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiPersConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;

/**
 *
 * @author ecueva
 */
public interface ConsultaEmiDocPersDao {
    List<DocumentoEmiPersConsulBean> getDocsPersConsulta(DocumentoEmiPersConsulBean buscarDocPer);
    DocumentoEmiPersConsulBean getDocumentoPersonalEmi(String pnuAnn, String pnuEmi);
    List<DestinatarioDocumentoEmiConsulBean> getLstDestintariotlbEmi(String pnuAnn, String pnuEmi);
    List<ReferenciaConsulBean> getLstDocumReferenciatblEmi(String pnuAnn, String pnuEmi);
    String getRutaReporte(DocumentoEmiPersConsulBean buscarDocPer);    
    List<DocumentoEmiPersConsulBean> getListaReporteBusqueda(DocumentoEmiPersConsulBean buscarDocPer);
}
