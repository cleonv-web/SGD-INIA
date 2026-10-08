package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoRecepcion;
import pe.gob.segdi.iotramitesgd.bean.IORecepcionExterna;

import java.util.List;


public interface IRecepcionExternaDao {

    List<ConsultaDocumentoRecepcion> bsqRecepcionExterna(String vrucentemi, String cflgest, int sidrecext, String dfecdesde, String dfechasta) throws Exception;

    int updCargoRecepcionExterna(IORecepcionExterna recepcionExterna) throws Exception;

    int updRecepcionExternaEnvioCargo(int sidrecext) throws Exception;

    byte[] getDocumetoPrincipal(int sidrecext) throws Exception;

    ConsultaDocumentoRecepcion getRecepcionExterna(int sidrecext) throws Exception;

    void fileUploadDocumentoPrincipal(int sidemiext, byte[] cargo) throws Exception;
}
