/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.dao.impl.sqlserver;

import java.sql.Types;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.SqlOutParameter;
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.SqlLobValue;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository; 
import pe.gob.onpe.mpvdoc.bean.CiudadanoBean;
import pe.gob.onpe.mpvdoc.bean.ConsultaDniBean;
import pe.gob.onpe.mpvdoc.bean.ConsultaSunatBean;
import pe.gob.onpe.mpvdoc.bean.DocumentoBean;
import pe.gob.onpe.mpvdoc.bean.DocumentoFilesStructBean; 
import pe.gob.onpe.mpvdoc.bean.MpvdocBean; 
import pe.gob.onpe.mpvdoc.bean.ProveedorBean;
import pe.gob.onpe.mpvdoc.bean.ResultMpvdocBean;
import pe.gob.onpe.mpvdoc.bean.TipoDocumentoBean;
import pe.gob.onpe.mpvdoc.bean.UbigeoBean;
import pe.gob.onpe.mpvdoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.mpvdoc.dao.MpvdocDao;

/**
 *
 * @author RATAYAURI
 */
@Repository("mpvdocDao")
@Profile("sqlserver")
public class MpvdocDaoImpl extends SimpleJdbcDaoBase implements MpvdocDao{
    
    private SimpleJdbcCall spdataSource;
    
    

    @Override
    public List<TipoDocumentoBean> listTipoDocumento() { 
        List<TipoDocumentoBean> list = null; 
       this.spdataSource = new SimpleJdbcCall(this.dataSource)
               .withProcedureName("idosgd.PK_SGD_MESA_VIRTUAL_LIST_TIP_DOCUMENTO")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames()                        
                .declareParameters()
                .returningResultSet("recordSets", BeanPropertyRowMapper.newInstance(TipoDocumentoBean.class));;
                SqlParameterSource in = new MapSqlParameterSource() ;       
        try {               
            Map<String, Object> dbResults = this.spdataSource.execute(in);
            list = (List<TipoDocumentoBean>)dbResults.get("recordSets");
            this.spdataSource =null;            
        } catch (Exception ex) { 
            ex.printStackTrace();
        }          
        return list;
    }
 @Override
    public List<UbigeoBean> listDepartamento() {
            StringBuilder sql = new StringBuilder();
        sql.append("select distinct ubdep codigo ,ltrim(rtrim(nodep)) descripcion from IDOSGD.idtubias where  UBLOC = '00'  AND CCOD_TIPO_UBI IN ('NACIONAL','LOCAL') order by 2 "); 
        
        List<UbigeoBean> list = null;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UbigeoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
     @Override
    public List<UbigeoBean> listProvincia(String codDep) {
            StringBuilder sql = new StringBuilder();
        sql.append("select distinct ubprv codigo ,ltrim(rtrim(noprv)) descripcion  from IDOSGD.idtubias where  UBPRV <> '00' and ubdep='"+codDep+"' order by 2 "); 
        
        List<UbigeoBean> list = null;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UbigeoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
      @Override
    public List<UbigeoBean> listDistrito(String codDepProv) {
            StringBuilder sql = new StringBuilder();
        sql.append("select distinct ubdis codigo ,ltrim(rtrim(nodis)) descripcion  from IDOSGD.idtubias where  ubdis <> '00' and ubdep+ubprv='"+codDepProv+"' order by 2 "); 
        
        List<UbigeoBean> list = null;

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UbigeoBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }

        return list;
    }
    @Override
    public CiudadanoBean getCiudadano(String pnuDoc) {
        StringBuffer sql = new StringBuffer();
        
       sql.append("select I.NULEM NU_DOCUMENTO,RTRIM(I.DEAPP) PATERNO,RTRIM(I.DEAPM) MATERNO ,RTRIM(I.DENOM)  NOMBRE,\n"
                + "COALESCE(I.UBDEP,'') ID_DEPARTAMENTO, COALESCE(I.UBPRV,'') ID_PROVINCIA, COALESCE(I.UBDIS,'') ID_DISTRITO, COALESCE(I.DEDOMICIL,'') DE_DIRECCION, COALESCE(I.DEEMAIL,'') DE_CORREO,COALESCE(I.DETELEFO,'') TELEFONO \n"
                //+ ",I.UBDEP, I.UBPRV,I.UBDIS,I.DEDOMICIL,I.DEEMAIL, I.DETELEFO ,  trim(UB.NODEP)||'/'||trim(UB.NOPRV)||'/'||trim(UB.NODIS) ubigeo \n"
                + "from IDOSGD.IDTANIRS I "//LEFT JOIN IDOSGD.IDTUBIAS UB ON UB.UBDEP=I.UBDEP AND UB.UBPRV=I.UBPRV AND UB.UBDIS=I.UBDIS \n"
                + "where\n"
                + "NULEM = ?\n"
                + "order by 2");
 
        CiudadanoBean ciudadanoBean = null;
        try {
            ciudadanoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(CiudadanoBean.class),
                    new Object[]{pnuDoc});
        } catch (EmptyResultDataAccessException e) {
            ciudadanoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return ciudadanoBean;
    }

    @Override
    public String getPassDniPide(String pctabCodTab) {
      StringBuffer sql = new StringBuffer();
        sql.append("SELECT TOP 1 CELE_DESCOR\n" +
                   "FROM   IDOSGD.SI_ELEMENTO\n" +
                   "WHERE  CTAB_CODTAB = ?  ");

        String pkExp = null;
        try {
            pkExp = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pctabCodTab });            
        } catch (EmptyResultDataAccessException e) {
            pkExp = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return pkExp;
    }

    @Override
    public String getParametros(String pco_param) {
       StringBuffer sql = new StringBuffer();
        String result = null;
        sql.append("select de_par\n" +
                    "from IDOSGD.TDTR_PARAMETROS\n" +
                    "where CO_PAR = ?");        
        
        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pco_param});
        } catch (EmptyResultDataAccessException e) {
            result = " ";
        } catch (Exception e) {
            result = " ";
            e.printStackTrace();
        }
        return result;   
    }

    @Override
    public String insPideCiudadano(ConsultaDniBean ciudadano, String nudni) {
        String vReturn      = "NO_OK";
        StringBuffer sql    = new StringBuffer();
       
        
        sql.append("insert into IDOSGD.TDTX_ANI_SIMIL ");
        sql.append("(NULEM, DEAPP, DEAPM, DENOM) ");
        sql.append("values (?, ?, ?, ?)");

        try {        
            //El campo Estado no se incluye en el bloque del insert pq es un valor default en la tabla
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                nudni, ciudadano.getApPrimer(), ciudadano.getApSegundo(), ciudadano.getPrenombres()
            });
            
            vReturn = "OK";
            
        } catch (DuplicateKeyException con) {
            vReturn = "Número de documento duplicado.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn; 
    }

    @Override
    public ProveedorBean getProveedor(String pnuRuc) {
        StringBuffer sql = new StringBuffer();
         
        sql.append("SELECT CPRO_RUC NU_RUC, CPRO_RAZSOC DESCRIPCION, \n"
                + "COALESCE(CUBI_CODDEP,'') ID_DEPARTAMENTO, COALESCE(CUBI_CODPRO,'') ID_PROVINCIA, COALESCE(CUBI_CODDIS,'') ID_DISTRITO, COALESCE(CPRO_DOMICIL,'') DE_DIRECCION, COALESCE(CPRO_EMAIL,'') DE_CORREO,COALESCE(CPRO_TELEFO,'') TELEFONO \n"
                +"FROM IDOSGD.LG_PRO_PROVEEDOR \n"
                +"where CPRO_RUC=? ");
        ProveedorBean proveedorBean=null;
        try {
            proveedorBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(ProveedorBean.class),
                    new Object[]{pnuRuc});
        } catch (EmptyResultDataAccessException e) {
            proveedorBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return proveedorBean;
    }

    @Override
    public String insPideProveedor(ConsultaSunatBean proveedor) {
       String vReturn = "NO_OK";
        StringBuilder sql = new StringBuilder();
        sql.append("insert into IDOSGD.LG_PRO_PROVEEDOR ");
        sql.append("(cpro_ruc, dpro_fecins ,cpro_razsoc, cpro_domicil, cubi_coddep, cubi_codpro, cubi_coddis) ");
        sql.append("values (?,GETDATE(),?,?,(select ubdep from IDOSGD.idtubias where ubdep+ubprv+ubdis=?),(select ubprv from IDOSGD.idtubias where ubdep+ubprv+ubdis=?),(select ubdis from IDOSGD.idtubias where ubdep+ubprv+ubdis=?)) ");
        try {

            if (proveedor.getDdp_nomzon().equals("-"))
            {
                proveedor.setDdp_nomzon("");
            }
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                proveedor.getDdp_numruc(), proveedor.getDdp_nombre().trim(), proveedor.getDesc_tipvia().trim()+" "+proveedor.getDdp_nomvia().trim()+" "+proveedor.getDdp_numer1().trim()+" "+proveedor.getDdp_nomzon(),
                proveedor.getDdp_ubigeo(), proveedor.getDdp_ubigeo(), proveedor.getDdp_ubigeo()
            });
            vReturn = "OK";
            
        } catch (DuplicateKeyException con) {
            vReturn = "Número de RUC duplicado."; 
        } catch (Exception e) {
            e.printStackTrace(); 
        }
        return vReturn;
    }

    @Override
    public ResultMpvdocBean getGrabarExpediente(MpvdocBean mpvdocBean) {
          ResultMpvdocBean resultado= new ResultMpvdocBean();
        final LobHandler lobhandler = new DefaultLobHandler();
        spdataSource = new SimpleJdbcCall(this.dataSource)
                        .withSchemaName("idosgd")
                        .withProcedureName("PK_SGD_MESA_VIRTUAL_CIUDADANO")
                        .withoutProcedureColumnMetaDataAccess()
                        .useInParameterNames("p_tipoper", "p_dni", "p_pat","p_mat","p_nom"
                                ,"p_ruc","p_raz","p_tipodoc","p_nrodoc","p_asu"
                                ,"p_arc","p_arcdat","p_dir","p_folio","p_correo","p_tel","p_dep","p_pro","p_dis","resultado")
                        .declareParameters(                                
                                new SqlParameter("p_tipoper", Types.VARCHAR),
                                new SqlParameter("p_dni", Types.VARCHAR),
                                new SqlParameter("p_pat", Types.VARCHAR),
                                new SqlParameter("p_mat", Types.VARCHAR),
                                new SqlParameter("p_nom", Types.VARCHAR),
                                new SqlParameter("p_ruc", Types.VARCHAR),
                                new SqlParameter("p_raz", Types.VARCHAR),
                                new SqlParameter("p_tipodoc", Types.VARCHAR),
                                new SqlParameter("p_nrodoc", Types.VARCHAR),
                                new SqlParameter("p_asu", Types.VARCHAR),
                                new SqlParameter("p_arc", Types.VARCHAR),
                                new SqlParameter("p_arcdat", Types.BLOB),
                                new SqlParameter("p_dir", Types.VARCHAR),
                                new SqlParameter("p_folio", Types.VARCHAR),
                                new SqlParameter("p_correo", Types.VARCHAR),
                                new SqlParameter("p_tel", Types.VARCHAR),
                                new SqlParameter("p_dep", Types.VARCHAR),
                                new SqlParameter("p_pro", Types.VARCHAR),
                                new SqlParameter("p_dis", Types.VARCHAR),
                                new SqlOutParameter("resultado", Types.VARCHAR));
                
                SqlParameterSource in = new MapSqlParameterSource()
                        .addValue("p_tipoper", mpvdocBean.getIdTipoPersona())
                        .addValue("p_dni", mpvdocBean.getDni())
                        .addValue("p_pat", mpvdocBean.getPat())
                        .addValue("p_mat", mpvdocBean.getMat())
                        .addValue("p_nom", mpvdocBean.getNom())
                        .addValue("p_ruc", mpvdocBean.getIdTipoPersona().contains("02")?mpvdocBean.getRuc():"")
                        .addValue("p_raz", mpvdocBean.getIdTipoPersona().contains("02")?mpvdocBean.getRaz():"")
                        .addValue("p_tipodoc", mpvdocBean.getTipoDocumento())
                        .addValue("p_nrodoc", mpvdocBean.getNrodoc())
                        .addValue("p_asu", mpvdocBean.getAsu())
                        .addValue("p_arc", mpvdocBean.getNombreArchivo())
                        .addValue("p_arcdat", new SqlLobValue(mpvdocBean.getArchivoBytes(),lobhandler))  
                        .addValue("p_dir", mpvdocBean.getDir())
                        .addValue("p_folio", mpvdocBean.getFolios())
                        .addValue("p_correo", mpvdocBean.getCorreo())
                        .addValue("p_tel", mpvdocBean.getTel())
                        .addValue("p_dep", mpvdocBean.getDep())
                        .addValue("p_pro", mpvdocBean.getPro())
                        .addValue("p_dis", mpvdocBean.getDis());
                 
        try{             
                Map  out = spdataSource.execute(in);
                String result = (String)out.get("resultado");
                String [] resulArray=result.split("@");
                if(resulArray!=null && resulArray.length>0){
                    resultado.setResultado(resulArray[0]); 
                    if (resultado.getResultado().contains("OK")){
                        resultado.setExpediente(resulArray[1]); 
                        resultado.setAnio(resulArray[2]); 
                        resultado.setEmision(resulArray[3]);
                        if(mpvdocBean.getFilesProcess()!=null && mpvdocBean.getFilesProcess().size()>0){
                            for (DocumentoFilesStructBean filesProces : mpvdocBean.getFilesProcess()){
                                getAnexos(filesProces,resultado.getAnio(),resultado.getEmision());
                            }
                        }
                    }
                    else {
                        resultado.setResultado(resulArray[1]); 
                    }
                }
                else {
                    resultado.setResultado("Se generó un error al guardar la información.");
                }
        } catch (Exception e) {
            e.printStackTrace();
            resultado.setResultado("Se generó un error al guardar la información. Intenta nuevamente en unos minutos.");
        }
        return resultado;
    }
  private void getAnexos(DocumentoFilesStructBean filesProces, String anio, String emision){
       final LobHandler lobhandler = new DefaultLobHandler();
        spdataSource = new SimpleJdbcCall(this.dataSource)
                        .withSchemaName("idosgd")
                        .withProcedureName("PK_SGD_MESA_VIRTUAL_CIUDADANO_ANEXOS")
                        .withoutProcedureColumnMetaDataAccess()
                        .useInParameterNames("p_anio", "p_emision", "p_titulo","p_anexos","p_arcdat") 
                        .declareParameters(new SqlParameter("p_anio", Types.VARCHAR),
                                new SqlParameter("p_emision", Types.VARCHAR),
                                new SqlParameter("p_titulo", Types.VARCHAR),
                                new SqlParameter("p_anexos", Types.INTEGER),
                                new SqlParameter("p_arcdat", Types.BLOB));                  
                SqlParameterSource in = new MapSqlParameterSource()
                        .addValue("p_anio", anio)
                        .addValue("p_emision", emision)
                        .addValue("p_titulo", filesProces.getVctit())
                        .addValue("p_anexos", filesProces.getInid())
                        .addValue("p_arcdat", new SqlLobValue(filesProces.getVcarcda(), lobhandler)) ; 
        try{             
               spdataSource.execute(in);           
        } catch (Exception e) {
            e.printStackTrace();             
        }
   }
    @Override
    public String RegisternotificarCorreo(String v_nom, String p_Para, String detalleDoc, String asunto, String cuerpo) {
         String mensaje = "NO_OK"; 
      SimpleJdbcCall spNotificarCorreo = new SimpleJdbcCall(this.dataSource)
               .withSchemaName("idosgd")
              .withProcedureName("PK_SGD_TRAMITE_CREA_NOTIFICACION_LOG_MPVC_INSERTA")
            .withoutProcedureColumnMetaDataAccess()
            .useInParameterNames("p_nombre", "p_para","p_detalle","p_asunto","p_cuerpo")
            .declareParameters(
                new SqlParameter("p_nombre", Types.VARCHAR),
                new SqlParameter("p_para", Types.VARCHAR),
                new SqlParameter("p_detalle", Types.VARCHAR),
                new SqlParameter("p_asunto", Types.VARCHAR),
                new SqlParameter("p_cuerpo", Types.VARCHAR));
                
        SqlParameterSource in = new MapSqlParameterSource()        
                .addValue("p_nombre", v_nom)
                .addValue("p_para", p_Para)
                .addValue("p_detalle", detalleDoc)
                .addValue("p_asunto", asunto)
                .addValue("p_cuerpo", cuerpo);
       
       try{
           spNotificarCorreo.execute(in);
           mensaje = "OK";
       }catch(Exception ex){
           mensaje = "NO_OK";
           ex.printStackTrace();
       }
      return mensaje;
    }

    @Override
    public List<DocumentoBean> getListaReporteBusquedaVoucher(DocumentoBean documento) {
       StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
             
        prutaReporte.append("SELECT     (EX.NU_CORR_EXP + '-' + EX.NU_ANN_EXP) AS nuExpediente, ");
        prutaReporte.append("		EX.NU_CORR_EXP, ");
        prutaReporte.append("		(SELECT DEP.TITULO_DEP FROM IDOSGD.RHTM_DEPENDENCIA DEP WHERE DEP.CO_DEPENDENCIA=RE.CO_DEP_EMI) DE_DEPENDENCIA, ");
        prutaReporte.append("		(SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_INSTITUCION') DE_INSTITUCION, ");
        prutaReporte.append("		ISNULL((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='ANEXO_MESA_PARTES'), '') ANEXO_MESA_PARTES, ");
        prutaReporte.append("		ISNULL((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='FONO_INSTITUCION'), '') FONO_INSTITUCION, ");
        prutaReporte.append("		(SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='PAG_WEB') PAG_WEB, ");
        prutaReporte.append("		ISNULL((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_MESA_PARTES'), '') DE_MESA_PARTES, ");
        prutaReporte.append("		ISNULL((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='DE_SLOGAN'), '') DE_SLOGAN, ");     
        prutaReporte.append("		ISNULL((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='TIPO_VOUCHER_MP'), ' ') tiVoucherMP, ");
        
        prutaReporte.append("		IDOSGD.PK_SGD_DESCRIPCION_TI_EMI_EMP(RE.NU_ANN, RE.NU_EMI) DE_ORI_EMI,RE.NU_FOLIOS as nuFolios,");
        
        //prutaReporte.append("		(SELECT LISTAGG(IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(CO_DEP_DES), ',') WITHIN GROUP (ORDER BY  CO_DEP_DES) FROM IDOSGD.TDTV_DESTINOS DES WHERE DES.NU_ANN=RE.NU_ANN AND DES.NU_EMI=RE.NU_EMI AND DES.CO_MOT<>'1') deDepDes, ");
        prutaReporte.append("(stuff((SELECT ', ' + (select RETORNO from IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(DES.CO_DEP_DES))\n" +
                            "	FROM IDOSGD.TDTV_DESTINOS as DES WITH (NOLOCK) \n" +
                            "	WHERE DES.NU_ANN=RE.NU_ANN AND DES.NU_EMI=RE.NU_EMI AND DES.CO_MOT<>'1'\n" +
                            "	FOR XML PATH('')), 1, 2, '')				 \n" +
                            "	) deDepDes, 	");
        prutaReporte.append("	EX.FE_EXP, ISNULL((SELECT COD_USER FROM IDOSGD.SEG_USUARIOS1 WHERE CEMP_CODEMP = RE.CO_EMP_RES),' ') COD_USER, ");
        prutaReporte.append("	(SELECT CDES_USER FROM IDOSGD.SEG_USUARIOS1 WHERE CEMP_CODEMP = RE.CO_EMP_RES) deUsuario,ISNULL(RE.NU_ANEXO,' ') as deAne, ");
        prutaReporte.append(" (stuff((SELECT ', ' + (select RETORNO from IDOSGD.PK_SGD_DESCRIPCION_DE_SIGLA_CORTA(DES.CO_DEP_DES))\n" +
                            "	FROM IDOSGD.TDTV_DESTINOS as DES  WITH (NOLOCK)  \n" +
                            "	WHERE DES.NU_ANN=RE.NU_ANN AND DES.NU_EMI=RE.NU_EMI AND DES.CO_MOT='1'\n" +
                            "	FOR XML PATH('')), 1, 2, '')		 \n" +
                            "	) as nuCopias,");
        prutaReporte.append("	(SELECT exp.NU_EXPEDIENTE FROM IDOSGD.tdtr_referencia ref inner join IDOSGD.tdtv_remitos rt on ref.NU_ANN_REF=rt.NU_ANN and ref.NU_EMI_REF=rt.nu_EMI"
                + " inner join IDOSGD.tdtc_expediente exp on exp.nu_sec_exp=rt.nu_sec_exp where ref.nu_emi=RE.nu_EMI and ref.nu_ann=RE.nu_ANN) nuEmiRef, ");
        prutaReporte.append("		EX.CCLAVE,(CASE WHEN RE.COBS_DOCUMENTO IS NULL THEN ' ' ELSE 'Observación:'+RE.COBS_DOCUMENTO END) as deObsDoc, ");
        prutaReporte.append("		(CASE WHEN RE.ES_DOC_EMI='8' THEN ISNULL((SELECT PAR.DE_PAR FROM IDOSGD.TDTR_PARAMETROS PAR WHERE PAR.CO_PAR='OBS_VOUCHER_MP'), ' ') ELSE ' ' END) as obsVoucherMP ");
               
        prutaReporte.append("FROM IDOSGD.TDTV_REMITOS RE  WITH (NOLOCK)  ");
        prutaReporte.append("INNER JOIN	  IDOSGD.TDTC_EXPEDIENTE EX WITH (NOLOCK)  ON RE.NU_ANN_EXP=EX.NU_ANN_EXP AND RE.NU_SEC_EXP=EX.NU_SEC_EXP ");  
        
        
        try {
            prutaReporte.append("WHERE RE.NU_ANN = :nu_ann ");
            objectParam.put("nu_ann", documento.getNuAnn());
            prutaReporte.append("AND RE.NU_EMI = :nu_emi ");
            objectParam.put("nu_emi", documento.getNuEmi()); 

            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocumentoBean.class));

        } catch (Exception ex) { 
            ex.printStackTrace();
        }      
        return list;
    }
     
    
 
}
