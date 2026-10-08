package pe.gob.onpe.verificadoc.dao.impl.sqlserver;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.verificadoc.bean.DocumentoObjBean;
import pe.gob.onpe.verificadoc.dao.DocumentoObjDao;
import pe.gob.onpe.verificadoc.dao.SimpleJdbcDaoBase;


@Repository("documentoObjDao")
@Profile("sqlserver")
public class DocumentoObjDaoImp extends SimpleJdbcDaoBase implements DocumentoObjDao {
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
             
    @Override
    public  DocumentoObjBean leerDocumento(String pnuAnn, String pnuEmi){
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
            mensaje.append("DocBlob:"+pnuAnn+"."+pnuEmi);
            LOGGER.error(mensaje, e);            
            //LOGGER.error(e.getMessage(), e);
        }

        return(docObjBean);
        
   }
    
   @Override
    public  DocumentoObjBean leerAnexo(String nuAnn, String nuEmi, String nuAne){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT NU_ANN, ");
        sql.append("    NU_EMI, ");
        sql.append("    NU_ANE, ");
        sql.append("    DE_DET, ");
        sql.append("    BL_DOC ");
        sql.append(" FROM IDOSGD.TDTV_ANEXOS ");
        sql.append(" WHERE NU_ANN = ? ");
        sql.append("   AND NU_EMI = ? ");
        sql.append("   AND NU_ANE = ? ");
   
        DocumentoObjBean docObjBean = new DocumentoObjBean();

        try {
            docObjBean = this.jdbcTemplate.queryForObject(sql.toString(), new AnexoRowMapper(docObjBean),new Object[]{ nuAnn, nuEmi, nuAne} );
        } catch (EmptyResultDataAccessException e) {
            docObjBean = null;
        } catch (Exception e) {
            StringBuffer mensaje = new StringBuffer();
            mensaje.append("DocBlobAnexo:" + nuAnn + "." + nuEmi + "." + nuAne);
            LOGGER.error(mensaje, e);            
            //LOGGER.error(e.getMessage(), e);
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
                LOGGER.error(mensaje, e);            
                //LOGGER.error(e.getMessage(), e);
            }
            return this.docObjBean;
        }
    }
    
    private class AnexoRowMapper implements ParameterizedRowMapper<DocumentoObjBean> {
        private DocumentoObjBean anexoObjBean;
        public AnexoRowMapper(){

        }
        public AnexoRowMapper(DocumentoObjBean anexoObjBean){
            this.anexoObjBean = anexoObjBean;
        }

        public DocumentoObjBean mapRow(ResultSet rs, int i) throws SQLException {
            int size=0;
            try {
                java.sql.Blob anexo = rs.getBlob("BL_DOC");
                if(anexo!=null){
                    this.anexoObjBean.setNuAnn(rs.getString("NU_ANN"));
                    this.anexoObjBean.setNuEmi(rs.getString("NU_EMI"));
                    this.anexoObjBean.setNuAne(rs.getString("NU_ANE"));
                    this.anexoObjBean.setNombreArchivo(rs.getString("DE_DET"));
                    InputStream blobStream = null;
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    try {
                         blobStream = anexo.getBinaryStream();
                         byte[] buffer = new byte[1024*8];
                         int bytesRead;
                         do {
                              bytesRead = blobStream.read(buffer);
                              if (bytesRead > 0) {
                                  baos.write(buffer, 0, bytesRead);
                              }
                         } while (bytesRead > -1);
                        baos.flush();
                        this.anexoObjBean.setDocumento(baos.toByteArray());
                    }finally {
                         if (blobStream != null) {
                              blobStream.close();
                         }
                         if (baos != null) {
                              baos.close();
                         }
                    }                    
                }
                anexo.free();
            } catch (Exception e) {
                StringBuffer mensaje = new StringBuffer();
                mensaje.append("LeerBlob Anexo:" + this.anexoObjBean.getNuAnn() + "." + this.anexoObjBean.getNuEmi() + "." + this.anexoObjBean.getNuAne());
                LOGGER.error(mensaje, e);            
                //LOGGER.error(e.getMessage(), e);
            }
            return this.anexoObjBean;
        }
    }
    
}
