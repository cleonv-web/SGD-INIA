-- Se detiene ante inconsistencias. No modifica documentos ni contraseñas.
DO $$
BEGIN
 IF (SELECT count(*) FROM idosgd.seg_usuarios1 WHERE cod_user IN ('admin','mesa_demo','area_demo','jefe_demo') AND es_usuario='A') <> 4 THEN
  RAISE EXCEPTION 'No están los cuatro usuarios ficticios activos';
 END IF;
 IF (SELECT count(*) FROM idosgd.seg_opciones WHERE cod_aplica='9') < 30 THEN
  RAISE EXCEPTION 'Faltan opciones de SGD';
 END IF;
 IF EXISTS(SELECT 1 FROM pg_constraint WHERE connamespace='idosgd'::regnamespace AND NOT convalidated) THEN
  RAISE EXCEPTION 'Existen restricciones sin validar';
 END IF;
 IF NOT has_table_privilege('sgd_app','idosgd.tdtv_remitos','SELECT,INSERT,UPDATE,DELETE') THEN
  RAISE EXCEPTION 'Permisos del usuario de aplicación incompletos';
 END IF;
 IF (SELECT rolsuper OR rolcreatedb OR rolcreaterole FROM pg_roles WHERE rolname='sgd_app') THEN
  RAISE EXCEPTION 'El usuario de aplicación tiene privilegios de administración';
 END IF;
END $$;
SELECT current_database(), version();
SELECT count(*) AS tablas FROM information_schema.tables WHERE table_schema='idosgd' AND table_type='BASE TABLE';
SELECT count(*) AS funciones FROM pg_proc WHERE pronamespace='idosgd'::regnamespace;
SELECT cod_user,es_usuario,in_acceso FROM idosgd.seg_usuarios1 ORDER BY cod_user;
SELECT idosgd.pk_sgd_descripcion_de_dependencia('49001'),idosgd.pk_sgd_descripcion_de_nom_emp('49002');
