package pe.gob.onpe.tramitedoc.service.impl;

import java.util.HashMap;
import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.onpe.autentica.model.Usuario;
import pe.gob.onpe.libreria.exception.validarDatoException;
import pe.gob.onpe.tramitedoc.bean.BuscarDocumentoRecepBean;
import pe.gob.onpe.tramitedoc.bean.DependenciaBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.ExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.dao.AnexoDocumentoDao;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.dao.RecepDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.service.AuditoriaMovimientoDocService;
import pe.gob.onpe.tramitedoc.service.RecepDocumentoAdmService;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.Paginacion;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

@Service("recepDocumentoAdmService")
public class RecepDocumentoAdmServiceImp implements RecepDocumentoAdmService{

    @Autowired
    private RecepDocumentoAdmDao recepDocumentoAdmDao;
    
    @Autowired
    private AnexoDocumentoDao anexoDocumentoDao;
    
    @Autowired
    AuditoriaMovimientoDocService audiMovDoc;     

    @Autowired
    private EmiDocumentoAdmDao emiDocumentoAdmDao;    

    
    @Override
    public List<DocumentoBean> getDocumentosRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep, Paginacion paginacion){
        List<DocumentoBean> list = null;
        estadoRecepcionDocumento(buscarDocumentoRecep);
        try{
            list = recepDocumentoAdmDao.getDocumentosRecepAdm(buscarDocumentoRecep,paginacion);
            paginacion.setNumeroTotalRegistros(recepDocumentoAdmDao.getRowDocumentosRecepAdm(buscarDocumentoRecep));
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;
    }    
    
    @Override
    public List<DocumentoBean> getDocumentosBuscaRecepAdm(BuscarDocumentoRecepBean buscarDocumentoRecep){
        List<DocumentoBean> list = null;
        //estadoRecepcionDocumento(buscarDocumentoRecep);
        try{
            list = recepDocumentoAdmDao.getDocumentosBuscaRecepAdm(buscarDocumentoRecep);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;        
    }
    //YUAL
     
    public ProcessResult<List<DocumentoBean>> getDocumentosBuscaRecepAdmLst(BuscarDocumentoRecepBean buscarDocumentoRecep,FiltroPaginate paginate){

        return recepDocumentoAdmDao.getDocumentosBuscaRecepAdmList(buscarDocumentoRecep,paginate);
    }
    //YUAL
    @Override
    public List<DocumentoBean> getResumenRecepAdmList(BuscarDocumentoRecepBean buscarDocumentoRecep){
        List<DocumentoBean> list = null;
        //estadoRecepcionDocumento(buscarDocumentoRecep);
        try{
            list = recepDocumentoAdmDao.getResumenRecepAdmList(buscarDocumentoRecep);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;        
    }
     @Override
    public List<DependenciaBean> getDocumDepenProveido(String codDepen){
        List<DependenciaBean> list = null;
        //estadoRecepcionDocumento(buscarDocumentoRecep);
        try{
            list = recepDocumentoAdmDao.getDocumDepenProveido(codDepen);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;        
    }
    
    @Override
    public BuscarDocumentoRecepBean estadoRecepcionDocumento(BuscarDocumentoRecepBean buscarDocumentoRecepBean,String accion){
        estadoRecepcionDocumento(buscarDocumentoRecepBean);
        return buscarDocumentoRecepBean;
    }
        
    private void estadoRecepcionDocumento(BuscarDocumentoRecepBean buscarDocumentoRecepBean){
     String sEstado = buscarDocumentoRecepBean.getsEstadoDoc();
     if(sEstado.equals("04")){
         buscarDocumentoRecepBean.setsEstadoDoc("0");
         buscarDocumentoRecepBean.setsPrioridadDoc("2");
     }else if(sEstado.equals("01")){
         buscarDocumentoRecepBean.setsEstadoDoc("0");
     }else if(sEstado.equals("07")){
         buscarDocumentoRecepBean.setsEstadoDoc("0");
         buscarDocumentoRecepBean.setsPrioridadDoc("3");
     }
    }
    
    @Override
    public DocumentoBean getDocumentoRecepAdm(String pnuAnn,String pnuEmi,String pnuDes){    
            DocumentoBean documentoBean = null;
        try{
            documentoBean = recepDocumentoAdmDao.getDocumentoRecepAdm(pnuAnn, pnuEmi, pnuDes);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return documentoBean;
    }

    @Override    
    public ExpedienteBean getExpDocumentoRecepAdm(String pnuAnnExp, String pnuSecExp) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            ExpedienteBean expedienteBean = null;
        try{
            expedienteBean = recepDocumentoAdmDao.getExpDocumentoRecepAdm(pnuAnnExp, pnuSecExp);
        }catch(Exception ex){
            ex.printStackTrace();
        }
        return expedienteBean;        
    }

    @Override    
    public List<ReferenciaBean> getDocumentosRefRecepAdm(String pnuAnn, String pnuEmi) {
        List<ReferenciaBean> list = null;
        try{
            list = recepDocumentoAdmDao.getDocumentosRefRecepAdm(pnuAnn,pnuEmi);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;          
    }
    
    @Override
    @Transactional (propagation = Propagation.REQUIRED,rollbackFor=Exception.class) 
    public String updDocumentoRecepAdm(DocumentoBean documentoBean,String accion,String sEsDocAnu,Usuario usuario) throws Exception{
        /*Valores de Accion
        0=Grabar(Archivar,Atender,Cambiar Etiqueta, Grabar Observacion)
        1=Recibir
        2=Anular
        */        
        String vReturn = "NO_OK";
        try{
            //Obtiene el estado actual del documento
            String esDocEmi=recepDocumentoAdmDao.getEstadoDocAdmBasico(documentoBean.getNuAnn(), documentoBean.getNuEmi());
            //Verifica el estado 
            if(esDocEmi!=null&&esDocEmi.trim().length()>0&&!(esDocEmi.equals("5")||esDocEmi.equals("7")||esDocEmi.equals("9"))){
                //Obtener el Documento Destino(TABLA TDTV_DESTNOS)
                DocumentoBean documentoDestinoBean=recepDocumentoAdmDao.getEstadoDocumento(documentoBean.getNuAnn(), documentoBean.getNuEmi(), documentoBean.getNuDes());
                //Verifica si se ha ingresado en la vista datos de archivamiento y Atencion y Cambia la ACCION
                accion = validarEstadoDocumentoRecepcion(documentoBean,accion);//EsDocRec,FeAteDoc,FeArcDoc
                if(accion.equals("2")){//Si la Accion es ANULAR
                vReturn = recepDocumentoAdmDao.validarAnulacionDocRecepcion(documentoBean.getNuAnn(), documentoBean.getNuEmi(), documentoBean.getNuDes());
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("No se puede Anular Recepcion, Documento ya fue Referenciado!");
                }  
            }
                //Obtiene el Tipo de Emisor del Documento
            String sTiEmi = documentoBean.getTiEmi();
            if(accion.equals("1")){//recibir documento
                documentoBean.setEsDocRec("1");//documento Recibido
                    //Verifica que el tipo de emisor sea ingresado por Mesa de Partes, no sea 01=Institucion o 05=Personal
                if(sTiEmi != null && !sTiEmi.equals("") && !sTiEmi.equals("01") && !sTiEmi.equals("05")){
                    //Para el caso de que el documento provenga de Mesa de Partes
                        vReturn = recepDocumentoAdmDao.actualizarGuiaMesaPartes(documentoBean);//Requerido nuAnn, nuEmi, nuDes, esDocRec
                    if ("NO_OK".equals(vReturn)) {
                        throw new validarDatoException("Error al Actualizar Guia Mesa Partes!");
                    }                 
                }                
            //}else if(accion.equals("2") && sEsDocAnu.equals("0")){//grabar documento
                }else if(accion.equals("2")){//Anular Recepcion del documento
                   documentoBean.setEsDocRec("0");//Setea como NO Leido
                   documentoBean.setCoEmpRec("");//Limpia los datos para anular
               documentoBean.setFeRecDoc("");
               documentoBean.setFeAteDoc("");
               documentoBean.setFeArcDoc("");
               documentoBean.setNuCorDes("");
               documentoBean.setCoEtiquetaRec("0");
               documentoBean.setTiFisicoRec("0");
               //YUAL
               String vReturn2="";
               vReturn2=anexoDocumentoDao.delArchivoAnexoDest(documentoBean.getNuAnn(),documentoBean.getNuEmi(),documentoBean.getNuDes());
               //END YUAL
               
               
                   //Verifica que el tipo de emisor sea ingresado por Mesa de Partes, no sea 01=Institucion o 05=Personal
               if(sTiEmi != null && !sTiEmi.equals("") && !sTiEmi.equals("01") && !sTiEmi.equals("05")){
                   //Posible Bloqueo segun el DBA
                       vReturn = recepDocumentoAdmDao.actualizarGuiaMesaPartes(documentoBean);//Requerido nuAnn, nuEmi, nuDes, esDocRec
                   if ("NO_OK".equals(vReturn)) {
                       throw new validarDatoException("Error al Actualizar Guia Mesa Partes!");
                   }                 
               }               
            }

            String sEsDocRec = documentoBean.getEsDocRec();
                String sNuCorDes = documentoBean.getNuCorDes();//NuCorDes=Numero Correlativo de Recepcion
            if((sEsDocRec.equals("1") || sEsDocRec.equals("2") || sEsDocRec.equals("3")) && sNuCorDes.equals("")){
                documentoBean.setNuCorDes(recepDocumentoAdmDao.getNumCorrelativoDestino(documentoBean.getNuAnn(),documentoBean.getCoDepDes()));
            }
            //if(!sEsDocAnu.equals("0")){
                if(!accion.equals("2")){//Verifica que sea para 1=Recibir y 0=Grabar    
                //td_pk_tramite.actualiza_estado(:b_04.nu_ann, :b_04.nu_emi, :b_04.nu_des, :b_04.es_doc_rec);
                    //Actualiza el estado de la tabla Remitos
                    vReturn = recepDocumentoAdmDao.actualizarEstado(documentoBean);//nuAnn, nuEmi, integer nuDes, esDocRec
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al Actualizando Estado!");
                }                
            }
                //Actualiza el TDTV_DESTINOS (accion anular deberia estar en otro metodo)
                vReturn = recepDocumentoAdmDao.updDocumentoBean(documentoBean, accion);//nuAnn, nuEmi, nuDes,FeRecDoc,CoEmpRec,FeAteDoc,FeArcDoc,EsDocRec,CodUseMod,NuCorDes,DeAne(OBServaciones),
            if ("NO_OK".equals(vReturn)) {
                throw new validarDatoException("Error al Actualizar Documento");
            }
                vReturn = recepDocumentoAdmDao.updEtiquetaTipoRecepDocumento(documentoBean);//TiFisicoRec,CoEtiquetaRec,nuAnn, nuEmi, nuDes
            if ("NO_OK".equals(vReturn)) {
                throw new validarDatoException("Error al Actualizar La etiqueta y tipo recepcion del documento");
            }
            documentoBean.setDeEsDocDes(recepDocumentoAdmDao.getDesEstadoDocRecepcion(documentoBean.getEsDocRec()));
                vReturn = audiMovDoc.audiEstadoDocumentoDestino(usuario, documentoDestinoBean.getNuAnn(), documentoDestinoBean.getNuEmi(), documentoDestinoBean.getNuDes(), documentoDestinoBean.getEsDocRec());
            if(!vReturn.equals("OK")){
                vReturn = "Error insertando Auditoria.";
                throw new validarDatoException(vReturn);
            }             
            }else{
                vReturn = "El Documento a cambiado de estado.";
            }
        }catch (validarDatoException e) { 
            throw e; 
         } 
         catch (Exception e) { 
            e.printStackTrace(); 
            throw new validarDatoException("ERROR EN TRANSACCION"); 
         }
       return vReturn;        
    }
    
    private String validarEstadoDocumentoRecepcion(DocumentoBean documentoBean,String accion){
       if(accion.equals("1") || accion.equals("0")){
        if(documentoBean!=null){
            String sEsDocRec = documentoBean.getEsDocRec();
            String sFeAteDoc = documentoBean.getFeAteDoc();
            String sFeArcDoc = documentoBean.getFeArcDoc();
            if(sFeAteDoc!=null && !sFeAteDoc.equals("")){
             //Verifica si estado documento no es Archivado   
             if(sEsDocRec!=null && !sEsDocRec.equals("") && !sEsDocRec.equals("3")){
                 //Setea el estado del documento en Atendido
                 documentoBean.setEsDocRec("2");
                 //Setea la accion en Grabar
                 accion="0";
             }
            }
            if(sFeArcDoc!=null && !sFeArcDoc.equals("")){
             //Setea el estado del documento en Archivado   
             documentoBean.setEsDocRec("3");  
             //Setea el estado del documento en Grabar
             accion="0";
            }
        }           
       }
       return accion;
    }
    
    @Override
    public HashMap getDocumentosEnReferencia(BuscarDocumentoRecepBean buscarDocumentoRecepBean){
        List<DocumentoBean> list = null;
        DocumentoBean documentoBean = null;
        HashMap mp = new HashMap();
        try{
            documentoBean = recepDocumentoAdmDao.existeDocumentoReferenciado(buscarDocumentoRecepBean);
            if(documentoBean!=null && documentoBean.getMsjResult()!=null && documentoBean.getMsjResult().equals("OK")){
                documentoBean.setCoDepDes(buscarDocumentoRecepBean.getsCoDependencia());
                documentoBean.setCoEmpDes(buscarDocumentoRecepBean.getsCoEmpleado());                
                list = recepDocumentoAdmDao.getDocumentosReferenciadoBusq(documentoBean,buscarDocumentoRecepBean.getsTiAcceso());                
                mp.put("recepDocumAdmList",list);
                mp.put("msjResult", "1");//Existe Documento
            }else{
                mp.put("msjResult", "0");//NO existe Documento
            }
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return mp;           
    }    

    @Override    
    public String getVerificaAtendido(String pnuAnn, String pnuEmi, String pnuDes) {
        String vResult="0";
        try{
            vResult = recepDocumentoAdmDao.getVerificaAtendido(pnuAnn, pnuEmi, pnuDes);
        }catch(Exception ex){
            vResult = "0";
            ex.printStackTrace();
        }

        return vResult;          
    }

        
    @Override    
    public List<ReferenciaBean> getConsultaAtendido(String pnuAnn, String pnuEmi, String pnuDes) {
        List<ReferenciaBean> list = null;
        try{
            list = recepDocumentoAdmDao.getConsultaAtendido(pnuAnn, pnuEmi, pnuDes);
        }catch(Exception ex){
            ex.printStackTrace();
        }

        return list;          
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)//Nuevo
    public String updDocumentoRecepAdm(DocumentoBean documentoBean, String accion, Usuario usuario) throws Exception {
        String vReturn = "NO_OK";
        try {
            //Obtiene el estado actual del documento
            DocumentoEmiBean documentoEmiBean = emiDocumentoAdmDao.getEstadoDocumento(documentoBean.getNuAnn(), documentoBean.getNuEmi());
            //Verifica el estado 
            if (documentoEmiBean != null && documentoEmiBean.getEsDocEmi() != null && documentoEmiBean.getEsDocEmi().trim().length() > 0
                    && !(documentoEmiBean.getEsDocEmi().equals("5") || documentoEmiBean.getEsDocEmi().equals("7") || documentoEmiBean.getEsDocEmi().equals("9"))) {
                //Obtener el Datos del Documento de Destino antes de cambiar de estado
                DocumentoBean documentoDestinoBean = recepDocumentoAdmDao.getEstadoDocumento(documentoBean.getNuAnn(), documentoBean.getNuEmi(), documentoBean.getNuDes());
                //Verifica si se ha ingresado en la vista datos de archivamiento y AtenciÃ³n y Cambia la ACCION
                accion = validarEstadoDocumentoRecepcion(documentoBean, accion);//EsDocRec,FeAteDoc,FeArcDoc                

                String msgActEstadoRemito = "";
                String msgActEstadoDestino = "";
                if (accion.equals("0")) {//GRABAR CAMBIOS
                    //Si se recibe, y atiende y/o recibe a la vez se debe 
//                    if (documentoDestinoBean.getEsDocRec() != null && documentoDestinoBean.getEsDocRec().equals("0")) {
//                        documentoBean.setNuCorDes(recepDocumentoAdmDao.getNumCorrelativoDestino(documentoBean.getNuAnn(), documentoDestinoBean.getCoDepDes()));
//                    }
                    msgActEstadoRemito = recepDocumentoAdmDao.actualizarEstado(documentoBean);//nuAnn, nuEmi, integer nuDes, esDocRec
                    msgActEstadoDestino = recepDocumentoAdmDao.updAtencionDocumentoBean(documentoBean);
                } else if (accion.equals("1")) {//RECIBIR DOCUMENTO
                    //Verificar si el estado de la recepci+on no ha sido cambiado desde otro proceso
                    if(documentoDestinoBean.getEsDocRec().equals("1")){
                        throw new validarDatoException("El estado del documento ya es recibido.");
                    }
                    documentoBean.setEsDocRec("1");//documento Recibido
                    //Verifica si no tiene correlativo de recepcion y se le asigna                    
                    documentoBean.setNuCorDes(recepDocumentoAdmDao.getNumCorrelativoDestino(documentoBean.getNuAnn(), documentoDestinoBean.getCoDepDes()));
                    msgActEstadoRemito = recepDocumentoAdmDao.actualizarEstado(documentoBean);//nuAnn, nuEmi, integer nuDes, esDocRec
                    msgActEstadoDestino = recepDocumentoAdmDao.updRecepcionDocumentoBean(documentoBean);
                } else if (accion.equals("2")) {//ANULAR RECEPCION DE DOCUMENTO
                    if(documentoDestinoBean.getEsDocRec().equals("0")){
                        throw new validarDatoException("No se puede anular la recepción, debido a que el estado del documento es No Leído.");
                    }
                    documentoBean.setEsDocRec("0");//Anulado para uso e el caso de mesa de partes
                    documentoBean.setCoEtiquetaRec("0");//Limpiar la etiqueta en cero para su actualizacion |updEtiquetaTipoRecepDocumento
                    documentoBean.setTiFisicoRec("0");//Limpiar el tipo fisico en cero para su actualizacion |updEtiquetaTipoRecepDocumento
                    this.changeToAnulado(documentoBean.getNuAnn(), documentoBean.getNuEmi(), documentoBean.getNuDes(), usuario.getCoUsuario());
                }
                if ("NO_OK".equals(msgActEstadoDestino)) {
                    throw new validarDatoException("Error al Actualizar Documento");
                }
                //Verifica que no exista un error al actualizar el estado de remitos
                if ("NO_OK".equals(msgActEstadoRemito)) {
                    throw new validarDatoException("Error al Actualizando Estado!");
                }
                //Verificar si es documento de mesa de partes
                if (accion.equals("1") || accion.equals("2")) {
                    if (documentoEmiBean.getTiEmi() != null && !documentoEmiBean.getTiEmi().trim().equals("") && !documentoEmiBean.getTiEmi().equals("01") && !documentoEmiBean.getTiEmi().equals("05")) {
                        //Posible Bloqueo segun el DBA(**)
                        vReturn = recepDocumentoAdmDao.actualizarGuiaMesaPartes(documentoBean);//Requerido nuAnn, nuEmi, nuDes, esDocRec
                        if ("NO_OK".equals(vReturn)) {
                            throw new validarDatoException("Error al Actualizar Guia Mesa Partes!");
                        }
                    }
                }
                //Grabar etiquetas seleccionadas en todos los casos
                vReturn = recepDocumentoAdmDao.updEtiquetaTipoRecepDocumento(documentoBean);//TiFisicoRec,CoEtiquetaRec,nuAnn, nuEmi, nuDes
                if ("NO_OK".equals(vReturn)) {
                    throw new validarDatoException("Error al Actualizar La etiqueta y tipo recepcion del documento");
                }
                //Grabar Auditoria
                documentoBean.setDeEsDocDes(recepDocumentoAdmDao.getDesEstadoDocRecepcion(documentoBean.getEsDocRec()));
                vReturn = audiMovDoc.audiEstadoDocumentoDestino(usuario, documentoDestinoBean.getNuAnn(), documentoDestinoBean.getNuEmi(), documentoDestinoBean.getNuDes(), documentoDestinoBean.getEsDocRec());
                if (!vReturn.equals("OK")) {
                    vReturn = "Error insertando Auditoria.";
                    throw new validarDatoException(vReturn);
                }
            }
        } catch (validarDatoException e) {
            throw e;
        } catch (Exception e) {
            e.printStackTrace();
            throw new validarDatoException("ERROR EN TRANSACCION");
        }
        return vReturn;
    }

    @Override
    public String changeToAnulado(String nuAnn, String nuEmi, String nuDes, String coUseMod) throws Exception {
        String vReturn = recepDocumentoAdmDao.validarAnulacionDocRecepcion(nuAnn, nuEmi, nuDes);
        if ("NO_OK".equals(vReturn)) {
            throw new validarDatoException("No se puede Anular Recepcion, Documento ya fue Referenciado!");
        } else {
            vReturn = recepDocumentoAdmDao.updAnulaRecepecionDocumentoBean(nuAnn, nuEmi, nuDes, coUseMod);
            if ("NO_OK".equals(vReturn)) {
                throw new validarDatoException("Error al Actualizar Documento");
            }
        }
        return vReturn;
    }    

}
