/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.UbigeoBean;
import pe.gob.onpe.tramitedoc.dao.ConsultaProveedorDao;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.dao.ProveedorDao;
import pe.gob.onpe.tramitedoc.service.ProveedorService;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.segdi.pide.consultas.bean.ConsultaSunatBean;
import pe.gob.segdi.pide.consultas.ws.WSConsultaRuc;

/**
 *
 * @author NGilt
 */
@Service("proveedorService")
public class ProveedorServiceImpl implements ProveedorService {

    @Autowired
    private ProveedorDao proveedorDao;

    @Autowired
    private ConsultaProveedorDao consultaProveedorDao;    
    @Autowired
    private DatosPlantillaDao datosPlantillaDao;
    public List<ProveedorBean> getProveedoresList() {
        List<ProveedorBean> list = null;
        try {
            list = proveedorDao.getProveedoresList();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public ProcessResult<List<ProveedorBean>> getProveedoresBusqList(String busNroRuc, String busRazonSocial,FiltroPaginate paginate) {        
         ProcessResult<List<ProveedorBean>> list = null;
        try {
            list = consultaProveedorDao.getProveedoresBusqList(busNroRuc, busRazonSocial,paginate);
            if(list == null || list.getResult()== null || list.getResult().size()==0){
                 try {                        
                    WSConsultaRuc wSSunat = new WSConsultaRuc();
                    ConsultaSunatBean beanDdp  = wSSunat.consultaSunat(datosPlantillaDao.getParametros("URL_SUNAT_REST"),busNroRuc);
                        if(beanDdp.getDdp_nombre()!=null){                            
                            String vResult=proveedorDao.insPideProveedor(beanDdp);                    
                            if (vResult=="OK")
                                {
                                      list = consultaProveedorDao.getProveedoresBusqList(busNroRuc, busRazonSocial,paginate);
                                }                            
                            }                        
                    }
                    catch (Exception e) {
                         e.printStackTrace();   
                    }
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<UbigeoBean> getUbigeoBusqList(UbigeoBean ubigeo) {
        List<UbigeoBean> list = null;
        try {
            list = proveedorDao.getUbigeoBusqList(ubigeo);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String insProveedor(ProveedorBean proveedor) {
        String vReturn = "NO_OK";
        try {
            proveedor.setDescripcion(proveedor.getDescripcion().toUpperCase());
            proveedor.setCproDomicil(proveedor.getCproDomicil().toUpperCase());            
            proveedor.setCproEmail(proveedor.getCproEmail().toUpperCase());
            vReturn = proveedorDao.insProveedor(proveedor);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    
    public ProveedorBean getProveedor(String codProveedor) {
        ProveedorBean proveedor = null;
        try {
            
            proveedor = proveedorDao.getProveedor(codProveedor);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return proveedor;
    }
    
    public String updProveedor(ProveedorBean proveedor, String codUsuario) {
        String vReturn = "NO_OK";
        try {
            proveedor.setDescripcion(proveedor.getDescripcion().toUpperCase().trim());
            proveedor.setCproDomicil(proveedor.getCproDomicil().toUpperCase().trim()); 
            proveedor.setCproEmail(proveedor.getCproEmail().toUpperCase().trim()); 
            
            vReturn = proveedorDao.updProveedor(proveedor, codUsuario);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }    
}
