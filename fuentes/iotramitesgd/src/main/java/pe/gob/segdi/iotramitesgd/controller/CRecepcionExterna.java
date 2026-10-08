package pe.gob.segdi.iotramitesgd.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.jboss.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.BeanUtils;
import pe.gob.segdi.iotramitesgd.bean.*;
import pe.gob.segdi.iotramitesgd.service.*;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;
import pe.gob.segdi.srvciotramite.main.WSPideTramite;
import pe.gob.segdi.srvcsunat.bean.RucBean;
import pe.gob.segdi.srvcsunat.main.EndPoint;
import pe.gob.segdi.wsiopidetramite.ws.CargoTramite;
import pe.gob.segdi.wsiopidetramite.ws.IoTipoDocumentoTramite;
import pe.gob.segdi.wsiopidetramite.ws.RespuestaCargoTramite;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.servlet.http.HttpServletRequest;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.*;


/**
 * Objeto : CRecepcionExterna.java.
 * Descripción : Clase de recepción de documentos , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@ManagedBean("cRecepcionExterna")
@RequestScoped
public class CRecepcionExterna extends BaseController {

    private static Logger depurador = Logger.getLogger(CRecepcionExterna.class.getName());
    private static final long serialVersionUID = 1L;
    private ConsultaDocumentoRecepcion consultaDocumento;
    private IODocumentoAnexo documentoAnexo;
    private DocumentoCargo documentoCargo;
    private UploadedFile fileAnexo, fileCargo;

    private List<IoTipoDocumentoTramite> lstTipDocTramite;
    private List<ConsultaDocumentoRecepcion> lstConsultaDocumento;
    private List<IOEmisionExterna> lstEmiExt;
    private List<IODocumentoAnexo> lstDocAnexo;
    private List<IODocumentoAnexo> lstDocAnexo2;
    private List<DocumentoCargo> lstDocCargo;
    private List<SelectItem> entidades;
    private List<CboTramite> cboOficina;
    private List<CboTramite> cboResponsableOficina;
    private List<Maestro> cboDependencia;
    private List<Maestro> cbotipdocmv;

    private int contaanex = 0;
    private int sidcat;
    private int sidrecext;
    private String dfecregstd;
    private String vrucentemi;
    private String cflgest;
    private String nomDocumento;
    private String argumento;
    private String desde;
    private String codependencia;
    private String co_par;
    private String documentName;
    private Date dfechasta;
    private Date dfecdesde;

    //@ManagedProperty(value = "#{iRecepcionExternaService}")
    @Inject
    private IRecepcionExternaService iRecepcionExternaService;
    //@ManagedProperty(value = "#{iDocumentoAnexoService}")
    @Inject
    private IDocumentoAnexoService iDocumentoAnexoService;
    //@ManagedProperty(value = "#{iTramiteService}")
    @Inject
    private ITramiteService iTramiteService;
    //@ManagedProperty(value = "#{iRemitenteService}")
    @Inject
    private IRemitenteService iRemitenteService;
    //@ManagedProperty(value = "#{iMaestroService}")
    @Inject
    private IMaestroService iMaestroService;
    @Inject
    private CVariableSession cVariableSession;
    private byte[] bcarstd;

    @PostConstruct
    public void init() {

        try {

            cboOficina = iTramiteService.getTramiteOficinas();
            cboDependencia = iMaestroService.getDependencia();

//            CVariableSession obj = (CVariableSession) findBean("cVariableSession");
//            co_par = obj.getEstMesaVirtual();
            co_par = cVariableSession.getEstMesaVirtual();
            depurador.info("co_par =========== " + co_par);
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    @Override
    public String inicializarControladora() {
        inicializarVariable();
        return "recepcionExterna";
    }

    public String inicializarControladora(String cflgest) {
        try {
            this.cflgest = cflgest;
            codependencia = "-1";
            vrucentemi = "";
            bsqDocumentoExterno();
            cbotipdocmv = iMaestroService.getTipoDocumentoMV();
        } catch (Exception e) {
            depurador.error(null, e);
        }
        return "recepcionExterna";
    }

    @Override
    public void inicializarVariable() {
        try {

            consultaDocumento = new ConsultaDocumentoRecepcion();
            lstDocAnexo = new ArrayList<IODocumentoAnexo>();
            lstDocAnexo2 = new ArrayList<IODocumentoAnexo>();
            lstDocCargo = new ArrayList<DocumentoCargo>();
            documentoAnexo = new IODocumentoAnexo();
            documentoCargo = new DocumentoCargo();
            cboDependencia = new ArrayList<Maestro>();

            documentName = "";
            sidrecext = 0;
            bcarstd = null;
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    @Override
    public void limpiarObjectos() {
        cboResponsableOficina = null;
        fileCargo = null;
        lstDocAnexo2 = new ArrayList<IODocumentoAnexo>();
        lstDocCargo = new ArrayList<DocumentoCargo>();
    }

    public void detalletRecepcionExterna() {
        ////depurador.info("detRecepcionExterna ==>");
        try {
            lstDocAnexo = iDocumentoAnexoService.getListaDocumentoAnexo(consultaDocumento.getSiddocext());

            if (consultaDocumento.getBcarstd() != null && (consultaDocumento.getCflgest().equals("R") || consultaDocumento.getCflgest().equals("O"))) {
                Date trialTime = new Date();
                InputStream in = new ByteArrayInputStream(consultaDocumento.getBcarstd());
                CVariableSession obj = (CVariableSession) findBean("cVariableSession");
                obj.setDocumentoPrincipalCargo(new DefaultStreamedContent(in, "application/pdf", trialTime.getTime() + ".pdf"));
            } else if (consultaDocumento.getBcarstd() != null && consultaDocumento.getCflgest().equals("P")) {
                CVariableSession obj = (CVariableSession) findBean("cVariableSession");
                obj.setDocumentoPrincipalCargo(null);
            }

            consultaDocumento = iRecepcionExternaService.getRecepcionExterna(consultaDocumento.getSidrecext());
            addCallbackComponent("PF('detalleTramiteDialogWidget').show()");

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }


    public void getResponsableOficina() {
        ////depurador.info("getResponsableOficina ==>"+consultaDocumento.getiCodOficina());
        try {
            String[] responsable = iTramiteService.getResponsableOficina(consultaDocumento.getiCodOficina());
            consultaDocumento.setiCodTrabajador(responsable[0]);
            consultaDocumento.setcNombresTrabajador(responsable[1]);
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public String bsqDocumentoExterno() {
        depurador.info("bsqDocumentoExterno ==>");

        try {
            String fecdesde = "";
            String fechasta = "";
            String msj = validarFormularioBusqueda();
            if (msj.equals("")) {
                if (this.dfecdesde != null) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    fecdesde = format.format(dfecdesde);
                    fechasta = format.format(dfechasta);
                }

                lstConsultaDocumento = iRecepcionExternaService.bsqRecepcionExterna(vrucentemi, cflgest, sidrecext, fecdesde, fechasta);

                //				if(lstConsultaDocumento.size() == 0) {
//					addInfoMessage("Mensaje", Utilitarios.ObtenerDatosProperties("MessageResources", "MENSAJE_BUSQUEDA"));
//				}
            } else {
                addInfoMessage("Error", msj);
            }
        } catch (Exception e) {
            depurador.error(null, e);
        }
        return "recepcionExterna";
    }

    public void documentoPrincipal() {
        ////depurador.info("documentoPrincipal ==>");
        try {
            if (consultaDocumento.getBpdfdoc() != null) {
                Date trialTime = new Date();
                InputStream in = new ByteArrayInputStream(consultaDocumento.getBpdfdoc());
                CVariableSession obj = (CVariableSession) findBean("cVariableSession");
                obj.setDocumentoPrincipal(new DefaultStreamedContent(in, "application/pdf", trialTime.getTime() + ".pdf"));
            }
            addCallbackComponent("PF('recepcionExternaDocumentoPrincipalDialogWidget').show()");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void fileUploadAnexo(FileUploadEvent event) throws IOException {
        depurador.info("fileUploadAnexo 0 ==>");

        fileAnexo = event.getFile();
        depurador.info("fileUploadAnexo NAME ==>" + fileAnexo.getFileName());
        documentoAnexo = new IODocumentoAnexo();
        documentoAnexo.setVnomdoc(fileAnexo.getFileName());
        documentoAnexo.setBdocanx(fileAnexo.getContents());
        lstDocAnexo2.add(documentoAnexo);
        contaanex++;


    }

    public void eliminarDocumentoCargo() {
        ////depurador.info("eliminarDocumentoCargo ==>");
        fileCargo = null;
        lstDocCargo.remove(documentoCargo);

    }

    public void eliminarAnexo() {
        //depurador.info("eliminarAnexo ==>"+documentoAnexo.getVnomdoc());
        fileAnexo = null;
        lstDocAnexo2.remove(documentoAnexo);

    }


    public String validarFormularioBusqueda() {
        if ((cflgest != null && cflgest.equals("-1"))) {
            return "Debe seleccionar el estado ";
        } else if ((dfecdesde != null && dfechasta == null) || (dfecdesde == null && dfechasta != null)) {
            return "Ingresar la fecha de inicio y fin ";
        }
        return "";
    }

    public String validarFormularioCargo() {
        if ((consultaDocumento.getiCodOficina().equals("-1"))) {
            return "Debe seleccionar la unidad orgánica ";
        } else if (consultaDocumento.getiCodTrabajador().equals("-1")) {
            return "Debe seleccionar el trabajador responsable ";
        } else if (bcarstd == null || bcarstd.length == 0) {
            return "Debe firmar el cargo de recepción ";
        } else if (consultaDocumento.getVobs().trim().length() == 0) {
            if (consultaDocumento.getSnumanx() != lstDocAnexo2.size()) {
                return "El número de documentos anexos cargados no corresponde con la cantidad de anexos especificados para el documento";
            }
        }
        return "";
    }

    public void formCargoRecepcionExterna1() {
        //depurador.info("formCargoRecepcionExterna1 ==>");
        try {
            lstDocAnexo = iDocumentoAnexoService.getListaDocumentoAnexo(consultaDocumento.getSiddocext());
            String ruc = "";
            ruc = iRemitenteService.getCodigoRemitente(consultaDocumento.getVrucentrem());

            if (ruc.equals("")) {
                EndPoint endPoint = new EndPoint();

                RucBean rucBean = endPoint.getRuc(consultaDocumento.getVrucentrem());
                Remitente remitente = new Remitente();
                remitente.setCproruc(consultaDocumento.getVrucentrem());
                remitente.setCprorazsoc(rucBean.getDdp_nombre());
                remitente.setcDireccion(rucBean.getDesc_tipvia() + " " + rucBean.getDdp_nomvia() + " " + rucBean.getDdp_numer1());

                ruc = iRemitenteService.insRemitente(remitente);
                addInfoMessage("Informe", "Se registró el RUC de la entidad emisora en el STD");
            }

            consultaDocumento.setiCodRemitente(ruc);
            consultaDocumento = iRecepcionExternaService.getRecepcionExterna(consultaDocumento.getSidrecext());
            addCallbackComponent("PF('cargoTramiteDialogWidget1').show()");

            lstDocAnexo2 = new ArrayList<IODocumentoAnexo>();


        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void recepcionDocumento() {
        ////depurador.info("recepcionDocumento ==>");
        addCallbackComponent("PF('cargoTramiteDialogWidget').hide()");
        addCallbackComponent("PF('recepcionDocumentoDialogWidget').show()");
    }

    public void recepcionarDocumento() {
        ////depurador.info("recepcionarDocumentoSTD ==>"+consultaDocumento.getCcodtipdoc());

        boolean flagEnvioExitoso = true;
        try {
            int codigo = consultaDocumento.getSidrecext();
            String filePath = "";
            if (Utilitarios.ObtenerDatosProperties("Parametros", "P_PC").equals("S")) {
                //filePath = System.getProperty("java.io.tmpdir") + File.separator + Utilitarios.ObtenerDatosProperties("Parametros", "P_UPLOAD_DIRECTORY") + File.separator + codigo+".pdf";
                filePath = Utilitarios.ObtenerDatosProperties("Parametros", "P_TEMP_DIRECTORY") + File.separator + Utilitarios.ObtenerDatosProperties("Parametros", "P_UPLOAD_DIRECTORY") + File.separator + codigo + ".pdf";
            } else {
                filePath = File.separator + Utilitarios.ObtenerDatosProperties("Parametros", "P_TEMP_DIRECTORY") + File.separator + Utilitarios.ObtenerDatosProperties("Parametros", "P_UPLOAD_DIRECTORY") + File.separator + codigo + ".pdf";
            }

            try {
                Path path = Paths.get(filePath);
                bcarstd = Files.readAllBytes(path);

            } catch (Exception e) {
                if (bcarstd != null) {
                    File fileDelete = new File(filePath);
                    fileDelete.delete();
                }
                bcarstd = null;
            }


            String msj = validarFormularioCargo();
            if (msj.equals("")) {

                Expediente expediente = new Expediente();
                DatosUsuario datosUsuario = new DatosUsuario();
                datosUsuario = (DatosUsuario) getAttributeSession("datosUsuario");
                DatosUsuarioConfiguracion datosUsuarioConfiguracion = new DatosUsuarioConfiguracion();
                datosUsuarioConfiguracion = (DatosUsuarioConfiguracion) getAttributeSession("datosConfiguracionUsuario");
                consultaDocumento.setLstDocAnexo(lstDocAnexo2);

                String cNomOficina = "";
                for (int i = 0; i < cboOficina.size(); i++) {
                    if (cboOficina.get(i).getcCodOficina().equals(consultaDocumento.getiCodOficina())) {
                        cNomOficina = cboOficina.get(i).getcNomOficina();
                        break;
                    }
                }

                consultaDocumento.setcNomOficina(cNomOficina);
                consultaDocumento.setBcarstd(bcarstd);
                iTramiteService.recepcionarDocumento(consultaDocumento, expediente, datosUsuario, datosUsuarioConfiguracion);
                consultaDocumento.setCflgest(consultaDocumento.getVobs().trim().equals("") ? Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_RECEPCION") : Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_OBSERVADO"));
                sidrecext = consultaDocumento.getSidrecext();


                addCallbackComponent("PF('cargoTramiteDialogWidget1').hide()");
                addInfoMessage("Mensaje", "Registro exitoso");
                enviarCargo();
                bsqDocumentoExterno();
                inicializarVariable();
            } else {
                addInfoMessage("Mensaje", msj);
            }


        } catch (Exception e) {
            addErrorMessage("Error", "No se pudo realizar la recepción del documento");
            flagEnvioExitoso = false;
            addCallbackComponent("PF('cargoTramiteDialogWidget1').hide()");
            depurador.error(null, e);
        }
    }

    public void enviarCargo() {
        ////depurador.info("enviarCargo ==>");
        co_par = cVariableSession.getEstMesaVirtual();
        depurador.info("co_par =========== " + co_par);
        try {
            CargoTramite cargoTramite = new CargoTramite();
            //.copyProperties(cargoTramite, consultaDocumento);
            BeanUtils.copyProperties(consultaDocumento, cargoTramite);

            cargoTramite.setVnumregstd(consultaDocumento.getVnumregstd());
            cargoTramite.setVanioregstd(consultaDocumento.getVanioregstd());
            cargoTramite.setVrucentrem(Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA"));
            cargoTramite.setVrucentrec(consultaDocumento.getVrucentrem());
            cargoTramite.setVusuregstd(consultaDocumento.getVusuregstd());
            cargoTramite.setVuniorgstd(consultaDocumento.getVuniorgstd());
            cargoTramite.setBcarstd(Base64.getEncoder().encode(bcarstd));
            cargoTramite.setVobs(consultaDocumento.getVobs());
            cargoTramite.setCflgest(consultaDocumento.getCflgest());
            GregorianCalendar gregory = new GregorianCalendar();
            gregory.setTime(gregory.getTime());
            DatatypeFactory df = DatatypeFactory.newInstance();
            XMLGregorianCalendar dfecdoc = df.newXMLGregorianCalendar(gregory);
            cargoTramite.setDfecregstd(dfecdoc);

            WSPideTramite wsPideTramite = new WSPideTramite();
            RespuestaCargoTramite respuestaCargoTramite = new RespuestaCargoTramite();
            respuestaCargoTramite = wsPideTramite.cargoTramite(cargoTramite, co_par);

            if (respuestaCargoTramite.getVcodres().equals("0000")) {
                addInfoMessage("Exito", "Se recepcionó el documento con éxito");
                iRecepcionExternaService.updRecepcionExternaEnvioCargo(consultaDocumento.getSidrecext());
            } else {
                addErrorMessage("Error", respuestaCargoTramite.getVdesres());
            }

        } catch (Exception e) {
            depurador.error(null, e);
            addErrorMessage("Error", "No se pudo obtener la confirmación de recepción del cargo");
        }
    }

    public void getArgumentos() {
        //depurador.info("obtenerNombreDocumento ==>");
        try {
            HttpServletRequest origRequest = (HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest();
            String pathServlet = origRequest.getServletPath();
            String fullPathServlet = origRequest.getRequestURL().toString();
            int resInt = fullPathServlet.length() - pathServlet.length();
            String serverPath = fullPathServlet.substring(0, resInt + 1);
            //depurador.info("pathServlet ==>"+pathServlet);
            //depurador.info("fullPathServlet ==>"+fullPathServlet);
            //depurador.info("serverPath ==>"+serverPath);

            String protocol = "";
            if (serverPath.contains("https://")) {
                protocol = "S";
            } else {
                protocol = "T";
            }

            Date trialTime = new Date();
            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(trialTime.getTime());
            final SimpleDateFormat sdfParser = new SimpleDateFormat("YYYYmmddHHmmss");

            documentName = sdfParser.format(trialTime.getTime()) + ".pdf";
            //depurador.info(getPath("/documents/")+File.separator+documentName);
            File file2 = new File(getPath("/documents/") + File.separator + documentName);

            BufferedOutputStream writer;
            writer = new BufferedOutputStream(new FileOutputStream(file2));
            writer.write(consultaDocumento.getBpdfdoc());
            writer.flush();
            writer.close();

            DatosUsuario datosUsuario = new DatosUsuario();
            datosUsuario = (DatosUsuario) getAttributeSession("datosUsuario");
            argumento = paramWeb(protocol, serverPath, datosUsuario.getCempNuDni());


            addCallbackParam("parametro", argumento);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public String paramWeb(String protocol, String serverPath, String dni) {
        //System.out.println("paramWeb ==>"+dni);
        String param = "";
        ObjectMapper mapper = new ObjectMapper();
        //Java to JSON
        try {
            Map<String, String> map = new HashMap<>();
            map.put("app", "pdf");
            map.put("clientId", Utilitarios.ObtenerDatosProperties("Parametros", "P_CLIENT_ID"));
            map.put("clientSecret", Utilitarios.ObtenerDatosProperties("Parametros", "P_CLIENT_SECRET"));
            map.put("idFile", "001"); //FieldName Multipart, al momento de recibir el archivo subido se puede utilizar este argumento como identificador.
            map.put("type", "W"); //L=Documento está en la PC , W=Documento está en la Web.
            map.put("protocol", protocol); //T=http, S=https (SE RECOMIENDA HTTPS)
            map.put("fileDownloadUrl", serverPath + "documents/" + documentName);
            map.put("fileDownloadLogoUrl", serverPath + "resources/img/iLogo1.png");
            map.put("fileDownloadStampUrl", serverPath + "resources/img/iFirma1.jpg");
            map.put("fileUploadUrl", serverPath + "uploadServlet");
            map.put("contentFile", "cargo.pdf");
            map.put("reason", "Soy el autor del documento");
            map.put("isSignatureVisible", "true");
            map.put("stampAppearanceId", "0"); //0:(sello+descripcion) horizontal, 1:(sello+descripcion) vertical, 2:solo sello, 3:solo descripcion
            map.put("pageNumber", "0");
            map.put("posx", "350");
            map.put("posy", "150");
            //map.put("width", "170");         
            map.put("fontSize", "7");
            map.put("dcfilter", ".*FIR.*|.*FAU.*"); //".*" todos, solo firma ".*FIR.*|.*FAU.*"
            map.put("timestamp", "false");
            map.put("outputFile", documentName);
            map.put("maxFileSize", Utilitarios.ObtenerDatosProperties("Parametros", "P_MAX_FILE_SIZE")); //Por defecto será 5242880 5MB - Maximo 100MB
            //JSON
            param = mapper.writeValueAsString(map);

            System.out.println("PARAM ==>" + param);

            //Base64 (JAVA 8)
            param = Base64.getEncoder().encodeToString(param.getBytes(StandardCharsets.UTF_8));

        } catch (JsonProcessingException ex) {
        }

        return param;
    }

    public void reenviarCargo() {
        ////depurador.info("enviarCargo ==>");
        try {
            consultaDocumento = iRecepcionExternaService.getRecepcionExterna(consultaDocumento.getSidrecext());
            CargoTramite cargoTramite = new CargoTramite();
            BeanUtils.copyProperties(cargoTramite, consultaDocumento);

            cargoTramite.setVnumregstd(consultaDocumento.getVnumregstd());
            cargoTramite.setVanioregstd(consultaDocumento.getVanioregstd());
            cargoTramite.setVrucentrem(Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA"));
            cargoTramite.setVrucentrec(consultaDocumento.getVrucentrem());
            cargoTramite.setVusuregstd(consultaDocumento.getVusuregstd());
            cargoTramite.setVuniorgstd(consultaDocumento.getVuniorgstd());
            cargoTramite.setBcarstd(Base64.getEncoder().encode(consultaDocumento.getBcarstd()));
            cargoTramite.setVobs(consultaDocumento.getVobs());
            cargoTramite.setCflgest(consultaDocumento.getCflgest());
            GregorianCalendar gregory = new GregorianCalendar();
            gregory.setTime(gregory.getTime());
            DatatypeFactory df = DatatypeFactory.newInstance();
            XMLGregorianCalendar dfecdoc = df.newXMLGregorianCalendar(gregory);
            cargoTramite.setDfecregstd(dfecdoc);

            WSPideTramite wsPideTramite = new WSPideTramite();
            RespuestaCargoTramite respuestaCargoTramite = new RespuestaCargoTramite();
            respuestaCargoTramite = wsPideTramite.cargoTramite(cargoTramite, co_par);

            if (respuestaCargoTramite.getVcodres().equals("0000")) {
                addInfoMessage("Exito", "Se envió el cargo del documento");
                iRecepcionExternaService.updRecepcionExternaEnvioCargo(consultaDocumento.getSidrecext());
                bsqDocumentoExterno();
            } else if (respuestaCargoTramite.getVcodres().equals("0002")) {
                iRecepcionExternaService.updRecepcionExternaEnvioCargo(consultaDocumento.getSidrecext());
                bsqDocumentoExterno();
                addInfoMessage("Info", respuestaCargoTramite.getVdesres());
            } else {
                addErrorMessage("Error", respuestaCargoTramite.getVdesres());
            }

        } catch (Exception e) {
            depurador.error(null, e);
            addErrorMessage("Error", "No se pudo obtener la confirmación de recepción del cargo");
        }
    }


    public void cargoDocumento() {
        //depurador.info(":::::::::::::::::::::::::::::::cargoDocumento::::::::::::::::::::::::::::::::");
        try {
            if (consultaDocumento.getBcarstd() != null) {
                InputStream in = new ByteArrayInputStream(consultaDocumento.getBcarstd());
                CVariableSession obj = (CVariableSession) findBean("cVariableSession");
                obj.setDocumentoPrincipalCargo(new DefaultStreamedContent(in, "application/pdf", consultaDocumento.getVnomdoc()));
            }
            addCallbackComponent("PF('cargoDialogWidget').show()");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void fileUploadDocumentoPrincipal(FileUploadEvent event) throws IOException {
        depurador.info("fileUploadCargo ==>" + consultaDocumento.getSiddocext());
        try {
            fileCargo = event.getFile();
            iRecepcionExternaService.fileUploadDocumentoPrincipal(consultaDocumento.getSiddocext(), fileCargo.getContents());
            fileCargo = null;
            this.consultaDocumento = iRecepcionExternaService.getRecepcionExterna(consultaDocumento.getSidrecext());
            addInfoMessage("Mensaje", "El documento se actualizó satisfactoriamente");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    /*
     * ********************************************************************/
    public ConsultaDocumentoRecepcion getConsultaDocumento() {
        return consultaDocumento;
    }

    public void setConsultaDocumento(ConsultaDocumentoRecepcion consultaDocumento) {
        this.consultaDocumento = consultaDocumento;
    }

    public UploadedFile getFileAnexo() {
        return fileAnexo;
    }

    public void setFileAnexo(UploadedFile fileAnexo) {
        this.fileAnexo = fileAnexo;
    }

    public int getSidcat() {
        return sidcat;
    }

    public void setSidcat(int sidcat) {
        this.sidcat = sidcat;
    }

    public List<SelectItem> getEntidades() {
        return entidades;
    }

    public void setEntidades(List<SelectItem> entidades) {
        this.entidades = entidades;
    }

    public List<IoTipoDocumentoTramite> getLstTipDocTramite() {
        return lstTipDocTramite;
    }

    public void setLstTipDocTramite(List<IoTipoDocumentoTramite> lstTipDocTramite) {
        this.lstTipDocTramite = lstTipDocTramite;
    }

    public String getVrucentemi() {
        return vrucentemi;
    }

    public void setVrucentemi(String vrucentemi) {
        this.vrucentemi = vrucentemi;
    }

    public List<IOEmisionExterna> getLstEmiExt() {
        return lstEmiExt;
    }

    public void setLstEmiExt(List<IOEmisionExterna> lstEmiExt) {
        this.lstEmiExt = lstEmiExt;
    }

//    public IRecepcionExternaService getiRecepcionExternaService() {
//        return iRecepcionExternaService;
//    }
//
//    public void setiRecepcionExternaService(IRecepcionExternaService iRecepcionExternaService) {
//        this.iRecepcionExternaService = iRecepcionExternaService;
//    }

    public List<IODocumentoAnexo> getLstDocAnexo() {
        return lstDocAnexo;
    }

    public List<ConsultaDocumentoRecepcion> getLstConsultaDocumento() {
        return lstConsultaDocumento;
    }

    public void setLstConsultaDocumento(List<ConsultaDocumentoRecepcion> lstConsultaDocumento) {
        this.lstConsultaDocumento = lstConsultaDocumento;
    }

//    public IDocumentoAnexoService getiDocumentoAnexoService() {
//        return iDocumentoAnexoService;
//    }
//
//    public void setiDocumentoAnexoService(IDocumentoAnexoService iDocumentoAnexoService) {
//        this.iDocumentoAnexoService = iDocumentoAnexoService;
//    }

    public String getDfecregstd() {
        return dfecregstd;
    }

    public void setDfecregstd(String dfecregstd) {
        this.dfecregstd = dfecregstd;
    }

    public String getCflgest() {
        return cflgest;
    }

    public void setCflgest(String cflgest) {
        this.cflgest = cflgest;
    }

    public UploadedFile getFileCargo() {
        return fileCargo;
    }

    public void setFileCargo(UploadedFile fileCargo) {
        this.fileCargo = fileCargo;
    }

    public List<CboTramite> getCboOficina() {
        return cboOficina;
    }

    public void setCboOficina(List<CboTramite> cboOficina) {
        this.cboOficina = cboOficina;
    }

    public List<CboTramite> getCboResponsableOficina() {
        return cboResponsableOficina;
    }

    public void setCboResponsableOficina(List<CboTramite> cboResponsableOficina) {
        this.cboResponsableOficina = cboResponsableOficina;
    }

//    public ITramiteService getiTramiteService() {
//        return iTramiteService;
//    }
//
//    public void setiTramiteService(ITramiteService iTramiteService) {
//        this.iTramiteService = iTramiteService;
//    }

    public List<IODocumentoAnexo> getLstDocAnexo2() {
        return lstDocAnexo2;
    }

    public IODocumentoAnexo getDocumentoAnexo() {
        return documentoAnexo;
    }

    public void setDocumentoAnexo(IODocumentoAnexo documentoAnexo) {
        this.documentoAnexo = documentoAnexo;
    }

//    public IRemitenteService getiRemitenteService() {
//        return iRemitenteService;
//    }
//
//    public void setiRemitenteService(IRemitenteService iRemitenteService) {
//        this.iRemitenteService = iRemitenteService;
//    }

    public List<DocumentoCargo> getLstDocCargo() {
        return lstDocCargo;
    }

    public DocumentoCargo getDocumentoCargo() {
        return documentoCargo;
    }

    public void setDocumentoCargo(DocumentoCargo documentoCargo) {
        this.documentoCargo = documentoCargo;
    }

    public String getNomDocumento() {
        return nomDocumento;
    }

    public void setNomDocumento(String nomDocumento) {
        this.nomDocumento = nomDocumento;
    }

    public String getArgumento() {
        return argumento;
    }

    public void setArgumento(String argumento) {
        this.argumento = argumento;
    }

    public Date getDfecdesde() {
        return dfecdesde;
    }

    public void setDfecdesde(Date dfecdesde) {
        this.dfecdesde = dfecdesde;
    }

    public Date getDfechasta() {
        return dfechasta;
    }

    public void setDfechasta(Date dfechasta) {
        this.dfechasta = dfechasta;
    }

    public String getCodependencia() {
        return codependencia;
    }

    public void setCodependencia(String codependencia) {
        this.codependencia = codependencia;
    }

    public String getDesde() {
        return desde;
    }

    public void setDesde(String desde) {
        this.desde = desde;
    }

    public List<Maestro> getCboDependencia() {
        return cboDependencia;
    }

    public void setCboDependencia(List<Maestro> cboDependencia) {
        this.cboDependencia = cboDependencia;
    }

//    public IMaestroService getiMaestroService() {
//        return iMaestroService;
//    }
//
//    public void setiMaestroService(IMaestroService iMaestroService) {
//        this.iMaestroService = iMaestroService;
//    }

    public List<Maestro> getCbotipdocmv() {
        return cbotipdocmv;
    }

    public void setCbotipdocmv(List<Maestro> cbotipdocmv) {
        this.cbotipdocmv = cbotipdocmv;
    }

    public int getContaanex() {
        return contaanex;
    }

    public void setContaanex(int contaanex) {
        this.contaanex = contaanex;
    }

    public void setLstDocAnexo2(List<IODocumentoAnexo> lstDocAnexo2) {
        this.lstDocAnexo2 = lstDocAnexo2;
    }

}
