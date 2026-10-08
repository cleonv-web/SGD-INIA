/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.io.BufferedReader;
import pe.gob.onpe.tramitedoc.service.ReporteService;

import net.sf.jasperreports.engine.export.JRPdfExporter;
import net.sf.jasperreports.engine.export.JRPdfExporterParameter;
import net.sf.jasperreports.engine.export.JRXlsExporter;
import net.sf.jasperreports.engine.export.JRXlsExporterParameter;
import net.sf.jasperreports.engine.util.JRLoader;

import javax.servlet.ServletContext;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.sql.DataSource;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
//import java.nio.file.Files;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRExporterParameter;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.JasperRunManager;
import net.sf.jasperreports.engine.data.JRBeanCollectionDataSource;
import org.codehaus.jackson.JsonParser;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import pe.gob.onpe.libreria.util.ServletUtility;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoEmiConsulBean;

import pe.gob.onpe.tramitedoc.service.ConsultaEmiDocService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
/**
 *
 * @author ecueva
 */
@Service("reporteService")
public class ReporteServiceImp  implements ReporteService {
    
    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private ConsultaEmiDocService consultaEmiDocService;
    public void ejecutaReporte(HttpServletRequest request, HttpServletResponse response, String pdataSource) {
        byte[] repBytes = null;

//        String coImpDirecto=request.getParameter("coImpDirecto") == null ? "N" : request.getParameter("coImpDirecto");
        String coReporte= ServletUtility.getInstancia().loadRequestParameter(request, "coReporte");
        String coParametros=ServletUtility.getInstancia().loadRequestParameter(request, "coParametros");
        String coSubReportes=ServletUtility.getInstancia().loadRequestParameter(request, "coSubReportes");
        String coImagenes=ServletUtility.getInstancia().loadRequestParameter(request, "coImagenes");

        File reportFileImg;
        File reportFileSubReport;
        String cadena;
        String vparam;
        String vvalor;
        Connection conn=null;
        ServletOutputStream ouputStream=null;
        HashMap Lparameters = null;

        try{
            Lparameters = llenaHashMap(coParametros);

            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
//            DataSource datasource = (DataSource) applicationContext.getBean("dataSource");
            DataSource datasource = (DataSource) applicationContext.getBean(pdataSource);
            conn = datasource.getConnection();

            ServletContext sc = request.getSession().getServletContext();

            //File reportFile = new File(sc.getRealPath("/reports/"+coReporte+".jasper"));//getFileReport(coReporte,sc);
            File reportFile = new File(sc.getRealPath(applicationProperties.getRutaReportes()+coReporte+".jasper"));//getFileReport(coReporte,sc);

//            if (coSubReporte!=null && coSubReporte != "") {
//                reportFileSubReport = new File(sc.getRealPath("/reports/"+coSubReporte+".jasper"));
//                Lparameters.put("SUBREPORT_DIR", reportFileSubReport.toString());
//            }
            if (coSubReportes!=null && coSubReportes!="") {
                String parametros = coSubReportes.replace("|","&");
                String[] listPar = parametros.split("&");
                for(int i = 0; i<listPar.length; i++){
                    try{
                        cadena =listPar[i];
                        vparam = cadena.substring(0,cadena.indexOf("="));
                        vvalor = cadena.substring(cadena.indexOf("=") + 1);
                        //reportFileSubReport = new File(sc.getRealPath("/reports/"+vvalor+".jasper"));
                        reportFileSubReport = new File(sc.getRealPath(applicationProperties.getRutaReportes()+vvalor+".jasper"));
                        Lparameters.put(vparam, reportFileSubReport.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }

            /* Procesamos las imagenes  */
            if (coImagenes!=null && coImagenes!="") {
                String parametros = coImagenes.replace("|","&");
                String[] listPar = parametros.split("&");
                for(int i = 0; i<listPar.length; i++){
                    try{
                        cadena =listPar[i];
                        vparam = cadena.substring(0,cadena.indexOf("="));
                        vvalor = cadena.substring(cadena.indexOf("=") + 1);
                        //reportFileImg = new File(sc.getRealPath("/reports/"+vvalor));
                        reportFileImg = new File(sc.getRealPath(applicationProperties.getRutaReportes()+vvalor));
                        Lparameters.put(vparam, reportFileImg.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
//            Lparameters.put("P_URLSERVER_IMG",buildReportURL().toString());

            if (coReporte.contains("XLS")) {
                repBytes = generateXLSOutput(Lparameters, reportFile, conn);
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + coReporte + "\""); //Configurar cabecera http
            }else{

//                if(coImpDirecto.equals("S")){
//                    repBytes = generatePDFDirect(Lparameters, reportFile, conn);
//
//                }else{
                    repBytes = generatePDFOutput(Lparameters, reportFile, conn);
//                }
                response.setContentType("application/pdf");
            }

            /*Enviamos el Resultado*/
            response.setContentLength(repBytes.length);
            ouputStream = response.getOutputStream();
            ouputStream.write(repBytes, 0, repBytes.length);
            ouputStream.flush();
        } catch (Exception e) {
            System.out.println("***************************************************************");
            System.out.println("Error in Report:--> " + coReporte + ".jrxml");
            System.out.println("Parametros:--> " + Lparameters.toString());
            System.out.println("***************************************************************");
            e.printStackTrace();
        }finally {
            try{
                if(conn!=null){
                  conn.close();
                }
                if(ouputStream!=null){
                 ouputStream.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void ejecutaReporteLista(HttpServletRequest request,HttpServletResponse response)
    {
        String coReporte= ServletUtility.getInstancia().loadRequestParameter(request, "coReporte");
        String coParametros=ServletUtility.getInstancia().loadRequestParameter(request, "coParametros");
        String coSubReportes=ServletUtility.getInstancia().loadRequestParameter(request, "coSubReportes");
        String coImagenes=ServletUtility.getInstancia().loadRequestParameter(request, "coImagenes");
        
        File reportFileImg;
        File reportFileSubReport;
        String cadena;
        String vparam;
        String vvalor;
       
        ServletOutputStream ouputStream=null;
        HashMap Lparameters = null;
        List lista = null;
        int posObjeto = 0;
        int posFiltro = 0;
        int posParametros = 0;
        int posServicio = 0;
        posObjeto = coParametros.indexOf("P_OBJETO")+9;
        posFiltro = coParametros.indexOf("P_FILTROS")+10;
        posParametros = coParametros.indexOf("P_OBJETO")-1;
        posServicio = coParametros.indexOf("P_SERVICIO")+11;
        String nomObjeto = coParametros.substring(posObjeto,posFiltro-11);
        String filtros = coParametros.substring(posFiltro,posServicio-12);
        String params = coParametros.substring(0,posParametros);
        String nomServicio = coParametros.substring(posServicio);
        byte[] repBytes = null;
        String rutaBase = "/reports/";
        
        
        Object obj = new Object();
       
        try {
            obj = CargarDatosObjeto(nomObjeto,filtros);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(ReporteServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            Logger.getLogger(ReporteServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            Logger.getLogger(ReporteServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(ReporteServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }
        try {
            //if (nomObjeto.equals("BuscarDocumentoEmiConsulBean")) {            
            if (nomServicio.equals("consultaEmiDocService.getRutaReporteLista")) {
                lista = consultaEmiDocService.getListaReporte((BuscarDocumentoEmiConsulBean) obj);
            }
            /*if (lista == null || lista.isEmpty()) {
              
               throw new Exception("ListaVacia");
            }*/
            Lparameters = llenaHashMap(params);
            ServletContext sc = request.getSession().getServletContext();
            
            File reportFile = new File(sc.getRealPath(rutaBase+coReporte+".jasper"));//getFileReport(coReporte,sc);
            
             /* Procesamos las imagenes  */
            if (coImagenes!=null && coImagenes!="") {
                String parametros = coImagenes.replace("|","&");
                String[] listPar = parametros.split("&");
                for(int i = 0; i<listPar.length; i++){
                    try{
                        cadena =listPar[i];
                        vparam = cadena.substring(0,cadena.indexOf("="));
                        vvalor = cadena.substring(cadena.indexOf("=") + 1);                        
                        reportFileImg = new File(sc.getRealPath(rutaBase+vvalor));
                        Lparameters.put(vparam, reportFileImg.toString());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }

            }
            if (coReporte.contains("XLS")) {
                 repBytes = generateXLSOutputLista(Lparameters, reportFile, lista);
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + coReporte + "\""); //Configurar cabecera http
            }
            else{
                repBytes = generatePDFOutputLista(Lparameters, reportFile, lista);
                response.setContentType("application/pdf");
            }
            
              /*Enviamos el Resultado*/
            response.setContentLength(repBytes.length);
            ouputStream = response.getOutputStream();
            ouputStream.write(repBytes, 0, repBytes.length);
            ouputStream.flush();
        } catch (Exception e) {
            /*if (e.getMessage().equals("ListaVacia")) {              
               response.setContentType("text/html");
               System.out.println("<script type=\"text/javascript\">");
               System.out.println("alert('No existe información para exportar.');");
               System.out.println("</script>");
               e.printStackTrace();
            }
            else
            {*/
                System.out.println("***************************************************************");
                System.out.println("Error in Report:--> " + coReporte + ".jrxml");
                System.out.println("Parametros:--> " + Lparameters.toString());
                System.out.println("***************************************************************");
                e.printStackTrace();
           // }
            
        }finally {
            try{                
                if(ouputStream!=null){
                 ouputStream.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
    }
    
    public void abrirReporteTemporal(HttpServletRequest request,HttpServletResponse response)
    {
        String coReporte= ServletUtility.getInstancia().loadRequestParameter(request, "coReporte");
        //ServletContext sc = request.getSession().getServletContext();
        
        String rutaBase = applicationProperties.getRutaTemporal()+"/"+coReporte;
        String coSubReportes=ServletUtility.getInstancia().loadRequestParameter(request, "coSubReportes");
        //String nombreArchivo ="";
        //String rutaBase = sc.getRealPath("/informe/"+coReporte);
        //File reportFile = new File(sc.getRealPath(coReporte));
        File reportFile = new File(rutaBase);
        ServletOutputStream ouputStream=null;
        try {
            //byte[] archivoArray = Files.readAllBytes(reportFile.toPath());            
            byte[] archivoArray = Utilidades.readFileToByteArray(reportFile);
            //byte[] archivoArray = new byte[(int) reportFile.length()];
            //BufferedReader br = new BufferedReader(new FileReader(rutaBase));
            //byte[] archivoArray = br.toString().getBytes();
            if (coReporte.contains("xls")) {                 
                response.setContentType("application/vnd.ms-excel");
                response.setHeader("Content-Disposition", "attachment;filename=\"" + coReporte + "\""); //Configurar cabecera http
            }
            else{                
                response.setContentType("application/pdf");
            }
                /*Enviamos el Resultado*/
            response.setContentLength(archivoArray.length);
            ouputStream = response.getOutputStream();
            ouputStream.write(archivoArray, 0, archivoArray.length);
            ouputStream.flush();
        } catch (IOException ex) {
            Logger.getLogger(ReporteServiceImp.class.getName()).log(Level.SEVERE, null, ex);
        }finally {
            try{                
                if(ouputStream!=null){
                 ouputStream.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }
        }
        
    }
    private Object CargarDatosObjeto(String nombreObjeto,String valores) throws ClassNotFoundException, InstantiationException, IllegalAccessException, IOException
    {
        //Class clase = Class.forName("pe.gob.onpe.tramitedoc.bean." + nombreObjeto);
        Class clase = Class.forName(nombreObjeto);
        Object obj = clase.newInstance();
        ObjectMapper mapper = new ObjectMapper();
        mapper.configure(JsonParser.Feature.ALLOW_UNQUOTED_FIELD_NAMES, true);
        mapper.configure(JsonParser.Feature.ALLOW_SINGLE_QUOTES, true);
        obj = mapper.readValue(valores, clase);
        
        return obj;
    }
    private HashMap llenaHashMap(String parametros){
        HashMap parameters = new HashMap();
        parametros = parametros.replace("|","&");
        String[] listPar = parametros.split("&");
        for(int i = 0; i<listPar.length; i++){
          String cadena =listPar[i];
          if(cadena.length()>0){
            parameters.put(cadena.substring(0,cadena.indexOf("=")), cadena.substring(cadena.indexOf("=") + 1));
          }
        }
        return parameters;
    }

    private byte[] generatePDFOutput(Map Lparameters, File reporte, Connection conn) throws JRException, SQLException, IOException {
        byte[] repBytes = null;
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
            repBytes = JasperRunManager.runReportToPdf(jasperReport, Lparameters, conn);

        } catch (Exception e) {
            e.printStackTrace();
        }
        return repBytes;
    }
    private byte[] generatePDFOutputLista(Map Lparameters, File reporte, List lista) throws JRException, SQLException, IOException {
        byte[] repBytes = null;
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
            repBytes = JasperRunManager.runReportToPdf(jasperReport, Lparameters, new JRBeanCollectionDataSource(lista));

        } catch (Exception e) {
            e.printStackTrace();
        }
        return repBytes;
    }
    private byte[] generatePDFDirect(Map Lparameters, File reporte, Connection conn) throws JRException, SQLException, IOException {
        byte[] repBytes = null;
        ByteArrayOutputStream baos = null;
        try {

            baos = new ByteArrayOutputStream();
            JRPdfExporter exporter = new JRPdfExporter();
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, Lparameters, conn);

            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRPdfExporterParameter.PDF_JAVASCRIPT,"this.print({bUI: false,bSilent: true,bShrinkToFit: false});");
//            exporter.setParameter(JRPdfExporterParameter.IS_ENCRYPTED, Boolean.TRUE);
//            exporter.setParameter(JRPdfExporterParameter.IS_128_BIT_KEY, Boolean.TRUE);
//            exporter.setParameter(JRPdfExporterParameter.PERMISSIONS,new Integer(PdfWriter.ALLOW_PRINTING | PdfWriter.ALLOW_ASSEMBLY));
            exporter.setParameter(JRPdfExporterParameter.METADATA_AUTHOR, "Secretaria General - SG");
            exporter.setParameter(JRPdfExporterParameter.METADATA_TITLE, "Sistema Gestion Documental");
            exporter.setParameter(JRPdfExporterParameter.METADATA_CREATOR, "SIO");

            exporter.setParameter(JRExporterParameter.OUTPUT_STREAM, baos);
            exporter.exportReport();
            repBytes = baos.toByteArray();

        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try{
                if(baos!=null){
                  baos.close();
                }
            }catch(Exception e){
                e.printStackTrace();
            }

        }
        return repBytes;
    }

    private byte[] generateXLSOutput(Map Lparameters, File reporte, Connection conn) throws JRException, SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, Lparameters, conn);
            byteArrayOutputStream = new ByteArrayOutputStream();
            //Creacion del XLS
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            //exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);//ok
            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
//            exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.TRUE);
//            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
//            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS , Boolean.TRUE);

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
    private byte[] generateXLSOutputLista(Map Lparameters, File reporte, List lista) throws JRException, SQLException, IOException {
        ByteArrayOutputStream byteArrayOutputStream = null;
        try {
            JasperReport jasperReport = (JasperReport) JRLoader.loadObject(reporte);
            JasperPrint jasperPrint = JasperFillManager.fillReport(jasperReport, Lparameters, new JRBeanCollectionDataSource(lista));
            byteArrayOutputStream = new ByteArrayOutputStream();
            //Creacion del XLS
            JRXlsExporter exporter = new JRXlsExporter();
            exporter.setParameter(JRExporterParameter.JASPER_PRINT, jasperPrint);
            exporter.setParameter(JRXlsExporterParameter.IS_ONE_PAGE_PER_SHEET, Boolean.FALSE);
            //exporter.setParameter(JRXlsExporterParameter.IS_DETECT_CELL_TYPE, Boolean.TRUE);
            //exporter.setParameter(JRXlsExporterParameter.IS_FONT_SIZE_FIX_ENABLED, Boolean.TRUE);//ok
            exporter.setParameter(JRXlsExporterParameter.IS_WHITE_PAGE_BACKGROUND, Boolean.FALSE);
//            exporter.setParameter(JRXlsExporterParameter.IS_IGNORE_CELL_BORDER, Boolean.TRUE);
//            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_ROWS, Boolean.TRUE);
//            exporter.setParameter(JRXlsExporterParameter.IS_REMOVE_EMPTY_SPACE_BETWEEN_COLUMNS , Boolean.TRUE);

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
    private StringBuffer buildReportURL() {
        StringBuffer urlReporte = new StringBuffer();
        urlReporte.append("http://");
        //urlReporte.append(applicationProperties.getStaticImageServerA());
        return urlReporte;
    }    
}
