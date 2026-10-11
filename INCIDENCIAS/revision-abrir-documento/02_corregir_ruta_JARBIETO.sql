-- PENDIENTE DE AUTORIZACION. No ejecutado por Codex.
-- Alcance: empleado 00237, oficina 00217, usuario JARBIETO.
-- Corrige las tres rutas solo si contienen exactamente la ruta incorrecta.
-- Termina en COMMIT: al ejecutar el archivo completo guarda la correccion.
-- Codex requiere aprobacion explicita antes de ejecutar este script.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '30s';
SET LOCAL standard_conforming_strings = on;

DO $control$
BEGIN
  IF current_database() <> 'idosgd_inia' THEN
    RAISE EXCEPTION 'Base incorrecta: se requiere idosgd_inia';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM idosgd.seg_usuarios1
                 WHERE cod_user = 'JARBIETO' AND cemp_codemp = '00237') THEN
    RAISE EXCEPTION 'No coincide la identidad JARBIETO / 00237';
  END IF;
  IF (SELECT count(*) FROM idosgd.tdtx_config_emp
      WHERE co_emp = '00237' AND co_dep = '00217') <> 1 THEN
    RAISE EXCEPTION 'Se esperaba exactamente una configuracion de 00237 / 00217';
  END IF;
END;
$control$;

-- El resultado ANTES permite conservar los valores originales.
SELECT 'ANTES' AS etapa, co_emp, co_dep, de_dir_emi, de_dir_rec, de_dir_ane
FROM idosgd.tdtx_config_emp
WHERE co_emp = '00237' AND co_dep = '00217';

UPDATE idosgd.tdtx_config_emp
SET de_dir_emi = CASE
      WHEN de_dir_emi = 'C:/SGD/SGD-INIA-INSTALL_PG/datos/documentos'
      THEN 'C:\SGD\SGD-INIA-INSTALL_PG\datos\documentos' ELSE de_dir_emi END,
    de_dir_rec = CASE
      WHEN de_dir_rec = 'C:/SGD/SGD-INIA-INSTALL_PG/datos/documentos'
      THEN 'C:\SGD\SGD-INIA-INSTALL_PG\datos\documentos' ELSE de_dir_rec END,
    de_dir_ane = CASE
      WHEN de_dir_ane = 'C:/SGD/SGD-INIA-INSTALL_PG/datos/documentos'
      THEN 'C:\SGD\SGD-INIA-INSTALL_PG\datos\documentos' ELSE de_dir_ane END
WHERE co_emp = '00237' AND co_dep = '00217'
  AND 'C:/SGD/SGD-INIA-INSTALL_PG/datos/documentos'
      IN (de_dir_emi, de_dir_rec, de_dir_ane)
RETURNING 'DESPUES (aun sin guardar)' AS etapa,
          co_emp, co_dep, de_dir_emi, de_dir_rec, de_dir_ane;

COMMIT;
