package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.libreria.util.Mensaje;
import pe.gob.onpe.tramitedoc.bean.UsuarioBean;

import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioDepAcceso;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.dao.UsuarioConfigDao;

@Repository("usuarioConfigDao")
public class UsuarioConfigDaoImp extends SimpleJdbcDaoBase implements UsuarioConfigDao {

    @Override
    public UsuarioConfigBean getConfig(Mensaje msg, String cempCodemp, String coDep) {
        if(msg==null){
            msg = new Mensaje();
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select\n" +
                    "replace(a.de_dir_emi,'\\','|') de_dir_emi ,\n" +
                    "a.IN_TIPO_DOC in_tipo_doc ,\n" +
                    "a.CO_EMP cemp_codemp ,\n" +
                    "a.CO_DEP co_dep ,\n" +
                    "PK_SGD_DESCRIPCION.DE_DEPENDENCIA(a.CO_DEP)  de_dep,\n" +
                    "PK_SGD_DESCRIPCION.DE_SIGLA_CORTA(a.CO_DEP)  de_Siglas_Dep,\n" +
//                    "a.CO_LOC co_local,\n" +
//                    "PK_SGD_DESCRIPCION.DE_LOCAL(a.CO_LOC) de_local,\n" +
                    "(SELECT cemp_co_local FROM rhtm_per_empleados WHERE cemp_codemp=a.co_emp) co_local,\n" +
                    "PK_SGD_DESCRIPCION.DE_LOCAL((SELECT cemp_co_local FROM rhtm_per_empleados WHERE cemp_codemp=a.co_emp)) de_local,\n" +
                    "a.DE_DIR_ANE de_Ruta_Anexo,\n" +
                    "a.DE_PIE_PAGINA ,\n" +
                    "a.TI_ACCESO ,\n" +
                    "a.IN_CONSULTA TI_CONSULTA,\n" +
                    "a.IN_MAIL in_Correo,\n" +
                    "a.IN_CARGA_DOC in_Carga_Doc_Mesa_Partes,\n" +
                    "a.IN_FIRMA, \n" +
                    "a.IN_OBS_DOCU in_Obs_Documento,\n" +
                    "a.IN_REVI_DOCU in_Revi_Documento,\n" +
                    "b.in_numero_mp in_numero_mp,\n" +
                    "b.in_mesa_partes in_mesa_partes,\n" +
                    "(SELECT DE_PAR FROM tdtr_parametros WHERE CO_PAR='ID_IMG_PORTADA_SGD') NAME_IMG_PORTADA_SGD,\n" +
                    "(SELECT DE_PAR FROM tdtr_parametros WHERE CO_PAR='DIAS_EXPIRACION_CLAVE') DIAS_EXPIRACION_CLAVE,\n" +                
                    "(SELECT DEP.CO_DEPENDENCIA FROM RHTM_DEPENDENCIA DEP WHERE DEP.IN_MESA_PARTES='1' AND DEP.IN_BAJA='0'\n" +
                    "AND 0 < (SELECT COUNT(1) FROM TDTR_DEPENDENCIA_MP DMP WHERE DMP.CO_DEP=B.CO_DEPENDENCIA AND DMP.ES_ELI='0' AND DMP.IN_MP='1')) CO_DEP_MP,\n" +
                    "a.ti_acceso_mp,\n" +
                    "a.in_consulta_mp ti_consulta_mp,\n" +
                    "a.in_mail_deriv inEmailDeriv \n" +
                    "from  TDTX_CONFIG_EMP a , rhtm_dependencia b\n" +
                    "where a.co_dep= ? \n" +
                    "and a.co_emp=? \n" +
                    "and a.co_dep = b.co_dependencia");

        UsuarioConfigBean usuConfig = new UsuarioConfigBean();

        try {
            usuConfig = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(UsuarioConfigBean.class), new Object[]{coDep, cempCodemp});
            msg.setCoRespuesta("00");
            msg.setDeRespuesta("Usuario Valido");                        
        } catch (EmptyResultDataAccessException e) {
            usuConfig = null;
            msg.setCoRespuesta("00");
            msg.setDeRespuesta("Usuario Valido");                          
        } catch (Exception e) {
            usuConfig = null;
            msg.setCoRespuesta("5001");
            msg.setDeRespuesta("Error interno de BASE DE DATOS.");            
            e.printStackTrace();
        }

        return usuConfig;

    }

    
    @Override
    public String insUsuarioConfingBasico(UsuarioConfigBean usuarioConf) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("insert into TDTX_CONFIG_EMP(\n" +
                    "co_emp, \n" +
                    "co_dep, \n" +
                    "co_loc, \n" +
                    "fe_use_cre, \n" +
                    "fe_use_mod, \n" +
                    "ti_acceso, \n" +
                    "es_reg, \n" +
                    "in_carga_doc, \n" +
                    "in_firma, \n" +
                    "in_tipo_doc, \n" +
                    "in_obs_docu, \n" +
                    "in_revi_docu, \n" +
                    "in_consulta \n" +
                    ")\n" +
                    "values(?,?,'001',sysdate,sysdate,?,'0',?,?,?,?,?,?)");
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                usuarioConf.getCempCodemp(),usuarioConf.getCoDep(),
                usuarioConf.getTiAcceso(),usuarioConf.getInCargaDocMesaPartes(),
                usuarioConf.getInFirma(),usuarioConf.getInTipoDoc(),
                usuarioConf.getInObsDocumento(),usuarioConf.getInReviDocumento(),
                usuarioConf.getTiConsulta() });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String updUsuarioConfing(UsuarioConfigBean usuarioConf) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("update TDTX_CONFIG_EMP set ");
        sql.append("de_dir_emi=?, de_pie_pagina=?, ti_acceso=?, in_carga_doc=?, in_firma=?, in_tipo_doc =?,IN_MAIL_DERIV=?, fe_use_mod=sysdate ");
        sql.append("where co_dep = ? AND co_emp = ? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                usuarioConf.getDeDirEmi(), usuarioConf.getDePiePagina(), usuarioConf.getTiAcceso(),
                usuarioConf.getInCargaDocMesaPartes(), usuarioConf.getInFirma(), usuarioConf.getInTipoDoc(),usuarioConf.getInEmailDeriv(),
                usuarioConf.getCoDep(), usuarioConf.getCempCodemp()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    
    @Override
	public List<UsuarioDepAcceso> getListDepAccesos(String cempCodemp , String coUsuario){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CO_DEPENDENCIA, DE_DEPENDENCIA , de_Corta_Depen \n" +
                    "  FROM RHTM_DEPENDENCIA\n" +
                    " WHERE CO_DEPENDENCIA IN (\n" +
                    "                    SELECT CO_DEPENDENCIA\n" +
                    "                    FROM RHTM_DEPENDENCIA\n" +
                    "                    WHERE IN_BAJA = '0'\n" +
                    "                    AND CO_EMPLEADO = ? \n" +
                    "                    UNION\n" +
                    "                    SELECT CO_DEPENDENCIA \n" +
                    "                      FROM RHTM_PER_EMPLEADOS\n" +
                    "                    WHERE CEMP_CODEMP = ?\n" +
                    "                   UNION\n" +
                    "                   SELECT CO_DEP\n" +
                    "                     FROM TDTX_DEPENDENCIA_EMPLEADO\n" +
                    "                    WHERE CO_EMP = ?\n" +
                    "                      AND ES_EMP = '0'\n" +
                    "                   UNION\n" +
                    "                   SELECT CO_DEP COD_DEP\n" +
                    "                     FROM TDTR_PERMISOS\n" +
                    "                    WHERE CO_USE = ?\n" +
                    "                      and ES_ACT = '0'\n" +
                    "                   MINUS\n" +
                    "                   SELECT CO_DEP COD_DEP\n" +
                    "                     FROM TDTR_PERMISOS\n" +
                    "                    WHERE CO_USE = ?\n" +
                    "                      and ES_ACT = '1')\n" +
                    "   AND IN_BAJA <> '1'\n" +
                    " ORDER BY ti_dependencia ");
        List<UsuarioDepAcceso> list=new ArrayList<UsuarioDepAcceso>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UsuarioDepAcceso.class),
                    new Object[]{cempCodemp,cempCodemp,cempCodemp,coUsuario,coUsuario});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        
        return list;
    }

    @Override
	public List<UsuarioDepAcceso> getListDepTotal(String cempCodemp){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CO_DEPENDENCIA, DE_DEPENDENCIA , de_Corta_Depen \n" +
                    "  FROM RHTM_DEPENDENCIA\n" +
                    " ORDER BY ti_dependencia, CO_DEPENDENCIA");
        List<UsuarioDepAcceso> list=new ArrayList<UsuarioDepAcceso>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UsuarioDepAcceso.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        
        return list;
    }
        
        
    @Override
    public String getCoDepUsuario(String pcoEmp) {
        String vcoDep=null;
        StringBuffer sql = new StringBuffer();
        sql.append("select B.CO_DEPENDENCIA\n"
                + "from RHTM_PER_EMPLEADOS B\n"
                + "WHERE B.CEMP_CODEMP = ? \n"
                + "AND B.CEMP_EST_EMP ='1' ");

        try {
            vcoDep = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pcoEmp});
        } catch (EmptyResultDataAccessException e) {
            vcoDep = null;
        } catch (Exception e) {
            vcoDep = null;
            e.printStackTrace();
        }
        return vcoDep;
    }

    @Override
    public String getTiEncargadoDep(String pcoEmp, String pcoDep) {
        String vcoDep=null;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT nvl(co_tipo_encargatura,'3') \n" +
                    "FROM RHTM_DEPENDENCIA\n" +
                    "WHERE IN_BAJA = '0'\n" +
                    "AND CO_DEPENDENCIA = ?\n" +
                    "AND CO_EMPLEADO = ?");
        try {
            vcoDep = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pcoDep, pcoEmp});
        } catch (EmptyResultDataAccessException e) {
            vcoDep = null;
        } catch (Exception e) {
            vcoDep = null;
            e.printStackTrace();
        }
        return vcoDep;
    }
    
    
    //YUAL
    
    public List<UsuarioBean> getUsuariosList() {
        StringBuffer sql = new StringBuffer();
          
        sql.append("SELECT u.cod_user, s.cdes_user, u.es_activo FROM seg_user_aplica u ");
        sql.append("INNER JOIN seg_usuarios1 s on u.cod_user = s.cod_user ");
        sql.append("WHERE u.es_activo = '1' ");
        sql.append("ORDER BY 1");

        List<UsuarioBean> list = new ArrayList<UsuarioBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UsuarioBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    
    @Override
    public UsuarioBean getUsuarioxCodEmp(String coEmp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoEmp", coEmp);
        sql.append("SELECT u.COD_USER from rhtm_per_empleados e INNER JOIN seg_usuarios1 u "
                + "ON e.CEMP_CODEMP = u.CEMP_CODEMP where e.CEMP_CODEMP = :pCoEmp");
        UsuarioBean usuario = new UsuarioBean();
        try {
            usuario = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(UsuarioBean.class));
        }catch (EmptyResultDataAccessException e) {
            usuario = null;
        }catch (Exception e) {
            usuario = null;
            e.printStackTrace();
        }
        return usuario;        
    }
    
    @Override
    public UsuarioConfigBean getTipAccesoConsulta(String codEmp, String coDep){

        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  TI_ACCESO, IN_CONSULTA TI_CONSULTA  from tdtx_config_emp WHERE co_emp = ? and co_dep = ?");

        UsuarioConfigBean usuConfig = new UsuarioConfigBean();
        try {
            usuConfig = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(UsuarioConfigBean.class), new Object[]{codEmp, coDep});          
        } catch (EmptyResultDataAccessException e) {
            usuConfig = null;                      
        } catch (Exception e) {        
            e.printStackTrace();
        }
        return usuConfig;

    }
    
    @Override
    public String updTipoPermisoUsuario(String codEmp, String codDep, String tipoAcceso, String tipoConsulta ) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("update tdtx_config_emp set TI_ACCESO = ? , IN_CONSULTA = ?  where  co_emp = ? and co_dep = ? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                tipoAcceso, tipoConsulta,codEmp, codDep 
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    
    
}
