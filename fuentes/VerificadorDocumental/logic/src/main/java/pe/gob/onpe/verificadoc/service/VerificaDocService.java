/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.verificadoc.service;

import java.util.List;
import pe.gob.onpe.verificadoc.bean.AnexoBean;
import pe.gob.onpe.verificadoc.bean.TipoDocBean;
import pe.gob.onpe.verificadoc.bean.VerificaDocBean;

/**
 *
 * @author RATAYAURI
 */
public interface VerificaDocService {
    public List<TipoDocBean> getTiposDocs();
    public VerificaDocBean getDocRemitos(VerificaDocBean doc);
    public String getCodVerifica(String nuAnn, String nuEmi);
    public String getDescTipoDoc(String codTipoDoc);
    public VerificaDocBean getDocRemitosBusq(String nuEmi, String nuAnno);
    public void limpiaMapDocsAccedidos();
    public List<AnexoBean> getAnexosList(String nuAnn, String nuEmi);
}
