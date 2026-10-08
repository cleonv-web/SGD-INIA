package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;

public class DocumentoTramiteAnexo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String vcodres;
	private String vdesres;
	private String vnomdoc   ;
	private byte[] bdocanx;
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
	public String getVnomdoc() {
		return vnomdoc;
	}
	public void setVnomdoc(String vnomdoc) {
		this.vnomdoc = vnomdoc;
	}
	public byte[] getBdocanx() {
		return bdocanx;
	}
	public void setBdocanx(byte[] bdocanx) {
		this.bdocanx = bdocanx;
	}
	
	
}
