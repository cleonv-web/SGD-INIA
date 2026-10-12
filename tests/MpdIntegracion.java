import java.sql.*;
import java.util.*;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.datasource.DataSourceTransactionManager;
import org.springframework.transaction.TransactionStatus;
import org.springframework.transaction.support.DefaultTransactionDefinition;
import pe.gob.onpe.mpvdoc.bean.*;
import pe.gob.onpe.mpvdoc.dao.impl.postgresql.MpvdocDaoImpl;

/** Prueba el DAO real exclusivamente sobre una copia temporal; revierte cada caso. */
public class MpdIntegracion {
    static void require(boolean ok, String message) { if (!ok) throw new AssertionError(message); }
    public static void main(String[] args) throws Exception {
        require(args[0].matches("jdbc:postgresql://[^/]+/mpd_prueba_[a-z0-9_]+"), "Solo base temporal mpd_prueba_.");
        Class.forName("org.postgresql.Driver");
        Connection connection = DriverManager.getConnection(args[0],"sgd_app",System.getenv("MPD_TEST_DB_PASSWORD"));
        connection.setAutoCommit(false);
        SingleConnectionDataSource ds = new SingleConnectionDataSource(connection,true);
        MpvdocDaoImpl dao = new MpvdocDaoImpl(); dao.init(ds);
        DataSourceTransactionManager manager=new DataSourceTransactionManager(ds);
        TransactionStatus transaction=null;
        String[] types={"009","001","002","003","020","234"};
        try {
            for (int i=0;i<types.length;i++) {
                transaction=manager.getTransaction(new DefaultTransactionDefinition());
                MpvdocBean b=new MpvdocBean(); b.setIdTipoPersona(i==2?"02":"03");
                b.setDni("99000001");b.setPat("PRUEBA");b.setMat("AISLADA");b.setNom("FICTICIO");
                b.setRuc("20990000001");b.setRaz("EMPRESA FICTICIA DE PRUEBA");
                b.setTipoDocumento(types[i]);b.setNrodoc("PRUEBA-"+i);b.setAsu("PRUEBA AISLADA MPD "+types[i]);
                b.setNombreArchivo("prueba.pdf");b.setArchivoBytes("%PDF-1.4 prueba aislada".getBytes("UTF-8"));
                b.setDir("Direccion de prueba");b.setFolios("1");b.setCorreo("prueba@example.invalid");b.setTel("900000000");
                b.setDep("15");b.setPro("01");b.setDis("01");
                LinkedList<DocumentoFilesStructBean> annexes=new LinkedList<DocumentoFilesStructBean>();
                for(int n=1;n<=i%3;n++) {
                    DocumentoFilesStructBean a=new DocumentoFilesStructBean();a.setInid(n);a.setVctit("anexo"+n+".pdf");a.setVcarcda(b.getArchivoBytes());annexes.add(a);
                }
                b.setFilesProcess(annexes);
                ResultMpvdocBean result=dao.getGrabarExpediente(b);
                require("OK".equals(result.getResultado()), "Registro "+types[i]+": "+result.getResultado());
                PreparedStatement statement=connection.prepareStatement("SELECT r.co_dep,r.co_emp_res,r.co_tip_doc_adm,r.ccod_origing,r.es_doc_emi,(SELECT count(*) FROM idosgd.tdtv_anexos a WHERE a.nu_ann=r.nu_ann AND a.nu_emi=r.nu_emi),(SELECT count(*) FROM idosgd.tdtx_remitos_resumen s WHERE s.nu_ann=r.nu_ann AND s.nu_emi=r.nu_emi),(SELECT octet_length(bl_doc) FROM idosgd.tdtv_archivo_doc d WHERE d.nu_ann=r.nu_ann AND d.nu_emi=r.nu_emi) FROM idosgd.tdtv_remitos r WHERE nu_ann=? AND nu_emi=?");
                statement.setString(1,result.getAnio());statement.setString(2,result.getEmision());ResultSet rows=statement.executeQuery();
                require(rows.next(),"No existe remito");require("49001".equals(rows.getString(1)),"Oficina incorrecta");
                require("49001".equals(rows.getString(2)),"Responsable incorrecto");require(types[i].equals(rows.getString(3)),"Tipo incorrecto");
                require("06".equals(rows.getString(4)) && "5".equals(rows.getString(5)),"Estado/origen incorrecto");
                require(rows.getInt(6)==annexes.size(),"Faltan anexos: esperados "+annexes.size()+", guardados "+rows.getInt(6));
                require(rows.getInt(7)==1,"No aparece en resumen SGD");require(rows.getInt(8)==b.getArchivoBytes().length,"PDF distinto");rows.close();statement.close();
                DocumentoBean query=new DocumentoBean();query.setNuAnn(result.getAnio());query.setNuEmi(result.getEmision());
                List<DocumentoBean> voucher=dao.getListaReporteBusquedaVoucher(query);
                require(voucher!=null && voucher.size()==1,"Falta cargo");require(result.getExpediente().equals(voucher.get(0).getNuExpediente()),"Cargo de otro expediente");
                if ("02".equals(b.getIdTipoPersona())) {
                    statement=connection.prepareStatement("SELECT idosgd.pk_sgd_descripcion_de_proveedor(?)");statement.setString(1,b.getRuc());rows=statement.executeQuery();rows.next();
                    require(b.getRaz().equals(rows.getString(1)),"Se pierde la razon social escrita manualmente");rows.close();statement.close();
                }
                statement=connection.prepareStatement("SELECT count(*) FROM idosgd.pk_sgd_tramite_consulta_tu_expediente(?)");
                statement.setString(1,result.getExpediente());rows=statement.executeQuery();rows.next();require(rows.getInt(1)==1,"La consulta no encuentra el expediente");rows.close();statement.close();
                System.out.println("OK tipo="+types[i]+" persona="+b.getIdTipoPersona()+" anexos="+annexes.size()+" PDF/resumen/cargo/responsable");
                manager.rollback(transaction);transaction=null;
                if(i==types.length-1) {
                    // Repetir el numero de anexo fuerza un fallo de BD real.
                    annexes.get(1).setInid(1);b.setAsu("PRUEBA FALLO ANEXOS AISLADA");
                    result=dao.getGrabarExpediente(b);require(!"OK".equals(result.getResultado()),"Se informo exito con un anexo fallido");
                    statement=connection.prepareStatement("SELECT count(*) FROM idosgd.tdtv_remitos WHERE de_asu=?");statement.setString(1,b.getAsu());rows=statement.executeQuery();rows.next();require(rows.getInt(1)==0,"Quedo un expediente incompleto");rows.close();statement.close();
                    System.out.println("OK fallo real de anexos revierte el expediente completo");
                }
            }
        } finally { if(transaction!=null && !transaction.isCompleted())manager.rollback(transaction);connection.rollback(); connection.close(); }
    }
}
