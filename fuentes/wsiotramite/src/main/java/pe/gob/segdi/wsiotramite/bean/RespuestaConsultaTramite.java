package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RespuestaConsultaTramite", propOrder = {
		"vcodres",
		"vdesres",
		"vcuo",
		"vcuoref",
		"vnumregstd",
		"vanioregstd",
		"vuniorgstd",
		"dfecregstd",
		"vusuregstd",
		"bcarstd",
		"vobs",
		"cflgest"
})
public class RespuestaConsultaTramite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vcodres;
	private String vdesres;
	private String vcuo;
	private String vcuoref;
	private String vnumregstd;
	private String vanioregstd;
	private String vuniorgstd;
	private Date dfecregstd;
	private String vusuregstd;
	private byte[] bcarstd;
	private String vobs;
	private String cflgest;
	
	public String getVcuoref() {
		return vcuoref;
	}
	public void setVcuoref(String vcuoref) {
		this.vcuoref = vcuoref;
	}
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
	public String getVcuo() {
		return vcuo;
	}
	public void setVcuo(String vcuo) {
		this.vcuo = vcuo;
	}
	public String getVnumregstd() {
		return vnumregstd;
	}
	public void setVnumregstd(String vnumregstd) {
		this.vnumregstd = vnumregstd;
	}
	public String getVanioregstd() {
		return vanioregstd;
	}
	public void setVanioregstd(String vanioregstd) {
		this.vanioregstd = vanioregstd;
	}
	public Date getDfecregstd() {
		return dfecregstd;
	}
	public void setDfecregstd(Date dfecregstd) {
		this.dfecregstd = dfecregstd;
	}
	public String getVuniorgstd() {
		return vuniorgstd;
	}
	public void setVuniorgstd(String vuniorgstd) {
		this.vuniorgstd = vuniorgstd;
	}
	
	public String getVusuregstd() {
		return vusuregstd;
	}
	public void setVusuregstd(String vusuregstd) {
		this.vusuregstd = vusuregstd;
	}
	public byte[] getBcarstd() {
		return bcarstd;
	}
	public void setBcarstd(byte[] bcarstd) {
		this.bcarstd = bcarstd;
	}
	public String getVobs() {
		return vobs;
	}
	public void setVobs(String vobs) {
		this.vobs = vobs;
	}
	public String getCflgest() {
		return cflgest;
	}
	public void setCflgest(String cflgest) {
		this.cflgest = cflgest;
	}
	

	
	
}
