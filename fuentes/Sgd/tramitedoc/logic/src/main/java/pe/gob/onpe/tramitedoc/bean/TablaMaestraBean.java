/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ypino
 */
public class TablaMaestraBean {
    private String nombreTabla;
    private String Columna;
    private String TipoDato;    
    private String Tamanio;
    private String Valor;

    /**
     * @return the nombreTabla
     */
    public String getNombreTabla() {
        return nombreTabla;
    }

    /**
     * @param nombreTabla the nombreTabla to set
     */
    public void setNombreTabla(String nombreTabla) {
        this.nombreTabla = nombreTabla;
    }

    /**
     * @return the Columna
     */
    public String getColumna() {
        return Columna;
    }

    /**
     * @param Columna the Columna to set
     */
    public void setColumna(String Columna) {
        this.Columna = Columna;
    }

    /**
     * @return the TipoDato
     */
    public String getTipoDato() {
        return TipoDato;
    }

    /**
     * @param TipoDato the TipoDato to set
     */
    public void setTipoDato(String TipoDato) {
        this.TipoDato = TipoDato;
    }

    /**
     * @return the Tamanio
     */
    public String getTamanio() {
        return Tamanio;
    }

    /**
     * @param Tamanio the Tamanio to set
     */
    public void setTamanio(String Tamanio) {
        this.Tamanio = Tamanio;
    }

    /**
     * @return the Valor
     */
    public String getValor() {
        return Valor;
    }

    /**
     * @param Valor the Valor to set
     */
    public void setValor(String Valor) {
        this.Valor = Valor;
    }
    
}
