package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoEmision;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExterna;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExternaBean;

import java.util.List;


public interface IEmisionExternaService {


    List<ConsultaDocumentoEmision> bsqEmisionExterna(String vrucentdst, String cflgest, String codependencia, String dfecdesde, String dfechasta) throws Exception;

    List<ConsultaDocumentoEmision> getEmisionExternaPendientes(String vnumregstd, String vanioregstd) throws Exception;

    ConsultaDocumentoEmision getEmisionExterna(int sidemiext) throws Exception;

    int insCuoEmisionExterna(String vcuo, int sidemiext) throws Exception;

    void updEstadoEmisionExterna(int sidemiext, String cflgest, String nu_emi, String nu_ann, String doc_estado_msj) throws Exception;

    int updCargoEmisionExterna(IOEmisionExterna emisionExterna) throws Exception;

    void updEstadoEnvio(int sidemiext, String cflgenv) throws Exception;

    String obtEstadoEnvio(int sidemiext) throws Exception;

    String obtCuo(int sidemiext) throws Exception;

    String anularRecepcion(String vnumregstd, String vanioregstd, String docestmsj, int sidemiext, String vdesanu) throws Exception;

    ///////////////////////////////////////////////
    List<IOEmisionExternaBean> getDocumentosEnviados(String vrucentrec) throws Exception;

    int actualizarCargoDespacho(IOEmisionExternaBean ioEmisionExternaBean) throws Exception;

    int actualizarEstadoDespacho(String cflgest) throws Exception;

    String getNumeroDocumento(String vcuo) throws Exception;

    List<IOEmisionExternaBean> getEntidadEnviados() throws Exception;

    void actualizarCargo(int sidemiext, byte[] cargo) throws Exception;
}
