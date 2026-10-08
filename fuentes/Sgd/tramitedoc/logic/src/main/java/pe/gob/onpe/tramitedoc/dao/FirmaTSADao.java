/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DestinoTSABean;

/**
 *
 * @author ecueva
 */
public interface FirmaTSADao {
    List<DestinoTSABean> getLsDestinoDoc(String nuAnn, String nuEmi);
    String getCanEntidadTSA(String nuRuc, String tiDoc);
}
