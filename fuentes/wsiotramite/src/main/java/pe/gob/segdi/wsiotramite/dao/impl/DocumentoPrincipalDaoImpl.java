package pe.gob.segdi.wsiotramite.dao.impl;

import java.io.Serializable;

import javax.sql.DataSource;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import pe.gob.segdi.wsiotramite.bean.DocumentoPrincipal;
import pe.gob.segdi.wsiotramite.dao.IDocumentoPrincipalDao;
import pe.gob.segdi.wsiotramite.dao.impl.cons.DocumentoPrincipalConst;


/**
 * 
 * Objeto : DocumentoPrincipalDaoImpl.java.
 * Descripción : Clase de servicio, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 28/03/2018.
 * Autor : Arturo Delgadoo.
 * 
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 * 
 * 
 */
@Repository("iDocumentoPrincipalDao")
public class DocumentoPrincipalDaoImpl extends JdbcTemplate implements IDocumentoPrincipalDao,Serializable{
    
	private static final long serialVersionUID = 1L;
	private static Logger depurador = Logger.getLogger(DocumentoPrincipalDaoImpl.class.getName());

	@Autowired
    public DocumentoPrincipalDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

	@Override
	public int insDocumentoPrincipal(DocumentoPrincipal documentoPrincipal) throws Exception{
		int flgInsert = 0;
		
		flgInsert = update(DocumentoPrincipalConst.INS_DOCUMENTO_PRINCIPAL,
				documentoPrincipal.getSiddocext(),
				documentoPrincipal.getVnomdoc(),
				documentoPrincipal.getBpdfdoc());
		
		return flgInsert;
	}

	
	
	
   
}
