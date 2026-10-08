/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.web.servlet;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import pe.gob.onpe.tramitedoc.service.ReporteService;

import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.context.ContextLoader;

/**
 *
 * @author ecueva
 */
public class ReporteSrvlt extends HttpServlet{
    private ReporteService reporteService;

    @Override
    public void init() throws ServletException {
        super.init();    //To change body of overridden methods use File | Settings | File Templates.
        // este es para acceder el servicio del spring

        WebApplicationContext applicationContext = ContextLoader.getCurrentWebApplicationContext();
        this.reporteService = (ReporteService) applicationContext.getBean("reporteService");
    }

    @Override
    public void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadReporte(request, response);
    }

    @Override
    public void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        loadReporte(request, response);
    }

    private void loadReporte(HttpServletRequest request, HttpServletResponse response) throws IOException {
        //reporteService.ejecutaReporte( request,  response,"dataSource");
        //reporteService.ejecutaReporteLista( request,  response);
        reporteService.abrirReporteTemporal(request,response);
        
    }    
}
