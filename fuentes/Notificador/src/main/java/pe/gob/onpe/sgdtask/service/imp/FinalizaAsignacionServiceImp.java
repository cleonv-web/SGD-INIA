/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.service.imp;

import java.util.Date;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.sgdtask.bean.AsignacionFuncionarioBean;
import pe.gob.onpe.sgdtask.dao.AsignacionFuncionarioDao;
import pe.gob.onpe.sgdtask.util.Utility;
import pe.gob.onpe.sgdtask.util.ValidarTransaccionException;

/**
 *
 * @author FSilva
 */
@Service
public class FinalizaAsignacionServiceImp{

    @Autowired
    AsignacionFuncionarioDao asignacionFuncionarioDao;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(FinalizaAsignacionServiceImp.class);

    @Scheduled(cron = "${timer.fin_funcionario}")
    public void iniciaAsignacionFuncionarios() {
        logger.warn("Inicio de tarea Finalizar Asginacion de funcionario");        
        List<AsignacionFuncionarioBean> lista = this.getListAsignacionFinaliza();
        if (!lista.isEmpty()) {
            for (AsignacionFuncionarioBean a : lista) {
                try {
                    Date hoy=new Date();
                    String msgCompara=Utility.getInstancia().comparaFechaSinHora(a.getFeFin(),hoy );
                    if(msgCompara.equals("1")){
                        String msgDao = this.updFinalizaAsignacionFuncionario(a);
                        //System.out.println(msgDao);
                    }                    
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //System.out.println("Error");
                }
//            System.out.println("Nro"+i);
//            System.out.println("Codigo"+a.getCoAsignacionFuncionario());
            }
        }
        logger.warn("Fin de tarea Finalizar Asginacion de funcionario");
    }
    public List<AsignacionFuncionarioBean> getListAsignacionFinaliza() {
        return asignacionFuncionarioDao.getListAsignacionFinaliza();
    }
    
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionario) {
        return asignacionFuncionarioDao.getAsignacionFuncionario(coAsignacionFuncionario);
    }
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updFinalizaAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) throws Exception {
        String msg = "0";
        msg = asignacionFuncionarioDao.updRestableceEmpleadoTitular(asignacionFuncionarioBean);
        if (!msg.equals("1")) {
            logger.error("Error actualizando RHTM_DEPENDENCIA con los valores del empleado titular "+asignacionFuncionarioBean.getCoAsignacionFuncionario());
            throw new ValidarTransaccionException("Error actualizando RHTM_DEPENDENCIA con los valores de la asignación");
        }        
        if (msg.equals("1")) {
            asignacionFuncionarioBean.setInAsignacion("2");
            asignacionFuncionarioDao.updAsignacionFuncionario(asignacionFuncionarioBean);
            if (!msg.equals("1")) {
                logger.error("Error actualizando TDTV_ASIGNACION_FUNCIONARIO con los valores del estado a FINALIZADO "+asignacionFuncionarioBean.getCoAsignacionFuncionario());
                throw new ValidarTransaccionException("Error actualizando TDTV_ASIGNACION_FUNCIONARIO con los valores del estado a ASIGNADO");
            }
        }
        return msg;
    }
}
