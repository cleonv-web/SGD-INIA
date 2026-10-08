/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocExtRecSeguiEstBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaDocExtRecepBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;

/**
 *
 * @author ECueva
 */
public interface SeguiEstRecDocExtService {
    List<DocExtRecSeguiEstBean> getLsDocExtRecSegui(DocExtRecSeguiEstBean docBuscar);
    String getObjJson(String[] params);
    DocExtRecSeguiEstBean getDocumentoExtSeguiBean(String nuAnn, String nuEmi, String nuDes);
    List<DestinatarioDocumentoEmiConsulBean> getLsDestinoEmi(String pnuAnn, String pnuEmi);
    List<ReferenciaDocExtRecepBean> getLsRefDocExterno(String pnuAnn, String pnuEmi);    
    String getRutaReporte(DocExtRecSeguiEstBean docBuscar);
    ReporteBean getGenerarReporte(DocExtRecSeguiEstBean docBuscar,Map parametros);
    List<DocExtRecSeguiEstBean> getListaReporte(DocExtRecSeguiEstBean docBuscar);
    List<DocExtRecSeguiEstBean> getListaReporteResumen(DocExtRecSeguiEstBean docBuscar);
    ReporteBean getGenerarReporteReumen(DocExtRecSeguiEstBean docBuscar,Map parametros);
}
