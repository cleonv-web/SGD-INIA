package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

public class ConsultaDocumentoRecepcion implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private String iCodOficina;
    private String cNomOficina;//PHP
    private int iCodTrabajadorDerivar; //PHP
    private String iCodTrabajador; //PHP
    private String iCodRemitente; //PHP

    private int siddocext;
    private int sidrecext;
    private String vrucentrem;
    private String vuniorgrem;
    private String ctipdociderem;
    private String vnumdociderem;
    private String vnumregstd;
    private String vanioregstd;
    private Date dfecregstd;
    private Date dfecregstd0;
    private String vuniorgstd;
    private byte[] bcarstd;
    private String vusuregstd;
    private String vcuo;
    private String vcuoref;
    private String vobs;
    private String cflgest;
    private String dfecreg;
    private String vusumod;

    private String cNombresTrabajador;
    private String vnomentemi;
    private String ccodtipdoc;
    private String vuniorgdst;
    private String vnumdoc;
    private Date dfecdoc;
    private String vnomdst;
    private String vnomcardst;
    private String vasu;
    private String cindtup;
    private String vnomdoc;
    private byte[] bpdfdoc;
    private Integer snumanx;
    private String vurldocanx;
    private int snumfol;
    private String cflgobs;
    private String cflgenvcar;
    private String ccoduniorgstd;
    private List<IODocumentoAnexo> lstDocAnexo;


    public String getCcoduniorgstd() {
        return ccoduniorgstd;
    }

    public void setCcoduniorgstd(String ccoduniorgstd) {
        this.ccoduniorgstd = ccoduniorgstd;
    }

    public String getVurldocanx() {
        return vurldocanx;
    }

    public void setVurldocanx(String vurldocanx) {
        this.vurldocanx = vurldocanx;
    }

    public String getiCodOficina() {
        return iCodOficina;
    }

    public void setiCodOficina(String iCodOficina) {
        this.iCodOficina = iCodOficina;
    }

    public String getcNomOficina() {
        return cNomOficina;
    }

    public void setcNomOficina(String cNomOficina) {
        this.cNomOficina = cNomOficina;
    }

    public int getiCodTrabajadorDerivar() {
        return iCodTrabajadorDerivar;
    }

    public void setiCodTrabajadorDerivar(int iCodTrabajadorDerivar) {
        this.iCodTrabajadorDerivar = iCodTrabajadorDerivar;
    }

    public String getiCodTrabajador() {
        return iCodTrabajador;
    }

    public void setiCodTrabajador(String iCodTrabajador) {
        this.iCodTrabajador = iCodTrabajador;
    }

    public String getiCodRemitente() {
        return iCodRemitente;
    }

    public void setiCodRemitente(String iCodRemitente) {
        this.iCodRemitente = iCodRemitente;
    }

    public int getSiddocext() {
        return siddocext;
    }

    public void setSiddocext(int siddocext) {
        this.siddocext = siddocext;
    }

    public int getSidrecext() {
        return sidrecext;
    }

    public void setSidrecext(int sidrecext) {
        this.sidrecext = sidrecext;
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

    public Date getDfecregstd0() {
        return dfecregstd0;
    }

    public void setDfecregstd0(Date dfecregstd0) {
        this.dfecregstd0 = dfecregstd0;
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

    public String getVuniorgdst() {
        return vuniorgdst;
    }

    public void setVuniorgdst(String vuniorgdst) {
        this.vuniorgdst = vuniorgdst;
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

    public String getCindtup() {
        return cindtup;
    }

    public void setCindtup(String cindtup) {
        this.cindtup = cindtup;
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

    public Integer getSnumanx() {
        return snumanx;
    }

    public void setSnumanx(Integer snumanx) {
        this.snumanx = snumanx;
    }

    public int getSnumfol() {
        return snumfol;
    }

    public void setSnumfol(int snumfol) {
        this.snumfol = snumfol;
    }

    public String getCflgobs() {
        return cflgobs;
    }

    public void setCflgobs(String cflgobs) {
        this.cflgobs = cflgobs;
    }

    public String getCflgenvcar() {
        return cflgenvcar;
    }

    public void setCflgenvcar(String cflgenvcar) {
        this.cflgenvcar = cflgenvcar;
    }

    public List<IODocumentoAnexo> getLstDocAnexo() {
        return lstDocAnexo;
    }

    public void setLstDocAnexo(List<IODocumentoAnexo> lstDocAnexo) {
        this.lstDocAnexo = lstDocAnexo;
    }

    public String getcNombresTrabajador() {
        return cNombresTrabajador;
    }

    public void setcNombresTrabajador(String cNombresTrabajador) {
        this.cNombresTrabajador = cNombresTrabajador;
    }


}
