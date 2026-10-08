/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ECueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class RemitenteEmiBean {
    private String coDependencia;
    private String deDependencia;
    private String coEmpFirma;
    private String deEmpFirma;
    private String coLocal;
    private String deLocal;
    private String coEmpElabora;
    private String deEmpElabora;

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }

    public String getCoEmpFirma() {
        return coEmpFirma;
    }

    public void setCoEmpFirma(String coEmpFirma) {
        this.coEmpFirma = coEmpFirma;
    }

    public String getDeEmpFirma() {
        return deEmpFirma;
    }

    public void setDeEmpFirma(String deEmpFirma) {
        this.deEmpFirma = deEmpFirma;
    }

    public String getCoLocal() {
        return coLocal;
    }

    public void setCoLocal(String coLocal) {
        this.coLocal = coLocal;
    }

    public String getDeLocal() {
        return deLocal;
    }

    public void setDeLocal(String deLocal) {
        this.deLocal = deLocal;
    }

    public String getCoEmpElabora() {
        return coEmpElabora;
    }

    public void setCoEmpElabora(String coEmpElabora) {
        this.coEmpElabora = coEmpElabora;
    }

    public String getDeEmpElabora() {
        return deEmpElabora;
    }

    public void setDeEmpElabora(String deEmpElabora) {
        this.deEmpElabora = deEmpElabora;
    }
    
}
