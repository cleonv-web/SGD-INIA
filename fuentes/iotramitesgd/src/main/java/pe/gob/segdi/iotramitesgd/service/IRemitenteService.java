package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.Remitente;


public interface IRemitenteService {

    String insRemitente(Remitente remitente) throws Exception;

    String getCodigoRemitente(String ruc) throws Exception;


}
