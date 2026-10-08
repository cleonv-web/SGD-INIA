/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.consultadoc.dao;

import java.util.List;  
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocSeguimientoBean;

/**
 *
 * @author RATAYAURI
 */
public interface ConsultaDocDao {
    
    public List<ConsultaDocBean> getBuscarExpediente(String numExpediente); 
    public List<ConsultaDocSeguimientoBean> getBuscarExpedienteSeguimiento(String filtro);
}
