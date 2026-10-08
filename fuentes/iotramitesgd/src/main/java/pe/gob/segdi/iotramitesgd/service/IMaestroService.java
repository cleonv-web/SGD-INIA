package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.Maestro;
import pe.gob.segdi.iotramitesgd.bean.ParametrosBean;

import java.util.List;


public interface IMaestroService {

    List<Maestro> getDependencia() throws Exception;

    List<Maestro> getTipoDocumentoMV() throws Exception;

    List<ParametrosBean> getParametros(String elemento) throws Exception;
}
