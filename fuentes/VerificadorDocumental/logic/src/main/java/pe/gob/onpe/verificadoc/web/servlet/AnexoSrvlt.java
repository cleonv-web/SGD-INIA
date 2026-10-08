/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.web.servlet;

import java.io.IOException;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.context.ContextLoader;
import org.springframework.web.context.WebApplicationContext;
import pe.gob.onpe.verificadoc.bean.DocumentoObjBean;
import pe.gob.onpe.verificadoc.service.DocumentoObjService;

/**
 *
 * @author ratayauri
 */
public class AnexoSrvlt extends HttpServlet{
    
    @Autowired
    private DocumentoObjService documentoObjService;
    
    @Override
    public void init(ServletConfig servletConfig) throws ServletException {        
        super.init(servletConfig);
        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        this.documentoObjService = (DocumentoObjService) applicationContext.getBean("documentoObjService");
    }
       
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {       
        descargarAnexo(request, response);
    }
    
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {       
        descargarAnexo(request, response);
    }
    
    private void descargarAnexo(HttpServletRequest request, HttpServletResponse response) throws IOException{
        
        String nuAnn = request.getParameter("anexoNuAnn");
        String nuEmi = request.getParameter("anexoNuEmi");
        String nuAne = request.getParameter("anexoNuAne");
        
        //************************************************************************
        
        DocumentoObjBean anexoFile = documentoObjService.leerAnexo(nuAnn, nuEmi, nuAne);
        
        response.setHeader("Content-Type", "application/octet-stream");
        response.setHeader("Content-Length", String.valueOf(anexoFile.getDocumento().length));
        response.setHeader("Content-Disposition", "attachment; filename=\"" + anexoFile.getNombreArchivo() + "\"");
        
        response.getOutputStream().write(anexoFile.getDocumento());
        response.getOutputStream().flush();
        
        
    }
    
}
