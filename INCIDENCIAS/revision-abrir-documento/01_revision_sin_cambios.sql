-- PENDIENTE DE AUTORIZACION. No ejecutado por Codex.
-- Solo consultas. Ejecutar en idosgd_inia despues de aprobar la revision.
BEGIN TRANSACTION READ ONLY;

SELECT current_database() AS base_actual;

SELECT u.cod_user, u.cemp_codemp, c.co_dep,
       c.de_dir_emi, c.de_dir_rec, c.de_dir_ane
FROM idosgd.seg_usuarios1 u
JOIN idosgd.tdtx_config_emp c ON c.co_emp = u.cemp_codemp
WHERE u.cod_user = 'JARBIETO' AND c.co_dep = '00217';

SELECT o.cod_opc, o.des_opc, p.es_habilitado
FROM idosgd.seg_opciones o
LEFT JOIN idosgd.seg_user_aplica_opc p
  ON p.cod_aplica = o.cod_aplica AND p.cod_opc = o.cod_opc
 AND p.cod_user = 'JARBIETO'
WHERE o.cod_aplica = '9' AND o.cod_opc IN ('M01', 'M0113')
ORDER BY o.cod_opc;

COMMIT;
