package pe.gob.segdi.iotramitesgd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.segdi.iotramitesgd.bean.Remitente;
import pe.gob.segdi.iotramitesgd.dao.IRemitenteDao;
import pe.gob.segdi.iotramitesgd.service.IRemitenteService;

/**
 * Objeto : RecepcionExternaServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface remitente , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Service("iRemitenteService")
public class RemitenteServiceImp implements IRemitenteService {
    @Autowired
    IRemitenteDao iRemitenteDao;

    @Override
    public String insRemitente(Remitente remitente) throws Exception {
        return iRemitenteDao.insRemitente(remitente);
    }

    @Override
    public String getCodigoRemitente(String ruc) throws Exception {
        return iRemitenteDao.getCodigoRemitente(ruc);
    }


}
