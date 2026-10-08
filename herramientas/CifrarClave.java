import java.io.*;
import java.nio.charset.StandardCharsets;
import pe.gob.onpe.libreria.util.Utility;
import pe.gob.onpe.libreria.util.ConstantesSec;
public class CifrarClave {
  public static void main(String[] args) throws Exception {
    String clave=new BufferedReader(new InputStreamReader(System.in,StandardCharsets.UTF_8)).readLine();
    System.out.print(Utility.getInstancia().cifrar(clave,ConstantesSec.SGD_SECRET_KEY_PASSWORD));
  }
}
