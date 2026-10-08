/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.dao;

import java.util.List;
import pe.gob.onpe.sgdtask.ws.bean.DocumentoExternoBean;
import pe.gob.onpe.sgdtask.ws.bean.EmisionExternaBean;

/**
 *
 * @author ecueva
 */
public interface EmisionExternaDao {   
    
    String updRecepcionexterna(EmisionExternaBean emi);    
    EmisionExternaBean getEmisionExterna(long coEmisionExterna);
    //Retorna La lista de emisiones via WS
    public List<EmisionExternaBean> getListEmisionExternaPendientes();
    public DocumentoExternoBean getDocumentoExterno(long coEmision);
}
