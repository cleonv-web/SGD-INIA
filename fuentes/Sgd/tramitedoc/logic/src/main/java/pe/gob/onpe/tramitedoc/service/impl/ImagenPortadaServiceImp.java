/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.io.FileOutputStream;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.dao.ImagenPortadaDao;
import pe.gob.onpe.tramitedoc.service.ImagenPortadaService;
//import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

/**
 *
 * @author ECueva
 */
@Service("imagenPortadaService")
public class ImagenPortadaServiceImp implements ImagenPortadaService{

    @Autowired
    private ImagenPortadaDao imagenPortadaDao;
    
    @Override
    public String saveImgPortada(String coUser, DocumentoFileBean fileMeta) {
        String vReturn = "NO_OK";
        FileOutputStream outputStream = null;
        try {
            String nomFile=fileMeta.getNombreArchivo();
            //String nameFile = "myFile.jpg";
            //String nameFile = "../webapps/recursos/"+nomFile;//tomcat
            String nameFile = "../docroot/recursos/"+nomFile;//glassfish
            outputStream = new FileOutputStream(nameFile);         
            outputStream.write(fileMeta.getArchivoBytes());
            imagenPortadaDao.changeImgPortada(nomFile);
            vReturn="OK";
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(outputStream!=null){
                    outputStream.close();
                }                
            } catch (Exception e) {
            }
        }
        return vReturn;
    }
    
}
