/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.web.util;

import com.octo.captcha.engine.image.ListImageCaptchaEngine;
import com.octo.captcha.component.image.backgroundgenerator.BackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.UniColorBackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.MultipleShapeBackgroundGenerator;
import com.octo.captcha.component.image.backgroundgenerator.FunkyBackgroundGenerator;
import com.octo.captcha.component.image.wordtoimage.WordToImage;
import com.octo.captcha.component.word.wordgenerator.WordGenerator;
import com.octo.captcha.component.word.wordgenerator.RandomWordGenerator;
import com.octo.captcha.component.image.wordtoimage.ComposedWordToImage;
import com.octo.captcha.image.gimpy.GimpyFactory;
import com.octo.captcha.component.image.textpaster.TextPaster;
import com.octo.captcha.component.image.textpaster.RandomTextPaster;
import com.octo.captcha.component.image.fontgenerator.FontGenerator;
import com.octo.captcha.component.image.fontgenerator.RandomFontGenerator;
import com.octo.captcha.component.image.color.RandomRangeColorGenerator;
import java.awt.Color;
import java.awt.Font;
import java.awt.geom.AffineTransform;
import java.util.Random;


/**
* 
* @Author  : NLapa
* @Fecha   : 10/07/2017
* @Objeto  : Personalizar el diseño del captcha
* @Modificaciones
* @Fecha           @Responsable        @Descripción del Cambio    
* 
*/
public class CaptchaPersonalizado extends ListImageCaptchaEngine{
    protected void buildInitialFactories() {
            WordGenerator wgen = new RandomWordGenerator("ABCDEFGHIJKLMNOPQRSTUVWXYZ123456789");
            RandomRangeColorGenerator cgen = new RandomRangeColorGenerator(
                 new int[] {0, 100},
                 new int[] {0, 100},
                 new int[] {0, 100});
            TextPaster textPaster = new RandomTextPaster(new Integer(5), new Integer(5), cgen, true);
             
            BackgroundGenerator backgroundGenerator = new UniColorBackgroundGenerator(new Integer(400), new Integer(200), new Color(204, 255, 255)); //FunkyBackgroundGenerator(new Integer(100), new Integer(42));

//            RandomRangeColorGenerator cgen2 = new RandomRangeColorGenerator(
//                 new int[] {0, 100},
//                 new int[] {0, 100},
//                 new int[] {0, 100},
//                 new int[] {220, 255});
//            BackgroundGenerator backgroundGenerator = new MultipleShapeBackgroundGenerator(new Integer(300), new Integer(150), cgen2.getNextColor(), cgen2.getNextColor(), 
//                    new Integer(10), new Integer(10), new Integer(10), new Integer(1), cgen2.getNextColor(), cgen2.getNextColor(),new Integer(1));//FunkyBackgroundGenerator(new Integer(100), new Integer(42));
            
//            BackgroundGenerator backgroundGenerator = new FunkyBackgroundGenerator(new Integer(300), new Integer(150), cgen,cgen,cgen, cgen, new Float(0.9));

            Font[] fontsList = new Font[] {
                new Font("Arial", 0, 35),
                new Font("Tahoma", 0, 35),
                new Font("Verdana", 0, 35),
             };
 
             FontGenerator fontGenerator = new RandomFontGenerator(new Integer(120), new Integer(140), fontsList); //120 140
 
             WordToImage wordToImage = new ComposedWordToImage(fontGenerator, backgroundGenerator, textPaster);
             this.addFactory(new GimpyFactory(wgen, wordToImage));
     }   
}
