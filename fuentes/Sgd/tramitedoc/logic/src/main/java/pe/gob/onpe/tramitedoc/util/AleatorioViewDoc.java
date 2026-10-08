/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.util;

import java.util.Date;
import pe.gob.onpe.tramitedoc.web.util.Utilidades;

/**
 *
 * @author ecueva
 */
public class AleatorioViewDoc {
    private final String nroViewDoc;
    private final long dTime;

    public AleatorioViewDoc(String nroViewDoc,long dTime) {
        this.nroViewDoc = nroViewDoc;
        this.dTime=dTime;
    }
    
    public AleatorioViewDoc() {
        this.nroViewDoc=Utilidades.generateRandomNumber(8);
        this.dTime=new Date().getTime();
    }

    public long getdTime() {
        return dTime;
    }

    public String getNroviewDoc() {
        return nroViewDoc;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 59 * hash + (this.nroViewDoc != null ? this.nroViewDoc.hashCode() : 0);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AleatorioViewDoc other = (AleatorioViewDoc) obj;
        if(other.dTime==1L){
            final Long lnow = new Date().getTime()-10*1000;
            return this.dTime<lnow;
        }else{
            return !((this.nroViewDoc == null) ? (other.nroViewDoc != null) : !this.nroViewDoc.equals(other.nroViewDoc));
        }
    }
    
    @Override
    public String toString(){
        return this.nroViewDoc;
    }
}
