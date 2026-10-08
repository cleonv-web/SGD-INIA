/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.ConAnioBean;
import pe.gob.onpe.tramitedoc.bean.ConConfigBusquedaEmiDocBean;
import pe.gob.onpe.tramitedoc.bean.ConDestinatarioBean;
import pe.gob.onpe.tramitedoc.bean.ConDiaBean;
import pe.gob.onpe.tramitedoc.bean.ConEstadoBean;
import pe.gob.onpe.tramitedoc.bean.ConExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.ConMesBean;
import pe.gob.onpe.tramitedoc.bean.ConRecepcionDet;
import pe.gob.onpe.tramitedoc.bean.ConRemitenteBean;
import pe.gob.onpe.tramitedoc.bean.ConTipoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ConTipoRemitenteBean;
import pe.gob.onpe.tramitedoc.dao.ConMaestrosDao;

/**
 *
 * @author NGilt
 */
@Service(value = "conReferencedData")
public class ConReferencedData {

    @Autowired
    private ConMaestrosDao conMaestrosDao;
    private List<ConAnioBean> conAnioList;
    private List<ConMesBean> conMesList;
    private List<ConDiaBean> conDiaList;
    private List<ConDestinatarioBean> conDestinatarioList;
    private List<ConTipoRemitenteBean> conTipoRemitenteList;
    private List<ConExpedienteBean> conExpedienteList;
    private List<ConRemitenteBean> conRemitenteList;
    private List<ConEstadoBean> conEstadoList;
    private List<ConTipoDocumentoBean> conTipoDocumentoList;

    public List<ConAnioBean> getConAnioList() {
        this.conAnioList = conMaestrosDao.listConAnio();
        return conAnioList;
    }

    public List<ConMesBean> getConMesList() {
        this.conMesList = conMaestrosDao.listConMes();
        return conMesList;
    }

    public List<ConDiaBean> getConDiaList() {
        this.conDiaList = conMaestrosDao.listConDia();
        return conDiaList;
    }

    public List<ConDestinatarioBean> getConDestinatarioList(String codDependencia) {
        this.conDestinatarioList = conMaestrosDao.listConDestinatarios(codDependencia);
        return conDestinatarioList;
    }

    public List<ConTipoRemitenteBean> getConTipoRemitenteList() {
        this.conTipoRemitenteList = conMaestrosDao.listConTipoRemitente();
        return conTipoRemitenteList;
    }

    public List<ConExpedienteBean> getConExpedienteList(String codDependencia) {
        this.conExpedienteList = conMaestrosDao.listConExpediente(codDependencia);
        return conExpedienteList;
    }

    public List<ConRemitenteBean> getConRemitenteList(String codDependencia) {
        this.conRemitenteList = conMaestrosDao.listConRemitente(codDependencia);
        return conRemitenteList;
    }

    public List<ConEstadoBean> getConEstadoList() {
        this.conEstadoList = conMaestrosDao.listConEstado();
        return conEstadoList;
    }

    public List<ConTipoDocumentoBean> getConTipoDocumentoList(String codDependencia) {
        this.conTipoDocumentoList = conMaestrosDao.listConTipoDocumento(codDependencia);
        return conTipoDocumentoList;
    }
}
