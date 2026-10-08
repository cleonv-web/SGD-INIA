package pe.gob.segdi.iotramitesgd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.segdi.iotramitesgd.bean.Maestro;
import pe.gob.segdi.iotramitesgd.bean.ParametrosBean;
import pe.gob.segdi.iotramitesgd.dao.IMaestroDao;
import pe.gob.segdi.iotramitesgd.service.IMaestroService;

import java.util.List;

/**
 * Objeto : MaestoServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface maestro , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Service("iMaestroService")
public class MaestoServiceImp implements IMaestroService {
    @Autowired
    IMaestroDao iMaestroDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<Maestro> getDependencia() throws Exception {
        return iMaestroDao.getDependencia();
    }

    @Override
    public List<Maestro> getTipoDocumentoMV() throws Exception {
        return iMaestroDao.getTipoDocumentoMV();
    }

    @Override
    public List<ParametrosBean> getParametros(String elemento) throws Exception {
        return iMaestroDao.getParametros(elemento);
    }


}
