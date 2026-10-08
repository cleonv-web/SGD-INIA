package pe.gob.onpe.verificadoc.service.impl;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pe.gob.onpe.verificadoc.bean.DocumentoObjBean;
import pe.gob.onpe.verificadoc.dao.DocumentoObjDao;
import pe.gob.onpe.verificadoc.service.DocumentoObjService;
import pe.gob.onpe.verificadoc.web.util.ApplicationProperties;

@Service("documentoObjService")
public class DocumentoObjServiceImp implements DocumentoObjService{

    private static Logger logger=Logger.getLogger("DocObjService");    
    
    @Autowired
    private DocumentoObjDao documentoObjDao;

    @Autowired
    private ApplicationProperties applicationProperties;
    
        
    @Override
    public DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi){
        DocumentoObjBean docObjBean = null;
        docObjBean = documentoObjDao.leerDocumento(pnuAnn, pnuEmi);
        
        if (docObjBean!=null && docObjBean.getNombreArchivo()!=null && docObjBean.getNombreArchivo().length()>0 ){
            int pos = docObjBean.getNombreArchivo().lastIndexOf(".");
            if (pos>0){
                docObjBean.setTipoDoc(docObjBean.getNombreArchivo().substring(pos+1).toLowerCase()); 
            }else{
                docObjBean.setTipoDoc("octet-stream"); 
            }
        }
        
        return(docObjBean);
    
    }

    @Override
    public DocumentoObjBean leerAnexo(String nuAnn, String nuEmi, String nuAne){
        DocumentoObjBean anexoObjBean = null;
        anexoObjBean = documentoObjDao.leerAnexo(nuAnn, nuEmi, nuAne);
        
        if (anexoObjBean!=null && !StringUtils.isEmpty(anexoObjBean.getNombreArchivo())){
            int pos = anexoObjBean.getNombreArchivo().lastIndexOf(".");
            if (pos>0){
                anexoObjBean.setTipoDoc(anexoObjBean.getNombreArchivo().substring(pos+1).toLowerCase()); 
            }else{
                anexoObjBean.setTipoDoc("octet-stream"); 
            }
        }
        
        return(anexoObjBean);
    
    }
    
}
