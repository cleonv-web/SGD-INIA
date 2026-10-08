package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.util.Date;

public class IODocumentoAnexo implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int siddocext;
    private int siddocanx;
    private String vnomdoc;
    private String nu_emi;
    private String nu_ann;
    private int nu_ane;
    private String de_det;
    private byte[] bdocanx;
    private byte[] bl_doc;
    private Date dfecreg;
    private String vcuo;

    public String getNu_emi() {
        return nu_emi;
    }

    public void setNu_emi(String nu_emi) {
        this.nu_emi = nu_emi;
    }

    public String getNu_ann() {
        return nu_ann;
    }

    public void setNu_ann(String nu_ann) {
        this.nu_ann = nu_ann;
    }

    public int getNu_ane() {
        return nu_ane;
    }

    public void setNu_ane(int nu_ane) {
        this.nu_ane = nu_ane;
    }

    public String getDe_det() {
        return de_det;
    }

    public void setDe_det(String de_det) {
        this.de_det = de_det;
    }

    public byte[] getBl_doc() {
        return bl_doc;
    }

    public void setBl_doc(byte[] bl_doc) {
        this.bl_doc = bl_doc;
    }

    public String getVcuo() {
        return vcuo;
    }

    public void setVcuo(String vcuo) {
        this.vcuo = vcuo;
    }

    public int getSiddocext() {
        return siddocext;
    }

    public void setSiddocext(int siddocext) {
        this.siddocext = siddocext;
    }

    public int getSiddocanx() {
        return siddocanx;
    }

    public void setSiddocanx(int siddocanx) {
        this.siddocanx = siddocanx;
    }

    public String getVnomdoc() {
        return vnomdoc;
    }

    public void setVnomdoc(String vnomdoc) {
        this.vnomdoc = vnomdoc;
    }

    public byte[] getBdocanx() {
        return bdocanx;
    }

    public void setBdocanx(byte[] bdocanx) {
        this.bdocanx = bdocanx;
    }

    public Date getDfecreg() {
        return dfecreg;
    }

    public void setDfecreg(Date dfecreg) {
        this.dfecreg = dfecreg;
    }


}
