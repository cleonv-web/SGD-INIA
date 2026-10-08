/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.dao;

import java.util.List;
import pe.gob.onpe.sgdtask.bean.NotificacionJobBean;
import pe.gob.onpe.sgdtask.bean.NotificacionJobDetBean;
import pe.gob.onpe.sgdtask.bean.NotificacionLogBean;

/**
 *
 * @author FSilva
 */
public interface NotificacionJobDao {
    public String[] insNotificacionJob(NotificacionJobBean notificacionJobBean);
    public String insNotificacionJobDet(NotificacionJobDetBean notificacionJobDetBean);
    public List<NotificacionLogBean> getListNotificacionLog(String tipo_envio);
    String updNotificacionLog(NotificacionLogBean notificacionJobDetBean);
}
