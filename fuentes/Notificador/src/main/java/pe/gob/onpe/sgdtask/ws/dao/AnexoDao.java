/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.ws.dao;

import java.util.List;
import pe.gob.onpe.sgdtask.ws.bean.AnexoBean;

/**
 *
 * @author FSilva
 */
public interface AnexoDao {
    //Obtienen la lista de anexos en base al codigo de recepcion
    public List<AnexoBean> getLisAnexos(long coRecepcion);
    List<String> getCanDocAnexos(String coDocExterno);
}
