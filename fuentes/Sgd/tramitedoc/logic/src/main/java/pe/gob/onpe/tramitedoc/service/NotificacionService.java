/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

/**
 *
 * @author ecueva
 */
public interface NotificacionService {
    String procesarNotificacion(String pnuAnn, String pnuEmi, String coUser);
    String notificarVistoBueno(String nuEmi,String nuAnn);
    String getLsEmpleadoNotificar(String nuEmi,String nuAnn, String detalleDoc);
    String getLsMesaVirtualNotificar(String nuEmi,String nuAnn, String detalleDoc);
    String getLsTupaNotificar(String nuEmi,String nuAnn, String detalleDoc);
}
