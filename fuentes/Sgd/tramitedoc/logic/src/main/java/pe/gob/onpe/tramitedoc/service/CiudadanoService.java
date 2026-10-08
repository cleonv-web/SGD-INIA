/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;

import java.util.List;
import pe.gob.onpe.tramitedoc.bean.CitizenBean;
import pe.gob.onpe.tramitedoc.util.FiltroPaginate;
import pe.gob.onpe.tramitedoc.util.ProcessResult;

/**
 *
 * @author RBerrocal
 */
public interface CiudadanoService {
    public List<CitizenBean> getCiudadanoList();
    public List<CitizenBean> getCiudadanoBusqList(String busCiudDocumento, 
                                                  String busCiudApPaterno, 
                                                  String busCiudApMaterno, 
                                                  String busCiudNombres ,String pUsuario);
    public ProcessResult<List<CitizenBean>> getCiudadanoList(String busCiudDocumento, 
                                                  String busCiudApPaterno, 
                                                  String busCiudApMaterno, 
                                                  String busCiudNombres ,String pUsuario,FiltroPaginate paginate);
    public String insCiudadano(CitizenBean ciudadano, String codUsuario);
    public CitizenBean getCiudadano(String codCiudadano);
    public String updCiudadano(CitizenBean ciudadano, String codUsuario);
}
