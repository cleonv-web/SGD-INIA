/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtask.dao;

import java.util.List;
import pe.gob.onpe.sgdtask.bean.DocumentosVbBean;
import pe.gob.onpe.sgdtask.bean.NotificacionLogBean;
import pe.gob.onpe.sgdtask.bean.PersonalVbBean;

/**
 *
 * @author FSilva
 */
public interface PersonalVbDao {    
    //Lista de Personal que tiene documentos pendientes de VB
    public List<PersonalVbBean> getListPersonalNoVB();
    //Lista Pe5rsonal que tiene documentos pendidentes de VB por Documento
    public List<PersonalVbBean> getListPersonalNoVBPorDocumento(String nuAnn, String nuEmi);
    //Lista de Documentos pendientes de VB de un Personal
    public List<DocumentosVbBean> getLisDocsPersonalNoVB(String coEmpVb);
}
