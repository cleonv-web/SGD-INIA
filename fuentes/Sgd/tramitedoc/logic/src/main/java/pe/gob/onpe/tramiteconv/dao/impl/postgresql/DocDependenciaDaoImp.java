/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.MotivoDocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.dao.DocDependenciaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.util.Paginacion;

/**
 *
 * @author ECueva
 */
@Repository("docDependenciaDao")
public class DocDependenciaDaoImp extends SimpleJdbcDaoBase implements DocDependenciaDao {

 

    @Override
    public DocumentoDependenciaBean getDocDependencia(String codDependencia, String codTipDoc) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT CO_DEP,CO_TIP_DOC,TI_DESCRIP,CO_USE_CRE,FE_USE_CRE,CO_USE_MOD,FE_USE_MOD,ES_OBL_FIRMA,CO_TIP_DOC AS CO_TIP_DOC_ANT ");
        sql.append("FROM ( SELECT t.CO_DEP, t.CO_TIP_DOC, t.ES_ELI, t.CO_USE_CRE,\n" +
                        " t.FE_USE_CRE, t.CO_USE_MOD, t.FE_USE_MOD,\n" +
                        " t.ES_OBL_FIRMA,\n" +
                        " IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(T.CO_TIP_DOC) ti_descrip\n" +
                        " FROM IDOSGD.sitm_doc_dependencia t ) SIVM_DOC_DEPENDENCIA ");
        sql.append("WHERE CO_DEP=? AND CO_TIP_DOC=? AND ES_ELI='0' ");

        DocumentoDependenciaBean documentoDependenciaBean = new DocumentoDependenciaBean();
        try {
            documentoDependenciaBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDependenciaBean.class),
                    new Object[]{codDependencia, codTipDoc});
        } catch (EmptyResultDataAccessException e) {
            documentoDependenciaBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (documentoDependenciaBean.getEsOblFirma() != null && documentoDependenciaBean.getEsOblFirma().equals("1")) {
            documentoDependenciaBean.setbEsOblFirma(true);
        }
        if (documentoDependenciaBean.getInGeneOfic() != null && documentoDependenciaBean.getInGeneOfic().equals("1")) {
            documentoDependenciaBean.setbInGeneOfic(true);
        }
        return documentoDependenciaBean;
    }

    @Override
    public int getRowCountDocXDependencia(String codDependencia) {
        int result = -1;
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT  COUNT(1) ");
        sql.append("FROM ( SELECT t.CO_DEP, t.CO_TIP_DOC, t.ES_ELI, t.CO_USE_CRE,\n" +
                        " t.FE_USE_CRE, t.CO_USE_MOD, t.FE_USE_MOD,\n" +
                        " t.ES_OBL_FIRMA,\n" +
                        " IDOSGD.PK_SGD_DESCRIPCION_DE_DOCUMENTO(T.CO_TIP_DOC) ti_descrip\n" +
                        " FROM IDOSGD.sitm_doc_dependencia t ) SIVM_DOC_DEPENDENCIA ");
        sql.append("WHERE CO_DEP=? AND ES_ELI='0' ");

        try {
            result = this.jdbcTemplate.queryForInt(sql.toString(), new Object[]{codDependencia});
        } catch (Exception e) {
            e.printStackTrace();
        }
        return result;
    }

//    @Override
//    public String updDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean) {
//        String vReturn = "NO_OK";
//        StringBuffer sql = new StringBuffer();
//        sql.append("UPDATE IDOPRETRAM.PTTV_PRE_FICHA ");
//        sql.append("SET ES_PRE_TRAMITE = '02',NU_FICHA_REG = ?,TI_DOC_REG_FICHA = ?,FE_REGI = TO_DATE(?,'DD/MM/YYYY HH24:MI:SS') ");
//        sql.append("WHERE NU_PRE_FICHA = ?");
//        try {
//            int retorno = this.jdbcTemplate.update(sql.toString(), new Object[]{});
//            vReturn = "OK";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return vReturn;
//    }

//    @Override
//    public String insDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean) {
//        String vReturn = "NO_OK";
//        StringBuffer sql = new StringBuffer();
//        sql.append("INSERT IDOPRETRAM.PTTV_PRE_FICHA ");
//        sql.append("NU_FICHA_REG, TI_DOC_REG_FICHA, CO_CONTINENTE_NACI, CO_CONTINENTE_DOMICILIO, CO_PAIS_NACI, CO_PAIS_DOMICILIO, CO_PROVINCIA_NACI ");
//        sql.append("VALUES(?,?,?,?,?,?,?");
//        try {
//            int retorno = this.jdbcTemplate.update(sql.toString(), new Object[]{});
//            vReturn = "OK";
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return vReturn;
//    }

    @Override
    public List<DocumentoDependenciaBean> getDocDependenciaList(String codDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("select A.CO_TIP_DOC CO_TIP_DOC,B.CDOC_DESDOC TI_DESCRIP from IDOSGD.SITM_DOC_DEPENDENCIA A, IDOSGD.si_mae_tipo_doc B ");
        sql.append("where B.CDOC_GRUPO IN ('01','03','02') AND ");
        sql.append("A.CO_TIP_DOC=B.CDOC_TIPDOC AND ");
        sql.append("co_dep=? ORDER BY B.CDOC_DESDOC");
        List<DocumentoDependenciaBean> list = new ArrayList<DocumentoDependenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDependenciaBean.class),
                    new Object[]{codDependencia});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<DocumentoDependenciaBean> getDocDependenciaFaltantesList(String codDependencia) {
        StringBuffer sql = new StringBuffer();
        sql.append("select CDOC_TIPDOC CO_TIP_DOC, CDOC_DESDOC TI_DESCRIP ");
        sql.append("from IDOSGD.si_mae_tipo_doc ");
        sql.append("WHERE CDOC_GRUPO in ('01','03','02') ");
        sql.append("AND CDOC_TIPDOC NOT IN ( SELECT CO_TIP_DOC FROM IDOSGD.SITM_DOC_DEPENDENCIA WHERE CO_DEP=?) ");
        sql.append("order by TI_DESCRIP");
        List<DocumentoDependenciaBean> list = new ArrayList<DocumentoDependenciaBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoDependenciaBean.class),
                    new Object[]{codDependencia});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public String insDocDependencia(DocumentoDependenciaBean docDepBean) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("insert into IDOSGD.SITM_DOC_DEPENDENCIA(CO_DEP,Co_Tip_Doc,Es_Eli,Co_Use_Cre,Fe_Use_Cre,Co_Use_Mod,Fe_Use_Mod,Es_Obl_Firma) ");
        sql.append("values (?,?,?,?,CURRENT_TIMESTAMP,?,CURRENT_TIMESTAMP,?)");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
            docDepBean.getCoDep(),docDepBean.getCoTipDoc(),docDepBean.getEsEli(),docDepBean.getCoUseCre(),docDepBean.getCoUseMod(),docDepBean.getEsOblFirma()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    public String eliDocDependencia(DocumentoDependenciaBean docDepBean) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("delete from IDOSGD.SITM_DOC_DEPENDENCIA ");
        sql.append("where CO_DEP=? and CO_TIP_DOC=? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
            docDepBean.getCoDep(),docDepBean.getCoTipDoc()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    public String updDocDependencia(DocumentoDependenciaBean docDepBean,String codDocReempl) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.SITM_DOC_DEPENDENCIA set CO_TIP_DOC=?,CO_USE_MOD=?,FE_USE_MOD=CURRENT_TIMESTAMP ");
        sql.append("where CO_DEP=? and CO_TIP_DOC=? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
            docDepBean.getCoTipDoc(),docDepBean.getCoUseMod(),docDepBean.getCoDep(),codDocReempl});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public List<MotivoBean> getMotDependenciaList(String codDependencia, String codDoc){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT de_mot,co_mot ");
        sql.append("FROM IDOSGD.tdtr_motivo ");
        sql.append("WHERE co_mot IN (SELECT co_mot ");
        sql.append("FROM IDOSGD.tdtx_moti_docu_depe ");
        sql.append("WHERE co_dep = ? AND co_tip_doc = ?) ORDER BY 1");
        
        List<MotivoBean> list = new ArrayList<MotivoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(MotivoBean.class),
                    new Object[]{codDependencia,codDoc});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    @Override
    public List<MotivoBean> getMotFaltantesList(String codDependencia,String codDoc) {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT de_mot,co_mot FROM IDOSGD.tdtr_motivo ");
        sql.append("WHERE co_mot NOT IN (SELECT co_mot ");
        sql.append("FROM IDOSGD.tdtx_moti_docu_depe ");
        sql.append("WHERE co_dep = ? AND co_tip_doc = ?)");
        List<MotivoBean> list = new ArrayList<MotivoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(MotivoBean.class),
                    new Object[]{codDependencia,codDoc});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
    
    public String insMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("insert into IDOSGD.TDTX_MOTI_DOCU_DEPE (CO_DEP,CO_TIP_DOC,CO_MOT) values (?,?,?)");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
            motDocDep.getCoDep(),motDocDep.getCoTipDoc(),motDocDep.getCoMot()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    public String updMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep,String codMotReempl) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.TDTX_MOTI_DOCU_DEPE set CO_MOT=? where CO_DEP=? and CO_TIP_DOC=? and CO_MOT=? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
            motDocDep.getCoMot(),motDocDep.getCoDep(),motDocDep.getCoTipDoc(),codMotReempl
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    public String eliMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("delete from IDOSGD.TDTX_MOTI_DOCU_DEPE ");
        sql.append("where CO_DEP=? and CO_TIP_DOC=? and CO_MOT=? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
            motDocDep.getCoDep(),motDocDep.getCoTipDoc(),motDocDep.getCoMot()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    
}
