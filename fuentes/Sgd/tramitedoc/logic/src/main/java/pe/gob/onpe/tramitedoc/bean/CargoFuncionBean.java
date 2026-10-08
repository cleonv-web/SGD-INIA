/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class CargoFuncionBean {
    String codigo;
    String descripcion;
    String estado;
    String coCategoria;
    String coOcupaPE;

    public CargoFuncionBean() {
    }

    public String getCodigo() {
        return codigo;
    }

    public void setCodigo(String codigo) {
        this.codigo = codigo;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getCoCategoria() {
        return coCategoria;
    }

    public void setCoCategoria(String coCategoria) {
        this.coCategoria = coCategoria;
    }

    public String getCoOcupaPE() {
        return coOcupaPE;
    }

    public void setCoOcupaPE(String coOcupaPE) {
        this.coOcupaPE = coOcupaPE;
    }
}
