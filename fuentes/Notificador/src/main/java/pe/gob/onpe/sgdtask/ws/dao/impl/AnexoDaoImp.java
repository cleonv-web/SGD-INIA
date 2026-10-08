/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.dao.impl;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.log4j.Logger;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sgdtask.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.sgdtask.ws.bean.AnexoBean;
import pe.gob.onpe.sgdtask.ws.dao.AnexoDao;

/**
 *
 * @author fSilva
 */
@Repository
public class AnexoDaoImp extends SimpleJdbcDaoBase implements AnexoDao{
    
    private static Logger logger=Logger.getLogger(AnexoDaoImp.class);    
    
    @Override
    public List<AnexoBean> getLisAnexos(long coDocumentoExterno){
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pcoDocumentoExterno", coDocumentoExterno);
        StringBuilder query = new StringBuilder();
        query.append("SELECT \n"
                + "ANE.CO_ANEXO,\n"
                + "ANE.CO_DOCUMENTO_EXTERNO,\n"
                + "ANE.DE_DESCRIPCION,\n"
                + "ANE.BL_DOC\n"
                + "FROM WSTD_ANEXO ANE\n"
                + "WHERE ANE.CO_DOCUMENTO_EXTERNO =:pcoDocumentoExterno AND ANE.CO_ESTADO='1'");
   
        List<AnexoBean> lista = null;
        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<AnexoBean>() {
                public AnexoBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    AnexoBean anexoBean = new AnexoBean();
                    anexoBean.setCoAnexo(rs.getLong("CO_ANEXO"));
                    anexoBean.setCoDocumentoExterno(rs.getLong("CO_DOCUMENTO_EXTERNO"));
                    anexoBean.setDeDescripcion(rs.getString("DE_DESCRIPCION"));
                    anexoBean.setBlDocAnexo(rs.getBytes("BL_DOC"));
                    return anexoBean;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            lista = null;
            e.printStackTrace();
        } catch (Exception e) {
            lista = null;
            e.printStackTrace();
        }
        return lista;
    }
    @Override
    public List<String> getCanDocAnexos(String coDocExterno){
        List<String> list = new ArrayList<String>();
        try {
            list = this.jdbcTemplate.queryForList("SELECT CO_ANEXO FROM WSTD_ANEXO\n" +
                                                    "WHERE CO_DOCUMENTO_EXTERNO=? ", String.class, new Object[]{coDocExterno});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list; 
    }   
}
