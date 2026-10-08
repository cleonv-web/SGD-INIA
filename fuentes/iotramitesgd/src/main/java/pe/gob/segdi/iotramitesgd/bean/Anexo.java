package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.sql.Date;

public class Anexo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String nuann;
    private String nuemi;
    private int nuane;
    private String dedet;
    private byte[] bldoc;
    private String derutori;
    private String derutdes;
    private String cotipdoc;
    private String cousecre;
    private Date feusecre;
    private String cousemod;
    private Date feusemod;
    private String feula;
    private String reqfirma;
    private int nudes;

    public String getNuann() {
        return nuann;
    }

    public void setNuann(String nuann) {
        this.nuann = nuann;
    }

    public String getNuemi() {
        return nuemi;
    }

    public void setNuemi(String nuemi) {
        this.nuemi = nuemi;
    }

    public int getNuane() {
        return nuane;
    }

    public void setNuane(int nuane) {
        this.nuane = nuane;
    }

    public String getDedet() {
        return dedet;
    }

    public void setDedet(String dedet) {
        this.dedet = dedet;
    }

    public byte[] getBldoc() {
        return bldoc;
    }

    public void setBldoc(byte[] bldoc) {
        this.bldoc = bldoc;
    }

    public String getDerutori() {
        return derutori;
    }

    public void setDerutori(String derutori) {
        this.derutori = derutori;
    }

    public String getDerutdes() {
        return derutdes;
    }

    public void setDerutdes(String derutdes) {
        this.derutdes = derutdes;
    }

    public String getCotipdoc() {
        return cotipdoc;
    }

    public void setCotipdoc(String cotipdoc) {
        this.cotipdoc = cotipdoc;
    }

    public String getCousecre() {
        return cousecre;
    }

    public void setCousecre(String cousecre) {
        this.cousecre = cousecre;
    }

    public Date getFeusecre() {
        return feusecre;
    }

    public void setFeusecre(Date feusecre) {
        this.feusecre = feusecre;
    }

    public String getCousemod() {
        return cousemod;
    }

    public void setCousemod(String cousemod) {
        this.cousemod = cousemod;
    }

    public Date getFeusemod() {
        return feusemod;
    }

    public void setFeusemod(Date feusemod) {
        this.feusemod = feusemod;
    }

    public String getFeula() {
        return feula;
    }

    public void setFeula(String feula) {
        this.feula = feula;
    }

    public String getReqfirma() {
        return reqfirma;
    }

    public void setReqfirma(String reqfirma) {
        this.reqfirma = reqfirma;
    }

    public int getNudes() {
        return nudes;
    }

    public void setNudes(int nudes) {
        this.nudes = nudes;
    }


}
