/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author NGilt
 */
public class EtiquetaBandejaEnBean {

    private String coEst;
    private String coDepDes;
    private String coEmpDes;
    private String descripcion;
    private String numeroDocumentos;

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getNumeroDocumentos() {
        return numeroDocumentos;
    }

    public void setNumeroDocumentos(String numeroDocumentos) {
        this.numeroDocumentos = numeroDocumentos;
    }

    public String getCoEst() {
        return coEst;
    }

    public void setCoEst(String coEst) {
        this.coEst = coEst;
    }

    public String getCoDepDes() {
        return coDepDes;
    }

    public void setCoDepDes(String coDepDes) {
        this.coDepDes = coDepDes;
    }

    public String getCoEmpDes() {
        return coEmpDes;
    }

    public void setCoEmpDes(String coEmpDes) {
        this.coEmpDes = coEmpDes;
    }
    

}
