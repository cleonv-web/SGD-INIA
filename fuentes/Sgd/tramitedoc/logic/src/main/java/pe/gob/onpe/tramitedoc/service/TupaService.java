/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service;
import java.util.List;
import pe.gob.onpe.tramitedoc.bean.TupaBean;
/**
 *
 * @author RBerrocal
 */
public interface TupaService {
    public List<TupaBean> getTupaList();
    public List<TupaBean> getTupaBusqList(String busTupaCorto, String busTupaDescripcion);
    public String insTupa(TupaBean tupa, String codusuario);
    public TupaBean getTupa(String codTupa);
    public String updTupa(TupaBean tupa, String codusuario);
}
