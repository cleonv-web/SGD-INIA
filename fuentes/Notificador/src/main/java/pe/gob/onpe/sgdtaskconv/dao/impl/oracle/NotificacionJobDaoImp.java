/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtaskconv.dao.impl.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sgdtask.bean.NotificacionJobBean;
import pe.gob.onpe.sgdtask.bean.NotificacionJobDetBean;
import pe.gob.onpe.sgdtask.bean.NotificacionLogBean;
import pe.gob.onpe.sgdtask.dao.NotificacionJobDao;
import pe.gob.onpe.sgdtask.dao.SimpleJdbcDaoBase;

/**
 *
 * @author FSilva
 */
@Repository
public class NotificacionJobDaoImp extends SimpleJdbcDaoBase implements NotificacionJobDao {

    @Override
    public String[] insNotificacionJob(NotificacionJobBean notificacionJobBean) {
        String msg[] = new String[3];
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlSeq = new StringBuilder();
        //Get sequence
        sqlSeq.append("SELECT TDTC_NOTIFICACION_JOB_CO_NOTIF.NEXTVAL FROM DUAL");
        sql.append("INSERT INTO TDTC_NOTIFICACION_JOB("
                + "    CO_NOTIFICACION_JOB,"
                + "    CO_EMP,"
                + "    FE_ENVIO,"
                + "    EMAIL_EMP,"
                + "    CO_ESTADO,CO_USE_CRE) "
                + "   VALUES(?,?,SYSDATE,?,?,?)");
        try {
            long coNotificacionJob = this.jdbcTemplate.queryForObject(sqlSeq.toString(), Long.class);
            notificacionJobBean.setCoNotificacionJob(coNotificacionJob);
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                notificacionJobBean.getCoNotificacionJob(),
                notificacionJobBean.getCoEmp(),
                notificacionJobBean.getEmailEmp(),
                notificacionJobBean.getCoEstado(),
                notificacionJobBean.getCoUseCre()
            });
            msg[0] = "01";
            msg[1] = ""+notificacionJobBean.getCoNotificacionJob();
            msg[2] = "Registrado Correctamente";
        }catch(DuplicateKeyException dep){
            msg[0] = "00";
            msg[1] = "El identificador de la table es duplicado";
        } catch (Exception e) {
            e.printStackTrace();
            msg[0] = "00";
            msg[1] = "Error en el registro";
        }
        return msg;
    }

    @Override
    public String insNotificacionJobDet(NotificacionJobDetBean notificacionJobDetBean) {
        String msg="99";
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlSeq = new StringBuilder();
        //Get sequence
        sqlSeq.append("SELECT TDTD_NOTIFICACION_JOB_DET_CO_N.NEXTVAL FROM DUAL");
        sql.append("INSERT INTO TDTD_NOTIFICACION_JOB_DET("
                + "    CO_NOTIFICACION_JOB_DET,"
                + "    CO_EMP_VB,"
                + "    NU_ANN,"
                + "    NU_EMI,"
                + "    CO_NOTIFICACION_JOB,"
                + "    CO_ESTADO ) "
                + "   VALUES(?,?,?,?,?,?)");
        try {
            long coNotificacionJobDet = this.jdbcTemplate.queryForObject(sqlSeq.toString(), Long.class);
            notificacionJobDetBean.setCoNotificacionJobDet(coNotificacionJobDet);
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                notificacionJobDetBean.getCoNotificacionJobDet(),
                notificacionJobDetBean.getCoEmpVb(),
                notificacionJobDetBean.getNuAnn(),
                notificacionJobDetBean.getNuEmi(),
                notificacionJobDetBean.getCoNotificacionJob(),
                notificacionJobDetBean.getCoEstado()
            });
            msg = "01";            
        }catch(DuplicateKeyException dep){
            msg = "00";            
        } catch (Exception e) {
            e.printStackTrace();
            msg = "00";
        }
        return msg;
    }

      @Override
    public List<NotificacionLogBean> getListNotificacionLog(String tipo_envio) {
       StringBuilder query =new StringBuilder();
        
        query.append("SELECT CODIGO,PARA\n" +
        "      ,CONTENIDO\n" + 
        "      ,ASUNTO\n" +
        "      ,PARA_NOMBRES\n" +
        "      ,DETALLE\n" +
        "      ,CUERPO\n" +
        "  FROM IDOSGD.TDTC_NOTIFICACION_LOG \n" +
        "  WHERE TIPO_ENVIO='"+tipo_envio+"' AND ESTADO='0' AND INTENTOS IN (0,1,2) AND ROWNUM<20  ");         
        List<NotificacionLogBean> lista = null;      
        
        
        
        lista = this.namedParameterJdbcTemplate.query(query.toString(),new ParameterizedRowMapper<NotificacionLogBean>() {
            public NotificacionLogBean mapRow(ResultSet rs, int i) throws SQLException{
                NotificacionLogBean notifiBean = new NotificacionLogBean();
                notifiBean.setCodigo(rs.getString("CODIGO"));
                notifiBean.setParaEmail(rs.getString("PARA"));
                notifiBean.setContenido(rs.getString("CONTENIDO"));
                notifiBean.setAsunto(rs.getString("ASUNTO"));
                notifiBean.setNombreEmpleado(rs.getString("PARA_NOMBRES"));
                notifiBean.setDetalle(rs.getString("DETALLE"));
                notifiBean.setCuerpo(rs.getString("CUERPO"));
                return notifiBean;
            }
        }); 
        return lista;
    }
    @Override
    public String updNotificacionLog(NotificacionLogBean notificacionJobDetBean) {
        String msg="99";
        StringBuilder sql = new StringBuilder();        
        sql.append("UPDATE IDOSGD.TDTC_NOTIFICACION_LOG SET  ESTADO=?, INTENTOS=INTENTOS+1, CONTENIDO=? ,msj_error=cast(? as varchar(2000))  WHERE CODIGO=?");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{             
                notificacionJobDetBean.getCoEstadoEnvio(),
                notificacionJobDetBean.getContenido(), 
                notificacionJobDetBean.getError(), 
                notificacionJobDetBean.getCodigo()
            });
            msg = "01";            
        }catch(DuplicateKeyException dep){
            msg = "00";            
        } catch (Exception e) {
            e.printStackTrace();
            msg = "00";
        }
        return msg;
    }
    
}
