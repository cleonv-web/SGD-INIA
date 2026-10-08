/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.io.IOException;
import java.util.Date;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.libreria.util.Mensaje;
import pe.gob.onpe.tramitedoc.bean.AsignacionFuncionarioBean;
import pe.gob.onpe.tramitedoc.dao.AsignacionFuncionarioDao;
import pe.gob.onpe.tramitedoc.service.AsignacionFuncionarioService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.RestUtility;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author FSilva
 */
@Service
public class AsignacionFuncionarioServiceImp implements AsignacionFuncionarioService {

    @Autowired
    ApplicationProperties applicationProperties;
    @Autowired
    AsignacionFuncionarioDao asignacionFuncionarioDao;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AsignacionFuncionarioServiceImp.class);
    @Override
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        List<AsignacionFuncionarioBean> lista = null;
        if (asignacionFuncionarioBean != null) {
            lista = asignacionFuncionarioDao.getListAsignacionFuncionario(asignacionFuncionarioBean);
        }
        return lista;
    }

    @Override
    public String[] insAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String msg[] = new String[3];
        if (asignacionFuncionarioBean != null) {
            //Comparar fechas
            Date hoy = new Date();
            String msgCompara =Utilidades.getInstancia().comparaFechaSinHora(asignacionFuncionarioBean.getFeInicio(),hoy );
//            if (!msgCompara.equals("1")) {
                if(asignacionFuncionarioBean.getCoTipoEncargo().equals("1")){
                    asignacionFuncionarioBean.setFeFin(null);
                }
                msg = asignacionFuncionarioDao.insAsignacionFuncionario(asignacionFuncionarioBean);
                
                if (msg[0].equals("01")) {
                    long coAsignacionFuncionario = Long.parseLong(msg[2]);
                    asignacionFuncionarioBean.setCoAsignacionFuncionario(coAsignacionFuncionario);
                    msgCompara = Utilidades.getInstancia().comparaFechaSinHora(asignacionFuncionarioBean.getFeInicio(),hoy);
                    //SI LA FECHA DE INICIO ES FECHA ACTUAL o MENOR LLAMAR AL REST
                    if (msgCompara.equals("0")||msgCompara.equals("1")) {
                        ObjectMapper mapper = new ObjectMapper();
                        String url = applicationProperties.getUrlSgdRestTask();                        
                        try {
                            String jsonRest = RestUtility.getInstancia().restAsignarFuncionario(asignacionFuncionarioBean, url);
                            Mensaje mensaje = mapper.readValue(jsonRest, Mensaje.class);
                            if (mensaje.getCoRespuesta().equals("01")) {
                                msg[1] = "Se registró y se actualizó la asignación del funcionario a la dependencia";
                                logger.info("SE ACTUALIZÓ EL SERVICIO REST");
                            }
                        } catch (IOException ex) {
                            msg[1] = "Se registró,pero esta pendiente la asignación del funcionario a la dependencia";
                            logger.info("SE ACTUALIZÓ EL SERVICIO REST");
                            ex.printStackTrace();
                        }catch(Exception ex){
                            msg[1] = "Se registró,pero esta pendiente la asignación del funcionario a la dependencia";
                            logger.info("SE ACTUALIZÓ EL SERVICIO REST");
                            ex.printStackTrace();
                        }
                    }
                }
//            }else{
//                msg[0]="00";
//                msg[1]="Fecha de inicio no puede ser menor que la fecha del sistema";
//            }

        }
        return msg;
    }

    @Override
    public AsignacionFuncionarioBean getFuncionarioPorDependencia(String coDependencia) {
        return asignacionFuncionarioDao.getFuncionarioPorDependencia(coDependencia);
    }

    @Override
    public int getExisteProgramacionEmpleado(String coEmpleado) {
        return asignacionFuncionarioDao.getExisteProgramacionEmpleado(coEmpleado);
    }

    @Override
    public int getExisteProgramacionDependencia(String coDependencia) {
        return asignacionFuncionarioDao.getExisteProgramacionDependencia(coDependencia);
    }

    @Override
    public String delAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        return asignacionFuncionarioDao.delAsignacionFuncionario(asignacionFuncionarioBean);
    }

    @Override
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionarioBean) {
        return asignacionFuncionarioDao.getAsignacionFuncionario(coAsignacionFuncionarioBean);
    }

    @Override
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String msg="0";
        if (asignacionFuncionarioBean != null) {
            //Comparar fechas
            Date hoy = new Date();
            String msgCompara="";// =Utilidades.getInstancia().comparaFechaSinHora(asignacionFuncionarioBean.getFeInicio(),hoy );
            //if (!msgCompara.equals("1")) {
                msg = asignacionFuncionarioDao.updAsignacionFuncionario(asignacionFuncionarioBean);
                //SI LA FECHA DE INICIO ES FECHA ACTUAL LLAMAR AL REST
                if (msg.equals("1")) {
                    msgCompara = Utilidades.getInstancia().comparaFechaSinHora(asignacionFuncionarioBean.getFeInicio(),hoy);
                    if (msgCompara.equals("0")||msgCompara.equals("1")) {
                        ObjectMapper mapper = new ObjectMapper();
                        String url = applicationProperties.getUrlSgdRestTask();
                        String jsonRest = RestUtility.getInstancia().restAsignarFuncionario(asignacionFuncionarioBean, url);
                        try {
                            Mensaje mensaje = mapper.readValue(jsonRest, Mensaje.class);
                            if (mensaje.getCoRespuesta().equals("01")) {
                                msg = "2";
                                logger.info("SE ACTUALIZÓ EL SERVICIO REST");
                            }
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }
                }
            //}else{
              //  msg="3";                
            //}
        }
        return msg;
    }
    
}
