
package pe.gob.segdi.wsiotramite.service.impl;

import java.util.List;

import org.jboss.logging.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.segdi.wsiotramite.bean.DocumentoAnexo;
import pe.gob.segdi.wsiotramite.bean.DocumentoExterno;
import pe.gob.segdi.wsiotramite.dao.IDocumentoAnexoDao;
import pe.gob.segdi.wsiotramite.dao.IDocumentoExternoDao;
import pe.gob.segdi.wsiotramite.dao.IDocumentoPrincipalDao;
import pe.gob.segdi.wsiotramite.service.IDocumentoExternoService;
/**
 * 
 * Objeto : DocumentoExternoServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface documentos externos , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 28/03/2018.
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
@Service("iDocumentoExternoService")
public class DocumentoExternoServiceImp implements IDocumentoExternoService {
	private static Logger depurador = Logger.getLogger(DocumentoExternoServiceImp.class.getName());
	@Autowired
	IDocumentoExternoDao iDocumentoExternoDao;
	@Autowired
	IDocumentoPrincipalDao iDocumentoPrincipalDao;
	@Autowired
	IDocumentoAnexoDao iDocumentoAnexoDao;
	
   	@Override
	public int insDocumentoExterno(DocumentoExterno documentoExterno) throws Exception {
   		//depurador.info("insDocumentoExterno ==>");
   		int flagDocExt = 0;
   		
		int siddocext = 0;
		siddocext = iDocumentoExternoDao.insDocumentoExterno(documentoExterno);
    	if(siddocext != 0){
    		documentoExterno.getDocumentoPrincipal().setSiddocext(siddocext);
    		int flag = 0;
    		flag = iDocumentoPrincipalDao.insDocumentoPrincipal(documentoExterno.getDocumentoPrincipal());
    		if(flag == 1){
    			List<DocumentoAnexo> lista = documentoExterno.getLstDocAnexo();
    			if(lista != null){
	                for (DocumentoAnexo a : lista) {
	                    a.setSiddocext(siddocext);
	                    flag = iDocumentoAnexoDao.insDocumentoAnexo(a);
	                    if (flag == 1) {
	                    	flagDocExt = 1; 
	                    }
	                }
    			}
    		}
    	}
		
		return flagDocExt;
	}

    
}
