package pe.gob.segdi.iotramitesgd.service.impl;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoRecepcion;
import pe.gob.segdi.iotramitesgd.dao.IRecepcionExternaDao;
import pe.gob.segdi.iotramitesgd.service.IRecepcionExternaService;

import java.util.List;

/**
 * Objeto : RecepcionExternaServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface recepcion externa , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Service("iRecepcionExternaService")
public class RecepcionExternaServiceImp implements IRecepcionExternaService {
    //private static Logger depurador = Logger.getLogger(RecepcionExternaServiceImp.class.getName());
    @Autowired
    IRecepcionExternaDao iRecepcionExternaDao;

    @Override
    public List<ConsultaDocumentoRecepcion> bsqRecepcionExterna(String vrucentemi, String cflgest, int sidrecext, String dfecdesde, String dfechasta) throws Exception {
        return iRecepcionExternaDao.bsqRecepcionExterna(vrucentemi, cflgest, sidrecext, dfecdesde, dfechasta);
    }

    @Override
    public int updRecepcionExternaEnvioCargo(int sidrecext) throws Exception {
        return iRecepcionExternaDao.updRecepcionExternaEnvioCargo(sidrecext);
    }

    @Override
    public byte[] getDocumetoPrincipal(int sidrecext) throws Exception {
        return iRecepcionExternaDao.getDocumetoPrincipal(sidrecext);
    }

    @Override
    public ConsultaDocumentoRecepcion getRecepcionExterna(int sidrecext) throws Exception {
        return iRecepcionExternaDao.getRecepcionExterna(sidrecext);
    }

    public void fileUploadDocumentoPrincipal(int sidemiext, byte[] cargo) throws Exception {
        iRecepcionExternaDao.fileUploadDocumentoPrincipal(sidemiext, cargo);
    }

}
