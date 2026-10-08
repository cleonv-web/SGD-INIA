/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.util.Date;

public class DatosUsuario implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private boolean isLoggedIn;
    private String codRespuesta;
    private String desRespuesta;
    private String cousuario;
    private String dePassword;

    //////////  DATOS USUARIO /////////
    private String codUser;
    private String cempNuDni;
    private String cempCodemp;
    private String esActivo;
    private String esAdmin;
    private Date feModClave;
    private Date feActual;
    private Date dFecMod; //fecha de actualizacion del registro
    private Date fullFechaActual;
    private int nroIntento;
    private String inAdmin;
    private String coDepEmi;
    private String coLocEmi;
    private String nombreCompleto;
    //////////////////////
    private String contrasenia;
    private String descripcion;
    private Rol rol;
    private String codependencia;
    private String dedependencia;

    public String getCempNuDni() {
        return cempNuDni;
    }

    public void setCempNuDni(String cempNuDni) {
        this.cempNuDni = cempNuDni;
    }

    public boolean isLoggedIn() {
        return isLoggedIn;
    }

    public void setLoggedIn(boolean isLoggedIn) {
        this.isLoggedIn = isLoggedIn;
    }

    public String getNombreCompleto() {
        return nombreCompleto;
    }

    public void setNombreCompleto(String nombreCompleto) {
        this.nombreCompleto = nombreCompleto;
    }

    public String getCoDepEmi() {
        return coDepEmi;
    }

    public void setCoDepEmi(String coDepEmi) {
        this.coDepEmi = coDepEmi;
    }

    public String getCoLocEmi() {
        return coLocEmi;
    }

    public void setCoLocEmi(String coLocEmi) {
        this.coLocEmi = coLocEmi;
    }

    public String getCousuario() {
        return cousuario;
    }

    public void setCousuario(String cousuario) {
        this.cousuario = cousuario;
    }

    public String getCodependencia() {
        return codependencia;
    }

    public void setCodependencia(String codependencia) {
        this.codependencia = codependencia;
    }

    public String getDedependencia() {
        return dedependencia;
    }

    public void setDedependencia(String dedependencia) {
        this.dedependencia = dedependencia;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getInAdmin() {
        return inAdmin;
    }

    public void setInAdmin(String inAdmin) {
        this.inAdmin = inAdmin;
    }

    public int getNroIntento() {
        return nroIntento;
    }

    public void setNroIntento(int nroIntento) {
        this.nroIntento = nroIntento;
    }

    public Date getdFecMod() {
        return dFecMod;
    }

    public void setdFecMod(Date dFecMod) {
        this.dFecMod = dFecMod;
    }

    public Date getFullFechaActual() {
        return fullFechaActual;
    }

    public void setFullFechaActual(Date fullFechaActual) {
        this.fullFechaActual = fullFechaActual;
    }

    public Date getFeModClave() {
        return feModClave;
    }

    public void setFeModClave(Date feModClave) {
        this.feModClave = feModClave;
    }

    public Date getFeActual() {
        return feActual;
    }

    public void setFeActual(Date feActual) {
        this.feActual = feActual;
    }

    public String getCodRespuesta() {
        return codRespuesta;
    }

    public void setCodRespuesta(String codRespuesta) {
        this.codRespuesta = codRespuesta;
    }

    public String getDesRespuesta() {
        return desRespuesta;
    }

    public void setDesRespuesta(String desRespuesta) {
        this.desRespuesta = desRespuesta;
    }

    public String getDePassword() {
        return dePassword;
    }

    public void setDePassword(String dePassword) {
        this.dePassword = dePassword;
    }

    public String getCempCodemp() {
        return cempCodemp;
    }

    public void setCempCodemp(String cempCodemp) {
        this.cempCodemp = cempCodemp;
    }

    public String getEsActivo() {
        return esActivo;
    }

    public void setEsActivo(String esActivo) {
        this.esActivo = esActivo;
    }

    public String getEsAdmin() {
        return esAdmin;
    }

    public void setEsAdmin(String esAdmin) {
        this.esAdmin = esAdmin;
    }

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getCodUser() {
        return codUser;
    }

    public void setCodUser(String codUser) {
        this.codUser = codUser;
    }


}
