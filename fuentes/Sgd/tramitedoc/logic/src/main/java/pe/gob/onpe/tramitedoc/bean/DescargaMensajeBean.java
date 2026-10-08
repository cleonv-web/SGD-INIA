/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;
/**
 *
 * @author oti3
 */
public class DescargaMensajeBean {
    private String nu_msj;
    private String fec_enviomsj;
    private String nu_ann;
    private String nu_emi;
    private String nu_des;
    private String ob_msj;
    private String mo_msj_dev;
    private String fe_ent_msj;
    private String ho_ent_msj;
    private String fe_dev_msj;
    private String ho_dev_msj;
    private String es_pen_msj;
    private String co_EstadoDoc;
    private String nro_guia_devolucion;
    private String fe_pla_dev;
    private String di_pla_dev;
    private String es_pen_dev;
    private String coMotivo;
    private String de_tipo_msj;
    private String pe_env_msj;
    private String co_usuario;
    private byte[] archivo;
    
    
    private String nu_Acta_Vis1;
    private String nu_Acta_Vis2;
    private String fe_Acta_Vis1;
    private String fe_Acta_Vis2;
    private String es_Acta1_msj;
    private String es_Acta2_msj;
    private String archivo_Acta1;
    private String archivo_Acta2;
       
    //CommonsMultipartFile archivo;

    public String getNu_msj() {
        return nu_msj;
    }

    public void setNu_msj(String nu_msj) {
        this.nu_msj = nu_msj;
    }

    public String getFec_enviomsj() {
        return fec_enviomsj;
    }

    public void setFec_enviomsj(String fec_enviomsj) {
        this.fec_enviomsj = fec_enviomsj;
    }

    public String getNu_ann() {
        return nu_ann;
    }

    public void setNu_ann(String nu_ann) {
        this.nu_ann = nu_ann;
    }

    public String getNu_emi() {
        return nu_emi;
    }

    public void setNu_emi(String nu_emi) {
        this.nu_emi = nu_emi;
    }

    public String getNu_des() {
        return nu_des;
    }

    public void setNu_des(String nu_des) {
        this.nu_des = nu_des;
    }

    public String getOb_msj() {
        return ob_msj;
    }

    public void setOb_msj(String ob_msj) {
        this.ob_msj = ob_msj;
    }

    public String getMo_msj_dev() {
        return mo_msj_dev;
    }

    public void setMo_msj_dev(String mo_msj_dev) {
        this.mo_msj_dev = mo_msj_dev;
    }

    public String getFe_ent_msj() {
        return fe_ent_msj;
    }

    public void setFe_ent_msj(String fe_ent_msj) {
        this.fe_ent_msj = fe_ent_msj;
    }

    public String getHo_ent_msj() {
        return ho_ent_msj;
    }

    public void setHo_ent_msj(String ho_ent_msj) {
        this.ho_ent_msj = ho_ent_msj;
    }

    public String getFe_dev_msj() {
        return fe_dev_msj;
    }

    public void setFe_dev_msj(String fe_dev_msj) {
        this.fe_dev_msj = fe_dev_msj;
    }

    public String getHo_dev_msj() {
        return ho_dev_msj;
    }

    public void setHo_dev_msj(String ho_dev_msj) {
        this.ho_dev_msj = ho_dev_msj;
    }

    public String getEs_pen_msj() {
        return es_pen_msj;
    }

    public void setEs_pen_msj(String es_pen_msj) {
        this.es_pen_msj = es_pen_msj;
    }

    public String getCo_EstadoDoc() {
        return co_EstadoDoc;
    }

    public void setCo_EstadoDoc(String co_EstadoDoc) {
        this.co_EstadoDoc = co_EstadoDoc;
    }

    public byte[] getArchivo() {
        return archivo;
    }

    public void setArchivo(byte[] archivo) {
        this.setArchivo(archivo);
    }

    public String getFe_pla_dev() {
        return fe_pla_dev;
    }

    public void setFe_pla_dev(String fe_pla_dev) {
        this.fe_pla_dev = fe_pla_dev;
    }

    public String getDi_pla_dev() {
        return di_pla_dev;
    }

    public void setDi_pla_dev(String di_pla_dev) {
        this.di_pla_dev = di_pla_dev;
    }



    public String getEs_pen_dev() {
        return es_pen_dev;
    }

    public void setEs_pen_dev(String es_pen_dev) {
        this.es_pen_dev = es_pen_dev;
    }

    public String getCoMotivo() {
        return coMotivo;
    }

    public void setCoMotivo(String coMotivo) {
        this.coMotivo = coMotivo;
    }

    public String getDe_tipo_msj() {
        return de_tipo_msj;
    }

    public void setDe_tipo_msj(String de_tipo_msj) {
        this.de_tipo_msj = de_tipo_msj;
    }

    public String getPe_env_msj() {
        return pe_env_msj;
    }

    public void setPe_env_msj(String pe_env_msj) {
        this.pe_env_msj = pe_env_msj;
    }

    public String getNro_guia_devolucion() {
        return nro_guia_devolucion;
    }

    public void setNro_guia_devolucion(String nro_guia_devolucion) {
        this.nro_guia_devolucion = nro_guia_devolucion;
    }

    /**
     * @return the nu_Acta_Vis1
     */
    public String getNu_Acta_Vis1() {
        return nu_Acta_Vis1;
    }

    /**
     * @param nu_Acta_Vis1 the nu_Acta_Vis1 to set
     */
    public void setNu_Acta_Vis1(String nu_Acta_Vis1) {
        this.nu_Acta_Vis1 = nu_Acta_Vis1;
    }

    /**
     * @return the nu_Acta_Vis2
     */
    public String getNu_Acta_Vis2() {
        return nu_Acta_Vis2;
    }

    /**
     * @param nu_Acta_Vis2 the nu_Acta_Vis2 to set
     */
    public void setNu_Acta_Vis2(String nu_Acta_Vis2) {
        this.nu_Acta_Vis2 = nu_Acta_Vis2;
    }

    /**
     * @return the fe_Acta_Vis1
     */
    public String getFe_Acta_Vis1() {
        return fe_Acta_Vis1;
    }

    /**
     * @param fe_Acta_Vis1 the fe_Acta_Vis1 to set
     */
    public void setFe_Acta_Vis1(String fe_Acta_Vis1) {
        this.fe_Acta_Vis1 = fe_Acta_Vis1;
    }

    /**
     * @return the fe_Acta_Vis2
     */
    public String getFe_Acta_Vis2() {
        return fe_Acta_Vis2;
    }

    /**
     * @param fe_Acta_Vis2 the fe_Acta_Vis2 to set
     */
    public void setFe_Acta_Vis2(String fe_Acta_Vis2) {
        this.fe_Acta_Vis2 = fe_Acta_Vis2;
    }

    /**
     * @return the es_Acta1_msj
     */
    public String getEs_Acta1_msj() {
        return es_Acta1_msj;
    }

    /**
     * @param es_Acta1_msj the es_Acta1_msj to set
     */
    public void setEs_Acta1_msj(String es_Acta1_msj) {
        this.es_Acta1_msj = es_Acta1_msj;
    }

    /**
     * @return the es_Acta2_msj
     */
    public String getEs_Acta2_msj() {
        return es_Acta2_msj;
    }

    /**
     * @param es_Acta2_msj the es_Acta2_msj to set
     */
    public void setEs_Acta2_msj(String es_Acta2_msj) {
        this.es_Acta2_msj = es_Acta2_msj;
    }

    /**
     * @return the archivo_Acta1
     */
    public String getArchivo_Acta1() {
        return archivo_Acta1;
    }

    /**
     * @param archivo_Acta1 the archivo_Acta1 to set
     */
    public void setArchivo_Acta1(String archivo_Acta1) {
        this.archivo_Acta1 = archivo_Acta1;
    }

    /**
     * @return the archivo_Acta2
     */
    public String getArchivo_Acta2() {
        return archivo_Acta2;
    }

    /**
     * @param archivo_Acta2 the archivo_Acta2 to set
     */
    public void setArchivo_Acta2(String archivo_Acta2) {
        this.archivo_Acta2 = archivo_Acta2;
    }

    /**
     * @return the co_usuario
     */
    public String getCo_usuario() {
        return co_usuario;
    }

    /**
     * @param co_usuario the co_usuario to set
     */
    public void setCo_usuario(String co_usuario) {
        this.co_usuario = co_usuario;
    }


    

}
