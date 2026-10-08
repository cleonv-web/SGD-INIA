/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DatosPlantillaDoc;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.PlantillaDocx;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;

/**
 *
 * @author wcutipa
 */
public interface DatosPlantillaDao {
    DatosPlantillaDoc getDocumentoEmitido(String pnuAnn, String pnuEmi);
    List<DestinatarioDocumentoEmiBean> getLstDestintarios(String pnuAnn, String pnuEmi);
    List<ReferenciaBean> getLstReferencia(String pnuAnn, String pnuEmi);
    String getDistritoLocal(String pco_local);
    String getParametros(String pco_param);
    String getPiePagina(String pco_emp, String pco_dep);

    List<PlantillaDocx> getLstPlantillasDocx(String pcoDep,String idPlantilla);
//    PlantillaDocx getPlantillaDocx(String pcoDep);
    
    
    
}
