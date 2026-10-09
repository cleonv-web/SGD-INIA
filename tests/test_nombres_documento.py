"""Servicios Java con datos ficticios: sin Spring, HTTP, BD ni documentos.

Requiere SGD compilar y el JDK incluido en el paquete.
"""
from pathlib import Path
import os
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
JAVA_TEST = r'''
import java.lang.reflect.*;
import java.util.*;
import pe.gob.onpe.tramitedoc.bean.*;
import pe.gob.onpe.tramitedoc.service.impl.*;
import fr.opensagres.xdocreport.document.IXDocReport;

public class NombreDocumentoTest {
    static int comprobaciones = 0;
    static void check(boolean ok, String message) {
        comprobaciones++;
        if (!ok) throw new AssertionError(message);
    }
    static DocumentoDatoBean datos(String tipo, String siglas) {
        DocumentoDatoBean b = new DocumentoDatoBean();
        b.setNuAnn("2026"); b.setNuEmi("FICTICIO"); b.setNuCorEmi("1");
        b.setTiEmi(tipo); b.setCoDoc("003"); b.setTipoDoc("INFORME");
        b.setTiCap("01"); b.setSiglasDoc(siglas); b.setCoDepEmi("FICTI");
        b.setEsDocEmi("5"); b.setNumeroDoc("1");
        return b;
    }
    static DocumentoObjBean archivo() {
        DocumentoObjBean b = new DocumentoObjBean();
        b.setNombreArchivo("ficticio.docx"); b.setTipoDoc("docx"); b.setNuTamano(1);
        return b;
    }
    static class Normal extends DocumentoObjServiceImp {
        DocumentoDatoBean b;
        Normal(DocumentoDatoBean value) { b=value; }
        public DocumentoDatoBean getDatosDoc(String a, String e) { return b; }
        public DocumentoObjBean getNombreArchivo(String a, String e, String c) { return archivo(); }
    }
    static class Inter extends DocumentoObjInterServiceImp {
        DocumentoDatoBean b;
        Inter(DocumentoDatoBean value) { b=value; }
        public DocumentoDatoBean getDatosDoc(String a, String e) { return b; }
        public DocumentoObjBean getNombreArchivo(String a, String e, String c) { return archivo(); }
    }
    static void inyectar(Object object, Class<?> owner, String name, InvocationHandler handler) throws Exception {
        Field f=owner.getDeclaredField(name); f.setAccessible(true);
        f.set(object, Proxy.newProxyInstance(f.getType().getClassLoader(), new Class[]{f.getType()}, handler));
    }
    static String esperado(String marker, String siglas, String ext) {
        String value=siglas == null ? "null" : siglas.replace('/', '-').replace('\\', '-');
        return "2026|INFORME-"+marker+"1-2026-"+value+ext;
    }
    public static void main(String[] args) throws Exception {
        for (String siglas : new String[]{"MIDAGRI-INIA-GG/UTI", "GG\\UTI", "SIN-BARRA", null}) {
            for (String tipo : new String[]{"01","05"}) {
                Normal n=new Normal(datos(tipo,siglas));
                inyectar(n,DocumentoObjServiceImp.class,"auditoriaMovimientoDocDao", (p,m,a)->null);
                check(esperado("R",siglas,".docx").equals(n.getNombreGeneraDocx("2026","FICTICIO","3").getNoDocumento()),"Generar: siglas forman una carpeta");
                for(String ope : new String[]{"3","4"}) {
                    String ext=ope.equals("3")?".docx":".pdf";
                    check(esperado("R",siglas,ext).equals(n.getNombreCargaDoc("2026","FICTICIO",ope).getNoDocumento()),"Cargar: nombre distinto al generado");
                    check(esperado("R",siglas,ext).equals(n.getNombreDocEmiReporte("2026","FICTICIO",ope,null,"").getNoDocumento()),"Abrir repositorio: nombre distinto");
                    if (tipo.equals("01")) {
                        check(esperado("R",siglas,ext).equals(n.getNombreDoc("2026","FICTICIO",ope,null).getNoDocumento()),"Abrir: nombre distinto");
                    }
                }
                check(Objects.equals(siglas,n.b.getSiglasDoc()),"Las siglas oficiales fueron alteradas");
            }
            Inter i=new Inter(datos("06",siglas));
            inyectar(i,DocumentoObjInterServiceImp.class,"auditoriaMovimientoDocDao", (p,m,a)->null);
            check(esperado("I",siglas,".docx").equals(i.getNombreGeneraDocx("2026","FICTICIO","3").getNoDocumento()),"Inter: nombre inválido al generar");
            check(esperado("I",siglas,".docx").equals(i.getNombreDoc("2026","FICTICIO","3",null).getNoDocumento()),"Inter: nombre inválido al abrir");
        }
        Normal n=new Normal(datos("01","FICTICIO"));
        Object[] respuestas={Collections.emptyList(),null,Collections.singletonList(new PlantillaDocx()),new RuntimeException("FICTICIO")};
        for (Object respuesta : respuestas) {
            inyectar(n,DocumentoObjServiceImp.class,"datosPlantillaService", (p,m,a)-> {
                if (respuesta instanceof RuntimeException) throw (RuntimeException)respuesta;
                check("FICTI".equals(a[0]) && "003".equals(a[1]),"La plantilla debe corresponder a tipo y dependencia");
                return respuesta;
            });
            check(!"OK".equals(n.validarPlantillaDocx("2026","FICTICIO")),"Una plantilla ausente o inválida fue aceptada");
        }
        PlantillaDocx valida=new PlantillaDocx();
        valida.setTemplate((IXDocReport)Proxy.newProxyInstance(IXDocReport.class.getClassLoader(),new Class[]{IXDocReport.class},(p,m,a)-> {throw new AssertionError("No se permite generar documentos en la prueba");}));
        inyectar(n,DocumentoObjServiceImp.class,"datosPlantillaService",(p,m,a)->Collections.singletonList(valida));
        check("OK".equals(n.validarPlantillaDocx("2026","FICTICIO")),"No se reconoció una plantilla cargada");
        n.b=null;
        check(!"OK".equals(n.validarPlantillaDocx("2026","FICTICIO")),"Se aceptaron datos inexistentes");
        System.out.println(comprobaciones+" comprobaciones Java offline aprobadas; no se generaron documentos.");
        System.exit(0); // El servicio heredado crea un temporizador no daemon al formar la URL.
    }
}
'''


class NombresDocumentoTest(unittest.TestCase):
    def test_servicios_con_datos_ficticios(self):
        suffix = '.exe' if os.name == 'nt' else ''
        jdk = (next((ROOT / 'java/windows').glob('jdk*')) if os.name == 'nt' else ROOT / 'java/linux') / 'bin'
        libs = ROOT / 'fuentes/Sgd/tramitedoc/web/target/tramitedoc-web-1.0/WEB-INF/lib'
        logic = ROOT / 'fuentes/Sgd/tramitedoc/logic'
        self.assertTrue((libs / 'tramitedoc-logic-1.0.jar').is_file(), 'Ejecutar SGD compilar primero')
        cp = os.pathsep.join([str(logic / 'target/classes'), str(libs / '*')])
        with tempfile.TemporaryDirectory(prefix='sgd-nombres-') as directory:
            path = Path(directory)
            source = path / 'NombreDocumentoTest.java'
            source.write_text(JAVA_TEST, encoding='utf-8')
            base = logic / 'src/main/java/pe/gob/onpe/tramitedoc'
            sources = [source, base / 'service/DocumentoObjService.java',
                       base / 'service/impl/DocumentoObjServiceImp.java',
                       base / 'service/impl/DocumentoObjInterServiceImp.java',
                       base / 'util/NombreArchivoDocumento.java']
            compiled = subprocess.run([str(jdk / ('javac' + suffix)), '-encoding', 'UTF-8', '-cp', cp,
                                       '-d', directory, *map(str, sources)], capture_output=True, text=True)
            self.assertEqual(compiled.returncode, 0, compiled.stderr)
            result = subprocess.run([str(jdk / ('java' + suffix)), '-cp', directory + os.pathsep + cp,
                                     'NombreDocumentoTest'], capture_output=True, text=True)
            self.assertEqual(result.returncode, 0, result.stderr)
            print(result.stdout.strip())


if __name__ == '__main__':
    unittest.main()
