"""Validacion del DAO/servicio reales con JDBC simulado y JVM aisladas.

No conecta a PostgreSQL, no despliega ni reinicia servicios. Compara con la misma
fuente sin el ajuste de cache, manteniendo las correcciones previas intactas.
"""
from pathlib import Path
import json
import os
import subprocess
import tempfile
import unittest

ROOT = Path(__file__).resolve().parents[1]
BASE = ROOT / 'fuentes/Sgd/tramitedoc/logic/src/main/java'
LIBS = ROOT / 'fuentes/Sgd/tramitedoc/web/target/tramitedoc-web-1.0/WEB-INF/lib'
DAO = BASE / 'pe/gob/onpe/tramiteconv/dao/impl/postgresql/DatosPlantillaDaoImp.java'
SERVICE = BASE / 'pe/gob/onpe/tramitedoc/service/impl/DocumentoXmlServiceImp.java'

JAVA = r'''
import java.io.*;
import java.lang.management.*;
import java.lang.ref.*;
import java.lang.reflect.*;
import java.nio.file.*;
import java.security.*;
import java.sql.*;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.*;
import java.util.zip.*;
import fr.opensagres.xdocreport.document.*;
import fr.opensagres.xdocreport.document.registry.*;
import pe.gob.onpe.tramitedoc.bean.*;
import pe.gob.onpe.tramitedoc.service.impl.DocumentoXmlServiceImp;
import pe.gob.onpe.tramiteconv.dao.impl.postgresql.DatosPlantillaDaoImp;

public class CachePlantillasTest {
 static final AtomicInteger checks = new AtomicInteger();
 static final List<WeakReference<IXDocReport>> references =
     Collections.synchronizedList(new ArrayList<WeakReference<IXDocReport>>());
 static Constructor<?> constructor;
 static Method mapRow;
 static boolean cached;
 static byte[][] templates;
 static void check(boolean value,String message) {
   checks.incrementAndGet(); if(!value)throw new AssertionError(message);
 }
 static class Servicio extends DocumentoXmlServiceImp {
   final DatosPlantillaDoc datos;
   final PlantillaDocx plantilla;
   Servicio(PlantillaDocx p,String token) {
     plantilla=p;datos=new DatosPlantillaDoc();
     datos.setTituloDependencia("UNIDAD FICTICIA");
     datos.setDeTipoDoc("INFORME");datos.setNumeroDoc("QA-2026");
     datos.setFechaDoc("La Molina, 10 de octubre de 2026");
     datos.setNombreAnio("DENOMINACION FICTICIA");
     datos.setDeEmpEmi("REMITENTE FICTICIO");
     datos.setDeCargoFunEmiMae("DIRECTOR");datos.setDeDepEmiMae("UNIDAD FICTICIA");
     datos.setEmpDestino("DESTINATARIO FICTICIO");
     datos.setDeCargoFunDestMae("ADMINISTRADOR");datos.setDeDepDestMae("OTRA UNIDAD");
     datos.setNombreDestinatario("DESTINATARIO EXTERNO FICTICIO");
     datos.setEntidadPrivadaDestinatario("ENTIDAD FICTICIA");
     datos.setDireccionDestinatario("DIRECCION FICTICIA");datos.setCargo("CARGO FICTICIO");
     datos.setDeAsunto(token + " & < >");
   }
   public DatosPlantillaDoc datosParaPlantilla(String a,String e){return datos;}
   protected PlantillaDocx obtenerPlantilla(DatosPlantillaDoc d){return plantilla;}
 }
 static PlantillaDocx cargar(final byte[] bytes) throws Exception {
   ResultSet rs=(ResultSet)Proxy.newProxyInstance(CachePlantillasTest.class.getClassLoader(),
       new Class[]{ResultSet.class},(o,m,a)-> {
         if(m.getName().equals("getBytes")) {
           check("bl_doc".equals(a[0]),"Columna blob inesperada"); return bytes;
         }
         if(m.getName().equals("getString")) {
           String column=(String)a[0];
           if(column.equals("co_dep"))return "QA";
           if(column.equals("co_tipo_doc"))return "INF";
           if(column.equals("nom_archivo"))return "FICTICIO.docx";
         }
         throw new AssertionError("JDBC inesperado: "+m.getName());
       });
   Object mapper=constructor.newInstance(new DatosPlantillaDaoImp());
   PlantillaDocx p=(PlantillaDocx)mapRow.invoke(mapper,rs,0);
   check("QA".equals(p.getCoDep()) && "INF".equals(p.getCoTipoDoc()),"Metadatos alterados");
   if(p.getTemplate()!=null) {
     IXDocReport report=p.getTemplate();
     check((XDocReportRegistry.getRegistry().getReport(report.getId())==report)==cached,
           "El registro global no coincide con el modo esperado");
     references.add(new WeakReference<IXDocReport>(report));
   }
   return p;
 }
 static Map<String,byte[]> entries(byte[] zip)throws Exception {
   Map<String,byte[]> result=new TreeMap<String,byte[]>();
   try(ZipInputStream in=new ZipInputStream(new ByteArrayInputStream(zip))) {
     ZipEntry entry; byte[] buffer=new byte[8192];
     while((entry=in.getNextEntry())!=null) {
       if(entry.isDirectory())continue;
       ByteArrayOutputStream out=new ByteArrayOutputStream();int n;
       while((n=in.read(buffer))!=-1)out.write(buffer,0,n);
       result.put(entry.getName(),out.toByteArray());
     }
   }
   return result;
 }
 static String hash(byte[] bytes)throws Exception {
   byte[] sum=MessageDigest.getInstance("SHA-256").digest(bytes);
   StringBuilder result=new StringBuilder();for(byte b:sum)result.append(String.format("%02x",b&255));
   return result.toString();
 }
 static String fingerprint(Map<String,byte[]> zip)throws Exception {
   MessageDigest digest=MessageDigest.getInstance("SHA-256");
   for(Map.Entry<String,byte[]> e:zip.entrySet()) {
     digest.update(e.getKey().getBytes("UTF-8"));digest.update((byte)0);digest.update(e.getValue());
   }
   return hash(digest.digest());
 }
 static String xml(Map<String,byte[]> zip)throws Exception {
   StringBuilder result=new StringBuilder();
   for(Map.Entry<String,byte[]> e:zip.entrySet())
     if(e.getKey().startsWith("word/") && e.getKey().endsWith(".xml"))
       result.append(new String(e.getValue(),"UTF-8"));
   return result.toString();
 }
 static String generar(byte[] template,String token,Path save)throws Exception {
   DocumentoObjBean doc=new Servicio(cargar(template),token).crearDocx("2026","FICTICIO");
   check(doc!=null && doc.getDocumento().length>1000,"DOCX incompleto");
   Map<String,byte[]> zip=entries(doc.getDocumento());String text=xml(zip);
   check(text.contains(token),"Datos del documento ausentes o mezclados");
   check(!text.contains("${"),"Campos sin resolver");
   check(text.contains("&amp;") && text.contains("&lt;"),"Escape XML alterado");
   java.util.regex.Matcher m=java.util.regex.Pattern.compile("TOKEN_[0-9]{4}").matcher(text);
   while(m.find())check(token.equals(m.group()),"Datos de otro documento en el resultado");
   if(save!=null)Files.write(save,doc.getDocumento());
   return fingerprint(zip);
 }
 static byte[] actualizar(byte[] template)throws Exception {
   Map<String,byte[]> zip=entries(template);int changes=0;
   for(Map.Entry<String,byte[]> e:zip.entrySet())if(e.getKey().endsWith(".xml")) {
     String text=new String(e.getValue(),"UTF-8");
     if(text.contains("${ASUNTO}")) {
       e.setValue(text.replace("${ASUNTO}","VERSION_NUEVA ${ASUNTO}").getBytes("UTF-8"));changes++;
     }
   }
   check(changes>0,"Fixture no contiene ASUNTO");
   ByteArrayOutputStream out=new ByteArrayOutputStream();
   try(ZipOutputStream z=new ZipOutputStream(out)) {
     for(Map.Entry<String,byte[]> e:zip.entrySet()) {
       z.putNextEntry(new ZipEntry(e.getKey()));z.write(e.getValue());z.closeEntry();
     }
   }
   return out.toByteArray();
 }
 static long usedAfterGc()throws Exception {
   for(int i=0;i<3;i++){System.gc();Thread.sleep(60);}
   return ManagementFactory.getMemoryMXBean().getHeapMemoryUsage().getUsed();
 }
 public static void main(String[] args)throws Exception {
   cached=args[2].equals("control");Path out=Paths.get(args[1]);Files.createDirectories(out);
   Class<?> mapper=Class.forName("pe.gob.onpe.tramiteconv.dao.impl.postgresql.DatosPlantillaDaoImp$PlantillaRowMapper");
   constructor=mapper.getDeclaredConstructor(DatosPlantillaDaoImp.class);constructor.setAccessible(true);
   mapRow=mapper.getDeclaredMethod("mapRow",ResultSet.class,int.class);mapRow.setAccessible(true);
   templates=new byte[][]{Files.readAllBytes(Paths.get(args[0],"INFORME-INIA.docx")),
                         Files.readAllBytes(Paths.get(args[0],"OFICIO-INIA.docx"))};
   List<String> fingerprints=new ArrayList<String>();
   for(int i=0;i<8;i++) fingerprints.add(generar(templates[i%2],String.format("TOKEN_%04d",i),
       out.resolve("muestra-"+i+".docx")));
   byte[] newer=actualizar(templates[0]);
   generar(newer,"TOKEN_0098",out.resolve("version-nueva.docx"));
   check(xml(entries(Files.readAllBytes(out.resolve("version-nueva.docx")))).contains("VERSION_NUEVA"),
         "No se respeta la actualizacion de plantilla");
   generar(templates[0],"TOKEN_0099",out.resolve("version-original.docx"));
   check(!xml(entries(Files.readAllBytes(out.resolve("version-original.docx")))).contains("VERSION_NUEVA"),
         "La nueva version contamina otra carga");
   for(byte[] invalid:new byte[][]{null,new byte[0],new byte[]{1,2,3}}) {
     PlantillaDocx p=cargar(invalid);check(p.getTemplate()==null,"Plantilla invalida aceptada");
     check(new Servicio(p,"TOKEN_0999").crearDocx("2026","FICTICIO")==null,
           "Se genero archivo parcial ante error de plantilla");
   }
   ExecutorService pool=Executors.newFixedThreadPool(8);CountDownLatch start=new CountDownLatch(1);
   List<Future<String>> futures=new ArrayList<Future<String>>();
   try {
     for(int i=0;i<32;i++) {
       final int operation=i;
       futures.add(pool.submit(()->{start.await();return generar(templates[operation%2],
           String.format("TOKEN_%04d",1000+operation),null);}));
     }
     start.countDown();for(Future<String> f:futures)f.get(60,TimeUnit.SECONDS);
   } finally {pool.shutdownNow();pool.awaitTermination(30,TimeUnit.SECONDS);}
   long warm=usedAfterGc();long first=0;
   for(int batch=0;batch<2;batch++) {
     for(int i=0;i<64;i++)generar(templates[i%2],String.format("TOKEN_%04d",2000+batch*64+i),null);
     long used=usedAfterGc();if(batch==0)first=used;
   }
   long retained=usedAfterGc();int alive=0;
   for(WeakReference<IXDocReport> ref:references)if(ref.get()!=null)alive++;
   int reports=XDocReportRegistry.getRegistry().getCachedReports().size();
   if(cached)check(reports>=160 && alive>=160,"El control no reproduce la retencion");
   else {check(reports==0,"El registro global crece");check(alive==0,"Se retienen reportes fuera del registro");}
   String fingerprintJson="[\""+String.join("\",\"",fingerprints)+"\"]";
   System.out.println("{\"modo\":\""+args[2]+"\",\"checks\":"+checks.get()+
       ",\"generaciones\":170,\"hilos\":8,\"cache_reportes\":"+reports+
       ",\"reportes_vivos\":"+alive+",\"heap_max_mib\":"+Runtime.getRuntime().maxMemory()/1048576+
       ",\"heap_caliente_mib\":"+warm/1048576.0+",\"heap_lote1_mib\":"+first/1048576.0+
       ",\"heap_final_mib\":"+retained/1048576.0+",\"huellas_docx\":"+fingerprintJson+"}");
 }
}
'''


class CachePlantillasTests(unittest.TestCase):
    @classmethod
    def setUpClass(cls):
        cls.temp = tempfile.TemporaryDirectory(prefix='sgd-cache-plantillas-')
        cls.addClassCleanup(cls.temp.cleanup)
        cls.root = Path(cls.temp.name)
        suffix = '.exe' if os.name == 'nt' else ''
        jdk = (next((ROOT / 'java/windows').glob('jdk*')) if os.name == 'nt'
               else ROOT / 'java/linux') / 'bin'
        cls.java = jdk / ('java' + suffix)
        cls.cp = str(LIBS / '*')
        harness = cls.root / 'CachePlantillasTest.java'
        harness.write_text(JAVA, encoding='utf-8')
        cls.classes = {}
        for mode in ('control', 'candidato'):
            folder = cls.root / mode
            folder.mkdir()
            dao = folder / 'DatosPlantillaDaoImp.java'
            source = DAO.read_text(encoding='utf-8')
            if mode == 'control':
                source = source.replace('TemplateEngineKind.Freemarker, false)',
                                        'TemplateEngineKind.Freemarker )')
            dao.write_text(source, encoding='utf-8')
            sources = [harness, dao, SERVICE,
                       BASE / 'pe/gob/onpe/tramitedoc/util/TextoDocumento.java',
                       BASE / 'pe/gob/onpe/tramitedoc/web/util/ApplicationProperties.java']
            result = subprocess.run([str(jdk / ('javac' + suffix)), '-encoding', 'UTF-8',
                                     '-cp', cls.cp, '-d', str(folder), *map(str, sources)],
                                    capture_output=True, text=True, timeout=60)
            if result.returncode:
                raise AssertionError(result.stderr)
            cls.classes[mode] = folder
        cls.results = {}
        cls.output = ROOT / 'output/qa-cache-plantillas-20261010'
        cls.output.mkdir(parents=True, exist_ok=True)
        for mode, heap in (('control', 1024), ('candidato', 1024), ('candidato', 4096)):
            label = mode + '-' + str(heap)
            output = cls.output / label
            result = subprocess.run([str(cls.java), '-Xmx' + str(heap) + 'm',
                                     '-cp', str(cls.classes[mode]) + os.pathsep + cls.cp,
                                     'CachePlantillasTest', str(ROOT / 'plantillas-docx'),
                                     str(output), mode], capture_output=True, text=True,
                                    timeout=120)
            (cls.output / (label + '.log')).write_text(result.stdout + result.stderr,
                                                       encoding='utf-8')
            if result.returncode:
                raise AssertionError(label + ': ' + result.stdout + result.stderr)
            rows = [line for line in result.stdout.splitlines() if line.startswith('{')]
            cls.results[label] = json.loads(rows[-1])
            print(label + ': ' + json.dumps({key: value for key, value in cls.results[label].items()
                                            if key != 'huellas_docx'}), flush=True)
        (cls.output / 'resultados.json').write_text(
            json.dumps(cls.results, ensure_ascii=False, indent=2) + '\n', encoding='utf-8')

    def test_documentos_iguales_en_control_y_candidato(self):
        expected = self.results['control-1024']['huellas_docx']
        for label in ('candidato-1024', 'candidato-4096'):
            self.assertEqual(self.results[label]['huellas_docx'], expected, label)

    def test_registro_sin_crecimiento_y_reportes_liberados(self):
        self.assertGreaterEqual(self.results['control-1024']['cache_reportes'], 160)
        for label in ('candidato-1024', 'candidato-4096'):
            self.assertEqual(self.results[label]['cache_reportes'], 0)
            self.assertEqual(self.results[label]['reportes_vivos'], 0)

    def test_retencion_reducida_en_jvm_comparable(self):
        control = self.results['control-1024']['heap_final_mib']
        candidate = self.results['candidato-1024']['heap_final_mib']
        self.assertLess(candidate, control * 0.5,
                        'No se observo la reduccion esperada bajo el mismo heap.')


if __name__ == '__main__':
    unittest.main()
