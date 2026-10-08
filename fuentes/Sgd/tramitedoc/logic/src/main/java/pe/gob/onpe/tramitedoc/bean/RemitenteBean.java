/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ecueva
 */
public class RemitenteBean {
    private String descrip;
    private String codDep;
    private String deCortaDepen;

    public RemitenteBean() {
    }

    public String getDeCortaDepen() {
        return deCortaDepen;
    }

    public void setDeCortaDepen(String deCortaDepen) {
        this.deCortaDepen = deCortaDepen;
    }

    public String getDescrip() {
        return descrip;
    }

    public void setDescrip(String descrip) {
        this.descrip = descrip;
    }

    public String getCodDep() {
        return codDep;
    }

    public void setCodDep(String codDep) {
        this.codDep = codDep;
    }
}
