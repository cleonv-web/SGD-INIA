package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;
import java.util.Date;

public class DocumentoAnexo implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int siddocext		;
	private int siddocanx		;
	private String vnomdoc   ;
	private Date dfecreg;
	
	public int getSiddocext() {
		return siddocext;
	}
	public void setSiddocext(int siddocext) {
		this.siddocext = siddocext;
	}
	public int getSiddocanx() {
		return siddocanx;
	}
	public void setSiddocanx(int siddocanx) {
		this.siddocanx = siddocanx;
	}
	
	public String getVnomdoc() {
		return vnomdoc;
	}
	public void setVnomdoc(String vnomdoc) {
		this.vnomdoc = vnomdoc;
	}
	public Date getDfecreg() {
		return dfecreg;
	}
	public void setDfecreg(Date dfecreg) {
		this.dfecreg = dfecreg;
	}
	
	
}
