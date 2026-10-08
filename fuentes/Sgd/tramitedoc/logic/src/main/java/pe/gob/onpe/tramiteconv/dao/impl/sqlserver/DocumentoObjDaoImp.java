package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.apache.log4j.Logger;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DocumentoDatoBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoNotificacionBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBean;
import pe.gob.onpe.tramitedoc.bean.DocumentoObjBlock;
import pe.gob.onpe.tramitedoc.dao.DocumentoObjDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;


@Repository("documentoObjDao")
public class DocumentoObjDaoImp extends SimpleJdbcDaoBase implements DocumentoObjDao {

    private static Logger logger=Logger.getLogger("SGDDocumentoObjDaoImp");
             
    public DocumentoDatoBean getDatosDoc(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    " nu_ann,\n" +
                    " nu_emi,\n" +
                    " co_dep_emi,\n" +
                    " co_emp_emi,\n" +
                    " CAST(nu_cor_emi AS VARCHAR) nu_cor_emi, \n" +
                    " ti_emi,\n" +
                    " ti_cap,\n" +
                    " es_doc_emi,\n" +
                    " co_tip_doc_adm co_doc,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(co_tip_doc_adm) tipo_Doc,\n" +
                    " COALESCE(nu_doc_emi,'') numero_Doc,\n" +
                    " de_doc_sig siglas_Doc,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(fe_emi,'DD-MM-YYYY') fecha_Doc,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(GETDATE(),'DD/MM/YYYY') fecha_actual,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(GETDATE(),'YYYY-MM-DD') fecha_firma,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_INITCAP((SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_DISTRITO_LOCAL(co_loc_emi))) de_Lugar,COALESCE(COD_VER_EXT,' ') as coVerExt\n" +
                    " from IDOSGD.tdtv_remitos  WITH (NOLOCK) \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?");
   
        DocumentoDatoBean docDatoBean = new DocumentoDatoBean();

        try {
            docDatoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDatoBean.class),new Object[]{ pnuAnn,pnuEmi} );
        } catch (EmptyResultDataAccessException e) {
            docDatoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(docDatoBean);
    }

    
    public  DocumentoObjBean getDatosDoc(String pnuAnn, String pnuEmi, String ptiCap){
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    " nu_ann, \n" +
                    " nu_emi, \n" +
                    " de_ruta_origen nombre_Archivo,\n" +
                    " COALESCE(len(bl_doc),0) nu_Tamano\n" +
                    " from IDOSGD.tdtv_archivo_doc  WITH (NOLOCK) \n" +
                    " where nu_ann = ?\n" +
                    "and nu_emi = ?");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class),new Object[]{ pnuAnn,pnuEmi} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(docObjBean);
    }
    
    
    @Override
    public  DocumentoObjBean getNombreArchivo(String pnuAnn, String pnuEmi, String ptiCap){
        StringBuffer sql = new StringBuffer();
        sql.append(" select \n" +
                    " nu_ann, \n" +
                    " nu_emi, \n" +
                    " de_ruta_origen nombre_Archivo,\n" +
                    " COALESCE(len(bl_doc),0) nu_Tamano,\n" +
                    "w_de_ruta_origen w_Nombre_Archivo,\n"+
                    "COALESCE(len(w_bl_doc),0) w_Nu_Tamano \n"+
                    " from IDOSGD.tdtv_archivo_doc  WITH (NOLOCK)  \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class),new Object[]{ pnuAnn,pnuEmi} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(docObjBean);
    }
    
    
    @Override
    public String CopiarAnexo(String pnuAnn, String pnuEmi, String pnuAnexo,String pNuAnnDocProyecto, String pnuEmiDocProyecto)
    {
        StringBuffer sql = new StringBuffer();
        boolean inInsert = false;
        /*Verificamos si es Insert o Update*/
         sql.append(" SELECT  count(nu_ann) \n" +
                   "  FROM IDOSGD.TDTV_ANEXOS  WITH (NOLOCK) \n" +
                   "  where nu_emi='"+pnuEmi+"' and nu_ann='"+pnuAnn+"' and nu_ane='"+pnuAnexo+"'" 
                   );
        try {
            int cant = this.jdbcTemplate.queryForInt(sql.toString(), new Object[]{});
            if (cant > 0) {
                inInsert = true;
            }  
        } catch (Exception e) {
            e.printStackTrace();            
        }
        String result = null;
        if (inInsert) {            
            sql.append("insert into IDOSGD.TDTV_ANEXOS \n" +
                       " (NU_ANN,NU_EMI,NU_ANE,DE_DET,BL_DOC,DE_RUT_ORI,DE_RUT_DES,CO_TIP_DOC,CO_USE_CRE,\n" +
                       " FE_USE_CRE,CO_USE_MOD,FE_USE_MOD,FEULA,REQ_FIRMA,NU_DES,TI_PUBLIC)\n" +
                       " SELECT '"+pNuAnnDocProyecto+"','"+pnuEmiDocProyecto+"',(SELECT COUNT(T.NU_ANE)+1 "
                               + "FROM IDOSGD.TDTV_ANEXOS T WITH (NOLOCK)  where nu_emi='"+pnuEmiDocProyecto+"' and nu_ann='"+pNuAnnDocProyecto+"')"
                      + ",DE_DET,BL_DOC,DE_RUT_ORI,DE_RUT_DES,CO_TIP_DOC,CO_USE_CRE,\n" +
                       "  FE_USE_CRE,CO_USE_MOD,FE_USE_MOD,FEULA,REQ_FIRMA,NU_DES,TI_PUBLIC\n" +
                       "  FROM IDOSGD.TDTV_ANEXOS  WITH (NOLOCK) \n" +
                       "  where nu_emi='"+pnuEmi+"' and nu_ann='"+pnuAnn+"' and nu_ane='"+pnuAnexo+"'" 
                       );

            try {
                result = this.jdbcTemplate.queryForObject(sql.toString(), String.class);
            } catch (EmptyResultDataAccessException e) {
                result = "S";
            } catch (Exception e) {
                result = "N";
                e.printStackTrace();
            }
        }
        else {
            result = "S";
        }
        
        return result;  
    }
    
    @Override
    public  DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap){
        StringBuffer sql = new StringBuffer();
        sql.append(" select \n" +
                    " nu_ann, \n" +
                    " nu_emi, \n" +
                    " de_ruta_origen ,\n" +
                    " bl_doc \n" +
                    " from IDOSGD.tdtv_archivo_doc \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), new DocumentoRowMapper(docObjBean),new Object[]{ pnuAnn,pnuEmi} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("DocBlob:"+pnuAnn+"."+pnuEmi+"."+ptiCap);
            logger.error(mensaje,e);            
            //e.printStackTrace();
        }

        return(docObjBean);
        
   }

    
@Override
    public DocumentoDatoBean CargarCabeceraReporte(String pnuAnn, String pnuEmi)
    {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT	[IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_emi) de_dep_emi,\n" +
                    "	a.co_dep_emi,\n" +
                    "	a.co_tip_doc_adm,\n" +
                    "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](a.co_tip_doc_adm) de_tip_doc_adm ,\n" +
                    "	A.NU_ANN,A.DE_DOC_SIG,\n" +
                    "	a.nu_dia_ate,\n" +
                    "	LTRIM(RTRIM(a.de_asu)) de_asu,\n" + //"	LTRIM(RTRIM(SUBSTRING(a.de_asu, 1, 400))) de_asu,\n"
                    "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE](A.NU_ANN_EXP, A.NU_SEC_EXP) NU_EXPEDIENTE,\n" +
                    "	CONVERT(VARCHAR(10), FE_EMI, 103) FECHA,\n" +
                    "	CAST(ISNULL(NU_DOC, 0) AS VARCHAR) NU_DOC,\n" +
                    "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](a.co_emp_emi) de_emp_emi,\n" +
                    "	(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_DE_CARGO_DOCUMENTO](a.co_dep_emi,a.co_emp_emi)) cargo_emp_emi,\n" +
                    "COALESCE((SELECT DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR = 'SIGLA_INSTITUCION'),' ') sigla_Institucion\n" +
                    "FROM IDOSGD.tdtv_remitos a WITH (NOLOCK) \n" +
                    "WHERE a.es_eli='0'\n" +
                    "AND a.nu_ann = ?\n" +
                    "AND a.nu_emi = ?");
   
        DocumentoDatoBean docDatoBean = new DocumentoDatoBean();
        
        try {
            docDatoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDatoBean.class),new Object[]{ pnuAnn,pnuEmi} );
        } catch (EmptyResultDataAccessException e) {
            docDatoBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(docDatoBean);
    }
    
    @Override
    public List<DocumentoDatoBean> CargarSubReporte1(String pnuAnn, String pnuEmi)
    {
        StringBuffer sql = new StringBuffer();
            sql.append("select    a.nu_ann NU_ANN_R,\n" +
                        "	a.nu_emi NU_EMI_R,\n" +
                        "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](b.co_tip_doc_adm) + ' Nº ' +\n" +
                        "	CASE B.TI_EMI\n" +
                        "		WHEN '01' THEN B.NU_DOC_EMI + '-' + B.NU_ANN + '-' + B.DE_DOC_SIG\n" +
                        "		WHEN '05' THEN B.NU_DOC_EMI + '-' + B.NU_ANN + '-' + B.DE_DOC_SIG\n" +
                        "		ELSE B.DE_DOC_SIG\n" +
                        "	END DOCUMENTO,\n" +
                        "	b.fe_emi FE_EMI_R,\n" +
                        "	LTRIM(RTRIM(b.de_asu)) DE_ASU_R\n" + // "	LTRIM(RTRIM(SUBSTRING(b.de_asu, 1, 900))) DE_ASU_R\n"
                        "from IDOSGD.TDTR_REFERENCIA a WITH (NOLOCK) ,\n" +
                        "     IDOSGD.TDTV_REMITOS b WITH (NOLOCK) \n" +
                        "where a.nu_emi_ref = b.nu_emi\n" +
                        "and a.nu_ann_ref = b.nu_ann\n" +
                        "and a.nu_ann= ?\n" +
                        "and a.nu_emi= ?");

            List<DocumentoDatoBean> list = new ArrayList<DocumentoDatoBean>();

            try {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDatoBean.class),new Object[]{ pnuAnn,pnuEmi} );
            } catch (EmptyResultDataAccessException e) {
                list = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
    }
    
    @Override
    public List<DocumentoDatoBean> CargarSubReporte2(String pnuAnn, String pnuEmi)
    {
        StringBuffer sql = new StringBuffer();
            sql.append("SELECT    a.nu_ann,\n" +
                        "	a.nu_emi,\n" +
                        "	b.nu_des,\n" +
                        "	a.nu_cor_emi,a.co_dep_emi,\n" +
                        "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](a.co_dep_emi) de_dep_emi,\n" +
                        "	a.co_emp_emi,\n" +
                        "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](a.co_emp_emi) de_emp_emi,\n" +
                        "	a.fe_emi,\n" +
                        "	a.co_tip_doc_adm,\n" +
                        "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_DOCUMENTO](a.co_tip_doc_adm) de_tip_doc_adm,\n" +
                        "	CASE A.TI_EMI\n" +
                        "		WHEN '01' THEN A.NU_DOC_EMI + '-' + A.NU_ANN + '-' + A.DE_DOC_SIG\n" +
                        "		WHEN '05' THEN A.NU_DOC_EMI + '-' + A.NU_ANN + '-' + A.DE_DOC_SIG\n" +
                        "		ELSE A.DE_DOC_SIG\n" +
                        "	END NU_DOC,\n" +
                        "	a.nu_dia_ate,\n" +
                        "	SUBSTRING(a.de_asu,1,400) de_asu,\n" +
                        "	b.co_dep_des,\n" +
                        "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](b.co_dep_des) de_dep_des,\n" +
                        "	b.co_emp_des,\n" +
                        "	CASE b.ti_des\n" +
                        "		WHEN '01' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](b.co_emp_des)\n" +
                        "		WHEN '02' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_PROVEEDOR](B.NU_RUC_DES)\n" +
                        "		WHEN '03' THEN [IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](b.Nu_Dni_Des)\n" +
                        "		WHEN '04' THEN [IDOSGD].[PK_SGD_DESCRIPCION_OTRO_ORIGEN](b.co_otr_ori_des)\n" +
                        "	END de_emp_des,\n" +
//                        "	CASE b.CO_EMP_DES\n" +
//                        "		WHEN (SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA = b.CO_DEP_DES)\n" +
//                        "			THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](b.co_dep_des)\n" +
//                        "		ELSE [IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](b.co_dep_des) + CHAR(10) + (\n" +
                        "		 [IDOSGD].[PK_SGD_DESCRIPCION_DE_DEPENDENCIA](b.co_dep_des) + CHAR(10) + (\n" +
                        "				CASE b.ti_des\n" +
                        "					WHEN '01' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_NOM_EMP](b.co_emp_des)\n" +
                        "					WHEN '02' THEN [IDOSGD].[PK_SGD_DESCRIPCION_DE_PROVEEDOR](B.NU_RUC_DES)\n" +
                        "					WHEN '03' THEN [IDOSGD].[PK_SGD_DESCRIPCION_ANI_SIMIL](b.Nu_Dni_Des)\n" +
                        "					WHEN '04' THEN [IDOSGD].[PK_SGD_DESCRIPCION_OTRO_ORIGEN](b.co_otr_ori_des)\n" +
                        "				END)\n" +
                        //"	END DE_DEP_EMP,\n" +
                        "	DE_DEP_EMP,\n" +
                        "	[IDOSGD].[PK_SGD_DESCRIPCION_MOTIVO](b.co_mot) de_mot,\n" +
                        "	(SELECT DE_PRI FROM IDOSGD.TDTR_PRIORIDAD WHERE CO_PRI = b.co_pri) de_pri,\n" +
                        "	b.de_pro,\n" +
                        "	[IDOSGD].[PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE](A.NU_ANN_EXP,A.NU_SEC_EXP) NU_EXPEDIENTE\n" +
                        "FROM IDOSGD.tdtv_remitos a WITH (NOLOCK) ,\n" +
                        "     IDOSGD.tdtv_destinos b WITH (NOLOCK) \n" +
                        "WHERE a.es_eli = '0'\n" +
                        "AND b.es_eli = '0'\n" +
                        "AND a.nu_ann = b.nu_ann\n" +
                        "AND a.nu_emi = b.nu_emi\n" +
                        "AND a.nu_ann = ?\n" +
                        "AND a.nu_emi = ?\n" +
                        "order by 3");

            List<DocumentoDatoBean> list = new ArrayList<DocumentoDatoBean>();

            try {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDatoBean.class),new Object[]{ pnuAnn,pnuEmi} );
            } catch (EmptyResultDataAccessException e) {
                list = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
    }
     
    @Override
    public List<DocumentoNotificacionBean> CargarReporteNotificacion(String pnuAnn, String pnuEmi, String tiOpe) {
        StringBuffer sql = new StringBuffer();
            sql.append("SELECT  'N° '+ CAST(a.nu_cor_emi AS NVARCHAR(10)) +'-'+ CAST(b.nu_des AS NVARCHAR(10)) +'-'+'"+tiOpe+"' numero_id, \n" +
                        "	a.fe_emi, \n" +
                        "	IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(a.co_tip_doc_adm)   +' '+\n" +
                        "	CASE A.TI_EMI\n" +
                        "		WHEN '01' THEN A.NU_DOC_EMI + '-' + A.NU_ANN + '-' + A.DE_DOC_SIG\n" +
                        "		WHEN '05' THEN A.NU_DOC_EMI + '-' + A.NU_ANN + '-' + A.DE_DOC_SIG\n" +
                        "		ELSE A.DE_DOC_SIG\n" +
                        "	END nro_documento, \n" +
                        "	(CASE b.ti_des\n" +
                        "		WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(b.co_emp_des) \n" +
                        "		WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(b.REMI_NU_DNI_EMI)\n" +
                        "		WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(b.Nu_Dni_Des)\n" +
                        "		WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(b.co_otr_ori_des)\n" +
                        "	END)+' - '+b.DE_CARGO destinatario,\n" +
                        "    ( CASE b.ti_des \n" +
                        "					WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(B.NU_RUC_DES)				 \n" +
                        "				END) razon_social ,\n" +
                        "	IDOSGD.PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE(A.NU_ANN_EXP,A.NU_SEC_EXP) expediente,\n" +
                        "    b.CDIR_REMITE \n" +
                        "    + ',' + (SELECT  RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_get_departamento(b.ccod_dpto)) \n" +
                        "    + '-' + (SELECT  RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_get_provincia (b.ccod_dpto, b.ccod_prov)) \n" +
                        "    + '-' +  (SELECT  RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_get_distrito (b.ccod_dpto,b.ccod_prov,b.ccod_dist)) direccion, '' referencia,'' folio,\n" +
                        "    (SELECT TOP 1 DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR='NOMBRE_ANIO'  ) nombre_anio_1,\n" +
                        "    (SELECT TOP 1 DE_PAR FROM IDOSGD.TDTR_PARAMETROS WHERE CO_PAR='NOMBRE_ANIO2'  ) nombre_anio_2\n" +
                        "FROM IDOSGD.tdtv_remitos a  WITH (NOLOCK) \n" +
                        "inner join IDOSGD.tdtv_destinos b  WITH (NOLOCK) on a.nu_ann = b.nu_ann and  a.nu_emi = b.nu_emi\n" +
                        "WHERE a.es_eli = '0' AND b.es_eli = '0' \n" +
                        "AND a.nu_ann = ? AND a.nu_emi = ? \n" +
                        "order by b.nu_des ");

             List<DocumentoNotificacionBean> list = new ArrayList<DocumentoNotificacionBean>();

            try {
                list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoNotificacionBean.class),new Object[]{ pnuAnn,pnuEmi} );
            } catch (EmptyResultDataAccessException e) {
                list = null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return list;
    }
        @Override
    public DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi, String ptiCap, String pAbreWord) {
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "nu_ann, \n" +
                    "nu_emi, \n" +
                    "de_ruta_origen ,\n" +
                    "w_bl_doc bl_doc \n" +
                    "from IDOSGD.tdtv_archivo_doc  WITH (NOLOCK) \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), new DocumentoRowMapper(docObjBean),new Object[]{ pnuAnn,pnuEmi} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("DocBlob:"+pnuAnn+"."+pnuEmi+"."+ptiCap);
            logger.error(mensaje,e);            
            //e.printStackTrace();
        }

        return(docObjBean);
    }
    
 
    private class DocumentoRowMapper implements ParameterizedRowMapper<DocumentoObjBean> {
        private DocumentoObjBean docObjBean;
        public DocumentoRowMapper(){

        }
        public DocumentoRowMapper(DocumentoObjBean docObjBean){
            this.docObjBean = docObjBean;
        }

        public DocumentoObjBean mapRow(ResultSet rs, int i) throws SQLException {
            int size=0;
            try {
                java.sql.Blob documento = rs.getBlob("bl_doc");
                if(documento!=null){
                    /*
                    size=0;
                    size=(int)documento.length();
                    this.docObjBean.setDocumento(documento.getBytes(1, size));
                    */
                    this.docObjBean.setNuAnn(rs.getString("nu_ann"));
                    this.docObjBean.setNuEmi(rs.getString("nu_emi"));
                    this.docObjBean.setNombreArchivo(rs.getString("de_ruta_origen"));
                    InputStream blobStream = null;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                         blobStream = documento.getBinaryStream();
                         byte[] buffer = new byte[1024*8];
                         int bytesRead;
                         do {
                              bytesRead = blobStream.read(buffer);
                              if (bytesRead > 0) {
                                  baos.write(buffer, 0, bytesRead);
                              }
                         } while (bytesRead > -1);
                        baos.flush();
                        this.docObjBean.setDocumento(baos.toByteArray());
                    }finally {
                         if (blobStream != null) {
                              blobStream.close();
                         }
                         if (baos != null) {
                              baos.close();
                         }
                    }                    
                }
                documento.free();
            } catch (Exception e) {
                StringBuffer mensaje = new StringBuffer();
                mensaje.append("LeerBlob:"+this.docObjBean.getNuAnn()+"."+this.docObjBean.getNuEmi());
                logger.error(mensaje,e);            
                //e.printStackTrace();
            }
            return this.docObjBean;
        }
    }
    

    @Override
    public  DocumentoObjBean getNombreArchivoAnexo(String pnuAnn, String pnuEmi, String pnuAnexo){
        StringBuffer sql = new StringBuffer();
        sql.append("	select \n" +
                    " nu_ann,\n" +
                    " nu_emi,\n" +
                    " nu_ane,\n" +
                    " de_rut_ori nombre_Archivo,\n" +
                    " COALESCE(LEN(bl_doc),0) nu_Tamano\n" +
                    " from IDOSGD.tdtv_anexos \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?\n" +
                    "and nu_ane = ?");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class),new Object[]{ pnuAnn,pnuEmi,pnuAnexo} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(docObjBean);
    }
    
       @Override
    public  DocumentoObjBean getNombreArchivoAnexoArchivado(String pnuAnn, String pnuEmi, String pnuAnexo){
        StringBuffer sql = new StringBuffer();
        sql.append("	select \n" +
                    " nu_ann,\n" +
                    " nu_emi,\n" +
                    " nu_ane,\n" +
                    " de_rut_ori nombre_Archivo,\n" +
                    " COALESCE(LEN(bl_doc),0) nu_Tamano\n" +
                    " from IDOSGD.tdtv_anexos  WITH (NOLOCK)  \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?\n" +
                    "and nu_ane = ?");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class),new Object[]{ pnuAnn,pnuEmi,pnuAnexo} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(docObjBean);
    }
   
    
    @Override
    public  DocumentoObjBean leerDocumentoAnexo(String pnuAnn, String pnuEmi, String pnuAne){
        StringBuffer sql = new StringBuffer();
        sql.append(" select \n" +
                    " nu_ann,\n" +
                    " nu_emi,\n" +
                    " nu_ane,\n" +
                    " de_rut_ori de_ruta_origen,\n" +
                    " bl_doc\n" +
                    " from IDOSGD.tdtv_anexos WITH (NOLOCK)  \n" +
                    " where nu_ann = ?\n" +
                    " and nu_emi = ?\n" +
                    " and nu_ane = ?");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), new DocumentoRowMapper(docObjBean),new Object[]{ pnuAnn,pnuEmi,pnuAne} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("Anexo:"+pnuAnn+"."+pnuEmi+"."+pnuAne);
            logger.error(mensaje,e);            
            //e.printStackTrace();
        }

        return(docObjBean);
        
   }
    
    @Override
    public  DocumentoObjBean leerActaNotificacion(String pnuAnn, String pnuEmi, String pnuDes, String pnuActa){
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "nu_ann,\n" +
                    "nu_emi,\n" +
//                    "nu_des,\n" +
                    "de_rut_ori de_ruta_origen,\n" +
                    "bl_doc\n" +
                    "from IDOSGD.TD_DET_MENSAJERIA\n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ?\n" +
                    "and nu_des = ?\n"+
                    "and nu_acta = ?\n");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), new DocumentoRowMapper(docObjBean),new Object[]{ pnuAnn,pnuEmi,pnuDes,pnuActa} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("Acta:"+pnuAnn+"."+pnuEmi+"."+pnuDes+"."+pnuActa);
            logger.error(mensaje,e);            
            //e.printStackTrace();
        }

        return(docObjBean);
        
   }
       @Override
    public  List<DocumentoObjBean> leerDocumentoAnexo(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "nu_ann,\n" +
                    "nu_emi,\n" +
                    "nu_ane,\n" +
                    "de_rut_ori nombre_Archivo,\n" +
                    "bl_doc documento \n" +
                    "from IDOSGD.tdtv_anexos WITH (NOLOCK) \n" +
                    "where nu_ann = ?\n" +
                    "and nu_emi = ? ");
   
        List<DocumentoObjBean> docObjBean = new ArrayList<DocumentoObjBean>();

        try {
            
            docObjBean = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBean.class),new Object[]{ pnuAnn,pnuEmi} );
            
             
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("Anexo:"+pnuAnn+"."+pnuEmi);
            logger.error(mensaje,e);            
            //e.printStackTrace();
        }

        return(docObjBean);
        
   }
    
    
    @Override
    public String getInFirmaDoc(String pcoDep,String pcoTipoDoc){
        StringBuffer sql = new StringBuffer();
        String result = null;
        sql.append(" SELECT (\n" +
                    " CASE es_obl_firma when '0' THEN 'N'\n" +
                    " ELSE 'F' END\n" +
                    " )  FROM IDOSGD.sitm_doc_dependencia \n" +
                    " WHERE co_dep     = ?\n" +
                    " AND co_tip_doc = ?");        
        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pcoDep, pcoTipoDoc});
        } catch (EmptyResultDataAccessException e) {
            result = "F";
        } catch (Exception e) {
            result = "F";
            e.printStackTrace();
        }
        
        return result;        
    }
    
    @Override
    public String insRemitosBlock(DocumentoObjBlock docBlock){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO IDOSGD.TDTV_REMITOS_BLOCK(NU_ANN,NU_EMI,NU_SEC_FIRMA,\n" +
                    "FECHA_FIRMA,TIPO_FIRMA)\n" +
                    "VALUES(?,?,?,\n" +
                    "GETDATE(),?)");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{docBlock.getNuAnn(),docBlock.getNuEmi(),docBlock.getNuSecFirma(),docBlock.getTipoFirma()});
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Datos Duplicado INSERT IDOSGD.TDTV_REMITOS_BLOCK.";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;  
    }
    
    @Override
    public String updRemitosBlock(DocumentoObjBlock docBlock) {
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("UPDATE IDOSGD.TDTV_REMITOS_BLOCK \n"
                + " SET NU_SEC_FIRMA=?\n"
                + ",FECHA_FIRMA=getdate()\n"
                + ",TIPO_FIRMA=?\n"
                + " WHERE\n"
                + " NU_ANN=? and\n"
                + " NU_EMI=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{docBlock.getNuSecFirma(),docBlock.getTipoFirma(),docBlock.getNuAnn(),docBlock.getNuEmi()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();
        }
        return vReturn;
    }    
    
    @Override
    public DocumentoObjBlock getDatosDocBlock(String pnuAnn,String pnuEmi){
        StringBuffer sql = new StringBuffer();
        DocumentoObjBlock docBlock = null;
        sql.append(" SELECT RB.NU_ANN,RB.NU_EMI,RB.NU_SEC_FIRMA,\n" +
                    " RB.FECHA_FIRMA,RB.TIPO_FIRMA,getdate() FECHA_ACTUAL\n" +
                    " FROM IDOSGD.TDTV_REMITOS_BLOCK RB\n" +
                    " WHERE RB.NU_ANN=?\n" +
                    " AND RB.NU_EMI=?");        
        
        try {
             docBlock = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoObjBlock.class),
                    new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            docBlock = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return docBlock;              
    }    

}
