/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;
import java.util.ArrayList;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
/**
 *
 * @author WCONDORI
 */
@JsonIgnoreProperties(ignoreUnknown = true)
public class DocumentoRecepMensajeriaBean {
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

    private String totalDestino;
    private String totalEnviado;
    private String totalPendiente;
    private String coTipMensajero;
    
    private String numsj;
    private String feregmsj;
    private String cousecre;
    private String deambito;
    private String detipmsj;
    private String reenvmsj;
    private String detipenv;
    private String nusermsj;
    private String ansermsj;
    private String fecenviomsj;
    private String hoenvmsj;
    private String feplamsj;
    private String hoplamsj;
    private String numDes;
    private String codigo;
    private String docEstadoMsj;
    private String fecRecepmp;
    private String fecEnviomsj;
    private String deAmbito;
    private String deTipEnv;
    private String nuSerMsj;
    private String fePlaMsj;
    private String diasVencimiento;
    private String diasEntrega;
    private String diasDevoluvion;
    private String fechaenvioamensajeria;
    private String calculaPenalizacion;
    
    private String in_env_sede_local;
    private String de_obs_msj;

    
    
    private String nu_Acta_Vis1;
    private String nu_Acta_Vis2;
    private String fe_Acta_Vis1;
    private String fe_Acta_Vis2;
    private String es_Acta1_msj;
    private String es_Acta2_msj;
    private String archivo_Acta1;
    private String archivo_Acta2;
    private String archivo_Cargo;
    private String sidEmiExt; //mesa virtual

    
    
    public String getCalculaPenalizacion() {
        return calculaPenalizacion;
    }

    public void setCalculaPenalizacion(String calculaPenalizacion) {
        this.calculaPenalizacion = calculaPenalizacion;
    }
    private ArrayList<DestinatarioDocumentoEmiBean> lstDestinatario;
    
    public ArrayList<DestinatarioDocumentoEmiBean> getLstDestinatario() {
        return lstDestinatario;
    }

    public void setLstDestinatario(ArrayList<DestinatarioDocumentoEmiBean> lstDestinatario) {
        this.lstDestinatario = lstDestinatario;
    }
    
    
    public String getDiasEntrega() {
        return diasEntrega;
    }

    public String getFechaenvioamensajeria() {
        return fechaenvioamensajeria;
    }

    public void setFechaenvioamensajeria(String fechaenvioamensajeria) {
        this.fechaenvioamensajeria = fechaenvioamensajeria;
    }

    public void setDiasEntrega(String diasEntrega) {
        this.diasEntrega = diasEntrega;
    }

    public String getDiasDevoluvion() {
        return diasDevoluvion;
    }

    public void setDiasDevoluvion(String diasDevoluvion) {
        this.diasDevoluvion = diasDevoluvion;
    }
    
    
    
    public String getFecEnviomsj() {
        return fecEnviomsj;
    }

    public void setFecEnviomsj(String fecEnviomsj) {
        this.fecEnviomsj = fecEnviomsj;
    }

    public String getDeAmbito() {
        return deAmbito;
    }

    public void setDeAmbito(String deAmbito) {
        this.deAmbito = deAmbito;
    }

    public String getDeTipEnv() {
        return deTipEnv;
    }

    public void setDeTipEnv(String deTipEnv) {
        this.deTipEnv = deTipEnv;
    }

    public String getNuSerMsj() {
        return nuSerMsj;
    }

    public void setNuSerMsj(String nuSerMsj) {
        this.nuSerMsj = nuSerMsj;
    }

    public String getFePlaMsj() {
        return fePlaMsj;
    }

    public void setFePlaMsj(String fePlaMsj) {
        this.fePlaMsj = fePlaMsj;
    }

    public String getDiasVencimiento() {
        return diasVencimiento;
    }

    public void setDiasVencimiento(String diasVencimiento) {
        this.diasVencimiento = diasVencimiento;
    }
    
    
            
    
    public String getFecRecepmp() {
        return fecRecepmp;
    }

    public void setFecRecepmp(String fecRecepmp) {
        this.fecRecepmp = fecRecepmp;
    }
    
    public String getDocEstadoMsj() {
        return docEstadoMsj;
    }

    public void setDocEstadoMsj(String docEstadoMsj) {
        this.docEstadoMsj = docEstadoMsj;
    }

     
    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    
    public String getNumDes() {
        return numDes;
    }

    public void setNumDes(String numDes) {
        this.numDes = numDes;
    }
    public String getNumsj() {
        return numsj;
    }

    public void setNumsj(String numsj) {
        this.numsj = numsj;
    }

    public String getFeregmsj() {
        return feregmsj;
    }

    public void setFeregmsj(String feregmsj) {
        this.feregmsj = feregmsj;
    }

    public String getCousecre() {
        return cousecre;
    }

    public void setCousecre(String cousecre) {
        this.cousecre = cousecre;
    }

    public String getDeambito() {
        return deambito;
    }

    public void setDeambito(String deambito) {
        this.deambito = deambito;
    }

    public String getDetipmsj() {
        return detipmsj;
    }

    public void setDetipmsj(String detipmsj) {
        this.detipmsj = detipmsj;
    }

    public String getReenvmsj() {
        return reenvmsj;
    }

    public void setReenvmsj(String reenvmsj) {
        this.reenvmsj = reenvmsj;
    }

    public String getDetipenv() {
        return detipenv;
    }

    public void setDetipenv(String detipenv) {
        this.detipenv = detipenv;
    }

    public String getNusermsj() {
        return nusermsj;
    }

    public void setNusermsj(String nusermsj) {
        this.nusermsj = nusermsj;
    }

    public String getAnsermsj() {
        return ansermsj;
    }

    public void setAnsermsj(String ansermsj) {
        this.ansermsj = ansermsj;
    }

    public String getFecenviomsj() {
        return fecenviomsj;
    }

    public void setFecenviomsj(String fecenviomsj) {
        this.fecenviomsj = fecenviomsj;
    }

    public String getHoenvmsj() {
        return hoenvmsj;
    }

    public void setHoenvmsj(String hoenvmsj) {
        this.hoenvmsj = hoenvmsj;
    }

    public String getFeplamsj() {
        return feplamsj;
    }

    public void setFeplamsj(String feplamsj) {
        this.feplamsj = feplamsj;
    }

    public String getHoplamsj() {
        return hoplamsj;
    }

    public void setHoplamsj(String hoplamsj) {
        this.hoplamsj = hoplamsj;
    }

            
            
    public DocumentoRecepMensajeriaBean() {
    }
    public String getCoTipMensajero() {
        return coTipMensajero;
    }

    public void setCoTipMensajero(String coTipMensajero) {
        this.coTipMensajero = coTipMensajero;
    }
    public String getTotalPendiente() {
        return totalPendiente;
    }

    public void setTotalPendiente(String totalPendiente) {
        this.totalPendiente = totalPendiente;
    }
    public String getTotalEnviado() {
        return totalEnviado;
    }

    public void setTotalEnviado(String totalEnviado) {
        this.totalEnviado = totalEnviado;
    }
    
    public String getTotalDestino() {
        return totalDestino;
    }

    public void setTotalDestino(String totalDestino) {
        this.totalDestino = totalDestino;
    }
    
    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }

    public String getDeLocEmi() {
        return deLocEmi;
    }

    public void setDeLocEmi(String deLocEmi) {
        this.deLocEmi = deLocEmi;
    }

    public String getNuDiaAte() {
        return nuDiaAte;
    }

    public void setNuDiaAte(String nuDiaAte) {
        this.nuDiaAte = nuDiaAte;
    }

    public String getExisteDoc() {
        return existeDoc;
    }

    public void setExisteDoc(String existeDoc) {
        this.existeDoc = existeDoc;
    }

    public String getFeEmiCorta() {
        return feEmiCorta;
    }

    public void setFeEmiCorta(String feEmiCorta) {
        this.feEmiCorta = feEmiCorta;
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

    public String getNuAnnExp() {
        return nuAnnExp;
    }

    public void setNuAnnExp(String nuAnnExp) {
        this.nuAnnExp = nuAnnExp;
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

    public String getNuSecExp() {
        return nuSecExp;
    }

    public void setNuSecExp(String nuSecExp) {
        this.nuSecExp = nuSecExp;
    }

    public String getDeEmpRes() {
        return deEmpRes;
    }

    public void setDeEmpRes(String deEmpRes) {
        this.deEmpRes = deEmpRes;
    }

    public String getDeDocSigG() {
        return deDocSigG;
    }

    public void setDeDocSigG(String deDocSigG) {
        this.deDocSigG = deDocSigG;
    }

    public String getDeDocSig() {
        return deDocSig;
    }

    public void setDeDocSig(String deDocSig) {
        this.deDocSig = deDocSig;
    }

    public String getCoEmpRes() {
        return coEmpRes;
    }

    public void setCoEmpRes(String coEmpRes) {
        this.coEmpRes = coEmpRes;
    }

    public String getNuCorEmi() {
        return nuCorEmi;
    }

    public void setNuCorEmi(String nuCorEmi) {
        this.nuCorEmi = nuCorEmi;
    }

    public String getNuCorDoc() {
        return nuCorDoc;
    }

    public void setNuCorDoc(String nuCorDoc) {
        this.nuCorDoc = nuCorDoc;
    }

    public String getCoUseMod() {
        return coUseMod;
    }

    public void setCoUseMod(String coUseMod) {
        this.coUseMod = coUseMod;
    }

    public String getNuFolios() {
        return nuFolios;
    }

    public void setNuFolios(String nuFolios) {
        this.nuFolios = nuFolios;
    }

    public String getCoTipDocAdm() {
        return coTipDocAdm;
    }

    public void setCoTipDocAdm(String coTipDocAdm) {
        this.coTipDocAdm = coTipDocAdm;
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

    public String getCoEsDocEmiMp() {
        return coEsDocEmiMp;
    }

    public void setCoEsDocEmiMp(String coEsDocEmiMp) {
        this.coEsDocEmiMp = coEsDocEmiMp;
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

    public String getInNumeroMp() {
        return inNumeroMp;
    }

    public void setInNumeroMp(String inNumeroMp) {
        this.inNumeroMp = inNumeroMp;
    }

    /**
     * @return the in_env_sede_local
     */
    public String getIn_env_sede_local() {
        return in_env_sede_local;
    }

    /**
     * @param in_env_sede_local the in_env_sede_local to set
     */
    public void setIn_env_sede_local(String in_env_sede_local) {
        this.in_env_sede_local = in_env_sede_local;
    }



    /**
     * @return the de_obs_msj
     */
    public String getDe_obs_msj() {
        return de_obs_msj;
    }

    /**
     * @param de_obs_msj the de_obs_msj to set
     */
    public void setDe_obs_msj(String de_obs_msj) {
        this.de_obs_msj = de_obs_msj;
    }

    /**
     * @return the nu_Acta_Vis1
     */
    public String getNu_Acta_Vis1() {
        return nu_Acta_Vis1;
    }

    /**
     * @param nu_Acta_Vis1 the nu_Acta_Vis1 to set
     */
    public void setNu_Acta_Vis1(String nu_Acta_Vis1) {
        this.nu_Acta_Vis1 = nu_Acta_Vis1;
    }

    /**
     * @return the nu_Acta_Vis2
     */
    public String getNu_Acta_Vis2() {
        return nu_Acta_Vis2;
    }

    /**
     * @param nu_Acta_Vis2 the nu_Acta_Vis2 to set
     */
    public void setNu_Acta_Vis2(String nu_Acta_Vis2) {
        this.nu_Acta_Vis2 = nu_Acta_Vis2;
    }

    /**
     * @return the fe_Acta_Vis1
     */
    public String getFe_Acta_Vis1() {
        return fe_Acta_Vis1;
    }

    /**
     * @param fe_Acta_Vis1 the fe_Acta_Vis1 to set
     */
    public void setFe_Acta_Vis1(String fe_Acta_Vis1) {
        this.fe_Acta_Vis1 = fe_Acta_Vis1;
    }

    /**
     * @return the fe_Acta_Vis2
     */
    public String getFe_Acta_Vis2() {
        return fe_Acta_Vis2;
    }

    /**
     * @param fe_Acta_Vis2 the fe_Acta_Vis2 to set
     */
    public void setFe_Acta_Vis2(String fe_Acta_Vis2) {
        this.fe_Acta_Vis2 = fe_Acta_Vis2;
    }

    /**
     * @return the es_Acta1_msj
     */
    public String getEs_Acta1_msj() {
        return es_Acta1_msj;
    }

    /**
     * @param es_Acta1_msj the es_Acta1_msj to set
     */
    public void setEs_Acta1_msj(String es_Acta1_msj) {
        this.es_Acta1_msj = es_Acta1_msj;
    }

    /**
     * @return the es_Acta2_msj
     */
    public String getEs_Acta2_msj() {
        return es_Acta2_msj;
    }

    /**
     * @param es_Acta2_msj the es_Acta2_msj to set
     */
    public void setEs_Acta2_msj(String es_Acta2_msj) {
        this.es_Acta2_msj = es_Acta2_msj;
    }

    /**
     * @return the archivo_Acta1
     */
    public String getArchivo_Acta1() {
        return archivo_Acta1;
    }

    /**
     * @param archivo_Acta1 the archivo_Acta1 to set
     */
    public void setArchivo_Acta1(String archivo_Acta1) {
        this.archivo_Acta1 = archivo_Acta1;
    }

    /**
     * @return the archivo_Acta2
     */
    public String getArchivo_Acta2() {
        return archivo_Acta2;
    }

    /**
     * @param archivo_Acta2 the archivo_Acta2 to set
     */
    public void setArchivo_Acta2(String archivo_Acta2) {
        this.archivo_Acta2 = archivo_Acta2;
    }

    /**
     * @return the archivo_Cargo
     */
    public String getArchivo_Cargo() {
        return archivo_Cargo;
    }

    /**
     * @param archivo_Cargo the archivo_Cargo to set
     */
    public void setArchivo_Cargo(String archivo_Cargo) {
        this.archivo_Cargo = archivo_Cargo;
    }

    /**
     * @return the sidEmiExt
     */
    public String getSidEmiExt() {
        return sidEmiExt;
    }

    /**
     * @param sidEmiExt the sidEmiExt to set
     */
    public void setSidEmiExt(String sidEmiExt) {
        this.sidEmiExt = sidEmiExt;
    }
    
    
}
