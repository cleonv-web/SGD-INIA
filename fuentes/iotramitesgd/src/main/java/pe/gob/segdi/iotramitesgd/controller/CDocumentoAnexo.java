package pe.gob.segdi.iotramitesgd.controller;

import org.jboss.logging.Logger;
import org.primefaces.model.StreamedContent;
import pe.gob.segdi.iotramitesgd.bean.IODocumentoAnexo;
import pe.gob.segdi.iotramitesgd.service.IDocumentoAnexoService;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;

import javax.annotation.ManagedBean;
import javax.annotation.PostConstruct;
import javax.faces.bean.ManagedProperty;
import javax.inject.Inject;
import java.util.ArrayList;
import java.util.List;

/**
 * Objeto : CDocumentoAnexo.java.
 * Descripción : Clase de documentos  anexos, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@ManagedBean("cDocumentoAnexo")
public class CDocumentoAnexo extends BaseController {

    private static Logger depurador = Logger.getLogger(CDocumentoAnexo.class.getName());
    private static final long serialVersionUID = 1L;
    //@ManagedProperty(value = "#{iDocumentoAnexoService}")
    @Inject
    private IDocumentoAnexoService iDocumentoAnexoService;
    private List<IODocumentoAnexo> lstAnexo;
    private StreamedContent file;
    private int siddocanx;
    private String vcuo;
    private IODocumentoAnexo ioDocumentoAnexo;

    @PostConstruct
    public void init() {
        inicializarVariable();
    }

    @Override
    public String inicializarControladora() {
        // TODO Auto-generated method stub
        return "descargaAnexo";
    }

    @Override
    public void inicializarVariable() {
        lstAnexo = new ArrayList<IODocumentoAnexo>();
        siddocanx = 0;
        ioDocumentoAnexo = new IODocumentoAnexo();
    }

    @Override
    public void limpiarObjectos() {
        // TODO Auto-generated method stub
    }


    public void getDocumentoAnexo() {
        //depurador.info("getDocumentoAnexo ==>"+ioDocumentoAnexo.getDe_det()+" "+ioDocumentoAnexo.getNu_emi());
        try {

            ioDocumentoAnexo = iDocumentoAnexoService.getDocumentoAnexo(ioDocumentoAnexo.getNu_emi(), ioDocumentoAnexo.getNu_ann(), ioDocumentoAnexo.getNu_ane());
            Utilitarios.executePdf(ioDocumentoAnexo.getBl_doc(), ioDocumentoAnexo.getDe_det());

        } catch (Exception e) {
            depurador.error(null, e);
        }
    }


    /*
     * **************************************************
     */

//    public IDocumentoAnexoService getiDocumentoAnexoService() {
//        return iDocumentoAnexoService;
//    }
//
//    public void setiDocumentoAnexoService(IDocumentoAnexoService iDocumentoAnexoService) {
//        this.iDocumentoAnexoService = iDocumentoAnexoService;
//    }

    public String getVcuo() {
        return vcuo;
    }

    public void setVcuo(String vcuo) {
        this.vcuo = vcuo;
    }

    public List<IODocumentoAnexo> getLstAnexo() {
        return lstAnexo;
    }

    public void setLstAnexo(List<IODocumentoAnexo> lstAnexo) {
        this.lstAnexo = lstAnexo;
    }

    public StreamedContent getFile() {
        return file;
    }

    public void setFile(StreamedContent file) {
        this.file = file;
    }

    public int getSiddocanx() {
        return siddocanx;
    }

    public void setSiddocanx(int siddocanx) {
        this.siddocanx = siddocanx;
    }

    public IODocumentoAnexo getIoDocumentoAnexo() {
        return ioDocumentoAnexo;
    }

    public void setIoDocumentoAnexo(IODocumentoAnexo ioDocumentoAnexo) {
        this.ioDocumentoAnexo = ioDocumentoAnexo;
    }

    @Override
    public String inicializarControladora(String x) {
        // TODO Auto-generated method stub
        return null;
    }


}
