/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 * @author RBerrocal
 * bean asociado a la tabla TDTR_PROCESOS_EXP para almacenar una TUPA
 */
public class TupaBean {
    private String coProceso;     //Codigo del Proceso
    private String tiProceso;     //Tipo de Proceso(0=General,1=TUPA)  
    private String coDepPro;      //Codigo de Dependencia a la que pertenece el Expediente
    private String deNombre;      //Descripcion Corta del Proceso
    private String deDescripcion; //Descripcion del proceso
    
    private Long   nuPlazo;       //Cantidad en Dias para el Plazo
    private String esEstado;      //Estado (1=ACTIVO,0=INACTIVO)
    private String usCreaAudi;    //Usuario que creacion
    private String feCreaAudi;    //Fecha de creacion 
    private String usModiAudi;    //Usuario de modificacion
    
    private String feModiAudi;    //Fecha de modificacion

    public String TupaBean() {
            return "Tupa [coProceso=" + coProceso + ", tiProceso=" + tiProceso + ", coDepPro=" + coDepPro + ", "
                            + ", deNombre=" + deNombre + ", deDescripcion=" + deDescripcion + ", nuPlazo=" + nuPlazo 
                            + ", esEstado=" + esEstado + ", usCreaAudi=" + usCreaAudi + ", feCreaAudi=" + feCreaAudi 
                            + ", usModiAudi=" + usModiAudi + ", feModiAudi=" + feModiAudi
                            + "]\n";
    }
	
    //getters and setters
    public String getCoProceso() {
        return coProceso;
    }

    public void setCoProceso(String coProceso) {
        this.coProceso = coProceso;
    }

    public String getTiProceso() {
        return tiProceso;
    }

    public void setTiProceso(String tiProceso) {
        this.tiProceso = tiProceso;
    }

    public String getCoDepPro() {
        return coDepPro;
    }

    public void setCoDepPro(String coDepPro) {
        this.coDepPro = coDepPro;
    }

    public String getDeNombre() {
        return deNombre;
    }

    public void setDeNombre(String deNombre) {
        this.deNombre = deNombre;
    }

    public String getDeDescripcion() {
        return deDescripcion;
    }

    public void setDeDescripcion(String deDescripcion) {
        this.deDescripcion = deDescripcion;
    }

    public Long getNuPlazo() {
        return nuPlazo;
    }

    public void setNuPlazo(Long nuPlazo) {
        this.nuPlazo = nuPlazo;
    }

    public String getEsEstado() {
        return esEstado;
    }

    public void setEsEstado(String esEstado) {
        this.esEstado = esEstado;
    }

    public String getUsCreaAudi() {
        return usCreaAudi;
    }

    public void setUsCreaAudi(String usCreaAudi) {
        this.usCreaAudi = usCreaAudi;
    }

    public String getFeCreaAudi() {
        return feCreaAudi;
    }

    public void setFeCreaAudi(String feCreaAudi) {
        this.feCreaAudi = feCreaAudi;
    }

    public String getUsModiAudi() {
        return usModiAudi;
    }

    public void setUsModiAudi(String usModiAudi) {
        this.usModiAudi = usModiAudi;
    }

    public String getFeModiAudi() {
        return feModiAudi;
    }

    public void setFeModiAudi(String feModiAudi) {
        this.feModiAudi = feModiAudi;
    }   
    
}
