/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoCargaMsjBean;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepConsulBean;
import pe.gob.onpe.tramitedoc.bean.DescargaMensajeBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.ElementoMensajeroBean;
import pe.gob.onpe.tramitedoc.bean.MensajesConsulBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;


/**
 *
 * @author oti3
 */
public interface DocumentoMensajesService {
    List<ElementoMensajeroBean> getListResponsableMensajeria(String tipo,String Ambito);
    List<SiElementoBean> getListTipoElementoMensajeria(String pctabCodtab);
    List<MensajesConsulBean> getBuscaDocumentosCarga(BuscarDocumentoCargaMsjBean buscarDocumentoCargaMsjBean); 
    MensajesConsulBean getBuscaDocumentosMsj(String nu_ann,String nu_emi,String nu_des,String nu_msj);
    String updMensajeriaDocumento(DescargaMensajeBean descargaMensaje);
    String delMensajeriaDocumento(DescargaMensajeBean descargaMensaje);
    String deleteMsj(DescargaMensajeBean descargaMensaje);
    String insArchivoAnexoDes(String coUsu,String pnuAnn,String pnuEmi,String pnuDes,DocumentoFileBean pfileAnexo);
    String insArchivoActa(String coUsu, String pnuAnn, String pnuEmi, String pnuDes, String pnu_Acta_Vis, DocumentoFileBean pfileAnexo);
    
    ReporteBean getGenerarReporte(BuscarDocumentoCargaMsjBean buscarDocumentoRecepConsulBean,Map parametros);
    DocumentoVerBean getNombreDocMsj(String pnuAnn, String pnuEmi,String pnuAnexo);    
    DocumentoVerBean getNombreActaMsj(String pnuAnn, String pnuEmi,String pNuDes,String pnuActa,String pnombreArchivo);
    DocumentoObjBean getNombreArchivoMsj(String pnuAnn, String pnuEmi, String pnuAnexo);
    String revMensajeriaDocumento(DescargaMensajeBean descargaMensaje);
    String selectCalFechaPlazo(DescargaMensajeBean descargaMensaje);
    
}
