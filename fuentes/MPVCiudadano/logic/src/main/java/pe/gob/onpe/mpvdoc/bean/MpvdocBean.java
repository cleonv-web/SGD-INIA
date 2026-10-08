/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.bean;
import java.util.ArrayList;
import java.util.LinkedList;
import org.codehaus.jackson.annotate.JsonIgnoreProperties;
/**
 *
 * @author RATAYAURI
 */
//@JsonIgnoreProperties(ignoreUnknown = true)
public class MpvdocBean {
    private String cookieSessionId;
    private String tipoDocumento;    
    private String idTipoPersona;      
    private String ruc;    
    private String raz;
    private String dni;
    private String pat;
    private String mat;
    private String nom;
    private String tel;
    private String correo;
    private String dir;
    private String nrodoc; 
    private String folios; 
    private String asu;     
    private String textoCaptcha; 
    private String  nombreArchivo;
    private ArrayList<String> listAnexos;    
    private LinkedList<DocumentoFilesStructBean> filesProcess;
    private byte[] archivoBytes;
    private String dep;
    private String pro; 
    private String dis; 

    public String getDep() {
        return dep;
    }

    public void setDep(String dep) {
        this.dep = dep;
    }

    public String getPro() {
        return pro;
    }

    public void setPro(String pro) {
        this.pro = pro;
    }

    public String getDis() {
        return dis;
    }

    public void setDis(String dis) {
        this.dis = dis;
    }
    
    public byte[] getArchivoBytes() {
        return archivoBytes;
    }

    public void setArchivoBytes(byte[] archivoBytes) {
        this.archivoBytes = archivoBytes;
    }
    
    
    public LinkedList<DocumentoFilesStructBean> getFilesProcess() {
        return filesProcess;
    }

    public void setFilesProcess(LinkedList<DocumentoFilesStructBean> filesProcess) {
        this.filesProcess = filesProcess;
    }
    
    public String getNombreArchivo() {
        return nombreArchivo;
    }

    public void setNombreArchivo(String nombreArchivo) {
        this.nombreArchivo = nombreArchivo;
    }
    
    
    public ArrayList<String> getListAnexos() {
        return listAnexos;
    }

    public void setListAnexos(ArrayList<String> listAnexos) {
        this.listAnexos = listAnexos;
    }
     
    public String getCookieSessionId() {
        return cookieSessionId;
    }

    public void setCookieSessionId(String cookieSessionId) {
        this.cookieSessionId = cookieSessionId;
    }

    public String getTipoDocumento() {
        return tipoDocumento;
    }

    public void setTipoDocumento(String tipoDocumento) {
        this.tipoDocumento = tipoDocumento;
    }

    public String getIdTipoPersona() {
        return idTipoPersona;
    }

    public void setIdTipoPersona(String idTipoPersona) {
        this.idTipoPersona = idTipoPersona;
    }

     

    public String getRuc() {
        return ruc;
    }

    public void setRuc(String ruc) {
        this.ruc = ruc;
    }

    public String getRaz() {
        return raz;
    }

    public void setRaz(String raz) {
        this.raz = raz;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public String getPat() {
        return pat;
    }

    public void setPat(String pat) {
        this.pat = pat;
    }

    public String getMat() {
        return mat;
    }

    public void setMat(String mat) {
        this.mat = mat;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getCorreo() {
        return correo;
    }

    public void setCorreo(String correo) {
        this.correo = correo;
    }

    public String getDir() {
        return dir;
    }

    public void setDir(String dir) {
        this.dir = dir;
    }

    public String getNrodoc() {
        return nrodoc;
    }

    public void setNrodoc(String nrodoc) {
        this.nrodoc = nrodoc;
    }

    public String getFolios() {
        return folios;
    }

    public void setFolios(String folios) {
        this.folios = folios;
    }

    public String getAsu() {
        return asu;
    }

    public void setAsu(String asu) {
        this.asu = asu;
    }

    public String getTextoCaptcha() {
        return textoCaptcha;
    }

    public void setTextoCaptcha(String textoCaptcha) {
        this.textoCaptcha = textoCaptcha;
    }
 
}
