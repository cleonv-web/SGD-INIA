/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.service.impl;

import java.io.File;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import pe.gob.onpe.verificadoc.bean.AnexoBean;
import pe.gob.onpe.verificadoc.bean.TipoDocBean;
import pe.gob.onpe.verificadoc.bean.VerificaDocBean;
import pe.gob.onpe.verificadoc.dao.VerificaDocDao;
import pe.gob.onpe.verificadoc.service.VerificaDocService;
import pe.gob.onpe.verificadoc.web.util.Utility;

/**
 *
 * @author RATAYAURI
 */
@Service("verificaDocService")
public class VerificaDocServiceImpl implements VerificaDocService{
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
    
    @Autowired
    private VerificaDocDao verificaDocDao;
    
    @Override
    public List<TipoDocBean> getTiposDocs(){
        
        List<TipoDocBean> list = null;
        
        try {
            list = verificaDocDao.getTiposDocs();
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return list;
    }
    
    @Override
    public String getDescTipoDoc(String codTipoDoc){
        
        String doc = null;
        
        try {
            doc = verificaDocDao.getDescTipoDoc(codTipoDoc);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return doc;
    }
    
    @Override
    public VerificaDocBean getDocRemitosBusq(String nuEmi, String nuAnno){
        VerificaDocBean doc = null;
        
        try {
            doc = verificaDocDao.getDocRemitosBusq(nuEmi, nuAnno);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return doc;
    }
    
    public VerificaDocBean getDocRemitos(VerificaDocBean verificaDocBean) {

        VerificaDocBean doc = null;
        try {
            String [] docu=verificaDocBean.getNumDoc().split("-");
            verificaDocBean.setNuAnn(docu[1]);
            verificaDocBean.setNumDoc(docu[0]);
            /*if (docu.length==5)
                verificaDocBean.setSiglaDoc(docu[3]+'-'+docu[4]);
            else 
                verificaDocBean.setSiglaDoc(docu[3]);*/
            doc = verificaDocDao.getDocRemitos(verificaDocBean);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return doc;
    }
    
    public String getCodVerifica(String nuAnn, String nuEmi) {

        String cod = null;

        try {
            cod = verificaDocDao.getCodVerifica(nuAnn, nuEmi);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }
        return cod;
    }
    
    @Scheduled(cron = "${timer.max_consulta}")
    public void limpiaMapDocsAccedidos() {
        LOGGER.warn("Inicio de tarea para resetear la cantidad de documentos consultados por dia.");
        Utility.getInstancia().docsAccesoMap.clear();        
        LOGGER.warn("Fin de tarea para resetear la cantidad de documentos consultados por dia.");
        
        LOGGER.warn("Inicio de tarea para borrar los pdfs creados en el dia.");
        //Borramos los archivos creados en el día
        if(!StringUtils.isEmpty(Utility.getInstancia().pathPdfs)){
            File directorio = new File(Utility.getInstancia().pathPdfs);
            File[] files = directorio.listFiles();
            for(File pdf:files){
                int tamanio = pdf.getName().length();
                if("PDF".equalsIgnoreCase(pdf.getName().substring(tamanio-3, tamanio))){
                    try{
                        pdf.delete();
                    }catch(Exception e){
                        LOGGER.warn("No se pudo eliminar el archivo " + pdf.getName());
                    }
                    
                }
                
            }
        }
        
        LOGGER.warn("Fin de tarea para borrar los pdfs creados en el dia.");
        
    }
    
    @Override
    public List<AnexoBean> getAnexosList(String nuAnn, String nuEmi){
        List<AnexoBean> lista = null;
        try {
            lista = verificaDocDao.getAnexosList(nuAnn, nuEmi);
        } catch (Exception ex) {
            LOGGER.error(ex.getMessage(), ex);
        }

        return lista;
    }
}
