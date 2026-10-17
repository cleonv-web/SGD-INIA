-- RETIRO EXCEPCIONAL DEL PILOTO. No se ejecuta durante la instalacion.
-- Requiere respaldo completo verificado y Payara detenido.
-- Elimina la funcionalidad descartada y SOLO sus documentos de prueba 990,
-- incluidas las relaciones que apuntan a esos documentos. Conserva el tipo 232.
-- Sin CASCADE: una dependencia no prevista aborta toda la transaccion.
BEGIN;
SET LOCAL lock_timeout='15s';
SET LOCAL statement_timeout='120s';

DO $verificar_tipo$
BEGIN
 IF EXISTS(SELECT 1 FROM idosgd.si_mae_tipo_doc WHERE cdoc_tipdoc='990'
           AND cdoc_desdoc IS DISTINCT FROM 'DERIVACION INTERNA') THEN
  RAISE EXCEPTION 'El tipo 990 tiene otro uso; no se puede retirar automaticamente';
 END IF;
END $verificar_tipo$;

CREATE TEMP TABLE retirar_documentos ON COMMIT DROP AS
 SELECT nu_ann,nu_emi FROM idosgd.tdtv_remitos WHERE co_tip_doc_adm='990';

CREATE FUNCTION pg_temp.retirar_condicion(p_tabla text,p_alias text) RETURNS text
LANGUAGE plpgsql AS $fn$
DECLARE a text; b text; filtro text:='false';
BEGIN
 FOR a,b IN SELECT * FROM (VALUES ('nu_ann','nu_emi'),('nu_ann_ref','nu_emi_ref'),
                                      ('nu_ann_des','nu_emi')) pares LOOP
  IF (SELECT count(*) FROM information_schema.columns WHERE table_schema='idosgd'
      AND table_name=p_tabla AND column_name IN (a,b))=2 THEN
   filtro:=filtro||format(' OR EXISTS(SELECT 1 FROM retirar_documentos retirar_clave WHERE retirar_clave.nu_ann=%I.%I AND retirar_clave.nu_emi=%I.%I)',p_alias,a,p_alias,b);
  END IF;
 END LOOP;
 IF p_tabla='si_mae_tipo_doc' THEN
  filtro:=filtro||format(' OR (%I.cdoc_tipdoc=''990'')',p_alias);
 END IF;
 IF p_tabla='tdtx_resumen_doc' THEN
  filtro:=filtro||format(' OR (%I.co_doc=''990'')',p_alias);
 END IF;
 RETURN '('||filtro||')';
END $fn$;

-- Congelar y comprobar todos los datos que no pertenecen al retiro.
CREATE TEMP TABLE retirar_protegidas(tabla text PRIMARY KEY,cantidad bigint,huella text) ON COMMIT DROP;
CREATE TEMP TABLE retirar_auditoria ON COMMIT DROP AS
 SELECT md5(row_to_json(a)::text) h FROM idosgd.tdts_auditoria a;
DO $snapshot$
DECLARE t text; n bigint; h text; filtro text;
BEGIN
 FOR t IN SELECT tablename FROM pg_tables WHERE schemaname='idosgd'
   AND tablename NOT IN ('tdtr_permiso_derivar','tdtv_derivacion_secretaria') ORDER BY tablename LOOP
  EXECUTE format('LOCK TABLE idosgd.%I IN SHARE ROW EXCLUSIVE MODE',t);
  filtro:=pg_temp.retirar_condicion(t,'r');
  EXECUTE format('SELECT count(*),md5(coalesce(string_agg(h,'''' ORDER BY h),'''')) FROM
   (SELECT md5(row_to_json(r)::text) h FROM idosgd.%I r WHERE NOT %s) q',t,filtro) INTO n,h;
  INSERT INTO retirar_protegidas VALUES(t,n,h);
 END LOOP;
END $snapshot$;

DROP TRIGGER IF EXISTS sgd_referencia_guard_derivacion ON idosgd.tdtr_referencia;
DROP TRIGGER IF EXISTS sgd_proteger_derivacion ON idosgd.tdtv_remitos;
DROP TRIGGER IF EXISTS sgd_proteger_derivacion ON idosgd.tdtv_destinos;
DROP TRIGGER IF EXISTS sgd_proteger_derivacion ON idosgd.tdtv_archivo_doc;
DROP FUNCTION IF EXISTS idosgd.sgd_preparar_derivacion(text,text,text,text,bigint,text,text,uuid) RESTRICT;
DROP FUNCTION IF EXISTS idosgd.sgd_finalizar_derivacion(text,text,uuid,bytea) RESTRICT;
DROP FUNCTION IF EXISTS idosgd.sgd_referencia_guard_derivacion() RESTRICT;
DROP FUNCTION IF EXISTS idosgd.sgd_proteger_derivacion() RESTRICT;
DROP FUNCTION IF EXISTS idosgd.sgd_puede_derivar(text,text) RESTRICT;
DROP TABLE IF EXISTS idosgd.tdtv_derivacion_secretaria RESTRICT;
DROP TABLE IF EXISTS idosgd.tdtr_permiso_derivar RESTRICT;

DO $retirar_piloto$
DECLARE t text; pasada integer;
BEGIN
 -- Un destino externo enlazado directamente requiere tratamiento especifico.
 IF EXISTS(SELECT 1 FROM idosgd.tdtv_destinos d JOIN retirar_documentos x
   ON x.nu_ann=d.nu_ann_ref AND x.nu_emi=d.nu_emi_ref
   WHERE NOT EXISTS(SELECT 1 FROM retirar_documentos k WHERE k.nu_ann=d.nu_ann AND k.nu_emi=d.nu_emi)) THEN
  RAISE EXCEPTION 'Destino posterior vinculado al documento 990: revisar antes de retirar';
 END IF;
 -- Primero hijos con FK, despues destinos/remitos; otra pasada retira su auditoria de piloto.
 FOR pasada IN 1..2 LOOP
  FOREACH t IN ARRAY ARRAY[
   'tdtd_guia_mp','tdtd_guia_mp_his','tdtr_referencia','tdtr_referencia_his',
   'tdtv_anexos','tdtv_anexos_his','tdtv_archivo_doc','tdtv_personal_vb',
   'tdtv_visualiza_doc','tdtv_notificaciones','tdtx_remitos_resumen',
   'tdtv_destino_detalle','tdtv_bandeja_rec','tdtv_estados_mov','tdtr_arbol_seg','tdtr_avance',
   'tdtv_remitos_his','tdtv_destinos_his','tdtx_resumen_doc']::text[] LOOP
   EXECUTE format('DELETE FROM idosgd.%I r WHERE %s',t,pg_temp.retirar_condicion(t,'r'));
  END LOOP;
  IF pasada=1 THEN
   DELETE FROM idosgd.tdtv_destinos d USING retirar_documentos x WHERE x.nu_ann=d.nu_ann AND x.nu_emi=d.nu_emi;
   DELETE FROM idosgd.tdtv_remitos r USING retirar_documentos x WHERE x.nu_ann=r.nu_ann AND x.nu_emi=r.nu_emi;
  END IF;
 END LOOP;
 DELETE FROM idosgd.si_mae_tipo_doc WHERE cdoc_tipdoc='990' AND cdoc_desdoc='DERIVACION INTERNA';
END $retirar_piloto$;

DO $comprobar$
DECLARE r record; n bigint; h text; filtro text;
BEGIN
 FOR r IN SELECT * FROM retirar_protegidas LOOP
  IF r.tabla='tdts_auditoria' THEN
   -- Los DELETE normales generan auditoria; conservar todas las filas anteriores.
   IF EXISTS(SELECT previa.h FROM retirar_auditoria previa EXCEPT ALL
             SELECT md5(row_to_json(a)::text) FROM idosgd.tdts_auditoria a) THEN
    RAISE EXCEPTION 'Se modifico auditoria anterior; transaccion cancelada';
   END IF;
   CONTINUE;
  END IF;
  filtro:=pg_temp.retirar_condicion(r.tabla,'x');
  EXECUTE format('SELECT count(*),md5(coalesce(string_agg(h,'''' ORDER BY h),'''')) FROM
   (SELECT md5(row_to_json(x)::text) h FROM idosgd.%I x WHERE NOT %s) q',r.tabla,filtro) INTO n,h;
  IF n<>r.cantidad OR h<>r.huella THEN
   RAISE EXCEPTION 'Se modificaron datos ajenos al retiro en %; transaccion cancelada',r.tabla;
  END IF;
 END LOOP;
 IF EXISTS(SELECT 1 FROM idosgd.tdtv_remitos WHERE co_tip_doc_adm='990')
    OR EXISTS(SELECT 1 FROM idosgd.si_mae_tipo_doc WHERE cdoc_tipdoc='990') THEN
  RAISE EXCEPTION 'Retiro incompleto del tipo 990';
 END IF;
END $comprobar$;
COMMIT;
