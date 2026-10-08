package pe.gob.onpe.tramitedoc.service.impl;

import com.itextpdf.kernel.pdf.PdfAConformanceLevel;
import com.itextpdf.kernel.pdf.PdfOutputIntent;
import com.itextpdf.kernel.pdf.PdfWriter;
import com.itextpdf.kernel.pdf.WriterProperties;
import com.itextpdf.pdfa.PdfADocument;
import com.lowagie.text.pdf.PdfDocument;
import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.IContext;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DatosPlantillaDoc;
import pe.gob.onpe.tramitedoc.bean.PlantillaDocx;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.service.DatosPlantillaService;
import pe.gob.onpe.tramitedoc.service.DocumentoXmlService;
import pe.gob.onpe.tramitedoc.util.Plantillas;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

@Service("documentoXmlService")
public class DocumentoXmlServiceImp implements DocumentoXmlService{

    @Autowired
    private DatosPlantillaDao datosPlantillaDao;    

    @Autowired
    private DatosPlantillaService datosPlantillaService;    
    
    @Autowired
    private ApplicationProperties applicationProperties;           

    @Override
    public DatosPlantillaDoc datosParaPlantilla(String pnuAnn, String pnuEmi){
        DatosPlantillaDoc  datosPlantilla = null;
        
        List<DestinatarioDocumentoEmiBean> listDestinos = null;        
        List<ReferenciaBean> listReferencia = null;
        
        try{
            datosPlantilla = datosPlantillaDao.getDocumentoEmitido(pnuAnn, pnuEmi);
            listDestinos     = datosPlantillaDao.getLstDestintarios(pnuAnn,pnuEmi);
            listReferencia   = datosPlantillaDao.getLstReferencia(pnuAnn,pnuEmi);
            // Mapeando datos del documento
            if( datosPlantilla!= null && listDestinos!= null){
                datosPlantilla.setNombreAnio(datosPlantillaDao.getParametros("NOMBRE_ANIO"));
                datosPlantilla.setNombreAnio2(datosPlantillaDao.getParametros("NOMBRE_ANIO2"));
                //datosPlantilla.setNumeroDoc("N° "+datosPlantilla.getNuDocEmi()+"-"+datosPlantilla.getNuAnn()); // Numero de Documento
                
                datosPlantilla.setSiglaDoc(generarFormatoSiglas(datosPlantilla.getDeTipoDoc(), datosPlantilla.getNuDocEmi(),datosPlantilla.getNuAnn(), applicationProperties.getSiglaInstitucion() , datosPlantilla.getDeDocSig()));
                datosPlantilla.setNumeroDoc("N° "+datosPlantilla.getNuDocEmi()+"-"+datosPlantilla.getNuAnn()+"-"+datosPlantilla.getDeDocSig()); // Numero de Documento                
                datosPlantilla.setFechaDoc(datosPlantillaDao.getDistritoLocal(datosPlantilla.getCoLocEmi())+", "+datosPlantilla.getFeEmiLargo());
                datosPlantilla.setPiePagina(datosPlantillaDao.getPiePagina(datosPlantilla.getCoEmpRes(), datosPlantilla.getCoDepEmi()));
                datosPlantilla.setDeCargoFunEmiMae(datosPlantilla.getDeCargoFunEmiMae()+datosPlantilla.getDeTiFun());
                
                // Cargo de Funcionario
                if(datosPlantilla.getCoEmpEmi().equals(datosPlantilla.getCoEmpFun())){
                    if (datosPlantilla.getDeCargoFun()!=null && datosPlantilla.getDeCargoFun().length()>0 ){
                       datosPlantilla.setDeDepEmi(datosPlantilla.getDeCargoFun());
                    }
                    datosPlantilla.setDeDepEmi(datosPlantilla.getDeDepEmi()+datosPlantilla.getDeTiFun());
                }
                
                // Verificamos Referencia
                if (listReferencia!=null){
                    ReferenciaBean datoReferencia = new ReferenciaBean();
                    String deReferencia = "";
                    for(int i=0; i<listReferencia.size(); i++) {
                        datoReferencia = listReferencia.get(i);
                        /*String vsig_inst= datoReferencia.getDeDocSig().trim().length()>0?datoReferencia.getDeDocSig().contains("/")?"-CNM-":"-CNM/":"";
                        deReferencia = deReferencia + datoReferencia.getDeTipoDoc()+" N° "+ datoReferencia.getLiNuDoc()+vsig_inst+datoReferencia.getDeDocSig()+" ("+datoReferencia.getFeEmiCorta()+")\n";*/                         
                        String siglaDoc = "";
                        if(datoReferencia.getLiNuDoc()!=null){
                            String[] cadenaSplit = datoReferencia.getLiNuDoc().split("-");  //si ti_emi es '01' o '05' el formato es "000003-2016-SGADYAG" en otro caso es "SGADYAG"                        
                            //YUAL
                            if(cadenaSplit.length > 2){     
                                if(cadenaSplit.length==4){
                                 siglaDoc = generarFormatoSiglas(datoReferencia.getDeTipoDoc(), cadenaSplit[0], cadenaSplit[1], applicationProperties.getSiglaInstitucion() , cadenaSplit[2]+"-"+cadenaSplit[3]);
                                }
                                else{
                                 siglaDoc = generarFormatoSiglas(datoReferencia.getDeTipoDoc(), cadenaSplit[0], cadenaSplit[1], applicationProperties.getSiglaInstitucion() , cadenaSplit[2]);
                                }
                                   
                            //if(cadenaSplit.length > 1){
                            
                               
                            }else{
                               // siglaDoc = generarFormatoSiglas(datoReferencia.getDeTipoDoc(), "", "",applicationProperties.getSiglaInstitucion() , cadenaSplit[0]);
                                siglaDoc=datoReferencia.getDeTipoDoc()+" "+datoReferencia.getLiNuDoc();
                            }
                        }
                        else
                        {
                            siglaDoc = generarFormatoSiglas(datoReferencia.getDeTipoDoc(),"", "","","S/N");
                        }                       
                         
                        siglaDoc = siglaDoc.replace("€", "");
                        siglaDoc = siglaDoc.replace("‹", "");
                        siglaDoc = siglaDoc.replace("", "  ");
                        siglaDoc = siglaDoc.replace("", "");                        
                        //deReferencia = deReferencia + datoReferencia.getDeTipoDoc()+" N° "+ datoReferencia.getLiNuDoc()+"/"+applicationProperties.getSiglaInstitucion()+" ("+datoReferencia.getFeEmiCorta()+")\n";
                        deReferencia = deReferencia + siglaDoc+" ("+datoReferencia.getFeEmiCorta()+")\n";                        
                    }   
                     datosPlantilla.setReferenciaDoc(deReferencia); //Referencia de documento
                }
                
                // Verificamos Destinos
                DestinatarioDocumentoEmiBean datoDestino = new DestinatarioDocumentoEmiBean();
                
               // if(datosPlantilla.getCoTipoDoc().equals("234") || datosPlantilla.getCoTipoDoc().equals("250") || datosPlantilla.getCoTipoDoc().equals("012")){ //Tipo de Documento
               if(datosPlantilla.getInMultiple().equals("1")){//DOC MULTIPLE
                    String deDestino = "";
                    String deDestino2 = "";
                    String deTituloDestino = "";
                   
                    //if(listDestinos.size()<8 && datosPlantilla.getCoTipoDoc().equals("012") )
                    if(listDestinos.size()<8 )
                    {
                    
                        for(int i=0; i<listDestinos.size(); i++) {
                            datoDestino = listDestinos.get(i);
                            if(i>0){
                                deDestino=deDestino+"\n\n";
                            }

                            if(datosPlantilla.getCoTipoDoc().equals("012")){
                                 deDestino = deDestino +(datoDestino.getNombreDestinatario()==null?"":datoDestino.getNombreDestinatario())+"\n" +(datoDestino.getCargo()==null?"":datoDestino.getCargo()+datoDestino.getCoTramite())+ "\n" +(datoDestino.getEntidadPrivadaDestinatario()==null?"":datoDestino.getEntidadPrivadaDestinatario())+"\n" +datoDestino.getDireccionDestinatario();
                            }else{
                                if(datoDestino.getCoTipoDestino().equals("01")){  //Si el destino es Institucional
    //                            if(datoDestino.getCoEmpleado().equals(datoDestino.getCoLocal())){
    //                                if (datoDestino.getDeTramite()!=null && datoDestino.getDeTramite().length()>0){
    //                                    datoDestino.setDeDependencia(datoDestino.getDeTramite());
    //                                }
    //                                datoDestino.setDeDependencia(datoDestino.getDeDependencia()+datoDestino.getCoTramite());
    //                            }
                                    deDestino = deDestino + datoDestino.getDeEmpleado()+"\n" +datoDestino.getDeCargoFunDestMae()+datoDestino.getCoTramite()+"\n" +datoDestino.getDeDepDestMae();

                                }else{ // Otros Destinos
                                    deDestino = deDestino + datoDestino.getDeEmpleado()+"\n";
                                }
                            }      
                        }
                    }
                    else
                    {
                        deDestino="DESTINATARIO MULTIPLE SEGÚN LISTADO ANEXO N° 01";
                        //deTituloDestino=deTituloDestino+"ANEXO N° 01\nDESTINATARIOS DEL OFICIO MULTIPLE";//de_tipo_doc
                        if (datosPlantilla.getDeTipoDoc().contains("CARTA")){
                              deTituloDestino=deTituloDestino+"ANEXO N° 01\nDESTINATARIOS DE LA "+datosPlantilla.getDeTipoDoc();
                        }
                        else {
                               deTituloDestino=deTituloDestino+"ANEXO N° 01\nDESTINATARIOS DEL "+datosPlantilla.getDeTipoDoc();
                        }
                        for(int i=0; i<listDestinos.size(); i++) {
                               datoDestino = listDestinos.get(i);
                               if(i>0){
                                   deDestino2=deDestino2+"\n\n";
                               }

                               if(datosPlantilla.getCoTipoDoc().equals("012")){
                                    deDestino2 = deDestino2 +(datoDestino.getNombreDestinatario()==null?"":datoDestino.getNombreDestinatario())+"\n" +(datoDestino.getCargo()==null?"":datoDestino.getCargo()+datoDestino.getCoTramite())+"\n" +(datoDestino.getEntidadPrivadaDestinatario()==null?"":datoDestino.getEntidadPrivadaDestinatario())+"\n" +datoDestino.getDireccionDestinatario();
                               }else{
                                   if(datoDestino.getCoTipoDestino().equals("01")){  

                                       deDestino2 = deDestino2 + datoDestino.getDeEmpleado()+"\n" +datoDestino.getDeCargoFunDestMae()+datoDestino.getCoTramite()+"\n" +datoDestino.getDeDepDestMae();

                                   }else{ // Otros Destinos
                                       deDestino2 = deDestino2 + datoDestino.getDeEmpleado()+"\n";
                                   }
                               }      
                           }
                       deDestino2=deDestino2+"";
                    }
                    datosPlantilla.setDepTituloDestLista(deTituloDestino);
                    datosPlantilla.setDepDestinoLista(deDestino2); 
                    datosPlantilla.setDepDestino(deDestino); // Dependencia de Destino
                    datosPlantilla.setEmpDestino(" "); // Empleado de Destino
                    datosPlantilla.setCopiaDoc(" "); // Copia de Documento

                }else{
                    datoDestino=listDestinos.get(0);
                    if(datoDestino.getCoTipoDestino().equals("01")){  //Si el destino es Institucional
                        if(datoDestino.getCoEmpleado().equals(datoDestino.getCoLocal())){
                            if (datoDestino.getDeTramite()!=null && datoDestino.getDeTramite().length()>0){
                                datoDestino.setDeDependencia(datoDestino.getDeTramite());
                            }
                            datoDestino.setDeDependencia(datoDestino.getDeDependencia()+datoDestino.getCoTramite());
                        }
                        datosPlantilla.setDepDestino(datoDestino.getDeDependencia()); // Dependencia de Destino
                        datosPlantilla.setEmpDestino(datoDestino.getDeEmpleado()); // Empleado de Destino
                        datosPlantilla.setDeCargoFunDestMae(datoDestino.getDeCargoFunDestMae()+datoDestino.getCoTramite());
                        datosPlantilla.setDireccionDestinatario(" ");
                        datosPlantilla.setCargo((datoDestino.getCargo()==null?"":datoDestino.getCargo()));
                        datosPlantilla.setDeDepDestMae(datoDestino.getDeDepDestMae());
                    }else{
                        datosPlantilla.setDepDestino(datoDestino.getDeEmpleado()); // Dependencia de Destino
                        datosPlantilla.setEmpDestino(" "); // Empleado de Destino
                        datosPlantilla.setDeCargoFunDestMae(" ");
                        datosPlantilla.setDireccionDestinatario(datoDestino.getDireccionDestinatario());
                        datosPlantilla.setCargo((datoDestino.getCargo()==null?"":datoDestino.getCargo()));
                        if(datoDestino.getCoTipoDestino().equals("02") || datoDestino.getCoTipoDestino().equals("04")){
                            datosPlantilla.setEntidadPrivadaDestinatario(datoDestino.getEntidadPrivadaDestinatario());
                        }else{
                            datosPlantilla.setEntidadPrivadaDestinatario("");
                        }
                    }
                    
                    datosPlantilla.setNombreDestinatario(datoDestino.getNombreDestinatario());
                    if(listDestinos.size()>1){
                        String deDestino = "";
                        for(int i=1; i<listDestinos.size(); i++) {
                            datoDestino = listDestinos.get(i);
                            deDestino = deDestino + datoDestino.getDeDependencia()+ "\n";
                        }   
                        datosPlantilla.setCopiaDoc(deDestino); 
                    }else{
                        datosPlantilla.setCopiaDoc(" "); 
                    }
                }                
            }else{
                datosPlantilla = null;
            }            
        }catch(Exception ex){
            ex.printStackTrace();
        }        
        
        return(datosPlantilla);        
    }

    
    @Override
    public DocumentoObjBean crearDocx(String pnuAnn, String pnuEmi){
        DocumentoObjBean docObjBean = null;

        long startTime = System.currentTimeMillis();
        
        DatosPlantillaDoc  datosPlantilla=datosParaPlantilla(pnuAnn,pnuEmi);
        PlantillaDocx plantillaDocx = Plantillas.getInstancia().getPlantilla(datosPlantilla.getCoTipoDoc(), datosPlantilla.getCoDepEmi());
        if(plantillaDocx != null){
            try
            {
                IXDocReport report = plantillaDocx.getTemplate();

                if(report!=null){
                    docObjBean = new DocumentoObjBean();
                    docObjBean.setNombreArchivo(plantillaDocx.getNomArchivo());

                    IContext context = report.createContext();

                    context.put("NOMBRE_ANIO",datosPlantilla.getNombreAnio());
                    
                    context.put("CORRELATIVO",datosPlantilla.getNuCorEmi());
                    context.put("TIPO_DOC",datosPlantilla.getDeTipoDoc());
                    context.put("SIGLA_DOC",datosPlantilla.getSiglaDoc());
                    context.put("NUMERO_DOC",datosPlantilla.getNumeroDoc());
//                    context.put("NUMERO_DOC","Informe de Prueba");
                    //context.put("SIGLA_DOC",datosPlantilla.getDeDocSig());
                    context.put("FECHA_DOC",datosPlantilla.getFechaDoc());
                    context.put("UUOO_EMITE",datosPlantilla.getDeDepEmi());
                    context.put("EMPLEADO_EMITE",datosPlantilla.getDeEmpEmi());                    
                    context.put("REFERENCIA",datosPlantilla.getReferenciaDoc());
                    context.put("ASUNTO",datosPlantilla.getDeAsunto());
                    context.put("NU_DNI",datosPlantilla.getNuDniDes());
                    context.put("PIE_PAGINA",datosPlantilla.getPiePagina());
                    context.put("UUOO_DESTINO",datosPlantilla.getDepDestino()+"");
                    context.put("TITULO_LISTA_DESTINO",(datosPlantilla.getDepTituloDestLista()==null? "":datosPlantilla.getDepTituloDestLista()));
                    context.put("LISTA_DESTINO",datosPlantilla.getDepDestinoLista()+"");
                    context.put("EMPLEADO_DESTINO",datosPlantilla.getEmpDestino());
                    context.put("COPIA",(datosPlantilla.getCopiaDoc()==null?"":"cc.: "+datosPlantilla.getCopiaDoc()));
                    context.put("INICIALES_EMP",datosPlantilla.getDeIniciales());
                    context.put("DEPENDENCIA_EMITE",datosPlantilla.getDeDepEmiMae());
                    context.put("DEPENDENCIA_DESTINO",datosPlantilla.getDeDepDestMae());
                    context.put("CARGO_EMP_EMITE",datosPlantilla.getDeCargoFunEmiMae());
                    context.put("CARGO_EMP_DESTINO",datosPlantilla.getDeCargoFunDestMae());
                    context.put("NOMBRE_DESTINATARIO",(datosPlantilla.getNombreDestinatario()==null? "":datosPlantilla.getNombreDestinatario()));
                    context.put("DIRECCION_DESTINATARIO",(datosPlantilla.getDireccionDestinatario()==null? "": datosPlantilla.getDireccionDestinatario()));
                    context.put("ENTIDAD_PRIVADA_DESTINATARIO",(datosPlantilla.getEntidadPrivadaDestinatario()==null?"":datosPlantilla.getEntidadPrivadaDestinatario()));
                    context.put("NRO_EXPEDIENTE",(datosPlantilla.getNroExpediente()==null?"":datosPlantilla.getNroExpediente()));
                    context.put("CARGO",(datosPlantilla.getCargo()==null?"":datosPlantilla.getCargo()));
                    
                    context.put("URL_WEB_VERIFICA",(datosPlantilla.getUrlWebVerifica()==null?"":datosPlantilla.getUrlWebVerifica()));
                    context.put("CO_VER_EXT",(datosPlantilla.getCoVerExt()==null?"":datosPlantilla.getCoVerExt()));
                    context.put("NOMBRE_ANIO2",datosPlantilla.getNombreAnio2());
                    
                    context.put("DEPENDENCIA_TITULO",datosPlantilla.getTituloDependencia());
                    context.put("DEPENDENCIA_TITULO_PADRE",datosPlantilla.getTituloDependenciaPadre());
                    
                     ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    report.process(context, baos ); 
                    docObjBean.setDocumento(baos.toByteArray());
                    baos.flush();
                    baos.close();
                }else{
                  docObjBean = null;  
                }

            }
            catch ( Throwable e )
            {
                e.printStackTrace();
            }        
        }else{
           //Plantilla no existe 
        }
        
        //System.out.println( "Generate with " + ( System.currentTimeMillis() - startTime ) + " ms." );
        
        return(docObjBean);
    }
    
    public DocumentoObjBean crearPdfx(String pnuAnn, String pnuEmi, String ptiCap){
        DocumentoObjBean docObjBean = null;
        long startTime = System.currentTimeMillis();
        try
        {

            InputStream in = new FileInputStream(new File("C:\\TDOCUMENTOS\\PLANTILLAS_DOCX\\MEMORANDO3.docx"));
            //InputStream in = DocxNativeLineBreakAndTabWithFreemarker.class.getResourceAsStream( "C:\\TDOCUMENTOS\\PLANTILLAS_DOCX\\MEMORANDO2.docx" );
            IXDocReport report = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker );

            IContext context = report.createContext();

            context.put("TIPO_DOC", "MEMORANDO");
            context.put("NUMERO_DOC", "Nº    -2013-WCC-SG");
            context.put("ASUNTO", "Asunto de Docx");
            context.put("PIE_PAG", "Jr Washington ");
            
            OutputStream out = new FileOutputStream( new File( "C:\\TDOCUMENTOS\\Prueba2.docx" ) );
            report.process( context, out );            
        }
        catch ( Throwable e )
        {
            e.printStackTrace();
        }
        return(docObjBean);
    }
    
    
    public String generarFormatoSiglas(String tipoDoc, String nuDoc, String anio, String institucion, String uuoo){
        tipoDoc = (tipoDoc==null?" ":tipoDoc);
        nuDoc = (nuDoc==null?" ":nuDoc);
        anio = (anio==null?" ":anio);
        institucion = (institucion==null?" ":institucion);
        uuoo = (uuoo==null?" ":uuoo);
        
        String cadenaSiglas = applicationProperties.getFormatoSiglas();
        cadenaSiglas=cadenaSiglas.replace("{TIPODOC}", tipoDoc);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{NUDOC}", nuDoc);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{ANIO}", anio);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{INS}", institucion);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("{UUOO}", uuoo);  //Reemplazamos el tipo de documento
        cadenaSiglas=cadenaSiglas.replace("Â", "");   //Reemplazamos el caracter Â
        cadenaSiglas=cadenaSiglas.replace("â", "");   //Reemplazamos el caracter Â
        return cadenaSiglas;
    }
}
