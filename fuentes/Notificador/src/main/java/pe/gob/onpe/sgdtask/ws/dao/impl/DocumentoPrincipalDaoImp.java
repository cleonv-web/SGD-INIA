/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sgdtask.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.sgdtask.ws.bean.DocumentoPrincipalBean;
import pe.gob.onpe.sgdtask.ws.dao.DocumentoPrincipalDao;

/**
 *
 * @author fSilva
 */
@Repository
public class DocumentoPrincipalDaoImp extends SimpleJdbcDaoBase implements DocumentoPrincipalDao{
    
    private static Logger logger=Logger.getLogger(DocumentoPrincipalDaoImp.class);    
    
    @Override
    public DocumentoPrincipalBean getDocumentoPrincipal(long coDocumentoExterno){
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pcoDocumentoExterno", coDocumentoExterno);
        StringBuilder query = new StringBuilder();
        query.append("SELECT \n"
                + "PRI.CO_DOCUMENTO_PRINCIPAL,\n"
                + "PRI.CO_DOCUMENTO_EXTERNO,\n"
                + "PRI.BL_DOC\n"
                + "FROM WSTD_DOCUMENTO_PRINCIPAL PRI\n" 
                + "WHERE PRI.CO_DOCUMENTO_EXTERNO=:pcoDocumentoExterno");
   
        List<DocumentoPrincipalBean> lista = null;
        DocumentoPrincipalBean documentoPrincipalBean=null;
        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<DocumentoPrincipalBean>() {
                public DocumentoPrincipalBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    DocumentoPrincipalBean documentoPrincipalBean = new DocumentoPrincipalBean();
                    documentoPrincipalBean.setCoDocumentoPrincipal(rs.getLong("CO_DOCUMENTO_PRINCIPAL"));
                    documentoPrincipalBean.setCoDocumentoExterno(rs.getLong("CO_DOCUMENTO_EXTERNO"));
                    documentoPrincipalBean.setBlDocumento(rs.getBytes("BL_DOC"));
                    return documentoPrincipalBean;
                }
            });
            documentoPrincipalBean = lista.get(0);
        } catch (EmptyResultDataAccessException e) {
            lista = null;
            e.printStackTrace();
        } catch (Exception e) {
            lista = null;
            e.printStackTrace();
        }
        return documentoPrincipalBean;
    }
         
}
