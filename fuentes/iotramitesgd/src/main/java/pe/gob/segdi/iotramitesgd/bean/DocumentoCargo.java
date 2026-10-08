package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class DocumentoCargo implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String vnomdoc;
    private byte[] bcarstd;

    public String getVnomdoc() {
        return vnomdoc;
    }

    public void setVnomdoc(String vnomdoc) {
        this.vnomdoc = vnomdoc;
    }

    public byte[] getBcarstd() {
        return bcarstd;
    }

    public void setBcarstd(byte[] bcarstd) {
        this.bcarstd = bcarstd;
    }

}
