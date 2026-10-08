/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtaskconv.dao.impl.sqlserver;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.PreparedStatementCreator;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.jdbc.support.KeyHolder;
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
    public String []insNotificacionJob(NotificacionJobBean notificacionJobBean) {
        final NotificacionJobBean objNotificacionJob=notificacionJobBean;
        String []msg = new String[3];
        final StringBuilder sql = new StringBuilder();
        //Get sequence
        sql.append("INSERT INTO IDOSGD.TDTC_NOTIFICACION_JOB("
                + "    CO_EMP,"
                + "    FE_ENVIO,"
                + "    EMAIL_EMP,"
                + "    CO_ESTADO,CO_USE_CRE) "
                + "   VALUES(?,GETDATE(),?,?,?)");
        try {
            KeyHolder keyHolder = new GeneratedKeyHolder();
            this.jdbcTemplate.update(new PreparedStatementCreator() {
                public PreparedStatement createPreparedStatement(Connection connection) throws SQLException {
                    PreparedStatement ps= connection.prepareStatement(sql.toString(), new String[]{"CO_NOTIFICACION_JOB"});
                    ps.setString(1, objNotificacionJob.getCoEmp());
                    ps.setString(2, objNotificacionJob.getEmailEmp());
                    ps.setString(3, objNotificacionJob.getCoEstado());
                    ps.setString(4, objNotificacionJob.getCoUseCre());
                    return ps;
                }
            },keyHolder);            
            msg[0] = "01";
            msg[1] = ""+keyHolder.getKey().longValue();
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
        sql.append("INSERT INTO IDOSGD.TDTD_NOTIFICACION_JOB_DET("
                + "    CO_EMP_VB,"
                + "    NU_ANN,"
                + "    NU_EMI,"
                + "    CO_NOTIFICACION_JOB,"
                + "    CO_ESTADO ) "
                + "   VALUES(?,?,?,?,?)");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{             
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
        StringBuilder query = new StringBuilder();
        Map<String, Object> objectParam = new HashMap<String, Object>(); 
        query.append("SELECT TOP 20 CODIGO,PARA\n" +
        "      ,CONTENIDO\n" + 
        "      ,ASUNTO\n" +
        "      ,PARA_NOMBRES\n" +
        "      ,DETALLE\n" +
        "      ,CUERPO\n" +
        "  FROM IDOSGD.TDTC_NOTIFICACION_LOG \n" +
        "  WHERE TIPO_ENVIO='"+tipo_envio+"' AND ESTADO='0' AND INTENTOS IN (0,1,2)   ");         
        List<NotificacionLogBean> lista = null;

        lista = this.namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<NotificacionLogBean>() {
            @Override
            public NotificacionLogBean mapRow(ResultSet rs, int i) throws SQLException {
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
        sql.append("UPDATE IDOSGD.TDTC_NOTIFICACION_LOG SET  ESTADO=?, INTENTOS=INTENTOS+1, CONTENIDO=? ,msj_error=cast(? as varchar(4000))  WHERE CODIGO=?");
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
