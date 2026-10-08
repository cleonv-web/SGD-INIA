package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.*;

import java.util.List;


public interface ITramiteService {

    List<DocumentoPorEstado> getNumeroDeDocumentos() throws Exception;

    String[] getResponsableOficina(String iCodOficina) throws Exception;

    void recepcionarDocumento(ConsultaDocumentoRecepcion consultaDocumentoRecepcion, Expediente expediente, DatosUsuario DatosUsuario, DatosUsuarioConfiguracion datosUsuarioConfiguracion) throws Exception;

    List<CboTramite> getTramiteOficinas() throws Exception;

    String getEstadoDocumento(String nu_emi, String nu_ann) throws Exception;

    String updEstadoDocumento(String nu_emi, String nu_ann, String doc_estado_msj) throws Exception;

    List<ParametrosBean> getEstadoMesaVirtual() throws Exception;

    //String getEstadoMesaVirtual() throws Exception;

    public int habilitarDocumento(String nu_emi, String nu_ann, String es_doc_emi, String doc_estado_msj, String ob_msj, String nu_ruc_des) throws Exception;


}
