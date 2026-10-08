package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;
import java.util.Date;

public class DocumentoPrincipal implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int siddocext	;
	private int siddocpri	;
	private String vnomdoc   ;
	private byte[] bpdfdoc   ;
	private String ccodest      ;
	private Date dfecreg      ;
	
	public int getSiddocext() {
		return siddocext;
	}
	public void setSiddocext(int siddocext) {
		this.siddocext = siddocext;
	}
	public int getSiddocpri() {
		return siddocpri;
	}
	public void setSiddocpri(int siddocpri) {
		this.siddocpri = siddocpri;
	}
	
	public String getVnomdoc() {
		return vnomdoc;
	}
	public void setVnomdoc(String vnomdoc) {
		this.vnomdoc = vnomdoc;
	}
	public byte[] getBpdfdoc() {
		return bpdfdoc;
	}
	public void setBpdfdoc(byte[] bpdfdoc) {
		this.bpdfdoc = bpdfdoc;
	}
	public String getCcodest() {
		return ccodest;
	}
	public void setCcodest(String ccodest) {
		this.ccodest = ccodest;
	}
	public Date getDfecreg() {
		return dfecreg;
	}
	public void setDfecreg(Date dfecreg) {
		this.dfecreg = dfecreg;
	}


}
