/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.CargoEntregaBean;
import pe.gob.onpe.tramitedoc.bean.DetGuiaMesaPartesBean;
import pe.gob.onpe.tramitedoc.bean.DocPedienteEntregaBean;
import pe.gob.onpe.tramitedoc.bean.GuiaMesaPartesBean;
import pe.gob.onpe.tramitedoc.dao.CargoEntregaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;

/**
 *
 * @author ecueva
 */
@Repository("cargoEntregaDao")
public class CargoEntregaDaoImp extends SimpleJdbcDaoBase implements CargoEntregaDao {

    @Override
    public List<CargoEntregaBean> getCargosEntrega(CargoEntregaBean cargo) {
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<CargoEntregaBean> list;

        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");
        sql.append(" SELECT A.NU_ANN_GUI NU_ANN_GUIA, A.NU_GUI NU_GUIA,A.NU_COR_GUI NU_COR_GUIA,TO_CHAR(A.FE_GUI_MP,'DD/MM/YYYY') FE_GUIA_MP_CORTA,");
        sql.append(" PK_SGD_DESCRIPCION.DE_LOCAL(A.CO_LOC_DES) DE_LOC_DES,");
        sql.append(" PK_SGD_DESCRIPCION.DE_DEPENDENCIA(A.CO_DEP_DES) DE_DEP_DES,");
        sql.append(" A.ES_GUI_MP ESTADO_GUIA_MP,");
        sql.append(" PK_SGD_DESCRIPCION.ESTADOS(A.ES_GUI_MP,'TDTC_GUIA_MP') DE_ESTADO_GUIA_MP");
        sql.append(" FROM TDTC_GUIA_MP A");
        sql.append(" WHERE");
        sql.append(" A.ES_ELI = '0'");
        /*sql.append(" AND A.CO_LOC_ORI=:pcoLocOri");
        objectParam.put("pcoLocOri", cargo.getCoLocOri());*/
//        String pNUAnn = cargo.getNuAnnGuia();
//        if (!(cargo.getEsFiltroFecha().equals("1") && pNUAnn.equals("0"))) {
//            sql.append(" AND A.NU_ANN_GUI = :pNuAnn");
//            objectParam.put("pNuAnn", pNUAnn);
//        }
        sql.append(" AND A.CO_DEP_ORI=:pcoDepOri");
        objectParam.put("pcoDepOri", cargo.getCoDepOri());

        //Filtro
        if (cargo.getCoLocDes()!= null && cargo.getCoLocDes().trim().length() > 0) {
            sql.append(" AND A.CO_LOC_DES = :pcoLocDes ");
            objectParam.put("pcoLocDes", cargo.getCoLocDes());
        }
        if (cargo.getCoDepDes()!= null && cargo.getCoDepDes().trim().length() > 0) {
            sql.append(" AND A.CO_DEP_DES = :pcoDepDes ");
            objectParam.put("pcoDepDes", cargo.getCoDepDes());
        }
        if (cargo.getEstadoGuiaMp()!= null && cargo.getEstadoGuiaMp().trim().length() > 0) {
            sql.append(" AND A.ES_GUI_MP = :pestadoGuia ");
            objectParam.put("pestadoGuia", cargo.getEstadoGuiaMp());
        }
        if (cargo.getEsFiltroFecha() != null
                && (cargo.getEsFiltroFecha().equals("1") || cargo.getEsFiltroFecha().equals("2") || cargo.getEsFiltroFecha().equals("3"))) {
            String vFechaIni = cargo.getFeGuiaIni();
            String vFechaFin = cargo.getFeGuiaFin();
            if (vFechaIni != null && vFechaIni.trim().length() > 0
                    && vFechaFin != null && vFechaFin.trim().length() > 0) {
                sql.append(" AND A.FE_GUI_MP between TO_DATE(:pfechaIni,'dd/mm/yyyy') AND TO_DATE(:pfechaFin,'dd/mm/yyyy') + 0.99999");
                objectParam.put("pfechaIni", vFechaIni);
                objectParam.put("pfechaFin", vFechaFin);
            }
        }

        //sql.append(" ORDER BY A.NU_COR_EMI DESC");
        sql.append(") A ");
        sql.append("WHERE ROWNUM < 51");

        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(CargoEntregaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }

    @Override
    public List<DocPedienteEntregaBean> getDocsPendienteEntrega(DocPedienteEntregaBean busqDoc) {
        boolean bBusqFiltro=false;
        StringBuffer sql = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();

        List<DocPedienteEntregaBean> list;

        sql.append("SELECT A.*, ROWNUM");
        sql.append(" FROM ( ");        
        sql.append(" SELECT A.NU_ANN,A.NU_EMI,B.NU_DES,A.NU_COR_EMI,PK_SGD_DESCRIPCION.TI_EMI_EMP(A.NU_ANN, A.NU_EMI) DE_ORI_EMI,");
        sql.append(" TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,PK_SGD_DESCRIPCION.DE_DOCUMENTO(A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,");
        sql.append(" C.NU_DOC,PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.CO_DEP_DES) DE_DEP_DES,");
        sql.append(" DECODE (B.TI_DES,'01', PK_SGD_DESCRIPCION.DE_NOM_EMP (B.CO_EMP_DES),'02', PK_SGD_DESCRIPCION.DE_NOM_EMP (B.NU_RUC_DES), '03', PK_SGD_DESCRIPCION.ANI_SIMIL (B.NU_DNI_DES), '04', PK_SGD_DESCRIPCION.OTRO_ORIGEN (B.CO_OTR_ORI_DES)) DE_EMP_DES,");
        sql.append(" A.DE_ASU"); //Se añade para considerar el asunto.
        sql.append(" FROM TDTV_REMITOS A, TDTV_DESTINOS B, TDTX_REMITOS_RESUMEN C");
        sql.append(" WHERE"); 
        sql.append(" B.NU_ANN = A.NU_ANN");
        sql.append(" AND B.NU_EMI = A.NU_EMI");
        sql.append(" AND C.NU_ANN = B.NU_ANN");
        sql.append(" AND C.NU_EMI = B.NU_EMI");
        String pNUAnn = busqDoc.getNuAnn();
        if(!(busqDoc.getFiltroFecha().equals("1") && pNUAnn.equals("0"))){
            sql.append(" AND A.NU_ANN = :pNuAnn");
            // Parametros Basicos
            objectParam.put("pNuAnn", pNUAnn);                
        }
        
        sql.append(" AND A.CO_DEP_EMI = :pcoDepEmi");
        objectParam.put("pcoDepEmi", busqDoc.getCoDepEmi());                
        /*sql.append(" AND A.CO_LOC_EMI = :pcoLocEmi");
        objectParam.put("pcoLocEmi", busqDoc.getCoLocEmi());             */

        sql.append(" AND A.ES_ELI = '0'");
        sql.append(" AND B.ES_ELI = '0'"); 
        sql.append(" AND A.TI_EMI NOT IN ('01','05')");
        sql.append(" AND A.ES_DOC_EMI NOT IN ('5', '9', '7')");
        sql.append(" AND A.IN_OFICIO = '0'");
        sql.append(" AND B.ES_ENT_MP='0'");
        
        
        String pTipoBusqueda = busqDoc.getTipoBusqueda();
        if(pTipoBusqueda.equals("1") && busqDoc.isEsIncluyeFiltro()){
            bBusqFiltro=true;
        }         
        
//        if (buscarDocumentoRecep.getsDestinatario()!=null && buscarDocumentoRecep.getsDestinatario().trim().length()>0 && (pTipoBusqueda.equals("0") || bBusqFiltro)){
//            sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
//            objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsDestinatario());
//        }else {    
//            if(buscarDocumentoRecep.getsTiAcceso().equals("1")){
//                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes ");
//                objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsCoEmpleado());
//            }else if(buscarDocumentoRecep.getsTiAcceso().equals("2")){
//                sql.append(" AND B.CO_EMP_DES = :pcoEmpDes  ");
//                objectParam.put("pcoEmpDes", buscarDocumentoRecep.getsCoEmpleado());
//            }
//        }
        
       
        //Filtro
        if(pTipoBusqueda.equals("0") || bBusqFiltro){
            if (busqDoc.getCoTipDocAdm()!= null && busqDoc.getCoTipDocAdm().trim().length()>0){
               sql.append(" AND A.CO_TIP_DOC_ADM = :pCoTipDocAdm ");
               objectParam.put("pCoTipDocAdm", busqDoc.getCoTipDocAdm());
            }
            if (busqDoc.getCoLocDes()!= null && busqDoc.getCoLocDes().trim().length()>0){
               sql.append(" AND B.CO_LOC_DES = :pCoLocDes ");
               objectParam.put("pCoLocDes", busqDoc.getCoLocDes());
            }
            if (busqDoc.getCoDepDes()!= null && busqDoc.getCoDepDes().trim().length()>0){
               sql.append(" AND B.CO_DEP_DES = :pCoDepDes ");
               objectParam.put("pCoDepDes", busqDoc.getCoDepDes());
            }
//            if (busqDoc.getCoEmpDes()!= null && busqDoc.getCoEmpDes().trim().length()>0){
//               sql.append(" AND B.CO_EMP_DES = :pCoEmpDes ");
//               objectParam.put("pCoEmpDes", busqDoc.getCoEmpDes());
//            }
            if(busqDoc.getFiltroFecha()!= null && 
               (busqDoc.getFiltroFecha().equals("1") || busqDoc.getFiltroFecha().equals("3"))){
                String vFeEmiIni = busqDoc.getFeEmiIni();
                String vFeEmiFin = busqDoc.getFeEmiFin();       
                if(vFeEmiIni!= null && vFeEmiIni.trim().length()>0 &&
                   vFeEmiFin!= null && vFeEmiFin.trim().length()>0){
                    sql.append(" AND A.FE_EMI between TO_DATE(:pFeEmiIni,'dd/mm/yyyy') AND TO_DATE(:pFeEmiFin,'dd/mm/yyyy') + 0.99999"); 
                    objectParam.put("pFeEmiIni", vFeEmiIni);
                    objectParam.put("pFeEmiFin", vFeEmiFin);                    
                }
            }            
        }    

        if (pTipoBusqueda.equals("1"))
        {
            if (busqDoc.getNuCorEmi()!= null && busqDoc.getNuCorEmi().trim().length()>0){
               sql.append(" AND A.NU_COR_EMI = :pnuCorEmi ");
               objectParam.put("pnuCorEmi", busqDoc.getNuCorEmi());
            }

            if (busqDoc.getNuDoc()!= null && busqDoc.getNuDoc().trim().length()>0){
               sql.append(" AND UPPER(C.NU_DOC) LIKE '%'||:pnuDoc||'%' "); //rbn Se coloca % para que haga correctamente el filtro por este asunto
               objectParam.put("pnuDoc", busqDoc.getNuDoc());
            }

            // Busqueda del Asunto
            if (busqDoc.getDeAsu()!= null && busqDoc.getDeAsu().trim().length()>0){
               sql.append(" AND UPPER(A.DE_ASU) LIKE '%'||:pDeAsunto||'%' ");
               objectParam.put("pDeAsunto", busqDoc.getDeAsu());
            }
            
        }        
        
        //sql.append(" ORDER BY A.FE_EMI DESC");
        sql.append(") A ");
        sql.append("WHERE ROWNUM < 51");
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(DocPedienteEntregaBean.class));
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;        
    }
    
    @Override
    public String isDocPendienteEnGuiaMp(String pnuAnn,String pnuEmi,String pnuDes){
        String vReturn="1";//duplicado "si".
        try {
            vReturn = this.jdbcTemplate.queryForObject("select count(1) from TDTD_GUIA_MP GD,TDTC_GUIA_MP G\n" +
                      "where GD.NU_ANN_GUI=G.NU_ANN_GUI\n" +
                        "AND GD.NU_GUI=G.NU_GUI\n" +
                        "AND GD.NU_ANN_DES=?\n" +
                        "AND GD.NU_EMI=?\n" +
                        "AND GD.NU_DES=?\n" +
                        "AND G.ES_GUI_MP<>9\n" +
                        "AND G.ES_ELI = '0'", 
                    String.class, new Object[]{pnuAnn, pnuEmi,pnuDes});
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return vReturn;
    }
    
    @Override
    public DocPedienteEntregaBean getDocPendienteEntrega(String pnuAnn,String pnuEmi,String pnuDes){
        StringBuffer sql = new StringBuffer();
        DocPedienteEntregaBean doc = null;
        sql.append("SELECT A.NU_ANN,A.NU_EMI,B.NU_DES,C.NU_EXPEDIENTE,PK_SGD_DESCRIPCION.DE_DOCUMENTO(A.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
                    "C.NU_DOC,TO_CHAR(A.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.CO_DEP_DES) DE_DEP_DES,\n" +
                    "PK_SGD_DESCRIPCION.TI_EMI_EMP(A.NU_ANN, A.NU_EMI) DE_ORI_EMI\n" +
                    "FROM TDTV_REMITOS A, TDTV_DESTINOS B, TDTX_REMITOS_RESUMEN C\n" +
                    "WHERE A.NU_ANN=? AND A.NU_EMI=?\n" +
                    "AND B.NU_ANN=A.NU_ANN AND B.NU_EMI=A.NU_EMI AND B.NU_DES=?\n" +
                    "AND C.NU_ANN=B.NU_ANN AND C.NU_EMI=B.NU_EMI\n" +
                    "AND A.TI_EMI NOT IN ('01','05')\n" +
                    "AND A.ES_DOC_EMI NOT IN ('5', '9', '7')\n" +
                    "AND A.IN_OFICIO = '0'\n" +
                    "AND A.ES_ELI='0'AND B.ES_ELI='0' AND B.ES_ENT_MP='0'");
        
        try {
            doc = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocPedienteEntregaBean.class),
                    new Object[]{pnuAnn,pnuEmi,pnuDes});            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;        
    }
    
    @Override
    public String insGuiaMp(GuiaMesaPartesBean guia){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO TDTC_GUIA_MP(\n"
                + "NU_ANN_GUI,NU_GUI,\n"
                + "CO_LOC_ORI,CO_DEP_ORI,\n"
                + "CO_LOC_DES,CO_DEP_DES,\n"
                + "ES_GUI_MP,FE_GUI_MP,\n"
                + "DE_OBS,NU_COR_GUI,\n"
                + "ES_ELI,CO_USE_MOD,\n"
                + "FE_USE_MOD,CO_USE_CRE,\n"
                + "FE_USE_CRE)\n"
                + "VALUES(?,?,?,?,?,?,'0',TO_DATE(?,'DD/MM/YYYY HH24:MI'),?,?,'0',?,SYSDATE,?,SYSDATE)");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{guia.getNuAnn(),guia.getNuGuia(),guia.getCoLocOri(),guia.getCoDepOri(),
            guia.getCoLocDes(),guia.getCoDepDes(),guia.getFeGuiMp(),guia.getDeObs(),guia.getNuCorGui(),guia.getCoUseMod(),guia.getCoUseMod()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String updGuiaMp(GuiaMesaPartesBean guia){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("UPDATE TDTC_GUIA_MP\n"
                + "SET FE_GUI_MP=to_date(?,'dd/mm/yyyy hh24:mi:ss')\n"
                + ",DE_OBS=?\n"
                + ",FE_USE_MOD=SYSDATE\n"
                + ",CO_USE_MOD=?\n"
                + "WHERE\n"
                + "NU_ANN_GUI=? AND NU_GUI=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{guia.getFeGuiMp(),
            guia.getDeObs(),guia.getCoUseMod(),guia.getNuAnn(),guia.getNuGuia()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;        
    }
    
    @Override
    public String insDetGuiaMp(DetGuiaMesaPartesBean detGuia){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();
        sqlUpd.append("INSERT INTO TDTD_GUIA_MP(\n"
                + "NU_ANN_GUI,NU_GUI,\n"
                + "NU_COR,NU_ANN_DES,\n"
                + "NU_EMI,NU_DES,\n"
                + "ES_DET_GUI,ES_ELI,\n"
                + "FE_USE_MOD,CO_USE_MOD,\n"
                + "CO_USE_CRE,FE_USE_CRE)\n"
                + "VALUES(?,?,?,?,?,?,'0','0',SYSDATE,?,?,SYSDATE)");

        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{detGuia.getNuAnnGui(),detGuia.getNuGui(),detGuia.getNuCor(),
            detGuia.getNuAnn(),detGuia.getNuEmi(),detGuia.getNuDes(),detGuia.getCoUseMod(),detGuia.getCoUseMod()});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return vReturn;
    }
    
    @Override
    public String getNroGuiaCabecera(String pnuAnnGuia){
        String vReturn="-1";
        try {
            vReturn = this.jdbcTemplate.queryForObject("SELECT nvl(MAX(to_number(NU_GUI)), 0) + 1\n" +
                                        "FROM TDTC_GUIA_MP\n" +
                                        "WHERE NU_ANN_GUI=?", 
                    String.class, new Object[]{pnuAnnGuia});
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return vReturn;
    }
    
    @Override
    public String getNroCorrelativoGuiaCabecera(String pnuAnnGuia,String pcoDepOri){
        String vReturn="-1";
        try {
            vReturn = this.jdbcTemplate.queryForObject("SELECT nvl(MAX(NU_COR_GUI), 0) + 1\n" +
                                        "FROM TDTC_GUIA_MP\n" +
                                        "WHERE NU_ANN_GUI=?\n" +
                                        "and CO_DEP_ORI=?", 
                    String.class, new Object[]{pnuAnnGuia,pcoDepOri});
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return vReturn;
    }
    
    @Override
    public String getNroCorrelativoDetGuiaCabecera(String pnuAnnGuia, String pnuGuia){
        String vReturn="-1";
        try {
            vReturn = this.jdbcTemplate.queryForObject("SELECT NVL(MAX(NU_COR), 0) + 1\n" +
                            "FROM TDTD_GUIA_MP\n" +
                            "WHERE NU_ANN_GUI=?\n" +
                            "AND NU_GUI=?", 
                    String.class, new Object[]{pnuAnnGuia,pnuGuia});
        } catch (Exception e) {
            e.printStackTrace();
        }        
        return vReturn;        
    }
    
    @Override
    public GuiaMesaPartesBean getGuiaMp(String pnuAnnGuia, String pnuGuia){
        StringBuffer sql = new StringBuffer();
        GuiaMesaPartesBean guia = null;
        sql.append("SELECT NU_ANN_GUI NU_ANN,NU_GUI NU_GUIA,CO_DEP_ORI,CO_LOC_ORI,\n" +
                "CO_LOC_DES,PK_SGD_DESCRIPCION.DE_LOCAL(CO_LOC_DES) DE_LOC_DES,\n" +
                "PK_SGD_DESCRIPCION.DE_LOCAL(CO_LOC_ORI) DE_LOC_ORI,\n" +
                "PK_SGD_DESCRIPCION.DE_DEPENDENCIA(CO_DEP_DES) DE_DEP_DES,\n" +
                "PK_SGD_DESCRIPCION.DE_DEPENDENCIA(CO_DEP_ORI) DE_DEP_ORI,\n" +
                "DE_OBS,TO_CHAR(FE_GUI_MP,'DD/MM/YYYY HH24:MI') FE_GUI_MP,NU_COR_GUI,\n" +
                "ES_GUI_MP ESTADO_GUIA,PK_SGD_DESCRIPCION.ESTADOS(ES_GUI_MP,'TDTC_GUIA_MP') DE_ESTADO_GUIA"
                + " FROM TDTC_GUIA_MP"
                + " WHERE NU_ANN_GUI=? AND NU_GUI=?");
        
        try {
            guia = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(GuiaMesaPartesBean.class),
                    new Object[]{pnuAnnGuia,pnuGuia});            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return guia;           
    }
    
    @Override
    public List<DocPedienteEntregaBean> getDetalleGuiaMp(String pnuAnnGuia,String pnuGuia){
        StringBuffer sql = new StringBuffer();        
        List<DocPedienteEntregaBean> list;
        sql.append("SELECT GD.NU_ANN_DES,GD.NU_EMI,GD.NU_DES,GD.NU_COR NU_COR_EMI,RR.NU_EXPEDIENTE,\n" +
        "PK_SGD_DESCRIPCION.DE_DOCUMENTO(R.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM,\n" +
        "RR.NU_DOC,TO_CHAR(R.FE_EMI,'DD/MM/YY') FE_EMI_CORTA,PK_SGD_DESCRIPCION.DE_DEPENDENCIA(D.CO_DEP_DES) DE_DEP_DES,\n" +
        "PK_SGD_DESCRIPCION.TI_EMI_EMP(R.NU_ANN, R.NU_EMI) DE_ORI_EMI,GD.ES_DET_GUI ES_DET_GUIA,\n" +
        "TO_CHAR(D.FE_REC_DOC,'DD/MM/YY HH24:MI') FE_REC_DOC_CORTA\n" +
        "FROM TDTC_GUIA_MP G,TDTD_GUIA_MP GD,TDTV_REMITOS R,TDTV_DESTINOS D,TDTX_REMITOS_RESUMEN RR\n" +
        "WHERE\n" +
        "G.NU_ANN_GUI = ?\n" +
        "AND G.NU_GUI = ?\n" +
        "AND GD.NU_ANN_GUI=?\n" +
        "AND GD.NU_GUI=?\n" +
        "AND GD.NU_ANN_DES = D.NU_ANN\n" +
        "AND GD.NU_EMI = D.NU_EMI\n" +
        "AND GD.NU_DES = D.NU_DES\n" +
        "AND R.NU_ANN = D.NU_ANN\n" +
        "AND R.NU_EMI = D.NU_EMI\n" +
        "AND RR.NU_ANN=R.NU_ANN\n" +
        "AND RR.NU_EMI=R.NU_EMI\n" +
        "AND G.ES_ELI = '0'");
        try {
            list = this.jdbcTemplate.query(sql.toString(), BeanPropertyRowMapper.newInstance(DocPedienteEntregaBean.class),
                    new Object[]{pnuAnnGuia,pnuGuia,pnuAnnGuia,pnuGuia});
        } catch (EmptyResultDataAccessException e) {
            list = null;
        } catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;          
    }
    
    @Override
    public String updEstadoGuiaMp(String estado, String pcoUseMod, String pnuAnnGuia, String pnuGuia){
        String vReturn = "NO_OK";
        StringBuffer sqlUpd = new StringBuffer();

        sqlUpd.append("UPDATE TDTC_GUIA_MP\n"
                + "SET ES_GUI_MP=?\n"
                + ",FE_USE_MOD=SYSDATE\n"
                + ",CO_USE_MOD=?\n"
                + "WHERE\n"
                + "NU_ANN_GUI=? AND NU_GUI=?");
        try {
            this.jdbcTemplate.update(sqlUpd.toString(), new Object[]{estado,pcoUseMod,pnuAnnGuia,pnuGuia});
            vReturn = "OK";
        } catch (Exception e) {
            e.printStackTrace();
            vReturn = e.getMessage();

        }
        return vReturn;  
    }
    
    @Override
    public DocPedienteEntregaBean getDependenciaDestinoDocExtRec(String pnuAnn, String pnuEmi, String pnuDes){
        StringBuffer sql = new StringBuffer();
        DocPedienteEntregaBean doc = null;
        sql.append("SELECT B.CO_LOC_DES,PK_SGD_DESCRIPCION.DE_LOCAL(CO_LOC_DES) DE_LOC_DES,\n" +
                    "B.CO_DEP_DES,PK_SGD_DESCRIPCION.DE_DEPENDENCIA(B.CO_DEP_DES) DE_DEP_DES\n" +
                    "FROM TDTV_REMITOS A, TDTV_DESTINOS B\n" +
                    "WHERE A.NU_ANN=? AND A.NU_EMI=? \n" +
                    "AND B.NU_ANN=? AND B.NU_EMI=? AND B.NU_DES=?\n" +
                    "AND A.TI_EMI NOT IN ('01','05')\n" +
                    "AND A.ES_DOC_EMI NOT IN ('5', '9', '7')\n" +
                    "AND A.ES_ELI='0'\n" +
                    "AND B.ES_ELI='0'\n" +
                    "AND B.ES_ENT_MP='0'");
        
        try {
            doc = this.jdbcTemplate.queryForObject(sql.toString(), BeanPropertyRowMapper.newInstance(DocPedienteEntregaBean.class),
                    new Object[]{pnuAnn, pnuEmi, pnuAnn, pnuEmi, pnuDes});            
        } catch (Exception e) {
            e.printStackTrace();
        }
        return doc;
    }

    @Override
    public List<CargoEntregaBean> getListaReporteBusqueda(CargoEntregaBean cargo) {
        String vResult;
        StringBuffer prutaReporte = new StringBuffer();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        List list = null;
        
        prutaReporte.append("SELECT	G.NU_ANN_GUI AS nuAnnGuia, ");
        prutaReporte.append("		G.NU_GUI AS nuGuia, ");
        prutaReporte.append("		G.NU_COR_GUI AS nuCorGuia, ");
        prutaReporte.append("		G.CO_LOC_ORI, ");
        prutaReporte.append("		PK_SGD_DESCRIPCION.DE_LOCAL(G.CO_LOC_ORI) DE_LOC_ORI, ");
        prutaReporte.append("		G.CO_LOC_DES, ");
        prutaReporte.append("		PK_SGD_DESCRIPCION.DE_LOCAL(G.CO_LOC_DES) DE_LOC_DES, ");
        prutaReporte.append("		G.CO_DEP_ORI, ");
        prutaReporte.append("		PK_SGD_DESCRIPCION.DE_DEPENDENCIA(G.CO_DEP_ORI) DE_DEP_ORI, ");
        prutaReporte.append("		G.CO_LOC_DES, ");
        prutaReporte.append("		PK_SGD_DESCRIPCION.DE_DEPENDENCIA(G.CO_DEP_DES) DE_DEP_DES_CAB, ");
        prutaReporte.append("		GD.NU_COR, ");
        prutaReporte.append("		RR.NU_EXPEDIENTE, ");
        prutaReporte.append("		CASE R.TI_EMI ");
        prutaReporte.append("			WHEN '01' THEN PK_SGD_DESCRIPCION.DE_NOM_EMP(R.CO_EMP_EMI) ");
        prutaReporte.append("			WHEN '02' THEN PK_SGD_DESCRIPCION.DE_PROVEEDOR(R.NU_RUC_EMI) || '-' || R.NU_RUC_EMI ");
        prutaReporte.append("			WHEN '03' THEN PK_SGD_DESCRIPCION.ANI_SIMIL(R.NU_DNI_EMI) || '-' || R.NU_DNI_EMI ");
        prutaReporte.append("			WHEN '04' THEN PK_SGD_DESCRIPCION.OTRO_ORIGEN(R.CO_OTR_ORI_EMI) ");
        prutaReporte.append("		END DE_ORI_EMI, ");
        prutaReporte.append("		PK_SGD_DESCRIPCION.DE_DOCUMENTO(R.CO_TIP_DOC_ADM) DE_TIP_DOC_ADM, ");
        prutaReporte.append("		RR.NU_DOC, ");
        prutaReporte.append("		CASE d.ti_des ");
        prutaReporte.append("			WHEN '01' THEN PK_SGD_DESCRIPCION.DE_NOM_EMP(d.co_emp_des) ");
        prutaReporte.append("			ELSE NULL ");
        prutaReporte.append("		END de_des, ");
        prutaReporte.append("		CASE d.ti_des ");
        prutaReporte.append("			WHEN '01' THEN PK_SGD_DESCRIPCION.DE_DEPENDENCIA(d.co_dep_des) ");
        prutaReporte.append("			WHEN '02' THEN PK_SGD_DESCRIPCION.DE_PROVEEDOR(d.nu_ruc_des) || ' -RUC:' || d.nu_ruc_des ");
        prutaReporte.append("			WHEN '03' THEN COALESCE(PK_SGD_DESCRIPCION.ANI_SIMIL(d.nu_dni_des), ");
        prutaReporte.append("									(SELECT LTRIM(RTRIM(deapp)) || ' ' || LTRIM(RTRIM(deapm)) || ' ' || LTRIM(RTRIM(denom)) ");
        prutaReporte.append("									 FROM idtanirs ");
        prutaReporte.append("									 WHERE nulem = d.nu_dni_des ");
        prutaReporte.append("								 )) || ' -DNI:' || d.nu_dni_des ");
        prutaReporte.append("			WHEN '04' THEN PK_SGD_DESCRIPCION.OTRO_ORIGEN(d.co_otr_ori_des) ");
        prutaReporte.append("		END de_dep_des, ");
        prutaReporte.append("		G.FE_GUI_MP ");
        prutaReporte.append("FROM TDTC_GUIA_MP G, ");
        prutaReporte.append("     TDTD_GUIA_MP GD, ");
        prutaReporte.append("     TDTV_DESTINOS D, ");
        prutaReporte.append("     TDTV_REMITOS R, ");
        prutaReporte.append("     TDTX_REMITOS_RESUMEN RR ");
        
        try {
            prutaReporte.append("WHERE G.NU_ANN_GUI = :num_ani_guia ");
            objectParam.put("num_ani_guia", cargo.getNuAnnGuia());
            prutaReporte.append("AND G.NU_GUI = :num_guia ");
            objectParam.put("num_guia", cargo.getNuGuia()); 
            prutaReporte.append("AND GD.NU_ANN_GUI=G.NU_ANN_GUI ");
            prutaReporte.append("AND GD.NU_GUI=G.NU_GUI ");
            prutaReporte.append("AND D.NU_ANN=GD.NU_ANN_DES ");
            prutaReporte.append("AND D.NU_EMI=GD.NU_EMI ");
            prutaReporte.append("AND D.NU_DES=GD.NU_DES ");
            prutaReporte.append("AND R.NU_ANN=D.NU_ANN ");
            prutaReporte.append("AND R.NU_EMI=D.NU_EMI ");
            prutaReporte.append("AND RR.NU_ANN=R.NU_ANN ");
            prutaReporte.append("AND RR.NU_EMI=R.NU_EMI ");
            prutaReporte.append("AND G.ES_ELI = '0' ");
            prutaReporte.append("ORDER BY GD.NU_COR ");
            
            list = this.namedParameterJdbcTemplate.query(prutaReporte.toString(), objectParam, BeanPropertyRowMapper.newInstance(CargoEntregaBean.class));

        } catch (Exception ex) {
            vResult = "1" + ex.getMessage();
            ex.printStackTrace();
        }        
        return list;
    }
    
}
