package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBean;
import pe.gob.onpe.tramitedoc.dao.EtiquetaDocumentoDao;
import pe.gob.onpe.tramitedoc.service.EtiquetaDocumentoService;

/**
 *
 * @author NGilt
 */
@Service("etiquetaDocumentoService")
public class EtiquetaDocumentoServiceImp implements EtiquetaDocumentoService{
    @Autowired
    private EtiquetaDocumentoDao etiquetaDocumentoDao;

    
    public List<EtiquetaBean> getListEtiquetas() {
        List<EtiquetaBean> list=null;
        try {
            list=etiquetaDocumentoDao.getListEtiquetas();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
}
