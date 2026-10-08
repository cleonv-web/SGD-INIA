/**
 * 
 */
package pe.gob.onpe.tramitedoc.bean;

/**
 * @author ecueva
 *
 */
public class AvisoBandejaEntradaBean {

	private String tiPen;
	private String dePen;
	private String deResumen;
	private String coDep;
	private String nuCan;
        private String nuCanUrgente;
        private String nuCanMuyUrgente;
        private String nuCanNormal;
	private String coBandeja;

    public String getCoBandeja() {
        return coBandeja;
    }

    public void setCoBandeja(String coBandeja) {
        this.coBandeja = coBandeja;
    }
        
	public String getTiPen() {
		return tiPen;
	}
	public void setTiPen(String tiPen) {
		this.tiPen = tiPen;
	}
	public String getDePen() {
		return dePen;
	}
	public void setDePen(String dePen) {
		this.dePen = dePen;
	}
	public String getCoDep() {
		return coDep;
	}
	public void setCoDep(String coDep) {
		this.coDep = coDep;
	}
	public String getNuCan() {
		return nuCan;
	}
	public void setNuCan(String nuCan) {
		this.nuCan = nuCan;
	}

    public String getDeResumen() {
        return deResumen;
    }

    public void setDeResumen(String deResumen) {
        this.deResumen = deResumen;
    }

    /**
     * @return the nuCanUrgente
     */
    public String getNuCanUrgente() {
        return nuCanUrgente;
    }

    /**
     * @param nuCanUrgente the nuCanUrgente to set
     */
    public void setNuCanUrgente(String nuCanUrgente) {
        this.nuCanUrgente = nuCanUrgente;
    }

    /**
     * @return the nuCanMuyUrgente
     */
    public String getNuCanMuyUrgente() {
        return nuCanMuyUrgente;
    }

    /**
     * @param nuCanMuyUrgente the nuCanMuyUrgente to set
     */
    public void setNuCanMuyUrgente(String nuCanMuyUrgente) {
        this.nuCanMuyUrgente = nuCanMuyUrgente;
    }

    /**
     * @return the nuCanNormal
     */
    public String getNuCanNormal() {
        return nuCanNormal;
    }

    /**
     * @param nuCanNormal the nuCanNormal to set
     */
    public void setNuCanNormal(String nuCanNormal) {
        this.nuCanNormal = nuCanNormal;
    }
    
}
