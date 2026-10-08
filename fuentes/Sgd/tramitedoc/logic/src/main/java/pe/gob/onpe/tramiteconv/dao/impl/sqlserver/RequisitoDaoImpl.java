/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.RequisitoBean;
import pe.gob.onpe.tramitedoc.dao.RequisitoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;

/**
 *
 * @author JHuamanF
 */
@Repository("requisitoDao")
public class RequisitoDaoImpl extends SimpleJdbcDaoBase implements RequisitoDao {
    
    @Autowired
    private ApplicationProperties applicationProperties;

    @Override
    public List<RequisitoBean> getRequisitoList() {
        StringBuffer sql = new StringBuffer();        
        sql.append("SELECT  COD_REQ AS codRequisito, ");
        sql.append("        DE_DESCRIPCION AS descripcion, ");
        sql.append("        ES_ESTADO, ");
        sql.append("        US_CREA_AUDI, ");
        sql.append("        CONVERT(VARCHAR(10), FE_CREA_AUDI, 103) FE_CREA_AUDI, ");
        sql.append("        US_MODI_AUDI, ");
        sql.append("        CONVERT(VARCHAR(10), FE_MODI_AUDI, 103) FE_MODI_AUDI ");
        sql.append("FROM IDOSGD.TDTR_REQUISITOS ");
        
        List<RequisitoBean> list = new ArrayList<RequisitoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RequisitoBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<RequisitoBean> getRequisitoBusqList(RequisitoBean requisito) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        
        sql.append("SELECT A.* FROM ( ");
        sql.append("	SELECT	COD_REQ AS codRequisito, ");
        sql.append("            DE_DESCRIPCION AS descripcion, ");
        sql.append("		ES_ESTADO, ");
        sql.append("		US_CREA_AUDI, ");
        sql.append("		CONVERT(VARCHAR(10), FE_CREA_AUDI, 103) FE_CREA_AUDI, ");
        sql.append("		US_MODI_AUDI, ");
        sql.append("		CONVERT(VARCHAR(10), FE_MODI_AUDI, 103) FE_MODI_AUDI, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY COD_REQ) AS ROWNUM ");
        sql.append("	FROM IDOSGD.TDTR_REQUISITOS ");
        sql.append("	WHERE 1 = 1 ");
        if (requisito.getDescripcion() != null && !requisito.getDescripcion().equals("")) {
            sql.append("AND UPPER(DE_DESCRIPCION) like '%'+:descripcion+'%' "); 
            objectParam.put("descripcion", requisito.getDescripcion());
        }
        if (requisito.getIndicador() != null && !requisito.getIndicador().equals("")) {
            sql.append("AND COD_REQ NOT IN (SELECT COD_REQUISITO FROM IDOSGD.TDTX_REQUISITO_PROCESO WHERE COD_PROCESO = :indicador) "); 
            objectParam.put("indicador", requisito.getIndicador());
        }
        if (requisito.getEsEstado() != null && !requisito.getEsEstado().equals("")) {
            sql.append("AND ES_ESTADO = :estado "); 
            objectParam.put("estado", requisito.getEsEstado());
        }
        sql.append(") A ");
        sql.append("WHERE ROWNUM <= ").append(applicationProperties.getTopRegistrosConsultas());
        sql.append(" ORDER BY codRequisito ");
        
        List<RequisitoBean> list = new ArrayList<RequisitoBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(RequisitoBean.class));            
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String insRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        String genCoProceso = "";
        
        sql.append("INSERT INTO IDOSGD.TDTR_REQUISITOS ");
        sql.append("(COD_REQ, DE_DESCRIPCION, ES_ESTADO, US_CREA_AUDI, US_MODI_AUDI, FE_CREA_AUDI, FE_MODI_AUDI) ");
        sql.append("values (?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP) ");

        try {
            //El id de la tabla sera autogenerado sumando 1 al ultimo ID
            String seq = "SELECT RIGHT(REPLICATE('0', 4) + CAST((ISNULL(MAX(CAST(COD_REQ AS INT)), 0) + 1) AS VARCHAR), 4) FROM IDOSGD.TDTR_REQUISITOS";
            genCoProceso = (String) this.jdbcTemplate.queryForObject(seq, String.class);
            
            //El campo Estado no se incluye en el bloque del insert pq es un valor default en la tabla
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                genCoProceso, requisito.getDescripcion(), requisito.getEsEstado(), codusuario, codusuario
            });
            
            vReturn = "OK";
            
        } catch (DuplicateKeyException con) {
            vReturn = "Número de Codigo de requisito duplicado.";
            con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public RequisitoBean getRequisito(String codRequisito) {
        RequisitoBean requisitoBean = new RequisitoBean();
        StringBuffer sql = new StringBuffer();        
        sql.append("SELECT  COD_REQ AS codRequisito, ");
        sql.append("        DE_DESCRIPCION AS descripcion, ");
        sql.append("        ES_ESTADO, ");
        sql.append("        US_CREA_AUDI, ");
        sql.append("        CONVERT(VARCHAR(10), FE_CREA_AUDI, 103) FE_CREA_AUDI, ");
        sql.append("        US_MODI_AUDI, ");
        sql.append("        CONVERT(VARCHAR(10), FE_MODI_AUDI, 103) FE_MODI_AUDI ");
        sql.append("FROM IDOSGD.TDTR_REQUISITOS WITH (NOLOCK)  ");
        sql.append("WHERE COD_REQ = ? ");

        try {
            requisitoBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(RequisitoBean.class),
                    new Object[]{ codRequisito });
        } catch (EmptyResultDataAccessException e) {
            requisitoBean = null;
        } catch (Exception e) {
            requisitoBean = null;
            e.printStackTrace();
        }
        
        return requisitoBean;
    }

    @Override
    public String updRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE IDOSGD.TDTR_REQUISITOS SET ");
        sql.append("ES_ESTADO=?, DE_DESCRIPCION=?, US_MODI_AUDI=?, FE_MODI_AUDI=CURRENT_TIMESTAMP ");
        sql.append("WHERE COD_REQ=? ");
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                requisito.getEsEstado(), requisito.getDescripcion(), codusuario, requisito.getCodRequisito()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return vReturn;
    }

    @Override
    public String insTupaRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO IDOSGD.TDTX_REQUISITO_PROCESO ");
        sql.append("(COD_REQUISITO, COD_PROCESO, NU_CORRELATIVO, ES_ESTADO, CO_USE_CRE, CO_USE_MOD, FE_USE_CRE, FE_USE_MOD, IN_OBLIGATORIO) ");
        sql.append("values (?, ?, ?, ?, ?, ?, CURRENT_TIMESTAMP, CURRENT_TIMESTAMP, ?) ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[] {
                requisito.getCodRequisito(), 
                requisito.getCodProceso(), 
                requisito.getNuCorrelativo(), 
                requisito.getEsEstado(), 
                codusuario, 
                codusuario,
                requisito.getIndicador()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String updTupaRequisito(RequisitoBean requisito, String codusuario) {
        List<RequisitoBean> lis_expedientes = null;
        String vReturn = "NO_OK";
        
        StringBuffer sql = new StringBuffer();
        sql.append("UPDATE IDOSGD.TDTX_REQUISITO_PROCESO SET ");
        if (requisito.getCodRem() != null && !requisito.getCodRem().equals("")) {
            sql.append("COD_REQUISITO=?, CO_USE_MOD=?, FE_USE_MOD=CURRENT_TIMESTAMP ");
        } else if (requisito.getIndicador() != null && !requisito.getIndicador().equals("")) {
            sql.append("IN_OBLIGATORIO=?, CO_USE_MOD=?, FE_USE_MOD=CURRENT_TIMESTAMP ");
        }
        sql.append("WHERE COD_REQUISITO=? AND COD_PROCESO=? ");
        
        try {
            if (requisito.getCodRem() != null && !requisito.getCodRem().equals("")) {
                // Verificamos si existe expedientes con requisitos asociados
                lis_expedientes = getExpedienteRequisitoList(requisito.getCodRem(), requisito.getCodProceso());
                
                if (lis_expedientes != null && lis_expedientes.size() > 0) {
                    
                    // Eliminamos las relaciones de los expedientes con requisitos asociados
                    eliExpedienteRequisito(requisito.getCodRem(), requisito.getCodProceso());
                    
                    // Actualizamos las relacion de los requisitos procesos
                    this.jdbcTemplate.update(sql.toString(), new Object[]{
                        requisito.getCodRequisito(), codusuario, requisito.getCodRem(), requisito.getCodProceso()
                    });
                    
                    // Insertamos los nuevos registros de expedientes con las relacion de los requisitos actualizados
                    for (RequisitoBean req : lis_expedientes) {
                        RequisitoBean req_expediente = new RequisitoBean();
                        req_expediente.setCodProceso(requisito.getCodProceso());
                        req_expediente.setCodRequisito(requisito.getCodRequisito());
                        req_expediente.setAniExpediente(req.getAniExpediente());
                        req_expediente.setNumExpediente(req.getNumExpediente());
                        insExpedienteRequisito(req_expediente, codusuario);
                    }
                    
                } else {
                    // Actualizamos las relacion de los requisitos procesos
                    this.jdbcTemplate.update(sql.toString(), new Object[]{
                        requisito.getCodRequisito(), codusuario, requisito.getCodRem(), requisito.getCodProceso()
                    });
                }
            } else if (requisito.getIndicador() != null && !requisito.getIndicador().equals("")) {
                // Actualizamos las relacion de los requisitos procesos
                this.jdbcTemplate.update(sql.toString(), new Object[]{
                    requisito.getIndicador(), codusuario, requisito.getCodRequisito(), requisito.getCodProceso()
                });
            } 
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return vReturn;
    }

    @Override
    public String eliTupaRequisito(RequisitoBean requisito) {
        List<RequisitoBean> lis_expedientes = null;
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM IDOSGD.TDTX_REQUISITO_PROCESO ");
        sql.append("WHERE COD_REQUISITO=? AND COD_PROCESO=? ");
        try {            
            // Verificamos si existe expedientes con requisitos asociados
            lis_expedientes = getExpedienteRequisitoList(requisito.getCodRequisito(), requisito.getCodProceso());
             
            if (lis_expedientes != null && lis_expedientes.size() > 0) {
                    
                // Eliminamos las relaciones de los expedientes con requisitos asociados
                eliExpedienteRequisito(requisito.getCodRequisito(), requisito.getCodProceso());

            }
            
            // Eliminamos las relacion de los requisitos procesos
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                requisito.getCodRequisito(), requisito.getCodProceso()
            });

            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public List<RequisitoBean> getTupaRequisitoBusqList(RequisitoBean requisito) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        
        sql.append("SELECT  r.COD_REQ AS codRequisito, ");
        sql.append("	    p.COD_PROCESO AS codProceso, ");
        sql.append("        r.DE_DESCRIPCION AS descripcion, ");
        sql.append("        p.IN_OBLIGATORIO AS indicador ");
        sql.append("FROM IDOSGD.TDTX_REQUISITO_PROCESO p ");
        sql.append("INNER JOIN IDOSGD.TDTR_REQUISITOS r ON p.COD_REQUISITO = r.COD_REQ ");
        sql.append("INNER JOIN IDOSGD.TDTR_PROCESOS_EXP e ON p.COD_PROCESO = e.CO_PROCESO ");
        sql.append("WHERE 1 = 1 ");
        
        if (requisito.getCodProceso() != null && !requisito.getCodProceso().equals("")) {
            sql.append("AND p.COD_PROCESO = :cod_proceso "); 
            objectParam.put("cod_proceso", requisito.getCodProceso());
        }
        if (requisito.getEsEstado() != null && !requisito.getEsEstado().equals("")) {
            sql.append("AND r.ES_ESTADO = :estado "); 
            objectParam.put("estado", requisito.getEsEstado());
        }
        sql.append("ORDER BY r.DE_DESCRIPCION ");
        
        List<RequisitoBean> list = new ArrayList<RequisitoBean>();
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(RequisitoBean.class));            
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public List<RequisitoBean> getExpedienteRequisitoList(String codRequisito, String codProceso) {
        StringBuffer sql = new StringBuffer();        
        sql.append("SELECT  NU_ANN_EXP AS aniExpediente, ");
        sql.append("        NU_SEC_EXP AS numExpediente ");
        sql.append("FROM IDOSGD.TDTX_REQUISITO_EXPEDIENTE ");
        sql.append("WHERE COD_REQUISITO = ? ");
        sql.append("AND COD_PROCESO = ? ");
        
        List<RequisitoBean> list = new ArrayList<RequisitoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(RequisitoBean.class),
                    new Object[]{ codRequisito, codProceso });
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    @Override
    public String eliExpedienteRequisito(String codRequisito, String codProceso) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("DELETE FROM IDOSGD.TDTX_REQUISITO_EXPEDIENTE ");
        sql.append("WHERE COD_REQUISITO = ? ");
        sql.append("AND COD_PROCESO = ? ");
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                codRequisito, codProceso
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    @Override
    public String insExpedienteRequisito(RequisitoBean requisito, String codusuario) {
        String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("INSERT INTO IDOSGD.TDTX_REQUISITO_EXPEDIENTE( ");
        sql.append("	NU_ANN_EXP, ");
        sql.append("	NU_SEC_EXP, ");
        sql.append("	COD_REQUISITO, ");
        sql.append("	COD_PROCESO, ");
        sql.append("	IN_OBLIGATORIO, ");
        sql.append("	NU_CORRELATIVO, ");
        sql.append("	IN_REQUISITO_OK, ");
        sql.append("	US_CREA_AUDI, ");
        sql.append("	US_MODI_AUDI) ");
        sql.append("	SELECT ");
        sql.append("	?, ");
        sql.append("	?, ");
        sql.append("	COD_REQUISITO, ");
        sql.append("	COD_PROCESO, ");
        sql.append("	IN_OBLIGATORIO, ");
        sql.append("	NU_CORRELATIVO, ");
        sql.append("	CASE IN_OBLIGATORIO WHEN '0' THEN '1' ELSE '0' END, ");
        sql.append("	?, ");
        sql.append("	? ");
        sql.append("	FROM IDOSGD.TDTX_REQUISITO_PROCESO ");
        sql.append("	WHERE COD_PROCESO = ? ");
        sql.append("	AND COD_REQUISITO = ? ");
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[] {
                requisito.getAniExpediente(), 
                requisito.getNumExpediente(),                               
                codusuario, 
                codusuario,
                requisito.getCodProceso(), 
                requisito.getCodRequisito()    
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
}
