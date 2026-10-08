/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.dao;

import java.util.List;   
import pe.gob.onpe.mpvdoc.bean.CiudadanoBean;
import pe.gob.onpe.mpvdoc.bean.ConsultaDniBean;
import pe.gob.onpe.mpvdoc.bean.ConsultaSunatBean;
import pe.gob.onpe.mpvdoc.bean.DocumentoBean;
import pe.gob.onpe.mpvdoc.bean.MpvdocBean;
import pe.gob.onpe.mpvdoc.bean.ProveedorBean;
import pe.gob.onpe.mpvdoc.bean.ResultMpvdocBean;
import pe.gob.onpe.mpvdoc.bean.TipoDocumentoBean;
import pe.gob.onpe.mpvdoc.bean.UbigeoBean;

/**
 *
 * @author RATAYAURI
 */
public interface MpvdocDao {
    
    List<TipoDocumentoBean> listTipoDocumento() ;
    List<UbigeoBean> listDepartamento();
    List<UbigeoBean> listDistrito(String codDepProv);
    List<UbigeoBean> listProvincia(String codDep);
    CiudadanoBean getCiudadano(String pnuDoc);
    String getPassDniPide(String pctabCodTab);
    String getParametros(String pco_param);
    String insPideCiudadano(ConsultaDniBean ciudadano, String nudni);
    ProveedorBean getProveedor(String pnuRuc);
    String insPideProveedor(ConsultaSunatBean proveedor);
    ResultMpvdocBean getGrabarExpediente(MpvdocBean mpvdocBean);
    String RegisternotificarCorreo(String v_nom, String p_Para, String detalleDoc, String asunto, String cuerpo) ;
    List<DocumentoBean> getListaReporteBusquedaVoucher(DocumentoBean documento);
}
