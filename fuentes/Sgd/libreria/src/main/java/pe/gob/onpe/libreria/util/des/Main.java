/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.libreria.util.des;

import pe.gob.onpe.libreria.util.Utility;

/**
 *
 * @author ECueva
 */
public class Main {

    /** Creates a new instance of Main */
    public Main() {
    }
    
    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // TODO code application logic here
        
        String output;
  
        DES desEncryptor = new DES();
        
        //Encripta
        output = desEncryptor.cipher("789456", "1234567890ABCDEF");
        System.out.println("OUTPUT 1: "+output.toUpperCase()+"\n");
        
        //Desencripta
        output = desEncryptor.decipher(output.toUpperCase(), "1234567890ABCDEF");
        System.out.println("OUTPUT 2: "+output.toUpperCase()+"\n");
//        
//        //Encripta
//        output = desEncryptor.cipher(output.toUpperCase(), "3333333333333333");
//        System.out.println("OUTPUT 3: "+output.toUpperCase()+"\n");
//        
//        //Desencripta 2
//        output = desEncryptor.decipher(output.toUpperCase(), "3333333333333333");
//        System.out.println("OUTPUT 11: "+output.toUpperCase()+"\n");
//        
//        //Encripta 2
//        output = desEncryptor.cipher(output.toUpperCase(), "2222222222222222");
//        System.out.println("OUTPUT 22: "+output.toUpperCase()+"\n");
//        
//        //Desencripta 2
//        output = desEncryptor.decipher(output.toUpperCase(), "1111111111111111");
//        System.out.println("OUTPUT 33: "+output.toUpperCase()+"\n");
        
        TripleDES tdes = new TripleDES();
        //String data = "21,card=450646150208515728,working_key=1234567890ABCDEF16,num_master_key=0";
        String data = "abcdefghijk";
        //String key1 = "1111111111111111";
        //String key2 = "2222222222222222";
        //String key3 = "3333333333333333";
        String key1 = "1234567890ABCDEF";
        String key2 = "ABCDEFGHIJKLMNOP";
        String key3 = "QRSTUVWXYZ123456";
        output = tdes.cipher(data, key1, key2 , key3);
        System.out.println("**************ENCRIPTADO: "+output+"\n");
        output = tdes.decipher(output, key1, key2 , key3);
        System.out.println("******************NORMAL: "+output+"\n");
        data="ECUEVA";
        output = Utility.getInstancia().cipher_3Des(data);
        System.out.println("**************ENCRIPTADO: "+output+"\n");
    }
}
