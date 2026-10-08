package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBeansContenedor;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;

public interface AnexoDocumentoService {
    List<ReferenciaBean> getDocumentosReferencia(String pnuAnn,String pnuEmi);
    String getReferenciaJson(String pnuAnn, String pnuEmi,int nivel);
    String getReferenciaRoot(String pnuAnn, String pnuEmi,String pnuDes,int nivel);
    List<ReferenciaBean> getReferenciaRootList(String pnuAnn, String pnuEmi,String pnuDes,int nivel);
    
    public DocumentoAnexoBean getReferenciaInicial(String pnuAnn, String pnuEmi, String pnuDes);
    List<ReferenciaBean> getDocumentosSeguimiento(String pnuAnn,String pnuEmi,String pnuDes);
    String getSeguimientoJson(String pnuAnn, String pnuEmi,String pnuDes,int nivel);
    String getSeguimientoRoot(String pnuAnn, String pnuEmi,String pnuDes,int nivel);

    String updArchivoAnexo(String coUsu,String pnuAnn,String pnuEmi,String pnuAne,DocumentoFileBean pfileAnexo);
    String updAnexoDetalle(DocumentoAnexoBeansContenedor docsAnexos,String coUsu, String rowCount) throws Exception;
    String insArchivoAnexo(String coUsu,String pnuAnn,String pnuEmi,DocumentoFileBean pfileAnexo);

    public String getNombreArchivo(String pNuAnn, String pNuEmi, String pNuAne);
    String getNuDesDocSeguimiento(String nuAnn,String nuEmi,String nuDes);
    String getVerificaPdfDoc(String pnuAnn, String pnuEmi, String pnuAne);
    String updArchivoAnexoFirmado(DocumentoObjBean docObjBean);
    String getCanAnexosReqFirma(String nuAnn, String nuEmi);
    String updRemitosResumenInFirmaAnexos(String reqFirma, String nuAnn, String nuEmi);
}
