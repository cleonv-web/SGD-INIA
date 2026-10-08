package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class CboTramite implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String cCodOficina;
    private String cNomOficina;

    private String iCodTrabajador;
    private String cNombresTrabajador;


    public String getiCodTrabajador() {
        return iCodTrabajador;
    }

    public void setiCodTrabajador(String iCodTrabajador) {
        this.iCodTrabajador = iCodTrabajador;
    }

    public String getcNombresTrabajador() {
        return cNombresTrabajador;
    }

    public void setcNombresTrabajador(String cNombresTrabajador) {
        this.cNombresTrabajador = cNombresTrabajador;
    }


    public String getcCodOficina() {
        return cCodOficina;
    }

    public void setcCodOficina(String cCodOficina) {
        this.cCodOficina = cCodOficina;
    }

    public String getcNomOficina() {
        return cNomOficina;
    }

    public void setcNomOficina(String cNomOficina) {
        this.cNomOficina = cNomOficina;
    }


}
