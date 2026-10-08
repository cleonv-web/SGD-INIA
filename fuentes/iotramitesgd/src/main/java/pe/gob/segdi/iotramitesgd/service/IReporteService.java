package pe.gob.segdi.iotramitesgd.service;

import pe.gob.segdi.iotramitesgd.bean.Reporte;

import java.util.Date;
import java.util.List;


public interface IReporteService {

    List<Reporte> getBandejaDespacho(String anio) throws Exception;

    List<Reporte> getBandejaDespachoTotalEnviados(String anio) throws Exception;

    List<Reporte> getBandejaRecepcion(String anio) throws Exception;

    List<Reporte> getBandejaRecepcionTotalRecepcionados(String anio) throws Exception;

    List<Reporte> getBandejaDespachoXFecha(Date dfecdesde, Date dfechasta) throws Exception;

    List<Reporte> getBandejaRecepcionXFecha(Date dfecdesde, Date dfechasta) throws Exception;

    List<Reporte> getDocumentosPendientesPorMesBandejaDespacho(String anio) throws Exception;

    List<Reporte> getDocumentosPendientesPorMesBandejaRecepcion(String anio) throws Exception;
}
