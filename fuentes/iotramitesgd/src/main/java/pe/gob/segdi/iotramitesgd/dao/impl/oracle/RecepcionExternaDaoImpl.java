package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoRecepcion;
import pe.gob.segdi.iotramitesgd.bean.IODocumentoPrincipal;
import pe.gob.segdi.iotramitesgd.bean.IORecepcionExterna;
import pe.gob.segdi.iotramitesgd.dao.IRecepcionExternaDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.RecepcionExternaConst;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Objeto : RecepcionExternaDaoImpl.java.
 * Descripción : Clase de servicio, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Repository("iRecepcionExternaDao")
public class RecepcionExternaDaoImpl extends JdbcTemplate implements IRecepcionExternaDao, Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger depurador = Logger.getLogger(RecepcionExternaDaoImpl.class.getName());

    @Autowired
    public RecepcionExternaDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public List<ConsultaDocumentoRecepcion> bsqRecepcionExterna(String vrucentemi, String cflgest, int sidrecext, String dfecdesde, String dfechasta) throws Exception {
        //depurador.info("bsqRecepcionExterna ==>"+cflgest);
        List<ConsultaDocumentoRecepcion> lstRecExt = new ArrayList<ConsultaDocumentoRecepcion>();

        try {
            String query = "";
            StringBuilder sql = null;
            if (!dfecdesde.equals("")) {
                query = RecepcionExternaConst.BSQ_RECEPCION_EXTERNA;
                sql = new StringBuilder();
                sql.append(query);

                sql.append(" AND re.cflgest ='" + cflgest + "'");
                sql.append(" and re.dfecreg BETWEEN  to_date(?,'dd/mm/YY') AND to_date(?,'dd/mm/YY')").
                        append(" order by re.sidrecext DESC");
                lstRecExt = query(sql.toString(),
                        new BeanPropertyRowMapper<ConsultaDocumentoRecepcion>(ConsultaDocumentoRecepcion.class),
                        Utilitarios.ObtenerDatosProperties("Parametros", "P_CFLGANU"),
                        dfecdesde,
                        dfechasta);
            } else {
                query = RecepcionExternaConst.BSQ_RECEPCION_EXTERNA;
                sql = new StringBuilder();
                sql.append(query);

                sql.append(" AND re.cflgest ='" + cflgest + "'");
                sql.append(" order by re.sidrecext DESC");
                lstRecExt = query(sql.toString(),
                        new BeanPropertyRowMapper<ConsultaDocumentoRecepcion>(ConsultaDocumentoRecepcion.class),
                        Utilitarios.ObtenerDatosProperties("Parametros", "P_CFLGANU")
                );
            }

        } catch (Exception e) {
            depurador.error(null, e);
        }
        return lstRecExt;
    }

    @Override
    public int updCargoRecepcionExterna(IORecepcionExterna recepcionExterna) throws Exception {
        //depurador.info("updCargoRecepcionExterna ==>"+recepcionExterna.getSidrecext());
        //depurador.info("getVnomuniorgstd ==>"+recepcionExterna.getVuniorgstd());
        //depurador.info("getCflgest ==>"+recepcionExterna.getCflgest());
        Integer sidemiext = 0;
        sidemiext = update(RecepcionExternaConst.UPD_RECEPCION_EXTERNA,
                recepcionExterna.getVnumregstd(),
                recepcionExterna.getVuniorgstd(),
                recepcionExterna.getCcoduniorgstd(),
                recepcionExterna.getVusuregstd(),
                recepcionExterna.getBcarstd(),
                recepcionExterna.getVobs().trim(),
                recepcionExterna.getCflgest(),
                recepcionExterna.getSidrecext()
        );
        return sidemiext;
    }

    @Override
    public int updRecepcionExternaEnvioCargo(int sidrecext) throws Exception {
        //depurador.info("updRecepcionExternaEnvioCargo ==>");
        Integer sidemiext = 0;
        sidemiext = update(RecepcionExternaConst.UPD_ESTADO_RECEPCION_EXTERNA,
                sidrecext
        );
        return sidemiext;
    }

    @Override
    public byte[] getDocumetoPrincipal(int sidrecext) throws Exception {
        IODocumentoPrincipal ioDocumentoPrincipal = new IODocumentoPrincipal();
        ioDocumentoPrincipal = (IODocumentoPrincipal) queryForObject(RecepcionExternaConst.GET_DOCUMENTO_PRINCIPAL,
                new BeanPropertyRowMapper<IODocumentoPrincipal>(IODocumentoPrincipal.class),
                sidrecext);

        return ioDocumentoPrincipal.getBpdfdoc();
    }

    @Override
    public ConsultaDocumentoRecepcion getRecepcionExterna(int sidrecext) throws Exception {
        ConsultaDocumentoRecepcion consultaDocumentoRecepcion = new ConsultaDocumentoRecepcion();
        consultaDocumentoRecepcion = queryForObject(RecepcionExternaConst.GET_RECEPCION_EXTERNA,
                new BeanPropertyRowMapper<ConsultaDocumentoRecepcion>(ConsultaDocumentoRecepcion.class),
                sidrecext);
        return consultaDocumentoRecepcion;
    }

    @Override
    public void fileUploadDocumentoPrincipal(int sidemiext, byte[] cargo) throws Exception {
        //depurador.info("fileUploadDocumentoPrincipal ==>");
        update(RecepcionExternaConst.UPD_DOCUMENTO_PRINCIPAL,
                cargo,
                sidemiext
        );

    }
}
