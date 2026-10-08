/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

//import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
//import pe.gob.onpe.tramitedoc.bean.NotificacionBean;

/**
 *
 * @author ECueva
 */
public interface NotificacionDao {
    /*String insNotificacion(NotificacionBean noti);
    String delNotificacion(NotificacionBean noti);
    String updNotificacion(NotificacionBean noti);
    List<NotificacionBean> getLsNotiPendienteEnvio(String pnuAnn, String pnuEmi);*/
    DocumentoDatoBean getDatosDoc(String nuAnn, String nuEmi);
    String notificarVistoBueno(String nuEmi,String nuAnn);
    String getLsEmpleadoNotificar(String nuEmi,String nuAnn, String detalleDoc);
    String getLsMesaVirtualNotificar(String nuEmi,String nuAnn, String detalleDoc);
    String getLsTupaNotificar(String nuEmi,String nuAnn, String detalleDoc);
       
}
