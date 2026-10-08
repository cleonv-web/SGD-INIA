/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author oti2
 */
public class TipoElementoMensajeriaBean {
    private String deDestinatario;
    private String coDestinatario;
    private String codigo;
    private String nombre;
    public TipoElementoMensajeriaBean() {
    }
    public String getNombre() {
        return nombre;
    }
    public void setNombre(String nombre) {
        this.nombre = nombre;
    }
   public String getCodigo() {
        return codigo;
    }
    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }
    public String getDeDestinatario() {
        return deDestinatario;
    }

    public void setDeDestinatario(String deDestinatario) {
        this.deDestinatario = deDestinatario;
    }

    public String getCoDestinatario() {
        return coDestinatario;
    }

    public void setCoDestinatario(String coDestinatario) {
        this.coDestinatario = coDestinatario;
    }
}
