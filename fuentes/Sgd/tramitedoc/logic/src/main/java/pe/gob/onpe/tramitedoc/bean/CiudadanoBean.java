/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class CiudadanoBean {
    private String nombre;
    private String nuDocumento;
    private String ubdep;
    private String ubprv;
    private String ubdis;
    private String dedomicil;
    private String deemail;
    private String detelefo;    
    private String idDepartamento;
    private String idProvincia;
    private String idDistrito;
    private String deDireccion;
    private String deCorreo;
    private String telefono;
    private String ubigeo;
    
    public CiudadanoBean() {
    }

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

    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getNuDocumento() {
        return nuDocumento;
    }

    public void setNuDocumento(String nuDocumento) {
        this.nuDocumento = nuDocumento;
    }

    /**
     * @return the ubigeo
     */
    public String getUbigeo() {
        return ubigeo;
    }

    /**
     * @param ubigeo the ubigeo to set
     */
    public void setUbigeo(String ubigeo) {
        this.ubigeo = ubigeo;
    }

    /**
     * @return the ubdep
     */
    public String getUbdep() {
        return ubdep;
    }

    /**
     * @param ubdep the ubdep to set
     */
    public void setUbdep(String ubdep) {
        this.ubdep = ubdep;
    }

    /**
     * @return the ubprv
     */
    public String getUbprv() {
        return ubprv;
    }

    /**
     * @param ubprv the ubprv to set
     */
    public void setUbprv(String ubprv) {
        this.ubprv = ubprv;
    }

    /**
     * @return the ubdis
     */
    public String getUbdis() {
        return ubdis;
    }

    /**
     * @param ubdis the ubdis to set
     */
    public void setUbdis(String ubdis) {
        this.ubdis = ubdis;
    }

    /**
     * @return the dedomicil
     */
    public String getDedomicil() {
        return dedomicil;
    }

    /**
     * @param dedomicil the dedomicil to set
     */
    public void setDedomicil(String dedomicil) {
        this.dedomicil = dedomicil;
    }

    /**
     * @return the deemail
     */
    public String getDeemail() {
        return deemail;
    }

    /**
     * @param deemail the deemail to set
     */
    public void setDeemail(String deemail) {
        this.deemail = deemail;
    }

    /**
     * @return the detelefo
     */
    public String getDetelefo() {
        return detelefo;
    }

    /**
     * @param detelefo the detelefo to set
     */
    public void setDetelefo(String detelefo) {
        this.detelefo = detelefo;
    }
}
