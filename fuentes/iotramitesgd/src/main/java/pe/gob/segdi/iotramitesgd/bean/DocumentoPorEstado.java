package pe.gob.segdi.iotramitesgd.bean;

import java.io.Serializable;

public class DocumentoPorEstado implements Serializable {

    /**
     *
     */
    private static final long serialVersionUID = 1L;
    private int pendientes;
    private int enviados;
    private int recepcionados;
    private int observados;

    public int getPendientes() {
        return pendientes;
    }

    public void setPendientes(int pendientes) {
        this.pendientes = pendientes;
    }

    public int getEnviados() {
        return enviados;
    }

    public void setEnviados(int enviados) {
        this.enviados = enviados;
    }

    public int getRecepcionados() {
        return recepcionados;
    }

    public void setRecepcionados(int recepcionados) {
        this.recepcionados = recepcionados;
    }

    public int getObservados() {
        return observados;
    }

    public void setObservados(int observados) {
        this.observados = observados;
    }


}
