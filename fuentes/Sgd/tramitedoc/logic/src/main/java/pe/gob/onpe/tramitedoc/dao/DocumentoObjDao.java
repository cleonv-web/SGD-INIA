/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoNotificacionBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBlock;

/**
 *
 * @author WCutipa
 */
public interface DocumentoObjDao {
    
    DocumentoDatoBean getDatosDoc(String pnuAnn, String pnuEmi);
    DocumentoObjBean getNombreArchivo(String pnuAnn, String pnuEmi, String ptiCap);
    DocumentoObjBean getNombreArchivoAnexo(String pnuAnn, String pnuEmi, String pnuAnexo);
    String CopiarAnexo(String pnuAnn, String pnuEmi, String pnuAnexo,String pNuAnnDocProyecto, String pnuEmiDocProyecto);
    DocumentoObjBean getNombreArchivoAnexoArchivado(String pnuAnn, String pnuEmi, String pnuDes);
    DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap);
    DocumentoObjBean leerDocumentoAnexo(String pnuAnn, String pnuEmi, String pnuAne);
    DocumentoObjBean leerActaNotificacion(String pnuAnn, String pnuEmi, String pnuDes, String pnuActa);
    List<DocumentoObjBean> leerDocumentoAnexo(String pnuAnn, String pnuEmi);
    String getInFirmaDoc(String pcoDep,String pcoTipoDoc);
    String insRemitosBlock(DocumentoObjBlock docBlock);
    String updRemitosBlock(DocumentoObjBlock docBlock);
    DocumentoObjBlock getDatosDocBlock(String pnuAnn,String pnuEmi);
    DocumentoDatoBean CargarCabeceraReporte(String pnuAnn, String pnuEmi);
    List<DocumentoDatoBean> CargarSubReporte1(String pnuAnn, String pnuEmi);
    List<DocumentoDatoBean> CargarSubReporte2(String pnuAnn, String pnuEmi);
    public List<DocumentoNotificacionBean> CargarReporteNotificacion(String pnuAnn, String pnuEmi, String tiOpe);
    DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap, String pAbreWord);
}
