/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.rest;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import pe.gob.onpe.sgdtask.bean.DocumentoVoBoRequest;
import pe.gob.onpe.sgdtask.service.NotificacionService;

/**
 *
 * @author FSilva
 */
@Controller
public class NotificacionVoBoRestController {

    private static Logger logger = Logger.getLogger(NotificacionVoBoRestController.class);
    @Autowired
    NotificacionService notificacionService;

    @RequestMapping(value = "/enviarMailVoBo", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody String getEnviarMailVoBo(@RequestBody DocumentoVoBoRequest data) {
        logger.warn("Accediendo a servicio rest enviarMailVoBo: nuann " + data.getNuAnn() + ",nuemi " + data.getNuEmi());

        //if (data != null) {
            String nuAnn = data.getNuAnn();
            String nuEmi = data.getNuEmi();
            String coUseCre = data.getCoUseCre();

            if (coUseCre != null && nuAnn != null && nuEmi != null && nuEmi.trim().length() > 0 && nuAnn.trim().length() == 4) {
                try {
                    //Ejecutar el único registro para devolver a la vista
                    String msg ="OK";// notificacionService.getEnviarMailVoBoPorDocumento(nuAnn, nuEmi, coUseCre);
                    if (msg.equals("OK")) {
                        logger.warn("Se realizó el envío de notificación correctamente");
                    } else {
                        logger.warn("Error al realizar el envío al correo");
                    }
                } catch (Exception ex) {
                    logger.warn("Excepción en el servicio rest de enviarMailVoBo");
                    ex.printStackTrace();
                }
            } else {
                logger.warn("Los datos de la petición al servicio estan incompletos");
            }
//        } else {
//            logger.warn("La petición al servicio se realizó con datos nulos");
//        }
        logger.warn("Finalizando acceso a servicio rest enviarMailVoBo: nuann " + data.getNuAnn() + ",nuemi " + data.getNuEmi());
        return "OK";
    }
}
