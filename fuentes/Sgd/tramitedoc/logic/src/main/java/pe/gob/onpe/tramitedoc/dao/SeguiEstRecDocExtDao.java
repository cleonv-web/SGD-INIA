/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

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
public interface SeguiEstRecDocExtDao {
    List<DocExtRecSeguiEstBean> getLsDocExtRecSegui(DocExtRecSeguiEstBean docBuscar);
    DocExtRecSeguiEstBean getDocumentoExtSeguiBean(String nuAnn, String nuEmi, String nuDes);
    List<ReferenciaDocExtRecepBean> getLsRefDocExterno(String pnuAnn, String pnuEmi);
    List<DestinatarioDocumentoEmiConsulBean> getLsDestinoEmi(String pnuAnn, String pnuEmi);
    String getRutaReporte(DocExtRecSeguiEstBean docBuscar);    
    List<DocExtRecSeguiEstBean> getListaReporteBusqueda(DocExtRecSeguiEstBean docExtRecSeguiEstBean); 
    List<DocExtRecSeguiEstBean> getListaReporteResumen(DocExtRecSeguiEstBean docExtRecSeguiEstBean); 
    
}
