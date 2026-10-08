/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.service;

import pe.gob.onpe.verificadoc.bean.DocumentoObjBean;

/**
 *
 * @author WCutipa
 */
public interface DocumentoObjService {
    DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi);
    DocumentoObjBean leerAnexo(String nuAnn, String nuEmi, String nuAne);
}
