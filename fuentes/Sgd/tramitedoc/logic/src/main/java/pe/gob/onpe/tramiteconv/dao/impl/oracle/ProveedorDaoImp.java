/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.ArrayList;
import java.util.List;
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
        StringBuilder sql = new StringBuilder();
        sql.append("select cpro_ruc nuRuc, to_char(dpro_fecins,'dd/mm/yyyy') dpro_fecins ,"
                + " cpro_razsoc descripcion, "
                + " cpro_domicil, "
                + " cubi_coddep, "
                + " cubi_codpro, ");
        sql.append("cubi_coddis ,no_dep ,no_prv, no_dis,cpro_email, cpro_telefo from SIVP_PROVEEDOR WHERE ROWNUM <= ").append(applicationProperties.getTopRegistrosConsultas());
        sql.append(" ORDER BY 3");
        List<ProveedorBean> list = new ArrayList<ProveedorBean>();
        System.out.println("pl: "+sql);
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

    public List<UbigeoBean> getUbigeoBusqList(UbigeoBean ubigeo) {
        StringBuilder sql = new StringBuilder();
        sql.append("select ubdep, ubprv, ubdis, ubloc ,coreg ,trim(nodep) nodep, trim(noprv) noprv , trim(nodis) nodis ,cpdis, stubi ,stsob ,feres, inubi, ub_inei, CCOD_TIPO_UBI  ambito  from idtubias where ");
        sql.append("ubdep like '%'||?||'%' and ubprv like '%'||?||'%' and ");
        sql.append("ubdis like '%'||?||'%' and nodep like '%'||?||'%' and ");
        sql.append("noprv like '%'||?||'%' and nodis like '%'||?||'%' and ");
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
        StringBuilder sql = new StringBuilder();
        sql.append("insert into LG_PRO_PROVEEDOR ");
        sql.append("(cpro_ruc, dpro_fecins ,cpro_razsoc, cpro_domicil, cubi_coddep, cubi_codpro, cubi_coddis , cpro_telefo,cpro_email) ");
        sql.append("values (?,sysdate,?,?,?,?,?,?,?) ");
        try {
            if (proveedor.getNuRuc().isEmpty()) {
                String keyCPRO_RUC = (String) this.jdbcTemplate.queryForObject("SELECT LPAD(NVL(MAX(TO_NUMBER(CPRO_RUC)),0)+1,11,0) FROM LG_PRO_PROVEEDOR", String.class);
                proveedor.setNuRuc(keyCPRO_RUC);
            }

            this.jdbcTemplate.update(sql.toString(), new Object[]{
                proveedor.getNuRuc(), proveedor.getDescripcion(), proveedor.getCproDomicil(),
                proveedor.getCubiCoddep(), proveedor.getCubiCodpro(), proveedor.getCubiCoddis(), proveedor.getCproTelefo(), proveedor.getCproEmail()
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

    @Override
    public ProveedorBean getProveedor(String codProveedor) {
        ProveedorBean proveedorBean = new ProveedorBean();
        StringBuilder sql = new StringBuilder();

        sql.append("SELECT cpro_ruc nuRuc, \n"
                + " to_char(dpro_fecins,'dd/mm/yyyy') dpro_fecins, \n"
                + " cpro_razsoc descripcion, \n"
                + " cpro_domicil, \n"
                + " cubi_coddep, \n"
                + " cubi_codpro, \n"
                + " cubi_coddis ,\n"
                + " PK_SGD_DESCRIPCION.fu_get_departamento (cubi_coddep) no_dep,\n"
                + " PK_SGD_DESCRIPCION.fu_get_provincia (cubi_coddep, cubi_codpro) no_prv,\n"
                + " PK_SGD_DESCRIPCION.fu_get_distrito (cubi_coddep,cubi_codpro,cubi_coddis) no_dis,\n"
                + " cpro_email, \n"
                + " cpro_telefo \n"
                + " FROM LG_PRO_PROVEEDOR WHERE cpro_ruc =?");

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

        StringBuilder sql = new StringBuilder();
        sql.append("update LG_PRO_PROVEEDOR set ");
        sql.append("cpro_razsoc=?, cpro_domicil=?, cubi_coddep=?, cubi_codpro = ?, cubi_coddis= ? , cpro_telefo =?, cpro_email=? ");
        sql.append("where cpro_ruc=? ");

        try {
            this.jdbcTemplate.update(sql.toString(), new Object[]{
                proveedor.getDescripcion(), proveedor.getCproDomicil(), proveedor.getCubiCoddep(),
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
        sql.append("insert into LG_PRO_PROVEEDOR ");
        sql.append("(cpro_ruc, dpro_fecins ,cpro_razsoc, cpro_domicil, cubi_coddep, cubi_codpro, cubi_coddis) ");
        sql.append("values (?,sysdate,?,?,(select ubdep from idtubias where ub_inei=?),(select ubprv from idtubias where ub_inei=?),(select ubdis from idtubias where ub_inei=?)) ");
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
