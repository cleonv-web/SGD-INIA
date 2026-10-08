-- Ejecutar personalmente en PostgreSQL, conectado a la base SGD elegida.
-- Sólo consulta; no prepara automáticamente equivalencias ni permisos.
BEGIN READ ONLY;
SELECT co_dependencia,de_dependencia,co_depen_padre,in_baja FROM idosgd.rhtm_dependencia ORDER BY co_dependencia;
SELECT ccod_local,de_nombre_local,de_direccion_local,es_local FROM idosgd.si_mae_local ORDER BY ccod_local;
SELECT ccar_co_cargo,ccar_descar,ccar_est_car FROM idosgd.rhtm_cargos ORDER BY ccar_co_cargo;
SELECT cod_opc,des_opc FROM idosgd.seg_opciones WHERE cod_aplica='9' ORDER BY cod_opc;
SELECT cdoc_tipdoc,cdoc_desdoc,cdoc_indbaj FROM idosgd.si_mae_tipo_doc ORDER BY cdoc_tipdoc;
COMMIT;
