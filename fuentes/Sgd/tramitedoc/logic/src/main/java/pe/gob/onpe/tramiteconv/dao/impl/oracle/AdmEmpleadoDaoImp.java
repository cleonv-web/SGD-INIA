/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
 
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoCargoBean;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoAccesoBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.dao.AdmEmpleadoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author GLuque
 */
@Repository("admEmpleadoDao")
public class AdmEmpleadoDaoImp extends SimpleJdbcDaoBase implements AdmEmpleadoDao{
    
    /*private String sql_base = "SELECT E.CEMP_CODEMP co_Empleado, E.CEMP_APEPAT ap_Paterno, E.CEMP_APEMAT ap_Materno,"
                +"E.CEMP_DENOM nombres, E.CEMP_NU_DNI dni,E.CEMP_TIPSEX sexo, E.FEMP_FECNAC fecha_Nacimiento, "
                +"E.CEMP_EMAIL email,E.CEMP_EST_EMP estado, "
                +"E.CEMP_CO_DEPEND co_Dependencia, E.CEMP_CO_CARGO coCargo, "
                +"PK_SGD_DESCRIPCION.de_dependencia(E.CEMP_CO_DEPEND) de_Dependencia, "
                +"PK_SGD_DESCRIPCION.de_cargo(E.CEMP_CO_CARGO) de_Cargo "
                +"FROM RHTM_PER_EMPLEADOS E ";*/
    private String sql_base = " SELECT E.CEMP_CODEMP co_Empleado, E.CEMP_APEPAT ap_Paterno, E.CEMP_APEMAT ap_Materno,\n" +
                            "                E.CEMP_DENOM nombres, E.CEMP_NU_DNI dni,E.CEMP_TIPSEX sexo, TO_CHAR(E.FEMP_FECNAC, 'DD/MM/YYYY')  fecha_Nacimiento, \n" +
                            "                E.CEMP_EMAIL email,E.CEMP_EST_EMP estado, \n" +
                            "                E.CEMP_CO_DEPEND co_Dependencia, E.CEMP_CO_CARGO coCargo, \n" +
                            "                PK_SGD_DESCRIPCION.de_dependencia(E.CEMP_CO_DEPEND) de_Dependencia, \n" +
                            "                PK_SGD_DESCRIPCION.de_cargo(E.CEMP_CO_CARGO) de_Cargo ,\n" +
                            "                U.COD_USER usuario,U.ES_USUARIO esUsuario,CASE U.ES_USUARIO WHEN 'A' THEN 'Activo' WHEN 'N' THEN 'Nuevo' WHEN 'B' THEN 'Bloq. x Otros' WHEN 'X' THEN 'Baja' WHEN 'M' THEN 'Migracion' WHEN 'I' THEN 'Bloq. x Intentos' ELSE 'Sin Usuario' END estadousuario,U.IN_AD as inAD \n" +
                            " ,e.CEMP_DESCRIP co_Observacion"
                            + " ,e.cemp_id_crea"
                            + ", CRE.CEMP_DENOM ||' '|| CRE.CEMP_APEPAT crea_Usuario "
                            + ",TO_CHAR(e.cemp_fe_crea,'DD/MM/YYYY HH:MI:SS')  crea_Fecha "
                            + ",e.cemp_id_act "
                            + ", ACT.CEMP_DENOM ||' '|| ACT.CEMP_APEPAT act_Usuario "
                            + ",TO_CHAR(e.cemp_fe_act,'DD/MM/YYYY HH:MI:SS')  act_Fecha , LO.de_nombre_local NO_LOCAL, E.CEMP_CO_LOCAL CO_LOCAL "+
                            "  FROM RHTM_PER_EMPLEADOS E LEFT JOIN SEG_USUARIOS1 U ON U.CEMP_CODEMP= E.CEMP_CODEMP "+
                            " LEFT JOIN RHTM_PER_EMPLEADOS CRE ON cre.cemp_codemp=e.cemp_id_crea LEFT JOIN RHTM_PER_EMPLEADOS ACT ON ACT.cemp_codemp=e.cemp_id_act "+
                            " LEFT JOIN IDOSGD.si_mae_local LO ON LO.CCOD_LOCAL=E.CEMP_CO_LOCAL ";
    /**
     * Obtiene un determinado empleado mediante su codigo
     * @return
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoBean getAdmEmpleado(String codEmp) throws Exception{
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        sql.append(this.sql_base).append(" WHERE E.CEMP_CODEMP = :pCoEmp");
        objectParam.put("pCoEmp", codEmp);
        AdmEmpleadoBean empleado = new AdmEmpleadoBean();
        try {
            empleado = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AdmEmpleadoBean.class));
        } catch (EmptyResultDataAccessException e) {
            empleado = null;
        } catch(Exception e){
            e.printStackTrace();
            throw e;
        }
        return empleado;
    }
    
    
    /**
     * Obtiene un determinado empleado mediante su DNI
     * @param dni
     * @return
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoBean getAdmEmpleadoDesdeDni(String dni) throws Exception{
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        sql.append(this.sql_base).append(" WHERE E.CEMP_NU_DNI=:pnuDNI");
        objectParam.put("pnuDNI", dni);
        AdmEmpleadoBean empleado = new AdmEmpleadoBean();
        try {
            empleado = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AdmEmpleadoBean.class));
        } catch (EmptyResultDataAccessException e) {
            empleado = null;
        } catch(Exception e){
            throw e;
        }
        return empleado;
    }
    
    
    /**
     * Actualiza datos personales de un empleado
     * @param empleado
     * @return
     * @throws Exception 
     */
    @Override
    public int updAdmEmpleado(AdmEmpleadoBean empleado, Usuario usuario) throws Exception{
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE RHTM_PER_EMPLEADOS SET "
                +" CEMP_NU_DNI=?,"
                +" CEMP_APEPAT=?,"
                +" CEMP_APEMAT=?,"
                +" CEMP_DENOM=?,"
                +" FEMP_FECNAC=to_date(?,'DD/MM/YYYY'),"
                +" CEMP_TIPSEX=?, "
                +" CEMP_EMAIL=?, "
                +" CEMP_EST_EMP=?, "
                +" CEMP_INDBAJ=?, "
                +((empleado.getEstado().equals("0")) ? "FEMP_FECBAJ=SYSDATE,":"")
                +" CEMP_CO_DEPEND=?, "
                +" CO_DEPENDENCIA=?, "
                +" CEMP_CO_CARGO=?, "
                +" CEMP_DESCRIP=?,"
                +" CEMP_FE_ACT=SYSDATE,CEMP_ID_ACT =?,CEMP_CO_LOCAL=? "
                +" WHERE CEMP_CODEMP=?");
        try {
            int upd = this.jdbcTemplate.update(sql.toString(),new Object[]{empleado.getDni(), empleado.getApPaterno().toUpperCase(),empleado.getApMaterno().toUpperCase(),
                //empleado.getNombres().toUpperCase(),empleado.getFechaNacimiento2(),empleado.getSexo(),empleado.getEmail().toLowerCase(),empleado.getEstado(),empleado.getEstado(),
                empleado.getNombres().toUpperCase(),(empleado.getFechaNacimiento2().equals("")?null:empleado.getFechaNacimiento2()),empleado.getSexo(),empleado.getEmail().toLowerCase(),empleado.getEstado(),empleado.getEstado(),
                empleado.getCoDependencia(),empleado.getCoDependencia(),empleado.getCoCargo().equals("")?"0000":empleado.getCoCargo(),empleado.getCoObservacion(),usuario.getCempCodemp(),empleado.getCoLocal(),empleado.getCoEmpleado()});
            /*if(acceso.getCoUsuario().length()>0){//Crear acceso si solo viene con USERNAME 
                this.saveNuevoAcceso(acceso);
            }*/
            return upd;
        } catch (Exception e) {
            throw e;
        }
    }

    /**
     * Guardar nuevo empleado
     * @param empleado
     * @param usuario
     * @return
     * @throws Exception 
     */
    @Override
    public String nuevoAdmEmpleado(AdmEmpleadoBean empleado, Usuario usuario) throws Exception{
        String vReturn="NO_OK";
        try{
            //Obtiene codigo de empleado
            /*String sql1 = "SELECT lpad(to_char(to_number(MAX(CEMP_CODEMP))+1),5,'0') coEmpleado FROM RHTM_PER_EMPLEADOS";
            AdmEmpleadoBean emp = this.jdbcTemplate.queryForObject(sql1, BeanPropertyRowMapper.newInstance(AdmEmpleadoBean.class));
            String coEmpleadoTmp = emp.getCoEmpleado();*/

            StringBuffer sql = new StringBuffer();
            sql.append("INSERT INTO RHTM_PER_EMPLEADOS (CEMP_CODEMP,CEMP_CO_EMP, "
                    +"CEMP_NU_DNI,CEMP_APEPAT,CEMP_APEMAT,CEMP_DENOM,"
                    +"CEMP_EMAIL,CEMP_EST_EMP,CEMP_INDBAJ, "
                    +"CEMP_TIPSEX,FEMP_FECNAC,CEMP_CO_DEPEND,CO_DEPENDENCIA,"
                    +"CEMP_CO_CARGO,CEMP_CO_CATEGORIA,"
                    +"CEMP_FE_CREA,CEMP_ID_CREA,CEMP_FE_ACT,CEMP_ID_ACT,"
                    +"FEMP_FECALT,CEMP_TI_EMPLEADO,CEMP_CO_LOCAL,CEMP_DESCRIP"
                    +" ) VALUES "
                    +"(?,?,"
                    +"?,?,?,?,"
                    +"?,?,?, "
                    +"?,to_date(?,'DD/MM/YYYY'),?,?, "
                    +"?,'' "
                    +",SYSDATE,?,SYSDATE,?,SYSDATE,'3',?,?)");
            this.jdbcTemplate.update(sql.toString(),new Object[]{empleado.getCoEmpleado(),empleado.getCoEmpleado(),empleado.getDni(),empleado.getApPaterno().toUpperCase(),
                empleado.getApMaterno().toUpperCase(),empleado.getNombres().toUpperCase(),empleado.getEmail().toLowerCase(),empleado.getEstado(),
                //empleado.getEstado(),empleado.getSexo(),empleado.getFechaNacimiento2(),empleado.getCoDependencia(),empleado.getCoDependencia(),
                empleado.getEstado(),empleado.getSexo(),(empleado.getFechaNacimiento2().equals("")?null:empleado.getFechaNacimiento2()),empleado.getCoDependencia(),empleado.getCoDependencia(),
                empleado.getCoCargo().equals("")?"0000":empleado.getCoCargo(),usuario.getCempCodemp(),usuario.getCempCodemp(),empleado.getCoLocal(),empleado.getCoObservacion()});
            // guardamos accesos
            /*acceso.setCoEmpleado(coEmpleadoTmp);
            String nuevo = this.saveNuevoAcceso(acceso);*/
            //------
            vReturn="OK";
        } catch (DuplicateKeyException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
        return vReturn;
    }
    
    /**
     * Busca empleado de la tabla maestra mediante DNI
     * @param dni
     * @return
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoBean getPersonaDesdeDni(String dni) throws Exception{
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.nulem dni, A.DEAPP apPaterno, A.DEAPM apMaterno, A.DENOM nombres, ");
        sql.append(" TO_DATE(A.FENAC,'YYYYMMDD') fechaNacimiento,A.INSEX sexo FROM  idtanirs A ");
        sql.append(" WHERE A.nulem='").append(dni).append("' ");
        AdmEmpleadoBean empleado = new AdmEmpleadoBean();
        try {
            empleado = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(AdmEmpleadoBean.class));
        } catch (EmptyResultDataAccessException e) {
            empleado = null;
        } catch(Exception e){
            throw e;
        }
        return empleado;
    }
    
    /**
     * Realiza búsqueda segun a los criterios detallados lineas abajo:
     * @param dni
     * @param apPaterno
     * @param apMaterno
     * @param nombres
     * @return
     * @throws Exception 
     */
    @Override
    public List<AdmEmpleadoBean> getBsqAdmEmpleado(String dni, String apPaterno, String apMaterno, String nombres, String usuario) throws Exception{
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        sql.append(this.sql_base);
        if(dni.length()>0 || apPaterno.length()>0 || apMaterno.length()>0 || nombres.length()>0 || usuario.length()>0 ){
            String where_and = " WHERE ";
            if(dni.length()>0){
                sql.append(where_and).append(" E.CEMP_NU_DNI LIKE ''||:pDNI||'%' ");
                objectParam.put("pDNI", dni);
                where_and = " AND ";
            }
            if(apPaterno.length()>0){
                sql.append(where_and).append(" E.CEMP_APEPAT LIKE '%'||:pdeApPater||'%' ");
                objectParam.put("pdeApPater", apPaterno);
                where_and = " AND ";
            }
            if(apMaterno.length()>0){
                sql.append(where_and).append(" E.CEMP_APEMAT LIKE '%'||:pdeApMater||'%' ");
                objectParam.put("pdeApMater", apMaterno);
                where_and = " AND ";
            }
            if(nombres.length()>0){
                sql.append(where_and).append(" E.CEMP_DENOM LIKE '%'||:pnombres||'%' ");
                objectParam.put("pnombres", nombres);
            }
            if(usuario.length()>0){
                sql.append(where_and).append(" U.COD_USER LIKE '%'||:pusuarios||'%' ");
                objectParam.put("pusuarios", usuario);
            }
        }
        List<AdmEmpleadoBean> list = new ArrayList<AdmEmpleadoBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AdmEmpleadoBean.class));
        } catch (Exception e) {
            throw e;
        }
        return list;
    }
    
    
    /**
     * Obtiene una lista de dependencia,
     * realizando una búsqueda de acuerdo al parámetro "CRITERIO"
     * @param criterio
     * @return
     * @throws Exception 
     */
    @Override
    public List<DependenciaBean> getBsqDependencia(String criterio)throws Exception{//ecueva
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        sql.append("SELECT CO_DEPENDENCIA coDependencia,DE_CORTA_DEPEN deCortaDepen, ");
        sql.append(" DE_DEPENDENCIA deDependencia  FROM RHTM_DEPENDENCIA");
        sql.append(" WHERE DE_CORTA_DEPEN LIKE ''||:pCriterio||'%' OR DE_DEPENDENCIA LIKE ''||:pCriterio||'%' ");
        objectParam.put("pCriterio", criterio);
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DependenciaBean.class));
        } catch (Exception e) {
            throw e;
        }
        return list;
    }
    
    /**
     * Obtiene lista de cargos de los empleados
     * @return
     * @throws Exception 
     */
    @Override
    public List<AdmEmpleadoCargoBean> getLsCargo() throws Exception{
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CCAR_CO_CARGO coCargo,CCAR_DESCAR deCargo FROM RHTM_CARGOS ORDER BY CCAR_CO_CARGO ASC");
        List<AdmEmpleadoCargoBean> list = new ArrayList<AdmEmpleadoCargoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(AdmEmpleadoCargoBean.class));
        } catch (Exception e) {
            throw e;
        }
        return list;
    }
    
    /**
     * Obtiene Objeto Acceso del Empleado
     * @param coEmpleado
     * @return
     * @throws Exception 
     */
    @Override
    public AdmEmpleadoAccesoBean getAcceso(String coEmpleado) throws Exception{
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COD_USER coUsuario,CDES_USER deUsuario,ES_USUARIO esUsuario,IN_AD inAD FROM SEG_USUARIOS1 WHERE CEMP_CODEMP='"+coEmpleado+"'");
        AdmEmpleadoAccesoBean usuario = new AdmEmpleadoAccesoBean();
        try {
            usuario = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(AdmEmpleadoAccesoBean.class));
        } catch (EmptyResultDataAccessException e) {
            usuario = null;
        } catch(Exception e){
            throw e;
        }
        return usuario;
    }
    
    /**
     * Obtiene lista de Obj Acceso que coincidan con el NombreUsuario el cual se 
     * le asignará al empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    @Override
    public List<AdmEmpleadoAccesoBean> getLsAcceso(AdmEmpleadoAccesoBean acceso) throws Exception{
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        //sql.append("SELECT COD_USER coUsuario,CDES_USER deUsuario FROM SEG_USUARIOS1 WHERE COD_USER LIKE ''||:pcoUser||'%' ");
        sql.append("SELECT COD_USER coUsuario,CDES_USER deUsuario FROM SEG_USUARIOS1 WHERE COD_USER=:pcoUser");        
        objectParam.put("pcoUser", acceso.getCoUsuario());
//        String coEmp=acceso.getCoEmpleado();
//        if(coEmp!=null&&coEmp.trim().length()>0){
//            sql.append("AND CEMP_CODEMP <> :pcoEmp");
//            objectParam.put("pcoEmp", coEmp);
//        }
        List<AdmEmpleadoAccesoBean> list = new ArrayList<AdmEmpleadoAccesoBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AdmEmpleadoAccesoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch(Exception e){
            throw e;
        }
        return list;
    }
    
    /**
     * Guarda nuevo Usuario con Acceso al sistema
     * @param coUser
     * @param coEmp
     * @param nuDni
     * @return
     * @throws Exception 
     */
    @Override
    public String saveNuevoAcceso(String coUser, String coEmp, String nuDni,String inAD) throws Exception{
        try{
            if(coUser!=null && coUser.length() >0 && coEmp.length()>0){
                SimpleJdbcCall simpleJdbcCall = 
                new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_TRAMITE").withFunctionName("CREA_USUARIO")
                        .withoutProcedureColumnMetaDataAccess()
                        .useInParameterNames("PCO_USUARIO", "PCO_EMP", "PNU_DNI","PIN_AD")
                        .declareParameters(
                            new SqlOutParameter("RESULT", Types.VARCHAR),
                            new SqlParameter("PCO_USUARIO", Types.VARCHAR),
                            new SqlParameter("PCO_EMP", Types.VARCHAR),
                            new SqlParameter("PNU_DNI", Types.VARCHAR),
                            new SqlParameter("PIN_AD", Types.VARCHAR));
                
                SqlParameterSource in = new MapSqlParameterSource()
                    .addValue("PCO_USUARIO", coUser)
                    .addValue("PCO_EMP", coEmp)
                    .addValue("PNU_DNI", nuDni)
                    .addValue("PIN_AD", inAD);
                    
                Map out;
                String mensaje= null;
                try {
                     out = simpleJdbcCall.execute(in);
                     mensaje = (String)out.get("RESULT");
                  } catch (Exception e) {
                     e.printStackTrace();
                     throw e;
                }
                return mensaje;
            } else {
                return "Sin crear nuevo Usuario";
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }
   
   //YUAL          
    public String updAcceso(String coUser, String coEmp, String nuDni,String inAD)  {
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        sqlUpd.append("update SEG_USUARIOS1 \n"
                + "set IN_AD=?\n"
                + "where\n"
                + " CEMP_CODEMP=? \n");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{inAD,coEmp});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
            
        }
        return vReturn;
    }
    
    /**
     * Elimina acceso al sistema para un determinado empleado
     * @param acceso
     * @return
     * @throws Exception 
     */
    /*@Override
    public boolean deleteAcceso(AdmEmpleadoAccesoBean acceso) throws Exception{
        try{
            if(acceso.getCoEmpleado()!=null && acceso.getCoEmpleado().length()>0){
                 //Obtiene el USERNAME del empleado 
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT COD_USER coUsuario,CDES_USER deUsuario FROM SEG_USUARIOS1 WHERE CEMP_CODEMP='"+acceso.getCoEmpleado()+"'");
                AdmEmpleadoAccesoBean acceso_tmp = new AdmEmpleadoAccesoBean();
                try {
                    acceso_tmp = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(AdmEmpleadoAccesoBean.class));
                } catch (EmptyResultDataAccessException e) {
                    acceso_tmp = null;
                } catch(Exception e){
                    throw e;
                }
                 //Tiene usuario 
                if(acceso_tmp!=null){
                    //PK_SGD_SEGURIDAD.borrar_usuario(pco_usuario varchar2); 
                    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_SEGURIDAD").withProcedureName("BORRAR_USUARIO")
                        .withoutProcedureColumnMetaDataAccess()
                        .useInParameterNames("PCO_USUARIO")
                        .declareParameters(new SqlParameter("PCO_USUARIO", Types.VARCHAR));
                    
                    SqlParameterSource in = new MapSqlParameterSource()
                            .addValue("PCO_USUARIO", acceso_tmp.getCoUsuario());
                    try {
                         simpleJdbcCall.execute(in);
                      } catch (Exception e) {
                         e.printStackTrace();
                         throw e;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }*/
    
    /**
     * Restablece el acceso al sistema de un empleado
     * @param acceso
     * @param empleado
     * @return
     * @throws Exception 
     */     
    /*@Override
    public boolean restablecerAcceso(AdmEmpleadoAccesoBean acceso, AdmEmpleadoBean empleado) throws Exception{
        try{
            if(acceso.getCoEmpleado()!=null && acceso.getCoEmpleado().length()>0){
                 //Obtiene el USERNAME del empleado 
                StringBuffer sql = new StringBuffer();
                sql.append("SELECT COD_USER coUsuario,CDES_USER deUsuario FROM SEG_USUARIOS1 WHERE CEMP_CODEMP='"+acceso.getCoEmpleado()+"'");
                AdmEmpleadoAccesoBean acceso_tmp = new AdmEmpleadoAccesoBean();
                try {
                    acceso_tmp = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(AdmEmpleadoAccesoBean.class));
                } catch (EmptyResultDataAccessException e) {
                    acceso_tmp = null;
                } catch(Exception e){
                    throw e;
                }
                // Tiene usuario 
                if(acceso_tmp!=null){
                    //PK_SGD_SEGURIDAD.borrar_usuario(pco_usuario varchar2); 
                    SimpleJdbcCall simpleJdbcCall = new SimpleJdbcCall(this.dataSource).withCatalogName("PK_SGD_SEGURIDAD").withProcedureName("RESET_PASSWORD")
                        .withoutProcedureColumnMetaDataAccess()
                        .useInParameterNames("PCO_USUARIO","PNU_DNI")
                        .declareParameters(
                                new SqlParameter("PCO_USUARIO", Types.VARCHAR),
                                new SqlParameter("PNU_DNI", Types.VARCHAR)
                        );
                    
                    SqlParameterSource in = new MapSqlParameterSource()
                            .addValue("PCO_USUARIO", acceso_tmp.getCoUsuario())
                            .addValue("PNU_DNI", empleado.getDni());
                    try {
                         simpleJdbcCall.execute(in);
                      } catch (Exception e) {
                         e.printStackTrace();
                         throw e;
                    }
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }*/
    
    @Override
    public String getNextCoEmpleado(){
        String vReturn = "1";
        //Obtiene codigo de empleado
        String sql = "SELECT lpad(to_char(to_number(MAX(CEMP_CODEMP))+1),5,'0') coEmpleado FROM RHTM_PER_EMPLEADOS";        
        try {
            vReturn = this.jdbcTemplate.queryForObject(sql, String.class);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
}
