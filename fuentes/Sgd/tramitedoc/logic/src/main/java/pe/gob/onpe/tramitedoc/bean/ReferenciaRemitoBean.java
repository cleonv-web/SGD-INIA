/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author NGilt
 */
public class ReferenciaRemitoBean {
    private String ti_emi;
    private String co_dep_emi;
    
    private String nu_emi;
    private String nu_ann;
    private String nu_des_ref;
    

    public String getTi_emi() {
        return ti_emi;
    }

    public void setTi_emi(String ti_emi) {
        this.ti_emi = ti_emi;
    }

    public String getCo_dep_emi() {
        return co_dep_emi;
    }

    public void setCo_dep_emi(String co_dep_emi) {
        this.co_dep_emi = co_dep_emi;
    }

    /**
     * @return the nu_emi
     */
    public String getNu_emi() {
        return nu_emi;
    }

    /**
     * @param nu_emi the nu_emi to set
     */
    public void setNu_emi(String nu_emi) {
        this.nu_emi = nu_emi;
    }

    /**
     * @return the nu_ann
     */
    public String getNu_ann() {
        return nu_ann;
    }

    /**
     * @param nu_ann the nu_ann to set
     */
    public void setNu_ann(String nu_ann) {
        this.nu_ann = nu_ann;
    }

    /**
     * @return the nu_des_ref
     */
    public String getNu_des_ref() {
        return nu_des_ref;
    }

    /**
     * @param nu_des_ref the nu_des_ref to set
     */
    public void setNu_des_ref(String nu_des_ref) {
        this.nu_des_ref = nu_des_ref;
    }
    
    
}
