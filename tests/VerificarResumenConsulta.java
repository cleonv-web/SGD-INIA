import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import java.util.regex.*;
import java.sql.*;
import javax.el.ELProcessor;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.*;
import org.springframework.jdbc.datasource.DriverManagerDataSource;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.dao.impl.postgresql.ConsultaDocDaoImpl;

/** Prueba de lectura del DAO real y vista aislada; no omite el CAPTCHA del portal. */
public class VerificarResumenConsulta {
    static Document view;
    static ELProcessor el;
    static int checks;
    static void check(boolean value, String message) {
        if (!value) throw new AssertionError(message);
        checks++;
    }
    static String escape(String text) {
        return text.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;")
                .replace("\"", "&quot;").replace("'", "&#39;");
    }
    static Object eval(String expression) {
        if (expression.matches("\\$\\{[^}]*\\}"))
            return el.eval(expression.substring(2, expression.length()-1));
        Matcher parts=Pattern.compile("\\$\\{([^}]*)\\}").matcher(expression);
        StringBuffer output=new StringBuffer();
        while(parts.find()) parts.appendReplacement(output,Matcher.quoteReplacement(String.valueOf(el.eval(parts.group(1)))));
        parts.appendTail(output);return output.toString();
    }
    static String render(Node node) {
        if (node.getNodeType() == Node.TEXT_NODE) return escape(node.getNodeValue());
        if (node.getNodeType() != Node.ELEMENT_NODE) return "";
        String name=node.getNodeName();
        if (name.startsWith("jsp:") && !name.equals("jsp:root")) return "";
        if (name.equals("c:out")) {
            Object value=eval(((Element)node).getAttribute("value"));return value==null?"":escape(value.toString());
        }
        if (name.equals("c:if") && !Boolean.TRUE.equals(eval(((Element)node).getAttribute("test")))) return "";
        boolean tag=!name.equals("c:if") && !name.equals("jsp:root");
        StringBuilder result=new StringBuilder();
        if(tag) {
            result.append("<").append(name); NamedNodeMap attrs=node.getAttributes();
            for(int i=0;i<attrs.getLength();i++) {
                Node attr=attrs.item(i);Object value=eval(attr.getNodeValue());
                result.append(" ").append(attr.getNodeName()).append("=\"").append(escape(String.valueOf(value))).append("\"");
            }
            result.append(">");
        }
        for(Node child=node.getFirstChild();child!=null;child=child.getNextSibling()) result.append(render(child));
        if(tag) result.append("</").append(name).append(">");
        return result.toString();
    }
    static String render(ConsultaDocBean bean) {
        el=new ELProcessor();el.defineBean("docExpediente",bean);
        Map<String,Object> request=new HashMap<String,Object>();request.put("contextPath","http://127.0.0.1:8085/consulta");
        Map<String,Object> context=new HashMap<String,Object>();context.put("request",request);el.defineBean("pageContext",context);
        return render(view.getDocumentElement());
    }
    public static void main(String[] args) throws Exception {
        Path root=Paths.get(args[0]),output=Paths.get(args[2]);Files.createDirectories(output);
        DriverManagerDataSource data=new DriverManagerDataSource(args[1],"postgres",System.getenv("PGPASSWORD"));
        ConsultaDocDaoImpl dao=new ConsultaDocDaoImpl();dao.init(data);
        List<ConsultaDocBean> rows=dao.getBuscarExpediente("2026-0000002");
        check(rows.size()==1,"Expediente de laboratorio encontrado");ConsultaDocBean bean=rows.get(0);
        check("EN PROYECTO".equals(bean.getEstado()),"Estado real del registro");
        check(!bean.isTieneDerivacion(),"El expediente en proyecto no tiene derivación");
        check("MESA DE PARTES DEMO".equals(bean.getOficinaRegistro()),"Oficina real de registro");
        check(bean.getOficinaAtencion().isEmpty(),"No se inventa una oficina en atención");
        check("11/10/2026 19:28:54".equals(bean.getFechaExpediente()),"Hora y minutos reales de registro");
        ConsultaDocBean pending=dao.getBuscarExpediente("2026-0000001").get(0);
        check("PARA DESPACHO".equals(pending.getEstado()),"Destino preparado no equivale a derivación emitida");
        check(!pending.isTieneDerivacion(),"No se presenta destino de documento pendiente como atención efectiva");
        String fixture=new String(Files.readAllBytes(root.resolve("tests/fixtures/consulta-derivaciones.sql")),StandardCharsets.UTF_8);
        String consulta=new String(Files.readAllBytes(root.resolve("fuentes/ConsultaTuTramite/logic/src/main/resources/sql/consulta-resumen.sql")),StandardCharsets.UTF_8)
                .replace("idosgd.","").replace(":anio","'2026'").replace(":emision","'ROOT'");
        try(Connection connection=data.getConnection()) {
            connection.setReadOnly(true);
            try(Statement statement=connection.createStatement();ResultSet resultado=statement.executeQuery(fixture+consulta)) {
                check(resultado.next(),"Fixture de derivaciones disponible sin insertar datos");
                check("ARCHIVADO".equals(resultado.getString("estado_destino")),"Se usa el último destino efectivo");
                check("OFICINA B".equals(resultado.getString("oficina_atencion")),"Se excluyen destinos anulados, eliminados, de borradores y de otro año");
                check("Atención concluida".equals(resultado.getString("obsfinalizar")),"Observación corresponde al estado seleccionado");
            }
        }
        DocumentBuilderFactory xml=DocumentBuilderFactory.newInstance();xml.setNamespaceAware(true);
        xml.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        view=xml.newDocumentBuilder().parse(root.resolve("fuentes/ConsultaTuTramite/web/src/main/webapp/WEB-INF/views/viewExpediente.jspx").toFile());
        String html=render(bean);
        check(html.contains("No registrada"),"Institución ausente explícita");
        check(html.contains("Sin observaciones registradas."),"Observaciones ausentes explícitas");
        check(html.contains("No hay una derivación vigente registrada."),"Explicación del seguimiento pendiente");
        check(html.contains("href=\"http://127.0.0.1:8085/consulta/inicio.do\""),"Enlace de nueva consulta completo");
        check(!render(new ConsultaDocBean()).contains("null"),"Campos ausentes sin null en pantalla");
        ConsultaDocBean hostile=new ConsultaDocBean();hostile.setSumilla("<script>alert('prueba')</script>");
        check(render(hostile).contains("&lt;script&gt;"),"Contenido escapado en la vista");
        hostile.setTieneDerivacion(true);hostile.setOficinaAtencion("Oficina de prueba");
        check(!render(hostile).contains("No hay una derivación vigente registrada."),"Aviso de pendiente solo cuando corresponde");
        String css=new String(Files.readAllBytes(root.resolve("fuentes/ConsultaTuTramite/web/src/main/resources/META-INF/web-resources/css/consulta-inia.css")),StandardCharsets.UTF_8);
        String page="<!doctype html><html lang=\"es\"><head><meta charset=\"utf-8\"><meta name=\"viewport\" content=\"width=device-width,initial-scale=1\"><title>Verificación del resultado INIA</title><style>"+css+"</style></head><body><main class=\"wrap\"><p style=\"font-size:11px;color:#65725c;margin:25px 0\">Vista de verificación · expediente de laboratorio · lectura de la base local</p><section class=\"result-section\">"+html+"</section></main></body></html>";
        Files.write(output.resolve("resultado-laboratorio.html"),page.getBytes(StandardCharsets.UTF_8));
        Files.write(output.resolve("verificacion-dao-vista.json"),("{\"comprobaciones\":"+checks+",\"correctas\":true,\"base\":\"solo lectura\",\"estado\":\"EN PROYECTO\",\"derivacion\":false}").getBytes(StandardCharsets.UTF_8));
        System.out.println(checks+" comprobaciones DAO/vista correctas. Sin cambios en la base ni en el CAPTCHA.");
    }
}
