/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.service;

import com.octo.captcha.service.image.ImageCaptchaService;
import com.octo.captcha.service.image.DefaultManageableImageCaptchaService;
import com.octo.captcha.service.captchastore.FastHashMapCaptchaStore;
import java.awt.Color;
import java.util.Random;
import pe.gob.onpe.verificadoc.web.util.CaptchaPersonalizado;
/**   
* @Author  : NLapa 
* @Fecha   : 10/07/2017
* @Objeto  : Se crea la clase para el Singletón, creación y obtención.   
*/
public class CaptchaSingletonService {
    private static ImageCaptchaService instance;    
    static{
        instance = new DefaultManageableImageCaptchaService(
                new FastHashMapCaptchaStore(),
                new CaptchaPersonalizado(),
                180, 
                100000, 
                75000);
    }
        
    public static ImageCaptchaService getInstance(){
        return instance;
    }
}
