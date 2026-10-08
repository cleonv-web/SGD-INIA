package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.util.Date;

public class IODocumentoPrincipal implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int siddocext;
    private int siddocpri;
    private String vnomdocpri;
    private byte[] bpdfdocpri;
    private String ccodest;
    private Date dfecreg;
    private byte[] bpdfdoc;

    public byte[] getBpdfdoc() {
        return bpdfdoc;
    }

    public void setBpdfdoc(byte[] bpdfdoc) {
        this.bpdfdoc = bpdfdoc;
    }

    public int getSiddocext() {
        return siddocext;
    }

    public void setSiddocext(int siddocext) {
        this.siddocext = siddocext;
    }

    public int getSiddocpri() {
        return siddocpri;
    }

    public void setSiddocpri(int siddocpri) {
        this.siddocpri = siddocpri;
    }

    public String getVnomdocpri() {
        return vnomdocpri;
    }

    public void setVnomdocpri(String vnomdocpri) {
        this.vnomdocpri = vnomdocpri;
    }

    public byte[] getBpdfdocpri() {
        return bpdfdocpri;
    }

    public void setBpdfdocpri(byte[] bpdfdocpri) {
        this.bpdfdocpri = bpdfdocpri;
    }

    public String getCcodest() {
        return ccodest;
    }

    public void setCcodest(String ccodest) {
        this.ccodest = ccodest;
    }

    public Date getDfecreg() {
        return dfecreg;
    }

    public void setDfecreg(Date dfecreg) {
        this.dfecreg = dfecreg;
    }


}
