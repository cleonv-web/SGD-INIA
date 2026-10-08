/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoSeguiEstEmiBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiSeguiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoSeguiEstEmitidoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaConsulBean;

/**
 *
 * @author NGilt
 */
public interface SeguiEstEmitidoDao {

    public List<DocumentoSeguiEstEmitidoBean> getListDocSeguiEstRec(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);

    public List<DependenciaBean> getListDestinatarioEmi(String coDep, String pdeDepEmite);

    public DocumentoSeguiEstEmitidoBean existeDocumentoReferenciado(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);

    public List<DocumentoSeguiEstEmitidoBean> getDocumentosReferenciadoBusq(DocumentoSeguiEstEmitidoBean documentoSeguiEstEmitidoBean, String tiAcceso);

    public DocumentoEmiSeguiBean getDocumentoEmiSeguiBean(String pnuAnn,String pnuEmi,String pnuDes);

    public List<ReferenciaConsulBean> getDocumentosRefRecepAdm(String snuAnn, String snuEmi);
    
    public String getRutaReporte(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);
    
    public List<DocumentoSeguiEstEmitidoBean> getListaReporteBusqueda(BuscarDocumentoSeguiEstEmiBean buscarDocumentoSeguiEstEmiBean);
}
