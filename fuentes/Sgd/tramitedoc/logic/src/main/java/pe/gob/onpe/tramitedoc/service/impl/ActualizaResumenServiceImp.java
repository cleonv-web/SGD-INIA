/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramitedoc.service.impl;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import pe.gob.onpe.tramitedoc.bean.DestinoBean;
import pe.gob.onpe.tramitedoc.bean.ReferenciaRemitoBean;
import pe.gob.onpe.tramitedoc.dao.EmiDocumentoAdmDao;
import pe.gob.onpe.tramitedoc.service.ActualizaResumenService;


/**
 *
 * @author ecueva
 */
@Service("actualizaResumenService")
public class ActualizaResumenServiceImp implements ActualizaResumenService{

    @Autowired
    private EmiDocumentoAdmDao emiDocumentoAdmDao;
    
    @Override
    public String updRemitosResumenDes(String pnuAnn, String pnuEmi){
        String vReturn = "NO_OK";
        String vti_des = ti_co_dep_des_v(pnuAnn, pnuEmi);
        String vco_pri = ti_des_est_v(pnuAnn, pnuEmi);
        String vnu_cant_des = nu_cant_des(pnuAnn, pnuEmi);
        String vresOriDes = resumenOriDestinos(pnuAnn, pnuEmi);
        
        if (vti_des != null) {
            vti_des = vti_des.trim();
            if (vti_des.length() >= 100) {
                vti_des = vti_des.substring(1, 100);
            }
        }
        if (vco_pri != null) {
            vco_pri = vco_pri.trim();
        }
        if (vresOriDes != null) {
            vresOriDes = vresOriDes.trim();
            if (vresOriDes.length() > 399) {
                vresOriDes = vresOriDes.substring(0, 399);
            }
        }        
        vReturn = emiDocumentoAdmDao.updRemitoResumenDestinatario(pnuAnn, pnuEmi, vti_des, vco_pri, vnu_cant_des, vresOriDes );
//        if ("NO_OK".equals(vReturn)) {
//            throw new validarDatoException("Error Actualizando Remito resumen destinatario");
//        }
        return vReturn;
    }

    @Override
    public String updRemitosResumenRef(String pnuAnn, String pnuEmi){
        String vReturn = "NO_OK";
        String vti_ori = ti_emi_ref(pnuAnn, pnuEmi);
        String vde_ori_emi = resumenOriReferencia(pnuAnn, pnuEmi);
        if (vti_ori != null) {
            vti_ori = vti_ori.trim();
            if (vti_ori.length() >= 100) {
                vti_ori = vti_ori.substring(1, 100);
            }
        }
        
        vReturn = emiDocumentoAdmDao.updRemitoResumenReferencia(pnuAnn, pnuEmi, vti_ori, vde_ori_emi);
        return vReturn;
    }
    
    private String ti_co_dep_des_v(String nu_ann, String nu_emi) {
        String vReturn = "";
        List<DestinoBean> destinatarios = getDestinosListCodDepTipoDes(nu_ann, nu_emi);
        for (DestinoBean destinatario : destinatarios) {
            if (vReturn != "") {
                vReturn = vReturn + ";\r\n";
            }
            if ("01".equals(destinatario.getTiDes())) {
                vReturn = vReturn + destinatario.getCoDepDes();
            }
        }
        return vReturn;
    }
    
    private List<DestinoBean> getDestinosListCodDepTipoDes(String nu_ann, String nu_emi) {
        List<DestinoBean> list = null;
        try {
            list = emiDocumentoAdmDao.getListaDestinosCodDepTipoDes(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    private String ti_des_est_v(String nu_ann, String nu_emi) {
        String vReturn = "1";
        List<DestinoBean> destinatarios = getDestinosListCodPri(nu_ann, nu_emi);
        for (DestinoBean destinatario : destinatarios) {
            vReturn = destinatario.getCoPri();
            break;
        }
        return vReturn;
    }
    
    private List<DestinoBean> getDestinosListCodPri(String nu_ann, String nu_emi) {
        List<DestinoBean> list = null;
        try {
            list = emiDocumentoAdmDao.getListaDestinosCodPri(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }
    
    private String nu_cant_des(String nu_ann, String nu_emi) {
        String vReturn = "0";
        vReturn = emiDocumentoAdmDao.getNumDestinos(nu_ann, nu_emi);
        return vReturn;
    }    
    
    private String ti_emi_ref(String nu_ann, String nu_emi) {
        String vReturn = "";
        List<ReferenciaRemitoBean> referenciasRemitos = getReferenciaRemitoList(nu_ann, nu_emi);
        for (ReferenciaRemitoBean referenciaRemito : referenciasRemitos) {
            if (vReturn != "") {
                vReturn = vReturn + ";\r\n";
            }
            if ("01".equals(referenciaRemito.getTi_emi())) {
                vReturn = vReturn + referenciaRemito.getCo_dep_emi();
            } else if ("02".equals(referenciaRemito.getTi_emi())) {
                vReturn = vReturn + "P";
            } else if ("03".equals(referenciaRemito.getTi_emi())) {
                vReturn = vReturn + "C";
            } else if ("04".equals(referenciaRemito.getTi_emi())) {
                vReturn = vReturn + "O";
            } else if ("05".equals(referenciaRemito.getTi_emi())) {
                vReturn = vReturn + "F";
            }
        }
        return vReturn;
    }
    
    private List<ReferenciaRemitoBean> getReferenciaRemitoList(String nu_ann, String nu_emi) {
        List<ReferenciaRemitoBean> list = null;
        try {
            list = emiDocumentoAdmDao.getListaReferenciaRemitos(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }    
    
    private String resumenOriDestinos(String nu_ann, String nu_emi) {
        String vReturn = "";
        List<DestinoBean> destinatarios = getOriDestinosList(nu_ann, nu_emi);
        for (DestinoBean destinatario : destinatarios) {
            if (vReturn != "") {
                vReturn = vReturn + "\r\n";
            }
            
            // Verificamos los datos de Destinos diferentes.
            if (destinatario.getTiDes().equals("02") || destinatario.getTiDes().equals("03") || destinatario.getTiDes().equals("04")  ) {
                vReturn = vReturn + destinatario.getCoDepDes();
            }
        }
        return vReturn;
    }
    
    private List<DestinoBean> getOriDestinosList(String nu_ann, String nu_emi) {
        List<DestinoBean> list = null;
        try {
            list = emiDocumentoAdmDao.getListaDestinosOriTipoDes(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }

    private String resumenOriReferencia(String nu_ann, String nu_emi) {
        String vReturn = "";
        List<ReferenciaRemitoBean> referenciasRemitos = getOriReferenciaList(nu_ann, nu_emi);
        for (ReferenciaRemitoBean referenciaRemito : referenciasRemitos) {
            if (vReturn != "") {
                vReturn = vReturn + "\r\n";
            }
            if (referenciaRemito.getTi_emi().equals("02") || referenciaRemito.getTi_emi().equals("03") || referenciaRemito.getTi_emi().equals("04")  ) {
                vReturn = vReturn + referenciaRemito.getCo_dep_emi();
            }
        }
        return vReturn;
    }
    
    private List<ReferenciaRemitoBean> getOriReferenciaList(String nu_ann, String nu_emi) {
        List<ReferenciaRemitoBean> list = null;
        try {
            list = emiDocumentoAdmDao.getOriReferenciaLista(nu_ann, nu_emi);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return list;
    }    
    
    
}
