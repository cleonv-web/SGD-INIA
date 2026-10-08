/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.ConAnioBean;
import pe.gob.onpe.tramitedoc.bean.ConDestinatarioBean;
import pe.gob.onpe.tramitedoc.bean.ConDiaBean;
import pe.gob.onpe.tramitedoc.bean.ConEstadoBean;
import pe.gob.onpe.tramitedoc.bean.ConExpedienteBean;
import pe.gob.onpe.tramitedoc.bean.ConMesBean;
import pe.gob.onpe.tramitedoc.bean.ConRemitenteBean;
import pe.gob.onpe.tramitedoc.bean.ConTipoDocumentoBean;
import pe.gob.onpe.tramitedoc.bean.ConTipoRemitenteBean;

/**
 *
 * @author NGilt
 */
public interface ConMaestrosDao {

    public List<ConAnioBean> listConAnio();

    public List<ConMesBean> listConMes();

    public List<ConDiaBean> listConDia();

    public List<ConDestinatarioBean> listConDestinatarios(String codDependencia);

    public List<ConTipoRemitenteBean> listConTipoRemitente();

    public List<ConExpedienteBean> listConExpediente(String codDependencia);

    public List<ConRemitenteBean> listConRemitente(String codDependencia);

    public List<ConEstadoBean> listConEstado();

    public List<ConTipoDocumentoBean> listConTipoDocumento(String codDependencia);
    
}
