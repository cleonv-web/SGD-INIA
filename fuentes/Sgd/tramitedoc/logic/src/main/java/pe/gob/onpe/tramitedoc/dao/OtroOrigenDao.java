/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.OtroOrigenBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;

/**
 *
 * @author NGilt
 */
public interface OtroOrigenDao {
    
    public List<OtroOrigenBean> getOtrosOrigenesList();

    public List<SiElementoBean> getTipoDocumentoList();

    public String insOtroOrigen(OtroOrigenBean otroOrigen);

    public OtroOrigenBean getOtroOrigen(String codOrigen);

    public String updOtroOrigen(OtroOrigenBean otroOrigen);
    
    public List<OtroOrigenBean> getOtrosOrigenesPorTipo(String codTipoOtroOrigen);
    
}
