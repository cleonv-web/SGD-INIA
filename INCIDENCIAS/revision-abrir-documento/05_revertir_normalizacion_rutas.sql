-- PENDIENTE DE AUTORIZACION. Codex no ha ejecutado este archivo.
-- Revierte el script 04 usando el respaldo que este crea.
-- Solo restaura filas que aun conservan EXACTAMENTE el resultado respaldado.
-- Si una ruta fue editada despues, se conserva y se muestra al final.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '60s';

DO $control$
BEGIN
  IF current_database() <> 'idosgd_inia' THEN
    RAISE EXCEPTION 'Base incorrecta: se requiere idosgd_inia';
  END IF;
  IF to_regclass('idosgd.respaldo_rutas_documentos_20261010') IS NULL THEN
    RAISE EXCEPTION 'No existe el respaldo del script 04';
  END IF;
END;
$control$;

LOCK TABLE idosgd.tdtx_config_emp IN SHARE ROW EXCLUSIVE MODE;

UPDATE idosgd.tdtx_config_emp c
SET de_dir_emi = b.datos_antes ->> 'de_dir_emi',
    de_dir_rec = b.datos_antes ->> 'de_dir_rec',
    de_dir_ane = b.datos_antes ->> 'de_dir_ane'
FROM idosgd.respaldo_rutas_documentos_20261010 b
WHERE b.tabla = 'tdtx_config_emp'
  AND c.co_emp = b.clave ->> 'co_emp' AND c.co_dep = b.clave ->> 'co_dep'
  AND jsonb_build_object('de_dir_emi', c.de_dir_emi, 'de_dir_rec', c.de_dir_rec,
                         'de_dir_ane', c.de_dir_ane) = b.datos_despues
RETURNING c.co_emp, c.co_dep, c.de_dir_emi, c.de_dir_rec, c.de_dir_ane;

DO $piloto$
BEGIN
  IF to_regclass('idosgd.piloto_objetos') IS NOT NULL THEN
    LOCK TABLE idosgd.piloto_objetos IN SHARE ROW EXCLUSIVE MODE;
    UPDATE idosgd.piloto_objetos o
    SET datos = b.datos_antes
    FROM idosgd.respaldo_rutas_documentos_20261010 b
    WHERE b.tabla = 'piloto_objetos' AND o.tabla = 'tdtx_config_emp'
      AND o.clave = b.clave AND o.datos = b.datos_despues
      AND EXISTS (
        SELECT 1 FROM idosgd.tdtx_config_emp c
        WHERE c.co_emp = o.clave ->> 'co_emp' AND c.co_dep = o.clave ->> 'co_dep'
          AND c.de_dir_emi IS NOT DISTINCT FROM b.datos_antes ->> 'de_dir_emi'
          AND c.de_dir_rec IS NOT DISTINCT FROM b.datos_antes ->> 'de_dir_rec'
          AND c.de_dir_ane IS NOT DISTINCT FROM b.datos_antes ->> 'de_dir_ane'
      );
  END IF;
END;
$piloto$;

-- Filas que no coinciden con su estado original: revisar sin sobrescribirlas.
SELECT c.co_emp, c.co_dep, c.de_dir_emi, c.de_dir_rec, c.de_dir_ane,
       b.datos_antes AS rutas_originales
FROM idosgd.tdtx_config_emp c
JOIN idosgd.respaldo_rutas_documentos_20261010 b
  ON b.tabla = 'tdtx_config_emp' AND c.co_emp = b.clave ->> 'co_emp'
 AND c.co_dep = b.clave ->> 'co_dep'
WHERE jsonb_build_object('de_dir_emi', c.de_dir_emi, 'de_dir_rec', c.de_dir_rec,
                         'de_dir_ane', c.de_dir_ane) IS DISTINCT FROM b.datos_antes;

-- El respaldo se conserva.
COMMIT;
