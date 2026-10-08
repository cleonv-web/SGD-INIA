/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.util.ArrayList;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.TupaBean;
import pe.gob.onpe.tramitedoc.dao.TupaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
/**
 *
 * @author RBerrocal
 */
@Repository("tupaDao")
public class TupaDaoImp extends SimpleJdbcDaoBase implements TupaDao{

    @Autowired
    private ApplicationProperties applicationProperties;    
    
    public List<TupaBean> getTupaList() {
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT CO_PROCESO, TI_PROCESO, CO_DEP_PRO, DE_NOMBRE, DE_DESCRIPCION, NU_PLAZO, ES_ESTADO, US_CREA_AUDI, ");
        sql.append("to_char(FE_CREA_AUDI,'dd/mm/yyyy') FE_CREA_AUDI, US_MODI_AUDI, to_char(FE_MODI_AUDI,'dd/mm/yyyy') FE_MODI_AUDI ");
        sql.append("FROM IDOSGD.TDTR_PROCESOS_EXP ");
        List<TupaBean> list = new ArrayList<TupaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TupaBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<TupaBean> getTupaBusqList(String busTupaCorto, String busTupaDescripcion) {
        StringBuffer sql = new StringBuffer();
        Object[] objectParam = null;
        
        sql.append("SELECT CO_PROCESO, TI_PROCESO, CO_DEP_PRO, DE_NOMBRE, DE_DESCRIPCION, NU_PLAZO, ES_ESTADO, US_CREA_AUDI, ");
        sql.append("to_char(FE_CREA_AUDI,'dd/mm/yyyy') FE_CREA_AUDI, US_MODI_AUDI, to_char(FE_MODI_AUDI,'dd/mm/yyyy') FE_MODI_AUDI ");
        sql.append("FROM IDOSGD.TDTR_PROCESOS_EXP WHERE TI_PROCESO='1' \n");
        
        if (busTupaCorto != null && !busTupaCorto.equals("")){
            sql.append("AND UPPER(DE_NOMBRE) like '%'||?||'%' ");
            objectParam = new Object[]{busTupaCorto};
            
        }
        else if( busTupaDescripcion != null && !busTupaDescripcion.equals("")) {
            sql.append("AND UPPER(DE_DESCRIPCION) like '%'||?||'%' "); 
            objectParam = new Object[]{busTupaDescripcion};
        }
        sql.append(" order by CO_PROCESO\n"); 
        sql.append(" LIMIT ").append(applicationProperties.getTopRegistrosConsultas());
        List<TupaBean> list = new ArrayList<TupaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TupaBean.class),objectParam);
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }


    public String insTupa(TupaBean tupa, String codusuario) {
        String vReturn      = "NO_OK";
        StringBuffer sql    = new StringBuffer();
        String genCoProceso = "";
        
        sql.append("insert into IDOSGD.TDTR_PROCESOS_EXP ");
        sql.append("(CO_PROCESO, TI_PROCESO, ES_ESTADO, CO_DEP_PRO, DE_NOMBRE, DE_DESCRIPCION, NU_PLAZO, US_CREA_AUDI, US_MODI_AUDI, FE_CREA_AUDI, FE_MODI_AUDI) ");
        sql.append("values (?,?,?,?,?,?, ?,?,?, CURRENT_TIMESTAMP ,CURRENT_TIMESTAMP ) ");

        try {
            //El id de la tabla sera autogenerado sumando 1 al ultimo ID
            genCoProceso = (String) this.jdbcTemplate.queryForObject("SELECT LPAD(CAST (COALESCE(MAX(CAST (CO_PROCESO AS INT)),0)+1 AS TEXT),4,'0') FROM IDOSGD.TDTR_PROCESOS_EXP", String.class);
            
            //El campo Estado no se incluye en el bloque del insert pq es un valor default en la tabla
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                genCoProceso, "1",tupa.getEsEstado(), "00000", tupa.getDeNombre(), tupa.getDeDescripcion(),
                 tupa.getNuPlazo(), codusuario, codusuario 
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
    
    public TupaBean getTupa(String codTupa){
        TupaBean tupaBean = new TupaBean();
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT CO_PROCESO, TI_PROCESO, CO_DEP_PRO, DE_NOMBRE, DE_DESCRIPCION, NU_PLAZO, ES_ESTADO, US_CREA_AUDI, ");
        sql.append("to_char(FE_CREA_AUDI,'dd/mm/yyyy') FE_CREA_AUDI, US_MODI_AUDI, to_char(FE_MODI_AUDI,'dd/mm/yyyy') FE_MODI_AUDI ");
        sql.append("FROM IDOSGD.TDTR_PROCESOS_EXP WHERE CO_PROCESO = ?");
        
        try {
            tupaBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(TupaBean.class),
                    new Object[]{codTupa});
        } catch (EmptyResultDataAccessException e) {
            tupaBean = null;
        } catch (Exception e) {
            tupaBean = null;
            e.printStackTrace();
        }
        
        return tupaBean;
    }
    
    public String updTupa(TupaBean tupa, String codusuario){
        String vReturn = "NO_OK";
        
        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.TDTR_PROCESOS_EXP set ");
        sql.append(" ES_ESTADO=?, DE_NOMBRE=?, DE_DESCRIPCION=?, NU_PLAZO=?, US_MODI_AUDI=?, FE_MODI_AUDI=CURRENT_TIMESTAMP ");
        sql.append("where CO_PROCESO=? ");
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                tupa.getEsEstado(),tupa.getDeNombre(), tupa.getDeDescripcion(), tupa.getNuPlazo(), codusuario, tupa.getCoProceso()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return vReturn;
    }
    
}
