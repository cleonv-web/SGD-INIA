package pe.gob.onpe.autentica.dao.impl.postgresql;

import org.springframework.jdbc.core.*;
import pe.gob.onpe.autentica.dao.UsuarioDao;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.model.UsuarioAcceso;
import pe.gob.onpe.libreria.util.Mensaje;

import javax.sql.DataSource;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import pe.gob.onpe.autentica.model.DatosUsuario;
import pe.gob.onpe.autentica.model.DatosUsuarioLog;
import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;


public class UsuarioDaoImpl implements UsuarioDao {
    private JdbcTemplate jdbcTemplate;

    public UsuarioDaoImpl(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

    @Override
    public void getRptaIdentificacion(Mensaje msg, Usuario usuario, boolean isEncripted) throws Exception {
        if (isEncripted) {
            desencripta(msg, usuario);
        }
        
        if(msg==null){
            msg = new Mensaje();
        }
        
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "cemp_codemp, \n" +
                    "cclave de_Password, TO_DATE(TO_CHAR(DFE_MOD_CLAVE,'YYYY-MM-DD'),'YYYY-MM-DD') FE_MOD_CLAVE, coalesce(ES_USUARIO,'N') ES_ACTIVO, TO_DATE(TO_CHAR(CURRENT_TIMESTAMP,'YYYY-MM-DD'),'YYYY-MM-DD') FE_ACTUAL,\n" +                
                    "DFEC_MOD D_FEC_MOD, CURRENT_TIMESTAMP FULL_FECHA_ACTUAL, coalesce(NU_INTENTO,0) + 1 NRO_INTENTO,IN_AD as inAD \n" +
                    "from IDOSGD.seg_usuarios1 \n" +
                    "where cod_user = ?");
        
        DatosUsuario datosUsuario =null;
                
        try {
            datosUsuario= this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DatosUsuario.class), usuario.getCoUsuario());
            usuario.setCempCodemp(datosUsuario.getCempCodemp());
            usuario.setInAD(datosUsuario.getInAD());
            String esUsuario=datosUsuario.getEsActivo();
            boolean userBloqMaxIntentos = false;
            if(esUsuario!=null&&(esUsuario.equals("A")||esUsuario.equals("N")||esUsuario.equals("M"))){
                boolean passOk = false;
                //para el caso de migracion de contraseña fuerte.
                if(esUsuario.equals("M")){
                    usuario.setDePassword(usuario.getDePassword().toUpperCase());
                    datosUsuario.setEsActivo("A");
                }
                 if(usuario.getInAD().equals("1"))
                {
                   msg.setCoRespuesta("2829");
                    msg.setDeRespuesta("Validación por Active Directory");
                }
                else
                {                            
                    String dePass=Utility.getInstancia().cifrar(usuario.getDePassword(),ConstantesSec.SGD_SECRET_KEY_PASSWORD);
                    if(dePass.equals(datosUsuario.getDePassword())){ 
                        passOk=true;
                        usuario.setFeModClave(datosUsuario.getFeModClave());
                        usuario.setEsUsuario(datosUsuario.getEsActivo());
                        usuario.setFeActual(datosUsuario.getFeActual());
                        msg.setCoRespuesta("00");
                        msg.setDeRespuesta("Usuario Valido");
                    }else{
                        msg.setCoRespuesta("005");
                        msg.setDeRespuesta("El Usuario o la Contraseña es incorrecta");                   
                    }
                    if(esUsuario.equals("A")){
                        if(passOk){
                            this.desbloquearUsuario(usuario.getCoUsuario());
                        }else{
                            if(datosUsuario.getNroIntento()==10){
                                userBloqMaxIntentos=true;
                            }
                            this.incrementarNumeroIntentos(usuario.getCoUsuario());
                        }
                    }
                }
                  
            }else if(esUsuario!=null && esUsuario.equals("I")){
                userBloqMaxIntentos=true;
            }else{
                msg.setCoRespuesta("005");
                msg.setDeRespuesta("Usuario Bloqueado.");                
            }
            if(userBloqMaxIntentos){
                usuario.setFullFechaActual(datosUsuario.getFullFechaActual());
                usuario.setdFecMod(datosUsuario.getdFecMod());                
                msg.setCoRespuesta("009");
                //msg.setDeRespuesta("Usuario bloqueado por exceder el número máximo de intentos.");                   
                msg.setDeRespuesta("Usuario bloqueado por máximo de intentos.");                   
            }
        }catch (EmptyResultDataAccessException e){
            msg.setCoRespuesta("005");
            //msg.setDeRespuesta("Usuario no existe");
              msg.setDeRespuesta("El Usuario o la Contraseña es incorrecta");
        } catch(Exception e) {
            msg.setCoRespuesta("5001");
            msg.setDeRespuesta("Error interno de base de datos, no es posible iniciar session");
            throw e;
        }
        
    }

    @Override
    public String getRptaAplicativo(Mensaje msg, Usuario usuario, String coAplicativo)  {
        String vret="NO";
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT coalesce(ES_ADMIN,'0') es_admin , coalesce(ES_ACTIVO,'0') es_activo \n" +
                   " FROM IDOSGD.SEG_USER_APLICA\n" +
                   " WHERE COD_USER = ? \n" +
                   " AND COD_APLICA = ? ");
        
        DatosUsuario datosUsuario =null;
                
        try {
            datosUsuario= this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DatosUsuario.class), usuario.getCoUsuario(),coAplicativo );
            if (datosUsuario !=null){
                if (datosUsuario.getEsActivo().equals("1")){
                    vret="OK";
                    msg.setCoRespuesta("00");
                    msg.setDeRespuesta("Usuario Valido");
                    if (datosUsuario.getEsAdmin().equals("1")){
                        usuario.setInAdmin("1");
                    }else{
                        usuario.setInAdmin("0");
                    }
                }else{
                    msg.setCoRespuesta("005");
                    msg.setDeRespuesta("Usuario no tiene accesos al aplicativo");
                }
            }
        }catch (EmptyResultDataAccessException e){
            msg.setCoRespuesta("005");
            msg.setDeRespuesta("Usuario no tiene accesos al aplicativo");
        } catch(Exception e) {
            msg.setCoRespuesta("5001");
            msg.setDeRespuesta("Error interno de base de datos, no es posible iniciar session");
        }
        
        return(vret);
        
    }
    
    
    
    @Override
    public void getModificaClave(Mensaje msg, Usuario usuario) throws Exception {
        StringBuffer sql = new StringBuffer();
        sql.append(" update IDOSGD.seg_usuarios1 \n" +
                    "set cclave = ? , dfec_mod = CURRENT_TIMESTAMP , cuser_mod = ?, DFE_MOD_CLAVE=CURRENT_TIMESTAMP, ES_USUARIO=? \n" +                
                    "where cod_user = ?");
        try{
            int nAct= this.jdbcTemplate.update(sql.toString(), new Object[]{
                            usuario.getDePasswordNuevo(),usuario.getCoUsuario(),usuario.getEsUsuario(),usuario.getCoUsuario()});            
            
            if (nAct>0){
                msg.setCoRespuesta("00");
                msg.setDeRespuesta("Su contraseña ha sido actualizada correctamente");
            }else{
                msg.setCoRespuesta("5002");
                msg.setDeRespuesta("Error en datos, Intente Nuevamente");
            }
            
        }catch(Exception e){
            msg.setCoRespuesta("5001");
            msg.setDeRespuesta("Error en base de datos, no es posible modificar clave");
            e.printStackTrace();
            throw e;
        }
    }

    @Override
    public void cerrarSesionAplicativo(Usuario usuario, String coAplicativo) throws Exception {
    }

    @Override
    public void getParametrosGlobales(Mensaje msg, Usuario usuario) throws Exception {
        if(msg==null){
            msg = new Mensaje();
        }
        StringBuffer sql = new StringBuffer();
        sql.append("select " 
               +" cemp_codemp cemp_codemp, "
               +" cemp_nu_dni nu_dni, "
               +" cemp_apepat de_apellido_paterno, "
               +" cemp_apemat de_apellido_materno, "
               +" cemp_denom de_prenombres, "
               +" cemp_co_depend co_dep, " 
               +" cemp_co_local co_local "
               +" from IDOSGD.rhtm_per_empleados "
               +" where cemp_codemp  = ? "                
               +" AND CEMP_INDBAJ='1'");

        
        Usuario usr=null;
        try{
            usr= this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(Usuario.class), usuario.getCempCodemp());
            usuario.setNuDni(usr.getNuDni());
            usuario.setDeApellidoPaterno(usr.getDeApellidoPaterno());
            usuario.setDeApellidoMaterno(usr.getDeApellidoMaterno());
            usuario.setDePrenombres(usr.getDePrenombres());
            //usuario.setCoDep(usr.getCoDep());
            usuario.setCoLocal(usr.getCoLocal());
        }catch (EmptyResultDataAccessException e){
            msg.setCoRespuesta("007");
            msg.setDeRespuesta("Empleado fue dado de \"BAJA\".");
        }catch(Exception e){
            msg.setCoRespuesta("5003");
            msg.setDeRespuesta("Error Interno, la instruccion SQL contiene errores");
            throw e;
        }
    }

    @Override
    public List<UsuarioAcceso> getPermisosUsuario(Usuario usuario, String coAplicativo) throws Exception {
        StringBuffer sql = new StringBuffer();
        List<UsuarioAcceso> list = new ArrayList<UsuarioAcceso>();
        try{
            sql.append("SELECT substr(cod_opc,1,3) co_modulo, cod_opc co_Opcion\n" +
                        "FROM IDOSGD.seg_user_aplica_opc\n" +
                        "where cod_user= ? \n" +
                        "and cod_aplica= ? \n" +
                        "and es_habilitado='1'\n" +
                        "group by cod_opc \n" +
                        "order by 1,2 ");
            
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UsuarioAcceso.class), new Object[]{usuario.getCoUsuario(),coAplicativo});
        }catch(Exception e){
            list = null;
            e.printStackTrace();
        }
        
        return list;
    }

    @Override
    public void desencripta(Mensaje msg, Usuario usuario) {
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT fn_desencripta('");
        sql.append(usuario.getNuDni());
        sql.append("','Onpe01') as NU_DNI_USUARIO, ");
        sql.append(" fn_desencripta('");
        sql.append(usuario.getDePassword());
        sql.append("','Onpe01') as DE_PASSWORD FROM DUAL ");
        Usuario usr=null;
        try{
            usr= this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(Usuario.class));
            usuario.setNuDni(usr.getNuDni());
            usuario.setDePassword(usr.getDePassword());
        }catch(Exception e){
            msg.setCoRespuesta("5004");
            msg.setDeRespuesta("Error Interno, la instruccion SQL contiene errores");
        }
    }

    @Override
    public String encriptaDato(Mensaje msg, String pdato ) {
        String vdato = null;
        StringBuffer sql = new StringBuffer();
        sql.append(" SELECT fn_desencripta('");
        sql.append(pdato);
        sql.append("','Onpe01') as DE_DATO ");
        sql.append(" FROM DUAL ");
        try{
            vdato= this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(String.class));
        }catch(Exception e){
            msg.setCoRespuesta("5004");

            msg.setCoRespuesta("Error Interno, la instruccion SQL contiene errores");
        }
        return vdato;
    }

    
    @Override
	public DatosUsuario getDepUsuario(String coUsuario){
        StringBuffer sql = new StringBuffer();
        sql.append("select B.CEMP_CODEMP, C.CO_DEPENDENCIA CO_DEP, C.DE_DEPENDENCIA DE_DEP,A.IN_AD as inAD \n" +
                    "from IDOSGD.SEG_USUARIOS1 A, IDOSGD.RHTM_PER_EMPLEADOS B, IDOSGD.RHTM_DEPENDENCIA C\n" +
                    "WHERE A.COD_USER = ? \n" +
                    "AND A.CEMP_CODEMP = B.CEMP_CODEMP\n" +
                    "AND B.CEMP_EST_EMP ='1' \n" +
                    "AND B.CO_DEPENDENCIA = C.CO_DEPENDENCIA ");

        DatosUsuario usuDep = new DatosUsuario();

        try {
            usuDep = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DatosUsuario.class), new Object[]{coUsuario});
        } catch (EmptyResultDataAccessException e) {
            usuDep = null;
        } catch (Exception e) {
            usuDep = null;
            e.printStackTrace();
        }
        return usuDep;
    }
    
    @Override
    public List<DatosUsuarioLog> getHistorialClave(String coUsuario){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT COD_USER, NU_CORR, DFE_INI, VCLAVE, VUS_CRE, CESTADO FROM IDOSGD.SEG_USUARIOS_LOG\n" +
                    "WHERE COD_USER=?\n" +
                    "ORDER BY NU_CORR DESC\n" +
                    "LIMIT 3");                

        List<DatosUsuarioLog> list = null;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DatosUsuarioLog.class), new Object[]{coUsuario});
        }catch(Exception e){
            list = null;
            e.printStackTrace();
        }
        return list;            
    }

    @Override
    public String getNroSgtCorrHistorialClave(String coUsuario){
        String vReturn = "1";
        try {
            StringBuffer sql = new StringBuffer();
            sql.append("SELECT coalesce(MAX(NU_CORR),0) + 1 FROM IDOSGD.SEG_USUARIOS_LOG WHERE COD_USER=?");

            vReturn = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{coUsuario});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "1";
        }
        return vReturn;            
    }    

    @Override
    public String insHistorialClave(DatosUsuarioLog data){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.SEG_USUARIOS_LOG\n" +
                    " (COD_USER, NU_CORR, DFE_INI, VCLAVE, VUS_CRE, DFE_CRE, CESTADO, CO_EMP, DE_IPPC, DE_NAMEPC, DE_USERPC)\n" +
                    "VALUES\n" +
                    " (?, cast(? as int), CURRENT_TIMESTAMP, ?, ?, CURRENT_TIMESTAMP, '1', ?, ?, ?, ?)");
                    //" (?, ?, CURRENT_TIMESTAMP, ?, ?, CURRENT_TIMESTAMP, '1', ?, ?, ?, ?)");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{data.getCodUser(),data.getNuCorr(),data.getvClave(),data.getCodUser(),
            data.getCoEmp(),data.getDeIppc(),data.getDeNamepc(),data.getDeUserpc()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado INSERT SEG_USUARIOS_LOG.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;             
    }
    

    @Override
    public String insAccesoLog(DatosUsuarioLog data){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.SEG_USUARIOS_ACCESO\n" +
                    " (COD_USER, DFE_ACCESO, CESTADO, CO_EMP, DE_IPPC, DE_NAMEPC, DE_USERPC, VUS_CRE, DFE_CRE, IN_SUCCESS)\n" +
                    "VALUES\n" +
                    " (?, CURRENT_TIMESTAMP, '1', ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, ?)");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{data.getCodUser(),data.getCoEmp(),data.getDeIppc(),data.getDeNamepc(),
            data.getDeUserpc(),data.getCodUser(), data.getSuccess()});
            vReturn = "OK";
        }catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;             
    }
    
    private String incrementarNumeroIntentos(String coUsuario){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("UPDATE IDOSGD.SEG_USUARIOS1 SET \n" +
                    "NU_INTENTO = coalesce(NU_INTENTO,0) + 1,\n" +
                    "ES_USUARIO=CASE WHEN NU_INTENTO=4 THEN 'I' ELSE ES_USUARIO END,\n" +
                    "CUSER_MOD = ?,\n" +
                    "DFEC_MOD = CURRENT_TIMESTAMP\n" +
                    "WHERE COD_USER=?");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUsuario, coUsuario});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;
    }
    
    @Override
    public String desbloquearUsuario(String coUsuario){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("UPDATE IDOSGD.SEG_USUARIOS1 SET \n" +
                    "NU_INTENTO = 0,\n" +
                    "ES_USUARIO = 'A',\n" +
                    "CUSER_MOD = ?,\n" +
                    "DFEC_MOD = CURRENT_TIMESTAMP\n" +
                    "WHERE COD_USER=?");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUsuario, coUsuario});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;
    }
     @Override
    public String cambiarEstadoUsuario(String coEmpleado,String estado,String coUsuario){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("UPDATE  IDOSGD.SEG_USUARIOS1 SET \n" +
                    "NU_INTENTO = 0,\n" +
                    "ES_USUARIO = ?,\n" +
                    "CUSER_MOD = ?,\n" +
                    "DFEC_MOD = CURRENT_TIMESTAMP\n" +
                    "WHERE CEMP_CODEMP=?");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{estado,coUsuario, coEmpleado});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;
    }
    @Override
    public String resetPassword(String coUse, String clave, String coUseMod){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("UPDATE IDOSGD.SEG_USUARIOS1 SET \n" +
                    "NU_INTENTO = 0,\n" +
                    "ES_USUARIO = 'N',\n" +
                    "CCLAVE = ?,\n" +
                    "CUSER_MOD = ?,\n" +
                    "DFEC_MOD = CURRENT_TIMESTAMP\n" +
                    "WHERE COD_USER=?");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{clave, coUseMod, coUse});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;
    }
    
    @Override
    public String delUserAplicaOpc(String coUse){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("DELETE FROM IDOSGD.seg_user_aplica_opc op \n" +
                        "WHERE op.cod_user = ? \n" +
                        "AND op.cod_aplica = '9'");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUse});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;        
    }
    
    @Override
    public String delUserAplica(String coUse){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("DELETE FROM IDOSGD.seg_user_aplica op \n" +
                        "WHERE cod_aplica = '9' \n" +
                        "AND cod_user = ?");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUse});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;          
    }   
    
    @Override
    public String delUser(String coUse){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("DELETE FROM IDOSGD.seg_usuarios1\n" +
                        "where cod_user= ?");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUse});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;            
    }
  
    @Override
    public String delClaveUserHis(String coUse){
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("DELETE FROM IDOSGD.SEG_USUARIOS_LOG\n" +
                        "WHERE COD_USER= ?");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{coUse});
            vReturn = "OK";
        }catch (Exception e) {
            vReturn = "NO_OK";
            //e.printStackTrace();
        }        
        return vReturn;          
    }     
}
