package pe.gob.onpe.tramitedoc.service;

import java.util.List;

import pe.gob.onpe.tramitedoc.bean.AvisoBandejaEntradaBean;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBandejaEnBean;

public interface AvisoBandejaEntradaService {

	public abstract List<AvisoBandejaEntradaBean> getBandejaEntrada(
			String coDependencia,String coEmpleado, String tipoAcesso,String bandeja);

    public List<EtiquetaBandejaEnBean> getListEtiquetaBandejaEntrada(String coDep, String coEmp);

}