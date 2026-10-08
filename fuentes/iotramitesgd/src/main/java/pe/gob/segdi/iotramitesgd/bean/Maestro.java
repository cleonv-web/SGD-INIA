package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class Maestro implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String codependencia;
    private String dedependencia;

    private String cdoc_tipdoc;
    private String cdoc_desdoc;
    private String cidtipdoc;

    public String getCidtipdoc() {
        return cidtipdoc;
    }

    public void setCidtipdoc(String cidtipdoc) {
        this.cidtipdoc = cidtipdoc;
    }

    public String getCdoc_desdoc() {
        return cdoc_desdoc;
    }

    public void setCdoc_desdoc(String cdoc_desdoc) {
        this.cdoc_desdoc = cdoc_desdoc;
    }

    public String getCdoc_tipdoc() {
        return cdoc_tipdoc;
    }

    public void setCdoc_tipdoc(String cdoc_tipdoc) {
        this.cdoc_tipdoc = cdoc_tipdoc;
    }

    public String getCodependencia() {
        return codependencia;
    }

    public void setCodependencia(String codependencia) {
        this.codependencia = codependencia;
    }

    public String getDedependencia() {
        return dedependencia;
    }

    public void setDedependencia(String dedependencia) {
        this.dedependencia = dedependencia;
    }

}
