/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class EmpleadoBean {
    private String cempApepat;
    private String cempApemat;
    private String cempDenom;
    private String cempCodemp;
    private String fullName;
    private String nombre;
    private String coLocal;
    private String deLocal;
    private String compName;
    private String accionBD;
    private String estado;

    public EmpleadoBean() {
    }

    public String getEstado() {
        return estado;
    }

    public void setEstado(String estado) {
        this.estado = estado;
    }

    public String getAccionBD() {
        return accionBD;
    }

    public void setAccionBD(String accionBD) {
        this.accionBD = accionBD;
    }

    public String getCompName() {
        return compName;
    }

    public void setCompName(String compName) {
        this.compName = compName;
    }


    public String getCoLocal() {
        return coLocal;
    }

    public void setCoLocal(String coLocal) {
        this.coLocal = coLocal;
    }

    public String getDeLocal() {
        return deLocal;
    }

    public void setDeLocal(String deLocal) {
        this.deLocal = deLocal;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getCempApepat() {
        return cempApepat;
    }

    public void setCempApepat(String cempApepat) {
        this.cempApepat = cempApepat;
    }

    public String getCempApemat() {
        return cempApemat;
    }

    public void setCempApemat(String cempApemat) {
        this.cempApemat = cempApemat;
    }

    public String getCempDenom() {
        return cempDenom;
    }

    public void setCempDenom(String cempDenom) {
        this.cempDenom = cempDenom;
    }

    public String getCempCodemp() {
        return cempCodemp;
    }

    public void setCempCodemp(String cempCodemp) {
        this.cempCodemp = cempCodemp;
    }

    public String getFullName() {
        return this.cempApepat + " " + this.cempApemat + " " + this.cempDenom;
    }
}
