package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.Maestro;
import pe.gob.segdi.iotramitesgd.bean.ParametrosBean;
import pe.gob.segdi.iotramitesgd.dao.IMaestroDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.MaestroConst;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Objeto : MaestroDaoImpl.java.
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
@Repository("iMaestroDao")
public class MaestroDaoImpl extends JdbcTemplate implements IMaestroDao, Serializable {

    private static final long serialVersionUID = 1L;
    //private static Logger depurador = Logger.getLogger(MaestroDaoImpl.class.getName());

    @Autowired
    public MaestroDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }


    @Override
    public List<Maestro> getDependencia() throws Exception {
        //depurador.info("getDependencia ==>");
        List<Maestro> lsta = new ArrayList<Maestro>();
        lsta = query(MaestroConst.GET_DEPENDENCIA, new BeanPropertyRowMapper<Maestro>(Maestro.class));
        return lsta;
    }


    @Override
    public List<Maestro> getTipoDocumentoMV() throws Exception {
        List<Maestro> lsta = new ArrayList<Maestro>();
        lsta = query(MaestroConst.GET_TIPO_DOCUMENTO_MV, new BeanPropertyRowMapper<Maestro>(Maestro.class));
        return lsta;
    }


    @Override
    public List<ParametrosBean> getParametros(String elemento) throws Exception {
        List<ParametrosBean> lsta = new ArrayList<ParametrosBean>();
        lsta = query(MaestroConst.GET_PARAMETROS, new BeanPropertyRowMapper<ParametrosBean>(ParametrosBean.class),
                elemento);
        return lsta;
    }


}
