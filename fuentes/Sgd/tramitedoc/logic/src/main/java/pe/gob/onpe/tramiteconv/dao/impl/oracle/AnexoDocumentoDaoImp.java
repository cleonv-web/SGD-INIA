package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.io.InputStream;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
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

/**
 *
 * @author wcutipa
 */
@Repository("anexoDocumentoDao")
public class AnexoDocumentoDaoImp extends SimpleJdbcDaoBase implements AnexoDocumentoDao {

    @Override
    public List<ReferenciaBean> getDocumentosReferencia(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT \n" +
                    "PK_SGD_DESCRIPCION.de_documento (a.co_tip_doc_adm) li_tip_doc, \n" +
                    "DECODE (a.ti_emi,'01', nvl(a.nu_doc_emi,' ') || '-' || a.nu_ann || '-' || a.de_doc_sig,\n" +
                    "                 '05', nvl(a.nu_doc_emi,' ') || '-' || a.nu_ann || '-' || a.de_doc_sig,nvl(a.de_doc_sig,'S/N')) li_nu_doc,\n" +
                    "substr(DECODE (a.ti_emi,\n" +
                    "      '01', PK_SGD_DESCRIPCION.de_dependencia (a.co_dep_emi),\n" +
                    "      '02', PK_SGD_DESCRIPCION.de_proveedor (a.nu_ruc_emi),\n" +
                    "      '03', 'CIUDADANO - ' ||PK_SGD_DESCRIPCION.ani_simil (a.nu_dni_emi),\n" +
                    "      '04', PK_SGD_DESCRIPCION.otro_origen (a.co_otr_ori_emi),\n" +
                    "      '05', PK_SGD_DESCRIPCION.de_nom_emp (a.co_emp_emi)\n" +
                    "     ),1,100) de_dep_emi,                 \n" +
                    "TO_CHAR(a.fe_emi,'DD/MM/YYYY') fe_emi_corta,\n" +
                    "b.nu_ann,\n" +
                    "b.nu_emi,\n" +
                    "nvl(trim(to_char(b.nu_des)),'N') nu_des ,\n" +
                    "b.nu_ann_ref,\n" +
                    "b.nu_emi_ref,\n" +
                    "nvl(trim(to_char(b.nu_des_ref)),'N') nu_des_ref,nvl(a.de_asu,'') as deAsu,a.CO_TIP_DOC_ADM  as coTipDocAdm \n" +
                    "FROM tdtv_remitos a,TDTR_REFERENCIA b \n" +
                    "WHERE a.nu_ann = b.nu_ann_ref \n" +
                    "AND a.nu_emi = b.nu_emi_ref\n" +
                    "and b.nu_ann = ? \n" +
                    "and b.nu_emi = ? ");

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
        sql.append("SELECT\n" +
                    "PK_SGD_DESCRIPCION.de_documento (a.co_tip_doc_adm) li_tip_doc, \n" +
                    "DECODE (a.ti_emi,'01', nvl(a.nu_doc_emi,' ') || '-' || a.nu_ann || '-' || a.de_doc_sig,\n" +
                    "                 '05', nvl(a.nu_doc_emi,' ') || '-' || a.nu_ann || '-' || a.de_doc_sig,nvl(a.de_doc_sig,'S/N')) li_nu_doc,\n" +
                    "substr(DECODE (a.ti_emi,\n" +
                    "      '01', PK_SGD_DESCRIPCION.de_dependencia (a.co_dep_emi),\n" +
                    "      '02', PK_SGD_DESCRIPCION.de_proveedor (a.nu_ruc_emi),\n" +
                    "      '03', 'CIUDADANO - ' ||PK_SGD_DESCRIPCION.ani_simil (a.nu_dni_emi),\n" +
                    "      '04', PK_SGD_DESCRIPCION.otro_origen (a.co_otr_ori_emi),\n" +
                    "      '05', PK_SGD_DESCRIPCION.de_nom_emp (a.co_emp_emi)\n" +
                    "     ),1,100) de_dep_emi,                 \n" +
                    "TO_CHAR(a.fe_emi,'DD/MM/YYYY') fe_emi_corta, \n" +
                    "a.nu_ann,\n" +
                    "a.nu_emi,\n" +
                    "'N' nu_des ,\n" +
                    "a.nu_ann nu_ann_ref,\n" +
                    "a.nu_emi nu_emi_ref,\n" +
                    "'N'  nu_des_ref\n" +
                    "FROM tdtv_remitos a \n" +
                    "WHERE a.nu_ann = ? \n" +
                    "and a.nu_emi = ? ");

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
        sql.append("SELECT \n"
                + "PK_SGD_DESCRIPCION.de_documento (b.co_tip_doc_adm) li_tip_doc, \n"
                + "DECODE (b.ti_emi,'01', b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig, \n"
                + "                 '05', b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig, nvl(b.de_doc_sig,'S/N')) li_nu_doc, \n"
                + "substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(b.co_dep_emi), 1, 100) de_dep_emi, \n"
                + "substr(DECODE(c.ti_des,'01', PK_SGD_DESCRIPCION.de_dependencia(c.co_dep_des),\n"
                + "                '02', PK_SGD_DESCRIPCION.de_proveedor(c.nu_ruc_des),\n"
                + "                '03', 'CIUDADANO - ' ||PK_SGD_DESCRIPCION.ani_simil(c.nu_dni_des),\n"
                + "                '04', PK_SGD_DESCRIPCION.otro_origen(c.co_otr_ori_des),' '),1,100) de_dep_des,\n"
                + "TO_CHAR(b.fe_emi,'DD/MM/YYYY') fe_emi_corta, \n"
                + "c.nu_ann,\n"
                + "c.nu_emi,\n"
                + "to_char(c.nu_des) nu_des, \n"
                + "a.nu_ann_ref,\n"
                + "a.nu_emi_ref \n"
                + "FROM tdtr_referencia a, tdtv_remitos b , tdtv_destinos c\n"
                + "WHERE a.nu_ann = b.nu_ann \n"
                + "and a.nu_emi = b.nu_emi \n"
                + "and b.nu_ann = c.nu_ann \n"
                + "and b.nu_emi = c.nu_emi\n"
                + "and b.es_eli = '0' \n"
                + "and b.es_doc_emi <> '9'\n"
                + "and a.nu_ann_ref = ? \n"
                + "and a.nu_emi_ref = ? \n"
                + "and a.nu_des_ref = ? \n");

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

        sql.append("SELECT \n"
                + "PK_SGD_DESCRIPCION.de_documento (b.co_tip_doc_adm) li_tip_doc, \n"
                + "DECODE (b.ti_emi,'01', b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig, \n"
                + "                 '05', b.nu_doc_emi || '-' || b.nu_ann || '-' || b.de_doc_sig,nvl(b.de_doc_sig,'S/N')) li_nu_doc, \n"
                + "substr(PK_SGD_DESCRIPCION.DE_DEPENDENCIA(b.co_dep_emi), 1, 100) de_dep_emi, \n"
                + "DECODE(c.ti_des,'01', PK_SGD_DESCRIPCION.de_dependencia(c.co_dep_des),\n"
                + "                '02', PK_SGD_DESCRIPCION.de_proveedor(c.nu_ruc_des),\n"
                + "                '03', 'CIUDADANO - ' || PK_SGD_DESCRIPCION.ani_simil(c.nu_dni_des),\n"
                + "                '04', PK_SGD_DESCRIPCION.otro_origen(c.co_otr_ori_des),' ') de_dep_des,\n"
                + "TO_CHAR(b.fe_emi,'DD/MM/YYYY') fe_emi_corta, \n"
                + "c.nu_ann, \n"
                + "c.nu_emi, \n"
                + "to_char(c.nu_des) nu_des \n"
                + "FROM tdtv_remitos b , tdtv_destinos c \n"
                + "WHERE b.nu_ann = c.nu_ann \n"
                + "and b.nu_emi = c.nu_emi \n"
                + "and b.es_eli = '0' \n"
                + "and b.es_doc_emi <> '9' \n"
                + "and b.nu_ann = ? \n"
                + "and b.nu_emi = ? \n");

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
        sql.append("select to_char(nvl(max(nu_ane),0) + 1) nu_ane \n"
                + "from tdtv_anexos \n"
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
        sql.append("update tdtv_anexos set\n"
                + "ti_public=?,nu_ann=?,\n"
                + "nu_emi=?,\n"
                + "nu_ane=?,\n"
                + "de_det=REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' '),\n"
                + "de_rut_ori=REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' '),\n"
                + "co_use_cre=?,\n"
                + "fe_use_cre=sysdate,\n"
                + "co_use_mod=?,\n"
                + "fe_use_mod=sysdate,\n"
                + "feula=to_char(sysdate,'yyyymmdd'),\n"
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
        sqlDel.append("delete from tdtv_anexos\n"
                + "WHERE NU_ANN = ?\n"
                + "AND NU_EMI = ?\n"
                + "and nu_ane=?");
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
        String vReturn = "NO_OK";

        StringBuffer sqlDel = new StringBuffer();
        sqlDel.append("delete from tdtv_anexos\n"
                + "WHERE NU_ANN = ?\n"
                + "AND NU_EMI = ?\n"
                + "and nu_des=?");
        StringBuffer sqlUpd = new StringBuffer();
//        sqlUpd.append("update tdtv_anexos\n"
//                + "set nu_ane=nu_ane-1\n"
//                + "where  \n"
//                + "NU_ANN = ?\n"
//                + "AND NU_EMI = ?\n"
//                + "and nu_ane>?");

        try {
            this.jdbcTemplate.update(sqlDel.toString(), new Object[]{pNuAnn, pNuEmi, pNuDes});
            //this.jdbcTemplate.update(sqlUpd.toString(),new Object[]{docAnexo.getNuAnn(),docAnexo.getNuEmi(),docAnexo.getNuAne()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            e.getMessage();
        }
        return vReturn;
    }

      
    public String updAnexoDetalle(final DocumentoAnexoBean docAnexo) {
        String vReturn = "NO_OK";
        StringBuffer  sqlUpd = new StringBuffer();
        sqlUpd.append("update tdtv_anexos \n"
                + "set ti_public=?, de_det=REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' ')\n"/*SEGDI MVALDERA*/
                + ",co_use_mod=?\n"
                + ",fe_use_mod=sysdate\n"
                + ",REQ_FIRMA=?\n"
                + "where\n"
                + "nu_ann=? and\n"
                + "nu_emi=? and\n"
                + "nu_ane=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{docAnexo.getTiPublic(),docAnexo.getDeDet(),docAnexo.getCoUseMod(),docAnexo.getReqFirma(),docAnexo.getNuAnn(),docAnexo.getNuEmi(), docAnexo.getNuAne()});
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
        sql.append("insert into tdtv_anexos(\n"
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
                + "values(?,?,?,REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ''),REGEXP_REPLACE(?, '[^-_ABCDEFGHIJKLMNÑOPQRSTUVWXYZabcdefghijklmnñopqrstuvwxyz0-9ÁÉÍÓÚáéíóú. ]', ' '),?,sysdate,?,sysdate,to_char(sysdate,'yyyymmdd'),?,'1')");

        final LobHandler lobhandler = new DefaultLobHandler();

        try {
            this.jdbcTemplate.execute(sql.toString(),
                    new AbstractLobCreatingPreparedStatementCallback(lobhandler) {
                protected void setValues(PreparedStatement ps, LobCreator lobCreator)
                        throws SQLException {
                    ps.setString(1, docAnexo.getNuAnn());
                    ps.setString(2, docAnexo.getNuEmi());
                    ps.setString(3, docAnexo.getNuAne());
                    ps.setString(4, docAnexo.getDeDet());
                    ps.setString(5, docAnexo.getDeRutOri());
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
        sql.append("select de_det from tdtv_anexos where nu_ann=? and nu_emi=? and nu_ane=?");

        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pNuAnn, pNuEmi,pNuAne});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }
    
    @Override
    public String updExisteAnexo(String pNuAnn, String pNuEmi) {
        StringBuffer sql = new StringBuffer();
        String result = "NO_OK";
        sql.append("update TDTX_REMITOS_RESUMEN SET IN_EXISTE_ANEXO ='0' where NU_ANN=? and NU_EMI=?");
        
        try {
            this.jdbcTemplate.update(sql.toString(),new Object[]{pNuAnn, pNuEmi});
        } catch (Exception e) {
            e.printStackTrace();
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
                    "UPPER(SUBSTR(de_rut_ori,instr(de_rut_ori, '.', -1, 1)+1)) tipo_doc\n" +
                    "from tdtv_anexos \n" +
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
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("UPDATE TDTX_REMITOS_RESUMEN SET IN_FIRMA_ANEXO = ?\n" +
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
        }
        return vReturn;        
    }
    
    @Override
    public String updArchivoAnexoFirmado(final DocumentoObjBean docObjBean){
        String vReturn = "NO_OK";

        final LobHandler lobhandler = new DefaultLobHandler();
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE TDTV_ANEXOS SET\n" +
                    "BL_DOC=?,\n" +
                    "CO_USE_MOD=?,\n" +
                    "FE_USE_MOD=SYSDATE,\n" +
                    "FEULA=TO_CHAR(SYSDATE,'YYYYMMDD'),\n" +
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
            vReturn = this.jdbcTemplate.queryForObject("SELECT count(1) from tdtv_anexos where\n" +
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
            vReturn = this.jdbcTemplate.queryForObject("SELECT count(1) from IDOSGD.tdtv_anexos where\n" +
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
                                    "NU_ANN=? AND NU_EMI=? AND NU_ANE<>? AND de_rut_ori='"+nombre+"'", String.class, 
                        new Object[]{nuAnn,nuEmi,anexo});
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = "0";
        }
        return vReturn;        
    }
}
