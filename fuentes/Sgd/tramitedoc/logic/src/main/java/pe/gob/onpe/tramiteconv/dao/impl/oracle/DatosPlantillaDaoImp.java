package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import fr.opensagres.xdocreport.document.IXDocReport;
import fr.opensagres.xdocreport.document.registry.XDocReportRegistry;
import fr.opensagres.xdocreport.template.TemplateEngineKind;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DatosPlantillaDoc;
import pe.gob.onpe.tramitedoc.bean.DestinatarioDocumentoEmiBean;
import pe.gob.onpe.tramitedoc.bean.PlantillaDocx;
import pe.gob.onpe.tramitedoc.bean.ReferenciaBean;
import pe.gob.onpe.tramitedoc.dao.DatosPlantillaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

@Repository("datosPlantillaDao")
public class DatosPlantillaDaoImp extends SimpleJdbcDaoBase implements DatosPlantillaDao{
    
    @Override
    public DatosPlantillaDoc getDocumentoEmitido(String pnuAnn, String pnuEmi) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT \n" +
                    "a.nu_ann, \n" +
                    "a.nu_emi, \n" +
                    "to_char(a.nu_cor_emi) nu_cor_emi, \n" +
                    "a.co_loc_emi, \n" +
                    "a.co_dep_emi, \n" +
                    "nvl(b.titulo_dep,b.de_dependencia) de_dep_emi,\n" +
                    "b.de_dependencia de_dep_emi_mae,b.TITULO_DEP titulo_dependencia,padre.TITULO_DEP  titulo_dependencia_padre, \n" +
                    "decode (b.co_empleado, a.co_emp_emi, PK_SGD_DESCRIPCION.de_cargo(b.co_cargo), PK_SGD_DESCRIPCION.de_cargo(e.cemp_co_cargo)) de_cargo_fun_emi_mae,\n" + 
                    "a.ti_emi, \n" +
                    "a.co_emp_emi,\n" +
                    "PK_SGD_DESCRIPCION.DE_NOM_EMP_AP(a.co_emp_emi) de_emp_emi, \n" +
                    "a.co_emp_res, \n" +
                    "b.co_empleado co_emp_fun, \n" +
                    "b.de_cargo_completo de_cargo_fun,\n" +
                    "decode(nvl(b.co_tipo_encargatura,'1'),'1',' ','4','(i)','(e)') de_ti_fun, \n" +
                    "PK_SGD_DESCRIPCION.fu_iniciales_emp(a.co_emp_emi)|| decode (a.co_emp_emi,a.co_emp_res,'','/'|| lower(PK_SGD_DESCRIPCION.fu_iniciales_emp(a.co_emp_res))) de_iniciales,\n" +
                    "TO_CHAR(a.fe_emi, 'DD/MM/YYYY') fe_emi, \n" +
                    "trim(to_char(a.fe_emi,'DD \"de \"fmmonth \"de\" YYYY','NLS_DATE_LANGUAGE=SPANISH')) fe_emi_largo,\n" +
                    "a.co_tip_doc_adm co_tipo_doc, PK_SGD_DESCRIPCION.de_documento(a.co_tip_doc_adm) de_tipo_doc,\n" +
                    "nvl(a.nu_doc_emi,'      ') nu_doc_emi,\n" +
                    "nvl(a.de_doc_sig,' ') de_doc_sig,\n" +
                    " (select S.IN_MULTIPLE from SI_MAE_TIPO_DOC S WHERE S.CDOC_TIPDOC=A.co_tip_doc_adm and rownum=1) as inMultiple, "+ 
                    //"DECODE(b.CO_NIVEL,'1','/','-') || nvl(a.de_doc_sig,' ') de_doc_sig,\n" +
                    "a.es_doc_emi, \n" +
                    "a.de_asu de_asunto, \n" +
                    "a.ti_cap, \n" +
                    "NVL(c.NU_EXPEDIENTE,' ') NRO_EXPEDIENTE,\n" +
                    "nvl(a.COD_VER_EXT,'') as coVerExt\n"+
                    //"FROM tdtv_remitos a , rhtm_dependencia b, rhtm_per_empleados e, tdtx_remitos_resumen c\n" +
                    " FROM tdtv_remitos a INNER JOIN  rhtm_dependencia b ON a.co_dep_emi = b.co_dependencia INNER JOIN  rhtm_per_empleados e ON A.CO_EMP_EMI = E.CEMP_CODEMP  \n"+
                    " INNER JOIN tdtx_remitos_resumen c ON C.NU_ANN=A.NU_ANN AND C.NU_EMI=A.NU_EMI \n"+
                    " LEFT JOIN rhtm_dependencia padre on B.CO_DEPEN_PADRE = padre.co_dependencia \n" +
                    //"FROM tdtv_remitos a , rhtm_dependencia b ,tdtx_remitos_resumen c\n" +
                    "WHERE A.NU_ANN = ? \n" +
                    "AND A.NU_EMI= ? ");  
                   // "AND A.CO_EMP_EMI = E.CEMP_CODEMP \n" +
                  //  "AND C.NU_ANN=A.NU_ANN \n" +
                   // "AND C.NU_EMI=A.NU_EMI \n" +
                   // "and a.co_dep_emi = b.co_dependencia");        
        //CO_DEPEN_PADRE
        DatosPlantillaDoc datosPlantilla = new DatosPlantillaDoc();
        try {
            datosPlantilla = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DatosPlantillaDoc.class),
                    new Object[]{pnuAnn,pnuEmi});
            datosPlantilla.setUrlWebVerifica(getParametros("URL_WEB_VERIFICA"));
        } catch (EmptyResultDataAccessException e) {
            datosPlantilla = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return datosPlantilla;        
    }
    
    @Override
    public List<DestinatarioDocumentoEmiBean> getLstDestintarios(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer(); 
        List<DestinatarioDocumentoEmiBean> list = new ArrayList<DestinatarioDocumentoEmiBean>();                
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,\n" +
                    "nvl(a.co_dep_des,' ') co_dependencia,\n" +
                    "nvl(b.titulo_dep,nvl(b.de_dependencia,' ')) de_dependencia,\n" +
                    "b.de_dependencia de_dep_dest_mae,\n" +
                    "b.de_cargo_completo de_tramite,\n" +
                    "decode (b.co_empleado, a.co_emp_des, PK_SGD_DESCRIPCION.de_cargo(b.co_cargo), PK_SGD_DESCRIPCION.de_cargo(e.cemp_co_cargo)) de_cargo_fun_dest_mae, \n" +
                    "decode(nvl(b.co_tipo_encargatura,'1'),'1',' ','4','(i)','(e)') co_tramite,\n" +
                    "b.co_empleado co_local,\n" +
                    "a.co_emp_des co_empleado,\n" +
                    "a.ti_des co_tipo_destino ,\n" +
                    "a.de_cargo cargo ,\n" +
                    " decode(a.ti_des, \n" +
                    "           '01', PK_SGD_DESCRIPCION.DE_NOM_EMP_AP(a.co_emp_des),\n" +
                    "           '02', PK_SGD_DESCRIPCION.DE_PROVEEDOR(a.nu_ruc_des) ,\n" +                
                    "           '03', PK_SGD_DESCRIPCION.ANI_SIMIL(a.nu_dni_des) , \n" +
                    "           '04', PK_SGD_DESCRIPCION.OTRO_ORIGEN(a.co_otr_ori_des),\n" +
                    "           '06', PK_SGD_DESCRIPCION.DE_PROVEEDOR(a.nu_ruc_des)\n" +
                    "       ) de_empleado, \n" +
                    " decode(a.ti_des, \n" +
                    "           '01', PK_SGD_DESCRIPCION.DE_NOM_EMP_AP(a.co_emp_des),\n" +
                    "           '02', CASE WHEN a.remi_nu_dni_emi IS NOT NULL THEN PK_SGD_DESCRIPCION.ANI_SIMIL(a.remi_nu_dni_emi) ELSE " +
                    "CASE WHEN a.remi_co_otr_ori_emi IS NOT NULL THEN PK_SGD_DESCRIPCION.OTRO_ORIGEN(a.remi_co_otr_ori_emi) ELSE '' END END,\n" +
                    "           '03', PK_SGD_DESCRIPCION.ANI_SIMIL(a.nu_dni_des) , \n" +
                    "           '06', A.DE_NOM_DES , \n" +
//                    "           '04', PK_SGD_DESCRIPCION.OTRO_ORIGEN(a.co_otr_ori_des)\n" +
                    "           '04', (SELECT A.DE_NOM_OTR||' '|| A.DE_APE_PAT_OTR||' '||A.DE_APE_MAT_OTR FROM   TDTR_OTRO_ORIGEN A, \n" +
                    "           (SELECT CELE_CODELE, CELE_DESELE FROM   SI_ELEMENTO WHERE  CTAB_CODTAB ='TIP_DOC_IDENT') B WHERE  A.CO_TIP_OTR_ORI = B.CELE_CODELE(+)\n" +
                    "           AND    A.CO_OTR_ORI = a.co_otr_ori_des) \n" +
                    "       ) nombre_destinatario, \n" +
                    " decode(a.ti_des, \n" +
                    "           '02', PK_SGD_DESCRIPCION.DE_PROVEEDOR(a.nu_ruc_des) ,\n" +
                    "           '06', PK_SGD_DESCRIPCION.DE_PROVEEDOR(a.nu_ruc_des) ,\n" +
                    "           '04', (SELECT NVL(DE_RAZ_SOC_OTR, ' ') from tdtr_otro_origen where co_otr_ori = a.co_otr_ori_des), ' ' \n" +
                    "       ) entidad_privada_destinatario, \n" +
                    "CDIR_REMITE || ',' || PK_SGD_DESCRIPCION.fu_get_departamento(a.ccod_dpto) || '-' || PK_SGD_DESCRIPCION.fu_get_provincia (a.ccod_dpto, a.ccod_prov) || '-' ||  PK_SGD_DESCRIPCION.fu_get_distrito (a.ccod_dpto,a.ccod_prov,a.ccod_dist) direccion_destinatario \n" + 
                    "FROM tdtv_destinos a , rhtm_dependencia b, rhtm_per_empleados e\n" +
                    "where nu_ann = ? \n" +
                    "and nu_emi = ? \n" +
                    "AND ES_ELI='0' \n" +
                    "AND a.CO_EMP_DES = e.CEMP_CODEMP(+) \n" +
                    "and a.co_dep_des = b.co_dependencia(+)\n" +
                    "order by ti_des desc, co_mot, nu_des");
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinatarioDocumentoEmiBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }    
    
    @Override
    public List<ReferenciaBean> getLstReferencia(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer(); 
        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();                
        sql.append("SELECT \n" +
                    "PK_SGD_DESCRIPCION.de_documento (a.co_tip_doc_adm) de_tipo_doc, \n" +
                    "DECODE (a.ti_emi,'01', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,\n" +
                    "         '05', a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig,a.de_doc_sig) li_nu_doc,\n" +
                    /*"DECODE (a.ti_emi,'01', a.nu_doc_emi || '-' || a.nu_ann,\n" +
                    "         '05', a.nu_doc_emi || '-' || a.nu_ann,a.de_doc_sig) li_nu_doc,\n" +
                    "    DECODE (a.ti_emi,'01',a.de_doc_sig,\n" +
                    "             '05', a.de_doc_sig,' ') de_doc_sig,\n" +                  */
                    "to_char(fe_emi,'DD')||substr(to_char(fe_emi,'FMMONTH','NLS_DATE_LANGUAGE=SPANISH'),1,3)||to_char(fe_emi,'YYYY') fe_emi_corta \n" +
                    "FROM tdtv_remitos a,TDTR_REFERENCIA b \n" +
                    "WHERE a.nu_ann = b.nu_ann_ref\n" +
                    "AND a.nu_emi = b.nu_emi_ref\n" +
                    "and a.es_doc_emi <> '9'\n" +
                    "and b.nu_ann = ?\n" +
                    "and b.nu_emi = ?");
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(ReferenciaBean.class),
                    new Object[]{pnuAnn,pnuEmi});
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }  
    
    @Override
    public String getDistritoLocal(String pco_local){
        StringBuffer sql = new StringBuffer();
        String result = null;
        sql.append("      SELECT trim(initcap(b.nodis))\n" +
                    "        FROM si_mae_local l,\n" +
                    "             idtubias     b\n" +
                    "       WHERE l.co_dep = b.ubdep\n" +
                    "         AND l.co_prv = b.ubprv\n" +
                    "         AND l.co_dis = b.ubdis\n" +
                    "         AND l.ccod_local = ?");        
        
        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pco_local});
        } catch (EmptyResultDataAccessException e) {
            result = "Lima";
        } catch (Exception e) {
            result = "Lima";
            e.printStackTrace();
        }
        
        return result;        
    }    

    @Override
    public String getParametros(String pco_param){
        StringBuffer sql = new StringBuffer();
        String result = null;
        sql.append("select de_par\n" +
                    "from TDTR_PARAMETROS\n" +
                    "where CO_PAR = ?");        
        
        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pco_param});
        } catch (EmptyResultDataAccessException e) {
            result = " ";
        } catch (Exception e) {
            result = " ";
            e.printStackTrace();
        }
        return result;        
    }    
    
    
    @Override
    public String getPiePagina(String pco_emp, String pco_dep){
        StringBuffer sql = new StringBuffer();
        String result = null;
        sql.append("SELECT de_pie_pagina\n" +
                    "  FROM tdtx_config_emp\n" +
                    " WHERE co_emp = ? \n" +
                    "   AND co_dep = ? ");        
        
        try {
            result = this.jdbcTemplate.queryForObject(sql.toString(), String.class, new Object[]{pco_emp,pco_dep});
        } catch (EmptyResultDataAccessException e) {
            result = null;
        } catch (Exception e) {
            result = null;
            e.printStackTrace();
        }
        
        if (result==null){
            result = getParametros("PIE_PAGINA");
        }
        
        return result;        
    }    

    
    @Override
    public List<PlantillaDocx> getLstPlantillasDocx(String pcoDep,String idPlantilla){
        StringBuffer sql = new StringBuffer(); 
        List<PlantillaDocx> list = new ArrayList<PlantillaDocx>();                
        sql.append("select \n" +
                    "co_dep, \n" +
                    "co_tipo_doc, \n" +
                    "bl_doc, \n" +
                    "nom_archivo \n" +
                    "from TDTR_PLANTILLA_DOCX a\n" +
                    " where es_doc = '1' and "+
                    " ( ((select count(1) from TDTR_PLANTILLA_DOCX where co_Dep=? AND co_tipo_doc='"+idPlantilla+"' and rownum=1)=1 and co_Dep=?)" +
                    "or ((select count(1) from TDTR_PLANTILLA_DOCX where co_Dep=? AND co_tipo_doc='"+idPlantilla+"' and rownum=1)=0 and co_Dep='00000') ) AND co_tipo_doc=? ");
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), new PlantillaRowMapper(),new Object[]{pcoDep,pcoDep,pcoDep,idPlantilla});
           // list = this.jdbcTemplate.query(sql.toString(), new PlantillaRowMapper());
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }        
        return list;
    }
    
    private class PlantillaRowMapper implements ParameterizedRowMapper<PlantillaDocx> {

        public PlantillaDocx mapRow(ResultSet rs, int i) throws SQLException {
            PlantillaDocx docObjBean = new PlantillaDocx();
            int size=0;
            try {
                java.sql.Blob documento = rs.getBlob("bl_doc");
                if(documento!=null){
                    size=0;
                    size=(int)documento.length();
                    docObjBean.setObjPlantilla(documento.getBytes(1, size));
                    
                    try {
                        InputStream in = new ByteArrayInputStream(docObjBean.getObjPlantilla());
                        IXDocReport template = XDocReportRegistry.getRegistry().loadReport(in, TemplateEngineKind.Freemarker );
                        docObjBean.setTemplate(template);
                    } catch (Exception e) {
                        docObjBean.setTemplate(null);
                    }
                }
                docObjBean.setCoDep(rs.getString("co_dep"));
                docObjBean.setCoTipoDoc(rs.getString("co_tipo_doc"));
                docObjBean.setNomArchivo(rs.getString("nom_archivo"));

            } catch (Exception e) {
                e.printStackTrace();
            }
            return docObjBean;
        }
    }
}
