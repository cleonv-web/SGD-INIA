/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.OtroOrigenBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

/**
 *
 * @author NGilt
 */
public interface OtroOrigenService {
    public List<OtroOrigenBean> getOtrosOrigenesList();

    public List<SiElementoBean> getTipoDocumentoList();

    public List<OtroOrigenBean> getOtroOrigenBusqList(OtroOrigenBean otroOrigen);
    
    public ProcessResult<List<OtroOrigenBean>> getOtroOrigenBusqList(String busRazonSocial,FiltroPaginate paginate);

    public String insOtroOrigen(OtroOrigenBean otroOrigen);

    public OtroOrigenBean getOtroOrigen(String codOrigen);

    public String updOtroOrigen(OtroOrigenBean otroOrigen);
    
    public List<OtroOrigenBean> getOtrosOrigenesPorTipo(String codTipoOtroOrigen);
    
}
