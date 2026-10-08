/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.AsignacionFuncionarioBean;
import pe.gob.onpe.tramitedoc.dao.AsignacionFuncionarioDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author FSilva
 */
@Repository
public class AsignacionFuncionarioDaoImp extends SimpleJdbcDaoBase implements AsignacionFuncionarioDao {

    @Override
    public String[] insAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String msg[] = new String[3];
        StringBuilder sql = new StringBuilder();
        StringBuilder sqlSeq = new StringBuilder();
        //Get sequence
        sqlSeq.append("SELECT TDTV_ASIGNACION_FUNCIONARIO_CO.NEXTVAL FROM DUAL");
        sql.append("INSERT\n"
                + "    INTO TDTV_ASIGNACION_FUNCIONARIO(\n"
                + "        CO_ASIGNACION_FUNCIONARIO,\n"
                + "        CO_DEPENDENCIA ,\n"
                + "        CO_EMPLEADO ,\n"
                + "        CO_TIPO_ENCARGO ,\n"
                + "        DE_DOC_ASIGNA ,\n"
                + "        FE_INICIO ,\n"
                + "        FE_FIN ,\n"
                + "        CO_ESTADO ,\n"
                + "        CO_USE_CRE ,\n"   
                + "        FE_USE_CRE ,\n"                                
                + "        IN_ASIGNACION\n"                             
                + "      )VALUES(?,?,?,?,?,?,?,'1',?,SYSDATE,'0')");
        try {
            long coAsignacionFuncionario = this.jdbcTemplate.queryForObject(sqlSeq.toString(), Long.class);
            asignacionFuncionarioBean.setCoAsignacionFuncionario(coAsignacionFuncionario);
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                asignacionFuncionarioBean.getCoAsignacionFuncionario(),
                asignacionFuncionarioBean.getCoDependencia(),
                asignacionFuncionarioBean.getCoEmpleado(),
                asignacionFuncionarioBean.getCoTipoEncargo(),
                asignacionFuncionarioBean.getDeDocAsigna(),
                asignacionFuncionarioBean.getFeInicio(),
                asignacionFuncionarioBean.getFeFin(),
                asignacionFuncionarioBean.getCoUseCre()
                //asignacionFuncionarioBean.getInAsignacion(),                
            });
            msg[0] = "01";
            msg[1] = "Registrado Correctamente";
            msg[2] = String.valueOf(coAsignacionFuncionario);
        } catch (DuplicateKeyException dep) {
            msg[0] = "00";
            msg[1] = "El identificador de la tabla es duplicado";
        } catch (Exception e) {
            e.printStackTrace();
            msg[0] = "00";
            msg[1] = "Error en el registro";
        }
        return msg;
    }

    @Override
    public List<AsignacionFuncionarioBean> getListAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pcoDependencia", asignacionFuncionarioBean.getCoDependencia());
        objectParam.put("pfeInicio", asignacionFuncionarioBean.getFeRegistraInicio());
        objectParam.put("pfeFin", asignacionFuncionarioBean.getFeRegistraFin());
        StringBuilder query =new StringBuilder();
        query.append("SELECT ASF.CO_ASIGNACION_FUNCIONARIO, \n"
                + "	ASF.CO_DEPENDENCIA, \n"
                + "	(SELECT DE_DEPENDENCIA FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA=ASF.CO_DEPENDENCIA) AS DE_DEPENDENCIA, \n"
                + "	ASF.DE_DOC_ASIGNA, \n"
                + "	ASF.CO_EMPLEADO, \n"
                + "	(SELECT TRIM(NVL(CEMP_APEPAT,''))||' '||TRIM(NVL(CEMP_APEMAT,''))||' '||TRIM(NVL(CEMP_DENOM,'')) FROM RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP=ASF.CO_EMPLEADO)DE_EMPLEADO,  \n"
                + "	ASF.FE_INICIO, \n"
                + "	ASF.FE_FIN, \n"
                + "	ASF.CO_TIPO_ENCARGO, \n"
                +"      (SELECT CELE_DESELE FROM SI_ELEMENTO WHERE CTAB_CODTAB='CO_TIPO_ENC' AND CELE_CODELE=ASF.CO_TIPO_ENCARGO) DE_TIPO_ENCARGO,"
                + "	ASF.CO_USE_CRE, \n"
                + "	ASF.FE_USE_CRE, \n"
                + "	ASF.CO_USE_MOD, \n"
                + "	ASF.FE_USE_MOD, \n"
                + "	ASF.CO_ESTADO, \n"
                + "	ASF.IN_ASIGNACION,\n"
                + "	(SELECT DE_EST FROM TDTR_ESTADOS WHERE DE_TAB='TDTV_ASIGNACION_FUNCIONARIO' AND CO_EST=ASF.IN_ASIGNACION) DE_ASIGNACION\n"
                +"      FROM TDTV_ASIGNACION_FUNCIONARIO ASF "
                + "     WHERE ASF.CO_ESTADO='1'");
        if(asignacionFuncionarioBean.getCoDependencia()!=null &&asignacionFuncionarioBean.getCoDependencia().trim().length()>0){
            query.append("     AND ASF.CO_DEPENDENCIA=:pcoDependencia");
        }
        if(asignacionFuncionarioBean.getFeRegistraInicio()!=null &&asignacionFuncionarioBean.getFeRegistraFin()!=null){
            query.append("     AND ASF.FE_USE_CRE BETWEEN TO_DATE(:pfeInicio,'dd/mm/yyyy') AND TO_DATE(:pfeFin,'dd/mm/yyyy') + 0.99999");
        }       
        query.append(" ORDER BY ASF.FE_USE_CRE");
        List lista=null;
        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
                public AsignacionFuncionarioBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
                    asignacionFuncionarioBean.setCoAsignacionFuncionario(rs.getLong("CO_ASIGNACION_FUNCIONARIO"));
                    asignacionFuncionarioBean.setCoDependencia(rs.getString("CO_DEPENDENCIA"));
                    asignacionFuncionarioBean.setDeDependencia(rs.getString("DE_DEPENDENCIA"));
                    asignacionFuncionarioBean.setCoEmpleado(rs.getString("CO_EMPLEADO"));
                    asignacionFuncionarioBean.setDeEmpleado(rs.getString("DE_EMPLEADO"));
                    asignacionFuncionarioBean.setCoEstado(rs.getString("CO_ESTADO"));
                    asignacionFuncionarioBean.setCoTipoEncargo(rs.getString("CO_TIPO_ENCARGO"));
                    asignacionFuncionarioBean.setDeTipoEncargo(rs.getString("DE_TIPO_ENCARGO"));
                    asignacionFuncionarioBean.setCoUseCre(rs.getString("CO_USE_CRE"));
                    asignacionFuncionarioBean.setCoUseMod(rs.getString("CO_USE_MOD"));
                    asignacionFuncionarioBean.setDeDocAsigna(rs.getString("DE_DOC_ASIGNA"));
                    asignacionFuncionarioBean.setFeUseCre(rs.getDate("FE_USE_CRE"));
                    asignacionFuncionarioBean.setFeFin(rs.getDate("FE_FIN"));
                    asignacionFuncionarioBean.setFeInicio(rs.getDate("FE_INICIO"));                    
                    asignacionFuncionarioBean.setInAsignacion(rs.getString("IN_ASIGNACION"));
                    asignacionFuncionarioBean.setDeAsignacion(rs.getString("DE_ASIGNACION"));
                    return asignacionFuncionarioBean;
                }
            });                
        } catch (EmptyResultDataAccessException e) {           
            lista=null;
            e.printStackTrace();
        } catch (Exception e) {            
            e.printStackTrace();
        }
        //return lista;        
        return lista;
    }

    @Override
    public String delAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String vReturn = "0";
        String sql = "UPDATE TDTV_ASIGNACION_FUNCIONARIO \n"
                + "SET CO_ESTADO = '0'\n"                
                + "WHERE CO_ASIGNACION_FUNCIONARIO = ?";
        try {
            long nRows = jdbcTemplate.update(sql,
                    new Object[]{asignacionFuncionarioBean.getCoAsignacionFuncionario()});
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
        AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pcoAsignacionFuncionario", coAsignacionFuncionarioBean);
        String query = "SELECT ASF.CO_ASIGNACION_FUNCIONARIO, \n"
                + "	ASF.CO_DEPENDENCIA, \n"
                + "	(SELECT DE_DEPENDENCIA FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA=ASF.CO_DEPENDENCIA) AS DE_DEPENDENCIA, \n"
                + "	ASF.DE_DOC_ASIGNA, \n"
                + "	ASF.CO_EMPLEADO, \n"
                + "	(SELECT TRIM(NVL(CEMP_APEPAT,''))||TRIM(NVL(CEMP_APEMAT,''))||TRIM(NVL(CEMP_DENOM,'')) FROM RHTM_PER_EMPLEADOS WHERE CEMP_CODEMP=ASF.CO_EMPLEADO)DE_EMPLEADO,  \n"
                + "	ASF.FE_INICIO, \n"
                + "	ASF.FE_FIN, \n"
                + "	ASF.CO_TIPO_ENCARGO, \n"
                + "	ASF.CO_USE_CRE, \n"
                + "	ASF.FE_USE_CRE, \n"
                + "	ASF.CO_USE_MOD, \n"
                + "	ASF.FE_USE_MOD, \n"
                + "	ASF.CO_ESTADO, \n"
                + "	ASF.IN_ASIGNACION,\n"
                + "	(SELECT DE_EST FROM TDTR_ESTADOS WHERE DE_TAB='TDTV_ASIGNACION_FUNCIONARIO' AND CO_EST=ASF.IN_ASIGNACION) DE_ASIGNACION,\n"
                + "	TO_CHAR(ASF.FE_INICIO, 'DD/MM/YYYY') AS FEC_INICIO, \n"
                + "     TO_CHAR(ASF.FE_FIN, 'DD/MM/YYYY') AS FEC_FIN \n"
                + "     FROM TDTV_ASIGNACION_FUNCIONARIO ASF "
                + "     WHERE ASF.CO_ASIGNACION_FUNCIONARIO=:pcoAsignacionFuncionario"
                + "     AND CO_ESTADO='1'";
        try {
            List<AsignacionFuncionarioBean> lista = namedParameterJdbcTemplate.query(query, objectParam, new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
                public AsignacionFuncionarioBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
                    asignacionFuncionarioBean.setCoAsignacionFuncionario(rs.getLong("CO_ASIGNACION_FUNCIONARIO"));
                    asignacionFuncionarioBean.setCoDependencia(rs.getString("CO_DEPENDENCIA"));
                    asignacionFuncionarioBean.setDeDependencia(rs.getString("DE_DEPENDENCIA"));
                    asignacionFuncionarioBean.setCoEmpleado(rs.getString("CO_EMPLEADO"));
                    asignacionFuncionarioBean.setDeEmpleado(rs.getString("DE_EMPLEADO"));
                    asignacionFuncionarioBean.setCoEstado(rs.getString("CO_ESTADO"));
                    asignacionFuncionarioBean.setCoTipoEncargo(rs.getString("CO_TIPO_ENCARGO"));
                    asignacionFuncionarioBean.setCoUseCre(rs.getString("CO_USE_CRE"));
                    asignacionFuncionarioBean.setCoUseMod(rs.getString("CO_USE_MOD"));
                    asignacionFuncionarioBean.setDeDocAsigna(rs.getString("DE_DOC_ASIGNA"));
                    asignacionFuncionarioBean.setFeFin(rs.getDate("FE_FIN"));
                    asignacionFuncionarioBean.setFeInicio(rs.getDate("FE_INICIO"));                    
                    asignacionFuncionarioBean.setInAsignacion(rs.getString("IN_ASIGNACION"));
                    asignacionFuncionarioBean.setDeAsignacion(rs.getString("DE_ASIGNACION"));
                    asignacionFuncionarioBean.setFec_inicio(rs.getString("FEC_INICIO"));
                    asignacionFuncionarioBean.setFec_fin(rs.getString("FEC_FIN"));
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
        //return lista;        
        return asignacionFuncionarioBean;
    }

    @Override
    public AsignacionFuncionarioBean getFuncionarioPorDependencia(String coDependencia) {
        AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pcoDependencia", coDependencia);
        String query = "SELECT DEP.CO_DEPENDENCIA,\n"
                +" DEP.CO_EMPLEADO,\n"
                + "(SELECT EMP.CEMP_APEPAT||' '||EMP.CEMP_APEMAT||' '||EMP.CEMP_DENOM  FROM RHTM_PER_EMPLEADOS EMP WHERE EMP.CEMP_CODEMP=DEP.CO_EMPLEADO)AS DE_EMPLEADO,\n"
                + "DEP.CO_TIPO_ENCARGATURA,\n"
                + "(SELECT CELE_DESELE FROM SI_ELEMENTO WHERE SI_ELEMENTO.CELE_CODELE=DEP.CO_TIPO_ENCARGATURA AND SI_ELEMENTO.CTAB_CODTAB='CO_TIPO_ENC') DE_TIPO_ENCARGO \n"
                + "FROM RHTM_DEPENDENCIA DEP\n"
                + "WHERE DEP.CO_DEPENDENCIA=:pcoDependencia";
        try {
            List<AsignacionFuncionarioBean> lista = namedParameterJdbcTemplate.query(query, objectParam, new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
                public AsignacionFuncionarioBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AsignacionFuncionarioBean asignacionFuncionarioBean=new AsignacionFuncionarioBean();                    
                    asignacionFuncionarioBean.setCoDependencia(rs.getString("CO_DEPENDENCIA"));
                    asignacionFuncionarioBean.setCoEmpleado(rs.getString("CO_EMPLEADO"));
                    asignacionFuncionarioBean.setDeEmpleado(rs.getString("DE_EMPLEADO"));                    
                    asignacionFuncionarioBean.setCoTipoEncargo(rs.getString("CO_TIPO_ENCARGATURA"));
                    asignacionFuncionarioBean.setDeTipoEncargo(rs.getString("DE_TIPO_ENCARGO"));                    
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
        //return lista;        
        return asignacionFuncionarioBean;    
    }

    @Override
    public int getExisteProgramacionEmpleado(String coEmpleado) {
        String sql="SELECT COUNT(CO_EMPLEADO) FROM TDTV_ASIGNACION_FUNCIONARIO WHERE IN_ASIGNACION='0' AND CO_EMPLEADO=? AND CO_ESTADO='1'";
        int cant=1;
        try {
            cant=this.jdbcTemplate.queryForObject(sql, new Object[]{coEmpleado}, Integer.class);
        } catch (Exception e) {
            cant=0;
        }
        return cant;
    }

    @Override
    public int getExisteProgramacionDependencia(String coDependencia) {
        String sql="SELECT COUNT(CO_DEPENDENCIA) FROM TDTV_ASIGNACION_FUNCIONARIO WHERE IN_ASIGNACION='0' AND CO_DEPENDENCIA=?  AND CO_ESTADO='1'";
        int cant=1;
        try {
            cant=this.jdbcTemplate.queryForObject(sql, new Object[]{coDependencia}, Integer.class);
        } catch (Exception e) {
            cant=0;
        }
        return cant;
    }

    @Override
    public String updAsignacionFuncionario(AsignacionFuncionarioBean asignacionFuncionarioBean) {
        String vReturn = "0";
        String sql = "UPDATE TDTV_ASIGNACION_FUNCIONARIO \n"
                + " SET FE_INICIO = ?,\n"
                + " FE_FIN = ?,\n"
                + " CO_TIPO_ENCARGO = ?,\n"
                + " DE_DOC_ASIGNA = ?,\n"
                + " CO_USE_MOD = ?,\n"
                + " FE_USE_MOD = SYSDATE\n"
                + " WHERE CO_ASIGNACION_FUNCIONARIO = ?";
        try {
            long nRows = jdbcTemplate.update(sql,
                    new Object[]{
                        asignacionFuncionarioBean.getFeInicio(),
                        asignacionFuncionarioBean.getFeFin(),
                        asignacionFuncionarioBean.getCoTipoEncargo(),
                        asignacionFuncionarioBean.getDeDocAsigna(),
                        asignacionFuncionarioBean.getCoUseMod(),
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
}
