/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.consultadoc.service;

import java.util.List; 
import pe.gob.onpe.consultadoc.bean.TipoDocBean;
import pe.gob.onpe.consultadoc.bean.ConsultaDocBean;

/**
 *
 * @author RATAYAURI
 */
public interface ConsultaDocService {
    public List<ConsultaDocBean> getBuscarExpediente(String numExpediente); 
    public List<String> SeguimientoOficinaAtencion(String filtro);
}
