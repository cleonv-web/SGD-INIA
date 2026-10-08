package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class Trabajador implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int iCodTrabajador;
    private int iCodOficina;
    private int iCodPerfil;
    private String cNombresTrabajador;
    private String cApellidosTrabajador;
    private String cTipoDocIdentidad;
    private String cNumDocIdentidad;
    private String cDireccionTrabajador;
    private String cMailTrabajador;
    private String cTlfTrabajador1;
    private String cTlfTrabajador2;
    private String cCargo;
    private String cUsuario;
    private String cPassword;
    private String fUltimoAcceso;
    private int nFlgEstado;
    private int iCodCategoria;
    private String cNomArchivoFoto;
    private String cDescPerfil;
    private String usuario;
    private String contrasenia;
    private String descripcion;
    private Rol rol;

    public Rol getRol() {
        return rol;
    }

    public void setRol(Rol rol) {
        this.rol = rol;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getUsuario() {
        return usuario;
    }

    public void setUsuario(String usuario) {
        this.usuario = usuario;
    }

    public String getContrasenia() {
        return contrasenia;
    }

    public void setContrasenia(String contrasenia) {
        this.contrasenia = contrasenia;
    }

    public String getcDescPerfil() {
        return cDescPerfil;
    }

    public void setcDescPerfil(String cDescPerfil) {
        this.cDescPerfil = cDescPerfil;
    }

    public int getiCodTrabajador() {
        return iCodTrabajador;
    }

    public void setiCodTrabajador(int iCodTrabajador) {
        this.iCodTrabajador = iCodTrabajador;
    }

    public int getiCodOficina() {
        return iCodOficina;
    }

    public void setiCodOficina(int iCodOficina) {
        this.iCodOficina = iCodOficina;
    }

    public int getiCodPerfil() {
        return iCodPerfil;
    }

    public void setiCodPerfil(int iCodPerfil) {
        this.iCodPerfil = iCodPerfil;
    }

    public String getcNombresTrabajador() {
        return cNombresTrabajador;
    }

    public void setcNombresTrabajador(String cNombresTrabajador) {
        this.cNombresTrabajador = cNombresTrabajador;
    }

    public String getcApellidosTrabajador() {
        return cApellidosTrabajador;
    }

    public void setcApellidosTrabajador(String cApellidosTrabajador) {
        this.cApellidosTrabajador = cApellidosTrabajador;
    }

    public String getcTipoDocIdentidad() {
        return cTipoDocIdentidad;
    }

    public void setcTipoDocIdentidad(String cTipoDocIdentidad) {
        this.cTipoDocIdentidad = cTipoDocIdentidad;
    }

    public String getcNumDocIdentidad() {
        return cNumDocIdentidad;
    }

    public void setcNumDocIdentidad(String cNumDocIdentidad) {
        this.cNumDocIdentidad = cNumDocIdentidad;
    }

    public String getcDireccionTrabajador() {
        return cDireccionTrabajador;
    }

    public void setcDireccionTrabajador(String cDireccionTrabajador) {
        this.cDireccionTrabajador = cDireccionTrabajador;
    }

    public String getcMailTrabajador() {
        return cMailTrabajador;
    }

    public void setcMailTrabajador(String cMailTrabajador) {
        this.cMailTrabajador = cMailTrabajador;
    }

    public String getcTlfTrabajador1() {
        return cTlfTrabajador1;
    }

    public void setcTlfTrabajador1(String cTlfTrabajador1) {
        this.cTlfTrabajador1 = cTlfTrabajador1;
    }

    public String getcTlfTrabajador2() {
        return cTlfTrabajador2;
    }

    public void setcTlfTrabajador2(String cTlfTrabajador2) {
        this.cTlfTrabajador2 = cTlfTrabajador2;
    }

    public String getcCargo() {
        return cCargo;
    }

    public void setcCargo(String cCargo) {
        this.cCargo = cCargo;
    }

    public String getcUsuario() {
        return cUsuario;
    }

    public void setcUsuario(String cUsuario) {
        this.cUsuario = cUsuario;
    }

    public String getcPassword() {
        return cPassword;
    }

    public void setcPassword(String cPassword) {
        this.cPassword = cPassword;
    }

    public String getfUltimoAcceso() {
        return fUltimoAcceso;
    }

    public void setfUltimoAcceso(String fUltimoAcceso) {
        this.fUltimoAcceso = fUltimoAcceso;
    }

    public int getnFlgEstado() {
        return nFlgEstado;
    }

    public void setnFlgEstado(int nFlgEstado) {
        this.nFlgEstado = nFlgEstado;
    }

    public int getiCodCategoria() {
        return iCodCategoria;
    }

    public void setiCodCategoria(int iCodCategoria) {
        this.iCodCategoria = iCodCategoria;
    }

    public String getcNomArchivoFoto() {
        return cNomArchivoFoto;
    }

    public void setcNomArchivoFoto(String cNomArchivoFoto) {
        this.cNomArchivoFoto = cNomArchivoFoto;
    }


}
