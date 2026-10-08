/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import java.util.Map;
import pe.gob.onpe.tramitedoc.bean.CargoEntregaBean;
import pe.gob.onpe.tramitedoc.bean.DocPedienteEntregaBean;
import pe.gob.onpe.tramitedoc.bean.GuiaMesaPartesBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.TrxGeneraGuiaMpBean;

/**
 *
 * @author ECueva
 */
public interface CargoEntregaService {
    List<CargoEntregaBean> getCargosEntrega(CargoEntregaBean cargo);
    List<DocPedienteEntregaBean> getDocsPendienteEntrega(DocPedienteEntregaBean busqDoc);
    List<DocPedienteEntregaBean> getDocsPendienteEntrega(List<DocPedienteEntregaBean> docs);
    String grabarCargoEntrega(TrxGeneraGuiaMpBean trxGuia)throws Exception;
    String getJsonRptGrabarCargoEntrega(TrxGeneraGuiaMpBean trxGuia);
    GuiaMesaPartesBean getGuiaMp(String pnuAnnGuia, String pnuGuia);
    List<DocPedienteEntregaBean> getDetalleGuiaMp(String pnuAnnGuia,String pnuGuia);
    String anularGuiaMp(GuiaMesaPartesBean guia);
    String getRutaGuia(String pnuAnnGuia, String pnuGuia, String pcoUser);
    GuiaMesaPartesBean getGuiaMp(List<DocPedienteEntregaBean> docs);
    public List<CargoEntregaBean> getListaReporte(CargoEntregaBean cargo);
    public ReporteBean getGenerarReporte(Map parametros, CargoEntregaBean cargo);
    
}
