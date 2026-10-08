/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.servlet;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.service.DocumentoObjService;
import pe.gob.onpe.tramitedoc.service.DocumentoXmlService;
import pe.gob.onpe.tramitedoc.util.LsAleatorioViewDoc;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

/**
 *
 * @author WCutipa
 */
public class DocumentoSrvlt extends HttpServlet{
    private DocumentoObjService documentoObjService;
    private DocumentoXmlService documentoXmlService;
    private ApplicationProperties applicationProperties;    
    
    @Override
    public void init() throws ServletException {
        super.init();    
    }

    protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String accion = request.getParameter("accion");
        if (accion.equals("abrirDoc")) {
            loadDocumento(request, response);
        } else if (accion.equals("cargaDoc")) {
            cargaDocumento(request, response);
        } else if (accion.equals("abrirAnexo")) {
            loadAnexo(request, response);
        }else if (accion.equals("abrirAnexos")) {
            loadAnexos(request, response);
        } else if (accion.equals("creaDocx")) {
            creaDocx(request, response);
        } else if (accion.equals("creaPdfx")) {
            creaPdfx(request, response);
        } else if(accion.equals("cargaDocAnexo")){
            cargaDocumentoAnexo(request, response);
        } else if(accion.equals("abrirActaNotificacion")){
            loadActaNotificacion(request, response);
        } 
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void loadDocumento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
         //jazanero
        String pnuAnn,pnuEmi,ptiCap,pnroView,pAbreWord;
        
        //String pnuAnn,pnuEmi,ptiCap,pnroView;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi");
        ptiCap = request.getParameter("tiCap");
        pnroView = request.getParameter("nroView");
        pAbreWord = request.getParameter("pAbreWord")==null? "NO":request.getParameter("pAbreWord");
        
        
        if (pnuAnn!=null && pnuEmi!=null && ptiCap!=null && pnroView!=null && pAbreWord!=null
                && !pnuAnn.equals("") && !pnuEmi.equals("") && !ptiCap.equals("") && !pnroView.equals("")){
        //YUAL estaba en la version anterior    
       // if (pnuAnn!=null && pnuEmi!=null && ptiCap!=null && pnroView!=null && !pnuAnn.equals("") && !pnuEmi.equals("") && !ptiCap.equals("") && !pnroView.equals("")){
            boolean bisView=LsAleatorioViewDoc.getInstancia().remove(pnroView);
            if(bisView){
                bisView=false;
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.documentoObjService = (DocumentoObjService) applicationContext.getBean("documentoObjService");
            DocumentoObjBean docObjBean = null;
               
            //jazanero
            if(pAbreWord.equals("SI"))
                docObjBean = documentoObjService.leerDocumento(pnuAnn,pnuEmi,ptiCap,pAbreWord);
            else
                 docObjBean = documentoObjService.leerDocumento(pnuAnn,pnuEmi,ptiCap); 
            
            
            if (docObjBean!=null){
                    bisView=true;
                if (docObjBean.getTipoDoc()!=null){
                    if (docObjBean.getTipoDoc().equals("pdf")){
                        response.setContentType("application/pdf");
                    }else if(docObjBean.getTipoDoc().equals("docx")){
                        response.setContentType("application/vnd.openxmlformats-officedocument.wordprocessingml.document");
                    }else if(docObjBean.getTipoDoc().equals("doc")){
                        response.setContentType("application/msword");
                    }else{
                        response.setContentType("application/"+docObjBean.getTipoDoc());
                    }
                    } else {
                    response.setContentType("application/octet-stream");
                }   
                
                //response.setContentType("application/x-download");
                response.setHeader("Content-Disposition","inline; filename=\"" + docObjBean.getNombreArchivo() + "\""); 
                response.setHeader("Pragma", "no-cache");
                response.setHeader("Expires", "0");
                response.setHeader("Cache-Control", "must-revalidate, post-check=0, pre-check=0");                
                response.setContentLength(docObjBean.getDocumento().length);
                ServletOutputStream ouputStream = null;
                ouputStream = response.getOutputStream();
                ouputStream.write(docObjBean.getDocumento(), 0, docObjBean.getDocumento().length);
                ouputStream.flush();
                ouputStream.close();
                }
            }
            if(!bisView){    
                response.setContentType("application/std");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter out = response.getWriter();
                out.println("Documento no se encuentra");
                out.close();
                out.flush();                
            }
        } 
    }

    private void cargaDocumento(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnuAnn,pnuEmi,pnuSec;
        boolean vestado = false;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi");
        pnuSec = request.getParameter("nuSec");
        String fileName = "ERROR";
        
        if (pnuAnn!=null && pnuEmi!=null && pnuSec!=null && !pnuAnn.equals("") && !pnuEmi.equals("") && !pnuSec.equals("") ){
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.applicationProperties = (ApplicationProperties) applicationContext.getBean("applicationProperties");
            try{
                // capturando la cabecera con el nombre del documento
                String cabFile = request.getHeader("fileName");
                fileName = pnuAnn+pnuEmi+pnuSec+cabFile.substring(cabFile.lastIndexOf('.'));

                String rutaFile = applicationProperties.getRutaTemporal();
                File saveFile = new File(rutaFile +"//"+ fileName);

                // Visualizando la cabecera
                /*
                System.out.println("===== Inicio headers =====");
                Enumeration<String> names = request.getHeaderNames();
                while (names.hasMoreElements()) {
                    String headerName = names.nextElement();
                    System.out.println(headerName + " = " + request.getHeader(headerName));        
                }
                System.out.println("===== Fin headers =====\n");
                */
                inputStream = request.getInputStream();
                outputStream = new FileOutputStream(saveFile);

                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                //System.out.println("Recibiendo datos...");

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                //System.out.println("Datos Recibidos.");
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                
                vestado = true;
            } catch (Exception e1) {
                 e1.printStackTrace();
            } finally {
                try {
                    if(outputStream != null) {
                        outputStream.close();
                    }
                    if(inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                 e.printStackTrace();
                }
           } 
        } 
        
        if (vestado){
            response.setContentType("text/html; charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            PrintWriter out = response.getWriter();
            out.println(fileName);
            out.close();
            out.flush();                
        } else {
            response.setContentType("text/html; charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            PrintWriter out = response.getWriter();
            out.println("ERROR");
            out.close();
            out.flush();                
            
        }
    }
       
    
     private void loadActaNotificacion(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnuAnn,pnuEmi,pnuDes,pnuActa;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi");
        pnuDes = request.getParameter("nuDes");
        pnuActa = request.getParameter("nuActa");
        if (pnuAnn!=null && pnuEmi!=null && pnuDes!=null && !pnuAnn.equals("") && !pnuEmi.equals("") && !pnuDes.equals("") ){
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.documentoObjService = (DocumentoObjService) applicationContext.getBean("documentoObjService");
            DocumentoObjBean docObjBean = null;
            docObjBean = documentoObjService.leerActaNotificacion(pnuAnn,pnuEmi,pnuDes,pnuActa);
            if (docObjBean!=null){
                response.setContentType("application/octet-stream");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentLength(docObjBean.getDocumento().length);
                ServletOutputStream ouputStream = null;
                ouputStream = response.getOutputStream();
                ouputStream.write(docObjBean.getDocumento(), 0, docObjBean.getDocumento().length);
                ouputStream.close();
                ouputStream.flush();
            }else{
                response.setContentType("application/std");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter out = response.getWriter();
                out.println("Documento no se encuentra");
                out.close();
                out.flush();                
            }
        } 
    }
            
    private void loadAnexo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnuAnn,pnuEmi,pnuAne;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi");
        pnuAne = request.getParameter("nuAne");
        if (pnuAnn!=null && pnuEmi!=null && pnuAne!=null && !pnuAnn.equals("") && !pnuEmi.equals("") && !pnuAne.equals("") ){
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.documentoObjService = (DocumentoObjService) applicationContext.getBean("documentoObjService");
            DocumentoObjBean docObjBean = null;
            docObjBean = documentoObjService.leerDocumentoAnexo(pnuAnn,pnuEmi,pnuAne);
            if (docObjBean!=null){
                response.setContentType("application/octet-stream");
                response.setHeader("Cache-Control", "no-cache");
                response.setContentLength(docObjBean.getDocumento().length);
                ServletOutputStream ouputStream = null;
                ouputStream = response.getOutputStream();
                ouputStream.write(docObjBean.getDocumento(), 0, docObjBean.getDocumento().length);
                ouputStream.close();
                ouputStream.flush();
            }else{
                response.setContentType("application/std");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter out = response.getWriter();
                out.println("Documento no se encuentra");
                out.close();
                out.flush();                
            }
        } 
    }
    private void loadAnexos(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnuAnn,pnuEmi,pnuAne;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi"); 
        if (pnuAnn!=null && pnuEmi!=null &&  !pnuAnn.equals("") && !pnuEmi.equals("")  ){
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.documentoObjService = (DocumentoObjService) applicationContext.getBean("documentoObjService");
            List<DocumentoObjBean> docObjBean = null;
            docObjBean = documentoObjService.leerDocumentoAnexo(pnuAnn,pnuEmi);
            if (docObjBean!=null && docObjBean.size()>0){
                response.setContentType("application/octet-stream");
                response.setHeader("Cache-Control", "no-cache");
                byte[] zips =this.zip(docObjBean);
                response.setContentLength(zips.length);                 
                ServletOutputStream ouputStream = null;
                ouputStream = response.getOutputStream();
                ouputStream.write(zips, 0, zips.length);
                ouputStream.close();
                ouputStream.flush();
            }else{
                response.setContentType("application/std");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter out = response.getWriter();
                out.println("Documento no se encuentra");
                out.close();
                out.flush();                
            }
        } 
    }
    private byte[] zip(List<DocumentoObjBean> documentos) throws IOException {
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        ZipOutputStream zout = null;
        try {
            zout = new ZipOutputStream(bos);
            for (DocumentoObjBean doc : documentos) {
                try {
                ZipEntry ze = new ZipEntry(doc.getNombreArchivo());
                zout.putNextEntry(ze);
                zout.write(doc.getDocumento());
                zout.closeEntry();
                }catch(Exception e){ 
                }
            }
        }catch(Exception e){ 
        } finally {
            if (zout != null) {
                zout.close();
            }
        }
        return bos.toByteArray();
    }
    private void creaDocx(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnuAnn,pnuEmi;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi");

        if (pnuAnn!=null && pnuEmi!=null && !pnuAnn.equals("") && !pnuEmi.equals("")){
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.documentoXmlService = (DocumentoXmlService) applicationContext.getBean("documentoXmlService");
            DocumentoObjBean docObjBean = null;
            docObjBean = documentoXmlService.crearDocx(pnuAnn,pnuEmi);
            if (docObjBean!=null){
                response.setContentType("application/octet-stream");
                response.setHeader("Content-Disposition","attachment;filename="+docObjBean.getNombreArchivo());                
                response.setHeader("Cache-Control", "no-cache");
                response.setContentLength(docObjBean.getDocumento().length);
                ServletOutputStream ouputStream = null;
                ouputStream = response.getOutputStream();
                ouputStream.write(docObjBean.getDocumento(), 0, docObjBean.getDocumento().length);
                ouputStream.close();
                ouputStream.flush();
            }else{
                response.setContentType("application/std");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter out = response.getWriter();
                out.println("Plantilla no Existe para este Documento");
                out.close();
                out.flush();                
            }
        } 

    }

    private void creaPdfx(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnuAnn,pnuEmi,pnuAne;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi");
        pnuAne = request.getParameter("nuAne");

        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.documentoXmlService = (DocumentoXmlService) applicationContext.getBean("documentoXmlService");
            DocumentoObjBean docObjBean = null;
            docObjBean = documentoXmlService.crearPdfx(pnuAnn, pnuEmi, pnuAne);
            
            response.setContentType("text/html; charset=UTF-8");
                response.setHeader("Cache-Control", "no-cache");
                PrintWriter out = response.getWriter();
                out.println("Documento no se encuentra");
                out.close();
                out.flush();                
    }
    
    private void cargaDocumentoAnexo(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String pnuAnn,pnuEmi,pnuAne,pnuSec;
        boolean vestado = false;
        pnuAnn = request.getParameter("nuAnn"); 
        pnuEmi = request.getParameter("nuEmi");
        pnuAne = request.getParameter("nuAne");
        pnuSec = request.getParameter("nuSec");
        String fileName = "ERROR";
        
        if (pnuAnn!=null && pnuEmi!=null && pnuAne!=null && pnuSec!=null && !pnuAnn.equals("") && !pnuEmi.equals("") && !pnuSec.equals("") && !pnuAne.equals("") ){
            InputStream inputStream = null;
            FileOutputStream outputStream = null;
            WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
            this.applicationProperties = (ApplicationProperties) applicationContext.getBean("applicationProperties");
            try{
                // capturando la cabecera con el nombre del documento
                String cabFile = request.getHeader("fileName");
                fileName = pnuAnn+pnuEmi+pnuAne+pnuSec+cabFile.substring(cabFile.lastIndexOf('.'));

                String rutaFile = applicationProperties.getRutaTemporal();
                File saveFile = new File(rutaFile +"//"+ fileName);

                // Visualizando la cabecera
                /*
                System.out.println("===== Inicio headers =====");
                Enumeration<String> names = request.getHeaderNames();
                while (names.hasMoreElements()) {
                    String headerName = names.nextElement();
                    System.out.println(headerName + " = " + request.getHeader(headerName));        
                }
                System.out.println("===== Fin headers =====\n");
                */
                inputStream = request.getInputStream();
                outputStream = new FileOutputStream(saveFile);

                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                //System.out.println("Recibiendo datos...");

                while ((bytesRead = inputStream.read(buffer)) != -1) {
                    outputStream.write(buffer, 0, bytesRead);
                }

                //System.out.println("Datos Recibidos.");
                outputStream.flush();
                outputStream.close();
                inputStream.close();
                
                vestado = true;
            } catch (Exception e1) {
                 e1.printStackTrace();
            } finally {
                try {
                    if(outputStream != null) {
                        outputStream.close();
                    }
                    if(inputStream != null) {
                        inputStream.close();
                    }
                } catch (IOException e) {
                 e.printStackTrace();
                }
           } 
        } 
        
        if (vestado){
            response.setContentType("text/html; charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            PrintWriter out = response.getWriter();
            out.println(fileName);
            out.close();
            out.flush();                
        } else {
            response.setContentType("text/html; charset=UTF-8");
            response.setHeader("Cache-Control", "no-cache");
            PrintWriter out = response.getWriter();
            out.println("ERROR");
            out.close();
            out.flush();                
            
        }
    }    
}
