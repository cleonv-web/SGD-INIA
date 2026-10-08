package pe.gob.segdi.iotramitesgd.controller;

import org.jboss.logging.Logger;
import org.primefaces.event.TransferEvent;
import org.primefaces.model.DualListModel;
import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoEmision;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExternaBean;
import pe.gob.segdi.iotramitesgd.service.IEmisionExternaService;
import pe.gob.segdi.srvciotramite.main.WSPideTramite;
import pe.gob.segdi.wsiopidetramite.ws.ConsultaTramite;
import pe.gob.segdi.wsiopidetramite.ws.RespuestaConsultaTramite;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.bean.ManagedProperty;
import javax.faces.bean.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;


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

@ManagedBean("cCargo")
@SessionScoped
public class CCargo extends BaseController {

    private static Logger depurador = Logger.getLogger(CCargo.class.getName());
    private static final long serialVersionUID = 1L;
    //@ManagedProperty(value = "#{iEmisionExternaService}")
    @Inject
    private IEmisionExternaService iEmisionExternaService;

    @Inject
    private CVariableSession cVariableSession;

    private String vRucEntRec;
    private String co_par;
    private DualListModel<IOEmisionExternaBean> lista;
    private List<String> listaRuc;
    private List<ConsultaDocumentoEmision> lstConsultaDocumento;
    private String flgcar;
    private String vcuo;
    private ConsultaDocumentoEmision consultaDocumento;


    @PostConstruct
    public void init() {
        depurador.info("INIT CARGO ::::::::::::::");
//        CVariableSession obj = (CVariableSession) findBean("cVariableSession");
//        co_par = obj.getEstMesaVirtual();
        co_par = cVariableSession.getEstMesaVirtual();
    }

    @Override
    public String inicializarControladora() {
        try {

            List<IOEmisionExternaBean> listaSource = iEmisionExternaService.getEntidadEnviados();
            List<IOEmisionExternaBean> listaTarget = new ArrayList<IOEmisionExternaBean>();

            lista = new DualListModel<IOEmisionExternaBean>(listaSource, listaTarget);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return "cargomasivo";
    }

    @Override
    public String inicializarControladora(String x) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public void inicializarVariable() {
        // TODO Auto-generated method stub

    }

    @Override
    public void limpiarObjectos() {
        // TODO Auto-generated method stub

    }


    public void actualizarCargoDespacho() throws Exception {
        try {
            //depurador.info("actualizarCargoDespacho ==>");
            IOEmisionExternaBean ioEmisionExternaBean = new IOEmisionExternaBean();
            List<IOEmisionExternaBean> lista = new ArrayList<IOEmisionExternaBean>();

            WSPideTramite wsPideTramite = new WSPideTramite();
            ConsultaTramite consultaTramite = null;
            RespuestaConsultaTramite respuestaConsultaTramite = null;

            for (int i = 0; i < listaRuc.size(); i++) {
                //depurador.info("RUC ==>" + listaRuc.get(i));
                lista = iEmisionExternaService.getDocumentosEnviados(listaRuc.get(i));

                for (int j = 0; j < lista.size(); j++) {
                    consultaTramite = new ConsultaTramite();
                    consultaTramite.setVrucentrem("20168999926");
                    consultaTramite.setVrucentrec(lista.get(j).getVrucentrec());
                    consultaTramite.setVcuo(lista.get(j).getVcuo());

                    respuestaConsultaTramite = wsPideTramite.consultarTramite(consultaTramite, co_par);

                    try {

                        if (respuestaConsultaTramite.getVcodres().equals("0000")) {
                            depurador.info("RUC ==>" + consultaTramite.getVrucentrec() + " CUO ==>" + lista.get(j).getVcuo()
                                    + " CODIGO RESPUESTA==>" + respuestaConsultaTramite.getVcodres()
                                    + " DESCRIPCION DE LA RESPUESTA: " + respuestaConsultaTramite.getVdesres());

                            if (respuestaConsultaTramite.getCflgest().equals("R") || respuestaConsultaTramite.getCflgest().equals("O")) {
                                depurador.info("ENTRA 1");
                                ioEmisionExternaBean.setVnumregstdrec(respuestaConsultaTramite.getVnumregstd());
                                ioEmisionExternaBean.setVanioregstdrec(respuestaConsultaTramite.getVanioregstd());
                                ioEmisionExternaBean.setDfecregstdrec(respuestaConsultaTramite.getDfecregstd().toGregorianCalendar().getTime());
                                ioEmisionExternaBean.setVuniorgstdrec(respuestaConsultaTramite.getVuniorgstd());
                                ioEmisionExternaBean.setVusuregstdrec(respuestaConsultaTramite.getVusuregstd());
                                byte[] decodedBytes = Base64.getDecoder().decode(respuestaConsultaTramite.getBcarstd());
                                ioEmisionExternaBean.setBcarstdrec(decodedBytes);
                                ioEmisionExternaBean.setCflgest(respuestaConsultaTramite.getCflgest());
                                ioEmisionExternaBean.setVobs(respuestaConsultaTramite.getVobs());
                                ioEmisionExternaBean.setVcuo(lista.get(j).getVcuo());
                                iEmisionExternaService.actualizarCargoDespacho(ioEmisionExternaBean);
                            } else if (respuestaConsultaTramite.getCflgest().equals("P")) {
                                depurador.info("RUC ==>" + consultaTramite.getVrucentrec() + " CUO ==>"
                                        + lista.get(j).getVcuo() + " El documento no ha sido recepcionado");
                            } else {
                                depurador.info("RUC ==>" + consultaTramite.getVrucentrec() + " CUO ==>"
                                        + lista.get(j).getVcuo() + " Error estado del documento no valido");
                            }

                        } else if (respuestaConsultaTramite.getVcodres().equals("0001")) {
                            depurador.info("RUC ==>" + consultaTramite.getVrucentrec() + " CUO ==>" + lista.get(j).getVcuo()
                                    + " Documento no encontrado en la entidad receptora " + lista.get(j).getVnomentrec());
                        } else if (respuestaConsultaTramite.getVcodres().equals("-1")) {
                            depurador.info("RUC ==>" + consultaTramite.getVrucentrec() + " CUO ==>" + lista.get(j).getVcuo()
                                    + " Error en la entidad receptora " + lista.get(j).getVnomentrec());
                        }
                    } catch (Exception e) {
                        depurador.error("ERROR EN EL SERVICIO :::::::::::::");

                    }


                }
            }
            inicializarControladora();
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    public void onTransfer(TransferEvent event) {
        //depurador.info("onTransfer ::::::::::::::::::::");
        listaRuc = new ArrayList<String>();
        for (Object item : event.getItems()) {
            //depurador.info("XXXXXX ==>" + item.toString());
            listaRuc.add(item.toString());
        }

    }

    public void onReorder() {
        FacesContext context = FacesContext.getCurrentInstance();
        context.addMessage(null, new FacesMessage(FacesMessage.SEVERITY_INFO, "List Reordered", null));
    }

    public String getvRucEntRec() {
        return vRucEntRec;
    }

    public void setvRucEntRec(String vRucEntRec) {
        this.vRucEntRec = vRucEntRec;
    }

    public DualListModel<IOEmisionExternaBean> getLista() {
        return lista;
    }

    public void setLista(DualListModel<IOEmisionExternaBean> lista) {
        this.lista = lista;
    }

//    public IEmisionExternaService getiEmisionExternaService() {
//        return iEmisionExternaService;
//    }
//
//    public void setiEmisionExternaService(IEmisionExternaService iEmisionExternaService) {
//        this.iEmisionExternaService = iEmisionExternaService;
//    }

    public String getFlgcar() {
        return flgcar;
    }

    public void setFlgcar(String flgcar) {
        this.flgcar = flgcar;
    }

    public List<ConsultaDocumentoEmision> getLstConsultaDocumento() {
        return lstConsultaDocumento;
    }

    public void setLstConsultaDocumento(List<ConsultaDocumentoEmision> lstConsultaDocumento) {
        this.lstConsultaDocumento = lstConsultaDocumento;
    }

    public String getVcuo() {
        return vcuo;
    }

    public void setVcuo(String vcuo) {
        this.vcuo = vcuo;
    }

    public ConsultaDocumentoEmision getConsultaDocumento() {
        return consultaDocumento;
    }

    public void setConsultaDocumento(ConsultaDocumentoEmision consultaDocumento) {
        this.consultaDocumento = consultaDocumento;
    }

}
