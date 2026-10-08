/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.util;

import java.util.Properties;
import javax.mail.BodyPart;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.Session;
import javax.mail.Transport;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeBodyPart;
import javax.mail.internet.MimeMessage;
import javax.mail.internet.MimeMultipart;
import org.apache.log4j.Logger;
import pe.gob.onpe.sgdtask.bean.DocumentosVbBean;
import pe.gob.onpe.sgdtask.bean.EmailMensajeBean;
import pe.gob.onpe.sgdtask.bean.NotificacionLogBean;
import pe.gob.onpe.sgdtask.bean.PersonalVbBean;

/**
 *
 * @author ecueva
 */
public class MailUtility {

    private static boolean instanciated = false;
    private static Transport transport;
    private static Session session;
    private static MailUtility instancia;
    private static MailProperties mailProperties;
    private static Logger logger=Logger.getLogger(MailUtility.class);
    private MailUtility() {
    }

    public static MailUtility getInstancia() {
        if (!MailUtility.instanciated) {
            MailUtility.instancia = new MailUtility();
            MailUtility.instanciated = true;
        }
        return MailUtility.instancia;
    }

    public String connectToServerMail() throws Exception {
        String vReturn = "NO_OK";
        final String username = mailProperties.getUserName();
        String password = mailProperties.getUserPassword();
        password=Utility.getInstancia().descifrar(password,ConstantesSec.SGD_SECRET_KEY_PASSWORD);
        final String serverName = mailProperties.getServerIP();
        final String serverPort = mailProperties.getServerPort();
        final String smtp_auth = mailProperties.getSmtpAuth();
        final String smtp_starttls = mailProperties.getSmtpStartTLS();
        Properties props = new Properties();
        props.put("mail.smtp.auth", smtp_auth);//false
        props.put("mail.smtp.host", serverName);
        props.put("mail.smtp.port", serverPort);
        props.setProperty("mail.smtp.quitwait", "false");
        props.put("mail.smtp.starttls.enable", smtp_starttls);//true
        props.put("mail.smtp.ssl.trust", serverName);
        session = javax.mail.Session.getInstance(props);
        session.setDebug(true);//verificar si se comenta esta linea en produccion   
        try {
            transport = session.getTransport("smtp");
            transport.connect(serverName, Integer.parseInt(serverPort), username, password);
            System.out.println("Conectado correctamente al server correo");
            vReturn = "OK";
        } catch (NumberFormatException ex) {
            vReturn = ex.getMessage();
            logger.error("ERROR",ex);  
            ex.printStackTrace();            
        } catch (MessagingException ex) {
            vReturn = ex.getMessage();
            logger.error("ERROR",ex);  
            ex.printStackTrace();            
        }
        return vReturn;
    }

    public String sendMail(EmailMensajeBean mensaje) {
        String vReturn = "NO_OK";
        final String emailFrom = mailProperties.getEmailFrom();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mensaje.getDeCorreoDestino()));
            message.setSubject(mensaje.getDeTituloMensaje());
            message.setText(mensaje.getDeCuerpoMensaje());

            transport.sendMessage(message, message.getAllRecipients());
            vReturn = "OK";
        } catch (Exception ex) {
            vReturn = ex.getMessage();
            logger.error("ERROR",ex);  
            ex.printStackTrace();  
        }
        return vReturn;
    }

    public String sendMailHtml(EmailMensajeBean mensaje) {
        String vReturn = "NO_OK";
        final String emailFrom = mailProperties.getEmailFrom();
        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(emailFrom));
            message.setRecipients(Message.RecipientType.TO,
                    InternetAddress.parse(mensaje.getDeCorreoDestino()));
            message.setSubject(mensaje.getDeTituloMensaje());

            //Aqui contenido HTML
            MimeMultipart multipart = new MimeMultipart("related");

            // first part  (the html)
            BodyPart messageBodyPart = new MimeBodyPart();
            messageBodyPart.setContent(mensaje.getDeCuerpoMensaje(), "text/html; charset=utf-8");
            // add it
            multipart.addBodyPart(messageBodyPart);
            // put everything together
            message.setContent(multipart);

            //message.setText(mensaje.getDeCuerpoMensaje());

            transport.sendMessage(message, message.getAllRecipients());
            vReturn = "OK";
        } catch (Exception ex) {
            vReturn = ex.getMessage();
            logger.error("ERROR",ex);  
            ex.printStackTrace();  
        }
        return vReturn;
    }

    public String closeConectServerMail() {
        String vReturn = "NO_OK";
        try {
            if (transport != null) {
                transport.close();
            }
            vReturn = "OK";
        } catch (Exception ex) {
            vReturn = ex.getMessage();
            logger.error("ERROR",ex);  
            ex.printStackTrace();  
        }
        return vReturn;
    }

    public boolean isOpenConnectServerMail() {
        return transport.isConnected();
    }
    public static void setMailProperties(MailProperties mailProperties) {
        MailUtility.mailProperties = mailProperties;
    }
    public String getEmailHtml(PersonalVbBean personalVbBean) {
        StringBuilder mensajeHtml = new StringBuilder();
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif; background-color: #001e41;color:white;text-align:center;\"><h2>");
        mensajeHtml.append(personalVbBean.getNoEntidadNotifica());
        mensajeHtml.append(" - Sistema de Gesti&oacute;n Documental</h2></div>");
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif\">");
        mensajeHtml.append("<div><h3 style=\"color: #5f7a9c\">Estimado(a)&nbsp;");
        mensajeHtml.append(personalVbBean.getDeNombreCompleto());
        mensajeHtml.append("</h3></div>");
        mensajeHtml.append("<p>El Sistema de Gesti&oacute;n Documental le recuerda que tiene los siguientes documentos por Visar:</p>");
        
        for (DocumentosVbBean d : personalVbBean.getListaDocumentos()) {            
            mensajeHtml.append("<table width=\"100%\" style=\"font-family: Arial, Helvetica, sans-serif\"  cellpadding=\"10\" cellspacing=\"0\">");      
            mensajeHtml.append("<tr><td style=\"border: 1px solid #5f7a9c;background-color: #405571;color: white;font-size:16px;\">");
            mensajeHtml.append(d.getDeDependencia());
            mensajeHtml.append("</td></tr>");
            mensajeHtml.append("<tr><td style=\"border: 1px solid #5f7a9c;font-size: 14px;font-weight: bold;color: #5f7a9c;padding-left: 20px;\">");
            mensajeHtml.append(d.getTipoDocumento());
            mensajeHtml.append("---");
            mensajeHtml.append(d.getSiglasDocumento());
            mensajeHtml.append("/");
            mensajeHtml.append(personalVbBean.getDeSiglaEntidadNotifica());
            mensajeHtml.append("<br>");
            mensajeHtml.append("Fecha:&nbsp;");
            mensajeHtml.append(d.getFechaDocString());
            mensajeHtml.append("</td></tr>");
            mensajeHtml.append("<tr><td style=\"border: 1px solid #5f7a9c;font-size: 14px;padding-left: 20px; color:#6f6a6a;\"><span style=\"font-size: 14px;font-weight: bold;\">ASUNTO:&nbsp;</span>");
            mensajeHtml.append(d.getDeAsunto());
            mensajeHtml.append("</td></tr>");
            mensajeHtml.append("</table>");
            mensajeHtml.append("<br>");
        }               
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif; background-color: #bd6c15;color:white;text-align:left;height:20px;fotn-size:14;\">Sistema de Gesti&oacute;n Documental</div>");
        mensajeHtml.append("</div>");
        return mensajeHtml.toString();
    }
     public String getEmailLogHtml(NotificacionLogBean logBean) {
        StringBuilder mensajeHtml = new StringBuilder();
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif; background-color: #001e41;color:white;text-align:center;\"><h2>");
        mensajeHtml.append(logBean.getNoEntidadNotifica());
        mensajeHtml.append(" - Sistema de Gesti&oacute;n Documental</h2></div>");
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif\">");
        mensajeHtml.append("<div><h3 style=\"color: #5f7a9c\">Estimado(a)&nbsp;");
        mensajeHtml.append(logBean.getNombreEmpleado());
        mensajeHtml.append("</h3></div>");
        mensajeHtml.append("<p>"+logBean.getCuerpo()+":</p>");
                    
            mensajeHtml.append("<table width=\"100%\" style=\"font-family: Arial, Helvetica, sans-serif\"  cellpadding=\"10\" cellspacing=\"0\">");                  
            mensajeHtml.append("<tr><td style=\"border: 1px solid #5f7a9c;font-size: 14px;font-weight: bold;color: #5f7a9c;padding-left: 20px;\">");
            mensajeHtml.append(logBean.getDetalle());                       
            mensajeHtml.append("</td></tr>");           
            mensajeHtml.append("</table>");
            mensajeHtml.append("<br>");
                      
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif; background-color: #bd6c15;color:white;text-align:left;height:20px;fotn-size:14;\">Sistema de Gesti&oacute;n Documental</div>");
        mensajeHtml.append("</div>");
        return mensajeHtml.toString();
    }
    
}
