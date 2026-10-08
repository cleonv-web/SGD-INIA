package pe.gob.onpe.tramitedoc.bean;
/*
 * Creado por Wiliams 
 */

import java.io.Serializable;

public class UsuarioConfigBean implements Serializable {

    private String coUsuario;
    private String cempCodemp;
    private String inEsAdmin;
    private String deDirEmi;
    private String deRutaAnexo;
    private String dePiePagina;
    private String coDep;
    private String deDep;
    private String deSiglasDep;
    private String coLocal;
    private String deLocal;
    private String tiAcceso;
    private String tiConsulta;
    private String inCorreo;
    private String inTipoDoc;
    private String deCorreo;
    private String tipoCorreo;
    private String inFirma;
    private String inCargaDocMesaPartes;
    private String inObsDocumento;
    private String inReviDocumento;
    private String inMesaPartes;
    private String inNumeroMp ;
    private String swFirma ;
    private String nameImgPortadaSgd;
    private int diasExpiracionClave;
    private String coDepMp;
    private String tiAccesoMp;
    private String tiConsultaMp;
    private int nroPestanna;
    private String inEmailDeriv;
    
    
    public UsuarioConfigBean() {
    }

    public int getNroPestanna() {
        return nroPestanna;
    }

    public void setNroPestanna(int nroPestanna) {
        this.nroPestanna = nroPestanna;
    }

    public String getTiAccesoMp() {
        return tiAccesoMp;
    }

    public void setTiAccesoMp(String tiAccesoMp) {
        this.tiAccesoMp = tiAccesoMp;
    }

    public String getTiConsultaMp() {
        return tiConsultaMp;
    }

    public void setTiConsultaMp(String tiConsultaMp) {
        this.tiConsultaMp = tiConsultaMp;
    }

    public String getCoDepMp() {
        return coDepMp;
    }

    public void setCoDepMp(String coDepMp) {
        this.coDepMp = coDepMp;
    }

    public int getDiasExpiracionClave() {
        return diasExpiracionClave;
    }

    public void setDiasExpiracionClave(int diasExpiracionClave) {
        this.diasExpiracionClave = diasExpiracionClave;
    }

    public String getCoUsuario() {
        return coUsuario;
    }

    public String getNameImgPortadaSgd() {
        return nameImgPortadaSgd;
    }

    public void setNameImgPortadaSgd(String nameImgPortadaSgd) {
        this.nameImgPortadaSgd = nameImgPortadaSgd;
    }

    public void setCoUsuario(String coUsuario) {
        this.coUsuario = coUsuario;
    }

    public String getCempCodemp() {
        return cempCodemp;
    }

    public void setCempCodemp(String cempCodemp) {
        this.cempCodemp = cempCodemp;
    }

    public String getInEsAdmin() {
        return inEsAdmin;
    }

    public void setInEsAdmin(String inEsAdmin) {
        this.inEsAdmin = inEsAdmin;
    }

    public String getDeRutaAnexo() {
        return deRutaAnexo;
    }

    public void setDeRutaAnexo(String deRutaAnexo) {
        this.deRutaAnexo = deRutaAnexo;
    }

    public String getDePiePagina() {
        return dePiePagina;
    }

    public void setDePiePagina(String dePiePagina) {
        this.dePiePagina = dePiePagina;
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getDeDep() {
        return deDep;
    }

    public void setDeDep(String deDep) {
        this.deDep = deDep;
    }

    public String getCoLocal() {
        return coLocal;
    }

    public void setCoLocal(String coLocal) {
        this.coLocal = coLocal;
    }

    public String getDeLocal() {
        return deLocal;
    }

    public void setDeLocal(String deLocal) {
        this.deLocal = deLocal;
    }

    public String getTiAcceso() {
        return tiAcceso;
    }

    public void setTiAcceso(String tiAcceso) {
        this.tiAcceso = tiAcceso;
    }

    public String getTiConsulta() {
        return tiConsulta;
    }

    public void setTiConsulta(String tiConsulta) {
        this.tiConsulta = tiConsulta;
    }

    public String getInCorreo() {
        return inCorreo;
    }

    public void setInCorreo(String inCorreo) {
        this.inCorreo = inCorreo;
    }

    public String getDeCorreo() {
        return deCorreo;
    }

    public void setDeCorreo(String deCorreo) {
        this.deCorreo = deCorreo;
    }

    public String getTipoCorreo() {
        return tipoCorreo;
    }

    public void setTipoCorreo(String tipoCorreo) {
        this.tipoCorreo = tipoCorreo;
    }

    public String getInFirma() {
        return inFirma;
    }

    public void setInFirma(String inFirma) {
        this.inFirma = inFirma;
    }

    public String getInCargaDocMesaPartes() {
        return inCargaDocMesaPartes;
    }

    public void setInCargaDocMesaPartes(String inCargaDocMesaPartes) {
        this.inCargaDocMesaPartes = inCargaDocMesaPartes;
    }

    public String getInObsDocumento() {
        return inObsDocumento;
    }

    public void setInObsDocumento(String inObsDocumento) {
        this.inObsDocumento = inObsDocumento;
    }

    public String getInReviDocumento() {
        return inReviDocumento;
    }

    public void setInReviDocumento(String inReviDocumento) {
        this.inReviDocumento = inReviDocumento;
    }

    public String getInTipoDoc() {
        return inTipoDoc;
    }

    public void setInTipoDoc(String inTipoDoc) {
        this.inTipoDoc = inTipoDoc;
    }

    public String getDeDirEmi() {
        return deDirEmi;
    }

    public void setDeDirEmi(String deDirEmi) {
        this.deDirEmi = deDirEmi;
    }

    public String getDeSiglasDep() {
        return deSiglasDep;
    }

    public void setDeSiglasDep(String deSiglasDep) {
        this.deSiglasDep = deSiglasDep;
    }

    public String getInMesaPartes() {
        return inMesaPartes;
    }

    public void setInMesaPartes(String inMesaPartes) {
        this.inMesaPartes = inMesaPartes;
    }

    public String getInNumeroMp() {
        return inNumeroMp;
    }

    public void setInNumeroMp(String inNumeroMp) {
        this.inNumeroMp = inNumeroMp;
    }

    public String getSwFirma() {
        return swFirma;
    }

    public void setSwFirma(String swFirma) {
        this.swFirma = swFirma;
    }

    /**
     * @return the inEmailDeriv
     */
    public String getInEmailDeriv() {
        return inEmailDeriv;
    }

    /**
     * @param inEmailDeriv the inEmailDeriv to set
     */
    public void setInEmailDeriv(String inEmailDeriv) {
        this.inEmailDeriv = inEmailDeriv;
    }
    
    
    
}
