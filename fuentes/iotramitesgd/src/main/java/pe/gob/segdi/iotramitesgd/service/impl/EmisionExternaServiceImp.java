package pe.gob.segdi.iotramitesgd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.segdi.iotramitesgd.bean.ConsultaDocumentoEmision;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExterna;
import pe.gob.segdi.iotramitesgd.bean.IOEmisionExternaBean;
import pe.gob.segdi.iotramitesgd.dao.IEmisionExternaDao;
import pe.gob.segdi.iotramitesgd.dao.ITramiteDao;
import pe.gob.segdi.iotramitesgd.service.IDocumentoExternoService;
import pe.gob.segdi.iotramitesgd.service.IEmisionExternaService;
import pe.gob.segdi.iotramitesgd.service.ITramiteService;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;

import java.util.List;

/**
 * Objeto : EmisionExternaServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface emision externa , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Service("iEmisionExternaService")
public class EmisionExternaServiceImp implements IEmisionExternaService {
    //private static Logger depurador = Logger.getLogger(EmisionExternaServiceImp.class.getName());
    @Autowired
    IEmisionExternaDao iEmisionExternaDao;
    @Autowired
    IDocumentoExternoService iDocumentoExternoService;
    @Autowired
    ITramiteDao iTramiteDao;
    @Autowired
    ITramiteService iTramiteService;


    @Override
    public List<ConsultaDocumentoEmision> bsqEmisionExterna(String vrucentdst, String cflgest, String codependencia, String dfecdesde, String dfechasta) throws Exception {
        return iEmisionExternaDao.bsqEmisionExterna(vrucentdst, cflgest, codependencia, dfecdesde, dfechasta);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int insCuoEmisionExterna(String vcuo, int sidemiext) throws Exception {
        int flag = iEmisionExternaDao.insCuoEmisionExterna(vcuo, sidemiext);
        return flag;
    }

    @Override
    public void updEstadoEmisionExterna(int sidemiext, String cflgest, String nu_emi, String nu_ann, String doc_estado_msj) throws Exception {
        iEmisionExternaDao.updEstadoEnvio(sidemiext, Utilitarios.ObtenerDatosProperties("Parametros", "P_FLG_ENVIO"));
        iEmisionExternaDao.updEstadoEmisionExterna(sidemiext, cflgest);
        iTramiteDao.updEstadoDocumento(nu_emi, nu_ann, doc_estado_msj);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int updCargoEmisionExterna(IOEmisionExterna emisionExterna) throws Exception {
        ////depurador.info("updCargoEmisionExterna ==>");
        int flgRecEmiExt = 0;
        flgRecEmiExt = iEmisionExternaDao.updCargoEmisionExterna(emisionExterna);
        return flgRecEmiExt;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void updEstadoEnvio(int sidemiext, String cflgenv) throws Exception {
        iEmisionExternaDao.updEstadoEnvio(sidemiext, cflgenv);

    }

    @Override
    public String obtEstadoEnvio(int sidemiext) throws Exception {
        return iEmisionExternaDao.obtEstadoEnvio(sidemiext);
    }

    @Override
    public String obtCuo(int sidemiext) throws Exception {
        return iEmisionExternaDao.obtCuo(sidemiext);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String anularRecepcion(String vnumregstd, String vanioregstd, String docestmsj, int sidemiext, String vdesanu) throws Exception {
        //String docestadomsj = iTramiteService.updEstaDocumento(vnumregstd, vanioregstd, docestmsj);
        iTramiteDao.updEstadoRemito(vnumregstd, vanioregstd, Utilitarios.ObtenerDatosProperties("Parametros", "P_ES_DOC_EMI3"));
        //iTramiteDao.updTipoEnvio(vnumregstd, vanioregstd);
        //iTramiteDao.updEstadoDocumento(vnumregstd, vanioregstd, docestmsj);
        iEmisionExternaDao.anularRecepcion(sidemiext, vdesanu);
        String docestadomsj = iTramiteDao.getEstadoDocumento(vnumregstd, vanioregstd);
        return docestadomsj;
    }

    @Override
    public ConsultaDocumentoEmision getEmisionExterna(int sidemiext) throws Exception {
        return iEmisionExternaDao.getEmisionExterna(sidemiext);
    }

    @Override
    public List<ConsultaDocumentoEmision> getEmisionExternaPendientes(String vnumregstd, String vanioregstd) throws Exception {

        return iEmisionExternaDao.getEmisionExternaPendientes(vnumregstd, vanioregstd);
    }

    /////////////////////////////////////////////////////////////
    public List<IOEmisionExternaBean> getDocumentosEnviados(String vrucentrec) throws Exception {
        return iEmisionExternaDao.getDocumentosEnviados(vrucentrec);
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int actualizarCargoDespacho(IOEmisionExternaBean ioEmisionExternaBean) throws Exception {
        //depurador.info("actualizarCargoDespacho ==>");
        int flag = iEmisionExternaDao.actualizarCargoDespacho(ioEmisionExternaBean);
        return flag;
    }

    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public int actualizarEstadoDespacho(String cflgest) throws Exception {
        //depurador.info("actualizarEstadoDespacho ==>");
        int flag = iEmisionExternaDao.actualizarEstadoDespacho(cflgest);
        return flag;
    }

    public String getNumeroDocumento(String vcuo) throws Exception {
        return iEmisionExternaDao.getNumeroDocumento(vcuo);
    }

    public List<IOEmisionExternaBean> getEntidadEnviados() throws Exception {
        return iEmisionExternaDao.getEntidadEnviados();
    }

    public void actualizarCargo(int sidemiext, byte[] cargo) throws Exception {
        iEmisionExternaDao.actualizarCargo(sidemiext, cargo);
    }
}
