/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.libreria.util;



import java.io.ByteArrayOutputStream;
import javax.crypto.*;
import javax.crypto.spec.IvParameterSpec;
import javax.xml.bind.DatatypeConverter;
import java.nio.charset.Charset;
import java.security.GeneralSecurityException;
import java.security.InvalidKeyException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.security.MessageDigest;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import pe.gob.onpe.libreria.util.des.TripleDES;


/**
 *
 * @author crosales
 */

public class Utility {
    private static boolean instanciated = false;
    private static Utility utilityInstance;
    private Random generator;
    private TripleDES _3Des;
    private static final Pattern numberPattern = Pattern.compile("^\\d+$");    
    private static final String key1_3DEs = "1234567890ABCDEF";
    private static final String key2_3DEs = "ABCDEFGHIJKLMNOP";
    private static final String key3_3DEs = "QRSTUVWXYZ123456";

    private Utility(){
        generator = new Random(19580427);
        _3Des = new TripleDES();
    }

    public static Utility getInstancia() {
        if (!Utility.instanciated) {
            Utility.utilityInstance = new Utility();
            Utility.instanciated = true;
        }
        return Utility.utilityInstance;
    }


    public Integer nextRandomNumber() {
        return Integer.valueOf(generator.nextInt());
    }

    public Date getLastDateInYear(Date date) {
        Date newd = null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        if (date == null) {
            date = new Date();
        }
        try {
            newd = format.parse("31/12/" + format.format(date).substring(6));
        } catch (ParseException e) {
            newd = new Date();
        }

        return newd;
    }

    public Date getFirstDateInYear(Date date) {
        Date newd = null;
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        if (date == null) {
            date = new Date();
        }
        try {
            newd = format.parse("01/01/" + format.format(date).substring(6));
        } catch (ParseException e) {
            newd = new Date();
        }

        return newd;
    }

    public String dateToFormatString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }

    public String dateToFormatString2(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }
    public String dateToFormatStringHHmm(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("HH:mm");
        return format.format(date);
    }
    public String dateToFormatString3(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm");
        return format.format(date);
    }    
    
    public String dateToFormatStringYYYY(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy");
        return format.format(date);
    }

    public String dateYearString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date).substring(6);
    }

    public String dateYearString() {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(new Date()).substring(6);
    }

    public String rellenaCerosIzquierda(String str, int length){
        String retval="";
        if(str!=null){
            for(int i=0; i<length-str.length(); i++){
                retval+="0";
            }
            retval+=str;
        }
        return retval;
    }

    public Date getDateHour(String string) throws ParseException {
        SimpleDateFormat dateFormat =
            new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return dateFormat.parse(string);
    }

    public String getDateString(Date date) {
        if (date == null) {
            return "";
        }
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
        //dateFormat.parse()
        return dateFormat.format(date);
    }

    public long getDateInt() {
        long vsec;
        Calendar now = Calendar.getInstance();
        vsec = now.getTimeInMillis()/1000;
        return vsec;
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


//    public String deCodifica(String newPassword)
//    {
//        String retorno = null;
//        String password = newPassword.trim();
//        try
//        {
//            BASE64Decoder decoder = new BASE64Decoder();
//            byte pass[] = decoder.decodeBuffer(password);
//            retorno = new String(pass);
//        }
//        catch(Exception exception) { }
//        return retorno;
//    }

//    public String codifica(String newPassword)
//    {
//        String retorno = null;
//        String password = newPassword;
//        try
//        {
//            BASE64Encoder encoder = new BASE64Encoder();
//            retorno = encoder.encodeBuffer(password.getBytes());
//        }
//        catch(Exception exception) { }
//        return retorno;
//    }

    //Restarle dias a una fecha determinada
    //@param fch La fecha
    //@param dias Dias a restar
    //@return La fecha restando los dias
    //http://www.programandoconcafe.com/2011/03/java-manejo-de-fechas-javautildate.html
    public Date restarFechasDias(Date fch, int dias) {
        Calendar cal = new GregorianCalendar();
        cal.setTimeInMillis(fch.getTime());
        cal.add(Calendar.DATE, -dias);
        return new Date(cal.getTimeInMillis());
    }

    public String codificaAES(String plainMessage,String symKeyHex) {

//        byte[] symKeyData = DatatypeConverter.parseBase64Binary(symKeyHex);
        byte[] symKeyData = symKeyHex.getBytes();
        byte[] encodedMessage = plainMessage.getBytes(Charset.forName("UTF-8"));
        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };

        try {
            Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
            SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");
            IvParameterSpec ivspec = new IvParameterSpec(iv);

            cipher.init(Cipher.ENCRYPT_MODE, symKey, ivspec);
            byte[] encryptedMessage = cipher.doFinal(encodedMessage);

            String ivAndEncryptedMessageHex = DatatypeConverter.printHexBinary(encryptedMessage);

            return ivAndEncryptedMessageHex;
        } catch (InvalidKeyException e) {
            throw new IllegalArgumentException("key argument does not contain a valid AES key");
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("Unexpected exception during encryption", e);
        }
      }

       public  String decodificaAES(String encryptedMessageHex, String symKeyHex) {
            byte[] symKeyData = symKeyHex.getBytes();
//            byte[] symKeyData = DatatypeConverter.parseHexBinary(symKeyHex);
            byte[] encryptedMessage = DatatypeConverter.parseHexBinary(encryptedMessageHex);
            byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
            try {
                Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
                SecretKeySpec symKey = new SecretKeySpec(symKeyData, "AES");
                IvParameterSpec ivspec = new IvParameterSpec(iv);

                cipher.init(Cipher.DECRYPT_MODE, symKey,ivspec);
                byte[] encodedMessage = cipher.doFinal(encryptedMessage);

                String message = new String(encodedMessage,Charset.forName("UTF-8"));

                return message;
            } catch (InvalidKeyException e) {
                throw new IllegalArgumentException("key argument does not contain a valid AES key");
            } catch (BadPaddingException e) {
                return null;
            } catch (GeneralSecurityException e) {
                throw new IllegalStateException("Unexpected exception during decryption", e);
            }

        }
       
       public Date getDateToString(String pfecha){
            SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy");
            Date date;
            try {

                    date = formatter.parse(pfecha);
//                    System.out.println(date);
//                    System.out.println(formatter.format(date));

            } catch (ParseException e) {
                    date = null;
            }           
            return date;
       }

    //PABLO 18/08/2012. SE FORMATEA FECHA Y HORA PARA ENVIAR A PAQUETE
    /*public String dateCompletaToFormatString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
        return format.format(date);
    }*/
       
    public String getStringFechaYYYYMMDDHHmm(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmm");
        return format.format(date);
    } 
    
    public String getStringFechaFormat(String pfecha,String formatoFechaReturn,String formatoPfecha){
        String vResult="";
        if (pfecha!=null&&!pfecha.equals("")&&formatoFechaReturn!=null&&!formatoFechaReturn.equals("")
            &&formatoPfecha!=null&&!formatoPfecha.equals("")) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formatoPfecha);
                Date date = dateFormat.parse(pfecha);
                dateFormat =  new SimpleDateFormat(formatoFechaReturn);
                vResult=dateFormat.format(date);                    
            } catch (Exception e) {
                e.printStackTrace();
                vResult="NO_OK";
            }
        }
        return vResult;
    }

   public static boolean validarContraseniaFuerte(String cadena){
       /*Descripción: Lo que hacemos en este patrón es buscar que tenga por lo menos una letra en mayúscula, una letra en minúscula y un número y que su longitud sea entre 6 y 20 caracteres. Esto es para asegurarnos que la contraseña sea segura.*/
        Pattern pat = Pattern.compile("^(?=.*[a-z])(?=.*[A-Z])(?=.*\\d){8,20}.+$");       
        Matcher mat = pat.matcher(cadena);
        return mat.find();
   }    
   
   public static boolean isNumber(String nro){
       return nro!=null && numberPattern.matcher(nro).matches();
   }    
   
    public Date getDateFromString(String pfecha){
         SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
         Date date;
         try {

                 date = formatter.parse(pfecha);

         } catch (ParseException e) {
                 date = null;
         }           
         return date;
    }
    
    //funcion para verificar si un arreglo de bytes "file" es del tipo pdf
    public boolean isPdf(byte[] data) {
        return data != null && data.length > 4 &&
                data[0] == 0x25 && // %
                data[1] == 0x50 && // P
                data[2] == 0x44 && // D
                data[3] == 0x46 && // F
                data[4] == 0x2D;
    }
    
    public String cipher_3Des(String data){
        return _3Des.toHexString(_3Des.cipher(data, key1_3DEs, key2_3DEs, key3_3DEs));
    }
            
    public String decipher_3Des(String data){
        return _3Des.decipher(data, key1_3DEs, key2_3DEs, key3_3DEs);
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
}
