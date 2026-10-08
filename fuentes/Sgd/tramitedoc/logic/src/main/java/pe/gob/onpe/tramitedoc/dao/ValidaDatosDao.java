/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import pe.gob.onpe.autentica.model.DatosUsuario;


/**
 *
 * @author WCutipa
 */
public interface  ValidaDatosDao {
    String getValidaDep(String pcoEmp,String pcoUsu, String pcoDep);
    
}
