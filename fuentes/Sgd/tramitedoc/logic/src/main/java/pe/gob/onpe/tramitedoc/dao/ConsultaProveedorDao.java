/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

/**
 *
 * @author ECueva
 */
public interface ConsultaProveedorDao {
    ProcessResult<List<ProveedorBean>> getProveedoresBusqList(String busNroRuc, String busRazonSocial,FiltroPaginate paginate);
}
