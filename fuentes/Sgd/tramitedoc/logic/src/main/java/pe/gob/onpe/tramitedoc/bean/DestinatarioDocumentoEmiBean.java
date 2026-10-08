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
public class DestinatarioDocumentoEmiBean {
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

    private String cdirRemite;
    private String cexpCorreoe;
    private String cTelefono;
    private String ccodDpto;
    private String ccodProv;
    private String ccodDist;
    private String remiTiEmi;
    private String remiNuDniEmi;
    private String remiCoOtrOriEmi;
    private String ubigeo;
    private String nombres;
    private String deCargoFunDestMae;
    private String nombreDestinatario;
    private String direccionDestinatario;
    private String entidadPrivadaDestinatario;
    private String cargo;
    private String deDepDestMae;
    
    
    private String cidCat;
    private String deCat;
    private String deDepDes;
    private String deNomDes;
    private String deCarDes;
    private String nuFol;
    private String nuAnex;
    
    public String getCargo() {
        return cargo;
    }
            
    public void setCargo(String cargo) {
        this.cargo = cargo;
    }
    
    public String getUbigeo() {
        return ubigeo;
    }

    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    public String getNombres() {
        return nombres;
    }

    public void setNombres(String nombres) {
        this.nombres = nombres;
    }
    
    
    public String getCdirRemite() {
        return cdirRemite;
    }

    public void setCdirRemite(String cdirRemite) {
        this.cdirRemite = cdirRemite;
    }

    public String getCexpCorreoe() {
        return cexpCorreoe;
    }

    public void setCexpCorreoe(String cexpCorreoe) {
        this.cexpCorreoe = cexpCorreoe;
    }

    public String getcTelefono() {
        return cTelefono;
    }

    public void setcTelefono(String cTelefono) {
        this.cTelefono = cTelefono;
    }

    public String getCcodDpto() {
        return ccodDpto;
    }

    public void setCcodDpto(String ccodDpto) {
        this.ccodDpto = ccodDpto;
    }

    public String getCcodProv() {
        return ccodProv;
    }

    public void setCcodProv(String ccodProv) {
        this.ccodProv = ccodProv;
    }

    public String getCcodDist() {
        return ccodDist;
    }

    public void setCcodDist(String ccodDist) {
        this.ccodDist = ccodDist;
    }

    public String getRemiTiEmi() {
        return remiTiEmi;
    }

    public void setRemiTiEmi(String remiTiEmi) {
        this.remiTiEmi = remiTiEmi;
    }

    public String getRemiNuDniEmi() {
        return remiNuDniEmi;
    }

    public void setRemiNuDniEmi(String remiNuDniEmi) {
        this.remiNuDniEmi = remiNuDniEmi;
    }

    public String getRemiCoOtrOriEmi() {
        return remiCoOtrOriEmi;
    }

    public void setRemiCoOtrOriEmi(String remiCoOtrOriEmi) {
        this.remiCoOtrOriEmi = remiCoOtrOriEmi;
    }

    
    
    public DestinatarioDocumentoEmiBean() {
        this.envMesaPartes="0";
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

    public String getDeCargoFunDestMae() {
        return deCargoFunDestMae;
    }

    public void setDeCargoFunDestMae(String deCargoFunDestMae) {
        this.deCargoFunDestMae = deCargoFunDestMae;
    }

    public String getNombreDestinatario() {
        return nombreDestinatario;
    }

    public void setNombreDestinatario(String nombreDestinatario) {
        this.nombreDestinatario = nombreDestinatario;
    }

    public String getDireccionDestinatario() {
        return direccionDestinatario;
    }

    public void setDireccionDestinatario(String direccionDestinatario) {
        this.direccionDestinatario = direccionDestinatario;
    }

    public String getEntidadPrivadaDestinatario() {
        return entidadPrivadaDestinatario;
    }

    public void setEntidadPrivadaDestinatario(String entidadPrivadaDestinatario) {
        this.entidadPrivadaDestinatario = entidadPrivadaDestinatario;
    }

    public String getDeDepDestMae() {
        return deDepDestMae;
    }

    public void setDeDepDestMae(String deDepDestMae) {
        this.deDepDestMae = deDepDestMae;
    }

    public String getCidCat() {
        return cidCat;
    }

    public void setCidCat(String cidCat) {
        this.cidCat = cidCat;
    }

    public String getDeCat() {
        return deCat;
    }

    public void setDeCat(String deCat) {
        this.deCat = deCat;
    }

    public String getDeDepDes() {
        return deDepDes;
    }

    public void setDeDepDes(String deDepDes) {
        this.deDepDes = deDepDes;
    }

    public String getDeNomDes() {
        return deNomDes;
    }

    public void setDeNomDes(String deNomDes) {
        this.deNomDes = deNomDes;
    }

    public String getDeCarDes() {
        return deCarDes;
    }

    public void setDeCarDes(String deCarDes) {
        this.deCarDes = deCarDes;
    }

    public String getNuFol() {
        return nuFol;
    }

    public void setNuFol(String nuFol) {
        this.nuFol = nuFol;
    }

    public String getNuAnex() {
        return nuAnex;
    }

    public void setNuAnex(String nuAnex) {
        this.nuAnex = nuAnex;
    }
    
    
    
    
}
