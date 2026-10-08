/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;

/**
 *
 * @author ECueva
 */
public interface ConsultaEmiDocExtDao {
    
    List<DocumentoExtConsulBean> getDocumentosExternos(BuscarDocumentoExtConsulBean buscarDocExt);
    DocumentoExtConsulBean getDocumentoExtConsulBean(String nuAnn, String nuEmi);
    List<ReferenciaDocExtRecepBean> getLsRefDocExterno(String pnuAnn, String pnuEmi);
    List<DestinatarioDocumentoEmiConsulBean> getLsDestinoEmi(String pnuAnn, String pnuEmi);
    String getRutaReporte(BuscarDocumentoExtConsulBean DocExt);
    List<DocumentoExtConsulBean> getListaReporteBusqueda(BuscarDocumentoExtConsulBean buscarDocumentoExtConsulBean);
    
}
