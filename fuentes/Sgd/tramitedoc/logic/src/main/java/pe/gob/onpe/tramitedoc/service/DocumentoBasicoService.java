/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.DestinoResBen;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.RemitosResBean;

/**
 *
 * @author wcutipa
 */
public interface DocumentoBasicoService {
    RemitosResBean getRemitoResumen(String pnuAnn, String pnuEmi);
    DestinoResBen  getDestinoResumen(String pnuAnn, String pnuEmi, String pnuDes);  
    DestinoResBen  getDestinoDocumento(String pnuAnn, String pnuEmi, String pnuDes);  
    List<DestinoResBen>  getDestinoResumenList(String pnuAnn, String pnuEmi);  
    List<DocumentoAnexoBean>  getAnexosList(String pnuAnn, String pnuEmi);  
    List<DocumentoAnexoBean>  getAnexosMsjList(String pnuAnn, String pnuEmi);  
    String verificaCargaDoc(String pnuAnn, String pnuEmi);      
    
}
