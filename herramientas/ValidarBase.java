import java.nio.file.*;
import java.nio.charset.StandardCharsets;
import java.sql.*;
/** Ejecuta la validación incluida por JDBC; sirve también con BD en otra VM. */
public class ValidarBase {
  public static void main(String[] args) throws Exception {
    Class.forName("org.postgresql.Driver");
    String sql=new String(Files.readAllBytes(Paths.get(args[1])),StandardCharsets.UTF_8);
    try(Connection c=DriverManager.getConnection(args[0],"sgd_app",System.getenv("SGD_DB_PASSWORD"))) {
      c.setAutoCommit(false);
      try(Statement s=c.createStatement()) {
        s.execute(sql);
        c.commit();
      } catch(Exception e) {c.rollback();throw e;}
    }
    System.out.println("Validación JDBC de base, usuarios y funciones completada.");
  }
}
