-- PENDIENTE DE AUTORIZACION. Codex no ha ejecutado este archivo.
-- Todos los usuarios y oficinas de idosgd_inia.
-- Conserva cada directorio. Normaliza de_dir_emi, de_dir_rec y de_dir_ane
-- solo en rutas Windows absolutas (unidad o UNC). Conserva rutas Linux y URL.
-- Crea un respaldo de los registros afectados en una tabla nueva.
-- Si existe el registro de cargas piloto, sincroniza sus rutas tambien.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '60s';
SET LOCAL standard_conforming_strings = on;

DO $control$
BEGIN
  IF current_database() <> 'idosgd_inia' THEN
    RAISE EXCEPTION 'Base incorrecta: se requiere idosgd_inia';
  END IF;
END;
$control$;

CREATE OR REPLACE FUNCTION pg_temp.normalizar_ruta_sgd(ruta text)
RETURNS text LANGUAGE sql IMMUTABLE STRICT AS $funcion$
  SELECT CASE
    WHEN (ruta ~ '^[A-Za-z]:' AND substring(ruta FROM 3 FOR 1) IN ('/', chr(92)))
      OR left(ruta, 2) IN ('//', repeat(chr(92), 2))
    THEN replace(ruta, '/', chr(92))
    ELSE ruta
  END;
$funcion$;

CREATE OR REPLACE FUNCTION pg_temp.normalizar_datos_rutas_sgd(datos jsonb)
RETURNS jsonb LANGUAGE sql IMMUTABLE STRICT AS $funcion$
  SELECT coalesce(jsonb_object_agg(key, CASE
    WHEN key IN ('de_dir_emi', 'de_dir_rec', 'de_dir_ane')
      AND jsonb_typeof(value) = 'string'
    THEN to_jsonb(pg_temp.normalizar_ruta_sgd(value #>> '{}'))
    ELSE value END), '{}'::jsonb)
  FROM jsonb_each(datos);
$funcion$;

CREATE TABLE IF NOT EXISTS idosgd.respaldo_rutas_documentos_20261010 (
  tabla text NOT NULL,
  clave jsonb NOT NULL,
  datos_antes jsonb NOT NULL,
  datos_despues jsonb NOT NULL,
  fecha_respaldo timestamptz NOT NULL DEFAULT current_timestamp,
  PRIMARY KEY (tabla, clave)
);

LOCK TABLE idosgd.tdtx_config_emp IN SHARE ROW EXCLUSIVE MODE;

-- Muestra exactamente las rutas que se van a cambiar.
SELECT co_emp, co_dep, rutas AS rutas_antes,
       pg_temp.normalizar_datos_rutas_sgd(rutas) AS rutas_despues
FROM (
  SELECT co_emp, co_dep,
    jsonb_build_object('de_dir_emi', de_dir_emi, 'de_dir_rec', de_dir_rec,
                       'de_dir_ane', de_dir_ane) AS rutas
  FROM idosgd.tdtx_config_emp
) actual
WHERE rutas IS DISTINCT FROM pg_temp.normalizar_datos_rutas_sgd(rutas);

INSERT INTO idosgd.respaldo_rutas_documentos_20261010
  (tabla, clave, datos_antes, datos_despues)
SELECT 'tdtx_config_emp', jsonb_build_object('co_emp', co_emp, 'co_dep', co_dep),
       rutas, pg_temp.normalizar_datos_rutas_sgd(rutas)
FROM (
  SELECT co_emp, co_dep,
    jsonb_build_object('de_dir_emi', de_dir_emi, 'de_dir_rec', de_dir_rec,
                       'de_dir_ane', de_dir_ane) AS rutas
  FROM idosgd.tdtx_config_emp
) actual
WHERE rutas IS DISTINCT FROM pg_temp.normalizar_datos_rutas_sgd(rutas)
ON CONFLICT (tabla, clave) DO NOTHING;

UPDATE idosgd.tdtx_config_emp
SET de_dir_emi = pg_temp.normalizar_ruta_sgd(de_dir_emi),
    de_dir_rec = pg_temp.normalizar_ruta_sgd(de_dir_rec),
    de_dir_ane = pg_temp.normalizar_ruta_sgd(de_dir_ane)
WHERE de_dir_emi IS DISTINCT FROM pg_temp.normalizar_ruta_sgd(de_dir_emi)
   OR de_dir_rec IS DISTINCT FROM pg_temp.normalizar_ruta_sgd(de_dir_rec)
   OR de_dir_ane IS DISTINCT FROM pg_temp.normalizar_ruta_sgd(de_dir_ane)
RETURNING co_emp, co_dep, de_dir_emi, de_dir_rec, de_dir_ane;

-- Mantiene consistente el historial del cargador para futuras cargas/retiros.
DO $piloto$
BEGIN
  IF to_regclass('idosgd.piloto_objetos') IS NOT NULL THEN
    LOCK TABLE idosgd.piloto_objetos IN SHARE ROW EXCLUSIVE MODE;
    INSERT INTO idosgd.respaldo_rutas_documentos_20261010
      (tabla, clave, datos_antes, datos_despues)
    SELECT 'piloto_objetos', clave, datos,
           pg_temp.normalizar_datos_rutas_sgd(datos)
    FROM idosgd.piloto_objetos
    WHERE tabla = 'tdtx_config_emp'
      AND datos IS DISTINCT FROM pg_temp.normalizar_datos_rutas_sgd(datos)
    ON CONFLICT (tabla, clave) DO NOTHING;

    UPDATE idosgd.piloto_objetos
    SET datos = pg_temp.normalizar_datos_rutas_sgd(datos)
    WHERE tabla = 'tdtx_config_emp'
      AND datos IS DISTINCT FROM pg_temp.normalizar_datos_rutas_sgd(datos);
  END IF;
END;
$piloto$;

-- La verificacion exige que no queden rutas Windows sin normalizar.
DO $verificacion$
BEGIN
  IF EXISTS (
    SELECT 1 FROM idosgd.tdtx_config_emp
    WHERE de_dir_emi IS DISTINCT FROM pg_temp.normalizar_ruta_sgd(de_dir_emi)
       OR de_dir_rec IS DISTINCT FROM pg_temp.normalizar_ruta_sgd(de_dir_rec)
       OR de_dir_ane IS DISTINCT FROM pg_temp.normalizar_ruta_sgd(de_dir_ane)
  ) THEN
    RAISE EXCEPTION 'Quedaron rutas Windows sin normalizar';
  END IF;
END;
$verificacion$;

SELECT tabla, count(*) AS registros_respaldados
FROM idosgd.respaldo_rutas_documentos_20261010
GROUP BY tabla ORDER BY tabla;

COMMIT;
