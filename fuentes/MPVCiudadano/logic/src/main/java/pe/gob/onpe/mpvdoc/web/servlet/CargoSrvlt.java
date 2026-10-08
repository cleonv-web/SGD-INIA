/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.web.servlet;

import java.io.ByteArrayInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import net.sf.jasperreports.engine.JRException;
import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import pe.gob.onpe.mpvdoc.bean.DocumentoBean;
import pe.gob.onpe.mpvdoc.service.MpvdocService;
import pe.gob.onpe.mpvdoc.web.util.ApplicationProperties;
import pe.gob.onpe.mpvdoc.web.util.Utilidades;

/**
 *
 * @author Usuario
 */
public class CargoSrvlt extends HttpServlet{
     @Autowired
    private MpvdocService mpvdocService;
    @Autowired
    private ApplicationProperties applicationProperties;
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {        
        super.init(servletConfig);
        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        this.mpvdocService = (MpvdocService) applicationContext.getBean("mpvdocService");
        this.applicationProperties = (ApplicationProperties) applicationContext.getBean("applicationProperties");
    }
       
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)   {       
        descargarCargo(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)   {       
        descargarCargo(request, response);
    }
    
    private void descargarCargo(HttpServletRequest request, HttpServletResponse response) {
        ServletContext sc = request.getSession().getServletContext();
        String nuAnn = request.getParameter("cargNuAnn");
        String nuEmi = request.getParameter("cargNuEmi");
        String nuAne = request.getParameter("cargExp");
        String rutaReporte = sc.getRealPath("/reports/");
        
        
        String logo = applicationProperties.getLogoReporteB64();                
        ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
        System.out.println("logo=>>>"+logo);
        Map parametros = new HashMap(); 
        parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);  
        parametros.put("P_DE_DEPENDENCIA", "");
        parametros.put("P_USER", "");
        String rutaJasper = rutaReporte + "/TDR15.jasper";
        int tipoArchivo = 2; // PDF
        List<DocumentoBean> lista=null;
        try {
            System.out.println("rutareporte=>>>"+rutaJasper);
            
            DocumentoBean documento = new DocumentoBean();
            documento.setNuAnn(nuAnn);
            documento.setNuEmi(nuEmi);
            lista = this.mpvdocService.getListaReporteBusquedaVoucher(documento);
            if(lista==null){
             System.out.println("lista=>>>null");
            }
            else {
             System.out.println("lista=>>>"+lista.size());
            }
            //eserror = "0";
        } catch(Exception ex) {  
            System.out.println("error-1");
            ex.printStackTrace();
            //eserror = "1";
            //deRespuesta = ex.getMessage();
        }
        try{    
            Utilidades util = new Utilidades();
            byte[] archivo = util.GenerarReporteLista(rutaJasper, lista, "TDR15", parametros, tipoArchivo);                
            response.setHeader("Content-Type", "application/octet-stream");
            response.setHeader("Content-Length", String.valueOf(archivo.length));
            response.setHeader("Content-Disposition", "attachment; filename=\"mpv_ciudadano_cargo.pdf\"");
            response.getOutputStream().write(archivo);
            response.getOutputStream().flush();
        
         } catch (JRException ex) {
            ex.printStackTrace();
            System.out.println("error-2");
        } catch (FileNotFoundException ex) {
             ex.printStackTrace();
            System.out.println("error-3");
        } catch (IOException ex) {
             ex.printStackTrace();
            System.out.println("error-4");
        }
        
    }
}
