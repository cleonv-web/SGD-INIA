package pe.gob.onpe.tramitedoc.bean;

import java.util.Date;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;

public class DocumentoBean extends ReporteBean {
    
    private String nuAnn;
    private String nuEmi;
    private String nuCorEmi;
    private String coLocEmi;
    private String deLocEmi;
    private String coDepEmi;
    private String deDepEmi;
    private String tiEmi;
    private String deTipEmi;
    private String coEmpEmi;
    private String deEmpEmi;
    private String coEmpRes;
    private String deEmpRes;
    private String feEmi;
    private String feEmiCorta;
    private String deOriEmi;
    private String coTipDocAdm;
    private String deTipDocAdm;
    private String nuDoc;
    private String coGru;
    private String esDocEmi;
    private String deEsDocEmi;
    private String nuDiaAte;
    private String deAsu;
    private String deAsuM;
    private String deAne;
    private String coExpEmi;
    private String deExpEmi;
    private String coUbiArcEmi;
    private String deUbiArcEmi;
    private String nuDes;
    private String nuCorDes;
    private String coLocDes;
    private String deLocDes;
    private String coDepDes;
    private String deDepDes;
    private String tiDes;
    private String deTipDes;
    private String coEmpDes;
    private String deEmpDes;
    private String nuDniDes;
    private String nuRucDes;
    private String coOtrOriDes;
    private String deOriDes;
    private String coPri;
    private String dePri;
    private String coMot;
    private String deMot;
    private String dePro;
    private String coExp;
    private String deExp;
    private String coUbiArc;
    private String deUbiArc;
    private String esDes;
    private String esEnvPorTra;
    private String esEnvMs;
    private String coEmpRec;
    private String deEmpRec;
    private String esDocRec;
    private String deEsDocDes;
    private String feRecDoc;
    private String feRecDocCorta;
    private String feArcDoc;
    private String feArcDocCorta;
    private String feAteDoc;
    private String feAteDocCorta;
    private String feDerDoc;
    private String nuAnnRef;
    private String nuEmiRef;
    private String nuDesRef;
    private String deAsuNue;
    private String nuDiaAteNue;
    private String coDepRef;
    private String coLocRef;
    private String esEntMp;
    private String esMpEnt;
    private String feMpEnt;
    private String tiCap;
    private String tiEmiRef;
    private String nuAnnExp;
    private String nuSecExp;
    private String nuDetExp;
    private String nuExpediente;
    private String inExpe;
    private String nuFolios;
    private String seMesaPartes;
    private String existeDoc;//existe documento
    private String existeAnexo;//existe anexo
    private String codUseMod;
    private String feExpCorta;
    private String coProceso;
    private String deProceso;
    private String feEmiCorta2;//DD/MM/YY
    private String msjResult;
    private String tiFisicoEmi;
    private String tiFisicoRec;
    private String coEtiquetaRec;
    private String coEtiquetaEmi;
    private String deDependencia;
    private String deInstitucion;
    private String anexoMesaPartes;
    private String fonoInstitucion;
    private String pagWeb;
    private Date feExp;
    private String nuCorrExp;
    private String deMesaPartes;
    private String deSlogan;
    private String cclave;
    private String codUser;
    private String enprocesoatencion;
    private int iCanAvance;
    
    private float fila;
    private float filasTotal;
    
    private String deEmiRef;
    private String recursoenvio;
    private String deEmpPro;
    private String tiEnvMsj;
    private String docEstadoMsj;
    private String tiDest;
    private String deObsDoc;
 
    private String orden ;
    
    private String iPendientes;
    private String iUrgentes;
    private String iNormal;
    
    private String tiVoucherMP;
    private String obsVoucherMP;
    private String nuCopias;
    private String deUsuario;
    private String confidencial;
    private String sFirmaVB;

    public String getsFirmaVB() {
        return sFirmaVB;
    }

    public void setsFirmaVB(String sFirmaVB) {
        this.sFirmaVB = sFirmaVB;
    }
    
    
    public String getConfidencial() {
        return confidencial;
    }

    public void setConfidencial(String confidencial) {
        this.confidencial = confidencial;
    }
    
    public float getFila() {
        return fila;
    }

    public void setFila(float fila) {
        this.fila = fila;
    }

    public float getFilasTotal() {
        return filasTotal;
    }

    public void setFilasTotal(float filasTotal) {
        this.filasTotal = filasTotal;
    }
    
    
    public String getEnprocesoatencion() {
        return enprocesoatencion;
    }

    public void setEnprocesoatencion(String enprocesoatencion) {
        this.enprocesoatencion = enprocesoatencion;
    }

    public DocumentoBean() {
        // TODO Auto-generated constructor stub
    }

    public String getDeMesaPartes() {
        return deMesaPartes;
    }

    public void setDeMesaPartes(String deMesaPartes) {
        this.deMesaPartes = deMesaPartes;
    }

    public String getDeSlogan() {
        return deSlogan;
    }

    public void setDeSlogan(String deSlogan) {
        this.deSlogan = deSlogan;
    }

    public String getMsjResult() {
        return msjResult;
    }

    public void setMsjResult(String msjResult) {
        this.msjResult = msjResult;
    }

    public String getFeEmiCorta2() {
        return feEmiCorta2;
    }

    public void setFeEmiCorta2(String feEmiCorta2) {
        this.feEmiCorta2 = feEmiCorta2;
    }

    public String getFeExpCorta() {
        return feExpCorta;
    }

    public void setFeExpCorta(String feExpCorta) {
        this.feExpCorta = feExpCorta;
    }

    public String getCoProceso() {
        return coProceso;
    }

    public void setCoProceso(String coProceso) {
        this.coProceso = coProceso;
    }

    public String getDeProceso() {
        return deProceso;
    }

    public void setDeProceso(String deProceso) {
        this.deProceso = deProceso;
    }

    public String getCodUseMod() {
        return codUseMod;
    }

    public void setCodUseMod(String codUseMod) {
        this.codUseMod = codUseMod;
    }

    public String getFeArcDocCorta() {
        return feArcDocCorta;
    }

    public void setFeArcDocCorta(String feArcDocCorta) {
        this.feArcDocCorta = feArcDocCorta;
    }

    public String getFeAteDocCorta() {
        return feAteDocCorta;
    }

    public void setFeAteDocCorta(String feAteDocCorta) {
        this.feAteDocCorta = feAteDocCorta;
    }

    public String getFeRecDocCorta() {
        return feRecDocCorta;
    }

    public void setFeRecDocCorta(String feRecDocCorta) {
        this.feRecDocCorta = feRecDocCorta;
    }

    public String getExisteDoc() {
        return existeDoc;
    }

    public void setExisteDoc(String existeDoc) {
        this.existeDoc = existeDoc;
    }

    public String getExisteAnexo() {
        return existeAnexo;
    }

    public void setExisteAnexo(String existeAnexo) {
        this.existeAnexo = existeAnexo;
    }

    public String getFeEmiCorta() {
        return feEmiCorta;
    }

    public void setFeEmiCorta(String feEmiCorta) {
        this.feEmiCorta = feEmiCorta;
    }

    public String getNuAnn() {
        return nuAnn;
    }

    public void setNuAnn(String nuAnn) {
        this.nuAnn = nuAnn;
    }

    public String getNuEmi() {
        return nuEmi;
    }

    public void setNuEmi(String nuEmi) {
        this.nuEmi = nuEmi;
    }

    public String getNuCorEmi() {
        return nuCorEmi;
    }

    public void setNuCorEmi(String nuCorEmi) {
        this.nuCorEmi = nuCorEmi;
    }

    public String getCoLocEmi() {
        return coLocEmi;
    }

    public void setCoLocEmi(String coLocEmi) {
        this.coLocEmi = coLocEmi;
    }

    public String getDeLocEmi() {
        return deLocEmi;
    }

    public void setDeLocEmi(String deLocEmi) {
        this.deLocEmi = deLocEmi;
    }

    public String getCoDepEmi() {
        return coDepEmi;
    }

    public void setCoDepEmi(String coDepEmi) {
        this.coDepEmi = coDepEmi;
    }

    public String getDeDepEmi() {
        return deDepEmi;
    }

    public void setDeDepEmi(String deDepEmi) {
        this.deDepEmi = deDepEmi;
    }

    public String getTiEmi() {
        return tiEmi;
    }

    public void setTiEmi(String tiEmi) {
        this.tiEmi = tiEmi;
    }

    public String getDeTipEmi() {
        return deTipEmi;
    }

    public void setDeTipEmi(String deTipEmi) {
        this.deTipEmi = deTipEmi;
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

    public String getCoEmpRes() {
        return coEmpRes;
    }

    public void setCoEmpRes(String coEmpRes) {
        this.coEmpRes = coEmpRes;
    }

    public String getDeEmpRes() {
        return deEmpRes;
    }

    public void setDeEmpRes(String deEmpRes) {
        this.deEmpRes = deEmpRes;
    }

    public String getFeEmi() {
        return feEmi;
    }

    public void setFeEmi(String feEmi) {
        this.feEmi = feEmi;
    }

    public String getDeOriEmi() {
        return deOriEmi;
    }

    public void setDeOriEmi(String deOriEmi) {
        this.deOriEmi = deOriEmi;
    }

    public String getCoTipDocAdm() {
        return coTipDocAdm;
    }

    public void setCoTipDocAdm(String coTipDocAdm) {
        this.coTipDocAdm = coTipDocAdm;
    }

    public String getDeTipDocAdm() {
        return deTipDocAdm;
    }

    public void setDeTipDocAdm(String deTipDocAdm) {
        this.deTipDocAdm = deTipDocAdm;
    }

    public String getNuDoc() {
        return nuDoc;
    }

    public void setNuDoc(String nuDoc) {
        this.nuDoc = nuDoc;
    }

    public String getCoGru() {
        return coGru;
    }

    public void setCoGru(String coGru) {
        this.coGru = coGru;
    }

    public String getEsDocEmi() {
        return esDocEmi;
    }

    public void setEsDocEmi(String esDocEmi) {
        this.esDocEmi = esDocEmi;
    }

    public String getDeEsDocEmi() {
        return deEsDocEmi;
    }

    public void setDeEsDocEmi(String deEsDocEmi) {
        this.deEsDocEmi = deEsDocEmi;
    }

    public String getNuDiaAte() {
        return nuDiaAte;
    }

    public void setNuDiaAte(String nuDiaAte) {
        this.nuDiaAte = nuDiaAte;
    }

    public String getDeAsu() {
        return deAsu;
    }

    public void setDeAsu(String deAsu) {
        this.deAsu = deAsu;
    }

    public String getDeAsuM() {
        return deAsuM;
    }

    public void setDeAsuM(String deAsuM) {
        this.deAsuM = deAsuM;
    }

    public String getDeAne() {
        return deAne;
    }

    public void setDeAne(String deAne) {
        this.deAne = deAne;
    }

    public String getCoExpEmi() {
        return coExpEmi;
    }

    public void setCoExpEmi(String coExpEmi) {
        this.coExpEmi = coExpEmi;
    }

    public String getDeExpEmi() {
        return deExpEmi;
    }

    public void setDeExpEmi(String deExpEmi) {
        this.deExpEmi = deExpEmi;
    }

    public String getCoUbiArcEmi() {
        return coUbiArcEmi;
    }

    public void setCoUbiArcEmi(String coUbiArcEmi) {
        this.coUbiArcEmi = coUbiArcEmi;
    }

    public String getDeUbiArcEmi() {
        return deUbiArcEmi;
    }

    public void setDeUbiArcEmi(String deUbiArcEmi) {
        this.deUbiArcEmi = deUbiArcEmi;
    }

    public String getNuDes() {
        return nuDes;
    }

    public void setNuDes(String nuDes) {
        this.nuDes = nuDes;
    }

    public String getNuCorDes() {
        return nuCorDes;
    }

    public void setNuCorDes(String nuCorDes) {
        this.nuCorDes = nuCorDes;
    }

    public String getCoLocDes() {
        return coLocDes;
    }

    public void setCoLocDes(String coLocDes) {
        this.coLocDes = coLocDes;
    }

    public String getDeLocDes() {
        return deLocDes;
    }

    public void setDeLocDes(String deLocDes) {
        this.deLocDes = deLocDes;
    }

    public String getCoDepDes() {
        return coDepDes;
    }

    public void setCoDepDes(String coDepDes) {
        this.coDepDes = coDepDes;
    }

    public String getDeDepDes() {
        return deDepDes;
    }

    public void setDeDepDes(String deDepDes) {
        this.deDepDes = deDepDes;
    }

    public String getTiDes() {
        return tiDes;
    }

    public void setTiDes(String tiDes) {
        this.tiDes = tiDes;
    }

    public String getDeTipDes() {
        return deTipDes;
    }

    public void setDeTipDes(String deTipDes) {
        this.deTipDes = deTipDes;
    }

    public String getCoEmpDes() {
        return coEmpDes;
    }

    public void setCoEmpDes(String coEmpDes) {
        this.coEmpDes = coEmpDes;
    }

    public String getDeEmpDes() {
        return deEmpDes;
    }

    public void setDeEmpDes(String deEmpDes) {
        this.deEmpDes = deEmpDes;
    }

    public String getNuDniDes() {
        return nuDniDes;
    }

    public void setNuDniDes(String nuDniDes) {
        this.nuDniDes = nuDniDes;
    }

    public String getNuRucDes() {
        return nuRucDes;
    }

    public void setNuRucDes(String nuRucDes) {
        this.nuRucDes = nuRucDes;
    }

    public String getCoOtrOriDes() {
        return coOtrOriDes;
    }

    public void setCoOtrOriDes(String coOtrOriDes) {
        this.coOtrOriDes = coOtrOriDes;
    }

    public String getDeOriDes() {
        return deOriDes;
    }

    public void setDeOriDes(String deOriDes) {
        this.deOriDes = deOriDes;
    }

    public String getCoPri() {
        return coPri;
    }

    public void setCoPri(String coPri) {
        this.coPri = coPri;
    }

    public String getDePri() {
        return dePri;
    }

    public void setDePri(String dePri) {
        this.dePri = dePri;
    }

    public String getCoMot() {
        return coMot;
    }

    public void setCoMot(String coMot) {
        this.coMot = coMot;
    }

    public String getDeMot() {
        return deMot;
    }

    public void setDeMot(String deMot) {
        this.deMot = deMot;
    }

    public String getDePro() {
        return dePro;
    }

    public void setDePro(String dePro) {
        this.dePro = dePro;
    }

    public String getCoExp() {
        return coExp;
    }

    public void setCoExp(String coExp) {
        this.coExp = coExp;
    }

    public String getDeExp() {
        return deExp;
    }

    public void setDeExp(String deExp) {
        this.deExp = deExp;
    }

    public String getCoUbiArc() {
        return coUbiArc;
    }

    public void setCoUbiArc(String coUbiArc) {
        this.coUbiArc = coUbiArc;
    }

    public String getDeUbiArc() {
        return deUbiArc;
    }

    public void setDeUbiArc(String deUbiArc) {
        this.deUbiArc = deUbiArc;
    }

    public String getEsDes() {
        return esDes;
    }

    public void setEsDes(String esDes) {
        this.esDes = esDes;
    }

    public String getEsEnvPorTra() {
        return esEnvPorTra;
    }

    public void setEsEnvPorTra(String esEnvPorTra) {
        this.esEnvPorTra = esEnvPorTra;
    }

    public String getEsEnvMs() {
        return esEnvMs;
    }

    public void setEsEnvMs(String esEnvMs) {
        this.esEnvMs = esEnvMs;
    }

    public String getCoEmpRec() {
        return coEmpRec;
    }

    public void setCoEmpRec(String coEmpRec) {
        this.coEmpRec = coEmpRec;
    }

    public String getDeEmpRec() {
        return deEmpRec;
    }

    public void setDeEmpRec(String deEmpRec) {
        this.deEmpRec = deEmpRec;
    }

    public String getEsDocRec() {
        return esDocRec;
    }

    public void setEsDocRec(String esDocRec) {
        this.esDocRec = esDocRec;
    }

    public String getDeEsDocDes() {
        return deEsDocDes;
    }

    public void setDeEsDocDes(String deEsDocDes) {
        this.deEsDocDes = deEsDocDes;
    }

    public String getFeRecDoc() {
        return feRecDoc;
    }

    public void setFeRecDoc(String feRecDoc) {
        this.feRecDoc = feRecDoc;
    }

    public String getFeArcDoc() {
        return feArcDoc;
    }

    public void setFeArcDoc(String feArcDoc) {
        this.feArcDoc = feArcDoc;
    }

    public String getFeAteDoc() {
        return feAteDoc;
    }

    public void setFeAteDoc(String feAteDoc) {
        this.feAteDoc = feAteDoc;
    }

    public String getFeDerDoc() {
        return feDerDoc;
    }

    public void setFeDerDoc(String feDerDoc) {
        this.feDerDoc = feDerDoc;
    }

    public String getNuAnnRef() {
        return nuAnnRef;
    }

    public void setNuAnnRef(String nuAnnRef) {
        this.nuAnnRef = nuAnnRef;
    }

    public String getNuEmiRef() {
        return nuEmiRef;
    }

    public void setNuEmiRef(String nuEmiRef) {
        this.nuEmiRef = nuEmiRef;
    }

    public String getNuDesRef() {
        return nuDesRef;
    }

    public void setNuDesRef(String nuDesRef) {
        this.nuDesRef = nuDesRef;
    }

    public String getDeAsuNue() {
        return deAsuNue;
    }

    public void setDeAsuNue(String deAsuNue) {
        this.deAsuNue = deAsuNue;
    }

    public String getNuDiaAteNue() {
        return nuDiaAteNue;
    }

    public void setNuDiaAteNue(String nuDiaAteNue) {
        this.nuDiaAteNue = nuDiaAteNue;
    }

    public String getCoDepRef() {
        return coDepRef;
    }

    public void setCoDepRef(String coDepRef) {
        this.coDepRef = coDepRef;
    }

    public String getCoLocRef() {
        return coLocRef;
    }

    public void setCoLocRef(String coLocRef) {
        this.coLocRef = coLocRef;
    }

    public String getEsEntMp() {
        return esEntMp;
    }

    public void setEsEntMp(String esEntMp) {
        this.esEntMp = esEntMp;
    }

    public String getEsMpEnt() {
        return esMpEnt;
    }

    public void setEsMpEnt(String esMpEnt) {
        this.esMpEnt = esMpEnt;
    }

    public String getFeMpEnt() {
        return feMpEnt;
    }

    public void setFeMpEnt(String feMpEnt) {
        this.feMpEnt = feMpEnt;
    }

    public String getTiCap() {
        return tiCap;
    }

    public void setTiCap(String tiCap) {
        this.tiCap = tiCap;
    }

    public String getTiEmiRef() {
        return tiEmiRef;
    }

    public void setTiEmiRef(String tiEmiRef) {
        this.tiEmiRef = tiEmiRef;
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

    public String getNuDetExp() {
        return nuDetExp;
    }

    public void setNuDetExp(String nuDetExp) {
        this.nuDetExp = nuDetExp;
    }

    public String getNuExpediente() {
        return nuExpediente;
    }

    public void setNuExpediente(String nuExpediente) {
        this.nuExpediente = nuExpediente;
    }

    public String getInExpe() {
        return inExpe;
    }

    public void setInExpe(String inExpe) {
        this.inExpe = inExpe;
    }

    public String getNuFolios() {
        return nuFolios;
    }

    public void setNuFolios(String nuFolios) {
        this.nuFolios = nuFolios;
    }

    public String getSeMesaPartes() {
        return seMesaPartes;
    }

    public void setSeMesaPartes(String seMesaPartes) {
        this.seMesaPartes = seMesaPartes;
    }

    public String getTiFisicoEmi() {
        return tiFisicoEmi;
    }

    public void setTiFisicoEmi(String tiFisicoEmi) {
        this.tiFisicoEmi = tiFisicoEmi;
    }

    public String getTiFisicoRec() {
        return tiFisicoRec;
    }

    public void setTiFisicoRec(String tiFisicoRec) {
        this.tiFisicoRec = tiFisicoRec;
    }

    public String getCoEtiquetaRec() {
        return coEtiquetaRec;
    }

    public void setCoEtiquetaRec(String coEtiquetaRec) {
        this.coEtiquetaRec = coEtiquetaRec;
    }

    public String getCoEtiquetaEmi() {
        return coEtiquetaEmi;
    }

    public void setCoEtiquetaEmi(String coEtiquetaEmi) {
        this.coEtiquetaEmi = coEtiquetaEmi;
    }

    public String getDeDependencia() {
        return deDependencia;
    }

    public void setDeDependencia(String deDependencia) {
        this.deDependencia = deDependencia;
    }

    public String getDeInstitucion() {
        return deInstitucion;
    }

    public void setDeInstitucion(String deInstitucion) {
        this.deInstitucion = deInstitucion;
    }

    public String getAnexoMesaPartes() {
        return anexoMesaPartes;
    }

    public void setAnexoMesaPartes(String anexoMesaPartes) {
        this.anexoMesaPartes = anexoMesaPartes;
    }

    public String getFonoInstitucion() {
        return fonoInstitucion;
    }

    public void setFonoInstitucion(String fonoInstitucion) {
        this.fonoInstitucion = fonoInstitucion;
    }

    public String getPagWeb() {
        return pagWeb;
    }

    public void setPagWeb(String pagWeb) {
        this.pagWeb = pagWeb;
    }

    public Date getFeExp() {
        return feExp;
    }

    public void setFeExp(Date feExp) {
        this.feExp = feExp;
    }

    public String getNuCorrExp() {
        return nuCorrExp;
    }

    public void setNuCorrExp(String nuCorrExp) {
        this.nuCorrExp = nuCorrExp;
    }

    public String getCclave() {
        return cclave;
    }

    public void setCclave(String cclave) {
        this.cclave = cclave;
    }

    public String getCodUser() {
        return codUser;
    }

    public void setCodUser(String codUser) {
        this.codUser = codUser;
    }
 

    /**
     * @return the iCanAvance
     */
    public int getiCanAvance() {
        return iCanAvance;
    }

    /**
     * @param iCanAvance the iCanAvance to set
     */
    public void setiCanAvance(int iCanAvance) {
        this.iCanAvance = iCanAvance;
    }

    /**
     * @return the orden
     */
    public String getOrden() {
        return orden;
    }

    /**
     * @param orden the orden to set
     */
    public void setOrden(String orden) {
        this.orden = orden;
    }

    /**
     * @return the deEmiRef
     */
    public String getDeEmiRef() {
        return deEmiRef;
    }

    /**
     * @param deEmiRef the deEmiRef to set
     */
    public void setDeEmiRef(String deEmiRef) {
        this.deEmiRef = deEmiRef;
    }

    /**
     * @return the recursoenvio
     */
    public String getRecursoenvio() {
        return recursoenvio;
    }

    /**
     * @param recursoenvio the recursoenvio to set
     */
    public void setRecursoenvio(String recursoenvio) {
        this.recursoenvio = recursoenvio;
    }

    /**
     * @return the deEmpPro
     */
    public String getDeEmpPro() {
        return deEmpPro;
    }

    /**
     * @param deEmpPro the deEmpPro to set
     */
    public void setDeEmpPro(String deEmpPro) {
        this.deEmpPro = deEmpPro;
    }

    /**
     * @return the tiEnvMsj
     */
    public String getTiEnvMsj() {
        return tiEnvMsj;
    }

    /**
     * @param tiEnvMsj the tiEnvMsj to set
     */
    public void setTiEnvMsj(String tiEnvMsj) {
        this.tiEnvMsj = tiEnvMsj;
    }

    /**
     * @return the docEstadoMsj
     */
    public String getDocEstadoMsj() {
        return docEstadoMsj;
    }

    /**
     * @param docEstadoMsj the docEstadoMsj to set
     */
    public void setDocEstadoMsj(String docEstadoMsj) {
        this.docEstadoMsj = docEstadoMsj;
    }

    /**
     * @return the tiDest
     */
    public String getTiDest() {
        return tiDest;
    }

    /**
     * @param tiDest the tiDest to set
     */
    public void setTiDest(String tiDest) {
        this.tiDest = tiDest;
    }

    /**
     * @return the deObsDoc
     */
    public String getDeObsDoc() {
        return deObsDoc;
    }

    /**
     * @param deObsDoc the deObsDoc to set
     */
    public void setDeObsDoc(String deObsDoc) {
        this.deObsDoc = deObsDoc;
    }

    /**
     * @return the iPendientes
     */
    public String getiPendientes() {
        return iPendientes;
    }

    /**
     * @param iPendientes the iPendientes to set
     */
    public void setiPendientes(String iPendientes) {
        this.iPendientes = iPendientes;
    }

    /**
     * @return the iUrgentes
     */
    public String getiUrgentes() {
        return iUrgentes;
    }

    /**
     * @param iUrgentes the iUrgentes to set
     */
    public void setiUrgentes(String iUrgentes) {
        this.iUrgentes = iUrgentes;
    }

    /**
     * @return the iNormal
     */
    public String getiNormal() {
        return iNormal;
    }

    /**
     * @param iNormal the iNormal to set
     */
    public void setiNormal(String iNormal) {
        this.iNormal = iNormal;
    }

    /**
     * @return the tiVoucherMP
     */
    public String getTiVoucherMP() {
        return tiVoucherMP;
    }

    /**
     * @param tiVoucherMP the tiVoucherMP to set
     */
    public void setTiVoucherMP(String tiVoucherMP) {
        this.tiVoucherMP = tiVoucherMP;
    }

 

    /**
     * @return the nuCopias
     */
    public String getNuCopias() {
        return nuCopias;
    }

    /**
     * @param nuCopias the nuCopias to set
     */
    public void setNuCopias(String nuCopias) {
        this.nuCopias = nuCopias;
    }

    /**
     * @return the deUsuario
     */
    public String getDeUsuario() {
        return deUsuario;
    }

    /**
     * @param deUsuario the deUsuario to set
     */
    public void setDeUsuario(String deUsuario) {
        this.deUsuario = deUsuario;
    }

    /**
     * @return the obsVoucherMP
     */
    public String getObsVoucherMP() {
        return obsVoucherMP;
    }

    /**
     * @param obsVoucherMP the obsVoucherMP to set
     */
    public void setObsVoucherMP(String obsVoucherMP) {
        this.obsVoucherMP = obsVoucherMP;
    }

    
}
