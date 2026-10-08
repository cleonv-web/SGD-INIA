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
import pe.gob.onpe.sgdtask.service.AsignacionFuncionarioService;
import pe.gob.onpe.sgdtask.util.Utility;
import pe.gob.onpe.sgdtask.util.ValidarTransaccionException;

/**
 *
 * @author FSilva
 */
@Service
public class AsignacionFuncionarioServiceImp implements AsignacionFuncionarioService {

    @Autowired
    AsignacionFuncionarioDao asignacionFuncionarioDao;
    private static org.apache.log4j.Logger logger = org.apache.log4j.Logger.getLogger(AsignacionFuncionarioServiceImp.class);

    @Scheduled(cron = "${timer.as_funcionario}")
    public void iniciaAsignacionFuncionarios() {
        List<AsignacionFuncionarioBean> lista = this.getListAsignacionFuncionario();
        logger.warn("Inicio de tarea Asginacion de funcionario");
        if (!lista.isEmpty()) {
            for (AsignacionFuncionarioBean a : lista) {
                try {
                    Date hoy = new Date();
                    String msgCompara = Utility.getInstancia().comparaFechaSinHora(a.getFeInicio(),hoy);
                    //Si la fecha es menor o igual
                    if (msgCompara.equals("0") || msgCompara.equals("1")) {
                        String msgDao = this.updAsignacionFuncionario(a);
                        //System.out.println(msgDao);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                    //System.out.println("Error");
                }
            }
        }
        logger.warn("Fin de tarea Asginacion de funcionario");
    }

    @Override
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario() {
        return asignacionFuncionarioDao.getListAsignacionFuncionario();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) throws Exception {
        String msg = "0";
        msg = asignacionFuncionarioDao.updAsignacionFuncionarioDependencia(asignacionFuncionarioBean);
        if (!msg.equals("1")) {
            logger.error("Error actualizando RHTM_DEPENDENCIA con los valores de la asignación");
            throw new ValidarTransaccionException("Error actualizando RHTM_DEPENDENCIA con los valores de la asignación "+asignacionFuncionarioBean.getCoAsignacionFuncionario());
        }
        if (asignacionFuncionarioBean.getCoTipoEncargo().equals("1")) {
            msg = asignacionFuncionarioDao.updEmpleadoTitular(asignacionFuncionarioBean);
            if (!msg.equals("1")) {
                logger.error("Error actualizando RHTM_DEPENDENCIA con los valores del nuevo titular");
                throw new ValidarTransaccionException("Error actualizando RHTM_DEPENDENCIA con los valores del nuevo titular "+asignacionFuncionarioBean.getCoAsignacionFuncionario());
            }
        }
        if (msg.equals("1")) {
            asignacionFuncionarioBean.setInAsignacion("1");
            asignacionFuncionarioDao.updAsignacionFuncionario(asignacionFuncionarioBean);
            if (!msg.equals("1")) {
                logger.error("Error actualizando TDTV_ASIGNACION_FUNCIONARIO con los valores del estado a ASIGNADO");
                throw new ValidarTransaccionException("Error actualizando TDTV_ASIGNACION_FUNCIONARIO con los valores del estado a ASIGNADO "+asignacionFuncionarioBean.getCoAsignacionFuncionario());
            }
        }
        return msg;
    }

    @Override
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionario) {
        return asignacionFuncionarioDao.getAsignacionFuncionario(coAsignacionFuncionario);
    }

    @Override
    public void getIniciarTareaAsignacionFuncionario() {
        iniciaAsignacionFuncionarios();
    }
    
}
