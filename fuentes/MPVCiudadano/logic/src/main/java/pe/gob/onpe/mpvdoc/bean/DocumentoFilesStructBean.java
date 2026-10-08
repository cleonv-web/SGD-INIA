/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.mpvdoc.bean;

/**
 *
 * @author Usuario
 */
public class DocumentoFilesStructBean { 
    private int inid;
    private String vctit;
    private byte[] vcarcda;

    public int getInid() {
        return inid;
    }

    public void setInid(int inid) {
        this.inid = inid;
    }

    public String getVctit() {
        return vctit;
    }

    public void setVctit(String vctit) {
        this.vctit = vctit;
    }

    public byte[] getVcarcda() {
        return vcarcda;
    }

    public void setVcarcda(byte[] vcarcda) {
        this.vcarcda = vcarcda;
    }
    
}
