/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstEmiBean;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstRecBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstEmitidoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstRecibidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;

/**
 *
 * @author NGilt
 */
public interface SeguiEstRecibidoDao {

    public List<DocumentoSeguiEstRecibidoBean> getListDocSeguiEstRec(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);

    public DocumentoRecepSeguiBean getDocumentoRecepAdmSegui(String snuAnn, String snuEmi, String snuDes);

    public List<ReferenciaConsulBean> getDocumentosRefRecepAdmSegui(String snuAnn, String snuEmi);

    public List<DependenciaBean> getListDestinatarioEmi(String pcoDepen, String pdeDepEmite);
    
    public String getRutaReporte(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);

    public List<DocumentoSeguiEstRecibidoBean> getListaReporteBusqueda(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);
    
    public List<DocumentoSeguiEstRecibidoBean> getDocumentosReferenciadoBusq(DocumentoSeguiEstRecibidoBean documentoSeguiEstEmitidoBean, String tiAcceso);
    
    public DocumentoSeguiEstRecibidoBean existeDocumentoReferenciado(BuscarDocumentoSeguiEstRecBean buscarDocumentoSeguiEstRecBean);
    
}
