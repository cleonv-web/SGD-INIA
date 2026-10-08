package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;
import java.util.Date;




public class RecepcionExterna implements Serializable{
	
	private static final long serialVersionUID = 1L;
	private String vrucentrem;
	private String vrucentrec;
	private String vuniorgrem;
	private String ctipdociderem;
	private String vnumdociderem; 
    private String vnumregstd;
	private String vanioregstd;
	private String vuniorgstd;
	private Date dfecregstd;
	private String vusuregstd;
	private byte[] bcarstd;
	private String vcuo;
	private String vcuoref;
	private String vobs;
	private String cflgest;
	private String dfecreg;
	private String vusumod;
	
	private DocumentoExterno documentoExterno;
	
	public String getVrucentrec() {
		return vrucentrec;
	}
	public void setVrucentrec(String vrucentrec) {
		this.vrucentrec = vrucentrec;
	}
	public DocumentoExterno getDocumentoExterno() {
		return documentoExterno;
	}
	public void setDocumentoExterno(DocumentoExterno documentoExterno) {
		this.documentoExterno = documentoExterno;
	}
	
	public String getVrucentrem() {
		return vrucentrem;
	}
	public void setVrucentrem(String vrucentrem) {
		this.vrucentrem = vrucentrem;
	}
	
	public String getVuniorgrem() {
		return vuniorgrem;
	}
	public void setVuniorgrem(String vuniorgrem) {
		this.vuniorgrem = vuniorgrem;
	}
	public String getCtipdociderem() {
		return ctipdociderem;
	}
	public void setCtipdociderem(String ctipdociderem) {
		this.ctipdociderem = ctipdociderem;
	}
	public String getVnumdociderem() {
		return vnumdociderem;
	}
	public void setVnumdociderem(String vnumdociderem) {
		this.vnumdociderem = vnumdociderem;
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
	public byte[] getBcarstd() {
		return bcarstd;
	}
	public void setBcarstd(byte[] bcarstd) {
		this.bcarstd = bcarstd;
	}
	public String getVusuregstd() {
		return vusuregstd;
	}
	public void setVusuregstd(String vusuregstd) {
		this.vusuregstd = vusuregstd;
	}
	public String getVcuo() {
		return vcuo;
	}
	public void setVcuo(String vcuo) {
		this.vcuo = vcuo;
	}
	public String getVcuoref() {
		return vcuoref;
	}
	public void setVcuoref(String vcuoref) {
		this.vcuoref = vcuoref;
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
	public String getDfecreg() {
		return dfecreg;
	}
	public void setDfecreg(String dfecreg) {
		this.dfecreg = dfecreg;
	}
	public String getVusumod() {
		return vusumod;
	}
	public void setVusumod(String vusumod) {
		this.vusumod = vusumod;
	}
	
	
	
    
}
