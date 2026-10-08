/*
 * To change this template, choose Tools | Templates
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
import pe.gob.onpe.tramitedoc.bean.ProveedorBean;
import pe.gob.onpe.tramitedoc.bean.UbigeoBean;
import pe.gob.onpe.tramitedoc.dao.ProveedorDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import pe.gob.onpe.tramitedoc.web.util.ApplicationProperties;
import pe.gob.segdi.pide.consultas.bean.ConsultaSunatBean;

/**
 *
 * @author NGilt
 */
@Repository("proveedorDao")
public class ProveedorDaoImp extends SimpleJdbcDaoBase implements ProveedorDao {

    @Autowired
    private ApplicationProperties applicationProperties;  
    
    public List<ProveedorBean> getProveedoresList() {
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.* FROM ( ");
        sql.append("	select	cpro_ruc nuRuc, "); 
        sql.append("		CONVERT(VARCHAR(10), dpro_fecins, 103) dpro_fecins, ");
        sql.append("		cpro_razsoc descripcion,  ");
        sql.append("		cpro_domicil, "); 
        sql.append("		cubi_coddep, "); 
        sql.append("		cubi_codpro, "); 
        sql.append("		cubi_coddis, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO](cubi_coddep)) no_dep, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_PROVINCIA](cubi_coddep, cubi_codpro)) no_pro, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DISTRITO](cubi_coddep, cubi_codpro, cubi_coddis)) no_dis,  ");
        sql.append("		cpro_telefo, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY cpro_razsoc) AS ROWNUM "); 
        sql.append("	from IDOSGD.LG_PRO_PROVEEDOR ");
        sql.append(") A ");
        sql.append("WHERE A.ROWNUM <= ").append(applicationProperties.getTopRegistrosConsultas());
        sql.append(" ORDER BY 3 ");
       
        List<ProveedorBean> list = new ArrayList<ProveedorBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ProveedorBean.class),
                    new Object[]{});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<ProveedorBean> getProveedoresBusqList(String busNroRuc, String busRazonSocial) {
        String sqlContains = "";
        Map<String, Object> objectParam = new HashMap<String, Object>();
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT A.* FROM ( ");
        sql.append("	select	cpro_ruc nuRuc, "); 
        sql.append("		CONVERT(VARCHAR(10), dpro_fecins, 103) dpro_fecins, ");
        sql.append("		cpro_razsoc descripcion,  ");
        sql.append("		cpro_domicil, "); 
        sql.append("		cubi_coddep, "); 
        sql.append("		cubi_codpro, "); 
        sql.append("		cubi_coddis, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO](cubi_coddep)) no_dep, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_PROVINCIA](cubi_coddep, cubi_codpro)) no_prv, ");
        sql.append("		(SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DISTRITO](cubi_coddep, cubi_codpro, cubi_coddis)) no_dis,  ");
        sql.append("		cpro_telefo, ");
        sql.append("		ROW_NUMBER() OVER (ORDER BY cpro_razsoc) AS ROWNUM "); 
        sql.append("	from IDOSGD.LG_PRO_PROVEEDOR ");
        sql.append("	where 1=1 ");
        if (busNroRuc!=null && !busNroRuc.equals("")){
            sql.append(" and cpro_ruc like '%'+:cpro_ruc+'%' ");
            objectParam.put("cpro_ruc", busNroRuc);
        }       
        if( busRazonSocial!=null && !busRazonSocial.equals("")) {
            sql.append(" AND CONTAINS(cpro_razsoc, :pBusquedaTextual) ");
            sqlContains = "SELECT RETORNO FROM [IDOSGD].PK_SGD_DESCRIPCION_QUERYFILTER('"+busRazonSocial.trim()+"')";
        }
        
        sql.append(") A ");
        sql.append("WHERE A.ROWNUM <= 6 ").append(applicationProperties.getTopRegistrosConsultas());;
        sql.append("ORDER BY 3 ");
        
        List<ProveedorBean> list = new ArrayList<ProveedorBean>();
        try {
            //Obteniendo el parametro textual si es requerido
            if (sqlContains.length() > 0) {
                String cadena = this.jdbcTemplate.queryForObject(sqlContains, String.class);            
                objectParam.put("pBusquedaTextual", cadena);
            }
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(ProveedorBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public List<UbigeoBean> getUbigeoBusqList(UbigeoBean ubigeo) {
        StringBuffer sql = new StringBuffer();
        sql.append("select  ubdep, "); 
        sql.append("        ubprv, "); 
        sql.append("        ubdis, "); 
        sql.append("        ubloc, ");
        sql.append("        coreg, ");
        sql.append("        LTRIM(RTRIM(nodep)) nodep, "); 
        sql.append("        LTRIM(RTRIM(noprv)) noprv,  ");
        sql.append("        LTRIM(RTRIM(nodis)) nodis, ");
        sql.append("        cpdis, "); 
        sql.append("        stubi, ");
        sql.append("        stsob, ");
        sql.append("        feres, "); 
        sql.append("        inubi, "); 
        sql.append("        ub_inei ");  
        sql.append("from IDOSGD.idtubias "); 
        sql.append("where ubdep like '%' + ? + '%' and ubprv like '%' + ? + '%' and "); 
        sql.append("ubdis like '%' + ? + '%' and nodep like '%' + ? + '%' and "); 
        sql.append("noprv like '%' + ? + '%' and nodis like '%' + ? + '%' and "); 
        sql.append("UBLOC = '00' and "); 
        sql.append("UBPRV <> '00' and "); 
        sql.append("UBDIS <> '00' and "); 
        sql.append("UBDEP <> ' ' ");

        List<UbigeoBean> list = new ArrayList<UbigeoBean>();
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(UbigeoBean.class),
                    new Object[]{
                ubigeo.getUbDep(), ubigeo.getUbPrv(),
                ubigeo.getUbDis(), ubigeo.getNoDep(),
                ubigeo.getNoPrv(), ubigeo.getNoDis()
            });
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }

    public String insProveedor(ProveedorBean proveedor) {
         String vReturn = "NO_OK";
        StringBuffer sql = new StringBuffer();
        sql.append("insert into IDOSGD.LG_PRO_PROVEEDOR ");
        sql.append("(cpro_ruc, dpro_fecins ,cpro_razsoc, cpro_domicil, cubi_coddep, cubi_codpro, cubi_coddis , cpro_telefo,cpro_email) ");
        sql.append("values (?,CURRENT_TIMESTAMP,?,?,?,?,?,?,?) ");
        try {
            if (proveedor.getNuRuc().isEmpty()) {
                String seq = "SELECT RIGHT(REPLICATE('0', 11) + CAST((ISNULL(MAX(CAST(CPRO_RUC AS BIGINT)), 0) + 1) AS VARCHAR), 11) FROM IDOSGD.LG_PRO_PROVEEDOR";
                String keyCPRO_RUC = (String) this.jdbcTemplate.queryForObject(seq, String.class);
                proveedor.setNuRuc(keyCPRO_RUC);
            }
            
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                proveedor.getNuRuc(), proveedor.getDescripcion(), proveedor.getCproDomicil(),
                proveedor.getCubiCoddep(), proveedor.getCubiCodpro(), proveedor.getCubiCoddis(),proveedor.getCproTelefo(), proveedor.getCproEmail()
            });
            vReturn = "OK";
        } catch (DuplicateKeyException con) {
            vReturn = "Número de RUC duplicado.";
            //con.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
        public ProveedorBean getProveedor(String codProveedor) {
        ProveedorBean proveedorBean = new ProveedorBean();
        StringBuffer sql = new StringBuffer();
        
        sql.append("select  cpro_ruc nuRuc, ");
        sql.append("        CONVERT(VARCHAR(10), dpro_fecins, 103) dpro_fecins, ");
        sql.append("        cpro_razsoc descripcion, "); 
        sql.append("        cpro_domicil, ");
        sql.append("        cubi_coddep, ");
        sql.append("        cubi_codpro, ");
        sql.append("        cubi_coddis, ");
        sql.append("        (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DEPARTAMENTO](cubi_coddep)) no_dep, ");
        sql.append("        (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_PROVINCIA](cubi_coddep, cubi_codpro)) no_prv, ");
        sql.append("        (SELECT RETORNO FROM [IDOSGD].[PK_SGD_DESCRIPCION_FU_GET_DISTRITO](cubi_coddep, cubi_codpro, cubi_coddis)) no_dis, ");
        sql.append("        cpro_email,cpro_telefo "); 
        sql.append("from IDOSGD.LG_PRO_PROVEEDOR "); 
        sql.append("WHERE cpro_ruc = ? ");
        
        try {
            proveedorBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(ProveedorBean.class),
                    new Object[]{codProveedor});
        } catch (EmptyResultDataAccessException e) {
            proveedorBean = null;
        } catch (Exception e) {
            proveedorBean = null;
            e.printStackTrace();
        }
        
        return proveedorBean;
    }
        
    public String updProveedor(ProveedorBean proveedor, String codProveedor) {
        String vReturn = "NO_OK";
        
        StringBuffer sql = new StringBuffer();
        sql.append("update IDOSGD.LG_PRO_PROVEEDOR set ");
        sql.append("cpro_ruc=?, cpro_razsoc=?, cpro_domicil=?, cubi_coddep=?, cubi_codpro = ?, cubi_coddis= ? , cpro_telefo =?, cpro_email=? ");
        sql.append("where cpro_ruc=? ");
        
        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                proveedor.getNuRuc(), proveedor.getDescripcion(), proveedor.getCproDomicil(), proveedor.getCubiCoddep(), 
                proveedor.getCubiCodpro(), proveedor.getCubiCoddis(), proveedor.getCproTelefo(),proveedor.getCproEmail(), proveedor.getNuRuc()
            });
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        
        return vReturn;
    }

    @Override
   public String insPideProveedor(ConsultaSunatBean proveedor) {
        String vReturn = "NO_OK";
        StringBuilder sql = new StringBuilder();
        sql.append("insert into IDOSGD.LG_PRO_PROVEEDOR ");
        sql.append("(cpro_ruc, dpro_fecins ,cpro_razsoc, cpro_domicil, cubi_coddep, cubi_codpro, cubi_coddis) ");
        sql.append("values (?,getdate(),?,?,(select ubdep from IDOSGD.idtubias where ub_inei=?),(select ubprv from IDOSGD.idtubias where ub_inei=?),(select ubdis from IDOSGD.idtubias where ub_inei=?)) ");
        try {
            if (proveedor.getDdp_nomzon().equals("-"))
            {
                proveedor.setDdp_nomzon("");
            }
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                proveedor.getDdp_numruc(), proveedor.getDdp_nombre().trim(), proveedor.getDesc_tipvia().trim()+" "+proveedor.getDdp_nomvia().trim()+" "+proveedor.getDdp_numer1().trim()+" "+proveedor.getDdp_nomzon(),
                proveedor.getDdp_ubigeo(), proveedor.getDdp_ubigeo(), proveedor.getDdp_ubigeo()
            });
            vReturn = "OK";
            
        } catch (DuplicateKeyException con) {
            vReturn = "Número de RUC duplicado.";
            //con.printStackTrace();
//            proveedorBean = null;
        } catch (Exception e) {
            e.printStackTrace();
//            proveedorBean = null;
        }
        return vReturn;
    }
    
}
