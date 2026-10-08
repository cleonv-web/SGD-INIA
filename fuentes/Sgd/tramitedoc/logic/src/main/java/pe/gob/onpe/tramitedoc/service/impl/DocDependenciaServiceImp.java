/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.bean.MotivoBean;
import pe.gob.onpe.tramitedoc.bean.MotivoDocumentoDependenciaBean;
import pe.gob.onpe.tramitedoc.dao.DocDependenciaDao;
import pe.gob.onpe.tramitedoc.service.DocDependenciaService;
import pe.gob.onpe.tramitedoc.util.Paginacion;

/**
 *
 * @author ECueva
 */
@Service("docDependenciaService")
public class DocDependenciaServiceImp implements DocDependenciaService {

    @Autowired
    private DocDependenciaDao docDependenciaDao;

//    @Override
//    public List<DocumentoDependenciaBean> getAllDocXDependencia(String codDependencia, Paginacion paginacion) {
//        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
//        List<DocumentoDependenciaBean> list = null;
//        try {
//            list = docDependenciaDao.getAllDocXDependencia(codDependencia, paginacion);
//            paginacion.setNumeroTotalRegistros(docDependenciaDao.getRowCountDocXDependencia(codDependencia));
//        } catch (Exception ex) {
//            ex.printStackTrace();
//        }
//
//        return list;
//    }

    @Override
    public DocumentoDependenciaBean getDocDependencia(String codDependencia, String codTipDoc) {
        DocumentoDependenciaBean documentoDependenciaBean = null;
        try {
            documentoDependenciaBean = docDependenciaDao.getDocDependencia(codDependencia, codTipDoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return documentoDependenciaBean;
    }

//    @Override
//    public String updDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean) {
//        String vReturn = "NO_OK";
//        try {
//            vReturn = docDependenciaDao.updDocXDependencia(documentoDependenciaBean);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return vReturn;
//    }

//    @Override
//    public String insDocXDependencia(DocumentoDependenciaBean documentoDependenciaBean) {
//        String vReturn = "NO_OK";
//        try {
//            vReturn = docDependenciaDao.insDocXDependencia(documentoDependenciaBean);
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        return vReturn;
//    }

    @Override
    public List<DocumentoDependenciaBean> getAllDocXDependencia(String codDependencia) {
        List<DocumentoDependenciaBean> list = null;
        try {
            //list = docDependenciaDao.getAllDocXDependencia(codDependencia);
            list = docDependenciaDao.getDocDependenciaList(codDependencia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<DocumentoDependenciaBean> getDocDependenciaList(String codDependencia) {
        List<DocumentoDependenciaBean> list = null;
        try {
            list = docDependenciaDao.getDocDependenciaList(codDependencia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public List<DocumentoDependenciaBean> getDocDependenciaFaltantesList(String codDependencia) {
        List<DocumentoDependenciaBean> list = null;
        try {
            list = docDependenciaDao.getDocDependenciaFaltantesList(codDependencia);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    public String insDocDependencia(DocumentoDependenciaBean documentoDependenciaBean,String coUsuario) {
        String vReturn = "NO_OK";
        try {
            documentoDependenciaBean.setEsEli("0");
            documentoDependenciaBean.setCoUseCre(coUsuario);
            documentoDependenciaBean.setCoUseMod(coUsuario);
//            documentoDependenciaBean.setEsOblCarga("0");
            documentoDependenciaBean.setEsOblFirma("1");
//            documentoDependenciaBean.setInGeneOfic("0");

            vReturn = docDependenciaDao.insDocDependencia(documentoDependenciaBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public String eliDocDependencia(DocumentoDependenciaBean documentoDependenciaBean) {
        String vReturn = "NO_OK";
        try {
            vReturn = docDependenciaDao.eliDocDependencia(documentoDependenciaBean);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }

    public String updDocDependencia(DocumentoDependenciaBean documentoDependenciaBean, String codDocReempl,String coUsuario) {
        String vReturn = "NO_OK";
        try {
            documentoDependenciaBean.setCoUseMod(coUsuario);
            vReturn = docDependenciaDao.updDocDependencia(documentoDependenciaBean, codDocReempl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    public List<MotivoBean> getMotDependenciaList(String codDependencia, String codDoc){
        List<MotivoBean> list = null;
        try {
            list = docDependenciaDao.getMotDependenciaList(codDependencia,codDoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public List<MotivoBean> getMotFaltantesList(String codDependencia,String codDoc) {
        List<MotivoBean> list = null;
        try {
            list = docDependenciaDao.getMotFaltantesList(codDependencia,codDoc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    public String insMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep) {
        String vReturn = "NO_OK";
        try {
            vReturn = docDependenciaDao.insMotDocDependencia(motDocDep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    public String updMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep,String codMotReempl) {
        String vReturn = "NO_OK";
        try {
            vReturn = docDependenciaDao.updMotDocDependencia(motDocDep,codMotReempl);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    public String eliMotDocDependencia(MotivoDocumentoDependenciaBean motDocDep) {
        String vReturn = "NO_OK";
        try {
            vReturn = docDependenciaDao.eliMotDocDependencia(motDocDep);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
}
