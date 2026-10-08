/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtaskconv.dao.impl.postgresql;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sgdtask.bean.AsignacionFuncionarioBean;
import pe.gob.onpe.sgdtask.dao.AsignacionFuncionarioDao;
import pe.gob.onpe.sgdtask.dao.SimpleJdbcDaoBase;

/**
 *
 * @author FSilva
 */
@Repository
public class AsignacionFuncionarioDaoImp extends SimpleJdbcDaoBase implements AsignacionFuncionarioDao {
 
    @Override
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario() {        
        StringBuilder query =new StringBuilder();
        query.append("SELECT ASF.CO_ASIGNACION_FUNCIONARIO, \n"
                + "	ASF.CO_DEPENDENCIA, \n"
                + "	ASF.DE_DOC_ASIGNA, \n"
                + "	ASF.CO_EMPLEADO, \n"
                + "	ASF.FE_INICIO, \n"
                + "	ASF.FE_FIN, \n"
                + "	ASF.CO_TIPO_ENCARGO, \n"
                + "	ASF.CO_USE_CRE, \n"
                + "	ASF.FE_USE_CRE, \n"
                + "	ASF.CO_USE_MOD, \n"
                + "	ASF.FE_USE_MOD, \n"
                + "	ASF.CO_ESTADO, \n"
                + "	ASF.IN_ASIGNACION\n"
                +"      FROM IDOSGD.TDTV_ASIGNACION_FUNCIONARIO ASF "
                + "     WHERE ASF.CO_ESTADO='1' AND IN_ASIGNACION='0'");               
        query.append(" ORDER BY ASF.FE_USE_CRE");
        List<AsignacionFuncionarioBean> lista=null;
        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
                public AsignacionFuncionarioBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
                    asignacionFuncionarioBean.setCoAsignacionFuncionario(rs.getLong("CO_ASIGNACION_FUNCIONARIO"));
                    asignacionFuncionarioBean.setCoDependencia(rs.getString("CO_DEPENDENCIA"));
                    asignacionFuncionarioBean.setCoEmpleado(rs.getString("CO_EMPLEADO"));
                    asignacionFuncionarioBean.setCoEstado(rs.getString("CO_ESTADO"));
                    asignacionFuncionarioBean.setCoTipoEncargo(rs.getString("CO_TIPO_ENCARGO"));
                    asignacionFuncionarioBean.setCoUseCre(rs.getString("CO_USE_CRE"));
                    asignacionFuncionarioBean.setCoUseMod(rs.getString("CO_USE_MOD"));
                    asignacionFuncionarioBean.setFeUseCre(rs.getDate("FE_USE_CRE"));
                    asignacionFuncionarioBean.setFeFin(rs.getDate("FE_FIN"));
                    asignacionFuncionarioBean.setFeInicio(rs.getDate("FE_INICIO"));                    
                    asignacionFuncionarioBean.setInAsignacion(rs.getString("IN_ASIGNACION"));
                    return asignacionFuncionarioBean;
                }
            });                
        } catch (EmptyResultDataAccessException e) {           
            e.printStackTrace();
        } catch (Exception e) {            
            e.printStackTrace();
        }
        //return lista;        
        return lista;
    }

    @Override
    public String updAsignacionFuncionarioDependencia(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String vReturn = "0";
        String sql = "UPDATE IDOSGD.RHTM_DEPENDENCIA \n"
                + "SET CO_EMPLEADO = ?,\n"
                + " CO_TIPO_ENCARGATURA=? \n"                
                + "WHERE CO_DEPENDENCIA = ?";
        try {
            long nRows = jdbcTemplate.update(sql,
                    new Object[]{
                        asignacionFuncionarioBean.getCoEmpleado(),
                        asignacionFuncionarioBean.getCoTipoEncargo(),
                        asignacionFuncionarioBean.getCoDependencia()
                    });
            if (nRows > 0) {
                vReturn = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;
    }
    @Override
    public String updEmpleadoTitular(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String vReturn = "0";
        String sql = "UPDATE IDOSGD.RHTM_DEPENDENCIA \n"
                + "SET CO_EMP_TITULAR=?, \n"
                + "TI_ENC_TITULAR=?\n"
                + "WHERE CO_DEPENDENCIA = ?";
        try {
            long nRows = jdbcTemplate.update(sql,
                    new Object[]{
                        asignacionFuncionarioBean.getCoEmpleado(),
                        asignacionFuncionarioBean.getCoTipoEncargo(),
                        asignacionFuncionarioBean.getCoDependencia()
                    });
            if (nRows > 0) {
                vReturn = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;
    }
    @Override
    public AsignacionFuncionarioBean getAsignacionFuncionario(long coAsignacionFuncionarioBean) {
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pcoAsignacion", coAsignacionFuncionarioBean);
        AsignacionFuncionarioBean asignacionFuncionarioBean=null;
        StringBuilder query =new StringBuilder();
        query.append("SELECT ASF.CO_ASIGNACION_FUNCIONARIO, \n"
                + "	ASF.CO_DEPENDENCIA, \n"
                + "	ASF.DE_DOC_ASIGNA, \n"
                + "	ASF.CO_EMPLEADO, \n"
                + "	ASF.FE_INICIO, \n"
                + "	ASF.FE_FIN, \n"
                + "	ASF.CO_TIPO_ENCARGO, \n"
                + "	ASF.CO_USE_CRE, \n"
                + "	ASF.FE_USE_CRE, \n"
                + "	ASF.CO_USE_MOD, \n"
                + "	ASF.FE_USE_MOD, \n"
                + "	ASF.CO_ESTADO, \n"
                + "	ASF.IN_ASIGNACION\n"
                +"      FROM IDOSGD.TDTV_ASIGNACION_FUNCIONARIO ASF "
                + "     WHERE ASF.CO_ESTADO='1' AND IN_ASIGNACION='0' AND CO_ASIGNACION_FUNCIONARIO=:pcoAsignacion" );               
        query.append(" ORDER BY ASF.FE_USE_CRE");
        List<AsignacionFuncionarioBean> lista=null;
        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), objectParam,new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
                public AsignacionFuncionarioBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
                    asignacionFuncionarioBean.setCoAsignacionFuncionario(rs.getLong("CO_ASIGNACION_FUNCIONARIO"));
                    asignacionFuncionarioBean.setCoDependencia(rs.getString("CO_DEPENDENCIA"));
                    asignacionFuncionarioBean.setCoEmpleado(rs.getString("CO_EMPLEADO"));
                    asignacionFuncionarioBean.setCoEstado(rs.getString("CO_ESTADO"));
                    asignacionFuncionarioBean.setCoTipoEncargo(rs.getString("CO_TIPO_ENCARGO"));
                    asignacionFuncionarioBean.setCoUseCre(rs.getString("CO_USE_CRE"));
                    asignacionFuncionarioBean.setCoUseMod(rs.getString("CO_USE_MOD"));
                    asignacionFuncionarioBean.setFeUseCre(rs.getDate("FE_USE_CRE"));
                    asignacionFuncionarioBean.setFeFin(rs.getDate("FE_FIN"));
                    asignacionFuncionarioBean.setFeInicio(rs.getDate("FE_INICIO"));                    
                    asignacionFuncionarioBean.setInAsignacion(rs.getString("IN_ASIGNACION"));
                    return asignacionFuncionarioBean;
                }
            });              
            if(!lista.isEmpty()){
                asignacionFuncionarioBean=lista.get(0);
            }
        } catch (EmptyResultDataAccessException e) {           
            e.printStackTrace();
        } catch (Exception e) {            
            e.printStackTrace();
        }        
        return asignacionFuncionarioBean;
    }
    @Override
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String vReturn = "0";
        String sql = "UPDATE IDOSGD.TDTV_ASIGNACION_FUNCIONARIO \n"
                + "SET IN_ASIGNACION = ?,\n"
                + " CO_USE_MOD='NOTIFICADOR',"
                + " FE_USE_MOD=CURRENT_TIMESTAMP\n"
                + "WHERE CO_ASIGNACION_FUNCIONARIO = ?";
        try {
            long nRows = jdbcTemplate.update(sql,
                    new Object[]{
                        asignacionFuncionarioBean.getInAsignacion(),
                        asignacionFuncionarioBean.getCoAsignacionFuncionario()
                    });
            if (nRows > 0) {
                vReturn = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;
    }

    @Override
    public List<AsignacionFuncionarioBean> getListAsignacionFinaliza() {
        StringBuilder query =new StringBuilder();
        query.append("SELECT ASF.CO_ASIGNACION_FUNCIONARIO, \n"
                + "	ASF.CO_DEPENDENCIA, \n"
                + "	ASF.DE_DOC_ASIGNA, \n"
                + "	ASF.CO_EMPLEADO, \n"
                + "	ASF.FE_INICIO, \n"
                + "	ASF.FE_FIN, \n"
                + "	ASF.CO_TIPO_ENCARGO, \n"
                + "	ASF.CO_USE_CRE, \n"
                + "	ASF.FE_USE_CRE, \n"
                + "	ASF.CO_USE_MOD, \n"
                + "	ASF.FE_USE_MOD, \n"
                + "	ASF.CO_ESTADO, \n"
                + "	ASF.IN_ASIGNACION\n"
                +"      FROM IDOSGD.TDTV_ASIGNACION_FUNCIONARIO ASF "
                + "     WHERE ASF.CO_ESTADO='1' AND IN_ASIGNACION='1' AND CO_TIPO_ENCARGO!='1'");               
        query.append(" ORDER BY ASF.FE_USE_CRE");
        List<AsignacionFuncionarioBean> lista=null;
        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
                public AsignacionFuncionarioBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
                    asignacionFuncionarioBean.setCoAsignacionFuncionario(rs.getLong("CO_ASIGNACION_FUNCIONARIO"));
                    asignacionFuncionarioBean.setCoDependencia(rs.getString("CO_DEPENDENCIA"));
                    asignacionFuncionarioBean.setCoEmpleado(rs.getString("CO_EMPLEADO"));
                    asignacionFuncionarioBean.setCoEstado(rs.getString("CO_ESTADO"));
                    asignacionFuncionarioBean.setCoTipoEncargo(rs.getString("CO_TIPO_ENCARGO"));
                    asignacionFuncionarioBean.setCoUseCre(rs.getString("CO_USE_CRE"));
                    asignacionFuncionarioBean.setCoUseMod(rs.getString("CO_USE_MOD"));
                    asignacionFuncionarioBean.setFeUseCre(rs.getDate("FE_USE_CRE"));
                    asignacionFuncionarioBean.setFeFin(rs.getDate("FE_FIN"));
                    asignacionFuncionarioBean.setFeInicio(rs.getDate("FE_INICIO"));                    
                    asignacionFuncionarioBean.setInAsignacion(rs.getString("IN_ASIGNACION"));
                    return asignacionFuncionarioBean;
                }
            });                
        } catch (EmptyResultDataAccessException e) {           
            e.printStackTrace();
        } catch (Exception e) {            
            e.printStackTrace();
        }
        //return lista;        
        return lista;
    }

    @Override
    public String updRestableceEmpleadoTitular(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String vReturn = "0";
        String sql = "UPDATE IDOSGD.RHTM_DEPENDENCIA \n"
                + "SET CO_EMPLEADO=CO_EMP_TITULAR ,CO_TIPO_ENCARGATURA=TI_ENC_TITULAR \n"
                + "WHERE CO_DEPENDENCIA = ? AND CO_EMP_TITULAR IS NOT NULL AND TI_ENC_TITULAR='1'";
        try {
            long nRows = jdbcTemplate.update(sql,
                    new Object[]{                        
                        asignacionFuncionarioBean.getCoDependencia()
                    });
            if (nRows > 0) {
                vReturn = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;
    }
    
     @Override
    public String updEmpleadoDelegado(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String vReturn = "0";
        String sql = "UPDATE RHTM_DEPENDENCIA \n"
                + "SET CO_EMP_TITULAR=?, \n"
                + "TI_ENC_TITULAR=?\n"
                + "WHERE CO_DEPENDENCIA = ?";
        try {
            long nRows = jdbcTemplate.update(sql,
                    new Object[]{
                        asignacionFuncionarioBean.getCoEmpleado(),
                        asignacionFuncionarioBean.getCoTipoEncargo(),
                        asignacionFuncionarioBean.getCoDependencia()
                    });
            if (nRows > 0) {
                vReturn = "1";
            }
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;
    }
    
    
}
