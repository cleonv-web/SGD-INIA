package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.sql.Date;

public class Expediente implements Serializable {

    private static final long serialVersionUID = 1L;
    private String nuannexp;
    private String nusecexp;
    private Date feexp;
    private Date fevence;
    private String coproceso;
    private String dedetalle;
    private String codepexp;
    private String cogru;
    private String nucorrexp;
    private String nuexpediente;
    private int nufolios;
    private int nuplazo;
    private String uscreaaudi;
    private Date fecreaaudi;
    private String usmodiaudi;
    private Date femodiaudi;
    private String esestado;
    private String tirecmp;
    private String ccodtipoexp;

    public String getNuannexp() {
        return nuannexp;
    }

    public void setNuannexp(String nuannexp) {
        this.nuannexp = nuannexp;
    }

    public String getNusecexp() {
        return nusecexp;
    }

    public void setNusecexp(String nusecexp) {
        this.nusecexp = nusecexp;
    }

    public Date getFeexp() {
        return feexp;
    }

    public void setFeexp(Date feexp) {
        this.feexp = feexp;
    }

    public Date getFevence() {
        return fevence;
    }

    public void setFevence(Date fevence) {
        this.fevence = fevence;
    }

    public String getCoproceso() {
        return coproceso;
    }

    public void setCoproceso(String coproceso) {
        this.coproceso = coproceso;
    }

    public String getDedetalle() {
        return dedetalle;
    }

    public void setDedetalle(String dedetalle) {
        this.dedetalle = dedetalle;
    }

    public String getCodepexp() {
        return codepexp;
    }

    public void setCodepexp(String codepexp) {
        this.codepexp = codepexp;
    }

    public String getCogru() {
        return cogru;
    }

    public void setCogru(String cogru) {
        this.cogru = cogru;
    }

    public String getNucorrexp() {
        return nucorrexp;
    }

    public void setNucorrexp(String nucorrexp) {
        this.nucorrexp = nucorrexp;
    }

    public String getNuexpediente() {
        return nuexpediente;
    }

    public void setNuexpediente(String nuexpediente) {
        this.nuexpediente = nuexpediente;
    }

    public int getNufolios() {
        return nufolios;
    }

    public void setNufolios(int nufolios) {
        this.nufolios = nufolios;
    }

    public int getNuplazo() {
        return nuplazo;
    }

    public void setNuplazo(int nuplazo) {
        this.nuplazo = nuplazo;
    }

    public String getUscreaaudi() {
        return uscreaaudi;
    }

    public void setUscreaaudi(String uscreaaudi) {
        this.uscreaaudi = uscreaaudi;
    }

    public Date getFecreaaudi() {
        return fecreaaudi;
    }

    public void setFecreaaudi(Date fecreaaudi) {
        this.fecreaaudi = fecreaaudi;
    }

    public String getUsmodiaudi() {
        return usmodiaudi;
    }

    public void setUsmodiaudi(String usmodiaudi) {
        this.usmodiaudi = usmodiaudi;
    }

    public Date getFemodiaudi() {
        return femodiaudi;
    }

    public void setFemodiaudi(Date femodiaudi) {
        this.femodiaudi = femodiaudi;
    }

    public String getEsestado() {
        return esestado;
    }

    public void setEsestado(String esestado) {
        this.esestado = esestado;
    }

    public String getTirecmp() {
        return tirecmp;
    }

    public void setTirecmp(String tirecmp) {
        this.tirecmp = tirecmp;
    }

    public String getCcodtipoexp() {
        return ccodtipoexp;
    }

    public void setCcodtipoexp(String ccodtipoexp) {
        this.ccodtipoexp = ccodtipoexp;
    }

}
