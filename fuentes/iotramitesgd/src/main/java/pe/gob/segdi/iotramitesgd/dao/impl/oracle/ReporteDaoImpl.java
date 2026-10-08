package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import java.io.Serializable;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Logger;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pe.gob.segdi.iotramitesgd.bean.Reporte;
import pe.gob.segdi.iotramitesgd.dao.IReporteDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.ReporteConst;


/**
 *
 * Objeto : DocumentoAnexoDaoImpl.java.
 * Descripción : Clase de servicio, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 *
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 *
 *
 */
@Repository("iReporteDao")
public class ReporteDaoImpl extends JdbcTemplate implements IReporteDao,Serializable{

    private static final long serialVersionUID = 1L;
    private static Logger depurador = Logger.getLogger(ReporteDaoImpl.class.getName());

    @Autowired
    public ReporteDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public List<Reporte> getBandejaDespacho(String anio) throws Exception{
        //depurador.info("getBandejaDespacho ==>");
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_BANDEJA_DESPACHO, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                anio,anio,anio,anio,anio,anio);
        depurador.info("lista reporte despacho ==>" +lstReporte.size());
        depurador.info("lista reporte despacho ==>" +ReporteConst.GET_BANDEJA_DESPACHO);
        return lstReporte;
    }

    @Override
    public List<Reporte> getBandejaRecepcion(String anio) throws Exception{
        //depurador.info("getBandejaRecepcion ==>");
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_BANDEJA_RECEPCION, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                anio,anio,anio,anio);

        return lstReporte;
    }

    @Override
    public List<Reporte> getBandejaDespachoTotalEnviados(String anio) throws Exception{
        //depurador.info("getBandejaDespachoTotalEnviados ==>");
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_BANDEJA_DESPACHO_TOTAL_ENVIADOS, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                anio);
        //depurador.info("getBandejaDespachoTotalEnviados TAMANIO ==>"+lstReporte.size());
        return lstReporte;
    }



    @Override
    public List<Reporte> getBandejaRecepcionTotalRecepcionados(String anio) throws Exception{
        //depurador.info("getBandejaRecepcionTotalRecepcionados ==>");
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_BANDEJA_RECEPCION_TOTAL_RECEPCIONADOS, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                anio);
        //depurador.info("getBandejaRecepcionTotalRecepcionados TAMANIO ==>"+lstReporte.size());
        return lstReporte;
    }

    @Override
    public List<Reporte> getBandejaDespachoXFecha(Date dfecdesde, Date dfechasta) throws Exception{
        //depurador.info("getBandejaDespachoXFecha ==>");
        String fecdesde = "";
        String fechasta = "";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        fecdesde = format.format(dfecdesde);
        fechasta = format.format(dfechasta);
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_BANDEJA_DESPACHO_POR_FECHA, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                fecdesde,
                fechasta);
        //depurador.info("getBandejaDespachoXFecha LISTA SIZE==>"+lstReporte.size());
        return lstReporte;
    }

    @Override
    public List<Reporte> getBandejaRecepcionXFecha(Date dfecdesde, Date dfechasta) throws Exception{
        //depurador.info("getBandejaRecepcionXFecha ==>");
        String fecdesde = "";
        String fechasta = "";
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        fecdesde = format.format(dfecdesde);
        fechasta = format.format(dfechasta);
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_BANDEJA_RECEPCION_POR_FECHA, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                fecdesde,
                fechasta);
        //depurador.info("getBandejaRecepcionXFecha LISTA SIZE==>"+lstReporte.size());
        return lstReporte;
    }

    @Override
    public List<Reporte> getDocumentosPendientesPorMesBandejaDespacho(String anio) throws Exception{
        //depurador.info("getDocumentosPendientesPorMesBandejaDespacho ==>");
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_PEDIENTES_POR_MES_BANDEJA_DESPACHO, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                anio);

        return lstReporte;
    }

    @Override
    public List<Reporte> getDocumentosPendientesPorMesBandejaRecepcion(String anio) throws Exception{
        //depurador.info("getDocumentosPendientesPorMesBandejaRecepcion ==>");
        List<Reporte> lstReporte = new ArrayList<Reporte>();
        lstReporte = query(ReporteConst.GET_PEDIENTES_POR_MES_BANDEJA_RECEPCION, new BeanPropertyRowMapper<Reporte>(Reporte.class),
                anio);

        return lstReporte;
    }

}
