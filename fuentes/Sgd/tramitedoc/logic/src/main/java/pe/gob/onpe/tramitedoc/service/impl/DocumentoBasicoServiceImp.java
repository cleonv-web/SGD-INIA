/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DestinoResBen;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.RemitosResBean;
import pe.gob.onpe.tramitedoc.dao.DocumentoBasicoDao;
import pe.gob.onpe.tramitedoc.service.DocumentoBasicoService;

/**
 *
 * @author wcutipa
 */

@Service("documentoBasicoService")
public class DocumentoBasicoServiceImp implements DocumentoBasicoService{

    @Autowired
    private DocumentoBasicoDao documentoBasicoDao;

    
    @Override
    public RemitosResBean getRemitoResumen(String pnuAnn, String pnuEmi){
        RemitosResBean docRemitoBean = null;
        docRemitoBean = documentoBasicoDao.getRemitoResumen(pnuAnn, pnuEmi);
        return (docRemitoBean);
    }
    
    @Override
    public DestinoResBen  getDestinoDocumento(String pnuAnn, String pnuEmi, String pnuDes){
        DestinoResBen docDestinoBean = null;
        docDestinoBean = documentoBasicoDao.getDestinoResumen(pnuAnn, pnuEmi, pnuDes);
        return (docDestinoBean);
    }

    @Override
    public DestinoResBen  getDestinoResumen(String pnuAnn, String pnuEmi, String pnuDes){
        DestinoResBen docDestinoBean = null;

       if (pnuDes!= null && !pnuDes.equals("N")){
            docDestinoBean = documentoBasicoDao.getDestinoResumen(pnuAnn, pnuEmi, pnuDes);
       }else{
            List<DestinoResBen> list = null;
            list = documentoBasicoDao.getDestinoResumenList(pnuAnn, pnuEmi);
            docDestinoBean = new DestinoResBen();
            for(int i=0; i<list.size(); i++) {
                docDestinoBean = list.get(i);
                break;
            }            
       }
        return (docDestinoBean);
    }
    
    
    @Override
    public List<DestinoResBen>  getDestinoResumenList(String pnuAnn, String pnuEmi){
        List<DestinoResBen> list = null;
        list = documentoBasicoDao.getDestinoResumenList(pnuAnn, pnuEmi);
        return (list);
        
    }
    
    @Override
    public List<DocumentoAnexoBean>  getAnexosList(String pnuAnn, String pnuEmi){
        List<DocumentoAnexoBean> list = null;
        list = documentoBasicoDao.getAnexosList(pnuAnn, pnuEmi);
        return (list);
    }

    @Override
    public List<DocumentoAnexoBean> getAnexosMsjList(String pnuAnn, String pnuEmi) {
        List<DocumentoAnexoBean> list = null;
        list = documentoBasicoDao.getAnexosMsjList(pnuAnn, pnuEmi);
        return (list);
    }
    
    
    
      @Override
    public String verificaCargaDoc(String pnuAnn, String pnuEmi) {
        
        return documentoBasicoDao.verificaCargaDoc(pnuAnn, pnuEmi);
        
    }
}
