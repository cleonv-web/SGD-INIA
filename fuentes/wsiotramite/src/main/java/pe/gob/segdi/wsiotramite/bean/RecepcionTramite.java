package pe.gob.segdi.wsiotramite.bean;

import java.util.Date;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;





@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "RecepcionTramite", propOrder = {
		"vrucentrem",
		"vrucentrec",
		"vnomentemi",
		"vuniorgrem",
		"vcuo",
		"vcuoref",
		"ccodtipdoc",
		"vnumdoc",
		"dfecdoc",
		"vuniorgdst",
		"vnomdst",
		"vnomcardst",
		"vasu",
		//"cindtup",
		"snumanx",
		"snumfol",
		"vurldocanx",
		"bpdfdoc",
		"vnomdoc",
		"lstanexos",
		"ctipdociderem",
		"vnumdociderem"
})
public class RecepcionTramite {
	private String vrucentrem;
	private String vrucentrec;
	private String vnomentemi;
	private String vuniorgrem;
	private String vcuo;
	private String vcuoref;
	private String ccodtipdoc;
    private String vnumdoc;
    private Date dfecdoc;
    private String vuniorgdst;
    //private String cindtup;
    private String vnomdst;
    private String vnomcardst;
    private String vasu;
    private int snumanx;
    private String vurldocanx;
    private byte[] bpdfdoc;
    private String vnomdoc;
    private int snumfol;
    private List<DocumentoAnexo> lstanexos;
    private String ctipdociderem;
	private String vnumdociderem; 
    
    
	
	public String getVrucentrec() {
		return vrucentrec;
	}
	public void setVrucentrec(String vrucentrec) {
		this.vrucentrec = vrucentrec;
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
	public int getSnumanx() {
		return snumanx;
	}
	public void setSnumanx(int snumanx) {
		this.snumanx = snumanx;
	}
	public int getSnumfol() {
		return snumfol;
	}
	public void setSnumfol(int snumfol) {
		this.snumfol = snumfol;
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
	public String getVnomentemi() {
		return vnomentemi;
	}
	public void setVnomentemi(String vnomentemi) {
		this.vnomentemi = vnomentemi;
	}
	
	public String getCcodtipdoc() {
		return ccodtipdoc;
	}
	public void setCcodtipdoc(String ccodtipdoc) {
		this.ccodtipdoc = ccodtipdoc;
	}
	
	public String getVnumdoc() {
		return vnumdoc;
	}
	public void setVnumdoc(String vnumdoc) {
		this.vnumdoc = vnumdoc;
	}
	public Date getDfecdoc() {
		return dfecdoc;
	}
	public void setDfecdoc(Date dfecdoc) {
		this.dfecdoc = dfecdoc;
	}
	public String getVuniorgdst() {
		return vuniorgdst;
	}
	public void setVuniorgdst(String vuniorgdst) {
		this.vuniorgdst = vuniorgdst;
	}
	public String getVnomdst() {
		return vnomdst;
	}
	public void setVnomdst(String vnomdst) {
		this.vnomdst = vnomdst;
	}
	public String getVnomcardst() {
		return vnomcardst;
	}
	public void setVnomcardst(String vnomcardst) {
		this.vnomcardst = vnomcardst;
	}
	public String getVasu() {
		return vasu;
	}
	public void setVasu(String vasu) {
		this.vasu = vasu;
	}
	public byte[] getBpdfdoc() {
		return bpdfdoc;
	}
	public void setBpdfdoc(byte[] bpdfdoc) {
		this.bpdfdoc = bpdfdoc;
	}
	public String getVnomdoc() {
		return vnomdoc;
	}
	public void setVnomdoc(String vnomdoc) {
		this.vnomdoc = vnomdoc;
	}
	public List<DocumentoAnexo> getLstanexos() {
		return lstanexos;
	}
	public void setLstanexos(List<DocumentoAnexo> lstanexos) {
		this.lstanexos = lstanexos;
	}
	public String getVcuo() {
		return vcuo;
	}
	public void setVcuo(String vcuo) {
		this.vcuo = vcuo;
	}
	public String getVurldocanx() {
		return vurldocanx;
	}
	public void setVurldocanx(String vurldocanx) {
		this.vurldocanx = vurldocanx;
	}
	public String getVcuoref() {
		return vcuoref;
	}
	public void setVcuoref(String vcuoref) {
		this.vcuoref = vcuoref;
	}
	
    
   
	
	
    
}
