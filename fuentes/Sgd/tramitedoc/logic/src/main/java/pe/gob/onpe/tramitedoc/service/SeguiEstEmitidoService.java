/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstEmitidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;

/**
 *
 * @author NGilt
 */
public interface SeguiEstEmitidoService {

    public DocumentoEmiSeguiBean getDocumentoEmiSeguiBean(String snuAnn, String snuEmi, String snuDes);

    public List<ReferenciaConsulBean> getDocumentosRefEmiAdmSegui(String snuAnn, String snuEmi);

    public String getRutaReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);

    public List<DocumentoSeguiEstEmitidoBean> getListDocSeguiEstEmi(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);

    public HashMap getBuscaDependenciaEmite(String coDep, String pdeDepEmite);

    public HashMap getDocumentosEnReferencia(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);
    public ReporteBean getGenerarReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean,Map parametros);
    public List<DocumentoSeguiEstEmitidoBean> getListaReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);
}
