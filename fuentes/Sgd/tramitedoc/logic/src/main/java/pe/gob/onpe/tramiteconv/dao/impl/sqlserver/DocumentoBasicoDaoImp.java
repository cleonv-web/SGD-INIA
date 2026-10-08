/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramiteconv.dao.impl.sqlserver;

import java.util.ArrayList;
import java.util.List;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.DestinoResBen;
import pe.gob.onpe.tramitedoc.bean.DocumentoAnexoBean;
import pe.gob.onpe.tramitedoc.bean.RemitosResBean;
import pe.gob.onpe.tramitedoc.dao.DocumentoBasicoDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author wcutipa
 */
@Repository("documentoBasicoDao")
public class DocumentoBasicoDaoImp extends SimpleJdbcDaoBase implements DocumentoBasicoDao{
    @Override
    public RemitosResBean getRemitoResumen(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
//        sql.append("select \n" +
//     "nu_ann, \n" +
//     "nu_emi, \n" +
//     "ti_emi,\n" +
//     "de_tip_emi de_ti_emi,\n" +
//     "ti_cap, \n" +
//     "de_dep_emi,\n" +
//     "de_emp_emi, \n" +
//     "de_emp_res, \n" +
//     "de_ori_emi,\n" +
//     "to_char(fe_emi,'dd/mm/yyyy') fe_emi,\n" +
//     "de_tip_doc_adm de_ti_doc,\n" +
//     "nu_doc,\n" +
//     "de_asu,\n" +
//     "de_es_doc_emi\n" +
//     "from tdvv_remitos_adm " +
//     "where nu_ann = ?\n" +
//     "and nu_emi = ?");
        sql.append("SELECT \n" +
            " a.nu_ann, \n" +
            " a.nu_emi, \n" +
            " a.nu_cor_emi, \n" +
            " a.co_loc_emi,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_DE_LOCAL (a.co_loc_emi) de_loc_emi, \n" +
            " a.co_dep_emi,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_de_dependencia (a.co_dep_emi) de_dep_emi,\n" +
            " a.ti_emi, \n" +
            " IDOSGD.PK_SGD_DESCRIPCION_ti_destino (a.ti_emi) de_tip_emi,\n" +
            " a.co_emp_emi, \n" +
            " IDOSGD.PK_SGD_DESCRIPCION_de_nom_emp (co_emp_emi) de_emp_emi,\n" +
            " a.co_emp_res, \n" +
            " IDOSGD.PK_SGD_DESCRIPCION_de_nom_emp (a.co_emp_res) de_emp_res,\n" +
            " a.nu_dni_emi, \n" +
            " a.nu_ruc_emi, \n" +
            " a.co_otr_ori_emi,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_ti_emi_emp (a.nu_ann, a.nu_emi) de_ori_emi,\n" +
            "				(CASE a.ti_emi WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_de_dependencia (a.co_dep_emi) + ' - ' + IDOSGD.PK_SGD_DESCRIPCION_de_nom_emp (a.co_emp_emi)\n" +
            "							   WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_de_proveedor (a.nu_ruc_emi)\n" +
            "							   WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ani_simil (a.nu_dni_emi)\n" +
            "							   WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_otro_origen (a.co_otr_ori_emi)\n" +
            "							   WHEN '05' THEN IDOSGD.PK_SGD_DESCRIPCION_de_nom_emp (a.co_emp_emi)\n" +
            "					END)de_ori_emi_mp, \n" +
            " CONVERT(varchar(20),a.fe_emi, 120)  fe_emi, \n" +
            "				RIGHT('00' + CAST(DATEPART(mm, a.fe_emi) AS varchar), 2)de_mes,  \n" +
            " a.co_tip_doc_adm,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_adm) de_ti_doc,\n" +
            "(CASE WHEN a.ti_emi = '01' OR a.ti_emi = '05' THEN COALESCE(a.nu_doc_emi,'') + '-' + COALESCE(a.nu_ann,'') + '/' + COALESCE(a.de_doc_sig,'')\n" +
            "   ELSE a.de_doc_sig "+ 
            " END) nu_doc,\n" +
            " a.co_gru, a.es_doc_emi,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_estados (a.es_doc_emi,'TDTV_REMITOS') de_es_doc_emi,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_estados_mp (a.es_doc_emi,'TDTV_REMITOS') de_es_doc_emi_mp,\n" +
            " a.nu_dia_ate, \n" +
            " a.de_asu, \n" +
            " UPPER (a.de_asu) de_asu_m, \n" +
            " a.de_ane,\n" +
            " a.co_tip_doc_pro,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_de_documento (a.co_tip_doc_pro) de_tip_doc_pro,\n" +
            "				(\n" +
            "				case a.nu_candes when 1 then (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_ti_des (a.nu_ann, a.nu_emi))\n" +
            "					else (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_TI_DES_V (a.nu_ann, a.nu_emi))\n" +
            "					end\n" +
            "				)ti_des,\n" +
            "				(\n" +
            "				case a.nu_candes when 1 then (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_ti_co_dep_des (a.nu_ann, a.nu_emi))\n" +
            "					else (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_TI_CO_DEP_DES_V (a.nu_ann, a.nu_emi)) end\n" +
            "				)co_dep_des_t,\n" +
            " (\n" +
            "					case a.nu_candes when 1 then (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_ti_des_emp (a.nu_ann, a.nu_emi))\n" +
            "					 else (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_ti_des_emp_v (a.nu_ann, a.nu_emi)) end\n" +
            "				)de_emp_pro\n" +
            "				, \n" +
            " a.ti_cap,\n" +
            " a.co_use_cre,\n" +
            " a.fe_use_cre, \n" +
            " a.co_use_mod, \n" +
            " a.fe_use_mod, \n" +
            " a.nu_ann_exp,\n" +
            " a.nu_sec_exp, \n" +
            " a.nu_det_exp,\n" +
            " IDOSGD.PK_SGD_DESCRIPCION_DE_NU_EXPEDIENTE(a.nu_ann_exp,a.nu_sec_exp) NU_EXPEDIENTE,\n" +
            "				(case a.nu_sec_exp when null then null else 'E' end)in_expe, \n" +
            " nu_folios, \n" +
            " SE_MESA_PARTES\n" +
            " FROM IDOSGD.tdtv_remitos A  WITH (NOLOCK) \n"
             + "WHERE a.es_eli = '0' AND a.nu_ann=? AND nu_emi=?");

        RemitosResBean remitoDocBean = new RemitosResBean();

        try {
            remitoDocBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(RemitosResBean.class),new Object[]{ pnuAnn,pnuEmi} );
        } catch (EmptyResultDataAccessException e) {
            remitoDocBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(remitoDocBean);
    
    
    }
    
    @Override
    public DestinoResBen  getDestinoResumen(String pnuAnn, String pnuEmi, String pnuDes){
        StringBuffer sql = new StringBuffer();
        String g="";
        if(pnuDes==null || pnuDes.trim()==""|| pnuDes.trim().equals("null")){
         g=" AND NU_DES IS NULL "; 
        }
        else
        {
         
         g=" AND NU_DES='"+pnuDes+"' ";
        }
        
        sql.append("SELECT \n" +
                    " b.de_ane, \n" +
                    " b.nu_ann, \n" +
                    " b.nu_emi, \n" +
                    " b.nu_des,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_ti_destino(b.ti_des) de_tip_des,\n" +
                    " (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_TI_DES_ORI (b.nu_ann, b.nu_emi, b.nu_des)) de_ori_des,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(co_emp_rec) de_emp_rec,\n" +
                    " (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_de_prioridad(b.co_pri)) de_pri,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_motivo (b.co_mot) de_mot,\n" +
                    " b.de_pro de_indica,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_estados (b.es_doc_rec,'TDTV_DESTINOS') de_es_doc_des,\n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_rec_doc,'dd/mm/yyyy HH24:MI') fe_rec_doc, \n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_arc_doc,'dd/mm/yyyy') fe_arc_doc, \n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_ate_doc,'dd/mm/yyyy') fe_ate_doc, \n" +
                    " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_der_doc,'dd/mm/yyyy') fe_der_doc\n" +
                    ",de_avance "+
                    " FROM  IDOSGD.tdtv_destinos b  WITH (NOLOCK)  \n" +
     "where nu_ann = ?\n" +
     "and nu_emi = ?\n" +
     "and nu_des = ?"+g);
        
   
        DestinoResBen destinoDocBean = new DestinoResBen();

        try {
            destinoDocBean = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DestinoResBen.class),new Object[]{ pnuAnn,pnuEmi,pnuDes} );
        } catch (EmptyResultDataAccessException e) {
            destinoDocBean = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(destinoDocBean);
    }
    
    @Override
    public List<DestinoResBen>  getDestinoResumenList(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT \n" +
                " b.nu_ann, \n" +
                " b.nu_emi, \n" +
                " b.nu_des,\n" +
                " IDOSGD.PK_SGD_DESCRIPCION_ti_destino(b.ti_des) de_tip_des,\n" +
                " (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_ti_des_ori (b.nu_ann, b.nu_emi, b.nu_des)) de_ori_des,\n" +
                " IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP(co_emp_rec) de_emp_rec,\n" +
                " (SELECT RETORNO FROM IDOSGD.PK_SGD_DESCRIPCION_de_prioridad(b.co_pri)) de_pri,\n" +
                " IDOSGD.PK_SGD_DESCRIPCION_motivo (b.co_mot) de_mot,\n" +
                " b.de_pro de_indica,\n" +
                " IDOSGD.PK_SGD_DESCRIPCION_estados (b.es_doc_rec,'TDTV_DESTINOS') de_es_doc_des,\n" +
                " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_rec_doc,'dd/mm/yyyy HH24:MI') fe_rec_doc, \n" +
                " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_arc_doc,'dd/mm/yyyy') fe_arc_doc, \n" +
                " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_ate_doc,'dd/mm/yyyy') fe_ate_doc, \n" +
                " IDOSGD.PK_SGD_DESCRIPCION_FORMAT(b.fe_der_doc,'dd/mm/yyyy') fe_der_doc\n" +
                ",de_avance "+
                " FROM  IDOSGD.tdtv_destinos b  WITH (NOLOCK) \n" +
     "where nu_ann = ?\n" +
     "and nu_emi = ? ");
   
        
        List<DestinoResBen> list = new ArrayList<DestinoResBen>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DestinoResBen.class), new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(list);
    
    }

    @Override
    public List<DocumentoAnexoBean>  getAnexosList(String pnuAnn, String pnuEmi){
        StringBuffer sql = new StringBuffer();
        sql.append("select  \n" +
                    " nu_ann,\n" +
                    " nu_emi,\n" +
                    " nu_ane,\n" +
                    " de_det,\n" +
                    " de_rut_ori,\n" +
                    " co_use_cre,\n" +
                    " fe_use_cre,\n" +
                    " co_use_mod,\n" +
                    " fe_use_mod,\n" +
                    " COALESCE(req_firma,'0') req_firma,\n" +
                    "ISNULL(ti_public,'0') ti_public\n" +
                    " from IDOSGD.tdtv_anexos  WITH (NOLOCK) \n" +
     "where nu_ann = ? \n" +
     "and nu_emi = ? \n"+
     "order by nu_ane ");
   
        
        List<DocumentoAnexoBean> list = new ArrayList<DocumentoAnexoBean>();

        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoAnexoBean.class), new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(list);
    
    }

    @Override
    public List<DocumentoAnexoBean> getAnexosMsjList(String pnuAnn, String pnuEmi) {
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "A.nu_ann,\n" +
                    "A.nu_emi,\n" +
                    "A.nu_des,\n" +
                    "B.nu_ane,\n" +
                    "ISNULL(B.de_det,'-1') de_det,\n" +
                    "ISNULL(B.de_rut_ori,'') de_rut_ori,\n" +
                    "B.co_use_cre,\n" +
                    "B.fe_use_cre,\n" +
                    "B.co_use_mod,\n" +
                    "B.fe_use_mod,\n" +
                    "ISNULL(B.req_firma,'0') req_firma,\n" +
                    "CASE A.TI_DES WHEN '01' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_DES) +'-'+IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_DES) "+
                    "WHEN '02' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_PROVEEDOR (A.NU_RUC_DES) "+
                    "WHEN '03' THEN IDOSGD.PK_SGD_DESCRIPCION_ANI_SIMIL (A.NU_DNI_DES) "+
                    "WHEN '04' THEN IDOSGD.PK_SGD_DESCRIPCION_OTRO_ORIGEN (A.CO_OTR_ORI_DES) "+
                    "WHEN '05' THEN IDOSGD.PK_SGD_DESCRIPCION_DE_DEPENDENCIA(A.CO_DEP_DES) +'-'+IDOSGD.PK_SGD_DESCRIPCION_DE_NOM_EMP (A.CO_EMP_DES) "+
                    " END deDestino,"+                   
                    " DOC.esDoc2 esDoc, \n" +
                    " ENV.tiEnv2 tiEnv, \n" +
                " (CASE WHEN B.DE_DET='CARGO_NOTIFICACION'+CAST(B.NU_DES AS NVARCHAR(10))+'.pdf'  THEN '1' ELSE '0' END) esNotificado "+
                    " FROM IDOSGD.tdtv_destinos A  WITH (NOLOCK) LEFT JOIN IDOSGD.tdtv_anexos B  WITH (NOLOCK) ON A.nu_ann=B.nu_ann AND A.nu_emi=B.nu_emi AND A.nu_des=B.nu_des \n" +
                    " LEFT JOIN (SELECT CO_EST,DE_EST esDoc2 FROM IDOSGD.TDTR_ESTADOS WITH (NOLOCK)  WHERE  DE_TAB='TD_MENSAJERIA') DOC ON DOC.CO_EST = A.ES_DOC_REC \n" +
                    " LEFT JOIN (SELECT NU_ANN,NU_EMI,ISNULL(TI_ENV_MSJ,'0') tiEnv2 FROM IDOSGD.TDTV_REMITOS   WITH (NOLOCK)  ) ENV ON ENV.NU_ANN=A.NU_ANN AND ENV.NU_EMI=A.NU_EMI\n" +
                    " where A.nu_ann = ? \n" +             
                    " and A.nu_emi = ? \n"+
                    " order by B.nu_ane ");
   
        
        List<DocumentoAnexoBean> list = new ArrayList<DocumentoAnexoBean>();
//        System.out.println(sql);
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoAnexoBean.class), new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return(list);
    }
    
    
    @Override
    public String verificaCargaDoc(String pnuAnn, String pnuEmi) {
        
        StringBuffer sql = new StringBuffer();
        sql.append("select \n" +
                    "A.NU_EMI \n" +
                    "FROM IDOSGD.TDTV_ARCHIVO_DOC A  WITH (NOLOCK) \n" +
                    "where A.nu_ann = ? \n" +             
                    "and A.nu_emi = ? \n"+
                    "and BL_DOC is not null ");
   
        
        List<DocumentoAnexoBean> list = new ArrayList<DocumentoAnexoBean>();
//        System.out.println(sql);
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocumentoAnexoBean.class), new Object[]{pnuAnn,pnuEmi});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            e.printStackTrace();
        }
        String bResult="0";
        if(list.size()>0){
        bResult="1";
        }
        return(bResult);
    }
}