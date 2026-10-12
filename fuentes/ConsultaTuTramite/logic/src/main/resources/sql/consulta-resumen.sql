-- Solo lectura: el registro original y la última derivación efectiva del expediente.
SELECT to_char(e.fe_exp, 'DD/MM/YYYY HH24:MI:SS') AS fecha_expediente,
       dep_reg.de_dependencia AS oficina_registro,
       trim(est_reg.de_est) AS estado_registro,
       seguimiento.oficina_atencion,
       CASE WHEN seguimiento.es_doc_rec IN ('0', '1') THEN 'EN PROCESO'
            ELSE trim(seguimiento.estado_destino) END AS estado_destino,
       seguimiento.de_ane AS obsfinalizar,
       (seguimiento.nu_des IS NOT NULL) AS tiene_derivacion
FROM idosgd.tdtv_remitos r
JOIN idosgd.tdtc_expediente e
  ON e.nu_ann_exp = r.nu_ann_exp AND e.nu_sec_exp = r.nu_sec_exp
LEFT JOIN idosgd.rhtm_dependencia dep_reg
  ON dep_reg.co_dependencia = coalesce(e.co_dep_exp, r.co_dep_emi)
LEFT JOIN idosgd.tdtr_estados est_reg
  ON est_reg.de_tab = 'TDTV_REMITOS' AND est_reg.co_est = r.es_doc_emi
LEFT JOIN LATERAL (
    SELECT d.nu_des, d.es_doc_rec, d.de_ane, dep.de_dependencia AS oficina_atencion,
           est.de_est AS estado_destino
    FROM idosgd.tdtv_remitos mov
    JOIN idosgd.tdtv_destinos d ON d.nu_ann = mov.nu_ann AND d.nu_emi = mov.nu_emi
    LEFT JOIN idosgd.rhtm_dependencia dep ON dep.co_dependencia = d.co_dep_des
    LEFT JOIN idosgd.tdtr_estados est
      ON est.de_tab = 'TDTV_DESTINOS' AND est.co_est = d.es_doc_rec
    WHERE mov.nu_ann_exp = e.nu_ann_exp AND mov.nu_sec_exp = e.nu_sec_exp
      AND mov.es_eli = '0' AND mov.es_doc_emi NOT IN ('5', '7', '9')
      AND d.es_eli = '0' AND d.es_doc_rec <> '9'
    ORDER BY coalesce(d.fe_use_mod, d.fe_use_cre, mov.fe_emi) DESC NULLS LAST,
             d.nu_ann DESC, d.nu_emi DESC, d.nu_des DESC
    LIMIT 1
) seguimiento ON true
WHERE r.nu_ann = :anio AND r.nu_emi = :emision AND r.es_eli = '0';
