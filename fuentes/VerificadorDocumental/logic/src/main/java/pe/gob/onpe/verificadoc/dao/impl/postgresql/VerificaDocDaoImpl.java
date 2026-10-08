/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.dao.impl.postgresql;

import java.util.ArrayList;
import java.util.List;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.context.annotation.Profile;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.verificadoc.bean.AnexoBean;
import pe.gob.onpe.verificadoc.bean.TipoDocBean;
import pe.gob.onpe.verificadoc.bean.VerificaDocBean;
import pe.gob.onpe.verificadoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.verificadoc.dao.VerificaDocDao;

/**
 *
 * @author RATAYAURI
 */
@Repository("verificaDocDao")
@Profile("postgresql")
public class VerificaDocDaoImpl extends SimpleJdbcDaoBase implements VerificaDocDao{
    
    private transient final Log LOGGER = LogFactory.getLog(getClass());
    
    @Override
    public List<TipoDocBean> getTiposDocs(){
        StringBuffer sql = new StringBuffer();
         
        sql.append(" SELECT DISTINCT TD.CDOC_TIPDOC CO_TIP_DOC, TD.CDOC_DESDOC DE_TIP_DOC \n" +
                    " FROM IDOSGD.SI_MAE_TIPO_DOC TD \n" +
                    " INNER JOIN IDOSGD.TDTR_PLANTILLA_DOCX P ON TD.CDOC_TIPDOC = P.CO_TIPO_DOC \n" +
                    " WHERE TD.IN_DOC_SALIDA = '1' AND P.ES_DOC = '1' ORDER BY DE_TIP_DOC  ");
        
        
        List<TipoDocBean> list1 = new ArrayList<TipoDocBean>();
        list1.add(new TipoDocBean(null, "[SELECCIONAR]"));
        
        List<TipoDocBean> list = new ArrayList<TipoDocBean>();
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocBean.class));
            
            list1.addAll(list);
            
        }catch (EmptyResultDataAccessException e) {
            list1 = null;
        }catch (Exception e) {
            list1 = null;
        }
        
        return list1;
    }
    
    @Override
    public String getDescTipoDoc(String codTipoDoc){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT  TD.CDOC_DESDOC DE_TIP_DOC ");
        sql.append("FROM IDOSGD.SI_MAE_TIPO_DOC TD ");
        sql.append("WHERE TD.CDOC_TIPDOC = ? ");
        
        TipoDocBean doc = new TipoDocBean();
        
        try {
            doc = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(TipoDocBean.class), new Object[]{codTipoDoc});
        }catch (EmptyResultDataAccessException e) {
            doc = null;
        }catch (Exception e) {
            doc = null;
        }
        
        if(doc != null){
            return doc.getDeTipDoc();
        }else{
            return null;
        }
    }
    
    @Override
    public VerificaDocBean getDocRemitosBusq(String nuEmi, String nuAnno){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT  R.CO_TIP_DOC_ADM TIPO_DOC, R.SIGLA_DOC NUM_DOC, R.NU_ANN, R.NU_EMI, R.COD_VER_EXT as CLAVE_ACCESO_DOC ");
        sql.append("FROM IDOSGD.TDTV_REMITOS R ");
        sql.append("WHERE R.NU_EMI = ? ");
        sql.append("  AND R.NU_ANN = ? ");
        
        VerificaDocBean doc = new VerificaDocBean();
        
        try {
            doc = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(VerificaDocBean.class), new Object[]{nuEmi, nuAnno});
            
        }catch (EmptyResultDataAccessException e) {
            doc = null;
        }catch (Exception e) {
            doc = null;
        }
        
        return doc;
    }
    
    @Override
    public VerificaDocBean getDocRemitos(VerificaDocBean doc){
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT R.NU_ANN, R.NU_EMI ");
        sql.append("FROM IDOSGD.TDTV_REMITOS R ");
        sql.append("WHERE R.CO_TIP_DOC_ADM = ? ");
         sql.append("AND R.nu_ann = ? ");
        sql.append("AND LTRIM(R.nu_doc_emi,'0')=TRIM(LTRIM(REPLACE(?,'D',''),'0')) ");
        sql.append("AND UPPER(TRIM(R.COD_VER_EXT)) = ? ");        
        sql.append("AND ES_DOC_EMI NOT IN ('5', '7', '9') ");
        
        VerificaDocBean docVerifica= new VerificaDocBean();
        
        try {
            docVerifica = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(VerificaDocBean.class), 
                     new Object[]{ doc.getTipoDoc(), doc.getNuAnn(),doc.getNumDoc(), doc.getClaveAccesoDoc()});
        }catch (EmptyResultDataAccessException e) {
            docVerifica = null;
        }catch (Exception e) {
            docVerifica = null;
            LOGGER.error(e.getMessage(), e);
        }
        
        return docVerifica;
    }
    
    @Override
    public String getCodVerifica(String nuAnn, String nuEmi){
        StringBuffer sql = new StringBuffer();
        
        sql.append(" SELECT R.COD_VER_EXT as CLAVE_ACCESO_DOC ");
        sql.append(" FROM IDOSGD.TDTV_REMITOS R ");
        sql.append(" WHERE R.CO_TIP_DOC_ADM = ? ");
        sql.append("WHERE R.NU_ANN = ? ");
        sql.append("AND R.NU_EMI = ? ");
        
        VerificaDocBean docVerifica= new VerificaDocBean();
        
        try {
            docVerifica = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(VerificaDocBean.class), 
                    new Object[]{nuAnn, nuEmi});
        }catch (EmptyResultDataAccessException e) {
            docVerifica = null;
        }catch (Exception e) {
            docVerifica = null;
            LOGGER.error(e.getMessage(), e);
        }
        
        if(docVerifica != null){
            return docVerifica.getClaveAccesoDoc();
        }else{
            return null;
        }
        
    }

    @Override
    public List<AnexoBean> getAnexosList(String nuAnn, String nuEmi) {
        StringBuffer sql = new StringBuffer();
        
        sql.append("SELECT ");
        sql.append("NU_ANN, NU_EMI, NU_ANE, DE_DET, DE_RUT_ORI, CO_TIP_DOC ");
        sql.append("FROM IDOSGD.TDTV_ANEXOS ");
        sql.append("WHERE NU_ANN = ? ");
        sql.append("AND NU_EMI = ? ");
        sql.append("ORDER BY NU_ANE ");
        
        List<AnexoBean> lista = new ArrayList<AnexoBean>();
        
        try {
            lista = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(AnexoBean.class), new Object[]{nuAnn,nuEmi});
            
        }catch (EmptyResultDataAccessException e) {
            lista = null;
            LOGGER.error(e.getMessage(), e);
        }catch (Exception e) {
            lista = null;
            LOGGER.error(e.getMessage(), e);
        }
        
        return lista;
    }
    
}
