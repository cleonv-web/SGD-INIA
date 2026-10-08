/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

//import java.util.List;
//import java.io.IOException;
//import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
//import pe.gob.onpe.libreria.util.Mensaje;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
//import pe.gob.onpe.tramitedoc.bean.MessageMailBean;
//import pe.gob.onpe.tramitedoc.bean.NotificacionBean;
import pe.gob.onpe.tramitedoc.dao.NotificacionDao;
import pe.gob.onpe.tramitedoc.service.NotificacionService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.RestUtility;
//import pe.gob.onpe.tramitedoc.util.MailUtility_del;
//import pe.gob.onpe.tramitedoc.web.util.MailProperties_del;

/**
 *
 * @author ecueva
 */
@Service("notificacionService")
public class NotificacionServiceImp  implements NotificacionService {

    //@Autowired
    //private MailProperties_del mailProperties;     
    @Autowired
    ApplicationProperties applicationProperties;    
    
    @Autowired
    private NotificacionDao notificacionDao;     
    
    private DocumentoDatoBean getDatosDoc(String pnuAnn, String pnuEmi){
        DocumentoDatoBean docDatoBean=null;
        try{
           docDatoBean = notificacionDao.getDatosDoc(pnuAnn, pnuEmi);
        }catch(Exception e){
            docDatoBean=null;
        }
        return  docDatoBean;

    }

    @Override
    public String procesarNotificacion(String pnuAnn, String pnuEmi, String coUser) {
        //boolean bResult=true;
        String vResult="NO_OK";
        //INSERT O DELETE A LA TABLA DE NOTIFICACIONES --SE ENCUENTRA EN TRIGGER
       
        /*
        try {
            DocumentoDatoBean docDatoBean = this.getDatosDoc(pnuAnn, pnuEmi);
            //VERIFICAR SI EL ESTADO DEL DOCUMENTO ES PARA DESPACHO
            if(docDatoBean!=null&&docDatoBean.getEsDocEmi()!=null&&docDatoBean.getEsDocEmi().equals("7")){
                //CONECTAR CON EL SERVICIO REST PARA ENVIO DE CORREOS
                String url = applicationProperties.getUrlSgdRestTask();  
                //ObjectMapper mapper = new ObjectMapper();
                String jsonRest = RestUtility.getInstancia().restSendMailVistoBueno(pnuAnn, pnuEmi, coUser, url);
                //Mensaje mensaje = mapper.readValue(jsonRest, Mensaje.class);
                vResult="OK";
                //if (mensaje.getCoRespuesta().equals("01")) {
                //    vResult="OK";
                //}
                
                //OBTENER LISTA DE NOTIFICACIONES PENDIENTES A ENVIAR CORREO
                //List<NotificacionBean> lsNoti=this.getLsNotiPendienteEnvio(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                //if(lsNoti!=null&&!lsNoti.isEmpty()){
                    //ESTABLECER CONEXION CON SERVER DE CORREO
                    //if(MailUtility_del.getInstancia().connectToServerMail(mailProperties).equals("OK")){
                        //for (NotificacionBean noti : lsNoti) {
                            //VERIFICAR SERVER CORREO ESTA CONECTADO
                            //if(MailUtility_del.getInstancia().isOpenConnectServerMail()){
                                //ENVIAR CORREO
                                //if(MailUtility_del.getInstancia().sendMail(this.getMesageMail(noti.getToEmail(), docDatoBean)).equals("OK")){
                                //validar que personal tenga correo asignado
                                //if(noti.getToEmail()!=null&&noti.getToEmail().length()>0){
                                    //if(MailUtility_del.getInstancia().sendMailHtml(
                                    //        this.getMessageMailHtml(noti.getToEmail(), noti.getDeNom(), docDatoBean)).equals("OK")){
                                       //noti.setEstadoEnvio("1"); //correo enviado correctamente
                                       //ACTUALIZAR ESTADO DE LA NOTIFICACION EN TBL DE NOTIFICACIONES            
                                       //vResult=notificacionDao.updNotificacion(noti);
                                       //if(!vResult.equals("OK")){
                                           //bResult=false;
                                           //vResult="Error actualizando estado de notificación de Envio de Correo.";
                                           //break;
                                       //}
                                    //}
                                //}
                            //}
                        //}
                    //}
                    //CERRAR CONEXION CON SERVER DE CORREO
                    //MailUtility_del.getInstancia().closeConectServerMail();
                //}
            }else{
                vResult="OK";
            }
        } catch (Exception e) {
            vResult="Error enviando correos.";
            e.printStackTrace();
        }
        //if(bResult){
            //vResult="OK";
        //}
        
        */
        return vResult;
    }    
    
    /*private List<NotificacionBean> getLsNotiPendienteEnvio(String nuAnn, String nuEmi){
        List<NotificacionBean> lsNoti=null;
        try {
            lsNoti=notificacionDao.getLsNotiPendienteEnvio(nuAnn, nuEmi);
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return lsNoti;
    }    
    
    private MessageMailBean getMesageMail(String toEmail, DocumentoDatoBean doc){
        MessageMailBean msgMail = new MessageMailBean();
        msgMail.setDeMailDestino(toEmail);
        msgMail.setDeTitulo("Notificación de documento para visto bueno.");
        msgMail.setDeContenido(doc.getTipoDoc()+" ------- "+doc.getSiglasDoc()+"/ONPE"
                            + "\nFecha : "+doc.getFechaDoc()
                            + "\nAsunto: "+doc.getDeAsu());
        return msgMail;
    }
    
    private MessageMailBean getMessageMailHtml(String toEmail, String deNom, DocumentoDatoBean doc){
        MessageMailBean msgMail = new MessageMailBean();
        msgMail.setDeMailDestino(toEmail);
        msgMail.setDeTitulo("Notificación de documento para visto bueno.");
        StringBuilder mensajeHtml = new StringBuilder();
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif; background-color: #001e41;color:white;text-align:center;\"><h2> ONPE - Sistema de Gesti&oacute;n Documental</h2></div>");
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif\">");
        mensajeHtml.append("<div><h3 style=\"color: #5f7a9c\">Estimado(a)&nbsp;");
        mensajeHtml.append(deNom);
        mensajeHtml.append("</h3></div>");
        mensajeHtml.append("<p>El Sistema de Gesti&oacute;n Documental le recuerda que tienes los siguienes documentos por Visar:</p>");
        
        mensajeHtml.append("<table width=\"100%\" style=\"font-family: Arial, Helvetica, sans-serif\"  cellpadding=\"10\" cellspacing=\"0\">");      
        mensajeHtml.append("<tr><td style=\"border: 1px solid #5f7a9c;background-color: #405571;color: white;font-size:16px;\">");
        mensajeHtml.append(doc.getDeDepEmi());
        mensajeHtml.append("</td></tr>");
        mensajeHtml.append("<tr><td style=\"border: 1px solid #5f7a9c;font-size: 14px;font-weight: bold;color: #5f7a9c;padding-left: 20px;\">");
        mensajeHtml.append(doc.getTipoDoc());
        mensajeHtml.append("---");
        mensajeHtml.append(doc.getSiglasDoc());
        mensajeHtml.append("/ONPE<br>");
        mensajeHtml.append("Fecha:&nbsp;");
        mensajeHtml.append(doc.getFechaDoc());
        mensajeHtml.append("</td></tr>");
        mensajeHtml.append("<tr><td style=\"border: 1px solid #5f7a9c;font-size: 14px;padding-left: 20px; color:#6f6a6a;\"><span style=\"font-size: 14px;font-weight: bold;\">ASUNTO:&nbsp;</span>");
        mensajeHtml.append(doc.getDeAsu());
        mensajeHtml.append("</td></tr>");
        mensajeHtml.append("</table>");
        mensajeHtml.append("<br>");        
//        mensajeHtml.append("<table width=\"100%\" style=\"font-family: Arial, Helvetica, sans-serif\"  cellpadding=\"5\" cellspacing=\"0\">");
//        mensajeHtml.append("<tr style=\"background-color: #001e41;color: white;\">"
//                + "                <td style=\"border: 1px solid #5f7a9c; width: 25%;\">Documento</td>\n"
//                + "                <td style=\"border: 1px solid #5f7a9c; width: 15%;\">Fecha</td>\n"
//                + "                <td style=\"border: 1px solid #5f7a9c; width: 60%;\">Asunto</td>\n"
//                + "            </tr>");    
//        
//        mensajeHtml.append("<tr>");
//        mensajeHtml.append("<td style=\"border: 1px solid #5f7a9c;\">");
//        mensajeHtml.append(doc.getTipoDoc());
//        mensajeHtml.append("---");
//        mensajeHtml.append(doc.getSiglasDoc());
//        mensajeHtml.append("/ONPE");
//        mensajeHtml.append("</td>");
//        mensajeHtml.append("<td style=\"border: 1px solid #5f7a9c;\">");
//        mensajeHtml.append(doc.getFechaDoc());
//        mensajeHtml.append("</td>");
//        mensajeHtml.append("<td style=\"border: 1px solid #5f7a9c;\">");
//        mensajeHtml.append(doc.getDeAsu());
//        mensajeHtml.append("</td>");
//        mensajeHtml.append("</tr>");
//        
//        mensajeHtml.append("</table>");
        mensajeHtml.append("<div style=\"font-family: Arial, Helvetica, sans-serif; background-color: #bd6c15;color:white;text-align:left;height:20px;fotn-size:14;\">Sistema de Gesti&oacute;n Documental</div>");
        mensajeHtml.append("</div>");
        msgMail.setDeContenido(mensajeHtml.toString());
        return msgMail;
    }*/
    public String notificarVistoBueno(String nuEmi,String nuAnn){
       String result;
        try{
           result = notificacionDao.notificarVistoBueno(nuEmi, nuAnn);
        }catch(Exception e){
            result=null;
        }
        return  result;
     }
     
            
            
     public String getLsEmpleadoNotificar(String nuEmi,String nuAnn, String detalleDoc){
       String result;
        try{
           result = notificacionDao.getLsEmpleadoNotificar(nuEmi, nuAnn,detalleDoc);
        }catch(Exception e){
            result=null;
        }
        return  result;
     }
     
      public String getLsMesaVirtualNotificar(String nuEmi,String nuAnn, String detalleDoc){
       String result;
        try{
           result = notificacionDao.getLsMesaVirtualNotificar(nuEmi, nuAnn,detalleDoc);
        }catch(Exception e){
            result=null;
        }
        return  result;
     }
      
       public String getLsTupaNotificar(String nuEmi,String nuAnn, String detalleDoc){
       String result;
        try{
           result = notificacionDao.getLsTupaNotificar(nuEmi, nuAnn,detalleDoc);
        }catch(Exception e){
            result=null;
        }
        return  result;
     }
     
}
