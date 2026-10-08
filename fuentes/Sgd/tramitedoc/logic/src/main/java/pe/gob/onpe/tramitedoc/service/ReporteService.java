/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;


import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 *
 * @author ecueva
 */
public interface ReporteService {
    void ejecutaReporte(HttpServletRequest request, HttpServletResponse response , String pdataSource);    
    void ejecutaReporteLista(HttpServletRequest request, HttpServletResponse response );    
    void abrirReporteTemporal(HttpServletRequest request, HttpServletResponse response );
}
