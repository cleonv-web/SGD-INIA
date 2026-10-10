"""Verifica el enlace de blobs en el DAO real con JdbcTemplate simulado."""
from pathlib import Path
import os
import subprocess
import tempfile
import unittest

ROOT=Path(__file__).resolve().parents[1]
JAVA=r'''
import java.lang.reflect.*;
import java.sql.*;
import java.util.*;
import org.springframework.jdbc.core.*;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramiteconv.dao.impl.postgresql.EmiDocumentoAdmDaoImp;

public class ArchivoDocumentoTest {
 static int checks;
 static void check(boolean b,String m){checks++;if(!b)throw new AssertionError(m);}
 static class JdbcFicticio extends JdbcTemplate {
  final boolean exists;int reads,writes,blobs;long bytes;List<String> statements=new ArrayList<>();byte[] expected;
  JdbcFicticio(boolean e,byte[] b){exists=e;expected=b;}
  public int queryForInt(String sql,Object[] args){reads++;return exists?1:0;}
  public <T>T execute(String sql,PreparedStatementCallback<T> callback){
   statements.add(sql);writes++;
   PreparedStatement ps=(PreparedStatement)Proxy.newProxyInstance(getClass().getClassLoader(),new Class[]{PreparedStatement.class},(o,m,a)->{
    if(m.getName().equals("setBytes")){byte[] data=(byte[])a[1];check(Arrays.equals(expected,data),"Blob alterado o incompleto");blobs++;bytes+=data.length;return null;}
    if(m.getName().equals("executeUpdate"))return 1;
    if(m.getName().equals("setString")||m.getName().equals("close"))return null;
    throw new AssertionError("JDBC inesperado: "+m.getName());
   });
   try{return callback.doInPreparedStatement(ps);}catch(SQLException e){throw new AssertionError(e);}
  }
 }
 public static void main(String[] args)throws Exception{
  byte[] payload=new byte[1048576];new Random(20261010).nextBytes(payload);
  for(boolean exists:new boolean[]{false,true})for(String extension:new String[]{"pdf","docx"}){
   JdbcFicticio jdbc=new JdbcFicticio(exists,payload);EmiDocumentoAdmDaoImp dao=new EmiDocumentoAdmDaoImp();
   Field field=SimpleJdbcDaoBase.class.getDeclaredField("jdbcTemplate");field.setAccessible(true);field.set(dao,jdbc);
   DocumentoObjBean bean=new DocumentoObjBean();bean.setNuAnn("2026");bean.setNuEmi("FICTICIO");bean.setNombreArchivo("FICTICIO."+extension);bean.setDocumento(payload);
   String result=dao.updDocumentoObj(bean);check("OK".equals(result),"DAO falló: "+result);
   int expected=exists&&extension.equals("pdf")?1:2;
   check(jdbc.reads==1,"Cantidad de consultas previa inesperada");
   check(jdbc.blobs==expected,"Cantidad de enlaces de blob inesperada");
   check(jdbc.bytes==(long)expected*payload.length,"Bytes de parámetros incorrectos");
   check(jdbc.writes==(exists&&extension.equals("docx")?2:1),"Cantidad de sentencias inesperada");
   System.out.println("archivo="+extension+" existente="+exists+" lectura_previa="+jdbc.reads+" escrituras="+jdbc.writes+" parametros_blob="+jdbc.blobs+" bytes_enlazados="+jdbc.bytes);
  }
  System.out.println(checks+" comprobaciones de blobs aprobadas; JDBC interceptado, ninguna sentencia ejecutada.");
 }
}
'''

class ArchivoDocumentoOfflineTest(unittest.TestCase):
    def test_dao_real_insert_y_update_sin_conexion(self):
        suffix='.exe' if os.name=='nt' else ''
        jdk=(next((ROOT/'java/windows').glob('jdk*')) if os.name=='nt' else ROOT/'java/linux')/'bin'
        libs=ROOT/'fuentes/Sgd/tramitedoc/web/target/tramitedoc-web-1.0/WEB-INF/lib'
        cp=str(libs/'*')
        source_dao=ROOT/'fuentes/Sgd/tramitedoc/logic/src/main/java/pe/gob/onpe/tramiteconv/dao/impl/postgresql/EmiDocumentoAdmDaoImp.java'
        with tempfile.TemporaryDirectory(prefix='sgd-blobs-') as temp:
            src=Path(temp)/'ArchivoDocumentoTest.java';src.write_text(JAVA,encoding='utf-8')
            result=subprocess.run([str(jdk/('javac'+suffix)),'-encoding','UTF-8','-cp',cp,'-d',temp,str(src),str(source_dao)],capture_output=True,text=True)
            self.assertEqual(result.returncode,0,result.stderr)
            result=subprocess.run([str(jdk/('java'+suffix)),'-cp',temp+os.pathsep+cp,'ArchivoDocumentoTest'],capture_output=True,text=True)
            self.assertEqual(result.returncode,0,result.stdout+result.stderr)
            print(result.stdout.strip())

if __name__=='__main__':unittest.main()
