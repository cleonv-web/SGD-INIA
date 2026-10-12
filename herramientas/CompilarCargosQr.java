import java.io.*;
import java.sql.Timestamp;
import java.util.*;
import net.sf.jasperreports.engine.*;
import net.sf.jasperreports.engine.data.JRMapCollectionDataSource;

/** Compila las plantillas de cargo y opcionalmente genera muestras ficticias para verificar el QR. */
public class CompilarCargosQr {
    public static void main(String[] args) throws Exception {
        File root = new File(args[0]);
        File reports = new File(root, "fuentes/Sgd/tramitedoc/web/src/main/webapp/reports");
        for (String name : new String[]{"TDR15", "TDR_VOUCHER", "TDR_VOUCHER2"}) {
            JasperReport report = JasperCompileManager.compileReport(new File(reports, name + ".jrxml").getPath());
            net.sf.jasperreports.engine.util.JRSaver.saveObject(report, new File(reports, name + ".jasper"));
            System.out.println("Plantilla QR compilada: " + name);
            if (args.length < 2) continue;
            Map<String,Object> row = new HashMap<String,Object>();
            for (JRField field : report.getFields()) if (field.getValueClass().equals(String.class)) row.put(field.getName(), "");
            row.put("nuExpediente", "2026-0000002"); row.put("nuCorrExp", "2026-0000002");
            row.put("deInstitucion", "INIA"); row.put("deMesaPartes", "MESA DE PARTES");
            row.put("codUser", "mesa_demo"); row.put("cclave", "9477");
            row.put("feExp", Timestamp.valueOf("2026-10-11 19:28:00"));
            row.put("deOriEmi", "CIUDADANO DE PRUEBA"); row.put("nuFolios", "1");
            row.put("deDepDes", "MESA DE PARTES"); row.put("deUsuario", "mesa_demo");
            Map<String,Object> params = new HashMap<String,Object>();
            params.put("P_CONSULTA_URL", "https://calidad.inia.gob.pe/consulta/inicio.do");
            File logo = new File(root, "fuentes/ConsultaTuTramite/web/src/main/resources/META-INF/web-resources/images/inia-logo.png");
            try (InputStream image = new FileInputStream(logo)) {
                params.put("P_LOGO_DIR", image);
                JasperPrint print = JasperFillManager.fillReport(report, params,
                        new JRMapCollectionDataSource(Collections.<Map<String,?>>singletonList(row)));
                File out = new File(args[1]); out.mkdirs();
                JasperExportManager.exportReportToPdfFile(print, new File(out, name + "-qr-muestra.pdf").getPath());
                if (print.getPages().size() != 1) throw new IllegalStateException("El cargo debe ocupar una página");
            }
        }
    }
}
