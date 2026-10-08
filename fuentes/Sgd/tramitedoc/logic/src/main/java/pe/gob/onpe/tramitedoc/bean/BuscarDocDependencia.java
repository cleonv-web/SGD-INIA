/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 *
 * @author ECueva
 */
public class BuscarDocDependencia {
   private String codDepencia;
   private String codDocumento;
   private String firmaDigital;// '1' si es necesario,'0' no
   private String generaOficio;// '1' si,'0' no
   private String muestraBarraPaginacion;

    public BuscarDocDependencia() {
    }
    
    public BuscarDocDependencia(String codDependencia,String muestraBarraPaginacion) {
        this.codDepencia = codDependencia;
        this.muestraBarraPaginacion = muestraBarraPaginacion;
    }

    public String getMuestraBarraPaginacion() {
        return muestraBarraPaginacion;
    }

    public void setMuestraBarraPaginacion(String muestraBarraPaginacion) {
        this.muestraBarraPaginacion = muestraBarraPaginacion;
    }

    public String getCodDepencia() {
        return codDepencia;
    }

    public void setCodDepencia(String codDepencia) {
        this.codDepencia = codDepencia;
    }

    public String getCodDocumento() {
        return codDocumento;
    }

    public void setCodDocumento(String codDocumento) {
        this.codDocumento = codDocumento;
    }

    public String getFirmaDigital() {
        return firmaDigital;
    }

    public void setFirmaDigital(String firmaDigital) {
        this.firmaDigital = firmaDigital;
    }

    public String getGeneraOficio() {
        return generaOficio;
    }

    public void setGeneraOficio(String generaOficio) {
        this.generaOficio = generaOficio;
    }
}
