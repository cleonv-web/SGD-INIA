/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.UbigeoBean;
import pe.gob.segdi.pide.consultas.bean.ConsultaSunatBean;


/**
 *
 * @author NGilt
 */
public interface ProveedorDao {

    public List<ProveedorBean> getProveedoresList();

    public List<UbigeoBean> getUbigeoBusqList(UbigeoBean ubigeo);

    public String insProveedor(ProveedorBean proveedor);
    
    public ProveedorBean getProveedor(String codProveedor);
    
    public String updProveedor(ProveedorBean proveedor, String codProveedor);
    
    public String insPideProveedor(ConsultaSunatBean proveedor);
        
}
