/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.util;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.List;
import org.springframework.jdbc.core.JdbcTemplate;

/**
 *
 * @author JHuamanF
 */
public class ReadFile {
    
    private static JdbcTemplate jdbcTemplate;
    
    private static final int BUFFER_SIZE = 4096;
 
    public static void main(String[] args) throws FileNotFoundException, SQLException, IOException {
        
        String filePath = "C:\\Users\\JHuamanf\\Documents\\pl\\";            
            
        String sql2 = "SELECT BL_DOC FROM IDOSGD.TDTR_PLANTILLA_DOCX";
        List<Blob> certs = jdbcTemplate.queryForList(sql2, Blob.class); 
        int nro = 1;            
        if (!certs.isEmpty()) {
            for (Blob bl : certs) {
                InputStream inputStream = bl.getBinaryStream();
                OutputStream outputStream = new FileOutputStream(filePath + String.format("%03d", nro) + ".doc");

                int bytesRead = -1;
                byte[] buffer = new byte[BUFFER_SIZE];
                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                inputStream.close();
                outputStream.close();
                System.out.println("File saved: "+nro);
                nro++;
            }
        }
        
    }
    
}
