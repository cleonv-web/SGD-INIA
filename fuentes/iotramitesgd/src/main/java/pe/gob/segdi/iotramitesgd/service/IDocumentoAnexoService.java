package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.IODocumentoAnexo;

import java.util.List;


public interface IDocumentoAnexoService {

    List<IODocumentoAnexo> getListaDocumentoAnexo(int siddocext) throws Exception;

    List<IODocumentoAnexo> getListaDocumentoAnexo2(String nuemi, String nuann) throws Exception;

    IODocumentoAnexo getDocumentoAnexo(String nuemi, String nuann, int nu_ane) throws Exception;


}
