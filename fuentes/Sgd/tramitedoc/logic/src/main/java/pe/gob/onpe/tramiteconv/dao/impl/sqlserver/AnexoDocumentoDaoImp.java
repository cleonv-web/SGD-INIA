package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import org.springframework.jdbc.support.lob.DefaultLobHandler;
import org.springframework.jdbc.support.lob.LobCreator;
import org.springframework.jdbc.support.lob.LobHandler;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.dao.AnexoDocumentoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;
import org.springframework.jdbc.core.BeanPropertyRowMapper; 
import org.springframework.jdbc.core.SqlParameter;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.jdbc.core.simple.SimpleJdbcCall;
import org.springframework.jdbc.core.support.AbstractLobCreatingPreparedStatementCallback;
import java.sql.Types;
/**
 *
 * @author wcutipa
 */
@Repository("anexoDocumentoDao")
public class AnexoDocumentoDaoImp extends SimpleJdbcDaoBase implements AnexoDocumentoDao {
    private SimpleJdbcCall spdataSource;
    @Override
    public List<ReferenciaBean> getDocumentosReferencia(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(a.co_tip_doc_adm) li_tip_doc, ");
        sql.append("        (CASE WHEN (a.ti_emi='01' OR a.ti_emi='05') THEN ISNULL(a.nu_doc_emi,' ') + '-' + a.nu_ann + '-' + a.de_doc_sig ");
        sql.append("		  ELSE ISNULL(a.de_doc_sig,'S/N') ");
        sql.append("         END) li_nu_doc, ");
        sql.append("        SUBSTRING( ");
        sql.append("        (CASE a.ti_emi ");
        sql.append("		WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_emi) ");
        sql.append("		WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_emi) ");
        sql.append("		WHEN '03' THEN 'CIUDADANO - ' + IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.nu_dni_emi) ");
        sql.append("		WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(a.co_otr_ori_emi) ");
        sql.append("		WHEN '05' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_emi) ");
        sql.append("		ELSE ' ' ");
        sql.append("         END) ");
        sql.append("        , 1, 100) de_dep_emi, ");
        sql.append("        convert(varchar, a.fe_emi, 103) fe_emi_corta, ");
        sql.append("        b.nu_ann, ");
        sql.append("        b.nu_emi, ");
        sql.append("        ISNULL(LTRIM(RTRIM(CAST(b.nu_des AS VARCHAR))), 'N') nu_des, ");
        sql.append("        b.nu_ann_ref, ");
        sql.append("        b.nu_emi_ref, ");
        sql.append("        ISNULL(LTRIM(RTRIM(CAST(b.nu_des_ref AS VARCHAR))), 'N') nu_des_ref ,ISNULL(a.de_asu,'') as deAsu,a.CO_TIP_DOC_ADM  as coTipDocAdm  ");
        sql.append("FROM IDOSGD.tdtv_remitos a WITH (NOLOCK)  ");
        sql.append(" INNER JOIN	 IDOSGD.TDTR_REFERENCIA b WITH (NOLOCK) ON a.nu_ann = b.nu_ann_ref  AND a.nu_emi = b.nu_emi_ref  ");
        sql.append("WHERE b.nu_ann = ? ");
        sql.append("and b.nu_emi = ? ");

        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class), new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;

    }

    @Override
    public List<ReferenciaBean> getDocumentoEmi(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO (a.co_tip_doc_adm) li_tip_doc, ");
        sql.append("        (CASE WHEN (a.ti_emi='01' OR a.ti_emi='05') THEN ISNULL(a.nu_doc_emi,' ') + '-' + a.nu_ann + '-' + a.de_doc_sig ");
        sql.append("		  ELSE ISNULL(a.de_doc_sig, 'S/N') ");
        sql.append("         END) li_nu_doc, ");
        sql.append("        SUBSTRING( ");
        sql.append("		(CASE a.ti_emi ");
        sql.append("                WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(a.co_dep_emi) ");
        sql.append("                WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_emi) ");
        sql.append("                WHEN '03' THEN 'CIUDADANO - ' + IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.nu_dni_emi) ");
        sql.append("                WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(a.co_otr_ori_emi) ");
        sql.append("                WHEN '05' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_emi) ");
        sql.append("                ELSE ' ' ");
        sql.append("		 END) ");
        sql.append("        ,1 , 100) de_dep_emi, ");
        sql.append("        convert(varchar(10), a.fe_emi, 103) fe_emi_corta, ");
        sql.append("        a.nu_ann, ");
        sql.append("        a.nu_emi, ");
        sql.append("        'N' nu_des, ");
        sql.append("        a.nu_ann nu_ann_ref, ");
        sql.append("        a.nu_emi nu_emi_ref, ");
        sql.append("        'N'  nu_des_ref ");
        sql.append("FROM IDOSGD.tdtv_remitos a WITH (NOLOCK)  ");
        sql.append("WHERE a.nu_ann = ? ");
        sql.append("and a.nu_emi = ? ");

        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class), new Object[]{pnuAnn, pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;

    }

    public List<ReferenciaBean> getDocumentosSeguimiento(String pnuAnn, String pnuEmi, String pnuDes) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  [IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](b.co_tip_doc_adm) li_tip_doc, ");
        sql.append("        CASE b.ti_emi ");
        sql.append("		WHEN '01' THEN COALESCE(b.nu_doc_emi,'') + '-' + COALESCE(b.nu_ann,'') + '-' + COALESCE(b.de_doc_sig, '') ");
        sql.append("		WHEN '05' THEN COALESCE(b.nu_doc_emi,'') + '-' + COALESCE(b.nu_ann,'') + '-' + COALESCE(b.de_doc_sig, '') ");
        sql.append("		ELSE ISNULL(b.de_doc_sig, 'S/N') ");
        sql.append("        END li_nu_doc, ");
        sql.append("        SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](b.co_dep_emi), 1, 100) de_dep_emi, ");
        sql.append("        SUBSTRING( ");
        sql.append("            CASE c.ti_des ");
        sql.append("                WHEN '01' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](c.co_dep_des) ");
        sql.append("                WHEN '02' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_PROVEEDOR](c.nu_ruc_des) ");
        sql.append("                WHEN '03' THEN 'CIUDADANO - ' + [IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](c.nu_dni_des) ");
        sql.append("                WHEN '04' THEN [IDOSGD].[PK_SGD_DESCRIPCION_OTRO_ORIGEN](c.co_otr_ori_des) ");
        sql.append("                ELSE ' ' ");
        sql.append("            END ");
        sql.append("        , 1, 100) de_dep_des, ");
        sql.append("        CONVERT(VARCHAR(10), b.fe_emi, 103) fe_emi_corta, ");
        sql.append("        c.nu_ann, ");
        sql.append("        c.nu_emi, ");
        sql.append("        CAST(c.nu_des AS VARCHAR) nu_des, ");
        sql.append("        a.nu_ann_ref, ");
        sql.append("        a.nu_emi_ref ");
        sql.append("FROM IDOSGD.tdtr_referencia a WITH (NOLOCK)   ");
        sql.append(" INNER JOIN	 IDOSGD.tdtv_remitos b WITH (NOLOCK) ON a.nu_ann = b.nu_ann and a.nu_emi = b.nu_emi ");
        sql.append(" INNER JOIN	 IDOSGD.tdtv_destinos c WITH (NOLOCK) ON  b.nu_ann = c.nu_ann and b.nu_emi = c.nu_emi  ");
        sql.append("WHERE  b.es_eli = '0' ");
        sql.append("and b.es_doc_emi <> '9' ");
        sql.append("and a.nu_ann_ref = ? ");
        sql.append("and a.nu_emi_ref = ? ");
        sql.append("and a.nu_des_ref = ? ");

        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class), new Object[]{pnuAnn, pnuEmi, pnuDes});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;

    }

    public List<ReferenciaBean> getDocumentoEmiSeg(String pnuAnn, String pnuEmi, String pnuDes) {
        StringBuffer sql = new StringBuffer();
        Object[] objectParam = null;

        sql.append("SELECT  [IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](b.co_tip_doc_adm) li_tip_doc, ");
        sql.append("        CASE b.ti_emi ");
        sql.append("		WHEN '01' THEN COALESCE(b.nu_doc_emi,'') + '-' + COALESCE(b.nu_ann,'') + '-' + COALESCE(b.de_doc_sig, '') ");
        sql.append("		WHEN '05' THEN COALESCE(b.nu_doc_emi,'') + '-' + COALESCE(b.nu_ann,'') + '-' + COALESCE(b.de_doc_sig, '') ");
        sql.append("		ELSE ISNULL(b.de_doc_sig, 'S/N') ");
        sql.append("        END li_nu_doc, ");
        sql.append("        SUBSTRING([IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](b.co_dep_emi), 1, 100) de_dep_emi, ");
        sql.append("        CASE c.ti_des ");
        sql.append("		WHEN '01' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](c.co_dep_des) ");
        sql.append("		WHEN '02' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_PROVEEDOR](c.nu_ruc_des) ");
        sql.append("		WHEN '03' THEN 'CIUDADANO - ' + [IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](c.nu_dni_des) ");
        sql.append("		WHEN '04' THEN [IDOSGD].[PK_SGD_DESCRIPCION_OTRO_ORIGEN](c.co_otr_ori_des) ");
        sql.append("		ELSE ' ' ");
        sql.append("        END  de_dep_des, ");
        sql.append("        CONVERT(VARCHAR(10), b.fe_emi, 103) fe_emi_corta, ");
        sql.append("        c.nu_ann, ");
        sql.append("        c.nu_emi, ");
        sql.append("        CAST(c.nu_des AS VARCHAR) nu_des ");
        sql.append("FROM IDOSGD.tdtv_remitos b  WITH (NOLOCK) ");
        sql.append(" INNER JOIN	 IDOSGD.tdtv_destinos c WITH (NOLOCK) ON b.nu_ann = c.nu_ann and b.nu_emi = c.nu_emi  ");
        sql.append("WHERE  b.es_eli = '0' ");
        sql.append("and b.es_doc_emi <> '9' ");
        sql.append("and b.nu_ann = ? ");
        sql.append("and b.nu_emi = ? ");

        if (pnuDes.equals("N")) {
            objectParam = new Object[]{pnuAnn, pnuEmi};
            
        } else {
            sql.append("and c.nu_des = ? ");
            objectParam = new Object[]{pnuAnn, pnuEmi, pnuDes};
        }

        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class), objectParam);
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;

    }

    public String getUltimoAnexo(String pnuAnn, String pnuEmi) {
        String result = null;

        StringBuffer sql = new StringBuffer();
        sql.append("select CAST(ISNULL(max(nu_ane),0) + 1 AS VARCHAR(10)) nu_ane \n"
                + "from IDOSGD.tdtv_anexos WITH (NOLOCK) \n"
                + "where nu_ann = ? \n"
                + "and nu_emi = ? ");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pnuAnn, pnuEmi});
        } catch (Exception e) {
            result = "1";
            e.printStackTrace();
        }
        return result;

    }

    public String updArchivoAnexo(final DocumentoAnexoBean docAnexo, final InputStream archivoAnexo, final int size) {
        String vReturn = "NO_OK";

        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.tdtv_anexos set\n"
                + "ti_public=?,nu_ann=?,\n"
                + "nu_emi=?,\n"
                + "nu_ane=?,\n"
                + "de_det=?,\n"//REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' ')
                + "de_rut_ori=?,\n"//REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' ')
                + "co_use_cre=?,\n"
                + "fe_use_cre=getdate(),\n"
                + "co_use_mod=?,\n"
                + "fe_use_mod=getdate(),\n"
                + "feula=REPLACE(convert(varchar, GETDATE(), 103),'/',''),\n"
                + "bl_doc=?\n"
                + "where \n"
                + "nu_ann=? and nu_emi=? and nu_ane=?");

        final LobHandler lobhandler = new DefaultLobHandler();

        try {
            this.jdbcTemplate.execute(sql.toString(),
                    new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                        throws SQLException {
                    ps.setString(1, docAnexo.getTiPublic());/*SEGDI MVALDERA*/
                    ps.setString(2, docAnexo.getNuAnn());
                    ps.setString(3, docAnexo.getNuEmi());
                    ps.setString(4, docAnexo.getNuAne());
                    ps.setString(5, docAnexo.getDeDet());
                    ps.setString(6, docAnexo.getDeRutOri());
                    ps.setString(7, docAnexo.getCoUseCre());
                    ps.setString(8, docAnexo.getCoUseMod());
                    lobCreator.setBlobAsBinaryStream(ps, 9, archivoAnexo, size);
                    ps.setString(10, docAnexo.getNuAnn());
                    ps.setString(11, docAnexo.getNuEmi());
                    ps.setString(12, docAnexo.getNuAne());
                }
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public String delArchivoAnexo(final DocumentoAnexoBean docAnexo) {
        String vReturn = "NO_OK";

        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("delete from IDOSGD.tdtv_anexos\n"
                + "WHERE NU_ANN = ?\n"
                + "AND NU_EMI = ?\n"
                + "and nu_ane=CAST(? AS NUMERIC(2,0))");
        StringBuffer sqlUpd = new StringBuffer();
//        sqlUpd.append("update tdtv_anexos\n"
//                + "set nu_ane=nu_ane-1\n"
//                + "where  \n"
//                + "NU_ANN = ?\n"
//                + "AND NU_EMI = ?\n"
//                + "and nu_ane>?");

        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{docAnexo.getNuAnn(), docAnexo.getNuEmi(), docAnexo.getNuAne()});
            //this.jdbcTemplate.update(sqlUpd.toString(),new Object[]{docAnexo.getNuAnn(),docAnexo.getNuEmi(),docAnexo.getNuAne()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
        return vReturn;
    }
    
    public String delArchivoAnexoDest(String pNuAnn,String pNuEmi,String pNuDes) {
        return "";
    }

    public String updAnexoDetalle(final DocumentoAnexoBean docAnexo) {
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        sqlUpd.append("update IDOSGD.tdtv_anexos \n"
                + "set de_det=?\n"
                + ",co_use_mod=?\n"
                + ",fe_use_mod=GETDATE()\n"
                + ",REQ_FIRMA=?\n"
                + "where\n"
                + "nu_ann=? and\n"
                + "nu_emi=? and\n"
                + "nu_ane=?");
        try {
            String deDet=Utilidades.fn_getCleanFileName(docAnexo.getDeDet());
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{deDet,docAnexo.getCoUseMod(),docAnexo.getReqFirma(),docAnexo.getNuAnn(),docAnexo.getNuEmi(), docAnexo.getNuAne()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn=e.getMessage();
            
        }
        return vReturn;
    }

    public String insArchivoAnexo(final DocumentoAnexoBean docAnexo, final InputStream archivoAnexo, final int size) {
        String vReturn = "NO_OK";

        StringBuffer sql = new StringBuffer();
        sql.append("insert into IDOSGD.tdtv_anexos(\n"
                + "nu_ann,\n"
                + "nu_emi,\n"
                + "nu_ane,\n"
                + "de_det,\n"
                + "de_rut_ori,\n"
                + "co_use_cre,\n"
                + "fe_use_cre,\n"
                + "co_use_mod,\n"
                + "fe_use_mod,\n"
                + "feula, \n"
                + "bl_doc,ti_public )\n"
                + "values(?,?,?,?,?,?,GETDATE(),?,GETDATE(),IDOSGD.PK_SGD_DESCRIPCION_FORMAT(GETDATE(),'yyyymmdd'),?,'1')");

        final LobHandler lobhandler = new DefaultLobHandler();

        try {
            this.jdbcTemplate.execute(sql.toString(),
                    new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                        throws SQLException {
                    ps.setString(1, docAnexo.getNuAnn());
                    ps.setString(2, docAnexo.getNuEmi());
                    ps.setString(3, docAnexo.getNuAne());
                    ps.setString(4, Utilidades.fn_getCleanFileName(docAnexo.getDeDet()));
                    ps.setString(5, Utilidades.fn_getCleanFileName(docAnexo.getDeRutOri()));
                    ps.setString(6, docAnexo.getCoUseCre());
                    ps.setString(7, docAnexo.getCoUseMod());
                    lobCreator.setBlobAsBinaryStream(ps, 8, archivoAnexo, size);
                }
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;

    }

    @Override
    public String getNombreArchivo(String pNuAnn, String pNuEmi, String pNuAne) {
        StringBuffer sql = new StringBuffer();
        String result = "NO_OK";
        sql.append("select de_det from IDOSGD.tdtv_anexos where nu_ann=? and nu_emi=? and nu_ane=?");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pNuAnn, pNuEmi,pNuAne});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public String updExisteAnexo(String pNuAnn, String pNuEmi) {        
        String result = "NO_OK";
        /*
        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.TDTX_REMITOS_RESUMEN SET IN_EXISTE_ANEXO ='0' where NU_ANN=? and NU_EMI=?");
        
        try {
            this.jdbcTemplate.update(sql.toString(),new Object[]{pNuAnn, pNuEmi});
        } catch (Exception e) {
            e.printStackTrace();
        }*/
         this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_REMITOS_RESUMEN_ANEXO_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR) );
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", pNuAnn)
                .addValue("p_nuEmi", pNuEmi);         
        try {               
            this.spdataSource.execute(in);
            result = "OK";
            this.spdataSource = null;
        } catch (Exception ex) { 
            ex.printStackTrace();
        }
        
        return result;
    }
    
    @Override
    public DocumentoObjBean getPropiedadesArchivo(String pnuAnn, String pnuEmi, String pnuAne) {
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "nu_ann, \n" +
                    "nu_emi, \n" +
                    "de_rut_ori nombre_Archivo,\n" +
                    "UPPER(right(de_rut_ori,3 ))  tipo_doc\n" +
                    "from IDOSGD.tdtv_anexos WITH (NOLOCK)  \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?\n" +
                    "AND NU_ANE = ?");


        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class), new Object[]{pnuAnn, pnuEmi, pnuAne});
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return (docObjBean);
    }
    
    @Override
    public String updRemitosResumenInFirmaAnexos(String pFirmaAnexo, String pnuAnn, String pnuEmi){
        String vReturn = "NO_OK";
        /*StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE IDOSGD.TDTX_REMITOS_RESUMEN SET IN_FIRMA_ANEXO = ?\n" +
                        "WHERE NU_ANN = ? \n" +
                        "AND NU_EMI = ? ");
        
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{
                pFirmaAnexo, pnuAnn, pnuEmi
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            //vReturn = e.getMessage();
        }*/
        this.spdataSource = new SimpleJdbcCall(this.dataSource).withProcedureName("[IDOSGD].[PK_SGD_TRAMITE_PROCE_REMITOS_RESUMEN_FIRMA_ANEXO_UDP]")
                .withoutProcedureColumnMetaDataAccess()
                .useInParameterNames("p_anio", "p_nuEmi","p_pFirmaAnexo")                        
                .declareParameters(
                        new SqlParameter("p_anio", Types.VARCHAR),
                        new SqlParameter("p_nuEmi", Types.VARCHAR),
                        new SqlParameter("p_pFirmaAnexo", Types.VARCHAR));
                SqlParameterSource in = new MapSqlParameterSource()
                .addValue("p_anio", pnuAnn)
                .addValue("p_nuEmi", pnuEmi)
                .addValue("p_pFirmaAnexo", pFirmaAnexo);         
        try {               
            this.spdataSource.execute(in);
            vReturn = "OK";
            this.spdataSource = null;
        } catch (Exception ex) {
             
            ex.printStackTrace();
        }
        return vReturn;        
    }
    
    @Override
    public String updArchivoAnexoFirmado(final DocumentoObjBean docObjBean){
        String vReturn = "NO_OK";

        final LobHandler lobhandler = new DefaultLobHandler();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE IDOSGD.TDTV_ANEXOS SET\n" +
                    "BL_DOC=?,\n" +
                    "CO_USE_MOD=?,\n" +
                    "FE_USE_MOD=GETDATE(),\n" +
                    "FEULA=IDOSGD.PK_SGD_DESCRIPCION_FORMAT(GETDATE(),'YYYYMMDD'),\n" +
                    "REQ_FIRMA='0'\n" +
                    "WHERE\n" +
                    "NU_ANN=? AND NU_EMI=? AND NU_ANE=?");
        try {
            this.jdbcTemplate.execute(sql.toString(),
                    new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                        throws SQLException {
                    lobCreator.setBlobAsBytes(ps, 1, docObjBean.getDocumento());
                    ps.setString(2, docObjBean.getCoUseMod());
                    ps.setString(3, docObjBean.getNuAnn());
                    ps.setString(4, docObjBean.getNuEmi());
                    ps.setString(5, docObjBean.getNuAne());
                }
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage().substring(0, 20);
        }

        return vReturn;        
    }
    
    @Override
    public String getCanAnexosReqFirma(String nuAnn, String nuEmi){
        String vReturn = "NO_OK";
        try {
            vReturn = this.jdbcTemplate.queryForObject("SELECT count(1) from IDOSGD.tdtv_anexos WITH (NOLOCK)  where\n" +
                                    "NU_ANN=? AND NU_EMI=?\n" +
                                    "AND REQ_FIRMA='1'", String.class, 
                        new Object[]{nuAnn,nuEmi});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;        
    }
    @Override
    public String getCanAnexosDuplicadosNombres(String nuAnn, String nuEmi,String nombre){
        String vReturn = "NO_OK";
        try {
            vReturn = this.jdbcTemplate.queryForObject("SELECT count(1) from IDOSGD.tdtv_anexos WITH (NOLOCK)  where\n" +
                                   "NU_ANN=? AND NU_EMI=? AND de_rut_ori='"+nombre+"'", String.class, 
                        new Object[]{nuAnn,nuEmi});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;        
    }
    @Override
    public String getCanAnexosDuplicadosNombres(String nuAnn, String nuEmi,String anexo,String nombre){
        String vReturn = "NO_OK";
        try {
            vReturn = this.jdbcTemplate.queryForObject("SELECT count(1) from IDOSGD.tdtv_anexos where\n" +
                                    "NU_ANN=? AND NU_EMI=? AND NU_ANE<>CAST(? AS NUMERIC(2,0)) AND de_rut_ori='"+nombre+"'", String.class, 
                        new Object[]{nuAnn,nuEmi,anexo});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;        
    }
}
