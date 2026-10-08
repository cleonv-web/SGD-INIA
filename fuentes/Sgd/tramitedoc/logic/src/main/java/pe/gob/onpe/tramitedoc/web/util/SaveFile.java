/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author JHuamanF/
 */
public class SaveFile {
    
    private static JdbcTemplate jdbcTemplate;

    public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
        
        String filePath = "C:\\Users\\JHuamanf\\Documents\\pl\\mod\\";            
            
        String sql1 = "UPDATE IDOSGD.TDTR_PLANTILLA_DOCX SET BL_DOC = ?, FE_MODI = CURRENT_TIMESTAMP WHERE CO_TIPO_DOC = ? ";

        InputStream inputStream = null;            

        String sql2 = "SELECT CO_TIPO_DOC FROM IDOSGD.TDTR_PLANTILLA_DOCX";

        List<String> listaCodigo = jdbcTemplate.queryForList(sql2, String.class); 
        int nro = 1;
        for (String codigo : listaCodigo) {

            inputStream = new FileInputStream(filePath + String.format("%03d", nro) + ".doc");

            jdbcTemplate.update(sql1.toString(), new Object[]{
                inputStream, codigo
            });

            nro++;
        }

        if (inputStream != null) {
            inputStream.close();
        }
        
    }
    
}
