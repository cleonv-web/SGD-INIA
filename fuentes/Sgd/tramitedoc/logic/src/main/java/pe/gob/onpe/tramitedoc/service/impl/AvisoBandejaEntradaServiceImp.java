/**
 *
 */
package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import pe.gob.onpe.tramitedoc.bean.AvisoBandejaEntradaBean;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBandejaEnBean;
import pe.gob.onpe.tramitedoc.dao.AvisoBandejaEntradaDao;
import pe.gob.onpe.tramitedoc.service.AvisoBandejaEntradaService;

/**
 * @author ecueva
 *
 */
@Service("avisoBandejaEntradaService")
public class AvisoBandejaEntradaServiceImp implements AvisoBandejaEntradaService {

    @Autowired
    private AvisoBandejaEntradaDao avisoBandejaEntradaDao;

    /* (non-Javadoc)
     * @see pe.gob.onpe.tramitedoc.core.impl.AvisoBandejaEntradaService#getBandejaEntrada(java.lang.String)
     */
    //@Override
    @Override
    public List<AvisoBandejaEntradaBean> getBandejaEntrada(String coDependencia, String coEmpleado, String tipoAcesso,String bandeja) {
        List<AvisoBandejaEntradaBean> list = null;
        try {
            if (tipoAcesso.equals("0")) {
                list = avisoBandejaEntradaDao.getBandejaEntradaAccesoTotal(coDependencia,coEmpleado,bandeja);
            } /*else if (tipoAcesso.equals("9")) {
                list = avisoBandejaEntradaDao.getBandejaEntradaAltaDireccionTotal(coDependencia, coEmpleado);
            }  else if (tipoAcesso.equals("2")) {
                list = avisoBandejaEntradaDao.getBandejaEntradaAccesoFuncionario(coDependencia, coEmpleado);
            } */
            else {
                list = avisoBandejaEntradaDao.getBandejaEntradaAccesoPersonal(coDependencia, coEmpleado,bandeja);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }

    @Override
    public List<EtiquetaBandejaEnBean> getListEtiquetaBandejaEntrada(String coDep, String coEmp) {
        List<EtiquetaBandejaEnBean> list = null;
        try {
            list = avisoBandejaEntradaDao.getListEtiquetaBandejaEntrada(coDep, coEmp);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        return list;
    }
}
