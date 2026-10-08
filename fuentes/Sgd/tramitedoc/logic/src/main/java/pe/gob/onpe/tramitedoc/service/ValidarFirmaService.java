/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

/**
 *
 * @author WCutipa
 */
public interface ValidarFirmaService {
    String getValidaCertificadoEmp(String filePdf,String pNuDni,String pNuRUC);
    String getValidaCertificadoEmp(String filePdf, String pNuDni);
}
