package pe.gob.segdi.iotramitesgd.controller;

import org.jboss.logging.Logger;
import org.primefaces.model.StreamedContent;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.SessionScoped;
import java.io.Serializable;


/**
 * Objeto : CVariableSession.java.
 * Descripción : Clase controladora que permite guardar variables de sessión para usarse en distintos controladores.
 * Fecha de Creación : 02/03/2018
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX       XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@ManagedBean("cVariableSession")
@SessionScoped
public class CVariableSession extends BaseController implements Serializable {
    private static Logger depurador = Logger.getLogger(CVariableSession.class.getName());
    private static final long serialVersionUID = 1L;
    private String usuario;
    private int icodtrabajdor;
    private String cIpHost;
    private String vnumdoctra;
    private StreamedContent documentoPrincipalCargo;
    private StreamedContent documentoPrincipal;
    private StreamedContent documentoAnexo;
    private String idsesion;
    private String flgactcar;
    private String wsServiceLocation;
    private String urlAplication;
    private String estMesaVirtual;
    private int anx_cantidad;
    private int anx_tamanio;

    /**
     * Constructor de la clase.
     */
    public CVariableSession() {
        inicializarVariable();
    }

    @PostConstruct
    public void init() {
        try {
            documentoAnexo = null;
        } catch (Exception e) {
            depurador.error(null, e);
        }
    }

    @Override
    public void inicializarVariable() {

    }

    @Override
    public String inicializarControladora() {
        return null;
    }

    @Override
    public void limpiarObjectos() {
        // TODO Auto-generated method stub
    }

    /*
     * *****************************************************************************************
     ******************************************************************************************
     ******************************************************************************************/

    public String getcIpHost() {
        return cIpHost;
    }

    public StreamedContent getDocumentoPrincipal() {
        return documentoPrincipal;
    }

    public void setDocumentoPrincipal(StreamedContent documentoPrincipal) {
        this.documentoPrincipal = documentoPrincipal;
    }

    public StreamedContent getDocumentoAnexo() {
        return documentoAnexo;
    }

    public void setDocumentoAnexo(StreamedContent documentoAnexo) {
        this.documentoAnexo = documentoAnexo;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public void setcIpHost(String cIpHost) {
        this.cIpHost = cIpHost;
    }

    public String getVnumdoctra() {
        return vnumdoctra;
    }

    public void setVnumdoctra(String vnumdoctra) {
        this.vnumdoctra = vnumdoctra;
    }

    public StreamedContent getDocumentoPrincipalCargo() {
        return documentoPrincipalCargo;
    }

    public void setDocumentoPrincipalCargo(StreamedContent documentoPrincipalCargo) {
        this.documentoPrincipalCargo = documentoPrincipalCargo;
    }

    public int getIcodtrabajdor() {
        return icodtrabajdor;
    }

    public void setIcodtrabajdor(int icodtrabajdor) {
        this.icodtrabajdor = icodtrabajdor;
    }

    public String getIdsesion() {
        return idsesion;
    }

    public void setIdsesion(String idsesion) {
        this.idsesion = idsesion;
    }

    public String getWsServiceLocation() {
        return wsServiceLocation;
    }

    public void setWsServiceLocation(String wsServiceLocation) {
        this.wsServiceLocation = wsServiceLocation;
    }

    public String getUrlAplication() {
        return urlAplication;
    }

    public void setUrlAplication(String urlAplication) {
        this.urlAplication = urlAplication;
    }

    public String getEstMesaVirtual() {
        return estMesaVirtual;
    }

    public void setEstMesaVirtual(String estMesaVirtual) {
        this.estMesaVirtual = estMesaVirtual;
    }

    @Override
    public String inicializarControladora(String x) {
        // TODO Auto-generated method stub
        return null;
    }

    public String getFlgactcar() {
        return flgactcar;
    }

    public void setFlgactcar(String flgactcar) {
        this.flgactcar = flgactcar;
    }

    public int getAnx_cantidad() {
        return anx_cantidad;
    }

    public void setAnx_cantidad(int anx_cantidad) {
        this.anx_cantidad = anx_cantidad;
    }

    public void setAnx_tamanio(int anx_tamanio) {
        this.anx_tamanio = anx_tamanio;
    }

    public int getAnx_tamanio() {
        return anx_tamanio;
    }


}
