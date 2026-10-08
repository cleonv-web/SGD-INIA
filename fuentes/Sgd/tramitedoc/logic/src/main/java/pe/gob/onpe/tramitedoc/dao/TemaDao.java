/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.TemaBean;

/**
 *
 * @author oti2
 */
public interface TemaDao {
    List<TemaBean> getListTema(TemaBean buscarTemaBean);
    String updTemaDestinos(String codigoEmision,String codTema);
    String updTemaRemitos(String codigoEmision,String codTema);
    String getEditTemaRemitos(String codigoEmision);
    String getEditTemaDestinos(String codigoEmision);
}
