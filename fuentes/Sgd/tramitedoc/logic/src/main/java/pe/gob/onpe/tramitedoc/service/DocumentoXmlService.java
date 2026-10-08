/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import pe.gob.onpe.tramitedoc.bean.DatosPlantillaDoc;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;

/**
 *
 * @author WCutipa
 */
public interface DocumentoXmlService {
    DatosPlantillaDoc datosParaPlantilla(String pnuAnn, String pnuEmi);
    DocumentoObjBean crearDocx(String pnuAnn, String pnuEmi);
    DocumentoObjBean crearPdfx(String pnuAnn, String pnuEmi, String ptiCap);
    String generarFormatoSiglas(String tipoDoc, String nuDoc, String anio, String institucion, String uuoo);
    
}
