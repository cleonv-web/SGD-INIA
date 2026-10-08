/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.Date;

/**
 *
 * @author ecueva
 */
public interface DocumentoTriggerDao {
    public String actualizarEstadoAfterEmitirDocumento(String pnuAnn, String pnuEmi, String pcoDepEmi, Date pfeEmi);
}
