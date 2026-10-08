/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.web.controller;

import com.octo.captcha.service.CaptchaServiceException;
import java.io.IOException;
import java.text.Format;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Random;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession; 
import org.springframework.stereotype.Controller; 
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod; 
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import pe.gob.onpe.mpvdoc.bean.DocumentoFileBean; 

/**
 *
 * @author WCONDORI
 */
@Controller
//@RequestMapping("/documento.do")
public class DocumentoController {
    
       @RequestMapping(method = RequestMethod.POST, value = "/goUploadDoc")//accion=goUploadDoc
    public @ResponseBody String goUploadDoc(MultipartHttpServletRequest request, HttpServletResponse response) {
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;  
        LinkedList<DocumentoFileBean> files = new LinkedList<DocumentoFileBean>();
        DocumentoFileBean fileMeta = null; 
        String base64Files=""; 
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next()); 
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());
            try {
               fileMeta.setArchivoBytes(mpf.getBytes());
               //base64Files = Base64.getEncoder().encodeToString(mpf.getBytes());
                //byte[] decoded = Base64.getDecoder().decode(base64Files.getBytes());
               request.getSession().setAttribute("SessionDoc", mpf.getBytes());
                //vreturn=anexoDocumentoService.insArchivoAnexo(coUsu,pNuAnn, pNuEmi, fileMeta);
            } catch (IOException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            //2.4 add to files
            files.add(fileMeta);
        }

        // result will be like this
        // [{"fileName":"app_engine-85x77.png","fileSize":"8 Kb","fileType":"image/png"},...]
         
        String nombreArchivo=fileMeta.getNombreArchivo();
        String tamanoArchivo=fileMeta.getTamanoArchivo();
        String tipoArchivo=fileMeta.getTipoArchivo();
        
        String res= "[{\"fileString\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\"}]";
        res=String.format(res, base64Files,nombreArchivo,tamanoArchivo,tipoArchivo);
        return res;        
    }
       @RequestMapping(method = RequestMethod.POST, value = "/goUploadDocAnexos") 
    public @ResponseBody String goUploadDocAnexos(MultipartHttpServletRequest request, HttpServletResponse response) {
        HttpServletRequest httpServletRequest = (HttpServletRequest) request;
        HttpSession sesion = httpServletRequest.getSession(false);
        Iterator<String> itr = request.getFileNames();
        MultipartFile mpf = null;  
        LinkedList<DocumentoFileBean> files = (LinkedList<DocumentoFileBean>)sesion.getAttribute("SessionAnexos");
        if(files == null){
             files = new LinkedList<DocumentoFileBean>();
        }
        DocumentoFileBean fileMeta = null;  
        while (itr.hasNext()) {
            mpf = request.getFile(itr.next()); 
            fileMeta = new DocumentoFileBean();
            fileMeta.setNombreArchivo(mpf.getOriginalFilename());
            fileMeta.setTamanoArchivo(mpf.getSize() / 1024 + " Kb");
            fileMeta.setTipoArchivo(mpf.getContentType());
            try {
               fileMeta.setArchivoBytes(mpf.getBytes());                
            } catch (IOException e) { 
                e.printStackTrace();
            } 
            Random r2 = new Random(1000);
            int n2 = r2.nextInt(1000);
            Format f = new SimpleDateFormat("mmddyyyyhhmmssSSS");
            String str = f.format(new Date());
            fileMeta.setIdDocumento(str+n2); 
            files.add(fileMeta);
            request.getSession().setAttribute("SessionAnexos", files);
        }

        String nombreArchivo=fileMeta.getNombreArchivo();
        String tamanoArchivo=fileMeta.getTamanoArchivo();
        String tipoArchivo=fileMeta.getTipoArchivo();
        
        String res= "[{\"id\":\"%s\",\"name\":\"%s\",\"fileSize\":\"%s\",\"fileType\":\"%s\"}]";
        res=String.format(res, fileMeta.getIdDocumento(),nombreArchivo,tamanoArchivo,tipoArchivo);
        return res;        
    }
    
    
    
}
