/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import pe.gob.onpe.tramitedoc.util.BasePaginate;

/**
 *
 * @author ECueva
 */
public class ProveedorBean extends BasePaginate{

    private String nuRuc;
    private String descripcion;
    private String dproFecins;
    private String cproDomicil;
    private String cubiCoddep;
    private String cubiCodpro;
    private String cubiCoddis;
    private String noDep;
    private String noPrv;
    private String noDis;
    private String cproTelefo;
    private String cproEmail;
    
    private String idDepartamento;
    private String idProvincia;
    private String idDistrito;
    private String deDireccion;
    private String deCorreo;    
    private String telefono;

    public String getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(String idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(String idProvincia) {
        this.idProvincia = idProvincia;
    }

    public String getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(String idDistrito) {
        this.idDistrito = idDistrito;
    }

    public String getDeDireccion() {
        return deDireccion;
    }

    public void setDeDireccion(String deDireccion) {
        this.deDireccion = deDireccion;
    }

    public String getDeCorreo() {
        return deCorreo;
    }

    public void setDeCorreo(String deCorreo) {
        this.deCorreo = deCorreo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public ProveedorBean() {
    }

    public String getNuRuc() {
        return nuRuc;
    }

    public void setNuRuc(String nuRuc) {
        this.nuRuc = nuRuc;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getDproFecins() {
        return dproFecins;
    }

    public void setDproFecins(String dproFecins) {
        this.dproFecins = dproFecins;
    }

    public String getCproDomicil() {
        return cproDomicil;
    }

    public void setCproDomicil(String cproDomicil) {
        this.cproDomicil = cproDomicil;
    }

    public String getCubiCoddep() {
        return cubiCoddep;
    }

    public void setCubiCoddep(String cubiCoddep) {
        this.cubiCoddep = cubiCoddep;
    }

    public String getCubiCodpro() {
        return cubiCodpro;
    }

    public void setCubiCodpro(String cubiCodpro) {
        this.cubiCodpro = cubiCodpro;
    }

    public String getCubiCoddis() {
        return cubiCoddis;
    }

    public void setCubiCoddis(String cubiCoddis) {
        this.cubiCoddis = cubiCoddis;
    }

    public String getNoDep() {
        return noDep;
    }

    public void setNoDep(String noDep) {
        this.noDep = noDep;
    }

    public String getNoPrv() {
        return noPrv;
    }

    public void setNoPrv(String noPrv) {
        this.noPrv = noPrv;
    }

    public String getNoDis() {
        return noDis;
    }

    public void setNoDis(String noDis) {
        this.noDis = noDis;
    }

    public String getCproTelefo() {
        return cproTelefo;
    }

    public void setCproTelefo(String cproTelefo) {
        this.cproTelefo = cproTelefo;
    }

    /**
     * @return the cproEmail
     */
    public String getCproEmail() {
        return cproEmail;
    }

    /**
     * @param cproEmail the cproEmail to set
     */
    public void setCproEmail(String cproEmail) {
        this.cproEmail = cproEmail;
    }

    public ProveedorBean(String nuRuc, String descripcion) {
        this.nuRuc = nuRuc;
        this.descripcion = descripcion;
    }
    
    
    
    
}

