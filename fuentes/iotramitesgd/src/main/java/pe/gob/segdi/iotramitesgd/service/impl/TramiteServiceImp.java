package pe.gob.segdi.iotramitesgd.service.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import pe.gob.segdi.iotramitesgd.bean.*;
import pe.gob.segdi.iotramitesgd.dao.IRecepcionExternaDao;
import pe.gob.segdi.iotramitesgd.dao.ITramiteDao;
import pe.gob.segdi.iotramitesgd.service.ITramiteService;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

/**
 * Objeto : TramiteServiceImp.java.
 * Descripción : Clase que implementa los métodos de la interface tramite , contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Service("iTramiteService")
public class TramiteServiceImp implements ITramiteService {
    //private static Logger depurador = Logger.getLogger(TramiteServiceImp.class.getName());
    @Autowired
    ITramiteDao iTramiteDao;
    @Autowired
    IRecepcionExternaDao iRecepcionExternaDao;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public List<CboTramite> getTramiteOficinas() throws Exception {
        return iTramiteDao.getTramiteOficinas();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void recepcionarDocumento(ConsultaDocumentoRecepcion consultaDocumentoRecepcion, Expediente expediente, DatosUsuario datosUsuario, DatosUsuarioConfiguracion datosUsuarioConfiguracion) throws Exception {

        String nu_sec_exp = iTramiteDao.getNumeroSecuenciaExpediente();
        String nu_expediente = iTramiteDao.getNumeroExpediente();
        String nu_corr_exp = iTramiteDao.getNumeroCorrelativoExpediente();

        expediente.setNusecexp(nu_sec_exp);
        //expediente.setNucorrexp(nu_expediente.split("-")[1]);
        expediente.setNucorrexp(nu_corr_exp.split("-")[1]);
        expediente.setNuexpediente(nu_expediente);
        expediente.setUscreaaudi(datosUsuario.getCempCodemp());
        iTramiteDao.insExpediente(expediente);

        Remito remito = new Remito();
        remito.setColocemi(datosUsuario.getCoLocEmi());
        //remito.setNucoremi(nu_expediente.substring(10));
        remito.setNucoremi(Integer.valueOf(nu_expediente.split("-")[1]));
        remito.setTiemi(Utilitarios.ObtenerDatosProperties("Parametros", "P_TIP_EMISION"));
        remito.setNurucemi(consultaDocumentoRecepcion.getVrucentrem());
        remito.setCogru(Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_GRUPO"));
        remito.setDeasu(consultaDocumentoRecepcion.getVasu());
        remito.setEsdocemi(consultaDocumentoRecepcion.getVobs().trim().equals("") ? Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_DOC_EMI") : Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_DOC_EMI_OBS"));
        remito.setCousecre(datosUsuario.getCodUser());
        String cdoc_tipdoc = iTramiteDao.getCodigoTipoDocumento(consultaDocumentoRecepcion.getCcodtipdoc());
        remito.setCotipdocadm(cdoc_tipdoc);
        remito.setCoempres(datosUsuario.getCempCodemp());
        remito.setNucandes(Integer.valueOf(Utilitarios.ObtenerDatosProperties("Parametros", "P_NU_CANDES")));
        remito.setNusecexp(nu_sec_exp);
        remito.setNudetexp(Integer.valueOf(Utilitarios.ObtenerDatosProperties("Parametros", "P_NU_DET_EXP")));
        remito.setNufolios(consultaDocumentoRecepcion.getSnumfol());
        remito.setDeoriemi(consultaDocumentoRecepcion.getVnomentemi());
        remito.setInbuscatexto(Utilitarios.ObtenerDatosProperties("Parametros", "P_IN_BUSCA_TEXTO"));
        remito.setCcodoriging(Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_ORI_ING"));
        remito.setDedocsig(consultaDocumentoRecepcion.getVnumdoc());
        String nu_emi = iTramiteDao.insRemito(remito);

        Archivo archivo = new Archivo();
        archivo.setNuemi(nu_emi);
        archivo.setBldoc(consultaDocumentoRecepcion.getBpdfdoc());
        archivo.setDerutaorigen(consultaDocumentoRecepcion.getVnomdoc());
        iTramiteDao.insDocumentoPrincipal(archivo);

        Anexo anexo = null;
        for (int i = 0; i < consultaDocumentoRecepcion.getLstDocAnexo().size(); i++) {
            anexo = new Anexo();
            anexo.setNuemi(nu_emi);
            anexo.setNuane(i + 1);
            anexo.setBldoc(consultaDocumentoRecepcion.getLstDocAnexo().get(i).getBdocanx());
            anexo.setDedet(consultaDocumentoRecepcion.getLstDocAnexo().get(i).getVnomdoc());
            anexo.setDerutori(consultaDocumentoRecepcion.getLstDocAnexo().get(i).getVnomdoc());
            anexo.setCousecre(datosUsuario.getCodUser());
            iTramiteDao.insDocumentoAnexo(anexo);
        }

        Destino destino = new Destino();
        destino.setNuemi(nu_emi);
        destino.setCodepdes(consultaDocumentoRecepcion.getiCodOficina());
        destino.setCoempdes(String.format("%05d", Integer.parseInt(consultaDocumentoRecepcion.getiCodTrabajador())));
        destino.setCousemod(datosUsuario.getCodUser());
        destino.setCousecre(datosUsuario.getCodUser());

        iTramiteDao.insDestino(destino);

        EstadoMovimiento estadoMovimiento = new EstadoMovimiento();
        estadoMovimiento.setNuemi(nu_emi);
        estadoMovimiento.setDeuser(datosUsuario.getCodUser());
        iTramiteDao.insEstadoMovimiento(estadoMovimiento);

        IORecepcionExterna recepcionExterna = new IORecepcionExterna();
        recepcionExterna.setSidrecext(consultaDocumentoRecepcion.getSidrecext());
        recepcionExterna.setVnumregstd(nu_expediente.split("-")[1]);
        recepcionExterna.setVuniorgstd(consultaDocumentoRecepcion.getcNomOficina());
        recepcionExterna.setCcoduniorgstd(consultaDocumentoRecepcion.getiCodOficina());
        recepcionExterna.setVusuregstd(datosUsuario.getNombreCompleto());
        recepcionExterna.setBcarstd(consultaDocumentoRecepcion.getBcarstd());
        recepcionExterna.setVobs(consultaDocumentoRecepcion.getVobs());
        recepcionExterna.setCflgest(recepcionExterna.getVobs().trim().equals("") ? Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_RECEPCION") : Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_OBSERVADO"));
        iRecepcionExternaDao.updCargoRecepcionExterna(recepcionExterna);

        final SimpleDateFormat sdfParser = new SimpleDateFormat("YYYY");
        Date trialTime = new Date();
        String vanioregstd = sdfParser.format(trialTime.getTime());
        consultaDocumentoRecepcion.setVnumregstd(nu_sec_exp);
        consultaDocumentoRecepcion.setVanioregstd(vanioregstd);
        consultaDocumentoRecepcion.setVuniorgstd(consultaDocumentoRecepcion.getcNomOficina());
        consultaDocumentoRecepcion.setVusuregstd(datosUsuario.getNombreCompleto());
        consultaDocumentoRecepcion.setVobs(consultaDocumentoRecepcion.getVobs());
        consultaDocumentoRecepcion.setCflgest(recepcionExterna.getVobs().trim().equals("") ? Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_RECEPCION") : Utilitarios.ObtenerDatosProperties("Parametros", "P_EST_OBSERVADO"));

    }

    @Override
    @Transactional(readOnly = true)
    public String[] getResponsableOficina(String iCodOficina) throws Exception {
        return iTramiteDao.getResponsableOficina(iCodOficina);
    }

    @Override
    public String getEstadoDocumento(String nu_emi, String nu_ann) throws Exception {
        return iTramiteDao.getEstadoDocumento(nu_emi, nu_ann);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public String updEstadoDocumento(String nu_emi, String nu_ann, String doc_estado_msj) throws Exception {
        String docestadomsj = "";
        iTramiteDao.updEstadoRemito(nu_emi, nu_ann, Utilitarios.ObtenerDatosProperties("Parametros", "P_ES_DOC_EMI1"));
        iTramiteDao.updEstadoDocumento(nu_emi, nu_ann, doc_estado_msj);
        docestadomsj = getEstadoDocumento(nu_emi, nu_ann);

        return docestadomsj;
    }

//	@Override
//	@Transactional(readOnly=true)
//	public String getEstadoMesaVirtual() throws Exception {
//		return iTramiteDao.getEstadoMesaVirtual();
//	}

    @Override
    @Transactional(readOnly = true)
    public List<ParametrosBean> getEstadoMesaVirtual() throws Exception {
        return iTramiteDao.getEstadoMesaVirtual();
    }

    @Override
    public List<DocumentoPorEstado> getNumeroDeDocumentos() throws Exception {
        return iTramiteDao.getNumeroDeDocumentos();
    }

    @Override
    public int habilitarDocumento(String nu_emi, String nu_ann, String es_doc_emi, String doc_estado_msj, String ob_msj, String nu_ruc_des)
            throws Exception {
        int flag = iTramiteDao.habilitarDocumento(nu_emi, nu_ann, es_doc_emi, doc_estado_msj);
        return flag;
    }


}
