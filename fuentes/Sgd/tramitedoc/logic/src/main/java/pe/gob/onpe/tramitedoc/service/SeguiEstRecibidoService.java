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
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstRecBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstRecibidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;

/**
 *
 * @author NGilt
 */
public interface SeguiEstRecibidoService {

    public List<DocumentoSeguiEstRecibidoBean> getListDocSeguiEstRec(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);

    public DocumentoRecepSeguiBean getDocumentoRecepAdmSegui(String snuAnn, String snuEmi, String snuDes);

    public List<ReferenciaConsulBean> getDocumentosRefRecepAdmSegui(String snuAnn, String snuEmi);

    public String getRutaReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);

    public HashMap getBuscaDependenciaEmite(String coDep, String pdeDepEmite);
    
    public HashMap getDocumentosEnReferencia(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);
    
    ReporteBean getGenerarReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean,Map parametros);
    
    List<DocumentoSeguiEstRecibidoBean> getListaReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);
}
