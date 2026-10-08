package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class Archivo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String nuann;
    private String nuemi;
    private byte[] bldoc;
    private String derutaorigen;
    private String derutadestino;
    private String esfirma;
    private byte[] bldocedit;
    private String feula;

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

    public byte[] getBldoc() {
        return bldoc;
    }

    public void setBldoc(byte[] bldoc) {
        this.bldoc = bldoc;
    }

    public String getDerutaorigen() {
        return derutaorigen;
    }

    public void setDerutaorigen(String derutaorigen) {
        this.derutaorigen = derutaorigen;
    }

    public String getDerutadestino() {
        return derutadestino;
    }

    public void setDerutadestino(String derutadestino) {
        this.derutadestino = derutadestino;
    }

    public String getEsfirma() {
        return esfirma;
    }

    public void setEsfirma(String esfirma) {
        this.esfirma = esfirma;
    }

    public byte[] getBldocedit() {
        return bldocedit;
    }

    public void setBldocedit(byte[] bldocedit) {
        this.bldocedit = bldocedit;
    }

    public String getFeula() {
        return feula;
    }

    public void setFeula(String feula) {
        this.feula = feula;
    }

}
