-- PENDIENTE DE AUTORIZACION. No ejecutado por Codex.
-- Alcance: solo JARBIETO, aplicativo 9.
-- M01: menu padre. M0113: Configuracion personal.
-- No asigna administrador ni otras opciones de configuracion.
-- Termina en COMMIT: al ejecutar el archivo completo guarda los cambios.
-- Codex requiere aprobacion explicita antes de ejecutar este script.
BEGIN;
SET LOCAL lock_timeout = '5s';
SET LOCAL statement_timeout = '30s';

DO $control$
BEGIN
  IF current_database() <> 'idosgd_inia' THEN
    RAISE EXCEPTION 'Base incorrecta: se requiere idosgd_inia';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM idosgd.seg_usuarios1
                 WHERE cod_user = 'JARBIETO' AND cemp_codemp = '00237') THEN
    RAISE EXCEPTION 'No coincide la identidad JARBIETO / 00237';
  END IF;
  IF NOT EXISTS (SELECT 1 FROM idosgd.seg_user_aplica
                 WHERE cod_user = 'JARBIETO' AND cod_aplica = '9'
                   AND es_activo = '1') THEN
    RAISE EXCEPTION 'JARBIETO no tiene el aplicativo 9 activo';
  END IF;
  IF (SELECT count(*) FROM idosgd.seg_opciones
      WHERE cod_aplica = '9' AND cod_opc IN ('M01', 'M0113')) <> 2 THEN
    RAISE EXCEPTION 'Faltan M01 / M0113 en el catalogo de opciones';
  END IF;
END;
$control$;

-- El resultado ANTES permite conservar el estado original.
SELECT 'ANTES' AS etapa, cod_user, cod_aplica, cod_opc, es_habilitado
FROM idosgd.seg_user_aplica_opc
WHERE cod_user = 'JARBIETO' AND cod_aplica = '9'
  AND cod_opc IN ('M01', 'M0113')
ORDER BY cod_opc;

INSERT INTO idosgd.seg_user_aplica_opc
  (cod_user, cod_aplica, cod_opc, es_habilitado)
VALUES ('JARBIETO', '9', 'M01', '1'),
       ('JARBIETO', '9', 'M0113', '1')
ON CONFLICT (cod_user, cod_aplica, cod_opc)
DO UPDATE SET es_habilitado = EXCLUDED.es_habilitado
RETURNING 'DESPUES (aun sin guardar)' AS etapa,
          cod_user, cod_aplica, cod_opc, es_habilitado;

COMMIT;
