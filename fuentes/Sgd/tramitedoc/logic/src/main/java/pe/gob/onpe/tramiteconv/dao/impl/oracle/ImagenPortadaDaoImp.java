/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.dao.ImagenPortadaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author ecueva
 */
@Repository("imagenPortadaDao")
public class ImagenPortadaDaoImp extends SimpleJdbcDaoBase implements ImagenPortadaDao{

    @Override
    public String changeImgPortada(String nomFile) {
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("UPDATE TDTR_PARAMETROS \n"
                + "SET DE_PAR = ?\n"
                + "WHERE CO_PAR = 'ID_IMG_PORTADA_SGD'");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{nomFile});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;        
    }
    
}
