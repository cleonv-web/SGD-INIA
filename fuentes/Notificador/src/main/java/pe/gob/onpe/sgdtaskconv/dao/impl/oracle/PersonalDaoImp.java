/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pe.gob.onpe.sgdtaskconv.dao.impl.oracle;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.springframework.jdbc.core.simple.ParameterizedRowMapper;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.sgdtask.bean.DocumentosVbBean;
import pe.gob.onpe.sgdtask.bean.NotificacionLogBean;
import pe.gob.onpe.sgdtask.bean.PersonalVbBean;
import pe.gob.onpe.sgdtask.dao.PersonalVbDao;
import pe.gob.onpe.sgdtask.dao.SimpleJdbcDaoBase;

/**
 *
 * @author FSilva
 */
@Repository
public class PersonalDaoImp extends SimpleJdbcDaoBase implements PersonalVbDao {

    @Override
    public List<PersonalVbBean> getListPersonalNoVB() {
        StringBuilder query = new StringBuilder();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        //objectParam.put("pCoClaseEntidad", coClaseEntidad);
        query.append("SELECT A.*, ROWNUM");
        query.append(" FROM ( ");
        query.append("SELECT "
                + " PVB.CO_EMP_VB,"
                + " EMP.CEMP_EMAIL,"
                + " EMP.CEMP_APEPAT,"
                + " EMP.CEMP_APEMAT,"
                + " EMP.CEMP_DENOM"
                + " FROM TDTV_REMITOS RMT "
                + " INNER JOIN TDTV_PERSONAL_VB PVB ON RMT.NU_EMI=PVB.NU_EMI AND RMT.NU_ANN=PVB.NU_ANN"
                + " INNER JOIN RHTM_PER_EMPLEADOS EMP ON PVB.CO_EMP_VB=EMP.CEMP_CODEMP"
                + " INNER JOIN TDTX_REMITOS_RESUMEN RER ON RER.NU_ANN=RMT.NU_ANN AND RMT.NU_EMI=RER.NU_EMI"
                + " WHERE RMT.ES_DOC_EMI=7 AND PVB.IN_VB IN ('0','B')"
                + " GROUP BY PVB.CO_EMP_VB,EMP.CEMP_EMAIL,EMP.CEMP_APEPAT,EMP.CEMP_APEMAT,EMP.CEMP_DENOM"
                + " ORDER BY PVB.CO_EMP_VB");
        query.append(") A ");
        //query.append(" WHERE ROWNUM < 3");
        List<PersonalVbBean> lista = null;

        lista = this.namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<PersonalVbBean>() {
            public PersonalVbBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                PersonalVbBean personalVbBean = new PersonalVbBean();
                personalVbBean.setCoEmpVb(rs.getString("CO_EMP_VB"));
                personalVbBean.setEmailEmp(rs.getString("CEMP_EMAIL"));                  
                personalVbBean.setDeNombreCompleto(rs.getString("CEMP_APEPAT")+" "+rs.getString("CEMP_APEMAT")+" "+rs.getString("CEMP_DENOM"));
                //Asignar la lista de documentos que tiene pendientes de firma
                List<DocumentosVbBean> listadocumentos=getLisDocsPersonalNoVB(personalVbBean.getCoEmpVb().trim());
                personalVbBean.setListaDocumentos(listadocumentos);
                return personalVbBean;
            }
        });
        return lista;
    }

    @Override
    public List<DocumentosVbBean> getLisDocsPersonalNoVB(String coEmpVb) {
        StringBuilder query = new StringBuilder();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoEmpVb", coEmpVb);
        query.append("SELECT A.*, ROWNUM");
        query.append(" FROM ( ");
        query.append("SELECT "
                + " RMT.NU_EMI,"
                + " RMT.NU_ANN,"
                + " RMT.ES_DOC_EMI,"
                + " RMT.DE_ASU,"
                + " TO_CHAR(RMT.FE_EMI,'DD/MM/YYYY HH24:MI') FECHA_DOC,"
                + " PVB.CO_EMP_VB,"
                + " PVB.IN_VB,"
                + " EMP.CEMP_EMAIL,"
                + " COALESCE(RER.NU_DOC,' ') SIGLAS_DOC,"
                + " PK_SGD_DESCRIPCION.DE_DEPENDENCIA(RMT.CO_DEP_EMI) DEP_EMI,"
                + " PK_SGD_DESCRIPCION.DE_DOCUMENTO(RMT.CO_TIP_DOC_ADM) TIPO_DOC "
                + " FROM TDTV_REMITOS RMT "
                + " INNER JOIN TDTV_PERSONAL_VB PVB ON RMT.NU_EMI=PVB.NU_EMI AND RMT.NU_ANN=PVB.NU_ANN"
                + " INNER JOIN RHTM_PER_EMPLEADOS EMP ON PVB.CO_EMP_VB=EMP.CEMP_CODEMP"
                + " INNER JOIN TDTX_REMITOS_RESUMEN RER ON RER.NU_ANN=RMT.NU_ANN AND RMT.NU_EMI=RER.NU_EMI"
                + " WHERE RMT.ES_DOC_EMI=7 AND PVB.IN_VB IN ('0','B')"
                + " AND PVB.CO_EMP_VB=:pCoEmpVb");
        query.append(") A ");
        //query.append(" WHERE ROWNUM < 100");
        List<DocumentosVbBean> lista = null;

        lista = this.namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<DocumentosVbBean>() {
            public DocumentosVbBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                DocumentosVbBean documentosVbBean = new DocumentosVbBean();
                documentosVbBean.setCoEmpVb(rs.getString("CO_EMP_VB"));                
                documentosVbBean.setEstadoDocumento(rs.getString("ES_DOC_EMI"));
                documentosVbBean.setFechaDocString(rs.getString("FECHA_DOC"));
                documentosVbBean.setIndicadorVb(rs.getString("IN_VB"));
                documentosVbBean.setNuAnn(rs.getString("NU_ANN"));
                documentosVbBean.setNuEmi(rs.getString("NU_EMI"));
                documentosVbBean.setSiglasDocumento(rs.getString("SIGLAS_DOC"));
                documentosVbBean.setTipoDocumento(rs.getString("TIPO_DOC"));
                documentosVbBean.setDeAsunto(rs.getString("DE_ASU"));
                documentosVbBean.setDeDependencia(rs.getString("DEP_EMI"));
                return documentosVbBean;
            }
        });

        return lista;
    }

    @Override
    public List<PersonalVbBean> getListPersonalNoVBPorDocumento(String nuAnn, String nuEmi) {
        StringBuilder query = new StringBuilder();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pNuAnn", nuAnn);
        objectParam.put("pNuEmi", nuEmi);
        query.append("SELECT A.*, ROWNUM");
        query.append(" FROM ( ");
        query.append("SELECT "
                + " PVB.CO_EMP_VB,"
                + " EMP.CEMP_EMAIL,"
                + " EMP.CEMP_APEPAT,"
                + " EMP.CEMP_APEMAT,"
                + " EMP.CEMP_DENOM"
                + " FROM TDTV_REMITOS RMT "
                + " INNER JOIN TDTV_PERSONAL_VB PVB ON RMT.NU_EMI=PVB.NU_EMI AND RMT.NU_ANN=PVB.NU_ANN"
                + " INNER JOIN RHTM_PER_EMPLEADOS EMP ON PVB.CO_EMP_VB=EMP.CEMP_CODEMP"
                + " INNER JOIN TDTX_REMITOS_RESUMEN RER ON RER.NU_ANN=RMT.NU_ANN AND RMT.NU_EMI=RER.NU_EMI"
                + " WHERE RMT.ES_DOC_EMI=7 AND PVB.IN_VB IN ('0','B') AND RMT.NU_ANN=:pNuAnn AND RMT.NU_EMI=:pNuEmi"
                + " GROUP BY PVB.CO_EMP_VB,EMP.CEMP_EMAIL,EMP.CEMP_APEPAT,EMP.CEMP_APEMAT,EMP.CEMP_DENOM"
                + " ORDER BY PVB.CO_EMP_VB");
        query.append(") A ");
        //query.append(" WHERE ROWNUM < 3");
        List<PersonalVbBean> lista = new ArrayList<PersonalVbBean>();

        lista = this.namedParameterJdbcTemplate.query(query.toString(), objectParam, new ParameterizedRowMapper<PersonalVbBean>() {
            public PersonalVbBean mapRow(ResultSet rs, int rowNum) throws SQLException {
                PersonalVbBean personalVbBean = new PersonalVbBean();
                personalVbBean.setCoEmpVb(rs.getString("CO_EMP_VB"));
                personalVbBean.setEmailEmp(rs.getString("CEMP_EMAIL"));
                personalVbBean.setDeNombreCompleto(rs.getString("CEMP_APEPAT") + " " + rs.getString("CEMP_APEMAT") + " " + rs.getString("CEMP_DENOM"));
                //Asignar la lista de documentos que tiene pendientes de firma
                List<DocumentosVbBean> listadocumentos = getLisDocsPersonalNoVB(personalVbBean.getCoEmpVb().trim());
                personalVbBean.setListaDocumentos(listadocumentos);
                return personalVbBean;
            }
        });
        return lista;
    }    

     
}
