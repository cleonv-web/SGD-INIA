package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaCargoTramite", propOrder = {
		"vcodres",
		"vdesres"
})
public class RespuestaCargoTramite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vcodres;
	private String vdesres;
	
	public String getVcodres() {
		return vcodres;
	}
	public void setVcodres(String vcodres) {
		this.vcodres = vcodres;
	}
	public String getVdesres() {
		return vdesres;
	}
	public void setVdesres(String vdesres) {
		this.vdesres = vdesres;
	}
	

}
