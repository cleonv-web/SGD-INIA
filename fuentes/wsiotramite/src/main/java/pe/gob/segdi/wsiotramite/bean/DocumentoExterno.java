package pe.gob.segdi.wsiotramite.bean;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class DocumentoExterno implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int siddocext    ;
	private int sidemiext    ;
	private int sidrecext    ;
	private String vnumdoc      ;
	private String ccodtipdoc   ;
	private String vnomentemi      ;
	private String vuniorgdst;
	private String vasu         ;
	private String vnomdst      ;
	private String vnomcardst   ;
	private Date dfecdoc      ;
	private int snumanx      ;
	private Integer snumfol;
	private String cindtup      ;
	private String vurldocanx   ;
	private DocumentoPrincipal documentoPrincipal;
	private List<DocumentoAnexo> lstDocAnexo;
	
	public Integer getSnumfol() {
		return snumfol;
	}
	public void setSnumfol(Integer snumfol) {
		this.snumfol = snumfol;
	}
	public int getSidemiext() {
		return sidemiext;
	}
	public void setSidemiext(int sidemiext) {
		this.sidemiext = sidemiext;
	}
	public int getSidrecext() {
		return sidrecext;
	}
	public void setSidrecext(int sidrecext) {
		this.sidrecext = sidrecext;
	}
	
	public String getVnumdoc() {
		return vnumdoc;
	}
	public void setVnumdoc(String vnumdoc) {
		this.vnumdoc = vnumdoc;
	}
	public String getCcodtipdoc() {
		return ccodtipdoc;
	}
	public void setCcodtipdoc(String ccodtipdoc) {
		this.ccodtipdoc = ccodtipdoc;
	}
	public String getVnomentemi() {
		return vnomentemi;
	}
	public void setVnomentemi(String vnomentemi) {
		this.vnomentemi = vnomentemi;
	}
	public int getSiddocext() {
		return siddocext;
	}
	public void setSiddocext(int siddocext) {
		this.siddocext = siddocext;
	}
	public String getVasu() {
		return vasu;
	}
	public void setVasu(String vasu) {
		this.vasu = vasu;
	}
	public String getVnomcardst() {
		return vnomcardst;
	}
	public void setVnomcardst(String vnomcardst) {
		this.vnomcardst = vnomcardst;
	}
	public String getVnomdst() {
		return vnomdst;
	}
	public void setVnomdst(String vnomdst) {
		this.vnomdst = vnomdst;
	}
	public String getVuniorgdst() {
		return vuniorgdst;
	}
	public void setVuniorgdst(String vuniorgdst) {
		this.vuniorgdst = vuniorgdst;
	}
	public Date getDfecdoc() {
		return dfecdoc;
	}
	public void setDfecdoc(Date dfecdoc) {
		this.dfecdoc = dfecdoc;
	}
	
	public String getVurldocanx() {
		return vurldocanx;
	}
	public void setVurldocanx(String vurldocanx) {
		this.vurldocanx = vurldocanx;
	}
	public int getSnumanx() {
		return snumanx;
	}
	public void setSnumanx(int snumanx) {
		this.snumanx = snumanx;
	}
	public String getCindtup() {
		return cindtup;
	}
	public void setCindtup(String cindtup) {
		this.cindtup = cindtup;
	}
	public DocumentoPrincipal getDocumentoPrincipal() {
		return documentoPrincipal;
	}
	public void setDocumentoPrincipal(DocumentoPrincipal documentoPrincipal) {
		this.documentoPrincipal = documentoPrincipal;
	}
	public List<DocumentoAnexo> getLstDocAnexo() {
		return lstDocAnexo;
	}
	public void setLstDocAnexo(List<DocumentoAnexo> lstDocAnexo) {
		this.lstDocAnexo = lstDocAnexo;
	}
	
	
	
    
    
}
