/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiConsulBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;

/**
 *
 * @author WCutipa
 */
public interface DocumentoObjService {
    DocumentoDatoBean getDatosDoc(String pnuAnn, String pnuEmi);
    DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap);
    DocumentoObjBean leerDocumentoAnexo(String pnuAnn, String pnuEmi, String pnuAne);
    DocumentoObjBean leerActaNotificacion(String pnuAnn, String pnuEmi, String pnuDes, String pnuActa);
    List<DocumentoObjBean> leerDocumentoAnexo(String pnuAnn, String pnuEmi);
    DocumentoObjBean getNombreArchivo(String pnuAnn, String pnuEmi, String ptiCap);
    DocumentoVerBean getNombreDoc(String pnuAnn, String pnuEmi,String ptiOpe,Usuario usuario);
    DocumentoVerBean getNombreDocInicial(String pnuAnn, String pnuEmi);
    DocumentoVerBean getNombreGeneraDocx(String pnuAnn, String pnuEmi,String ptiOpe);

    DocumentoObjBean getNombreArchivoAnexo(String pnuAnn, String pnuEmi, String pnuAnexo);
    String CopiarAnexo(String pnuAnn, String pnuEmi, String pnuAnexo,String pNuAnnDocProyecto, String pnuEmiDocProyecto);
    DocumentoVerBean getNombreDocAnexo(String pnuAnn, String pnuEmi,String pnuAnexo);
    DocumentoVerBean getNombreDocAnexoArchivado(String pnuAnn, String pnuEmi,String pnuDes);
    DocumentoVerBean getNombreCargaDoc(String pnuAnn, String pnuEmi,String ptiOpe);
    DocumentoVerBean getNombreFirmaDoc(String pnuAnn, String pnuEmi,String ptiOpe,UsuarioConfigBean usuarioConfig, String rutaBase);
    DocumentoVerBean getNombreCargaFirmaDoc(String pnuAnn, String pnuEmi,String ptiOpe);
    String getFormatoDoc(String pnuAnn, String pnuEmi, String ptiCap);
    DocumentoVerBean getNombreDocEmi(String pnuAnn, String pnuEmi,String ptiOpe, Usuario usuario, String rutaBase);
    DocumentoVerBean getNombreVoBoDoc(String pnuAnn, String pnuEmi,String ptiOpe,UsuarioConfigBean usuarioConfig, String rutaBase);
    DocumentoVerBean getNombreCargaVoBoDoc(String pnuAnn, String pnuEmi,String ptiOpe);
    DocumentoVerBean getNombreFirmaDocAnexo(String pnuAnn, String pnuEmi,String pnuAnexo,String ptiOpe,UsuarioConfigBean usuarioConfig);
    DocumentoVerBean getNombreCargaFirmaDocAnexo(String pnuAnn, String pnuEmi, String pnuAne,String ptiOpe);
    String cargaDocAnexo(String pnuAnn,String pnuEmi,String pnuAne,String pnuSecFirma,String coUsuario, String prutaDoc)throws Exception;
    DocumentoVerBean getNombreDocEmiReporte(String pnuAnn, String pnuEmi,String ptiOpe, Usuario usuario, String rutaReporteBase);
    DocumentoVerBean getNombreFirmaDocReporte(String pnuAnn, String pnuEmi,String ptiOpe,UsuarioConfigBean usuarioConfig, String rutaBase);
    
    DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap, String pAbreWord);
    ReporteBean getGenerarReporteNotificacion(BuscarDocumentoEmiConsulBean buscarDocumentoEmiConsulBean,Map parametros);
}
