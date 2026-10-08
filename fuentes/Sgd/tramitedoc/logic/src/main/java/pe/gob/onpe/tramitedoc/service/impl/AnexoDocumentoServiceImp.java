package pe.gob.onpe.tramitedoc.service.impl;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.StringWriter;
import java.io.Writer;
import java.util.ArrayList;
import java.util.List;
import org.codehaus.jackson.map.ObjectMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.libreria.json.JSONObject;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBeansContenedor;
import pe.gob.onpe.tramitedoc.bean.DocumentoFileBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.dao.AnexoDocumentoDao;
import pe.gob.onpe.tramitedoc.dao.CommonQueryDao;
import pe.gob.onpe.tramitedoc.service.AnexoDocumentoService;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

@Service("anexoDocumentoService")
@Transactional 
public class AnexoDocumentoServiceImp implements AnexoDocumentoService {

    @Autowired
    private AnexoDocumentoDao anexoDocumentoDao;
    
    @Autowired
    private CommonQueryDao commonQueryDao;    
    
    @Autowired
    private ApplicationProperties applicationProperties;       

    @Override
    public List<ReferenciaBean> getDocumentosReferencia(String pnuAnn, String pnuEmi) {
        List<ReferenciaBean> list = null;
        try {
            list = anexoDocumentoDao.getDocumentosReferencia(pnuAnn, pnuEmi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String getReferenciaRoot(String pnuAnn, String pnuEmi, String pnuDes, int nivel) {
        String retval = null;
        String nodoHijo = null;
        String titleJSON = null;
        

        List<ReferenciaBean> list = null;

        if (nivel > 0) {
            try {
                list = anexoDocumentoDao.getDocumentoEmi(pnuAnn, pnuEmi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (list != null && list.size() > 0) {
            ReferenciaBean refBean = new ReferenciaBean();

            retval = "[";
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    retval = retval + ",{";
                } else {
                    retval = retval + "{";
                }
                refBean = list.get(i);
                refBean.setNuDesRef(pnuDes);
                try {
                    String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " - " + refBean.getDeDepEmi();
                    Writer strWriter = new StringWriter();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(strWriter, vtitle );
                    titleJSON = strWriter.toString();   
                } catch (Exception e) {
                    titleJSON = "";
                }

                retval = retval + "\"title\":" + titleJSON + ",";
                retval = retval + "\"key\":\"" + refBean.getNuAnnRef() + refBean.getNuEmiRef() + refBean.getNuDesRef() + "\",";
                retval = retval + "\"isFolder\": true ,";
                retval = retval + "\"expand\": true";
                
                nodoHijo = getReferenciaJson(refBean.getNuAnnRef(), refBean.getNuEmiRef(), nivel);

                if (nodoHijo != null) {
                    retval = retval + ", \"children\":" + nodoHijo;
                }
                retval = retval + "}";
            }

            retval = retval + "]";
        } else {
            retval = null;
        }
        
        
        return retval;
    }
    
    
    public List<ReferenciaBean> getReferenciaRootList(String pnuAnn, String pnuEmi, String pnuDes, int nivel) {
        String retval = null;
         List<ReferenciaBean>  nodoHijo = null;
         List<ReferenciaBean>  Result = new ArrayList<ReferenciaBean>();
        String titleJSON = null;
        

        List<ReferenciaBean> list = null;

        if (nivel > 0) {
            try {
                list = anexoDocumentoDao.getDocumentoEmi(pnuAnn, pnuEmi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (list != null && list.size() > 0) {
            ReferenciaBean refBean = new ReferenciaBean();

            retval = "[";
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    retval = retval + ",{";
                } else {
                    retval = retval + "{";
                }
                refBean = list.get(i);
                refBean.setNuDesRef(pnuDes);
                try {
                    String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " - " + refBean.getDeDepEmi();
                    Writer strWriter = new StringWriter();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(strWriter, vtitle );
                    titleJSON = strWriter.toString();   
                } catch (Exception e) {
                    titleJSON = "";
                }

                retval = retval + "\"title\":" + titleJSON + ",";
                retval = retval + "\"key\":\"" + refBean.getNuAnnRef() + refBean.getNuEmiRef() + refBean.getNuDesRef() + "\",";
                retval = retval + "\"isFolder\": true ,";
                retval = retval + "\"expand\": true";
                Result.add(refBean);
                nodoHijo = getReferenciaJsonList(refBean.getNuAnnRef(), refBean.getNuEmiRef(), nivel);

                if (nodoHijo != null) {
                    /*retval = retval + ", \"children\":" + nodoHijo;*/
                    Result.addAll(nodoHijo);
                    
                }
                
                retval = retval + "}";
            }

            retval = retval + "]";
        } else {
            retval = null;
        }
        
        
        return Result;
    }
    
    //YUAL
    @Override
     public DocumentoAnexoBean getReferenciaInicial(String pnuAnn, String pnuEmi, String pnuDes) {
        String retval = null;
        String nodoHijo = null;
        String titleJSON = null;
        DocumentoAnexoBean oResult= new DocumentoAnexoBean();
        oResult.setNuEmi(pnuEmi);
        oResult.setNuAnn(pnuAnn);


        List<ReferenciaBean> list = null;

        
            try {
                list = anexoDocumentoDao.getDocumentoEmi(pnuAnn, pnuEmi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        

        if (list != null && list.size() > 0) {
            ReferenciaBean refBean = new ReferenciaBean();

            retval = "[";
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    retval = retval + ",{";
                } else {
                    retval = retval + "{";
                }
                refBean = list.get(i);
                refBean.setNuDesRef(pnuDes);
                try {
                    String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " - " + refBean.getDeDepEmi();
                    Writer strWriter = new StringWriter();
                    ObjectMapper mapper = new ObjectMapper();
                    mapper.writeValue(strWriter, vtitle );
                    titleJSON = strWriter.toString();   
                } catch (Exception e) {
                    titleJSON = "";
                }

                retval = retval + "\"title\":" + titleJSON + ",";
                retval = retval + "\"key\":\"" + refBean.getNuAnnRef() + refBean.getNuEmiRef() + refBean.getNuDesRef() + "\",";
                retval = retval + "\"isFolder\": true ,";
                retval = retval + "\"expand\": true";
                
                
                oResult.setNuEmi(refBean.getNuEmiRef());
                oResult.setNuAnn(refBean.getNuAnn());

                oResult = getReferenciaJsonInicial(refBean.getNuAnnRef(), refBean.getNuEmiRef());
                if (nodoHijo != null) {
                    retval = retval + ", \"children\":" + nodoHijo;
                      
                }
                
                retval = retval + "}";
            }

            retval = retval + "]";
        } else {
            retval = null;
               
                oResult.setNuEmi(pnuEmi);
                oResult.setNuAnn(pnuAnn);
        }
        
        
        return oResult;
    }

    
      public String getReferenciaJson(String pnuAnn, String pnuEmi, int nivel) {
        String retval = null;
        String nodoHijo = null;
        String titleJSON = null;

        List<ReferenciaBean> list = null;

        if (nivel >= 0) {
            try {
                list = anexoDocumentoDao.getDocumentosReferencia(pnuAnn, pnuEmi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (list != null && list.size() > 0) {
            if (nivel > 0) {
                nivel = nivel - 1;
                ReferenciaBean refBean = new ReferenciaBean();

                retval = "[";
                for (int i = 0; i < list.size(); i++) {

                    if (i > 0) {
                        retval = retval + ",{";
                    } else {
                        retval = retval + "{";
                    }

                    refBean = list.get(i);
                    
                    try {
                        String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " - " + refBean.getDeDepEmi();
                        Writer strWriter = new StringWriter();
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(strWriter, vtitle );
                        titleJSON = strWriter.toString();   
                    } catch (Exception e) {
                        titleJSON = "";
                    }
                    
                    
                    retval = retval + "\"title\":" + titleJSON + ",";
                    retval = retval + "\"key\":\"" + refBean.getNuAnnRef() + refBean.getNuEmiRef() + refBean.getNuDesRef() + "\"";

                    nodoHijo = getReferenciaJson(refBean.getNuAnnRef(), refBean.getNuEmiRef(), nivel);

                    if (nodoHijo != null) {
                        if (nivel > 0) {
                            retval = retval + ",\"expand\": true";
                            retval = retval + ",\"isFolder\": true";
                            retval = retval + ",\"children\":" + nodoHijo;
                        } else {
                            retval = retval + ",\"isFolder\": true";
                            retval = retval + ",\"isLazy\": true";
                        }
                    }
                    retval = retval + "}";
                }

                retval = retval + "]";
            } else {
                retval = "CON";
            }
        } else {
            retval = null;
        }
        return retval;
    }
      
      
      //YUAL
      public List<ReferenciaBean> getReferenciaJsonList(String pnuAnn, String pnuEmi, int nivel) {
        String retval = null;
         List<ReferenciaBean>  nodoHijo = null;
        String titleJSON = null;
        
        List<ReferenciaBean> list = null;
        List<ReferenciaBean> Result = new ArrayList<ReferenciaBean>();

        if (nivel >= 0) {
            try {
                list = anexoDocumentoDao.getDocumentosReferencia(pnuAnn, pnuEmi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (list != null && list.size() > 0) {
            if (nivel > 0) {
                nivel = nivel - 1;
                ReferenciaBean refBean = new ReferenciaBean();

                retval = "[";
                for (int i = 0; i < list.size(); i++) {

                    if (i > 0) {
                        retval = retval + ",{";
                    } else {
                        retval = retval + "{";
                    }

                    refBean = list.get(i);
                    
                    try {
                        String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " - " + refBean.getDeDepEmi();
                        Writer strWriter = new StringWriter();
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(strWriter, vtitle );
                        titleJSON = strWriter.toString();   
                    } catch (Exception e) {
                        titleJSON = "";
                    }
                    
                    
                    retval = retval + "\"title\":" + titleJSON + ",";
                    retval = retval + "\"key\":\"" + refBean.getNuAnnRef() + refBean.getNuEmiRef() + refBean.getNuDesRef() + "\"";
                    Result.add(refBean);
                    nodoHijo = getReferenciaJsonList(refBean.getNuAnnRef(), refBean.getNuEmiRef(), nivel);

                    if (nodoHijo != null) {
                        if (nivel > 0) {
                         /*   retval = retval + ",\"expand\": true";
                            retval = retval + ",\"isFolder\": true";
                            retval = retval + ",\"children\":" + nodoHijo;*/
                             Result.addAll(nodoHijo);
                        } else {
                            /*retval = retval + ",\"isFolder\": true";
                            retval = retval + ",\"isLazy\": true";*/
                        }
                    }
                    retval = retval + "}";
                }

                retval = retval + "]";
            } else {
                retval = "CON";
            }
        } else {
            retval = null;
        }
        
         list = null;
          nodoHijo = null;
          
        return Result;
    }
      
//YUAL
    public DocumentoAnexoBean getReferenciaJsonInicial(String pnuAnn, String pnuEmi) {
        String retval = null;
        String nodoHijo = null;
        String titleJSON = null;
        DocumentoAnexoBean result= new DocumentoAnexoBean();
        List<ReferenciaBean> list = null;
        result.setNuEmi(pnuEmi);
        result.setNuAnn(pnuAnn);

            try {
                list = anexoDocumentoDao.getDocumentosReferencia(pnuAnn, pnuEmi);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
     

        if (list != null && list.size() > 0) {

                ReferenciaBean refBean = new ReferenciaBean();

                retval = "[";
                for (int i = 0; i < list.size(); i++) {

                    if (i > 0) {
                        retval = retval + ",{";
                    } else {
                        retval = retval + "{";
                    }

                    refBean = list.get(i);
                    
                    try {
                        String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " - " + refBean.getDeDepEmi();
                        Writer strWriter = new StringWriter();
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(strWriter, vtitle );
                        titleJSON = strWriter.toString();   
                    } catch (Exception e) {
                        titleJSON = "";
                    }
                    
                    
                    retval = retval + "\"title\":" + titleJSON + ",";
                    retval = retval + "\"key\":\"" + refBean.getNuAnnRef() + refBean.getNuEmiRef() + refBean.getNuDesRef() + "\"";
                    result.setNuEmi(refBean.getNuEmiRef());
                    result.setNuAnn(refBean.getNuAnn());

                    result = getReferenciaJsonInicial(refBean.getNuAnnRef(), refBean.getNuEmiRef());

                }

                retval = retval + "]";

        } else {
            retval = null;
            result.setNuEmi(pnuEmi);
            result.setNuAnn(pnuAnn);

        }
        //return retval;
        return result;
    }

    public List<ReferenciaBean> getDocumentosSeguimiento(String pnuAnn, String pnuEmi, String pnuDes) {
        List<ReferenciaBean> list = null;
        try {
            list = anexoDocumentoDao.getDocumentosSeguimiento(pnuAnn, pnuEmi, pnuDes);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String getSeguimientoRoot(String pnuAnn, String pnuEmi, String pnuDes, int nivel) {
        String retval = null;
        String nodoHijo = null;
        String titleJSON = null;

        List<ReferenciaBean> list = null;

        if (nivel > 0) {
            try {
                if(!pnuDes.equals("N")){
                    pnuDes=getNuDesDocSeguimiento(pnuAnn, pnuEmi, pnuDes);
                }
                
                list = anexoDocumentoDao.getDocumentoEmiSeg(pnuAnn, pnuEmi, pnuDes);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (list != null && list.size() > 0) {
            ReferenciaBean refBean = new ReferenciaBean();

            retval = "[";
            for (int i = 0; i < list.size(); i++) {
                if (i > 0) {
                    retval = retval + ",{";
                } else {
                    retval = retval + "{";
                }
                refBean = list.get(i);
                
                    try {
                        String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " -> " + refBean.getDeDepDes();
                        Writer strWriter = new StringWriter();
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(strWriter, vtitle );
                        titleJSON = strWriter.toString();   
                    } catch (Exception e) {
                        titleJSON = "";
                    }
                
                
                retval = retval + "\"title\":" + titleJSON + ",";
                retval = retval + "\"key\":\"" + refBean.getNuAnn() + refBean.getNuEmi() + refBean.getNuDes() + "\",";
                retval = retval + "\"isFolder\": true ,";
                retval = retval + "\"expand\": true";
                nodoHijo = getSeguimientoJson(refBean.getNuAnn(), refBean.getNuEmi(), refBean.getNuDes(), nivel);

                if (nodoHijo != null) {
                    retval = retval + ", \"children\":" + nodoHijo;
                }
                retval = retval + "}";
            }

            retval = retval + "]";
        } else {
            retval = null;
        }
        return retval;
    }

    public String getSeguimientoJson(String pnuAnn, String pnuEmi, String pnuDes, int nivel) {
        String retval = null;
        String nodoHijo = null;
        String titleJSON = null;

        List<ReferenciaBean> list = null;

        if (nivel >= 0) {
            try {
                list = anexoDocumentoDao.getDocumentosSeguimiento(pnuAnn, pnuEmi, pnuDes);
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }

        if (list != null && list.size() > 0) {
            if (nivel > 0) {
                nivel = nivel - 1;
                ReferenciaBean refBean = new ReferenciaBean();

                retval = "[";
                for (int i = 0; i < list.size(); i++) {

                    if (i > 0) {
                        retval = retval + ",{";
                    } else {
                        retval = retval + "{";
                    }

                    refBean = list.get(i);
                    try {
                        String vtitle = refBean.getLiTipDoc() + " " + refBean.getLiNuDoc() + " -> " + refBean.getDeDepDes();
                        Writer strWriter = new StringWriter();
                        ObjectMapper mapper = new ObjectMapper();
                        mapper.writeValue(strWriter, vtitle );
                        titleJSON = strWriter.toString();   
                    } catch (Exception e) {
                        titleJSON = "";
                    }

                    retval = retval + "\"title\":" + titleJSON + ",";
                    retval = retval + "\"key\":\"" + refBean.getNuAnn() + refBean.getNuEmi() + refBean.getNuDes() + "\"";

                    nodoHijo = getSeguimientoJson(refBean.getNuAnn(), refBean.getNuEmi(), refBean.getNuDes(), nivel);

                    if (nodoHijo != null) {
                        if (nivel > 0) {
                            retval = retval + ",\"expand\": true";
                            retval = retval + ",\"isFolder\": true";
                            retval = retval + ",\"children\":" + nodoHijo;
                        } else {
                            retval = retval + ",\"isFolder\": true";
                            retval = retval + ",\"isLazy\": true";
                        }
                    }
                    retval = retval + "}";
                }

                retval = retval + "]";
            } else {
                retval = "CON";
            }
        } else {
            retval = null;
        }
        return retval;
    }

    public String updArchivoAnexo(String coUsu,String pnuAnn, String pnuEmi, String pnuAne, DocumentoFileBean pfileAnexo) {
        String vReturn = "NO_OK";

        DocumentoAnexoBean docAnexo = new DocumentoAnexoBean();

        docAnexo.setNuAnn(pnuAnn);
        docAnexo.setNuEmi(pnuEmi);
        docAnexo.setNuAne(pnuAne);
        docAnexo.setDeDet(pfileAnexo.getNombreArchivo());
        docAnexo.setDeRutOri(pfileAnexo.getNombreArchivo());
        docAnexo.setCoUseCre(coUsu);
        docAnexo.setCoUseMod(coUsu);
        validarBeanDocAnexo(docAnexo);

        final InputStream archivoAnexo = new ByteArrayInputStream(pfileAnexo.getArchivoBytes());
        final int size = pfileAnexo.getArchivoBytes().length;
        // int maxUploadSize=10000000;    
        int maxUploadSize= Integer.parseInt(applicationProperties.getFileSizeMaxCargo())*1024*1024;
        try {
            if(size<=maxUploadSize){
                String cantidadAnexo=anexoDocumentoDao.getCanAnexosDuplicadosNombres(pnuAnn, pnuEmi,pnuAne, pfileAnexo.getNombreArchivo());
                if (cantidadAnexo.equals("0")){
                    vReturn = anexoDocumentoDao.updArchivoAnexo(docAnexo, archivoAnexo, size);
                }
                else {
                    vReturn = "El archivo "+pfileAnexo.getNombreArchivo()+" ya está registrado.";
                }
            }
            else {
                vReturn = "El tamaño de archivo supera el limite permitido.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(archivoAnexo!=null){
                    archivoAnexo.close();                    
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }        

        return vReturn;

    }

@Transactional (propagation = Propagation.REQUIRED,rollbackFor=Exception.class) 
public String updAnexoDetalle(DocumentoAnexoBeansContenedor docsAnexos,String coUsu,String rowCount) throws Exception {
        String vReturn = "NO_OK";
        ArrayList<DocumentoAnexoBean> docs = docsAnexos.getDocs();
        try{
            String nuAnn=null;
            String nuEmi=null;
            boolean isPkRemitos=false;
            for (DocumentoAnexoBean doc : docs) {
                if(!isPkRemitos){
                    nuAnn=doc.getNuAnn();
                    nuEmi=doc.getNuEmi();
                    isPkRemitos=true;
                }
                if ("update".equals(doc.getTiOpe())) {
                    doc.setCoUseMod(coUsu);
                    validarBeanDocAnexo(doc);
                    if(doc.getReqFirma()!=null&&doc.getReqFirma().equals("1")){
                        vReturn=getVerificaPdfDoc(doc.getNuAnn(),doc.getNuEmi(),doc.getNuAne());                        
                        if (!vReturn.equals("OK")){
                            throw new validarDatoException("El Documento tiene que estar en PDF");
                        }
                    }
                    vReturn = anexoDocumentoDao.updAnexoDetalle(doc);
                    if ("NO_OK".equals(vReturn)) {
                        throw new validarDatoException("Error al Actualizar");
                    }
                }
                if ("delete".equals(doc.getTiOpe())) {
                    vReturn = anexoDocumentoDao.delArchivoAnexo(doc);
                    if ("NO_OK".equals(vReturn)) {
                        throw new validarDatoException("Error al Borrar");
                    }
                }
            }
            
            if("0".equals(rowCount)){
                vReturn=anexoDocumentoDao.updExisteAnexo(nuAnn, nuEmi);
            }
            
            if(isPkRemitos){
                String reqFirma="1";
                vReturn=anexoDocumentoDao.getCanAnexosReqFirma(nuAnn, nuEmi);
                if(vReturn.equals("0")){
                    reqFirma="0";
                }                
                vReturn=anexoDocumentoDao.updRemitosResumenInFirmaAnexos(reqFirma, nuAnn, nuEmi);
                if(!vReturn.equals("OK")){
                    throw new validarDatoException("Error actualizando remitos resumen firma anexos.");
                }                
            }
        } 
        catch (validarDatoException e) { 
           throw e; 
        } 
        catch (Exception e) { 
           e.printStackTrace(); 
           throw new validarDatoException("ERROR GRABAR ANEXOS"); 
        } 
        return vReturn;
    }

    public String insArchivoAnexo(String coUsu,String pnuAnn, String pnuEmi, DocumentoFileBean pfileAnexo) {
        String vReturn = "NO_OK";
        String vnuAne = anexoDocumentoDao.getUltimoAnexo(pnuAnn, pnuEmi);

        DocumentoAnexoBean docAnexo = new DocumentoAnexoBean();

        docAnexo.setNuAnn(pnuAnn);
        docAnexo.setNuEmi(pnuEmi);
        docAnexo.setNuAne(vnuAne);
        docAnexo.setDeDet(pfileAnexo.getNombreArchivo());
        docAnexo.setDeRutOri(pfileAnexo.getNombreArchivo());
        docAnexo.setCoUseCre(coUsu);
        docAnexo.setCoUseMod(coUsu);
        validarBeanDocAnexo(docAnexo);

        final InputStream archivoAnexo = new ByteArrayInputStream(pfileAnexo.getArchivoBytes());
        final int size = pfileAnexo.getArchivoBytes().length;
        //int maxUploadSize=10000000;
         int maxUploadSize= Integer.parseInt(applicationProperties.getFileSizeMaxCargo())*1024*1024;
        try {
            if(size<=maxUploadSize){
                String cantidadAnexo=anexoDocumentoDao.getCanAnexosDuplicadosNombres(pnuAnn, pnuEmi, pfileAnexo.getNombreArchivo());
                if (cantidadAnexo.equals("0")){
                    vReturn = anexoDocumentoDao.insArchivoAnexo(docAnexo, archivoAnexo, size);
                }
                else {
                    vReturn = "El archivo "+pfileAnexo.getNombreArchivo()+" ya está registrado.";
                }
            } 
            else {
                vReturn = "El tamaño de archivo supera el limite permitido.";
            }
        } catch (Exception e) {
            e.printStackTrace();
        }finally{
            try {
                if(archivoAnexo!=null){
                    archivoAnexo.close();                                        
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }                

        return vReturn;

    }

    @Override
    public String getNombreArchivo(String pNuAnn, String pNuEmi, String pNuAne) {
        String retval = null;
        try {
            retval = anexoDocumentoDao.getNombreArchivo(pNuAnn, pNuEmi,pNuAne);
        } catch (Exception ex) {
            ex.printStackTrace();
            retval="";
        }
        return retval;
    }
    
    private void validarBeanDocAnexo(DocumentoAnexoBean docAnexo){
        String pdeDet=docAnexo.getDeDet();
        if(pdeDet!=null){
            int tamCampoDeDet=200;
            pdeDet=pdeDet.trim();
            int pLenDeDet=pdeDet.length();
            if(pLenDeDet>tamCampoDeDet){
                pdeDet=pdeDet.substring(pLenDeDet-tamCampoDeDet, pLenDeDet);
            }
        }
        docAnexo.setDeDet(pdeDet);
    }
    
    @Override
    public String getNuDesDocSeguimiento(String nuAnn,String nuEmi,String nuDes){
        String vReturn = nuDes;
        String[] vReturnAux;
        try {
            vReturnAux=commonQueryDao.getCodigoMotivoDocRec(nuAnn, nuEmi, nuDes);
            if(vReturnAux!=null&&vReturnAux[0].equals("OK")){
               String coMot=vReturnAux[1];
               if(coMot!=null&&coMot.equals(applicationProperties.getCoMotSegui())){
                   vReturn="N";
               }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String getVerificaPdfDoc(String pnuAnn, String pnuEmi, String pnuAne) {
        String vReturn = "NO_OK";
        try {
            DocumentoObjBean docObjBean = anexoDocumentoDao.getPropiedadesArchivo(pnuAnn, pnuEmi, pnuAne);
            if (docObjBean != null) {
                if (docObjBean.getTipoDoc() != null && docObjBean.getTipoDoc().equals("PDF")) {
                    vReturn = "OK";
                } else {
                    vReturn = "Para la Firmar del documento tiene que estar en PDF ";
                }
            } else {
                vReturn = "Error en verificar formato del documento";
            }

        } catch (Exception e) {
            vReturn = "Error en verificar formato del documento";
        }

        return vReturn;
    }
    
    @Override
    public String updArchivoAnexoFirmado(DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";

        try {
            vReturn = anexoDocumentoDao.updArchivoAnexoFirmado(docObjBean);
        } catch (Exception e) {
            e.printStackTrace();
        }  
        return vReturn;
    }    
    
    @Override
    public String getCanAnexosReqFirma(String nuAnn, String nuEmi) {
        String vReturn = "NO_OK";

        try {
            vReturn = anexoDocumentoDao.getCanAnexosReqFirma(nuAnn, nuEmi);
        } catch (Exception e) {
            e.printStackTrace();
        }  
        return vReturn;
    }
    
    @Override
    public String updRemitosResumenInFirmaAnexos(String reqFirma, String nuAnn, String nuEmi){
        String vReturn = "NO_OK";

        try {
            vReturn = anexoDocumentoDao.updRemitosResumenInFirmaAnexos(reqFirma, nuAnn, nuEmi);
        } catch (Exception e) {
            e.printStackTrace();
        }  
        return vReturn;
    }
}
