/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author oti2
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DestiDocumentoEnvMensajeriaBean {
    private String coDependencia;
    private String deDependencia;
    private String coLocal;
    private String deLocal;
    private String coEmpleado;
    private String deEmpleado;
    private String coTramite;
    private String deTramite;    
    private String coTramiteFirst;
    private String deTramiteFirst;
    private String coTramiteNext;
    private String deTramiteNext;    
    private String deIndicaciones;
    private String coPrioridad;
    private String nuAnn;
    private String nuEmi;
    private String nuDes;
    private String nuRuc;
    private String deProveedor;
    private String nuDni;
    private String deCiudadano;
    private String coOtroOrigen;
    private String deOtroOrigenFull;
    private String deNombreOtroOrigen;
    private String deTipoDocOtroOrigen;
    private String nuDocOtroOrigen;
    private String coTipoDestino;
    private String accionBD;
    private String coUseCre;
    private String coUseMod;
    private String envMesaPartes;
    private String fila;
    private String documento;
    private String departamento;
    private String direccion;
    private String destinatario;
    private String nuDoc;
    private String ambito;
    private String feExpCorta;
    private String fecEnviomsj;
    private String deCargo;
    private String guia; 
    private String feEmiCorta; 
    private String ccodProv; 
    private String ccodDpto; 
    private String ccodDist;
    
    public String getCcodProv() {
        return ccodProv;
    }

    public void setCcodProv(String ccodProv) {
        this.ccodProv = ccodProv;
    }

    public String getCcodDpto() {
        return ccodDpto;
    }

    public void setCcodDpto(String ccodDpto) {
        this.ccodDpto = ccodDpto;
    }

    public String getCcodDist() {
        return ccodDist;
    }

    public void setCcodDist(String ccodDist) {
        this.ccodDist = ccodDist;
    }
    
    public String getFecEnviomsj() {
        return fecEnviomsj;
    }

    public void setFecEnviomsj(String fecEnviomsj) {
        this.fecEnviomsj = fecEnviomsj;
    }
    
    public String getFeEmiCorta() {
        return feEmiCorta;
    }

    public void setFeEmiCorta(String feEmiCorta) {
        this.feEmiCorta = feEmiCorta;
    }
    public String getGuia() {
        return guia;
    }

    public void setGuia(String guia) {
        this.guia = guia;
    }
    public String getDeCargo() {
        return deCargo;
    }

    public void setDeCargo(String deCargo) {
        this.deCargo = deCargo;
    }
    
    
    public String getFeExpCorta() {
        return feExpCorta;
    }

    public void setFeExpCorta(String feExpCorta) {
        this.feExpCorta = feExpCorta;
    }

    public String getAmbito() {
        return ambito;
    }

    public void setAmbito(String ambito) {
        this.ambito = ambito;
    }
    
    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }
    
    public DestiDocumentoEnvMensajeriaBean() {
        this.envMesaPartes="0";
    }
    public String getDestinatario() {
        return destinatario;
    }

    public void setDestinatario(String destinatario) {
        this.destinatario = destinatario;
    }
     public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }
    
    public String getDepartamento() {
        return departamento;
    }

    public void setDepartamento(String departamento) {
        this.departamento = departamento;
    }
    public String getDocumento() {
        return documento;
    }

    public void setDocumento(String documento) {
        this.documento = documento;
    }
    public String getFila() {
        return fila;
    }

    public void setFila(String fila) {
        this.fila = fila;
    }
    public String getEnvMesaPartes() {
        return envMesaPartes;
    }

    public void setEnvMesaPartes(String envMesaPartes) {
        this.envMesaPartes = envMesaPartes;
    }

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public String getAccionBD() {
        return accionBD;
    }

    public void setAccionBD(String accionBD) {
        this.accionBD = accionBD;
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

    public String getDeNombreOtroOrigen() {
        return deNombreOtroOrigen;
    }

    public void setDeNombreOtroOrigen(String deNombreOtroOrigen) {
        this.deNombreOtroOrigen = deNombreOtroOrigen;
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

    public String getCoTipoDestino() {
        return coTipoDestino;
    }

    public void setCoTipoDestino(String coTipoDestino) {
        this.coTipoDestino = coTipoDestino;
    }

    public String getNuAnn() {
        return nuAnn;
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

    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }

    public String getDeLocal() {
        return deLocal;
    }

    public void setDeLocal(String deLocal) {
        this.deLocal = deLocal;
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

    public String getCoDependencia() {
        return coDependencia;
    }

    public void setCoDependencia(String coDependencia) {
        this.coDependencia = coDependencia;
    }

    public String getCoLocal() {
        return coLocal;
    }

    public void setCoLocal(String coLocal) {
        this.coLocal = coLocal;
    }

    public String getCoEmpleado() {
        return coEmpleado;
    }

    public void setCoEmpleado(String coEmpleado) {
        this.coEmpleado = coEmpleado;
    }

    public String getCoTramiteFirst() {
        return coTramiteFirst;
    }

    public void setCoTramiteFirst(String coTramiteFirst) {
        this.coTramiteFirst = coTramiteFirst;
    }

    public String getDeTramiteFirst() {
        return deTramiteFirst;
    }

    public void setDeTramiteFirst(String deTramiteFirst) {
        this.deTramiteFirst = deTramiteFirst;
    }

    public String getCoTramiteNext() {
        return coTramiteNext;
    }

    public void setCoTramiteNext(String coTramiteNext) {
        this.coTramiteNext = coTramiteNext;
    }

    public String getDeTramiteNext() {
        return deTramiteNext;
    }

    public void setDeTramiteNext(String deTramiteNext) {
        this.deTramiteNext = deTramiteNext;
    }

    public String getDeIndicaciones() {
        return deIndicaciones;
    }

    public void setDeIndicaciones(String deIndicaciones) {
        this.deIndicaciones = deIndicaciones;
    }

    public String getCoPrioridad() {
        return coPrioridad;
    }

    public void setCoPrioridad(String coPrioridad) {
        this.coPrioridad = coPrioridad;
    }
}
