package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.*;

import java.util.List;


public interface ITramiteDao {

    List<DocumentoPorEstado> getNumeroDeDocumentos() throws Exception;

    String[] getResponsableOficina(String iCodOficina) throws Exception;

    String getNumeroExpediente() throws Exception;

    String getNumeroSecuenciaExpediente() throws Exception;

    String getNumeroCorrelativoExpediente() throws Exception;

    void insExpediente(Expediente expediente) throws Exception;

    String getCodigoEmpleadoDependencia(String codependencia) throws Exception;

    String insRemito(Remito remito) throws Exception;

    List<CboTramite> getTramiteOficinas() throws Exception;

    String getCodigoTipoDocumento(String cCodTipDoc) throws Exception;

    void insDocumentoPrincipal(Archivo archivo) throws Exception;

    void insDocumentoAnexo(Anexo anexo) throws Exception;

    void insDestino(Destino destino) throws Exception;

    void insEstadoMovimiento(EstadoMovimiento estadoMovimiento) throws Exception;

    String getEstadoDocumento(String nu_emi, String nu_ann) throws Exception;

    void updEstadoDocumento(String nu_emi, String nu_ann, String doc_estado_msj) throws Exception;

    List<ParametrosBean> getEstadoMesaVirtual() throws Exception;

    void updEstadoRemito(String nu_emi, String nu_ann, String es_doc_emi) throws Exception;

    void updTipoEnvio(String nu_emi, String nu_ann) throws Exception;

    int habilitarDocumento(String nu_emi, String nu_ann, String es_doc_emi, String doc_estado_msj) throws Exception;

    void observarDocumento(String ob_msj, String nu_emi, String nu_ann, String nu_ruc_des) throws Exception;

}
