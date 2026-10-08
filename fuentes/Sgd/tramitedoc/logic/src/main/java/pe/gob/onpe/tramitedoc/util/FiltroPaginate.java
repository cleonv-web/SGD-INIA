/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.util;

/**
 *
 * @author oti2
 */
public class FiltroPaginate {
    private int numeroPagina;
    private int registrosPagina; 
    private String orden ;
    public String getOrden() {
        return orden;
    }

    public void setOrden(String orden) {
        this.orden = orden;
    }
    
    public int getPaginaDesde() {
        return (numeroPagina * registrosPagina)+1;
    }

    public int getPaginaHasta() {
        return (numeroPagina + 1) * registrosPagina;
    }
    
        
     public FiltroPaginate()
        {
            this.numeroPagina = 1;
            this.registrosPagina = Integer.MAX_VALUE;
        }
     
    public int getNumeroPagina() {
        return numeroPagina;
    }

    public void setNumeroPagina(int numeroPagina) {
        this.numeroPagina = numeroPagina;
    }

    public int getRegistrosPagina() {
        return registrosPagina;
    }

    public void setRegistrosPagina(int registrosPagina) {
        this.registrosPagina = registrosPagina;
    }
  
}
