package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.sql.Date;
import java.util.List;

public class IODocumentoExterno implements Serializable {
    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int sidemiext;
    private String vnumdoc;
    private String ccodtipdoc;
    private String vnomentemi;
    private String vrucentdst;
    private int siddocext;
    private String vasu;
    private String vnomcardst;
    private String vnomdst;
    private String vuniorgdst;
    private Date dfecdoc;
    private String vurldocanx;
    private int snumanx;
    private Integer snumfol;
    private String cindtup;
    private IODocumentoPrincipal documentoPrincipal;
    private List<IODocumentoAnexo> lstDocAnexo;

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

    public IODocumentoPrincipal getDocumentoPrincipal() {
        return documentoPrincipal;
    }

    public void setDocumentoPrincipal(IODocumentoPrincipal documentoPrincipal) {
        this.documentoPrincipal = documentoPrincipal;
    }

    public List<IODocumentoAnexo> getLstDocAnexo() {
        return lstDocAnexo;
    }

    public void setLstDocAnexo(List<IODocumentoAnexo> lstDocAnexo) {
        this.lstDocAnexo = lstDocAnexo;
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

    public String getVrucentdst() {
        return vrucentdst;
    }

    public void setVrucentdst(String vrucentdst) {
        this.vrucentdst = vrucentdst;
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

    public String getVnumdoc() {
        return vnumdoc;
    }

    public void setVnumdoc(String vnumdoc) {
        this.vnumdoc = vnumdoc;
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


}
