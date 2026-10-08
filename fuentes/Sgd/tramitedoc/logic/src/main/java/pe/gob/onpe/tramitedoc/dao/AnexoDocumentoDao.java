/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.io.InputStream;
import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;

/**
 *
 * @author wcutipa
 */
public interface AnexoDocumentoDao {
        List<ReferenciaBean> getDocumentosReferencia(String pnuAnn,String pnuEmi);
        List<ReferenciaBean> getDocumentoEmi(String pnuAnn,String pnuEmi);
        List<ReferenciaBean> getDocumentosSeguimiento(String pnuAnn,String pnuEmi,String pnuDes);
        List<ReferenciaBean> getDocumentoEmiSeg(String pnuAnn,String pnuEmi,String pnuDes);
        String getUltimoAnexo(String pnuAnn,String pnuEmi);
        String updArchivoAnexo(DocumentoAnexoBean docAnexo, InputStream archivoAnexo ,int size);
        String updAnexoDetalle(DocumentoAnexoBean docAnexo);
        String delArchivoAnexo(DocumentoAnexoBean docAnexo);
        String delArchivoAnexoDest(String pNuAnn,String pNuEmi,String pNuDes);
        String insArchivoAnexo(DocumentoAnexoBean docAnexo, InputStream archivoAnexo ,int size);
        String updExisteAnexo(String nuAnn, String nuEmi);
        String getNombreArchivo(String pNuAnn, String pNuEmi, String pNuAne);
        DocumentoObjBean getPropiedadesArchivo(String pnuAnn, String pnuEmi, String pnuAne);
        String updRemitosResumenInFirmaAnexos(String pFirmaAnexo, String pnuAnn, String pnuEmi);
        String updArchivoAnexoFirmado(final DocumentoObjBean docObjBean);
        String getCanAnexosReqFirma(String nuAnn, String nuEmi);
        String getCanAnexosDuplicadosNombres(String nuAnn, String nuEmi,String nombre);
        String getCanAnexosDuplicadosNombres(String nuAnn, String nuEmi,String anexo,String nombre);
}
