/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Types;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
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
        sql.append("INSERT\n"
                + "    INTO IDOSGD.TDTV_ASIGNACION_FUNCIONARIO(\n"
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
                + "      )VALUES(?,?,?,?,?,CONVERT(DATETIME, ?, 103),CONVERT(DATETIME, ?, 103),'1',?,CURRENT_TIMESTAMP,'0')");
        try {
            //Get sequence
            SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_UTILS_SEC_ASIGNACION_FUNCIONARIO_CO]")
                                            .withoutProcedureColumnMetaDataAccess()
                                            .declareParameters(new SqlOutParameter("COD_ASI_FUN_CO", Types.INTEGER));
                    
            Map out = simpleJdbcCall.execute();
            Integer codigo = (Integer)out.get("COD_ASI_FUN_CO");
            long coAsignacionFuncionario = codigo.longValue();
            
            asignacionFuncionarioBean.setCoAsignacionFuncionario(coAsignacionFuncionario);
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                asignacionFuncionarioBean.getCoAsignacionFuncionario(),
                asignacionFuncionarioBean.getCoDependencia(),
                asignacionFuncionarioBean.getCoEmpleado(),
                asignacionFuncionarioBean.getCoTipoEncargo(),
                asignacionFuncionarioBean.getDeDocAsigna(),
                getDate(asignacionFuncionarioBean.getFeInicio()),
                getDate(asignacionFuncionarioBean.getFeFin()),
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
        StringBuilder query = new StringBuilder();
        query.append("SELECT	ASF.CO_ASIGNACION_FUNCIONARIO, "); 
        query.append("		ASF.CO_DEPENDENCIA, ");
        query.append("		(SELECT DE_DEPENDENCIA ");
        query.append("		 FROM IDOSGD.RHTM_DEPENDENCIA ");
        query.append("		 WHERE CO_DEPENDENCIA=ASF.CO_DEPENDENCIA) AS DE_DEPENDENCIA, ");
        query.append("		ASF.DE_DOC_ASIGNA, ");
        query.append("		ASF.CO_EMPLEADO, ");
        query.append("		(SELECT LTRIM(RTRIM(ISNULL(CEMP_APEPAT, ''))) + ' ' + LTRIM(RTRIM(ISNULL(CEMP_APEMAT, ''))) + ' ' + LTRIM(RTRIM(ISNULL(CEMP_DENOM, ''))) ");
        query.append("		 FROM IDOSGD.RHTM_PER_EMPLEADOS ");
        query.append("		 WHERE CEMP_CODEMP=ASF.CO_EMPLEADO) AS DE_EMPLEADO, ");
        query.append("		ASF.FE_INICIO, ");
        query.append("		ASF.FE_FIN, ");
        query.append("		ASF.CO_TIPO_ENCARGO, ");
        query.append("		(SELECT CELE_DESELE ");
        query.append("		 FROM IDOSGD.SI_ELEMENTO ");
        query.append("		 WHERE CTAB_CODTAB='CO_TIPO_ENC' ");
        query.append("		 AND CELE_CODELE=ASF.CO_TIPO_ENCARGO) AS DE_TIPO_ENCARGO, ");
        query.append("		ASF.CO_USE_CRE, ");
        query.append("		ASF.FE_USE_CRE, ");
        query.append("		ASF.CO_USE_MOD, ");
        query.append("		ASF.FE_USE_MOD, ");
        query.append("		ASF.CO_ESTADO, ");
        query.append("		ASF.IN_ASIGNACION, ");
        query.append("		(SELECT DE_EST ");
        query.append("		 FROM IDOSGD.TDTR_ESTADOS ");
        query.append("		 WHERE DE_TAB='TDTV_ASIGNACION_FUNCIONARIO' ");
        query.append("		 AND CO_EST=ASF.IN_ASIGNACION) AS DE_ASIGNACION ");
        query.append("FROM IDOSGD.TDTV_ASIGNACION_FUNCIONARIO ASF ");
        query.append("WHERE ASF.CO_ESTADO = '1' ");
        if(asignacionFuncionarioBean.getCoDependencia()!=null &&asignacionFuncionarioBean.getCoDependencia().trim().length()>0){
            query.append("AND ASF.CO_DEPENDENCIA = :pcoDependencia ");
        }
        if(asignacionFuncionarioBean.getFeRegistraInicio()!=null &&asignacionFuncionarioBean.getFeRegistraFin()!=null){
            query.append("AND ASF.FE_USE_CRE BETWEEN CONVERT(DATETIME, :pfeInicio, 103) AND CONVERT(DATETIME, :pfeFin, 103) + 0.99999 ");
        }       
        query.append("ORDER BY ASF.FE_USE_CRE ");
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
        String sql = "UPDATE IDOSGD.TDTV_ASIGNACION_FUNCIONARIO \n"
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
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT  ASF.CO_ASIGNACION_FUNCIONARIO, ");
        sql.append("        ASF.CO_DEPENDENCIA, ");
        sql.append("        (SELECT DE_DEPENDENCIA ");
        sql.append("         FROM IDOSGD.RHTM_DEPENDENCIA ");
        sql.append("         WHERE CO_DEPENDENCIA=ASF.CO_DEPENDENCIA) AS DE_DEPENDENCIA, ");
        sql.append("        ASF.DE_DOC_ASIGNA, ");
        sql.append("        ASF.CO_EMPLEADO, ");
        sql.append("        (SELECT LTRIM(RTRIM(ISNULL(CEMP_APEPAT, ''))) + LTRIM(RTRIM(ISNULL(CEMP_APEMAT, ''))) + LTRIM(RTRIM(ISNULL(CEMP_DENOM, ''))) ");
        sql.append("         FROM IDOSGD.RHTM_PER_EMPLEADOS ");
        sql.append("         WHERE CEMP_CODEMP=ASF.CO_EMPLEADO) AS DE_EMPLEADO, ");
        sql.append("        ASF.FE_INICIO, ");
        sql.append("        ASF.FE_FIN, ");
        sql.append("        ASF.CO_TIPO_ENCARGO, ");
        sql.append("        ASF.CO_USE_CRE, ");
        sql.append("        ASF.FE_USE_CRE, ");
        sql.append("        ASF.CO_USE_MOD, ");
        sql.append("        ASF.FE_USE_MOD, ");
        sql.append("        ASF.CO_ESTADO, ");
        sql.append("        ASF.IN_ASIGNACION, ");
        sql.append("        (SELECT DE_EST ");
        sql.append("         FROM IDOSGD.TDTR_ESTADOS ");
        sql.append("         WHERE DE_TAB='TDTV_ASIGNACION_FUNCIONARIO' ");
        sql.append("         AND CO_EST=ASF.IN_ASIGNACION) AS DE_ASIGNACION, ");
        sql.append("        CONVERT(VARCHAR(10), ASF.FE_INICIO, 103) AS FEC_INICIO, ");
        sql.append("        CONVERT(VARCHAR(10), ASF.FE_FIN, 103) AS FEC_FIN ");
        sql.append("FROM IDOSGD.TDTV_ASIGNACION_FUNCIONARIO ASF ");
        sql.append("WHERE ASF.CO_ASIGNACION_FUNCIONARIO = :pcoAsignacionFuncionario ");
        sql.append("AND CO_ESTADO = '1' ");
        try {
            List<AsignacionFuncionarioBean> lista = namedParameterJdbcTemplate.query(sql.toString(), objectParam, new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
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
        StringBuilder sql = new StringBuilder();
        sql.append("SELECT  DEP.CO_DEPENDENCIA, ");
        sql.append("        DEP.CO_EMPLEADO, ");
        sql.append("        (SELECT EMP.CEMP_APEPAT + ' ' + EMP.CEMP_APEMAT + ' ' + EMP.CEMP_DENOM ");
        sql.append("         FROM IDOSGD.RHTM_PER_EMPLEADOS EMP ");
        sql.append("         WHERE EMP.CEMP_CODEMP=DEP.CO_EMPLEADO) AS DE_EMPLEADO, ");
        sql.append("        DEP.CO_TIPO_ENCARGATURA, ");
        sql.append("        (SELECT CELE_DESELE ");
        sql.append("         FROM IDOSGD.SI_ELEMENTO ");
        sql.append("         WHERE SI_ELEMENTO.CELE_CODELE=DEP.CO_TIPO_ENCARGATURA ");
        sql.append("         AND SI_ELEMENTO.CTAB_CODTAB='CO_TIPO_ENC') AS DE_TIPO_ENCARGO ");
        sql.append("FROM IDOSGD.RHTM_DEPENDENCIA DEP ");
        sql.append("WHERE DEP.CO_DEPENDENCIA = :pcoDependencia ");
        try {
            List<AsignacionFuncionarioBean> lista = namedParameterJdbcTemplate.query(sql.toString(), objectParam, new ParameterizedRowMapper<AsignacionFuncionarioBean>() {
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
        String sql="SELECT COUNT(CO_EMPLEADO) FROM IDOSGD.TDTV_ASIGNACION_FUNCIONARIO WHERE IN_ASIGNACION='0' AND CO_EMPLEADO=? AND CO_ESTADO='1'";
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
        String sql="SELECT COUNT(CO_DEPENDENCIA) FROM IDOSGD.TDTV_ASIGNACION_FUNCIONARIO WHERE IN_ASIGNACION='0' AND CO_DEPENDENCIA=?  AND CO_ESTADO='1'";
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
        String sql = "UPDATE IDOSGD.TDTV_ASIGNACION_FUNCIONARIO \n"
                + " SET FE_INICIO = ?,\n"
                + " FE_FIN = ?,\n"
                + " CO_TIPO_ENCARGO = ?,\n"
                + " DE_DOC_ASIGNA = ?,\n"
                + " CO_USE_MOD = ?,\n"
                + " FE_USE_MOD = CURRENT_TIMESTAMP\n"
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
    
    private String getDate(Date date) {
        String fecha = null;
        if (date != null) {
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            fecha = sdf.format(date); 
        }
        return fecha;
    }
    
}
