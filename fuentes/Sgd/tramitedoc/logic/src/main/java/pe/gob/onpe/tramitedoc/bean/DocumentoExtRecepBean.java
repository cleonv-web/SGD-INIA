/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

/**
 *
 * @author ecueva
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentoExtRecepBean {
    private String nuExpediente;
    private String nuEmi;
    private String nuDoc;
    private String nuAnn;
    private String tiEmi;
    private String deTiEmi;
    private String deTipDocAdm;
    private String deOriEmiMp;
    private String deEsDocEmiMp;
    private String deEmpDes;
    private String deAsu;
    private String coEsDocEmiMp;
    private String coDepEmi;
    private String coLocEmi;
    private String coEmpEmi;
    private String deEmpEmi;
    private String nuCorrExp;
    private String feExp;
    private String feExpCorta;
    private String coTipDocAdm;
    private String nuFolios;
    private String nuCorDoc;
    private String nuCorEmi;
    private String coEmpRes;
    private String deDocSig;
    private String deDocSigG;
    private String deEmpRes;
    private String nuAnnExp;
    private String nuSecExp;
    private String nuDni;
    private String deNuDni;
    private String nuRuc;
    private String deNuRuc;
    private String coOtros;
    private String deNomOtros;
    private String deDocOtros;
    private String nuDocOtros;
    private String feVence;
    private String coProceso;
    private String feEmiCorta;
    private String existeDoc;
    private String inNumeroMp;
    private String coUseMod;
    private String nuDiaAte;
    private String deLocEmi;
    private String deDependencia;
    private String idDepartamento;
    private String idProvincia;
    private String idDistrito;
    private String deDireccion;
    private String coTipoExp;
    private String coOriDoc;
    private String coTraDest;
    private String coComision;
    private String coTipoCongresista;
    private String nroDniTramitante;
    private String coTipoInv;
    private String nuDniRes;
    private String deNuDniRes;
    private String deCorreo;
    private String telefono;
    private String nSobre;
    private String anioSobre;
    private String sensible;
    private String notificado;
    private String deCargo;
    private String deObservacion;
    private String reiterativo;
    private String emiResp;    
    private String coOtrosRes;
    private String deNomOtrosRes;
    private String tiRemitente;
    private String deRemitente;
    private String deEmpPro;    
    private String coProcesoExp;
    private String deEsDocEmi;
    private String feExpVenceCorta;
    private String deProcesoExp;
    private String deEmpRec;
    
    private String nuAnexo;
    private String nuCopia;
    private String nuDiasHabiles;

    public String getDeEmpRec() {
        return deEmpRec;
    }

    public void setDeEmpRec(String deEmpRec) {
        this.deEmpRec = deEmpRec;
    }
    
    public String getDeProcesoExp() {
        return deProcesoExp;
    }

    public void setDeProcesoExp(String deProcesoExp) {
        this.deProcesoExp = deProcesoExp;
    }

    public String getFeExpVenceCorta() {
        return feExpVenceCorta;
    }

    public void setFeExpVenceCorta(String feExpVenceCorta) {
        this.feExpVenceCorta = feExpVenceCorta;
    }
    
    public String getDeEsDocEmi() {
        return deEsDocEmi;
    }

    public void setDeEsDocEmi(String deEsDocEmi) {
        this.deEsDocEmi = deEsDocEmi;
    }

    public String getDeEmpPro() {
        return deEmpPro;
    }

    public void setDeEmpPro(String deEmpPro) {
        this.deEmpPro = deEmpPro;
    }

    public String getCoProcesoExp() {
        return coProcesoExp;
    }

    public void setCoProcesoExp(String coProcesoExp) {
        this.coProcesoExp = coProcesoExp;
    }
 
    public DocumentoExtRecepBean() {
    }

    public String getNuExpediente() {
        return nuExpediente;
    }

    public void setNuExpediente(String nuExpediente) {
        this.nuExpediente = nuExpediente;
    }

    public String getNuEmi() {
        return nuEmi;
    }

    public void setNuEmi(String nuEmi) {
        this.nuEmi = nuEmi;
    }

    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }

    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getTiEmi() {
        return tiEmi;
    }

    public void setTiEmi(String tiEmi) {
        this.tiEmi = tiEmi;
    }

    public String getDeTiEmi() {
        return deTiEmi;
    }

    public void setDeTiEmi(String deTiEmi) {
        this.deTiEmi = deTiEmi;
    }

    public String getDeTipDocAdm() {
        return deTipDocAdm;
    }

    public void setDeTipDocAdm(String deTipDocAdm) {
        this.deTipDocAdm = deTipDocAdm;
    }

    public String getDeOriEmiMp() {
        return deOriEmiMp;
    }

    public void setDeOriEmiMp(String deOriEmiMp) {
        this.deOriEmiMp = deOriEmiMp;
    }

    public String getDeEsDocEmiMp() {
        return deEsDocEmiMp;
    }

    public void setDeEsDocEmiMp(String deEsDocEmiMp) {
        this.deEsDocEmiMp = deEsDocEmiMp;
    }

    public String getDeEmpDes() {
        return deEmpDes;
    }

    public void setDeEmpDes(String deEmpDes) {
        this.deEmpDes = deEmpDes;
    }

    public String getDeAsu() {
        return deAsu;
    }

    public void setDeAsu(String deAsu) {
        this.deAsu = deAsu;
    }

    public String getCoEsDocEmiMp() {
        return coEsDocEmiMp;
    }

    public void setCoEsDocEmiMp(String coEsDocEmiMp) {
        this.coEsDocEmiMp = coEsDocEmiMp;
    }

    public String getCoDepEmi() {
        return coDepEmi;
    }

    public void setCoDepEmi(String coDepEmi) {
        this.coDepEmi = coDepEmi;
    }

    public String getCoLocEmi() {
        return coLocEmi;
    }

    public void setCoLocEmi(String coLocEmi) {
        this.coLocEmi = coLocEmi;
    }

    public String getCoEmpEmi() {
        return coEmpEmi;
    }

    public void setCoEmpEmi(String coEmpEmi) {
        this.coEmpEmi = coEmpEmi;
    }

    public String getDeEmpEmi() {
        return deEmpEmi;
    }

    public void setDeEmpEmi(String deEmpEmi) {
        this.deEmpEmi = deEmpEmi;
    }

    public String getNuCorrExp() {
        return nuCorrExp;
    }

    public void setNuCorrExp(String nuCorrExp) {
        this.nuCorrExp = nuCorrExp;
    }

    public String getFeExp() {
        return feExp;
    }

    public void setFeExp(String feExp) {
        this.feExp = feExp;
    }

    public String getFeExpCorta() {
        return feExpCorta;
    }

    public void setFeExpCorta(String feExpCorta) {
        this.feExpCorta = feExpCorta;
    }

    public String getCoTipDocAdm() {
        return coTipDocAdm;
    }

    public void setCoTipDocAdm(String coTipDocAdm) {
        this.coTipDocAdm = coTipDocAdm;
    }

    public String getNuFolios() {
        return nuFolios;
    }

    public void setNuFolios(String nuFolios) {
        this.nuFolios = nuFolios;
    }

    public String getNuCorDoc() {
        return nuCorDoc;
    }

    public void setNuCorDoc(String nuCorDoc) {
        this.nuCorDoc = nuCorDoc;
    }

    public String getNuCorEmi() {
        return nuCorEmi;
    }

    public void setNuCorEmi(String nuCorEmi) {
        this.nuCorEmi = nuCorEmi;
    }

    public String getCoEmpRes() {
        return coEmpRes;
    }

    public void setCoEmpRes(String coEmpRes) {
        this.coEmpRes = coEmpRes;
    }

    public String getDeDocSig() {
        return deDocSig;
    }

    public void setDeDocSig(String deDocSig) {
        this.deDocSig = deDocSig;
    }

    public String getDeDocSigG() {
        return deDocSigG;
    }

    public void setDeDocSigG(String deDocSigG) {
        this.deDocSigG = deDocSigG;
    }

    public String getDeEmpRes() {
        return deEmpRes;
    }

    public void setDeEmpRes(String deEmpRes) {
        this.deEmpRes = deEmpRes;
    }

    public String getNuAnnExp() {
        return nuAnnExp;
    }

    public void setNuAnnExp(String nuAnnExp) {
        this.nuAnnExp = nuAnnExp;
    }

    public String getNuSecExp() {
        return nuSecExp;
    }

    public void setNuSecExp(String nuSecExp) {
        this.nuSecExp = nuSecExp;
    }

    public String getNuDni() {
        return nuDni;
    }

    public void setNuDni(String nuDni) {
        this.nuDni = nuDni;
    }

    public String getDeNuDni() {
        return deNuDni;
    }

    public void setDeNuDni(String deNuDni) {
        this.deNuDni = deNuDni;
    }

    public String getNuRuc() {
        return nuRuc;
    }

    public void setNuRuc(String nuRuc) {
        this.nuRuc = nuRuc;
    }

    public String getDeNuRuc() {
        return deNuRuc;
    }

    public void setDeNuRuc(String deNuRuc) {
        this.deNuRuc = deNuRuc;
    }

    public String getCoOtros() {
        return coOtros;
    }

    public void setCoOtros(String coOtros) {
        this.coOtros = coOtros;
    }

    public String getDeNomOtros() {
        return deNomOtros;
    }

    public void setDeNomOtros(String deNomOtros) {
        this.deNomOtros = deNomOtros;
    }

    public String getDeDocOtros() {
        return deDocOtros;
    }

    public void setDeDocOtros(String deDocOtros) {
        this.deDocOtros = deDocOtros;
    }

    public String getNuDocOtros() {
        return nuDocOtros;
    }

    public void setNuDocOtros(String nuDocOtros) {
        this.nuDocOtros = nuDocOtros;
    }

    public String getFeVence() {
        return feVence;
    }

    public void setFeVence(String feVence) {
        this.feVence = feVence;
    }

    public String getCoProceso() {
        return coProceso;
    }

    public void setCoProceso(String coProceso) {
        this.coProceso = coProceso;
    }

    public String getFeEmiCorta() {
        return feEmiCorta;
    }

    public void setFeEmiCorta(String feEmiCorta) {
        this.feEmiCorta = feEmiCorta;
    }

    public String getExisteDoc() {
        return existeDoc;
    }

    public void setExisteDoc(String existeDoc) {
        this.existeDoc = existeDoc;
    }

    public String getInNumeroMp() {
        return inNumeroMp;
    }

    public void setInNumeroMp(String inNumeroMp) {
        this.inNumeroMp = inNumeroMp;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public String getNuDiaAte() {
        return nuDiaAte;
    }

    public void setNuDiaAte(String nuDiaAte) {
        this.nuDiaAte = nuDiaAte;
    }

    public String getDeLocEmi() {
        return deLocEmi;
    }

    public void setDeLocEmi(String deLocEmi) {
        this.deLocEmi = deLocEmi;
    }

    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }

    public String getIdDepartamento() {
        return idDepartamento;
    }

    public void setIdDepartamento(String idDepartamento) {
        this.idDepartamento = idDepartamento;
    }

    public String getIdProvincia() {
        return idProvincia;
    }

    public void setIdProvincia(String idProvincia) {
        this.idProvincia = idProvincia;
    }

    public String getIdDistrito() {
        return idDistrito;
    }

    public void setIdDistrito(String idDistrito) {
        this.idDistrito = idDistrito;
    }   

    public String getDeDireccion() {
        return deDireccion;
    }

    public void setDeDireccion(String deDireccion) {
        this.deDireccion = deDireccion;
    }

    public String getCoTipoExp() {
        return coTipoExp;
    }

    public void setCoTipoExp(String coTipoExp) {
        this.coTipoExp = coTipoExp;
    }

    public String getCoOriDoc() {
        return coOriDoc;
    }

    public void setCoOriDoc(String coOriDoc) {
        this.coOriDoc = coOriDoc;
    }

    public String getCoTraDest() {
        return coTraDest;
    }

    public void setCoTraDest(String coTraDest) {
        this.coTraDest = coTraDest;
    }

    public String getCoComision() {
        return coComision;
    }

    public void setCoComision(String coComision) {
        this.coComision = coComision;
    }

    public String getCoTipoCongresista() {
        return coTipoCongresista;
    }

    public void setCoTipoCongresista(String coTipoCongresista) {
        this.coTipoCongresista = coTipoCongresista;
    }

    public String getNroDniTramitante() {
        return nroDniTramitante;
    }

    public void setNroDniTramitante(String nroDniTramitante) {
        this.nroDniTramitante = nroDniTramitante;
    }

    public String getCoTipoInv() {
        return coTipoInv;
    }

    public void setCoTipoInv(String coTipoInv) {
        this.coTipoInv = coTipoInv;
    }

    public String getNuDniRes() {
        return nuDniRes;
    }

    public void setNuDniRes(String nuDniRes) {
        this.nuDniRes = nuDniRes;
    }

    public String getDeNuDniRes() {
        return deNuDniRes;
    }

    public void setDeNuDniRes(String deNuDniRes) {
        this.deNuDniRes = deNuDniRes;
    }

    public String getDeCorreo() {
        return deCorreo;
    }

    public void setDeCorreo(String deCorreo) {
        this.deCorreo = deCorreo;
    }

    public String getTelefono() {
        return telefono;
    }

    public void setTelefono(String telefono) {
        this.telefono = telefono;
    }

    public String getnSobre() {
        return nSobre;
    }

    public void setnSobre(String nSobre) {
        this.nSobre = nSobre;
    }

    public String getAnioSobre() {
        return anioSobre;
    }

    public void setAnioSobre(String anioSobre) {
        this.anioSobre = anioSobre;
    }

    

    public String getDeCargo() {
        return deCargo;
    }

    public void setDeCargo(String deCargo) {
        this.deCargo = deCargo;
    }

    public String getDeObservacion() {
        return deObservacion;
    }

    public void setDeObservacion(String deObservacion) {
        this.deObservacion = deObservacion;
    }


    public String getEmiResp() {
        return emiResp;
    }

    public void setEmiResp(String emiResp) {
        this.emiResp = emiResp;
    }

    public String getCoOtrosRes() {
        return coOtrosRes;
    }

    public void setCoOtrosRes(String coOtrosRes) {
        this.coOtrosRes = coOtrosRes;
    }

    public String getDeNomOtrosRes() {
        return deNomOtrosRes;
    }

    public void setDeNomOtrosRes(String deNomOtrosRes) {
        this.deNomOtrosRes = deNomOtrosRes;
    }

    public String getSensible() {
        return sensible;
    }

    public void setSensible(String sensible) {
        this.sensible = sensible;
    }

    public String getNotificado() {
        return notificado;
    }

    public void setNotificado(String notificado) {
        this.notificado = notificado;
    }

    public String getReiterativo() {
        return reiterativo;
    }

    public void setReiterativo(String reiterativo) {
        this.reiterativo = reiterativo;
    } 

    public String getTiRemitente() {
        return tiRemitente;
    }

    public void setTiRemitente(String tiRemitente) {
        this.tiRemitente = tiRemitente;
    }

    public String getDeRemitente() {
        return deRemitente;
    }

    public void setDeRemitente(String deRemitente) {
        this.deRemitente = deRemitente;
    }

    /**
     * @return the nuAnexo
     */
    public String getNuAnexo() {
        return nuAnexo;
    }

    /**
     * @param nuAnexo the nuAnexo to set
     */
    public void setNuAnexo(String nuAnexo) {
        this.nuAnexo = nuAnexo;
    }

    /**
     * @return the nuCopia
     */
    public String getNuCopia() {
        return nuCopia;
    }

    /**
     * @param nuCopia the nuCopia to set
     */
    public void setNuCopia(String nuCopia) {
        this.nuCopia = nuCopia;
    }

    /**
     * @return the nuDiasHabiles
     */
    public String getNuDiasHabiles() {
        return nuDiasHabiles;
    }

    /**
     * @param nuDiasHabiles the nuDiasHabiles to set
     */
    public void setNuDiasHabiles(String nuDiasHabiles) {
        this.nuDiasHabiles = nuDiasHabiles;
    }
    
    
}
