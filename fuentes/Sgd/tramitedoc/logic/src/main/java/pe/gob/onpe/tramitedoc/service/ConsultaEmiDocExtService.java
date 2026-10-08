/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoExtConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.RemitenteBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;

/**
 *
 * @author ecueva
 */
public interface ConsultaEmiDocExtService {
    List<DocumentoExtConsulBean> getDocumentosExtConsulBean(BuscarDocumentoExtConsulBean buscarDocExt);
    DocumentoExtConsulBean getDocumentoExtConsulBean(String nuAnn, String nuEmi);
    List<DestinatarioDocumentoEmiConsulBean> getLsDestinoEmi(String pnuAnn, String pnuEmi);
    List<ReferenciaDocExtRecepBean> getLsRefDocExterno(String pnuAnn, String pnuEmi);
    String getRutaReporte(BuscarDocumentoExtConsulBean DocExt);
    List<RemitenteBean> getAllDependencias();
    public List<DocumentoExtConsulBean> getListaReporte(BuscarDocumentoExtConsulBean buscarDocumentoExtConsulBean);
    public ReporteBean getGenerarReporte(BuscarDocumentoExtConsulBean buscarDocumentoExtConsulBean, Map parametros);
}
