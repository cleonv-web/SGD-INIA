package pe.gob.segdi.iotramitesgd.controller;


import org.jboss.logging.Logger;
import org.primefaces.event.FileUploadEvent;
import org.primefaces.event.SelectEvent;
import org.primefaces.model.DefaultStreamedContent;
import org.primefaces.model.StreamedContent;
import org.primefaces.model.UploadedFile;
import org.springframework.beans.BeanUtils;
import pe.gob.segdi.iotramitesgd.bean.*;
import pe.gob.segdi.iotramitesgd.service.IDocumentoAnexoService;
import pe.gob.segdi.iotramitesgd.service.IEmisionExternaService;
import pe.gob.segdi.iotramitesgd.service.IMaestroService;
import pe.gob.segdi.iotramitesgd.service.ITramiteService;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;
import pe.gob.segdi.srvccuo.service.EndPoint;
import pe.gob.segdi.srvciotramite.main.WSPideTramite;
import pe.gob.segdi.wsiopidetramite.ws.*;

import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedBean;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.model.SelectItem;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.XMLGregorianCalendar;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Objeto : CEmisionExterna.java. Descripción : Clase de despacho de documentos,
 * contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y
 * consultas). Fecha de Creación : 02/03/2018. Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones Fecha Nombre Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX XXXXXXXXX XXXXXXXXXXXXXXXXXX.
 */

@ManagedBean(name = "cEmisionExterna")
@SessionScoped
public class CEmisionExterna extends BaseController {

    private static Logger depurador = Logger.getLogger(CEmisionExterna.class.getName());
    private static final long serialVersionUID = 1L;
    private ConsultaDocumentoEmision consultaDocumento;
    private ConsultaDocumentoEmision consultaDocumentoObservacion;
    private List<IOEmisionExterna> lstEmiExt;
    private List<IODocumentoAnexo> lstDocAnexo;
    private List<IODocumentoAnexo> lstDocAnexo2;
    private List<IoTipoDocumentoTramite> lstTipDocTramite;
    private List<ConsultaDocumentoEmision> lstConsultaDocumento;
    private List<CboTramite> cboOficina;
    private List<Maestro> cboDependencia;
    private List<Maestro> cbotipdocmv;
    private List<SelectItem> entidades;
    private UploadedFile file, fileCargo;
    private UploadedFile fileDocumentoPrincipal;
    private Date dfecdesde;
    private Date dfechasta;
    private static int count = 1;
    private int sidcat;
    private int contaAnexo;
    private int iCodOficina;
    private int siddocext;
    private RecepcionTramite recepcionTramite;
    private RespuestaTramite respuestaTramite;
    private IODocumentoAnexo ioDocumentoAnexo;
    private String vrucentdst;
    private String vnomentrec;
    private String codependencia;
    private String cflgest;
    private String docestadomsj;
    private String co_par;
    private StreamedContent anexo;
    private boolean flganu;
    private boolean flgdesanu = true;

    @ManagedProperty(value = "#{iMaestroService}")
    private IMaestroService iMaestroService;
    @ManagedProperty(value = "#{iEmisionExternaService}")
    private IEmisionExternaService iEmisionExternaService;
    @ManagedProperty(value = "#{iDocumentoAnexoService}")
    private IDocumentoAnexoService iDocumentoAnexoService;
    @ManagedProperty(value = "#{iTramiteService}")
    private ITramiteService iTramiteService;
    private StreamedContent imagen;
    private StreamedContent filePrincipal;

    @PostConstruct
    public void init() {
        // depurador.info("INIT ======================>");
        // WSPideTramite wsPideTramite = new WSPideTramite();
        CVariableSession obj = (CVariableSession) findBean("cVariableSession");
        co_par = obj.getEstMesaVirtual();
        // lstTipDocTramite = wsPideTramite.getTipoDocumento(co_par);

    }

    @Override
    public String inicializarControladora() {
        return "emisionExterna";
    }

    @Override
    public String inicializarControladora(String cflgest) {
        // depurador.info("XXXXXX ===>"+cflgest);

        CVariableSession obj = (CVariableSession) findBean("cVariableSession");
        obj.setVnumdoctra(cflgest);
        this.cflgest = cflgest;
        codependencia = "-1";
        vrucentdst = "";
        try {
            lstConsultaDocumento = iEmisionExternaService.bsqEmisionExterna(vrucentdst, cflgest, codependencia, "", "");
            cbotipdocmv = iMaestroService.getTipoDocumentoMV();
            System.out.println(lstConsultaDocumento.size());

        } catch (Exception e) {
            depurador.error(null, e);
        }
        return "emisionExterna";
    }

    @Override
    public void inicializarVariable() {
        try {
            consultaDocumento = new ConsultaDocumentoEmision();
            consultaDocumentoObservacion = new ConsultaDocumentoEmision();

            WSPideTramite wsPideTramite = new WSPideTramite();

            contaAnexo = 0;
            recepcionTramite = new RecepcionTramite();
            respuestaTramite = new RespuestaTramite();

            lstDocAnexo = new ArrayList<IODocumentoAnexo>();
            lstConsultaDocumento = new ArrayList<ConsultaDocumentoEmision>();
            cboOficina = new ArrayList<CboTramite>();
            cboOficina = iTramiteService.getTramiteOficinas();

            imagen = new DefaultStreamedContent();
            filePrincipal = new DefaultStreamedContent();

            cboDependencia = new ArrayList<Maestro>();
            cboDependencia = iMaestroService.getDependencia();
            lstTipDocTramite = wsPideTramite.getTipoDocumento(co_par);

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    @Override
    public void limpiarObjectos() {
        consultaDocumento = new ConsultaDocumentoEmision();
        lstDocAnexo = new ArrayList<IODocumentoAnexo>();
        recepcionTramite = new RecepcionTramite();
        CVariableSession obj = (CVariableSession) findBean("cVariableSession");
        obj.setDocumentoPrincipalCargo(null);
    }

    public void registroEmisionExterna() {
        // depurador.info("registroEmisionExterna ==>");
        try {
            consultaDocumento = new ConsultaDocumentoEmision();
            addCallbackComponent("PF('emisionExternaInsertDialogWidget').show()");

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public String validaRegistroEmisionExterna() {
        if (consultaDocumento.getSidcatent() == 0) {
            return "Debe seleccionar el tipo de categoria el DNI";
        } else if (consultaDocumento.getVrucentrec() == null || consultaDocumento.getVrucentrec().equals("")) {
            return "Debe seleccionar la entidad destinataria";
        } else if (consultaDocumento.getCcodtipdoc() == null || consultaDocumento.getCcodtipdoc().equals("")) {
            return "Debe seleccionar el tipo de documento";
        } else if (consultaDocumento.getVnumdoc().equals("")) {
            return "Debe ingresar el número del documento";
        } else if (consultaDocumento.getDfecdoc0() == null || consultaDocumento.getDfecdoc0().equals("")) {
            return "Debe ingresar la fecha del documento";
        } else if (consultaDocumento.getVuniorgrem().equals("")) {
            return "Debe ingresar el nombre de la unidad organica emisora";
        } else if (consultaDocumento.getVuniorgdst().equals("")) {
            return "Debe ingresar el nombre de la unidad organica destino";
        } else if (consultaDocumento.getVnomdst().equals("")) {
            return "Debe ingresar el nombre del destinatario";
        } else if (consultaDocumento.getVnomcardst().equals("")) {
            return "Debe ingresar el nombre del cargo del destinatario";
        } else if (consultaDocumento.getVasu().equals("")) {
            return "Debe ingresar el asunto";
        } else if (fileDocumentoPrincipal == null) {
            return "Debe ingresar el documento principal";
        } else if (consultaDocumento.getSnumfol() == 0) {
            return "Debe ingresar el número de folios";
        }

        return "";
    }

    public void detalleEmisionExterna() {
        // depurador.info("detalleEmisionExterna ==>");
        try {
            lstDocAnexo = iDocumentoAnexoService.getListaDocumentoAnexo(consultaDocumento.getSiddocext());
            docestadomsj = iTramiteService.getEstadoDocumento(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd());
            // depurador.info("docestadomsj ==>"+docestadomsj);
            consultaDocumento = iEmisionExternaService.getEmisionExterna(consultaDocumento.getSidemiext());
            addCallbackComponent("PF('emisionExternaDetalleDialogWidget').show()");

            // PrimeFaces.current().executeScript("PF('emisionExternaDetalleDialogWidget').show()");;

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void recepcionarDocumento() {
        try {
            docestadomsj = iTramiteService.updEstadoDocumento(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd(),
                    Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ1"));

            // depurador.info("docestadomsjjj ==>"+docestadomsj);
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void enviarDocumento() {
        // depurador.info("enviarDocumento ==>");
        try {

            WSPideTramite pideTramite = new WSPideTramite();
            EndPoint endPoint = new EndPoint();
            consultaDocumento
                    .setVrucentrem(Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA").trim());
            recepcionTramite = new RecepcionTramite();
            //BeanUtils.copyProperties(recepcionTramite, consultaDocumento);
            BeanUtils.copyProperties(consultaDocumento, recepcionTramite);

            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(consultaDocumento.getDfecdoc0().getTime());
            DatatypeFactory df = DatatypeFactory.newInstance();
            XMLGregorianCalendar dfecdoc = df.newXMLGregorianCalendar(gc);

            recepcionTramite
                    .setVnomentemi(Utilitarios.ObtenerDatosProperties("Parametros", "P_NOMBRE_ENTIDAD_EMISORA"));
            recepcionTramite.setDfecdoc(dfecdoc);
            recepcionTramite.setBpdfdoc(Base64.getEncoder().encode(consultaDocumento.getBpdfdoc()));
            DocumentoAnexo anexoBean = null;

            for (int i = 0; i < lstDocAnexo2.size(); i++) {
                anexoBean = new DocumentoAnexo();
                anexoBean.setVnomdoc(lstDocAnexo2.get(i).getVnomdoc());
                recepcionTramite.getLstanexos().add(anexoBean);
            }

            String cflgenv = iEmisionExternaService.obtEstadoEnvio(consultaDocumento.getSidemiext());
            String vcuo = "";
            if (!cflgenv.equals("E")) {
                if (co_par.equals("P")) {
                    vcuo = endPoint.getCuo(
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA").trim(),
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                } else if (co_par.equals("D")) {
                    vcuo = endPoint.getCuo(Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                }

                if (!vcuo.equals("-1")) {
                    consultaDocumento.setVcuo(vcuo);
                    recepcionTramite.setVcuo(vcuo);
                    iEmisionExternaService.insCuoEmisionExterna(consultaDocumento.getVcuo(),
                            consultaDocumento.getSidemiext());
                    try {
                        respuestaTramite = pideTramite.recepcionarTramite(recepcionTramite, co_par);
                        if (respuestaTramite.getVcodres().equals("0000")) {
                            iEmisionExternaService.updEstadoEmisionExterna(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_ENVIADO"),
                                    consultaDocumento.getVnumregstd(), consultaDocumento.getVanioregstd(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ2"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        } else {
                            iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        }
                    } catch (Exception e) {
                        // depurador.info(null,e);
                        iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                        addInfoMessage("Mensaje",
                                Utilitarios.ObtenerDatosProperties("MessageResources", "ERROR_SERVICIO_RECEPCION"));
                    }
                } else {
                    addInfoMessage("Mensaje",
                            Utilitarios.ObtenerDatosProperties("MessageResources", "ERROR_SERVICIO_CUO"));
                }

            } else {

                vcuo = iEmisionExternaService.obtCuo(consultaDocumento.getSidemiext());
                ConsultaTramite request = new ConsultaTramite();
                request.setVcuo(vcuo);
                request.setVrucentrem(Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA"));
                request.setVrucentrec(consultaDocumento.getVrucentrec());

                RespuestaConsultaTramite respuestaConsultaTramite = pideTramite.consultarTramite(request, co_par);
                System.out.println("CODIGO RESPUESTA ===>" + respuestaConsultaTramite.getVcodres());
                if (respuestaConsultaTramite.getVcodres().equals("0000")) {
                    iEmisionExternaService.updEstadoEmisionExterna(consultaDocumento.getSidemiext(),
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_ENVIADO"),
                            consultaDocumento.getVnumregstd(), consultaDocumento.getVanioregstd(),
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ2"));
                    addInfoMessage("Mensaje", Utilitarios.ObtenerDatosProperties("MessageResources", "ESTADO_ENVIADO"));
                } else if (respuestaConsultaTramite.getVcodres().equals("0001")) {
                    if (co_par.equals("P")) {
                        vcuo = endPoint.getCuo(
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA").trim(),
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                    } else if (co_par.equals("D")) {
                        vcuo = endPoint
                                .getCuo(Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                    }
                    consultaDocumento.setVcuo(vcuo);
                    recepcionTramite.setVcuo(vcuo);
                    iEmisionExternaService.insCuoEmisionExterna(consultaDocumento.getVcuo(),
                            consultaDocumento.getSidemiext());
                    try {
                        respuestaTramite = pideTramite.recepcionarTramite(recepcionTramite, co_par);
                        if (respuestaTramite.getVcodres().equals("0000")) {
                            iEmisionExternaService.updEstadoEmisionExterna(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_ENVIADO"),
                                    consultaDocumento.getVnumregstd(), consultaDocumento.getVanioregstd(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ2"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        } else {
                            iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        }
                    } catch (Exception e) {
                        // depurador.info(null,e);
                        iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                        addInfoMessage("Mensaje",
                                Utilitarios.ObtenerDatosProperties("MessageResources", "ERROR_SERVICIO_RECEPCION"));
                    }
                } else {
                    addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                }
            }

            bsqDocumentoDespacho();
            limpiarObjectos();
            addCallbackComponent("PF('emisionExternaEnvioDialogWidget').hide()");
            // addCallbackParam("flag",flagEnvioExitoso);

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void enviarDocumentoMultiple() {
        // depurador.info("enviarDocumentoMultiple ==>");
        try {
            WSPideTramite pideTramite = new WSPideTramite();
            EndPoint endPoint = new EndPoint();
            consultaDocumento
                    .setVrucentrem(Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA").trim());
            //BeanUtils.copyProperties(recepcionTramite, consultaDocumento);
            BeanUtils.copyProperties(consultaDocumento, recepcionTramite);

            GregorianCalendar gc = new GregorianCalendar();
            gc.setTimeInMillis(consultaDocumento.getDfecdoc0().getTime());
            DatatypeFactory df = DatatypeFactory.newInstance();
            XMLGregorianCalendar dfecdoc = df.newXMLGregorianCalendar(gc);

            recepcionTramite
                    .setVnomentemi(Utilitarios.ObtenerDatosProperties("Parametros", "P_NOMBRE_ENTIDAD_EMISORA"));
            recepcionTramite.setDfecdoc(dfecdoc);
            recepcionTramite.setBpdfdoc(Base64.getEncoder().encode(consultaDocumento.getBpdfdoc()));
            DocumentoAnexo anexoBean = null;

            for (int i = 0; i < lstDocAnexo.size(); i++) {
                anexoBean = new DocumentoAnexo();
                anexoBean.setVnomdoc(lstDocAnexo.get(i).getVnomdoc());
                recepcionTramite.getLstanexos().add(anexoBean);
            }

            String cflgenv = iEmisionExternaService.obtEstadoEnvio(consultaDocumento.getSidemiext());
            String vcuo = "";
            if (!cflgenv.equals("E")) {
                if (co_par.equals("P")) {
                    vcuo = endPoint.getCuo(
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA").trim(),
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                } else if (co_par.equals("D")) {
                    vcuo = endPoint.getCuo(Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                }

                if (!vcuo.equals("-1")) {
                    consultaDocumento.setVcuo(vcuo);
                    recepcionTramite.setVcuo(vcuo);
                    iEmisionExternaService.insCuoEmisionExterna(consultaDocumento.getVcuo(),
                            consultaDocumento.getSidemiext());
                    try {
                        respuestaTramite = pideTramite.recepcionarTramite(recepcionTramite, co_par);
                        if (respuestaTramite.getVcodres().equals("0000")) {
                            iEmisionExternaService.updEstadoEmisionExterna(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_ENVIADO"),
                                    consultaDocumento.getVnumregstd(), consultaDocumento.getVanioregstd(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ2"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        } else {
                            iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        }
                    } catch (Exception e) {
                        // depurador.info(null,e);
                        iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                        addInfoMessage("Mensaje",
                                Utilitarios.ObtenerDatosProperties("MessageResources", "ERROR_SERVICIO_RECEPCION"));
                    }
                } else {
                    addInfoMessage("Mensaje",
                            Utilitarios.ObtenerDatosProperties("MessageResources", "ERROR_SERVICIO_CUO"));
                }

            } else {

                vcuo = iEmisionExternaService.obtCuo(consultaDocumento.getSidemiext());
                ConsultaTramite request = new ConsultaTramite();
                request.setVcuo(vcuo);
                request.setVrucentrem(Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA"));
                request.setVrucentrec(consultaDocumento.getVrucentrec());

                RespuestaConsultaTramite respuestaConsultaTramite = pideTramite.consultarTramite(request, co_par);

                if (respuestaConsultaTramite.getVcodres().equals("0000")) {
                    iEmisionExternaService.updEstadoEmisionExterna(consultaDocumento.getSidemiext(),
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_ENVIADO"),
                            consultaDocumento.getVnumregstd(), consultaDocumento.getVanioregstd(),
                            Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ2"));
                    addInfoMessage("Mensaje", Utilitarios.ObtenerDatosProperties("MessageResources", "ESTADO_ENVIADO"));
                } else if (respuestaConsultaTramite.getVcodres().equals("0001")) {
                    if (co_par.equals("P")) {
                        vcuo = endPoint.getCuo(
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA").trim(),
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                    } else if (co_par.equals("D")) {
                        vcuo = endPoint
                                .getCuo(Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_SERVICIO").trim());
                    }
                    consultaDocumento.setVcuo(vcuo);
                    recepcionTramite.setVcuo(vcuo);
                    iEmisionExternaService.insCuoEmisionExterna(consultaDocumento.getVcuo(),
                            consultaDocumento.getSidemiext());
                    try {
                        respuestaTramite = pideTramite.recepcionarTramite(recepcionTramite, co_par);
                        if (respuestaTramite.getVcodres().equals("0000")) {
                            iEmisionExternaService.updEstadoEmisionExterna(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_ENVIADO"),
                                    consultaDocumento.getVnumregstd(), consultaDocumento.getVanioregstd(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ2"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        } else {
                            iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                    Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                            addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                        }
                    } catch (Exception e) {
                        // depurador.info(null,e);
                        iEmisionExternaService.updEstadoEnvio(consultaDocumento.getSidemiext(),
                                Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ERROR_ENVIO"));
                        addInfoMessage("Mensaje",
                                Utilitarios.ObtenerDatosProperties("MessageResources", "ERROR_SERVICIO_RECEPCION"));
                    }
                } else {
                    addInfoMessage("Mensaje", respuestaTramite.getVdesres());
                }
            }

            bsqDocumentoDespacho();
            limpiarObjectos();
            // addCallbackParam("flag",flagEnvioExitoso);
            addCallbackComponent("PF('emisionExternaEnvioDialogWidget').hide()");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void bsqDocumentoDespacho() {
        try {
            limpiarObjectos();
            String fecdesde = "";
            String fechasta = "";
            String msj = validarFormularioBusqueda();
            if (msj.equals("")) {
                if (this.dfecdesde != null) {
                    SimpleDateFormat format = new SimpleDateFormat("dd/MM/yyyy");
                    fecdesde = format.format(dfecdesde);
                    fechasta = format.format(dfechasta);
                }
                lstDocAnexo = new ArrayList<IODocumentoAnexo>();
                lstConsultaDocumento = iEmisionExternaService.bsqEmisionExterna(vrucentdst, cflgest, codependencia,
                        fecdesde, fechasta);

            } else {
                addInfoMessage("Error", msj);
            }
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void getDocumentoDespacho(SelectEvent event) {
        // depurador.info("getDocumentoDespacho ==>"+((ConsultaDocumentoEmision)
        // event.getObject()).getSidemiext());
        try {
            lstDocAnexo = new ArrayList<IODocumentoAnexo>();
            consultaDocumento = iEmisionExternaService
                    .getEmisionExterna(((ConsultaDocumentoEmision) event.getObject()).getSidemiext());

            lstDocAnexo2 = iDocumentoAnexoService.getListaDocumentoAnexo2(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd());
            docestadomsj = iTramiteService.getEstadoDocumento(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd());
            addCallbackComponent("PF('emisionExternaEnvioDialogWidget').show()");

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public String validarFormularioBusqueda() {
        if ((cflgest != null && cflgest.equals("-1"))) {
            return "Debe seleccionar el estado ";
        } else if ((dfecdesde != null && dfechasta == null) || (dfecdesde == null && dfechasta != null)) {
            return "Ingresar la fecha de inicio y fin ";
        }
        return "";
    }

    public void eliminarFilePrincipalObservacionEmisionExterna() {
        try {
            consultaDocumentoObservacion.setVnomdoc("");
            consultaDocumentoObservacion.setBpdfdoc(null);
            consultaDocumentoObservacion.setVnumdoc("");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void fileUploadPrincipal(FileUploadEvent event) throws IOException {
        // depurador.info("fileUploadPrincipal ==>");
        fileDocumentoPrincipal = event.getFile();
        consultaDocumento.setVnomdoc(fileDocumentoPrincipal.getFileName());
        // depurador.info("NAME ==>"+fileDocumentoPrincipal.getFileName());
        consultaDocumento.setBpdfdoc(fileDocumentoPrincipal.getContents());

    }

    public void notEmisionExterna() {
        // depurador.info("notEmisionExterna ==>");
        try {
            addCallbackComponent("PF('notificacionExternaEnvioDialogWidget').show()");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void verVoucher() throws IOException {
        // depurador.info("verVoucher ==>");
        try {
            Utilitarios.executePdf(Base64.getDecoder().decode(consultaDocumento.getBcarstdrec()),
                    "cargo_" + consultaDocumento.getVcuo());

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void anularRecepcion() {
        // depurador.info("anularRecepcion ==>");
        try {

            if (flganu == true
                    && (consultaDocumento.getVdesanu() == null || consultaDocumento.getVdesanu().equals(""))) {
                addInfoMessage("Mensaje",
                        Utilitarios.ObtenerDatosProperties("MessageResources", "MSJ_MOT_ANU_DOC_DES"));
            } else {
                docestadomsj = iEmisionExternaService.anularRecepcion(consultaDocumento.getVnumregstd(),
                        consultaDocumento.getVanioregstd(),
                        //Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ0"),
                        null,
                        consultaDocumento.getSidemiext(),
                        consultaDocumento.getVdesanu());
                if (docestadomsj.equals("0")) {
                    addInfoMessage("Mensaje", Utilitarios.ObtenerDatosProperties("MessageResources", "MSJ_ANU_DOC_MV"));
                } else {
                    addInfoMessage("Mensaje",
                            Utilitarios.ObtenerDatosProperties("MessageResources", "MSJ_ANU_DOC_MV2"));
                }
                bsqDocumentoDespacho();
                addCallbackComponent("PF('emisionExternaEnvioDialogWidget').hide()");
            }
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void habilitarDocumentoSGD() {
        // depurador.info("habilitarDocumentoSGD ==>");
        try {
            int flag = iTramiteService.habilitarDocumento(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd(),
                    Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_DOC_EMI"),
                    Utilitarios.ObtenerDatosProperties("Parametros", "P_DOC_ESTADO_MSJ0"), consultaDocumento.getVobs(),
                    consultaDocumento.getVrucentrec());
            if (flag == 1) {
                addInfoMessage("Mensaje", Utilitarios.ObtenerDatosProperties("MessageResources", "MSJ_HAB_DOC_SGD"));
            } else {
                addInfoMessage("Mensaje", Utilitarios.ObtenerDatosProperties("MessageResources", "MSJ_HAB_DOC_SGD2"));
            }

            bsqDocumentoDespacho();
            addCallbackComponent("PF('emisionExternaDetalleDialogWidget').hide()");

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void formEnvioDocumento() {
        // depurador.info("formEnvioDocumento ==>");
        try {
            flganu = false;
            flgdesanu = true;
            lstDocAnexo2 = iDocumentoAnexoService.getListaDocumentoAnexo2(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd());
            docestadomsj = iTramiteService.getEstadoDocumento(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd());
            consultaDocumento = iEmisionExternaService.getEmisionExterna(consultaDocumento.getSidemiext());
            addCallbackComponent("PF('emisionExternaEnvioDialogWidget').show()");

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void documentoPrincipal() {
        // depurador.info("documentoPrincipal ==>");
        try {
            if (consultaDocumento.getBpdfdoc() != null) {
                InputStream in = new ByteArrayInputStream(consultaDocumento.getBpdfdoc());
                CVariableSession obj = (CVariableSession) findBean("cVariableSession");
                obj.setDocumentoPrincipal(
                        new DefaultStreamedContent(in, "application/pdf", consultaDocumento.getVnomdoc()));
            }
            lstDocAnexo2 = iDocumentoAnexoService.getListaDocumentoAnexo2(consultaDocumento.getVnumregstd(),
                    consultaDocumento.getVanioregstd());
            addCallbackComponent("PF('emisionExternaDocumentoPrincipalDialogWidget').show()");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void cargoDocumento() {
        // depurador.info(":::::::::::::::::::::::::::::::cargoDocumento::::::::::::::::::::::::::::::::");
        try {
            if (consultaDocumento.getBcarstdrec() != null) {
                Date fecha = new Date();
                InputStream in = new ByteArrayInputStream(consultaDocumento.getBcarstdrec());
                CVariableSession obj = (CVariableSession) findBean("cVariableSession");
                obj.setDocumentoPrincipalCargo(
                        new DefaultStreamedContent(in, "application/pdf", fecha.getTime() + ".pdf"));
            }
            addCallbackComponent("PF('cargoDialogWidget').show()");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void verDescripcionAnulacion() {
        if (!flganu) {
            consultaDocumento.setVdesanu("");
            flgdesanu = false;
        } else {
            flgdesanu = true;

        }
    }

    public void actualizarCargoDespacho() throws Exception {
        try {
            //depurador.info("actualizarCargoDespacho ==>");
            IOEmisionExternaBean ioEmisionExternaBean = new IOEmisionExternaBean();

            WSPideTramite wsPideTramite = new WSPideTramite();
            ConsultaTramite consultaTramite = new ConsultaTramite();
            consultaTramite.setVrucentrem(Utilitarios.ObtenerDatosProperties("Parametros", "P_RUC_ENTIDAD_EMISORA"));
            consultaTramite.setVrucentrec(consultaDocumento.getVrucentrec());
            consultaTramite.setVcuo(consultaDocumento.getVcuo());
            RespuestaConsultaTramite respuestaConsultaTramite = null;

            respuestaConsultaTramite = wsPideTramite.consultarTramite(consultaTramite, co_par);

            if (respuestaConsultaTramite.getVcodres().equals("0000")) {
                byte[] decodedBytes = null;
                Date fecregstdrec = null;
                if (respuestaConsultaTramite.getCflgest().equals("R") || respuestaConsultaTramite.getCflgest().equals("O")) {
                    decodedBytes = Base64.getDecoder().decode(respuestaConsultaTramite.getBcarstd());
                    fecregstdrec = respuestaConsultaTramite.getDfecregstd().toGregorianCalendar().getTime();
                }

                ioEmisionExternaBean.setVnumregstdrec(respuestaConsultaTramite.getVnumregstd());
                ioEmisionExternaBean.setVanioregstdrec(respuestaConsultaTramite.getVanioregstd());
                ioEmisionExternaBean.setDfecregstdrec(fecregstdrec);
                ioEmisionExternaBean.setVuniorgstdrec(respuestaConsultaTramite.getVuniorgstd());
                ioEmisionExternaBean.setVusuregstdrec(respuestaConsultaTramite.getVusuregstd());
                ioEmisionExternaBean.setBcarstdrec(decodedBytes);
                ioEmisionExternaBean.setCflgest(respuestaConsultaTramite.getCflgest());
                ioEmisionExternaBean.setVobs(respuestaConsultaTramite.getVobs());
                ioEmisionExternaBean.setVcuo(consultaTramite.getVcuo());

                if (respuestaConsultaTramite.getCflgest().equals("R") || respuestaConsultaTramite.getCflgest().equals("O")) {
                    iEmisionExternaService.actualizarCargoDespacho(ioEmisionExternaBean);
                    if (respuestaConsultaTramite.getCflgest().equals("R")) {
                        addInfoMessage("Mensaje", "El documento fue recepcionado");
                    } else {
                        addInfoMessage("Mensaje", "El documento fue observado");
                    }
                } else if (respuestaConsultaTramite.getCflgest().equals("P")) {
                    addErrorMessage("Error", "El documento se encuentra en estado Pendiente");
                } else {
                    addErrorMessage("Error", "El estado del documento no corresponde");
                }

            } else if (respuestaConsultaTramite.getVcodres().equals("0001")) {
                addErrorMessage("Error", respuestaConsultaTramite.getVdesres());
            } else if (respuestaConsultaTramite.getVcodres().equals("-1")) {
                addErrorMessage("Error", respuestaConsultaTramite.getVdesres());
            }

            inicializarControladora(Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_ENVIADO"));
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void fileUploadCargo(FileUploadEvent event) throws IOException {
        //depurador.info("fileUploadCargo ==>" + contaAnexo);
        try {
            fileCargo = event.getFile();
            iEmisionExternaService.actualizarCargo(consultaDocumento.getSidemiext(), fileCargo.getContents());
            fileCargo = null;
            consultaDocumento = iEmisionExternaService.getEmisionExterna(consultaDocumento.getSidemiext());
            addInfoMessage("Mensaje", "El cargo se actualizó satisfactoriamente");
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    /*
     * ********************************************************************/
    public ConsultaDocumentoEmision getconsultaDocumento() {
        return consultaDocumento;
    }

    public void setconsultaDocumento(ConsultaDocumentoEmision consultaDocumento) {
        this.consultaDocumento = consultaDocumento;
    }

    public UploadedFile getFile() {
        return file;
    }

    public void setFile(UploadedFile file) {
        this.file = file;
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

    public String getVrucentdst() {
        return vrucentdst;
    }

    public void setVrucentdst(String vrucentdst) {
        this.vrucentdst = vrucentdst;
    }

    public List<IOEmisionExterna> getLstEmiExt() {
        return lstEmiExt;
    }

    public void setLstEmiExt(List<IOEmisionExterna> lstEmiExt) {
        this.lstEmiExt = lstEmiExt;
    }

    public IEmisionExternaService getiEmisionExternaService() {
        return iEmisionExternaService;
    }

    public void setiEmisionExternaService(IEmisionExternaService iEmisionExternaService) {
        this.iEmisionExternaService = iEmisionExternaService;
    }

    public List<IODocumentoAnexo> getLstDocAnexo() {
        return lstDocAnexo;
    }

    public List<ConsultaDocumentoEmision> getLstConsultaDocumento() {
        return lstConsultaDocumento;
    }

    public void setLstConsultaDocumento(List<ConsultaDocumentoEmision> lstConsultaDocumento) {
        this.lstConsultaDocumento = lstConsultaDocumento;
    }

    public IDocumentoAnexoService getiDocumentoAnexoService() {
        return iDocumentoAnexoService;
    }

    public void setiDocumentoAnexoService(IDocumentoAnexoService iDocumentoAnexoService) {
        this.iDocumentoAnexoService = iDocumentoAnexoService;
    }

    public String getCflgest() {
        return cflgest;
    }

    public void setCflgest(String cflgest) {
        this.cflgest = cflgest;
    }

    public StreamedContent getImagen() throws IOException {
        /*
         * if (imagen != null) imagen.getStream().reset(); //reset stream to the start
         * position!
         */
        return imagen;
    }

    public void setImagen(StreamedContent imagen) {
        this.imagen = imagen;
    }

    public String getVnomentrec() {
        return vnomentrec;
    }

    public void setVnomentrec(String vnomentrec) {
        this.vnomentrec = vnomentrec;
    }

    public StreamedContent getFilePrincipal() {
        return filePrincipal;
    }

    public void setFilePrincipal(StreamedContent filePrincipal) {
        this.filePrincipal = filePrincipal;
    }

    public ConsultaDocumentoEmision getConsultaDocumentoObservacion() {
        return consultaDocumentoObservacion;
    }

    public void setConsultaDocumentoObservacion(ConsultaDocumentoEmision consultaDocumentoObservacion) {
        this.consultaDocumentoObservacion = consultaDocumentoObservacion;
    }

    public UploadedFile getFileDocumentoPrincipal() {
        return fileDocumentoPrincipal;
    }

    public void setFileDocumentoPrincipal(UploadedFile fileDocumentoPrincipal) {
        this.fileDocumentoPrincipal = fileDocumentoPrincipal;
    }

    public List<CboTramite> getCboOficina() {
        return cboOficina;
    }

    public void setCboOficina(List<CboTramite> cboOficina) {
        this.cboOficina = cboOficina;
    }

    public ITramiteService getiTramiteService() {
        return iTramiteService;
    }

    public void setiTramiteService(ITramiteService iTramiteService) {
        this.iTramiteService = iTramiteService;
    }

    public int getiCodOficina() {
        return iCodOficina;
    }

    public void setiCodOficina(int iCodOficina) {
        this.iCodOficina = iCodOficina;
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

    public IMaestroService getiMaestroService() {
        return iMaestroService;
    }

    public void setiMaestroService(IMaestroService iMaestroService) {
        this.iMaestroService = iMaestroService;
    }

    public List<Maestro> getCboDependencia() {
        return cboDependencia;
    }

    public void setCboDependencia(List<Maestro> cboDependencia) {
        this.cboDependencia = cboDependencia;
    }

    public String getDocestadomsj() {
        return docestadomsj;
    }

    public void setDocestadomsj(String docestadomsj) {
        this.docestadomsj = docestadomsj;
    }

    public StreamedContent getAnexo() {
        return anexo;
    }

    public void setAnexo(StreamedContent anexo) {
        this.anexo = anexo;
    }

    public int getSiddocext() {
        return siddocext;
    }

    public void setSiddocext(int siddocext) {
        this.siddocext = siddocext;
    }

    public List<IODocumentoAnexo> getLstDocAnexo2() {
        return lstDocAnexo2;
    }

    public void setLstDocAnexo2(List<IODocumentoAnexo> lstDocAnexo2) {
        this.lstDocAnexo2 = lstDocAnexo2;
    }

    public IODocumentoAnexo getIoDocumentoAnexo() {
        return ioDocumentoAnexo;
    }

    public void setIoDocumentoAnexo(IODocumentoAnexo ioDocumentoAnexo) {
        this.ioDocumentoAnexo = ioDocumentoAnexo;
    }

    public List<Maestro> getCbotipdocmv() {
        return cbotipdocmv;
    }

    public void setCbotipdocmv(List<Maestro> cbotipdocmv) {
        this.cbotipdocmv = cbotipdocmv;
    }

    public boolean isFlganu() {
        return flganu;
    }

    public void setFlganu(boolean flganu) {
        this.flganu = flganu;
    }

    public boolean isFlgdesanu() {
        return flgdesanu;
    }

    public void setFlgdesanu(boolean flgdesanu) {
        this.flgdesanu = flgdesanu;
    }

}
