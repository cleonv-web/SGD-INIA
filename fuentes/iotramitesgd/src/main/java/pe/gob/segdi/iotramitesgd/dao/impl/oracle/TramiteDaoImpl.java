package pe.gob.segdi.iotramitesgd.dao.impl.oracle;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.GeneratedKeyHolder;
import org.springframework.stereotype.Repository;
import pe.gob.segdi.iotramitesgd.bean.*;
import pe.gob.segdi.iotramitesgd.dao.ITramiteDao;
import pe.gob.segdi.iotramitesgd.dao.impl.oracle.cons.TramiteConst;
import pe.gob.segdi.iotramitesgd.util.Utilitarios;

import javax.sql.DataSource;
import java.io.Serializable;
import java.sql.PreparedStatement;
import java.util.ArrayList;
import java.util.List;


/**
 * Objeto : TramiteDaoImpl.java.
 * Descripción : Clase de servicio, contiene los métodos de acceso a la capa DAO (inserciones,actualizaciones y consultas).
 * Fecha de Creación : 02/03/2018.
 * Autor : Arturo Delgado.
 * <p>
 * ------------------------------------------------------------------------
 * Modificaciones
 * Fecha            Nombre                     Descripción
 * ------------------------------------------------------------------------
 * XXXXXXXXX      XXXXXXXXX                 XXXXXXXXXXXXXXXXXX.
 */
@Repository("iTramiteDao")
public class TramiteDaoImpl extends JdbcTemplate implements ITramiteDao, Serializable {

    private static final long serialVersionUID = 1L;
    //private static Logger depurador = Logger.getLogger(TramiteDaoImpl.class.getName());

    @Autowired
    public TramiteDaoImpl(DataSource dataSource) {
        super.setDataSource(dataSource);
    }

//	@Override
//	public String getEstadoMesaVirtual() throws Exception {
//		//depurador.info("getEstadoMesaVirtual ==>");
//		String de_par = "";
//		de_par = queryForObject(TramiteConst.GET_ESTADO_MESA_VIRTUAL,String.class,
//				Utilitarios.ObtenerDatosProperties("Parametros", "P_ESTADO_MESA_VIRTUAL"));
//		return de_par;
//	}

    @Override
    public List<ParametrosBean> getEstadoMesaVirtual() throws Exception {
        //depurador.info("getEstadoMesaVirtual ==>");

        List<ParametrosBean> lista = new ArrayList<ParametrosBean>();
        lista = query(TramiteConst.GET_ESTADO_MESA_VIRTUAL, new BeanPropertyRowMapper<ParametrosBean>(ParametrosBean.class));
        //depurador.info("listaaa ==>"+lista.size());
        return lista;
    }

    @Override
    public String getNumeroSecuenciaExpediente() throws Exception {
        //depurador.info("getSecuenciaExpediente ==>");
        String nu_sec_exp = "";
        nu_sec_exp = queryForObject(TramiteConst.GET_NUMERO_SECUENCIA_EXPEDIENTE, String.class);
        return nu_sec_exp;
    }

    @Override
    public String getNumeroExpediente() throws Exception {
        //depurador.info("getNumeroExpediente ==>");
        String coddepemi = getCodigoDependenciaTramite();
        //depurador.info("coddepemi ==>"+coddepemi);
        String nu_expediente = queryForObject(TramiteConst.GET_NUMERO_EXPEDIENTE, String.class,
                coddepemi);

        //depurador.info("nu_expediente ==>"+nu_expediente);
        return nu_expediente;
    }

    @Override
    public String getNumeroCorrelativoExpediente() throws Exception {
        //depurador.info("getCorrelativoExpediente ==>");
        String coddepemi = getCodigoDependenciaTramite();
        String nu_corr_exp = queryForObject(TramiteConst.GET_NUMERO_CORRELATIVO_EXPEDIENTE, String.class,
                coddepemi);

        return nu_corr_exp.trim();
    }

    @Override
    public void insExpediente(Expediente expediente) throws Exception {
        //depurador.info("insExpediente ==>");
        String coddepemi = getCodigoDependenciaTramite();
        update(TramiteConst.INS_EXPEDIENTE,
                expediente.getNusecexp(),
                coddepemi,
                expediente.getNucorrexp(),
                expediente.getNuexpediente(),
                expediente.getUscreaaudi(),
                expediente.getUscreaaudi()
        );
    }

    @Override
    public List<CboTramite> getTramiteOficinas() throws Exception {
        //depurador.info("getTramiteOficinas ==>");
        List<CboTramite> lstOficina = new ArrayList<CboTramite>();
        lstOficina = query(TramiteConst.GET_TRAMITE_OFICINAS, new BeanPropertyRowMapper<CboTramite>(CboTramite.class));
        return lstOficina;
    }

    @Override
    public String getCodigoTipoDocumento(String cCodTipDoc) throws Exception {
        //depurador.info("getCodigoTipoDocumento ==>"+cCodTipDoc);
        String cdoc_tipdoc = "";
        //depurador.info("SQL ==>"+TramiteConst.GET_CODIGO_TIPO_DOCUMENTO);
        cdoc_tipdoc = queryForObject(TramiteConst.GET_CODIGO_TIPO_DOCUMENTO, String.class,
                cCodTipDoc
        );
        //depurador.info("cCodTipDoc ==>"+cCodTipDoc);
        return cdoc_tipdoc;
    }

    @Override
    public String[] getResponsableOficina(String iCodOficina) throws Exception {
        //depurador.info("getResponsableOficina ==>"+iCodOficina);
        String responsable = queryForObject(TramiteConst.GET_RESPONSABLE_OFICINA, String.class, iCodOficina);
        //depurador.info("responsable ==>"+responsable);
        String[] datos = responsable.split("-");
        return datos;
    }

    @Override
    public String insRemito(Remito remito) throws Exception {
        //depurador.info("insRemito ==>");
        String codigodependencia = getCodigoDependencia(remito.getCousecre());
        String coddepemi = getCodigoDependenciaTramite();
        //depurador.info("CO_DEP_EMI ==>"+coddepemi);
        String codigoempleadoemisor = getCodigoEmpleadoDependencia(codigodependencia);
        String nu_cor_doc = getCorrelativoDocumento(coddepemi, remito.getTiemi());

        //depurador.info("UK_TDTV_REMITOS_01 CO_DEP_EMI ==>"+coddepemi+"  TI_EMI => "+Utilitarios.ObtenerDatosProperties("Parametros", "P_TIP_EMISION")+"  CO_TIP_DOC_ADM =>"+remito.getCotipdocadm()+"  NU_COR_DOC =>"+nu_cor_doc);
        //depurador.info("UK_TDTV_REMITOS_02 CO_DEP_EMI ==>"+coddepemi+"  CO_GRU =>"+Utilitarios.ObtenerDatosProperties("Parametros", "P_COD_GRUPO")+"  NU_COR_EMI =>"+remito.getNucoremi());

        GeneratedKeyHolder keyHolder = new GeneratedKeyHolder();
        String id_column = "NU_EMI";
        update(con -> {
            PreparedStatement ps = con.prepareStatement(TramiteConst.INS_REMITO, new String[]{id_column});
            ps.setInt(1, remito.getNucoremi());
            ps.setString(2, remito.getColocemi());
            ps.setString(3, coddepemi);
            ps.setString(4, remito.getTiemi());
            ps.setString(5, codigoempleadoemisor);
            ps.setString(6, remito.getNurucemi());
            ps.setString(7, remito.getCogru());
            ps.setString(8, remito.getDeasu());
            ps.setString(9, remito.getEsdocemi());
            ps.setString(10, remito.getCousecre());
            ps.setString(11, remito.getCousecre());
            ps.setString(12, remito.getCotipdocadm());
            ps.setString(13, remito.getCoempres());
            ps.setInt(14, remito.getNucandes());
            ps.setString(15, nu_cor_doc);
            ps.setString(16, remito.getDedocsig());
            ps.setString(17, remito.getNusecexp());
            ps.setInt(18, remito.getNudetexp());
            ps.setInt(19, remito.getNufolios());
            ps.setString(20, remito.getDeoriemi());
            ps.setString(21, remito.getInbuscatexto());
            ps.setString(22, codigodependencia);
            ps.setString(23, remito.getCcodoriging());
            return ps;
        }, keyHolder);

        String id = (String) keyHolder.getKeys().get(id_column);

        return id;
    }

    @Override
    public void insDocumentoPrincipal(Archivo archivo) throws Exception {
        //depurador.info("insDocumentoPrincipal ==>"+archivo.getNuemi());
        update(TramiteConst.INS_ARCHIVO,
                archivo.getNuemi(),
                archivo.getBldoc(),
                archivo.getDerutaorigen()
        );
    }

    @Override
    public void insDocumentoAnexo(Anexo anexo) throws Exception {
        //depurador.info("insDocumentoAnexo ==>");
        update(TramiteConst.INS_ANEXO,
                anexo.getNuemi(),
                anexo.getNuane(),
                anexo.getDedet(),
                anexo.getBldoc(),
                anexo.getDerutori(),
                anexo.getCousecre(),
                anexo.getCousecre());
    }

    @Override
    public void insDestino(Destino destino) throws Exception {
        //depurador.info("insDestino ==>");

        update(TramiteConst.INS_DESTINO,
                destino.getNuemi(),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_NU_DES"),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_TI_DES"),
                destino.getCodepdes(),
                destino.getCoempdes(),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_ES_ELI"),
                destino.getCousemod(),
                destino.getCousecre(),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_ES_DOC_REC"),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_ES_ENV_POR_TRA"),
                destino.getCodepdes(),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_CO_MOT")
        );

    }

    @Override
    public void insEstadoMovimiento(EstadoMovimiento estadoMovimiento) throws Exception {
        //depurador.info("insEstadoMovimiento ==>");
        update(TramiteConst.INS_ESTADO_MOVIMIENTO,
                estadoMovimiento.getNuemi(),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_TI_PROCESO"),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_ES_PROCESO"),
                estadoMovimiento.getDeuser(),
                Utilitarios.ObtenerDatosProperties("Parametros", "P_DE_NAMEPC")
        );
    }

    public String getCodigoDependencia(String cousuario) throws Exception {
        //depurador.info(" getCodigoDependencia ==>");
        String cod_dependencia = queryForObject(TramiteConst.GET_CODIGO_DEPENDENCIA, String.class,
                cousuario);
        return cod_dependencia;
    }

    public String getCodigoDependenciaTramite() throws Exception {
        //depurador.info(" getCodigoDependenciaTramite ==>");
        String cod_dependencia = queryForObject(TramiteConst.GET_CODIGO_DEPENDENCIA_TRAMITE, String.class);
        //depurador.info("cod_dependencia ==>");
        return cod_dependencia;
    }

    public String getCodigoLocal(String codependencia) throws Exception {
        //depurador.info(" getCodigoLocal ==>");
        String ccod_local = queryForObject(TramiteConst.GET_CODIGO_EMPLEADO_DEPENDENCIA, String.class,
                codependencia);
        return ccod_local;
    }

    public String getCodigoEmpleadoDependencia(String codependencia) throws Exception {
        //depurador.info(" getCodigoEmpleadoDependencia ==>");
        String co_emp_emi = queryForObject(TramiteConst.GET_CODIGO_EMPLEADO_DEPENDENCIA, String.class,
                codependencia);
        return co_emp_emi;
    }

    public String getCorrelativoDocumento(String co_dep_emi, String ti_emi) throws Exception {
        //depurador.info("getCorrelativoDocumento ==>");
        String nu_cor_doc = "";
        nu_cor_doc = queryForObject(TramiteConst.GET_CORRELATIVO_DOCUMENTO, String.class,
                co_dep_emi,
                ti_emi);
        return nu_cor_doc;
    }

    @Override
    public String getEstadoDocumento(String nu_emi, String nu_ann) throws Exception {
        //depurador.info("getEstadoDocumento ==>"+nu_emi+" - "+nu_ann);
        String nu_cor_doc = "";
        nu_cor_doc = queryForObject(TramiteConst.GET_ESTADO_DOCUMENTO, String.class,
                nu_emi,
                nu_ann);
        return nu_cor_doc;
    }

    @Override
    public void updEstadoDocumento(String nu_emi, String nu_ann, String doc_estado_msj) throws Exception {
        //depurador.info("updEstadoDocumento ==>");
        update(TramiteConst.UPD_ESTADO_DOCUMENTO,
                doc_estado_msj,
                nu_emi,
                nu_ann
        );
    }

    @Override
    public List<DocumentoPorEstado> getNumeroDeDocumentos() throws Exception {
        //depurador.info("getNumeroDeDocumentos ==>");
        List<DocumentoPorEstado> numeroDocumetosPorEstado = new ArrayList<DocumentoPorEstado>();
        numeroDocumetosPorEstado = query(TramiteConst.GET_NUMERO_DE_DOCUMENTOS, new BeanPropertyRowMapper<DocumentoPorEstado>(DocumentoPorEstado.class));
        return numeroDocumetosPorEstado;
    }

    @Override
    public void updEstadoRemito(String nu_emi, String nu_ann, String es_doc_emi) throws Exception {
        //depurador.info("updEstadoRemito ==>"+es_doc_emi);
        update(TramiteConst.UPD_ESTADO_REMITO,
                es_doc_emi,
                nu_emi,
                nu_ann
        );
    }

    @Override
    public void updTipoEnvio(String nu_emi, String nu_ann) throws Exception {
        //depurador.info("updTipoEnvio ==>");
        update(TramiteConst.UPD_TIPO_ENVIO,
                nu_emi,
                nu_ann
        );
    }

    @Override
    public int habilitarDocumento(String nu_emi, String nu_ann, String es_doc_emi, String doc_estado_msj
    ) throws Exception {
        //depurador.info("habilitarDocumento ==>");
        int flag = update(TramiteConst.HABILITAR_DOCUMENTO,
                es_doc_emi,
                doc_estado_msj,
                nu_emi,
                nu_ann
        );
        return flag;
    }

    @Override
    public void observarDocumento(String nu_emi, String nu_ann, String ob_msj,
                                  String nu_ruc_des) throws Exception {
        //depurador.info("observarDocumento ==>"+ob_msj);
        update(TramiteConst.OBSERVAR_DOCUMENTO,
                ob_msj,
                nu_emi,
                nu_ann,
                nu_ruc_des
        );
    }


}
