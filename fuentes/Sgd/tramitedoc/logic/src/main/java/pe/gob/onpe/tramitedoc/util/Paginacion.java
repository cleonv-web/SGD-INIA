/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.util;
//import pe.gob.onpe.tramitedoc.util.Constantes;

/**
 *
 * @author ECueva
 */
public class Paginacion implements Constantes{
	private int numeroDePagina;
	private int registrosPorPagina;
	private int motorDeBD;
	private int numeroTotalDePaginas;
	
	private int numeroTotalRegistros;
	private int nroRegistroInicio;	
	public int getNumeroTotalRegistros() {
		return numeroTotalRegistros;
	}

	public void setNumeroTotalRegistros(int numeroTotalRegistros) {
		this.numeroTotalRegistros = numeroTotalRegistros;
		int nroTotalRegistros = numeroTotalRegistros;
		int nroTotalPaginas = (int)Math.ceil((double)nroTotalRegistros / (double)this.getRegistrosPorPagina());
		this.setNumeroTotalDePaginas(nroTotalPaginas);
	}

	public Paginacion(){
                nroRegistroInicio=1;//agregado ecueva
		numeroDePagina = 1; 	// pagina por defecto
		registrosPorPagina = nroRegistrosXPagina;
		motorDeBD = DB_ORACLE;//Conexion.obtenerMotorDeBDActual();
	}
	
	public Paginacion(int paramNroRegistrosXPagina){
		registrosPorPagina = paramNroRegistrosXPagina;
		motorDeBD = DB_POSTGRES;//Conexion.obtenerMotorDeBDActual();
	}
	
	public int getNumeroDePagina() {
		return numeroDePagina;
	}
	public void setNumeroDePagina(int numeroDePagina) {
		this.numeroDePagina = numeroDePagina;
	}
	public int getRegistrosPorPagina() {
		return registrosPorPagina;
	}
	public void setRegistrosPorPagina(int registrosPorPagina) {
		this.registrosPorPagina = registrosPorPagina;
	}
	
	public String obtenerSentencia(){
		String sentencia = "";
		if(motorDeBD == DB_MYSQL){
			sentencia = "LIMIT " + nroRegistroInicio + " , " + registrosPorPagina + " "; 	 
		}else if (motorDeBD == DB_ORACLE){
			///
		}else if (motorDeBD == DB_POSTGRES){
			sentencia = "LIMIT " + registrosPorPagina + " OFFSET " + nroRegistroInicio + " "; 	 
		}else if (motorDeBD == DB_MSSQL){
			
		}	
		return sentencia;
	}

	public int getNumeroTotalDePaginas() {
		return numeroTotalDePaginas;
	}

	public void setNumeroTotalDePaginas(int numeroTotalDePaginas) {
		this.numeroTotalDePaginas = numeroTotalDePaginas;
	}

	public void actualizarPaginaActual(String nroPaginaActual, String operacionPaginacion, String nroTotalPaginas) {
		int nroPaginaActualTmp = Integer.parseInt(nroPaginaActual.trim());
		if(operacionPaginacion.equals(paginacion_primero)){			
			this.numeroDePagina = 1;
		}else if(operacionPaginacion.equals(paginacion_atras)){		
			this.numeroDePagina = nroPaginaActualTmp - 1;
		}else if(operacionPaginacion.equals(paginacion_siguiente)){
			this.numeroDePagina = nroPaginaActualTmp + 1;
		}else if(operacionPaginacion.equals(paginacion_ultimo)){
			this.numeroDePagina = Integer.parseInt(nroTotalPaginas);
		}else{
			this.numeroDePagina = nroPaginaActualTmp;
		} 	
		this.nroRegistroInicio = registrosPorPagina * (this.numeroDePagina - 1 );
	}
	
	public String getStrRegistrosMostrados(){
		StringBuffer strBf = new StringBuffer();
		strBf.append("Registros Mostrados ");
		int nroRegistroEmpieza =  (Constantes.nroRegistrosXPagina * (this.numeroDePagina - 1)) +1;
		int nroRegistroTermina ;
		if(this.numeroDePagina == this.numeroTotalDePaginas){
			nroRegistroTermina = this.numeroTotalRegistros;
		}else{
			nroRegistroTermina = nroRegistroEmpieza + this.getRegistrosPorPagina() - 1 ;
		}
		
		strBf.append(nroRegistroEmpieza);
		strBf.append(" - ");
		strBf.append(nroRegistroTermina);
		strBf.append(" de ");
		strBf.append(this.numeroTotalRegistros);
		return strBf.toString();
	}

	public int getNroRegistroInicio() {
		return nroRegistroInicio;
	}

	public void setNroRegistroInicio(int nroRegistroInicio) {
		this.nroRegistroInicio = nroRegistroInicio;
	}    
}
