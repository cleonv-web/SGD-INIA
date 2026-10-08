package pe.gob.segdi.iotramitesgd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.segdi.iotramitesgd.bean.Reporte;
import pe.gob.segdi.iotramitesgd.dao.IReporteDao;
import pe.gob.segdi.iotramitesgd.service.IReporteService;

import java.util.Date;
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
@Service("iReporteService")
public class ReporteServiceImp implements IReporteService {
    @Autowired
    IReporteDao iReporteDao;


    @Override
    @Transactional(readOnly = true)
    public List<Reporte> getBandejaDespacho(String anio) throws Exception {
        return iReporteDao.getBandejaDespacho(anio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> getBandejaDespachoTotalEnviados(String anio) throws Exception {
        return iReporteDao.getBandejaDespachoTotalEnviados(anio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> getBandejaRecepcion(String anio) throws Exception {
        return iReporteDao.getBandejaRecepcion(anio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> getBandejaRecepcionTotalRecepcionados(String anio) throws Exception {
        return iReporteDao.getBandejaRecepcionTotalRecepcionados(anio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> getBandejaDespachoXFecha(Date dfecdesde, Date dfechasta) throws Exception {
        return iReporteDao.getBandejaDespachoXFecha(dfecdesde, dfechasta);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<Reporte> getBandejaRecepcionXFecha(Date dfecdesde, Date dfechasta) throws Exception {
        return iReporteDao.getBandejaRecepcionXFecha(dfecdesde, dfechasta);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> getDocumentosPendientesPorMesBandejaDespacho(String anio) throws Exception {
        return iReporteDao.getDocumentosPendientesPorMesBandejaDespacho(anio);
    }

    @Override
    @Transactional(readOnly = true)
    public List<Reporte> getDocumentosPendientesPorMesBandejaRecepcion(String anio) throws Exception {
        return iReporteDao.getDocumentosPendientesPorMesBandejaRecepcion(anio);
    }

}
