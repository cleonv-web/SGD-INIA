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
import pe.gob.onpe.sgdtask.bean.AsignacionFuncionarioBean;
import pe.gob.onpe.sgdtask.bean.AsignacionRequest;
import pe.gob.onpe.sgdtask.response.AsignacionFuncionarioResponse;
import pe.gob.onpe.sgdtask.service.AsignacionFuncionarioService;

/**
 *
 * @author FSilva
 */
@Controller
public class AsignacionFuncionarioRestController {

    private static Logger logger = Logger.getLogger(AsignacionFuncionarioRestController.class);
    @Autowired
    AsignacionFuncionarioService asignacionFuncionarioService;

    @RequestMapping(value = "/asignaFuncionario", method = RequestMethod.POST, produces = "application/json; charset=utf-8")
    public @ResponseBody
    AsignacionFuncionarioResponse getAsignaFuncionario(@RequestBody AsignacionRequest data) {
        logger.warn("Accediendo a servicio rest: "+data.getCoAsignacion());
        AsignacionFuncionarioResponse response = new AsignacionFuncionarioResponse();

        //if (data != null) {
            String coAsignacion = data.getCoAsignacion();
            String coUseCre = data.getCoUseCre();
            long coAsignacionFuncionario=Long.parseLong(coAsignacion);
            if(coUseCre!=null && coAsignacionFuncionario>0 && coUseCre.trim().length()>0){
                AsignacionFuncionarioBean asignacionFuncionarioBean=asignacionFuncionarioService.getAsignacionFuncionario(coAsignacionFuncionario);
                logger.warn("Asignacion: "+asignacionFuncionarioBean);
                if(asignacionFuncionarioBean!=null && asignacionFuncionarioBean.getCoAsignacionFuncionario()>0){
                    try {
                        //Ejecutar el único registro para devolver a la vista
                        String msg=asignacionFuncionarioService.updAsignacionFuncionario(asignacionFuncionarioBean);
                        asignacionFuncionarioService.getIniciarTareaAsignacionFuncionario();
                        if(msg.equals("1")){
                            response.setCoRespuesta("01");
                            response.setDeRespuesta("Se realizó la asignación del empleado a dependencia.");
                        }else{
                            response.setCoRespuesta("02");
                            response.setDeRespuesta("No Se realizó la asignación del empleado a dependencia.");
                        }
                    } catch (Exception ex) {
                        response.setCoRespuesta("02");
                        response.setDeRespuesta("Error al intentar actualizar los datos.");
                        ex.printStackTrace();
                    }
                }else{
                    response.setCoRespuesta("02");
                    response.setDeRespuesta("Error al obtener los datos de la asignacion.");
                }
            }else{
                response.setCoRespuesta("02");
                response.setDeRespuesta("No estan completos los parametros.");
            }
//        } else {
//            response.setCoRespuesta("03");
//            response.setDeRespuesta("No se recibieron datos de la consulta.");
//        }
        return response;
    }
}
