package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class Correlativos implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String nuCorrExp;
    private String deSiglaCorta;

    public String getNuCorrExp() {
        return nuCorrExp;
    }

    public void setNuCorrExp(String nuCorrExp) {
        this.nuCorrExp = nuCorrExp;
    }

    public String getDeSiglaCorta() {
        return deSiglaCorta;
    }

    public void setDeSiglaCorta(String deSiglaCorta) {
        this.deSiglaCorta = deSiglaCorta;
    }


}
