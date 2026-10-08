package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.IODocumentoAnexo;

import java.util.List;


public interface IDocumentoAnexoDao {

    int insDocumentoAnexo(IODocumentoAnexo documentoAnexo) throws Exception;

    List<IODocumentoAnexo> getListaDocumentoAnexo(int siddocext) throws Exception;

    List<IODocumentoAnexo> getListaDocumentoAnexo2(String nuemi, String nuann) throws Exception;

    IODocumentoAnexo getDocumentoAnexo(String nuemi, String nuann, int nu_ane) throws Exception;


}
