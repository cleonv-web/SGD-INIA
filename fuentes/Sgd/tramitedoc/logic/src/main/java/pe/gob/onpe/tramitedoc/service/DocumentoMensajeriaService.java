/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;
import java.util.List; 
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepMensajeriaBean; 
import pe.gob.onpe.tramitedoc.bean.DestiDocumentoEnvMensajeriaBean;
import pe.gob.onpe.tramitedoc.bean.DestinoResBen;
import pe.gob.onpe.tramitedoc.bean.DocumentoRecepMensajeriaBean; 
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.TipoElementoMensajeriaBean;
/**
 *
 * @author WCONDORI
 */
public interface DocumentoMensajeriaService {
    List<DocumentoRecepMensajeriaBean> getDocumentoRecepMensajeria(BuscarDocumentoRecepMensajeriaBean buscarDocumentoRecepMensajeriaBean);
    List<DestiDocumentoEnvMensajeriaBean> getLstDestinoEnvMensajeria(String pnuAnnpnuEmi);
    List<TipoElementoMensajeriaBean> getlistTipoElementoMensajeria(String tipo);
    List<TipoElementoMensajeriaBean> getListResponsableMensajeria(String tipo,String Ambito,String tipoEnvio);
    String insMensajeriaDocumento(DocumentoRecepMensajeriaBean documentoMensajeria);
    String updMensajeriaDocumentoRecibir(String codigos);
    String updMensajeriaDocumentoDevolver(String codigos);
    String selectCalcularFechaPlazo(DocumentoRecepMensajeriaBean documentoMensajeria);
    List<DocumentoRecepMensajeriaBean> getLstDetalleMensajeria(DestinoResBen oDestinoResBen);
    ReporteBean getImprimirReporte(BuscarDocumentoRecepMensajeriaBean buscarDocumentoRecepConsulBean, Map parametros);
    List<DocumentoRecepMensajeriaBean> getLstDetalleMesaVirtual(DestinoResBen oDestinoResBen);
    
}
