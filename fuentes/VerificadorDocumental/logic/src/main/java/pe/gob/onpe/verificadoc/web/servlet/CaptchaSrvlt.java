/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.web.servlet;

import com.octo.captcha.service.CaptchaServiceException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import javax.imageio.ImageIO;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import pe.gob.onpe.verificadoc.service.CaptchaSingletonService;

/**   
* @Author  : Ratayauri 
* @Fecha   : 10/07/2017
* @Objeto  : Servlet para el uso del captcha  
*/
public class CaptchaSrvlt extends HttpServlet{
     private transient final Log LOGGER = LogFactory.getLog(getClass());
//    @Override
//    public void init(ServletConfig servletConfig) throws ServletException {        
//        super.init(servletConfig);
//    }
    @Override
        protected void doPost(HttpServletRequest request, HttpServletResponse response)
                        throws ServletException, IOException {
          doGet(request, response);
        }   
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)    {       
         System.out.println("OK CAPTCHA 0==>");      
        byte[] captchaChallengeAsJpeg = null;
        ByteArrayOutputStream jpegOutputStream = new ByteArrayOutputStream();
        try {
            System.out.println("OK CAPTCHA 1==>");            
            String captchaId = request.getSession().getId();                                              
            BufferedImage challenge =  CaptchaSingletonService.getInstance().getImageChallengeForID(captchaId, request.getLocale());            
            ImageIO.write(challenge, "jpeg", jpegOutputStream);
             System.out.println("OK CAPTCHA 2==>");
            captchaChallengeAsJpeg = jpegOutputStream.toByteArray();               
        response.setHeader("Cache-Control", "no-store");
        response.setHeader("Pragma", "no-cache");
        response.setDateHeader("Expires", 0);
        response.setContentType("image/jpeg");
        ServletOutputStream responseOutputStream = response.getOutputStream();
        responseOutputStream.write(captchaChallengeAsJpeg);
        responseOutputStream.flush();
        responseOutputStream.close(); 
             
        } catch (IllegalArgumentException e) {
            //response.sendError(HttpServletResponse.SC_NOT_FOUND);
             System.out.println("ERROR 1==>");
            LOGGER.error(e.getMessage(), e);
            return;
        } catch (CaptchaServiceException e) {
           // response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            System.out.println("ERROR 2==>");
            LOGGER.error(e.getMessage(), e);
            return;
        } catch (IOException e) {
        System.out.println("ERROR 3==>");
        LOGGER.error(e.getMessage(), e);
        }
 
        
    }
}
