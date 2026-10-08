/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.CitizenBean;
import pe.gob.onpe.tramitedoc.dao.CiudadanoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.segdi.pide.consultas.bean.ConsultaDniBean;
/**
 *
 * @author RBerrocal
 */
@Repository("ciudadanoDao")
public class CiudadanoDaoImp extends SimpleJdbcDaoBase implements CiudadanoDao {
    @Autowired
    private ApplicationProperties applicationProperties;  
    
    public List<CitizenBean> getCiudadanoList() {
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT NULEM, UBDEP, UBPRV, UBDIS, LTRIM(RTRIM(DEAPP)) AS DEAPP, LTRIM(RTRIM(DEAPM)) AS DEAPM, LTRIM(RTRIM(DENOM)) AS DENOM, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO(ubdep)) NO_DEP, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_PROVINCIA(ubdep,ubprv)) NO_PRV, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DISTRITO(ubdep,ubprv,ubdis)) NO_DIS ");
        sql.append("FROM IDOSGD.TDTX_ANI_SIMIL WITH (NOLOCK) ");
        List<CitizenBean> list = new ArrayList<CitizenBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(CitizenBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<CitizenBean> getCiudadanoBusqList(String busCiudDocumento, String busCiudApPaterno, String busCiudApMaterno, String busCiudNombres) {
        
        StringBuffer sql = new StringBuffer();
        //Object[] objectParam = null;
        List<CitizenBean> list = new ArrayList<CitizenBean>();
        Map<String, Object> objectParam = new HashMap<String, Object>();    
        
        sql.append("SELECT TOP ").append(applicationProperties.getTopRegistrosConsultas());
        sql.append(" * FROM ( ");
        sql.append("SELECT NULEM, UBDEP, UBPRV, UBDIS, LTRIM(RTRIM(DEAPP)) AS DEAPP, LTRIM(RTRIM(DEAPM)) AS DEAPM, LTRIM(RTRIM(DENOM)) AS DENOM, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO(ubdep)) NO_DEP, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_PROVINCIA(ubdep,ubprv)) NO_PRV, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DISTRITO(ubdep,ubprv,ubdis)) NO_DIS,DEDOMICIL,DEEMAIL,DETELEFO ");
        sql.append("FROM IDOSGD.TDTX_ANI_SIMIL ) A WHERE 1=1 ");     
        
        String parApPaterno = "%" + busCiudApPaterno + "%";
        String parApMaterno = "%" + busCiudApMaterno + "%";
        String parApNombre = "%" + busCiudNombres + "%";
        
        if (!busCiudDocumento.equals("")) {
            sql.append(" AND A.NULEM = :pCiudDoc");
            objectParam.put("pCiudDoc", busCiudDocumento);   
        }
        if (!busCiudApPaterno.equals("")) {            
            sql.append(" and UPPER(A.DEAPP) like :pCiudApPat");
            objectParam.put("pCiudApPat", parApPaterno);  
        }
        if (!busCiudApMaterno.equals("")) {             
            sql.append(" and UPPER(A.DEAPM) like :pCiudApMat");
            objectParam.put("pCiudApMat", parApMaterno);
        }
        if (!busCiudNombres.equals("")) {                     
            sql.append(" and UPPER(A.DENOM) like :pCiudNom");
            objectParam.put("pCiudNom", parApNombre);            
        }     
        sql.append(" order by DEAPP");
        
  
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(CitizenBean.class));            
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    public ProcessResult<List<CitizenBean>> getCiudadanoBusqList(String busCiudDocumento, String busCiudApPaterno, String busCiudApMaterno, String busCiudNombres, FiltroPaginate paginate) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();    
        
        sql.append("SELECT A.* FROM ( ");
        sql.append("SELECT NULEM, UBDEP, UBPRV, UBDIS, LTRIM(RTRIM(DEAPP)) AS DEAPP, LTRIM(RTRIM(DEAPM)) AS DEAPM, LTRIM(RTRIM(DENOM)) AS DENOM, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO(ubdep)) NO_DEP, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_PROVINCIA(ubdep,ubprv)) NO_PRV, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DISTRITO(ubdep,ubprv,ubdis)) NO_DIS,DEDOMICIL,DEEMAIL,DETELEFO, ");
        sql.append(" ROW_NUMBER() OVER (ORDER BY DEAPP) AS fila, COUNT(1) OVER() AS filasTotal ");
        sql.append(" FROM IDOSGD.TDTX_ANI_SIMIL  WHERE 1=1 ");     
        
        //String parApPaterno = "%" + busCiudApPaterno + "%";
        //String parApMaterno = "%" + busCiudApMaterno + "%";
        //String parApNombre = "%" + busCiudNombres + "%";
        
        if (busCiudDocumento!=null&& busCiudDocumento.trim().length()>0) {
            sql.append(" AND NULEM = :pCiudDoc");
            objectParam.put("pCiudDoc", busCiudDocumento);   
        }
        if (busCiudApPaterno!=null && busCiudApPaterno.trim().length()>0) {            
            sql.append(" AND UPPER(DEAPP) like '%'+:pCiudApPat+'%'");
            objectParam.put("pCiudApPat", busCiudApPaterno);  
        }
        if (busCiudApMaterno!=null && busCiudApMaterno.trim().length()>0) {             
            sql.append(" AND UPPER(DEAPM) like '%'+:pCiudApMat+'%'");
            objectParam.put("pCiudApMat", busCiudApMaterno);
        }
        if (busCiudNombres!=null && busCiudNombres.trim().length()>0) {                     
            sql.append(" AND UPPER(DENOM) like '%'+:pCiudNom+'%'");
            objectParam.put("pCiudNom", busCiudNombres);            
        }     
       // sql.append(" order by DEAPP");
        sql.append(" ) A ");
        sql.append("  WHERE fila BETWEEN " +paginate.getPaginaDesde()+" AND "+ paginate.getPaginaHasta() +" ");
        ProcessResult<List<CitizenBean>> Result = new  ProcessResult<List<CitizenBean>>();
        List<CitizenBean> list = new ArrayList<CitizenBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(CitizenBean.class));
            Result.setResult(list);
            Result.setIsSuccess(true);
        } catch (EmptyResultDataAccessException e) {
            Result.setIsSuccess(false);
            Result.setMessage(e.getMessage());
        } catch (Exception e) {
            Result.setIsSuccess(false);
            Result.setMessage(e.getMessage());
            e.printStackTrace();
        }
        return Result;
    }
    
    public String insCiudadano(CitizenBean ciudadano, String codUsuario) {
        String vReturn      = "NO_OK";
        StringBuffer sql    = new StringBuffer();
        String genCoProceso = "";
        
        sql.append("insert into IDOSGD.TDTX_ANI_SIMIL ");
        sql.append("(NULEM, UBDEP, UBPRV, UBDIS, DEAPP, DEAPM, DENOM, DEDOMICIL, DEEMAIL, DETELEFO) ");
        sql.append("values (?, ?, ?, ?, ?, ?, ?, ?, ?, ?)");

        try {        
            //El campo Estado no se incluye en el bloque del insert pq es un valor default en la tabla
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                ciudadano.getNuLem(), ciudadano.getUbDep(), ciudadano.getUbPrv(), ciudadano.getUbDis(), 
                ciudadano.getDeApp(), ciudadano.getDeApm(),  ciudadano.getDeNom(),  ciudadano.getDeDomicil(),  ciudadano.getDeEmail(),  ciudadano.getDeTelefo()
            });
            
            vReturn = "OK";
            
        } catch (DuplicateKeyException con) {
            vReturn = "Número de documento duplicado.";
            con.printStackTrace();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn; 
    }

    public CitizenBean getCiudadano(String codCiudadano) {
        CitizenBean ciudadanoBean = new CitizenBean();
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT NULEM, UBDEP, UBPRV, UBDIS, LTRIM(RTRIM(DEAPP)) AS DEAPP, LTRIM(RTRIM(DEAPM)) AS DEAPM, LTRIM(RTRIM(DENOM)) AS DENOM, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO(ubdep)) NO_DEP, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_PROVINCIA(ubdep,ubprv)) NO_PRV, ");
        sql.append("(select retorno from IDOSGD.PK_SGD_DESCRIPCION_FU_GET_DISTRITO(ubdep,ubprv,ubdis)) NO_DIS,DEDOMICIL,DEEMAIL,DETELEFO ");
        sql.append("FROM IDOSGD.TDTX_ANI_SIMIL WHERE NULEM = ?");
        
        try {
            ciudadanoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(CitizenBean.class),
                    new Object[]{codCiudadano});
        } catch (EmptyResultDataAccessException e) {
            ciudadanoBean = null;
        } catch (Exception e) {
            ciudadanoBean = null;
            e.printStackTrace();
        }
        
        return ciudadanoBean;
    }

    public String updCiudadano(CitizenBean ciudadano, String codUsuario) {
        String vReturn = "NO_OK";
        
        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.TDTX_ANI_SIMIL set ");
        sql.append("UBDEP=?, UBPRV=?, UBDIS=?, DEAPP=?, DEAPM = ?, DENOM= ?, ");
        sql.append("DEDOMICIL=?, DEEMAIL=?, DETELEFO=?");      
        sql.append(" where NULEM=? ");
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                ciudadano.getUbDep(), ciudadano.getUbPrv(), ciudadano.getUbDis(), ciudadano.getDeApp(), 
                ciudadano.getDeApm(), ciudadano.getDeNom(), ciudadano.getDeDomicil(),  ciudadano.getDeEmail(),  ciudadano.getDeTelefo(), ciudadano.getNuLem()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return vReturn;
    }

    @Override
    public String insPideCiudadano(ConsultaDniBean ciudadano, String codUsuario) {
        String vReturn      = "NO_OK";
        StringBuffer sql    = new StringBuffer();
       
        
        sql.append("insert into IDOSGD.TDTX_ANI_SIMIL ");
        sql.append("(NULEM, DEAPP, DEAPM, DENOM) ");
        sql.append("values (?, ?, ?, ?)");

        try {        
            //El campo Estado no se incluye en el bloque del insert pq es un valor default en la tabla
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                codUsuario, ciudadano.getApPrimer(), ciudadano.getApSegundo(), ciudadano.getPrenombres()
            });
            
            vReturn = "OK";
            
        } catch (DuplicateKeyException con) {
            vReturn = "Número de documento duplicado.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn; 
    }
    
}
