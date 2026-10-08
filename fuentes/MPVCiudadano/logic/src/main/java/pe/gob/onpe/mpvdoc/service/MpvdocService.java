/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.service;

import java.util.List; 
import pe.gob.onpe.mpvdoc.bean.DocumentoBean;
import pe.gob.onpe.mpvdoc.bean.TipoDocBean;
import pe.gob.onpe.mpvdoc.bean.MpvdocBean;
import pe.gob.onpe.mpvdoc.bean.ResultMpvdocBean;
import pe.gob.onpe.mpvdoc.bean.TipoDocumentoBean;
import pe.gob.onpe.mpvdoc.bean.UbigeoBean;

/**
 *
 * @author RATAYAURI
 */
public interface MpvdocService { 
    List<TipoDocumentoBean> getTiposDocs();
    List<UbigeoBean> getListDepartamento() ;
    List<UbigeoBean> getListProvincia(String codDep);
    List<UbigeoBean> getListDistrito(String codDepProv);
    String getCiudadano(String pnuDoc);
    String getProveedor(String pnuRuc);
    ResultMpvdocBean getGrabarExpediente(MpvdocBean mpvdocBean);
    String RegisternotificarCorreo(String v_nom, String p_Para, String detalleDoc, String asunto, String cuerpo) ;
    List<DocumentoBean> getListaReporteBusquedaVoucher(DocumentoBean documento);
}
