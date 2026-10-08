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
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sgdtask.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.sgdtask.ws.bean.DocumentoExternoBean;
import pe.gob.onpe.sgdtask.ws.bean.EmisionExternaBean;
import pe.gob.onpe.sgdtask.ws.dao.EmisionExternaDao;

/**
 *
 * @author ecueva
 */
@Repository("emisionExternaSoapDao")
public class EmisionExternaDaoImp extends SimpleJdbcDaoBase implements EmisionExternaDao {

    @Override
    public String updRecepcionexterna(EmisionExternaBean emisionExternaBean) {
        String vReturn = "NO_OK";
        StringBuilder sqlUpd = new StringBuilder();
        sqlUpd.append("UPDATE WSTC_EMISION_EXTERNA \n"
                + "SET CO_RECEPCION_EXTERNA = ?,\n"
                + "IN_ENVIO = '1',\n"
                + "BL_SIGNATURE_EMISION = ?,\n"
                + "BL_SIGNATURE_RECEPCION = ?,\n"
                + "FE_ENVIO = ?\n"
                + "WHERE CO_EMISION = ?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(),
                    new Object[]{emisionExternaBean.getCoRecepcionExterna(),
                        emisionExternaBean.getBlSignatureEmision(),
                        emisionExternaBean.getBlSignatureRecepcion(),
                        emisionExternaBean.getFeEnvio(),
                        emisionExternaBean.getCoEmision()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public EmisionExternaBean getEmisionExterna(long coEmision) {
        EmisionExternaBean emisionExternaBean = null;
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoEmision", coEmision);
        StringBuilder query = new StringBuilder();
        query.append("SELECT "
                + "    CO_EMISION,\n"
                + "    CO_ENTIDAD_EXTERNA,\n"
                + "    NO_ENTIDAD_EXTERNA,\n"
                + "    NU_ANN,\n"
                + "    NU_EMI,\n"
                + "    FE_EMISION,\n"
                + "    NU_ANEXOS,\n"
                + "    IN_TUPA \n"
                + "    FROM WSTC_EMISION_EXTERNA "
                + "    WHERE CO_EMISION=:pCoEmision");

        // define lista retornar
        List<EmisionExternaBean> lista = null;

        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<EmisionExternaBean>() {
                public EmisionExternaBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    EmisionExternaBean emisionExternaBean = new EmisionExternaBean();
                    emisionExternaBean.setCoEmision(rs.getLong("CO_EMISION"));
                    emisionExternaBean.setCoEntidadExterna(rs.getString("CO_ENTIDAD_EXTERNA"));
                    emisionExternaBean.setNoEntidadExterna(rs.getString("NO_ENTIDAD_EXTERNA"));
                    emisionExternaBean.setFeEmiSgd(rs.getDate("FE_EMISION"));
                    emisionExternaBean.setNuAnexos(rs.getInt("NU_ANEXOS"));
                    emisionExternaBean.setInTupa(rs.getString("IN_TUPA"));
                    return emisionExternaBean;
                }
            });
            emisionExternaBean = lista.get(0);
        } catch (EmptyResultDataAccessException e) {
            lista = null;
            e.printStackTrace();
        } catch (Exception e) {
            lista = null;
            e.printStackTrace();
        }
        //return lista;        
        return emisionExternaBean;
    }

    @Override
    public List<EmisionExternaBean> getListEmisionExternaPendientes() {
        Map<String, Object> objectParam = new HashMap<String, Object>();
        StringBuilder query = new StringBuilder();
        query.append("SELECT "
                + " EMI.CO_EMISION,\n"
                + " EMI.IN_ENVIO\n"
                + " FROM WSTC_EMISION_EXTERNA EMI\n"
                + " WHERE EMI.CO_ESTADO='1' AND EMI.IN_ENVIO='0'");
        // define lista retornar
        List<EmisionExternaBean> lista = null;
        //consulta lista de colegios
        try {
            lista = namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<EmisionExternaBean>() {
                public EmisionExternaBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                    EmisionExternaBean emisionExternaBean = new EmisionExternaBean();
                    emisionExternaBean.setCoEmision(rs.getLong("CO_EMISION"));
                    emisionExternaBean.setInEnvio(rs.getString("IN_ENVIO"));
                    return emisionExternaBean;
                }
            });
        } catch (EmptyResultDataAccessException e) {
            lista = null;
            e.printStackTrace();
        } catch (Exception e) {
            lista = null;
            e.printStackTrace();
        }
        //return lista;        
        return lista;
    }
    @Override
    public DocumentoExternoBean getDocumentoExterno(long coEmision){
        String sql = "SELECT "
                + "DOC.CO_EMISION, "
                + "DOC.CO_DOCUMENTO_EXTERNO, "
                + "DOC.CO_TIPO_DOCUMENTO, "
                + "DOC.DE_ASUNTO, "
                + "DOC.NO_TIPO_DOCUMENTO, "
                + "DOC.NU_DOCUMENTO, "
                + "DOC.NO_UNIDAD_ORG_DESTINO, "
                + "DOC.NO_CARGO_DESTINO, "
                + "DOC.NO_DESTINO, "
                + "DOC.FE_DOCUMENTO\n"
                + "FROM WSTC_DOCUMENTO_EXTERNO DOC\n"
                + "WHERE DOC.CO_EMISION=?";

        DocumentoExternoBean doc = new DocumentoExternoBean();
        try {
            doc = this.jdbcTemplate.queryForObject(sql, BeanPropertyRowMapper.newInstance(DocumentoExternoBean.class),
                    new Object[]{coEmision});
        } catch (EmptyResultDataAccessException e) {
            doc = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    } 
}
