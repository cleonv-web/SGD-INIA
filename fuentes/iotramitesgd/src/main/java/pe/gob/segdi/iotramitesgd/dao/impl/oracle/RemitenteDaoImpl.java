package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.Remitente;
import pe.gob.segdi.iotramitesgd.dao.IRemitenteDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.RemitenteConst;

import javax.sql.DataSource;
import java.io.Serializable;

/**
 * Objeto : RemitenteDaoImpl.java.
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
@Repository("iRemitenteDao")
public class RemitenteDaoImpl extends JdbcTemplate implements IRemitenteDao, Serializable {

    private static final long serialVersionUID = 1L;
    //private static Logger depurador = Logger.getLogger(RemitenteDaoImpl.class.getName());

    @Autowired
    public RemitenteDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public String insRemitente(Remitente remitente) throws Exception {
        //depurador.info("insRemitente ==>"+remitente.getCprorazsoc());
        String ruc = "";
        queryForObject(RemitenteConst.INS_REMITENTE, String.class,
                remitente.getCproruc(),
                remitente.getCprorazsoc(),
                remitente.getcDireccion());

        ruc = remitente.getCproruc();
        return ruc;
    }

    @Override
    public String getCodigoRemitente(String ruc) throws Exception {
        //depurador.info("getCodigoRemitentee ==>"+ruc);
        try {
            String cproruc = "";
            cproruc = queryForObject(RemitenteConst.GET_CODIGO_REMITENTE, String.class,
                    ruc);
            return cproruc;
        } catch (EmptyResultDataAccessException e) {
            return "";
        }

    }


}
