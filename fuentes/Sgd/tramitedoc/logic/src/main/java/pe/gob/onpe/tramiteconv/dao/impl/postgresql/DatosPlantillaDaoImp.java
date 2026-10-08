package pe.gob.onpe.tramiteconv.dao.impl.postgresql;

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
        sql.append("SELECT \n"
                + "a.nu_ann, \n"
                + "a.nu_emi, \n"
                + "CAST(a.nu_cor_emi AS text) nu_cor_emi, \n"
                + "a.co_loc_emi, \n"
                + "a.co_dep_emi, \n"
                + "COALESCE(b.titulo_dep,b.de_dependencia) de_dep_emi,\n"
                + "b.de_dependencia de_dep_emi_mae,b.TITULO_DEP titulo_dependencia,padre.TITULO_DEP  titulo_dependencia_padre, \n"  
                + "(CASE WHEN b.co_empleado=a.co_emp_emi THEN IDOSGD.PK_SGD_DESCRIPCION_de_cargo(b.co_cargo) ELSE IDOSGD.PK_SGD_DESCRIPCION_de_cargo(e.cemp_co_cargo) END ) de_cargo_fun_emi_mae,\n"  
                + "a.ti_emi, \n"
                + "a.co_emp_emi,\n"
                + "IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP_AP(a.co_emp_emi) de_emp_emi, \n"
                + "a.co_emp_res, \n"
                + "b.co_empleado co_emp_fun, \n"
                + "b.de_cargo_completo de_cargo_fun,\n" 
                +" CASE\n"
                + "WHEN COALESCE(b.co_tipo_encargatura,'1')='1' THEN ' '\n"
                + "WHEN COALESCE(b.co_tipo_encargatura,'1')='4' THEN '(i)'\n"
                + "ELSE '(e)' END AS de_ti_fun,"
                + "((SELECT substr(cemp_denom, 1, 1) || substr(cemp_apepat, 1, 1) || substr(cemp_apemat, 1, 1)  FROM IDOSGD.rhtm_per_empleados WHERE cemp_codemp=a.co_emp_emi)||(CASE \n"
                + "WHEN a.co_emp_emi=a.co_emp_res THEN \n"
                + "''\n"
                + "ELSE\n"
                + "'/'|| lower((SELECT substr(cemp_denom, 1, 1) || substr(cemp_apepat, 1, 1) || substr(cemp_apemat, 1, 1)  FROM IDOSGD.rhtm_per_empleados WHERE cemp_codemp=a.co_emp_res))\n"
                + "END)) as de_iniciales,\n"
                + "TO_CHAR(a.fe_emi, 'DD/MM/YYYY') fe_emi, \n"
                + "trim(case \n"
                + "    when extract(month FROM a.fe_emi)=1 then (TO_CHAR(a.fe_emi,'dd')||' de Enero de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=2 then (TO_CHAR(a.fe_emi,'dd')||' de Febrero de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=3 then (TO_CHAR(a.fe_emi,'dd')||' de Marzo de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=4 then (TO_CHAR(a.fe_emi,'dd')||' de Abril de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=5 then (TO_CHAR(a.fe_emi,'dd')||' de Mayo de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=6 then (TO_CHAR(a.fe_emi,'dd')||' de Junio de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=7 then (TO_CHAR(a.fe_emi,'dd')||' de Julio de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=8 then (TO_CHAR(a.fe_emi,'dd')||' de Agosto de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=9 then (TO_CHAR(a.fe_emi,'dd')||' de Setiembre de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=10 then (TO_CHAR(a.fe_emi,'dd')||' de Octubre de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=11 then (TO_CHAR(a.fe_emi,'dd')||' de Noviembre de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    else (TO_CHAR(a.fe_emi,'dd')||' de Diciembre de '||extract(year FROM a.fe_emi))::varchar \n"
                + "    end) fe_emi_largo,\n"
                + "a.co_tip_doc_adm co_tipo_doc, IDOSGD.PK_SGD_DESCRIPCION_de_documento(a.co_tip_doc_adm) de_tipo_doc,\n"
                + "COALESCE(a.nu_doc_emi,'      ') nu_doc_emi,\n"
                + "COALESCE(a.de_doc_sig,' ') de_doc_sig,\n"
                + " (select S.IN_MULTIPLE from IDOSGD.SI_MAE_TIPO_DOC S WHERE S.CDOC_TIPDOC=A.co_tip_doc_adm   LIMIT 1) as inMultiple, " 
                + "a.es_doc_emi, \n"
                + "a.de_asu de_asunto, \n"
                + "a.ti_cap, \n" 
                +"COALESCE(c.NU_EXPEDIENTE,' ') NRO_EXPEDIENTE,\n" 
                +"COALESCE(a.COD_VER_EXT,'') as coVerExt \n"
                +" FROM IDOSGD.tdtv_remitos a INNER JOIN  IDOSGD.rhtm_dependencia b ON a.co_dep_emi = b.co_dependencia INNER JOIN  IDOSGD.rhtm_per_empleados e ON A.CO_EMP_EMI = E.CEMP_CODEMP  \n"
                +" INNER JOIN IDOSGD.tdtx_remitos_resumen c ON C.NU_ANN=A.NU_ANN AND C.NU_EMI=A.NU_EMI \n"
                +" LEFT JOIN IDOSGD.rhtm_dependencia padre on B.CO_DEPEN_PADRE = padre.co_dependencia \n" 
                + "WHERE A.NU_ANN = ? \n"
                + "AND A.NU_EMI= ?  ");

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
        sql.append("select a.nu_ann,a.nu_emi,a.nu_des,\n"
                + "COALESCE(a.co_dep_des,' ') co_dependencia,\n"
                + "COALESCE(b.titulo_dep,COALESCE(b.de_dependencia,' ')) de_dependencia,\n"
                + "b.de_dependencia de_dep_dest_mae,\n" 
                + "b.de_cargo_completo de_tramite,\n"                
                +" CASE WHEN b.co_empleado= a.co_emp_des THEN IDOSGD.PK_SGD_DESCRIPCION_de_cargo(b.co_cargo) ELSE IDOSGD.PK_SGD_DESCRIPCION_de_cargo(e.cemp_co_cargo) END de_cargo_fun_dest_mae,"
                + "CASE WHEN COALESCE(b.co_tipo_encargatura,'1')='1' THEN ' '\n"
                + "WHEN COALESCE(b.co_tipo_encargatura,'1')='4' THEN '(i)' ELSE '(e)' END co_tramite,\n"
                + "b.co_empleado co_local,\n"
                + "a.co_emp_des co_empleado,\n"
                + "a.ti_des co_tipo_destino ,\n"
                + "a.de_cargo cargo ,\n" 
                + " CASE WHEN a.ti_des='01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP_AP(a.co_emp_des)\n"
                + "      WHEN a.ti_des='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_des) \n"
                + "      WHEN a.ti_des='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.nu_dni_des)  \n"
                + "      WHEN a.ti_des='04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(a.co_otr_ori_des)\n"
                + " END AS de_empleado,\n"                
                + " CASE WHEN a.ti_des='01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP_AP(a.co_emp_des)\n"
                + "      WHEN a.ti_des='02' THEN CASE WHEN a.remi_nu_dni_emi IS NOT NULL THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.remi_nu_dni_emi) ELSE " +
                    "CASE WHEN a.remi_co_otr_ori_emi IS NOT NULL THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(a.remi_co_otr_ori_emi) ELSE '' END END \n"
                + "      WHEN a.ti_des='03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.nu_dni_des)  \n"
                + "      WHEN a.ti_des='06' THEN A.DE_NOM_DES "
                + "      WHEN a.ti_des='04' THEN (SELECT AA.DE_NOM_OTR||' '|| AA.DE_APE_PAT_OTR||' '||AA.DE_APE_MAT_OTR FROM IDOSGD.TDTR_OTRO_ORIGEN AA  \n" +
                    "          LEFT OUTER JOIN (SELECT CELE_CODELE, CELE_DESELE FROM   IDOSGD.SI_ELEMENTO WHERE  CTAB_CODTAB ='TIP_DOC_IDENT') B ON  AA.CO_TIP_OTR_ORI = B.CELE_CODELE \n" +
                    "           AND    AA.CO_OTR_ORI = a.co_otr_ori_des) \n"
                + " END AS nombre_destinatario,\n"
                + " CASE WHEN a.ti_des='02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_des)\n"
                + "      WHEN a.ti_des='06' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_des) \n" 
                + "      WHEN a.ti_des='04' THEN (SELECT COALESCE(DE_RAZ_SOC_OTR, ' ') from IDOSGD.tdtr_otro_origen where co_otr_ori = a.co_otr_ori_des) ELSE ' ' \n"
                + " END AS entidad_privada_destinatario,\n"
                 
                + "CDIR_REMITE || ',' || IDOSGD.PK_SGD_DESCRIPCION_fu_get_departamento(a.ccod_dpto) || '-' || IDOSGD.PK_SGD_DESCRIPCION_fu_get_provincia (a.ccod_dpto, a.ccod_prov) || '-' ||  IDOSGD.PK_SGD_DESCRIPCION_fu_get_distrito (a.ccod_dpto,a.ccod_prov,a.ccod_dist) direccion_destinatario \n" +                                     
                 "FROM IDOSGD.tdtv_destinos a LEFT OUTER JOIN IDOSGD.rhtm_dependencia b\n"               
                + "ON  a.co_dep_des = b.co_dependencia \n"
                + " LEFT OUTER JOIN IDOSGD.rhtm_per_empleados e on  a.CO_EMP_DES = e.CEMP_CODEMP "
                + "where nu_ann = ? \n"
                + "and nu_emi = ? \n"
                + "AND ES_ELI='0' \n"                
                + "order by ti_des desc, co_mot, nu_des");//FSILVA
        
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
        sql.append("SELECT \n"
                + "IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tipo_doc, \n"
                + "CASE WHEN a.ti_emi='01' THEN a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig\n"
                + "	WHEN a.ti_emi='05' THEN a.nu_doc_emi || '-' || a.nu_ann || '-' || a.de_doc_sig ELSE a.de_doc_sig END AS li_nu_doc,"
                //"to_char(fe_emi,'DD')||substr(to_char(fe_emi,'FMMONTH','NLS_DATE_LANGUAGE=SPANISH'),1,3)||to_char(fe_emi,'YYYY') fe_emi_corta \n" +
                + "case \n"
                + "    when extract(month FROM a.fe_emi)=1 then (extract(day FROM a.fe_emi)||'ENE'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=2 then (extract(day FROM a.fe_emi)||'FEB'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=3 then (extract(day FROM a.fe_emi)||'MAR'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=4 then (extract(day FROM a.fe_emi)||'ABR'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=5 then (extract(day FROM a.fe_emi)||'MAY'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=6 then (extract(day FROM a.fe_emi)||'JUN'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=7 then (extract(day FROM a.fe_emi)||'JUL'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=8 then (extract(day FROM a.fe_emi)||'AGO'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=9 then (extract(day FROM a.fe_emi)||'SEP'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=10 then (extract(day FROM a.fe_emi)||'OCT'||extract(year FROM a.fe_emi))::varchar \n"
                + "    when extract(month FROM a.fe_emi)=11 then (extract(day FROM a.fe_emi)||'NOV'||extract(year FROM a.fe_emi))::varchar \n"
                + "    else (extract(day FROM a.fe_emi)||'DIC'||extract(year FROM a.fe_emi))::varchar \n"
                + "    end AS fe_emi_corta\n"
                + "FROM IDOSGD.tdtv_remitos a,IDOSGD.TDTR_REFERENCIA b \n"
                + "WHERE a.nu_ann = b.nu_ann_ref\n"
                + "AND a.nu_emi = b.nu_emi_ref\n"
                + "and a.es_doc_emi <> '9'\n"
                + "and b.nu_ann = ?\n"
                + "and b.nu_emi = ?");
        
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
                    "        FROM IDOSGD.si_mae_local l,\n" +
                    "             IDOSGD.idtubias     b\n" +
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
                    "from IDOSGD.TDTR_PARAMETROS\n" +
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
                    "  FROM IDOSGD.tdtx_config_emp\n" +
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
                    "from IDOSGD.TDTR_PLANTILLA_DOCX a\n" +
                    " where es_doc = '1' and "+
                    " ( ((select count(1) from IDOSGD.TDTR_PLANTILLA_DOCX where co_Dep=? AND co_tipo_doc='"+idPlantilla+"' limit 1)=1 and co_Dep=?)" +
                    "or ((select count(1) from IDOSGD.TDTR_PLANTILLA_DOCX where co_Dep=? AND co_tipo_doc='"+idPlantilla+"'  limit 1)=0 and co_Dep='00000') ) AND co_tipo_doc=? ");
        
        
        try {
            list = this.jdbcTemplate.query(sql.toString(), new PlantillaRowMapper(),new Object[]{pcoDep,pcoDep,pcoDep,idPlantilla});
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
                //docObjBean.setObjPlantilla(rs.getBytes("bl_doc"));
                byte[] fileBytes = rs.getBytes("bl_doc");
                  //java.sql.Blob documento = rs.getByte("bl_doc");//GETbOLB
                
                if(fileBytes!=null){
                    //size=0;
                    //size=(int)fileBytes.length;
                    docObjBean.setObjPlantilla(fileBytes);
                    
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
                fileBytes=null;
            } catch (Exception e) {
                e.printStackTrace();
            }
            return docObjBean;
        }
    }
}
