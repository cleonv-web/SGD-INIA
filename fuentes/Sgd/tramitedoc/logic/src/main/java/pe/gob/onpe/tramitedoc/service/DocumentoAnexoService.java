/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

/**
 *
 * @author ecueva
 */
public interface DocumentoAnexoService {
    String cargaDocAnexoFirmado(String pnuAnn,String pnuEmi,String pnuAne,String pnuSecFirma,String coUsuario, String prutaDoc, String dniUsu, String nroRucInstitucion) throws Exception;
}
