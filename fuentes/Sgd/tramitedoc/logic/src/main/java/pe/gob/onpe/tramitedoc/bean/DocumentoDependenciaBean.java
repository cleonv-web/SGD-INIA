/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class DocumentoDependenciaBean {
    private String coDep;
    private String coTipDoc;
    private String esEli;
    private String coUseCre;
    private String feUseCre;
    private String coUseMod;
    private String feUseMod;
    private String esOblCarga;
    private String esOblFirma;
    private String inGeneOfic;
    private boolean bEsOblFirma;
    private boolean bInGeneOfic;
    private String tiDescrip;
    private String coTipDocAnt;

    public DocumentoDependenciaBean() {
    }

    public String getCoTipDocAnt() {
        return coTipDocAnt;
    }

    public void setCoTipDocAnt(String coTipDocAnt) {
        this.coTipDocAnt = coTipDocAnt;
    }

    public boolean isbInGeneOfic() {
        return bInGeneOfic;
    }

    public void setbInGeneOfic(boolean bInGeneOfic) {
        this.bInGeneOfic = bInGeneOfic;
    }

    public boolean isbEsOblFirma() {
        return bEsOblFirma;
    }

    public void setbEsOblFirma(boolean bEsOblFirma) {
        this.bEsOblFirma = bEsOblFirma;
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getCoTipDoc() {
        return coTipDoc;
    }

    public void setCoTipDoc(String coTipDoc) {
        this.coTipDoc = coTipDoc;
    }

    public String getEsEli() {
        return esEli;
    }

    public void setEsEli(String esEli) {
        this.esEli = esEli;
    }

    public String getCoUseCre() {
        return coUseCre;
    }

    public void setCoUseCre(String coUseCre) {
        this.coUseCre = coUseCre;
    }

    public String getFeUseCre() {
        return feUseCre;
    }

    public void setFeUseCre(String feUseCre) {
        this.feUseCre = feUseCre;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public String getFeUseMod() {
        return feUseMod;
    }

    public void setFeUseMod(String feUseMod) {
        this.feUseMod = feUseMod;
    }

    public String getEsOblCarga() {
        return esOblCarga;
    }

    public void setEsOblCarga(String esOblCarga) {
        this.esOblCarga = esOblCarga;
    }

    public String getEsOblFirma() {
        return esOblFirma;
    }

    public void setEsOblFirma(String esOblFirma) {
        this.esOblFirma = esOblFirma;
    }

    public String getInGeneOfic() {
        return inGeneOfic;
    }

    public void setInGeneOfic(String inGeneOfic) {
        this.inGeneOfic = inGeneOfic;
    }

    public String getTiDescrip() {
        return tiDescrip;
    }

    public void setTiDescrip(String tiDescrip) {
        this.tiDescrip = tiDescrip;
    }
    
}
