/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.dao;

import pe.gob.onpe.sgdtask.ws.bean.DocumentoPrincipalBean;

/**
 *
 * @author FSilva
 */
public interface DocumentoPrincipalDao {
    //Obtienen el documento principal en base al codigo de recepcion
    public DocumentoPrincipalBean getDocumentoPrincipal(long coEmision);
}
