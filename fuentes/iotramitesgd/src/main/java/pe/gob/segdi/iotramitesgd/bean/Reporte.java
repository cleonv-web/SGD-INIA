package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class Reporte implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;

    private Integer cantidad;
    private int pendiente;
    private int enviado;
    private int recepcionado;
    private int observado;
    private int subsanado;
    private String cflgest;
    private String vmes;

    public Integer getCantidad() {
        return cantidad;
    }

    public void setCantidad(Integer cantidad) {
        this.cantidad = cantidad;
    }

    public int getPendiente() {
        return pendiente;
    }

    public void setPendiente(int pendiente) {
        this.pendiente = pendiente;
    }

    public int getEnviado() {
        return enviado;
    }

    public void setEnviado(int enviado) {
        this.enviado = enviado;
    }

    public int getRecepcionado() {
        return recepcionado;
    }

    public void setRecepcionado(int recepcionado) {
        this.recepcionado = recepcionado;
    }

    public int getObservado() {
        return observado;
    }

    public void setObservado(int observado) {
        this.observado = observado;
    }

    public int getSubsanado() {
        return subsanado;
    }

    public void setSubsanado(int subsanado) {
        this.subsanado = subsanado;
    }

    public String getCflgest() {
        return cflgest;
    }

    public void setCflgest(String cflgest) {
        this.cflgest = cflgest;
    }

    public String getVmes() {
        return vmes;
    }

    public void setVmes(String vmes) {
        this.vmes = vmes;
    }


}
