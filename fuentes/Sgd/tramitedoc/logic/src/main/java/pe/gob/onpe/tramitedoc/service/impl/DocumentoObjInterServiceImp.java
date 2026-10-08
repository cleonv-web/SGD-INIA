package pe.gob.onpe.tramitedoc.service.impl;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.codec.binary.Base64;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBlock;
import pe.gob.onpe.tramitedoc.bean.DocumentoVerBean;
import pe.gob.onpe.tramitedoc.bean.ReporteBean;
import pe.gob.onpe.tramitedoc.bean.UsuarioConfigBean;
import pe.gob.onpe.tramitedoc.dao.AuditoriaMovimientoDocDao;
import pe.gob.onpe.tramitedoc.dao.DocumentoObjDao;
import pe.gob.onpe.tramitedoc.service.AnexoDocumentoService;
import pe.gob.onpe.tramitedoc.service.DocumentoObjInterService;
//import pe.gob.onpe.tramitedoc.service.DocumentoObjService;
import pe.gob.onpe.tramitedoc.service.DocumentoVoBoService;
import pe.gob.onpe.tramitedoc.service.DocumentoXmlService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoAdmService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoInteroperabilidadService;
import pe.gob.onpe.tramitedoc.service.EmiDocumentoPersonalService;
import pe.gob.onpe.tramitedoc.util.ArchivoTemporal;
import pe.gob.onpe.tramitedoc.util.LsAleatorioViewDoc;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

@Service("documentoObjInterService")
public class DocumentoObjInterServiceImp implements DocumentoObjInterService{

    private static Logger logger=Logger.getLogger("SGDDocObjService");    
    
    @Autowired
    private DocumentoObjDao documentoObjDao;

    @Autowired
    private EmiDocumentoInteroperabilidadService emiDocumentoAdmService;    

    @Autowired
    private EmiDocumentoPersonalService emiDocumentoPersonalService;

    @Autowired
    private AuditoriaMovimientoDocDao auditoriaMovimientoDocDao;
    
    @Autowired
    private AnexoDocumentoService anexoDocumentoService;
    
    @Autowired
    private DocumentoVoBoService documentoVoBoService;
    
    @Autowired
    private ApplicationProperties applicationProperties;
    
    @Autowired
    private DocumentoXmlService DocumentoXmlService;
        
    @Override
    public DocumentoDatoBean getDatosDoc(String pnuAnn, String pnuEmi){
        DocumentoDatoBean docDatoBean=null;
        try{
           docDatoBean = documentoObjDao.getDatosDoc(pnuAnn, pnuEmi);
           if (docDatoBean!=null){
               docDatoBean.setNuSecFirma(Utilidades.generateRandomNumber(8));
           }
        }catch(Exception e){
            docDatoBean=null;
        }
        return  docDatoBean;

    }

    @Override
    public DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap){
        DocumentoObjBean docObjBean = null;
        docObjBean = documentoObjDao.leerDocumento(pnuAnn, pnuEmi, ptiCap);
        
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
    public DocumentoObjBean leerDocumentoAnexo(String pnuAnn, String pnuEmi, String pnuAne){
        DocumentoObjBean docObjBean = null;
        docObjBean = documentoObjDao.leerDocumentoAnexo(pnuAnn, pnuEmi, pnuAne);
        return(docObjBean);
    
    }
    
    @Override
    public DocumentoObjBean getNombreArchivo(String pnuAnn, String pnuEmi, String ptiCap){
        DocumentoObjBean docObjBean = null;
        docObjBean = documentoObjDao.getNombreArchivo(pnuAnn, pnuEmi, ptiCap);
        // Verificamos la Extencion del archivo
        if (docObjBean!=null && docObjBean.getNombreArchivo().length()>0 ){
            int pos = docObjBean.getNombreArchivo().lastIndexOf(".");
            if (pos>0){
                docObjBean.setTipoDoc(docObjBean.getNombreArchivo().substring(pos+1).toLowerCase()); 
            }else{
                docObjBean.setTipoDoc("archivo"); 
            }
        }
        return(docObjBean);
    }

    
   @Override
   public DocumentoVerBean getNombreDoc(String pnuAnn, String pnuEmi,String ptiOpe,Usuario usuario){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        DocumentoObjBean docObjBean = null;
        
        docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        if (docDatoBean!=null){
            docObjBean = this.getNombreArchivo(pnuAnn, pnuEmi, docDatoBean.getTiCap());
            if (docObjBean!=null && docObjBean.getNuTamano()>0){
                docVerBean.setInDoc(true);
                docVerBean.setDeMensaje("OK");
                // Activamos el Log de Visualiza Documento
                auditoriaMovimientoDocDao.audiVisualizaDocumento(docObjBean, usuario);
            }else{
                docVerBean.setInDoc(false);
                docVerBean.setDeMensaje("Documento no se encuentra cargado en Repositorio");
            }
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=abrirDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&tiCap="+docDatoBean.getTiCap()/*;//*/+"&nroView="+LsAleatorioViewDoc.getInstancia().add();
            String nombreDoc;

            /*
              Parametro
              1 - Abrir Documento .DOC (\temp\)
              2 - Abrir Documento .PDF (\temp\)
              3 - Generar Documento (\año\*.docx)
              4 - Generar Documento (\año\*.pdf)
              5 - Generar Documento (\año\*.*)
            */
            
            
            //verificamos si el documento es generado en la institucion
            if(docDatoBean.getTiEmi().equals("06")){
                //Nombre de los archivos
                if(ptiOpe.equals("0")){
                    if(docDatoBean.getNumeroDoc()!=null&&docDatoBean.getNumeroDoc().trim().length()>0){
                        nombreDoc = "TEMP|"+docDatoBean.getTipoDoc()+"-"+docDatoBean.getNumeroDoc()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                    }else{
                        nombreDoc = "TEMP|"+docDatoBean.getTipoDoc()+"-"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                    }
                }else if(ptiOpe.equals("1") || ptiOpe.equals("2") ){
                    nombreDoc = "TEMP|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                }else if(ptiOpe.equals("3")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".docx";
                }else if(ptiOpe.equals("4")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".pdf";
                }else if(ptiOpe.equals("5")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                }else{
                    nombreDoc = "TEMP|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                }
                
            }else{ 

                   nombreDoc = "TEMP|I"+docDatoBean.getNuCorEmi()+"."+docObjBean.getTipoDoc();

            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
        }
        return(docVerBean);
   }
    
   @Override
   public DocumentoVerBean getNombreGeneraDocx(String pnuAnn, String pnuEmi,String ptiOpe){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        
        docDatoBean = getDatosDoc(pnuAnn, pnuEmi);

        if (docDatoBean!=null){
            // Para el Caso de Proveido y Hoja de Envio
            if (docDatoBean.getCoDoc().equals("232") || docDatoBean.getCoDoc().equals("304")){
                docVerBean.setInDoc(false);
                docVerBean.setDeMensaje("Tipo de Documento no requiere Generar Formato");
            }else{
                docVerBean.setInDoc(true);
             }
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=creaDocx&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&tiCap="+docDatoBean.getTiCap();
            String nombreDoc="";

            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
            if(docDatoBean.getTiEmi().equals("06")){
               nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".docx";
            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
            docVerBean.setInDoc(true);
            docVerBean.setDeMensaje("OK");
        }
        return(docVerBean);
   }
    

    @Override
    public DocumentoObjBean getNombreArchivoAnexo(String pnuAnn, String pnuEmi, String pnuAnexo){
        DocumentoObjBean docObjBean = null;
        docObjBean = documentoObjDao.getNombreArchivoAnexo(pnuAnn, pnuEmi, pnuAnexo);
        // Verificamos la Extencion del archivo
        if (docObjBean!=null && docObjBean.getNombreArchivo().length()>0 ){
            int pos = docObjBean.getNombreArchivo().lastIndexOf("\\");
            if (pos>0){
                docObjBean.setNombreArchivo(docObjBean.getNombreArchivo().substring(pos+1)); 
            }
            
            pos = docObjBean.getNombreArchivo().lastIndexOf(".");
            if (pos>0){
                docObjBean.setTipoDoc(docObjBean.getNombreArchivo().substring(pos+1).toLowerCase()); 
            }else{
                docObjBean.setTipoDoc("archivo"); 
            }
        }
        return(docObjBean);
    }
    
   @Override
   public DocumentoVerBean getNombreDocAnexo(String pnuAnn, String pnuEmi,String pnuAnexo){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setNuAne(pnuAnexo); 
        docVerBean.setInDoc(false);
        
        DocumentoObjBean docObjBean = null;
        
        docObjBean = this.getNombreArchivoAnexo(pnuAnn, pnuEmi, pnuAnexo);
        if (docObjBean!=null && docObjBean.getNuTamano()>0){
            docVerBean.setInDoc(true);
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            docVerBean.setDeMensaje("OK");
            String urlDoc = "documento?accion=abrirAnexo&nuAnn="+docObjBean.getNuAnn()+"&nuEmi="+docObjBean.getNuEmi()+"&nuAne="+docObjBean.getNuAne();

            //Ruta del Documento
            String nombreDoc = "TEMP|"+docObjBean.getNombreArchivo();
            
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
        }
        return(docVerBean);
   }
    
   @Override
   public DocumentoVerBean getNombreCargaDoc(String pnuAnn, String pnuEmi,String ptiOpe){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        
        docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        if (docDatoBean!=null){
            docVerBean.setInDoc(true);
            docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
            docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
            docVerBean.setTiCap(docDatoBean.getTiCap());
            if(docDatoBean.getEsDocEmi().equals("7") && ptiOpe.equals("3")){
                ptiOpe = "4";
            }
        }else{
            docVerBean.setDeMensaje("Error en datos del documento");
        }
        
        /*
          Parametro
          1 - Abrir Documento .DOC (\temp\)
          2 - Abrir Documento .PDF (\temp\)
          3 - Generar Documento (\año\*.doc)
          4 - Generar Documento (\año\*.pdf)
        */
        
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=cargaDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&nuSec="+docDatoBean.getNuSecFirma();
            String nombreDoc=null;
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01

                if(docDatoBean.getTiEmi().equals("06") ){
               //Segun la Operacion Cargamos los mvaldera 
                    String vnumDoc="";

                 if(ptiOpe.equals("3")){
                        vnumDoc = "I"+docDatoBean.getNuCorEmi()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".docx";
                        nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-"+vnumDoc;
                    }else if(ptiOpe.equals("4")){
                        vnumDoc = "I"+docDatoBean.getNuCorEmi()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".pdf";
                        nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-"+vnumDoc;
                    }else{
                        nombreDoc = null;
                    }
                     docVerBean.setUrlDocumento(urlDoc);
                     docVerBean.setNoDocumento(nombreDoc);
                     docVerBean.setDeMensaje("OK");
                 }else{
                     docVerBean.setDeMensaje("Operación no permitida");
                 }
            
            

        }
        return(docVerBean);
   }

   @Override
   public DocumentoVerBean getNombreFirmaDoc(String pnuAnn, String pnuEmi,String ptiOpe,UsuarioConfigBean usuarioConfig, String rutaReporteBase) {
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        String vin_pref="1";
        String vin_TSA="0";
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        
        if(usuarioConfig.getSwFirma()!=null) {
            docVerBean.setNoPrefijo("[NF]");
            if(usuarioConfig.getSwFirma().equals("R")){
                docVerBean.setNoPrefijo("[R]");
            }
            docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        }
        
        if (docDatoBean!=null ){
            if( (docDatoBean.getTiEmi().equals("06") && docDatoBean.getEsDocEmi().equals("7") && ptiOpe.equals("5")) ){
                //Verificamos si el documento esta en PDF
                String coTipDoc = docDatoBean.getCoDoc();
                String vformatoDoc = "NO_OK";
                if (coTipDoc.equals("232") || coTipDoc.equals("304")){
                    vformatoDoc="OK";
                    vin_pref = "N";
                }else{
                    vformatoDoc = emiDocumentoAdmService.getVerificaPdfDoc(docDatoBean.getNuAnn(), docDatoBean.getNuEmi(),docDatoBean.getTiCap());
                    //verificar si el tipo de documento se numero y firma o solo se numera.
                    String vin_firma=emiDocumentoAdmService.getInFirmaDocAdm(coTipDoc); 
                    if(vin_firma.equals("1")){//solo firma
                        vin_pref = "N";
                    }else if(vin_firma.equals("2")){//firma y numera
                        vin_pref = "1";
                    }
                }                
                
                // Para el Caso de Proveido y Hoja de Envio
//                if (docDatoBean.getCoDoc().equals("232") || docDatoBean.getCoDoc().equals("304")){
//                    vformatoDoc="OK";
//                    vin_pref = "N";
//                }else{
//                    vformatoDoc = emiDocumentoAdmService.getVerificaPdfDoc(docDatoBean.getNuAnn(), docDatoBean.getNuEmi(),docDatoBean.getTiCap());
//                    vin_pref = "1";
//                }
                
                if (vformatoDoc.equals("OK")){
                    docVerBean.setDeMensaje("OK");
                    docVerBean.setInDoc(true);
                    docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
                    docVerBean.setFeFirma(docDatoBean.getFechaActual());
                    docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
                    if (usuarioConfig.getInFirma()!= null && usuarioConfig.getInFirma().equals("1")){
                        docVerBean.setInTipoFirma("N");
                    }else{
                        if (usuarioConfig.getInFirma().equals("0")){
                            String vinDoc = documentoObjDao.getInFirmaDoc(docDatoBean.getCoDepEmi(),docDatoBean.getCoDoc());
                            docVerBean.setInTipoFirma(vinDoc);
                        }else{
                            docVerBean.setInTipoFirma("F");
                        }
                    }
                    
                    // verificamos si tiene numero de documento 
                        if(docDatoBean.getNumeroDoc()==null || docDatoBean.getNumeroDoc().length()==0 ){
                            // Obtenemos el Numero de Documento
                            if (docDatoBean.getTiEmi().equals("06")){
                                String vnuDoc=emiDocumentoAdmService.getNumeroDocSiguienteAdm(docDatoBean.getNuAnn(),docDatoBean.getCoDepEmi(), docDatoBean.getCoDoc());
                                docVerBean.setNumeroDoc(vnuDoc);
                                docDatoBean.setNumeroDoc(vnuDoc);
                            }
//                            else if(docDatoBean.getTiEmi().equals("05")){
//                                String vnuDoc=emiDocumentoPersonalService.getNumeroDocSiguientePersonal(docDatoBean.getNuAnn(),docDatoBean.getCoEmpEmi(),docDatoBean.getCoDoc());
//                                docVerBean.setNumeroDoc(vnuDoc);
//                                docDatoBean.setNumeroDoc(vnuDoc);
//                                
//                            }
                        }else{
                            docVerBean.setNumeroDoc(docDatoBean.getNumeroDoc());
                        }
                }else{
                    docVerBean.setDeMensaje(vformatoDoc);
                    docVerBean.setInDoc(false);
                }
            }
        }else{
             docVerBean.setDeMensaje("Error en datos del documento");
        }

        /*
          5 - Firmar Documento (PDF en \temp)
          6 - Docuemento Firmarmado por AUTOR (en Directorio Acrobat)
          7 - Docuemento Firmarmado por ENCARGO (en Directorio Acrobat)
          8 - Docuemento Numerado (en Directorio Acrobat)
        */
        
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=abrirDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&tiCap="+docDatoBean.getTiCap()/*;//*/+"&nroView="+LsAleatorioViewDoc.getInstancia().add();
            // Para el Caso de Proveidos se tiene que generar
            if (docVerBean.getCoDocEmi().equals("232") || docVerBean.getCoDocEmi().equals("304")){
               
                // urlDoc = "reporte?coReporte=TDR11&coParametros=P_NU_ANN="+docDatoBean.getNuAnn()+"|P_NU_EMI="+docDatoBean.getNuEmi()+"|P_NU_DOC="+docDatoBean.getNumeroDoc()+"|P_FECHA="+docVerBean.getFeFirma()+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg&coSubReportes=SUB_REPORT_DIR1=TDR11_SUB1|SUB_REPORT_DIR2=TDR11_SUB2";
            
                String vgnum_reg = "R"+docDatoBean.getNuCorEmi()+"."+docDatoBean.getNuSecFirma();
                String cadena = generarFormatoSiglasFirma(docDatoBean.getTipoDoc(), docDatoBean.getNumeroDoc(),docDatoBean.getNuAnn(), applicationProperties.getSiglaInstitucion() , docDatoBean.getSiglasDoc());
                String vgnumDoc = cadena.replace(" ", "")+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");
                String gnombreDoc = "TEMP|"+vgnum_reg+"$"+vgnumDoc+"$"+vin_pref+".pdf";
                
                
                List<DocumentoDatoBean> listaSubReport1 = documentoObjDao.CargarSubReporte1(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                        
                List<DocumentoDatoBean> listaSubReport2 = documentoObjDao.CargarSubReporte2(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());

                
//                String rutaImagen = rutaReporteBase + "/logo_onpe.jpg";
                
                String logo = applicationProperties.getLogoReporteB64();                
                ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
                
                Map parametros = new HashMap();
                parametros.put("P_NU_ANN", docDatoBean.getNuAnn());
                parametros.put("P_NU_EMI", docDatoBean.getNuEmi());
                parametros.put("P_NU_DOC", docDatoBean.getNumeroDoc());  
                parametros.put("P_FECHA", docVerBean.getFeFirma());  
                parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);
                parametros.put("datosSubRep1", listaSubReport1);
                parametros.put("datosSubRep2", listaSubReport2);
                parametros.put("SUB_REPORT_DIR1", rutaReporteBase);
                parametros.put("SUB_REPORT_DIR2", rutaReporteBase);
                
                try {
                    ReporteBean objReporte = getGenerarReporte("PDF", rutaReporteBase, parametros, gnombreDoc, docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                    if(bis!=null)bis.close();
                    urlDoc = objReporte.noUrl; 
                } catch (Exception e) {
                    docVerBean.setInDoc(false);
                    docVerBean.setDeMensaje(e.getMessage());
                }
            
            }
            String nombreDoc=null;
            String nombreFirma=null;
            
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
            if(docDatoBean.getTiEmi().equals("06")){
               //Segun la Operacion Cargamos los documentos 
               String vnumDoc="";
               String vnum_reg = "";
                
               if(ptiOpe.equals("5")){
                   //verificar si el documento se firma TSA
                   /*if(docDatoBean.getTiEmi().equals("01")){
                    String vFirmaTSA = verificarFirmaTSAService.VerificarDestinoCNM(docDatoBean.getNuAnn(), docDatoBean.getNuEmi(), docDatoBean.getCoDoc());
                    if(vFirmaTSA.equals("OK")){
                        vin_TSA="1";
                    }
                   }*/
                   // Verificamos el Software de Firma
                    if(usuarioConfig.getSwFirma()!=null && usuarioConfig.getSwFirma().equals("O") ) {
                        //Para el Firma ONPE
                        // Se envia "#" y sera remplazado por "/"
                        vnum_reg = "R"+docDatoBean.getNuCorEmi()+"."+docDatoBean.getNuSecFirma();
                        //String vsig_inst= docDatoBean.getSiglasDoc().contains("/")?"-DP-":"-DP!";
                        /*String vsig_inst= docDatoBean.getSiglasDoc().contains("/")?"-CNM-":"-CNM!";
                        vnumDoc =  docDatoBean.getTipoDoc()+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+vsig_inst+docDatoBean.getSiglasDoc().replaceAll("/", "!")+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");*/
                        String cadena = generarFormatoSiglasFirma(docDatoBean.getTipoDoc(), docDatoBean.getNumeroDoc(),docDatoBean.getNuAnn(), applicationProperties.getSiglaInstitucion() , docDatoBean.getSiglasDoc());
//                        vnumDoc =  docDatoBean.getTipoDoc()+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()+"!"+applicationProperties.getSiglaInstitucion()+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");                        
                        vnumDoc =  cadena.replace(" ", "")+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");
//                        vnumDoc =  "Prueba"+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()+"!"+applicationProperties.getSiglaInstitucion()+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");
                        
                        //vnumDoc =  "Prueba"+"$"+"Prueba de Plantillas"+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");  
                        nombreDoc = "TEMP|"+vnum_reg+"$"+vnumDoc+"$"+vin_pref+".pdf";
                        nombreFirma = "TEMP|"+vnum_reg+"$"+vnumDoc+"$"+vin_pref;
                    }else{
                        //Para ReFirma
//                        vnum_reg = "R"+docDatoBean.getNuCorEmi()+"."+docDatoBean.getNuSecFirma()+"#";
//                        vnumDoc =  docDatoBean.getDeLugar()+"-"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc().replaceAll("/", "-").replaceAll("-", "_")+"-"+docDatoBean.getFechaFirma();
//                        nombreDoc = "TEMP|"+vnum_reg+docDatoBean.getTipoDoc()+"-"+vnumDoc+"-1.pdf";
//                        nombreFirma = "TEMP|"+vnum_reg+docDatoBean.getTipoDoc()+"-"+vnumDoc+"-N";
                    }
               
               }else if(ptiOpe.equals("6")){
                   nombreDoc = null;
               }else{
                   nombreDoc = null;
               }
            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
            docVerBean.setNoFirma(nombreFirma);
        }
        return(docVerBean);
   }

   @Override
   public DocumentoVerBean getNombreCargaFirmaDoc(String pnuAnn, String pnuEmi,String ptiOpe){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        
        docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        if (docDatoBean!=null){
            if((docDatoBean.getTiEmi().equals("06") && docDatoBean.getEsDocEmi().equals("7") && ptiOpe.equals("6"))){
                    docVerBean.setDeMensaje("OK");
                    docVerBean.setInDoc(true);
                    docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
                    docVerBean.setFeFirma(docDatoBean.getFechaActual());
                    docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
            }else{
                docVerBean.setDeMensaje("Estado del Documento a cambiado");
            }
        }else{
             docVerBean.setDeMensaje("Error en datos del documento");
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=cargaDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&nuSec="+docDatoBean.getNuSecFirma();
            
            String nombreDoc=null;
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
            if(docDatoBean.getTiEmi().equals("06")){
               //Segun la Operacion Cargamos los documentos 
               if(ptiOpe.equals("6")){
                   nombreDoc = "OK";
               }else{
                   nombreDoc = null;
               }
            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
        }
        return(docVerBean);
   }

   @Override
    public String getFormatoDoc(String pnuAnn, String pnuEmi, String ptiCap){
        String vReturn = "ERR";
        try{
           vReturn =   emiDocumentoAdmService.getFormatoDoc(pnuAnn, pnuEmi, ptiCap);
        }catch(Exception e){
            vReturn = "ERR";
        }
        
        return vReturn;
    }
    

   @Override
   public DocumentoVerBean getNombreDocEmi(String pnuAnn, String pnuEmi,String ptiOpe, Usuario usuario, String rutaReporteBase) {
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        DocumentoObjBean docObjBean = null;
        
        docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        if (docDatoBean!=null){
            docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
            docVerBean.setNumeroDoc(docDatoBean.getNumeroDoc());
            docVerBean.setFeFirma(docDatoBean.getFechaActual());
            docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
            if (docVerBean.getCoDocEmi().equals("232") || docVerBean.getCoDocEmi().equals("304")){
                docVerBean.setInDoc(true);
                docVerBean.setDeMensaje("OK");
                docObjBean = new DocumentoObjBean();
                docObjBean.setTipoDoc("pdf");
                if(docDatoBean.getNumeroDoc()==null){
                    docDatoBean.setNumeroDoc("      ");
                    docVerBean.setNumeroDoc("      ");
                }
            }else{
                docObjBean = this.getNombreArchivo(pnuAnn, pnuEmi, docDatoBean.getTiCap());
                if (docObjBean!=null && docObjBean.getNuTamano()>0){
                    docVerBean.setInDoc(true);
                    docVerBean.setDeMensaje("OK");
                    // Activamos el Log de Visualiza Documento
                    auditoriaMovimientoDocDao.audiVisualizaDocumento(docObjBean, usuario);
                    
                }else{
                    docVerBean.setInDoc(false);
                    docVerBean.setDeMensaje("Documento no se encuentra cargado en Repositorio");
                }
            }
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=abrirDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&tiCap="+docDatoBean.getTiCap()/*;//*/+"&nroView="+LsAleatorioViewDoc.getInstancia().add();
            if (docVerBean.getCoDocEmi().equals("232") || docVerBean.getCoDocEmi().equals("304")){                
                
                // urlDoc = "reporte?coReporte=TDR11&coParametros=P_NU_ANN="+docDatoBean.getNuAnn()+"|P_NU_EMI="+docDatoBean.getNuEmi()+"|P_NU_DOC="+docDatoBean.getNumeroDoc()+"|P_FECHA="+docDatoBean.getFechaActual()+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg&coSubReportes=SUB_REPORT_DIR1=TDR11_SUB1|SUB_REPORT_DIR2=TDR11_SUB2";                              
            
                String gnombreDoc = "TEMP|R"+docDatoBean.getNuCorEmi()+"."+docObjBean.getTipoDoc();
                
                
                List<DocumentoDatoBean> listaSubReport1 = documentoObjDao.CargarSubReporte1(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                        
                List<DocumentoDatoBean> listaSubReport2 = documentoObjDao.CargarSubReporte2(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
               
                
                //tring rutaImagen = rutaReporteBase + "/logo_onpe.jpg";                
                String logo = applicationProperties.getLogoReporteB64();                
                ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
                
                Map parametros = new HashMap();
                parametros.put("P_NU_ANN", docDatoBean.getNuAnn());
                parametros.put("P_NU_EMI", docDatoBean.getNuEmi());
                parametros.put("P_NU_DOC", docDatoBean.getNumeroDoc());  
                parametros.put("P_FECHA", docDatoBean.getFechaActual());  
                parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);
                parametros.put("datosSubRep1", listaSubReport1);
                parametros.put("datosSubRep2", listaSubReport2);
                parametros.put("SUB_REPORT_DIR1", rutaReporteBase);
                parametros.put("SUB_REPORT_DIR2", rutaReporteBase);
                
                try {
                    ReporteBean objReporte = getGenerarReporte("PDF", rutaReporteBase, parametros, gnombreDoc, docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                    if(bis!=null)bis.close();
                    urlDoc = objReporte.noUrl; 
                } catch (Exception e) {
                    docVerBean.setInDoc(false);
                    docVerBean.setDeMensaje(e.getMessage());
                }
                
            }
            /*
              Parametro
              3 - Generar Documento (\año\*.docx)
              4 - Generar Documento (\año\*.pdf)
              5 - Generar Documento (\año\*.*)
            */
            
            String nombreDoc;
            //verificamos si el documento es generado en la institucion
            if(docDatoBean.getTiEmi().equals("06")){
                //Nombre de los archivos
                if(ptiOpe.equals("3")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".docx";
                }else if(ptiOpe.equals("4")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".pdf";
                }else if(ptiOpe.equals("5")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                }else{
                    nombreDoc = "TEMP|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                }
                
            }else{ /*archivo viene de mesa de partes*/
               nombreDoc = "TEMP|I"+docDatoBean.getNuCorEmi()+"."+docObjBean.getTipoDoc();
            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
        }
        return(docVerBean);
   }

   @Override
   public DocumentoVerBean getNombreVoBoDoc(String pnuAnn, String pnuEmi,String ptiOpe,UsuarioConfigBean usuarioConfig, String rutaReporteBase){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        boolean bswFirmaOnpe=true;
        
        DocumentoDatoBean docDatoBean=null;
        
        if(usuarioConfig.getSwFirma()!=null) {
            docVerBean.setNoPrefijo("[VF]");
            if(usuarioConfig.getSwFirma().equals("R")){
                docVerBean.setNoPrefijo("[R]");
                bswFirmaOnpe=false;
            }else{
                docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
            }
        }
        if(bswFirmaOnpe){
            if (docDatoBean!=null ){
                if( (docDatoBean.getTiEmi().equals("06") && docDatoBean.getEsDocEmi().equals("7") && ptiOpe.equals("5")) /*|| ( docDatoBean.getTiEmi().equals("05") && docDatoBean.getEsDocEmi().equals("5") && ptiOpe.equals("5"))*/ ){
                    //Verificamos si el documento esta en PDF
                    String vformatoDoc = "NO_OK";
                    // Para el Caso de Proveido y Hoja de Envio
                    if (docDatoBean.getCoDoc().equals("232") || docDatoBean.getCoDoc().equals("304")){
                        vformatoDoc="OK";
                    }else{
                        vformatoDoc = emiDocumentoAdmService.getVerificaPdfDoc(docDatoBean.getNuAnn(), docDatoBean.getNuEmi(),docDatoBean.getTiCap());
                    }

                    if (vformatoDoc.equals("OK")){
                        docVerBean.setDeMensaje("OK");
                        docVerBean.setInDoc(true);
                        docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
                        docVerBean.setFeFirma(docDatoBean.getFechaActual());
                        docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
                        docVerBean.setInTipoFirma("F");
                        
                        // verificamos si tiene numero de documento 
                        if(docDatoBean.getNumeroDoc()==null){
                            docDatoBean.setNumeroDoc("");
                        }
                        docVerBean.setNumeroDoc(docDatoBean.getNumeroDoc());
                    }else{
                        docVerBean.setDeMensaje(vformatoDoc);
                        docVerBean.setInDoc(false);
                    }
                }else{
                    docVerBean.setDeMensaje("documento en elaboración");   
                }
            }else{
                 docVerBean.setDeMensaje("Error en datos del documento");
            }            
        }else{
            docVerBean.setDeMensaje("Utilizar software Firma Onpe.");            
        }


        /*
          5 - Firmar Documento (PDF en \temp)
          6 - Docuemento Firmarmado por AUTOR (en Directorio Acrobat)
          7 - Docuemento Firmarmado por ENCARGO (en Directorio Acrobat)
          8 - Docuemento Numerado (en Directorio Acrobat)
        */
        
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=abrirDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&tiCap="+docDatoBean.getTiCap()/*;//*/+"&nroView="+LsAleatorioViewDoc.getInstancia().add();
            // Para el Caso de Proveidos se tiene que generar
            if (docVerBean.getCoDocEmi().equals("232") || docVerBean.getCoDocEmi().equals("304")){
               
                // urlDoc = "reporte?coReporte=TDR11&coParametros=P_NU_ANN="+docDatoBean.getNuAnn()+"|P_NU_EMI="+docDatoBean.getNuEmi()+"|P_NU_DOC="+docDatoBean.getNumeroDoc()+"|P_FECHA="+docVerBean.getFeFirma()+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg&coSubReportes=SUB_REPORT_DIR1=TDR11_SUB1|SUB_REPORT_DIR2=TDR11_SUB2";
            
                String vgnum_reg = "R"+docDatoBean.getNuCorEmi()+"."+docDatoBean.getNuSecFirma();
                String vgnumDoc =  docDatoBean.getTipoDoc()+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/;
                String gnombreDoc = "TEMP|"+vgnum_reg+"$"+vgnumDoc+".pdf";
                
                
                List<DocumentoDatoBean> listaSubReport1 = documentoObjDao.CargarSubReporte1(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                        
                List<DocumentoDatoBean> listaSubReport2 = documentoObjDao.CargarSubReporte2(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                                       
//                String rutaImagen = rutaReporteBase + "/logo_onpe.jpg";
                
                String logo = applicationProperties.getLogoReporteB64();                
                ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
                
                Map parametros = new HashMap();
                parametros.put("P_NU_ANN", docDatoBean.getNuAnn());
                parametros.put("P_NU_EMI", docDatoBean.getNuEmi());
                parametros.put("P_NU_DOC", docDatoBean.getNumeroDoc());  
                parametros.put("P_FECHA", docVerBean.getFeFirma());  
                parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);
                parametros.put("datosSubRep1", listaSubReport1);
                parametros.put("datosSubRep2", listaSubReport2);
                parametros.put("SUB_REPORT_DIR1", rutaReporteBase);
                parametros.put("SUB_REPORT_DIR2", rutaReporteBase);
                
                try {
                    ReporteBean objReporte = getGenerarReporte("PDF", rutaReporteBase, parametros, gnombreDoc, docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                    if(bis!=null)bis.close();
                    urlDoc = objReporte.noUrl; 
                } catch (Exception e) {
                    docVerBean.setInDoc(false);
                    docVerBean.setDeMensaje(e.getMessage());
                }            
            
            }
            
            String nombreDoc = null;
            String nombreFirma = null;
            
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
            if(docDatoBean.getTiEmi().equals("06") /*|| docDatoBean.getTiEmi().equals("05")*/){
               //Segun la Operacion Cargamos los documentos 
               String vnumDoc="";
               String vnum_reg = "";
                
               if(ptiOpe.equals("5")){
                    
                   // Verificamos el Software de Firma
                    if(usuarioConfig.getSwFirma()!=null && usuarioConfig.getSwFirma().equals("O") ) {
                        //Para el Firma ONPE
                        // Se envia "#" y sera remplazado por "/"
                        vnum_reg = "R"+docDatoBean.getNuCorEmi()+"."+docDatoBean.getNuSecFirma();
                        vnumDoc =  docDatoBean.getTipoDoc()+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/;
                        nombreDoc = "TEMP|"+vnum_reg+"$"+vnumDoc+".pdf";
                        nombreFirma = "TEMP|"+vnum_reg+"$"+vnumDoc;
                        //bloquear registro vobo
                        documentoVoBoService.bloquearVoBoPersonal(pnuAnn, pnuEmi, usuarioConfig.getCoDep(), usuarioConfig.getCempCodemp(), usuarioConfig.getCempCodemp());
                    }else{
                        //Para ReFirma
                        nombreDoc = null;
                        nombreFirma = null;
                    }
               
               }else if(ptiOpe.equals("6")){
                   nombreDoc = null;
               }else{
                   nombreDoc = null;
               }
            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
            docVerBean.setNoFirma(nombreFirma);
        }
        return(docVerBean);
   } 
   
   @Override
   public DocumentoVerBean getNombreCargaVoBoDoc(String pnuAnn, String pnuEmi,String ptiOpe){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        
        docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        if (docDatoBean!=null){
            if((docDatoBean.getTiEmi().equals("06") && (docDatoBean.getEsDocEmi().equals("5")||docDatoBean.getEsDocEmi().equals("7")) && ptiOpe.equals("6"))){
                    docVerBean.setDeMensaje("OK");
                    docVerBean.setInDoc(true);
                    docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
                    docVerBean.setFeFirma(docDatoBean.getFechaActual());
                    docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
            }else{
                docVerBean.setDeMensaje("Estado del Documento a cambiado");
            }
        }else{
             docVerBean.setDeMensaje("Error en datos del documento");
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=cargaDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&nuSec="+docDatoBean.getNuSecFirma();
            
            String nombreDoc=null;
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
            if(docDatoBean.getTiEmi().equals("06")){
               //Segun la Operacion Cargamos los documentos 
               if(ptiOpe.equals("6")){
                   nombreDoc = "OK";
               }else{
                   nombreDoc = null;
               }
            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
        }
        return(docVerBean);
   }   
   
   public String verificarBloqueoDocumento(String pnuAnn,String pnuEmi,String pnuSecFirma){
       String vResult="NO_OK";
       try {
            DocumentoObjBlock docBlock=documentoObjDao.getDatosDocBlock(pnuAnn, pnuEmi);
            if(docBlock!=null){
                if(docBlock.getNuSecFirma()!=null&&docBlock.getNuSecFirma().equals(pnuSecFirma)){
                    vResult="OK";
                }else{
                   Date fechaFirma=docBlock.getFechaFirma();
                   Date fechaActual=docBlock.getFechaActual();
                   if(fechaFirma!=null&&fechaActual!=null){
                        Date fechaMaxBloqueoDoc= new Date(fechaFirma.getTime()+3*60*1000);// 3 minutos de bloqueo
                        if(fechaActual.after(fechaMaxBloqueoDoc)){
                            docBlock=new DocumentoObjBlock();
                            docBlock.setNuAnn(pnuAnn);
                            docBlock.setNuEmi(pnuEmi);
                            docBlock.setNuSecFirma(pnuSecFirma);
                            docBlock.setTipoFirma("1");
                            vResult=documentoObjDao.updRemitosBlock(docBlock);
                        }else{
                            vResult="DOCUMENTO SE ESTA FIRMANDO.";
                        }
                   } 
                }
            }else{
                docBlock=new DocumentoObjBlock();
                docBlock.setNuAnn(pnuAnn);
                docBlock.setNuEmi(pnuEmi);
                docBlock.setNuSecFirma(pnuSecFirma);
                docBlock.setTipoFirma("1");
                vResult=documentoObjDao.insRemitosBlock(docBlock);
            }           
       } catch (Exception e) {
           e.printStackTrace();
           vResult=e.getMessage();
       }

       //si el doc existe en tbl bloqueos
            //decidir que tipo_firma es
                 //firma normal
                     //verificar si coincide numero de secuencia firma retorna procede
                     //sino pregunta si ya paso mas de 3 minutos
                         //si, entonces limpia nro secuencia firma y tiempo retorna procede
                         //sino retorna documento se esta firmando
                 //firma visto bueno        
       //sino retorna procede
       return vResult;
   }
   
   @Override
   public DocumentoVerBean getNombreFirmaDocAnexo(String pnuAnn, String pnuEmi,String pnuAnexo,String ptiOpe,UsuarioConfigBean usuarioConfig){
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setNuAne(pnuAnexo);
        docVerBean.setInDoc(false);
        boolean bswFirmaOnpe=true;
        
        DocumentoObjBean docObjBean = null;
        
        if(usuarioConfig.getSwFirma()!=null) {
            docVerBean.setNoPrefijo("[AF]");
            if(usuarioConfig.getSwFirma().equals("R")){
                docVerBean.setNoPrefijo("[R]");
                bswFirmaOnpe=false;
            }else{
                docObjBean = this.getNombreArchivoAnexo(pnuAnn, pnuEmi, pnuAnexo);
                if (docObjBean!=null && docObjBean.getNuTamano()>0){
                    docVerBean.setInDoc(true);
                }                
            }
        }
        if(bswFirmaOnpe){
            if (docObjBean!=null){
                    String vformatoDoc = "NO_OK";
                    vformatoDoc = anexoDocumentoService.getVerificaPdfDoc(docObjBean.getNuAnn(), docObjBean.getNuEmi(),docObjBean.getNuAne());

                    if (vformatoDoc.equals("OK")){
                        docVerBean.setDeMensaje("OK");
                        docVerBean.setInDoc(true);
                        docVerBean.setNuSecFirma(Utilidades.generateRandomNumber(8));
                        docVerBean.setInTipoFirma("F");
                    }else{
                        docVerBean.setDeMensaje(vformatoDoc);
                        docVerBean.setInDoc(false);
                    }
            }else{
                 docVerBean.setDeMensaje("Error en datos del documento");
            }            
        }else{
            docVerBean.setDeMensaje("Utilizar software Firma Onpe.");            
        }


        /*
          5 - Firmar Documento (PDF en \temp)
          6 - Docuemento Firmarmado por AUTOR (en Directorio Acrobat)
          7 - Docuemento Firmarmado por ENCARGO (en Directorio Acrobat)
          8 - Docuemento Numerado (en Directorio Acrobat)
        */
        
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=abrirAnexo&nuAnn="+docObjBean.getNuAnn()+"&nuEmi="+docObjBean.getNuEmi()+"&nuAne="+docObjBean.getNuAne();

            String nombreDoc=null;
            String nombreFirma=null;
            
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
//            if(docDatoBean.getTiEmi().equals("01")){
               //Segun la Operacion Cargamos los documentos 
               String vnumDoc="";
               String vnum_reg = "";
                
               if(ptiOpe.equals("5")){
                    
                   // Verificamos el Software de Firma
                    if(usuarioConfig.getSwFirma()!=null && usuarioConfig.getSwFirma().equals("O") ) {
                        //Para el Firma ONPE
                        // Se envia "#" y sera remplazado por "/"
                        vnum_reg = "R"+docObjBean.getNuAnn()+"."+docObjBean.getNuEmi()+"-"+docObjBean.getNuAne();
                        vnumDoc =  docVerBean.getNuSecFirma();
                        nombreDoc = "TEMP|"+vnum_reg+"-"+vnumDoc+".pdf";
                        nombreFirma = "TEMP|"+vnum_reg+"-"+vnumDoc;
                    }else{
                        //Para ReFirma
                        nombreDoc = null;
                        nombreFirma = null;
                    }
               
               }else if(ptiOpe.equals("6")){
                   nombreDoc = null;
               }else{
                   nombreDoc = null;
               }
//            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
            docVerBean.setNoFirma(nombreFirma);
        }
        return(docVerBean);
   }
   
   @Override
   public DocumentoVerBean getNombreCargaFirmaDocAnexo(String pnuAnn, String pnuEmi, String pnuAne,String ptiOpe){//ECUEVA
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setNuAne(pnuAne);
        docVerBean.setInDoc(false);
        
        DocumentoObjBean docObjBean = null;
        
        docObjBean = this.getNombreArchivoAnexo(pnuAnn, pnuEmi, pnuAne);
        if (docObjBean!=null){
            if(ptiOpe.equals("6")){
                    docVerBean.setDeMensaje("OK");
                    docVerBean.setInDoc(true);
                    docVerBean.setNuSecFirma(Utilidades.generateRandomNumber(8));
            }else{
                docVerBean.setDeMensaje("Estado del Documento a cambiado");
            }
        }else{
             docVerBean.setDeMensaje("Error en datos del documento");
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=cargaDocAnexo&nuAnn="+docObjBean.getNuAnn()+"&nuEmi="+docObjBean.getNuEmi()+"&nuAne="+docObjBean.getNuAne()+"&nuSec="+docVerBean.getNuSecFirma();
            
            String nombreDoc=null;
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
            //if(docDatoBean.getTiEmi().equals("01") || docDatoBean.getTiEmi().equals("05")){
               //Segun la Operacion Cargamos los documentos 
               if(ptiOpe.equals("6")){
                   nombreDoc = "OK";
               }else{
                   nombreDoc = null;
               }
            //}
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
        }
        return(docVerBean);
   }
   
    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String cargaDocAnexo(String pnuAnn,String pnuEmi,String pnuAne,String pnuSecFirma,String coUsuario, String prutaDoc) throws Exception {
        String vReturn = "NO_OK";
            try {
                DocumentoObjBean docObjBean = this.getNombreArchivoAnexo(pnuAnn, pnuEmi, pnuAne);
                if (docObjBean!=null && docObjBean.getNuTamano()>0){
                    docObjBean.setCoUseMod(coUsuario);
                    docObjBean.setNumeroSecuencia(pnuSecFirma);
                    vReturn=cargaDocAnexoFirmado(docObjBean);
                    if(vReturn.equals("OK")){
                        vReturn=anexoDocumentoService.getCanAnexosReqFirma(pnuAnn, pnuEmi);
                        if(vReturn.equals("0")){
                            vReturn=anexoDocumentoService.updRemitosResumenInFirmaAnexos("0", pnuAnn, pnuEmi);
                            if(!vReturn.equals("OK")){
                                throw new validarDatoException("Error actualizando remitos resumen firma anexos.");
                            }                                        
                        }else{
                            vReturn="OK";
                        }
                    }
                }else{
                   throw new validarDatoException("Error cargando documento Firmado."); 
                }
            } catch (validarDatoException e) {
                throw e;
            } catch (Exception e) {
                e.printStackTrace();
                throw new validarDatoException("ERROR EN TRANSACCION.");
            }
        return vReturn;
    }
    
    public String cargaDocAnexoFirmado(DocumentoObjBean docObjBean) {
        String vReturn = "NO_OK";
        
        if(docObjBean!=null){
            try {
                
                byte[] archivoByte = ArchivoTemporal.leerArchivo(docObjBean.getNumeroSecuencia());
                docObjBean.setDocumento(archivoByte);
                docObjBean.setNuTamano((int) Math.round(((double) archivoByte.length) / 1024));
                    
                
                vReturn=anexoDocumentoService.updArchivoAnexoFirmado(docObjBean);
            } catch (Exception ex) {
                StringBuffer mensaje = new StringBuffer();
                vReturn = "Error en Leer Documento de Repositorio";
                mensaje.append(docObjBean.getNumeroSecuencia()+":");
                mensaje.append(docObjBean.getNuAnn()+"."+docObjBean.getNuEmi());
                logger.error(mensaje,ex);            
            }
        }else{
            vReturn = "Error en Carga de Documento";
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("cargaDocEmi:Objeto docObjBean NULO");
            logger.error(mensaje);            
        }
        return vReturn;
    }

    public String generarFormatoSiglasFirma(String tipoDoc, String nuDoc, String anio, String institucion, String uuoo){
        tipoDoc = (tipoDoc==null?" ":tipoDoc);
        nuDoc = (nuDoc==null?" ":nuDoc);
        anio = (anio==null?" ":anio);
        institucion = (institucion==null?" ":institucion);
        uuoo = (uuoo==null?" ":uuoo);
        
        String cadenaSiglas = applicationProperties.getFormatoSiglas();
        cadenaSiglas=cadenaSiglas.replace("{TIPODOC}", tipoDoc +"$");  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{NUDOC}", nuDoc);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{ANIO}", anio);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{INS}", institucion);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{UUOO}", uuoo);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("Â", "");   //Reemplazamos el caracter Â
        cadenaSiglas=cadenaSiglas.replace("N°", "");   //Reemplazamos el caracter Â
        cadenaSiglas=cadenaSiglas.replace("/", "!");
        return cadenaSiglas;
    }

    @Override
    public DocumentoVerBean getNombreDocEmiReporte(String pnuAnn, String pnuEmi,String ptiOpe, Usuario usuario, String rutaReporteBase)
    {
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        DocumentoObjBean docObjBean = null;
        
        docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        if (docDatoBean!=null){
            docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
            docVerBean.setNumeroDoc(docDatoBean.getNumeroDoc());
            docVerBean.setFeFirma(docDatoBean.getFechaActual());
            docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
            if (docVerBean.getCoDocEmi().equals("232") || docVerBean.getCoDocEmi().equals("304")){
                docVerBean.setInDoc(true);
                docVerBean.setDeMensaje("OK");
                docObjBean = new DocumentoObjBean();
                docObjBean.setTipoDoc("pdf");
                if(docDatoBean.getNumeroDoc()==null){
                    docDatoBean.setNumeroDoc("      ");
                    docVerBean.setNumeroDoc("      ");
                }
            }else{
                //aqui verifica que el archivo existe en BD
                docObjBean = this.getNombreArchivo(pnuAnn, pnuEmi, docDatoBean.getTiCap());
                if (docObjBean!=null && docObjBean.getNuTamano()>0){
                    docVerBean.setInDoc(true);
                    docVerBean.setDeMensaje("OK");
                    //YUAL -se encontraba en la version anterior
                     docVerBean.setNombreDocumentoWord(docDatoBean.getNuAnn()+"|"+docObjBean.getwNombreArchivo());
                    // Activamos el Log de Visualiza Documento
                    
                    auditoriaMovimientoDocDao.audiVisualizaDocumento(docObjBean, usuario);
                    
                }else{
                    docVerBean.setInDoc(false);
                    docVerBean.setDeMensaje("Documento no se encuentra cargado en Repositorio");
                }
            }
        }
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=abrirDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&tiCap="+docDatoBean.getTiCap()/*;//*/+"&nroView="+LsAleatorioViewDoc.getInstancia().add();
            
            
            /*
              Parametro
              3 - Generar Documento (\año\*.docx)
              4 - Generar Documento (\año\*.pdf)
              5 - Generar Documento (\año\*.*)
            */
            
            String nombreDoc;
            //verificamos si el documento es generado en la institucion
            //YUAL si estaba en la version anterior
            //if(docDatoBean.getTiEmi().equals("01")||docDatoBean.getTiEmi().equals("05")){
            if(docDatoBean.getTiEmi().equals("06")){
                //Nombre de los archivos
                if(ptiOpe.equals("3")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".docx";
                }else if(ptiOpe.equals("4")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+".pdf";
                }else if(ptiOpe.equals("5")){
                    nombreDoc = docDatoBean.getNuAnn()+"|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                }else{
                    nombreDoc = "TEMP|"+docDatoBean.getTipoDoc()+"-I"+docDatoBean.getNuCorEmi()+"-"+ docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()/*.replaceAll("/", "-")*/+"."+docObjBean.getTipoDoc();
                }
                
            }else{ /*archivo viene de mesa de partes*/
               nombreDoc = "TEMP|I"+docDatoBean.getNuCorEmi()+"."+docObjBean.getTipoDoc();
            }
            
            if (docVerBean.getCoDocEmi().equals("232") || docVerBean.getCoDocEmi().equals("304"))
            {   
                List<DocumentoDatoBean> listaSubReport1 = documentoObjDao.CargarSubReporte1(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                        
                List<DocumentoDatoBean> listaSubReport2 = documentoObjDao.CargarSubReporte2(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                
                
//                String rutaImagen = rutaReporteBase + "/logo_onpe.jpg";                
                String logo = applicationProperties.getLogoReporteB64();
                ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
                

                
                Map parametros = new HashMap();
                parametros.put("P_NU_ANN", docDatoBean.getNuAnn());
                parametros.put("P_NU_EMI", docDatoBean.getNuEmi());
                parametros.put("P_NU_DOC", docDatoBean.getNumeroDoc());  
                parametros.put("P_FECHA", docDatoBean.getFechaActual());  
                parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);
                parametros.put("datosSubRep1", listaSubReport1);
                parametros.put("datosSubRep2", listaSubReport2);
                parametros.put("SUB_REPORT_DIR1", rutaReporteBase);
                parametros.put("SUB_REPORT_DIR2", rutaReporteBase);
                //urlDoc = "reporte?coReporte=TDR11&coParametros=P_NU_ANN="+docDatoBean.getNuAnn()+"|P_NU_EMI="+docDatoBean.getNuEmi()+"|P_NU_DOC="+docDatoBean.getNumeroDoc()+"|P_FECHA="+docDatoBean.getFechaActual()+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg&coSubReportes=SUB_REPORT_DIR1=TDR11_SUB1|SUB_REPORT_DIR2=TDR11_SUB2";
                try {
                    ReporteBean objReporte = getGenerarReporte("PDF",rutaReporteBase,parametros,nombreDoc,docDatoBean.getNuAnn(),docDatoBean.getNuEmi());
                    if(bis!=null)bis.close();
                    urlDoc = objReporte.noUrl; 
                } catch (Exception e) {
                    docVerBean.setInDoc(false);
                    docVerBean.setDeMensaje(e.getMessage());
                }
            }
            
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
        }
        return(docVerBean);        
   }
    
    public ReporteBean getGenerarReporte(String formato,String rutaReporteBase,Map parametros,String archivoServer,String nuAnn, String nuEmi)
    {
        ReporteBean objReporte = new ReporteBean();
        String eserror="1";//si
        String coRespuesta="";
        String deRespuesta="";
        String prutaReporte = "";
        String deNoDoc = "";
        String nombreArchivo = "";
        String coReporte = "";
        try{          
            
            String extensionArch;
            int tipoArchivo=0;
            byte[] archivo;
            
            //String fecha = Utility.getInstancia().getStringFechaYYYYMMDDHHmm(new Date());  
            
            List<DocumentoDatoBean> lista = null;
            String formatoSiglas = "";
            DocumentoDatoBean objDocDatoBean;
            try{  
                lista = new ArrayList<DocumentoDatoBean>();
                objDocDatoBean = documentoObjDao.CargarCabeceraReporte(nuAnn, nuEmi);
                lista.add(objDocDatoBean);
                String nuDocumento = parametros.get("P_NU_DOC")==null?"":(parametros.get("P_NU_DOC").toString().equals("0")?"":parametros.get("P_NU_DOC").toString());
                       nuDocumento = nuDocumento.equals("")?(objDocDatoBean.getNuDoc().equals("0")?"":objDocDatoBean.getNuDoc()):nuDocumento;
                formatoSiglas=DocumentoXmlService.generarFormatoSiglas(objDocDatoBean.getDeTipDocAdm(),nuDocumento,objDocDatoBean.getNuAnn(),applicationProperties.getSiglaInstitucion(),objDocDatoBean.getDeDocSig());
                parametros.put("formatoSiglas", formatoSiglas);
                eserror="0";
            }catch(Exception ex){               
                eserror="1";
                deRespuesta=ex.getMessage();
            }
            if (lista.size()==0) {                 
                 coRespuesta = "1";
                 deRespuesta="No existe información para generar reporte.";
                 throw new Exception(deRespuesta);
            }
            if(formato.equalsIgnoreCase("PDF")){
                coReporte = "TDR11";                
                extensionArch=".pdf";
                tipoArchivo = 2;
            }else{
                coReporte = "TDR11_XLS";
                extensionArch=".xls";
                tipoArchivo = 1;
            }
            String rutaJasper =  rutaReporteBase + "/" + coReporte + ".jasper";
            
            nombreArchivo = Utilidades.generateRandomNumber(10);
            //prutaReporte = "EMISION_"+nombreArchivo + extensionArch;
            //deNoDoc = "temp|EMISION_"+fecha+extensionArch;
            
            prutaReporte = nombreArchivo + extensionArch;
            deNoDoc = archivoServer;
            //deNoDoc = "temp|"+archivoServer;
            
            Utilidades util = new Utilidades();
            
            archivo = util.GenerarReporteLista(rutaJasper, lista, coReporte, parametros, tipoArchivo);
            
            if (archivo==null) {
                eserror="1";
                coRespuesta="1";
                deRespuesta = "No se generó correctamente el archivo.";
                throw new Exception("No se generó correctamente el archivo.");
            }
            
            String rutaBase = applicationProperties.getRutaTemporal() + "/" + prutaReporte;
            
            File tmpFile = new File(rutaBase);
            FileOutputStream fos = new FileOutputStream(tmpFile);
            fos.write(archivo);
            fos.flush();
            fos.close();
            String rutaUrl = "reporte?coReporte=";
            prutaReporte = rutaUrl + prutaReporte;
            
            if(eserror.equals("0")){
                coRespuesta=eserror;
            }else{
                coRespuesta="1";
            }
            
        } catch (FileNotFoundException ex) {            
            eserror="1";
            deRespuesta=ex.getMessage();
        } catch (IOException ex) {            
            eserror="1";
            deRespuesta=ex.getMessage();
        }  
        finally{
            objReporte.setcoRespuesta(coRespuesta);
            objReporte.setdeRespuesta(deRespuesta);
            objReporte.setnoUrl(prutaReporte);
            objReporte.setnoDoc(deNoDoc);
            return objReporte;
        }
            
    }

    @Override
    public DocumentoVerBean getNombreFirmaDocReporte(String pnuAnn, String pnuEmi, String ptiOpe, UsuarioConfigBean usuarioConfig, String rutaReporteBase) {
        DocumentoVerBean docVerBean= new DocumentoVerBean();
        String vin_pref="1";
        String vin_TSA="0";
        
        docVerBean.setNuAnn(pnuAnn);
        docVerBean.setNuEmi(pnuEmi);
        docVerBean.setInDoc(false);
        
        DocumentoDatoBean docDatoBean=null;
        
        if(usuarioConfig.getSwFirma()!=null) {
            docVerBean.setNoPrefijo("[NF]");
            if(usuarioConfig.getSwFirma().equals("R")){
                docVerBean.setNoPrefijo("[R]");
            }
            docDatoBean = getDatosDoc(pnuAnn, pnuEmi);
        }
        
        if (docDatoBean!=null ){
            if( (docDatoBean.getTiEmi().equals("06") && docDatoBean.getEsDocEmi().equals("7") && ptiOpe.equals("5")) ){
                //Verificamos si el documento esta en PDF
                String coTipDoc = docDatoBean.getCoDoc();
                String vformatoDoc = "NO_OK";
                if (coTipDoc.equals("232") || coTipDoc.equals("304")){
                    vformatoDoc="OK";
                    vin_pref = "N";
                }else{
                    vformatoDoc = emiDocumentoAdmService.getVerificaPdfDoc(docDatoBean.getNuAnn(), docDatoBean.getNuEmi(),docDatoBean.getTiCap());
                    //verificar si el tipo de documento se numero y firma o solo se numera.
                    String vin_firma=emiDocumentoAdmService.getInFirmaDocAdm(coTipDoc); 
                    if(vin_firma.equals("1")){//solo firma
                        vin_pref = "N";
                    }else if(vin_firma.equals("2")){//firma y numera
                        vin_pref = "1";
                    }
                }                
                
                // Para el Caso de Proveido y Hoja de Envio
//                if (docDatoBean.getCoDoc().equals("232") || docDatoBean.getCoDoc().equals("304")){
//                    vformatoDoc="OK";
//                    vin_pref = "N";
//                }else{
//                    vformatoDoc = emiDocumentoAdmService.getVerificaPdfDoc(docDatoBean.getNuAnn(), docDatoBean.getNuEmi(),docDatoBean.getTiCap());
//                    vin_pref = "1";
//                }
                
                if (vformatoDoc.equals("OK")){
                    docVerBean.setDeMensaje("OK");
                    docVerBean.setInDoc(true);
                    docVerBean.setNuSecFirma(docDatoBean.getNuSecFirma());
                    docVerBean.setFeFirma(docDatoBean.getFechaActual());
                    docVerBean.setCoDocEmi(docDatoBean.getCoDoc());
                    if (usuarioConfig.getInFirma()!= null && usuarioConfig.getInFirma().equals("1")){
                        docVerBean.setInTipoFirma("N");
                    }else{
                        if (usuarioConfig.getInFirma().equals("0")){
                            String vinDoc = documentoObjDao.getInFirmaDoc(docDatoBean.getCoDepEmi(),docDatoBean.getCoDoc());
                            docVerBean.setInTipoFirma(vinDoc);
                        }else{
                            docVerBean.setInTipoFirma("F");
                        }
                    }
                    
                    // verificamos si tiene numero de documento 
                        if(docDatoBean.getNumeroDoc()==null || docDatoBean.getNumeroDoc().length()==0 ){
                            // Obtenemos el Numero de Documento
                            if (docDatoBean.getTiEmi().equals("06")){
                                String vnuDoc=emiDocumentoAdmService.getNumeroDocSiguienteAdm(docDatoBean.getNuAnn(),docDatoBean.getCoDepEmi(), docDatoBean.getCoDoc());
                                docVerBean.setNumeroDoc(vnuDoc);
                                docDatoBean.setNumeroDoc(vnuDoc);
                            }
//                            else if(docDatoBean.getTiEmi().equals("05")){
//                                String vnuDoc=emiDocumentoPersonalService.getNumeroDocSiguientePersonal(docDatoBean.getNuAnn(),docDatoBean.getCoEmpEmi(),docDatoBean.getCoDoc());
//                                docVerBean.setNumeroDoc(vnuDoc);
//                                docDatoBean.setNumeroDoc(vnuDoc);
//                                
//                            }
                        }else{
                            docVerBean.setNumeroDoc(docDatoBean.getNumeroDoc());
                        }
                }else{
                    docVerBean.setDeMensaje(vformatoDoc);
                    docVerBean.setInDoc(false);
                }
            }
        }else{
             docVerBean.setDeMensaje("Error en datos del documento");
        }

        /*
          5 - Firmar Documento (PDF en \temp)
          6 - Docuemento Firmarmado por AUTOR (en Directorio Acrobat)
          7 - Docuemento Firmarmado por ENCARGO (en Directorio Acrobat)
          8 - Docuemento Numerado (en Directorio Acrobat)
        */
        
        
        if (docVerBean.isInDoc()){
            //Creando la Url del Documento 
            //Cambiar para colocar la seguridad maximo 5 segundos
            String urlDoc = "documento?accion=abrirDoc&nuAnn="+docDatoBean.getNuAnn()+"&nuEmi="+docDatoBean.getNuEmi()+"&tiCap="+docDatoBean.getTiCap()/*;//*/+"&nroView="+LsAleatorioViewDoc.getInstancia().add();
           
            String nombreDoc=null;
            String nombreFirma=null;
            
            //verificamos si el documento es generado en la institucion
            // NOTA controlar en caso de TI_EMI <> 01
            if(docDatoBean.getTiEmi().equals("06")){
               //Segun la Operacion Cargamos los documentos 
               String vnumDoc="";
               String vnum_reg = "";
                
               if(ptiOpe.equals("5")){
                   //verificar si el documento se firma TSA
                   /*if(docDatoBean.getTiEmi().equals("01")){
                    String vFirmaTSA = verificarFirmaTSAService.VerificarDestinoCNM(docDatoBean.getNuAnn(), docDatoBean.getNuEmi(), docDatoBean.getCoDoc());
                    if(vFirmaTSA.equals("OK")){
                        vin_TSA="1";
                    }
                   }*/
                   // Verificamos el Software de Firma
                    if(usuarioConfig.getSwFirma()!=null && usuarioConfig.getSwFirma().equals("O") ) {
                        //Para el Firma ONPE
                        // Se envia "#" y sera remplazado por "/"
                        vnum_reg = "I"+docDatoBean.getNuCorEmi()+"."+docDatoBean.getNuSecFirma();
                        //String vsig_inst= docDatoBean.getSiglasDoc().contains("/")?"-DP-":"-DP!";
                        /*String vsig_inst= docDatoBean.getSiglasDoc().contains("/")?"-CNM-":"-CNM!";
                        vnumDoc =  docDatoBean.getTipoDoc()+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+vsig_inst+docDatoBean.getSiglasDoc().replaceAll("/", "!")+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");*/
                        String cadena = generarFormatoSiglasFirma(docDatoBean.getTipoDoc(), docDatoBean.getNumeroDoc(),docDatoBean.getNuAnn(), applicationProperties.getSiglaInstitucion() , docDatoBean.getSiglasDoc());
//                        vnumDoc =  docDatoBean.getTipoDoc()+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()+"!"+applicationProperties.getSiglaInstitucion()+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");                        
                        vnumDoc =  cadena.replace(" ", "")+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");
//                        vnumDoc =  "Prueba"+"$"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc()+"!"+applicationProperties.getSiglaInstitucion()+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");
                        
                        //vnumDoc =  "Prueba"+"$"+"Prueba de Plantillas"+"$"+docDatoBean.getDeLugar()+"$"+docDatoBean.getFechaFirma().replace("-", "$");  
                        nombreDoc = "TEMP|"+vnum_reg+"$"+vnumDoc+"$"+vin_pref+".pdf";
                        nombreFirma = "TEMP|"+vnum_reg+"$"+vnumDoc+"$"+vin_pref;
                    }else{
                        //Para ReFirma
//                        vnum_reg = "R"+docDatoBean.getNuCorEmi()+"."+docDatoBean.getNuSecFirma()+"#";
//                        vnumDoc =  docDatoBean.getDeLugar()+"-"+docDatoBean.getNumeroDoc()+"-"+docDatoBean.getNuAnn()+"-"+docDatoBean.getSiglasDoc().replaceAll("/", "-").replaceAll("-", "_")+"-"+docDatoBean.getFechaFirma();
//                        nombreDoc = "TEMP|"+vnum_reg+docDatoBean.getTipoDoc()+"-"+vnumDoc+"-1.pdf";
//                        nombreFirma = "TEMP|"+vnum_reg+docDatoBean.getTipoDoc()+"-"+vnumDoc+"-N";
                    }
               
               }else if(ptiOpe.equals("6")){
                   nombreDoc = null;
               }else{
                   nombreDoc = null;
               }
               
                // Para el Caso de Proveidos se tiene que generar
            if (docVerBean.getCoDocEmi().equals("232") || docVerBean.getCoDocEmi().equals("304")){
               List<DocumentoDatoBean> listaSubReport1 = documentoObjDao.CargarSubReporte1(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                        
               List<DocumentoDatoBean> listaSubReport2 = documentoObjDao.CargarSubReporte2(docDatoBean.getNuAnn(), docDatoBean.getNuEmi());
                
//                String rutaImagen = rutaReporteBase + "/logo_onpe.jpg";
                
                String logo = applicationProperties.getLogoReporteB64();                
                ByteArrayInputStream bis = new ByteArrayInputStream(Base64.decodeBase64(logo));
                
                Map parametros = new HashMap();
                parametros.put("P_NU_ANN", docDatoBean.getNuAnn());
                parametros.put("P_NU_EMI", docDatoBean.getNuEmi());
                parametros.put("P_NU_DOC", docDatoBean.getNumeroDoc());  
                parametros.put("P_FECHA", docDatoBean.getFechaActual());  
                parametros.put("P_LOGO_DIR", bis.available()==0?null:bis);
                parametros.put("datosSubRep1", listaSubReport1);
                parametros.put("datosSubRep2", listaSubReport2);
                parametros.put("SUB_REPORT_DIR1", rutaReporteBase);
                parametros.put("SUB_REPORT_DIR2", rutaReporteBase);
                //urlDoc = "reporte?coReporte=TDR11&coParametros=P_NU_ANN="+docDatoBean.getNuAnn()+"|P_NU_EMI="+docDatoBean.getNuEmi()+"|P_NU_DOC="+docDatoBean.getNumeroDoc()+"|P_FECHA="+docDatoBean.getFechaActual()+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg&coSubReportes=SUB_REPORT_DIR1=TDR11_SUB1|SUB_REPORT_DIR2=TDR11_SUB2";
                try {
                    ReporteBean objReporte = getGenerarReporte("PDF",rutaReporteBase,parametros,nombreDoc,docDatoBean.getNuAnn(),docDatoBean.getNuEmi());
                    if(bis!=null)bis.close();
                    urlDoc = objReporte.noUrl; 
                } catch (Exception e) {
                    docVerBean.setInDoc(false);
                    docVerBean.setDeMensaje(e.getMessage());
                }
                
                //urlDoc = "reporte?coReporte=TDR11&coParametros=P_NU_ANN="+docDatoBean.getNuAnn()+"|P_NU_EMI="+docDatoBean.getNuEmi()+"|P_NU_DOC="+docDatoBean.getNumeroDoc()+"|P_FECHA="+docVerBean.getFeFirma()+"&coImagenes=P_LOGO_DIR=logo_onpe.jpg&coSubReportes=SUB_REPORT_DIR1=TDR11_SUB1|SUB_REPORT_DIR2=TDR11_SUB2";
            }
               
            }
            docVerBean.setUrlDocumento(urlDoc);
            docVerBean.setNoDocumento(nombreDoc);
            docVerBean.setNoFirma(nombreFirma);
        }
        return(docVerBean);
    }
 
        @Override
    public DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap, String pAbreWord) {
        DocumentoObjBean docObjBean = null;
        docObjBean = documentoObjDao.leerDocumento(pnuAnn, pnuEmi, ptiCap, pAbreWord);
        
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
    
}
