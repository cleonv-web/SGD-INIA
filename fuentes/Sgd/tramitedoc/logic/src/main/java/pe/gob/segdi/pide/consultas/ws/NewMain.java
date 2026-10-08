/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.segdi.pide.consultas.ws;

import java.util.Base64;
import pe.gob.onpe.libreria.util.ConstantesSec;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
 
/**
 *
 * @author mvaldera
 */
public class NewMain {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) throws Exception {
        // TODO code application logic here
        /*
        String pass=Utility.getInstancia().cifrar("C:\\glassfish5\\glassfish\\tmppcm",ConstantesSec.SGD_SECRET_KEY_PROPERTIES);
        System.out.println("pass==>"+pass);
        String pass2=Utility.getInstancia().descifrar(pass,ConstantesSec.SGD_SECRET_KEY_PROPERTIES);
        System.out.println("pass2==>"+pass2);  
         */
 /*    
        String pass2=Utility.getInstancia().descifrar("5881236AED5F9FA69E5D8C59E0C979E8",ConstantesSec.SGD_SECRET_KEY_PASSWORD);
        System.out.println("pass2==>"+pass2);  */
        //42690172|0D7508519D2C2B71005EB6427D240FA6 original 
        // String pass3=Utility.getInstancia().cifrar("20603965451",ConstantesSec.SGD_SECRET_KEY_PASSWORD);
        String pass3 = Utility.getInstancia().cifrar("40022904", ConstantesSec.SGD_SECRET_KEY_PASSWORD);
        System.out.println("passFONDE==>" + pass3);
//77141643|51EB41F8E20B8417C511EDB453706274
        String pass4 = Utility.getInstancia().descifrar("B6C0958243D537A32C4F818AD13D2A1F", ConstantesSec.SGD_SECRET_KEY_PASSWORD);
        System.out.println("pass2ABREÑAAAAAAAAAAAAAA==>" + pass4);

        String pass5 = Utility.getInstancia().cifrar("", ConstantesSec.SGD_SECRET_KEY_PROPERTIES);
        System.out.println("passRUC==>DNIe====>" + pass5);
        String pass52 = Utility.getInstancia().cifrar("20131368586", ConstantesSec.SGD_SECRET_KEY_PROPERTIES);
        System.out.println("passBREÑAAAAAA==>" + pass52);
        String pass6 = Utility.getInstancia().descifrar("2F57878F6285FD719A212B1746A88EC3", ConstantesSec.SGD_SECRET_KEY_PROPERTIES);
        System.out.println("pass2SGD_SECRET_KEY_PROPERTIES==>" + pass6);
        //     String pass5=Utility.getInstancia().descifrar("10F2852051B7BABD69D6D1C876EE9C6A051ABF082B410FFB79036F15AC90B25F",ConstantesSec.SGD_SECRET_KEY_PROPERTIES);
        //   System.out.println("pass3==>"+pass5);
        //String dddd = Utilidades.encriptar("AAA");
        //System.out.println("encriptar==>" + dddd);
         
        byte[] datosEncriptar = "AAAAA".getBytes("UTF-8");
        String base64= new String(Base64.getEncoder().encode(datosEncriptar));
        System.out.println("base64==>" + base64);
        
        
        byte[] bytesEncriptados = Base64.getDecoder().decode(base64);
        String datos = new String(bytesEncriptados);
        System.out.println("base64datos==>" + datos);
    }

}
