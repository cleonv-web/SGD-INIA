package pe.gob.onpe.tramitedoc.web.util;

import org.springframework.ui.Model;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.autentica.model.UsuarioAcceso;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.util.Constantes;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.net.HttpURLConnection;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;  
import javax.crypto.SecretKey;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.PBEKeySpec; 
import java.security.spec.KeySpec; 

import javax.servlet.ServletContext;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;
import org.springframework.beans.factory.annotation.Autowired;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
//import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by IntelliJ IDEA.
 * User: crosales
 * Date: 13/09/11
 * Time: 04:00 PM
 * To change this template use File | Settings | File Templates.
 */
public class Utilidades {
    private static boolean instanciated = false;
    private static Utilidades instancia;
    public Utilidades(){
    }

    public static Utilidades getInstancia(){
        if(!Utilidades.instanciated){
            Utilidades.instancia = new Utilidades();
            Utilidades.instanciated = true;
        }
        return Utilidades.instancia;
    }

    public void saveUserInCookie(HttpServletResponse response, Usuario usuario){
        String serial = usuario.getNuDni()+"/" +
                usuario.getDeApellidoPaterno() + "/"+
                usuario.getDeApellidoMaterno()+ "/"+
                usuario.getDePrenombres();
                //usuario.getParametrosGlobales().getCoLocal()+"/"+
                //usuario.getParametrosGlobales().getDeLocal();
//                +"/"+usuario.getRemoteAttribs().getSessionId();
        agregarCookie(response, Constantes.USER_COOKIE_ID, serial, true);
    }

    public void saveAccessInCookie(HttpServletResponse response, Usuario usuario){
      String serial = "";
        for(UsuarioAcceso usuarioAcceso: usuario.getUsuarioAccesos()){
            if(!serial.equals("")){
                serial += "/";
            }
            serial+= usuarioAcceso.getCoModulo() + usuarioAcceso.getCoOpcion();
        }
        agregarCookie(response, Constantes.USER_ACCESS_COOKIE_ID, serial, false);
    }

    private void agregarCookie(HttpServletResponse response, String id, String valor, boolean expire){
        Cookie cookie=new Cookie(id,valor);
/*
        if(expire){
            cookie.setMaxAge(10);
        }
*/
        response.addCookie(cookie);
    }

    public void saveSessionInCookie(HttpServletResponse response, Usuario usuario){
        saveUserInCookie(response, usuario);
        saveAccessInCookie(response, usuario);
        agregarCookie(response, Constantes.SESSION_SATTUS, "1", false);
        //saveSessionIdInCookie(response, usuario);
    }

    public Usuario loadUserFromCookie(HttpServletRequest request){
    Usuario usuario = new Usuario();
    String user=giveMeCoockieValue(request, Constantes.USER_COOKIE_ID);
    splitAndPopuleUser(user, usuario);

    return usuario;
    }

    public void splitAndPopuleUser(String user, Usuario usuario){
        if(!user.equals("")){
            String str[] = user.split("/");
            if(str.length==6){
                usuario.setNuDni(str[0]);
                usuario.setDeApellidoPaterno(str[1]);
                usuario.setDeApellidoMaterno(str[2]);
                usuario.setDePrenombres(str[3]);
                //usuario.getParametrosGlobales().setCoLocal(str[4]);
                //usuario.getParametrosGlobales().setDeLocal(str[5]);
            }
        }
    }

    public String giveMeCoockieValue(HttpServletRequest request, String cookieName){
        String retval="";
        Cookie[] cookies = request.getCookies();
        if(cookies!=null){
            for(int i=0; i<cookies.length; i++){
                if(cookies[i].getName().equals(cookieName)){
                    retval = cookies[i].getValue();
                    break;
                }
            }
        }
        return retval;
    }


    public void loadAccessFromCookie(HttpServletRequest request, Usuario usuario){
        String acces=giveMeCoockieValue(request, Constantes.USER_ACCESS_COOKIE_ID);
        if(!acces.equals("")){
            String str[] = acces.split("/");
            List<UsuarioAcceso> usuarioAccesos = new ArrayList<UsuarioAcceso>();

            for(String x : str){
                usuarioAccesos.add(new UsuarioAcceso(x.substring(1,2), x.substring(3)));
            }
            usuario.setUsuarioAccesos(usuarioAccesos);
        }
    }
    public void setCatcha(HttpServletRequest request, String valor){
        ServletUtility.getInstancia().saveRequestAttribute(request, "CAPTCHA", valor);
    }
    public String getCatcha(HttpServletRequest request){
        return (String)ServletUtility.getInstancia().loadSessionAttribute(request, "CAPTCHA");
    }  
    public Usuario loadUserFromSession(HttpServletRequest request){
        return (Usuario) ServletUtility.getInstancia().loadSessionAttribute(request, "usuario");
    }

    
    public UsuarioConfigBean loadUserConfigFromSession(HttpServletRequest request){
        return (UsuarioConfigBean) ServletUtility.getInstancia().loadSessionAttribute(request, "usuarioConfig");
    }    
    public DocumentoVerBean loadDocFromSession(HttpServletRequest request){
        return (DocumentoVerBean) ServletUtility.getInstancia().loadSessionAttribute(request, "docSession");
    }    

    public void readHttpAndWriteInOutputStream(InputStream in, HttpServletResponse response, String contentType) throws Exception{
        try{
            long h = 0;
            int length = -1;
            byte[] buffer = new byte[4096];

            response.setContentType(contentType);
            ServletOutputStream ouputStream = response.getOutputStream();
            while ((length = in.read(buffer)) != -1){
                ouputStream.write(buffer, 0, length);
                ouputStream.flush();
                h += length;
            }
            ouputStream.flush();
            ouputStream.close();

        }catch (Exception e){
            throw e;
        }
    }

    public StringBuffer readHttpAndWriteInOutput(InputStream in) throws Exception{
        StringBuffer sf = new StringBuffer();
        try{
            BufferedReader input =
            new BufferedReader(new InputStreamReader(in));


            String line = "";
            while ((line = input.readLine()) != null){
                sf.append(line.trim());
            }
        }catch (Exception e){
            throw e;
        }
        return sf;
    }

    public String procesaReportePrimerIntento(HttpServletResponse response, String urlReporte) throws IOException, ServletException {
        String urlRetval = null;
        HttpURLConnection m_con=null;
        InputStream in=null;
        String contentType=null;
        try{
                java.net.URL url = new java.net.URL(urlReporte.toString().replace(" ","%20"));
                m_con = (HttpURLConnection)url.openConnection();

                if(m_con!=null){
                    contentType= m_con.getHeaderField("Content-Type");
                    if(contentType!=null && contentType.equals("application/pdf")){
                        in = new BufferedInputStream(m_con.getInputStream());
                        readHttpAndWriteInOutputStream(in, response, contentType);
                    }
                    else{
                        urlRetval = "forward:srReporteContingencia.do";
                    }
                }
                else{
                  urlRetval = "forward:srReporteContingencia.do";
                }
        }
        catch(Exception e){
            urlRetval = "forward:srReporteContingencia.do";
            e.printStackTrace();
        }
        finally{
            try{
                if(in!=null){
                    in.close();
                }
                if(m_con!=null){
                   m_con.disconnect();
                }
            }
            catch(Exception e)
            {
                e.printStackTrace();
            }
        }
        return urlRetval;
    }

    public String procesaReporteSegundoIntento(HttpServletResponse response, String urlReporte, Model model)throws IOException, ServletException {

        String urlRetval = null;

        ServletOutputStream ouputStream=null;
        HttpURLConnection m_con=null;
        InputStream in=null;
        String contentType=null;
        try{
                java.net.URL url = new java.net.URL(urlReporte.toString().replace(" ","%20"));
                m_con = (HttpURLConnection)url.openConnection();

                if(m_con!=null){
                    contentType= m_con.getHeaderField("Content-Type");
                    if(contentType!=null && contentType.equals("application/pdf")){
                        in = new BufferedInputStream(m_con.getInputStream());
                        readHttpAndWriteInOutputStream(in, response, contentType);
                    }
                    else{
                       urlRetval = "reportError";
                       StringBuffer sf =  readHttpAndWriteInOutput(m_con.getInputStream());
                       model.addAttribute("textError", sf.toString());
                    }
                }
                else{
                  urlRetval = "reportError";
                  model.addAttribute("textError", "<H4>No se puede conectar al servidor de reportes, esta fuera de servicio o hay problemas de conexion</h4>");
                }
        }
        catch(Exception e){
            e.printStackTrace();
            response.reset();
            urlRetval =  "reportError";
            model.addAttribute("textError", "<H4>Error interno en la ejecución del reporte</h4>");
            model.addAttribute("detailError", e.getStackTrace());
        }
        finally{
            try{
                if(in!=null){
                    in.close();
                }
                if(m_con!=null){
                   m_con.disconnect();
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
        return urlRetval;
    }
    
    /**
     * Ajusta campos a la derecha de una cadena pasada por parametro
     * @param szBuffer String que contiene la cadena a ajustar sus campos
     * @param tam_dest Tamaño de la cadena
     * @param szCar Caracter de relleno
     * @return La cadena ajustada a la derecha
     */
    public static String AjustaCampoDerecha(String szBuffer, int tam_dest, String szCar) {
        int iLen;
        String szDest = "";
        for (int i = 0; i < tam_dest; i++) {
            szDest = szDest.concat(szCar);
        }
        iLen = szBuffer.length();
        if (iLen > tam_dest) {
            // El número es excesivamente largo (desbordamiento).
            // Truncarlo e indicar el error en el valor de retorno
            iLen = tam_dest;
        }
        szDest = szDest.substring(0, (tam_dest - iLen)) + szBuffer;
        return szDest;

    }
    
  /**
     * Ajusta una cadena a la izquierda
     * @param szBuffer Es el tamaño la cadena
     * @param tam_dest Es el tamaño de la cadena
     * @return Es el campo formateado
     */
    public static String AjustaCampoIzquierda(String szBuffer, int tam_dest, String szCar) {
        int iLen = szBuffer.length ();
        StringBuffer sbdest=new StringBuffer ();
        for(int i=0;i<tam_dest-iLen;i++)
        {
            sbdest.append (szCar);
        }
        if (iLen > tam_dest)
        {
            // El número es excesivamente largo (desbordamiento).
            // Truncarlo e indicar el error en el valor de retorno
            iLen = tam_dest;
        }
        return szBuffer+sbdest;
        
    }    

    /**
       * borra los espacios en blanco repetidos
       *
       * @param   text    input to format
       * @return  String  string with single whitespaces
       */
      public static String reduceWhitespace(String text)
      {
        char[]    chars   = text.trim().toCharArray();
        int       start   = 0;
        int       end     = 0;
        int       occ     = 0;

        // replace n occurrences with one
        for (int i = 0; i < chars.length; i++)
        {
          if ( Character.isWhitespace(chars[i]) )
          {
            occ++;
          }
          else
          {
            occ=0;
          }

          if (occ<2)
          {
            chars[end++] = chars[i];
          }
        } // end for

        // return formatted string
        return new String(chars,start,end);
      }

   public static String generateRandomNumber2(int length) {
        StringBuffer buffer = new StringBuffer();
        String characters = "12345678901234567890";

        try{
            int charactersLength = characters.length();
            for (int i = 0; i < length; i++) {
                    double index = Math.random() * charactersLength;
                    buffer.append(characters.charAt((int) index));
            }
        }
        catch(Exception e){
            buffer.append("0");
        }

        return buffer.toString();
    }

   public static String generateRandomNumber(int length) {
        String  randomString = "";
        try{
            double multi = Math.pow(10.0, length);
            int randomValue = (int)(Math.random() * multi);
            randomString = String.valueOf(randomValue);
        }
        catch(Exception e){
            randomString = "0";
        }

        return randomString;
    }

   public static String fn_getCleanTextExpReg(String text){
        if(text!=null&&text.trim().length()>0){
            text=text.trim().replaceAll("[^a-zA-Z 0-9ÑñäëïöüÄËÏÖÜÁÉÍÓÚáéíóú]", "").replaceAll("\\s+", " ");
        }
        return text;
   }
   
   public static String fn_getCleanTextLenGreathree(String text){
       if(text!=null&&text.trim().length()>0){
            String[] arrText = text.split(" ");
            String textAux="";
            for (String value : arrText) {
                if(value.trim().length()>1){
                    textAux=textAux+" "+value;
                }
            }
            text=textAux.trim();           
       }
       return text;       
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
    public static String fn_getCleanFileName(String text){
        if(text!=null&&text.trim().length()>0){
            text=text.trim().replaceAll("[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]", "").replaceAll("\\s+", " ");
        }
        return text;
   }
   @Autowired
   private ApplicationProperties applicationProperties;
   public byte[] GenerarReporte(HttpServletRequest request, HttpServletResponse response,List lista,String nombreReporte,Map parametros,int TipoArchivo) throws JRException
   {
       //JasperReport rpt = JasperCompileManager.compileReport(reportFile);
       //String coReporte= ServletUtility.getInstancia().loadRequestParameter(request, "coReporte");
       //String coParametros=ServletUtility.getInstancia().loadRequestParameter(request, "coParametros");
       
       HashMap Lparameters = null;
       byte[] repBytes = null;
       Connection conn=null;
       ServletOutputStream ouputStream=null;
       
       ServletContext sc = request.getSession().getServletContext();
       
       //String rutaArchivo=sc.getRealPath(applicationProperties.getRutaReportes()+nombreReporte+".jasper");
       String rutaArchivo=sc.getRealPath("/reports/"+nombreReporte+".jasper");
       
       File reportFile = new File(rutaArchivo);
       
       try {     
            //Lparameters = llenaHashMap(parametros);
            Lparameters=(HashMap) (parametros); 
            
            //WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            //DataSource datasource = (DataSource) applicationContext.getBean(pdataSource);
            //conn = datasource.getConnection();
            if (TipoArchivo==1)//Excel
            {
                repBytes = generateXLSOutput(Lparameters, reportFile, lista);
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + nombreReporte + ".xls\"");
            }
            else{
                
            }
            
       } catch (Exception e) {
            System.out.println("***************************************************************");
            System.out.println("Error in Report:--> " + nombreReporte + ".jrxml");
            System.out.println("Parametros:--> " + Lparameters.toString());
            System.out.println("***************************************************************");
            e.printStackTrace();
       }
       return repBytes;
   }
   public byte[] GenerarReporteLista(String rutaReporte,List lista,String nombreReporte,Map parametros,int TipoArchivo) throws JRException
   {
       HashMap Lparameters = null;
       byte[] repBytes = null;
              
       String rutaArchivo=rutaReporte;
       
       File reportFile = new File(rutaArchivo);
       
       try {     
            Lparameters=(HashMap) (parametros); 
            
            if (TipoArchivo==1)//Excel
            {
                repBytes = generateXLSOutput(Lparameters, reportFile, lista);
            }
            else{
                repBytes = generatePDFOutputLista(Lparameters, reportFile, lista);
            }
            
       } catch (JRException e) {            
            e.printStackTrace();
       } catch (SQLException e) {
           e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
       return repBytes;
   }
   private byte[] generateXLSOutput(Map Lparameters, File reporte, List lista) throws JRException, SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
            //JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, Lparameters, conn);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, Lparameters,new JRBeanCollectionDataSource(lista));
            
            byteArrayOutputStream = new ByteArrayOutputStream();
            //Creacion del XLS
            JRXlsExporter exporter = new JRXlsExporter();
            
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);

            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);


            exporter.setParameter(JRXlsExporterParameter.IS_COLLAPSE_ROW_SPAN , Boolean.TRUE);//ok
            exporter.setParameter(JRXlsExporterParameter.OUTPUT_STREAM,byteArrayOutputStream);


            exporter.exportReport();
        } catch (Exception e) {
            e.printStackTrace();
        }finally {
            try{
                if(byteArrayOutputStream!=null){
                  byteArrayOutputStream.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        return byteArrayOutputStream.toByteArray();
    }
   private byte[] generatePDFOutputLista(Map Lparameters, File reporte, List lista)  {//throws JRException, SQLException, IOException
        byte[] repBytes = null;
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
            JRBeanCollectionDataSource datosLis = new JRBeanCollectionDataSource(lista);
            repBytes = JasperRunManager.runReportToPdf(jasperReport, Lparameters,datosLis );

       }
        catch (JRException e) {            
            e.printStackTrace();
       } catch (Exception e) {
            e.printStackTrace();
        }
        return repBytes;
   }
   public static String numeroRandom(int min,int max)
   {       
       //int valor = ThreadLocalRandom.current().nextInt(min,max);
       int valor = (int) (Math.random()*(min-max+1)+max+1);
       String dato = String.format("%09d", valor);
       return dato;
   }
   
   public static byte[] readFileToByteArray(File archivo)
   {
       FileInputStream fis = null;
       byte[] bArray = new byte[(int) archivo.length()];
       try {
           fis = new FileInputStream(archivo);
           fis.read(bArray);
           fis.close();
       } catch (IOException e) {
           e.printStackTrace();
       }
       return bArray;
   }
   
   public static String generateRandomLetter(int length){
       String [] _cadena = {"A", "B", "C", "D", "E", "F", "G", "H", "I", "J", 
       "K", "L", "M","N","O","P","Q","R","S","T","U","V","W", "X","Y","Z","0","1",
       "2","3","4","5","6","7","8","9"};       
       String [] cadena = {"0","1",
       "2","3","4","5","6","7","8","9"};              
       String  randomString = "";
       try {
           for (int i = 0; i < length; i++) {
                int index = (int) Math.round(Math.random() * 9 ) ;
                randomString += cadena[index];
           }
       } catch (Exception e) {
           randomString = "0";
       }
       return randomString;
   } 
   public static String DescriptaCadena(String cadena){
        String resultado="";
        byte[] bytesEncriptados = Base64.getDecoder().decode(cadena);
        resultado = new String(bytesEncriptados);
        resultado= resultado.replace("WSXSDSDFFGDFGSDSEUY87OLKMN12DF",""); 
          bytesEncriptados = Base64.getDecoder().decode(resultado);
        resultado = new String(bytesEncriptados);
        return resultado;
   }
     
}
