package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoEmision;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExterna;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExternaBean;
import pe.gob.segdi.iotramitesgd.dao.IEmisionExternaDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.EmisionExternaConst;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;

import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


/**
 * Objeto : EmisionExternaDaoImpl.java.
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
@Repository("iEmisionExternaDao")
public class EmisionExternaDaoImpl extends JdbcTemplate implements IEmisionExternaDao, Serializable {

    private static final long serialVersionUID = 1L;
    private static Logger depurador = Logger.getLogger(EmisionExternaDaoImpl.class.getName());

    @Autowired
    public EmisionExternaDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public int insEmisionExterna(IOEmisionExterna emisionExterna) throws Exception {
        //depurador.info("insEmisionExterna ==>");

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "sidemiext";
        update(con -> {
            PreparedStatement ps = con.prepareStatement(EmisionExternaConst.INS_EMISION_EXTERNA, new String[]{id_column});
            ps.setString(1, emisionExterna.getVnumregstd());
            ps.setString(2, emisionExterna.getVrucentrec());
            ps.setString(3, emisionExterna.getVnomentrec());
            ps.setString(4, emisionExterna.getCtipdociderem());
            ps.setString(5, emisionExterna.getVnumdociderem());
            ps.setString(6, emisionExterna.getVcoduniorgrem());
            ps.setString(7, emisionExterna.getVuniorgrem());
            ps.setString(8, emisionExterna.getVusureg());
            ps.setString(9, emisionExterna.getVcuoref());
            return ps;
        }, keyHolder);

        BigDecimal id = (BigDecimal) keyHolder.getKeys().get(id_column);

        return Integer.valueOf(id.intValue());

    }

    @Override
    public List<ConsultaDocumentoEmision> bsqEmisionExterna(String vrucentdst, String cflgest, String codependencia, String dfecdesde, String dfechasta) throws Exception {
        depurador.info("bsqEmisionExterna ==>");
        List<ConsultaDocumentoEmision> lstEmiExt = new ArrayList<ConsultaDocumentoEmision>();

        String query = "";
        StringBuilder sql = null;
        if (!dfecdesde.equals("")) {
            query = EmisionExternaConst.BSQ_EMISION_EXTERNA;
            sql = new StringBuilder();
            sql.append(query);

            //sql.append(" AND ee.cflgest IN (\"+\"'\"+cflgest+\"'\"+\")");
            sql.append(" AND ee.cflgest ='" + cflgest + "'");
            sql.append(" and ee.dfecreg BETWEEN  to_date(?,'dd/mm/YY') AND to_date(?,'dd/mm/YY')").
                    append(" order by ee.sidemiext DESC");

            lstEmiExt = query(sql.toString(),
                    new BeanPropertyRowMapper<ConsultaDocumentoEmision>(ConsultaDocumentoEmision.class),
                    dfecdesde,
                    dfechasta);
        } else {
            query = EmisionExternaConst.BSQ_EMISION_EXTERNA;
            sql = new StringBuilder();
            sql.append(query);
            sql.append(" AND ee.cflgest ='" + cflgest + "'");
            sql.append(" order by ee.sidemiext DESC");
            depurador.info("SQL ==>" + sql);
            lstEmiExt = query(sql.toString(),
                    new BeanPropertyRowMapper<ConsultaDocumentoEmision>(ConsultaDocumentoEmision.class));
        }
        depurador.info("TAMANIO BUSQUEDA ==>" + lstEmiExt.size());
        return lstEmiExt;
    }

    @Override
    public ConsultaDocumentoEmision getEmisionExterna(int sidemiext) throws Exception {
        //depurador.info("getEmisionExterna ==>"+sidemiext);
        ConsultaDocumentoEmision consultaDocumentoEmision = new ConsultaDocumentoEmision();
        //depurador.info("sql ==>"+EmisionExternaConst.GET_EMISION_EXTERNA);
        consultaDocumentoEmision = queryForObject(EmisionExternaConst.GET_EMISION_EXTERNA, new BeanPropertyRowMapper<ConsultaDocumentoEmision>(ConsultaDocumentoEmision.class),
                sidemiext);

        return consultaDocumentoEmision;
    }

    @Override
    public int insCuoEmisionExterna(String vcuo, int sidemiext) throws Exception {
        //depurador.info("insCuoEmisionExterna ==>"+vcuo+ " - "+sidemiext);
        int flag = 0;
        flag = update(EmisionExternaConst.INS_CUO_EMISION_EXTERNA,
                vcuo,
                sidemiext);
        return flag;
    }

    @Override
    public void updEstadoEmisionExterna(int sidemiext, String cflgest) throws Exception {
        //depurador.info("updEstadoEmisionExterna ==>"+sidemiext+ " - "+cflgest);
        update(EmisionExternaConst.UPD_ESTADO_EMISION_EXTERNA,
                cflgest,
                sidemiext);
    }

    @Override
    public int updCargoEmisionExterna(IOEmisionExterna emisionExterna) throws Exception {
        //depurador.info("updCargoEmisionExterna ==>");
        Integer flag = 0;
        flag = update(EmisionExternaConst.UPD_CARGO_EMISION_EXTERNA,
                emisionExterna.getVnumregstdrec(),
                emisionExterna.getVanioregstdrec(),
                emisionExterna.getDfecregstdrec(),
                emisionExterna.getVuniorgstdrec(),
                emisionExterna.getVusuregstdrec(),
                emisionExterna.getBcarstdrec(),
                emisionExterna.getCflgest(),
                emisionExterna.getVobs(),
                emisionExterna.getVcuo()
        );
        return flag;
    }

    @Override
    public int updEstadoEnvio(int sidemiext, String cflgenv) throws Exception {
        //depurador.info("updEstadoEnvio ==>"+sidemiext+" cflgenv ==>"+cflgenv);
        Integer flag = 0;
        flag = update(EmisionExternaConst.UPD_ESTADO_ENVIO,
                cflgenv,
                sidemiext
        );
        return flag;
    }

    @Override
    public String obtEstadoEnvio(int sidemiext) throws Exception {
        //depurador.info("obtEstadoEnvio ==>"+sidemiext);
        return queryForObject(EmisionExternaConst.OBT_ESTADO_ENVIO, String.class, sidemiext);
    }

    @Override
    public String obtCuo(int sidemiext) throws Exception {
        //depurador.info("obtCuo ==>");
        return queryForObject(EmisionExternaConst.OBT_CUO, String.class, sidemiext);
    }

    @Override
    public void anularRecepcion(int sidemiext, String vdesanu) throws Exception {
        //depurador.info(" P_CFLGANU ==>"+Utilitarios.ObtenerDatosProperties("Parametros", "P_CFLGANU"));

        update(EmisionExternaConst.ANULAR_RECEPCION,
                Utilitarios.ObtenerDatosProperties("Parametros", "P_CFLGANU"),
                vdesanu,
                sidemiext);
    }

    @Override
    public List<ConsultaDocumentoEmision> getEmisionExternaPendientes(String vnumregstd, String vanioregstd)
            throws Exception {
        List<ConsultaDocumentoEmision> lsta = new ArrayList<ConsultaDocumentoEmision>();
        lsta = query(EmisionExternaConst.GET_EMISION_EXTERNA_PENDIENTES, new BeanPropertyRowMapper<ConsultaDocumentoEmision>(ConsultaDocumentoEmision.class),
                vnumregstd,
                vanioregstd);
        return lsta;
    }

    ///////////////////////////////////////////////////////////////////
    public List<IOEmisionExternaBean> getDocumentosEnviados(String vrucentrec) throws Exception {
        //depurador.info("getDespachoEnviados ==>");
        List<IOEmisionExternaBean> lstEmi = new ArrayList<IOEmisionExternaBean>();
        lstEmi = query(EmisionExternaConst.GET_DOCUMENTOS_ENVIADOS, new BeanPropertyRowMapper<IOEmisionExternaBean>(IOEmisionExternaBean.class),
                vrucentrec);
        ////depurador.info("TAMANIO ==>"+lstEmi.size());
        return lstEmi;
    }

    public int actualizarCargoDespacho(IOEmisionExternaBean ioEmisionExternaBean) throws Exception {
        //depurador.info("actualizarCargoDespacho ==>");
        Integer flag = 0;
        flag = update(EmisionExternaConst.ACTUALIZAR_CARGO_DESPACHO,
                ioEmisionExternaBean.getVnumregstdrec(),
                ioEmisionExternaBean.getVanioregstdrec(),
                ioEmisionExternaBean.getDfecregstdrec(),
                ioEmisionExternaBean.getVuniorgstdrec(),
                ioEmisionExternaBean.getVusuregstdrec(),
                ioEmisionExternaBean.getBcarstdrec(),
                ioEmisionExternaBean.getCflgest(),
                ioEmisionExternaBean.getVobs(),
                ioEmisionExternaBean.getVcuo()
        );

        return flag;
    }

    public int actualizarEstadoDespacho(String cflgest) throws Exception {

        Integer flag = 0;
        flag = update(EmisionExternaConst.ACTUALIZAR_ESTADOO_DESPACHO,
                cflgest
        );

        return flag;
    }

    public String getNumeroDocumento(String vcuo) throws Exception {
        depurador.info("GET_NUMERO_DOCUMENTO ==>" + EmisionExternaConst.GET_NUMERO_DOCUMENTO);
        String numeroDocumento = "";
        try {
            numeroDocumento = queryForObject(EmisionExternaConst.GET_NUMERO_DOCUMENTO, String.class, vcuo);

        } catch (EmptyResultDataAccessException e) {
            return "-1";
        }
        return numeroDocumento;
    }

    public List<IOEmisionExternaBean> getEntidadEnviados() throws Exception {
        depurador.info("getEntidadEnviados ==>");
        List<IOEmisionExternaBean> lstEmi = query(EmisionExternaConst.GET_ENTIDAD_ENVIADOS, new BeanPropertyRowMapper<IOEmisionExternaBean>(IOEmisionExternaBean.class));
        depurador.info("TAMANIO ==>" + lstEmi.size());
        return lstEmi;
    }

    @Override
    public void actualizarCargo(int sidemiext, byte[] cargo) throws Exception {
        //depurador.info("insDocumentoAnexo ==>");
        update(EmisionExternaConst.UPD_CARGO,
                cargo,
                sidemiext
        );

    }

}
