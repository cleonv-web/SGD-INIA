-- ALTERNATIVA MANUAL DE RECUPERACION; no es un paso obligatorio de instalación o carga.
-- La instalación nueva y Cargar-Area ya resuelven la sede por sus flujos normales.
-- Si se utiliza esta alternativa, ejecutar personalmente el archivo COMPLETO en PostgreSQL del SGD.
-- Windows y Linux: el mismo SQL. No ejecutar en SQL Server.
-- Mantiene el código 001 y sus relaciones. Sólo actualiza nombre y dirección.
-- Datos aprobados en datos/carga-uti-preparada/area.json para la sede central.
BEGIN;
SET LOCAL lock_timeout = '15s';

DO $sede$
DECLARE
    actual record;
    filas integer;
BEGIN
    SELECT de_nombre_local, de_direccion_local, es_local
    INTO actual
    FROM idosgd.si_mae_local
    WHERE ccod_local = '001'
    FOR UPDATE;

    IF NOT FOUND THEN
        RAISE EXCEPTION 'No existe la sede 001. No se creó ni modificó ninguna sede.';
    END IF;

    IF actual.de_nombre_local IS NOT DISTINCT FROM 'INSTITUTO NACIONAL DE INNOVACIÓN AGRARIA'
       AND actual.de_direccion_local IS NOT DISTINCT FROM 'Av. La Molina Nº 1981, La Molina, Lima'
       AND actual.es_local IS NOT DISTINCT FROM '1' THEN
        RAISE NOTICE 'La sede 001 ya tiene los datos correctos. No se repite la actualización.';
    ELSIF actual.de_nombre_local IS NOT DISTINCT FROM 'INSTALACION LOCAL INIA'
       AND actual.de_direccion_local IS NOT DISTINCT FROM 'PENDIENTE'
       AND actual.es_local IS NOT DISTINCT FROM '1' THEN
        UPDATE idosgd.si_mae_local
        SET de_nombre_local = 'INSTITUTO NACIONAL DE INNOVACIÓN AGRARIA',
            de_direccion_local = 'Av. La Molina Nº 1981, La Molina, Lima'
        WHERE ccod_local = '001';

        GET DIAGNOSTICS filas = ROW_COUNT;
        IF filas <> 1 THEN
            RAISE EXCEPTION 'Se esperaba actualizar una sola sede. La operación se revierte.';
        END IF;
        RAISE NOTICE 'Sede 001 actualizada. Código, estado y relaciones conservados.';
    ELSE
        RAISE EXCEPTION 'La sede 001 tiene otros datos o está inactiva. No se sobrescribe; revisar su catálogo.';
    END IF;
END
$sede$;

SELECT ccod_local AS codigo, de_nombre_local AS nombre,
       de_direccion_local AS direccion, es_local AS estado
FROM idosgd.si_mae_local
WHERE ccod_local = '001';
COMMIT;
