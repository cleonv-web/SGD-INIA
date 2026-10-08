/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

import java.util.List;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ecueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class TrxGeneraGuiaMpBean {
    private String accionBd;
    private String nuAnnGuia;
    private String nuGuia;
    private String coUsuario;
    private GuiaMesaPartesBean guia;
    private List<DetGuiaMesaPartesBean> lsDetGuia;

    public String getAccionBd() {
        return accionBd;
    }

    public void setAccionBd(String accionBd) {
        this.accionBd = accionBd;
    }

    public String getCoUsuario() {
        return coUsuario;
    }

    public void setCoUsuario(String coUsuario) {
        this.coUsuario = coUsuario;
    }

    public String getNuAnnGuia() {
        return nuAnnGuia;
    }

    public void setNuAnnGuia(String nuAnnGuia) {
        this.nuAnnGuia = nuAnnGuia;
    }

    public String getNuGuia() {
        return nuGuia;
    }

    public void setNuGuia(String nuGuia) {
        this.nuGuia = nuGuia;
    }

    public GuiaMesaPartesBean getGuia() {
        return guia;
    }

    public void setGuia(GuiaMesaPartesBean guia) {
        this.guia = guia;
    }

    public List<DetGuiaMesaPartesBean> getLsDetGuia() {
        return lsDetGuia;
    }

    public void setLsDetGuia(List<DetGuiaMesaPartesBean> lsDetGuia) {
        this.lsDetGuia = lsDetGuia;
    }
    
}
