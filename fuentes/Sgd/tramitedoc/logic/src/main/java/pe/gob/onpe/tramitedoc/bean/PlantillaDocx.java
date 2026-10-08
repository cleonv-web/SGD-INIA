/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

import fr.opensagres.xdocreport.document.IXDocReport;

/**
 *
 * @author WCutipa
 */
public class PlantillaDocx {
    private String coTipoDoc;
    private String coDep;
    private String nomArchivo;
    private byte[] objPlantilla;
    IXDocReport template;
            
    public String getCoTipoDoc() {
        return coTipoDoc;
    }

    public void setCoTipoDoc(String coTipoDoc) {
        this.coTipoDoc = coTipoDoc;
    }

    public String getCoDep() {
        return coDep;
    }

    public void setCoDep(String coDep) {
        this.coDep = coDep;
    }

    public String getNomArchivo() {
        return nomArchivo;
    }

    public void setNomArchivo(String nomArchivo) {
        this.nomArchivo = nomArchivo;
    }

    public byte[] getObjPlantilla() {
        return objPlantilla;
    }

    public void setObjPlantilla(byte[] objPlantilla) {
        this.objPlantilla = objPlantilla;
    }

    public IXDocReport getTemplate() {
        return template;
    }

    public void setTemplate(IXDocReport template) {
        this.template = template;
    }

    
}
