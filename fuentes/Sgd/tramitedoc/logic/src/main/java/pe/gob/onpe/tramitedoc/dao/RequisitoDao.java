/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.dao;
import java.util.List;
import pe.gob.onpe.tramitedoc.bean.RequisitoBean;
/**
 *
 * @author JHuamanF
 */
public interface RequisitoDao {
    
    public List<RequisitoBean> getRequisitoList();
    
    public List<RequisitoBean> getRequisitoBusqList(RequisitoBean requisito);
    
    public String insRequisito(RequisitoBean requisito, String codusuario);
    
    public RequisitoBean getRequisito(String codRequisito);
    
    public String updRequisito(RequisitoBean requisito, String codusuario);
    
    public String insTupaRequisito(RequisitoBean requisito, String codusuario);
    
    public String updTupaRequisito(RequisitoBean requisito, String codusuario);
    
    public String eliTupaRequisito(RequisitoBean requisito);
    
    public List<RequisitoBean> getTupaRequisitoBusqList(RequisitoBean requisito);
    
    public List<RequisitoBean> getExpedienteRequisitoList(String codRequisito, String codProceso);
    
    public String eliExpedienteRequisito(String codRequisito, String codProceso);
    
    public String insExpedienteRequisito(RequisitoBean requisito, String codusuario);
    
}
