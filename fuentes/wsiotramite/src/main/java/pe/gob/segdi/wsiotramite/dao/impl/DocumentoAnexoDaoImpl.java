package pe.gob.segdi.wsiotramite.dao.impl;

import java.io.Serializable;

import javax.sql.DataSource;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pe.gob.segdi.wsiotramite.bean.DocumentoAnexo;
import pe.gob.segdi.wsiotramite.dao.IDocumentoAnexoDao;
import pe.gob.segdi.wsiotramite.dao.impl.cons.DocumentoAnexoConst;


/**
 * 
 * Objeto : DocumentoAnexoDaoImpl.java.
 * Descripción : Clase de servicio, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 28/03/2018.
 * Autor : Arturo Delgadoo.
 * 
 * -----------------------s-------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 * 
 * 
 */
@Repository("iDocumentoAnexoDao")
public class DocumentoAnexoDaoImpl extends JdbcTemplate implements IDocumentoAnexoDao,Serializable{
    
	private static final long serialVersionUID = 1L;
	//private static Logger depurador = Logger.getLogger(DocumentoAnexoDaoImpl.class.getName());

	@Autowired
    public DocumentoAnexoDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

	@Override
	public int insDocumentoAnexo(DocumentoAnexo documentoAnexo) throws Exception{
		int flgInsert = 0;
		
		flgInsert = update(DocumentoAnexoConst.INS_DOCUMENTO_ANEXO,
				documentoAnexo.getSiddocext(),
				documentoAnexo.getVnomdoc()
				);

		return flgInsert;
	}

	
   
}
