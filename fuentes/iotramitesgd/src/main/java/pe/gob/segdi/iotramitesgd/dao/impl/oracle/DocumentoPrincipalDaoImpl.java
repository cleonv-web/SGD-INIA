package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.IODocumentoPrincipal;
import pe.gob.segdi.iotramitesgd.dao.IDocumentoPrincipalDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.DocumentoPrincipalConst;

import javax.sql.DataSource;
import java.io.Serializable;


/**
 * Objeto : DocumentoPrincipalDaoImpl.java.
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
@Repository("iDocumentoPrincipalDao")
public class DocumentoPrincipalDaoImpl extends JdbcTemplate implements IDocumentoPrincipalDao, Serializable {

    private static final long serialVersionUID = 1L;
    //private static Logger depurador = Logger.getLogger(DocumentoPrincipalDaoImpl.class.getName());

    @Autowired
    public DocumentoPrincipalDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public int insDocumentoPrincipal(IODocumentoPrincipal documentoPrincipal) throws Exception {
        int flgInsert = 0;
        flgInsert = update(DocumentoPrincipalConst.INS_DOCUMENTO_PRINCIPAL,
                documentoPrincipal.getSiddocext(),
                documentoPrincipal.getVnomdocpri(),
                documentoPrincipal.getBpdfdocpri());

        return flgInsert;
    }

    @Override
    public IODocumentoPrincipal getDocumentoPrincipal(String vcuo) throws Exception {
        IODocumentoPrincipal documentoPrincipal = new IODocumentoPrincipal();
        documentoPrincipal = queryForObject(DocumentoPrincipalConst.GET_DOCUMENTO_PRINCIPAL, IODocumentoPrincipal.class,
                vcuo);
        return documentoPrincipal;
    }


}
