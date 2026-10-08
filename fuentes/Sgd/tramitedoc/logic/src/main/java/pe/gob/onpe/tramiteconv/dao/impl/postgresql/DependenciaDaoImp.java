/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import org.springframework.stereotype.Repository;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.EmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.LocalDepBean;
import pe.gob.onpe.tramitedoc.dao.DependenciaDao;

/**
 *
 * @author ECueva
 */
@Repository("dependenciaDao")
public class DependenciaDaoImp extends SimpleJdbcDaoBase implements DependenciaDao{

    @Override
    public List<DependenciaBean> getAllDependencia(boolean esAdmin,String codDependencia) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE_DEPENDENCIA, CO_DEPENDENCIA FROM IDOSGD.RHTM_DEPENDENCIA WHERE ");
        if(!esAdmin){
            sql.append(" CO_DEPENDENCIA='");
            sql.append(codDependencia);
            sql.append("'");
            sql.append(" AND");
        }
        sql.append(" IN_BAJA='0' ORDER BY 1 ");
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }

    @Override
    public DependenciaBean getDependencia(String coDep) {
        DependenciaBean dependenciaBean=null;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT DE.co_dependencia,de_dependencia,in_baja,co_nivel,\n" +
                "de_corta_depen,co_empleado,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(co_empleado) de_empleado,\n" +
                "de_sigla,co_cargo,IDOSGD.PK_SGD_DESCRIPCION_DE_CARGO(co_cargo) de_cargo,co_tipo_encargatura,\n" +
                "IDOSGD.PK_SGD_DESCRIPCION_DE_DOMINIOS('CO_TIPO_ENC',co_tipo_encargatura) de_tipo_encargatura,\n" +
                "de_cargo_completo,co_depen_padre,IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(co_depen_padre) de_depen_padre,\n" +
                "titulo_dep,\n" +
                "ti_dependencia tiDependencia, \n"+
                " de.id_crea ,"
                + "CRE.CEMP_DENOM ||' '|| CRE.CEMP_APEPAT crea_Usuario ,"
                + "TO_CHAR(de.fe_crea,'DD/MM/YYYY HH:MI:SS')  crea_Fecha,"
                + "de.id_act,ACT.CEMP_DENOM ||' '|| ACT.CEMP_APEPAT act_Usuario ,"
                + "TO_CHAR(de.fe_act,'DD/MM/YYYY HH:MI:SS')  act_Fecha , "
                + "DE.DE_DESCRIP co_Observacion "+
                
                "FROM IDOSGD.RHTM_DEPENDENCIA DE \n" +
                "LEFT JOIN IDOSGD.RHTM_PER_EMPLEADOS CRE ON cre.cemp_codemp=de.id_crea LEFT JOIN IDOSGD.RHTM_PER_EMPLEADOS ACT ON ACT.cemp_codemp=de.id_act "+
                "WHERE DE.CO_DEPENDENCIA=?");
        try {
            dependenciaBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class),
                    new Object[]{coDep});            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return dependenciaBean;
    }

    @Override
    public List<DependenciaBean> getDependenciaHijo(String coDepPadre) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT \n" +
                    "co_dependencia, \n" +
                    "de_dependencia, \n" +
                    "in_baja, \n" +
                    "co_nivel, \n" +
                    "de_corta_depen, \n" +
                    "co_depen_padre, \n" +
                    "co_empleado,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(co_empleado) de_empleado, \n" +
                    "ti_dependencia, \n" +
                    "de_sigla, \n" +
                    "co_cargo, \n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_CARGO(co_cargo) de_cargo,\n" +
                    "co_tipo_encargatura, \n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_DOMINIOS('CO_TIPO_ENC',co_tipo_encargatura) de_tipo_encargatura,\n" +
                    "co_sec_dep, \n" +
                    "titulo_dep, \n" +
                    "de_cargo_completo \n" +
                    "FROM IDOSGD.RHTM_DEPENDENCIA \n" +
                    "where co_depen_padre = ? \n" +
                    "order by co_dependencia");
        
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class),new Object[]{coDepPadre});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public List<DependenciaBean> getBuscaDependencia(String busDep, String busTipo) {
        StringBuffer sql = new StringBuffer();
        String sqlDep = " and ti_dependencia='"+busTipo+"' ";
        if(busTipo.equals("2")){
            sqlDep = "";//no aplica filtro 2=todos
        }
        sql.append("SELECT \n" +
                    "co_dependencia, \n" +
                    "de_dependencia, \n" +
                    "in_baja, \n" +
                    "co_nivel, \n" +
                    "de_corta_depen, \n" +
                    "co_depen_padre, \n" +
                    "co_empleado,\n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(co_empleado) de_empleado, \n" +
                    "ti_dependencia, \n" +
                    "de_sigla, \n" +
                    "co_cargo, \n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_CARGO(co_cargo) de_cargo,\n" +
                    "co_tipo_encargatura, \n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_DE_DOMINIOS('CO_TIPO_ENC',co_tipo_encargatura) de_tipo_encargatura,\n" +
                    "co_sec_dep, \n" +
                    "titulo_dep, \n" +
                    "de_cargo_completo \n" +
                    "FROM IDOSGD.RHTM_DEPENDENCIA \n" +
                    "where de_dependencia like '%'||?||'%' \n" +
                    sqlDep+
                    "order by ti_dependencia , co_dependencia ");
        List<DependenciaBean> list = new ArrayList<DependenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DependenciaBean.class),new Object[]{busDep});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public String updDependencia(DependenciaBean dep,String coDepAn, String pTipoAnt){
        // tipo INSTITUCION => COMITE ESPECIAL, entonces generar MAX()+1 al 
        if(pTipoAnt.equals("0") && dep.getTiDependencia().equals("1")){ 
            String sql1 = "(SELECT lpad(cast(cast (MAX(CO_DEPENDENCIA) as int)+1 as text),5,'0') FROM IDOSGD.RHTM_DEPENDENCIA WHERE TI_DEPENDENCIA='1')";
            String coDep = this.jdbcTemplate.queryForObject(sql1, String.class);
            dep.setCoDependencia(coDep);
        }
        String vReturn = "ERROR";
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE IDOSGD.RHTM_DEPENDENCIA SET\n" +
                    "CO_DEPENDENCIA='"+dep.getCoDependencia()+"' ,"
                    +"DE_DEPENDENCIA=?,IN_BAJA=?,CO_NIVEL=?,DE_CORTA_DEPEN=?,\n" +
                    "CO_DEPEN_PADRE=?,CO_EMPLEADO=?,FE_ACT=now(),ID_ACT=?,\n" +
                    "DE_SIGLA=?,CO_CARGO=?,CO_TIPO_ENCARGATURA=?,TITULO_DEP=?,\n" +
                    "DE_CARGO_COMPLETO=?,TI_DEPENDENCIA=?,DE_DESCRIP=? \n" +
                    "WHERE CO_DEPENDENCIA=?");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{dep.getDeDependencia(),dep.getInBaja(),dep.getCoNivel(),
            dep.getDeCortaDepen(),dep.getCoDepenPadre(),dep.getCoEmpleado(),dep.getIdAct(),dep.getDeSigla(),dep.getCoCargo(),
            dep.getCoTipoEncargatura(),dep.getTituloDep(),dep.getDeCargoCompleto(),dep.getTiDependencia(),dep.getCoObservacion(),coDepAn});
            //vReturn = "OK";
            vReturn = dep.getCoDependencia();
        } catch (DuplicateKeyException con) {
            vReturn = "DUPLICADO";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
    @Override
    public String insDependencia(DependenciaBean dep){
        if(dep.getTiDependencia().equals("1")){ 
            String sql1 = "(SELECT lpad(cast(cast(COALESCE(MAX(CO_DEPENDENCIA),'0') as int)+1 as text),5,'0') FROM IDOSGD.RHTM_DEPENDENCIA WHERE TI_DEPENDENCIA='1')";
            String coDep = this.jdbcTemplate.queryForObject(sql1, String.class);
            dep.setCoDependencia(coDep);
        }
        String vReturn = "ERROR";
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO IDOSGD.RHTM_DEPENDENCIA(\n" +
                "CO_DEPENDENCIA,DE_DEPENDENCIA,IN_BAJA,CO_NIVEL,DE_CORTA_DEPEN,\n" +
                "CO_DEPEN_PADRE,CO_EMPLEADO,TI_DEPENDENCIA,FE_CREA,ID_CREA,FE_ACT,ID_ACT,\n" +
                "DE_SIGLA,CO_CARGO,CO_TIPO_ENCARGATURA,TITULO_DEP,DE_CARGO_COMPLETO,DE_DESCRIP)\n" +
                "VALUES(?,?,'0',?,?,\n" +
                "?,?,?,now(),?,now(),?,\n" +
                "?,?,?,?,?,?)");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{dep.getCoDependencia(),dep.getDeDependencia(),dep.getCoNivel(),
            dep.getDeCortaDepen(),dep.getCoDepenPadre(),dep.getCoEmpleado(),dep.getTiDependencia(),dep.getIdAct(),dep.getIdAct(),
            dep.getDeSigla(),dep.getCoCargo(),dep.getCoTipoEncargatura(),dep.getTituloDep(),dep.getDeCargoCompleto(),dep.getCoObservacion()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Código Dependencia Duplicado.";
            //con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public String insLocalDependencia(LocalDepBean localDep){
        String vReturn = "ERROR";
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO IDOSGD.SITM_LOCAL_DEPENDENCIA(CO_DEP,CO_LOC,NU_CORRELATIVO,ES_ELI,\n" +
                    "CO_USE_CRE,FE_USE_CRE,CO_USE_MOD,FE_USE_MOD)\n" +
                    "VALUES(?,?,cast(? as int),'0',?,now(),?,now())");        
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{localDep.getCoDep(),localDep.getCoLoc(),localDep.getNuCorr(),
            localDep.getCoUseMod(),localDep.getCoUseMod()});
            vReturn = "OK";
        }catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public LocalDepBean getLocalDepBean(String coDep){
        LocalDepBean localDepBean=null;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CO_DEP,CO_LOC,NU_CORRELATIVO NUCORR,ES_ELI FROM IDOSGD.SITM_LOCAL_DEPENDENCIA\n" +
                    "WHERE CO_DEP=?");
        
        try {
            localDepBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(LocalDepBean.class),
                    new Object[]{coDep});            
        } catch (EmptyResultDataAccessException e) {
            localDepBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return localDepBean;         
    }
    
    @Override
    public String delLocalDependencia(String coDep){
        String vReturn = "ERROR";
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM IDOSGD.SITM_LOCAL_DEPENDENCIA\n" +
                    "WHERE CO_DEP=?");        
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{coDep});
            vReturn = "OK";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }    
    
    @Override
    public String insEmpDependencia(String coEmp,String coDep){
        String vReturn = "ERROR";
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO IDOSGD.TDTX_DEPENDENCIA_EMPLEADO(CO_DEP,CO_EMP,ES_EMP)\n" +
                    "VALUES(?,?,'0')");        
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{coDep,coEmp});
            vReturn = "OK";
        }catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }
    
    @Override
    public String delEmpDependencia(String coDep){
        String vReturn = "ERROR";
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM IDOSGD.TDTX_DEPENDENCIA_EMPLEADO\n" +
                    "WHERE CO_DEP=?");        
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{coDep});
            vReturn = "OK";
        }catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }
        return vReturn;
    }   
    
    @Override
    public List<EmpleadoBean> getLsEmpDepen(String coDep) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CO_EMP CEMP_CODEMP,ES_EMP ESTADO,IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(CO_EMP) NOMBRE\n" +
                    "FROM IDOSGD.TDTX_DEPENDENCIA_EMPLEADO\n" +
                    "WHERE CO_DEP=?");
        
        List<EmpleadoBean> list = new ArrayList<EmpleadoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(EmpleadoBean.class),new Object[]{coDep});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }    
}
