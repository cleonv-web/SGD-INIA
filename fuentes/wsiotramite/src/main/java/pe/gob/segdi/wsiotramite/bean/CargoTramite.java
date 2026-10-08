package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;
import java.util.Date;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "CargoTramite", propOrder = {
		"vcuo",
		"vcuoref",
		"vnumregstd",
		"vanioregstd",
		"dfecregstd",
		"vuniorgstd",
		"vusuregstd",
		"bcarstd",
		"vobs",
		"cflgest",
		"vdesanxstdrec"
		
})
public class CargoTramite implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private String vcuo;
	private String vcuoref;
	private String vnumregstd;
	private String vanioregstd;
	private Date dfecregstd;
	private String vuniorgstd;
	private String vusuregstd;
	private byte[] bcarstd;
	private String vobs;
	private String cflgest;
	private String vdesanxstdrec;
	
	
	public String getVdesanxstdrec() {
		return vdesanxstdrec;
	}
	public void setVdesanxstdrec(String vdesanxstdrec) {
		this.vdesanxstdrec = vdesanxstdrec;
	}
	public String getVcuoref() {
		return vcuoref;
	}
	public void setVcuoref(String vcuoref) {
		this.vcuoref = vcuoref;
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
