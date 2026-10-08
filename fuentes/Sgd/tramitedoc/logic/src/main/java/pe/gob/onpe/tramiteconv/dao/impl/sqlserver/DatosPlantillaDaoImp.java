package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

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
        sql.append("SET LANGUAGE Español;");
        sql.append("SELECT \n" +
                    " a.nu_ann, \n" +
                    " a.nu_emi, \n" +
                    " CAST(a.nu_cor_emi AS VARCHAR) nu_cor_emi, \n" +
                    " a.co_loc_emi, \n" +
                    " a.co_dep_emi, \n" +
                    " COALESCE(b.titulo_dep,b.de_dependencia) de_dep_emi,\n" +
                    "b.de_dependencia de_dep_emi_mae,b.TITULO_DEP titulo_dependencia,padre.TITULO_DEP  titulo_dependencia_padre, \n" + 
                    " CASE b.co_empleado WHEN a.co_emp_emi THEN IDOSGD.PK_SGD_DESCRIPCION_de_cargo(b.co_cargo) ELSE IDOSGD.PK_SGD_DESCRIPCION_de_cargo(e.cemp_co_cargo) END  de_cargo_fun_emi_mae,\n" + 
                    " a.ti_emi, \n" +
                    " a.co_emp_emi,\n" +
                    " (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP_AP(a.co_emp_emi)) de_emp_emi, \n" +
                    " a.co_emp_res, \n" +
                    " b.co_empleado co_emp_fun, \n" +
                    " b.de_cargo_completo de_cargo_fun,\n" +
                    "	(CASE COALESCE(b.co_tipo_encargatura,'1') WHEN '1' THEN ' '	WHEN '4' THEN '(i)'	ELSE '(e)' END)de_ti_fun,  \n" +
                    "	(SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_iniciales_emp(a.co_emp_emi)) +\n" +
                    "	(CASE a.co_emp_emi WHEN a.co_emp_res THEN '' ELSE '/' + lower((SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_FU_INICIALES_EMP(a.co_emp_res))) END) de_iniciales,                                    \n" +
                    " CONVERT(VARCHAR(10),a.fe_emi, 103) fe_emi, \n" +
                    " (RIGHT('0' + convert(varchar,DATEPART(DAY,GETDATE())),2) + ' de ' +  DATENAME(mm,GETDATE()) + ' de ' + CAST(DATEPART(YEAR,GETDATE())AS VARCHAR)) fe_emi_largo,                    \n" +
                    " a.co_tip_doc_adm co_tipo_doc, IDOSGD.PK_SGD_DESCRIPCION_de_documento(a.co_tip_doc_adm) de_tipo_doc,\n" +
                    " COALESCE(a.nu_doc_emi,'      ') nu_doc_emi,\n" +
                    " COALESCE(a.de_doc_sig,' ') de_doc_sig, \n" +
                    " ISNULL((select TOP 1 S.IN_MULTIPLE from IDOSGD.SI_MAE_TIPO_DOC S WHERE S.CDOC_TIPDOC=A.co_tip_doc_adm  ),0) as inMultiple, "+
                    " a.es_doc_emi, \n" +
                    " a.de_asu de_asunto, \n" +
                    " a.ti_cap, \n" +
                    "ISNULL(c.NU_EXPEDIENTE,' ') NRO_EXPEDIENTE,\n" +
                    "ISNULL(a.COD_VER_EXT,'') as coVerExt\n"+
                    
                    " FROM IDOSGD.tdtv_remitos a  WITH (NOLOCK) INNER JOIN  IDOSGD.rhtm_dependencia b  WITH (NOLOCK) ON a.co_dep_emi = b.co_dependencia INNER JOIN  IDOSGD.rhtm_per_empleados e WITH (NOLOCK)  ON A.CO_EMP_EMI = E.CEMP_CODEMP  \n"+
                    " INNER JOIN IDOSGD.tdtx_remitos_resumen c  WITH (NOLOCK) ON C.NU_ANN=A.NU_ANN AND C.NU_EMI=A.NU_EMI \n"+
                    " LEFT JOIN IDOSGD.rhtm_dependencia padre  WITH (NOLOCK) on B.CO_DEPEN_PADRE = padre.co_dependencia \n" +    
                
                    "WHERE A.NU_ANN = ? \n" +
                    "AND A.NU_EMI= ? \n" +
                    /*"AND C.NU_ANN=A.NU_ANN \n" +
                    "AND C.NU_EMI=A.NU_EMI \n" +*/
                    "  ");        
        
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
"                    COALESCE(a.co_dep_des,' ') co_dependencia,\n" +
"                    b.de_dependencia de_dep_dest_mae, \n" +                
"                    COALESCE(b.titulo_dep,COALESCE(b.de_dependencia,' ')) de_dependencia,\n" +
"                    b.de_cargo_completo de_tramite,\n" +
"                    CASE WHEN b.co_empleado= a.co_emp_des THEN IDOSGD.PK_SGD_DESCRIPCION_de_cargo(b.co_cargo) ELSE IDOSGD.PK_SGD_DESCRIPCION_de_cargo(e.cemp_co_cargo)  END   de_cargo_fun_dest_mae, \n" +                
"		     (CASE COALESCE(b.co_tipo_encargatura,'1') WHEN '1' THEN ' ' WHEN '4' THEN '(i)'	ELSE '(e)' END)co_tramite,\n" +
"                    b.co_empleado co_local,\n" +
"                    a.co_emp_des co_empleado,\n" +
"                    a.ti_des co_tipo_destino ,\n" +
"					( CASE a.ti_des WHEN '01' THEN (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP_AP(a.co_emp_des))\n" +
"									WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_des)\n" +
"									WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.nu_dni_des)\n" +
"									WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(a.co_otr_ori_des) END)de_empleado, \n" +
" CASE a.ti_des  WHEN '01'  THEN IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(a.co_emp_des)  "+
"  WHEN '02'  THEN CASE WHEN a.remi_nu_dni_emi IS NOT NULL THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.remi_nu_dni_emi) ELSE "+
"                  CASE WHEN a.remi_co_otr_ori_emi IS NOT NULL THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN(a.remi_co_otr_ori_emi) ELSE '' END END "+                
"  WHEN '03'  THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL(a.nu_dni_des) "+   
"  WHEN '04'  THEN NOMBRE_OTRO_ORIGEN "+                  
"  WHEN '06'  THEN  A.DE_NOM_DES "+                 
"   END nombre_destinatario, "+ 
" CASE a.ti_des  WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_des)   "+                 
"  WHEN '06' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR(a.nu_ruc_des) "+ 
"  WHEN '04' THEN (SELECT ISNULL(DE_RAZ_SOC_OTR, ' ') from IDOSGD.tdtr_otro_origen where co_otr_ori = a.co_otr_ori_des) "+ 
"  ELSE '' END  entidad_privada_destinatario, "+              
                    "CDIR_REMITE + ',' + (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_get_departamento(a.ccod_dpto)) + '-' + (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_get_provincia (a.ccod_dpto, a.ccod_prov)) + '-' +  (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_fu_get_distrito (a.ccod_dpto,a.ccod_prov,a.ccod_dist)) direccion_destinatario \n" +                 
                
"                    FROM IDOSGD.tdtv_destinos a WITH (NOLOCK) "
                    + " LEFT OUTER JOIN IDOSGD.rhtm_dependencia b  WITH (NOLOCK) ON a.co_dep_des = b.co_dependencia"                
                    + " LEFT  JOIN IDOSGD.rhtm_per_empleados e  WITH (NOLOCK) ON   a.CO_EMP_DES = e.CEMP_CODEMP " 
                    + " LEFT JOIN ( SELECT CO_OTR_ORI,A.DE_NOM_OTR+' '+ A.DE_APE_PAT_OTR+' '+A.DE_APE_MAT_OTR NOMBRE_OTRO_ORIGEN   FROM   IDOSGD.TDTR_OTRO_ORIGEN A WITH (NOLOCK)  LEFT JOIN    \n" +
"                       (SELECT CELE_CODELE, CELE_DESELE FROM   IDOSGD.SI_ELEMENTO  WITH (NOLOCK) WHERE  CTAB_CODTAB ='TIP_DOC_IDENT') B ON A.CO_TIP_OTR_ORI = B.CELE_CODELE  ) TB ON TB.CO_OTR_ORI = a.co_otr_ori_des "
                    + " where nu_ann = ? \n" 
                    + " and nu_emi = ? \n" 
                    + " AND ES_ELI='0' \n"                     
                    + "order by ti_des desc, co_mot, nu_des");
        
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
    //Me quede en este punto
    @Override
    public List<ReferenciaBean> getLstReferencia(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer(); 
        List<ReferenciaBean> list = new ArrayList<ReferenciaBean>();                
        sql.append("SET LANGUAGE Español;");
        sql.append("SELECT \n" +
                    "IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_tipo_doc, \n" +
                    "(CASE WHEN a.ti_emi = '01' OR a.ti_emi = '05' THEN a.nu_doc_emi + '-' + a.nu_ann + '-' + a.de_doc_sig ELSE a.de_doc_sig END)li_nu_doc,\n" +                   
                    "(RIGHT('0' + convert(varchar,DATEPART(DAY,fe_emi)),2)+DATENAME(mm,fe_emi)+CAST(DATEPART(YEAR,fe_emi)AS VARCHAR))fe_emi_corta \n" +
                    "FROM IDOSGD.tdtv_remitos a WITH (NOLOCK)  INNER JOIN IDOSGD.TDTR_REFERENCIA b  WITH (NOLOCK) ON a.nu_ann = b.nu_ann_ref AND a.nu_emi = b.nu_emi_ref \n" +
                    "WHERE  a.es_doc_emi <> '9' \n" +
                    "and b.nu_ann = ? \n" +
                    "and b.nu_emi = ? ");
        
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
        sql.append("      SELECT RTRIM(LTRIM(IDOSGD.PK_SGD_DESCRIPCION_INITCAP(b.nodis)))\n" +
                    "        FROM IDOSGD.si_mae_local l WITH (NOLOCK)  \n" +
                    "        INNER JOIN   IDOSGD.idtubias b WITH (NOLOCK)  ON l.co_dep = b.ubdep AND l.co_prv = b.ubprv AND l.co_dis = b.ubdis \n" +
                    "       WHERE l.ccod_local = ? \n" );        
        
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
                    "from IDOSGD.TDTR_PARAMETROS WITH (NOLOCK) \n" +
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
                    "  FROM IDOSGD.tdtx_config_emp WITH (NOLOCK) \n" +
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
                    "from IDOSGD.TDTR_PLANTILLA_DOCX a WITH (NOLOCK) \n" +
                    "where es_doc = '1' and \n" +
                    " ( ((select count(1) from IDOSGD.TDTR_PLANTILLA_DOCX  WITH (NOLOCK)  where co_Dep=?  )>=1 and co_Dep=?)" +
                    "or ((select count(1) from IDOSGD.TDTR_PLANTILLA_DOCX WITH (NOLOCK)  where co_Dep=?  )=0 and co_Dep='00000') ) and co_tipo_doc='"+idPlantilla+"' ");
        
        try {
            //list = this.jdbcTemplate.query(sql.toString(), new PlantillaRowMapper(),new Object[]{pcoDep});
            list = this.jdbcTemplate.query(sql.toString(), new PlantillaRowMapper(),new Object[]{pcoDep,pcoDep,pcoDep});
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
