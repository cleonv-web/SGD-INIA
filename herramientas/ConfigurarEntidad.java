import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.util.*;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.libreria.util.ConstantesSec;
public class ConfigurarEntidad {
  public static void main(String[] args) throws Exception {
    Path target=Paths.get(args[0]); Properties p=new Properties();
    try(Reader r=Files.newBufferedReader(target,StandardCharsets.UTF_8)){p.load(r);}
    p.setProperty("nro_ruc",Utility.getInstancia().cifrar(args[4],ConstantesSec.SGD_SECRET_KEY_PROPERTIES));
    p.setProperty("sigla_institucion",args[3]);
    p.setProperty("ruta_temporal",Utility.getInstancia().cifrar(args[1],ConstantesSec.SGD_SECRET_KEY_PROPERTIES));
    p.setProperty("ruta_reportes",args[2]);
    String texto=new String(Files.readAllBytes(target),StandardCharsets.UTF_8);
    for(String k:Arrays.asList("nro_ruc","sigla_institucion","ruta_temporal","ruta_reportes")){
      String value=p.getProperty(k).replace("\\","/");
      if(texto.matches("(?s).*?(?m)^"+k+"\\s*=.*"))texto=texto.replaceAll("(?m)^"+k+"\\s*=.*$",java.util.regex.Matcher.quoteReplacement(k+"="+value));
      else texto+="\n"+k+"="+value+"\n";
    }
    Files.write(target,texto.getBytes(StandardCharsets.UTF_8));
    System.out.println("Entidad INIA y rutas locales configuradas.");
  }
}
