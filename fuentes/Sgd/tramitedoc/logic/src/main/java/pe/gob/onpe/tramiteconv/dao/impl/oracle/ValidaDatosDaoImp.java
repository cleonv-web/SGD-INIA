/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

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
        
        sql.append("select CO_DEPENDENCIA\n" +
                    "from (\n" +
                    "SELECT CO_DEPENDENCIA\n" +
                    "  FROM RHTM_DEPENDENCIA\n" +
                    " WHERE CO_DEPENDENCIA IN (\n" +
                    "                    SELECT CO_DEPENDENCIA\n" +
                    "                    FROM RHTM_DEPENDENCIA\n" +
                    "                    WHERE IN_BAJA = '0'\n" +
                    "                    AND CO_EMPLEADO = :pcoEmp \n" +
                    "                    UNION\n" +
                    "                    SELECT CO_DEPENDENCIA \n" +
                    "                      FROM RHTM_PER_EMPLEADOS\n" +
                    "                    WHERE CEMP_CODEMP = :pcoEmp\n" +
                    "                   UNION\n" +
                    "                   SELECT CO_DEP\n" +
                    "                     FROM TDTX_DEPENDENCIA_EMPLEADO\n" +
                    "                    WHERE CO_EMP = :pcoEmp\n" +
                    "                      AND ES_EMP = '0'\n" +
                    "                   UNION\n" +
                    "                   SELECT CO_DEP COD_DEP\n" +
                    "                     FROM TDTR_PERMISOS\n" +
                    "                    WHERE CO_USE = :pcoUsu\n" +
                    "                      and ES_ACT = '0'\n" +
                    "                   MINUS\n" +
                    "                   SELECT CO_DEP COD_DEP\n" +
                    "                     FROM TDTR_PERMISOS\n" +
                    "                    WHERE CO_USE = :pcoUsu\n" +
                    "                      and ES_ACT = '1')\n" +
                    "   AND IN_BAJA <> '1' ) \n" +
                    " where CO_DEPENDENCIA = :pcoDep ");
        
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
