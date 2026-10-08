/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.util;

import java.io.ByteArrayOutputStream;
import java.io.StringReader;
import java.io.StringWriter;
import java.security.MessageDigest;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.regex.Pattern;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.xml.sax.InputSource;
/*import pe.gob.onpe.wsoapiop.schema.RecepcionRequest;
import pe.gob.onpe.wsoapiop.schema.RecepcionResponse;*/

/**
 *
 * @author crosales
 */
public class Utility {

    private static boolean instanciated = false;
    private static Utility utilityInstance;

    private Utility() {        
    }

    public static Utility getInstancia() {
        if (!Utility.instanciated) {
            Utility.utilityInstance = new Utility();
            Utility.instanciated = true;
        }
        return Utility.utilityInstance;
    }

    public Date getDateHour(String pfecha) throws ParseException {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date;
        try {
            date = formatter.parse(pfecha);
        } catch (ParseException e) {
            date = null;
        }
        return date;
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
        vsec = now.getTimeInMillis() / 1000;
        return vsec;
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

    public Date getDateToString(String pfecha) {
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

    public String getStringFechaFormat(String pfecha, String formatoFechaReturn, String formatoPfecha) {
        String vResult = "";
        if (pfecha != null && !pfecha.equals("") && formatoFechaReturn != null && !formatoFechaReturn.equals("")
                && formatoPfecha != null && !formatoPfecha.equals("")) {
            try {
                SimpleDateFormat dateFormat = new SimpleDateFormat(formatoPfecha);
                Date date = dateFormat.parse(pfecha);
                dateFormat = new SimpleDateFormat(formatoFechaReturn);
                vResult = dateFormat.format(date);
            } catch (Exception e) {
                e.printStackTrace();
                vResult = "NO_OK";
            }
        }
        return vResult;
    }
    public Date getDateWithOutTime(Date fecha) {
        Date res = null;
        Calendar calendar = Calendar.getInstance();

        calendar.setTime(fecha);
        calendar.set(Calendar.HOUR_OF_DAY, 0);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);

        res = calendar.getTime();

        return res;
    }

    public Date getDateWithOutMinutes(Date fecha) {
        Date res = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.MINUTE, 0);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        res = calendar.getTime();
        return res;
    }

    public Date getDateWithOutSeconds(Date fecha) {
        Date res = null;
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(fecha);
        calendar.set(Calendar.SECOND, 0);
        calendar.set(Calendar.MILLISECOND, 0);
        res = calendar.getTime();
        return res;
    }

    public String comparaFechaSinHora(Date date1, Date date2) {
        Date d1 = getDateWithOutTime(date1);
        Date d2 = getDateWithOutTime(date2);
        String msg = "0";
        if (d1.before(d2)) {
            msg = "1";
        }
        if (d1.after(d2)) {
            msg = "2";
        }
        return msg;
    }

    public String comparaFechaSinSegundos(Date date1, Date date2) {
        Date d1 = getDateWithOutSeconds(date1);
        Date d2 = getDateWithOutSeconds(date2);
        String msg = "0";
        if (d1.before(d2)) {
            msg = "1";
        }
        if (d1.after(d2)) {
            msg = "2";
        }
        return msg;
    }

    public String comparaFechaSinMinutos(Date date1, Date date2) {
        Date d1 = getDateWithOutMinutes(date1);
        Date d2 = getDateWithOutMinutes(date2);
        String msg = "0";
        if (d1.before(d2)) {
            msg = "1";
        }
        if (d1.after(d2)) {
            msg = "2";
        }
        return msg;
    }

    public String comparaFechaConHora(Date d1, Date d2) {
        String msg = "0";
        if (d1.before(d2)) {
            msg = "1";
        }
        if (d1.after(d2)) {
            msg = "2";
        }
        return msg;
    }

    public boolean isPdf(byte[] data) {
        if (data != null && data.length > 4
                && data[0] == 0x25
                && // %
                data[1] == 0x50
                && // P
                data[2] == 0x44
                && // D
                data[3] == 0x46
                && // F
                data[4] == 0x2D) { // -
            return true;
        }
        return false;
    }

    //Convertir Clase a String XML
    /*public String jaxbObjectToXML(RecepcionResponse response) {
        String xmlString = "";
        try {
            JAXBContext context = JAXBContext.newInstance(RecepcionResponse.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
            StringWriter sw = new StringWriter();
            m.marshal(response, sw);
            xmlString = sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return xmlString;
    }
*/
    
    public String dateToFormatString(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
        return format.format(date);
    }

    public String dateToFormatString2(Date date) {
        SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        return format.format(date);
    }

    //Convertir String XML a File XML en Bytes
    public byte[] getBytesArchivoXML(String xmlString) {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document document = builder.parse(new InputSource(new StringReader(xmlString)));
            TransformerFactory tranFactory = TransformerFactory.newInstance();
            Transformer aTransformer = tranFactory.newTransformer();
            Source src = new DOMSource(document);
            ByteArrayOutputStream bos = new ByteArrayOutputStream();
            Result dest = new StreamResult(bos);
            aTransformer.transform(src, dest);
            return bos.toByteArray();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new byte[0];
    }

//Convertir Clase a String XML
    /*
    public String jaxbObjectToXML(RecepcionRequest request) {
        String xmlString = "";
        try {
            JAXBContext context = JAXBContext.newInstance(RecepcionRequest.class);
            Marshaller m = context.createMarshaller();
            m.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, Boolean.TRUE); // To format XML
            StringWriter sw = new StringWriter();
            m.marshal(request, sw);
            xmlString = sw.toString();
        } catch (JAXBException e) {
            e.printStackTrace();
        }
        return xmlString;
    }*/

    public Date getDateFromString(String pfecha) {
        SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss");
        Date date;
        try {

            date = formatter.parse(pfecha);

        } catch (ParseException e) {
            date = null;
        }
        return date;
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
