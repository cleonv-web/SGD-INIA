/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class DestinatarioDocumentoEmiConsulBean {
    private String nuAnn;
    private String nuEmi;
    private String nuDes;
    private String coLocal;
    private String deLocal;
    private String coDependencia;
    private String deDependencia;
    private String coEmpleado;
    private String deEmpleado;
    private String coTramite;
    private String deTramite;
    private String coPrioridad;
    private String dePrioridad;
    private String deIndicaciones;
    private String nuRuc;
    private String deProveedor;
    private String nuDni;
    private String deCiudadano;
    private String coOtroOrigen;
    private String deOtroOrigenFull;
    private String coTipoDestino;
    private String deNombreOtroOrigen;
    private String deTipoDocOtroOrigen;
    private String nuDocOtroOrigen;

    public DestinatarioDocumentoEmiConsulBean() {
    }

    public String getDePrioridad() {
        return dePrioridad;
    }

    public void setDePrioridad(String dePrioridad) {
        this.dePrioridad = dePrioridad;
    }

    public String getNuAnn() {
        return nuAnn;
    }

    public String getDeTipoDocOtroOrigen() {
        return deTipoDocOtroOrigen;
    }

    public void setDeTipoDocOtroOrigen(String deTipoDocOtroOrigen) {
        this.deTipoDocOtroOrigen = deTipoDocOtroOrigen;
    }

    public String getNuDocOtroOrigen() {
        return nuDocOtroOrigen;
    }

    public void setNuDocOtroOrigen(String nuDocOtroOrigen) {
        this.nuDocOtroOrigen = nuDocOtroOrigen;
    }

    public String getDeNombreOtroOrigen() {
        return deNombreOtroOrigen;
    }

    public void setDeNombreOtroOrigen(String deNombreOtroOrigen) {
        this.deNombreOtroOrigen = deNombreOtroOrigen;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getNuEmi() {
        return nuEmi;
    }

    public void setNuEmi(String nuEmi) {
        this.nuEmi = nuEmi;
    }

    public String getNuDes() {
        return nuDes;
    }

    public void setNuDes(String nuDes) {
        this.nuDes = nuDes;
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

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
    }

    public String getDeEmpleado() {
        return deEmpleado;
    }

    public void setDeEmpleado(String deEmpleado) {
        this.deEmpleado = deEmpleado;
    }

    public String getCoTramite() {
        return coTramite;
    }

    public void setCoTramite(String coTramite) {
        this.coTramite = coTramite;
    }

    public String getDeTramite() {
        return deTramite;
    }

    public void setDeTramite(String deTramite) {
        this.deTramite = deTramite;
    }

    public String getCoPrioridad() {
        return coPrioridad;
    }

    public void setCoPrioridad(String coPrioridad) {
        this.coPrioridad = coPrioridad;
    }

    public String getDeIndicaciones() {
        return deIndicaciones;
    }

    public void setDeIndicaciones(String deIndicaciones) {
        this.deIndicaciones = deIndicaciones;
    }

    public String getNuRuc() {
        return nuRuc;
    }

    public void setNuRuc(String nuRuc) {
        this.nuRuc = nuRuc;
    }

    public String getDeProveedor() {
        return deProveedor;
    }

    public void setDeProveedor(String deProveedor) {
        this.deProveedor = deProveedor;
    }

    public String getNuDni() {
        return nuDni;
    }

    public void setNuDni(String nuDni) {
        this.nuDni = nuDni;
    }

    public String getDeCiudadano() {
        return deCiudadano;
    }

    public void setDeCiudadano(String deCiudadano) {
        this.deCiudadano = deCiudadano;
    }

    public String getCoOtroOrigen() {
        return coOtroOrigen;
    }

    public void setCoOtroOrigen(String coOtroOrigen) {
        this.coOtroOrigen = coOtroOrigen;
    }

    public String getDeOtroOrigenFull() {
        return deOtroOrigenFull;
    }

    public void setDeOtroOrigenFull(String deOtroOrigenFull) {
        String[] array = deOtroOrigenFull != null ? deOtroOrigenFull.split("##") : new String[0];
        for (int i = 0; i < array.length; i++) {
            switch(i) {
                case 0:
                    this.deNombreOtroOrigen = array[i];
                    break;
                case 1:
                    this.deTipoDocOtroOrigen = array[i];
                    break;
                case 2:
                    this.nuDocOtroOrigen = array[i];
                    break;                    
                 default: 
                    break;
                }
            }
        this.deOtroOrigenFull = deOtroOrigenFull;
    }    

    public String getCoTipoDestino() {
        return coTipoDestino;
    }

    public void setCoTipoDestino(String coTipoDestino) {
        this.coTipoDestino = coTipoDestino;
    }
}
