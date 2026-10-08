package pe.gob.segdi.iotramitesgd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.segdi.iotramitesgd.bean.IODocumentoPrincipal;
import pe.gob.segdi.iotramitesgd.dao.IDocumentoPrincipalDao;
import pe.gob.segdi.iotramitesgd.service.IDocumentoPrincipalService;

/**
 * Objeto : DocumentoPrincipalServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface documento principal , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Service("iDocumentoPrincipalervice")
public class DocumentoPrincipalServiceImp implements IDocumentoPrincipalService {
    @Autowired
    IDocumentoPrincipalDao iDocumentoPrincipalDao;


    @Override
    @Transactional(readOnly = true)
    public IODocumentoPrincipal getDocumentoPrincipal(String vcuo) throws Exception {
        return iDocumentoPrincipalDao.getDocumentoPrincipal(vcuo);
    }

}
