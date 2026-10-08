/**
 * 
 */
package pe.gob.onpe.tramiteconv.dao.impl.oracle;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BeanPropertyRowMapper;

import pe.gob.onpe.tramitedoc.dao.AvisoBandejaEntradaDao;
import pe.gob.onpe.tramitedoc.dao.SimpleJdbcDaoBase;
import org.springframework.stereotype.Repository;
import pe.gob.onpe.tramitedoc.bean.AvisoBandejaEntradaBean;
import pe.gob.onpe.tramitedoc.bean.EtiquetaBandejaEnBean;

/**
 * @author ecueva
 *
 */
@Repository("avisoBandejaEntradaDao")
public class AvisoBandejaEntradaDaoImp  extends SimpleJdbcDaoBase implements AvisoBandejaEntradaDao {
	 
    
    //WCONDORI odicifación de bandeja a servicio
     @Override
    public List<AvisoBandejaEntradaBean> getBandejaEntradaAccesoTotal(String coDependencia,String coEmpleado, String bandeja){
        StringBuffer sql = new StringBuffer();
        if (bandeja.equals("BANDEJAFIRMA")) {
            sql.append("SELECT 1 orden,'06' Ti_Pen, 'PARA FIRMA' de_pen, 'Documentos Administrativos listos para la Firma' de_resumen, \n" +
"                          SUM(nu_can) Nu_Can , :pCoDep co_dep , '' co_bandeja,0 as nuCanMuyUrgente ,0 as nuCanUrgente, 0 as nuCanNormal \n" +
"                    FROM (                    \n" +
"                    SELECT count(1) Nu_Can FROM IDOSGD.Tdtv_Remitos r \n" +
"                    WHERE r.Es_Doc_Emi IN ('7') \n" +
"                    AND r.Ti_Emi = '01'                 \n" +
"                    AND r.Co_Dep_Emi = :pCoDep \n" +
"                    GROUP BY r.Co_Dep_Emi \n" +
"                    union select 0 nu_can from dual\n" +
"                    )");
        }
        else if (bandeja.equals("BANDEJAVB")) {
            sql.append("SELECT 2 ORDEN,'VB' TI_PEN, 'PARA VISTO BUENO' DE_PEN,'Documentos Administrativos listos para Visto Bueno' DE_RESUMEN,\n" +
"                       COUNT(1) NU_CAN,:pCoDep CO_DEP, '' co_bandeja,0 as nuCanMuyUrgente ,0 as nuCanUrgente, 0 as nuCanNormal \n" +
"                       FROM IDOSGD.TDTV_REMITOS R\n" +
"                       INNER  JOIN IDOSGD.TDTV_PERSONAL_VB VB ON R.NU_ANN=VB.NU_ANN AND R.NU_EMI=VB.NU_EMI\n" +
"                       WHERE  (R.ES_DOC_EMI='7' OR (R.TI_EMI='05' AND R.ES_DOC_EMI='5'))\n" +
"                            AND VB.CO_DEP=:pCoDep \n" +
"                            AND VB.CO_EMP_VB=:pCoEmp  \n" +
"                            AND VB.IN_VB NOT IN ('1','2')\n" + // AND VB.IN_VB <> '1' /*JHON La consulta devuelve valores que no sea 1 ('SIN VB') NI 2 ('OBSERVADO') */
"                            GROUP BY VB.CO_DEP");
        }
        else if (bandeja.equals("BANDEJAPROYECTO")) {  
            sql.append("SELECT 3 orden,'05' Ti_Pen, 'BORRADORES' De_Pen,'Documentos Administrativos en proceso de elaboración' de_resumen, \n" +
"                           sum(Nu_Can) Nu_Can , :pCoDep co_dep, '' co_bandeja,0 as nuCanMuyUrgente,0 as nuCanUrgente, 0 as nuCanNormal \n" +
"                      from \n" +
"                      (\n" +
"                      select   COUNT(1) Nu_Can \n" +
"                        FROM IDOSGD.Tdtv_Remitos r \n" +
"                       WHERE r.Es_Doc_Emi IN ('5') \n" +
"                         AND r.Ti_Emi = '01' \n" +
"                         AND r.Co_Dep_Emi = :pCoDep\n" +
"                       GROUP BY r.Co_Dep_Emi \n" +
"                        union select 0 nu_can from dual \n" +
"                        )");
        }
        else if (bandeja.equals("BANDEJAENTRADA")) {
            sql.append("SELECT 4 orden,'P' Ti_Pen, 'BANDEJA DE ENTRADA' De_Pen, 'Documentos recibidos' de_resumen,\n" +
"                        NVL(nuCanNormal,0)+NVL(nuCanUrgente,0)+NVL(nuCanMuyUrgente,0)  AS Nu_Can,:pCoDep co_dep , 'O' co_bandeja,\n" +
"                        NVL(nuCanNormal,0) nuCanNormal,NVL(nuCanUrgente,0) nuCanUrgente ,NVL(nuCanMuyUrgente,0) nuCanMuyUrgente\n" +
"                        FROM (\n" +
"                        SELECT  t.Co_Pri,COUNT(1) Nu_Can \n" +
"                                                  FROM IDOSGD.Tdtv_Remitos  r \n" +
"                                                  INNER JOIN IDOSGD.Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
"                                                   AND r.Nu_Emi = t.Nu_Emi \n" +
"                                                   AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
"                                                   AND t.Es_Doc_Rec in ('0','1')\n" +
"                                                   AND t.Es_Eli = '0'\n" +
"                                               --AND t.Co_Pri = '3'\n" +
"                                                   AND t.Co_Dep_Des = :pCoDep\n" +
"                                                   AND t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep)   \n" +
"                                                  LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
"                                                  FROM IDOSGD.tdtr_referencia ref \n" +
"                                                  INNER JOIN IDOSGD.tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
"                                                  WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
"                                                  GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
"                                                  where nvl(totalproceso_ref,0)=0\n" +
"                                                  GROUP BY t.Co_Pri\n" +
"                        )TB \n" +
"                         PIVOT (AVG(Nu_Can) FOR Co_Pri IN ('1' AS nuCanNormal,'2' AS nuCanUrgente,'3' AS nuCanMuyUrgente)) ");
        }
        else if (bandeja.equals("BANDEJADELEGADOS")) {  
         sql.append("SELECT 5 orden,'P' Ti_Pen, 'DELEGADOS' De_Pen, 'Documentos recibidos' de_resumen, NVL(nuCanNormal,0)+NVL(nuCanUrgente,0)+NVL(nuCanMuyUrgente,0)  AS Nu_Can\n" +
"                    ,:pCoDep co_dep , 'P' co_bandeja,NVL(nuCanNormal,0) nuCanNormal,NVL(nuCanUrgente,0) nuCanUrgente ,NVL(nuCanMuyUrgente,0) nuCanMuyUrgente FROM (\n" +
"                    SELECT  t.Co_Pri , COUNT(1) Nu_Can \n" +
"                          FROM IDOSGD.Tdtv_Remitos  r \n" +
"                           INNER JOIN    IDOSGD.Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
"                           AND r.Nu_Emi = t.Nu_Emi \n" +
"                           AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
"                           AND t.Es_Doc_Rec in ('0','1')\n" +
"                           AND t.Es_Eli = '0'\n" +
"                          --AND t.Co_Pri = '3'\n" +
"                           AND t.Co_Dep_Des = :pCoDep\n" +
"                           AND NOT t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM IDOSGD.RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep)   \n" +
"                          LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
"                          FROM IDOSGD.tdtr_referencia ref \n" +
"                          INNER JOIN IDOSGD.tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
"                          WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
"                          GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
"                          where nvl(totalproceso_ref,0)=0\n" +
"                          group by t.Co_Pri\n" +
"                    )TB \n" +
"                    PIVOT (AVG(Nu_Can) FOR Co_Pri IN ('1' AS nuCanNormal,'2' AS nuCanUrgente,'3' AS nuCanMuyUrgente))");
        }
        else if (bandeja.equals("BANDEJAENVIADOS")) { 
         sql.append("SELECT 6 orden,'09' Ti_Pen,  'ENVIADOS' de_pen, 'Documentos Administrativos derivados' de_resumen,\n" +
"                          SUM(nu_can) Nu_Can , :pCoDep co_dep , '' co_bandeja,0 as nuCanMuyUrgente,0 as nuCanUrgente, 0 as nuCanNormal\n" +
"                    FROM (\n" +
"                    SELECT count(1) Nu_Can     \n" +
"                           FROM IDOSGD.Tdtv_Remitos r\n" +
"                          WHERE r.Es_Doc_Emi IN ('0') \n" +
"                         AND r.Ti_Emi = '01'                 \n" +
"                            AND r.Co_Dep_Emi = :pCoDep\n" +
"                            AND TO_CHAR(r.FE_EMI,'dd/mm/yy')=TO_CHAR(sysdate,'dd/mm/yy') \n" +
"                          GROUP BY r.Co_Dep_Emi \n" +
"                          union select 0 nu_can from dual\n" +
"                          )");
        }
        else if (bandeja.equals("BANDEJASALIDA")) { 
            sql.append(" SELECT 7 orden,'08' Ti_Pen,\n" +
"                          '<p style=''color:#D50911; padding:0px;''>BANDEJA DE SALIDA</p>' De_Pen, 'Doc. pendientes de envio a mensajería' de_resumen, \n" +
"                           COUNT (1) Nu_Can, :pCoDep co_dep, '' co_bandeja,0 as nuCanMuyUrgente,0 as nuCanUrgente, 0 as nuCanNormal \n" +
"                           FROM IDOSGD.Tdtv_Remitos r\n" +
"                          WHERE r.Es_Doc_Emi='0' \n" +
"                          AND r.DOC_ESTADO_MSJ is null AND r.TI_ENV_MSJ is null \n" +
"                          AND r.CO_TIP_DOC_ADM in ( SELECT CDOC_TIPDOC FROM IDOSGD.SI_MAE_TIPO_DOC WHERE IN_DOC_SALIDA=1 ) AND TI_EMI='01' \n" +
"                          AND r.Es_Eli = '0'\n" +
"                          AND r.Co_Dep_Emi = :pCoDep\n" +
"                       GROUP BY r.Co_Dep_Emi");
        }
/*else {
        sql.append("SELECT 1 orden,'06' Ti_Pen,\n" +
                    "      'PARA FIRMA' de_pen, 'Documentos Administrativos listos para la Firma' de_resumen,\n" +
                    "      SUM(nu_can) Nu_Can , :pCoDep co_dep , '' co_bandeja,0 as nuCanMuyUrgente ,0 as nuCanUrgente, 0 as nuCanNormal\n" +
                    "FROM (SELECT count(1) Nu_Can     \n" +
                    "       FROM Tdtv_Remitos r\n" +
                    "      WHERE r.Es_Doc_Emi IN ('7') \n" +
                    "     AND r.Ti_Emi = '01' \n" +                
                    "        AND r.Co_Dep_Emi = :pCoDep \n" +
                 //  "  AND ( ((SELECT co_empleado FROM RHTM_DEPENDENCIA WHERE co_dependencia=:pCoDep)=:pCoEmp AND r.CO_EMP_EMI =:pCoEmp ) OR ( (SELECT co_empleado FROM RHTM_DEPENDENCIA WHERE co_dependencia=:pCoDep)!=:pCoEmp AND r.CO_DEP_EMI = :pCoDep)  ) "+
                    "      GROUP BY r.Co_Dep_Emi \n" +
                    "      union select 0 nu_can from dual)\n" +
                    "UNION\n" +
                    "SELECT 2 ORDEN,'VB' TI_PEN,\n" +
                    "   'PARA VISTO BUENO' DE_PEN,'Documentos Administrativos listos para Visto Bueno' DE_RESUMEN,\n" +
                    "   COUNT(1) NU_CAN,:pCoDep CO_DEP, '' co_bandeja,0 as nuCanMuyUrgente ,0 as nuCanUrgente, 0 as nuCanNormal \n" +
                    "   FROM TDTV_REMITOS R,TDTV_PERSONAL_VB VB\n" +
                    "   WHERE R.NU_ANN=VB.NU_ANN\n" +
                    "        AND R.NU_EMI=VB.NU_EMI\n" +
                    "        AND (R.ES_DOC_EMI='7' OR (R.TI_EMI='05' AND R.ES_DOC_EMI='5'))\n" +
//                    "        AND R.CO_DEP_EMI=:pCoDep\n" +
                    "        AND VB.CO_DEP=:pCoDep\n" +
                    "        AND VB.CO_EMP_VB=:pCoEmp\n" +                                
                    "        AND VB.IN_VB <> '1'\n" +
                    "        GROUP BY VB.CO_DEP\n" +
                    "UNION\n" +
                    "SELECT 3 orden,'05' Ti_Pen,\n" +
                    "       'BORRADORES' De_Pen,'Documentos Administrativos en proceso de elaboración' de_resumen, \n" +
                    "       sum(Nu_Can) Nu_Can , :pCoDep co_dep, '' co_bandeja,0 as nuCanMuyUrgente,0 as nuCanUrgente, 0 as nuCanNormal \n" +
                    "  from \n" +
                    "  (select \n" +
                    "        COUNT(1) Nu_Can \n" +
                    "    FROM Tdtv_Remitos r \n" +
                    "   WHERE r.Es_Doc_Emi IN ('5') \n" +
                    "     AND r.Ti_Emi = '01' \n" +
                    "     AND r.Co_Dep_Emi = :pCoDep \n" +
                    "   GROUP BY r.Co_Dep_Emi \n" +
                    "    union select 0 nu_can from dual \n" +
                    "    )\n" +
                    "union\n"+
                
                     "SELECT 4 orden,'P' Ti_Pen, \n" +
                    "       'BANDEJA DE ENTRADA' De_Pen, 'Documentos recibidos' de_resumen, \n" +
                    " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "       inner join Tdtv_Destinos t on r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "     ) as Nu_Can,\n" + 
                    ":pCoDep co_dep , 'O' co_bandeja,\n" +
                    " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "      INNER JOIN Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "   AND t.Co_Pri = '3'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "     ) as nuCanMuyUrgente,\n" +
                 " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "      INNER JOIN Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "      AND t.Co_Pri = '2'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "    ) as nuCanUrgente,\n" +
                 " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "      INNER JOIN    Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "   AND t.Co_Pri = '1'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "     ) as nuCanNormal\n" +
                    "FROM DUAL " +                      
                 "UNION\n" +
                    "SELECT 5 orden,'P' Ti_Pen, \n" +
                    "       'DELEGADOS' De_Pen, 'Documentos recibidos' de_resumen, \n" +
                    " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "      INNER JOIN Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND NOT t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "    ) as Nu_Can,\n" +
                    " :pCoDep co_dep , 'P' co_bandeja,\n" +
                    " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "       INNER JOIN    Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "   AND t.Co_Pri = '3'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND NOT t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "    ) as nuCanMuyUrgente,\n" +
                 " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "       INNER JOIN    Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "   AND t.Co_Pri = '2'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND NOT t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "    ) as nuCanUrgente,\n" +
                 " (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r \n" +
                    "       INNER JOIN Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9', '8')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1')\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "   AND t.Co_Pri = '1'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND NOT t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "      LEFT JOIN ( SELECT COUNT(1) totalproceso_ref,ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref\n" +
                    "      FROM tdtr_referencia ref \n" +
                    "      INNER JOIN tdtv_remitos b ON ref.nu_ann = b.nu_ann and ref.nu_emi = b.nu_emi\n" +
                    "      WHERE  b.es_eli = '0'  and b.es_doc_emi <> '9'  \n" +
                    "      GROUP BY ref.nu_ann_ref,ref.nu_emi_ref,ref.nu_des_ref ) TB  ON   TB.nu_ann_ref=r.nu_ann AND TB.nu_emi_ref=r.NU_EMI AND TB.nu_des_ref=t.nu_des    \n" +
                    "      where nvl(totalproceso_ref,0)=0"+
                    "    ) as nuCanNormal\n" +
                    "FROM dual \n" +
                    "UNION\n" +
                    "SELECT 6 orden,'09' Ti_Pen,\n" +
                    "      'ENVIADOS' de_pen, 'Documentos Administrativos derivados' de_resumen,\n" +
                    "      SUM(nu_can) Nu_Can , :pCoDep co_dep , '' co_bandeja,0 as nuCanMuyUrgente,0 as nuCanUrgente, 0 as nuCanNormal\n" +
                    "FROM (SELECT count(1) Nu_Can     \n" +
                    "       FROM Tdtv_Remitos r\n" +
                    "      WHERE r.Es_Doc_Emi IN ('0') \n" +
                    "     AND r.Ti_Emi = '01' \n" +                
                    "        AND r.Co_Dep_Emi = :pCoDep \n" +
                    "        AND TO_CHAR(r.FE_EMI,'dd/mm/yy')=TO_CHAR(sysdate,'dd/mm/yy') \n" +
                    "      GROUP BY r.Co_Dep_Emi \n" +
                    "      union select 0 nu_can from dual)\n" +
                    "UNION\n" +
                     //YUAL
                    "SELECT 7 orden,'08' Ti_Pen,\n" +
                    "      '<p style=''color:#D50911; padding:0px;''>BANDEJA DE SALIDA</p>' De_Pen, 'Doc. pendientes de envio a mensajería' de_resumen, \n" +
                    "       COUNT (1) Nu_Can, :pCoDep co_dep, '' co_bandeja,0 as nuCanMuyUrgente,0 as nuCanUrgente, 0 as nuCanNormal \n" +
                    "       FROM Tdtv_Remitos r\n" +
                    "      WHERE r.Es_Doc_Emi='0' \n" +
                    "      AND r.DOC_ESTADO_MSJ is null AND r.TI_ENV_MSJ is null \n" +
                    "  AND r.CO_TIP_DOC_ADM in ( SELECT CDOC_TIPDOC FROM SI_MAE_TIPO_DOC WHERE IN_DOC_SALIDA=1 ) AND TI_EMI='01' "+
                    "       AND r.Es_Eli = '0'\n" +
                    "        AND r.Co_Dep_Emi = :pCoDep \n" +
                    "   GROUP BY r.Co_Dep_Emi\n" +
                    "ORDER BY 1");

}*/
        List<AvisoBandejaEntradaBean> list = new ArrayList<AvisoBandejaEntradaBean>();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoDep", coDependencia);
        objectParam.put("pCoEmp", coEmpleado);
        
        try {
 
        if (bandeja.equals("BANDEJAFIRMA") || bandeja.equals("BANDEJAVB")  || bandeja.equals("BANDEJAPROYECTO") || bandeja.equals("BANDEJAENTRADA") || bandeja.equals("BANDEJADELEGADOS")
            || bandeja.equals("BANDEJAENVIADOS") || bandeja.equals("BANDEJASALIDA") )
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AvisoBandejaEntradaBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }    
 

 @Override
 public List<AvisoBandejaEntradaBean> getBandejaEntradaAccesoPersonal(String coDependencia,String coEmpleado,String bandeja){
        StringBuffer sql = new StringBuffer();
        if (bandeja.equals("BANDEJAFIRMA")) {
        sql.append("SELECT 1 orden,'06' Ti_Pen,  'PARA DESPACHO' De_Pen, 'Documentos Administrativos listos para la Firma' de_resumen,\n" +
"                          SUM(nu_can) Nu_Can , :pCoDep  co_dep \n" +
"                    FROM (SELECT count(1) Nu_Can     \n" +
"                           FROM IDOSGD.Tdtv_Remitos r\n" +
"                          WHERE r.Es_Doc_Emi IN ('7') \n" +
"                         AND r.Ti_Emi = '01'                 \n" +
"                            AND r.Co_Dep_Emi = :pCoDep \n" +
"                            AND r.Co_Emp_Res = :pCoEmp  \n" +
"                          GROUP BY r.Co_Dep_Emi,\n" +
"                                   r.Co_Emp_Res\n" +
"                          union select 0 nu_can from dual) ");
        }
        else if (bandeja.equals("BANDEJAVB")) {
        sql.append("SELECT 2 ORDEN,'VB' TI_PEN,  'PARA VISTO BUENO' DE_PEN,'Documentos Administrativos listos para Visto Bueno' DE_RESUMEN,\n" +
"                       COUNT(1) NU_CAN,:pCoDep CO_DEP\n" +
"                       FROM IDOSGD.TDTV_REMITOS R\n" +
"                       INNER JOIN IDOSGD.TDTV_PERSONAL_VB VB ON R.NU_ANN=VB.NU_ANN AND R.NU_EMI=VB.NU_EMI\n" +
"                       WHERE  (R.ES_DOC_EMI='7' OR (R.TI_EMI='05' AND R.ES_DOC_EMI='5'))\n" +
"                            AND VB.CO_DEP=:pCoDep \n" +
"                            AND VB.CO_EMP_VB=:pCoEmp                                 \n" +
"                            AND VB.IN_VB NOT IN ('1','2')\n" + // AND VB.IN_VB <> '1' /*JHON*/
"                            GROUP BY VB.CO_DEP ");
        }
        else if (bandeja.equals("BANDEJAPROYECTO")) {
        sql.append(" SELECT 3 orden,'05' Ti_Pen,  'EN PROYECTO' De_Pen,'Documentos Administrativos en proceso de elaboración' de_resumen, \n" +
"                           sum(Nu_Can) Nu_Can , :pCoDep co_dep \n" +
"                      from \n" +
"                      (select  COUNT(1) Nu_Can \n" +
"                        FROM IDOSGD.Tdtv_Remitos r \n" +
"                       WHERE r.Es_Doc_Emi IN ('5') \n" +
"                         AND r.Ti_Emi = '01' \n" +
"                         AND r.Co_Dep_Emi = :pCoDep \n" +
"                         AND r.Co_Emp_Res = :pCoEmp  \n" +
"                       GROUP BY r.Co_Dep_Emi, \n" +
"                                r.Co_Emp_Res  \n" +
"                        union select 0 nu_can from dual ) ");
        }
        else if (bandeja.equals("BANDEJAMUYURGENTE")) {
        sql.append(" SELECT 5 orden,'07' Ti_Pen,  'MUY URGENTES' De_Pen,'Documentos pendientes de recibir MUY URGENTES' de_resumen,\n" +
"                           COUNT(1) Nu_Can, :pCoDep co_dep \n" +
"                      FROM IDOSGD.Tdtv_Remitos  r \n" +
"                       INNER JOIN IDOSGD.Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann AND r.Nu_Emi = t.Nu_Emi\n" +
"                     WHERE  r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
"                       AND t.Co_Dep_Des = :pCoDep \n" +
"                       AND t.Co_Emp_Des = :pCoEmp  \n" +
"                       AND t.Es_Doc_Rec = '0'\n" +
"                       AND t.Es_Eli = '0'\n" +
"                       AND t.Co_Pri = '3'\n" +
"                       AND r.in_oficio = '0'\n" +
"                     GROUP BY t.Co_Dep_Des,\n" +
"                              t.Co_Emp_Des");
        }
        else if (bandeja.equals("BANDEJAURGENTE")) {
        sql.append("  SELECT 6 orden,'04' Ti_Pen,  'URGENTES' De_Pen, 'Documentos pendientes de recibir URGENTES' de_resumen,\n" +
"                           COUNT(1) Nu_Can, :pCoDep co_dep \n" +
"                      FROM IDOSGD.Tdtv_Remitos  r\n" +
"                           INNER JOIN IDOSGD.Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann\n" +
"                       AND r.Nu_Emi = t.Nu_Emi\n" +
"                     WHERE r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
"                       AND t.Co_Dep_Des = :pCoDep \n" +
"                       AND t.Co_Emp_Des = :pCoEmp  \n" +
"                       AND t.Es_Doc_Rec = '0'\n" +
"                       AND t.Es_Eli = '0'\n" +
"                       AND t.Co_Pri = '2'\n" +
"                       AND r.in_oficio = '0'\n" +
"                     GROUP BY t.Co_Dep_Des,\n" +
"                              t.Co_Emp_Des  ");
        }
        else if (bandeja.equals("BANDEJADELEGADOS")) {//BANDEJARECIBIDOS
        sql.append("SELECT 4 orden,'02' Ti_Pen,   'RECIBIDOS' De_Pen, 'Documentos Recibidos' de_resumen, \n" +
"                           SUM(nu_can) Nu_Can , :pCoDep co_dep \n" +
"                    FROM (SELECT  COUNT(1) Nu_Can \n" +
"                          FROM IDOSGD.Tdtv_Remitos  r  \n" +
"                           INNER JOIN IDOSGD.Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann  AND r.Nu_Emi = t.Nu_Emi \n" +
"                         WHERE  r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
"                           AND t.Co_Dep_Des = :pCoDep \n" +
"                           AND t.Co_Emp_Des = :pCoEmp  \n" +
"                           AND t.Es_Doc_Rec = '1'\n" +
"                           AND t.Es_Eli = '0'\n" +
"                         GROUP BY t.Co_Dep_Des,\n" +
"                                  t.Co_Emp_Des \n" +
"                         union select 0 nu_can from dual)");
        }
        else if (bandeja.equals("BANDEJAENTRADA")) {//BANDEJANOLEIDOS
        sql.append("SELECT 7 orden,'01' Ti_Pen,    'NO LEIDOS' De_Pen, 'Documentos pendientes de recibir' de_resumen, \n" +
"                           SUM(nu_can) Nu_Can , :pCoDep co_dep \n" +
"                    FROM (SELECT  COUNT(1) Nu_Can \n" +
"                          FROM IDOSGD.Tdtv_Remitos  r  \n" +
"                             INNER JOIN  IDOSGD.Tdtv_Destinos t ON r.Nu_Ann = t.Nu_Ann \n" +
"                           AND r.Nu_Emi = t.Nu_Emi\n" +
"                         WHERE r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
"                           AND t.Co_Dep_Des = :pCoDep \n" +
"                           AND t.Co_Emp_Des = :pCoEmp  \n" +
"                           AND t.Es_Doc_Rec = '0'\n" +
"                           AND t.Es_Eli = '0'\n" +
"                         GROUP BY t.Co_Dep_Des,\n" +
"                                  t.Co_Emp_Des \n" +
"                         union select 0 nu_can from dual)");
        }
        /*sql.append("SELECT 1 orden,'06' Ti_Pen,\n" +
                    "      'PARA DESPACHO' De_Pen, 'Documentos Administrativos listos para la Firma' de_resumen,\n" +
                    "      SUM(nu_can) Nu_Can , :pCoDep co_dep \n" +
                    "FROM (SELECT count(1) Nu_Can     \n" +
                    "       FROM Tdtv_Remitos r\n" +
                    "      WHERE r.Es_Doc_Emi IN ('7') \n" +
                    "     AND r.Ti_Emi = '01' \n" +                
                    "        AND r.Co_Dep_Emi = :pCoDep \n" +
                    "        AND r.Co_Emp_Res = :pCoEmp \n" +
                    "      GROUP BY r.Co_Dep_Emi,\n" +
                    "               r.Co_Emp_Res\n" +
                    "      union select 0 nu_can from dual)" +
                    "UNION\n" +
                    "SELECT 2 ORDEN,'VB' TI_PEN,\n" +
                    "   'PARA VISTO BUENO' DE_PEN,'Documentos Administrativos listos para Visto Bueno' DE_RESUMEN,\n" +
                    "   COUNT(1) NU_CAN,:pCoDep CO_DEP\n" +
                    "   FROM TDTV_REMITOS R,TDTV_PERSONAL_VB VB\n" +
                    "   WHERE R.NU_ANN=VB.NU_ANN\n" +
                    "        AND R.NU_EMI=VB.NU_EMI\n" +
                    "        AND (R.ES_DOC_EMI='7' OR (R.TI_EMI='05' AND R.ES_DOC_EMI='5'))\n" +
//                    "        AND R.CO_DEP_EMI=:pCoDep\n" +
                    "        AND VB.CO_DEP=:pCoDep\n" +
                    "        AND VB.CO_EMP_VB=:pCoEmp\n" +                                
                    "        AND VB.IN_VB <> '1'\n" +
                    "        GROUP BY VB.CO_DEP\n" +
                    "UNION\n" +                
                    "SELECT 3 orden,'05' Ti_Pen,\n" +
                    "       'EN PROYECTO' De_Pen,'Documentos Administrativos en proceso de elaboración' de_resumen, \n" +
                    "       sum(Nu_Can) Nu_Can , :pCoDep co_dep \n" +
                    "  from \n" +
                    "  (select \n" +
                    "        COUNT(1) Nu_Can \n" +
                    "    FROM Tdtv_Remitos r \n" +
                    "   WHERE r.Es_Doc_Emi IN ('5') \n" +
                    "     AND r.Ti_Emi = '01' \n" +
                    "     AND r.Co_Dep_Emi = :pCoDep \n" +
                    "     AND r.Co_Emp_Res = :pCoEmp \n" +
                    "   GROUP BY r.Co_Dep_Emi, \n" +
                    "            r.Co_Emp_Res  \n" +
                    "    union select 0 nu_can from dual \n" +
                    "    )" +
                    "UNION\n" +
                    "SELECT 5 orden,'07' Ti_Pen,\n" +
                    "       'MUY URGENTES' De_Pen,'Documentos pendientes de recibir MUY URGENTES' de_resumen,\n" +
                    "       COUNT(1) Nu_Can, :pCoDep co_dep \n" +
                    "  FROM Tdtv_Remitos  r,\n" +
                    "       Tdtv_Destinos t\n" +
                    " WHERE r.Nu_Ann = t.Nu_Ann\n" +
                    "   AND r.Nu_Emi = t.Nu_Emi\n" +
                    "   AND r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
                    "   AND t.Co_Dep_Des = :pCoDep \n" +
                    "   AND t.Co_Emp_Des = :pCoEmp \n" +
                    "   AND t.Es_Doc_Rec = '0'\n" +
                    "   AND t.Es_Eli = '0'\n" +
                    "   AND t.Co_Pri = '3'\n" +
                    "   AND r.in_oficio = '0'\n" +
                    " GROUP BY t.Co_Dep_Des,\n" +
                    "          t.Co_Emp_Des\n" +
                    "UNION\n" +
                    "SELECT 6 orden,'04' Ti_Pen,\n" +
                    "       'URGENTES' De_Pen, 'Documentos pendientes de recibir URGENTES' de_resumen,\n" +
                    "       COUNT(1) Nu_Can, :pCoDep co_dep \n" +
                    "  FROM Tdtv_Remitos  r,\n" +
                    "       Tdtv_Destinos t\n" +
                    " WHERE r.Nu_Ann = t.Nu_Ann\n" +
                    "   AND r.Nu_Emi = t.Nu_Emi\n" +
                    "   AND r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
                    "   AND t.Co_Dep_Des = :pCoDep \n" +
                    "   AND t.Co_Emp_Des = :pCoEmp \n" +
                    "   AND t.Es_Doc_Rec = '0'\n" +
                    "   AND t.Es_Eli = '0'\n" +
                    "   AND t.Co_Pri = '2'\n" +
                    "   AND r.in_oficio = '0'\n" +
                    " GROUP BY t.Co_Dep_Des,\n" +
                    "          t.Co_Emp_Des  \n" +
                    "UNION\n" +
                    "SELECT 4 orden,'02' Ti_Pen, \n" +
                    "       'RECIBIDOS' De_Pen, 'Documentos Recibidos' de_resumen, \n" +
                    "       SUM(nu_can) Nu_Can , :pCoDep co_dep \n" +
                    "FROM (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r, \n" +
                    "           Tdtv_Destinos t \n" +
                    "     WHERE r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND t.Co_Emp_Des = :pCoEmp \n" +
                    "       AND t.Es_Doc_Rec = '1'\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "     GROUP BY t.Co_Dep_Des,\n" +
                    "              t.Co_Emp_Des \n" +
                    "     union select 0 nu_can from dual)" +
                    "UNION\n" +
                    "SELECT 7 orden,'01' Ti_Pen, \n" +
                    "       'NO LEIDOS' De_Pen, 'Documentos pendientes de recibir' de_resumen, \n" +
                    "       SUM(nu_can) Nu_Can , :pCoDep co_dep \n" +
                    "FROM (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r, \n" +
                    "           Tdtv_Destinos t \n" +
                    "     WHERE r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND t.Co_Emp_Des = :pCoEmp \n" +
                    "       AND t.Es_Doc_Rec = '0'\n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "     GROUP BY t.Co_Dep_Des,\n" +
                    "              t.Co_Emp_Des \n" +
                    "     union select 0 nu_can from dual)" +
                    " ");
*/
        List<AvisoBandejaEntradaBean> list = new ArrayList<AvisoBandejaEntradaBean>();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoDep", coDependencia);
        objectParam.put("pCoEmp", coEmpleado);
        
        try {
             if (bandeja.equals("BANDEJAFIRMA") || bandeja.equals("BANDEJAVB")  || bandeja.equals("BANDEJAPROYECTO") || bandeja.equals("BANDEJAENTRADA") || bandeja.equals("BANDEJADELEGADOS")
            || bandeja.equals("BANDEJAMUYURGENTE") || bandeja.equals("BANDEJAURGENTE") )
                list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AvisoBandejaEntradaBean.class));
            
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }


//YUAL
      @Override
    public List<AvisoBandejaEntradaBean> getBandejaEntradaAltaDireccionTotal(String coDependencia,String coEmpleado){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT 1 orden,'06' Ti_Pen,\n" +
                    "      'PARA DESPACHO' de_pen, 'Documentos Administrativos listos para la Firma' de_resumen,\n" +
                    "      SUM(nu_can) Nu_Can , :pCoDep co_dep , '' co_bandeja\n" +
                    "FROM (SELECT count(1) Nu_Can     \n" +
                    "       FROM Tdtv_Remitos r\n" +
                    "      WHERE r.Es_Doc_Emi IN ('7') \n" +
                    "     AND r.Ti_Emi = '01' \n" +                
                    "        AND r.Co_Dep_Emi = :pCoDep \n" +
             //       "  AND ( ((SELECT co_empleado FROM RHTM_DEPENDENCIA WHERE co_dependencia=:pCoDep)=:pCoEmp AND r.CO_EMP_EMI =:pCoEmp ) OR ( (SELECT co_empleado FROM RHTM_DEPENDENCIA WHERE co_dependencia=:pCoDep)!=:pCoEmp AND r.CO_DEP_EMI = :pCoDep)  ) "+
                    "      GROUP BY r.Co_Dep_Emi \n" +
                    "      union select 0 nu_can from dual)\n" +
                    "UNION\n" +
                    "SELECT 2 ORDEN,'VB' TI_PEN,\n" +
                    "   'PARA VISTO BUENO' DE_PEN,'Documentos Administrativos listos para Visto Bueno' DE_RESUMEN,\n" +
                    "   COUNT(1) NU_CAN,:pCoDep CO_DEP, '' co_bandeja \n" +
                    "   FROM TDTV_REMITOS R,TDTV_PERSONAL_VB VB\n" +
                    "   WHERE R.NU_ANN=VB.NU_ANN\n" +
                    "        AND R.NU_EMI=VB.NU_EMI\n" +
                    "        AND (R.ES_DOC_EMI='7' OR (R.TI_EMI='05' AND R.ES_DOC_EMI='5'))\n" +
//                    "        AND R.CO_DEP_EMI=:pCoDep\n" +
                    "        AND VB.CO_DEP=:pCoDep\n" +
                    "        AND VB.CO_EMP_VB=:pCoEmp\n" +                                
                    "        AND VB.IN_VB <> '1'\n" +
                    "        GROUP BY VB.CO_DEP\n" +
                                
                 //   "SELECT 7 orden,'02' Ti_Pen, \n" +
                 ////   "       'RECIBIDOS (Oficina)' De_Pen, 'Documentos recibidos' de_resumen, \n" +
                  //  "       SUM(nu_can) Nu_Can , :pCoDep co_dep , 'O' co_bandeja\n" +
                 //   "FROM (SELECT  COUNT(1) Nu_Can \n" +
                   // "      FROM Tdtv_Remitos  r, \n" +
                    //"           Tdtv_Destinos t \n" +
                    //"     WHERE r.Nu_Ann = t.Nu_Ann \n" +
                    //"       AND r.Nu_Emi = t.Nu_Emi \n" +
                    //"       AND r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
                    //"       AND t.Es_Doc_Rec = '1'\n" +
                    //"       AND t.Es_Eli = '0'\n" +
                    //"       AND t.Co_Dep_Des = :pCoDep \n" +
                    //"       AND t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    //"     GROUP BY t.Co_Dep_Des\n" +
                    //"     union select 0 nu_can from dual)\n" +
                    "UNION\n" +
                    "SELECT 3 orden,'P' Ti_Pen, \n" +
                    "       'BANDEJA DE ENTRADA' De_Pen, 'Documentos pendientes de recibir' de_resumen, \n" +
                    "       SUM(nu_can) Nu_Can , :pCoDep co_dep, 'O' co_bandeja \n" +
                    "FROM (SELECT  COUNT(1) Nu_Can \n" +
                    "      FROM Tdtv_Remitos  r, \n" +
                    "           Tdtv_Destinos t \n" +
                    "     WHERE r.Nu_Ann = t.Nu_Ann \n" +
                    "       AND r.Nu_Emi = t.Nu_Emi \n" +
                    "       AND r.Es_Doc_Emi NOT IN ('5', '7', '9')\n" +
                    "       AND t.Es_Doc_Rec in ('0','1') \n" +
                    "       AND t.Es_Eli = '0'\n" +
                    "       AND t.Co_Dep_Des = :pCoDep \n" +
                    "       AND t.CO_EMP_DES in ( SELECT CO_EMPLEADO FROM RHTM_DEPENDENCIA WHERE CO_DEPENDENCIA= :pCoDep )   \n" +
                    "     GROUP BY t.Co_Dep_Des\n" +
                    "     union select 0 nu_can from dual)\n" +
                     "UNION\n" +
                    "SELECT 4 orden,'09' Ti_Pen,\n" +
                    "      'ENVIADOS' de_pen, 'Documentos Administrativos derivados' de_resumen,\n" +
                    "      SUM(nu_can) Nu_Can , :pCoDep co_dep , '' co_bandeja\n" +
                    "FROM (SELECT count(1) Nu_Can     \n" +
                    "       FROM Tdtv_Remitos r\n" +
                    "      WHERE r.Es_Doc_Emi IN ('0') \n" +
                    "     AND r.Ti_Emi = '01' \n" +                
                    "        AND r.Co_Dep_Emi = :pCoDep \n" +
                    "        AND TO_CHAR(r.FE_EMI,'dd/mm/yy')=TO_CHAR(sysdate,'dd/mm/yy') \n" +
                    "      GROUP BY r.Co_Dep_Emi \n" +
                    "      union select 0 nu_can from dual)\n" +
                    "UNION\n" +
                    "SELECT 5 orden,'05' Ti_Pen,\n" +
                    "       'BORRADORES' De_Pen,'Documentos Administrativos en proceso de elaboración' de_resumen, \n" +
                    "       sum(Nu_Can) Nu_Can , :pCoDep co_dep, '' co_bandeja \n" +
                    "  from \n" +
                    "  (select \n" +
                    "        COUNT(1) Nu_Can \n" +
                    "    FROM Tdtv_Remitos r \n" +
                    "   WHERE r.Es_Doc_Emi IN ('5') \n" +
                    "     AND r.Ti_Emi = '01' \n" +
                    "     AND r.Co_Dep_Emi = :pCoDep \n" +
                    "   GROUP BY r.Co_Dep_Emi \n" +
                    "    union select 0 nu_can from dual \n" +
                    "    )\n" +     
                    "UNION\n" +
                     //YUAL
                    "SELECT 18 orden,'08' Ti_Pen,\n" +
                    "      '<p style=''color:#D50911;''>MESA DE PARTES</p>' De_Pen, 'Doc. pendientes de envio a mensajería' de_resumen, \n" +
                    "       COUNT (1) Nu_Can, :pCoDep co_dep, '' co_bandeja \n" +
                    "       FROM Tdtv_Remitos r\n" +
                    "      WHERE r.Es_Doc_Emi='0' \n" +
                    "      AND r.DOC_ESTADO_MSJ is null AND r.TI_ENV_MSJ is null \n" +
                    "  AND r.CO_TIP_DOC_ADM in ('011','012') "+
                    "       AND r.Es_Eli = '0'\n" +
                    "        AND r.Co_Dep_Emi = :pCoDep \n" +
                    "   GROUP BY r.Co_Dep_Emi\n" +
                    "ORDER BY 1");

        List<AvisoBandejaEntradaBean> list = new ArrayList<AvisoBandejaEntradaBean>();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoDep", coDependencia);
        objectParam.put("pCoEmp", coEmpleado);
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AvisoBandejaEntradaBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }    
    
     
    @Override
    public List<AvisoBandejaEntradaBean> getBandejaEntradaAccesoFuncionario(String coDependencia,String coEmpleado){
        StringBuffer sql = new StringBuffer();
        sql.append("SELECT 1 orden,'06' Ti_Pen,\n" +
                    "	       'PARA DESPACHO' De_Pen, 'Documentos Administrativos listos para la Firma' de_resumen ,\n" +
                    "	       COUNT(1) Nu_Can, :pCoDep co_dep\n" +
                    "	  FROM Tdtv_Remitos r\n" +
                    "	 WHERE r.Es_Doc_Emi IN ('7') --Listos para despacho\n" +
                    "     AND r.Ti_Emi = '01' \n" +
                    "	   and r.Co_Dep_Emi = :pCoDep \n" +
                    "	   AND (r.Co_Emp_Res = :pCoEmp OR r.co_emp_emi = :pCoEmp)\n" +
                    "	 GROUP BY r.Co_Dep_Emi\n" +
                    "	 UNION\n" +
                    "SELECT 2 ORDEN,'VB' TI_PEN,\n" +
                    "   'PARA VISTO BUENO' DE_PEN,'Documentos Administrativos listos para Visto Bueno' DE_RESUMEN,\n" +
                    "   COUNT(1) NU_CAN,:pCoDep CO_DEP\n" +
                    "   FROM TDTV_REMITOS R,TDTV_PERSONAL_VB VB\n" +
                    "   WHERE R.NU_ANN=VB.NU_ANN\n" +
                    "        AND R.NU_EMI=VB.NU_EMI\n" +
                    "        AND R.ES_DOC_EMI='7'\n" +
//                    "        AND R.CO_DEP_EMI=:pCoDep\n" +
                    "        AND VB.CO_DEP=:pCoDep\n" +
                    "        AND VB.CO_EMP_VB=:pCoEmp\n" +                                
                    "        AND VB.IN_VB <> '1'\n" +
                    "        GROUP BY VB.CO_DEP\n" +
                    "UNION\n" +               
                    "	SELECT 3 orden,'05' Ti_Pen,\n" +
                    "	       'EN PROYECTO' De_Pen, 'Documentos Administrativos en proceso de elaboración' de_resumen,\n" +
                    "	       COUNT(1) Nu_Can, :pCoDep co_dep\n" +
                    "	  FROM Tdtv_Remitos r\n" +
                    "	 WHERE r.Es_Doc_Emi IN ('5') --En proyecto\n" +
                    "	   AND r.Ti_Emi = '01'\n" +
                    "	   and r.Co_Dep_Emi = :pCoDep \n" +
                    "	   AND (r.Co_Emp_Res = :pCoEmp OR r.co_emp_emi = :pCoEmp)\n" +
                    "	 GROUP BY r.Co_Dep_Emi			 \n" +
                    "	 UNION\n" +
                    "	SELECT 4 orden,'07' Ti_Pen,\n" +
                    "	       'MUY URGENTES' De_Pen, 'Documentos pendientes de recibir MUY URGENTES' de_resumen,\n" +
                    "	       COUNT(1) Nu_Can, :pCoDep co_dep\n" +
                    "	  FROM Tdtv_Remitos  r,\n" +
                    "			       Tdtv_Destinos t\n" +
                    "	 WHERE r.Nu_Ann = t.Nu_Ann\n" +
                    "	   AND r.Nu_Emi = t.Nu_Emi\n" +
                    "	   AND r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
                    "	   AND t.Es_Doc_Rec = '0'\n" +
                    "	   AND t.Es_Eli = '0'\n" +
                    "	   AND t.Co_Pri = '3'\n" +
                    "	   and t.Co_Dep_Des = :pCoDep \n" +
                    "	   AND t.Co_Emp_Des = :pCoEmp\n" +
                    "	 GROUP BY t.Co_Dep_Des,\n" +
                    "	          t.Co_Emp_Des			\n" +
                    "	 UNION\n" +
                    "	SELECT 5 orden,'04' Ti_Pen,\n" +
                    "	       'URGENTES' De_Pen, 'Documentos pendientes de recibir URGENTES' de_resumen,\n" +
                    "	       COUNT(1) Nu_Can, :pCoDep co_dep\n" +
                    "	  FROM Tdtv_Remitos  r,\n" +
                    "	       Tdtv_Destinos t\n" +
                    "	 WHERE r.Nu_Ann = t.Nu_Ann\n" +
                    "	   AND r.Nu_Emi = t.Nu_Emi\n" +
                    "	   AND r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
                    "	   AND t.Es_Doc_Rec = '0'\n" +
                    "	   AND t.Es_Eli = '0'\n" +
                    "	   AND t.Co_Pri = '2'\n" +
                    "	   and t.Co_Dep_Des = :pCoDep\n" +
                    "	   AND t.Co_Emp_Des = :pCoEmp\n" +
                    "	 GROUP BY t.Co_Dep_Des,\n" +
                    "	          t.Co_Emp_Des	\n" +
                    "	 UNION\n" +
                    "	SELECT 4 orden,'01' Ti_Pen,\n" +
                    "	       'BANDEJA' De_Pen, 'Documentos pendientes de recibir' de_resumen,\n" +
                    "	       COUNT(1) Nu_Can, :pCoDep co_dep\n" +
                    "	  FROM Tdtv_Remitos  r,\n" +
                    "	       Tdtv_Destinos t\n" +
                    "	 WHERE r.Nu_Ann = t.Nu_Ann\n" +
                    "	   AND r.Nu_Emi = t.Nu_Emi\n" +
                    "	   AND r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
                    "	   AND t.Es_Doc_Rec = '1'\n" +
                    "	   AND t.Es_Eli = '0'\n" +
                    "	   and t.Co_Dep_Des = :pCoDep \n" +
                    "	   AND t.Co_Emp_Des = :pCoEmp \n" +
                    "	 GROUP BY t.Co_Dep_Des,\n" +
                    "	          t.Co_Emp_Des"+
                    "	 UNION\n" +
                    "	SELECT 7 orden,'01' Ti_Pen,\n" +
                    "	       'NO LEIDOS' De_Pen, 'Documentos pendientes de recibir' de_resumen,\n" +
                    "	       COUNT(1) Nu_Can, :pCoDep co_dep\n" +
                    "	  FROM Tdtv_Remitos  r,\n" +
                    "	       Tdtv_Destinos t\n" +
                    "	 WHERE r.Nu_Ann = t.Nu_Ann\n" +
                    "	   AND r.Nu_Emi = t.Nu_Emi\n" +
                    "	   AND r.Es_Doc_Emi NOT IN ('5', '7', '9') --en proyecto, anulados\n" +
                    "	   AND t.Es_Doc_Rec = '0'\n" +
                    "	   AND t.Es_Eli = '0'\n" +
                    "	   and t.Co_Dep_Des = :pCoDep \n" +
                    "	   AND t.Co_Emp_Des = :pCoEmp \n" +
                    "	 GROUP BY t.Co_Dep_Des,\n" +
                    "	          t.Co_Emp_Des");

        List<AvisoBandejaEntradaBean> list = new ArrayList<AvisoBandejaEntradaBean>();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoDep", coDependencia);
        objectParam.put("pCoEmp", coEmpleado);
        
        
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(AvisoBandejaEntradaBean.class));
            
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }     

    @Override
    public List<EtiquetaBandejaEnBean> getListEtiquetaBandejaEntrada(String coDep, String coEmp) {
        StringBuffer sql = new StringBuffer();
        sql.append("select e.co_est,e.de_est descripcion,count(1) numero_documentos,d.co_dep_des,d.co_emp_rec ");
        sql.append("from tdtv_bandeja_rec d,tdtr_estados e ");
        sql.append("where d.co_dep_des = :pCoDep ");
        sql.append("and d.co_emp_rec=:pCoEmp ");
        sql.append("and d.co_etiqueta_rec<>0 ");
        sql.append("and d.es_doc_emi not in ('5','7','9') ");
        sql.append("and e.co_est=d.co_etiqueta_rec ");
        sql.append("and e.de_tab='CO_ETIQUETA_REC' ");
        sql.append("group by e.co_est,e.de_est,d.co_dep_des,d.co_emp_rec ");
        sql.append("order by e.co_est ");
        List<EtiquetaBandejaEnBean> list = new ArrayList<EtiquetaBandejaEnBean>();
        Map<String, Object> objectParam = new HashMap<String, Object>();
        objectParam.put("pCoDep", coDep);
        objectParam.put("pCoEmp", coEmp);
        try {
            list = this.namedParameterJdbcTemplate.query(sql.toString(), objectParam, BeanPropertyRowMapper.newInstance(EtiquetaBandejaEnBean.class));
        }catch (EmptyResultDataAccessException e) {
            list = null;
        }catch (Exception e) {
            list = null;
            e.printStackTrace();
        }
        return list;
    }
}
