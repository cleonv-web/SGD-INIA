/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.autentica.model;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class Usuario implements Serializable {
  private String nuDni;
  private String coUsuario;
  private String dePrenombres;
  private String deApellidoPaterno;
  private String deApellidoMaterno;
  private String cempCodemp;
  private Integer tiIdentificacion;
  private String dePassword;
  private String dePasswordNuevo;
  private String inicioId;
  private String coDep;
  private String coLocal;
  private String ipPC;
  private String nombrePC;
  private String usuPc;
  private String sessionId;
  private String inAdmin;
  private String deDep;
  private String inClave;//indicador de clave NF:clave no es fuerte, EXP:clave expiro,NE: no expira aun, expiro >0 DIAS DE EXPRIRACION
  private Date feModClave;
  private Date feActual;
  private String esUsuario;
  private int diasAntesExpiraClave;
  private Date dFecMod; //fecha de actualizacion del registro
  private Date fullFechaActual;
  private int contIntentos=0;
  private String captcha;
  private String inAccesoLocal;
  private String inAD; 
  
  
    public int getContIntentos() {
        return contIntentos;
    }

    public void setContIntentos(int contIntentos) {
        this.contIntentos = contIntentos;
    }
  private List<UsuarioAcceso> usuarioAccesos;

  public Usuario() {
      this.inicioId = "login";
  }

  public Usuario(String nuDni, String dePrenombres, String deApellidoMaterno, String deApellidoPaterno, String cempCodemp, Integer tiIdentificacion) {
        this.nuDni = nuDni;
        this.dePrenombres = dePrenombres;
        this.deApellidoMaterno = deApellidoMaterno;
        this.deApellidoPaterno = deApellidoPaterno;
        this.cempCodemp = cempCodemp;
        this.tiIdentificacion = tiIdentificacion;
        this.inicioId = "login";
  }

    public Date getFullFechaActual() {
        return fullFechaActual;
    }

    public void setFullFechaActual(Date fullFechaActual) {
        this.fullFechaActual = fullFechaActual;
    }

    public Date getdFecMod() {
        return dFecMod;
    }

    public void setdFecMod(Date dFecMod) {
        this.dFecMod = dFecMod;
    }

    public int getDiasAntesExpiraClave() {
        return diasAntesExpiraClave;
    }

    public void setDiasAntesExpiraClave(int diasAntesExpiraClave) {
        this.diasAntesExpiraClave = diasAntesExpiraClave;
    }

    public String getInClave() {
        return inClave;
    }

    public void setInClave(String inClave) {
        this.inClave = inClave;
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

    public String getEsUsuario() {
        return esUsuario;
    }

    public void setEsUsuario(String esUsuario) {
        this.esUsuario = esUsuario;
    }

    public String getDeDep() {
        return deDep;
    }

    public void setDeDep(String deDep) {
        this.deDep = deDep;
    }

    public String getNuDni() {
        return nuDni;
    }

    public void setNuDni(String nuDni) {
        this.nuDni = nuDni;
    }

    public String getDePrenombres() {
        return dePrenombres;
    }

    public void setDePrenombres(String dePrenombres) {
        this.dePrenombres = dePrenombres;
    }

    public String getDeApellidoPaterno() {
        return deApellidoPaterno;
    }

    public void setDeApellidoPaterno(String deApellidoPaterno) {
        this.deApellidoPaterno = deApellidoPaterno;
    }

    public String getDeApellidoMaterno() {
        return deApellidoMaterno;
    }

    public void setDeApellidoMaterno(String deApellidoMaterno) {
        this.deApellidoMaterno = deApellidoMaterno;
    }

    public String getCempCodemp() {
        return cempCodemp;
    }

    public void setCempCodemp(String cempCodemp) {
        this.cempCodemp = cempCodemp;
    }

    public Integer getTiIdentificacion() {
        return tiIdentificacion;
    }

    public void setTiIdentificacion(Integer tiIdentificacion) {
        this.tiIdentificacion = tiIdentificacion;
    }

    public String getDePassword() {
        return dePassword;
    }

    public void setDePassword(String dePassword) {
        this.dePassword = dePassword;
    }

    public String getDePasswordNuevo() {
        return dePasswordNuevo;
    }

    public void setDePasswordNuevo(String dePasswordNuevo) {
        this.dePasswordNuevo = dePasswordNuevo;
    }


    public List<UsuarioAcceso> getUsuarioAccesos() {
        return usuarioAccesos;
    }

    public void setUsuarioAccesos(List<UsuarioAcceso> usuarioAccesos) {
        this.usuarioAccesos = usuarioAccesos;
    }

    public String getInicioId() {
        return inicioId;
    }

    public void setInicioId(String inicioId) {
        this.inicioId = inicioId;
    }

	public String getCoDep() {
		return coDep;
	}

	public void setCoDep(String coDep) {
		this.coDep = coDep;
	}

	public String getCoLocal() {
		return coLocal;
	}

	public void setCoLocal(String coLocal) {
		this.coLocal = coLocal;
	}

	public String getCoUsuario() {
		return coUsuario;
	}

	public void setCoUsuario(String coUsuario) {
		this.coUsuario = coUsuario;
	}

    public String getDeFullName() {
        return this.getDeApellidoPaterno() + " " + this.getDeApellidoMaterno() + " " + this.getDePrenombres();
    }

    public String getIpPC() {
        return ipPC;
    }

    public void setIpPC(String ipPC) {
        this.ipPC = ipPC;
    }

    public String getNombrePC() {
        return nombrePC;
    }

    public void setNombrePC(String nombrePC) {
        this.nombrePC = nombrePC;
    }

    public String getUsuPc() {
        return usuPc;
    }

    public void setUsuPc(String usuPc) {
        this.usuPc = usuPc;
    }

    public String getSessionId() {
        return sessionId;
    }

    public void setSessionId(String sessionId) {
        this.sessionId = sessionId;
    }

    public String getInAdmin() {
        return inAdmin;
    }

    public void setInAdmin(String inAdmin) {
        this.inAdmin = inAdmin;
    }

        public String getCaptcha() {
        return captcha;
    }
    public void setCaptcha(String captcha) {
        this.captcha = captcha;
    }

    /**
     * @return the inAccesoLocal
     */
    public String getInAccesoLocal() {
        return inAccesoLocal;
    }

    /**
     * @param inAccesoLocal the inAccesoLocal to set
     */
    public void setInAccesoLocal(String inAccesoLocal) {
        this.inAccesoLocal = inAccesoLocal;
    }

    /**
     * @return the inAD
     */
    public String getInAD() {
        return inAD;
    }

    /**
     * @param inAD the inAD to set
     */
    public void setInAD(String inAD) {
        this.inAD = inAD;
    }
    
}
