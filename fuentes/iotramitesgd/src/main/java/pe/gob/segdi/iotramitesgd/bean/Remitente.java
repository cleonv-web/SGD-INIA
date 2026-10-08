package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class Remitente implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String cproruc;
    private String cprorazsoc;
    private String cDireccion;

    public String getCproruc() {
        return cproruc;
    }

    public void setCproruc(String cproruc) {
        this.cproruc = cproruc;
    }

    public String getCprorazsoc() {
        return cprorazsoc;
    }

    public void setCprorazsoc(String cprorazsoc) {
        this.cprorazsoc = cprorazsoc;
    }

    public String getcDireccion() {
        return cDireccion;
    }

    public void setcDireccion(String cDireccion) {
        this.cDireccion = cDireccion;
    }


}
