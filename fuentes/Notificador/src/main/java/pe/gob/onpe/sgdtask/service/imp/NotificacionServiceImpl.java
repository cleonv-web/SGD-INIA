/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.service.imp;

import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sgdtask.bean.DocumentosVbBean;
import pe.gob.onpe.sgdtask.bean.EmailMensajeBean;
import pe.gob.onpe.sgdtask.bean.NotificacionJobBean;
import pe.gob.onpe.sgdtask.bean.NotificacionJobDetBean;
import pe.gob.onpe.sgdtask.bean.NotificacionLogBean;
import pe.gob.onpe.sgdtask.bean.PersonalVbBean;
import pe.gob.onpe.sgdtask.dao.NotificacionJobDao;
import pe.gob.onpe.sgdtask.dao.PersonalVbDao;
import pe.gob.onpe.sgdtask.service.NotificacionService;
import pe.gob.onpe.sgdtask.util.ApplicationProperties;
import pe.gob.onpe.sgdtask.util.MailProperties;
import pe.gob.onpe.sgdtask.util.MailUtility;
import pe.gob.onpe.sgdtask.util.ValidarTransaccionException;

/**
 *
 * @author FSilva
 */
@Service
public class NotificacionServiceImpl implements NotificacionService{
    @Autowired
    ApplicationProperties applicationProperties;
    @Autowired
    MailProperties mailProperties;
    @Autowired
    PersonalVbDao personalVbDao;
    @Autowired
    NotificacionJobDao notificacionJobDao;
    private static Logger logger=Logger.getLogger(NotificacionServiceImpl.class);
    @Scheduled(cron = "${timer.cron}")
    public void getIniciarTareaEnviarNotificacionLog() throws Exception {            
            logger.warn("Se inició la tarea programada de enviar notificacion.");                   
            List<NotificacionLogBean> listaNotificacionLog=getListNotificacionLog("scheduler");            
            if (listaNotificacionLog != null && !listaNotificacionLog.isEmpty()) {
                //Realizar la conexión al servidor SMTP
                MailUtility.getInstancia().setMailProperties(mailProperties);
                String vReturn = MailUtility.getInstancia().connectToServerMail();
                if (vReturn.equals("OK")) {
                    for (NotificacionLogBean p : listaNotificacionLog) {
                        //Enviar correo 
                        if (p.getParaEmail()!= null) {
                            this.getEnviarNoficacionLogCorreo(p);
                        }
                    }
                }else{
                    logger.error("No se finalizó la ejecución de la tarea debido a que no se logró establecer conexión con el servidor de correos.");       
                }
            }
            logger.warn("Se finalizó la tarea programada de enviar notificacion.");
    }
    @Scheduled(cron = "${timer.cron.mvpciudadano}")
    public void getIniciarTareaEnviarNotificacionLogMPVCiudadano() throws Exception {            
            logger.warn("Se inició la tarea programada de enviar notificacion.");                   
            List<NotificacionLogBean> listaNotificacionLog=getListNotificacionLog("schedulermpvc");            
            if (listaNotificacionLog != null && !listaNotificacionLog.isEmpty()) {
                //Realizar la conexión al servidor SMTP
                MailUtility.getInstancia().setMailProperties(mailProperties);
                String vReturn = MailUtility.getInstancia().connectToServerMail();
                if (vReturn.equals("OK")) {
                    for (NotificacionLogBean p : listaNotificacionLog) {
                        //Enviar correo 
                        if (p.getParaEmail()!= null) {
                            this.getEnviarNoficacionLogCorreoMPVC(p);
                        }
                    }
                }else{
                    logger.error("No se finalizó la ejecución de la tarea debido a que no se logró establecer conexión con el servidor de correos.");       
                }
            }
            logger.warn("Se finalizó la tarea programada de enviar notificacion.");
    }
    //@Scheduled(cron = "${timer.cron}")
    public void getIniciarTareaPersonalNoVb() throws Exception {            
            logger.warn("Se inició la tarea programada.");                   
            List<PersonalVbBean> listaPersonal=getListPersonalNoVB();            
            if (listaPersonal != null && !listaPersonal.isEmpty()) {
                //Realizar la conexión al servidor SMTP
                MailUtility.getInstancia().setMailProperties(mailProperties);
                String vReturn = MailUtility.getInstancia().connectToServerMail();
                if (vReturn.equals("OK")) {
                    for (PersonalVbBean p : listaPersonal) {
                        //Enviar correo
                        p.setCoUseCre("NOTIFICADOR");
                        if (p.getEmailEmp() != null) {
                            this.getEnvioCorreoVoBo(p);
                        }
                    }
                }else{
                    logger.error("No se finalizó la ejecución de la tarea debido a que no se logró establecer conexión con el servidor de correos.");       
                }
            }
            logger.warn("Se finalizó la tarea programada.");
    } 
    
    public void getEnviarNoficacionLogCorreo(NotificacionLogBean p) {
        p.setNoEntidadNotifica(applicationProperties.getNoEntidad());
        p.setDeSiglaEntidadNotifica(applicationProperties.getDeSiglaEntidad());
        EmailMensajeBean emailMensajeBean = new EmailMensajeBean(); 
        if(p.getContenido().equals("new")){
            String deCuerpoMensaje = MailUtility.getInstancia().getEmailLogHtml(p);
            emailMensajeBean.setDeCuerpoMensaje(deCuerpoMensaje);
            p.setContenido(deCuerpoMensaje);                  
        }
        else{
            emailMensajeBean.setDeCuerpoMensaje(p.getContenido());
        }
        emailMensajeBean.setDeTituloMensaje(p.getAsunto());
        emailMensajeBean.setDeCorreoDestino(p.getParaEmail());         
        //Enviar correo                        
        if (MailUtility.getInstancia().isOpenConnectServerMail()) {
            String vReturn = MailUtility.getInstancia().sendMailHtml(emailMensajeBean);
            p.setCoEstadoEnvio("0");
            if (vReturn.equals("OK")) {
                p.setCoEstadoEnvio("1");
            }
            p.setError(vReturn);
            //Registrar los envios  
            try { 
                notificacionJobDao.updNotificacionLog(p);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("ERROR", ex);
            }
        }
    }    
    public void getEnviarNoficacionLogCorreoMPVC(NotificacionLogBean p) {
        p.setNoEntidadNotifica(applicationProperties.getNoEntidad());
        p.setDeSiglaEntidadNotifica(applicationProperties.getDeSiglaEntidad());
        EmailMensajeBean emailMensajeBean = new EmailMensajeBean(); 
        if(p.getContenido().equals("new")){
            String deCuerpoMensaje = p.getCuerpo();
            emailMensajeBean.setDeCuerpoMensaje(deCuerpoMensaje);
            p.setContenido(deCuerpoMensaje);                  
        }
        else{
            emailMensajeBean.setDeCuerpoMensaje(p.getContenido());
        }
        emailMensajeBean.setDeTituloMensaje(p.getAsunto());
        emailMensajeBean.setDeCorreoDestino(p.getParaEmail());         
        //Enviar correo                        
        if (MailUtility.getInstancia().isOpenConnectServerMail()) {
            String vReturn = MailUtility.getInstancia().sendMailHtml(emailMensajeBean);
            p.setCoEstadoEnvio("0");
            if (vReturn.equals("OK")) {
                p.setCoEstadoEnvio("1");
            }
            p.setError(vReturn);
            //Registrar los envios  
            try { 
                notificacionJobDao.updNotificacionLog(p);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("ERROR", ex);
            }
        }
    }    
    public void getEnvioCorreoVoBo(PersonalVbBean p) {
        p.setNoEntidadNotifica(applicationProperties.getNoEntidad());
        p.setDeSiglaEntidadNotifica(applicationProperties.getDeSiglaEntidad());
        EmailMensajeBean emailMensajeBean = new EmailMensajeBean();
        //Construyendo el cuerpo del mensaje en el metodo siguente
        String deCuerpoMensaje = MailUtility.getInstancia().getEmailHtml(p);
        emailMensajeBean.setDeCuerpoMensaje(deCuerpoMensaje);
        emailMensajeBean.setDeTituloMensaje("Notificación del Documento para visto bueno");
        emailMensajeBean.setDeCorreoDestino(p.getEmailEmp());
        //Correo de prueba
        //emailMensajeBean.setDeCorreoDestino("XXXX@onpe.gob.pe");
        //Enviar correo                        
        if (MailUtility.getInstancia().isOpenConnectServerMail()) {
            String vReturn = MailUtility.getInstancia().sendMailHtml(emailMensajeBean);
            p.setCoEstadoEnvio("0");
            if (vReturn.equals("OK")) {
                p.setCoEstadoEnvio("1");
            }
            //Registrar los envios  
            try {
                this.insNotificacionJob(p);
            } catch (Exception ex) {
                ex.printStackTrace();
                logger.error("ERROR", ex);
            }
        }
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void insNotificacionJob(PersonalVbBean personalVbBean) throws Exception {
        NotificacionJobBean notificacionJobBean = new NotificacionJobBean();
        notificacionJobBean.setCoEmp(personalVbBean.getCoEmpVb());
        notificacionJobBean.setCoEstado(personalVbBean.getCoEstadoEnvio());
        notificacionJobBean.setEmailEmp(personalVbBean.getEmailEmp());        
        notificacionJobBean.setCoUseCre(personalVbBean.getCoUseCre());
        String msgDao[] = notificacionJobDao.insNotificacionJob(notificacionJobBean);
        if (msgDao[0].equals("01")) {
            long coNotificacionJobBean = Long.parseLong(msgDao[1]);
            notificacionJobBean.setCoNotificacionJob(coNotificacionJobBean);
            for (DocumentosVbBean d : personalVbBean.getListaDocumentos()) {
                NotificacionJobDetBean notificacionJobDetBean = new NotificacionJobDetBean();
                notificacionJobDetBean.setCoEmpVb(d.getCoEmpVb());
                notificacionJobDetBean.setCoEstado("1");
                notificacionJobDetBean.setCoNotificacionJob(coNotificacionJobBean);
                notificacionJobDetBean.setNuAnn(d.getNuAnn());
                notificacionJobDetBean.setNuEmi(d.getNuEmi());
                String msg = notificacionJobDao.insNotificacionJobDet(notificacionJobDetBean);
                if (!msg.equals("01")) {
                    logger.error("Error registrando el detalle de la notificación");    
                    throw new ValidarTransaccionException("Error registrando el detalle");
                }
            }
        } else {
            logger.error("Error registrando la cabecera de la notificación");                
            throw new ValidarTransaccionException(msgDao[1]);
        }
    }    
    public List<PersonalVbBean> getListPersonalNoVB(){
        List<PersonalVbBean> listaPersonal =null;
        try {
            listaPersonal=personalVbDao.getListPersonalNoVB();
        } catch (Exception e) {
            logger.error("Error al obtener la lista de personal pendiente de Visto Bueno");   
            e.printStackTrace();
        }
        return listaPersonal;
    }
    
   public List<NotificacionLogBean> getListNotificacionLog(String tipo_envio){
        List<NotificacionLogBean> listaPersonal =null;
        try {
            listaPersonal=notificacionJobDao.getListNotificacionLog(tipo_envio);
        } catch (Exception e) {
            logger.error("Error al obtener la lista de Notificacion Log: "+e.toString());   
            e.printStackTrace();
        }
        return listaPersonal;
    }
    
    @Override
    public String getEnviarMailVoBoPorDocumento(String nuAnn, String nuEmi, String coUseCre) {
        String vReturn="NO_OK";
        List<PersonalVbBean> listaPersonal =null;
        try {
            listaPersonal=personalVbDao.getListPersonalNoVBPorDocumento(nuAnn, nuEmi);
            if (listaPersonal != null && !listaPersonal.isEmpty()) {
                //Realizar la conexión al servidor SMTP
                MailUtility.getInstancia().setMailProperties(mailProperties);
                vReturn = MailUtility.getInstancia().connectToServerMail();
                if (vReturn.equals("OK")) {
                    for (PersonalVbBean p : listaPersonal) {
                        //Enviar correo
                        p.setCoUseCre(coUseCre);
                        if (p.getEmailEmp() != null) {
                            this.getEnvioCorreoVoBo(p);
                        }
                    }
                }else{
                    logger.error("No se finalizó la ejecución de la solicitud rest debido a que no se logró establecer conexión con el servidor de correos.");       
                }
            }
        } catch (Exception e) {
            logger.error("Error al obtener la lista de personal pendiente de Visto Bueno");   
            e.printStackTrace();
        }
        return vReturn;
    }    
}
