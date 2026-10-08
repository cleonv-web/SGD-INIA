package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.IODocumentoAnexo;
import pe.gob.segdi.iotramitesgd.dao.IDocumentoAnexoDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.DocumentoAnexoConst;

import javax.sql.DataSource;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;


/**
 * Objeto : DocumentoAnexoDaoImpl.java.
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
@Repository("iDocumentoAnexoDao")
public class DocumentoAnexoDaoImpl extends JdbcTemplate implements IDocumentoAnexoDao, Serializable {

    private static final long serialVersionUID = 1L;
    //private static Logger depurador = Logger.getLogger(DocumentoAnexoDaoImpl.class.getName());

    @Autowired
    public DocumentoAnexoDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

    @Override
    public int insDocumentoAnexo(IODocumentoAnexo documentoAnexo) throws Exception {
        int flgInsert = 0;
        flgInsert = queryForObject(DocumentoAnexoConst.INS_DOCUMENTO_ANEXO, Integer.class,
                documentoAnexo.getSiddocext(),
                documentoAnexo.getVnomdoc());


        return flgInsert;
    }

    @Override
    public List<IODocumentoAnexo> getListaDocumentoAnexo(int siddocext) throws Exception {
        //depurador.info("getListaDocumentoAnexo ==>"+siddocext);
        List<IODocumentoAnexo> lstDocAnx = new ArrayList<IODocumentoAnexo>();
        lstDocAnx = query(DocumentoAnexoConst.GET_LISTA_DOCUMENTO_ANEXO, new BeanPropertyRowMapper<IODocumentoAnexo>(IODocumentoAnexo.class),
                siddocext);
        //depurador.info("tamanio anexo ==>"+lstDocAnx.size());
        return lstDocAnx;
    }

    @Override
    public List<IODocumentoAnexo> getListaDocumentoAnexo2(String nuemi, String nuann) throws Exception {
        //depurador.info("getListaDocumentoAnexo2 ==>"+nuemi+ " - "+nuann);
        List<IODocumentoAnexo> lstDocAnx = new ArrayList<IODocumentoAnexo>();
        lstDocAnx = query(DocumentoAnexoConst.GET_LISTA_DOCUMENTO_ANEXO2, new BeanPropertyRowMapper<IODocumentoAnexo>(IODocumentoAnexo.class),
                nuemi,
                nuann);
        //depurador.info("TAMANIO ==>"+lstDocAnx.size());
        return lstDocAnx;
    }

    @Override
    public IODocumentoAnexo getDocumentoAnexo(String nuemi, String nuann, int nu_ane) throws Exception {
        //depurador.info("getDocumentoAnexo ==>"+nuemi+ " - "+nuann+ " - "+nu_ane);
        IODocumentoAnexo documentoAnexo = null;
        documentoAnexo = queryForObject(DocumentoAnexoConst.GET_DOCUMENTO_ANEXO, new BeanPropertyRowMapper<IODocumentoAnexo>(IODocumentoAnexo.class),
                nuemi,
                nuann,
                nu_ane);
        return documentoAnexo;
    }


}
