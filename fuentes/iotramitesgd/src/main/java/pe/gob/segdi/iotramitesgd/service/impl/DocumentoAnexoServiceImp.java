package pe.gob.segdi.iotramitesgd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.segdi.iotramitesgd.bean.IODocumentoAnexo;
import pe.gob.segdi.iotramitesgd.dao.IDocumentoAnexoDao;
import pe.gob.segdi.iotramitesgd.service.IDocumentoAnexoService;

import java.util.List;

/**
 * Objeto : DocumentoAnexoServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface documentos anexos , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Service("iDocumentoAnexoService")
public class DocumentoAnexoServiceImp implements IDocumentoAnexoService {
    @Autowired
    IDocumentoAnexoDao iDocumentoAnexoDao;

    @Override
    @Transactional(readOnly = true)
    public List<IODocumentoAnexo> getListaDocumentoAnexo(int siddocext) throws Exception {
        return iDocumentoAnexoDao.getListaDocumentoAnexo(siddocext);
    }

    @Override
    @Transactional(readOnly = true)
    public List<IODocumentoAnexo> getListaDocumentoAnexo2(String nuemi, String nuann) throws Exception {
        return iDocumentoAnexoDao.getListaDocumentoAnexo2(nuemi, nuann);
    }

    @Override
    @Transactional(readOnly = true)
    public IODocumentoAnexo getDocumentoAnexo(String nuemi, String nuann, int nu_ane) throws Exception {
        return iDocumentoAnexoDao.getDocumentoAnexo(nuemi, nuann, nu_ane);
    }

}
