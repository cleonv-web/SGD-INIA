package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoEmision;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExterna;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExternaBean;

import java.util.List;


public interface IEmisionExternaDao {

    int insEmisionExterna(IOEmisionExterna emisionExterna) throws Exception;

    List<ConsultaDocumentoEmision> bsqEmisionExterna(String vrucentdst, String cflgest, String codependencia, String dfecdesde, String dfechasta) throws Exception;

    List<ConsultaDocumentoEmision> getEmisionExternaPendientes(String vnumregstd, String vanioregstd) throws Exception;

    ConsultaDocumentoEmision getEmisionExterna(int sidemiext) throws Exception;

    int insCuoEmisionExterna(String vcuo, int sidemiext) throws Exception;

    void updEstadoEmisionExterna(int sidemiext, String cflgest) throws Exception;

    int updCargoEmisionExterna(IOEmisionExterna emisionExterna) throws Exception;

    int updEstadoEnvio(int sidemiext, String cflgenv) throws Exception;

    String obtEstadoEnvio(int sidemiext) throws Exception;

    String obtCuo(int sidemiext) throws Exception;

    void anularRecepcion(int sidemiext, String vdesanu) throws Exception;

    ///////////////////////////////////////////////
    List<IOEmisionExternaBean> getDocumentosEnviados(String vrucentrec) throws Exception;

    int actualizarCargoDespacho(IOEmisionExternaBean ioEmisionExternaBean) throws Exception;

    int actualizarEstadoDespacho(String cflgest) throws Exception;

    String getNumeroDocumento(String vcuo) throws Exception;

    List<IOEmisionExternaBean> getEntidadEnviados() throws Exception;

    void actualizarCargo(int sidemiext, byte[] cargo) throws Exception;

}
