/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.bean.AdmEmpleadoBean;
import pe.gob.onpe.tramitedoc.bean.SiElementoBean;
import pe.gob.onpe.tramitedoc.dao.CredencialUserDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author USUARIO
 */
@Repository("CredencialUserDao")
public class CredencialUserDaoImp extends SimpleJdbcDaoBase implements CredencialUserDao{

    @Override
    public List<AdmEmpleadoBean> getListaBusqUsuario(String indicador, String estado) throws Exception {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        sql.append("  SELECT E.CEMP_CODEMP co_Empleado,  U.COD_USER usuario, E.CEMP_APEPAT ||' '||  E.CEMP_DENOM nombres ,E.CEMP_EST_EMP estado ");
        sql.append("  FROM idosgd.RHTM_PER_EMPLEADOS E LEFT JOIN idosgd.SEG_USUARIOS1 U ON U.CEMP_CODEMP= E.CEMP_CODEMP ");
        sql.append("  LEFT JOIN idosgd.RHTM_PER_EMPLEADOS CRE ON cre.cemp_codemp=e.cemp_id_crea LEFT JOIN idosgd.RHTM_PER_EMPLEADOS ACT ON ACT.cemp_codemp=e.cemp_id_act ");
        sql.append("  LEFT JOIN IDOSGD.si_mae_local LO ON LO.CCOD_LOCAL=E.CEMP_CO_LOCAL ");
        sql.append(" WHERE E.CEMP_EST_EMP = :pestado ");
        objectParam.put("pestado",estado);
        sql.append(" AND U.COD_USER  NOT IN(SELECT cele_desele ");
        sql.append(" FROM idosgd.SI_ELEMENTO WHERE ctab_codtab = 'CO_DNI_PIDE' AND nele_numsec='1') ");
        List<AdmEmpleadoBean> list = new ArrayList<AdmEmpleadoBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AdmEmpleadoBean.class));
        } catch (Exception e) {
            throw e;
        }
        return list;
    }
    
    public List<SiElementoBean> getListaBusqCredencialUser(String estado) throws Exception{
         StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        sql.append(" SELECT ");
        sql.append("  ctab_codtab, ");
        sql.append("  cele_codele, ");
        sql.append("  cele_desele, ");
        sql.append("  nele_numsec ");
        sql.append("  FROM ");
        sql.append(" idosgd.si_elemento WHERE ctab_codtab = 'CO_DNI_PIDE' AND  nele_numsec= :pestado ");
        objectParam.put("pestado",estado);
        List<SiElementoBean> list = new ArrayList<SiElementoBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(SiElementoBean.class));
        } catch (Exception e) {
            throw e;
        }
        return list;
    }
    
    public String updCredencial(SiElementoBean user, String codUsuario){
        String vReturn = "NO_OK";
        String tabla="CO_DNI_PIDE";
        StringBuffer sql  = new StringBuffer();
        sql.append("UPDATE idosgd.SI_ELEMENTO SET ");
        sql.append(" CELE_DESCOR = ? || '|' || ?,");    
        sql.append(" CELE_CODELE2=TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS'), CELE_CODELE3=? ");
        sql.append(" WHERE CTAB_CODTAB = ?");
                
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                user.getNuDni(),user.getCeleDescor(),codUsuario,tabla});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    public String insCredencial(SiElementoBean user, String codUsuario){
        String vReturn = "NO_OK";
        StringBuffer sql  = new StringBuffer();
        
         sql.append("INSERT INTO idosgd.SI_ELEMENTO ");
         sql.append(" (CTAB_CODTAB, CELE_CODELE, CELE_DESELE,CELE_DESCOR, CELE_CODELE2, CELE_CODELE3 )"); //columnas
         sql.append(" VALUES (?, ?, ?, ? || '|' || ?,TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS'),?)");// valores
                
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                 "CO_DNI_PIDE", "0","I",
                user.getNuDni(),user.getCeleDescor(),codUsuario});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    public SiElementoBean getCredencial() {
        SiElementoBean user=null;
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT ");
        sql.append(" ctab_codtab, ");
        sql.append(" cele_codele, ");
        sql.append(" cele_desele, ");
        sql.append(" cele_descor  ");
        sql.append(" FROM ");
        sql.append(" idosgd.si_elemento WHERE ctab_codtab='CO_DNI_PIDE' AND ROWNUM = '1' ");
        
        try {
            user = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(SiElementoBean.class),
                    new Object[]{});
            String credencial = user.getCeleDescor();
            String[] partes = credencial.split("\\|");  // Utilizamos el caracter "|" como delimitador
            // Acceder a las partes individuales
            String DNI = partes[0];  // "75249655"
            String clave = partes[1];  // "claveCifrada"
            // desCifrar la clave
            String clave2 = Utility.getInstancia().descifrar(clave, ConstantesSec.SGD_SECRET_KEY_PASSWORD);
            // actualizamos lo valores 
            user.setNuDni(DNI);
            user.setNuClave(clave2);
        } catch (EmptyResultDataAccessException e) {
            user = null;
        } catch (Exception e) {
            user = null;
            e.printStackTrace();
        }
        return user;
    }
    
     public List<SiElementoBean> getUsuario(String usuario){
         StringBuffer sql = new StringBuffer();
         Map<String, Object> objectParam = new HashMap<String, Object>();
         sql.append("SELECT ");
         sql.append(" ctab_codtab,");
         sql.append(" cele_codele,");
         sql.append(" cele_desele");
         sql.append(" FROM");
         sql.append(" idosgd.si_elemento WHERE ctab_codtab = 'CO_DNI_PIDE' AND ");
         sql.append(" cele_desele = :pusuario AND ");
         objectParam.put("pusuario", usuario);
         sql.append(" cele_codele IS NOT NULL");
         
         List<SiElementoBean> list = new ArrayList<SiElementoBean>();
         //System.out.println("pl: "+sql);
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(),objectParam, BeanPropertyRowMapper.newInstance(SiElementoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
         
         return list;
     }
     
    public List<SiElementoBean> getCredencialIni(){
         StringBuffer sql = new StringBuffer();
         Map<String, Object> objectParam = new HashMap<String, Object>();
         sql.append("SELECT ");
         sql.append(" ctab_codtab,");
         sql.append(" cele_codele,");
         sql.append(" cele_desele");
         sql.append(" FROM");
         sql.append(" idosgd.si_elemento WHERE ctab_codtab = 'CO_DNI_PIDE' ");         
         List<SiElementoBean> list = new ArrayList<SiElementoBean>();
         //System.out.println("pl: "+sql);
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(),objectParam, BeanPropertyRowMapper.newInstance(SiElementoBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
         
         return list;
     }
     
     public String insCredencialUser(SiElementoBean userCredencial, String codUsuario){
         String vReturn = "NO_OK";
         StringBuffer sql = new StringBuffer();
         String geCoCredencial = "";
         
         sql.append("INSERT INTO idosgd.SI_ELEMENTO ");
         sql.append(" (CTAB_CODTAB, CELE_CODELE, CELE_DESELE, NELE_NUMSEC, CELE_DESCOR, CELE_CODELE2, CELE_CODELE3 )"); //columnas
         sql.append(" VALUES (?, ?, ?, ?, ? || '|' || ?,TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS'), ?)");// valores
         
         try {
             // El id de la tabla sera autogenerado sumando 1 al ultimo ID 
             String seq = "SELECT MAX(TO_NUMBER(cele_codele) ) +1  FROM IDOSGD.SI_ELEMENTO WHERE ctab_codtab = 'CO_DNI_PIDE'"; 
             geCoCredencial = (String) this.jdbcTemplate.queryForObject(seq, String.class);
             
             this.jdbcTemplate.update(sql.toString(),new Object[] {
                 "CO_DNI_PIDE",
                 geCoCredencial,
                 userCredencial.getDeUser(),
                 userCredencial.getNeleNumsec(),
                 userCredencial.getNuDni(),
                 userCredencial.getCeleDescor(),
                 codUsuario
             });
             
             vReturn = "OK";
             
         } catch (DuplicateKeyException con) {
            vReturn = "Número de Codigo de proceso duplicado.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
         return vReturn;
     }
     
     public String updCredencialUser(SiElementoBean userCredencial, String codUsuario){
         String vReturn = "NO_OK";
         StringBuffer sql = new StringBuffer();
         sql.append(" UPDATE idosgd.SI_ELEMENTO SET ");
         sql.append(" CELE_CODELE2 = TO_CHAR(SYSDATE, 'DD/MM/YYYY HH24:MI:SS'), CELE_CODELE3=?, "); 
         sql.append(" NELE_NUMSEC = ? ");    
         sql.append(" WHERE CTAB_CODTAB = ? AND CELE_DESELE = ?");
         
         try{
             this.jdbcTemplate.update(sql.toString(), new Object[]{
                codUsuario,userCredencial.getNeleNumsec(),"CO_DNI_PIDE",userCredencial.getDeUser()});
             vReturn="OK";
         } catch(Exception e){
             e.printStackTrace();
         }
         
         return vReturn; 
     }
    
//    public String insCredencial(Usuario user, String codUsuario){
//        String vReturn = "NO_OK";
//        StringBuffer sql    = new StringBuffer();
//        
//        return null;
//    }
    
}
