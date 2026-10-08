package pe.gob.segdi.iotramitesgd.dao;

import pe.gob.segdi.iotramitesgd.bean.Remitente;


public interface IRemitenteDao {

    String insRemitente(Remitente remitente) throws Exception;

    String getCodigoRemitente(String ruc) throws Exception;

}
