package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.IODocumentoExterno;
import pe.gob.segdi.iotramitesgd.dao.IDocumentoExternoDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.DocumentoExternoConst;

import javax.sql.DataSource;
import java.io.Serializable;
import java.math.BigDecimal;
import java.sql.PreparedStatement;


/**
 * Objeto : DocumentoExternoDaoImpl.java.
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
@Repository("iDocumentoExternoDao")
public class DocumentoExternoDaoImpl extends JdbcTemplate implements IDocumentoExternoDao, Serializable {

    private static final long serialVersionUID = 1L;
    //private static Logger depurador = Logger.getLogger(DocumentoExternoDaoImpl.class.getName());

    @Autowired
    public DocumentoExternoDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public int insDocumentoExterno(IODocumentoExterno documentoExterno) throws Exception {
        //depurador.info("insDocumentoExterno ==>");

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "siddocext";
        update(con -> {
            PreparedStatement ps = con.prepareStatement(DocumentoExternoConst.INS_DOCUMENTO_EXTERNO, new String[]{id_column});
            ps.setInt(1, documentoExterno.getSidemiext());
            ps.setString(2, documentoExterno.getVnomentemi());
            ps.setString(3, documentoExterno.getCcodtipdoc());
            ps.setString(4, documentoExterno.getVnumdoc());
            ps.setDate(5, documentoExterno.getDfecdoc());
            ps.setString(6, documentoExterno.getVuniorgdst());
            ps.setString(7, documentoExterno.getVnomdst());
            ps.setString(8, documentoExterno.getVnomcardst());
            ps.setString(9, documentoExterno.getVasu());
            ps.setInt(10, documentoExterno.getSnumanx());
            ps.setInt(11, documentoExterno.getSnumfol());
            ps.setString(12, documentoExterno.getVurldocanx());
            return ps;
        }, keyHolder);

        BigDecimal id = (BigDecimal) keyHolder.getKeys().get(id_column);
        System.out.println("ID ==>" + id);
        return Integer.valueOf(id.intValue());
    }


}
