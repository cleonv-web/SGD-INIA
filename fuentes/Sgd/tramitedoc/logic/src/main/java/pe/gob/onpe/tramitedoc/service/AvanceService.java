/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.AvanceBean;

/**
 *
 * @author oti2
 */
public interface AvanceService {
    List<AvanceBean> getListAvance(AvanceBean buscarAvanceBean);
    String insertAvance(AvanceBean buscarAvanzadaBean);
}
