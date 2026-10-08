/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.util.HashMap;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.autentica.model.DatosUsuario;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.dao.ValidaDatosDao;

/**
 *
 * @author WCutipa
 */
@Repository("validaDatosDao")
public class ValidaDatosDaoImp extends SimpleJdbcDaoBase implements ValidaDatosDao{
    
    @Override
    public String getValidaDep(String pcoEmp, String pcoUsu, String pcoDep){
        StringBuffer sql = new StringBuffer();
        String result = null;
        Map<String, Object> objectParam = new HashMap<String, Object>();
        
        sql.append("select A.CO_DEPENDENCIA ");
        sql.append("from ( ");
        sql.append("  SELECT CO_DEPENDENCIA ");
        sql.append("  FROM IDOSGD.RHTM_DEPENDENCIA ");
        sql.append("  WHERE CO_DEPENDENCIA IN ( ");
        sql.append("                    SELECT CO_DEPENDENCIA ");
        sql.append("                    FROM IDOSGD.RHTM_DEPENDENCIA ");
        sql.append("                    WHERE IN_BAJA = '0' ");
        sql.append("                    AND CO_EMPLEADO = :pcoEmp "); 
        sql.append("                    UNION ");
        sql.append("                    SELECT CO_DEPENDENCIA "); 
        sql.append("                      FROM IDOSGD.RHTM_PER_EMPLEADOS ");
        sql.append("                    WHERE CEMP_CODEMP = :pcoEmp ");
        sql.append("                    UNION ");
        sql.append("                    SELECT CO_DEP ");
        sql.append("                    FROM IDOSGD.TDTX_DEPENDENCIA_EMPLEADO WITH (NOLOCK)  ");
        sql.append("                    WHERE CO_EMP = :pcoEmp ");
        sql.append("                      AND ES_EMP = '0' ");
        sql.append("                    UNION ");
        sql.append("                    SELECT CO_DEP COD_DEP ");
        sql.append("                    FROM IDOSGD.TDTR_PERMISOS ");
        sql.append("                    WHERE CO_USE = :pcoUsu ");
        sql.append("                      AND ES_ACT = '0' ");
        sql.append("                    EXCEPT ");
        sql.append("                    SELECT CO_DEP COD_DEP ");
        sql.append("                    FROM IDOSGD.TDTR_PERMISOS ");
        sql.append("                    WHERE CO_USE = :pcoUsu ");
        sql.append("                      AND ES_ACT = '1') ");
        sql.append("  AND IN_BAJA <> '1' ) A ");
        sql.append("where A.CO_DEPENDENCIA = :pcoDep ");
        
        objectParam.put("pcoEmp", pcoEmp);
        objectParam.put("pcoUsu", pcoUsu);
        objectParam.put("pcoDep", pcoDep);
        
        try {
            result = this.namedParameterJdbcTemplate.queryForObject(sql.toString(), objectParam, String.class);
        } catch (EmptyResultDataAccessException e) {
            result = null;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        
        return result;       
    }
    
}
