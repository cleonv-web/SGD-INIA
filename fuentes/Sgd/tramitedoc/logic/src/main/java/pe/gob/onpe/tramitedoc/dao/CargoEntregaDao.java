/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.CargoEntregaBean;
import pe.gob.onpe.tramitedoc.bean.DetGuiaMesaPartesBean;
import pe.gob.onpe.tramitedoc.bean.DocPedienteEntregaBean;
import pe.gob.onpe.tramitedoc.bean.GuiaMesaPartesBean;

/**
 *
 * @author ecueva
 */
public interface CargoEntregaDao {
    List<CargoEntregaBean> getCargosEntrega(CargoEntregaBean cargo);
    List<DocPedienteEntregaBean> getDocsPendienteEntrega(DocPedienteEntregaBean busqDoc);
    String isDocPendienteEnGuiaMp(String pnuAnn,String pnuEmi,String pnuDes);
    DocPedienteEntregaBean getDocPendienteEntrega(String pnuAnn,String pnuEmi,String pnuDes);
    String insGuiaMp(GuiaMesaPartesBean guia);
    String insDetGuiaMp(DetGuiaMesaPartesBean detGuia);
    String getNroGuiaCabecera(String pnuAnnGuia);
    String getNroCorrelativoGuiaCabecera(String pnuAnnGuia,String pcoDepOri);
    String getNroCorrelativoDetGuiaCabecera(String pnuAnnGuia, String pnuGuia);
    String updGuiaMp(GuiaMesaPartesBean guia);
    GuiaMesaPartesBean getGuiaMp(String pnuAnnGuia, String pnuGuia);
    List<DocPedienteEntregaBean> getDetalleGuiaMp(String pnuAnnGuia,String pnuGuia);
    String updEstadoGuiaMp(String estado, String pcoUseMod, String pnuAnnGuia, String pnuGuia);
    DocPedienteEntregaBean getDependenciaDestinoDocExtRec(String pnuAnn, String pnuEmi, String pnuDes);
    List<CargoEntregaBean> getListaReporteBusqueda(CargoEntregaBean cargo);
}
