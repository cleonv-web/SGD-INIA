/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.web.util;

import java.io.ByteArrayOutputStream;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

/**
 *
 * @author RATAYAURI
 */
public class Utility {
    private static boolean instanciated = false;
    private static Utility utilityInstance;
    public static Map<String, Integer> docsAccesoMap;
    public static String pathPdfs = "";

    private Utility(){
    }

    public static Utility getInstancia() {
        if (!Utility.instanciated) {
            Utility.utilityInstance = new Utility();
            Utility.instanciated = true;
            docsAccesoMap = new HashMap();
        }
        return Utility.utilityInstance;
    }

    public String cifrar(String sinCifrar, String key) throws Exception {
        final byte[] bytes = sinCifrar.getBytes("UTF-8");
        final Cipher aes = obtieneCipher(true,key);
        final byte[] cifrado = aes.doFinal(bytes);
        return DatatypeConverter.printHexBinary(cifrado);
    }

    public String descifrar(String cifrado,String key) throws Exception {
        byte[] bcadena = hexStrToByteArray(cifrado);
        final Cipher aes = obtieneCipher(false,key);
        final byte[] bytes = aes.doFinal(bcadena);
        final String sinCifrar = new String(bytes, "UTF-8");
        return sinCifrar;
    }

    public byte[] hexStrToByteArray(String hex) {
        ByteArrayOutputStream baos = new ByteArrayOutputStream(hex.length() / 2);
        for (int i = 0; i < hex.length(); i += 2) {
            String output = hex.substring(i, i + 2);
            int decimal = Integer.parseInt(output, 16);
            baos.write(decimal);
        }
        return baos.toByteArray();
    }
    
    private Cipher obtieneCipher(boolean paraCifrar,String keyString) throws Exception {
            final MessageDigest digest = MessageDigest.getInstance("SHA");
            digest.update(keyString.getBytes("UTF-8"));
            final SecretKeySpec key = new SecretKeySpec(digest.digest(), 0, 16, "AES");

            final Cipher aes = Cipher.getInstance("AES/ECB/PKCS5Padding");
            if (paraCifrar) {
                    aes.init(Cipher.ENCRYPT_MODE, key);
            } else {
                    aes.init(Cipher.DECRYPT_MODE, key);
            }

            return aes;
    }  
    
}
